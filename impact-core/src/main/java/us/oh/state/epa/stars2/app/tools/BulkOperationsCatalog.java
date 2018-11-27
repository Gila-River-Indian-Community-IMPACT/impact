package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.PollEvent;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceList;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.FacilityOnlyRoleDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.NAICSDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitDocIssuanceStageDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.PortableGroupNames;
import us.oh.state.epa.stars2.def.PortableGroupTypes;
import us.oh.state.epa.stars2.def.ReportAttributes;
import us.oh.state.epa.stars2.def.RevenueState;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.def.SICDef;
import us.oh.state.epa.stars2.def.TransitStatusDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

/**
 * This class is modeled after ReportTree (JasperSoft management reports). The
 * difference is this class is using BOs to retrieve data as opposed to passing
 * the Search paramenters to the chosen Jasper report. Our strategy with BO is
 * not quite as flexible. To add a new Bulk Operation requires Java coding. You
 * need to add your own BO, and jsp popup.
 * 
 * The methods for editing / creating bulk operations are not used (could be
 * removed). At this point these methods have not been removed in the outside
 * chance that someone comes up with a mechanism to add a bulk operation without
 * adding Java and jsp code.
 * 
 * @author swooster
 * 
 */
public class BulkOperationsCatalog extends TreeBase {
	
	private static final long serialVersionUID = -1249835688026588050L;
	
	private static final int BULK_OPERATION_ADD_CORRESPONDENCE_MAX_LIMIT = 2000;
	private String bulkMenu;
	private BulkDef bulkOpDef;
	protected BulkOperation bulkOperation;
	private HashMap<Integer, BulkDef> bulkOperations;
	private transient UIXTable table;
	private transient boolean allowClose; // allow close after selected to save
											// or not save correspondence
	private transient boolean notReset = false; // if refine search not pushed
												// (initially false)

	// Common search attributes begin here.
	private String facilityId;
	private String facilityNm;
	private Timestamp startDate;
	private Timestamp endDate;
	private Integer year;

	// Facility search attributes begin here.
	private boolean bulkFacilitySearch;
	private boolean hasFacilitySearchResults;
	private FacilityList[] facilities;
	private FacilityList[] selectedFacilities;
	private String facilityDesc;
	private String facilityRole;
	private Integer staffId;
	private String taskName;
	private String doLaa;
	private String city;
	private String county;
	private String zipCode;
	private String permittingClassCd;
	private String issuanceStageCd;
	private String globalStatusCd;
	private String reportCategoryCd;
	private String tvPermitStatus;
	private String operatingStatus;
	private String sicCd;
	private String naicsCd;
	private boolean inAttainment;
	private boolean portable;
	private boolean neshapsInd;
	private String neshapsSubpart;
	private boolean nspsInd;
	private String nspsSubpart;
	private String perDueDate;
	private String newPerDueDate;
	private String pollutant;
	private boolean mactInd;
	private String mactCd;
	private String mactSubpart;
	private String transitStatus;
	private String portableGroupType;
	private String portableGroupName;
	private String euDesc;
	private String egressPointTypeCd;

	// New Facility search attributes (IMPACT)
	private boolean syncSearch;
	private boolean hasFacilitySyncSearchResults;
	private boolean facOwnerChange;
	private boolean generateNSRInvoice;
	private boolean generateNSRInvoiceFlag;   // Used to disable button
	private String companyName;
	private String cmpId;
	private String address1;
	private String permitClassCd;
	private String facilityTypeCd;
	private String operatingStatusCd = OperatingStatusDef.OP;
	private TableSorter facilitySyncResultsWrapper;
	private TableSorter selectedFacResultsWrapper;
	private TreeNode facilityOwnerNode;
	public String fac_owner_bo_path;
	public String owner_group_path;
	private TreeNode NSRInvoiceNode;
	public String nsr_invoice_bo_path;
	public String invoice_group_path;
	// Assigned in database
	public static Integer OWNER_BULK_ID = 505;
	public static Integer NSR_INVOICE_BULK_ID = 560;
	public static Integer FACILITY_PURGE_BULK_ID = 999;
	
	// New Contact Type search attributes (IMPACT)
	private boolean bulkContactTypeSearch;
	private boolean hasContactTypeSearchResults;
	private Integer contactId;
	private List<SelectItem> contactsList;
	private List<Contact> contacts;
	private ContactType[] contactTypes;
	private ContactType[] selectedContactTypes;
	private TableSorter contactTypeResultsWrapper;
	private TableSorter selectedCtypeResultsWrapper;

	// Permit search attributes begin here.
	private boolean hasPermitSyncSearchResults;
	private boolean bulkPermitSearch;
	private boolean hasPermitSearchResults;
	private Permit[] permits;
	private Permit[] selectedPermits;
	private String applicationNbr;
	private String permitType;
	private String permitReason;
	private String permitNumber;
	private String permitStatusCd;
	private TableSorter permitSyncResultsWrapper;
	private TableSorter selectedPermitResultsWrapper;

	// Correspondence search attributes begin here.
	private boolean bulkCorrespondenceSearch;
	private boolean hasCorrSearchResults;
	private String correspondenceType;
	private String correspondenceDate;
	private Timestamp dateGenerated;

	// Application search attributes begin here.
	private boolean hasApplicationSyncSearchResults;
	private boolean bulkApplicationSearch;
	private boolean hasApplicationSearchResults;
	private ApplicationSearchResult[] applications;
	private ApplicationSearchResult[] selectedApplications;
	private String applicationType;
	private TableSorter applicationSyncResultsWrapper;
	private TableSorter selectedAppResultsWrapper;

	// Emissions Inventory search attributes begin here.
	private boolean hasEmissionReportsSyncSearchResults;
	private boolean bulkEmissionsReportSearch;
	private boolean hasEmissionsReportSearchResults;
	private EmissionsReportSearch[] emissionsReports;
	private EmissionsReportSearch[] selectedEmissionsReports;
	private Integer emissionsReportId;
	
	// Compliance Report search attributes begin here.
	private boolean bulkComplianceReportSearch;
	private boolean hasComplianceReportSearchResults;
	private ComplianceReportList[] complianceReports;
	private ComplianceReportList[] selectedComplianceReports;

	// Invoice search attributes begin here.
	private Integer revenueId;
	private String revenueTypeCd;
	private String invoiceStateCd;
	private String revenueStateCd;
	private boolean bulkInvoiceSearch;
	private boolean hasInvoiceSearchResults;
	private InvoiceList[] invoices;
	private InvoiceList[] selectedInvoices;
	// For invoice beginDate and endDate, common attribs startDate and endDate
	// is utilised.
	
	// Inspection Search attributes begin here
	private String inspId;
	private boolean hasInspectionSyncSearchResults;
	private boolean bulkInspectionSearch;
	private boolean hasInspectionSearchResults;
	private FullComplianceEval[] inspections;
	private FullComplianceEval[] selectedInspections;
	private TableSorter inspectionSyncResultsWrapper;
	private TableSorter selectedInspectionResultsWrapper;
	
	// Facility Purge Search attributes
	private boolean purgeFacility;
	
	private TreeNode purgeFacilityNode;
	private boolean bulkFacilityPurgeSearch;
	private List<FacilityPurgeSearchLineItem> purdgeCandidates;	
	private List<FacilityPurgeSearchLineItem> selectedPurgeCandidates;
	private TableSorter fpurgeSearchResultWrapper;
	
	private Thread searchThread;
	private boolean _browserCompleted;
	private DisplayUtil displayUtilBean;
	private FacilityProfile fProf;;
	private int _percentage;

	private User user;
	private Integer currentUserId;

	// indicate whether operation is a workflow task-related operation
	private boolean taskOperation = false;
	// indicate whether operation is a facility role-related operation
	private boolean roleOperation = false;

	private boolean showProgressBar;
	private boolean showProgressBarInvoiceGeneration;

	private boolean isAsyncRunning = false;
	
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;
	
	private boolean pollRendered = false;
	
	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}


	public BulkOperationsCatalog() {
		selectedFacResultsWrapper = new TableSorter();
		facilitySyncResultsWrapper = new TableSorter();
		contactTypeResultsWrapper = new TableSorter();
		selectedCtypeResultsWrapper = new TableSorter();
		selectedAppResultsWrapper = new TableSorter();
		applicationSyncResultsWrapper = new TableSorter();
		selectedPermitResultsWrapper = new TableSorter();
		permitSyncResultsWrapper = new TableSorter();
		inspectionSyncResultsWrapper = new TableSorter();
		fpurgeSearchResultWrapper = new TableSorter();
		setGenerateNSRInvoiceFlag(false);
		this.currentUserId = InfrastructureDefs.getCurrentUserId();
	}

	public final String setBulkOpToBOPS() {
		stopSearch();
		reset2(); // Dennis ADDED for CETA Generate Letter
		treeData = null;

		bulkMenu = "bops";
		bulkOpDef = null;

		setBulkFacilitySearch(false);
		setHasFacilitySearchResults(false);
		setHasFacilitySyncSearchResults(false);
		setGenerateNSRInvoiceFlag(false);
		doLaa = null;
		facilities = null;
		selectedFacilities = null;
		contactTypes = null;
		selectedContactTypes = null;

		setBulkPermitSearch(false);
		setHasPermitSearchResults(false);
		permits = null;
		
		setBulkInspectionSearch(false);
		setHasInspectionSearchResults(false);
		inspections = null;

		setBulkCorrespondenceSearch(false);

		setBulkApplicationSearch(false);
		setHasApplicationSearchResults(false);
		setHasApplicationSyncSearchResults(false);

		setBulkEmissionsReportSearch(false);
		setHasEmissionsReportSearchResults(false);

		setBulkComplianceReportSearch(false);
		setHasComplianceReportSearchResults(false);

		setBulkContactTypeSearch(false);
		setHasContactTypeSearchResults(false);

		return "tools.bulkOperationsCatalog";
	}

	public final String setBulkOpToDGEN() {
		stopSearch();
		reset2(); // Dennis ADDED for CETA Generate Letter
		treeData = null;

		bulkMenu = "dgen";
		bulkOpDef = null;

		setBulkFacilitySearch(false);
		setHasFacilitySearchResults(false);
		setHasFacilitySyncSearchResults(false);
		setGenerateNSRInvoiceFlag(false);
		doLaa = null;
		facilities = null;
		selectedFacilities = null;
		contactTypes = null;
		selectedContactTypes = null;

		setBulkPermitSearch(false);
		setHasPermitSearchResults(false);
		permits = null;
		
		setBulkInspectionSearch(false);
		setHasInspectionSearchResults(false);
		inspections = null;

		setBulkCorrespondenceSearch(false);

		setBulkApplicationSearch(false);
		setHasApplicationSearchResults(false);
		setHasApplicationSyncSearchResults(false);

		setBulkEmissionsReportSearch(false);
		setHasEmissionsReportSearchResults(false);

		setBulkComplianceReportSearch(false);
		setHasComplianceReportSearchResults(false);

		setBulkContactTypeSearch(false);
		setHasContactTypeSearchResults(false);

		return "tools.docGenerationCatalog";
	}

	public final String setBulkOpToCOGN() {
		stopSearch();
		reset2(); // Dennis ADDED for CETA Generate Letter
		treeData = null;

		bulkMenu = "cogn";
		bulkOpDef = null;

		setBulkFacilitySearch(false);
		setHasFacilitySearchResults(false);
		setHasFacilitySyncSearchResults(false);
		setGenerateNSRInvoiceFlag(false);
		doLaa = null;
		facilities = null;
		selectedFacilities = null;
		contactTypes = null;
		selectedContactTypes = null;

		setBulkPermitSearch(false);
		setHasPermitSearchResults(false);
		permits = null;
		
		setBulkInspectionSearch(false);
		setHasInspectionSearchResults(false);
		inspections = null;

		setBulkCorrespondenceSearch(false);

		setBulkApplicationSearch(false);
		setHasApplicationSearchResults(false);
		setHasApplicationSyncSearchResults(false);

		setBulkEmissionsReportSearch(false);
		setHasEmissionsReportSearchResults(false);

		setBulkComplianceReportSearch(false);
		setHasComplianceReportSearchResults(false);

		setBulkInvoiceSearch(false);
		setHasInvoiceSearchResults(false);

		setBulkContactTypeSearch(false);
		setHasContactTypeSearchResults(false);

		return "tools.coDocGenerationCatalog";
	}

	public String getBulkSearchType() {

		if (bulkOpDef == null || bulkOpDef.getSearchType() == null) {
			return "";
		} else if (bulkOpDef.getSearchType().equalsIgnoreCase("facility")) {
			return "Facility ";
		} else if (bulkOpDef.getSearchType().equalsIgnoreCase("permit")) {
			return "Permit ";
		} else if (bulkOpDef.getSearchType().equalsIgnoreCase("applic")) {
			return "Application ";
		} else if (bulkOpDef.getSearchType().equalsIgnoreCase("emrpt")) {
			return "Emissions Inventory ";
		} else if (bulkOpDef.getSearchType().equalsIgnoreCase("cmplrpt")) {
			return "Compliance Report ";
		} else if (bulkOpDef.getSearchType().equalsIgnoreCase("cnt_typ")) {
			return "Contact Type ";
		}

		return "";

	}

	public final UIXTable getTable() {
		return table;
	}

	public final void setTable(UIXTable table) {
		this.table = table;
	}

	public final void setSelectedTreeNode(TreeNode selectedTreeNode) {
		super.setSelectedTreeNode(selectedTreeNode);
		// reset(); //DENNIS commented out for CETA Generate Letter
		refineSearch(); // DENNIS addded since reset() commented out
		getBulkDef();
	}

	public final List<String> getCurrentReportAttributes() {
		List<String> ret = new ArrayList<String>();

		if (bulkOpDef != null) {
			for (SimpleDef attr : bulkOpDef.getAttributes()) {
				ret.add(attr.getCode());
			}
		}

		return ret;
	}

	public final HashMap<String, String> getAllBulkOperationAttributes() {

		List<SelectItem> tempList = ReportAttributes.getData().getItems()
				.getItems(getPermittingClassCd(), true);

		HashMap<String, String> ret = new HashMap<String, String>();

		for (SelectItem item : tempList) {
			ret.put(item.getLabel(), (String) item.getValue());
		}
		return ret;
	}

	public final BulkOperation getBulkOperation() {
		return bulkOperation;
	}

	public final synchronized void setErrorMessage(String error) {
		displayUtilBean.addMessageToQueue(error, DisplayUtil.ERROR, null);
	}

	public final synchronized void setWarnMessage(String error) {
		displayUtilBean.addMessageToQueue(error, DisplayUtil.WARN, null);
	}

	public final synchronized void setInfoMessage(String info) {
		displayUtilBean.addMessageToQueue(info, DisplayUtil.INFO, null);
	}

	public final BulkDef getBulkDef() {

		if (selectedTreeNode.getType().equals("bulkOperation")) {

			if (bulkOperations == null) {
				bulkOperations = new HashMap<Integer, BulkDef>(0);
			}

			if ((bulkOpDef == null)
					|| (selectedTreeNode.getIdentifier().compareTo(
							bulkOpDef.getBulkId().toString()) != 0)) {

				bulkOpDef = bulkOperations.get(new Integer(selectedTreeNode
						.getIdentifier()));

				try {
					bulkOpDef = getInfrastructureService().retrieveBulkDef(
							bulkOpDef.getBulkId());

					if (bulkOpDef.getSearchType().equals("permit")) {
						bulkPermitSearch = true;
					} else if (bulkOpDef.getSearchType().equals("facility")) {
						bulkFacilitySearch = true;
					} else if (bulkOpDef.getSearchType().equals("corres")) {
						bulkCorrespondenceSearch = true;
					} else if (bulkOpDef.getSearchType().equals("applic")) {
						bulkApplicationSearch = true;
					} else if (bulkOpDef.getSearchType().equals("emrpt")) {
						bulkEmissionsReportSearch = true;
					} else if (bulkOpDef.getSearchType().equals("cmplrpt")) {
						bulkComplianceReportSearch = true;
					} else if (bulkOpDef.getSearchType().equals("invoice")) {
						bulkInvoiceSearch = true;
					} else if (bulkOpDef.getSearchType().equals("cnt_typ")) {
						bulkContactTypeSearch = true;
					} else if (bulkOpDef.getSearchType().equals("insp")) {
						bulkInspectionSearch = true;
					} else if (bulkOpDef.getSearchType().equals("fpurge")){
						bulkFacilityPurgeSearch = true;
					}

					bulkOperation = (BulkOperation) Class.forName(
							bulkOpDef.getClassname()).newInstance();

					bulkOperation.init(this);
				} catch (RemoteException re) {
					logger.error(re.getMessage(), re);
					DisplayUtil
							.displayError("System error. Please contact system administrator");
				} catch (ClassNotFoundException cnfe) {
					logger.error(cnfe.getMessage(), cnfe);
					DisplayUtil
							.displayError("System error. Please contact system administrator");
				} catch (InstantiationException ie) {
					logger.error(ie.getMessage(), ie);
					DisplayUtil
							.displayError("System error. Please contact system administrator");
				} catch (IllegalAccessException iae) {
					logger.error(iae.getMessage(), iae);
					DisplayUtil
							.displayError("System error. Please contact system administrator");
				}
			}
		}

		return bulkOpDef;
	}

	@SuppressWarnings("unchecked")
	public final TreeModelBase getTreeData() {

		if (treeData == null) {

			TreeNodeBase root = new TreeNodeBase("root", "Bulk Operations",
					"root", false);
			treeData = new TreeModelBase(root);
			ArrayList<String> treePath = new ArrayList<String>();
			treePath.add("0");

			String ownerId = null;
			String invoiceId = null;
			TreeNodeBase nsrBillingGenerateInvoiceNode = null;

			try {
				HashMap<String, TreeNodeBase> reportGroups = new HashMap<String, TreeNodeBase>();
				HashMap<String, TreeNodeBase> freeStandingReports = new HashMap<String, TreeNodeBase>();

				int groupNum = 0;

				BulkDef[] tempBulkOperations = getInfrastructureService()
						.retrieveBulkDefs(bulkMenu);
				bulkOperations = new HashMap<Integer, BulkDef>(
						tempBulkOperations.length);

				for (BulkDef bulkO : tempBulkOperations) {

					bulkOperations.put(bulkO.getBulkId(), bulkO);

					TreeNodeBase reportNode = new TreeNodeBase("bulkOperation",
							bulkO.getName(), bulkO.getBulkId().toString(),
							false);

					if (bulkO.getGroupNm() != null) {

						TreeNodeBase groupNode = reportGroups.get(bulkO
								.getGroupNm());

						if (groupNode == null) {
							treePath.add("0:" + Integer.toString(groupNum));

							groupNode = new TreeNodeBase("group",
									bulkO.getGroupNm(),
									Integer.toString(groupNum++), false);
							reportGroups.put(bulkO.getGroupNm(), groupNode);

							if (bulkO.getBulkId().equals(OWNER_BULK_ID)) {
								// set Owner group path
								ownerId = groupNode.getIdentifier();
							}

						}
						
						if (bulkO.getBulkId().equals(NSR_INVOICE_BULK_ID)) {
							// set NSR Billing - Generate Invoice group path
							invoiceId = groupNode.getIdentifier();
							nsrBillingGenerateInvoiceNode = reportNode;
						}
						
						if (bulkO.getBulkId().equals(FACILITY_PURGE_BULK_ID)){
							purgeFacilityNode = reportNode;
						}

						groupNode.getChildren().add(reportNode);

					} else {
						freeStandingReports.put(bulkO.getName(), reportNode);
					}

				}

				// Add all the groups to the root tree
				for (TreeNodeBase groupNode : reportGroups.values()) {
					// set owner group path
					if (ownerId != null) {
						if (ownerId.equals(groupNode.getIdentifier())) {
							owner_group_path = "0:" + root.getChildCount();
							fac_owner_bo_path = owner_group_path + ":"
									+ (groupNode.getChildCount() - 1);
						}
					}
					
					if (invoiceId != null && nsrBillingGenerateInvoiceNode != null) {
						int index = groupNode.getChildren().indexOf(nsrBillingGenerateInvoiceNode);
						if (invoiceId.equals(groupNode.getIdentifier())) {
							invoice_group_path = "0:" + root.getChildCount();
							nsr_invoice_bo_path = invoice_group_path + ":"
									+ index;
						}
					}

					root.getChildren().add(groupNode);
				}

				// Add the reports that aren't in groups to the tree
				for (TreeNodeBase reportNode : freeStandingReports.values()) {
					root.getChildren().add(reportNode);
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}

			TreeStateBase treeState = new TreeStateBase();

			treeState.collapsePath(treePath.toArray(new String[0]));
			treeData.setTreeState(treeState);
			selectedTreeNode = root;
			current = "root";
		}

		if (facOwnerChange) {
			selectFacilityOwnership();
		}
		if (generateNSRInvoice) {
			selectGenerateNSRInvoice();
		}
		
		if (purgeFacility){
			selectPurgeFacility();
		}

		return treeData;
	}

	private void selectFacilityOwnership() {

		facilityOwnerNode = treeData.getNodeById(fac_owner_bo_path);

		TreeStateBase treeState = new TreeStateBase();
		treeState.toggleExpanded(owner_group_path);
		treeState.setSelected(fac_owner_bo_path);
		treeData.setTreeState(treeState);

		setSelectedTreeNode(facilityOwnerNode);
		current = Integer.toString(OWNER_BULK_ID);

		// handle navigation from owners tab
		if (facilityId != null) {
			operatingStatusCd = null;
			submitSyncFacilitySearch();
		}

		setFacOwnerChange(false);
	}
	
	private void selectGenerateNSRInvoice() {

		NSRInvoiceNode = treeData.getNodeById(nsr_invoice_bo_path);

		TreeStateBase treeState = new TreeStateBase();
		treeState.toggleExpanded(invoice_group_path);
		treeState.setSelected(nsr_invoice_bo_path);
		treeData.setTreeState(treeState);

		setSelectedTreeNode(NSRInvoiceNode);
		current = Integer.toString(NSR_INVOICE_BULK_ID);

		setGenerateNSRInvoice(false);
	}

	public final void setFacilityOwnerNode(TreeNode facilityOwnerNode) {
		this.facilityOwnerNode = facilityOwnerNode;
	}

	public final TreeNode getFacilityOwnerNode() {
		return facilityOwnerNode;
	}

	public final void setFacOwnerChange(boolean facOwnerChange) {
		this.facOwnerChange = facOwnerChange;
	}

	public final boolean isFacOwnerChange() {
		return facOwnerChange;
	}
	
	public final void setGenerateNSRInvoice(boolean generateNSRInvoice) {
		this.generateNSRInvoice = generateNSRInvoice;
	}

	public final boolean isGenerateNSRInvoice() {
		return generateNSRInvoice;
	}
	
	public final void setGenerateNSRInvoiceFlag(boolean generateNSRInvoiceFlag) {
		this.generateNSRInvoiceFlag = generateNSRInvoiceFlag;
	}

	public final boolean isGenerateNSRInvoiceFlag() {
		return generateNSRInvoiceFlag;
	}
	
	public final void setNSRInvoiceNode(TreeNode NSRInvoiceNode) {
		this.NSRInvoiceNode = NSRInvoiceNode;
	}

	public final TreeNode getNSRInvoiceNode() {
		return NSRInvoiceNode;
	}

	public final void setRefreshStr(String rs) {
		fProf.setRefreshStr(rs);
	}

	public final String reset() {
		refineSearch();
		return reset2();
	}

	private final String reset2() { // Dennnis split reset() into 2 functions.
		doLaa = null;
		if (!facOwnerChange) {
			facilityId = null;
		}
		facilityNm = null;

		// new impact search facility attributes
		companyName = null;
		cmpId = null;
		address1 = null;
		permitClassCd = null;
		facilityTypeCd = null;
		operatingStatusCd = OperatingStatusDef.OP;

		// new IMPACT tables
		facilitySyncResultsWrapper.clearWrappedData();
		selectedFacResultsWrapper.clearWrappedData();
		contactTypeResultsWrapper.clearWrappedData();
		selectedCtypeResultsWrapper.clearWrappedData();
		applicationSyncResultsWrapper.clearWrappedData();
		selectedAppResultsWrapper.clearWrappedData();
		permitSyncResultsWrapper.clearWrappedData();
		selectedPermitResultsWrapper.clearWrappedData();
		inspectionSyncResultsWrapper.clearWrappedData();
		fpurgeSearchResultWrapper.clearWrappedData();
	
		
		// IMPACT contact type attributes
		contactId = null;

		startDate = null;
		endDate = null;
		year = null;
		facilityDesc = null;
		facilityRole = null;
		staffId = null;
		doLaa = null;
		city = null;
		county = null;
		zipCode = null;
		permittingClassCd = null;
		issuanceStageCd = null;
		globalStatusCd = null;
		reportCategoryCd = null;
		tvPermitStatus = null;
		operatingStatus = null;
		sicCd = null;
		naicsCd = null;
		inAttainment = false;
		portable = false;
		neshapsInd = false;
		neshapsSubpart = null;
		nspsInd = false;
		nspsSubpart = null;
		perDueDate = null;
		newPerDueDate = null;
		pollutant = null;
		mactInd = false;
		mactCd = null;
		mactSubpart = null;
		transitStatus = null;
		portableGroupType = null;
		portableGroupName = null;
		euDesc = null;
		egressPointTypeCd = null;

		applicationNbr = null;
		permitType = null;
		permitReason = null;
		permitNumber = null;
		permitStatusCd = null;

		correspondenceType = null;
		correspondenceDate = null;
		dateGenerated = null;

		applicationType = null;

		emissionsReportId = null;
		revenueId = null;
		revenueTypeCd = null;
		invoiceStateCd = null;
		revenueStateCd = null;
		
		taskName = null;
		//setFacilities(null);
		inspId = null;

		return CANCELLED;
	}

	private void stopSearch() {
		boolean stoppedSearch = false;
		notReset = false;
		if (fProf != null) {
			fProf.setRefreshStr(" ");
		}

		if (getSearchThread() != null && getSearchThread().isAlive()) {
			try {
				getSearchThread().interrupt();
				getSearchThread().join();
			} catch (Exception e) {
				// Ignore.
			} finally {
				stoppedSearch = true;
				setSearchThread(null);
			}
		} else {
			setSearchThread(null);
		}

		if (bulkOperation != null) {
			bulkOperation.setSearchStarted(false);
			bulkOperation.setSearchCompleted(false);
		}
		
		if(stoppedSearch) {
			DisplayUtil.clearQueuedMessages();
			setInfoMessage("The search operation has been stopped.");
		}

		setBrowserCompleted(false);
		setAsyncRunning(false);
		setShowProgressBar(false);
		setShowProgressBarInvoiceGeneration(false);
	}
	
	public final String refineSearch() {
		stopSearch();

		bulkOpDef = null;

		setBulkFacilitySearch(false);
		setHasFacilitySearchResults(false);
		setHasFacilitySyncSearchResults(false);
		setBulkPermitSearch(false);
		setHasPermitSearchResults(false);
		setBulkInspectionSearch(false);
		setHasInspectionSearchResults(false);
		setBulkCorrespondenceSearch(false);
		setBulkApplicationSearch(false);
		setHasApplicationSearchResults(false);
		setHasApplicationSyncSearchResults(false);
		setBulkEmissionsReportSearch(false);
		setHasEmissionsReportSearchResults(false);
		setBulkInvoiceSearch(false);
		setHasInvoiceSearchResults(false);
		setBulkContactTypeSearch(false);
		setHasContactTypeSearchResults(false);
		setBulkComplianceReportSearch(false);
		setHasComplianceReportSearchResults(false);

		facilities = null;
		contactTypes = null;
		selectedContactTypes = null;
		selectedFacilities = null;
		permits = null;

		taskOperation = false;
		roleOperation = false;
		
		

		getBulkDef();
		return CANCELLED;
	}

	private final void initForSearch() {
		notReset = true;
	}

	public final synchronized Thread getSearchThread() {
		return searchThread;
	}

	public final synchronized void setSearchThread(Thread thread) {
		searchThread = thread;
	}

	public final synchronized boolean isBrowserCompleted() {
		return _browserCompleted;
	}

	public final synchronized void setBrowserCompleted(boolean completed) {

		_browserCompleted = completed;

		if (_browserCompleted) {

			if (getSearchThread() != null && getSearchThread().isAlive()) {
				try {
					getSearchThread().interrupt();
					getSearchThread().join();
				} catch (Exception e) {
					// Ignore.
				} finally {
					setSearchThread(null);
				}
			} else {
				setSearchThread(null);
			}

			bulkOperation.setSearchStarted(false);
		}

	}

	public final boolean isShowProgressBar() {
//		boolean ret = false;
//		if (bulkOperation != null && bulkOperation.isSearchStarted()
//				&& isAsyncRunning()) {
//			if (!bulkOperation.isSearchCompleted()) {
//				ret = true;
//			} else if (bulkOperation.isSearchCompleted()
//					&& !isBrowserCompleted()) {
//				setBrowserCompleted(true);
//				DisplayUtil.displayInfo("Search completed.");
//				ret = false;
//			} else {
//				ret = true;
//			}
//		}
		
		//showProgressBar = ret;
		
		

		return showProgressBar;
	}

	/**
	 * @param showProgressBar
	 *            the showProgressBar to set
	 */
	public final void setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
	}

	public final boolean isShowProgressBarInvoiceGeneration() {

		return showProgressBarInvoiceGeneration;
	}

	/**
	 * @param showProgressBarInvoiceGeneration
	 *            the showProgressBarInvoiceGeneration to set
	 */
	public final void setShowProgressBarInvoiceGeneration(boolean showProgressBar) {
		this.showProgressBarInvoiceGeneration = showProgressBar;
	}

	public final String bulkOperationDone(ActionEvent actionEvent) {
		reset();
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
		return SUCCESS;
	}

	public final String bulkOperationCancel(ActionEvent actionEvent) {
		if(bulkOperation instanceof GenerateBulkFacilityCorrespondence) {
			((GenerateBulkFacilityCorrespondence) bulkOperation).deleteUploadedFile();
		}
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
		return SUCCESS;
	}

	/** ******************************************************************* */
	/* COMMON search attributes begin here. */
	/** ******************************************************************* */

	public final Integer getYear() {
		return year;
	}

	public final void setYear(Integer year) {
		this.year = year;
	}

	public final boolean isYearEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("year");
	}

	public final String getFacilityId() {
		return facilityId;
	}

	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final boolean isFacilityIdEnabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("fcid");
	}

	// ****************************** \\
	// New Facility Search Attributes \\
	// ****************************** \\
	public boolean isSyncSearch() {
		return syncSearch;
	}

	public void setSyncSearch(boolean syncSearch) {
		this.syncSearch = syncSearch;
	}

	public final boolean isSyncSearchEnabled() {
		setSyncSearch(null == bulkOpDef? false :bulkOpDef.isAttributeEnabled("sync"));
		return this.syncSearch;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public final boolean isCompanyNameEnabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("cmpn");
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public String getCmpId() {
		return cmpId;
	}

	public final boolean isCmpIdEnabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("cmpi");
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public final boolean isFacilityAddress1Enabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("far1");
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

	public final boolean isFacilityClassEnabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("fcla");
	}

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public final boolean isFacilityTypeEnabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("ftyp");
	}

	public String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public void setOperatingStatusCd(String operatingStatusCd) {
		this.operatingStatusCd = operatingStatusCd;
	}

	public final boolean isFacilityOpStatusEnabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("fop");
	}

	public final TableSorter getFacilitySyncResultsWrapper() {
		return this.facilitySyncResultsWrapper;
	}

	public final TableSorter getSelectedFacResultsWrapper() {
		return this.selectedFacResultsWrapper;
	}
	
	public final TableSorter getApplicationSyncResultsWrapper() {
		return this.applicationSyncResultsWrapper;
	}

	public final TableSorter getSelectedAppResultsWrapper() {
		return this.selectedAppResultsWrapper;
	}
	
	public final TableSorter getPermitSyncResultsWrapper() {
		return this.permitSyncResultsWrapper;
	}

	public final TableSorter getSelectedPermitResultsWrapper() {
		return this.selectedPermitResultsWrapper;
	}
	
	
	// Contact Type Search
	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public final boolean isContactIdEnabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("cnti");
	}

	public void setContactsList(List<SelectItem> contactsList) {
		this.contactsList = contactsList;
	}

	public List<SelectItem> getContactsList() {
		contacts = new ArrayList<Contact>();
		contactsList = new ArrayList<SelectItem>();

		try {
			contacts = getFacilityService().retrieveAllContacts();
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Accessing global contacts failed");
		}
		
		if (contacts.isEmpty()) {
			DisplayUtil
					.displayError("There are no contacts defined; please cancel this operation and create a global contact.");
		} else {
			SelectItem cont;
			String name;
			for (Contact tempContact : contacts) {
				name = tempContact.getName();

				cont = new SelectItem(tempContact.getContactId(), name);
				contactsList.add(cont);

			}
		}

		return contactsList;
	}

	public ContactType[] getContactTypes() {
		return contactTypes;
	}

	public void setContactTypes(ContactType[] contactTypes) {
		this.contactTypes = contactTypes;
		if (bulkOperation.isSearchStarted()
				|| bulkOperation.isHasSearchResults()) {
			if ((contactTypes == null) || (contactTypes.length == 0)) {
				setHasContactTypeSearchResults(false);
			} else {
				setSelectedContactTypes(contactTypes);
				// synchronous contact type search
				contactTypeResultsWrapper.setWrappedData(this.contactTypes);
				setHasContactTypeSearchResults(true);
			}
		}

	}

	public ContactType[] getSelectedContactTypes() {
		return selectedContactTypes;
	}

	public void setSelectedContactTypes(ContactType[] selectedContactTypes) {
		this.selectedContactTypes = selectedContactTypes;

		if (this.selectedContactTypes == null) {
			return;
		}

		for (ContactType cType : this.selectedContactTypes) {
			cType.setSelected(true);
		}

		selectedCtypeResultsWrapper.setWrappedData(this.selectedContactTypes);

	}

	public final String setSelectedContactTypes() {
		try {
			List<ContactType> cTypes = new ArrayList<ContactType>();

			if (contactTypes != null) {

				for (ContactType cType : contactTypes) {
					if (cType.isSelected()) {
						cTypes.add(cType);
					}
				}

				selectedContactTypes = cTypes.toArray(new ContactType[0]);

				if (selectedContactTypes.length == 0) {
					DisplayUtil
							.displayError("No contact types have been selected.");
					return FAIL;
				}

				bulkOperation.performPreliminaryWork(this);
			}

			selectedCtypeResultsWrapper
					.setWrappedData(this.selectedContactTypes);

			return bulkOperation.getNavigation();
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				DisplayUtil.displayError(e.getMessage());
				logger.error(e.getMessage(), e);
			} else {
				String msg = "System Error: Could not set selected contact types.";
				DisplayUtil.displayError(msg);
				logger.error(msg, e);
			}
			return "FAILURE";
		}

	}

	public final TableSorter getContactTypeResultsWrapper() {
		return this.contactTypeResultsWrapper;
	}

	public TableSorter getSelectedCtypeResultsWrapper() {
		return selectedCtypeResultsWrapper;
	}

	public void setSelectedCtypeResultsWrapper(
			TableSorter selectedCtypeResultsWrapper) {
		this.selectedCtypeResultsWrapper = selectedCtypeResultsWrapper;
	}

	public final boolean isBulkContactTypeSearch() {
		return bulkContactTypeSearch;
	}

	public final void setBulkContactTypeSearch(boolean contactTypeSearch) {
		this.bulkContactTypeSearch = contactTypeSearch;
	}

	public final boolean isHasContactTypeSearchResults() {
		return hasContactTypeSearchResults;
	}

	public final void setHasContactTypeSearchResults(
			boolean hasContactTypeSearchResults) {
		this.hasContactTypeSearchResults = hasContactTypeSearchResults;
	}

	// ************************************* \\
	// End of New Search Attributes (IMPACT) \\
	// ************************************* \\

	public final String getFacilityNm() {
		return facilityNm;
	}

	public final void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}

	public final boolean isFacilityNmEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("fcnm");
	}

	public final Timestamp getStartDate() {
		return startDate;
	}

	public final void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public final boolean isStartDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("stdt");
	}

	public final Timestamp getEndDate() {
		return endDate;
	}

	public final void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public final boolean isEndDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("endt");
	}

	public final boolean isBulkFacilitySearch() {
		return bulkFacilitySearch;
	}

	public final void setBulkFacilitySearch(boolean facilitySearch) {
		bulkFacilitySearch = facilitySearch;
	}

	public final boolean isHasFacilitySearchResults() {
		if (bulkOperation != null && bulkOperation.isSearchCompleted()) {
			setBrowserCompleted(true);
		}
		return hasFacilitySearchResults;
	}

	
	
	public final void setHasFacilitySyncSearchResults(boolean hasSearchResults) {
		hasFacilitySyncSearchResults = hasSearchResults;
	}

	public final boolean isHasFacilitySyncSearchResults() {
		// if (bulkOperation != null && bulkOperation.isSearchCompleted()) {
		// setBrowserCompleted(true);
		// }
		return hasFacilitySyncSearchResults;
	}

	public final void setHasFacilitySearchResults(boolean hasSearchResults) {
		hasFacilitySearchResults = hasSearchResults;
	}

	public final synchronized int getPercentage() {
		return _percentage;
	}

	public final synchronized void setPercentage(int percentage) {
		_percentage = percentage;
	}

	public final synchronized User getUser() {
		return user;
	}

	public final synchronized void setUser(User user) {
		this.user = user;
	}
	
	public final synchronized Integer getCurrentUserId() {
		return currentUserId;
	}

	public final synchronized void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}

	public final synchronized void submitFacilitySearch() {
		if (!isSyncSearchEnabled()) {
			
			if (!bulkOperation.isSearchStarted() && getSearchThread() == null) {
				initForSearch();
				FacesContext facesContext = FacesContext.getCurrentInstance();
				displayUtilBean = DisplayUtil.getSessionInstance(facesContext,
						true);

				String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
						+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
						+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
						+ "<META HTTP-EQUIV=\"Cache-Control\" "
						+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
				fProf = (FacilityProfile) FacesUtil
						.getManagedBean("facilityProfile");
				// fProf.setRefreshStr(refresh);
				allowClose = false;
				setFacilities(null);
				setSelectedFacilities(null);
				setHasFacilitySearchResults(false);
				setShowProgressBar(true);
				
				final BulkOperationsCatalog boc = this;

				try {
					boc.setUser(InfrastructureDefs.getPortalUser());
				} catch (RemoteException re) {
					logger.error(re.getMessage(), re);
				}

				setSearchThread(new Thread("Candidate Facility Search Thread") {

					public void run() {
						try {
							sleep(2000);
							bulkOperation.searchFacilities(boc);
							boc.setSearchThread(null);
						} catch (Exception e) {
							String error = "Search failed: System Error. ";
							logger.error(error, e);
							if (e.getMessage() != null
									&& e.getMessage().length() > 0) {
								error += e.getMessage();
							}
							setErrorMessage(error
									+ " Please contact the System Administrator.");
							bulkOperation.setSearchStarted(false);
							bulkOperation.setSearchCompleted(false);
							fProf.setRefreshStr(" ");
						}
					}
				});

				getSearchThread().setDaemon(true);
				setBrowserCompleted(false);
				bulkOperation.start(searchThread);
				setAsyncRunning(true);

				if (!bulkOperation.isSearchCompleted()) {
					DisplayUtil
							.displayInfo("Examining candidate facilities. This may take several moments. "
									+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
				} else {
					fProf.setRefreshStr(" ");
					setBrowserCompleted(true);
				}

			} else {
				DisplayUtil
						.displayInfo("Still examining candidate facilities. Please wait."
								+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
			}
		} else {
			submitSyncFacilitySearch();
		}

	}

	/**
	 * Submits a synchronous facility search.
	 */
	public final void submitSyncFacilitySearch() {
		// reset prior facilities
		setFacilities(null);
		setSelectedFacilities(null);
		setHasFacilitySyncSearchResults(false);
		allowClose = false;
		final BulkOperationsCatalog boc = this;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		displayUtilBean = DisplayUtil
				.getSessionInstance(facesContext, true);

		try {
			bulkOperation.searchFacilities(boc);
		} catch (Exception e) {
			String error = "Search failed: System Error. ";
			logger.error(error, e);
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				error += e.getMessage();
			} else {
				setErrorMessage(error
						+ " Please contact the System Administrator.");
			}
			bulkOperation.setSearchStarted(false);
			bulkOperation.setSearchCompleted(false);
		}
	}

	/**
	 * Submits a synchronous contact type search.
	 */
	public final void submitContactTypeSearch() {
		// reset prior contact types
		setContactTypes(null);
		setSelectedContactTypes(null);
		setHasContactTypeSearchResults(false);
		allowClose = false;
		final BulkOperationsCatalog boc = this;

		try {
			bulkOperation.searchContactTypes(boc);
		} catch (Exception e) {
			String error = "Search failed: System Error. ";
			logger.error(error, e);
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				error += e.getMessage();
			} else {
				setErrorMessage(error
						+ " Please contact the System Administrator.");
			}
			bulkOperation.setSearchStarted(false);
			bulkOperation.setSearchCompleted(false);
		}
	}

	public String selectAllF() {
		if (facilities != null) {
			for (FacilityList fl : facilities) {
				fl.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneF() {
		if (facilities != null) {
			for (FacilityList fl : facilities) {
				fl.setSelected(false);
			}
		}
		return null;
	}

	public String selectAllCT() {
		if (contactTypes != null) {
			for (ContactType cl : contactTypes) {
				cl.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneCT() {
		if (contactTypes != null) {
			for (ContactType cl : contactTypes) {
				cl.setSelected(false);
			}
		}
		return null;
	}

	public final FacilityList[] getFacilities() {
		if (null != bulkOperation && (bulkOperation.isSearchCompleted()
				|| bulkOperation.isHasSearchResults())) {
			if ((facilities == null) || (facilities.length == 0)) {
				facilities = new FacilityList[0];
			}
			return facilities;
		}

		return new FacilityList[0];
	}

	public final void setFacilities(FacilityList[] facilities) {
		this.facilities = facilities;
		if (null != bulkOperation && (bulkOperation.isSearchStarted()
				|| bulkOperation.isHasSearchResults())) {
			setShowProgressBar(false);
			this.notReset = false;
			if ((facilities == null) || (facilities.length == 0)) {
				setInfoMessage("There are no facilities that match the search criteria.");
				setHasFacilitySearchResults(false);
				setHasCorrSearchResults(false);
				setHasFacilitySyncSearchResults(false);
			} else {
				setSelectedFacilities(facilities);
				if (isSyncSearchEnabled()) {
					// synchronous facility search
					facilitySyncResultsWrapper.setWrappedData(this.facilities);
					setHasFacilitySyncSearchResults(true);
					setHasFacilitySearchResults(false);
				} else {
					if (isBulkFacilitySearch()) {
						setHasFacilitySearchResults(true);
						setHasFacilitySyncSearchResults(false);
					} else if (isBulkCorrespondenceSearch()) {
						setHasCorrSearchResults(true);
					}
				}
			}
		}
	}

	public final FacilityList[] getSelectedFacilities() {
		return selectedFacilities;
	}

	public final void setSelectedFacilities(FacilityList[] selectedFacilities) {
		this.selectedFacilities = selectedFacilities;
		if (this.selectedFacilities == null) {
			return;
		}

		boolean giveMsg = false;
		for (FacilityList facility : this.selectedFacilities) {
			//boolean b = facility.whatIsSelected();
			boolean b = false;
			if (this.isSyncSearch()) {
				b = true;
			}		
			// if (!b) {
			// giveMsg = true;
			// }
			facility.setSelected(b);
		}

		if (giveMsg) {
			String msg = "Facilities are not marked as selected in the table below if a NTV report has been submitted for one of the years.  "
					+ "This is because the primary contact for the missing report may not have been sent a reminder letter.  "
					+ "You should generate late letters for only these selected facilities first.  "
					+ "Then rerun the Late Letter Document Generation to find the remaining facilities.  "
					+ "Finally for each of these facilities, determine whether a first reminder letter has already been sent for the missing report prior to performing the selections for a late letter.";
			setWarnMessage(msg);
		}

		if (this.isSyncSearch()) {
			selectedFacResultsWrapper.setWrappedData(this.selectedFacilities);
		}
	}

	public final String setSelectedFacilities() {

		try {

			List<FacilityList> facs = new ArrayList<FacilityList>();

			if (facilities != null) {

				for (FacilityList facility : facilities) {
					if (facility.isSelected()) {
						facs.add(facility);
					}
				}
				
				selectedFacilities = facs.toArray(new FacilityList[0]);

				if (selectedFacilities.length == 0) {
					DisplayUtil
							.displayError("No facilities have been selected.");
					return FAIL;
				}
				
				if(bulkOperation instanceof GenerateBulkFacilityCorrespondence &&
						selectedFacilities.length > BULK_OPERATION_ADD_CORRESPONDENCE_MAX_LIMIT) {
					DisplayUtil
							.displayWarning("For performance reasons, you are allowed to select a maximum of " +
									BULK_OPERATION_ADD_CORRESPONDENCE_MAX_LIMIT + " facilities. Please select " +
									BULK_OPERATION_ADD_CORRESPONDENCE_MAX_LIMIT + " facilities or less in order " +
									"to continue.");
					return FAIL;
				}

				bulkOperation.performPreliminaryWork(this);
			}

			if (this.isSyncSearch()) {
				selectedFacResultsWrapper
						.setWrappedData(this.selectedFacilities);
			}

			return bulkOperation.getNavigation();
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				DisplayUtil.displayError(e.getMessage());
				String msg = "Preliminary work failed: System Error.";
				DisplayUtil.displayError(msg);
				logger.error(e.getMessage(), e);
			} else {
				String msg = "System Error: Could not set selected facilities.";
				DisplayUtil.displayError(msg);
				logger.error(msg, e);
			}
			return "FAILURE";
		}

	}

	public void correspondence(ActionEvent actionEvent) throws RemoteException {
		allowClose = true;
		bulkOperation.correspondence(actionEvent);
	}

	public final String enableClose() {
		allowClose = true;
		return null;
	}

	public final String applyFinalAction(ActionEvent actionEvent) {

		String outcome = SUCCESS;
		allowClose = true;
		try {
			outcome = bulkOperation.performPostWork(this);
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				DisplayUtil.displayError(e.getMessage());
				logger.error(e.getMessage(), e);
			} else {
				DisplayUtil
						.displayError("Preliminary work failed: System Error.");
				logger.error(e.getMessage(), e);
			}
			return "FAILURE";
		}

		reset();
		FacesUtil.returnFromDialogAndRefresh();
		//AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);

		return outcome;
	}
	
	public final String applyFinalAction2(ActionEvent actionEvent) {

		String outcome = SUCCESS;
		allowClose = true;
		try {
			outcome = bulkOperation.performPostWork(this);
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				DisplayUtil.displayError(e.getMessage());
				logger.error(e.getMessage(), e);
			} else {
				DisplayUtil
						.displayError("Preliminary work failed: System Error.");
				logger.error(e.getMessage(), e);
			}
			return "FAILURE";
		}

		if(outcome != ERROR) {
			reset();
			FacesUtil.returnFromDialogAndRefresh();
		}
		//AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);

		return outcome;
	}

	public final String getFacilityDesc() {
		return facilityDesc;
	}

	public final void setFacilityDesc(String facilityDesc) {
		this.facilityDesc = facilityDesc;
	}

	public final boolean isFacilityDescEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("fcds");
	}

	public final String getFacilityRole() {
		return facilityRole;
	}

	public final void setFacilityRole(String facilityRole) {
		this.facilityRole = facilityRole;
	}

	/**
	 * @return the staffId
	 */
	public final Integer getStaffId() {
		return staffId;
	}

	/**
	 * @param staffId
	 *            the staffId to set
	 */
	public final void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public final List<SelectItem> getFacilityRoles() {
		return FacilityRoleDef.getData().getItems()
				.getItems(facilityRole, true);
	}

	public final boolean isFacilityRoleEnabled() {
		//TODO verify logic
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("fcro");
	}

	public final boolean isStaffIdEnabled() {
		//TODO verify logic
		return  null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("sfid");
	}
	
	public final boolean isFacilityRoleStaffIdEnabled() {
		//TODO verify logic
		return  null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("ufsi");
	}

	public final boolean isTaskNameEnabled() {
		//TODO verify logic
		return  null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("wftn");
	}

	public final String getDoLaa() {
		return doLaa;
	}

	public final void setDoLaa(String doLaa) {
		this.doLaa = doLaa;
	}

	public final List<SelectItem> getDoLaas() {
		return DoLaaDef.getData().getItems().getAllSearchItems();
	}

	public final boolean isDoLaaEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("dola");
	}

	public final String getCity() {
		return city;
	}

	public final void setCity(String city) {
		this.city = city;
	}

	public final boolean isCityEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("city");
	}

	public final String getCounty() {
		return county;
	}

	public final void setCounty(String county) {
		this.county = county;
	}

	public final boolean isCountyEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("cnty");
	}

	public final String getZipCode() {
		return zipCode;
	}

	public final void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public final boolean isZipCodeEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("zipc");
	}

	public final String getPermittingClassCd() {
		return permittingClassCd;
	}

	public final void setPermittingClassCd(String permittingClassCd) {
		this.permittingClassCd = permittingClassCd;
	}

	public final List<SelectItem> getPermittingClassCds() {
		return PermitClassDef.getData().getItems()
				.getItems(getPermittingClassCd(), true);
	}

	public final boolean isPermittingClassCdEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("ptcl");
	}

	public final String getIssuanceStageCd() {
		return issuanceStageCd;
	}

	public final void setIssuanceStageCd(String issuanceStageCd) {
		this.issuanceStageCd = issuanceStageCd;
	}

	public final List<SelectItem> getIssuanceStageCds() {
		return PermitDocIssuanceStageDef.getData().getItems()
				.getItems(issuanceStageCd, true);
	}

	public final boolean isIssuanceStageCdEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("issg");
	}

	public final String getGlobalStatusCd() {
		return globalStatusCd;
	}

	public final void setGlobalStatusCd(String globalStatusCd) {
		this.globalStatusCd = globalStatusCd;
	}

	public final List<SelectItem> getGlobalStatusCds() {
		return PermitGlobalStatusDef.getData().getItems()
				.getItems(globalStatusCd, true);
	}

	public final boolean isGlobalStatusCdEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("isst");
	}

	public final String getReportCategoryCd() {
		return reportCategoryCd;
	}

	public final void setReportCategoryCd(String reportCategoryCd) {
		this.reportCategoryCd = reportCategoryCd;
	}

	public final List<SelectItem> getReportCategoryCds() {
		return getReportingTypes(getReportCategoryCd());
	}

	public final boolean isReportCategoryEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("rptc");
	}

	public final String getTvPermitStatus() {
		return tvPermitStatus;
	}

	public final void setTvPermitStatus(String tvPermitStatus) {
		this.tvPermitStatus = tvPermitStatus;
	}

	public final boolean isTvPermitStatusEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("tvps");
	}

	/** See get/setStartDate for field data. */
	public final boolean isInstallStartDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("insd");
	}

	/** See get/setEndDate for field data. */
	public final boolean isInstallEndDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("ined");
	}

	/** See get/setStartDate for field data. */
	public final boolean isIssuanceStartDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("issd");
	}

	/** See get/setEndDate for field data. */
	public final boolean isIssuanceEndDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("ised");
	}

	/** See get/setStartDate for field data. */
	public final boolean isEffectiveStartDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("efsd");
	}

	/** See get/setEndDate for field data. */
	public final boolean isEffectiveEndDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("efed");
	}

	/** See get/setStartDate for field data. */
	public final boolean isBalanceStartDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("basd");
	}

	/** See get/setEndDate for field data. */
	public final boolean isBalanceEndDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("baed");
	}

	public final String getOperatingStatus() {
		return operatingStatus;
	}

	public final void setOperatingStatus(String operatingStatus) {
		this.operatingStatus = operatingStatus;
	}

	public final List<SelectItem> getOperatingStatuses() {
		return OperatingStatusDef.getData().getItems()
				.getItems(operatingStatus, true);
	}

	public final boolean isOperatingStatusEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("opst");
	}

	public final String getSicCd() {
		return sicCd;
	}

	public final void setSicCd(String sicCd) {
		this.sicCd = sicCd;
	}

	public final List<SelectItem> getSicCds() {
		return SICDef.getData().getItems().getItems(sicCd, true);
	}

	public final boolean isSicCdEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("sicc");
	}

	public final String getNaicsCd() {
		return naicsCd;
	}

	public final void setNaicsCd(String naicsCd) {
		this.naicsCd = naicsCd;
	}

	public final List<SelectItem> getNaicsCds() {
		return NAICSDef.getData().getItems().getItems(naicsCd, true);
	}

	public final boolean isNaicsCdEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("naic");
	}

	public final boolean isInAttainment() {
		return inAttainment;
	}

	public final void setInAttainment(boolean inAttainment) {
		this.inAttainment = inAttainment;
	}

	public final boolean isInAttainmentEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("inat");
	}

	public final boolean isPortable() {
		return portable;
	}

	public final void setPortable(boolean portable) {
		this.portable = portable;
	}

	public final boolean isPortableEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("port");
	}

	public final boolean isNeshapsInd() {
		return neshapsInd;
	}

	public final void setNeshapsInd(boolean neshapsInd) {
		this.neshapsInd = neshapsInd;
	}

	public final boolean isNeshapsIndEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("nesi");
	}

	public final String getNeshapsSubpart() {
		return neshapsSubpart;
	}

	public final void setNeshapsSubpart(String neshapsSubpart) {
		this.neshapsSubpart = neshapsSubpart;
	}

	public final List<SelectItem> getNeshapsSubparts() {
		return PTIONESHAPSSubpartDef.getData().getItems()
				.getItems(neshapsSubpart, true);
	}

	public final boolean isNeshapsSubpartEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("nesa");
	}

	public final boolean isNspsIndEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("nspi");
	}

	public final boolean isNspsInd() {
		return nspsInd;
	}

	public final void setNspsInd(boolean nspsInd) {
		this.nspsInd = nspsInd;
	}

	public final String getNspsSubpart() {
		return nspsSubpart;
	}

	public final void setNspsSubpart(String nspsSubpart) {
		this.nspsSubpart = nspsSubpart;
	}

	public final List<SelectItem> getNspsSubparts() {
		return PTIONSPSSubpartDef.getData().getItems()
				.getItems(nspsSubpart, true);
	}

	public final boolean isNspsSubpartEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("nsps");
	}

	/** Used for search. */
	public final String getPerDueDate() {
		return perDueDate;
	}

	/** Used for search. */
	public final void setPerDueDate(String perDueDate) {
		this.perDueDate = perDueDate;
	}

	/** Used for search. */
	public final boolean isPerDueDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("perd");
	}

	public final List<SelectItem> getPerDueDates() {
		return PTIOPERDueDateDef.getData().getItems()
				.getItems(perDueDate, true);
	}

	/** Used for setting new PER Due Date. */
	public final String getNewPerDueDate() {
		return newPerDueDate;
	}

	/** Used for setting new PER Due Date. */
	public final void setNewPerDueDate(String newPerDueDate) {
		this.newPerDueDate = newPerDueDate;
	}

	/** Used for setting new PER Due Date. */
	public final boolean isNewPerDueDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("nprd");
	}

	/** Used for setting new PER Due Date. */
	public final List<SelectItem> getNewPerDueDates() {
		return PTIOPERDueDateDef.getData().getItems()
				.getItems(newPerDueDate, true);
	}

	public final String getPollutant() {
		return pollutant;
	}

	public final void setPollutant(String pollutant) {
		this.pollutant = pollutant;
	}

	public final List<SelectItem> getPollutants() {
		return PollutantDef.getData().getItems().getItems(pollutant, true);
	}

	public final boolean isPollutantEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("poll");
	}

	public final boolean isMactIndEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("maci");
	}

	public final boolean isMactInd() {
		return mactInd;
	}

	public final void setMactInd(boolean mactInd) {
		this.mactInd = mactInd;
	}

	public final String getMactCd() {
		return mactCd;
	}

	public final void setMactCd(String mactCd) {
		this.mactCd = mactCd;
	}

	public final boolean isMactCdEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("mact");
	}

	public final String getMactSubpart() {
		return mactSubpart;
	}

	public final List<SelectItem> getMactSubparts() {
		return PTIOMACTSubpartDef.getData().getItems()
				.getItems(mactSubpart, true);
	}

	public final void setMactSubpart(String mactSubpart) {
		this.mactSubpart = mactSubpart;
	}

	public final boolean isMactSubpartEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("macs");
	}

	public final String getTransitStatus() {
		return transitStatus;
	}

	public final void setTransitStatus(String transitStatus) {
		this.transitStatus = transitStatus;
	}

	public final List<SelectItem> getTransitStatuses() {
		return TransitStatusDef.getData().getItems()
				.getItems(transitStatus, true);
	}

	public final boolean isTransitStatusEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("tran");
	}

	public final String getPortableGroupType() {
		return portableGroupType;
	}

	public final void setPortableGroupType(String portableGroupType) {
		this.portableGroupType = portableGroupType;
	}

	public final List<SelectItem> getPortableGroupTypes() {
		return PortableGroupTypes.getData().getItems()
				.getItems(portableGroupType, true);
	}

	public final boolean isPortableGroupTypeEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("pogt");
	}

	public final String getPortableGroupName() {
		return portableGroupName;
	}

	public final void setPortableGroupName(String portableGroupName) {
		this.portableGroupName = portableGroupName;
	}

	public final List<SelectItem> getPortableGroupNames() {
		return PortableGroupNames.getData().getItems()
				.getItems(portableGroupName, true);
	}

	public final boolean isPortableGroupNameEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("pogn");
	}

	public final String getEuDesc() {
		return euDesc;
	}

	public final void setEuDesc(String euDesc) {
		this.euDesc = euDesc;
	}

	public final boolean isEuDescEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("euds");
	}

	public final String getEgressPointTypeCd() {
		return egressPointTypeCd;
	}

	public final void setEgressPointTypeCd(String egressPointTypeCd) {
		this.egressPointTypeCd = egressPointTypeCd;
	}

	public final List<SelectItem> getEgressPointTypes() {
		return EgrPointTypeDef.getData().getItems()
				.getItems(getEgressPointTypeCd(), true);
	}

	public final boolean isEgressPointTypeEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("egpt");
	}

	public final boolean isBulkPermitSearch() {
		return bulkPermitSearch;
	}

	public final void setBulkPermitSearch(boolean permitSearch) {
		bulkPermitSearch = permitSearch;
	}

	public final boolean isHasPermitSearchResults() {
		if ((permits == null) || (permits.length == 0)) {
			hasPermitSearchResults = false;
		}
		return hasPermitSearchResults;
	}

	public final void setHasPermitSearchResults(boolean hasSearchResults) {
		hasPermitSearchResults = hasSearchResults;
	}

	public final void submitPermitSearch() {
		if (!isSyncSearchEnabled()) {
		if (!bulkOperation.isSearchStarted() && getSearchThread() == null) {
			initForSearch();

			FacesContext facesContext = FacesContext.getCurrentInstance();
			displayUtilBean = DisplayUtil
					.getSessionInstance(facesContext, true);

			String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
					+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
					+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
					+ "<META HTTP-EQUIV=\"Cache-Control\" "
					+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
			fProf = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fProf.setRefreshStr(refresh);
			allowClose = false;
			setPermits(null);

			final BulkOperationsCatalog boc = this;

			setSearchThread(new Thread("Candidate Permit Search Thread") {

				public void run() {
					try {
						sleep(2000);
						bulkOperation.searchPermits(boc);
						boc.setSearchThread(null);
					} catch (Exception e) {
						String error = "Search failed: System Error. ";
						logger.error(error, e);
						if (e.getMessage() != null
								&& e.getMessage().length() > 0) {
							error += e.getMessage();
						}
						setErrorMessage(error
								+ " Please contact the System Administrator.");
						bulkOperation.setSearchStarted(false);
						bulkOperation.setSearchCompleted(false);
					}
				}
			});

			getSearchThread().setDaemon(true);
			setBrowserCompleted(false);
			bulkOperation.start(searchThread);

			if (!bulkOperation.isSearchCompleted()) {
				DisplayUtil
						.displayInfo("Examining candidate permits. This may take several moments. "
								+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
			} else {
				fProf.setRefreshStr(" ");
				setBrowserCompleted(true);
			}
		} else {
			DisplayUtil
					.displayInfo("Still examining candidate permits. Please wait. "
							+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
		}
		}else{
			submitSyncPermitSearch();
		}

	}
	
	public final void submitSyncPermitSearch() {
		setPermits(null);
		//setSelectedPermits(null);
		setHasPermitSearchResults(false);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		allowClose = false;
		displayUtilBean = DisplayUtil
				.getSessionInstance(facesContext, true);
		

		final BulkOperationsCatalog boc = this;
		try {
			System.out.println("boc: "+boc);
			bulkOperation.searchPermits(boc);
			
		} catch (Exception e) {
			String error = "Search failed: System Error. ";
			logger.error(error, e);
			if (e.getMessage() != null
					&& e.getMessage().length() > 0) {
				error += e.getMessage();
			}
			setErrorMessage(error
					+ " Please contact the System Administrator.");
		
			bulkOperation.setSearchStarted(false);
			bulkOperation.setSearchCompleted(false);
		}

	}

	public final String getApplicationNumber() {
		return applicationNbr;
	}

	public final void setApplicationNumber(String appNumber) {
		applicationNbr = appNumber;
	}

	public final boolean isApplicationNumberEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("appn");
	}

	public final String getPermitNumber() {
		return permitNumber;
	}

	public final void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	public final boolean isPermitNumberEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("pnum");
	}

	public final String getPermitType() {
		return permitType;
	}

	public final void setPermitType(String permitType) {
		this.permitType = permitType;
	}

	public final boolean isPermitTypeEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("ptyp");
	}

	public final String getPermitReason() {
		return permitReason;
	}

	public final void setPermitReason(String permitReason) {
		this.permitReason = permitReason;
	}

	public final boolean isPermitReasonEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("prsn");
	}

	public final String getPermitStatusCd() {
		return permitStatusCd;
	}

	public final void setPermitStatusCd(String permitStatusCd) {
		this.permitStatusCd = permitStatusCd;
	}

	public final boolean isPermitStatusCdEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("pscd");
	}

	public String selectAllP() {
		if (permits != null) {
			for (Permit p : permits) {
				p.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneP() {
		if (permits != null) {
			for (Permit p : permits) {
				p.setSelected(false);
			}
		}
		return null;
	}

	public final Permit[] getPermits() {
		if (bulkOperation.isSearchCompleted()) {
			if ((permits == null) || (permits.length == 0)) {
				permits = new Permit[0];
			}
			return permits;
		}

		return new Permit[0];
	}

	public final void setPermits(Permit[] permits) {
		this.permits = permits;
		if (bulkOperation.isSearchStarted()) {
			this.notReset = false;
			if ((permits == null) || (permits.length == 0)) {
				setInfoMessage("There are no permits that match the search criteria.");
				setHasPermitSearchResults(false);
			} else {
				setSelectedPermits(permits);
				setHasPermitSearchResults(true);
			}
		}
	}

	public final Permit[] getSelectedPermits() {
		return selectedPermits;
	}

	public final void setSelectedPermits(Permit[] selectedPermits) {
		this.selectedPermits = selectedPermits;
		for (Permit permit : this.selectedPermits) {
			permit.setSelected(true);
		}
	}

	public final String setSelectedPermits() {
		List<Permit> pts = new ArrayList<Permit>();
		
		this.setPollRendered(true);

		for (Permit permit : permits) {
			if (permit.isSelected()) {
				pts.add(permit);
			}
		}

		selectedPermits = pts.toArray(new Permit[0]);

		try {
			bulkOperation.performPreliminaryWork(this);
		} catch (Exception e) {
			DisplayUtil.displayError("Preliminary work failed: System Error.");
			logger.error(e.getMessage(), e);
			return "FAILURE";
		}
		
		this.setPollRendered(false);

		return bulkOperation.getNavigation();

	}

	/** ******************************************************************* */
	/* PERMIT search attributes end here. */
	/** ******************************************************************* */

	// SAM W: I believe all of the following methods are yours and should be
	// removed from this class and moved to a new class derived from
	// BulkOperation.
	//
	// -Chuck
	/** ******************************************************************* */
	/* Correspondence search attributes start here. */
	/** ******************************************************************* */

	public final boolean isBulkCorrespondenceSearch() {
		return bulkCorrespondenceSearch;
	}

	public final void setBulkCorrespondenceSearch(boolean correspondenceSearch) {
		bulkCorrespondenceSearch = correspondenceSearch;
	}

	public final String getCorrespondenceType() {
		return correspondenceType;
	}

	public final void setCorrespondenceType(String correspondenceType) {
		this.correspondenceType = correspondenceType;
	}

	public final boolean isCorrespondenceDateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("crdt");
	}

	public final boolean isCorrespondenceSavable() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("crdt")
				&& bulkOpDef.getCorrespondenceTypeCd() != null;
	}

	public final String getCorrespondenceDate() {
		return correspondenceDate;
	}

	public final void setCorrespondenceDate(String correspondenceDate) {
		this.correspondenceDate = correspondenceDate;
	}

	public final Timestamp getDateGenerated() {
		return dateGenerated;
	}

	public final void setDateGenerated(Timestamp dateGenerated) {
		this.dateGenerated = dateGenerated;
	}

	public boolean isHasCorrSearchResults() {
		return hasCorrSearchResults;
	}

	public void setHasCorrSearchResults(boolean hasCorrSearchResults) {
		this.hasCorrSearchResults = hasCorrSearchResults;
	}

	public final void submitCorrespondenceSearch() {

		if (!bulkOperation.isSearchStarted() && getSearchThread() == null) {
			initForSearch();

			FacesContext facesContext = FacesContext.getCurrentInstance();
			displayUtilBean = DisplayUtil
					.getSessionInstance(facesContext, true);

			String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
					+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
					+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
					+ "<META HTTP-EQUIV=\"Cache-Control\" "
					+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
			fProf = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fProf.setRefreshStr(refresh);
			allowClose = false;
			setFacilities(null);
			setSelectedFacilities(null);

			final BulkOperationsCatalog boc = this;

			setSearchThread(new Thread("Candidate Correspondence Search Thread") {

				public void run() {
					try {
						sleep(2000);
						bulkOperation.searchFacilities(boc);
						boc.setSearchThread(null);
					} catch (Exception e) {
						String error = "Search failed: System Error. ";
						logger.error(error, e);
						if (e.getMessage() != null
								&& e.getMessage().length() > 0) {
							error += e.getMessage();
						}
						setErrorMessage(error
								+ " Please contact the System Administrator.");
						bulkOperation.setSearchStarted(false);
						bulkOperation.setSearchCompleted(false);
					}
				}
			});

			getSearchThread().setDaemon(true);
			setBrowserCompleted(false);
			bulkOperation.start(searchThread);

			if (!bulkOperation.isSearchCompleted()) {
				DisplayUtil
						.displayInfo("Examining candidate facilities. This may take several moments. "
								+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
			} else {
				fProf.setRefreshStr(" ");
				setBrowserCompleted(true);
			}
		} else {
			DisplayUtil
					.displayInfo("Still examining candidate facilities. Please wait."
							+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
		}

	}

	/** ******************************************************************* */
	/* Correspondence search attributes end here. */
	/** ******************************************************************* */

	/************************************************************************/
	/* Application search attributes start here. */
	/************************************************************************/

	public final String getApplicationType() {
		return applicationType;
	}

	public final void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public final boolean isApplicationTypeEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("atyp");
	}

	public final boolean isBulkApplicationSearch() {
		return bulkApplicationSearch;
	}

	public final void setBulkApplicationSearch(boolean applicationSearch) {
		bulkApplicationSearch = applicationSearch;
	}

	public final boolean isHasApplicationSearchResults() {
		return hasApplicationSearchResults;
	}

	public final void setHasApplicationSearchResults(boolean hasSearchResults) {
		hasApplicationSearchResults = hasSearchResults;
	}
	
	public final void setHasApplicationSyncSearchResults(boolean hasSearchResults) {
		hasApplicationSyncSearchResults = hasSearchResults;
	}
	
	public final void setHasEmissionReportsSyncSearchResults(boolean hasSearchResults) {
		hasEmissionReportsSyncSearchResults = hasSearchResults;
	}

	public final boolean isHasApplicationSyncSearchResults() {
		// if (bulkOperation != null && bulkOperation.isSearchCompleted()) {
		// setBrowserCompleted(true);
		// }
		return hasApplicationSyncSearchResults;
	}
	
	public final void setHasPermitSyncSearchResults(boolean hasSearchResults) {
		hasPermitSyncSearchResults = hasSearchResults;
	}

	public final boolean isHasPermitSyncSearchResults() {
		// if (bulkOperation != null && bulkOperation.isSearchCompleted()) {
		// setBrowserCompleted(true);
		// }
		return hasPermitSyncSearchResults;
	}

	public final void submitApplicationSearch() {
		if (!isSyncSearchEnabled()) {
		if (!bulkOperation.isSearchStarted() && getSearchThread() == null) {
			initForSearch();

			FacesContext facesContext = FacesContext.getCurrentInstance();
			displayUtilBean = DisplayUtil
					.getSessionInstance(facesContext, true);

			String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
					+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
					+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
					+ "<META HTTP-EQUIV=\"Cache-Control\" "
					+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
			fProf = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fProf.setRefreshStr(refresh);
			allowClose = false;
			setApplications(null);
			setSelectedApplications(null);

			final BulkOperationsCatalog boc = this;

			setSearchThread(new Thread("Candidate Application Search Thread") {

				public void run() {
					try {
						sleep(2000);
						bulkOperation.searchApplications(boc);
						boc.setSearchThread(null);
					} catch (Exception e) {
						String error = "Search failed: System Error. ";
						logger.error(error, e);
						if (e.getMessage() != null
								&& e.getMessage().length() > 0) {
							error += e.getMessage();
						}
						setErrorMessage(error
								+ " Please contact the System Administrator.");
						bulkOperation.setSearchStarted(false);
						bulkOperation.setSearchCompleted(false);
					}
				}
			});

			getSearchThread().setDaemon(true);
			setBrowserCompleted(false);
			bulkOperation.start(searchThread);

			if (!bulkOperation.isSearchCompleted()) {
				DisplayUtil
						.displayInfo("Examining candidate applications. This may take several moments. "
								+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
			} else {
				fProf.setRefreshStr(" ");
				setBrowserCompleted(true);
			}
		} else {
			DisplayUtil
					.displayInfo("Still examining candidate applications. Please wait."
							+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
		}
		}else{
			submitSyncApplicationSearch();
		}
	}
	
	public final void submitSyncApplicationSearch() {
		setApplications(null);
		setSelectedApplications(null);
		setHasApplicationSearchResults(false);
		setHasApplicationSyncSearchResults(false);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		allowClose = false;
		displayUtilBean = DisplayUtil
				.getSessionInstance(facesContext, true);
		

		final BulkOperationsCatalog boc = this;
		try {
			System.out.println("boc: "+boc);
			bulkOperation.searchApplications(boc);
			
		} catch (Exception e) {
			String error = "Search failed: System Error. ";
			logger.error(error, e);
			if (e.getMessage() != null
					&& e.getMessage().length() > 0) {
				error += e.getMessage();
			}
			setErrorMessage(error
					+ " Please contact the System Administrator.");
		
			bulkOperation.setSearchStarted(false);
			bulkOperation.setSearchCompleted(false);
		}
		
		this.notReset = false;

	}

	public String selectAllA() {
		if (applications != null) {
			for (ApplicationSearchResult asr : applications) {
				asr.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneA() {
		if (applications != null) {
			for (ApplicationSearchResult asr : applications) {
				asr.setSelected(false);
			}
		}
		return null;
	}

	public final ApplicationSearchResult[] getApplications() {
		if (bulkOperation.isSearchCompleted()) {
			if ((applications == null) || (applications.length == 0)) {
				applications = new ApplicationSearchResult[0];
			}
			return applications;
		}

		return new ApplicationSearchResult[0];
	}

	public final void setApplications(ApplicationSearchResult[] applications) {
		this.applications = applications;
		if (null != bulkOperation && (bulkOperation.isSearchStarted() || bulkOperation.isHasSearchResults())) {
			this.notReset = false;
			if ((applications == null) || (applications.length == 0)) {
				//setInfoMessage("There are no applications that match the search criteria.");
				setHasApplicationSearchResults(false);
				setHasCorrSearchResults(false);
				setHasApplicationSyncSearchResults(false);
				
				//setHasApplicationSearchResults(false);
			} else {
				//setHasApplicationSearchResults(true);
				setSelectedApplications(applications);
				if (isSyncSearchEnabled()) {
					// synchronous application search
					applicationSyncResultsWrapper.setWrappedData(this.applications);
					setHasApplicationSyncSearchResults(true);
				} else {
					if (isBulkApplicationSearch()) {
						setHasApplicationSearchResults(true);
					} else if (isBulkCorrespondenceSearch()) {
						setHasCorrSearchResults(true);						
					}
				}
			}
		}
	}

	public final ApplicationSearchResult[] getSelectedApplications() {
		return selectedApplications;
	}

	public final void setSelectedApplications(
			ApplicationSearchResult[] selectedApplications) {

		this.selectedApplications = selectedApplications;
		
		if (this.selectedApplications == null) {
			return;
		}

		if (this.selectedApplications != null) {
			for (ApplicationSearchResult application : this.selectedApplications) {
				application.setSelected(true);
			}
		}
		if (this.isSyncSearch()) {
			selectedAppResultsWrapper.setWrappedData(this.selectedApplications);
		}

	}

	public final String setSelectedApplications() {

		List<ApplicationSearchResult> apps = new ArrayList<ApplicationSearchResult>();

		if (applications != null) {

			for (ApplicationSearchResult application : applications) {
				if (application.isSelected()) {
					apps.add(application);
				}
			}

			selectedApplications = apps.toArray(new ApplicationSearchResult[0]);

			try {
				bulkOperation.performPreliminaryWork(this);
				if (this.isSyncSearch()) {
					selectedAppResultsWrapper
							.setWrappedData(this.selectedApplications);
				}
			} catch (Exception e) {
				DisplayUtil
						.displayError("Preliminary work failed: System Error.");
				logger.error(e.getMessage(), e);
				return "FAILURE";
			}
		}

		return bulkOperation.getNavigation();

	}

	/************************************************************************/
	/* Application search attributes end here. */
	/************************************************************************/

	/************************************************************************/
	/* Emissions Inventory search attributes start here. */
	/************************************************************************/

	public final Integer getEmissionsReportNumber() {
		return emissionsReportId;
	}

	public final void setEmissionsReportNumber(Integer emissionsReportId) {
		this.emissionsReportId = emissionsReportId;
	}

	public final boolean isEmissionsReportNumberEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("erid");
	}

	public final boolean isBulkEmissionsReportSearch() {
		return bulkEmissionsReportSearch;
	}

	public final void setBulkEmissionsReportSearch(boolean emissionsReportSearch) {
		bulkEmissionsReportSearch = emissionsReportSearch;
	}

	public final boolean isHasEmissionsReportSearchResults() {
		return hasEmissionsReportSearchResults;
	}

	public final void setHasEmissionsReportSearchResults(
			boolean hasSearchResults) {
		hasEmissionsReportSearchResults = hasSearchResults;
	}

	public final void submitEmissionsReportSearch() {
		if (isSyncSearchEnabled()) {
		allowClose = false;
		emissionsReports = null;
		selectedEmissionsReports = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		displayUtilBean = DisplayUtil.getSessionInstance(facesContext, true);
		fProf = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");

		try {
			//initForSearch();
			emissionsReports = bulkOperation.searchEmissionsReports(this);

			if ((emissionsReports == null) || (emissionsReports.length == 0)) {
				DisplayUtil
						.displayInfo("Cannot find any emissions inventories for this search");
			} else {
				for (EmissionsReportSearch ers : emissionsReports) {
					ers.setSelected(true);
				}

				hasEmissionsReportSearchResults = true;
				selectedEmissionsReports = emissionsReports;
			}
			this.notReset = false;
		} catch (RemoteException re) {
			DisplayUtil.displayError("Search failed: System Error");
			logger.error(re.getMessage(), re);
			// TODO consider creating a thread to run this search.
		}
		}
	}
	
	public final void submitSyncEmissionReportsSearch() {
		emissionsReports = null;
		selectedEmissionsReports = null;
		setHasEmissionsReportSearchResults(false);
		allowClose = false;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		displayUtilBean = DisplayUtil
				.getSessionInstance(facesContext, true);
		

		final BulkOperationsCatalog boc = this;
		try {
			System.out.println("boc: "+boc);
			bulkOperation.searchApplications(boc);
			
		} catch (Exception e) {
			String error = "Search failed: System Error. ";
			logger.error(error, e);
			if (e.getMessage() != null
					&& e.getMessage().length() > 0) {
				error += e.getMessage();
			}
			setErrorMessage(error
					+ " Please contact the System Administrator.");
		
			bulkOperation.setSearchStarted(false);
			bulkOperation.setSearchCompleted(false);
		}

	}

	public String selectAllE() {
		if (emissionsReports != null) {
			for (EmissionsReportSearch ers : emissionsReports) {
				ers.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneE() {
		if (emissionsReports != null) {
			for (EmissionsReportSearch ers : emissionsReports) {
				ers.setSelected(false);
			}
		}
		return null;
	}

	public final EmissionsReportSearch[] getEmissionsReports() {
		return emissionsReports;
	}

	public final void setEmissionsReport(
			EmissionsReportSearch[] emissionsReports) {
		this.emissionsReports = emissionsReports;
	}

	public final EmissionsReportSearch[] getSelectedEmissionsReports() {
		return selectedEmissionsReports;
	}

	public final void setSelectedEmissionsReports(
			EmissionsReportSearch[] selectedEmissionsReports) {
		this.selectedEmissionsReports = selectedEmissionsReports;
	}

	public final String setSelectedEmissionsReports() {

		List<EmissionsReportSearch> ers = new ArrayList<EmissionsReportSearch>();

		for (EmissionsReportSearch er : emissionsReports) {
			if (er.isSelected()) {
				ers.add(er);
			}
		}

		selectedEmissionsReports = ers.toArray(new EmissionsReportSearch[0]);

		try {
			bulkOperation.performPreliminaryWork(this);
		} catch (Exception e) {
			DisplayUtil.displayError("Preliminary work failed: System Error.");
			logger.error(e.getMessage(), e);
			return "FAILURE";
		}

		return bulkOperation.getNavigation();

	}

	/************************************************************************/
	/* Emissions Inventory search attributes end here. */
	/************************************************************************/

	/************************************************************************/
	/* Compliance Certificate search attributes start here. */
	/************************************************************************/

	public final boolean isBulkComplianceReportSearch() {
		return bulkComplianceReportSearch;
	}

	public final void setBulkComplianceReportSearch(
			boolean complianceReportSearch) {
		bulkComplianceReportSearch = complianceReportSearch;
	}

	public final boolean isHasComplianceReportSearchResults() {
		return hasComplianceReportSearchResults;
	}

	public final void setHasComplianceReportSearchResults(
			boolean hasSearchResults) {
		hasComplianceReportSearchResults = hasSearchResults;
	}

	public final void submitComplianceReportSearch() {
		if (isSyncSearchEnabled()) {
			setHasComplianceReportSearchResults(false);
		allowClose = false;
		complianceReports = null;
		selectedComplianceReports = null;

		try {
			//initForSearch();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			displayUtilBean = DisplayUtil
					.getSessionInstance(facesContext, true);

//			String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
//					+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
//					+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
//					+ "<META HTTP-EQUIV=\"Cache-Control\" "
//					+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
//			fProf = (FacilityProfile) FacesUtil
//					.getManagedBean("facilityProfile");
//			fProf.setRefreshStr(refresh);
//			

			complianceReports = bulkOperation.searchComplianceReports(this);

			if ((complianceReports == null) || (complianceReports.length == 0)) {
				DisplayUtil
						.displayInfo("Cannot find any compliance reports for this search");
			} else {
				for (ComplianceReportList crl : complianceReports) {
					crl.setSelected(true);
				}

				setHasComplianceReportSearchResults(true);
				selectedComplianceReports = complianceReports;
			}
			this.notReset = false;
		} catch (RemoteException re) {
			DisplayUtil.displayError("Search failed: System Error");
			logger.error(re.getMessage(), re);
			// TODO consider creating a thread to run this search.
		}
		}
	}

	public String selectAllCR() {
		if (complianceReports != null) {
			for (ComplianceReportList cr : complianceReports) {
				cr.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneCR() {
		if (complianceReports != null) {
			for (ComplianceReportList cr : complianceReports) {
				cr.setSelected(false);
			}
		}
		return null;
	}

	public final ComplianceReportList[] getComplianceReports() {
		return complianceReports;
	}

	public final void setComplianceReport(
			ComplianceReportList[] complianceReports) {
		this.complianceReports = complianceReports;
	}

	public final ComplianceReportList[] getSelectedComplianceReports() {
		return selectedComplianceReports;
	}

	public final void setSelectedComplianceReports(
			ComplianceReportList[] selectedComplianceReports) {
		this.selectedComplianceReports = selectedComplianceReports;
	}

	public final String setSelectedComplianceReports() {

		List<ComplianceReportList> crls = new ArrayList<ComplianceReportList>();

		for (ComplianceReportList crl : complianceReports) {
			if (crl.isSelected()) {
				crls.add(crl);
			}
		}

		selectedComplianceReports = crls.toArray(new ComplianceReportList[0]);

		try {
			bulkOperation.performPreliminaryWork(this);
		} catch (Exception e) {
			DisplayUtil.displayError("Preliminary work failed: System Error.");
			logger.error(e.getMessage(), e);
			return "FAILURE";
		}

		return bulkOperation.getNavigation();

	}

	/************************************************************************/
	/* Compliance Certificate search attributes end here. */
	/************************************************************************/

	/************************************************************************/
	/* Invoice search attributes start here. */
	/************************************************************************/
	public final boolean isRevenueIdEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("rvid");
	}

	public final boolean isRevenueTypeEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("rvtp");
	}

	public final boolean isInvoiceStateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("ivst");
	}

	public final boolean isRevenueStateEnabled() {
		return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("rvst");
	}

	public final List<SelectItem> getRevenueTypes() {
		return RevenueTypeDef.getData().getItems()
				.getItems(getRevenueTypeCd(), true);
	}

	public final List<SelectItem> getInvoiceStates() {
		List<SelectItem> list = InvoiceState.getData().getItems().getAllItems();
		ArrayList<SelectItem> newList = new ArrayList<SelectItem>(list.size());
		for (SelectItem l : list) {
			if (!l.getValue().equals(InvoiceState.READY_TO_INVOICE)) {
				// Documents are not generated for invoices which is not posted
				// to revenues,
				// so remove Ready_To_Invoice state from search.
				newList.add(l);
			}
		}
		return newList;
	}

	public final List<SelectItem> getRevenueStates() {
		return RevenueState.getData().getItems()
				.getItems(getRevenueStateCd(), true);
	}

	public String getInvoiceStateCd() {
		return invoiceStateCd;
	}

	public void setInvoiceStateCd(String invoiceStateCd) {
		this.invoiceStateCd = invoiceStateCd;
	}

	public Integer getRevenueId() {
		return revenueId;
	}

	public void setRevenueId(Integer revenueId) {
		this.revenueId = revenueId;
	}

	public String getRevenueStateCd() {
		return revenueStateCd;
	}

	public void setRevenueStateCd(String revenueStateCd) {
		this.revenueStateCd = revenueStateCd;
	}

	public String getRevenueTypeCd() {
		return revenueTypeCd;
	}

	public void setRevenueTypeCd(String revenueTypeCd) {
		this.revenueTypeCd = revenueTypeCd;
	}

	public boolean isBulkInvoiceSearch() {
		return bulkInvoiceSearch;
	}

	public void setBulkInvoiceSearch(boolean bulkInvoiceSearch) {
		this.bulkInvoiceSearch = bulkInvoiceSearch;
	}

	public boolean isHasInvoiceSearchResults() {
		return hasInvoiceSearchResults;
	}

	public void setHasInvoiceSearchResults(boolean hasInvoiceSearchResults) {
		this.hasInvoiceSearchResults = hasInvoiceSearchResults;
	}

	public String selectAllI() {
		if (invoices != null) {
			for (InvoiceList il : invoices) {
				il.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneI() {
		if (invoices != null) {
			for (InvoiceList il : invoices) {
				il.setSelected(false);
			}
		}
		return null;
	}

	public InvoiceList[] getInvoices() {
		return invoices;
	}

	public void setInvoices(InvoiceList[] invoices) {
		this.invoices = invoices;
		if (bulkOperation.isSearchStarted()) {
			if ((invoices == null) || (invoices.length == 0)) {
				setInfoMessage("There are no invoices that match the search criteria.");
				setHasInvoiceSearchResults(false);
			} else if (invoices.length > 20000) {
				setInfoMessage("Due to Revenues system limitations, the system cannot return the more than 20,000 invoice records. "
						+ "Please narrow your search and try again ");
				setHasInvoiceSearchResults(false);
			} else {
				setSelectedInvoices(invoices);
				setHasInvoiceSearchResults(true);
			}
		}
	}

	public InvoiceList[] getSelectedInvoices() {
		return selectedInvoices;
	}

	public void setSelectedInvoices(InvoiceList[] selectedInvoices) {
		this.selectedInvoices = selectedInvoices;

		if (this.selectedInvoices != null) {
			for (InvoiceList invoice : this.selectedInvoices) {
				invoice.setSelected(true);
			}
		}
	}

	public final String setSelectedInvoices() {
		List<InvoiceList> invs = new ArrayList<InvoiceList>();

		if (invoices != null) {

			for (InvoiceList invoice : invoices) {
				if (invoice.isSelected()) {
					invs.add(invoice);
				}
			}

			selectedInvoices = invs.toArray(new InvoiceList[0]);

			try {
				bulkOperation.performPreliminaryWork(this);
			} catch (Exception e) {
				DisplayUtil
						.displayError("Preliminary work failed: System Error.");
				logger.error(e.getMessage(), e);
			}
		}

		return bulkOperation.getNavigation();
	}

	public final void submitInvoiceSearch() {

		if (!bulkOperation.isSearchStarted() && getSearchThread() == null) {

			FacesContext facesContext = FacesContext.getCurrentInstance();
			displayUtilBean = DisplayUtil
					.getSessionInstance(facesContext, true);

			if (getRevenueStateCd() != null && getStartDate() == null) {
				DisplayUtil.displayError("Select Start date.");
				return;
			}
			initForSearch();
			String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
					+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
					+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
					+ "<META HTTP-EQUIV=\"Cache-Control\" "
					+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
			fProf = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fProf.setRefreshStr(refresh);
			allowClose = false;
			setInvoices(null);
			setSelectedInvoices(null);

			final BulkOperationsCatalog boc = this;

			try {
				boc.setUser(InfrastructureDefs.getPortalUser());
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
			}

			setSearchThread(new Thread("Candidate Invoice Search Thread") {

				public void run() {
					try {
						sleep(2000);
						bulkOperation.searchInvoices(boc);
						boc.setSearchThread(null);
					} catch (Exception e) {
						String error = "Search failed: System Error. ";
						logger.error(error, e);
						if (e.getMessage() != null
								&& e.getMessage().length() > 0) {
							error += e.getMessage();
						}
						setErrorMessage(error
								+ " Please contact the System Administrator.");
						bulkOperation.setSearchStarted(false);
						bulkOperation.setSearchCompleted(false);
					}
				}
			});

			getSearchThread().setDaemon(true);
			setBrowserCompleted(false);
			bulkOperation.start(searchThread);

			if (!bulkOperation.isSearchCompleted()) {
				DisplayUtil
						.displayInfo("Examining candidate invoices. This may take several moments. "
								+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
			} else {
				fProf.setRefreshStr(" ");
				setBrowserCompleted(true);
			}
		} else {
			DisplayUtil
					.displayInfo("Still examining candidate invoices. Please wait."
							+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
		}

	}

	public final boolean isTaskOperation() {
		return taskOperation;
	}

	public final void setTaskOperation(boolean taskOperation) {
		this.taskOperation = taskOperation;
	}

	public final boolean isRoleOperation() {
		return roleOperation;
	}

	public final void setRoleOperation(boolean roleOperation) {
		this.roleOperation = roleOperation;
	}

	public boolean isAllowClose() {
		return allowClose;
	}

	public boolean isNotReset() {
		return notReset;
	}

	public final String getTaskName() {
		return taskName;
	}

	public final void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/************************************************************************/
	/* Invoice search attributes end here. */
	/************************************************************************/

	public final boolean isFacilityRolesOnly() {
		return "Facility Operations".equals(bulkOpDef.getGroupNm());
	}

	public final List<SelectItem> getFacilityOnlyRoles() {
		return FacilityOnlyRoleDef.getData().getItems()
				.getItems(facilityRole, true);
	}

	public boolean isAsyncRunning() {
		boolean ret = this.isAsyncRunning;
		
		if(isBrowserCompleted()) {
			setAsyncRunning(false);
		}

		return ret;
	}

	public void setAsyncRunning(boolean isAsyncRunning) {
		this.isAsyncRunning = isAsyncRunning;
	}

	public boolean isHasInspectionSyncSearchResults() {
		return hasInspectionSyncSearchResults;
	}

	public void setHasInspectionSyncSearchResults(
			boolean hasInspectionSyncSearchResults) {
		this.hasInspectionSyncSearchResults = hasInspectionSyncSearchResults;
	}

	public boolean isBulkInspectionSearch() {
		return bulkInspectionSearch;
	}

	public void setBulkInspectionSearch(boolean bulkInspectionSearch) {
		this.bulkInspectionSearch = bulkInspectionSearch;
	}

	public boolean isHasInspectionSearchResults() {
		if ((inspections == null) || inspections.length == 0) {
			hasInspectionSearchResults = false;
		}
		return hasInspectionSearchResults;
	}

	public void setHasInspectionSearchResults(boolean hasInspectionSearchResults) {
		this.hasInspectionSearchResults = hasInspectionSearchResults;
	}
	
	public final FullComplianceEval[] getInspections() {
		if (bulkOperation.isSearchCompleted()) {
			if ((inspections == null) || (inspections.length == 0)) {
				inspections = new FullComplianceEval[0];
			}
			return inspections;
		}

		return new FullComplianceEval[0];
	}

	public final void setInspections(FullComplianceEval[] inspections) {
		this.inspections = inspections;
		if (bulkOperation.isSearchStarted()) {
			this.notReset = false;
			if ((inspections == null) || (inspections.length == 0)) {
				setInfoMessage("There are no Inspection that match the search criteria.");
				setHasInspectionSearchResults(false);
			} else {
				setSelectedInspections(inspections);
				setHasInspectionSearchResults(true);
			}
		}
	}
	
	public final FullComplianceEval[] getSelectedInspections() {
		return selectedInspections;
	}

	public final void setSelectedInspections(FullComplianceEval[] selectedInspections) {
		this.selectedInspections = selectedInspections;
		for (FullComplianceEval selectedInspection : this.selectedInspections) {
			selectedInspection.setSelected(true);
		}
	}

	public final String setSelectedInspections() {
		List<FullComplianceEval> insp = new ArrayList<FullComplianceEval>();

		for (FullComplianceEval inspection : inspections) {
			if (inspection.isSelected()) {
				insp.add(inspection);
			}
		}

		selectedInspections = insp.toArray(new FullComplianceEval[0]);

		try {
			bulkOperation.performPreliminaryWork(this);
		} catch (Exception e) {
			DisplayUtil.displayError("Preliminary work failed: System Error.");
			logger.error(e.getMessage(), e);
			return "FAILURE";
		}

		return bulkOperation.getNavigation();

}


public final TableSorter getInspectionSyncResultsWrapper() {
		return this.inspectionSyncResultsWrapper;
}


public final TableSorter getSelectedInspectionesultsWrapper() {
		return this.selectedInspectionResultsWrapper;
}

public final void submitInspectionSearch() {
	if (!isSyncSearchEnabled()) {
		if (!bulkOperation.isSearchStarted() && getSearchThread() == null) {
			initForSearch();

			FacesContext facesContext = FacesContext.getCurrentInstance();
			displayUtilBean = DisplayUtil
					.getSessionInstance(facesContext, true);

			String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
					+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
					+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
					+ "<META HTTP-EQUIV=\"Cache-Control\" "
					+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
			fProf = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			fProf.setRefreshStr(refresh);
			allowClose = false;
			setInspections(null);

			final BulkOperationsCatalog boc = this;

			setSearchThread(new Thread("Candidate Inspection Search Thread") {

				public void run() {
					try {
						sleep(2000);
						bulkOperation.searchInspections(boc);
						boc.setSearchThread(null);
					} catch (Exception e) {
						String error = "Search failed: System Error. ";
						logger.error(error, e);
						if (e.getMessage() != null
								&& e.getMessage().length() > 0) {
							error += e.getMessage();
						}
						setErrorMessage(error
								+ " Please contact the System Administrator.");
						bulkOperation.setSearchStarted(false);
						bulkOperation.setSearchCompleted(false);
					}
				}
			});

			getSearchThread().setDaemon(true);
			setBrowserCompleted(false);
			bulkOperation.start(searchThread);
			setAsyncRunning(true);

			if (!bulkOperation.isSearchCompleted()) {
				DisplayUtil
				.displayInfo("Examining candidate inspections. This may take several moments. "
						+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
			} else {
				fProf.setRefreshStr(" ");
				setBrowserCompleted(true);
			}
		} else {
			DisplayUtil
			.displayInfo("Still examining candidate inspections. Please wait. "
					+ "You may cancel the operation by pressing the \"Stop Search\" or \"Reset\" button.");
		}
	}else{
		submitSyncInspectionSearch();
	}

}

public final void submitSyncInspectionSearch() {
	setInspections(null);
	setHasInspectionSearchResults(false);
	FacesContext facesContext = FacesContext.getCurrentInstance();
	displayUtilBean = DisplayUtil
			.getSessionInstance(facesContext, true);
	

	final BulkOperationsCatalog boc = this;
	try {
		System.out.println("boc: "+boc);
		bulkOperation.searchInspections(boc);
		allowClose = false;
		
	} catch (Exception e) {
		String error = "Search failed: System Error. ";
		logger.error(error, e);
		if (e.getMessage() != null
				&& e.getMessage().length() > 0) {
			error += e.getMessage();
		}
		setErrorMessage(error
				+ " Please contact the System Administrator.");
	
		bulkOperation.setSearchStarted(false);
		bulkOperation.setSearchCompleted(false);
	}

}
public String selectAllInsp() {
	if (inspections != null) {
		for (FullComplianceEval insp : inspections) {
			insp.setSelected(true);
		}
	}
	return null;
}

public String selectNoneInsp() {
	if (inspections != null) {
		for (FullComplianceEval insp : inspections) {
			insp.setSelected(false);
		}
	}
	return null;
}

public String getInspId() {
	return inspId;
}

public void setInspId(String inspId) {
	this.inspId = inspId;
}

public final boolean isInspIdEnabled() {
	return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("insi");
}

public final boolean getNSRFeeSummaryEditAllowed() {
    return InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()
    		|| InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("editNSRFeeSummary");
}

public final boolean isNSRInvoiceExplanationEnabled() {
	return null == bulkOpDef? false : bulkOpDef.isAttributeEnabled("expl");
}

public boolean isPollRendered() {
	return pollRendered;
}

public void setPollRendered(boolean pollRendered) {
	this.pollRendered = pollRendered;
}

public void refreshInvoiceProgress(PollEvent pollevent) {
	if (pollRendered) {
		pollevent.getComponent().findComponent("generate").setRendered(false);		
	} else {
		pollevent.getComponent().findComponent("generate").setRendered(true);
	}
}



	public boolean isPurgeFacility() {
		return purgeFacility;
	}
	
	public void setPurgeFacility(boolean purgeFacility) {
		this.purgeFacility = purgeFacility;
	}

	public boolean isBulkFacilityPurgeSearch() {
		return bulkFacilityPurgeSearch;
	}

	public void setBulkFacilityPurgeSearch(boolean bulkFacilityPurgeSearch) {
		this.bulkFacilityPurgeSearch = bulkFacilityPurgeSearch;
	}

	public List<FacilityPurgeSearchLineItem> getPurdgeCandidates() {
		return purdgeCandidates;
	}
	
	public void setPurdgeCandidates(List<FacilityPurgeSearchLineItem> purdgeCandidates) {
		this.purdgeCandidates = purdgeCandidates;
	}
	
	public List<FacilityPurgeSearchLineItem> getSelectedPurgeCandidates() {
		return selectedPurgeCandidates;
	}

	public void setSelectedPurgeCandidates(List<FacilityPurgeSearchLineItem> selectedPurgeCandidates) {
		this.selectedPurgeCandidates = selectedPurgeCandidates;
	}
	
	public TableSorter getFpurgeSearchResultWrapper() {
		return fpurgeSearchResultWrapper;
	}

	public void setFpurgeSearchResultWrapper(TableSorter fpurgeSearchResultWrapper) {
		this.fpurgeSearchResultWrapper = fpurgeSearchResultWrapper;
	}
	
	public final String setBulkOpToFPUR() {
		//if treeData is null, retrieve all bulkDefs and rebuild nodes and tree for the bulkMenu, set selectedTreeNode to root node or certain node
		//this also set bulkOpDef and initialize bulkOperation
		treeData = null;
		bulkMenu = "fpur";
		bulkOpDef = null;
		setPurgeFacility(true);		
		getTreeData(); 	
		reset(); 		
		//retrieve Purge Candidates;
		submitFacilitPurgeSearch();
		return "admin.facilityPurge";
	}

	private void selectPurgeFacility() {
		setSelectedTreeNode(purgeFacilityNode);
		current = Integer.toString(FACILITY_PURGE_BULK_ID);
		setPurgeFacility(false);
	}
	
	
	public void submitFacilitPurgeSearch(){
		setPurdgeCandidates(null);
		this.setSelectedPurgeCandidates();
		allowClose = false;
		final BulkOperationsCatalog boc = this;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		displayUtilBean = DisplayUtil.getSessionInstance(facesContext, true);

		try {
			bulkOperation.searchFacilityPurgeCandidates(boc);
			fpurgeSearchResultWrapper = new TableSorter(); 
			fpurgeSearchResultWrapper.setWrappedData(purdgeCandidates);
		} catch (Exception e) {
			String error = "Search failed: System Error. ";
			logger.error(error, e);
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				error += e.getMessage();
			} else {
				setErrorMessage(error
						+ " Please contact the System Administrator.");
			}
			bulkOperation.setSearchStarted(false);
			bulkOperation.setSearchCompleted(false);
		}
	}
	
	
	public String selectAllPurgeCandidates() {
		if (purdgeCandidates != null) {
			for (FacilityPurgeSearchLineItem i : purdgeCandidates) {
				i.setSelected(true);
			}
		}
		return null;
	}
	
	public String selectNonePurgeCandidates() {
		if (purdgeCandidates != null) {
			for (FacilityPurgeSearchLineItem i : purdgeCandidates) {
				i.setSelected(false);
			}
		}
		return null;
	}
	
	public final String setSelectedPurgeCandidates() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }		
		try {
			selectedPurgeCandidates = new ArrayList<FacilityPurgeSearchLineItem>();
			if (purdgeCandidates != null) {
				for (FacilityPurgeSearchLineItem facility : purdgeCandidates) {
					if (facility.isSelected()) {
						selectedPurgeCandidates.add(facility);
					}
				}
				if (selectedPurgeCandidates.isEmpty()) {
					DisplayUtil.displayError("No facilities have been selected.");
					return FAIL;
				}
				bulkOperation.performPreliminaryWork(this);
			}
			return bulkOperation.getNavigation();
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().length() > 0) {
				DisplayUtil.displayError(e.getMessage());
				String msg = "Preliminary work failed: System Error.";
				DisplayUtil.displayError(msg);
				logger.error(e.getMessage(), e);
			} else {
				String msg = "System Error: Could not set selected facilities.";
				DisplayUtil.displayError(msg);
				logger.error(msg, e);
			}
			return "FAILURE";
		} finally {
            clearButtonClicked();
        }

	}
	
	public String getWarningMessage(){
		return "You are about to purge " + selectedPurgeCandidates.size() + " facility record(s).";
	}
	
	
}
