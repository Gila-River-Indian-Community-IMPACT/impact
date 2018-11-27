package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import javax.faces.model.SelectItem;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

@SuppressWarnings("serial")
public class SiteVisitSearch extends AppBase {
	private String facilityId;
	private String facilityName;
	private String doLaaCd;
	private String countyCd;
	private Timestamp beginVisitDt;
	private Timestamp endVisitDt;
	private String visitTypeCd;
	private Integer evaluator;
	private String announced;
    private Integer fceId;
    private String inspId;
	private Integer siteVisitId;
    private boolean searchPerformed = false;
	private SiteVisit[] siteVisits;
	private boolean hasSearchResults;
	private boolean fromFacility;
	private SiteVisitSearch backup;
	private String cmpId;
	private String siteId;
	protected String complianceIssued;
	private String facilityTypeCd;
    private String permitClassCd;
	
	private FullComplianceEvalService fullComplianceEvalService;

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}
	
	 public SiteVisitSearch()
	    {
	    	super();
	    }

	public final String reset() {
    	facilityId = null;
    	facilityName = null;
    	doLaaCd = null;
    	countyCd = null;
        hasSearchResults = false;
    	beginVisitDt = null;
    	endVisitDt = null;
        visitTypeCd = null;
    	evaluator = null;
        announced = null;
        fceId = null;
        inspId = null;
        cmpId = null;
        siteId = null;
        complianceIssued = null;
        permitClassCd = null;
        facilityTypeCd = null;
        return SUCCESS;
    }
	
	/**
	 * Invoked from SiteVisit search screen.
	 * @return
	 */
	public final String search() {
        searchPerformed = true;
		try {
            FceSiteVisits fceSiteVisits = (FceSiteVisits)FacesUtil.getManagedBean("fceSiteVisits");
            fceSiteVisits.setFromFacility(false);
            hasSearchResults = false;
            if(endVisitDt != null) {
                if(beginVisitDt != null && beginVisitDt.after(this.endVisitDt)) {
                    DisplayUtil.displayError("To Visit Date must not be before From Visit Date");
                }
            }
       	 	siteVisits = getFullComplianceEvalService().retrieveVisitBySearch(siteId ,inspId, facilityId, 
                    beginVisitDt, endVisitDt, visitTypeCd,
                    announced, evaluator, facilityName,
       	 	    doLaaCd, countyCd, permitClassCd, facilityTypeCd, cmpId, complianceIssued);
            DisplayUtil.displayHitLimit(siteVisits.length);
            if (siteVisits.length == 0) {
            	if (fromFacility) {
            		DisplayUtil.displayInfo("There are currently no site visits for this facility.");
            	} else {
            		DisplayUtil.displayInfo("There are no site visits that match the search criteria.");
            	}
            } else {
                hasSearchResults = true;
            }
		} catch (RemoteException e) {
			handleException(e);
		}
		return "siteVisitSearch";
	}
    
    // called after site visits may have been updated.
    public final void silentSearch() {
        if(!searchPerformed) return;
        hasSearchResults = false;
        Timestamp endVisit = endVisitDt;
        if(endVisit != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(endVisit.getTime());
            c.add(Calendar.DATE, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            endVisit = new Timestamp(c.getTimeInMillis());
            endVisit.setNanos(0);
            if(beginVisitDt != null && beginVisitDt.after(this.endVisitDt)) {
                DisplayUtil.displayError("To Visit Date must not be before From Visit Date");
            }
        } 
        try {
        siteVisits = getFullComplianceEvalService().retrieveVisitBySearch(siteId, inspId, facilityId, 
                beginVisitDt, endVisit, visitTypeCd,
                announced, evaluator, facilityName,
                doLaaCd, countyCd, permitClassCd, facilityTypeCd, cmpId, complianceIssued);
        } catch(RemoteException e) {
            handleException(e);
        }
        if (siteVisits.length != 0) {
            hasSearchResults = true;
        }
    }

    public final String refresh() {
        String ret = "siteVisit.search";
        if (backup != null) {
            copy(backup);
            backup = null;
            search();
        }
        return ret;
    }
    
    public final void copy(SiteVisitSearch es) {
    	setSiteVisitId(es.siteVisitId);
    	setFacilityId(es.facilityId);
    }
	
	/**
	 * This is the method called by the third-level menu of the facility inventory to
	 * display the enforcement table.
	 * @return
	 */
	public final String initSiteVisits() {
		FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
        reset();
        facilityId = fp.getFacilityId();
        backup = new SiteVisitSearch();
        backup.copy(this);
		if (facilityId != null && facilityId.trim().length() > 0) {
			search();
		}
        return "facilities.profile.siteVisits";
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

	public final Timestamp getBeginVisitDt() {
		return beginVisitDt;
	}

	public final void setBeginVisitDt(Timestamp beginVisitDt) {
		this.beginVisitDt = beginVisitDt;
	}

	public final Timestamp getEndVisitDt() {
		return endVisitDt;
	}

	public final void setEndVisitDt(Timestamp endVisitDt) {
        this.endVisitDt = endVisitDt; 
	}

	public final String getVisitTypeCd() {
		return visitTypeCd;
	}

	public final void setVisitTypeCd(String visitTypeCd) {
		this.visitTypeCd = visitTypeCd;
	}

	public final Integer getEvaluator() {
		return evaluator;
	}

	public final void setEvaluator(Integer evaluator) {
		this.evaluator = evaluator;
	}

	public final String getAnnounced() {
		return announced;
	}

	public final void setAnnounced(String announced) {
		this.announced = announced;
	}

	public final Integer getSiteVisitId() {
		return siteVisitId;
	}

	public final void setSiteVisitId(Integer siteVisitId) {
		this.siteVisitId = siteVisitId;
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
	
    public LinkedHashMap<String, Timestamp> getScheduleChoices() {
        LinkedHashMap<String, Timestamp> scheds = new LinkedHashMap<String, Timestamp>();
        Calendar cal = Calendar.getInstance();
        Timestamp ref = new Timestamp(cal.getTimeInMillis());
        cal.setTime(new Date(ref.getTime()));
        for(int i=0; i<20; i++) {
            scheds.put(CetaBaseDB.getScheduled(ref), ref);
            cal.add(Calendar.MONTH, 3);
            ref = new Timestamp(cal.getTimeInMillis());
        }
        return scheds;
    }

    public Integer getFceId() {
        return fceId;
    }

    public void setFceId(Integer fceId) {
        this.fceId = fceId;
    }

    public SiteVisit[] getSiteVisits() {
        return siteVisits;
    }

    public String getInspId() {
		return inspId;
	}

	public void setInspId(String inspId) {
		this.inspId = inspId;
	}

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	
	public String getComplianceIssued() {
		return complianceIssued;
	}

	public void setComplianceIssued(String complianceIssued) {
		this.complianceIssued = complianceIssued;
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
}
