package us.wy.state.deq.impact.app.contact;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.model.RowKeySet;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ContactService;

@SuppressWarnings("serial")
public class MergeContact extends AppBase {
	public static String MC_OUTCOME = "contacts.mergeContact";
	public static String MC_CONFIRM = "dialog:confirmMergeContact";
	private Contact baseContact;
	private Contact[] selectedTargetContacts;
	private TableSorter duplicateSearchResultsWrapper;
	private TableSorter selectedTargetContactsWrapper;
	private Contact[] contacts;
	private boolean hasDuplicateSearchResults;

	private String duplicateFirstName;
	private String duplicateLastName;
	private String duplicateEmail;
	private boolean editableDuplicateSearch;

	private String popupRedirect;
	
	private ContactService contactService;
	
	
	public MergeContact() {
		super();
		duplicateSearchResultsWrapper = new TableSorter();
		setSelectedTargetContactsWrapper(new TableSorter());
		cacheViewIDs.add("/contacts/contactSearch.jsp");
	}
	public ContactService getContactService() {
		return contactService;
	}
	
	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public Contact getBaseContact() {
		return baseContact;
	}

	public void setBaseContact(Contact baseContact) {
		if (baseContact != null) {
			if (!Utility.isNullOrEmpty(baseContact.getFirstNm())) {
				setDuplicateFirstName(prepareAttribute(baseContact.getFirstNm()));
			} else {
				setDuplicateFirstName(null);
			}

			if (!Utility.isNullOrEmpty(baseContact.getLastNm())) {
				setDuplicateLastName(prepareAttribute(baseContact.getLastNm()));
			} else {
				setDuplicateLastName(null);
			}

			setDuplicateEmail(null);

			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_mergeContact"))
					.setDisabled(false);
		} else {
			setDuplicateFirstName(null);
			setDuplicateLastName(null);
			setDuplicateEmail(null);
			
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_mergeContact"))
					.setDisabled(true);
		}
		this.baseContact = baseContact;
	}

	public TableSorter getDuplicateSearchResultsWrapper() {
		return duplicateSearchResultsWrapper;
	}

	public void setDuplicateSearchResultsWrapper(
			TableSorter duplicateSearchResultsWrapper) {
		this.duplicateSearchResultsWrapper = duplicateSearchResultsWrapper;
	}

	public TableSorter getSelectedTargetContactsWrapper() {
		return selectedTargetContactsWrapper;
	}

	public void setSelectedTargetContactsWrapper(
			TableSorter selectedTargetContactsWrapper) {
		this.selectedTargetContactsWrapper = selectedTargetContactsWrapper;
	}

	public boolean isHasDuplicateSearchResults() {
		return hasDuplicateSearchResults;
	}

	public void setHasDuplicateSearchResults(boolean hasDuplicateSearchResults) {
		this.hasDuplicateSearchResults = hasDuplicateSearchResults;
	}

	public String submitDuplicateSearch() {
		contacts = null;
		setHasDuplicateSearchResults(false);
		setEditableDuplicateSearch(false);

		Map<String, String> params = new HashMap<String, String>();
		if (baseContact != null) {
			params.put("firstName", duplicateFirstName);
			params.put("lastName", duplicateLastName);
			params.put("email", duplicateEmail);
		} else {
			DisplayUtil.displayError("No base contact was selected");
			return "contactSearch";
		}

		try {
			contacts = getContactService().searchForDuplicateContacts(params,
					unlimitedResults(), baseContact);
			DisplayUtil.displayHitLimit(contacts.length);
			duplicateSearchResultsWrapper.clearWrappedData();
			duplicateSearchResultsWrapper.setWrappedData(contacts);

			if (contacts.length == 0) {
				DisplayUtil.displayInfo("No duplicate contacts were found");
			} else {
				setHasDuplicateSearchResults(true);
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search failed");
			contacts = new Contact[0];
			duplicateSearchResultsWrapper.setWrappedData(contacts);
		}

		return MC_OUTCOME;
	}

	public String reset() {
		setEditableDuplicateSearch(false);

		return SUCCESS;
	}

	public String resetBaseContact() {
		baseContact = null;
		return SUCCESS;
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

	public String merge() {
		setEditableDuplicateSearch(false);

		List<Contact> selectedContactList = new ArrayList<Contact>();

		try {
			UIXTable table = this.duplicateSearchResultsWrapper.getTable();
			@SuppressWarnings("unchecked")
			Iterator<RowKeySet> selection = table.getSelectionState()
					.getKeySet().iterator();
			while (selection.hasNext()) {
				table.setRowKey(selection.next());
				Contact row = (Contact) table.getRowData();
				if (row != null) {
					selectedContactList.add(row);
				}
			}

		} catch (Exception re) {
			DisplayUtil.displayError("Could not load chosen contact.");
			return FAIL;
		}

		selectedTargetContacts = selectedContactList.toArray(new Contact[0]);
		selectedTargetContactsWrapper.setWrappedData(selectedTargetContacts);

		return MC_CONFIRM;
	}

	public String cancelMerge() {
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public final void mergeContacts() {
		if (baseContact == null) {
			DisplayUtil.displayError("No base contact selected for merge");
			FacesUtil.returnFromDialogAndRefresh();
			return;
		}

		if (!baseContact.isActive()) {
			DisplayUtil
					.displayError("Base contact must be set to active for merge");
			FacesUtil.returnFromDialogAndRefresh();
			return;
		}

		if (selectedTargetContacts == null
				|| selectedTargetContacts.length == 0) {
			DisplayUtil.displayError("No target contacts selected for merge");
			FacesUtil.returnFromDialogAndRefresh();
			return;
		}

		try {
			// must retrieve full contact details for merge process
			baseContact = getContactService().retrieveContactDetail(
					baseContact.getContactId());

			List<Contact> updatedTargetList = new ArrayList<Contact>();
			for (Contact targetContact : selectedTargetContacts) {
				targetContact = getContactService().retrieveContactDetail(
						targetContact.getContactId());
				updatedTargetList.add(targetContact);
			}

			selectedTargetContacts = updatedTargetList.toArray(new Contact[0]);

			getContactService().mergeContact(baseContact, selectedTargetContacts);
		} catch (Exception re) {
			DisplayUtil.displayError("Could not merge contact");
			return;
		}

		ContactDetail cd = (ContactDetail) FacesUtil
				.getManagedBean("contactDetail");
		cd.setContactId(baseContact.getContactId());
		cd.refreshContactDetail();
		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_contactDetail"))
				.setDisabled(false);
		setPopupRedirect(ContactDetail.CTP_OUTCOME);
		setBaseContact(null);

		DisplayUtil.displayInfo("The selected contacts have been merged");

		FacesUtil.returnFromDialogAndRefresh();
	}

	public String getPopupRedirect() {
		if (popupRedirect != null) {
			logger.debug("Trying to be redirected to: " + popupRedirect);
			FacesUtil.setOutcome(null, popupRedirect);
			popupRedirect = null;
		} else {
			if(baseContact == null){
				popupRedirect = "contactSearch";
				logger.debug("Trying to be redirected to: " + popupRedirect);
				FacesUtil.setOutcome(null, popupRedirect);
				popupRedirect = null;
			}
		}
		return null;
	}

	public final void setPopupRedirect(String popupRedirect) {
		this.popupRedirect = popupRedirect;
	}

	public String getDuplicateFirstName() {
		return duplicateFirstName;
	}

	public void setDuplicateFirstName(String duplicateFirstName) {
		this.duplicateFirstName = duplicateFirstName;
	}

	public String getDuplicateLastName() {
		return duplicateLastName;
	}

	public void setDuplicateLastName(String duplicateLastName) {
		this.duplicateLastName = duplicateLastName;
	}

	public String getDuplicateEmail() {
		return duplicateEmail;
	}

	public void setDuplicateEmail(String duplicateEmail) {
		this.duplicateEmail = duplicateEmail;
	}

	public boolean isEditableDuplicateSearch() {
		return editableDuplicateSearch;
	}

	public void setEditableDuplicateSearch(boolean editableDuplicateSearch) {
		this.editableDuplicateSearch = editableDuplicateSearch;
	}

	public String modifyDuplicateContactSearch() {
		setEditableDuplicateSearch(true);
		return null;
	}
}
