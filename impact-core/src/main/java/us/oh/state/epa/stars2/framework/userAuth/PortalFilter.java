package us.oh.state.epa.stars2.framework.userAuth;

import java.io.IOException;
import java.sql.Timestamp;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.portal.home.MyTasks;

/**
 * This filter checks to see if the user is logged in.
 * 
 * @author $Author: dleinbaugh $
 * @version $Revision: 1.30 $
 */
public class PortalFilter implements Filter {
    private static Logger logger = Logger.getLogger(PortalFilter.class);
    private String epaPortal;
    
    public PortalFilter() {
        super();
    }
    
    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public final void init(FilterConfig filterConfig) throws ServletException {
        epaPortal = Config.findNode("app.defaultEPAPortal").getText();
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
        throws IOException, ServletException {
        String timeoutFlag = "";
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (request == null) {
            logger.error("PortalFilter.doFilter called with NULL request");
            return;
        }
        boolean logout = AbstractDAO.translateIndicatorToBoolean(request.getParameter("LOGOUT"));

        HttpSession tempSession = request.getSession(false);
        logger.debug("Logout value " + logout);
        
        if (tempSession != null) {
            logger.debug("*** session id = " + tempSession.getId());
            Timestamp createTime = new Timestamp(tempSession.getCreationTime());
            Timestamp lastAccessed = new Timestamp(tempSession.getLastAccessedTime());
            logger.debug("*** session create time = " + createTime.toString());
            logger.debug("*** session last accessed = " + lastAccessed.toString());
            if (logout) {
                logger.debug("Logging out user " + request.getParameter("LOGIN_NAME"));
                String referalStr = request.getHeader("referer");
                String redirect = epaPortal;
                if (redirect == null) {
                	redirect = referalStr;
                }
                logger.debug("Invalidating current session");
                tempSession.invalidate();
                logger.debug("redirecting to '" + redirect + "'");
                response.sendRedirect(redirect);
            }
        } 
        // test change to timeout
        else if (request.getParameter("LOGOUT") == null) {
//            String referalStr = request.getHeader("referer");
//            logger.debug("--- request parameters:");
//            Enumeration<?> paramEnum = request.getParameterNames();
//            while (paramEnum.hasMoreElements()) {
//                String parmName = (String) paramEnum.nextElement();
//                logger.debug("*** Parameter " + parmName + " has a value of '" + 
//                        request.getParameter(parmName) + "'");
//            }
//            logger.debug("--- end of parameters");
//            logger.debug("--- request attributes:");
//            Enumeration<?> attrEnum = request.getAttributeNames();
//            while (attrEnum.hasMoreElements()) {
//                String attrName = (String) attrEnum.nextElement();
//                logger.debug("*** Attribute " + attrName + " has a value of '" + 
//                        request.getAttribute(attrName) + "'");
//            }
//            logger.debug("--- end of attributes");
            logger.debug(" Session is null, redirecting to: timeout.html");
            timeoutFlag = "Timing Out";
            response.sendRedirect("timeout.html");
        }
        try {
            chain.doFilter(request, response);
            timeoutFlag = "";
        }catch(Exception e) { 
            // This is an attempt to catch and log exceptions that more focused code does not
            // have catches for.
            FacesContext facesContext = getFacesContext(request, response);
            if(facesContext != null) {
                MyTasks mt = (MyTasks) facesContext.getApplication().getVariableResolver()
                    .resolveVariable(facesContext, "myTasks");
                String info = mt.getInfo();
                logger.debug(" " + timeoutFlag + " Captured MyTasks Information(" + request.getRequestURI() + "): " + info, e);
            } else {
                logger.debug(" " + timeoutFlag + " facesContext is null (" + request.getRequestURI() + ")");
            }
            timeoutFlag = "";
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else if(e instanceof ServletException) {
                throw (ServletException)e;
            }else if(e instanceof IOException) {
                throw (IOException)e;
            }
        }
    }
    
    private FacesContext getFacesContext(ServletRequest request,
            ServletResponse response) {
        // Try to get it first
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder
                    .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
                    .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            Lifecycle lifecycle = lifecycleFactory
                    .getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

            // Either set a private member servletContext =
            // filterConfig.getServletContext();
            // in you filter init() method or set it here like this:
            ServletContext servletContext = ((HttpServletRequest) request)
                    .getSession().getServletContext();
            // Note that the above line would fail if you are using any other
            // protocol than http

            // Doesn't set this instance as the current instance of
            // FacesContext.getCurrentInstance
            facesContext = contextFactory.getFacesContext(servletContext,
                    request, response, lifecycle);
        }

        return facesContext;
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public final void destroy() {
        logger = null;
    }
}
