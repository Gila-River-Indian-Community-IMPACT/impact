package us.oh.state.epa.stars2.webcommon.reports;

/*
 * May not be consistent in using treeReport everywhere.
 */

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsVariable;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityVersion;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.def.ContentTypeDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.EmissionsAttachmentTypeDef;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.RegulatoryRequirementTypeDef;
import us.oh.state.epa.stars2.def.ReportEisStatusDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.FacilityEmissionFlow;
import us.oh.state.epa.stars2.webcommon.Stars2TreeNode;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.oh.state.epa.stars2.webcommon.facility.FacilityReference;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;

/*
 * TODO  handle failure from getYearlyReportingInfo()
 */

/*
 * editReport() -- (from facilityRep.jsp) 
 * saveReport -- (from facilityRep.jsp) 
 * 
 * createNewEmissionReport() -- from erSearchTable.jsp
 * startCreateReportDone () -- from createReport.jsp
 * 
 *  startReportCopyFinished() -- returnListener from createReport.jsp
 *  startCreateReportData() -- from createReport.jsp
 *  startReportCopy(), in derived class -- from erCopyTable.jsp (from createReportData.jsp)
 *  startReportCopyI() -- called from startReportCopy()
 * 
 * startCreateRevisedReport() -- from facilityRep.jsp
 * startCreateReviseReportDone() -- from reviseReport.jsp  
 * 
 * selectCompareReport() -- from facilityRep.jsp
 * compareToPrevious() -- from facilityRep.jsp
 * cancelCompareReportDialog() -- from selectCompareReport.jsp
 * startReportCompare() -- from erSelectTable.jsp
 * compareReportOK() --(from facilityRep.jsp, emissionUnitRep.jsp,
 *                      emissionUnitGrpRep.jsp & emissionPeriod.jsp
 * 
 * createGroup()  -- (from emissionPeriod.jsp) Create a group given a process under an EU.
 * editEmission() -- (from emissionPeriod.jsp) Edit emissions.
 * saveEmission() -- (from emissionPeriod.jsp) Save results.
 * addEmission() -- (from emissionPeriod.jsp) add new emission row
 * deleteEmission()  -- (from emissionPeriod.jsp) delete emission row
 * cancelEmissionEdit() -- (from emissionPeriod.jsp) cancel operation.
 * 
 * NOTE:  Material EDIT ALSO INCLUCDES SCHEDULE AND SEASONS.
 * editMaterial() -- (from emissionPeriod.jsp) Edit material/throughput.
 * saveMaterial() -- (from emissionPeriod.jsp) Save material/throughputresults.
 * changeMaterial() -- the select button pushed to change material
 * cancelMaterial() -- (from emissionPeriod.jsp) cancel material/throughput operation.
 * 
 * editEmissionGroup() -- (from emissionUnitGrpRep.jsp) Edit the group members and/or group name
 * saveEmissionGroup() -- (from emissionUnitGrpRep.jsp) Save results
 * cancelEmissionGroup -- (from emissionUnitGrpRep.jsp) Do not save results
 * createGroupPop() -- (from facilityRep.jsp)  list processes to pick one for group
 * createEUListPop() -- (from facilityRep.jsp)  list EUs to set/reset EG#71
 * editEmissionUnit() -- (from emissionUnitRep.jsp)
 * saveEmissionUnit() -- (from emissionUnitRep.jsp)
 * cancelEmissionUnitEdit() -- (from emissionUnitRep.jsp)
 * 
 * requestDeletePeriod() -- (from emissionPeriod.jsp)
 * deletePeriod() -- (from deletePeriod.jsp) period no longer belongs so delete it from report
 * 
 * requestEditFP() -- (from facilityRep.jsp) Ask to create a facility version to edit
 * editFacilityProfile()  Actually split the facility inventory.
 * 
 * submitProfile()  -- from facilityRep.jsp
 * 
 * submitReport() -- (from facilityRep.jsp)
 * submitVerify() -- (from facilityRep.jsp)
 * 
 * addEmissionsRptInfo -- (from facility/yearlyReportingInfo.jsp)
 * editYearlyInfo -- (from facility/yearlyReportingInfo.jsp)
 * saveYearlyInfo -- (from facility/yearlyReportingInfo.jsp)
 * cancelYearlyInfo -- (from facility/yearlyReportingInfo.jsp)
 * editViewYearlyComment -- (from facility/yearlyReportingInfo.jsp
 * 
 * editFire -- (from fireTable.jsp)
 * closeCancelFIRE -- (from fireTable.jsp)
 * saveFireSelection -- (from fireTable.jsp)
 * setEmissionRowMethod() --(from emissionsTable.jsp)
 * 
 * TRADE SECRET STUFF
 * editViewFactorTS -- (from emissionsTable.jsp)
 * editViewExpTS -- (from emissionsTable.jsp)
 * editViewMaterialTS -- (from emissionsTable.jsp)
 * editViewThroughputTS -- (from emissionsTable.jsp)
 * editViewSchedTS --(from emissionPeriod.jsp)
 * applyEditTS -- (from tradeSecretJust.jsp)  used for OK
 * cancelEditTS -- (from tradeSecretJust.jsp) used for both Cancel and Close.
 * 
 * Logic in the get methods:
 * getCurrentGrpEUs() -- from emissionUnitGrpRep.jsp
 * getCurrentCompareGrpEUs() -- from emissionUnitGrpRep.jsp
 * getAllGrpEUs() -- from emissionUnitGrpRep.jsp
 * getAllCompareGrpEUs() -- from emissionUnitGrpRep.jsp
 * 
 * Delete the report
 * deleteReport() -- from deleteReportReq.jsp popup
 * startDeleteReport() -- from facilityRep.jsp
 * 
 * enableAllReporting() -- from enableReporting.jsp
 * 
 * getFacilityHistory() -- from associateProfile.jsp
 * associateFP() -- from facilityRep.jsp
 * ReportProfile.associateProfile() -- from associateProfile.jsp
 * 
 * startViewNote() -- from notesTable.jsp
 * startEditNote() -- noteDetail.jsp
 * cancelEditNote() -- noteDetail.jsp
 * applyEditNote() -- noteDetail.jsp
 * 
 * reopenEdit() -- from facilityRep.jsp -when STARS2ADMIN is user
 */
public abstract class ReportProfileBase extends ReportBaseCommon {

	private static final long serialVersionUID = 958913625668152148L;

	public final static String noRptReq = " report not created because no emissions reporting is required for ";
	public final static String noRptReq2 = ".  If this is not correct, contact AQD.";
	private static String hapNonAttestationMsg;

	// public final static String REPORTING_ID = "reportProfile:";

	public final static String TV_REPORT = "emissionReport";
	public final static String HOME_TV_REPORT = "home.reports.reportDetail";

	// Table used by processChices.jsp
	private ArrayList<ProcessGroupChoice> processGroupTable = new ArrayList<ProcessGroupChoice>(
			0);
	private ProcessGroupChoice firstGrpProcess = null; // element selected in
														// jsp from
														// processGroupTable

	// Table used by processEG71Choieces.jsp
	private ArrayList<EuEg71Choice> euEg71Table = new ArrayList<EuEg71Choice>(0);
	// Table used by report confirmation save
	private ArrayList<EuEg71Choice> haveEmissions = new ArrayList<EuEg71Choice>();

	// List of TV/SMTV reports to select one to copy for new report
	ArrayList<EmissionsReportSearch> selectRptList = null;

	// Begin from TreeBase
	private TreeNode selectedTreeNode;
	protected String current;
	// End from TreeBase

	// Keep track of what current tree node was
	private String previousCurrent = "";

	private String rptTitle = "Emissions Inventory";

	// reportTitle is used on report pages.
	private String reportTitle = rptTitle;

	private boolean facilityEditable; // Can the facility be edited?

	// the report used by the getTreeData() routine.
	private EmissionsReport treeReport;

	private boolean secondPopup; // Indicates if second popup window created.

	private String displayTotal = null;

	private TreeModelBase treeData;
	private String treeDataId = ""; // which report is tree for?

	private EmissionsReportEU emissionUnit; // Selected Emission Unit

	private Integer euEmissionChoice; // copy of eu emissions choice (report,
										// eg71, zero)
	private boolean emissionsNeedClearing;

	private Integer rptEmissionChoice = 2; // 0 - zero emissions (did not
											// operate), 2 - report emission
											// details.
	private Integer compareRptEmissionChoice = 2;

	private EmissionsReportEUGroup grpEmissionUnit; // Selected group

	private String grpName; // attribute used on the web page

	private EmissionsReportPeriod emissionsPeriod; // Selected period

	private EmissionsReportPeriod webPeriod; // Copy of selected period
	private EmissionsReportPeriod savedWebPeriod = null; // saved to preserve
															// schedule & season
															// values.
	private EmissionsReportPeriod savedWebPeriod2 = null; // saved to preserve
															// schedule & season
															// values.

	private String renderComponent = "report";

	private boolean editableM; // edit material/throughput

	EmissionsRptInfo rptInfo; // Info about report to create

	private List<MissingFIREFactor> missingFactors;
	private TableSorter missingFactorsWrapper = new TableSorter();

	// Map identifier to tree node
	private HashMap<String, Stars2TreeNode> treeNodeMap;

	// Message to display
	private String userMsg = "Report Error";

	// Period info
	private boolean discloseEmissions = true; // Is period info filled out?
	private String sccCodeStrLine; // to display on period page
	private FireRow[] periodFireRows;
	private boolean materialSelected; // If material selected, display emissions
										// table
	private ArrayList<EmissionsMaterialActionUnits> periodMaterialList;

	// private ArrayList<SelectItem> activeMaterials; // for pick list on
	// emissions page

	private TableSorter periodMaterialWrapper = new TableSorter();
	private TableSorter periodMaterialCompWrapper = new TableSorter();

	private List<EmissionsVariable> periodVariableWrapper = new ArrayList<EmissionsVariable>(
			0);
	private TableSorter periodVariableCompWrapper = new TableSorter();
	boolean addedRow = false;

	boolean reportLevelOnly = false;

	// Emissions
	// Four wrappers needed because if two are in the same (expanded)
	// .jsp, then only the last one is populated.
	private TableSorter emissionReportWrapper = new TableSorter();
	private TableSorter emissionReportWrapperHAP = new TableSorter();
	private int displayHapRows = 0;
	private boolean showDisplayAll = true;
	private boolean showDisplaySome = false;
	private int numHapRows = 0;

	protected ArrayList<EmissionRow> reportEmissions;

	private TableSorter emissionGroupWrapper = new TableSorter();
	private TableSorter emissionGroupWrapperHAP = new TableSorter();

	private TableSorter emissionEuWrapper = new TableSorter();
	private TableSorter emissionEuWrapperHAP = new TableSorter();

	private TableSorter emissionPeriodWrapper = new TableSorter();
	private ArrayList<EmissionRow> capSublist; // the sublist of CAPs put into
												// Wraper
	private TableSorter emissionPeriodWrapperHAP = new TableSorter();
	private ArrayList<EmissionRow> hapSublist; // the sublist of HAPs put into
												// WraperTab2
	private boolean hapTable = false; // only true if emissionPeriodWrapperHAP
										// has emissions rows
	private boolean didPositionToLast = false;

	// The emissions rows displayed for the period
	private ArrayList<EmissionRow> periodEmissions;
	private String condensibleTotal;
	private String condensibleUnit;
	private String filterableTotal;
	private String filterableUnit;

	private EmissionRow emissionRow;

	private boolean renderSum = true;

	// For Emission Unit Group Page
	private boolean removeGroup = false; // should group be deleted.
	// Used in shuttle
	private List<Integer> currentGrpEUs = new ArrayList<Integer>();
	// Used in shuttle
	private List<Integer> currentCompareGrpEUs = new ArrayList<Integer>();
	// Used in table
	private List<String> currentTblGrpEUs = new ArrayList<String>();
	private List<String> currentTblCompareGrpEUs = new ArrayList<String>();
	// Used in shuttle
	private List<SelectItem> allGrpEUs = new ArrayList<SelectItem>();
	// Used in shuttle
	private List<SelectItem> allCompareGrpEUs = new ArrayList<SelectItem>();

	// Report Comparisons
	private String compRptTitle = "Emissions Inventory Comparison with Report ";

	private Integer selectedReportId;

	private Facility compareFacility;
	private EmissionsReport compareReport;
	private SccCode compareScc; // The SCC code of the comparison period (or
								// null)
	private boolean doRptCompare;

	private boolean expandedExp;
	private int expRows = 2;

	private Integer percentDiff = new Integer(20); // Set on web page

	// FIRE updates
	public EmissionRow emissionRowMethod;
	private TableSorter fireWrapper = new TableSorter();

	private ArrayList<FireRow> rowFireTable;

	// Create new report variables
	private String create_facilityId = null; // identification passed to
	private Integer create_year = new Integer(-1);
	private String createContentTypeCd;
	private boolean enableRptCopy; // if TV/SMTV allow copy of other report
	private Facility create_facility = null;
	private boolean copyFromExistingRpt = false; // true means create with
													// existing data
	private String createReviseMsg = null;
	private boolean creatingRevisedRpt = false; // true means revision, false
												// means new
	private EmissionsReportSearch[] existingReports = null;
	private boolean createRptUsingProfile = true;

	// used by:enableAllReporting
	private Integer enableYear = new Integer(-1);
	private boolean enableAllRptDef;
	private boolean enableAllFIRE;
	private boolean enableAllSCC;

	private LinkedHashMap<Integer, Integer> reportYears;

	private LinkedHashMap<String, String> reportContentTypes;

	private ArrayList<EmissionsReportSearch> reportsSameYear;

	// For facility level report information.
	// From FP_Yearly_Reporting_Category.
	// Use of this functionality does not affect other objects in ReportProfile
	// bean.
	private Integer savedFpId; // Used only for category info on
	// Used with facility/yearlyReportingInfo.jsp
	private EmissionsRptInfo emissionsRptInfoRow;

	// facility page
	private EmissionsRptInfo currentRptInfo; // used on facility page
	private EmissionsRptInfo previousRptInfo; // used on facility page

	// used on Report Category page
	private EmissionsRptInfo anticipatedRptInfo; // This is set when
	// currentRptInfo is not.

	// used on Report Category page
	private ArrayList<EmissionsRptInfo> emissionsReportInfo;

	private TableSorter rptInfoWrapper = new TableSorter();

	// pick list of missing years.
	private ArrayList<SelectItem> categoryYears;

	private Integer defaultCategoryYear; // used when row added

	// Trade Secret stuff
	// NOTE: Trade secret info was simplified. Only schedTSStr is being used.
	private final String factorTSStr = "Emissions Factor Trade Secret Justification";
	private final String explainTSStr = "Emissions Explanation Trade Secret Justification";
	private final String materialTSStr = "Material Trade Secret Justification";
//	private final String throughputTSStr = "Throughput Trade Secret Justification";
	private final String throughputTSStr = "Throughput Confidential Data Justification";
	private final String schedTSStr = "Trade Secret Justification:  Schedule, Throughput, Variable Amounts, Emission Factors and Explanations will all be considered trade secret.";
	private final String variableTSStr = "Factor Formula Variable Trade Secret Justification";
	private final String explainStr = "Explanation/Justification for calculation";
	private final String yearlyComment = "Comment text";
	private String justificationStr; // One of the above titles to display.
	public String tsJust; // the text of the justification
	private EmissionRow secretEmissionRow; // the row to update
	private EmissionsMaterialActionUnits secretMaterialRow; // the row to update
	private EmissionsMaterialActionUnits materialRow; // the row to update
	private EmissionsMaterialActionUnits tmpMaterialRow; // the row to update
	private EmissionsVariable secretVariableRow; // the row to update

	// Used by explain methods
	private TableSorter explainMethods = new TableSorter();
	private TableSorter explainRecommendedMethods = new TableSorter();

	// Used by page calculationTable.jsp
	private TableSorter calcWrapper = new TableSorter();
	private String calcWrapperComputeError = null;
	private List<ControlInfoRow> rowControlInfoTable = null;
	EmissionRow calcEmissionRow = null;
	EmissionRow savedCalcEmissionRow = null;
	private String calcStackEmissions;
	private String calcFugitiveEmissions;

	// Used to select profile to associate report with
	private TableSorter facilityHistory = new TableSorter();

	protected Timestamp invoiceDate;

	protected String byClickEmiUnitOrGrp;
	private TreeNode saveSelectedTreeNode;
	private String saveCurrent;

	// Used for Locate button to locate a pollutant
	PollutantRow[] euPoll = null;
	PollutantRow[] processPoll = null;
	private EmissionRow emissionRowPollutant;
	private PollutantRow pollutantRow;

	private boolean showHelp = false;

	private EmissionsReportService emissionsReportService;
	private FacilityService facilityService;

	private boolean invalidYear = false;
	public EmissionsDocument doc;

	private EmissionsRptInfo reportingYear;
	private boolean includeAllAttachments = true;

	private Integer missingFactorsYear;
	private String facilityClass = PermitClassDef.TV;

	private boolean updatingInvoiceInfo = false;

	private EmissionsInvoice emissionsInvoice = null;

	private SCEmissionsReport[] scEmissionsReports;

	private FacilityReference facilityReference;

	// for use with data entry wizard only
	private EmissionsReportPeriod selectedPeriod;
	private EmissionsMaterialActionUnits selectedMaterial;

	private boolean deleteDisabled;
	private String deleteDisabledMsg;
	
	//
	public boolean throughputConfidentialFlag;
	
	public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	
	

	public String getCreateContentTypeCd() {
		return createContentTypeCd;
	}

	public void setCreateContentTypeCd(String createContentTypeCd) {
		this.createContentTypeCd = createContentTypeCd;

		// Determine message and whether new or revised report
		createReviseMsg = "You are creating the first " + getCreateContentTypeLabel()
				+ " emissions inventory for the year " + create_year + ".";
		creatingRevisedRpt = false;
		for (EmissionsReportSearch ers : existingReports) {
			if (ers.getYear().equals(create_year) && ers.getContentTypeCd().equals(createContentTypeCd)) {
				creatingRevisedRpt = true;
				createReviseMsg = "Inventory(s) for the year "
						+ create_year
						+ " and Content Type: " + getCreateContentTypeLabel()
						+ " already exist. &nbsp;You are creating a revised "
						+ getCreateContentTypeLabel()
						+ " emissions inventory for the year "
						+ create_year
						+ ".<br><br>To minimize errors, this revised emissions inventory will be associated with the current facility inventory, not the facility inventory used at the time of last submission.&nbsp;&nbsp;If necessary, you may associate the emissions inventory with a different facility inventory by clicking <b>Associate with Different Facility Inventory</b> after creating the emissions inventory.";
				break;
			}
		}
		selectRptList = new ArrayList<EmissionsReportSearch>(
				existingReports.length);
		for (EmissionsReportSearch er : existingReports) {
			if (er.getContentTypeCd().equals(createContentTypeCd)) {
				selectRptList.add(er);
			}
		}
		enableRptCopy = selectRptList.size() > 0
				&& enableCopyRpt(create_year, createContentTypeCd);
	}

	public String setShowHelp() {
		showHelp = true;
		return null;
	}

	public ReportProfileBase() {
		setTag(ValidationBase.REPORT_TAG);
	}

	// For workflow--this is a stub and re-implemented in app ReportProfile
	public void setFromTODOList(boolean fromTODOList) {
	}

	public final Integer getFpId() {
		return fpId;
	}

	public final void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public final Facility getFacility() {
		getReport();
		return facility;
	}

	public final void setFacility(Facility facility) {
		this.facility = facility;
	}

	public final boolean isCompareSubmitted() {
		return compareReport.getRptReceivedStatusDate() != null;
	}

	protected void recreateTreeFromReport(String focus) throws RemoteException {
		if (!isClosedForEdit()) {
			emissionsReportService.reportFacilityMatch(facility, report,
					scReports);
		}
		emissionsReportService.locatePeriodNames(facility, report);

		treeReport = report;
		treeData = null;
		getTreeData(focus, byClickEmiUnitOrGrp);
	}

	public boolean isBillable() throws RemoteException {
		List<ReportTemplates> scs = retrieveAssociatedSCEmissionsReports(getReport());
		return getEmissionsReportService().isEmissionsReportBillable(scs);
	}

	public String getRegulatoryRequirements() {
		List<ReportTemplates> scs = getAssociatedScReports();
		StringBuffer regReqs = new StringBuffer();
		for (int i = 0; i < scs.size(); i++) {
			SCEmissionsReport sc = scs.get(i).getSc();
			String regReqCd = sc.getRegulatoryRequirementCd();
			String regReq = RegulatoryRequirementTypeDef.getData().getItems()
					.getItemDesc(regReqCd);
			regReqs.append(regReq);
			if (i != scs.size() - 1) {
				regReqs.append("; ");
			}
		}
		return regReqs.toString();
	}

	public EmissionsReport getReport(ReportProfileBase rpb) {
		
		ok = true;
		String errStr = "";
		hapNonAttestationMsg = SystemPropertyDef.getSystemPropertyValue("HAP_EMISSIONS_NON_ATTESTATION_MSG", null);
		if ((report == null) || !savedReportId.equals(reportId)
				|| (!reportId.equals(report.getEmissionsRptId()))) {
			ReportSearch rs = (ReportSearch) FacesUtil
					.getManagedBean("reportSearch");
			rs.setPopupRedirectOutcome(null);
			passedValidation = null;
			err = false;
			openedForEdit = false;
			hasBeenOpenedForEdit = false;
			setEditable(false);
			setUpdatingInvoiceInfo(false);
			editableM = false;
			try {
				savedCalcEmissionRow = null;
				doRptCompare = false;
				setEditable(false);
				setUpdatingInvoiceInfo(false);
				report = emissionsReportService.retrieveEmissionsReport(
						reportId, inStaging);
				if (report == null) {
					ok = false;
					errStr = "Failed to read the emissions invenroty "
							+ AbstractDAO.formatId("EI", "%07d",
									reportId.toString());
					logger.error("Failed to Read Report " + reportId
							+ ", inStaging=" + inStaging);
				}
				if (ok) {
					attachments = report.getAttachments();
					// load information for all attachments from document table
					for (EmissionsDocumentRef attachment : attachments) {
						emissionsReportService.loadDocuments(attachment,
								inStaging);
					}
					savedReportId = reportId;
					current = treeNodeId(report);
					previousCurrent = "";
					fpId = report.getFpId();
					facility = getFacilityProfile();
				}
				if (facility == null) {
					ok = false;
					errStr = "Failed to read the facility with fpId " + fpId;
					logger.error("Failed to Read Facility with fpId " + fpId
							+ " for inventory " + reportId + ", inStaging="
							+ inStaging);
				}
				revisedReportNotProcessedMsg = null;
				if (ok) {
					if (isInternalApp() && report.getReportModified() != null) {

						EmissionsReport r = emissionsReportService
								.retrieveEmissionsReportRow(
										report.getReportModified(), false);
						if (r == null) {
							ok = false;
							errStr = "Failed to read previous report "
									+ report.getReportModified();
						}
						if (ok && !r.processed()) {
							revisedReportNotProcessedMsg = "This emissions inventory is a revision of inventory "
									+ formatId("EI", "%07d",
											Integer.toString(report
													.getReportModified()))
									+ " which has not been approved/rejected";
							DisplayUtil
									.displayInfo(revisedReportNotProcessedMsg);
						}
					}
				}
				if (ok) {
					locatedScReports = retrieveSCEmissionsReports(report,
							facility.getFacilityId());
					locatedScReports.displayMsgs();
					if (locatedScReports.isFailed()) {
						errStr = "Operation Failed because of the above reasons.";
						ok = false;
					}
					reportsNeeded();
					// If report not submitted, merge with facility inventory
					if (!isClosedForEdit()) {
						emissionsReportService.reportFacilityMatch(facility,
								report, scReports);
					}
					emissionsReportService.locatePeriodNames(facility, report);
					treeReport = report;
					treeData = null; // need to redraw the tree
					rpb.processNotes(report);
					getYearlyReportingInfo();
					if (isPublicApp()) {
						setReportLevelOnly(true);
					} else {
						setReportLevelOnly(false);
					}
					// Make sure we also execute getTreeData which will not be
					// called
					// from the jsp in the case of SMTV and migrated report
					// (reportLevelOnly).
					getTreeData(treeNodeId(treeReport));
					if (this.isInternalApp()) {
						us.oh.state.epa.stars2.app.emissionsReport.ReportDetail rd = (us.oh.state.epa.stars2.app.emissionsReport.ReportDetail) FacesUtil
								.getManagedBean("reportDetail");
						rd.setReportId(reportId); // needed to keep track of
													// last report examined.
					} else {  // used for both portal and public
						us.oh.state.epa.stars2.portal.emissionsReport.ReportDetail rd = (us.oh.state.epa.stars2.portal.emissionsReport.ReportDetail) FacesUtil
								.getManagedBean("reportDetail");
						rd.setReportId(reportId); // needed to keep track of
													// last report examined.
					}
					setAssociatedScReports(retrieveAssociatedSCEmissionsReports(report));
				}
			} catch (RemoteException re) {
				handleException(reportId, re);
				setErr(true);
				// Note, ok not set to false because an error display msg
				// already done
			} catch (Exception e) {
				handleException(reportId, e);
				setErr(true);
				// Note, ok not set to false because an error display msg
				// already done
			}
			if (!ok) {
				DisplayUtil.displayError(errStr);
				setErr(true);
			}
		}
		return report;
	}

	// Code is in the derived class to invoke getReport(ReportProfile rpb)
	protected EmissionsReport getReport() {
		logger.error("ReportProfileBase.getReport() is never called");
		return null;
	}

	protected Facility getFacilityProfile() throws RemoteException {
		Facility f = facilityService.retrieveFacilityProfile(fpId, inStaging);
		if (f == null) {
			DisplayUtil.displayError("Failed to read facility inventory");
			String e = "Did not locate profile " + fpId + " for report "
					+ reportId + ", inStaging=" + inStaging;
			logger.error(e);
			throw new DAOException(e);
		}
		facilityEditable = true;
		if (!f.isCopyOnChange() && !f.getVersionId().equals(-1)
				|| f.getVersionId().equals(-1) && f.isCopyOnChange()) {
			facilityEditable = false;
		}
		return f;
	}

	public final String nodeClickedCollaspe() {
		String ret = TV_REPORT;
		if (selectedTreeNode.getType().equals("moreNodes")) {
			byClickEmiUnitOrGrp = null;
			selectedTreeNode = saveSelectedTreeNode;
			current = saveCurrent;
		} else {
			ret = nodeClicked();
			byClickEmiUnitOrGrp = current;
		}
		treeData = null;
		getTreeData(current, byClickEmiUnitOrGrp);
		return ret;
	}

	public final String nodeClicked() {
		String ret = TV_REPORT;
		// current is set to the identifier of selected tree node.
		if (!current.equals(previousCurrent)) {
			try {
				previousCurrent = current;
				internalNodeClicked();
			} catch (ApplicationException e) {
				DisplayUtil.displayError("Failure due to " + dataProb);
				logger.error(
						"Failure on Emissions Inventory "
								+ report.getEmissionsRptId() + " due to "
								+ dataProb, e);
				emissionPeriodWrapper.setWrappedData(null);
				emissionPeriodWrapperHAP.setWrappedData(null);
				hapTable = false;
				emissionReportWrapper.setWrappedData(null);
				emissionReportWrapperHAP.setWrappedData(null);
				emissionGroupWrapper.setWrappedData(null);
				emissionGroupWrapperHAP.setWrappedData(null);
				emissionEuWrapper.setWrappedData(null);
				emissionEuWrapperHAP.setWrappedData(null);
				// TODO what else
			}
		}

		if (isPortalApp() && !isInStaging()) { //??
			ret = HOME_TV_REPORT;
		} else if (isPublicApp()) {
			ret = HOME_TV_REPORT;
		}
		return ret; // stay on same page
	}

	private void internalNodeClicked() throws ApplicationException {
		Object object = ((Stars2TreeNode) selectedTreeNode).getUserObject();
		if (selectedTreeNode.getType().equals("report")) {
			setReportFocus();
		} else if (selectedTreeNode.getType().equals("emissionUnits")
				|| selectedTreeNode.getType().equals("emissionUnitsCaution")
				|| selectedTreeNode.getType().equals("emissionUnitsExcluded")
				|| selectedTreeNode.getType().equals("emissionUnitsDeleted")) {
			emissionUnit = (EmissionsReportEU) object;
			setEUFocus();
		} else if (selectedTreeNode.getType().equals("emissionUnitGroups")
				|| selectedTreeNode.getType().equals(
						"emissionUnitGroupsCaution")
				|| selectedTreeNode.getType().equals(
						"emissionUnitGroupsDeleted")) {
			grpEmissionUnit = (EmissionsReportEUGroup) object;
			setEUGroupFocus(grpEmissionUnit);
		} else if (selectedTreeNode.getType().equals("emissionPeriods")
				|| selectedTreeNode.getType().equals("emissionPeriodsCaution")
				|| selectedTreeNode.getType().equals("emissionPeriodsDeleted")) {
			emissionsPeriod = (EmissionsReportPeriod) object;
			setPeriodFocus(false);
		} else if (selectedTreeNode.getType().equals("emissionGPeriods")
				|| selectedTreeNode.getType().equals("emissionGPeriodsCaution")
				|| selectedTreeNode.getType().equals("emissionGPeriodsDeleted")) {
			emissionsPeriod = (EmissionsReportPeriod) object;
			setPeriodFocus(false);
		}
		saveSelectedTreeNode = selectedTreeNode;
		saveCurrent = current;
	}

	private void setReportFocus() throws ApplicationException {
		setExpandedExp(false);
		current = treeNodeId(report);
		selectedTreeNode = treeNodeMap.get(current);
		renderComponent = "report";
		renderSum = true;
		reportsNeeded();
		if (isPublicApp()) {
			setReportLevelOnly(true);
		} else {
			setReportLevelOnly(false);
		}
		rptEmissionChoice = 2;
		if (report.zeroEmissions()) {
			rptEmissionChoice = 0;
		}
		if (doRptCompare) {
			compareRptEmissionChoice = 2;
			if (treeReport.getComp().zeroEmissions()) {
				compareRptEmissionChoice = 0;
			}
		}
		reportEmissions = EmissionRow.getEmissions(treeReport, doRptCompare,
				isClosedForEdit(), percentDiff, scReports, logger, false);
		wrapEmissions(emissionReportWrapper, emissionReportWrapperHAP,
				reportEmissions, false);
		if (!isClosedForEdit() && !doRptCompare) {
			if (null != scReports.getSc()) {
				try {
					emissionsReportService.setFeeInfo(report, reportEmissions,
							scReports.getSc());
				} catch (RemoteException e) {
					handleException(reportId, e);
				}
			} else {
				DisplayUtil
						.displayError("Failure to access FER report template");
			}
		}
		displayTotal = getReportTotal();
	}

	public boolean isAllowJumpStartDataEntry() {
		List<ReportTemplates> scs = getAssociatedScReports();
		for (ReportTemplates sc : scs) {
			if (!RegulatoryRequirementTypeDef.allowedToJumpstartDataEntry(sc
					.getSc().getRegulatoryRequirementCd())) {
				return false;
			}
		}
		return true;
	}

	private void wrapEmissions(TableSorter caps, TableSorter haps,
			ArrayList<EmissionRow> emissions, boolean euLevel) {
		Integer i = EmissionRow.secondTab1stRow(emissions);
		hapTable = true;
		capSublist = new ArrayList<EmissionRow>(15);
		hapSublist = new ArrayList<EmissionRow>(emissions.size());
		caps.clearWrappedData();
		haps.clearWrappedData();
		if (i != null) {
			for (int x = 0; x < i; x++) {
				EmissionRow er = emissions.get(x);
				// remove FIL and CON PMs
				if ("PM-FIL".equals(er.getPollutantCd())
						|| "PM10-FIL".equals(er.getPollutantCd())
						|| "PM25-FIL".equals(er.getPollutantCd())
						|| "PM-CON".equals(er.getPollutantCd())) {
					continue;
				}
				er.setEuLevel(euLevel);
				capSublist.add(er);

			}
			caps.setWrappedData(capSublist);
			numHapRows = 0;
			if (i < emissions.size()) {
				for (int x = i; x < emissions.size(); x++) {
					EmissionRow er = emissions.get(x);

					// If excluded PM was manually added, set the fire_id to
					// null
					// so it will be treated like other manually added
					// pollutants.
					if (er.isDeletable()
							&& ("PM-FIL".equals(er.getPollutantCd())
									|| "PM10-FIL".equals(er.getPollutantCd())
									|| "PM25-FIL".equals(er.getPollutantCd()) || "PM-CON"
										.equals(er.getPollutantCd()))) {
						er.setFireRef(null);
					}
					// exclude FIL and CON PMs
					// First clause covers PMs with one active fire row.
					// Second clause covers PMs with more than one active fire rows.
					if ((er.getFireRef() != null || (!er.isDeletable() && er.getNumFireRows() > 1))
							&& ("PM-FIL".equals(er.getPollutantCd())
									|| "PM10-FIL".equals(er.getPollutantCd())
									|| "PM25-FIL".equals(er.getPollutantCd()) || "PM-CON"
										.equals(er.getPollutantCd()))) {
						continue;
					}
					er.setEuLevel(false);
					hapSublist.add(er);
				}
				numHapRows = hapSublist.size();
				haps.setWrappedData(hapSublist);
			}
		}
	}

	private void setEUGroupFocus(EmissionsReportEUGroup grp)
			throws ApplicationException {
		showHelp = false;
		setExpandedExp(false);
		current = treeNodeId(grp);
		if (!doRptCompare) {
			displayTotal = EmissionsReport.numberToString(EmissionRow
					.getTotalEmissions(grp, report.getReportYear(), scReports,
							logger));
		} else {
			Double tOrig = null;
			if (grp.getOrig() != null) {
				tOrig = EmissionRow.getTotalEmissions(grp.getOrig(),
						report.getReportYear(), scReports, logger);
			}
			Double tComp = null;
			if (grp.getComp() != null) {
				tComp = EmissionRow.getTotalEmissions(grp.getComp(),
						report.getReportYear(), scReports, logger);
			}
			displayTotal = getDisplayTot(tComp, tOrig);
		}

		selectedTreeNode = treeNodeMap.get(current);
		grpEmissionUnit = grp;
		renderComponent = "emissionUnitGroups";
		renderSum = true;
		grpName = grpEmissionUnit.getReportEuGroupName();
		ArrayList<EmissionRow> euGrpEmissions = EmissionRow.getEmissions(
				grp,
				doRptCompare,
				percentDiff,
				scReports,
				logger,
				report.getReportYear(),
				((compareReport != null && doRptCompare) ? compareReport
						.getReportYear() : null)); // Need to move
		wrapEmissions(emissionGroupWrapper, emissionGroupWrapperHAP,
				euGrpEmissions, false);
		return;
	}

	private void setEUFocus() throws ApplicationException {
		showHelp = false;
		setExpandedExp(false);
		current = treeNodeId(emissionUnit);
		if (!doRptCompare) {
			displayTotal = EmissionsReport.numberToString(EmissionRow
					.getTotalEmissions(emissionUnit, report.getReportYear(),
							scReports, logger));
		} else {
			Double tOrig = null;
			if (emissionUnit.getOrig() != null) {
				tOrig = EmissionRow.getTotalEmissions(emissionUnit.getOrig(),
						report.getReportYear(), scReports, logger);
			}
			Double tComp = null;
			if (emissionUnit.getComp() != null) {
				tComp = EmissionRow.getTotalEmissions(emissionUnit.getComp(),
						report.getReportYear(), scReports, logger);
			}
			displayTotal = getDisplayTot(tComp, tOrig);
		}

		grpEmissionUnit = null; // Indicate not in a group
		renderComponent = "emissionUnits";
		renderSum = true;
		euEmissionChoice = new Integer(emissionUnit.getEmissionChoice());
		// TODO if exempt, should skip.
		ArrayList<EmissionRow> euEmissions = EmissionRow.getEmissions(
				emissionUnit,
				doRptCompare,
				percentDiff,
				scReports,
				logger,
				report.getReportYear(),
				((compareReport != null && doRptCompare) ? compareReport
						.getReportYear() : null));
		wrapEmissions(emissionEuWrapper, emissionEuWrapperHAP, euEmissions,
				true);
	}

	private void setPeriodFocus(boolean skipCapture)
			throws ApplicationException {
		showHelp = false;
		setExpandedExp(false);
		didPositionToLast = false;
		boolean ts = emissionsPeriod.isTradeSecretS();
		current = treeNodeId(emissionsPeriod);
		if (!doRptCompare) {
			displayTotal = EmissionsReport.numberToString(EmissionRow
					.getTotalEmissions(emissionsPeriod, report.getReportYear(),
							scReports, logger));
		} else {
			Double tOrig = null;
			if (emissionsPeriod.getOrig() != null) {
				tOrig = EmissionRow.getTotalEmissions(
						emissionsPeriod.getOrig(), report.getReportYear(),
						scReports, logger);
			}
			Double tComp = null;
			if (emissionsPeriod.getComp() != null) {
				tComp = EmissionRow.getTotalEmissions(
						emissionsPeriod.getComp(), report.getReportYear(),
						scReports, logger);
			}
			displayTotal = getDisplayTot(tComp, tOrig);
		}

		renderComponent = "emissionPeriods";
		renderSum = false;

		// Get material list and pollutants
		try {
			/*
			 * Retrieve for current report and if doing comparison, then also
			 * for that report.
			 * 
			 * If doing comparison, then just display chosen material for each
			 * report. Here we are assuming one material per period.
			 */

			/*
			 * Include in periodFireRows, all rows that were active for the
			 * report period--scc.
			 */
			periodFireRows = emissionsReportService.retrieveFireRows(
					report.getReportYear(), emissionsPeriod);
			// Create SCC labeling and material table
			String prefix = "";
			discloseEmissions = true;
			if (!doRptCompare) {
				if (treeReport.getRptReceivedStatusCd().equals(
						ReportReceivedStatusDef.NOT_FILED)) {
					discloseEmissions = !emissionsPeriod.zeroHours()
							&& emissionsPeriod.allValuesSet();
				}
				// Needed to render/not render the grouping button on
				// emissionPeriod.jsp
				grpEmissionUnit = report.findEuG(emissionsPeriod);
				// get the SCC string to display
				if (null != emissionsPeriod.getTreeLabel()) {
					prefix = emissionsPeriod.getTreeLabel() + ":  ";
				}
				sccCodeStrLine = prefix
						+ "Source Classification Code (SCC) is "
						+ EmissionsReportPeriod.convertSCC(emissionsPeriod);
				periodMaterialList = emissionsReportService
						.processMaterialActions(treeReport.getReportYear(),
								isClosedForEdit(), emissionsPeriod,
								periodFireRows);
				materialSelected = false;
				materialRow = null;
				for (EmissionsMaterialActionUnits mau : periodMaterialList) {
//					mau.setTradeSecretM(ts);
//					mau.setTradeSecretT(ts);
					if (mau.isBelongs()) {
						materialSelected = true;
						materialRow = mau; // reference selected row.
						break;
					}
				}
				for (EmissionsVariable v : emissionsPeriod.getVars()) {
					v.setTradeSecret(ts);
				}
			} else {
				materialSelected = true;
				// get the SCC string to display
				if (null == emissionsPeriod.getComp()
						|| null == emissionsPeriod.getComp().getSccId()
						|| emissionsPeriod.getComp().getSccId().length() < 7) {
					compareScc = null;
				} else {
					compareScc = emissionsPeriod.getComp().getSccCode();
				}
				if (null != emissionsPeriod.getTreeLabel()) {
					prefix = emissionsPeriod.getTreeLabel() + ":  ";
				}
				sccCodeStrLine = prefix
						+ "Source Classification Code (SCC) is "
						+ EmissionsReportPeriod.convertSCC(emissionsPeriod
								.getOrig())
						+ " : "
						+ EmissionsReportPeriod.convertSCC(emissionsPeriod
								.getComp());
				periodMaterialList = emissionsReportService
						.compareMaterials(
								emissionsPeriod,
								percentDiff,
								report.getReportYear(),
								((compareReport != null && doRptCompare) ? compareReport
										.getReportYear() : null));
				periodVariableWrapper = emissionsPeriod.getVars();
				periodVariableCompWrapper
						.setWrappedData(EmissionsVariable.variablesComparison(
								emissionsPeriod.getOrig(),
								emissionsPeriod.getComp(),
								percentDiff,
								report.getReportYear(),
								((compareReport != null && doRptCompare) ? compareReport
										.getReportYear() : null)));
			}
			// We cannot have the same wrapper used twice on the same
			// .jsp page; even thou only one is rendered.
			periodMaterialWrapper = new TableSorter();
			periodMaterialWrapper.setWrappedData(periodMaterialList);
			periodMaterialCompWrapper = new TableSorter();
			periodMaterialCompWrapper.setWrappedData(periodMaterialList);

			// activeMaterials = emissionsReportBO.
			// getActiveMaterials(emissionsPeriod, doRptCompare);
		} catch (RemoteException e) {
			handleException(reportId, e); // DENNIS should fail here.
		}

		// Put updates into WebPeriod
		webPeriod = new EmissionsReportPeriod(emissionsPeriod);
		emissionPeriodWrapper = new TableSorter();
		emissionPeriodWrapperHAP = new TableSorter();
		hapSublist = new ArrayList<EmissionRow>();
		capSublist = new ArrayList<EmissionRow>();
		// Set needed for auto generation of HAPs
		// THIS IS NOT NEEDED YET EmissionRow.setCapHapList(periodEmissions);
		// Determine if process can generate fugitive or stack emissions
		EmissionUnit feu = null;
		Integer euId = null;
		if (grpEmissionUnit != null && !grpEmissionUnit.isNotInFacility()
				&& !grpEmissionUnit.getEus().isEmpty()) {
			// this is a representative EU from group
			euId = grpEmissionUnit.getEus().get(0);
			if (euId != null) {
				feu = facility.getMatchingEmissionUnit(euId);
			}
		}
		if (feu == null) {
			// Was not part of a group, check for EU
			EmissionsReportEU rpeu = report.findEU(emissionsPeriod);
			if (rpeu != null) {
				feu = facility.getMatchingEmissionUnit(rpeu.getCorrEpaEmuId());
			}
		}
		EmissionProcess fep = null;
		if (null != feu) {
			fep = feu.findProcess(emissionsPeriod.getSccId());
		}

		// Set needed for changes make on web page
		webPeriod.setFireRows(periodFireRows);
		webPeriod.setStaticMaterial(webPeriod.getCurrentMaterial());
		webPeriod.setStaticYear(report.getReportYear());
		// Need the initial emissions for input--needs ordering
		periodEmissions = EmissionRow.getEmissions(report.getReportYear(),
				webPeriod, scReports, false, true, percentDiff, logger);
		webPeriod.setCapHapList(periodEmissions);

		periodEmissions = EmissionRow.getEmissions(
				report.getReportYear(),
				((compareReport != null && doRptCompare) ? compareReport
						.getReportYear() : -1), webPeriod, scReports,
				doRptCompare, isClosedForEdit(), percentDiff, logger, report.isAutoGenerated());

		// Set period into rows, set default calc method to throughput-based
		for (EmissionRow er : periodEmissions) {
			er.setP(webPeriod);
			if (isAllowJumpStartDataEntry() && Utility.isNullOrEmpty(er.getEmissionCalcMethodCd())) {
				if (doRptCompare && compareReport.isAutoGenerated()) {
					// Keep calc method null for EI reports when being compared
				} else {
					er.setEmissionCalcMethodCd(EmissionCalcMethodDef.SCCEmissionsFactor);
					er.setAnnualAdjust("0");
				}
			}
			if (!doRptCompare && openedForEdit && report.isAutoGenerated()
					&& StringUtils.equals(er.getEmissionCalcMethodCd(), EmissionCalcMethodDef.AQDGenerated)) {
				er.setEmissionCalcMethodCd(EmissionCalcMethodDef.SCCEmissions);
			}
		}
		webPeriod.setCapHapList(periodEmissions);
		// for each pollutant determine whether fugitive or stack emissions are
		// possible.
		for (EmissionRow er : periodEmissions) {
			try {
				if (fep != null) {
					List<ControlInfoRow> l = ControlInfoRow
							.generateControlMatrix(facility, fep, webPeriod,
									er, false, false);
					if (!isSubmitted() && ControlInfoRow.isProblems()) {
						String s2 = ControlInfoRow.getProblems().toString()
								+ " for emissions inventory "
								+ report.getEmissionsInventoryId()
								+ " and emission unit " + feu.getEpaEmuId()
								+ " and scc " + webPeriod.getSccId()
								+ ", pollutantCd " + er.getPollutantCd();
						DisplayUtil.displayError(s2);
					}
					er.setFugitivesPossible(ControlInfoRow.fugitiveTotal(l) != 0d);
					er.setStackEmissionsPossible(ControlInfoRow.stackTotal(l) != 0d);
				} else {
					er.setFugitivesPossible(true);
					er.setStackEmissionsPossible(true);
				}
			} catch (Exception e) {
				String s = "Failed to determine if fugitive/stack emissions possible for pollutantCd "
						+ er.getPollutantCd()
						+ " in EU "
						+ feu.getCorrEpaEmuId()
						+ " and process "
						+ fep.getProcessId() + "--assume yes.";
				DisplayUtil.displayError(s);
				logger.error(s, e);
			}
		}

		if (!isClosedForEdit() && !doRptCompare) {
			// Recalculate everything
			emissionsPeriod.updateEmissions(facility, report, periodEmissions,
					periodFireRows);
		}

		// // Set needed for changes make on web page
		// EmissionRow.setCapHapList(periodEmissions);
		// EmissionRow.setFireRows(periodFireRows);
		// EmissionRow.setStaticMaterial(webPeriod.getCurrentMaterial());
		// EmissionRow.setStaticYear(report.getReportYear());
		wrapEmissions(emissionPeriodWrapper, emissionPeriodWrapperHAP,
				periodEmissions, false);
		// determine variables (maybe for second time)
		if (!isClosedForEdit()) {
			webPeriod.determineVars(report, periodEmissions, periodFireRows);
		}
		if (!doRptCompare) {
			// periodVariableWrapper = new TableSorter();
			// periodVariableCompWrapper = new TableSorter();
			periodVariableWrapper = webPeriod.getVars();
			periodVariableCompWrapper.setWrappedData(webPeriod.getVars());
		}
		if (!skipCapture) {
			savedWebPeriod = savedWebPeriod2;
			savedWebPeriod2 = webPeriod;
		}
	}

	// This is only called after setPeriodFocus has been called.
	public boolean isFactorFormulaUsed() {
		return EmissionRow.isFactorFormulaUsed(periodEmissions);
	}

	String getTotal(EmissionsReportEU eu) {
		Double sum = EmissionRow.getTotalEmissions(eu, report.getReportYear(),
				scReports, logger);
		return EmissionRow.getTotalStringHyphon(sum);
	}

	// return string value of chageable total
	String getTotal(EmissionsReportEUGroup grp) {
		Double sum = EmissionRow.getTotalEmissions(grp, report.getReportYear(),
				scReports, logger);
		return EmissionRow.getTotalStringHyphon(sum);
	}

	private String getReportTotal() {
		if (!doRptCompare) {
			if (report.getTotalEmissions() == null)
				return " ";
			return EmissionsReport.numberToString(report.getTotalEmissions()
					.doubleValue()) + " Tons";
		} else {
			return getDisplayTot(
					toDoubleFromFloat(compareReport.getTotalEmissions()),
					toDoubleFromFloat(report.getTotalEmissions()));
		}
	}

	private Double toDoubleFromFloat(Float f) {
		if (f == null) {
			return null;
		}
		return new Double(f.doubleValue());
	}

	private String getDisplayTot(Double tComp, Double tOrig) {
		boolean diff = false;
		float diffPercent = percentDiff.intValue() / 100.0f;
		String origStr = " ";
		String compStr = " ";
		if (tOrig == null && tComp != null) {
			origStr = "No Info";
			compStr = EmissionsReport.numberToString(tComp);
			diff = true;
		} else if (tOrig != null && tComp == null) {
			diff = true;
			origStr = EmissionsReport.numberToString(tOrig);
		} else if (tOrig == null && tComp == null) {
			;
		} else {
			diff = EmissionRow.flagDifference(tComp, tOrig, diffPercent);
			compStr = EmissionsReport.numberToString(tComp);
			origStr = EmissionsReport.numberToString(tOrig);
		}
		String beginBold = "";
		String endBold = "";
		if (diff) {
			beginBold = "<b>";
			endBold = "</b>";
		}
		return beginBold + origStr + endBold + "<b>:</b>" + compStr + " Tons";
	}

	public final EmissionUnit getEmissionUnit() {
		return emissionUnit;
	}

	public final EmissionsReportEUGroup getGrpEmissionUnit() {
		return grpEmissionUnit;
	}

	public final EmissionsReportPeriod getEmissionsPeriod() {
		return emissionsPeriod;
	}

	public final String editEmissionUnit() {
		setEditable(true);
		return TV_REPORT;
	}

	public String saveEmissionUnit() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			return saveEmissionUnitInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final String saveEmissionUnitInternal() {
		passedValidation = null;
		if (scReports.getSc().isBelowRequirements(emissionUnit)
				&& euEmissionChoice != null && euEmissionChoice == 2
				&& !emissionUnit.isForceDetailedReporting()) {
			String sE = scReports.getSc().exemptionReason(
					emissionUnit.getExemptStatusCd());
			String sT = scReports.getSc().tvClassificationReason(
					emissionUnit.getTvClassCd());

			String seLabel = "";
			String stLabel = "";
			String or = "";
			if (sE.length() > 0) {
				seLabel = " Exemption Status of ";
			}
			if (sT.length() > 0) {
				stLabel = " TV Classification of ";
			}
			if (sE.length() > 0 && sT.length() > 0)
				or = " or";

			DisplayUtil
					.displayError("Can only change between Did Not Operate and Less Than Reporting Requirement while emission unit in profile specifies"
							+ seLabel
							+ sE
							+ or
							+ stLabel
							+ sT
							+ ".  Click Force Detailed Reporting to report anyway.");
			return null;
		}
		setEditable(false);
		emissionsNeedClearing = false;
		boolean needApprovedClearing = false;
		if (euEmissionChoice != 2 && emissionUnit.getEmissionChoice() == 2) {
			emissionsNeedClearing = true;
			needApprovedClearing = emissionUnit.nonEmptyPeriods();
		}
		ArrayList<EmissionsReportEU> reuList = null;
		if (needApprovedClearing) {
			return "dialog:deleteEmissions";
		} else if (emissionsNeedClearing) {
			reuList = new ArrayList<EmissionsReportEU>(1);
			reuList.add(emissionUnit);
		}
		emissionUnit.setExemptEG71(false);
		if (euEmissionChoice == 1) {
			emissionUnit.setExemptEG71(true);
		}
		emissionUnit.setZeroEmissions(false);
		if (euEmissionChoice == 0) {
			emissionUnit.setZeroEmissions(true);
		}
		saveEmissionUnitInternal2(reuList);
		if (ok) {
			byClickEmiUnitOrGrp = treeNodeId(emissionUnit);
			try {
				recreateTreeFromReport(treeNodeId(emissionUnit));
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok = false;
				err = true;
			}
			if (!ok) {
				DisplayUtil.displayError("Read of emissions inventory failed");
			}
		}
		return null;
	}

	/*
	 * This will clear emissions from the EU for the cases where the EU is set
	 * to EG#71 or Zero Emissions
	 */
	private final void saveEmissionUnitInternal2(
			ArrayList<EmissionsReportEU> reuClearList) {
		ok = true;
		try {
			emissionsReportService.modifyEmissionsReport(facility, scReports,
					report, reuClearList, null, openedForEdit);
		} catch (RemoteException re) {
			DisplayUtil.displayError("Update failed");
			handleException(reportId, re);
			ok = false;
			err = true;
		}

		if (ok) {
			// update total reported and total chargeable emissions for the
			// report
			try {
				emissionsReportService.updateTotalEmissions(report, scReports,
						isClosedForEdit());
			} catch (DAOException de) {
				DisplayUtil.displayError("Failed to update total emissions");
				handleException(de);
				ok = false;
				err = true;
			} catch (ApplicationException ae) {
				DisplayUtil.displayError("Failed to update total emissions");
				handleException(reportId, ae);
				ok = false;
				err = true;
			}
		}

		if (ok) {
			DisplayUtil.displayInfo("Update successful");
			try {
				report = emissionsReportService.retrieveEmissionsReport(
						report.getEmissionsRptId(), true);
				if (report == null) {
					ok = false;
					err = true;
				}
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok = false;
				err = true;
			}
			if (!ok) {
				DisplayUtil.displayError("Read of emissions inventory failed");
			}
		}
	}

	public void deleteEmissions() { // Called from deleteEmissions.jsp to delete
									// all emissions from EU
		ArrayList<EmissionsReportEU> reuList = null;
		emissionUnit.setExemptEG71(false);
		if (euEmissionChoice == 1) {
			emissionUnit.setExemptEG71(true);
		}
		emissionUnit.setZeroEmissions(false);
		if (euEmissionChoice == 0) {
			emissionUnit.setZeroEmissions(true);
		}
		reuList = new ArrayList<EmissionsReportEU>(1);
		reuList.add(emissionUnit);
		saveEmissionUnitInternal2(reuList);
		if (ok) {
			byClickEmiUnitOrGrp = treeNodeId(emissionUnit);
			try {
				recreateTreeFromReport(treeNodeId(emissionUnit));
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok = false;
				err = true;
			}
			if (!ok) {
				DisplayUtil.displayError("Read of emissions inventory failed");
			}
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelDeleteEmissions() {
		setEditable(false);
		euEmissionChoice = new Integer(emissionUnit.getEmissionChoice());
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelEmissionUnitEdit() {
		setEditable(false);
		euEmissionChoice = new Integer(emissionUnit.getEmissionChoice());
		FacesUtil.setOutcome(null, TV_REPORT);
		return;
	}

	public final String cancelEdit() {
		setEditable(false);
		return CANCELLED; // note that this is a define not a string.
	}

	public final void editEmission() {
		wrapEmissions(emissionPeriodWrapper, emissionPeriodWrapperHAP, periodEmissions, false);
		setEditable(true);
		addedRow = false;
		didPositionToLast = false;
		for (Emissions e : emissionsPeriod.getEmissions().values()) {
			e.setVisited(false);
		}
		condensibleTotal = "0";
		filterableTotal = "0";
		// Blank out emissions info that are/will be derived
		for (EmissionRow er : periodEmissions) {
			if (PollutantDef.PMCOND_CD.equals(er.getPollutantCd())) {
				condensibleTotal = er.getTotalEmissions();
				condensibleUnit = er.getEmissionsUnitNumerator();
			}
			if (PollutantDef.PMFIL_CD.equals(er.getPollutantCd())) {
				filterableTotal = er.getTotalEmissions();
				filterableUnit = er.getEmissionsUnitNumerator();
			}

			er.setTotalEmissions(null); // don't show derived values during edit
			if (null != er.getEmissionCalcMethodCd()
					&& null != er.getFactorNumericValue()) {
				// There is a factor, don't show emissions on edit screen.
				// er.setFugitiveEmissions(null);
				// er.setStackEmissions(null);
			}
		}
		// Note that webPeriod is already initialized.
		FacesUtil.setOutcome(null, TV_REPORT);
	}

	public final void addEmission() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			// Add row to table.
			addedRow = true;
			EmissionRow e = new EmissionRow();
			e.setDeletable(true);
			e.setNewLine(true);
			e.setP(webPeriod);
			hapSublist.add(e);
			if (hapSublist.size() == 1) {
				hapTable = true;
				emissionPeriodWrapperHAP.setWrappedData(hapSublist);
			} else {
				didPositionToLast = true;
				// emissionPeriodWrapperHAP.positionToLastBlock(displayHapRows);
			}
		} finally {
			clearButtonClicked();
		}
		return;
	}

	public final void deleteEmission() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			// delete row of table.
			Iterator<EmissionRow> i = hapSublist.iterator();
			while (i.hasNext()) {
				if (i.next().isDelete()) {
					i.remove();
				}
			}
			if (hapSublist.size() == 0) {
				emissionPeriodWrapperHAP.setWrappedData(hapSublist);
			} else {
				if (addedRow) {
					// emissionPeriodWrapperHAP.positionToLastBlock(displayHapRows);
					didPositionToLast = true;
				}
			}
		} finally {
			clearButtonClicked();
		}
		return;
	}

	public String saveEmission() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			return saveEmissionInternal();
		} finally {
			clearButtonClicked();
		}
	}

	@SuppressWarnings("unchecked")
	private final String saveEmissionInternal() {
		ok = true;
		passedValidation = null;
		// recalculate periodEmissions
		periodEmissions = new ArrayList<EmissionRow>(capSublist.size()
				+ hapSublist.size());
		for (EmissionRow e : capSublist) {
			periodEmissions.add(e);
		}
		for (EmissionRow e : hapSublist) {
			periodEmissions.add(e);
		}
		if (!emissionsPeriod.checkEmissionsInfo(periodEmissions,
				periodFireRows, report)) {
			return "Fail";
		}
		emissionsPeriod.updateFireFactors(periodEmissions, periodFireRows,
				report);
		if (isHapTable()) {
			emissionPeriodWrapperHAP.positionToFirstBlock();
		}
		setEditable(false);
		didPositionToLast = false;
		try {
			// Move in emissions changes.
			emissionsPeriod.clearErrors();
			emissionsPeriod.updateEmissions(facility, report, periodEmissions,
					periodFireRows);
			displayWarnings(emissionsPeriod.getErrors());
			emissionsReportService.modifyEmissionsPeriod(facility, scReports,
					report, emissionsPeriod, openedForEdit);
			DisplayUtil.displayInfo("Period/Emission update successful");
		} catch (RemoteException re) {
			DisplayUtil.displayError("Period/Emission Update failed");
			handleException(reportId, re);
			ok = false;
			err = true;
		} catch (ApplicationException re) {
			DisplayUtil.displayError("Period/Emission Update failed");
			handleException(reportId, re);
			ok = false;
			err = true;
		}

		if (ok) {
			// update total reported and total chargeable emissions for the
			// report
			try {
				emissionsReportService.updateTotalEmissions(report, scReports,
						isClosedForEdit());
			} catch (DAOException de) {
				DisplayUtil.displayError("Failed to update total emissions");
				handleException(de);
				ok = false;
				err = true;
			} catch (ApplicationException ae) {
				DisplayUtil.displayError("Failed to update total emissions");
				handleException(reportId, ae);
				ok = false;
				err = true;
			}
		}

		boolean ok2 = true;
		if (ok) {
			try {
				report = emissionsReportService.retrieveEmissionsReport(
						report.getEmissionsRptId(), true);
				if (report == null) {
					ok2 = false;
					err = true;
				}
				if (ok2) {
					treeData = null;
					recreateTreeFromReport(treeNodeId(emissionsPeriod));
				}
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok2 = false;
				err = true;
			}
		}
		if (!ok2) {
			DisplayUtil.displayError("Failed to read the emissions inventory");
		}
		// else {
		// // check for condensible and filterable amounts.
		// String newCondensibleTotal = "0";
		// String newFilterableTotal = "0";
		// String newCondensibleUnit = "";
		// String newFilterableUnit = "";
		// // Blank out emissions info that are/will be derived
		// for (EmissionRow er : periodEmissions) {
		// if(PollutantDef.PMCOND_CD.equals(er.getPollutantCd())) {
		// newCondensibleTotal = er.getTotalEmissions();
		// newCondensibleUnit = er.getEmissionsUnitNumerator();
		// }
		// if(PollutantDef.PMFIL_CD.equals(er.getPollutantCd())) {
		// newFilterableTotal = er.getTotalEmissions();
		// newFilterableUnit = er.getEmissionsUnitNumerator();
		// }
		// }
		// if(newCondensibleTotal != null && newCondensibleTotal.length() > 0 &&
		// newFilterableTotal != null && newFilterableTotal.length() > 0 &&
		// newCondensibleUnit != null &&
		// newFilterableUnit != null &&
		// (!newCondensibleTotal.equals(condensibleTotal) ||
		// !newFilterableTotal.equals(filterableTotal) ||
		// !newCondensibleUnit.equals(condensibleUnit) ||
		// !newFilterableUnit.equals(filterableUnit))
		// ) {
		// // check new values
		// double c = EmissionsReport.convertStringToNum(newCondensibleTotal);
		// double f = EmissionsReport.convertStringToNum(newFilterableTotal);
		// double ff = EmissionUnitReportingDef.convert(newFilterableUnit, f,
		// newCondensibleUnit);
		// if(c > ff) {
		// // provide the page describing the problem.
		// String condFiltProb =
		// "You have provided values which result in total condensible particulate matter ("
		// + PollutantDef.PMCOND_CD +
		// ") exceeding total filterable particulate matter (" +
		// PollutantDef.PMFIL_CD + ") for process " +
		// emissionsPeriod.getTreeLabel() +
		// ".<br><br><b>Condensible = " + newCondensibleTotal + " " +
		// EmissionUnitReportingDef.getData().getItems().getItemDesc(newCondensibleUnit)
		// +
		// "<br>Filterable = " + newFilterableTotal + " " +
		// EmissionUnitReportingDef.getData().getItems().getItemDesc(newFilterableUnit)
		// +
		// "</b><br><br>It is rarely the case that there is more condensible than filterable particulate matter.  You may want to confirm your calculations."
		// +
		// "<br><br>You will not receive this warning again for this process unless you change the values and condensible still exceeds filterable.";
		// FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("condFiltProb",
		// condFiltProb);
		// FacesUtil.startModelessDialog("../reports/condFiltWarning.jsf", 300,
		// 800);
		// }
		// }
		// }
		return null;
	}

	public final String cancelEmissionEdit() throws ApplicationException {
		setEditable(false);
		if (isHapTable()) {
			emissionPeriodWrapperHAP.positionToFirstBlock();
		}
		setPeriodFocus(false);
		// FacesUtil.setOutcome(null, TV_REPORT);
		return TV_REPORT;
	}

	public final void editMaterial() {
		
		setEditableM(true);
		// mark current material
		materialRow = null;
		for (EmissionsMaterialActionUnits em : periodMaterialList) {
			if (em.isBelongs()) {
				materialRow = em;
				break;
			}
		}
		if (!report.isSubmitted() && savedWebPeriod != null
				&& webPeriod.isAllSchedSeasonNull()
				&& !savedWebPeriod.isAllSchedSeasonNull()) {
			// update the values
			webPeriod.autoPopulate(savedWebPeriod);
		}
	}

	public String saveMaterial() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			return saveMaterialInternal();
		} finally {
			clearButtonClicked();
		}
	}

	public String resetSchedSeason() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			webPeriod.resetSchedSeason();
			return null;
		} finally {
			clearButtonClicked();
		}
	}

	// Used with change material popup
	public final void changeMaterialAndReturn() {
		if (tmpMaterialRow != null) {
			materialRow = tmpMaterialRow;
		}
		changeMaterial();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void changeMaterial() {
		// jsp page set variable materialRow to current material
		// replace with updated material list
		for (EmissionsMaterialActionUnits em : periodMaterialList) {
			em.setBelongs(false);
			if (null != materialRow
					&& materialRow.getMaterial().equals(em.getMaterial())
					&& materialRow.getAction().equals(em.getAction())) {
				em.setBelongs(true);
			}
		}
		if (null != materialRow && null != scReports) {
			webPeriod.setMaus(periodEmissions, materialRow, scReports.getSc());
		}
		try {
			webPeriod.setFireRows(periodFireRows);
			webPeriod.setStaticMaterial(webPeriod.getCurrentMaterial());
			webPeriod.setStaticYear(report.getReportYear());
			// Need the initial emissions for input--need ordering (maybe not
			// needed here)
			periodEmissions = EmissionRow.getEmissions(report.getReportYear(),
					webPeriod, scReports, false, true, percentDiff, logger);
			webPeriod.setCapHapList(periodEmissions);

			periodEmissions = EmissionRow.getEmissions(
					report.getReportYear(),
					((compareReport != null && doRptCompare) ? compareReport
							.getReportYear() : -1), webPeriod, scReports,
					doRptCompare, isClosedForEdit(), percentDiff, logger);
			for (EmissionRow er : periodEmissions) {
				er.setP(webPeriod);
				if (isAllowJumpStartDataEntry()
						&& Utility.isNullOrEmpty(er.getEmissionCalcMethodCd())) {
					er.setEmissionCalcMethodCd(EmissionCalcMethodDef.SCCEmissionsFactor);
					er.setAnnualAdjust("0");
				}
			}
			webPeriod.setCapHapList(periodEmissions);
			wrapEmissions(emissionPeriodWrapper, emissionPeriodWrapperHAP,
					periodEmissions, false);
			webPeriod
					.updateFireFactors(periodEmissions, periodFireRows, report);
			// determine variables (maybe for second time)
			webPeriod.determineVars(report, periodEmissions, periodFireRows);
			periodVariableWrapper = webPeriod.getVars();
		} catch (ApplicationException ae) {
			handleException(reportId, ae);
			err = true;
			DisplayUtil.displayError("Update failed");
		}
	}

	private final String saveMaterialInternal() {
		ok = true;
		passedValidation = null;
		webPeriod.clearValidationMessages();
		ValidationMessage[] validationMessages = webPeriod
				.checkPageValues(periodMaterialList, report);
		// validate periodEmissions
		boolean hasError = displayValidationMessages(
				"body:detailPage:comRptDetails:reportProfilePage:emissionPeriods:",
				validationMessages);
		if (hasError) {
			return TV_REPORT;
		}
		setEditableM(false); // Error beyond here can not be user-recovered from
		emissionsPeriod.updateUserFields(webPeriod); // Move new values in
		if (null != materialRow && null != scReports) {
			emissionsPeriod.setMaus(periodEmissions, materialRow,
					scReports.getSc());
		}
		emissionsPeriod.updateFireFactors(periodEmissions, periodFireRows,
				report);
		try {
			// Do recalculations because material may change emission numbers
			emissionsPeriod.clearErrors();
			emissionsPeriod.updateEmissions(facility, report, periodEmissions,
					periodFireRows);
			displayWarnings(emissionsPeriod.getErrors());
			emissionsReportService.modifyEmissionsPeriod(facility, scReports,
					report, emissionsPeriod, openedForEdit);
			DisplayUtil.displayInfo("Material/Throughput update successful");
		} catch (RemoteException re) {
			DisplayUtil.displayError("Update failed");
			handleException(reportId, re);
			ok = false;
			err = true;
		} catch (ApplicationException re) {
			DisplayUtil.displayError("Update failed");
			handleException(reportId, re);
			ok = false;
			err = true;
		}

		if (ok) {
			// update total reported and total chargeable emissions for the
			// report
			try {
				emissionsReportService.updateTotalEmissions(report, scReports,
						isClosedForEdit());
			} catch (DAOException de) {
				DisplayUtil.displayError("Failed to update total emissions");
				handleException(de);
				ok = false;
				err = true;
			} catch (ApplicationException ae) {
				DisplayUtil.displayError("Failed to update total emissions");
				handleException(reportId, ae);
				ok = false;
				err = true;
			}
		}

		boolean ok2 = true;
		if (ok) {
			try {
				report = emissionsReportService.retrieveEmissionsReport(
						report.getEmissionsRptId(), true);
				if (report == null) {
					ok2 = false;
					err = true;
				}
				if (ok2) {
					treeData = null;
					recreateTreeFromReport(treeNodeId(emissionsPeriod));
				}
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok2 = false;
				err = true;
			}
		}
		if (!ok2) {
			DisplayUtil.displayError("Failed to read the emissions inventory");
		}
		return null; // Stay on page
	}

	public final String cancelMaterial() {
		setEditableM(false);
		try {
			// Restore values from Period
			setPeriodFocus(false);
		} catch (ApplicationException e) {
			DisplayUtil.displayError("Failure due to " + dataProb);
			logger.error(
					"Failure due to " + dataProb
							+ " in Emissions Inventory Id "
							+ report.getEmissionsRptId() + ", period Id "
							+ emissionsPeriod.getEmissionPeriodId(), e);
			emissionPeriodWrapper.setWrappedData(null);
			emissionPeriodWrapperHAP.setWrappedData(null);
			hapTable = false;
		}
		// FacesUtil.setOutcome(null, TV_REPORT);
		return TV_REPORT;

	}

	public final void rememberAddEditEmissions() {
		FacesUtil.returnFromDialogAndRefresh();
		return;
	}

	public final void cancelAddEditEmmissions() {
		setEditable(false);
		FacesUtil.returnFromDialogAndRefresh();
		return;
	}

	public final EmissionRow getEmissionRow() {
		return emissionRow;
	}

	public final void setEmissionRow(EmissionRow emissionRow) {
		this.emissionRow = emissionRow;
	}

	public final String newReport() {
		return TV_REPORT;
	}

	private Map<String,List<Integer>> reportContentTypeYears;
	
	public List<SelectItem> getReportYearContentTypes() {
		List<Object> codes = new ArrayList<Object>();
		DefData data = ContentTypeDef.getData();
		for (String contentTypeCd : reportContentTypes.values()) {
			List<Integer> years = reportContentTypeYears.get(contentTypeCd);
			if (years.contains(create_year)) {
				codes.add(contentTypeCd);
			}
		}
		List<SelectItem> filtered = new ArrayList<SelectItem>();
		for (SelectItem item : data.getItems().getCurrentItems()) {
			if (codes.contains(item.getValue())) {
				filtered.add(item);
			}
		}
		return filtered;
	}
	
	// This is called from FacilityProfile.java and from MyTasks.java
	public final String createNewEmissionReport(String facilityId) {
		ok = true;
		create_facilityId = facilityId;
		secondPopup = false;
		String ret = null;
		copyFromExistingRpt = false;
		createReviseMsg = null;
		enableRptCopy = false;
		selectedReportId = null;
		reportYears = null;
		reportContentTypes = null;
		reportContentTypeYears = null;
		
		try {
			emissionsReportInfo = emissionsReportService
					.getYearlyReportingInfo(create_facilityId);
		} catch (RemoteException re) {
			handleException(re);
			ok = false;
		}
		Integer defaultYear = null;

		if (ok) {
			defaultYear = null;
			if (emissionsReportInfo.size() > 0) {
				defaultYear = emissionsReportInfo.get(0).getYear();
			}

			if (defaultYear == null) {
				ok = false;
				DisplayUtil
						.displayError("Reporting not enabled for any year, contact AQD for help.");
			}
		}
		if (ok) {
			try {
				existingReports = getEmissionsReports(create_facilityId, false);
				selectRptList = new ArrayList<EmissionsReportSearch>(
						existingReports.length);
				for (EmissionsReportSearch er : existingReports) {
					selectRptList.add(er);
				}

				// populate reportYears with years for which reporting is
				// enabled and
				// for which there is a template in the service catalog
				reportYears = new LinkedHashMap<Integer, Integer>();
				reportContentTypes = new LinkedHashMap<String,String>();
				reportContentTypeYears = new HashMap<String,List<Integer>>();
				for (EmissionsRptInfo info : emissionsReportInfo) {
					if (info.getReportingEnabled()) {
						Integer iy = new Integer(info.getYear());
						ReportTemplates rtA = emissionsReportService
								.retrieveSCEmissionsReports(info.getYear(),
										info.getContentTypeCd(),
										create_facilityId);
						if (!rtA.isFailed()) {
							reportYears.put(iy, iy);
							reportContentTypes.put(ContentTypeDef.getData().getItems()
									.getItemDesc(info.getContentTypeCd()),info.getContentTypeCd());
							if (null == reportContentTypeYears.get(info.getContentTypeCd())) {
								reportContentTypeYears.put(info.getContentTypeCd(),
										new ArrayList<Integer>());
							}
							reportContentTypeYears.get(info.getContentTypeCd()).add(iy);
						}
					}
				}

				create_year = null; // make user select the year
				createContentTypeCd = null;
				ret = "dialog:createNewReport";
			} catch (RemoteException re) {
				DisplayUtil
						.displayError("Failed while checking for existing reports.");
			}
		}
		return ret;
	}

	boolean enableCopyRpt(Integer year, String contentTypeCd) {
		boolean rtn = false;
		if (year != null) {
			for (EmissionsRptInfo info : emissionsReportInfo) {
				if (info.getYear().equals(year) && info.getContentTypeCd().equals(contentTypeCd)) {
					rtn = true;
					break;
				}
			}
		}
		return rtn;
	}

	// D O N O T D E L E T E (keep for reference): Return Listener -- was called
	// from createReport.jsp prior to August 2009
	// public void startReportCopyFinished(ReturnEvent actionEvent) {
	// if (secondPopup) {
	// FacesUtil.returnFromDialogAndRefresh();
	// } else {
	// AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	// }
	// }

	// Report selected, copy it.
	public String startReportCopyI() {
		ok = true;
		if (!isModTV_SMTV()) {
			ok = false;
			DisplayUtil
					.displayError("You do not have permission to create a TV or SMTV emissions inventory.");
		}
		EmissionsReport newReport = null;
		Facility f = null;
		if (ok) {
			// use current profile
			try {
				f = facilityService.retrieveFacility(create_facilityId);
				if (f == null) {
					ok = false;
					DisplayUtil.displayError("Failed to read the facility");
					logger.error("Failed to read the facility "
							+ create_facilityId);
				}
			} catch (RemoteException re) {
				handleException(selectedReportId, re);
				logger.error(
						"Failed to read the facility " + create_facilityId, re); // handleException()
																					// does
																					// not
																					// include
																					// the
																					// facility
																					// id
				ok = false;
			}
		}
		if (ok) {
			try {
				// Get report to copy from ReadOnly or AQD database
				newReport = emissionsReportService.retrieveEmissionsReport(
						selectedReportId, false);
				if (newReport == null) {
					DisplayUtil
							.displayError("Failed to read emissions inventory "
									+ formatId("EI", "%07d",
											Integer.toString(selectedReportId))
									+ " to be copied");
					ok = false;
				}
				if (creatingRevisedRpt) {
					// creating a revised report with data from another report
					if (ok) {
						// accomplish this by calling
						// startCreateReviseReportDone() after
						// reading the report to revise (which is the one with
						// selectedReportId)
						// Need to set the correct year into it--create_year.
						report = newReport;
						facility = f;
						boolean rtn = startCreateReviseReportDoneInternal1();
						if (rtn) {
							return TV_REPORT;
						} else {
							return null;
						}
					}
				} else {
					// Creating a new report year report with data from another
					// report
					if (ok) {
						ok = createAllowed(create_year, createContentTypeCd,
								create_facilityId, false); // not revising a
															// report
					}
					if (ok) {
						newReport.setFpId(f.getFpId());
						newReport.setEmissionsRptId(null);
						newReport.setReceiveDate(null);
						newReport.setRptApprovedStatusDate(null);
						newReport.setRptReceivedStatusDate(null);
						newReport.setReportModified(null);
						newReport.setLegacy(false);
						newReport.setFeeId(null);
						newReport.setFeeId2(null);
						newReport.setRevisionReason(null);
						newReport.setTotalEmissions(null);
						newReport.setNotes(null);
						newReport.setValidated(false);
						newReport.setSubmitterUser(null);
						newReport.setSubmitterContact(null);
						
						// TFS - 7117
						// check for the validity of the previously selected material
						// in the current reporting year. if the previously selected
						// material is not a valid material for the given SCC code in
						// the current reporting year, then remove the material and
						// corresponding emissions.
						for (EmissionsReportEU eu : newReport.getEus()) {
							for (EmissionsReportPeriod period : eu.getPeriods()) {
								boolean clearMausAndEmissions = false;
								String materialDesc = null;
								for (EmissionsMaterialActionUnits maus : period.getMaus()) {
									if (null == emissionsReportService.retrieveFireRow(period.getSccId(),
											maus.getMaterial(), create_year)) {
										materialDesc = MaterialDef.getData().getItems().getItemDesc(maus.getMaterial());
										clearMausAndEmissions = true;
										break;
									}
								}
								// clear the selected material and emissions
								if(clearMausAndEmissions) {
									period.getMaus().clear();
									period.getEmissions().clear();
									DisplayUtil.displayWarning(materialDesc + " is not a valid material for"
											+ " SCC " + period.getSccId() 
											+ " in the " + create_year	+ " reporting year." 
											+ " The material and the associated emissions data is not cloned.");
								}
							}
						}
						
						// TFS - 6959
						// if it is AQD generated report then set the emissions calculation
						// method to "Emissions" for all the reported emissions.
						if(newReport.isAutoGenerated()) {
							newReport.setAutoGenerated(false);
							changeEmissionsCalcMethodToEmissions(newReport);
						}
						
						if ((create_year.intValue() == newReport.getReportYear()
								.intValue()) && (createContentTypeCd.equals(newReport.getContentTypeCd()))) {
							// Remove signature document if it exists.
							Iterator<EmissionsDocumentRef> atIt = newReport
									.getAttachments().iterator();
							while (atIt.hasNext()) {
								EmissionsDocumentRef edr = atIt.next();
								if (EmissionsAttachmentTypeDef.RO_SIGNATURE
										.equals(edr
												.getEmissionsDocumentTypeCD())) {
									logger.debug(" Not copying attestation document with path = "
											+ edr.getPublicDoc().getPath());
									atIt.remove();
								}
								if (DefData.isDapcAttachmentOnly(edr
										.getEmissionsDocumentTypeCD())) {
									atIt.remove();
								}
							}
						} else {
							// remove all attachments.
							newReport
									.setAttachments(new ArrayList<EmissionsDocumentRef>());
						}
						newReport.setReportYear(create_year);
						newReport
								.setRptReceivedStatusCd(ReportReceivedStatusDef.NOT_FILED);
						if (!newReport.getEisStatusCd().equals(
								ReportEisStatusDef.NONE)) {
							newReport
									.setEisStatusCd(ReportEisStatusDef.NOT_FILED);
						}
						newReport.setServiceCatalogs(null);   // so we reset SCs based on create year and content type
						f = facilityService.retrieveFacilityProfile(
								newReport.getFpId(), false);
						if (f == null) {
							ok = false;
							DisplayUtil
									.displayError("Failed to read facility inventory "
											+ newReport.getFpId());
						} else {
							createRTask = callTV_SMTV_create(f, create_year, createContentTypeCd,
									newReport, null);
							if (createRTask == null) {
								ok = false;
							}
						}
					}
				}
				if (ok) {
					create_facility = f;
					locatedScReports = emissionsReportService
							.retrieveSCEmissionsReports(create_year,
									createContentTypeCd,
									create_facility.getFacilityId());
					locatedScReports.displayMsgs();
					if (locatedScReports.isFailed()) {
						ok = false;
					}
				}
			} catch (RemoteException re) {
				handleException(selectedReportId, re);
				ok = false;
			}
		}
		String ret = null;
		if (ok) {
			// was sucessfull, set variables so usual report initialization is
			// done.
			report = null;
			reportId = newReport.getEmissionsRptId();
			inStaging = true;
			showHelp = true;
			DisplayUtil
					.displayInfo("New emissions inventory based upon inventory "
							+ selectedReportId + " created successfully");
			setNtvReportFlag(false);
			ret = TV_REPORT;
		} else {
			DisplayUtil
					.displayError("Failed to create new emissions inventory");
			ret = null;
		}
		FacesUtil.returnFromDialogAndRefresh();
		return ret;
	}

	/*
	 * Called when a new report is created from scratch (using facility
	 * inventory info). (Called from the derived class app or
	 * portal/ReportProfile.startCreateReportDone()
	 * 
	 * A new report or a revised report cannot be created if reporting is not
	 * enabled for that year and cannot be created if the state is set to
	 * "report not required"
	 * 
	 * Also called if a NTV report is to be created.
	 */
	protected final String startCreateReportDoneI() {
		ok = true;
		err = false;
		String ret = null;
		NtvReport rpt = null;
		try {
			ok = createAllowed(create_year, createContentTypeCd, create_facilityId,
					creatingRevisedRpt);
			Integer rptRevisedId = null;
			if (ok) {
				if (creatingRevisedRpt && submittedReportPrim != -1) {
					rptRevisedId = submittedReportPrim;
				}
				// Need the current facility inventory
				facility = facilityService.retrieveFacility(create_facilityId);
				if (facility == null) {
					ok = false;
					DisplayUtil
							.displayError("Failed to read current facility inventory");
				}
			}
			if (ok) {
				// Does user have permission?
				if (!isModTV_SMTV()) {
					ok = false;
					DisplayUtil
							.displayError("You do not have permission to create a TV or SMTV report.");
				}
				if (ok) {
					ReportTemplates rtA = emissionsReportService
							.retrieveSCEmissionsReports(create_year, createContentTypeCd, create_facilityId);
					if (rtA.isFailed()) {
						rtA.displayMsgs();
						ok = false;
					} else {
						if (creatingRevisedRpt) { // creating revised empty
													// report
							// create empty report
							report = new EmissionsReport();
							report.setReportYear(create_year);
							boolean rtn = startCreateReviseReportDoneInternal1();
							if (rtn) {
								showHelp = true;
								return TV_REPORT;
							} else {
								return null;
							}
						}
						// create new report year report that is empty.
						createRTask = callTV_SMTV_create(facility, create_year, createContentTypeCd,
								null, rptRevisedId);
						if (createRTask == null) {
							// Already provided failure display message
							ok = false;
						}
					}
				}
				if (ok) {
					showHelp = true;
					setNtvReportFlag(false);
					ret = TV_REPORT;
					facilityId = create_facilityId;
					inStaging = true;
					String contentTypeDesc = 
							ContentTypeDef.getData().getItems().getItemDesc(createContentTypeCd);
					DisplayUtil.displayInfo("New report for "
							+ create_year.toString() + " Content Type: " + contentTypeDesc + " created successfully");
					facility = null; // Force reading the profile
					report = null; // Force reading the report
					getReport();
				}
			}
		} catch (RemoteException e) {
			ok = false;
			DisplayUtil
					.displayError("System error. Please contact system administrator");
			logger.error("startCreateReportDoneI failed", e);
		}
		if (ok) {
			if (this.isPortalApp()) {
				MyTasks mt = (MyTasks) FacesUtil.getManagedBean("myTasks");
				mt.renderEmissionReportMenu(createRTask);
				mt.refreshTasks(facility.getFacilityId());
			} else {
				((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_TVDetail"))
						.setDisabled(false);
			}
		} else {
			err = true;
			ret = null;
		}
		return ret;
	}

	private void reportsNeeded() {
		reportsNeeded(scReports, locatedScReports, report);
	}

	// Deterimine the report should be created, get its category and
	// read in the report definitions for it.
	private boolean createAllowed(Integer yr, String contentTypeCd, String facId,
			boolean revised) throws RemoteException {
		if (isPublicApp()) {
			return false;
		}
		ok = true;
		locateNewestRpts(yr, contentTypeCd, null, null, facId);
		submittedReportPrim = submittedReport;
		if (revised) { // messages if revised report
			if (notFiledReport != -1) {
				DisplayUtil
						.displayWarning("Revised "
								+ yr + " " + contentTypeCd
								+ " Emissions Inventory not created because inventory "
								+ formatId("EI", "%07d",
										Integer.toString(notFiledReport))
								+ " should be modified instead.  Go to that inventory to continue editing it.");
				ok = false;
			} else if (submittedReport == -1) {
				DisplayUtil
						.displayError(yr + " " + contentTypeCd
								+ " Inventory not created because the system failed to find the existing inventory for year "
								+ yr + " content type " + contentTypeCd + ".");
				ok = false;
			} else {
				submittedReportPrim = getMinId(submittedReport,
						submittedReportComp);
			}
		} else { // messages if new report
			if (notFiledReport != -1) {
				notFiledReport = getMinId(notFiledReport, notFiledReportComp);
				DisplayUtil
						.displayWarning(yr + " " + contentTypeCd
								+ " Emissions inventory not created because inventory "
								+ formatId("EI", "%07d",
										Integer.toString(notFiledReport))
								+ " should be modified instead.  Go to that inventory to continue editing it.");
				ok = false;
			} else if (submittedReport != -1) {
				submittedReportPrim = getMinId(submittedReport,
						submittedReportComp);
				DisplayUtil
						.displayWarning(yr + " " + contentTypeCd
								+ " Emissions inventory not created because the inventory "
								+ formatId("EI", "%07d",
										Integer.toString(submittedReportPrim))
								+ " has already been submitted."
								+ "  Go to that emissions inventory or another inventory for that year and click create revised emissions inventory.");
				ok = false;
			}
		}

		EmissionsRptInfo info = null;
		if (ok) {
			info = EmissionsRptInfo.locateEmissionsRptInfo(emissionsReportInfo, yr, contentTypeCd);
			if (null == info || !info.getReportingEnabled().booleanValue()) {
				String s = "Revised "
						+ yr + " " + contentTypeCd
						+ " emissions inventory not created because reporting not enabled for "
						+ yr + " " + contentTypeCd + ". Contact AQD for help.";
				if (!revised) {
					s = "Report not created because reporting not enabled for "
							+ yr + " " + contentTypeCd + ". Contact AQD for help.";
				}
				DisplayUtil.displayWarning(s);
				ok = false;
			} else if (ReportReceivedStatusDef.NO_REPORT_NEEDED.equals(info
					.getState())) {
				DisplayUtil.displayWarning(yr + " " + contentTypeCd + noRptReq + yr + " " + contentTypeCd + noRptReq2);
				ok = false;
			}
		}

		return ok;
	}

	// TODO determine what to return to go to page.
	// Called when a new report is created and is a clone of another report
	public final void startCopyReportDone() {
		FacesUtil.returnFromDialogAndRefresh();
		setEditable(false);
		return;
	}

	public void startCreateReviseReportDone() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		boolean rtn = false;
		try {
			create_year = report.getReportYear();
			createContentTypeCd = report.getContentTypeCd();
			creatingRevisedRpt = true;
			startCreateReviseReportDoneInternal1();
		} finally {
			clearButtonClicked();
		}
		return;
	}

	public boolean startCreateReviseReportDoneInternal1() {
		boolean rtn = startCreateReviseReportDoneInternal2();
		report = null; // Force reading the report
		if (rtn) {
			getReport(); // Read the report
		}
		return rtn;
	}

	// called when revise report selected.
	private final boolean startCreateReviseReportDoneInternal2() {
		ReportTemplates locatedScRpts = null;
		ok = true;
		err = false;
		setEditable(false);
		Integer rptRevisedId = null;
		try {
			facilityId = facility.getFacilityId();
			ok = createAllowed(create_year, createContentTypeCd, facilityId, true);
			if (submittedReportPrim != -1) {
				rptRevisedId = submittedReportPrim;
			}
		} catch (RemoteException e) {
			ok = false;
			err = true;
			handleException(reportId, e);
		}
		String navTo = null;

		if (ok) {
			// Does user have permission?
			if (!isModTV_SMTV()) {
				ok = false;
				DisplayUtil
						.displayError("You do not have permission to create a TV or SMTV emissions inventory.");

			}
			if (ok) {
				// Create TV or SMTV report based on this TV or SMTV report
				try {
					// Base report off current facility inventory
					facility = facilityService.retrieveFacility(facilityId);
					if (facility == null) {
						ok = false;
						err = true;
					}
					if (ok) {
						report.setFpId(facility.getFpId());
						locatedScRpts = emissionsReportService
								.retrieveSCEmissionsReports(create_year,
										createContentTypeCd,
										facility.getFacilityId());
						if (locatedScRpts == null) {
							ok = false;
						} else {
							locatedScRpts.displayMsgs();
						}
					}
					
					if (ok && locatedScRpts.isFailed()) {
						ok = false;
						err = true;
					} else {
						report.setRptReceivedStatusCd(ReportReceivedStatusDef.NOT_FILED);
						report.setRptReceivedStatusDate(null);
						report.setRptApprovedStatusDate(null);
						report.setReceiveDate(null);
						report.setLegacy(false);
						report.setNotes(null);
						report.setRevisionReason(null);
						report.setSubmitterUser(null);
						report.setSubmitterContact(null);
						report.setValidated(false);
						if ((create_year.intValue() == report.getReportYear()
								.intValue())
								&& (createContentTypeCd.equals(report
										.getContentTypeCd()))) {
							// Remove signature document if it exists.
							Iterator<EmissionsDocumentRef> atIt = report
									.getAttachments().iterator();
							while (atIt.hasNext()) {
								EmissionsDocumentRef edr = atIt.next();
								if (EmissionsAttachmentTypeDef.RO_SIGNATURE
										.equals(edr
												.getEmissionsDocumentTypeCD())) {
									logger.debug(" Not copying attestation document with path = "
											+ edr.getPublicDoc().getPath());
									atIt.remove();
								}
								if (DefData.isDapcAttachmentOnly(edr
										.getEmissionsDocumentTypeCD())) {
									atIt.remove();
								}
							}
						} else {
							// remove all attachments.
							report.setAttachments(new ArrayList<EmissionsDocumentRef>());
						}
						
						// if it is AQD generated report then set the emissions calculation
						// method to "Emissions" for all the reported emissions.
						if(report.isAutoGenerated()) {
							report.setAutoGenerated(false);
							changeEmissionsCalcMethodToEmissions(report);
						}
						
						Task rtn = callTV_SMTV_create(facility, create_year,
								createContentTypeCd, report, rptRevisedId);
						if (rtn != null) {
							createRTask = rtn;
						} else {
							ok = false;
							err = true;
						}
						if (ok) {
							showHelp = true;
							// get the new report ID.
							reportId = report.getEmissionsRptId();
							setSavedReportId(reportId);
							if (this.isPortalApp()) { // TODO This ends up
															// being
															// done twice
								MyTasks mt = (MyTasks) FacesUtil
										.getManagedBean("myTasks");
								mt.renderEmissionReportMenu(createRTask);
								mt.refreshTasks(facility.getFacilityId());
							} else {
								((SimpleMenuItem) FacesUtil
										.getManagedBean("menuItem_TVDetail"))
										.setDisabled(false);
							}

							getReport(); // TODO This ends up being called twice
											// --may be noop second time.
							if (!err) {
								// Reports always have FER.
								// report.addReportType(ReportGroupTypes.TVType);
								// switch to new report templates
								locatedScReports = locatedScRpts;
								reportsNeeded();
								recreateTreeFromReport(treeNodeId(report));
							}
						}
					}
					if (ok) {
						navTo = TV_REPORT;
						setNtvReportFlag(false);
					}
				} catch (RemoteException re) {
					handleException(reportId, re);
					ok = false;
					err = true;
				}
			}
		}
		boolean rtn;
		if (ok) {
			DisplayUtil
					.displayInfo("Revised emissions inventory created successfully");
			inStaging = true;
			ReportSearch rs = (ReportSearch) FacesUtil
					.getManagedBean("reportSearch");
			rs.setPopupRedirectOutcome(navTo);
			rtn = true;
		} else {
			emissionReportWrapper.setWrappedData(null);
			hapTable = false;
			emissionReportWrapperHAP.setWrappedData(null);
			DisplayUtil.displayError("Revised emissions inventory not created");
			rtn = false;
		}
		FacesUtil.returnFromDialogAndRefresh();
		return rtn;
	}

	public final void editFire() {
		setFireEditable(true);
	}

	public void saveFireSelection() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			saveFireSelectionInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final void saveFireSelectionInternal() {
		setEditable(false);
		passedValidation = null;

		// Iterator<?> it =
		// fireWrapper.getTable().getSelectionState().getKeySet().iterator();
		FireRow selectedRow = null;
		for (FireRow i : rowFireTable) {
			if (i.isSelected()) {
				selectedRow = i;
			}
		}

		if (selectedRow != null) {

			ok = true;
			// Update emissions row
			try {
				emissionRowMethod.setFactorNumericValue(selectedRow
						.getFactorFormula());
				emissionRowMethod.setFactorNumericValueOverride(false);
				emissionRowMethod.setFormula(selectedRow.getFormula());
				emissionRowMethod.setFireRef(FireRow.getFireId(selectedRow));
				emissionRowMethod.setFireRefFactor(selectedRow
						.getFactorFormula());
				emissionRowMethod.setNumFireRows(fireWrapper.getRowCount());
				emissionsPeriod.clearErrors();
				emissionsPeriod.updateEmissions(facility, report,
						periodEmissions, periodFireRows);
				displayWarnings(emissionsPeriod.getErrors());
				emissionsReportService.modifyEmissionsReport(facility,
						scReports, report, null, null, openedForEdit);
				report = emissionsReportService.retrieveEmissionsReport(
						report.getEmissionsRptId(), true);
				if (report == null) {
					ok = false;
				}
				if (ok) {
					recreateTreeFromReport(treeNodeId(emissionsPeriod));
				}
			} catch (NumberFormatException e) {
				ok = false;
				err = true;
				DisplayUtil.displayError("Fire Factor (\""
						+ selectedRow.getFactor() + "\") is invalid");
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok = false;
				err = true;
				DisplayUtil.displayError("Update failed");
			} catch (ApplicationException re) {
				DisplayUtil.displayError("Update failed");
				handleException(reportId, re);
				ok = false;
				err = true;
			}
			if (ok) {
				DisplayUtil.displayInfo("Fire Factor update successful");
				try {
					emissionsReportService.modifyEmissionsReport(facility,
							scReports, report, null, null, openedForEdit);
					report = emissionsReportService.retrieveEmissionsReport(
							report.getEmissionsRptId(), true);
					if (report == null) {
						ok = false;
					}
					if (ok) {
						recreateTreeFromReport(treeNodeId(emissionsPeriod));
					}
				} catch (RemoteException re) {
					handleException(reportId, re);
					ok = false;
					err = true;
					DisplayUtil
							.displayError("Failed to re-read the emissions inventory");
				}
			}
		} else {
			DisplayUtil.displayError("No FIRE Factor was selected");
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void closeCancelFIRE() {
		setEditable(false);
		FacesUtil.returnFromDialogAndRefresh();
		return;
	}

	public final TreeModelBase getTreeData() {
		TreeModelBase rtn = null;
		if (!doRptCompare) {
			getReport();
		}
		if (err) {
			logger.error("doRptCompare=" + doRptCompare + ", savedReportId="
					+ savedReportId + ", reportId=" + reportId);
			// If error, make graceful return
			Stars2TreeNode stn = new Stars2TreeNode("", "", "", true, null);
			rtn = new TreeModelBase(stn);
		} else {
			rtn = getTreeData(treeNodeId(treeReport));
		}
		return rtn;
	}

	public static String treeNodeId(EmissionsReport rpt) {
		return "R" + rpt.getEmissionsRptId().toString();
	}

	public static String treeNodeId(EmissionsReportEUGroup grp) {
		return "G" + grp.getReportEuGroupName();
	}

	public static String treeNodeId(EmissionsReportPeriod p) {
		return "P" + p.getEmissionPeriodId();
	}

	public static String treeNodeId(EmissionsReportEU eu) {
		return "E" + eu.getEpaEmuId();
	}

	private TreeModelBase getTreeData(String selectedObj) {
		return getTreeData(selectedObj, null);
	}

	@SuppressWarnings("unchecked")
	private TreeModelBase getTreeData(String selectedObj,
			String argByClickEmiUnitOrGrp) {
		String id;
		boolean collaspeNodeFound = false;

		if (treeReport != null
				&& (treeData == null || !treeDataId.equals(treeReport
						.getEmissionsRptId().toString()))) {
			// Reset argByClickEmiUnitOrGrp to null when changing report.
			if (treeDataId != null && treeReport != null) {
				if (!treeDataId.equals(treeReport.getEmissionsRptId()
						.toString())) {
					argByClickEmiUnitOrGrp = null; // Set both the argument and
					byClickEmiUnitOrGrp = null; // the class variable.
				}
			}
			treeNodeMap = new HashMap<String, Stars2TreeNode>(); // reset map
			boolean leaf = false;
			List<EmissionsReportEUGroup> grps = treeReport.getEuGroups();

			int euCnt = 0;
			for (EmissionsReportEU eu : treeReport.getEus()) { // account for
				// EUs
				if (!eu.isForceDetailedReporting() && eu.isNoPeriods()
						&& treeReport.belongsToGroup(eu.getCorrEpaEmuId())) {
					continue; // skip ones that are just for groups
				}
				euCnt++;
			}

			if (grps.isEmpty() && euCnt == 0)
				leaf = true;
			id = treeNodeId(treeReport);
			String compareRptId = "";
			if (null != treeReport.getComp()) {
				compareRptId = ":"
						+ treeReport.getComp().getEmissionsInventoryId();
			}
			Stars2TreeNode reportNode = new Stars2TreeNode("report",
					treeReport.getEmissionsInventoryId() + compareRptId, id,
					leaf, treeReport);

			treeNodeMap.put(id, reportNode);
			treeData = new TreeModelBase(reportNode);
			treeDataId = treeReport.getEmissionsRptId().toString();

			if (selectedObj.equals(id)) {
				selectedTreeNode = reportNode;
				current = id;
			}

			// This is for expanding all tree nodes the first time the
			// tree is rendered
			ArrayList<String> treePath = new ArrayList<String>();

			treePath.add("0");
			int ii = 0;
			for (ii = 0; ii < grps.size(); ii++) { // account for groups
				treePath.add("0:" + Integer.toString(ii));
			}
			// account for EUs
			for (EmissionsReportEU eu : treeReport.getEus()) {
				if (!eu.isForceDetailedReporting() && eu.isNoPeriods() // (eu.getPeriods()
																		// ==
																		// null
																		// ||
																		// eu.getPeriods().isEmpty())
						&& treeReport.belongsToGroup(eu.getCorrEpaEmuId())) {
					continue; // skip ones that are just for groups
				}
				treePath.add("0:" + Integer.toString(ii));
				ii++;
			}

			// EU groups
			int grpNum = 1; // Consider changing to using group and unit
			// identifiers
			int byPassCnt = 0;

			for (EmissionsReportEUGroup euG : grps) {
				leaf = false;

				EmissionsReportPeriod ePeriod = euG.getPeriod();
				if (null == ePeriod)
					leaf = true;
				id = treeNodeId(euG);

				if (argByClickEmiUnitOrGrp != null && !collaspeNodeFound) {
					if (!argByClickEmiUnitOrGrp.equals(id)) {
						byPassCnt++;
						continue;
					}

					collaspeNodeFound = true;

					if (byPassCnt > 0) {
						treePath.add("0:" + Integer.toString(grpNum));
						grpNum++;
						Stars2TreeNode dummyNode = new Stars2TreeNode(
								"moreNodes", " ", "More Nodes", leaf, " ");
						reportNode.getChildren().add(dummyNode);
						byPassCnt = 0;
					}
				}

				Stars2TreeNode euGNode;
				String grpLabel;
				if (null == treeReport.getComp()) {
					grpLabel = euG.getReportEuGroupName() + getTotal(euG);
				} else {
					grpLabel = euG.getReportEuGroupName();
				}
				if (euG.isNotInFacility() || euG.isCaution()) {
					euGNode = new Stars2TreeNode("emissionUnitGroupsCaution",
							grpLabel, id, leaf, euG);
				} else {
					euGNode = new Stars2TreeNode("emissionUnitGroups",
							grpLabel, id, leaf, euG);
				}
				treeNodeMap.put(id, euGNode);
				if (selectedObj.equals(id)) {
					selectedTreeNode = euGNode;
					current = id;
				}

				if (!leaf) {
					leaf = true;
					// only 1 period
					treePath.add("0:" + Integer.toString(grpNum) + ":0");
					id = treeNodeId(ePeriod);
					Stars2TreeNode epNode;
					if (ePeriod.isCaution()) {
						epNode = new Stars2TreeNode("emissionPeriodsCaution",
								ePeriod.getTreeLabel(), id, leaf, ePeriod);
					} else {
						epNode = new Stars2TreeNode("emissionPeriods",
								ePeriod.getTreeLabel(), id, leaf, ePeriod);

					}
					treeNodeMap.put(id, epNode);
					if (selectedObj.equals(id)) {
						selectedTreeNode = epNode;
						current = id;
					}
					euGNode.getChildren().add(epNode);
				}

				reportNode.getChildren().add(euGNode);
				grpNum++;
			}

			// individual EUs
			int euNum = grpNum;
			for (EmissionsReportEU eu : treeReport.getEus()) {
				leaf = false;
				if (eu.isNoPeriods() // (eu.getPeriods() == null ||
										// eu.getPeriods().isEmpty())
						&& treeReport.belongsToGroup(eu.getCorrEpaEmuId())) {
					continue; // skip ones that are just for groups
				}

				ii = 0;
				if (!eu.isBelowRequirements() && !eu.isZeroEmissions()
						&& !eu.isNoPeriods() && !eu.isNotInFacility()
						&& (null != treeReport.getComp() || !eu.isCaution())) {
					for (ii = 0; ii < eu.getPeriods().size(); ii++) {
						treePath.add("0:" + Integer.toString(euNum) + ":"
								+ Integer.toString(ii));
					}
				} else
					leaf = true;

				id = treeNodeId(eu);

				if (argByClickEmiUnitOrGrp != null && !collaspeNodeFound) {
					if (!argByClickEmiUnitOrGrp.equals(id)) {
						byPassCnt++;
						continue;
					}

					collaspeNodeFound = true;

					if (byPassCnt > 0) {
						treePath.add("0:" + Integer.toString(euNum));
						euNum++;
						Stars2TreeNode dummyNode = new Stars2TreeNode(
								"moreNodes", " ", "More Nodes", leaf, " ");
						reportNode.getChildren().add(dummyNode);
						byPassCnt = 0;
					}
				}

				Stars2TreeNode euNode;
				String euLabel;
				if (null == treeReport.getComp()) {
					euLabel = eu.getEpaEmuId() + getTotal(eu);
				} else {
					euLabel = eu.getEpaEmuId();
				}
				if (2 != eu.getEmissionChoice()) {
					euNode = new Stars2TreeNode("emissionUnitsExcluded",
							euLabel, id, leaf, eu);
				} else if (eu.isNotInFacility() || eu.isCaution()) {
					euNode = new Stars2TreeNode("emissionUnitsCaution",
							euLabel, id, leaf, eu);
				} else {
					euNode = new Stars2TreeNode("emissionUnits", euLabel, id,
							leaf, eu);
				}
				treeNodeMap.put(id, euNode);
				if (selectedObj.equals(id)) {
					selectedTreeNode = euNode;
					current = id;
				}

				if (!eu.isBelowRequirements() && !eu.isZeroEmissions()
						&& !eu.isNoPeriods() && !eu.isNotInFacility()
						&& (null != treeReport.getComp() || !eu.isCaution())) {
					for (EmissionsReportPeriod period : eu.getPeriods()) {
						leaf = true;
						id = treeNodeId(period);
						Stars2TreeNode epNode;
						if (period.isNotInFacility() || period.isCaution()) {
							epNode = new Stars2TreeNode(
									"emissionPeriodsCaution",
									period.getTreeLabel(), id, leaf, period);
						} else {
							epNode = new Stars2TreeNode("emissionPeriods",
									period.getTreeLabel(), id, leaf, period);
						}
						treeNodeMap.put(id, epNode);
						if (selectedObj.equals(id)) {
							selectedTreeNode = epNode;
							current = id;
						}
						euNode.getChildren().add(epNode);
					}
				}
				reportNode.getChildren().add(euNode);
				euNum++;
			}

			TreeStateBase treeState = new TreeStateBase();

			treeState.expandPath(treePath.toArray(new String[0]));
			treeData.setTreeState(treeState);
			try {
				internalNodeClicked();
			} catch (ApplicationException re) {
				DisplayUtil
						.displayError("Failure to read Emissions Inventory due to "
								+ dataProb);
				handleException(treeReport.getEmissionsRptId(), re);
			}
		}
		return treeData;
	}

	public final String submitProfile() {
		FacilityProfileBase fp = (FacilityProfileBase) FacesUtil
				.getManagedBean("facilityProfile");
		if (isPortalApp()){
			fp.initFacilityProfile(fpId, inStaging);
		} else {
			fp.initFacilityProfile(fpId, false);
		}

		if (isPortalApp()) {
			if (!fp.isStaging()) {
				return FacilityProfileBase.HOME_FAC_OUTCOME;
			}
		} else if (isPublicApp()) {
			return FacilityProfileBase.HOME_FAC_OUTCOME;
		}

		return "goToFacilityPage";
	}

	public final String requestEditFP() {
		return "dialog:requestEditFP";
	}

	public void editFacilityProfile() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			editFacilityProfileInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final void editFacilityProfileInternal() {
		ok = true;
		// The facility inventory is frozen and must be split.
		// Report must refer to the new facility inventory and
		// the Emission unit Ids must be translated.
		try {
			facility = emissionsReportService.editFacilityProfile(report,
					facility, InfrastructureDefs.getCurrentUserId(),
					openedForEdit);
			if (facility == null) {
				ok = false;
			}
		} catch (RemoteException re) {
			handleException(reportId, re);
			ok = false;
		}
		// Make sure facilityProfile jsp page displays correct one
		if (ok) {
			fpId = facility.getFpId(); // get the new fpId.
			FacilityProfileBase fp = (FacilityProfileBase) FacesUtil
					.getManagedBean("facilityProfile");
			fp.setFpId(fpId);
			fp.setFacility(null);
			ReportSearch rs = (ReportSearch) FacesUtil
					.getManagedBean("reportSearch");
			rs.setPopupRedirectOutcome("goToFacilityPage");
		}
		if (!ok) {
			DisplayUtil
					.displayError("Failed to make Facility inventory editable");
		} else {
			DisplayUtil
					.displayInfo("Facility inventory is now editable. When the emissions inventory is submitted, the facility inventory will be preserved.");
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String requestDeletePeriod() {
		return "dialog:deletePeriod";
	}

	public String createEUListPop() {
		showHelp = false;
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		setAdminEditable(false);
		try {
			return createEUListPopInternal();
		} finally {
			clearButtonClicked();
		}
	}

	public String createEditEUListPop() {
		showHelp = false;
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			setAdminEditable(true);
			return createEUListPopInternal();
		} finally {
			clearButtonClicked();
		}
	}

	public final String createEUListPopInternal() {
		// build list of EUs
		euEg71Table = new ArrayList<EuEg71Choice>(200);
		for (EmissionsReportEU eu : report.getEus()) {
			EuEg71Choice ee71c = new EuEg71Choice(report, eu, scReports);
			euEg71Table.add(ee71c);
		}
		return "dialog:createEUListPop";
	}

	public void allNotOperate() {
		for (EuEg71Choice e71 : euEg71Table) {
			e71.setForceDetails(false); // this must be done first
			e71.setEg71Zero(new Integer(0));
		}
	}

	public void allDetailed() {
		for (EuEg71Choice e71 : euEg71Table) {
			if (!e71.isDeMinimis()) {
				e71.setInclude(true);
			} else {
				e71.setEg71Zero(null);
				e71.setForceDetails(true);
			}
		}
	}

	public void allEG71() {
		for (EuEg71Choice e71 : euEg71Table) {
			e71.setEg71Zero(new Integer(1));
			e71.setForceDetails(false);
		}
	}

	public void saveEmissionChoiceChanges() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			saveEmissionChoiceChangesInternal();
		} finally {
			clearButtonClicked();
		}
	}

	public boolean isAllow71Save() {
		return isModTV_SMTV() && !isClosedForEdit() && isInternalEditable();
	}

	private void saveEmissionChoiceChangesInternal() {
		setAdminEditable(false);
		passedValidation = null;
		ArrayList<EmissionsReportEU> euList = setEuLevelGenclearList();
		saveEmissionUnitInternal2(euList);
		if (ok) {
			byClickEmiUnitOrGrp = null;
			try {
				recreateTreeFromReport(treeNodeId(report));
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok = false;
				err = true;
			}
			if (!ok) {
				DisplayUtil.displayError("Read of emissions inventory failed");
			}
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	private ArrayList<EmissionsReportEU> setEuLevelGenclearList() {
		ArrayList<EmissionsReportEU> euList = new ArrayList<EmissionsReportEU>();
		for (EuEg71Choice e71 : euEg71Table) {
			e71.getEu().setExemptEG71(false);
			e71.getEu().setZeroEmissions(false);
			e71.getEu().setForceDetailedReporting(e71.isForceDetails());
			if (!e71.isForceDetails()) {
				if (e71.getEg71Zero() != null) {
					// set whether eg#71 or zero emissions
					if (e71.getEg71Zero() == 1) {
						e71.getEu().setExemptEG71(true);
						euList.add(e71.getEu()); // delete process information
					}
					if (e71.getEg71Zero() == 0) {
						e71.getEu().setZeroEmissions(true);
						euList.add(e71.getEu()); // delete process information
					}
				}
			}
		}
		return euList;
	}

	public String createGroupPop() {
		showHelp = false;
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			return createGroupPopInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final String createGroupPopInternal() {
		// build list of possible processes
		processGroupTable = new ArrayList<ProcessGroupChoice>(200);
		for (EmissionsReportEU eu : treeReport.getEus()) {
			if (!eu.isBelowRequirements() && eu.isNoPeriods()
					&& !eu.isNoProcesses()) {
				continue; // skip ones that are just for groups
			}
			if (!eu.isBelowRequirements()) {
				for (EmissionsReportPeriod p : eu.getPeriods()) {
					ProcessGroupChoice pgc = new ProcessGroupChoice(p, eu);
					processGroupTable.add(pgc);
				}
			}
		}
		return "dialog:createGroupPop";
	}

	public void createGroupFromPop() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			createGroupFromPopInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private void createGroupFromPopInternal() {
		if (firstGrpProcess != null) {
			// locate period
			emissionsPeriod = firstGrpProcess.getPeriod();
			try {
				previousCurrent = "";
				setPeriodFocus(true);
				createGroupInternal();
			} catch (ApplicationException ae) {
				DisplayUtil
						.displayError("Failed to create an emissions group with process having processId="
								+ emissionsPeriod.getEmissionPeriodId());
				handleException(report.getEmissionsRptId(), ae);
				logger.error("For reportId="
						+ report.getEmissionsRptId()
						+ ", could not create a group with process having processId="
						+ emissionsPeriod.getEmissionPeriodId());
			}
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void deletePeriod() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			deletePeriodInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final void deletePeriodInternal() {
		ok = true;
		// This period belongs to an EU -- not a group
		// Find the EU and remove the period from it.
		Integer corrId = null;
		EmissionsReportEU e = report.findEU(emissionsPeriod);
		if (e != null) {
			corrId = e.getCorrelationId();
		} else {
			logger.error("For reportId=" + report.getEmissionsRptId()
					+ ", could not find EU for period with id="
					+ emissionsPeriod.getEmissionPeriodId());
			ok = false;
			DisplayUtil.displayError("Failed to delete period");
		}
		if (ok) {
			try {
				emissionsReportService
						.modifyEmissionsReport(facility, scReports, report,
								null, emissionsPeriod, openedForEdit);
				// TODO should separate into two try blocks.
				report = emissionsReportService.retrieveEmissionsReport(
						report.getEmissionsRptId(), true);
				if (report == null) {
					ok = false;
				}
				if (ok) {
					e = report.getEu(corrId);
					if (e == null || e.isNoPeriods()) { // e.getPeriods().isEmpty())
														// {
						// entire eu is gone
						setReportFocus();
						byClickEmiUnitOrGrp = null;
					} else {
						// change focus to eu the period belonged to
						emissionUnit = e;
						setEUFocus();
					}
					recreateTreeFromReport(current);
				}
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok = false;
				err = true;
			} catch (ApplicationException re) {
				handleException(reportId, re);
				ok = false;
				err = true;
			}
		}
		if (ok) {
			DisplayUtil.displayInfo("Period successfully deleted");
		} else {
			emissionReportWrapper.setWrappedData(null);
			hapTable = false;
			emissionReportWrapperHAP.setWrappedData(null);
			emissionEuWrapper.setWrappedData(null);
			emissionEuWrapperHAP.setWrappedData(null);
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String requestDeleteEmissionUnit() {
		return "dialog:deleteEmissionUnit";
	}

	// delete emissions is marked eg#71 or marked zero emissions
	public final String deleteEG71Emissions() {
		return "dialog:deleteEmissions";
	}

	public final void deleteEmissionUnit() {
		ok = true;
		byClickEmiUnitOrGrp = null; // turn off tree collapse
		try {
			// Remove all periods under the EU
			ArrayList<EmissionsReportEU> euList = new ArrayList<EmissionsReportEU>(
					1);
			euList.add(emissionUnit);
			emissionsReportService.modifyEmissionsReport(facility, scReports,
					report, euList, null, openedForEdit);
			// TODO should separate into two try blocks.
			report = emissionsReportService.retrieveEmissionsReport(
					report.getEmissionsRptId(), true);
			if (report == null) {
				ok = false;
			}
			if (ok) {
				setReportFocus();
				recreateTreeFromReport(current);
			}
		} catch (RemoteException re) {
			handleException(report.getEmissionsRptId(), re);
			ok = false;
			err = true;
			DisplayUtil.displayError("Failed to delete Emission Unit");
		} catch (ApplicationException re) {
			DisplayUtil.displayError("Failed to delete Emission Unit");
			handleException(report.getEmissionsRptId(), re);
			ok = false;
			err = true;
			emissionReportWrapper.setWrappedData(null);
			hapTable = false;
			emissionReportWrapperHAP.setWrappedData(null);
		}
		if (ok) {
			DisplayUtil.displayInfo("Emission Unit successfully deleted");
		} else {
			emissionReportWrapper.setWrappedData(null);
			hapTable = false;
			emissionReportWrapperHAP.setWrappedData(null);
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String startCreateRevisedReport() {
		try {
			rptInfo = null;
			rptInfo = emissionsReportService.getEmissionsRptInfo(
					report.getReportYear(), facility.getFacilityId());
		} catch (RemoteException re) {
			handleException(reportId, re);
		}
		return "dialog:reviseReport";
	}

	private final void compare() {
		try {
			compareReport = emissionsReportService.retrieveEmissionsReport(
					selectedReportId, false);
			doRptCompare = true;
			compareFacility = facilityService
					.retrieveFacilityProfile(compareReport.getFpId());
			emissionsReportService.locatePeriodNames(compareFacility,
					compareReport);
			treeReport = emissionsReportService.generateComparisonReport(
					report, compareReport);
			treeData = null;
			reportEmissions = EmissionRow.getEmissions(treeReport,
					doRptCompare, isClosedForEdit(), percentDiff, scReports,
					logger, false);
			reportTitle = compRptTitle
					+ compareReport.getEmissionsRptId().toString();
		} catch (RemoteException re) {
			handleException(selectedReportId, re);
			logger.error("Failure to compare Emissions Inventory "
					+ report.getEmissionsInventoryId() + " to report "
					+ compareReport.getEmissionsInventoryId());
			reportEmissions = null;
			emissionReportWrapper.setWrappedData(null);
			hapTable = false;
			emissionReportWrapperHAP.setWrappedData(null);
			err = true;
		} catch (ApplicationException re) {
			handleException(selectedReportId, re);
			logger.error(
					"Failure to compare Emissions Inventory "
							+ report.getEmissionsInventoryId() + " to report "
							+ compareReport.getEmissionsInventoryId()
							+ " due to " + dataProb, re);
			reportEmissions = null;
			emissionReportWrapper.setWrappedData(null);
			hapTable = false;
			emissionReportWrapperHAP.setWrappedData(null);
			err = true;
		}
		wrapEmissions(emissionReportWrapper, emissionReportWrapperHAP,
				reportEmissions, false);
	}

	// Compare current report to the previous report.
	public final String compareToPrevious() {
		// Build comparison of reportId and previously modifed report
		selectedReportId = report.getReportModified();
		compare();
		return TV_REPORT;
	}

	public final String selectCompareReport() throws RemoteException {
		submittedReports = getEmissionsReports(facility.getFacilityId(), false);
		if (submittedReports.length == 0) {
			DisplayUtil
					.displayInfo("This facility does not have any emissions inventories");
			return null;
		}

		return "dialog:selectCompareReport";
	}

	public final void cancelCompareReportDialog() {
		FacesUtil.returnFromDialogAndRefresh();
		doRptCompare = false;
		compareReport = null;
		reportTitle = rptTitle;
		try {
			setReportFocus();
		} catch (ApplicationException e) {
			handleException(report.getEmissionsRptId(), e);
			emissionReportWrapper.setWrappedData(null);
			emissionReportWrapperHAP.setWrappedData(null);
			hapTable = false;
		}
	}

	public final void startReportCompare() {
		int err = 0;
		// make sure percentDiff is provided
		if (percentDiff == null) {
			DisplayUtil
					.displayError("Attribute 'Highlight emissions differences of more than' is not set.");
			err++;
		}
		// make sure selectedReportId is provided
		if (getSelectedReportId() == null) {
			DisplayUtil.displayError("No Emissions Inventory was selected");
			err++;
		}
		if (err == 0) {
			// Build comparison of reportId and selectedReportId
			compare();
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public final void compareReportOK() {
		previousCurrent = ""; // reset history
		doRptCompare = false;
		compareReport = null;
		treeReport = report;
		treeData = null;
		reportTitle = rptTitle;
		try {
			setReportFocus();
		} catch (ApplicationException e) {
			handleException(report.getEmissionsRptId(), e);
		}
	}

	public final String editReport() {
		showHelp = false;
		setEditable(true);
		return TV_REPORT;
	}

	public final String updateInvoiceInfo() {
		showHelp = false;
		setUpdatingInvoiceInfo(true);
		return TV_REPORT;
	}

	public String saveReport() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			return saveReportInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final String saveReportInternal() {
		setEditable(false);
		setUpdatingInvoiceInfo(false);
		passedValidation = null;
		/*
		 * if(report.isRptEIS()) { // If EIS turned on, then set the EIS report
		 * template scReports.setScEIS(locatedScReports.getScEIS()); } else { //
		 * If EIS turned off, then clear the EIS report template.
		 * scReports.setScEIS((SCEmissionsReport)null); } if(report.isRptES()) {
		 * scReports.setScES(locatedScReports.getScES()); } else {
		 * scReports.setScES((SCEmissionsReport)null); }
		 */

		scReports.setSc(locatedScReports.getSc());
		String rtn = null;
		rtn = saveReportInternal2();
		return rtn;
	}

	public void confirmedSaveReport() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		setEditable(false);
		passedValidation = null;
		haveEmissions = null;
		/*
		 * if(report.isRptEIS()) { // If EIS turned on, then set the EIS report
		 * template scReports.setScEIS(locatedScReports.getScEIS()); } else { //
		 * If EIS turned off, then clear the EIS report template.
		 * scReports.setScEIS((SCEmissionsReport)null); } if(report.isRptES()) {
		 * scReports.setScES(locatedScReports.getScES()); } else {
		 * scReports.setScES((SCEmissionsReport)null); }
		 */

		scReports.setSc(locatedScReports.getSc());
		saveReportInternal2();
		FacesUtil.returnFromDialogAndRefresh();
		clearButtonClicked();
	}

	public String confirmSaveReport() {
		return "dialog:confirmSaveReport";
	}

	private final String saveReportInternal2() {
		ArrayList<EmissionsReportEU> euList = null;
		boolean isModified = false;
		try {
			euList = setEuLevelGenclearList(); // update the EUs and get clear
												// list
			if (!validateEdit(true)) {
				return null;
			}
			isModified = emissionsReportService.modifyEmissionsReport(facility,
					scReports, report, euList, null, openedForEdit);
			// TODO do these separately
			report = emissionsReportService.retrieveEmissionsReport(
					report.getEmissionsRptId(), true);
			if (report == null) {
				ok = false;
			}
			if (ok) {
				setReportFocus();
				recreateTreeFromReport(current);
			}
		} catch (RemoteException re) {
			handleException(report.getEmissionsRptId(), re);
			ok = false;
			err = true;
		} catch (Exception e) {
			handleException(report.getEmissionsRptId(), e);
			ok = false;
			err = true;
		}
		if (ok && isModified == true) {
			DisplayUtil.displayInfo("Emissions inventory updated successfully");
		} else {
			DisplayUtil.displayError("Failed to update emissions inventory");
		}
		return TV_REPORT;
	}

	public final String cancelReportEdit() {
		setEditable(false);
		setUpdatingInvoiceInfo(false);
		// report.expandRptTypes(); // Restore original values.
		return TV_REPORT;
	}

	/*
	 * TO DO For each selected service catalog detail ... , enable reporting as
	 * follows: --- for the previous emissions reporting year if the associated
	 * content type for a service catalog detail is configured to apply to a
	 * full year; and --- for the current emissions reporting year if the
	 * associated content type for a service catalog detail is not configured
	 * for a full year.
	 * 
	 * NEEDS WORK The assumption is that reporting will not be enabled until
	 * January of the next year. Individually, one can enable the current year
	 * (3rd level menu at facility)
	 */
	public final String enableAllReporting() {
    	
		if (scEmissionsReports == null) {
			DisplayUtil
					.displayError("There are no service catalog details defined for year "
							+ enableYear + ".");
			return null;
		}
    	
		Map<SCEmissionsReport, Integer> enabledCnt = new HashMap<SCEmissionsReport, Integer>();
		ok = true;
		// enableYear set by getEnableYear()
		try {
			for (SCEmissionsReport s : scEmissionsReports) {
				if (s.isSelected()) {
					int cnt = emissionsReportService.turnOnReporting(s);
					enabledCnt.put(s, new Integer(cnt));
				}
			}
		} catch (RemoteException re) {
			handleException(re);
			ok = false;
		}
		if (ok) {
        	for (Entry<SCEmissionsReport, Integer> e : enabledCnt.entrySet()) {
        		
        		String contentTypeDesc = 
    					ContentTypeDef.getData().getItems().getItemDesc(e.getKey().getContentTypeCd());
        		String regReqDesc = 
    					RegulatoryRequirementTypeDef.getData().getItems().getItemDesc(e.getKey().getRegulatoryRequirementCd());
        		DisplayUtil.displayInfo("Successfully enabled reporting for Reporting Year: " + e.getKey().getReportingYear() +
        			"; Content Type: " + contentTypeDesc +
        			"; Regulatory Requirement: " + regReqDesc +
                    " (" + e.getValue().intValue() + " facilities changed to reporting enabled).");
        	}
		} else {
			DisplayUtil.displayError("Failed to enable reporting for year "
					+ enableYear);
		}
		return null;
	}

	public final String viewMissingFIREFactors() {
		String ret = null;
		ok = true;

		if (missingFactorsYear == null) {
			ok = false;
			DisplayUtil.displayError("Please select a reporting year.");
			return ret;
		}

		try {
			setMissingFactors(emissionsReportService.retrieveMissingFactors(
					missingFactorsYear, facilityClass));
			missingFactorsWrapper = new TableSorter();
			missingFactorsWrapper.setWrappedData(getMissingFactors());
			ret = "dialog:missingFIREFactors";
		} catch (RemoteException re) {
			handleException(re);
			ok = false;
		}

		if (!ok) {
			DisplayUtil
					.displayError("Failed to generate missing FIRE factor report.");
		}

		return ret;
	}

	public final String editEmissionGroup() {
		setEditable(true);
		removeGroup = false;
		return TV_REPORT;
	}

	public String saveEmissionGroup() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			return saveEmissionGroupInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final String saveEmissionGroupInternal() {
		passedValidation = null;
		report.clearValidationMessages();
		if (removeGroup) {
			removeGroup = false;
			// Remove all group members.
			currentGrpEUs = new ArrayList<Integer>();
		}
		ValidationMessage[] validationMessages = report.verifyUniqueGrpName(
				grpName, grpEmissionUnit);

		boolean hasError = displayValidationMessages(
				"body:detailPage:comRptDetails:reportProfilePage:emissionUnitGrpRep:",
				validationMessages);
		if (hasError)
			return "Fail";
		setEditable(false);

		grpEmissionUnit.setReportEuGroupName(grpName);
		// Act on the changes. These cannot be in error.
		// Compare Current Group with currentGrpEUs
		// Any in Group and in currentGrpEUs -- no change (except remove from
		// currentGrpEUs
		// Any in Group and not in currentGrpEUs -- remove from group
		List<Integer> l = grpEmissionUnit.getEus();
		int i = 0;
		while (i < l.size()) {
			Integer euId = l.get(i);
			// Locate the emission unit in the report
			EmissionsReportEU rEu = report.getEu(euId);
			boolean exists = currentGrpEUs.remove(euId);
			if (null != rEu && !exists) {
				// has been removed from the emissions group
				l.remove(i);
				// add the period.
				if (!rEu.isBelowRequirements()) {
					EmissionsReportPeriod p;
					if (!currentGrpEUs.isEmpty() || !l.isEmpty()) {
						p = new EmissionsReportPeriod(
								grpEmissionUnit.getPeriod(), true);
					} else {
						p = grpEmissionUnit.getPeriod();
					}
					rEu.getPeriods().add(p);
				}
			} else
				i++; // Note: Don't add one if the true alternative was taken
		}
		// Any remaining in currentGrpEUs -- add to group
		String sccId = grpEmissionUnit.getPeriod().getSccId();
		while (!currentGrpEUs.isEmpty()) {
			Integer euId = currentGrpEUs.remove(0);
			// find the report EU
			EmissionsReportEU eR = report.getEu(euId);
			EmissionsReportPeriod p = eR.getPeriod(sccId); // find the group
			// period
			eR.getPeriods().remove(p); // remove the group period
			// add the report EU to the group
			grpEmissionUnit.addEu(eR.getCorrEpaEmuId());
		}
		if (grpEmissionUnit.getEus().isEmpty()) {
			// Get rid of group
			report.getEuGroups().remove(grpEmissionUnit);
			byClickEmiUnitOrGrp = null; // turn off tree collapse
		}
		ok = true;
		try {
			emissionsReportService.modifyEmissionsReport(facility, scReports,
					report, null, null, openedForEdit);
			// TODO should separate into two try blocks.
			report = emissionsReportService.retrieveEmissionsReport(
					report.getEmissionsRptId(), true);
			if (report == null) {
				ok = false;
			}
			if (ok) {
				// Note grpEmissionUnit still points to group in the report
				// before being read back into memory by modifyEmissionReport()
				if (grpEmissionUnit.getEus().isEmpty()) {
					byClickEmiUnitOrGrp = null;
					setReportFocus();
				} else {
					// Need to relocate same group
					setEUGroupFocus(report.findEuG(grpEmissionUnit
							.getReportEuGroupID()));
					// in case group name changes
					byClickEmiUnitOrGrp = current;
				}

				recreateTreeFromReport(current);
			}
		} catch (RemoteException re) {
			handleException(report.getEmissionsRptId(), re);
			DisplayUtil.displayError("Failed to update group");
			err = true;
			ok = false;
		} catch (ApplicationException re) {
			handleException(report.getEmissionsRptId(), re);
			DisplayUtil.displayError("Failed to update group");
			err = true;
			ok = false;
		}
		if (ok) {
			DisplayUtil.displayInfo("Group/group members updated successfully");
		} else {
		}
		return TV_REPORT;
	}

	public final String cancelEmissionGroup() {
		setEditable(false);
		removeGroup = false;
		return TV_REPORT;
	}

	public String createGroup() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			return createGroupInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final String createGroupInternal() {
		ok = true;
		// The currently selected period/process is to be placed into a new
		// group
		EmissionsReportEU e = report.findEU(emissionsPeriod);
		EmissionsReportEUGroup newGrp = new EmissionsReportEUGroup();
		// set temporary group name
		newGrp.setReportEuGroupName(report.uniqueDefaultName());
		newGrp.setPeriod(new EmissionsReportPeriod(emissionsPeriod));
		newGrp.addEu(e.getCorrEpaEmuId());
		e.addEuGroup(newGrp);
		report.addEuGroup(newGrp); // link to report
		e.getPeriods().remove(emissionsPeriod);
		String grpId = treeNodeId(newGrp);

		// Rebuild the tree and mark group as the selected node.
		try {
			emissionsReportService.modifyEmissionsReport(facility, scReports,
					report, null, emissionsPeriod, openedForEdit);
			// TODO should separate into two try blocks.
			report = emissionsReportService.retrieveEmissionsReport(
					report.getEmissionsRptId(), true);
			if (report == null) {
				ok = false;
			}
			if (ok) {
				// group does not exist in tree until after this.
				byClickEmiUnitOrGrp = grpId;
				recreateTreeFromReport(grpId);
			}
		} catch (RemoteException re) {
			handleException(report.getEmissionsRptId(), re);
			logger.error(
					"Failure creating group with EU with correlation Id ="
							+ e.getCorrEpaEmuId() + " in Emissions Inventory "
							+ report.getEmissionsRptId(), re);
			ok = false;
			err = true;
		}
		if (ok) {
			DisplayUtil
					.displayInfo("Group created successfully, add additional group members and Save.");
			setEditable(true);
		} else {
			DisplayUtil.displayError("Failed to create group");
			emissionPeriodWrapper.setWrappedData(null);
			emissionPeriodWrapperHAP.setWrappedData(null);
			hapTable = false;
			emissionReportWrapper.setWrappedData(null);
			emissionReportWrapperHAP.setWrappedData(null);
			emissionGroupWrapper.setWrappedData(null);
			emissionGroupWrapperHAP.setWrappedData(null);
			emissionEuWrapper.setWrappedData(null);
			emissionEuWrapperHAP.setWrappedData(null);
		}
		return TV_REPORT;
	}

	public final String editViewExpTS() {
		justificationStr = explainTSStr;
		if (null == secretEmissionRow.getTradeSecretEText()
				|| secretEmissionRow.getTradeSecretEText().trim().length() == 0) {
			tsJust = new String();
		} else
			tsJust = secretEmissionRow.getTradeSecretEText();
		return "dialog:tradeSecret";
	}

	public final String editViewFactorTS() {
		justificationStr = factorTSStr;
		if (null == secretEmissionRow.getTradeSecretFText()
				|| secretEmissionRow.getTradeSecretFText().trim().length() == 0) {
			tsJust = new String();
		} else
			tsJust = secretEmissionRow.getTradeSecretFText();
		return "dialog:tradeSecret";
	}

	public final String editViewMaterialTS() {
		justificationStr = materialTSStr;
		if (null == secretMaterialRow.getTradeSecretMText()
				|| secretMaterialRow.getTradeSecretMText().trim().length() == 0) {
			tsJust = new String();
		} else
			tsJust = secretMaterialRow.getTradeSecretMText();
		return "dialog:tradeSecretM";
	}

	public final String editViewThroughputTS() {
		justificationStr = throughputTSStr;
		if(null!=secretMaterialRow){
		if (null == secretMaterialRow.getTradeSecretTText()
				|| secretMaterialRow.getTradeSecretTText().trim().length() == 0) {
			tsJust = new String();
		} else
			tsJust = secretMaterialRow.getTradeSecretTText();
		}
		return "dialog:tradeSecretM";
	}

	public final String editViewSchedTS() {
		justificationStr = schedTSStr;
		if (null == webPeriod.getTradeSecretSText()
				|| webPeriod.getTradeSecretSText().trim().length() == 0) {
			tsJust = new String();
		} else
			tsJust = webPeriod.getTradeSecretSText();
		return "dialog:tradeSecretSched";
	}

	public final String editViewVariableTS() {
		justificationStr = variableTSStr;
		if (null == secretVariableRow.getTradeSecretText()
				|| secretVariableRow.getTradeSecretText().trim().length() == 0) {
			tsJust = new String();
		} else
			tsJust = secretVariableRow.getTradeSecretText();
		return "dialog:tradeSecretSched";
	}

	public final String editViewExp() {
		justificationStr = explainStr;
		if (null == secretEmissionRow.getExplanation()
				|| secretEmissionRow.getExplanation().trim().length() == 0) {
			tsJust = new String();
		} else
			tsJust = secretEmissionRow.getExplanation();
		return "dialog:tradeSecretES";
	}

	public void applyEditTS() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			applyEditTSInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final void applyEditTSInternal() {
		// "==" works here since looking for same string instance
		if (justificationStr == factorTSStr) {
			if (null != tsJust && tsJust.trim().length() > 0) {
				secretEmissionRow.setTradeSecretF(true);
				secretEmissionRow.setTradeSecretFText(tsJust);
			} else {
				secretEmissionRow.setTradeSecretF(false);
				secretEmissionRow.setTradeSecretFText(null);
			}
		} else if (justificationStr == explainTSStr) {
			if (null != tsJust && tsJust.trim().length() > 0) {
				secretEmissionRow.setTradeSecretE(true);
				secretEmissionRow.setTradeSecretEText(tsJust);
			} else {
				secretEmissionRow.setTradeSecretE(false);
				secretEmissionRow.setTradeSecretEText(null);
			}
		} else if (justificationStr == materialTSStr) {
			if (null != tsJust && tsJust.trim().length() > 0) {
				secretMaterialRow.setTradeSecretM(true);
				secretMaterialRow.setTradeSecretMText(tsJust);
			} else {
				secretMaterialRow.setTradeSecretM(false);
				secretMaterialRow.setTradeSecretMText(null);
			}
		} else
			
			if (justificationStr == throughputTSStr) {
			if (null != tsJust && tsJust.trim().length() > 0) {
				secretMaterialRow.setTradeSecretT(true);
				secretMaterialRow.setTradeSecretTText(tsJust);
			} else {
				secretMaterialRow.setTradeSecretT(false);
				secretMaterialRow.setTradeSecretTText(null);
			}
		} else if (justificationStr == schedTSStr) {
			if (null != tsJust && tsJust.trim().length() > 0) {
				webPeriod.setTradeSecretS(true);
				webPeriod.setTradeSecretSText(tsJust);
			} else {
				webPeriod.setTradeSecretS(false);
				webPeriod.setTradeSecretSText(null);
			}
		} else if (justificationStr == variableTSStr) {
			if (null != tsJust && tsJust.trim().length() > 0) {
				secretVariableRow.setTradeSecret(true);
				secretVariableRow.setTradeSecretText(tsJust);
			} else {
				secretVariableRow.setTradeSecret(false);
				secretVariableRow.setTradeSecretText(null);
			}
		} else if (justificationStr == explainStr) {
			if (null != tsJust && tsJust.trim().length() > 0) {
				secretEmissionRow.setExplanation(tsJust);
			} else {
				secretEmissionRow.setExplanation(null);
			}
		} else if (justificationStr == yearlyComment) {
			if (null != tsJust && tsJust.trim().length() > 0) {
				emissionsRptInfoRow.setComment(tsJust);
			} else {
				emissionsRptInfoRow.setComment(null);
			}
		} else {
			logger.error("Not expected: justificationStr=\"" + justificationStr
					+ "\", tsJust=\"" + tsJust + "\"");
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void viewCalcOK() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	// Used both for cancel and close
	public final void cancelEditTS() {
		setAdminEditable(false); // some calls to here start in edit mode.
		setEditable(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	// Used both for cancel and close
	public final void cancelEditExplanation() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final boolean isShowReopenEdit() {
		return report.isSubmitted() && isStars2Admin() && !openedForEdit;
	}

	public final String reopenEdit() {
		// For STARS2ADMIN to do edits after submit.
		openedForEdit = true;
		hasBeenOpenedForEdit = true;
		DisplayUtil
				.displayInfo("Opened for edit.  Press Recompute Totals button when done.");
		try {
			emissionsReportService.reportFacilityMatch(facility, report,
					scReports);
			emissionsReportService.locatePeriodNames(facility, report);
			treeData = null; // Recreate tree
			getTreeData(treeNodeId(treeReport));
		} catch (RemoteException re) {
			handleException(reportId, re);
			setErr(true);
		} catch (Exception e) {
			handleException(reportId, e);
			setErr(true);
		}
		return TV_REPORT;
	}

	public final boolean isShowRecomputeTotals() {
		return report.isSubmitted() && isStars2Admin() && openedForEdit
				&& (isHasPassedValidation() || report.isValidated());
	}

	public void recomputeTotals() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			recomputeTotalsInternal();
		} finally {
			clearButtonClicked();
		}
	}

	private final void recomputeTotalsInternal() {
		// For STARS2ADMIN to finish edits after submit.
		openedForEdit = false;
		 
		//If a csv imported EI report is Reopened and saved, mark Generated from Import flag to false ('N')
		if(report.isAutoGenerated()) {
			report.setAutoGenerated(false);
		}
		
		// Since reportFocus() already called, just set
		// total lines and update report.
		ok = true;
		try {
			emissionsReportService.createTotalsRows(report, reportEmissions);
			emissionsReportService.modifyEmissionsReport(facility, scReports,
					report, null, null, openedForEdit);
		} catch (RemoteException e) {
			handleException(report.getEmissionsRptId(), e);
			ok = false;
			err = true;
			DisplayUtil.displayError("Failed to update emissions totals");
		}
		if (ok) {
			DisplayUtil.displayInfo("Totals updated, editing complete");
			try {
				report = emissionsReportService.retrieveEmissionsReport(
						report.getEmissionsRptId(), true);
				recreateTreeFromReport(treeNodeId(report));
			} catch (RemoteException e) {
				handleException(report.getEmissionsRptId(), e);
				ok = false;
				err = true;
				DisplayUtil.displayError("Failed to read emissions inventory");
			}
		}
	}

	public final TableSorter getEmissionPeriodWrapper() {
		return emissionPeriodWrapper;
	}

	public final TableSorter getEmissionEuWrapper() {
		return emissionEuWrapper;
	}

	public final TableSorter getEmissionGroupWrapper() {
		return emissionGroupWrapper;
	}

	public final TableSorter getEmissionReportWrapper() {
		return emissionReportWrapper;
	}

	public final boolean getRenderSum() {
		return renderSum;
	}

	public final LinkedHashMap<Integer, Integer> getReportYears() {
		return reportYears;
	}

	public final LinkedHashMap<String, String> getReportContentTypes() {
		return reportContentTypes;
	}

	public final String getUserMsg() {
		return userMsg;
	}

	public final TableSorter getFireWrapper() {
		return fireWrapper;
	}

	public final Integer getCompareReportId() {
		return selectedReportId;
	}

	public final void setCompareReportId(Integer selectedReportId) {
		this.selectedReportId = selectedReportId;
	}

	public final boolean isDoRptCompare() {
		return doRptCompare;
	}

	public final String getReportTitle() {
		return reportTitle;
	}

	public final Integer getPercentDiff() {
		return percentDiff;
	}

	public final void setPercentDiff(Integer percentDiff) {
		this.percentDiff = percentDiff;
	}

	public final String getRenderComponent() {
		return renderComponent;
	}

	public final void setRenderComponent(String renderComponent) {
		this.renderComponent = renderComponent;
	}

	// This gets all EU/processes that share the same equipment for the
	// report and for the report when it is being compared to another report
	public final List<SelectItem> getAllGrpEUs() {
		if (doRptCompare) {
			getAllGrpEUs(compareFacility, treeReport.getOrig(),
					grpEmissionUnit.getOrig(), allGrpEUs);
		} else {
			getAllGrpEUs(facility, report, grpEmissionUnit, allGrpEUs);
		}

		return allGrpEUs;
	}

	private final void getAllGrpEUs(Facility fac, EmissionsReport rpt,
			EmissionsReportEUGroup euG, List<SelectItem> allList) {
		// Class EmissionReportPdf generator uses the
		// first part of this same function (with simplifications).

		// The shuttle modifies this collection making it not work
		// so we will reconstruct it each time.
		allList.clear();
		// The SCC for the group
		String sccId = euG.getPeriod().getSccId();
		// add in those already in the group
		EmissionProcess repEuP = null;
		for (Integer euId : euG.getEus()) {
			EmissionsReportEU e = rpt.getEu(euId);
			// Get process name
			String pName = "";
			EmissionUnit facilityEu = fac.getMatchingEmissionUnit(e
					.getCorrEpaEmuId());
			if (null != facilityEu) {
				EmissionProcess euP = facilityEu.findProcess(sccId);
				if (null != euP) { // should have found it.
					repEuP = euP; // get representative facility process
					if (null != euP.getProcessId()) {
						pName = euP.getProcessId();
					}
				}
			}
			allList.add(new SelectItem(euId, e.getEpaEmuId() + ":" + pName));
		}
		if (repEuP == null) {
			// This may be true for migrated groups.
			// Since no members, add all that have same SCC regardless of
			// equipment
			for (EmissionsReportEU e : rpt.getEus()) {
				if (e.isBelowRequirements() || e.isNotInFacility()) {
					continue;
				}
				EmissionUnit facilityEu = fac.getMatchingEmissionUnit(e
						.getCorrEpaEmuId());
				if (null != facilityEu) {
					EmissionProcess euP2 = facilityEu.findProcess(sccId);
					if (null != euP2) { // May not be any matching ones.
						// found process under the EU in facility
						// Now look for a matching period
						for (EmissionsReportPeriod p : e.getPeriods()) {
							if (p != null && !p.isNotInFacility()
									&& p.getSccId() != null
									&& p.getSccId().equals(sccId)) {
								allList.add(new SelectItem(e.getCorrEpaEmuId(),
										e.getEpaEmuId() + ":"
												+ p.getTreeLabel()));
							}
						}
					}
				}

			}
		} else {
			// Which others could belong
			for (EmissionsReportEU e : rpt.getEus()) {
				if (e.isBelowRequirements() || e.isNotInFacility()) {
					continue;
				}
				EmissionUnit facilityEu = fac.getMatchingEmissionUnit(e
						.getCorrEpaEmuId());
				if (null != facilityEu) {
					EmissionProcess euP2 = facilityEu.findProcess(sccId);
					if (null != euP2) { // May not be any matching ones.
						// found process under the EU in facility
						// Now look for a matching period
						for (EmissionsReportPeriod p : e.getPeriods()) {
							if (p != null && !p.isNotInFacility()
									&& p.getSccId() != null
									&& p.getSccId().equals(sccId)
									&& repEuP.sameEquipment(euP2)) {
								allList.add(new SelectItem(e.getCorrEpaEmuId(),
										e.getEpaEmuId() + ":"
												+ p.getTreeLabel()));
							}
						}
					}
				}

			}
		}
		return;
	}

	public final List<SelectItem> getAllCompareGrpEUs() {
		getAllGrpEUs(compareFacility, treeReport.getComp(),
				grpEmissionUnit.getComp(), allCompareGrpEUs);
		return allCompareGrpEUs;
	}

	public final List<Integer> getCurrentGrpEUs() {
		if (doRptCompare) {
			getCurrentGrpEUs(treeReport.getOrig(), grpEmissionUnit.getOrig(),
					currentGrpEUs);
		} else {
			getCurrentGrpEUs(report, grpEmissionUnit, currentGrpEUs);
		}

		return currentGrpEUs;
	}

	public final List<Integer> getCurrentCompareGrpEUs() {
		getCurrentGrpEUs(treeReport.getComp(), grpEmissionUnit.getComp(),
				currentCompareGrpEUs);
		return currentCompareGrpEUs;
	}

	private final void getCurrentGrpEUs(EmissionsReport rpt,
			EmissionsReportEUGroup euG, List<Integer> currentList) {
		currentList.clear();
		for (Integer euId : euG.getEus()) {
			// EmissionsReportEU e = rpt.getEu(euId);
			currentList.add(euId);
		}
		return;
	}

	public final void setCurrentGrpEUs(List<Integer> currentGrpEUs) {
		this.currentGrpEUs = currentGrpEUs;
	}

	public List<String> getCurrentTblCompareGrpEUs() {
		getAllGrpEUs(compareFacility, treeReport.getComp(),
				grpEmissionUnit.getComp(), allCompareGrpEUs);
		currentTblCompareGrpEUs = new ArrayList<String>(grpEmissionUnit
				.getComp().getEus().size());
		for (Integer i : grpEmissionUnit.getComp().getEus()) {
			for (SelectItem si : allCompareGrpEUs) {
				if (si.getValue().equals(i)) {
					currentTblCompareGrpEUs.add(si.getLabel());
					break;
				}
			}
		}
		return currentTblCompareGrpEUs;
	}

	public List<String> getCurrentTblGrpEUs() {
		EmissionsReportEUGroup euG;
		if (doRptCompare) {
			getAllGrpEUs(compareFacility, treeReport.getOrig(),
					grpEmissionUnit.getOrig(), allGrpEUs);
			euG = grpEmissionUnit.getOrig();
		} else {
			getAllGrpEUs(facility, report, grpEmissionUnit, allGrpEUs);
			euG = grpEmissionUnit;
		}
		currentTblGrpEUs = new ArrayList<String>(euG.getEus().size());
		for (Integer i : euG.getEus()) {
			for (SelectItem si : allGrpEUs) {
				if (si.getValue().equals(i)) {
					currentTblGrpEUs.add(si.getLabel());
					break;
				}
			}
		}
		return currentTblGrpEUs;
	}

	public final EmissionsReportPeriod getWebPeriod() {
		return webPeriod;
	}

	public final void setWebPeriod(EmissionsReportPeriod webPeriod) {
		this.webPeriod = webPeriod;
	}

	public final EmissionsRptInfo getCurrentRptInfo() {
		// Get report for the year prior to the current year.
		getYearlyReportingInfo();
		return currentRptInfo;
	}

	// Retrieve information if needed, else do nothing.
	protected void getYearlyReportingInfo() {
		// TODO may still need to check state= shutdown to revise state.
		if (null == savedFpId || !savedFpId.equals(fpId)) {
			currentRptInfo = null;
			previousRptInfo = null;
			anticipatedRptInfo = null;
			emissionsReportInfo = null;
			defaultCategoryYear = null;
			Facility f2;
			int yr = Calendar.getInstance().get(Calendar.YEAR);
			try {
				// Get instance to get facility Id
				Facility f1 = facilityService.retrieveFacility(fpId);
				if (f1 == null) {
					// Handle case where not found (e.g., because new in
					// staging)
					anticipatedRptInfo = new EmissionsRptInfo();
					anticipatedRptInfo.setYear(yr);
					currentRptInfo = anticipatedRptInfo;
					return;
				}
				facilityId = f1.getFacilityId();
				f2 = facilityService.retrieveFacility(facilityId);
				savedFpId = fpId;

				emissionsReportInfo = emissionsReportService
						.getYearlyReportingInfo(facilityId);
				if (emissionsReportInfo.size() == 0) {
					anticipatedRptInfo = emissionsReportService
							.getCurrentReportingInfo(f2);
					currentRptInfo = anticipatedRptInfo;
					defaultCategoryYear = yr;
				} else if (emissionsReportInfo.get(0).getYear().intValue() == yr) {
					currentRptInfo = emissionsReportInfo.get(0);
					if (emissionsReportInfo.size() > 1)
						previousRptInfo = emissionsReportInfo.get(1);
				} else { // current year not set
					previousRptInfo = emissionsReportInfo.get(0);
					// make category the same as the last recorded one.
					anticipatedRptInfo = new EmissionsRptInfo();
					anticipatedRptInfo.setYear(yr);
					// anticipatedRptInfo.setCategory(previousRptInfo
					// .getCategory());
					anticipatedRptInfo.setContentTypeCd(previousRptInfo
							.getContentTypeCd());
					anticipatedRptInfo
							.setRegulatoryRequirementCd(previousRptInfo
									.getRegulatoryRequirementCd());
					currentRptInfo = anticipatedRptInfo;
					defaultCategoryYear = yr;
				}
				rptInfoWrapper = new TableSorter();
				rptInfoWrapper.setWrappedData(emissionsReportInfo);

				// Determine years that can be added.
				categoryYears = new ArrayList<SelectItem>();
				if (null != defaultCategoryYear) {
					categoryYears.add(new SelectItem(defaultCategoryYear));
					yr--;
				}
				int lastYear = 2013; // Earliest reporting year is 2013

				while (yr >= lastYear) {

					categoryYears.add(new SelectItem(new Integer(yr)));
					yr--;
				}
			} catch (RemoteException re) {
				handleException(reportId, re);
			}
		}
	}

	public final EmissionsRptInfo getPreviousRptInfo() {
		// if(!isInternalApp()) { // TODO fix when portal side done
		// previousRptInfo = new EmissionsRptInfo();
		// previousRptInfo.setYear(new Integer(2009));
		// previousRptInfo.setCategory(EmissionReportsDef.TV);
		// previousRptInfo.setState(ReportReceivedStatusDef.REPORT_NOT_REQUESTED);
		// } else {
		// Get report for the year two years prior to the current year.
		getYearlyReportingInfo();
		// }
		return previousRptInfo;
	}

	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final TableSorter getRptInfoWrapper() {
		getYearlyReportingInfo();
		return rptInfoWrapper;
	}

	public final void addEmissionsRptInfo() {
		if (null == emissionsReportInfo) {
			emissionsReportInfo = new ArrayList<EmissionsRptInfo>();
		}
		reportingYear = new EmissionsRptInfo();
		reportingYear.setState(ReportReceivedStatusDef.REPORT_NOT_REQUESTED);
		reportingYear.setReportingEnabled(true);
		if (null != anticipatedRptInfo) {
			boolean seenYear = false;
			for (EmissionsRptInfo eri : emissionsReportInfo) {
				if (eri.getYear().equals(anticipatedRptInfo.getYear())) {
					seenYear = true;
					break;
				}
			}
			if (!seenYear) {
				reportingYear.setYear(anticipatedRptInfo.getYear());
			}
		}
	}

	public final String editYearlyInfo() {
		setYearlyInfoEditable(true);
		if (!getAllowedToDeleteEmissionsRptInfo()){
			if (isStars2Admin()){
				DisplayUtil.displayWarning("Please note that there are existing Emissions Inventory report associated with it.");
			} else{
				DisplayUtil.displayWarning("Content Type and Regulatory Requirement cannot be changed since there are existing Emissions Inventory report associated with it.");
			}
		}
		return null;
	}

	public String saveYearlyInfo() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		String ret = null;
		try {
			ret = saveYearlyInfoInternal();
		} finally {
			clearButtonClicked();
		}
		return ret;
	}

	private final String saveYearlyInfoInternal() {
		ok = true;

		if (ok) {

			try {
				Integer id = getInfrastructureService()
						.retrieveSCEmissionsReportId(reportingYear.getYear(),
								reportingYear.getContentTypeCd(),
								reportingYear.getRegulatoryRequirementCd());
				if (id == null) {
					DisplayUtil
							.displayError("No service catalog detail found.");
					ok = false;
				} else {
					reportingYear.setScEmissionsReportId(id);
				}
			} catch (RemoteException re) {
				handleException(re);
				ok = false;
			}

			if (ok && reportingYear != null && reportingYear.isNewObject()) {
				emissionsReportInfo.add(reportingYear);
				// no longer a new object
				reportingYear.setNewObject(false);
			}

			if (ok) {
				ValidationMessage[] validationMessages = EmissionsRptInfo
						.validate(emissionsReportInfo);
				ok = !displayValidationMessages("", validationMessages); // TODO?
																			// Not
																			// flagging
																			// exact
																			// attribute
			}
			if (ok) {
				setYearlyInfoEditable(false);
				
				// Make sure that the value of 'Reporting Enabled' is the same
				// for all rows with the same combination of Year and Content
				// Type.
				for (EmissionsRptInfo eri : emissionsReportInfo) {
					boolean isMatch = reportingYear
							.isYearAndContentTypeMatch(eri);
					if (isMatch) {
						eri.setReportingEnabled(reportingYear
								.getReportingEnabled());
					}
				}
				
				try {
					emissionsReportService.updateYearlyReportingInfo(
							facilityId, emissionsReportInfo);
				} catch (RemoteException re) {
					handleException(re);
					ok = false;
				}
				savedFpId = null; // make sure data is reloaded.
				if (ok) {
					FacesUtil.returnFromDialogAndRefresh();
					DisplayUtil
							.displayInfo("Enabled Emissions Inventories updated successfully");
				} else {
					DisplayUtil.displayError("Failed to update information");
				}
			}
		}

		return null;
	}

	public final String startAddReportingYear() {
		emissionsRptInfoRow = null;
		addEmissionsRptInfo();
		reportingYear.setNewObject(true);
		setYearlyInfoEditable(true);
		return "dialog:yearlyInfoDetail";
	}

	public final String startEditReportingYear() {
		if (emissionsRptInfoRow == null) {
			return startAddReportingYear();
		} else {
			emissionsRptInfoRow.setNewObject(false);
			reportingYear = emissionsRptInfoRow;
			setYearlyInfoEditable(false);
		}
		return "dialog:yearlyInfoDetail";
	}

	public final String closeReportingYearDialog() {
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public final String editViewYearlyComment() {
		justificationStr = yearlyComment;
		if (null == emissionsRptInfoRow.getComment()
				|| emissionsRptInfoRow.getComment().length() == 0) {
			tsJust = new String();
		} else
			tsJust = emissionsRptInfoRow.getComment();
		return "dialog:viewEditYearyComment";
	}

	public final String cancelYearlyInfo() {
		setYearlyInfoEditable(false);
		savedFpId = null; // To restore original values.
		return closeReportingYearDialog();
	}

	public final String startDeleteReport() {
		if (report.getInspectionsReferencedIn().size() > 0) {
			deleteDisabled = true;
			deleteDisabledMsg = "You cannot delete this Emissions Inventory while it is being referenced in Inspection Report(s).";
		} else {
			deleteDisabled = false;
			deleteDisabledMsg = "";
		}
		return "dialog:deleteReportReq";
	}

	public boolean getDisplayReferencedInspectionsTable() {
		return isDeleteDisabled() && !getReport().getInspectionsReferencedIn().isEmpty();
	}
	
	public boolean deleteReportI() {
		ok = true;
		try {
			emissionsReportService.deleteEmissionsReport(report);
		} catch (RemoteException re) {
			handleException(report.getEmissionsRptId(), re);
			ok = false;
		}
		return ok;
	}

	public String associateFP() {
		showHelp = false;
		FacilityVersion[] facilities = null;
		ok = true;
		try {
			facilities = facilityService
					.retrieveAllFacilityVersions(facilityId);
		} catch (RemoteException re) {
			handleException(reportId, re);
			ok = false;
		}
		if (ok) {
			facilityHistory.setWrappedData(facilities);
			return "dialog:associateProfile";
		}

		return null;
	}

	public final String getGrpName() {
		return grpName;
	}

	public final void setGrpName(String grpName) {
		this.grpName = grpName;
	}

	public final ArrayList<EmissionsReportSearch> getReportsSameYear() {
		return reportsSameYear;
	}

	public final EmissionsReport getTreeReport() {
		return treeReport;
	}

	public final boolean isFacilityEditable() {
		return facilityEditable;
	}

	public final TableSorter getPeriodMaterialWrapper() {
		return periodMaterialWrapper;
	}

	public final List<EmissionsVariable> getPeriodVariableWrapper() {
		return periodVariableWrapper;
	}

	public int getVariableSize() {
		return periodVariableWrapper.size();
	}

	public int getMaterialSize() {
		return periodMaterialList.size();
	}

	public final EmissionsRptInfo getAnticipatedRptInfo() {
		getYearlyReportingInfo();
		return anticipatedRptInfo;
	}

	public final void setAnticipatedRptInfo(EmissionsRptInfo anticipatedRptInfo) {
		this.anticipatedRptInfo = anticipatedRptInfo;
	}

	public final ArrayList<SelectItem> getCategoryYears() {
		getYearlyReportingInfo();
		/*
		 * categoryYears = saveCategoryYears; // Remove any years that have been
		 * added. for (EmissionsRptInfo i : emissionsReportInfo) {
		 * if(!i.isNewObject()) continue; for(SelectItem s : categoryYears) {
		 * if(i.getYear().equals(s.getValue())) { categoryYears.remove(s); } } }
		 */
		return categoryYears;
	}

	public final boolean isCategoryYearsLeft() {
		if (this.categoryYears == null) {
			return false;
		}

		if (this.categoryYears.size() == 0) {
			return false;
		}
		return true;
	}

	public final void setSavedFpId(Integer savedFpId) {
		this.savedFpId = savedFpId;
	}

	public final boolean isEditableM() {
		return editableM;
	}

	public boolean isDisallowClick() {
		return isEditableM() || isEditable() || isUpdatingInvoiceInfo();

	}

	public final void setEditableM(boolean editableM) {
		this.editableM = editableM;
	}

	// public ArrayList<SelectItem> getActiveMaterials() {
	// return activeMaterials;
	// }

	public final boolean isAnyEditable() {
		// This only needs check editable because only
		// used on report level page.
		return this.isEditable();
	}

	public String getJustificationStr() {
		return justificationStr;
	}

	public EmissionRow getSecretEmissionRow() {
		return secretEmissionRow;
	}

	public void setSecretEmissionRow(EmissionRow secretEmissionRow) {
		this.secretEmissionRow = secretEmissionRow;
	}

	public String getTsJust() {
		return tsJust;
	}

	public void setTsJust(String tsJust) {
		this.tsJust = tsJust;
	}

	public void setSecretMaterialRow(
			EmissionsMaterialActionUnits secretMaterialRow) {
		this.secretMaterialRow = secretMaterialRow;
	}

	public EmissionsMaterialActionUnits getSecretMaterialRow() {
		return secretMaterialRow;
	}

	public void setEmissionRowMethod(EmissionRow emissionRowMethod) {
		this.emissionRowMethod = emissionRowMethod;
		// Set from jsp
		int yr;
		if (emissionRowMethod.isFromComparisonRpt()) {
			yr = compareReport.getReportYear();
		} else {
			yr = report.getReportYear();
		}
		String materCd = null;
		if (!doRptCompare) {
			materCd = emissionsPeriod.getCurrentMaterial();
		} else {
			if (emissionRowMethod.isFromComparisonRpt()) {
				materCd = emissionsPeriod.getComp().getCurrentMaterial();
				;
			} else {
				materCd = emissionsPeriod.getOrig().getCurrentMaterial();
			}
		}

		rowFireTable = FireRow.computeFireChoices(emissionRowMethod, materCd,
				periodFireRows, yr);
		setFireEditable(false);
		if (rowFireTable != null && rowFireTable.size() > 1
				&& this.emissionRowMethod != null) {

			editFire();
		}

		fireWrapper = new TableSorter();
		fireWrapper.setWrappedData(rowFireTable);
	}

	public EmissionsRptInfo getEmissionsRptInfoRow() {
		return emissionsRptInfoRow;
	}

	public void setEmissionsRptInfoRow(EmissionsRptInfo emissionsRptInfoRow) {
		this.emissionsRptInfoRow = emissionsRptInfoRow;
	}

	public String getSccCodeStrLine() {
		return sccCodeStrLine;
	}

	public EmissionRow getEmissionRowMethod() {
		return emissionRowMethod;
	}

	public TableSorter getCalcWrapper() {
		// Initialize for calcTable.jsp
		// Locate a representative EU and the facility process
		if (savedCalcEmissionRow == calcEmissionRow) {
			return calcWrapper;
		}
		calcWrapperComputeError = null;
		savedCalcEmissionRow = calcEmissionRow;
		Integer euId = null;
		EmissionsReportEU repEu;
		EmissionsReport tempRpt;
		EmissionsReportPeriod tempPeriod;
		Facility tempFacility;
		if (!doRptCompare) {
			tempRpt = treeReport;
			tempPeriod = emissionsPeriod;
			tempFacility = facility;
		} else {
			if (calcEmissionRow.isFromComparisonRpt()) {
				tempRpt = treeReport.getComp();
				tempPeriod = emissionsPeriod.getComp();
				tempFacility = compareFacility;
			} else {
				tempRpt = treeReport.getOrig();
				tempPeriod = emissionsPeriod.getOrig();
				tempFacility = facility;
			}
		}
		if (tempPeriod.isNotInFacility()) {
			calcWrapperComputeError = "Calculations not performed because process is not in facility inventory.  See caution in inventory tree.";
			return null;
		}
		repEu = tempRpt.findEU(tempPeriod);
		if (null == repEu) { // look in groups
			EmissionsReportEUGroup euG = tempRpt.findEuG(tempPeriod);
			if (euG.isNotInFacility()) {
				calcWrapperComputeError = "Calculations not performed because group contains emissions units not in facility inventory.  See caution in inventory tree.";
				return null;
			}
			if (!euG.getEus().isEmpty()) {
				euId = euG.getEus().get(0);
			}
		} else {
			if (repEu.isNotInFacility()) {
				calcWrapperComputeError = "Calculations not performed because emissions units not in facility inventory.  See caution in inventory tree.";
				return null;
			}
			euId = repEu.getCorrEpaEmuId();
		}

		EmissionProcess p = null;
		EmissionUnit e = null;
		if (null != euId) {
			e = tempFacility.getMatchingEmissionUnit(euId);
			if (null != e) {
				p = e.findProcess(tempPeriod.getSccId());
			}
		}
		try {
			rowControlInfoTable = ControlInfoRow.generateControlMatrix(
					facility, p, tempPeriod, calcEmissionRow, false, false);
			if (ControlInfoRow.isProblems()) {
				String s2 = ControlInfoRow.getProblems().toString()
						+ " for report " + report.getEmissionsInventoryId()
						+ " and emission unit " + e.getEpaEmuId() + " and scc "
						+ p.getSccId() + ", pollutantCd "
						+ calcEmissionRow.getPollutantCd();
				DisplayUtil.displayError(s2);
			}
			calcStackEmissions = BaseDB.numberToString(ControlInfoRow
					.stackTotal(rowControlInfoTable)
					* ControlInfoRow.startingValue);
			calcFugitiveEmissions = BaseDB.numberToString(ControlInfoRow
					.fugitiveTotal(rowControlInfoTable)
					* ControlInfoRow.startingValue);
		} catch (Exception ex) {
			rowControlInfoTable = new ArrayList<ControlInfoRow>();
			handleException(tempRpt.getEmissionsRptId(), ex);
		}
		calcWrapper = new TableSorter();
		calcWrapper.setWrappedData(rowControlInfoTable);
		return calcWrapper;
	}

	public TableSorter getMethodBasedCalcWrapper() {
		TableSorter calcWrapper = null;
		if (getCalcEmissionRow().getEmissionCalcMethodCd() != null) {
			int calcMethodCd = Integer.parseInt(getCalcEmissionRow()
					.getEmissionCalcMethodCd());

			if (calcMethodCd < 100) { // time-based method
				calcWrapper = filterControlEfficiency(getCalcWrapper());
			} else {
				calcWrapper = getCalcWrapper();
			}
		} else {
			calcWrapper = getCalcWrapper();
		}
		return calcWrapper;
	}

	private TableSorter filterControlEfficiency(TableSorter calcWrapper) {
		String previousStreamLabel = null;
		Double previousOutput = null;
		Double stackTotal = 0d;
		for (Object data : calcWrapper.getData()) {
			ControlInfoRow row = (ControlInfoRow) data;
			if (null != previousStreamLabel
					&& row.getStreamLabel().startsWith(previousStreamLabel)) {
				row.setInput(previousOutput);
			}
			double input = row.getInput();
			double fugitive = row.getFugitiveFraction();
			double output = input - fugitive;
			row.setOutput(output);
			if (FacilityEmissionFlow.STACK_TYPE.equals(row.getType())
					&& row.getStackFraction() != output) {
				row.setStackFraction(output);
			}
			stackTotal += row.getStackFraction();
			previousStreamLabel = row.getStreamLabel();
			previousOutput = output;
		}
		this.calcStackEmissions = BaseDB.numberToString(stackTotal);
		return calcWrapper;
	}

	public EmissionRow getCalcEmissionRow() {
		return calcEmissionRow;
	}

	public void setCalcEmissionRow(EmissionRow calcEmissionRow) {
		this.calcEmissionRow = calcEmissionRow;
	}

	public TableSorter getPeriodMaterialCompWrapper() {
		return periodMaterialCompWrapper;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

	public String submitVerify() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			passedValidation = internalSubmitVerify();
			if ((report.getRptReceivedStatusDate() == null || openedForEdit)
					&& (report.isValidated() != passedValidation)) {
				getEmissionsReportService().setValidatedFlag(report,
						passedValidation);
			}
		} catch (RemoteException re) {
			handleException(report.getEmissionsRptId(), re);
			DisplayUtil.displayError("Failed to update report validated flag.");
		} finally {
			clearButtonClicked();
		}
		boolean tsData = report.containsTradeSecretProcessData();
		boolean tsAttach = report.attachmentsContainTradeSecrets();
		if (passedValidation && (tsData || tsAttach)) {
			return "validationTradeSecretNotification";
		} else {
			return "emissionReportNoRedirect";
		}
	}

	protected final Boolean internalSubmitVerify() {
		// Do Facility Validate of the EUs: if not EG71
		ArrayList<Integer> euListBasic = new ArrayList<Integer>();
		ArrayList<Integer> euList = new ArrayList<Integer>();
		report.getReportEmuIds(facility, scReports, euListBasic, euList);
		// List<ValidationMessage> vml0 = new ArrayList<ValidationMessage>();
		// List<ValidationMessage> vml1 = new ArrayList<ValidationMessage>();
		List<ValidationMessage> vml2 = new ArrayList<ValidationMessage>();
		boolean fOk = true;
		try {
			// vml0 = FacilityValidation.validateBasicEmissionsReport(facility,
			// euListBasic);
			// vml1 = FacilityValidation.validateFERandESemissionReport(fpId,
			// euListBasic);
			// if(report.isRptEIS() && report.getReportYear() > 2007) {
			vml2 = FacilityValidation.validateEISemissionReport(report,
					facility, euListBasic, euList);
			// }
		} catch (ValidationException ve) {
			fOk = false;
			DisplayUtil
					.displayError("Failed to validate the facility--partial validation performed");
			handleException(reportId, ve);
		}
		ok = true;
		// First, save the report period emissions with all pollutants needed.
		// This means all provisioned pollutants and those from fire.
		// But only if the report has not yet been submitted.
		if (report.getRptReceivedStatusCd().equals(
				ReportReceivedStatusDef.NOT_FILED)) {
			try {
				ArrayList<EmissionRow> pE;
				// EU Group periods
				for (EmissionsReportEUGroup g : report.getEuGroups()) {
					periodFireRows = emissionsReportService.retrieveFireRows(
							report.getReportYear(), g.getPeriod());
					g.getPeriod().setFireRows(periodFireRows);
					pE = EmissionRow.getEmissions(report.getReportYear(),
							g.getPeriod(), scReports, false, false, null,
							logger);
					g.getPeriod().clearErrors();
					g.getPeriod().updateEmissions(facility, report, pE,
							periodFireRows);
				}

				// EU periods
				for (EmissionsReportEU eu : report.getEus()) {
					for (EmissionsReportPeriod p : eu.getPeriods()) {
						periodFireRows = emissionsReportService
								.retrieveFireRows(report.getReportYear(), p);
						p.setFireRows(periodFireRows);
						pE = EmissionRow.getEmissions(report.getReportYear(),
								p, scReports, false, false, null, logger);
						p.clearErrors();
						p.updateEmissions(facility, report, pE, periodFireRows);
					}
				}
				setReportFocus(); // Add pollutant totals
				emissionsReportService.modifyEmissionsReport(facility,
						scReports, report, null, null, openedForEdit);
			} catch (ApplicationException re) {
				DisplayUtil
						.displayError("Failed to validate emissions inventory "
								+ report.getEmissionsRptId()
								+ "--partial validation performed");
				handleException(reportId, re);
				ok = false;
				err = true;
			} catch (RemoteException re) {
				DisplayUtil
						.displayError("Failed to validate emissions inventory "
								+ report.getEmissionsRptId()
								+ "--partial validation performed");
				handleException(reportId, re);
				ok = false;
				err = true;
			}
			if (!ok) {
				DisplayUtil
						.displayError("Failed to save emissions inventory prior to validation");
			} else {
				try {
					report = emissionsReportService.retrieveEmissionsReport(
							report.getEmissionsRptId(), true);
					if (report == null) {
						ok = false;
						err = true;
					} else {
						byClickEmiUnitOrGrp = null;
						recreateTreeFromReport(treeNodeId(report));
					}
				} catch (RemoteException re) {
					handleException(reportId, re);
					ok = false;
					err = true;
				}
			}
		} // end update report
		if (ok) {
			List<ValidationMessage> validationMessages;
			List<ValidationMessage> billingErr = new ArrayList<ValidationMessage>();
			try {
				if (isBillable()) {
					billingErr = FacilityValidation
							.determineMissingBilling(facility, report.getReportYear());
					if (billingErr.size() > 0) {
						// Could not find billing contact appropriate for report
						// check for current billing contact.
						if (facility.getBillingContact() == null) {
							ValidationMessage m = billingErr.get(0);
							ValidationMessage vMsg = new ValidationMessage(
									"MissingCurrentBilling",
									"Facility:  There is no active Billing Contact",
									ValidationMessage.Severity.ERROR, "contact");
							vMsg.setReferenceID(m.getReferenceID()); // use existing
																		// reference
																		// ID
							billingErr.add(vMsg);
						}
					}
				}
			
				validationMessages = report.submitVerify(
						emissionsReportService, vml2.size() != 0);

				for (ValidationMessage vm : validationMessages) {
					vm.setReferenceID(ValidationBase.REPORT_TAG + ":"
							+ reportId + ":" + vm.getReferenceID() + ":"
							+ TV_REPORT);
				}
				validationMessages.addAll(billingErr);
				// validationMessages.addAll(vml0);
				// validationMessages.addAll(vml1);
				validationMessages.addAll(vml2);
				ValidationBase.removeDuplicates(validationMessages);
				ok = AppValidationMsg.validate(validationMessages, true);
				if (ok) {
					Object close_validation_dialog = FacesContext
							.getCurrentInstance().getExternalContext()
							.getSessionMap()
							.get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
					if (close_validation_dialog != null
							&& (validationMessages != null && validationMessages
									.isEmpty())) {
						FacesUtil
								.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
						FacesContext
								.getCurrentInstance()
								.getExternalContext()
								.getSessionMap()
								.remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
					}
				}
			} catch (ApplicationException re) {
				handleException(reportId, re);
				ok = false;
			} catch (RemoteException re) {
				handleException(reportId, re);
				ok = false;
			 }
		}
		return new Boolean(ok && fOk);
	}

	static void displayWarnings(List<String> lst) {
		if (lst != null) {
			for (String s : lst) {
				DisplayUtil.displayWarning(s);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.webcommon.ValidationBase#getValidationDlgReference
	 * ()
	 */
	public final String getValidationDlgReference() {
		if (selectedTreeNode == null) {
			return ValidationBase.REPORT_TAG + ":" + reportId + ":" + "report"
					+ ":" + FacesUtil.getCurrentPage();
		}

		Object object = ((Stars2TreeNode) selectedTreeNode).getUserObject();
		String ret;
		if (selectedTreeNode.getType().equals("report")) {
			ret = "report:report:" + treeNodeId((EmissionsReport) object);
		} else if (selectedTreeNode.getType().equals("emissionUnits")
				|| selectedTreeNode.getType().equals("emissionUnitsCaution")
				|| selectedTreeNode.getType().equals("emissionUnitsExcluded")
				|| selectedTreeNode.getType().equals("emissionUnitsDeleted")) {
			ret = "emissionUnits:" + treeNodeId((EmissionsReportEU) object);
		} else if (selectedTreeNode.getType().equals("emissionUnitGroups")
				|| selectedTreeNode.getType().equals(
						"emissionUnitGroupsCaution")
				|| selectedTreeNode.getType().equals(
						"emissionUnitGroupsDeleted")) {
			ret = "emissionUnitGroups:"
					+ treeNodeId((EmissionsReportEUGroup) object);
		} else if (selectedTreeNode.getType().equals("emissionPeriods")
				|| selectedTreeNode.getType().equals("emissionPeriodsCaution")
				|| selectedTreeNode.getType().equals("emissionPeriodsDeleted")) {
			ret = "emissionPeriods:"
					+ treeNodeId((EmissionsReportPeriod) object);
		} else if (selectedTreeNode.getType().equals("emissionGPeriods")
				|| selectedTreeNode.getType().equals("emissionGPeriodsCaution")
				|| selectedTreeNode.getType().equals("emissionGPeriodsDeleted")) {
			ret = "emissionPeriods:"
					+ treeNodeId((EmissionsReportPeriod) object);
		} else {
			this.logger.error("Unexpected type=" + selectedTreeNode.getType());
			ret = "report";
		}
		ret = ValidationBase.REPORT_TAG + ":" + reportId + ":" + ret + ":"
				+ FacesUtil.getCurrentPage();
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.webcommon.ValidationBase#validationDlgAction()
	 */
	public final String validationDlgAction() {
		// Also note that this called for validations or "Locate"--since it
		// works using validation.
		String rtn = super.validationDlgAction();
		if (null != rtn)
			return rtn;
		if (newValidationDlgReference == null
				|| newValidationDlgReference
						.equals(getValidationDlgReference())) {
			return null; // stay on same page
		}

		StringTokenizer st = new StringTokenizer(newValidationDlgReference, ":");

		String subsystem = st.nextToken();
		if (!subsystem.equals(ValidationBase.REPORT_TAG)) {
			this.logger.error("For reportId=" + reportId
					+ ": Report reference is in error: "
					+ newValidationDlgReference);
			DisplayUtil.displayError("Error processing validation message");
			return null;
		}
		String valId = st.nextToken();
		if (!valId.equals(reportId.toString())) {
			DisplayUtil
					.displayError("Validation message is for emissions inventory: "
							+ valId);
			return null;
		}

		this.setReportId(Integer.parseInt(valId));
		setReport(null);
		getReport(this);

		String refType = st.nextToken();

		String nodeID = st.nextToken();
		if (!refType.equals("period") && !refType.equals("group")
				&& !refType.equals("unit") && !refType.equals("report")) {
			DisplayUtil.displayError("bad node type selected in popup");
			return null; // stay on same page
		}

		if (treeNodeMap.get(nodeID) == null) {
			DisplayUtil.displayError("Cannot find node selected in popup");
			return null; // stay on same page
		}
		selectedTreeNode = treeNodeMap.get(nodeID);
		current = nodeID;
		nodeClicked();
		// return outCome for navigation to right page
		return st.nextToken();
	}

	public void applyEditNote(ActionEvent actionEvent) {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			applyEditNoteInternal(actionEvent);
		} finally {
			clearButtonClicked();
		}
	}

	private final void applyEditNoteInternal(ActionEvent actionEvent) {
		ok = true;

		// make sure note is provided
		if (reportNote.getNoteTxt() == null
				|| reportNote.getNoteTxt().trim().equals("")) {
			DisplayUtil.displayError("Attribute Note is not set.");
			return;
		}

		// TODO the following validate() just creates empty array. Do we need
		// anything???
		ValidationMessage[] validationMessages = reportNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}

		try {
			if (!noteModify) {
				emissionsReportService.createNote(reportNote);
			} else {
				emissionsReportService.modifyNote(reportNote);
			}
			reportNote = null;

			// retrieve added/updated notes
			reloadNotes();
		} catch (RemoteException re) {
			handleException(reportId, re);
			ok = false;
			setErr(true);
		}

		if (ok) {
			DisplayUtil
					.displayInfo("Emissions inventory note added/updated successfully");
			FacesUtil.returnFromDialogAndRefresh();
		} else {
			cancelEditNote();
			DisplayUtil
					.displayError("Adding/Updating emissions inventory note failed");
		}
	}

	public SccCode getCompareScc() {
		return compareScc;
	}

	public boolean isMaterialSelected() {
		return materialSelected;
	}

	public EmissionsReport getCompareReport() {
		return compareReport;
	}

	public String initEnableYear() {
		Calendar c = Calendar.getInstance();
		enableYear = c.get(Calendar.YEAR) - 1; // active reporting for last year
		missingFactorsYear = enableYear;
		enableAllRptDef = false;
		enableAllFIRE = false;
		enableAllSCC = false;
        refreshSCEmissionsReports();
		return "EREnable";
	}

	public Integer getEnableYear() {
		// Set in function initEnableYear (called from admin-config.xml)
		return enableYear;
	}

	public TableSorter getFacilityHistory() {
		return facilityHistory;
	}

	public Integer getCreate_year() {
		return create_year;
	}

	public void setCreate_year(Integer create_year) {
		this.create_year = create_year;
		this.invalidYear = false;
		// Check category
		copyFromExistingRpt = false;
	}
	
	public String getCreateContentTypeLabel() {
		return ContentTypeDef.getData().getItems()
				.getItemDesc(createContentTypeCd);
	}

	public String locatePollutant() {
		TreeSet<PollutantRow> euPollTree = new TreeSet<PollutantRow>();
		TreeSet<PollutantRow> processPollTree = new TreeSet<PollutantRow>();
		report.locatePollutant(emissionRowPollutant.getPollutantCd(),
				emissionRowPollutant.getEmissionsUnitNumerator(), euPollTree,
				processPollTree);
		euPoll = euPollTree.toArray(new PollutantRow[0]);
		processPoll = processPollTree.toArray(new PollutantRow[0]);
		int numRows = euPoll.length + processPoll.length;
		int len = 310 + numRows * 20;
		if (len > 950)
			len = 950;
		FacesUtil.startModelessDialog("../reports/locatePollutant.jsf", len,
				1120);
		return null;
	}

	public Integer getSelectedReportId() {
		return selectedReportId;
	}

	public void setSelectedReportId(Integer selectedReportId) {
		this.selectedReportId = selectedReportId;
	}

	public Timestamp getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Timestamp invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public ArrayList<EmissionsRptInfo> getYearlyEmissionsReportInfo(Integer fpId) {
		savedFpId = null;
		this.fpId = fpId;
		getYearlyReportingInfo();
		return emissionsReportInfo;
	}

	public EmissionsRptInfo getRptInfo() {
		return rptInfo;
	}

	public TableSorter getPeriodVariableCompWrapper() {
		return periodVariableCompWrapper;
	}

	public EmissionsVariable getSecretVariableRow() {
		return secretVariableRow;
	}

	public void setSecretVariableRow(EmissionsVariable secretVariableRow) {
		this.secretVariableRow = secretVariableRow;
	}

	public boolean isReportLevelOnly() {
		return reportLevelOnly;
	}

	public void setReportLevelOnly(boolean reportLevelOnly) {
		this.reportLevelOnly = reportLevelOnly;
	}

	public boolean isDiscloseEmissions() {
		return discloseEmissions;
	}

	public boolean isPrintTradeSecret() {
		boolean tsData = report.containsTradeSecretProcessData();
		boolean tsAttach = report
				.attachmentsContainTradeSecrets(includeAllAttachments);
		return (tsData || tsAttach);
	}

	// 'View What You are About to Submit' Button in the portal
	public String prepareWhatYouSubmit() {
		/*
		 * This was changed to look just the way the similar function in
		 * ApplicationDetailCommon does. However, somehow, when the call to
		 * facilityBO().generateTempFacilityProfileReport results in none of the
		 * backing bean variable set after this being retained. It is like a new
		 * instance of the backing bean gets created and then discarded by the
		 * time any calls from the popup page are made. By changing
		 * generateTempFacilityProfileReport to do nothing (comment out its
		 * code) and adjusting so having the results null is OK, then everything
		 * works fine (just like in the case of Applications.
		 */
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			reportDocuments = null; // cause regeneration
			reportAttachments = null; // cause regeneration
			setIncludeAllAttachments(true);
			setHideTradeSecret(!isPrintTradeSecret());
		} finally {
			clearButtonClicked();
		}
		return "dialog:emissionReport";
	}

	public String printEmissionReport() {
		/*
		 * This was changed to look just the way the similar function in
		 * ApplicationDetailCommon does. However, somehow, when the call to
		 * facilityBO().generateTempFacilityProfileReport results in none of the
		 * backing bean variable set after this being retained. It is like a new
		 * instance of the backing bean gets created and then discarded by the
		 * time any calls from the popup page are made. By changing
		 * generateTempFacilityProfileReport to do nothing (comment out its
		 * code) and adjusting so having the results null is OK, then everything
		 * works fine (just like in the case of Applications.
		 */
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		try {
			reportDocuments = null; // cause regeneration
			reportAttachments = null; // cause regeneration
			setIncludeAllAttachments(false);

			// Since no trade secrets, give warning instead;
			if (!isHideTradeSecret() && !isPrintTradeSecret()) {
				DisplayUtil
						.displayWarning("There is no trade secret information in the Emissions Inventory or file attachments.  Use Download/Print button instead.");
				return null;
			}
		} finally {
			clearButtonClicked();
		}
		return "dialog:emissionReport";
	}

	public List<Document> getReportDocuments() {

		if (reportDocuments != null)
			return reportDocuments;
		String user = getUserName();
		reportDoc = new TmpDocument();
		reportSummaryDoc = new TmpDocument();
		try {
			facilityDoc = facilityService.generateTempFacilityProfileReport(
					facility, null);
			facilityDoc.setDescription("Printable View of Facility Inventory");
		} catch (RemoteException re) {
			logger.error(
					"generateTempFacilityProfileReport() failed for report "
							+ treeReport.getEmissionsRptId(), re);
			DisplayUtil
					.displayError("Unable to generate facility inventory document");
		}

		try {
			reportDocuments = emissionsReportService.getPrintableDocumentList(
					facility, facilityDoc, reportDoc, reportSummaryDoc,
					treeReport, scReports, user, hideTradeSecret, inStaging,
					includeAllAttachments);
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Unable to generate emissions inventory documents");
			handleException(reportId, re);
		}
		//
		// if (facilityDoc != null) {
		// logger.debug(" Finished generateTempFacilityProfileReport. description = "
		// + facilityDoc.getDescription());
		// reportDocuments.add(facilityDoc);
		// }
		return reportDocuments;
	}

	public List<Document> getReportAttachments() {
		if (reportAttachments != null)
			return reportAttachments;
		try {
			reportAttachments = emissionsReportService
					.getPrintableAttachmentList(facilityDoc, reportDoc,
							reportSummaryDoc, report.getAttachments(),
							hideTradeSecret, inStaging,
							report.getRptReceivedStatusDate(),
							includeAllAttachments);
		} catch (RemoteException re) {
			logger.error("getPrintableAttachmentList() failed for report "
					+ report.getEmissionsRptId(), re);
			DisplayUtil
					.displayError("Unable to generate emissions inventory attachments");
		}
		if (reportAttachments == null || reportAttachments.size() == 0)
			reportAttachments = null;
		return reportAttachments;
	}

	public String getDisplayTotal() {
		return displayTotal;
	}

	public ArrayList<EmissionsReportSearch> getSelectRptList() {
		return selectRptList;
	}

	public boolean isEnableRptCopy() {
		return enableRptCopy;
	}

	public boolean isEnableAllFIRE() {
		return enableAllFIRE;
	}

	public void setEnableAllFIRE(boolean enableAllFIRE) {
		this.enableAllFIRE = enableAllFIRE;
	}

	public boolean isEnableAllRptDef() {
		return enableAllRptDef;
	}

	public void setEnableAllRptDef(boolean enableAllRptDef) {
		this.enableAllRptDef = enableAllRptDef;
	}

	public boolean isEnableAllSCC() {
		return enableAllSCC;
	}

	public void setEnableAllSCC(boolean enableAllSCC) {
		this.enableAllSCC = enableAllSCC;
	}

	public String getCalcWrapperComputeError() {
		return calcWrapperComputeError;
	}

	public boolean isEmissionsNeedClearing() {
		return emissionsNeedClearing;
	}

	public EmissionsMaterialActionUnits getMaterialRow() {
		return materialRow;
	}

	public void setMaterialRow(EmissionsMaterialActionUnits materialRow) {
		if (this.materialRow == null) {
			for (EmissionsMaterialActionUnits mat : periodMaterialList) {
				mat.setBelongs(true);
				//if (!mat.getMaterial().equals(materialRow.getMaterial())) {
				//	mat.setNotActive(true);
				//}
			}
			this.materialRow = materialRow;
		}
		this.tmpMaterialRow = materialRow;
	}

	public final String expandExp() {
		expandedExp = true;
		return TV_REPORT;
	}

	public final String collapseExp() {
		expandedExp = false;
		return TV_REPORT;
	}

	public final String expandExpMore() {
		expRows = expRows * 2;
		if (expRows > 17) {
			expRows = 17;
		}
		return TV_REPORT;
	}

	public boolean isExpandedExp() {
		return expandedExp;
	}

	public int getExpRows() {
		return expRows;
	}

	public void setExpandedExp(boolean expandedExp) {
		this.expandedExp = expandedExp;
		if (!expandedExp) {
			expRows = 2;
		}
	}

	public String getCalcFugitiveEmissions() {
		return calcFugitiveEmissions;
	}

	public String getCalcStackEmissions() {
		return calcStackEmissions;
	}

	public ArrayList<ProcessGroupChoice> getProcessGroupTable() {
		return processGroupTable;
	}

	public ProcessGroupChoice getFirstGrpProcess() {
		return firstGrpProcess;
	}

	public void setFirstGrpProcess(ProcessGroupChoice firstGrpProcess) {
		this.firstGrpProcess = firstGrpProcess;
	}

	public boolean isRemoveGroup() {
		return removeGroup;
	}

	public void setRemoveGroup(boolean removeGroup) {
		this.removeGroup = removeGroup;
	}

	public ArrayList<EuEg71Choice> getEuEg71Table() {
		return euEg71Table;
	}

	public boolean isCopyFromExistingRpt() {
		return copyFromExistingRpt;
	}

	public void setCopyFromExistingRpt(boolean copyFromExistingRpt) {
		this.copyFromExistingRpt = copyFromExistingRpt;
	}

	public String getCreateReviseMsg() {
		return createReviseMsg;
	}

	public boolean isCreateRptUsingProfile() {
		return createRptUsingProfile;
	}

	public boolean isCreatingRevisedRpt() {
		return creatingRevisedRpt;
	}

	public PollutantRow[] getEuPoll() {
		return euPoll;
	}

	public PollutantRow[] getProcessPoll() {
		return processPoll;
	}

	public EmissionRow getEmissionRowPollutant() {
		return emissionRowPollutant;
	}

	public void setEmissionRowPollutant(EmissionRow emissionRowPollutant) {
		this.emissionRowPollutant = emissionRowPollutant;
	}

	public PollutantRow getPollutantRow() {
		return pollutantRow;
	}

	public void setPollutantRow(PollutantRow pollutantRow) {
		this.pollutantRow = pollutantRow;
	}

	public Integer getEuEmissionChoice() {
		return euEmissionChoice;
	}

	public void setEuEmissionChoice(Integer euEmissionChoice) {
		emissionsNeedClearing = false;
		if (euEmissionChoice != 2 && emissionUnit.getEmissionChoice() == 2) {
			emissionsNeedClearing = true;
		}
		this.euEmissionChoice = euEmissionChoice;
	}

	public Integer getRptEmissionChoice() {
		return rptEmissionChoice;
	}

	public void setRptEmissionChoice(Integer rptEmissionChoice) {
		this.rptEmissionChoice = rptEmissionChoice;
		// generate euEg71Table and haveEmissions tables.
		int prevVal = 2;
		if (report.zeroEmissions()) {
			prevVal = 0;
		}
		haveEmissions = new ArrayList<EuEg71Choice>();
		euEg71Table = new ArrayList<EuEg71Choice>(200);
		if (rptEmissionChoice != prevVal) {
			// process reporting level changes
			for (EmissionsReportEU eu : report.getEus()) {
				EuEg71Choice ee71c = new EuEg71Choice(report, eu, scReports);
				if (rptEmissionChoice == 0) {
					// Mark all as did not operate
					ee71c.setEg71Zero(0);
					ee71c.setInclude(false);
					if (ee71c.isDeleteEmissions()) {
						haveEmissions.add(ee71c);
					}
				} else {
					// if not de Minimis, mark as detailed reporting
					if (!ee71c.isDeMinimis()) {
						ee71c.setEg71Zero(null);
						ee71c.setInclude(true);
					} else {
						ee71c.setEg71Zero(1);
						ee71c.setInclude(false);
					}
				}
				euEg71Table.add(ee71c);
			}
		}
	}

	public Integer getCompareRptEmissionChoice() {
		return compareRptEmissionChoice;
	}

	public ArrayList<EuEg71Choice> getHaveEmissions() {
		return haveEmissions;
	}

	public TableSorter getEmissionPeriodWrapperHAP() {
		return emissionPeriodWrapperHAP;
	}

	public String getHapNonAttestationMsg() {
		return hapNonAttestationMsg;
	}

	public boolean isHapTable() {
		return hapTable;
	}

	public TableSorter getEmissionEuWrapperHAP() {
		return emissionEuWrapperHAP;
	}

	public TableSorter getEmissionGroupWrapperHAP() {
		return emissionGroupWrapperHAP;
	}

	public TableSorter getEmissionReportWrapperHAP() {
		return emissionReportWrapperHAP;
	}

	public int getDisplayHapRows() {
		return displayHapRows;
	}

	public int getNumHapRows() {
		return numHapRows;
	}

	public void displaySomeRows() {
		displayHapRows = 0;
		if (didPositionToLast && addedRow) {
			// emissionPeriodWrapperHAP.positionToLastBlock(displayHapRows);
		}
		showDisplayAll = true;
		showDisplaySome = false;
	}

	public void displayAllRows() {
		displayHapRows = 0;
		if (didPositionToLast && addedRow) {
			// emissionPeriodWrapperHAP.positionToLastBlock(displayHapRows);
		}
		showDisplayAll = false;
		showDisplaySome = true;
	}

	public boolean isShowDisplayAll() {
		return showDisplayAll && numHapRows > 8;
	}

	public boolean isShowDisplaySome() {
		return showDisplaySome && numHapRows > 8;
	}

	public EmissionsReportPeriod getSavedWebPeriod() {
		return savedWebPeriod;
	}

	public boolean isShowHelp() {
		return showHelp;
	}

	public void setShowHelp(boolean showHelp) {
		this.showHelp = showHelp;
	}

	public boolean isSccDepreciated() {
		return webPeriod.getSccId() != null
				&& webPeriod.getSccCode().isDeprecated();
	}

	public boolean isNoMaterialDefined() {
		return webPeriod.getSccId() != null
				&& !webPeriod.getSccCode().isDeprecated()
				&& periodMaterialList.size() == 0;
	}

	public boolean isNoRemainingFireRows() {
		return webPeriod.getSccId() != null
				&& !webPeriod.getSccCode().isDeprecated()
				&& periodMaterialList.size() == 1
				&& periodMaterialList.get(0).isBelongsNotActvie();
	}

	public boolean isMustSelectDiffMaterial() {
		if (webPeriod.getSccId() != null
				&& !webPeriod.getSccCode().isDeprecated()
				&& periodMaterialList.size() > 1) {
			for (EmissionsMaterialActionUnits emau : periodMaterialList) {
				if (emau.isBelongsNotActvie()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isNothingWrong() {
		return !isSccDepreciated() && !isNoMaterialDefined()
				&& !isNoRemainingFireRows() && !isMustSelectDiffMaterial();
	}

	public boolean isNothingWrong2() {
		return !isSccDepreciated() && !isNoMaterialDefined()
				&& !isNoRemainingFireRows();
	}

	// From emissionUnitRep jsp page
	public void setForceDetailedReporting(boolean b) {
		emissionUnit.setForceDetailedReporting(b);
		if (b) {
			emissionUnit.setEmissionChoice(new Integer(2));
			euEmissionChoice = new Integer(2);
		} else {
			if (scReports.getSc().isBelowRequirements(emissionUnit)) {
				emissionUnit.setEmissionChoice(new Integer(1));
				euEmissionChoice = new Integer(1);
			}
		}
	}

	// rom emissionUnitRep jsp page
	public boolean isForceDetailedReporting() {
		return emissionUnit.isForceDetailedReporting();
	}

	public boolean isInvalidYear() {
		return invalidYear;
	}

	public void setInvalidYear(boolean invalidYear) {
		this.invalidYear = invalidYear;
	}

	public boolean isGeneralUser() {
		boolean ret = false;

		if (isInternalApp()) {
			ret = InfrastructureDefs.getCurrentUserAttrs()
					.isCurrentUseCaseValid("generalUser");
		}

		return ret;
	}

	public EmissionsDocument getDoc() {
		return doc;
	}

	public void setDoc(EmissionsDocument doc) {
		this.doc = doc;
	}

	public int getNumberEnabledYears() {
		if (reportYears != null) {
			return reportYears.size();
		} else {
			return 0;
		}
	}

	public boolean isFeesSame() {
		Fee[] f = scReports.getSc().getFees();
		if ((null == f[0].getAmount() || null == f[1].getAmount())
				|| !f[0].getAmount().equals(f[1].getAmount()))
			return false;
		else
			return true;
	}

	public EmissionsRptInfo getReportingYear() {
		return reportingYear;
	}

	public void setReportingYear(EmissionsRptInfo reportingYear) {
		this.reportingYear = reportingYear;
	}

	public final boolean isIncludeAllAttachments() {
		return includeAllAttachments;
	}

	public final void setIncludeAllAttachments(boolean includeAllAttachments) {
		this.includeAllAttachments = includeAllAttachments;
	}

	public final String generateInvoice() {
		TemplateDocument templateDoc = TemplateDocTypeDef
				.getTemplate(TemplateDocTypeDef.TV_FEE_INVOICE);
		String docURL = null;
		doc = new EmissionsDocument();

		try {
			if (templateDoc != null)
				docURL = getEmissionsReportService().generateTempDocument(this,
						getReport(), facility, templateDoc);
			doc.setGeneratedDocumentPath(docURL);

		} catch (Exception ex) {
			if (ex instanceof DAOException && ex.getCause() == null) {
				DisplayUtil.displayError(ex.getMessage());

			}
			DisplayUtil.displayError(ex.getMessage() + " "
					+ templateDoc.getPath());

		}
		return "dialog:openReportGenDoc";
	}

	public final void compareReportSelection() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			startReportCompare();
		} finally {
			clearButtonClicked();
		}
	}

	public TableSorter getExplainMethods() {
		explainMethods = new TableSorter();
		explainMethods.setWrappedData(EmissionCalcMethodDef.retrieveMethods());
		return explainMethods;
	}

	public void setExplainMethods(TableSorter explainMethods) {
		this.explainMethods = explainMethods;
	}

	public TableSorter getExplainRecommendedMethods() {
		if (explainRecommendedMethods == null) {
			logger.debug("Getting recommended methods");
			explainRecommendedMethods = new TableSorter();
			List<EmissionCalcMethod> recommendedMethods = null;
			if (doRptCompare) {
				if (emissionRowMethod.getReportYear().equals(
						report.getReportYear())) {
					recommendedMethods = getEmissionsReportService()
							.retrieveRecommendedMethods(emissionRowMethod,
									report, facility, emissionsPeriod.getOrig());
				} else if (emissionRowMethod.getReportYear().equals(
						compareReport.getReportYear())) {
					recommendedMethods = getEmissionsReportService()
							.retrieveRecommendedMethods(emissionRowMethod,
									compareReport, compareFacility,
									emissionsPeriod.getComp());
				}
			} else {
				recommendedMethods = getEmissionsReportService()
						.retrieveRecommendedMethods(emissionRowMethod, report,
								facility, emissionsPeriod);
			}
			explainRecommendedMethods.setWrappedData(recommendedMethods);
		}
		return explainRecommendedMethods;
	}

	public void setExplainRecommendedMethods(
			TableSorter explainRecommendedMethods) {
		this.explainRecommendedMethods = explainRecommendedMethods;
	}

	public String viewExplanation() {
		// reset recommended methods
		explainRecommendedMethods = null;
		getExplainRecommendedMethods();
		// if(getCalcEmissionRow().getEmissionCalcMethodCd()!=null){
		return "dialog:viewCalc";
		// }

		// return null;
	}

	private boolean validateEdit(boolean internalApp) {
		boolean isValid = true;
		String containerId = "";
		List<ValidationMessage> validationaMessages = new ArrayList<ValidationMessage>();
		validateEditEmissionInventory(internalApp, validationaMessages);

		if (validationaMessages.size() > 0) {
			displayValidationMessages(containerId,
					validationaMessages.toArray(new ValidationMessage[0]));
			isValid = false;
		}

		return isValid;
	}

	public void validateEditEmissionInventory(boolean internalApp,
			List<ValidationMessage> validationaMessages) {

		if (report.getInvoiceDate() != null
				&& report.getPaymentReceivedDate() != null
				&& report.getPaymentReceivedDate().before(
						report.getInvoiceDate())) {
			validationaMessages
					.add(new ValidationMessage(
							"paymentReceivedDate",
							"The \" Payment Received Date \" cannot be before \"Invoice Date\"",
							ValidationMessage.Severity.ERROR, "report:R"
									+ report.getEmissionsInventoryId()));

		}
	}

	public static final String formatId(String prefix, String digitFormat,
			String id) {
		if (!Utility.isNullOrEmpty(id) && !Utility.isNullOrEmpty(prefix)) {
			String format = prefix + digitFormat;

			int tempId;
			try {
				tempId = Integer.parseInt(id);
				id = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
		return id;
	}

	public final boolean isUpdatingInvoiceInfo() {
		return updatingInvoiceInfo;
	}

	public final void setUpdatingInvoiceInfo(boolean updatingInvoiceInfo) {
		this.updatingInvoiceInfo = updatingInvoiceInfo;
	}

	public List<MissingFIREFactor> getMissingFactors() {
		return missingFactors;
	}

	public void setMissingFactors(List<MissingFIREFactor> missingFactors) {
		this.missingFactors = missingFactors;
	}

	public TableSorter getMissingFactorsWrapper() {
		return missingFactorsWrapper;
	}

	public void setMissingFactorsWrapper(TableSorter missingFactorsWrapper) {
		this.missingFactorsWrapper = missingFactorsWrapper;
	}

	public Integer getMissingFactorsYear() {
		return missingFactorsYear;
	}

	public void setMissingFactorsYear(Integer missingFactorsYear) {
		this.missingFactorsYear = missingFactorsYear;
	}

	public String getFacilityClass() {
		return facilityClass;
	}

	public void setFacilityClass(String facilityClass) {
		this.facilityClass = facilityClass;
	}

	public EmissionsInvoice getEmissionsInvoice() {
		return emissionsInvoice;
	}

	public void setEmissionsInvoice(EmissionsInvoice emissionsInvoice) {
		this.emissionsInvoice = emissionsInvoice;
	}

	public final String showInvoiceDetails() {
		emissionsInvoice = new EmissionsInvoice();
		emissionsInvoice.setReport(report);
		emissionsInvoice.setEmissionsReportService(emissionsReportService);
		emissionsInvoice.setFacility(facility);

		return emissionsInvoice.getReportTotalEmissionsFee();
	}

	public final void closeChangeMaterialDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public String initAdminEnableYear() {
		initEnableYear();
		return "adminEREnable";
	}

	public void selectAllSCEmissionsReports() {
		for (SCEmissionsReport s : this.scEmissionsReports) {
			s.setSelected(true);
		}
	}

	public void unSelectAllSCEmissionsReports() {
		for (SCEmissionsReport s : this.scEmissionsReports) {
			s.setSelected(false);
		}
	}

	public String refreshSCEmissionsReports() {

		List<SCEmissionsReport> scEmissionsReportsNew = new ArrayList<SCEmissionsReport>();

		try {
			SCEmissionsReport[] scEmissionsReports = getInfrastructureService()
					.retrieveSCEmissionsReports();

			Calendar c = Calendar.getInstance();
			Integer currentYear = new Integer(-1);
			currentYear = c.get(Calendar.YEAR);

			for (SCEmissionsReport s : scEmissionsReports) {
				// If Service Catalog template is for last year and the content
				// type is configured
				// for full year, add the template.
				if (s.getReportingYear() != null
						&& s.getReportingYear().equals(getEnableYear())) {
					if (s.isSCContentTypeForFullYear()) {
						SCEmissionsReport item = new SCEmissionsReport(s);
						item.setSelected(true);

						scEmissionsReportsNew.add(item);
					}
					// If Service Catalog template is for current year and the
					// content type is configured
					// for partial year, add the template.
				} else if (s.getReportingYear() != null
						&& s.getReportingYear().equals(currentYear)) {
					if (s.isItTimeToEnableReportingForPartialCurrentYear()) {
						SCEmissionsReport item = new SCEmissionsReport(s);
						item.setSelected(true);

						scEmissionsReportsNew.add(item);
					}

				}
			}
		} catch (RemoteException e) {
			handleException(e);
			return null;
		}
		setScEmissionsReports(scEmissionsReportsNew
				.toArray(new SCEmissionsReport[scEmissionsReportsNew.size()]));

		return null;
	}

	public SCEmissionsReport[] getScEmissionsReports() {
		return scEmissionsReports;
	}

	public void setScEmissionsReports(SCEmissionsReport[] scEmissionsReports) {
		this.scEmissionsReports = scEmissionsReports;
	}

	public FacilityReference getFacilityReference() {
		return facilityReference;
	}

	public void setFacilityReference(FacilityReference facilityReference) {
		this.facilityReference = facilityReference;
	}

	// ******************** Wizard Start ******************** //

	public String confirmWizardLaunch() {
		return "dialog:confirmWizardLaunch";
	}

	public void closeWizardAndRefresh(ReturnEvent re) {
		closeDialog();
	}

	public String launchWizard() {
		String ret = null;
		try {
			// first clear any exisiting data that has been entered
			clearEmissionsReportData();

			for (EmissionsReportEU eu : this.report.getEus()) {
				for (EmissionsReportPeriod period : eu.getPeriods()) {
					FireRow[] fireRows = getEmissionsReportService()
							.retrieveFireRows(this.report.getReportYear(),
									period);
					period.setFireRows(fireRows);

					period.setMaus(getEmissionsReportService()
							.processMaterialActions(
									getTreeReport().getReportYear(),
									isClosedForEdit(), period, fireRows));
					// do we really need to set the below two properties??
					period.setStaticMaterial(period.getCurrentMaterial());
					period.setStaticYear(this.report.getReportYear());

					// add material and variable only if there is one material
					// if there are multiple materials, then material and
					// variable information will be added later when the user
					// selects which material to use on the wizard.
					if (!period.isHasMultipleMaterials()) {
						ArrayList<EmissionRow> periodEmissions = EmissionRow
								.getEmissions(this.report.getReportYear(),
										period, this.scReports, false,
										isClosedForEdit(), getPercentDiff(),
										logger);
						period.setCapHapList(periodEmissions);

						period.determineVars(this.report, periodEmissions,
								fireRows);
					}
				}
			}
			ret = "dialog:dataEntryWizard";
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("An error occured when trying to launch the data entry wizard");
			handleException(this.reportId, re);
		} catch (ApplicationException ae) {
			DisplayUtil
					.displayError("An error occured when trying to launch the data entry wizard");
			handleException(this.reportId, ae);
		}

		return ret;
	}

	public void saveDataFromWizard() {
		if (validateWizardData()) {
			try {
				for (EmissionsReportEU eu : this.report.getEus()) {
					if (!eu.isZeroEmissions()) {
						for (EmissionsReportPeriod period : eu.getPeriods()) {
							FireRow[] fireRows = getEmissionsReportService()
									.retrieveFireRows(
											this.report.getReportYear(), period);
							period.setFireRows(fireRows);

							// remove unslected material(s) (applies when period
							// has more than one material)
							Iterator<EmissionsMaterialActionUnits> iter = period
									.getMaus().iterator();
							while (iter.hasNext()) {
								if (!iter.next().isBelongs()) {
									iter.remove();
								}
							}

							ArrayList<EmissionRow> periodEmissions = EmissionRow
									.getEmissions(this.report.getReportYear(),
											period, this.scReports, false,
											false, getPercentDiff(), logger);

							for (EmissionRow er : periodEmissions) {
								er.setP(period);
								// default the calculation method to throughput
								// based
								if (Utility.isNullOrEmpty(er
										.getEmissionCalcMethodCd())) {
									er.setEmissionCalcMethodCd(EmissionCalcMethodDef.SCCEmissionsFactor);
									er.setAnnualAdjust("0");
								}
							}
							period.setCapHapList(periodEmissions);

							period.updateFireFactors(periodEmissions, fireRows,
									this.report);

							period.updateEmissions(this.facility, this.report,
									periodEmissions, period.getFireRows());
						}
					}
				}
				// setup the euEg71Table table so that the data associated with
				// those eus that have been marked as did not operate, is
				// deleted during the save report
				createEUListPopInternal();

				saveReport();

				// update the total emissions
				getEmissionsReportService().updateTotalEmissions(this.report,
						this.scReports, isClosedForEdit());

				AdfFacesContext.getCurrentInstance().returnFromDialog(null,
						null);

			} catch (RemoteException re) {
				DisplayUtil
						.displayError("An error occured when trying to save the data from the wizard");
				handleException(this.reportId, re);
			} catch (ApplicationException ae) {
				DisplayUtil
						.displayError("An error occured when trying to save the data from the wizard");
				handleException(this.reportId, ae);
			}
		}
	}

	public boolean validateWizardData() {
		String pageViewId = "dataEntryWizard:";
		boolean isValid = true;
		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();
		int i = 0;

		for (EmissionsReportEU reu : this.report.getEus()) {
			if (!reu.isEg71OrZero()) {
				int j = 0;
				for (EmissionsReportPeriod erp : reu.getPeriods()) {
					if (null != erp.getHoursPerYear()) {
						if (erp.getHoursPerYear() < EmissionsReportPeriod.MIN_OPERATING_HOURS_VAL
								|| erp.getHoursPerYear() > getMaxHoursInReportingPeriod()) {
							isValid = false;
							String id = "eus:" + i + ":emissionPeriods:" + j
									+ ":hoursPerYear";
							valMsgs.add(new ValidationMessage(
									id,
									"Operating Hours must be between "
											+ EmissionsReportPeriod.MIN_OPERATING_HOURS_VAL
											+ " and "
											+ getMaxHoursInReportingPeriod(),
									ValidationMessage.Severity.ERROR,
									"hoursPerYear"));
						} else if (erp.getHoursPerYear() != 0) {
							// check the user has entered data for material
							// throughput and variables
							int k = 0;
							for (EmissionsMaterialActionUnits maus : erp
									.getMaus()) {
								if (maus.isBelongs()
										&& Utility.isNullOrEmpty(maus
												.getThroughput())) {
									isValid = false;
									String id = "eus:" + i
											+ ":emissionPeriods:" + j
											+ ":maus:" + k + ":throughput";
									valMsgs.add(new ValidationMessage(
											id,
											"Must enter value for throughput because operating hours is non-zero",
											ValidationMessage.Severity.ERROR,
											"throughput"));
								}
								k++;
							}
							int l = 0;
							for (EmissionsVariable ev : erp.getVars()) {
								if (Utility.isNullOrEmpty(ev.getValue())) {
									isValid = false;
									String id = "eus:" + i
											+ ":emissionPeriods:" + j
											+ ":vars:" + l + ":value";
									valMsgs.add(new ValidationMessage(
											id,
											"Must enter value for the variable because operating hours is non-zero",
											ValidationMessage.Severity.ERROR,
											"value"));
								}
								l++;
							}
						}
					} else {
						// raise error if throughput and/or variables are 
						// not null when operating hours is null
						for (EmissionsMaterialActionUnits maus : erp
								.getMaus()) {
							if (maus.isBelongs()
									&& !Utility.isNullOrEmpty(maus
											.getThroughput())) {
								isValid = false;
								String id = "eus:" + i + ":emissionPeriods:" + j
										+ ":hoursPerYear";
								valMsgs.add(new ValidationMessage(
										id,
										"Operating Hours cannot be empty when Throughput has a value",
										ValidationMessage.Severity.ERROR,
										"hoursPerYear")); 
							}
						}
						
						for (EmissionsVariable ev : erp.getVars()) {
							if (!Utility.isNullOrEmpty(ev.getValue())) {
								isValid = false;
								String id = "eus:" + i + ":emissionPeriods:" + j
										+ ":hoursPerYear";
								valMsgs.add(new ValidationMessage(
										id,
										"Operating Hours cannot be empty when a Variable has a value",
										ValidationMessage.Severity.ERROR,
										"hoursPerYear"));
							}
						}
					}
					j++;
				}
			}
			i++;
		}

		if (!isValid) {
			displayValidationMessages(pageViewId,
					valMsgs.toArray(new ValidationMessage[0]));
		}

		return isValid;
	}

	private void clearEmissionsReportData() throws RemoteException {
		this.ok = true;
		ArrayList<EmissionsReportEU> reuClearList = new ArrayList<EmissionsReportEU>(
				this.report.getEus());

		// first clear existing data
		saveEmissionUnitInternal2(reuClearList);

		// next set eu reporting level to detailed reporting
		if (this.ok) {
			for (EmissionsReportEU reu : this.report.getEus()) {
				reu.setEmissionChoice(2);
			}
			saveEmissionUnitInternal2(null);
			if (this.ok) {
				this.recreateTreeFromReport(this.getCurrent());
			}
		}

		if (!this.ok) {
			throw new DAOException(
					"An error occured when trying to clear the emissions report data");
		}
	}

	public EmissionsReportPeriod getSelectedPeriod() {
		return selectedPeriod;
	}

	public void setSelectedPeriod(EmissionsReportPeriod selectedPeriod) {
		this.selectedPeriod = selectedPeriod;
	}

	public EmissionsMaterialActionUnits getSelectedMaterial() {
		return selectedMaterial;
	}

	public void setSelectedMaterial(
			EmissionsMaterialActionUnits selectedMaterial) {
		this.selectedMaterial = selectedMaterial;
	}

	public String selectMaterial() {
		if (null != this.selectedPeriod && null != this.selectedMaterial) {
			try {
				FireRow[] fireRows = this.selectedPeriod.getFireRows();
				List<EmissionsMaterialActionUnits> maus = new ArrayList<EmissionsMaterialActionUnits>(
						this.selectedPeriod.getMaus());

				for (EmissionsMaterialActionUnits m : this.selectedPeriod
						.getMaus()) {
					m.setBelongs(false);
					m.setThroughput(null);

					if (m.getMaterial().equals(
							this.selectedMaterial.getMaterial())
							&& m.getAction().equals(
									this.selectedMaterial.getAction())) {
						m.setBelongs(true);
					}
				}

				// temporarily remove any unselected material(s) so that only
				// pollutants relevant to the selected material can be added
				// to the period
				Iterator<EmissionsMaterialActionUnits> iter = this.selectedPeriod
						.getMaus().iterator();
				while (iter.hasNext()) {
					if (!iter.next().isBelongs()) {
						iter.remove();
					}
				}

				// clear exising pollutants
				this.selectedPeriod.getEmissions().clear();

				ArrayList<EmissionRow> periodEmissions = EmissionRow
						.getEmissions(this.report.getReportYear(),
								this.selectedPeriod, this.scReports, false,
								false, getPercentDiff(), logger);
				for (EmissionRow er : periodEmissions) {
					er.setP(this.selectedPeriod);
				}

				this.selectedPeriod.setCapHapList(periodEmissions);

				this.selectedPeriod.updateFireFactors(periodEmissions,
						fireRows, this.report);

				this.selectedPeriod.updateEmissions(this.facility, this.report,
						periodEmissions, this.selectedPeriod.getFireRows());

				this.selectedPeriod.setStaticMaterial(this.selectedPeriod
						.getCurrentMaterial());
				this.selectedPeriod.setStaticYear(this.report.getReportYear());

				this.selectedPeriod.determineVars(this.report, periodEmissions,
						fireRows);

				// add the unselected materials back to the period so that they
				// can be displayed on the web page
				this.selectedPeriod.setMaus(maus);

			} catch (ApplicationException ae) {
				DisplayUtil
						.displayError("An error occured when selecting a material");
				handleException(this.reportId, ae);
			}
		}
		return null; // stay on the same page
	}
	
	public String markAllEUsAsDidNotOperate() {
		for(EmissionsReportEU reu: this.report.getEus()) {
			reu.setZeroEmissions(true);
		}
		
		return null; // stay on the same page
	}
	
	public String markAllEUsAsOperated() {
		for(EmissionsReportEU reu: this.report.getEus()) {
			reu.setZeroEmissions(false);
		}
		
		return null; // stay on the same page
	}

	// ******************** Wizard End ******************** //

	public final List<SelectItem> getContentTypes() {
		List<SelectItem> ret = null;

		if (report != null) {
			ret = getContentTypes(reportingYear.getContentTypeCd());
		} else {
			ret = getContentTypes("");
		}

		return ret;
	}

	public final List<SelectItem> getRegulatoryRequirementTypes() {
		List<SelectItem> ret = null;

		if (report != null) {
			ret = getRegulatoryRequirementTypes(reportingYear
					.getRegulatoryRequirementCd());
		} else {
			ret = getRegulatoryRequirementTypes("");
		}

		return ret;
	}
	public void deleteEmissionsRptInfo(ReturnEvent re) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}

		try {
			getEmissionsReportService().deleteEmissionsRptInfo(
					this.reportingYear.getFacilityId(),
					this.reportingYear.getScEmissionsReportId());
			String contentTypeDesc = 
					ContentTypeDef.getData().getItems().getItemDesc(this.reportingYear.getContentTypeCd());
    		String regReqDesc = 
					RegulatoryRequirementTypeDef.getData().getItems().getItemDesc(this.reportingYear.getRegulatoryRequirementCd());
			DisplayUtil
					.displayInfo("Successfully deleted EI enabled status row for "
							+ this.reportingYear.getFacilityId()
							+ "; Reporting Year: "
							+ this.reportingYear.getYear()
							+ "; Content Type: "
							+ contentTypeDesc
							+ "; Regulatory Requirement Type: "
							+ regReqDesc);
			closeReportingYearDialogAndRefresh();
		} catch (RemoteException de) {
			String msg = "Failed to delete EI enabled status row";
			logger.error(msg, de);
			DisplayUtil.displayError(msg);
			handleException(de);
		}
	}

	public void closeReportingYearDialogAndRefresh() {
		setYearlyInfoEditable(false);
		savedFpId = null;
		getYearlyReportingInfo();
		closeReportingYearDialog();
	}

	// only allow to delete an EI enabled status row if it is not associated
	// with any EIs
	public boolean getAllowedToDeleteEmissionsRptInfo() {
		boolean ret = false;

		Integer cnt = getEmissionsReportService()
				.emissionsInventoriesForFacilityAndServiceCatalog(
						this.reportingYear.getFacilityId(),
						this.reportingYear.getScEmissionsReportId());
		if (cnt != null && cnt.intValue() == 0) {
			// should not be null, if it is ignore it.
			ret = true;
		}

		return ret;
	}
	
	public void resetCopyFlag(ValueChangeEvent vce) {
		setCopyFromExistingRpt(false);
	}
	
	// returns maximum hours in the period defined by the start date and
	// end date for the content type associated with the report
	public double getMaxHoursInReportingPeriod() {
		
		double maxHours = report.getMaxHoursInReportingPeriod();
/*		ContentTypeDef contentTypeDef = ContentTypeDef.getContentTypeDef(report
				.getContentTypeCd());
		
		LocalDate periodStarDate = LocalDate.of(report.getReportYear(),
				contentTypeDef.getStartMonth(), contentTypeDef.getStartDay());
		
		LocalDate periodEndDate = LocalDate.of(report.getReportYear(),
				contentTypeDef.getEndMonth(), contentTypeDef.getEndDay());

		long daysInBetween = ChronoUnit.DAYS.between(periodStarDate,
				periodEndDate) + 1; // add 1 for the days to be inclusive

		double maxHours = daysInBetween * 24;*/

		return maxHours;
	}
	
	public boolean isWinterReport() {
		return (null != report.getContentTypeCd() 
					&& report.getContentTypeCd().equals(ContentTypeDef.WINTER)) ? true	: false;
	}
	
	
	/**
	 * For all the emisisons periods in the report, changes the 
	 * emissions calculation method to "Emissions" for the reported
	 * emissions
	 * 
	 * @param report
	 * @throws none
	 * 
	 */
	private void changeEmissionsCalcMethodToEmissions(EmissionsReport report) {
		if (null != report) {
			for (EmissionsReportEU eu : report.getEus()) {
				for (EmissionsReportPeriod period : eu.getPeriods()) {
					Iterator<Emissions> iter = period.getEmissions().values().iterator();
					while (iter.hasNext()) {
						Emissions e = (Emissions) iter.next();
						if (null != e) {
							e.setEmissionCalcMethodCd(EmissionCalcMethodDef.SCCEmissions);
						}
					}
				}
			}
		}
	}
	
	
	public void TsValueChange(ValueChangeEvent valueChangeEvent) {
		if (valueChangeEvent.getNewValue().toString().equalsIgnoreCase("true")) {
			materialRow.setTradeSecretT(true);
		}

		if (valueChangeEvent.getNewValue().toString().equalsIgnoreCase("false")) {
			materialRow.setTradeSecretT(false);
			materialRow.setTradeSecretTText(null);
		}
	}
	
	
	
	public boolean isConfidentialDataVisibleForIntrnalUser(){
		boolean ret=false;	
		// has it already been marked as TS?
			if (materialRow != null) {
				if (null != materialRow.getTradeSecretTText()) {
					tsJust = materialRow.getTradeSecretTText();
//					 boolean tsFlag=materialRow.isTradeSecretT(); 
						 //Why is flag not
					// available here
					// If flag has been set and user does not have the Use Case,
					// then only hide
					if (!tsJust.isEmpty() ){ // || tsFlag) {
						ret= InfrastructureDefs.getCurrentUserAttrs()
								.isCurrentUseCaseValid("EIConfidentialDataAccess");

					}
				}
				//If it hasn't been marked confidential, show it even if user does not have the use case assigned.
				if (null==materialRow.getTradeSecretTText() || ! materialRow.isTradeSecretT()) {
					ret= true;

				}

			}
			
			if(isStars2Admin()){
				ret=true;
			}
		return ret;
	}
	
	public boolean isThroughputVisible() {
		boolean ret = true;

		if (isPublicApp()) {
			ret = false;
		}

		if (isPortalApp()) {
			ret = true;
		}

		if (isInternalApp()) {
			ret=isConfidentialDataVisibleForIntrnalUser();
		}

		return ret;
	}

	
	public boolean isDisableConfidentialLink() {
		boolean ret = false;

		if (isPublicApp()) {
			return true;
		}

		if (isPortalApp() || isStars2Admin()) {
			return false;
		}

		if (isInternalApp()) {

			if (isConfidentialDataVisibleForIntrnalUser() == false) {

				ret = true;

			}

			// If TS is null(i.e not marked as TS till now like for existing
			// PRCs), for those who do not have use case.
			// Is there any use to show read only link if there is no
			// justification text(i.e. empty)
			if (null == materialRow.getTradeSecretTText() || materialRow.getTradeSecretTText().isEmpty()) {
				boolean hasAccessToUseCase = InfrastructureDefs.getCurrentUserAttrs()
						.isCurrentUseCaseValid("EIConfidentialDataAccess");
				if (hasAccessToUseCase == false) {
					ret = true;
				}
			}

		}
		return ret;
	}

	
	public boolean isEiConfidentialDataAccess() {
		boolean ret = true;

		if (isInternalApp()) {
			ret=isConfidentialDataVisibleForIntrnalUser();

		}
		if (isPublicApp()) {
			ret = false;
		}

		return ret;
	}

	
	public boolean isHideEiConfidentialDataFromPDF() {
		boolean ret = true;

		if (isInternalApp()) {
			boolean hasAccessToUseCase = InfrastructureDefs.getCurrentUserAttrs()
					.isCurrentUseCaseValid("EIConfidentialDataAccess");
			if(hasAccessToUseCase==true ){
				ret=false;
			}

		}
		if (isPublicApp()) {
			ret = true;
		}
		
		if(isPortalApp()){
			ret=true;
		}

		if( isStars2Admin()){
			ret=false;
		}
		return ret;
	}

	
	
	public boolean isConfidentialLabelVisible() {
		return SystemPropertyDef.getSystemPropertyValueAsBoolean("hideEIConfidentialData",true);
//		Confidential Label is displayed in UI only when this feature is enabled/available for the instance,(i.e when the flag is "true".
	}


	public boolean isDeleteDisabled() {
		return deleteDisabled;
	}

	public void setDeleteDisabled(boolean deleteDisabled) {
		this.deleteDisabled = deleteDisabled;
	}

	public String getDeleteDisabledMsg() {
		return deleteDisabledMsg;
	}

	public void setDeleteDisabledMsg(String deleteDisabledMsg) {
		this.deleteDisabledMsg = deleteDisabledMsg;
	}

	
	public boolean isTradeSecretT(){
		boolean throughputConfidentialFlag = false;
		if(secretMaterialRow!=null){
		if (null == secretMaterialRow.getTradeSecretTText()
				|| secretMaterialRow.getTradeSecretTText().trim().length() == 0) {
			throughputConfidentialFlag= false;
		}
		else throughputConfidentialFlag= true;
		}
		return throughputConfidentialFlag;

	}
}