package us.oh.state.epa.stars2.app.ceta;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.bo.EnforcementActionService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementActionEvent;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EnforcementActionEventDef;
import us.oh.state.epa.stars2.def.EnforcementActionEventDependencyDef;
import us.oh.state.epa.stars2.def.EnforcementActionFRVTypeDef;
import us.oh.state.epa.stars2.def.EnforcementActionHPVCriterionDef;
import us.oh.state.epa.stars2.def.EnforcementActionReferralTypeDef;
import us.oh.state.epa.stars2.def.EnforcementActionTypeDef;
import us.oh.state.epa.stars2.def.EnforcementAttachmentTypeDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.correspondence.CorrespondenceSearch;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;


@SuppressWarnings("serial")
public class EnforcementActionDetail extends TaskBase implements IAttachmentListener{

	private String facilityId;
	private Integer enforcementActionId;
	private EnforcementAction enforcementAction;
	private String facilityName;
	private String companyName;
	
	private EnforcementActionService enforcementActionService;
	private FacilityService facilityService;
	private CorrespondenceService correspondenceService;
	private ReadWorkFlowService workFlowService;
	private boolean editable;
	private boolean editMode1;
	
	protected String popupRedirect = null;
	
	private EnforcementNote enforcementNote;
	private boolean noteReadOnly;
	
	private TableSorter enforcementActionEventWrapper;
	protected EnforcementActionEvent modifyEnforcementActionEvent;
	private boolean newEnforcementActionEvent = false;
	
	private boolean disableDelete = false;
	private String disableDeleteMsg;
	private boolean hasCorrespondences = false;
	
	private static final int EA_WIDTH = 600;
	
	private boolean newEnforcementAction = false;
	private boolean fromStackTest = false;
	private boolean fromInspection = false;

	public EnforcementActionDetail() {
		super();
		enforcementAction = new EnforcementAction();
		enforcementActionEventWrapper = new TableSorter();
	}

	public EnforcementAction getEnforcementAction() {
		return enforcementAction;
	}
	
	public void setEnforcementAction(EnforcementAction enforcementAction) {
		this.enforcementAction = enforcementAction;
	}
	
	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public EnforcementActionService getEnforcementActionService() {
		return enforcementActionService;
	}
	
	public void setEnforcementActionService(EnforcementActionService enforcementActionService) {
		this.enforcementActionService = enforcementActionService;
	}
	
	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
	public CorrespondenceService getCorrespondenceService() {
		return correspondenceService;
	}

	public void setCorrespondenceService(CorrespondenceService correspondenceService) {
		this.correspondenceService = correspondenceService;
	}
	
	public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}
	
	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public final String startNewEnforcementAction() throws DAOException {
		enforcementAction = new EnforcementAction();
		editable = true;
		FacilityProfile facProfile;
		Facility facility;
		
		try {
			if (this.facilityId == null) {
				facProfile = (FacilityProfile)FacesUtil.getManagedBean("facilityProfile");
				facility = facProfile.getFacility();
			} else {
				facility = getFacilityService().retrieveFacility(facilityId);
			}
			if (facility == null){
				DisplayUtil.displayError("Failed to read facility information");
				return null;
			}
			setCompanyName(facility.getOwner().getCompany().getName());
			setFacilityName(facility.getName());
			enforcementAction.setFacilityId(facility.getFacilityId());
			enforcementAction.setFacilityNm(facility.getName());
			enforcementAction.setCompanyName(facility.getOwner().getCompany().getName());
			enforcementAction.setCreatorId(InfrastructureDefs.getCurrentUserId());
			enforcementAction.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			//do no insert new row to ce_enf_action table until it is saved.
//			enforcementAction = enforcementActionService.createEnforcementAction(enforcementAction);
//			enforcementAction = enforcementActionService.retrieveEnforcementAction(enforcementAction.getEnforcementActionId());
			enforcementAction.setEnfFormOfActionCd("I");
			//Will not allow adding attachments or notes until the enforcement action is saved
			facilityId = enforcementAction.getFacilityId();
//			setEnforcementActionId(enforcementAction.getEnforcementActionId());
			this.newEnforcementAction = true; // set the new enforcementAction indicator to true
			initializeAttachmentBean();
            this.refreshEnforcementActionEvents();
            this.setCorrespondences();
           
			editable = true;
		}catch(Exception ex) {
			DisplayUtil.displayError("Failed to create enforcement action");
			logger.error(ex.getMessage(), ex);
		}
		
//		((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_enforcementActionDetail")).setDisabled(false);
		return "dialog:enforcementActionDetail";
	}
	
	public final String saveEnforcementAction(ActionEvent event) {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		boolean ok = true;
		try {
			String errorClientIdPrefix = "saveEnforcementAction:";
			
			if (displayValidationMessages(errorClientIdPrefix,
					enforcementAction.validate())) {
				ok = false;
			}
			
			if (!enforcementAction.isLegacyFlag() && isNOVClosed()) {
				if (!validateFinal()) {
					ok = false;
				}
			}

			if (ok) {
				if (!this.isShowCriterion()) {
					enforcementAction.setEnforcementActionHPVCriterion(null);
				}
				if (!this.isShowFRVType()) {
					enforcementAction.setEnforcementActionFRVType(null);
				}
				if (!this.enforcementAction.isReferralTypeOther()) {
					enforcementAction.setOtherDescription(null);
				}
				if (Utility.isNullOrEmpty(this.enforcementAction.getSepFlag()) || this.enforcementAction.getSepFlag().equals("N")) {
					enforcementAction.setSepOffsetAmount(null);
				}
				if (Utility.isNullOrEmpty(this.enforcementAction.getOtherSARequirements()) || this.enforcementAction.getOtherSARequirements().equals("N")) {
					enforcementAction.setOtherSARequirementsMet(null);
				}
				if (Utility.isNullOrEmpty(this.enforcementAction.getEnvironmentalImpact()) || this.enforcementAction.getEnvironmentalImpact().equals("N")) {
					enforcementAction.setEnvironmentalImpactDescription(null);
				}
				if (Utility.isNullOrEmpty(this.enforcementAction.getEvidenceAttached()) || this.enforcementAction.getEvidenceAttached().equals("N")) {
					enforcementAction.setEvidence(null);
				}
				//If it is a new enforcementAction, insert data to SQL table
				if (newEnforcementAction){
					enforcementAction = enforcementActionService.createEnforcementAction(enforcementAction);
					this.newEnforcementAction = false;//if successfully insert to SQL table, set the new indicator to false
					enforcementAction = enforcementActionService.retrieveEnforcementAction(enforcementAction.getEnforcementActionId());
					enforcementAction.setEnfFormOfActionCd("I");
					facilityId = enforcementAction.getFacilityId();
					setEnforcementActionId(enforcementAction.getEnforcementActionId());
					setFromTODOList(false);
					
					initializeAttachmentBean();
					this.setCorrespondences();
					((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_enforcementActionDetail")).setDisabled(false);
					DisplayUtil.displayInfo("Enforcement Action created successfully.");
				}
				else {
					enforcementActionService.modifyEnforcementAction(enforcementAction);
					DisplayUtil.displayInfo("Enforcement Action updated successfully.");
				}
				editable = false;		
			}
		} catch (Exception ex) {
			if (newEnforcementAction){
				enforcementAction.setEnforcementActionId(null);
				DisplayUtil.displayError("Failed to create enforcement action");
			} else {
				DisplayUtil.displayError("Failed to update enforcement action");
			}
			logger.error(ex.getMessage(), ex);
		} finally {
			clearButtonClicked();
		}
		return "dialog:enforcementActionDetail";
	}
	
	public final void startEditAction() {
		editable = true;
	}
	
	public final String cancelEditAction() {
		enforcementAction = null;
		enforcementAction = new EnforcementAction();
		editable = false;
		//for new enforcement action
		if (newEnforcementAction){
			DisplayUtil.displayInfo("Operation Cancelled.  Enforcement Action not created.");
		    EnforcementActionDetail enforcementActionDetail = (EnforcementActionDetail) FacesUtil.getManagedBean("enforcementActionDetail");
		    enforcementActionDetail.setFacilityId(null);//??????
		    newEnforcementAction = false;
		    if (this.fromInspection){
		        fromInspection = false;
		        //retrieve inspection detail???
		        return "ceta.fceDetail"; 
		    }
		    if (fromStackTest){
		        fromStackTest = false;
		        return "ceta.stackTestDetail";
		    }
//		    FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
//		    try {
//		    	fp.setFacility(getFacilityService().retrieveFacility(fp.getFpId()));
//		    } catch (RemoteException re) {
//		        DisplayUtil.displayError("Failed to read facility inventory for fpId " + fp.getFpId());
//		        handleException(re);
//		        return null;
//		    }
		    EnforcementSearch enforcementSearch = (EnforcementSearch)FacesUtil.getManagedBean("enforcementSearch");
		    enforcementSearch.setFromFacility(true);
		    enforcementSearch.initEnforcements();
		    return "facilities.profile.enforcements";
//		    return "facilities.profile.enforcements";
		}
		else {//for non-new enforcement action --> enforcementActionDetail page
			try{
				enforcementAction = enforcementActionService.retrieveEnforcementAction(enforcementActionId);
			} catch(Exception ex) {
				DisplayUtil.displayError("Failed to retrieve enforcement action");
				logger.error(ex.getMessage(), ex);
			}
			return "dialog:enforcementActionDetail";
		}
//		try {
//			//for new enforcement action
//			if (newEnforcementAction){
//				DisplayUtil.displayInfo("Operation Cancelled.  Enforcement Action not created.");
//
//			    reset();
//			    EnforcementActionDetail enforcementActionDetail = (EnforcementActionDetail) FacesUtil.getManagedBean("enforcementActionDetail");
//			    enforcementActionDetail.setFacilityId(null);//??????
//			    FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
//			    try {
//			    	fp.setFacility(getFacilityService().retrieveFacility(fp.getFpId()));
//			    } catch (RemoteException re) {
//			        DisplayUtil.displayError("Failed to read facility inventory for fpId " + fp.getFpId());
//			        handleException(re);
//			        return null;
//			    }
//			        
//			    EnforcementSearch enforcementSearch = (EnforcementSearch)FacesUtil.getManagedBean("enforcementSearch");
//			    enforcementSearch.setFromFacility(true);
//			    enforcementSearch.initEnforcements();
//			        
//			    if (fromInspection){
//			        fromInspection = false;
//			        return "ceta.fceDetail"; 
//			    }
//			    if (fromStackTest){
//			        fromStackTest = false;
//			        return "ceta.stackTestDetail";
//			    }
//			    return "facilities.profile.enforcements";
//			} else { //not new enforcement action
//				enforcementAction = enforcementActionService.retrieveEnforcementAction(enforcementActionId);
//			}
//			
//		}catch(Exception ex) {
//			DisplayUtil.displayError("Failed to retrieve enforcement action");
//			logger.error(ex.getMessage(), ex);
//		}
//		return "dialog:enforcementActionDetail";
	}

	public final DefSelectItems getEnforcementActionReferralTypeDef() {
        return EnforcementActionReferralTypeDef.getData().getItems();
    }
	
	public final DefSelectItems getEnforcementActionTypeDef() {
		return EnforcementActionTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getEnforcementActionHPVCriterionDef() {
		return EnforcementActionHPVCriterionDef.getData().getItems();
	}
	
	public final DefSelectItems getEnforcementActionFRVTypeDef() {
		return EnforcementActionFRVTypeDef.getData().getItems();
	}
	
	private void setCorrespondences() throws RemoteException {
    	Correspondence[] correspondence = getCorrespondenceService().searchCorrespondenceByLinkedToId(this.getEnforcemenActionId());
    	hasCorrespondences = false;
    	if (correspondence.length > 0) {
    		hasCorrespondences = true;
    	}
		CorrespondenceSearch cs = (CorrespondenceSearch) FacesUtil.getManagedBean("correspondenceSearch");
		cs.setCorresForLinkedByTo(correspondence);
		cs.setFromFacility("false");
		cs.setFromEnforcementAction("true");
    }
    
    @Override
	public Integer getExternalId() {
		return enforcementActionId;
	}

	@Override
	public void setExternalId(Integer externalId) {
		enforcementActionId = externalId;
		submitInternal();
		SimpleMenuItem.setDisabled("menuItem_enforcementActionDetail", false);  
	}
	
	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		
		return messages;
	}
	
	@Override
	public String findOutcome(String url, String ret) {
		
		return submitInternal();
	}
	
	public final void reset() {
		enforcementAction = null;
		editable = false;
		//setEnfDiscoveryIdName(null);
	}
	
	public final void setEnforcementActionId(Integer enforcementActionId) {
    	this.enforcementActionId = enforcementActionId;
    }


	public final Integer getEnforcemenActionId() {
		return enforcementActionId;
	}
	
	public final String submitEnforcementAction() {
		popupRedirect = null;
		
		return submitInternal();
	}
	
	public String getPopupRedirect() {
		if (popupRedirect != null) {
            FacesUtil.setOutcome(null, popupRedirect);
            popupRedirect = null;
        }
        return null;
	}
	
	public final void setPopupRedirect(String popupRedirect) {
        this.popupRedirect = popupRedirect;
    }

	public boolean isLocked() {
		boolean ret = true;
		/*
		 * This returns true is user is read-only user.
		 */
		if (isAdmin()) {
			ret = false;
		} else if (!isReadOnlyUser()) {
			ret = false;
		}

		return ret;
	}
	
	public boolean isAdmin() {
		boolean ret = false;

		ret = InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();

		return ret;
	}
	
	public String getExternalNum() {
		return enforcementAction.getEnfId();
	}
	
	public String getExternalName(ProcessActivity activity) {
		String ret = super.getExternalName(activity);
		ret = "Enforcement Action";
		return ret;
	}
	
	public String toExternal() {
        return "dialog:enforcementActionDetail";
    }
	
	public boolean isActiveWorkflowProcess() {
		Integer processTemplateId = 12;
		Integer externalId = enforcementAction.getEnforcementActionId();
    	String errMsg = "Error occurred while checking if an active " +
    			"workflow process exists for this enforcement action; " +
    			"enforcment action deletion will not be allowed.";
		try {
			return null == workFlowService.findActiveProcessByExternalObject(
					processTemplateId, externalId) ? false : true;
		} catch (RemoteException e) {
			logger.error("error occurred checking for active wf process",e);
			DisplayUtil.displayError(errMsg);
			handleException(e);
			return true;
		}
	}
	
	public final String requestDelete() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		clearButtonClicked();

		return "dialog:requestEnforcementActionDelete";
	}
	
	public boolean isDisableDelete() {
		return disableDelete;
	}

	public String getDisableDeleteMsg() {
		return disableDeleteMsg;
	}
	
	public final void closePopup() {
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public final String deleteEnforcementAction() {

		boolean ok = true;

		if (ok) {
			try {
				getEnforcementActionService().deleteEnforcementAction(
						enforcementAction);

				EnforcementSearch enforcementSearch = (EnforcementSearch) FacesUtil
						.getManagedBean("enforcementSearch");
				enforcementSearch
						.setPopupRedirectOutcome(EnforcementSearch.SEARCH_OUTCOME);
				enforcementSearch.search();
				DisplayUtil
						.displayInfo("Enforcement Action successfully deleted");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_enforcementActionDetail"))
						.setDisabled(true);

				FacesUtil.returnFromDialogAndRefresh();
				return null;

			} catch (DAOException e) {

				DisplayUtil
						.displayError("Cannot Delete Enforcement Action Because "
								+ ((DAOException) e).getPrettyMsg());
				FacesUtil.returnFromDialogAndRefresh();
				return null;
			} catch (RemoteException e) {
				handleException(e);

				ok = false;
			}
		}
		if (!ok) {
			DisplayUtil.displayInfo("Failed to delete Enforcement Action.");
		}
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}
	
	public void initializeAttachmentBean() {
		Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
		a.addAttachmentListener(this);
		a.setStaging(false);
		a.setNewPermitted(isCetaUpdate());
		a.setUpdatePermitted(isCetaUpdate());
		a.setFacilityId(enforcementAction.getFacilityId());
		a.setSubPath("EnforcementAction");
		if(enforcementAction.getEnforcementActionId() != null) a.setObjectId(Integer.toString(enforcementAction.getEnforcementActionId()));
        else a.setObjectId("");
		a.setSubtitle(null);
		a.setTradeSecretSupported(false);
		a.setHasDocType(true);
		a.setAttachmentTypesDef(EnforcementAttachmentTypeDef.getData()
				.getItems());
		a.setAttachmentList(enforcementAction.getAttachments().toArray(
				new Attachment[0]));
		a.cleanup();
	}

	public void cancelAttachment() {
		// also used for other cancels
		popupRedirect=null;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public AttachmentEvent createAttachment(Attachments attachment)
			throws AttachmentException {
		boolean ok = true;
		if (attachment.getDocument() == null) {
			// should never happen
			logger.error("Attempt to process null attachment");
			ok = false;
		} else {
			
			if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getFileToUpload() != null) {
				attachment.setPublicAttachmentInfo(new UploadedFileInfo(
						attachment.getFileToUpload()));
			}
			
			if (attachment.getDocument().getDescription() == null) {
				DisplayUtil
						.displayError("Please specify the description for this attachment");
				ok = false;
			}
			if (attachment.getDocument().getDocTypeCd() == null) {
				DisplayUtil
						.displayError("Please specify an attachment type for this attachment");
				ok = false;
			}
			//if (attachment.getDocument().getFileName() == null) {
			if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getTempDoc().getDocumentID() == null) {
				DisplayUtil
						.displayError("Please specify the file to upload for this attachment");
				ok = false;
			}
		}
		if (ok) {
			if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getFileToUpload() != null) {
				attachment.setPublicAttachmentInfo(new UploadedFileInfo(
						attachment.getFileToUpload()));
			}
			try {
				try {
					getEnforcementActionService().createEnforcementAttachment(
							enforcementAction,
							attachment.getTempDoc(),
							attachment.getPublicAttachmentInfo()
									.getInputStream());
				} catch (IOException e) {
					throw new RemoteException(e.getMessage(), e);
				}
				
				// save the EA because user may have entered some new data
				ok = enforcementActionService
						.modifyEnforcementAction(enforcementAction);
				
				if (ok) {

					enforcementAction = getEnforcementActionService()
							.retrieveEnforcementAction(
									enforcementAction.getEnforcementActionId());
					// setEnfDiscoveryIdName(null);
					Attachments a = (Attachments) FacesUtil
							.getManagedBean("attachments");
					a.setAttachmentList(enforcementAction.getAttachments()
							.toArray(new Attachment[0]));
					attachment.cleanup();
					FacesUtil.returnFromDialogAndRefresh();
				}
			} catch (RemoteException e) {
				handleException(e);
			}
		}
		return null;
	}

	public AttachmentEvent deleteAttachment(Attachments attachment) {
		boolean ok = true;
		try {
			Attachment doc = attachment.getTempDoc();
			EnforcementAttachment sa = new EnforcementAttachment(doc);
			getEnforcementActionService().removeEnforcementAttachment(sa);
		} catch (RemoteException e) {
			ok = false;
			handleException(e);
		}
		// reload attachments
		try {
			enforcementAction.setAttachments(getEnforcementActionService()
					.retrieveEnforcementAttachments(enforcementAction.getEnforcementActionId()));
			Attachments a = (Attachments) FacesUtil
					.getManagedBean("attachments");
			a.setAttachmentList(enforcementAction.getAttachments().toArray(
					new Attachment[0]));
			attachment.cleanup();
		} catch (RemoteException e) {
			//error = true;
			setEditable(false);
			//memoEditable = false;
			// turn it into an exception we can handle.
			ok = false;
			handleException(e);
		}
		if (ok) {
			DisplayUtil.displayInfo("The attachment has been removed.");
			FacesUtil.returnFromDialogAndRefresh();

		}
		return null;
	}

	public AttachmentEvent updateAttachment(Attachments attachment) {
		Attachment doc = attachment.getTempDoc();
		boolean ok = true;

		// make sure document description is provided
		if (doc.getDescription() == null
				|| doc.getDescription().trim().equals("")) {
			DisplayUtil
					.displayError("Please specify the description for this attachment");
			ok = false;
		}

		if (ok) {
			try {
				EnforcementAttachment ea = new EnforcementAttachment(doc);
				getEnforcementActionService().modifyEnforcementAttachment(ea);
			} catch (RemoteException e) {
				ok = false;
				handleException(e);
			}
			// reload attachments
			try {
				enforcementAction.setAttachments(getEnforcementActionService()
						.retrieveEnforcementAttachments(enforcementAction.getEnforcementActionId()));
				Attachments a = (Attachments) FacesUtil
						.getManagedBean("attachments");
				a.setAttachmentList(enforcementAction.getAttachments().toArray(
						new Attachment[0]));
				attachment.cleanup();
			} catch (RemoteException e) {
				//error = true;
				setEditable(false);
				//memoEditable = false;
				// turn it into an exception we can handle.
				ok = false;
				handleException(e);
			}
			if (ok) {
				DisplayUtil
						.displayInfo("The attachment information has been updated.");
				FacesUtil.returnFromDialogAndRefresh();
			}
		}
		return null;
	}
	
	public final Note getEnforcementNote() {
		return enforcementNote;
	}
	
	public final String startAddNote() {
		enforcementNote = new EnforcementNote();
		enforcementNote.setUserId(InfrastructureDefs.getCurrentUserId());
		enforcementNote.setNoteTypeCd(NoteType.DAPC);
		enforcementNote
				.setDateEntered(new Timestamp(System.currentTimeMillis()));
		enforcementNote.setEnforcementId(enforcementAction.getEnforcementActionId());
    	noteReadOnly = false;
		return "dialog:enforcementNoteDetail";
	}

	public final String startEditNote() { 
		
		enforcementNote = new EnforcementNote((EnforcementNote) FacesUtil.getManagedBean("enfNote"));
		enforcementNote.setEnforcementId(enforcementAction.getEnforcementActionId());
		
		if (enforcementNote.getUserId().equals(InfrastructureDefs.getCurrentUserId())) {
			noteReadOnly = false;
		} else {
			noteReadOnly = true;
		}

		return "dialog:enforcementNoteDetail";
		
		
	}

	public final void applyEditNote(ActionEvent actionEvent) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		
		// make sure note is provided
        if (enforcementNote.getNoteTxt() == null || enforcementNote.getNoteTxt().trim().equals("")) {
        	validationMessages.add(new ValidationMessage("noteTxt", "Attribute " + "Note" + " is not set.",
					ValidationMessage.Severity.ERROR, "noteTxt"));
		}
		
		if (validationMessages.size() > 0) {
			displayValidationMessages("", validationMessages
					.toArray(new ValidationMessage[0]));
		} else {
			try {
				if (enforcementNote.getNoteId() == null) {
					// new note
					getEnforcementActionService().createEnforcementNote(enforcementNote);
					DisplayUtil
							.displayInfo("The note added to the enforcement successfully.");
				} else {
					// edit existing note
					if (!isStars2Admin()
							&& !enforcementNote.getUserId().equals(
									InfrastructureDefs.getCurrentUserId())) {
						DisplayUtil
								.displayError("You may only edit a note which you have created.");
					} else {
						getEnforcementActionService().modifyEnforcementNote(enforcementNote);
						DisplayUtil.displayInfo("Note updated successfully");
					}
				}
				enforcementNote = null;
				reloadNotes();
				FacesUtil.returnFromDialogAndRefresh();
			} catch (RemoteException re) {
				handleException(re);
			}
		}
	}

	public final void cancelEditNote() {
		
		enforcementNote = null;
    	noteReadOnly = false;
    	FacesUtil.returnFromDialogAndRefresh();
	}
	
	public void closeViewNote(ActionEvent actionEvent) {
		enforcementNote = null;
    	noteReadOnly = false;
    	FacesUtil.returnFromDialogAndRefresh();
	}  
    
    public final void noteDialogDone(ReturnEvent returnEvent) {
    	enforcementNote = null;
    	noteReadOnly = false;
    	reloadNotes();
    }
    
    /**
	 * Reload notes for current compliance.
	 */
	private final String reloadNotes() {
		loadNotes(enforcementAction.getEnforcementActionId());
		return FacesUtil.getCurrentPage(); // stay on same page
	}
    
    private void loadNotes(int enforcementActionId) {
        try {
        	List<EnforcementNote> notes = getEnforcementActionService().retrieveEnforcementNotes(enforcementActionId);
        	enforcementAction.setNotes(notes);
        } catch (RemoteException ex) {
            DisplayUtil.displayError("cannot load compliance report notes");
            handleException(ex);
            return;
        }
    }
    
    public final boolean isNoteReadOnly() {
		if (isReadOnlyUser()) {
			return true;
		}

		return noteReadOnly;
	}

	public final void setNoteReadOnly(boolean noteReadOnly) {
		this.noteReadOnly = noteReadOnly;
	}
	
	public final boolean isShowCriterion() {
		boolean ret = false;
		if (EnforcementActionTypeDef.needHPVCriterion(getEnforcementAction().getEnforcementActionType(), logger)) {
			ret = true;
		}
		return ret;
	}
	
	public final boolean isShowFRVType() {
		boolean ret = false;
		if (EnforcementActionTypeDef.needFRVType(getEnforcementAction().getEnforcementActionType(), logger)) {
			ret = true;
		}
		return ret;
	}
	
	public final String startEditAttachment() {
        String ret = null;
        Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
		a.setNewAttachment(false);
        ret= "dialog:editEnforcementActionAttachment";
        return ret;
    }
	
	/* START CODE: ENFORCEMENT ACTION EVENT */

	private final void initializeEnforcementActionEvents() {
		this.enforcementActionEventWrapper = new TableSorter();
		try {
			 if (newEnforcementAction){
 		        this.enforcementAction.setEnforcementActionEventList(null);
 		    } else{ 
				this.enforcementAction
						.setEnforcementActionEventList(getEnforcementActionService()
								.retrieveEnforcementActionEventList(
										this.enforcementAction
												.getEnforcementActionId()));
 		    }
			this.enforcementActionEventWrapper = new TableSorter();
			this.enforcementActionEventWrapper
					.setWrappedData((this.enforcementAction)
							.getEnforcementActionEventList());

		} catch (RemoteException re) {
			logger.error("failed to get Enforcement Action Events", re);
		} finally {
			this.editMode1 = false;
		}
	}

	public final String startToAddEnforcementActionEvent() {
		this.editMode1 = true;
		this.modifyEnforcementActionEvent = new EnforcementActionEvent();
		if (!newEnforcementAction){
			this.modifyEnforcementActionEvent
			.setEnforcementActionId(enforcementAction
					.getEnforcementActionId());
		}
		this.modifyEnforcementActionEvent.setAddedBy(InfrastructureDefs.getCurrentUserId());
		this.newEnforcementActionEvent = true;

		return "dialog:enforcementActionEventDetail";
	}

	public final String startToEditEnforcementActionEvent() {
		int index = this.enforcementActionEventWrapper.getRowIndex();
		this.modifyEnforcementActionEvent = getSelectedEnforcementActionEvent(index);
		//this.modifyEnforcementActionEvent.setAddedBy(InfrastructureDefs.getCurrentUserId());

		this.newEnforcementActionEvent = false;
		this.editMode1 = false;

		return "dialog:enforcementActionEventDetail";
	}

	public EnforcementActionEvent getModifyEnforcementActionEvent() {

		return this.modifyEnforcementActionEvent;
	}

	public void setModifyEnforcementActionEvent(
			EnforcementActionEvent modifyEnforcementActionEvent) {
		this.modifyEnforcementActionEvent = modifyEnforcementActionEvent;
	}

	private final EnforcementActionEvent getSelectedEnforcementActionEvent(
			int index) {
		EnforcementActionEvent cp = (EnforcementActionEvent) this.enforcementActionEventWrapper
				.getRowData(index);

		return cp;
	}

	public boolean isNewEnforcementActionEvent() {
		return newEnforcementActionEvent;
	}

	public void setNewEnforcementActionEvent(boolean newEnforcementActionEvent) {
		this.newEnforcementActionEvent = newEnforcementActionEvent;
	}

	public final void editEnforcementActionEvent() {
		this.editMode1 = true;
		this.newEnforcementActionEvent = false;
	}

	public final void saveEnforcementActionEvent() {

		DisplayUtil.clearQueuedMessages();

		this.getModifyEnforcementActionEvent().setAddedBy(
				InfrastructureDefs.getCurrentUserId());

		if (enforcementActionEventValidated()) {
			if (this.newEnforcementActionEvent) {
				createEnforcementActionEvent();
			} else {
				updateEnforcementActionEvent();
			}
		}
	}
	
	public final boolean isDuplicate() {

		boolean ret = false;

		if (!EnforcementActionEventDef.multiplesAllowed(this
				.getModifyEnforcementActionEvent().getEventCd(), logger)) {

			List<EnforcementActionEvent> events = (this.enforcementAction)
					.getEnforcementActionEventList();

			List<String> eventCodes = new ArrayList<String>();

			for (EnforcementActionEvent eae : events) {
				eventCodes.add(new String(eae.getEventCd()));
				//logger.debug("event cd = " + eae.getEventCd());
			}
			int count = 0;
			for (String item : eventCodes) {
				if (item.equals(this.getModifyEnforcementActionEvent()
						.getEventCd())) {
					count++;
					if (newEnforcementActionEvent || (!newEnforcementActionEvent && count > 1)) {
						logger.debug("Event is already in Tracking Dates table = "
								+ (String) item);
						return true;
					}

				}
			}
		}

		return ret;
	}
	
	public final boolean isNOVClosed() {

		boolean ret = false;

		List<EnforcementActionEvent> events = (this.enforcementAction)
				.getEnforcementActionEventList();

		List<String> eventCodes = new ArrayList<String>();

		for (EnforcementActionEvent eae : events) {
			eventCodes.add(new String(eae.getEventCd()));
			// logger.debug("event cd = " + eae.getEventCd());
		}
		for (String item : eventCodes) {
			if (item.equals(EnforcementActionEventDef.NOV_CLOSED)) {

				return true;

			}
		}

		return ret;
	}

	public void closeEnforcementActionEventDialog() {
		this.refreshEnforcementActionEvents();
		closeDialog();
	}

	protected final void refreshEnforcementActionEvents() {
		this.editMode1 = false;
		this.newEnforcementActionEvent = false;
		this.initializeEnforcementActionEvents();
	}

	public void createEnforcementActionEvent() {

		try {

			getEnforcementActionService().createEnforcementActionEvent(
					this.getModifyEnforcementActionEvent());
			DisplayUtil.displayInfo("Create Enforcement Action Event Success");

		} catch (DAOException e) {
			logger.error("Create Enforcement Action Event Failed", e);
			DisplayUtil.displayError("Create Enforcement Action Event Failed");

		} catch (Exception ex) {
			logger.error("Create Enforcement Action Event Failed", ex);
			DisplayUtil.displayError("Create Enforcement Action Event Failed");

		} finally {
			this.refreshEnforcementActionEvents();
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public void updateEnforcementActionEvent() {

		try {
			// Modify Enforcement Action Event

			getEnforcementActionService().modifyEnforcementActionEvent(
					this.getModifyEnforcementActionEvent());
			DisplayUtil.displayInfo("Create Enforcement Action Event Success");

		} catch (DAOException e) {
			logger.error("Create Enforcement Action Event Failed", e);
			DisplayUtil.displayError("Create Enforcement Action Event Failed");

		//} catch (RemoteException ex) {
		//	logger.error("Create Enforcement Action Event Failed", ex);
		//	DisplayUtil.displayError("Create Enforcement Action Event Failed");

		} catch (Exception ex) {
			logger.error("Create Enforcement Action Event Failed", ex);
			DisplayUtil.displayError("Create Enforcement Action Event Failed");

		} finally {
			this.refreshEnforcementActionEvents();
			closeDialog();
		}
	}

	public void deleteEnforcementActionEvent() {

		DisplayUtil.clearQueuedMessages();
		
		if (!Utility
				.isNullOrEmpty(getDependentEventCd(this.modifyEnforcementActionEvent
						.getEventCd()))) {
			String desc = EnforcementActionEventDef
					.getData()
					.getItems()
					.getItemDesc(
							getDependentEventCd(this.modifyEnforcementActionEvent
									.getEventCd()));
			if (null != desc) {
				DisplayUtil.displayError("Dependent event " + desc
						+ " must be deleted before deleting this event.");
			} else {
				DisplayUtil
						.displayError("Dependent event must be deleted before deleting this event.");
			}

			return;
		}

		try {
			getEnforcementActionService().removeEnforcementActionEvent(
					this.modifyEnforcementActionEvent);
			DisplayUtil.displayInfo("Delete Enforcement Action Event Success");

		} catch (DAOException e) {
			logger.error("Remove Enforcement Action Event failed", e);
			DisplayUtil.displayError("Remove Enforcement Action Event failed");

		} finally {

			refreshEnforcementActionEvents();
			closeDialog();
		}
	}

	public void cancelEnforcementActionEvent() {
		this.refreshEnforcementActionEvents();
		closeDialog();
	}

	public final TableSorter getEnforcementActionEventWrapper() {
		return this.enforcementActionEventWrapper;
	}

	public final void setEnforcementActionEventWrapper(
			TableSorter enforcementActionEventWrapper) {
		this.enforcementActionEventWrapper = enforcementActionEventWrapper;
	}

	public boolean enforcementActionEventValidated() {

		if (this.getModifyEnforcementActionEvent().getEventDate() == null
				|| Utility.isNullOrEmpty(this.getModifyEnforcementActionEvent()
						.getEventDate().toString())) {
			DisplayUtil.displayError("ERROR: Attribute Date is not set.");
			return false;
		}

		if (Utility.isNullOrEmpty(this.getModifyEnforcementActionEvent()
				.getEventCd())) {
			DisplayUtil
					.displayError("ERROR: Attribute Enforcement Action Event is not set.");
			return false;
		}
		
		if (this.isDuplicate()) {
			DisplayUtil
			.displayError("ERROR: Enforcement Action Event already exists. Multiple instances of this event are not allowed.");
			return false;
		}
		
		if(!isValidEventDate(getModifyEnforcementActionEvent().getEventCd(), 
				getModifyEnforcementActionEvent().getEventDate())) {
			return false;
		}
		
		// If user is trying to add event "NOV Closed" make sure certain fields have values.
		if (getModifyEnforcementActionEvent().getEventCd().equals(EnforcementActionEventDef.NOV_CLOSED)) {
			return validateFinal();
		}
		
		return true;
		
	}
	
	public boolean validateFinal() {
		boolean ret = true;
		
		if ((!Utility.isNullOrEmpty(this.getEnforcementAction()
				.getOtherSARequirements()) && this.getEnforcementAction()
				.getOtherSARequirements().equals("Y"))
				&& Utility.isNullOrEmpty(this.getEnforcementAction()
						.getOtherSARequirementsMet())) {

			DisplayUtil
					.displayError("ERROR: Since Other Requirements in SA is set to Yes, Other Requirements in SA Met must have a value.");
			ret = false;
		}

		if (this.getEnforcementAction().getPenaltyAmount() == null
				|| Utility.isNullOrEmpty(this.getEnforcementAction()
						.getPenaltyAmount().toString())) {
			DisplayUtil
					.displayError("ERROR: Penalty Amount must have a value.");
			ret = false;
		}
		if (Utility.isNullOrEmpty(this.getEnforcementAction()
				.getOtherSARequirements())) {
			DisplayUtil
					.displayError("ERROR: Other Requirements in SA must have a value.");
			ret = false;
		}

		if ((Utility.isNullOrEmpty(this.getEnforcementAction()
				.getEnvironmentalImpact()))) {
			DisplayUtil
					.displayError("ERROR: Environmental Impact must have a value.");
			ret = false;
		}

		if ((Utility.isNullOrEmpty(this.getEnforcementAction()
				.getEvidenceAttached()))) {
			DisplayUtil
					.displayError("ERROR: Evidence Attached must have a value.");
			ret = false;
		}
		
		if ((!Utility.isNullOrEmpty(this.getEnforcementAction()
				.getEvidenceAttached()))
				&& this.getEnforcementAction().getEvidenceAttached()
						.equals("Y")
				&& !this.getEnforcementAction().hasAttachments()) {
			DisplayUtil
					.displayError("ERROR: Since Evidence Attached is set to Yes, there must be at least one attachment.");
			ret = false;
		}

		if ((Utility.isNullOrEmpty(this.getEnforcementAction().getSepFlag()))) {
			DisplayUtil
					.displayError("ERROR: Supplemental Environmental Project (SEP)? must have a value.");
			ret = false;
		}

		return ret;
	}

	public final void closeDialog() {
		setEditMode1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public boolean isEditMode1() {
		return editMode1;
	}

	public void setEditMode1(boolean editMode1) {
		this.editMode1 = editMode1;
	}

	public final List<SelectItem> getEnforcementActionEventDefs() {
		return EnforcementActionEventDef.getData().getItems()
				.getItems(getParent().getValue());
	}
	
	public final boolean getEnforcementActionEditAllowed() {
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| InfrastructureDefs.getCurrentUserAttrs()
						.isCurrentUseCaseValid("editEnforcementAction");
	}
	
	public final boolean getEnforcementActionCreateAllowed() {
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| InfrastructureDefs.getCurrentUserAttrs()
						.isCurrentUseCaseValid("createEnforcementAction");
	}

	public final String getEATableWidth() {
		return (EA_WIDTH - 5) + "";
	}

	/**
	 * returns todays date with time set to 23:59:59
	 */
	public Timestamp getTodaysDate() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return new Timestamp(cal.getTimeInMillis());
	}
	
	public boolean isValidEventDate(String eventCd, Timestamp eventDate) {
		boolean ret = true;

		HashMap<String, List<String>> predecessorEventsMap =  EnforcementActionEventDependencyDef.getPredecessorEventsMap(logger);
		if(!predecessorEventsMap.isEmpty()) {
			// debug information only
			logger.debug("Event:Predecessor Events Map");
			Iterator<String> iter = predecessorEventsMap.keySet().iterator();
			while(iter.hasNext()) {
				String event = iter.next();
				List<String> predecessorEvents = predecessorEventsMap.get(event);
				if(null != predecessorEvents) {
					logger.debug("Event " + "[" + event + "]" + " Predecessor Events -> " + predecessorEvents);
				}
			}
			// end debug
			
			// check if the date of the tracking event being added is not before the date of
			// any predecessor tracking event
			// for each predecessor event code get the predecessor event from the tracking table and
			// 1. check that the predecessor event exists
			// 2. check the date of the event being added is not before the date of the predecessor event
			List<EnforcementActionEvent> enfActionEventList = getEnforcementAction().getEnforcementActionEventList();
			List<String> predecessorEventCds = predecessorEventsMap.get(eventCd);
			if(null != predecessorEventCds) {
				for(String cd : predecessorEventCds) {
					boolean predecessorEventExists = false;
					if (cd.equals(eventCd)) {

						DisplayUtil
								.displayError("Selected event type and its configured predecessor event type are identical. Contact IMPACT administrator for assistance.");
						return false;
					}
					if (EnforcementActionEventDef.isDeprecated(cd, logger)) {
						DisplayUtil
								.displayError("Selected event type is configured with an inactive predecessor event type (cd = "
										+ cd
										+ "). Contact IMPACT administrator for assistance.");
						return false;
					}
					for(EnforcementActionEvent eaEvent : enfActionEventList) {
						if(cd.equals(eaEvent.getEventCd())) {
							predecessorEventExists = true;
							if(eventDate.before(eaEvent.getEventDate())) {
								DisplayUtil.displayError("Date should be on or after the most recent "
															+ eaEvent.getEventDesc() + " event date");
								return false;
							}
						}
					}
					if(!predecessorEventExists
							&& !EnforcementActionEventDef.getData().getItems().isItemDepricated(cd)) {
						String desc = EnforcementActionEventDef.getData().getItems().getItemDesc(cd);
						if(null != desc) {
							DisplayUtil.displayError("Predecessor event " + desc + " should be added before adding this event");
						} else {
							DisplayUtil.displayError("Predecessor event should be added before adding this event");
						}
						return false;
					}
				}
			}
			
			// now we need to check if the event being added (or edited) violates the rule
			// i.e., the date of the event should be on or before the date of any other
			// event for which this event is a predecessor event.
	
			// for each dependent event code get the dependent event from the tracking table and
			// if the dependent event exists, then check the date of this event is not after the
			// date of the dependent event.
			
			List<String> dependentEventCds = getDependentEventCdList(eventCd);
			
			if(null != dependentEventCds && dependentEventCds.size() != 0) {
				for(String cd : dependentEventCds) {
					for(EnforcementActionEvent eaEvent : enfActionEventList) {
						if(cd.equals(eaEvent.getEventCd())
								&& eventDate.after(eaEvent.getEventDate())) {
							DisplayUtil.displayError("Date should be on or before the oldest "
														+ eaEvent.getEventDesc() + " event date");
							return false;
						}
					}
				}
			}
		}

		return ret;
	}
	
	public String getDependentEventCd(String eventCd) {
		String ret = null;
		List<String> dependentEventCds = getDependentEventCdList(eventCd);
		List<EnforcementActionEvent> enfActionEventList = getEnforcementAction()
				.getEnforcementActionEventList();

		if ((null != dependentEventCds && dependentEventCds.size() != 0) && (null != enfActionEventList && enfActionEventList.size() != 0)) {
			for (String cd : dependentEventCds) {
				for (EnforcementActionEvent eaEvent : enfActionEventList) {
					if (cd.equals(eaEvent.getEventCd())) {
						ret = cd;
						return ret;
					}
				}
			}
		}
		return ret;
	}
	
	public List<String> getDependentEventCdList(String eventCd) {

		List<String> dependentEventCds = new ArrayList<String>();
		HashMap<String, List<String>> predecessorEventsMap = EnforcementActionEventDependencyDef
				.getPredecessorEventsMap(logger);
		if (!predecessorEventsMap.isEmpty()) {
			// debug information only
			logger.debug("Event:Predecessor Events Map");
			Iterator<String> iter = predecessorEventsMap.keySet().iterator();
			while (iter.hasNext()) {
				String event = iter.next();
				List<String> predecessorEvents = predecessorEventsMap
						.get(event);
				if (null != predecessorEvents) {
					logger.debug("Event " + "[" + event + "]"
							+ " Predecessor Events -> " + predecessorEvents);
				}
			}
			// end debug

			// create a list of events that depend on this event
			// i.e., events for which this event is a predecessor event

			iter = predecessorEventsMap.keySet().iterator();
			while (iter.hasNext()) {
				String event = iter.next();
				List<String> predecessorEvents = predecessorEventsMap
						.get(event);
				if (null != predecessorEvents
						&& predecessorEvents.contains(eventCd)) {
					dependentEventCds.add(event);
				}
			}
			logger.debug("Found following dependent events "
					+ dependentEventCds + " for event " + "[" + eventCd + "]");

		}
		return dependentEventCds;
	}
	

	/* END CODE: ENFORCEMENT ACTION EVENT */

	public boolean isHasCorrespondences() {
		return hasCorrespondences;
	}

	public void setHasCorrespondences(boolean hasCorrespondences) {
		this.hasCorrespondences = hasCorrespondences;
	}
	
	//***************

	public String from2ndLevelMenu() {
        submit();

        return ENFORCEMENT_TAG;
    }
    
    public final String submit() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        setFromTODOList(false);
        try {
            return submitInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final String submitInternal() {
    	reset();
        String ret = null;
        boolean ok = true;
        
        if (enforcementActionId != null) {
            
            try {
                enforcementAction = getEnforcementActionService().retrieveEnforcementAction(enforcementActionId);
                facilityId = enforcementAction.getFacilityId();

                if (enforcementAction != null) {
                    initializeAttachmentBean();
                    this.refreshEnforcementActionEvents();
                    this.setCorrespondences();
                    
                } else {
                    ok = false;
                    
                    DisplayUtil.displayError("Enforcement Action is not found with that number.");
                }
            } catch (RemoteException e) {
                handleException("Failed to retrieve Enforcement Action " + enforcementAction, e);
                ok = false;
            }
            if(ok) {
                ret = ENFORCEMENT_TAG;
                ((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_enforcementActionDetail")).setDisabled(false);
            } else return null;
        } else {
			try {
				enforcementAction = new EnforcementAction();
				enforcementAction.setFacilityId(facilityId);
				initializeAttachmentBean();
				this.refreshEnforcementActionEvents();
				this.setCorrespondences();
			} catch (RemoteException e) {
				handleException("Failed to retrieve Enforcement Action "
						+ enforcementAction, e);
				ok = false;
			}
        }
        
		// load facility inventory if it hasn't already been loaded
		if (facilityId != null) {
			FacilityProfile fp = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			if (fp.getFacilityId() == null
					|| !fp.getFacilityId().equals(facilityId)) {
				fp.setFacilityId(facilityId);
				fp.submitProfileById();
			}
		}
		
        return ret;
    }
    
	public boolean isNewEnforcementAction() {
		return newEnforcementAction;
	}

	public void setNewEnforcementAction(boolean newEnforcementAction) {
		this.newEnforcementAction = newEnforcementAction;
	}
    
	public boolean isFromStackTest() {
		return fromStackTest;
	}

	public void setFromStackTest(boolean fromStackTest) {
		this.fromStackTest = fromStackTest;
	}

	public boolean isFromInspection() {
		return fromInspection;
	}

	public void setFromInspection(boolean fromInspection) {
		this.fromInspection = fromInspection;
	}
}
