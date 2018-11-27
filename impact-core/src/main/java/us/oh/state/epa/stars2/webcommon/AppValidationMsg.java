package us.oh.state.epa.stars2.webcommon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import oracle.adf.view.faces.component.UIXShowDetail;
import oracle.adf.view.faces.component.UIXSwitcher;
import oracle.adf.view.faces.component.core.layout.CorePanelBorder;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.StopWatch;

public class AppValidationMsg implements Serializable {
    public static final String VALIDATION_RESULTS_URL = "../util/validationResults.jsf";

    private static final String VALIDATION_BEAN_NAME = "validationMsgs";

    public static final String CLOSE_VALIDATION_DIALOG = "CLOSE_VALIDATION_DIALOG";
    
    public static enum Severity {
        INFO, WARNING, ERROR
    }

    private Severity severity;

    private String message;
    
    private String euId;

    private String referenceID;

    private String clientID;

    private String showDetailClientID;
    
    private Integer permitId;

    public final Severity getSeverity() {
        return severity;
    }

    public final void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public final String getMessage() {
        return message;
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    public final String getReferenceID() {
        return referenceID;
    }

    public final void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    public final String getClientID() {
        return clientID;
    }

    public final void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public final String getShowDetailClientID() {
        return showDetailClientID;
    }

    public final void setShowDetailClientID(String showDetailClientID) {
        this.showDetailClientID = showDetailClientID;
    }

    @SuppressWarnings("unchecked")
    public static boolean validate(List<ValidationMessage> messages, boolean pop) {
    	return validate(messages, pop, false);
    }

    @SuppressWarnings("unchecked")
    public static boolean validate(List<ValidationMessage> messages, boolean pop, boolean errorMsgAdded) {

        boolean hasErrors = false;

        if (messages != null && !messages.isEmpty()) {
            List<AppValidationMsg> validationMsgs = new ArrayList<AppValidationMsg>();
            for (ValidationMessage msg : messages) {
                if (msg.getSeverity().equals(ValidationMessage.Severity.ERROR)) {
                    hasErrors = true;
                }
                validationMsgs.add(convertToAppValidationMsg(msg));
            }

            if (!validationMsgs.isEmpty() && pop) {
           		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(CLOSE_VALIDATION_DIALOG, "TRUE");
                FacesContext.getCurrentInstance().getExternalContext()
                        .getSessionMap().put(VALIDATION_BEAN_NAME,
                                validationMsgs);
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("disableLinks", !hasErrors);
                FacesUtil.startModelessDialog(VALIDATION_RESULTS_URL, 500, 500);
                //FacesContext.getCurrentInstance().renderResponse();
            }
        }
        
        if (hasErrors) {
        	if (!errorMsgAdded) { 
        		DisplayUtil.displayError("Validation Failed. See 'Validation results' pop up window for detail.");
        	}
        } else {
            DisplayUtil.displayInfo("Validation Successful");
        }

        return !hasErrors;
    }

    @SuppressWarnings("unchecked")
    public static void buildMap() {
        String page = FacesUtil.getCurrentPage();

        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        Hashtable<String, HashMap<String, String>> allIDMap = cfgMgr.getAllIDMap();
        HashMap<String, String> idMap = allIDMap.get(page);
        if (idMap == null){
            idMap = new HashMap<String, String>();
            allIDMap.put(page, idMap);
            UIComponent root = FacesContext.getCurrentInstance().getViewRoot();
            List<UIComponent> children = root.getChildren();
            StopWatch timer = new StopWatch();
            timer.start();
            buildMap(children.iterator(), root.getId(), idMap);
            timer.stop();
            Logger.getLogger("AppValidationMsg").debug("Build id map done in : " + timer.toString());
        }
        return;
    }

    @SuppressWarnings("unchecked")
    private static void buildMap(Iterator<? extends UIComponent> it, String pid, 
            HashMap<String, String> idMap) {

        while (it.hasNext()) {
            UIComponent comp = it.next();
            String id = comp.getId();
            String wID = pid;
            if (comp instanceof NamingContainer) {
                wID = getLinkedID(wID, id);
                idMap.put(id, wID);
                addShowDetailClientID(id, comp, idMap);
            } else {
                if (id.charAt(0) != '_') {
                    idMap.put(id, getLinkedID(wID, id));
                    addShowDetailClientID(id, comp, idMap);
                }
            }
            if (comp instanceof UIXSwitcher) {
                UIXSwitcher switcher = (UIXSwitcher) comp;
                buildMap(switcher.getFacetsAndChildren(), wID, idMap);
            } else if (comp instanceof CorePanelBorder) {
                CorePanelBorder switcher = (CorePanelBorder) comp;
                buildMap(switcher.getFacetsAndChildren(), wID, idMap);
            } else if (comp.getChildren().size() > 0) {
                buildMap(comp.getChildren().iterator(), wID, idMap);
            }
        }
    }

    private static void addShowDetailClientID(String id, UIComponent comp, 
            HashMap<String, String> idMap) {
        UIComponent tc = comp;
        for (int i = 0; i < 6; i++) {
            if (tc == null) {
                break;
            }
            tc = tc.getParent();
            if (tc instanceof UIXShowDetail) {
                ConfigManager cfgMgr = ConfigManagerFactory.configManager();
                Hashtable<String, HashMap<String, String>> allIDMap = cfgMgr.getAllIDMap();
                HashMap<String, String> showDetailClientIDMap = allIDMap.get("showDetailClientIDMap");
                if (showDetailClientIDMap == null)
                    showDetailClientIDMap = new HashMap<String, String>();
                showDetailClientIDMap.put(id, idMap.get(tc.getId()));
                break;
            }
        }
    }

    private static String getLinkedID(String wid, String id) {
        String tID;
        if (wid != null) {
            tID = wid + ":" + id;
        } else {
            tID = id;
        }

        return tID;
    }

    private static AppValidationMsg convertToAppValidationMsg(
            ValidationMessage msg) {
        AppValidationMsg retVal = new AppValidationMsg();

        /*
         * Set severity, message text and reference ID
         */
        if (msg.getSeverity().equals(ValidationMessage.Severity.ERROR)) {
            retVal.setSeverity(Severity.ERROR);
        } else if (msg.getSeverity().equals(ValidationMessage.Severity.WARNING)) {
            retVal.setSeverity(Severity.WARNING);
        } else {
            retVal.setSeverity(Severity.INFO);
        }
        retVal.setMessage(msg.getMessage());
        retVal.setReferenceID(msg.getReferenceID());

        if (msg.getProperty() == null) {
            retVal.setClientID(null);
            retVal.setShowDetailClientID(null);
        } else {
            retVal.setClientID(msg.getProperty());
            findClientID(retVal);
        }
        retVal.setEuId(msg.getEuId());
        retVal.setPermitId(msg.getPermitId());

        return retVal;
    }

    private static void findClientID(AppValidationMsg retVal) {
        String ref = retVal.getReferenceID();
        String cID = retVal.getClientID();
        if (!cID.contains(":")) {
            retVal.setClientID("");
            if (ref != null) {
                String page = ref.substring(ref.lastIndexOf(":") + 1, ref
                        .length());
                ConfigManager cfgMgr = ConfigManagerFactory.configManager();
                Hashtable<String, HashMap<String, String>> allIDMap = cfgMgr.getAllIDMap();
                HashMap<String, String> idMap = allIDMap.get(page);
                if (idMap != null) {
                    String id = idMap.get(cID);

                    if (id == null) {
                        buildMap();
                        id = idMap.get(cID);
                    }
                    if (id != null) {
                        retVal.setClientID(id);
                        Map<String, String> showDetailClientIDMap = allIDMap.get("showDetailClientIDMap");
                        if (showDetailClientIDMap != null)
                            retVal.setShowDetailClientID(showDetailClientIDMap
                                    .get(cID));
                        else
                            retVal.setShowDetailClientID(null);
                    }
                }
            }
        }
    }

    public static String updateClientID(String clientID) {
        String page = FacesUtil.getCurrentPage();
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        Hashtable<String, HashMap<String, String>> allIDMap = cfgMgr.getAllIDMap();
        Map<String, String> idMap = allIDMap.get(page);
        if (idMap == null)
            buildMap();

        if (idMap != null) {
            String id = idMap.get(clientID);
            if (id != null) {
                return id;
            }
        }
        return clientID;
    }

    /**
     * @return the euId
     */
    public final String getEuId() {
        return euId;
    }

    /**
     * @param euId the euId to set
     */
    public final void setEuId(String euId) {
        this.euId = euId;
    }

	public Integer getPermitId() {
		return permitId;
	}

	public void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}
}
