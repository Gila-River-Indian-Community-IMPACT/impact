package us.oh.state.epa.stars2.app.admin;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.component.UIXTable;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import us.oh.state.epa.stars2.app.tools.SpatialData;
import us.oh.state.epa.stars2.app.tools.SpatialData.SpatialDataLineItem;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.StringContainer;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ForeignKeyReference;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEUCategory;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCNonChargePollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCDataImportPollutant;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.def.ShapeDef;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.facility.FacilityReference;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

public class ServiceCatalog extends TreeBase {

	private static final long serialVersionUID = 6747599993725241881L;

	public static final String SPATIAL_DATA_OUTCOME = "tools.spatialData";

	private ArrayList<SCEmissionsReport> reports;
	private LinkedHashMap<String, String> reportGroups;
	private LinkedHashMap<String, Integer> referenceYears;
	private LinkedHashMap<String, Integer> reportFreqYears;
	private SCEmissionsReport report;
	private SCEUCategory category;
	private Fee fee;
	private boolean addingReport;
	private boolean cloneReport;
	private boolean addingCategory;
	private boolean addingCategoryFee;
	private boolean editingReport;
	private boolean reportEditable;
	private boolean editingCategory;
	private boolean editingCategoryFee;
	private boolean reportGroup;
	private boolean rangeFeeType;

	protected static final String EMISSIONS_CATEGORY_DIALOG = "dialog:serviceCatalogEmissionsInfo";
	protected static final String NON_CHARGEABLE_POLLUTANTS_DIALOG = "dialog:serviceCatalogAddNCPollutant";
	public static final String SERVICE_CATALOG_OUTCOME = "admin.serviceCatalog";

	private TableSorter pollutantTableWrapper = new TableSorter();
	private TableSorter ncPollutantTableWrapper = new TableSorter();
	private TableSorter dataImportPoluttantTableWrapper = new TableSorter();

	private transient UIXTable pollutantTable;
	private transient UIXTable ncPollutantTable;
	private transient UIXTable dataImportPollutantTable;
	private transient UIXTable feeTable;
	private transient UIXTable exTable;
	private transient UIXTable tvClassTable;

	private boolean pollutantsDeprecated;
	private boolean ncPollutantsDeprecated;
	private boolean diPollutantsDeprecated;
	private boolean ncEmissionCategorySelected;
	private String pollutantCategory;
	private LinkedHashMap<String, Integer> workflows;

	private InfrastructureService infrastructureService;
	private ReadWorkFlowService workFlowService;

	private List<Integer> currentYears = new ArrayList<Integer>(0);

	private FacilityReference facilityReference;
	
	private transient SCNonChargePollutant addedNcPollutant;

	public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	public ServiceCatalog() {
		super();

		cacheViewIDs.add("/admin/serviceCatalog.jsp");
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public final boolean isEditingCategoryFee() {
		return editingCategoryFee;
	}

	public final boolean isPollutantsDeprecated() {
		return pollutantsDeprecated;
	}

	public final void setPollutantsDeprecated(boolean pollutantsDeprecated) {
		this.pollutantsDeprecated = pollutantsDeprecated;
	}

	public boolean isNcPollutantsDeprecated() {
		return ncPollutantsDeprecated;
	}

	public void setNcPollutantsDeprecated(boolean ncPollutantsDeprecated) {
		this.ncPollutantsDeprecated = ncPollutantsDeprecated;
	}

	public final void setEditingCategoryFee(boolean editingCategoryFee) {
		this.editingCategoryFee = editingCategoryFee;
	}

	public final boolean isAddingCategory() {
		return addingCategory;
	}

	public final void setAddingCategory(boolean addingCategory) {
		this.addingCategory = addingCategory;
	}

	public final boolean isEditingCategory() {
		return editingCategory;
	}

	public final void setEditingCategory(boolean editingCategory) {
		this.editingCategory = editingCategory;
	}

	public final LinkedHashMap<String, String> getReportGroups() {

		if (reportGroups == null || reportGroups.size() < 1) {
			reportGroups = new LinkedHashMap<String, String>();
		}

		if (reportGroups.size() < 1) {
			reportGroups.put("TV", "TV");
		}

		return reportGroups;
	}

	public final String nodeClicked() {

		String savedCurrent = current;
		TreeNode savedSelectedTreeNode = selectedTreeNode;
		reset();
		reports = null;
		treeData = null;
		retrieveTreeData();
		current = savedCurrent;
		setSelectedTreeNode(savedSelectedTreeNode);
		retrieveReport();
		return null;
	}

	@Override
	public final void setSelectedTreeNode(TreeNode selectedTreeNode) {
		reset();

		this.selectedTreeNode = selectedTreeNode;
	}

	public SCEmissionsReport getReport() {
		return report;
	}

	public void setReport(SCEmissionsReport report) {
		this.report = report;
	}

	public final TreeModelBase getTreeData() {
		return treeData;
	}

	public final String reset() {

		setAddingReport(false);
		setAddingCategory(false);
		setAddingCategoryFee(false);
		setEditingReport(false);
		setReportEditable(false);
		setEditingCategory(false);
		setEditingCategoryFee(false);
		setReportGroup(false);
		setRangeFeeType(false);

		report = null;
		fee = null;
		category = null;

		if (this.pollutantTable != null && this.pollutantTable.getSelectionState() != null) {
			this.pollutantTable.getSelectionState().clear();
		}
		if (this.ncPollutantTable != null && this.ncPollutantTable.getSelectionState() != null) {
			this.ncPollutantTable.getSelectionState().clear();
		}
		if (this.dataImportPollutantTable != null && this.dataImportPollutantTable.getSelectionState() != null) {
			this.dataImportPollutantTable.getSelectionState().clear();
		}

		return CANCELLED;
	}

	public final String addReport() {

		report = new SCEmissionsReport();

		setAddingReport(true);

		if (selectedTreeNode.getType().equals("reportGroup")) {
			report.setReportGroup(selectedTreeNode.getDescription());
			setReportGroup(true);
		}

		return "ReportAdded";
	}

	public final void addFee() {
		if (report != null) {
			report.addFee(new Fee());
		}
	}

	public final void addEx() {
		if (report != null) {
			report.addEx(new StringContainer());
		}
	}

	public final void addTvClass() {
		if (report != null) {
			report.addTvClass(new StringContainer());
		}
	}

	public final String addCategoryFee() {
		fee = new Fee();

		setAddingCategoryFee(true);

		return "CategoryFeeAdded";
	}

	public final String addCategory() {
		
		category = new SCEUCategory();
		setAddingCategory(true);

		return "CategoryAdded";
	}

	public final String addReminder() {
		return "ReminderAdded";
	}

	public final String cloneReport() {

		if (report != null) {
			setCloneReport(true);
			setAddingReport(true);
			setEditingReport(true);

			report = new SCEmissionsReport(report);

			report.setId(null);
			report.setReportName("");
			report.setReportingYear(null);

			checkDeprecatedPollutants();
			checkDeprecatedNonChargePollutants();
			checkDeprecatedDataImportPollutants();
		}
		return "ReportCloned";
	}

	public final String saveReport() {

		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		boolean operationOk = true;

		if (report != null) {
			
			operationOk = validateReport(report);
			
			if (operationOk) {
				
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				// Timestamp todaysDate = new Timestamp(cal.getTimeInMillis());
				// if(todaysDate.after(report.getEffectiveDate())) {
				// DisplayUtil.displayError("The Effective Date cannot be in the
				// past.");
				// clearButtonClicked();
				// return ""; // Stay on the page.
				// }
				report.setReportName(report.getReportingYear() + " " + report.getContentTypeCd() + " "
						+ report.getRegulatoryRequirementCd());
				int i = 1;
				for (Fee fee : report.getFees()) {
					if (i == 1) {
						fee.setFeeNm(report.getReportName() + " Fee First Half");
					} else {
						fee.setFeeNm(report.getReportName() + " Fee Second Half");
					}
					i++;
				}

				try {
					if (report.getId() == null) {
						infrastructureService.createSCEmissionsReport(report);
					} else {
						infrastructureService.modifySCEmissionsReport(report);
					}
					setNcEmissionCategorySelected(false);
					DisplayUtil.displayInfo("Report saved");
				} catch (RemoteException re) {
					operationOk = false;
					logger.error(re.getMessage(), re);
					DisplayUtil.displayError("System error. Please contact system administrator");
				} finally {
					clearButtonClicked();
					reports = null;
				}
			}
		} else {
			operationOk = false;
		}

		if (!operationOk) {
			clearButtonClicked();
			return null;
		}

		reset();
		if (isCloneReport()) {
			treeData = null;
			retrieveTreeData();
		}
		setCloneReport(false);
		retrieveReports();
		retrieveReport();
		return "ReportSaved";
	}

	public final void removeReport() {

		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}

		ConfirmWindow cw = (ConfirmWindow) FacesUtil.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			clearButtonClicked();
			return;
		}

		boolean operationOk = false;

		if (report != null) {
			try {

				operationOk = infrastructureService.removeSCEmissionsReport(report);

				setNcEmissionCategorySelected(false);
				DisplayUtil.displayInfo("Service Catalog Template Detail removed");

			} catch (RemoteException re1) {
				operationOk = false;
				logger.error(re1.getMessage(), re1);
				DisplayUtil.displayError("System error. Please contact system administrator");
			} finally {
				clearButtonClicked();
				reports = null;
				reset();
				setCloneReport(false);
				treeData = null;
				retrieveTreeData();
			}
		} else {
			operationOk = false;
		}

		if (!operationOk) {
			clearButtonClicked();
			return;
		}

		return;
	}

	// only allow to delete a Service Catalog Detail if it is not associated
	// with any EIs or Enabled EIs

	public boolean getAllowedToDeleteReport() {

		boolean ret = true;

		// check if the deleting service catalog is referred by other objects -
		// EI, Enabled EI etc
		// if yes, then don't delete the service catalog template detail

		if (isReferenceToSc(this.report)) {
			logger.debug("Cannot delete service catalog template detail: " + this.retrieveReport().getId());
			ret = false;
		}

		return ret;
	}

	private boolean validateReport(SCEmissionsReport report) {
		
		boolean ret = true;

		if (report.getPermitClassCds().isEmpty() && !Utility.isNullOrEmpty(report.getTreatPartialAsFullPeriodFlag())) {
			DisplayUtil.displayError(
					"\'Require Emissions Reporting if Facility Class Met During Period?\' must not have a value if no Facility Class is selected.");
			ret = false;
		}

		if (!report.getPermitClassCds().isEmpty() && Utility.isNullOrEmpty(report.getTreatPartialAsFullPeriodFlag())) {
			DisplayUtil.displayError(
					"\'Require Emissions Reporting if Facility Class Met During Period?\' must have a value if one or more Facility Class values is selected.");
			ret = false;
		}

		ValidationMessage[] valMsgs = report.validate();

		if (valMsgs != null && valMsgs.length > 0) {
			for (ValidationMessage msg : valMsgs) {
				if (msg.getSeverity().equals(ValidationMessage.Severity.ERROR)) {
					ret = false;
					DisplayUtil.displayError(msg.getMessage());
				}
			}
			ret = false;
		}
		
		if (!ret) {
			return ret;
		}

		try {
			if (!infrastructureService.okToSaveServiceCatalogTemplate(report)) {
				DisplayUtil.displayError(
						"There cannot be two entries with the same combination of Reporting Year, Content Type, and Regulatory Requirement.");
				return false;
			}
		} catch (DAOException e) {
			DisplayUtil.displayError("System error. Please contact system administrator.");
			logger.error("System error while running infrastructureService.okToSaveServiceCatalogTemplate(report).", e);
		}

		return ret;
	}

	public final String saveCategory() {

		try {
			if (category.getCategoryId() == null) {
				infrastructureService.createSCEUCategory(category);
			} else {
				infrastructureService.modifySCEUCategory(category);
			}
		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			DisplayUtil.displayError("System error. Please contact system administrator");
		}

		reset();

		// treeData = null;

		return "CategorySaved";
	}

	public final String saveCategoryFee() {

		try {
			if (fee.getFeeId() == null) {
				infrastructureService.createCategoryFee(fee, category.getCategoryId());
			} else {
				infrastructureService.modifyCategoryFee(fee);
			}
		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			DisplayUtil.displayError("System error. Please contact system administrator");
		}

		reset();

		// treeData = null;

		return "CategoryFeeSaved";
	}

	public final void addPollutant() {

		if (report != null) {
			SCPollutant tempPollutant = new SCPollutant();
			tempPollutant.setDisplayOrder(report.getPollutants().length + 1);
			report.addPollutant(tempPollutant);
			pollutantTableWrapper.setWrappedData(report.getPollutants());
			checkDeprecatedPollutants();
		}
	}

	public final void deletePollutants() {

		if (report != null) {
			List<SCPollutant> delObjects = new ArrayList<SCPollutant>();
			pollutantTable = pollutantTableWrapper.getTable();
			Iterator<?> it = pollutantTable.getSelectionState().getKeySet().iterator();
			Object oldKey = pollutantTable.getRowKey();
			while (it.hasNext()) {
				Object obj = it.next();
				pollutantTable.setRowKey(obj);
				delObjects.add((SCPollutant) pollutantTable.getRowData());
			}

			for (SCPollutant delObject : delObjects) {
				report.removePollutant(delObject);
			}

			pollutantTable.setRowKey(oldKey);
			pollutantTable.getSelectionState().clear();
			pollutantTableWrapper.setWrappedData(report.getPollutants());
			checkDeprecatedPollutants();
		}
	}

	public final void deleteFees() {

		List<Fee> delObjects = new ArrayList<Fee>();
		Iterator<?> it = feeTable.getSelectionState().getKeySet().iterator();
		Object oldKey = feeTable.getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			feeTable.setRowKey(obj);
			delObjects.add((Fee) feeTable.getRowData());
		}

		for (Fee delObject : delObjects) {
			report.removeFee(delObject);
		}

		feeTable.setRowKey(oldKey);
		feeTable.getSelectionState().clear();

	}

	public final void deleteEx() {

		List<StringContainer> delObjects = new ArrayList<StringContainer>();
		Iterator<?> it = exTable.getSelectionState().getKeySet().iterator();
		Object oldKey = exTable.getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			exTable.setRowKey(obj);
			delObjects.add((StringContainer) exTable.getRowData());
		}

		for (StringContainer delObject : delObjects) {
			report.removeEx(delObject);
		}

		exTable.setRowKey(oldKey);
		exTable.getSelectionState().clear();

	}

	public final void deleteTvClass() {

		List<StringContainer> delObjects = new ArrayList<StringContainer>();
		Iterator<?> it = tvClassTable.getSelectionState().getKeySet().iterator();
		Object oldKey = tvClassTable.getRowKey();
		while (it.hasNext()) {
			Object obj = it.next();
			tvClassTable.setRowKey(obj);
			delObjects.add((StringContainer) tvClassTable.getRowData());
		}

		for (StringContainer delObject : delObjects) {
			report.removeTvClass(delObject);
		}

		tvClassTable.setRowKey(oldKey);
		tvClassTable.getSelectionState().clear();

	}

	public final String editReport() {

		setEditingReport(true);
		checkDeprecatedPollutants();
		checkDeprecatedNonChargePollutants();
		checkDeprecatedDataImportPollutants();
		if (isReferenceToSc(this.report)) {
			DisplayUtil.displayWarning("There are Emissions Inventory associated with this Service Catalog.");
		}

		return "ReportEditable";
	}

	public final String editCategory() {
		setEditingCategory(true);

		return "CategoryEditable";
	}

	public final String editCategoryFee() {
		setEditingCategoryFee(true);

		return "CategoryFeeEditable";
	}

	public final boolean isAddingReport() {
		return addingReport;
	}

	public final void setAddingReport(boolean addingReport) {
		this.addingReport = addingReport;
	}

	public final boolean isEditingReport() {
		return editingReport;
	}

	public final void setEditingReport(boolean editingReport) {
		this.editingReport = editingReport;
	}

	public final String getFeeType() {
		String ret = "unit";
		if (report != null) {
			ret = report.getFeeType();
		}

		return ret;
	}

	public final void setFeeType(String feeType) {
		if (report != null) {
			if (feeType.equals("unit")) {
				rangeFeeType = false;
			} else {
				rangeFeeType = true;
			}
			report.setFees(null);

			report.setFeeType(feeType);
		}
	}

	public final boolean isReportGroup() {
		return reportGroup;
	}

	public final boolean isEisReport() {
		boolean ret = false;

		if ((report != null) && (report.getReportGroup().equals("EIS"))) {
			ret = true;
		}

		return ret;
	}

	public final boolean isEsReport() {
		boolean ret = false;

		if ((report != null) && (report.getReportGroup().equals("ES"))) {
			ret = true;
		}

		return ret;
	}

	public final void setReportGroup(boolean reportGroup) {
		this.reportGroup = reportGroup;
	}

	public final SCEUCategory getCategory() {

		if (selectedTreeNode.getType().equals("category") && !addingCategory) {
			if ((category == null)
					|| (selectedTreeNode.getIdentifier().compareTo(category.getCategoryId().toString()) != 0)) {
				try {
					Integer categoryId = new Integer(
							selectedTreeNode.getIdentifier().substring(8, selectedTreeNode.getIdentifier().length()));
					category = infrastructureService.retrieveEUCategory(categoryId);
				} catch (RemoteException re) {
					logger.error(re.getMessage(), re);
					DisplayUtil.displayError("System error. Please contact system administrator");
				}
			}
		}

		return category;
	}

	public final void setCategory(SCEUCategory category) {
		this.category = category;
	}

	public final boolean isAddingCategoryFee() {
		return addingCategoryFee;
	}

	public final void setAddingCategoryFee(boolean addingCategoryFee) {
		this.addingCategoryFee = addingCategoryFee;
	}

	public final Fee getFee() {

		if (selectedTreeNode.getType().equals("categoryFee") && !addingCategoryFee) {
			if ((fee == null) || (selectedTreeNode.getIdentifier().compareTo(fee.getFeeId().toString()) != 0)) {
				try {
					Integer feeId = new Integer(
							selectedTreeNode.getIdentifier().substring(3, selectedTreeNode.getIdentifier().length()));
					fee = infrastructureService.retrieveFee(feeId);

					category = infrastructureService.retrieveCategoryByFeeId(feeId);
				} catch (RemoteException re) {
					logger.error(re.getMessage(), re);
					DisplayUtil.displayError("System error. Please contact system administrator");
				}
			}
		}

		return fee;
	}

	public final void setFee(Fee fee) {
		this.fee = fee;
	}

	public final boolean isReportEditable() {
		return reportEditable;
	}

	public final void setReportEditable(boolean reportEditable) {
		this.reportEditable = reportEditable;
	}

	public final boolean isRangeFeeType() {
		return rangeFeeType;
	}

	public final void setRangeFeeType(boolean rangeFeeType) {
		this.rangeFeeType = rangeFeeType;
	}

	public final UIXTable getFeeTable() {
		return feeTable;
	}

	public final void setFeeTable(UIXTable feeTable) {
		this.feeTable = feeTable;
	}

	public final UIXTable getPollutantTable() {
		return pollutantTable;
	}

	public final void setPollutantTable(UIXTable pollutantTable) {
		this.pollutantTable = pollutantTable;
	}

	public final UIXTable getNcPollutantTable() {
		return ncPollutantTable;
	}

	public final void setNcPollutantTable(UIXTable ncPollutantTable) {
		this.ncPollutantTable = ncPollutantTable;
	}

	public final List<SelectItem> getReportingYears() {

		List<SelectItem> ret = new ArrayList<SelectItem>();

		// At most, populate the Reporting Years drop down with last year, this
		// year, next year.
		Integer year = Calendar.getInstance().get(Calendar.YEAR) - 2;
		int lookForYear = 0;
		if (report != null && report.getReportingYear() != null) {
			lookForYear = report.getReportingYear();
		}
		boolean notInList = true;
		for (int i = 0; i <= 2; i++) {
			year++;
			if (year == lookForYear) {
				notInList = false;
			}

			ret.add(new SelectItem(year, year.toString()));

		}

		// Need to insure that the reportingYear in the current report is in the
		// drop down list. If current reportingYear is less than current year
		// then add it to the list disabled so it can't be chosen.
		if (lookForYear != 0 && notInList) {
			SelectItem temp = new SelectItem(report.getReportingYear(), report.getReportingYear().toString());
			temp.setDisabled(true);
			ret.add(temp);
		}
		return ret;
	}

	public final LinkedHashMap<String, Integer> getReferenceYears() {

		if ((referenceYears == null) || (referenceYears.size() == 0)) {
			referenceYears = new LinkedHashMap<String, Integer>();

			Integer year = Calendar.getInstance().get(Calendar.YEAR);

			year = year - 10;
			for (int i = 0; i <= 20; i++) {
				referenceYears.put(year.toString(), year++);
			}
		}

		return referenceYears;
	}

	public final LinkedHashMap<String, Integer> getReportFrequencyYears() {

		if ((reportFreqYears == null) || (reportFreqYears.size() == 0)) {
			reportFreqYears = new LinkedHashMap<String, Integer>();

			for (Integer i = 0; i <= 10; i++) {
				reportFreqYears.put(i.toString(), i);
			}
		}

		return reportFreqYears;
	}

	private void checkDeprecatedPollutants() {

		if (report != null) {
			pollutantsDeprecated = false;
			for (SCPollutant pollutant : report.getPollutants()) {
				if (pollutant.isDeprecated()) {
					pollutantsDeprecated = true;

					DisplayUtil.displayError("Pollutant " + pollutant.getPollutantDsc()
							+ " is deprecated, please delete from pollutant table.");
				}
			}
		}
	}

	private void checkDeprecatedNonChargePollutants() {

		if (report != null) {
			ncPollutantsDeprecated = false;
			for (SCNonChargePollutant ncPollutant : report.getNcPollutants()) {
				if (ncPollutant.isDeprecated()) {
					ncPollutantsDeprecated = true;

					DisplayUtil.displayError("Pollutant " + ncPollutant.getPollutantDsc()
							+ " is deprecated, please delete from non-chargeable hazardous air pollutant table.");
				}
			}
		}
	}

	public void clearCache() {
		/*
		 * super.clearCache();
		 * 
		 * if (reports != null) { reports.clear(); reports = null; }
		 * 
		 * if (reportGroups != null) { reportGroups.clear(); reportGroups =
		 * null; }
		 * 
		 * if (referenceYears != null) { referenceYears.clear(); referenceYears
		 * = null; }
		 * 
		 * if (reportFreqYears != null) { reportFreqYears.clear();
		 * reportFreqYears = null; }
		 */}

	private void retrieveReports() {

		if (reports == null) {
			reports = new ArrayList<SCEmissionsReport>();

			try {
				reports = new ArrayList<SCEmissionsReport>(
						Arrays.asList(infrastructureService.retrieveSCEmissionsReports()));
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil.displayError("System error. Please contact system administrator");
			}
		}
	}

	public final LinkedHashMap<String, Integer> getWorkflows() {

		if (workflows == null) {
			try {
				workflows = new LinkedHashMap<String, Integer>();
				LinkedHashMap<String, Integer> tw = getWorkFlowService().retrieveWorkflowTempIdAndNm();
				for (String ptName : tw.keySet()) {
					Integer ptId = tw.get(ptName);
					ProcessTemplate p = getWorkFlowService().retrieveProcessTemplate(ptId);
					if (p.getProcessCd().equalsIgnoreCase(WorkflowProcessDef.EMISSION_REPORTING)) {
						workflows.put(ptName, ptId);
					}
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil.displayError("System error. Please contact system administrator");
			}
		}
		return workflows;
	}

	public UIXTable getExTable() {
		return exTable;
	}

	public void setExTable(UIXTable exTable) {
		this.exTable = exTable;
	}

	public UIXTable getTvClassTable() {
		return tvClassTable;
	}

	public void setTvClassTable(UIXTable tvClassTable) {
		this.tvClassTable = tvClassTable;
	}

	public final void addNonChargePollutant() {
		
		if (report != null) {
			
			SCNonChargePollutant tempNcPollutant = new SCNonChargePollutant();
			report.addNonChargePollutant(tempNcPollutant);
			ncPollutantTableWrapper = new TableSorter();
			ncPollutantTableWrapper.setWrappedData(report.getNcPollutants());
			checkDeprecatedNonChargePollutants();
		}
	}

	public void applyAddPollutantByCategory(ActionEvent actionEvent) {

		if (getPollutantCategory() == null || !(getPollutantCategory().length() > 0)) {
			DisplayUtil.displayError("Please select Pollutant Category");
			return;
		}

		if (report != null) {
			List<SCNonChargePollutant> pollutants = null;
			// getTreeData();
			try {
				setNcEmissionCategorySelected(true);
				pollutants = getInfrastructureService().retrievePollutantsByCategory(getPollutantCategory(),
						report.getId());
			} catch (DAOException e) {
				DisplayUtil.displayError("System error. Please contact system administrator.");
				logger.error(
						"System error while running getInfrastructureService().retrievePollutantsByCategory(getPollutantCategory().",
						e);
			}
			if (report.getNcPollutants().length > 0) {
				filterAddNonChargePoulltants(pollutants);
			} else {
				report.addNonChargePollutantByCategory(pollutants);
			}
			ncPollutantTableWrapper = new TableSorter();
			ncPollutantTableWrapper.setWrappedData(report.getNcPollutants());
			checkDeprecatedNonChargePollutants();
			setPollutantCategory(null);
			closeDialog();
		}

	}

	public final void deleteNonChargePollutants() {

		if (report != null) {
			
			List<SCNonChargePollutant> delObjects = new ArrayList<SCNonChargePollutant>();
			ncPollutantTable = ncPollutantTableWrapper.getTable();
			Iterator<?> it = ncPollutantTable.getSelectionState().getKeySet().iterator();
			Object oldKey = ncPollutantTable.getRowKey();
			while (it.hasNext()) {
				Object obj = it.next();
				ncPollutantTable.setRowKey(obj);
				delObjects.add((SCNonChargePollutant) ncPollutantTable.getRowData());
			}

			for (SCNonChargePollutant delObject : delObjects) {
				report.removeNonChargePollutant(delObject);
			}

			ncPollutantTable.setRowKey(oldKey);
			ncPollutantTable.getSelectionState().clear();
			ncPollutantTableWrapper = new TableSorter();
			ncPollutantTableWrapper.setWrappedData(report.getNcPollutants());
			checkDeprecatedNonChargePollutants();

		}
	}

	public String startSelectPollutantCategory() {
		return EMISSIONS_CATEGORY_DIALOG;
	}

	public final String cancelAddPollutantByCategory() {
		this.closeDialog();
		return CANCELLED;
	}

	public void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public String getPollutantCategory() {
		return pollutantCategory;
	}

	public void setPollutantCategory(String pollutantCategory) {
		this.pollutantCategory = pollutantCategory;
	}

	public boolean isNcEmissionCategorySelected() {
		return ncEmissionCategorySelected;
	}

	public void setNcEmissionCategorySelected(boolean ncEmissionCategorySelected) {
		this.ncEmissionCategorySelected = ncEmissionCategorySelected;
	}

	public void filterAddNonChargePoulltants(List<SCNonChargePollutant> pollutants) {

		SCNonChargePollutant[] tempNcPollutants = report.getNcPollutants();
		for (SCNonChargePollutant categoryPollutant : pollutants) {
			boolean alreadyExists = false;
			for (SCNonChargePollutant reportPollutant : tempNcPollutants) {

				if (categoryPollutant.getPollutantCd().equalsIgnoreCase(reportPollutant.getPollutantCd())) {
					alreadyExists = true;
					break;
				}
			}
			if (!alreadyExists)
				report.addNonChargePollutant(categoryPollutant);
		}
		tempNcPollutants = null;

	}

	public boolean isCloneReport() {
		return cloneReport;
	}

	public void setCloneReport(boolean cloneReport) {
		this.cloneReport = cloneReport;
	}

	public String initServiceCatalog() {

		logger.debug("Initializing the Service Catalog...");
		String ret = SERVICE_CATALOG_OUTCOME;
		reset();
		clearButtonClicked();
		reports = null;
		this.treeData = null;
		retrieveTreeData();
		return ret;
	}

	public String initReportsServiceCatalog() {
		initServiceCatalog();
		return "reports.serviceCatalog";
	}

	public final List<SelectItem> getContentTypes() {

		List<SelectItem> ret = null;

		if (report != null) {
			ret = getContentTypes(report.getContentTypeCd());
		} else {
			ret = getContentTypes("");
		}

		return ret;
	}

	public final List<SelectItem> getRegulatoryRequirementTypes() {

		List<SelectItem> ret = null;

		if (report != null) {
			ret = getRegulatoryRequirementTypes(report.getRegulatoryRequirementCd());
		} else {
			ret = getRegulatoryRequirementTypes("");
		}

		return ret;
	}

	public String displayFacilityLocationOnMap() {

		SpatialData spatialDataBean = (SpatialData) FacesUtil.getManagedBean("spatialData");

		if (null != spatialDataBean) {
			spatialDataBean.refresh();
			Integer shapeId = this.report.getShapeId();
			for (SpatialDataLineItem item : spatialDataBean.getImportedShapes()) {
				item.setSelected(item.getShapeId().equals(shapeId));
			}
		}

		return SPATIAL_DATA_OUTCOME;
	}

	public final String getShapeLabel() {
		return ShapeDef.getData().getItems().getItemDesc((this.report).getStrShapeId());
	}

	public FacilityReference getFacilityReference() {
		return facilityReference;
	}

	public void setFacilityReference(FacilityReference facilityReference) {
		this.facilityReference = facilityReference;
	}

	public boolean isReferenceToSc(SCEmissionsReport report) {

		final String fkTableName = "SC_EMISSIONS_REPORT";
		final String fkColumnName = "SC_EMISSIONS_REPORT_ID";
		final String schema = "dbo";

		List<ForeignKeyReference> fkReferences = new ArrayList<ForeignKeyReference>();
		boolean ret = false;

		try {
			fkReferences = getInfrastructureService().retrieveForeignKeyReferences(fkTableName, fkColumnName, schema);

			if (null != fkReferences) {

				// following references should not prevent Service Catalog from
				// being deleted so remove them from the list (if they exist)
				fkReferences.remove(
						new ForeignKeyReference("SC_REPORT_FEE_XREF", "SC_EMISSIONS_REPORT_ID", "SC_REPORT_FEE_XREF"));
				fkReferences.remove(new ForeignKeyReference("SC_REPORT_POLLUTANT_XREF", "SC_EMISSIONS_REPORT_ID",
						"SC_REPORT_POLLUTANT_XREF"));
				fkReferences.remove(new ForeignKeyReference("SC_REPORT_NC_POLLUTANT_XREF", "SC_EMISSIONS_REPORT_ID",
						"SC_REPORT_NC_POLLUTANT_XREF"));
				fkReferences.remove(new ForeignKeyReference("SC_REPORT_DATA_IMPORT_POLLUTANT_XREF",
						"SC_EMISSIONS_REPORT_ID", "SC_REPORT_DATA_IMPORT_POLLUTANT_XREF"));
				fkReferences.remove(new ForeignKeyReference("SC_REPORT_PERMIT_CLASS_XREF", "SC_EMISSIONS_REPORT_ID",
						"SC_REPORT_PERMIT_CLASS_XREF"));
				fkReferences.remove(new ForeignKeyReference("SC_REPORT_FACILITY_TYPE_XREF", "SC_EMISSIONS_REPORT_ID",
						"SC_REPORT_FACILITY_TYPE_XREF"));

				if (this.editingReport) {
					fkReferences.remove(new ForeignKeyReference("FP_YEARLY_REPORTING_CATEGORY",
							"SC_EMISSIONS_REPORT_ID", "FP_YEARLY_REPORTING_CATEGORY"));
				}

				for (ForeignKeyReference fkRef : fkReferences) {

					if (getInfrastructureService().checkForeignKeyReferencedData(fkRef.getFkTableName(),
							fkRef.getFkColumnName(), report.getId())) {
						logger.info("Service Catalog has reference to one or more " + fkRef.getFkObjectName());
						ret = true;
						break; // don't need to keep checking
					}
				}
			}
		} catch (RemoteException e) {
			handleException(e);
		}

		return ret;
	}

	public UIXTable getDataImportPollutantTable() {
		return dataImportPollutantTable;
	}

	public void setDataImportPollutantTable(UIXTable dataImportPollutantTable) {
		this.dataImportPollutantTable = dataImportPollutantTable;
	}

	public void addDataImportPollutant() {

		if (report != null) {
			SCDataImportPollutant tempDataImportPollutant = new SCDataImportPollutant();
			report.addDataImportPollutant(tempDataImportPollutant);

			checkDeprecatedDataImportPollutants();
		}
	}

	public void deleteDataImportPollutants() {

		if (report != null) {
			List<SCDataImportPollutant> deleteObjects = new ArrayList<SCDataImportPollutant>();
			dataImportPollutantTable = dataImportPoluttantTableWrapper.getTable();
			Iterator<?> iterator = dataImportPollutantTable.getSelectionState().getKeySet().iterator();
			Object oldKey = dataImportPollutantTable.getRowKey();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				dataImportPollutantTable.setRowKey(obj);
				deleteObjects.add((SCDataImportPollutant) dataImportPollutantTable.getRowData());
			}

			for (SCDataImportPollutant delObject : deleteObjects) {
				report.removeDataImportPollutant(delObject);
			}

			dataImportPollutantTable.setRowKey(oldKey);
			dataImportPollutantTable.getSelectionState().clear();
			dataImportPoluttantTableWrapper.setWrappedData(report.getDataImportPollutantList());
			checkDeprecatedDataImportPollutants();
		}
	}

	public final boolean isDataImportPollutantsDeprecated() {
		return diPollutantsDeprecated;
	}

	public final void setDataImportPollutantsDeprecated(boolean diPollutantsDeprecated) {
		this.diPollutantsDeprecated = diPollutantsDeprecated;
	}

	private void checkDeprecatedDataImportPollutants() {

		if (report != null) {
			diPollutantsDeprecated = false;
			for (SCDataImportPollutant diPollutant : report.getDataImportPollutantList()) {
				if (diPollutant.isDeprecated()) {
					diPollutantsDeprecated = true;

					DisplayUtil.displayError("Pollutant " + diPollutant.getPollutantDsc()
							+ " is deprecated, please delete from the Data Import Pollutant table.");
				}
			}
		}
	}

	public final String cancelEdit() {
		reset();
		retrieveReport();
		return CANCELLED;
	}

	public final SCEmissionsReport retrieveReport() {

		if (selectedTreeNode.getType().equals("report") && !addingReport) {
			try {
				report = infrastructureService.retrieveSCEmissionsReport(new Integer(selectedTreeNode.getIdentifier()));

				setReportEditable(true);
				if (report != null) {
					setupTableWrappers();
				}

			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil.displayError("System error. Please contact system administrator");
			}
		}
		return report;
	}

	public final TreeModelBase retrieveTreeData() {

		if (treeData == null) {
			TreeNodeBase root = new TreeNodeBase("root", "ServiceCatalog", "root", false);
			treeData = new TreeModelBase(root);
			ArrayList<String> treePath = new ArrayList<String>();
			treePath.add("0");

			retrieveReports();

			TreeNodeBase reportRoot = new TreeNodeBase("reportRoot", "Emissions Inventories", "-1", false);

			currentYears = new ArrayList<Integer>(0);
			for (SCEmissionsReport lReport : reports) {
				TreeNodeBase reportNode = new TreeNodeBase("report", lReport.getReportName(),
						lReport.getId().toString(), false);

				reportRoot.getChildren().add(reportNode);

				Integer year = new Integer(lReport.getReportingYear());
				currentYears.add(year);
			}

			root.getChildren().add(reportRoot);

			TreeStateBase treeState = new TreeStateBase();

			treeState.expandPath(treePath.toArray(new String[0]));
			treeData.setTreeState(treeState);
			selectedTreeNode = root;
			current = "root";
		}
		
		return treeData;
	}

	public TableSorter getPollutantTableWrapper() {
		return pollutantTableWrapper;
	}

	public void setPollutantTableWrapper(TableSorter pollutantTableWrapper) {
		this.pollutantTableWrapper = pollutantTableWrapper;
	}

	public TableSorter getNcPollutantTableWrapper() {
		return ncPollutantTableWrapper;
	}

	public void setNcPollutantTableWrapper(TableSorter ncPollutantTableWrapper) {
		this.ncPollutantTableWrapper = ncPollutantTableWrapper;
	}

	public TableSorter getDataImportPoluttantTableWrapper() {
		return dataImportPoluttantTableWrapper;
	}

	public void setDataImportPoluttantTableWrapper(TableSorter dataImportPoluttantTableWrapper) {
		this.dataImportPoluttantTableWrapper = dataImportPoluttantTableWrapper;
	}

	private void setupTableWrappers() {

		// set up the wrappers
		pollutantTableWrapper = new TableSorter();
		pollutantTableWrapper.setWrappedData(this.report.getPollutants());

		ncPollutantTableWrapper = new TableSorter();
		ncPollutantTableWrapper.setWrappedData(this.report.getNcPollutants());

		dataImportPoluttantTableWrapper = new TableSorter();
		dataImportPoluttantTableWrapper.setWrappedData(this.report.getDataImportPollutantList());

	}

	
	public final SCNonChargePollutant getAddedNcPollutant() {
		return addedNcPollutant;
	}

	public final void setAddedNcPollutant(SCNonChargePollutant addedNcPollutant) {
		this.addedNcPollutant = addedNcPollutant;
	}

	public String startAddNonChargeablePollutant() {
		
		addedNcPollutant = new SCNonChargePollutant();
		if (this.getReport() != null) {
			addedNcPollutant.setSCReportId(this.getReport().getId());
		}
		return NON_CHARGEABLE_POLLUTANTS_DIALOG;
	}

	public final String applyAddNonChargeablePollutant() {
		
		if (report != null && addedNcPollutant != null) {
			
			addedNcPollutant.setPollutantDsc(facilityReference.getNonToxicPollutantDefs().getDescFromAllItem(addedNcPollutant.getPollutantCd()));
			report.addNonChargePollutant(addedNcPollutant);
			ncPollutantTableWrapper = new TableSorter();
			ncPollutantTableWrapper.setWrappedData(report.getNcPollutants());
			checkDeprecatedNonChargePollutants();
		}

		addedNcPollutant = null;
		this.closeDialog();
		return CANCELLED;
	}

	public final String cancelAddNonChargeablePollutant() {
		this.closeDialog();
		return CANCELLED;
	}
	
}
