package us.oh.state.epa.stars2.framework.userAuth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oracle.adf.view.faces.context.AdfFacesContext;

import org.apache.log4j.Logger;
import org.apache.myfaces.config.RuntimeConfig;
import org.apache.myfaces.config.element.NavigationCase;
import org.apache.myfaces.config.element.NavigationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDefBase;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.InvalidUserException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuTree;

import com.Ostermiller.util.StringTokenizer;

@Scope("session") //TODO verify this is the only scope config necessary
@Component("userAttrs")
public class UserAttributes implements Serializable {
	
	private static final long serialVersionUID = 1767106005648052088L;

	private Integer userId;
    private boolean stars2Login = true;
    private boolean loggedIn = false;
    private String userName;
    private String userFullName;
    private String epaPortal;
    @Autowired private UserAuth userAuth;
    private LinkedHashMap<String, UseCase> useCases = new LinkedHashMap<String, UseCase>();
    private List<String> viewIds;
    private User itsUser;
    private transient Logger logger;
    private HashMap<String, String> viewIdsToUseCase = new HashMap<String,String>();
    public String migrated; //TODO the reason this is public?
    public static boolean bolMigrated=false; //TODO the reason this is public?
    

    public UserAttributes() {
    	super();
    }

    @PostConstruct
	private void init() {
		logger = Logger.getLogger(this.getClass());
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
        
        String isMigrated = 
        		Config.findNode("app.dataMigrated").getAsString("jndiName");
        try {
        	Context initContext = new InitialContext();
        	Context envContext  = (Context)initContext.lookup("java:/comp/env");
        	migrated =(String)envContext.lookup(isMigrated);
        	bolMigrated = Boolean.parseBoolean(migrated);
        	logger.debug("bolMigrated = " + bolMigrated);
        	
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
        
        if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
        	login();
        }
	}

	public UserAttributes(UserDefBase userDef) {
        epaPortal = Config.findNode("app.defaultEPAPortal").getText();
        logger = Logger.getLogger(this.getClass());
    }
    
    public UserAttributes(UserDef userDef) {
        epaPortal = Config.findNode("app.defaultEPAPortal").getText();
        logger = Logger.getLogger(this.getClass());
    }
    
    public UserAuth getUserAuth() {
		return userAuth;
	}

	public void setUserAuth(UserAuth userAuth) {
		this.userAuth = userAuth;
	}

	public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        if (userId == null) {
            logger.error("User Id argument is null.");
            throw new IllegalArgumentException("UserAttributes.setUserId(userId) userId is null.");
        }
        this.userId = userId;
    }

    public final String getUserName() {
        return userName;
    }

    public final void setUserName(String userName) {
        if (userName.compareTo("") == 0) {
            this.userName = null;
        } else {
            this.userName = userName;
        }
    }

    public final String getPassword() {
        return null;
    }

    public final String getUserFullName() {
        return userFullName;
    }

    public final void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
    
    public final boolean isDisplayUncertainArea(){
    	String isDisplay = Config.findNode("app").getAsString("displayUncertainArea");
    	
        return isDisplay == null? false : Boolean.valueOf(isDisplay);
    }
    
    public final boolean isStars2Admin(){
        return isCurrentUseCaseValid("impactAdmin");
    }
    
    public final boolean isGeneralUser(){
        return isCurrentUseCaseValid("generalUser");
    }
    
    public final boolean isNSRAdminUser() {
    	return isCurrentUseCaseValid("nsrAdmin");
    }
    
    public final boolean isTimesheetAdminUser() {
    	return isCurrentUseCaseValid("timesheetAdmin");
    }
    
    public final boolean isPermitAttachments(){
        return isCurrentUseCaseValid("permits.detail.attachments");
    }
    
    public final boolean isPermitEditable(){
        return isCurrentUseCaseValid("permits.detail.editable");
    }
    
    public final boolean isPermitConditionsEditable(){
        return isCurrentUseCaseValid("permits.detail.permitConditions.editable");
    }
    
    public final boolean isPortalDetailEditable(){
        return isCurrentUseCaseValid("contacts.detail.portalDetail.editable");
    }    
    
    public final boolean isFceScheduler(){
        return isCurrentUseCaseValid("inspectSched");
    }
    
//    public final boolean isCetaUser(){
//        return isCurrentUseCaseValid("cetaUser");
//    }
    
    public final boolean isCetaUpdater(){
        return isCurrentUseCaseValid("cetaUpdater");
    }
    
    public final boolean isGeneralIssuanceUser(){
        return isCurrentUseCaseValid("generalIssuance");
    }

    public final boolean isPublicReadOnlyUser() {
        return isCurrentUseCaseValid("publicReadOnlyUser");
    }
    
    public final boolean isCurrentUseCaseValid(String useCase) {
        return (useCases.containsKey(useCase));
    }

    public final boolean isCurrentViewIdValid(String viewId) {
        boolean ret = true;
        
        if (viewIds == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            SimpleMenuTree menuTree = (SimpleMenuTree) facesContext.getExternalContext().getSessionMap().get(
                        "menuModel");
                
            if (menuTree != null) {
                viewIds = new ArrayList<String>();
                // view id "/__ADFv__.jsp" is a popup window. always?
                viewIds.add("/__ADFv__.jsp");
                // imageView.jsp for image display
                viewIds.add("/util/imageView.jsp");
                // for validation pop up window.
                viewIds.add("/util/validationResults.jsp");
                
                loadAllUseCaseChildren(menuTree.getChildren());
                loadAllViewIdsFor("dialog:");
            }
            
        }
        
        // viewIds could still be null because we may be called before the menuModel
        // has been initialized/.
        if (viewIds != null) {
            ret = viewIds.contains(viewId);
        }
        
        return ret;
    }

    private void loadAllViewIdsFor(String string) {
        ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        RuntimeConfig runtimeConfig = RuntimeConfig
                .getCurrentInstance(externalContext);

        Collection<?> rules = runtimeConfig.getNavigationRules();

        for (Iterator<?> iterator = rules.iterator(); iterator.hasNext();) {
            NavigationRule rule = (NavigationRule) iterator.next();

            Object[] ncs = rule.getNavigationCases().toArray();
            for (int i = 0; i < ncs.length; i++) {
                NavigationCase nc = (NavigationCase) ncs[i];
                if (nc.getFromOutcome() != null
                        && nc.getFromOutcome().contains(string)) {
                    viewIds.add(nc.getToViewId());
                }
            }

        }
    }

    /*
    public final Timestamp getPasswordExpiration() {
        return passwordExpDt;
    }

    public final void setPasswordExpiration(Timestamp passwordExpDt) {
        this.passwordExpDt = passwordExpDt;
    }
*/
    public final String getEpaPortal() {
        return epaPortal;
    }

    public final void setEpaPortal(String epaPortal) {        
        this.epaPortal = epaPortal;
    }

    public final String logout() {
        reset();
        
        if (stars2Login) {
        	goLogout();            
        } else {
            goEpaPortal();
        }

        return AppBase.SUCCESS;
    }

    public final String login() {
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	HttpServletRequest request = (HttpServletRequest) ctx
                .getExternalContext().getRequest();
    	String userNameWithDomian = request.getRemoteUser();
    	
//    	userName = userNameWithDomian.substring(userNameWithDomian.lastIndexOf("\\")+1);
    	userName = request.getRemoteUser(); //TODO remove this when we get waffle to work in azure
    	
        String ret = "Failure";
        
       /*if ((userName != null) && (password != null)) {
            if (userAuth == null) {
                try {
                    userAuth = (UserAuth) CompMgr.newInstance(UserAuth.DEFAULT);
                } 
                catch (UnableToStartException e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        
            UserAttributes userAttr = userAuth.checkAuthentication(userName,
                    password);*/
//        	if (userAuth == null) {
//	            try {
//	                userAuth = (UserAuth) CompMgr.newInstance(UserAuth.DEFAULT);
//	            } 
//	            catch (UnableToStartException e) {
//	                logger.error(e.getMessage(), e);
//	                throw new RuntimeException(e);
//	            }
//        	}
        	UserAttributes userAttr = userAuth.checkUserExists(userName);
        	
        	boolean userExits = userAuth.checkUserPresent(userName);
        	boolean userActive = userAuth.checkUserActive(userName);
        	
        	if (!userExits) {
        		boolean isUserCreated=userAuth.createInternalUser(userName);
        		if(isUserCreated)
        			logger.debug("Internal user created");
        		else
        			logger.debug("Internal user creation failed");
        	} else if(!userActive) {
        		logger.debug("Internal user is de-activated");
        		reset();
        		throw new InvalidUserException("Internal user is de-activated: " + request.getRemoteUser());
        	}

            if (userAttr != null) {
                setInternal(userAttr);

                useCases = userAuth.retrieveUserUseCases(userId);
                ret = AppBase.SUCCESS;
            }
      

        return ret;
    }

    public final String goLogin() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        HttpServletRequest request = (HttpServletRequest) ctx
                .getExternalContext().getRequest();

        redirect(request.getContextPath() + "/login.jsf");
        
        return "Login";
    }
    
    public final String goLogout() {
        FacesContext ctx = FacesContext.getCurrentInstance();

        HttpServletRequest request = (HttpServletRequest) ctx
                .getExternalContext().getRequest();

        redirect(request.getContextPath() + "/logout.jsf");
        
        return "Logout";
    }
    
    public final String goEpaPortal() {
        reset();

        redirect(epaPortal);
        
        return "Portal";
    }
    
    public final String goHelp() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = facesContext.getViewRoot().getViewId();

        logger.debug("Built usecase map");
        
        String useCaseName = viewIdsToUseCase.get(viewId);
        String url = null;
        
        if (useCaseName != null) {
            logger.debug("usecase name " + useCaseName);
            AdfFacesContext context = AdfFacesContext.getCurrentInstance();

            StringTokenizer st = new StringTokenizer(useCaseName, ".");
            String topic = st.nextToken();
            logger.debug("topic before " + topic);
            
            while (st.hasNext()) 
                topic = topic + "_" + st.nextToken();
                
            logger.debug("topic after " + topic);
            
            url = (String) context.getHelpTopic().get(topic);
            
            if (url != null) {
                logger.debug("url not null");
                HttpServletResponse response = (HttpServletResponse) facesContext
                        .getExternalContext().getResponse();

                url = "window.open" + url.replace("javascript:(new Function('a', 'b', 'c', 'd', 'openWindow(a, b, c, d)'))(top,", "(");
                url = url.replace("{width:650, height:450, menubar:0, location:0, status:0, directories:0}", "'height=450,width=650,resizable=yes,scrollbars=yes'");
                try {
                    PrintWriter responseWriter = response.getWriter();
                    responseWriter.println("<script>");
                    responseWriter.println(url);
                    responseWriter.println("</script>");
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);
                }

            }
        }
        
        return null;
    }
    
    public final String redirect(String redirectTo) {
        FacesContext ctx = FacesContext.getCurrentInstance();

        HttpServletResponse response = (HttpServletResponse) ctx
                .getExternalContext().getResponse();

        try {
            logger.debug("redirecting to '" + redirectTo + "'");
            response.sendRedirect(redirectTo);
        } catch (IOException ioe) {
            logger.error("Can't redirect to " + redirectTo, ioe);
        }
        ctx.responseComplete();
        
        return "Portal";
    }
    
    public final String reset() {
        userName = null;

        FacesContext ctx = FacesContext.getCurrentInstance();
        
        HttpSession session = (HttpSession)ctx.getExternalContext().getSession(false);

        // This REALLY should not happen, but we check it anyway.
        if (session != null) { 
            logger.debug("reset() - invalidating current session");
            session.invalidate();
        }
        
        return AppBase.SUCCESS;
    }

    private void setInternal(UserAttributes userAttr) {
        if (userAttr != null) {
            userId = userAttr.getUserId();
            userName = userAttr.getUserName();
            userFullName = userAttr.getUserFullName();
            loggedIn = true;
//            passwordExpDt = userAttr.getPasswordExpiration();
        }
    }

    public final boolean isStars2Login() {
        return stars2Login;
    }

    public final void setStars2Login(boolean stars2Login) {        
        this.stars2Login = stars2Login;
    }

    public final User getItsUser() {
        return itsUser;
    }

    public final void setItsUser(User itsUser) {
        this.itsUser = itsUser;
    }
    
    private void loadAllUseCaseChildren(List<SimpleMenuItem> children) {
        if (children != null) {
            for (SimpleMenuItem item : children) {
                if (item.getChildren() != null) {
                    loadAllUseCaseChildren(item.getChildren());
                }

                List<String> temp = item.getViewIDs();
                if (temp != null) {
                    for (String viewId : item.getViewIDs()) {
                        viewIds.add(viewId);
                    }
                }
            }
        } else {
            Throwable temp = new Throwable();

            temp.fillInStackTrace();

            logger.error("Children is null", temp);
        }
    }
    
//    public  UserAuth getUserAuth() {
//        return userAuth;
//    }
//
//    public  void setUserAuth(UserAuth userAuth) {
//        this.userAuth = userAuth;
//    }

    public final LinkedHashMap<String, UseCase> getUseCases() {
        return useCases;
    }

    public final void setUseCases(LinkedHashMap<String, UseCase> useCases) {
        this.useCases = useCases;
    }

    public final List<String> getViewIds() {
        return viewIds;
    }

    public final void setViewIds(List<String> viewIds) {
        this.viewIds = viewIds;
    }

    public final HashMap<String, String> getViewIdsToUseCase() {
        return viewIdsToUseCase;
    }

    public final void addViewIdToUseCase(String viewId, String name) {
        if (this.viewIdsToUseCase == null) {
            this.viewIdsToUseCase = new HashMap<String,String>();
        }
        
        this.viewIdsToUseCase.put(viewId, name);
    }
    
    public final void setViewIdsToUseCase(HashMap<String, String> viewIdsToUseCase) {
        this.viewIdsToUseCase = viewIdsToUseCase;
    }

    public final boolean isLoggedIn() {
        return loggedIn;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}
