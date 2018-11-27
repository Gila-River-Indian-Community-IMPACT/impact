package us.oh.state.epa.stars2.webcommon.correspondence;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.app.workflow.ActivityProfile;
import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.EnforcementActionService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.CorrespondenceAttachmentTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@SuppressWarnings("serial")
public class CorrespondenceDetail extends TaskBase implements IAttachmentListener{

    static private final String desName = "User uploaded Document";
	private static final String CORR_TAG = "correspondence";
    private Integer correspondenceID;
    private Correspondence correspondence;
    private boolean editable;
    private String facilityID;
    private String savedDesName;
    private String useDescription;
    private List<EnforcementAction> linkedToObjs;
    private String linkedToObj;
    
    private CorrespondenceService correspondenceService;
    private DocumentService documentService;
    private EnforcementActionService enforcementActionService;
    private FacilityService facilityService;
    private ReadWorkFlowService workFlowService;
    
    private String pageRedirect;
    public static String DETAIL_OUTCOME="correspondence.detail";
    
    private String associatedWithFacility;
    private boolean allowedToChangeFacility;
    private boolean deleteAllowed;
    private String disableDeleteMsg;
    
    public EnforcementActionService getEnforcementActionService() {
		return enforcementActionService;
	}
	public void setEnforcementActionService(EnforcementActionService enforcementActionService) {
		this.enforcementActionService = enforcementActionService;
	}
    
    public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

    
    public CorrespondenceDetail() {
        super();
    }

    public CorrespondenceService getCorrespondenceService() {
		return correspondenceService;
	}

	public void setCorrespondenceService(CorrespondenceService correspondenceService) {
		this.correspondenceService = correspondenceService;
	}

	public final Correspondence getCorrespondence() {        
        return correspondence;
    }

    public final void setCorrespondence(Correspondence correspondence) {
        this.correspondence = correspondence;
    }

    public final Integer getCorrespondenceID() {
        return correspondenceID;
    }

    public final void setCorrespondenceID(Integer correspondenceID) {
        this.correspondenceID = correspondenceID;
    }

    public final boolean isEditable() {
        return editable;
    }

    public final void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getFacilityID() {
		return facilityID;
	}

	public void setFacilityID(String facilityID) {
		this.facilityID = facilityID;
	}

	public Integer getEnforcementActionId() {
		if (linkedToObj == null || linkedToObj.equals("")) {
			return 0;
		} else {
			return new Integer(linkedToObj);
		}
	}

	public void setEnforcementActionId(Integer enforcementId) {
		if (enforcementId == null || enforcementId.equals(0)) {
			linkedToObj = "";
		} else {
			linkedToObj = enforcementId.toString();
		}
		correspondence.setLinkedToId(enforcementId);
	}

	public String getLinkedToObj() {
		return linkedToObj;
	}

	public void setLinkedToObj(String linkedToObj) {
		this.linkedToObj = linkedToObj;
	}

	public List<EnforcementAction> getLinkedToObjs() {
		return linkedToObjs;
	}

	public void setLinkedToObjs(List<EnforcementAction> linkedToObjs) {
		this.linkedToObjs = linkedToObjs;
	}

    public final String submitFromJsp() { // from Search Datagrid
    	String rtn = submit(true);
        createLinkedToObjs();
		setFromTODOList(false);
        return rtn;
    }

    // submit
    public final String submit(boolean readOnlyDB) { 
        savedDesName = null;
        associatedWithFacility = null;
        allowedToChangeFacility = false;
		try {
			correspondence = getCorrespondenceService().retrieveCorrespondence(correspondenceID);
			if(correspondence == null) {
				DisplayUtil.displayError("No correspondence found with id: "
						+ correspondenceID + ".");
				return FAIL;
			} else {
		        
		        if(correspondence != null) {
		        	initializeAttachmentBean();
		        }
		        
	            if(correspondence.getInspectionsReferencedIn().size() > 0){
	            	setDeleteAllowed(false);
	            	setDisableDeleteMsg("You cannot delete this Correspondence while it is being referenced in other places.");
	            } else {
	            	setDeleteAllowed(true);
	            	setDisableDeleteMsg("");
	            }
			}
	    } catch (RemoteException re) {
	    	DisplayUtil.displayError("Retrieval of Correspondence failed");
	        logger.error(re.getMessage(), re);
	    }
	 
        useDescription = desName;
        if(savedDesName != null) {
        	useDescription = savedDesName; 
        }
        ((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_correspondenceDetail")).setDisabled(false);
        return SUCCESS;
    }
    public boolean isAllowTypeUpdate() {
        boolean rtn = false;
        if(isStars2Admin() && isEditable()) rtn = true;
        return rtn;
    }
    
    public boolean isAllowFileChange() {
        boolean rtn = true;
        String types = ";3;11;14;15;20;70;71;72;73;74;75;76;77;84;85;86;87;111;114;115;116;117;118;119;120;121;122;123;124;125;126;127;128;129;130;131;132;133;134;136;137;";
        String cd = ";" + correspondence.getCorrespondenceTypeCode() + ";";
        if(types.contains(cd)) rtn = false;
        return rtn;
    }

    public final void editCorrespondence() {
        setEditable(true);
    }

    public final void cancelEdit() {
        setEditable(false);
        submit(false);  // recover existing values
    }
    
    public final void deleteFile() {
       
    }

    public final String saveEditCorrespondence() {
        boolean operationOK = true;
        
    	// clean up orphan data
    	cleanOrphanData();
        
        String errorClientIdPrefix = "correspondence:";
        
        if(displayValidationMessages(errorClientIdPrefix,
				correspondence.validate())) {
        	operationOK = false;
        }
        
        if(operationOK) {
            try {
                // if correspondence is associated with a facility then update the district information
    			if(associatedWithFacility == "Y" && correspondence.getFacilityID() != null) {
    				correspondence.setDistrict(facilityService.retrieveFacility(correspondence.getFacilityID()).getDoLaaCd());
    			}
            	getCorrespondenceService().modifyCorrespondence(correspondence);

                submit(false);  //   Retrieve new object
            } 
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                operationOK = false;
            }
            setEditable(false);
            if (operationOK) {
                DisplayUtil.displayInfo("Correspondence updated successfully");
            }
            else {
                cancelEdit();
                DisplayUtil.displayError("Correspondence update failed");
            }
        }
        return SUCCESS;
    }
    
    private void cleanOrphanData() {
    	if(!correspondence.isFollowUpAction()) {
    		correspondence.setFollowUpActionDate(null);
    		correspondence.setFollowUpActionDescription(null);
    	}
    	
    	if(associatedWithFacility == "Y" ) {
    		correspondence.setDistrict(null);
			correspondence.setCountyCd(null);
			correspondence.setCityCd(null);
		} else if(associatedWithFacility == "N" ) {
			correspondence.setFacilityID(null);
		}
    	
    	if(!getCorrespondence().isLinkedtoEnfAction()) {
    		getCorrespondence().setLinkedToId(null);
    		//getCorrespondence().setHideAttachments(false);  // DO NOT reset, in case correspondence is
    		                                                  // ever re-linked to an enforcement action.
    														  // Require EA admin to manually set it to
    		                                                  // false.  This is the more conservative 
    		                                                  // approach.
    	}
    	
    	/*if(correspondence.isIncoming()) {
    		correspondence.setDateGenerated(null);
    	} 
    	
    	if(correspondence.isOutgoing()) {
    		correspondence.setReceiptDate(null);
    	}*/
    	
		/*if (isAllowedToChangeFacilityId()) {
			if (!Utility.isNullOrEmpty(correspondence.getFacilityID())) {
				if (isAssociated()
						&& !Utility.isNullOrEmpty(correspondence.getDistrict())) {
					correspondence.setDistrict(null);
				}
			}
		}*/
    }
    
    public final void deleteCorrespondence(ActionEvent actionEvent) {
    	try {
			getCorrespondenceService().deleteCorrespondence(correspondence);
			correspondenceID = null;
			DisplayUtil.displayInfo("Correspondence has been deleted.");
	    } catch (RemoteException re) {
	    	handleException("exception in Correspondence " + correspondence.getCorrespondenceID(), re);
	    }
    	((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_correspondenceDetail")).setDisabled(true);
    	this.setPageRedirect(CorrespondenceSearch.SEARCH_OUTCOME);
	    FacesUtil.returnFromDialogAndRefresh();
    }
    
    private int getUserID(){
    	return InfrastructureDefs.getCurrentUserId();
    }
    
    public final String reset() {
		correspondence = new Correspondence();	
        correspondence.setDateGenerated(new Timestamp(System.currentTimeMillis()));			
		return SUCCESS;
	}
    
    private void createLinkedToObjs() {
    	if (correspondence == null) {
    		linkedToObj = "";
    	} else if (correspondence.getLinkedToTypeCd() == null || correspondence.getLinkedToId() == null || correspondence.getLinkedToId().equals(0)) {
    		linkedToObj = "";
    	} else {
    		linkedToObj = correspondence.getLinkedToId().toString();
    	}
    }
    
    public String linkToEnforcement(){
    	try {
    		// createLinkedToObjs(true);
    		linkedToObjs = getEnforcementActionService().searchEnforcementActions(correspondence.getFacilityID(), null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    	} catch(RemoteException ex){
    	    DisplayUtil.displayError("Failed to create new Correspondence ");
    	    logger.error(ex.getMessage(), ex);
    	    return null;
    	}
    	return "dialog:linkToEnforcement";
    }
    
    public String linkToUpdateEnforcement(){
    	try {
    		//createLinkedToObjs(false);
    		linkedToObjs = getEnforcementActionService().searchEnforcementActions(correspondence.getFacilityID(), null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    	} catch(RemoteException ex){
    	    DisplayUtil.displayError("Failed to create new Correspondence ");
    	    logger.error(ex.getMessage(), ex);
    	    return null;
    	}
    	return "dialog:linkToEnforcement";
    }
    
    public final void cancelLinkToObj(ActionEvent actionEvent) {
    	FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void updateLinkToObj(ActionEvent actionEvent) {
    	boolean operationOK = true;
    	if (correspondence.getCorrespondenceID() != null) {
    		try {
                getCorrespondenceService().modifyCorrespondence(correspondence);
                submit(false);
                createLinkedToObjs();
            } 
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                operationOK = false;
            }
            if (operationOK) {
                DisplayUtil.displayInfo("Link to enforcement updated successfully");
            }
            else {
                cancelEdit();
                DisplayUtil.displayError("Link to enforcement update failed");
            }
    	}
    	FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void clearLinkToObj() {
    	boolean operationOK = true;

    	try {
    		correspondence.setLinkedToId(null);
    		getCorrespondenceService().modifyCorrespondence(correspondence);
    		submit(false);
    		createLinkedToObjs();
        } 
        catch (Exception ex) {
        	logger.error(ex.getMessage(), ex);
            operationOK = false;
        }
        if (operationOK) {
        	DisplayUtil.displayInfo("Link to enforcement updated successfully");
    	} else {
        	cancelEdit();
        	DisplayUtil.displayError("Link to enforcement update failed");
    	}   	
    }
    
    public final String newCorrespondence(){    	
    	correspondence = new Correspondence();    	
    	correspondence.setFacilityID(facilityID);
    	correspondence.setDateGenerated(new Timestamp(System.currentTimeMillis()));
    	linkedToObj = "";
    	return "dialog:createCorrespondence";
	}
    
    public final boolean isShowLinkedToObj() {
    	boolean ret = false;
    	if (correspondence != null && "enf".equals(correspondence.getLinkedToTypeCd())) {
    		ret = true;
    	}
    	return ret;
    }
	
	public final void createCorrespondence(ActionEvent event){
    	boolean ok = true;
    	try{
    	    if(correspondence.getFacilityID() != null &&
    	            correspondence.getFacilityID().length() > 0){
    	        if(correspondence.getCorrespondenceTypeCode() == null){   				
    	            DisplayUtil.displayError("Enter Correspondence type");
    	            ok = false;
    	        }

    	    } else {
    			DisplayUtil.displayError("Facility Id not set");
    			ok = false;
    		}
    		
    	    if(ok){
    	        correspondence = getCorrespondenceService().createCorrespondence(correspondence);

    	        if(correspondence.getCorrespondenceID() == null){
    	            DisplayUtil.displayError("Failed to create Correspondence");  				
    	        }else{
    	            DisplayUtil.displayInfo("Correspondence created");
    	            FacilityProfile facProfile = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
    	            facProfile.getCorrespondence();
    	        }
    	        
    	        FacesUtil.returnFromDialogAndRefresh();
    	    }
    	} catch(Exception ex){
    	    DisplayUtil.displayError("Failed to create Correspondence ");
    	    logger.error(ex.getMessage(), ex);
    	    ok = false;
    	}
	}	
	
	public String getCorrespondenceTypeCode() {
		if (correspondence != null) {
			return correspondence.getCorrespondenceTypeCode();
		} else {
			return "";
		}
	}
	
	public void setCorrespondenceTypeCode(String correspondenceTypeCode) {
		if (correspondence == null) {
			return;
		}
		correspondence.setCorrespondenceTypeCode(correspondenceTypeCode);
		if (!isShowLinkedToObj()) {
			correspondence.setLinkedToId(null);
		}
	}
	
	public String refreshCorrespondenceDetail() {
		if(correspondenceID != null) {
			if (FAIL == submit(false)) {
				return null;
			}
			setFromTODOList(false);
		}
		return DETAIL_OUTCOME;
	}
	
	public String getPageRedirect() {
		if (pageRedirect != null) {
			FacesUtil.setOutcome(null, pageRedirect);
			pageRedirect = null;
		}
		return null;
	}
	
	public void setPageRedirect(String pageRedirect) {
		this.pageRedirect = pageRedirect;
	}
	
	public Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	public Timestamp getMinFollowUpDate() {
		Timestamp tomorrow = null;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		tomorrow = new Timestamp(cal.getTimeInMillis());
		return tomorrow;
	}
	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		
		// add correspondence workflow validations
		
		return messages;
	}
	@Override
	public String findOutcome(String url, String ret) {
		ActivityProfile ap = (ActivityProfile) FacesUtil
				.getManagedBean("activityProfile");
		WorkFlowProcess p = ap.getProcess();
		if(p != null) {
			correspondenceID = p.getExternalId();
			if (FAIL == submit(true)) {
				return null;
			}
			return DETAIL_OUTCOME;
		}
		return null;
	}
	@Override
	public Integer getExternalId() {
		return correspondenceID;
	}
	@Override
	public void setExternalId(Integer externalId) {
		correspondenceID = externalId;
		
	}
	
	@Override
	public String toExternal() {
		boolean fromToDo = this.getFromTODOList();
		String ret = refreshCorrespondenceDetail();
		setFromTODOList(fromToDo);
		return ret;
	}	
	
	@Override
	public String getExternalName(ProcessActivity activity) {
		return "Correspondence";
	}

	@Override
	public String getExternalNum() {
		if(correspondenceID == null) return null;
		return correspondenceID.toString();
	}

	@Override
	public String getValidationDlgReference() {
		return CORR_TAG;
	}
	
	public String getAssociatedWithFacility() {
		if(!Utility.isNullOrEmpty(correspondence.getFacilityID()) && !isAllowedToChangeFacilityId()) {
			associatedWithFacility="Y";
		} else if(!Utility.isNullOrEmpty(correspondence.getDistrict()) && !isAllowedToChangeFacilityId()) {
			associatedWithFacility="N";
		}
		return associatedWithFacility;
	}

	public void setAssociatedWithFacility(String associatedWithFacility) {
		if(AbstractDAO.translateIndicatorToBoolean(associatedWithFacility)) {
			setAllowedToChangeFacility(true);
		} else {
			correspondence.setFacilityID(null);
			setAllowedToChangeFacility(false);
		}
		
		this.associatedWithFacility = associatedWithFacility;
	}
	
	public boolean isAssociated() {
		boolean ret = false;
		
		ret = AbstractDAO.translateIndicatorToBoolean(getAssociatedWithFacility());
		
		return ret;
	}
	
	public boolean isAllowedToChangeFacilityId() {
		return allowedToChangeFacility;
	}
	
	public void setAllowedToChangeFacility(boolean allowedToChangeFacility) {
		this.allowedToChangeFacility = allowedToChangeFacility;
	}
	public boolean isDeleteAllowed() {
		return deleteAllowed;
	}
	public void setDeleteAllowed(boolean deleteAllowed) {
		this.deleteAllowed = deleteAllowed;
	}
	
	
	public void initializeAttachmentBean() {
		Attachments attachments = (Attachments) FacesUtil.getManagedBean("attachments");
		attachments.addAttachmentListener(this);
		
		attachments.setSubPath("Correspondence");
		
		attachments.setFacilityId(correspondence.getFacilityID());
		
		attachments.setAttachmentTypesDef(CorrespondenceAttachmentTypeDef.getData()
                .getItems());
		
		
		attachments.setNewPermitted(true);
		attachments.setUpdatePermitted(true);
		attachments.setDeletePermitted(true);
		
		attachments.setHasDocType(true);
		
		try {
			attachments.setAttachmentList(getCorrespondenceService()
					.retrieveCorrespondenceAttachments(correspondenceID));
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Cannot access correspondence attachments");
		}
    }
	
	public AttachmentEvent createAttachment(Attachments attachments) {
		boolean ok = true;
        if (attachments.getDocument() == null) {
            // should never happen
            logger.error("Attempt to process null attachment");
            ok = false;
        } else {
        	/*if (attachments.getPublicAttachmentInfo() == null
                    && attachments.getFileToUpload() != null) {
                attachments.setPublicAttachmentInfo(new UploadedFileInfo(
                        attachments.getFileToUpload()));
            }*/
        	
        	// make sure document description and type are provided
            if (attachments.getDocument().getDescription() == null || attachments.getDocument().getDescription().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify the description for this attachment");
                ok = false;
            }
            if (attachments.getDocument().getDocTypeCd() == null || attachments.getDocument().getDocTypeCd().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify an attachment type for this attachment");
                ok = false;
            }
            // make sure document file is provided
            /*if (attachments.getPublicAttachmentInfo() == null
					&& attachments.getTempDoc().getDocumentID() == null) {
				DisplayUtil
						.displayError("Please specify the file to upload for this attachment");
				ok = false;
			}*/
            if(attachments.getFileToUpload() == null) {
            	DisplayUtil
				.displayError("Please specify the file to upload for this attachment");
            	ok = false;
            }
        }
		
		if(ok) {
			try {
				attachments.getTempDoc().setObjectId(null);
				getCorrespondenceService().createCorrespondenceAttachment(correspondenceID, attachments.getTempDoc(),
						attachments.getFileToUpload().getInputStream());
				attachments.setAttachmentList(getCorrespondenceService()
						.retrieveCorrespondenceAttachments(correspondenceID));
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Create correspondence attachment failed");
			} catch (IOException ioe) {
				handleException(new RemoteException(ioe.getMessage()));
				DisplayUtil.displayError("Create correspondence attachment failed");
			}
			FacesUtil.returnFromDialogAndRefresh();
		}
		
		return null;
	}

	public void cancelAttachment() {
		Attachments attachments = (Attachments) FacesUtil.getManagedBean("attachments");
		try {
			attachments.setAttachmentList(getCorrespondenceService()
					.retrieveCorrespondenceAttachments(correspondenceID));
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Refreshing correspondence attachment(s) failed");
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public AttachmentEvent deleteAttachment(Attachments attachments) {
		try {
			getCorrespondenceService().removeCorrespondenceAttachment(attachments.getTempDoc());
			attachments.setAttachmentList(getCorrespondenceService()
					.retrieveCorrespondenceAttachments(correspondenceID));
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Delete facility attachment failed");
		}
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public AttachmentEvent updateAttachment(Attachments attachments) {
		boolean ok = true;
		
		// make sure document description is provided
        if (attachments.getDocument().getDescription() == null || attachments.getDocument().getDescription().trim().equals("")) {
            DisplayUtil
                    .displayError("Please specify the description for this attachment");
            ok = false;
        }
        if(ok) {
			try {
				getCorrespondenceService().updateCorrespondenceAttachment(attachments.getTempDoc());
				attachments.setAttachmentList(getCorrespondenceService()
						.retrieveCorrespondenceAttachments(correspondenceID));
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Update correspondence attachment failed");
			}
			FacesUtil.returnFromDialogAndRefresh();
        }
		return null;
	}
	
	public FacilityService getFacilityService() {
		return facilityService;
	}
	
	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
	public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}
	
	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}
	
	public boolean isActiveWorkflowProcess() { 
		Integer processTemplateId = 96;
		Integer externalId = getCorrespondenceID();
    	String errMsg = "Error occurred while checking if an active " +
    			"workflow process exists for this Correspondence; " +
    			"correspondence deletion will not be allowed.";
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
	
	public List<SelectItem> getFacilityEnforcementAction() {
		List<SelectItem> si = new ArrayList<SelectItem>();
		String idTemplate = "F000000";
		String inputFacilityId = getCorrespondence().getFacilityID(); // facility id as entered by the user on the UI
		if(null != inputFacilityId && inputFacilityId.length() <= idTemplate.length()) {
			// sometimes the user may specify just the numeric part of the facility id, so
			// convert the user entered facility id to FXXXXXX format before doing the search
			String facilityId = idTemplate.substring(0, idTemplate.length() - inputFacilityId.length()) + inputFacilityId;
			if(null != facilityId) {
				try {
					EnforcementAction[] eaList = enforcementActionService.retrieveEnforcementActionByFacilityId(facilityId);
					for(EnforcementAction ea : eaList) {
						si.add(new SelectItem(ea.getEnforcementActionId(), ea.getEnfId()));
					}
				}catch(DAOException daoe) {
					handleException(daoe);
				}
			}
		}
		return si;
	}
	
	public boolean getDisplayReferencedInspectionsTable() {
		return !isDeleteAllowed() && !correspondence.getInspectionsReferencedIn().isEmpty();
	}
	
	public String getDisableDeleteMsg() {
		return disableDeleteMsg;
	}
	public void setDisableDeleteMsg(String disableDeleteMsg) {
		this.disableDeleteMsg = disableDeleteMsg;
	}
}
