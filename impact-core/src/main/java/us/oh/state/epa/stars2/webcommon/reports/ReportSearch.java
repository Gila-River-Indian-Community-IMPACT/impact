package us.oh.state.epa.stars2.webcommon.reports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionTotal;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;


@SuppressWarnings("serial")
public class ReportSearch extends AppBase {
    private static final long REFRESH_INTERVAL = 10 * 60 * 1000; // 10
    private static final String STANDARD_LIST = "StandardList";
    private static final String INVOICE_TRACKING_LIST = "InvoiceTrackingList";
    // minutes
    private long refreshTime = 0;
    private EmissionsReportSearch searchObj = new EmissionsReportSearch();
    private transient Collection<SelectItem> feePickList = null;
    private String unit = EmissionUnitReportingDef.TONS; // units for min/maxEmissions
    private String minEmissions;
    private String maxEmissions;
    private EmissionsReportSearch[] reports = new EmissionsReportSearch[0];
    // put in separate wrappers to avoid jsp problem displaying table
    private boolean hasSearchResults = false;
    private String fromFacility = "false";
    private boolean fromThread = false; // called from a created thread
    private LinkedHashMap<String, String> reportPollutants;
    private String popupRedirectOutcome;
    private boolean ntvReport;  // is it a ntv report (or tv/smtv report)

    private EmissionsReportService emissionsReportService;
	private InfrastructureService infrastructureService;
	private FacilityService facilityService;
	
	
	 protected static Logger logger;
	 protected static FileWriter outStream;
	 private static String outFileName = "recomputeTvTotalLog.txt";
	 protected static FileWriter outStreamSQL;
	 private static String outFileSQLName = "recomputeTvTotalSQL.txt";
	 protected static EmissionsReportSearch[] emissReports;
	 protected static boolean viewOnly = true;  // TODO  change flag to change behavior
	 
	 private boolean standardList;	// search results page for standard view
	 private boolean invoiceTrackingList; // search results page for invoice tracking view

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}
    
    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}
	
	public void setFacilityService(
			FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
	public FacilityService getFacilityService() {
		return facilityService;
	}
	
    public ReportSearch() {
        super();
     
        cacheViewIDs.add("/reports/erSearch.jsp");
        cacheViewIDs.add("/reports/createReportData.jsp");        
        cacheViewIDs.add("/reports/createReport.jsp");
        cacheViewIDs.add("/reports/selectCompareReport.jsp");
        cacheViewIDs.add("/reports/reportDetail.jsp");
        cacheViewIDs.add("/facilities/reports.jsp");
        cacheViewIDs.add("/__ADFv__.jsp");
        
        standardList = true;
        invoiceTrackingList = false;
    }
    
    public Set<String> getRegulatoryRequirementCds() {
		return searchObj.getRegulatoryRequirementCds();
	}

	public String getContentTypeCd() {
		return searchObj.getContentTypeCd();
	}

	public void setContentTypeCd(String contentTypeCd) {
		searchObj.setContentTypeCd(contentTypeCd);
	}

	public String getRegulatoryRequirementCd() {
		return searchObj.getRegulatoryRequirementCd();
	}

	public void setRegulatoryRequirementCd(String regulatoryRequirementCd) {
		searchObj.setRegulatoryRequirementCd(regulatoryRequirementCd);
	}

	public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }

    public final String getFromFacility() {
        return fromFacility;
    }

    public final void setFromFacility(String fromFacility) {
        this.fromFacility = fromFacility;
    }

    public final Integer getReportId() {
        return searchObj.getEmissionsRptId();
    }

    public final Integer getFpId() {
        return searchObj.getFpId();
    }

    public final void setFpId(Integer fpId) {
        searchObj.setFpId(fpId);
    }

    public final String getFacilityId() {
        return searchObj.getFacilityId();
    }

    public final void setFacilityId(String facilityId) {
        searchObj.setFacilityId(facilityId);
    }

    public final String getFacilityName() {
        return searchObj.getFacilityName();
    }

    public final void setFacilityName(String facilityName) {
        searchObj.setFacilityName(facilityName);
    }

    public final String getCountyCd() {
        return searchObj.getCountyCd();
    }

    public final void setCountyCd(String countyCd) {
        searchObj.setCountyCd(countyCd);
    }

    public final String getDoLaaCd() {
        return searchObj.getDolaaCd();
    }

    public final void setDoLaaCd(String doLaaCd) {
        searchObj.setDolaaCd(doLaaCd);
    }

    public final List<SelectItem> getDoLaas() {
        return getDoLaas(getDoLaaCd());
    }
    
    public String getMaxEmissions() {
        return maxEmissions;
    }

    public void setMaxEmissions(String maxEmissions) {
        this.maxEmissions = maxEmissions;
    }

    public String getMinEmissions() {
        return minEmissions;
    }

    public void setMinEmissions(String minEmissions) {
        this.minEmissions = minEmissions;
    }
    
    public Timestamp getBeginDt() {
        return searchObj.getBeginDt();
    }

    public void setBeginDt(Timestamp beginDt) {
        if(searchObj.getEndDt() != null && beginDt != null
                && searchObj.getEndDt().before(beginDt)) {
            DisplayUtil.displayError("End Date must not be before Begin Date");
        } else {
            searchObj.setBeginDt(beginDt);
        }
    }

    public Timestamp getEndDt() {
        return searchObj.getEndDt();
    }

    public void setEndDt(Timestamp endDt) {
        if(endDt != null) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(endDt.getTime());
            c.add(Calendar.DATE, 1);
            Timestamp ts = new Timestamp(c.getTimeInMillis());
            if(searchObj.getBeginDt() != null && searchObj.getBeginDt().after(endDt)) {
                DisplayUtil.displayError("End Date must not be before Begin Date");
            } else {
                searchObj.setEndDt(ts);
            }
        }
        else {
            searchObj.setEndDt(null);
        }
    }

    public final String submitSearch() {
        // Do not use staging DB
        return submitSearch(false);
    }
    
    public final String submitSearch(boolean staging) {
        String rtn = null;
        try {
            rtn = internalSubmitSearch(staging);
            if(reports == null) {
                // null used for exception return but only when used internally
                reports = new EmissionsReportSearch[0];
            }
            if(hasSearchResults) {
                if (reports.length == 0 && fromFacility.equals("false")) {
                    DisplayUtil
                    .displayInfo("Cannot find any reports for this search");
                }
            }
        } catch (RemoteException re) {
            handleException(re);
            DisplayUtil.displayError("Search failed");
            logger.error(re.getMessage(), re);
        }
        return rtn;
    }
    
    
    
    public final String internalSubmitSearch(boolean staging) throws RemoteException {
        reports = null;  // free up space
        hasSearchResults = false;
        Float v = null;
        
        if ((!Utility.isNullOrEmpty(minEmissions) || !Utility.isNullOrEmpty(maxEmissions)) &&
        		Utility.isNullOrEmpty(getPollutantCd())) {
        	DisplayUtil
            .displayInfo("Pollutant must be selected if range value(s) is entered.");
        	return null;
        }
        
        if(minEmissions != null && !minEmissions.equals("" ) && unit != null) {
            v = new Float(EmissionUnitReportingDef.convert(
                    unit, new Double(minEmissions),
                    EmissionUnitReportingDef.TONS));
        }
        searchObj.setMinEmissions(v);

        v = null;
        if(maxEmissions != null && !maxEmissions.equals("") && unit != null) {
            v = new Float(EmissionUnitReportingDef.convert(
                    unit, new Double(maxEmissions),
                    EmissionUnitReportingDef.TONS));
        }
        searchObj.setMaxEmissions(v);
        searchObj.setStagingDBQuery(staging);

        try {
            searchObj.setUnlimitedResults(true);
            if(!fromThread) {
                searchObj.setUnlimitedResults(unlimitedResults());
            }            
            reports = getEmissionsReportService().searchEmissionsReports(searchObj, staging);
            if(!fromThread) {
                DisplayUtil.displayHitLimit(reports.length);
            }
            if (reports.length == 0) {
                if (fromFacility.equals("false") && !fromThread) {
                    DisplayUtil
                    .displayInfo("There are no Emissions Inventories that match the search criteria.");
                }
            } else {
                hasSearchResults = true;
            }
        } catch (RemoteException re) {
            // indicate exception gotten
            reports = null;
            if(!fromThread) {
                handleException(re);
                DisplayUtil.displayError("Search failed");
            } else {
                logger.error(re.getMessage(), re);
            }
        }

        return SUCCESS;
    }
    
    public String search() {
        if(hasSearchResults) {
            submitSearch();
        }
        return "ERSearch";
    }

    public final String reset() {
        searchObj = new EmissionsReportSearch();
        hasSearchResults = false;
        unit = EmissionUnitReportingDef.TONS;
        maxEmissions = null;
        minEmissions = null;
        setFeeId(null);
        standardList = true;
        invoiceTrackingList = false;
        return SUCCESS;
    }

    public final boolean getHasSearchResults() {
        return hasSearchResults;
    }

    public final EmissionsReportSearch[] getReports() {
        return reports;
    }

    public final Float getTotalEmissions() {
        return searchObj.getTotalEmissions();
    }

    public final EmissionsReportSearch getSearchObj() {
        return searchObj;
    }

    public final void setTotalEmissions(Float totalEmissions) {
        searchObj.setTotalEmissions(totalEmissions);
    }

    public final Integer getEmissions() {
        return searchObj.getEmissions();
    }

    public final void setEmissions(Integer emissions) {
        searchObj.setEmissions(emissions);
    }

    public final Integer getEmissionsRptId() {
        return searchObj.getEmissionsRptId();
    }

    public final void setEmissionsRptId(Integer emissionsRptId) {
        searchObj.setEmissionsRptId(emissionsRptId);
    }

    public final String getPollutantCd() {
        return searchObj.getPollutantCd();
    }

    public final void setPollutantCd(String pollutantCd) {
        searchObj.setPollutantCd(pollutantCd);
    }

    public final LinkedHashMap<String, String> getReportPollutants() {
        if (System.currentTimeMillis() > refreshTime) {
            refreshTime = System.currentTimeMillis() + REFRESH_INTERVAL;
            try {
                SimpleDef[] tempDefs = getInfrastructureService()
                        .retrieveReportPollutants();

                reportPollutants = new LinkedHashMap<String, String>();
                for (SimpleDef tempDef : tempDefs) {
                    reportPollutants.put(tempDef.getDescription(), tempDef.getCode());
                }
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }
        return reportPollutants;
    }

    public final String getReportingState() {
        return searchObj.getReportingState();
    }
    
    public final String getEisStatusCd() {
        return searchObj.getEisStatusCd();
    }
    
    public final void setEisStatusCd(String eisStatusCd) {
        searchObj.setEisStatusCd(eisStatusCd);
    }

    public final void setReportingState(String reportingState) {
        searchObj.setReportingState(reportingState);
    }

    public final Integer getYear() {
        return searchObj.getYear();
    }

    public final void setYear(Integer year) {
        searchObj.setYear(year);
    }

    public final void setPopupRedirectOutcome(String popupRedirectOutcome) {
        this.popupRedirectOutcome = popupRedirectOutcome;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Collection<SelectItem> getFeePickList() {
        return feePickList;
    }

    public Integer getFeeId() {
        return searchObj.getFeeId();
    }

    public void setFeeId(Integer feeId) {
        searchObj.setFeeId(feeId);
    }

    public void setSearchObj(EmissionsReportSearch searchObj) {
        this.searchObj = searchObj;
    }
    
    public void restoreCache() {
    }

    public void clearCache() {
        // Following two lines useful to determine when
        // a viewID has been missed that should be included
        // with cacheViewIDs.add() in the constructor above.
        //String viewID = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        //logger.error("ViewId is " + viewID);
    	
        reports = new EmissionsReportSearch[0];
        hasSearchResults = false;
    }

    public boolean isNtvReport() {
        return ntvReport;
    }

    public void setNtvReport(boolean ntvReport) {
        this.ntvReport = ntvReport;
    }

    public boolean isFromThread() {
        return fromThread;
    }

    public void setFromThread(boolean fromThread) {
        this.fromThread = fromThread;
    }	
    
    public final String getCmpId() {
        return searchObj.getCmpId();
    }

    public final void setCmpId(String cmpId) {
        searchObj.setCmpId(cmpId);
    }
    
    public final String getCompanyName() {
        return searchObj.getCompanyName();
    }

    public final void setCompanyName(String companyName) {
        searchObj.setCompanyName(companyName);
    }
    
    /**
     * @return the emissionsInventoryId
     */
    public final String getEmissionsInventoryId() {
        return searchObj.getEmissionsInventoryId();
    }

    /**
     * @param emissionsInventoryId the emissionsInventoryId to set
     */
    public final void setEmissionsInventoryId(String emissionsInventoryId) {
    	searchObj.setEmissionsInventoryId(emissionsInventoryId);
    }
    
    public final long getRefreshTime() {
        return refreshTime;
    }

    public final void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }
    
    public final Integer getSelectedReportId() {
    	Integer ret = null;

    	for(EmissionsReportSearch i: reports){
    		if(i.isSelected()){
    			ret = i.getEmissionsRptId();
    		}
    	}

    	return ret;	
    }
    
    public void process(String type) throws IOException {  
   	 	outStream = new FileWriter(new File(outFileName));
        outStreamSQL = new FileWriter(new File(outFileSQLName));
        
        
        emissReports = getEmissionsReports(false);
        outStream.write("Total number of " + type + " reports is " + emissReports.length + "\n");
        for(EmissionsReportSearch ers : emissReports) {
            // Read report
            EmissionsReport report = null;
            try {
                report = getEmissionsReportService().retrieveEmissionsReport(ers.getEmissionsRptId(), false);
            } catch(RemoteException re) {
                outStream.write("Failed on retrieveEmissionsReport(): report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + "\n");
                continue;
            }
            if(report == null) {
                outStream.write("Failed to read report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + "\n");
                continue;
            }

            Float oldTotalOrig = report.getTotalEmissions();
            Float oldTotal = oldTotalOrig;
            if(oldTotalOrig == null) {
                oldTotal = new Float(0);
            }
            TreeSet<EmissionTotal> oldTotals = report.getEmissionTotalsTreeSet();

            ReportTemplates locatedScRpts = null;
            try {
                locatedScRpts = getEmissionsReportService().retrieveSCEmissionsReports(report.getReportYear(),
                		report.getContentTypeCd(),
                		ers.getFacilityId());
            } catch(RemoteException re) {
                outStream.write("Failed on retrieveSCEmissionsReports(): report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + "\n");
                continue;
            }
            if(locatedScRpts == null) {
                outStream.write("Failed to retrieveSCEmissionsReports for report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + "\n");
                continue;
            }
            for(String s : locatedScRpts.getDisplayMsgs()) {
                outStream.write("(Failed?): Display Msg for report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + " " + s + 
                        " and content type " + ers.getContentTypeCd() + "\n");
            }
            if(locatedScRpts.isFailed()) {
                outStream.write("Failed to locate report definitions for report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() +"\n");
                continue;
            }

            ArrayList<EmissionRow> reportEmissions = null;
            
            try {
                // get report rollup of emissions
            	 reportEmissions = EmissionRow.getEmissions(report, false,
                         true, new Integer(1), locatedScRpts, logger, false);
            } catch(ApplicationException ae) {
                outStream.write("Failed, Application Exception for report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + "\n");
                continue;
            }
            // does not update database
            getEmissionsReportService().createTotalsRows(report, reportEmissions);

            // Determine if the totals have changed.
            boolean changedTotals = false;
            TreeSet<EmissionTotal> newTotals = report.getEmissionTotalsTreeSet();
            if(oldTotals.size() != newTotals.size()) {
                changedTotals = true;
            }

            if(!changedTotals) {
                for(EmissionTotal et : oldTotals) {
                    changedTotals = true;
                    for(EmissionTotal et2 : newTotals) {
                        if(et.getPollutantCd().equals(et2.getPollutantCd())) {
                            if(et.getTotalEmissions().equals(et2.getTotalEmissions())) {
                                changedTotals = false;
                            }
                            break;
                        }
                    }
                    if(changedTotals) {
                        break;
                    }
                }
            }
            
            StringBuffer newTotalsValues = new StringBuffer(", newTotals ");
            if(changedTotals) {
                for(EmissionTotal et2 : newTotals) {
                    newTotalsValues.append(et2.getPollutantCd() + "="
                         + et2.getTotalEmissions() + ", ");
                    outStreamSQL.write("INSERT INTO rp_report_pollutant_totals (emissions_rpt_id, pollutant_cd, total_emissions) VALUES (" + ers.getEmissionsRptId() + ", '" + et2.getPollutantCd() + "', '" +  et2.getTotalEmissions() + "');");
                }
            }

            Float newTotal = 0f;
            // update total in report
            if(null != locatedScRpts.getSc()) {
                try {
                    // does not update database
                	getEmissionsReportService().setFeeInfo(report, reportEmissions, locatedScRpts.getSc());
                } catch (RemoteException e) {
                    outStream.write("Failed to total emissions or set fee info for report " + ers.getEmissionsRptId() + 
                            " for facility " + ers.getFacilityId() +
                            " and year " + ers.getYear() + "\n");
                    continue;
                }
                newTotal = report.getTotalReportedEmissions();
                getEmissionsReportService().updateReportedEmissionsTotal(report, newTotal);
                if(!oldTotal.equals(newTotal)) {
                    outStreamSQL.write("UPDATE rp_emissions_rpt set total_emissions=" +
                            newTotal + " WHERE emissions_rpt_id=" + ers.getEmissionsRptId() + ";");
                }
            } 
        }
    }

    // Return reports
    // FUNCTION USED ORIGINALLY TO GO THROUGH ENTIRE DATABASE
    protected EmissionsReportSearch[] getEmissionsReports(boolean staging) throws RemoteException {
        EmissionsReportSearch searchObj = new EmissionsReportSearch();
        searchObj.setUnlimitedResults(true);
        emissReports = getEmissionsReportService().searchEmissionsReports(searchObj, staging);        
        return emissReports;
    }

	public boolean isStandardList() {
		return standardList;
	}

	public void setStandardList(boolean standardList) {
		this.standardList = standardList;
	}

	public boolean isInvoiceTrackingList() {
		return invoiceTrackingList;
	}

	public void setInvoiceTrackingList(boolean invoiceTrackingList) {
		this.invoiceTrackingList = invoiceTrackingList;
	}
    
	public String getListType() {
		String ret = null;
		if(isStandardList()) {
			ret = STANDARD_LIST;
		}else if(isInvoiceTrackingList()) {
			ret = INVOICE_TRACKING_LIST;
		}else {
			ret = "Unknown";
		}
		
		return ret;
	}
}
