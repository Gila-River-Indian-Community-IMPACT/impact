package us.oh.state.epa.stars2.app.emissionsReport;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityYearPair;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.reports.AutoGenRow;
import us.oh.state.epa.stars2.webcommon.reports.NtvReport;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;
import us.oh.state.epa.stars2.webcommon.reports.ReportTemplates;

public class AutoGenNTV extends AppBase {

	private static final long serialVersionUID = -5347077260405712910L;

	private transient Logger logger = Logger.getLogger(this.getClass());
    private int evenYear;
    private int oddYear;
    private ReportSearch reportSearch = null;
    private Integer user = null;
    
    private FacilityYearPair anyInfo = null;
    private FacilityYearPair evenInfo = null;
    private FacilityYearPair oddInfo = null;
    
    private ArrayList<AutoGenRow> genList = new ArrayList<AutoGenRow>();
    private ArrayList<AutoGenRow> mayGenList = new ArrayList<AutoGenRow>();
    
    private String candidateIncludedStr = " ";
    private String errorStr = null;
    private int percentProg = 0;
    private boolean genListButton = true;  // render the generate candidate list button
    private boolean cancelButton = false;  // render the cancel button
    private boolean genRptsButton = false;  // render the generate the reports button
    private boolean showTable = false;  // show table results -- not during operations
    private boolean showProgress = false;  // show progress bar on generating reports
    private boolean allowInput = true; // allow user to input years.
    boolean execute = false;
    
    private SCEmissionsReport oddDef = null;
    private SCEmissionsReport evenDef = null;
    
    private static AutoGenNTV autoGenNTV = null;
    private boolean cancelled = false;
    private String refreshStr = " ";
    private String refreshStrOn = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
        + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
        + "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
        + "<META HTTP-EQUIV=\"Cache-Control\" "
        + "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";

    
    private EmissionsReportService emissionsReportService;
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

	public String initAutoGenNTV() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        if(y %2 != 0) {
            oddYear = y - 2;
        } else {
            oddYear = y - 1;
        }
        evenYear = oddYear - 1;
        genList = new ArrayList<AutoGenRow>();
        mayGenList = new ArrayList<AutoGenRow>();
        genListButton = true;
        cancelButton = false;
        genRptsButton = false;
        showTable = false;
        showProgress = false;
        allowInput = true;
        candidateIncludedStr = " ";
        errorStr = null;
        return "autoGenNTV";
    }
    
    public String startOperation() {
        /* Look in yearly category table for those facilities where oddYear and/or
         * evenYear has a state of:
         *     ReportReceivedStatusDef.REMINDER_SENT or
         *     ReportReceivedStatusDef.SECOND_REMINDER_SENT
         * retrieve year and facilityId ordered first by facilityId then year.
         * 
         * Generate lines of the datagrid:
         *   Retrieve the report for the previous year (no need to retrieve the
         *   previous both previous years).
         *   Fill in the autoGenRow from the FacilityYearPair objects.
         *   
         *   When done, display the datagrid so user can select which ones to generate.
         */
        
        execute = isInvoicer();
        genListButton = true;
        cancelButton = false;
        genRptsButton = false;
        showTable = false;
        showProgress = false;
        candidateIncludedStr = " ";
        errorStr = null;
        if(evenYear % 2 != 0) {
            DisplayUtil.displayError("Even Year is not an even number");
            return "autoGenNTV";
        }
        if(evenYear + 1 != oddYear) {
            DisplayUtil.displayError("Odd Year is not one higher than Even Year");
            return "autoGenNTV";
        }
 
        ReportTemplates evenTemplate = null;
        try {
            reportSearch = (ReportSearch) FacesUtil.getManagedBean("reportSearch");
            user = InfrastructureDefs.getCurrentUserId();
            evenTemplate = getEmissionsReportService().retrieveSCEmissionsReports(evenYear, "AC",
                "F000555");   // dummy value since this code is not used in IMPACT
            if(evenTemplate == null) {
                DisplayUtil.displayError("No NTV Report Definitions found for year " + evenYear);
                return "autoGenNTV";
            }
        } catch(RemoteException re) {
            handleException(re);
            return "autoGenNTV";
        }
        evenDef = evenTemplate.getSc();
        if(evenDef == null) {
            DisplayUtil.displayError("No NTV Report Definition found for year " + evenYear);
            return "autoGenNTV";
        }
        allowInput = false;
        ReportTemplates oddTemplate = null;
        try {
            oddTemplate = getEmissionsReportService().retrieveSCEmissionsReports(oddYear, "AC",
            		"F000555"); // dummy value since this code is not used in IMPACT
            if(oddTemplate == null) {
                DisplayUtil.displayError("No NTV Reports Definition found for year " + oddYear);
                return "autoGenNTV";
            }
        } catch(RemoteException re) {
            handleException(re);
            return "autoGenNTV";
        }
        oddDef = oddTemplate.getSc();
        if(oddDef == null) {
            DisplayUtil.displayError("No NTV Report Definition found for year " + oddYear);
            return "autoGenNTV";
        }
        
        Integer minValue = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_NTV_FER_Must_Enumerate", null);
        boolean err = false;
        genList = new ArrayList<AutoGenRow>();
        mayGenList = new ArrayList<AutoGenRow>();
        FacilityYearPair[] candidates = null;
        try {
            candidates = getEmissionsReportService().retrieveStragglerNTV(oddYear, evenYear);
        } catch(RemoteException re) {
            handleException(re);
           err = true;
        }
        if(!err) {
            int pairIndex = 0;
            while (pairIndex >= 0) {
                if(genList.size() + mayGenList.size() >= 1000) {
                    errorStr = "First 1000 candidate faciities found.  Process these, then repeat.";
                    break;
                }
                boolean evenSkip = false;
                boolean oddSkip = false;
                pairIndex = locateNextPair(pairIndex, candidates);
                if(evenInfo == null || !evenInfo.getCategory().equals(EmissionReportsDef.NTV)
                        || (!evenInfo.getState().equals(ReportReceivedStatusDef.REMINDER_SENT)
                           && !evenInfo.getState().equals(ReportReceivedStatusDef.SECOND_REMINDER_SENT))) {
                    evenSkip = true;
                }
                if(oddInfo == null || !oddInfo.getCategory().equals(EmissionReportsDef.NTV)
                        || (!oddInfo.getState().equals(ReportReceivedStatusDef.REMINDER_SENT)
                           && !oddInfo.getState().equals(ReportReceivedStatusDef.SECOND_REMINDER_SENT))) {
                    oddSkip = true;
                }
                if(evenSkip && oddSkip) {
                    continue;
                }
                
                // locate previous approved report
                int year = evenYear - 1;
                if(evenSkip) {
                    year = evenYear;
                }
                EmissionsReport rpt = null;
                Integer prevRptId = null;
                String facilityName = "";
                int fpId = 0;
                Float totEmissions = null;
                String feeDescription = null;
                String oldEmissions = "";
                Integer oddFeeId = null;
                Integer evenFeeId = null; 
                try {
                    rpt = getEmissionsReportService().retrieveLatestEmissionReport(year, anyInfo.getFacilityId());
                } catch(RemoteException re) {
                    logger.error("retrieveLatestEmissionReport() failed one facility " + anyInfo.getFacilityId() +
                            " and year " + year, re);
                    err = true;
                }
                
                if(!err) {
                    AutoGenRow agr = new AutoGenRow(anyInfo, evenSkip, evenYear, oddSkip, oddYear);
                    boolean autoGen = false;
                    Integer emissionsRequired = null;
                    if(rpt == null) {
                        // no previous approved report.
                        // Get facility name
                        Facility f;
                        try {
                            f = getFacilityService().retrieveFacilityData(anyInfo.getFacilityId(), new Integer(-1));
                            facilityName = f.getName();
                            fpId = f.getFpId();
                        } catch(RemoteException re) {
                            logger.error("retrieveFacilityData() failed for " + anyInfo.getFacilityId());
                        }
                    } else {
                        // There is a report.
                        prevRptId = rpt.getEmissionsRptId();
                        // Retrieve details of report by searching based upon report Id.
                        EmissionsReportSearch[] rptDetails =  getReportDetail(rpt.getEmissionsRptId());
                        if(rptDetails == null) { 
                            logger.error("getReportDetail() failed on " + rpt.getEmissionsRptId());
                            err = true;
                        } else if(rptDetails.length != 1) {
                            logger.error("getReportDetail() failed on " + rpt.getEmissionsRptId() +
                                    " with length=" + rptDetails.length);
                            err = true;
                        } else {
                            facilityName = rptDetails[0].getFacilityName(); // TODO We could use name from current not profile tied to report
                            fpId = rptDetails[0].getFpId();  // TODO We could also get fp_id from current
                            totEmissions = rptDetails[0].getTotalEmissions();
                            if(totEmissions == null) {
                                if(rptDetails[0].getLowRange() != null) {
                                    // get displayable
                                    if(rptDetails[0].getHighRange() != null) {
                                        if(rptDetails[0].getHighRange() >= 1) {
                                            emissionsRequired = new Integer(rptDetails[0].getHighRange() - 1);
                                        } else {
                                            emissionsRequired = new Integer(0);
                                        }
                                        oldEmissions = rptDetails[0].getLowRange().toString() +
                                            " to " + rptDetails[0].getHighRange().toString();
                                    } else {
                                        oldEmissions = "more than " + rptDetails[0].getLowRange().toString();
                                    }
                                }
                            } else {
                                emissionsRequired = new Integer(totEmissions.intValue());
                                // get displayable
                                double d = totEmissions.doubleValue();
                                oldEmissions = EmissionsReport.numberToString(d);
                            }
                            // Determine if emissions too high
                            if(emissionsRequired != null && minValue > emissionsRequired.intValue()) {
                                if(!evenSkip) {
                                    evenFeeId = evenDef.locateFeeId(emissionsRequired.intValue());
                                    if(evenFeeId != null && evenDef.locateFee(evenFeeId) != null) {
                                        feeDescription = evenDef.locateFee(evenFeeId).getShortDescription();
                                    } else {
                                        DisplayUtil.displayError("Failed to locate Fee for  " + emissionsRequired.intValue() + 
                                                " tons for year "+ evenYear);
                                    }
                                }
                                if(!oddSkip) { 
                                    String oddFeeDescription = "";
                                    oddFeeId = oddDef.locateFeeId(emissionsRequired.intValue());
                                    if(oddFeeId != null && oddDef.locateFee(oddFeeId) != null) {
                                        oddFeeDescription = oddDef.locateFee(oddFeeId).getShortDescription();
                                    } else {
                                        DisplayUtil.displayError("Failed to locate Fee for  " + emissionsRequired.intValue() + 
                                                " tons for year "+ oddYear);
                                    }
                                    if(feeDescription == null) {
                                        feeDescription = oddFeeDescription;
                                    } else {
                                        feeDescription = feeDescription + " & " + oddFeeDescription;
                                    }
                                }   
                                autoGen = true;
                            }
                        }
                        if(err) {
                            DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
                            return "autoGenNTV";
                        }
                    }
                    agr.addRptInfo(prevRptId, null, facilityName, fpId, oldEmissions, feeDescription, evenFeeId, oddFeeId, autoGen);
                    if(agr.isCanGenerate()) {
                        genList.add(agr);
                    } else {
                        mayGenList.add(agr);
                    }
                }
            }
            genList.addAll(mayGenList);
        }
        genRptsButton = true;
        showTable = true;
        allowInput = true;
        return "autoGenNTV";
    }

    public String selectAll() {
        for(AutoGenRow agr : genList) {
            agr.setCanGenerate(true);
        }
        return "autoGenNTV";
    }
    
    public String selectNone() {
        for(AutoGenRow agr : genList) {
            agr.setCanGenerate(false);
        }
        return "autoGenNTV";
    }
    
    protected EmissionsReportSearch[] getReportDetail(Integer rptId)  {
        EmissionsReportSearch[] reports = null;
        try {
            EmissionsReportSearch searchObj = new EmissionsReportSearch();
            searchObj.setEmissionsRptId(rptId);
            searchObj.setUnlimitedResults(true);
            reports = getEmissionsReportService().searchEmissionsReports(searchObj, false);
        } catch(RemoteException e) {
            logger.error("Failed on getEmissionsReports() for emissionsRptId " + rptId, e);
            handleException(e);
        }
        return reports;
    }
    
    private int locateNextPair(int pairIndex, FacilityYearPair[]candidates) {
        if (pairIndex < candidates.length) {
            evenInfo = null;
            oddInfo = null;
            anyInfo = candidates[pairIndex];
            if(anyInfo.getYear() == evenYear)  {
                evenInfo = anyInfo;
            } else if(anyInfo.getYear() == oddYear) {
                oddInfo = anyInfo;
            } else {
                // not expected.
            }
            pairIndex ++;
            if (pairIndex < candidates.length) {
                FacilityYearPair info2 = candidates[pairIndex];
                if(anyInfo.getFacilityId().equals(info2.getFacilityId())) {
                    if(info2.getYear() == evenYear && evenInfo == null)  {
                        evenInfo = info2;
                    } else if(info2.getYear() == oddYear && oddInfo == null) {
                        oddInfo = info2;
                    } else {
                        // not expected.
                    }
                    pairIndex ++;
                }
            }
        } else {
            pairIndex = -1;
        }
        return pairIndex;
    }
    
    public String doGenerate() {
        genListButton = false;
        cancelButton = true;
        cancelled = false;
        genRptsButton = false;
        showTable = false;
        showProgress = true;
        allowInput = false;
        autoGenNTV = this;
        refreshStr = refreshStrOn;
        candidateIncludedStr = "";
        errorStr = null;
        percentProg = 0;
        this.setValue(percentProg);
        this.setMaximum(100);
        RunReport reportThread = new RunReport();
        reportThread.start();
        return "autoGenNTV";
    }

class RunReport extends Thread {
    public void run() {
        try {
            autoGenNTV.process();
        } catch(Exception e) {
            String s = "ERROR: autoGenNTV.process() failed with " + e.getMessage();
            logger.error(s, e);
            errorStr = s;
            genListButton = true;
            cancelButton = false;
            showTable = true;
            showProgress = false;
            allowInput = true;
            refreshStr = " ";
        }
        genListButton = true;
        cancelButton = false;
        showTable = true;
        showProgress = false;
        allowInput = true;
        refreshStr = " ";
    }
}

public void process() throws DAOException {
        int numGenerated = 0;
        int numDup = 0;
        boolean err = false;
        // For each row marked to generate,
        // create and submit a NTV report.
        Calendar cal = Calendar.getInstance();
        
        for(AutoGenRow agr : genList) {
            if(cancelled) {
                break;
            }
            if(!agr.isCanGenerate()) {
                continue;
            }
            EmissionsReport reportA = null;
            if(agr.getEvenYear() != 0) {
                try {
                    EmissionsReportSearch[] rpts = getEmissionsReports(agr.getFacilityId(), agr.getEvenYear());
                    if(rpts.length > 0) {
                        numDup++;
                        agr.setDuplicate(true);
                    }
                } catch(RemoteException re) {
                    errorStr = numGenerated + " NTV reports generated prior to an error.  Rerun to retry the rest.";
                    err = true;
                    refreshStr = " ";
                    logger.error("getEmissionsReports failed", re);
                    throw new DAOException(re.getMessage(), re);
                }
                
                reportA = new EmissionsReport();
                reportA.setReportYear(agr.getEvenYear());
                reportA.setFeeId(agr.getEvenFeeId());
                reportA.setRptReceivedStatusDate(new Timestamp(cal.getTimeInMillis()));
            }
            EmissionsReport reportB = null;
            if(agr.getOddYear() != 0) {
                try {
                    EmissionsReportSearch[] rpts = getEmissionsReports(agr.getFacilityId(), agr.getOddYear());
                    if(!agr.isDuplicate() && rpts.length > 0) {
                        numDup++;
                        agr.setDuplicate(true);
                    }
                } catch(RemoteException re) {
                    errorStr = numGenerated + " NTV reports generated prior to an error.  Rerun to retry the rest.";
                    err = true;
                    refreshStr = " ";
                    logger.error("getEmissionsReports failed", re);
                    throw new DAOException(re.getMessage(), re);
                }
                
                reportB = new EmissionsReport();
                reportB.setReportYear(agr.getOddYear());
                reportB.setFeeId(agr.getOddFeeId());
                reportB.setRptReceivedStatusDate(new Timestamp(cal.getTimeInMillis()));
            }
            if(!err && !agr.isDuplicate()) {
                NtvReport genRpt = new NtvReport(reportA, reportB);
                try {
                    getEmissionsReportService().createSubmitReport(agr.getFacilityId(), agr.getFpId(), genRpt, user);
                    agr.setHasBeenGenerated(true);
                    numGenerated++;
                    percentProg = (numGenerated * 100)/genList.size();
                    candidateIncludedStr = numGenerated + " NTV reports generated";
                    this.setValue(percentProg);
                } catch(RemoteException re) {
                    errorStr = numGenerated + " NTV reports generated prior to an error.  Rerun to retry the rest.";
                    err = true;
                    refreshStr = " ";
                    logger.error("createSubmitReport failed", re);
                    throw new DAOException(re.getMessage(), re);
                }
            }
            if(err) {
                break;   // quit
            }
        }
        if(!err && numDup > 0) {
            errorStr = "There exist " + numDup + " facilities where the facility report category table indicates reminders sent but no report submitted; however, reports exist--new report not generated for these.";
            err = true;
        }
    }

public String cancel() {
    cancelled = true;
    errorStr = "Remaining report generation cancelled by user.";
    refreshStr = " ";
    genListButton = true;
    cancelButton = false;
    genRptsButton = false;
    showTable = true;
    showProgress = false;
    allowInput = true;
    return "autoGenNTV";
}

    public boolean isInvoicer(){
        return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("invoices.posttorevenues");
    }
    
    protected EmissionsReportSearch[] getEmissionsReports(String facilityId, Integer year) throws RemoteException {
        EmissionsReportSearch saveRS = reportSearch.getSearchObj();
        reportSearch.reset();
        reportSearch.setFromFacility("true");
        reportSearch.setFromThread(true);
        reportSearch.setFacilityId(facilityId);
        reportSearch.setYear(year);
        reportSearch.internalSubmitSearch(false);
        reportSearch.setSearchObj(saveRS);
        if(reportSearch.getReports() == null) {
            throw new RemoteException("internalSubmitSearch failed");
        }
        return reportSearch.getReports();
    }

    public ArrayList<AutoGenRow> getGenList() {
        return genList;
    }

    public String getCandidateIncludedStr() {
        return candidateIncludedStr;
    }

    public int getEvenYear() {
        return evenYear;
    }

    public void setEvenYear(int evenYear) {
        this.evenYear = evenYear;
    }

    public int getOddYear() {
        return oddYear;
    }

    public void setOddYear(int oddYear) {
        this.oddYear = oddYear;
    }

    public String getRefreshStr() {
        return refreshStr;
    }

    public boolean isExecute() {
        return execute;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getErrorStr() {
        return errorStr;
    }

    public int getPercentProg() {
        return percentProg;
    }

    public boolean isCancelButton() {
        return cancelButton;
    }

    public boolean isGenListButton() {
        return genListButton;
    }

    public boolean isGenRptsButton() {
        return genRptsButton;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public boolean isShowTable() {
        return showTable;
    }

    public boolean isAllowInput() {
        return allowInput;
    }
}
