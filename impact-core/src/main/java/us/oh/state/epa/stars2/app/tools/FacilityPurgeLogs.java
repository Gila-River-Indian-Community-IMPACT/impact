package us.oh.state.epa.stars2.app.tools;


import java.util.List;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeLog;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;

@SuppressWarnings("serial")
public class FacilityPurgeLogs extends AppBase {
	public static final String PAGE_OUTCOME = "tools.facilityPurgeLog";
	
	private TableSorter resultsWrapper;
	private List<FacilityPurgeLog> purgeLogList;
	private boolean hasSearchResults;
	private FacilityService facilityService;	

    public TableSorter getResultsWrapper() {
		return resultsWrapper;
	}

	public void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}

    public List<FacilityPurgeLog> getPurgeLogList() {
		return purgeLogList;
	}

	public void setPurgeLogList(List<FacilityPurgeLog> purgeLogList) {
		this.purgeLogList = purgeLogList;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
    public final String refresh() {
        try {
        	resultsWrapper = new TableSorter();
        	purgeLogList = getFacilityService().retrieveFacilityPurgeLogs();
        	resultsWrapper.setWrappedData(purgeLogList);
        	if (purgeLogList != null && !purgeLogList.isEmpty()){
        		hasSearchResults = true;
        	} else{
        		hasSearchResults = false;
        		DisplayUtil.displayInfo("There are no facility purge logs.");
        	}
            
        } catch (DAOException re) {
            handleException(re);
        }
        return PAGE_OUTCOME;
    }


    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }
    
}
