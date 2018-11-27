package us.oh.state.epa.stars2.webcommon.ceta;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class StackTestsCommon extends ValidationBase {
    protected Facility facility;
    protected String facilityIdForStackTestPopup;
    protected Facility facilityForStackTestPopup;
	private boolean hasSearchResults;
    protected String popupRedirectOutcome = null;
    protected StackTest[] stackTestList;
    
    protected Timestamp visitDate;  // to get stack tests for this date
	private boolean fromFacility = false;
	private FacilityService facilityService;
	private FullComplianceEvalService fullComplianceEvalService;
	private StackTestService stackTestService;

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public StackTestsCommon() {
        super();
    }
    
    
    
    public final String reset() {
    	popupRedirectOutcome = null;
    	hasSearchResults = false;
    	return SUCCESS;
    }
    
    public List<Evaluator> getWitnesses() {
        if(stackTestList != null && stackTestList.length > 0) {
            return stackTestList[0].getWitnesses();
        }
        ArrayList<Evaluator> eList = new ArrayList<Evaluator>();
        return eList;
    }
   
    public final StackTest[] getStackTestList() {
    	return stackTestList;
    }
    
	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public final void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
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

    public Timestamp getVisitDate() {
        return visitDate;
    }
    
    public String getVisitDateStr() {
        return CetaBaseDB.getDateStr(visitDate);
    }

    public void setVisitDate(Timestamp visitDate) {
        this.visitDate = visitDate;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getFacilityIdForStackTestPopup() {
        return facilityIdForStackTestPopup;
    }

    public void setFacilityIdForStackTestPopup(String facilityIdForStackTestPopup) {
        this.facilityIdForStackTestPopup = facilityIdForStackTestPopup;
    }

    public Facility getFacilityForStackTestPopup() {
        return facilityForStackTestPopup;
    }

	public boolean isFromFacility() {
		return fromFacility;
	}

	public void setFromFacility(boolean fromFacility) {
		this.fromFacility = fromFacility;
	}
}