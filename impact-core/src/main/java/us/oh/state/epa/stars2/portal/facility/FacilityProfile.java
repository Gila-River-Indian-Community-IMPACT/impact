package us.oh.state.epa.stars2.portal.facility;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.model.SortCriterion;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.ceta.FceSearch;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ApiGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.application.ApplicationDetail;
import us.oh.state.epa.stars2.portal.application.ApplicationSearch;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.portal.compliance.ComplianceReportSearch;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.portal.permit.PermitSearch;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;
import us.oh.state.epa.stars2.webcommon.correspondence.CorrespondenceSearch;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;
import us.wy.state.deq.impact.app.contact.ContactDetail;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.def.FCEInspectionReportStateDef;
import us.wy.state.deq.impact.webcommon.contact.CreateContact;

@SuppressWarnings("serial")
public class FacilityProfile extends FacilityProfileBase {
	// private Contact saveContact;
	private Task task;
	private Task contactTask;
	private boolean saveStaging = false;
	private Integer corePlaceId;
	private boolean frozenFacilityExist;
	private MyTasks myTasks;
	private List<Permit> allEuPermits;
	private List<SelectItem> contactsList;
	
	private CompanyService companyService;
	
	@Autowired private FceSearch fceSearch;
	
	public FceSearch getFceSearch() {
		return fceSearch;
	}

	public void setFceSearch(FceSearch fceSearch) {
		this.fceSearch = fceSearch;
	}

	public FacilityProfile() {
		super();
	}

	private MyTasks getMyTasks() {
		if (myTasks == null) {
			myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
		}
		return myTasks;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
	
	

	public String getUserName() {
		return getMyTasks().getUserName();
	}

	public boolean getFacilityById(String facilityId) {
		Facility ret = null;
		try {
			ret = getFacilityService().retrieveFacility(facilityId);
			if (ret == null) {
				DisplayUtil.displayInfo("Frozen facility does not exist.");
				return false;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing facility failed.");
		}
		return true;
	}

	public Facility getFacilityByCorePlaceId(Integer corePlaceId,
			boolean staging) throws RemoteException {
		this.staging = staging;
		allContacts = null;
		try {
			facility = getFacilityService().retrieveFacilityByCorePlaceId(corePlaceId,
					staging);
			if (facility != null) {
				fpId = facility.getFpId();
				facilityId = facility.getFacilityId();
				setOtherParts();
			}
		} catch (RemoteException re) {
			throw re;
		}
		return facility;
	}

	public Facility getFacilityByFacilityId(String facilityId, boolean staging)
			throws RemoteException {
		boolean origStaging = this.staging;
		this.staging = staging;
		allContacts = null;
		try {
			facility = getFacilityService().retrieveFacilityByFacilityId(facilityId,
					staging);
			if (facility != null) {
				fpId = facility.getFpId();
				this.facilityId = facility.getFacilityId();
				setOtherParts();
			}
		} catch (RemoteException re) {
			throw re;
		}
		this.staging = origStaging;
		return facility;
	}

	private void resetFacilityId() {
		if (isPortalApp()) {
			facilityId = getMyTasks().getFacility().getFacilityId();
			frozenFacilityExist = getFacilityById(facilityId);
		} else if (isPublicApp()) {
			getCurrentFacilityData();
		}
	}

	public final String fromHomeContacts(boolean stagingDb) {
		resetFacilityId();
		setEditable1(false);

		if (!frozenFacilityExist) {
			DisplayUtil
					.displayInfo("Please submit facility before updating and submitting contacts.");
		}

		String ret = getFacilityContacts(stagingDb);
		if (ret != null) {
			setFacilityContacts();
		}

		this.staging = stagingDb;
		return ret;
	}

	public String getFacilityContacts(boolean stagingDb) {
		String ret = CONTACTS_OUTCOME;
		setEditable1(false);
		try {
			allContacts = getFacilityService().retrieveFacilityContacts(facilityId,
					stagingDb);
			setFacilityContacts();
			setFacOwnerContacts();
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing facility contacts failed.");
			ret = null;
		}
		return ret;
	}

	public final String getEventLogs() {
		staging = false;
		resetFacilityId();
		getEventLog();
		return "facilities.profile.eventLog";
	}
	
	public final String getHomeEventLogs() {
		getEventLogs();

		return "home.eventLogs";
	}

	public final String fromHomeFacilityHistory() {
		staging = false;
		resetFacilityId();
		getFacilityHistory();
		return "facilities.profile.history";
	}

	public final String fromHomeFedRules() {
		return "facilities.profile.homeFedRules";
	}

	public final String fromHomeEuSummary() {
		if (isPublicApp()) {
			setHideInvalidEUs(true);
			toggleEUList();
		}
		
		return "facilities.profile.homeEuSummary";
	}

	public final String fromHomeCeSummary() {
		return "facilities.profile.homeCeSummary";
	}

	public final String fromHomeEgpSummary() {
		return "facilities.profile.homeEgpSummary";
	}
	
	public final String fromHomeContinuousMonitors() {
		return "facilities.profile.homeContinuousMonitors";
	}
	
	// not used - must explicitly retrieve limits each time
	//public final String fromHomeFacilityCemComLimits() {
	//	return "facilities.profile.homeFacilityCemComLimits";
	//}

	/* START CODE: owners/contacts */

	public String submitContacts() {
		// List<ValidationMessage> validationMessages;

		if (!getFacilityById(facilityId)) {
			DisplayUtil
					.displayError("Submitting facility contacts failed; Please submit facility before updating and submitting contacts.");
			return CONTACTS_OUTCOME;
		}

		contactTask.setFacilityContacts(allContacts);
		contactTask.setFacility(facility);
		SubmitTask submitTask = (SubmitTask) FacesUtil
				.getManagedBean("submitTask");
		submitTask.setTitle("Submit Facility Contacts");
		submitTask.setRoSubmissionRequired(false);
		submitTask.setDapcAttestationRequired(false);
		submitTask.setDocuments(prepareFacilityContactsDocument());

//		submitTask.setNonROSubmission(!getMyTasks().isHasSubmit());
		
		return submitTask.confirm();
	}
	

	private List<Document> prepareFacilityContactsDocument() {
		String user = InfrastructureDefs.getCurrentUserAttrs().getUserName();
		List<Document> documents = new ArrayList<Document>();

		try {
			documents = getContactService().getPrintableDocumentList(
					facility, user);
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Unable to generate facility contacts document");
			handleException("Stack Test " + facility.getFacilityId(), re);
		}
		return documents;
	}


	/* END CODE: owners/contacts */

	/* START CODE: facility applications, permits, reports, invoices */
	/* NOTE: some can go to base if have common Search code */

	public final String getApplications() {
		staging = false;
		resetFacilityId();

		ApplicationSearch applSearch = (ApplicationSearch) FacesUtil
				.getManagedBean("applicationSearch");
		applSearch.reset();
		applSearch.setFromFacility("true");
		applSearch.setFacilityID(facilityId);
		applSearch.setNewApplicationFacilityID(facilityId);
		applSearch.search();
		if (applSearch.getApplications() == null) {
			DisplayUtil.displayError("Search Application failed.");
		}
		return "facilities.profile.applications";
	}

	public final String getHomeApplications() {
		getApplications();

		return "home.applications";
	}
	
	public final String getHomeComplianceReports() {
		getComplianceReports();
		setEditStaging(false);
		return "home.complianceReports";
	}
	
	public final String getInspectionReports() {
		getHomeInspectionReports();
		return "facilities.profile.inspectionReports";
	}

	public final String getHomeInspectionReports() {
		staging = false;
		resetFacilityId();

		FceSearch fceSearch = getFceSearch();
		fceSearch.reset();
		fceSearch.setFacilityId(facilityId);
		fceSearch.setCompleted("Y");
		// need to show only finalized inspections on the portal
		fceSearch.getInspectionReportStateCds().clear();
		fceSearch.getInspectionReportStateCds().add(FCEInspectionReportStateDef.FINAL);
		fceSearch.submitSearch(true);
		if (fceSearch.getFceList() == null) {
			DisplayUtil
					.displayError("Search Inspection Reports for the facility failed.");
		} else if (fceSearch.getFceList().length == 0) {
			DisplayUtil
					.displayInfo("There are no Inspection Reports available for this facility.");
		}
		return "home.inspectionReports";
	}

	public final String getComplianceReports() {
		staging = false;
		resetFacilityId();

		ComplianceReportSearch comReportSearch = (ComplianceReportSearch) FacesUtil
				.getManagedBean("complianceReportSearch");
		comReportSearch.reset();
		comReportSearch.setFromFacility("true");
		comReportSearch.setFacilityId(facilityId);
		comReportSearch.submitSearch();
		return "facilities.profile.compReports";
	}

	public final String getHomeEmissionsInventories() {
		getEmissionsReports();
		setEditStaging(false);
		return "home.emissionsInventories";
	}
	
	public final String getPermits() {
		staging = false;
		resetFacilityId();

		PermitSearch permitSearch = (PermitSearch) FacesUtil
				.getManagedBean("permitSearch");
		permitSearch.reset();
		permitSearch.setFromFacility("true");
		permitSearch.setFacilityID(facilityId);

		// Node portalNode = Config.findNode("app.defaultEPAPortal");
		// String epaPortal; // DENNIS get lots of permits....
		// if (portalNode != null) {
		// epaPortal = portalNode.getText();
		// if(epaPortal != null && epaPortal.contains("testapps")){
		// permitSearch.setFacilityID(facilityId.substring(0, 4) + "%");
		// logger.error("Doing facility permit search with facility string " +
		// permitSearch.getFacilityID());
		// }
		// }

		permitSearch.search();
		if (permitSearch.getPermitsNSR() == null && permitSearch.getPermitsTV() == null) {
			DisplayUtil.displayError("Search Permits for the facility failed.");
		} else if (permitSearch.getPermitsNSR().size() == 0 && permitSearch.getPermitsTV().size() == 0) {
        		DisplayUtil.displayInfo("There are no Permits available for this facility.");
		}
		return "facilities.profile.permits";
	}
	
	public final String getHomePermits() {
		getPermits();

		return "home.permits";
	}

	public final String getEmissionsReports() {
		staging = false;
		resetFacilityId();

		ReportSearch reportSearch = (ReportSearch) FacesUtil
				.getManagedBean("reportSearch");
		reportSearch.reset();
		reportSearch.setFromFacility("true");
		reportSearch.setFacilityId(facilityId);
		reportSearch.submitSearch();
		reportSearch.setPopupRedirectOutcome(null);
		if (reportSearch.getReports() == null) {
			DisplayUtil
					.displayError("Search emissions inventories for the facility failed.");
		} else if (reportSearch.getReports().length == 0) {
                DisplayUtil.displayInfo("There are no Emissions Inventories available for this facility.");
		}
		return "facilities.profile.emissionReports";
	}

	public final String createNewEmissionReport() {
		// This supports the "newReport" button from 3rd level menu.
		// Delete this and button or tie to MyTasks
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		String s = null;
		try {
			resetFacilityId();
			logger.error("This is no longer used.-- otherwise tell Dennis");
			ReportProfileBase reportProfile = (ReportProfileBase) FacesUtil
					.getManagedBean("reportProfile");
			reportProfile.setInStaging(false);
			s = reportProfile.createNewEmissionReport(facilityId);
		} finally {
			clearButtonClicked();
		}
		return s;
	}

	public final String getReportingCategory() {
		staging = false;
		resetFacilityId();
		ReportProfileBase reportProfile = (ReportProfileBase) FacesUtil
				.getManagedBean("reportProfile");
		reportProfile.setFpId(getMyTasks().getFacility().getFpId());
		reportProfile.setSavedFpId(null);
		return "facilities.profile.reportingCategory";
	}

	public final String getCorrespondence() {
		CorrespondenceSearch corrSearch = (CorrespondenceSearch) FacesUtil
				.getManagedBean("correspondenceSearch");
		corrSearch.reset();
		corrSearch.setFromFacility("true");
		corrSearch.setFromEnforcementAction("false");
		corrSearch.setFacilityId(facilityId);
		corrSearch.submitSearch();
		return "facilities.profile.correspondence";
	}

	/* END CODE: facility applications, permits, reports, invoices */

	public final void createStagingFacility() {
		try {
			getFacilityService().createStagingFacilityProfile(fpId);
		} catch (RemoteException re) {
			handleException(re);
		}
		refreshFacility();
	}

	public final String viewFrozenFacility() {
		staging = false;
		saveStaging = false;
		byClickEpaEmuId = null;
		expandOpt = 0;
		selEpaEmuIds = null;

		try {
			facility = getFacilityService().retrieveFacilityByCorePlaceId(corePlaceId);
			if (facility != null) {
				fpId = facility.getFpId();
			} else {
				DisplayUtil.displayInfo("Frozen facility does not exist.");
				return null;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing facility failed.");
		}

		refreshFacility();
		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_facility"))
				.setRendered(false); // turn tab off since frozen profile not in
										// staging.
		return "facilityProfile";
	}

	// requires fpId be set to view frozen facility
	public final String viewFrozenHistFacility() {
		staging = false;
		saveStaging = false;
		byClickEpaEmuId = null;
		expandOpt = 0;
		selEpaEmuIds = null;
		refreshFacility();
		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_facility"))
				.setRendered(false); // turn tab off since frozen profile not in
										// staging.
		return "facilityProfile";
	}

	/*
	 * public String submitHistoryProfile() { // reset facility in case of
	 * splitting current and immediately // navigating to // old current.
	 * staging = false; saveStaging = false; facility = null; ((SimpleMenuItem)
	 * FacesUtil.getManagedBean("menuItem_facility")).setRendered(false); //
	 * turn tab off since not in staging. return submitProfile(); }
	 */

	public final String submitFacilityProfileUpdates() {
		ValidationMessage[] validationMessages;
		boolean isValid = true;

		if (facility.getVersionId() != -1) {
			DisplayUtil
					.displayError("You cannot submit a historic facility inventory by itself. You can only submit a historic facility inventory as part of an emissions inventory that is associated with that historic facility inventory.");
			return null;
		}
		try {
			validationMessages = getFacilityService().submitFacilityProfile(
					facility.getFpId());

			if (validationMessages.length > 0) {
				isValid = printValidationMessages(validationMessages);
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayInfo("Unable to validate facility inventory to submit.");
			isValid = false;
		}

		refreshFacility();
		selectedTreeNode = facNode;
		current = facility.getFacilityId();
		nodeClicked();
		if (isValid == true) {
			task.setFacility(facility);
			SubmitTask submitTask = (SubmitTask) FacesUtil
					.getManagedBean("submitTask");
			submitTask.setRoSubmissionRequired(false);
			submitTask.setDapcAttestationRequired(false);
			submitTask.setTitle("Submit Facility Inventory");
			submitTask.setDocuments(getFacProfDocuments());
			return submitTask.confirm();
		}

		DisplayUtil
				.displayError("Submit of facility inventory failed due to validation problems.");
		return null;
	}

	public boolean disableSubmit() {
		String recon = (String) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("RECONCILATION");
		recon = "Y"; // for now

		return (!facility.isValidated()) || (!recon.equals("Y"))
				|| isDisabledUpdateButton();
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public boolean isSaveStaging() {
		return saveStaging;
	}

	public void setSaveStaging(boolean saveStaging) {
		this.saveStaging = saveStaging;
	}

	public String facilityProfileTab() {
		staging = saveStaging;

		return "facilityProfile";
	}

	public void initFacilityProfile(Integer fpId, boolean staging) {
		super.initFacilityProfile(fpId, staging);
		this.staging = staging;
		setEditStaging(staging);
		saveStaging = staging;
		if (staging) {
			task = getMyTasks().findFacilityTask(fpId);
			if (task == null) {
				logger.error("Facility Change Task not found for fpId = "
						+ fpId);
			}
		}
	}

	public Integer getCorePlaceId() {
		return corePlaceId;
	}

	public void setCorePlaceId(Integer corePlaceId) {
		this.corePlaceId = corePlaceId;
	}
/* Not used in IMPACT
	public String getAllEuPermitDialog() {
		try {
			allEuPermits = getPermitService().searchAllEuPermits(
					emissionUnit.getCorrEpaEmuId());
			for (Permit permit : allEuPermits) {
				permit.setPermitEU(emissionUnit.getCorrEpaEmuId());
			}
			PermitSearch permitSearch = (PermitSearch) FacesUtil
					.getManagedBean("permitSearch");
			permitSearch.setFromEUPermitHistory(true);
			permitSearch.setPermits(allEuPermits);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing EU Permits failed");
			return null;
		}
		return "dialog:allEuPermits";
	}
*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.webcommon.ValidationBase#validationDlgAction()
	 */
	public String validationDlgAction() {
		String ret = super.validationDlgAction();
		
		if (ret != null) {
			if (ret.equals(CONTACTS_OUTCOME)) {
				getFacilityContacts(true);
			}
			if (ret.equals(FAC_OUTCOME)) {
				if (isPortalApp() && isEditStaging() && getMyTasks() != null) {
					Task tmpTask = getMyTasks().findFacilityTask(this.getFpId());
					if (tmpTask != null) {
						getMyTasks().setTaskId(tmpTask.getTaskId());
						getMyTasks().submitTask(true);
					}
				}
			}
		}
		
		return ret;
	}

	public Task getContactTask() {
		return contactTask;
	}

	public void setContactTask(Task contactTask) {
		this.contactTask = contactTask;
	}

	public void setMyTasks(MyTasks myTasks) {
		this.myTasks = myTasks;
	}

	public boolean getEUShutdown() {
		boolean euShutdown = false;
		try {
			Facility internalFac = getFacilityService().retrieveFacility(
					facility.getFacilityId(), false);
			// RO submission is required if an EU has been shut down
			// compare EU status between internal and external to identify any
			// changes
			for (EmissionUnit eu : facility.getEmissionUnits()) {
				EmissionUnit internalEU = internalFac
						.getMatchingEmissionUnit(eu.getCorrEpaEmuId());
				if (internalEU != null) {
					if ("sd".equals(eu.getOperatingStatusCd())
							&& !"sd".equals(internalEU.getOperatingStatusCd())) {
						logger.debug("EU " + eu.getEpaEmuId() + " for facility "
								+ facility.getFacilityId()
								+ " is shutdown in Portal");
						euShutdown = true;
						break;
					}
				}
			}
			if (getOwnerChanged()) {
				euShutdown = true;
			}
		} catch (RemoteException e) {
			handleException(e);
		}
		return euShutdown;
	}

	private boolean getOwnerChanged() {
		Contact portalOwner = null;
		ContactType portalContactType = null;
		ContactType internalContactType = null;
		Contact internalOwner = null;
		boolean ownerChanged = false;

		try {
			String facId = facility.getFacilityId();
			if (facId == null)
				logger.error("facilityId is null");
			List<Contact> lc = getFacilityService().retrieveFacilityContacts(facId,
					true);
			if (lc == null)
				logger.error("contact list is null for facility " + facId);
			for (Contact contact : lc) {
				for (ContactType contactType : contact.getContactTypes()) {
					if (contactType.getEndDate() == null
							) {
						if (portalOwner == null) {
							portalOwner = contact;
							portalContactType = contactType;
						} else {
							portalOwner = portalContactType.getStartDate()
									.after(contactType.getStartDate()) ? portalOwner
									: contact;
						}
					}
				}
			}
			for (Contact contact : getFacilityService().retrieveFacilityContacts(
					facility.getFacilityId(), false)) {
				for (ContactType contactType : contact.getContactTypes()) {
					if (contactType.getEndDate() == null
							) {
						if (internalOwner == null) {
							internalOwner = contact;
							internalContactType = contactType;
						} else {
							internalOwner = internalContactType.getStartDate()
									.after(contactType.getStartDate()) ? internalOwner
									: contact;
						}
					}
				}
			}

			if (internalOwner == null && portalOwner != null) {
				ownerChanged = true;
			} else if (portalOwner != null) {
				if (internalOwner.getLastNm() == null
						&& portalOwner.getLastNm() != null) {
					ownerChanged = true;
				} else if (portalOwner.getLastNm() != null
						&& !portalOwner
								.getLastNm()
								.toUpperCase()
								.equals(internalOwner.getLastNm().toUpperCase())) {
					ownerChanged = true;
				}
				if (internalOwner.getFirstNm() == null
						&& portalOwner.getFirstNm() != null) {
					ownerChanged = true;
				} else if (portalOwner.getFirstNm() != null
						&& !portalOwner
								.getFirstNm()
								.toUpperCase()
								.equals(internalOwner.getFirstNm()
										.toUpperCase())) {
					ownerChanged = true;
				}
				if (internalOwner.getMiddleNm() == null
						&& portalOwner.getMiddleNm() != null) {
					ownerChanged = true;
				} else if (portalOwner.getMiddleNm() != null
						&& !portalOwner
								.getMiddleNm()
								.toUpperCase()
								.equals(internalOwner.getMiddleNm()
										.toUpperCase())) {
					ownerChanged = true;
				}
				if (internalOwner.getCompanyId() == null
						&& portalOwner.getCompanyId() != null) {
					ownerChanged = true;
				} else if (portalOwner.getCompanyId() != null
						&& !portalOwner
								.getCompanyId()
								.equals(internalOwner.getCompanyId())) {
					ownerChanged = true;
				}
			}
		} catch (RemoteException e) {
			handleException(e);
		}
		if (ownerChanged) {
			logger.debug("Owner has been changed for facility "
					+ facility.getFacilityId());
		}

		return ownerChanged;
	}

	public final String getAttestationDocURL() {
		String url = null;
		if (facility != null) {
			us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment doc;
			try {
				doc = getFacilityService().createFacilityAttestationDocument(facility);
				if (doc != null) {
					url = doc.getDocURL();
				} else {
					// this should never happen
					logger.error("Error creating attestation document for facility: "
							+ facility.getFacilityId());
				}
			} catch (RemoteException e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.error("getAttestationDocURL called with null facility");
		}
		return url; // stay on same page
	}

	public final boolean isAttestationRequiredForProfile() {
		return (isPortalApp() && !getMyTasks().isHasSubmit() && getEUShutdown());
	}

	public final boolean isAttestationRequiredForContactChange()
			throws Exception {
		boolean rtn = false;
		try {
			rtn = (isPortalApp() && !getMyTasks().isHasSubmit() && getOwnerChanged());
		} catch (Exception e) {
			logger.error("isAttestationRequiredForContactChange() failed()", e);
			throw e;
		}
		return rtn;
	}

	public String submitHistoryProfile() {
		excuteSubmitHistoryProfile();

		return "homeFacilityProfile";
	}

	protected final void setFacOwnerContacts() {
		
		List<Contact> facOwnerContacts = new ArrayList<Contact>();
		List<Contact> stagingContacts = new ArrayList<Contact>();
		List<Contact> companyContacts = new ArrayList<Contact>();
		List<Contact> tempFacOwnerContacts = new ArrayList<Contact>();

		try {
			if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
				if (getMyTasks().isFromHomeContact()) {
					setGlobalContacts(getFacilityService().retrieveAllContacts(false));
				} else {
					setGlobalContacts(getFacilityService().retrieveAllContacts(false));
					stagingContacts = getFacilityService()
							.retrieveStagedContactsByFacilityId(facilityId);
				}
			} else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
				setGlobalContacts(new ArrayList<Contact>());
			} else {
				setGlobalContacts(getFacilityService().retrieveAllContacts(false));
			}
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed.");
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed.");
		}

		if (facility == null || facility.getOwner() == null) {
			DisplayUtil.displayError("Cannot not access facility owner.");
			return;
		}
		
		if (globalContacts != null) {
			for (Contact tempContact : globalContacts) {
				if (tempContact.getCompanyId() == null) {
					continue;
				}

				if (tempContact.getCompanyId().equals(facility.getOwner().getCompany().getCompanyId())) {
					companyContacts.add(tempContact);
				}

			}
		}
		
		// replace staged contacts with that of internal contacts
		for (Contact companyContact : companyContacts) {
			companyContact.setSelected(false);
			facOwnerContacts.add(companyContact);
		}

		for (Contact companyContact : facOwnerContacts) {
			boolean isStagedContact = false;
			Contact contactInStaging = null;
			for (Contact stagedContact : stagingContacts) {
				if (companyContact.getContactId().equals(
						stagedContact.getContactId())) {
					contactInStaging = stagedContact;
					isStagedContact = true;
					break;
				}
			}
			if (isStagedContact) {
				// must set read only company attributes
				contactInStaging.setCompanyId(companyContact.getCompanyId());
				contactInStaging.setCmpId(companyContact.getCmpId());
				contactInStaging
						.setCompanyName(companyContact.getCompanyName());

				// pending change
				contactInStaging.setSelected(true);

				tempFacOwnerContacts.add(contactInStaging);
			} else {
				tempFacOwnerContacts.add(companyContact);
			}
		}

		facOwnerContacts = tempFacOwnerContacts;

		for (Contact newStagedContact : stagingContacts) {
			boolean isNewContact = true;
			for (Contact existingContact : tempFacOwnerContacts) {
				if (newStagedContact.getContactId().equals(
						existingContact.getContactId())) {
					isNewContact = false;
					break;
				}
			}

			if (isNewContact) {
				if (newStagedContact.getCompanyId() != null) {
					try {
						Company contactCompany = getCompanyService().retrieveCompany(
								newStagedContact.getCompanyId());
						if (contactCompany != null) {
							newStagedContact.setCompanyName(contactCompany
									.getName());
							newStagedContact
									.setCmpId(contactCompany.getCmpId());
						}
					} catch (RemoteException e) {
						logger.error(e.getMessage());
					}

				}

				newStagedContact.setSelected(true);
				facOwnerContacts.add(newStagedContact);
			}
		}

		facOwnerContacts = tempFacOwnerContacts;

		facOwnerContactsWrapper.setWrappedData(facOwnerContacts);

		// sort by name
		SortCriterion sc = new SortCriterion("lastName", true);
		List<SortCriterion> criteria = new ArrayList<SortCriterion>();
		criteria.add(sc);
		facOwnerContactsWrapper.setSortCriteria(criteria);

	}

	public final void saveContactDetailStaging() {
		ContactDetail cp = (ContactDetail) FacesUtil
				.getManagedBean("contactDetail");

		Contact potentialApplicationContact = null;
		if (cp.getContact() != null) {
			potentialApplicationContact = new Contact(cp.getContact());
		}
		
		if (cp.saveContactDetail().equals(SUCCESS)) {
			getFacilityContacts(true);

			Application app=null;
			ApplicationDetail ad = (ApplicationDetail) FacesUtil
					.getManagedBean("applicationDetail");
			if (!(ad.isApplicationDeleted())) {			
				if (ad != null && ad.getApplication() != null) {
					// save permit contact info
					Contact applicationContact = null;
					if (ad.getApplicationContact() != null
							&& ad.getApplicationContact().getContactId() != null) {
						if (potentialApplicationContact != null
								&& potentialApplicationContact.getContactId().equals(
										ad.getApplicationContact().getContactId())) {
							applicationContact = potentialApplicationContact;
						}
					}
					
					ad.reload();
					
					// reload permit contact info
					if (ad.getApplicationContact() == null
							|| (ad.getApplicationContact() != null && ad
									.getApplicationContact().getContactId() == null)) {
						
						ad.setApplicationContact(applicationContact);
					}
				}
			}
			
			

			FacesUtil.returnFromDialogAndRefresh();
		}

		return;

	}
	
	public final void cancelEditContactDetailStaging() {
		ContactDetail cp = (ContactDetail) FacesUtil
				.getManagedBean("contactDetail");
		
		if (cp.cancelEdit().equals(CANCELLED)) {
			FacesUtil.returnFromDialogAndRefresh();
		}

		return;

	}

	public String startAddTypeToContact() {
		// get global contacts
		try {
			setGlobalContacts(getFacilityService().retrieveAllContacts(false));
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed.");
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed.");
		}
		facContact = new ContactUtil();
		contactModify = false;
		if (globalContacts.isEmpty()) {
			facContact
					.setMessage("There are no contacts defined; please cancel this operation and create a global contact.");
		}
		return "dialog:addTypeToContact";
	}

	public void setFacContactTypeCd(String contactTypeCd) {
		String name;
		SelectItem cont;
		List<Contact> stagingContacts = new ArrayList<Contact>();
		List<Contact> companyContacts = new ArrayList<Contact>();
		facContact = new ContactUtil();
		facContact.getContactType().setContactTypeCd(contactTypeCd);
		contactsList = new ArrayList<SelectItem>();

		if (globalContacts.isEmpty()) {
			facContact.getContactType().setContactId(-1);
			cont = new SelectItem(0, "N/A - NO CONTACTS");

			contactsList.add(cont);
			facContact
					.setMessage("This facility has no contact person(s) defined; please cancel this request and add one or more contact person(s) first");
			return;
		}
		try {
			stagingContacts = getFacilityService().retrieveStagedContactsByFacilityId(
					facilityId);
			for (Contact tempContact : globalContacts) {
				for (Contact facilityContact : stagingContacts) {
					if (tempContact.getContactId().equals(
							facilityContact.getContactId())) {
						tempContact = facilityContact;
						break;
					}
				}

				name = tempContact.getName();
				if (tempContact.isCurrentContactType(facContact
						.getContactType().getContactTypeCd(), facilityId)) {
					continue;
				} else {
					if (tempContact.getCompanyId().intValue() == facility
							.getOwner().getCompany().getCompanyId().intValue()) {
						cont = new SelectItem(tempContact.getContactId(), name);
						companyContacts.add(tempContact);
					} else {
						for (Contact facContact : allContacts) {
							if (tempContact.getContactId().intValue() == facContact
									.getContactId().intValue()) {
								companyContacts.add(tempContact);
							}
						}
					}
				}
			}

			for (Contact stagedContact : stagingContacts) {
				boolean isStagedContact = false;
				if (stagedContact.isCurrentContactType(facContact
						.getContactType().getContactTypeCd(), facilityId)) {
					continue;
				} else {
					for (Contact companyContact : companyContacts) {

						if (companyContact.getContactId().equals(
								stagedContact.getContactId())) {
							isStagedContact = true;
							break;
						}

					}
				}
				if (!isStagedContact) {
					companyContacts.add(stagedContact);
				}
			}

			for (Contact tempContact : companyContacts) {
				name = tempContact.getName();
				cont = new SelectItem(tempContact.getContactId(), name);
				contactsList.add(cont);
			}

		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed.");
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed.");
		}

		if (contactTypeCd.equals(ContactTypeDef.RSOF)) {
			// facContact.setMessage(RO_CAUTION_MSG);
		}
	}

	public void saveEditContactType() {
		boolean operationOK = true;
		Contact stagingContact = null;
		List<Contact> stagingContacts = new ArrayList<Contact>();
		ValidationMessage[] validationMessages = facContact.getContactType()
				.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages(CONTACT_CLIENT_ID, validationMessages)) {
				return;
			}
		}

		try {
			List<Contact> nonStagingContacts = getFacilityService()
					.retrieveAllContacts(false);
			if (contactModify == false) {
				if (stagingContact == null) {
					if (nonStagingContacts != null) {
						for (Contact tempContact : nonStagingContacts) {
							if (tempContact.getContactId().equals(
									facContact.getContactType().getContactId())) {
								stagingContact = tempContact;
								break;
							}
						}
					}
				}
			}

			// must use all facility contacts for validation purposes
			if (stagingContact == null) {
				stagingContacts = getFacilityService()
						.retrieveStagedContactsByFacilityId(facilityId);
				if (stagingContacts != null) {
					for (Contact tempContact : stagingContacts) {
						if (tempContact.getContactId().equals(
								facContact.getContactType().getContactId())) {
							stagingContact = tempContact;
							break;
						}
					}
				}
			}

			logger.debug("Retrieving global staging contacts");
			setGlobalContacts(getFacilityService().retrieveStagedContactsByFacilityId(
					facilityId));

		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed.");
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed.");
		}

		validationMessages = FacilityValidation.validateEditContactType(
				saveContactType, facContact.getContactType(), allContacts,
				globalContacts, contactModify)
				.toArray(new ValidationMessage[0]);
		if (validationMessages.length > 0) {
			if (displayValidationMessages(CONTACT_CLIENT_ID, validationMessages)) {
				return;
			}
		}

		try {
			if (contactModify == false) {
				if (stagingContact == null) {
					DisplayUtil.displayError("Error attaining contact.");
					logger.debug("Staging contact is null.");
					return;
				}
				stagingContact.setFacilityId(facilityId);
				checkAndImportContactsToStaging(stagingContact);
				getInfrastructureService().addContactType(facContact.getContactType(),
						fpId, currentUserId, facilityId);
			} else if (saveContactType != null) {
				// edit
				getInfrastructureService().modifyContactType(saveContactType,
						facContact.getContactType(), fpId, currentUserId);
			} else {
				// delete
				getInfrastructureService().deleteContactType(
						facContact.getContactType(), fpId, currentUserId);
			}

			if (isActiveContacts()) {
				allContacts = getFacilityService().retrieveActiveFacilityContacts(
						facilityId, staging);
			} else {
				allContacts = getFacilityService().retrieveFacilityContacts(facilityId,
						staging);
			}

			// refresh tables
			setFacilityContacts();
			setFacOwnerContacts();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil
					.displayInfo("Facility contact type data updated successfully.");
		} else {
			cancelEditContact();
			DisplayUtil
					.displayError("Updating facility contact type data failed.");
		}

		contactModify = false;
		setEditable1(false);

		FacesUtil.returnFromDialogAndRefresh();
	}

	public List<SelectItem> getContactsList() {
		return contactsList;
	}

	public void setContactsList(List<SelectItem> contactsList) {
		this.contactsList = contactsList;
	}

	public final String submitCreateContactForStaging() {
		CreateContact cc = (CreateContact) FacesUtil
				.getManagedBean("createContact");

		String ret = cc.submitCreateContactForStaging();
		
		if (ret.equals(SUCCESS)) {
			getFacilityContacts(true);

			FacesUtil.returnFromDialogAndRefresh();
		}

		return ret;

	}
	
	public final void createStagingContactFromDialog() {
		String ret = null;
		CreateContact cc = (CreateContact) FacesUtil
				.getManagedBean("createContact");
		ret= cc.createStagingContactFromDialog();
		
		if (ret.equals(SUCCESS)) {
			getFacilityContacts(true);

			AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
		}
		
	}

	public void returnSubmitCreateContactForStaging(ReturnEvent actionEvent) {
		DisplayUtil.displayInfo("Contact created successfully.");
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public void createFacilityNaics() {
		String currentNaics = (String) this.modifyNaics.getValue();

		if (Utility.isNullOrEmpty(currentNaics)) {
			DisplayUtil.displayError("ERROR: Attribute NAICS is not set.");
			return;
		}

		for (Stars2Object temp : super.naicsCds) {
			if (temp.getValue().toString().compareTo(currentNaics) == 0) {
				DisplayUtil
						.displayError("Invalid NAICS Code. NAICS Code must be unique.");
				return;
			}
		}

		List<String> naicses = Stars2Object.fromStar2Object(this.naicsCds);

		try {
			
			// The call to modify facility existed since the beginning of the time.
			// It is called probably to prevent concurrent updates but
			// if the facility detail page is in the edit mode, calling modify facility 
			// without first validating the facility is not a good idea.
			// However if validation is done prior to calling modify facility, it will
			// create a catch 22 situation in some cases like when the facility does not
			// have both API and NAICS code but both are required fields. 
			// In such cases, due to validation errors, it won't be possible to add either 
			// API or NAICS code.
			// In order to provide a decent protection against concurrent modification, and
			// still do validation upon changing the facility detail, perform the modification
			// only when API or NAICS code or address is added/modified while not in the edit mode.
			if(!getEditable()) {
				if (!getFacilityService().modifyFacility(facility, currentUserId)) {
					DisplayUtil
							.displayInfo("The facility submission to FR as result of your update failed.");
				}
			}

			// Modify NAICS codes
			naicses.add(currentNaics);

			getNaicsService().addFacilityNAICSs(fpId, naicses);
			DisplayUtil.displayInfo("Create Facility NAICS Success.");

		} catch (DAOException e) {
			logger.error("Create Facility NAICS Failed", e);
			DisplayUtil.displayError("Create Facility NAICS Failed.");

		} catch (RemoteException ex) {
			logger.error("Create Facility NAICS Failed", ex);
			DisplayUtil.displayError("Create Facility NAICS Failed.");

		} finally {
			refreshFacility();
			super.refreshFacilityNaics();
			closeDialog();
		}
	}

	public void updateFacilityNaics() {
		String currentNaics = (String) this.modifyNaics.getValue();

		if (Utility.isNullOrEmpty(currentNaics)) {
			DisplayUtil.displayError("ERROR: Attribute NAICS is not set.");
			return;
		}

		int count = 0;

		for (Stars2Object temp : this.naicsCds) {
			if (temp.getValue().toString().compareTo(currentNaics) == 0) {
				count++;
			}
		}

		if (count > 1) {
			DisplayUtil
					.displayError("Invalid NAICS Code. NAICS Code must be unique.");
			return;
		}

		List<String> naicses = Stars2Object.fromStar2Object(this.naicsCds);

		try {
			// The call to modify facility existed since the beginning of the time.
			// It is called probably to prevent concurrent updates but
			// if the facility detail page is in the edit mode, calling modify facility 
			// without first validating the facility is not a good idea.
			// However if validation is done prior to calling modify facility, it will
			// create a catch 22 situation in some cases like when the facility does not
			// have both API and NAICS code but both are required fields. 
			// In such cases, due to validation errors, it won't be possible to add either 
			// API or NAICS code.
			// In order to provide a decent protection against concurrent modification, and
			// still do validation upon changing the facility detail, perform the modification
			// only when API or NAICS code or address is added/modified while not in the edit mode.
			if(!getEditable()) {
				if (!getFacilityService().modifyFacility(facility, currentUserId)) {
					DisplayUtil
							.displayInfo("The facility submission to FR as result of your update failed.");
				}
			}
			getNaicsService().addFacilityNAICSs(fpId, naicses);
			DisplayUtil.displayInfo("Create Facility NAICS Success.");

		} catch (DAOException e) {
			logger.error("Update Facility NAICS Failed", e);
			DisplayUtil.displayError("Update Facility NAICS Failed.");

		} catch (RemoteException ex) {
			logger.error("Update Facility NAICS Failed", ex);
			DisplayUtil.displayError("Update Facility NAICS Failed.");

		} finally {
			refreshFacility();
			this.refreshFacilityNaics();
			closeDialog();
		}
	}

	public void deleteFacilityNaics() {
		DisplayUtil.clearQueuedMessages();

		try {
			// The call to modify facility existed since the beginning of the time.
			// It is called probably to prevent concurrent updates but
			// if the facility detail page is in the edit mode, calling modify facility 
			// without first validating the facility is not a good idea.
			// However if validation is done prior to calling modify facility, it will
			// create a catch 22 situation in some cases like when the facility does not
			// have both API and NAICS code but both are required fields. 
			// In such cases, due to validation errors, it won't be possible to add either 
			// API or NAICS code.
			// In order to provide a decent protection against concurrent modification, and
			// still do validation upon changing the facility detail, perform the modification
			// only when API or NAICS code or address is added/modified while not in the edit mode.
			if(!getEditable()) {
				if (!getFacilityService().modifyFacility(facility, currentUserId)) {
					DisplayUtil
							.displayInfo("The facility submission to FR as result of your update failed.");
				}
			}
			getNaicsService().deleteFacilityNaics(this.fpId,
					(String) this.modifyNaics.getValue());
			DisplayUtil.displayInfo("Delete Facility NAICS Code Success.");

		} catch (DAOException e) {
			logger.error("Delete Facility NAICS Code failed", e);
			DisplayUtil.displayError("Delete Facility NAICS Code failed.");

		} catch (RemoteException ex) {
			logger.error("Delete Facility NAICS Code failed", ex);
			DisplayUtil.displayError("Delete Facility NAICS Code failed.");

		} finally {
			refreshFacility();
			refreshFacilityNaics();
			closeDialog();
		}
	}

	public final String saveAddresses() {
		boolean operationOK = true;

//		address.validateAddress();
//		address.validateLocationInfo();
//
//		address.getValidationMessages().remove("zipCode");
//		address.getValidationMessages().remove("cityName");
//		address.getValidationMessages().remove("addressLine1");

		address.validateFacilityAddress();
		
		ValidationMessage[] errs = address.validate();

		for (ValidationMessage error : errs) {
			DisplayUtil.displayError(error.getMessage());
			operationOK = false;
		}
		if (!operationOK) {
			return null;
		}

		try {

			// The call to modify facility existed since the beginning of the time.
			// It is called probably to prevent concurrent updates but
			// if the facility detail page is in the edit mode, calling modify facility 
			// without first validating the facility is not a good idea.
			// However if validation is done prior to calling modify facility, it will
			// create a catch 22 situation in some cases like when the facility does not
			// have both API and NAICS code but both are required fields. 
			// In such cases, due to validation errors, it won't be possible to add either 
			// API or NAICS code.
			// In order to provide a decent protection against concurrent modification, and
			// still do validation upon changing the facility detail, perform the modification
			// only when API or NAICS code or address is added/modified while not in the edit mode.
			if(!getEditable()) {
				if (!getFacilityService().modifyFacility(facility, currentUserId)) {
					DisplayUtil
							.displayInfo("The facility submission to FR as result of your update failed.");
				}
			}

			getFacilityService().updateFacilityAddresses(fpId, address);
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		refreshFacility();

		if (operationOK) {
			DisplayUtil
					.displayInfo("Facility location data successfully updated.");
			closeDialog();
			setEditable1(false);
		} else {
			DisplayUtil.displayError("Updating facility location data failed.");
		}

		return "facilityAddresses";
	}

	public final void createFacilityApi() {
		ValidationMessage[] errs = this.modifyApiGroup.validate();
		if (errs.length > 0) {
			for (ValidationMessage error : errs) {
				DisplayUtil.displayError(error.getMessage());
			}

			return;
		}

		String cuurentApiNo = this.modifyApiGroup.getApiNo();

		for (ApiGroup temp : this.facility.getApis()) {
			String tempApiNo = temp.getApiNo();
			if (temp.getApiCd() != this.modifyApiGroup.getApiCd()
					&& cuurentApiNo.equals(tempApiNo)) {
				DisplayUtil
						.displayError("Invalid API Number. Api Number must be unique.");
				return;
			}
		}
		
		if(isDuplicateApiNo(cuurentApiNo)) {
			return;
		}

		this.modifyApiGroup.setFpId(this.fpId);

		try {
			
			// The call to modify facility existed since the beginning of the time.
			// It is called probably to prevent concurrent updates but
			// if the facility detail page is in the edit mode, calling modify facility 
			// without first validating the facility is not a good idea.
			// However if validation is done prior to calling modify facility, it will
			// create a catch 22 situation in some cases like when the facility does not
			// have both API and NAICS code but both are required fields. 
			// In such cases, due to validation errors, it won't be possible to add either 
			// API or NAICS code.
			// In order to provide a decent protection against concurrent modification, and
			// still do validation upon changing the facility detail, perform the modification
			// only when API or NAICS code or address is added/modified while not in the edit mode.
			if(!getEditable()) {
				if (!getFacilityService().modifyFacility(facility, currentUserId)) {
					DisplayUtil
							.displayInfo("The facility submission to FR as result of your update failed.");
				}
			}
			getFacilityService().createFacilityApi(this.modifyApiGroup);
			DisplayUtil.displayInfo("Create Facility API Success.");

		} catch (DAOException e) {
			logger.error("Create Facility API Failed", e);
			DisplayUtil.displayError("Create Facility API Failed.");

		} catch (RemoteException ex) {
			logger.error("Create Facility API Failed", ex);
			DisplayUtil.displayError("Create Facility API Failed.");

		} finally {
			refreshFacility();
			this.refreshFacilityApis();

			// reset API detail window
			setEditable1(true);

			this.modifyApiGroup = new ApiGroup();
			this.modifyApiGroup.setFpId(this.fpId);
			this.modifyApiGroup.setNewObject(true);
			
			FacesUtil.refreshMainWindow();
		}
	}

	public final void updateFacilityApi() {
		ValidationMessage[] errs = this.modifyApiGroup.validate();
		if (errs.length > 0) {
			for (ValidationMessage error : errs) {
				DisplayUtil.displayError(error.getMessage());
			}

			return;
		}

		String cuurentApiNo = this.modifyApiGroup.getApiNo();

		for (ApiGroup temp : this.facility.getApis()) {
			String tempApiNo = temp.getApiNo();
			if (temp.getApiCd() != this.modifyApiGroup.getApiCd()
					&& cuurentApiNo.equals(tempApiNo)) {
				DisplayUtil
						.displayError("Invalid API Number. Api Number must be unique.");
				return;
			}
		}
		
		if(isDuplicateApiNo(cuurentApiNo)) {
			return;
		}

		try {
			
			// The call to modify facility existed since the beginning of the time.
			// It is called probably to prevent concurrent updates but
			// if the facility detail page is in the edit mode, calling modify facility 
			// without first validating the facility is not a good idea.
			// However if validation is done prior to calling modify facility, it will
			// create a catch 22 situation in some cases like when the facility does not
			// have both API and NAICS code but both are required fields. 
			// In such cases, due to validation errors, it won't be possible to add either 
			// API or NAICS code.
			// In order to provide a decent protection against concurrent modification, and
			// still do validation upon changing the facility detail, perform the modification
			// only when API or NAICS code or address is added/modified while not in the edit mode.
			if(!getEditable()) {
				if (!getFacilityService().modifyFacility(facility, currentUserId)) {
					DisplayUtil
							.displayInfo("The facility submission to FR as result of your update failed.");
				}
			}
			getFacilityService().updateFacilityApi(this.modifyApiGroup);
			DisplayUtil.displayInfo("Update Facility API Success.");

		} catch (DAOException e) {
			logger.error("Update Facility API failed", e);
			DisplayUtil.displayError("Update Facility API Failed.");

		} catch (RemoteException ex) {
			logger.error("Update Facility API failed", ex);
			DisplayUtil.displayError("Update Facility API Failed.");

		} finally {
			refreshFacility();
			this.refreshFacilityApis();
			closeDialog();
		}
	}

	public final void deleteFacilityApi() {
		try {
			
			// The call to modify facility existed since the beginning of the time.
			// It is called probably to prevent concurrent updates but
			// if the facility detail page is in the edit mode, calling modify facility 
			// without first validating the facility is not a good idea.
			// However if validation is done prior to calling modify facility, it will
			// create a catch 22 situation in some cases like when the facility does not
			// have both API and NAICS code but both are required fields. 
			// In such cases, due to validation errors, it won't be possible to add either 
			// API or NAICS code.
			// In order to provide a decent protection against concurrent modification, and
			// still do validation upon changing the facility detail, perform the modification
			// only when API or NAICS code or address is added/modified while not in the edit mode.
			if(!getEditable()) {
				if (!getFacilityService().modifyFacility(facility, currentUserId)) {
					DisplayUtil
							.displayInfo("The facility submission to FR as result of your update failed.");
				}
			}
			getFacilityService().deleteFacilityApi(this.modifyApiGroup.getApiCd());
			DisplayUtil.displayInfo("Delete Facility API Success.");

		} catch (DAOException e) {
			logger.error("Delete Facility API failed", e);
			DisplayUtil.displayError("Delete Facility API failed.");

		} catch (RemoteException ex) {
			logger.error("Delete Facility API failed", ex);
			DisplayUtil.displayError("Delete Facility API failed.");

		} finally {
			refreshFacility();
			refreshFacilityApis();
			closeDialog();
		}
	}

	public final void closeFacilityApiDialog() {
		this.setEditable1(false);
		closeDialog();
	}

	public final void closeFacilityNaicsDialog() {
		this.setEditable1(false);
		closeDialog();
	}

	public final boolean isMigrationHappened() {
		if (UserAttributes.bolMigrated) {
			return true;
		}

		return false;
	}
	
// TFS Task 5312 - moved this method to the base class
//	public boolean isEUExistsInternally() {
//		boolean euExistsInternally = false;
//		try {
//			Facility internalFac = getFacilityService().retrieveFacility(
//					facility.getFacilityId(), false);
//
//			// If EU exists in internal IMPACT, return true
//			EmissionUnit internalEU = internalFac
//					.getMatchingEmissionUnit(emissionUnit.getCorrEpaEmuId());
//			if (internalEU != null) {
//				euExistsInternally = true;
//			}
//		} catch (RemoteException e) {
//			handleException(e);
//		}
//		return euExistsInternally;
//	}

	// Get all the limits for the Facility
	public final String getFacilityCemComLimits() {
		initializeFacilityCemComLimitList();
		return "facilities.profile.facilityCemComLimits";
	}

	// Get all the limits for the Facility
	public final String getFacilityCemComLimitsReadOnly() {
		initializeFacilityCemComLimitList();
		return "facilities.profile.homeFacilityCemComLimits";
	}
	
	public String refreshSearchFacilities() {
		FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
		fp.setFacility(null);
		fp.setTreeData(null);

		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_facProfile")).setDisabled(null == getFacilityId());

		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_homeFacilities")).setDisabled(false);

		return "facilitySearch";
	}
    
}
