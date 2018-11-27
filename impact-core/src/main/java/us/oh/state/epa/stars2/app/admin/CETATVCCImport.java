package us.oh.state.epa.stars2.app.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.app.ceta.EnforcementSearch;
import us.oh.state.epa.stars2.app.ceta.FceSiteVisits;
import us.oh.state.epa.stars2.app.ceta.StackTestSearch;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.FacilityHistoryService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityComplianceStatus;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.OffsitePCE;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.ComplianceStatusDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.YearsForFCEs;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.Afs9_1PlantGeneral;
import us.oh.state.epa.stars2.util.Afs9_2PlantGeneral;
import us.oh.state.epa.stars2.util.Afs9_3Pollutant;
import us.oh.state.epa.stars2.util.Afs9_4Actions;
import us.oh.state.epa.stars2.util.Afs9_Base;
import us.oh.state.epa.stars2.util.AfsFceSched;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

/*
 *  Export is done in two steps:
 *    1)  Retrieve the records that are to be exported. This is done with functions with names exportXXX().
 *        These are displayed and the user has the option to go to step 2.
 *        This step has the option of specifying the count of the number of records to retrieve.
 *    2)  Partially lock the records and generate the 9.1, 9.2, 9.3 & 9.4 records.  Partially lock means to
 *        set the AFS ACTION ID (or just AFS ID) but not set the AFS Sent Date (or just AFS Date).
 *        This is done with function with names lockXXX().
 *        The user should retrieve these 9.x files and process them outside of Stars2 to get them into AFS.
 *        AFS will provide results to indicate the records have been accepted.
 *
 *        For Inspection Schedule, at the time it is locked, the Inspection classification is set (unless already set).
 *         If already set, it is compared to the current Inspection Classification to provde a  message if different
 *         but the value in the Inspection is sent to AFS.
 *        
 *  Import will process the records accepted by AFS and set the AFS Sent Date.
 *    The SCSC ID, the AFS ID and information specific to the type of record is used to locate and verify
 *    the record to insert the AFS Date into.
 *    The specific information used is:
 *      Stack Test:      Achieve Date (last test date)Pollutant Code, Test Results.
 *      Site Visit:          Achieve Date (Visit Date).
 *      Off Site Visit:      Achieve Date (Visit Date).
 *      Inspection Scheduled        no validation
 *      Inspection Completed:       Achieve Date (Completion Date).
 *      Enforcement Action:  Achieve Date, Penalty Amount.
 *      TV CC:               using Action Type:  Achieve Date (date reviewed/received)
 *      
 *      The exception to this is Inspection Scheudules uses the Inspection ID directly.
 *      
 *      Additional Checks:
 *         For Stack Tests, Site Visits, Off Site Visits Inspection Completioins and TV CC, the AFS Sent Date is checked to see
 *         if it is being set for the first time or change.
 *         
 *      
 *    You have the choice of Importing the 9.4 file (just to confirm all dates have been set or
 *    importing the AFS Import file to actually first set the date in.
 *      
 *      Notes:
 *      Action Type ?RM? records were entered by USEPA. You can tell because the action IDs start with ?9?. 
 */

/*
 * INCONSISTENCIES Between 9.4 format and returning Import format from AFS
 *   Dates are sent as YYYYMMDD and returned as YYMMDD
 *   Penalty is sent as 9 characters and returned as 7 characters.
 *   Pollutant Cd is sent in one set of columns and CAS Code in another set of columns but returned in the same starting column.
 *      The Pollutant Cd in 5 characters but the CAS Code in 6 characters (although it is sent in 9 characters).
 *   In HPV Enforcement Actions, the programs and Key Action are in diffferent columns but returned in the same starting columns.
 *      The Programs in 6 colums and Key Action in 5 columns.
 */

//http://www.epa.gov/compliance/data/systems/air/afsui/index.html
@SuppressWarnings("serial")
public class CETATVCCImport extends AppBase {
    private Timestamp afsSentDate;
    private UploadedFile cetaImportFile;
    private boolean hasImportResults;
    private boolean hasExportReview = false;
    private boolean hasExported = false;
    private boolean hasSchedExported = false;
    private List<ComplianceReport> reportList;
    private List<ComplianceReportList> tvCcs;
    private List<SiteVisit> siteVisits;
    private List<OffsitePCE> offSiteVisits;
    private List<FullComplianceEval> fceList;
    private List<StackTestedPollutant> stackTests;
    private List<EnforcementAction> enforceActionList;
    private String exportImport = null;
    private String importType = "fileAfs";  // from jsp  import 9.4 file or AFS Import file
    private String objectType;
    private String whatExported = null;
    private String importButtonLabel = null;
    private boolean operating = false; // working on something;  allow no changes.
    private String fceFfySched;
    private String fileNameComponent;
    private String zipFileName; // name of zip file containing four UI files
    private String schedFileName;  // name of file containing the Inspection schedule information.

    private String tmpDirName;

    private ArrayList<Afs9_1PlantGeneral> recs91;
    private List<Afs9_2PlantGeneral> recs92;
    private ArrayList<Afs9_3Pollutant> recs93;

    private int rootSize = 0;  // number of characters in FILE_STORE_ROOT_PATH + 1 (for the '\')
    private PrintStream file91 = null;
    private PrintStream file92 = null;
    private PrintStream file93 = null;
    private PrintStream file94 = null;
    private PrintStream fileSched = null;
    File f91;
    File f92;
    File f93;
    File f94;
    File fSched;
    File fz;
    String lockButton; 

    private boolean testMode = false;
    private Integer testCnt = null;
    private Integer testAfsIdBase = 88000;  // used to fake AFS IDs
    private ArrayList<Afs9_1PlantGeneral> afs_1_list;
    private ArrayList<Afs9_2PlantGeneral> afs_2_list;
    private ArrayList<Afs9_3Pollutant> afs_3_list;
    private ArrayList<Afs9_4Actions> afs_4_list;
    private ArrayList<AfsFceSched> afs_sched_list;

    public static boolean NO_FAIL = true;

    static String FACIL = "FACIL";  // facility pollutant
    static String THAP = "THAP";  // MACT pollutant
    
    static String AIR_PROG_SIP = "0";
    static String AIR_PROG_PSD = "6";
    static public final String AIR_PROG_NSR = "7";
    static String AIR_PROG_NESHAP = "8";
    static String AIR_PROG_NSPS = "9";
    static String AIR_PROG_FESOP ="F";
    static String AIR_PROG_MACT = "M";
    static public String AIR_PROG_TITLE_V = "V";
    private final static DateFormat dateFormat94 = new SimpleDateFormat("yyyyMMdd");
    private final static DateFormat dateFormatAfsImport = new SimpleDateFormat("yyMMdd");

    // These comment lines are the first lines of the files, but will be skipped anywhere.
    private String commentLine1 = "THIS FILE";  // starts in first character of line.
    private String otherName = "Offsite(Other)";
    /* AFS Export of Enforcements
     * Send only HPV Enforcements and only Formal Actions
     * NEW ENFORCEMENT (not yet sent to AFS)
     *   send  STATE_DAY_ZERO_38
     *   send  DATE_DAY_ZERO_ADDED_TO_AFS_VL
     *   send  DATE_VIOATION_DISCOVERED_SHARP_SHARP("##") (this is the discovery object)
     *     where ## is replaced with the type of object.
     *   
     *   Then send formal actions in the order they were created (determined by their Stars2 id)
     *   IF(REFER_TO_AGO_X3):
     *       send  REFER_TO_AGO_X3
     *       send  DATE_REFER_TO_AGO_ADDED_TO_AFS_OT (but only send one with this batch)
     *       
     *   IF( _66 or _66A or LOCAL_FINDINGS_AND_ORDERS_68)
     *       send that action
     *       send DATE_FINDINGS_OR_FINAL_COMPLIANCE_ADDED_TO_AFS_RT (but only send one with this batch)
     *       
     *   IF(WITHDRAWL_OF_ENFORCEMENT_ACTION_EW or CASE_CLOSED_BANKRUPTCY_OR_LIMITATIONS_K9)
     *       send that action
     *       send 
     */
    
    private ComplianceReportService complianceReportService;
    
    private DocumentService documentService;
    
    private FacilityService facilityService;
    
    private FullComplianceEvalService fullComplianceEvalService;
    
    //private EnforcementService enforcementService;
    
	private StackTestService stackTestService;
	
	private FacilityHistoryService facilityHistoryService;
	
	private InfrastructureService infrastructureService;


    public CETATVCCImport(){
        //      find out the current year
        Calendar now = Calendar.getInstance();
        int currentYear = now.get(Calendar.YEAR);
        if(Calendar.MONTH >= Calendar.OCTOBER) {
            // We want next FFY
            currentYear++;
        }
        currentYear++;
        fceFfySched = Integer.toString(currentYear);
        afsSentDate = new Timestamp(now.getTimeInMillis());
    }

    private void clearLists() {
        reportList = null;
        siteVisits = null;
        fceList = null;
        stackTests = null;
        enforceActionList = null;

        EnforcementSearch es = (EnforcementSearch) FacesUtil.getManagedBean("enforcementSearch");
        es.setFromFacility(false);
        StackTestSearch stSearch = (StackTestSearch)FacesUtil.getManagedBean("stackTestSearch");
        stSearch.setFromFacility(false);
        FceSiteVisits fceSiteVisits = (FceSiteVisits)FacesUtil.getManagedBean("fceSiteVisits");
        fceSiteVisits.setFromFacility(false);
    }
    
    public ComplianceReportService getComplianceReportService() {
		return complianceReportService;
	}

	public void setComplianceReportService(
			ComplianceReportService complianceReportService) {
		this.complianceReportService = complianceReportService;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	/*public EnforcementService getEnforcementService() {
		return enforcementService;
	}

	public void setEnforcementService(EnforcementService enforcementService) {
		this.enforcementService = enforcementService;
	}*/

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	public FacilityHistoryService getFacilityHistoryService() {
		return facilityHistoryService;
	}

	public void setFacilityHistoryService(
			FacilityHistoryService facilityHistoryService) {
		this.facilityHistoryService = facilityHistoryService;
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public final Timestamp getAfsSentDate() {
        return afsSentDate;
    }
    public final void setAfsSentDate(Timestamp afsSentDate) {
        this.afsSentDate = afsSentDate;
    }
    public final UploadedFile getCetaImportFile() {
        return cetaImportFile;
    }
    public final void setCetaImportFile(UploadedFile cetaImportFile) {
        this.cetaImportFile = cetaImportFile;
    }

    public final String getImportFileFormat() {
        return "The CETA import file must have the following format: " +
        "<ul>" + 
        "<li>facid (10 digits)</li>" +
        "<li>scsc (10 digits)</li>" +
        "<li>AFS Action id (6digits)</li>" + 
        "<li>report id (integer)</li>" +
        "</ul>";

    }

    public final String exportCetaData() {
        FceSiteVisits fsv = (FceSiteVisits)FacesUtil.getManagedBean("fceSiteVisits");
        fsv.setFromFacility(false);
        StackTestSearch sts = (StackTestSearch)FacesUtil.getManagedBean("stackTestSearch");
        sts.setFromFacility(false);
        /*
            For all types of exports what records are needed:
            One Afs9_1PlantGeneral record per facility
            One Afs9_2PlantGeneral record for each air program the facility has that has subparts
            One Afs9_3Pollutant record for each air program the facility has that specifies pollutants

            One Afs9_4Actions record for each object being exported.
            Different constructors are called to easily allow population of the fields needed
            for that particualar object:

            Inspection Schedule uses function Afs9_4Actions.create94FCE()
            Site Visit uses function Afs9_4Actions.create94SiteVisit()
            Off Site Visit (PCE) uses function Afs9_4Actions.create94OffSite()
            Stack Test for each pollutant uses function Afs9_4Actions.create94EmissionTest()
            Enforcement Actions uses function Afs9_4Actions.create94Enf()
               --note the only difference between non-HPV and HPV is that
                 for HPV pathways are created in AFS, so several extra Action Types must be used.
            TV CC uses function Afs9_4Actions.create94TvCc()
         */
        Calendar cal = Calendar.getInstance();
//      long t = afsSentDate.getTime();
//      cal.setTimeInMillis(t);

        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);
        int y = cal.get(Calendar.YEAR);
        String mStr = Integer.toString(m);
        if(mStr.length() <2) mStr = "0" + mStr;
        String dStr = Integer.toString(d);
        if(dStr.length() <2) dStr = "0" + dStr;
        String dateStr = mStr + dStr + Integer.toString(y);
        hasExported = false;
        hasSchedExported = false;
        String rtn = null;
        afs_1_list = new ArrayList<Afs9_1PlantGeneral>();
        afs_2_list = new ArrayList<Afs9_2PlantGeneral>();
        afs_3_list = new ArrayList<Afs9_3Pollutant>();
        afs_4_list = new ArrayList<Afs9_4Actions>();
        afs_sched_list = new ArrayList<AfsFceSched>();

        if("tv".equals(objectType)) {
            fileNameComponent = "tvCc" + dateStr;
            lockButton = "Partially Lock TV CCs and Create AFS Records";
            rtn = exportTVCCs();
        } else if("fce".equals(objectType)) {
            fileNameComponent = "fceCompleted" + dateStr;
            lockButton = "Partially Lock Completed Inspections and Create AFS Records";
            rtn = exportFCEs();
        } else if("sf".equals(objectType)) {
            fileNameComponent = "fceScheduled" + dateStr;
            lockButton = "Partially Lock Scheduled Inspections and Create Inspection Schedule";
            rtn = exportScheduledFCEs();
        } else if("sv".equals(objectType)) {
            fileNameComponent = "onSiteVisit" + dateStr;
            lockButton = "Partially Lock On Site Visits and Create AFS Records";
            rtn = exportSiteVisits();
        }else if("osv".equals(objectType)) {
            fileNameComponent = "offSiteVisit" + dateStr;
            lockButton = "Partially Lock " + otherName + " and Create AFS Records";
            rtn = exportOffSiteVisits();
        } else if("et".equals(objectType)) {
            fileNameComponent = "emissionsTest" + dateStr;
            lockButton = "Partially Lock Emission Tests and Create AFS Records";
            rtn = exportEmissionsTests();
        } else if("ea".equals(objectType)) {
            fileNameComponent = "enforceAction" + dateStr;
            lockButton = "Partially Lock Enforcement Actions and Create AFS Records";
            rtn = exportEnforcementActions();
        }
        return rtn;
    }

    public final String lockCetaData() {
        String rtn = null;
        if("tv".equals(objectType)) {
            initAfsFiles();
            rtn = lockTvCcs();
            if(hasExported) zipUpFiles();
        } else if("fce".equals(objectType)) {
            initAfsFiles();
            rtn = lockFCEs();
            if(hasExported) zipUpFiles();
        } else if("sf".equals(objectType)) {
            createSchedFiles();
            rtn = lockScheduledFCEs();
        } else if("sv".equals(objectType)) {
            initAfsFiles();
            rtn = lockSiteVisits();
            if(hasExported) zipUpFiles();
        }else if("osv".equals(objectType)) {
            initAfsFiles();
            rtn = lockOffSiteVisits();
            if(hasExported) zipUpFiles();
        } else if("et".equals(objectType)) {
            initAfsFiles();
            rtn = lockEmissionsTests();
            if(hasExported) zipUpFiles();
        } /*else if("ea".equals(objectType)) {
            initAfsFiles();
            rtn = lockEnforcementActions();
            if(hasExported) zipUpFiles();
        }*/
        return rtn;
    }

    public final String importCetaData() {
        String rtn = null;
        boolean ok = true;
        if (afsSentDate == null) {
            DisplayUtil.displayError("AFS Sent Date is required.");
            ok = false;
        }

        if(afsSentDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            Timestamp today = new Timestamp(cal.getTimeInMillis());

            if(today.before(afsSentDate)) {
                DisplayUtil.displayError("AFS Sent Date cannot be in the future.");
                ok = false;
            }
        }

        if (cetaImportFile == null) {
            DisplayUtil.displayError("CETA Import File is required.");
            ok = false;
        }
        // Make sesnt date accurate down to the day
        Calendar calAfs = Calendar.getInstance();
        calAfs.setTimeInMillis(afsSentDate.getTime());
        calAfs.set(Calendar.HOUR_OF_DAY, 0);
        calAfs.set(Calendar.MINUTE, 0);
        calAfs.set(Calendar.SECOND, 0);
        calAfs.set(Calendar.MILLISECOND, 0);
        afsSentDate = new Timestamp(calAfs.getTimeInMillis());
        afsSentDate.setNanos(0);

        if(!ok) return null;
        if("tv".equals(objectType)) {
            importTVCCData();
        } else if("fce".equals(objectType)) {
            importFceEvals();
        } else if("sf".equals(objectType)) {
            if(fceFfySched == null || fceFfySched.length() == 0) {
                DisplayUtil.displayError("FFY Inspection Scheduled is not set.");
                return null;
            }
            importFceScheds();
        } else if("sv".equals(objectType)) {
            importSiteVisits();
        /*} else if("osv".equals(objectType)) {
            importOffSiteVisits();*/
        }else if("et".equals(objectType)) {
            importEmissionsTests();
        } /*else if("ea".equals(objectType)) {
            importEnforcementActions();
        }*/
        return rtn;
    }

    private final void importTVCCData() {
        int alreadySetCnt = 0;  // total number where AFS Sent Date already correctly set.
        int changeSetCnt = 0;  // total number where AFS Sent Date being changed.
        int setAfsDateCnt = 0;  // total number where AFS Sent Date set into record because was not set or is changed.
        int wouldSetAfsDateCnt = 0; int recordsInErrCnt = 0;  // those skipped because of some problem.  Date not set.
        try {
            hasImportResults = false;
            reportList = new ArrayList<ComplianceReport>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(cetaImportFile.getInputStream()));
            String line = null;
            String srInfo = null;
            while ((line = reader.readLine()) != null) {
                if(line.length() > commentLine1.length() && line.substring(0, commentLine1.length()).equals(commentLine1)){
                    // line if just a comment
                    continue;
                }

                if (line.length() == 0) {
                    DisplayUtil.displayError("Blank input line--skipped");
                    recordsInErrCnt++;
                    continue;
                }

                if(importType.equals("file94")) {
                    if (line.length() < 39) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                } else {
                    if (line.length() < 54) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                }
                String scscId = line.substring(0, 10).trim();
                String afsId;
                String afsIdFirstChar;
                String actionType;
                String dateAchievStr;
                String reviewDateStr;
                String s161;

                if(importType.equals("file94")) {
                    // These are the 9.4 formats
                    afsId = line.substring(10, 15).trim();
                    afsIdFirstChar= line.substring(10, 11).trim();
                    actionType = line.substring(21, 23).trim();
                    dateAchievStr = line.substring(31, 39).trim();
                } else {
                    // These are AFS import formats
                    s161 = line.substring(10, 13).trim();
                    afsId = line.substring(13, 18).trim();
                    afsIdFirstChar= line.substring(13, 14).trim();
                    actionType = line.substring(24, 26).trim();
                    dateAchievStr = line.substring(48, 54).trim();
                    if(!"161".equals(s161)) {
                        String s = "Invalid line in AFS Import file.  \"161\" not in expected positions (11-13); AFS Action ID " +
                        afsId + " and SCSC ID " + scscId + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                }
                if("9".equals(afsIdFirstChar)) continue;  // Added by AFS; ignore it.
                if(!checkInput(scscId, afsId, actionType, line)) {
                    recordsInErrCnt++;
                    continue;
                }
                if(!actionType.equals("SR") && !actionType.equals("CB")) {
                    wrongTypeMsg("SR or CB", line);
                    recordsInErrCnt++;
                    continue;
                }
                if(actionType.equals("CB")) {
                    if(srInfo != null) {
                        String s = "A TV CC for SCSC ID " + srInfo + " with Action Type \"CB\" found without a matching \"CB\" immediately after it--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    srInfo = scscId;
                    continue;  //we don't use the SR record.
                }else {
                    // got Action Type "SR"
                    if(srInfo == null) {
                        String s = "A TV CC for SCSC ID " + scscId + " with Action Type \"SR\" found without a matching \"SR\" immediately before it--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    if(!srInfo.equals(scscId)) {
                        String s = "A TV CC for SCSC ID " + scscId + " with Action Type \"SR\" found with previous \"SR\" before it with different SCSC ID(" +
                        srInfo + ")--skipped.";
                        DisplayUtil.displayError(s);
                        srInfo = null;
                        recordsInErrCnt++;
                        continue;
                    }
                    srInfo = null;
                }
                try {
                    List<ComplianceReportList> tvCcs = null;
                    tvCcs = complianceReportService.retrieveTvCcByAfsId(scscId, afsId);
                    if(!checkImportReturn("TV CC", tvCcs, scscId, afsId)) {
                        recordsInErrCnt++;
                        continue;
                    }
                    if(importType.equals("file94")) {
                        reviewDateStr = dateFormat94.format(tvCcs.get(0).getReviewDate());
                    } else {
                        reviewDateStr = dateFormatAfsImport.format(tvCcs.get(0).getReviewDate());
                    }
                    if(!dateAchievStr.equals(reviewDateStr)) {
                        String s = "The review date (" + reviewDateStr + ") does not match the returning AFS Achieved date (" +
                        dateAchievStr + ") for TV CC with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + " and Facility ID " + tvCcs.get(0).getFacilityId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    if(tvCcs.get(0).getTvccAfsSentDate() != null && !afsSentDate.equals(tvCcs.get(0).getTvccAfsSentDate())) {
                        // Already set but being changed
                        DisplayUtil.displayInfo(" AFS Sent Date being changed from " + tvCcs.get(0).getTvccAfsSentDate() +
                                " for AFS Action ID " + afsId +
                                " and SCSC ID " + scscId);
                        changeSetCnt++;
                    }
                    if(tvCcs.get(0).getTvccAfsSentDate() != null && afsSentDate.equals(tvCcs.get(0).getTvccAfsSentDate())) {
                        // Date already set.  Do nothing
                        alreadySetCnt++;
                        continue;
                    }
                    tvCcs.get(0).setTvccAfsSentDate(afsSentDate);
                    try {
                        if(!isTestMode()) {
                            complianceReportService.afsSetDateTvCc(tvCcs.get(0));
                            setAfsDateCnt++;
                        } else wouldSetAfsDateCnt++;
                    } catch (RemoteException e) {
                        String s = "An eror occurred while updating ASF Date for TV CC report " + tvCcs.get(0).getReportId() + 
                        " with AFS Action ID " + afsId + " and SCSC ID " + scscId;
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        handleException(s, e);
                    }
                } catch (RemoteException e) {
                    recordsInErrCnt++;
                    handleException(e);
                }
            }
            closeReader(reader);
        } catch (IOException e) {
            String s = "Unable to read TV CC CETA Import File.";
            DisplayUtil.displayError(s);
            logger.error(s, e);
        }
        String s1 = alreadySetCnt + " record(s) already had correct AFS Sent Date set; " +
        + changeSetCnt + " record(s) with AFS Sent Date change;";
        String s0 = "";
        if(recordsInErrCnt > 0) {
            s0 = recordsInErrCnt + " record(s) in error and skipped; ";
        }
        String s3 = "";
        if(!testMode) {
            s3 =  " AFS Send Date set in " + (setAfsDateCnt - changeSetCnt) + " TV CC report(s).";
        } else s3 = " AFS Send Date would be set/updated in " + wouldSetAfsDateCnt + " TV CC report(s).";
        DisplayUtil.displayInfo(s0 + s1 + s3);
    }

    private final void importSiteVisits() {
        int alreadySetCnt = 0;  // total number where AFS Sent Date already correctly set.
        int changeSetCnt = 0;  // total number where AFS Sent Date being changed.
        int setAfsDateCnt = 0;  // total number where AFS Sent Date set into record because was not set or is changed.
        int wouldSetAfsDateCnt = 0; int recordsInErrCnt = 0;  // those skipped because of some problem.  Date not set.
        try {
            hasImportResults = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(cetaImportFile.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.length() > commentLine1.length() && line.substring(0, commentLine1.length()).equals(commentLine1)){
                    // line if just a comment
                    continue;
                }

                if (line.length() == 0) {
                    DisplayUtil.displayError("Blank input line skipped");
                    recordsInErrCnt++;
                    continue;
                }
                if(importType.equals("file94")) {
                    if (line.length() < 39) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                } else {
                    if (line.length() < 54) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                }
                String scscId = line.substring(0, 10).trim();
                String afsId;
                String afsIdFirstChar;
                String actionType;
                String dateAchievStr;
                String visitDateStr;
                String s161;

                if(importType.equals("file94")) {
                    // These are the 9.4 formats
                    afsId = line.substring(10, 15).trim();
                    afsIdFirstChar= line.substring(10, 11).trim();
                    actionType = line.substring(21, 23).trim();
                    dateAchievStr = line.substring(31, 39).trim();
                } else {
                    // These are AFS import formats
                    s161 = line.substring(10, 13).trim();
                    afsId = line.substring(13, 18).trim();
                    afsIdFirstChar= line.substring(13, 14).trim();
                    actionType = line.substring(24, 26).trim();
                    dateAchievStr = line.substring(48, 54).trim();
                    if(!"161".equals(s161)) {
                        String s = "Invalid line in AFS Import file.  \"161\" not in expected positions (11-13); AFS Action ID " +
                        afsId + " and SCSC ID " + scscId + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                }
                if("9".equals(afsIdFirstChar)) continue;  // Added by AFS; ignore it.
                if(!checkInput(scscId, afsId, actionType, line))  {
                    recordsInErrCnt++;
                    continue;
                }
                if(actionType.equals("00")) {
                    // This is an extra row related to a new facility--just ignore it.
                    continue;
                }
                if(!actionType.equals("83")) {
                    wrongTypeMsg("83", line);
                    recordsInErrCnt++;
                    continue;
                }
                try {
                    List<SiteVisit> svs = fullComplianceEvalService.retrieveSiteVisitByAfsId(scscId, afsId);
                    if(!checkImportReturn("Site Visit", svs, scscId, afsId)) {
                        recordsInErrCnt++;
                        continue;
                    }
                    if(importType.equals("file94")) {
                        visitDateStr = dateFormat94.format(svs.get(0).getVisitDate());
                    } else {
                        visitDateStr = dateFormatAfsImport.format(svs.get(0).getVisitDate());
                    }
                    if(!dateAchievStr.equals(visitDateStr)) {  // added for validation
                        String s = "The Visit Date does not match the returning AFS Achieved Date for Site Visit with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + " and Facility ID " + svs.get(0).getFacilityId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    if(svs.get(0).getAfsDate() != null && !afsSentDate.equals(svs.get(0).getAfsDate())) {
                        // Already set but being changed
                        DisplayUtil.displayInfo(" AFS Sent Date being changed from " + svs.get(0).getAfsDate() +
                                " for AFS Action ID " + afsId +
                                " and SCSC ID " + scscId);
                        changeSetCnt++;
                    }
                    if(svs.get(0).getAfsDate() != null && afsSentDate.equals(svs.get(0).getAfsDate())) {
                        // Date already set.  Do nothing
                        alreadySetCnt++;
                        continue;
                    }
                    svs.get(0).setAfsDate(afsSentDate);
                    try {
                        if(!isTestMode()) {
                            fullComplianceEvalService.afsSetDateSiteVisit(svs.get(0));
                            setAfsDateCnt++;
                        } else wouldSetAfsDateCnt++;
                    } catch (RemoteException e) {
                        String s = "An eror occurred while updating ASF Date for Site Visit with AFS Action ID " + afsId +
                        " and SCSC ID " + scscId;
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        handleException(s, e);
                    }
                } catch (RemoteException e) {
                    handleException(e);
                }
            }
            closeReader(reader);
        } catch (IOException e) {
            String s = "Unable to read Site Visit CETA Import File.";
            DisplayUtil.displayError(s);
            logger.error(s, e);
        }
        String s1 = alreadySetCnt + " record(s) already had correct AFS Sent Date set; " +
        + changeSetCnt + " record(s) with AFS Sent Date change;";
        String s0 = "";
        if(recordsInErrCnt > 0) {
            s0 = recordsInErrCnt + " record(s) in error and skipped; ";
        }
        String s3 = "";
        if(!testMode) {
            s3 =  " AFS Send Date set in " + (setAfsDateCnt - changeSetCnt) + " On Site Visit(s).";
        } else s3 = " AFS Send Date would be set/updated in " + wouldSetAfsDateCnt + " On Site Visit(s).";
        DisplayUtil.displayInfo(s0 + s1 + s3);
    }
/*
    private final void importEnforcementActions() {
        int alreadySetCnt = 0;  // total number where AFS Sent Date already correctly set.
        int changeSetCnt = 0;  // total number where AFS Sent Date being changed.
        int setAfsDateCnt = 0;  // total number where AFS Sent Date set into record because was not set or is changed.
        int wouldSetAfsDateCnt = 0; int recordsInErrCnt = 0;  // those skipped because of some problem.  Date not set.
        try {
            hasImportResults = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(cetaImportFile.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.length() > commentLine1.length() && line.substring(0, commentLine1.length()).equals(commentLine1)){
                    // line if just a comment
                    continue;
                }

                if (line.length() == 0) {
                    DisplayUtil.displayError("Blank input line skipped");
                    recordsInErrCnt++;
                    continue;
                }
                if(importType.equals("file94")) {
                    if (line.length() < 48) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                } else {
                    if (line.length() < 64) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                }
                String scscId = line.substring(0, 10).trim();
                String afsId;
                String afsIdFirstChar;
                String actionType;
                String dateAchievStr;
                String actionDateStr;
                String s161;
                String afsCashPenalty;

                if(importType.equals("file94")) {
                    // These are the 9.4 formats
                    afsId = line.substring(10, 15).trim();
                    afsIdFirstChar= line.substring(10, 11).trim();
                    actionType = line.substring(21, 23).trim();
                    dateAchievStr = line.substring(31, 39).trim();
                    afsCashPenalty = line.substring(39,48).trim();  // length 9 in returned AFS file
                } else {
                    // These are AFS import formats
                    // For High Priortiy HPV either programs -6 or (Key Action – 5 and Blank – 1) 
                    s161 = line.substring(10, 13).trim();
                    afsId = line.substring(13, 18).trim();
                    afsIdFirstChar= line.substring(13, 14).trim();
                    actionType = line.substring(24, 26).trim();
                    dateAchievStr = line.substring(48, 54).trim();
                    afsCashPenalty = line.substring(56,63).trim();  // length 7 in returned AFS file
                    if(!"161".equals(s161)) {
                        String s = "Invalid line in AFS Import file.  \"161\" not in expected positions (11-13); AFS Action ID " +
                        afsId + " and SCSC ID " + scscId + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                }
                if("9".equals(afsIdFirstChar)) continue;  // Added by AFS; ignore it.
                if(afsCashPenalty == null || afsCashPenalty.length() == 0) afsCashPenalty = "0";
                if(!checkInput(scscId, afsId, actionType, line))  {
                    recordsInErrCnt++;
                    continue;
                }
                BaseDef bd = AFSEnforcementTypeDef.getData().getItem(actionType);
                if(bd == null) {
//                  // not a valid action code.
//                  if(actionType.equals("80") ||
//                  actionType.equals("81") ||
//                  actionType.equals("83") ||
//                  actionType.equals("37") ||
//                  actionType.equals("SR") ||
//                  actionType.equals("CB")) {
//                  String s = "ActionType " + actionType + " is not valid for an Enforcement Action.  Import record skipped (\"" + line + "\"";
//                  logger.error(s);
//                  DisplayUtil.displayError(s);
//                  continue;
//                  }
                    // Must be an Action Type added just for export--skip it
                    if(actionType.equals("38")) {
                        // Handle this one since it contains the zero date afs ID
                        // Set the date into the enforcement case
                        // First retreive enforcement and scscId
                        Enforcement enf = enforcementService.retrieveEnforcementOnly(afsId);
                        if(enf == null) {
                            String s = "Enforcement not found for zero_date_afs_id" + afsId + ", SCSC ID " + scscId ;
                            DisplayUtil.displayError(s);
                            recordsInErrCnt++;
                            continue;
                        }
                        if(!scscId.equals(enf.getScscId())) {
                            String s = "Enforcement with zero_date_afs_id" + afsId + ", Enforcement ID " + enf.getEnforcementId() + ", and SCSC ID " + enf.getScscId() +
                            " did not match the SCSC ID in the import file";
                            DisplayUtil.displayError(s);
                            recordsInErrCnt++;
                            continue;
                        }
                        // Set the afs date
                        enf.setAfsDate(afsSentDate);
                        enforcementService.setZeroAfsDateEnf(enf);
                        continue;
                    } else continue;
                }
                try {
                    List<EnforcementAction> eas = enforcementService.retrieveEnforceActionByAfsId(scscId, afsId);
                    if(!checkImportReturn("Enforcement Action", eas, scscId, afsId)) {
                        recordsInErrCnt++;
                        continue;
                    }
                    if(importType.equals("file94")) {
                        actionDateStr = dateFormat94.format(eas.get(0).getActionDate());
                    } else {
                        actionDateStr = dateFormatAfsImport.format(eas.get(0).getActionDate());
                    }
                    if(!dateAchievStr.equals(actionDateStr)) { // added for validation
                        String s = "The Action Date does not match the returning AFS Achieved Date for Enforcement Action with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + ",Facility ID " + eas.get(0).getFacilityId() + " and Enforcement ID " + 
                        eas.get(0).getEnforcementId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }

                    String cashPenFromAction = eas.get(0).getFinalCashAmount();
                    if(cashPenFromAction == null || cashPenFromAction.length() == 0) cashPenFromAction = "0";
                    long afsPenalityAmount = 0;
                    long stars2Penalty = 0;

                    try {
                        afsPenalityAmount = Money.toCents(afsCashPenalty)/100;
                    } catch(EPAIllegalArgumentException ex) {
                        String s = "The returning AFS penalty amount (" + afsCashPenalty + ") is invalid for Enforcement Action with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + ",Facility ID " + eas.get(0).getFacilityId() + " and Enforcement ID " + 
                        eas.get(0).getEnforcementId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    try {
                        stars2Penalty = Money.toCents(cashPenFromAction)/100;
                    } catch(EPAIllegalArgumentException ex) {
                        String s = "The penalty amount in the Stars2 database (" + cashPenFromAction + ") is invalid for Enforcement Action with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + ",Facility ID " + eas.get(0).getFacilityId() + " and Enforcement ID " + 
                        eas.get(0).getEnforcementId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    if(afsPenalityAmount != stars2Penalty) { // added for validation
                        String s = "The penalty amount in the Stars2 database (" + cashPenFromAction + ") does not match the AFS penalty amount (" + afsCashPenalty + ") for Enforcement Action with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + ",Facility ID " + eas.get(0).getFacilityId() + " and Enforcement ID " + 
                        eas.get(0).getEnforcementId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    if(eas.get(0).getAfsSentDate() != null && !afsSentDate.equals(eas.get(0).getAfsSentDate())) {
                        // Already set but being changed
                        DisplayUtil.displayInfo(" AFS Sent Date being changed from " + eas.get(0).getAfsSentDate() +
                                " for AFS Action ID " + afsId +
                                " and SCSC ID " + scscId);
                        changeSetCnt++;
                    }
                    if(eas.get(0).getAfsSentDate() != null && afsSentDate.equals(eas.get(0).getAfsSentDate())) {
                        // Date already set.  Do nothing
                        alreadySetCnt++;
                        continue;
                    }
                    eas.get(0).setAfsSentDate(afsSentDate);
                    try {
                        if(!isTestMode()) {
                            enforcementService.afsSetDateEnfAction(eas.get(0));
                            setAfsDateCnt++;
                        } else wouldSetAfsDateCnt++;
                    } catch (RemoteException e) {
                        String s = "An eror occurred while updating ASF Date for Enforcement Action with AFS Action ID " + afsId +
                        " and SCSC ID " + scscId;
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        handleException(s, e);
                    }
                } catch (RemoteException e) {
                    handleException(e);
                }
            }
            closeReader(reader);
        } catch (IOException e) {
            String s = "Unable to read Site Visit CETA Import File.";
            DisplayUtil.displayError(s);
            logger.error(s, e);
        }
        String s1 = alreadySetCnt + " record(s) already had correct AFS Sent Date set; " +
        + changeSetCnt + " record(s) with AFS Sent Date change;";
        String s0 = "";
        if(recordsInErrCnt > 0) {
            s0 = recordsInErrCnt + " record(s) in error and skipped; ";
        }
        String s3 = "";
        if(!testMode) {
            s3 =  " AFS Send Date set in " + (setAfsDateCnt - changeSetCnt) + " Enforcement Action(s).";
        } else s3 = " AFS Send Date would be set/updated in " + wouldSetAfsDateCnt + " Enforcement Action(s).";
        DisplayUtil.displayInfo(s0 + s1 + s3);
    }
*/
    /*private final void importOffSiteVisits() {
        int alreadySetCnt = 0;  // total number where AFS Sent Date already correctly set.
        int changeSetCnt = 0;  // total number where AFS Sent Date being changed.
        int setAfsDateCnt = 0;  // total number where AFS Sent Date set into record because was not set or is changed.
        int wouldSetAfsDateCnt = 0; int recordsInErrCnt = 0;  // those skipped because of some problem.  Date not set.
        try {
            hasImportResults = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(cetaImportFile.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.length() > commentLine1.length() && line.substring(0, commentLine1.length()).equals(commentLine1)){
                    // line if just a comment
                    continue;
                }

                if (line.length() == 0) {
                    DisplayUtil.displayError("Blank input line skipped");
                    recordsInErrCnt++;
                    continue;
                }
                if(importType.equals("file94")) {
                    if (line.length() < 39) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                } else {
                    if (line.length() < 54) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                }
                String scscId = line.substring(0, 10).trim();
                String afsId;
                String afsIdFirstChar;
                String actionType;
                String dateAchievStr;
                String visitDateStr;
                String s161;

                if(importType.equals("file94")) {
                    // These are the 9.4 formats
                    afsId = line.substring(10, 15).trim();
                    afsIdFirstChar= line.substring(10, 11).trim();
                    actionType = line.substring(21, 23).trim();
                    dateAchievStr = line.substring(31, 39).trim();
                } else {
                    // These are AFS import formats
                    s161 = line.substring(10, 13).trim();
                    afsId = line.substring(13, 18).trim();
                    afsIdFirstChar= line.substring(13, 14).trim();
                    actionType = line.substring(24, 26).trim();
                    dateAchievStr = line.substring(48, 54).trim();
                    if(!"161".equals(s161)) {
                        String s = "Invalid line in AFS Import file.  \"161\" not in expected positions (11-13); AFS Action ID " +
                        afsId + " and SCSC ID " + scscId + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                }
                if("9".equals(afsIdFirstChar)) continue;  // Added by AFS; ignore it.
                if(!checkInput(scscId, afsId, actionType, line))  {
                    recordsInErrCnt++;
                    continue;
                }
                if(!actionType.equals("80")) {
                    wrongTypeMsg("80", line);
                    recordsInErrCnt++;
                    continue;
                }
                try {
                    List<OffsitePCE> offVisits = enforcementService.retrieveOffSiteVisitsByAfsId(scscId, afsId);
                    if(!checkImportReturn(otherName, offVisits, scscId, afsId)) {
                        recordsInErrCnt++;
                        continue;
                    }
                    if(importType.equals("file94")) {
                        visitDateStr = dateFormat94.format(offVisits.get(0).getActionDate());
                    } else {
                        visitDateStr = dateFormatAfsImport.format(offVisits.get(0).getActionDate());
                    }
                    if(!dateAchievStr.equals(visitDateStr)) { // added for validation
                        String s = "The Action Date does not match the returning AFS Achieved Date for " + otherName + " with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + ",Facility ID " + offVisits.get(0).getFacilityId() + " and Enforcement ID " + offVisits.get(0).getEnforcementId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    if(offVisits.get(0).getAfsdate() != null && !afsSentDate.equals(offVisits.get(0).getAfsdate())) {
                        // Already set but being changed
                        DisplayUtil.displayInfo(" AFS Sent Date being changed from " + offVisits.get(0).getAfsdate() +
                                " for AFS Action ID " + afsId +
                                " and SCSC ID " + scscId);
                        changeSetCnt++;
                    }
                    if(offVisits.get(0).getAfsdate() != null && afsSentDate.equals(offVisits.get(0).getAfsdate())) {
                        // Date already set.  Do nothing
                        alreadySetCnt++;
                        continue;
                    }
                    offVisits.get(0).setAfsdate(afsSentDate);
                    try {
                        if(!isTestMode()) {
                            enforcementService.afsSetDateOffsitePCE(offVisits.get(0));
                            setAfsDateCnt++;
                        } else wouldSetAfsDateCnt++;
                    } catch (RemoteException e) {
                        String s = "An eror occurred while updating ASF Date for Off Site Visit " + offVisits.get(0).getOffsitePceId();
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        handleException(s, e);
                    }
                } catch (NumberFormatException nfe) {
                    String s = "Invalid report id in Off Site Visit CETA import file. Line is \"" + line.replaceAll(" ", ".") + "\"";
                    DisplayUtil.displayError(s);
                    recordsInErrCnt++;
                    logger.error(s, nfe);
                } catch (RemoteException e) {
                    handleException(e);
                }
            }
            closeReader(reader);
        } catch (IOException e) {
            DisplayUtil.displayError("Unable to read Off Site Visit CETA Import File.");
            logger.error("Unable to read Off Site Visit CETA Import File.", e);
        }
        String s1 = alreadySetCnt + " record(s) already had correct AFS Sent Date set; " +
        + changeSetCnt + " record(s) with AFS Sent Date change;";
        String s0 = "";
        if(recordsInErrCnt > 0) {
            s0 = recordsInErrCnt + " record(s) in error and skipped; ";
        }
        String s3 = "";
        if(!testMode) {
            s3 =  " AFS Send Date set in " + (setAfsDateCnt - changeSetCnt) + " " + otherName + " record(s).";
        } else s3 = " AFS Send Date would be set/updated in " + wouldSetAfsDateCnt + " " +  otherName + " record(s).";
        DisplayUtil.displayInfo(s0 + s1 + s3);
    }*/

    private final void importFceEvals() {
        int alreadySetCnt = 0;  // total number where AFS Sent Date already correctly set.
        int changeSetCnt = 0;  // total number where AFS Sent Date being changed.
        int wouldSetAfsDateCnt = 0; int setAfsDateCnt = 0;  // total number where AFS Sent Date set into record because was not set or is changed.
        int recordsInErrCnt = 0;  // those skipped because of some problem.  Date not set.
        try {
            hasImportResults = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(cetaImportFile.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.length() > commentLine1.length() && line.substring(0, commentLine1.length()).equals(commentLine1)){
                    // line if just a comment
                    continue;
                }

                if (line.length() == 0) {
                    DisplayUtil.displayError("Blank input line skipped");
                    recordsInErrCnt++;
                    continue;
                }
                if(importType.equals("file94")) {
                    if (line.length() < 39) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                } else {
                    if (line.length() < 54) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                }
                String scscId = line.substring(0, 10).trim();
                String afsId;
                String afsIdFirstChar;
                String actionType;
                String dateAchievStr;
                String evaluatedDateStr;
                String s161;
                if(importType.equals("file94")) {
                    // These are the 9.4 formats
                    afsId = line.substring(10, 15).trim();
                    afsIdFirstChar= line.substring(10, 11).trim();
                    actionType = line.substring(21, 23).trim();
                    dateAchievStr = line.substring(31, 39).trim();
                } else {
                    // These are AFS import formats
                    s161 = line.substring(10, 13).trim();
                    afsId = line.substring(13, 18).trim();
                    afsIdFirstChar= line.substring(13, 14).trim();
                    actionType = line.substring(24, 26).trim();
                    dateAchievStr = line.substring(48, 54).trim();
                    if(!"161".equals(s161)) {
                        String s = "Invalid line in AFS Import file.  \"161\" not in expected positions (11-13); AFS Action ID " +
                        afsId + " and SCSC ID " + scscId + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                }
                if("9".equals(afsIdFirstChar)) continue;  // Added by AFS; ignore it.
                if(!checkInput(scscId, afsId, actionType, line))  {
                    recordsInErrCnt++;
                    continue;
                }
                if(!actionType.equals("81")) {
                    wrongTypeMsg("81", line);
                    recordsInErrCnt++;
                    continue;
                }
                try {
                    List<FullComplianceEval> fces = fullComplianceEvalService.retrieveEvalFce(scscId, afsId);
                    if(!checkImportReturn("Completed Inspection", fces, scscId, afsId)) {
                        recordsInErrCnt++;
                        continue;
                    }
                    if(importType.equals("file94")) {
                        evaluatedDateStr = dateFormat94.format(fces.get(0).getDateEvaluated());
                    } else {
                        evaluatedDateStr = dateFormatAfsImport.format(fces.get(0).getDateEvaluated());
                    }
                    if(!dateAchievStr.equals(evaluatedDateStr)) {  // added for validation
                        String s = "The Evaluation Date does not match the returning AFS Achieved Date for Inspection with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + ",Facility ID " + fces.get(0).getFacilityId() + " and Inspection ID " + 
                        fces.get(0).getId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                    if(fces.get(0).getEvalAfsDate() != null) {
                        if(fces.get(0).getEvalAfsDate().equals(afsSentDate)) {
                            alreadySetCnt++;
                            continue;  // already set
                        }


                        if(!afsSentDate.equals(fces.get(0).getEvalAfsDate())) {
                            // Already set but being changed
                            DisplayUtil.displayInfo(" AFS Sent Date being changed from " + fces.get(0).getEvalAfsDate() +
                                    " for AFS Action ID " + afsId +
                                    " and SCSC ID " + scscId);
                            changeSetCnt++;
                        }   
                    }
                    fces.get(0).setEvalAfsDate(afsSentDate);
                    try {
                        if(!isTestMode()) {
                            fullComplianceEvalService.afsSetDateFceComp(fces.get(0));
                            setAfsDateCnt++;
                        } else wouldSetAfsDateCnt++;
                    } catch (RemoteException e) {
                        String s = "An eror occurred while updating Evaluation AFS Date for Completed Inspection found with AFS Action ID " + afsId +
                        " and SCSC ID " + scscId;
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        handleException(s, e);
                    }
                } catch (NumberFormatException nfe) {
                    String s = "Invalid report id in Evaluation Inspection CETA import file. Line is \"" + line.replaceAll(" ", ".") + "\"";
                    DisplayUtil.displayError(s);
                    recordsInErrCnt++;
                    logger.error(s, nfe);
                } catch (RemoteException e) {
                    handleException(e);
                }
            }
            closeReader(reader);
        } catch (IOException e) {
            DisplayUtil.displayError("Unable to read Evaluation Inspection CETA Import File.");
            logger.error("Unable to read Evaluation Inspection CETA Import File.", e);
        }
        String s1 = alreadySetCnt + " record(s) already had correct AFS Sent Date set; " +
        + changeSetCnt + " record(s) with AFS Sent Date change;";
        String s0 = "";
        if(recordsInErrCnt > 0) {
            s0 = recordsInErrCnt + " record(s) in error and skipped; ";
        }
        String s3 = "";
        if(!testMode) {
            s3 =  " AFS Send Date set in " + (setAfsDateCnt - changeSetCnt) + " Evaluation Inspection(s).";
        } else s3 = " AFS Send Date would be set/updated in " + wouldSetAfsDateCnt + " Evaluation Inspection(s).";
        DisplayUtil.displayInfo(s0 + s1 + s3);
    }

    private final void importFceScheds() {
        int alreadySetCnt = 0;  // total number where AFS Sent Date already correctly set.
        int changeSetCnt = 0;  // total number where AFS Sent Date being changed.
        int setAfsDateCnt = 0;  // total number where AFS Sent Date set into record because was not set or is changed.
        int wouldSetAfsDateCnt = 0; int recordsInErrCnt = 0;  // those skipped because of some problem.  Date not set.
        try {
            hasImportResults = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(cetaImportFile.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                // format is SCCID,facilityID,fceId,year,facilityName
                int comma1 = line.indexOf(',');
                int comma2 = line.indexOf(',', comma1 + 1);
                int comma3 = line.indexOf(',', comma2 + 1);
                int comma4 = line.indexOf(',', comma3 + 1);
                if(comma4 == -1 || comma3 == -1 || comma2 == -1 || comma1 == -1) {
                    String s ="Invalid data in Scheduled Inspection CETA import file. Line is \"" + line + "\"";
                    DisplayUtil.displayError(s);
                    logger.error(s);
                    continue;
                }
                String scscId = line.substring(0, comma1).trim();
                //    String facilityId = line.substring(comma1 + 1, comma2).trim();
                String fceIdStr = line.substring(comma2 + 1, comma3).trim();
                //    String inspecClass = line.substring(comma3 + 1, comma3).trim();
                //    String ffyStr = line.substring(comma4 + 1, comma3).trim();
                try {
                    int reportId = Integer.parseInt(fceIdStr);
                    FullComplianceEval fce = fullComplianceEvalService.retrieveFceOnly(reportId);
                    if (fce != null) {
                        if(fce.getSchedAfsId() == null) {
                            // afs ID has not been set
                            String s = "Scheduled AFS ID is not set in Inspection " + reportId;
                            DisplayUtil.displayError(s);
                            logger.error(s);
                            continue;
                        }
                        if(!scscId.equals(fce.getScscId())) {
                            DisplayUtil.displayInfo("SCSC IDs in input file(" + scscId + ") and Inspection (" +
                                    fce.getScscId() + ") do not match for Inspection " + reportId);
                            continue;
                        }
                        if(fce.getSchedAfsDate() != null) {
                            if(fce.getSchedAfsDate().equals(afsSentDate)) {
                                alreadySetCnt++;
                                continue;  // already set
                            }
                            if(!fce.getSchedAfsDate().equals(afsSentDate)) {
                                // Already set but being changed
                                DisplayUtil.displayInfo("AFS Sent Date being changed from " + fce.getSchedAfsDate() +
                                        " for Inspection " + reportId +
                                        " and SCSC ID " + scscId);
                                changeSetCnt++;
                            } 
                        }
                        fce.setSchedAfsDate(afsSentDate);
                        try {
                            if(!isTestMode()) {
                                fullComplianceEvalService.afsSetDateFceSched(fce);
                                setAfsDateCnt++;
                            } else wouldSetAfsDateCnt++;
                        } catch (RemoteException e) {
                            String s = "An eror occurred while updating Scheduled AFS Date for Inspection " + fceIdStr;
                            DisplayUtil.displayError(s);
                            handleException(s, e);
                        }
                    } else {
                        DisplayUtil.displayError("No Scheduled Inspection found matching id: " + fceIdStr);
                    }
                } catch (NumberFormatException nfe) {
                    String s = "Invalid report id in Scheduled Inspection CETA import file. Line is \"" + line.replaceAll(" ", ".") + "\"";
                    DisplayUtil.displayError(s);
                    logger.error(s, nfe);
                } catch (RemoteException e) {
                    handleException(e);
                }
            }
            closeReader(reader);
        } catch (IOException e) {
            DisplayUtil.displayError("Unable to read CETA Import File.");
            logger.error("Unable to read CETA Import File.", e);
        }
        String s1 = alreadySetCnt + " record(s) already had correct AFS Sent Date set; " +
        + changeSetCnt + " record(s) with AFS Sent Date change;";
        String s0 = "";
        if(recordsInErrCnt > 0) {
            s0 = recordsInErrCnt + " record(s) in error and skipped; ";
        }
        String s3 = "";
        if(!testMode) {
            s3 =  " AFS Send Date set in " + (setAfsDateCnt - changeSetCnt) + " Inspection Schedules(s).";
        } else s3 = " AFS Send Date would be set/updated in " + wouldSetAfsDateCnt + " Inspection Schedules(s).";
        DisplayUtil.displayInfo(s0 + s1 + s3);
    }

    //  This import uses the SCSC ID and AFS Action Id to locate the pollutant test record to update.
    //  It further uses the pollutant code to confirm it is correct.
    //  If it already has the date, then nothing is done.
    //  If it has a different date, an INFO message is displayed and the date changed.
    //  If no pollutant test record with that AFS Action Id can be found then an ERROR message is displayed.
    //  Otherwise the record is updated by putting the dat in.
    //  The date is the first milisecond after midnight on the date given (makeing comparisons easier).
    private final void importEmissionsTests() {
        int alreadySetCnt = 0;  // total number where AFS Sent Date already correctly set.
        int changeSetCnt = 0;  // total number where AFS Sent Date being changed.
        int setAfsDateCnt = 0;  // total number where AFS Sent Date set into record because was not set or is changed.
        int wouldSetAfsDateCnt = 0; int recordsInErrCnt = 0;  // those skipped because of some problem.  Date not set.
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(cetaImportFile.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.length() > commentLine1.length() && line.substring(0, commentLine1.length()).equals(commentLine1)){
                    // line if just a comment
                    continue;
                }

                if(importType.equals("file94")) {
                    if (line.length() < 58) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                } else {
                    if (line.length() < 70) {
                        DisplayUtil.displayError("Input line too short--skipped");
                        recordsInErrCnt++;
                        continue;
                    }
                }
                String scscId = line.substring(0, 10).trim();
                String afsId;
                String afsIdFirstChar;
                String actionType;
                String dateAchievStr;
                String lastTestDateStr;
                String s161;
                String passFail;
                String pollutantCd;
                if(importType.equals("file94")) {
                    // These are the 9.4 formats
                    afsId = line.substring(10, 15).trim();
                    afsIdFirstChar= line.substring(10, 11).trim();
                    actionType = line.substring(21, 23).trim();
                    dateAchievStr = line.substring(31, 39).trim();
                    passFail = line.substring(48, 50).trim();
                    pollutantCd = line.substring(53, 58).trim();
                    if(pollutantCd == null || pollutantCd.length() == 0) {
                        // If not pollutant code then get CAS code
                        pollutantCd = line.substring(58, 68).trim();
                    }
                } else {
                    // These are AFS import formats
                    s161 = line.substring(10, 13).trim();
                    afsId = line.substring(13, 18).trim();
                    afsIdFirstChar= line.substring(13, 14).trim();
                    actionType = line.substring(24, 26).trim();
                    dateAchievStr = line.substring(48, 54).trim();
                    passFail = line.substring(54, 56).trim();
                    pollutantCd = line.substring(68, 78).trim(); // If pollutant code not in 5 characters then CAS code will be in 9 characters.
                    if(!"161".equals(s161)) {
                        String s = "Invalid line in AFS Import file.  \"161\" not in expected positions (11-13); AFS Action ID " +
                        afsId + " and SCSC ID " + scscId + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }
                }
                if("9".equals(afsIdFirstChar)) continue;  // Added by AFS; ignore it.
                if(!checkInput(scscId, afsId, actionType, line))  {
                    recordsInErrCnt++;
                    continue;
                }
                if(!actionType.equals("37")) {
                    continue;  // skip any other action type
                }
                try {
                    List<StackTestedPollutant> pollList = stackTestService.retrieveTestPollutant(scscId, afsId);
                    if(!checkImportReturn("Stack Test", pollList, scscId, afsId)) {
                        recordsInErrCnt++;
                        continue;
                    }
                    // verify Pass/Fail
                    String stars2PassFail = pollList.get(0).getStackTestResultsCd();
                    if(passFail == null || !passFail.equals(stars2PassFail)) {
                        DisplayUtil.displayError("Pass/Fail indiction for AFS pollutant Cd " + pollutantCd +
                                " in Stack Test " + pollList.get(0).getId() + " with AFS Action ID " + afsId +
                                " and SCSC ID " + scscId + " does not match--skipped.");
                        recordsInErrCnt++;
                        continue;
                    }
                    // reverse translate the pollutant cd.
                    boolean foundMatch = false;

                    for(BaseDef bd : PollutantDef.getData().getItems().getCompleteItems().values()) {
                        if(((PollutantDef)bd).getNeiCode().equals(pollutantCd)) {
                            foundMatch = true;
                            break;
                        }
                    }
                    if(!foundMatch) {
                        DisplayUtil.displayError("No Stars2 pollutant found with AFS pollutant Cd " + pollutantCd +
                                " in Stack Test " + pollList.get(0).getId() + " with AFS Action ID " + afsId +
                                " and SCSC ID " + scscId + ".  Note that the codes sent to AFS are those under column NeiCode in the pollutant table--skipped.");
                        recordsInErrCnt++;
                        continue;
                    }
                    Timestamp lastTestDate = stackTestService.retrieveLastStackTestDate(pollList.get(0).getId());
                    if(importType.equals("file94")) {
                        lastTestDateStr = dateFormat94.format(lastTestDate);
                    } else {
                        lastTestDateStr = dateFormatAfsImport.format(lastTestDate);
                    } 
                    if(!dateAchievStr.equals(lastTestDateStr)) {  // added for validation
                        String s = "The last Test Date does not match the returning AFS Achieved Date for Stack Test Pollutant Test with AFS Action ID " +
                        afsId + ", SCSC ID " + scscId + ",Facility ID " + pollList.get(0).getFacilityId() + " and Emissins Test ID " + 
                        pollList.get(0).getId() + "--skipped.";
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        continue;
                    }

                    if(pollList.get(0).getAfsSentDate() != null && !afsSentDate.equals(pollList.get(0).getAfsSentDate())) {
                        // Already set but being changed
                        DisplayUtil.displayInfo(" AFS Sent Date being changed from " + pollList.get(0).getAfsSentDate() +
                                " for AFS Action ID " + afsId +
                                " and SCSC ID " + scscId);
                        changeSetCnt++;
                    }
                    if(pollList.get(0).getAfsSentDate() != null && afsSentDate.equals(pollList.get(0).getAfsSentDate())) {
                        // Date already set.  Do nothing
                        alreadySetCnt++;
                        continue;
                    }
                    pollList.get(0).setAfsSentDate(afsSentDate);
                    try {
                        if(!isTestMode()) {

                            // We are updating/checking the last modified of the stack test.
                            StackTest st = stackTestService.retrieveStackTestRowOnly(pollList.get(0).getStackTestId());
                            if(st == null) {
                                String s = "Error while attempting to retrieve Stack Test " + pollList.get(0).getStackTestId();
                                DisplayUtil.displayError(s);
                                recordsInErrCnt++;
                                logger.error(s);
                                continue;
                            }
                            pollList.get(0).setStackTestLastMod(st.getLastModified());
                            stackTestService.afsSetDateStackTestPollutant(pollList.get(0));
                            setAfsDateCnt++;
                        } else wouldSetAfsDateCnt++;
                    } catch (RemoteException e) {
                        String s = "An eror occurred while updating data in Stack Test "
                            + pollList.get(0).getId() + " with AFS Action ID " + afsId +
                            " and SCSC ID " + scscId;
                        DisplayUtil.displayError(s);
                        recordsInErrCnt++;
                        handleException(s, e);
                    }
                } catch (RemoteException e) {
                    handleException(e);
                }
            }
            closeReader(reader);
        } catch (IOException e) {
            DisplayUtil.displayError("Unable to read Stack Test CETA Import File.");
            logger.error("Unable to read CETA Import File.", e);
        }
        String s1 = alreadySetCnt + " record(s) already had correct AFS Sent Date set; " +
        + changeSetCnt + " record(s) with AFS Sent Date change;";
        String s0 = "";
        if(recordsInErrCnt > 0) {
            s0 = recordsInErrCnt + " record(s) in error and skipped; ";
        }
        String s3 = "";
        if(!testMode) {
            s3 =  " AFS Send Date set in " + (setAfsDateCnt - changeSetCnt) + " Stack Test(s).";
        } else s3 = " AFS Send Date would be set/updated in " + wouldSetAfsDateCnt + " Stack Test(s).";
        DisplayUtil.displayInfo(s0 + s1 + s3);
    }

    private String exportTVCCs() {
        try {
            tvCcs = complianceReportService.newAfsTvCc();
            if(testCnt != null && tvCcs.size() > testCnt) {
                tvCcs = tvCcs.subList(0, testCnt);
            }
            hasExportReview = true;
        } catch(Exception re) {
            String s = "Error while attempting export review of TVCCs.";
            DisplayUtil.displayError(s);
            logger.error(s, re);
        }
        return null;
    }

    private String exportOffSiteVisits() {
        // query all OffSite Visits which do not have the afs ID set.
        // Off Site Visits do not depend upon anything else
        /*try {
            offSiteVisits = enforcementService.newAfsOffsitePCEs();
            if(testCnt != null && offSiteVisits.size() > testCnt) {
                offSiteVisits = offSiteVisits.subList(0, testCnt);
            }
            hasExportReview = true;
        } catch(Exception re) {
            String s = "Error while attempting export review of Off Site Visits.";
            DisplayUtil.displayError(s);
            logger.error(s, re);
        }*/
        return null;
    }

    private String exportSiteVisits() {
        // query all Site Visits which do not have the afs ID set.
        // Site Visits do not depend upon anything else
        // EXCEPT Site Visit of type Stack Test cannot be sent until
        // at least one of the Stack Tests has been submitted.
        try {
            siteVisits = fullComplianceEvalService.newAfsSiteVisits();
            if(testCnt != null && siteVisits.size() > testCnt) {
                siteVisits = siteVisits.subList(0, testCnt);
            }
            hasExportReview = true;
        } catch(RemoteException re) {
            String s = "Error while attempting export review of On Site Visits.";
            DisplayUtil.displayError(s);
            handleException(s, re);
        }
        return null;
    }

    private String exportScheduledFCEs() {
        /*
         * Schedules are not sent unless committed.
         * 
         * For other object types we set the AFS Sent Date when we pull the records out of Stars2 and 
         * then when we import back in we set the AFS ID.  The records are considered locked when the 
         * Date is set.
         * When we pull records out we pull all the "eligible" records out that have no AFS ID 
         * (regardless of whether they have an AFS Date or not).  This will protect us in case we 
         * start pulling and locking records and then something happens (we don't complete it, 
         * or we don't send them to AFS or AFS does not send them back to us).  
         * In those cases we will pull them again (because their AFS IDs are still null) 
         * and this time they will have the AFS Date already set.
         * 
         * We need to make Scheduled Inspections work similarly:
         * 1.  We will pull them in exactly the same way we pull other records and set 
         *     the AFS Date as we pull them.
         * 2.  We will have a database field in the Scheduled Inspections similar to the AFS ID but 
         *     it is just used internally.
         * 3.  Once we are sure that the Scheduled Inspections have been reported, 
         * we will do an Import operation.  There will be no import file but the import will 
         * set our special AFS ID field from null to "1" for every Scheduled Inspection that has it 
         * null and has the AFS Sent Date set.  This marks the records as handled and we will not 
         * attempt to export them again later.
         */
        if(fceFfySched == null || fceFfySched.length() == 0) {
            DisplayUtil.displayError("FFY Inspection Scheduled is not set.");
            return null;
        }

        Timestamp beginSched = null;
        Timestamp endSched = null;

        Calendar cal = Calendar.getInstance();
        if(fceFfySched != null) {
            int y = Integer.parseInt(fceFfySched);
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

        if(fceFfySched != null) {
            int y = Integer.parseInt(fceFfySched);
            // compute end date--make it just beyond so test for less
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.MONTH, Calendar.OCTOBER);
            endSched = new Timestamp(cal.getTimeInMillis());
        }

        try {
            fceList = fullComplianceEvalService.newAfsScheduledFCEs(beginSched, endSched);
            if(testCnt != null && fceList.size() > testCnt) {
                fceList = fceList.subList(0, testCnt);
            }
            // Set the inspection classification.
            hasExportReview = true;
        } catch(RemoteException re) {
            String s = "Error while attempting export review of Scheduled Inspections.";
            DisplayUtil.displayError(s);
            handleException(s, re);
        }
        return null;
    }

    private String exportFCEs() {
        // Inspection reviews are not sent until there is a completion date.
        try {
            fceList = fullComplianceEvalService.newAfsFCEs();
            if(testCnt != null && fceList.size() > testCnt) {
                fceList = fceList.subList(0, testCnt);
            }
            hasExportReview = true;
        } catch(RemoteException re) {
            String s = "Error while attempting export review of Completed Inspections.";
            DisplayUtil.displayError(s);
            handleException(s, re);
        }
        return null;
    }

    private String exportEmissionsTests() {
        // Stack Tests cannot be sent until they are submitted
        try {
            stackTests = stackTestService.newAfsStackTests();
            if(testCnt != null && stackTests.size() > testCnt) {
                stackTests = stackTests.subList(0, testCnt);
            }
            for(StackTestedPollutant stp : stackTests) {
                if(stp.getTestedEu() == null || stp.getEpaEmuId() == null ||
                        stp.getPollutantCd() == null || stp.getSccId() == null
                        || stp.getTestDate() == null || stp.getStackTestResultsCd() == null) {
                    stp.setAfsExportErrors("Some required attributes are not set.  Will not be exported");
                }
            }
            hasExportReview = true;
        } catch(RemoteException re) {
            String s = "Error while attempting export review of Stack Tests.";
            DisplayUtil.displayError(s);
            handleException(s, re);
        }
        return null;
    }

    private String exportEnforcementActions() {
        // Retrieve all formal actions that do not have an AFS ID.
        /*try {
            List<Enforcement> enforceList = enforcementService.retrieveAfsEnforcementActions();
            enforceActionList = new ArrayList<EnforcementAction>(enforceList.size());
            int cnt = 0;
            for(Enforcement e : enforceList) {
                enforceActionList.add(e.getFirstEnforcementAction());
                if(testCnt != null && ++cnt >= testCnt) break;
            }
            hasExportReview = true;
        } catch(RemoteException re) {
            String s = "Error while attempting export review of Enforcement Actions.";
            DisplayUtil.displayError(s);
            handleException(s, re);
        }*/
        return null;
    }

    private String lockTvCcs() {
       // Facility f = null;
        String lastFacility = "";
        Facility scscFacility = null;
        String lastScscId = "";
        try {
            int tvCnt = 0;
            for(ComplianceReportList crl : tvCcs) {
                if(crl.getScscId() == null) {
                    DisplayUtil.displayError("SCSC ID missing for facility " + crl.getFacilityId() + "--skipped.");
                    continue;
                }
                Integer afsId;
                if(crl.getTvccAfsId() == null) {
                    if(isTestMode()) {
                        ++testAfsIdBase;
                        afsId = ++testAfsIdBase;
                    } else {
                        afsId = complianceReportService.afsLockTvCc(crl.getScscId(), crl);
                    }
                    // get two afsIds.  The higher one to put into the TV CC and one used for the AFS submmit record and one for the AFS review record.
                    crl.setTvccAfsId(Afs9_Base.convertAfsIdToString(afsId));
                }
                if(!lastScscId.equals(crl.getScscId())) {
                    // switching to another SCSC ID
                    // can now generate and write out the 9.1, 9.2 & 9.3 records
                    if(tvCnt > 0) {
                        afs_1_list.addAll(recs91);
                        afs_2_list.addAll(recs92);
                        afs_3_list.addAll(recs93);
                    }
                    // initialize to start new SCSC ID
                    lastScscId = crl.getScscId();
                    scscFacility = facilityService.retrieveMutliEstabFacilityAirProgInfo(lastScscId);
                    generateBaseRecords(scscFacility);
                    tvCnt = 0;
                }
//                if(!lastFacility.equals(crl.getFacilityId())) {
//                    lastFacility = crl.getFacilityId();
//                    // Retrieve facility information
//                    f = facilityService.retrieveFacility(crl.getFacilityId());
//                    if(!checkScscId(f, crl.getFacilityId())) {
//                        continue;
//                    }
//                    if(lastScscId.length() == 0) {
//                        lastScscId = f.getFederalSCSCId();
//                        scscFacility = facilityService.retrieveMutliEstabFacilityAirProgInfo(lastScscId);
//                    }
//                }
                ArrayList<String> airList = new ArrayList<String>();
                airList.add(AIR_PROG_TITLE_V);
                List<Afs9_4Actions> r4L = Afs9_4Actions.create94TvCc(lastScscId, airList, crl, "facility " + lastFacility);
                for(Afs9_4Actions r : r4L) {
                    r.writeRecord(file94);
                    afs_4_list.add(r);
                    tvCnt++;
                }
            }
            // handle last one
            if(tvCnt > 0) {
                generateBaseRecords(scscFacility);
                afs_1_list.addAll(recs91);
                afs_2_list.addAll(recs92);
                afs_3_list.addAll(recs93);
            }
            hasExported = true;
            closeAfsFiles();
        } catch(DAOException e) {
            String s = "Error while attempting Export of TV CCs, lastFacility is " + lastFacility + ".";
            DisplayUtil.displayError(s);
            handleException(e);
            closeAfsFiles();
        } catch(Exception re) {
            String s = "Error while attempting Export of TV CCs, lastFacility is " + lastFacility + ".";
            DisplayUtil.displayError(s);
            logger.error(s, re);
            closeAfsFiles();
        }
        return null;
    }

    private String lockOffSiteVisits() {
        //Facility f = null;
        Facility fWithOffSite = null;
        String lastFacility = "";
        Facility scscFacility = null;
        String lastScscId = "";
        try {
            Integer afsId = null;
            int offVisitCnt = 0;
            for(OffsitePCE osv : offSiteVisits) {
/*                if(osv.getScscId() == null) {
                    DisplayUtil.displayError("SCSC ID missing for facility " + osv.getFacilityId() + 
                            " and Offsite Visit database ID " + osv.getOffsitePceId() + "--skipped.");
                    continue;
                }
                if(osv.getAfsId() == null) {
                    if(isTestMode()) {
                        afsId = ++testAfsIdBase;
                    } else {
                        afsId = enforcementService.afsLockOffSiteVisit(osv.getScscId(), osv);
                    }
                    osv.setAfsId(Afs9_Base.convertAfsIdToString((afsId)));
                }*/
                if(!lastScscId.equals(osv.getScscId())) {
                    // switching to another SCSC ID
                    // can now generate and write out the 9.1, 9.2 & 9.3 records
                    if(offVisitCnt > 0) {
                        afs_1_list.addAll(recs91);
                        afs_2_list.addAll(recs92);
                        afs_3_list.addAll(recs93);
                    }
                    // initialize to start new SCSC ID
                    lastScscId = osv.getScscId();
                    scscFacility = facilityService.retrieveMutliEstabFacilityAirProgInfo(lastScscId);
                    generateBaseRecords(scscFacility);
                    offVisitCnt = 0;
                }
//                if(!lastFacility.equals(osv.getFacilityId())) {
//                    lastFacility = osv.getFacilityId();
//                    // Retrieve facility information
//                    f = facilityService.retrieveFacility(osv.getFacilityId());
//                    if(!checkScscId(f, osv.getFacilityId())) {
//                        continue;
//                    }
//                    if(lastScscId.length() == 0) {
//                        lastScscId = f.getFederalSCSCId();
//                        scscFacility = facilityService.retrieveMutliEstabFacilityAirProgInfo(lastScscId);
//                    }
//                }
                fWithOffSite = facilityService.retrieveFacility(osv.getFpId());
                if(fWithOffSite == null) {
                    DisplayUtil.displayError("Facility " + osv.getFacilityId() + " with FP ID " + osv.getFpId() + " and Off Site Visit ID " + osv.getOffsitePceId() + " not found--skipped");
                    continue;
                }
                ArrayList<String> airList = new ArrayList<String>();
                airList = determinAirProgInfoFor94(true, fWithOffSite, null);
                Afs9_4Actions r4 = Afs9_4Actions.create94OffSite(lastScscId, osv.getAfsId(), airList, osv.getActionDate(), "facility " + lastFacility);
                r4.writeRecord(file94);
                afs_4_list.add(r4);
                offVisitCnt++;
            }
//          handle last one
            if(offVisitCnt > 0) {
                generateBaseRecords(scscFacility);
                afs_1_list.addAll(recs91);
                afs_2_list.addAll(recs92);
                afs_3_list.addAll(recs93);
            }
            hasExported = true;
            closeAfsFiles();
        } catch(DAOException e) {
            String s = "Error while attempting Export of Off Site Visits, lastFacility is " + lastFacility + ".";
            DisplayUtil.displayError(s);
            handleException(e);
            closeAfsFiles();
        } catch(Exception re) {
            String s = "Error while attempting Export of Off Site Visits, lastFacility is " + lastFacility + ".";
            DisplayUtil.displayError(s);
            logger.error(s, re);
            closeAfsFiles();
        }
        return null;
    }

    private String lockSiteVisits() {
        //Facility f = null;
        String lastFacility = "";
        Facility scscFacility = null;
        String lastScscId = "";
        try {
            Integer afsId = null;
            int a4RecCnt = 0;
            for(SiteVisit sv : siteVisits) {
                if(sv.getScscId() == null) {
                    DisplayUtil.displayError("SCSC ID missing for facility " + sv.getFacilityId() + 
                            " and Site Visit database ID " + sv.getId() + "--skipped.");
                    continue;
                }
                if(sv.getAfsId() == null) {
                    if(isTestMode()) {
                        afsId = ++testAfsIdBase;
                    } else {
                        afsId = fullComplianceEvalService.afsLockSiteVisit(sv.getScscId(), sv);
                    }
                    sv.setAfsId(Afs9_Base.convertAfsIdToString((afsId)));
                }
                if(!lastScscId.equals(sv.getScscId())) {
                    // switching to another SCSC ID
                    // can now generate and write out the 9.1, 9.2 & 9.3 records
                    if(a4RecCnt > 0) {
                        afs_1_list.addAll(recs91);
                        afs_2_list.addAll(recs92);
                        afs_3_list.addAll(recs93);
                    }
                    // initialize to start new SCSC ID
                    lastScscId = sv.getScscId();
//                    if("3915100006".equals(lastScscId)) {
//                        DisplayUtil.displayError("found 3915100006");
//                    }
                    scscFacility = facilityService.retrieveMutliEstabFacilityAirProgInfo(lastScscId);
                    generateBaseRecords(scscFacility);
                    a4RecCnt = 0;
                }
//                if(!lastFacility.equals(sv.getFacilityId())) {
//                    lastFacility = sv.getFacilityId();
//                    // Retrieve facility information
//                    f = facilityService.retrieveFacility(sv.getFacilityId());
//                    if(!checkScscId(f, sv.getFacilityId())) {
//                        continue;
//                    }
//                }
                FacilityHistory fh = null;
                fh = facilityHistoryService.retrieveFacilityHistory(sv.getFacilityHistId());
                if(fh == null) {
                    DisplayUtil.displayError("Did not find Air Program History for facility " + sv.getFacilityId() +
                            " and Site Visit database ID " + sv.getId() + " --skipped.");
                    continue;
                }
                ArrayList<String> airList = new ArrayList<String>();
                airList = determinAirProgInfoFor94(true, null, fh);
                Afs9_4Actions r4 = Afs9_4Actions.create94SiteVisit(sv.getScscId(), sv.getAfsId(), airList, sv.getVisitDate(), "facility " + lastFacility);
                r4.writeRecord(file94);
                afs_4_list.add(r4);
                a4RecCnt++;
            }
            // handle last one
            if(a4RecCnt > 0) {
                generateBaseRecords(scscFacility);
                afs_1_list.addAll(recs91);
                afs_2_list.addAll(recs92);
                afs_3_list.addAll(recs93);
            }
            hasExported = true;
            closeAfsFiles();
        } catch(DAOException e) {
            String s = "Error while attempting Export of On Site Visits, lastFacility is " + lastFacility + ".";
            DisplayUtil.displayError(s);
            handleException(e);
            closeAfsFiles();
        } catch(Exception re) {
            String s = "Error while attempting Export of On Site Visits, lastFacility is " + lastFacility + ".";
            DisplayUtil.displayError(s);
            logger.error(s, re);
            closeAfsFiles();
        }
        return null;
    }

    private String lockScheduledFCEs() {
        Facility f = null;
        String lastFacility = "";
        try {
            for(FullComplianceEval fce : fceList) {
                if(fce.getScscId() == null) {
                    DisplayUtil.displayError("SCSC ID missing for facility " + fce.getFacilityId() + 
                            " and Inspection " + fce.getId() + "--skipped.");
                    continue;
                }
                if(fce.getSchedAfsId() == null) {
                    // need to set the AFS ID
                    fce.setSchedAfsId("Exported");
                    if(!isTestMode()) {
                        fullComplianceEvalService.afsLockFceSched(fce.getScscId(), fce);
                    }
                }
                if(!lastFacility.equals(fce.getFacilityId())) {
                    lastFacility = fce.getFacilityId();
                    // Retrieve current facility information
                    f = facilityService.retrieveFacility(fce.getFacilityId());
                }
                AfsFceSched r = new AfsFceSched(f, fce);
                r.writeRecord(fileSched);
                afs_sched_list.add(r);
            }
            hasSchedExported = true;
            closeSchedFiles();
        } catch(DAOException e) {
            String s = "Error while attempting Export of Scheduled Inspections, lastFacility is " + lastFacility + ".";;
            DisplayUtil.displayError(s);
            handleException(e);
        } catch(Exception re) {
            String s = "Error while attempting Export of Scheduled Inspections, lastFacility is " + lastFacility + ".";;
            DisplayUtil.displayError(s);
            logger.error(s, re);
        }
        return null;
    }

    private String lockFCEs() {
        //Facility f = null;
        String lastFacility = "";
        Facility scscFacility = null;
        String lastScscId = "";
        try {
            Integer recCnt = testCnt;
            int fceCnt = 0;
            for(FullComplianceEval fce : fceList) {
                if(fce.getScscId() == null) {
                    DisplayUtil.displayError("SCSC ID missing for facility " + fce.getFacilityId() + 
                            " and Inspection " +  fce.getId() + "--skipped.");
                    continue;
                }
                if(isTestMode() && recCnt != null) { 
                    recCnt--; 
                    if(recCnt < 0) break; 
                }
                Integer afsId = null;
                if(fce.getEvalAfsId() == null) {
                    if(isTestMode()) {
                        afsId = ++testAfsIdBase;
                    } else {
                        afsId = fullComplianceEvalService.afsLockFceComp(fce.getScscId(), fce);
                    }
                    fce.setEvalAfsId(Afs9_Base.convertAfsIdToString((afsId)));
                }
                if(!lastScscId.equals(fce.getScscId())) {
                    // switching to another SCSC ID
                    // can now generate and write out the 9.1, 9.2 & 9.3 records
                    if(fceCnt > 0) {
                        afs_1_list.addAll(recs91);
                        afs_2_list.addAll(recs92);
                        afs_3_list.addAll(recs93);
                    }
                    // initialize to start new SCSC ID
                    lastScscId = fce.getScscId();
                    scscFacility = facilityService.retrieveMutliEstabFacilityAirProgInfo(lastScscId);
                    generateBaseRecords(scscFacility);
                    fceCnt = 0;
                }
//                if(!lastFacility.equals(fce.getFacilityId())) {
//                    lastFacility = fce.getFacilityId();
//                    // Retrieve facility information
//                    f = facilityService.retrieveFacility(fce.getFacilityId());
//                    if(!checkScscId(f, fce.getFacilityId())) {
//                        continue;
//                    }
//                }
                FacilityHistory fh = null;
                fh = facilityHistoryService.retrieveFacilityHistory(fce.getFacilityHistId());
                if(fh == null) {
                    DisplayUtil.displayError("Did not find Air Program History for facility " + fce.getFacilityId() +
                            " and Inspection ID " + fce.getId() + "--skipped.");
                    continue;
                }
                ArrayList<String> airList = new ArrayList<String>();
                airList = determinAirProgInfoFor94(true, null, fh);
                Afs9_4Actions r4 = Afs9_4Actions.create94FCE(lastScscId, fce.getEvalAfsId(), airList, fce.getDateEvaluated(),
                        "facility " + lastFacility);
                r4.writeRecord(file94);
                afs_4_list.add(r4);
                fceCnt++;
            }
            // handle last one
            if(fceCnt > 0) {
                generateBaseRecords(scscFacility);
                afs_1_list.addAll(recs91);
                afs_2_list.addAll(recs92);
                afs_3_list.addAll(recs93);
            }
            hasExported = true;
            closeAfsFiles();
        } catch(DAOException e) {
            String s = "Error while attempting Export of Completed Inspections, lastFacility is " + lastFacility + ".";
            DisplayUtil.displayError(s);
            handleException(e);
            closeAfsFiles();
        } catch(Exception re) {
            String s = "Error while attempting Export of Completed Inspections, lastFacility is " + lastFacility + ".";
            DisplayUtil.displayError(s);
            logger.error(s, re);
            closeAfsFiles();
        }
        return null;
    }

    private String lockEmissionsTests() {
        // Stack Tests cannot be sent until they are submitted
        Facility f = null;
        Facility fWithTest = null;
        String lastFacility = "";
        Facility scscFacility = null;
        String lastScscId = "";
        StackTestedPollutant savedStp = null;
        try {
            Integer afsId = null;
            int stCnt = 0;
            for(StackTestedPollutant stp : stackTests) {
                if(stp.getScscId() == null) {
                    DisplayUtil.displayError("SCSC ID missing for facility " + stp.getFacilityId() + 
                            " and Stack Test " + stp.getStackTestId() + "--skipped.");
                    continue;
                }
                if(stp.getAfsExportErrors() != null) continue;
                savedStp = stp;
                if(stp.getAfsId() == null) {
                    if(isTestMode()) {
                        afsId = ++testAfsIdBase;
                    } else {
                        // We are updating/checking the last modified of the stack test.
                        StackTest st = stackTestService.retrieveStackTestRowOnly(stp.getStackTestId());
                        if(st == null) {
                            String s = "Error while attempting to retrieve Stack Test " + stp.getStackTestId();
                            DisplayUtil.displayError(s);
                            logger.error(s);
                            continue;
                        }
                        stp.setStackTestLastMod(st.getLastModified());
                        afsId = stackTestService.afsLockStackTestPollutant(stp.getScscId(), stp);
                    }
                    stp.setAfsId(Afs9_Base.convertAfsIdToString((afsId)));
                }
                
                if(!lastFacility.equals(stp.getFacilityId())) {
                    lastFacility = stp.getFacilityId();
                    // Retrieve facility information
                    f = facilityService.retrieveFacility(stp.getFacilityId());
                    if(!checkScscId(f, stp.getFacilityId())) {
                        continue;
                    }
                    fWithTest = facilityService.retrieveFacility(stp.getFpId());
                }
                
                if(!lastScscId.equals(stp.getScscId())) {
                    // switching to another SCSC ID
                    // can now generate and write out the 9.1, 9.2 & 9.3 records
                    if(stCnt > 0) {
                        afs_1_list.addAll(recs91);
                        afs_2_list.addAll(recs92);
                        afs_3_list.addAll(recs93);
                    }
                    // initialize to start new SCSC ID
                    lastScscId = stp.getScscId();
                    scscFacility = facilityService.retrieveMutliEstabFacilityAirProgInfo(lastScscId);
                    generateBaseRecords(scscFacility);
                    stCnt = 0;
                } 
                
                EmissionUnit eu = f.getMatchingEmissionUnit(stp.getTestedEu());
                if(eu == null) {
                    String s = "No EU found for " + stpInfo(stp) + "--skipped.";
                    DisplayUtil.displayError(s);
                } else {
                    if(fWithTest == null) {
                        DisplayUtil.displayError("Facility " + stp.getFacilityId() + " with FP ID " + stp.getFpId() + " and Stack Test ID " + stp.getStackTestId() + " not found--skipped");
                        continue;
                    }
                    ArrayList<String> airList = new ArrayList<String>();
                    airList = determinAirProgInfoFor94(true, fWithTest, null);
                    Afs9_4Actions r4 = Afs9_4Actions.create94EmissionTest(f.getFederalSCSCId(), airList, stp.getTestDate(),
                            stp, eu, "Facility " + lastFacility + ", EU " + eu.getEpaEmuId() + ", Stack Test "
                            + stp.getStackTestId() + ", pollutant code " + stp.getPollutantCd());
                    r4.writeRecord(file94);
                    afs_4_list.add(r4);
                    stCnt++;
                }
            }
//          handle last one
            if(stCnt > 0) {
                generateBaseRecords(scscFacility);
                afs_1_list.addAll(recs91);
                afs_2_list.addAll(recs92);
                afs_3_list.addAll(recs93);
            }
            hasExported = true;
            closeAfsFiles();
        } catch(DAOException e) {
            String s = "Error while attempting Export of Stack Tests.  Processing " + stpInfo(savedStp);
            DisplayUtil.displayError(s);
            handleException(e);
            closeAfsFiles();
        }catch(Exception re) {
            String s = "Error while attempting Export of Stack Tests.  Processing " + stpInfo(savedStp);
            DisplayUtil.displayError(s);
            logger.error(s, re);
            closeAfsFiles();
        }
        return null;
    }

    private String stpInfo(StackTestedPollutant stp) {
        if(stp == null) return "prior to first row";
        return " Stack Test ID=" + stp.getStackTestId().toString() + " EU=" + stp.getEpaEmuId() + ", SCC ID=" + stp.getSccId() + ", pollutant code=" + stp.getPollutantCd();
    }

    /*private String lockEnforcementActions() {
       // Facility f = null;
        Facility scscFacility = null;
        String lastScscId = "";
        Enforcement enf = null;
        Integer lastEnforcementId = null;
        EnforcementAction ea1 = null;
        Integer enforceNotOutOfComplianceId = null;
        try {
            int eaCnt = 0;
            boolean actionOutOfCompliance = true;
            for(EnforcementAction ea : enforceActionList) {
                if(ea.getEnforcement().getScscId() == null) {
                    DisplayUtil.displayError("SCSC ID missing for facility " + ea.getFacilityId() + 
                            ", Enforcement " + ea.getEnforcementId() + " and Action Date " + ea.getActionDate() +"--skipped.");
                    continue;
                }
                if(enforceNotOutOfComplianceId != null && enforceNotOutOfComplianceId.equals(ea.getEnforcementId())) {
                    String possibleErr = "Previous Action had no Air Programs out of compliance in Air Program History for facility " 
                        + ea.getFacilityId() + ", Enforcement " + ea.getEnforcementId() + " and Action Date " + ea.getActionDate() +
                     "--skipped.";
                    DisplayUtil.displayError(possibleErr);
                    continue;
                } else enforceNotOutOfComplianceId = null;
                if(ea.getDiscoveryObjAfsId() == null && ea.getEnfDiscoveryId() != null) {
                    // There is a discovery object but it does not have an AFS ID.
                    continue;
                }
                ea1 = ea;     
                // get next AFS ID for enforcement action
                Integer afsId = null;

                // >>>>>Begin Just figure out number of Action IDs needed and how many before and after our Enforcement Action.
                // >>>>>Generate the 9.1, 9.2 and 9.3 records but not the 9.4 records yet
                int numBefore = 0;
                int numAfter = 0;
                boolean newCase = false;

                if(lastEnforcementId == null || !lastEnforcementId.equals(ea.getEnforcementId())) {
                    // new case
                    newCase = true;
                    actionOutOfCompliance = true;
                    lastEnforcementId = ea.getEnforcementId();
                    enf = enforcementService.retrieveEnforcementOnly(lastEnforcementId);
                    if(enf == null) {
                        String s = "Failed to locate Enforcement " + lastEnforcementId;
                        logger.error(s); DisplayUtil.displayError(s);
                        return null;
                    }

                    if(!lastScscId.equals(enf.getScscId())) {
                        // switching to another SCSC ID
                        // can now generate and write out the 9.1, 9.2 & 9.3 records
                        if(eaCnt > 0) {
                            afs_1_list.addAll(recs91);
                            afs_2_list.addAll(recs92);
                            afs_3_list.addAll(recs93);
                            eaCnt = 0;
                        }
                        // initialize to start new SCSC ID
                        lastScscId = enf.getScscId();
                        scscFacility = facilityService.retrieveMutliEstabFacilityAirProgInfo(lastScscId);
                        generateBaseRecords(scscFacility);
                        eaCnt = 0;
                    }


                    
                    // know it is first time if day zero afs id not yet sent.
                    if(enf.isAddToHPVList() && enf.getAfsDate() == null) {
                         The first time we need to send DayZero.
                         * 38 - State Day Zero. 
                         * and
                         * VL - Date the '38' is added to AFS.
                         
                        if(enf.getDayZeroAfsId() == null) {
                            // If Id set then that and the next one are for 38 and VL.
                            numBefore++; numBefore++;
                        }
                    }
                }
                FacilityHistory fh = null;
                String possibleErr = "No Air Programs out of compliance in Air Program History for facility " + ea.getFacilityId() +
                ", Enforcement " + ea.getEnforcementId() + " and Action Date " + ea.getActionDate() + "--skipped.";
                fh = facilityHistoryService.retrieveFacilityHistory(ea.getFacilityHistId());
                if(fh == null) {
                    DisplayUtil.displayError("Did not find Air Program History for facility " + ea.getFacilityId() +
                            ", Enforcement " + ea.getEnforcementId() + " and Action Date " + ea.getActionDate() + " assuming not out of compliance--skipped.");
                    enforceNotOutOfComplianceId = ea.getEnforcementId(); // remember; skip as if not out of compliance
                    continue;
                }
                // don't check resolving or closing actions
                if(actionOutOfCompliance && !AFSEnforcementTypeDef.isResolvingAction(ea.getEnfActionTypeCd()) && 
                        !AFSEnforcementTypeDef.isClosingAction(ea.getEnfActionTypeCd())) {
                    // see if now not out of compliance--to skip the rest
                    actionOutOfCompliance = !fh.allInCompliance();
                }
                // But check all Actions, so the first one that gets skipped results in all being skipped.
                if(!actionOutOfCompliance) {
                    DisplayUtil.displayError(possibleErr);
                    enforceNotOutOfComplianceId = ea.getEnforcementId(); // remember the id to skip all the rest.
                    continue;
                }
                ArrayList<String> airList = new ArrayList<String>();
                airList = determinAirProgInfoFor94(false, null, fh);

                 So the discovery object (for HPV) is the initiating action.
                 *
                 *  The discovery object is then just another record in the 9.4 actions file 
                 *  for HPV, with the same structure as the other records. 
                 *  The action type is "##" and the AFS ID of the discovery object is used.
                 

                // Actions the first of which requires an RT:  '05', '05A', '66', '66A', '68', 'X3'
                if(enf.isAddToHPVList() &&
                        (AFSEnforcementTypeDef.AFS_FINAL_COMPLIANCE_05.equals(ea.getEnfActionTypeCd().substring(0, 2))
                       || AFSEnforcementTypeDef._66.equals(ea.getEnfActionTypeCd().substring(0, 2))
                       || AFSEnforcementTypeDef.LOCAL_FINDINGS_AND_ORDERS_68.equals(ea.getEnfActionTypeCd())
                       || AFSEnforcementTypeDef.G4.equals(ea.getEnfActionTypeCd()))
                          && enforcementService.isNeedRT(ea)) {
                    numBefore++;
                }
                
                if(enf.isAddToHPVList() &&
                        (AFSEnforcementTypeDef.AFS_FINAL_COMPLIANCE_05.equals(ea.getEnfActionTypeCd().substring(0, 2)))) {
  
                    // Actions which if missing when we encounter a '05' or '05A' require an A4:  '66', '66A', '68', 'G4', 'X3'
                    if(enforcementService.isNotNeedA4(ea)) {
                        numBefore++;
                    }
                    numBefore++;
                }

                // The action in Stars2

                // If the action is REFER_TO_AGO_X3 then generate an "OT"
                if(enf.isAddToHPVList() && AFSEnforcementTypeDef.REFER_TO_AGO_X3.equals(ea.getEnfActionTypeCd())) {
                    numAfter++;
                }
   
                // <<<<<After Just figure out number of Action IDs needed and how many before and after our Enforcement Action.

                // get the AFS Action ID numbers
                if(ea.getAfsId() == null) {
                    if(isTestMode()) {
                        afsId = testAfsIdBase + numBefore + 1;
                        testAfsIdBase = afsId + numAfter;
                    } else {
                        afsId = enforcementService.afsLockEnfAction(ea.getEnforcement().getScscId(), ea, numBefore, numAfter);
                    }
                    ea.setAfsId(Afs9_Base.convertAfsIdToString((afsId)));
                } else afsId = Integer.parseInt(ea.getAfsId());
                Integer IdNxt = afsId - numBefore;

                if(newCase) {
                    // new case

                    // know it is first time if day zero afs id not yet sent.
                    if(enf.isAddToHPVList() && enf.getAfsDate() == null) {
                         The first time we need to send DayZero.
                         * 38 - State Day Zero. 
                         * and
                         * VL - Date the '38' is added to AFS.
                         
                        String dayZeroAfsId;
                        String afsVLId;
                        if(enf.getDayZeroAfsId() == null) {
                            dayZeroAfsId = Afs9_Base.convertAfsIdToString((IdNxt++));
                            afsVLId = Afs9_Base.convertAfsIdToString((IdNxt++));
                            enf.setDayZeroAfsId(dayZeroAfsId);
                            if(!isTestMode()) {
                                enforcementService.afsLockZeroIdEnf(enf);
                            }
                        } else {
                            // If Id set then that and the next one are for 38 and VL.
                            dayZeroAfsId = enf.getDayZeroAfsId();
                            afsVLId = Afs9_Base.convertAfsIdToString(Integer.parseInt(dayZeroAfsId) + 1);
                        }

                        Afs9_4Actions rDayZero = Afs9_4Actions.create94EnfGhost(lastScscId, enf.getDayZeroAfsId(), airList, 
                                AFSEnforcementTypeDef.STATE_DAY_ZERO_38, enf.getDayZeroDate(), enf.getDayZeroAfsId(), 
                                "facillity " + enf.getFacilityId() + ", AFS ID " + ea.getAfsId());
                        rDayZero.writeRecord(file94);
                        afs_4_list.add(rDayZero);
                        eaCnt++;

                        Timestamp todaysDate = null;
                        Calendar cal = Calendar.getInstance();
                        todaysDate = new Timestamp(cal.getTimeInMillis());
                        Afs9_4Actions rVL = Afs9_4Actions.create94EnfGhost(lastScscId, afsVLId, airList, 
                                AFSEnforcementTypeDef.DATE_DAY_ZERO_ADDED_TO_AFS_VL, todaysDate, enf.getDayZeroAfsId(), 
                                "facillity " + enf.getFacilityId() + ", AFS ID " + ea.getAfsId());
                        rVL.writeRecord(file94);
                        afs_4_list.add(rVL);
                        eaCnt++;
                    }
                }

                 So the discovery object (for HPV) is the initiating action.
                 *
                 *  The discovery object is then just another record in the 9.4 actions file 
                 *  for HPV, with the same structure as the other records. 
                 *  The action type is "##" and the AFS ID of the discovery object is used.
                 *  No new AFS Action ID is needed/used.
                 
                // Set airList depending upon whether Resolving Action
                if(AFSEnforcementTypeDef.AFS_FINAL_COMPLIANCE_05.equals(ea.getEnfActionTypeCd().substring(0, 2))
                       || AFSEnforcementTypeDef._66.equals(ea.getEnfActionTypeCd().substring(0, 2))
                       || AFSEnforcementTypeDef.LOCAL_FINDINGS_AND_ORDERS_68.equals(ea.getEnfActionTypeCd())
                       || AFSEnforcementTypeDef.REFER_TO_AGO_X3.equals(ea.getEnfActionTypeCd())
                       || AFSEnforcementTypeDef.WITHDRAWL_OF_ENFORCEMENT_ACTION_EW.equals(ea.getEnfActionTypeCd())
                       || AFSEnforcementTypeDef.CASE_CLOSED_BANKRUPTCY_OR_LIMITATIONS_K9.equals(ea.getEnfActionTypeCd())) {
                    // Should be nothing out of compliance so just specify the program
                    airList = determinAirProgInfoFor94_05(fh);
                }
                // Date Violation Discovered & Discovery Object
                if(enf.isAddToHPVList() && ea.getEnfDiscoveryId() != null) {
                    // Note that discovery object is optional.
                    // Any validation that there is at least one is performed when Enforcement Actions are created.
                    String sharpSharpCode = "";
                    if(AFSActionDiscoveryTypeDef.FCE.equals(ea.getEnfDiscoveryTypeCd())) sharpSharpCode = "81";
                    else if(AFSActionDiscoveryTypeDef.OFF_SITE_HPV.equals(ea.getEnfDiscoveryTypeCd())) sharpSharpCode = "80";
                    else if(AFSActionDiscoveryTypeDef.SITE_VISIT.equals(ea.getEnfDiscoveryTypeCd())) sharpSharpCode = "83";
                    else if(AFSActionDiscoveryTypeDef.STACK_TEST.equals(ea.getEnfDiscoveryTypeCd())) sharpSharpCode = "37";
                    else if(AFSActionDiscoveryTypeDef.TITLE_V_ANNUAL_COMPLIANCE_CERTIFICATIONS.equals(ea.getEnfDiscoveryTypeCd())) sharpSharpCode = "SR"; 

                    Afs9_4Actions r4 = Afs9_4Actions.create94EnfGhost(lastScscId, ea.getDiscoveryObjAfsId(), airList, 
                            sharpSharpCode, ea.getDiscoveryObjDate(),
                            enf.getDayZeroAfsId(), "facillity " + enf.getFacilityId() + ", AFS ID " + ea.getAfsId());
                    r4.writeRecord(file94);
                    afs_4_list.add(r4);
                    eaCnt++;
                }

                String nxtAfsId;
                // Actions the first of which requires an RT:  '05', '05A', '66', '66A', '68', 'X3'
                if(enf.isAddToHPVList() &&
                        (AFSEnforcementTypeDef.AFS_FINAL_COMPLIANCE_05.equals(ea.getEnfActionTypeCd().substring(0, 2))
                       || AFSEnforcementTypeDef._66.equals(ea.getEnfActionTypeCd().substring(0, 2))
                       || AFSEnforcementTypeDef.LOCAL_FINDINGS_AND_ORDERS_68.equals(ea.getEnfActionTypeCd())
                       || AFSEnforcementTypeDef.REFER_TO_AGO_X3.equals(ea.getEnfActionTypeCd()))
                          && enforcementService.isNeedRT(ea)) {
                    Timestamp todaysDate = null;
                    Calendar cal = Calendar.getInstance();
                    todaysDate = new Timestamp(cal.getTimeInMillis());

                    nxtAfsId = Afs9_Base.convertAfsIdToString((IdNxt++));
                    Afs9_4Actions rRT = Afs9_4Actions.create94EnfGhost(lastScscId, nxtAfsId, airList, 
                            AFSEnforcementTypeDef.DATE_FINDINGS_OR_FINAL_COMPLIANCE_ADDED_TO_AFS_RT, todaysDate,  
                            enf.getDayZeroAfsId(), "facillity " + enf.getFacilityId() + ", AFS ID " + ea.getAfsId());
                    rRT.writeRecord(file94);
                    afs_4_list.add(rRT);
                    eaCnt++;
                }
                
                if(enf.isAddToHPVList() &&
                        (AFSEnforcementTypeDef.AFS_FINAL_COMPLIANCE_05.equals(ea.getEnfActionTypeCd().substring(0, 2)))) {
  
                    // Actions which if missing when we encounter a '05' or '05A' require an A4:  '66', '66A', '68', 'X3'
                    if(enforcementService.isNotNeedA4(ea)) {
                        nxtAfsId = Afs9_Base.convertAfsIdToString((IdNxt++));
                        Afs9_4Actions rDayZero = Afs9_4Actions.create94EnfGhost(lastScscId, nxtAfsId, airList, 
                                AFSEnforcementTypeDef.DATE_FINDINGS_OR_FINAL_COMPLIANCE_WAS_DECIDED_A4, ea.getActionDate(), 
                                enf.getDayZeroAfsId(), "facillity " + enf.getFacilityId() + ", AFS ID " + ea.getAfsId());
                        rDayZero.writeRecord(file94);
                        afs_4_list.add(rDayZero);
                        eaCnt++;
                    }
                    
                    nxtAfsId = Afs9_Base.convertAfsIdToString((IdNxt++));
                    Afs9_4Actions r44 = Afs9_4Actions.create94EnfGhost(lastScscId, nxtAfsId, airList, 
                            AFSEnforcementTypeDef.WHAT_IS_THE_DESCRIPTION_44, ea.getActionDate(), 
                            enf.getDayZeroAfsId(), "facillity " + enf.getFacilityId() + ", AFS ID " + ea.getAfsId());
                    r44.writeRecord(file94);
                    afs_4_list.add(r44);
                    eaCnt++;
                }
                // The action in Stars2
                IdNxt++;  // skip over one for Enforcement Action
                Afs9_4Actions r5 = Afs9_4Actions.create94Enf(lastScscId, ea.getAfsId(), airList, 
                        ea.getEnfActionTypeCd(), ea.getActionDate(), ea.getFinalCashAmount(), 
                        enf.getDayZeroAfsId(), ea.getAfsMemo(), "facillity " + enf.getFacilityId() + ", AFS ID " + ea.getAfsId());
                r5.writeRecord(file94);
                afs_4_list.add(r5);
                eaCnt++;

                Timestamp todaysDate = null;
                Calendar cal = Calendar.getInstance();
                todaysDate = new Timestamp(cal.getTimeInMillis());
                // If the action is REFER_TO_AGO_X3 then generate an "OT"
                if(enf.isAddToHPVList() && AFSEnforcementTypeDef.REFER_TO_AGO_X3.equals(ea.getEnfActionTypeCd())) {
                    nxtAfsId = Afs9_Base.convertAfsIdToString((IdNxt++));
                    Afs9_4Actions rOT = Afs9_4Actions.create94EnfGhost(lastScscId, nxtAfsId, airList, 
                            AFSEnforcementTypeDef.DATE_REFER_TO_AGO_ADDED_TO_AFS_OT, todaysDate, 
                            enf.getDayZeroAfsId(), "facillity " + enf.getFacilityId() + ", AFS ID " + ea.getAfsId());
                    rOT.writeRecord(file94);
                    afs_4_list.add(rOT);
                    eaCnt++;
                }
            }
//          handle last one
            if(eaCnt > 0) {
                generateBaseRecords(scscFacility);
                afs_1_list.addAll(recs91);
                afs_2_list.addAll(recs92);
                afs_3_list.addAll(recs93);
            }
            hasExported = true;
            closeAfsFiles();
        } catch(DAOException e) {
            String lastEaInfo = "";
            if(ea1 != null) {
                lastEaInfo = ", last Enforcement Action Id is " + ea1.getActionId().toString();
                closeAfsFiles();
            }
            String s = "Error while attempting Export of Enforcement Action, lastEnforcementId is " + 
            lastEnforcementId + lastEaInfo + ".";
            DisplayUtil.displayError(s);
            handleException(e);
            closeAfsFiles();
        } catch(Exception re) {
            String lastEaInfo = "";
            if(ea1 != null) {
                lastEaInfo = ", last Enforcement Action Id is " + ea1.getActionId().toString();
                closeAfsFiles();
            }
            String s = "Error while attempting Export of Enforcement Action, lastEnforcementId is " + 
            lastEnforcementId + lastEaInfo + ".";
            DisplayUtil.displayError(s);
            logger.error(s, re);
        }
        return null;
    }*/

    boolean checkScscId(Facility f, String facilityId) {
        if(f == null || f.getFederalSCSCId() == null || f.getFederalSCSCId().length() != 10) {
            DisplayUtil.displayError("Facility " + facilityId + " not found or has incorrect length SCSC ID");
            return false;
        }
        return true;
    }

    private final void resetInternal() {
        clearLists();
        cetaImportFile = null;
        hasImportResults = false;
        hasExportReview = false;
        hasExported = false;
        hasSchedExported = false;
    }

    public final String reset() {
        resetInternal();
        objectType = null;
        exportImport = null;
        importType = "fileAfs";
        testMode = false;
        testCnt = null;
        return SUCCESS;
    }

    private void wrongTypeMsg(String actionId, String line) {
        String s = "ActionType is not " + actionId + ".  Import record skipped \"" + line.replaceAll(" ", ".") + "\"";
        logger.error(s);
        DisplayUtil.displayError(s);
    }

    private boolean checkInput(String scscId, String afsId, String actionType, String line) {
        boolean rtn = true;  
        String msg = "";
        if(scscId == null || scscId.length() == 0) {
            rtn = false; msg = msg + "SCSC ID is not set; "; 
        }
        if(afsId == null || afsId.length() == 0) {
            rtn = false; msg = msg + "AFS Action ID is not set; "; 
        }
        if(actionType == null || actionType.length() == 0) {
            rtn = false; msg = msg + "Action Type is not set"; 
        }
        if(!rtn) {
            String s = msg + " Import record skipped. Line is \"" + line.replaceAll(" ", ".") + "\"";
            logger.error(s);
            DisplayUtil.displayError(s);
        }
        return rtn;
    }

    private boolean checkImportReturn(String type, List l, String scscId, String afsId) {
        if (l == null || l.size() == 0) {
            DisplayUtil.displayError("No " + type + " found with AFS Action ID " + afsId +
                    " and SCSC ID " + scscId + "--skipped.");
            return false;
        }
        if (l.size() > 1) {
            DisplayUtil.displayError("More than one " + type + " found with AFS Action ID " + afsId +
                    " and SCSC ID " + scscId + "--skipped.");
            return false;
        }
        return true;
    }

    private void closeReader(BufferedReader reader) {
        try {
            reader.close();
        } catch(IOException ioe) {
            String s = "Failed to close import file for reason " + ioe.getMessage();
            DisplayUtil.displayError(s);
            logger.error(s, ioe);
        }
    }

    void initAfsFiles() {
        tmpDirName = "";
        String userName = null;
        try {
            int userId = InfrastructureDefs.getCurrentUserId();
            UserDef currentUser = infrastructureService.retrieveUserDef(userId);
            userName = currentUser.getNetworkLoginNm();
            tmpDirName = documentService.createTmpDir(userName);
        } catch(RemoteException re) {
            String s = "Error while attempting to create files.";
            DisplayUtil.displayError(s);
            handleException(s, re);
            hasExportReview = false;
            return;
        }
        try {
            rootSize = DocumentUtil.getFileStoreRootPath().length() + 1;
            f91 = new File(DocumentUtil.getFileStoreRootPath() + tmpDirName + File.separator + "f9.1_" + fileNameComponent + ".txt");
            file91 = new PrintStream(f91);
            f92 = new File(DocumentUtil.getFileStoreRootPath() + tmpDirName + File.separator + "f9.2_" + fileNameComponent + ".txt");
            file92 = new PrintStream(f92);
            f93 = new File(DocumentUtil.getFileStoreRootPath() + tmpDirName + File.separator + "f9.3_" + fileNameComponent + ".txt");
            file93 = new PrintStream(f93);
            f94 = new File(DocumentUtil.getFileStoreRootPath() + tmpDirName + File.separator + "f9.4_" + fileNameComponent + ".txt");
            file94 = new PrintStream(f94);
            fz = new File(DocumentUtil.getFileStoreRootPath() + tmpDirName + File.separator + fileNameComponent + ".zip");
        } catch(IOException e) {
            String s = "Unable to create files";
            logger.error(s, e);
            hasExportReview = false;
        }
    }

    void createSchedFiles() {
        String tmpDirName = "";
        String userName = null;
        try {
            int userId = InfrastructureDefs.getCurrentUserId();
            UserDef currentUser = infrastructureService.retrieveUserDef(userId);
            userName = currentUser.getNetworkLoginNm();

            tmpDirName = documentService.createTmpDir(userName);
        } catch(RemoteException re) {
            String s = "Error while attempting to create files.";
            DisplayUtil.displayError(s);
            handleException(s, re);
            hasExportReview = false;
            return;
        }
        try {
            rootSize = DocumentUtil.getFileStoreRootPath().length() + 1;
            schedFileName = tmpDirName + File.separator + "afs_" + fileNameComponent + ".csv";
            fSched = new File(DocumentUtil.getFileStoreRootPath() + schedFileName);
            fileSched = new PrintStream(fSched);
        } catch(IOException e) {
            String s = "Unable to create files";
            logger.error(s, e);
            hasExportReview = false;
        }
    }


    void closeAfsFiles() {
        if(file91 != null) file91.flush();
        if(file92 != null) file92.flush();
        if(file93 != null) file93.flush();
        if(file94 != null) file94.flush();
        if(file91 != null) file91.close();
        if(file92 != null) file92.close();
        if(file93 != null) file93.close();
        if(file94 != null) file94.close();
    }

    void closeSchedFiles() {
        fileSched.flush();
        fileSched.close();
    }

    private void generateBaseRecords(Facility f) throws Exception {
        recs91 = new ArrayList<Afs9_1PlantGeneral>();
        recs92 = new ArrayList<Afs9_2PlantGeneral>();
        recs93 = new ArrayList<Afs9_3Pollutant>();
        String ident = "Facility " + f.getFacilityId() + ", generateBaseRecords for ";
        Afs9_1PlantGeneral r = new Afs9_1PlantGeneral(f);
        r.writeRecord(file91);
        recs91.add(r);

        String sipCompCd = convertComplianceCd(f.getSipCompCd(), logger, ident + "SIP");
        String opCode = convertOpCode(f.getOperatingStatusCd());
        boolean attainment = true;
        String attain = convertAttainment(attainment);
        Afs9_2PlantGeneral r2;
        Afs9_3Pollutant r3;

        //Put TV/FESOP first followed by SIP so the first air program specifies TV/FESOP or neither
        // Air Program TV or FESOP
/*        if(InspectionClassDef.TV.equals(f.getAirProgramCd())) {
            r2 = new Afs9_2PlantGeneral(f.getFederalSCSCId(), AIR_PROG_TITLE_V, opCode, new String[0], ident);
            r2.writeRecord(file92);
            recs92.add(r2);

            r3 = new Afs9_3Pollutant(f.getFederalSCSCId(), AIR_PROG_TITLE_V, FACIL, 
                    convertComplianceCd(f.getAirProgramCompCd(), logger, ident + "TV"),  attain, ident + "TV");
            r3.writeRecord(file93);
            recs93.add(r3);
        } else if(InspectionClassDef.FEPTIO.equals(f.getAirProgramCd())) {
            r2 = new Afs9_2PlantGeneral(f.getFederalSCSCId(), AIR_PROG_FESOP, opCode, new String[0], ident);
            r2.writeRecord(file92);
            recs92.add(r2);
            r3 = new Afs9_3Pollutant(f.getFederalSCSCId(), AIR_PROG_FESOP, FACIL, 
                    convertComplianceCd(f.getAirProgramCompCd(), logger, ident + "FESOP"), attain, ident + "FESOP");
            r3.writeRecord(file93);
            recs93.add(r3);
        } */

        // SIP
        r2 = new Afs9_2PlantGeneral(f.getFederalSCSCId(), AIR_PROG_SIP, opCode, new String[0], ident);
        r2.writeRecord(file92);
        recs92.add(r2);

        r3 = new Afs9_3Pollutant(f.getFederalSCSCId(), AIR_PROG_SIP, FACIL, 
                sipCompCd, attain, ident + "SIP");
        r3.writeRecord(file93);
        recs93.add(r3);

        // PSD
        r2 = null;
        // NESHAPS
        Afs9_Base base = new Afs9_Base();
        boolean baseFail = false;
        if(f.getNspsSubparts() != null && f.getNeshapsSubparts().size() > 0) {
            String[] subParts = new String[f.getNeshapsSubparts().size()];
            int i = 0;
            for(String s : f.getNeshapsSubparts()) {
                PTIONESHAPSSubpartDef d = PTIONESHAPSSubpartDef.getSubpartDef(s);
                String afsCd = null;
                if(d == null) {
                    logger.error("Failed to locate NSPS Subpart code " + s);
                } else {
                    afsCd = d.getCode();
                }
                subParts[i++] = base.insertValueNoTruncate(Afs9_Base.blanks(5), afsCd, ident + "NESHAP");
                if(base.isRecordInError()) baseFail = true;
            }

            r2 = new Afs9_2PlantGeneral(f.getFederalSCSCId(), AIR_PROG_NESHAP, opCode, subParts, ident + "NESHAP");
            if(baseFail) r2.setRecordInError(baseFail);
            r2.writeRecord(file92);
            recs92.add(r2);
        }
        if(f.getNeshapsSubpartsCompCds() != null && f.getNeshapsSubpartsCompCds().size() > 0) {
        	ArrayList<PollutantCompCode> lst = new ArrayList<PollutantCompCode>();
            for(PollutantCompCode pcc : f.getNeshapsSubpartsCompCds()) {
                String neshapSubpart = pcc.getPollutantCd();
                PTIONESHAPSSubpartDef neshap = (PTIONESHAPSSubpartDef)PTIONESHAPSSubpartDef.getData().getItems().getItem(neshapSubpart);
                String pollCd = null;
                if(neshap != null) {
                    pollCd = neshap.getPollutantCd();
//                    if(pollCd != null) {
//                        pollCd = convertPollutantCd(pollCd);
//                    }
                }
                addPollutantCompInfo(lst, pcc, pollCd, ident + "NESHAPS");
            }
            for(PollutantCompCode pcc : lst) {
                r3 = new Afs9_3Pollutant(f.getFederalSCSCId(), AIR_PROG_NESHAP, convertPollutantCd(pcc.getPollutantCd()), 
                        convertComplianceCd(pcc.getPollutantCompCd(), logger, ident + "NESHAPS"), attain, ident + "NESHAPS");
                r3.writeRecord(file93);
                recs93.add(r3);
            }
        }

        // NSPS
        base = new Afs9_Base();
        baseFail = false;
        r2 = null;
        if(f.getNspsSubparts() != null && f.getNspsSubparts().size() > 0) {
            String[] subParts = new String[f.getNspsSubparts().size()];
            int i = 0;
            for(String s : f.getNspsSubparts()) {
                PTIONSPSSubpartDef d = PTIONSPSSubpartDef.getSubpartDef(s);
                String afsCd = null;
                if(d == null) {
                    logger.error("Failed to locate NSPS Subpart code " + s);
                } else {
                    afsCd = d.getNspsSubpartAfsCd();
                }
                subParts[i++] = base.insertValueNoTruncate(Afs9_Base.blanks(5), afsCd, ident + "NSPS");
                if(base.isRecordInError()) baseFail = true;
            }

            r2 = new Afs9_2PlantGeneral(f.getFederalSCSCId(), AIR_PROG_NSPS, opCode, subParts, ident + "NSPS");
            if(baseFail) r2.setRecordInError(baseFail);
        }
        
        // MACT
        base = new Afs9_Base();
        baseFail = false;
        if(f.isMact()) {
            if(f.getMactSubparts() != null && f.getMactSubparts().size() >0) {
                String[] subParts = new String[f.getMactSubparts().size()];
                int i = 0;
                for(String s : f.getMactSubparts()) {
                    String afsMactCd = Afs9_Base.convetMactSubpartCd(s);
                    subParts[i++] = base.insertValueNoTruncate(Afs9_Base.blanks(5), afsMactCd, ident + "MACT");
                    if(base.isRecordInError()) baseFail = true;
                }

                r2 = new Afs9_2PlantGeneral(f.getFederalSCSCId(), AIR_PROG_MACT, opCode, subParts, ident + "MACT");
                if(baseFail) r2.setRecordInError(baseFail);
                r2.writeRecord(file92);
                recs92.add(r2);
                r3 = new Afs9_3Pollutant(f.getFederalSCSCId(), AIR_PROG_MACT, THAP, 
                        convertComplianceCd(f.getMactCompCd(), logger, ident + "MACT"), attain, ident + "MACT");
                r3.writeRecord(file93);
                recs93.add(r3);
            }
        }

        // NSR
        r2 = null;
        
    }

    private static String convertComplianceCd(String stars2CompCd, Logger logger, String ident) {
        if("Y".equals(stars2CompCd)) return "3";
        if("N".equals(stars2CompCd)) return "1";
        if("O".equals(stars2CompCd)) return "5";
        logger.error("Failed to convert compliance status for " + ident);
        return "?";
    }

    private static String convertInspClassCd(String classCd, Logger logger, String ident) {
        if(classCd == null || classCd.length() == 0) return " ";
        if("TV".equals(classCd)) return "A";
        if("MEGA".equals(classCd)) return "A";
        if("SM".equals(classCd)) return "SM";
        if("NHPF".equals(classCd)) return "B";
        return "?";
    }

    private static String convertOpCode(String opCd) {
        if("sd".equals(opCd)) return "X";
        return "O";
    }

    private static String convertAttainment(boolean opCd) {
        if(opCd) return "A";
        return "N";
    }

    static String convertPollutantCd(String code) throws ApplicationException {
        String afsPollCd = PollutantDef.getNeiCode(code);
        return afsPollCd;
    }
    
    static String convertEquivAFSCd(String code) throws ApplicationException {
        String afsEquivPollCd = PollutantDef.getAfsEquivPollutantCd(code);
        if(afsEquivPollCd != null)
        	return afsEquivPollCd;
        else
        	return code;
    }

//  static String convertPollutantCd(String code) {
////conversions
//  if("PM-PRI".equals(code))
//  code = "PT";  
//  else if("7664939".equals(code))
//  code = "HSO4P";  
//  else if("7439921".equals(code)) 
//  code = "PB"; 
//  else if("7783064".equals(code))
//  code = "H2S";  
//  else if("463581".equals(code)) 
//  code = "COS";  
//  else if("75150".equals(code)) 
//  code = "CADIS";  
//  else if("7647010".equals(code))  // Hydrogen Chloride
//  code = "HCL";  
//  else if("7664393".equals(code)) 
//  code = "HF";  
//  else if("7782505".equals(code)) 
//  code = "CL";  
//  else if("7439976".equals(code)) // mercury
//  code = "HG";  
//  else if("109".equals(code)) 
//  code = "BE";  
//  else if("75014".equals(code))
//  code = "VC";  
//  else if("7440382".equals(code)) // Arsenic
//  code = "AS";  
//  else if("PM10-FIL".equals(code))
//  code = "PM10";  
//  else if("7440360".equals(code)) // Antimony
//  code = "SB-PT";  
//  else if("7440417".equals(code))  // Beryllium
//  code = "BE";
//  else if("7440439".equals(code))  // Cadmium
//  code = "CD";
//  else if("7440473".equals(code))  // Chromium
//  code = "CRC";  
//  else if("7440508".equals(code))  // Copper
//  code = "CU-PT";  
//  else if("7439965".equals(code))  // Manganese
//  code = "MN-PT";  
//  else if("7440393".equals(code))  // Barium
//  code = "BA-PT";
//  else if("67561".equals(code)) // Methanol
//  code = "MTNOL"; 
//  else if("TI".equals(code)) // Thallium
//  code = "7440280";  
//  else if("50000".equals(code)) // Formaldehyde
//  code = "FORM";  
//  else if("132649".equals(code)) // DIBENZOFURAN
//  code = "DBNZF";  
//  else if("7440022".equals(code)) // Nickel Powder
//  code = "NI-PT";  
//  return code;
//  }

    public final DefSelectItems getYearsForFCEsDef() {
        return YearsForFCEs.getData().getItems();
    }

    private void zipUpFiles() {
        try {

            stackTestService.generateTempAttachmentZipFile(fileNameComponent,
                    f91.getCanonicalPath().substring(rootSize), f92.getCanonicalPath().substring(rootSize),
                    f93.getCanonicalPath().substring(rootSize), f94.getCanonicalPath().substring(rootSize),
                    fz.getCanonicalPath().substring(rootSize));
        } catch(RemoteException re){
            logger.error("zipUpFiles() failed", re);
        } catch(IOException re){
            logger.error("zipUpFiles() failed", re);
        }  
    }

    public final List<ComplianceReport> getReportList() {
        return reportList;
    }
    public final void setReportList(List<ComplianceReport> reportList) {
        this.reportList = reportList;
    }
    public String getExportImport() {
        return exportImport;
    }
    public void setExportImport(String exportImport) {
        this.exportImport = exportImport;
        resetInternal();

    }
    public String getObjectType() {
        return objectType;
    }
    public void setObjectType(String objectType) {
        this.objectType = objectType;
        resetInternal();
        // if("sf".equals(operating)) importType = "file94";
        if("tv".equals(objectType)) {
            whatExported ="AFS Export of:  Accepted TV CCs with Compliance Status of Comply or Intermittant Comply or Not Comply";
        } else if("fce".equals(objectType)) {
            whatExported ="AFS Export of:  Completed Inspections whether committed to the US EPA or not";
        } else if("sf".equals(objectType)) {
            whatExported ="AFS Export of:  Scheduled Inspections that are marked committed to the US EPA";
        } else if("sv".equals(objectType)) {
            whatExported ="AFS Export of:  Site Visits";
        } else if("osv".equals(objectType)) {
            whatExported ="AFS Export of:  " + otherName + " Records";
        } else if("et".equals(objectType)) {
            whatExported ="AFS Export of:  Submitted Stack Tests";
        } else if("ea".equals(objectType)) {
            whatExported ="AFS Export of:  Enforcement Actions";
        }
    }

    public String getImportButtonLabel() {
        if(objectType == null || exportImport == null) return null;
        String type;
        if("file94".equals(importType)) type = "9.4 Export ";
        else type = "CETA Import ";
        if("tv".equals(objectType)) {
            importButtonLabel = type + "TV CC File: ";  
        } else if("fce".equals(objectType)) {
            importButtonLabel = type + "Inspection Completed File: ";
        } else if("sf".equals(objectType)) {
            importButtonLabel = "Export Inspection Scheduled File: ";
        } else if("sv".equals(objectType)) {
            importButtonLabel = type + "Site Visit File: ";
        }else if("osv".equals(objectType)) {
            importButtonLabel = type + " " + otherName + " File: ";
        } else if("et".equals(objectType)) {
            importButtonLabel = type + "Stack Test File: ";
        } else if("ea".equals(objectType)) {
            importButtonLabel = type + "Enforcement Action File: ";
        }
        return importButtonLabel;
    }

    public boolean isOperating() {
        return operating;
    }
    public void setOperating(boolean operating) {
        this.operating = operating;
    }
    public List<SiteVisit> getSiteVisits() {
        return siteVisits;
    }
    public boolean isHasImportResults() {
        return hasImportResults;
    }
    public void setHasImportResults(boolean hasImportResults) {
        this.hasImportResults = hasImportResults;
    }
    public List<StackTestedPollutant> getStackTests() {
        return stackTests;
    }
    public List<FullComplianceEval> getFceList() {
        return fceList;
    }
    public boolean isHasExportReview() {
        return hasExportReview;
    }
    public void setHasExportReview(boolean hasExportReview) {
        this.hasExportReview = hasExportReview;
    }
    public List<EnforcementAction> getEnforceActionList() {
        return enforceActionList;
    }
    public void setEnforceActionList(List<EnforcementAction> enforceActionList) {
        this.enforceActionList = enforceActionList;
    }
    public String getFceFfySched() {
        return fceFfySched;
    }
    public void setFceFfySched(String fceFfySched) {
        this.fceFfySched = fceFfySched;
        resetInternal();
    }

    public boolean isHasExported() {
        return hasExported;
    }

    public void setHasExported(boolean hasExported) {
        this.hasExported = hasExported;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public String getDocsZipFileName() {
        return fileNameComponent + ".zip";
    }

    public String getDocsZipFileURL() {
        Document d = new Document();
        try {
            d.setBasePath(fz.getCanonicalPath().substring(rootSize));
        } catch(IOException e) {
            String s = "Failed to get zip file " + fz.getAbsolutePath();
            DisplayUtil.displayError(s);
            logger.error(s, e);
        }
        return d.getDocURL();
    }

    public String getSchedFileDoc() {
        Document d = new Document();
        try {
            d.setBasePath(fSched.getCanonicalPath().substring(rootSize));
        } catch(IOException e) {
            String s = "Failed to get scheduled file " + fSched.getAbsolutePath();
            DisplayUtil.displayError(s);
            logger.error(s, e);
        }
        return d.getDocURL();
    }

    public String getDocsSchedFileName() {
        return fileNameComponent + ".csv";
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public List<Afs9_2PlantGeneral> getRecs92() {
        return recs92;
    }

    public ArrayList<Afs9_1PlantGeneral> getAfs_1_list() {
        return afs_1_list;
    }

    public ArrayList<Afs9_3Pollutant> getAfs_3_list() {
        return afs_3_list;
    }

    public ArrayList<Afs9_4Actions> getAfs_4_list() {
        return afs_4_list;
    }

    public Integer getTestCnt() {
        return testCnt;
    }

    public void setTestCnt(Integer testCnt) {
        this.testCnt = testCnt;
    }

    public ArrayList<Afs9_2PlantGeneral> getAfs_2_list() {
        return afs_2_list;
    }

    public List<OffsitePCE> getOffSiteVisits() {
        return offSiteVisits;
    }

    public List<ComplianceReportList> getTvCcs() {
        return tvCcs;
    }

    public void setTvCcs(List<ComplianceReportList> tvCcs) {
        this.tvCcs = tvCcs;
    }

    public boolean isHasSchedExported() {
        return hasSchedExported;
    }

    public ArrayList<AfsFceSched> getAfs_sched_list() {
        return afs_sched_list;
    }

    public String getLockButton() {
        return lockButton;
    }

    public String getWhatExported() {
        return whatExported;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    ArrayList<String> determinAirProgInfoFor94(boolean onlyOne, Facility f, FacilityHistory fh) {
        ArrayList<String> airList = new ArrayList<String>();
        int onlyOneCnt = 0;
        if(fh == null) {
            // Title V or SM/FESOP
            boolean inCompliance = !ComplianceStatusDef.NO.equals(f.getAirProgramCompCd());
/*            if(InspectionClassDef.TV.equals(f.getAirProgramCd())) {
                if(onlyOne || !inCompliance) {
                    airList.add(AIR_PROG_TITLE_V);
                }
                onlyOneCnt++;
            } else if(InspectionClassDef.FEPTIO.equals(f.getAirProgramCd())) {
                if(onlyOne || !inCompliance) {
                    airList.add(AIR_PROG_FESOP);
                }
                onlyOneCnt++;
            } */

            // SIP
            inCompliance = !ComplianceStatusDef.NO.equals(f.getSipCompCd());
            if(onlyOne && onlyOneCnt == 0 || !onlyOne && !inCompliance) {
                airList.add(AIR_PROG_SIP);
            }
            if(onlyOne) return airList;

            // NESHAPS
            if(f.getNspsSubparts() != null && f.getNeshapsSubparts().size() > 0) {
                inCompliance = true;
                for (PollutantCompCode neshapsSubpart : f.getNeshapsSubpartsCompCds()) {
                    if (ComplianceStatusDef.NO.equals(neshapsSubpart.getPollutantCompCd())) {
                        inCompliance = false;
                    }
                }
                if(!inCompliance) {
                    airList.add(AIR_PROG_NESHAP);
                } 
            }

            // MACT
            if(f.isMact()) {
                inCompliance = !ComplianceStatusDef.NO.equals(f.getMactCompCd());
                if(!inCompliance) {
                    airList.add(AIR_PROG_MACT);
                }
            }

        } else {
            // Title V or SM/FESOP
            boolean inCompliance = !ComplianceStatusDef.NO.equals(fh.getAirProgramCompCd());
/*            if(InspectionClassDef.TV.equals(fh.getAirProgramCd())) {
                if(onlyOne || !inCompliance) {
                    airList.add(AIR_PROG_TITLE_V);
                }
                onlyOneCnt++;
            } else if(InspectionClassDef.FEPTIO.equals(fh.getAirProgramCd())) {
                if(onlyOne || !inCompliance) {
                    airList.add(AIR_PROG_FESOP);
                }
                onlyOneCnt++;
            }*/

            // SIP
            inCompliance = !ComplianceStatusDef.NO.equals(fh.getSipCompCd());
            if(onlyOne && onlyOneCnt == 0 || !onlyOne && !inCompliance) {
                airList.add(AIR_PROG_SIP);
            }
            if(onlyOne) return airList;

            // PSD
            if(fh.getPsdCompStatusList() != null && fh.getPsdCompStatusList().size() > 0) {
                inCompliance = true;
                for (FacilityComplianceStatus psdPollutant : fh.getPsdCompStatusList()) {
                    if (ComplianceStatusDef.NO.equals(psdPollutant.getComplianceCd())) {
                        inCompliance = false;
                    }
                }
                if(!inCompliance) {
                    airList.add(AIR_PROG_PSD);
                }          
            }

            // NESHAPS
            if(fh.getNeshapsCompStatusList() != null && fh.getNeshapsCompStatusList().size() > 0) {
                inCompliance = true;
                for (FacilityComplianceStatus neshapsSubpart : fh.getNeshapsCompStatusList()) {
                    if (ComplianceStatusDef.NO.equals(neshapsSubpart.getComplianceCd())) {
                        inCompliance = false;
                    }
                }
                if(!inCompliance) {
                    airList.add(AIR_PROG_NESHAP);
                } 
            }

            // NSPS
            if(fh.getNspsCompStatusList() != null && fh.getNspsCompStatusList().size() > 0) {
                inCompliance = true;
                for (FacilityComplianceStatus nspsPollutant : fh.getNspsCompStatusList()) {
                    if (ComplianceStatusDef.NO.equals(nspsPollutant.getComplianceCd())) {
                        inCompliance = false;
                    }
                }
                if(!inCompliance) {
                    airList.add(AIR_PROG_NSPS);
                }
            }

            // MACT
            if(fh.isMact()) {
                inCompliance = !ComplianceStatusDef.NO.equals(fh.getMactCompCd());
                if(!inCompliance) {
                    airList.add(AIR_PROG_MACT);
                }
            }

            // NSR
            if(airList.size() < 6 && fh.getNsrNonAttainmentCompStatusList() != null && fh.getNsrNonAttainmentCompStatusList().size() > 0) {
                inCompliance = true;
                for (FacilityComplianceStatus nsrPollutant : fh.getNsrNonAttainmentCompStatusList()) {
                    if (ComplianceStatusDef.NO.equals(nsrPollutant.getComplianceCd())) {
                        inCompliance = false;
                    }
                }
                if(!inCompliance) {
                    airList.add(AIR_PROG_NSR);
                }
            }
        }
        return airList;
    }
    
    ArrayList<String> determinAirProgInfoFor94_05(FacilityHistory fh) {
        // Just V, F or O and ignore compliance
        ArrayList<String> airList = new ArrayList<String>();
        boolean needSip = true;
/*        if(InspectionClassDef.TV.equals(fh.getAirProgramCd())) {
            airList.add(AIR_PROG_TITLE_V);
            needSip = false;
        } else if(InspectionClassDef.FEPTIO.equals(fh.getAirProgramCd())) {
            airList.add(AIR_PROG_FESOP);
            needSip = false;
        } */

        // SIP
        if(needSip) {
            airList.add(AIR_PROG_SIP);
        }
        return airList;
    }
    
    private void addPollutantCompInfo(List<PollutantCompCode> lst, PollutantCompCode pcc, String pollCd, String ident) {
    	PollutantCompCode pcc2 = new PollutantCompCode();
    	pcc2.setPollutantCd(pollCd);  // use pollutant not subpart
    	pcc2.setPollutantCompCd(pcc.getPollutantCompCd());
    	addPollutantCompInfo(lst, pcc2, ident);
    }
    
    private void addPollutantCompInfo(List<PollutantCompCode> lst, PollutantCompCode pcc, String ident) {
        // Add pcc to the list unless it already is present in some form, in which case use worst compliance code.
        boolean inList = false;
        String pCd;
        try {
        pCd = convertEquivAFSCd(pcc.getPollutantCd()); // convert to AFS pollutant (Stars2 code)
        if(pCd == null) return;
        } catch(ApplicationException ae) {
            DisplayUtil.displayError("Attempt to locate Equivalent AFS pollutant using " + pcc.getPollutantCd() + " failed for " + ident);
            pCd = pcc.getPollutantCd();  // use original code.
        }
        PollutantCompCode newPcc = new PollutantCompCode(); // don't modify original object
        newPcc.setPollutantCd(pCd);
        newPcc.setPollutantCompCd(pcc.getPollutantCompCd());
        for(PollutantCompCode cc : lst) {
            if(pCd.equals(cc.getPollutantCd())) {
                // use worst compliance code.
                if(cc.getPollutantCompCd() == null) {
                    cc.setPollutantCompCd(newPcc.getPollutantCompCd());
                }else if(newPcc.getPollutantCompCd() != null && cc.getPollutantCompCd().compareTo(newPcc.getPollutantCompCd()) > 0) {
                    cc.setPollutantCompCd(newPcc.getPollutantCompCd());
                }
                inList = true;
                break;
            }
        }
        if(!inList) lst.add(newPcc);
    }
}
