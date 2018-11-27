package us.oh.state.epa.stars2.app.facility;

import java.rmi.RemoteException;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRequestList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.def.DOLaaCeta;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.FacilityRequestStatusDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.facility.NewFacilityRequest;

@SuppressWarnings("serial")
public class FacilityRequestSearch extends AppBase {
	private String facilityName;
	private String requestId;
	private String operatingStatusCd;
	private String countyCd;
	private String doLaaCd;
	private FacilityRequestList[] facilityRequests;
	private boolean hasSearchResults;
	private TableSorter resultsWrapper;
	private String address1;
	private String facilityTypeCd;
	private String companyName;
	
	private String firstName;
	private String lastName;
	private String phone;
	private String cntId;
	private String externalUsername;
	private String email;
	private String requestStateCd = FacilityRequestStatusDef.PENDING;
	

	private String popupRedirectOutcome = null;
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public final String getCompanyName() {
		return companyName;
	}

	public final void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public FacilityRequestSearch() {
		super();

		resultsWrapper = new TableSorter();

		cacheViewIDs.add("/facilities/facilityRequestSearch.jsp");
	}

	public final TableSorter getResultsWrapper() {
		return resultsWrapper;
	}
	
	public void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}

	public final String getRequestId() {
		return requestId;
	}

	public final void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public final String getFacilityName() {
		return facilityName;
	}

	public final void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public final String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public final void setOperatingStatusCd(String operatingStatusCd) {
		this.operatingStatusCd = operatingStatusCd;
	}

	public final String getCountyCd() {
		return countyCd;
	}

	public final void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final List<SelectItem> getDoLaas() {
		DefData dd = DoLaaDef.getData();
		DefSelectItems items = dd.getItems();
		return items.getAllSearchItems();
	}

	public final List<SelectItem> getDoLaasCeta() {
		return DOLaaCeta.getData().getItems().getAllSearchItems();
	}

	public String submitSearch() {
		facilityRequests = null;
		hasSearchResults = false;
		
		Integer companyId = null; // this parameter is used to select facility requests for a given company 
								//   to display on Facility Selector page of the portal

		try {
			facilityRequests = getFacilityService().searchFacilityRequests(facilityName,
					requestId, companyName, countyCd,
					operatingStatusCd, doLaaCd, firstName, lastName,
					externalUsername, address1, cntId, phone, email,
					requestStateCd, unlimitedResults(), facilityTypeCd, companyId);
			
			DisplayUtil.displayHitLimit(facilityRequests.length);
			resultsWrapper.setWrappedData(facilityRequests);
			if (facilityRequests.length == 0) {
				DisplayUtil
						.displayInfo("There are no facility requests that match the search criteria");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search failed");
			facilityRequests = new FacilityRequestList[0];
			resultsWrapper.setWrappedData(facilityRequests);
		}

		return SUCCESS;
	}

	public String reset() {
		address1 = null;
		facilityName = null;
		requestId = null;
		companyName = null;
		operatingStatusCd = null;
		countyCd = null;
		doLaaCd = null;
		facilityRequests = null;
		resultsWrapper.clearWrappedData();
		hasSearchResults = false;
		facilityTypeCd = null;
		firstName = null;
		lastName = null;
		phone = null;
		cntId = null;
		externalUsername = null;
		email = null;
		requestStateCd = FacilityRequestStatusDef.PENDING;
		popupRedirectOutcome = null;
		return SUCCESS;
	}

	public final boolean getHasSearchResults() {
		return hasSearchResults;
	}

	public FacilityRequestList[] getFacilityRequests() {
		return facilityRequests;
	}

	public String refreshSearchFacilityRequests() {
		NewFacilityRequest nfr = (NewFacilityRequest) FacesUtil
				.getManagedBean("newFacilityRequest");
		nfr.setFacilityRequest(null);
		nfr.setEditMode(false);
		
		this.reset();
		this.submitSearch();

		return "facilityRequestSearch";
	}

	public void restoreCache() {
		// submitSearch();
	}

	public void clearCache() {
		//if (resultsWrapper != null) {
		//	resultsWrapper.clearWrappedData();
		//}

		//facilityRequests = null;
		//hasSearchResults = false;
	}

	public final void setFacilityRequests(FacilityRequestList[] facilityRequests) {
		this.facilityRequests = facilityRequests;
	}

	public final void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public final String getAddress1() {
		return address1;
	}

	public final void setAddress1(String address1) {
		this.address1 = address1;
	}

	public final void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public final String getFacilityTypeCd() {
		return facilityTypeCd;
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
	
	public String getCntId() {
		return cntId;
	}

	public void setCntId(String cntId) {
		this.cntId = cntId;
	}
	
	public String getExternalUsername() {
		return externalUsername;
	}

	public void setExternalUsername(String externalUsername) {
		this.externalUsername = externalUsername;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRequestStateCd() {
		return requestStateCd;
	}

	public void setRequestStateCd(String requestStateCd) {
		this.requestStateCd = requestStateCd;
	}
	
	public final String getPopupRedirectOutcome() {
		return popupRedirectOutcome;
	}

	public final void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}
	
    public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }
}
