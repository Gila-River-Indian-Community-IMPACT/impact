package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import us.oh.state.epa.stars2.bo.FullComplianceEvalService;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.YearsForFCEs;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.def.FCEInspectionReportStateDef;

public class FceSearch extends AppBase {
	
	private static final long serialVersionUID = 4780892209479085609L;
	
	private String facilityId;
	private String facilityName;
	private String doLaaCd;
	private String countyCd;
	private Integer reviewer;
	private Integer staffAssigned;
	private String beginFedYearSched;
	private String endFedYearSched;
	private String beginFedYearComp;
	private String endFedYearComp;
	private Integer fceId;
	private String completed;
	private String usEpaCommitted;
	private String portable; 
	private String inspId;
	private String cmpId;
	private String facilityTypeCd;
    private String permitClassCd;
    private List<String> inspectionReportStateCds = new ArrayList<String>();

	private FullComplianceEval[] fceList;
	private boolean hasSearchResults;
	private boolean fromFacility;
	
	private FullComplianceEvalService fullComplianceEvalService;

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}
	
	public FceSearch() {
		super();
		inspectionReportStateCds.add(FCEInspectionReportStateDef.INITIAL);
	    inspectionReportStateCds.add(FCEInspectionReportStateDef.PREPARE);
	    inspectionReportStateCds.add(FCEInspectionReportStateDef.COMPLETE);
	    inspectionReportStateCds.add(FCEInspectionReportStateDef.FINAL);
	}
	
	public final String reset() {
		inspectionReportStateCds.clear();
	    facilityId = null;
	    facilityName = null;
	    doLaaCd = null;
	    countyCd = null;
	    beginFedYearSched = null;
	    endFedYearSched = null;
	    beginFedYearComp = null;
	    endFedYearComp = null;
	    fceId = null;
	    inspectionReportStateCds.add(FCEInspectionReportStateDef.INITIAL);
	    inspectionReportStateCds.add(FCEInspectionReportStateDef.PREPARE);
	    inspectionReportStateCds.add(FCEInspectionReportStateDef.COMPLETE);
	    inspectionReportStateCds.add(FCEInspectionReportStateDef.FINAL);
	    usEpaCommitted = null;
	    hasSearchResults = false;
	    reviewer = null;
	    staffAssigned = null;
	    portable = null;
	    inspId = null;
	    cmpId = null;
	    return null;
	}

	public final String submitSearch() {
		return submitSearch(false);
	}
	
	/**
	 * Invoked from Stack Test search screen.
	 * @return
	 */
	public final String submitSearch(boolean includeAttachments) {
	    boolean ok = true;
	    // compute beginSched and endSched dates
	    Timestamp beginSched = null;
	    Timestamp endSched = null;
	    Timestamp beginComp = null;
	    Timestamp endComp = null;
        Calendar cal = Calendar.getInstance();
	    if(beginFedYearSched != null) {
            int y = Integer.parseInt(beginFedYearSched);
	        // Determine dates
	        // compute begin date
	        cal.set(Calendar.DAY_OF_MONTH, 1);
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
	        cal.set(Calendar.YEAR, y);
	        cal.set(Calendar.YEAR, y - 1);
	        cal.set(Calendar.MONTH, Calendar.OCTOBER);
	        beginSched = new Timestamp(cal.getTimeInMillis());
	        beginSched.setNanos(0);
        }

        if(endFedYearSched != null) {
            int y = Integer.parseInt(endFedYearSched);
	        // compute end date--make it just beyond so test for less
	        cal.set(Calendar.YEAR, y);
	        cal.set(Calendar.MONTH, Calendar.OCTOBER);
	        endSched = new Timestamp(cal.getTimeInMillis());
	    }

	    if(beginFedYearComp != null) {
            int y = Integer.parseInt(beginFedYearComp);
	        // Determine dates
	        // compute begin date
	        cal = Calendar.getInstance();
	        cal.set(Calendar.DAY_OF_MONTH, 1);
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
	        cal.set(Calendar.YEAR, y);
	        cal.set(Calendar.YEAR, y - 1);
	        cal.set(Calendar.MONTH, Calendar.OCTOBER);
	        beginComp = new Timestamp(cal.getTimeInMillis());
	        beginComp.setNanos(0);
        }
        if(endFedYearComp != null) {
            int y = Integer.parseInt(endFedYearComp);
	        // compute end date--make it just beyond so test for less
	        cal.set(Calendar.YEAR, y);
	        cal.set(Calendar.MONTH, Calendar.OCTOBER);
	        endComp = new Timestamp(cal.getTimeInMillis());
	    }
        if(beginSched != null && endSched != null) {
            if(beginSched.after(endSched)) {
                DisplayUtil.displayError("FFY Scheduled From is after To");
                ok = false;
            }
        }
        if(beginComp != null && endComp != null) {
            if(beginComp.after(endComp)) {
                DisplayUtil.displayError("FFY Completed From is after To");
                ok = false;
            }
        }
        if(ok) {
            try {
                FceSiteVisits fceSiteVisits = (FceSiteVisits)FacesUtil.getManagedBean("fceSiteVisits");
                fceSiteVisits.setFromFacility(false);
                hasSearchResults = false;
                // this should be a more general search
                fceList = getFullComplianceEvalService().retrieveFceBySearch(inspId, facilityId,
                        facilityName, countyCd, doLaaCd, permitClassCd, facilityTypeCd,
                        beginSched, endSched,
                        beginComp, endComp, staffAssigned, 
                        reviewer, usEpaCommitted, inspectionReportStateCds, portable, cmpId);
                if (includeAttachments) {
                	for (FullComplianceEval fce : fceList) {
                		List<FceAttachment> attachments = 
                				getFullComplianceEvalService().retrieveFceAttachments(fce.getId());
                		fce.setAttachments(attachments);
                	}
                }
            } catch (RemoteException e) {
                handleException(e);
                ok = false;
            }
        }
	    if(ok) {
            DisplayUtil.displayHitLimit(fceList.length);
            if (fceList.length == 0) {
            	if(isInternalApp()) {
            		if (fromFacility) {
            			DisplayUtil.displayInfo("There are no Inspections available for this facility.");
            		} else {
            			DisplayUtil.displayInfo("There are no Inspections that match the search criteria.");
            		}
            	}
            } else {
                hasSearchResults = true;
            }
        }
		return null;
	}

    public final DefSelectItems getYearsForFCEsDef() {
        return YearsForFCEs.getData().getItems();
    }

    public String getCountyCd() {
        return countyCd;
    }

    public void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public Integer getReviewer() {
        return reviewer;
    }

    public void setReviewer(Integer reviewer) {
        this.reviewer = reviewer;
    }

    public boolean isFromFacility() {
        return fromFacility;
    }

    public void setFromFacility(boolean fromFacility) {
		this.fromFacility = fromFacility;
	}

    public boolean isHasSearchResults() {
        return hasSearchResults;
    }
    
    public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

    public Integer getStaffAssigned() {
        return staffAssigned;
    }

    public void setStaffAssigned(Integer staffAssigned) {
        this.staffAssigned = staffAssigned;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public FullComplianceEval[] getFceList() {
        return fceList;
    }

    public Integer getFceId() {
        return fceId;
    }

    public void setFceId(Integer fceId) {
        this.fceId = fceId;
    }

    public String getUsEpaCommitted() {
        return usEpaCommitted;
    }

    public void setUsEpaCommitted(String usEpaCommitted) {
        this.usEpaCommitted = usEpaCommitted;
    }

    public String getBeginFedYearComp() {
        return beginFedYearComp;
    }

    public void setBeginFedYearComp(String beginFedYearComp) {
        this.beginFedYearComp = beginFedYearComp;
    }

    public String getBeginFedYearSched() {
        return beginFedYearSched;
    }

    public void setBeginFedYearSched(String beginFedYearSched) {
        this.beginFedYearSched = beginFedYearSched;
    }

    public String getEndFedYearComp() {
        return endFedYearComp;
    }

    public void setEndFedYearComp(String endFedYearComp) {
        this.endFedYearComp = endFedYearComp;
    }

    public String getEndFedYearSched() {
        return endFedYearSched;
    }

    public void setEndFedYearSched(String endFedYearSched) {
        this.endFedYearSched = endFedYearSched;
    }
    
	public String getPortable() {
		return portable;
	}

	public void setPortable(String portable) {
		this.portable = portable;
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
	
	public final List<String> getInspectionReportStateCds() {
		return inspectionReportStateCds;
	}
	
	public final void setInspectionReportStateCds(List<String> inspectionReportStateCds) {
		this.inspectionReportStateCds = inspectionReportStateCds;
		if (this.inspectionReportStateCds == null) {
			this.inspectionReportStateCds = new ArrayList<String>();
		}
	}
	
	public final DefSelectItems getInspectionReportStatusDefs() {
		return FCEInspectionReportStateDef.getData().getItems();
	}
	
}
