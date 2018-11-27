package us.wy.state.deq.impact.app.contact;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.wy.state.deq.impact.bo.ContactService;

@SuppressWarnings("serial")
public class ContactSearch extends AppBase {

	private Contact[] contacts;
	private boolean hasSearchResults;
	private TableSorter resultsWrapper;

	private String firstName;
	private String lastName;
	private String preferredName;
	private String phone;
	private Integer companyId;
	private String cntId;
	private String active;
	private String externalUsername;
	
	private ContactService contactService;
	

	public ContactSearch() {
		super();
		resultsWrapper = new TableSorter();
		cacheViewIDs.add("/contacts/contactSearch.jsp");
	}

	public ContactService getContactService() {
		return contactService;
	}
	
	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCntId() {
		return cntId;
	}

	public void setCntId(String cntId) {
		this.cntId = cntId;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getExternalUsername() {
		return externalUsername;
	}

	public void setExternalUsername(String externalUsername) {
		this.externalUsername = externalUsername;
	}

	public boolean getHasSearchResults() {
		return hasSearchResults;
	}

	public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public TableSorter getResultsWrapper() {
		return resultsWrapper;
	}

	public void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}

	public String submitSearch() {
		contacts = null;
		hasSearchResults = false;

		Map<String, String> params = new HashMap<String, String>();
		params.put("cntId", cntId);
		params.put("firstName", firstName);
		params.put("lastName", lastName);
		params.put("preferredName", preferredName);
		params.put("phone", phone);
		params.put("companyId", companyId == null ? "" : companyId.toString());
		params.put("active", active);
		params.put("enviteUsername", externalUsername);

		try {
			contacts = getContactService().searchContacts(params, unlimitedResults());
			DisplayUtil.displayHitLimit(contacts.length);
			resultsWrapper.setWrappedData(contacts);
			if (contacts.length == 0) {
				DisplayUtil
						.displayInfo("There are no contacts that match the search criteria");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search failed");
			contacts = new Contact[0];
			resultsWrapper.setWrappedData(contacts);
		}

		return SUCCESS;
	}

	public String reset() {
		firstName = null;
		lastName = null;
		preferredName = null;
		active = null;
		companyId = null;
		phone = null;
		resultsWrapper.clearWrappedData();
		hasSearchResults = false;
		cntId = null;
		externalUsername = null;

		return SUCCESS;
	}

	public String refreshSearchContacts() {
		return "contactSearch";
	}

}
