package us.wy.state.deq.impact.app.continuousMonitoring;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.event.ReturnEvent;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.wy.state.deq.impact.bo.ContinuousMonitorService;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorEqt;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorNote;

@SuppressWarnings("serial")
// public class ContinuousMonitorDetail extends TaskBase implements
// IAttachmentListener{
public class ContinuousMonitorDetail extends TaskBase {

	private String facilityId;
	private Integer continuousMonitorId;
	private ContinuousMonitor continuousMonitor;
	private String facilityName;
	private String companyName;

	private ContinuousMonitorService continuousMonitorService;
	private FacilityService facilityService;
	private boolean editable;
	private boolean editMode1;

	protected String popupRedirect = null;

	private ContinuousMonitorNote continuousMonitorNote;
	private boolean noteReadOnly;

	private TableSorter continuousMonitorEqtWrapper;
	protected ContinuousMonitorEqt modifyContinuousMonitorEqt;
	private boolean newContinuousMonitorEqt = false;

	private boolean disableDelete = false;
	private String disableDeleteMsg;

	private static final int EA_WIDTH = 600;
	
	private List<Integer> fpEuIdsToAssociate = new ArrayList<Integer>();
	private List<Integer> fpEuIdsToDisassociate = new ArrayList<Integer>();
	
	private List<Integer> fpEgressPointIdsToAssociate = new ArrayList<Integer>();
	private List<Integer> fpEgressPointIdsToDisassociate = new ArrayList<Integer>();
	
	private boolean staging = false; // true if we should access staging db when on portal

	private String continuousMonitorDeleteAllowedMsg;
	
	public ContinuousMonitorDetail() {
		super();
		continuousMonitor = new ContinuousMonitor();
		continuousMonitorEqtWrapper = new TableSorter();
	}

	public ContinuousMonitor getContinuousMonitor() {
		return continuousMonitor;
	}

	public void setContinuousMonitor(ContinuousMonitor continuousMonitor) {
		this.continuousMonitor = continuousMonitor;
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

	public ContinuousMonitorService getContinuousMonitorService() {
		return continuousMonitorService;
	}

	public void setContinuousMonitorService(
			ContinuousMonitorService continuousMonitorService) {
		this.continuousMonitorService = continuousMonitorService;
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

	public final String startNewContinuousMonitor() throws DAOException {
		continuousMonitor = new ContinuousMonitor();
		editable = true;
		FacilityProfile facProfile;
		Facility facility;

		try {
			facProfile = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			
			facility = facProfile.getFacility();

			setCompanyName(facility.getOwner().getCompany().getName());
			setFacilityName(facility.getName());
			continuousMonitor.setFacilityId(facility.getFacilityId());
			continuousMonitor.setFpId(facility.getFpId());
			continuousMonitor.setCreatorId(InfrastructureDefs
					.getCurrentUserId());
			continuousMonitor.setAddDate(new Timestamp(System
					.currentTimeMillis()));
			continuousMonitor = continuousMonitorService
					.createContinuousMonitor(continuousMonitor);
			continuousMonitor = continuousMonitorService
					.retrieveContinuousMonitor(continuousMonitor
							.getContinuousMonitorId());
			facilityId = continuousMonitor.getFacilityId();
			setContinuousMonitorId(continuousMonitor.getContinuousMonitorId());
			// initializeAttachmentBean();
			// refresh facility profile if a new version of the facility was created
			if(facProfile.getFpId().intValue() != continuousMonitor.getFpId().intValue()) {
				refreshFacilityProfile(continuousMonitor.getFpId());
				facProfile = (FacilityProfile) FacesUtil
						.getManagedBean("facilityProfile");
			}
			this.refreshContinuousMonitorEqts();
			facProfile.refreshFacilityCemComLimitsByMonitor(continuousMonitor.getContinuousMonitorId());
			editable = true;
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to create continuous monitor");
			logger.error(ex.getMessage(), ex);
		}

		return "dialog:continuousMonitorDetail";
	}

	public final String saveContinuousMonitor(ActionEvent event) {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		boolean ok = true;
		try {
			String errorClientIdPrefix = "saveContinuousMonitor:";
			if (displayValidationMessages(errorClientIdPrefix,
					continuousMonitor.validate())) {
				ok = false;
			}

			if (!validateFinal()) {
				ok = false;
			}

			if (ok) {
				Integer fpId = continuousMonitor.getFpId();	
				Facility facility = continuousMonitorService
						.modifyContinuousMonitor(continuousMonitor,
								fpEuIdsToAssociate, fpEuIdsToDisassociate,
								fpEgressPointIdsToAssociate,
								fpEgressPointIdsToDisassociate);
				// if the update has triggered a new version of the facility then we need to
				// set the continuousMonitorId to the id assigned to the ContinuousMonitor object
				if(fpId.intValue() != facility.getFpId().intValue()) {
					for(ContinuousMonitor cm : facility.getContinuousMonitorList()) {
						if(cm.getCorrMonitorId().intValue() == continuousMonitor.getCorrMonitorId().intValue()) {
							setContinuousMonitorId(cm.getContinuousMonitorId());
							break;
						}
					}
				}
				// get the updated continuous monitor from the DB
				continuousMonitor = continuousMonitorService.retrieveContinuousMonitor(continuousMonitorId);
				
				fpEuIdsToAssociate.clear();
				fpEuIdsToDisassociate.clear();
				fpEgressPointIdsToAssociate.clear();
				fpEgressPointIdsToDisassociate.clear();
				
				// refresh associated object(s) 
				continuousMonitor.setAssociatedFpEuIds(
						continuousMonitorService.retrieveAssociatedFpEuIdsByMonitorId(continuousMonitorId));
				
				continuousMonitor.setAssociatedFpEgressPointIds(
						continuousMonitorService.retrieveAssociatedFpEgressPointIdsByMonitorId(continuousMonitorId));
				
				// refresh the facility profile
				refreshFacilityProfile(facility.getFpId());
					
				// refresh the cem/com limits
				// need to do this in order to properly render the cem/com limits
				// associated with a given continuous monitor
				FacilityProfile facProfile = (FacilityProfile)FacesUtil.getManagedBean("facilityProfile");
				facProfile.refreshFacilityCemComLimitsByMonitor(continuousMonitor.getContinuousMonitorId());
			}
		} catch (RemoteException ex) {
			DisplayUtil.displayError("Failed to update continuous monitor");
			handleException(ex);
		} finally {
			clearButtonClicked();
		}

		if (ok) {
			editable = false;
		}

		return "dialog:continuousMonitorDetail";
	}

	public final void startEditAction() {
		editable = true;
	}

	public final String cancelEditAction(ActionEvent actionEqt) {
		continuousMonitor = null;
		fpEuIdsToAssociate.clear();
		fpEuIdsToDisassociate.clear();
		fpEgressPointIdsToAssociate.clear();
		fpEgressPointIdsToDisassociate.clear();
		editable = false;
		try {
			continuousMonitor = continuousMonitorService
					.retrieveContinuousMonitor(continuousMonitorId);
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to retrieve continuous monitor");
			logger.error(ex.getMessage(), ex);
		}
		refreshFacilityProfile(continuousMonitor.getFpId());
		
		// refresh the cem/com limits
		// need to do this in order to properly render the cem/com limits
		// associated with a given continuous monitor
		FacilityProfile facProfile = (FacilityProfile)FacesUtil.getManagedBean("facilityProfile");
		facProfile.refreshFacilityCemComLimitsByMonitor(continuousMonitor.getContinuousMonitorId());
		
		return "dialog:continuousMonitorDetail";
	}

	@Override
	public Integer getExternalId() {
		return continuousMonitorId;
	}

	@Override
	public void setExternalId(Integer externalId) {
		continuousMonitorId = externalId;
		submitInternal();
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
		continuousMonitor = null;
		editable = false;
	}

	public final void setContinuousMonitorId(Integer continuousMonitorId) {
		this.continuousMonitorId = continuousMonitorId;
	}

	public final Integer getContinuousMonitorId() {
		return continuousMonitorId;
	}

	public final String submitContinuousMonitor() {
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
		 * This returns true if user is read-only user.
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
		return continuousMonitor.getMonId();
	}

	public String getExternalName(ProcessActivity activity) {
		String ret = super.getExternalName(activity);
		ret = "Continuous Monitor";
		return ret;
	}

	public String toExternal() {
		return "dialog:continuousMonitorDetail";
	}

	public final String requestDelete() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		clearButtonClicked();

		return "dialog:requestContinuousMonitorDelete";
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

	public final String deleteContinuousMonitor() {

		boolean ok = true;

		if (ok) {
			try {
				Integer fpId = continuousMonitor.getFpId();
				Integer newFpId = getContinuousMonitorService().deleteContinuousMonitor(
						continuousMonitor);
				
				// if a new version of the facility was created then refresh the facility profile
				//if(fpId.intValue() != newFpId.intValue()) {
					refreshFacilityProfile(newFpId);
				//}
				
				ContinuousMonitorSearch continuousMonitorSearch = (ContinuousMonitorSearch) FacesUtil
						.getManagedBean("continuousMonitorSearch");
				continuousMonitorSearch
						.setPopupRedirectOutcome(ContinuousMonitorSearch.SEARCH_OUTCOME);
				//continuousMonitorSearch.setFpId(newFpId);
				//continuousMonitorSearch.search();
				DisplayUtil
						.displayInfo("Continuous Monitor successfully deleted");

				FacesUtil.returnFromDialogAndRefresh();
				return null;

			} catch (DAOException e) {
				if (!e.prettyMsgIsNull()) {
					DisplayUtil
							.displayError("Cannot Delete Continuous Monitor because "
									+ ((DAOException) e).getPrettyMsg());
				} else {
					DisplayUtil
							.displayError("Cannot Delete Continuous Monitor ... it may be included in a Compliance Report.");
				}
				FacesUtil.returnFromDialogAndRefresh();
				return null;
			} catch (RemoteException e) {
				handleException(e);

				ok = false;
			}
		}
		if (!ok) {
			DisplayUtil.displayInfo("Failed to delete Continuous Monitor.");
		}
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	/*
	 * public void initializeAttachmentBean() { Attachments a = (Attachments)
	 * FacesUtil.getManagedBean("attachments"); a.addAttachmentListener(this);
	 * a.setStaging(false); a.setNewPermitted(isCetaUpdate());
	 * a.setUpdatePermitted(isCetaUpdate());
	 * a.setFacilityId(continuousMonitor.getFacilityId());
	 * a.setSubPath("ContinuousMonitor");
	 * if(continuousMonitor.getContinuousMonitorId() != null)
	 * a.setObjectId(Integer
	 * .toString(continuousMonitor.getContinuousMonitorId())); else
	 * a.setObjectId(""); a.setSubtitle(null); a.setTradeSecretSupported(false);
	 * a.setHasDocType(true);
	 * a.setAttachmentTypesDef(EnforcementAttachmentTypeDef.getData()
	 * .getItems());
	 * a.setAttachmentList(continuousMonitor.getAttachments().toArray( new
	 * Attachment[0])); a.cleanup(); }
	 * 
	 * public void cancelAttachment() { // also used for other cancels
	 * popupRedirect=null; FacesUtil.returnFromDialogAndRefresh(); }
	 * 
	 * public AttachmentEvent createAttachment(Attachments attachment) throws
	 * AttachmentException { boolean ok = true; if (attachment.getDocument() ==
	 * null) { // should never happen
	 * logger.error("Attempt to process null attachment"); ok = false; } else {
	 * 
	 * if (attachment.getPublicAttachmentInfo() == null &&
	 * attachment.getFileToUpload() != null) {
	 * attachment.setPublicAttachmentInfo(new UploadedFileInfo(
	 * attachment.getFileToUpload())); }
	 * 
	 * if (attachment.getDocument().getDescription() == null) { DisplayUtil
	 * .displayError("Please specify the description for this attachment"); ok =
	 * false; } if (attachment.getDocument().getDocTypeCd() == null) {
	 * DisplayUtil
	 * .displayError("Please specify an attachment type for this attachment");
	 * ok = false; } //if (attachment.getDocument().getFileName() == null) { if
	 * (attachment.getPublicAttachmentInfo() == null &&
	 * attachment.getTempDoc().getDocumentID() == null) { DisplayUtil
	 * .displayError("Please specify the file to upload for this attachment");
	 * ok = false; } } if (ok) { if (attachment.getPublicAttachmentInfo() ==
	 * null && attachment.getFileToUpload() != null) {
	 * attachment.setPublicAttachmentInfo(new UploadedFileInfo(
	 * attachment.getFileToUpload())); } try { try {
	 * getContinuousMonitorService().createContinuousMonitorAttachment(
	 * continuousMonitor, attachment.getTempDoc(),
	 * attachment.getPublicAttachmentInfo() .getInputStream()); } catch
	 * (IOException e) { throw new RemoteException(e.getMessage(), e); }
	 * 
	 * // save the CM because user may have entered some new data ok =
	 * continuousMonitorService .modifyContinuousMonitor(continuousMonitor);
	 * 
	 * if (ok) {
	 * 
	 * continuousMonitor = getContinuousMonitorService()
	 * .retrieveContinuousMonitor( continuousMonitor.getContinuousMonitorId());
	 * // setEnfDiscoveryIdName(null); Attachments a = (Attachments) FacesUtil
	 * .getManagedBean("attachments");
	 * a.setAttachmentList(continuousMonitor.getAttachments() .toArray(new
	 * Attachment[0])); attachment.cleanup();
	 * FacesUtil.returnFromDialogAndRefresh(); } } catch (RemoteException e) {
	 * handleException(e); } } return null; }
	 * 
	 * public AttachmentEvent deleteAttachment(Attachments attachment) { boolean
	 * ok = true; try { Attachment doc = attachment.getTempDoc();
	 * ContinuousMonitorAttachment sa = new ContinuousMonitorAttachment(doc);
	 * getContinuousMonitorService().removeContinuousMonitorAttachment(sa); }
	 * catch (RemoteException e) { ok = false; handleException(e); } // reload
	 * attachments try {
	 * continuousMonitor.setAttachments(getContinuousMonitorService()
	 * .retrieveContinuousMonitorAttachments
	 * (continuousMonitor.getContinuousMonitorId())); Attachments a =
	 * (Attachments) FacesUtil .getManagedBean("attachments");
	 * a.setAttachmentList(continuousMonitor.getAttachments().toArray( new
	 * Attachment[0])); attachment.cleanup(); } catch (RemoteException e) {
	 * //error = true; setEditable(false); //memoEditable = false; // turn it
	 * into an exception we can handle. ok = false; handleException(e); } if
	 * (ok) { DisplayUtil.displayInfo("The attachment has been removed.");
	 * FacesUtil.returnFromDialogAndRefresh();
	 * 
	 * } return null; }
	 * 
	 * public AttachmentEvent updateAttachment(Attachments attachment) {
	 * Attachment doc = attachment.getTempDoc(); boolean ok = true;
	 * 
	 * // make sure document description is provided if (doc.getDescription() ==
	 * null || doc.getDescription().trim().equals("")) { DisplayUtil
	 * .displayError("Please specify the description for this attachment"); ok =
	 * false; }
	 * 
	 * if (ok) { try { ContinuousMonitorAttachment ea = new
	 * ContinuousMonitorAttachment(doc);
	 * getContinuousMonitorService().modifyContinuousMonitorAttachment(ea); }
	 * catch (RemoteException e) { ok = false; handleException(e); } // reload
	 * attachments try {
	 * continuousMonitor.setAttachments(getContinuousMonitorService()
	 * .retrieveContinuousMonitorAttachments
	 * (continuousMonitor.getContinuousMonitorId())); Attachments a =
	 * (Attachments) FacesUtil .getManagedBean("attachments");
	 * a.setAttachmentList(continuousMonitor.getAttachments().toArray( new
	 * Attachment[0])); attachment.cleanup(); } catch (RemoteException e) {
	 * //error = true; setEditable(false); //memoEditable = false; // turn it
	 * into an exception we can handle. ok = false; handleException(e); } if
	 * (ok) { DisplayUtil
	 * .displayInfo("The attachment information has been updated.");
	 * FacesUtil.returnFromDialogAndRefresh(); } } return null; }
	 */

	public final Note getContinuousMonitorNote() {
		return continuousMonitorNote;
	}

	public final String startAddNote() {
		continuousMonitorNote = new ContinuousMonitorNote();
		continuousMonitorNote.setUserId(InfrastructureDefs.getCurrentUserId());
		continuousMonitorNote.setNoteTypeCd(NoteType.DAPC);
		continuousMonitorNote.setDateEntered(new Timestamp(System
				.currentTimeMillis()));
		continuousMonitorNote.setCorrMonitorId(continuousMonitor
				.getCorrMonitorId());
		noteReadOnly = false;
		return "dialog:continuousMonitorNoteDetail";
	}

	public final String startEditNote() {

		continuousMonitorNote = new ContinuousMonitorNote(
				(ContinuousMonitorNote) FacesUtil.getManagedBean("cmNote"));
		continuousMonitorNote.setCorrMonitorId(continuousMonitor
				.getCorrMonitorId());

		if (continuousMonitorNote.getUserId().equals(
				InfrastructureDefs.getCurrentUserId())) {
			noteReadOnly = false;
		} else {
			noteReadOnly = true;
		}

		return "dialog:continuousMonitorNoteDetail";

	}

	public final void applyEditNote(ActionEvent actionEqt) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		// make sure note is provided
		if (continuousMonitorNote.getNoteTxt() == null
				|| continuousMonitorNote.getNoteTxt().trim().equals("")) {
			validationMessages.add(new ValidationMessage("noteTxt",
					"Attribute " + "Note" + " is not set.",
					ValidationMessage.Severity.ERROR, "noteTxt"));
		}

		if (validationMessages.size() > 0) {
			displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0]));
		} else {
			try {
				if (continuousMonitorNote.getNoteId() == null) {
					// new note
					getContinuousMonitorService().createContinuousMonitorNote(
							continuousMonitorNote);
					DisplayUtil
							.displayInfo("The note added to the monitor successfully.");
				} else {
					// edit existing note
					if (!isStars2Admin()
							&& !continuousMonitorNote.getUserId().equals(
									InfrastructureDefs.getCurrentUserId())) {
						DisplayUtil
								.displayError("You may only edit a note which you have created.");
					} else {
						getContinuousMonitorService()
								.modifyContinuousMonitorNote(
										continuousMonitorNote);
						DisplayUtil.displayInfo("Note updated successfully");
					}
				}
				continuousMonitorNote = null;
				reloadNotes();
				FacesUtil.returnFromDialogAndRefresh();
			} catch (RemoteException re) {
				handleException(re);
			}
		}
	}

	public final void cancelEditNote() {

		continuousMonitorNote = null;
		noteReadOnly = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void closeViewNote(ActionEvent actionEqt) {
		continuousMonitorNote = null;
		noteReadOnly = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void noteDialogDone(ReturnEvent returnEqt) {
		continuousMonitorNote = null;
		noteReadOnly = false;
		reloadNotes();
	}

	/**
	 * Reload notes for current monitor.
	 */
	private final String reloadNotes() {
		loadNotes(continuousMonitor.getCorrMonitorId());
		return FacesUtil.getCurrentPage(); // stay on same page
	}

	private void loadNotes(int corrMonitorId) {
		try {
			List<ContinuousMonitorNote> notes = getContinuousMonitorService()
					.retrieveContinuousMonitorNotes(corrMonitorId);
			continuousMonitor.setNotes(notes);
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

	/*
	 * public final String startEditAttachment() { String ret = null;
	 * Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
	 * a.setNewAttachment(false); ret= "dialog:editContinuousMonitorAttachment";
	 * return ret; }
	 */

	/* START CODE: Physical Monitor */
	
	private final void initializeContinuousMonitorEqts() {
		initializeContinuousMonitorEqts(true);
	}

	private final void initializeContinuousMonitorEqts(boolean staging) {
		this.continuousMonitorEqtWrapper = new TableSorter();
		try {
			this.continuousMonitor
					.setContinuousMonitorEqtList(getContinuousMonitorService()
							.retrieveContinuousMonitorEqtList(
									this.continuousMonitor
											.getContinuousMonitorId(), staging));
			this.continuousMonitorEqtWrapper = new TableSorter();
			this.continuousMonitorEqtWrapper
					.setWrappedData((this.continuousMonitor)
							.getContinuousMonitorEqtList());

		} catch (RemoteException re) {
			logger.error("failed to get Physical Monitors", re);
			DisplayUtil.displayError("Error while retrieving Monitor Equipment Tracking history.");
		} finally {
			this.editMode1 = false;
		}
	}

	public final String startToAddContinuousMonitorEqt() {
		this.editMode1 = true;
		this.modifyContinuousMonitorEqt = new ContinuousMonitorEqt();
		this.modifyContinuousMonitorEqt
				.setContinuousMonitorId(continuousMonitor
						.getContinuousMonitorId());
		this.modifyContinuousMonitorEqt.setAddedBy(InfrastructureDefs
				.getCurrentUserId());
		this.newContinuousMonitorEqt = true;

		return "dialog:continuousMonitorEqtDetail";
	}

	public final String startToEditContinuousMonitorEqt() {
		int index = this.continuousMonitorEqtWrapper.getRowIndex();
		this.modifyContinuousMonitorEqt = getSelectedContinuousMonitorEqt(index);
		// this.modifyContinuousMonitorEqt.setAddedBy(InfrastructureDefs.getCurrentUserId());

		this.newContinuousMonitorEqt = false;
		this.editMode1 = false;

		return "dialog:continuousMonitorEqtDetail";
	}

	public ContinuousMonitorEqt getModifyContinuousMonitorEqt() {

		return this.modifyContinuousMonitorEqt;
	}

	public void setModifyContinuousMonitorEqt(
			ContinuousMonitorEqt modifyContinuousMonitorEqt) {
		this.modifyContinuousMonitorEqt = modifyContinuousMonitorEqt;
	}

	private final ContinuousMonitorEqt getSelectedContinuousMonitorEqt(int index) {
		ContinuousMonitorEqt cp = (ContinuousMonitorEqt) this.continuousMonitorEqtWrapper
				.getRowData(index);

		return cp;
	}

	public boolean isNewContinuousMonitorEqt() {
		return newContinuousMonitorEqt;
	}

	public void setNewContinuousMonitorEqt(boolean newContinuousMonitorEqt) {
		this.newContinuousMonitorEqt = newContinuousMonitorEqt;
	}

	public final void editContinuousMonitorEqt() {
		this.editMode1 = true;
		this.newContinuousMonitorEqt = false;
	}

	public final void saveContinuousMonitorEqt() {

		DisplayUtil.clearQueuedMessages();

		this.getModifyContinuousMonitorEqt().setAddedBy(
				InfrastructureDefs.getCurrentUserId());

		if (continuousMonitorEqtValidated()) {
			if (this.newContinuousMonitorEqt) {
				createContinuousMonitorEqt();
			} else {
				updateContinuousMonitorEqt();
			}
		}
	}

	public void closeContinuousMonitorEqtDialog() {
		closeDialog();
	}

	protected final void refreshContinuousMonitorEqts() {
		this.editMode1 = false;
		this.newContinuousMonitorEqt = false;
		this.initializeContinuousMonitorEqts();
	}
	
	protected final void refreshContinuousMonitorEqtsReadOnly() {
		this.editMode1 = false;
		this.newContinuousMonitorEqt = false;
		this.initializeContinuousMonitorEqts(false);
	}
	
	protected final void refreshContinuousMonitorEqts(boolean staging) {
		this.editMode1 = false;
		this.newContinuousMonitorEqt = false;
		this.initializeContinuousMonitorEqts(staging);
	}

	public void createContinuousMonitorEqt() {

		try {
			Integer fpId = continuousMonitor.getFpId();
			Integer newFpId = getContinuousMonitorService().createContinuousMonitorEqt(
					this.getModifyContinuousMonitorEqt());
			
			// refresh the facility profile and the continuous monitor
			refresh(newFpId);
			
			DisplayUtil.displayInfo("Create Physical Monitor Success");

		} catch (RemoteException e) {
			logger.error("Create Physical Monitor Failed", e);
			DisplayUtil.displayError("Create Physical Monitor Failed");
			handleException(e);
		} finally {
			this.refreshContinuousMonitorEqts();
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public void updateContinuousMonitorEqt() {

		try {
			// Modify Physical Monitor
			Integer fpId = continuousMonitor.getFpId();
			Integer newFpId = getContinuousMonitorService().modifyContinuousMonitorEqt(
					this.getModifyContinuousMonitorEqt());
			// refresh the facility profile and the continuous monitor
			refresh(newFpId);
			
			DisplayUtil.displayInfo("Modify Physical Monitor Success");

		} catch (RemoteException e) {
			logger.error("Modify Physical Monitor Failed", e);
			DisplayUtil.displayError("Modify Physical Monitor Failed");
			handleException(e);
		} finally {
			this.refreshContinuousMonitorEqts();
			closeDialog();
		}
	}

	public void deleteContinuousMonitorEqt() {

		DisplayUtil.clearQueuedMessages();

		try {
			
			Integer fpId = continuousMonitor.getFpId();
			Integer newFpId = getContinuousMonitorService().removeContinuousMonitorEqt(
					this.modifyContinuousMonitorEqt);
			
			// refresh the facility profile and the continuous monitor
			refresh(newFpId);
			
			DisplayUtil.displayInfo("Delete Physical Monitor Success");

		} catch (RemoteException e) {
			logger.error("Remove Physical Monitor failed", e);
			DisplayUtil.displayError("Remove Physical Monitor failed");
			handleException(e);
		}finally {
			refreshContinuousMonitorEqts();
			closeDialog();
		}
	}

	public void cancelContinuousMonitorEqt() {
		this.refreshContinuousMonitorEqts();
		closeDialog();
	}

	public final TableSorter getContinuousMonitorEqtWrapper() {
		return this.continuousMonitorEqtWrapper;
	}

	public final void setContinuousMonitorEqtWrapper(
			TableSorter continuousMonitorEqtWrapper) {
		this.continuousMonitorEqtWrapper = continuousMonitorEqtWrapper;
	}

	public boolean continuousMonitorEqtValidated() {

		if (this.getModifyContinuousMonitorEqt().getManufacturerName() == null
				|| Utility.isNullOrEmpty(this.getModifyContinuousMonitorEqt()
						.getManufacturerName().trim())) {
			DisplayUtil.displayError("ERROR: Manufacturer is not set.");
			return false;
		}

		if (this.getModifyContinuousMonitorEqt().getModelNumber() == null
				|| Utility.isNullOrEmpty(this.getModifyContinuousMonitorEqt()
						.getModelNumber().trim())) {
			DisplayUtil.displayError("ERROR: Model Number is not set.");
			return false;
		}
		
		if (this.getModifyContinuousMonitorEqt().getSerialNumber() == null
				|| Utility.isNullOrEmpty(this.getModifyContinuousMonitorEqt()
						.getSerialNumber().trim())) {
			DisplayUtil.displayError("ERROR: Serial Number is not set.");
			return false;
		}

		if (this.getModifyContinuousMonitorEqt().getQAQCAcceptedDate() != null
				&& !Utility.isNullOrEmpty(this.getModifyContinuousMonitorEqt()
						.getQAQCAcceptedDate().toString())) {
			if (this.getModifyContinuousMonitorEqt().getQAQCSubmittedDate() == null
					|| Utility.isNullOrEmpty(this
							.getModifyContinuousMonitorEqt()
							.getQAQCSubmittedDate().toString())) {
				DisplayUtil
						.displayError("ERROR: The \"QA/QC Submitted Date\" must have a value since a value has been entered for \"QA/QC Accepted Date\".");
				return false;
			} else {
				if (getModifyContinuousMonitorEqt().getQAQCAcceptedDate()
						.before(getModifyContinuousMonitorEqt().getQAQCSubmittedDate())) {
					DisplayUtil
						.displayError("ERROR: The \"QA/QC Accepted Date\" must be on or after \"QA/QC Submitted Date\".");
					return false;
				}
			}
			
		}

		if (this.getModifyContinuousMonitorEqt().getQAQCSubmittedDate() != null
				&& this.getModifyContinuousMonitorEqt().getQAQCAcceptedDate() != null
				&& this.getModifyContinuousMonitorEqt()
						.getQAQCAcceptedDate()
						.before(this.getModifyContinuousMonitorEqt()
								.getQAQCSubmittedDate())) {
			DisplayUtil
					.displayError("ERROR: The \"QA/QC Accepted Date\" cannot be before \"QA/QC Submitted Date\".");
			return false;

		}

		//  per Tyler, Install Date is not required.
		//if (this.getModifyContinuousMonitorEqt().getInstallDate() == null
		//		|| Utility.isNullOrEmpty(this.getModifyContinuousMonitorEqt()
		//				.getInstallDate().toString())) {
		//	DisplayUtil
		//			.displayError("ERROR: Attribute Install Date is not set.");
		//	return false;
		//}

		if (this.getModifyContinuousMonitorEqt().getInstallDate() != null
				&& this.getModifyContinuousMonitorEqt().getRemovalDate() != null
				&& this.getModifyContinuousMonitorEqt()
						.getRemovalDate()
						.before(this.getModifyContinuousMonitorEqt()
								.getInstallDate())) {
			DisplayUtil
					.displayError("ERROR: The \"Removal Date\" cannot be before \"Install Date\".");
			return false;

		}
		
		if (getModifyContinuousMonitorEqt().getInstallDate() != null &&
				getModifyContinuousMonitorEqt().getQAQCSubmittedDate() != null &&
				getModifyContinuousMonitorEqt().getQAQCSubmittedDate().before(getModifyContinuousMonitorEqt().getInstallDate())) {
			DisplayUtil
			.displayError("ERROR: The \"QA/QC Submitted Date\" cannot be before \"Install Date\".");
			return false;
		}
				
		
		// check for duplicate equipment only if the removal date is null
		if (null == getModifyContinuousMonitorEqt().getRemovalDate()
				&& this.isDuplicateEqt()) {
			DisplayUtil
					.displayError("ERROR: An active physical CEM/COM/CMS monitor with this manufacturer, model number, and serial number already exists. Multiple active instances of this monitor are not allowed.");
			return false;
		}

		return validateFinal();

	}

	public boolean validateFinal() {
		boolean ret = true;

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

	public final boolean getContinuousMonitorEditAllowed() {
		//return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
		//		|| InfrastructureDefs.getCurrentUserAttrs()
		//				.isCurrentUseCaseValid("editContinuousMonitor");
		if (!isInternalApp()) {
			return false;
		}
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| !isReadOnlyUser();
		
	}

	public final boolean getContinuousMonitorCreateAllowed() {
		//return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
		//		|| InfrastructureDefs.getCurrentUserAttrs()
		//				.isCurrentUseCaseValid("createContinuousMonitor");
		if (!isInternalApp()) {
			return false;
		} 
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| !isReadOnlyUser();
	}
	
	public final boolean getContinuousMonitorDeleteAllowed() {
		//return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
		//		|| InfrastructureDefs.getCurrentUserAttrs()
		//				.isCurrentUseCaseValid("createContinuousMonitor");
		
		if (isPublicApp()) {
			return false;
		} else if (isInternalApp()) {

			// If any Compliance Reports exist with this monitor, Delete is not
			// allowed.
			if (getFacilityMonitorComplianceReportCount() != 0) {
				setContinuousMonitorDeleteAllowedMsg("Delete is disabled because at least one Compliance Report includes this Monitor.");
				return false;
			}
			if(getFacilityMonitorLimitsIncludedInInspection()){
				setContinuousMonitorDeleteAllowedMsg("Delete is disabled because at least one Inspection Report includes this Monitor's Limit(s).");
				return false;
			}

		}
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();
	}

	private boolean getFacilityMonitorLimitsIncludedInInspection() {
		boolean ret = false;

		ArrayList<Integer> limits = new ArrayList<Integer>();
		for (FacilityCemComLimit lim : continuousMonitor.getFacilityCemComLimitList()) {
			limits.add(lim.getLimitId());
		}
		try {
			ret = getFacilityService().isMonitorLimitsIncludedInInspectionReport(limits);
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil
					.displayError("Accessing facility monitor inspection report count failed");
		}
		return ret;
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

	public boolean isValidEqtDate(String eventCd, Timestamp eventDate) {
		boolean ret = true;

		return ret;
	}

	/* END CODE: Monitor Equipment */

	// ***************

	public String from2ndLevelMenu() {
		submit();

		return CONTINUOUS_MONITOR_TAG;
	}

	public final String submit() {
		if (!firstButtonClick()) { // protect from multiple clicks
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

		if (continuousMonitorId != null) {

			try {

				continuousMonitor = getContinuousMonitorService()
						.retrieveContinuousMonitor(continuousMonitorId,
								this.isStaging());
				if (continuousMonitor != null) {
					continuousMonitor = getFacilityService()
							.retrieveCompleteContinuousMonitor(
									continuousMonitor, this.isStaging());
					this.continuousMonitorEqtWrapper = new TableSorter();
					this.continuousMonitorEqtWrapper
							.setWrappedData((this.continuousMonitor)
									.getContinuousMonitorEqtList());

					if (isInternalApp()) {
						us.oh.state.epa.stars2.app.facility.FacilityProfile fp = (us.oh.state.epa.stars2.app.facility.FacilityProfile) FacesUtil
								.getManagedBean("facilityProfile");
						if (fp != null) {
							fp.setFacilityCemComLimitsWrapper(new TableSorter());
							fp.getFacilityCemComLimitsWrapper().setWrappedData(
									(this.continuousMonitor)
											.getFacilityCemComLimitList());

						}

					} else {

						us.oh.state.epa.stars2.portal.facility.FacilityProfile fp = (us.oh.state.epa.stars2.portal.facility.FacilityProfile) FacesUtil
								.getManagedBean("facilityProfile");
						if (fp != null) {

							fp.setFacilityCemComLimitsWrapper(new TableSorter());
							fp.getFacilityCemComLimitsWrapper().setWrappedData(
									(this.continuousMonitor)
											.getFacilityCemComLimitList());
						}

					}
				}

			} catch (RemoteException e) {
				handleException("Failed to retrieve Continuous Monitor "
						+ continuousMonitor, e);
				ok = false;
			}
			if (ok) {
				// If on portal and accessing staging (portal) database, return
				// to Continuous Monitor detail page for Facility Inventory
				// Change task. Otherwise, return to the detail page for the
				// Current Facility Inventory.
				if (isPublicApp()) {
					ret = "homeContinuousMonitor";
				} else if (!isInternalApp() && !this.isStaging()) {
					ret = "homeContinuousMonitor";
				} else {
					ret = "continuousMonitor";
				}

			} else {
				return null;
			}
		} else {
			DisplayUtil.displayError("Continuous Monitor cannot be located.");
			logger.debug("continuousMonitorId is null");
		}

		return ret;
	}
	
	public final boolean isDuplicateEqt() {

		boolean ret = false;
	
		try {
			// get a list of facilities that might have a physical monitor
			// with the same manufacturer, model, and serial number
			Map<Integer, String> matchingEquips = getContinuousMonitorService()
					.facilitiesWithMatchingContinuousMonitorEqt(
							getModifyContinuousMonitorEqt()
									.getManufacturerName(),
							getModifyContinuousMonitorEqt().getModelNumber(),
							getModifyContinuousMonitorEqt().getSerialNumber());
			
			// don't want to check against itself when updating the equipment
			if(!isNewContinuousMonitorEqt()) {
				Integer equipmentId = getModifyContinuousMonitorEqt().getMonitorEqtId();
				matchingEquips.remove(equipmentId);
			}
			
			if(!matchingEquips.isEmpty()) {
				DisplayUtil
						.displayError("ERROR: Active physical monitor with this Manufacturer, Model Number, and Serial Number is already present in facility "
								+ StringUtils.join(matchingEquips.values().toArray(new String[0]), ","));
				ret = true;
			} else {
				ret = false;
			}

		} catch (DAOException daoe) {
			DisplayUtil
					.displayError("An error occured when checking for duplicate physical monitor");
			handleException(daoe);
		}

		return ret;
	}
	
	// shuttle support for associating emissions units
		/** 
		 * Returns a list of emu ids of operating emissions units in the 
		 * facility profile associated with this monitor.
		 * 
		 */
		public List<SelectItem> getAvailableEUs() {
			List<SelectItem> availableEUs = new ArrayList<SelectItem>();
			FacilityProfileBase fp = (FacilityProfileBase)FacesUtil.getManagedBean("facilityProfile");
			if(null != fp) {
				for(EmissionUnit fpEU : fp.getFacility().getEmissionUnits()) {
					if(fpEU.getOperatingStatusCd().equals(EuOperatingStatusDef.OP)
							|| getContinuousMonitor().getAssociatedFpEuIds().contains(fpEU.getEmuId())) {
						availableEUs.add(new SelectItem(fpEU.getEmuId(), fpEU.getEpaEmuId()));
					}
				}
			}
			
			Collections.sort(availableEUs, new Comparator<SelectItem>() {
				public int compare(SelectItem si1, SelectItem si2) {
					return si1.getLabel().compareToIgnoreCase(si2.getLabel());
				};
			});
			
			return availableEUs;
		}
		
		public List<Integer> getAssociatedEUs() {
			List<Integer> fpEuIds = new ArrayList<Integer>();
			
			fpEuIds.addAll(getContinuousMonitor().getAssociatedFpEuIds());
			
			if(!fpEuIdsToAssociate.isEmpty()) {
				fpEuIds.addAll(fpEuIdsToAssociate);
			}
			
			if(!fpEuIdsToDisassociate.isEmpty()) {
				fpEuIds.removeAll(fpEuIdsToDisassociate);
				
			}
			return fpEuIds;
		}
		
		public void setAssociatedEUs(List<Integer> associatedEUs) {
			fpEuIdsToAssociate.clear();
			fpEuIdsToDisassociate.clear();
			
			List<Integer> associatedFpEuIds = 
					getContinuousMonitor().getAssociatedFpEuIds();
			
			// determine if any previously associated EUs were now disassociated
			for(Integer emuId : associatedFpEuIds) {
				if(!associatedEUs.contains(emuId)) {
					fpEuIdsToDisassociate.add(emuId);
				}
			}
			
			// determine any newly associated EUs
			for(Integer emuId : associatedEUs) {
				if(!associatedFpEuIds.contains(emuId)) {
					fpEuIdsToAssociate.add(emuId);
				}
			}
		}
		
		// shuttle support for associating release points
		/** 
		 * Returns a list of fpnode ids of operating release points in the 
		 * facility profile associated with this monitor.
		 * 
		 */
		public List<SelectItem> getAvailableEgressPoints() {
			List<SelectItem> availableEgressPoints = new ArrayList<SelectItem>();
			FacilityProfileBase fp = (FacilityProfileBase)FacesUtil.getManagedBean("facilityProfile");
			if(null != fp) {
				for(EgressPoint egp : fp.getFacility().getEgressPointsList()) {
					if(egp.getOperatingStatusCd().equals(EgOperatingStatusDef.OP)
							|| getContinuousMonitor().getAssociatedFpEgressPointIds().contains(egp.getFpNodeId())) {
						availableEgressPoints.add(new SelectItem(egp.getFpNodeId(), egp.getReleasePointId()));
					}
				}
			}
			
			Collections.sort(availableEgressPoints, new Comparator<SelectItem>() {
				public int compare(SelectItem si1, SelectItem si2) {
					return si1.getLabel().compareToIgnoreCase(si2.getLabel());
				};
			});
			
			return availableEgressPoints;
		}
		
		public List<Integer> getAssociatedEgressPoints() {
			List<Integer> egressPointIds = new ArrayList<Integer>();
			
			egressPointIds.addAll(getContinuousMonitor().getAssociatedFpEgressPointIds());
			
			if(!fpEgressPointIdsToAssociate.isEmpty()) {
				egressPointIds.addAll(fpEgressPointIdsToAssociate);
			}
			
			if(!fpEgressPointIdsToDisassociate.isEmpty()) {
				egressPointIds.removeAll(fpEgressPointIdsToDisassociate);
			}
			
			return egressPointIds;
		}
		
		public void setAssociatedEgressPoints(List<Integer> associatedEgressPoints) {
			fpEgressPointIdsToAssociate.clear();
			fpEgressPointIdsToDisassociate.clear();
			
			List<Integer> associatedFpEgressPointIds = 
					getContinuousMonitor().getAssociatedFpEgressPointIds();
			
			// determine if any previously associated release points were now disassociated
			for (Integer fpNodeId : associatedFpEgressPointIds) {
				if (!associatedEgressPoints.contains(fpNodeId)) {
					fpEgressPointIdsToDisassociate.add(fpNodeId);
				}
			}

			// determine any newly associated release points
			for (Integer fpNodeId : associatedEgressPoints) {
				if (!associatedFpEgressPointIds.contains(fpNodeId)) {
					fpEgressPointIdsToAssociate.add(fpNodeId);
				}
			}
		}
		
	public boolean isHasAssociatedObjects() {
			return !getContinuousMonitor().getAssociatedFpEuIds().isEmpty() 
					 || !getContinuousMonitor().getAssociatedFpEgressPointIds().isEmpty();
		}
		
	private void refreshFacilityProfile(Integer fpId) {
		FacilityProfile fac = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fac.setFpId(fpId);
		fac.refreshFacilityProfile();
	}

	private void refresh(int fpId) throws RemoteException {
		refreshFacilityProfile(fpId);
		
		FacilityProfile fac = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
		
		for (ContinuousMonitor cm : fac.getFacility().getContinuousMonitorList()) {
			if (cm.getCorrMonitorId().intValue() == continuousMonitor.getCorrMonitorId().intValue()) {
				continuousMonitor = continuousMonitorService
						.retrieveContinuousMonitor(cm.getContinuousMonitorId());
				setContinuousMonitorId(continuousMonitor.getContinuousMonitorId());
				break;
			}
		}
		
		fac.refreshFacilityCemComLimitsByMonitor(continuousMonitor.getContinuousMonitorId());
	}
	
	public final Integer getFacilityMonitorComplianceReportCount() {
		Integer facilityMonitorReportCount = 0;
		if (continuousMonitor == null) {
			logger.debug("getFacilityMonitorComplianceReportCount: continuousMonitor is null");
		}

		if (continuousMonitor != null) {
			try {
				facilityMonitorReportCount = getFacilityService()
						.retrieveCemsComplianceReportCountWithMonitor(
								continuousMonitor.getCorrMonitorId());

				logger.debug("facilityMonitorReportCount = "
						+ facilityMonitorReportCount);

			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil
						.displayError("Accessing facility monitor compliance report count failed");
			}
		} else {
			logger.debug("continuousMonitor is null ... cannot query for facility monitor compliance report count.");
		}

		return facilityMonitorReportCount;
	}

	public boolean isStaging() {
		return staging;
	}

	public void setStaging(boolean staging) {
		this.staging = staging;
	}

	public String getContinuousMonitorDeleteAllowedMsg() {
		return continuousMonitorDeleteAllowedMsg;
	}

	public void setContinuousMonitorDeleteAllowedMsg(String continuousMonitorDeleteAllowedMsg) {
		this.continuousMonitorDeleteAllowedMsg = continuousMonitorDeleteAllowedMsg;
	}
	
	
}
