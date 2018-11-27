package us.oh.state.epa.stars2.app.permit;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.ceta.FceDetail;
import us.oh.state.epa.stars2.app.ceta.StackTestDetail;
import us.oh.state.epa.stars2.app.compliance.ComplianceReports;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.PermitConditionService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSupersession;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.def.ComplianceTrackingEventTypeDef;
import us.wy.state.deq.impact.def.PermitConditionSupersedenceStatusDef;

public class PermitConditionDetail extends TaskBase {

	private static final long serialVersionUID = -468472899438613944L;
	
    public static final String COMPLIANCE_STATUS_HISTORY_URI = "../permits/permitConditionsComplianceStatusHistory.jsf";
    public static final int COMPLIANCE_STATUS_HISTORY_WINDOW_WIDTH = 1050;
    public static final int COMPLIANCE_STATUS_HISTORY_WINDOW_HEIGHT = 350;
    
    public static final String PERMIT_CONDITION_DETAIL_URI = "../permits/permitConditionDetail.jsf";
    public static final String PERMIT_CONDITION_DETAIL_DIALOG = "dialog:permitConditionDetail";
    public static final int PERMIT_CONDITION_WINDOW_WIDTH = 1065;
    public static final int PERMIT_CONDITION_WINDOW_HEIGHT = 900;
    
    public static final String PERMIT_CONDITION_SUPERSEDENCE_STATUS_URI = "../permits/permitConditionSupersedenceStatus.jsf";
    public static final int PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_WIDTH = 1050;
    public static final int PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_HEIGHT = 300;
    
    public static final int PERMIT_CONDITION_TEXT_TRUNCATE_AT = 200;
    public final static String PERMIT_CONDITION_NO_EU_SELECTED_WARNING = "Facility wide is 'No', but no EUs have been selected. Click 'Save' again to continue.";

    private PermitConditionService permitConditionService;
    private PermitService permitService;
    private FacilityService facilityService;
    private StackTestService stackTestService;
    private ComplianceReportService complianceReportService;
    private FullComplianceEvalService fullComplianceEvalService;
	   
    // Permit Condition
    private PermitCondition modifyPermitCondition;
    private boolean newPermitCondition = false;
    private boolean noEUsSelectedWarningDisplayed;
    private boolean editMode1 = false;
    private Permit permit;
    private List<Integer> corrEuIdsToAssociate = new ArrayList<Integer>();
    private List<Integer> corrEuIdsToDisassociate = new ArrayList<Integer>();
    
    // compliance status event
	private ComplianceStatusEvent modifyComplianceStatusEvent;
	private boolean newComplianceStatusEvent = false;
	private Integer complianceReferenceId;
	private String complianceEventTypeCd;
	private boolean editComplianceStatus = false;
	
	// supersession
	private PermitConditionSupersession conditionSupersession;
	private boolean newPermitConditionSupersession = false;
	private boolean editPermitConditionSupersession = false;
	private ArrayList<SelectItem> possibleSupersededConditions;
	
	// TableSorters
	private TableSorter PermitConditionStatusWrapper;
	private TableSorter ComplianceStatusEventsWrapper;
	private TableSorter SuperSessionWrapper;
	private TableSorter PermitConditionStatusWrapper2; // used by permitConditionSupersedenceList.jsp only
	
	// for permit condition search
	private Integer permitConditionId;
	private boolean fromFacilityPermitConditionSearch = false;
	private PermitConditionSearchLineItem permitConditionSearchLineItem;
	private String facilityId;
	
	// for readonly permit condition detail
	private PermitCondition readOnlyPermitCondition;
	
	
	/* The list of modeless dialogs to be closed before opening any one of them.
	 * This is to ensure only of these dialogs can be open at a given point.
	 * Trying to open one of these dialogs should automatically close other
	 * dialogs that are open.
	 */
	private final String[] modelessDialogUris = { 
							PERMIT_CONDITION_DETAIL_URI,
							PERMIT_CONDITION_SUPERSEDENCE_STATUS_URI,
							COMPLIANCE_STATUS_HISTORY_URI
	};

	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findOutcome(String url, String ret) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getExternalId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExternalId(Integer externalId) {
		// TODO Auto-generated method stub
	}
	
	public PermitConditionService getPermitConditionService() {
		return permitConditionService;
	}

	public void setPermitConditionService(PermitConditionService permitConditionService) {
		this.permitConditionService = permitConditionService;
	}

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public ComplianceReportService getComplianceReportService() {
		return complianceReportService;
	}

	public void setComplianceReportService(ComplianceReportService complianceReportService) {
		this.complianceReportService = complianceReportService;
	}

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public PermitCondition getModifyPermitCondition() {
		return modifyPermitCondition;
	}

	public void setModifyPermitCondition(PermitCondition modifyPermitCondition) {
		this.modifyPermitCondition = modifyPermitCondition;
	}

	public boolean isNewPermitCondition() {
		return newPermitCondition;
	}

	public void setNewPermitCondition(boolean newPermitCondition) {
		this.newPermitCondition = newPermitCondition;
	}

	public boolean isNoEUsSelectedWarningDisplayed() {
		return noEUsSelectedWarningDisplayed;
	}

	public void setNoEUsSelectedWarningDisplayed(boolean noEUsSelectedWarningDisplayed) {
		this.noEUsSelectedWarningDisplayed = noEUsSelectedWarningDisplayed;
	}

	public boolean isEditMode1() {
		return editMode1;
	}

	public void setEditMode1(boolean editMode1) {
		this.editMode1 = editMode1;
	}

	public Permit getPermit() {
		return permit;
	}

	public void setPermit(Permit permit) {
		this.permit = permit;
	}

	public List<Integer> getCorrEuIdsToAssociate() {
		return corrEuIdsToAssociate;
	}

	public void setCorrEuIdsToAssociate(List<Integer> corrEuIdsToAssociate) {
		this.corrEuIdsToAssociate = corrEuIdsToAssociate;
	}

	public List<Integer> getCorrEuIdsToDisassociate() {
		return corrEuIdsToDisassociate;
	}

	public void setCorrEuIdsToDisassociate(List<Integer> corrEuIdsToDisassociate) {
		this.corrEuIdsToDisassociate = corrEuIdsToDisassociate;
	}

	public ComplianceStatusEvent getModifyComplianceStatusEvent() {
		return modifyComplianceStatusEvent;
	}

	public void setModifyComplianceStatusEvent(ComplianceStatusEvent modifyComplianceStatusEvent) {
		this.modifyComplianceStatusEvent = modifyComplianceStatusEvent;
	}

	public boolean isNewComplianceStatusEvent() {
		return newComplianceStatusEvent;
	}

	public void setNewComplianceStatusEvent(boolean newComplianceStatusEvent) {
		this.newComplianceStatusEvent = newComplianceStatusEvent;
	}

	public Integer getComplianceReferenceId() {
		return complianceReferenceId;
	}

	public void setComplianceReferenceId(Integer complianceReferenceId) {
		this.complianceReferenceId = complianceReferenceId;
	}

	public String getComplianceEventTypeCd() {
		return complianceEventTypeCd;
	}

	public void setComplianceEventTypeCd(String complianceEventTypeCd) {
		this.complianceEventTypeCd = complianceEventTypeCd;
	}

	public boolean isEditComplianceStatus() {
		return editComplianceStatus;
	}

	public void setEditComplianceStatus(boolean editComplianceStatus) {
		this.editComplianceStatus = editComplianceStatus;
	}

	public PermitConditionSupersession getConditionSupersession() {
		return conditionSupersession;
	}

	public void setConditionSupersession(PermitConditionSupersession conditionSupersession) {
		this.conditionSupersession = conditionSupersession;
	}

	public boolean isNewPermitConditionSupersession() {
		return newPermitConditionSupersession;
	}

	public void setNewPermitConditionSupersession(boolean newPermitConditionSupersession) {
		this.newPermitConditionSupersession = newPermitConditionSupersession;
	}

	public boolean isEditPermitConditionSupersession() {
		return editPermitConditionSupersession;
	}

	public void setEditPermitConditionSupersession(boolean editPermitConditionSupersession) {
		this.editPermitConditionSupersession = editPermitConditionSupersession;
	}

	public ArrayList<SelectItem> getPossibleSupersededConditions() {
		return possibleSupersededConditions;
	}

	public void setPossibleSupersededConditions(ArrayList<SelectItem> possibleSupersededConditions) {
		this.possibleSupersededConditions = possibleSupersededConditions;
	}

	public TableSorter getPermitConditionStatusWrapper() {
		return PermitConditionStatusWrapper;
	}

	public void setPermitConditionStatusWrapper(TableSorter permitConditionStatusWrapper) {
		PermitConditionStatusWrapper = permitConditionStatusWrapper;
	}

	public TableSorter getComplianceStatusEventsWrapper() {
		return ComplianceStatusEventsWrapper;
	}

	public void setComplianceStatusEventsWrapper(TableSorter complianceStatusEventsWrapper) {
		ComplianceStatusEventsWrapper = complianceStatusEventsWrapper;
	}
	
	public TableSorter getSuperSessionWrapper() {
		return SuperSessionWrapper;
	}

	public void setSuperSessionWrapper(TableSorter superSessionWrapper) {
		SuperSessionWrapper = superSessionWrapper;
	}
	
	public TableSorter getPermitConditionStatusWrapper2() {
		return PermitConditionStatusWrapper2;
	}

	public void setPermitConditionStatusWrapper2(TableSorter permitConditionStatusWrapper2) {
		PermitConditionStatusWrapper2 = permitConditionStatusWrapper2;
	}

	public Integer getPermitConditionId() {
		return permitConditionId;
	}

	public void setPermitConditionId(Integer permitConditionId) {
		this.permitConditionId = permitConditionId;
	}

	public boolean isFromFacilityPermitConditionSearch() {
		return fromFacilityPermitConditionSearch;
	}
	

	public void setFromFacilityPermitConditionSearch(boolean fromFacilityPermitConditionSearch) {
		this.fromFacilityPermitConditionSearch = fromFacilityPermitConditionSearch;
	}
	
	public PermitConditionSearchLineItem getPermitConditionSearchLineItem() {
		return permitConditionSearchLineItem;
	}

	public void setPermitConditionSearchLineItem(PermitConditionSearchLineItem permitConditionSearchLineItem) {
		this.permitConditionSearchLineItem = permitConditionSearchLineItem;
	}
	
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public PermitCondition getReadOnlyPermitCondition() {
		return readOnlyPermitCondition;
	}

	public void setReadOnlyPermitCondition(PermitCondition readOnlyPermitCondition) {
		this.readOnlyPermitCondition = readOnlyPermitCondition;
	}
	
	public Integer getCurrentUserID() {
		return InfrastructureDefs.getCurrentUserId();
	}
	
	public final int getPermitConditionWindowWidth() {
		return PERMIT_CONDITION_WINDOW_WIDTH;
	}
	
	public final int getPermitConditionWindowHeight() {
		return PERMIT_CONDITION_WINDOW_HEIGHT;
	}
	
	public int getPermitConditionTextTruncateAt() {
		return PERMIT_CONDITION_TEXT_TRUNCATE_AT;
	}
	
	public String getTinyMCEMode() {
		return !isEditMode1() ? CommonConst.TINYMCE_MODE_READONLY : CommonConst.TINYMCE_MODE_DESIGN;
	}

	public void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public final void closeModelessDialog() {
		FacesUtil.returnFromDialog();
	}
	
    /*  PERMIT CONDTION START  */
    
	public final void startToAddPermitCondition() {

		this.modifyPermitCondition = new PermitCondition();
		this.modifyPermitCondition.setPermitId(permit.getPermitID());
		this.modifyPermitCondition.setLastUpdatedById(getCurrentUserID());
		this.modifyPermitCondition.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
		
		this.corrEuIdsToAssociate = new ArrayList<Integer>();
		this.corrEuIdsToDisassociate = new ArrayList<Integer>();
		
		this.ComplianceStatusEventsWrapper = new TableSorter();
		this.PermitConditionStatusWrapper = new TableSorter();
		this.SuperSessionWrapper = new TableSorter();
		
		this.noEUsSelectedWarningDisplayed = false;
		
		this.newPermitCondition = true;
		
		this.editMode1 = true;

		FacesUtil.startModelessDialog(PERMIT_CONDITION_DETAIL_URI, PERMIT_CONDITION_WINDOW_HEIGHT,
				PERMIT_CONDITION_WINDOW_WIDTH);
	}
	
	public final void startToEditPermitCondition() {

		if(null != this.modifyPermitCondition) {
			closeOpenModelessDialogs();
			this.modifyPermitCondition.setLastUpdatedById(getCurrentUserID());
			this.modifyPermitCondition.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
	
			this.corrEuIdsToAssociate = new ArrayList<Integer>();
			this.corrEuIdsToDisassociate = new ArrayList<Integer>();
	
			this.ComplianceStatusEventsWrapper = new TableSorter();
			this.ComplianceStatusEventsWrapper.setWrappedData(this.modifyPermitCondition.getComplianceStatusEvents());
	
			this.PermitConditionStatusWrapper = new TableSorter();
			this.PermitConditionStatusWrapper.setWrappedData(this.modifyPermitCondition.getSupersedesThis());
			
			this.SuperSessionWrapper = new TableSorter();
			this.SuperSessionWrapper.setWrappedData(this.modifyPermitCondition.getSupersededByThis());
	
			this.noEUsSelectedWarningDisplayed = false;
	
			this.fromFacilityPermitConditionSearch = false;
	
			this.newPermitCondition = false;
			
			FacesUtil.startModelessDialog(PERMIT_CONDITION_DETAIL_URI, PERMIT_CONDITION_WINDOW_HEIGHT,
					PERMIT_CONDITION_WINDOW_WIDTH);
		} else {
			// should never happen
			DisplayUtil.displayError("Cannot load selected permit condition");
			refreshPermitConditions();
		}
	}
	
	public final void editPermitCondition() {
		this.editMode1 = true;
	}
	
	public void deletePermitCondition(ReturnEvent re) {

		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			clearButtonClicked();
			return;
		}

		DisplayUtil.clearQueuedMessages();

		try {
			getPermitConditionService().removePermitCondition(this.modifyPermitCondition);

			DisplayUtil.displayInfo("Delete Permit Condition Success");

		} catch (DAOException e) {
			logger.error("Remove Permit Condition failed", e);
			DisplayUtil.displayError("Remove Permit Condition failed");

		} finally {
			clearButtonClicked();
			closePermitConditionDialog();
		}
	}
	
	public final void savePermitCondition() {

		DisplayUtil.clearQueuedMessages();

		if (permitConditionValidated()) {
			if (this.newPermitCondition) {
				createPermitCondition();
				this.newPermitCondition = false;
			} else {
				updatePermitCondition();
			}

			closePermitConditionDialog();
		}
	}

	public void cancelPermitCondition() {
		closePermitConditionDialog();
	}
	
	public void closePermitConditionDialog() {
		if(!isFromFacilityPermitConditionSearch()) {
			// refresh permit conditions datagrid on the 3rd level menu
			refreshPermitConditions();  
		}
		resetPermitConditionDialogData();
		closeModelessDialog();
	}
	
	public final boolean getPermitConditionEditAllowed() {

		if (isFromFacilityPermitConditionSearch()) {
			return false;
		}

		if (isReadOnlyUser()) {
			return false;
		}

		if (isStars2Admin()) {
			return true;
		}

		if (isPermitConditionEditor()) {
			return true;
		}

		if (!isPermitInDoneStatus()) {
			return true;
		}
		 
		return false;
	}
	
	public boolean isAllowedToDeletePermitCondition() {
		if (this.modifyPermitCondition.getSupersededByThis().isEmpty() && this.modifyPermitCondition.getSupersedesThis().isEmpty())
			return true;
		else
			return false;
	}
	
	public void refreshPermitConditionDetailWithThis() {
		if (null != this.permitConditionId) {
			try {
				resetPermitConditionDialogData();
				loadPermitCondition();
				this.newPermitCondition = false;
				this.fromFacilityPermitConditionSearch = true; // disable any sort of editing
				this.editMode1 = false;
			} catch (RemoteException re) {
				DisplayUtil.displayError("Failed to retrieve permit condition");
				handleException(re);
				logger.error(re.getMessage(), re);
			}
		}
	}
	
    private boolean createPermitCondition() {
		boolean created = false;
		try {
			 getPermitConditionService().createPermitCondition(this.getModifyPermitCondition(), corrEuIdsToAssociate,
					corrEuIdsToDisassociate);
			 created = true;
			DisplayUtil.displayInfo("Create Permit Condition Success");
		} catch (DAOException daoe) {
			logger.error("Create Permit Condition Failed", daoe);
			DisplayUtil.displayError("Create Permit Condition Failed");
			handleException(daoe);
		}
		
		return created;
	}
    
    private boolean updatePermitCondition() {
    	boolean updated = false;
		try {
			getPermitConditionService().modifyPermitCondition(this.getModifyPermitCondition(),
					corrEuIdsToAssociate, corrEuIdsToDisassociate);
			updated = true;
			DisplayUtil.displayInfo("Modify Permit Condition Success");
		} catch (RemoteException ex) {
			logger.error("Modify Permit Condition Failed", ex);
			DisplayUtil.displayError("Modify Permit Condition Failed");
			handleException(ex);
		}
		
		return updated;
	}
    
	private void refreshPermitConditions() {
		PermitDetail permitDetail = (PermitDetail) FacesUtil.getManagedBean("permitDetail");
		if(null != permitDetail) {
			permitDetail.refreshPermitConditionList();
		}
	}
	
	private boolean isPermitInDoneStatus() {
		PermitDetail permitDetail = (PermitDetail) FacesUtil.getManagedBean("permitDetail");
		if(null != permitDetail) {
			return permitDetail.isPermitInDoneStatus();
		} else {
			return false;
		}
	}
	
	private void resetPermitConditionDialogData() {
		this.newPermitCondition = false;
		this.editMode1 = false;
		this.fromFacilityPermitConditionSearch = false;
		this.modifyPermitCondition = null;
		this.ComplianceStatusEventsWrapper = null;
		this.PermitConditionStatusWrapper = null;
		this.SuperSessionWrapper = null;
		this.corrEuIdsToAssociate = null;
		this.corrEuIdsToDisassociate = null;
		this.facilityId = null;
	}
	
	private boolean permitConditionValidated() {
		boolean isValid = false;
		PermitCondition permitCondition = this.getModifyPermitCondition();

		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>(Arrays.asList(permitCondition.validate()));

		if (!Utility.isNullOrEmpty(permitCondition.getPermitConditionNumber())) {
			try {
				if (getPermitConditionService().isDuplicatePermitConditionNumber(permitCondition)) {
					String dupErrorMessage = "The Permit Condition Number "
							+ this.getModifyPermitCondition().getPermitConditionNumber()
							+ " already exists for this permit.";

					ValidationMessage dupConditionValMsg = new ValidationMessage("permitConditionNumber",
							dupErrorMessage, ValidationMessage.Severity.ERROR, null);
					valMsgs.add(dupConditionValMsg);
				}
				if (StringUtils.equalsIgnoreCase(permitCondition.getFacilityWide(), "N")
						&& corrEuIdsToAssociate.isEmpty() && !isNoEUsSelectedWarningDisplayed()) {
					if ((!permitCondition.getAssociatedCorrEuIds().isEmpty()
							&& permitCondition.getAssociatedCorrEuIds().size() == corrEuIdsToDisassociate.size())
							|| (permitCondition.getAssociatedCorrEuIds().isEmpty())) {
						ValidationMessage noEUsSelectedWarning = new ValidationMessage("associatedEUs",
								PERMIT_CONDITION_NO_EU_SELECTED_WARNING, ValidationMessage.Severity.WARNING, null);
						valMsgs.add(noEUsSelectedWarning);
						setNoEUsSelectedWarningDisplayed(true);
					}
				}
			} catch (DAOException e) {
				logger.error("permitConditionValidated failed", e);
				DisplayUtil.displayError("Permit Condition Validation failed");
			}
		}

		if (valMsgs.isEmpty()) {
			isValid = true;
		} else {
			displayValidationMessages("", valMsgs.toArray(new ValidationMessage[0]));
		}

		return isValid;
	}
	
	// shuttle support for associating emissions units
	/**
	 * Returns a list of correlated eu ids of valid emissions units in the
	 * facility profile associated with this condition.
	 * 
	 */
	public List<SelectItem> getAvailableEUs() {
		List<SelectItem> availableEUs = new ArrayList<SelectItem>();
		if (null != this.permit) {
			try {

				EmissionUnit[] feus = getFacilityService().retrieveFacilityEmissionUnits(this.permit.getFpId());
				if (null != feus) {
					for (EmissionUnit fpEU : feus) {
						if (!fpEU.getOperatingStatusCd().equals(EuOperatingStatusDef.IV)) {
							availableEUs.add(new SelectItem(fpEU.getCorrEpaEmuId(), fpEU.getEpaEmuId()));
						}
					}
				}

				Collections.sort(availableEUs, new Comparator<SelectItem>() {
					@Override
					public int compare(SelectItem si1, SelectItem si2) {
						return si1.getLabel().compareToIgnoreCase(si2.getLabel());
					};
				});

			} catch (RemoteException re) {
				DisplayUtil.displayError("Failed to retrieve available emissions unit list");
				handleException(re);
			}
		}

		return availableEUs;
	}

	public List<Integer> getAssociatedEUs() {
		List<Integer> fpEuIds = new ArrayList<Integer>();

		fpEuIds.addAll(getModifyPermitCondition().getAssociatedCorrEuIds());

		if (!corrEuIdsToAssociate.isEmpty()) {
			fpEuIds.addAll(corrEuIdsToAssociate);
		}

		if (!corrEuIdsToDisassociate.isEmpty()) {
			fpEuIds.removeAll(corrEuIdsToDisassociate);

		}
		return fpEuIds;
	}

	public void setAssociatedEUs(List<Integer> associatedEUs) {
		corrEuIdsToAssociate.clear();
		corrEuIdsToDisassociate.clear();

		List<Integer> associatedFpEuIds = getModifyPermitCondition().getAssociatedCorrEuIds();

		// determine if any previously associated EUs were now disassociated
		for (Integer emuId : associatedFpEuIds) {
			if (!associatedEUs.contains(emuId)) {
				corrEuIdsToDisassociate.add(emuId);
			}
		}

		// determine any newly associated EUs
		for (Integer emuId : associatedEUs) {
			if (!associatedFpEuIds.contains(emuId)) {
				corrEuIdsToAssociate.add(emuId);
			}
		}
	}
	
	private void loadPermitCondition() throws RemoteException {
		this.modifyPermitCondition = getPermitConditionService().retrievePermitConditionById(this.permitConditionId);

		if (null != this.modifyPermitCondition) {
			PermitInfo permitInfo = getPermitService().retrievePermit(this.modifyPermitCondition.getPermitId());
			this.permit = permitInfo.getPermit();
			
			this.facilityId = this.permit.getFacilityId();

			this.corrEuIdsToAssociate = new ArrayList<Integer>();
			this.corrEuIdsToDisassociate = new ArrayList<Integer>();

			this.ComplianceStatusEventsWrapper = new TableSorter();
			this.ComplianceStatusEventsWrapper.setWrappedData(this.modifyPermitCondition.getComplianceStatusEvents());

			this.PermitConditionStatusWrapper = new TableSorter();
			this.PermitConditionStatusWrapper.setWrappedData(this.modifyPermitCondition.getSupersedesThis());

			this.SuperSessionWrapper = new TableSorter();
			this.SuperSessionWrapper.setWrappedData(this.modifyPermitCondition.getSupersededByThis());

		} else {
			throw new DAOException("No matching permit condition found");
		}
	}

    
    /* PERMIT CONDITION END */
	
	
	/*  COMPLIANCE EVENT START  */
	
	public final String startToAddPermitConditionComplainceStatusEvent() {
		
		this.modifyComplianceStatusEvent = new ComplianceStatusEvent();
		this.modifyComplianceStatusEvent.setPermitConditionId(this.modifyPermitCondition.getPermitConditionId());
		this.modifyComplianceStatusEvent.setLastUpdatedById(getCurrentUserID());
		this.modifyComplianceStatusEvent.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
		
		this.editComplianceStatus = true;
		
		this.newComplianceStatusEvent = true;
		
		return "dialog:permitConditionComplianceStatus";
	}
	
	public final String startToEditPermitConditionComplainceStatusEvent() {
		String navigateTo = null;

		if (null != this.modifyComplianceStatusEvent) {
			

			this.newComplianceStatusEvent = false;

			navigateTo = "dialog:permitConditionComplianceStatus";
		} else {
			// should never happen
			DisplayUtil.displayError("Cannot load selected compliance status event");
			refreshComplianceStatusEvents();
		}

		return navigateTo;
	}
	
	public final void editComplianceStatusEvent() {
		this.editComplianceStatus = true;
	}
	
	public void deleteComplianceStatusEvent(ReturnEvent re) {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			clearButtonClicked();
			return;
		}
		
		DisplayUtil.clearQueuedMessages();

		try {
			getPermitConditionService().removeComplianceStatusEvent(getModifyComplianceStatusEvent().getComplianceStatusId());
			DisplayUtil.displayInfo("Delete Compliance Status Event Success");
		} catch (DAOException daoe) {
			logger.error("Remove Compliance Status Event failed", daoe);
			DisplayUtil.displayError("Remove Compliance Status Event failed");
			handleException(daoe);
		} finally {
			clearButtonClicked();
			closeComplianceStatusEventDialog();
		}
	}
	
	public void saveComplianceStatusEvent() {
		DisplayUtil.clearQueuedMessages();

		if (complianceStatusValidated()) {
			if (isNewComplianceStatusEvent()) {
				createComplianceStatusEvent();
				this.newComplianceStatusEvent = false;
			} else {
				this.modifyComplianceStatusEvent.setLastUpdatedById(getCurrentUserID());
				this.modifyComplianceStatusEvent.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));
				updateComplianceStatusEvent();
			}
			
			closeComplianceStatusEventDialog();
		}
	}

	public void cancelComplianceStatusDialog() {
		closeComplianceStatusEventDialog();
	}
	
	public void closeComplianceStatusEventDialog() {
		resetComplianceSatusEventDialogData();
		refreshComplianceStatusEvents();
		closeDialog();
	}
	
	public void closeComplianceEventHistoryDialog() {
//		this.modifyPermitCondition = null;
		this.ComplianceStatusEventsWrapper = null;
		this.fromFacilityPermitConditionSearch = false;
		this.facilityId = null;
		closeModelessDialog();
	}
	
	public String showAssociatedComplianceEventType() {
		String ret = null;
		if (null != getComplianceEventTypeCd() && null != getComplianceReferenceId()) {
			if (StringUtils.equalsIgnoreCase(getComplianceEventTypeCd(),
					ComplianceTrackingEventTypeDef.COMPLIANCE_REPORT)) {
				logger.debug("Navigating to compliance report id " + getComplianceReferenceId());
				ComplianceReports cr = (ComplianceReports) FacesUtil.getManagedBean("complianceReport");
				cr.setReportId(getComplianceReferenceId());
				cr.setFromTODOList(false);
				ret = cr.viewDetail();
			} else if (StringUtils.equalsIgnoreCase(getComplianceEventTypeCd(),
					ComplianceTrackingEventTypeDef.STACK_TEST)) {

				logger.debug("Navigating to stack test id " + getComplianceReferenceId());
				StackTestDetail st = (StackTestDetail) FacesUtil.getManagedBean("stackTestDetail");
				st.setId(getComplianceReferenceId());
				ret = st.viewDetail();
			} else if (StringUtils.equalsIgnoreCase(getComplianceEventTypeCd(),
					ComplianceTrackingEventTypeDef.INSPECTION)) {
				logger.debug("Navigating to Inspection id " + getComplianceReferenceId());
				FceDetail ins = (FceDetail) FacesUtil.getManagedBean("fceDetail");
				ins.setFceId(getComplianceReferenceId());
				ret = ins.viewDetail();
			}
		}
		return ret;
	}
	
	public final void displayPermitConditionComplianceHistory() {
		if (null != this.modifyPermitCondition) {
			closeOpenModelessDialogs();
			this.ComplianceStatusEventsWrapper = new TableSorter();
			this.ComplianceStatusEventsWrapper.setWrappedData(this.modifyPermitCondition.getComplianceStatusEvents());

			FacesUtil.startModelessDialog(COMPLIANCE_STATUS_HISTORY_URI, COMPLIANCE_STATUS_HISTORY_WINDOW_HEIGHT,
					COMPLIANCE_STATUS_HISTORY_WINDOW_WIDTH);
		} else {
			// should never happen
			DisplayUtil.displayError("Cannot display Compliance Status History");
		}
	}
	
	public Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	public String getReferenceWarning() {
		String warning = "";
		if(StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(), ComplianceTrackingEventTypeDef.STACK_TEST)) {
			warning = "No Stack tests were found for this facility";
		} else if(StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(), ComplianceTrackingEventTypeDef.COMPLIANCE_REPORT)) {
			warning = "No Compliance reports were found for this facility";
		} else if(StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(), ComplianceTrackingEventTypeDef.INSPECTION)) {
			warning = "No Inspections were found for this facility";
		}
		
		return warning;
	}
	
	public void eventTypeCdChanged(ValueChangeEvent ve) {
		getModifyComplianceStatusEvent().setInspectionReference(null);
		getModifyComplianceStatusEvent().setComplianceReportReference(null);
		getModifyComplianceStatusEvent().setStackTestReference(null);
	}
	
	public boolean getComplianceEventReferenceApplicable() {
		return (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
				ComplianceTrackingEventTypeDef.STACK_TEST)
				|| StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
						ComplianceTrackingEventTypeDef.COMPLIANCE_REPORT)
				|| StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
						ComplianceTrackingEventTypeDef.INSPECTION));
	}
	
	public boolean isComplianceReportEventType() {
		if (getModifyComplianceStatusEvent() == null)
			return false;
		return (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
				ComplianceTrackingEventTypeDef.COMPLIANCE_REPORT));
	}

	public boolean isInspectionEventType() {
		if (getModifyComplianceStatusEvent() == null)
			return false;
		return (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
				ComplianceTrackingEventTypeDef.INSPECTION));
	}

	public boolean isStackTestEventType() {
		if (getModifyComplianceStatusEvent() == null)
			return false;
		return (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
				ComplianceTrackingEventTypeDef.STACK_TEST));
	}

	public List<SelectItem> getComplianceEventReferenceIdsForFacility() {
		List<SelectItem> complianceEventReferenceIds = new ArrayList<SelectItem>();
		if (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
				ComplianceTrackingEventTypeDef.STACK_TEST)) {
			complianceEventReferenceIds = getStackTestIdsForFacility();
		} else if (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
				ComplianceTrackingEventTypeDef.COMPLIANCE_REPORT)) {
			complianceEventReferenceIds = getComplianceReportIdsForFacility();
		} else if (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(),
				ComplianceTrackingEventTypeDef.INSPECTION)) {
			complianceEventReferenceIds = getInspectionIdsForFacility();
		}

		Collections.sort(complianceEventReferenceIds, new Comparator<SelectItem>() {
			@Override
			public int compare(SelectItem si1, SelectItem si2) {
				return si2.getLabel().compareToIgnoreCase(si1.getLabel());
			};
		});

		return complianceEventReferenceIds;
	}

	public List<SelectItem> getStackTestIdsForFacility() {
		List<SelectItem> stackTestIds = new ArrayList<SelectItem>();
		try {
			String facilityId = isFromFacilityPermitConditionSearch() ? this.facilityId : this.permit.getFacilityId();
			StackTest[] stackTests = getStackTestService().searchStackTests(facilityId,
					EmissionsTestStateDef.SUBMITTED);
			for (StackTest stackTest : stackTests) {
				stackTestIds.add(new SelectItem(stackTest.getId(), stackTest.getStckId()));
			}
		} catch (Exception e) {
			logger.error("Exception while retrieving stack test ids", e);
		}
		return stackTestIds;
	}

	public List<SelectItem> getComplianceReportIdsForFacility() {
		List<SelectItem> complianceReportIds = new ArrayList<SelectItem>();
		try {
			String facilityId = isFromFacilityPermitConditionSearch() ? this.facilityId : this.permit.getFacilityId();
			ComplianceReportList[] complianceReports = getComplianceReportService()
					.searchComplianceReportByFacilityAndStatus(facilityId,
							ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED);
			for (ComplianceReportList complianceReport : complianceReports) {
				complianceReportIds
						.add(new SelectItem(complianceReport.getReportId(), complianceReport.getReportCRPTId()));
			}
		} catch (Exception e) {
			logger.error("Exception while retrieving compliance report ids", e);
		}
		return complianceReportIds;
	}

	public List<SelectItem> getInspectionIdsForFacility() {
		List<SelectItem> inspectionIds = new ArrayList<SelectItem>();
		try {
			String facilityId = isFromFacilityPermitConditionSearch() ? this.facilityId : this.permit.getFacilityId();
			FullComplianceEval[] fceList = getFullComplianceEvalService()
					.retrieveFceBySearch(facilityId);

			for (FullComplianceEval fce : fceList) {
				inspectionIds.add(new SelectItem(fce.getId(), fce.getInspId()));
			}
		} catch (RemoteException e) {
			logger.error("Exception while retrieving compliance report ids", e);
			handleException(e);
		}
		return inspectionIds;
	}
	
	public int getComplianceEventReferenceIdsForFacilitySize() {
		return getComplianceEventReferenceIdsForFacility().size();
	}
	
	private boolean createComplianceStatusEvent() {
		boolean created = false;
		try {
			getPermitConditionService().createComplianceStatusEvent(getModifyComplianceStatusEvent());
			created = true;
			DisplayUtil.displayInfo("Create Compliance Status Event Success");
		} catch (DAOException daoe) {
			logger.error("Create Compliance Status Event Failed", daoe);
			DisplayUtil.displayError("Create Compliance Status Event Failed");
			handleException(daoe);
		}
		
		return created;
	}
	
	private boolean updateComplianceStatusEvent() {
		boolean updated = false;
		try {
			getPermitConditionService().modifyComplianceStatusEvent(getModifyComplianceStatusEvent());
			updated = true;
			DisplayUtil.displayInfo("Modify Compliance Status Event Success");
		} catch (DAOException daoe) {
			logger.error("Modify Compliance Status Event Failed", daoe);
			DisplayUtil.displayError("Modify Compliance Status Event Failed");
			handleException(daoe);
		}
		
		return updated;
	}
	
	private void resetComplianceSatusEventDialogData() {
		this.modifyComplianceStatusEvent = null;
		this.editComplianceStatus = false;
		this.newComplianceStatusEvent = false;
		this.complianceReferenceId = null;
		this.complianceEventTypeCd = null;
	}
	
	private void refreshComplianceStatusEvents() {
		if (null != this.modifyPermitCondition) {
			this.ComplianceStatusEventsWrapper = new TableSorter();

			try {
				this.modifyPermitCondition.setComplianceStatusEvents(getPermitConditionService()
						.retrieveComplianceStatusEventList(this.modifyPermitCondition.getPermitConditionId()));
				this.ComplianceStatusEventsWrapper
						.setWrappedData(this.modifyPermitCondition.getComplianceStatusEvents());
			} catch (DAOException daoe) {
				DisplayUtil.displayError("Failed to retrieve Compliance Status Events list");
				logger.error("failed to get ComplianceStatusEvents LIST", daoe);
				handleException(daoe);
			}
		}
	}	
	
	private boolean complianceStatusValidated() {
		boolean isValid = true;
		
		if (this.getModifyComplianceStatusEvent().getEventDate() == null
				|| Utility.isNullOrEmpty(this.getModifyComplianceStatusEvent().getEventDate().toString())) {
			DisplayUtil.displayError("ERROR: Attribute Event Date is not set.");
			isValid = false;
		}
		
		if (this.getModifyComplianceStatusEvent().getEventTypeCd() == null
				|| Utility.isNullOrEmpty(this.getModifyComplianceStatusEvent().getEventTypeCd().toString())) {
			DisplayUtil.displayError("ERROR: Attribute Event Type must have a value.");
			isValid = false;
		}
		if (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(), ComplianceTrackingEventTypeDef.STACK_TEST)
				&& getModifyComplianceStatusEvent().getStackTestReference() == null) {
			DisplayUtil.displayError("ERROR: Attribute Reference must have a value.");
			isValid = false;
		}
		if (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(), ComplianceTrackingEventTypeDef.COMPLIANCE_REPORT)
				&& getModifyComplianceStatusEvent().getComplianceReportReference() == null) {
			DisplayUtil.displayError("ERROR: Attribute Reference must have a value.");
			isValid = false;
		}
		if (StringUtils.equalsIgnoreCase(getModifyComplianceStatusEvent().getEventTypeCd(), ComplianceTrackingEventTypeDef.INSPECTION)
				&& getModifyComplianceStatusEvent().getInspectionReference() == null) {
			DisplayUtil.displayError("ERROR: Attribute Reference must have a value.");
			isValid = false;
		}

		return isValid;
	}

    /*  COMPLIANCE EVENT END  */
	
	/* CONDITION STATUS START */
	
/*	public final String displayPermitConditionStatus() {
		String navigateTo = null;
		
		if (null != this.permitConditionId) {
			try {
				List<PermitConditionSupersession> thisConditionSupersededByList = getPermitConditionService()
						.retrieveThisConditionSupersededByList(this.permitConditionId);
				this.PermitConditionStatusWrapper2 = new TableSorter();
				this.PermitConditionStatusWrapper2.setWrappedData(thisConditionSupersededByList);
				navigateTo = "dialog:permitConditionSupersedenceDetail";
			} catch (DAOException daoe) {
				DisplayUtil.displayError("Failed to retrieve supeseding permit condition list");
				logger.error(daoe.getMessage(), daoe);
				handleException(daoe);
			}
		} else {
			// should never happen
			DisplayUtil.displayError("Cannot load permit condition status");
		}
		
		return navigateTo;
	}*/
	public final void displayPermitConditionStatus(){
		
		if (null != this.permitConditionId) {
			try {
				closeOpenModelessDialogs();
				List<PermitConditionSupersession> thisConditionSupersededByList = getPermitConditionService()
						.retrieveThisConditionSupersededByList(this.permitConditionId);
				this.PermitConditionStatusWrapper2 = new TableSorter();
				this.PermitConditionStatusWrapper2.setWrappedData(thisConditionSupersededByList);
				FacesUtil.startModelessDialog(PERMIT_CONDITION_SUPERSEDENCE_STATUS_URI, PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_HEIGHT,
						PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_WIDTH);
			} catch (DAOException daoe) {
				DisplayUtil.displayError("Failed to retrieve supeseding permit condition list");
				logger.error(daoe.getMessage(), daoe);
				handleException(daoe);
			}
		} else {
			// should never happen
			DisplayUtil.displayError("Cannot load permit condition status");
		}
/*		
		
		if (null != this.modifyPermitCondition) {
			this.PermitConditionStatusWrapper2 = new TableSorter();
			this.PermitConditionStatusWrapper2.setWrappedData(this.modifyPermitCondition.getSupersededByThis());
			FacesUtil.startModelessDialog(PERMIT_CONDITION_SUPERSEDENCE_STATUS_URI, PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_HEIGHT,
					PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_WIDTH);
		} else {
			// should never happen
			DisplayUtil.displayError("Cannot load permit condition status");
		}*/
	}

	
	public boolean isDisplayNoSupersessionNotification() {
		boolean ret = true;
		if (null != this.PermitConditionStatusWrapper2 && this.PermitConditionStatusWrapper2.getRowCount() > 0) {
			ret = false;
		}
		return ret;
	}
	
	public void closePermitConditionStatusDialog() {
//		this.modifyPermitCondition = null;
		this.PermitConditionStatusWrapper2 = null;
		closeModelessDialog();
	}
	
	public String getSupersededPermitConditionId() {
		String supersededPermitConditionId = null;
		if(null != this.PermitConditionStatusWrapper2
				&& this.PermitConditionStatusWrapper2.getRowCount() > 0 ) {
			@SuppressWarnings("unchecked")
			List<PermitConditionSupersession> superSessionList = (ArrayList<PermitConditionSupersession>) this.PermitConditionStatusWrapper2
					.getWrappedData();
			if(null != superSessionList && !superSessionList.isEmpty()) {
				supersededPermitConditionId = superSessionList.get(0).getSupersededpcondId();
			}
		}
		
		return supersededPermitConditionId;
	}
	
	/* CONDITION STATUS END */
	
	/* PERMIT CONDITION SUPERSESSION START */
	
	public final String startToAddPermitConditionSupersession() {

		this.conditionSupersession = new PermitConditionSupersession();
		this.conditionSupersession.setSupersedingPermitConditionId(modifyPermitCondition.getPermitConditionId());
		this.conditionSupersession.setSupersedingPermitNumber(this.getPermit().getPermitNumber());
		this.conditionSupersession.setSupersedingPermitType(this.getPermit().getPermitType());
		
		this.possibleSupersededConditions = new ArrayList<SelectItem>();
		
		this.editPermitConditionSupersession = false;
		
		this.newPermitConditionSupersession = true;

		return "dialog:permitConditionSupersession";
	}
	
	public final String displayPermitConditionSupersession() {
		String nextDialog = null;
		
		if(null != this.conditionSupersession) {
			this.possibleSupersededConditions = new ArrayList<SelectItem>();
			this.newPermitConditionSupersession = false;
			nextDialog = "dialog:permitConditionSupersession";
		} else {
				DisplayUtil.displayError("Cannot load permit condition supersession");
				refreshPermitConditionSupersessionList();
		}
		
		return nextDialog;
	}
	
	public void startToEditPermitConditionSupersession() {
		this.editPermitConditionSupersession = true;
	}
	
	public final void savePermitConditionSupersession() {

		DisplayUtil.clearQueuedMessages();

		if (conditionSupersessionValidated()) {
			if (isNewPermitConditionSupersession()) {
				createPermitConditionSupersession();
			} else {
				updatePermitConditionSupersession();
			}
			
			closePermitConditionSupersessionDialog();
		}
	}
	
	public void cancelPermitConditionSupersessionDialog() {
		closePermitConditionSupersessionDialog();
	}
	
	public void closePermitConditionSupersessionDialog() {
		resetPermitConditionSuperssionDialogData();
		refreshPermitConditionSupersessionList();
		closeDialog();
	}
	
	public void deletePermitConditionSupersession(ReturnEvent re) {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			clearButtonClicked();
			return;
		}
		
		DisplayUtil.clearQueuedMessages();

		try {
			getPermitConditionService().deletePermitConditionSupersession(conditionSupersession);
			DisplayUtil.displayInfo("Permit condition supersession deleted successfully.");
		}  catch (DAOException daoe) {
			logger.error("Delete of PermitConditionSupersession failed. ", daoe);
			DisplayUtil.displayError("Delete of permit condition supersession failed. ");
			handleException(daoe);

		} finally {
			clearButtonClicked();
			closePermitConditionSupersessionDialog();
		}
	}
	
	public final void supersededPermitChanged(ValueChangeEvent ve) {
		
		possibleSupersededConditions = new ArrayList<SelectItem>();

        try {
			
			Integer supersessionPermitId = (Integer) ve.getNewValue();
			if (supersessionPermitId == null) {
				DisplayUtil.displayError("The superseded permit number is missing.");
				return;
			}
			
			List<PermitCondition> pcs = getPermitConditionService().retrievePermitConditionList(supersessionPermitId);
            DisplayUtil.displayHitLimit(pcs.size());
            for (PermitCondition pc : pcs) {
            	boolean alreadySuperseded = false;
            	for (PermitConditionSupersession cs : modifyPermitCondition.getSupersededByThis()) {
            		if (pc.getPermitConditionId().equals(cs.getSupersededPermitConditionId())) {
            			alreadySuperseded = true;
            		}
            	}
            	if (!alreadySuperseded) {
        			possibleSupersededConditions.add(new SelectItem(pc.getPermitConditionId(), pc.getPcondId() + " - " + pc.getPermitConditionNumber()));
            	}
            }
		} catch (DAOException re) {
			DisplayUtil.displayError("Failed to retrieve list of permit conditions.");
			handleException(re);
			logger.error(re.getMessage(), re);
		}
	}
	
    public final void deleteEUSupersededPermit(ActionEvent actionEvent) {
        /*
         * TODO - Fix superseded permits. List<Permit> supersededPermits =
         * getSupersededPermitsToDelete(actionEvent); for (Permit
         * supersededPermit : supersededPermits) {
         * selectedEU.getSupersededPermits().remove(supersededPermit); }
         */
    }

	public boolean conditionSupersessionValidated() {

		boolean isValid = true;
		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();
		
		if(isNewPermitConditionSupersession()) {
			if (conditionSupersession.getSupersededPermitConditionIds() == null
					|| conditionSupersession.getSupersededPermitConditionIds().size() == 0) {
				isValid = false;			
				valMsgs.add(new ValidationMessage("supersededPermitConditionIds",
						"At least one superseded permit Condition Id must be selected.", 
						ValidationMessage.Severity.ERROR, null));
			}
		} else {
			if (conditionSupersession.getSupersededPermitConditionId() == null) {
				isValid = false;			
				valMsgs.add(new ValidationMessage("supersededPermitConditionId",
						"The superseded permit Condition Id is missing.", 
						ValidationMessage.Severity.ERROR, null));
			}
		}
		
		if (conditionSupersession.getSupersedingPermitConditionId() == null) {
			isValid = false;			
			valMsgs.add(new ValidationMessage("supersedingPermitConditionId",
					"The superseding permit Condition Id is missing.", 
					ValidationMessage.Severity.ERROR, null));
		}
		if (conditionSupersession.getSupersedingOption() == null) {
			isValid = false;
			valMsgs.add(new ValidationMessage("supersedingOption",
					"The superseding option is missing.", 
					ValidationMessage.Severity.ERROR, null));
		}
		if (conditionSupersession.getSupersedingOption() != null 
				&& conditionSupersession.getSupersedingOption().equals(PermitConditionSupersedenceStatusDef.PARTIAL)
				&& conditionSupersession.getComments().isEmpty()) {
			isValid = false;			
			valMsgs.add(new ValidationMessage("comment",
					"Comments are required if the superseding option is partial.", 
					ValidationMessage.Severity.ERROR, null));
		}

		if (!isValid) {
			displayValidationMessages("", valMsgs.toArray(new ValidationMessage[0]));
		}
		
		return isValid;
	}
	
	public final List<SelectItem> getOtherActivePermitsForFacility() {

		List<SelectItem> activePermits = new ArrayList<SelectItem>();

		try {

			String dateBy = null;
			if (permit.getFinalIssueDate() != null) {
				dateBy = "pif.issuance_date";
			}
			// PermitLevelStatusDef.ACTIVE
			List<Permit> ps = getPermitService().search(null, null, null, permit.getPermitType(), null, null, null,
					null, permit.getFacilityId(), null, PermitGlobalStatusDef.ISSUED_FINAL, dateBy, null,
					permit.getFinalIssueDate(), null, unlimitedResults(), null);

			DisplayUtil.displayHitLimit(ps.size());

			for (Permit p : ps) {
				if (!permit.getPermitID().equals(p.getPermitID())) {
					activePermits.add(new SelectItem(p.getPermitID(), p.getPermitNumber()));
				}
			}

		} catch (RemoteException e) {
			handleException(e);
		}

		return activePermits;
	}
	
	private boolean createPermitConditionSupersession() {
		boolean created = false;
		try {
			getPermitConditionService().createPermitConditionSupersession(conditionSupersession);
			created = true;
			DisplayUtil.displayInfo("Permit condition supersession created successfully.");
		}  catch (DAOException daoe) {
			logger.error("Create PermitConditionSupersession failed. ", daoe);
			DisplayUtil.displayError("Create permit condition supersession failed. ");
			handleException(daoe);
		}
		
		return created;
	}

	private boolean updatePermitConditionSupersession() {
		boolean updated = false;
		try {
			getPermitConditionService().modifyPermitConditionSupersession(conditionSupersession);
			updated = true;
			DisplayUtil.displayInfo("Permit condition supersession updated successfully.");
		}  catch (DAOException daoe) {
			logger.error("Update of PermitConditionSupersession failed. ", daoe);
			DisplayUtil.displayError("Update of permit condition supersession failed. ");
			handleException(daoe);
		}
		
		return updated;
	}
	
	private final void refreshPermitConditionSupersessionList() {
		if (this.modifyPermitCondition != null) {
			try {
				this.SuperSessionWrapper = new TableSorter();
				List<PermitConditionSupersession> newSupersessionList = getPermitConditionService().retrieveThisConditionSupersedesList(this.modifyPermitCondition.getPermitConditionId());
				this.modifyPermitCondition.setSupersededByThis(newSupersessionList);
				this.SuperSessionWrapper.setWrappedData(newSupersessionList);
			}  catch (DAOException daoe) {
				logger.error("Retieval of new PermitConditionSupersession list failed. ", daoe);
				DisplayUtil.displayError("Retieval of new PermitConditionSupersession list failed. ");
				handleException(daoe);
			} 	
		}
	}
	
	private void resetPermitConditionSuperssionDialogData() {
		this.editPermitConditionSupersession = false;
		this.newPermitConditionSupersession = false;
		this.possibleSupersededConditions = null;
		this.conditionSupersession = null;
	}
	
	/* PERMIT CONDITION SUPERSESSION END */
	
	
	/* PERMIT CONDITION SEARCH START */
	
	public void displayComplianceEventHistoryFromSearch() {
		if (null != this.permitConditionSearchLineItem) {
			this.ComplianceStatusEventsWrapper = new TableSorter();
			this.ComplianceStatusEventsWrapper.setWrappedData(this.permitConditionSearchLineItem.getComplianceStatusEventList());
			
			FacesUtil.startModelessDialog(COMPLIANCE_STATUS_HISTORY_URI, COMPLIANCE_STATUS_HISTORY_WINDOW_HEIGHT,
						COMPLIANCE_STATUS_HISTORY_WINDOW_WIDTH);
		} else {
			DisplayUtil.displayError("Cannot load permit condition compliance status history");
		}
	}
	
	
	public void displayPermitConditionStatusFromSearch(){
//		if (null != this.permitConditionSearchLineItem){
//			this.PermitConditionStatusWrapper2 = new TableSorter();
//			this.PermitConditionStatusWrapper2.setWrappedData(this.permitConditionSearchLineItem.getSupersededByThis());
//			FacesUtil.startModelessDialog(PERMIT_CONDITION_SUPERSEDENCE_STATUS_URI, PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_HEIGHT,
//					PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_WIDTH);
//		} else {
//			// should never happen
//			DisplayUtil.displayError("Cannot load permit condition status");
//		}
//		
//		
//		
		
		if (null != this.permitConditionId) {
			try {
				List<PermitConditionSupersession> thisConditionSupersededByList = getPermitConditionService()
						.retrieveThisConditionSupersededByList(this.permitConditionId);
				this.PermitConditionStatusWrapper2 = new TableSorter();
				this.PermitConditionStatusWrapper2.setWrappedData(thisConditionSupersededByList);
				FacesUtil.startModelessDialog(PERMIT_CONDITION_SUPERSEDENCE_STATUS_URI, PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_HEIGHT,
						PERMIT_CONDITION_SUPERSEDENCE_STATUS_WINDOW_WIDTH);
			} catch (DAOException daoe) {
				DisplayUtil.displayError("Failed to retrieve supeseding permit condition list");
				logger.error(daoe.getMessage(), daoe);
				handleException(daoe);
			}
		} else {
			// should never happen
			DisplayUtil.displayError("Cannot load permit condition status");
		}
		
		
	}
	
	
	public void showPermitConditionDetailFromSearch() {
		if (null != this.permitConditionId) {
			closeOpenModelessDialogs();
			try {
				loadPermitCondition();
				this.newPermitCondition = false;
				this.editMode1 = false;

				FacesUtil.startModelessDialog(PERMIT_CONDITION_DETAIL_URI, PERMIT_CONDITION_WINDOW_HEIGHT,
						PERMIT_CONDITION_WINDOW_WIDTH);

			} catch (RemoteException re) {
				DisplayUtil.displayError("Failed to retrieve permit condition");
				handleException(re);
				logger.error(re.getMessage(), re);
			}
		} else {
			DisplayUtil.displayError("Cannot load permit condition");
		}
	}
	
	
	/* PERMIT CONDITION SEARCH END */
	
	/**
	 * Attempts to close modeless dialogs whose uris are specified 
	 * in the {@link PermitConditionDetail#modelessDialogUris} array 
 
	 * @return none
	 */
	private void closeOpenModelessDialogs() {
		for(String uri: this.modelessDialogUris) {
			FacesUtil.startAndCloseModelessDialog(uri);
		}
	}
}
