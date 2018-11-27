package us.oh.state.epa.stars2.webcommon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.component.core.data.CoreTable;
import oracle.adf.view.faces.component.core.input.CoreSelectOneChoice;
import oracle.adf.view.faces.model.BoundedRangeModel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.def.ContentTypeDef;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.RegulatoryRequirementTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.framework.exception.OutOfMemoryDAOException;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class AppBase extends BoundedRangeModel 
    implements SessionCacheObject, java.io.Serializable {

	private static final long serialVersionUID = -5292867433590471841L;

	@Autowired DoLaaDef doLaaDef;
	@Autowired SystemPropertyDef systemPropertyDef;
	
    protected List<String> cacheViewIDs = new ArrayList<String>();
    public static final String SUCCESS = "Success";
    public static final String CANCELLED = "Cancelled";
    public static final String ERROR = "Error";
    public static final String WARNING = "Warning";
    public static final String FAIL = "Fail";
    public static final String EDITABLE = "Editable";
//    private final static boolean internalApp = true; //TODO RESOLVE CompMgr.getAppName().equals(CommonConst.INTERNAL_APP);
    protected transient Logger logger;
    private transient CoreSelectOneChoice parent;
    
    /**
     * objects used for generic handling of deleting rows from a table
     * and adding rows to a table
     */
    protected transient UIXTable actionTable;
    protected transient Object actionTableNewObject;

    /** BoundedRangeModel variables for jsp af:progressIndicator progress bar. */
    private long _maximumBoundedValue;
    private long _currentBoundedValue;
    
    // Used with routines firstButtonClick() and clearButtonClicked()
    private boolean buttonClicked = false;
    
    private InfrastructureService infrastructureService;

    public AppBase() {
        logger = Logger.getLogger(this.getClass());
    }

    public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}



/**    
    protected final ApplicationService applicationBO() throws RemoteException {
        ApplicationService service = null;

        try {
            service = ServiceFactory.getInstance().getApplicationService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }

    protected final CorrespondenceService correspondenceBO()
            throws RemoteException {
        CorrespondenceService service = null;

        try {
            service = ServiceFactory.getInstance().getCorrespondenceService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }

    protected final DocumentService documentBO() throws RemoteException {
        DocumentService service = null;

        try {
            service = ServiceFactory.getInstance().getDocumentService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final ComplianceReportService complianceReportBO() throws RemoteException {
        ComplianceReportService service = null;

        try {
            service = ServiceFactory.getInstance().getComplianceReportService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final RelocateRequestService relocateRequestBO() throws RemoteException {
        RelocateRequestService service = null;
        try {
            service =  ServiceFactory.getInstance().getRelocateRequestService(); 
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    
    protected final DelegationRequestService delegationRequestBO() throws RemoteException {
        DelegationRequestService service = null;

        try {
            service =  ServiceFactory.getInstance().getDelegationRequestService(); 
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }

    protected final EmissionsReportService emissionsReportBO()
            throws RemoteException {
        EmissionsReportService service = null;

        try {
            service = ServiceFactory.getInstance().getEmissionsReportService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }

    protected final FacilityService facilityBO() throws RemoteException {
        FacilityService service = null;

        try {
            service = ServiceFactory.getInstance().getFacilityService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final NaicsService naicsBO() throws RemoteException {
    	NaicsService service = null;

        try {
            service = ServiceFactory.getInstance().getNaicsService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }

    protected final CompanyService companyBO() throws RemoteException {
        CompanyService service = null;

        try {
            service = ServiceFactory.getInstance().getCompanyService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final ContactService contactBO() throws RemoteException {
    	ContactService service = null;

        try {
            service = ServiceFactory.getInstance().getContactService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final InfrastructureService infrastructureBO()
            throws RemoteException {
        InfrastructureService service = null;

        try {
            service = ServiceFactory.getInstance().getInfrastructureService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }

    protected final PermitService permitBO() throws RemoteException {
        PermitService service = null;

        try {
            service = ServiceFactory.getInstance().getPermitService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }

    protected final ReportService reportBO() throws RemoteException {
        ReportService service = null;

        try {
            service = ServiceFactory.getInstance().getReportService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }

    protected final WorkFlowService workFlowBO() throws RemoteException {
        WorkFlowService service = null;

        try {
            service = ServiceFactory.getInstance().getWorkFlowService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final GenericIssuanceService genericIssuanceBO() throws RemoteException {
        GenericIssuanceService service = null;

        try {
            service = ServiceFactory.getInstance().getGenericIssuanceService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
**/

    // Get reporting Categories (does not include TV Type A)
    protected final List<SelectItem> getReportingTypes(String reportCd) {
        return EmissionReportsDef.getData().getItems().getItems(reportCd, true);
    }
    
    protected final List<SelectItem> getContentTypes(String contentTypeCd) {
        return ContentTypeDef.getData().getItems().getItems(contentTypeCd, true);
    }
    
    protected final List<SelectItem> getRegulatoryRequirementTypes(String regulatoryRequirementCd) {
        return RegulatoryRequirementTypeDef.getData().getItems().getItems(regulatoryRequirementCd, true);
    }

    protected final List<SelectItem> getDoLaas(String doLaaCd) {
        return doLaaDef.getData().getItems().getItems(doLaaCd, true);
    }

    protected final List<SelectItem> getPermitTypes(String permitTypeCd) {
        return PermitTypeDef.getData().getItems().getItems(permitTypeCd, true);
    }

    /**
     * @param parent
     * 
     */
    public final void setParent(CoreSelectOneChoice parent) {
        this.parent = parent;
    }

    protected final CoreSelectOneChoice getParent() {
        return parent;
    }

    protected final String getSeverity(ValidationMessage validationMessage) {
        if (validationMessage.getSeverity().equals(
                ValidationMessage.Severity.ERROR)) {
            return "ERROR: ";
        } else if (validationMessage.getSeverity().equals(
                ValidationMessage.Severity.INFO)) {
            return "INFO: ";
        } else if (validationMessage.getSeverity().equals(
                ValidationMessage.Severity.WARNING)) {
            return "WARNING: ";
        }
        return "";
    }

    protected final boolean displayValidationMessages(String pageViewId,
            ValidationMessage[] validationMessages) {
        int i;
        String clientId;
        boolean hasError = false;

        for (i = 0; i < validationMessages.length; i++) {
            if (validationMessages[i].getSeverity().equals(
                    ValidationMessage.Severity.ERROR)) {
                hasError = true;
                clientId = pageViewId + validationMessages[i].getProperty();
                DisplayUtil.displayError(getSeverity(validationMessages[i])
                        + validationMessages[i].getMessage(), clientId);
            }
            else if(validationMessages[i].getSeverity().equals(
                    ValidationMessage.Severity.WARNING)) {
            	clientId = pageViewId + validationMessages[i].getProperty();
            	DisplayUtil.displayWarning(getSeverity(validationMessages[i])
            			+ validationMessages[i].getMessage(), clientId);
            }
        }
        return hasError;
    }

    protected final void displayValidationMessage(String pageViewId,
            ValidationMessage validationMessage) {
        String clientId;
        clientId = pageViewId + validationMessage.getProperty();
        DisplayUtil.displayError(getSeverity(validationMessage)
                + validationMessage.getMessage(), clientId);
    }

    protected final HashMap<String, Object> getSortedStringMap(HashMap<String, String> hmap) {
        HashMap<String, Object> map = new LinkedHashMap<String, Object>();
        List<String> mapKeys = new ArrayList<String>(hmap.keySet());
        List<String> mapValues = new ArrayList<String>(hmap.values());
        hmap.clear();
        TreeSet<String> sortedSet = new TreeSet<String>(mapValues);
        Object[] sortedArray = sortedSet.toArray();
        int size = sortedArray.length;

        // Ascending sort
        for (int i = 0; i < size; i++) {
            map.put(mapKeys.get(mapValues.indexOf(sortedArray[i])),
                    sortedArray[i]);
        }
        return map;
    }

    public final boolean isInternalApp() {
//        return internalApp;
        return CompMgr.getAppName().equals(CommonConst.INTERNAL_APP);
    }
    
	public final boolean isPortalApp() {
		return CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP);
	}

	public final boolean isPublicApp() {
		return CompMgr.getAppName().equals(CommonConst.PUBLIC_APP);
	}
    
    public boolean isStars2Admin() {
    	boolean isAdmin = false;
    	
    	if (isInternalApp()) {
    		UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
    		if (attrs != null) {
    			isAdmin = attrs.isStars2Admin();
    		}
    	}
        return isAdmin;
    }
    
    public boolean isPermitAttachments() {
        boolean rtn = false;
        
        if (isInternalApp()) {
            UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
            if (attrs != null) {
                rtn = attrs.isPermitAttachments();
            }
        }
        return rtn;
    }
    
    public boolean isPermitEditable() {
        boolean rtn = false;
        
        if (isInternalApp()) {
            UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
            if (attrs != null) {
                rtn = attrs.isPermitEditable();
            }
        }
        return rtn;
    }

    public boolean isPortalDetailEditable() {
        boolean rtn = false;
        
        if (isInternalApp()) {
            UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
            if (attrs != null) {
                rtn = attrs.isPortalDetailEditable();
            }
        }
        return rtn;
    }    
    
    public final boolean isFceScheduler() {
        boolean isFceScheduler = false;
        
        if (isInternalApp()) {
            UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
            if (attrs != null) {
                isFceScheduler = attrs.isFceScheduler();
            }
        }
        return isFceScheduler;
    }

//    public final boolean isCetaUser() {
//    	boolean isCetaUser = false;
//    	
//    	if (isInternalApp()) {
//    		UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
//    		if (attrs != null) {
//    			isCetaUser = attrs.isCetaUser();
//    		}
//    	}
//        return isCetaUser;
//    }
    
    protected final boolean isCetaUpdater() {
        boolean isCetaUpdater = false;
    	
    	if (isInternalApp()) {
    		UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
    		if (attrs != null) {
                isCetaUpdater = attrs.isCetaUpdater();
            }
    		}
        return isCetaUpdater;
    	}
    
    public final boolean isCetaUpdate() {
    	return (isStars2Admin() || isCetaUpdater());
    }

    public final boolean isGeneralIssuanceUser() {
    	boolean isGeneralIssuanceUser = false;
    	
    	if (isInternalApp()) {
    		UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
    		if (attrs != null) {
    			isGeneralIssuanceUser = attrs.isGeneralIssuanceUser();
    		}
    	}
        return isGeneralIssuanceUser;
    }
    
    
    public final boolean isPublicReadOnlyUser() {
    	boolean isPublicReadOnlyUser = false;
    	
    	if (isInternalApp()) {
    		UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
    		if (attrs != null) {
    			isPublicReadOnlyUser = attrs.isPublicReadOnlyUser();
    		}
    	}
        return isPublicReadOnlyUser;
    }
    
    
    
    protected final String getAppName() {
        return CompMgr.getAppName();
    }
    
    public final Integer getPageLimit() {
        return SystemPropertyDef.getSystemPropertyValueAsInteger("PaginationLimit", null);
    }
    
    public final Integer getPageLimitShort() {
        return SystemPropertyDef.getSystemPropertyValueAsInteger("PaginationLimitShort", null);
    }
    
    protected String getExternalParameter(String parm) {
    	String value = null;
		String jndiName = Config.findNode(parm).getAsString("jndiName");
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			value = (String) envContext.lookup(jndiName);
		} catch (NamingException ne) {
			throw new RuntimeException(ne);
		}
        return value;
    }
    
    /**
     * "Action Table" methods. These methods support managing adding data to
     * and deleting data from tables in a generic manner.
     */
    
    /**
     * Initialize the actionTable by setting it to the table component that is the parent
     * of the component that fired this event. This is meant to be fired by
     * command buttons that are children of a table and is used so this class
     * knows which table is invoking an action method.
     * @param event
     */
    public void initActionTable(ActionEvent event) {
        UIComponent comp = event.getComponent();
        // find the table componenet which should be in the buttons ancestry
        while (!(comp instanceof CoreTable) && comp.getParent() != null) {
            comp = comp.getParent();
        }
        if (comp != null) {
            actionTable = (UIXTable)comp;
        }
    }

    /**
     * Retrieve the object used as a "blank" row in the actionTable.
     * @return
     */
    public final Object getActionTableNewObject() {
        return actionTableNewObject;
    }

    /**
     * Set the object used as a "blank" row in the actionTable.
     * @param actionTableObject
     */
    public final void setActionTableNewObject(Object actionTableObject) {
        this.actionTableNewObject = actionTableObject;
    }

    /**
     * Add a new row to the actionTable. To use this method, set the actionListener
     * attribute of the command button to "initActionTable" and set the action
     * attribute to "addActionTableRow". Additionally, the command button must have
     * an updateActionListener child which sets the actionTableObject to a new
     * instance of the type of object inserted into each row of the table. The 
     * command button must be a child of a table in order for this to work.
     *
     */
    @SuppressWarnings("unchecked")
    public final void addActionTableRow() {
        if (actionTable != null && actionTable.getValue() != null && actionTableNewObject != null) {
            try {
                ((Collection)actionTable.getValue()).add(actionTableNewObject);
            } catch (Exception e) {
                logger.error("Exception attempting to create action table object", e);
            }
        } else {
            logger.error("Missing component(s) required for add to table operation.");
        }
    }

    /**
     * Delete one or more rows from the actionTable. To use this method, set the actionListener
     * attribute of the command button to "initActionTable" and set the action
     * attribute to "deleteActionTableRows". The command button must be a child
     * of a table in order for this to work.
     *
     */
    public final void deleteActionTableRows() {
        if (actionTable != null && actionTable.getValue() != null && actionTable.getValue() instanceof Collection<?>) {
            List<Object> delObjects = new ArrayList<Object>();
            Iterator<?> it = actionTable.getSelectionState().getKeySet().iterator();
            Object oldKey = actionTable.getRowKey();
            while (it.hasNext()) {
                Object obj = it.next();
                actionTable.setRowKey(obj);
                delObjects.add(actionTable.getRowData());
            }
            Collection<?> values = (Collection<?>)actionTable.getValue();
            for (Object o : delObjects) {
                values.remove(o);
            }
    
            actionTable.setRowKey(oldKey);
            actionTable.getSelectionState().clear();
        }
    }
    
    /**
     * Retrieve the rows currently selected in actionTable.
     * @return
     */
    public final List<Object> getSelectedActionTableRows() {
        List<Object> selectedRows = new ArrayList<Object>();
        if (actionTable != null && actionTable.getValue() != null && actionTable.getValue() instanceof Collection<?>) {
            Object oldKey = actionTable.getRowKey();
            Iterator<?> it = actionTable.getSelectionState().getKeySet().iterator();
            while (it.hasNext()) {
                actionTable.setRowKey(it.next());
                selectedRows.add(actionTable.getRowData());
            }
            actionTable.setRowKey(oldKey);
        }
        return selectedRows;
    }

    public String reload() {
        return null;
    }
    
    protected void handleException(RemoteException re) {
        if (re != null) {
            if (re instanceof DataStoreConcurrencyException) {
                DisplayUtil.displayWarning("The data you are trying to update is out of synch with the data store. Please try again.");
                logger.error(re.getMessage(), re);
            } else if (re instanceof OutOfMemoryDAOException) {
                DisplayUtil.displayError("System error: The system has encountered an out-of-memory condition. If the problem persists, please contact the System Administrator");
                logger.error(re.getMessage(), re);
            }else if (re.getClass().equals(DAOException.class) && !((DAOException)re).prettyMsgIsNull()) {
                String s = ((DAOException)re).getPrettyMsg();
                DisplayUtil.displayError(s);
                logger.error(re.getMessage(), re);
            } else {
                DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
                logger.error(re.getMessage(), re);
            }
        }
    }
    
    protected void handleException(String msg, RemoteException re) {
        if (re != null) {
            if (re instanceof DataStoreConcurrencyException) {
                DisplayUtil.displayWarning("The data you are trying to update is out of synch with the data store. Please try again.");
                logger.error(re.getMessage() + " with " + msg, re);
            } else if (re instanceof OutOfMemoryDAOException) {
                DisplayUtil.displayError("System error: The system has encountered an out-of-memory condition. If the problem persists, please contact the System Administrator");
                logger.error(re.getMessage() + " with " + msg, re);
            }else if (re.getClass().equals(DAOException.class) && !((DAOException)re).prettyMsgIsNull()) {
                String s = ((DAOException)re).getPrettyMsg();
                DisplayUtil.displayError(s);
                logger.error(re.getMessage() + " with " + msg, re);
            } else {
                DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
                logger.error(re.getMessage() + " with " + msg, re);
            }
        }
    }
    
    protected boolean unlimitedResults() {
        boolean ret = false;

        if (isInternalApp()) {
            String rs = "";
        	rs = SystemPropertyDef.getSystemPropertyValue("NoRowLimitUseCase", "noRowLimit");
            ret = InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(rs);
        }

        return ret;
    }
    
    public boolean isReadOnlyUser() {
        boolean ret = false;

        if (isInternalApp()) {
            ret = InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("readOnly");
        }

        return ret;
    }
    
    public boolean isReleasePlannerUser() {
        boolean ret = false;

        if (isInternalApp()) {
            ret = InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("releasePlanner");
        }

        return ret;
    }
    
    public boolean isGeneralUser() {
        boolean ret = false;

        if (isInternalApp()) {
            ret = InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("generalUser");
        }

        return ret;
    }
    
    public boolean isEnforcementActionUser() {
        boolean ret = false;

        if (isInternalApp()) {
            ret = InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("enforcementAction.detail");
        }

        return ret;
    }
    
    public boolean isMonitorReportUploadUser() {
        boolean ret = false;

        if (isInternalApp()) {
            ret = InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("monitorReportUpload");
        }

        return ret;
    }
    
    private void handleServiceFactoryException(ServiceFactoryException sfe)
            throws RemoteException {
        DisplayUtil
                .displayError("Major application error, please notify system administrator");
        logger.error(sfe.getMessage(), sfe);
        throw new RemoteException(sfe.getMessage());

    }
    
    public String[] getCacheViewIds() {
        return cacheViewIDs.toArray(new String[0]);
    }

    public void restoreCache() {
    }

    public void clearCache() {
    }
    
    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
        
        logger.debug("ReadObject called for " + this.getClass().getName());
    }
    
    public synchronized boolean firstButtonClick() {

    	boolean ret = false;
        
        if (!isButtonClicked()) {
            buttonClicked = true;
            ret = true;
        }
        return ret;
    }
    
    public void clearButtonClicked() {
        buttonClicked = false;
    }
    
    public final synchronized boolean isButtonClicked() {
    	return buttonClicked;
    }
    
    /** BoundedRangeModel methods for jsp af:progressIndicator progress bar. */
    public synchronized long getMaximum() {
        return _maximumBoundedValue;
    }

    /** BoundedRangeModel methods for jsp af:progressIndicator progress bar. */
    public synchronized void setMaximum(long maximum) {
        _maximumBoundedValue = maximum;
    }

    /** BoundedRangeModel methods for jsp af:progressIndicator progress bar. */
    public synchronized long getValue() {
        return _currentBoundedValue;
    }

    /** BoundedRangeModel methods for jsp af:progressIndicator progress bar. */
    public synchronized void setValue(long currentValue) {
        _currentBoundedValue = currentValue;
    }
    
    // are strings the same--includes cases where string may be null (null and zero characters is the same).
    public static boolean stringsSame(String s1, String s2) {
        if(exists(s1)) {
            if(!exists(s2)) return false;
            else return s1.equals(s2);
        } else {
            if(exists(s2)) return false;
            else return true;
        }
    }

    public static boolean exists(String s) {
        if(s != null && s.trim().length() > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * ********************************************************************************
     * 					CETA BOs
     * ********************************************************************************
     */

/**    
    protected final StackTestService stackTestBO() throws RemoteException {
        StackTestService service = null;

        try {
            service = ServiceFactory.getInstance().getStackTestService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final FullComplianceEvalService fullComplianceEvalBO() throws RemoteException {
        FullComplianceEvalService service = null;

        try {
            service = ServiceFactory.getInstance().getFullComplianceEvalService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final EnforcementService enforcementBO() throws RemoteException {
    	EnforcementService service = null;

        try {
            service = ServiceFactory.getInstance().getEnforcementService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final FacilityHistoryService facilityHistoryBO() throws RemoteException {
    	FacilityHistoryService service = null;

        try {
            service = ServiceFactory.getInstance().getFacilityHistoryService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final ComplaintsService complaintsBO() throws RemoteException {
    	ComplaintsService service = null;

        try {
            service = ServiceFactory.getInstance().getComplaintsService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
    protected final GDFService gdfBO() throws RemoteException {
    	GDFService service = null;

        try {
            service = ServiceFactory.getInstance().getGDFService();
        } catch (ServiceFactoryException sfe) {
            handleServiceFactoryException(sfe);
        }

        return service;
    }
    
**/    
    
    public boolean isPermitConditionEditor() {
    	boolean isPermitConditionEditor = false;
    	
    	if (isInternalApp()) {
    		UserAttributes attrs = InfrastructureDefs.getCurrentUserAttrs();
    		if (attrs != null) {
    			isPermitConditionEditor = attrs.isPermitConditionsEditable();
    		}
    	}
        return isPermitConditionEditor;
    }
}
