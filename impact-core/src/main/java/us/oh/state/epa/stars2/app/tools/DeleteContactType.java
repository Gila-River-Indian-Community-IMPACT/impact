package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ContactService;

@SuppressWarnings("serial")
public class DeleteContactType extends BulkOperation {
	private ContactType[] contactTypes;
	private ContactType[] activeContactTypes;
	private boolean deleteContactTypeEditable;
	private boolean finalStep;
	public static String CLIENT_ID = "contact";
	private Contact contact;
	private transient Timestamp endDate;
	private String message;
	private ContactService contactService = 
			App.getApplicationContext().getBean(ContactService.class); //TODO reconsider static call
	
	public boolean isDeleteContactTypeEditable() {
		return deleteContactTypeEditable;
	}

	public void setDeleteContactTypeEditable(boolean deleteContactTypeEditable) {
		this.deleteContactTypeEditable = deleteContactTypeEditable;
	}

	public boolean isFinalStep() {
		return finalStep;
	}

	public void setFinalStep(boolean finalStep) {
		this.finalStep = finalStep;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public ContactType[] getContactTypes() {
		return contactTypes;
	}

	public void setContactTypes(ContactType[] contactTypes) {
		this.contactTypes = contactTypes;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ContactType[] getActiveContactTypes() {
		return activeContactTypes;
	}

	public void setActiveContactTypes(ContactType[] activeContactTypes) {
		this.activeContactTypes = activeContactTypes;
	}

	public DeleteContactType() {
		super();
		setButtonName("Delete Contact Type");
		setNavigation("dialog:deleteContactType");
	}

	/**
	 * Each bulk operation is responsible for providing a search operation based
	 * on the paramters gathered by the BulkOperationsCatalog bean. The search
	 * is run when the "Select" button on the Bulk Operations screen is clicked.
	 * The BulkOperationsCatalog will then display the results of the search.
	 * Below the results is a "Bulk Operation" button.
	 */
	@Override
	public final void searchContactTypes(BulkOperationsCatalog lcatalog)
			throws RemoteException {

		this.catalog = lcatalog;

		if (this.catalog.getContactId() == null) {
			DisplayUtil.displayError("Select a contact before searching");
		} else {
			contact = contactService.retrieveContactDetail(
					this.catalog.getContactId());

			List<ContactType> cTypes = contact.getContactTypes();
			this.contactTypes = cTypes.toArray(new ContactType[0]);

			List<ContactType> activeCtypes = new ArrayList<ContactType>();
			for (ContactType cType : cTypes) {
				if (cType.getEndDate() == null) {
					activeCtypes.add(cType);
				}
			}
			this.activeContactTypes = activeCtypes.toArray(new ContactType[0]);

			if (this.activeContactTypes.length == 0) {
				DisplayUtil
						.displayInfo("There are no contact type records that match the search criteria");
			} else {
				setHasSearchResults(true);
				catalog.setContactTypes(this.activeContactTypes);
				deleteContactTypeEditable = true;
			}

			setMaximum(activeContactTypes.length);
			setValue(activeContactTypes.length);
		}

	}

	public final void apply() {
		List<ValidationMessage> validiationMessages = new ArrayList<ValidationMessage>();
		java.util.Date date = new java.util.Date();
		Timestamp currentTime = new Timestamp(date.getTime());

		if (endDate == null) {
			validiationMessages.add(new ValidationMessage("endDate",
					"End date is missing.", ValidationMessage.Severity.ERROR));
		} else {
			if (endDate.after(currentTime)) {
				validiationMessages
						.add(new ValidationMessage(
								"endDate",
								"Invalid End Date. End date cannot be placed in the future",
								ValidationMessage.Severity.ERROR));
			}
		}

		if (validiationMessages.size() > 0) {
			if (displayValidationMessages(CLIENT_ID,
					validiationMessages.toArray(new ValidationMessage[0]))) {
				return;
			}
		}
		this.deleteContactTypeEditable = false;
		this.setFinalStep(true);
		String numberOfContactTypes = NumberFormat.getNumberInstance(
				java.util.Locale.US).format(activeContactTypes.length);
		setMessage("Caution: the following operation will result in a total of "
				+ numberOfContactTypes
				+ " contact type records being timed out.");
	}

	public final void change() {
		this.deleteContactTypeEditable = true;
		this.setFinalStep(false);

	}

	public final void applyFinalAction() {
		try {
			String outcome = performPostWork();

			if (outcome.equals(FAIL)) {
				this.deleteContactTypeEditable = true;
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
				.displayInfo("The selected contact type records have been timed out.");
		reset();
		this.catalog.reset();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String performPostWork() {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		validationMessages.addAll(validateTimeOutOperation(contactTypes,
				activeContactTypes, this.endDate));

		if (validationMessages.size() > 0) {
			if (displayValidationMessages(CLIENT_ID,
					validationMessages.toArray(new ValidationMessage[0]))) {
				return FAIL;
			}
		}

		try {
			contactService
					.deleteContactType(this.activeContactTypes, this.endDate);
		} catch (DAOException e) {
			DisplayUtil
					.displayError("Delete contact type update failed for contact type records.");
			logger.error(e);
			return FAIL;
		} catch (RemoteException e) {
			DisplayUtil
					.displayError("Delete contact type update failed for contact type records.");
			logger.error(e);
			return FAIL;
		}

		reset();
		return SUCCESS;
	}

	@Override
	public void performPreliminaryWork(BulkOperationsCatalog catalog)
			throws RemoteException {
		logger.debug("Performing Add Contact Type Preliminary Work");

		// Set selected contact types
		setActiveContactTypes(catalog.getSelectedContactTypes());
		if (activeContactTypes.length == 0) {
			deleteContactTypeEditable = false;
			return;
		}

		if (contact == null) {
			contact = contactService.retrieveContactDetail(
					this.catalog.getContactId());
			List<ContactType> cTypes = contact.getContactTypes();
			this.contactTypes = cTypes.toArray(new ContactType[0]);
		}

		return;
	}

	public final void reset() {
		setContact(null);
		setContactTypes(null);
		setActiveContactTypes(null);
		setDeleteContactTypeEditable(true);
		setFinalStep(false);
		setMessage(null);
	}

	@Override
	public String performPostWork(BulkOperationsCatalog catalog)
			throws RemoteException {
		return null;
	}

	private List<ValidationMessage> validateTimeOutOperation(
			ContactType[] contactTypes, ContactType[] activeContactTypes,
			Timestamp endDate) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		for (ContactType activeCtype : activeContactTypes) {
			if (endDate.before(activeCtype.getStartDate())) {
				validationMessages.add(new ValidationMessage("endDate",
						"Contact Type's end date cannot be before its start date. For fp_id = "
								+ activeCtype.getFacilityId(),
						ValidationMessage.Severity.ERROR, "Facility:"
								+ activeCtype.getFacilityId()));
			}

			for (ContactType cType : contactTypes) {
				if (cType.getEndDate() == null) {
					continue;
				}

				if (!cType.getFacilityId().equals(activeCtype.getFacilityId())) {
					continue;
				}

				if (!cType.getContactTypeCd().equals(
						activeCtype.getContactTypeCd())) {
					continue;
				}

				if (cType.getEndDate().equals(endDate)
						&& cType.getStartDate().equals(
								activeCtype.getStartDate())) {
					validationMessages
							.add(new ValidationMessage(
									"endDate",
									"End date matches the end date of existing contact type record with same contact person and same start date. For fp_id = "
											+ activeCtype.getFacilityId(),
									ValidationMessage.Severity.ERROR,
									"Facility:" + activeCtype.getFacilityId()));
				}

			}
		}

		return validationMessages;
	}

}
