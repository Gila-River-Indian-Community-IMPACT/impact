package us.wy.state.deq.impact.webcommon.monitoring;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;

@SuppressWarnings("serial")
public class MonitorGroupSearch extends AppBase {

	private boolean hasSearchResults;
	private MonitorGroup searchObj = new MonitorGroup();
	
	private MonitorGroup[] monitorGroups;
	private TableSorter resultsWrapper = new TableSorter();
	private TableSorter facResultsWrapper = new TableSorter();
	
	public static final String SEARCH_OUTCOME = "monitoring.monitorGroupSearch";
	
	private MonitoringService monitoringService;
	
	private InfrastructureDefs infraDefs;
	
	private String popupRedirectOutcome;


	public final String submitSearch() {
		monitorGroups = null;
		hasSearchResults = false;
		
		try {
			resultsWrapper = new TableSorter();
			facResultsWrapper = new TableSorter();
			
			// if the user is a AQD monitor report uploader, then don't include
			// facility owned monitor groups in the search results
			boolean excludeFacilityOwned = false;
			InfrastructureDefs infraDefs = (InfrastructureDefs)FacesUtil.getManagedBean("infraDefs");
			excludeFacilityOwned = infraDefs.isAmbientMonitorReportUploader();
			if (isPublicApp()){
				excludeFacilityOwned=true;
			}
			monitorGroups = getMonitoringService().searchMonitorGroups(
					searchObj, excludeFacilityOwned);
			resultsWrapper.setWrappedData(monitorGroups);
			facResultsWrapper.setWrappedData(monitorGroups);
			if (monitorGroups.length == 0) {
				DisplayUtil
				.displayInfo("Cannot find any Monitor Groups for this search");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("Search Failed");
			logger.error(re.getMessage(), re);
		}
		return SUCCESS;
	}
		
	public boolean isHasSearchResults() {
		return hasSearchResults;
	}


	public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}


	public MonitorGroup[] getMonitorGroups() {
		return monitorGroups;
	}


	public void setMonitorGroups(MonitorGroup[] monitorGroups) {
		this.monitorGroups = monitorGroups;
	}


	public TableSorter getResultsWrapper() {
		return resultsWrapper;
	}


	public void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}


	public MonitoringService getMonitoringService() {
		return monitoringService;
	}


	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}


	public InfrastructureDefs getInfraDefs() {
		return infraDefs;
	}


	public void setInfraDefs(InfrastructureDefs infraDefs) {
		this.infraDefs = infraDefs;
	}

	public final String reset() {
		searchObj = new MonitorGroup();
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
	
	public Integer getMonitorReviewerId() {
		return searchObj.getMonitorReviewerId();
	}
	
	public void setMonitorReviewerId(Integer monitorReviewerId) {
		searchObj.setMonitorReviewerId(monitorReviewerId);
	}
    
}
