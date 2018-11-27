package us.oh.state.epa.stars2.webcommon.application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.zip.ZipOutputStream;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import au.com.bytecode.opencsv.CSVReader;
import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.permit.PermitDetail;
import us.oh.state.epa.stars2.app.relocation.Relocation;
import us.oh.state.epa.stars2.app.workflow.ActivityProfile;
import us.oh.state.epa.stars2.bo.ApplicationBO;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage.Severity;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUFugitiveLeaks;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUMaterialUsed;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationNote;
import us.oh.state.epa.stars2.database.dbObjects.application.FacilityWideRequirement;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationBACTEmission;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationLAEREmission;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotificationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequestDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TIVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVAltScenarioPteReq;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicableReq;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVCompliance;
import us.oh.state.epa.stars2.database.dbObjects.application.TVComplianceObligations;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperatingScenario;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperationalRestriction;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUPollutantLimit;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedAltLimits;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedExemptions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedTestChanges;
import us.oh.state.epa.stars2.database.dbObjects.application.TVPteAdjustment;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeENG;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.AppEUEquipServiceTypeDef;
import us.oh.state.epa.stars2.def.ApplicationEUEmissionTableDef;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.MaterialUsedDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PAEuPteDeterminationBasisDef;
import us.oh.state.epa.stars2.def.PATvEuPteDeterminationBasisDef;
import us.oh.state.epa.stars2.def.PBRNotifDocTypeDef;
import us.oh.state.epa.stars2.def.PBRTypeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationEUPurposeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationFacilityTypeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationPurposeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationSageGrouseDef;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIORequestingFedLimitsDef;
import us.oh.state.epa.stars2.def.PTIORequestingFedLimitsReasonDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.RPCRequestDocTypeDef;
import us.oh.state.epa.stars2.def.RPCTypeDef;
import us.oh.state.epa.stars2.def.RPRReasonDef;
import us.oh.state.epa.stars2.def.RelocationTypeDef;
import us.oh.state.epa.stars2.def.RuleCitationDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.TVApplicationPurposeDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.def.TVRuleCiteTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.OpenDocumentUtil;
import us.oh.state.epa.stars2.webcommon.Stars2TreeNode;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentUpdateListener;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ContactService;
import us.wy.state.deq.impact.def.PAEuPTEUnitsDef;

public class ApplicationDetailCommon extends ValidationBase implements
		IAttachmentListener, IAttachmentUpdateListener {
	
	private static final long serialVersionUID = 4271311913120695522L;

	/*
	 * The following strings must match those used in the JSP's and JSF config
	 * dialogs.
	 */

	protected static final String DOCUMENT_DIALOG = "dialog:applicationDoc";
	protected static final String REQUIRED_DOCUMENT_DIALOG = "dialog:requiredApplicationDoc";
	protected static final String TS_DOC_DOWNLOAD_DIALOG = "dialog:appConfirmTSDownload";
	protected static final String NOTE_DIALOG = "dialog:appNoteDetail";
	protected static final String APP_SUBMIT_DIALOG = "dialog:appSubmitDetail";
	protected static final String PTIO_EMISSIONS_DIALOG = "dialog:appEmissionsInfo";
	protected static final String PTIO_EMISSIONS_FUGLEAKS_OILGAS_DIALOG = "dialog:appEmissionsFugLeaksOilGasInfo";
	protected static final String TV_EMISSIONS_DIALOG = "dialog:appTVEmissionsInfo";
	protected static final String ALT_SCENARIO_PTE_REQ_DIALOG = "dialog:tvAltScenarioPteReq";
	protected static final String APPLICABLE_REQ_DIALOG = "dialog:appTVAppReqInfo";
	protected static final String POLLUTANT_LIMIT_DIALOG = "dialog:appTVPollutantLimit";
	protected static final String FAC_WIDE_REQ_DIALOG = "dialog:tvAppFacWideReqDetail";
	protected static final String OPERATIONAL_RESTRICTION_DIALOG = "dialog:appTVOperationalRestriction";
	protected static final String BACT_POLLUTANT_DIALOG = "dialog:bactPollutantDetail";
	protected static final String LAER_POLLUTANT_DIALOG = "dialog:laerPollutantDetail";
	protected static final String APP_DOC_TYPE_EXP_DIALOG = "dialog:appDocTypeExplanations";
	protected static final String TV_APP_DOC_TYPE_EXP_DIALOG = "dialog:tvAppDocTypeExplanations";

	// managed beans
	protected static final String APPDETAIL_MENU_MANAGED_BEAN = "menuItem_appDetail";
	protected static final String REQ_MANAGED_BEAN = "req";
	protected static final String DOC_MANAGED_BEAN = "doc";
	protected static final String NOTE_MANAGED_BEAN = "notes";
	protected static final String CONFIRM_WINDOW_MANAGED_BEAN = "confirmWindow";
	protected static final String ACTIVITY_PROFILE_MANAGED_BEAN = "activityProfile";
	protected static final String APP_SEARCH_MANAGED_BEAN = "applicationSearch";
	protected static final String APP_DETAIL_MANAGED_BEAN = "applicationDetail";
	protected static final String FAC_WIDE_REQ_MANAGED_BEAN = "facWideReq";

	// tree nodes
	protected static final String APPLICATION_NODE_TYPE = "application";
	protected static final String EU_NODE_TYPE = "eu";
	protected static final String INSIG_EU_NODE_TYPE = "insignificantEU";
	protected static final String EXCLUDED_EU_NODE_TYPE = "excludedEU";
	protected static final String NOT_INCLUDABLE_EU_NODE_TYPE = "notIncludableEU";
	protected static final String ALT_SCENARIO_NODE_TYPE = "scenario";
	protected static final String EU_GROUP_NODE_TYPE = "euGroup";
	protected static final String EU_SHUTTLE_NODE_TYPE = "euShuttle";
	protected static final String EU_COPY_NODE_TYPE = "euCopy";

	// pollutant types
	protected final String POLLUTANT_CAP = "CAP";
	protected final String POLLUTANT_HAP_TAC = "HAP_TAC";
	protected final String POLLUTANT_SEC3 = "SEC3";
	protected final String POLLUTANT_GHG = "GHG";
	protected final String POLLUTANT_OTH = "OTH";

	/*
	 * The following ID's must match those used in ApplicationBO.validateXXX()
	 */
	protected static final String EU_REFERENCE_PREFIX = "eu:";
	protected static final String INSIG_EU_REFERENCE_PREFIX = "insignificantEU:";
	protected static final String EXCLUDED_EU_REFERENCE_PREFIX = "excludedEU:";
	protected static final String ALT_SCENARIO_REFERENCE_PREFIX = "scenario:";
	protected static final String APPLICATION_REFERENCE = "application";

	protected String pollutantType;

	protected TreeModelBase treeData;
	protected Stars2TreeNode selectedTreeNode;
	private Stars2TreeNode saveSelectedTreeNode;
	protected Application application;
	protected ApplicationEU selectedEU;
	protected PermitEU selectedPermitEU; // only applicable for PBRs
	protected HashMap<String, String> euModificationDesc = new HashMap<String, String>();
	protected TVEUOperatingScenario selectedScenario;
	protected boolean alternateScenario;
	protected EmissionUnit selectedExcludedEU;
	protected ApplicationEU euToCopy;
	protected ApplicationDocumentRef applicationDoc;
	protected boolean docUpdate;
	protected boolean usingExistingDoc;
	protected boolean editMode;
	protected boolean semiEditMode;
	protected UploadedFile fileToUpload;
	protected UploadedFileInfo publicFileInfo;
	protected UploadedFile tsFileToUpload;
	protected UploadedFileInfo tsFileInfo;
	protected boolean fromTODOList;
	protected Integer workflowActivityId;
	protected Integer workflowProcessId;
	protected ApplicationNote applicationNote;
	protected boolean noteModify;
	protected ApplicationEUEmissions emissions;
	protected ApplicationEUMaterialUsed materialUsed;
	protected ApplicationEUFugitiveLeaks applicationEUFugitiveLeaks;
	protected TVApplicationEUEmissions tvEmissions;
	protected Contact applicationContact;
	protected Contact createApplicationContact;
	protected boolean contactReadOnly;
	protected boolean newContact;
	protected boolean createNewContact;
	private boolean usingFacilityContact;
	private TableSorter facilityContactsWrapper;
	protected List<ContactUtil> facilityContacts;
	/**
	 * Objects needed to support subparts tables
	 */
	protected List<Stars2Object> nspsSubparts;
	protected List<Stars2Object> neshapSubparts;
	protected List<Stars2Object> mactSubparts;

	protected List<Stars2Object> tvNspsSubparts;
	protected List<Stars2Object> tvNeshapSubparts;
	protected List<Stars2Object> tvMactSubparts;
	protected List<Stars2Object> tvEuNspsSubparts;
	protected List<Stars2Object> tvEuNeshapSubparts;
	protected List<Stars2Object> tvEuMactSubparts;

	protected boolean emissionsModify;
	protected boolean emissionsDialogOpen;
	protected boolean perDueDateEditable = true;
	protected boolean applicationDeleted;
	protected ApplicationDocumentRef downloadDoc;
	protected LinkedList<ApplicationDocumentRef> applicationDocuments = new LinkedList<ApplicationDocumentRef>();

	protected List<String> nonEditablePtioApplicationPurposeCds = new ArrayList<String>();
	protected String ptioApplicationSageGrouseCd;
	protected HashSet<ApplicationEU> eusToRemove = new HashSet<ApplicationEU>();
	protected HashSet<Integer> eusToAdd = new HashSet<Integer>();

	protected String tvRuleCiteTypeCd;
	protected String tvRuleCiteCd;
	protected Object ruleCiteObject;

	protected List<TVApplicableReq> applicableRequirements;
	protected List<TVApplicableReq> stateOnlyRequirements;
	protected TVApplicableReq applicableReq;
	protected boolean appReqModify;
	protected String appReqTableId;
	protected String tvPollutantLimitsId;
	protected String tvOperationalRestrictionId;
	protected TVEUGroup selectedEUGroup;
	protected int euGroupNum = 0;
	protected List<Integer> eusTargetedForCopy = new ArrayList<Integer>();
	protected TVAltScenarioPteReq pteRequirement;

	protected String popupRedirectOutcome;

	private Permit permit;
	protected boolean readOnly;
	protected boolean hideTradeSecret;
	protected boolean tradeSecret;
	protected String tradeSecretValue;
	protected transient boolean tradeSecretStatusChanged = false;
	private boolean moreNodesSelected;

	protected List<Permit> associatedPermitList;
	private Integer relatedPermitId;
	protected boolean staging;
	private boolean viewDoc;
	private boolean deleteDocAllowed = true;
	private boolean editModeFacWideReq = false;
	protected List<ApplicationEU> eusAvailableForCopy;

	private List<Document> printableDocumentList;
	private List<Document> printableAttachmentList;
	private TableSorter bactPollutantWrapper;
	private TableSorter laerPollutantWrapper;
	private TableSorter facWideReqWrapper;
	private NSRApplicationBACTEmission selectedBACTEmission;
	private NSRApplicationLAEREmission selectedLAEREmission;
	private boolean editable1;

	protected boolean pollutantLimitModify;
	protected boolean newPollutantLimit;
	protected TVEUPollutantLimit pollutantLimit;
	private FacilityWideRequirement facWideReq;

	protected boolean operationalRestrictionModify;
	protected boolean newOperationalRestriction;
	protected TVEUOperationalRestriction operationalRestriction;

	private ContactService contactService;
	private FacilityService facilityService;
	private PermitService permitService;
	private ApplicationService applicationService;
	
	private String migrationTempFolder = DocumentUtil.getFileStoreRootPath();
	private String logFileName = "Application-migration-log.txt";
	private String logFilePath = migrationTempFolder + "\\" + logFileName;
	private String applicationCsvFilePath;
	private String euCsvFilePath;
	private PrintWriter printWriter;
	private boolean migrate=false;
	private String[] migratedData = null;
	

	private List<Document> printableDocumentAppRefEUs;	

	private boolean disableDeleteOnPopup;
	private String disableDeleteMsgOnPopup;
	
	public ApplicationDetailCommon() {
		// set tag to identify this class in the validateDlgAction method
		setTag(APPLICATION_TAG);
		facilityContactsWrapper = new TableSorter();
		bactPollutantWrapper = new TableSorter();
		laerPollutantWrapper = new TableSorter();
		facWideReqWrapper = new TableSorter();
		contactReadOnly = true;
		newContact = false;

	}
	
	public boolean isMigrate() {
		return migrate;
	}

	public void setMigrate(boolean migrate) {
		this.migrate = migrate;
	}
	
	public String getApplicationCsvFilePath() {
		return applicationCsvFilePath;
	}

	public void setApplicationCsvFilePath(String applicationCsvFilePath) {
		this.applicationCsvFilePath = applicationCsvFilePath;
	}
	
	public String getEuCsvFilePath() {
		return euCsvFilePath;
	}

	public void setEuCsvFilePath(String euCsvFilePath) {
		this.euCsvFilePath = euCsvFilePath;
	}

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public ApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
			FacesUtil.setOutcome(null, popupRedirectOutcome);
			popupRedirectOutcome = null;
		}
		return null;
	}

	public String goToCurrentWorkflow() {
		ActivityProfile activityProfile = (ActivityProfile) FacesUtil
				.getManagedBean(ACTIVITY_PROFILE_MANAGED_BEAN);
		activityProfile.setActivityTemplateId(workflowActivityId);
		activityProfile.setFromExternal(fromTODOList);
		activityProfile.submitProfile();
		return "currentWorkflow";
	}

	/*
	 * Backing bean properties getters/setters - common
	 */
	/**
	 * Set the Id of the application to be displayed and load it on the screen.
	 * Within the portal application, this method will always retrieve
	 * application data from the STAGING schema. If there is a need to view data
	 * from the READONLY schema, setApplicationNumber should be used instead.
	 * 
	 * @param applicationID
	 */
	public void setApplicationID(int applicationID) {
		reset();
		application = null; // clear old application
		// readOnly is set to true only for internal app and read only user
		readOnly = isInternalApp() && isReadOnlyUser();
		loadApplicationSummary(applicationID);
		if (!isPublicApp()) {
			((SimpleMenuItem) FacesUtil.getManagedBean(APPDETAIL_MENU_MANAGED_BEAN)).setDisabled(false);
		}
	}

	/**
	 * Set the application number of the application to be displayed and load it
	 * on the screen. Within the portal application, this method will always
	 * retrieve application data from the READONLY schema. If there is a need to
	 * view data from the STAGING schema, setApplicationID should be used
	 * instead.
	 * 
	 * @param applicationNumber
	 */
	public void setApplicationNumber(String applicationNumber) {
		logger.debug("Enter setApplicationNumber" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
		reset();
		application = null; // clear old application
		// loading application by number forces it to be read-only in the portal
		// but edits are allowed if application is running internally (unless
		// the user is read only.
		readOnly = !isInternalApp() || isReadOnlyUser();
		loadApplicationSummary(applicationNumber);
		if (!isPublicApp()) {
			((SimpleMenuItem) FacesUtil.getManagedBean(APPDETAIL_MENU_MANAGED_BEAN)).setDisabled(false);
		}
		logger.debug("Exit setApplicationNumber" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
	}

	public String submitApplicationDetail() {
		String ret = null;
		if (application != null) {
			ret = APP_DETAIL_MANAGED_BEAN;
		}
		return ret;
	}

	/*
	 * Clear out values that may have been carried over from a previous
	 * application displayed in UI
	 */
	protected void reset() {
		setSelectedEU(null);
		selectedPermitEU = null;
		selectedTreeNode = null;
		fromTODOList = false;
		applicationDeleted = false;
		permitInfo = null;
		associatedPermitList = null;
		resetApplicableReqTables();
	}

	protected void resetApplicableReqTables() {
		if (applicableReqTable != null
				&& applicableReqTable.getSelectionState() != null) {
			applicableReqTable.getSelectionState().clear();
		}
		if (stateOnlyReqTable != null
				&& stateOnlyReqTable.getSelectionState() != null) {
			stateOnlyReqTable.getSelectionState().clear();
		}
	}

	protected void resetTvApplicationSubparts(TVApplication tvApp) {
		tvNspsSubparts = Stars2Object
				.toStar2Object(tvApp.getNspsSubpartCodes());
		tvNeshapSubparts = Stars2Object.toStar2Object(tvApp
				.getNeshapSubpartCodes());
		tvMactSubparts = Stars2Object
				.toStar2Object(tvApp.getMactSubpartCodes());
	}

	public int newApplication(Application app) {
		int ret = 0;
		try {
			application = getApplicationService().createApplication(app);

			// if PER Due date was set in createApplication,
			// don't allow the user to change it
			if (application instanceof PTIOApplication
					&& ((PTIOApplication) application)
							.getRequestedPERDueDateCD() != null) {
				perDueDateEditable = false;
			}

			// copy data from application to local variables as needed
			reset();
			loadApplicationSummary(application.getApplicationID());
			
			readOnly = false;
			enterEditMode();
			DisplayUtil.displayInfo("New Application is created successfully.");
		} catch (RemoteException e) {
			handleException(
					"Exception for application " + app.getApplicationNumber(),
					e);
			ret = -1;
		}
		return ret; // success
	}

	public int newApplication(Class<? extends Application> applicationClass,
			Facility facility) {
		int ret = 0;
		try {
			application = applicationClass.newInstance();
			if (application instanceof PBRNotification) {
				((PBRNotification) application).setPbrTypeCd(pbrTypeCd);
			} else if (application instanceof RPRRequest) {
				((RPRRequest) application).setPermitId(permitId);
				if (fromPBR)
					((RPRRequest) application).setRprReasonCd(RPRReasonDef.PBR);
				fromPBR = false;
			} else if (application instanceof RPERequest) {
				((RPERequest) application).setPermitId(permitId);
				permit = getPermitService().retrievePermit(permitId)
						.getPermit();
				GregorianCalendar tdgc = new GregorianCalendar();
				tdgc.setTime(permit.getEffectiveDate());
				tdgc.add(Calendar.MONTH, 30);
				Timestamp td = new Timestamp(tdgc.getTimeInMillis());
				((RPERequest) application).setTerminationDate(td);
			}
			application.setFacility(facility);

			// need to create application in database to get an application id
			application = getApplicationService()
					.createApplication(application);

			// if PER Due date was set in createApplication,
			// don't allow the user to change it
			if (application instanceof PTIOApplication
					&& ((PTIOApplication) application)
							.getRequestedPERDueDateCD() != null) {
				perDueDateEditable = false;
			}

			// copy data from application to local variables as needed
			reset();
			loadApplicationData();

			createTree();
			readOnly = false;
			enterEditMode();

		} catch (RemoteException ex) {
			handleException(
					"Exception for facility " + facility.getFacilityId(), ex);
			ret = -1;
		} catch (InstantiationException e) {
			DisplayUtil
					.displayError("A system error has occurred. Please contact System Administrator.");
			logger.error("Exception for facility " + facility.getFacilityId(),
					e);
			ret = -1;
		} catch (IllegalAccessException e) {
			DisplayUtil
					.displayError("A system error has occurred. Please contact System Administrator.");
			logger.error("Exception for facility " + facility.getFacilityId(),
					e);
			ret = -1;
		}
		return ret; // success
	}

	private Contact createContact() {
		Contact contact = new Contact();
		contact.setAddress(new Address());
		// force country to USA
		contact.getAddress().setCountryCd("US");
		return contact;
	}

	public TreeModelBase getTreeData() {
		return treeData;
	}

	public Stars2TreeNode getselectedTreeNode() {
		return selectedTreeNode;
	}

	public void setselectedTreeNode(Stars2TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

	public Application getApplication() {
		//Logger.debug(" getPublicDocURL - editMode: "+this.editMode);
		//Logger.debug("this.migrate: "+this.migrate);
		//if(this.migrate){
		//	this.editMode=true;
		//}
		//Logger.debug("editMode: "+this.editMode);
		return application;
	}

	public ApplicationEU getSelectedEU() {
		return selectedEU;
	}

	public EmissionUnit getSelectedExcludedEU() {
		return selectedExcludedEU;
	}

	@Override
	public boolean getEditMode() {
		return editMode;
	}

	@Override
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public UploadedFile getFileToUpload() {
		return fileToUpload;
	}

	public void setFileToUpload(UploadedFile fileToUpload) {
		this.fileToUpload = fileToUpload;
	}

	public ApplicationDocumentRef getApplicationDoc() {
		return this.applicationDoc;
	}
	
	public List<Document> getPrintableDocumentAppRefEUs() {
		return printableDocumentAppRefEUs;
	}

	public void setPrintableDocumentAppRefEUs(List<Document> printableDocumentAppRefEUs) {
		this.printableDocumentAppRefEUs = printableDocumentAppRefEUs;
	}

	public boolean getEditAllowed() {
		if (isPublicApp()) {
			return false;
		}
		// admin is ALWAYS allowed to edit
		return isStars2Admin()
				|| (!readOnly && !applicationDeleted && (application
						.getSubmittedDate() == null
						|| (application instanceof RPRRequest && ((RPRRequest) application)
								.getDispositionFlag() == null)
						|| (application instanceof RPERequest && ((RPERequest) application)
								.getDispositionFlag() == null) || (application instanceof PBRNotification && PBRNotification.RECEIVED
						.equals(((PBRNotification) application)
								.getDispositionFlag()))));
	}

	public boolean getValidateAllowed() {
		return !readOnly && !applicationDeleted
				&& application.getSubmittedDate() == null;
	}

	public boolean getSubmitAllowed() {
		return !readOnly && !applicationDeleted && application.getValidated()
				&& application.getSubmittedDate() == null;
	}

	public boolean getDeleteAllowed() {
		return !readOnly && !applicationDeleted
				&& application.getSubmittedDate() == null;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	/*
	 * Backing bean properties getters/setters - RPC, RPR, RPE
	 */
	public String getPermitNumber() {
		String ret = "";
		if (application.getReferencedPermits() != null
				&& application.getReferencedPermits().size() > 0) {
			ret = application.getReferencedPermits().get(0).getPermitNumber();
		}
		return ret;
	}

	public void setPermitNumber(String permitNumber) {
		if (!getPermitNumber().equals(permitNumber)) {
			if (permitNumber == null || permitNumber.length() == 0) {
				if (application.getReferencedPermits() != null) {
					application.getReferencedPermits().clear();
				}
			} else {
				if (application.getReferencedPermits() == null) {
					application.setReferencedPermits(new ArrayList<Permit>());
				} else {
					application.getReferencedPermits().clear();
				}
				Permit referencedPermit = getReferencedPermitToAdd(permitNumber);
				if (referencedPermit != null) {
					application.getReferencedPermits().add(referencedPermit);
				}
			}
		}
	}

	public List<Permit> getAssociatedPermitsList() {
		if (associatedPermitList == null) {
			associatedPermitList = new ArrayList<Permit>();
			if (application instanceof RPCRequest) {
				try {
					PermitService permitBO = getPermitService();
					RPCRequest req = (RPCRequest) application;
					if (req.getPermitId() != null) {
						PermitInfo pi = permitBO.retrievePermit(req
								.getPermitId());
						if (pi != null) {
							associatedPermitList.add(pi.getPermit());
						}
					}
				} catch (RemoteException e) {
					handleException(
							"Exception for application "
									+ application.getApplicationNumber(), e);
				}
			} else if (application instanceof RPERequest) {
				try {
					PermitService permitBO = getPermitService();
					RPERequest req = (RPERequest) application;
					if (req.getPermitId() != null) {
						PermitInfo pi = permitBO.retrievePermit(req
								.getPermitId());
						if (pi != null) {
							associatedPermitList.add(pi.getPermit());
						}
					}
				} catch (RemoteException e) {
					handleException(
							"Exception for application "
									+ application.getApplicationNumber(), e);
				}
			} else if (application instanceof RPRRequest) {
				try {
					PermitService permitBO = getPermitService();
					RPRRequest req = (RPRRequest) application;
					if (req.getPermitId() != null) {
						PermitInfo pi = permitBO.retrievePermit(req
								.getPermitId());
						if (pi != null) {
							associatedPermitList.add(pi.getPermit());
						}
					}
				} catch (RemoteException e) {
					handleException(
							"Exception for application "
									+ application.getApplicationNumber(), e);
				}
			}
			// all types of applications may have a permit specified in the
			// pt_permit_application_xref table. The code below
			// retrieves those permits
			try {
				Permit[] permits = getApplicationService()
						.retrievePermitsForApplication(
								application.getApplicationID());
				for (Permit permit : permits) {
					associatedPermitList.add(permit);
				}
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
		}
		return associatedPermitList;
	}

	/*
	 * Backing bean properties getters/setters - PTIO
	 */
	public String getPtioEUPurposeCD() {
		String code = null;
		if (selectedEU instanceof PTIOApplicationEU) {
			code = ((PTIOApplicationEU) selectedEU).getPtioEUPurposeCD();
		}
		return code;
	}

	public void setPtioEUPurposeCD(String ptioEUPurposeCD) {
		if (ptioEUPurposeCD != null && selectedEU instanceof PTIOApplicationEU) {
			((PTIOApplicationEU) selectedEU)
					.setPtioEUPurposeCD(ptioEUPurposeCD);
		}
	}

	public List<String> getPtioEUFederalLimitsReasonCDs() {
		List<String> codes = null;
		if (selectedEU instanceof PTIOApplicationEU) {
			codes = ((PTIOApplicationEU) selectedEU)
					.getFederalLimitsReasonCDs();
		} else {
			codes = new ArrayList<String>();
		}
		return codes;
	}

	public void setPtioEUFederalLimitsReasonCDs(
			List<String> federalLimitsReasonCDs) {
		if (selectedEU instanceof PTIOApplicationEU) {
			((PTIOApplicationEU) selectedEU)
					.setFederalLimitsReasonCDs(federalLimitsReasonCDs == null ? new ArrayList<String>()
							: federalLimitsReasonCDs);
		}
	}

	public boolean getAppPurposeCDsContainOther() {
		return nonEditablePtioApplicationPurposeCds
				.contains(PTIOApplicationPurposeDef.OTHER);
	}

	public boolean getEuPurposeCDContainsModification() {
		boolean ret = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			ret = PTIOApplicationEUPurposeDef.MODIFICATION
					.equals(((PTIOApplicationEU) selectedEU)
							.getPtioEUPurposeCD());
		}
		return ret;
	}

	/*
	 * public boolean getEuPurposeCDContainsModificationNotBegun() { boolean ret
	 * = false; if (selectedEU instanceof PTIOApplicationEU) { ret =
	 * PTIOApplicationEUPurposeDef.MODIFICATION_NOT_BEGUN
	 * .equals(((PTIOApplicationEU) selectedEU) .getPtioEUPurposeCD()); } return
	 * ret; }
	 */

	public boolean getEuPurposeCDsContainGeneralPermit() {
		boolean ret = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			ret = ((PTIOApplicationEU) selectedEU).isGeneralPermit();
		}
		return ret;
	}

	public boolean getEuPurposeCDsContainReconstruction() {
		boolean ret = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			ret = ((PTIOApplicationEU) selectedEU).getPtioEUPurposeCDs()
					.contains(PTIOApplicationEUPurposeDef.RECONSTRUCTION);
		}
		return ret;
	}

	public boolean getEuPurposeCDsContainOther() {
		boolean ret = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			ret = ((PTIOApplicationEU) selectedEU).getPtioEUPurposeCDs()
					.contains(PTIOApplicationEUPurposeDef.OTHER);
		}
		return ret;
	}

	public boolean getEuPurposeCDsRequireWorkDate() {
		boolean ret = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			List<String> purposeCds = ((PTIOApplicationEU) selectedEU)
					.getPtioEUPurposeCDs();
			ret = (purposeCds
					.contains(PTIOApplicationEUPurposeDef.CONSTRUCTION) && FacilityTypeDef
					.isOilAndGas(application.getFacility().getFacilityTypeCd()))
					|| purposeCds
							.contains(PTIOApplicationEUPurposeDef.MODIFICATION);
		}
		return ret;
	}

	public boolean getEuPurposeCDsRequireOpBeginDate() {
		boolean ret = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			List<String> purposeCds = ((PTIOApplicationEU) selectedEU)
					.getPtioEUPurposeCDs();
			ret = purposeCds.contains(PTIOApplicationEUPurposeDef.class)
					|| purposeCds
							.contains(PTIOApplicationEUPurposeDef.MODIFICATION);
		}
		return ret;
	}

	public boolean getEuPurposeCDsRequireAfterPermitFlag() {
		boolean ret = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			List<String> purposeCds = ((PTIOApplicationEU) selectedEU)
					.getPtioEUPurposeCDs();
			ret = purposeCds.contains(PTIOApplicationEUPurposeDef.CONSTRUCTION)
					&& FacilityTypeDef.isOilAndGas(application.getFacility()
							.getFacilityTypeCd());
		}
		return ret;
	}

	public String getEuPurposeCDWorkStartLabel() {
		String label = null;
		if (selectedEU instanceof PTIOApplicationEU) {
			List<String> purposeCds = ((PTIOApplicationEU) selectedEU)
					.getPtioEUPurposeCDs();
			if (purposeCds.contains(PTIOApplicationEUPurposeDef.CONSTRUCTION)) {
				label = "Date production began: ";
			} else if (purposeCds
					.contains(PTIOApplicationEUPurposeDef.MODIFICATION)) {
				label = "When will you begin to modify the air contaminant source? ";
			}
		}
		return label;
	}

	public String getEuPurposeCDOpBeganDateLabel() {
		String label = "Date operation began : ";
		if (selectedEU instanceof PTIOApplicationEU) {
			List<String> purposeCds = ((PTIOApplicationEU) selectedEU)
					.getPtioEUPurposeCDs();
			if (purposeCds.contains(PTIOApplicationEUPurposeDef.RECONSTRUCTION)) {
				label = "Identify the date operation began after installation or latest modification : ";
			}
		}
		return label;
	}

	public List<SelectItem> getModelGeneralPermitDefs() {
		List<SelectItem> si = new ArrayList<SelectItem>();
		String typeCd = null;
		if (selectedEU instanceof PTIOApplicationEU) {
			typeCd = ((PTIOApplicationEU) selectedEU).getGeneralPermitTypeCd();
		}
		if (typeCd != null)
			try {
				SimpleDef[] defs = getPermitService()
						.retrieveModelGeneralPermitDefs(typeCd);
				for (SimpleDef s : defs) {
					SelectItem item = new SelectItem(s.getCode(),
							s.getDescription());
					item.setDisabled(s.isDeprecated());
					si.add(item);
				}
			} catch (RemoteException e) {
				handleException(e);
			}
		return si;
	}

	public boolean getEuFedLimitsReasonCDsContainOther() {
		boolean ret = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			ret = ((PTIOApplicationEU) selectedEU).getFederalLimitsReasonCDs()
					.contains(PTIORequestingFedLimitsReasonDef.OTHER);
		}
		return ret;
	}

	/*
	 * Backing bean properties getters/setters - TV
	 */
	public boolean isPermitReasonSelectable() {
		return ((application instanceof TVApplication && TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING
				.equals(((TVApplication) application)
						.getTvApplicationPurposeCd())) || (application instanceof TIVApplication && TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING
				.equals(((TIVApplication) application).getAppPurposeCd())));
	}

	public boolean isPteSelectable() {
		return application instanceof TVApplication
				&& (TVApplicationPurposeDef.INITIAL_APPLICATION
						.equals(((TVApplication) application)
								.getTvApplicationPurposeCd()) || TVApplicationPurposeDef.RENEWAL
						.equals(((TVApplication) application)
								.getTvApplicationPurposeCd()));
	}
	
	public boolean isPermitReasonReopeningSelectable() {
		return isPermitReasonSelectable() && PermitReasonsDef.REOPENING.equals(((TVApplication) application).getPermitReasonCd());
	}
	
	public boolean isPermitReasonSPMSelectable() {
		return isPermitReasonSelectable() && PermitReasonsDef.SPM.equals(((TVApplication) application).getPermitReasonCd());
	}
	
	/*
	 * Actions - common
	 */
	public String nodeClicked() {
		if (selectedTreeNode.getType().equals("moreNodes")) {
			selectedTreeNode = saveSelectedTreeNode;
			moreNodesSelected = true;
		}

		// set internal variables to proper values based on type of node
		// selected
		selectedScenario = null;
		alternateScenario = false;
		setSelectedEU(null);
		selectedPermitEU = null;
		selectedExcludedEU = null;
		selectedEUGroup = null;
		bactPollutantWrapper = new TableSorter();
		bactPollutantWrapper.clearWrappedData();
		laerPollutantWrapper = new TableSorter();
		laerPollutantWrapper.clearWrappedData();
		facWideReqWrapper = new TableSorter();
		bactPollutantWrapper
				.setWrappedData(new ArrayList<NSRApplicationBACTEmission>());
		laerPollutantWrapper
				.setWrappedData(new ArrayList<NSRApplicationLAEREmission>());

		if(selectedTreeNode != null) {
			if (selectedTreeNode.getType().equals(APPLICATION_NODE_TYPE)) {
				// reload application to get the latest data
				reloadApplicationSummary();
				if (application instanceof TVApplication) {
					TVApplication tvApp = (TVApplication) application;
					applicableRequirements = tvApp.getApplicableRequirements();
					stateOnlyRequirements = tvApp.getStateOnlyRequirements();
					facWideReqWrapper.setWrappedData(tvApp
							.getFacilityWideRequirements());
				}
			} else if (selectedTreeNode.getType().equals(EU_NODE_TYPE)) {
				setSelectedEU((ApplicationEU) selectedTreeNode.getUserObject());
				reloadApplicationWithSelectedEU();
				if (selectedEU instanceof TVApplicationEU) {
					// retrieve subparts
					TVApplicationEU tvAppEu = (TVApplicationEU) selectedEU;
					tvEuNspsSubparts = Stars2Object.toStar2Object(tvAppEu
							.getNspsSubpartCodes());
					tvEuNeshapSubparts = Stars2Object.toStar2Object(tvAppEu
							.getNeshapSubpartCodes());
					tvEuMactSubparts = Stars2Object.toStar2Object(tvAppEu
							.getMactSubpartCodes());
	
					// set selectedScenario to normal scenario by default
					selectedScenario = ((TVApplicationEU) selectedEU)
							.getNormalOperatingScenario();
					applicableRequirements = ((TVApplicationEU) selectedEU)
							.getApplicableRequirements();
					stateOnlyRequirements = ((TVApplicationEU) selectedEU)
							.getStateOnlyRequirements();
				} else if (selectedEU instanceof PTIOApplicationEU) {
					// retrieve subparts
					PTIOApplicationEU ptioAppEu = (PTIOApplicationEU) selectedEU;
					nspsSubparts = Stars2Object.toStar2Object(ptioAppEu
							.getNspsSubpartCodes());
					neshapSubparts = Stars2Object.toStar2Object(ptioAppEu
							.getNeshapSubpartCodes());
					mactSubparts = Stars2Object.toStar2Object(ptioAppEu
							.getMactSubpartCodes());
	
					// setup BACT emissions
					List<NSRApplicationBACTEmission> bactEmissions = new ArrayList<NSRApplicationBACTEmission>();
					bactEmissions = ((PTIOApplicationEU) selectedEU)
							.getBactEmissions();
					bactPollutantWrapper.setWrappedData(bactEmissions);
	
					// setup LAER emissions
					List<NSRApplicationLAEREmission> laerEmissions = new ArrayList<NSRApplicationLAEREmission>();
					laerEmissions = ((PTIOApplicationEU) selectedEU)
							.getLaerEmissions();
					laerPollutantWrapper.setWrappedData(laerEmissions);
	
					euModificationDesc.clear();
					String currentEUPurposeCd = ((PTIOApplicationEU) selectedEU)
							.getPtioEUPurposeCD();
					if (currentEUPurposeCd != null) {
						euModificationDesc.put(currentEUPurposeCd,
								((PTIOApplicationEU) selectedEU)
										.getModificationDesc());
					}
				}
			} else if (selectedTreeNode.getType().equals(INSIG_EU_NODE_TYPE)) {
				setSelectedEU((ApplicationEU) selectedTreeNode.getUserObject());
				reloadApplicationWithSelectedEU();
				if (selectedEU instanceof TVApplicationEU) {
					// set selectedScenario to normal scenario by default
					selectedScenario = ((TVApplicationEU) selectedEU)
							.getNormalOperatingScenario();
				}
			} else if (selectedTreeNode.getType().equals(EXCLUDED_EU_NODE_TYPE)) {
				selectedExcludedEU = (EmissionUnit) selectedTreeNode
						.getUserObject();
			} else if (selectedTreeNode.getType().equals(
					NOT_INCLUDABLE_EU_NODE_TYPE)) {
				setSelectedEU((ApplicationEU) selectedTreeNode.getUserObject());
				reloadApplicationWithSelectedEU();
			} else if (selectedTreeNode.getType().equals(ALT_SCENARIO_NODE_TYPE)) {
				alternateScenario = true;
				selectedScenario = (TVEUOperatingScenario) selectedTreeNode
						.getUserObject();
				// find EU Node with which scenario is associated
				for (ApplicationEU eu : application.getEus()) {
					if (eu.getApplicationEuId().equals(
							selectedScenario.getApplicationEuId())) {
						setSelectedEU(eu);
						break;
					}
				}
			} else if (selectedTreeNode.getType().equals(EU_GROUP_NODE_TYPE)) {
				selectedEUGroup = (TVEUGroup) selectedTreeNode.getUserObject();
				applicableRequirements = selectedEUGroup
						.getApplicableRequirements();
				stateOnlyRequirements = selectedEUGroup.getStateOnlyRequirements();
			}
	
			saveSelectedTreeNode = selectedTreeNode;
			moreNodesSelected = false;
		} else {
			// this should rarely happen. Say, after excluding an eu from the application, the user
			// navigates to the facility inventory and changes the operating status of the eu to invalid.
			// and then accesses the application detail page via Applications-->Application Detail.
			// Trying to click on the excluded eu blows up because it is no longer accessible.
			DisplayUtil.displayInfo("It appears the node your are trying to access is no longer available." +
					" You are trying to access old data." );
		}

		return null; // stay on same page
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.webcommon.ValidationBase#getValidationDlgReference
	 * ()
	 */
	public String getValidationDlgReference() {
		String ret = null;
		if (selectedTreeNode != null) {
			if (selectedTreeNode.getType().equals(EU_NODE_TYPE)) {
				ret = EU_REFERENCE_PREFIX
						+ ((ApplicationEU) selectedTreeNode.getUserObject())
								.getApplicationEuId();
			} else if (selectedTreeNode.getType().equals(INSIG_EU_NODE_TYPE)) {
				ret = INSIG_EU_REFERENCE_PREFIX
						+ ((ApplicationEU) selectedTreeNode.getUserObject())
								.getApplicationEuId();
			} else if (selectedTreeNode.getType().equals(EXCLUDED_EU_NODE_TYPE)) {
				ret = EXCLUDED_EU_REFERENCE_PREFIX
						+ ((EmissionUnit) selectedTreeNode.getUserObject())
								.getCorrEpaEmuId();
			} else if (selectedTreeNode.getType()
					.equals(ALT_SCENARIO_NODE_TYPE)) {
				TVEUOperatingScenario scenario = (TVEUOperatingScenario) selectedTreeNode
						.getUserObject();
				ret = ALT_SCENARIO_REFERENCE_PREFIX
						+ scenario.getApplicationEuId() + ":"
						+ scenario.getTvEuOperatingScenarioId();
			} else {
				ret = APPLICATION_REFERENCE;
			}
		} else {
			ret = APPLICATION_REFERENCE;
		}

		ret += ":" + FacesUtil.getCurrentPage();

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.webcommon.ValidationBase#validationDlgAction()
	 */
	public String validationDlgAction() {
		String ret = super.validationDlgAction();
		if (null != ret)
			return ret;
		if (newValidationDlgReference == null
				|| newValidationDlgReference
						.equals(getValidationDlgReference())) {
			enterEditMode();
			return null; // stay on same page
		}

		// super class will return null if reference is within the application
		// search for node corresponding to item selected from application
		// dialog if the item from the dialog is within the application
		// but not already selected
		if (ret == null) {
			// find appropriate node in tree to match the reference from the
			// dialog

			StringTokenizer st = new StringTokenizer(newValidationDlgReference,
					":");

			String subsystem = st.nextToken();
			boolean isRefAppDetail = newValidationDlgReference.contains("appDetail");
			if (!subsystem.equals(ValidationBase.APPLICATION_TAG)) {
				logger.error("Application reference is in error: "
						+ newValidationDlgReference);
				DisplayUtil.displayError("Error processing validation message");
				return null;
			}

			String appID = st.nextToken();
			if (!appID.equals(this.application.getApplicationID().toString())) {
				setApplicationID(Integer.parseInt(appID));
			}

			// set selectedTreeNode to null to re-create the entire EU tree so that navigation
			// to a EU node which is above the currently selected EU node does not result 
			// in an error (TFS task - 4547)
			selectedTreeNode = null;
			loadApplicationSummary();
			newValidationDlgReference = newValidationDlgReference
					.substring(subsystem.length() + appID.length() + 2);

			Stars2TreeNode referenceNode = null;
			if (newValidationDlgReference == null
					|| newValidationDlgReference.equals(APPLICATION_REFERENCE)
					|| newValidationDlgReference.equalsIgnoreCase("appDetail")) {
				selectedTreeNode = (Stars2TreeNode) treeData.getNodeById("0"); // root
			} else {
				String nodeType = null;
				String nodeID = null;
				if (newValidationDlgReference.startsWith(EU_REFERENCE_PREFIX)) {
					nodeID = newValidationDlgReference
							.substring(EU_REFERENCE_PREFIX.length());
					nodeType = EU_NODE_TYPE;
				} else if (newValidationDlgReference
						.startsWith(INSIG_EU_REFERENCE_PREFIX)) {
					nodeID = newValidationDlgReference
							.substring(INSIG_EU_REFERENCE_PREFIX.length());
					nodeType = INSIG_EU_NODE_TYPE;
				} else if (newValidationDlgReference
						.startsWith(EXCLUDED_EU_REFERENCE_PREFIX)) {
					nodeID = newValidationDlgReference
							.substring(EXCLUDED_EU_REFERENCE_PREFIX.length());
					nodeType = EXCLUDED_EU_NODE_TYPE;
				} else if (newValidationDlgReference
						.startsWith(ALT_SCENARIO_REFERENCE_PREFIX)) {
					nodeID = newValidationDlgReference
							.substring(ALT_SCENARIO_REFERENCE_PREFIX.length());
					nodeType = ALT_SCENARIO_NODE_TYPE;
				} else {
					DisplayUtil
							.displayError("Unable to navigate to selected location.");
					nodeType = null;
				}
				referenceNode = (Stars2TreeNode) treeData.getNodeById("0");
				referenceNode = referenceNode.findNode(nodeType, nodeID);
				if (referenceNode == null) {
					DisplayUtil
							.displayError("Unable to navigate to selected location.");
				}
				selectedTreeNode = referenceNode;
			}
			// update selected node in tree and enter edit mode
			if (selectedTreeNode != null) {
				ret = nodeClicked();
				ret = APP_DETAIL_MANAGED_BEAN;
				if(!isRefAppDetail) {
					enterEditMode();
				}
			}
		}
		return ret;
	}

	public String enterEditMode() {
		editMode = true;
		setMigrate(false);

		if (selectedEU != null) {
			reloadWithSelectedEU();
		}

		return null; // stay on same page
	}
	
	public String loadExcelData(){
		return "dialog:uploadExcelData";
	}

	public String updateApplication() {
		//setMigrate(false);
	    DisplayUtil.clearQueuedMessages();
		if (this.application instanceof PTIOApplication) {
			String containerId = "";
			containerId = "ThePage:ptioFacility:";
			if (((PTIOApplication)application).isLegacyStatePTOApp() && ((PTIOApplication)application).isKnownIncompleteNSRApp()) {
				List<ValidationMessage> validationaMessages = new ArrayList<ValidationMessage>();
				ValidationMessage validResult = new ValidationMessage(
						"knownIncompleteNSRApp",
						"NSR Application cannot be both legacy and known incomplete.  Please set only one of these options to Yes.",
						ValidationMessage.Severity.ERROR, "knownIncompleteNSRApp");

				validationaMessages.add(validResult);
				displayValidationMessages(containerId,
						validationaMessages.toArray(new ValidationMessage[0]));
				return null;
			}
		}
		
		if (!application.isLegacy() && !application.isKnownIncomplete()) {
			if (!checkApplicationContents()) {
				return null;
			}

				if (!validateApplicationRequiredFields()) {
					if(!this.migrate){
						return null;
					}else{
						editMode=false;
						semiEditMode = false;
					}
				}			
		}
		
		if (!validateApplicationSubparts()) {
			return null;
		}
		
		if (getEditAllowed() || isSemiEditAllowed()) {
			try {
				// set validated to false since changes may have
				// invalidated the application
				// Note: Admin changes to submitted app should not alter
				// validated flag
				// Also, the app should not be invalidated if in semiedit mode
				if (!isSemiEditMode() && (!isStars2Admin() || application.getSubmittedDate() == null)) {
					application.setValidated(false);
				}

				// create contact info in database if it was previously missing
				if (contactInfoProvided() && !usingFacilityContact) {
					// add required fields as necessary
					List<ContactType> contactTypes = new ArrayList<ContactType>();
					ContactType appContactType = new ContactType();
					appContactType.setFacilityId(application.getFacilityId());
					appContactType.setStartDate(Utility.getToday());
					appContactType.setContactTypeCd(ContactTypeDef.PERM);
					contactTypes.add(appContactType);
					applicationContact.setContactTypes(contactTypes);

					if (!isInternalApp()) {
						applicationContact.setFacilityId(application
								.getFacilityId());

						if (application.getContact() == null) {
							getInfrastructureService().createContact(
									applicationContact);
						} else {
							getContactService().modifyContact(
									applicationContact);
						}
					} else {
						if (application.getContact() == null) {
							getInfrastructureService().createContact(
									applicationContact);
						} else {
							getContactService().modifyContact(
									applicationContact);
						}
					}
				}
				application.setContact(applicationContact);

				// for RPCRequest, clear list of EUs if permit id has changed
				if (application instanceof RPCRequest) {
					Integer permitId = ((RPCRequest) application).getPermitId();
					if (permitInfo != null
							&& permitId != null
							&& !permitInfo.getPermit().getPermitID()
									.equals(permitId)) {
						application.clearEus();
					}
				}

				// for submitted PBR, modify the permit too.
				if (application instanceof PBRNotification
						&& application.getSubmittedDate() != null
						&& permit != null && permit.getPermitID() != null) {
					getPermitService().modifyPermit(permit, getUserID());
				}

				if (application instanceof TVApplication) {
					TVApplication tvApp = (TVApplication) application;
					calculateTVCo2Equivalents(tvApp);
					setTvSubpartCDs(tvApp);
				}
				boolean modifyEUs = false;
				getApplicationService().modifyApplication(application, modifyEUs);

				if (application instanceof PTIOApplication) {
					if (!((PTIOApplication) application).isLegacyStatePTOApp() && !((PTIOApplication) application).isKnownIncompleteNSRApp())
						verifyNSRAppRequiredAttachments(application);
				} else if (application instanceof TVApplication) {
					if (!((TVApplication) application).isLegacyStateTVApp())
						verifyTVAppRequiredAttachments(application);
				}

				DisplayUtil.displayInfo("Update successful");
				contactReadOnly = true;
				leaveEditMode();
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			} finally {
				reloadApplicationSummary();
			}
		} else {
			DisplayUtil
					.displayError("You are not authorized to modify this application");
		}
		return null; // stay on same page
	}

	private void verifyTVAppRequiredAttachments(Application app) {
		TVApplication tvApp = (TVApplication) app;

		try {
			ApplicationService appBO = getApplicationService();
			appBO.verifyTVAppRequiredAttachments(tvApp);

		} catch (Exception e) {
			DisplayUtil
					.displayError("There is an error when the system was processing the generation attachment. Error: "
							+ e.getMessage());
			logger.error(e);
		}

		loadApplicationData();
	}

	private void verifyTVAppAttachments(Application app) {
		TVApplication tvApp = (TVApplication) app;

		try {
			ApplicationService appBO = getApplicationService();
			appBO.verifyTVAppRequiredAttachments(tvApp);

		} catch (Exception e) {
			DisplayUtil
					.displayError("There is an error when the system was processing the generation attachment. Error: "
							+ e.getMessage());
			logger.error(e);
		}
	}

	private void verifyTVAppEURequiredAttachments(Application app,
			ApplicationEU appEU) {
		TVApplication tvApp = (TVApplication) app;
		TVApplicationEU tvAppEU = (TVApplicationEU) appEU;

		try {
			ApplicationService appBO = getApplicationService();
			appBO.verifyTVAppEURequiredAttachments(tvApp, tvAppEU);

		} catch (Exception e) {
			DisplayUtil
					.displayError("There is an error when the system was processing the generation attachment. Error: "
							+ e.getMessage());
			logger.error(e);
		}

		loadApplicationData();
	}

	private void verifyTVAppEUAttachments(Application app, ApplicationEU appEU) {
		TVApplication tvApp = (TVApplication) app;
		TVApplicationEU tvAppEU = (TVApplicationEU) appEU;

		try {
			ApplicationService appBO = getApplicationService();
			appBO.verifyTVAppEURequiredAttachments(tvApp, tvAppEU);

		} catch (Exception e) {
			DisplayUtil
					.displayError("There is an error when the system was processing the generation attachment. Error: "
							+ e.getMessage());
			logger.error(e);
		}
	}

	private void verifyNSRAppRequiredAttachments(Application app) {
		PTIOApplication ptioApp = (PTIOApplication) app;

		try {
			ApplicationService appBO = getApplicationService();
			appBO.verifyNSRAppRequiredAttachments(ptioApp);

		} catch (Exception e) {
			DisplayUtil
					.displayError("There is an error when the system was processing the generation attachment. Error: "
							+ e.getMessage());
			logger.error(e);
		}

		loadApplicationData();
	}

	private void verifyNSRAppAttachments(Application app) {
		PTIOApplication ptioApp = (PTIOApplication) app;

		try {
			ApplicationService appBO = getApplicationService();
			appBO.verifyNSRAppRequiredAttachments(ptioApp);

		} catch (Exception e) {
			DisplayUtil
					.displayError("There is an error when the system was processing the generation attachment. Error: "
							+ e.getMessage());
			logger.error(e);
		}
	}

	private void verifyNSRAppEURequiredAttachments(Application app,
			ApplicationEU appEU) {
		PTIOApplication ptioApp = (PTIOApplication) app;
		PTIOApplicationEU ptioAppEU = (PTIOApplicationEU) appEU;

		try {
			ApplicationService appBO = getApplicationService();
			appBO.verifyNSRAppEURequiredAttachments(ptioApp, ptioAppEU);

		} catch (Exception e) {
			DisplayUtil
					.displayError("There is an error when the system was processing the generation attachment. Error: "
							+ e.getMessage());
			logger.error(e);
		}

		loadApplicationData();
	}

	private void verifyNSRAppEUAttachments(Application app, ApplicationEU appEU) {
		PTIOApplication ptioApp = (PTIOApplication) app;
		PTIOApplicationEU ptioAppEU = (PTIOApplicationEU) appEU;

		try {
			ApplicationService appBO = getApplicationService();
			appBO.verifyNSRAppEURequiredAttachments(ptioApp, ptioAppEU);

		} catch (Exception e) {
			DisplayUtil
					.displayError("There is an error when the system was processing the generation attachment. Error: "
							+ e.getMessage());
			logger.error(e);
		}
	}

	private void calculateTVCo2Equivalents(TVApplication app) {
		List<TVPteAdjustment> totals = app.getGhgPteTotals();
		for (TVPteAdjustment adj : totals) {
			String tonsPerYear = adj.getPteEUTotal();
			if (adj.getPteAdjusted() != null
					&& !"0".equals(adj.getPteAdjusted().trim())) {
				tonsPerYear = adj.getPteAdjusted();
			}
			BigDecimal co2e = new BigDecimal("0");
			try {
				co2e = PollutantDef.computeBigDecimalCO2Equivalent(
						adj.getPollutantCd(), tonsPerYear);
			} catch (NumberFormatException e1) {
				logger.error(
						"Invalid value for CO2 Equivalent for application "
								+ app.getApplicationNumber(), e1);
			}
			try {
				adj.setCo2Equivalent(getApplicationService()
						.getEmissionBigDecimalValueAsString(
								adj.getPollutantCd(),
								adj.getEuEmissionTableCd(), co2e));
			} catch (RemoteException e) {
				logger.error(
						"Exception calculating CO2 Equivalent values for application "
								+ app.getApplicationNumber(), e);
			}
		}
	}

	/**
	 * make sure all fields are specified if they would cause problems with the
	 * database if left unspecified. Full validation of all fields is left to
	 * ApplicatioBO.validateApplication to handle since we want to allow the
	 * user to save partially complete applications.
	 */
	private boolean checkApplicationContents() {
		boolean ret = true;
		if (contactInfoProvided()) {
			Address address = applicationContact.getAddress();
			String idPrefix = "ThePage:ptioFacility:contact:";
			String msgSuffix = " in the Permit Application Contact section.";
			if (application instanceof TVApplication) {
				idPrefix = "ThePage:tvFacility:";
				msgSuffix = " in the Statutory Agent section.";
			}
			address.clearValidationMessages();
			if (!address.validateAddress()) {
				int errCount = 0;
				for (String key : address.getValidationMessages().keySet()) {
					// ignore county code errors since county is not a field
					// used in applications
					if ("countyCd".equals(key)) {
						continue;
					}
					ValidationMessage addrMsg = address.getValidationMessages()
							.get(key);
					this.displayValidationMessage(idPrefix,
							new ValidationMessage(addrMsg.getProperty(),
									addrMsg.getMessage().replaceAll("\\.$", "")
											+ msgSuffix, addrMsg.getSeverity()));
					errCount++;

				}
				ret = errCount == 0;
			}
		}
		return ret;
	}

	/**
	 * Test to see if contact information was provided on the screen.
	 * 
	 * @return <code>true</code> if contact information is present.
	 */
	private boolean contactInfoProvided() {
		Address address = applicationContact.getAddress();
		return applicationContact.getFirstNm() != null
				|| applicationContact.getMiddleNm() != null
				|| applicationContact.getCompanyTitle() != null
				|| applicationContact.getLastModified() != null
				|| applicationContact.getTitleCd() != null
				|| address.getAddressLine1() != null
				|| address.getAddressLine2() != null
				|| address.getCityName() != null || address.getState() != null
				|| address.getZipCode() != null;
	}

	/**
	 * Cancel changes to application
	 * 
	 * @return
	 */
	public String undoApplication() {
		//setMigrate(false);
		contactReadOnly = true;
		reloadApplicationSummary();

		if (application instanceof PTIOApplication) {
			verifyNSRAppRequiredAttachments(application);
		} else if (application instanceof TVApplication) {
			verifyTVAppRequiredAttachments(application);
		}

		leaveEditMode();
		reloadApplicationSummary();
		return null;
	}

	public String validateApplication() {
		String ret = null;
		logger.debug("Enter validateApplication()" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
		if (getValidateAllowed()) {
			try {
				List<ValidationMessage> messages = getApplicationService()
						.validateApplication(application.getApplicationID());

				if (!messages.isEmpty()) {
					printValidationMessages(messages);
				} else {
	            	Object close_validation_dialog = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
					if (close_validation_dialog != null) {
	            		FacesUtil.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
	            		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
	            	}
					DisplayUtil.displayInfo("Validation Successful");
				}

			} catch (RemoteException e) {
				handleException(
						"exception in application "
								+ application.getApplicationNumber(), e);
			} finally {
				reloadApplicationSummary();
			}
		} else {
			DisplayUtil
					.displayError("You are not authorized to validate this application");
		}

		logger.debug("Exit validateApplication()" + new Timestamp(new GregorianCalendar().getTimeInMillis()).toString());
		return ret; // stay on same page
	}

	private boolean printValidationMessages(
			List<ValidationMessage> validationMessages) {
		String refID;
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		for (ValidationMessage msg : validationMessages) {
			refID = msg.getReferenceID();
			if (!refID.startsWith(ValidationBase.FACILITY_TAG)) {
				if(msg.getProperty().equalsIgnoreCase("appDetail")) {
					msg.setReferenceID(ValidationBase.APPLICATION_TAG + ":"
							+ this.application.getApplicationID().toString() + ":"
							+ msg.getProperty());// + ":" + APP_DETAIL_MANAGED_BEAN
				} else {
					msg.setReferenceID(ValidationBase.APPLICATION_TAG + ":"
							+ this.application.getApplicationID().toString() + ":"
							+ refID);// + ":" + APP_DETAIL_MANAGED_BEAN
				}
				
			}
			valMessages.add(msg);
		}

		return AppValidationMsg.validate(valMessages, true);

	}

	public void removeApplication(ActionEvent actionEvent) {
		try {
			getApplicationService().removeApplication(application);
			applicationDeleted = true;
			FacesUtil.returnFromDialogAndRefresh();
			DisplayUtil.displayInfo("Application has been deleted.");
		} catch (RemoteException e) {
			handleException(
					"exception in application "
							+ application.getApplicationNumber(), e);
		}
	}

	public String downloadDoc() {
		ApplicationDocumentRef docRef = (ApplicationDocumentRef) FacesUtil
				.getManagedBean(DOC_MANAGED_BEAN);
		Document doc = docRef.getPublicDoc();
		if (doc != null) {
			try {
				OpenDocumentUtil.downloadDocument(doc.getPath());
			} catch (IOException e) {
				DisplayUtil.displayError("A system error has occurred. "
						+ "Please contact System Administrator.");
				logger.error(
						"Exception downloading document at path "
								+ doc.getPath(), e);
			}
		} else {
			// this should never happen
			logger.error("No public document found for application document id: "
					+ docRef.getApplicationDocId());
		}
		return null; // stay on same page
	}

	public String getPublicDocURL() {
		String url = null;
		
		//Logger.debug(" getPublicDocURL - editMode: "+this.editMode);
		//Logger.debug("this.migrate: "+this.migrate);
		//if(this.migrate){
		//	this.editMode=true;
		//}
		//Logger.debug("editMode: "+this.editMode);
		ApplicationDocumentRef docRef = (ApplicationDocumentRef) FacesUtil
				.getManagedBean(DOC_MANAGED_BEAN);
		Document doc = docRef.getPublicDoc();
		if (doc != null) {
			url = doc.getDocURL();
		} else {
			// this should never happen
			logger.error("No public document found for application document id: "
					+ docRef.getApplicationDocId());
		}
		return url; // stay on same page
	}

	public String getTradeSecretDocURL() {
		//Logger.debug("getTradeSecretDocURL - editMode: "+this.editMode);
		String url = null;
		
		if (isPublicApp()) {
			return url;
		}
		// downloadDoc is set in the startDownloadTSDoc method for internal apps
		// but this method is called directly by the portal (bypassing
		// startDownloadTSDoc)
		// so we need to retrieve the value if it is not already set
		if (downloadDoc == null || !isInternalApp()) {
			downloadDoc = (ApplicationDocumentRef) FacesUtil
					.getManagedBean(DOC_MANAGED_BEAN);
		}
		
		if (downloadDoc == null || downloadDoc.getTradeSecretDocId() == null) {
			DisplayUtil.displayError("No Trade Secret document is available.");
		} else {
			Document doc = downloadDoc.getTradeSecretDoc();
			if (doc != null) {
				url = doc.getDocURL();
			} else {
				// this should never happen
				logger.error("No public document found for application document id: "
						+ downloadDoc.getApplicationDocId());
			}
		}
		return url; // stay on same page
	}

	public String getAttestationDocURL() {
		//Logger.debug("getAttestationDocURL - editMode: "+this.editMode);
		String url = null;
		ApplicationDocument doc;
		try {
			doc = getApplicationService().createApplicationAttestationDocument(
					application);
			if (doc != null) {
				url = doc.getDocURL();
			} else {
				// this should never happen
				logger.error("Error creating attestation document for application: "
						+ application.getApplicationNumber());
			}
		} catch (RemoteException e) {
			handleException(e);
		}
		return url; // stay on same page
	}

	public String getTsConfirmMessage() {
		return SystemPropertyDef.getSystemPropertyValue("TsConfirmMessage", null);
	}

	public String startDownloadTSDoc() {
		downloadDoc = (ApplicationDocumentRef) FacesUtil
				.getManagedBean(DOC_MANAGED_BEAN);
		return TS_DOC_DOWNLOAD_DIALOG;
	}

	public String downloadTSDoc() {
		if (isPublicApp()) {
			DisplayUtil.displayError("No Trade Secret document is available.");
			return null;
		}
		// downloadDoc is set in the startDownloadTSDoc method for internal apps
		// but this method is called directly by the portal (bypassing
		// startDownloadTSDoc)
		// so we need to retrieve the value if it is not already set
		if (downloadDoc == null || !isInternalApp()) {
			downloadDoc = (ApplicationDocumentRef) FacesUtil
					.getManagedBean(DOC_MANAGED_BEAN);
		}
		if (downloadDoc == null || downloadDoc.getTradeSecretDocId() == null) {
			DisplayUtil.displayError("No Trade Secret document is available.");
		} else {
			try {
				Document tsDoc = downloadDoc.getTradeSecretDoc();
				OpenDocumentUtil.downloadDocument(tsDoc.getPath());
			} catch (RemoteException e) {
				handleException(e);
			} catch (IOException e) {
				DisplayUtil
						.displayError("A system error has occurred. Please contact System Administrator.");
				logger.error(e.getMessage(), e);
			}
		}

		return null;
	}

	public void cancelDownloadTSDoc(ActionEvent event) {
		downloadDoc = null;
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public String excludeEU() {
		String ret = APP_DETAIL_MANAGED_BEAN;

		if (getEditAllowed()) {
			try {
				selectedEU.setExcluded(true);
				selectedEU = getApplicationService().replaceApplicationEU(
						application, selectedEU);
				selectedTreeNode.setIdentifier(Integer.toString(selectedEU.getApplicationEuId()));
				DisplayUtil.displayInfo("EU "
						+ selectedEU.getFpEU().getEpaEmuId()
						+ " has been excluded from the application");
				// remove CAM Plan attachment at cover sheet if this EU has CAM
				// plan
				if (selectedEU instanceof TVApplicationEU) {
					getApplicationService().checkAndRemoveCAMPlan(application,
							selectedEU);
				}

				selectedExcludedEU = selectedEU.getFpEU();
				setSelectedEU(null);
				selectedPermitEU = null;

				reloadWithSelectedExcludedEU();
				// update NSR reasons
				getApplicationService().updateNSRPurposes(application);

				// invalidate the application, however if it is the Admin user
				// editing the submitted application then do not invalidate
				if (!isStars2Admin() || application.getSubmittedDate() == null) {
					getApplicationService()
							.setValidatedFlag(application, false);
				}

			} catch (RemoteException e) {
				handleException(
						"exception in application "
								+ application.getApplicationNumber(), e);
				reloadWithSelectedEU();
				ret = null; // stay on same page
			}
		} else {
			DisplayUtil
					.displayError("You are not authorized to modify this application");
		}
		return ret; // return to application detail page
	}

	public String includeEU() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		setSelectedEU(findApplicationEU(selectedExcludedEU.getCorrEpaEmuId(),
				true));
		// create EU in the database (to get AppEUId)
		try {
			if (selectedEU == null) {
				setSelectedEU(getApplicationService().getNewApplicationEU(
						application, selectedExcludedEU));
			}
			selectedEU = getApplicationService().retrieveApplicationEU(
					getSelectedEU().getApplicationEuId());
			selectedEU.setExcluded(false);
			getApplicationService().modifyApplicationEU(selectedEU);
			// retrieve eu from database to be sure it's in synch
			setSelectedEU(getApplicationService().retrieveApplicationEU(
					selectedEU.getApplicationEuId()));

			// populate EU with data from euToCopy if it's not null
			if (euToCopy != null) {
				ApplicationEU euToCopyLocal = findApplicationEU(euToCopy
						.getFpEU().getCorrEpaEmuId(), true);
				// retrieve euToCopy from database to be sure it's in
				// synch
				euToCopyLocal = getApplicationService().retrieveApplicationEU(
						euToCopyLocal.getApplicationEuId());
				String copyErrMsg = okToCopyEU(euToCopyLocal, selectedEU);
				if (copyErrMsg == null) {
					euToCopyLocal.setExcluded(false);
					setSelectedEU(getApplicationService()
							.copyApplicationEUData(euToCopyLocal, selectedEU));
					setSelectedEU(getApplicationService()
							.retrieveApplicationEU(
									selectedEU.getApplicationEuId()));
				} else {
					DisplayUtil.displayWarning(copyErrMsg);
				}
				// clear out euToCopy
				euToCopy = null;
			}

			// new data may make application invalid.
			// Note: Admin changes to submitted app should not alter validated
			// flag
			if (!isStars2Admin() || application.getSubmittedDate() == null) {
				getApplicationService().setValidatedFlag(application, false);
			}
			// clear cache of EU modification descriptions.
			euModificationDesc.clear();

			reloadApplicationWithSelectedEU();
			if (isEuEditAllowed()) {
				enterEditMode();
			}
		} catch (RemoteException e) {
			handleException(
					"exception in application "
							+ application.getApplicationNumber(), e);
			setSelectedEU(null);
			selectedPermitEU = null;
			reloadWithSelectedExcludedEU();
		} finally {
			clearButtonClicked();
		}
		return null; // stay on same page
	}

	private String okToCopyEU(ApplicationEU fromEU, ApplicationEU toEU) {
		String errMsg = null;
		if (fromEU == null || toEU == null) {
			// this should never happen
			errMsg = "Internal Error";
		} else if (application instanceof TVApplication) {
			// make sure both EUs are insignificant or non-insignificant
			boolean fromInsig = (fromEU.getFpEU().getTvClassCd() != null && fromEU
					.getFpEU().getTvClassCd().equals(TVClassification.INSIG));
			boolean toInsig = (toEU.getFpEU().getTvClassCd() != null && toEU
					.getFpEU().getTvClassCd().equals(TVClassification.INSIG));
			if (fromInsig != toInsig) {
				errMsg = "Cannot copy data from "
						+ (fromInsig ? "insignificant" : "non-Insignificant")
						+ " EU " + fromEU.getFpEU().getEpaEmuId() + " to "
						+ (toInsig ? "insignificant" : "non-Insignificant")
						+ " EU " + toEU.getFpEU().getEpaEmuId();
			}
		}

		return errMsg;
	}
	
	public String addOperatingScenario() {
		try {
			selectedScenario = new TVEUOperatingScenario();
			selectedScenario
					.setApplicationEuId(selectedEU.getApplicationEuId());
			selectedScenario = getApplicationService()
					.createTVEUOperatingScenario(selectedScenario);
			alternateScenario = true;
			reloadWithSelectedScenario();
			enterEditMode();
		} catch (RemoteException e) {
			handleException(
					"exception in application "
							+ application.getApplicationNumber(), e);
			selectedScenario = null;
			reloadWithSelectedEU();
		}
		return null; // stay on same page
	}

	public String updateScenario() {
		String ret = null;

		if (getEditAllowed()) {
			List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
			// make sure all data is valid before commiting changes
			for (TVApplicationEUEmissions emissions : selectedScenario
					.getCapEmissions()) {
				validateTVEmissions(emissions, "cap", validationMessages);
			}
			for (TVApplicationEUEmissions emissions : selectedScenario
					.getHapEmissions()) {
				validateTVEmissions(emissions, "hap", validationMessages);
			}

			for (TVAltScenarioPteReq req : selectedScenario
					.getPteRequirements()) {
				validatePTERequirement(req, validationMessages);
			}
			// check consistency of CAP values against one another
			boolean editError = false;
			try {
				List<String> consistencyErrors = getApplicationService()
						.verifyTVSubpartTotals(
								selectedScenario.getCapEmissions());
				for (String error : consistencyErrors) {
					DisplayUtil.displayError(error, "ThePage:tvEU:capTable");
					editError = true;
				}
			} catch (RemoteException e) {
				handleException(
						"exception in application "
								+ application.getApplicationNumber(), e);
			}
			if (validationMessages.size() == 0 && !editError) {
				try {
					// changes may have made EU invalid
					// Note: Admin changes to submitted app should not alter
					// validated flag
					if (!isStars2Admin()
							|| application.getSubmittedDate() == null) {
						selectedEU.setValidated(false);
					}
					// modifyApplicationEU to EU will also pick up
					// changes to scenario
					ApplicationService appBO = getApplicationService();
					appBO.modifyApplicationEU(selectedEU);
					// mark application as not validated since
					// updated EU information has not yet been validated
					// Note: Admin changes to submitted app should not alter
					// validated flag
					if (!isStars2Admin()
							|| application.getSubmittedDate() == null) {
						appBO.setValidatedFlag(application, false);
					}
					DisplayUtil
							.displayInfo("Alternate Scenario data successfully saved");
					leaveEditMode();
				} catch (RemoteException re) {
					handleException(
							"exception in application "
									+ application.getApplicationNumber(), re);
				} finally {
					reloadWithSelectedScenario();
				}
			} else {
				displayValidationMessages("",
						validationMessages.toArray(new ValidationMessage[0]));
			}
		} else {
			DisplayUtil
					.displayError("You are not authorized to modify this application");
		}
		return ret; // stay on same page
	}

	private void validatePTERequirement(TVAltScenarioPteReq req,
			List<ValidationMessage> validationMessages) {
		req.requiredField(req.getPollutantCd(), "pollutantCd", "Pollutant",
				"ptePollutant", validationMessages);
		req.requiredField(req.getAllowable(), "allowable", "Allowable",
				"pteAllowableText", validationMessages);
		req.requiredField(req.getEmissionUnitsCd(), "emissionUnitsCd",
				"Units for the Allowable", "pteAllowableUnitsChoice",
				validationMessages);
		req.requiredField(req.getApplicableReq(), "applicableReq",
				"Applicable Requirement", "pteApplicableReqText",
				validationMessages);
	}

	private void validateTVEmissions(TVApplicationEUEmissions emissions,
			String idPrefix, List<ValidationMessage> validationMessages) {
		emissions.requiredField(emissions.getPollutantCd(), "pollutantCd",
				"Pollutant", idPrefix + "PollutantChoice", validationMessages);
		emissions.requiredField(emissions.getPteTonsYr(), "pteTonsYr",
				"PTE (ton/year)", idPrefix + "PTEText", validationMessages);
		if (emissions.isDetBasisTradeSecret()) {
			emissions.requiredField(emissions.getReasonDetBasisTradeSecret(),
					"reasonDetBasisTradeSecret",
					"Reason Determination Basis is a Trade Secret", idPrefix
							+ "DetBasisTSReasonCol", validationMessages);
		}
	}

	public String undoScenario() {
		reloadWithSelectedScenario();
		leaveEditMode();
		return null; // stay on same page
	}

	public void deleteScenario(ActionEvent actionEvent) {
		if (getEditAllowed()) {
			try {
				getApplicationService().removeTVEUOperatingScenario(
						selectedScenario);
				selectedTreeNode = null;
				reloadApplicationSummary();
				FacesUtil.returnFromDialogAndRefresh();
				DisplayUtil.displayInfo("Alternate Scenario "
						+ selectedScenario.getTvEuOperatingScenarioNm()
						+ " has been removed from the application");
			} catch (RemoteException e) {
				handleException(
						"exception in application "
								+ application.getApplicationNumber(), e);
				reloadWithSelectedScenario();
			}
		} else {
			DisplayUtil
					.displayError("You are not authorized to modify this application");
		}
	}

	public String updateEU() {
		String ret = null;
		if (getEditAllowed()) {
			boolean editError = false;
			try {
				if (selectedEU instanceof PTIOApplicationEU) {
					if (((PTIOApplicationEU) selectedEU).getEuType() instanceof NSRApplicationEUTypeENG) {
						NSRApplicationEUTypeENG eng = (NSRApplicationEUTypeENG) ((PTIOApplicationEU) selectedEU)
								.getEuType();
						if ("Diesel".equals(eng.getFpEuPrimaryFuelType())
								|| "Diesel".equals(eng
										.getFpEuSecondaryFuelType())) {
							eng.setDiesel(true);
						}
					}
					setSubpartCDs(selectedEU);

					PTIOApplicationEU ptioEU = (PTIOApplicationEU) selectedEU;
					String currentEUPurposeCd = ptioEU.getPtioEUPurposeCD();
					String modificationDesc = euModificationDesc
							.get(currentEUPurposeCd);
					ptioEU.setModificationDesc(euModificationDesc
							.get(currentEUPurposeCd));

					// validate EU required fields
					if (!application.isLegacy() && !isValidNSRApplicationEU()) {
						editError = true;
					}
					
					// need to check to see if any bact emission is null
					List<NSRApplicationBACTEmission> bactEmissions = ptioEU
							.getBactEmissions();
					boolean bactError = false;
					if (ptioEU.isBactAnalysisCompleted()) {
						for (NSRApplicationBACTEmission bactEmission : bactEmissions) {
							if (bactEmission.getPollutantCd() == null) {
								bactError = true;
							}
						}
					} else {
						List<NSRApplicationBACTEmission> newBactEmissions = new ArrayList<NSRApplicationBACTEmission>();
						for (NSRApplicationBACTEmission bactEmission : bactEmissions) {
							if (bactEmission.getPollutantCd() != null) {
								newBactEmissions.add(bactEmission);
							}
						}
						ptioEU.setBactEmissions(newBactEmissions);
					}
					
					if (bactError) {
						DisplayUtil
								.displayError(
										"An added BACT emission never had a pollutant selected.",
										"ThePage:ptioEU:bactAnalysisCompleted");
						editError = true;
					}

					// need to check to see if any laer emission is null
					List<NSRApplicationLAEREmission> laerEmissions = ptioEU
							.getLaerEmissions();
					boolean laerError = false;
					if (ptioEU.isLaerAnalysisCompleted()) {
						for (NSRApplicationLAEREmission laerEmission : laerEmissions) {
							if (laerEmission.getPollutantCd() == null) {
								laerError = true;
							}
						}
					} else {
						List<NSRApplicationLAEREmission> newLaerEmissions = new ArrayList<NSRApplicationLAEREmission>();
						for (NSRApplicationLAEREmission laerEmission : laerEmissions) {
							if (laerEmission.getPollutantCd() != null) {
								newLaerEmissions.add(laerEmission);
							}
						}
						ptioEU.setLaerEmissions(newLaerEmissions);
					}

					if (laerError) {
						DisplayUtil
								.displayError(
										"An added LAER emission never had a pollutant selected.",
										"ThePage:ptioEU:laerAnalysisCompleted");
						editError = true;
					}

					// need to check text values here since marking the fields
					// as
					// required is problematic
					if (PTIOApplicationEUPurposeDef.RECONSTRUCTION
							.equals(currentEUPurposeCd)) {
						if (ptioEU.getReconstructionDesc() == null
								|| ptioEU.getReconstructionDesc().trim()
										.length() == 0) {
							DisplayUtil
									.displayError(
											"A value is required for the Reconstruction Reason field.",
											"ThePage:ptioEU:reconExplText");
							editError = true;
						}
					} else if (PTIOApplicationEUPurposeDef.OTHER
							.equals(currentEUPurposeCd)) {
						if (modificationDesc == null
								|| modificationDesc.trim().length() == 0) {
							DisplayUtil
									.displayError(
											"A value is required for the Other Reason field.",
											"ThePage:ptioEU:otherReasonText");
							editError = true;
						}
					}

					// check consistency of CAP values against one another
					List<String> consistencyErrors = getApplicationService()
							.verifySubpartTotals(ptioEU.getCapEmissions());
					for (String error : consistencyErrors) {
						DisplayUtil.displayError(error,
								"ThePage:ptioEU:capTable");
						editError = true;
					}

				} else if (selectedEU instanceof TVApplicationEU) {
					setTvEuSubpartCDs(selectedEU);

					// check consistency of CAP values against one another
					List<String> consistencyErrors = getApplicationService()
							.verifyTVSubpartTotals(
									((TVApplicationEU) selectedEU)
											.getNormalOperatingScenario()
											.getCapEmissions());
					int i = 0;
					
					for (TVApplicationEUEmissions emissions : selectedScenario
							.getCapEmissions()) {
						if (!Utility.isNullOrEmpty(emissions.getPteTonsYr())) {
							if (Float.parseFloat(emissions.getPteTonsYr()) > 0) {

								if (Utility.isNullOrEmpty(emissions
										.getPteDeterminationBasis())) {
									DisplayUtil
											.displayError(
													"Basis for Determination attribute is not set.",
													"ThePage:tvEU:tvEuPTE:capTable:"
															+ new Integer(i)
																	.toString()
															+ ":capPTEDetBasisDropdown");
									editError = true;
								}

							}
						}
						i++;
					}

					for (String error : consistencyErrors) {
						DisplayUtil.displayError(error,
								"ThePage:ptioEU:capTable");
						editError = true;
					}
					
					if(!checkTVEUSubparts((TVApplicationEU) selectedEU)){
						editError = true;
					}
				}
				if (!editError) {
					// changes may have made EU invalid
					// Note: Admin changes to submitted app should not alter
					// validated flag
					if (!isStars2Admin()
							|| application.getSubmittedDate() == null) {
						selectedEU.setValidated(false);
						// mark application as not validated since
						// updated EU information has not yet been validated
						application.setValidated(false);
					}
					
					getApplicationService().modifyApplicationEU(selectedEU);
					
					ApplicationService appBO = getApplicationService();
					setSelectedEU(appBO.retrieveApplicationEU(selectedEU
							.getApplicationEuId()));
					
					// changes to EUs may make application invalid.
					// Note: Admin changes to submitted app should not alter validated
					// flag
					if (!isStars2Admin() || application.getSubmittedDate() == null) {
						getApplicationService().setValidatedFlag(application, false);
					}
					
					if (selectedEU instanceof PTIOApplicationEU) {
						verifyNSRAppEURequiredAttachments(application,
								selectedEU);

					} else if (selectedEU instanceof TVApplicationEU) {
						verifyTVAppEURequiredAttachments(application,
								selectedEU);

					}

					DisplayUtil.displayInfo("EU data successfully saved");
					leaveEditMode();
				}
			} catch (RemoteException re) {
				handleException(
						"exception in application "
								+ application.getApplicationNumber(), re);
			} finally {
				// reload application to keep "last modified" in synch.
				if (!editError) {
					reloadWithSelectedEU();
				}
			}
		} else {
			DisplayUtil
					.displayError("You are not authorized to modify this application");
		}
		return ret; // stay on same page
	}

	private boolean isValidNSRApplicationEU() {
		boolean isValid = true;
		String containerId = "";
		ValidationMessage[] validationMessages = null;
		List<ValidationMessage> validationaMessagesList = new ArrayList<ValidationMessage>();
		if (this.selectedEU instanceof PTIOApplicationEU) {

			// validate EU types
			validationMessages = ((PTIOApplicationEU) this.selectedEU)
					.getEuType().validate();
			containerId = "ThePage:ptioEU:";
			validationaMessagesList.addAll(Utility
					.createArrayList(validationMessages));
			if (((PTIOApplicationEU) this.selectedEU).getFpEU()
					.getEmissionUnitTypeCd().equals("CMX")){
				checkCMXMaterialUsed((PTIOApplicationEU) this.selectedEU,
						validationaMessagesList);
			}
			checkNSRSubparts((PTIOApplicationEU) this.selectedEU, validationaMessagesList);
		}
		checkNSRCapEmissions((PTIOApplicationEU) this.selectedEU,
				validationaMessagesList);
		checkNSRHapEmissions((PTIOApplicationEU) this.selectedEU,
				validationaMessagesList);
		checkNSRGhgEmissions((PTIOApplicationEU) this.selectedEU,
				validationaMessagesList);

		if (validationaMessagesList.size() > 0) {
			displayValidationMessages(containerId,
					validationaMessagesList.toArray(new ValidationMessage[0]));
			isValid = false;
		}

		return isValid;
	}
	
	private void checkNSRSubparts(PTIOApplicationEU nsrAppEU,
			List<ValidationMessage> validationMessages) {
    	List<String> nspsSubparts = nsrAppEU.getNspsSubpartCodes();
    	List<String> neshapPart61Subparts = nsrAppEU.getNeshapSubpartCodes();
    	List<String> neshapPart63Subparts = nsrAppEU.getMactSubpartCodes();
    	
    	boolean nspsHasDuplicate = false;
    	boolean neshapPart61HasDuplicate = false;
    	boolean neshapPart63HasDuplicate = false;
    	
    	if(nspsSubparts != null) {
    		nspsHasDuplicate = Utility.hasDuplicate(nspsSubparts);
    		if(nspsHasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"NSPSChoice",
						"You cannot add duplicate NSPS subparts.",
						ValidationMessage.Severity.ERROR, "FederalRulesShowDetail"));
    		}
    	}
    	
    	if(neshapPart61Subparts != null) {
    		neshapPart61HasDuplicate = Utility.hasDuplicate(neshapPart61Subparts);
    		if(neshapPart61HasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"NESHAPChoice",
						"You cannot add duplicate Part 61 NESHAP subparts.",
						ValidationMessage.Severity.ERROR, "FederalRulesShowDetail"));
    		}
    	}
    	
    	if(neshapPart63Subparts != null) {
    		neshapPart63HasDuplicate = Utility.hasDuplicate(neshapPart63Subparts);
    		if(neshapPart63HasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"MACTChoice",
						"You cannot add duplicate Part 63 NESHAP subparts.",
						ValidationMessage.Severity.ERROR, "FederalRulesShowDetail"));
    		}
    	}
	}
	
	private boolean checkTVEUSubparts(TVApplicationEU tvAppEU) {
		boolean isValid = true;
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
    	List<String> nspsSubparts = tvAppEU.getNspsSubpartCodes();
    	List<String> neshapPart61Subparts = tvAppEU.getNeshapSubpartCodes();
    	List<String> neshapPart63Subparts = tvAppEU.getMactSubpartCodes();
    	
    	boolean nspsHasDuplicate = false;
    	boolean neshapPart61HasDuplicate = false;
    	boolean neshapPart63HasDuplicate = false;
    	
    	String containerId = "ThePage:tvEU:tvEuSpecificRequirements:";
    	
    	if(nspsSubparts != null) {
    		nspsHasDuplicate = Utility.hasDuplicate(nspsSubparts);
    		if(nspsHasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"NSPSChoice",
						"You cannot add duplicate NSPS subparts.",
						ValidationMessage.Severity.ERROR, "tvEuSpecificRequirements"));
    		}
    	}
    	
    	if(neshapPart61Subparts != null) {
    		neshapPart61HasDuplicate = Utility.hasDuplicate(neshapPart61Subparts);
    		if(neshapPart61HasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"NESHAPChoice",
						"You cannot add duplicate Part 61 NESHAP subparts.",
						ValidationMessage.Severity.ERROR, "tvEuSpecificRequirements"));
    		}
    	}
    	
    	if(neshapPart63Subparts != null) {
    		neshapPart63HasDuplicate = Utility.hasDuplicate(neshapPart63Subparts);
    		if(neshapPart63HasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"MACTChoice",
						"You cannot add duplicate Part 63 NESHAP subparts.",
						ValidationMessage.Severity.ERROR, "tvEuSpecificRequirements"));
    		}
    	}
    	
		if (validationMessages.size() > 0) {
			displayValidationMessages(containerId,
					validationMessages.toArray(new ValidationMessage[0]));
			isValid = false;
		}
		
		return isValid;
	}

	public String updateTvEuGroup() {
		String ret = null;

		if (getEditAllowed()) {
			if (selectedEUGroup.getTvEuGroupName() == null
					|| selectedEUGroup.getTvEuGroupName().length() == 0) {
				DisplayUtil
						.displayError("Please provide a name for this EU Group");
			} else {
				try {
					getApplicationService().modifyTvEuGroup(selectedEUGroup);
					DisplayUtil.displayInfo("EU Group successfully updated");
				} catch (RemoteException re) {
					handleException(
							"exception in application "
									+ application.getApplicationNumber(), re);
				} finally {
					reloadWithSelectedEUGroup();
					leaveEditMode();
				}
			}
		} else {
			DisplayUtil
					.displayError("You are not authorized to modify this application");
		}
		return ret; // stay on same page
	}

	public String undoTvEuGroup() {
		reloadWithSelectedEUGroup();
		leaveEditMode();
		return null; // stay on same page
	}

	public void deleteTvEuGroup(ActionEvent actionEvent) {
		if (getEditAllowed()) {
			try {
				String euGroupName = selectedEUGroup.getTvEuGroupName();
				getApplicationService().removeTVEUGroup(
						selectedEUGroup.getTvEuGroupId());
				selectedTreeNode = null;
				selectedEUGroup = null;
				reloadApplicationSummary();
				FacesUtil.returnFromDialogAndRefresh();
				DisplayUtil.displayInfo("EU Group  " + euGroupName
						+ " has been removed from the application");
			} catch (RemoteException re) {
				handleException(
						"exception in application "
								+ application.getApplicationNumber(), re);
				reloadWithSelectedEUGroup();
			}
		} else {
			DisplayUtil
					.displayError("You are not authorized to modify this application");
		}
	}
	
	private void reloadWithSelectedEU() {
		// update application with data from newly created EU

		// select node in tree corresponding to selectedEU
		Stars2TreeNode root = (Stars2TreeNode) treeData.getNodeById("0");
		for (Object node : root.getChildren()) {
			Stars2TreeNode treeNode = (Stars2TreeNode) node;
			if ((treeNode.getType().equals(EU_NODE_TYPE) || treeNode.getType()
					.equals(INSIG_EU_NODE_TYPE))
					&& treeNode.getDescription().equals(
							selectedEU.getFpEU().getEpaEmuId())) {
				selectedTreeNode = treeNode;
				saveSelectedTreeNode = selectedTreeNode;

				// reset selectedEU to point to the updated object
				setSelectedEU((ApplicationEU) selectedTreeNode.getUserObject());
				reloadApplicationWithSelectedEU();
				if (selectedEU instanceof TVApplicationEU) {
					// reset selectedScenario and applicable requirement
					selectedScenario = ((TVApplicationEU) selectedEU)
							.getNormalOperatingScenario();
					applicableRequirements = ((TVApplicationEU) selectedEU)
							.getApplicableRequirements();
					stateOnlyRequirements = ((TVApplicationEU) selectedEU)
							.getStateOnlyRequirements();
				} else if (selectedEU instanceof PTIOApplicationEU) {
					euModificationDesc.clear();
					String currentEUPurposeCd = ((PTIOApplicationEU) selectedEU)
							.getPtioEUPurposeCD();
					if (currentEUPurposeCd != null) {
						euModificationDesc.put(currentEUPurposeCd,
								((PTIOApplicationEU) selectedEU)
										.getModificationDesc());
					}
				}
				break;
			}
		}

		if (selectedEU != null) {
			if (selectedEU instanceof PTIOApplicationEU) {
				laerPollutantWrapper = new TableSorter();
				laerPollutantWrapper.clearWrappedData();
				bactPollutantWrapper = new TableSorter();
				bactPollutantWrapper.clearWrappedData();
				laerPollutantWrapper
						.setWrappedData(((PTIOApplicationEU) selectedEU)
								.getLaerEmissions());
				bactPollutantWrapper
						.setWrappedData(((PTIOApplicationEU) selectedEU)
								.getBactEmissions());
			}
		}
	}

	private void reloadWithSelectedExcludedEU() {
		reloadApplicationSummary();

		// select node in tree corresponding to selectedExcludedEU
		Stars2TreeNode root = (Stars2TreeNode) treeData.getNodeById("0");
		for (Object node : root.getChildren()) {
			Stars2TreeNode treeNode = (Stars2TreeNode) node;
			if (treeNode.getType().equals(EXCLUDED_EU_NODE_TYPE)
					&& treeNode.getDescription().equals(
							selectedExcludedEU.getEpaEmuId())) {
				selectedTreeNode = treeNode;
				saveSelectedTreeNode = selectedTreeNode;

				// reset selectedExcludedEU to point to the updated ibject
				selectedExcludedEU = (EmissionUnit) selectedTreeNode
						.getUserObject();
				break;
			}
		}
	}

	private void reloadWithSelectedScenario() {
		// update application with data from newly created EU
		reloadApplicationSummary();

		// select node in tree corresponding to selectedScenario
		Stars2TreeNode root = (Stars2TreeNode) treeData.getNodeById("0");
		for (Object node : root.getChildren()) {
			Stars2TreeNode treeNode = (Stars2TreeNode) node;
			if (treeNode.getType().equals(EU_NODE_TYPE)
					&& (treeNode.getIdentifier().equals(selectedScenario
							.getApplicationEuId().toString()))) {
				setSelectedEU((ApplicationEU) treeNode.getUserObject());
				for (Object leafNode : treeNode.getChildren()) {
					if (((Stars2TreeNode) leafNode).getIdentifier().equals(
							selectedScenario.getApplicationEuId()
									+ ":"
									+ selectedScenario
											.getTvEuOperatingScenarioId())) {
						selectedTreeNode = ((Stars2TreeNode) leafNode);
						selectedScenario = (TVEUOperatingScenario) selectedTreeNode
								.getUserObject();
						break;
					}
				}
				break;
			}
		}
	}

	private void reloadWithSelectedEUGroup() {
		// update application with data from newly created EU Group
		reloadApplicationSummary();

		// select node in tree corresponding to selectedEU
		Stars2TreeNode root = (Stars2TreeNode) treeData.getNodeById("0");
		for (Object node : root.getChildren()) {
			Stars2TreeNode treeNode = (Stars2TreeNode) node;
			if (treeNode.getType().equals(EU_GROUP_NODE_TYPE)
					&& treeNode.getIdentifier().equals(
							selectedEUGroup.getTvEuGroupId().toString())) {
				selectedTreeNode = (Stars2TreeNode) node;
				selectedEUGroup = (TVEUGroup) selectedTreeNode.getUserObject();
				applicableRequirements = selectedEUGroup
						.getApplicableRequirements();
				stateOnlyRequirements = selectedEUGroup
						.getStateOnlyRequirements();
				break;
			}
		}
	}

	public String undoEU() {
		if (application instanceof PTIOApplication) {
			verifyNSRAppEURequiredAttachments(application, selectedEU);

		} else if (selectedEU instanceof TVApplicationEU) {
			verifyTVAppEURequiredAttachments(application, selectedEU);
			try {
				getApplicationService().checkAndAddCAMPlan(application,
						selectedEU);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unknow exception in adding CAM attachment");
				logger.error(e.getMessage());
			}
		}
		
		leaveEditMode();
		reloadApplicationWithSelectedEU();
		return null; // stay on same page
	}

	/**
	 * Display the application as a PDF file in a browser window.
	 * 
	 * @return
	 */
	// public String printApplication() {
	// String ret = null;
	// try {
	// FacesContext facesContext = FacesContext.getCurrentInstance();
	// HttpServletResponse response
	// = (HttpServletResponse) facesContext.getExternalContext().getResponse();
	//
	// response.setContentType("application/pdf");
	// OutputStream os = response.getOutputStream();
	// getApplicationService().writeApplicationReportToStream(application,
	// hideTradeSecret, os);
	// os.flush();
	// facesContext.responseComplete();
	// } catch (RemoteException re) {
	// handleException(re);
	// } catch (IOException e) {
	// DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
	// logger.error("Exception printing application", e);
	// }
	// return ret;
	// }

	public String zipApplication() {
		String ret = null;
		try {
			String userName = null;
			if (isInternalApp()
					&& InfrastructureDefs.getCurrentUserAttrs() != null) {
				userName = InfrastructureDefs.getCurrentUserAttrs()
						.getUserName();
			}
			if (userName == null) {
				// create a dummy user name so the name of the generated
				// facility report does not contain "null"
				userName = "internal";
			}
			
			Facility appFacility = getFacilityService()
					.retrieveFacilityProfile(
							application.getFacility().getFpId(),
							isPortalApp() && !readOnly);
			
			reloadApplicationWithIncludedEUs();
			
			Document facilityDoc = getFacilityService()
					.generateTempFacilityProfileReport(
							appFacility, null);
			

			Document zipDoc = getApplicationService()
					.generateTempApplicationZipFile(application, facilityDoc,
							true, false, readOnly);

			String fileName = ((TmpDocument) zipDoc).getTmpFileName();
			String url = getZipFileDirName(application.getFacilityId())
					+ fileName;
			String contentDisposition = "attachment;filename=" + fileName;
			OpenDocumentUtil.downloadDocument(url, contentDisposition);
		} catch (FileNotFoundException nfe) {
			DisplayUtil
					.displayError("Attachment file not found. "
							+ nfe.getMessage()
							+ " Please delete the corresponding entry from the attachments table and upload it again.");
			logger.error(
					"exception in application "
							+ application.getApplicationNumber(), nfe);
		} catch (IOException ioe) {
			handleException(new RemoteException("exception in application "
					+ application.getApplicationNumber(), ioe));

		}
		return ret;
	}

	private String getZipFileDirName(String facilityId) {

		String baseDir = File.separator + "tmp";

		if (facilityId != null && facilityId.length() > 0) {
			baseDir += File.separator + "Facilities" + File.separator
					+ facilityId + File.separator;
		}

		return baseDir;
	}

	/*
	 * Actions - attachments-related
	 */
	public String startEditDoc() {
		try {
			// make sure viewDoc flag is false.
			viewDoc = false;
			applicationDoc = (ApplicationDocumentRef) FacesUtil
					.getManagedBean(DOC_MANAGED_BEAN);
			// make sure doc is not null
			if (applicationDoc != null) {
				// make sure doc has an id
				if (applicationDoc.getApplicationDocId() != null) {
					applicationDoc = getApplicationService()
							.retrieveApplicationDocument(application.getApplicationTypeCD(),
									applicationDoc.getApplicationDocId(),
									readOnly);
					getApplicationService().loadDocuments(applicationDoc, readOnly);
					docUpdate = true;
				} else {
					logger.error("Invalid application document with no id in document table");
				}
			} else {
				logger.error("Unable to retrieve managed bean for document in application document table");
			}
		} catch (RemoteException e) {
			handleException(
					"exception in application "
							+ application.getApplicationNumber(), e);
		}

		this.publicFileInfo = null;
		this.tsFileInfo = null;

		if (applicationDoc.isRequiredDoc()) {
			return REQUIRED_DOCUMENT_DIALOG;
		}

		return DOCUMENT_DIALOG;
	}

	public String startAddDoc() {
		if (application != null && application instanceof PTIOApplication) {
			applicationDoc = new NSRApplicationDocumentRef();
		} else if (application != null && application instanceof TVApplication) {
			applicationDoc = new TVApplicationDocumentRef();
		} else {
			applicationDoc = new ApplicationDocumentRef();
		}
		docUpdate = false;
		return DOCUMENT_DIALOG;
	}

	public void applyEditDoc(ActionEvent actionEvent) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		if (!usingExistingDoc) {
			// copy file upload information so it isn't lost
			if (publicFileInfo == null && fileToUpload != null) {
				publicFileInfo = new UploadedFileInfo(fileToUpload);
			}
			if (isTradeSecretAllowed() && tsFileInfo == null && tsFileToUpload != null) {
				tsFileInfo = new UploadedFileInfo(tsFileToUpload);
			}

			// make sure document description and type are provided
			if (applicationDoc.getDescription() == null || applicationDoc.getDescription().trim().equals("")) {
	        	validationMessages.add(new ValidationMessage("description", "Attribute " + "Description" + " is not set.",
						ValidationMessage.Severity.ERROR, "descriptionText"));
			}
			
			if (!isPbrRprRpe()) {
				if (applicationDoc.getApplicationDocumentTypeCD() == null || applicationDoc.getApplicationDocumentTypeCD().trim().equals("")) {
		        	validationMessages.add(new ValidationMessage("applicationDocumentTypeCD", "Attribute " + "Attachment Type" + " is not set.",
							ValidationMessage.Severity.ERROR, "docTypeChoice"));
				}
			} /*
			 * else { applicationDoc
			 * .setApplicationDocumentTypeCD(ApplicationDocumentTypeDef.OTHER);
			 * } if (applicationDoc.getApplicationDocumentTypeCD() != null &&
			 * applicationDoc.getApplicationDocumentTypeCD().equals(
			 * ApplicationDocumentTypeDef.EAC)) { // EAC form type is required
			 * if document type is EAC
			 * applicationDoc.requiredField(applicationDoc.getEacFormTypeCD(),
			 * "eacFormTypeCD", "EAC Form Type", "eacFormTypeChoice",
			 * validationMessages); }
			 */

			if (publicFileInfo == null
					&& applicationDoc.getApplicationDocumentTypeCD() != null) {
					if (applicationDoc.getDocumentId() == null) {
						validationMessages
								.add(new ValidationMessage(
										"documentId",
										"Please specify a Public File to Upload",
										ValidationMessage.Severity.ERROR,
										"publicFile"));
					}
			} 
			
			if (fileToUpload != null && !DocumentUtil.isValidFileExtension(fileToUpload)){
				validationMessages.add(new ValidationMessage("documentId",
						DocumentUtil.invalidFileExtensionMessage(null), ValidationMessage.Severity.ERROR, "publicFile")); 
				publicFileInfo = null;
			}
			
			// make sure a justification is provided for trade secret document
			if (isTradeSecretAllowed()) {
				if (tsFileToUpload != null && !DocumentUtil.isValidFileExtension(tsFileToUpload)){
					validationMessages.add(new ValidationMessage("tradeSecretDocId",
							DocumentUtil.invalidFileExtensionMessage("trade secret"), ValidationMessage.Severity.ERROR, "tradeSecretFile")); 
					tsFileInfo = null;
				} else {
					if (tsFileInfo != null || applicationDoc.getTradeSecretDocId() != null) {
						if (applicationDoc.getTradeSecretReason() == null || applicationDoc.getTradeSecretReason().trim().equals("")) {
				        	validationMessages.add(new ValidationMessage("tradeSecretReason", 
				        			"Attribute " + "Trade Secret Justification" + " is not set.", ValidationMessage.Severity.ERROR, "tradeSecretReason"));
						}
					} else if (applicationDoc.getTradeSecretReason() != null && applicationDoc.getTradeSecretReason().trim().length() > 0) {
						validationMessages.add(new ValidationMessage("tradeSecretReason",
									"The Trade Secret Justification field should only be populated when a trade secret document is specified.", ValidationMessage.Severity.ERROR, "tradeSecretReason"));
					}
				}
			}
			
			if (validationMessages.size() == 0) {
				boolean isEUDoc = !selectedTreeNode.getType().equals(
						APPLICATION_NODE_TYPE);
				if (docUpdate) {
					try {
						ApplicationService appBO = getApplicationService();
						// add public doc if it was not included when
						// applicationDoc was created
						if (publicFileInfo != null
								&& applicationDoc.getDocumentId() == null) {
							ApplicationDocument publicDoc = appBO
									.uploadApplicationDocument(application,
											publicFileInfo,
											applicationDoc.getDescription(),
											getUserID());
							applicationDoc.setDocumentId(publicDoc
									.getDocumentID());
						}

						// add trade secret doc if it was not included when
						// applicationDoc was created
						if (tsFileToUpload != null && applicationDoc.getTradeSecretDocId() == null) {
							ApplicationDocument tsDoc = appBO
									.uploadApplicationDocument(application,
											tsFileInfo,
											applicationDoc.getDescription(),
											getUserID());
							applicationDoc.setTradeSecretDocId(tsDoc
									.getDocumentID());
						}

						// modify database instance of document
						appBO.modifyApplicationDocument(applicationDoc);

						// avoid data loss during attachment add
						refreshAttachments();
						if (!editMode) {
							loadApplicationSummary(application.getApplicationID());
						}

						if (applicationDoc.getApplicationEUId() != null) {
							refreshAttachments();
							if (!editMode) {
								reloadWithSelectedEU();
							}
						}

						FacesUtil.returnFromDialogAndRefresh();
						DisplayUtil
								.displayInfo("The attachment information has been updated.");
					} catch (RemoteException re) {
						handleException("exception in application "
								+ application.getApplicationNumber(), re);
					}
				} else {
					// if uploading a required document via add button then
					// remove the earlier
					// references to the require document of this type in the
					// application documents
					for (ApplicationDocumentRef appDocRef : applicationDocuments) {
						if (appDocRef.isRequiredDoc()
								&& appDocRef.getDocumentId() == null
								&& appDocRef
										.getApplicationDocumentTypeCD()
										.equals(applicationDoc
												.getApplicationDocumentTypeCD())) {
							applicationDoc.setRequiredDoc(true);
							try {
								getApplicationService()
										.removeApplicationDocument(appDocRef);
							} catch (RemoteException e) {
								DisplayUtil
										.displayError("There is an error when the system was processing the generation attachment. Error: "
												+ e.getMessage());
								logger.error(e);
							}
							break;
						}
					}
					// new document
					// upload document and create database entry pointing to it
					applicationDoc.setApplicationId(application
							.getApplicationID());
					if (isEUDoc) {
						applicationDoc.setApplicationEUId(selectedEU
								.getApplicationEuId());
					}
					try {
						ApplicationService appBO = getApplicationService();
						// add document information to the system
						if (isTradeSecretAllowed()) {
							applicationDoc = appBO.uploadApplicationDocument(
								application, applicationDoc, publicFileInfo,
								tsFileInfo, getUserID());
						} else {
							applicationDoc.setTradeSecretReason(null);
							applicationDoc = appBO.uploadApplicationDocument(
								application, applicationDoc, publicFileInfo,
								null, getUserID());
						}
						appBO.loadDocuments(applicationDoc, readOnly);
						// add document to local list of documents
						applicationDocuments.add(applicationDoc);
						application.addDocument(applicationDoc);

						// add document to EU attachments (if associated with
						// selectedEU)
						if (applicationDoc.getApplicationEUId() != null
								&& selectedEU != null
								&& selectedEU.getApplicationEuId().equals(
										applicationDoc.getApplicationEUId())) {
							selectedEU.getEuDocuments().add(applicationDoc);
						}

						// avoid data loss during attachment add
						refreshAttachments();
						if (!editMode) {
							loadApplicationSummary(application.getApplicationID());
						}

						DisplayUtil
								.displayInfo("The attachment has been added to the application.");

						// clear out temporary variables
						fileToUpload = null;
						if (publicFileInfo != null) {
							publicFileInfo.cleanup();
							publicFileInfo = null;
						}
						tsFileToUpload = null;
						if (tsFileInfo != null) {
							tsFileInfo.cleanup();
							tsFileInfo = null;
						}
						applicationDoc = null;
						FacesUtil.returnFromDialogAndRefresh();
					} catch (RemoteException re) {
						handleException("exception in application "
								+ application.getApplicationNumber(), re);
					}
				}
			} else {
				displayValidationMessages("",
						validationMessages.toArray(new ValidationMessage[0]));
			}
		} else {
			// reusing an attachment that was added to another EU or other part
			// of the application
			try {
				// if uploading a required document via add button then remove
				// the earlier
				// references to the require document of this type in the
				// application documents
				for (ApplicationDocumentRef appDocRef : applicationDocuments) {
					if (appDocRef.isRequiredDoc()
							&& appDocRef.getDocumentId() == null
							&& appDocRef.getApplicationDocumentTypeCD().equals(
									applicationDoc
											.getApplicationDocumentTypeCD())) {
						applicationDoc.setRequiredDoc(true);
						try {
							getApplicationService().removeApplicationDocument(
									appDocRef);
						} catch (RemoteException e) {
							DisplayUtil
									.displayError("There is an error when the system was processing the generation attachment. Error: "
											+ e.getMessage());
							logger.error(e);
						}
						break;
					}
				}

				ApplicationService appBO = getApplicationService();
				appBO.loadDocuments(applicationDoc, readOnly);
				// add document to local list of documents
				applicationDocuments.add(applicationDoc);
				// add document to EU attachments (if associated with
				// selectedEU)
				if (selectedEU != null) {
					applicationDoc.setApplicationEUId(selectedEU
							.getApplicationEuId());
					applicationDoc.setApplicationDocId(null);
					appBO.createApplicationDocument(applicationDoc);
					selectedEU.getEuDocuments().add(applicationDoc);
					DisplayUtil
							.displayInfo("The attachment has been added to the application EU.");
				} else {
					if (applicationDoc.getApplicationEUId() != null) {
						applicationDoc.setApplicationEUId(null);
						applicationDoc.setApplicationDocId(null);
						appBO.createApplicationDocument(applicationDoc);
						application.addDocument(applicationDoc);
						DisplayUtil
								.displayInfo("The attachment has been added to the application.");
					}
				}
				applicationDoc = null;
				usingExistingDoc = false;
				FacesUtil.returnFromDialogAndRefresh();
			} catch (RemoteException e) {
				handleException(
						"exception in application "
								+ application.getApplicationNumber(), e);
			}
		}

		// refresh EU level data if on the EU page
		if (selectedTreeNode != null
				&& !selectedTreeNode.getType().equals(APPLICATION_NODE_TYPE)) {
			// avoid data loss during attachment add
			refreshAttachments();
			if (!editMode) {
				reloadWithSelectedEU();
			}
			
		}
		// reset this to true if it has been set to false
		deleteDocAllowed = true;
	}

	public void cancelEditDoc(ActionEvent actionEvent) {
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public void docDialogDone(ReturnEvent returnEvent) {
		docUpdate = false;
		usingExistingDoc = false;
		applicationDoc = null;
		if (publicFileInfo != null) {
			publicFileInfo.cleanup();
			publicFileInfo = null;
		}
		if (tsFileInfo != null) {
			tsFileInfo.cleanup();
			tsFileInfo = null;
		}
	}

	public void removeEditDoc(ActionEvent actionEvent) {
		// remove document from database
		try {
			getApplicationService().removeApplicationDocument(applicationDoc);
			// remove document from local object
			ListIterator<ApplicationDocumentRef> iterator = applicationDocuments
					.listIterator();
			while (iterator.hasNext()) {
				ApplicationDocumentRef matchDoc = iterator.next();
				if (matchDoc.getApplicationDocId().equals(
						applicationDoc.getApplicationDocId())) {
					iterator.remove();
				}
			}
			// remove document from EU (if EU attachment)
			if (applicationDoc.getApplicationEUId() != null
					&& selectedEU != null
					&& selectedEU.getApplicationEuId().equals(
							applicationDoc.getApplicationEUId())) {
				iterator = selectedEU.getEuDocuments().listIterator();
				while (iterator.hasNext()) {
					ApplicationDocumentRef matchDoc = iterator.next();
					if (matchDoc.getApplicationDocId().equals(
							applicationDoc.getApplicationDocId())) {
						iterator.remove();
						/*
						 * if (selectedEU.isValid() &&
						 * ApplicationDocumentTypeDef.EAC .equals(applicationDoc
						 * .getApplicationDocumentTypeCD()) ||
						 * ApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM
						 * .equals(applicationDoc
						 * .getApplicationDocumentTypeCD())) { // Deleting EAC
						 * or Process Flow diagram will make EU // and app
						 * invalid // Note: Admin changes to submitted app
						 * should not // alter validated flag if
						 * (!isStars2Admin() || application.getSubmittedDate()
						 * == null) { selectedEU.setValidated(false);
						 * ApplicationService appBO = getApplicationService();
						 * appBO.modifyApplicationEU(selectedEU);
						 * appBO.setValidatedFlag(application, false); } }
						 */
					}
				}
			}
			// mark trade secret as "dirty" (even though it might not be)
			tradeSecretStatusChanged = true;
			FacesUtil.returnFromDialogAndRefresh();
			DisplayUtil.displayInfo("The attachment has been removed.");
		} catch (RemoteException re) {
			handleException(
					"exception in application "
							+ application.getApplicationNumber(), re);
		}

	}

	public String startViewDoc() {
		applicationDoc = (ApplicationDocumentRef) FacesUtil
				.getManagedBean("doc");
		viewDoc = true;
		return DOCUMENT_DIALOG;
	}

	public void closeViewDoc(ActionEvent actionEvent) {
		// reset this to true if it has been set to false
		deleteDocAllowed = true;
		viewDoc = false;
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	/*
	 * Actions - Note related
	 */
	public String startEditNote() {
		noteModify = true;
		applicationNote = new ApplicationNote((ApplicationNote) FacesUtil
				.getManagedBean(NOTE_MANAGED_BEAN));
		return NOTE_DIALOG;
	}

	public String startAddNote() {
		noteModify = false;
		applicationNote = new ApplicationNote();
		applicationNote.setApplicationId(application.getApplicationID());
		applicationNote.setUserId(getUserID());
		applicationNote.setNoteTypeCd(NoteType.DAPC);
		applicationNote
				.setDateEntered(new Timestamp(System.currentTimeMillis()));
		return NOTE_DIALOG;
	}

	public void applyEditNote(ActionEvent actionEvent) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		
		// make sure note is provided
        if (applicationNote.getNoteTxt() == null || applicationNote.getNoteTxt().trim().equals("")) {
        	validationMessages.add(new ValidationMessage("noteTxt", "Attribute " + "Note" + " is not set.",
					ValidationMessage.Severity.ERROR, "noteTxt"));
		}
        
		if (validationMessages.size() > 0) {
			displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0]));
		} else {
			try {
				if (!noteModify) {
					// new note
					getApplicationService().createApplicationNote(
							applicationNote);
					application.addApplicationNote(applicationNote);
					DisplayUtil
							.displayInfo("The note has been added to the application.");
				} else {
					// edit existing note
					if (!isStars2Admin()
							&& !applicationNote.getUserId().equals(getUserID())) {
						DisplayUtil
								.displayError("You may only edit a note which you have created.");
					} else {
						getApplicationService().modifyApplicationNote(
								applicationNote);
						DisplayUtil.displayInfo("Note updated successfully");
					}
				}
				noteModify = false;
				applicationNote = null;
				reloadNotes();
			} catch (RemoteException re) {
				handleException(
						"exception in application "
								+ application.getApplicationNumber(), re);
			}
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public void cancelEditNote() {
		applicationNote = null;
		noteModify = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	/*
	 * PBR EU Status change related.
	 */

	public String startEditPbrEuStatus() {
		enterEditMode();
		return null;
	}

	public void saveEditPbrEuStatus() {
		boolean ok = true;
		try {
			leaveEditMode();
			getPermitService().modifyEU(selectedPermitEU, getUserID());
			DisplayUtil.displayInfo("The EU Permit Status has been updated.");
		} catch (RemoteException re) {
			logger.error("Exception calling modifyEU for permit "
					+ application.getApplicationNumber()
					+ " and EU with correlation Id "
					+ selectedPermitEU.getCorrEpaEMUID());
			handleException(
					"exception in application "
							+ application.getApplicationNumber(), re);
			ok = false;
		} finally {
			// reloadApplication(); // assume we do not need to read application
			// again...
		}
		if (ok) {
			PermitEU permitEu = selectedPermitEU;
			selectedPermitEU = null;
			try { // relocate the permit EU
				Permit permit = getPermitService().retrievePermit(
						application.getApplicationNumber());
				/*
				 * Remove PBR references in permit applications if (permit !=
				 * null && PermitTypeDef.PBR.equals(permit.getPermitType())) {
				 * for (PermitEU eu : permit.getEus()) { if
				 * (eu.getCorrEpaEMUID().equals(
				 * permitEu.getFpEU().getCorrEpaEmuId())) { selectedPermitEU =
				 * eu; break; } } }
				 */
			} catch (RemoteException e) {
				logger.error(
						"Exception retrieving permit "
								+ application.getApplicationNumber(), e);
				ok = false;
			}
		}
	}

	public void cancelEditPbrEuStatus() {
		leaveEditMode();
	}

	/*
	 * Actions - Submit related
	 */
	public String startSubmitApplication() {
		return APP_SUBMIT_DIALOG;
	}

	public void applySubmit(ActionEvent actionEvent) {
	/*	UIComponent uiComponent = actionEvent.getComponent();
		if (uiComponent instanceof CoreCommandButton) {
			CoreCommandButton button = (CoreCommandButton)uiComponent;
			button.setDisabled(true);
		}*/
		
		if(!firstButtonClick()) { // protect from multiple clicks
			return;
        }
		
		if (null != application) {
			reloadApplicationWithAllEUs();
		}
        
		try {
			List<ValidationMessage> msgs = getApplicationService()
					.submitApplication(application, getUserID(),
							new Timestamp(System.currentTimeMillis()));
			for (ValidationMessage msg : msgs) {
				if (msg.getSeverity().equals(ValidationMessage.Severity.INFO)) {
					DisplayUtil.displayInfo(msg.getMessage());
				} else if (msg.getSeverity().equals(
						ValidationMessage.Severity.WARNING)) {
					DisplayUtil.displayWarning(msg.getMessage());
				} else if (msg.getSeverity().equals(
						ValidationMessage.Severity.ERROR)) {
					DisplayUtil.displayError(msg.getMessage());
				}
			}
		} catch (RemoteException re) {
			handleException(
					"exception in application "
							+ application.getApplicationNumber(), re);
		} finally {
			clearButtonClicked();
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	/**
	 * Bogus submit version used for testing
	 * 
	 * @return
	 */
	public String testSubmit() {
		try {
			application.setValidated(true);
			List<ValidationMessage> msgs = getApplicationService()
					.submitApplication(application, getUserID(),
							new Timestamp(System.currentTimeMillis()));
			for (ValidationMessage msg : msgs) {
				if (msg.getSeverity().equals(ValidationMessage.Severity.INFO)) {
					DisplayUtil.displayInfo(msg.getMessage());
				} else if (msg.getSeverity().equals(
						ValidationMessage.Severity.WARNING)) {
					DisplayUtil.displayWarning(msg.getMessage());
				} else if (msg.getSeverity().equals(
						ValidationMessage.Severity.ERROR)) {
					DisplayUtil.displayError(msg.getMessage());
				}
			}
		} catch (RemoteException re) {
			handleException(
					"exception in application "
							+ application.getApplicationNumber(), re);
		} finally {
			reloadApplicationWithAllEUs();
		}
		return null;
	}

	public void cancelSubmit() {		
		FacesUtil.returnFromDialogAndRefresh();
	}

	/*
	 * Actions - Emissions related (PTIO)
	 */
	public String startAddEmissionsInfo() {
		emissionsModify = false;
		emissionsDialogOpen = true;
		emissions = new ApplicationEUEmissions();
		emissions.setApplicationEuId(selectedEU.getApplicationEuId());
		return PTIO_EMISSIONS_DIALOG;
	}

	public String startEditEmissions() {
		if (pollutantType.equals(POLLUTANT_CAP)) {
			emissions = (ApplicationEUEmissions) FacesUtil
					.getManagedBean("capEmissions");
		} else if (pollutantType.equals(POLLUTANT_HAP_TAC)) {
			emissions = (ApplicationEUEmissions) FacesUtil
					.getManagedBean("hapTacEmissions");
		} else if (pollutantType.equals(POLLUTANT_GHG)) {
			emissions = (ApplicationEUEmissions) FacesUtil
					.getManagedBean("ghgEmissions");
		} else if (pollutantType.equals(POLLUTANT_OTH)) {
			emissions = (ApplicationEUEmissions) FacesUtil
					.getManagedBean("othEmissions");
		}
		emissionsModify = true;
		emissionsDialogOpen = true;
		return PTIO_EMISSIONS_DIALOG;
	}

	public void cancelEditEmissions() {
		emissions = null;
		emissionsModify = false;
		emissionsDialogOpen = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void applyEditEmissions(ActionEvent actionEvent) {
		ValidationMessage[] validationMessages = emissions.validate();
		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}
		// set emissions to CAP by default
		List<ApplicationEUEmissions> euEmissions = ((PTIOApplicationEU) selectedEU)
				.getCapEmissions();
		if (pollutantType.equals(POLLUTANT_HAP_TAC)) {
			euEmissions = ((PTIOApplicationEU) selectedEU).getHapTacEmissions();
			emissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.HAP_TABLE_CD);
		} else if (pollutantType.equals(POLLUTANT_OTH)) {
			euEmissions = ((PTIOApplicationEU) selectedEU).getOthEmissions();
			emissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.OTH_TABLE_CD);
		} else if (pollutantType.equals(POLLUTANT_GHG)) {
			euEmissions = ((PTIOApplicationEU) selectedEU).getGhgEmissions();
			emissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.GHG_TABLE_CD);
			try {
				BigDecimal co2e = new BigDecimal("0");
				try {
					co2e = PollutantDef.computeBigDecimalCO2Equivalent(
							emissions.getPollutantCd(),
							emissions.getPotentialToEmitTonYr());
				} catch (NumberFormatException e) {
					logger.error("Invalid value for CO2 Equivalent", e);
				}
				emissions.setCo2Equivalent(getApplicationService()
						.getEmissionBigDecimalValueAsString(
								emissions.getPollutantCd(),
								emissions.getEuEmissionTableCd(), co2e));
			} catch (NumberFormatException e) {
				logger.error(
						"Exception calculating CO2 equivalent value for pollutant="
								+ emissions.getPollutantCd() + ", PTE value="
								+ emissions.getPotentialToEmitTonYr()
								+ " for application "
								+ application.getApplicationNumber(), e);
			} catch (RemoteException e) {
				logger.error(
						"Exception calculating CO2 equivalent value for pollutant="
								+ emissions.getPollutantCd() + ", PTE value="
								+ emissions.getPotentialToEmitTonYr()
								+ " for application "
								+ application.getApplicationNumber(), e);
			}
		}

		if (emissionsModify == false) {
			// new emissions info being added to list of pollutants
			euEmissions.add(emissions);
		} else {
			// find emissions object under edit and update it with new
			// information
			for (ApplicationEUEmissions matchEmissions : euEmissions) {
				if (matchEmissions.getPollutantCd().equals(
						emissions.getPollutantCd())) {
					matchEmissions = new ApplicationEUEmissions(emissions);
					break;
				}
			}
		}
		emissionsModify = false;
		emissionsDialogOpen = false;
		emissions = null;
		refreshAttachments();
		FacesUtil.returnFromDialogAndRefresh();
	}

	/*
	 * Actions - Emissions related (TV)
	 */
	/**
	 * This method may be used to add a row directly to the emissions table
	 * (instead of using a popup). It is currently not being used.
	 */
	public String addTVEmissionsInfo() {
		emissionsModify = false;
		tvEmissions = new TVApplicationEUEmissions();
		tvEmissions.setApplicationEuId(selectedEU.getApplicationEuId());
		tvEmissions.setTvEuOperatingScenarioId(selectedScenario
				.getTvEuOperatingScenarioId());

		// add new row to appropriate table
		List<TVApplicationEUEmissions> euEmissions = null;
		if (pollutantType.equals(POLLUTANT_HAP_TAC)) {
			euEmissions = selectedScenario.getHapEmissions();
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.HAP_TABLE_CD);
		} else if (pollutantType.equals(POLLUTANT_CAP)) {
			euEmissions = selectedScenario.getCapEmissions();
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.CAP_TABLE_CD);
		} else if (pollutantType.equals(POLLUTANT_GHG)) {
			euEmissions = selectedScenario.getGhgEmissions();
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.GHG_TABLE_CD);
		} else if (pollutantType.equals(POLLUTANT_OTH)) {
			euEmissions = selectedScenario.getOthEmissions();
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.OTH_TABLE_CD);
		}
		euEmissions.add(tvEmissions);

		return null;
	}

	public String startAddTVEmissionsInfo() {
		emissionsModify = false;
		emissionsDialogOpen = true;
		tvEmissions = new TVApplicationEUEmissions();
		tvEmissions.setApplicationEuId(selectedEU.getApplicationEuId());
		tvEmissions.setTvEuOperatingScenarioId(selectedScenario
				.getTvEuOperatingScenarioId());
		if (pollutantType.equals(POLLUTANT_CAP)) {
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.CAP_TABLE_CD);
		} else {
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.HAP_TABLE_CD);
		}
		// initialize variables for rule cite portion of popup
		tvRuleCiteTypeCd = null;
		tvRuleCiteCd = null;
		return TV_EMISSIONS_DIALOG;
	}

	public String startEditTVEmissions() {
		if (pollutantType.equals(POLLUTANT_CAP)) {
			tvEmissions = (TVApplicationEUEmissions) FacesUtil
					.getManagedBean("tvCapEmissions");
		} else if (pollutantType.equals(POLLUTANT_HAP_TAC)) {
			tvEmissions = (TVApplicationEUEmissions) FacesUtil
					.getManagedBean("tvHapEmissions");
		} else if (pollutantType.equals(POLLUTANT_GHG)) {
			tvEmissions = (TVApplicationEUEmissions) FacesUtil
					.getManagedBean("ghgEmissions");
		} else if (pollutantType.equals(POLLUTANT_OTH)) {
			tvEmissions = (TVApplicationEUEmissions) FacesUtil
					.getManagedBean("othEmissions");
		}

		emissionsModify = true;
		emissionsDialogOpen = true;
		return TV_EMISSIONS_DIALOG;
	}

	public void cancelEditTVEmissions() {
		tvEmissions = null;
		emissionsModify = false;
		emissionsDialogOpen = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void applyEditTVEmissions(ActionEvent actionEvent) {
		// make sure a pollutant is specified
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		tvEmissions.requiredField(tvEmissions.getPollutantCd(), "pollutantCd",
				"Pollutant", "pollutantChoice", validationMessages);
		tvEmissions.requiredField(tvEmissions.getPteTonsYr(), "pte",
				"PTE (ton/year)", "pteText", validationMessages);
		tvEmissions.requiredField(tvEmissions.getPteDeterminationBasis(),
				"AppBasisForDeterminationDropdown", "Basis For Determination",
				"AppBasisForDeterminationDropdown", validationMessages);
		try {
			if (tvEmissions.getPteTonsYr() != null
					&& getApplicationService().getEmissionValueAsBigDecimal(
							tvEmissions.getPollutantCd(),
							tvEmissions.getEuEmissionTableCd(),
							tvEmissions.getPteTonsYr()).compareTo(
							new BigDecimal("0")) <= 0) {
				validationMessages.add(new ValidationMessage("pte",
						"PTE (ton/year) must be greater than zero.",
						ValidationMessage.Severity.ERROR, "pteText"));

			}
		} catch (RemoteException e1) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e1);
		}
		if (validationMessages.size() > 0) {
			if (displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0])) == true) {
				return;
			}
		}
		List<TVApplicationEUEmissions> euEmissions = null;
		if (pollutantType.equals(POLLUTANT_HAP_TAC)) {
			euEmissions = selectedScenario.getHapEmissions();
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.HAP_TABLE_CD);
		} else if (pollutantType.equals(POLLUTANT_CAP)) {
			euEmissions = selectedScenario.getCapEmissions();
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.CAP_TABLE_CD);
		} else if (pollutantType.equals(POLLUTANT_OTH)) {
			euEmissions = selectedScenario.getOthEmissions();
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.OTH_TABLE_CD);
		} else if (pollutantType.equals(POLLUTANT_GHG)) {
			euEmissions = selectedScenario.getGhgEmissions();
			tvEmissions
					.setEuEmissionTableCd(ApplicationEUEmissionTableDef.GHG_TABLE_CD);
			try {
				BigDecimal co2e = new BigDecimal("0");
				try {
					co2e = PollutantDef.computeBigDecimalCO2Equivalent(
							tvEmissions.getPollutantCd(),
							tvEmissions.getPteTonsYr());
				} catch (NumberFormatException e) {
					logger.error("Invalid value for CO2 Equivalent", e);
				}
				tvEmissions.setCo2Equivalent(getApplicationService()
						.getEmissionBigDecimalValueAsString(
								tvEmissions.getPollutantCd(),
								tvEmissions.getEuEmissionTableCd(), co2e));
			} catch (NumberFormatException e) {
				logger.error(
						"Exception calculating CO2 equivalent value for pollutant="
								+ tvEmissions.getPollutantCd() + ", PTE value="
								+ tvEmissions.getPteTonsYr()
								+ " for application "
								+ application.getApplicationNumber(), e);
			} catch (RemoteException e) {
				logger.error(
						"Exception calculating CO2 equivalent value for pollutant="
								+ tvEmissions.getPollutantCd() + ", PTE value="
								+ tvEmissions.getPteTonsYr()
								+ " for application "
								+ application.getApplicationNumber(), e);
			}
		}
		if (emissionsModify == false) {
			// new emissions info being added to list of pollutants
			euEmissions.add(tvEmissions);
			if (euEmissions.size() == 1
					&& pollutantType.equals(POLLUTANT_HAP_TAC)) {
				// appendTVHapsTotals(euEmissions);
			}
		} else {
			// find emissions object under edit and update it with new
			// information
			for (TVApplicationEUEmissions matchEmissions : euEmissions) {
				if (matchEmissions.getPollutantCd().equals(
						tvEmissions.getPollutantCd())) {
					matchEmissions = new TVApplicationEUEmissions(tvEmissions);
					break;
				}
			}
		}
		emissionsModify = false;
		emissionsDialogOpen = false;
		tvEmissions = null; // don't need this object any more
		refreshAttachments();
		FacesUtil.returnFromDialogAndRefresh();
	}

	/*
	 * Actions - TV Alternate Scenario PTE Applicable Requirements
	 */
	public String addAltSenarioReq() {

		try {
			pteRequirement = getApplicationService().createTVAltScenarioPteReq(
					pteRequirement);
			pteRequirement.setApplicationEuId(selectedEU.getApplicationEuId());
			pteRequirement.setTvEuOperatingScenarioId(selectedScenario
					.getTvEuOperatingScenarioId());
			selectedScenario.getPteRequirements().add(pteRequirement);
		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
		}
		return null;
	}

	public String startAddAltSenarioReq() {
		String ret = ALT_SCENARIO_PTE_REQ_DIALOG;
		pteRequirement = new TVAltScenarioPteReq();
		pteRequirement.setApplicationEuId(selectedEU.getApplicationEuId());
		pteRequirement.setTvEuOperatingScenarioId(selectedScenario
				.getTvEuOperatingScenarioId());
		return ret;
	}

	public void cancelEditAltSenarioReq() {
		pteRequirement = null;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void applyEditAltSenarioReq(ActionEvent actionEvent) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		pteRequirement.requiredField(pteRequirement.getPollutantCd(),
				"pollutantCd", "Pollutant", "pollutantChoice",
				validationMessages);
		pteRequirement.requiredField(pteRequirement.getAllowable(),
				"allowable", "Allowable", "allowableText", validationMessages);
		pteRequirement.requiredField(pteRequirement.getEmissionUnitsCd(),
				"emissionUnitsCd", "Units for the Allowable",
				"allowableUnitsChoice", validationMessages);
		pteRequirement.requiredField(pteRequirement.getApplicableReq(),
				"applicableReq", "Applicable Requirement", "applicableReqText",
				validationMessages);

		if (validationMessages.size() > 0) {
			if (displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0])) == true) {
				return;
			}
		}
		try {
			// creater database instance if there is none
			if (pteRequirement.getTvAltScenarioPteReqId() == null) {
				pteRequirement = getApplicationService()
						.createTVAltScenarioPteReq(pteRequirement);
			}
			selectedScenario.getPteRequirements().add(pteRequirement);
			pteRequirement = null;
		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	/*
	 * Actions - Applicable Requirements related (TV)
	 */
	public String startAddTVAppReqInfo() {
		String ret = APPLICABLE_REQ_DIALOG;
		try {
			applicableReq = getApplicationService().getTempApplicableReq();
			applicableReq.setApplicationId(application.getApplicationID());
			if (selectedEU != null) {
				applicableReq.setApplicationEuId(selectedEU
						.getApplicationEuId());
			} else if (selectedEUGroup != null) {
				applicableReq.setTvEuGroupId(selectedEUGroup.getTvEuGroupId());
			}
		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
			ret = null;
		}
		appReqModify = false;
		return ret;
	}

	public String startEditTVAppReq() {
		String ret = null;
		List<Object> selectedList = getSelectedActionTableRows();
		if (selectedList.size() == 1) {
			applicableReq = (TVApplicableReq) selectedList.get(0);
			appReqModify = true;
			ret = APPLICABLE_REQ_DIALOG;
		}
		return ret;
	}

	public String startCopyTVAppReq() {
		String ret = null;
		TVApplicableReq selectedApplicableReq = null;
		List<Object> selectedList = getSelectedActionTableRows();
		if (selectedList.size() == 1) {
			selectedApplicableReq = (TVApplicableReq) selectedList.get(0);
			try {
				applicableReq = getApplicationService().getApplicableReqCopy(
						selectedApplicableReq);
				appReqModify = false;
				ret = APPLICABLE_REQ_DIALOG;
			} catch (RemoteException re) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), re);
				ret = null;
			}
		}
		return ret;
	}

	public String startViewTVAppReq() {
		applicableReq = (TVApplicableReq) FacesUtil
				.getManagedBean(appReqTableId);
		appReqModify = true;
		return APPLICABLE_REQ_DIALOG;
	}

	public void cancelEditTVAppReq() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void applyEditTVAppReq(ActionEvent actionEvent) {
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		applicableReq.requiredField(applicableReq.getTvRuleCiteTypeCd(),
				"tvRuleCiteTypeCd", "OAC/ORC Rules", "ruleCiteTypeChoice",
				validationMessages);

		// make sure a rule cite or permit cite is listed for all
		// requirements with a value
		if (applicableReq.getAllowableValue() != null
				&& applicableReq.getAllowableRuleCiteCd() == null) {
			ValidationMessage msg = new ValidationMessage("allowableValue",
					"Please specify a Rule Cite value for the Allowable Limit",
					Severity.ERROR, "allowableLimitText");
			validationMessages.add(msg);
		}
		if (applicableReq.getMonitoringValue() != null
				&& applicableReq.getMonitoringRuleCiteCd() == null
				&& applicableReq.getMonitoringPermitCite() == null) {
			ValidationMessage msg = new ValidationMessage(
					"monitoringValue",
					"Please specify a Rule Cite or Permit Cite value for the Monitoring Requirement",
					Severity.ERROR, "monitoringReqText");
			validationMessages.add(msg);
		}
		if (applicableReq.getRecordKeepingValue() != null
				&& applicableReq.getRecordKeepingRuleCiteCd() == null
				&& applicableReq.getRecordKeepingPermitCite() == null) {
			ValidationMessage msg = new ValidationMessage(
					"recordKeepingValue",
					"Please specify a Rule Cite or Permit Cite value for the Record Keeping Requirement",
					Severity.ERROR, "recordKeepingReqText");
			validationMessages.add(msg);
		}
		if (applicableReq.getTvCompRptFreqCd() != null
				&& applicableReq.getReportingRuleCiteCd() == null
				&& applicableReq.getReportingPermitCite() == null) {
			ValidationMessage msg = new ValidationMessage(
					"tvCompRptFreqCd",
					"Please specify a Rule Cite or Permit Cite value for the Reporting Requirement",
					Severity.ERROR, "reportingReqChoice");
			validationMessages.add(msg);
		}
		if (applicableReq.getTestingValue() != null
				&& applicableReq.getTestingRuleCiteCd() == null
				&& applicableReq.getTestingPermitCite() == null) {
			ValidationMessage msg = new ValidationMessage(
					"testingValue",
					"Please specify a Rule Cite or Permit Cite value for the Testing Requirement",
					Severity.ERROR, "testingReqText");
			validationMessages.add(msg);
		}

		if (applicableReq.getAllowableValue() == null
				&& applicableReq.getMonitoringValue() == null
				&& applicableReq.getRecordKeepingValue() == null
				&& applicableReq.getTvCompRptFreqCd() == null
				&& applicableReq.getTestingValue() == null) {
			ValidationMessage msg = new ValidationMessage("allowableValue",
					"Please specify a value for Allowable, Monitoring, Record Keeping, "
							+ "Reporting, or Testing.", Severity.ERROR,
					"allowableLimitText");
			validationMessages.add(msg);
		} else if (applicableReq.getAllowableValue() == null) {
			DisplayUtil.displayWarning("Allowable Limit is not specified");
		}

		if (validationMessages.size() > 0) {
			displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0]));
		} else {
			List<TVApplicableReq> applicableReqs = null;
			if (appReqTableId != null
					&& appReqTableId.equals("tvApplicableReq")) {
				applicableReqs = applicableRequirements;
				applicableReq.setStateOnly(false);
			} else {
				applicableReqs = stateOnlyRequirements;
				applicableReq.setStateOnly(true);
			}
			if (appReqModify == false) {
				// new applicable requirements info being added
				applicableReqs.add(applicableReq);
			} else {
				// find TVApplicableReq object under edit and update it with new
				// information
				for (TVApplicableReq matchReq : applicableReqs) {
					if (matchReq.getTvApplicableReqId().equals(
							applicableReq.getTvApplicableReqId())) {
						matchReq = new TVApplicableReq(applicableReq);
						break;
					}
				}
			}
			appReqModify = false;
			applicableReq = null;
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	/*
	 * Private functions
	 */
	@SuppressWarnings("unchecked")
	private void createTree() {
		ArrayList<String> treePath = new ArrayList<String>();
		String applicationType = ApplicationTypeDef.getData().getItems()
				.getItemDesc(application.getApplicationTypeCD());

		// add root node for the whole facility
		Stars2TreeNode facNode = new Stars2TreeNode(APPLICATION_NODE_TYPE,
				applicationType, "", false, application);
		treePath.add("0");

		// add EU Groups for Title V applications
		if (application instanceof TVApplication) {
			euGroupNum = 0;
			for (TVEUGroup euGroup : ((TVApplication) application)
					.getEuGroups()) {
				String branchPathString = "0:" + Integer.toString(euGroupNum);
				Stars2TreeNode euGroupNode = new Stars2TreeNode(
						EU_GROUP_NODE_TYPE, euGroup.getTvEuGroupName(),
						Integer.toString(euGroup.getTvEuGroupId()), true,
						euGroup);
				treePath.add(branchPathString);

				facNode.getChildren().add(euGroupNode);
				euGroupNum++;
			}
		}

		int euNodeNum = euGroupNum;

		// sort EUs by EpaEmuId so they'll appear in order in the tree
		List<EmissionUnit> sortedEUs = getSortedEUList(true);

		// identify label of selected EU node (if any EU is selected)
		String selectedNodeDesc = null;
		if (selectedTreeNode != null) {
			if (selectedTreeNode.getType().equals(EU_NODE_TYPE)
					|| selectedTreeNode.getType().equals(INSIG_EU_NODE_TYPE)
					|| selectedTreeNode.getType().equals(EXCLUDED_EU_NODE_TYPE)
					|| selectedTreeNode.getType().equals(
							NOT_INCLUDABLE_EU_NODE_TYPE)) {
				selectedNodeDesc = selectedTreeNode.getDescription();
			} else if (selectedEU != null) {
				selectedNodeDesc = selectedEU.getFpEU().getEpaEmuId();
			} else if (selectedExcludedEU != null) {
				selectedNodeDesc = selectedExcludedEU.getEpaEmuId();
			}
		}

		// add tree nodes for emission units
		int bypassCount = 0;
		for (EmissionUnit fpEU : sortedEUs) {
			if (!moreNodesSelected && selectedNodeDesc != null) {
				if (fpEU.getEpaEmuId().compareTo(selectedNodeDesc) < 0) {
					// skip all EUs before the selected EU
					bypassCount++;
					continue;
				}
				if (bypassCount > 0) {
					// draw "moreNodes" node in place of skipped EUs
					treePath.add("0:" + Integer.toString(euNodeNum));
					euNodeNum++;
					Stars2TreeNode dummyNode = new Stars2TreeNode("moreNodes",
							" ", "More Nodes", true, " ");
					facNode.getChildren().add(dummyNode);
					bypassCount = 0;
				}
			}
			// create regular EU node for selected EU and those below it
			Stars2TreeNode euNode = addEuBranch(fpEU, euNodeNum, treePath, "0");
			if (euNode != null) {
				euNodeNum++;
				facNode.getChildren().add(euNode);
			}
		}

		TreeStateBase treeState = new TreeStateBase();
		treeState.expandPath(treePath.toArray(new String[0]));
		treeData = new TreeModelBase(facNode);
		treeData.setTreeState(treeState);
		if (selectedTreeNode == null) {
			selectedTreeNode = facNode;
			setSelectedEU(null);
			selectedPermitEU = null;
			selectedExcludedEU = null;
		} else {
			String nodeType = selectedTreeNode.getType();
			String nodeId = selectedTreeNode.getIdentifier();
			// update selectedTreeNode since tree has been recreated.
			if (nodeType.equals(APPLICATION_NODE_TYPE)) {
				selectedTreeNode = facNode;
			} else if (!nodeType.equals(EU_SHUTTLE_NODE_TYPE)
					&& !nodeType.equals(EU_COPY_NODE_TYPE)
					&& !nodeType.equals("moreNodes")) {
				selectedTreeNode = facNode.findNode(nodeType, nodeId);
				if (selectedTreeNode == null) {
					logger.error("No node found in application tree of type "
							+ nodeType + " with id=" + nodeId);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<EmissionUnit> getSortedEUList(boolean showNotIncludable) {
		HashMap<Integer, ApplicationEU> aeus = new HashMap<Integer, ApplicationEU>();
		for (ApplicationEU ae : application.getEus()) {
			aeus.put(ae.getFpEU().getCorrEpaEmuId(), ae);
		}
		List<EmissionUnit> sortedEUs = new ArrayList<EmissionUnit>();
		if ((application instanceof RPRRequest
				|| application instanceof RPERequest || application instanceof RPCRequest)) {
			if (permitInfo != null) {
				List<PermitEUGroup> egs = permitInfo.getPermit().getEuGroups();
				for (PermitEUGroup g : egs) {
					for (PermitEU pe : g.getPermitEUs()) {
						ApplicationEU ae = aeus.get(pe.getCorrEpaEMUID());
						// if it is a RPR or RPC
						// This eu should not be show on the profile.
						if (application instanceof RPERequest) {
							continue;
						}
						// if eu is included in app or the eu in permit is
						// active
						if (ae != null
								&& (showNotIncludable || !ae.isNotIncludable())) {
							EmissionUnit currentEU = application
									.getFacility()
									.getEmissionUnit(pe.getFpEU().getEpaEmuId());
							if (currentEU != null) {
								sortedEUs.add(currentEU);
							} else {
								logger.error("PermitEU "
										+ pe.getFpEU().getEpaEmuId()
										+ " not found in current facility inventory for facility "
										+ application.getFacility()
												.getFacilityId()
										+ ". EU will not be included in application "
										+ application.getApplicationNumber());
							}
						}
					}
				}
				// add remaining EUs from facility for Off permit change request
				if (application instanceof RPCRequest
						&& RPCTypeDef.TV_OFF_PERMIT_CHANGE
								.equals(((RPCRequest) application)
										.getRpcTypeCd())) {
					for (EmissionUnit fpEU : application.getFacility()
							.getEmissionUnits()) {
						if (!sortedEUs.contains(fpEU)) {
							sortedEUs.add(fpEU);
						}
					}
				}
			} else {
				logger.error("No permit information found for permit associated with request "
						+ application.getApplicationNumber());
			}
		} else {
			sortedEUs = application.getFacility().getEmissionUnits();
		}

		Collections.sort(sortedEUs, new Comparator() {
			public int compare(Object o1, Object o2) {
				return (((EmissionUnit) o1).getEpaEmuId()
						.compareTo(((EmissionUnit) o2).getEpaEmuId()));
			}

		});
		return sortedEUs;
	}

	@SuppressWarnings("unchecked")
	private Stars2TreeNode addEuBranch(EmissionUnit fpEU, int euNodeNum,
			List<String> treePath, String branchPathString) {
		Stars2TreeNode euNode = null;
		ApplicationEU appEU;
		// don't exclude EUs for legacy State applications
		boolean bypassEUExclusion = (application instanceof PTIOApplication && application
				.isLegacy()) || (application instanceof RPRRequest);
		// don't include shutdown, invalid or trivial EUs in the tree
		if (!bypassEUExclusion
				&& (fpEU.getEuShutdownDate() != null
						|| fpEU.getOperatingStatusCd().equals(
								EuOperatingStatusDef.IV)
						|| fpEU.getOperatingStatusCd().equals(
								EuOperatingStatusDef.SD)
						|| TVClassification.TRIVIAL.equals(fpEU.getTvClassCd()) || TVClassification.INSIG_NO_APP_REQS
							.equals(fpEU.getTvClassCd()))) {
			return null;
		}

		// figure out what kind of EU this is, then add it to the tree
		treePath.add(branchPathString + ":" + Integer.toString(euNodeNum));
		if ((appEU = findApplicationEU(fpEU.getCorrEpaEmuId(), true)) != null) {
			if (appEU.isNotIncludable()) {
				// EU cannot be included in application
				euNode = new Stars2TreeNode(NOT_INCLUDABLE_EU_NODE_TYPE,
						fpEU.getEpaEmuId(), Integer.toString(appEU
								.getApplicationEuId()), true, appEU);
			} else if (appEU.isExcluded()) {
				// EU not currently included in application
				euNode = new Stars2TreeNode(EXCLUDED_EU_NODE_TYPE,
						fpEU.getEpaEmuId(), Integer.toString(fpEU
								.getCorrEpaEmuId()), true, fpEU);
			} else {
				// EU is included in the application

				// set node type for insignificant/non-insignificant eus
				String euNodeType = EU_NODE_TYPE;
				if (appEU.getFpEU().getTvClassCd() != null
						&& appEU instanceof TVApplicationEU
						&& appEU.getFpEU().getTvClassCd()
								.equals(TVClassification.INSIG)) {
					// set node type to insignificant for IEUs in a Title V
					// application
					// This designation has no meaning in PTIO or other
					// applications
					euNodeType = INSIG_EU_NODE_TYPE;
				}

				if (appEU instanceof TVApplicationEU) {
					// TV EU node
					euNode = new Stars2TreeNode(
							euNodeType,
							fpEU.getEpaEmuId(),
							Integer.toString(appEU.getApplicationEuId()),
							((TVApplicationEU) appEU)
									.getAlternateOperatingScenarios().size() == 0,
							appEU);

					// create nodes for scenarios
					int scenarioNodeNum = 0;
					for (TVEUOperatingScenario scenario : ((TVApplicationEU) appEU)
							.getAlternateOperatingScenarios()) {
						// Note: application EU Id is included in scenario node
						// id to ensure then node id is unique.
						Stars2TreeNode scenarioNode = new Stars2TreeNode(
								ALT_SCENARIO_NODE_TYPE,
								scenario.getTvEuOperatingScenarioNm(),
								scenario.getApplicationEuId() + ":"
										+ scenario.getTvEuOperatingScenarioId(),
								true, scenario);
						treePath.add(branchPathString + ":" + euNodeNum + ":"
								+ scenarioNodeNum++);
						euNode.getChildren().add(scenarioNode);
					}
				} else {
					// non-TV EU node
					euNode = new Stars2TreeNode(euNodeType, fpEU.getEpaEmuId(),
							Integer.toString(appEU.getApplicationEuId()), true,
							appEU);
				}
			}
		} else {
			// EU not currently included in application
			euNode = new Stars2TreeNode(EXCLUDED_EU_NODE_TYPE,
					fpEU.getEpaEmuId(),
					Integer.toString(fpEU.getCorrEpaEmuId()), true, fpEU);
		}
		return euNode;
	}

	// support for EU shuttle
	public List<Integer> getIncludedEUs() {
		ArrayList<Integer> includedEUs = new ArrayList<Integer>();
		TreeMap<String, Integer> sortedEUMap = new TreeMap<String, Integer>();
		for (ApplicationEU appEU : application.getIncludedEus()) {
			sortedEUMap.put(appEU.getFpEU().getEpaEmuId(), appEU.getFpEU()
					.getCorrEpaEmuId());
		}
		includedEUs.addAll(sortedEUMap.values());
		return includedEUs;
	}

	public void setIncludedEUs(List<Integer> eus) {
		// all EUs included added could potentially be removed
		List<ApplicationEU> includedEUs = application.getIncludedEus();
		eusToRemove = new HashSet<ApplicationEU>(includedEUs);
		eusToAdd.clear();

		for (Integer corrId : eus) {
			boolean noChangeForEU = false;
			for (ApplicationEU appEU : includedEUs) {
				if (appEU.getFpEU().getCorrEpaEmuId().equals(corrId)) {
					// EU is already included, remove it from eusToRemove
					// and set the "no change" flag
					eusToRemove.remove(appEU);
					noChangeForEU = true;
					break;
				}
			}
			if (!noChangeForEU) {
				eusToAdd.add(corrId);
			}
		}
	}

	public String copyApplicationEUData() {
		try {
			for (Integer targetEuId : this.eusTargetedForCopy) {
				ApplicationEU targetEu;
				ApplicationService appBO = getApplicationService();
				targetEu = appBO.retrieveApplicationEU(targetEuId);
				targetEu = appBO.copyApplicationEUData(selectedEU, targetEu);
			}
			DisplayUtil.displayInfo("EU data has been successfully copied.");
		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
		} finally {
			reloadApplicationSummary();
		}
		return null;
	}

	public String updateApplicationEUs() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		
		try {
			ApplicationService appBO = getApplicationService();

			if (eusToAdd != null && !eusToAdd.isEmpty()) {
				// add eus that are not already present in application
				for (Integer corrId : eusToAdd) {
					// find a matching EU in the list of EUs
					ApplicationEU appEu = findApplicationEU(corrId, true);
					if (appEu == null) {
						for (EmissionUnit fpEU : application.getFacility()
								.getEmissionUnits()) {
							if (fpEU.getCorrEpaEmuId().equals(corrId)) {
								appEu = getApplicationService()
										.getNewApplicationEU(application, fpEU);
							}
						}
					}
					
					if (appEu instanceof TVApplicationEU) {
						TVApplication tvApp = (TVApplication) application;
						try {
							appBO.verifyTVAppRequiredAttachments(tvApp);
						} catch (Exception e) {
							DisplayUtil
									.displayError("There is an error when the system was processing the generation attachment. Error: "
											+ e.getMessage());
							logger.error(e);
						}
					}

					appEu = appBO.retrieveApplicationEU(appEu
							.getApplicationEuId());
					appEu.setExcluded(false);
					appBO.modifyApplicationEU(appEu);
					// populate EU with data from euToCopy if it's not null
					if (euToCopy != null) {
						// retrieve appEu from database to be sure it's in synch
						appEu = appBO.retrieveApplicationEU(appEu
								.getApplicationEuId());
						ApplicationEU euToCopyLocal = findApplicationEU(
								euToCopy.getFpEU().getCorrEpaEmuId(), true);
						// retrieve euToCopy from database to be sure it's in
						// synch
						euToCopyLocal = appBO
								.retrieveApplicationEU(euToCopyLocal
										.getApplicationEuId());
						String copyErrMsg = okToCopyEU(euToCopyLocal, appEu);
						if (copyErrMsg == null) {
							appEu = appBO.copyApplicationEUData(euToCopyLocal,
									appEu);
						} else {
							DisplayUtil.displayWarning(copyErrMsg);
						}
					}
				}
			}
			// clear out collection of EUs to add
			eusToAdd.clear();

			if (eusToRemove != null && !eusToRemove.isEmpty()) {
				// remove EUs that should no longer be included
				for (ApplicationEU appEU : eusToRemove) {
					try {
						ApplicationEU applicationEu = appBO
								.retrieveApplicationEU(appEU
										.getApplicationEuId());
						applicationEu.setExcluded(true);
						if (applicationEu instanceof TVApplicationEU) {
							appBO.checkAndRemoveCAMPlan(application,
									applicationEu);
						}
						appBO.modifyApplicationEU(applicationEu);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			// clear out collection of EUs to add
			eusToRemove.clear();
			// clear out euToCopy
			euToCopy = null;

			// changes to EUs may make application invalid.
			// Note: Admin changes to submitted app should not alter validated
			// flag
			if (!isStars2Admin() || application.getSubmittedDate() == null) {
				appBO.setValidatedFlag(application, false);
			}

			DisplayUtil
					.displayInfo("The set of EUs included in this application has been updated");

		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
		} finally {
			// load application to redraw tree
			reloadApplicationSummary();
			clearButtonClicked();
		}

		return null;
	}	

	public String cancelEUShuttle() {
		selectedTreeNode = null;
		reloadApplicationSummary();
		return APP_DETAIL_MANAGED_BEAN;
	}

	public List<SelectItem> getAvailableEUs() {
		List<SelectItem> availableEUs = new ArrayList<SelectItem>();
		for (EmissionUnit fpEU : getSortedEUList(false)) {
			if (application instanceof RPRRequest
					|| !(fpEU.getEuShutdownDate() != null
							|| fpEU.getOperatingStatusCd().equals(
									EuOperatingStatusDef.IV)
							|| fpEU.getOperatingStatusCd().equals(
									EuOperatingStatusDef.SD)
							|| TVClassification.TRIVIAL.equals(fpEU
									.getTvClassCd()) || TVClassification.INSIG_NO_APP_REQS
								.equals(fpEU.getTvClassCd()))) {
				availableEUs.add(new SelectItem(fpEU.getCorrEpaEmuId(), fpEU
						.getEpaEmuId()));
			}
		}
		return availableEUs;
	}

	public List<SelectItem> getTvEusAvailableForGroup() {
		List<SelectItem> availableEUs = new ArrayList<SelectItem>();
		if (application instanceof TVApplication) {
			for (ApplicationEU appEU : application.getIncludedEus()) {
				availableEUs.add(new SelectItem(appEU.getFpEU()
						.getCorrEpaEmuId(), appEU.getFpEU().getEpaEmuId()));
			}
		}
		return availableEUs;
	}

	// support for TV EU Group shuttle
	public List<Integer> getTvEusInGroup() {
		ArrayList<Integer> includedEUs = new ArrayList<Integer>();
		if (selectedEUGroup != null) {
			// sort EUs by EPA Emu Id
			TreeMap<String, TVApplicationEU> sortedEUMap = new TreeMap<String, TVApplicationEU>();
			for (TVApplicationEU eu : selectedEUGroup.getEus()) {
				sortedEUMap.put(eu.getFpEU().getEpaEmuId(), eu);
			}
			for (String epaEmuId : sortedEUMap.keySet()) {
				TVApplicationEU eu = sortedEUMap.get(epaEmuId);
				includedEUs.add(eu.getFpEU().getCorrEpaEmuId());
			}
		}
		return includedEUs;
	}

	public List<EmissionUnit> getTvEusObjectsInGroup() {
		ArrayList<EmissionUnit> includedEUs = new ArrayList<EmissionUnit>();
		if (selectedEUGroup != null) {
			// sort EUs by EPA Emu Id
			TreeMap<String, TVApplicationEU> sortedEUMap = new TreeMap<String, TVApplicationEU>();
			for (TVApplicationEU eu : selectedEUGroup.getEus()) {
				sortedEUMap.put(eu.getFpEU().getEpaEmuId(), eu);
			}
			for (String epaEmuId : sortedEUMap.keySet()) {
				TVApplicationEU eu = sortedEUMap.get(epaEmuId);
				includedEUs.add(eu.getFpEU());
			}
		}
		return includedEUs;
	}

	public void setTvEusInGroup(List<Integer> eus) {
		selectedEUGroup.getEus().clear();
		for (Integer corrId : eus) {
			ApplicationEU appEU = findApplicationEU(corrId, false);
			if (appEU != null) {
				selectedEUGroup.getEus().add((TVApplicationEU) appEU);
			}
		}
	}

	public List<Integer> getEusTargetedForCopy() {
		return this.eusTargetedForCopy;
	}

	public void setEusTargetedForCopy(List<Integer> eusTargetedForCopy) {
		this.eusTargetedForCopy = eusTargetedForCopy;
	}

	public List<SelectItem> getEusAvailableForCopy() {
		List<SelectItem> availableEUs = new ArrayList<SelectItem>();
		eusAvailableForCopy = new ArrayList<ApplicationEU>();
		String targetNodeType = EU_NODE_TYPE; // look for "regular" included EUs
												// by default
		if (application instanceof TVApplication
				&& ((selectedEU != null && TVClassification.INSIG
						.equals(selectedEU.getFpEU().getTvClassCd())) || (selectedExcludedEU != null && TVClassification.INSIG
						.equals(selectedExcludedEU.getTvClassCd())))) {
			targetNodeType = INSIG_EU_NODE_TYPE;
		}

		// iterate over list of EUs to find candidate nodes
		for (ApplicationEU appEU : application.getIncludedEus()) {
			if (INSIG_EU_NODE_TYPE.equals(targetNodeType)
					&& !TVClassification.INSIG.equals(appEU.getFpEU()
							.getTvClassCd())) {
				continue;
			}
			if (selectedEU != null
					&& selectedEU.getApplicationEuId().equals(
							appEU.getApplicationEuId())) {
				// don't include selected EU in the list
				continue;
			}
			availableEUs.add(new SelectItem(appEU.getApplicationEuId(), appEU
					.getFpEU().getEpaEmuId()));
			eusAvailableForCopy.add(appEU);
		}
		return availableEUs;
	}

	public boolean isEuCopyAllowed() {
		return isEuEditAllowed() && getEusAvailableForCopy().size() > 0;
	}

	private ApplicationEU findApplicationEU(int corrId, boolean includeExcluded) {
		ApplicationEU ret = null;
		for (ApplicationEU eu : application.getEus()) {
			if (eu.getFpEU().getCorrEpaEmuId().equals(corrId)
					&& (includeExcluded || !eu.isExcluded())) {
				ret = eu;
				break;
			}
		}
		return ret;
	}

	/**
	 * Load data for the application identified by applicationID. Within the
	 * portal application, this method will always load data from the STAGING
	 * schema.
	 * 
	 * @param applicationID
	 */
/*
	private void loadApplication(int applicationID) {
		try {
			ApplicationService appBO = getApplicationService();
			staging = true;

			if (isInternalApp()) {
				application = appBO.retrieveApplication(applicationID);
			} else {
				application = appBO.retrieveApplication(applicationID, staging);
			}

			loadApplication();
		} catch (RemoteException re) {
			handleException("Exception for application ID " + applicationID, re);
		}
	}
*/

	public void loadApplication(Application app) {  // not used by IMPACT
		/*
		application = app;
		staging = true;
		loadApplication();
		*/
	}

	/**
	 * Load data for the application identified by applicationNumber. Within the
	 * portal application, this method will always load data from the READONLY
	 * schema.
	 * 
	 * @param applicationID
	 */
	/*
	private void loadApplication(String applicationNumber) {
		try {
			ApplicationService appBO = getApplicationService();
			// when retrieving a read-only ITR app from staging, we need to get
			// facility info from My Tasks
			if (!isInternalApp() && readOnly && applicationNumber != null
					&& applicationNumber.startsWith("REL")) {
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				Facility facility = myTasks.getFacility();
				application = appBO.retrieveRelocationApplication(
						applicationNumber, facility);
			} else {
				application = appBO.retrieveApplication(applicationNumber);
			}
			staging = false;
			if (!this.isInternalApp()) {
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_applications"))
						.setRendered(false);
			}
			if (application != null) {
				loadApplication();
			} else {
				DisplayUtil.displayError("No application found with number "
						+ applicationNumber + ".");
			}
		} catch (RemoteException re) {
			handleException("Exception for application " + applicationNumber,
					re);
		}
	}
*/

	/**
	 * Load notes for the application identified by applicationID.
	 * 
	 * @param applicationID
	 */
	private void loadNotes(int applicationID) {
		try {
			ApplicationService appBO = getApplicationService();
			ApplicationNote[] notes = appBO.retrieveApplicationNotes(applicationID);
			application.setApplicationNotes(Arrays.asList(notes));
		} catch (RemoteException re) {
			handleException("Exception for application ID " + applicationID, re);
		}
	}
	
	private EmissionUnitTypeDAO getEUTypeDAO(String typeCd, boolean staging) {
		EmissionUnitTypeDAO dao = null;
		String beanName = buildEUTypeDAOName(typeCd, staging);
		if (App.getApplicationContext().containsBean(beanName)) {
			dao = (EmissionUnitTypeDAO) App.getApplicationContext().getBean(
					beanName);
		} else {
			beanName = buildEUTypeDAOName("Default", staging);
			if (App.getApplicationContext().containsBean(beanName)) {
				dao = (EmissionUnitTypeDAO) App.getApplicationContext().getBean(
						beanName);
			}
		}
		return dao;
	}

	private String buildEUTypeDAOName(String typeCd, boolean staging) {
		String beanName = "EmissionUnitType" + typeCd + "DAO";
		if (staging && CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			beanName = "staging" + beanName;
		} else {
			beanName = "readOnly" + beanName;
		}
		return beanName;
	}
/*	
	private void loadApplication() {
		if (application != null) {
			facilityContacts = null;
			try {
				getApplicationService().setSubmitterUser(application);
				if (isInternalApp() || !isRelocationClass()) {
					// need to retrieve more information for facility EUs
					Facility fac = getFacilityService()
							.retrieveFacilityProfile(
									application.getFacility().getFpId(),
									!isInternalApp() && !readOnly, null);
					fac.setOwner(getFacilityService().retrieveFacilityOwner(
							application.getFacilityId()));

					// must set emission unit type to each EU and
					// ApplicationEU.FpEU
					for (EmissionUnit eu : fac.getEmissionUnits()) {
						if (eu.getEmissionUnitTypeCd() != null
								&& eu.getEmissionUnitTypeCd().length() > 0) {
							EmissionUnitTypeDAO euTypeDAO = getEUTypeDAO(
									eu.getEmissionUnitTypeCd(), staging);
							if (euTypeDAO != null) {
								eu.setEmissionUnitType(euTypeDAO
										.retrieveEmissionUnitType(eu.getEmuId()));
							}
							if (eu.isReplacementType()) {
								eu.setEmissionUnitReplacements(getFacilityService()
										.retrieveEmissionUnitReplacements(
												eu.getEmuId()));
							}
							if (application instanceof PTIOApplication) {
								for (ApplicationEU aeu : application.getEus()) {
									if (eu.getEmuId().equals(
											aeu.getFpEU().getEmuId())) {
										aeu.setFpEU(eu);

										// load facility EU data to EU Type
										if (EmissionUnitTypeDef.EGU.equals(aeu
												.getFpEU()
												.getEmissionUnitTypeCd())) {
											((NSRApplicationEUTypeEGU) ((PTIOApplicationEU) aeu)
													.getEuType())
													.loadFpEuTypeData(aeu
															.getFpEU()
															.getEmissionUnitType());
										}
										if (EmissionUnitTypeDef.CSH.equals(aeu
												.getFpEU()
												.getEmissionUnitTypeCd())) {
											((NSRApplicationEUTypeCSH) ((PTIOApplicationEU) aeu)
													.getEuType())
													.loadFpEuTypeData(aeu
															.getFpEU()
															.getEmissionUnitType());
										}
										if (EmissionUnitTypeDef.TNK.equals(aeu
												.getFpEU()
												.getEmissionUnitTypeCd())) {
											((NSRApplicationEUTypeTNK) ((PTIOApplicationEU) aeu)
													.getEuType())
													.loadFpEuTypeData(aeu
															.getFpEU()
															.getEmissionUnitType());
										}
									}
								}
							}

						}
					}
					application.setFacility(fac);
				}
				// load document data for application attachments
				getApplicationService().loadAllDocuments(application, readOnly);
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}

			loadApplicationData();
			loadEuData();
			createTree();
		} else {
			applicationDeleted = true;
			DisplayUtil.displayWarning("Selected application does not exist. "
					+ "Refresh data and try again.");
		}
		// if ITR or Delegation app then update appropriate managed bean since
		// these
		// are handled outside of this object.
		if (isRelocationClass()) {
			setRelocation();
		} else if (isDelegationClass()) {
			if (isInternalApp()) {
				us.oh.state.epa.stars2.app.delegation.Delegation gi = (us.oh.state.epa.stars2.app.delegation.Delegation) FacesUtil
						.getManagedBean("delegation");
				gi.setDelegationRequest((DelegationRequest) application);
			} else {
				us.oh.state.epa.stars2.portal.delegation.Delegation gi = (us.oh.state.epa.stars2.portal.delegation.Delegation) FacesUtil
						.getManagedBean("delegation");
				gi.setDelegationRequest((DelegationRequest) application);
			}
		}
	}
*/	

	private void loadEuData() {
		if (selectedEU == null) {
			return;
		}

		if (selectedEU instanceof PTIOApplicationEU) {
			// retrieve subparts
			PTIOApplicationEU ptioAppEu = (PTIOApplicationEU) selectedEU;
			nspsSubparts = Stars2Object.toStar2Object(ptioAppEu
					.getNspsSubpartCodes());
			neshapSubparts = Stars2Object.toStar2Object(ptioAppEu
					.getNeshapSubpartCodes());
			mactSubparts = Stars2Object.toStar2Object(ptioAppEu
					.getMactSubpartCodes());
			bactPollutantWrapper = new TableSorter();
			bactPollutantWrapper.clearWrappedData();
			bactPollutantWrapper
					.setWrappedData(((PTIOApplicationEU) selectedEU)
							.getBactEmissions());
			laerPollutantWrapper = new TableSorter();
			laerPollutantWrapper.clearWrappedData();
			laerPollutantWrapper
					.setWrappedData(((PTIOApplicationEU) selectedEU)
							.getLaerEmissions());
		}

		if (selectedEU instanceof TVApplicationEU) {
			// retrieve subparts
			TVApplicationEU tvAppEu = (TVApplicationEU) selectedEU;
			tvEuNspsSubparts = Stars2Object.toStar2Object(tvAppEu
					.getNspsSubpartCodes());
			tvEuNeshapSubparts = Stars2Object.toStar2Object(tvAppEu
					.getNeshapSubpartCodes());
			tvEuMactSubparts = Stars2Object.toStar2Object(tvAppEu
					.getMactSubpartCodes());

			// set selectedScenario to normal scenario by default
			selectedScenario = ((TVApplicationEU) selectedEU)
					.getNormalOperatingScenario();
			applicableRequirements = ((TVApplicationEU) selectedEU)
					.getApplicableRequirements();
			stateOnlyRequirements = ((TVApplicationEU) selectedEU)
					.getStateOnlyRequirements();
		}

	}

	@Override
	public String reload() {		
		return reloadApplicationSummary();
	}

	/**
	 * Reload data for current application and exit edit mode.
	 * 
	 * @return
	 */
/*
	public String reloadApplication() {
		if (!readOnly) {
			loadApplication(application.getApplicationID());
		} else {
			// load read-only version if edit is not allowed
			loadApplication(application.getApplicationNumber());
		}
		return null;
	}
*/
	/**
	 * Reload notes for current application.
	 */
	private void reloadNotes() {
		loadNotes(application.getApplicationID());
	}
	
	/**
	 * Copy data from application to local objects in this class as needed.
	 * 
	 */
	protected void loadApplicationData() {
		try {
			// mark trade secret as "dirty"
			tradeSecretStatusChanged = true;
			// make local copy of application contact
			// (this is needed because contact information may not
			// be provided by user, so a local copy is needed for the UI).
			if (application.getContact() != null) {
				for (Contact contact : getFacilityContactList()) {
					if (contact.getContactId().equals(
							application.getContactId())) {
						// make note of the fact that this contact comes from a
						// facility and mark it as read only
						usingFacilityContact = true;
						newContact = true;
						contactReadOnly = true;
					}
				}
				applicationContact = application.getContact();
			} else {
				applicationContact = createContact();
			}
			
			// refresh facility owner
			if (application.getFacility() != null) {
				Facility fac = application.getFacility();
				fac.setOwner(getFacilityService().retrieveFacilityOwner(
						application.getFacilityId()));
				application.setFacility(fac);
			}

			setApplicationDocuments(application.getDocuments());

			if (application instanceof PTIOApplication) {
				PTIOApplication ptioApp = (PTIOApplication) application;

				// update non-editable application purpose codes
				nonEditablePtioApplicationPurposeCds.clear();
				for (String purposeCd : ptioApp.getApplicationPurposeCDs()) {
					nonEditablePtioApplicationPurposeCds.add(purposeCd);
				}
			} else if (application instanceof TVApplication) {
				TVApplication tvApp = (TVApplication) application;

				applicableRequirements = tvApp.getApplicableRequirements();
				stateOnlyRequirements = tvApp.getStateOnlyRequirements();
				this.facWideReqWrapper.setWrappedData(tvApp
						.getFacilityWideRequirements());

				resetTvApplicationSubparts(tvApp);
				resetApplicableReqTables();

			}
		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
		}
	}

	private void leaveEditMode() {
		editMode = false;
		semiEditMode = false;
	}

	private Permit getReferencedPermitToAdd(String referencedPermitNumber) {
		Permit referencedPermit;
		// try {
		referencedPermit = new Permit();
		// TODO service
		// referencedPermit =
		// permitService.getPermitByNumber(newSupersededPermitNumber)
		referencedPermit.setPermitNumber(referencedPermitNumber);
		// } catch (RemoteException re) {
		// handleException(re);
		// referencedPermit = null;
		// }
		return referencedPermit;
	}

	private int getUserID() {
		return InfrastructureDefs.getCurrentUserId();
	}

	public Integer getWorkflowProcessId() {
		return workflowProcessId;
	}

	public void setWorkflowProcessId(Integer workflowProcessId) {
		this.workflowProcessId = workflowProcessId;
	}

	public Integer getWorkflowActivityId() {
		return workflowActivityId;
	}

	public void setWorkflowActivityId(Integer workflowActivityId) {
		this.workflowActivityId = workflowActivityId;
	}

	public boolean getFromTODOList() {
		return fromTODOList;
	}

	public void setFromTODOList(boolean fromTODOList) {
		this.fromTODOList = fromTODOList;
	}

	public boolean isNoteModify() {
		return noteModify;
	}

	public ApplicationNote getApplicationNote() {
		return applicationNote;
	}

	private void setSubpartCDs(ApplicationEU appEu) {
		if (!(appEu instanceof PTIOApplicationEU)) {
			return;
		}

		if (nspsSubparts == null) {
			nspsSubparts = new ArrayList<Stars2Object>();
		}

		if (mactSubparts == null) {
			mactSubparts = new ArrayList<Stars2Object>();
		}

		if (neshapSubparts == null) {
			neshapSubparts = new ArrayList<Stars2Object>();
		}

		List<String> sub = ((PTIOApplicationEU) appEu).getNspsSubpartCodes();
		sub.clear();
		for (Stars2Object s : nspsSubparts) {
			if (s.getValue() != null) {
				sub.add(s.getValue().toString());
			}
		}
		sub = ((PTIOApplicationEU) appEu).getMactSubpartCodes();
		sub.clear();
		for (Stars2Object s : mactSubparts) {
			if (s.getValue() != null) {
				sub.add(s.getValue().toString());
			}
		}
		sub = ((PTIOApplicationEU) appEu).getNeshapSubpartCodes();
		sub.clear();
		for (Stars2Object s : neshapSubparts) {
			if (s.getValue() != null) {
				sub.add(s.getValue().toString());
			}
		}
	}

	private void setTvEuSubpartCDs(ApplicationEU appEu) {
		if (!(appEu instanceof TVApplicationEU)) {
			return;
		}

		if (tvEuNspsSubparts == null) {
			tvEuNspsSubparts = new ArrayList<Stars2Object>();
		}

		if (tvEuMactSubparts == null) {
			tvEuMactSubparts = new ArrayList<Stars2Object>();
		}

		if (tvEuNeshapSubparts == null) {
			tvEuNeshapSubparts = new ArrayList<Stars2Object>();
		}

		List<String> sub = ((TVApplicationEU) appEu).getNspsSubpartCodes();
		sub.clear();
		for (Stars2Object s : tvEuNspsSubparts) {
			if (s.getValue() != null) {
				sub.add(s.getValue().toString());
			}
		}
		sub = ((TVApplicationEU) appEu).getMactSubpartCodes();
		sub.clear();
		for (Stars2Object s : tvEuMactSubparts) {
			if (s.getValue() != null) {
				sub.add(s.getValue().toString());
			}
		}
		sub = ((TVApplicationEU) appEu).getNeshapSubpartCodes();
		sub.clear();
		for (Stars2Object s : tvEuNeshapSubparts) {
			if (s.getValue() != null) {
				sub.add(s.getValue().toString());
			}
		}
	}

	private void setTvSubpartCDs(TVApplication tvApp) {
		List<String> nspsSubpartCds = getSubpartCdsBySelectedSubparts(tvNspsSubparts);
		List<String> neshapSubpartCds = getSubpartCdsBySelectedSubparts(tvNeshapSubparts);
		List<String> mactSubpartCds = getSubpartCdsBySelectedSubparts(tvMactSubparts);

		tvApp.setNspsSubpartCodes(nspsSubpartCds);
		tvApp.setNeshapSubpartCodes(neshapSubpartCds);
		tvApp.setMactSubpartCodes(mactSubpartCds);
	}

	private List<String> getSubpartCdsBySelectedSubparts(
			List<Stars2Object> sourceSubparts) {
		if (sourceSubparts == null)
			sourceSubparts = new ArrayList<Stars2Object>();

		List<String> subpartCds = new ArrayList<String>();

		for (Stars2Object obj : sourceSubparts) {
			if (obj == null || obj.getValue() == null)
				continue;

			String subpart = obj.getValue().toString();
			if (!Utility.isNullOrEmpty(subpart))
				subpartCds.add(subpart);
		}

		return subpartCds;
	}

	/**
	 * Retrieve CAP emissions making sure they're in the correct order.
	 * 
	 * @return
	 */
	/* **************** OBSOLETE ***************************************
	public List<ApplicationEUEmissions> getCapEmissions() {
		List<ApplicationEUEmissions> sortedCapEmissions = null;
		if (selectedEU instanceof PTIOApplicationEU) {
			List<ApplicationEUEmissions> capEmissions = ((PTIOApplicationEU) selectedEU)
					.getCapEmissions();
			sortedCapEmissions = new ArrayList<ApplicationEUEmissions>();
			for (String pollutantCd : ApplicationBO
					.getPTIOCapPollutantCodesOrdered()) {
				for (ApplicationEUEmissions emissions : capEmissions) {
					if (emissions.getPollutantCd().equals(pollutantCd)) {
						sortedCapEmissions.add(new ApplicationEUEmissions(emissions));
						break;
					}
				}
			}
		} else {
			sortedCapEmissions = new ArrayList<ApplicationEUEmissions>();
		}
		return sortedCapEmissions;
	}
	

	public List<TVApplicationEUEmissions> getTvCapEmissions() {
		List<TVApplicationEUEmissions> sortedCapEmissions = new ArrayList<TVApplicationEUEmissions>();
		if (selectedScenario != null) {
			List<TVApplicationEUEmissions> capEmissions = selectedScenario
					.getCapEmissions();
			TreeMap<String, TVApplicationEUEmissions> sortedMap = new TreeMap<String, TVApplicationEUEmissions>();
			for (TVApplicationEUEmissions emission : capEmissions) {
				String label = new String(PollutantDef.getData().getItems()
						.getItemDesc(emission.getPollutantCd()));
				TVApplicationEUEmissions em = new TVApplicationEUEmissions(emission);
				sortedMap.put(label, em);
			}

			for (String label : sortedMap.keySet()) {
				sortedCapEmissions.add(new TVApplicationEUEmissions(sortedMap.get(label)));
			}
		}
		return sortedCapEmissions;

	}
	*/

	public List<ApplicationEUEmissions> getHapTacEmissions() {
		List<ApplicationEUEmissions> ret = null;
		if (selectedEU instanceof PTIOApplicationEU) {
			ret = ((PTIOApplicationEU) selectedEU).getHapTacEmissions();
		} else {
			ret = new ArrayList<ApplicationEUEmissions>();
		}
		return ret;
	}

	public List<ApplicationEUEmissions> getGhgEmissions() {
		List<ApplicationEUEmissions> ret = null;
		if (selectedEU instanceof PTIOApplicationEU) {
			ret = ((PTIOApplicationEU) selectedEU).getGhgEmissions();
		} else {
			ret = new ArrayList<ApplicationEUEmissions>();
		}
		return ret;
	}

	public boolean getDisplayNSPSSubparts() {
		boolean showSubparts = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			String flag = ((PTIOApplicationEU) selectedEU)
					.getNspsApplicableFlag();
			showSubparts = flag != null
					&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		}
		return showSubparts;
	}

	public boolean getDisplayNESHAPSubparts() {
		boolean showSubparts = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			String flag = ((PTIOApplicationEU) selectedEU)
					.getNeshapApplicableFlag();
			showSubparts = flag != null
					&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		}
		return showSubparts;
	}

	public boolean getDisplayMACTSubparts() {
		boolean showSubparts = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			String flag = ((PTIOApplicationEU) selectedEU)
					.getMactApplicableFlag();
			showSubparts = flag != null
					&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		}
		return showSubparts;
	}

	public boolean getFedRulesExemption() {
		boolean exempt = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			PTIOApplicationEU app = (PTIOApplicationEU) selectedEU;
			exempt = app.getFedRulesExemption();
		}
		return exempt;
	}

	public boolean getPteAnalysisRequested() {
		boolean needPteAnalysis = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			PTIOApplicationEU eu = (PTIOApplicationEU) selectedEU;
			needPteAnalysis = PTIORequestingFedLimitsDef.YES.equals(eu
					.getRequestingFederalLimitsFlag())
					&& (eu.getFederalLimitsReasonCDs()
							.contains(
									PTIORequestingFedLimitsReasonDef.AVOID_BEING_MAJOR_TV_SOURCE)
							|| eu.getFederalLimitsReasonCDs()
									.contains(
											PTIORequestingFedLimitsReasonDef.AVOID_BEING_MAJOR_MACT_SOURCE) || eu
							.getFederalLimitsReasonCDs()
							.contains(
									PTIORequestingFedLimitsReasonDef.AVOID_BEING_MAJOR_STATIONARY_SOURCE));
		}
		return needPteAnalysis;
	}

	public boolean getNetEmissionsChangeAnalysisRequested() {
		boolean needNetEmissionsChangeAnalysis = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			PTIOApplicationEU eu = (PTIOApplicationEU) selectedEU;
			needNetEmissionsChangeAnalysis = PTIORequestingFedLimitsDef.YES
					.equals(eu.getRequestingFederalLimitsFlag())
					&& eu.getFederalLimitsReasonCDs()
							.contains(
									PTIORequestingFedLimitsReasonDef.AVOID_BEING_MAJOR_MODIFICATION);
		}
		return needNetEmissionsChangeAnalysis;
	}

	public boolean getDescriptionOfRestrictionsRequested() {
		boolean needDescriptionOfRestrictions = false;
		if (selectedEU instanceof PTIOApplicationEU) {
			PTIOApplicationEU eu = (PTIOApplicationEU) selectedEU;
			needDescriptionOfRestrictions = PTIORequestingFedLimitsDef.YES
					.equals(eu.getRequestingFederalLimitsFlag())
					&& (eu.getFederalLimitsReasonCDs()
							.contains(
									PTIORequestingFedLimitsReasonDef.AVOID_AIR_DISP_MODEL_REQUIREMNTS)
							|| eu.getFederalLimitsReasonCDs()
									.contains(
											PTIORequestingFedLimitsReasonDef.AVOID_BAT_REQUIREMENTS) || eu
							.getFederalLimitsReasonCDs().contains(
									PTIORequestingFedLimitsReasonDef.OTHER));
		}
		return needDescriptionOfRestrictions;
	}

	public List<Stars2Object> getMactSubparts() {
		if (mactSubparts == null) {
			mactSubparts = new ArrayList<Stars2Object>();
		}

		return this.mactSubparts;
	}

	public void setMactSubparts(List<Stars2Object> mactSubparts) {
		this.mactSubparts = mactSubparts;
	}

	public List<Stars2Object> getNeshapSubparts() {
		if (neshapSubparts == null) {
			neshapSubparts = new ArrayList<Stars2Object>();
		}

		return neshapSubparts;
	}

	public void setNeshapSubparts(List<Stars2Object> neshapSubparts) {
		this.neshapSubparts = neshapSubparts;
	}

	public List<Stars2Object> getNspsSubparts() {
		if (nspsSubparts == null) {
			nspsSubparts = new ArrayList<Stars2Object>();
		}

		return nspsSubparts;
	}

	public void setNspsSubparts(List<Stars2Object> nspsSubparts) {
		this.nspsSubparts = nspsSubparts;
	}

	public void addPollutant(ActionEvent ae) {
		if (selectedEU instanceof PTIOApplicationEU) {
			ApplicationEUEmissions emissions = new ApplicationEUEmissions();
			((PTIOApplicationEU) selectedEU).getCapEmissions().add(emissions);
		}
	}

	public ApplicationEUEmissions getEmissions() {
		return emissions;
	}

	public void setEmissions(ApplicationEUEmissions emissions) {
		this.emissions = emissions;
	}

	public boolean isEmissionsModify() {
		return emissionsModify;
	}

	public List<SelectItem> getCapPollutantDefs() {
		// create select list of CAP pollutants
		List<SelectItem> pollutantDefs = new ArrayList<SelectItem>();
		for (String pollutantCd : ApplicationBO.getPTIOCapPollutantDefs()
				.keySet()) {
			pollutantDefs.add(new SelectItem(pollutantCd, ApplicationBO
					.getPTIOCapPollutantDefs().get(pollutantCd)));
		}
		return pollutantDefs;
	}

	private void filterPollutantDefs(Map<String, SelectItem> pollutantDefs,
			List<ApplicationEUEmissions> selectedPollutants) {
		// remove pollutants already included (for new pollutant only)
		for (ApplicationEUEmissions pollutant : selectedPollutants) {
			for (String pollutantDefKey : pollutantDefs.keySet()) {
				SelectItem pollutantDef = pollutantDefs.get(pollutantDefKey);
				if (pollutantDef.getValue().equals(pollutant.getPollutantCd())) {
					pollutantDefs.remove(pollutantDefKey);
					break;
				}
			}
		}
	}

	private void filterTVPollutantDefs(Map<String, SelectItem> pollutantDefs,
			List<TVApplicationEUEmissions> selectedPollutants) {
		// remove pollutants already included (for new pollutant only)
		for (TVApplicationEUEmissions pollutant : selectedPollutants) {
			for (String pollutantDefKey : pollutantDefs.keySet()) {
				SelectItem pollutantDef = pollutantDefs.get(pollutantDefKey);
				if (pollutantDef.getValue().equals(pollutant.getPollutantCd())) {
					pollutantDefs.remove(pollutantDefKey);
					break;
				}
			}
		}
	}

	public List<SelectItem> getPtioPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			// Mantis 1908: include all non-CAP pollutants in HAP list
			if (POLLUTANT_HAP_TAC.equals(pollutantType)) {
				if (ApplicationBO.getPTIOCapPollutantDefs().get(
						pollutantDef.getCode()) == null
						&& !pollutantDef.isGhg()) {
					pollutantDefs.put(pollutantDef.getSortCategory() + ":"
							+ item.getLabel(), item);
				}
			} else if (POLLUTANT_GHG.equals(pollutantType)) {
				if (pollutantDef.isGhg()) {
					pollutantDefs.put(pollutantDef.getSortCategory() + ":"
							+ item.getLabel(), item);
				}
			} else if (POLLUTANT_OTH.equals(pollutantType)) {
				if (pollutantDef.isOtherRegulatedPollutant()) {
					pollutantDefs.put(pollutantDef.getSortCategory() + ":"
							+ item.getLabel(), item);
				}
			}
		}

		// remove pollutants already included (for new pollutant in popup only)
		if (emissionsDialogOpen && !emissionsModify
				&& selectedEU instanceof PTIOApplicationEU) {
			if (pollutantType.equals(POLLUTANT_HAP_TAC)) {
				filterPollutantDefs(pollutantDefs,
						((PTIOApplicationEU) selectedEU).getHapTacEmissions());
			} else if (pollutantType.equals(POLLUTANT_GHG)) {
				filterPollutantDefs(pollutantDefs,
						((PTIOApplicationEU) selectedEU).getGhgEmissions());
			} else if (pollutantType.equals(POLLUTANT_OTH)) {
				filterPollutantDefs(pollutantDefs,
						((PTIOApplicationEU) selectedEU).getOthEmissions());
			}
		}
		
		List<SelectItem> result = new ArrayList<SelectItem>(
				pollutantDefs.values());
		return result;
	}

	public List<SelectItem> getTvPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (POLLUTANT_HAP_TAC.equals(pollutantType)
					&& pollutantDef.isTvptoAppSec13()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			} else if (POLLUTANT_CAP.equals(pollutantType)
					&& pollutantDef.isTvptoAppSec12()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			} else if (POLLUTANT_SEC3.equals(pollutantType)
					&& pollutantDef.isTvptoAppSec3()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			} else if (POLLUTANT_GHG.equals(pollutantType)
					&& pollutantDef.isGhg()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			} else if (POLLUTANT_OTH.equals(pollutantType)
					&& pollutantDef.isOtherRegulatedPollutant()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			}
		}

		// remove pollutants already included (for new pollutant in popup only)
		if (emissionsDialogOpen && !emissionsModify
				&& selectedEU instanceof TVApplicationEU) {
			if (pollutantType.equals(POLLUTANT_HAP_TAC)) {
				filterTVPollutantDefs(pollutantDefs,
						selectedScenario.getHapEmissions());
			} else if (pollutantType.equals(POLLUTANT_CAP)) {
				filterTVPollutantDefs(pollutantDefs,
						selectedScenario.getCapEmissions());
			} else if (pollutantType.equals(POLLUTANT_GHG)) {
				filterTVPollutantDefs(pollutantDefs,
						selectedScenario.getGhgEmissions());
			} else if (pollutantType.equals(POLLUTANT_OTH)) {
				filterTVPollutantDefs(pollutantDefs,
						selectedScenario.getOthEmissions());
			}
		}
		List<SelectItem> result = new ArrayList<SelectItem>(
				pollutantDefs.values());
		return result;
	}

	public List<SelectItem> getTvPollutantLimitPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (pollutantDef.isPollutantLimit()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			}
		}

		return new ArrayList<SelectItem>(pollutantDefs.values());
	}

	public final List<SelectItem> getNsrBactPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (pollutantDef.isNsrAppBact()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			}
		}

		return new ArrayList<SelectItem>(pollutantDefs.values());
	}

	public final List<SelectItem> getNsrLaerPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (pollutantDef.isNsrAppLaer()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			}
		}

		return new ArrayList<SelectItem>(pollutantDefs.values());
	}

	public final List<SelectItem> getTvCapPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (pollutantDef.isTvptoAppSec12()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			}
		}

		return new ArrayList<SelectItem>(pollutantDefs.values());
	}

	public final List<SelectItem> getHapTacPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (ApplicationBO.getPTIOCapPollutantDefs().get(
					pollutantDef.getCode()) == null
					&& !pollutantDef.isGhg()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			}
		}

		return new ArrayList<SelectItem>(pollutantDefs.values());
	}

	public final List<SelectItem> getGhgPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (pollutantDef.isGhg()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			}
		}

		return new ArrayList<SelectItem>(pollutantDefs.values());
	}

	public final List<SelectItem> getOthPollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : pollutantDefItems.getCurrentItems()) {
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (pollutantDef.isOtherRegulatedPollutant()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			}
		}

		return new ArrayList<SelectItem>(pollutantDefs.values());
	}

	public List<SelectItem> getTvHapPollutantDefs() {
		TVApplicationEUEmissions emissionsInRow = (TVApplicationEUEmissions) FacesUtil
				.getManagedBean("tvHapEmissions");

		// find out which pollutants are already in the table (they will be
		// excluded
		// from the pick list for new records)
		List<String> codesInUse = new ArrayList<String>();
		for (TVApplicationEUEmissions emissions : selectedScenario
				.getHapEmissions()) {
			if (emissions.getPollutantCd() != null
					&& !emissions.getPollutantCd().equals(
							emissionsInRow.getPollutantCd())) {
				codesInUse.add(new String(emissions.getPollutantCd()));
			}
		}

		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		SelectItem hapMaxItem = null;
		SelectItem hapTotItem = null;
		for (SelectItem item : pollutantDefItems.getAllItems()) {
			// don't display pollutants already in table in drop down list
			if (codesInUse.contains(item.getValue())) {
				continue;
			}
			PollutantDef pollutantDef = (PollutantDef) pollutantDefItems
					.getItem(item.getValue().toString());
			if (pollutantDef.isTvptoAppSec13()) {
				pollutantDefs.put(
						pollutantDef.getSortCategory() + ":" + item.getLabel(),
						item);
			} else if (pollutantDef.getCode().equals(PollutantDef.HMAX_CD)) {
				hapMaxItem = item;
			} else if (pollutantDef.getCode().equals(PollutantDef.HTOT_CD)) {
				hapTotItem = item;
			}
		}
		ArrayList<SelectItem> result = new ArrayList<SelectItem>(
				pollutantDefs.values());
		result.add(hapMaxItem);
		result.add(hapTotItem);

		return result;
	}

	/*
	 * public List<SelectItem> getApplicationDocTypeDefs() { List<SelectItem>
	 * docTypeDefs = new ArrayList<SelectItem>(); if (application instanceof
	 * TIVApplication) { // title iv document, calculations and other are the
	 * only doc types // available to title iv app docTypeDefs .add(new
	 * SelectItem( ApplicationDocumentTypeDef.TITLE_IV_ACID_RAIN_APP,
	 * ApplicationDocumentTypeDef .getData() .getItems() .getItemDesc(
	 * ApplicationDocumentTypeDef.TITLE_IV_ACID_RAIN_APP))); docTypeDefs.add(new
	 * SelectItem( ApplicationDocumentTypeDef.CALCULATIONS,
	 * ApplicationDocumentTypeDef .getData() .getItems() .getItemDesc(
	 * ApplicationDocumentTypeDef.CALCULATIONS))); docTypeDefs.add(new
	 * SelectItem(ApplicationDocumentTypeDef.OTHER,
	 * ApplicationDocumentTypeDef.getData().getItems()
	 * .getItemDesc(ApplicationDocumentTypeDef.OTHER))); } else { for
	 * (SelectItem item : ApplicationDocumentTypeDef.getData()
	 * .getItems().getCurrentItems()) { if
	 * (ApplicationDocumentTypeDef.TITLE_IV_ACID_RAIN_APP
	 * .equals(item.getValue().toString())) { // title iv document only
	 * available to title iv app continue; } // filter out general permit doc
	 * type for title V applications if
	 * (ApplicationDocumentTypeDef.GENERAL_PERMIT.equals(item
	 * .getValue().toString()) && (application instanceof TVApplication)) {
	 * continue; } docTypeDefs.add(item); } } return docTypeDefs; }
	 */

	/**
	 * Create a select list with non-zero items from the selected scenario's CAP
	 * emissions table and HAP emissions table for use in the applicable
	 * requirements table.
	 * 
	 * @return
	 */
	public List<SelectItem> getAltScenarioPtePollutantDefs() {
		DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
		TreeMap<String, SelectItem> pollutantDefs = new TreeMap<String, SelectItem>();
		if (selectedScenario != null) {
			for (SelectItem item : pollutantDefItems.getCurrentItems()) {
				for (TVApplicationEUEmissions emissions : selectedScenario
						.getCapEmissions()) {
					if (item.getValue().equals(emissions.getPollutantCd())) {
						pollutantDefs.put(item.getLabel(), item);
					}
				}
				for (TVApplicationEUEmissions emissions : selectedScenario
						.getHapEmissions()) {
					// exclude total and max HAPs from the drop down list
					if (emissions.getPollutantCd() != null
							&& !emissions.getPollutantCd().equals(
									PollutantDef.HTOT_CD)
							&& !emissions.getPollutantCd().equals(
									PollutantDef.HMAX_CD)) {
						if (item.getValue().equals(emissions.getPollutantCd())) {
							pollutantDefs.put(item.getLabel(), item);
						}
					}
				}
			}
		}
		List<SelectItem> result = new ArrayList<SelectItem>(
				pollutantDefs.values());
		return result;
	}

	public Contact getApplicationContact() {
		return applicationContact;
	}

	public void setApplicationContact(Contact applicationContact) {
		this.applicationContact = applicationContact;
	}

	public boolean isPerDueDateEditable() {
		return perDueDateEditable;
	}

	public void setPerDueDateEditable(boolean perDueDateEditable) {
		this.perDueDateEditable = perDueDateEditable;
	}

	public int correctApplication(String amendedApplicationNumber,
			boolean dapcAmendment, String correctedReason) {
		int ret = 0; // success
		try {
			ApplicationService appBO = getApplicationService();
			Application oldApp = appBO
					.retrieveApplicationWithAllEUs(amendedApplicationNumber);
			if (oldApp != null) {
				application = appBO.createApplicationCopy(oldApp, true,
						correctedReason, dapcAmendment);

				// load new corrected application
				reset();
				reloadApplicationSummary();
			} else {
				DisplayUtil.displayError("Failed correcting application. "
						+ "No application exists with application number "
						+ amendedApplicationNumber);
			}
		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
		}
		return ret; // success
	}

	public int cloneApplication(String applicationNumber,
			Class<? extends Application> newApplicationClass) {
		int ret = 0; // success
		try {
			ApplicationService appBO = getApplicationService();
			Application oldApp = appBO.retrieveApplicationWithAllEUs(applicationNumber);
			if (oldApp != null) {
				application = appBO.createApplicationCopy(oldApp, false, null,
						false);

				// load cloned application
				reset();
				reloadApplicationSummary();
			} else {
				DisplayUtil.displayError("Failed copying application. "
						+ "No application exists with application number "
						+ applicationNumber);
			}
		} catch (RemoteException re) {
			handleException("Exception for application " + applicationNumber,
					re);
			ret = -1;
		}
		return ret; // success
	}

	public boolean isDocUpdate() {
		return docUpdate;
	}

	public void setDocUpdate(boolean docUpdate) {
		this.docUpdate = docUpdate;
	}

	public UploadedFile getTsFileToUpload() {
		return tsFileToUpload;
	}

	public void setTsFileToUpload(UploadedFile tsFileToUpload) {
		this.tsFileToUpload = tsFileToUpload;
	}

	public String getCAP() {
		return this.POLLUTANT_CAP;
	}

	public String getHAPTAC() {
		return this.POLLUTANT_HAP_TAC;
	}

	public String getSEC3() {
		return this.POLLUTANT_SEC3;
	}

	public String getGHG() {
		return this.POLLUTANT_GHG;
	}

	public String getOTH() {
		return this.POLLUTANT_OTH;
	}

	public String getPollutantType() {
		return pollutantType;
	}

	public void setPollutantType(String pollutantType) {
		this.pollutantType = pollutantType;
	}

	public String displayEUShuttle() {
		// just select EU_SHUTTLE_NODE_TYPE tree node and jsp will handle the
		// rest
		selectedTreeNode = new Stars2TreeNode(EU_SHUTTLE_NODE_TYPE, null, null,
				false, null);
		return null;
	}

	public String displayEUCopyShuttle() {
		eusTargetedForCopy.clear();
		// just select EU_COPY_NODE_TYPE tree node and jsp will handle the rest
		selectedTreeNode = new Stars2TreeNode(EU_COPY_NODE_TYPE, null, null,
				false, null);
		return null;
	}

	public String createTvEuGroup() {
		try {
			selectedEUGroup = new TVEUGroup();
			selectedEUGroup.setApplicationId(application.getApplicationID());
			// create a default name for this group
			selectedEUGroup.setTvEuGroupName("EU Group " + (euGroupNum + 1));
			selectedEUGroup = getApplicationService().createTvEuGroup(
					selectedEUGroup);
			((TVApplication) application).getEuGroups().add(selectedEUGroup);
			reloadWithSelectedEUGroup();
			enterEditMode();
		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
		}
		return null; // stay on same page
	}

	public String calculateHaps() {
		if (selectedEU instanceof PTIOApplicationEU) {
			try {
				getApplicationService().calculateHaps(
						(PTIOApplicationEU) selectedEU);
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
		}

		return null;
	}

	private void appendTVHapsTotals(List<TVApplicationEUEmissions> hapEmissions) {
		if (selectedEU instanceof TVApplicationEU) {
			TVApplicationEUEmissions hapMaxEmissions = null;
			TVApplicationEUEmissions hapTotEmissions = null;

			// determine highest value and total value for requested
			// allowable (ton/year)
			for (TVApplicationEUEmissions emissions : hapEmissions) {
				if (emissions.getPollutantCd().equals(PollutantDef.HMAX_CD)) {
					hapMaxEmissions = emissions;
				} else if (emissions.getPollutantCd().equals(
						PollutantDef.HTOT_CD)) {
					hapTotEmissions = emissions;
				}
			}

			if (hapMaxEmissions == null) {
				hapEmissions.add(new TVApplicationEUEmissions(selectedScenario
						.getApplicationEuId(), selectedScenario
						.getTvEuOperatingScenarioId(), PollutantDef.HMAX_CD,
						ApplicationEUEmissionTableDef.HAP_TABLE_CD));
			}
			if (hapTotEmissions == null) {
				hapEmissions.add(new TVApplicationEUEmissions(selectedScenario
						.getApplicationEuId(), selectedScenario
						.getTvEuOperatingScenarioId(), PollutantDef.HTOT_CD,
						ApplicationEUEmissionTableDef.HAP_TABLE_CD));
			}
		}
	}

	public String calculateTVHaps() {
		if (selectedEU instanceof TVApplicationEU) {
			try {
				getApplicationService().calculateTVHaps(selectedScenario);
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
		}

		return null;
	}

	public boolean isApplicationDeleted() {
		return applicationDeleted;
	}

	public void setApplicationDeleted(boolean applicationDeleted) {
		this.applicationDeleted = applicationDeleted;
	}

	/*
	 * public List<SelectItem> getEditablePtioApplicationPurposeDefs() {
	 * List<SelectItem> defs = new ArrayList<SelectItem>(); for (SelectItem item
	 * : PTIOApplicationPurposeDef.getData().getItems() .getAllItems()) { if
	 * (isEditablePtioApplicationPurposeCd(item.getValue().toString())) {
	 * defs.add(item); } } return defs; }
	 */

	/*
	 * private final boolean isEditablePtioApplicationPurposeCd(String
	 * purposeCd) { return
	 * purposeCd.equals(PTIOApplicationPurposeDef.TV_TO_PTIO); }
	 */

	public List<String> getNonEditablePtioApplicationPurposeCds() {
		return nonEditablePtioApplicationPurposeCds;
	}

	public void setNonEditablePtioApplicationPurposeCds(
			List<String> nonEditablePtioApplicationPurposeCds) {
		if (nonEditablePtioApplicationPurposeCds == null) {
			this.nonEditablePtioApplicationPurposeCds.clear();
		} else {
			this.nonEditablePtioApplicationPurposeCds = nonEditablePtioApplicationPurposeCds;
		}
	}

	public List<SelectItem> getNonEditablePtioApplicationPurposeDefs() {
		List<SelectItem> defs = new ArrayList<SelectItem>();
		for (SelectItem item : PTIOApplicationPurposeDef.getData().getItems()
				.getAllItems()) {
			defs.add(item);
		}
		return defs;
	}

	public List<SelectItem> getPtioApplicationSageGrouseDefs() {
		List<SelectItem> defs = new ArrayList<SelectItem>();
		for (SelectItem item : PTIOApplicationSageGrouseDef.getData()
				.getItems().getAllItems()) {
			defs.add(item);
		}
		return defs;
	}

	public List<SelectItem> getPaeuPteDeterminationBasisDefs() {
		List<SelectItem> defs = new ArrayList<SelectItem>();
		for (SelectItem item : PAEuPteDeterminationBasisDef.getData()
				.getItems().getAllItems()) {
			defs.add(new SelectItem(item.getValue(), item.getLabel(), item.getDescription(), item.isDisabled()));
		}
		return defs;
	}

	public List<SelectItem> getPatveuPteDeterminationBasisDefs() {
		List<SelectItem> defs = new ArrayList<SelectItem>();
		for (SelectItem item : PATvEuPteDeterminationBasisDef.getData()
				.getItems().getAllItems()) {
			defs.add(new SelectItem(item.getValue(), item.getLabel(), item.getDescription(), item.isDisabled()));
		}
		return defs;
	}

	public DefSelectItems getPaeuPteUnitsDefs() {
		return PAEuPTEUnitsDef.getData().getItems();
	}

	public Integer getCopyEUId() {
		Integer copyEUId = null;
		if (euToCopy != null) {
			copyEUId = euToCopy.getApplicationEuId();
		}
		return copyEUId;
	}

	public void setCopyEUId(Integer copyEUId) {
		for (ApplicationEU appEU : eusAvailableForCopy) {
			if (appEU.getApplicationEuId().equals(copyEUId)) {
				euToCopy = appEU;
			}
		}
	}

	/**
	 * Navigate to facility inventory screen for application's facility.
	 * 
	 * @return facility inventory screen
	 */
	public String showFacilityProfile() {
		String ret = FacilityProfileBase.FAC_OUTCOME;
		FacilityProfileBase facProfile = (FacilityProfileBase) FacesUtil
				.getManagedBean("facilityProfile");
		facProfile.initFacilityProfile(application.getFacility().getFpId(),
				isPortalApp() && !readOnly);

		return ret;
	}

	public void showRelatedPermit(ActionEvent event) {
		if (relatedPermitId != null) {
			PermitDetail permitDetail = (PermitDetail) FacesUtil
					.getManagedBean("permitDetail");
			permitDetail.setPermitID(relatedPermitId);
			permitDetail.setFromTODOList(false);
			// setup redirect so page will navigate to permit detail
			// when dialog returns.
			popupRedirectOutcome = permitDetail.loadPermit();
			FacesUtil.returnFromDialogAndRefresh();
		}
	}

	public boolean isShowFacilityProfileBtn() {
		// application instanceof RPRRequest ||
		return !(application instanceof RPERequest);
	}

	public boolean isPbrRprRpe() {
		return application instanceof RPRRequest
				|| application instanceof RPERequest
				|| application instanceof PBRNotification;
	}

	/*
	 * public boolean isTransitionToPTIO() { return application instanceof
	 * PTIOApplication && ((PTIOApplication)
	 * application).getApplicationPurposeCDs()
	 * .contains(PTIOApplicationPurposeDef.TV_TO_PTIO); }
	 */

	/*
	 * public void setTransitionToPTIO(boolean transitionToPTIO) { if
	 * (application instanceof PTIOApplication) { if (transitionToPTIO &&
	 * !isTransitionToPTIO()) { ((PTIOApplication)
	 * application).getApplicationPurposeCDs().add(
	 * PTIOApplicationPurposeDef.TV_TO_PTIO); } else if (!transitionToPTIO &&
	 * isTransitionToPTIO()) { ((PTIOApplication)
	 * application).getApplicationPurposeCDs()
	 * .remove(PTIOApplicationPurposeDef.TV_TO_PTIO); } } }
	 */

	public boolean getOkToEditAttachment() {
		boolean okToEdit = false;

		// if viewDoc flag is on, we're just viewing attachments and edit should
		// be off
		if (viewDoc) {
			okToEdit = false;
		}
		// attachments for read-only application should not be editable
		else if (!readOnly && isInternalApp() && isGeneralUser()) {
			// attachment may only be edited if the application has not yet been
			// submitted
			// or the attachment was uploaded after the application was
			// submitted
			long uploadTime = 0;
			long submittedTime = 0;
			ApplicationDocumentRef docRef = (ApplicationDocumentRef) FacesUtil
					.getManagedBean(DOC_MANAGED_BEAN);
			if (docRef == null) {
				docRef = applicationDoc;
			}
			if (application.getSubmittedDate() != null) {
				submittedTime = application.getSubmittedDate().getTime();
			}
			// if document is not null and was not added by migration, check
			// upload date
			if (docRef != null) {
				if (docRef.getPublicDoc() != null
						&& docRef.getPublicDoc().getUploadDate() != null) {
					uploadTime = docRef.getPublicDoc().getUploadDate()
							.getTime();
				} else if (docRef.getTradeSecretDoc() != null
						&& docRef.getTradeSecretDoc().getUploadDate() != null) {
					uploadTime = docRef.getTradeSecretDoc().getUploadDate()
							.getTime();
				}
			}
			// document may be edited if it is new (uploadTime ==0)
			// or if it was uploaded after the application was submitted
			okToEdit = uploadTime == 0 || (uploadTime >= submittedTime);
		} else if (!readOnly) {
			// it's always ok to edit non-read only documents for
			// applications in external app
			okToEdit = true;
		}
		return okToEdit; // Don't edit attachment if application in
							// edit mode.
	}
	
	public boolean isTradeSecretVisible() {
		// Note that this Use Case is used both to protect Trade Secret
		// Application attachments and Compliance attachments.
		boolean ret = false;
		if (isPublicApp()) {
			ret = false;
		} else if (isPortalApp()) {
			ret = true;
		} else if (isInternalApp()) {
			ret = InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("applications.viewtradesecret");
		}
		return ret;
	}

	public TVApplicationEUEmissions getTvEmissions() {
		return tvEmissions;
	}

	public void setTvEmissions(TVApplicationEUEmissions tvEmissions) {
		this.tvEmissions = tvEmissions;
	}

	public boolean isTvApplication() {
		return (application instanceof TVApplication);
	}

	public boolean isShowApplicableRequirements() {
		return isTvApplication()
				&& selectedTreeNode != null
				&& selectedTreeNode.getType() != null
				&& (selectedTreeNode.getType().equals(APPLICATION_NODE_TYPE)
						|| selectedTreeNode.getType().equals(EU_NODE_TYPE) || selectedTreeNode
						.getType().equals(EU_GROUP_NODE_TYPE));
	}

	public String getPublicFormButtonLabel() {
		// Mantis 1674
		// String label = "List Documents";
		// if (application instanceof PTIOApplication ||
		// application instanceof TVApplication) {
		// label = "List Public Documents";
		// }
		return "Download/Print";
	}

	public boolean isRenderDisplayPublicFormButton() {
		return application instanceof PTIOApplication
				|| application instanceof TVApplication
				|| application instanceof TIVApplication
				|| application instanceof PBRNotification
				|| application instanceof RPCRequest;
	}

	public boolean isRenderDisplayTradeSecretFormButton() {
		return isTradeSecretVisible()
				&& (application instanceof PTIOApplication
						|| application instanceof TVApplication || application instanceof TIVApplication);
	}

	public boolean isRenderZipButton() {
		return (application instanceof PTIOApplication
				|| application instanceof TVApplication
				|| application instanceof TIVApplication
				|| application instanceof PBRNotification || application instanceof RPCRequest)
				&& isInternalApp();
	}

	public boolean isRenderCopyEUDataButton() {
		return getEditAllowed()
				&& (application instanceof PTIOApplication || application instanceof TVApplication);
	}

	public boolean isRenderExcludeEUButton() {
		return getEditAllowed() && !(application instanceof RPERequest);
	}

	public boolean isRenderGeneratePermitButton() {
		boolean render = false;
		if (application != null) {
			render = (application instanceof PTIOApplication
					|| application instanceof TVApplication
					|| application instanceof TIVApplication || application instanceof RPCRequest)
					&& application.isValid()
					&& application.getSubmittedDate() != null;
		}
		return render;
	}

	public boolean isRenderAttachments() {
		boolean render = false;
		if (application != null && selectedTreeNode != null) {
			render = selectedTreeNode.getType().equals(APPLICATION_NODE_TYPE)
					|| ((selectedTreeNode.getType().equals(EU_NODE_TYPE) || selectedTreeNode
							.getType().equals(INSIG_EU_NODE_TYPE))
							&& !(application instanceof PBRNotification)
							&& !(application instanceof RPCRequest)
							&& !(application instanceof RPRRequest) && !(application instanceof RPERequest));
		}

		return render;
	}

	public boolean isEuEditAllowed() {
		boolean edit = false;
		if (application != null) {
			edit = getEditAllowed()
					&& (application instanceof PTIOApplication
							|| application instanceof TVApplication || application instanceof RPRRequest);
		}
		return edit;
	}

	public boolean isInsignificantEUSelected() {
		return selectedEU != null
				&& selectedEU.getFpEU().getTvClassCd() != null
				&& selectedEU.getFpEU().getTvClassCd()
						.equals(TVClassification.INSIG);
	}

	public TVEUOperatingScenario getSelectedScenario() {
		return selectedScenario;
	}

	public void setSelectedScenario(TVEUOperatingScenario selectedScenario) {
		this.selectedScenario = selectedScenario;
	}

	public boolean isAlternateScenario() {
		return alternateScenario;
	}

	public void setAlternateScenario(boolean alternateScenario) {
		this.alternateScenario = alternateScenario;
	}

	public String getTvRuleCiteCd() {
		return tvRuleCiteCd;
	}

	public void setTvRuleCiteCd(String tvRuleCiteCd) {
		this.tvRuleCiteCd = tvRuleCiteCd;
	}

	public void setTvRuleCiteCd(Integer tvRuleCiteCd) {
		this.tvRuleCiteCd = null;
		if (tvRuleCiteCd != null) {
			this.tvRuleCiteCd = tvRuleCiteCd.toString();
		}
	}

	public String getTvRuleCiteDsc() {
		String ruleCiteDsc = null;
		if (tvRuleCiteTypeCd != null && tvRuleCiteCd != null) {
			if (tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.RULE)) {
				ruleCiteDsc = RuleCitationDef.getData().getItem(tvRuleCiteCd)
						.getDescription();
			} else if (tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.MACT)) {
				ruleCiteDsc = PTIOMACTSubpartDef.getData()
						.getItem(tvRuleCiteCd).getDescription();
			} else if (tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.NESHAP)) {
				ruleCiteDsc = PTIONESHAPSSubpartDef.getData()
						.getItem(tvRuleCiteCd).getDescription();
			} else if (tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.NSPS)) {
				ruleCiteDsc = PTIONSPSSubpartDef.getData()
						.getItem(tvRuleCiteCd).getDescription();
			}
		}
		return ruleCiteDsc;
	}

	public String getTvRuleCiteTypeCd() {
		return tvRuleCiteTypeCd;
	}

	public void setTvRuleCiteTypeCd(String tvRuleCiteTypeCd) {
		this.tvRuleCiteTypeCd = tvRuleCiteTypeCd;
	}

	// flags used to determine type of rule cite in popup
	public boolean isRuleCitation() {
		return tvRuleCiteTypeCd != null
				&& tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.RULE);
	}

	public boolean isMactCitation() {
		return tvRuleCiteTypeCd != null
				&& tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.MACT);
	}

	public boolean isNeshapCitation() {
		return tvRuleCiteTypeCd != null
				&& tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.NESHAP);
	}

	public boolean isNspsCitation() {
		return tvRuleCiteTypeCd != null
				&& tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.NSPS);
	}

	public boolean isHideTradeSecret() {
		return hideTradeSecret;
	}

	public void setHideTradeSecret(boolean hideTradeSecret) {
		this.hideTradeSecret = hideTradeSecret;
	}

	public Object getRuleCiteObject() {
		return ruleCiteObject;
	}

	public void setRuleCiteObject(Object ruleCiteObject) {
		this.ruleCiteObject = ruleCiteObject;
	}

	public TVApplicableReq getApplicableReq() {
		return applicableReq;
	}

	public void setApplicableReq(TVApplicableReq applicableReq) {
		this.applicableReq = applicableReq;
	}

	public String getAppReqTableId() {
		return appReqTableId;
	}

	public void setAppReqTableId(String appReqTableId) {
		this.appReqTableId = appReqTableId;
	}

	public List<TVApplicableReq> getApplicableRequirements() {
		return applicableRequirements;
	}

	public void setApplicableRequirements(
			List<TVApplicableReq> applicableRequirements) {
		this.applicableRequirements = applicableRequirements;
	}

	public List<TVApplicableReq> getStateOnlyRequirements() {
		return stateOnlyRequirements;
	}

	public void setStateOnlyRequirements(
			List<TVApplicableReq> stateOnlyRequirements) {
		this.stateOnlyRequirements = stateOnlyRequirements;
	}

	public TVEUGroup getSelectedEUGroup() {
		return selectedEUGroup;
	}

	public void setSelectedEUGroup(TVEUGroup selectedEUGroup) {
		this.selectedEUGroup = selectedEUGroup;
	}

	public List<ApplicationDocumentRef> getApplicationDocuments() {
		return applicationDocuments;
//		return filterTradeSecretDocs(applicationDocuments);
	}
	

	private List<ApplicationDocumentRef> filterTradeSecretDocs(
			LinkedList<ApplicationDocumentRef> applicationDocuments) {

		List<ApplicationDocumentRef> tradeSecretDocs = 
				new ArrayList<ApplicationDocumentRef>();
		
		for (ApplicationDocumentRef doc : applicationDocuments) {
			if (doc.getTradeSecret()) {
				tradeSecretDocs.add(doc);
			}
		}
		for (ApplicationDocumentRef doc : tradeSecretDocs) {
			applicationDocuments.remove(doc);
		}
		
		return applicationDocuments;
	}

	public boolean isRenderSelectAttachmentTable() {
		// select attachment table should be rendered only if there are
		// documents
		// matching the selected document type (and possibly EAC type)
		return (!usingExistingDoc && getAvailableAttachments().size() > 0);
	}

	public List<ApplicationDocumentRef> getAvailableAttachments() {
		ArrayList<ApplicationDocumentRef> ret = new ArrayList<ApplicationDocumentRef>();
		if (applicationDoc != null
				&& applicationDoc.getApplicationDocumentTypeCD() != null) {
			ArrayList<ApplicationDocumentRef> existingDocs = new ArrayList<ApplicationDocumentRef>();
			ArrayList<ApplicationDocumentRef> docsAtThisLevel = new ArrayList<ApplicationDocumentRef>();
			String selectedEpaEmuId = null;

			try {
				ApplicationDocumentRef[] appDocs;
				appDocs = getApplicationService().retrieveApplicationDocuments(
						application.getApplicationTypeCD(),
						application.getApplicationID());

				if (selectedTreeNode.getType().equals(APPLICATION_NODE_TYPE)) {

					for (ApplicationDocumentRef appDoc : appDocs) {
						getApplicationService().loadDocuments(appDoc,
								isInternalApp());
						docsAtThisLevel.add(appDoc);
					}
				} else {
					selectedEpaEmuId = selectedTreeNode.getDescription();
					for (ApplicationDocumentRef appDoc : appDocs) {
						getApplicationService().loadDocuments(appDoc,
								isInternalApp());
						existingDocs.add(appDoc);
					}
				}

				for (ApplicationEU appEU : application.getEus()) {
					ApplicationDocumentRef[] appEuDocs;
					appEuDocs = getApplicationService()
							.retrieveApplicationEUDocuments(
									application.getApplicationTypeCD(),
									appEU.getApplicationEuId());

					if (appEU.getFpEU().getEpaEmuId().equals(selectedEpaEmuId)) {
						for (ApplicationDocumentRef appEuDoc : appEuDocs) {
							getApplicationService().loadDocuments(appEuDoc,
									isInternalApp());
							docsAtThisLevel.add(appEuDoc);
						}
					} else {
						for (ApplicationDocumentRef appEuDoc : appEuDocs) {
							getApplicationService().loadDocuments(appEuDoc,
									isInternalApp());
							existingDocs.add(appEuDoc);
						}

					}
				}
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Unable to retrieve application attachments.");
				logger.error("Couldn't retrieve attachments from database: "
						+ e.toString());
			} catch (RemoteException e) {
				DisplayUtil
						.displayError("Unable to retrieve application attachments.");
				logger.error("Couldn't retrieve attachments: " + e.toString());
			}

			String docTypeCd = applicationDoc.getApplicationDocumentTypeCD();
			for (ApplicationDocumentRef doc : existingDocs) {
				if (docTypeCd.equals(doc.getApplicationDocumentTypeCD())) {
					boolean docAlreadyAtThisLevel = false;
					for (ApplicationDocumentRef tmpDoc : docsAtThisLevel) {
						if ((doc.getDocumentId() != null && doc.getDocumentId()
								.equals(tmpDoc.getDocumentId()))
								|| (doc.getTradeSecretDocId() != null && doc
										.getTradeSecretDocId().equals(
												tmpDoc.getTradeSecretDocId()))) {
							docAlreadyAtThisLevel = true;
						}
					}
					if (!docAlreadyAtThisLevel) {
						ret.add(doc);
					}
				}
			}
		}
		return ret;
	}

	public void loadSelectedAttachment() {
		if (actionTable != null && actionTable.getValue() != null
				&& actionTable.getValue() instanceof Collection<?>) {
			Iterator<?> it = actionTable.getSelectionState().getKeySet()
					.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				actionTable.setRowKey(obj);
				applicationDoc = (ApplicationDocumentRef) actionTable
						.getRowData();
				usingExistingDoc = true;
			}
		}
	}

	public void setApplicationDocuments(
			List<ApplicationDocumentRef> applicationDocuments) {
		this.applicationDocuments.clear();
		if (applicationDocuments != null) {
			this.applicationDocuments.addAll(applicationDocuments);
		}
	}

	public String getApplicableRequirementsHeader() {
		String header = "Applicable Requirements";
		if (selectedTreeNode != null) {
			if (selectedTreeNode.getType().equals(APPLICATION_NODE_TYPE)) {
				header = "Facility-Wide Requirements";
			} else if (selectedTreeNode.getType().equals(EU_NODE_TYPE)) {
				header = "Emissions Unit-Specific Requirements";
			} else if (selectedTreeNode.getType().equals(EU_GROUP_NODE_TYPE)) {
				header = "Group Requirements";
			}
		}
		return header;
	}

	public TVAltScenarioPteReq getPteRequirement() {
		return pteRequirement;
	}

	public void setPteRequirement(TVAltScenarioPteReq pteRequirement) {
		this.pteRequirement = pteRequirement;
	}

	/**
	 * Flag indicating whether facility inventory may be updated to most recent
	 * version. Only applicable for internal version of the application.
	 * 
	 * @return
	 */
	public boolean isOkToUpdateFacilityProfile() {
		return false;
	}

	/**
	 * Flag indicating whether EUs may be synched with latest facility inventory.
	 * Currently, this only applies to Internal PBRs.
	 * 
	 * @return
	 */
	public boolean isOkToSyncEUsWithProfile() {
		return false;
	}

	/**
	 * Update the application's facility inventory to point to the latest version.
	 * This operation is only available to the DAPC, so the base class version
	 * is a no-op.
	 * 
	 * @param returnEvent
	 */
	public void updateFacilityProfile(ActionEvent actionEvent) {
	}

	/**
	 * Retrieve a new Stars2Object. This is used to populate blank rows in the
	 * Mact, Neshap, and Nsps tables of the PTIO application.
	 * 
	 * @return
	 */
	public Stars2Object getNewStars2Object() {
		return new Stars2Object();
	}

	public TVAltScenarioPteReq getNewTVAltScenarioPteReq() {
		return new TVAltScenarioPteReq();
	}

	public TVCompliance getNewTVComplianceObject() {
		TVCompliance ret = new TVCompliance();
		if (applicableReq != null) {
			ret.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
		}
		return ret;
	}

	public TVComplianceObligations getNewTVComplianceObligationsObject() {
		TVComplianceObligations ret = new TVComplianceObligations();
		if (applicableReq != null) {
			ret.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
		}
		return ret;
	}

	public TVProposedExemptions getNewTVProposedExemptionsObject() {
		TVProposedExemptions ret = new TVProposedExemptions();
		if (applicableReq != null) {
			ret.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
		}
		return ret;
	}

	public TVProposedAltLimits getNewTVProposedAltLimitsObject() {
		TVProposedAltLimits ret = new TVProposedAltLimits();
		if (applicableReq != null) {
			ret.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
		}
		return ret;
	}

	public TVProposedTestChanges getNewTVProposedTestChangesObject() {
		TVProposedTestChanges ret = new TVProposedTestChanges();
		if (applicableReq != null) {
			ret.setTvApplicableReqId(applicableReq.getTvApplicableReqId());
		}
		return ret;
	}

	/**
	 * methods needed for RPC
	 */
	public List<SelectItem> getPermitsForRpc() {
		List<SelectItem> permitSelectList = new ArrayList<SelectItem>();
		if (application instanceof RPCRequest) {
			try {
				List<Permit> permits = getApplicationService()
						.retrievePermitsForRPCRequest(
								((RPCRequest) application).getRpcTypeCd(),
								application.getFacilityId());
				for (Permit permit : permits) {
					SelectItem item = new SelectItem();
					item.setValue(permit.getPermitID());
					String itemListing = permit.getPermitNumber()
							+ " "
							+ PermitTypeDef.getData().getItems()
									.getItemDesc(permit.getPermitType());
					item.setLabel(itemListing);
					item.setDescription(itemListing);
					permitSelectList.add(item);
				}
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
		}
		return permitSelectList;
	}

	// End RPC

	/**
	 * Objects needed to support PBR
	 */
	private String pbrTypeCd;

	private final String modifyApplication() {
		try {
			getApplicationService().modifyApplication(application, true);
			DisplayUtil.displayInfo("Update successful");
		} catch (RemoteException ex) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), ex);
		} finally {
			reloadApplicationSummary();
		}
		return null;
	}

	public String getPbrTypeLongDesc() {
		String ret = "Cannot find the detail description for current PBR type.";
		String ld = PBRTypeDef
				.getLongDscData()
				.getItems()
				.getDescFromAllItem(
						((PBRNotification) application).getPbrTypeCd());
		if (ld != null)
			ret = ld;
		return ret;
	}

	public String getPbrTypeCd() {
		return pbrTypeCd;
	}

	public void setPbrTypeCd(String pbrTypeCd) {
		this.pbrTypeCd = pbrTypeCd;
	}

	public List<Permit> getEuPermits() {
		List<Permit> euPermits = new ArrayList<Permit>();
		try {
			euPermits = getPermitService().searchEuPermits(
					selectedEU.getFpEU().getCorrEpaEmuId());
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return euPermits;
	}

	/*
	 * Remove PBR references in permit applications public PermitEU
	 * getSelectedPermitEU() { PermitEU permitEU = null; if
	 * (ApplicationTypeDef.PBR_NOTIFICATION.equals(application
	 * .getApplicationTypeCD())) { if (selectedPermitEU != null) { permitEU =
	 * selectedPermitEU; } else { if (selectedEU != null) { try { Permit permit
	 * = getPermitService().retrievePermit( application.getApplicationNumber());
	 * if (permit != null && PermitTypeDef.PBR.equals(permit .getPermitType()))
	 * { for (PermitEU eu : permit.getEus()) { if (eu.getCorrEpaEMUID().equals(
	 * selectedEU.getFpEU().getCorrEpaEmuId())) { permitEU = eu;
	 * selectedPermitEU = permitEU; break; } } } } catch (RemoteException e) {
	 * logger.error("Exception retrieving permit " +
	 * application.getApplicationNumber(), e); } } } } return permitEU; }
	 */
/*
	public void pbrAccepted(ReturnEvent returnEvent) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
			return;

		try {
			permit = getPermitService().retrievePermit(
					application.getApplicationNumber());
			List<ValidationMessage> messages = PermitValidation
					.validatePBRRevocation(permit);
			if (AppValidationMsg.validate(messages, true)) {
				permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_FINAL);
				getPermitService().updateFinalPBR(permit,
						InfrastructureDefs.getCurrentUserId());
			}
			reload();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
	}
*/
/*
	public void applyPBRAccepted(ActionEvent actionEvent) {
		try {
			permit = getPermitService().retrievePermit(
					application.getApplicationNumber());
			List<ValidationMessage> messages = PermitValidation
					.validatePBRRevocation(permit);
			if (AppValidationMsg.validate(messages, true)) {
				permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_FINAL);
				getPermitService().updateFinalPBR(permit,
						InfrastructureDefs.getCurrentUserId());
			}
			reload();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		FacesUtil.returnFromDialogAndRefresh();
	}
*/
/*
	public String getPbrNotEligibleForAcceptMsg() {
		String msg = "This PBR cannot be accepted because [EU ID] has an invalid "
				+ "prefix (e.g.,TMP, Z).  This must be sent to the DO/LAA staff person "
				+ "to fix the EU ID in the facility inventory and then sync this request "
				+ "with the current profile.";
		if (application != null) {
			StringBuilder euIds = new StringBuilder();
			for (ApplicationEU appEU : application.getIncludedEus()) {
				// Accept PBR shouldn't work if EU has TMP or Z ID (Mantis 2676)
				if (appEU.getFpEU().getEpaEmuId().startsWith("TMP")
						|| appEU.getFpEU().getEpaEmuId().startsWith("Z")) {
					euIds.append(appEU.getFpEU().getEpaEmuId() + ", ");
				}
			}
			if (euIds.length() > 2) {
				msg = msg.replace("[EU ID]",
						euIds.toString().subSequence(0, euIds.length() - 2));
			}
		}
		return msg;
	}
*/
/*
	public boolean isOkToAcceptPBR() {
		boolean ok = false;
		if (application instanceof PBRNotification) {
			ok = true; // assume ok if app is PBR
			for (ApplicationEU appEU : application.getIncludedEus()) {
				// Accept PBR shouldn't work if EU has TMP or Z ID (Mantis 2676)
				if (appEU.getFpEU().getEpaEmuId().startsWith("TMP")
						|| appEU.getFpEU().getEpaEmuId().startsWith("Z")) {
					ok = false;
					break;
				}
			}
		}
		return ok;
	}
*/
/*
	public void pbrDenied(ReturnEvent returnEvent) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");

		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}

		try {
			permit = getPermitService().retrievePermit(
					application.getApplicationNumber());
			permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_DENIAL);
			getPermitService().updateFinalPBR(permit,
					InfrastructureDefs.getCurrentUserId());
			reload();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
	}
*/
	/*
	public void pbrNoApplicable(ReturnEvent returnEvent) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
			return;
		boolean ok = true;
		try {
			((PBRNotification) application).setDispositionFlag("n");
			getApplicationService().modifyPbrNoApplicableApplication(
					application, getUserID());
		} catch (RemoteException ex) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), ex);
			ok = false;
		}
		if (ok) {
			DisplayUtil.displayInfo("Update successful");
		}
		reload();
	}
	*/

	// End for PBR
	/**
	 * Objects needed to support RPR, RPE
	 */
	private Integer permitId;
	private PermitInfo permitInfo;
	private List<SelectItem> permitList;
	private PermitEU rpeEU;
	private boolean fromPBR = false;

	public Integer getPermitId() {
		return permitId;
	}

	public void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}

	public List<SelectItem> getPermitList() {
		return permitList;
	}

	public void setPermitList(List<SelectItem> permitList) {
		this.permitList = permitList;
	}

	public String workOnRPR() {
		/*
		EmissionUnit tempExcludedEu = selectedEU.getFpEU();
		Application app = null;
		try {
			ApplicationService appBO = getApplicationService();
			ApplicationSearchResult[] results = appBO
					.retrieveApplicationSearchResults(null, null,
							application.getFacilityId(), null, null, null,
							ApplicationTypeDef.RPR_REQUEST, null, false, null,
							null, null, true);
			for (ApplicationSearchResult result : results) {
				Application tApp = appBO.retrieveApplication(result
						.getApplicationId());
				if (((RPRRequest) tApp).getPermitId().equals(permitId)) {
					for (ApplicationEU aeu : tApp.getEus()) {
						if (aeu.getFpEU().equals(tempExcludedEu)) {
							application = tApp;
							loadApplication();
							return APP_DETAIL_MANAGED_BEAN;
						}
					}
					if (tApp.getSubmittedDate() == null)
						app = tApp;
				}
			}
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}

		if (app != null) {
			application = app;
			Stars2TreeNode tSelectedTreeNode = selectedTreeNode;
			loadApplication();
			selectedTreeNode = tSelectedTreeNode;
		} else {
			fromPBR = true;
			if (newApplication(RPRRequest.class, application.getFacility()) < 0) {
				return null;
			}
		}

		selectedExcludedEU = tempExcludedEu;
		includeEU();
		*/
		return APP_DETAIL_MANAGED_BEAN;
		
	}

	/**
	 * This permit is the application related permit. It is now used for RPE
	 * application to find the fee and invoice
	 * 
	 * @return
	 */
	public Permit getPermit() {
		return permit;
	}

	/**
	 * This permit is the old issued permit.
	 * 
	 * @return
	 */
	public Permit getOldPermit() {
		return permitInfo.getPermit();
	}

	/**
	 * This permit is the application related permit. It is now used for RPE
	 * application to find the fee and invoice
	 * 
	 * @param permit
	 */
	public void setPermit(Permit permit) {
		this.permit = permit;
	}

	public String saveFeeChange() {
		modifyApplication();
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public String cancelFeeChange() {
		reloadApplicationSummary();
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public PermitEU getRpeEU() {
		if (rpeEU != null
				&& rpeEU.getCorrEpaEMUID().equals(
						selectedEU.getFpEU().getCorrEpaEmuId()))
			return rpeEU;

		for (PermitEUGroup peug : permit.getEuGroups())
			for (PermitEU pe : peug.getPermitEUs())
				if (pe.getCorrEpaEMUID().equals(
						selectedEU.getFpEU().getCorrEpaEmuId())) {
					rpeEU = pe;
				}
		return rpeEU;
	}

	public void setRpeEU(PermitEU rpeEU) {
		this.rpeEU = rpeEU;
	}

	public String euFeeClicked() {
		selectedEUGroup = null;
		selectedExcludedEU = null;

		Integer applicationEuId = null;
		Integer ceuId = rpeEU.getCorrEpaEMUID();
		for (ApplicationEU ae : application.getEus())
			if (ae.getFpEU().getCorrEpaEmuId().equals(ceuId))
				applicationEuId = ae.getApplicationEuId();

		Stars2TreeNode root = (Stars2TreeNode) treeData.getNodeById("0");
		String nodeID = Integer.toString(applicationEuId);
		selectedTreeNode = root.findNode(EU_NODE_TYPE, nodeID);

		setSelectedEU(null);
		selectedPermitEU = null;

		return nodeClicked();
	}

	public void rpeDenied(ReturnEvent returnEvent) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
			return;

		((RPERequest) application).setDispositionFlag(RPERequest.DENIED);
		modifyApplication();
		reload();
	}

	public void rpeDeadEnded(ReturnEvent returnEvent) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
			return;

		((RPERequest) application).setDispositionFlag(RPERequest.DEAD_ENDED);
		modifyApplication();
		reload();
	}

	public void rprReturned(ReturnEvent returnEvent) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO))
			return;

		((RPRRequest) application).setDispositionFlag(RPRRequest.RETURNED);
		modifyApplication();
		reload();
	}

	/**
	 * PBR and RPC attachments
	 * 
	 */
	public void initializeAttachmentBean() {
		/*
		 * STEP 1 Get a reference to the Attachment backing bean
		 */
		Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
		a.addAttachmentListener(this);
		a.addAttachmentUpdateListener(this);

		/*
		 * STEP 2 Create a new, empty Document object, set its FacilityID and
		 * give the attachment backing bean a reference to it.
		 */
		a.setFacilityId(application.getFacilityId());
		a.setSubPath("Applications");
		a.setObjectId(Integer.toString(application.getApplicationID()));

		// attachment type available for all application types
		a.setHasDocType(true);
		a.setStaging(isPortalApp());

		// enable new attachment button only if the application is not submitted
		// or the application is being viewed internally
		if (isPublicApp()) {
			a.setNewPermitted(false);
		} else {
			a.setNewPermitted((application.getSubmittedDate() == null || isInternalApp()) && !editMode);
		}

		/*
		 * STEP 3 Set the picklist in the backing bean for the document types
		 */
		if (application instanceof PBRNotification) {
			a.setAttachmentTypesDef(PBRNotifDocTypeDef.getData().getItems());
			a.setAttachmentList(((PBRNotification) application)
					.getPbrDocuments().toArray(new Attachment[0]));
		} else if (application instanceof RPCRequest) {
			a.setAttachmentTypesDef(RPCRequestDocTypeDef.getData().getItems());
			a.setAttachmentList(((RPCRequest) application).getRpcDocuments()
					.toArray(new Attachment[0]));
		}
	}

	public void cancelAttachment() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public AttachmentEvent createAttachment(Attachments attachment)
			throws AttachmentException {
		boolean ok = true;
		if (attachment.getDocument() == null) {
			// should never happen
			logger.error("Attempt to process null attachment");
			ok = false;
		} else {
			if (attachment.getDocument().getDescription() == null) {
				DisplayUtil
						.displayError("Please specify the description for this attachment");
				ok = false;
			}
			if (attachment.getDocument().getDocTypeCd() == null) {
				DisplayUtil
						.displayError("Please specify an attachment type for this attachment");
				ok = false;
			}
			if (attachment.getDocument().getFileName() == null) {
				DisplayUtil
						.displayError("Please specify the file to upload for this attachment");
				ok = false;
			}
		}
		if (ok) {
			try {
				getApplicationService().uploadApplicationAttachment(attachment,
						application);
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
			FacesUtil.returnFromDialogAndRefresh();
			// need to reload application to see documents in table
			reloadApplicationSummary();
		}
		return null;
	}

	public AttachmentEvent deleteAttachment(Attachments attachment) {
		try {
			Attachment doc = attachment.getTempDoc();
			if (application instanceof PBRNotification) {
				getApplicationService().removePBRNotificationDocument(
						new PBRNotificationDocument(doc));
			} else if (application instanceof RPCRequest) {
				getApplicationService().removeRPCRequestDocument(
						new RPCRequestDocument(doc));
			} else {
				// this should never happen
				logger.error("Unexpected call to deleteAttachment from application of type "
						+ application.getClass().getName());
				doc = null;
			}
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		FacesUtil.returnFromDialogAndRefresh();
		// reload application to see change in table
		reloadApplicationSummary();
		return null;
	}

	public AttachmentEvent updateAttachment(Attachments attachment) {
		FacesUtil.returnFromDialogAndRefresh();
		Attachment doc = attachment.getTempDoc();
		try {
			if (application instanceof PBRNotification) {
				getApplicationService().modifyPBRNotificationDocument(
						new PBRNotificationDocument(doc));
			} else if (application instanceof RPCRequest) {
				getApplicationService().modifyRPCRequestDocument(
						new RPCRequestDocument(doc));
			} else {
				// this should never happen
				logger.error("Unexpected call to updateAttachment from application of type "
						+ application.getClass().getName());
			}
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return null;
	}

	public String preparePrintableDocs() {
		if (null != application) {
			reloadApplicationWithAllEUs();
		}
		
		tradeSecretStatusChanged = true;
		if (!isHideTradeSecret() && !isTradeSecret(false)) {
			// Since no trade secrets, give error instead;
			DisplayUtil.displayWarning("There is no trade secret information in the application or file attachments.  Use Download/Print button instead.");
			// return "dialog:appNoTsDocuments";
			return null; // just show the warning message is fine. -- Chuan-Hui
		} 
		preparePrintableDocumentList(false); // this sets flag in application
										// indicating whether there are trade
										// secrets
										// this sets flag in application
										
		// enhanced download/print capability to print referenced eus only for NSR application
		// TFS task 5255
		if(null != application 
				&& application.getApplicationTypeCD().equals(ApplicationTypeDef.PTIO_APPLICATION)) {
			preparePrintableDocumentReferencedEUsList(false);
		}
		
	    preparePrintableAttachmentList(false);
	    return "dialog:appPrintableDocuments";
	}

	// 'View What You are About to Submit' Button in the portal
	public String prepareWhatYouSubmit() {
		tradeSecretStatusChanged = true;
		hideTradeSecret = !isTradeSecret(true);
		preparePrintableDocumentList(true);

		preparePrintableAttachmentList(true);
		return "dialog:appPrintableDocuments";
	}

	public List<Document> getPrintableDocumentList() {
		return printableDocumentList;
	}

	public void preparePrintableDocumentList(boolean includeAllAttachments) {
		List<Document> docList = null;
		if (application != null) {
			try {
				
				Facility fac = getFacilityService()
						.retrieveFacilityProfile(
								application.getFacility().getFpId(),
							isPortalApp() && !readOnly, null);

				Document facilityDoc = getFacilityService()
						.generateTempFacilityProfileReport(
								fac, null);
				facilityDoc
						.setDescription("Printable View of Facility Inventory");
				docList = getApplicationService().getPrintableDocumentList(
						application, facilityDoc, readOnly, hideTradeSecret, includeAllAttachments);
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
		}
		printableDocumentList = docList;
	}

	public List<Document> getPrintableAttachmentList() {
		return printableAttachmentList;
	}

	public void preparePrintableAttachmentList(boolean includeAllAttachments) {
		List<Document> docList = null;
		if (application != null) {
			try {
				getApplicationService().loadAllDocuments(application, readOnly);
				docList = getApplicationService().getPrintableAttachmentList(
						application, readOnly, hideTradeSecret, includeAllAttachments);
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
		}
		printableAttachmentList = docList;
	}

	public String getReasonEUNotIncludable() {
		String reason = "";
		if (application instanceof RPCRequest
				|| application instanceof RPERequest
				|| application instanceof RPRRequest) {
			try {
				reason = getApplicationService().retrieveReasonEUNotIncludable(
						application, selectedEU);
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
		}
		return reason;
	}

	private transient UIXTable applicableReqTable;
	private transient UIXTable stateOnlyReqTable;

	public boolean isOkToEditApplicableReqTable() {
		boolean ok = false;
		if (applicableReqTable != null) {
			ok = applicableReqTable.getSelectionState().getSize() == 1;
		}
		return ok;
	}

	public boolean isOkToDeleteApplicableReqTable() {
		boolean ok = false;
		if (applicableReqTable != null) {
			ok = applicableReqTable.getSelectionState().getSize() > 0;
		}
		return ok;
	}

	public boolean isOkToEditStateOnlyReqTable() {
		boolean ok = false;
		if (stateOnlyReqTable != null) {
			ok = stateOnlyReqTable.getSelectionState().getSize() == 1;
		}
		return ok;
	}

	public boolean isOkToDeleteStateOnlyReqTable() {
		boolean ok = false;
		if (stateOnlyReqTable != null) {
			ok = stateOnlyReqTable.getSelectionState().getSize() > 0;
		}
		return ok;
	}

	public UIXTable getApplicableReqTable() {
		return applicableReqTable;
	}

	public void setApplicableReqTable(UIXTable applicableReqTable) {
		this.applicableReqTable = applicableReqTable;
	}

	public UIXTable getStateOnlyReqTable() {
		return stateOnlyReqTable;
	}

	public void setStateOnlyReqTable(UIXTable stateOnlyReqTable) {
		this.stateOnlyReqTable = stateOnlyReqTable;
	}

	public Timestamp getMaxReceivedDate() {
		return new Timestamp(System.currentTimeMillis());
	}

	public String getEuModificationDesc() {
		String currentEUPurposeCd = ((PTIOApplicationEU) selectedEU)
				.getPtioEUPurposeCD();
		return euModificationDesc.get(currentEUPurposeCd);
	}

	public void setEuModificationDesc(String euModificationDesc) {
		String currentEUPurposeCd = ((PTIOApplicationEU) selectedEU)
				.getPtioEUPurposeCD();
		if (currentEUPurposeCd != null) {
			this.euModificationDesc.put(currentEUPurposeCd, euModificationDesc);
		}
	}

	public String getCapEmissionsValueFormat() {
		String format = null;
		try {
			format = getApplicationService().getCapEmissionsValueFormat();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return format;
	}

	public String getCapLbHrEmissionsValueFormat() {
		String format = null;
		try {
			format = getApplicationService().getCapLbHrEmissionsValueFormat();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return format;
	}

	public String getEmissionsValueFormat() {
		if (pollutantType.equals(POLLUTANT_HAP_TAC)) {
			return getTvHapEmissionsValueFormat();
		} else if (pollutantType.equals(POLLUTANT_CAP)) {
			return getTvCapEmissionsValueFormat();
		} else if (pollutantType.equals(POLLUTANT_GHG)) {
			return getGhgEmissionsValueFormat();
		} else if (pollutantType.equals(POLLUTANT_OTH)) {
			return getOthEmissionsValueFormat();
		}

		return null;
	}

	public String getHapEmissionsValueFormat() {
		String format = null;
		try {
			format = getApplicationService().getHapEmissionsValueFormat();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return format;
	}

	public String getTvCapEmissionsValueFormat() {
		String format = null;
		try {
			format = getApplicationService().getTvCapEmissionsValueFormat();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return format;
	}

	public String getTvHapEmissionsValueFormat() {
		String format = null;
		try {
			format = getApplicationService().getTvHapEmissionsValueFormat();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return format;
	}

	public String getGhgEmissionsValueFormat() {
		String format = null;
		try {
			format = getApplicationService().getGhgEmissionsValueFormat();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return format;
	}

	public String getOthEmissionsValueFormat() {
		String format = null;
		try {
			format = getApplicationService().getOthEmissionsValueFormat();
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return format;
	}

	public boolean isLegacyTV() {
		return (application instanceof TVApplication && application.isLegacy());
	}

	public boolean isRelocationClass() {
		if (getApplication() == null) {
			logger.error("Unable to get application class");
			return false;
		}
		if (getApplication().getApplicationTypeCD().equals(
				RelocationTypeDef.INTENT_TO_RELOCATE)
				|| getApplication().getApplicationTypeCD().equals(
						RelocationTypeDef.SITE_PRE_APPROVAL)
				|| getApplication().getApplicationTypeCD().equals(
						RelocationTypeDef.RELOCATE_TO_PREAPPROVED_SITE)) {
			return true;
		}
		return false;
	}

	public boolean isPbrClass() {
		if (getApplication() == null) {
			logger.error("Unable to get application class");
			return false;
		}
		/*
		 * Remove PBR references in permit applications if
		 * (getApplication().getApplicationTypeCD().equals(
		 * ApplicationTypeDef.PBR_NOTIFICATION) && isPermitEditable()) { return
		 * true; }
		 */
		return false;
	}

	public boolean isDelegationClass() {
		if (getApplication() == null) {
			logger.error("Unable to get application class");
			return false;
		}
		if (getApplication().getApplicationTypeCD().equals(
				ApplicationTypeDef.DELEGATION_OF_RESPONSIBILITY)) {
			return true;
		}
		return false;
	}

	public Class<? extends Application> getApplicationClass() {
		return getApplication() == null ? null : getApplication().getClass();
	}

	public List<EmissionUnit> getIncludedEmissionUnits() {
		ArrayList<EmissionUnit> euList = new ArrayList<EmissionUnit>();

		for (ApplicationEU appEU : application.getIncludedEus()) {
			EmissionUnit fpEU = application.getFacility().getEmissionUnit(
					appEU.getFpEU().getEmuId());
			if (fpEU == null) {
				logger.error("No Facility EU found matching EmuId: "
						+ appEU.getFpEU().getEmuId()
						+ " in facility with fp_id: "
						+ application.getFacility().getFpId());
				continue;
			}
			euList.add(fpEU);
		}
		return euList;
	}
	
	public boolean isTradeSecret(boolean includeAllAttachments) {
		// The operation to check trade secret status is fairly expensive,
		// so only do it when necessary
		if (tradeSecretStatusChanged) {
			try {
				tradeSecret = getApplicationService().isTradeSecret(
						application, readOnly, includeAllAttachments);
				tradeSecretStatusChanged = false;
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}
		}
		return tradeSecret;
	}

	public Stars2TreeNode getSaveSelectedTreeNode() {
		return saveSelectedTreeNode;
	}

	public void setSaveSelectedTreeNode(Stars2TreeNode saveSelectedTreeNode) {
		this.saveSelectedTreeNode = saveSelectedTreeNode;
	}

	public boolean isAttachmentDeletePermitted(Attachments attachment) {
		return isOkToModifyAttachment(attachment);
	}

	public boolean isAttachmentUpdatePermitted(Attachments attachment) {
		return isOkToModifyAttachment(attachment);
	}

	private boolean isOkToModifyAttachment(Attachments attachment) {
		boolean okToMod = true;
		// retrieve attachment from managed bean if this is being
		// called from the attachment table (as opposed to the edit popup)
		Attachment attachmentDoc = (Attachment) FacesUtil
				.getManagedBean("report");
		if (attachmentDoc == null) {
			attachmentDoc = attachment.getDocument();
		}
		if (application != null) {
			if (attachmentDoc != null) {
				if (attachmentDoc.getLastModifiedTSLong() > 0
						&& attachmentDoc.getLastModifiedTSLong() < application
								.getSubmittedDateLong()) {
					// don't allow delete for attachments added prior to
					// application submission.
					okToMod = false;
				}

			}
		}
		return okToMod;
	}

	public UploadedFileInfo getPublicFileInfo() {
		return publicFileInfo;
	}

	public UploadedFileInfo getTsFileInfo() {
		return tsFileInfo;
	}

	public List<Contact> getFacilityContactList() {
		List<Contact> facilityContactList = new ArrayList<Contact>();
		try {
			if (isPublicApp()) {
				facilityContactList = new ArrayList<Contact>();
			} else if (isInternalApp() || readOnly) {
				facilityContactList = getFacilityService()
						.retrieveFacilityContacts(application.getFacilityId());
			} else if (isPortalApp()) {
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				myTasks.setFromHomeContact(false);
				facilityContactList = getFacilityService()
						.retrieveFacilityContacts(application.getFacilityId(),
								true);
			}
		} catch (RemoteException re) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), re);
			DisplayUtil.displayError("Accessing facility contacts failed");
		}
		return facilityContactList;
	}

	protected final void getFacilityContacts() {
		if (facilityContacts == null) {
			facilityContacts = new ArrayList<ContactUtil>();
			ContactUtil tempCU;
			for (Contact tempContact : getFacilityContactList()) {
				if (tempContact.getContactTypes().isEmpty()
						&& tempContact.getEndDate() == null) {
					facilityContacts.add(new ContactUtil(tempContact));
					continue;
				}

				for (ContactType tempCT : tempContact.getContactTypes()) {
					if (tempCT.getEndDate() == null) {
						tempCU = new ContactUtil(tempContact, tempCT);
						facilityContacts.add(tempCU);
					}
				}
			}
			facilityContactsWrapper.setWrappedData(facilityContacts);
		}
	}

	public TableSorter getFacilityContactsWrapper() {
		getFacilityContacts();
		return facilityContactsWrapper;
	}

	public boolean isOkToAddFacilityContact() {
		boolean ok = false;
		if (facilityContactsWrapper.getTable() != null
				&& facilityContactsWrapper.getTable().getValue() != null
				&& facilityContactsWrapper.getTable().getValue() instanceof TableSorter) {
			UIXTable table = ((TableSorter) facilityContactsWrapper.getTable()
					.getValue()).getTable();
			ok = table.getSelectionState().getSize() == 1;
		}
		return ok;
	}

	public boolean getUseOtherAttachments() {
		boolean useOtherAttachments = false;
		if (application != null
				&& ("PBR".equals(application.getApplicationTypeCD())
						|| "RPC".equals(application.getApplicationTypeCD())
						|| "ITR".equals(application.getApplicationTypeCD())
						|| "SPA".equals(application.getApplicationTypeCD())
						|| "RPS".equals(application.getApplicationTypeCD()) || "DOR"
							.equals(application.getApplicationTypeCD()))) {
			useOtherAttachments = true;
		}
		return useOtherAttachments;
	}

	public void applyFacilityContact(ActionEvent actionEvent) {
		List<Object> selectedRows = new ArrayList<Object>();
		if (facilityContactsWrapper.getTable() != null
				&& facilityContactsWrapper.getTable().getValue() != null
				&& facilityContactsWrapper.getTable().getValue() instanceof TableSorter) {
			UIXTable table = ((TableSorter) facilityContactsWrapper.getTable()
					.getValue()).getTable();
			Object oldKey = table.getRowKey();
			Iterator<?> it = table.getSelectionState().getKeySet().iterator();
			while (it.hasNext()) {
				table.setRowKey(it.next());
				selectedRows.add(table.getRowData());
			}
			table.setRowKey(oldKey);
			if (selectedRows.size() == 1) {
				ContactUtil selectedContact = (ContactUtil) selectedRows.get(0);
				if (selectedContact != null) {
					FacesUtil.returnFromDialogAndRefresh();
					// use contact selected from dialog and make note
					// that a facility contact is being used.
					applicationContact = selectedContact.getContact();
					usingFacilityContact = true;

					// clear out old contact information to avoid mismatch
					// with last modified value on update
					application.setContact(null);

					// force contact information to be read only
					// if this is not done, the contact data will not be
					// displayed
					// on the screen
					contactReadOnly = true;

					// has contact
					newContact = true;
				}
			} else {
				DisplayUtil
						.displayError("Please select one row from the contact table "
								+ "before selecting the 'Use Selected Contact' button.");
			}
		} else {
			DisplayUtil
					.displayError("An error occurred while attempting to update the application contact.");
		}
	}

	public String resetContact() {
		applicationContact = new Contact();
		applicationContact.setAddress(new Address());
		application.setContact(null);
		usingFacilityContact = false;
		contactReadOnly = true;
		newContact = false;
		return null;
	}

	public String newContact() {
		createApplicationContact = new Contact();
		createApplicationContact.setAddress(new Address());
		createApplicationContact.setCompanyId(application.getFacility()
				.getOwner().getCompany().getCompanyId());
		usingFacilityContact = false;
		contactReadOnly = false;
		newContact = true;
		return null;
	}

	public String newFacilityContact() {
		newContact();
		createNewContact = true;
		return null;
	}

	public String loadFacilityContact() {
		String ret = "dialog:appFacilityContacts";
		createNewContact = false;
		return ret;
	}

	public String resetCreateFacilityContact(ActionEvent ae) {
		newContact();
		return null;
	}

	public String cancelCreateFacilityContact(ActionEvent ae) {
		newContact();
		createNewContact = false;
		return null;
	}

	public String createFacilityContact() {
		ValidationMessage[] validationMessages;
		try {
			validationMessages = getContactService().validateCreateContact(
					createApplicationContact);
			if (displayValidationMessages("createContact:", validationMessages)) {
				return FAIL;
			}
			application.setContact(null);
			applicationContact = createApplicationContact;
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Create contact failed");
			return FAIL;
		}

		DisplayUtil.displayInfo("New contact loaded successfully");
		FacesUtil.returnFromDialogAndRefresh();

		return SUCCESS;
	}

	public Integer getRelatedPermitId() {
		return relatedPermitId;
	}

	public void setRelatedPermitId(Integer relatedPermitId) {
		this.relatedPermitId = relatedPermitId;
	}

	public boolean isContactReadOnly() {
		return contactReadOnly;
	}

	public void setContactReadOnly(boolean contactReadOnly) {
		this.contactReadOnly = contactReadOnly;
	}

	public boolean isUsingExistingDoc() {
		return usingExistingDoc;
	}

	public void setUsingExistingDoc(boolean usingExistingDoc) {
		this.usingExistingDoc = usingExistingDoc;
	}

	protected void setRelocation() {
		Relocation gi = (Relocation) FacesUtil.getManagedBean("relocation");
		gi.setRelocateRequest((RelocateRequest) application);
	}

	public boolean isDeleteDocAllowed() {
		return deleteDocAllowed;
	}

	public void setDeleteDocAllowed(boolean deleteDocAllowed) {
		this.deleteDocAllowed = deleteDocAllowed;
	}

	public String getPtioApplicationSageGrouseCd() {
		return ptioApplicationSageGrouseCd;
	}

	public void setPtioApplicationSageGrouseCd(
			String ptioApplicationSageGrouseCd) {
		this.ptioApplicationSageGrouseCd = ptioApplicationSageGrouseCd;
	}

	public String getApplicationType() {
		if (application == null)
			return "";

		if (application instanceof PTIOApplication)
			return "PTIO";

		if (application instanceof TVApplication)
			return "TV";

		return "";
	}

	public String getTradeSecretValue() {
		if (isTradeSecret(false))
			return "true";
		else
			return "false";
	}

	public void setTradeSecretValue(String tradeSecretValue) {
		this.tradeSecretValue = tradeSecretValue;
	}

	public TableSorter getBactPollutantWrapper() {
		return bactPollutantWrapper;
	}

	public void setBactPollutantWrapper(TableSorter bactPollutantWrapper) {
		this.bactPollutantWrapper = bactPollutantWrapper;
	}

	public TableSorter getLaerPollutantWrapper() {
		return laerPollutantWrapper;
	}

	public void setLaerPollutantWrapper(TableSorter laerPollutantWrapper) {
		this.laerPollutantWrapper = laerPollutantWrapper;
	}

	public TableSorter getFacWideReqWrapper() {
		return facWideReqWrapper;
	}

	public void setFacWideReqWrapper(TableSorter facWideReqWrapper) {
		this.facWideReqWrapper = facWideReqWrapper;
	}

	public Integer getRequiredAttachmentCount() {
		Integer count = 0;

		for (ApplicationDocumentRef docRef : this.applicationDocuments) {
			if (docRef.isRequiredDoc())
				count++;
		}

		return count;
	}

	public boolean isEditModeFacWideReq() {
		return editModeFacWideReq;
	}

	public void setEditModeFacWideReq(boolean editModeFacWideReq) {
		this.editModeFacWideReq = editModeFacWideReq;
	}

	public FacilityWideRequirement getFacWideReq() {
		return facWideReq;
	}

	public void setFacWideReq(FacilityWideRequirement facWideReq) {
		this.facWideReq = facWideReq;
	}

	public void deleteBACTEmissions() {
		List<NSRApplicationBACTEmission> delObjects = new ArrayList<NSRApplicationBACTEmission>();
		Iterator<?> it = bactPollutantWrapper.getTable().getSelectionState()
				.getKeySet().iterator();
		Object oldKey = bactPollutantWrapper.getTable().getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			bactPollutantWrapper.getTable().setRowKey(obj);
			delObjects.add((NSRApplicationBACTEmission) bactPollutantWrapper
					.getTable().getRowData());
		}

		for (NSRApplicationBACTEmission delObject : delObjects) {
			if (selectedEU instanceof PTIOApplicationEU) {
				((PTIOApplicationEU) selectedEU).removeBACTEmission(delObject);
			}
		}

		bactPollutantWrapper.getTable().setRowKey(oldKey);
		bactPollutantWrapper.getTable().getSelectionState().clear();

		if (selectedEU instanceof PTIOApplicationEU) {
			bactPollutantWrapper
					.setWrappedData(((PTIOApplicationEU) selectedEU)
							.getBactEmissions());
		}
	}

	public void addBACTEmission() {
		NSRApplicationBACTEmission emission = new NSRApplicationBACTEmission();
		if (selectedEU instanceof PTIOApplicationEU) {
			((PTIOApplicationEU) selectedEU).addBACTEmission(emission);
			bactPollutantWrapper
					.setWrappedData(((PTIOApplicationEU) selectedEU)
							.getBactEmissions());
		}
	}

	public void deleteLAEREmissions() {
		List<NSRApplicationLAEREmission> delObjects = new ArrayList<NSRApplicationLAEREmission>();
		Iterator<?> it = laerPollutantWrapper.getTable().getSelectionState()
				.getKeySet().iterator();
		Object oldKey = laerPollutantWrapper.getTable().getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			laerPollutantWrapper.getTable().setRowKey(obj);
			delObjects.add((NSRApplicationLAEREmission) laerPollutantWrapper
					.getTable().getRowData());
		}

		for (NSRApplicationLAEREmission delObject : delObjects) {
			if (selectedEU instanceof PTIOApplicationEU) {
				((PTIOApplicationEU) selectedEU).removeLAEREmission(delObject);
			}
		}

		laerPollutantWrapper.getTable().setRowKey(oldKey);
		laerPollutantWrapper.getTable().getSelectionState().clear();

		if (selectedEU instanceof PTIOApplicationEU) {
			laerPollutantWrapper
					.setWrappedData(((PTIOApplicationEU) selectedEU)
							.getLaerEmissions());
		}
	}

	public void addLAEREmission() {
		NSRApplicationLAEREmission emission = new NSRApplicationLAEREmission();
		if (selectedEU instanceof PTIOApplicationEU) {
			((PTIOApplicationEU) selectedEU).addLAEREmission(emission);
			laerPollutantWrapper
					.setWrappedData(((PTIOApplicationEU) selectedEU)
							.getLaerEmissions());
		}
	}

	public void applyRequiredDoc(ActionEvent actionEvent) {
		ApplicationDocument publicDoc = null;
		ApplicationDocument tsDoc = null;
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		try {
			if (publicFileInfo == null && fileToUpload != null) {
				publicFileInfo = new UploadedFileInfo(fileToUpload);
			}
			if (tsFileInfo == null && tsFileToUpload != null) {
				tsFileInfo = new UploadedFileInfo(tsFileToUpload);
			}
			if (applicationDoc.getDocumentId() == null) {
				// new attachment

				if (fileToUpload == null) {
					validationMessages
							.add(new ValidationMessage("requiredDocFile",
									"Please specify a Public File to Upload",
									ValidationMessage.Severity.ERROR,
									"requiredDocFile"));
				} else if (!DocumentUtil.isValidFileExtension(fileToUpload)){
					validationMessages.add(new ValidationMessage("requiredDocFile",
							DocumentUtil.invalidFileExtensionMessage(null), ValidationMessage.Severity.ERROR, "requiredDocFile")); 
					publicFileInfo = null;
					
				}

				if (applicationDoc.getDescription() == null
						|| applicationDoc.getDescription().trim().isEmpty()) {
					validationMessages
							.add(new ValidationMessage("descriptionText",
									"Please provide the description",
									ValidationMessage.Severity.ERROR,
									"descriptionText"));
				}
				
				if (tsFileToUpload != null && !DocumentUtil.isValidFileExtension(tsFileToUpload)){
					validationMessages.add(new ValidationMessage("tradeSecretFile",
							DocumentUtil.invalidFileExtensionMessage("trade secret"), ValidationMessage.Severity.ERROR, "tradeSecretFile")); 
					tsFileInfo = null;
				} else if (tsFileToUpload != null
						&& (applicationDoc.getTradeSecretReason() == null || applicationDoc
								.getTradeSecretReason().trim().isEmpty())) {
					validationMessages.add(new ValidationMessage(
							"tradeSecretReason",
							"Please provide trade secret justification",
							ValidationMessage.Severity.ERROR,
							"tradeSecretReason"));
				}

				if (tsFileToUpload == null
						&& (applicationDoc.getTradeSecretReason() != null && !applicationDoc
								.getTradeSecretReason().trim().isEmpty())) {
					validationMessages
							.add(new ValidationMessage(
									"tradeSecretReason",
									"The Trade Secret Justification field should only be populated when a trade secret document is specified",
									ValidationMessage.Severity.ERROR,
									"tradeSecretReason"));
				}

			} else {
				// updating existing attachment information

				if (applicationDoc.getTradeSecretDocId() != null
						&& (applicationDoc.getTradeSecretReason() == null || applicationDoc.getTradeSecretReason().trim().isEmpty())) {
					validationMessages.add(new ValidationMessage(
							"tradeSecretReason",
							"Please provide trade secret justification",
							ValidationMessage.Severity.ERROR,
							"tradeSecretReason"));
				}
				if (applicationDoc.getDescription() == null
						|| applicationDoc.getDescription().trim().isEmpty()) {
					validationMessages
							.add(new ValidationMessage("descriptionText",
									"Please provide the description",
									ValidationMessage.Severity.ERROR,
									"descriptionText"));

				}

			}
			if (validationMessages.size() == 0) {
				if (applicationDoc.getDocumentId() == null) {

					publicDoc = getApplicationService()
							.uploadApplicationDocument(application,
									publicFileInfo, "", getUserID());
					applicationDoc.setDocumentId(publicDoc.getDocumentID());

					if (tsFileToUpload != null) {
						tsDoc = getApplicationService()
								.uploadApplicationDocument(application,
										tsFileInfo, "", getUserID());
						applicationDoc.setTradeSecretDocId(tsDoc
								.getDocumentID());
					}

				}

				getApplicationService().modifyApplicationDocument(
						applicationDoc);

				// avoid data loss during attachment upload
				refreshAttachments();
				if (!editMode) {
					loadApplicationSummary(application.getApplicationID());
				}

				fileToUpload = null;
				publicFileInfo = null;
				tsFileToUpload = null;
				tsFileInfo = null;

				boolean isEUDoc = selectedTreeNode != null
						&& !selectedTreeNode.getType().equals(
								APPLICATION_NODE_TYPE);

				if (isEUDoc) {
					refreshAttachments();
					if (!editMode) {
						reloadWithSelectedEU();
					}
				}

				FacesUtil.returnFromDialogAndRefresh();
			} else {
				displayValidationMessages("",
						validationMessages.toArray(new ValidationMessage[0]));
			}
		} catch (Exception e) {
			DisplayUtil
					.displayError("Exception calling Upload Required Attachment.");
			logger.error("Exception calling Upload Required Attachment"
					+ e.getMessage());
		}

		// reset this to true if it has been set to false
		deleteDocAllowed = true;

	}

	public void removeRequiredDocAttachment(ActionEvent actionEvent) {
		try {
			getApplicationService()
					.removeApplicationDocumentDoc(applicationDoc);
			applicationDoc.setDocumentId(null);
			// if required document is being removed and a similar document has
			// been attached as optional document,
			// then make the optional document as now required
			for (ApplicationDocumentRef appDocRef : applicationDocuments) {
				if (appDocRef.getApplicationDocumentTypeCD().equals(
						applicationDoc.getApplicationDocumentTypeCD())
						&& !appDocRef.isRequiredDoc()) {
					appDocRef.setRequiredDoc(true);
					getApplicationService()
							.modifyApplicationDocument(appDocRef);
					getApplicationService().removeApplicationDocument(
							applicationDoc);
					break;
				}
			}

			// avoid data loss during attachment delete
			refreshAttachments();
			if (!editMode) {
				loadApplicationSummary(application.getApplicationID());
			}

			boolean isEUDoc = selectedTreeNode != null
					&& !selectedTreeNode.getType()
							.equals(APPLICATION_NODE_TYPE);

			if (isEUDoc) {
				refreshAttachments();
				if (!editMode) {
					reloadWithSelectedEU();
				}
			}

			FacesUtil.returnFromDialogAndRefresh();
			DisplayUtil.displayInfo("The attachment has been removed.");
		} catch (Exception e) {
			DisplayUtil
					.displayError("Exception calling Delete Required Attachment.");
			logger.error("Exception calling Delete Required Attachment"
					+ e.getMessage());
		}
	}

	private boolean validateApplicationRequiredFields() {
		boolean isValid = true;
		String containerId = "";
		List<ValidationMessage> validationaMessages = new ArrayList<ValidationMessage>();

		if (this.application instanceof PTIOApplication) {
			validateNSRAppRequiredFields(validationaMessages);
			containerId = "ThePage:ptioFacility:";
		}

		if (this.application instanceof TVApplication) {
			validateTVAppRequiredFields(validationaMessages);
			containerId = "ThePage:tvFacility:";
		}

		if (validationaMessages.size() > 0) {
			displayValidationMessages(containerId,
					validationaMessages.toArray(new ValidationMessage[0]));
			isValid = false;
		}

		return isValid;
	}
	
	private boolean validateApplicationSubparts() {
		boolean isValid = true;
		String containerId = "";
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
		
		if (this.application instanceof TVApplication) {
			TVApplication tvApp = (TVApplication) this.application;
			setTvSubpartCDs(tvApp);
			checkTVSubparts(tvApp, validationMessages);
			containerId = "ThePage:tvFacility:";
		}
		
		if (validationMessages.size() > 0) {
			displayValidationMessages(containerId,
					validationMessages.toArray(new ValidationMessage[0]));
			isValid = false;
		}
		
		
		return isValid;
	}
	
	private boolean checkTVSubparts(TVApplication tvApp, List<ValidationMessage> validationMessages) {
		boolean isValid = true;
    	List<String> nspsSubparts = tvApp.getNspsSubpartCodes();
    	List<String> neshapPart61Subparts = tvApp.getNeshapSubpartCodes();
    	List<String> neshapPart63Subparts = tvApp.getMactSubpartCodes();
    	
    	boolean nspsHasDuplicate = false;
    	boolean neshapPart61HasDuplicate = false;
    	boolean neshapPart63HasDuplicate = false;
    	
    	if(nspsSubparts != null) {
    		nspsHasDuplicate = Utility.hasDuplicate(nspsSubparts);
    		if(nspsHasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"nspsApplicableFlag",
						"You cannot add duplicate NSPS subparts.",
						ValidationMessage.Severity.ERROR, null));
    		}
    	}
    	
    	if(neshapPart61Subparts != null) {
    		neshapPart61HasDuplicate = Utility.hasDuplicate(neshapPart61Subparts);
    		if(neshapPart61HasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"neshapApplicableFlag",
						"You cannot add duplicate Part 61 NESHAP subparts.",
						ValidationMessage.Severity.ERROR, null));
    		}
    	}
    	
    	if(neshapPart63Subparts != null) {
    		neshapPart63HasDuplicate = Utility.hasDuplicate(neshapPart63Subparts);
    		if(neshapPart63HasDuplicate) {
				validationMessages
				.add(new ValidationMessage(
						"mactApplicableFlag",
						"You cannot add duplicate Part 63 NESHAP subparts.",
						ValidationMessage.Severity.ERROR, null));
    		}
    	}
    	
		if (validationMessages.size() > 0) {
			isValid = false;
		}
		
		return isValid;
	}
	
	private void validateNSRAppRequiredFields(
			List<ValidationMessage> validationaMessages) {
		PTIOApplication ptioApp = (PTIOApplication) this.application;
		if (!ptioApp.isKnownIncompleteNSRApp()) {
			ptioApp.requiredFields();
		}
		validationaMessages.addAll(Utility.createArrayList(ptioApp.validate()));

		ValidationMessage validResult = checkChangedLocationRequiredRule(ptioApp);
		if (validResult != null)
			validationaMessages.add(validResult);

		validResult = checkContainH2SRequiredRule(ptioApp);
		if (validResult != null)
			validationaMessages.add(validResult);

		validResult = checkAQRVAnalysisRequiredRule(ptioApp);
		if (validResult != null)
			validationaMessages.add(validResult);

		validResult = checkModelingProtocolSubmitRequiredRule(ptioApp);
		if (validResult != null)
			validationaMessages.add(validResult);

		validResult = checkPreventionPsdRequiredRule(ptioApp);
		if (validResult != null)
			validationaMessages.add(validResult);
	}

	private void validateTVAppRequiredFields(
			List<ValidationMessage> validationaMessages) {
		TVApplication tvApp = (TVApplication) this.application;

		if (isPermitReasonSelectable()) {
			tvApp.removeRevisionRequiredFields();
		}
		
		validationaMessages.addAll(Utility.createArrayList(tvApp.validate()));
		validationaMessages.addAll(checkTVFacWideReqRequiredRule(tvApp));

		ValidationMessage validResult = checkTVReasonRequiredRule(tvApp);

		if (validResult != null)
			validationaMessages.add(validResult);
		
		validResult = checkPlanSubmittedUnder112R(tvApp);
		if (validResult != null) {
			validationaMessages.add(validResult);
		}

		validResult = checkRiskManagementPlanSubmitDate(tvApp);
		if (validResult != null) {
			validationaMessages.add(validResult);
		}

		validResult = checkComplianceRequirementsNotMet(tvApp);
		if (validResult != null) {
			validationaMessages.add(validResult);
		}

		validResult = checkNsrPermitNumber(tvApp);
		if (validResult != null) {
			validationaMessages.add(validResult);
		}
	}

	private ValidationMessage checkChangedLocationRequiredRule(
			PTIOApplication ptioApp) {
		String changedLocationFlag = ptioApp.getFacilityChangedLocationFlag();
		String landUsePlanning = ptioApp.getLandUsePlanningFlag();

		if (!Utility.isNullOrEmpty(changedLocationFlag)
				&& changedLocationFlag.equalsIgnoreCase("Y")
				&& Utility.isNullOrEmpty(landUsePlanning)) {

			return new ValidationMessage(
					"landUsePlanningFlag",
					"Has a Land Use Planning document been included in this application is not set.",
					ValidationMessage.Severity.ERROR, "landUsePlanningFlag");
		}

		return null;
	}

	private ValidationMessage checkContainH2SRequiredRule(
			PTIOApplication ptioApp) {
		String containH2SFlag = ptioApp.getContainH2SFlag();
		String divisionContacedFlag = ptioApp.getDivisionContacedFlag();

		if (!Utility.isNullOrEmpty(containH2SFlag)
				&& containH2SFlag.equalsIgnoreCase("Y")
				&& Utility.isNullOrEmpty(divisionContacedFlag)) {

			return new ValidationMessage(
					"divisionContacedFlag",
					"Has the Division been contacted regarding this application is not set.",
					ValidationMessage.Severity.ERROR, "divisionContacedFlag");
		}

		return null;
	}

	private ValidationMessage checkPreventionPsdRequiredRule(
			PTIOApplication ptioApp) {
		String psdFlag = ptioApp.getPreventionPsdFlag();
		String preAppMettingFalg = ptioApp.getPreAppMeetingFlag();

		if (!Utility.isNullOrEmpty(psdFlag) && psdFlag.equalsIgnoreCase("Y")
				&& Utility.isNullOrEmpty(preAppMettingFalg)) {
			return new ValidationMessage(
					"preAppMeetingFlag",
					"Has the Division been notified to schedule a pre-application meeting is not set.",
					ValidationMessage.Severity.ERROR, "preAppMeetingFlag");
		}

		return null;
	}

	private ValidationMessage checkModelingProtocolSubmitRequiredRule(
			PTIOApplication ptioApp) {
		String psdFlag = ptioApp.getPreventionPsdFlag();
		String preAppMeetingFlag = ptioApp.getPreAppMeetingFlag();
		String protocolSubmitFalg = ptioApp.getModelingProtocolSubmitFlag();

		if (!Utility.isNullOrEmpty(psdFlag) && psdFlag.equalsIgnoreCase("Y")
				&& !Utility.isNullOrEmpty(preAppMeetingFlag)
				&& preAppMeetingFlag.equalsIgnoreCase("Y")
				&& Utility.isNullOrEmpty(protocolSubmitFalg)) {
			return new ValidationMessage(
					"modelingProtocolSubmitFlag",
					"Has a modeling protocol been submitted to and approved by the Division is not set.",
					ValidationMessage.Severity.ERROR,
					"modelingProtocolSubmitFlag");
		}

		return null;
	}

	private void checkCMXMaterialUsed(PTIOApplicationEU ptioAppEU,
			List<ValidationMessage> validationMessages) {
		int i = 0;
		for (ApplicationEUMaterialUsed materialUsed : getMaterialUsed()) {
			if (Utility.isNullOrEmpty(materialUsed.getMaterialAmount())) {

				validationMessages.add(new ValidationMessage(
						"nsrAppEmissionUnitTypeCMX:materialUsedTable:"
								+ new Integer(i).toString() + ":"
								+ "materialAmount", ApplicationBO
								.getPTIOMaterialUsedDefs().get(
										materialUsed.getMaterialUsedCd())
								+ " Amount attribute is not set.",
						ValidationMessage.Severity.ERROR, materialUsed
								.getMaterialUsedCd()));
			}

			if (Utility.isNullOrEmpty(materialUsed.getUnitCd())) {

				validationMessages.add(new ValidationMessage(
						"nsrAppEmissionUnitTypeCMX:materialUsedTable:"
								+ new Integer(i).toString() + ":UnitCd",
						ApplicationBO.getPTIOMaterialUsedDefs().get(
								materialUsed.getMaterialUsedCd())
								+ " Units attribute is not set.",
						ValidationMessage.Severity.ERROR, materialUsed
								.getMaterialUsedCd()));
			}
			i++;
		}
	}

	private void checkNSRCapEmissions(PTIOApplicationEU ptioAppEU,
			List<ValidationMessage> validationMessages) {
		int i = 0;
		for (ApplicationEUEmissions emissions : ptioAppEU.getCapEmissions()) {
			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmit())) {
				if (Float.parseFloat(emissions.getPotentialToEmit()) > 0) {
					if (Utility.isNullOrEmpty(emissions.getUnitCd())) {

						validationMessages.add(new ValidationMessage(
								"capTable:" + new Integer(i).toString()
										+ ":unitsCd",
								"Units attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
					}
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"capTable:" + new Integer(i).toString()
										+ ":potentialToEmit",
								"Potential to Emit (PTE) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}

			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmitLbHr())) {
				if ((Float.parseFloat(emissions.getPotentialToEmitLbHr()) > 0)
						&& (Utility.isNullOrEmpty(emissions
								.getPteDeterminationBasisCd()))) {
					validationMessages.add(new ValidationMessage("capTable:"
							+ new Integer(i).toString()
							+ ":AppBasisForDeterminationDropdown",
							"Basis for Determination attribute is not set.",
							ValidationMessage.Severity.ERROR, emissions
									.getPteDeterminationBasisCd()));
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"capTable:" + new Integer(i).toString()
										+ ":potentialToEmitLbHr",
								"Potential to Emit (PTE) (lbs/hr) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}

			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmitTonYr())) {
				if ((Float.parseFloat(emissions.getPotentialToEmitTonYr()) > 0)
						&& (Utility.isNullOrEmpty(emissions
								.getPteDeterminationBasisCd()))) {
					validationMessages.add(new ValidationMessage("capTable:"
							+ new Integer(i).toString()
							+ ":AppBasisForDeterminationDropdown",
							"Basis for Determination attribute is not set.",
							ValidationMessage.Severity.ERROR, emissions
									.getPteDeterminationBasisCd()));
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"capTable:" + new Integer(i).toString()
										+ ":potentialToEmitTonYr",
								"Potential to Emit (PTE) (tons/yr) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}
			i++;
		}
	}

	private void checkNSRHapEmissions(PTIOApplicationEU ptioAppEU,
			List<ValidationMessage> validationMessages) {
		int i = 0;
		for (ApplicationEUEmissions emissions : ptioAppEU.getHapTacEmissions()) {
			if (Utility.isNullOrEmpty(emissions.getPreCtlPotentialEmissions())){
				validationMessages
				.add(new ValidationMessage(
						"hapsTable:" + new Integer(i).toString()
								+ ":preCtlPotentialEmissions",
						"Pre-Controlled Potential Emissions (tons/yr) attribute is not set.",
						ValidationMessage.Severity.ERROR, emissions
								.getUnitCd()));
			}
			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmit())) {
				if (Float.parseFloat(emissions.getPotentialToEmit()) > 0) {
					if (Utility.isNullOrEmpty(emissions.getUnitCd())) {
						validationMessages.add(new ValidationMessage(
								"hapsTable:" + new Integer(i).toString()
										+ ":unitsCd",
								"Units attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
					}
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"hapsTable:" + new Integer(i).toString()
										+ ":potentialToEmit",
								"Potential to Emit (PTE) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}

			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmitLbHr())) {
				if ((Float.parseFloat(emissions.getPotentialToEmitLbHr()) > 0)
						&& (Utility.isNullOrEmpty(emissions
								.getPteDeterminationBasisCd()))) {
					validationMessages.add(new ValidationMessage("hapsTable:"
							+ new Integer(i).toString()
							+ ":AppBasisForDeterminationDropdown",
							"Basis for Determination attribute is not set.",
							ValidationMessage.Severity.ERROR, emissions
									.getPteDeterminationBasisCd()));
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"hapsTable:" + new Integer(i).toString()
										+ ":potentialToEmitLbHr",
								"Potential to Emit (PTE) (lbs/hr) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}

			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmitTonYr())) {
				if ((Float.parseFloat(emissions.getPotentialToEmitTonYr()) > 0)
						&& (Utility.isNullOrEmpty(emissions
								.getPteDeterminationBasisCd()))) {
					validationMessages.add(new ValidationMessage("hapsTable:"
							+ new Integer(i).toString()
							+ ":AppBasisForDeterminationDropdown",
							"Basis for Determination attribute is not set.",
							ValidationMessage.Severity.ERROR, emissions
									.getPteDeterminationBasisCd()));
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"hapsTable:" + new Integer(i).toString()
										+ ":potentialToEmitTonYr",
								"Potential to Emit (PTE) (tons/yr) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}
			i++;
		}
		
	}
	
	private void checkNSRGhgEmissions(PTIOApplicationEU ptioAppEU,
			List<ValidationMessage> validationMessages) {
		int i = 0;
		for (ApplicationEUEmissions emissions : ptioAppEU.getGhgEmissions()) {
			if (Utility.isNullOrEmpty(emissions.getPreCtlPotentialEmissions())){
				validationMessages
				.add(new ValidationMessage(
						"ghgTable:" + new Integer(i).toString()
								+ ":preCtlPotentialEmissions",
						"Pre-Controlled Potential Emissions (tons/yr) attribute is not set.",
						ValidationMessage.Severity.ERROR, emissions
								.getUnitCd()));
			}
			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmit())) {
				if (Float.parseFloat(emissions.getPotentialToEmit()) > 0) {
					if (Utility.isNullOrEmpty(emissions.getUnitCd())) {
						validationMessages.add(new ValidationMessage(
								"ghgTable:" + new Integer(i).toString()
										+ ":unitsCd",
								"Units attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
					}
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"ghgTable:" + new Integer(i).toString()
										+ ":potentialToEmit",
								"Potential to Emit (PTE) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}

			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmitLbHr())) {
				if ((Float.parseFloat(emissions.getPotentialToEmitLbHr()) > 0)
						&& (Utility.isNullOrEmpty(emissions
								.getPteDeterminationBasisCd()))) {
					validationMessages.add(new ValidationMessage("ghgTable:"
							+ new Integer(i).toString()
							+ ":AppBasisForDeterminationDropdown",
							"Basis for Determination attribute is not set.",
							ValidationMessage.Severity.ERROR, emissions
									.getPteDeterminationBasisCd()));
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"ghgTable:" + new Integer(i).toString()
										+ ":potentialToEmitLbHr",
								"Potential to Emit (PTE) (lbs/hr) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}

			if (!Utility.isNullOrEmpty(emissions.getPotentialToEmitTonYr())) {
				if ((Float.parseFloat(emissions.getPotentialToEmitTonYr()) > 0)
						&& (Utility.isNullOrEmpty(emissions
								.getPteDeterminationBasisCd()))) {
					validationMessages.add(new ValidationMessage("ghgTable:"
							+ new Integer(i).toString()
							+ ":AppBasisForDeterminationDropdown",
							"Basis for Determination attribute is not set.",
							ValidationMessage.Severity.ERROR, emissions
									.getPteDeterminationBasisCd()));
				}
			} else {
				validationMessages
						.add(new ValidationMessage(
								"ghgTable:" + new Integer(i).toString()
										+ ":potentialToEmitTonYr",
								"Potential to Emit (PTE) (tons/yr) attribute is not set.",
								ValidationMessage.Severity.ERROR, emissions
										.getUnitCd()));
			}
			i++;
		}
		
	}
	private ValidationMessage checkAQRVAnalysisRequiredRule(
			PTIOApplication ptioApp) {
		String psdFlag = ptioApp.getPreventionPsdFlag();
		String preAppMeetingFlag = ptioApp.getPreAppMeetingFlag();
		String aqrvFalg = ptioApp.getAqrvAnalysisSubmitFlag();

		if (!Utility.isNullOrEmpty(psdFlag) && psdFlag.equalsIgnoreCase("Y")
				&& !Utility.isNullOrEmpty(preAppMeetingFlag)
				&& preAppMeetingFlag.equalsIgnoreCase("Y")
				&& Utility.isNullOrEmpty(aqrvFalg)) {
			return new ValidationMessage(
					"aqrvAnalysisSubmitFlag",
					"Has the Division received a Q/D analysis to submit to the respective "
							+ "FLMs to determine the need for an AQRV analysis is not set.",
					ValidationMessage.Severity.ERROR, "aqrvAnalysisSubmitFlag");
		}

		return null;
	}

	private ValidationMessage checkTVReasonRequiredRule(TVApplication tvApp) {
		String purposeCd = tvApp.getTvApplicationPurposeCd();
		String reasonCd = tvApp.getPermitReasonCd();

		if (!Utility.isNullOrEmpty(purposeCd)
				&& purposeCd
						.equals(TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING)
				&& Utility.isNullOrEmpty(reasonCd)) {

			return new ValidationMessage("permitReasonCd",
					"The setting of the application reason is incompleted.",
					ValidationMessage.Severity.ERROR, "permitReasonCd");
		}

		return null;
	}

	private List<ValidationMessage> checkTVFacWideReqRequiredRule(
			TVApplication tvApp) {
		List<ValidationMessage> validResult = new ArrayList<ValidationMessage>();

		for (FacilityWideRequirement facWideReq : tvApp
				.getFacilityWideRequirements()) {
			ValidationMessage[] tempValidedResult = facWideReq.validate();

			if (tempValidedResult != null && tempValidedResult.length > 0) {
				validResult.addAll(Utility.createArrayList(tempValidedResult));

				break;
			}
		}

		return validResult;
	}

	public boolean isNewContact() {
		return newContact;
	}

	public void setNewContact(boolean newContact) {
		this.newContact = newContact;
	}

	public boolean isCreateNewContact() {
		return createNewContact;
	}

	public void setCreateNewContact(boolean createNewContact) {
		this.createNewContact = createNewContact;
	}

	public Contact getCreateApplicationContact() {
		return createApplicationContact;
	}

	public void setCreateApplicationContact(Contact createApplicationContact) {
		this.createApplicationContact = createApplicationContact;
	}

	public boolean getDisplayTvEuNSPSSubparts() {
		boolean showSubparts = false;
		if (selectedEU instanceof TVApplicationEU) {
			String flag = ((TVApplicationEU) selectedEU)
					.getNspsApplicableFlag();
			showSubparts = flag != null
					&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		}
		return showSubparts;
	}
	
	public boolean getDisplayTvEuNESHAPSubparts() {
		boolean showSubparts = false;
		if (selectedEU instanceof TVApplicationEU) {
			String flag = ((TVApplicationEU) selectedEU)
					.getNeshapApplicableFlag();
			showSubparts = flag != null
					&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		}
		return showSubparts;
	}

	public boolean getDisplayTvEuMACTSubparts() {
		boolean showSubparts = false;
		if (selectedEU instanceof TVApplicationEU) {
			String flag = ((TVApplicationEU) selectedEU)
					.getMactApplicableFlag();
			showSubparts = flag != null
					&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
							.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		}
		return showSubparts;
	}

	public boolean getDisplayTvNSPSSubparts() {
		boolean showSubparts = false;
		if (!(application instanceof TVApplication))
			return showSubparts;

		String flag = ((TVApplication) application).getNspsApplicableFlag();
		showSubparts = !Utility.isNullOrEmpty(flag)
				&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		return showSubparts;
	}

	public boolean getDisplayTvNESHAPSubparts() {
		boolean showSubparts = false;
		if (!(application instanceof TVApplication))
			return showSubparts;

		String flag = ((TVApplication) application).getNeshapApplicableFlag();
		showSubparts = !Utility.isNullOrEmpty(flag)
				&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		return showSubparts;
	}

	public boolean getDisplayTvMACTSubparts() {
		boolean showSubparts = false;
		if (!(application instanceof TVApplication))
			return showSubparts;

		String flag = ((TVApplication) application).getMactApplicableFlag();
		showSubparts = !Utility.isNullOrEmpty(flag)
				&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));

		return showSubparts;
	}

	public boolean getTvEuFedRulesExemption() {
		boolean exempt = false;
		if (selectedEU instanceof TVApplicationEU) {
			TVApplicationEU app = (TVApplicationEU) selectedEU;
			exempt = app.getFedRulesExemption();
		}
		return exempt;
	}

	public List<Stars2Object> getTvEuMactSubparts() {
		if (tvEuMactSubparts == null) {
			tvEuMactSubparts = new ArrayList<Stars2Object>();
		}

		return this.tvEuMactSubparts;
	}

	public void setTvEuMactSubparts(List<Stars2Object> tvEuMactSubparts) {
		this.tvEuMactSubparts = tvEuMactSubparts;
	}

	public List<Stars2Object> getTvEuNeshapSubparts() {
		if (tvEuNeshapSubparts == null) {
			tvEuNeshapSubparts = new ArrayList<Stars2Object>();
		}

		return tvEuNeshapSubparts;
	}

	public void setTvEuNeshapSubparts(List<Stars2Object> tvEuNeshapSubparts) {
		this.tvEuNeshapSubparts = tvEuNeshapSubparts;
	}

	public List<Stars2Object> getTvEuNspsSubparts() {
		if (tvEuNspsSubparts == null) {
			tvEuNspsSubparts = new ArrayList<Stars2Object>();
		}

		return tvEuNspsSubparts;
	}

	public void setTvEuNspsSubparts(List<Stars2Object> tvEuNspsSubparts) {
		this.tvEuNspsSubparts = tvEuNspsSubparts;
	}

	public List<Stars2Object> getTvNspsSubparts() {
		return tvNspsSubparts;
	}

	public void setTvNspsSubparts(List<Stars2Object> tvNspsSubparts) {
		this.tvNspsSubparts = tvNspsSubparts;
	}

	public List<Stars2Object> getTvNeshapSubparts() {
		return tvNeshapSubparts;
	}

	public void setTvNeshapSubparts(List<Stars2Object> tvNeshapSubparts) {
		this.tvNeshapSubparts = tvNeshapSubparts;
	}

	public List<Stars2Object> getTvMactSubparts() {
		return tvMactSubparts;
	}

	public void setTvMactSubparts(List<Stars2Object> tvMactSubparts) {
		this.tvMactSubparts = tvMactSubparts;
	}

	public void pollutantValueChanged(ValueChangeEvent valueChangeEvent) {
		if (valueChangeEvent != null) {
			pollutantLimit.setPollutantCd((String) valueChangeEvent
					.getNewValue());
		}
	}

	public String startViewTVPollutantLimit() {
		pollutantLimit = (TVEUPollutantLimit) FacesUtil
				.getManagedBean(tvPollutantLimitsId);
		newPollutantLimit = false;
		pollutantLimitModify = false;
		return POLLUTANT_LIMIT_DIALOG;
	}

	public String startAddTVPollutantLimit() {
		pollutantLimitModify = true;
		newPollutantLimit = true;
		pollutantLimit = new TVEUPollutantLimit();

		return POLLUTANT_LIMIT_DIALOG;
	}

	public String editTVPollutantLimit(ActionEvent actionEvent) {
		pollutantLimitModify = true;
		return null;
	}

	public String cancelEditTVPollutantLimit() {
		pollutantLimitModify = false;

		if (newPollutantLimit) {
			newPollutantLimit = false;
		}

		refreshAttachments();
		if (!editMode) {
			reloadWithSelectedEU();
		}

		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public String applyEditPollutantLimit(ActionEvent actionEvent) {
		boolean operationOk = true;

		if (!(selectedEU instanceof TVApplicationEU)) {
			DisplayUtil.displayError("Selected emission unit is invalid");
			operationOk = false;
			return null;
		}

		TVApplicationEU tvAppEu = (TVApplicationEU) selectedEU;

		operationOk = validatePollutantLimits(null, pollutantLimit);

		if (operationOk) {
			if (newPollutantLimit) {
				tvAppEu.getPollutantLimits().add(pollutantLimit);
			}

			try {
				operationOk = getApplicationService().modifyPollutantLimits(
						tvAppEu);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Could not update this application's pollutant limits");
				logger.error("Could not update application EU id: "
						+ tvAppEu.getApplicationEuId());
			} catch (RemoteException e) {
				DisplayUtil
						.displayError("Could not update this application's pollutant limits");
				logger.error("Could not update application EU id: "
						+ tvAppEu.getApplicationEuId());
			}

			newPollutantLimit = false;
			pollutantLimitModify = false;
			pollutantLimit = null;
		}

		if (operationOk) {
			refreshAttachments();
			if (!editMode) {
				reloadWithSelectedEU();
			}

			DisplayUtil.displayInfo("Pollutant limits successfully modified!");
			FacesUtil.returnFromDialogAndRefresh();
		}

		return null;
	}

	public String removeTVPollutantLimit(ActionEvent actionEvent) {
		boolean operationOk = true;

		if (!(selectedEU instanceof TVApplicationEU)) {
			DisplayUtil.displayError("Selected emission unit is invalid");
			operationOk = false;
			return null;
		}

		TVApplicationEU tvAppEu = (TVApplicationEU) selectedEU;

		if (operationOk) {
			tvAppEu.getPollutantLimits().remove(pollutantLimit);

			try {
				operationOk = getApplicationService().modifyPollutantLimits(
						tvAppEu);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Could not update this application's pollutant limits");
				logger.error("Could not update application EU id: "
						+ tvAppEu.getApplicationEuId());
			} catch (RemoteException e) {
				DisplayUtil
						.displayError("Could not update this application's pollutant limits");
				logger.error("Could not update application EU id: "
						+ tvAppEu.getApplicationEuId());
			}

			//boolean controlled = getApplicationService().isPollutantControlled(
			//		selectedEU, pollutantLimit.getPollutantCd());

			newPollutantLimit = false;
			pollutantLimitModify = false;
			pollutantLimit = null;
		}

		if (operationOk) {
			refreshAttachments();
			if (!editMode) {
				reloadWithSelectedEU();
			}

			DisplayUtil
					.displayInfo("Pollutant limit has been succesfully removed!");
			FacesUtil.returnFromDialogAndRefresh();
		}

		return null;
	}

	/**
	 * Validates the following set of pollutant limits: {Existing Application EU
	 * Pollutant Limits + New Pollutant Limit}.
	 * 
	 * @param tvAppEu
	 *            An application EU, can be null if only needing to validate
	 *            individual new pollutant limit.
	 * @param newPollutantLimit
	 *            New pollutant limit being validated
	 * @return
	 */
	private final boolean validatePollutantLimits(TVApplicationEU tvAppEu,
			TVEUPollutantLimit newPollutantLimit) {
		boolean isValid = true;
		ValidationMessage[] valMessages = null;

		if (isValid) {
			// try {
			List<TVEUPollutantLimit> pollutantLimits = new ArrayList<TVEUPollutantLimit>();
			if (tvAppEu != null) {
				pollutantLimits.addAll(tvAppEu.getPollutantLimits());
			}
			pollutantLimits.add(newPollutantLimit);
			valMessages = getApplicationService().validatePollutantLimits(
					pollutantLimits);
			isValid = !displayValidationMessages("", valMessages);
			// } catch (RemoteException e) {
			// isValid = false;
			// DisplayUtil.displayError("There was an unexpected error.");
			// logger.error("Could not validate application EU: "
			// + selectedEU.getApplicationEuId());
			// }
		}

		return isValid;
	}

	public String getPollutantStatus() {
		String ret = null;
		if (pollutantLimit == null) {
			return ret;
		}
		try {
			if (pollutantLimit.getPollutantCd() == null) {
				return null;
			}

			boolean controlled = getApplicationService().isPollutantControlled(
					selectedEU, pollutantLimit.getPollutantCd());
			
			pollutantLimit.setControlled(controlled);
			ret = pollutantLimit.getPollutantStatus();
			
		} catch (RemoteException e) {
			handleException(
					"Exception for application "
							+ application.getApplicationNumber(), e);
		}
		return ret;
	}

	public void closeTVPollutantLimit(ActionEvent actionEvent) {
		pollutantLimitModify = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public TVEUPollutantLimit getPollutantLimit() {
		return pollutantLimit;
	}

	public void setPollutantLimit(TVEUPollutantLimit pollutantLimit) {
		this.pollutantLimit = pollutantLimit;
	}

	public String getTvPollutantLimitsId() {
		return tvPollutantLimitsId;
	}

	public void setTvPollutantLimitsId(String tvPollutantLimitsId) {
		this.tvPollutantLimitsId = tvPollutantLimitsId;
	}

	public boolean isPollutantLimitModify() {
		return pollutantLimitModify;
	}

	public void setPollutantLimitModify(boolean pollutantLimitModify) {
		this.pollutantLimitModify = pollutantLimitModify;
	}

	public boolean isNewPollutantLimit() {
		return this.newPollutantLimit;
	}

	public String startToAddFacWideReq() {
		this.facWideReq = new FacilityWideRequirement();
		this.editModeFacWideReq = true;

		return FAC_WIDE_REQ_DIALOG;
	}

	public String startToEditFacWideReq() {
		FacilityWideRequirement old = (FacilityWideRequirement) FacesUtil
				.getManagedBean(FAC_WIDE_REQ_MANAGED_BEAN);
		this.facWideReq = new FacilityWideRequirement(old);

		return FAC_WIDE_REQ_DIALOG;
	}

	public void changeEditModeFacWideReq() {
		this.editModeFacWideReq = true;
	}

	public void cancelEditModeFacWideReq(ActionEvent actionEvent) {
		if (!(application instanceof TVApplication))
			return;

		this.editModeFacWideReq = false;

		boolean isNew = facWideReq == null
				|| facWideReq.getRequirementId() == null
				|| facWideReq.getRequirementId() < 1;
		if (isNew) {
			FacesUtil.returnFromDialogAndRefresh();
			return;
		}

		Integer reqId = this.facWideReq.getRequirementId();
		for (FacilityWideRequirement item : ((TVApplication) application)
				.getFacilityWideRequirements()) {
			if (reqId == item.getRequirementId()) {
				this.facWideReq = new FacilityWideRequirement(item);
				break;
			}
		}
	}

	private void resetFacWideReqEditStatus() {
		this.facWideReq = null;
		this.editModeFacWideReq = false;
	}

	public void closeFacWideReqDialog(ActionEvent actionEvent) {
		resetFacWideReqEditStatus();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void saveFacilityWideRequirement() {
		if (!(application instanceof TVApplication))
			return;

		ValidationMessage[] validationMessages = facWideReq.validate();
		if (validationMessages != null && validationMessages.length > 0) {
			displayValidationMessages("", validationMessages);
			return;
		}

		TVApplication tvApp = ((TVApplication) application);
		Integer appId = tvApp.getApplicationID();
		boolean isNew = this.facWideReq.getRequirementId() == null
				|| facWideReq.getRequirementId() < 1;

		try {
			ApplicationService appBO = getApplicationService();

			if (isNew) {
				this.facWideReq.setApplicationId(appId);
				appBO.createFacilityWideRequirement(facWideReq);
			} else {
				appBO.modifyFacilityWideRequirement(facWideReq);
			}

			tvApp.setFacilityWideRequirements(appBO
					.retrieveFacilityWideRequirements(appId));
			this.facWideReqWrapper.setWrappedData(tvApp
					.getFacilityWideRequirements());

			resetFacWideReqEditStatus();
			FacesUtil.returnFromDialogAndRefresh();
		} catch (RemoteException e) {
			DisplayUtil
					.displayError("A system error has occurred. Please contact System Administrator.");
			logger.error(e.getMessage());
		}
	}

	public void deleteFacilityWideRequirement() {
		if (!(application instanceof TVApplication) || this.facWideReq == null)
			return;

		TVApplication tvApp = ((TVApplication) application);
		Integer appId = tvApp.getApplicationID();

		try {
			ApplicationService appBO = getApplicationService();
			appBO.removeFacilityWideRequirement(facWideReq.getRequirementId());
			tvApp.setFacilityWideRequirements(appBO
					.retrieveFacilityWideRequirements(appId));
			this.facWideReqWrapper.setWrappedData(tvApp
					.getFacilityWideRequirements());

			resetFacWideReqEditStatus();
			FacesUtil.returnFromDialogAndRefresh();
		} catch (RemoteException e) {
			DisplayUtil
					.displayError("A system error has occurred. Please contact System Administrator.");
			logger.error(e.getMessage());
		}
	}

	public TVEUOperationalRestriction getOperationalRestriction() {
		return operationalRestriction;
	}

	public void setOperationalRestriction(
			TVEUOperationalRestriction operationalRestriction) {
		this.operationalRestriction = operationalRestriction;
	}

	public boolean isOperationalRestrictionModify() {
		return operationalRestrictionModify;
	}

	public void setOperationalRestrictionModify(
			boolean operationalRestrictionModify) {
		this.operationalRestrictionModify = operationalRestrictionModify;
	}

	public boolean isNewOperationalRestriction() {
		return this.newOperationalRestriction;
	}

	public String getTvOperationalRestrictionId() {
		return tvOperationalRestrictionId;
	}

	public void setTvOperationalRestrictionId(String tvOperationalRestrictionId) {
		this.tvOperationalRestrictionId = tvOperationalRestrictionId;
	}

	public String startViewTVOperationalRestriction() {
		operationalRestriction = (TVEUOperationalRestriction) FacesUtil
				.getManagedBean(tvOperationalRestrictionId);
		newOperationalRestriction = false;
		operationalRestrictionModify = false;
		return OPERATIONAL_RESTRICTION_DIALOG;
	}

	public String startAddTVOperationalRestriction() {
		operationalRestrictionModify = true;
		newOperationalRestriction = true;
		operationalRestriction = new TVEUOperationalRestriction();

		return OPERATIONAL_RESTRICTION_DIALOG;
	}

	public String editTVOperationalRestriction(ActionEvent actionEvent) {
		operationalRestrictionModify = true;
		return null;
	}

	public String cancelEditTVOperationalRestriction() {
		operationalRestrictionModify = false;

		if (newOperationalRestriction) {
			newOperationalRestriction = false;
		}

		refreshAttachments();
		if (!editMode) {
			reloadWithSelectedEU();
		}

		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public void closeTVOperationalRestriction(ActionEvent actionEvent) {
		operationalRestrictionModify = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public String applyEditOperationalRestriction(ActionEvent actionEvent) {
		boolean operationOk = true;

		if (!(selectedEU instanceof TVApplicationEU)) {
			DisplayUtil.displayError("Selected emission unit is invalid");
			operationOk = false;
			return null;
		}

		TVApplicationEU tvAppEu = (TVApplicationEU) selectedEU;

		operationOk = validateOperationalRestrictions(null,
				operationalRestriction);

		if (operationOk) {
			if (newOperationalRestriction) {
				tvAppEu.getOperationalRestrictions()
						.add(operationalRestriction);
			}

			try {
				operationOk = getApplicationService()
						.modifyOperationalRestrictions(tvAppEu);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Could not update this application's operational restrictions");
				logger.error("Could not update application EU id: "
						+ tvAppEu.getApplicationEuId());
			} catch (RemoteException e) {
				DisplayUtil
						.displayError("Could not update this application's operational restrictions");
				logger.error("Could not update application EU id: "
						+ tvAppEu.getApplicationEuId());
			}

			newOperationalRestriction = false;
			operationalRestrictionModify = false;
			operationalRestriction = null;
		}

		if (operationOk) {
			refreshAttachments();
			if (!editMode) {
				reloadWithSelectedEU();
			}

			DisplayUtil
					.displayInfo("Operational restrictions successfully modified!");
			FacesUtil.returnFromDialogAndRefresh();
		}

		return null;
	}

	public String removeTVOperationalRestriction(ActionEvent actionEvent) {
		boolean operationOk = true;

		if (!(selectedEU instanceof TVApplicationEU)) {
			DisplayUtil.displayError("Selected emission unit is invalid");
			operationOk = false;
			return null;
		}

		TVApplicationEU tvAppEu = (TVApplicationEU) selectedEU;

		if (operationOk) {
			tvAppEu.getOperationalRestrictions().remove(operationalRestriction);

			try {
				operationOk = getApplicationService()
						.modifyOperationalRestrictions(tvAppEu);
			} catch (DAOException e) {
				DisplayUtil
						.displayError("Could not update this application's operational restrictions");
				logger.error("Could not update application EU id: "
						+ tvAppEu.getApplicationEuId());
			} catch (RemoteException e) {
				DisplayUtil
						.displayError("Could not update this application's operational restrictions");
				logger.error("Could not update application EU id: "
						+ tvAppEu.getApplicationEuId());
			}

			newOperationalRestriction = false;
			operationalRestrictionModify = false;
			operationalRestriction = null;
		}

		if (operationOk) {
			refreshAttachments();
			if (!editMode) {
				reloadWithSelectedEU();
			}

			DisplayUtil
					.displayInfo("Operational restriction has been succesfully removed!");
			FacesUtil.returnFromDialogAndRefresh();
		}

		return null;
	}

	/**
	 * Validates the following set of operational restrictions: {Existing
	 * Application EU Operational Restrictions + New Operational Restriction}.
	 * 
	 * @param tvAppEu
	 *            An application EU, can be null if only needing to validate
	 *            individual new operational restriction.
	 * @param newOperationalRestriction
	 *            New operational restriction being validated
	 * @return
	 */
	private final boolean validateOperationalRestrictions(
			TVApplicationEU tvAppEu,
			TVEUOperationalRestriction newOperationalRestriction) {
		boolean isValid = true;
		ValidationMessage[] valMessages = null;

		if (isValid) {
			// try {
			List<TVEUOperationalRestriction> operationalRestrictions = new ArrayList<TVEUOperationalRestriction>();
			if (tvAppEu != null) {
				operationalRestrictions.addAll(tvAppEu
						.getOperationalRestrictions());
			}
			operationalRestrictions.add(newOperationalRestriction);
			valMessages = getApplicationService()
					.validateOperationalRestrictions(operationalRestrictions);
			isValid = !displayValidationMessages("", valMessages);
			// } catch (RemoteException e) {
			// isValid = false;
			// DisplayUtil.displayError("There was an unexpected error.");
			// logger.error("Could not validate application EU: "
			// + selectedEU.getApplicationEuId());
			// }
		}

		return isValid;
	}

	public boolean getEditable1() {
		return editable1;
	}

	public void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}

	public void dialogDone() {
		return;
	}

	public void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	// LAER
	public NSRApplicationLAEREmission getSelectedLAEREmission() {
		return selectedLAEREmission;
	}

	public void setSelectedLAEREmission(
			NSRApplicationLAEREmission selectedLAEREmission) {
		this.selectedLAEREmission = selectedLAEREmission;
	}

	public String startToEditLAEREmission() {
		this.selectedLAEREmission.setNewObject(false);
		setEditable1(false);
		return LAER_POLLUTANT_DIALOG;
	}

	public String startToAddLAEREmission() {

		setEditable1(true);
		this.selectedLAEREmission = new NSRApplicationLAEREmission();
		this.selectedLAEREmission.setApplicationEuId(selectedEU
				.getApplicationEuId());
		return LAER_POLLUTANT_DIALOG;

	}

	public void editLAEREmission() {
		setEditable1(true);
	}

	public void cancelEditLAEREmission() {
		setEditable1(false);
		refreshLAEREmissions();
		refreshLaerPollutantWrapper();
		closeDialog();
		return;
	}

	private void refreshLAEREmissions() {

		if (this.selectedEU != null) {
			try {
				((PTIOApplicationEU) selectedEU)
						.setLaerEmissions(getApplicationService()
								.retrieveNSRApplicationLaerEmissions(
										selectedEU.getApplicationEuId()));
			} catch (RemoteException e) {
				logger.error(e.getMessage());
				DisplayUtil.displayError("Could not retrieve LAER Emissions.");
			}
		}

	}

	private void refreshLaerPollutantWrapper() {
		// setup LAER emissions
		laerPollutantWrapper.setWrappedData(((PTIOApplicationEU) selectedEU)
				.getLaerEmissions());
	}

	public final void saveLAEREmission() {

		if (!validateLAEREmission()) {
			return;
		}

		if (this.selectedLAEREmission.isNewObject()) {
			((PTIOApplicationEU) selectedEU)
					.addLAEREmission(this.selectedLAEREmission);
			this.selectedLAEREmission.setNewObject(false);
		}

		if (validateLAEREmissions()) {
			if (!Utility.isNullOrZero(((PTIOApplicationEU) selectedEU)
					.getApplicationEuId())) {

				try {
					getApplicationService()
							.saveNSRApplicationLaerEmissions(
									((PTIOApplicationEU) selectedEU)
											.getLaerEmissions(),
									selectedEU.getApplicationEuId());
				} catch (RemoteException re) {
					handleException(re);
					DisplayUtil.displayError("Save LAER Emissions failed");
				}
				this.refreshLAEREmissions();
			}
		} else {
			return;
		}

		refreshLaerPollutantWrapper();
		closeDialog();
	}

	private boolean validateLAEREmissions() {
		boolean isValid = true;
		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();
		if (!((PTIOApplicationEU) selectedEU).hasUniqueLAERPollutants()) {
			valMsgs.add(new ValidationMessage("laerAnalysisCompleted",
					"Cannot save duplicate LAER pollutants.",
					ValidationMessage.Severity.ERROR));
		}

		String pageViewId = "emissionUnit:";
		if (displayValidationMessages(pageViewId,
				valMsgs.toArray(new ValidationMessage[0]))) {
			isValid = false;
		}

		return isValid;
	}

	private boolean validateLAEREmission() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		validationMessages = this.selectedLAEREmission.validate();

		String pageViewId = "emissionUnit:";
		if (displayValidationMessages(pageViewId, validationMessages)) {
			isValid = false;
		}

		return isValid;
	}

	public void deleteLAEREmission() {
		((PTIOApplicationEU) selectedEU)
				.removeLAEREmission(this.selectedLAEREmission);
		saveLAEREmission();
	}

	// //BACT
	public NSRApplicationBACTEmission getSelectedBACTEmission() {
		return selectedBACTEmission;
	}

	public void setSelectedBACTEmission(
			NSRApplicationBACTEmission selectedBACTEmission) {
		this.selectedBACTEmission = selectedBACTEmission;
	}

	public String startToEditBACTEmission() {
		this.selectedBACTEmission.setNewObject(false);
		setEditable1(false);
		return BACT_POLLUTANT_DIALOG;
	}

	public String startToAddBACTEmission() {

		setEditable1(true);
		this.selectedBACTEmission = new NSRApplicationBACTEmission();
		this.selectedBACTEmission.setApplicationEuId(selectedEU
				.getApplicationEuId());
		return BACT_POLLUTANT_DIALOG;

	}

	public void editBACTEmission() {
		setEditable1(true);
	}

	public void cancelEditBACTEmission() {
		setEditable1(false);
		refreshBACTEmissions();
		refreshBactPollutantWrapper();
		closeDialog();
		return;
	}

	private void refreshBACTEmissions() {

		if (this.selectedEU != null) {
			try {
				((PTIOApplicationEU) selectedEU)
						.setBactEmissions(getApplicationService()
								.retrieveNSRApplicationBactEmissions(
										selectedEU.getApplicationEuId()));
			} catch (RemoteException e) {
				logger.error(e.getMessage());
				DisplayUtil.displayError("Could not retrieve BACT Emissions.");
			}
		}

	}

	private void refreshBactPollutantWrapper() {
		// setup BACT emissions
		bactPollutantWrapper.setWrappedData(((PTIOApplicationEU) selectedEU)
				.getBactEmissions());
	}

	public final void saveBACTEmission() {

		if (!validateBACTEmission()) {
			return;
		}

		if (this.selectedBACTEmission.isNewObject()) {
			((PTIOApplicationEU) selectedEU)
					.addBACTEmission(this.selectedBACTEmission);
			this.selectedBACTEmission.setNewObject(false);
		}

		if (validateBACTEmissions()) {
			if (((PTIOApplicationEU) selectedEU).hasUniqueBACTPollutants()) {

				if (!Utility.isNullOrZero(((PTIOApplicationEU) selectedEU)
						.getApplicationEuId())) {

					try {
						getApplicationService()
								.saveNSRApplicationBactEmissions(
										((PTIOApplicationEU) selectedEU)
												.getBactEmissions(),
										selectedEU.getApplicationEuId());
					} catch (RemoteException re) {
						handleException(re);
						DisplayUtil.displayError("Save BACT Emissions failed");
					}
					this.refreshBACTEmissions();
				}
			}
		} else {
			return;
		}

		refreshBactPollutantWrapper();
		closeDialog();
	}

	private boolean validateBACTEmissions() {
		boolean isValid = true;
		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();
		if (!((PTIOApplicationEU) selectedEU).hasUniqueBACTPollutants()) {
			valMsgs.add(new ValidationMessage("bactAnalysisCompleted",
					"Cannot save duplicate BACT pollutants.",
					ValidationMessage.Severity.ERROR));
		}

		String pageViewId = "emissionUnit:";
		if (displayValidationMessages(pageViewId,
				valMsgs.toArray(new ValidationMessage[0]))) {
			isValid = false;
		}

		return isValid;
	}

	private boolean validateBACTEmission() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		validationMessages = this.selectedBACTEmission.validate();

		String pageViewId = "emissionUnit:";
		if (displayValidationMessages(pageViewId, validationMessages)) {
			isValid = false;
		}

		return isValid;
	}

	public void deleteBACTEmission() {
		((PTIOApplicationEU) selectedEU)
				.removeBACTEmission(this.selectedBACTEmission);
		saveBACTEmission();
	}

	public String displayAppDocTypeExp() {
		return APP_DOC_TYPE_EXP_DIALOG;
	}

	public String displayTvAppDocTypeExp() {
		return TV_APP_DOC_TYPE_EXP_DIALOG;
	}

	public ApplicationEUFugitiveLeaks getApplicationEUFugitiveLeaks() {
		return applicationEUFugitiveLeaks;
	}

	public void setApplicationEUFugitiveLeaks(
			ApplicationEUFugitiveLeaks applicationEUFugitiveLeaks) {
		this.applicationEUFugitiveLeaks = applicationEUFugitiveLeaks;
	}

	public final String startToAddApplicationEUFugitiveLeaks() {

		setEditable1(true);
		applicationEUFugitiveLeaks = new ApplicationEUFugitiveLeaks();
		applicationEUFugitiveLeaks.setApplicationEuId(selectedEU
				.getApplicationEuId());
		return "dialog:appEmissionsFugLeaksOilGasInfo";
	}

	public final String startToEditApplicationEUFugitiveLeaks() {
		this.applicationEUFugitiveLeaks.setNewObject(false);
		setEditable1(false);
		return "dialog:appEmissionsFugLeaksOilGasInfo";
	}

	public final void editApplicationEUFugitiveLeaks() {
		setEditable1(true);
	}

	public final void cancelEditApplicationEUFugitiveLeaks() {
		setEditable1(false);
		refreshApplicationEUFugitiveLeaks();
		this.closeDialog();
		return;
	}

	public final void saveApplicationEUFugitiveLeaks() {

		if (!validateApplicationEUFugitiveLeaks()) {
			return;
		}

		if (this.applicationEUFugitiveLeaks.isNewObject()) {
			((PTIOApplicationEU) selectedEU)
					.addApplicationEUFugitiveLeaks(this.applicationEUFugitiveLeaks);
			this.applicationEUFugitiveLeaks.setNewObject(false);
		}

		// EU is new or not
		if (!Utility.isNullOrZero(selectedEU.getApplicationEuId())) {
			try {
				getApplicationService()
						.saveApplicationEUFugitiveLeaks(
								((PTIOApplicationEU) selectedEU)
										.getApplicationEUFugitiveLeaks(),
								selectedEU.getApplicationEuId());
				this.selectedEU.setValidated(false);
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil
						.displayError("Save Application Fugitive Leaks data failed");
			}
			this.refreshApplicationEUFugitiveLeaks();
		}
		closeDialog();
	}

	private boolean validateApplicationEUFugitiveLeaks() {
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		validationMessages = this.applicationEUFugitiveLeaks.validate();

		String pageViewId = "emissionUnit:";
		if (displayValidationMessages(pageViewId, validationMessages)) {
			isValid = false;
		}

		return isValid;
	}

	private void refreshApplicationEUFugitiveLeaks() {
		if (this.selectedEU != null) {
			try {
				((PTIOApplicationEU) this.selectedEU)
						.setApplicationEUFugitiveLeaks(getApplicationService()
								.retrieveApplicationEUFugitiveLeaks(
										selectedEU.getApplicationEuId()));

			} catch (RemoteException e) {
				logger.error(e.getMessage());
				DisplayUtil.displayError("Could not retrieve fugitive Leaks.");
			}
		}
	}

	private void setSelectedEU(ApplicationEU selectedEU) {
		logger.debug("---> setSelectedEU() -> " + selectedEU);
		// try {
		// throw new RuntimeException("setSelectedEU() trace");
		// } catch (RuntimeException e) {
		// e.printStackTrace();
		// }
		this.selectedEU = selectedEU;
	}

	public void deleteApplicationEUFugitiveLeaks() {
		((PTIOApplicationEU) this.selectedEU)
				.removeApplicationEUFugitiveLeaks(this.applicationEUFugitiveLeaks);
		saveApplicationEUFugitiveLeaks();
	}

	public final void closeEditDialog() throws RemoteException {
		setEditable1(false);
		closeDialog();
	}

	public DefSelectItems getAppEUEquipServiceTypeDefs() {
		DefData eUEquipServiceTypeDefs = new DefData();
		eUEquipServiceTypeDefs.setRefresh(true);
		eUEquipServiceTypeDefs = AppEUEquipServiceTypeDef.getData();
		for (SelectItem selectItem : eUEquipServiceTypeDefs.getItems()
				.getAllItems()) {
			for (ApplicationEUFugitiveLeaks fugitiveLeak : ((PTIOApplicationEU) selectedEU)
					.getApplicationEUFugitiveLeaks())
				if (selectItem.getValue().equals(
						fugitiveLeak.getEquipmentServiceTypeCd())
						&& (this.applicationEUFugitiveLeaks == null || !selectItem
								.getValue().equals(
										this.applicationEUFugitiveLeaks
												.getEquipmentServiceTypeCd()))) {
					selectItem.setDisabled(true);
					break;
				}
		}

		return eUEquipServiceTypeDefs.getItems();
	}

	public final List<ApplicationEUMaterialUsed> getMaterialUsed() {
		List<ApplicationEUMaterialUsed> sortedMaterialUsed = null;
		if (selectedEU instanceof PTIOApplicationEU) {
			List<ApplicationEUMaterialUsed> materialUsedList = ((PTIOApplicationEU) selectedEU)
					.getMaterialUsed();
			sortedMaterialUsed = new ArrayList<ApplicationEUMaterialUsed>();
			for (String materialUsedCd : ApplicationBO
					.getPTIOMaterialUsedCodesOrdered()) {
				for (ApplicationEUMaterialUsed materialUsed : materialUsedList) {
					if (materialUsed.getMaterialUsedCd().equals(materialUsedCd)) {
						sortedMaterialUsed.add(materialUsed);
						break;
					}
				}
			}
		} else {
			sortedMaterialUsed = new ArrayList<ApplicationEUMaterialUsed>();
		}
		return sortedMaterialUsed;
	}

	public final List<SelectItem> getMaterialUsedDefs() {
		// create select list of CAP pollutants
		List<SelectItem> materialUsedDefs = new ArrayList<SelectItem>();
		for (String materialUsedCd : ApplicationBO.getPTIOMaterialUsedDefs()
				.keySet()) {
			materialUsedDefs.add(new SelectItem(materialUsedCd, ApplicationBO
					.getPTIOMaterialUsedDefs().get(materialUsedCd)));
		}
		return materialUsedDefs;
	}

	public final List<SelectItem> getPtioMaterialUsedDefs() {
		DefSelectItems materialUsedDefItems = MaterialUsedDef.getData()
				.getItems();
		// Note: TreeMap is used to sort pollutants by label
		TreeMap<String, SelectItem> materialUsedDefs = new TreeMap<String, SelectItem>();
		for (SelectItem item : materialUsedDefItems.getCurrentItems()) {
			MaterialUsedDef materialDef = (MaterialUsedDef) materialUsedDefItems
					.getItem(item.getValue().toString());
			materialUsedDefs.put(materialDef.getCode() + ":" + item.getLabel(),
					item);
		}

		List<SelectItem> result = new ArrayList<SelectItem>(
				materialUsedDefs.values());
		return result;
	}

	public final String getCmxEmissionsMaterialUsedValueFormat() {
		String format = null;
		// try {
		format = getApplicationService()
				.getCmxEmissionsMaterialUsedValueFormat();
		// } catch (RemoteException e) {
		// handleException(
		// "Exception for application "
		// + application.getApplicationNumber(), e);
		// }
		return format;
	}

	public boolean isTradeSecretAllowed() {

		boolean ret = false;

		// Get the Trade Secret Allowed value from the permit applications attachments types definitions list instead of calling database.
		if (applicationDoc != null) {
    		ret = applicationDoc.isTradeSecretAllowed();
    	}

		return ret;
	}

	private ValidationMessage checkPlanSubmittedUnder112R(TVApplication tvApp) {
		if (tvApp.isSubjectTo112RAct()
				&& Utility.isNullOrEmpty(tvApp.getPlanSubmittedUnder112R())) {
			return new ValidationMessage("plan112RSubmittedBox",
					"Attribute Plan Submitted Under 112(r) is not set.",
					ValidationMessage.Severity.ERROR, "planSubmittedUnder112R");
		}

		return null;
	}

	private ValidationMessage checkRiskManagementPlanSubmitDate(
			TVApplication tvApp) {
		Timestamp date = tvApp.getRiskManagementPlanSubmitDate();

		if (tvApp.isPlanSubmittedUnder112RAct()
				&& (date == null || date.toString().isEmpty())) {
			return new ValidationMessage(
					"riskManagementPlanSubmitDate",
					"Attribute Risk management plan submission date is not set.",
					ValidationMessage.Severity.ERROR,
					"riskManagementPlanSubmitDate");
		}

		return null;
	}

	private ValidationMessage checkComplianceRequirementsNotMet(
			TVApplication tvApp) {
		if (!tvApp.isCompWithApplicableEnhancedMonitoring()
				&& Utility.isNullOrEmpty(tvApp
						.getComplianceRequirementsNotMet()) && (isPteSelectable() || isPermitReasonSPMSelectable())) {
			return new ValidationMessage("complianceRequirementsNotMet",
					"Attribute Compliance requirements not met is not set.",
					ValidationMessage.Severity.ERROR,
					"complianceRequirementsNotMet");
		}

		return null;
	}

	private ValidationMessage checkNsrPermitNumber(TVApplication tvApp) {
		if (tvApp.isSubjectToEngConfigRestrictions()
				&& Utility.isNullOrEmpty(tvApp.getNsrPermitNumber())) {

			return new ValidationMessage("nsrPermitNumber",
					"Attribute NSR permit number.",
					ValidationMessage.Severity.ERROR, "nsrPermitNumber");
		}

		return null;
	}

	public void refreshAttachments(ValueChangeEvent ve) {
		if (ve.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
			ve.setPhaseId(PhaseId.INVOKE_APPLICATION);
			ve.queue();
			return;
		}

		refreshAttachments();
	}

	public void refreshAttachments(ReturnEvent re) {
		refreshAttachments();
	}

	private void refreshAttachments() {
		if (isRenderAttachments()) {
			boolean reloadAttachments = false;
			if (application instanceof PTIOApplication) {
				reloadAttachments = true;
				verifyNSRAppAttachments(application);
				if (selectedEU != null
						&& selectedEU instanceof PTIOApplicationEU) {
					PTIOApplicationEU nsrAppEu = (PTIOApplicationEU) selectedEU;
					verifyNSRAppEUAttachments(application, nsrAppEu);
				}
			} else if (application instanceof TVApplication) {
				reloadAttachments = true;
				verifyTVAppAttachments(application);
				if (selectedEU != null && selectedEU instanceof TVApplicationEU) {
					TVApplicationEU tvAppEu = (TVApplicationEU) selectedEU;
					verifyTVAppEUAttachments(application, tvAppEu);
				}

			}

			if (reloadAttachments) {
				List<ApplicationDocumentRef> appDocList = new ArrayList<ApplicationDocumentRef>();
				List<ApplicationDocumentRef> appEuDocList = new ArrayList<ApplicationDocumentRef>();

				try {
					ApplicationDocumentRef[] appDocs;
					appDocs = getApplicationService()
							.retrieveApplicationDocuments(application.getApplicationTypeCD(),
									application.getApplicationID());

					for (ApplicationDocumentRef appDoc : appDocs) {
						appDocList.add(appDoc);
					}

					if (selectedEU != null) {
						ApplicationDocumentRef[] appEuDocs;
						appEuDocs = getApplicationService()
								.retrieveApplicationEUDocuments(application.getApplicationTypeCD(),
										selectedEU.getApplicationEuId());

						for (ApplicationDocumentRef appEuDoc : appEuDocs) {
							appEuDocList.add(appEuDoc);
						}
					}
				} catch (DAOException e) {
					DisplayUtil
							.displayError("Unable to retrieve application attachments.");
					logger.error("Couldn't retrieve attachments from database: "
							+ e.toString());
				} catch (RemoteException e) {
					DisplayUtil
							.displayError("Unable to retrieve application attachments.");
					logger.error("Couldn't retrieve attachments: "
							+ e.toString());
				}

				application.setDocuments(appDocList);
				if (selectedEU != null) {
					selectedEU.setEuDocuments(appEuDocList);
					setApplicationDocuments(appEuDocList);
				} else {
					setApplicationDocuments(appDocList);
				}

			}
		}

		FacesContext context = FacesContext.getCurrentInstance();
		UIComponent attachmentsTable = context.getViewRoot().findComponent(
				"ThePage:app_attachments:attachmentList");
		if (attachmentsTable != null) {
			AdfFacesContext.getCurrentInstance().addPartialTarget(
					attachmentsTable);
		}
	}

	public void deleteEmissions() {
		deleteActionTableRows();
		refreshAttachments();
	}
	
	public final boolean isDisableNoteButton() {
		if (isReadOnlyUser()) {
			return true;
		} else if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		} else if (!applicationNote.getUserId().equals(getUserID())) {
			return true;
		}
		return false;
	}
	
	public boolean isImpactFullEnabled() {
		boolean ret = true;
		if (isPortalApp()) {
			MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			ret = myTasks.isImpactFullEnabled();
		}
		return ret;
	}
	
	public String getLogFilePath() {
		return logFilePath;
	}
	
	// *********** Public Method Region ***********
		public final void startMigrateApplicationData() {
			
			
			if (fileToUpload == null) {
				DisplayUtil.displayError("Please add a migartion zip file.");
				return;
			}

			if (!DocumentUtil.getFileExtension(fileToUpload.getFilename())
					.equalsIgnoreCase("zip")) {
				DisplayUtil
						.displayError("The migration file type is incorrect. It must be a zip file.");
				return;
			}

			/*try {
				Utility.copyApplication(fileToUpload.getInputStream(), migrationTempFolder);
			} catch (IOException exio) {
				DisplayUtil.displayError(exio.getMessage());
				return;
			}*/
			
			try {
				Utility.unZipIt(fileToUpload.getInputStream(), migrationTempFolder);
			} catch (IOException exio) {
				DisplayUtil.displayError(exio.getMessage());
				return;
			}

			try {
				migrateApplicationData(migrationTempFolder, logFilePath);
			

                FacesUtil.returnFromDialogAndRefresh();
                
				//Logger.debug("editMode: "+this.editMode);

			} catch (RemoteException ex) {
				DisplayUtil.displayError(ex.getMessage());
				return;
			}
		}
		
		public void migrateApplicationData(String migrationTempFolder, String logFilePath) throws DAOException{
			applicationCsvFilePath = migrationTempFolder + "\\NSRApplication.csv";
			euCsvFilePath = migrationTempFolder + "\\NSRApplicationEu.csv";

			try {
				printWriter = new PrintWriter(new File(logFilePath));

				wirteMigrateLog("The path of the log file: " + logFilePath);

				//if (!validateMigrationFileNames())
				//	return;
				addNSRApplicationData();
				addNSRApplicationEuData();
				//DisplayUtil.displayInfo("Migration Operation Completed");

			} catch (FileNotFoundException e) {
				DisplayUtil.displayError(e.getMessage());

			} finally {
				if (printWriter != null) {
					printWriter.close();
					printWriter = null;
				}
			}
			
			return;
		}
		
		private final void wirteMigrateLog(String message) {
			if (printWriter == null)
				return;

			SimpleDateFormat ft = new SimpleDateFormat(
					"yyyy.MM.dd hh:mm:ss.SSS zzz");

			String msg = "****** Time: " + ft.format(new Date()) + ", " + message;

			printWriter.println(msg);
			//DisplayUtil.displayInfo(msg);
			// logger.debug(msg);
		}
		
		private final void wirteMigrateError(String message, String wiseViewId) {
			wirteMigrateError(message + ", WISE View ID: " + wiseViewId);
		}
		
		private final void wirteMigrateError(String message) {
			if (printWriter == null)
				return;

			String msg = "Migration Error:" + message;

			printWriter.println(msg);
			//DisplayUtil.displayError(msg);

			SimpleDateFormat ft = new SimpleDateFormat(
					"yyyy.MM.dd hh:mm:ss.SSS zzz");
					
			logger.error("****** Time: " + ft.format(new Date()) + ", " + msg);
		}
		
		public void addNSRApplicationData() throws DAOException {
			
			CSVReader csvReader = null;

			try {
				String[] rawData;

				csvReader = new CSVReader(new FileReader(applicationCsvFilePath));

				while ((rawData = csvReader.readNext()) != null) {
					/*if (rawData.length < 4) {
						wirteMigrateError(
								"The Emission Unit csv format is incorrect, csv raw data: ",
								Utility.toString(rawData, ", "));
						continue;
					}*/
					reloadApplicationWithModifiedData(rawData);

				}
			} catch (Exception efo) {
				wirteMigrateError(efo.getMessage());

			} finally {
				if (csvReader != null) {
					try {
						csvReader.close();
					} catch (IOException eio) {
						wirteMigrateError(eio.getMessage());
					}
				}
			}

			
			return;
		}
		
		
		public void addNSRApplicationEuData() throws DAOException {
			
			CSVReader csvReader = null;

			try {
				String[] rawData;

				csvReader = new CSVReader(new FileReader(euCsvFilePath));

				while ((rawData = csvReader.readNext()) != null) {
					/*if (rawData.length < 4) {
						wirteMigrateError(
								"The Emission Unit csv format is incorrect, csv raw data: ",
								Utility.toString(rawData, ", "));
						continue;
					}*/
					reloadApplicationEuWithModifiedData(rawData);

				}
			} catch (Exception efo) {
				wirteMigrateError(efo.getMessage());

			} finally {
				if (csvReader != null) {
					try {
						csvReader.close();
					} catch (IOException eio) {
						wirteMigrateError(eio.getMessage());
					}
				}
			}

			
			return;
		}
		
		public void reloadApplicationWithModifiedData(String[] rawData) {						
					
					migratedData = rawData;
					getMigratedData();
					
					
					try{
					Contact con  = application.getContact();
					if(con==null){
						String  cntId = rawData[20];
						//Contact contact = contactService.retrieveContactDetail(rawData[20]);
						applicationContact = contactService.retrieveContactDetail(Integer.parseInt(cntId.substring(3, cntId.length())));
						
						//applicationContact = selectedContact.getContact();
						usingFacilityContact = true;

						// clear out old contact information to avoid mismatch
						// with last modified value on update
						application.setContact(null);

						// force contact information to be read only
						// if this is not done, the contact data will not be
						// displayed
						// on the screen
						contactReadOnly = true;

						// has contact
						newContact = true;			
						
					}else if(con!=null){
						if(!con.getCntId().equalsIgnoreCase(rawData[20])){
							String  cntId = rawData[20];							
							Contact contact = contactService.retrieveContactDetail(Integer.parseInt(cntId.substring(3, cntId.length())));
							applicationContact.setCntId(contact.getCntId());	
							applicationContact.setAddress(contact.getAddress());		
							applicationContact.setFirstNm(contact.getFirstNm());	
							applicationContact.setMiddleNm(contact.getMiddleNm());	
							applicationContact.setLastNm(contact.getLastNm());	
							applicationContact.setCompanyTitle(contact.getCompanyTitle());	
							applicationContact.setCmpId(contact.getCmpId());
							applicationContact.setPhoneNo(contact.getPhoneNo());
							applicationContact.setEmailAddressTxt(contact.getEmailAddressTxt());
							applicationContact.setEmailAddressTxt2(contact.getEmailAddressTxt2());
							applicationContact.setLastModified(contact.getLastModified());	
							applicationContact.setTitleCd(contact.getTitleCd());	
							applicationContact.setContactId(contact.getContactId());
							usingFacilityContact = false;
						}
					}
					
					
					}catch(Exception e){
						e.printStackTrace();
					}
					
					this.migrate=true;
					updateApplication();
					
					List<EmissionUnit>  tempEus = getSortedEUList(false);
					Iterator<EmissionUnit> eus = tempEus.iterator();
					while(eus.hasNext()){
						EmissionUnit eu = eus.next();
						if(eu.getEpaEmuId().toString().equalsIgnoreCase(rawData[21])){							
							eusToAdd.add(eu.getCorrEpaEmuId());
							updateApplicationEUs();
						}
					}
					
					/*List<ApplicationEU>  appEus  = ((PTIOApplication)application).getEus();
					Iterator<ApplicationEU> appEu = appEus.iterator();
					while(appEu.hasNext()){
						ApplicationEU selectedEU = appEu.next();
						if(selectedEU.getFpEU().getEpaEmuId().toString().equalsIgnoreCase(rawData[21])){
							((PTIOApplicationEU)selectedEU).setPtioEUPurposeCD(rawData[22]);
							((PTIOApplicationEU)selectedEU).getFpEU().setEmissionUnitTypeCd(rawData[23]);
							((PTIOApplicationEU)selectedEU).setOpSchedHrsDay(Integer.parseInt(rawData[24]));
							((PTIOApplicationEU)selectedEU).setOpSchedHrsYr(Integer.parseInt(rawData[25]));
							((PTIOApplicationEU)selectedEU).setPsdBACTFlag(rawData[26]);
							((PTIOApplicationEU)selectedEU).setBactFlag(rawData[27]);
							((PTIOApplicationEU)selectedEU).setNsrLAERFlag(rawData[28]);
							((PTIOApplicationEU)selectedEU).setLaerFlag(rawData[29]);
							((PTIOApplicationEU)selectedEU).setNspsApplicableFlag(rawData[30]);
							((PTIOApplicationEU)selectedEU).setNeshapApplicableFlag(rawData[31]);
							((PTIOApplicationEU)selectedEU).setMactApplicableFlag(rawData[32]);
							((PTIOApplicationEU)selectedEU).setPsdApplicableFlag(rawData[33]);
							((PTIOApplicationEU)selectedEU).setNsrApplicableFlag(rawData[34]);	
							if (selectedEU instanceof PTIOApplicationEU) {	
								setSelectedEU(selectedEU);
								updateEU();
							}
						}
					}*/
					
			return;
		}
		
		public String getMigratedData() {
			try{
			if(migratedData != null) {				
				application.setReceivedDate(Timestamp.valueOf(migratedData[0]));
				((PTIOApplication)application).setLegacyStatePTOApp(Boolean.parseBoolean(migratedData[1]));
				setNonEditablePtioApplicationPurposeCds(new ArrayList<String>(Arrays.asList(migratedData[2].split(","))));
				((PTIOApplication)application).setOtherPurposeDesc(migratedData[3]);					
				((PTIOApplication)application).setPotentialTitleVFlag(migratedData[4]);
				((PTIOApplication)application).setSageGrouseCd(migratedData[5]);
				((PTIOApplication)application).setApplicationDesc(migratedData[6]);
				((PTIOApplication)application).setSageGrouseAgencyName(migratedData[7]);
				((PTIOApplication)application).setFacilityChangedLocationFlag(migratedData[8]);
				((PTIOApplication)application).setContainH2SFlag(migratedData[9]);
				((PTIOApplication)application).setDivisionContacedFlag(migratedData[10]);
				((PTIOApplication)application).setPsdApplicableFlag(migratedData[11]);
				((PTIOApplication)application).setNsrApplicableFlag(migratedData[12]);
				setTradeSecretValue(migratedData[13]);
				((PTIOApplication)application).setModelingContactFlag(migratedData[14]);
				((PTIOApplication)application).setModelingAnalysisFlag(migratedData[15]);
				((PTIOApplication)application).setPreventionPsdFlag(migratedData[16]);
				((PTIOApplication)application).setPreAppMeetingFlag(migratedData[17]);
				((PTIOApplication)application).setModelingProtocolSubmitFlag(migratedData[18]);
				((PTIOApplication)application).setAqrvAnalysisSubmitFlag(migratedData[19]);
			}
			
			migratedData = null;
			}catch(Exception e){
				wirteMigrateError("CSV file has wrong data.");
				e.printStackTrace();
			}
			return null;
		}
		
		public void reloadApplicationEuWithModifiedData(String[] rawData) {		
			List<ApplicationEU>  appEus  = ((PTIOApplication)application).getEus();
			Iterator<ApplicationEU> appEu = appEus.iterator();
			while(appEu.hasNext()){
				ApplicationEU selectedEU = appEu.next();
				if(selectedEU.getFpEU().getEpaEmuId().toString().equalsIgnoreCase(rawData[0])){
					((PTIOApplicationEU)selectedEU).setPtioEUPurposeCD(rawData[1]);
					((PTIOApplicationEU)selectedEU).getFpEU().setEmissionUnitTypeCd(rawData[2]);
					((PTIOApplicationEU)selectedEU).setOpSchedHrsDay(Integer.parseInt(rawData[3]));
					((PTIOApplicationEU)selectedEU).setOpSchedHrsYr(Integer.parseInt(rawData[4]));
					((PTIOApplicationEU)selectedEU).setPsdBACTFlag(rawData[5]);
					((PTIOApplicationEU)selectedEU).setBactFlag(rawData[6]);
					((PTIOApplicationEU)selectedEU).setNsrLAERFlag(rawData[7]);
					((PTIOApplicationEU)selectedEU).setLaerFlag(rawData[8]);
					((PTIOApplicationEU)selectedEU).setNspsApplicableFlag(rawData[9]);
					((PTIOApplicationEU)selectedEU).setNeshapApplicableFlag(rawData[10]);
					((PTIOApplicationEU)selectedEU).setMactApplicableFlag(rawData[11]);
					((PTIOApplicationEU)selectedEU).setPsdApplicableFlag(rawData[12]);
					((PTIOApplicationEU)selectedEU).setNsrApplicableFlag(rawData[13]);	
					if (selectedEU instanceof PTIOApplicationEU) {	
						setSelectedEU(selectedEU);
						updateEU();
					}
				}
			}
		}
		
		public List<SelectItem> getPtioApplicationFacilityTypeDefs() {
			List<SelectItem> defs = new ArrayList<SelectItem>();
			for (SelectItem item : PTIOApplicationFacilityTypeDef.getData()
					.getItems().getAllItems()) {
				defs.add(item);
			}
			return defs;
		}
		
		/**
		 * TFS - 4782
		 * Determines if a non-admin user is allowed to partially edit a submitted NSR application.
		 * Facility Type, Sage Grouse, and Name of Agency/Department fields are editable if all of
		 * the following conditions are met.
		 * 
		 * 1. Application is internal 
		 * 2. User is neither admin nor read-only.
		 * 3. NSR application is submitted i.e., submitted date is not null.
		 * 
		 * @param none
		 * 
		 * @return boolean
		 *  			true if partial edit of NSR application is allowed, otherwise false.
		 */
		
		public boolean isSemiEditAllowed() {
			return isInternalApp() &&
					application.getApplicationTypeCD().equalsIgnoreCase(ApplicationTypeDef.PTIO_APPLICATION)
					&& !(isStars2Admin() || isReadOnlyUser())
					&& (!readOnly && !applicationDeleted && application.getSubmittedDate() != null);
		}
		
		public String enterSemiEditMode() {
			semiEditMode = true;
			setMigrate(false);

			if (selectedEU != null) {
				reloadWithSelectedEU();
			}

			return null; // stay on same page
		}
		
		public boolean isSemiEditMode() {
			return semiEditMode;
		}
		
	public void preparePrintableDocumentReferencedEUsList(boolean includeAllAttachments) {
		List<Document> docList = new ArrayList<Document>();
		if (application != null) {
			try {
				Facility facility = new Facility();
				
				Facility appFacility = getFacilityService()
								.retrieveFacilityProfile(
										application.getFacility().getFpId(),
										isPortalApp() && !readOnly, null);
		
				// make a copy of the application.facility using serialization
				ObjectOutputStream oos;
				ObjectInputStream ois;
				ByteArrayOutputStream bos;
				ByteArrayInputStream bis;
				byte[] bytes;
				
				bos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(bos);
				oos.writeObject(appFacility);
				bytes = bos.toByteArray();
				
				bis = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bis);
				facility = (Facility)ois.readObject();
				
				List<EmissionUnit> facilityEUs = facility.getEmissionUnits();
				List<ControlEquipment> facilityCEs = facility.getControlEquips();
				List<EgressPoint> facilityEPs = facility.getEgressPointsList();
				
				List<ApplicationEU> applicationEUs = application.getIncludedEus();
				if (null != applicationEUs) {
					List<Integer> includedEmuIds = new ArrayList<Integer>();

					for (ApplicationEU aeu : applicationEUs) {
						includedEmuIds.add(aeu.getFpEU().getCorrEpaEmuId());
					}

					List<EmissionUnit> referencedEUs = new ArrayList<EmissionUnit>();
					for (Integer corrId : includedEmuIds) {
						EmissionUnit eu = facility
								.getMatchingEmissionUnit(corrId);
						referencedEUs.add(eu);
					}

					/* emission units referenced in the application */
					facilityEUs.clear(); // list contains all the eus in the
											// facility, so remove them first
					facilityEUs.addAll(referencedEUs);
				} else {
					facilityEUs.clear();
				}
				if(null != facilityEUs) {
					String str = "";
					for(EmissionUnit eu : facilityEUs) {
						str += " " + eu.getEpaEmuId() + " ";
					}
				}
				
				/* control equipment connected to the referenced eus in the application */
				facilityCEs.clear(); // list contains all the control equipment in the facility, so remove them first
				facilityCEs.addAll(getAllReferencedControlEquipment(facilityEUs));
				if(null != facilityCEs) {
					String str = "";
					for(ControlEquipment ce : facilityCEs) {
						str += " " + ce.getControlEquipmentId() + " ";
					}
				}
				
				/* release points connected to the referenced eus in the application */
				facilityEPs.clear(); // list contains all the release points in the facility, so remove them first
				facilityEPs.addAll(getAllReferencedReleasePoints(facilityEUs));	
				if(null != facilityEPs) {
					String str = "";
					for(EgressPoint ep : facilityEPs) {
						str += " " + ep.getReleasePointId() + " ";
					}
				}
				
				Document facilityDoc = getFacilityService().generateTempFacilityProfileReport(facility, "AppEUsOnly");
				facilityDoc.setDescription("Printable View of Facility Inventory - Referenced EUs Only");
				
				TmpDocument appDoc = new TmpDocument();
				boolean hasTS = getApplicationService()
									.generateTempApplicationReport(application, hideTradeSecret, appDoc, false, includeAllAttachments);
				
				TmpDocument zipDoc = new TmpDocument();
				// Set the path elements of the temp doc.
				zipDoc.setDescription(ApplicationService.APPLICATION_ZIP_FILE);
				zipDoc.setFacilityID(application.getFacilityId());
				zipDoc.setTemporary(true);
				if(this.hideTradeSecret) {
					zipDoc.setTmpFileName(application.getApplicationNumber() + "_appEUsonly.zip");
				} else {
					zipDoc.setTmpFileName(application.getApplicationNumber() + "_appEUsonly" + "_TS" + ".zip");
				}
				zipDoc.setContentType(Document.CONTENT_TYPE_ZIP);

				// make sure temporary directory exists
				DocumentUtil.mkDirs(zipDoc.getDirName());
				OutputStream os = DocumentUtil.createDocumentStream(zipDoc.getPath());
				ZipOutputStream zos = new ZipOutputStream(os);
				getApplicationService().zipApplicationFiles(application, facilityDoc, zos, hideTradeSecret,
						includeAllAttachments, readOnly);
				zos.close();
				os.close();
				
				if (!isPublicApp()) {
					docList.add(facilityDoc);
				}
				docList.add(appDoc);
				docList.add(zipDoc);

				
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			} catch (IOException ioe) {
				logger.error(ioe.getMessage(), ioe);
			} catch (ClassNotFoundException cnfe) {
				logger.error(cnfe.getMessage(), cnfe);
			}
		}
			
		printableDocumentAppRefEUs = docList;
	}
	
	/**
		For a given list of emission units, returns the list of control equipment that are connected
		directly or indirectly to the emission process that are associated with the given list of
		emissions units.
		
		@param - List<EmissionUnit> a list of EmissionUnit
		@return - List<ControlEquipment> list of control equipment connected to the given emission units
		@exception - none

	*/
	public List<ControlEquipment> getAllReferencedControlEquipment(List<EmissionUnit> facilityEUs) {
		
		HashMap<String, ControlEquipment> referencedControlEquipment = new HashMap<String, ControlEquipment>();
		
		for(EmissionUnit eu : facilityEUs) {
			for(EmissionProcess ep : eu.getEmissionProcesses()) {
				Set<ControlEquipment> controlEquipList = null;
				controlEquipList = ep.getAllControlEquipments();
				if(null != controlEquipList) {
					for(ControlEquipment ce : controlEquipList) {
						referencedControlEquipment.put(ce.getControlEquipmentId(), ce);
					}
				}
			}
		}
		
		return new ArrayList<ControlEquipment>(referencedControlEquipment.values());
	}
	
	/**
		For a given list of emission units, returns the list of release points that are connected
		directly or indirectly to the emission process that are associated with the given list of
		emissions units.
		
		@param - List<EmissionUnit> a list of EmissionUnit
		@return - List<EgressPoint> list of release points connected to the given emission units
		@exception - none

	*/
	public List<EgressPoint> getAllReferencedReleasePoints(List<EmissionUnit> facilityEUs) {
		HashMap<String, EgressPoint> referencedReleasePoints = new HashMap<String, EgressPoint>();
		
		for(EmissionUnit eu : facilityEUs) {
			for(EmissionProcess ep : eu.getEmissionProcesses()) {
				Set<EgressPoint> egressPointList = null;
				egressPointList = ep.getAllEgressPoints();
				if(null != egressPointList) {
					for(EgressPoint eg : egressPointList) {
						referencedReleasePoints.put(eg.getReleasePointId(), eg);
					}
				}
			}
		}
		
		return new ArrayList<EgressPoint>(referencedReleasePoints.values());
	}
	
	/**
	 * Load data for the application identified by applicationID. Within the
	 * portal application, this method will always load data from the STAGING
	 * schema.
	 * 
	 * @param applicationID
	 */

	private void loadApplicationSummary(int applicationID) {
		try {
			ApplicationService appBO = getApplicationService();
			staging = false;
			if (isPortalApp()) {
				staging = true;
			}

			if (isInternalApp()) {
				application = appBO.retrieveApplicationSummary(applicationID);
			} else {
				application = appBO.retrieveApplicationSummary(applicationID,
						staging);
			}

			loadApplicationSummary();

		} catch (RemoteException re) {
			handleException("Exception for application ID " + applicationID, re);
		}
	}
/*
	private void loadApplicationWithSelectedEU(int applicationID) {
		try {
			ApplicationService appBO = getApplicationService();
			staging = true;

			application = appBO.retrieveApplicationWithBasicEUs(applicationID);

			if (application != null) {
				setSelectedEU(appBO.retrieveApplicationEU(getSelectedEU()
						.getApplicationEuId()));
				loadApplicationEU();
			}
		} catch (RemoteException re) {
			handleException("Exception for application ID " + applicationID, re);
		}
	}
*/	
	private void loadApplicationWithSelectedEU(int applicationID) {
		try {
			ApplicationService appBO = getApplicationService();
			staging = false;
			if (isPortalApp()) {
				staging = true;
			}

			if (isInternalApp()) {
				application = appBO.retrieveApplicationWithBasicEUs(applicationID);
			} else {
				application = appBO.retrieveApplicationWithBasicEUs(applicationID, staging);
			}

			if (application != null) {
				setSelectedEU(appBO.retrieveApplicationEU(getSelectedEU()
						.getApplicationEuId()));
				loadApplicationEU();
			}
		} catch (RemoteException re) {
			handleException("Exception for application ID " + applicationID, re);
		}
	}

	private void loadApplicationWithAllEUs(int applicationID) {
		try {
			ApplicationService appBO = getApplicationService();
			staging = false;
			if (isPortalApp()) {
				staging = true;
			}

			if (isInternalApp()) {
				application = appBO.retrieveApplicationWithAllEUs(applicationID);
			} else {
				application = appBO.retrieveApplicationWithAllEUs(applicationID, staging);
			}

		} catch (RemoteException re) {
			handleException("Exception for application ID " + applicationID, re);
		}
	}

	private void loadApplicationWithIncludedEUs(int applicationID) {
		try {
			ApplicationService appBO = getApplicationService();
			
			staging = false;
			if (isPortalApp()) {
				staging = true;
			}

			if (isInternalApp()) {
				application = appBO
					.retrieveApplicationWithIncludedEUs(applicationID);
			} else {
				application = appBO.retrieveApplicationWithIncludedEUs(applicationID, staging);
			}

		} catch (RemoteException re) {
			handleException("Exception for application ID " + applicationID, re);
		}
	}

	private void loadApplicationSummary(String applicationNumber) {
		Calendar now = new GregorianCalendar();
		logger.debug("Enter loadApplicationSummary(String applicationNumber)"
				+ new Timestamp(now.getTimeInMillis()).toString());
		try {
			ApplicationService appBO = getApplicationService();
			// when retrieving a read-only ITR app from staging, we need to get
			// facility info from My Tasks
			if (isPortalApp() && readOnly && applicationNumber != null
					&& applicationNumber.startsWith("REL")) {
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				Facility facility = myTasks.getFacility();
				application = appBO.retrieveRelocationApplication(
						applicationNumber, facility);
			} else {
				application = appBO
						.retrieveApplicationSummary(applicationNumber);
			}
			staging = false;
			if (this.isPortalApp()) {
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_applications"))
						.setRendered(false);
			}
			if (application != null) {

				loadApplicationSummary();

			} else {
				DisplayUtil.displayError("No application found with number "
						+ applicationNumber + ".");
			}
		} catch (RemoteException re) {
			handleException("Exception for application " + applicationNumber,
					re);
		}
		now = new GregorianCalendar();
		logger.debug("Exit loadApplicationSummary(String applicationNumber)"
				+ new Timestamp(now.getTimeInMillis()).toString());
	}

	private void loadApplicationWithSelectedEU(String applicationNumber) {
		try {
			ApplicationService appBO = getApplicationService();
			// when retrieving a read-only ITR app from staging, we need to get
			// facility info from My Tasks
			if (isPortalApp() && readOnly && applicationNumber != null
					&& applicationNumber.startsWith("REL")) {
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				Facility facility = myTasks.getFacility();
				application = appBO.retrieveRelocationApplication(
						applicationNumber, facility);
			} else {
				application = appBO.retrieveApplicationSummary(applicationNumber);
			}
			staging = false;
			if (this.isPortalApp()) {
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_applications"))
						.setRendered(false);
			}
			if (application != null) {
				setSelectedEU(appBO
						.retrieveApplicationEUReadOnly(getSelectedEU()
								.getApplicationEuId()));
				loadApplicationEU();

			} else {
				DisplayUtil.displayError("No application found with number "
						+ applicationNumber + ".");
			}
		} catch (RemoteException re) {
			handleException("Exception for application " + applicationNumber,
					re);
		}

	}

	private void loadApplicationWithAllEUs(String applicationNumber) {
		try {
			ApplicationService appBO = getApplicationService();
			// when retrieving a read-only ITR app from staging, we need to get
			// facility info from My Tasks
			if (isPortalApp() && readOnly && applicationNumber != null
					&& applicationNumber.startsWith("REL")) {
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				Facility facility = myTasks.getFacility();
				application = appBO.retrieveRelocationApplication(
						applicationNumber, facility);
			} else {
				application = appBO
						.retrieveApplicationWithAllEUs(applicationNumber);
			}
			staging = false;
			if (this.isPortalApp()) {
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_applications"))
						.setRendered(false);
			}
			if (application == null) {
				DisplayUtil.displayError("No application found with number "
						+ applicationNumber + ".");
			}
		} catch (RemoteException re) {
			handleException("Exception for application " + applicationNumber,
					re);
		}

	}

	private void loadApplicationWithIncludedEUs(String applicationNumber) {
		try {
			ApplicationService appBO = getApplicationService();
			// when retrieving a read-only ITR app from staging, we need to get
			// facility info from My Tasks
			if (isPortalApp() && readOnly && applicationNumber != null
					&& applicationNumber.startsWith("REL")) {
				MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
				Facility facility = myTasks.getFacility();
				application = appBO.retrieveRelocationApplication(
						applicationNumber, facility);
			} else {
				application = appBO
						.retrieveApplicationWithIncludedEUs(applicationNumber);
			}
			staging = false;
			if (this.isPortalApp()) {
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_applications"))
						.setRendered(false);
			}
			if (application == null) {
				DisplayUtil.displayError("No application found with number "
						+ applicationNumber + ".");
			}
		} catch (RemoteException re) {
			handleException("Exception for application " + applicationNumber,
					re);
		}

	}

	private void loadApplicationSummary() {
		Calendar now = new GregorianCalendar();
		logger.debug("Enter loadApplicationSummary()"
				+ new Timestamp(now.getTimeInMillis()).toString());
		if (application != null) {
			facilityContacts = null;
			try {
				// load document data for application attachments
				getApplicationService().loadApplicationDetailDocuments(
						application, readOnly);
			} catch (RemoteException e) {
				handleException(
						"Exception for application "
								+ application.getApplicationNumber(), e);
			}

			loadApplicationData();
			createTree();
		} else {
			applicationDeleted = true;
			DisplayUtil.displayWarning("Selected application does not exist. "
					+ "Refresh data and try again.");
		}
		now = new GregorianCalendar();
		logger.debug("exit loadApplicationSummary()"
				+ new Timestamp(now.getTimeInMillis()).toString());
	}

	private void loadApplicationEU() {
		if (application != null) {
			loadEuData();
			if (selectedEU != null) {
				setApplicationDocuments(selectedEU.getEuDocuments());
			}
			createTree();
		} else {
			applicationDeleted = true;
			DisplayUtil.displayWarning("Selected application does not exist. "
					+ "Refresh data and try again.");
		}

	}

	public String reloadApplicationSummary() {
		if (!readOnly) {
			loadApplicationSummary(application.getApplicationID());
		} else {
			// load read-only version if edit is not allowed
			loadApplicationSummary(application.getApplicationNumber());
		}
		return null;
	}

	public String reloadApplicationWithSelectedEU() {
		if (!readOnly) {
			loadApplicationWithSelectedEU(application.getApplicationID());
		} else {
			// load read-only version if edit is not allowed
			loadApplicationWithSelectedEU(application.getApplicationNumber());
		}
		return null;
	}

	public String reloadApplicationWithAllEUs() {
		if (!readOnly) {
			loadApplicationWithAllEUs(application.getApplicationID());
		} else {
			// load read-only version if edit is not allowed
			loadApplicationWithAllEUs(application.getApplicationNumber());
		}
		return null;
	}

	public String reloadApplicationWithIncludedEUs() {
		if (!readOnly) {
			loadApplicationWithIncludedEUs(application.getApplicationID());
		} else {
			// load read-only version if edit is not allowed
			loadApplicationWithIncludedEUs(application.getApplicationNumber());
		}
		return null;
	}

	public boolean isDisableDeleteOnPopup() {
		return disableDeleteOnPopup;
	}

	public void setDisableDeleteOnPopup(boolean disableDeleteOnPopup) {
		this.disableDeleteOnPopup = disableDeleteOnPopup;
	}

	public String getDisableDeleteMsgOnPopup() {
		return disableDeleteMsgOnPopup;
	}

	public void setDisableDeleteMsgOnPopup(String disableDeleteMsgOnPopup) {
		this.disableDeleteMsgOnPopup = disableDeleteMsgOnPopup;
	}

	public String startDelete() {
		if (application.getInspectionsReferencedIn().size() > 0) {
			setDisableDeleteOnPopup(true);
			setDisableDeleteMsgOnPopup("You cannot delete this Application while it is being referenced in other places.");
		}
		
		return "dialog:appDeleteDetail";
	}
	
	public boolean getDisplayReferencedInspectionsTable() {
		return isDisableDeleteOnPopup() && !application.getInspectionsReferencedIn().isEmpty();
	}
}