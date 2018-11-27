package us.oh.state.epa.stars2.app.facility;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityHistList;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

@SuppressWarnings("serial")
public class FacilityHistorySearch extends AppBase {
    private FacilityHistList searchFacility = new FacilityHistList();
    private String selFacilityId;
    private FacilityHistList[] facilities;
    private boolean hasSearchResults;

    private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public FacilityHistorySearch() {
        super();
        
        cacheViewIDs.add("/facilities/facilityHistorySearch.jsp");
    }
    
    public final String getSelFacilityId() {
        return selFacilityId;
    }

    public final void setSelFacilityId(String selFacilityId) {
        this.selFacilityId = selFacilityId;
    }

    public final String submitSearch() {
        facilities = null;
        hasSearchResults = false;

        try {
            searchFacility.setUnlimitedResults(unlimitedResults());
            facilities = getFacilityService().searchFacilitiesHist(searchFacility);
            DisplayUtil.displayHitLimit(facilities.length);
            if (facilities.length == 0) {
                DisplayUtil
                        .displayInfo("There are no facilities that match the search criteria");
            } else {
                hasSearchResults = true;
            }
        } catch (RemoteException re) {
        	handleException(re);
            DisplayUtil.displayError("Search failed");
        }

        return SUCCESS;
    }

    public final String reset() {
        searchFacility = new FacilityHistList();
        selFacilityId = null;
        facilities = null;
        hasSearchResults = false;
        return SUCCESS;
    }

    public final boolean getHasSearchResults() {
        return hasSearchResults;
    }

    public final FacilityHistList[] getFacilities() {
        return facilities;
    }

    public final String submitCurrentProfile() {
        String ret = FAIL;
        Facility facility;
        try {
            facility = getFacilityService().retrieveFacilityData(selFacilityId, -1);
            FacilityProfile facilityProfile = (FacilityProfile) FacesUtil
                    .getManagedBean("facilityProfile");
            facilityProfile.setFpId(facility.getFpId());
            facilityProfile.submitProfile();
            ret = SUCCESS;
        } catch (RemoteException re) {
        	handleException(re);
            DisplayUtil.displayError("Accessing Current facility inventory failed");
        }
        return ret;
    }

    public final FacilityHistList getSearchFacility() {
        return searchFacility;
    }

    public final void setSearchFacility(FacilityHistList searchFacility) {
        this.searchFacility = searchFacility;
    }
    
    public String refreshSearchFacilities() {
		// if (hasSearchResults) {
		// submitSearch();
		// }
    	
    	return "facilities.facilityHistorySearch";
    }
    
    public void restoreCache() {
    }

	public void clearCache() {
		// facilities = null;
		// hasSearchResults = false;
	}
}
