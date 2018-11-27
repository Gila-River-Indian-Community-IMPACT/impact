package us.oh.state.epa.stars2.webcommon;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

/*
 * DisplayUtil allows the application to display messages
 * to the JSF <h:messages /> or ADF <af:messages /> tag via
 * a number of static methods. Unlike FacesContext.addMessage(),
 * DisplayUtil works with navigation rules using <rediret/>.
 */
public class DisplayUtil implements java.io.Serializable {

	private static final long serialVersionUID = 3309707928164482436L;
	private static Logger logger = Logger.getLogger(DisplayUtil.class);

    protected static class QueuedMessage implements java.io.Serializable {
    	
		private static final long serialVersionUID = 3350729579703032874L;
		String text;
        String severity;
        String clientID;
    }

    public static final String DISPLAY_UTIL_SESSION_ATTRIBUTE = "displayUtil";
    public static final String ERROR = "error";
    public static final String WARN = "warn";
    public static final String INFO = "info";
    private List<QueuedMessage> messages = new ArrayList<QueuedMessage>();

    public DisplayUtil() {
        super();
    }

    public static void displayError(String text) {
        displayMessage(text, ERROR, null);
    }

    public static void displayWarning(String text) {
        displayMessage(text, WARN, null);
    }

    public static void displayInfo(String text) {
        displayMessage(text, INFO, null);
    }

    public static void displayError(String text, String clientID) {
        logger.debug("displayError: " + text);
        displayMessage(text, ERROR, clientID);
    }

    public static void displayWarning(String text, String clientID) {
        logger.debug("displayWarning: " + text);
        displayMessage(text, WARN, clientID);
    }

    public static void displayInfo(String text, String clientID) {
        logger.debug("displayInfo: " + text);
        displayMessage(text, INFO, clientID);
    }

    public static DisplayUtil getSessionInstance(FacesContext facesContext,
                                                    boolean createIfNeeded) {
        logger.debug("start getSessionInstance");
        
        DisplayUtil util = (DisplayUtil) facesContext.getApplication().getVariableResolver()
                .resolveVariable(facesContext, DISPLAY_UTIL_SESSION_ATTRIBUTE);
            
        if (util == null && createIfNeeded) {
            logger.debug("create new DisplayUtil. This should never happen!");
            util = new DisplayUtil();
        }
        logger.debug("end getSessionInstance");

        return util;
    }

    private static void displayMessage(String text, String severity, String clientID) {
        logger.debug("start displayMessage");             
        FacesContext facesContext = FacesContext.getCurrentInstance();
        DisplayUtil util = getSessionInstance(facesContext, true);
        util.addMessageToQueue(text, severity, clientID);
        logger.debug("end displayMessage"); 
    }

    public void addMessageToQueue(String text, String severity, String clientID) {
        logger.debug("start addMessageToQueue");    
        QueuedMessage queuedMsg = new QueuedMessage();
        queuedMsg.text = text;
        queuedMsg.severity = severity;
        queuedMsg.clientID = clientID;
        messages.add(queuedMsg);
        logger.debug("end addMessageToQueue");  
    }

    protected void renderQueuedMessages(FacesContext facesContext) {
        logger.debug("start renderQueuedMessages");  
        for (QueuedMessage queuedMsg : messages) {
            FacesMessage facesMessage = new FacesMessage(queuedMsg.text);
            if (ERROR.equalsIgnoreCase(queuedMsg.severity)) {
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            }
            else if (WARN.equalsIgnoreCase(queuedMsg.severity)) {
                facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
            }
            else {
                facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
            }
            logger.debug("adding message to faces context. Client ID = " + 
                    queuedMsg.clientID + ", message detail = " + facesMessage.getDetail());
            facesContext.addMessage(queuedMsg.clientID, facesMessage);
        }
        if (messages != null)
            messages.clear();
        logger.debug("end renderQueuedMessages");  
    }
    
    public static void clearQueuedMessages() {
    	FacesContext facesContext = FacesContext.getCurrentInstance();
        DisplayUtil util = getSessionInstance(facesContext, true);
        
    	if (util.messages != null)
    		util.messages.clear();
    }
    
    public String getQueuedMessages() {
        for (QueuedMessage queuedMsg : messages) {
            FacesMessage facesMessage = new FacesMessage(queuedMsg.text);
            if (ERROR.equalsIgnoreCase(queuedMsg.severity)) {
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            }
            else if (WARN.equalsIgnoreCase(queuedMsg.severity)) {
                facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
            }
            else {
                facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
            }
            logger.debug("adding message to faces context. Client ID = " + 
                    queuedMsg.clientID + ", message detail = " + facesMessage.getDetail());
            FacesContext.getCurrentInstance().addMessage(queuedMsg.clientID, facesMessage);
        }
        if (messages != null)
            messages.clear();
        return "";
    }

    public static void displayNoRecords() {
        displayInfo("Search returned no records.");
    }

    public static void displayHitLimit(int i) {
    	
		InfrastructureDefs infraDefs = (InfrastructureDefs) FacesUtil.getManagedBean("infraDefs");
    	
    	if (infraDefs.isInternalApp()) {
    		String rs = SystemPropertyDef.getSystemPropertyValue("NoRowLimitUseCase", "noRowLimit");
    		if (!InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(rs)) {
    			int defaultSearchLimit = infraDefs.getDefaultSearchLimit();
    	        if (i == defaultSearchLimit) {
    	            displayInfo("Search result size is limited to " + defaultSearchLimit + " hits.");
    	        }
            }
    	}

    }

}
