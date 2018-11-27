package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.HashMap;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

@SuppressWarnings("serial")
public class SubmissionLogSearch extends AppBase {
	private SubmissionLog searchSubmissionLog = new SubmissionLog();
	private SubmissionLog[] submissionLogs;
	private Timestamp beginDate;
	private Timestamp endDate;
	private boolean hasSearchResults;
	private String selFacilityId;
	private HashMap<String, String> submissionTypes;
	
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public SubmissionLogSearch() {
		super();
		cacheViewIDs.add("/tools/submissionLog.jsp");
	}

	public final String submitSearch() {
		submissionLogs = null;
		hasSearchResults = false;
		/*        
		 if (searchFieldAuditLog.getFacilityId() == null && categoryCd == null) {
		 DisplayUtil.displayError("Facility ID or Category is required");
		 return FAIL;
		 }
		 */
		try {
			submissionLogs = getFacilityService().searchSubmissionLog(searchSubmissionLog, beginDate, endDate);
			if (submissionLogs.length == 0) {
				DisplayUtil.displayInfo("Cannot find any submission logs for this search");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
        	handleException(re);
            DisplayUtil.displayError("Search failed");
        }
		return SUCCESS;
	}

	public final String submitProfile() {
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
			DisplayUtil
					.displayError("Accessing Current facility inventory failed");
			logger.error(re.getMessage(), re);
		}
		return ret;
	}

	public final String reset() {
		searchSubmissionLog = new SubmissionLog();
		beginDate = null;
		endDate = null;
		submissionLogs = null;
		hasSearchResults = false;
		return SUCCESS;
	}

	public boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public SubmissionLog getSearchSubmissionLog() {
		return searchSubmissionLog;
	}

	public void setSearchSubmissionLog(SubmissionLog searchSubmissionLog) {
		this.searchSubmissionLog = searchSubmissionLog;
	}

	public String getSelFacilityId() {
		return selFacilityId;
	}

	public void setSelFacilityId(String selFacilityId) {
		this.selFacilityId = selFacilityId;
	}

	public SubmissionLog[] getSubmissionLogs() {
		return submissionLogs;
	}

	public void setSubmissionLogs(SubmissionLog[] submissionLogs) {
		this.submissionLogs = submissionLogs;
	}

	public HashMap<String, String> getSubmissionTypes() {
		Task t = new Task();
		HashMap<TaskType, String> taskTypeDescs = t.getTaskTypeDescs(); 
		if(submissionTypes == null){
			submissionTypes = new HashMap<String, String>();
			for(String s : taskTypeDescs.values()){
				submissionTypes.put(s, s);
			}
		}
		return submissionTypes;
	}

	public String refreshSearchSubmissionLog() {
    	if (hasSearchResults) {
    		submitSearch();
    	}
    	
    	return "tools.submissionLog";
    }
    
    public void restoreCache() {
    }

    public void clearCache() {
		// submissionLogs = null;
		// hasSearchResults = false;
	}

	public Timestamp getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Timestamp beginDate) {
		this.beginDate = beginDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}
}
