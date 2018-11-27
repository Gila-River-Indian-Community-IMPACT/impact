package us.oh.state.epa.stars2.app.reports;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage.Severity;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitExpirationDetails;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

/**
 * @author yehp
 * 
 */
@SuppressWarnings("serial")
public class PermitExpirationReportSearch extends AppBase {
    private Timestamp fromDate;
    private Timestamp toDate;
    private boolean hideShutdownFacility;
    private boolean hideExemptPBR;
    private boolean hideEUPermitStatusTRS;
    private boolean hideEUExemptionDmPe;
    private boolean hideEUShutdownInvalid;
    private boolean hidePtoPtioEuActivePBR;

    private PermitExpirationDetails[] details;

    private String doLaaCd;
    private String type;
    
    private boolean hasSearchResults;
    private boolean hasSummaryResults;
    //private boolean hasSptoResults;
    private boolean hasPtioResults;
    private boolean hasTvResults;
    
    private boolean hasTotalResults;
    private boolean hasGeneralResults;
    private boolean hasDolaaResults;
    private boolean hasReasonResults;
    private boolean hasFinalResults;
    
    private String permitType;
    private String issuanceType;
    
    // progress bar variables
    private Thread searchThread;
    private DisplayUtil displayUtilBean;
    private FacilityProfile fProf;
    private boolean searchStarted;
	private boolean showProgressBar;
	
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public PermitExpirationReportSearch() {
        super();
        reset();
    }

    /**
     * @return
     */
    public final boolean isHasSearchResults() {
        boolean ret = false;
        
        if (hasSearchResults) {
            ret = true;
        }

        return ret;
    }

    public final String getDrillDown() {

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String sDate = df.format(fromDate);
        String eDate = df.format(toDate);

        return "DO/LAA: " + doLaaCd + ", Dates: From " + sDate + " To " + eDate;
    }
    
    public final String submitDrillDown() {
   
        DoLaaPieImage dolaaSummary = (DoLaaPieImage)FacesUtil.getManagedBean("dolaaSummary");
        dolaaSummary.setType("EXP."+getType());
        dolaaSummary.setStartDt(getFromDate());
        dolaaSummary.setEndDt(getToDate());
        dolaaSummary.setDoLaaName(getDoLaaCd());
        dolaaSummary.setClickURL("/mgmt/permitExpirationReport.jsf");
        
        GeneralPieImage generalSummary = (GeneralPieImage)FacesUtil.getManagedBean("generalSummary");
        generalSummary.setType(getType() +".EXP");
        generalSummary.setStartDt(getFromDate());
        generalSummary.setEndDt(getToDate());
        generalSummary.setDoLaaName(getDoLaaCd());
        generalSummary.setClickURL("/mgmt/permitExpirationReport.jsf");
        
        ReasonPieImage reasonSummary = (ReasonPieImage)FacesUtil.getManagedBean("reasonSummary");
        reasonSummary.setType(getType()+".EXP");
        reasonSummary.setStartDt(getFromDate());
        reasonSummary.setEndDt(getToDate());
        reasonSummary.setDoLaaName(getDoLaaCd());
        reasonSummary.setClickURL("/mgmt/permitExpirationReport.jsf");
        
        DirectFinalPieImage finalSummary = (DirectFinalPieImage)FacesUtil.getManagedBean("finalSummary");
        finalSummary.setType(getType()+".EXP");
        finalSummary.setStartDt(getFromDate());
        finalSummary.setEndDt(getToDate());
        finalSummary.setDoLaaName(getDoLaaCd());
        finalSummary.setClickURL("/mgmt/permitExpirationReport.jsf");
        
        TypePieImage typeSummary = (TypePieImage)FacesUtil.getManagedBean("typeSummary");
        typeSummary.setType(getType()+".EXP");
        typeSummary.setStartDt(getFromDate());
        typeSummary.setEndDt(getToDate());
        typeSummary.setDoLaaName(getDoLaaCd());
        typeSummary.setClickURL("/mgmt/permitExpirationReport.jsf");
        
        PermitExpirationDetailReport detailReport = (PermitExpirationDetailReport)FacesUtil.getManagedBean("permitExpirationDetailReport");
        detailReport.setType(type);
        detailReport.setDoLaaName(getDoLaaCd());
        detailReport.setStartDt(getFromDate());
        detailReport.setEndDt(getToDate());
        detailReport.setIssuanceType(getIssuanceType());
        detailReport.setHideShutdownFacility(hideShutdownFacility);
        detailReport.setHideExemptPBR(hideExemptPBR);
        detailReport.setHideEUPermitStatusTRS(hideEUPermitStatusTRS);
        detailReport.setHideEUExemptionDmPe(hideEUExemptionDmPe);
        detailReport.setHideEUShutdownInvalid(hideEUShutdownInvalid);
        detailReport.setHidePtoPtioEuActivePBR(hidePtoPtioEuActivePBR);
        detailReport.submit();
        
        //if ("SPTO".equals(permitType) 
        //        && detailReport.getSptoDetails() != null 
        //        && detailReport.getSptoDetails().length != 0) 
        //{
        //    hasSptoResults = true;
        //    hasSummaryResults = true;
        //} 
        //else 
        if ("NSR".equals(permitType) 
                && detailReport.getPtioDetails() != null 
                && detailReport.getPtioDetails().length != 0) 
        {
            hasPtioResults = true;
            hasSummaryResults = true;
        } 
        else if ("TVPTO".equals(permitType) 
                && detailReport.getTvDetails() != null 
                && detailReport.getTvDetails().length != 0) 
        {
            hasTvResults = true;
            hasSummaryResults = true;
        }
            
        return SUCCESS;
    }
    
    public boolean getShowSummaryTable() {
        return hasSearchResults && 
        		//!hasSptoResults && 
        		!hasPtioResults && !hasTvResults;
    }
    
    /**
     * @return
     */
    public final String submit() {
    	List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
    	// Mantis 2818 - from date is always now
    	fromDate = new Timestamp(System.currentTimeMillis());
    	if (toDate == null) {
    		validationMessages.add(new ValidationMessage("To Date", 
    				"To Date is required",
    				Severity.ERROR, "toDate"));
    	}

    	if (validationMessages.size() == 0) {
    		if (!searchStarted && getSearchThread() == null) {
    			String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
    				+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
    				+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
    				+ "<META HTTP-EQUIV=\"Cache-Control\" "
    				+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
    			fProf = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
    			fProf.setRefreshStr(refresh);

    			initSearchThread();
    			getSearchThread().setDaemon(true);
    			setValue(0);
    			setMaximum(1000);
    			showProgressBar = true;
    			getSearchThread().start();
    			if (!hasSearchResults) {
    				DisplayUtil.displayInfo("Processing report. This may take several moments. "
    						+ "You may cancel the operation by pressing the \"Reset\" button.");
    			}
    			else {
    				fProf.setRefreshStr(" ");
    			}
    		} else {
    			DisplayUtil.displayInfo("Still processing. Please wait."
    					+ "You may cancel the operation by pressing the \"Reset\" button.");

    		}
    	} else {
    		displayValidationMessages("", validationMessages.toArray(new ValidationMessage[0]));
    	}

    	return SUCCESS;
    }
    

	private void initSearchThread() {
		searchThread = new Thread("PER Overdue Report Thread") {
            
            public void run() {
                try {
			        hasSearchResults = false;
			        details = getReportService().retrievePermitExpirationDetails(fromDate, toDate, hideShutdownFacility, 
			        		hideExemptPBR, hideEUPermitStatusTRS, hideEUExemptionDmPe, hideEUShutdownInvalid, 
			        		hidePtoPtioEuActivePBR);
		        	
    				setValue(1000);
			        hasSearchResults = true;
			        showProgressBar = false;
                    fProf.setRefreshStr(" ");
                    searchThread = null;
                } catch (Exception e) {
	                    String error = "Search failed: System Error. ";
	                    logger.error(error, e);
	                    if (e.getMessage() != null && e.getMessage().length() > 0) {
	                        error += e.getMessage();
	                    }
	                    displayUtilBean.addMessageToQueue(error, DisplayUtil.ERROR, null);
	                    searchStarted = false;
	                    hasSearchResults = false;
	                    fProf.setRefreshStr(" ");
	                }
	            }
	        };
		
	}
    
    public final String selectAll() {
        hideShutdownFacility = true;
        hideExemptPBR = true;
        hideEUPermitStatusTRS = true;
        hideEUExemptionDmPe = true;
        hideEUShutdownInvalid = true;
        hidePtoPtioEuActivePBR = true;
    	return null;
    }
    
    public final String selectNone() {
        hideShutdownFacility = false;
        hideExemptPBR = false;
        hideEUPermitStatusTRS = false;
        hideEUExemptionDmPe = false;
        hideEUShutdownInvalid = false;
        hidePtoPtioEuActivePBR = false;
    	return null;
    }

    /**
     * @return
     */
    public final String reset() {
        details = null;
        fromDate = null;
        toDate = null;
        hasSearchResults = false;
        hasSummaryResults = false;
        //hasSptoResults = false;
        hasPtioResults = false;
        hasTvResults = false;

        hasTotalResults = false;
        hasDolaaResults = false;
        hasGeneralResults = false;
        hasReasonResults = false;
        hasFinalResults = false;
        
        hideShutdownFacility = false;
        hideExemptPBR = false;
        hideEUPermitStatusTRS = false;
        hideEUExemptionDmPe = false;
        hideEUShutdownInvalid = false;
        hidePtoPtioEuActivePBR = false;
        
        // turn off progress bar
        searchThread = null;
		showProgressBar = false;
		if (fProf != null) {
			fProf.setRefreshStr(" ");
		}

		return SUCCESS;
    }

    /**
     * @return
     */
    public final String refresh() {
        hasSummaryResults = false;
        hasTotalResults = false;
        hasDolaaResults = false;
        hasGeneralResults = false;
        hasReasonResults = false;
        hasFinalResults = false;
        //hasSptoResults = false;
        hasPtioResults = false;
        hasTvResults = false;
        return null;
    }

    /**
     * @return
     */
    public final Timestamp getToDate() {
        return toDate;
    }

    /**
     * @param toDate
     */
    public final void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

    /**
     * @return
     */
    public final Timestamp getFromDate() {
        return fromDate;
    }

    public final boolean isHideShutdownFacility() {
		return hideShutdownFacility;
	}

	public final void setHideShutdownFacility(boolean hideShutdownFacility) {
		this.hideShutdownFacility = hideShutdownFacility;
	}

	public final boolean isHideExemptPBR() {
		return hideExemptPBR;
	}

	public final void setHideExemptPBR(boolean hideExemptPBR) {
		this.hideExemptPBR = hideExemptPBR;
	}

	public final boolean isHideEUPermitStatusTRS() {
		return hideEUPermitStatusTRS;
	}

	public final void setHideEUPermitStatusTRS(boolean hideEUPermitStatusTRS) {
		this.hideEUPermitStatusTRS = hideEUPermitStatusTRS;
	}

	public final boolean isHideEUExemptionDmPe() {
		return hideEUExemptionDmPe;
	}

	public final void setHideEUExemptionDmPe(boolean hideEUExemptionDmPe) {
		this.hideEUExemptionDmPe = hideEUExemptionDmPe;
	}

	public final boolean isHideEUShutdownInvalid() {
		return hideEUShutdownInvalid;
	}

	public final void setHideEUShutdownInvalid(boolean hideEUShutdownInvalid) {
		this.hideEUShutdownInvalid = hideEUShutdownInvalid;
	}

	public final boolean isHidePtoPtioEuActivePBR() {
		return hidePtoPtioEuActivePBR;
	}

	public final void setHidePtoPtioEuActivePBR(boolean hidePtoPtioEuActivePBR) {
		this.hidePtoPtioEuActivePBR = hidePtoPtioEuActivePBR;
	}

	/**
     * @return
     */
    public final PermitExpirationDetails[] getDetails() {
        return details;
    }

    /**
     * @param details
     */
    public final void setDetails(PermitExpirationDetails[] details) {
        this.details = details;
    }

    /**
     * @param hasSearchResults
     */
    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    /**
     * @return
     */
    public final boolean isHasSummaryResults() {
        return hasSummaryResults;
    }

    /**
     * @param hasSummaryResults
     */
    public final void setHasSummaryResults(boolean hasSummaryResults) {
        this.hasSummaryResults = hasSummaryResults;
    }

    /**
     * @return
     */
    public final boolean isHasTotalResults() {
        return hasTotalResults && hasSearchResults;
    }

    /**
     * @return
     */
    public final boolean isHasGeneralResults() {
        return hasGeneralResults && hasSearchResults;
    }

    /**
     * @param hasGeneralResults
     */
    public final void setHasGeneralResults(boolean hasGeneralResults) {
        this.hasGeneralResults = hasGeneralResults;
    }

    /**
     * @return
     */
    public final boolean isHasDolaaResults() {
        return hasDolaaResults && hasSearchResults;
    }

    /**
     * @param hasDolaaResults
     */
    public final void setHasDolaaResults(boolean hasDolaaResults) {
        this.hasDolaaResults = hasDolaaResults;
    }

    /**
     * @return
     */
    public final boolean isHasReasonResults() {
        return hasReasonResults && hasSearchResults;
    }

    /**
     * @param hasReasonResults
     */
    public final void setHasReasonResults(boolean hasReasonResults) {
        this.hasReasonResults = hasReasonResults;
    }

    /**
     * @return
     */
    public final boolean isHasFinalResults() {
        return hasFinalResults && hasSearchResults;
    }

    /**
     * @param hasFinalResults
     */
    public final void setHasFinalResults(boolean hasFinalResults) {
        this.hasFinalResults = hasFinalResults;
    }

    /**
     * @param hasTotalResults the hasTotalResults to set
     */
    public final void setHasTotalResults(boolean hasTotalResults) {
        this.hasTotalResults = hasTotalResults;
    }

    /**
     * @return the type
     */
    public final String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public final void setType(String type) {
        StringTokenizer st = new StringTokenizer(type, ".");

        permitType = st.nextToken();
        if (st.hasMoreTokens())
            issuanceType = st.nextToken();
        else
            issuanceType = null;

        this.type = type;
    }

    /**
     * @return the doLaaCd
     */
    public final String getDoLaaCd() {
        return doLaaCd;
    }

    /**
     * @param doLaaCd the doLaaCd to set
     */
    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    //public final boolean isHasSptoResults() {
	//	return hasSptoResults;
	//}

	//public final void setHasSptoResults(boolean hasSptoResults) {
	//	this.hasSptoResults = hasSptoResults;
	//}

	public final boolean isHasPtioResults() {
        return hasPtioResults && hasSearchResults;
    }

    public final void setHasPtioResults(boolean hasPtioResults) {
        this.hasPtioResults = hasPtioResults;
    }

    public final boolean isHasTvResults() {
        return hasTvResults && hasSearchResults;
    }

    public final void setHasTvResults(boolean hasTvResults) {
        this.hasTvResults = hasTvResults;
    }

    public final String getPermitType() {
        return permitType;
    }

    public final void setPermitType(String permitType) {
        this.permitType = permitType;
    }

    public final String getIssuanceType() {
        return issuanceType;
    }

    public final void setIssuanceType(String issuanceType) {
        this.issuanceType = issuanceType;
    }    
    

    // progress-bar methods
	
	private Thread getSearchThread() {
		return searchThread;
	}

	public final boolean isShowProgressBar() {
		return showProgressBar;
	}

	public final void setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
	}
	
    public synchronized long getValue() {
        long value = super.getValue() + 5;
        setValue(value);
        return value;
    }
}
