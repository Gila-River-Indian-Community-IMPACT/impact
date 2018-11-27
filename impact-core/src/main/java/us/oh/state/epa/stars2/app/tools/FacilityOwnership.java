package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

@SuppressWarnings("serial")
public class FacilityOwnership extends BulkOperation {
	private FacilityList[] facilities;
	private Facility firstFacility;
	private boolean facilityOwnerEditable;
	private String facilityOwnerCmpId;
	private Integer environmentalContactId;
	private Integer responsibleOfficialContactId;
	private List<SelectItem> contactsList;
	private boolean titleVFacilityExists;
	private transient Timestamp ownershipChangeDate;
	private List<Contact> contacts;
	private String message;
	public static String CLIENT_ID = "owner";
	private boolean finalStep;
	private LinkedHashMap<String, String> companies;
	private boolean globalListUsed;

	private CompanyService companyService = App.getApplicationContext()
			.getBean(CompanyService.class);

	public FacilityList[] getFacilities() {
		return facilities;
	}

	public void setFacilities(FacilityList[] facilities) {
		this.facilities = facilities;
	}

	public Facility getFirstFacility() {
		return firstFacility;
	}

	public void setFirstFacility(Facility firstFacility) {
		this.firstFacility = firstFacility;
	}

	public boolean isFacilityOwnerEditable() {
		return facilityOwnerEditable;
	}

	public void setFacilityOwnerEditable(boolean facilityOwnerEditable) {
		this.facilityOwnerEditable = facilityOwnerEditable;
	}

	public String getFacilityOwnerCmpId() {
		return facilityOwnerCmpId;
	}

	public void setFacilityOwnerCmpId(String facilityOwnerCmpId) {
		this.facilityOwnerCmpId = facilityOwnerCmpId;

		if (!Utility.isNullOrEmpty(this.facilityOwnerCmpId)) {
			setGlobalListUsed(this.globalListUsed);
		}
	}

	public Integer getEnvironmentalContactId() {
		return environmentalContactId;
	}

	public void setEnvironmentalContactId(Integer environmentalContactId) {
		this.environmentalContactId = environmentalContactId;
	}

	public Integer getResponsibleOfficialContactId() {
		return responsibleOfficialContactId;
	}

	public void setResponsibleOfficialContactId(
			Integer responsibleOfficialContactId) {
		this.responsibleOfficialContactId = responsibleOfficialContactId;
	}

	public List<SelectItem> getContactsList() {
		return contactsList;
	}

	public LinkedHashMap<String, String> getCompanies() {
		return companies;
	}

	public void setCompanies(LinkedHashMap<String, String> companies) {
		this.companies = companies;
	}

	public void setContactsList(List<SelectItem> contactsList) {
		this.contactsList = contactsList;
	}

	public boolean isTitleVFacilityExists() {
		return titleVFacilityExists;
	}

	public void setTitleVFacilityExists(boolean titleVFacilityExists) {
		this.titleVFacilityExists = titleVFacilityExists;
	}

	public Timestamp getOwnershipChangeDate() {
		return ownershipChangeDate;
	}

	public void setOwnershipChangeDate(Timestamp ownershipChangeDate) {
		this.ownershipChangeDate = ownershipChangeDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isFinalStep() {
		return finalStep;
	}

	public void setFinalStep(boolean finalStep) {
		this.finalStep = finalStep;
	}

	public boolean isGlobalListUsed() {
		return globalListUsed;
	}

	public void setGlobalListUsed(boolean globalListUsed) {
		this.globalListUsed = globalListUsed;

		retrieveContactsList();
	}

	public FacilityOwnership() {
		super();
		setButtonName("Change Facility Ownership");
		setNavigation("dialog:changeFacilityOwnership");
	}

	/**
	 * Each bulk operation is responsible for providing a search operation based
	 * on the paramters gathered by the BulkOperationsCatalog bean. The search
	 * is run when the "Select" button on the Bulk Operations screen is clicked.
	 * The BulkOperationsCatalog will then display the results of the search.
	 * Below the results is a "Bulk Operation" button.
	 */
	public final void searchFacilities(BulkOperationsCatalog lcatalog)
			throws RemoteException {

		this.catalog = lcatalog;
		catalog.setRoleOperation(true);

		facilities = getFacilityService().searchFacilities(
				catalog.getFacilityNm(), catalog.getFacilityId(),
				catalog.getCompanyName(), null, catalog.getCounty(),
				catalog.getOperatingStatusCd(), catalog.getDoLaa(),
				catalog.getNaicsCd(), catalog.getPermitClassCd(),
				catalog.getTvPermitStatus(), catalog.getAddress1(), null, null,
				null, null, true, catalog.getFacilityTypeCd());

		if (facilities.length == 0) {
			DisplayUtil
					.displayInfo("There are no facilities that match the search criteria");
		} else {
			setHasSearchResults(true);
			catalog.setFacilities(facilities);
			facilityOwnerEditable = true;
		}

		setMaximum(facilities.length);
		setValue(facilities.length);

	}

	public final void apply() {
		List<ValidationMessage> validiationMessages = new ArrayList<ValidationMessage>();
		java.util.Date date = new java.util.Date();
		Timestamp currentTime = new Timestamp(date.getTime());

		if (facilityOwnerCmpId == null) {
			validiationMessages.add(new ValidationMessage("facilityOwnerCmpId",
					"New owner is missing.", ValidationMessage.Severity.ERROR));
		}

		if (ownershipChangeDate == null) {
			validiationMessages.add(new ValidationMessage(
					"ownershipDateChange",
					"Effective date of ownership change is missing.",
					ValidationMessage.Severity.ERROR));
		} else {
			if (ownershipChangeDate.after(currentTime)) {
				validiationMessages
						.add(new ValidationMessage(
								"ownershipDateChange",
								"Effective date of ownership change cannot be placed in the future.",
								ValidationMessage.Severity.ERROR));
			}
		}

		if (titleVFacilityExists) {
			if (responsibleOfficialContactId == null) {
				validiationMessages.add(new ValidationMessage(
						"responsibleOfficialContactId",
						"Responsible Official is missing.",
						ValidationMessage.Severity.ERROR));
			}
		}

		if (environmentalContactId == null) {
			validiationMessages.add(new ValidationMessage(
					"environmentalContactId",
					"Environmental Contact is missing.",
					ValidationMessage.Severity.ERROR));
		}

		if (validiationMessages.size() > 0) {
			if (displayValidationMessages(CLIENT_ID,
					validiationMessages.toArray(new ValidationMessage[0]))) {
				return;
			}
		}
		this.facilityOwnerEditable = false;
		this.finalStep = true;
		this.message = "Are you sure you would like to continue?";

	}

	public final void change() {
		this.facilityOwnerEditable = true;
		this.finalStep = false;

	}

	public final void applyFinalAction() {
		try {
			String outcome = performPostWork();

			if (outcome.equals(FAIL)) {
				this.facilityOwnerEditable = true;
				this.finalStep = false;
				return;
			}
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				DisplayUtil.displayError(e.getMessage());
				logger.error(e.getMessage(), e);
			} else {
				DisplayUtil
						.displayError("Preliminary work failed: System Error.");
				logger.error(e.getMessage(), e);
			}
			return;
		}

		DisplayUtil.displayInfo("The selected facilities have been updated.");
		reset();
		this.catalog.reset();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String performPostWork() {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		try {
			validationMessages.addAll(getFacilityService().changeOwnership(
					facilities, environmentalContactId,
					responsibleOfficialContactId, facilityOwnerCmpId,
					ownershipChangeDate));
		} catch (DAOException e) {
			DisplayUtil.displayError("Ownership update failed for facilities.");
			logger.error(e);
			return FAIL;
		} catch (RemoteException e) {
			DisplayUtil.displayError("Ownership update failed for facilities.");
			logger.error(e);
			return FAIL;
		}

		if (validationMessages.size() > 0) {
			if (displayValidationMessages(CLIENT_ID,
					validationMessages.toArray(new ValidationMessage[0]))) {
				return FAIL;
			}
		}

		reset();
		return SUCCESS;
	}

	@Override
	public void performPreliminaryWork(BulkOperationsCatalog catalog)
			throws RemoteException {
		logger.debug("Performing Change Facility Ownership Preliminary Work");

		// Set selected facilities
		setFacilities(catalog.getSelectedFacilities());
		if (facilities.length == 0) {
			facilityOwnerEditable = false;
			return;
		}

		// Get owners list
		retrieveCompaniesList();

		// Get contacts list
		contacts = null;
		setGlobalListUsed(false);

		// Determine if Title V facility is being changed
		determineTitleVFacilityExists();

		return;
	}

	public final void reset() {
		setContactsList(null);
		setOwnershipChangeDate(null);
		setFacilities(null);
		setFacilityOwnerEditable(true);
		setTitleVFacilityExists(false);
		setResponsibleOfficialContactId(null);
		setFacilityOwnerCmpId(null);
		setEnvironmentalContactId(null);
		setMessage(null);
		setFinalStep(false);
	}

	/**
	 * Sets up and retrieves global contacts. Places global contacts in
	 * contactsList. If no global contacts exist, an error is given and the
	 * operation cannot proceed.
	 */
	private void retrieveContactsList() {
		contactsList = new ArrayList<SelectItem>();

		try {
			if (contacts == null) {
				contacts = new ArrayList<Contact>();
				contacts = getFacilityService().retrieveAllContacts();
			}
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed");
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed");
		}

		if (contacts.isEmpty()) {
			facilityOwnerEditable = false;
			DisplayUtil
					.displayError("There are no contacts defined; please cancel this operation and create a global contact.");
		} else {
			SelectItem cont;
			String name;

			for (Contact tempContact : contacts) {
				if (this.globalListUsed) {
					name = tempContact.getName() + " ("
							+ tempContact.getCompanyName() + ")";

					cont = new SelectItem(tempContact.getContactId(), name);
					contactsList.add(cont);
				} else {
					if (Utility.isNullOrEmpty(facilityOwnerCmpId)) {
						break;
					}

					if (Utility.isNullOrEmpty(tempContact.getCmpId())) {
						continue;
					}

					if (tempContact.getCmpId().equals(facilityOwnerCmpId)) {
						name = tempContact.getName();

						cont = new SelectItem(tempContact.getContactId(), name);
						contactsList.add(cont);
					}

				}

			}
		}
	}

	private void retrieveCompaniesList() {
		try {
			Company[] tempCompanies = companyService.retrieveCompanies();

			companies = new LinkedHashMap<String, String>();
			for (Company tempCompany : tempCompanies) {
				companies.put(tempCompany.getName(), tempCompany.getCmpId());
			}
		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}

		if (companies.isEmpty()) {
			facilityOwnerEditable = false;
			DisplayUtil
					.displayError("There are no companies defined; please cancel this operation and create a company.");
		}
	}

	/**
	 * Determines if a Title V facility exists within the selected set of
	 * facilities.
	 */
	private void determineTitleVFacilityExists() {
		setTitleVFacilityExists(false);
		if (facilities != null) {
			for (FacilityList facility : facilities) {
				if (facility.getPermitClassCd().equals("tv")) {
					logger.debug("A Type V Facility is Selected");
					setTitleVFacilityExists(true);
					break;
				}
			}
		}
	}

	@Override
	public String performPostWork(BulkOperationsCatalog catalog)
			throws RemoteException {
		return null;
	}

}
