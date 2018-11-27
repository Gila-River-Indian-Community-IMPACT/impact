package us.oh.state.epa.stars2.app.compliance;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.ComplianceReportAcceptedDef;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.ReportComplianceStatusDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.compliance.ComplianceReportsCommon;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

@SuppressWarnings("serial")
public class ComplianceReports extends ComplianceReportsCommon {
	private boolean newRecord;

	// Workflow
	private boolean workflowEnabled = false;
	private ReadWorkFlowService workFlowService;

	// Notes
	private boolean newNote;
	private ComplianceReportNote tempNote;
	private ComplianceReportNote modifyNote;
	private boolean noteReadOnly;

    private boolean cannotDelete;  // used when trying to delete.
    private String cannotDeleteReason;
    
	private static final String NOTE_DIALOG_OUTCOME = "dialog:complianceReportNoteDetail";

	public ComplianceReports() {
		super();
		this.setTag(ValidationBase.COMPLIANCE_TAG);
		setReadOnlyReport(false);
	}
	
	@Override
	public FacilityList[] getBulkComplianceReportFacilities() {
		Company company = getFacility().getOwner().getCompany();
		if (null == company.getFacilities()) {
			try {
				company.setFacilities(
						getFacilityService().searchFacilities(
								null, null, company.getCmpId(),	null, null, null, 
								null, null, null, null, null, null, null, null, 
								null, true, null));
			} catch (RemoteException e) {
				String msg = "An error occurred while getting company facilities.";
				logger.error(msg, e);
				DisplayUtil.displayError(msg);
				handleException(e);
				return new FacilityList[0];
			}
		}
		FacilityList[] rawFacilities = company.getFacilities();
		List<FacilityList> facilities = new ArrayList<FacilityList>();
		for (int i=0; i < rawFacilities.length; i++) {
			if (!rawFacilities[i].getFacilityId().equals(
					getFacility().getFacilityId() ) && 
					null == rawFacilities[i].getEndDate()) {
				facilities.add(rawFacilities[i]);
			}
		}
		Collections.sort(facilities);
		return facilities.toArray(new FacilityList[0]);
	}

	public boolean isActiveWorkflowProcess() {
		Integer processTemplateId = null;
		String reportType = getComplianceReport().getReportType();
		String errMsg = "Error occurred while checking if an active "
				+ "workflow process exists for this compliance report; "
				+ "report deletion will not be allowed.";
		if ("cems".equals(reportType)) {
			processTemplateId = 6;
		} else if ("one".equals(reportType)) {
			processTemplateId = 7;
		} else if ("othr".equals(reportType)) {
			processTemplateId = 9;
		} else {
			processTemplateId = 11; // Generic compliance report
		}

		Integer externalId = getReportId();
		try {
			return null == workFlowService.findActiveProcessByExternalObject(
					processTemplateId, externalId) ? false : true;
		} catch (RemoteException e) {
			logger.error("error while checking for active wf process: ", e);
			DisplayUtil.displayError(errMsg);
			handleException(e);
			return true;
		}
	}

	public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public boolean isWorkflowEnabled() {
		return workflowEnabled;
	}

	public void setWorkflowEnabled(boolean workflowEnabled) {
		this.workflowEnabled = workflowEnabled;
	}

	public String from2ndLevelMenu() {
		if (complianceReport != null) {
			loadComplianceReport(complianceReport.getReportId(), true);
		}
		return "complianceDetail";
	}

	public String getReportType() {
		return complianceReport.getReportType();
	}

	public void setReportType(String reportType) {
		complianceReport.setReportType(reportType);
	}

	public final String startNewReport() {
		setPassedValidation(false);
		setEditable(true);
		setFromTODOList(false);
		newRecord = true;

		complianceReport = new ComplianceReport();
		complianceReport.setCorrectedReport(false);
		complianceReport.setReportBeingSubmitted(false);
		complianceReport.setReportType(null);
		complianceReport.setTvccReportingYear(initializeTvccPeriods());
		complianceReport.setFacilityId(getFacility().getFacilityId());
		complianceReport.setFacilityNm(getFacility().getName());
		complianceReport.setUserId(InfrastructureDefs.getCurrentUserId());
		complianceReport
				.setDapcAccepted(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED);
		complianceReport
				.setReportStatus(ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT);
		return "dialog:newComplianceReport";
	}

	public final void cancelNewReport(ActionEvent actionEvent) {
		newRecord = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String cancelEdit() {

		String ret = "";

		if (!newRecord) {
			ret = FacesUtil.getCurrentPage();
			;
		}
		setEditMode(false);

		loadComplianceReport(complianceReport.getReportId(), true);
		return ret;
	}

	public final void createNewReport(ActionEvent event) {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		if (Utility.isNullOrEmpty(complianceReport.getOtherCategoryCd())
				) {
			DisplayUtil.displayError("Category is not set");
			clearButtonClicked();
			return;
		}
		
		if (!validateTypeCategoryCombination()) {
			DisplayUtil.displayError("Invalid Type/Category combination.");
			clearButtonClicked();
			return;
		}
		
		try {
			setFacility(getFacilityService().retrieveFacility(
					complianceReport.getFacilityId()));
			complianceReport = getComplianceReportService()
					.createComplianceReport(complianceReport, getFacility());

			if (complianceReport == null) {
				setComplianceReportDeleted(true);
				DisplayUtil
						.displayError("Failed to create Compliance Report. Please contact System Administrator.");
				return;
			}

			complianceReport = getComplianceReportService()
					.retrieveComplianceReport(complianceReport.getReportId());

			// update the search bean (this is redundant on the portal side
			if (isInternalApp()) {
				updateSearch();
			}
			if (complianceReport != null) {
				reset();
				initializeAttachmentBean();
				complianceReport.refreshCompReportMonitorList();
			} else {
				setComplianceReportDeleted(true);
				DisplayUtil.displayError("No report found with id "
						+ complianceReport.getReportId());
				throw new RemoteException("No report found with id "
						+ complianceReport.getReportId());
			}
			setNewRecord(false); // the record has been created by this point

			setEditMode(true);
			setPassedValidation(false);
			setPopupRedirectOutcome("complianceDetail");

			FacesUtil.returnFromDialogAndRefresh();
		} catch (RemoteException e) {
			handleException(e);
		} finally {
			clearButtonClicked();
		}
	}

	private boolean validateTypeCategoryCombination() {
		List<SelectItem> validCategories = getReportTypeSubCategories();
		for (SelectItem validCategory : validCategories) {
			if (validCategory.getValue().equals(complianceReport.getOtherCategoryCd())) {
				return true;
			}
		}
		return false;
	}

	public final DefSelectItems getComplianceStatusDef() {
		return ReportComplianceStatusDef.getData().getItems();
	}
	
	@Override
	public void loadComplianceReport(int reportId, boolean useReadOnlySchema) {
		super.loadComplianceReport(reportId, useReadOnlySchema);
		selectNoneF();
	}

	public final String viewDetail() {
		this.setPassedValidation(false);
		this.setEditable(false);
		this.setEditMode(false);
		this.setReadOnlyReport(false);
		String ret = null;
		complianceReport = null;

		logger.debug("viewDetail getReportId() = " + getReportId());
		loadComplianceReport(getReportId(), schemaFlag());

		// don't allow edit if AFS Id and sent date has been set
		if (complianceReport != null) {
			setEditable(complianceReport.getTvccAfsId() == null
					&& complianceReport.getTvccAfsSentDate() == null);
			logger.debug("viewDetail this.getComplianceReport().getReportId() = "
					+ this.getComplianceReport().getReportId());
			if (this.getComplianceReport().getReportId() != null) {
				ret = "complianceDetail";
			} else {
				ret = null;
				DisplayUtil
						.displayError("Compliance Report is not found with that number.");
			}
		} else {
			DisplayUtil
					.displayError("Compliance Report is not found with that number.");
		}
		return ret;
	}

	public String submit() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		/*
		 * 1. validate the report 2. if valid 3. change status to 'submitted' 4.
		 * update 5. if (portal) transfer data across DMZ
		 */
		String ret = "";
		if (!validateReport()) {
			DisplayUtil
					.displayError("Report is invalid and may not be submitted until errors are fixed.");
		} else {
			if (!submitReport()) {
				DisplayUtil
						.displayError("There was an internal error while attempting to submit the report.");
				loadComplianceReport(complianceReport.getReportId(), true);
			} else {
				setFromTODOList(false);
				// reload the compliance report
				loadComplianceReport(complianceReport.getReportId(), true);
				if (complianceReport != null) {
					DisplayUtil.clearQueuedMessages();
					DisplayUtil.displayInfo("Submit successful");
				} else {
					DisplayUtil
							.displayError("Compliance Report is not found with that number.");
				}
				// update the search bean since we're not in the portal
				updateSearch();
			}
		}
		clearButtonClicked();
		return ret;
	}

/*	public String initFacilityProfile() {
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		
		Integer fpId = null != getComplianceReport().getFpId() ? getComplianceReport()
				.getFpId() : getFacility().getFpId();

		fp.initFacilityProfile(fpId, false);
		
		return "facilityProfile";
	}
*/

	private boolean submitReport() {
		boolean ret = true;
		int reportId = complianceReport.getReportId();
		if (isInternalApp()) {
			// we only update this if we're not on the portal.
			// InfrastructureBO handles the updating of the CR on the portal
			// side
			try {
				complianceReport
						.setReportStatus(ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED);
				// make sure the To-Do item is created
				complianceReport.setReportBeingSubmitted(true);
				// set submitted date to current date
				complianceReport.setSubmittedDate(new Timestamp(System
						.currentTimeMillis()));
				complianceReport.setUserId(InfrastructureDefs
						.getCurrentUserId());
				
				setTargetedFacilityIds();

				// ALL THIS SHOULD BE DONE IN BO TO MAKE IT ONE TRANSACTION
				// should we use loadComplianceReport here?
				getComplianceReportService()
						.modifyComplianceReport(complianceReport,
								InfrastructureDefs.getCurrentUserId());

				setFacility(getFacilityService().retrieveFacility(
						complianceReport.getFacilityId()));

				complianceReport = getComplianceReportService()
						.retrieveComplianceReport(reportId);
				if (complianceReport != null) {
					reset();
					initializeAttachmentBean();
					complianceReport.refreshCompReportMonitorList();
				} else {
					ret = false;
					setComplianceReportDeleted(true);
					DisplayUtil
							.displayError("Compliance Report is not found with that number.");
				}
			} catch (Exception e) {
				handleException(new DAOException("Error submitting report.", e));
				ret = false;
			}
		}
		return ret;

	}

	@Override
	protected void setTargetedFacilityIds() {
		//TODO maybe redundant to have string ids and facilityList objects?
		List<String> facilityIds = new ArrayList<String>();
		List<FacilityList> facilities = new ArrayList<FacilityList>();
		for (FacilityList f : getBulkComplianceReportFacilities()) {
			if (f.isSelected()) {
				facilityIds.add(f.getFacilityId());
				facilities.add(f);
			}
		}
		complianceReport.setTargetFacilityIds(facilityIds.toArray(new String[0]));
		complianceReport.setTargetFacilities(facilities.toArray(new FacilityList[0]));
	}

	public String delete() {
		setEditMode(false);
		setCannotDelete(false);
		setCannotDeleteReason("");
		if (complianceReport.getAssocComplianceStatusEvents().size() > 0
				|| complianceReport.getInspectionsReferencedIn().size() > 0) {
			setCannotDelete(true);
			setCannotDeleteReason(
					"You cannot delete this Compliance Report while it is being referenced in other places.");
		}
		return "dialog:confirmComplianceReportDelete";
	}
	
    public boolean getDisplayComplianceEventsTable() {
    	return isCannotDelete() && !getComplianceReport().getAssocComplianceStatusEvents().isEmpty();
    }

    public boolean getDisplayReferencedInspectionsTable() {
    	return isCannotDelete() && !getComplianceReport().getInspectionsReferencedIn().isEmpty();
    }
    
	public void confirmDelete() {
		try {
			getComplianceReportService().deleteComplianceReport(
					complianceReport);
			// setComplianceReportDeleted(true);
			DisplayUtil.displayInfo("Compliance report deleted successfully.");
			handleBadDetail();
		} catch (RemoteException re) {
			handleException("Failed to delete Compliance report ", re);
			return;
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public String save() {
		boolean okToSave = true;
		String ret = "complianceDetail";
		try {

			if (ComplianceReportTypeDef.anyOtherType(complianceReport
					.getReportType())) {
				if (Utility
						.isNullOrEmpty(complianceReport.getOtherCategoryCd())) {
					DisplayUtil
							.displayError("The report category is required.");
					okToSave = false;
				}
			}
			
			if(complianceReport.isCemsComsRataRpt()) {
				if(null == complianceReport.getReportYear()) {
					DisplayUtil
					.displayError("Attribute Report Year is not set.");
					okToSave = false;
				} else {
					if(null == complianceReport.getReportQuarter()) {
						DisplayUtil
						.displayError("Attribute Report Quarter is not set.");
						okToSave = false;
					}
				}
			}
			
			if (okToSave) {
			
				if (!complianceReport.isCemsComsRataRpt()) {
					complianceReport.setReportYear(null);
					complianceReport.setReportQuarter(null);
				}
			
				if (newRecord) {
					// create the new record, without any validation (yet) as we
					// do that on submit
					getComplianceReportService().createComplianceReport(
							complianceReport, getFacility());
				} else {
					if (okToSave) {
						if ((okToSave && ((complianceReport.getDapcAccepted() != null && (!complianceReport
								.getDapcAccepted()
								.equals(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED))) || complianceReport
								.getDapcDateReviewed() != null))) {
							// they've set the report's disposition or reviewed
							// date

							if (okToSave
									&& complianceReport.isLegacyFlag()
									&& ((complianceReport.getReceivedDate() != null && Utility
											.isNullOrEmpty(complianceReport
													.getReceivedDate()
													.toString())) || complianceReport
											.getReceivedDate() == null)) {
								DisplayUtil
										.displayError("Must specify the received date.");
								okToSave = false;
							}

							if (okToSave
									&& ((complianceReport.getDapcDateReviewed() != null && Utility
											.isNullOrEmpty(complianceReport
													.getDapcDateReviewed()
													.toString())) || complianceReport
											.getDapcDateReviewed() == null)) {
								DisplayUtil
										.displayError("Must specify the review date since the report has been marked reviewed.");
								okToSave = false;
							}

							if (complianceReport
									.getDapcAccepted()
									.equals(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED)) {
								DisplayUtil
										.displayError("Must specify the Report Accepted status since the review date has been entered.");
								okToSave = false;
							}

							if (complianceReport.getDapcDateReviewed() != null
									&& complianceReport.getReceivedDate() != null) {
								Calendar cReviewed = Calendar.getInstance();
								cReviewed.setTime(complianceReport
										.getDapcDateReviewed());
								cReviewed.set(Calendar.HOUR_OF_DAY, 23);
								Calendar cReceived = Calendar.getInstance();
								cReceived.setTime(complianceReport
										.getReceivedDate());
								cReceived.set(Calendar.HOUR, 0);
								// we have both fields. Now, is the reviewed on
								// date on or after the submitted date?
								if (cReviewed.before(cReceived)) {
									DisplayUtil
											.displayError("The date the report was reviewed must be on or after the date the report was received.");
									okToSave = false;
								}
							}
						}
					}

					if (okToSave) {

						// staff person updating existing record
						if (!complianceReport
								.getDapcAccepted()
								.equals(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED)
								&& complianceReport.getDapcReviewer() == 0) {
							// they've set the accepted field - update the
							// reviewed
							// by if not aleady set
							complianceReport.setDapcReviewer(InfrastructureDefs
									.getCurrentUserId());
						}
						
						if (!isStars2Admin() && !complianceReport.isLegacyFlag()) {
							if (!Utility.isNullOrZero(complianceReport
									.getDapcReviewer())
									&& complianceReport.getDapcReviewer() != InfrastructureDefs
											.getCurrentUserId()) {
								DisplayUtil
										.displayError("Reviewed By: value must be set to the current user.");
								okToSave = false;
							}
						}
						
						if (okToSave) {

							if (complianceReport
									.getReportStatus()
									.equalsIgnoreCase(
											ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT)) {
								complianceReport.setValidated(false);
								setPassedValidation(false);
							}

							getComplianceReportService()
									.modifyComplianceReport(
											complianceReport,
											InfrastructureDefs
													.getCurrentUserId());

							setEditMode(false);
							DisplayUtil
									.displayInfo("Compliance Report updated successfully.");
						}
					}
				}
			}
		} catch (RemoteException e) {
			handleException(e);
		} finally {
			if (okToSave) {
				// only refresh if we did a database update/insert (this ensures
				// that if errors occurred the object in memory
				// retains all the unsaved data - making it easier for the
				// user to edit what they need to fix.

				if (complianceReport != null) {
					loadComplianceReport(complianceReport.getReportId(), true);

					((SimpleMenuItem) FacesUtil
							.getManagedBean("menuItem_compReportDetail"))
							.setDisabled(false);
				} else {
					ret = "complianceSearch";
				}
			}
		}
		return ret;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@Override
	public String findOutcome(String url, String ret) {
		return "complianceDetail";
	}

	@Override
	public Integer getExternalId() {
		setFromTODOList(true);
		return complianceReport.getReportId();
	}

	@Override
	public void setExternalId(Integer externalId) {
		setFromTODOList(true);
		loadComplianceReport(externalId, true);
		this.setWorkflowEnabled(true);
		SimpleMenuItem.setDisabled("menuItem_compReportDetail", false);
	}

	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		ValidationMessage[] ret = complianceReport.validate();

		messages = new ArrayList<ValidationMessage>(Arrays.asList(ret));

		// validate report reviewed status
		if (complianceReport.getDapcAccepted().equals(
				ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED)) {
			String msg = "Cannot complete task, Report "
					+ complianceReport.getReportCRPTId()
					+ " not yet Reviewed. Facility: "
					+ complianceReport.getFacilityId();
			ValidationMessage notReviewedVM = new ValidationMessage(
					"Compliance Report", msg, ValidationMessage.Severity.ERROR);
			notReviewedVM.setReferenceID(ValidationBase.COMPLIANCE_TAG + ":"
					+ getReportId() + ":" + COMPLIANCE_REPORT);
			messages.add(notReviewedVM);
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		Timestamp today = new Timestamp(cal.getTimeInMillis());

		if (complianceReport.getDapcDateReviewed() != null) {
			if (today.before(complianceReport.getDapcDateReviewed())) {
				messages.add(new ValidationMessage("reviewed_date",
						"The Reviewed Date cannot be in the future",
						ValidationMessage.Severity.ERROR,
						ValidationBase.COMPLIANCE_TAG + ":" + getReportId()
								+ ":" + COMPLIANCE_REPORT));
			}
		}

		return messages;
	}

	@Override
	public String getExternalNum() {
		return complianceReport.getReportCRPTId();
	}

	@Override
	public String toExternal() {
		return "complianceDetail";
	}

	public String goToSummaryPage() {
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.setFacilityId(complianceReport.getFacilityId());
		fp.submitProfileById();
		ComplianceReportSearch complianceReportSearch = (ComplianceReportSearch) FacesUtil
				.getManagedBean("complianceReportSearch");
		return complianceReportSearch.getFacilityComplianceReports();
	}

	public final void saveNote(ActionEvent actionEvent) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		// make sure note is provided
		if (tempNote.getNoteTxt() == null
				|| tempNote.getNoteTxt().trim().equals("")) {
			validationMessages.add(new ValidationMessage("noteTxt",
					"Attribute " + "Note" + " is not set.",
					ValidationMessage.Severity.ERROR, "noteTxt"));
		}

		if (validationMessages.size() > 0) {
			displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0]));
		} else {
			try {
				if (newNote) {
					getComplianceReportService().createNote(tempNote);
				} else {
					getComplianceReportService().modifyNote(tempNote);
				}

			} catch (RemoteException e) {
				DisplayUtil.displayError("cannot save note");
				handleException(e);
				return;
			}

			tempNote = null;
			reloadNotes();
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	/**
	 * Reload notes for current compliance.
	 */
	private final String reloadNotes() {
		loadNotes(complianceReport.getReportId());
		return FacesUtil.getCurrentPage(); // stay on same page
	}

	private void loadNotes(int reportId) {
		try {
			Note[] notes = getComplianceReportService().retrieveNotes(reportId);
			complianceReport.setNotes(notes);
		} catch (RemoteException ex) {
			DisplayUtil.displayError("cannot load compliance report notes");
			handleException(ex);
			return;
		}
	}

	public final String startAddNote() {
		tempNote = new ComplianceReportNote();
		tempNote.setUserId(getCurrentUserID());
		tempNote.setReportId(complianceReport.getReportId());
		tempNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
		tempNote.setNoteTypeCd(NoteType.DAPC);
		newNote = true;
		noteReadOnly = false;

		return NOTE_DIALOG_OUTCOME;
	}

	public final String startEditNote() {
		newNote = false;
		tempNote = new ComplianceReportNote(modifyNote);
		if (tempNote.getUserId().equals(getCurrentUserID())) {
			noteReadOnly = false;
		} else {
			noteReadOnly = true;
		}

		return NOTE_DIALOG_OUTCOME;
	}

	public final ComplianceReportNote getTempNote() {
		return tempNote;
	}

	public final void setTempNote(ComplianceReportNote tempNote) {
		this.tempNote = tempNote;
	}

	public final ComplianceReportNote getModifyNote() {
		return modifyNote;
	}

	public final void setModifyNote(ComplianceReportNote modifyNote) {
		this.modifyNote = modifyNote;
	}

	public Integer getCurrentUserID() {
		return InfrastructureDefs.getCurrentUserId();
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

	public void cancelEditNote(ActionEvent actionEvent) {
		tempNote = null;
		noteReadOnly = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void closeViewNote(ActionEvent actionEvent) {
		tempNote = null;
		noteReadOnly = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}

	private void handleBadDetail() {
		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_compReportDetail"))
				.setDisabled(true);
		updateSearch();
		setPopupRedirectOutcome("complianceSearch");
		complianceReport = null;
		setComplianceReportDeleted(true);
	}

	public final void close() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public boolean isSubmittedFromPortal() {
		Integer userId = this.complianceReport.getUserId();
		if (userId.equals(CommonConst.GATEWAY_USER_ID)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isAdmin() {
		return isStars2Admin();
	}

	@Override
	public void updateSearch() {
		ComplianceReportSearch crs = (ComplianceReportSearch) FacesUtil
				.getManagedBean("complianceReportSearch");
		crs.submitSearch();
	}
	
	public void associateRptWithCurrentFacility() {

		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}

		try {
			Facility facility = getFacilityService().retrieveFacility(
					getComplianceReport().getFacilityId(), false);
			if (null != facility) {
				Integer fpId = facility.getFpId();
				Integer lastModified = facility.getLastModified();
				// update only if the report is not already associated with the
				// current version of the facility inventory
				if (!getComplianceReport().getFpId().equals(fpId)
						|| !getFacility().getLastModified().equals(lastModified)) {
				
					logger.debug("Associating compliance report with current facility version (fpId = " + fpId + ")");
					getComplianceReportService().associateRptWithCurrentFacility(
							getComplianceReport(), facility, InfrastructureDefs.getCurrentUserId());
					
					// retrieve the update report
					setComplianceReport(getComplianceReportService()
							.retrieveComplianceReport(getReportId(), false));
					
					complianceReport.refreshCompReportMonitorList();
					
					// update facility
					setFacility(facility);
					
					// user has to validate the report again before it can be submitted
					setPassedValidation(false);
	
					DisplayUtil
							.displayInfo("Compliance report is now associated with the current version of the facility inventory");
				} else {
					DisplayUtil
							.displayInfo("Compliance report is already associated with the current version of the facility inventory");
				}
			} else {
				// should not happen
				DisplayUtil.displayError("Failed to retrieve facility associated with this compliance report");
				throw new DAOException("Failed to retrieve facility "
						+ getComplianceReport().getFacilityId());
			}
		} catch (RemoteException e) {
			DisplayUtil
					.displayError("Failed to assoicate the compliance report with the current version of the facility inventory");
			handleException(e);
		} 
	}

	public boolean isCannotDelete() {
		return cannotDelete;
	}

	public void setCannotDelete(boolean cannotDelete) {
		this.cannotDelete = cannotDelete;
	}

	public String getCannotDeleteReason() {
		return cannotDeleteReason;
	}

	public void setCannotDeleteReason(String cannotdeleteReason) {
		this.cannotDeleteReason = cannotdeleteReason;
	}
	
	public String edit() {
		super.edit();
		setNewRecord(false);  // newRecord could erroneously be true if user closed
		                      // new ComplianceReport by clicking the red x.
		return null;
	}

	
}
