package us.oh.state.epa.stars2.webcommon.facility;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import com.lowagie.text.DocumentException;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.component.core.layout.CorePanelForm;
import oracle.adf.view.faces.event.DisclosureEvent;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.model.RowKeySet;
import oracle.adf.view.faces.model.SortCriterion;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.facility.UndeliveredMail;
import us.oh.state.epa.stars2.app.tools.FieldAuditLogSearch;
import us.oh.state.epa.stars2.app.tools.ModelingExtract;
import us.oh.state.epa.stars2.bo.FacilityBO;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.NaicsService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.bo.StackTestService;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage.Severity;
import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.facility.DecaneProperties;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPointCem;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EuEmission;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityOwner;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRole;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRoleActivity;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityVersion;
import us.oh.state.epa.stars2.database.dbObjects.facility.HydrocarbonAnalysisSampleDetail;
import us.oh.state.epa.stars2.database.dbObjects.facility.MultiEstabFacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantsControlled;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitReplacement;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ApiGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ForeignKeyReference;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.def.AirProgramDef;
import us.oh.state.epa.stars2.def.AirProgramsDef;
import us.oh.state.epa.stars2.def.ComplianceStatusDef;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.EventLogTypeDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.NAICSDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.FacilityEmissionFlow;
import us.oh.state.epa.stars2.webcommon.Stars2TreeNode;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;
import us.oh.state.epa.stars2.webcommon.component.BuildComponent;
import us.oh.state.epa.stars2.webcommon.converter.SccCodeConverter;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.pdf.facility.FacilityPdfGenerator;
import us.wy.state.deq.impact.app.continuousMonitoring.ContinuousMonitorDetail;
import us.wy.state.deq.impact.bo.ContactService;
import us.wy.state.deq.impact.bo.ContinuousMonitorService;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;
import us.wy.state.deq.impact.def.EquipTypeDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeLiquidDef;

public class FacilityProfileBase extends TreeBase {

	private static final long serialVersionUID = -3501443034650232438L;

	private static final short MAX_NUM_COMPS = 10;
	private static final short FACILITY_COMP = 0;
	private static final short EM_UNIT_COMP = 1;
	private static final short EM_PROC_COMP = 2;
	private static final short CNT_EQUIP_COMP = 3;
	private static final short EG_POINT_COMP = 4;
	private static final short ADD_CNT_EQUIP_COMP = 5;
	private static final short ADD_EG_POINT_COMP = 6;
	private static final short REM_EG_POINT_COMP = 8;
	private static final short REM_CNT_EQUIP_COMP = 9;
	private static final short NO_OP = 0;
	private static final short EDIT_OP = 1;
	private static final short CREATE_OP = 2;
	private static final short DELETE_OP = 3;
	private static final short CLONE_OP = 4;

	private static final String FAC_NODE = "facility";
	private static final String FAC_REFERENCE = "facility";
	private static final String UACE_REFERENCE = "UnassignedCEs";
	private static final String UACE_NODE = "UnassignedCEs";
	private static final String UAEGP_REFERENCE = "UnassignedEgrPnts";
	private static final String UAEGP_NODE = "UnassignedEgrPnts";
	private static final String EU_NODE = "emissionUnits";
	private static final String EU_REFERENCE_PREFIX = "emissionUnit:";
	private static final String EP_NODE = "emissionProcesses";
	public static final String EP_NOSTACK_PREFIX = "noStack";
	private static final String EP_REFERENCE_PREFIX = "emissionProcess:";
	private static final String CE_NODE = "controlEquipment";
	private static final String CE_REFERENCE_PREFIX = "controlEquipment:";
	private static final String EGP_NODE = "egressPoints";
	private static final String EGP_REFERENCE_PREFIX = "releasePoint:";
	private static final String RP_REFERENCE_PREFIX = "releasePoint:";
	public static final String CONTACTS_REFERENCE = "contact";
	public static final String CONTACTS_OUTCOME = "facilities.profile.contacts";
	public static final String RULES_REFERENCE = "fedRules";
	public static final String RULES_OUTCOME = "facilities.profile.fedRules";
	public static final String OWNERS_OUTCOME = "facilities.profile.owners";
	public static final String FAC_OUTCOME = "facilityProfile";
	public static final String HOME_FAC_OUTCOME = "homeFacilityProfile";
	public static final String EU_NOPROCESSES_PREFIX = "noEmissionProcesses";
	private static final String FAC_CLIENT_ID = "facilityProfile:comFacProfile:";
	protected static final String CONTACT_CLIENT_ID = "contact:";
	private static final String[] renderComps = { FAC_NODE, EU_NODE, EP_NODE,
			CE_NODE, EGP_NODE, "addControlEquip", "addEgressPoint",
			"invalidEmissionUnit", "removeEgressPoint", "removeControlEquip" };
	private static final String DUMMY_CE_NODE = "Disassociated CEs";
	private static final String DUMMY_EGR_PNT_NODE = "Disassociated R. Points";
	private static final String DUMMY_INV_EU_NODE = "Invalid EUs";
	private static final String DUMMY_SD_EU_NODE = "Shutdown EUs";
	private static final String DUMMY_MORE_NODE = " ";
	public static final String FAC_NO_NAICS = "naicsTab";
	public static final String FAC_NO_API = "apisTab";
	// private static final String OWNER_CAUTION_MSG =
	// "Caution: you have selected 'owner' as the contact type. "
	// +
	// "Saving this change will constitute a change in ownership for the facility "
	// +
	// "which may impact emissions reporting responsibility and corresponding fees.";
	// private static final String RO_CAUTION_MSG =
	// "You are about to identify or change a responsible official for your facility."
	// +
	// " Only certain officials or persons within an organization statutorily meet the requirements to perform the signatory authority duties of a responsible official."
	// +
	// " Improperly assigning a responsible official could lead to delays in the processing of documents requiring signature by the official."
	// +
	// " Please read the important requirements contained in the document - Ensuring the Correct Person is Acting as the Responsible Official for a Facility Subject to Air Pollution Regulations - "
	// +
	// "found on the External Reference page. This document will help you identify qualifying persons for signatory authority. ";
	// private static final String OWNER_DEL_CAUTION_MSG =
	// "Caution: you have selected 'owner' as the contact type. "
	// +
	// "Saving of deleting ownership will constitute a change in ownership for the facility "
	// +
	// "which may impact emissions reporting responsibility and corresponding fees. The contact person will remain but not as the owner during the start and end dates.";
	private static final String RO_DEL_CAUTION_MSG = "You are about to delete dates of a responsible official for your facility.The contact person will remain but not as the responsible official during the start and end dates."
			+ " Only certain officials or persons within an organization statutorily meet the requirements to perform the signatory authority duties of a responsible official."
			+ " Improperly deleting dates of a responsible official could lead to delays in the processing of documents requiring signature by the official.";

	private static final int addAddressIndex = (int) 9999;
	private static final String NEW_EGRPNT_ID = "NEWEGRPNTID";
	private static final String NEW_CNTEQUIP_ID = "NEWCNTEQUIPID";

	protected boolean staging;
	protected Integer fpId;
	protected String facilityId;
	protected Facility facility;
	protected Facility currentFacility;
	protected EmissionUnit emissionUnit; // Selected Emission Unit
	private EmissionUnit emissionUnit1;
	private EmissionProcess emissionProcess;
	private ControlEquipment controlEquipment;
	private ControlEquipment controlEquipmentTemp;
	private EgressPoint egressPoint;
	private EgressPoint egressPointTemp;
	private boolean discloseEIS = false;
	private FacilityReference facilityReference;
	private String epaEmuId;
	private String cntEquipId;
	private transient CorePanelForm contEquipTypeData;
	private String egrPntId;
	private boolean editable;
	private boolean editable1;
	private boolean activeContacts;
	private boolean editStaging = false;
	private String oldFacOperStatus = "";
	private short[] operation = new short[MAX_NUM_COMPS];
	private String selectSccMethod;
	protected String byClickEpaEmuId;
	private String saveObjId;
	private HashMap<Integer, Integer> copyOnChangeFpNodeIds;

	// address
	private boolean addressChanging; // if not in edit mode or in edit mode and
										// address not changed.
	protected Stars2TreeNode facNode;
	private Stars2TreeNode UnAssignCEsNode;
	private Stars2TreeNode UnAssignEgpsNode;
	private HashMap<String, Stars2TreeNode> facNodeMap;
	private FacilityNote[] facilityNotes;
	private FacilityNote facilityNote;
	private FacilityNote modifyFacilityNote;
	private boolean noteModify;
	protected int expandOpt;
	private boolean expandTree = false;
	private Timestamp revisedHistoryDate;
	private FacilityVersion selectedHistory;
	private FacilityNote historyNote;
	private EmissionUnitReplacement emissionUnitReplacement;
	private EmissionUnitReplacement currentEngineSerialNumber;
	private transient UIXTable historyTable;
	protected List<ContactUtil> facilityContacts;
	protected List<Contact> allContacts;
	protected List<Contact> globalContacts;
	protected List<Contact> allFacilityContacts;

	// TODO Owner
	protected List<FacilityOwner> facilityOwners;
	private List<SelectItem> contactsList;
	private boolean useGlobalContactList;
	private ArrayList<ContactType> contactTypes = new ArrayList<ContactType>();
	protected ContactUtil facContact;
	protected Contact contact;
	protected FacilityOwner owner;
	protected ContactType saveContactType;
	protected boolean contactModify;
	protected boolean addressModify;
	protected Address address;
	protected ApiGroup modifyApiGroup;
	protected Stars2Object modifyNaics;

	private int modifyApiIndex;
	private int modifyAddressIndex;
	private int modifyNaicsIndex;
	private boolean newNaics = false;

	private SccCodeConverter sccCodeConverter = new SccCodeConverter();
	private String sccSearchkeyWords;
	private SccCode[] sccCodes;
	private transient UIXTable sccCodesTable;
	private EventLog newEventLog;
	private String removeOpMessage;
	private String confirmMessage;
	private String confirmOperation = "";
	private String contEquipsEpaEuIds;
	private String egrPointsEuIds;
	private transient UIXTable egressPointCemTable;
	private Correspondence[] correspondences;
	private List<Stars2Object> sicCds;
	protected List<Stars2Object> naicsCds;
	private List<Stars2Object> mactSubparts;
	private List<Stars2Object> mactComp;
	private List<PollutantCompCode> neshapsSubparts;
	private List<Stars2Object> nspsSubparts;
	private List<Stars2Object> neshapsPollutants;
	protected List<String> selEpaEmuIds;
	private List<Permit> euHistPermits;
	private TableSorter facilityContactsWrapper;
	private TableSorter facilityOwnersWrapper;
	private TableSorter facilityApisWrapper;
	private TableSorter emusWrapper;
	private TableSorter cesWrapper;
	private TableSorter egpsWrapper;
	private TableSorter notesWrapper;
	private TableSorter notesFacWrapper;
	private TableSorter facRolesWrapper;
	private TableSorter addrHistWrapper;
	private TableSorter euEmissionsWrapper;
	private TableSorter pollutantsContWrapper;
	private TableSorter facEmissionsWrapper;
	private TableSorter mactSubpartsWrapper;
	private TableSorter mactCompWrapper;
	private TableSorter neshapsSubpartsWrapper;
	private TableSorter nspsSubpartsWrapper;
	private TableSorter sicCdsWrapper;
	private TableSorter naicsCdsWrapper;
	private InfrastructureDefs infraDefs;
	private ArrayList<String> treePath;
	private MultiEstabFacilityList[] multiEstabFacilities;
	private DataDetail[] ceDataDetails;
	private int renderComp = 1; // default facility
	private String facilityProfileBean;
	protected String popupRedirect = null;
	protected int currentUserId = InfrastructureDefs.getCurrentUserId();
	private List<Document> facProfDocuments = null;
	private String branchingAirFlowMsg = null;
	private String controlEquipAttribDescMsg = null;
	private String cancelLabel = CANCEL; // on process and control equipment
											// page
	private static String CANCEL = "Cancel";
	private static String CANCEL_LATER = "Cancel; I will provide missing percentages later";
	private static String PROPORTIONAL_PERCENT_MSG = "Cancel; The Flow Percentages will be proportionally adjusted to total 100%";

	private boolean hideInvalidEUs = false;

	// PORTAL: facility-owned company contacts
	protected TableSorter facOwnerContactsWrapper;
	private ApiGroup oldApiGroup;
	private Address oldAddress;

	private boolean addedneshapsRow;
	private boolean addedNspsRow;
	private boolean addedPsdRow;
	private boolean addedNsrRow;

	// Enforcement/Compliance Summary info
	private Timestamp lastFCEDate = null;
	private String fceScheduled = "";
	private String lastSiteVisitDate = "";
	private Timestamp lastStackTestDate = null;

	protected EuEmission selectedEuEmission;
	protected boolean newEuEmission;

	private FacilityService facilityService;
	private FullComplianceEvalService fullComplianceEvalService;
	private ContactService contactService;
	private InfrastructureService infrastructureService;
	private NaicsService naicsService;
	private PermitService permitService;
	private StackTestService stackTestService;
	private String validTradeSecretNotifFrom;
	private boolean noEngineSerialNumbers;
	private String serialNumberText;
	private boolean engineSerialTrackingEditMode;
	private boolean engineSerialNumberStatusEditable;
	private List<EmissionUnitReplacement> oldSerialNumbers;
	private List<EmissionUnitReplacement> currentSerialNumbers;
	private List<EuEmission> oldEUPermittedEmissions;
	
	// Facility CEM/COM/CMS Limits
	private TableSorter facilityCemComLimitsWrapper;
	protected FacilityCemComLimit modifyFacilityCemComLimit;
	private boolean newFacilityCemComLimit = false;
	private boolean facilityCemComLimitEditMode;
	private static final int CCL_WIDTH = 1000;
	
	private ContinuousMonitorService continuousMonitorService;
	
	private TableSorter continuousMonitorsWrapper;

	private String facilityCemComLimitDeleteAllowedMsg;
	
	private boolean discloseHC = false;

	//Fugitive Component Table
	private TableSorter fugComponentsWrapper;
	//Extended Hydrocarbon Analysis Table
	private TableSorter hydrocarbonAnalysisWrapper;
	private TableSorter hydrocarbonAnalysisSampleDetailWrapper;
	private List<HCAnalysisSampleDetailRow> hcAnalysisSampleDetailList;
	
	// decane properties table
	private List<DecanePropertyLineItem> decanePropertyLineItems = new ArrayList<DecanePropertyLineItem>();
	private TableSorter decanePropertiesWrapper = new TableSorter();
	
	// Facility User Roles - Activities
	private String facilityRoleCdForActivity;
	private TableSorter facUserRoleAactivityWrapper;
	private boolean isActivityNull;

	public FacilityProfileBase() {
		contEquipTypeData = new CorePanelForm();
		facilityContactsWrapper = new TableSorter();
		facilityOwnersWrapper = new TableSorter();
		facilityApisWrapper = new TableSorter();
		euHistPermits = null;
		emusWrapper = new TableSorter();
		cesWrapper = new TableSorter();
		egpsWrapper = new TableSorter();
		notesWrapper = new TableSorter();
		notesFacWrapper = new TableSorter();
		facRolesWrapper = new TableSorter();
		facUserRoleAactivityWrapper = new TableSorter();
		addrHistWrapper = new TableSorter();
		euEmissionsWrapper = new TableSorter();
		fugComponentsWrapper = new TableSorter();
		pollutantsContWrapper = new TableSorter();
		facEmissionsWrapper = new TableSorter();
		mactSubpartsWrapper = new TableSorter();
		mactCompWrapper = new TableSorter();
		neshapsSubpartsWrapper = new TableSorter();
		nspsSubpartsWrapper = new TableSorter();
		sicCdsWrapper = new TableSorter();
		naicsCdsWrapper = new TableSorter();
		facOwnerContactsWrapper = new TableSorter();
		facilityCemComLimitsWrapper = new TableSorter();
		
		continuousMonitorsWrapper = new TableSorter();
		hydrocarbonAnalysisWrapper = new TableSorter();
		hydrocarbonAnalysisSampleDetailWrapper = new TableSorter();
		decanePropertiesWrapper = new TableSorter();
		useGlobalContactList = false;

		this.setTag(ValidationBase.FACILITY_TAG);
		for (int i = 0; i < MAX_NUM_COMPS; i++) {
			operation[i] = NO_OP;
		}
		branchingAirFlowMsg = SystemPropertyDef.getSystemPropertyValue("BRANCHING_AIR_FLOW_DESCRIPTION", null);
		controlEquipAttribDescMsg = SystemPropertyDef.getSystemPropertyValue("ER_CONTROL_EQUIP_ATTRIB_DESC", null);
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

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(
			InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public NaicsService getNaicsService() {
		return naicsService;
	}

	public void setNaicsService(NaicsService naicsService) {
		this.naicsService = naicsService;
	}

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public StackTestService getStackTestService() {
		return stackTestService;
	}

	public void setStackTestService(StackTestService stackTestService) {
		this.stackTestService = stackTestService;
	}

	protected final String euFacNodeMapId(String id) {
		return "E:" + id;
	}

	protected final String epFacNodeMapId(String id) {
		return "P:" + id;
	}

	protected final String ceFacNodeMapId(String id) {
		return "C:" + id;
	}

	protected final String egFacNodeMapId(String id) {
		return "G:" + id;
	}

	public final Integer getUserID() {
		return currentUserId;
	}

	protected String getUserName() {
		return InfrastructureDefs.getCurrentUserAttrs().getUserName();
	}

	public final boolean isCntEquipAssigned() {
		return facility.isCntEquipAssigned(null == controlEquipment ? null
				: controlEquipment.getFpNodeId());
	}

	public final boolean isEgressPointAssigned() {
		return facility.isEgressPointAssigned(egressPoint.getFpNodeId());
	}

	public final Integer getFpId() {
		return fpId;
	}

	public final void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public final String getFacilityId() {
		return facilityId;
	}

	public final boolean getEditable() {
		return editable;
	}

	public final void setEditable(boolean editable) {
		this.editable = editable;
	}

	public final boolean getEditable1() {
		return editable1;
	}

	public final void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}

	public final boolean isDisabledAddressUpdateButton() {
		if (facility != null
				&& InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			// Only allow updates to current and by Stars2Admin
			return false;
		}
		return true;
	}

	public final boolean isDisabledUpdateButton() {
		if (isPublicApp()) {
			return true;
		}
		if (isReadOnlyUser()) {
			return true;
		}

    	if (isPortalApp()) {
        	return staging ? false : true;
        }

		if (facility != null
				&& (facility.getVersionId() == -1 || InfrastructureDefs
						.getCurrentUserAttrs().isStars2Admin())) {
			return false;
		}

		return true;
	}

	public final boolean isDisableNoteButton() {
		boolean isAdmin = InfrastructureDefs.getCurrentUserAttrs()
				.isStars2Admin();

		if (isReadOnlyUser()) {
			return true;
		}

		if (isAdmin)
			return false;

		boolean isCreatedUser = facilityNote.getUserId().equals(getUserID());
		if (isCreatedUser)
			return false;

		return true;
	}

	public final boolean isReadOnlyNote() {
		if (!editable1) {
			return true;
		} else if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		} else if (!facilityNote.getUserId().equals(getUserID())) {
			return true;
		}
		return false;
	}

	public final String getRenderComponent() {
		return renderComps[renderComp];
	}

	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final void setEpaEmuId(String epaEmuId) {
		this.epaEmuId = epaEmuId;
	}

	public final String getCntEquipId() {
		return cntEquipId;
	}

	public final void setCntEquipId(String cntEquipId) {
		this.cntEquipId = cntEquipId;
	}

	public final String getEgrPntId() {
		return egrPntId;
	}

	public final void setEgrPntId(String egrPntId) {
		this.egrPntId = egrPntId;
	}

	public final FacilityReference getFacilityReference() {
		return facilityReference;
	}

	public final void setFacilityReference(FacilityReference facilityReference) {
		this.facilityReference = facilityReference;
	}

	public final FacilityVersion[] getFacilityHistory() {
		FacilityVersion[] facilities = null;

		try {
			facilities = getFacilityService().retrieveAllFacilityVersions(
					facilityId);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing facility history failed");
		}
		return facilities;
	}

	private void refreshNotes() {

		facilityNote = null;
		try {
			facilityNotes = getFacilityService().retrieveFacilityNotes(
					facilityId);
			this.facility.setNotes(new ArrayList<FacilityNote>(Arrays
					.asList(facilityNotes)));
			setNotesInFacility();
		} catch (RemoteException re) {
			handleException(re);
		}
	}

	protected final void setNotesInFacility() {
		if (this.isInternalApp()) {
			notesWrapper.setWrappedData(facilityNotes);
		}
		notesFacWrapper.setWrappedData(facilityNotes);
	}

	public final void setOtherParts() throws RemoteException {
		// Make sure and reset the tree, otherwise we have mixed data...
		facilityId = facility.getFacilityId();

		treeData = null;

		egressPointCemTable = null;

		addedneshapsRow = false;
		addedNspsRow = false;
		addedPsdRow = false;
		addedNsrRow = false;

		if (this.isInternalApp()) {
			facility.generateFacilityEmissions();
			facEmissionsWrapper.setWrappedData(facility.getFacilityEmissions());

			if (facRolesWrapper == null) {
				facRolesWrapper = new TableSorter();
			} else {
				facRolesWrapper.clearWrappedData();
			}
			facRolesWrapper.setWrappedData(facility.getFacilityRoles().values()
					.toArray(new FacilityRole[0]));

			// sort by name
			SortCriterion sc = new SortCriterion("facilityRoleDsc", true);

			List<SortCriterion> criteria = new ArrayList<SortCriterion>();

			criteria.add(sc);

			facRolesWrapper.setSortCriteria(criteria);

		}

		sicCds = Stars2Object.toStar2Object(facility.getSicCds());
		sicCdsWrapper = new TableSorter();
		sicCdsWrapper.setWrappedData(sicCds);
		naicsCds = Stars2Object.toStar2Object(facility.getNaicsCds());
		naicsCdsWrapper = new TableSorter();
		naicsCdsWrapper.setWrappedData(naicsCds);
		mactSubparts = Stars2Object.toStar2Object(facility.getMactSubparts());
		mactSubpartsWrapper = new TableSorter();
		mactSubpartsWrapper.setWrappedData(mactSubparts);
		mactCompWrapper = new TableSorter();
		mactComp = new ArrayList<Stars2Object>(1);
		mactComp.add(new Stars2Object(facility.getMactCompCd()));
		mactCompWrapper.setWrappedData(mactComp);
		neshapsSubparts = facility.getNeshapsSubpartsCompCds();
		neshapsSubpartsWrapper = new TableSorter();
		neshapsSubpartsWrapper.setWrappedData(neshapsSubparts);
		nspsSubparts = Stars2Object.toStar2Object(facility.getNspsSubparts());
		nspsSubpartsWrapper = new TableSorter();
		nspsSubpartsWrapper.setWrappedData(nspsSubparts);
		hydrocarbonAnalysisWrapper = new TableSorter();
		hydrocarbonAnalysisWrapper.setWrappedData(facility.getHydrocarbonPollutantList());
		//hydrocarbon analysis sample detail table
		setupSampleDetailWrapper();
		// hydrocarabon analysis decane table
		setupDecanePropertiesWrapper();
		
		if (multiEstabFacilities == null) {
			facility.setMultiEstabFacility(false);
		} else {
			facility.setMultiEstabFacility(true);
		}

		hideInvalidEUs = false;
		euHistPermits = null;
		emusWrapper = new TableSorter();
		emusWrapper.setWrappedData(facility.getEmissionUnits().toArray(
				new EmissionUnit[0]));
		cesWrapper = new TableSorter();
		cesWrapper.setWrappedData(facility.getControlEquips().toArray(
				new ControlEquipment[0]));
		egpsWrapper = new TableSorter();
		egpsWrapper.setWrappedData(facility.getEgressPoints());

		facilityNotes = facility.getNotes().toArray(new FacilityNote[0]);
		setNotesInFacility();

		infraDefs = (InfrastructureDefs) FacesUtil.getManagedBean("infraDefs");

		addrHistWrapper = new TableSorter();
		addrHistWrapper.setWrappedData(facility.getAddresses().toArray());

		// TODO Setup owner upon facility detail retrieval get owner.
		setFacilityOwner();

		facilityApisWrapper = new TableSorter();
		facilityApisWrapper.setWrappedData(facility.getApis());
		// this.initializeFacilityApis();
		
		facilityCemComLimitsWrapper = new TableSorter();
		facilityCemComLimitsWrapper.setWrappedData(facility.getFacilityCemComLimitList());
		
		continuousMonitorsWrapper = new TableSorter();
		continuousMonitorsWrapper.setWrappedData(facility.getContinuousMonitorList());
	}

	public void refreshProfileRemotely() {
		facility = null;
		getFacility();
	}

	public final Facility getFacility() {
		if (facility == null) {
			logger.debug("getFacility: facility is null" + ", fpId = " + fpId);
		}

		if ((facility == null && fpId != null)
				|| (fpId != null && !fpId.equals(facility.getFpId()))) {
			logger.debug("fpId = " + fpId + ", Staging = " + staging);
			try {
				facility = getFacilityService().retrieveFacilityProfile(fpId,
						staging);
				facProfDocuments = null;
				if (facility != null) {
					if (isInternalApp()) {
						// get a reference to the UndeliveredMail backend-bean
						// and
						// set its facilityID

						UndeliveredMail fRum = (UndeliveredMail) FacesUtil
								.getManagedBean("undeliveredMail");
						fRum.setFacility(facility);

						// Read the Enforcement/Compliance summary info
						String fproId = facility.getFacilityId();
						lastFCEDate = null;
						try {
							FullComplianceEval last = getFullComplianceEvalService()
									.retrieveLastCompletedFce(fproId);
							if (last == null || last.getDateEvaluated() == null)
								lastFCEDate = null;
							else
								lastFCEDate = last.getDateEvaluated();
						} catch (RemoteException re) {
							logger.error("setting lastFCEDate failed on "
									+ fproId, re);
						}

						fceScheduled = "Error";
						try {
							FullComplianceEval last = getFullComplianceEvalService()
									.retrieveLastScheduledFce(fproId);
							if (last == null
									|| last.getScheduledTimestamp() == null)
								fceScheduled = "Not Scheduled";
							else
								fceScheduled = CetaBaseDB.getScheduled(last
										.getScheduledTimestamp());
						} catch (RemoteException re) {
							logger.error("setting fceScheduled failed on "
									+ fproId, re);
						}

						lastSiteVisitDate = "Error";
						try {
							SiteVisit sv = getFullComplianceEvalService()
									.retrieveLastSiteVisit(fproId);
							if (sv == null || sv.getVisitDate() == null)
								lastSiteVisitDate = "Never Visited";
							else
								lastSiteVisitDate = sv.getVisitDateStr();
						} catch (RemoteException re) {
							logger.error("setting lastSiteVisitDate failed on "
									+ fproId, re);
						}

						lastStackTestDate = null;
						try {
							lastStackTestDate = getStackTestService()
									.retrieveLastStackTestDate(fproId);
						} catch (RemoteException re) {
							logger.error("setting lastStackTestDate failed on "
									+ fproId, re);
						}
					}

					multiEstabFacilities = getFacilityService()
							.retrieveMutliEstabFacilities(facility);
					setOtherParts();
					logger.debug("Number of EUs = "
							+ facility.getEmissionUnits().size());
				} else {
					logger.error("No facility found for fpId = " + fpId);
				}
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing facility failed");
			}
		} else {
			if (!facility.isCopyOnChange() || facility.getVersionId().equals(-1)){
				facility.synchronizeHydrocarbonPollutantList();
			}
			logger.debug("No need to re-query for facility.");
		}
		if (facility == null) {
			logger.error("getFacility is returning NULL!!!  FpId = " + fpId
					+ ", Staging = " + staging);
		}

		return facility;
	}

	public final void setFacility(Facility facility) {
		this.facility = facility;
		facProfDocuments = null;
	}

	public final String nodeClickedByUser() {
		String ret;
		if (selectedTreeNode.getType().equals("moreNodes")) {
			byClickEpaEmuId = null;
			selEpaEmuIds = null;
			expandOpt = 0;
			getEuTreeData(selEpaEmuIds, expandOpt);
			String allNumFacilityId = facility.getFacilityId().replace("F", "");
			selectedTreeNode = treeData.getNodeById(allNumFacilityId);
			current = facility.getFacilityId();
			ret = nodeClicked();
			return ret;
		}

		ret = nodeClicked();
		if (renderComp == EM_UNIT_COMP) {
			byClickEpaEmuId = emissionUnit.getEpaEmuId();
			selEpaEmuIds = new ArrayList<String>(0);
			selEpaEmuIds.add(byClickEpaEmuId);
			expandOpt = 0;
			getEuTreeData(selEpaEmuIds, expandOpt);
		} else if (renderComp == FACILITY_COMP) {
			byClickEpaEmuId = null;
			selEpaEmuIds = null;
			expandOpt = 0;
			getEuTreeData(selEpaEmuIds, expandOpt);
		}
		return ret;
	}

	public final String nodeClicked() {
		if (selectedTreeNode == null) {
			logger.error("selectedTreeNode is NULL!!!");
			return null;
		}
		Object facObject = (((Stars2TreeNode) selectedTreeNode).getUserObject());
		if (selectedTreeNode.getType().equals(FAC_NODE)) {
			renderComp = FACILITY_COMP;
			facility.generateFacilityEmissions();
		} else if (selectedTreeNode.getType().equals(EU_NODE)) {
			emissionUnit = (EmissionUnit) facObject;
			renderComp = EM_UNIT_COMP;
			euEmissionsWrapper = new TableSorter();
			euEmissionsWrapper.setWrappedData(emissionUnit.getEuEmissions());
			fugComponentsWrapper = new TableSorter();
			fugComponentsWrapper.setWrappedData(emissionUnit.getEmissionUnitType().getComponents());
		} else if (selectedTreeNode.getType().equals(EP_NODE)) {
			emissionProcess = (EmissionProcess) facObject;

			if (emissionProcess.getSccId() != null
					&& emissionProcess.getSccId().length() == SccCode.SCC_LEN) {
				try {
					emissionProcess.setSccCode(getFacilityService()
							.getFullSccCode(emissionProcess.getSccId()));
				} catch (RemoteException re) {
					logger.warn("Failed to retrieve SccCode based upon sccId="
							+ emissionProcess.getSccId(), re);
				}
			}
			renderComp = EM_PROC_COMP;
		} else if (selectedTreeNode.getType().equals(CE_NODE)) {
			controlEquipment = (ControlEquipment) facObject;
			buildContEquipTypeData();
			renderComp = CNT_EQUIP_COMP;
			pollutantsContWrapper = new TableSorter();
			pollutantsContWrapper.setWrappedData(controlEquipment
					.getPollutantsControlled());
		} else if (selectedTreeNode.getType().equals(EGP_NODE)) {
			egressPoint = (EgressPoint) facObject;
			renderComp = EG_POINT_COMP;
		} else if (selectedTreeNode.getType().equals(UACE_NODE)) {
			// for validation pop-up
			renderComp = FACILITY_COMP;
		} else if (selectedTreeNode.getType().equals(UAEGP_NODE)) {
			// for validation pop-up
			renderComp = FACILITY_COMP;
		}

		setEditable(false);

		return getFacilityProfileOutcome(); // stay on same page
	}

	public final EmissionUnit getEmissionUnit() {
		return emissionUnit;
	}

	public final void setEmissionUnit(EmissionUnit emissionUnit) {
		this.emissionUnit = emissionUnit;
	}

	public final EmissionProcess getEmissionProcess() {
		return emissionProcess;
	}

	public final void setEmissionProcess(EmissionProcess emissionProcess) {
		this.emissionProcess = emissionProcess;
	}

	public final ControlEquipment getControlEquipment() {
		return controlEquipment;
	}

	public final void setControlEquipment(ControlEquipment controlEquipment) {
		this.controlEquipment = controlEquipment;
	}

	public final EgressPoint getEgressPoint() {
		return egressPoint;
	}

	public final void setEgressPoint(EgressPoint egressPoint) {
		this.egressPoint = egressPoint;
	}

	public final String submitProfile() {
		popupRedirect = null;
		editable = false;
		editable1 = false;
		byClickEpaEmuId = null;
		expandOpt = 0;
		expandTree = false;
		selEpaEmuIds = null;
		epaEmuId = null;
		facility = null;
		treeData = null;
		getFacility();
		if (facility == null) {
			logger.error("Unexpected NULL pointer return from accesing existing facility from Database.");
			DisplayUtil
					.displayError("Facility is not found due to unexpected system problem; Please try again.");
		} else if (isPublicApp()) {
		
			getCurrentFacilityData();

			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_homeApplications")).setDisabled(false);
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_homeEmissionsInventories")).setDisabled(false);
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_homePermits")).setDisabled(false);
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_homeStackTestReports")).setDisabled(false);
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_homeComplianceReports")).setDisabled(false);
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_homeAmbientMonitoring"))
					.setDisabled(null == facility.getAssociatedMonitorGroup().getGroupId());
		}
		setDiscloseHC(false);
		return getFacilityProfileOutcome();
	}

	public final String excuteSubmitHistoryProfile() {
		facProfDocuments = null;

		return submitProfile();
	}

	private boolean useFpId;
	
	public boolean isUseFpId() {
		return useFpId;
	}

	public void setUseFpId(boolean useFpId) {
		this.useFpId = useFpId;
	}

	public final String submitProfileById() {
		
		if (!useFpId) {
			fpId = null;
		}
		popupRedirect = null;
		editable = false;
		editable1 = false;
		byClickEpaEmuId = null;
		epaEmuId = null;
		treeData = null;

		try {

			Facility fac = null;
			if (useFpId) {
				fac = getFacilityService().retrieveFacility(fpId);
			} else {
				fac = getFacilityService().retrieveFacility(facilityId);
			}

			useFpId = false;
			if (fac == null) {
				DisplayUtil.displayError("Accessing facility " + facilityId
						+ " failed");
				logger.error("Accessing facility " + facilityId + " failed");
				facilityId = null;
				return null;
			}
			fpId = fac.getFpId();
			
		} catch (RemoteException re) {
			handleException("Accessing facility " + facilityId + " failed", re);
			DisplayUtil.displayError("Accessing facility " + facilityId
					+ " failed");
			facilityId = null;
			return null;
		}

		return getFacilityProfileOutcome();
	}

	public final String submitProfileByFpId() {
		popupRedirect = null;
		editable = false;
		editable1 = false;
		byClickEpaEmuId = null;
		epaEmuId = null;
		treeData = null;
		String s = "Accessing facility with fpId " + fpId + " failed";
		try {
			Facility fac = getFacilityService().retrieveFacility(fpId);
			if (fac == null) {
				DisplayUtil.displayError(s);
				logger.error(s, new Exception());
				facilityId = null;
				return null;
			}
		} catch (RemoteException re) {
			handleException(s, re);
			DisplayUtil.displayError(s);
			facilityId = null;
			return null;
		}

		return getFacilityProfileOutcome();
	}

	public final String submitEmissionUnit() {

		selectedTreeNode = facNodeMap.get(euFacNodeMapId(epaEmuId));
		current = euFacNodeMapId(epaEmuId);
		nodeClicked();

		byClickEpaEmuId = epaEmuId;
		selEpaEmuIds = new ArrayList<String>(0);
		selEpaEmuIds.add(byClickEpaEmuId);
		expandOpt = 1;
		expandTree = false;
		getEuTreeData(selEpaEmuIds, expandOpt);
		return nodeClicked();
	}

	// This is called from EU Status Search results page.
	// Neither the current profile nor the EU is in to start with.
	public final String submitFacEmissionUnit() {
		popupRedirect = null;
		editable = false;
		editable1 = false;
		byClickEpaEmuId = null;
		expandOpt = 0;
		expandTree = false;
		selEpaEmuIds = null;
		facility = null;
		treeData = null;
		refreshFacility();
		if (facility == null) {
			logger.error("Unexpected NULL pointer return from accesing existing facility from Database.");
			DisplayUtil
					.displayError("Facility is not found due to unexpected system problem; Please try again.");
			return null; // do not change pages.
		}

		current = euFacNodeMapId(epaEmuId);
		selectedTreeNode = facNodeMap.get(current);
		if (selectedTreeNode == null) {
			DisplayUtil.displayError("Emission Unit " + epaEmuId
					+ " not found.");
			return null;
		}

		Object fObj = (((Stars2TreeNode) selectedTreeNode).getUserObject());
		if (fObj == null) {
			if (selectedTreeNode == null) {
				DisplayUtil.displayError("Emission Unit " + epaEmuId
						+ " not found.");
				return null;
			}
		}
		emissionUnit = (EmissionUnit) fObj;
		renderComp = EM_UNIT_COMP;
		euEmissionsWrapper = new TableSorter();
		euEmissionsWrapper.setWrappedData(emissionUnit.getEuEmissions());
		fugComponentsWrapper.setWrappedData(emissionUnit.getEmissionUnitType().getComponents());

		byClickEpaEmuId = epaEmuId;
		selEpaEmuIds = new ArrayList<String>(0);
		selEpaEmuIds.add(byClickEpaEmuId);
		expandOpt = 1;
		expandTree = false;
		getEuTreeData(selEpaEmuIds, expandOpt);
		return getFacilityProfileOutcome();
	}

	public final EmissionUnit[] getEmissionUnits() {
		return facility.getEmissionUnits().toArray(new EmissionUnit[0]);
	}

	public final String submitControlEquip() {
		byClickEpaEmuId = null;

		if (contEquipsEpaEuIds != null) {
			StringTokenizer tokenizer = new StringTokenizer(contEquipsEpaEuIds,
					",");
			selEpaEmuIds = new ArrayList<String>(0);
			String nextToken;

			while (tokenizer.hasMoreTokens()) {
				nextToken = tokenizer.nextToken();
				selEpaEmuIds.add(nextToken.trim());
			}
		}

		expandOpt = 2;
		expandTree = false;
		getEuTreeData(selEpaEmuIds, expandOpt);
		selectedTreeNode = facNodeMap.get(ceFacNodeMapId(cntEquipId));
		current = ceFacNodeMapId(cntEquipId);
		nodeClicked();
		return getFacilityProfileOutcome();
	}

	public final String submitEgressPoint() {
		byClickEpaEmuId = null;

		if (egrPointsEuIds != null) {
			StringTokenizer tokenizer = new StringTokenizer(egrPointsEuIds, ",");
			selEpaEmuIds = new ArrayList<String>(0);
			String nextToken;

			while (tokenizer.hasMoreTokens()) {
				nextToken = tokenizer.nextToken();
				selEpaEmuIds.add(nextToken.trim());
			}
		}

		expandOpt = 2;
		expandTree = false;
		getEuTreeData(selEpaEmuIds, expandOpt);
		selectedTreeNode = facNodeMap.get(egFacNodeMapId(egrPntId));
		current = egFacNodeMapId(egrPntId);
		nodeClicked();
		return getFacilityProfileOutcome();
	}

	public final HashMap<String, Object> getControlEquipsList() {
		HashMap<String, String> contEquipsList = new HashMap<String, String>();
		ControlEquipment[] contEquips = facility.getControlEquips().toArray(
				new ControlEquipment[0]);

		for (ControlEquipment tempCE : contEquips) {
			contEquipsList.put(tempCE.getControlEquipmentId(),
					tempCE.getControlEquipmentId());
		}

		return getSortedStringMap(contEquipsList);
	}

	public final HashMap<String, Object> getEgressPointsList() {
		HashMap<String, String> egrPointsList = new HashMap<String, String>();
		EgressPoint[] egrPoints = getEgressPoints();

		for (EgressPoint tempEGP : egrPoints) {
			egrPointsList.put(tempEGP.getReleasePointId(),
					tempEGP.getReleasePointId());
		}
		return getSortedStringMap(egrPointsList);
	}

	public final HashMap<String, Object> getControlEquipsRemoveList() {
		HashMap<String, String> contEquipsList = new HashMap<String, String>();
		ControlEquipment[] contEquips;

		if (selectedTreeNode.getType().equals(EP_NODE)) {
			contEquips = emissionProcess.getControlEquipments().toArray(
					new ControlEquipment[0]);
		} else {
			contEquips = controlEquipment.getControlEquips().toArray(
					new ControlEquipment[0]);
		}

		for (ControlEquipment tempCE : contEquips) {
			/*
			 * if (tempCE.getControlEquips().length > 0 ||
			 * tempCE.getEgressPoints().length > 0) { continue; }
			 */
			contEquipsList.put(tempCE.getControlEquipmentId(),
					tempCE.getControlEquipmentId());
		}

		return getSortedStringMap(contEquipsList);
	}

	public final HashMap<String, Object> getEgressPointsRemoveList() {
		HashMap<String, String> egrPointsList = new HashMap<String, String>();

		EgressPoint[] egrPoints;
		if (selectedTreeNode.getType().equals(EP_NODE)) {
			egrPoints = emissionProcess.getEgressPoints().toArray(
					new EgressPoint[0]);
		} else {
			egrPoints = controlEquipment.getEgressPoints().toArray(
					new EgressPoint[0]);
		}

		for (EgressPoint tempEGP : egrPoints) {
			egrPointsList.put(tempEGP.getReleasePointId(),
					tempEGP.getReleasePointId());
		}

		return getSortedStringMap(egrPointsList);
	}

	public final EgressPoint[] getEgressPoints() {
		return facility.getEgressPoints();
	}

	public final boolean isOperStatusUpdatable() {
		if (editable) {
			if (!OperatingStatusDef.SD.equals(facility.getOperatingStatusCd())) {
				return true;
			} else if (isInternalApp()) {
				if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
						|| !facility.getOrigOperatingStatusCd().equals(
								OperatingStatusDef.SD)) {
					return true;
				}
			}
		}
		return false;
	}

	public final void editFacility() {
		setEditable(true);
		facility.clearValidationMessages();
		oldFacOperStatus = facility.getOperatingStatusCd();
		operation[FACILITY_COMP] = EDIT_OP;
		addressChanging = false;
	}

	protected final void refreshFacility() {
		facility = null;
		treeData = null;
		facProfDocuments = null;
		getTreeData();
	}
	

	public String refreshFacilityProfile() {
		if (fpId != null) {
			TreeNode tempSelectedTreeNode = selectedTreeNode;
			String tempCurrent = current;
			refreshFacility();
			selectedTreeNode = tempSelectedTreeNode;
			current = tempCurrent;
			nodeClicked();
		}
		return FAC_OUTCOME;
	}

	protected final boolean validateFacility() {
		return validateFacility(false);
	}

	protected final boolean validateFacility(boolean passRulesRegs) {

		boolean isValid = true;

		try {

			List<ValidationMessage> msgs = FacilityValidation.validateFacility(facility, facility.getFpId(),
					facility.getEmissionUnits());
			ValidationMessage[] validationMessages = msgs.toArray(new ValidationMessage[0]);
			//ValidationMessage[] validationMessages = facility.validate(isInternalApp(), passRulesRegs);
			//if (validationMessages.length > 0) {
			//	for (int i = 0; i < validationMessages.length; i++) {
			//		msgs.add(validationMessages[i]);
			//	}
			//}

			if (addressChanging && facility.getStartDate() == null) {
				// Add in the validation message about missing new effective
				// address
				msgs.add(new ValidationMessage("effAddDate",
						"Attribute " + "Effective Date of New Address" + " is not set.",
						ValidationMessage.Severity.ERROR, "facility"));
				for (ValidationMessage vm : validationMessages) {
					msgs.add(vm);
				}
				validationMessages = msgs.toArray(new ValidationMessage[0]);
			}
			validationMessages = msgs.toArray(new ValidationMessage[0]);

			if (validationMessages.length > 0) {
				if (displayValidationMessages(FAC_CLIENT_ID + "facility:", validationMessages)) {
					isValid = false;
				}
				// needed because of nested Address validation
				facility.clearValidationMessages();
			}
		} catch (ValidationException ve) {
			logger.error("ValidationException caught: " + ve.getMessage());
			isValid = false;
		}

		return isValid;
	}

	protected final boolean validateFedRules() {
		boolean isValid = true;

		ValidationMessage[] validationMessages = facility.validateFedRules();
		if (validationMessages.length > 0) {
			if (displayValidationMessages(FAC_CLIENT_ID + "facility:",
					validationMessages)) {
				isValid = false;
			}
			// needed beacuse of nested Address validation
			facility.clearValidationMessages();
		}

		return isValid;
	}

	public final String saveFacilityFedRules() {
		boolean operationOK = true;
		boolean tvSmInds = false;

		if (facility.isTvInd()) {
			if (facility.isSmInd()) {
				tvSmInds = true;
			}
		} else if (facility.isSmInd()) {
			if (facility.isTvInd()) {
				tvSmInds = true;
			}
		}

		// needed to remove any old validation messages that are
		// no longer valid.
		facility.requiredFields();

		// Check that trying to delete entire table if not in compliance
		boolean complianceMismatch = false;

		String facAirProgCd = facility.getAirProgramCd();
		String facAirprogCompCd = facility.getAirProgramCompCd();
		if (tvSmInds) {
			DisplayUtil.displayError("Cannot set both subject to "
					+ AirProgramDef.printableValue(AirProgramDef.TITLE_V)
					+ " and "
					+ AirProgramDef.printableValue(AirProgramDef.SM_FESOP));
		} else if (facAirprogCompCd != null) {
			if (AirProgramDef.TITLE_V.equals(facAirProgCd)) {
				if (!facility.isTvInd()
						&& !ComplianceStatusDef.YES.equals(facAirprogCompCd)) {
					DisplayUtil.displayError("Cannot remove Subject to "
							+ AirProgramDef
									.printableValue(AirProgramDef.TITLE_V)
							+ " while it is not in compliance");
					complianceMismatch = true;
				}
			} else if (AirProgramDef.SM_FESOP.equals(facAirProgCd)) {
				if (!facility.isSmInd()
						&& !ComplianceStatusDef.YES.equals(facAirprogCompCd)) {
					DisplayUtil.displayError("Cannot remove Subject to "
							+ AirProgramDef
									.printableValue(AirProgramDef.SM_FESOP)
							+ " while it is not in compliance");
					complianceMismatch = true;
				}
			}
		}

		if (!facility.isMact()) {
			if (!ComplianceStatusDef.YES.equals(facility.getMactCompCd())) {
				DisplayUtil
						.displayError("Cannot remove Subject to MACT while not in compliance");
				complianceMismatch = true;
			}
		} else {
			// check for deprecated
			for (String s : Stars2Object.fromStar2Object(mactSubparts)) {
				try {
					PTIOMACTSubpartDef pd = PTIOMACTSubpartDef.getSubpartDef(s);
					if (pd != null && pd.isDeprecated()) {
						DisplayUtil.displayWarning("Subpart " + s
								+ " is no longer a valid MACT Subpart");
					}

				} catch (ApplicationException ae) {
				}
			}
		}

		if (!facility.isNeshaps()) {
			for (PollutantCompCode pcc : neshapsSubparts) {
				if (!ComplianceStatusDef.YES.equals(pcc.getPollutantCompCd())) {
					complianceMismatch = true;
					DisplayUtil
							.displayError("Cannot remove NESHAPS subparts which are not in compliance");
					break;
				}
			}
		} else {
			// check for deprecated
			for (PollutantCompCode pcc : neshapsSubparts) {
				try {
					PTIONESHAPSSubpartDef pd = PTIONESHAPSSubpartDef
							.getSubpartDef(pcc.getPollutantCd());
					if (pd != null && pd.isDeprecated()) {
						DisplayUtil.displayWarning("Subpart " + pd.getCode()
								+ " is no longer a valid NESHAPS Subpart");
					}

				} catch (ApplicationException ae) {
				}
			}
		}

		if (facility.isNsps()) {
			// check for deprecated
			for (String s : Stars2Object.fromStar2Object(nspsSubparts)) {
				try {
					PTIONSPSSubpartDef pd = PTIONSPSSubpartDef.getSubpartDef(s);
					if (pd != null && pd.isDeprecated()) {
						DisplayUtil.displayWarning("Subpart " + pd.getCode()
								+ " is no longer a valid NSPS Subpart");
					}

				} catch (ApplicationException ae) {
				}
			}
		}

		if (isInternalApp() && (complianceMismatch || tvSmInds))
			return "Fail";

		if (facility.getAirProgramCompCd() == null
				|| facility.getAirProgramCompCd().equals("")) {
			facility.setAirProgramCompCd(ComplianceStatusDef.YES);
		}

		if (facility.isTvInd()) {
			facility.setAirProgramCd(AirProgramDef.TITLE_V);
		} else if (facility.isSmInd()) {
			facility.setAirProgramCd(AirProgramDef.SM_FESOP);
		} else {
			facility.setAirProgramCd(null);
		}

		facility.setMactSubparts(Stars2Object
				.fromStar2Object(this.mactSubparts));
		facility.setNeshapsSubpartsCompCds(neshapsSubparts);
		facility.setNspsSubparts(Stars2Object
				.fromStar2Object(this.nspsSubparts));

		if (isInternalApp() && !validateFedRules()) {
			return "Fail";
		}

		setEditable(false);
		operation[FACILITY_COMP] = NO_OP;

		try {
			getFacilityService()
					.modifyFacilityFedRules(facility, currentUserId);
			// in case fpId is changed for copy on change
			fpId = facility.getFpId();

		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		refreshFacility();
		if (operationOK) {

			DisplayUtil.displayInfo("Facility rules & regs saved successfully");
			return SUCCESS;
		}
		DisplayUtil.displayError("Saving facility rules & regs failed");
		return "Fail";
	}

	private void handleException(RemoteException re, String message) {
		if (re != null) {
			if (re instanceof DataStoreConcurrencyException) {
				DisplayUtil
						.displayError(message
								+ " - more than one user was attempting to edit and save facility information at one time. Please re-enter your changes and submit again.");
				logger.warn(re.getMessage(), re);
			} else {
				DisplayUtil
						.displayError(message
								+ " -  system error has occurred. Please contact System Administrator.");
				logger.error(re.getMessage(), re);
			}
		}
	}

	public final String saveFacilityProfile() {
		boolean operationOK = true;
		boolean remoteException = false;

		facility.setSicCds(Stars2Object.fromStar2Object(this.sicCds));
		facility.setNaicsCds(Stars2Object.fromStar2Object(this.naicsCds));
		// re-calculate column totals when saving facility profile edit
		facility.calculateFacHCAnalysisTotal(); 
		updateFacHydrocarbonAnalysisSampleDetail();
		updateDecanePropertiesFromWrapper();
		
		if (!validateFacility(true)) {
			return getFacilityProfileOutcome();
		}

		setEditable(false);
		addressChanging = false;
		operation[FACILITY_COMP] = NO_OP;
		try {
			if (!getFacilityService().modifyFacility(facility, currentUserId)) {
				DisplayUtil
						.displayInfo("The facility submission to FR as result of your update failed.");
			}

			// in case fpId is changed for copy on change
			fpId = facility.getFpId();

		} catch (RemoteException re) {
			handleException(re, "Saving facility data failed");
			operationOK = false;
			remoteException = true;
		}

		refreshFacility();
		selectedTreeNode = facNode;
		current = facilityId;
		if (operationOK) {
			DisplayUtil.displayInfo("Facility data saved successfully");
		} else {
			if (!remoteException) {
				DisplayUtil.displayError("Saving facility data failed");
			}
		}
		return getFacilityProfileOutcome();
	}

	public final boolean isEuOperStatusUpdatable() {
		boolean ret = false;

		if (editable) {
			if (!(EuOperatingStatusDef.SD.equals(emissionUnit
					.getOperatingStatusCd()))) {
				return true;
			} else if (isInternalApp()) {
				if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
						|| !emissionUnit.getOrigOperatingStatusCd().equals(
								OperatingStatusDef.SD)) {
					return true;
				}
			} else if (!emissionUnit.getOrigOperatingStatusCd().equals(
					OperatingStatusDef.SD)) {
				return true;
			}
		}
		return ret;
	}

	public final boolean isConfirmEuShutDown() {
		if (isInternalApp()) {
			return false;
		} else if (editable
				&& !OperatingStatusDef.SD.equals(emissionUnit
						.getOrigOperatingStatusCd())
				&& OperatingStatusDef.SD.equals(emissionUnit
						.getOperatingStatusCd())) {
			return true;
		}
		return false;
	}

	public final boolean isNormalEuShutDown() {
		if (isInternalApp()) {
			return editable;
		} else if (editable
				&& (OperatingStatusDef.SD.equals(emissionUnit
						.getOrigOperatingStatusCd()) || !OperatingStatusDef.SD
						.equals(emissionUnit.getOperatingStatusCd()))) {
			return true;
		}
		return false;
	}

	public final boolean isAllowEuRemoval() {
		boolean okToRemove = false;
		if (facility != null && facility.getVersionId() == -1
				&& isPortalApp() && emissionUnit != null
				&& emissionUnit.getEpaEmuId() != null
				&& emissionUnit.getEpaEmuId().startsWith("TMP")) {
			EmissionUnit[] pastEUs;
			try {
				pastEUs = getFacilityService()
						.retrieveEmissionUnitsFromPastProfiles(
								emissionUnit.getCorrEpaEmuId(),
								facility.getFacilityId());
				if (pastEUs == null || pastEUs.length == 0) {
					okToRemove = true;
				}
			} catch (RemoteException e) {
				logger.error(
						"Exception looking for EU information in past profiles for epaEmuId = "
								+ emissionUnit.getCorrEpaEmuId(), e);
			}
		}
		return okToRemove;
	}

	public final void createEmissionUnit() {
		emissionUnit = new EmissionUnit();
		emissionUnit.setFpId(fpId);
		setEditable(true);
		renderComp = EM_UNIT_COMP;
		operation[EM_UNIT_COMP] = CREATE_OP;
		euEmissionsWrapper.setWrappedData(emissionUnit.getEuEmissions());
		fugComponentsWrapper.setWrappedData(emissionUnit.getEmissionUnitType().getComponents());
	}

	// called from:
	// - portal clone facility
	public final void cloneEmissionUnit(boolean interFacilityClone) {
		emissionUnit1 = emissionUnit;
		emissionUnit = emissionUnit1.cloneEmissionUnit(isInternalApp());
		emissionUnit.setFpId(fpId);
		emissionUnit.setEpaEmuId(null);
		emissionUnit.setWiseViewId(null);
		
		if (interFacilityClone) {
			emissionUnit.setEuInitInstallDate(null);
			emissionUnit.setEuInitStartupDate(null);
			emissionUnit.setEuInstallDate(null);
			emissionUnit.setEuStartupDate(null);
			emissionUnit.setEuShutdownNotificationDate(null);
			emissionUnit.setEuShutdownDate(null);
			emissionUnit.setDapcDescription(null);
		}
		setEditable(true);
		renderComp = EM_UNIT_COMP;
		operation[EM_UNIT_COMP] = CLONE_OP;
		euEmissionsWrapper.setWrappedData(emissionUnit.getEuEmissions());
		fugComponentsWrapper.setWrappedData(emissionUnit.getEmissionUnitType().getComponents());
	}
	// called from:
	// - internal eu clone
	// - portal eu clone
	public final void cloneEmissionUnit() {
		cloneEmissionUnit(false);
	}

	public final void editEmissionUnit() {
		setEditable(true);
		emissionUnit.clearValidationMessages();
		operation[EM_UNIT_COMP] = EDIT_OP;
		saveObjId = emissionUnit.getEpaEmuId();
	}

	protected final boolean validateEmissionUnit() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		ValidationMessage[] validationMessages2;

		try {
			validationMessages = getFacilityService().validateEmissionUnit(
					emissionUnit, facility, isInternalApp());
			
			String pageViewId = FAC_CLIENT_ID + "emissionUnit:";
			if (displayValidationMessages(pageViewId, validationMessages)) {
				isValid = false;
			}
			
			// if eu operating status is changed to invalid then check if there are any
			// references to this eu in other objects
			if (operation[EM_UNIT_COMP] == EDIT_OP
					&& emissionUnit.getOperatingStatusCd().equalsIgnoreCase(EuOperatingStatusDef.IV)) {
				validationMessages2 = checkReferencesToEu(emissionUnit);

				if (displayValidationMessages(pageViewId, validationMessages2)) {
					isValid = false;
				}
			}
			
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Accessing Emission Unit failed for validation");
			isValid = false;
		}

		return isValid;
	}

	public final String saveEmissionUnit() {
		return saveEmissionUnit(true);
	}
	
	public final String saveEmissionUnit(boolean validate) {
		boolean operationOK = true;

		if (validate && !validateEmissionUnit()) {
			return getFacilityProfileOutcome();
		}

		setEditable(false);

		try {
			if (operation[EM_UNIT_COMP] == EDIT_OP) {
				setNullforUnusedFields();
				byClickEpaEmuId = getFacilityService().modifyEmissionUnit(emissionUnit, currentUserId);

				DisplayUtil.displayInfo("Emission unit updated successfully");
			} else if (operation[EM_UNIT_COMP] == DELETE_OP) {
				// delete just return for now
				return getFacilityProfileOutcome();
			} else if (operation[EM_UNIT_COMP] == CREATE_OP) {
				// new Emission Unit
				setNullforUnusedFields();
				emissionUnit = getFacilityService().createEmissionUnit(emissionUnit, currentUserId);

				byClickEpaEmuId = emissionUnit.getEpaEmuId();

				DisplayUtil.displayInfo("Emission unit: "
						+ emissionUnit.getEpaEmuId() + " added successfully");
			} else {
				// called from:
				// - internal eu clone
				// - portal eu clone
//				if (isPortalApp()) {
//					emissionUnit.setEuInitInstallDate(null);
//					emissionUnit.setEuInitStartupDate(null);
//					emissionUnit.setEuInstallDate(null);
//					emissionUnit.setEuStartupDate(null);
//					emissionUnit.setEuShutdownNotificationDate(null);
//					emissionUnit.setEuShutdownDate(null);
//				}
				setNullforUnusedFields();
				emissionUnit = getFacilityService().createEmissionUnit(emissionUnit, currentUserId);
				DisplayUtil.displayInfo("Emission unit cloned successfully");
			}
			// in case fpId is changed for copy on change
			fpId = emissionUnit.getFpId();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			if (emissionUnit.getActivePermits().length > 0) {
				String message;
				for (Permit tempPerm : emissionUnit.getActivePermits()) {
					message = new String(
							"INFO: Shut down Emission Unit has active permit. (Permit Number : "
									+ tempPerm.getPermitNumber()
									+ " Permit Type : "
									+ tempPerm.getPermitType() + " ).");
					DisplayUtil.displayInfo(message);
				}
			}

			refreshFacility();

			byClickEpaEmuId = emissionUnit.getEpaEmuId();

			selectedTreeNode = facNodeMap.get(euFacNodeMapId(byClickEpaEmuId));
			current = euFacNodeMapId(byClickEpaEmuId);
			nodeClicked();

			selEpaEmuIds = new ArrayList<String>(0);
			selEpaEmuIds.add(byClickEpaEmuId);
			expandOpt = 1;
			expandTree = false;
			getEuTreeData(selEpaEmuIds, expandOpt);

			return nodeClicked();
		}

		DisplayUtil.displayError("Adding or updating emission unit failed");
		return cancelEmissionUnit();
	}

	public final String cancelEmissionUnit() {
		this.fpId = this.emissionUnit.getFpId();
		setEditable(false);
		euEmissionsWrapper = new TableSorter();
		fugComponentsWrapper = new TableSorter();
		refreshFacility();

		if (operation[EM_UNIT_COMP] == EDIT_OP) {
			selectedTreeNode = facNodeMap.get(euFacNodeMapId(saveObjId));
			current = euFacNodeMapId(saveObjId);
		} else if (operation[EM_UNIT_COMP] == CLONE_OP) {
			selectedTreeNode = facNodeMap.get(euFacNodeMapId(emissionUnit1
					.getEpaEmuId()));
			current = euFacNodeMapId(emissionUnit1.getEpaEmuId());
		} else {
			selectedTreeNode = facNode;
			current = facilityId;
		}

		operation[EM_UNIT_COMP] = NO_OP;
		return nodeClicked();
	}

	public final String cancelEmissionProc() {
		setEditable(false);
		cancelLabel = CANCEL;
		refreshFacility();

		selectedTreeNode = facNodeMap.get(epFacNodeMapId(saveObjId));
		current = epFacNodeMapId(saveObjId);

		operation[EM_PROC_COMP] = NO_OP;
		return nodeClicked();
	}

	public final String cancelControlEquipment() {
		cancelLabel = CANCEL;
		return cancelControlEquipment(true);
	}

	private final String cancelControlEquipment(boolean cancelButton) {
		setEditable(false);
		pollutantsContWrapper = new TableSorter();
		refreshFacility();

		if (operation[CNT_EQUIP_COMP] == EDIT_OP) {
			selectedTreeNode = facNodeMap.get(ceFacNodeMapId(saveObjId));
			current = ceFacNodeMapId(saveObjId);
		} else if (cancelButton && operation[CNT_EQUIP_COMP] == CLONE_OP) {
			selectedTreeNode = facNodeMap.get(ceFacNodeMapId(saveObjId));
			current = ceFacNodeMapId(saveObjId);
		} else {
			selectedTreeNode = facNode;
			current = facilityId;
		}

		operation[CNT_EQUIP_COMP] = NO_OP;
		return nodeClicked();
	}

	public final String cancelEgressPoint() {
		return cancelEgressPoint(true);
	}

	private final String cancelEgressPoint(boolean cancelButton) {
		setEditable(false);
		refreshFacility();

		if (operation[EG_POINT_COMP] == EDIT_OP) {
			selectedTreeNode = facNodeMap.get(egFacNodeMapId(saveObjId));
			current = egFacNodeMapId(saveObjId);
		} else if (operation[EG_POINT_COMP] == CLONE_OP) {
			selectedTreeNode = facNodeMap.get(egFacNodeMapId(saveObjId));
			current = egFacNodeMapId(saveObjId);
		} else {
			selectedTreeNode = facNode;
			current = facilityId;
		}

		operation[EG_POINT_COMP] = NO_OP;
		return nodeClicked();
	}

	public final String cancelAddControlEquipment() {
		setEditable(false);

		if (selectedTreeNode.getType().equals(EP_NODE)) {
			refreshFacility();
			selectedTreeNode = facNodeMap.get(epFacNodeMapId(emissionProcess
					.getProcessId()));
			current = epFacNodeMapId(emissionProcess.getProcessId());
		} else if (selectedTreeNode.getType().equals(CE_NODE)) {
			refreshFacility();
			selectedTreeNode = facNodeMap.get(ceFacNodeMapId(controlEquipment
					.getControlEquipmentId()));
			current = ceFacNodeMapId(controlEquipment.getControlEquipmentId());
		} else {
			logger.error("Internal error: invalid selected node");
		}

		return nodeClicked();
	}

	public final String cancelAddEgressPoint() {
		setEditable(false);

		if (selectedTreeNode.getType().equals(EP_NODE)) {
			refreshFacility();
			selectedTreeNode = facNodeMap.get(epFacNodeMapId(emissionProcess
					.getProcessId()));
			current = epFacNodeMapId(emissionProcess.getProcessId());
		} else if (selectedTreeNode.getType().equals(CE_NODE)) {
			refreshFacility();
			selectedTreeNode = facNodeMap.get(ceFacNodeMapId(controlEquipment
					.getControlEquipmentId()));
			current = ceFacNodeMapId(controlEquipment.getControlEquipmentId());
		} else {
			logger.error("Internal error: invalid selected node");
		}

		return nodeClicked();
	}

	public final String cancelEdit() {
		setEditable(false);
		setEditable1(false);
		facRolesWrapper = null;
		refreshFacility();
		return CANCELLED;
	}

	public final String cancelEditFacility() {
		cancelEdit();
		selectedTreeNode = facNode;
		current = facilityId;
		addressChanging = false;
		return getFacilityProfileOutcome();
	}

	public final String cloneControlEquipment() {
		operation[CNT_EQUIP_COMP] = CLONE_OP;
		controlEquipment.setFpNodeId(null);
		controlEquipment.setCorrelationId(null);
		controlEquipment.setControlEquipmentId(null);
		controlEquipment.setWiseViewId(null);
		controlEquipment.setCeId(0); //Setting Ceid to 0 for cloned CE, so DAO generates a new ce id.
		for (PollutantsControlled pc : controlEquipment
				.getPollutantsControlled()) {
			pc.setFpNodeId(null);
		}
		controlEquipment.setContEquipInstallDate(null);
		if (isPortalApp()) {
			controlEquipment.setDapcDesc(null);
		}
		return internalCreateControlEquipment(controlEquipment);
	}

	public final String createControlEquipment() {
		ControlEquipment newCe = new ControlEquipment();
		operation[CNT_EQUIP_COMP] = CREATE_OP;
		controlEquipmentTemp = controlEquipment;
		return internalCreateControlEquipment(newCe);
	}

	private final String internalCreateControlEquipment(ControlEquipment ce) {
		controlEquipment = ce;
		controlEquipment.setFpId(fpId);
		setEditable(true);
		renderComp = CNT_EQUIP_COMP;
		pollutantsContWrapper = new TableSorter();
		pollutantsContWrapper.setWrappedData(controlEquipment
				.getPollutantsControlled());
		buildContEquipTypeData();
		return getFacilityProfileOutcome();
	}

	protected final boolean validateControlEquipment() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		try {
			validationMessages = getFacilityService().validateControlEquipment(
					controlEquipment);
			if (displayValidationMessages(FAC_CLIENT_ID + "cntEquip:",
					validationMessages)) {
				isValid = false;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Accessing Control Equipment failed for validation");
			isValid = false;
		}

		return isValid;
	}
	
	public final String saveControlEquipment() {
		return saveControlEquipment(true);
	}	

	public final String saveControlEquipment(boolean validate) {
		LinkedHashMap<String, String> tdata = BuildComponent
				.getDataToHashMap(contEquipTypeData);

		if (ceDataDetails != null) {
			for (DataDetail detail : ceDataDetails) {
				detail.setDataDetailVal(tdata.get(detail.getDataDetailLbl()));
			}
		}
		boolean operationOK = true;
		Boolean isCreate = false;
		if (validate) {
			ValidationMessage[] vMsgs = controlEquipment.validate();
			if (vMsgs.length > 0) {
				if (displayValidationMessages(FAC_CLIENT_ID + "cntEquip:", vMsgs)) {
					DisplayUtil
							.displayError("Creating or updating control equipment failed");
					return getFacilityProfileOutcome();
				}
			}
		}
		cancelLabel = CANCEL;

		if (validate && !validateControlEquipment()) {
			double needEdit = isUnevenPercentValuesNot100(controlEquipment
					.getCeEmissionFlows());
			if (needEdit != (double) FacilityBO.MISSING_FLOW_PERCENT) {
				cancelLabel = PROPORTIONAL_PERCENT_MSG;
			}
			return getFacilityProfileOutcome();
		}

		setEditable(false);
		try {
			if (operation[CNT_EQUIP_COMP] == EDIT_OP) {
				for (FacilityEmissionFlow fef : controlEquipment
						.getCeEmissionFlows()) {
					if (fef.getPercent() != null
							&& fef.getPercent().length() > 0) {
						fef.setFlowFactor(fef.getPercentValue());
					} else {
						fef.setFlowFactor(FacilityBO.MISSING_FLOW_PERCENT);
					}
				}
				getFacilityService().modifyControlEquipment(controlEquipment,
						currentUserId);
				saveObjId = controlEquipment.getControlEquipmentId();
				DisplayUtil
						.displayInfo("Control Equipment updated successfully");
			} else if (operation[CNT_EQUIP_COMP] == CLONE_OP) {
				controlEquipment = getFacilityService().cloneControlEquipment(
						controlEquipment, currentUserId);
				
				
				
				DisplayUtil
						.displayInfo("Control Equipment cloned successfully");
			} else if (operation[CNT_EQUIP_COMP] == DELETE_OP) {
				// delete; just return for now
				return getFacilityProfileOutcome();
			} else {
				Facility newFacility = getFacilityService()
						.copyFacilityProfile(controlEquipment.getFpId(),
								new Timestamp(System.currentTimeMillis()),
								currentUserId);
				if (newFacility == null) {
					DAOException e = new DAOException(
							"Cannot access facility to create control equipment.");
					throw e;
				}

				if (!newFacility.getFpId().equals(controlEquipment.getFpId())) {
					controlEquipment.setFpId(newFacility.getFpId());
					this.copyOnChangeFpNodeIds = newFacility
							.getCopyOnChangeFpNodeIds();
				}

				isCreate = true;
				// new Control Equipment
				controlEquipment = getFacilityService().cloneControlEquipment(
						controlEquipment, currentUserId);
				DisplayUtil
						.displayInfo("Control Equipment created successfully");
			}

			// in case fpId is changed for copy on change
			fpId = controlEquipment.getFpId();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		// Link if create by EP_NODE or CE_NODE
		if (isCreate
				&& (selectedTreeNode.getType().equals(EP_NODE) || selectedTreeNode
						.getType().equals(CE_NODE))) {
			cntEquipId = NEW_CNTEQUIP_ID;
			// Set the selected CE and set new CE to CEtemp
			ControlEquipment temp = controlEquipment;
			controlEquipment = controlEquipmentTemp;
			controlEquipmentTemp = temp;
			saveAddControlEquipment();
		} else {
			copyOnChangeFpNodeIds = null;
		}

		// reuse cancel code to position to parent node
		cancelControlEquipment(false);

		if (!operationOK) {
			DisplayUtil
					.displayError("Creating or updating control equipment failed");
		}
		return getFacilityProfileOutcome();
	}

	public final void cloneEgressPoint() {
		operation[EG_POINT_COMP] = CLONE_OP;
		egressPoint.setFpNodeId(null);
		egressPoint.setCorrelationId(null);
		egressPoint.setReleasePointId(null);
		egressPoint.setBaseElevation(null);
		egressPoint.setLatitudeNum(null);
		egressPoint.setLongitudeNum(null);
		egressPoint.setAqdWiseEgressPointId(null);
		if (isPortalApp()) {
			egressPoint.setDapcDesc(null);
		}
		for (EgressPointCem epc : egressPoint.getCems()) {
			epc.setFpNodeId(null);
		}
		internalCreateEgressPoint(egressPoint);
	}

	public final void createEgressPoint() {
		EgressPoint newEp = new EgressPoint();
		operation[EG_POINT_COMP] = CREATE_OP;
		internalCreateEgressPoint(newEp);
	}

	private final void internalCreateEgressPoint(EgressPoint ep) {
		egressPoint = ep;
		egressPoint.setFpId(fpId);
		setEditable(true);
		renderComp = EG_POINT_COMP;
	}

	protected final boolean validateEgressPoint() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;

		try {
			ArrayList<ValidationMessage> ml = new ArrayList<ValidationMessage>();
			if (egressPoint.getLatitudeNum() != null
					&& egressPoint.getLongitudeNum() != null) {
				validationMessages = getFacilityService()
						.validateEgressPointLatLong(egressPoint, facility);
				for (ValidationMessage msg : validationMessages) {
					ml.add(msg);
				}
			}
			validationMessages = egressPoint.validate(false);
			for (ValidationMessage msg : validationMessages) {
				ml.add(msg);
			}
			validationMessages = ml.toArray(new ValidationMessage[0]);
			if (displayValidationMessages(FAC_CLIENT_ID + "releasePoint:",
					validationMessages)) {
				isValid = false;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Accessing Release Point failed for validation");
			isValid = false;
		}
		return isValid;
	}

	public final String saveEgressPoint() {
		return saveEgressPoint(true);
	}
	
	public final String saveEgressPoint(boolean validate) {
		boolean operationOK = true;

		if (validate && !validateEgressPoint()) {
			return getFacilityProfileOutcome();
		}

		setEditable(false);

		try {
			if (operation[EG_POINT_COMP] == EDIT_OP) {
				getFacilityService().modifyEgressPoint(egressPoint,
						currentUserId);
				saveObjId = egressPoint.getReleasePointId();
				DisplayUtil.displayInfo("Release point updated successfully");
			} else if (operation[EG_POINT_COMP] == DELETE_OP) {
				// delete; just return for now
				return getFacilityProfileOutcome();
			} else {
				// new Release Point
				Facility newFacility = getFacilityService()
						.copyFacilityProfile(fpId,
								new Timestamp(System.currentTimeMillis()),
								currentUserId);
				if (newFacility == null) {
					DAOException e = new DAOException(
							"Cannot access facility to create release point.");
					throw e;
				}
				if (!newFacility.getFpId().equals(egressPoint.getFpId())) {
					egressPoint.setFpId(newFacility.getFpId());
					this.copyOnChangeFpNodeIds = newFacility
							.getCopyOnChangeFpNodeIds();
				}

				egressPoint = getFacilityService().createEgressPoint(
						egressPoint, currentUserId);
				DisplayUtil.displayInfo("Release point created successfully");
			}
			// in case fpId is changed for copy on change
			fpId = egressPoint.getFpId();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		// Link if create by EP_NODE or CE_NODE
		if (null != selectedTreeNode 
				&& (selectedTreeNode.getType().equals(EP_NODE)
						|| selectedTreeNode.getType().equals(CE_NODE))) {
			egrPntId = NEW_EGRPNT_ID;
			egressPointTemp = egressPoint;
			saveAddEgressPoint(!isPortalClone());
		} else {
			this.copyOnChangeFpNodeIds = null;
		}

		// reuse cancel code to position to parent node
		cancelEgressPoint(false);

		if (!operationOK) {
			DisplayUtil
					.displayError("Creating or updating release point failed");
		}
		return getFacilityProfileOutcome();
	}

	private boolean isPortalClone() {
		return (isPortalApp() && CLONE_OP == operation[EG_POINT_COMP]);
	}
	
	public final void addEmissionProcess() {
		emissionProcess = new EmissionProcess();
		emissionProcess.setFpId(fpId);
		emissionProcess.setEmissionUnitId(emissionUnit.getEmuId());
		operation[EM_PROC_COMP] = CREATE_OP;
		setEditable(true);
		setSelectSccMethod("inputSCC");
		renderComp = EM_PROC_COMP;
	}

	public final void cloneEmissionProcess(EmissionProcess ep) {
		emissionProcess = ep;
		addEmissionProcess();
		emissionProcess.setEmissionProcessNm(ep.getEmissionProcessNm());
		emissionProcess.setProcessName(ep.getProcessName());
		emissionProcess.setSccCode(ep.getSccCode());
	}

	public final void editEmissionProcess() {
		setEditable(true);
		setSelectSccMethod("inputSCC");
		emissionProcess.clearValidationMessages();
		operation[EM_PROC_COMP] = EDIT_OP;
		saveObjId = emissionProcess.getProcessId();
		if (emissionProcess.getSccId() != null) {
			emissionProcess.setOldSccId(new String(emissionProcess.getSccId()));
		} else {
			emissionProcess.setOldSccId(null);
		}
	}

	protected final boolean validateEmissionProcess() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;

		if (selectSccMethod.equals("inputSCC")) {
			emissionProcess.getSccCode().clearInputSccValidMessages();
		} else if (selectSccMethod.equals("searchSCC")) {
			emissionProcess.getSccCode().clearSearchSccValidMessages();
			if (emissionProcess.getSccId() == null) {
				DisplayUtil
						.displayError("SCC code is not selected. Select a SCC by searching SCC codes.");
				return false;
			}
		} else {
			emissionProcess.getSccCode().clearLevelSccValidMessages();
		}

		try {
			validationMessages = getFacilityService().validateEmissionProcess(
					emissionProcess);
			if (displayValidationMessages(FAC_CLIENT_ID + "emissionProc:",
					validationMessages)) {
				isValid = false;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Accessing Emission Process failed for validation");
			isValid = false;
		}

		return isValid;
	}
	public final String saveEmissionProcess() {
		return saveEmissionProcess(true);
	}

	public final String saveEmissionProcess(boolean validate) {
		boolean operationOK = true;
		cancelLabel = CANCEL;

		if (validate && !validateEmissionProcess()) {
			double needEdit = isUnevenPercentValuesNot100(emissionProcess
					.getEpEmissionFlows());
			if (needEdit != (double) FacilityBO.MISSING_FLOW_PERCENT) {
				cancelLabel = PROPORTIONAL_PERCENT_MSG;
			}
			return getFacilityProfileOutcome();
		}

		setEditable(false);
		try {
			if (operation[EM_PROC_COMP] == EDIT_OP) {
				// copy flow percents to flow factors
				for (FacilityEmissionFlow fef : emissionProcess
						.getEpEmissionFlows()) {
					if (fef.getPercent() != null
							&& fef.getPercent().length() > 0) {
						fef.setFlowFactor(fef.getPercentValue());
					} else {
						fef.setFlowFactor(FacilityBO.MISSING_FLOW_PERCENT);
					}
				}
				getFacilityService().modifyEmissionProcess(emissionProcess,
						currentUserId);
				saveObjId = emissionProcess.getProcessId();
				DisplayUtil
						.displayInfo("Emission process updated successfully");
			} else if (operation[EM_PROC_COMP] == DELETE_OP) {
				// delete; just return for now
			} else {
				// new Emission Process

				emissionProcess = getFacilityService().createEmissionProcess(
						emissionProcess, currentUserId);
				DisplayUtil.displayInfo("Emission process Added successfully");
				saveObjId = emissionProcess.getProcessId();
			}
			// in case fpId is changed for copy on change
			fpId = emissionProcess.getFpId();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		// reuse cancel code to position to parent node
		cancelEmissionProc();

		if (!operationOK) {
			DisplayUtil
					.displayError("Adding or Updating emission process failed");
		}
		return getFacilityProfileOutcome();
	}

	public final void addControlEquipment() {
		renderComp = ADD_CNT_EQUIP_COMP;
		cntEquipId = null;
	}

	public final String saveAddControlEquipment() {
		Integer fromFpNodeId = 0;
		boolean operationOK = true;
		ValidationMessage[] validMessages;
		String audLogOrigVal = null;
		String audLogNewVal = null;

		if (cntEquipId == null) {
			DisplayUtil
					.displayError("Cannot add control equipment. Please select a control equipment or cancel");
			return getFacilityProfileOutcome();
		}

		float flowFactor;
		ControlEquipment controlEquipment1;
		if (cntEquipId == NEW_CNTEQUIP_ID) {
			controlEquipment1 = controlEquipmentTemp;
		} else {
			controlEquipment1 = facility.getControlEquipment(cntEquipId);
		}

		if (selectedTreeNode.getType().equals(EP_NODE)) {
			flowFactor = getFactor(emissionProcess.getEpEmissionFlows());
			validMessages = emissionProcess
					.validateAddContEquip(controlEquipment1);
			if (validMessages.length > 0) {
				if (displayValidationMessages(FAC_CLIENT_ID + "control:",
						validMessages)) {
					return getFacilityProfileOutcome();
				}
			}
			emissionProcess.addControlEquipment(controlEquipment1);
			fromFpNodeId = emissionProcess.getFpNodeId();
			audLogOrigVal = "Control equipment: "
					+ controlEquipment1.getControlEquipmentId()
					+ " not associated to emission process: "
					+ emissionProcess.getProcessId();
			audLogNewVal = "Control equipment: "
					+ controlEquipment1.getControlEquipmentId()
					+ "  associated to emission process: "
					+ emissionProcess.getProcessId();
		} else {
			flowFactor = getFactor(controlEquipment.getCeEmissionFlows());
			validMessages = controlEquipment
					.validateAddContEquip(controlEquipment1);
			if (validMessages.length > 0) {
				if (displayValidationMessages(FAC_CLIENT_ID + "control:",
						validMessages)) {
					return getFacilityProfileOutcome();
				}
			}

			controlEquipment.addControlEquipment(controlEquipment1);
			fromFpNodeId = controlEquipment.getFpNodeId();
			audLogOrigVal = "Control equipment: "
					+ controlEquipment1.getControlEquipmentId()
					+ " not associated to control equipment: "
					+ controlEquipment.getControlEquipmentId();
			audLogNewVal = "Control equipment: "
					+ controlEquipment1.getControlEquipmentId()
					+ " associated to control equipment: "
					+ controlEquipment.getControlEquipmentId();
		}
		try {
			if (copyOnChangeFpNodeIds != null) {
				fromFpNodeId = copyOnChangeFpNodeIds.get(fromFpNodeId);
				copyOnChangeFpNodeIds = null;
			}
			fpId = getFacilityService().createRelationShip(fromFpNodeId,
					controlEquipment1.getFpNodeId(), flowFactor, fpId,
					audLogOrigVal, audLogNewVal, currentUserId);
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		// reuse cancel code to position to parent node
		cancelAddControlEquipment();

		if (operationOK) {
			// If flowFactor now = FacilityBO.MISSING_FLOW_PERCENT then there
			// already exist more than one factor
			// and they are different. Otherwise, there are 0, 1 or n flow
			// factors
			// and they are all the same.
			cancelLabel = CANCEL;
			DisplayUtil.displayInfo("Control equipment added successfully");
			if (flowFactor <= FacilityBO.MISSING_FLOW_PERCENT) {
				// enter edit mode to set the new percentages.
				cancelLabel = CANCEL_LATER;
				DisplayUtil.displayWarning(FacilityBO.MISSING_PERCENTS);
				if (selectedTreeNode.getType().equals(EP_NODE)) {
					editEmissionProcess();
				} else if (selectedTreeNode.getType().equals(CE_NODE)) {
					editControlEquipment();
				}
			}
		} else {
			DisplayUtil.displayError("Adding control equipment failed");
		}
		return getFacilityProfileOutcome();
	}

	public final String editControlEquipment() {
		setEditable(true);
		controlEquipment.clearValidationMessages();
		operation[CNT_EQUIP_COMP] = EDIT_OP;
		saveObjId = controlEquipment.getControlEquipmentId();
		buildContEquipTypeData();
		return getFacilityProfileOutcome();
	}

	public final void addEgressPoint() {
		renderComp = ADD_EG_POINT_COMP;
		egrPntId = null;
	}

	public final String saveAddEgressPoint() {
		return saveAddEgressPoint(true);
	}
	
	public final String saveAddEgressPoint(boolean validate) {
		Integer fromFpNodeId = 0;
		boolean operationOK = true;
		ValidationMessage[] validMessages = new ValidationMessage[0];
		String audLogOrigVal = null;
		String audLogNewVal = null;

		if (egrPntId == null) {
			DisplayUtil
					.displayError("Cannot add Release point. Please select an Release point or cancel");
			return getFacilityProfileOutcome();
		}

		float flowFactor;
		if (egrPntId == NEW_EGRPNT_ID) {
			egressPoint = egressPointTemp;
		} else {
			egressPoint = facility.getEgressPoint(egrPntId);
		}

		if (selectedTreeNode.getType().equals(EP_NODE)) {
			flowFactor = getFactor(emissionProcess.getEpEmissionFlows());
			if (validate) {
				validMessages = emissionProcess.validateAddEgressPoint(egressPoint);
			}
			if (validMessages.length > 0) {
				if (displayValidationMessages(FAC_CLIENT_ID + "releasePoint:",
						validMessages)) {
					return getFacilityProfileOutcome();
				}
			}
			emissionProcess.addEgressPoint(egressPoint);
			fromFpNodeId = emissionProcess.getFpNodeId();
			audLogOrigVal = "Release point: " + egressPoint.getReleasePointId()
					+ " not associated to emission process: "
					+ emissionProcess.getProcessId();
			audLogNewVal = "Release point: " + egressPoint.getReleasePointId()
					+ "  associated to emission process: "
					+ emissionProcess.getProcessId();
		} else {
			flowFactor = getFactor(controlEquipment.getCeEmissionFlows());
			if (validate) {
				validMessages = controlEquipment
						.validateAddEgressPoint(egressPoint);
			}
			if (validMessages.length > 0) {
				if (displayValidationMessages(FAC_CLIENT_ID + "releasePoint:",
						validMessages)) {
					return getFacilityProfileOutcome();
				}
			}
			controlEquipment.addEgressPoint(egressPoint);
			fromFpNodeId = controlEquipment.getFpNodeId();
			audLogOrigVal = "Release point: " + egressPoint.getReleasePointId()
					+ " not associated to control equipment: "
					+ controlEquipment.getControlEquipmentId();
			audLogNewVal = "Release point: " + egressPoint.getReleasePointId()
					+ "  associated to control equipment: "
					+ controlEquipment.getControlEquipmentId();
		}
		try {
			if (copyOnChangeFpNodeIds != null) {
				fromFpNodeId = copyOnChangeFpNodeIds.get(fromFpNodeId);
				copyOnChangeFpNodeIds = null;
			}
			fpId = getFacilityService().createRelationShip(fromFpNodeId,
					egressPoint.getFpNodeId(), flowFactor, fpId, audLogOrigVal,
					audLogNewVal, currentUserId);
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		// reuse cancel code to position to parent node
		cancelAddEgressPoint();

		if (operationOK) {
			// If flowFactor now = -1 then there already exist more than one
			// factor
			// and they are different. Otherwise, there are 0, 1 or n flow
			// factors
			// and they are all the same.
			cancelLabel = CANCEL;
			DisplayUtil.displayInfo("Release point added successfully");
			if (!egressPoint.isFugitive()) {
				if (flowFactor <= FacilityBO.MISSING_FLOW_PERCENT) {
					// enter edit mode to set the new percentages.
					cancelLabel = CANCEL_LATER;
					DisplayUtil.displayWarning(FacilityBO.MISSING_PERCENTS);
					if (selectedTreeNode.getType().equals(EP_NODE)) {
						editEmissionProcess();
					} else if (selectedTreeNode.getType().equals(CE_NODE)) {
						editControlEquipment();
					}
				}
			}
		} else {
			DisplayUtil.displayError("Adding release point failed");
		}
		return getFacilityProfileOutcome();
	}

	public final void editEgressPoint() {
		setEditable(true);
		egressPoint.clearValidationMessages();
		operation[EG_POINT_COMP] = EDIT_OP;
		saveObjId = egressPoint.getReleasePointId();
	}

	public final void removeEgressPoint() {
		renderComp = REM_EG_POINT_COMP;
		egrPntId = null;
	}

	public final void removeControlEquipment() {
		renderComp = REM_CNT_EQUIP_COMP;
		cntEquipId = null;
	}

	public final String confirmRemoveOp() {
		return "dialog:confirmFacilityRemoveOp";
	}

	public final void confirmReturned(ReturnEvent event) {
	}

	public final void confirmRemoveEUReturned(ReturnEvent event) {
	}

	public final String getRemoveOpMessage() {
		if (renderComp == REM_EG_POINT_COMP || renderComp == REM_CNT_EQUIP_COMP) {
			removeOpMessage = "You have made a request to disassociate ";
			if (renderComp == REM_EG_POINT_COMP) {
				removeOpMessage += "release point: " + egrPntId + " from ";
			} else {
				removeOpMessage += "control equipment: " + cntEquipId
						+ " from ";
			}

			if (selectedTreeNode.getType().equals(EP_NODE)) {
				removeOpMessage += "emission process:  "
						+ selectedTreeNode.getDescription();
			} else {
				removeOpMessage += "control equipment:  "
						+ selectedTreeNode.getDescription()
						+ ". This change will be reflected under every Emissions Unit that contains this control equipment";
			}
			removeOpMessage += ". Continue with disassociation?";
		} else if (renderComp == EM_PROC_COMP) {
			removeOpMessage = "Are you sure you want to delete ";
			removeOpMessage += "emissions process: "
					+ emissionProcess.getProcessId() + " ?";
		} else if (renderComp == EG_POINT_COMP) {
			removeOpMessage = "Are you sure you want to delete ";
			removeOpMessage += "release point: "
					+ egressPoint.getReleasePointId() + " ?";
		} else if (renderComp == CNT_EQUIP_COMP) {
			removeOpMessage = "Are you sure you want to delete ";
			removeOpMessage += "control equipment: "
					+ controlEquipment.getControlEquipmentId() + " ?";
		} else if (renderComp == EM_UNIT_COMP) {
			// shut down EU
			removeOpMessage = "<br>WARNING: IF YOU CLICK ON 'YES' IN THIS WINDOW, YOU WILL NOT BE ABLE TO CHANGE THE OPERATING STATUS BACK TO ANY OTHER STATE.</br>"
					+ " This operation is NOT reversable (unless you delete your entire in-process facility detail change without submitting it to DEQ)."
					+ " Only click 'Yes' if this Emissions Unit is not operating or producing emissions and"
					+ " will not be started again so that the equipment may be dismantled and/or removed."
					+ " Once shutdown, to re-start this unit you would need to submit a new application and receive a new permit.";
		}

		return removeOpMessage;
	}

	public final String getRemoveEUMessage() {
		String msg = "You have made a request to remove Emission Unit "
				+ emissionUnit.getEpaEmuId()
				+ " from this Facility Detail.&nbsp;&nbsp;"
				+ "Once the Emission Unit has been removed, it cannot be restored.&nbsp;&nbsp;"
				+ "Would you like to remove this Emission Unit?&nbsp;&nbsp;"
				+ "<b>Warning: any application or emissions-related work will also be lost.</b>";

		return msg;
	}

	public final void removeEU(ActionEvent actionEvent) {
		if (emissionUnit != null) {
			if (emissionUnit.getEmissionProcesses().size() > 0) {
				DisplayUtil
						.displayError("The selected Emission Unit has Emission is associated "
								+ "with one or more Emission Processes. These processes must be disassociated "
								+ " from the Emission Unit before the Emission Unit can be deleted.");
			} else {
				try {
					getFacilityService().removeEmissionUnit(emissionUnit);
					facility.removeEmissionUnit(emissionUnit);
					refreshFacility();
					FacesUtil.returnFromDialogAndRefresh();
					DisplayUtil
							.displayInfo("Emission Unit successfully deleted.");
				} catch (RemoteException e) {
					handleException(e);
				}
			}
		}
	}

	public final void cancelRemoveEU(ActionEvent actionEvent) {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void saveRemoveOperation(ActionEvent actionEvent) {
		if (renderComp == REM_EG_POINT_COMP) {
			saveRemoveEgressPoint();
		} else if (renderComp == REM_CNT_EQUIP_COMP) {
			saveRemoveControlEquipment();
		} else if (renderComp == EM_PROC_COMP) {
			saveDeleteEmissionProcess();
		} else if (renderComp == EG_POINT_COMP) {
			saveDeleteEgressPoint();
		} else if (renderComp == CNT_EQUIP_COMP) {
			saveDeleteControlEquipment();
		} else if (renderComp == EM_UNIT_COMP) {
			saveEmissionUnit();
		}

		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String saveRemoveEgressPoint() {
		boolean operationOK = true;

		Integer fromFpNodeId;
		Integer toFpNodeId;
		String audLogOrigVal = null;
		String audLogNewVal = null;
		String fromNodeId = selectedTreeNode.getIdentifier();
		List<FacilityEmissionFlow> savedFefs = null;
		if (selectedTreeNode.getType().equals(EP_NODE)) {
			fromFpNodeId = emissionProcess.getFpNodeId();
			toFpNodeId = emissionProcess.findEgressPoint(egrPntId)
					.getFpNodeId();
			audLogOrigVal = "Release point: " + egrPntId
					+ " associated to emission process: "
					+ emissionProcess.getProcessId();
			audLogNewVal = "Release point: " + egrPntId
					+ "  disassociated from emission process: "
					+ emissionProcess.getProcessId();
			// keep hold of percents that were displayed
			savedFefs = emissionProcess.getEpEmissionFlows();
		} else {
			fromFpNodeId = controlEquipment.getFpNodeId();
			toFpNodeId = controlEquipment.findEgressPoint(egrPntId)
					.getFpNodeId();
			audLogOrigVal = "Release point: " + egrPntId
					+ " associated to control equipment: "
					+ controlEquipment.getControlEquipmentId();
			audLogNewVal = "Release point: " + egrPntId
					+ "  disassociated from control equipment: "
					+ controlEquipment.getControlEquipmentId();
			// keep hold of percents that were displayed
			savedFefs = controlEquipment.getCeEmissionFlows();
		}

		try {
			fpId = getFacilityService().removeRelationShip(fromFpNodeId,
					toFpNodeId, fpId, audLogOrigVal, audLogNewVal,
					currentUserId);
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			refreshFacility();
			selectedTreeNode = facNodeMap.get(fromNodeId);
			current = fromNodeId;
			nodeClicked();
			DisplayUtil.displayInfo("Release point disassociated successfully");
			double needEdit;
			if (selectedTreeNode.getType().equals(EP_NODE)) {
				needEdit = isUnevenPercentValuesNot100(emissionProcess
						.getEpEmissionFlows());
			} else {
				needEdit = isUnevenPercentValuesNot100(controlEquipment
						.getCeEmissionFlows());
			}
			cancelLabel = CANCEL;
			boolean stayInEdit = false;
			if (needEdit != (double) FacilityBO.MISSING_FLOW_PERCENT) {
				cancelLabel = PROPORTIONAL_PERCENT_MSG;
				DisplayUtil.displayError(FacilityBO.FLOW_TOTAL_WRONG1
						+ BaseDB.numberToxxx_xx(needEdit)
						+ FacilityBO.FLOW_TOTAL_WRONG2);
				stayInEdit = true;
			}
			// enter edit mode to set the new percentages.
			if (stayInEdit) {
				if (selectedTreeNode.getType().equals(EP_NODE)) {
					// Restore previous percent
					restorePercents(emissionProcess.getFpNodeId(),
							emissionProcess.getEpEmissionFlows(), savedFefs);
					editEmissionProcess();
				} else if (selectedTreeNode.getType().equals(CE_NODE)) {
					// Restore previous percent
					restorePercents(controlEquipment.getFpNodeId(),
							controlEquipment.getCeEmissionFlows(), savedFefs);
					editControlEquipment();
				}
			}
		} else {
			// reuse cancel to position to parent
			cancelAddEgressPoint();
			DisplayUtil.displayError("Disassociating release point failed");
		}
		return getFacilityProfileOutcome();
	}

	public final String saveRemoveControlEquipment() {
		boolean operationOK = true;

		Integer fromFpNodeId;
		Integer toFpNodeId;
		String audLogOrigVal = null;
		String audLogNewVal = null;

		String fromNodeId = selectedTreeNode.getIdentifier();
		List<FacilityEmissionFlow> savedFefs = null;
		if (selectedTreeNode.getType().equals(EP_NODE)) {
			fromFpNodeId = emissionProcess.getFpNodeId();
			toFpNodeId = emissionProcess.findControlEquipment(cntEquipId)
					.getFpNodeId();
			audLogOrigVal = "Control equipment: " + cntEquipId
					+ " associated to emission process: "
					+ emissionProcess.getProcessId();
			audLogNewVal = "Control equipment: " + cntEquipId
					+ "  disassociated from emission process: "
					+ emissionProcess.getProcessId();
			// keep hold of percents that were displayed
			savedFefs = emissionProcess.getEpEmissionFlows();
		} else {
			fromFpNodeId = controlEquipment.getFpNodeId();
			toFpNodeId = controlEquipment.findControlEquipment(cntEquipId)
					.getFpNodeId();
			audLogOrigVal = "Control equipment: " + cntEquipId
					+ " associated to control equipment: "
					+ controlEquipment.getControlEquipmentId();
			audLogNewVal = "Control equipment: " + cntEquipId
					+ "  disassociated from control equipment: "
					+ controlEquipment.getControlEquipmentId();
			// keep hold of percents that were displayed
			savedFefs = controlEquipment.getCeEmissionFlows();
		}

		try {
			fpId = getFacilityService().removeRelationShip(fromFpNodeId,
					toFpNodeId, fpId, audLogOrigVal, audLogNewVal,
					currentUserId);
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			refreshFacility();
			selectedTreeNode = facNodeMap.get(fromNodeId);
			current = fromNodeId;
			nodeClicked();
			DisplayUtil
					.displayInfo("Control equipment disassociated successfully");

			double needEdit;
			if (selectedTreeNode.getType().equals(EP_NODE)) {
				needEdit = isUnevenPercentValuesNot100(emissionProcess
						.getEpEmissionFlows());
			} else {
				needEdit = isUnevenPercentValuesNot100(controlEquipment
						.getCeEmissionFlows());
			}
			cancelLabel = CANCEL;
			boolean stayInEdit = false;
			if (needEdit != (double) FacilityBO.MISSING_FLOW_PERCENT) {
				cancelLabel = PROPORTIONAL_PERCENT_MSG;
				DisplayUtil.displayError(FacilityBO.FLOW_TOTAL_WRONG1
						+ BaseDB.numberToxxx_xx(needEdit)
						+ FacilityBO.FLOW_TOTAL_WRONG2);
				stayInEdit = true;
			}
			if (stayInEdit) {
				if (selectedTreeNode.getType().equals(EP_NODE)) {
					// Restore previous percent
					restorePercents(emissionProcess.getFpNodeId(),
							emissionProcess.getEpEmissionFlows(), savedFefs);
					editEmissionProcess();
				} else if (selectedTreeNode.getType().equals(CE_NODE)) {
					// Restore previous percent
					restorePercents(controlEquipment.getFpNodeId(),
							controlEquipment.getCeEmissionFlows(), savedFefs);
					editControlEquipment();
				}
			}
		} else {
			// reuse cancel to reposition to parent
			cancelAddControlEquipment();
			DisplayUtil.displayError("Disassociating control equipment failed");
		}
		return getFacilityProfileOutcome();
	}

	private void restorePercents(Integer node,
			List<FacilityEmissionFlow> currentFefs,
			List<FacilityEmissionFlow> savedFefs) {
		for (FacilityEmissionFlow fefC : currentFefs) {
			boolean found = false;
			for (FacilityEmissionFlow fefS : savedFefs) {
				if (fefC.getRelationship().getToNodeId()
						.equals(fefS.getRelationship().getToNodeId())) {
					fefC.setPercents(fefS.getPercent(), fefS.getPercentValue());
					found = true;
					break;
				}
			}
			if (!found) {
				logger.error("Expected to find toNodeId "
						+ fefC.getRelationship().getToNodeId() + " for node "
						+ node);
			}
		}
	}

	// Determine if there is an actual missing value or if all values are zero.
	boolean isMissingPercentValues(List<FacilityEmissionFlow> fefs) {
		float total = 0f;
		boolean missing = false;
		for (FacilityEmissionFlow ef : fefs) {
			if (ef.getFlowFactor() > 0)
				total += ef.getFlowFactor();
			if (ef.getFlowFactor() == FacilityBO.MISSING_FLOW_PERCENT) {
				missing = true;
				break;
			}
		}
		boolean rtn = false;
		if (missing || total == 0f) {
			rtn = true;
		}
		return rtn;
	}

	// Determine if all remaining branches have values, not all the same and do
	// not add to 100%.
	// Returning FacilityBO.MISSING_FLOW_PERCENT means we do not need to go into
	// edit mode.
	// Otherwise return the sum to display in error message.
	float isUnevenPercentValuesNot100(List<FacilityEmissionFlow> fefs) {
		if (fefs.size() == 0) {
			return FacilityBO.MISSING_FLOW_PERCENT;
		}
		float total = 0f;
		boolean missing = false;
		float f = -2000f;
		boolean allSame = true;
		for (FacilityEmissionFlow ef : fefs) {
			if (f == -2000f) {
				f = ef.getFlowFactor();
			} else {
				if (f != ef.getFlowFactor()) {
					allSame = false;
				}
			}
			if (ef.getFlowFactor() > 0)
				total += ef.getFlowFactor();
			if (ef.getFlowFactor() == FacilityBO.MISSING_FLOW_PERCENT) {
				missing = true;
				break;
			}
		}
		if (missing || (allSame && total != 0)) {
			// not different values or some missing
			return FacilityBO.MISSING_FLOW_PERCENT; // OK to leave some blank
													// and OK if all remaining
													// are the same--as long as
													// not all zero
		}
		// get here if have all values
		if (total < 99.995 || total > 100.005)
			return total;
		else
			return FacilityBO.MISSING_FLOW_PERCENT;
	}

	// Determine what factor to use for next association.
	// If first one use 100f. If not all the same then make it blank
	// (FacilityBO.MISSING_FLOW_PERCENT).
	float getFactor(List<FacilityEmissionFlow> fefs) {
		int numBranches = fefs.size();
		float rtn = -2000f;
		if (numBranches == 0) {
			rtn = 100f; // for 100%
		} else {
			for (FacilityEmissionFlow ef : fefs) {
				if (rtn == -2000f) {
					rtn = ef.getFlowFactor();
				} else {
					if (rtn != ef.getFlowFactor()) {
						rtn = FacilityBO.MISSING_FLOW_PERCENT;
						break;
					}
				}
			}
			if (rtn == 0f) {
				rtn = FacilityBO.MISSING_FLOW_PERCENT;
			}
		}
		return rtn;
	}

	public final String saveDeleteEmissionProcess() {
		boolean operationOK = true;

		try {
			getFacilityService().removeEmissionProcess(emissionProcess,
					currentUserId);
			// in case fpId is changed for copy on change
			fpId = emissionProcess.getFpId();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		// reuse cancel to position to parent
		cancelEmissionProc();

		if (operationOK) {
			DisplayUtil.displayInfo("Emission process deleted successfully");
		} else {
			DisplayUtil.displayError("Deleting emission process failed");
		}
		return getFacilityProfileOutcome();
	}

	public final String saveDeleteEgressPoint() {
		String pageViewId = FAC_CLIENT_ID + "egressPoint:";
		boolean operationOK = true;
		ValidationMessage[] valMsgs = checkReferencesToEgressPoint(egressPoint);
		if(valMsgs.length != 0) {
			displayValidationMessages(pageViewId, valMsgs);
			operationOK = false;
		} else {
			try {
				getFacilityService().removeEgressPoint(egressPoint, currentUserId);
				// in case fpId is changed for copy on change
				fpId = egressPoint.getFpId();
			} catch (RemoteException re) {
				handleException(re);
				operationOK = false;
			}
	
			setEditable(false);
			refreshFacility();
			nodeClicked();
		}

		if (operationOK) {
			DisplayUtil.displayInfo("Release Point deleted successfully");
		} else {
			DisplayUtil.displayError("Deleting release point failed");
		}
		return getFacilityProfileOutcome();
	}

	public final String saveDeleteControlEquipment() {
		boolean operationOK = true;

		try {
			getFacilityService().removeControlEquipment(controlEquipment,
					currentUserId);
			// in case fpId is changed for copy on change
			fpId = controlEquipment.getFpId();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		setEditable(false);
		refreshFacility();
		if (operationOK) {
			DisplayUtil.displayInfo("Control equipment deleted successfully");
		} else {
			DisplayUtil.displayError("Deleting control equipment failed");
		}
		return nodeClicked();
	}

	public final boolean isAddToEuDisabled() {
		if (!isDisabledUpdateButton() && !isEuOperatingStatusIV()) {
			return false;
		}
		return true;
	}

	private boolean isEuOperatingStatusIV() {
		return null == emissionUnit ? false : EuOperatingStatusDef.IV
				.equals(emissionUnit.getOperatingStatusCd());
	}

	public final boolean isAddToCeDisabled() {
		if (!isDisabledUpdateButton() && isCntEquipAssigned()) {
			return false;
		}
		return true;
	}

	public final boolean isRemoveEgpDisabled() {
		if (!isDisabledUpdateButton()) {
			if (selectedTreeNode.getType().equals(EP_NODE)) {
				return (emissionProcess.getEgressPoints().isEmpty() ? true
						: false);
			}
			if (null != controlEquipment) {
				return (controlEquipment.getEgressPoints().isEmpty() ? true
						: false);
			}
		}
		return true;
	}

	public final boolean isRemoveCeDisabled() {
		if (!isDisabledUpdateButton()) {
			if (selectedTreeNode.getType().equals(EP_NODE)) {
				return (emissionProcess.getControlEquipments().isEmpty() ? true
						: false);
			}

			if (null != controlEquipment) {
				return (controlEquipment.getControlEquips().isEmpty() ? true
						: false);
			}
		}
		return true;
	}

	public final boolean isDeleteEpDisabled() {
	
		 // TODO double check this logic.
		if (!isDisabledUpdateButton() && null != emissionProcess) {
			if (emissionProcess.getControlEquipments().isEmpty()
					&& emissionProcess.getEgressPoints().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public final boolean isDeleteEgrPntDisabled() {
		if (!isDisabledUpdateButton() && null != egressPoint) {
			if (!facility.isEgressPointAssigned(egressPoint.getFpNodeId())) {
				return false;
			}
		}
		return true;
	}

	public final boolean isDeleteCntEquipDisabled() {
		if (!isDisabledUpdateButton() && null != controlEquipment) {
			if (!facility.isCntEquipAssigned(controlEquipment.getFpNodeId())) {
				if (controlEquipment.getEgressPoints().isEmpty()
						&& controlEquipment.getControlEquips().isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	public final void cancelRemoveOperation(ActionEvent actionEvent) {
		FacesUtil.returnFromDialogAndRefresh();
	}

	protected boolean printValidationMessages(
			ValidationMessage[] validationMessages) {
		String refID;
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		for (int i = 0; i < validationMessages.length; i++) {
			refID = validationMessages[i].getReferenceID();
			if (refID == null) {
				validationMessages[i]
						.setReferenceID(ValidationBase.FACILITY_TAG + ":"
								+ facilityId + ":" + FAC_REFERENCE + ":"
								+ FAC_OUTCOME);
			} else if (refID.startsWith(CONTACTS_REFERENCE)) {
				validationMessages[i]
						.setReferenceID(ValidationBase.FACILITY_TAG + ":"
								+ facilityId + ":" + refID + ":"
								+ CONTACTS_OUTCOME);
			} else if (refID.startsWith(RULES_REFERENCE)) {
				validationMessages[i]
						.setReferenceID(ValidationBase.FACILITY_TAG + ":"
								+ facilityId + ":" + refID + ":"
								+ RULES_OUTCOME);
			} else if (validationMessages[i].getProperty().equalsIgnoreCase(FacilityProfileBase.FAC_NO_NAICS)) {
				validationMessages[i].setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":" + refID + ":"
						+ FacilityProfileBase.FAC_OUTCOME + ":" + FacilityProfileBase.FAC_NO_NAICS);
			} else if (validationMessages[i].getProperty().equalsIgnoreCase(FacilityProfileBase.FAC_NO_API)) {
				validationMessages[i].setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":" + refID + ":"
						+ FacilityProfileBase.FAC_OUTCOME + ":" + FacilityProfileBase.FAC_NO_API);
			} else {
				validationMessages[i]
						.setReferenceID(ValidationBase.FACILITY_TAG + ":"
								+ facilityId + ":" + refID + ":" + FAC_OUTCOME);
			}
			valMessages.add(validationMessages[i]);
		}
		return AppValidationMsg.validate(valMessages, true);
	}

	public final String validateFacilityProfile() {
		ValidationMessage[] validationMessages;

		// if (facility.isValidated()) {
		// DisplayUtil.displayInfo("Facility inventory already validated");
		// return null;
		// }

		try {
			validationMessages = getFacilityService().validateFacilityProfile(
					facility.getFpId());
			if (validationMessages.length > 0) {
				printValidationMessages(validationMessages);
			} else {
				Object close_validation_dialog = FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap()
						.get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
				if (close_validation_dialog != null) {
					FacesUtil
							.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
					FacesContext.getCurrentInstance().getExternalContext()
							.getSessionMap()
							.remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
				}
				DisplayUtil.displayInfo("Validation Successful");
			}

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Unable to validate facility detail");
		}

		refreshFacility();
		selectedTreeNode = facNode;
		current = facilityId;
		nodeClicked();
		return null;
	}

	public final void updateFacilityValidity() {
		ValidationMessage[] validationMessages;

		try {
			validationMessages = getFacilityService().validateFacilityProfile(
					facility.getFpId());
			if (validationMessages.length == 0) {
				DisplayUtil.displayInfo("Validation Successful");
			}

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Unable to validate facility detail");
		}

		refreshFacility();
	}

	public final String reportFacilityProfile() {
		return "dialog:facilityProfileReport";
	}

	/**
	 * Generate a pdf file containing data from the facility and create a
	 * temporary Document object refrencing this file.
	 * 
	 * @param fpId
	 *            the facility FP ID to be rendered in a PDF file.
	 * @return Document object referencing pdf file.
	 */
	// public Document generateTempFacilityProfileReport(String userName)
	// throws DAOException {
	// TmpDocument profileDoc = null;
	//
	// try {
	// profileDoc = new TmpDocument();
	//
	// // Set the path elements of the temp doc.
	//
	// //profileDoc.setUserName(userName);
	// profileDoc.setDescription("Facility Detail Data Report");
	// profileDoc.setFacilityID(facility.getFacilityId());
	// profileDoc.setTmpFileName("FacProfileDoc.pdf");
	// profileDoc.setTemporary(true);
	// profileDoc.setLastModifiedBy(1);
	// profileDoc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
	// profileDoc.setUploadDate(profileDoc.getLastModifiedTS());
	// DocumentUtil.mkDir(profileDoc.getDirName());
	// OutputStream os =
	// DocumentUtil.createDocumentStream(profileDoc.getPath());
	// writeFacilityProfileToStream(facility, os);
	// os.close();
	//
	// } catch (Exception ex) {
	// handleException(new RemoteException(ex.getMessage(), ex));
	// throw new DAOException("Cannot generate facility detail report", ex);
	// }
	//
	// return profileDoc;
	// }

	/**
	 * Generate a pdf file containing data for the given facility and create a
	 * temporary Document object refrencing this file.
	 * 
	 * @param fpId
	 *            the facility FP ID to be rendered in a PDF file.
	 * @return Document object referencing pdf file.
	 */
	// public Document generateFacilityProfileReport(Integer fpId, String
	// userName)
	// throws DAOException {
	// Document profileDoc = null;
	//
	// try {
	// this.fpId = fpId;
	// refreshFacility();
	// profileDoc = generateTempFacilityProfileReport(userName);
	//
	// } catch (Exception ex) {
	// handleException(new RemoteException(ex.getMessage(), ex));
	// throw new DAOException("Cannot generate facility detail report", ex);
	// }
	//
	// return profileDoc;
	// }

	/**
	 * Write pdf version of facility detail to an output stream.
	 * 
	 * @param facility
	 * @param os
	 * @throws DAOException
	 */
	public void writeFacilityProfileToStream(Facility facility, OutputStream os)
			throws IOException {

		try {
			FacilityPdfGenerator generator = new FacilityPdfGenerator();
			generator.generatePdf(facility, os, null);
		} catch (DocumentException e) {
			handleException(new RemoteException(e.getMessage()));
			throw new IOException("Exception writing facility detail to stream");
		}
	}

	public final void printFacilityTree(ActionEvent actionEvent) {
		FacesUtil.startModelessDialogWithMenubar(
				"../facilities/printFacTree.jsf", 900, 700);
	}

	public final TreeModelBase getTreeData() {
		if (treeData == null) {
			getFacility();

			if (facility != null) {
				facNodeMap = new HashMap<String, Stars2TreeNode>();

				treeData = getEuTreeData(selEpaEmuIds, expandOpt);
				selectedTreeNode = facNode;
				current = facilityId;
				renderComp = FACILITY_COMP;
			}
		}
		return treeData;
	}

	@SuppressWarnings("unchecked")
	public final TreeModelBase getPrintTreeData() {
		boolean leaf = false;
		ArrayList<EmissionUnit> invalidEUs = new ArrayList<EmissionUnit>();

		EmissionUnit[] emissionUnits = facility.getEmissionUnits().toArray(
				new EmissionUnit[0]);

		Stars2TreeNode facNode1 = new Stars2TreeNode(FAC_NODE,
				facility.getFacilityId(), facility.getFacilityId(), leaf,
				facility);

		TreeModelBase treeData1 = new TreeModelBase(facNode1);

		// This is for expanding all tree nodes the first time the
		// tree is rendered
		ArrayList<String> treePath1 = new ArrayList<String>();

		treePath1.add("0");

		for (int i = 0; i < emissionUnits.length; i++) {
			treePath1.add("0:" + Integer.toString(i));
		}

		int euNum = 0;

		for (EmissionUnit tempEU : emissionUnits) {
			if (EuOperatingStatusDef.IV.equals(tempEU.getOperatingStatusCd())) {
				invalidEUs.add(tempEU);
			}
		}

		if (emissionUnits.length != invalidEUs.size()) {
			for (EmissionUnit tempEU : emissionUnits) {
				leaf = false;

				if (EuOperatingStatusDef.IV.equals(tempEU
						.getOperatingStatusCd())) {
					continue;
				}

				EmissionProcess[] emissionProcesses = tempEU
						.getEmissionProcesses().toArray(new EmissionProcess[0]);

				for (int i = 0; i < emissionProcesses.length; i++) {
					treePath1.add("0:" + Integer.toString(euNum) + ":"
							+ Integer.toString(i));
				}

				if (emissionProcesses.length == 0) {
					leaf = true;
				}

				Stars2TreeNode euNode = new Stars2TreeNode(EU_NODE,
						tempEU.getEpaEmuId(),
						euFacNodeMapId(tempEU.getEpaEmuId()), leaf, tempEU);
				int epNum = 0;
				for (EmissionProcess tempEP : emissionProcesses) {
					leaf = false;
					ControlEquipment[] controlEquips = tempEP
							.getControlEquipments().toArray(
									new ControlEquipment[0]);

					EgressPoint[] egressPoints = tempEP.getEgressPoints()
							.toArray(new EgressPoint[0]);

					if ((controlEquips.length == 0)
							&& (egressPoints.length == 0)) {
						leaf = true;
					}

					Stars2TreeNode epNode = new Stars2TreeNode(EP_NODE,
							tempEP.getProcessId(),
							epFacNodeMapId(tempEP.getProcessId()), leaf, tempEP);

					int cep = 0;
					String cePath;
					for (ControlEquipment tempCE : controlEquips) {
						leaf = false;

						ControlEquipment[] ceControlEquips = tempCE
								.getControlEquips().toArray(
										new ControlEquipment[0]);

						EgressPoint[] ceEgressPoints = tempCE.getEgressPoints()
								.toArray(new EgressPoint[0]);

						if ((ceControlEquips.length == 0)
								&& (ceEgressPoints.length == 0)) {
							leaf = true;
						}
						cePath = "0:" + Integer.toString(euNum) + ":"
								+ Integer.toString(epNum) + ":"
								+ Integer.toString(cep);
						treePath1.add(cePath);
						Stars2TreeNode ceNode = addControlEquipToTree(tempCE,
								cePath, treePath1);
						cep++;

						epNode.getChildren().add(ceNode);
						ceNode.setParentFpNodeId(tempEP.getFpNodeId());
						ceNode.setParentNodeId(epFacNodeMapId(tempEP
								.getProcessId()));
					}

					for (EgressPoint tempEGP : egressPoints) {
						Stars2TreeNode egpNode = new Stars2TreeNode(EGP_NODE,
								tempEGP.getReleasePointId(),
								egFacNodeMapId(tempEGP.getReleasePointId()),
								true, tempEGP);

						epNode.getChildren().add(egpNode);
						egpNode.setParentFpNodeId(tempEP.getFpNodeId());
						egpNode.setParentNodeId(epFacNodeMapId(tempEP
								.getProcessId()));
					}
					euNode.getChildren().add(epNode);
					epNum++;
				}

				facNode1.getChildren().add(euNode);
				euNum++;
			}
		}
		
		if (!isPublicApp()) {

			if (!invalidEUs.isEmpty()) {
				treePath1.add("0:" + Integer.toString(euNum));
				Stars2TreeNode dummyNode = new Stars2TreeNode("InvalidEUs", DUMMY_INV_EU_NODE, "InvalidEUs", false,
						DUMMY_INV_EU_NODE);
				facNode1.getChildren().add(dummyNode);

				for (int i = 0; i < invalidEUs.size(); i++) {
					treePath1.add("0:" + Integer.toString(euNum) + ":" + Integer.toString(i));
				}

				for (EmissionUnit tempEU : invalidEUs) {
					Stars2TreeNode euNode = new Stars2TreeNode(EU_NODE, tempEU.getEpaEmuId(),
							euFacNodeMapId(tempEU.getEpaEmuId()), true, tempEU);
					dummyNode.getChildren().add(euNode);
				}
				euNum++;
			}

		}

		ControlEquipment[] controlEquips = facility.getUnassignedCntEquips();
		if (controlEquips.length > 0) {
			treePath1.add("0:" + Integer.toString(euNum));
			UnAssignCEsNode = new Stars2TreeNode(UACE_NODE, DUMMY_CE_NODE,
					UACE_NODE, false, DUMMY_CE_NODE);
			facNode1.getChildren().add(UnAssignCEsNode);

			for (int i = 0; i < controlEquips.length; i++) {
				treePath1.add("0:" + Integer.toString(euNum) + ":"
						+ Integer.toString(i));
			}

			int k = 0;
			String cePath;
			for (ControlEquipment tempCE : controlEquips) {
				cePath = "0:" + Integer.toString(euNum) + ":"
						+ Integer.toString(k);
				Stars2TreeNode ceNode1 = addControlEquipToTree(tempCE, cePath,
						treePath1);
				UnAssignCEsNode.getChildren().add(ceNode1);
				k++;
			}
			euNum++;
		}

		EgressPoint[] egrPoints = facility.getUnassignedEgrPoints();
		if (egrPoints.length > 0) {
			treePath1.add("0:" + Integer.toString(euNum));
			UnAssignEgpsNode = new Stars2TreeNode(UAEGP_NODE,
					DUMMY_EGR_PNT_NODE, UAEGP_NODE, false, DUMMY_EGR_PNT_NODE);
			facNode1.getChildren().add(UnAssignEgpsNode);

			for (int i = 0; i < egrPoints.length; i++) {
				treePath1.add("0:" + Integer.toString(euNum) + ":"
						+ Integer.toString(i));
			}

			for (EgressPoint tempEGR : egrPoints) {
				Stars2TreeNode egrNode1 = new Stars2TreeNode(EGP_NODE,
						tempEGR.getReleasePointId(),
						egFacNodeMapId(tempEGR.getReleasePointId()), true,
						tempEGR);
				UnAssignEgpsNode.getChildren().add(egrNode1);
			}
			euNum++;
		}

		TreeStateBase treeState = new TreeStateBase();

		treeState.expandPath(treePath1.toArray(new String[0]));
		treeData1.setTreeState(treeState);

		return treeData1;
	}

	@SuppressWarnings("unchecked")
	protected final Stars2TreeNode addControlEquipToTree(
			ControlEquipment tempCE, String cePath, ArrayList<String> treePath1) {
		boolean leaf = false;
		String cePath1 = null;

		ControlEquipment[] ceControlEquips = tempCE.getControlEquips().toArray(
				new ControlEquipment[0]);
		EgressPoint[] ceEgressPoints = tempCE.getEgressPoints().toArray(
				new EgressPoint[0]);

		if ((ceControlEquips.length == 0) && (ceEgressPoints.length == 0)) {
			leaf = true;
		}

		Stars2TreeNode ceNode = new Stars2TreeNode(CE_NODE,
				tempCE.getControlEquipmentId(),
				ceFacNodeMapId(tempCE.getControlEquipmentId()), leaf, tempCE);

		int cep = 0;
		for (ControlEquipment tempCeCE : ceControlEquips) {
			if (cePath != null) {
				cePath1 = cePath + ":" + Integer.toString(cep);
				treePath1.add(cePath1);
			}
			Stars2TreeNode ceCeNode = addControlEquipToTree(tempCeCE, cePath1,
					treePath1);
			cep++;
			ceNode.getChildren().add(ceCeNode);
			ceCeNode.setParentFpNodeId(tempCE.getFpNodeId());
			ceCeNode.setParentNodeId(ceFacNodeMapId(tempCE
					.getControlEquipmentId()));
		}

		for (EgressPoint tempEGP : ceEgressPoints) {
			Stars2TreeNode egpNode = new Stars2TreeNode(EGP_NODE,
					tempEGP.getReleasePointId(),
					egFacNodeMapId(tempEGP.getReleasePointId()), true, tempEGP);
			if (cePath != null) {
				cePath1 = cePath + ":" + Integer.toString(cep);
				treePath1.add(cePath1);
			}
			cep++;
			ceNode.getChildren().add(egpNode);
			egpNode.setParentFpNodeId(tempCE.getFpNodeId());
			egpNode.setParentNodeId(ceFacNodeMapId(tempCE
					.getControlEquipmentId()));
		}

		return ceNode;
	}

	@SuppressWarnings("unchecked")
	protected final Stars2TreeNode addControlEquipToTree(
			ControlEquipment tempCE, String cePath, String epaEuId, int expand) {
		boolean leaf = false;
		String cePath1 = null;

		tempCE.setAssociatedEpaEuIds(epaEuId);

		ControlEquipment[] ceControlEquips = tempCE.getControlEquips().toArray(
				new ControlEquipment[0]);

		EgressPoint[] ceEgressPoints = tempCE.getEgressPoints().toArray(
				new EgressPoint[0]);

		if ((ceControlEquips.length == 0) && (ceEgressPoints.length == 0)) {
			leaf = true;
		}

		Stars2TreeNode ceNode = new Stars2TreeNode(CE_NODE,
				tempCE.getControlEquipmentId(),
				ceFacNodeMapId(tempCE.getControlEquipmentId()), leaf, tempCE);
		facNodeMap.put(ceFacNodeMapId(tempCE.getControlEquipmentId()), ceNode);

		int cep = 0;
		for (ControlEquipment tempCeCE : ceControlEquips) {
			// if (cePath != null && expand == 2) {
			if (cePath != null && expand >= 0) {
				cePath1 = cePath + ":" + Integer.toString(cep);
				treePath.add(cePath1);
			}
			Stars2TreeNode ceCeNode = addControlEquipToTree(tempCeCE, cePath1,
					epaEuId, expand);
			cep++;
			ceNode.getChildren().add(ceCeNode);
			ceCeNode.setParentFpNodeId(tempCE.getFpNodeId());
			ceCeNode.setParentNodeId(ceFacNodeMapId(tempCE
					.getControlEquipmentId()));
		}

		for (EgressPoint tempEGP : ceEgressPoints) {
			Stars2TreeNode egpNode = new Stars2TreeNode(EGP_NODE,
					tempEGP.getReleasePointId(),
					egFacNodeMapId(tempEGP.getReleasePointId()), true, tempEGP);
			if (cePath != null && expand >= 0) {
				cePath1 = cePath + ":" + Integer.toString(cep);
				treePath.add(cePath1);
			}
			cep++;
			facNodeMap
					.put(egFacNodeMapId(tempEGP.getReleasePointId()), egpNode);
			ceNode.getChildren().add(egpNode);
			egpNode.setParentFpNodeId(tempCE.getFpNodeId());
			egpNode.setParentNodeId(ceFacNodeMapId(tempCE
					.getControlEquipmentId()));
			tempEGP.setAssociatedEpaEuIds(epaEuId);
		}

		return ceNode;
	}

	@SuppressWarnings("unchecked")
	public final TreeModelBase getEuTreeData(List<String> selEpaEmuIds,
			int expand) {
		boolean leaf = false;
		boolean selEmuInvalid = false;
		boolean selEmuShutdown = false;
		ArrayList<EmissionUnit> invalidEUs = new ArrayList<EmissionUnit>();
		ArrayList<EmissionUnit> shutdownEUs = new ArrayList<EmissionUnit>();

		EmissionUnit[] emissionUnits = facility.getEmissionUnits().toArray(
				new EmissionUnit[0]);

		facNode = new Stars2TreeNode(FAC_NODE, facility.getFacilityId(),
				facility.getFacilityId(), leaf, facility);

		treeData = new TreeModelBase(facNode);

		// This is for expanding all tree nodes the first time the
		// tree is rendered
		treePath = new ArrayList<String>();

		treePath.add("0");

		int euNum = 0;

		for (EmissionUnit tempEU : emissionUnits) {
			if (EuOperatingStatusDef.IV.equals(tempEU.getOperatingStatusCd())) {
				invalidEUs.add(tempEU);
				if (selEpaEmuIds != null
						&& selEpaEmuIds.contains(tempEU.getEpaEmuId())) {
					selEmuInvalid = true;
				}
			} else if (EuOperatingStatusDef.SD.equals(tempEU
					.getOperatingStatusCd())) {
				shutdownEUs.add(tempEU);
				if (selEpaEmuIds != null
						&& selEpaEmuIds.contains(tempEU.getEpaEmuId())) {
					selEmuShutdown = true;
				}
			}
		}

		if (selEmuInvalid && !isPublicApp()) {
			if (!expandTree && emissionUnits.length != invalidEUs.size()) {
				treePath.add("0:" + Integer.toString(euNum));
				euNum++;
				Stars2TreeNode dummyNode = new Stars2TreeNode("moreNodes", DUMMY_MORE_NODE, "More Nodes", leaf,
						DUMMY_MORE_NODE);
				facNode.getChildren().add(dummyNode);
			}
		} else if (selEmuShutdown) {
			if (!expandTree && emissionUnits.length != shutdownEUs.size()) {
				treePath.add("0:" + Integer.toString(euNum));
				euNum++;
				Stars2TreeNode dummyNode = new Stars2TreeNode("moreNodes", DUMMY_MORE_NODE, "More Nodes", leaf,
						DUMMY_MORE_NODE);
				facNode.getChildren().add(dummyNode);
			}
		} else if (emissionUnits.length != invalidEUs.size()) {
			if (!expandTree && selEpaEmuIds != null) {
				// if (expand == 1) {
				if (expand >= 0) {
					treePath.add("0:" + Integer.toString(euNum));
				}
				euNum++;
				Stars2TreeNode dummyNode = new Stars2TreeNode("moreNodes", DUMMY_MORE_NODE, "More Nodes", leaf,
						DUMMY_MORE_NODE);
				facNode.getChildren().add(dummyNode);
			}
		}
		for (EmissionUnit tempEU : emissionUnits) {
			// skip invalid and shutdown EUs for now.
			if (EuOperatingStatusDef.IV.equals(tempEU.getOperatingStatusCd())
					|| EuOperatingStatusDef.SD.equals(tempEU
							.getOperatingStatusCd())) {
				continue;
			}

			// create tree nodes for other EUs (not shutdown or invalid).
			if (selEpaEmuIds == null) {
				setSpecEuTreeData(tempEU, facNode, "0:", euNum, expandTree,
						null);
				euNum++;
			} else if (expandTree) {
				setSpecEuTreeData(tempEU, facNode, "0:", euNum, true, null);
				euNum++;
			} else {
				// if a node was selected, display it as expanded in the tree
				String tempId = tempEU.getEpaEmuId();
				for (String tempEpaId : selEpaEmuIds) {
					if (tempEpaId.equals(tempId)) {
						if (selEpaEmuIds.size() == 1) {
							// if only one node was selected, show it as
							// expanded and
							// make it the selected node
							setSpecEuTreeData(tempEU, facNode, "0:", euNum,
									true, tempEpaId);
						} else {
							// if multiple nodes were selected, expand them all
							setSpecEuTreeData(tempEU, facNode, "0:", euNum,
									true, null);
						}
						euNum++;
						break;
					}
				}
			}
		}
		
		if (!isPublicApp()) {

			// Draw invalid EUs under a node labeled "Invalid EUs"
			if (!invalidEUs.isEmpty()) {
				// if (expand == 1) {
				if (expand >= 0) {
					treePath.add("0:" + Integer.toString(euNum));
				}
				Stars2TreeNode dummyNode = new Stars2TreeNode("InvalidEUs", DUMMY_INV_EU_NODE, "InvalidEUs", false,
						DUMMY_INV_EU_NODE);
				facNode.getChildren().add(dummyNode);
				// if (expand == 1) {
				if (expand >= 0) {
					if (!selEmuInvalid && !selEmuShutdown) {
						for (int i = 0; i < invalidEUs.size(); i++) {
							treePath.add("0:" + Integer.toString(euNum) + ":" + Integer.toString(i));
						}
					} else if (!expandTree) {
						treePath.add("0:" + Integer.toString(euNum) + ":0");
						Stars2TreeNode dummyNode1 = new Stars2TreeNode("moreNodes", DUMMY_MORE_NODE, "More Nodes", leaf,
								DUMMY_MORE_NODE);
						dummyNode.getChildren().add(dummyNode1);
					}
				}

				for (EmissionUnit tempEU : invalidEUs) {
					if (expandTree) {
						Stars2TreeNode euNode = new Stars2TreeNode(EU_NODE, tempEU.getEpaEmuId(),
								euFacNodeMapId(tempEU.getEpaEmuId()), true, tempEU);
						dummyNode.getChildren().add(euNode);
						facNodeMap.put(euFacNodeMapId(tempEU.getEpaEmuId()), euNode);
					} else {
						if (!selEmuInvalid && !selEmuShutdown) {
							Stars2TreeNode euNode = new Stars2TreeNode(EU_NODE, tempEU.getEpaEmuId(),
									euFacNodeMapId(tempEU.getEpaEmuId()), true, tempEU);
							dummyNode.getChildren().add(euNode);
							facNodeMap.put(euFacNodeMapId(tempEU.getEpaEmuId()), euNode);
						} else if (!selEmuShutdown) {
							if (selEpaEmuIds.get(0).equals(tempEU.getEpaEmuId())) {
								Stars2TreeNode euNode = new Stars2TreeNode(EU_NODE, tempEU.getEpaEmuId(),
										euFacNodeMapId(tempEU.getEpaEmuId()), true, tempEU);
								dummyNode.getChildren().add(euNode);
								facNodeMap.put(euFacNodeMapId(tempEU.getEpaEmuId()), euNode);
								break;
							}
						}
					}
				}
				euNum++;
			}

		}

		// draw shutdown EUs under a node labeled "Shutdown EUs"
		if (!shutdownEUs.isEmpty()) {
			// if (expand == 1) {
			if (expand >= 0) {
				treePath.add("0:" + Integer.toString(euNum));
			}
			Stars2TreeNode dummyNode = new Stars2TreeNode("ShutdownEUs",
					DUMMY_SD_EU_NODE, "ShutdownEUs", false, DUMMY_SD_EU_NODE);
			facNode.getChildren().add(dummyNode);
			// if (expand == 1) {
			if (expand >= 0) {
				if ((selEmuShutdown || selEmuInvalid) && !expandTree) {
					treePath.add("0:" + Integer.toString(euNum) + ":0");
					Stars2TreeNode dummyNode1 = new Stars2TreeNode("moreNodes",
							DUMMY_MORE_NODE, "More Nodes", leaf,
							DUMMY_MORE_NODE);
					dummyNode.getChildren().add(dummyNode1);
				}
			}

			for (EmissionUnit tempEU : shutdownEUs) {
				String treePathRoot = "0:" + Integer.toString(euNum) + ":";
				if (expandTree) {
					setSpecEuTreeData(tempEU, dummyNode, treePathRoot, euNum,
							true, null);
				} else {
					if (!selEmuShutdown && !selEmuInvalid) {
						setSpecEuTreeData(tempEU, dummyNode, treePathRoot,
								euNum, false, null);
					} else if (!selEmuInvalid) {
						if (selEpaEmuIds.get(0).equals(tempEU.getEpaEmuId())) {
							setSpecEuTreeData(tempEU, dummyNode, treePathRoot,
									euNum, true, tempEU.getEpaEmuId());
							break;
						}
					}
				}
			}
			euNum++;
		}

		ControlEquipment[] controlEquips = facility.getUnassignedCntEquips();
		if (controlEquips.length > 0) {
			// if (expand == 2) {
			if (expand >= 0) {
				treePath.add("0:" + Integer.toString(euNum));
			}
			UnAssignCEsNode = new Stars2TreeNode(UACE_NODE, DUMMY_CE_NODE,
					UACE_NODE, false, DUMMY_CE_NODE);
			facNode.getChildren().add(UnAssignCEsNode);
			// if (expand == 2) {
			if (expand >= 0) {
				for (int i = 0; i < controlEquips.length; i++) {
					treePath.add("0:" + Integer.toString(euNum) + ":"
							+ Integer.toString(i));
				}
			}
			int k = 0;
			String cePath;
			for (ControlEquipment tempCE : controlEquips) {
				// if (expand == 2) {
				if (expand >= 0) {
					cePath = "0:" + Integer.toString(euNum) + ":"
							+ Integer.toString(k);
				} else {
					cePath = null;
				}
				Stars2TreeNode ceNode1 = addControlEquipToTree(tempCE, cePath,
						null, expand);
				UnAssignCEsNode.getChildren().add(ceNode1);
				facNodeMap.put(euFacNodeMapId(tempCE.getControlEquipmentId()),
						ceNode1);
				k++;
			}
			euNum++;
		}

		EgressPoint[] egrPoints = facility.getUnassignedEgrPoints();
		if (egrPoints.length > 0) {
			// if (expand == 2) {
			if (expand >= 0) {
				treePath.add("0:" + Integer.toString(euNum));
			}
			UnAssignEgpsNode = new Stars2TreeNode(UAEGP_NODE,
					DUMMY_EGR_PNT_NODE, UAEGP_NODE, false, DUMMY_EGR_PNT_NODE);
			facNode.getChildren().add(UnAssignEgpsNode);
			// if (expand == 2) {
			if (expand >= 0) {
				for (int i = 0; i < egrPoints.length; i++) {
					treePath.add("0:" + Integer.toString(euNum) + ":"
							+ Integer.toString(i));
				}
			}
			for (EgressPoint tempEGR : egrPoints) {
				Stars2TreeNode egrNode1 = new Stars2TreeNode(EGP_NODE,
						tempEGR.getReleasePointId(),
						egFacNodeMapId(tempEGR.getReleasePointId()), true,
						tempEGR);
				facNodeMap.put(egFacNodeMapId(tempEGR.getReleasePointId()),
						egrNode1);
				UnAssignEgpsNode.getChildren().add(egrNode1);
			}
			euNum++;
		}

		TreeStateBase treeState = new TreeStateBase();
		treeState.expandPath(treePath.toArray(new String[0]));
		treeData.setTreeState(treeState);

		if (expandTree && byClickEpaEmuId != null) {
			selectedTreeNode = facNodeMap.get(euFacNodeMapId(byClickEpaEmuId));
			current = euFacNodeMapId(byClickEpaEmuId);
		}

		return treeData;
	}

	@SuppressWarnings("unchecked")
	private void setSpecEuTreeData(EmissionUnit tempEU,
			Stars2TreeNode parentNode, String treePathRoot, int euNum,
			boolean expand, String selEpaEmuId) {
		boolean leaf = false;

		EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses()
				.toArray(new EmissionProcess[0]);

		if (expand) {
			treePath.add(treePathRoot + Integer.toString(euNum));
		}

		for (int i = 0; i < emissionProcesses.length; i++) {
			treePath.add(treePathRoot + Integer.toString(euNum) + ":"
					+ Integer.toString(i));
		}

		if (emissionProcesses.length == 0) {
			leaf = true;
		}

		Stars2TreeNode euNode = new Stars2TreeNode(EU_NODE,
				tempEU.getEpaEmuId(), euFacNodeMapId(tempEU.getEpaEmuId()),
				leaf, tempEU);
		facNodeMap.put(euFacNodeMapId(tempEU.getEpaEmuId()), euNode);
		parentNode.getChildren().add(euNode);

		int epNum = 0;
		for (EmissionProcess tempEP : emissionProcesses) {
			leaf = false;
			ControlEquipment[] controlEquips = tempEP.getControlEquipments()
					.toArray(new ControlEquipment[0]);

			EgressPoint[] egressPoints = tempEP.getEgressPoints().toArray(
					new EgressPoint[0]);

			if ((controlEquips.length == 0) && (egressPoints.length == 0)) {
				leaf = true;
			}

			Stars2TreeNode epNode = new Stars2TreeNode(EP_NODE,
					tempEP.getProcessId(),
					epFacNodeMapId(tempEP.getProcessId()), leaf, tempEP);
			facNodeMap.put(epFacNodeMapId(tempEP.getProcessId()), epNode);

			int cep = 0;
			String cePath;
			for (ControlEquipment tempCE : controlEquips) {
				leaf = false;

				ControlEquipment[] ceControlEquips = tempCE.getControlEquips()
						.toArray(new ControlEquipment[0]);

				EgressPoint[] ceEgressPoints = tempCE.getEgressPoints()
						.toArray(new EgressPoint[0]);

				if ((ceControlEquips.length == 0)
						&& (ceEgressPoints.length == 0)) {
					leaf = true;
				}
				cePath = treePathRoot + Integer.toString(euNum) + ":"
						+ Integer.toString(epNum) + ":" + Integer.toString(cep);
				treePath.add(cePath);
				Stars2TreeNode ceNode = addControlEquipToTree(tempCE, cePath,
						tempEU.getEpaEmuId(), 2);
				cep++;

				epNode.getChildren().add(ceNode);
				ceNode.setParentFpNodeId(tempEP.getFpNodeId());
				ceNode.setParentNodeId(epFacNodeMapId(tempEP.getProcessId()));
			}

			for (EgressPoint tempEGP : egressPoints) {
				Stars2TreeNode egpNode = new Stars2TreeNode(EGP_NODE,
						tempEGP.getReleasePointId(),
						egFacNodeMapId(tempEGP.getReleasePointId()), true,
						tempEGP);

				cePath = treePathRoot + Integer.toString(euNum) + ":"
						+ Integer.toString(epNum) + ":" + Integer.toString(cep);
				treePath.add(cePath);
				cep++;
				facNodeMap.put(egFacNodeMapId(tempEGP.getReleasePointId()),
						egpNode);
				epNode.getChildren().add(egpNode);
				egpNode.setParentFpNodeId(tempEP.getFpNodeId());
				egpNode.setParentNodeId(epFacNodeMapId(tempEP.getProcessId()));
				tempEGP.setAssociatedEpaEuIds(tempEU.getEpaEmuId());
			}
			euNode.getChildren().add(epNode);
			epNum++;
		}

		if (selEpaEmuId != null) {
			selectedTreeNode = euNode;
			current = euFacNodeMapId(selEpaEmuId);
		}
	}

	public String expandFacilityTree() {
		String ret;
		byClickEpaEmuId = null;
		expandOpt = 2;
		expandTree = true;
		selEpaEmuIds = null;
		getEuTreeData(selEpaEmuIds, expandOpt);
		String allNumFacilityId = facility.getFacilityId().replace("F", "");
		selectedTreeNode = treeData.getNodeById(allNumFacilityId);
		current = facility.getFacilityId();
		ret = nodeClicked();
		return ret;
	}

	public String collaspeFacilityTree() {
		String ret;
		expandTree = false;
		byClickEpaEmuId = null;
		selEpaEmuIds = null;
		expandOpt = 0;
		getEuTreeData(selEpaEmuIds, expandOpt);
		String allNumFacilityId = facility.getFacilityId().replace("F", "");
		selectedTreeNode = treeData.getNodeById(allNumFacilityId);
		current = facility.getFacilityId();
		ret = nodeClicked();
		return ret;
	}

	/* START CODE: facility event logs */

	public final EventLog[] getEventLog() {
		EventLog[] eventLog = null;
		EventLog el = new EventLog();
		try {
			if (facilityId != null) {
				logger.debug("In getEventLog, facilityId = " + facilityId);
				el.setFacilityId(facilityId);
			} else {
				logger.error("Facility ID is null in getEventLog method."
						+ " Events for all facilities will be retrieved!");
			}
			eventLog = getFacilityService().retrieveEventLogs(el);
			if (eventLog.length == 0) {
				// DisplayUtil.displayInfo("This facility does not have any
				// events");
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Retrieving facility event log failed");
		}
		return eventLog;
	}

	public final EventLog getNewEventLog() {
		return newEventLog;
	}

	public final void setNewEventLog(EventLog newEventLog) {
		this.newEventLog = newEventLog;
	}

	public final String createNewEventLog() {
		newEventLog = new EventLog();
		newEventLog.setFpId(fpId);
		newEventLog.setFacilityId(facilityId);
		newEventLog.setDate(new Timestamp(System.currentTimeMillis()));
		newEventLog.setEventTypeDefCd(EventLogTypeDef.DAPC_NOTE);
		newEventLog.setUserId(getUserID());
		return "dialog:newEventLog";
	}

	public final void saveNewEventLog(ActionEvent actionEvent) {
		boolean operationOK = true;

		try {
			getFacilityService().createEventLog(newEventLog);
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil.displayInfo("New event log created successfully");
		} else {
			DisplayUtil.displayError("creating new event log failed");
		}

		FacesUtil.returnFromDialogAndRefresh();
	}

	/* END CODE: facility event logs */

	/* START CODE: facility roles */

	/*
	 * 
	 * @return
	 */
	public final String getFacilityRoles() {
		facility = null;
		getFacility();
		if (facRolesWrapper != null) {
			facRolesWrapper.clearWrappedData();
		}
		facRolesWrapper.setWrappedData(facility.getFacilityRoles().values()
				.toArray(new FacilityRole[0]));

		// sort by name

		SortCriterion sc = new SortCriterion("facilityRoleDsc", true);

		List<SortCriterion> criteria = new ArrayList<SortCriterion>();

		criteria.add(sc);

		facRolesWrapper.setSortCriteria(criteria);

		return "facilities.profile.facilityRoles";
	}

	public final String saveNewFacilityRoles() {
		setEditable(false);

		operation[FACILITY_COMP] = NO_OP;
		try {
			getFacilityService().updateFacilityRoles(
					facility.getFacilityRoles().values()
							.toArray(new FacilityRole[0]), facility,
					currentUserId);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Updating facility roles failed");
			return "Fail";
		}
		DisplayUtil.displayInfo("Facility roles updated successfully");
		return "facilities.profile.facilityRoles";
	}

	/* END CODE: facility roles */

	/* START CODE: facility notes */

	public final String startViewNote() {
		setEditable1(false);
		noteModify = false;
		facilityNote = new FacilityNote(modifyFacilityNote);
		return "dialog:noteDetail";
	}

	public final String startEditNote(ActionEvent actionEvent) {
		setEditable1(true);
		noteModify = true;
		return "dialog:noteDetail";
	}

	public final String startAddNote() {
		setEditable1(true);
		noteModify = false;
		facilityNote = new FacilityNote();
		facilityNote.setFpId(fpId);
		facilityNote.setFacilityId(facilityId);
		facilityNote.setUserId(getUserID());
		facilityNote.setNoteTypeCd(NoteType.DAPC);
		facilityNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
		return "dialog:noteDetail";
	}

	public final String startToAddEmissionUnitReplacement() {

		setEditable1(true);
		emissionUnitReplacement = new EmissionUnitReplacement();
		emissionUnitReplacement.setEmuId(emissionUnit.getEmuId());
		return "dialog:emissionUnitReplacementDetail";

	}

	public final String startToEditEmissionUnitReplacement() {
		this.emissionUnitReplacement.setNewObject(false);
		setEditable1(false);
		return "dialog:emissionUnitReplacementDetail";
	}

	public final void editEmissionUnitReplacement() {
		setEditable1(true);
	}

	public final void cancelEditEmissionUnitReplacement() {
		setEditable1(false);
		refreshEmissionUnitReplacements();
		this.closeDialog();
		return;
	}

	public final void saveEmissionUnitReplacement() {

		if (!validateEmissionUnitReplacement()) {
			return;
		}

		if (this.emissionUnitReplacement.isNewObject()) {
			emissionUnit
					.addEmissionUnitReplacement(this.emissionUnitReplacement);
			this.emissionUnitReplacement.setNewObject(false);
		}

		// EU is new or not
		if (!Utility.isNullOrZero(emissionUnit.getEmuId())) {
			if (facility.isCopyOnChange()) {
				try {
					copyOnChangeFacilityProfile();
					copyOnChangeEmissionUnit();
				} catch (Exception ex) {
					return;
				}
			}

			try {
				getFacilityService().saveEmissionUnitReplacements(
						emissionUnit.getEmissionUnitReplacements(),
						emissionUnit.getEmuId());
				this.facility.setValidated(false);
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil
						.displayError("Save emission unit replacement data failed");
			}
			this.refreshEmissionUnitReplacements();
		}
		
		// update fpId if it is changed due to copy on change flag
		fpId = emissionUnit.getFpId();
		closeDialog();
	}

	private void copyOnChangeEmissionUnit() throws Exception {

		Integer facFpId = new Integer(facility.getFpId());
		Integer euFpId = new Integer(emissionUnit.getFpId());

		if (!facFpId.equals(euFpId)) {

			emissionUnit.setFpId(facility.getFpId());

			String tempEpaId = emissionUnit.getEpaEmuId();
			EmissionUnit tempEU;

			if (tempEpaId != null) {

				tempEU = getFacilityService().retrieveEmissionUnit(
						emissionUnit.getFpId(), tempEpaId);

				if (tempEU != null) {
					emissionUnit.setEmuId(tempEU.getEmuId());
					emissionUnit.setLastModified(1);
				}
			}
		}
	}

	private boolean validateEmissionUnitReplacement() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		validationMessages = this.emissionUnitReplacement
				.validate(this.emissionUnit.getEmissionUnitTypeCd());

		String pageViewId = FAC_CLIENT_ID + "emissionUnit:";
		if (displayValidationMessages(pageViewId, validationMessages)) {
			isValid = false;
		}

		return isValid;
	}

	private void refreshEmissionUnitReplacements() {
		if (this.emissionUnit != null && emissionUnit.getEmuId() != null) {
			try {
				this.emissionUnit
						.setEmissionUnitReplacements(getFacilityService()
								.retrieveEmissionUnitReplacements(
										emissionUnit.getEmuId()));
				this.emissionUnit
						.setEmissionUnitReplacements(getFacilityService()
								.retrieveEmissionUnitReplacements(
										emissionUnit.getEmuId()));

			} catch (RemoteException e) {
				logger.error(e.getMessage());
				DisplayUtil
						.displayError("Could not retrieve emission unit replacements.");
			}
		}
	}

	public void deleteEmissionUnitReplacement() {
		this.emissionUnit
				.removeEmissionUnitReplacement(this.emissionUnitReplacement);
		saveEmissionUnitReplacement();
	}

	public final void dialogDone() {
		return;
	}

	public final void applyEditNote(ActionEvent actionEvent) {
		boolean operationOK = true;

		// make sure note is provided
		if (facilityNote.getNoteTxt() == null
				|| facilityNote.getNoteTxt().trim().equals("")) {
			DisplayUtil.displayError("Attribute Note is not set.");
			return;
		}

		ValidationMessage[] validationMessages = facilityNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}

		try {
			if (noteModify == false) {
				getFacilityService().createFacilityNote(facilityNote);
			} else {
				// edit
				getFacilityService().modifyFacilityNote(facilityNote);
			}

			refreshNotes();

		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil.displayInfo("Facility notes updated successfully");
		} else {
			cancelEditNote();
			DisplayUtil.displayError("Updating facility notes failed");
		}

		noteModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelEditNote() {
		noteModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final boolean isNoteModify() {
		return noteModify;
	}

	public final FacilityNote getFacilityNote() {
		return facilityNote;
	}

	public final void setFacilityNote(FacilityNote facilityNote) {
		this.facilityNote = facilityNote;
	}

	public final FacilityNote getModifyFacilityNote() {
        return modifyFacilityNote;
    }

    public final void setModifyFacilityNote(FacilityNote modifyFacilityNote) {
        this.modifyFacilityNote = modifyFacilityNote;
    }
    
	/* END CODE: facility notes */

	/* START CODE: facility history profile */

	public final UIXTable getHistoryTable() {
		return historyTable;
	}

	public final void setHistoryTable(UIXTable historyTable) {
		this.historyTable = historyTable;
	}

	public final Timestamp getRevisedHistoryDate() {
		return revisedHistoryDate;
	}

	public final void setRevisedHistoryDate(Timestamp revisedHistoryDate) {
		this.revisedHistoryDate = revisedHistoryDate;
	}

	public final FacilityNote getHistoryNote() {
		return historyNote;
	}

	public final void setHistoryNote(FacilityNote historyNote) {
		this.historyNote = historyNote;
	}

	public final EmissionUnitReplacement getEmissionUnitReplacement() {
		return emissionUnitReplacement;
	}

	public final void setEmissionUnitReplacement(
			EmissionUnitReplacement emissionUnitReplacement) {
		this.emissionUnitReplacement = emissionUnitReplacement;
	}

	public final void applyCloneHistFacilityProfile(ActionEvent actionEvent) {
		ValidationMessage[] validationMessages = historyNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("historyNote", validationMessages) == true) {
				return;
			}
		}

		try {
			getFacilityService().splitFacilityProfile(
					selectedHistory.getFpId(), revisedHistoryDate, historyNote,
					currentUserId);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Spliting Facility failed");
			revisedHistoryDate = null;
			selectedHistory = null;
			FacesUtil.returnFromDialogAndRefresh();
			return;
		}

		revisedHistoryDate = null;
		selectedHistory = null;
		DisplayUtil.displayInfo("Facility Detail cloned successfully");
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelCloneHistFacilityProfile(ActionEvent actionEvent) {
		revisedHistoryDate = null;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final FacilityVersion getSelectedHistory() {
		return selectedHistory;
	}

	public final String selectHistFacilityProfile() {
		Iterator<?> it = historyTable.getSelectionState().getKeySet()
				.iterator();
		selectedHistory = null;
		if (!it.hasNext()) {
			DisplayUtil.displayInfo("Please select a facility detail");
			return "fail";
		}

		Object obj = it.next();
		historyTable.setRowKey(obj);
		selectedHistory = (FacilityVersion) historyTable.getRowData();
		historyTable.getSelectionState().clear();
		historyNote = new FacilityNote();
		historyNote.clearValidationMessages();
		return "dialog:historyClone";
	}

	public final String getNextInHistory() {
		Integer nextVersionId = ((facility.getVersionId().equals(facility
				.getMaxVersion())) ? -1 : (facility.getVersionId() + 1));

		try {
			facility = getFacilityService().retrieveFacilityData(facilityId,
					nextVersionId);
			setFpId(facility.getFpId());
			facility = null;
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Accessing next facility detail history failed");
			return "fail";
		}

		return getFacilityProfileOutcome();
	}

	public final String getPreviousInHistory() {
		Integer preVersionId = ((facility.getVersionId()
				.equals(new Integer(-1))) ? facility.getMaxVersion()
				: (facility.getVersionId() - 1));
		try {
			facility = getFacilityService().retrieveFacilityData(facilityId,
					preVersionId);
			setFpId(facility.getFpId());
			facility = null;
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Accessing previous facility detail history failed");
			return getFacilityProfileOutcome();
		}

		return getFacilityProfileOutcome();
	}

	public final String startCreateprofileHistory() {
		confirmOperation = "createProfileHistory";
		return "dialog:confirmOperation";
	}

	/* END CODE: facility history profile */

	public final TableSorter getAddrHistWrapper() {
		return addrHistWrapper;
	}

	/* START CODE: owners */
	protected final void setFacilityOwners() {
		facilityOwnersWrapper.setWrappedData(facilityOwners);
	}

	// TODO Owner
	public String getFacilityOwners() {
		logger.debug("Getting Facility Owners for Facility Id: " + facilityId);
		String ret = OWNERS_OUTCOME;
		setEditable1(false);
		if (isPublicApp()) {
			getCurrentFacilityData();
		}
		try {
			facilityOwners = getFacilityService().retrieveFacilityOwners(
					facilityId);
			setFacilityOwners();
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing facility owners failed");
			ret = null;
		}
		return ret;
	}

	public void setFacilityOwner() {
		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				facility.setOwner(getFacilityService().retrieveFacilityOwner(
						facilityId));
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing facility owner failed");
		}
	}

	public TableSorter getFacilityOwnersWrapper() {
		return facilityOwnersWrapper;
	}

	public void setFacilityOwnersWrapper(TableSorter facilityOwnersWrapper) {
		this.facilityOwnersWrapper = facilityOwnersWrapper;
	}

	/* START CODE: contacts */
	protected final void setFacilityContacts() {
		
		facilityContacts = new ArrayList<ContactUtil>();
		ContactUtil tempCU;
		
		if (allContacts != null) {
			
			for (Contact tempContact : allContacts) {
				if (tempContact.getContactTypes().isEmpty()) {
					// facilityContacts.add(new ContactUtil(tempContact));
					continue;
				}

				for (ContactType tempCT : tempContact.getContactTypes()) {
					tempCU = new ContactUtil(tempContact, tempCT);
					facilityContacts.add(tempCU);
				}

			}
		}
		
		facilityContactsWrapper.setWrappedData(facilityContacts);

		// must sort by end date
		SortCriterion sc = new SortCriterion("endDate", true);
		List<SortCriterion> criteria = new ArrayList<SortCriterion>();
		criteria.add(sc);
		facilityContactsWrapper.setSortCriteria(criteria);
	}

	public String getFacilityContacts() {
		String ret = CONTACTS_OUTCOME;
		setEditable1(false);
		setActiveContacts(false);
		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				allContacts = getFacilityService()
						.retrieveFacilityActiveContacts(facilityId, false);
			} else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
				allContacts = new ArrayList<Contact>();
			} else {
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				myTasks.setFromHomeContact(false);
				allContacts = getFacilityService().retrieveFacilityContacts(
						facilityId, true);
			}
			setFacilityContacts();
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing facility contacts failed");
			ret = null;
		}
		return ret;
	}

	public String getActiveFacilityContacts() {
		String ret = CONTACTS_OUTCOME;
		setEditable1(false);
		setActiveContacts(true);
		try {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				allContacts = getFacilityService()
						.retrieveFacilityActiveContacts(facilityId, true);
			} else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
				allContacts = new ArrayList<Contact>();
			} else {
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				myTasks.setFromHomeContact(false);
				allContacts = getFacilityService().retrieveFacilityContacts(
						facilityId, true);
			}
			setFacilityContacts();
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing facility contacts failed");
			ret = null;
		}
		return ret;
	}

	public boolean isActiveContacts() {
		return activeContacts;
	}

	public void setActiveContacts(boolean activeContacts) {
		this.activeContacts = activeContacts;
	}

	public boolean isEditStaging() {
		return this.editStaging;
	}

	public void setEditStaging(boolean isEdit) {
		this.editStaging = isEdit;
	}

	public String startAddTypeToContact() {
		// get global contacts
		try {
			setGlobalContacts(getFacilityService().retrieveAllContacts());
			setAllFacilityContacts(getFacilityService()
					.retrieveFacilityContacts(facilityId));
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed");
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed");
		}
		facContact = new ContactUtil();
		contactModify = false;
		if (globalContacts.isEmpty()) {
			facContact
					.setMessage("There are no contacts defined; please cancel this operation and create a global contact.");
		}
		return "dialog:addTypeToContact";
	}

	public List<Contact> getGlobalContacts() {
		return this.globalContacts;
	}

	public void setGlobalContacts(List<Contact> globalContacts) {
		this.globalContacts = globalContacts;
	}

	public final String startAddOwnerToContact() {
		facContact = new ContactUtil();
		contactModify = false;
		if (allContacts.isEmpty()) {
			facContact
					.setMessage("There are no contacts defined for this facility; please cancel this operation and create a contact person");
		}
		// setFacContactTypeCd(ContactTypeDef.OWNR);
		return "dialog:addTypeToContact";
	}

	public final String startAddContact() {
		setEditable1(true);

		contactModify = false;
		contact = new Contact();

		Address tempAddress = new Address();
		contact.setAddress(tempAddress);
		contact.getAddress().setCountryCd("US");

		contact.setFacilityId(facilityId);
		return "dialog:addContact";
	}

	public final void addContactType() {
		ContactType tempCt = new ContactType();
		contactTypes.add(tempCt);
	}

	public final void cancelAddContact() {
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void editContact() {
		setEditable1(true);
		contactModify = true;
	}

	public void editContactType() {
		setEditable1(true);
		contactModify = true;
		facContact.setMessage(null);
		saveContactType = new ContactType(facContact.getContactType());
	}

	public void deleteContactType() {
		setEditable1(true);
		contactModify = true;
		facContact.setMessage(null);
		if (facContact.getContactType().getContactTypeCd()
				.equals(ContactTypeDef.RSOF)) {
			facContact.setMessage(RO_DEL_CAUTION_MSG);
		} else {
			facContact
					.setMessage("You are about to delete this contact type. The contact person will remain but not as this contact type during the start and end dates. Please Save or Cancel.");
		}
		saveContactType = null;
	}

	public final boolean isContactTypeDelDisable() {
		if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| InfrastructureDefs.getCurrentUserAttrs()
						.isCurrentUseCaseValid(
								"facilities.profile.contacts.deleteTypeDates")) {
			return false;
		}
		return true;
	}

	public final boolean isContactDatesReadOnly() {
		boolean ret = !editable1;

		if (isDapcUser() && editable1 && saveContactType == null) {
			ret = true;
		}
		return ret;
	}

	public final void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void closeAddrDialog() throws RemoteException {
		setEditable1(false);
		closeDialog();
	}

	public final String loadContactAddress() {
		if (facility == null)
			logger.error("facility is null");
		Address a = facility.getPhyAddr();
		if (a == null)
			logger.error("a is null for facility " + facility.getFacilityId());
		if (contact == null)
			logger.error("contact is null for facility "
					+ facility.getFacilityId());
		Address b = contact.getAddress();
		if (b == null)
			logger.error("address is null for facility "
					+ facility.getFacilityId());
		b.copyAddress(a);
		return null;
	}

	public final Contact getContact() {
		return contact;
	}

	public final void setContact(Contact contact) {
		this.contact = contact;
	}

	public final ContactUtil getFacContact() {
		return facContact;
	}

	public void setFacContact(ContactUtil facContact) {
		this.facContact = facContact;
	}

	public final String getFacContactTypeCd() {
		return facContact.getContactType().getContactTypeCd();
	}

	public void refreshContactList(String contactTypeCd) {
		String name;
		SelectItem cont;
		contactsList = new ArrayList<SelectItem>();

		try {
			if (allFacilityContacts == null) {
				setAllFacilityContacts(getFacilityService()
						.retrieveFacilityContacts(facilityId));
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage());
			DisplayUtil.displayError("Could not retrieve facility contacts.");
		}

		if (useGlobalContactList) {
			for (Contact tempContact : globalContacts) {
				for (Contact facilityContact : allFacilityContacts) {
					if (facilityContact.getContactId().equals(
							tempContact.getContactId())) {
						tempContact = facilityContact;
						break;
					}
				}

				if (tempContact.isCurrentContactType(facContact
						.getContactType().getContactTypeCd(), facilityId)) {
					continue;
				}

				name = tempContact.getName() + " ("
						+ tempContact.getCompanyName() + ")";
				cont = new SelectItem(tempContact.getContactId(), name);
				contactsList.add(cont);
			}
		} else {
			int ownerCompanyId = facility.getOwner().getCompany()
					.getCompanyId().intValue();

			for (Contact tempContact : globalContacts) {
				for (Contact facilityContact : allFacilityContacts) {
					if (facilityContact.getContactId().equals(
							tempContact.getContactId())) {
						tempContact = facilityContact;
						break;
					}
				}

				name = tempContact.getName();
				if (tempContact.isCurrentContactType(facContact
						.getContactType().getContactTypeCd(), facilityId)) {
					continue;
				}

				if (tempContact.getCompanyId().intValue() == ownerCompanyId) {
					cont = new SelectItem(tempContact.getContactId(), name);
					contactsList.add(cont);
				}
			}
		}
	}

	public void setFacContactTypeCd(String contactTypeCd) {
		SelectItem cont;
		facContact = new ContactUtil();
		facContact.getContactType().setContactTypeCd(contactTypeCd);
		useGlobalContactList = false;

		if (globalContacts.isEmpty()) {
			facContact.getContactType().setContactId(-1);
			cont = new SelectItem(0, "N/A - NO CONTACTS");

			contactsList.add(cont);
			facContact
					.setMessage("This facility has no contact person(s) defined; please cancel this request and add one or more contact person(s) first");
			return;
		}

		refreshContactList(contactTypeCd);
	}

	public List<SelectItem> getContactsList() {
		return contactsList;
	}

	public boolean isUseGlobalContactList() {
		return useGlobalContactList;
	}

	public void setUseGlobalContactList(boolean useGlobalContactList) {
		boolean needrefreshContactList = this.useGlobalContactList != useGlobalContactList;
		this.useGlobalContactList = useGlobalContactList;

		if (needrefreshContactList) {
			refreshContactList(getFacContactTypeCd());
		}
	}

	public final List<Contact> retrieveFacilityContacts() {
		return allContacts;
	}

	public void saveEditContactType() {
		boolean operationOK = true;
		// Contact stagingContact = null;
		ValidationMessage[] validationMessages = facContact.getContactType()
				.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages(CONTACT_CLIENT_ID, validationMessages)) {
				return;
			}
		}
		// if (contactModify == false) {
		// if (globalContacts != null) {
		// for (Contact tempContact : globalContacts) {
		// if (tempContact.getContactId().intValue() == facContact
		// .getContactType().getContactId()) {
		// // stagingContact = tempContact;
		// }
		// }
		// }
		//
		// }

		// must use all facility contacts for validation purposes
		try {
			setGlobalContacts(getFacilityService().retrieveFacilityContacts(
					facilityId));
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed");
		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed");
		}

		validationMessages = FacilityValidation.validateEditContactType(
				saveContactType, facContact.getContactType(), allContacts,
				globalContacts, contactModify)
				.toArray(new ValidationMessage[0]);
		if (validationMessages.length > 0) {
			if (displayValidationMessages(CONTACT_CLIENT_ID, validationMessages)) {
				return;
			}
		}

		try {
			if (contactModify == false) {
				getInfrastructureService().addContactType(
						facContact.getContactType(), fpId, currentUserId,
						facilityId);
			} else if (saveContactType != null) {
				// edit
				getInfrastructureService().modifyContactType(saveContactType,
						facContact.getContactType(), fpId, currentUserId);
			} else {
				// delete
				getInfrastructureService().deleteContactType(
						facContact.getContactType(), fpId, currentUserId);
			}

			if (activeContacts) {
				allContacts = getFacilityService()
						.retrieveActiveFacilityContacts(facilityId, staging);
			} else {
				allContacts = getFacilityService().retrieveFacilityContacts(
						facilityId, staging);
			}

			setFacilityContacts();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil
					.displayInfo("Facility contact type data updated successfully");
		} else {
			cancelEditContact();
			DisplayUtil
					.displayError("Updating facility contact type data failed");
		}

		contactModify = false;
		setEditable1(false);

		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelEditContact() {
		contact = null;
		facContact = null;

		try {
			if (activeContacts) {
				allContacts = getFacilityService()
						.retrieveActiveFacilityContacts(facilityId, staging);
			} else {
				allContacts = getFacilityService().retrieveFacilityContacts(
						facilityId, staging);
			}

			setFacilityContacts();
		} catch (RemoteException re) {
			handleException(re);
		}

		contactModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelEditContactType() {
		saveContactType = null;
		cancelEditContact();
	}

	/* END CODE: owners/contacts */

	/* START CODE: NAICS */
	public static String getNaicsDesc(String code) {
		if (Utility.isNullOrEmpty(code))
			return "";

		String desc = NAICSDef.getData().getItem(code).getDescription();
		return desc;
	}

	public Stars2Object getModifyNaics() {

		return this.modifyNaics;
	}

	public void setModifyNaics(Stars2Object modifyNaics) {
		this.modifyNaics = modifyNaics;
	}

	public boolean isNewNaics() {
		return newNaics;
	}

	public void setNewNaics(boolean newNaics) {
		this.newNaics = newNaics;
	}

	private final void initializeNaicsCodes() {
		this.naicsCdsWrapper = new TableSorter();
		try {
			this.facility.setNaicsCds(getNaicsService().retrieveNAICSCodes(
					this.fpId));
			this.naicsCds = Stars2Object.toStar2Object(this.facility
					.getNaicsCds());
			this.naicsCdsWrapper.setWrappedData(this.naicsCds);

		} catch (RemoteException re) {
			logger.error("failed to get NAICS", re);
		} finally {
			this.editable1 = false;
		}
	}

	public final String startToAddFacilityNaics() {
		this.editable1 = true;
		this.modifyNaics = new Stars2Object("");
		this.newNaics = true;

		return "dialog:naicsDetail";
	}

	public final String startToEditFacilityNaics() {
		int index = this.naicsCdsWrapper.getRowIndex();
		this.modifyNaicsIndex = index;
		this.modifyNaics = getSelectedNaics(index);

		return "dialog:naicsDetail";
	}

	public final void editFacilityNaics() {
		this.editable1 = true;
		this.newNaics = false;
	}

	public final void saveFacilityNaics() {
		if (facility.isCopyOnChange()) {
			try {
				copyOnChangeFacilityProfile();
			} catch (Exception e) {
				return;
			}
		}

		DisplayUtil.clearQueuedMessages();

		if (this.newNaics) {
			createFacilityNaics();
		} else {
			updateFacilityNaics();
		}

	}

	private void copyOnChangeFacilityProfile() throws Exception {
		Facility newFacility = getFacilityService().copyFacilityProfile(
				facility.getFpId(), new Timestamp(System.currentTimeMillis()),
				currentUserId);
		if (newFacility == null) {
			DAOException e = new DAOException(
					"Cannot access facility to modify it.");
			throw e;
		}
		
		newFacility = getFacilityService().updateLatSubmissionType(newFacility); // see TFS task 7215

		if (!newFacility.getFpId().equals(facility.getFpId())) {
			facility.setCopyOnChange(newFacility.isCopyOnChange());
			facility.setFpId(newFacility.getFpId());
			facility.setVersionId(newFacility.getVersionId());
			facility.setStartDate(newFacility.getStartDate());
			facility.setLastModified(newFacility.getLastModified());
			facility.getPhyAddr().setAddressId(
					newFacility.getPhyAddr().getAddressId());
			facility.setApis(newFacility.getApis());
			facility.setAddresses(newFacility.getAddresses());
			facility.setFacilityCemComLimitList(newFacility.getFacilityCemComLimitList());
			facility.setContinuousMonitorList(newFacility.getContinuousMonitorList());
			fpId = newFacility.getFpId();
		}
		
		
	}

	public void closeFacilityNaicsDialog() {
		this.refreshFacilityNaics();
		closeDialog();
	}

	private final Stars2Object getSelectedNaics(int index) {
		Stars2Object naics = (Stars2Object) this.naicsCdsWrapper
				.getRowData(index);

		return naics;
	}

	protected final void refreshFacilityNaics() {
		this.editable1 = false;
		this.newNaics = false;
		this.initializeNaicsCodes();
	}

	public void createFacilityNaics() {
		String currentNaics = (String) this.modifyNaics.getValue();

		if (Utility.isNullOrEmpty(currentNaics)) {
			DisplayUtil.displayError("ERROR: Attribute NAICS is not set.");
			return;
		}

		for (Stars2Object temp : this.naicsCds) {
			if (temp.getValue().toString().compareTo(currentNaics) == 0) {
				DisplayUtil
						.displayError("Invalid NAICS Code. NAICS Code must be unique");
				return;
			}
		}

		List<String> naicses = Stars2Object.fromStar2Object(this.naicsCds);

		try {
			// Modify NAICS codes
			naicses.add(currentNaics);

			getNaicsService().addFacilityNAICSs(fpId, naicses);
			DisplayUtil.displayInfo("Create Facility NAICS Success");

		} catch (DAOException e) {
			logger.error("Create Facility NAICS Failed", e);
			DisplayUtil.displayError("Create Facility NAICS Failed");

		} finally {
			if (!this.editable) {
				refreshFacility();
				selectedTreeNode = facNode;
				current = facilityId;
			}

			this.refreshFacilityNaics();
			closeDialog();
		}
	}

	public void updateFacilityNaics() {
		String currentNaics = (String) this.modifyNaics.getValue();

		if (Utility.isNullOrEmpty(currentNaics)) {
			DisplayUtil.displayError("ERROR: Attribute NAICS is not set.");
			return;
		}

		int count = 0;

		for (Stars2Object temp : this.naicsCds) {
			if (temp.getValue().toString().compareTo(currentNaics) == 0) {
				count++;
			}
		}

		if (count > 1) {
			DisplayUtil
					.displayError("Invalid NAICS Code. NAICS Code must be unique");
			return;
		}

		List<String> naicses = Stars2Object.fromStar2Object(this.naicsCds);

		try {
			getNaicsService().addFacilityNAICSs(fpId, naicses);
			DisplayUtil.displayInfo("Create Facility NAICS Success");

		} catch (DAOException e) {
			logger.error("Update Facility NAICS Failed", e);
			DisplayUtil.displayError("Update Facility NAICS Failed");

		} finally {
			if (!this.editable) {
				refreshFacility();
				selectedTreeNode = facNode;
				current = facilityId;
			}
			this.refreshFacilityNaics();
			closeDialog();
		}
	}

	public void deleteFacilityNaics() {
		if (facility.isCopyOnChange()) {
			try {
				copyOnChangeFacilityProfile();
			} catch (Exception ex) {
				return;
			}
		}

		DisplayUtil.clearQueuedMessages();

		try {
			getNaicsService().deleteFacilityNaics(this.fpId,
					(String) this.modifyNaics.getValue());
			DisplayUtil.displayInfo("Delete Facility NAICS Code Success");

		} catch (DAOException e) {
			logger.error("Delete Facility NAICS Code failed", e);
			DisplayUtil.displayError("Delete Facility NAICS Code failed");

		} finally {
			if (!this.editable) {
				refreshFacility();
				selectedTreeNode = facNode;
				current = facilityId;
			}
			refreshFacilityNaics();
			closeDialog();
		}
	}

	public final void cancelFacilityNaics() {
		refreshFacilityNaics();
		this.modifyNaics = getSelectedNaics(this.modifyNaicsIndex);
	}

	/* END CODE: NAICS */

	/* START CODE: API */
	public ApiGroup getModifyApiGroup() {
		return modifyApiGroup;
	}

	public void setModifyApiGroup(ApiGroup modifyApiGroup) {
		this.modifyApiGroup = modifyApiGroup;
	}

	private final void initializeFacilityApis() {
		this.facilityApisWrapper = new TableSorter();
		try {
			this.facility.setApis(getFacilityService().retrieveFacilityApis(
					this.fpId));
			this.facilityApisWrapper.setWrappedData(this.facility.getApis());
		} catch (RemoteException re) {
			logger.error("failed to get api group", re);
		} finally {
			this.editable1 = false;
		}
	}

	public TableSorter getFacilityApisWrapper() {
		return this.facilityApisWrapper;
	}

	public final String startToAddFacilityApi() {
		this.editable1 = true;

		this.modifyApiGroup = new ApiGroup();
		this.modifyApiGroup.setFpId(this.fpId);
		this.modifyApiGroup.setNewObject(true);
		FacesUtil.startModelessDialog("../facilities/apiDetail.jsf", 250, 420);

		return null;
	}

	public final String startToEditFacilityApi() {
		int index = this.facilityApisWrapper.getRowIndex();
		this.modifyApiIndex = index;
		this.modifyApiGroup = getSelectedApi(index);
		this.modifyApiGroup.setNewObject(false);
		this.modifyApiGroup.setFpId(this.fpId);
		this.editable1 = false;
		this.oldApiGroup = getSelectedApi(index);

		return "dialog:apiDetail";
	}

	public final void editFacilityApi() {
		this.editable1 = true;
		this.modifyApiGroup.setNewObject(false);
	}

	public final String saveFacilityApi() {
		String ret = null;

		if (facility.isCopyOnChange()) {
			try {
				copyOnChangeFacilityProfile();
				updateModifyApiGroup();
			} catch (Exception ex) {
				return null;
			}
		}

		DisplayUtil.clearQueuedMessages();
		
		if (modifyApiGroup.isNewObject()) {
			createFacilityApi();
		} else {
			updateFacilityApi();
		}

		return ret;
	}

	private void updateModifyApiGroup() {
		modifyApiGroup.setFpId(facility.getFpId());
		if (modifyApiGroup.isNewObject())
			return;
		for (ApiGroup temp : this.facility.getApis()) {
			if (temp.getApiNo().equals(oldApiGroup.getApiNo())) {
				this.modifyApiGroup.setApiCd(temp.getApiCd());
			}
		}
	}

	public void closeFacilityApiDialog() {
		closeDialog();
		this.refreshFacilityApis();
	}

	public void closeNewFacilityApiDialog() {
		this.refreshFacilityApis();
	}

	private final ApiGroup getSelectedApi(int index) {
		ApiGroup api = (ApiGroup) this.facilityApisWrapper.getRowData(index);

		return api.copy();
	}

	public final void refreshFacilityApis() {
		this.editable1 = false;
		this.initializeFacilityApis();
	}

	public void createFacilityApi() {
		ValidationMessage[] errs = this.modifyApiGroup.validate();
		if (errs.length > 0) {
			for (ValidationMessage error : errs) {
				DisplayUtil.displayError(error.getMessage());
			}

			return;
		}

		String cuurentApiNo = this.modifyApiGroup.getApiNo();

		for (ApiGroup temp : this.facility.getApis()) {
			String tempApiNo = temp.getApiNo();
			if (temp.getApiCd() != this.modifyApiGroup.getApiCd()
					&& cuurentApiNo.equals(tempApiNo)) {
				DisplayUtil
						.displayError("Invalid API Number. Api Number must be unique");
				return;
			}
		}
		
		if(isDuplicateApiNo(cuurentApiNo)) {
			return;
		}

		try {
			getFacilityService().createFacilityApi(this.modifyApiGroup);
			DisplayUtil.displayInfo("Create Facility API Success");

		} catch (DAOException e) {
			logger.error("Create Facility API Failed", e);
			DisplayUtil.displayError("Create Facility API Failed");

		} finally {
			if (!this.editable) {
				refreshFacility();
				selectedTreeNode = facNode;
				current = facilityId;
			}

			this.refreshFacilityApis();

			// reset API detail window
			this.editable1 = true;

			this.modifyApiGroup = new ApiGroup();
			this.modifyApiGroup.setFpId(this.fpId);
			this.modifyApiGroup.setNewObject(true);

			FacesUtil.refreshMainWindow();
		}
	}

	public void updateFacilityApi() {
		ValidationMessage[] errs = this.modifyApiGroup.validate();
		if (errs.length > 0) {
			for (ValidationMessage error : errs) {
				DisplayUtil.displayError(error.getMessage());
			}

			return;
		}

		String cuurentApiNo = this.modifyApiGroup.getApiNo();

		for (ApiGroup temp : this.facility.getApis()) {
			String tempApiNo = temp.getApiNo();
			if (temp.getApiCd() != this.modifyApiGroup.getApiCd()
					&& cuurentApiNo.equals(tempApiNo)) {
				DisplayUtil
						.displayError("Invalid API Number. Api Number must be unique");
				return;
			}
		}
		
		if(isDuplicateApiNo(cuurentApiNo)) {
			return;
		}

		try {
			getFacilityService().updateFacilityApi(this.modifyApiGroup);

			DisplayUtil.displayInfo("Update Facility API Success");

		} catch (DAOException e) {
			logger.error("Update Facility API failed", e);
			DisplayUtil.displayError("Update Facility API Failed");

		} finally {
			if (!this.editable) {
				refreshFacility();
				selectedTreeNode = facNode;
				current = facilityId;
			}

			this.refreshFacilityApis();
			closeDialog();
		}
	}

	public void deleteFacilityApi() {
		try {
			if (facility.isCopyOnChange()) {
				try {
					copyOnChangeFacilityProfile();
					updateModifyApiGroup();
				} catch (Exception ex) {
					return;
				}
			}

			DisplayUtil.clearQueuedMessages();

			getFacilityService().deleteFacilityApi(
					this.modifyApiGroup.getApiCd());
			DisplayUtil.displayInfo("Delete Facility API Success");

		} catch (DAOException e) {
			logger.error("Delete Facility API failed", e);
			DisplayUtil.displayError("Delete Facility API failed");

		} finally {
			if (!this.editable) {
				refreshFacility();
				selectedTreeNode = facNode;
				current = facilityId;
			}

			refreshFacilityApis();
			closeDialog();
		}
	}

	public final void cancelFacilityApi() {
		this.editable1 = false;
		this.modifyApiGroup = getSelectedApi(this.modifyApiIndex);
		this.modifyApiGroup.setNewObject(false);
	}

	/* END CODE: API */

	public final Correspondence[] getCorrespondences() {
		// for now
		correspondences = new Correspondence[2];
		Correspondence temp = new Correspondence();
		temp.setCorrespondId(112);
		temp.setType("temp");
		temp.setDate(new Timestamp(System.currentTimeMillis()));
		temp.setsentById(1);
		temp.setSentTo("Smith A.");
		correspondences[0] = temp;
		temp = new Correspondence();
		temp.setCorrespondId(114);
		temp.setType("perm");
		temp.setDate(new Timestamp(System.currentTimeMillis()));
		temp.setsentById(1);
		temp.setSentTo("Smith A.");
		correspondences[1] = temp;

		return correspondences;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void buildContEquipTypeData() {
		BuildComponent.cleanUp(contEquipTypeData);
		contEquipTypeData = new CorePanelForm();
		List children = contEquipTypeData.getChildren();
		children.clear();

		if (controlEquipment == null) {
			return;
		}

		ceDataDetails = controlEquipment.getCeDataDetails().toArray(
				new DataDetail[0]);

		for (DataDetail dataDetail : ceDataDetails) {
			BuildComponent bc = new BuildComponent();
			if (dataDetail.getDataDetailVal() != null
					&& dataDetail.getDataDetailVal().length() > 70) {
				dataDetail.setDataTypeId(7);
			}
			bc.setDataDetail(dataDetail);
			bc.setId(dataDetail.getJspId());
			bc.setReadOnly(!editable);
			bc.setShowRequired(dataDetail.isRequired());

			children.add(bc.byDataTypeId());
		}
	}

	public final String getCntEquipType() {
		return null == controlEquipment ? null : controlEquipment
				.getEquipmentTypeCd();
	}

	public final void setCntEquipType(String cntEquipType) {
		controlEquipment.setEquipmentTypeCd(cntEquipType);
		DataDetail[] tdata;
		try {
			tdata = getFacilityService().retrieveContEquipDataDetail(
					controlEquipment.getEquipmentTypeCd());
			controlEquipment.setCeDataDetails(tdata);
			buildContEquipTypeData();
		} catch (RemoteException re) {
			handleException(re);
		}
	}

	public final CorePanelForm getContEquipTypeData() {
		return contEquipTypeData;
	}

	public final void setContEquipTypeData(CorePanelForm data) {
		this.contEquipTypeData = data;
	}

	public final void deleteFacilityEmissions() {
	}

	public final UIXTable getEgressPointCemTable() {
		return egressPointCemTable;
	}

	public final void setEgressPointCemTable(UIXTable egressPointCemTable) {
		this.egressPointCemTable = egressPointCemTable;
	}

	public final MultiEstabFacilityList[] getMultiEstabFacilities() {
		return multiEstabFacilities;
	}

	public final EuEmission[] getFacilityEmissions() {
		EuEmission[] euEmissions = null;
		return euEmissions;
	}

	public final List<Stars2Object> getSicCodes() {
		return sicCds;
	}

	public final void addSicCd() {
		sicCds.add(new Stars2Object(""));
		sicCdsWrapper.positionToLastBlock(getPageLimit().intValue());
	}

	public final List<Stars2Object> deleteStars2Objects(UIXTable table,
			List<Stars2Object> inObjs) {
		List<Stars2Object> retObjs;
		List<Stars2Object> delObjects = new ArrayList<Stars2Object>();

		Iterator<?> it = table.getSelectionState().getKeySet().iterator();
		Object oldKey = table.getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			table.setRowKey(obj);
			delObjects.add((Stars2Object) table.getRowData());
		}

		retObjs = Stars2Object.deleteItems(inObjs, delObjects);

		table.setRowKey(oldKey);
		table.getSelectionState().clear();

		return retObjs;
	}

	public final void deleteSicCds() {
		sicCds = deleteStars2Objects(sicCdsWrapper.getTable(), sicCds);
	}

	public final List<Stars2Object> getNaicsCodes() {
		return naicsCds;
	}

	public final void deleteNaicsCds() {
		naicsCds = deleteStars2Objects(naicsCdsWrapper.getTable(), naicsCds);
	}

	public final List<Stars2Object> getMactSubparts() {
		return mactSubparts;
	}

	public final void addMactSubpart() {
		mactSubparts.add(new Stars2Object(""));
		mactSubpartsWrapper.positionToLastBlock(getPageLimit().intValue());
	}

	public final void deleteMactSubparts() {
		mactSubparts = deleteStars2Objects(mactSubpartsWrapper.getTable(),
				mactSubparts);
	}

	public List<PollutantCompCode> getNeshapsSubparts() {
		return neshapsSubparts;
	}

	public void setNeshapsSubparts(List<PollutantCompCode> neshapsSubparts) {
		this.neshapsSubparts = neshapsSubparts;
	}

	public final void addNeshapsSubpart() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			// Add row to table.
			addedneshapsRow = true;
			neshapsSubparts.add(new PollutantCompCode());
			if (neshapsSubparts.size() == 1) {
				neshapsSubpartsWrapper.setWrappedData(neshapsSubparts);
			} else {
				neshapsSubpartsWrapper.positionToLastBlock(getPageLimit()
						.intValue());
			}
		} finally {
			clearButtonClicked();
		}
		return;
	}

	public final void deleteNeshapsSubparts() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			// delete row of table.
			boolean illegalDelete = false;

			UIXTable table = neshapsSubpartsWrapper.getTable();
			@SuppressWarnings("unchecked")
			Iterator<RowKeySet> selection = table.getSelectionState()
					.getKeySet().iterator();
			String oldKey = (String) table.getRowKey();
			while (selection.hasNext()) {
				table.setRowKey(selection.next());
				PollutantCompCode row = (PollutantCompCode) table.getRowData();
				row.setSelected(true);
			}
			// restore the old key:
			table.setRowKey(oldKey);

			Iterator<PollutantCompCode> i = neshapsSubparts.iterator();
			while (i.hasNext()) {
				PollutantCompCode pcc = i.next();

				if (pcc.isDelete() || pcc.isSelected()) {
					if (ComplianceStatusDef.YES
							.equals(pcc.getPollutantCompCd())) {
						i.remove();
					} else if (!illegalDelete) {
						illegalDelete = true;
						DisplayUtil
								.displayError("You are not allowed to delete rows from NESHAPS table where pollutant is not in compliance.");
					}
				}
			}
			if (neshapsSubparts.size() == 0) {
				neshapsSubpartsWrapper.setWrappedData(neshapsSubparts);
			} else {
				if (addedneshapsRow) {
					neshapsSubpartsWrapper.positionToLastBlock(getPageLimit()
							.intValue());
				}
			}
		} finally {
			clearButtonClicked();
		}
		return;
	}

	public final List<Stars2Object> getNspsSubparts() {
		return nspsSubparts;
	}

	public final void addNspsSubpart() {
		nspsSubparts.add(new Stars2Object(""));
		nspsSubpartsWrapper.positionToLastBlock(getPageLimit().intValue());
	}

	public final void deleteNspsSubparts() {
		nspsSubparts = deleteStars2Objects(nspsSubpartsWrapper.getTable(),
				nspsSubparts);
	}

	public final void addEuEmission() {
		editEmissionUnit();
		EuEmission emission = new EuEmission();
		emissionUnit.addEuEmission(emission);
	}

	public final void deleteEuEmissions() {
		List<EuEmission> delObjects = new ArrayList<EuEmission>();
		Iterator<?> it = euEmissionsWrapper.getTable().getSelectionState()
				.getKeySet().iterator();
		Object oldKey = euEmissionsWrapper.getTable().getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			euEmissionsWrapper.getTable().setRowKey(obj);
			delObjects.add((EuEmission) euEmissionsWrapper.getTable()
					.getRowData());
		}

		for (EuEmission delObject : delObjects) {
			emissionUnit.removeEuEmission(delObject);
		}

		euEmissionsWrapper.getTable().setRowKey(oldKey);
		euEmissionsWrapper.getTable().getSelectionState().clear();
	}

	public final void addPollutantControlled() {
		PollutantsControlled cePollutant = new PollutantsControlled();
		controlEquipment.addPollutantsControlled(cePollutant);
		pollutantsContWrapper.positionToLastBlock(getPageLimit().intValue());
	}

	public final void deletePollutantsControlled() {
		List<PollutantsControlled> delObjects = new ArrayList<PollutantsControlled>();
		Iterator<?> it = pollutantsContWrapper.getTable().getSelectionState()
				.getKeySet().iterator();
		Object oldKey = pollutantsContWrapper.getTable().getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			pollutantsContWrapper.getTable().setRowKey(obj);
			delObjects.add((PollutantsControlled) pollutantsContWrapper
					.getTable().getRowData());
		}

		for (PollutantsControlled delObject : delObjects) {
			controlEquipment.removePollutantsControlled(delObject);
		}

		pollutantsContWrapper.getTable().setRowKey(oldKey);
		pollutantsContWrapper.getTable().getSelectionState().clear();
	}

	public final void addEgressPointCem() {
		EgressPointCem cem = new EgressPointCem();
		egressPoint.addCem(cem);
	}

	public final void deleteEgressPointCems() {
		List<EgressPointCem> delObjects = new ArrayList<EgressPointCem>();
		Iterator<?> it = egressPointCemTable.getSelectionState().getKeySet()
				.iterator();
		Object oldKey = egressPointCemTable.getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			egressPointCemTable.setRowKey(obj);
			delObjects.add((EgressPointCem) egressPointCemTable.getRowData());
		}

		for (EgressPointCem delObject : delObjects) {
			egressPoint.getCems().remove(delObject);
		}

		egressPointCemTable.setRowKey(oldKey);
		egressPointCemTable.getSelectionState().clear();
	}

	public final SccCodeConverter getSccCodeConverter() {
		return sccCodeConverter;
	}

	public final String getProcSccIdLevel1() {
		return emissionProcess.getSccCode().getSccIdL1Cd();
	}

	public final String getProcSccIdLevel2() {
		return emissionProcess.getSccCode().getSccIdL2Cd();
	}

	public final String getProcSccIdLevel3() {
		return emissionProcess.getSccCode().getSccIdL3Cd();
	}

	public final String getProcSccIdLevel4() {
		return emissionProcess.getSccCode().getSccIdL4Cd();
	}

	public final String getSelectSccMethod() {
		return selectSccMethod;
	}

	public final void setSelectSccMethod(String selectSccMethod) {
		this.selectSccMethod = selectSccMethod;
		if (selectSccMethod.equals("searchSCC")) {
			sccCodes = null;
			sccSearchkeyWords = null;
		}
	}

	public final String getSccSearchkeyWords() {
		return sccSearchkeyWords;
	}

	public final void setSccSearchkeyWords(String sccSearchkeyWords) {
		this.sccSearchkeyWords = sccSearchkeyWords;
	}

	public final SccCode[] getSccCodes() {
		return sccCodes;
	}

	public final String searchSccCodes() {
		sccCodes = null;
		if (sccSearchkeyWords == null || sccSearchkeyWords.equals("")) {
			DisplayUtil.displayInfo("Please enter a search SCC key word.");
			return "Fail";
		}

		sccSearchkeyWords = sccSearchkeyWords.trim();
		StringTokenizer tokenizer = new StringTokenizer(sccSearchkeyWords, " ");

		List<String> searchSccList = new ArrayList<String>();
		String nextToken;

		while (tokenizer.hasMoreTokens()) {
			nextToken = tokenizer.nextToken();
			searchSccList.add(nextToken);
		}

		try {
			sccCodes = getInfrastructureService().retrieveSccCodes(
					searchSccList);
			if (sccCodes.length == 0) {
				DisplayUtil
						.displayInfo("SCC codes for the search key not found.");
				return "Fail";
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search SCC codes failed.");
		}

		return "dialog:searchSccCodes";
	}

	public final UIXTable getSccCodesTable() {
		return sccCodesTable;
	}

	public final void setSccCodesTable(UIXTable sccCodesTable) {
		this.sccCodesTable = sccCodesTable;
	}

	public final void applySelectedSccCode() {
		Iterator<?> it = sccCodesTable.getSelectionState().getKeySet()
				.iterator();
		if (!it.hasNext()) {
			DisplayUtil
					.displayInfo("Please select a SCC code or cancel selection");
			return;
		}

		Object obj = it.next();
		sccCodesTable.setRowKey(obj);
		SccCode selectedSccCode = (SccCode) sccCodesTable.getRowData();
		emissionProcess.getSccCode().setSccId(selectedSccCode.getSccId());
		sccCodesTable.getSelectionState().clear();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelSelectSccCode() {
		DisplayUtil.displayInfo("Selection of new SCC code is cancelled");
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String markProfileHistory() {
		boolean operationOK = true;

		try {
			getFacilityService().markProfileHistory(fpId);
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		refreshFacility();
		selectedTreeNode = facNode;
		current = facilityId;
		if (operationOK) {
			DisplayUtil
					.displayInfo("Success: facility inventory is now marked as preserved");
		} else {
			DisplayUtil.displayError("Marking profile as preserved failed");
		}
		return getFacilityProfileOutcome();
	}

	public final String startAddAddress() {
		setEditable1(true);
		resetAddress();

		return "dialog:addressDetail";
	}

	public final void editAddress() {
		this.address = getModifyAddress(this.modifyAddressIndex);
		this.address.setNewObject(false);

		setEditable1(true);
	}

	public final void cancelEditAddress() {
		setEditable1(false);
		if (addAddressIndex == this.modifyAddressIndex) {
			try {
				this.closeAddrDialog();
				return;
			} catch (RemoteException e) {
				DisplayUtil.displayError("Could not cancel edit address.");
				logger.error("Could not cancel edit address. Exception: "
						+ e.getMessage());
			}
		}
		this.address = getModifyAddress(this.modifyAddressIndex);
	}

	private final Address getModifyAddress(int index) {
		Address addr = new Address(
				(Address) this.addrHistWrapper.getRowData(index));

		return addr;
	}

	public final String startEditAddress() {
		int index = this.addrHistWrapper.getRowIndex();
		this.modifyAddressIndex = index;
		this.address = getModifyAddress(index);
		this.address.setNewObject(false);
		this.oldAddress = getModifyAddress(index);
		setEditable1(false);

		return "dialog:addressDetail";
	}

	public final void resetAddress() {
		this.addressModify = false;
		this.address = new Address();
		this.address.setCountryCd("US");
		this.address.setState("WY");
		this.address.setBeginDate(new Timestamp(System.currentTimeMillis()));
		this.modifyAddressIndex = addAddressIndex;
	}

	public String saveAddresses() {
		boolean operationOK = true;

//		address.validateAddress();
//
//		address.getValidationMessages().remove("zipCode");
//		address.getValidationMessages().remove("cityName");
//		address.getValidationMessages().remove("addressLine1");
//
//		address.validZipCode();
//		address.validateLocationInfo();

		address.validateFacilityAddress();
		
		ValidationMessage[] errs = address.validate();

		for (ValidationMessage error : errs) {
			DisplayUtil.displayError(error.getMessage());
			operationOK = false;
		}
		if (!operationOK) {
			return null;
		}

		try {

			if (facility.isCopyOnChange()) {
				try {
					copyOnChangeFacilityProfile();
					updateModifyAddress();
				} catch (Exception ex) {
					return null;
				}
			}

			DisplayUtil.clearQueuedMessages();

			getFacilityService().updateFacilityAddresses(fpId, address);
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		refreshFacility();

		if (operationOK) {
			DisplayUtil
					.displayInfo("Facility location data successfully updated");
			closeDialog();
			setEditable1(false);
		} else {
			DisplayUtil.displayError("Updating facility location data failed");
		}

		return "facilityAddresses";
	}

	private void updateModifyAddress() {
		if (address.isNewObject())
			return;

		for (Address temp : this.facility.getAddresses()) {
			if (temp.getLatitude().equals(oldAddress.getLatitude())
					&& temp.getLongitude().equals(oldAddress.getLongitude())
					&& ((temp.getEndDate() == null && oldAddress.getEndDate() == null) || temp
							.getEndDate().equals(oldAddress.getEndDate()))) {
				this.address.setAddressId(temp.getAddressId());
				this.address.setLastModified(temp.getLastModified());
			}
		}

	}

	public boolean isAddressChanging() {
		return addressChanging;
	}

	public final void performOperation() {
		if (confirmOperation.equals("createProfileHistory")) {
			markProfileHistory();
		}

		confirmOperation = "";
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelOperation() {
		confirmOperation = "";
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String getConfirmMessage() {
		if (confirmOperation.equals("createProfileHistory")) {
			confirmMessage = "This action marks this version of the facility inventory as 'preserved'. "
					+ "A copy of the facility inventory as it exists now will be copied into the Facility Inventory History table. "
					+ "CAUTION:  Do NOT use this function when saving this facility inventory in history is not essential. "
					+ "Overuse of this function will take up space in the database and could cause the system to slow down over time. "
					+ "Click 'Yes' to continue or 'No' to cancel";
		}
		return confirmMessage;
	}

	public final TableSorter getFacilityContactsWrapper() {
		return facilityContactsWrapper;
	}

	public final TableSorter getEmusWrapper() {
		return emusWrapper;
	}

	private void setEpCeEpaEmuIds() {
		EmissionUnit[] emissionUnits = facility.getEmissionUnits().toArray(
				new EmissionUnit[0]);

		for (EmissionUnit tempEU : emissionUnits) {
			EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses()
					.toArray(new EmissionProcess[0]);
			for (EmissionProcess tempEP : emissionProcesses) {
				ControlEquipment[] controlEquips = tempEP
						.getControlEquipments()
						.toArray(new ControlEquipment[0]);
				EgressPoint[] egressPoints = tempEP.getEgressPoints().toArray(
						new EgressPoint[0]);

				for (ControlEquipment tempCE : controlEquips) {
					addEpaEmuIdToContEquip(tempCE, tempEU.getEpaEmuId());
				}

				for (EgressPoint tempEGP : egressPoints) {
					tempEGP.setAssociatedEpaEuIds(tempEU.getEpaEmuId());
				}
			}
		}
	}

	private void addEpaEmuIdToContEquip(ControlEquipment tempCE, String epaEuId) {
		tempCE.setAssociatedEpaEuIds(epaEuId);

		ControlEquipment[] ceControlEquips = tempCE.getControlEquips().toArray(
				new ControlEquipment[0]);
		EgressPoint[] ceEgressPoints = tempCE.getEgressPoints().toArray(
				new EgressPoint[0]);

		for (ControlEquipment tempCeCE : ceControlEquips) {
			addEpaEmuIdToContEquip(tempCeCE, epaEuId);
		}

		for (EgressPoint tempEGP : ceEgressPoints) {
			tempEGP.setAssociatedEpaEuIds(epaEuId);
		}
	}

	public final TableSorter getCesWrapper() {
		setEpCeEpaEmuIds();
		return cesWrapper;
	}

	public final TableSorter getEgpsWrapper() {
		setEpCeEpaEmuIds();
		return egpsWrapper;
	}

	public final TableSorter getNotesWrapper() {
		return notesWrapper;
	}

	public final TableSorter getNotesFacWrapper() {
		return notesFacWrapper;
	}

	public final TableSorter getFacRolesWrapper() {
		return facRolesWrapper;
	}

	public final TableSorter getMactSubpartsWrapper() {
		return mactSubpartsWrapper;
	}

	public final void setMactSubpartsWrapper(TableSorter mactSubpartsWrapper) {
		this.mactSubpartsWrapper = mactSubpartsWrapper;
	}

	public final TableSorter getNeshapsSubpartsWrapper() {
		return neshapsSubpartsWrapper;
	}

	public final void setNeshapsSubpartsWrapper(
			TableSorter neshapsSubpartsWrapper) {
		this.neshapsSubpartsWrapper = neshapsSubpartsWrapper;
	}

	public final TableSorter getNspsSubpartsWrapper() {
		return nspsSubpartsWrapper;
	}

	public final void setNspsSubpartsWrapper(TableSorter nspsSubpartsWrapper) {
		this.nspsSubpartsWrapper = nspsSubpartsWrapper;
	}

	public final TableSorter getSicCdsWrapper() {
		return sicCdsWrapper;
	}

	public final void setSicCdsWrapper(TableSorter sicCdsWrapper) {
		this.sicCdsWrapper = sicCdsWrapper;
	}

	public final TableSorter getNaicsCdsWrapper() {
		return naicsCdsWrapper;
	}

	public final void setNaicsCdsWrapper(TableSorter naicsCdsWrapper) {
		this.naicsCdsWrapper = naicsCdsWrapper;
	}

	public final TableSorter getEuEmissionsWrapper() {
		return euEmissionsWrapper;
	}

	public final void setEuEmissionsWrapper(TableSorter euEmissionsWrapper) {
		this.euEmissionsWrapper = euEmissionsWrapper;
	}

	public final TableSorter getPollutantsContWrapper() {
		return pollutantsContWrapper;
	}

	public final void setPollutantsContWrapper(TableSorter pollutantsContWrapper) {
		this.pollutantsContWrapper = pollutantsContWrapper;
	}

	public final TableSorter getFacEmissionsWrapper() {
		return facEmissionsWrapper;
	}

	public final void setFacEmissionsWrapper(TableSorter facEmissionsWrapper) {
		this.facEmissionsWrapper = facEmissionsWrapper;
	}
	
	public TableSorter getContinuousMonitorsWrapper() {
		return continuousMonitorsWrapper;
	}

	public void setContinuousMonitorsWrapper(TableSorter continuousMonitorsWrapper) {
		this.continuousMonitorsWrapper = continuousMonitorsWrapper;
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
			return facilityId + ":" + FAC_REFERENCE + ":"
					+ FacesUtil.getCurrentPage();
		}

		Object facObject = (((Stars2TreeNode) selectedTreeNode).getUserObject());
		String ret;
		if (selectedTreeNode.getType().equals(EU_NODE)) {
			ret = EU_REFERENCE_PREFIX
					+ ((EmissionUnit) facObject).getEpaEmuId();
		} else if (selectedTreeNode.getType().equals(EP_NODE)) {
			ret = EP_REFERENCE_PREFIX
					+ ((EmissionProcess) facObject).getProcessId();
		} else if (selectedTreeNode.getType().equals(CE_NODE)) {
			ret = CE_REFERENCE_PREFIX
					+ ((ControlEquipment) facObject).getControlEquipmentId();
		} else if (selectedTreeNode.getType().equals(EGP_NODE)) {
			ret = EGP_REFERENCE_PREFIX
					+ ((EgressPoint) facObject).getReleasePointId();
		} else if (selectedTreeNode.getType().equals(UACE_NODE)) {
			ret = UACE_REFERENCE;
		} else if (selectedTreeNode.getType().equals(UAEGP_NODE)) {
			ret = UAEGP_REFERENCE;
		} else {
			ret = FAC_REFERENCE;
		}

		ret = facilityId + ":" + ret + ":" + FacesUtil.getCurrentPage();

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.webcommon.ValidationBase#validationDlgAction()
	 */
	public String validationDlgAction() {
		String rtn = super.validationDlgAction();
		if (null != rtn)
			return rtn;
		if (newValidationDlgReference == null
				|| newValidationDlgReference
						.equals(getValidationDlgReference())) {
			return null; // stay on same page
		}

		StringTokenizer st = new StringTokenizer(newValidationDlgReference, ":");

		boolean disableEUEdit = 
				newValidationDlgReference.contains(EU_NOPROCESSES_PREFIX);

		boolean disableContactEdit = newValidationDlgReference
				.contains(CONTACTS_REFERENCE);
		
		boolean disableEPEdit =	
				newValidationDlgReference.contains(EP_NOSTACK_PREFIX);
		
		boolean disableFPEdit = newValidationDlgReference.contains(FAC_NO_NAICS) || newValidationDlgReference.contains(FAC_NO_API);

		String subsystem = st.nextToken();
		if (!subsystem.equals(ValidationBase.FACILITY_TAG)) {
			logger.error("Facility reference is in error: "
					+ newValidationDlgReference);
			DisplayUtil.displayError("Error processing validation message");
			return null;
		}

		String valFacId = st.nextToken();
		if (!valFacId.equals(facilityId)) {
			DisplayUtil.displayError("Validation message is  for  facility: "
					+ valFacId);
			return null;
		}

		getFacility();

		HashMap<String, String> epToEmu = new HashMap<String, String>(0);
		setEusInSubComps(epToEmu);

		String refType = st.nextToken();

		if (FAC_REFERENCE.startsWith(refType)) {
			byClickEpaEmuId = null;
			selEpaEmuIds = null;
			expandTree = false;
			expandOpt = 0;
			getEuTreeData(selEpaEmuIds, expandOpt);
			selectedTreeNode = treeData.getNodeById("0"); // root
			current = facilityId;
		} else if (UACE_REFERENCE.startsWith(refType)) {
			selectedTreeNode = UnAssignCEsNode;
			current = UACE_NODE;
		} else if (UAEGP_REFERENCE.startsWith(refType)) {
			selectedTreeNode = UnAssignEgpsNode;
			current = UAEGP_NODE;
		} else if (RULES_REFERENCE.startsWith(refType)) {
			// return RULES_OUTCOME;
		} else if (!CONTACTS_REFERENCE.startsWith(refType)) {
			String nodeID = st.nextToken();

			if (EU_REFERENCE_PREFIX.startsWith(refType)
					|| EU_NOPROCESSES_PREFIX.startsWith(refType)) {
				byClickEpaEmuId = nodeID;
				selEpaEmuIds = new ArrayList<String>(0);
				selEpaEmuIds.add(byClickEpaEmuId);
				expandOpt = 0;
				expandTree = false;
				getEuTreeData(selEpaEmuIds, expandOpt);
				nodeID = euFacNodeMapId(nodeID);
				if (!disableEUEdit) {
					operation[EM_UNIT_COMP] = EDIT_OP;
					saveObjId = byClickEpaEmuId;
				}
			} else if (EP_REFERENCE_PREFIX.startsWith(refType) || 
					EP_NOSTACK_PREFIX.startsWith(refType)) {
				byClickEpaEmuId = null;
				selEpaEmuIds = new ArrayList<String>(0);
				selEpaEmuIds.add(epToEmu.get(nodeID));
				expandOpt = 2;
				expandTree = false;
				getEuTreeData(selEpaEmuIds, expandOpt);
				nodeID = epFacNodeMapId(nodeID);
				if (!disableEPEdit) {
					operation[EM_PROC_COMP] = EDIT_OP;
				}
			} else if (CE_REFERENCE_PREFIX.startsWith(refType)) {
				byClickEpaEmuId = null;
				ControlEquipment ce = facility.getControlEquipment(nodeID);
				if (ce != null && ce.getAssociatedEpaEuIds() != null) {
					StringTokenizer tokenizer = new StringTokenizer(
							ce.getAssociatedEpaEuIds(), ",");
					selEpaEmuIds = new ArrayList<String>(0);
					String nextToken;

					while (tokenizer.hasMoreTokens()) {
						nextToken = tokenizer.nextToken();
						selEpaEmuIds.add(nextToken.trim());
					}
					expandOpt = 2;
				} else {
					selEpaEmuIds = null;
					expandOpt = 0;
				}
				expandTree = false;
				getEuTreeData(selEpaEmuIds, expandOpt);
				nodeID = ceFacNodeMapId(nodeID);
				operation[CNT_EQUIP_COMP] = EDIT_OP;

				if (!(disableEUEdit || disableContactEdit)) {
					saveObjId = ce.getControlEquipmentId();
					setEditable(true);
				}
			} else if (EGP_REFERENCE_PREFIX.startsWith(refType)
					|| RP_REFERENCE_PREFIX.startsWith(refType)) {
				byClickEpaEmuId = null;
				EgressPoint egp = facility.getEgressPoint(nodeID);
				if (egp != null && egp.getAssociatedEpaEuIds() != null) {
					StringTokenizer tokenizer = new StringTokenizer(
							egp.getAssociatedEpaEuIds(), ",");
					selEpaEmuIds = new ArrayList<String>(0);
					String nextToken;

					while (tokenizer.hasMoreTokens()) {
						nextToken = tokenizer.nextToken();
						selEpaEmuIds.add(nextToken.trim());
					}
					expandOpt = 2;
				} else {
					selEpaEmuIds = null;
					expandOpt = 0;
				}
				expandTree = false;
				getEuTreeData(selEpaEmuIds, expandOpt);
				nodeID = egFacNodeMapId(nodeID);
				operation[EG_POINT_COMP] = EDIT_OP;
				saveObjId = egp.getReleasePointId();
			} else {
				DisplayUtil.displayError("bad node type selected in popup");
				return null; // stay on same page
			}

			if (facNodeMap.get(nodeID) == null) {
				DisplayUtil.displayError("Cannot find node selected in popup");
				return null; // stay on same page
			}

			selectedTreeNode = facNodeMap.get(nodeID);
			current = nodeID;
		} else {
			if (this.activeContacts) {
				getActiveFacilityContacts();
			} else {
				getFacilityContacts();
			}
		}

		nodeClicked();
		if (disableEUEdit || disableContactEdit || disableEPEdit || disableFPEdit) {
			setEditable(false);
		} else {
			setEditable(true);
		}
		// return outCome for navigation to right page
		return st.nextToken();
	}

	public final void setEusInSubComps(HashMap<String, String> epToEmu) {
		String epaEmuId;
		EmissionUnit[] emissionUnits = facility.getEmissionUnits().toArray(
				new EmissionUnit[0]);

		for (EmissionUnit tempEU : emissionUnits) {
			if (EuOperatingStatusDef.IV.equals(tempEU.getOperatingStatusCd())) {
				continue;
			}

			epaEmuId = tempEU.getEpaEmuId();
			EmissionProcess[] emissionProcesses = tempEU.getEmissionProcesses()
					.toArray(new EmissionProcess[0]);

			for (EmissionProcess tempEP : emissionProcesses) {
				epToEmu.put(tempEP.getProcessId(), epaEmuId);
				ControlEquipment[] controlEquips = tempEP
						.getControlEquipments()
						.toArray(new ControlEquipment[0]);
				EgressPoint[] egressPoints = tempEP.getEgressPoints().toArray(
						new EgressPoint[0]);

				for (ControlEquipment tempCE : controlEquips) {
					setEuInContEquip(tempCE, epaEmuId);
				}

				for (EgressPoint tempEGP : egressPoints) {
					tempEGP.setAssociatedEpaEuIds(epaEmuId);
				}
			}
		}
	}

	protected final void setEuInContEquip(ControlEquipment tempCE,
			String epaEuId) {

		ControlEquipment[] ceControlEquips = tempCE.getControlEquips().toArray(
				new ControlEquipment[0]);
		EgressPoint[] ceEgressPoints = tempCE.getEgressPoints().toArray(
				new EgressPoint[0]);

		tempCE.setAssociatedEpaEuIds(epaEuId);

		for (ControlEquipment tempCeCE : ceControlEquips) {
			setEuInContEquip(tempCeCE, epaEuId);
		}

		for (EgressPoint tempEGP : ceEgressPoints) {
			tempEGP.setAssociatedEpaEuIds(epaEuId);
		}
	}

	public final boolean getEditMode() {
		return editable;
	}

	public final void setEditMode(boolean editMode) {
		editable = false;
	}

	public final String getFacilityProfileBean() {
		return facilityProfileBean;
	}

	public final void setFacilityProfileBean(String facilityProfileBean) {
		this.facilityProfileBean = facilityProfileBean;
	}

	public final String getFacilityProfileOutcome() {
		
		String ret = FacilityProfileBase.FAC_OUTCOME;

		if (isPortalApp() && !editStaging) {
			ret = HOME_FAC_OUTCOME;
		} else if (isPublicApp()) {
			ret = HOME_FAC_OUTCOME;
		}

		return ret;
	}

	public final boolean isDemReadOnlyAttr() {
		if (isInternalApp()) {
			return !editable;
		}

		return true;
	}

	public final boolean isNotAdminReadOnlyAttr() {
		if (isInternalApp()) {
			return !editable
					|| (!InfrastructureDefs.getCurrentUserAttrs()
							.isStars2Admin() && !facility.getPortable());
		}

		return true;
	}

	public final boolean isDapcReadOnlyAttr() {
		if (isInternalApp()) {
			return true;
		}

		return !editable;
	}

	public final boolean isDapcReadOnlyAdminAttr() {
		if (isInternalApp() && editable) {
			return !(InfrastructureDefs.getCurrentUserAttrs().isStars2Admin());
		}

		return !editable;
	}

	public final boolean isDapcUser() {
		return isInternalApp();
	}

	public final boolean isDisplayUncertainArea() {
		return InfrastructureDefs.getCurrentUserAttrs()
				.isDisplayUncertainArea();
	}

	public final boolean isDapcUpdateRenderComp() {
		if (isInternalApp()) {
			return editable;
		}

		return false;
	}

	public final boolean isCorePlaceIdReadOnlyAttr() {
		if (isInternalApp()) {
			if (facility.getCorePlaceId() == null
					|| InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
				return !editable;
			}
		}

		return true;
	}

	public final DefSelectItems getFacOperatingStatusDefs() {
		FacilityReference facRef = (FacilityReference) FacesUtil
				.getManagedBean("facilityReference");
		if (isInternalApp()) {
			return facRef.getOperatingStatusDefs();
		}
		if (OperatingStatusDef.SD.equals(facility.getOperatingStatusCd())) {
			return facRef.getOperatingStatusDefs();
		}

		return facRef.getDemOperatingStatusDefs();
	}

	public final DefSelectItems getEuOperatingStatusDefs() {
		FacilityReference facRef = (FacilityReference) FacesUtil
				.getManagedBean("facilityReference");

		return facRef.getEuOperatingStatusDefs();

	}

	public final boolean isStaging() {
		return staging;
	}

	public final void setStaging(boolean staging) {
		this.staging = staging;
	}

	protected void finalize() throws Throwable {

	}

	public List<Contact> getAllContacts() {
		return allContacts;
	}

	public void setAllContacts(List<Contact> allContacts) {
		this.allContacts = allContacts;
	}

	public void initFacilityProfile(Integer fpId, boolean staging) {
		facility = null;
		expandOpt = 0;
		selEpaEmuIds = null;
		byClickEpaEmuId = null;
		facProfDocuments = null;
		this.fpId = fpId;
		this.staging = staging;
	}

	public List<Document> getFacProfDocuments() {
		try {
			facProfDocuments = getFacilityService().getPrintableDocumentList(
					facility);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Unable to generate facility detail report");
		}

		return facProfDocuments;
	}

	public String getPopupRedirect() {
		if (popupRedirect != null) {
			FacesUtil.setOutcome(null, popupRedirect);
			popupRedirect = null;
		}
		return null;
	}

	public final EmissionUnit getEmissionUnit1() {
		return emissionUnit1;
	}

	public final void setEmissionUnit1(EmissionUnit emissionUnit1) {
		this.emissionUnit1 = emissionUnit1;
	}

	public final String getOldFacOperStatus() {
		return oldFacOperStatus;
	}

	public final void setOldFacOperStatus(String oldFacOperStatus) {
		this.oldFacOperStatus = oldFacOperStatus;
	}

	public final short[] getOperation() {
		return operation;
	}

	public final void setOperation(short[] operation) {
		this.operation = operation;
	}

	public final String getByClickEpaEmuId() {
		return byClickEpaEmuId;
	}

	public final void setByClickEpaEmuId(String byClickEpaEmuId) {
		this.byClickEpaEmuId = byClickEpaEmuId;
	}

	public final String getSaveObjId() {
		return saveObjId;
	}

	public final void setSaveObjId(String saveObjId) {
		this.saveObjId = saveObjId;
	}

	public final Stars2TreeNode getFacNode() {
		return facNode;
	}

	public final void setFacNode(Stars2TreeNode facNode) {
		this.facNode = facNode;
	}

	public final Stars2TreeNode getUnAssignCEsNode() {
		return UnAssignCEsNode;
	}

	public final void setUnAssignCEsNode(Stars2TreeNode unAssignCEsNode) {
		UnAssignCEsNode = unAssignCEsNode;
	}

	public final Stars2TreeNode getUnAssignEgpsNode() {
		return UnAssignEgpsNode;
	}

	public final void setUnAssignEgpsNode(Stars2TreeNode unAssignEgpsNode) {
		UnAssignEgpsNode = unAssignEgpsNode;
	}

	public final HashMap<String, Stars2TreeNode> getFacNodeMap() {
		return facNodeMap;
	}

	public final void setFacNodeMap(HashMap<String, Stars2TreeNode> facNodeMap) {
		this.facNodeMap = facNodeMap;
	}

	public final FacilityNote[] getFacilityNotes() {
		return facilityNotes;
	}

	public final void setFacilityNotes(FacilityNote[] facilityNotes) {
		this.facilityNotes = facilityNotes;
	}

	public final Address getAddress() {
		return this.address;
	}

	public final void setAddress(Address address) {
		this.address = address;
	}

	public final ArrayList<ContactType> getContactTypes() {
		return contactTypes;
	}

	public final void setContactTypes(ArrayList<ContactType> contactTypes) {
		this.contactTypes = contactTypes;
	}

	public final ContactType getSaveContactType() {
		return saveContactType;
	}

	public final void setSaveContactType(ContactType saveContactType) {
		this.saveContactType = saveContactType;
	}

	public final boolean isContactModify() {
		return contactModify;
	}

	public final void setContactModify(boolean contactModify) {
		this.contactModify = contactModify;
	}

	public final String getConfirmOperation() {
		return confirmOperation;
	}

	public final void setConfirmOperation(String confirmOperation) {
		this.confirmOperation = confirmOperation;
	}

	public final List<Stars2Object> getSicCds() {
		return sicCds;
	}

	public final void setSicCds(List<Stars2Object> sicCds) {
		this.sicCds = sicCds;
	}

	public final List<Stars2Object> getNaicsCds() {
		return naicsCds;
	}

	public final void setNaicsCds(List<Stars2Object> naicsCds) {
		this.naicsCds = naicsCds;
	}

	public final InfrastructureDefs getInfraDefs() {
		return infraDefs;
	}

	public final void setInfraDefs(InfrastructureDefs infraDefs) {
		this.infraDefs = infraDefs;
	}

	public final ArrayList<String> getTreePath() {
		return treePath;
	}

	public final void setTreePath(ArrayList<String> treePath) {
		this.treePath = treePath;
	}

	public final DataDetail[] getCeDataDetails() {
		return ceDataDetails;
	}

	public final void setCeDataDetails(DataDetail[] ceDataDetails) {
		this.ceDataDetails = ceDataDetails;
	}

	public final int getRenderComp() {
		return renderComp;
	}

	public final void setRenderComp(int renderComp) {
		this.renderComp = renderComp;
	}

	public final int getCurrentUserId() {
		return currentUserId;
	}

	public final void setCurrentUserId(int currentUserId) {
		this.currentUserId = currentUserId;
	}

	public final String getEpaEmuId() {
		return epaEmuId;
	}

	public final void setNoteModify(boolean noteModify) {
		this.noteModify = noteModify;
	}

	public final void setSelectedHistory(FacilityVersion selectedHistory) {
		this.selectedHistory = selectedHistory;
	}

	public final void setFacilityContacts(List<ContactUtil> facilityContacts) {
		this.facilityContacts = facilityContacts;
	}

	public void setContactsList(List<SelectItem> contactsList) {
		this.contactsList = contactsList;
	}

	public final void setSccCodeConverter(SccCodeConverter sccCodeConverter) {
		this.sccCodeConverter = sccCodeConverter;
	}

	public final void setSccCodes(SccCode[] sccCodes) {
		this.sccCodes = sccCodes;
	}

	public final void setRemoveOpMessage(String removeOpMessage) {
		this.removeOpMessage = removeOpMessage;
	}

	public final void setConfirmMessage(String confirmMessage) {
		this.confirmMessage = confirmMessage;
	}

	public final void setCorrespondences(Correspondence[] correspondences) {
		this.correspondences = correspondences;
	}

	public final void setMactSubparts(List<Stars2Object> mactSubparts) {
		this.mactSubparts = mactSubparts;
	}

	public final void setNspsSubparts(List<Stars2Object> nspsSubparts) {
		this.nspsSubparts = nspsSubparts;
	}

	public final void setMultiEstabFacilities(
			MultiEstabFacilityList[] multiEstabFacilities) {
		this.multiEstabFacilities = multiEstabFacilities;
	}

	public final void setPopupRedirect(String popupRedirect) {
		this.popupRedirect = popupRedirect;
	}

	public final synchronized void setFacAfterValid(Integer fpId) {
		this.fpId = fpId;
		facility = null;
		treeData = null;
		facProfDocuments = null;
		getTreeData();
	}

	public String getContEquipsEpaEuIds() {
		return contEquipsEpaEuIds;
	}

	public void setContEquipsEpaEuIds(String contEquipsEpaEuIds) {
		this.contEquipsEpaEuIds = contEquipsEpaEuIds;
	}

	public String getEgrPointsEuIds() {
		return egrPointsEuIds;
	}

	public void setEgrPointsEuIds(String egrPointsEuIds) {
		this.egrPointsEuIds = egrPointsEuIds;
	}

	public List<String> getSelEpaEmuIds() {
		return selEpaEmuIds;
	}

	public void setSelEpaEmuIds(List<String> selEpaEmuIds) {
		this.selEpaEmuIds = selEpaEmuIds;
	}

	public int getExpandOpt() {
		return expandOpt;
	}

	public void setExpandOpt(int expandOpt) {
		this.expandOpt = expandOpt;
	}

	public boolean isExpandTree() {
		return expandTree;
	}

	public void setExpandTree(boolean expandTree) {
		this.expandTree = expandTree;
	}

	public final boolean isHideInvalidEUs() {
		return hideInvalidEUs;
	}

	public final void setHideInvalidEUs(boolean hideInvalidEUs) {
		this.hideInvalidEUs = hideInvalidEUs;
	}

	public final String toggleEUList() {
		if (hideInvalidEUs) {
			emusWrapper = new TableSorter();
			emusWrapper.setWrappedData(facility.getValidEmissionUnits()
					.toArray(new EmissionUnit[0]));
		} else {
			emusWrapper = new TableSorter();
			emusWrapper.setWrappedData(facility.getEmissionUnits().toArray(
					new EmissionUnit[0]));
		}
		return null;
	}

	public final String getGoogleMapsURL() {
		String url = null;
		if (facility != null && facility.getPhyAddr() != null) {
			if (facility.getPhyAddr().getAddressLine1() != null
					&& facility.getPhyAddr().getCityName() != null) {
				StringBuilder sb = new StringBuilder(
						"http://maps.google.com/maps?f=q&source=s_q&z=8&hl=en&geocode=&q=");
				sb.append(facility.getPhyAddr().getAddressLine1());
				if (facility.getPhyAddr().getAddressLine2() != null) {
					sb.append("+" + facility.getPhyAddr().getAddressLine2());
				}
				sb.append("+" + facility.getPhyAddr().getCityName());
				if (facility.getPhyAddr().getState() != null) {
					sb.append("+" + facility.getPhyAddr().getState());
				}
				if (facility.getPhyAddr().getZipCode() != null) {
					sb.append("+" + facility.getPhyAddr().getZipCode());
				}
				url = sb.toString().replaceAll("\\s", "+");
			}
		}
		return url;
	}

	public final String getEchoURL() {
		String url = null;
		if (facility != null && facility.getFederalSCSCId() != null) {
			url = "http://www.epa-echo.gov/cgi-bin/get1cReport.cgi?tool=echo&IDNumber="
					+ facility.getFederalSCSCId();
		}
		return url;
	}

	public String getBranchingAirFlowMsg() {
		return branchingAirFlowMsg;
	}

	public String getControlEquipAttribDescMsg() {
		return controlEquipAttribDescMsg;
	}

	public String getCancelLabel() {
		return cancelLabel;
	}

	public final List<Permit> getEuHistPermits() {
		euHistPermits = new ArrayList<Permit>();
		try {
			for (EmissionUnit eu : facility.getEmissionUnits()) {
				List<Permit> euPermits = getPermitService().searchAllEuPermits(
						eu.getCorrEpaEmuId());
				for (Permit permit : euPermits) {
					permit.setPermitEU(eu.getCorrEpaEmuId());
					euHistPermits.add(permit);
				}
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing EU Permits failed.");
		}
		return euHistPermits;
	}

	public void discloseListen(DisclosureEvent disclosureEvent) {
		if (disclosureEvent.isExpanded()) {
			discloseEIS = true;
		} else
			discloseEIS = false;
	}

	public boolean isDiscloseEIS() {
		return discloseEIS;
	}

	public final List<Stars2Object> getNeshapsPollutants() {
		if (neshapsPollutants == null) {
			neshapsPollutants = new ArrayList<Stars2Object>();
		}
		return neshapsPollutants;
	}

	public final void addNeshapsPollutant() {
		neshapsPollutants.add(new Stars2Object(""));
	}

	public final String getInfo() {
		String rtn = "facility is null, facilityId=" + facilityId;
		if (facility != null) {
			rtn = "facilityId=" + facility.getFacilityId() + ", corePlaceId="
					+ facility.getCorePlaceId();
		}
		return rtn;
	}

	public final void checkAndImportContactsToStaging(Contact contact) {

		try {
			Contact existingContact = getContactService().retrieveContactDetail(contact.getContactId());

			if (existingContact != null) {
				contact.setAddress(existingContact.getAddress());
				contact.getAddress().setAddressId(null);
				getFacilityService().createStagingFacilityContactsWithOutAddType(contact);
			}
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Importing contacts failed.");
		}

	}

	public TableSorter getFacOwnerContactsWrapper() {
		return facOwnerContactsWrapper;
	}

	public void setFacOwnerContactsWrapper(TableSorter facOwnerContactsWrapper) {
		this.facOwnerContactsWrapper = facOwnerContactsWrapper;
	}

	public TableSorter getMactCompWrapper() {
		return mactCompWrapper;
	}

	public void setMactCompWrapper(TableSorter mactCompWrapper) {
		this.mactCompWrapper = mactCompWrapper;
	}

	public String getTvAirProgramDesc() {
		return AirProgramDef.printableValue(AirProgramDef.TITLE_V);
	}

	public String getSmAirProgramDesc() {
		return AirProgramDef.printableValue(AirProgramDef.SM_FESOP);
	}

	public boolean isSipAirProgram() {
		return true;
	}

	public final List<PollutantCompCode> getAirPrograms() {
		List<PollutantCompCode> airPrograms = new ArrayList<PollutantCompCode>();
		PollutantCompCode cs;
		cs = new PollutantCompCode();
		cs.setPollutantCd(AirProgramsDef.SIP);
		cs.setPollutantCompCd(facility.getSipCompCd());
		airPrograms.add(cs);

		if (facility.getAirProgramCd() != null) {
			cs = new PollutantCompCode();
			cs.setPollutantCd(facility.getAirProgramCd());
			cs.setPollutantCompCd(facility.getAirProgramCompCd());
			airPrograms.add(cs);
		}

		return airPrograms;
	}

	public String getFceScheduled() {
		return fceScheduled;
	}

	public Timestamp getLastFCEDate() {
		return lastFCEDate;
	}

	public String getLastSiteVisitDate() {
		return lastSiteVisitDate;
	}

	public Timestamp getLastStackTestDate() {
		return lastStackTestDate;
	}

	public final String displayEUTypeHelpInfo() {
		return "dialog:emissionUnitHelpInfo";
	}

	public final String displayEUTypeChangeLog() {
		FieldAuditLogSearch fals = (FieldAuditLogSearch) FacesUtil
				.getManagedBean("fieldAuditLogSearch");

		FieldAuditLog fal = new FieldAuditLog();
		fal.setFacilityId(facilityId);
		fal.setCorrEmuId(emissionUnit.getCorrEpaEmuId());

		fals.setCategoryCd("eu");
		fals.setSearchFieldAuditLog(fal);

		fals.submitSearch();

		return "tools.fieldAuditLog";
	}
	
	public final String displayModelingData() {
		ModelingExtract fals = (ModelingExtract) FacesUtil
				.getManagedBean("modelingData");
		fals.reset();
		fals.setLatitudeDegrees((double)facility.getLatitudeDeg());
		fals.setLongitudeDegrees((double)facility.getLongitude());
	 
        return "tools.modelingData";
	}

	public boolean isPotentialEmissionsEditable() {
		boolean ret = false;
		if (isDapcUser() && this.editable) {
			ret = true;
		}
		return ret;
	}

	public List<Contact> getAllFacilityContacts() {
		return allFacilityContacts;
	}

	public void setAllFacilityContacts(List<Contact> allFacilityContacts) {
		this.allFacilityContacts = allFacilityContacts;
	}

	public final String startToAddPTE() {
		this.editable1 = true;
		this.selectedEuEmission = new EuEmission();
		this.newEuEmission = true;

		return "dialog:pteDetail";
	}

	public final String startToEditPTE() {
		this.editable1 = false;
		int index = this.euEmissionsWrapper.getRowIndex();
		this.newEuEmission = false;
		UIXTable table = euEmissionsWrapper.getTable();
		this.selectedEuEmission = (EuEmission) table.getRowData(index);

		return "dialog:pteDetail";
	}

	public final void savePermittedEmissions(ActionEvent ae) {
		List<EuEmission> euPermittedEmissions = new ArrayList<EuEmission>();
		for (EuEmission euPermittedEmission : this.emissionUnit
				.getEuEmissions()) {
			EuEmission copyEuPermittedEmission = new EuEmission(
					euPermittedEmission);
			euPermittedEmissions.add(copyEuPermittedEmission);
		}
		setOldEUPermittedEmissions(euPermittedEmissions);
	}

	public final void editPTE() {
		this.editable1 = true;
	}

	public final String savePTE() {
		boolean operationOK = true;

		if (this.newEuEmission) {
			emissionUnit.addEuEmission(this.selectedEuEmission);
			this.newEuEmission = false;
		}

		if (operationOK) {
			if (!validatePTE()) {
				operationOK = false;
			}
		}

		// EU is new or not
		if (!Utility.isNullOrZero(emissionUnit.getEmuId()) && operationOK) {
			if (facility.isCopyOnChange()) {
				try {
					copyOnChangeFacilityProfile();
					copyOnChangeEmissionUnit();
				} catch (Exception ex) {
					return null;
				}
			}

			try {
				getFacilityService().saveEmissionUnitPTE(emissionUnit);

				this.facility.setValidated(false);
			} catch (RemoteException re) {
				operationOK = false;
				handleException(re);
				DisplayUtil
						.displayError("Save emission unit replacement data failed");
			}
			this.refreshEuEmissions();
		}

		// update fpId if it is changed due to copy on change flag
		fpId = emissionUnit.getFpId();
		
		if (operationOK) {
			if (Utility.isNullOrZero(emissionUnit.getEmuId())) {
				DisplayUtil.displayInfo("Potential emissions added");
			} else {
				DisplayUtil.displayInfo("Potential emissions saved");
			}
			FacesUtil.returnFromDialogAndRefresh();
		}

		return null;

	}

	public final String deletePTE() {
		emissionUnit.removeEuEmission(this.selectedEuEmission);

		return savePTE();
	}

	public final String cancelPTE() {
		this.editable1 = false;

		if (this.emissionUnit != null && emissionUnit.getEmuId() == null) {
			this.emissionUnit.setEuEmissions(getOldEUPermittedEmissions());
			euEmissionsWrapper.clearWrappedData();
			euEmissionsWrapper.setWrappedData(emissionUnit.getEuEmissions());
		}

		refreshEuEmissions();

		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	private void refreshEuEmissions() {
		if (emissionUnit != null && emissionUnit.getEmuId() != null) {
			try {
				euEmissionsWrapper.clearWrappedData();
				getFacilityService().retrieveEuEmissions(emissionUnit);
				euEmissionsWrapper
						.setWrappedData(emissionUnit.getEuEmissions());
				facility.generateFacilityEmissions();
				facEmissionsWrapper.clearWrappedData();
				facEmissionsWrapper.setWrappedData(facility
						.getFacilityEmissions());
			} catch (DAOException e) {
				DisplayUtil.displayError("Could not refresh Emission Unit");
				logger.error("Could not retrieve emission unit. Exception: "
						+ e.getMessage());
			}
		}
	}

	public final String closePTEDialog() {
		// byClickEpaEmuId = emissionUnit.getEpaEmuId();
		//
		// selectedTreeNode = facNodeMap.get(euFacNodeMapId(byClickEpaEmuId));
		// current = euFacNodeMapId(byClickEpaEmuId);
		// nodeClicked();
		//
		// selEpaEmuIds = new ArrayList<String>(0);
		// selEpaEmuIds.add(byClickEpaEmuId);
		// expandOpt = 1;
		// expandTree = false;
		// getEuTreeData(selEpaEmuIds, expandOpt);
		// nodeClicked();

		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	private boolean validatePTE() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;

		try {
			validationMessages = getFacilityService().validateEuEmissions(
					emissionUnit);

			String pageViewId = FAC_CLIENT_ID + "emissionUnit:";
			if (displayValidationMessages(pageViewId, validationMessages)) {
				isValid = false;
			}

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Accessing Emission Unit failed for validation");
			isValid = false;
		}

		return isValid;
	}

	public EuEmission getSelectedEuEmission() {
		return selectedEuEmission;
	}

	public void setSelectedEuEmission(EuEmission selectedEuEmission) {
		this.selectedEuEmission = selectedEuEmission;
	}

	public String getValidTradeSecretNotifFrom() {
		return validTradeSecretNotifFrom;
	}

	public void setValidTradeSecretNotifFrom(String validTradeSecretNotifFrom) {
		this.validTradeSecretNotifFrom = validTradeSecretNotifFrom;
	}

	public String returnFromValidTradeSecretNotif() {
		return validTradeSecretNotifFrom;
	}

	public String getValidTradeSecretNotifMsg() {
		String rtnMsg = SystemPropertyDef.getSystemPropertyValue("ValidationTradeSecretNotification", null);
		if (rtnMsg == null) {
			rtnMsg = "";
		}
		return rtnMsg;
	}

	public String confirmResetFacRoles() {
		return "dialog:confirmResetFacilityRoles";
	}

	public void resetFacilityRoles(ActionEvent actionEvent) {
		try {
			getFacilityService().updateFacilityRoles(facility);
			refreshFacility();
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

		// popupRedirectOutcome = "facilityProfile";
		DisplayUtil.displayInfo("Facility roles updated successfully");
		// return "facilities.profile.facilityRoles";
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void noConfirm(ActionEvent actionEvent) {
		// popupRedirectOutcome = null;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final String startToAddEngineReplacement() {
		setEditable1(true);
		emissionUnitReplacement = new EmissionUnitReplacement();
		emissionUnitReplacement.setEmuId(emissionUnit.getEmuId());
		setEngineSerialTrackingEditMode(false);

		if (emissionUnit.getEmissionUnitReplacements().isEmpty()) {
			setNoEngineSerialNumbers(true);
		} else {
			if (emissionUnit.getCurrentEngineSerialNumbers().isEmpty()) {
				setNoEngineSerialNumbers(true);
			} else {
				this.currentSerialNumbers = new ArrayList<EmissionUnitReplacement>();
				copyReplacementData(
						emissionUnit.getCurrentEngineSerialNumbers(),
						this.currentSerialNumbers);
				setCurrentEngineSerialNumber(emissionUnit
						.getCurrentEngineSerialNumbers().get(0));
				setNoEngineSerialNumbers(false);
			}
		}

		if (isNoEngineSerialNumbers()) {
			setSerialNumberText("New Engine");
		} else {
			setSerialNumberText("New Like-kind Engine");
		}

		return "dialog:engineReplacementDetail";

	}

	public final String startToEditEngineReplacement() {
		this.emissionUnitReplacement.setNewObject(false);
		setEditable1(false);
		setEngineSerialTrackingEditMode(true);

		if (!emissionUnit.getCurrentEngineSerialNumbers().isEmpty()) {
			setCurrentEngineSerialNumber(emissionUnit
					.getCurrentEngineSerialNumbers().get(0));
		}
		setEngineSerialNumberStatusEditable(!this.emissionUnitReplacement
				.isSerialNumberCurrent());
		
		if (isPublicApp()) {
			setSerialNumberText("View Engine");
		} else {
			setSerialNumberText("Edit Engine");
		}
		return "dialog:engineReplacementDetail";
	}

	public final void editEngineReplacement() {
		setEditable1(true);
	}

	public final void cancelEditEngineReplacement() {
		setEditable1(false);
		if (this.emissionUnit != null && emissionUnit.getEmuId() == null) {
			this.emissionUnit
					.setEmissionUnitReplacements(getOldSerialNumbers());
		}

		refreshEmissionUnitReplacements();
		this.closeDialog();
		return;
	}

	private void copyReplacementData(List<EmissionUnitReplacement> source,
			List<EmissionUnitReplacement> target) {
		for (EmissionUnitReplacement serialNumber : source) {
			EmissionUnitReplacement copySerialNumber = new EmissionUnitReplacement(
					serialNumber);
			copySerialNumber.setReplacementId(serialNumber.getReplacementId());
			target.add(copySerialNumber);
		}
	}

	public final void saveEngine(ActionEvent ae) {
		List<EmissionUnitReplacement> engineSerialNumbers = new ArrayList<EmissionUnitReplacement>();
		for (EmissionUnitReplacement engineSerialNumber : this.emissionUnit
				.getEmissionUnitReplacements()) {
			EmissionUnitReplacement copyEngineSerialNumber = new EmissionUnitReplacement(
					engineSerialNumber);
			copyEngineSerialNumber.setReplacementId(engineSerialNumber
					.getReplacementId());
			engineSerialNumbers.add(copyEngineSerialNumber);
		}
		setOldSerialNumbers(engineSerialNumbers);
	}

	public final void saveEngineReplacement() {

		if (!validateEngineReplacement()) {
			return;
		}

		if (this.emissionUnitReplacement.isNewObject()) {
			emissionUnit
					.addEmissionUnitReplacement(this.emissionUnitReplacement);
			this.emissionUnitReplacement.setNewObject(false);
		}

		saveEngineSerialNumber();

		closeDialog();
	}

	private final void saveEngineSerialNumber() {
		// EU is new or not
		if (!Utility.isNullOrZero(emissionUnit.getEmuId())) {
			if (facility.isCopyOnChange()) {
				try {
					copyOnChangeFacilityProfile();
					copyOnChangeEmissionUnit();
				} catch (Exception ex) {
					return;
				}
			}

			try {
				getFacilityService().saveEmissionUnitReplacements(
						emissionUnit.getEmissionUnitReplacements(),
						emissionUnit.getEmuId());
				this.facility.setValidated(false);
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil
						.displayError("Save emission unit replacement data failed");
			}
			this.refreshEmissionUnitReplacements();
		}
		
		// update fpId if it is changed due to copy on change flag
		fpId = emissionUnit.getFpId();
	}

	private boolean validateEngineReplacement() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		validationMessages = this.emissionUnitReplacement
				.validateEngine(emissionUnit.getEmissionUnitReplacements());

		String pageViewId = "";
		if (displayValidationMessages(pageViewId, validationMessages)) {
			isValid = false;
		}

		return isValid;
	}

	private boolean validateDeleteEngineReplacement() {
		boolean isValid = true;
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();

		if (this.emissionUnit.getEmissionUnitReplacements().isEmpty()) {
			valMessages
					.add(new ValidationMessage(
							"",
							"There must be at least one entry in the serial number tracking table.",
							ValidationMessage.Severity.ERROR, null));
		} else {
			if (this.emissionUnitReplacement.isSerialNumberCurrent()) {
				valMessages
						.add(new ValidationMessage(
								"",
								"Cannot remove the current engine from the serial number tracking table.",
								ValidationMessage.Severity.ERROR, null));
			}
		}

		ValidationMessage[] validationMessages = valMessages
				.toArray(new ValidationMessage[0]);

		String pageViewId = "";
		if (displayValidationMessages(pageViewId, validationMessages)) {
			isValid = false;
		}

		return isValid;
	}

	public void deleteEngineReplacement() {
		this.emissionUnit
				.removeEmissionUnitReplacement(this.emissionUnitReplacement);

		if (!validateDeleteEngineReplacement()) {
			this.emissionUnit
					.addEmissionUnitReplacement(this.emissionUnitReplacement);
			return;
		}

		saveEngineSerialNumber();

		closeDialog();
	}

	public EmissionUnitReplacement getCurrentEngineSerialNumber() {
		return currentEngineSerialNumber;
	}

	public void setCurrentEngineSerialNumber(
			EmissionUnitReplacement currentEngineSerialNumber) {
		this.currentEngineSerialNumber = currentEngineSerialNumber;
	}

	public boolean isNoEngineSerialNumbers() {
		return noEngineSerialNumbers;
	}

	public void setNoEngineSerialNumbers(boolean noEngineSerialNumbers) {
		this.noEngineSerialNumbers = noEngineSerialNumbers;
	}

	public String getSerialNumberText() {
		return this.serialNumberText;
	}

	public void setSerialNumberText(String serialNumberText) {
		this.serialNumberText = serialNumberText;
	}

	public boolean isEngineSerialTrackingEditMode() {
		return engineSerialTrackingEditMode;
	}

	public void setEngineSerialTrackingEditMode(
			boolean engineSerialTrackingEditMode) {
		this.engineSerialTrackingEditMode = engineSerialTrackingEditMode;
	}

	public boolean isEngineSerialNumberStatusEditable() {
		return engineSerialNumberStatusEditable;
	}

	public void setEngineSerialNumberStatusEditable(
			boolean engineSerialNumberStatusEditable) {
		this.engineSerialNumberStatusEditable = engineSerialNumberStatusEditable;
	}

	public List<EmissionUnitReplacement> getOldSerialNumbers() {
		return oldSerialNumbers;
	}

	public void setOldSerialNumbers(
			List<EmissionUnitReplacement> oldSerialNumbers) {
		this.oldSerialNumbers = oldSerialNumbers;
	}

	public List<EmissionUnitReplacement> getCurrentSerialNumbers() {
		return currentSerialNumbers;
	}

	public void setCurrentSerialNumbers(
			List<EmissionUnitReplacement> currentSerialNumbers) {
		this.currentSerialNumbers = currentSerialNumbers;
	}

	public List<EuEmission> getOldEUPermittedEmissions() {
		return oldEUPermittedEmissions;
	}

	public void setOldEUPermittedEmissions(
			List<EuEmission> oldEUPermittedEmissions) {
		this.oldEUPermittedEmissions = oldEUPermittedEmissions;
	}
	
	public ValidationMessage[] checkReferencesToEu(EmissionUnit emissionUnit) {
		
		/*
		 * In the future try to move the FK table names and keys to an external 
		 * configuration, may be a def. list or some configuration xml
		 */
		
		final String fkTableName = "FP_EMISSIONS_UNIT";
		final String fkColumnName = "EMU_ID";
		final String schema ="dbo";
		
		EmissionUnit pastEmissionUnits[];
		EmissionUnit currentEmissionUnit;
		
		List<ForeignKeyReference> fkReferences = new ArrayList<ForeignKeyReference>();
		ArrayList<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>(0);
		
		try{
			fkReferences = getInfrastructureService().retrieveForeignKeyReferences(fkTableName, fkColumnName, schema);
			
			if(null != fkReferences) {
				
				//following references should not prevent EU from being invalidated so remove them from the list (if they exist)
				fkReferences.remove(new ForeignKeyReference("FP_EMISSIONS_UNIT_REPLACEMENT", "EMU_ID", "FP_EMISSIONS_UNIT_REPLACEMENT"));
				fkReferences.remove(new ForeignKeyReference("FP_EMISSIONS_UNIT_TYPE", "EMU_ID", "FP_EMISSIONS_UNIT_TYPE"));
				fkReferences.remove(new ForeignKeyReference("FP_EU_EMISSIONS", "EMU_ID", "FP_EU_EMISSIONS"));
				fkReferences.remove(new ForeignKeyReference("FP_EMISSION_PROCESS", "EMU_ID", "FP_EMISSION_PROCESS"));
				fkReferences.remove(new ForeignKeyReference("FP_EU_FUG_COMPONENT_XREF", "EMU_ID", "FP_EU_FUG_COMPONENT_XREF"));
				// get the past eus
				pastEmissionUnits = getFacilityService().retrieveEmissionUnitsFromPastProfiles(emissionUnit.getCorrEpaEmuId(),
						facility.getFacilityId());
				
				currentEmissionUnit = getFacilityService().retrieveEmissionUnitFromCurrentProfile(emissionUnit.getCorrEpaEmuId(),
						facility.getFacilityId());
				
				for(ForeignKeyReference fkRef : fkReferences) {
					for(EmissionUnit pastEmissionUnit : pastEmissionUnits) {
						if(getInfrastructureService().checkForeignKeyReferencedData(fkRef.getFkTableName(),
								fkRef.getFkColumnName(), pastEmissionUnit.getEmuId())) {
							ValidationMessage valMsg = new ValidationMessage(
									"Invalid EU",
									"Emission Unit has reference to one or more " + fkRef.getFkObjectName(),
									ValidationMessage.Severity.WARNING, "emissionUnit:"
											+ epaEmuId, epaEmuId);
							validationMessages.add(valMsg);
							break; // don't want to print the same message multiple times
						}
					}
					// check current version
					// If currentEmissionUnit is null, it is newly-created on
					// the portal and there is no need to validate.
					if (currentEmissionUnit != null) {
						if (getInfrastructureService().checkForeignKeyReferencedData(fkRef.getFkTableName(),
								fkRef.getFkColumnName(), currentEmissionUnit.getEmuId())) {
							if (fkRef.getFkTableName().equalsIgnoreCase("PA_EU")) {
								// don't allow to change the eu operating status
								// to invalid if is is included
								// in the application associated with current
								// facility inventory
								ValidationMessage valMsg = new ValidationMessage("Invalid EU",
										"Emission Unit has reference to one or more " + fkRef.getFkObjectName()
												+ " associated with the current version of the facility inventory. ",
										ValidationMessage.Severity.ERROR, "emissionUnit:" + epaEmuId, epaEmuId);
								validationMessages.add(valMsg);
							} else {
								ValidationMessage valMsg = new ValidationMessage("Invalid EU",
										"Emission Unit has reference to one or more " + fkRef.getFkObjectName(),
										ValidationMessage.Severity.WARNING, "emissionUnit:" + epaEmuId, epaEmuId);
								validationMessages.add(valMsg);
							}
						}
					}
				}
			}
			
			// need to check for any references to corr_epa_emu_id of this emission unit 
			ArrayList<ForeignKeyReference> corrEmuIdRefs = new ArrayList<ForeignKeyReference>(0);
			corrEmuIdRefs.add(new ForeignKeyReference("RP_REPORT_EU","CORR_ID", "Emissions Inventory"));
			corrEmuIdRefs.add(new ForeignKeyReference("CE_STACK_TEST_POLLUTANT_XREF","TESTED_EU", "Stack Test(s)"));
			corrEmuIdRefs.add(new ForeignKeyReference("PT_PERMIT_CONDITION_EU_XREF","CORR_EPA_EMU_ID", "Permit Condition(s)"));
			
			for(ForeignKeyReference fkRef : corrEmuIdRefs) {
				if(getInfrastructureService().checkForeignKeyReferencedData(fkRef.getFkTableName(),
						fkRef.getFkColumnName(), emissionUnit.getCorrEpaEmuId())) {
					Severity severity = Severity.WARNING;
					
					// don't allow to change the eu operating status to invalid if is is included in 
					// a permit condition. setting the severity level to error does that.
					if(fkRef.getFkTableName().equals("PT_PERMIT_CONDITION_EU_XREF")) {
						severity = Severity.ERROR; 
					}
					
					ValidationMessage valMsg = new ValidationMessage(
							"Invalid EU",
							"Emission Unit has reference to one or more " + fkRef.getFkObjectName(),
							severity, "emissionUnit:"
									+ epaEmuId, epaEmuId);
					validationMessages.add(valMsg);
				}
			}
		}catch (RemoteException e) {
			handleException(e);
		}
		
		return validationMessages.toArray(new ValidationMessage[0]);	
	}
	
	public final boolean isAllowEuDelete() {
		return (facility != null
				&& facility.getVersionId() == -1
				&& isStars2Admin()
				&& emissionUnit != null
				&& emissionUnit.getEpaEmuId() != null
				&& emissionUnit.getOperatingStatusCd().equalsIgnoreCase(EuOperatingStatusDef.IV));
	}	
	
	public final void cancelDeleteEU(ActionEvent actionEvent) {
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public final void deleteEU(ActionEvent actionEvent) {
		ValidationMessage validationMessages[];
		String pageViewId = FAC_CLIENT_ID + "emissionUnit:";
		EmissionUnit euToDelete = null;
		Integer corrEpaEmuId = null;
		
		FacesUtil.returnFromDialogAndRefresh();
		
		if (emissionUnit != null) {
			// save the corr_epa_emu_id of the selected emission unit
			// it will be later used to retrieve the correct (deleting) emission unit
			// after creating the new facility profile
			corrEpaEmuId = emissionUnit.getCorrEpaEmuId();
			
			// check if the deleting emission unit is refered by other objects - application, permit etc
			// if yes, then don't delete the emission unit
			validationMessages = checkReferencesToEu(emissionUnit);
			 if(validationMessages.length != 0) {
				 displayValidationMessages(pageViewId, validationMessages);
				 DisplayUtil.displayError("Cannot delete emission unit: " + emissionUnit.getEpaEmuId());
			 }
			 else {
				try {
					// preserve copy of current facility inventory before deleting the eu so that
					// there is history of the emission unit in invalid state before being deleted
					// from the facility inventory 
					getFacilityService().markProfileHistory(fpId);
					copyOnChangeFacilityProfile();
					
					// we have the new facility profile and the emu_id of the emission unit to be deleted
					// has changed. Retrieve the deleting emission unit from the newly created facility profile
					euToDelete = getFacilityService().retrieveEmissionUnitByCorrEpaEmuId(fpId, corrEpaEmuId);
					if(euToDelete != null) {
						getFacilityService().removeEmissionUnit(euToDelete);
						facility.removeEmissionUnit(euToDelete);
						
						// create field audit log entry
						FieldAuditLog auditLog[] = new FieldAuditLog[1];
						auditLog[0] = new FieldAuditLog();
						auditLog[0].setAttributeCd("eudl");
						auditLog[0].setFacilityId(facilityId);
						auditLog[0].setFacilityName(facility.getName());
						auditLog[0].setUniqueId(euToDelete.getEpaEmuId());
						auditLog[0].setOriginalValue("Emission Unit Exists");
						auditLog[0].setNewValue("Emission Unit Deleted");
						auditLog[0].setCorrEmuId(euToDelete.getCorrEpaEmuId());
						
						getFacilityService().createFieldAuditLog(auditLog, currentUserId);
						
						DisplayUtil
						.displayInfo("Emission Unit successfully deleted.");
					}
					else {
						// this should not happen
						DisplayUtil
						.displayError("An unknown error occured while trying to retrieve the emission unit"
								+ "to delete. (fpId: " + fpId + " corrEpaEmuId: " + corrEpaEmuId +")");
					}
					refreshFacility();
					
				} catch (RemoteException e) {
					handleException(e);
				}
				catch(Exception e) {
					DisplayUtil.displayError("Error occured while trying to preserve the current facility inventory.");
	                logger.error(e.getMessage(), e);
				}
			 }
		}
	}
	
	public final boolean isAllowedToEditWiseViewId() {
		return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("editAQDWiseViewId");
	}
	
	public final boolean isEUEditOperation() {
		if (operation[EM_UNIT_COMP] == EDIT_OP) {
			return true;
		} else {
			return false;
		}
	}
	
	public final boolean isEuTypeLockedForEdit() {
		if(editable) {
			// admin user can always edit the eu type
			if(InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
				return false;
			}
			
			// if it is a new eu being created then eu type should be editable
			if(operation[EM_UNIT_COMP] == CREATE_OP || operation[EM_UNIT_COMP] == CLONE_OP) {
				return false;
			}
			
			// if this eu exists in historic versions, then eu type should not be editable
			if(isHistoricEmissionUnit()) {
				return true;
			}
			
			// if the current facility version is preserved, then eu type should not be editable
			if(facility.getVersionId() == -1 && facility.isCopyOnChange()) {
				return true;
			}
			
			// if you come here then eu type should be editable
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Returns true (allowed to change the emission unit type) when following conditions are met
	 * <br>
	 * <b>Internal app:</b><br>
	 * 	- admin user or<br>
	 * 	- eu operating status is not shutdown and<br>
	 * 	- eu does not exist in historic facility version(s) and<br>
	 *  - the current facility version is not preserved<br>
	 *  <br>
	 * <b>Portal app:</b><br>
	 * 	- eu operating status is not shutdown and<br>
	 *  - eu does not exist in the readonly (internal) database<br>
	 *  <br>
	 * @return true if eu type can be changed, otherwise false
	 */
	public final boolean isAllowedToChangeEUType() {
		
		boolean allowedToChangeEUType = false;
		if(editable) {
			if (isPublicApp()) {
				allowedToChangeEUType = false;
			} else if (isInternalApp()) {
				boolean euOperStatusUpdatable = isEuOperStatusUpdatable();
				boolean euTypeLockedForEdit = isEuTypeLockedForEdit();
				
				allowedToChangeEUType = euOperStatusUpdatable && !euTypeLockedForEdit;
				
			} else if (isPortalApp()) {
				boolean euOperStatusUpdatable = isEuOperStatusUpdatable();
				boolean euExistsInternally = isEUExistsInternally();
				
				allowedToChangeEUType = euOperStatusUpdatable && !euExistsInternally;
			}
		}
		
		return allowedToChangeEUType;
	}
	
	public boolean isEUExistsInternally() {
		boolean euExistsInternally = false;
		try {
			Facility internalFac = getFacilityService().retrieveFacility(
					facility.getFacilityId(), false);

			// If EU exists in internal IMPACT, return true
			EmissionUnit internalEU = internalFac
					.getMatchingEmissionUnit(emissionUnit.getCorrEpaEmuId());
			if (internalEU != null) {
				euExistsInternally = true;
			}
		} catch (RemoteException e) {
			handleException(e);
		}
		return euExistsInternally;
	}
	
	/* START CODE: FACILITY CEM COM LIMIT */

	protected final void initializeFacilityCemComLimitList(Integer monitorId) {
		this.facilityCemComLimitsWrapper = new TableSorter();
		try {
			this.facility.setFacilityCemComLimitList(getFacilityService()
					.retrieveFacilityCemComLimitListByMonitorId(
							monitorId, staging));
			this.facilityCemComLimitsWrapper = new TableSorter();
			this.facilityCemComLimitsWrapper.setWrappedData((this.facility)
					.getFacilityCemComLimitList());

		} catch (RemoteException re) {
			logger.error("failed to get Facility CEM/COM/CMS Limits", re);
		} finally {
			this.facilityCemComLimitEditMode = false;
		}
	}
	
	protected final void initializeFacilityCemComLimitList() {
		this.facilityCemComLimitsWrapper = new TableSorter();
		try {
			this.facility.setFacilityCemComLimitList(getFacilityService()
					.retrieveFacilityCemComLimitListByFpId(
							this.facility.getFpId(), staging));
			this.facilityCemComLimitsWrapper = new TableSorter();
			this.facilityCemComLimitsWrapper.setWrappedData((this.facility)
					.getFacilityCemComLimitList());

		} catch (RemoteException re) {
			logger.error("failed to get Facility CEM/COM/CMS Limits", re);
		} finally {
			this.facilityCemComLimitEditMode = false;
		}
	}

	public final String startToAddFacilityCemComLimit() {
		this.facilityCemComLimitEditMode = true;
		this.modifyFacilityCemComLimit = new FacilityCemComLimit();
		this.modifyFacilityCemComLimit.setAddedBy(InfrastructureDefs
				.getCurrentUserId());
		
		ContinuousMonitorDetail cmd = (ContinuousMonitorDetail) FacesUtil.getManagedBean("continuousMonitorDetail");
		
		this.modifyFacilityCemComLimit.setMonitorId(cmd.getContinuousMonitorId());
		this.newFacilityCemComLimit = true;

		return "dialog:facilityMonitorCemComLimitDetail";
	}

	public final String startToEditFacilityCemComLimit() {
		int index = this.facilityCemComLimitsWrapper.getRowIndex();
		this.modifyFacilityCemComLimit = getSelectedFacilityCemComLimit(index);
		// this.modifyFacilityCemComLimit.setAddedBy(InfrastructureDefs.getCurrentUserId());

		this.newFacilityCemComLimit = false;
		this.facilityCemComLimitEditMode = false;

		return "dialog:facilityMonitorCemComLimitDetail";
	}
	
	public final String startToViewFacilityCemComLimit() {
		int index = this.facilityCemComLimitsWrapper.getRowIndex();
		this.modifyFacilityCemComLimit = getSelectedFacilityCemComLimit(index);
		// this.modifyFacilityCemComLimit.setAddedBy(InfrastructureDefs.getCurrentUserId());

		this.newFacilityCemComLimit = false;
		this.facilityCemComLimitEditMode = false;

		return "dialog:facilityCemComLimitDetail";
	}

	public final String viewFacilityCemComLimit(FacilityCemComLimit facilityCemComLimit) {
		this.modifyFacilityCemComLimit = facilityCemComLimit;

		this.newFacilityCemComLimit = false;
		this.facilityCemComLimitEditMode = false;

		return "dialog:facilityCemComLimitDetail";
	}
	
	public FacilityCemComLimit getModifyFacilityCemComLimit() {

		return this.modifyFacilityCemComLimit;
	}

	public void setModifyFacilityCemComLimit(
			FacilityCemComLimit modifyFacilityCemComLimit) {
		this.modifyFacilityCemComLimit = modifyFacilityCemComLimit;
	}

	private final FacilityCemComLimit getSelectedFacilityCemComLimit(int index) {
		FacilityCemComLimit fccl = (FacilityCemComLimit) this.facilityCemComLimitsWrapper
				.getRowData(index);

		return fccl;
	}

	public boolean isNewFacilityCemComLimit() {
		return newFacilityCemComLimit;
	}

	public void setNewFacilityCemComLimit(boolean newFacilityCemComLimit) {
		this.newFacilityCemComLimit = newFacilityCemComLimit;
	}

	public final void editFacilityCemComLimit() {
		this.facilityCemComLimitEditMode = true;
		this.newFacilityCemComLimit = false;
	}

	public final void saveFacilityCemComLimit() {

		DisplayUtil.clearQueuedMessages();

		//this.getModifyFacilityCemComLimit().setAddedBy(
		//		InfrastructureDefs.getCurrentUserId());
		
		if (this.getModifyFacilityCemComLimit().getLimitDesc() != null) {
			this.getModifyFacilityCemComLimit().setLimitDesc(this.getModifyFacilityCemComLimit().getLimitDesc().trim());
		}
		if (this.getModifyFacilityCemComLimit().getLimitSource() != null) {
			this.getModifyFacilityCemComLimit().setLimitSource(this.getModifyFacilityCemComLimit().getLimitSource().trim());
		}

		if (facilityCemComLimitValidated()) {
			if (this.newFacilityCemComLimit) {
				createFacilityCemComLimit();
			} else {
				updateFacilityCemComLimit();
			}
		}
	}

	public final boolean isDuplicateLimit() {

		boolean ret = false;
		boolean dup = false;

		List<FacilityCemComLimit> limits = (this.facility)
				.getFacilityCemComLimitList();

		int count = 0;
		for (FacilityCemComLimit item : limits) {
			dup = false;
			if ((item.getLimitSource() != null && !Utility.isNullOrEmpty(item
					.getLimitSource().trim()))
					&& (this.getModifyFacilityCemComLimit().getLimitSource() != null)
					&& !Utility.isNullOrEmpty(this
							.getModifyFacilityCemComLimit().getLimitSource()
							.trim())) {
				// If Description and Source are identical to an existing row,
				// consider the row to be a duplicate.
				if ((item.getLimitDesc().trim().equals(this
						.getModifyFacilityCemComLimit().getLimitDesc().trim()))
						&& ((item.getLimitSource().trim().equals(this
								.getModifyFacilityCemComLimit()
								.getLimitSource().trim())))) {
					dup = true;
				}
			} else if ((item.getLimitSource() == null || (item.getLimitSource() != null && Utility
					.isNullOrEmpty(item.getLimitSource().trim())))
					&& ((this.getModifyFacilityCemComLimit().getLimitSource() == null) || (this
							.getModifyFacilityCemComLimit().getLimitSource() != null && Utility
							.isNullOrEmpty(this.getModifyFacilityCemComLimit()
									.getLimitSource().trim())))) {
				// If Description is identical to an existing row and both
				// sources are empty/blank/null, consider the row to be a
				// duplicate.
				if ((item.getLimitDesc().trim().equals(this
						.getModifyFacilityCemComLimit().getLimitDesc().trim()))) {
					dup = true;
				}
			}
			// Only report a duplicate if the existing row doesn't have an end
			// date.
			if (dup) {
				if (item.getEndDate() == null
						|| (item.getEndDate() != null && Utility
								.isNullOrEmpty(item.getEndDate().toString()))) {

					count++;
					if (newFacilityCemComLimit
							|| (!newFacilityCemComLimit && count > 1)) {
						logger.debug("Another active Limit with this description and source is already in the Facility CEM/COM/CMS Limit table. Duplicate description = "
								+ (String) item.getLimitDesc()
								+ "Duplicate source = "
								+ (String) item.getLimitSource());
						return true;
					}
				}
			}

		}

		return ret;
	}

	public void closeFacilityCemComLimitDialog() {
		this.refreshFacilityCemComLimits();
		closeDialog();
	}
	
	public void closeFacilityMonitorCemComLimitDialog() {
		this.refreshFacilityCemComLimitsByMonitor(this.getModifyFacilityCemComLimit().getMonitorId());
		closeDialog();
	}

	protected final void refreshFacilityCemComLimits() {
		this.facilityCemComLimitEditMode = false;
		this.newFacilityCemComLimit = false;
		this.initializeFacilityCemComLimitList();
	}
	
	public final void refreshFacilityCemComLimitsByMonitor(Integer monitorId) {
		this.facilityCemComLimitEditMode = false;
		this.newFacilityCemComLimit = false;
		this.initializeFacilityCemComLimitList(monitorId);
	}

	public void createFacilityCemComLimit() {

		try {

			Integer monitorId = modifyFacilityCemComLimit.getMonitorId();
			
			modifyFacilityCemComLimit = getFacilityService().createFacilityCemComLimit(
					this.getModifyFacilityCemComLimit());
			
			// if a new facility version was created then refresh the facility profile
			if(!monitorId.equals(modifyFacilityCemComLimit.getMonitorId())) {
				// get the fpId associated with the new monitor
				ContinuousMonitor cm = getContinuousMonitorService().
						retrieveContinuousMonitor(modifyFacilityCemComLimit.getMonitorId());
				setFpId(cm.getFpId());
				
				refreshContinuousMonitorDetail(modifyFacilityCemComLimit.getMonitorId());
			}
			refreshFacilityProfile();
			
			DisplayUtil.displayInfo("Create Facility CEM/COM/CMS Limit Success");


		} catch (RemoteException e) {
			logger.error("Create Facility CEM/COM/CMS Limit Failed", e);
			DisplayUtil.displayError("Create Facility CEM/COM/CMS Limit Failed");
			handleException(e);

		} finally {
			this.refreshFacilityCemComLimitsByMonitor(this.getModifyFacilityCemComLimit().getMonitorId());
			FacesUtil.returnFromDialogAndRefresh();
		}
	}
	
	private void refreshContinuousMonitorDetail(Integer monitorId) {
		ContinuousMonitorDetail cmDetailBean = (ContinuousMonitorDetail) FacesUtil
				.getManagedBean("continuousMonitorDetail");
		
		if(null != cmDetailBean) {
			boolean editable = cmDetailBean.isEditable();
			cmDetailBean.setContinuousMonitorId(monitorId);
			cmDetailBean.submit();
			cmDetailBean.setEditable(editable);
		}
	}

	public void updateFacilityCemComLimit() {

		try {
			// Modify Facility CEM/COM/CMS Limit
			Integer monitorId = modifyFacilityCemComLimit.getMonitorId();
			
			modifyFacilityCemComLimit = getFacilityService().modifyFacilityCemComLimit(
					this.getModifyFacilityCemComLimit());
			
			// if a new facility version was created then refresh the facility profile
			if(!monitorId.equals(modifyFacilityCemComLimit.getMonitorId())) {
				// get the fpId associated with the new monitor
				ContinuousMonitor cm = getContinuousMonitorService().
						retrieveContinuousMonitor(modifyFacilityCemComLimit.getMonitorId());
				setFpId(cm.getFpId());
				
				refreshContinuousMonitorDetail(modifyFacilityCemComLimit.getMonitorId());
			}
			refreshFacilityProfile();

			DisplayUtil.displayInfo("Update Facility CEM/COM/CMS Limit Success");

		} catch (RemoteException e) {
			logger.error("Update Facility CEM/COM/CMS Limit Failed", e);
			DisplayUtil.displayError("Update Facility CEM/COM/CMS Limit Failed");
			handleException(e);
		} finally {
			this.refreshFacilityCemComLimitsByMonitor(this.getModifyFacilityCemComLimit().getMonitorId());
			closeDialog();
		}
	}

	public void deleteFacilityCemComLimit(ReturnEvent re) {
		
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}

		DisplayUtil.clearQueuedMessages();

		Integer monitorId = getModifyFacilityCemComLimit().getMonitorId();
		try {
			Integer newFpId = getFacilityService().removeFacilityCemComLimit(
					this.modifyFacilityCemComLimit);
			
			// if a new facility version was created then refresh the monitor
			// limits.
			if(!getFpId().equals(newFpId)) {
				setFpId(newFpId);
				
				// in order to refresh the associated cem/com limits, 
				// first we need to get the corresponding logical monitor id (with which
				// the deleted limit was associated) from the new version of the facility.
				// then we can get the list of cem/com limits associated with this
				// logical monitor id.
				ContinuousMonitor cm = getContinuousMonitorService().
						retrieveContinuousMonitor(monitorId);
								
				for(ContinuousMonitor monitor : 
						getContinuousMonitorService().retrieveContinuousMonitorByFpId(newFpId)) {
					if(monitor.getCorrMonitorId().equals(cm.getCorrMonitorId())) {
						// id of the corresponding logical monitor in the new current facility version
						monitorId = monitor.getContinuousMonitorId();
						break;
					}
				}
				
				refreshContinuousMonitorDetail(monitorId);
			}
			
			refreshFacilityProfile();
			
			DisplayUtil.displayInfo("Delete Facility CEM/COM/CMS Limit Success");

		} catch (RemoteException e) {
			logger.error("Remove Facility CEM/COM/CMS Limit failed", e);
			DisplayUtil.displayError("Remove Facility CEM/COM/CMS Limit failed");
			handleException(e);
		} finally {
			this.refreshFacilityCemComLimitsByMonitor(monitorId);
			closeDialog();
		}
	}

	public void cancelFacilityCemComLimit() {
		this.refreshFacilityCemComLimitsByMonitor(this.getModifyFacilityCemComLimit().getMonitorId());
		closeDialog();
	}

	public final TableSorter getFacilityCemComLimitsWrapper() {
		return this.facilityCemComLimitsWrapper;
	}

	public final void setFacilityCemComLimitsWrapper(
			TableSorter facilityCemComLimitsWrapper) {
		this.facilityCemComLimitsWrapper = facilityCemComLimitsWrapper;
	}

	public boolean facilityCemComLimitValidated() {

		if (this.getModifyFacilityCemComLimit().getStartDate() == null
				|| Utility.isNullOrEmpty(this.getModifyFacilityCemComLimit()
						.getStartDate().toString())) {
			DisplayUtil.displayError("ERROR: Attribute Start Date is not set.");
			return false;
		} else if (this.getModifyFacilityCemComLimit().getEndDate() != null
				&& !Utility.isNullOrEmpty(this.getModifyFacilityCemComLimit()
						.getEndDate().toString())) {

			if (!this.getModifyFacilityCemComLimit().getStartDate()
					.before(this.getModifyFacilityCemComLimit().getEndDate())
					&& !this.getModifyFacilityCemComLimit()
							.getStartDate()
							.equals(this.getModifyFacilityCemComLimit()
									.getEndDate())) {

				DisplayUtil
						.displayError("ERROR: The End Date must be after the Start Date OR the same as the Start Date.");
				return false;

			}
		}

		if (this.getModifyFacilityCemComLimit().getLimitDesc() == null
				|| Utility.isNullOrEmpty(this.getModifyFacilityCemComLimit()
						.getLimitDesc().trim())) {
			DisplayUtil
					.displayError("ERROR: Attribute Limit Description is not set.");
			return false;
		}

		if (this.isDuplicateLimit()) {
			DisplayUtil
					.displayError("ERROR: An active Facility CEM/COM/CMS Limit with this description and source already exists. Multiple active instances of this limit are not allowed.");
			return false;
		}

		return true;

	}

	public final void closeDialogFacilityCemComLimit() {
		setFacilityCemComLimitEditMode(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public boolean isFacilityCemComLimitEditMode() {
		return facilityCemComLimitEditMode;
	}

	public void setFacilityCemComLimitEditMode(
			boolean facilityCemComLimitEditMode) {
		this.facilityCemComLimitEditMode = facilityCemComLimitEditMode;
	}

	public final boolean getFacilityCemComLimitEditAllowed() {
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| !isReadOnlyUser();

		// return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
		// || InfrastructureDefs.getCurrentUserAttrs()
		// .isCurrentUseCaseValid("editEnforcementAction");
	}

	public final boolean getFacilityCemComLimitDeleteAllowed() {

		// If any Compliance Reports exist with this limit, Delete is not
		// allowed.
		if (isPublicApp()) {
			return false;
		} else if (isInternalApp()) {
			if (getFacilityLimitComplianceReportCount() != 0) {
				setFacilityCemComLimitDeleteAllowedMsg(
						"Delete is disabled because at least one Compliance Report includes this Limit.");
				return false;
			} else if (getFacilityLimitInspectionReportCount() != 0) {
				setFacilityCemComLimitDeleteAllowedMsg(
						"Delete is disabled because at least one Inspection Report includes this Limit.");
				return false;
			}
		}

		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| !isReadOnlyUser();
	}

	public Integer getFacilityLimitInspectionReportCount() {
		Integer facilityLimitReportCount = 0;
		if (modifyFacilityCemComLimit == null) {
			logger.debug("getFacilityMonitorComplianceReportCount: modifyFacilityCemComLimit is null");
		} else {
			try {
				List<String> insp = getFacilityService()
						.retrieveInspectionIdsForCemComId(modifyFacilityCemComLimit.getLimitId());
				facilityLimitReportCount = insp == null ? 0 : insp.size();
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing facility limit inspection report count failed");
			}
		}

		return facilityLimitReportCount;
	}
	
	public final boolean getFacilityCemComCreateLimitAllowed() {
		if (isPublicApp()) {
			return false;
		}
		
		return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
				|| !isReadOnlyUser();

		// return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
		// || InfrastructureDefs.getCurrentUserAttrs()
		// .isCurrentUseCaseValid("createEnforcementAction");
	}

	public final String getCCLTableWidth() {
		return (CCL_WIDTH - 5) + "";
	}

	/* END CODE: FACILITY CEM COM LIMITS */
	
	/**
	 * returns todays date with time set to 23:59:59
	 */
	public Timestamp getTodaysDate() {
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return new Timestamp(cal.getTimeInMillis());
	}
	
public ValidationMessage[] checkReferencesToEgressPoint(EgressPoint egressPoint) {
		
		final String fkTableName = "FP_EGRESS_POINT";
		final String fkColumnName = "FPNODE_ID";
		final String schema ="dbo";
		
		List<ForeignKeyReference> fkReferences = new ArrayList<ForeignKeyReference>();
		ArrayList<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>(0);
		
		try{
			fkReferences = getInfrastructureService().retrieveForeignKeyReferences(fkTableName, fkColumnName, schema);
			
			if(null != fkReferences) {
				
				for(ForeignKeyReference fkRef : fkReferences) {
					// check in current version only
					if(getInfrastructureService().checkForeignKeyReferencedData(fkRef.getFkTableName(),
						fkRef.getFkColumnName(), egressPoint.getFpNodeId())) {
						ValidationMessage valMsg = new ValidationMessage(
								"Delete Release Point",
								"Release point has reference to one or more " + fkRef.getFkObjectName(),
								ValidationMessage.Severity.WARNING);

						// Since portal user cannot change release point associations to continuous monitors,
						// ask portal user to contact the system administrator.
						if (fkRef.getFkTableName().equalsIgnoreCase("FP_CONTINUOUS_MONITOR_EGRESS_POINT_XREF")) {

							valMsg = new ValidationMessage(
									"Delete Release Point",
									"Release point has reference to one or more "
											+ fkRef.getFkObjectName()
											+ ". Please contact System Administrator.",
									ValidationMessage.Severity.WARNING);

						}

						validationMessages.add(valMsg);
					}
				}
			}
		}catch (RemoteException e) {
			handleException(e);
		}
		
		return validationMessages.toArray(new ValidationMessage[0]);	
	}

	public ContinuousMonitorService getContinuousMonitorService() {
		return continuousMonitorService;
	}
	
	public void setContinuousMonitorService(
			ContinuousMonitorService continuousMonitorService) {
		this.continuousMonitorService = continuousMonitorService;
}
	/**
	 * Returns true if the given emissions unit exists in historic facility version(s)
	 */
	public boolean isHistoricEmissionUnit() {
		boolean ret = false;
		
		Integer corrEpaEmuId = getEmissionUnit().getCorrEpaEmuId();
		if(null != corrEpaEmuId) {
			for(FacilityVersion fv : getFacilityHistory()) {
				if(fv.getVersionId() != -1) {
					try{
						EmissionUnit eu = getFacilityService().retrieveEmissionUnitByCorrEpaEmuId(fv.getFpId(), corrEpaEmuId);
						if(null != eu) {
							ret = true;
							break;
						}
					} catch(RemoteException re) {
						handleException(re);
						DisplayUtil.displayError("Accessing emission unit failed");
					}
				}
			}
		}
		
		return ret;
	}

	public final Integer getFacilityLimitComplianceReportCount() {
		Integer facilityLimitReportCount = 0;
		if (modifyFacilityCemComLimit == null) {
			logger.debug("getFacilityMonitorComplianceReportCount: modifyFacilityCemComLimit is null");
		}

		if (modifyFacilityCemComLimit != null) {
			try {
				facilityLimitReportCount = getFacilityService()
						.retrieveCemsComplianceReportCountWithLimit(
								modifyFacilityCemComLimit.getCorrLimitId());

				logger.debug("facilityMonitorReportCount = "
						+ facilityLimitReportCount);

			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil
						.displayError("Accessing facility limit compliance report count failed");
			}
		} else {
			logger.debug("modifyFacilityCemComLimit is null ... cannot query for facility limit compliance report count.");
		}

		return facilityLimitReportCount;
	}
	
	protected boolean isDuplicateApiNo(String apiNo) {
		boolean isDuplicate = false;
		List<String> facilityIdList = new ArrayList<String>();
		try {
			facilityIdList = getFacilityService()
					.retrieveFacilitiesWithMatchingApiNumber(
							apiNo, this.facilityId);
			if(facilityIdList.size() > 0) {
				DisplayUtil
						.displayError("Duplicate API number. API number exists in the following facilities: "
								+ StringUtils.join(
										facilityIdList.toArray(new String[0]),
										","));
				DisplayUtil
						.displayInfo("If you believe that the API number you entered is correct, please contact the System Administrator.");
				isDuplicate = true;
			}
		} catch(DAOException e) {
			handleException(e);
		}
		
		return isDuplicate;
	}

	public Facility getCurrentFacility() {
		return currentFacility;
	}

	public void setCurrentFacility(Facility currentFacility) {
		this.currentFacility = currentFacility;
	}
	
	public final Facility getCurrentFacilityData() {

		if (!Utility.isNullOrEmpty(facilityId)) {
			try {
				currentFacility = getFacilityService().retrieveFacilityData(facilityId, -1);
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing current facility data failed");
			}
			return currentFacility;
		} else {
			return null;
		}
	}

	public String getFacilityCemComLimitDeleteAllowedMsg() {
		return facilityCemComLimitDeleteAllowedMsg;
	}

	public void setFacilityCemComLimitDeleteAllowedMsg(String facilityCemComLimitDeleteAllowedMsg) {
		this.facilityCemComLimitDeleteAllowedMsg = facilityCemComLimitDeleteAllowedMsg;
	}

	
	// should the HydroCarbon Analysis section on the facility detail be displayed?
	public boolean getShowHCAnalysisSection() {

		boolean showHCAnalysisSection = false;

		if (isPublicApp()) {
			showHCAnalysisSection = false;
		} else {
			if (null != this.facility) {
				showHCAnalysisSection = FacilityTypeDef.hasHCAnalysis(this.facility.getFacilityTypeCd());
			}
		}

		return showHCAnalysisSection;
	}

	public TableSorter getHydrocarbonAnalysisWrapper() {
		return hydrocarbonAnalysisWrapper;
	}

	public void setHydrocarbonAnalysisWrapper(TableSorter hydrocarbonAnalysisWrapper) {
		this.hydrocarbonAnalysisWrapper = hydrocarbonAnalysisWrapper;
	}
	
	public void setNullforUnusedFields() {
		
		// Setting the values for fields that don't exist on the screen to NULL, for Storage Tanks.
		if (emissionUnit.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.TNK)) {
			
			if (!Utility.isNullOrEmpty(emissionUnit.getEmissionUnitType().getMaterialTypeStored())) {
				if (!emissionUnit.getEmissionUnitType().getMaterialTypeStored()
						.equals(TnkMaterialStoredTypeDef.LIQUID)) {
					emissionUnit.getEmissionUnitType().setLiquidMaterialTypeStored(null);
				}
			}
			
			if (!Utility.isNullOrEmpty(emissionUnit.getEmissionUnitType().getLiquidMaterialTypeStored())) {
				if (!emissionUnit.getEmissionUnitType().getLiquidMaterialTypeStored()
						.equals(TnkMaterialStoredTypeLiquidDef.OTHER)) {
					emissionUnit.getEmissionUnitType().setMaterialTypeStoredDesc(null);
				}
			}
		}
		
		// Setting the values for fields that don't exist on the screen to NULL, for Pnemuatic Equipment.
		if (emissionUnit.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.PNE)) {
			if (!Utility.isNullOrEmpty(emissionUnit.getEmissionUnitType().getEquipmentType())) {
				if (emissionUnit.getEmissionUnitType().getEquipmentType().equals(EquipTypeDef.INTERMITTENT)) {
					emissionUnit.getEmissionUnitType().setGasConsumptionRate(null);
				} else if (emissionUnit.getEmissionUnitType().getEquipmentType().equals(EquipTypeDef.PUMP)) {
					emissionUnit.getEmissionUnitType().setBleedRate(null);
				} else if (emissionUnit.getEmissionUnitType().getEquipmentType().equals(EquipTypeDef.CONTINUOUS)) {
					emissionUnit.getEmissionUnitType().setGasConsumptionRate(null);
					emissionUnit.getEmissionUnitType().setBleedRate(null);
				}
			}
		}
	}
	
	public boolean isDiscloseHC() {
		return discloseHC;
	}

	public void setDiscloseHC(boolean discloseHC) {
		this.discloseHC = discloseHC;
	}
	
	public void hcDisclosureChanged(DisclosureEvent disclosureEvent){
		if (disclosureEvent.isExpanded()){
			setDiscloseHC(true);
		} else {
			setDiscloseHC(false);
		}
		
	}

	public TableSorter getFugComponentsWrapper() {
		return fugComponentsWrapper;
	}

	public void setFugComponentsWrapper(TableSorter fugComponentsWrapper) {
		this.fugComponentsWrapper = fugComponentsWrapper;
	}
	
	public final boolean isEmissionFactorGroupUpdatable() {
		if (isInternalApp() || isPortalApp()) {
			return !editable;
		}

		return false;
	}
	
	public TableSorter getHydrocarbonAnalysisSampleDetailWrapper() {
		return hydrocarbonAnalysisSampleDetailWrapper;
	}

	public void setHydrocarbonAnalysisSampleDetailWrapper(TableSorter hydrocarbonAnalysisSampleDetailWrapper) {
		this.hydrocarbonAnalysisSampleDetailWrapper = hydrocarbonAnalysisSampleDetailWrapper;
	}

	public List<HCAnalysisSampleDetailRow> getHcAnalysisSampleDetailList() {
		return hcAnalysisSampleDetailList;
	}

	public void setHcAnalysisSampleDetailList(List<HCAnalysisSampleDetailRow> hcAnalysisSampleDetailList) {
		this.hcAnalysisSampleDetailList = hcAnalysisSampleDetailList;
	}
	
	//update DTO from UI list before executing BO method
	public void updateFacHydrocarbonAnalysisSampleDetail(){
		
		if (null != this.facility) {
			if (null == this.facility.getHydrocarbonAnalysisSampleDetail()) {
				HydrocarbonAnalysisSampleDetail hcSampleDetail = new HydrocarbonAnalysisSampleDetail();
				hcSampleDetail.setFpId(this.facility.getFpId());
//				hcSampleDetail.setNewObject(true);  // newObject flag should be set to true when the new obejct is created
				facility.setHydrocarbonAnalysisSampleDetail(hcSampleDetail);
			} 
//			else {
//				facility.getHydrocarbonAnalysisSampleDetail().setNewObject(false);
//			}
			for (HCAnalysisSampleDetailRow sampleDetailRow : this.hcAnalysisSampleDetailList){
				if (HCAnalysisSampleDetailRow.SAMPLE_FACILITY_NAME.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityNameGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityNameOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityNameWater((String)sampleDetailRow.getWaterValue());
				} else if (HCAnalysisSampleDetailRow.SAMPLE_FACILITY_API.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityAPIGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityAPIOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityAPIWater((String)sampleDetailRow.getWaterValue());
				} else if (HCAnalysisSampleDetailRow.SAMPLE_FACILITY_PRODUCING_FIELD.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityProducingFieldGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityProducingFieldOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityProducingFieldWater((String)sampleDetailRow.getWaterValue());
				} else if (HCAnalysisSampleDetailRow.SAMPLE_FACILITY_PRODUCING_FORMATION.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityProducingFormationGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityProducingFormationOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFacilityProducingFormationWater((String)sampleDetailRow.getWaterValue());
				} else if (HCAnalysisSampleDetailRow.SAMPLE_DATE.equals(sampleDetailRow.getRowDesc())){
					if (sampleDetailRow.getGasValue() != null){
						facility.getHydrocarbonAnalysisSampleDetail().setSampleDateGas(new Timestamp(((java.util.Date)sampleDetailRow.getGasValue()).getTime()));
					} else {
						facility.getHydrocarbonAnalysisSampleDetail().setSampleDateGas(null);
					}
					if (sampleDetailRow.getOilValue() != null){
						facility.getHydrocarbonAnalysisSampleDetail().setSampleDateOil(new Timestamp(((java.util.Date)sampleDetailRow.getOilValue()).getTime()));
					} else {
						facility.getHydrocarbonAnalysisSampleDetail().setSampleDateOil(null);
					}
					if (sampleDetailRow.getWaterValue() != null){
						facility.getHydrocarbonAnalysisSampleDetail().setSampleDateWater(new Timestamp(((java.util.Date)sampleDetailRow.getWaterValue()).getTime()));
					} else {
						facility.getHydrocarbonAnalysisSampleDetail().setSampleDateWater(null);
					}
				} else if (HCAnalysisSampleDetailRow.SAMPLE_POINT.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setSamplePointGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSamplePointOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSamplePointWater((String)sampleDetailRow.getWaterValue());
				} else if (HCAnalysisSampleDetailRow.ANALYSIS_COMPANY_NAME.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setAnalysisCompanyNameGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setAnalysisCompanyNameOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setAnalysisCompanyNameWater((String)sampleDetailRow.getWaterValue());
				} else if (HCAnalysisSampleDetailRow.ANALYSIS_DATE.equals(sampleDetailRow.getRowDesc())){
					if (sampleDetailRow.getGasValue() != null){
						facility.getHydrocarbonAnalysisSampleDetail().setAnalysisDateGas(new Timestamp(((java.util.Date)sampleDetailRow.getGasValue()).getTime()));
					} else {
						facility.getHydrocarbonAnalysisSampleDetail().setAnalysisDateGas(null);
					}
					if (sampleDetailRow.getOilValue() != null){
						facility.getHydrocarbonAnalysisSampleDetail().setAnalysisDateOil(new Timestamp(((java.util.Date)sampleDetailRow.getOilValue()).getTime()));
					} else {
						facility.getHydrocarbonAnalysisSampleDetail().setAnalysisDateOil(null);
					}
					if (sampleDetailRow.getWaterValue() != null){
						facility.getHydrocarbonAnalysisSampleDetail().setAnalysisDateWater(new Timestamp(((java.util.Date)sampleDetailRow.getWaterValue()).getTime()));
					} else {
						facility.getHydrocarbonAnalysisSampleDetail().setAnalysisDateWater(null);
					}
					
				} else if (HCAnalysisSampleDetailRow.SAMPLE_PRESSURE.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setSamplePressureTxtGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSamplePressureTxtOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSamplePressureTxtWater((String)sampleDetailRow.getWaterValue());
				} else if (HCAnalysisSampleDetailRow.SAMPLE_TEMP.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setSampleTempTxtGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleTempTxtOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleTempTxtWater((String)sampleDetailRow.getWaterValue());
				} else if (HCAnalysisSampleDetailRow.SAMPLE_FLOW_RATE.equals(sampleDetailRow.getRowDesc())){
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFlowRateTxtGas((String)sampleDetailRow.getGasValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFlowRateTxtOil((String)sampleDetailRow.getOilValue());
					facility.getHydrocarbonAnalysisSampleDetail().setSampleFlowRateTxtWater((String)sampleDetailRow.getWaterValue());
				} 
			}

		}
	}
	

	//update table sorter from facility DTO
	private void setupSampleDetailWrapper() {
		this.hydrocarbonAnalysisSampleDetailWrapper = new TableSorter();
		if (null != this.facility){
			this.setHcAnalysisSampleDetailList(facility.convertHydrocarbonAnalysisSampleDetail());	
		}
		this.hydrocarbonAnalysisSampleDetailWrapper.setWrappedData(hcAnalysisSampleDetailList);
	}

	
	public List<DecanePropertyLineItem> getDecanePropertyLineItems() {
		return decanePropertyLineItems;
	}

	public void setDecanePropertyLineItems(List<DecanePropertyLineItem> decanePropertyLineItems) {
		this.decanePropertyLineItems = decanePropertyLineItems;
	}

	public TableSorter getDecanePropertiesWrapper() {
		return decanePropertiesWrapper;
	}

	public void setDecanePropertiesWrapper(TableSorter decanePropertiesWrapper) {
		this.decanePropertiesWrapper = decanePropertiesWrapper;
	}
	
	private void setupDecanePropertiesWrapper() {
		
		if (null != this.facility) {
			
			DecaneProperties decaneProperties = this.facility.getDecaneProperties();
			
			this.decanePropertyLineItems = new ArrayList<DecanePropertyLineItem>();
			this.decanePropertiesWrapper.clearWrappedData();
			
			if (null == decaneProperties) {
				
				this.decanePropertyLineItems.add(new DecanePropertyLineItem(DecaneProperties.MOLECULAR_WEIGHT_PROPERTY_LABEL, null, null));
				this.decanePropertyLineItems.add(new DecanePropertyLineItem(DecaneProperties.SPECIFIC_GRAVITY_PROPERTY_LABEL, null, null));
				
			} else {
				
				this.decanePropertyLineItems.add(new DecanePropertyLineItem(
						DecaneProperties.MOLECULAR_WEIGHT_PROPERTY_LABEL, decaneProperties.getAvgMolecularWtOfOil(),
						decaneProperties.getAvgMolecularWtOfProducedWater()));
				
				this.decanePropertyLineItems.add(new DecanePropertyLineItem(
						DecaneProperties.SPECIFIC_GRAVITY_PROPERTY_LABEL, decaneProperties.getSpecificGravityOfOil(),
						decaneProperties.getSpecificGravityOfProducedWater()));
			}
			
			this.decanePropertiesWrapper = null;
			this.decanePropertiesWrapper = new TableSorter();
			this.decanePropertiesWrapper.setWrappedData(this.decanePropertyLineItems);
		}
	}
	
	
	private void updateDecanePropertiesFromWrapper() {

		if (null != this.facility) {

			DecaneProperties decaneProperties = this.facility.getDecaneProperties();

			if (null == decaneProperties) {

				decaneProperties = new DecaneProperties();
				decaneProperties.setFpId(this.facility.getFpId());
				decaneProperties.setNewObject(true);

			} else {
				
				if (null == decaneProperties.getLastModified()) {
					// this is still a new object
					decaneProperties.setNewObject(true);
				} else {
					decaneProperties.setNewObject(false);
				}
			}

			for (DecanePropertyLineItem item : this.decanePropertyLineItems) {

				if (item.getLabel().equals(DecaneProperties.MOLECULAR_WEIGHT_PROPERTY_LABEL)) {

					decaneProperties.setAvgMolecularWtOfOil(item.getOilCondenstanteValue());
					decaneProperties.setAvgMolecularWtOfProducedWater(item.getProducedWaterValue());

				} else if (item.getLabel().equals(DecaneProperties.SPECIFIC_GRAVITY_PROPERTY_LABEL)) {

					decaneProperties.setSpecificGravityOfOil(item.getOilCondenstanteValue());
					decaneProperties.setSpecificGravityOfProducedWater(item.getProducedWaterValue());

				}
			}
			
			if (decaneProperties.isValid()) {
				decaneProperties.roundUpValues();
			}
			
			this.facility.setDecaneProperties(decaneProperties);
		}
	}

	public String getFacilityRoleCdForActivity() {
		return facilityRoleCdForActivity;
	}

	public void setFacilityRoleCdForActivity(String facilityRoleCdForActivity) {
		this.facilityRoleCdForActivity = facilityRoleCdForActivity;
	}

	public TableSorter getFacUserRoleAactivityWrapper() {
		return facUserRoleAactivityWrapper;
	}

	public void setFacUserRoleAactivityWrapper(TableSorter facUserRoleAactivityWrapper) {
		this.facUserRoleAactivityWrapper = facUserRoleAactivityWrapper;
	}

	public boolean getIsActivityNull() {
		return isActivityNull;
	}

	public void setIsActivityNull(boolean isActivityNull) {
		this.isActivityNull = isActivityNull;
	}

	public final String displayRoleActivities() {
		if (null != this.facRolesWrapper) {
			this.facUserRoleAactivityWrapper = new TableSorter();

			try {
				List<FacilityRoleActivity> activityProcess = getFacilityService()
						.retrieveActivitiesByFacilityRole(this.facilityRoleCdForActivity);
				this.facUserRoleAactivityWrapper.setWrappedData(activityProcess);

				// Display appropriate text in the pop-up.
				if (facUserRoleAactivityWrapper.getRowCount() == 0) {
					isActivityNull = true;
				} else {
					isActivityNull = false;
				}
			} catch (DAOException daoe) {
				handleException(daoe);
				logger.error(daoe);
			}
			return "dialog:facilityRoleActivity";
		} else {
			// This should never happen.
			DisplayUtil.displayError("Cannot display the Activites for this Facility Role.");
			return FAIL;
		}
	}
	
	public final boolean isAdministrativeHoldAdmin() {
		return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("administrativeHold");
	}

}