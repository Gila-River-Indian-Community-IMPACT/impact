package us.oh.state.epa.stars2.database.dbObjects.ceta;
// Moved this file to package us.oh.state.epa.stars2.app.ceta; where it belongs
/*
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.EnforcementActionService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EnforcementActionReferralTypeDef;
import us.oh.state.epa.stars2.def.EnforcementActionStateDef;
import us.oh.state.epa.stars2.def.EnforcementActionTypeDef;
import us.oh.state.epa.stars2.def.EnforcementAttachmentTypeDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;


@SuppressWarnings("serial")
public class EnforcementActionDetail extends TaskBase implements IAttachmentListener{

	private String facilityId;
	private Facility curFacility;
	private Integer enforcementActionId;
	private EnforcementAction enforcementAction;
	private String facilityName;
	private String companyName;
	
	private EnforcementActionService enforcementActionService;
	private FacilityService facilityService;
	private boolean editable;
	
	protected String popupRedirect = null;
	
	private EnforcementNote enforcementNote;
	private boolean noteReadOnly;
	
	public EnforcementActionDetail() {
		super();
		enforcementAction = new EnforcementAction();
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

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public final String startNewEnforcementAction() {
		enforcementAction = new EnforcementAction();
		editable = true;
		FacilityProfile facProfile = (FacilityProfile)FacesUtil.getManagedBean("facilityProfile");
		try {
			
			setCompanyName(facProfile.getFacility().getOwner().getCompany().getName());
			setFacilityName(facProfile.getFacility().getName());
			enforcementAction.setFacilityId(facProfile.getFacilityId());
			enforcementAction.setCreatorId(InfrastructureDefs.getCurrentUserId());
			enforcementAction.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			enforcementAction = enforcementActionService.createEnforcementAction(enforcementAction);
			enforcementAction = enforcementActionService.retrieveEnforcementAction(enforcementAction.getEnforcementActionId());
			setEnforcementActionId(enforcementAction.getEnforcementActionId());
		}catch(Exception ex) {
			DisplayUtil.displayError("Failed to create enforcement action");
			logger.error(ex.getMessage(), ex);
		}
		
		((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_enforcementActionDetail")).setDisabled(false);
		return "dialog:enforcementActionDetail";
	}
	
	public final String saveEnforcementAction(ActionEvent event) {
		if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
		boolean ok = false;
		try {
			ok = enforcementActionService.modifyEnforcementAction(enforcementAction);
			if(ok) {
				// delete and add existing xref entries
				enforcementActionService.deleteReferralTypeById(enforcementActionId);
				for(String referralTypeCd : enforcementAction.getReferralTypes()) {
					enforcementActionService.createReferalType(referralTypeCd, enforcementActionId);
				}
			}
		}catch(Exception ex) {
			DisplayUtil.displayError("Failed to update enforcement action");
			logger.error(ex.getMessage(), ex);
		} finally {
			clearButtonClicked();
		}
		
		editable = false;
		return "dialog:enforcementActionDetail";
	}
	
	public final void startEditAction() {
		editable = true;
	}
	
	public final String cancelEditAction(ActionEvent actionEvent) {
		enforcementAction = null;
		editable = false;
		try {
			enforcementAction = enforcementActionService.retrieveEnforcementAction(enforcementActionId);
		}catch(Exception ex) {
			DisplayUtil.displayError("Failed to retrieve enforcement action");
			logger.error(ex.getMessage(), ex);
		}
		return "dialog:enforcementActionDetail";
	}

	public final DefSelectItems getEnforcementActionReferralTypeDef() {
        return EnforcementActionReferralTypeDef.getData().getItems();
    }
	
	public final DefSelectItems getEnforcementActionTypeDef() {
		return EnforcementActionTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getEnforcementActionStateDef() {
		return EnforcementActionStateDef.getData().getItems();
	}
	
	
	//private void setCorrespondences() throws RemoteException {
    //	Correspondence[] correspondence = getCorrespondenceService().searchCorrespondenceByLinkedToId(enforcement.getEnforcementId());
	//	CorrespondenceSearch cs = (CorrespondenceSearch) FacesUtil.getManagedBean("correspondenceSearch");
	//	cs.setCorresForLinkedByTo(correspondence);
   // }
    
    
    public String from2ndLevelMenu() {
    	
        if(enforcementAction != null) {
        	initializeAttachmentBean();
        	//try {
        	//	setCorrespondences();
        	//} catch (RemoteException e) {
    		//	handleException(e);
    		//}
        }
        //enforcementAction = null;
		//evaluators = null;
		
        return ENFORCEMENT_TAG;
    }
    
    @Override
	public Integer getExternalId() {
		return enforcementActionId;
	}

	@Override
	public void setExternalId(Integer externalId) {
		//this.setEnforcementActionId(externalId, true);
	}
	
	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		
		return messages;
	}
	
	@Override
	public String findOutcome(String url, String ret) {
		return submitEnforcementAction();
	}
	
	public final void reset() {
		enforcementAction = null;
		editable = false;
		//setEnfDiscoveryIdName(null);
	}
	
	public final void setEnforcementActionId(Integer enforcementActionId) {
    	setEnforcementActionId(enforcementActionId, false);
    }

	private final void setEnforcementActionId(Integer enforcementActionId, boolean curFacFlag) {
		reset();
		try {
			this.enforcementActionId = enforcementActionId;
			if (enforcementActionId != null) {
				enforcementAction = getEnforcementActionService()
						.retrieveEnforcementAction(enforcementActionId);
				//setEnfDiscoveryIdName(null);
				//violationTypeCodes = Stars2Object.toStar2Object(enforcement
				//		.getViolationTypeCodes());
				facilityId = enforcementAction.getFacilityId();
				if (curFacFlag) {
					curFacility = getFacilityService().retrieveFacility(facilityId);
				}
				//setCorrespondences();
			} else {
				enforcementAction = new EnforcementAction();
				//setEnfDiscoveryIdName(null);
				enforcementAction.setFacilityId(facilityId);
			}

			//if (enforcement.isPropSettleAmtFlag() && enforcement.getPropSettleAmt() == null) {
			//	DisplayUtil.displayInfo("The proposed Settlement Amount is not set. The amount may be needed to resolve the enforcement. Please update the enforcement.");
			//}

			// load facility inventory if it hasn't already been loaded
			//if (facilityId != null) {
			//	FacilityProfile fp = (FacilityProfile) FacesUtil
			//			.getManagedBean("facilityProfile");
			//	if (fp.getFacilityId() == null
			//			|| !fp.getFacilityId().equals(facilityId)) {
			//		fp.setFacilityId(facilityId);
			//		fp.submitProfileById();
			//	}
			//}

		} catch (RemoteException e) {
			handleException(e);
		}
	}

	public final Integer getEnforcemenActionId() {
		return enforcementActionId;
	}
	
	public final String submitEnforcementAction() {  
		String ret = null;
		popupRedirect = null;
		if (enforcementAction != null) {
			try {
				curFacility = getFacilityService().retrieveFacility(facilityId);
				initializeAttachmentBean();
				ret = ENFORCEMENT_TAG;
			} catch (RemoteException e) {
				handleException(e);
			}
		} else {
			DisplayUtil.displayError("Unable to retrieve data for selected enforcement action.");
			logger.error("Enforcement Action is not set in call to submitEnforcementAction");
		}
		return ret;
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
	
	public final Facility getCurFacility() {
		return curFacility;
	}

	public final void setCurFacility(Facility curFacility) {
		this.curFacility = curFacility;
	}
	
	public boolean isLocked() {
		boolean ret = true;
	
		 //This returns true if the record has been closed. It ensures
		 //fields are not editable by AQD unless they are Admin
		 //once the enforcement action has been closed
		
		if (isAdmin()) {
			ret = false;
		} else if ((!EnforcementActionStateDef.CLOSED.equalsIgnoreCase(enforcementAction
				.getEnforcementActionStateCd()))
				&& !isReadOnlyUser()) {
			ret = false;
		}

		return ret;
	}
	
	public boolean isAdmin() {
		boolean ret = false;

		ret = InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();

		return ret;
	}
	
	public void initializeAttachmentBean() {
		Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
		a.addAttachmentListener(this);
		a.setStaging(false);
		a.setNewPermitted(isCetaUpdate());
		a.setUpdatePermitted(isCetaUpdate());
		a.setFacilityId(enforcementAction.getFacilityId());
		a.setSubPath("EnforcementAction");
		a.setObjectId(Integer.toString(enforcementAction.getEnforcementActionId()));
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
			if (attachment.getDocument().getFileName() == null) {
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

				enforcementAction = getEnforcementActionService().retrieveEnforcementAction(
						enforcementAction.getEnforcementActionId());
				//setEnfDiscoveryIdName(null);
				Attachments a = (Attachments) FacesUtil
						.getManagedBean("attachments");
				a.setAttachmentList(enforcementAction.getAttachments().toArray(
						new Attachment[0]));
				attachment.cleanup();
				FacesUtil.returnFromDialogAndRefresh();
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
}
*/
