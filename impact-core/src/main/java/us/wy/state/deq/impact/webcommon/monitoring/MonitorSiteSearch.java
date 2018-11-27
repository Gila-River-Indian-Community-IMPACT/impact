package us.wy.state.deq.impact.webcommon.monitoring;

import java.rmi.RemoteException;
import java.util.LinkedHashMap;

import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;

@SuppressWarnings("serial")
public class MonitorSiteSearch extends AppBase {
	private boolean hasSearchResults;
	private MonitorSite searchObj = new MonitorSite();
	
	private MonitorSite[] monitorSites;
	private TableSorter resultsWrapper = new TableSorter();
	private TableSorter facResultsWrapper = new TableSorter();
	
	public static final String SEARCH_OUTCOME = "monitoring.monitorSiteSearch";
	
	private MonitoringService monitoringService;
	
	private InfrastructureDefs infraDefs;
	
	private String popupRedirectOutcome;

	public InfrastructureDefs getInfraDefs() {
		return infraDefs;
	}

	public void setInfraDefs(InfrastructureDefs infraDefs) {
		this.infraDefs = infraDefs;
	}

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}
	
	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}
	
	public final String submitSearch() {
		monitorSites = null;
		hasSearchResults = false;
		
		try {
			resultsWrapper = new TableSorter();
			facResultsWrapper = new TableSorter();
			
			// if the user is a AQD monitor report uploader, then don't include
			// the sites belonging to the facility owned monitor groups
			// in the search results
			boolean excludeFacilityOwned = false;
			InfrastructureDefs infraDefs = (InfrastructureDefs)FacesUtil.getManagedBean("infraDefs");
			excludeFacilityOwned = infraDefs.isAmbientMonitorReportUploader();		
			monitorSites = getMonitoringService().searchMonitorSites(searchObj, excludeFacilityOwned);
			loadCountyNames(monitorSites);
			resultsWrapper.setWrappedData(monitorSites);
			facResultsWrapper.setWrappedData(monitorSites);
			if (monitorSites.length == 0) {
				DisplayUtil
				.displayInfo("Cannot find any Monitor Sites for this search");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("Search Failed");
			logger.error(re.getMessage(), re);
		}
		return SUCCESS;
	}
	
	private void loadCountyNames(MonitorSite[] monitorSites) {
		LinkedHashMap<String,String> counties = 
				getInfraDefs().getCounties();
		LinkedHashMap<String,String> countiesById = 
				new LinkedHashMap<String,String>();
		for (String name : counties.keySet()) {
			countiesById.put(counties.get(name), name);
		}
		for (MonitorSite site : monitorSites) {
			site.setCountyName(countiesById.get(site.getCounty()));
		}
	}

	public final String reset() {
		searchObj = new MonitorSite();
		hasSearchResults = false;
		 resultsWrapper.clearWrappedData();
		 facResultsWrapper.clearWrappedData();
		return SUCCESS;
	}
	
	public String refresh() {
		return SEARCH_OUTCOME;
	}

	public String getMgrpId() {
		return searchObj.getMgrpId();
	}

	public void setMgrpId(String mgrpId) {
		searchObj.setMgrpId(mgrpId);
	}

	public String getMstId() {
		return searchObj.getMstId();
	}

	public void setMstId(String mstId) {
		searchObj.setMstId(mstId);
	}

	public String getGroupName() {
		return searchObj.getGroupName();
	}

	public void setGroupName(String groupName) {
		searchObj.setGroupName(groupName);
	}

	public Integer getGroupId() {
		return searchObj.getGroupId();
	}

	public void setGroupId(Integer groupId) {
		searchObj.setGroupId(groupId);
	}

	public String getSiteName() {
		return searchObj.getSiteName();
	}

	public void setSiteName(String siteName) {
		searchObj.setSiteName(siteName);
	}

	public Integer getSiteId() {
		return searchObj.getSiteId();
	}

	public void setSiteId(Integer siteId) {
		searchObj.setSiteId(siteId);
	}

	public String getCounty() {
		return searchObj.getCounty();
	}

	public void setCounty(String county) {
		searchObj.setCounty(county);
	}

	public String getFacilityId() {
		return searchObj.getFacilityId();
	}

	public void setFacilityId(String facilityId) {
		searchObj.setFacilityId(facilityId);
	}

	public String getFacilityName() {
		return searchObj.getFacilityName();
	}

	public void setFacilityName(String facilityName) {
		searchObj.setFacilityName(facilityName);
	}

	public String getCompanyId() {
		return searchObj.getCompanyId();
	}

	public void setCompanyId(String companyId) {
		searchObj.setCompanyId(companyId);
	}

	public String getStatus() {
		return searchObj.getStatus();
	}

	public void setStatus(String status) {
		searchObj.setStatus(status);
	}

	public TableSorter getResultsWrapper() {
		return resultsWrapper;
	}

	public void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}

	public boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public String getAqsSiteId() {
		return searchObj.getAqsSiteId();
	}

	public String getLatLon() {
		return searchObj.getLatLon();
	}
	
	public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }
	
	public final void setPopupRedirectOutcome(String popupRedirectOutcome) {
        this.popupRedirectOutcome = popupRedirectOutcome;
    }
	
	
}
