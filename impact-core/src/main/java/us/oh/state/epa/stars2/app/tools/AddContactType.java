package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

@SuppressWarnings("serial")
public class AddContactType extends BulkOperation {
	private FacilityList[] facilities;
	private boolean addContactTypeEditable;
	private boolean finalStep;
	public static String CLIENT_ID = "contact";
	private List<Contact> contacts;
	private List<SelectItem> contactsList;
	private transient Timestamp startDate;
	private transient Timestamp endDate;
	private Integer contactId;
	private ContactType contactType;
	private String message;
	
//	private FacilityService facilityService;
//
//	public FacilityService getFacilityService() {
//		return facilityService;
//	}
//
//	public void setFacilityService(FacilityService facilityService) {
//		this.facilityService = facilityService;
//	}

	public FacilityList[] getFacilities() {
		return facilities;
	}

	public void setFacilities(FacilityList[] facilities) {
		this.facilities = facilities;
	}

	public boolean isAddContactTypeEditable() {
		return addContactTypeEditable;
	}

	public void setAddContactTypeEditable(boolean addContactTypeEditable) {
		this.addContactTypeEditable = addContactTypeEditable;
	}

	public boolean isFinalStep() {
		return finalStep;
	}

	public void setFinalStep(boolean finalStep) {
		this.finalStep = finalStep;
	}

	public List<SelectItem> getContactsList() {
		return contactsList;
	}

	public void setContactsList(List<SelectItem> contactsList) {
		this.contactsList = contactsList;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public ContactType getContactType() {
		return contactType;
	}

	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}

	public AddContactType() {
		super();
		contactType = new ContactType();
		setButtonName("Add Contact Type");
		setNavigation("dialog:addContactType");
	}

	public String getContactTypeCd() {
		return contactType.getContactTypeCd();
	}

	public final void setContactTypeCd(String contactTypeCd) {
		contactType = new ContactType();
		contactType.setContactTypeCd(contactTypeCd);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

		facilities = getFacilityService().searchFacilities(catalog.getFacilityNm(),
				catalog.getFacilityId(), catalog.getCompanyName(), null,
				catalog.getCounty(), catalog.getOperatingStatusCd(),
				catalog.getDoLaa(), catalog.getNaicsCd(),
				catalog.getPermitClassCd(), catalog.getTvPermitStatus(),
				catalog.getAddress1(), null, null, null, null, true,
				catalog.getFacilityTypeCd());

		if (facilities.length == 0) {
			DisplayUtil
					.displayInfo("There are no facilities that match the search criteria");
		} else {
			setHasSearchResults(true);
			catalog.setFacilities(facilities);
			addContactTypeEditable = true;
		}

		setMaximum(facilities.length);
		setValue(facilities.length);

	}

	public final void apply() {
		List<ValidationMessage> validiationMessages = new ArrayList<ValidationMessage>();
		java.util.Date date = new java.util.Date();
		Timestamp currentTime = new Timestamp(date.getTime());
		
		if (contactType.getContactTypeCd() == null) {
			validiationMessages.add(new ValidationMessage("contactType",
					"Contact type is missing.",
					ValidationMessage.Severity.ERROR));
		}

		if (contactId == null) {
			validiationMessages.add(new ValidationMessage("contactId",
					"Contact is missing.", ValidationMessage.Severity.ERROR));
		}

		if (startDate == null) {
			validiationMessages.add(new ValidationMessage("startDate",
					"Contact type start date is missing.",
					ValidationMessage.Severity.ERROR));
		}

		if (endDate != null) {
			if (endDate.before(startDate)) {
				validiationMessages
						.add(new ValidationMessage(
								"endDate",
								"Invalid End Date. End date cannot be before start date",
								ValidationMessage.Severity.ERROR));
			}
			
			if (endDate.after(currentTime)){
				validiationMessages
				.add(new ValidationMessage(
						"endDate",
						"Invalid End Date. End date cannot be placed in the future",
						ValidationMessage.Severity.ERROR));
			}
		}
		
		if (startDate != null) {
			if (startDate.after(currentTime)){
				validiationMessages
				.add(new ValidationMessage(
						"startDate",
						"Invalid Start Date. Start date cannot be placed in the future",
						ValidationMessage.Severity.ERROR));
			}
		}

		if (validiationMessages.size() > 0) {
			if (displayValidationMessages(CLIENT_ID,
					validiationMessages.toArray(new ValidationMessage[0]))) {
				return;
			}
		}
		this.addContactTypeEditable = false;
		this.setFinalStep(true);
		String numberOfFacilities = NumberFormat.getNumberInstance(
				java.util.Locale.US).format(facilities.length);
		setMessage("Caution: the following operation will result in a contact type being added to a total of "
				+ numberOfFacilities + " facilities.");
	}

	public final void change() {
		this.addContactTypeEditable = true;
		this.setFinalStep(false);

	}

	public final void applyFinalAction() {
		try {
			String outcome = performPostWork();

			if (outcome.equals(FAIL)) {
				this.addContactTypeEditable = true;
				this.setFinalStep(false);
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

		DisplayUtil
				.displayInfo("The new contact type has been added to the selected facilities.");
		reset();
		this.catalog.reset();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String performPostWork() {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		// create new contact type
		contactType.setContactId(contactId);
		contactType.setStartDate(startDate);
		contactType.setEndDate(endDate);

		try {
			validationMessages.addAll(getFacilityService().addNewContactType(
					facilities, contactType));
		} catch (DAOException e) {
			DisplayUtil
					.displayError("New contact type update failed for facilities.");
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
		logger.debug("Performing Add Contact Type Preliminary Work");

		// Set selected facilities
		setFacilities(catalog.getSelectedFacilities());
		if (facilities.length == 0) {
			addContactTypeEditable = false;
			return;
		}

		// Get contacts list
		retrieveContactsList();

		return;
	}

	public final void reset() {
		setContactsList(null);
		setFacilities(null);
		setAddContactTypeEditable(true);
		setFinalStep(false);
	}

	/**
	 * Sets up and retrieves global contacts. Places global contacts in
	 * contactsList. If no global contacts exist, an error is given and the
	 * operation cannot proceed.
	 */
	private void retrieveContactsList() {
		contacts = new ArrayList<Contact>();
		contactsList = new ArrayList<SelectItem>();

		try {
			contacts = getFacilityService().retrieveAllContacts();
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed");
		}
		
		if (contacts.isEmpty()) {
			addContactTypeEditable = false;
			DisplayUtil
					.displayError("There are no contacts defined; please cancel this operation and create a global contact.");
		} else {
			SelectItem cont;
			String name;
			for (Contact tempContact : contacts) {
				name = tempContact.getName();

				cont = new SelectItem(tempContact.getContactId(), name);
				contactsList.add(cont);

			}
		}
	}

	@Override
	public String performPostWork(BulkOperationsCatalog catalog)
			throws RemoteException {
		return null;
	}

}
