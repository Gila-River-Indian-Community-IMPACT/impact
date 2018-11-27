package us.oh.state.epa.stars2.app.emissionsReport;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityAppInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityPermitEuInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityPermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.NAICSDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SICDef;
import us.oh.state.epa.stars2.def.SccCodesDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportTemplates;
import us.oh.state.epa.stars2.webcommon.reports.RowContainer;

@SuppressWarnings("serial")
public class FacilityEmissions extends AppBase {

    private transient Logger logger = Logger.getLogger(this.getClass());
    
    /*
     * Facility and EU information comes from the current profile.
     * If process emissions are requested, then historic profiles for each of the reporting years are 
     * referenced so process information is for the year--along with the emissions information.
     * We need to get each of those historic profiles and see what different SCCs a given EU has
     * so we can create the right number of lines--a given line will be for a single SCC but we will
     * have blank columns where not in that year's profile.
     */
    
    /*
     * Here is the list of considerations needed when determining what permit or application is applicable 
     * to a given EU during a given reporting year (from Erica).
     *   . For a reporting period you have Begin date 1/1/BBBB through End date 12/31/EEEE. 
     *   . A permit/PBR will be considered as effective during the reporting period if the
     *     following criteria are met: 
     *      > Permit issuance state = "Issued Final" or PBR disposition = "Accepted" 
     *      > permit/PBR effective date is prior to 12/31/EEEE; AND 
     *      > permit EU level terminated date is null or AFTER 01/01/BBBB; AND 
     *      > permit EU level revoked date is null or AFTER 01/01/BBBB; AND 
     *      > permit EU level superseded date is null or AFTER 01/01/BBBB; AND
     *   . In addition to any applications associated with effective permits, you need to display any 
     *     applications with an application received date prior to 12/31/EEEE with no associated permit
     *     that is in the "Issued Final" state.  
     *      > An "application" in this instance means any PTI/PTIO app, Title V app, PBR Request, or RAPM. 
     *      > An application received date will be either that date as specified in an app or a 
     *       the request received date in the case of the PBR.
     */
    private boolean haveInitialized = false;
    // Search Criteria
    private int firstYear = -1; // set from web page
    private int lastYear = -1; // set from web page
    private String facilityId; // set from web page
    //private String doLaaCd;
    //private String countyCd;
    private boolean ferPolls = true; // set from web page
    private boolean esPolls = true; // set from web page
    private boolean eisPolls = true; // set from web page
    private boolean facTots = true; // set from web page
    private boolean includeCurr = true;  // set from web page
    private boolean euTots = false;
    private boolean sdEUs = false; // set from web page
    private boolean opEUs = true; // set from web page
    private boolean removeNull = false; // set from web page -- remove if no emissions
    // rptLevel==0 exclude no facilites
    // rptLevel==1 exclude if no reports
    // rptLevel==2 exclude if no SMTV/TV reports
    // rptLevel==3 exclude if no TV reports
    private int rptLevel = 1; // set from web page
    
    // columns
    private boolean rowEuTot;
    private boolean includeAll;
    private boolean columnProcess;
    private boolean columnSccDesc;
    private boolean columnCE;
    private boolean columnPid;
    private boolean columnPdesc;
    //private boolean columnPtiPermit;
    private boolean columnAllPermit;
    private boolean columnTvPtoPermit;
    private boolean columnPtioPermit; //includes state PTO and PTIOs issued with "FEPTIO" enabled check box
    //private boolean columnRegistrationPermit;
    //private boolean columnPbrPermit;
    private boolean columnSic;
    private boolean columnNaics = true;
    private boolean columnSicNaicsDesc;
    private boolean columnEuDapc = true;
    private boolean columnEuCompany;
    private boolean columnEuInstallDates;
    private String extraPollutantCd;
    
    private List<String> selectedEUs = new ArrayList<String>();
    private List<String> selectedEUsIsAll = new ArrayList<String>();
    private List<SelectItem> allEUs;
    private boolean selectedAllEUs = false;
    private List<SelectItem> allCounties;
    private List<String> selectedCounties = new ArrayList<String>();
    private List<String> selectedCountiesIsAll = new ArrayList<String>();
    private boolean selectedAllCounties = false;

    private int highestRptLevel = 0; // keep track of highest level found.
    private boolean notInProgress = true;
    private String candidateIncludedStr = "";
    private String errorStr = null;
    private int percentProg = 0;
    private int numProcessed;
    private int rowCount = 0;
    private boolean cancelled = false;

    private int numFacilityLabels;
    private int numEULabels;
    private int numSccLabels;
    private int numProcessLabels;
    int numRptLevelLabels;
    private int euIdColPos;
    // private int euShutdownDateColumn;
    private  int facilityNameIndex;
    private int processSccIndex;
    private int pollsFromGrpsColumn;
    private int totLabels = 0;
    private int lastIndex = 0;
    private int corrIdIndex;  // position of string representing correlation ID of EU
    private String county =  null;
    private String facSic;
    private String facNaics;
    private String facSicDesc;
    private String facNaicsDesc;
    private HashMap<Integer, ReportTemplates> templates = new HashMap<Integer, ReportTemplates>(30);
    
    // Used when each year is another row.
    private ArrayList<String> allPollutants = new ArrayList<String>();  // pollutants for all years.

    private ArrayList<String> labels;
    private ArrayList<ArrayList<String>> datagrid = new ArrayList<ArrayList<String>>();
    private ArrayList<ArrayList<String>> datagridAll = new ArrayList<ArrayList<String>>();
    private EmissionsReportService erBO;
    private FacilityService fBO;
    protected static Integer[] facilities; // fp_id of facilities returned
    protected FacilityPermitInfo[] permitInfo;
    protected FacilityAppInfo[] appInfo;
    protected static boolean viewOnly = true;  // TODO  change flag to change behavior

    ReportProfileBase reportProfile;
    
    
    private String EU_TOTAL = " - ";  // "EU Total";
    private String EU_LETTERS = "";
    
    //StringBuffer strPbr = new StringBuffer();
    //StringBuffer strReg = new StringBuffer();
    StringBuffer strTvPto = new StringBuffer();
    StringBuffer strPtio = new StringBuffer();
    //StringBuffer strPti = new StringBuffer();
    //StringBuffer strPbrDate = new StringBuffer();
    //StringBuffer strRegDate = new StringBuffer();
    StringBuffer strTvPtoDate = new StringBuffer();
    StringBuffer strPtioDate = new StringBuffer();
    //StringBuffer strPtiDate = new StringBuffer();

    StringBuffer strTvPtoApp = new StringBuffer();
    StringBuffer strPtioApp = new StringBuffer();          
    //StringBuffer strPtiApp = new StringBuffer();

    StringBuffer strTvPtoAppDate = new StringBuffer();
    StringBuffer strPtioAppDate = new StringBuffer();
    //StringBuffer strPtiAppDate = new StringBuffer();

    private static FacilityEmissions facilityEmissions = null;
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
    /*
     * The generate datagrid need fields:
     *  facility fields; EU Id; and then for each year needs:  reporting category, total emissions per pollutant.
     *
     *  We need to determine the union of all pollutants over all reporting years included for TV and SMTV.
     *  Then when all the data is gathered, we can see where there is null in every row and colapse to remove those columns.
     */

    public String initFacilityEmissions() {
    	
        if (!haveInitialized) {
            reset();
            haveInitialized = true;
        }
        
        datagrid = new ArrayList<ArrayList<String>>();
        notInProgress = true;
        Calendar cal = Calendar.getInstance();
        if(firstYear == -1) {
            firstYear = cal.get(Calendar.YEAR) - 1;
            lastYear = firstYear;
        }

        EU_LETTERS = SystemPropertyDef.getSystemPropertyValue("VALID_EU_LETTERS", null);
        allEUs = new ArrayList<SelectItem>();
        allEUs.add(new SelectItem("all", "All EUs"));
        for(int ndx=0; ndx<EU_LETTERS.length(); ndx++) {
            String l = EU_LETTERS.substring(ndx, ndx + 1);
            String s = l + "xxx";
            allEUs.add(new SelectItem(s, s));
        }
 
        allCounties = new ArrayList<SelectItem>();
        allCounties.add(new SelectItem("all", "All Counties"));
        for(SelectItem si : County.getData().getItems().getAllItems()) {
            allCounties.add(new SelectItem(si.getValue(), si.getLabel()));
        }
        candidateIncludedStr = "";
        return "fpEmissions";
    }
    
    public String cancel() {
        cancelled = true;
        errorStr = "Report generation cancelled by user, partial results displayed.  Please wait for partial results.";
        return "fpEmissions";
    }
    
    public void reset() {
        datagrid = new ArrayList<ArrayList<String>>();
        notInProgress = true;
        candidateIncludedStr = "";
        errorStr = null;
        percentProg = 0;
        rowCount = 0;
        cancelled = false;
        Calendar cal = Calendar.getInstance();
        firstYear = cal.get(Calendar.YEAR) - 1;
        lastYear = firstYear;
        selectedEUs = new ArrayList<String>();
        selectedEUs.add("all");
        selectedEUsIsAll.add("all");
        selectedCounties = new ArrayList<String>();
        selectedCounties.add("all");
        selectedCountiesIsAll.add("all");
        facilityId = null;
        rptLevel = 1;
        ferPolls = true; // set from web page
        esPolls = true; // set from web page
        eisPolls = true; // set from web page
        facTots = true; // set from web page
        euTots = false;
        sdEUs = false; // set from web page
        opEUs = true; // set from web page
        removeNull = false;
        rowEuTot = false;
        columnProcess = false;
        columnSccDesc = false;
        columnCE = false;
        columnPid = false;
        columnPdesc = false;
        //columnPtiPermit = false;
        columnTvPtoPermit = false;
        //columnPtioPermit = false; //includes state PTO and PTIOs issued with "FEPTIO" enabled check box
        columnPtioPermit = false; //includes NSR
        //columnRegistrationPermit = false;
        //columnPbrPermit = false;
        columnSic = false;
        columnNaics = true;
        columnSicNaicsDesc = false;
        columnEuDapc = true;
        columnEuCompany = false;
        columnEuInstallDates = false;
        extraPollutantCd = null;
    }

	public String startOperation() {
//      try { // used to provide time to start the debugger
//      Thread.sleep(10000);  // sleep 10 seconds
//      } catch(InterruptedException e) {
//      System.err.println("Sleep exception");
//      }

        cancelled = false;
//        try {
            erBO = getEmissionsReportService();
            fBO = getFacilityService();
//        } catch(RemoteException re) {
//            logger.error("Failed on accessing the BOs", re);
//            DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
//            return "fpEmissions";
//        }

        if(!sdEUs && !opEUs && !facTots) {
            DisplayUtil.displayError("Must include shutdown and/or operating & shutdown EUs and/or facility totals");
            return "fpEmissions";
        }
        
        // process the EUs
        euTots = sdEUs || opEUs;
        
        if(!ferPolls && !esPolls && !eisPolls && extraPollutantCd==null) {
            DisplayUtil.displayError("Must include some pollutants (FER, ES, EIS, and/or Additional Pollutant)");
            return "fpEmissions";
        }

//        if(!euTots && !facTots) {
//            DisplayUtil.displayError("Must include some Facility and/or EU Totals");
//            return "fpEmissions";
//        }

        if(firstYear > lastYear) {
            DisplayUtil.displayError("First report year cannot be later than last report year");
            return "fpEmissions";
        }

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        if(lastYear > year) {
            DisplayUtil.displayError("Cannot provide a Report Year in the future");
            return "fpEmissions";
        }
        datagrid = new ArrayList<ArrayList<String>>();
        datagridAll = new ArrayList<ArrayList<String>>();
        // Get the bean address before new thread is created.
        reportProfile = (ReportProfileBase) FacesUtil
        .getManagedBean("reportProfile");
        
        selectedAllCounties = false;
        if(selectedCounties == null || selectedCounties.size() == 0 ||
                selectedCounties.get(0).equals("all")) {
            selectedAllCounties = true;
        }
        
        selectedAllEUs = false;
        if(selectedEUs == null || selectedEUs.size() == 0 ||
                selectedEUs.get(0).equals("all")) {
            selectedAllEUs = true;
        }

        facilityEmissions = this;
        refreshStr = refreshStrOn;
        candidateIncludedStr = "";
        errorStr = null;
        percentProg = 0;
        rowCount = -1; // don't count label row.
        this.setValue(percentProg);
        this.setMaximum(100);
        notInProgress = false;
        RunReport reportThread = new RunReport();
        reportThread.start();
        return "fpEmissions";
    }

    @SuppressWarnings("unchecked")
	public void process() {
        // Retrieve all report definitions:
        candidateIncludedStr = "Retrieving Report Definitions";
        for(int year = firstYear; year <= lastYear; year++) {
            getReportTemplate(year, true);
        }
        // compute pollutants per year
        determinePollutantsPerYear();

        // Generate datagrid column labels
        numFacilityLabels = 6;
        if(columnSic) numFacilityLabels++;
        if(columnSic && columnSicNaicsDesc) numFacilityLabels++;
        if(columnNaics) numFacilityLabels++;
        if(columnNaics && columnSicNaicsDesc) numFacilityLabels++;
        numEULabels = 5;
        if(columnEuDapc) numEULabels++;
        if(columnEuCompany) numEULabels++;
        if(columnEuInstallDates) numEULabels = numEULabels + 3;
        numRptLevelLabels = 1;
        numSccLabels = 1;  //always include column to record SccId, or "fac Total" or "EU Total"
        if(columnSccDesc) numSccLabels = numSccLabels + 4;
        numProcessLabels = 0;
        //if(columnPtiPermit) numProcessLabels = numProcessLabels + 4;
        if(columnTvPtoPermit) numProcessLabels = numProcessLabels + 4;
        if(columnPtioPermit) numProcessLabels = numProcessLabels + 4;
        //if(columnRegistrationPermit) numProcessLabels = numProcessLabels + 2;
        //if(columnPbrPermit) numProcessLabels = numProcessLabels + 2;
        if(columnPid) numProcessLabels++;
        if(columnPdesc) numProcessLabels++;
        if(columnCE) numProcessLabels = numProcessLabels + 2;
        int totNumRptLabels = 0;
        int polCnt = 0;
        if(allPollutants != null && allPollutants.size() != 0) {
            polCnt = allPollutants.size();
        }
        totNumRptLabels = totNumRptLabels + numRptLevelLabels + numProcessLabels + polCnt;

        totLabels = numFacilityLabels + numEULabels + numSccLabels + totNumRptLabels;
        lastIndex = totLabels - 1;
        if(totLabels > 200) {
            candidateIncludedStr = "Generated datagrid would have " + totLabels + " columns.  200 is the maximum.  Reduce number of years or pollutants included.";
            removeNull = false;  // to turn off postprocessing
            return;
        }
        labels = new ArrayList<String>(totLabels);
        initRow(labels, totLabels);
        // datagridAll.add(labels);
        int curLabel = 0;
        labels.set(curLabel++, "Year");
        labels.set(curLabel++, "Facility Operating Status");
        labels.set(curLabel++, "Facility ID");
        facilityNameIndex = curLabel;
        labels.set(curLabel++, "Faciity Name");
        labels.set(curLabel++, "County");
        if(columnSic) {
            labels.set(curLabel++, "SIC");
        }
        if(columnSic && columnSicNaicsDesc) {
            labels.set(curLabel++, "SIC Description");
        }
        if(columnNaics) {
            labels.set(curLabel++, "NAICS");
        }
        if(columnNaics && columnSicNaicsDesc) {
            labels.set(curLabel++, "NAICS Description");
        }
        labels.set(curLabel++, "Current Reporting Category");
        euIdColPos = curLabel;
        labels.set(curLabel++, "EU ID");
        labels.set(curLabel++, "EU Operating Status");
        if(columnEuDapc) {
            labels.set(curLabel++, "AQD Description");
        }
        if(columnEuCompany) {
            labels.set(curLabel++, "Company Description");
        }
        if(columnEuInstallDates) {
            labels.set(curLabel++, "EU Initial Install");
            labels.set(curLabel++, "EU Begin Install");
            labels.set(curLabel++, "EU Commence Operate");
        }
        // euShutdownDateColumn = curLabel;
        labels.set(curLabel++, "EU Shutdown Date");
        pollsFromGrpsColumn = curLabel;
        labels.set(curLabel++, "Grouped");
        corrIdIndex = curLabel;
        labels.set(curLabel++, "Correlation ID");  // will not be visible
        processSccIndex = curLabel;
//      if(!facTots && !euTots) {
        labels.set(curLabel++, "SCC ID");
//      } else {
//      labels.set(curLabel++, "Fac/EU /SCC");
//      }
        if(columnSccDesc) {
            labels.set(curLabel++, "SCC L1 Desc");
            labels.set(curLabel++, "SCC L2 Desc");
            labels.set(curLabel++, "SCC L3 Desc");
            labels.set(curLabel++, "SCC L4 Desc");
        }

        labels.set(curLabel++, "Reporting Category ");
        if(columnPid) {
            labels.set(curLabel++, "Process Id");
        }
        if(columnPdesc) {
            labels.set(curLabel++, "Process Desc");
        }
        if(columnCE) {
            labels.set(curLabel++, "Process CE");
            labels.set(curLabel++, "CE Install");
        }
        //if(columnPtiPermit) {
        //    labels.set(curLabel++, "NSR Permits");
        //    labels.set(curLabel++, "NSR Permits Effective");
        //    labels.set(curLabel++, "Included NSR Applications");
        //    labels.set(curLabel++, "Applications Submitted");
        //}
        if(columnTvPtoPermit) {
            labels.set(curLabel++, "TV NSR Permits");
            labels.set(curLabel++, "TV NSR Permits Effective");
            labels.set(curLabel++, "Included TV NSR Applications");
            labels.set(curLabel++, "Applications Submitted");
        }
        if(columnPtioPermit) {
            labels.set(curLabel++, "NSR Permits");
            labels.set(curLabel++, "NSR Permits Effective");
            labels.set(curLabel++, "Included NSR Applications");
            labels.set(curLabel++, "Applications Submitted");
        }
        //if(columnRegistrationPermit) {
        //    labels.set(curLabel++, "Reg Permits");
        //    labels.set(curLabel++, "Reg Permits Effective");
        //}
        //if(columnPbrPermit) {
        //    labels.set(curLabel++, "PBR Permits");
        //    labels.set(curLabel++, "PBR Permits Effective");
        //}

        if(polCnt != 0) {
            for(String s : allPollutants) {
                String name = PollutantDef.getData().getItems().getItemShortDesc(s);
                labels.set(curLabel++, name);
            }
        }
        candidateIncludedStr = "Determining Matching Facilities";
        facilities = getFacilities();
        if(facilities == null) {
            // no facilities matched conditions
            candidateIncludedStr = "No facilites matched the conditions";
            notInProgress = true;
            refreshStr = " ";
            return;
        }

        // Loop through the FpIds of returned facilities.
        numProcessed = 0;
        int firstFacRow = 0;
     facilityLoop:
        for(Integer iii : facilities) {
            highestRptLevel = 0;
            if(cancelled) {
                break;
            }
            percentProg = (numProcessed * 100)/facilities.length;
            numProcessed++;
            this.setValue(percentProg);
            // Read facility
            Facility facility = null;
            try {
                facility = fBO.retrieveFacilityProfile(iii);
            } catch(RemoteException re) {
                logger.error("Failed on retrieveFacilityData(): fpId " + iii, re);
            }
            if(facility == null) {
                logger.error("Failed to read facility with fpId " + iii);
                // Mark missing facility data
                ArrayList<String> r = new ArrayList<String>(totLabels);
                initRow(r, totLabels);
                r.set(facilityNameIndex, "ERROR Reading FpId " + iii);
                datagrid.add(r);
                continue;
            }

            // Get permit/App info for facility
            if(
            		//columnPtiPermit || 
            		columnTvPtoPermit ||
            		columnPtioPermit 
            		//||
                    //columnRegistrationPermit || columnPbrPermit
                    ) {
                // Do this once per facility.
                retrievePermitsApps(facility.getFacilityId());
            }
            // Get all the reports
            EmissionsReportSearch[] rpts = null;
            rpts = getEmissionsReports(facility.getFacilityId());
            if(rpts == null) {
                logger.error("Failed on getEmissionsReports for facility " + facility.getFacilityId());
                // Mark missing data
                ArrayList<String> r = new ArrayList<String>(totLabels);
                initRow(r, totLabels);
                int i = 0;
                r.set(i++, null);
                r.set(i++, county);
                String op = null;
                if(facility.getOperatingStatusCd() != null) {
                    op = EuOperatingStatusDef.getData().getItems().getItemDesc(facility.getOperatingStatusCd());
                }
                r.set(i++, op);
                r.set(i++, facility.getFacilityId());
                r.set(facilityNameIndex, "Failed to read reports for facility ");
                datagrid.add(r);
                continue;
            }

//          These three lists will be used together.
            ArrayList<EmissionsReportSearch> rptInfoList = new ArrayList<EmissionsReportSearch>();
            ArrayList<Facility> rptFacList = new ArrayList<Facility>();
            ArrayList<Boolean>  rptFacSuccessList = new ArrayList<Boolean>();
            for(int year = firstYear; year <= lastYear; year++) {
                EmissionsReportSearch rptInfo = getLatest(rpts, year);
                rptInfoList.add(rptInfo);
                Facility f = null;
                boolean ok = true;
                if(columnProcess || euTots) {
                    if(rptInfo != null) {
                        try {
                            f = fBO.retrieveFacilityProfile(rptInfo.getFpId());
                        }catch(RemoteException re) {
                            ok = false;
                            logger.error("Failed reading facility " + facility.getFacilityId() + ", fpId " + 
                                    rptInfo.getFpId() + " for report " + rptInfo.getEmissionsRptId(), re);
                        }
                    }
                }
                rptFacSuccessList.add(ok);
                rptFacList.add(f);
            }

            int lastFacRow;
            // rows for historic facilites tied to reports
            // first loop around is for current.  Don't look for any reports or historic profiles
            int year = firstYear - 2;
            for(int ndx= -1; ndx < rptInfoList.size(); ndx++) {
                int yearOriginIndex = numFacilityLabels + numEULabels + numSccLabels;
                Facility ff = null;
                if(ndx >= 0) {
                    ff = rptFacList.get(ndx);
                } else {
                    ff = facility;
                }
                year++;
                firstFacRow = datagrid.size();
                ArrayList<String> facRow = null;
                if(ff == null) continue;  // since no profile, cannot display anything.
                if(ndx >= 0) {
                    facRow = createFacilityRows(year, ff, firstFacRow, ff.getFpId(), rptFacList);
                } else if(includeCurr){ // should we include current facility information
                    int sizeBefore = datagrid.size();
                    facRow = createFacilityRows(0, ff, firstFacRow, ff.getFpId(), rptFacList);
                    int sizeAfter = datagrid.size();
                    if(!(selectedEUs.size()==0 || "all".equals(selectedEUs.get(0))) && facTots && (sizeAfter - sizeBefore) == 1) {
                        datagrid.remove(datagrid.size() - 1); // remove facility row and skip facility
                        continue facilityLoop;
                    }
                }
                lastFacRow = datagrid.size() - 1;
                if(firstFacRow == datagrid.size()) {
                    // nothing to report for this facility, therefore
                    // skip processing reports
                    continue;
                }
                EmissionsReport report = null;
                ReportTemplates locatedScRpts = null;
                if(ndx >= 0) {
                    EmissionsReportSearch rptInfo = rptInfoList.get(ndx);
                    if(rptInfo == null) {
                        // no report so skip
                        continue;
                    }
                    
                    locatedScRpts = getReportTemplate(year, false);
                    if(locatedScRpts == null) {
                        logger.error("getReportTemplate() failed to find report definition for year " + year);
                        // Mark missing data
                        for(int i = firstFacRow; i <= lastFacRow; i++) {
                            ArrayList<String> row = datagrid.get(i);
                            row.set(yearOriginIndex, "ERROR");
                        }
                        continue;
                    }


                    // Read the report.
                    try {
                        report = erBO.retrieveEmissionsReport(rptInfo.getEmissionsRptId(), false);
                        getEmissionsReportService().reportFacilityMatch(ff, report, locatedScRpts);
                    } catch(RemoteException re) {
                        logger.error("Failed on retrieveEmissionsReport(): report " + rptInfo.getEmissionsRptId() + 
                                " for facility " + facility.getFacilityId() +
                                " and year " + year, re);
                        // Mark missing data
                        for(int i = firstFacRow; i <= lastFacRow; i++) {
                            ArrayList<String> row = datagrid.get(i);
                            row.set(yearOriginIndex, "ERROR");
                        }
                        continue;
                    }
                    if(report == null) {
                        logger.error("Failed on retrieveEmissionsReport(): report " + rptInfo.getEmissionsRptId() + 
                                " for facility " + facility.getFacilityId() +
                                " and year " + year);
                        // Mark missing data
                        for(int i = firstFacRow; i <= lastFacRow; i++) {
                            ArrayList<String> row = datagrid.get(i);
                            row.set(yearOriginIndex, "ERROR");
                        }
                        continue;
                    }

                        // get rid of groups
                        try {
                            erBO.explodeGroupsLocatePeriodNames(facility, report);
                        } catch(RemoteException re) {
                            logger.error("Failed on explodeGroupsLocatePeriodNames(): report " + rptInfo.getEmissionsRptId() + 
                                    " for facility " + facility.getFacilityId() +
                                    " and year " + year, re);
                            // Mark missing data
                            for(int i = firstFacRow; i <= lastFacRow; i++) {
                                ArrayList<String> row = datagrid.get(i);
                                row.set(yearOriginIndex, "ERROR");
                            }
                            continue;
                        }

                    for(int i = firstFacRow; i <= lastFacRow; i++) {
                        // Set the reporting category in all rows
                        ArrayList<String> row = datagrid.get(i);
                        int pollutantIndexOrig = yearOriginIndex;
                        row.set(pollutantIndexOrig++, null);
                    }
                    highestRptLevel = 3;
                    if(facTots && facRow != null) {
                        // Go through all the facility level pollutants
                        int pollutantIndexOrig = yearOriginIndex;
                        pollutantIndexOrig++;  // category has already been set.
                        ArrayList<EmissionRow> reportEmissions = null;
                        try {
                            // get report rollup of emissions
                            reportEmissions = EmissionRow.getEmissions(report, false,
                                    true, new Integer(1), locatedScRpts, logger, false);
                        } catch(ApplicationException ae) {
                            logger.error("Failed, Application Exception for report " + report.getEmissionsRptId(), ae);
                            // mark missing data
                            for(int j = 0; j < allPollutants.size(); j++) {
                                facRow.set(pollutantIndexOrig + j, "ERROR");
                            }
                        }
                        if(columnPid) {
                            pollutantIndexOrig++;
                        }
                        if(columnPdesc) {
                            pollutantIndexOrig++;
                        }
                        if(columnCE) {
                            pollutantIndexOrig = pollutantIndexOrig + 2;
                        }
                        //if(columnPtiPermit) {
                        //    pollutantIndexOrig = pollutantIndexOrig + 4;
                        //}
                        if(columnTvPtoPermit) {
                            pollutantIndexOrig = pollutantIndexOrig + 4;
                        }
                        if(columnPtioPermit) {
                            pollutantIndexOrig = pollutantIndexOrig + 4;
                        }
                        //if(columnRegistrationPermit) {
                        //    pollutantIndexOrig = pollutantIndexOrig + 2;
                        //}
                        //if(columnPbrPermit) {
                        //    pollutantIndexOrig = pollutantIndexOrig + 2;
                        //}

                        // Fill in faility level emissions
                        // go through each pollutant for this year and insert into facility row
                        for(int j = 0; j < allPollutants.size(); j++) {
                            // look for pollutant [j]
                            String tot = null;
                            for(EmissionRow er : reportEmissions) {
                                if(er.getPollutantCd().equals(allPollutants.get(j))) {
                                    tot = er.getTotalEmissions();
                                    if(tot == null) {
                                        tot = er.getFugitiveEmissions();
                                        if(tot == null) {
                                            tot = er.getStackEmissions();
                                        }
                                    }
                                    // convert if needed.
                                    if(tot != null && !er.getEmissionsUnitNumerator().equals(EmissionUnitReportingDef.TONS)) {
                                        double v = EmissionUnitReportingDef.convert(
                                                er.getEmissionsUnitNumerator(), er.getStackEmissionsV(),
                                                EmissionUnitReportingDef.TONS);
                                        double sum = v;
                                        v = EmissionUnitReportingDef.convert(
                                                er.getEmissionsUnitNumerator(), er.getFugitiveEmissionsV(),
                                                EmissionUnitReportingDef.TONS);
                                        sum = sum + v;
                                        tot = EmissionsReport.numberToString(sum);
                                    }
                                    facRow.set(pollutantIndexOrig + j, tot);
                                    break;
                                }
                            }
                        }
                    }
                } // if(ndx >= 0)
                if(euTots) {
                    // For this report go through all the facility rows locating EU emissions in report
                    int startingPoint = firstFacRow;
                    if(facTots) {
                        startingPoint++;
                    }
                    Integer lastCid = new Integer(-1);
                    EmissionUnit eu = null;
                    for(int i = startingPoint; i <= lastFacRow; i++) {
                        ArrayList<String> row = datagrid.get(i);
                        Integer cId = Integer.valueOf(row.get(corrIdIndex));
                        if(!lastCid.equals(cId)) {
                            if(ff != null) {
                                eu = ff.getMatchingEmissionUnit(cId);
                            }
                            lastCid = cId;
                        }
                        // For this EU or SCC go through all the pollutants
                        int pollutantIndexOrig = yearOriginIndex;
                        pollutantIndexOrig++;  // category has already been set.
                        ArrayList<EmissionRow> emissions = null;
                        // are we at the EU total row
                        boolean onEuRow = false;
                        EmissionsReportEU rEu = null;
                        if(row.get(processSccIndex).equals(EU_TOTAL)) {
                            onEuRow = true;
                            if(columnPid) {
                                pollutantIndexOrig++;
                            }
                            if(columnPdesc) {
                                pollutantIndexOrig++;
                            }
                            if(columnCE) {
                                pollutantIndexOrig = pollutantIndexOrig + 2;
                            }
//                            if(columnPtiPermit || columnTvPtoPermit ||columnPtioPermit ||
//                                    columnRegistrationPermit || columnPbrPermit) {
//                                if(ndx >= 0) {
//                                    getPermAppInfo(cId, year);
//                                } else {
//                                    getPermAppInfo(cId, 0);
//                                }
//                                if(columnPtiPermit) {
//                                    row.set(pollutantIndexOrig++, strPti.toString());
//                                    row.set(pollutantIndexOrig++, strPtiDate.toString());
//                                    row.set(pollutantIndexOrig++, strPtiApp.toString());
//                                    row.set(pollutantIndexOrig++, strPtiAppDate.toString());
//                                }
//                                if(columnTvPtoPermit) {
//                                    row.set(pollutantIndexOrig++, strTvPto.toString());
//                                    row.set(pollutantIndexOrig++, strTvPtoDate.toString());
//                                    row.set(pollutantIndexOrig++, strTvPtoApp.toString());
//                                    row.set(pollutantIndexOrig++, strTvPtoAppDate.toString());
//                                }
//                                if(columnPtioPermit) {
//                                    row.set(pollutantIndexOrig++, strPtio.toString());
//                                    row.set(pollutantIndexOrig++, strPtioDate.toString());
//                                    row.set(pollutantIndexOrig++, strPtioApp.toString());
//                                    row.set(pollutantIndexOrig++, strPtioAppDate.toString());
//                                }
//                                if(columnRegistrationPermit) {
//                                    row.set(pollutantIndexOrig++, strReg.toString());
//                                    row.set(pollutantIndexOrig++, strRegDate.toString());
//                                }
//                                if(columnPbrPermit) {
//                                    row.set(pollutantIndexOrig++, strPbr.toString());
//                                    row.set(pollutantIndexOrig++, strPbrDate.toString());
//                                }
//                            } else {
                                //if(columnPtiPermit) {
                                //    pollutantIndexOrig = pollutantIndexOrig + 4;
                                //}
                                if(columnTvPtoPermit) {
                                    pollutantIndexOrig = pollutantIndexOrig + 4;
                                }
                                if(columnPtioPermit) {
                                    pollutantIndexOrig = pollutantIndexOrig + 4;
                                }
                                //if(columnRegistrationPermit) {
                                //    pollutantIndexOrig = pollutantIndexOrig + 2;
                                //}
                                //if(columnPbrPermit) {
                                //    pollutantIndexOrig = pollutantIndexOrig + 2;
                               // }
                         //   } // if(columnPtiPermit || columnTvPtoPermit ||columnPtioPermit || columnRegistrationPermit || columnPbrPermit)
                            
                            if(ndx < 0 || report == null) continue; // current facility and no reports; skip this row
                            // Retrieve emission info for this EU 
                            rEu = report.getEu(cId);
                            if(rEu == null) {
                                // Skip this row for this report year
                                continue;
                            }
                            try {
                                // get EU rollup of emissions
                                emissions = EmissionRow.getEmissions(rEu, false,
                                        new Integer(1), locatedScRpts, logger);
                            } catch(ApplicationException ae) {
                                logger.error("Failed, Application Exception for report " + report.getEmissionsRptId() + 
                                        " and EU with correlation id " + cId);
                                // mark missing data
                                for(int j = 0; j < allPollutants.size(); j++) {
                                    row.set(pollutantIndexOrig + j, "ERROR");
                                }
                                continue;
                            }
                        } else {  // SCC level
                            // Look for process in facility eu
                            EmissionProcess ep = eu.findProcess(row.get(processSccIndex));
                            String pId = "";
                            String pDesc = "";
                            String pCeT = "";
                            String pCeD = "";
                            if(ep != null) {
                                pId = ep.getProcessId();
                                pDesc = ep.getEmissionProcessNm();
                            }
                            if(ep != null && columnCE) {
                                StringBuffer sbT = new StringBuffer();
                                StringBuffer sbD = new StringBuffer();
                                for(ControlEquipment ce : ep.getAllControlEquipments()) {
                                    String type = ContEquipTypeDef.getData().getItems().getItemDesc(ce.getEquipmentTypeCd());
                                    Timestamp ts = ce.getContEquipInstallDate();
                                    String str;
                                    if(ts != null) {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(ts);
                                        int y = cal.get(Calendar.YEAR);
                                        int m = cal.get(Calendar.MONTH) + 1;
                                        int d = cal.get(Calendar.DAY_OF_MONTH);
                                        str = Integer.toString(m) + "/" + Integer.toString(d) + "/" + Integer.toString(y);
                                    } else str = "--/--/--";
                                    sbT.append(ce.getControlEquipmentId() + "(" + type + ") ");
                                    if(sbD.length() > 0) sbD.append(" ");
                                    sbD.append(str);
                                }

                                pCeT = sbT.toString();
                                pCeD = sbD.toString();
                            }
                            if(columnPid) {
                                row.set(pollutantIndexOrig++, pId);
                            }
                            if(columnPdesc) {
                                row.set(pollutantIndexOrig++, pDesc);
                            }
                            if(columnCE) {
                                row.set(pollutantIndexOrig++, pCeT);
                                row.set(pollutantIndexOrig++, pCeD);
                            }
                            if(report == null) continue;  // go to next row
                            rEu = report.getEu(cId);
                            if(rEu != null) {
                                EmissionsReportPeriod erp = rEu.getPeriod(row.get(processSccIndex));
                                // process not in report so no emissions.
                                if(erp != null) {
                                    //if(columnPtiPermit) {
                                    //    pollutantIndexOrig = pollutantIndexOrig + 4;
                                    //}
                                    if(columnTvPtoPermit) {
                                        pollutantIndexOrig = pollutantIndexOrig + 4;
                                    }
                                    if(columnPtioPermit) {
                                        pollutantIndexOrig = pollutantIndexOrig + 4;
                                    }
                                    //if(columnRegistrationPermit) {
                                    //    pollutantIndexOrig = pollutantIndexOrig + 2;
                                    //}
                                    //if(columnPbrPermit) {
                                    //    pollutantIndexOrig = pollutantIndexOrig + 2;
                                    //}
                                    try {
                                        erp.setFireRows(null);
                                        emissions = EmissionRow.getEmissions(year, erp, locatedScRpts,
                                                 false, true, new Integer(1), logger);
                                    } catch(ApplicationException ae) {
                                        logger.error("Failed, Application Exception for report " + report.getEmissionsRptId() + 
                                                " and EU with correlation id " + cId);
                                        // mark missing data
                                        for(int j = 0; j < allPollutants.size(); j++) {
                                            row.set(pollutantIndexOrig + j, "ERROR");
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                            // Fill in emissions
                            if(onEuRow && rEu.getPeriods().size() == 0) {
                                // no processes so no emissions, indicate why
                                String reason = "???";
                                if(rEu.isZeroEmissions()) reason = "NoOper";
                                else if(rEu.isBelowRequirements()) {
                                    reason = "Low";
                                    // DO NOT specify the exact reason for not reporting
//                                    if(eu.getExemptStatusCd().equals("dem")) reason = "deMin";
//                                    else if(eu.getExemptStatusCd().equals("exm")) reason = "PermX";
                                }
                                for(int j = 0; j < allPollutants.size(); j++) {
                                    row.set(pollutantIndexOrig + j, reason);
                                }
                            } else if(emissions != null) {
                                // go through each pollutant for this year and insert into facility row
                                for(int j = 0; j < allPollutants.size(); j++) {

                                    // look for pollutant [j]
                                    String tot = null;
                                    for(EmissionRow er : emissions) {
                                        if(er.getPollutantCd().equals(allPollutants.get(j))) {
                                            tot = er.getTotalEmissions();
                                            if(tot == null) {
                                                tot = er.getFugitiveEmissions();
                                                if(tot == null) {
                                                    tot = er.getStackEmissions();
                                                }
                                            }
                                            // convert if needed.
                                            if(tot != null && !er.getEmissionsUnitNumerator().equals(EmissionUnitReportingDef.TONS)) {
                                                double v = EmissionUnitReportingDef.convert(
                                                        er.getEmissionsUnitNumerator(), er.getStackEmissionsV(),
                                                        EmissionUnitReportingDef.TONS);
                                                double sum = v;
                                                v = EmissionUnitReportingDef.convert(
                                                        er.getEmissionsUnitNumerator(), er.getFugitiveEmissionsV(),
                                                        EmissionUnitReportingDef.TONS);
                                                sum = sum + v;
                                                tot = EmissionsReport.numberToString(sum);
                                            }
                                            row.set(pollutantIndexOrig + j, tot);
                                            break;
                                        }
                                    }
                                }
                            }
                    } // for(int i = startingPoint; i <= lastFacRow; i++)
                } //if(euTots)
            } //for(int ndx= -1; ndx < rptInfoList.size(); ndx++)
            
            if(highestRptLevel >= rptLevel) {
                // keep the facility; otherwise all rows lost.
                if(removeNull) {
                    facilityEmissions.postProcess();
                }
                ArrayList<RowContainer> rowList = new ArrayList<RowContainer>(datagrid.size());
                int pI = processSccIndex;
                if(!columnProcess) {
                    pI = -1;
                }
                for(ArrayList<String> row : datagrid) {
                    rowList.add(new RowContainer(euIdColPos, pI, row));
                }
                datagrid = new ArrayList<ArrayList<String>>();
                Collections.sort(rowList);
                for(RowContainer row : rowList) {
                    datagridAll.add(row.getRow()); 
                }
                
            }

//            ArrayList<RowContainer> rowList = new ArrayList<RowContainer>(datagrid.size());
//            int pI = processSccIndex;
//            if(!columnProcess) {
//                pI = -1;
//            }
//            for(ArrayList<String> row : datagrid) {
//                rowList.add(new RowContainer(euIdColPos, pI, row));
//            }
//            
//            Collections.sort(rowList);
//            for(RowContainer row : rowList) {
//                datagridAll.add(row.getRow()); 
//            }
//            datagrid = new ArrayList<ArrayList<String>>();
//            if(highestRptLevel < rptLevel) {
//                facilityEmissions.removeFacility(firstFacRow);
//            } else if(removeNull) {
//                // Remove rows for this facility
//                // Start with firstFacRow and go to end of datagrid.
//                facilityEmissions.postProcess(firstFacRow);
//            }
            candidateIncludedStr = facilities.length + " candidate facilities; " + numProcessed + " processed;"
            + rowCount + " rows generated;  " + datagridAll.size() + " rows kept";
        } //for(Integer iii : facilities)
        // Make sure count is correct.  The for loop does contain continue statements to skip code.
        candidateIncludedStr = facilities.length + " candidate facilities; " + numProcessed + " processed;"
        + rowCount + " rows generated;  " + datagridAll.size() + " rows kept";
        
        datagrid = datagridAll;
    }

    protected ArrayList<String> createFacilityRows(int year, Facility facility, int firstFacRow, Integer iii, ArrayList<Facility> rptFacList) {
        county =  null;
        if(facility.getPhyAddr() != null) {
            if(facility.getPhyAddr().getCountyCd() != null) {
                county = County.getData().getItems().getItemDesc(facility.getPhyAddr().getCountyCd());
            }
        }

        if(columnSic) {
            StringBuffer sicBuf = new StringBuffer();
            StringBuffer sicBufDesc = new StringBuffer();
            String desc;
            for(String s : facility.getSicCds()) {
                sicBuf.append(s + " ");
                if(columnSicNaicsDesc) {
                    desc = SICDef.getData().getItems().getItemDesc(s);
                    if(desc == null) desc = " ";
                    sicBufDesc.append(desc);
                }
            }
            facSic = sicBuf.toString(); //EmissionReportsRealDef.getData().getItems().getItemDesc(eri.getCategory());
            facSicDesc = sicBufDesc.toString();
        }

        if(columnNaics) {
            StringBuffer naicsBuf = new StringBuffer();
            StringBuffer naicsBufDesc = new StringBuffer();
            for(String s : facility.getNaicsCds()) {
                naicsBuf.append(s + " ");
                String desc;
                if(columnSicNaicsDesc) {
                    desc = NAICSDef.getData().getItems().getItemDesc(s);
                    if(desc == null) desc = " ";
                    naicsBufDesc.append(desc);
                }
            }
            facNaics = naicsBuf.toString();
            facNaicsDesc = naicsBufDesc.toString();
        }

        EmissionUnit[] eus = null;
        eus = facility.getEmissionUnits().toArray(new EmissionUnit[0]);

        if(eus == null) {
            logger.error("Failed to read facility EUs with fpId " + iii);
            // Mark missing EU data
            ArrayList<String> r = new ArrayList<String>(totLabels);
            initRow(r, totLabels);
            r.set(facilityNameIndex, "ERROR Reading EUs for FpId " + iii);
            datagrid.add(r);
            return null;
        }

        // Process Report data driven by Facility EUs
        // Do one facility at a time and within the facility
        // process each yearly report once.
        // The first pass creates all the facility rows and the
        // following passes (processing reports) inserts values into existing rows.
        // There may be a last pass that removes useless rows.
        // Finally go to the next facility

        // create rows for facility and EU
        firstFacRow = datagrid.size();
        ArrayList<String> facRow =  null;
        if(facTots) {  // facility totals only
            facRow = createFacilityRow(year, facility, "facil");
            datagrid.add(facRow);
        }

        if(euTots) {
            for(EmissionUnit eu : eus) {
                boolean includeEu = false;
                // Include EUs that are shutdown in the right timeframe
                if(sdEUs && eu.getOperatingStatusCd().equals(EuOperatingStatusDef.SD)) {
                    if(eu.getEuShutdownDate() == null) {
                        includeEu = true;
                    } else {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(eu.getEuShutdownDate());
                        int y = cal.get(Calendar.YEAR);
                        if(y >= firstYear || y <= lastYear) {
                            includeEu = true;
                        }
                    }
                }
                if(opEUs && (eu.getOperatingStatusCd().equals(EuOperatingStatusDef.OP) ||
                        eu.getOperatingStatusCd().equals(EuOperatingStatusDef.SD))) {
                    includeEu = true;
                }


                if(includeEu && selectedEUs.size() > 0) {
                    if(!"all".equals(selectedEUs.get(0))) {
                        String euName = eu.getEpaEmuId();
                        if(euName.length() != 4) {
                            includeEu = false;
                        } else {
                            if(!("0123456789".contains(euName.substring(1, 2)) &&
                                    "0123456789".contains(euName.substring(2, 3)) &&
                                    "0123456789".contains(euName.substring(3, 4)))) {
                                includeEu = false;
                            } else {
                                includeEu = false;
                                for(String s : selectedEUs) {
                                    if(s.substring(0, 1).equals(euName.substring(0, 1))) {
                                        includeEu = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if(includeEu && rowEuTot) {
                    // Create a row for the EU for the EU total
                    ArrayList<String> row = createRow(year, facility, eu);
                    datagrid.add(row);


                    int pollutantIndexOrig = numFacilityLabels + numEULabels + numSccLabels;
                    pollutantIndexOrig++;  // category will be set later
                    if(columnPid) {
                        pollutantIndexOrig++;
                    }
                    if(columnPdesc) {
                        pollutantIndexOrig++;
                    }
                    if(columnCE) {
                        pollutantIndexOrig = pollutantIndexOrig + 2;
                    }
                    if(
                    		//columnPtiPermit || 
                    		columnTvPtoPermit ||
                    		columnPtioPermit 
                    		//||
                            //columnRegistrationPermit || columnPbrPermit
                            ) {
                        Integer cId = eu.getCorrEpaEmuId();
                        getPermAppInfo(cId, year);
                        //if(columnPtiPermit) {
                            //row.set(pollutantIndexOrig++, strPti.toString());
                            //row.set(pollutantIndexOrig++, strPtiDate.toString());
                            //row.set(pollutantIndexOrig++, strPtiApp.toString());
                            //row.set(pollutantIndexOrig++, strPtiAppDate.toString());
                        //}
                        if(columnTvPtoPermit) {
                            row.set(pollutantIndexOrig++, strTvPto.toString());
                            row.set(pollutantIndexOrig++, strTvPtoDate.toString());
                            row.set(pollutantIndexOrig++, strTvPtoApp.toString());
                            row.set(pollutantIndexOrig++, strTvPtoAppDate.toString());
                        }
                        if(columnPtioPermit) {
                            row.set(pollutantIndexOrig++, strPtio.toString());
                            row.set(pollutantIndexOrig++, strPtioDate.toString());
                            row.set(pollutantIndexOrig++, strPtioApp.toString());
                            row.set(pollutantIndexOrig++, strPtioAppDate.toString());
                        }
                        //if(columnRegistrationPermit) {
                        //    row.set(pollutantIndexOrig++, strReg.toString());
                        //    row.set(pollutantIndexOrig++, strRegDate.toString());
                        //}
                        //if(columnPbrPermit) {
                        //    row.set(pollutantIndexOrig++, strPbr.toString());
                        //    row.set(pollutantIndexOrig++, strPbrDate.toString());
                        //}
                    } 
                }
                if(includeEu && columnProcess) {
                    // generate process rows.  Look at all the profiles with the reports at this EU to get all SCCs
                    // used.
                    TreeSet<String> sccs = null;
                    //  for(Facility f : rptFacList) {
//                  if(f == null) continue;
//                  EmissionUnit eux = f.getMatchingEmissionUnit(eu.getCorrEpaEmuId());
//                  if(eux == null){
//                  // This EU does not exist in the earlier profile
//                  continue;
//                  }
                    sccs = new TreeSet<String>();
                    for(EmissionProcess ep : eu.getEmissionProcesses()) {
                        if(ep.getSccId() != null) {
                            sccs.add(ep.getSccId());
                        } else { 
                            sccs.add("-no scc-");
                        }
                    }
                    // }
                    for(String scc : sccs) {
                        ArrayList<String> sccRow = createRow(year, facility, eu, scc);
                        datagrid.add(sccRow);
                    }
                } // if(includeEu && columnProcess)
            } // for(EmissionUnit eu : eus)
        } // if(euTots)
        return facRow;
    }
    
//    protected void removeFacility(int firstRow) {
//        // Remove all rows 
//        for(int i = firstRow; i < datagrid.size(); i++) {
//            datagrid.remove(i);
//            i = i - 1;
//        }
//    }

    protected void postProcess() {
        int yearOriginIndex = numFacilityLabels + numEULabels + numSccLabels;
        
        int pollutantIndexOrig = yearOriginIndex;
        pollutantIndexOrig++;  // category has already been set.
        if(columnPid) {
            pollutantIndexOrig++;
        }
        if(columnPdesc) {
            pollutantIndexOrig++;
        }
        if(columnCE) {
            pollutantIndexOrig = pollutantIndexOrig + 2;
        }
        //if(columnPtiPermit) {
        //    pollutantIndexOrig = pollutantIndexOrig + 4;
       //}
        if(columnTvPtoPermit) {
            pollutantIndexOrig = pollutantIndexOrig + 4;
        }
        if(columnPtioPermit) {
            pollutantIndexOrig = pollutantIndexOrig + 4;
        }
        //if(columnRegistrationPermit) {
        //    pollutantIndexOrig = pollutantIndexOrig + 2;
        //}
        //if(columnPbrPermit) {
        //    pollutantIndexOrig = pollutantIndexOrig + 2;
        //}
        // Remove rows with no emissions for any of the years.
        for(int i = 0; i < datagrid.size(); i++) {
            ArrayList<String> row = datagrid.get(i);
            if(row.get(0).equals("curr")) continue; // don't delete any current rows
            if(row.get(yearOriginIndex).equals("facil")) continue; // don't delete rows for facility totals
            boolean deleteRow = true;
            // Remove row is no emissions specified anywhere on row
            bigLoop:
                if(allPollutants != null && allPollutants.size() != 0) {
                    for(int j = 0; j < allPollutants.size(); j++) {
                        if(row.get(pollutantIndexOrig + j) != null && 
                                row.get(pollutantIndexOrig + j).length() != 0 &&
                                !row.get(pollutantIndexOrig + j).matches("0*\\.?+0*") &&
                                !row.get(pollutantIndexOrig + j).equals("NoOper") &&
                                !row.get(pollutantIndexOrig + j).equals("Low")) {
                            deleteRow = false;
                            break bigLoop;
                        }
                    }
                }
            if(deleteRow) {
                datagrid.remove(i);
                i = i - 1;  // adjust because position i needs to checked again
            }
        }
    }

    protected void initRow(ArrayList<String> l, int size) {
        for(int i = 0; i < size; i++) {
            l.add(null);
        }
        rowCount++;
    }
    
    ArrayList<String> createRow(int yr, Facility f, EmissionUnit eu, String scc) {
        ArrayList<String> r = createRow(yr, f, eu);
        int curLabel = processSccIndex;
        r.set(curLabel, scc);
        curLabel++;
        if(columnSccDesc) {
            SccCode sc = SccCodesDef.getSccCode(scc);
            String l1 = "";
            String l2 = "";
            String l3 = "";
            String l4 = "";
            if(sc != null) {
                l1 = sc.getSccLevel1Desc();
                l2 = sc.getSccLevel2Desc();
                l3 = sc.getSccLevel3Desc();
                l4 = sc.getSccLevel4Desc();
            }
            r.set(curLabel, l1);
            curLabel++;
            r.set(curLabel, l2);
            curLabel++;
            r.set(curLabel, l3);
            curLabel++;
            r.set(curLabel, l4);
            curLabel++;
        }
        return r;
    }

    ArrayList<String> createRow(int yr, Facility f, EmissionUnit eu) {
        ArrayList<String> r = createFacilityRow(yr, f);
        int curLabel = numFacilityLabels;
        String op = null;
        if(eu.getOperatingStatusCd() != null) {
            op = EuOperatingStatusDef.getData().getItems().getItemDesc(eu.getOperatingStatusCd());
        }
        r.set(curLabel++, eu.getEpaEmuId());
        r.set(curLabel++, op);
        if(columnEuDapc) {
            r.set(curLabel++, eu.getEuDesc());
        }
        if(columnEuCompany) {
            r.set(curLabel++, eu.getRegulatedUserDsc());
        }
        Calendar cal = Calendar.getInstance();
        if(columnEuInstallDates) {
            if(eu.getEuInitInstallDate() != null) {
                cal.setTime(eu.getEuInitInstallDate());
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH) + 1;
                int d = cal.get(Calendar.DAY_OF_MONTH);
                r.set(curLabel, Integer.toString(m) + "/" + Integer.toString(d) + "/" + Integer.toString(y));
            }
            curLabel++;
            if(eu.getEuInstallDate() != null) {
                cal.setTime(eu.getEuInstallDate());
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH) + 1;
                int d = cal.get(Calendar.DAY_OF_MONTH);
                r.set(curLabel, Integer.toString(m) + "/" + Integer.toString(d) + "/" + Integer.toString(y));
            }
            curLabel++;
            if(eu.getEuStartupDate() != null) {
                cal.setTime(eu.getEuStartupDate());
                int y = cal.get(Calendar.YEAR);
                int m = cal.get(Calendar.MONTH) + 1;
                int d = cal.get(Calendar.DAY_OF_MONTH);
                r.set(curLabel, Integer.toString(m) + "/" + Integer.toString(d) + "/" + Integer.toString(y));
            }
            curLabel++;
        }
        if(eu.getEuShutdownDate() != null) {
            cal.setTime(eu.getEuShutdownDate());
            int y = cal.get(Calendar.YEAR);
            int m = cal.get(Calendar.MONTH) + 1;
            int d = cal.get(Calendar.DAY_OF_MONTH);
            r.set(curLabel, Integer.toString(m) + "/" + Integer.toString(d) + "/" + Integer.toString(y));
        }
        curLabel++;
        
        curLabel++;  // skip field for "Grouped"
        r.set(curLabel++, eu.getCorrEpaEmuId().toString());
        r.set(curLabel++, EU_TOTAL);
        return r;
    }
    
    ArrayList<String> createFacilityRow(int yr, Facility f, String name) {
        ArrayList<String> r = createFacilityRow(yr, f);
        int curLabel = euIdColPos;
        r.set(curLabel, name);
        return r;
    }
    
    ArrayList<String> createFacilityRow(int year, Facility f) {
        ArrayList<String> r = new ArrayList<String>(totLabels);
        initRow(r, totLabels);
        int curLabel = 0;
        if(year == 0) {
            r.set(curLabel++, "curr");
        } else {
            r.set(curLabel++, Integer.toString(year));
        }
        String op = null;
        if(f.getOperatingStatusCd() != null) {
            op = OperatingStatusDef.getData().getItems().getItemDesc(f.getOperatingStatusCd());
        }
        r.set(curLabel++, op);
        r.set(curLabel++, f.getFacilityId());
        r.set(curLabel++, f.getName());
        r.set(curLabel++, county);
        if(columnSic) {
            r.set(curLabel++, facSic);
        }
        if(columnSic && columnSicNaicsDesc) {
            r.set(curLabel++, facSicDesc);
        }
        if(columnNaics) {
            r.set(curLabel++, facNaics);
        }
        if(columnNaics && columnSicNaicsDesc) {
            r.set(curLabel++, facNaicsDesc);
        }
        r.set(curLabel++, null);
        r.set(processSccIndex, " - "); //  "Fac Total"
        return r;
    }

    protected void determinePollutantsPerYear() {
        HashMap<String, EmissionRow> allYearsRows = new HashMap<String, EmissionRow>();
        allPollutants = new ArrayList<String>();
        for(int year = firstYear; year <= lastYear; year++) {
            // need to define order also
            ArrayList<String> yearlyPollutants = new ArrayList<String>(10);
            HashMap<String, EmissionRow> tRows = new HashMap<String, EmissionRow>();
            ReportTemplates rt = getReportTemplate(year, false);
            // Is no report definition for this year, skip
            if(rt != null) {
                SCEmissionsReport scer;
                SCPollutant[] sp;
                if(ferPolls) {
                    //scer = rt.getScFER();
                    scer = rt.getSc();
                    sp = scer.getPollutants();
                    for(SCPollutant p : sp) {
                        EmissionRow er = new EmissionRow();
                        er.setPollutantCd(p.getPollutantCd());
                        er.setOrder(p.getDisplayOrder());
                        // Don't keep duplicates
                        tRows.put(er.getPollutantCd(), er);
                        allYearsRows.put(er.getPollutantCd(), er);
                    }
                }
                if(esPolls) {
                    //scer = rt.getScES();
                    scer = rt.getSc();
                    sp = scer.getPollutants();
                    for(SCPollutant p : sp) {
                        EmissionRow er = new EmissionRow();
                        er.setPollutantCd(p.getPollutantCd());
                        er.setOrder(p.getDisplayOrder());
                        // Don't keep duplicates
                        tRows.put(er.getPollutantCd(), er);
                        allYearsRows.put(er.getPollutantCd(), er);
                    }
                }
                if(eisPolls) {
                    //scer = rt.getScEIS();
                    scer = rt.getSc();
                    sp = scer.getPollutants();
                    for(SCPollutant p : sp) {
                        EmissionRow er = new EmissionRow();
                        er.setPollutantCd(p.getPollutantCd());
                        er.setOrder(p.getDisplayOrder());
                        // Don't keep duplicates
                        tRows.put(er.getPollutantCd(), er);
                        allYearsRows.put(er.getPollutantCd(), er);
                    }
                }
            }
            
            if(extraPollutantCd != null) {
                if(!tRows.containsKey(extraPollutantCd)) {
                    EmissionRow er = new EmissionRow();
                    er.setPollutantCd(extraPollutantCd);
                    er.setOrder(1000000000);
                    tRows.put(er.getPollutantCd(), er);
                    allYearsRows.put(er.getPollutantCd(), er);
                }
            }

            rt = getReportTemplate(year, false);
            // Is no report definition for this year, skip
            if(rt != null) {
                SCEmissionsReport scer;
                SCPollutant[] sp;
                if(ferPolls) {
                    //scer = rt.getScFER();
                    scer = rt.getSc();
                    sp = scer.getPollutants();
                    for(SCPollutant p : sp) {
                        EmissionRow er = new EmissionRow();
                        er.setPollutantCd(p.getPollutantCd());
                        er.setOrder(p.getDisplayOrder());
                        // Don't keep duplicates
                        tRows.put(er.getPollutantCd(), er);
                        allYearsRows.put(er.getPollutantCd(), er);
                    }
                }
                if(esPolls) {
                    //scer = rt.getScES();
                    scer = rt.getSc();
                    sp = scer.getPollutants();
                    for(SCPollutant p : sp) {
                        EmissionRow er = new EmissionRow();
                        er.setPollutantCd(p.getPollutantCd());
                        er.setOrder(p.getDisplayOrder());
                        // Don't keep duplicates
                        tRows.put(er.getPollutantCd(), er);
                        allYearsRows.put(er.getPollutantCd(), er);
                    }
                }
                if(eisPolls) {
                    //scer = rt.getScEIS();
                    scer = rt.getSc();
                    sp = scer.getPollutants();
                    for(SCPollutant p : sp) {
                        EmissionRow er = new EmissionRow();
                        er.setPollutantCd(p.getPollutantCd());
                        er.setOrder(p.getDisplayOrder());
                        // Don't keep duplicates
                        tRows.put(er.getPollutantCd(), er);
                        allYearsRows.put(er.getPollutantCd(), er);
                    }
                }
            }

            // Take out of tRows because that gets each one once and
            // put into TreeSet to get ordering
            // That is the order to put pollents into the datagrid
            TreeSet<EmissionRow> ordered = new TreeSet<EmissionRow>(tRows.values());
            for(EmissionRow r: ordered) {
                yearlyPollutants.add(r.getPollutantCd());
            }
        }
        TreeSet<EmissionRow> allOrdered = new TreeSet<EmissionRow>(allYearsRows.values());
        for(EmissionRow r: allOrdered) {
            allPollutants.add(r.getPollutantCd());
        }
    }

    protected ReportTemplates getReportTemplate(int year, boolean update) {
        ReportTemplates locatedScRpts = templates.get(year);
        if(!update) {
            if(locatedScRpts == null) {
                // If date too early, then there will be no report definitions
                if(year >= 1993) {
                    try {
                        locatedScRpts = erBO.retrieveSCEmissionsReports(year, "AC",
                                "F000555"); // dummy value since this code is not used in IMPACT
                    } catch(RemoteException re) {
                        logger.error("Failed on retrieveSCEmissionsReports(): for year " +
                                year);
                    }
                    if(locatedScRpts == null) {
                        logger.error("Failed on retrieveSCEmissionsReports(): for year " +
                                year);
                    } else {
                        for(String s : locatedScRpts.getDisplayMsgs()) {
                            logger.error("(Failed?): Display Msg for year " +
                                    year + ": " + s);
                        }
                        if(locatedScRpts.isFailed()) {
                            logger.error("Failed to locate report definitions for year " +
                                    year);
                        } else {
                            templates.put(year, locatedScRpts);
                        }
                    }
                }
            }
        }
        return locatedScRpts;
    }

    protected EmissionsReportSearch getLatest(EmissionsReportSearch[] rpts, int year) {
        EmissionsReportSearch rptInfo = null;
        Timestamp ts = null;
        for(int i = rpts.length - 1; i >= 0; i--) {
            EmissionsReportSearch r = rpts[i];
            if(r.getYear().intValue() != year) {
                continue;
            }
            // Get newest approved report for the year
            if(r.getReportingState().equals(ReportReceivedStatusDef.DOLAA_APPROVED) ||
                    r.getReportingState().equals(ReportReceivedStatusDef.DOLAA_APPROVED_RR)) {
                if(rptInfo == null) {
                    rptInfo = r;
                    ts = r.getApprovedDate();
                } else {
                    if(r.getApprovedDate() != null) {
                        // if this has null date then it cannot be better
                        // than the one we already have.
                        if(ts != null) {
                            // Both have dates
                            if(ts.before(r.getApprovedDate())) {
                                rptInfo = r;
                                ts = r.getApprovedDate();
                            }
                        } else {
                            // Take the better report
                            rptInfo = r;
                            ts = r.getApprovedDate();
                        }
                    }
                }
            }
        }
        return rptInfo;
    }

    protected EmissionsReportSearch[] getEmissionsReports(String facilityId)  {
        EmissionsReportSearch[] reports = null;
        try {
            EmissionsReportSearch searchObj = new EmissionsReportSearch();
            searchObj.setFacilityId(facilityId);
            searchObj.setUnlimitedResults(true);
            reports = erBO.searchEmissionsReports(searchObj, false);
        } catch(RemoteException e) {
            logger.error("Failed on getEmissionsReports() for facility " + facilityId, e);
        }
        return reports;
    }

    protected  Integer[] getFacilities() {
        Integer f[] = null;
        try {
            f = erBO.searchFacilities(selectedCounties, null, facilityId, firstYear, lastYear, false);
        } catch(RemoteException e) {
            logger.error("Failed on searchFacilities()", e);
        }
        return f;
    }

    public int getFirstYear() {
        return firstYear;
    }

    public void setFirstYear(int firstYear) {
        this.firstYear = firstYear;
    }

    public int getLastYear() {
        return lastYear;
    }

    public void setLastYear(int lastYear) {
        this.lastYear = lastYear;
    }

    public boolean isNotInProgress() {
        return notInProgress;
    }

    public String getRefreshStr() {
        return refreshStr;
    }

    class RunReport extends Thread {
        public void run() {
            try {
                facilityEmissions.process();
            } catch(Exception e) {
                String s = "ERROR: facilityEmissions.process() failed with " + e.getMessage();
                logger.error(s, e);
                errorStr = s;
                notInProgress = true;
                refreshStr = " ";
            }
            notInProgress = true;
            refreshStr = " ";
        }
    }
    
    // Get permit application information
    void getPermAppInfo(Integer corrId, int year) {
        // if year is negative then do for current where year is the newest year requested.
        Timestamp begin = null;
        Timestamp end = null;
        if(year > 0) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, 0);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            begin = new Timestamp(cal.getTimeInMillis());
            cal.set(Calendar.MONTH, 11);
            cal.set(Calendar.DAY_OF_MONTH, 31);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            end = new Timestamp(cal.getTimeInMillis());
        }
        //strPbr = new StringBuffer();
        //strReg = new StringBuffer();
        strTvPto = new StringBuffer();
        strPtio = new StringBuffer();
        //strPti = new StringBuffer();
        //strPbrDate = new StringBuffer();
        //strRegDate = new StringBuffer();
        strTvPtoDate = new StringBuffer();
        strPtioDate = new StringBuffer();
        //strPtiDate = new StringBuffer();
        strTvPtoApp = new StringBuffer();
        strPtioApp = new StringBuffer();          
        //strPtiApp = new StringBuffer();
        strTvPtoAppDate = new StringBuffer();
        strPtioAppDate = new StringBuffer();
        //strPtiAppDate = new StringBuffer();
        for(FacilityPermitInfo fpi : permitInfo) {
            //Select permits
            if((fpi.getEffectiveDate() == null && year > 0) || (year > 0 && !fpi.getEffectiveDate().before(end))) continue;
            for(FacilityPermitEuInfo fpei : fpi.getEus()) {
                if(!fpei.getEuCorrId().equals(corrId)) continue;
                if((fpei.getRevocationDt() == null || (year > 0 && fpei.getRevocationDt().after(begin))) &&
                        (fpei.getTerminatedDt() == null || (year >0 && fpei.getTerminatedDt().after(begin))) &&
                        (fpei.getSupersededDt() == null || (year > 0 && fpei.getSupersededDt().after(begin)))) {
                    // keep this one
                    //if(columnRegistrationPermit && fpi.getPermitTypeCd().equals("REG")) {
                    //    if(strReg.length() > 0) strReg.append(" ");
                    //    strReg.append(fpi.getPermitNbr());
                    //    if(strRegDate.length() > 0) strRegDate.append(" ");
                    //    strRegDate.append(getDateStr(fpi.getEffectiveDate()));
                    //} else 
                	if(columnTvPtoPermit && fpi.getPermitTypeCd().equals("TVPTO")) {
                        if(strTvPto.length() > 0) strTvPto.append(" ");
                        strTvPto.append(fpi.getPermitNbr());
                        if(strTvPtoDate.length() > 0) strTvPtoDate.append(" ");
                        strTvPtoDate.append(getDateStr(fpi.getEffectiveDate()));
                        appendApplications(strTvPtoApp, fpi.getApplications());
                        appendApplicationDates(strTvPtoAppDate, fpi.getApplications());
                    } else if(
                    		//columnPtioPermit && fpi.getPermitTypeCd().equals("SPTO") ||
                            fpi.getPermitTypeCd().equals("NSR")) {
                        if(strPtio.length() > 0) strPtio.append(" ");
                        strPtio.append(fpi.getPermitNbr());
                        if(strPtioDate.length() > 0) strPtioDate.append(" ");
                        strPtioDate.append(getDateStr(fpi.getEffectiveDate()));
                        appendApplications(strPtioApp, fpi.getApplications());
                        appendApplicationDates(strPtioAppDate, fpi.getApplications());
                    //} else if(columnPtiPermit && fpi.getPermitTypeCd().equals("TVPTI")) {
                    //    if(strPti.length() > 0) strPti.append(" ");
                    //    strPti.append(fpi.getPermitNbr());
                    //    if(strPtiDate.length() > 0) strPtiDate.append(" ");
                    //    strPtiDate.append(getDateStr(fpi.getEffectiveDate()));
                    //    appendApplications(strPtiApp, fpi.getApplications());
                    //    appendApplicationDates(strPtiAppDate, fpi.getApplications());
                    }
                }
                break;
            }
        }
        removeTwoCharFromEnd(strTvPtoApp);
        removeTwoCharFromEnd(strTvPtoAppDate);
        removeTwoCharFromEnd(strPtioApp);
        removeTwoCharFromEnd(strPtioAppDate);
        //removeTwoCharFromEnd(strPtiApp);
        //removeTwoCharFromEnd(strPtiAppDate);

        //if(year == 0) {
       //     for(FacilityAppInfo fai : appInfo) {
                // look for PBR permits
                //if(!fai.getApplicationTypeCd().equals("PBR")) continue;
                //for(Integer cId : fai.getEuCorrIds()) {
                //    if(!cId.equals(corrId)) continue;
                //   strPbr.append(fai.getApplicationNbr() + " ");
                //    strPbrDate.append(getDateStr(fai.getReceivedDate()) + " ");
                //    break;
                //}
       //     }
        //} else {
        //    for(FacilityAppInfo fai : appInfo) {
                // look for PBR permits
                //if(!fai.getApplicationTypeCd().equals("PBR")) continue;
              //if(!"a".equals(fai.getDispositionFlag())) continue;
              //  if(!fai.getReceivedDate().before(end)) continue;

                //for(Integer cId : fai.getEuCorrIds()) {
                //    if(!cId.equals(corrId)) continue;
                //    strPbr.append(fai.getApplicationNbr() + " ");
                //   strPbrDate.append(getDateStr(fai.getReceivedDate()) + " ");
                //    break;
                //}
         //   }
        //}
    }
    
    public void appendNextStringSemiColon(StringBuffer sb1, StringBuffer sb2) {
        if(sb1.length() != 0 && sb2.length() != 0) sb1.append("; ");
        sb1.append(sb2);
    }
    
    public void appendNextStringBlank(StringBuffer sb1, StringBuffer sb2) {
        if(sb1.length() != 0 && sb2.length() != 0) sb1.append(" ");
        sb1.append(sb2);
    }
    
    public static void removeTwoCharFromEnd(StringBuffer sb) {
        if(sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length() - 1);
        }
    }
    
    public static void removeOneCharFromEnd(StringBuffer sb) {
        if(sb.length() > 1) {
            sb.delete(sb.length() - 1, sb.length() - 1);
        }
    }
    
    public static void appendApplications(StringBuffer sb, List<FacilityAppInfo> fais) {
        if(fais == null) return;
        if(fais.size() == 0) {
            sb.append("none; ");
            return;
        }
        for(FacilityAppInfo fai : fais) {
            if(sb.length() > 0) sb.append(" ");
            sb.append(fai.getApplicationNbr());
        }
        sb.append("; ");
    }
    
    public static void appendApplicationDates(StringBuffer sb, List<FacilityAppInfo> fais) {
        if(fais == null) return;
        if(fais.size() == 0) {
            sb.append("none; ");
            return;
        }
        for(FacilityAppInfo fai : fais) {
            if(sb.length() > 0) sb.append(" ");
            sb.append(getDateStr(fai.getReceivedDate()));
        }
        sb.append("; ");
    }
    
    void retrievePermitsApps(String facilityId) {
        try {
            permitInfo = getEmissionsReportService().retrieveFacilityPermitInfo(facilityId);
            appInfo = getEmissionsReportService().retrieveFacilityAppInfo(facilityId);
            // Add in the list of applications
            for(FacilityPermitInfo fpi : permitInfo) {
                ArrayList<FacilityAppInfo> aList = new ArrayList<FacilityAppInfo>();
                for(Integer ip : fpi.getApplicationIds()) {
                    boolean found = false;
                    for(FacilityAppInfo fai : appInfo) {
                        if(ip.equals(fai.getApplicationId())) {
                            aList.add(fai);
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        // should have found the application
                    }
                }
                fpi.setApplications(aList);
                
            }
        } catch(RemoteException re) {

        }
    }
    
    public static String getDateStr(Timestamp ts) {
        if(ts == null) return "--/--/----";
        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        return Integer.toString(m) + "/" + Integer.toString(d) + "/" + Integer.toString(y);
    }

    public ArrayList<ArrayList<String>> getDatagrid() {
        return datagrid;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public String getCandidateIncludedStr() {
        return candidateIncludedStr;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public boolean isEisPolls() {
        return eisPolls;
    }

    public void setEisPolls(boolean eisPolls) {
        this.eisPolls = eisPolls;
    }

    public boolean isEsPolls() {
        return esPolls;
    }

    public void setEsPolls(boolean esPolls) {
        this.esPolls = esPolls;
    }

    public boolean isFerPolls() {
        return ferPolls;
    }

    public void setFerPolls(boolean ferPolls) {
        this.ferPolls = ferPolls;
    }

    public boolean isEuTots() {
        return euTots;
    }

    public void setEuTots(boolean euTots) {
        this.euTots = euTots;
    }

    public boolean isFacTots() {
        return facTots;
    }

    public void setFacTots(boolean facTots) {
        this.facTots = facTots;
    }

    public boolean isOpEUs() {
        return opEUs;
    }

    public void setOpEUs(boolean opEUs) {
        this.opEUs = opEUs;
    }

    public boolean isSdEUs() {
        return sdEUs;
    }

    public void setSdEUs(boolean sdEUs) {
        this.sdEUs = sdEUs;
    }

    public boolean isRemoveNull() {
        return removeNull;
    }

    public void setRemoveNull(boolean removeNull) {
        this.removeNull = removeNull;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public boolean isDatagridEmpty() {
        return datagrid.size() == 0;
    }

    public String getErrorStr() {
        return errorStr;
    }

    public int getRptLevel() {
        return rptLevel;
    }

    public void setRptLevel(int rptLevel) {
        this.rptLevel = rptLevel;
    }

    public List<SelectItem> getAllEUs() {
        return allEUs;
    }

    public boolean isColumnCE() {
        return columnCE;
    }

    public void setColumnCE(boolean columnCE) {
        this.columnCE = columnCE;
    }

    public boolean isColumnProcess() {
        return columnProcess;
    }

    public void setColumnProcess(boolean columnProcess) {
        this.columnProcess = columnProcess;
        if(!columnProcess) {
            columnSccDesc = false;
            columnCE = false;
            columnPid = false;
            columnPdesc = false;
        }
    }

    public List<String> getSelectedEUs() {
        return selectedEUs;
    }

    public void setSelectedEUs(List<String> selectedEUs) {
        this.selectedEUs = selectedEUs;
    }

    public String getExtraPollutantCd() {
        return extraPollutantCd;
    }

    public void setExtraPollutantCd(String extraPollutantCd) {
        this.extraPollutantCd = extraPollutantCd;
    }

    public boolean isColumnNaics() {
        return columnNaics;
    }

    public void setColumnNaics(boolean columnNaics) {
        this.columnNaics = columnNaics;
    }

    public boolean isColumnSic() {
        return columnSic;
    }

    public void setColumnSic(boolean columnSic) {
        this.columnSic = columnSic;
    }

    public boolean isColumnSicNaicsDesc() {
        return columnSicNaicsDesc;
    }

    public void setColumnSicNaicsDesc(boolean columnSicNaicsDesc) {
        this.columnSicNaicsDesc = columnSicNaicsDesc;
    }

    public boolean isColumnEuCompany() {
        return columnEuCompany;
    }

    public void setColumnEuCompany(boolean columnEuCompany) {
        this.columnEuCompany = columnEuCompany;
    }

    public boolean isColumnEuDapc() {
        return columnEuDapc;
    }

    public void setColumnEuDapc(boolean columnEuDapc) {
        this.columnEuDapc = columnEuDapc;
    }

    public boolean isColumnEuInstallDates() {
        return columnEuInstallDates;
    }

    public void setColumnEuInstallDates(boolean columnEuInstallDates) {
        this.columnEuInstallDates = columnEuInstallDates;
    }

    public int getPollsFromGrpsColumn() {
        return pollsFromGrpsColumn;
    }

    public void setPollsFromGrpsColumn(int pollsFromGrpsColumn) {
        this.pollsFromGrpsColumn = pollsFromGrpsColumn;
    }

    public int getCorrIdIndex() {
        return corrIdIndex;
    }

    public boolean isColumnPdesc() {
        return columnPdesc;
    }

    public void setColumnPdesc(boolean columnPdesc) {
        this.columnPdesc = columnPdesc;
    }

    public boolean isColumnPid() {
        return columnPid;
    }

    public void setColumnPid(boolean columnPid) {
        this.columnPid = columnPid;
    }

    public boolean isColumnSccDesc() {
        return columnSccDesc;
    }

    public void setColumnSccDesc(boolean columnSccDesc) {
        this.columnSccDesc = columnSccDesc;
    }

    public boolean isRowEuTot() {
        return rowEuTot;
    }

    public void setRowEuTot(boolean rowEuTot) {
        this.rowEuTot = rowEuTot;
    }

    public List<String> getSelectedCounties() {
        return selectedCounties;
    }

    public void setSelectedCounties(List<String> selectedCounties) {
        this.selectedCounties = selectedCounties;
    }

    public List<SelectItem> getAllCounties() {
        return allCounties;
    }

    //public boolean isColumnPbrPermit() {
    //    return columnPbrPermit;
    //}

    //public void setColumnPbrPermit(boolean columnPbrPermit) {
    //    this.columnPbrPermit = columnPbrPermit;
    //}

    //public boolean isColumnRegistrationPermit() {
    //    return columnRegistrationPermit;
    //}

    //public void setColumnRegistrationPermit(boolean columnRegistrationPermit) {
    //    this.columnRegistrationPermit = columnRegistrationPermit;
    //}

    public boolean isColumnPtioPermit() {
        return columnPtioPermit;
    }

    public void setColumnPtioPermit(boolean columnPtioPermit) {
        this.columnPtioPermit = columnPtioPermit;
    }

    //public boolean isColumnPtiPermit() {
    //    return columnPtiPermit;
    //}

    //public void setColumnPtiPermit(boolean columnPtiPermit) {
    //    this.columnPtiPermit = columnPtiPermit;
    //}

    public boolean isColumnTvPtoPermit() {
        return columnTvPtoPermit;
    }

    public void setColumnTvPtoPermit(boolean columnTvPtoPermit) {
        this.columnTvPtoPermit = columnTvPtoPermit;
    }

    public boolean isColumnAllPermit() {
        return columnAllPermit;
    }

    public void setColumnAllPermit(boolean columnAllPermit) {
        if(columnAllPermit) {
            //columnPtiPermit = true;
            columnTvPtoPermit = true;
            columnPtioPermit = true;
            //columnRegistrationPermit = true;
            //columnPbrPermit = true;
        } else {
            //columnPtiPermit = false;
            columnTvPtoPermit = false;
            columnPtioPermit = false; //includes state PTO and PTIOs issued with "FEPTIO" enabled check box
            //columnRegistrationPermit = false;
            //columnPbrPermit = false;
        }
        this.columnAllPermit = columnAllPermit;
    }

    public boolean isIncludeAll() {
        return includeAll;
    }

    public void setIncludeAll(boolean includeAll) {
        this.includeAll = includeAll;
        facTots = includeAll;
        includeCurr = includeAll;
        rowEuTot = includeAll;
        columnSic = includeAll;
        columnNaics = includeAll;
        columnSicNaicsDesc = includeAll;
        columnEuCompany = includeAll;
        columnEuInstallDates = includeAll;
        columnProcess = includeAll;
        columnSccDesc = includeAll;
        columnCE = includeAll;
        columnPid = includeAll;
        columnPdesc = includeAll;
    }

    public boolean isSelectedAllCounties() {
        return selectedAllCounties;
    }

    public List<String> getSelectedCountiesIsAll() {
        return selectedCountiesIsAll;
    }

    public boolean isSelectedAllEUs() {
        return selectedAllEUs;
    }

    public List<String> getSelectedEUsIsAll() {
        return selectedEUsIsAll;
    }

    public boolean isIncludeCurr() {
        return includeCurr;
    }

    public void setIncludeCurr(boolean includeCurr) {
        this.includeCurr = includeCurr;
    }
}