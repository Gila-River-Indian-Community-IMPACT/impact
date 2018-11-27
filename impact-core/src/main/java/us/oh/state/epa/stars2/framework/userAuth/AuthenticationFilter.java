package us.oh.state.epa.stars2.framework.userAuth;

import java.io.IOException;
import java.util.HashMap;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.aport.web.PortalClient;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;

/**
 * This filter checks to see if the user is logged in.
 * 
 * @author $Author: dleinbaugh $
 * @version $Revision: 1.41 $
 */
public class AuthenticationFilter implements Filter {
//    private Integer currentInstanceKey;
    private UserAuth userAuth;
    private static Logger logger = Logger.getLogger(AuthenticationFilter.class);
    private HashMap<String, String>allUseCases;
    private HashMap<String, User>userCache;
    private String epaPortal;
    
    public AuthenticationFilter() {
        super();
        String epaPortalJndiName = 
        		Config.findNode("app.defaultEPAPortal").getAsString("jndiName");
        try {
        	Context initContext = new InitialContext();
        	Context envContext  = (Context)initContext.lookup("java:/comp/env");
        	epaPortal = (String)envContext.lookup(epaPortalJndiName);
        	logger.debug("epaPortal = " + epaPortal);
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
        userCache = new HashMap<String, User>();
    }
    
    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public final void init(FilterConfig filterConfig) throws ServletException {
        String appTypeCd = CompMgr.getAppName();

        if (appTypeCd != null) {
            try {
                userAuth = (UserAuth) CompMgr.newInstance(UserAuth.DEFAULT);

            } catch (UnableToStartException utse) {
                logger.error(utse.getMessage(), utse);
            }
        }
    }

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) 
        throws java.io.IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        logger.debug("requestURI = " + request.getRequestURI());
        if ( !request.getRequestURI().startsWith( request.getContextPath() + "/login.jsf" ) ) {                              
            String itsPortalSid = request.getParameter("sid");
            User portalUser = null;
            UserAttributes userAttrs = null;
            HttpSession tempSession = request.getSession(false);

            // If a session is present, this means we are either already in the
            // app or we are came from ITS's portal and are in the same
            // container as the portal. If there isn't a session, look for the 
            // sessionId in the URL and try and get the user from the ITS service.
            if (itsPortalSid != null) {
                
                if (itsPortalSid.indexOf('!') > 0) {
                    itsPortalSid = itsPortalSid.substring(0, itsPortalSid.indexOf('!'));
                }
                
                if (userCache.containsKey(itsPortalSid)) {
                    portalUser = userCache.get(itsPortalSid);
                } else {
                	logger.error("logic not implemented");
//                    try {
                        // If we get a request from the ITS portal to login,
                        // check
                        // the session to see if this user is already logged in.
                        // This
                        // could happen if the user killed the browser/tab they
                        // were in or
                        // are trying to login a second time. If this is the
                        // case we
                        // to invalidate their previous session and start a new
                        // one.
//                        if (tempSession != null) {
//                            userAttrs = (UserAttributes) tempSession.getAttribute("userAttrs");
//
//                            if ((userAttrs != null) && userAttrs.isLoggedIn()) {
//                                logger.debug(" User already logged in. Invalidating current session.");
//                                tempSession.invalidate();
//
//                                request.getSession(true);
//                            }
//                        }

                        // This loop is a kludge, because the ITS portal isn't
                        // replicating sessions in development, should be fine
                        // in
                        // production.
                        // Looping 30 times at request of ITS to handle new server config
//                        int loopCnt = 0;
//                        while ((portalUser == null) && (loopCnt++ < 30)) {
//                            PortalService portalService;
//                            portalService = PortalClient.getPortalService();
//                            portalService = ServiceFactory.getInstance().getPortalServiceService();

//                            if (portalService != null) {
//                                logger.debug("PortalService acquired");
//                                portalUser = portalService.retrieveUserForSession(itsPortalSid);
//                            }
//                        }

//                        String msg = "(Case 1) ITS User class is ";
//                        if (portalUser != null) {
//                           userCache.put(itsPortalSid, portalUser);
//                           msg += portalUser.getClass().getName() + ".";
//                        } else {
//                            msg += "null.";
//                        }
//                        logger.debug(msg);
//                    } catch (ServiceFactoryException sfe) {
//                        logger.error(sfe.getMessage(), sfe);
//                        throw new RuntimeException(sfe);
//                    } catch (EPAException ee) {
//                        logger.error(ee.getMessage(), ee);
//                        throw new RuntimeException(ee);
//                    }
                }

                if (portalUser != null) {
                    userAttrs = userAuth.retrieveUserAttributes(portalUser
                            .getOID().longValue());

                    if (userAttrs != null) {
                        userAttrs.setItsUser(portalUser);
                        setUserAttrs(userAttrs, request, response);
                    } else {
                        throw new RuntimeException(
                                "Unable to retrieve ITS Portal User Stars2 attributes");
                    }
                } else {
                    throw new RuntimeException(
                            "Unable to retrieve ITS Portal User");
                }
            } else if (tempSession != null) {
                portalUser = PortalClient.getUser(tempSession);

                String msg = "(Case 2) ITS User class is ";
                if (portalUser != null) {
                    msg += portalUser.getClass().getName() + ".";
                }
                logger.debug(msg);

                userAttrs = (UserAttributes) tempSession
                        .getAttribute("userAttrs");
                
                if (portalUser != null && userAttrs == null) {
                    logger.debug("UserAttrs is NULL!");
                    userAttrs = userAuth.retrieveUserAttributes(portalUser
                            .getOID().longValue());

                    if (userAttrs != null) {
                        userAttrs.setItsUser(portalUser);
                        setUserAttrs(userAttrs, request, response);
                    } else {
                        throw new RuntimeException(
                                "Unable to retrieve ITS Portal User Stars2 attributes");
                    }
                }
            }

            if (userAttrs == null || userAttrs.getUserName() == null 
                || userAttrs.getUserId() == null) {

                logger.debug(" redirecting to '" + epaPortal + "'");
                response.sendRedirect(epaPortal);
                return;
            } 
        } else {
            // Check the session to see if this user is already logged in. This
            // could happen if the user killed the browser/tab they were in or
            // are trying to login a second time. If this is the case we 
            // to invalidate their previous session and start a new one.

            HttpSession tempSession = request.getSession(false);

            if (tempSession != null) {
                UserAttributes userAttrs = (UserAttributes) tempSession
                .getAttribute("userAttrs");

                if ((userAttrs != null) && userAttrs.isLoggedIn()) {
                    logger.debug(" User already logged in. Invalidating current session.");
                    tempSession.invalidate();
                
                    request.getSession(true);
                }
            }
        }
        try {
            chain.doFilter(request, response);
        }catch(Exception e) { 
            // This is an attempt to catch and log exceptions that more focused code does no
            // have catches for.
            FacesContext facesContext = getFacesContext(request, response);
            if(facesContext != null) {
                FacilityProfile fp = (FacilityProfile) facesContext.getApplication().getVariableResolver()
                    .resolveVariable(facesContext, "facilityProfile");
                String info = fp.getInfo();
                logger.error("Captured Facility Information(" + request.getRequestURI() + "): " + info, e);
            } else {
                logger.error("facesContext is null (" + request.getRequestURI() + ")");
            }
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else if(e instanceof ServletException) {
                throw (ServletException)e;
            }else if(e instanceof IOException) {
                throw (IOException)e;
            }
        }
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public final void destroy() {
        userAuth = null;
        logger = null;

        if (allUseCases != null) {
            for (String tempStr : allUseCases.keySet()) {
                allUseCases.remove(tempStr);
            }
        }

        allUseCases = null;
    }

    private abstract static class InnerFacesContext extends FacesContext {
        protected static void setFacesContextAsCurrentInstance(
                FacesContext facesContext) {
            FacesContext.setCurrentInstance(facesContext);
        }
    }

    @SuppressWarnings("unchecked")
    private UserAttributes setUserAttrs(UserAttributes userAttrs,
            HttpServletRequest request, HttpServletResponse response) {
        if (userAttrs != null) {
            // We need to do this to set the useCases.
            userAttrs.login();

            userAttrs.setStars2Login(false);

            String referalStr = request.getHeader("referer");

            if (referalStr != null) {
                userAttrs.setEpaPortal(referalStr);
            }

            FacesContext facesContext = getFacesContext(request, response);

            ExternalContext externalContext = facesContext.getExternalContext();

            externalContext.getSession(true);

            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put("userAttrs", userAttrs);
        }

        return userAttrs;
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

            // Set using our inner class
            InnerFacesContext.setFacesContextAsCurrentInstance(facesContext);

            // set a new viewRoot, otherwise context.getViewRoot returns null
            UIViewRoot view = facesContext.getApplication().getViewHandler()
                    .createView(facesContext, "/home/home.jsp");
            facesContext.setViewRoot(view);
        }

        return facesContext;
    }       
}
