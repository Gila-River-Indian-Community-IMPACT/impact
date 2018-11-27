package us.oh.state.epa.stars2.app.admin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.adhoc.DataGridCellDef;
import us.oh.state.epa.stars2.database.adhoc.DataGridRow;
import us.oh.state.epa.stars2.database.adhoc.DataSet;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TreeBase;

@SuppressWarnings("serial")
public class DefCatalog extends TreeBase {
	private boolean newRecord;
	private boolean newCustomRecord;
	private boolean editingRecord;
	private SimpleDef data;
	private boolean retrieveError;
	private String dataDetailId;
	private HashMap<String, SimpleDef> details;
	private DefinitionSet ds;
	private DefinitionCategory rootCategory;
	private transient DataSet dataSet;
	private int currentColumn;
	private int currentCustomRecord = -1;
	private SimpleDef[] dataDetails = null;
	
	private InfrastructureService infrastructureService;
	private DefinitionsLoader defLoader;
	
	// any other definition xml files that needs to be included
	private List<DefCatalog> subDefCatalogList;

	public DefCatalog() {
		super();
		ds = new DefinitionSet();
	}
	
	public DefinitionsLoader getDefLoader() {
		return defLoader;
	}

	public void setDefLoader(DefinitionsLoader defLoader) {
		if (null == this.defLoader && null != defLoader) {
			rootCategory = defLoader.loadFromConfigMgr();
		}
		this.defLoader = defLoader;
	}


	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	private void loadSimpleDefData() throws DAOException, RemoteException {
		details = new HashMap<String, SimpleDef>();
		ds = defLoader.getDefSet(selectedTreeNode.getIdentifier());
		String cd = ds.getColumnPrefix() + "_CD";
		String dsc = ds.getColumnPrefix() + "_DSC";
		String orderBy = ds.getOrderBy();
		int maxCodeLength = 0;
		int maxDescriptionLength = 0;

		// get the meta data for the code and description columns
		SimpleDef sd = new SimpleDef();
		sd.setTable(ds.getTable());
		sd.setDescriptionColumn(dsc);
		sd.setCodeColumn(cd);

		sd = infrastructureService.getSimpleDefMetaData(sd);

		if (ds.isDeprecate()) {
			dataDetails = infrastructureService.retrieveSimpleDefs(ds.getTable(),
					cd, dsc, "DEPRECATED", orderBy);
		} else {
			dataDetails = infrastructureService.retrieveSimpleDefs(ds.getTable(),
					cd, dsc, null, orderBy);
		}
		for (SimpleDef tempDetail : dataDetails) {
			tempDetail.setCodeColumnSize(maxCodeLength);
			tempDetail.setDescriptionColumnSize(maxDescriptionLength);
			tempDetail.setCodeColumnSize(sd.getCodeColumnSize());
			tempDetail.setDescriptionColumnSize(sd.getDescriptionColumnSize());
			details.put(tempDetail.getCode(), tempDetail);
		}
	}

	@Override
	public final void setSelectedTreeNode(TreeNode selectedTreeNode) {
		if (selectedTreeNode != null) {
			reset();
			this.selectedTreeNode = selectedTreeNode;

			retrieveError = false;
			currentColumn = 0;
			DefinitionSet lds;
			lds = defLoader.getDefSet(selectedTreeNode.getIdentifier());
			if (selectedTreeNode.getType().equals("defTable")
					&& (lds.isCustom())) {

				String table = "<undefined>";
				try {
					table = lds.getTable();
					dataSet.setup(lds.getTable(), defLoader.getDataSetDefinition(lds),
							lds.getOrderBy());
					dataSet.retrieveResultSet();
				} catch (Exception e) {
					DisplayUtil
							.displayError("Table "
									+ table
									+ " does not exist or definitions.xml is not configured properly");
					logger.error(e.getMessage(), e);
					retrieveError = true;
				}
			} else if (selectedTreeNode.getType().equals("defTable")
					&& (!lds.isCustom())) {
				try {
					loadSimpleDefData();
				} catch (RemoteException re) {
					retrieveError = true;
					DisplayUtil
							.displayError("Accessing definition data details failed");
					logger.error(re.getMessage(), re);
				}
			}
		}
	}

	public final String getCustomDetailColumnHeader() {
		DataGridRow dgr = null;
		try {
			dgr = defLoader.getDataSetDefinition(defLoader.getDefSet(selectedTreeNode
					.getIdentifier()));
		} catch (RemoteException re) {
			retrieveError = true;
			DisplayUtil
					.displayError("Accessing definition data details failed");
			logger.error(re.getMessage(), re);
		}
		DataGridCellDef[] dgcd = dgr.getDefCells();

		if ((dataSet != null) && (currentColumn > dataSet.getColumnCount() - 1)) {
			currentColumn = 0;
		}

		return dgcd[currentColumn++].getHeaderText();
	}

	public final String[] getCustomDetailHeaders() {
		String[] ret = null;

		if (dataSet != null) {
			ret = dataSet.getColumnHeaders();
		}

		return ret;
	}

	public final boolean getRenderCustomColumn() {
		boolean ret = false;
		if ((dataSet != null)
				&& !(currentColumn > dataSet.getColumnCount() - 1)) {
			currentColumn++;
			ret = true;
		}
		return ret;
	}

	public final void resetColumnCtr() {
		currentColumn = 0;
	}

	public final DataGridRow[] getCustomDetailData() {
		DataGridRow[] ret = null;

		if (!retrieveError) {
			ret = dataSet.getRowsAsArray();
		}
		return ret;
	}

	public final SimpleDef[] getDetailData() {
		return dataDetails;
	}

	public final boolean isDeprecatable() {
		boolean ret = false;
		try {
			if ((selectedTreeNode != null)
					&& (defLoader.getDefSet(selectedTreeNode.getIdentifier()) != null)) {
				ret = defLoader.getDefSet(selectedTreeNode.getIdentifier())
						.isDeprecate();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

	public final boolean isCustom() {
		boolean ret = false;
		try {
			if ((selectedTreeNode != null)
					&& (defLoader.getDefSet(selectedTreeNode.getIdentifier()) != null)) {
				ret = defLoader.getDefSet(selectedTreeNode.getIdentifier()).isCustom();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

	public final boolean isEditable() {
		boolean ret = false;
		try {
			if ((selectedTreeNode != null)
					&& (defLoader.getDefSet(selectedTreeNode.getIdentifier()) != null)) {
				ret = defLoader.getDefSet(selectedTreeNode.getIdentifier()).isUpdate();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

	public final boolean isNewPermitted() {
		boolean ret = false;
		try {
			if ((selectedTreeNode != null)
					&& (defLoader.getDefSet(selectedTreeNode.getIdentifier()) != null)) {
				ret = defLoader.getDefSet(selectedTreeNode.getIdentifier()).isCreate();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

	public final boolean isSupportsImport() {
		boolean ret = false;
		try {
			if ((selectedTreeNode != null)
					&& (defLoader.getDefSet(selectedTreeNode.getIdentifier()) != null)) {
				ret = defLoader.getDefSet(selectedTreeNode.getIdentifier())
						.isSupportsImport();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

	public final String getDescription() {
		String ret = "";
		try {
			if ((selectedTreeNode != null)
					&& (defLoader.getDefSet(selectedTreeNode.getIdentifier()) != null)) {
				ret = defLoader.getDefSet(selectedTreeNode.getIdentifier())
						.getDescription();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

	public final String getLabel() {
		String ret = "";
		try {
			if ((selectedTreeNode != null)
					&& (defLoader.getDefSet(selectedTreeNode.getIdentifier()) != null)) {
				ret = defLoader.getDefSet(selectedTreeNode.getIdentifier()).getLabel();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	public final void addTreeCategoryAndDefinitions(DefinitionCategory dc,
			TreeNodeBase parent) {

		// skip the root node, which is just a container for all the groups

		// 1. Add the category to the tree
		// 2. Recursively add any subcategories and definitions in those
		// subcategories
		// 3. Add any definitions in the (current) category

		if (dc.getPath().length() == 0) {
			Iterator<String> i = dc.getSubCategories().keySet().iterator();
			while (i.hasNext()) {
				String key = i.next();
				DefinitionCategory dcTmp = dc.getCategory(key);
				if (dcTmp.getDefinitionCountRecursive() > 0) {
					// only render nodes that have defsets or child nodes
					addTreeCategoryAndDefinitions(dcTmp, parent);
				}
			}
		} else {

			// Step 1
			// only create a new node if this isn't the root
			TreeNodeBase ceTypeNode = new TreeNodeBase("root", dc.getLabel(),
					dc.getPath(), false);

			// Step 2
			Iterator<String> i = dc.getSubCategories().keySet().iterator();
			while (i.hasNext()) {
				String key = i.next();
				DefinitionCategory dcTmp = dc.getCategory(key);
				if (dcTmp.getDefinitionCountRecursive() > 0) {
					addTreeCategoryAndDefinitions(dcTmp, ceTypeNode);
				}
			}

			// Step 3
			Iterator<String> ii = dc.getDefinitions().keySet().iterator();
			while (ii.hasNext()) {
				String key = ii.next();
				DefinitionSet lds = dc.getDefinition(key);
				TreeNodeBase definitionTable = new TreeNodeBase("defTable",
						lds.getLabel(), lds.getPath(), true);
				ceTypeNode.getChildren().add(definitionTable);
			}

			// Add the final result to the parent node
			parent.getChildren().add(ceTypeNode);
		}
	}

	@Override
	public final TreeModelBase getTreeData() {
		int categoryCounter = 0;
		if (treeData == null) {
			TreeNodeBase root = new TreeNodeBase("root", "Categories", "root",
					false);
			treeData = new TreeModelBase(root);
			ArrayList<String> treePath = new ArrayList<String>();
			treePath.add("0");

			// recursively build the tree
			categoryCounter++;
			try {
				addTreeCategoryAndDefinitions(rootCategory, root);
			} catch (Exception e) {
				DisplayUtil.displayError("Error building Tree");
				logger.error(e.getMessage(), e);
			}

			TreeStateBase treeState = new TreeStateBase();
			treeState.expandPath(treePath
					.toArray(new String[categoryCounter - 1]));
			treeData.setTreeState(treeState);
			selectedTreeNode = root;
			current = "root";
		}
		return treeData;
	}

	public final int getColumnCount() {
		int ret = 0;
		try {
			if ((selectedTreeNode != null)
					&& (defLoader.getDefSet(selectedTreeNode.getIdentifier()) != null)) {
				ret = defLoader.getDataSetDefinition(
						defLoader.getDefSet(selectedTreeNode.getIdentifier()))
						.getColumnCount();
			}
		} catch (Exception e) {
			DisplayUtil.displayError("Error retrieving column count");
			logger.error(e.getMessage(), e);
		}
		return ret;
	}

	public final void reset() {
		setNewRecord(false);
		setNewCustomRecord(false);
		setEditingRecord(false);
		data = null;
	}

	public final void cancelNew() {
		reset();
	}

	public final void cancel(ActionEvent actionEvent) {
		reset();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void dialogDone() {
		logger.debug("Dialog done");
		return;
	}

	public final void editRecord() {
		setEditingRecord(true);
	}

	public final void editCustomRecord() {
		logger.debug("editing custom record");
	}

	public final DataGridRow getCustomRecord() {
		DataGridRow dgr = new DataGridRow();
		if (isNewCustomRecord()) {
			dgr = dataSet.getNewRow();
		} else {
			if ((currentCustomRecord < dataSet.getSize())
					&& (dataSet.getSize() > 0)) {
				dgr = dataSet.getRow(currentCustomRecord);
			}
		}
		return dgr;
	}

	public final void setEditCustomRecord(Object id) {
		try {
			currentCustomRecord = (Integer) id;
		} catch (Exception e) {
			DisplayUtil.displayError("Can't edit custom record");
			logger.error(e.getMessage(), e);
		}
	}

	public final void setCreateCustomRecord(Object id) {
		try {
			logger.debug("Creating custom record");
			setNewCustomRecord(true);
			dataSet.createNewRow();
		} catch (Exception e) {
			DisplayUtil.displayError("Problem setting custom record");
			logger.error(e.getMessage(), e);
		}
	}

	public final boolean isNewRecord() {
		return newRecord;
	}

	public final boolean isNewCustomRecord() {
		return newCustomRecord;
	}

	public final void setNewRecord(boolean value) {
		if (value) {
			addNewRecord();
		}
		this.newRecord = value;
	}

	public final void setNewCustomRecord(boolean value) {
		this.newCustomRecord = value;
	}

	public final boolean isEditingRecord() {
		return editingRecord;
	}

	public final void setEditingRecord(boolean value) {
		this.editingRecord = value;
	}

	public final void setEditRecord(String id) {
		this.setDataDetailId(id);
		this.editingRecord = true;
	}

	public final boolean getAllowSystemFlush() {
		boolean allowFlush = false;
		if (selectedTreeNode != null) {
			DefinitionSet lds = defLoader.getDefSet(selectedTreeNode.getIdentifier());
			if (lds != null) {
				allowFlush = (lds.getImportClass() != null && lds
						.getImportClass().trim().length() > 0);
			}
		}
		return allowFlush;
	}

	public void addNewRecord() {
		// setNewRecord(true);
		data = new SimpleDef();
		String cd = ds.getColumnPrefix() + "_CD";
		String dsc = ds.getColumnPrefix() + "_DSC";
		data.setCodeColumn(cd);
		data.setDescriptionColumn(dsc);
		data.setTable(ds.getTable());
		try {
			data = infrastructureService.getSimpleDefMetaData(data);
		} catch (Exception e) {
			DisplayUtil.displayError("Error initializing new record screen");
			logger.error(e.getMessage(), e);
			setNewRecord(false);
		}
	}

	public final void saveCustom(ActionEvent actionEvent) {
		try {
			if (newCustomRecord) {
				if (dataSet.insert(getCustomRecord())) {
	                // update list system wide
	                flushSystem();
					
					DisplayUtil.displayInfo("Insert succeeded");
				} else {
					DisplayUtil.displayError("Insert failed");
				}
			} else {
				if (dataSet.update(getCustomRecord())) {
	                // update list system wide
	                flushSystem();
					
					DisplayUtil.displayInfo("Update succeeded");
				} else {
					DisplayUtil.displayError("Update failed");
				}
			}
			reset();

			dataSet.retrieveResultSet();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			DisplayUtil.displayError("Error retrieving records.");
		}

		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void save(ActionEvent actionEvent) {
		String cd = ds.getColumnPrefix() + "_CD";
		String dsc = ds.getColumnPrefix() + "_DSC";

		try {
			if (!newRecord) {
				if (ds.isUpdate()) {
					infrastructureService.modifyColumn(ds.getTable(), cd,
							"String", data.getCode(), dsc, "String",
							data.getDescription());
					if (ds.isDeprecate()) {
						if (data.isDeprecated()) {
							infrastructureService.modifyColumn(ds.getTable(), cd,
									"String", data.getCode(), "DEPRECATED",
									"String", "Y");
						} else {
							infrastructureService.modifyColumn(ds.getTable(), cd,
									"String", data.getCode(), "DEPRECATED",
									"String", "N");
						}
					}
				}

				// update list system wide
				flushSystem();

				DisplayUtil.displayInfo("Update succeeded");
			} else {
				saveNew();

				// update list system wide
				flushSystem();

				DisplayUtil.displayInfo("Insert succeeded");
			}
			loadSimpleDefData();
		} catch (RemoteException re) {
			DisplayUtil.displayError("Insert/Update failed");
			logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}
		// reset();
		// refresh our SimpleDef (detailData) list
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void flushSystem() {
		DefinitionSet lds = defLoader.getDefSet(selectedTreeNode.getIdentifier());
		String defClassName = lds.getImportClass();
		if (defClassName != null && defClassName.trim().length() > 0) {
			try {
				tryReload(lds, defClassName);
				if (defClassName.trim().equals(
						"us.oh.state.epa.stars2.def.PollutantDef")) {
					// reload all the def tables having to do with the same
					// pollutant table
					tryReload(lds,
							"us.oh.state.epa.stars2.def.NonToxicPollutantDef");
					tryReload(lds,
							"us.oh.state.epa.stars2.def.NspsPollutantDef");
					tryReload(lds, "us.oh.state.epa.stars2.def.NsrPollutantDef");
					tryReload(lds, "us.oh.state.epa.stars2.def.PsdPollutantDef");
				} else if (defClassName.trim().equals("us.oh.state.epa.stars2.def.ComplianceOtherTypeDef")) {
					// reload all the def tables having to do with the same compliance report categories table
					tryReload(lds, "us.oh.state.epa.stars2.def.ComplianceCemsTypeDef");
					tryReload(lds, "us.oh.state.epa.stars2.def.ComplianceOneTypeDef");
				} else if (defClassName.trim().equals("us.oh.state.epa.stars2.def.ComplianceReportAllSubAttachmentTypesDef")) {
					// reload all the def tables having to do with the same compliance report attachment types table
//					tryReload(lds, "us.oh.state.epa.stars2.def.ComplianceAttachmentCemsTypeDef");
//					tryReload(lds, "us.oh.state.epa.stars2.def.ComplianceAttachmentOneTypeDef");
//					tryReload(lds, "us.oh.state.epa.stars2.def.ComplianceAttachmentOtherTypeDef");					
					tryReload(lds, "us.oh.state.epa.stars2.def.ComplianceAttachmentTypeDef");
				} else if(defClassName.trim().equals("us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef")) {
					// reload NSRBillingChargePaymentTransactionTypeDef definition list 
					NSRBillingChargePaymentTypeDef.getChargePaymentTypeData().reload();
				}
			} catch (ClassNotFoundException e) {
				logger.error("Exception while reloading data for definition "
						+ lds.getLabel(), e);
			} catch (InstantiationException e) {
				logger.error("Exception while reloading data for definition "
						+ lds.getLabel(), e);
			} catch (IllegalAccessException e) {
				logger.error("Exception while reloading data for definition "
						+ lds.getLabel(), e);
			} catch (IllegalArgumentException e) {
				logger.error("Exception while reloading data for definition "
						+ lds.getLabel(), e);
			} catch (InvocationTargetException e) {
				logger.error("Exception while reloading data for definition "
						+ lds.getLabel(), e);
			}
		} else {
			DisplayUtil
					.displayError("Unable to load definition data to the system. Please contact System Administrator.");
			logger.error("No importClass attribute set for definition "
					+ lds.getLabel());
		}
	}

	public final void flushSystem(ActionEvent actionEvent) {
		flushSystem();
	}

	void tryReload(DefinitionSet lds, String defClassName)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, InvocationTargetException,
			ClassNotFoundException {
		Class<?> defClass = Class.forName(defClassName);
//		Method getDataMethod = null;
		Method[] methods = defClass.getMethods();
//		TFS Task - 5242
//		commented the below lines and added the ones after the comment
//		in order to refresh all the (sub)definition lists associated with
//		a given definition list class
//		for (Method method : methods) {
//			if ("getData".equals(method.getName())) {
//				getDataMethod = method;
//				break;
//			}
//		}
//		if (getDataMethod != null) {
//			Object defObject = defClass.newInstance();
//			Object defData = getDataMethod.invoke(defObject, (Object[]) null);
//			if (defData instanceof DefData) {
//				((DefData) defData).reload();
//			} else {
//				logger.error("Invalid importClass specified for definition "
//						+ lds.getLabel() + ". The getData method for class "
//						+ defClassName + " does not return DefData.");
//			}
//		} else {
//			logger.error("Invalid importClass specified for definition "
//					+ lds.getLabel() + ". The getData method for class "
//					+ defClassName + " is not defined.");
//		}
		ArrayList<Method> getDataMethods = new ArrayList<Method>();
		for (Method method : methods) {
			if(method.getName().endsWith("Data")) {
				getDataMethods.add(method);
			}
		}
		
		for(Method method : getDataMethods) {
			Object defObject = defClass.newInstance();
			Object defData = method.invoke(defObject, (Object[]) null);
			if (defData instanceof DefData) {
				((DefData) defData).reload();
			} else {
				logger.error("Invalid importClass specified for definition "
						+ lds.getLabel() + ". The " + method.getName() + " method for class "
						+ defClassName + " does not return DefData.");
			}
		}
	}

	public final void saveNew() {
		String cd = ds.getColumnPrefix() + "_CD";
		String dsc = ds.getColumnPrefix() + "_DSC";

		try {
			logger.debug("Adding new simpledef. " + ds.getTable() + " " + cd
					+ " " + dsc);
			if (newRecord) {
				infrastructureService
						.createSimpleDef(ds.getTable(), cd, dsc, data);
				setNewRecord(false);
				setNewCustomRecord(false);
				setEditingRecord(false);
				logger.debug("New record saved");
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("Insert failed. " + re);
			logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}
	}

	public final void saveNewCustom() {
		logger.debug("Saving new custom...");
		// dataset.insert(getCustomRecord());
		reset();
	}

	public final String getDataDetailId() {
		return dataDetailId;
	}

	public final void setDataDetailId(String dataDetailId) {
		data = details.get(dataDetailId);
		this.dataDetailId = dataDetailId;
	}

	public final SimpleDef getData() {
		return data;
	}

	public final void setData(SimpleDef data) {
		this.data = data;
	}

	public boolean isRetrieveError() {
		return retrieveError;
	}

	public void setRetrieveError(boolean retrieveError) {
		this.retrieveError = retrieveError;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();

//		dataSet = new DataSet();
	}

	public List<DefCatalog> getSubDefCatalogList() {
		return subDefCatalogList;
	}

	public void setSubDefCatalogList(List<DefCatalog> subDefCatalogList) {
		this.subDefCatalogList = subDefCatalogList;
		
		if(null != this.subDefCatalogList) {
			for(DefCatalog defCatalog : this.subDefCatalogList) {
				DefinitionsLoader defLoader = defCatalog.getDefLoader();
				if(null != defLoader 
						&& !Utility.isNullOrEmpty(defLoader.getDefConfigNode())) {
					logger.debug("Adding defnitions from config node " + defLoader.getDefConfigNode());
					DefinitionCategory defCategory = defLoader.getRootCategory();
					
					// start adding the sub-categories present under the root category
					Collection<DefinitionCategory> subCategories = defCategory.getSubCategories().values();
					for(DefinitionCategory category : subCategories) {
						logger.debug("Adding sub-category: " + category.getLabel());
						rootCategory.addCategory(category);
					}
				}
			}
		}
	}
}
