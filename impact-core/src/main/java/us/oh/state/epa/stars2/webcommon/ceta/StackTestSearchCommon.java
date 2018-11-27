package us.oh.state.epa.stars2.webcommon.ceta;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;

import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;

@SuppressWarnings("serial")
public class StackTestSearchCommon extends AppBase {
	
	private String facilityId;
	private String facilityName;
	private String doLaaCd;
	private String countyCd;
	private String dateBy;
	private Timestamp beginDt;
	private Timestamp endDt;
    private boolean failedPolls;
	private Integer reviewer;
    private Integer stackTestId;
    private Integer fceId;
	private String stackTestMethodCd;
    private String emissionTestState;

	private StackTest[] stackTests;
    private TableSorter resultsWrapper;
	private boolean hasSearchResults;
	private boolean fromFacility;
	private String inspId;
	private String stckId;
	private String cmpId;
	
	private String facilityTypeCd;
    private String permitClassCd;
    
    /**
     * Fields for New Stack TEst Dialog
     */
    protected StackTest newStackTest;
    
    private String popupRedirectOutcome;
	 	 
	private StackTestService stackTestService;

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

    public StackTestSearchCommon() {
        super();
        resultsWrapper = new TableSorter();
        cacheViewIDs.add("/ceta/stackTestSearch.jsp");
        cacheViewIDs.add("/ceta/stackTestDetail.jsp");
        cacheViewIDs.add("/ceta/stackTestDetail2.jsp");
    }
    
    public void restoreCache() {
  }

  public void clearCache() {
		// if (resultsWrapper != null) {
		// resultsWrapper.clearWrappedData();
		// }
		//
		// stackTests = null;
		// hasSearchResults = false;
    }

	public final String reset() {
    	facilityId = null;
    	facilityName = null;
    	doLaaCd = null;
    	countyCd = null;
        hasSearchResults = false;
        dateBy = null;
    	beginDt = null;
    	endDt = null;
    	reviewer = null;
        failedPolls = false;
        fceId = null;
        stackTestId = null;
        stackTestMethodCd = null;
        emissionTestState = null;
        resultsWrapper = new TableSorter();
        resultsWrapper.setWrappedData(stackTests);
        inspId = null;
        stckId = null;
        cmpId = null;
        facilityTypeCd = null;
        permitClassCd = null;
        popupRedirectOutcome = null;
    	return SUCCESS;
    }
    
	public final String search() {
		try {
            hasSearchResults = false;
            
            if(beginDt != null && endDt != null && beginDt.after(this.endDt)) {
                DisplayUtil.displayError("Test Begin Date must not be before Test End Date");
            }
            
            stackTests = getStackTestService().retrieveStackTestsBySearch(facilityId,
                    stackTestId, fceId, facilityName, 
                     doLaaCd, countyCd, permitClassCd, facilityTypeCd,
                     dateBy, beginDt, endDt, failedPolls, reviewer,
                    stackTestMethodCd, emissionTestState, inspId, stckId, cmpId);
            resultsWrapper.setWrappedData(stackTests);
            DisplayUtil.displayHitLimit(stackTests.length);
            if (stackTests.length == 0) {
                if (fromFacility) {
                    DisplayUtil.displayInfo("There are no Stack Tests for this facility.");
                } else {
                    DisplayUtil.displayInfo("There are no Stack Tests that match the search criteria.");
                }
            } else {
                hasSearchResults = true;
            }
            } catch (RemoteException e) {
                handleException(e);
            }
		return null;
	}
	
	public final String getFacilityId() {
		return facilityId;
	}
	
	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	
	public final String getFacilityName() {
		return facilityName;
	}

	public final void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final String getCountyCd() {
		return countyCd;
	}

	public final void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}
	
	public final String getDateBy() {
		return dateBy;
	}

	public final void setDateBy(String dateBy) {
		if (dateBy == null || (dateBy != null && dateBy.trim().length() == 0)) {
			beginDt = null;
			endDt = null;
			dateBy = null;
		}
		this.dateBy = dateBy;
	}

	public final Timestamp getBeginDt() {
		return beginDt;
	}

	public final void setBeginDt(Timestamp beginDt) {
	    this.beginDt = beginDt;
	}

	public final Timestamp getEndDt() {
		return endDt;
	}

	public final void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
	}
	
	public final Integer getReviewer() {
		return reviewer;
	}

	public final void setReviewer(Integer reviewer) {
		this.reviewer = reviewer;
	}

	public final String getStackTestMethodCd() {
		return stackTestMethodCd;
	}

	public final void setStackTestMethodCd(String stackTestMethodCd) {
		this.stackTestMethodCd = stackTestMethodCd;
	}

	public final StackTest[] getStackTests() {
		return stackTests;
	}

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}
	
    public final boolean isFromFacility() {
		return fromFacility;
	}

	public final void setFromFacility(boolean fromFacility) {
		this.fromFacility = fromFacility;
	}

    public Integer getFceId() {
        return fceId;
    }

    public void setFceId(Integer fceId) {
        this.fceId = fceId;
    }

    public Integer getStackTestId() {
        return stackTestId;
    }

    public void setStackTestId(Integer stackTestId) {
        this.stackTestId = stackTestId;
    }

    public String getEmissionTestState() {
        return emissionTestState;
    }

    public void setEmissionTestState(String emissionTestState) {
        this.emissionTestState = emissionTestState;
    }

    public TableSorter getResultsWrapper() {
        return resultsWrapper;
    }

    public boolean isFailedPolls() {
        return failedPolls;
    }

    public void setFailedPolls(boolean failedPolls) {
        this.failedPolls = failedPolls;
    }

	public String getInspId() {
		return inspId;
	}

	public void setInspId(String inspId) {
		this.inspId = inspId;
	}
	
	public String getStckId() {
		return stckId;
	}

	public void setStckId(String stckId) {
		this.stckId = stckId;
	}
	
	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}
	
	public final StackTest getNewStackTest() {
        return newStackTest;
    }

    public final void setNewStackTest(StackTest newStackTest) {
        this.newStackTest = newStackTest;
    }
    
    public void resetNewStackTest() {
        newStackTest = null;
    }
    
    public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }
}