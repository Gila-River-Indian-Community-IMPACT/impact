package us.wy.state.deq.impact.webcommon.tools;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
 
 
public class ApplicationSessionExpiryFilter implements Filter {
    private FilterConfig _filterConfig = null;
    
    public void init(FilterConfig filterConfig) throws ServletException {
        _filterConfig = filterConfig;
    }
 
    public void destroy() {
        _filterConfig = null;
    }
 
    public void doFilter(ServletRequest request, ServletResponse response, 
                     FilterChain chain) throws IOException, ServletException {
    	
    	HttpServletRequest httpRequest = (HttpServletRequest)request;
    	HttpServletResponse httpResponse = (HttpServletResponse)response;

    	HttpSession session = httpRequest.getSession();
	    String requestedSessionId = httpRequest.getRequestedSessionId();
        String currentWebSessionId = session.getId();
        String requestUri = httpRequest.getRequestURI();
        
        // check for valid session
        boolean sessionOk = null != requestedSessionId && 
        		currentWebSessionId.equalsIgnoreCase(requestedSessionId);

        if (sessionOk) {
        	// check to see if the user has visited 'home.jsf' yet, because
        	// the session won't be fully initialized until they do.
        	sessionOk = requestUri.endsWith("/home.jsf") || 
        			null != session.getAttribute("HOME_VISITED");
        }
        
        if (!sessionOk){
        	// the session has expired or renewed. Redirect request
        	String redirect = 
        			_filterConfig.getInitParameter("sessionTimeoutRedirect");
        	httpResponse.sendRedirect(httpRequest.getContextPath() + "/" + 
        			(null == redirect? "" : redirect));
        }
        else{
        	// the session is valid
            PrintWriter out = response.getWriter();
            CharResponseWrapper responseWrapper = new CharResponseWrapper(
                    (HttpServletResponse) response);

            chain.doFilter(request, responseWrapper);

            String s = responseWrapper.toString();
            String contentType = responseWrapper.getContentType();

            if (null != contentType && contentType.startsWith("text/html") &&
                    StringUtils.isNotBlank(s)) {
            	CharArrayWriter caw = new CharArrayWriter();

            	// insert meta refresh element to redirect:
            	// --internal: home page
            	// --portal: idp signout page
            	int indexOfMeta = s.indexOf("<meta");
            	if (indexOfMeta != -1) {
	            	int maxInactiveInterval = session.getMaxInactiveInterval();
	            	String contextPath = httpRequest.getContextPath();
	                caw.write(s.substring(0,indexOfMeta));
	                caw.write("<meta http-equiv=\"refresh\" content=\"" + (maxInactiveInterval-45) + ";url=" + contextPath + "/?wa=wsignout1.0\">");
	                caw.write(s.substring(indexOfMeta, s.length()));
	                s = caw.toString();
            	}

                // insert js that closes dialog windows that
            	// get redirected to the home page
            	int indexOfBodyClose = s.indexOf("</body>");
            	if (indexOfBodyClose != -1) {
	                caw = new CharArrayWriter();
	                caw.write(s.substring(0, indexOfBodyClose));
	                caw.write("<script type=\"text/javascript\" src=\"" + 
	                		httpRequest.getContextPath() + 
	                		"/scripts/jquery-2.2.3.min-no-script-tag.js\"></script>");
	                caw.write("<script type=\"text/javascript\" src=\"" + 
	                		httpRequest.getContextPath() + 
	                		"/scripts/dialog-redirected-check.js\"></script>");
	                caw.write(s.substring(indexOfBodyClose, s.length()));
	                s = caw.toString();
            	}
            	
                response.setContentLength(s.length());
                out.write(s);
            }
            else {
                out.write(s);
            }

            if (requestUri.endsWith("/home.jsf")) {
            	try {
            		session.setAttribute("HOME_VISITED", true);
            	} catch (IllegalStateException e) {}
            }
            
            out.close();
        }
    }
    
    private static class CharResponseWrapper extends HttpServletResponseWrapper {
        private CharArrayWriter output;

        public String toString() {
            return output.toString();
        }

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
            output = new CharArrayWriter();
        }

        public PrintWriter getWriter() {
            return new PrintWriter(output);
        }
    }
    
} 
