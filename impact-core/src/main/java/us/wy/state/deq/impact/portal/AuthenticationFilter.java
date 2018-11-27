package us.wy.state.deq.impact.portal;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gricdeq.impact.UserPrincipal;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;

/**
 * Servlet Filter implementation class AuthenticationFilter
 */
//@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {

    private Logger logger = Logger.getLogger(AuthenticationFilter.class);
    private boolean testMode = "true".equals(Config.getEnvEntry("app/testMode"));
    
    /**
     * Default constructor. 
     */
    public AuthenticationFilter() {
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		boolean redirect = false;
		String loginUrl = req.getContextPath() + "/login.jsf";
		String companySelectorUrl = req.getContextPath() + "/companySelector.jsf";
		
		if (!testMode) {
		    HttpSession session = req.getSession(false);
		    MyTasks myTasks = (session != null) ? (MyTasks) session.getAttribute("myTasks") : null;
		    Principal principal = req.getUserPrincipal();
		    Object externalCompanyId = null;
		    boolean authorized = false;
		    
		    if (null != myTasks) {
		    	externalCompanyId = myTasks.getExternalCompanyId();
		    }
		    if (null != principal) {
		    	if (principal instanceof UserPrincipal) {
		    		UserPrincipal userPrincipal = (UserPrincipal)principal;
		    		if (null != userPrincipal.getContact().getContactRoles()) {
		    			List<ContactRole> contactRoles = userPrincipal.getContact().getContactRoles();
		    			for (ContactRole contactRole : contactRoles) {
		    				if (contactRole.isActive()) {
		    					ExternalOrganization externalOrg = contactRole.getExternalRole().getOrganization();
		    					if (null != externalCompanyId && 
		    							externalCompanyId.equals(externalOrg.getOrganizationId())) {
		    						//TODO more checks here possibly
		    						authorized = true;
		    						break;
		    					}
		    				}
		    			}
		    		}
		    	}
		    }

		    if (!authorized && 
		    		!req.getRequestURI().equals(loginUrl) && 
		    		!req.getRequestURI().equals(companySelectorUrl) && 
		    		null != myTasks) {
		    	redirect = true;
		    }
		}
		
		if (redirect) {
			res.sendRedirect(loginUrl);			
		} else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
