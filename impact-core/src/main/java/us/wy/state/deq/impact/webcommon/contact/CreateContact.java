package us.wy.state.deq.impact.webcommon.contact;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ContactService;

@SuppressWarnings("serial")
public class CreateContact extends AppBase {
	private Contact contact;
	private ContactService contactService;
	private Contact[] matchingContacts = null;
	
	private TableSorter duplicateSearchResultsWrapper;
	
	public static final String DUPLICATE_CONTACT_LIST_URI = "../contacts/displayDuplicateContactList.jsf";
	public static final String DUPLICATE_CONTACT_DIALOG = "dialog:displayDuplicateContacts";
	public static final int DUPLICATE_CONTACT_LIST_WINDOW_HEIGHT = 350;
    public static final int DUPLICATE_CONTACT_LIST_WINDOW_WIDTH = 950;
	
	private boolean hasDuplicateSearchResults;
	private boolean editable1 = true;
	private String popupRedirect;
	

	public CreateContact() {
		duplicateSearchResultsWrapper = new TableSorter();
		popupRedirect = null;
		resetCreateContact();
	}

	public ContactService getContactService() {
		return contactService;
	}
	
	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public final void resetCreateContact() {
		contact = new Contact();
		contact.setAddress(new Address());
	}
	
	public final void resetCreateContact(ActionEvent actionEvent) {
		resetCreateContact();
	}

	public final String submitCreateContact() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		String ret = "contactDetail";
		
		ValidationMessage[] validationMessages;
		try {
			validationMessages = getContactService().validateCreateContact(contact);
			if (displayValidationMessages("createContact:", validationMessages)) {
				return FAIL;
			}

			findMatchingContacts();

			if (!isDuplicateContact()) {
				createNewContact();
			} else {
				duplicateSearchResultsWrapper.clearWrappedData();
				duplicateSearchResultsWrapper.setWrappedData(matchingContacts);
				ret = DUPLICATE_CONTACT_DIALOG;
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("An error occured when creating the contact.");
			handleException(re);
			return FAIL;
		} finally {
			clearButtonClicked();
		}

		return ret ;
	}

	public final String submitCreateContactForStaging() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		String ret = SUCCESS;
		
		ValidationMessage[] validationMessages;
		try {
			validationMessages = getContactService().validateCreateContact(contact);
			if (displayValidationMessages("createContact:", validationMessages)) {
				return FAIL;
			}
			
			findMatchingContacts();
			
			if (!isDuplicateContact()) {
				boolean isContactCreated = createNewContactStaging();
				if (!isContactCreated) {
					return FAIL;
				} else {
					ContactDetailBase contactDetail = (ContactDetailBase) FacesUtil.getManagedBean("contactDetail");
					contactDetail.setContactId(contact.getContactId());
					
					resetCreateContact();
					DisplayUtil.displayInfo("Contact created successfully.");
				}
			} else {
				duplicateSearchResultsWrapper.clearWrappedData();
				duplicateSearchResultsWrapper.setWrappedData(matchingContacts);
				ret = DUPLICATE_CONTACT_DIALOG;
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("An error occured when creating the contact.");
			handleException(re);
			return FAIL;
		} finally {
			clearButtonClicked();
		}

		return ret;
	}

	public final String loadContactAddress() {
		// contact.getAddress().copyAddress(facility.getPhyAddr());
		return null;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public boolean isEditable1() {
		return editable1;
	}

	public void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}

	public TableSorter getDuplicateSearchResultsWrapper() {
		return duplicateSearchResultsWrapper;
	}

	public void setDuplicateSearchResultsWrapper(TableSorter duplicateSearchResultsWrapper) {
		this.duplicateSearchResultsWrapper = duplicateSearchResultsWrapper;
	}

	public boolean isHasDuplicateSearchResults() {
		return hasDuplicateSearchResults;
	}

	public void setHasDuplicateSearchResults(boolean hasDuplicateSearchResults) {
		this.hasDuplicateSearchResults = hasDuplicateSearchResults;
	}

	public final void closeModelessDialog() {
		logger.info("---- Closing pop-up window. ----");
		FacesUtil.returnFromDialog();
	}

	public void findMatchingContacts() throws DAOException {
		if (null != this.contact) {
			Map<String, String> params = new HashMap<String, String>();

			if (contact != null) {
				params.put("firstName", prepareAttribute(this.contact.getFirstNm()));
				params.put("lastName", prepareAttribute(this.contact.getLastNm()));
				params.put("email", prepareAttribute(this.contact.getEmailAddressTxt()));
			}
			this.matchingContacts = getContactService().searchForDuplicateContacts(params, unlimitedResults(), contact);
		}
	}

	private String prepareAttribute(String attribute) {
		String newAttribute = attribute;
		if (newAttribute == null) {
			return newAttribute;
		}

		newAttribute = newAttribute.trim();
		if (newAttribute.length() > 1 && newAttribute.contains(" ")) {
			newAttribute = newAttribute.substring(0, newAttribute.indexOf(" "));
		}
		newAttribute = "%" + newAttribute + "%";

		return newAttribute;
	}

	public void createContactFromDialog() {
		try {
			boolean isContactCreated = createNewContact();
			if (isContactCreated) {
				setPopupRedirect("contactDetail");
				FacesUtil.returnFromDialogAndRefresh();
				return;
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("An error occured when creating the contact.");
			handleException(re);
		}
	}
	
	public final boolean createNewContact() throws DAOException {
		boolean ret = false;
		
		Contact createdContact = getContactService().createContact(contact);
		
		if (createdContact == null) {
			String msg = "Create contact failed.";
			DisplayUtil.displayError(msg);
			logger.error(msg);
		} else {
			contact = createdContact;
			ContactDetailBase contactDetail = (ContactDetailBase) FacesUtil.getManagedBean("contactDetail");
			contactDetail.setContactId(contact.getContactId());	
			
			resetCreateContact();
			DisplayUtil.displayInfo("Contact created successfully.");
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_contactDetail")).setDisabled(false);
			
			ret = true;
		}
		
		return ret;
	}

	public String createStagingContactFromDialog() {
		try {
			boolean isContactCreated = createNewContactStaging();
			
			if (!isContactCreated) {
				return FAIL;
			} else {
				resetCreateContact();
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("An error occured when creating the contact.");
			handleException(re);
			return FAIL;
		}
		return SUCCESS;
	}
	
	public final boolean createNewContactStaging() throws DAOException {
		boolean ret = false;
		
		Contact createdContact = getContactService().createContactForStaging(contact);
		if (createdContact == null) {
			String msg = "Create contact failed.";
			DisplayUtil.displayError(msg);
			logger.error(msg);
		} else {
			contact = createdContact;
			ret = true;
		}
		
		return ret;
	}

	public boolean isDuplicateContact() {
		Boolean ret = false;
		if (null == this.matchingContacts || this.matchingContacts.length == 0) {
			ret = false;
		} else
			ret = true;
		return ret;
	}

	public boolean isDisplayCompanyName() {
		boolean display = false;
		if (isInternalApp()) {
			display = true;
		} else {
			display = false;
		}
		return display;
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

}