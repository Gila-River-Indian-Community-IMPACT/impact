package us.wy.state.deq.impact.webcommon.contact;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.contact.ContactNote;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.app.contact.ContactSearch;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.bo.ContactService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;


public class ContactDetailBase extends TreeBase {

	private static final long serialVersionUID = -7765720515374273102L;

	public static final String CTP_OUTCOME = "contactDetail";
	public static final String CTP_DIALOG_OUTCOME = "dialog:viewContact";
	private Contact contact;
	private boolean editable;
	protected Integer contactId;
	protected String facilityId;
	protected int currentUserId = InfrastructureDefs.getCurrentUserId();
	private TableSorter notesWrapper = new TableSorter();
	private TableSorter notesCtWrapper = new TableSorter();
	private TableSorter possibleDuplicatesWrapper = new TableSorter();
	private ContactNote[] contactNotes;
	private ContactNote contactNote;
	private ContactNote modifyContactNote;
	private boolean noteModify;
	private boolean noteFromContact;
	private boolean editable1;
	private boolean dialogDone;
	private ContactType[] contactTypes;
	private ContactType contactType;
	private TableSorter contactTypesWrapper = new TableSorter();
	private String message;

	private ContactType[] activeContactTypes;
	private boolean timeOutEditable;
	private boolean timeOutFinalStep;
	private transient Timestamp timeOutEndDate;
	private String timeOutMessage;
	private String popupRedirect;

	private boolean staging;
	private boolean hasDuplicates;
	
	private ContactService contactService;
	private CompanyService companyService;

	public ContactDetailBase() {
		popupRedirect = null;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public String viewContactTypeFacility() {

		// TODO: uncertain whether all of these methods are required to be
		// called
		// here or if some extra work in being done to set up the facility
		// profile
		FacesContext context = FacesContext.getCurrentInstance();
		ValueBinding vl = context.getApplication().createValueBinding(
				"#{facilityProfile}");
		FacilityProfile facilityProfile = (FacilityProfile) vl
				.getValue(context);
		facilityProfile.setFacilityId(contactType.getFacilityId());
		facilityProfile.submitProfileById();
		facilityProfile.refreshFacilityProfile();

		return facilityProfile.getActiveFacilityContacts();
	}

	public final String startEditNote(ActionEvent actionEvent) {
		setEditable1(true);
		noteModify = true;
		logger.debug("Made it startEditNote");
		return "dialog:contactNoteDetail";
	}

	public final void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final boolean isDisableNoteButton() {
		if (noteFromContact) {
			return true;
		} else if (isReadOnlyUser()) {
			return true;
		} else if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		} else if (!contactNote.getUserId().equals(getUserID())) {
			return true;
		}
		return false;
	}

	public boolean isDialogDone() {
		return dialogDone;
	}

	public void setDialogDone(boolean dialogDone) {
		this.dialogDone = dialogDone;
	}

	public ContactType getContactType() {
		return contactType;
	}

	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}

	public TableSorter getContactTypesWrapper() {
		return contactTypesWrapper;
	}

	public void setContactTypesWrapper(TableSorter contactTypesWrapper) {
		this.contactTypesWrapper = contactTypesWrapper;
	}

	public final void cancelEditNote() {
		contactNote = null;
		noteModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	protected final void setNotesInContact() {
		if (this.isInternalApp()) {
			notesWrapper.setWrappedData(contactNotes);
		}
		notesCtWrapper.setWrappedData(contactNotes);
	}

	protected final void setContactTypesInContact() {
		if (this.isInternalApp()) {
			contactTypesWrapper.setWrappedData(contactTypes);
		}
		contactTypesWrapper.setWrappedData(contactTypes);
	}

	public final void applyEditNote(ActionEvent actionEvent) {
		boolean operationOK = true;

		// make sure note is provided
		if (contactNote.getNoteTxt() == null || contactNote.getNoteTxt().trim().equals("")) {
			DisplayUtil.displayError("Attribute Note is not set.");
			return;
		}
		  
		ValidationMessage[] validationMessages = contactNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}

		try {

			if (noteModify == false) {
				getContactService().createContactNote(contactNote);
			} else {
				// edit
				getContactService().modifyContactNote(contactNote);
			}
			contactNotes = getContactService().retrieveContactNotes(contactId);
			setNotesInContact();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil.displayInfo("contact notes updated successfully");
		} else {
			cancelEditNote();
			DisplayUtil.displayError("Updating contact notes failed");
		}

		noteModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final boolean isReadOnlyNote() {
		if (!editable1) {
			return true;
		} else if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		} else if (!contactNote.getUserId().equals(getUserID())) {
			return true;
		}
		return false;
	}

	public final String startAddNote() {
		setEditable1(true);
		noteModify = false;
		contactNote = new ContactNote();
		contactNote.setContactId(contactId);
		contactNote.setUserId(getUserID());
		contactNote.setNoteTypeCd(NoteType.DAPC);
		contactNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
		return "dialog:contactNoteDetail";
	}

	public final String startViewContactType() {
		return "dialog:contactTypeDetail";

	}

	public final String startViewNote() {
		contactNote = new ContactNote(modifyContactNote);
		return "dialog:contactNoteDetail";
	}

	public final String startAddContactType() {
		return "dialog:contactTypeDetail";

	}

	public final Integer getUserID() {
		return currentUserId;
	}

	public TableSorter getNotesWrapper() {
		return notesWrapper;
	}

	public void setNotesWrapper(TableSorter notesWrapper) {
		this.notesWrapper = notesWrapper;
	}

	public TableSorter getNotesCtWrapper() {
		return notesCtWrapper;
	}

	public void setNotesCtWrapper(TableSorter notesCtWrapper) {
		this.notesCtWrapper = notesCtWrapper;
	}

	public ContactNote[] getContactNotes() {
		return contactNotes;
	}

	public void setContactNotes(ContactNote[] contactNotes) {
		this.contactNotes = contactNotes;
	}

	public ContactNote getContactNote() {
		return contactNote;
	}

	public void setContactNote(ContactNote contactNote) {
		this.contactNote = contactNote;
	}

	public final ContactNote getModifyContactNote() {
        return modifyContactNote;
    }

    public final void setModifyContactNote(ContactNote modifyContactNote) {
        this.modifyContactNote = modifyContactNote;
    }
    
	public boolean isNoteModify() {
		return noteModify;
	}

	public void setNoteModify(boolean noteModify) {
		this.noteModify = noteModify;
	}

	public boolean isNoteFromContact() {
		return noteFromContact;
	}

	public void setNoteFromContact(boolean noteFromContact) {
		this.noteFromContact = noteFromContact;
	}

	public boolean isEditable1() {
		return editable1;
	}

	public void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}

	public final void editContact() {
		setEditable(true);
		contact.clearValidationMessages();
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	public final String deleteContact() {
		setMessage("You are about to delete this contact. Deleting a contact will permanently remove them from any contact listings.");
		return "dialog:deleteContact";
	}

	public final boolean isDisabledUpdateButton() {
		if (isReadOnlyUser()) {
			return true;
		}

		if (isGeneralUser()) {
			return false;
		}

		// handle when not internal
		if (isPortalApp()) {
			MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			if (myTasks.isFromHomeContact()) {
				return true;
			} else {
				// TODO make false later
				return false;
			}
		}

		if (contact != null
				&& (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin())) {
			return false;
		}

		return true;
	}

	public final boolean getEditable() {
		return editable;
	}

	protected final boolean validateContact() {
		boolean isValid = true;
		contact.clearValidationMessages();
		ValidationMessage[] validationMessages = null;

		try {
			validationMessages = getContactService().validateContact(contact);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing Contact failed for validation");
			isValid = false;
		}

		if (displayValidationMessages("contactDetail:comCtDetail:",
				validationMessages)) {
			isValid = false;
		}

		return isValid;
	}

	public final boolean isDapcUser() {
		return isInternalApp();
	}

	public final String saveContactDetail() {

		boolean operationOk = true;

		if (!validateContact()) {
			operationOk = false;
			return FAIL;
		}

		setEditable(false);

		try {
			if (facilityId != null) {
				if (contact.getFacilityId() == null) {
					// facility id is used by staging for contacts
					contact.setFacilityId(facilityId);
				}
			}

			if (!getContactService().modifyContact(contact)) {
				DisplayUtil.displayInfo("Contact update failed.");
			}
		} catch (RemoteException re) {
			handleException(re, "Saving contact data failed");
			operationOk = false;
		}

		refreshContact();
		if (operationOk) {
			DisplayUtil.displayInfo("Contact data saved successfully");
		}

		if (operationOk) {
			return SUCCESS;
		} else {
			return FAIL;
		}
	}

	private void handleException(RemoteException re, String message) {
		if (re != null) {
			if (re instanceof DataStoreConcurrencyException) {
				DisplayUtil
						.displayError(message
								+ " - more than one user was attempting to edit and save contact information at one time. Please re-enter your changes and submit again.");
				logger.warn(re.getMessage(), re);
			} else {
				DisplayUtil
						.displayError(message
								+ " -  system error has occurred. Please contact System Administrator.");
				logger.error(re.getMessage(), re);
			}
		}
	}

	public final void applyContactDelete() {
		try {
			getContactService().deleteContact(contact);
			FacesContext context = FacesContext.getCurrentInstance();
			ValueBinding vl = context.getApplication().createValueBinding(
					"#{contactSearch}");
			ContactSearch contactSearch = (ContactSearch) vl.getValue(context);
			contactSearch.submitSearch();
			DisplayUtil.displayInfo("Contact has been deleted");
			setPopupRedirect("contactSearch");
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_contactDetail"))
					.setDisabled(true);
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Deleting contact failed");
		} finally {
			setMessage(null);
		}

		refreshContact();

		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelContactDelete() {
		setMessage(null);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String cancelEditContact() {
		cancelEdit();
		// current = contactId;
		return getContactDetailOutcome();
	}

	public final void cancelEditContactStaging() {
		cancelEdit();
		// current = contactId;
		return;
	}

	public final String cancelEdit() {
		setEditable(false);
		setMessage(null);
		refreshContact();
		return CANCELLED;
	}

	protected final void refreshContact() {
		contact = null;
		treeData = null;
		getTreeData();
	}

	public final String submitDetail() {
		resetTimeOut();
		editable = false;
		contact = null;
		treeData = null;
		contact = getContact();
		if (contact == null) {
			logger.error("Unexpected NULL pointer return from accesing existing contact from Database.");
			DisplayUtil
					.displayError("Contact is not found with that number.");
			return null;
		}
		return getContactDetailOutcome();
	}

	public final String getContactDetailOutcome() {
		return ContactDetailBase.CTP_OUTCOME;
	}

	public final Contact getContact() {
		if (contact == null) {
			logger.debug("getContact: contact is null " + ", contactId = "
					+ contactId);
		}
		if ((contact == null)
				|| (contactId != null && !contactId.equals(contact
						.getContactId()))) {
			logger.debug("contactId = " + contactId);
			try {
				contact = getContactService().retrieveContactDetail(contactId, false);

				if (contact != null) {
					contactId = contact.getContactId();

					contactNotes = contact.getNotes().toArray(
							new ContactNote[0]);
					setNotesInContact();

					contactTypes = contact.getContactTypes().toArray(
							new ContactType[0]);
					setContactTypesInContact();

					logger.debug("Contact Name: " + contact.getName());

					// get possible duplicates
					setPossibleContactDuplicatesInContact();
				} else {
					logger.error("No contact found for contactId = "
							+ contactId);
				}
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing contact failed");
			}
		} else {
			logger.debug("No need to re-query for contact.");
		}
		if (contact == null) {
			logger.error("getContact is returning NULL!!!  contactId = "
					+ contactId);
		}

		return contact;
	}

	public final Contact getContactFromStaging() {
		if (contact == null) {
			logger.debug("getContact: contact is null " + ", contactId = "
					+ contactId);
		}
		if ((contact == null)
				|| (contactId != null
						&& !contactId.equals(contact.getContactId()) && facilityId != null)) {
			logger.debug("contactId = " + contactId);
			try {
				contact = getContactService().retrieveStagingContactDetail(contactId,
						facilityId, true);

				if (contact == null) {
					contact = getContactService().retrieveContactDetail(contactId,
							false);
				}

				if (contact != null) {
					contact.setFacilityId(facilityId);
					contactId = contact.getContactId();

					contactTypes = contact.getContactTypes().toArray(
							new ContactType[0]);
					setContactTypesInContact();

					logger.debug("Contact Name: " + contact.getName());
				} else {
					logger.error("No contact found for contactId = "
							+ contactId);
				}
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing contact failed");
			}
		} else {
			logger.debug("No need to re-query for contact.");
		}
		if (contact == null) {
			logger.error("getContact is returning NULL!!!  contactId = "
					+ contactId);
		}

		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public final Company getCompany() {
		return null;
	}

	public String timeOutContact() {
		resetTimeOut();

		if (contactTypes != null) {
			List<ContactType> activeCTypes = new ArrayList<ContactType>();
			for (ContactType cType : contactTypes) {
				if (cType.getEndDate() == null) {
					activeCTypes.add(cType);
				}
			}

			activeContactTypes = activeCTypes.toArray(new ContactType[0]);

		}

		if (activeContactTypes.length > 0) {
			return "dialog:timeOutContact";
		} else {
			DisplayUtil
					.displayInfo("There are no active contact type records for this contact");
			return CTP_OUTCOME;
		}
	}

	public boolean isTimeOutEditable() {
		return timeOutEditable;
	}

	public void setTimeOutEditable(boolean timeOutEditable) {
		this.timeOutEditable = timeOutEditable;
	}

	public boolean isTimeOutFinalStep() {
		return timeOutFinalStep;
	}

	public void setTimeOutFinalStep(boolean timeOutFinalStep) {
		this.timeOutFinalStep = timeOutFinalStep;
	}

	public ContactType[] getActiveContactTypes() {
		return activeContactTypes;
	}

	public void setActiveContactTypes(ContactType[] activeContactTypes) {
		this.activeContactTypes = activeContactTypes;
	}

	public Timestamp getTimeOutEndDate() {
		return timeOutEndDate;
	}

	public void setTimeOutEndDate(Timestamp timeOutEndDate) {
		this.timeOutEndDate = timeOutEndDate;
	}

	public String getTimeOutMessage() {
		return timeOutMessage;
	}

	public void setTimeOutMessage(String timeOutMessage) {
		this.timeOutMessage = timeOutMessage;
	}

	public String getPopupRedirect() {
		if (popupRedirect != null) {
			FacesUtil.setOutcome(null, popupRedirect);
			popupRedirect = null;
		}

		return popupRedirect;
	}

	public void setPopupRedirect(String popupRedirect) {
		this.popupRedirect = popupRedirect;
	}

	public void applyTimeOut() {
		List<ValidationMessage> validiationMessages = new ArrayList<ValidationMessage>();
		java.util.Date date = new java.util.Date();
		Timestamp currentTime = new Timestamp(date.getTime());

		if (timeOutEndDate == null) {
			validiationMessages.add(new ValidationMessage("endDate",
					"End date is missing.", ValidationMessage.Severity.ERROR));
		} else {
			if (timeOutEndDate.after(currentTime)) {
				validiationMessages
						.add(new ValidationMessage(
								"endDate",
								"Invalid End Date; End date cannot be placed in the future",
								ValidationMessage.Severity.ERROR));
			}
		}

		if (validiationMessages.size() > 0) {
			if (displayValidationMessages("contact",
					validiationMessages.toArray(new ValidationMessage[0]))) {
				return;
			}
		}
		setTimeOutEditable(false);
		setTimeOutFinalStep(true);
		String numberOfContactTypes = NumberFormat.getNumberInstance(
				java.util.Locale.US).format(activeContactTypes.length);
		setTimeOutMessage("Caution: the following operation will result in a total of "
				+ numberOfContactTypes
				+ " contact type records being timed out.");
	}

	public void saveTimeOut() {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		validationMessages.addAll(validateTimeOutOperation(contactTypes,
				activeContactTypes, timeOutEndDate));

		if (validationMessages.size() > 0) {
			if (displayValidationMessages("Contact",
					validationMessages.toArray(new ValidationMessage[0]))) {
				changeTimeOut();
			}
		} else {

			try {
				getContactService().deleteContactType(activeContactTypes,
						timeOutEndDate);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Time out failed for contact type records.");
				logger.error(e);
			} catch (RemoteException e) {
				DisplayUtil
						.displayError("Time out failed for contact type records.");
				logger.error(e);
			}

			resetTimeOut();
			refreshContact();
			FacesUtil.returnFromDialogAndRefresh();
		}
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

	public void cancelTimeOut() {
		resetTimeOut();
		FacesUtil.returnFromDialogAndRefresh();
	}

	private void resetTimeOut() {
		setTimeOutEditable(true);
		setActiveContactTypes(null);
		setTimeOutFinalStep(false);

	}

	public void changeTimeOut() {
		setTimeOutEditable(true);
		setTimeOutFinalStep(false);
	}

	public final String goToDetail() {
		resetTimeOut();
		refreshContact();
		editable = false;
		if (staging) {
			contact = getContactFromStaging();
		} else {
			contact = getContact();
		}
		if (contact == null) {
			logger.error("Unexpected NULL pointer return from accesing existing contact from Database.");
			DisplayUtil
					.displayError("Contact is not found due to unexpected system problem; Please try again.");
		}

		return CTP_DIALOG_OUTCOME;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public boolean isStaging() {
		return staging;
	}

	public void setStaging(boolean staging) {
		this.staging = staging;
	}

	public TableSorter getPossibleDuplicatesWrapper() {
		return possibleDuplicatesWrapper;
	}

	public void setPossibleDuplicatesWrapper(
			TableSorter possibleDuplicatesWrapper) {
		this.possibleDuplicatesWrapper = possibleDuplicatesWrapper;
	}

	private void setPossibleContactDuplicatesInContact() {
		if (this.isInternalApp()) {
			setHasDuplicates(false);
			if (contact != null) {
				Contact[] duplicateContacts = null;

				try {
					duplicateContacts = getContactService().searchForDuplicateContacts(
							null, unlimitedResults(), contact);
					DisplayUtil.displayHitLimit(duplicateContacts.length);
					possibleDuplicatesWrapper.setWrappedData(duplicateContacts);

					if (duplicateContacts.length != 0) {
						setHasDuplicates(true);
					}
				} catch (RemoteException re) {
					handleException(re);
					DisplayUtil.displayError("Search failed");
					duplicateContacts = new Contact[0];
					possibleDuplicatesWrapper.setWrappedData(duplicateContacts);
				}
			} else {
				possibleDuplicatesWrapper.setWrappedData(null);
			}
		}
	}

	public boolean isHasDuplicates() {
		return hasDuplicates;
	}

	public void setHasDuplicates(boolean hasDuplicates) {
		this.hasDuplicates = hasDuplicates;
	}

}
