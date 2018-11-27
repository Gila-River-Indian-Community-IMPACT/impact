package us.oh.state.epa.stars2.portal.compliance;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportAttachment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.def.ComplianceReportAcceptedDef;
import us.oh.state.epa.stars2.def.ComplianceReportAllSubtypesDef;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.compliance.ComplianceReportsCommon;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.portal.company.CompanyProfile;

@SuppressWarnings("serial")
public class ComplianceReports extends ComplianceReportsCommon {
	private static final String HOME = "home";
	private String facilityId;

	private boolean submitAllowed;
	private Task task;
	private Task depFacTask;
	private MyTasks myTasks;
	private CompanyProfile companyProfile;

	private boolean testMode = false;

	private InfrastructureService infrastructureService;

	public ComplianceReports() {
		super();
		this.setTag(ValidationBase.COMPLIANCE_TAG);
		setReadOnlyReport(false);
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(
			InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}
	
	public CompanyProfile getCompanyProfile() {
		return companyProfile;
	}

	public void setCompanyProfile(CompanyProfile companyProfile) {
		this.companyProfile = companyProfile;
	}

	public Task getDepFacTask() {
		return depFacTask;
	}

	public void setDepFacTask(Task depFacTask) {
		this.depFacTask = depFacTask;
	}

	@Override
	public FacilityList[] getBulkComplianceReportFacilities() {
		FacilityList[] rawFacilities = getCompanyProfile().getAuthorizedFacilities();
		List<FacilityList> facilities = new ArrayList<FacilityList>();
		for (int i=0; i < rawFacilities.length; i++) {
			if (!rawFacilities[i].getFacilityId().equals(
					getFacility().getFacilityId() )) {
				facilities.add(rawFacilities[i]);
			}
		}
		return facilities.toArray(new FacilityList[0]);
	}
	
	
	// not sure about this SCG
	public String from2ndLevelMenu() {
		if (testMode)
			logger.error("in from2ndLevelMenu with reportId " + getReportId());
		if (complianceReport != null) {
			loadComplianceReport(getReportId(), false); // refresh in case
														// facility data
														// changed.
		}
		logger.debug("GET A HANDLE ON THIS in from2ndLevelMenu with reportId "
				+ getReportId());
		return "complianceDetail";
	}

	public boolean isSubmitAllowed() {
		// return !readOnly && !applicationDeleted && application.getValidated()
		// && application.getSubmittedDate() == null;

		return submitAllowed;
	}

	public void setSubmitAllowed(boolean submitAllowed) {
		this.submitAllowed = submitAllowed;
	}

	public String getReportType() {
		return complianceReport.getReportType();
	}

	public void setReportType(String reportType) {
		complianceReport.setReportType(reportType);
	}

	public final String startNewReport() {
		setEditable(true);
		try {
			Facility f = getFacilityService().retrieveFacility(
					this.getComplianceReport().getFacilityId(), true);
			if (testMode)
				logger.error("Attempt access to staging facility inventory "
						+ complianceReport.getFacilityId());
			if (f == null) {
				if (testMode)
					logger.error("staging profile not found");
				// otherwise use the current one in the read-only database.
				f = getFacilityService().retrieveFacility(
						complianceReport.getFacilityId());
			}
			setFacility(f);
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Create new report failed.");
			return null;
		}
		complianceReport = new ComplianceReport();
		complianceReport.setCorrectedReport(false);
		complianceReport.setReportBeingSubmitted(false);
		complianceReport.setReportType(null);
		complianceReport.setTvccReportingYear(initializeTvccPeriods());
		complianceReport.setFacilityId(getFacility().getFacilityId());
		complianceReport.setFacilityNm(getFacility().getName());
		complianceReport.setUserId(CommonConst.GATEWAY_USER_ID);
		complianceReport
				.setDapcAccepted(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED);
		complianceReport
				.setReportStatus(ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT);
		return "dialog:newComplianceReport";
	}

	public final String startNewReport(String reportType, Task task) {
		// this is a gateway (Portal) record

		setEditable(true);
		selectNoneF();
		this.setTask(task);
		// need to go retrieve the facility
		// look for a current facility in staging
		try {
			Facility f = getFacilityService().retrieveFacility(
					getFacility().getFacilityId(), true);
			if (testMode)
				logger.error("Attempt access to staging facility inventory "
						+ complianceReport.getFacilityId());
			if (f == null) {
				if (testMode)
					logger.error("staging profile not found");
				// otherwise use the current one in the read-only database.
				f = getFacilityService().retrieveFacility(
						getFacility().getFacilityId());
			}
			setFacility(f);
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Create new report failed.");
			return null;
		}
		complianceReport.setCorrectedReport(false);
		complianceReport.setReportType(reportType);
		complianceReport.setFacilityId(getFacility().getFacilityId());
		complianceReport.setFacilityNm(getFacility().getName());
		complianceReport.setUserName(task.getUserName());
		complianceReport.setUserId(CommonConst.GATEWAY_USER_ID);
		complianceReport
				.setDapcAccepted(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED);
		complianceReport
				.setReportStatus(ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT);

		task.setComplianceReport(complianceReport);
		task.setFacility(getFacility());
		return "dialog:newComplianceReport";
	}

	public final void cancelNewReport(ActionEvent actionEvent) {
		FacesUtil.returnFromDialogAndRefresh();
		disableThirdLevelMenu();
	}
	
	public final String cancelEdit() {
		String ret = "";
		ret = FacesUtil.getCurrentPage();
		setEditMode(false);
		loadComplianceReport(complianceReport.getReportId(), false);
		return ret;
	}

	public final String createCompReport() {
		String ret = "";
		try {
			if (!myTasks.facilityExistsInternally()) {
				DisplayUtil.displayError("Facility "
						+ myTasks.getFacility().getFacilityId() + "with facility ID "
						+ facilityId + " does not exist; " + myTasks.contactMsg);
				return ret;
			}

			Task facTask = null;
			if (getComplianceReport().isCemsComsRataRpt()) {
				facTask = myTasks.changeFacilityProfileTask(3);
				if (facTask == null) {
					return ret;
				}
				setDepFacTask(facTask);
			}
			
			Task newTask = MyTasks.createNewTask(
					ComplianceReportTypeDef
							.getData()
							.getItems()
							.getItemDesc(
									ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER),
					Task.TaskType.CR_OTHR, 
					getComplianceReport().isCemsComsRataRpt(), 
					null == facTask? null : facTask.getTaskId(),
					myTasks.getFacility().getFacilityId(), 
					null == facTask? null : facTask.getFpId(), 
					"current",
					myTasks.getCorePlaceId(), myTasks.getLoginName());
			if (newTask != null) {
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_compliance"))
						.setRendered(true);
			}

			ComplianceReportSearch compReportSearch = (ComplianceReportSearch) FacesUtil
					.getManagedBean("complianceReportSearch");
			compReportSearch.setFacilityId(myTasks.getFacility().getFacilityId());
			setFacility(myTasks.getFacility());
			startNewReport(getReportType(), newTask);
		} catch (Exception e) {
			handleException(new RemoteException(
					"Failed in Creating CR task", e));
			DisplayUtil
					.displayError("System error initializing new report");
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_compliance"))
					.setRendered(false);
			throw e;
		}
		return ret;
	}
	
	public final void createNewReport(ActionEvent event) {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		if (Utility.isNullOrEmpty(complianceReport.getOtherCategoryCd())) {
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
			createCompReport();
			// need to go retrieve the facility
			// look for a current facility in staging
			Facility f = getFacilityService().retrieveFacility(
					complianceReport.getFacilityId(), true);
			if (testMode)
				logger.error("Attempt access to staging facility inventory "
						+ complianceReport.getFacilityId());
			if (f == null) {
				if (testMode)
					logger.error("staging profile not found");
				// otherwise use the current one in the read-only database.
				f = getFacilityService().retrieveFacility(
						complianceReport.getFacilityId());
			}
			setFacility(f);
			task.setFacility(getFacility()); // so task can display facility
												// name
			task.setTaskDescription(ComplianceReportTypeDef.getData()
					.getItems().getItemDesc(getReportType()));
			logger.error("report type == " + getReportType());
			if (getReportType().equals(
					ComplianceReportTypeDef.COMPLIANCE_TYPE_ONE)) {
				task.setTaskType(Task.TaskType.CR_ONE);
				logger.error("task type = one");
			} else if (getReportType().equals(
					ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER)) {
				task.setTaskType(Task.TaskType.CR_OTHR);
				logger.error("task type = othr");
			} else if (getReportType().equals(
					ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS)) {
				task.setTaskType(Task.TaskType.CR_CEMS);
				logger.error("task type = cems");
			} else {
				task.setTaskType(Task.TaskType.CR_GENERIC);
				logger.error("task type = generic compliance report");
			}

			if (null != getDepFacTask() && complianceReport.isCemsComsRataRpt()) {
				task = getInfrastructureService().createTask(getTask(),getDepFacTask());				
			} else {
				task = getInfrastructureService().createTask(getTask());
			}
			String ret = HOME;
			ret = myTasks.renderComplianceReportMenu(task, false);

			myTasks.setPageRedirect(ret);
			myTasks.refreshTasks(getTask().getFacility().getFacilityId());
			myTasks.setTaskId(task.getTaskId());

			if (complianceReport == null) {
				DisplayUtil
						.displayError("Failed to create new Compliance Report");
			} else {
				complianceReport = getComplianceReportService()
						.retrieveComplianceReport(
								complianceReport.getReportId());
				if (complianceReport != null) {

					reset();

					setPassedValidation(false);

					complianceReport.setValidated(false);

					initializeAttachmentBean();
					
					complianceReport.refreshCompReportMonitorList();

					DisplayUtil
							.displayInfo("Sucessfully created new Compliance Report.");

				} else {
					setComplianceReportDeleted(true);
					DisplayUtil
							.displayError("Failed to create new Compliance Report");
				}

				setPopupRedirectOutcome("complianceDetail");
			}

			clearButtonClicked();
			FacesUtil.returnFromDialogAndRefresh();
		} catch (RemoteException e) {
			clearButtonClicked();
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
	public final String viewDetail() {
		// THIS IS USED ONLY TO VIEW A COMPLIANCE REPORT THAT IS Read/Only.
		// reportId and facilityId are set from the .jsp page.
		this.setPassedValidation(false);
		this.setEditable(false);
		this.setEditMode(false);
		this.setReadOnlyReport(true);
		String rtn = null;

		complianceReport = null;

		// load compliance report from read only db
		logger.debug("viewDetail getReportId() = " + getReportId());
		loadComplianceReport(getReportId(), true);

		if (complianceReport != null) {
			if (isPortalApp()) {
				((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_compliance"))
					.setRendered(false);
			}

			if (this.getComplianceReport().getReportId() != null) {
				rtn = "home.compliance.complianceReportDetails";

			} else {
				rtn = null;
				DisplayUtil
						.displayError("Compliance Report is not found with that number.");
			}
		}

		clearButtonClicked();
		return rtn;
	}

	public String submit() {
		/*
		 * 1. validate the report 2. if valid 3. change status to 'submitted' 4.
		 * update 5. if (portal) transfer data across DMZ
		 */
		if (complianceReport == null) {
			logger.error("NULL compliance report in submit!!!");
		} else {
			logger.debug(" submitting compliance report "
					+ complianceReport.getReportId());
		}
		String ret = "";
		if (!validateReport()) {
			DisplayUtil
					.displayError("Report is invalid and may not be submitted until errors are fixed.");
		} else {
			try {
				
				setTargetedFacilityIds();

				ret = applySubmitFromPortal();

			} catch (Exception e) {
				// we've been seeing a few cases where air services users are
				// seeing 500 errors when the click the Submit button.
				// This catch is here to see if we can get more information
				// about these problems.
				logger.error("Submit resulted in unexpected exception", e);
			}
		}
		return ret;
	}
	

	@Override
	protected void setTargetedFacilityIds() {
		List<String> facilityIds = new ArrayList<String>();
		List<FacilityList> facilities = new ArrayList<FacilityList>();

		if (companyProfile.getAuthorizedFacilities() != null) {
			for (FacilityList f : companyProfile.getAuthorizedFacilities()) {
				if (f.isSelected()) {
					facilityIds.add(f.getFacilityId());
					facilities.add(f);
				}
			}
		}
		complianceReport.setTargetFacilityIds(facilityIds.toArray(new String[0]));
		complianceReport.setTargetFacilities(facilities.toArray(new FacilityList[0]));
	}

	public String save() {
		boolean okToSave = true;
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
		if(okToSave) {
			try {
				// if (newRecord) {
				// //create the new record, without any validation (yet) as we do
				// that on submit
				// getComplianceReportService().createComplianceReport(complianceReport);
				// } else {
				if ((complianceReport.getDapcAccepted() != null)
						&& (!complianceReport
								.getDapcAccepted()
								.equals(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED))) {
					// they've set the report's disposition
					if (complianceReport.getDapcDateReviewed() != null) {
	
						Calendar cReviewed = Calendar.getInstance();
						cReviewed.setTime(complianceReport.getDapcDateReviewed());
						cReviewed.set(Calendar.HOUR_OF_DAY, 23);
						Calendar cReceived = Calendar.getInstance();
						cReceived.setTime(complianceReport.getSubmittedDate());
						cReceived.set(Calendar.HOUR_OF_DAY, 0);
						// we have both fields. Now, is the reviewed on date on or
						// after the submitted date?
						if (cReviewed.before(cReceived)) {
							DisplayUtil
									.displayError("The date the report was reviewed must be on or after the date the report "
											+ "was received.");
							okToSave = false;
						} else {
							logger.debug("dates are ok");
						}
					} else {
						DisplayUtil
								.displayError("The date the report was reviewed and whether deviations were reported must be "
										+ "indicated before the report is set to a reviewed state.");
						okToSave = false;
					}
				} else {
					logger.debug("things were 'not yet reviewed'");
				}
	
				if (okToSave) {
					
					if (!complianceReport.isCemsComsRataRpt()) {
						complianceReport.setReportYear(null);
						complianceReport.setReportQuarter(null);
					}
					
					getComplianceReportService().modifyComplianceReport(
							complianceReport, getTask(),
							CommonConst.GATEWAY_USER_ID);
					myTasks.refreshTasks(getTask().getFacility().getFacilityId());
					setTask(getInfrastructureService().retrieveTask(
							getTask().getTaskId()));
					getTask().setFacility(getFacility());
					setEditMode(false);
				}
				// }
			} catch (RemoteException e) {
				handleException(e);
			} finally {
				if (okToSave) {
					try {
						// only refresh if we did a database update/insert (this
						// ensures that if errors occred the object in memory
						// retains all the unsaved data - making it easier for the
						// user to edit what they need to fix.
	
						loadComplianceReport(complianceReport.getReportId(), false);
	
						complianceReport = getComplianceReportService()
								.retrieveComplianceReport(
										complianceReport.getReportId());
						if (complianceReport != null) {
							reset();
							initializeAttachmentBean();
							complianceReport.refreshCompReportMonitorList();
						} else {
							setComplianceReportDeleted(true);
							DisplayUtil
									.displayError("Compliance Report is not found with that number.");
							return null;
						}
					} catch (RemoteException ee) {
						setComplianceReportDeleted(true);
						handleException(ee);
					}
				}
			}
		}
		return "complianceDetail";
	}

	public String validationDlgAction() {
		String ret = super.validationDlgAction();
		logger.debug("validationDlgAction ... ret = " + ret);
		if (ret != null) {
			if (ret.equals(COMPLIANCE_REPORT)) {
				if (isPortalApp() && getMyTasks() != null) {
					Task tmpTask = getMyTasks().findComplianceReportTask(
							this.complianceReport.getReportId());
					if (tmpTask != null) {
						getMyTasks().setTaskId(tmpTask.getTaskId());
						getMyTasks().submitTask(true);
					}
				}
			}
		}
		logger.debug("validationDlgAction ... ret 2 = " + ret);
		return ret;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public void setReadOnlyReportId(int reportId) {
		// THIS IS USED ONLY TO VIEW A COMPLIANCE REPORT THAT IS Read/Only.
		// this.reportId = reportId;
		setReportId(reportId);
	}

	public final Task getTask() {
		return task;
	}

	public final MyTasks getMyTasks() {
		return myTasks;
	}

	public final void setMyTasks(MyTasks myTasks) {
		this.myTasks = myTasks;
	}

	public final boolean getSubmitAllowed() {
		boolean ret = isSubmitAllowed();
		ret = myTasks.isHasSubmit() && isSubmitAllowed();
		return ret;
	}

	protected void loadReportData() {
		myTasks.setPageRedirect(null);
	}

	public final String applySubmitFromPortal() {
		String ret = null;
		// don't want duplicate attachments
		task.getAttachments().clear();
		task.setComplianceReport(complianceReport);
		task.setFacility(getFacility()); // SCG
		task.getComplianceReport().setReportBeingSubmitted(true);

		SubmitTask submitTask = (SubmitTask) FacesUtil
				.getManagedBean("submitTask");
		boolean attestationRequired = true;
		submitTask.setDapcAttestationRequired(attestationRequired);
		submitTask.setRoSubmissionRequired(attestationRequired);
		submitTask.setNonROSubmission(!myTasks.isHasSubmit());
		submitTask.setTitle("Submit Compliance Report");

		submitTask.setDocuments(preparePrintableDocumentList());
		ret = submitTask.confirm();
		logger.debug("Compliance Report submittal ended. Result code is: " + ret);
		return ret;
	}

	public final boolean isAttestationRequired() {
		boolean attestationRequired = false;
		// this is an 'OTHER' report type
		attestationRequired = task.getFacility() != null
				&& ((PermitClassDef.TV.equals(task.getFacility()
						.getPermitClassCd())
						|| PermitClassDef.SMTV.equals(task.getFacility()
								.getPermitClassCd()) || PermitClassDef.NTV
							.equals(task.getFacility().getPermitClassCd())) && ComplianceReportAllSubtypesDef
						.needTvAttestation(
								complianceReport.getOtherCategoryCd(), logger));
		return attestationRequired;
	}

	public final void setTask(Task task) {
		this.task = task;
	}
	
	// when called from the submit popup page, want to print the trade
	// secret version if there is one, otherwise the public one.
	public final String printComplianceReportAtSubmit() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		try {
			setPrintableDocumentList(preparePrintableDocumentList());

		} finally {
			clearButtonClicked();
		}
		return "dialog:viewPDF";
	}

	public final String getAttestationDocURL() {
		if (complianceReport == null) {
			String s = "Error:  There is no Compliance Report.  ReportId="
					+ getReportId();
			DisplayUtil.displayError(s);
			logger.error(s, new Exception());
			return null;
		}
		String url = null;
		ComplianceReportAttachment doc;
		try {
			doc = getComplianceReportService().createCRAttestationDocument(
					complianceReport);
			if (doc != null) {
				url = doc.getDocURL();
			} else {
				// this should never happen
				logger.error("Error creating attestation document for compliance report: "
						+ complianceReport.getReportId());
			}
		} catch (RemoteException e) {
			handleException(e);
		}
		return url; // stay on same page
	}

	public boolean isAdmin() {
		return false;
	}

	@Override
	public void updateSearch() {
		ComplianceReportSearch crs = (ComplianceReportSearch) FacesUtil
				.getManagedBean("complianceReportSearch");
		crs.submitSearch();
	}
}
