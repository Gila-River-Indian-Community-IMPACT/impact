package us.oh.state.epa.stars2.app.admin;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.def.DataTypeDef;
import us.oh.state.epa.stars2.def.EnumDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.wy.state.deq.impact.def.CeDataDetailDef;

@SuppressWarnings("serial")
public class ControlEquipmentCatalog extends TreeBase {
    private boolean newCeType;
    private boolean editingCE;
    private boolean editingAttribute;
    private SimpleDef[] ceTypes;
    private SimpleDef ceType;
    private DataDetail data = new DataDetail();
    private Integer dataDetailId;
    private HashMap<Integer, DataDetail> details;
    
	private InfrastructureService infrastructureService;
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public ControlEquipmentCatalog() {
        super();
    }
    

    public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}


	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}


	@Override
    public final void setSelectedTreeNode(TreeNode selectedTreeNode) {
        reset();

        this.ceType = new SimpleDef();
        this.ceType.setCode(selectedTreeNode.getIdentifier());
        this.ceType.setDescription(selectedTreeNode.getDescription());
        this.selectedTreeNode = selectedTreeNode;
    }

    public final DataDetail[] getDetailData() {

        DataDetail[] dataDetails = null;
        details = new HashMap<Integer, DataDetail>();

        try {
            dataDetails = getFacilityService().retrieveContEquipDataDetail(
                    selectedTreeNode.getIdentifier());

            for (DataDetail tempDetail : dataDetails) {
                details.put(tempDetail.getDataDetailId(), tempDetail);

            }
        } catch (RemoteException re) {
            DisplayUtil
                    .displayError("Accessing control equipment data details failed");
            logger.error(re.getMessage(), re);
        }
        return dataDetails;
    }

    @SuppressWarnings("unchecked")
    public final TreeModelBase getTreeData() {
        if (treeData == null) {
            TreeNodeBase root = new TreeNodeBase("root",
                    "ControlEquipmentCatalog", "root", false);
            treeData = new TreeModelBase(root);
            ArrayList<String> treePath = new ArrayList<String>();
            treePath.add("0");

            try {
                ceTypes = infrastructureService.retrieveSimpleDefs(
                        "fp_equipment_type", "equipment_type_cd",
                        "equipment_type_dsc", "deprecated", null);

                int ceTypeNum = 0;

                for (SimpleDef tempCeTypeDef : ceTypes) {
                    treePath.add("0:" + Integer.toString(ceTypeNum++));

                    TreeNodeBase ceTypeNode = new TreeNodeBase(
                            "controlEquipment", tempCeTypeDef.getDescription(),
                            tempCeTypeDef.getCode(), false);
                    root.getChildren().add(ceTypeNode);
                }

            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }

            TreeStateBase treeState = new TreeStateBase();

            treeState.expandPath(treePath.toArray(new String[0]));
            treeData.setTreeState(treeState);
            selectedTreeNode = root;
            current = "root";
        }
        return treeData;
    }

    public final String reset() {
        setNewCeType(false);
        setEditingAttribute(false);
        setEditingCE(false);
        data = new DataDetail();

        return CANCELLED;
    }

    public final void cancelAddContAttribute() {
        reset();
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final void dialogDone() {
        return;
    }

    public final void applyAddAttribute() {
        try {
            if (editingAttribute) {
                getFacilityService().modifyEquipDetailDef(data, ceType.getCode());
            } else {
                data.setVisible(true);
                getFacilityService().createEquipDetailDef(data, ceType.getCode());
            }
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        reset();
        FacesUtil.returnFromDialogAndRefresh();
    }

    public final String addContEquip() {
        return "ContEquipAdded";
    }

    public final String saveContEquip() {
        return "ContEquipSaved";
    }

    public final String editContEquip() {
        setEditingCE(true);
        return "ContEquipEditable";
    }

    public final boolean isNewCeType() {
        return newCeType;
    }

    public final void setNewCeType(boolean value) {
        this.newCeType = value;
    }

    public final boolean isEditingCE() {
        return editingCE;
    }

    public final void setEditingCE(boolean editingCE) {
        this.editingCE = editingCE;
    }

    public final SimpleDef getCeType() {
        return ceType;
    }

    public final String addNewCeType() {
        setNewCeType(true);

        ceType = new SimpleDef();

        return "CeTypeAdded";
    }

    public final String saveContEquipType() {

        try {
            if (newCeType) {
                infrastructureService.createSimpleDef("fp_equipment_type",
                        "equipment_type_cd", "equipment_type_dsc", ceType);
            } else {
                infrastructureService.modifySimpleDef("fp_equipment_type",
                        "equipment_type_cd", "equipment_type_dsc",
                        "deprecated", ceType);
            }
            CeDataDetailDef.forceRefresh();
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        reset();

        treeData = null;

        return "CeTypeSaved";
    }

    public final Integer getDataDetailId() {
        return dataDetailId;
    }

    public final void setDataDetailId(Integer dataDetailId) {
        data = details.get(dataDetailId);
        setEditingAttribute(true);
        this.dataDetailId = dataDetailId;
    }

    public final DataDetail getData() {
        return data;
    }

    public final void setData(DataDetail data) {
        this.data = data;
    }

    public final List<SelectItem> getDataTypes() {
        return DataTypeDef.getData().getItems().getItems(dataDetailId, true);
    }

    public final List<SelectItem> getEnumTypes() {
        return EnumDef.getData().getItems().getItems(data.getEnumCd(), true);
    }

    public final boolean isGotEnum() {
        // Check for Enum datatype
        boolean ret = false;

        if (data.getDataTypeId() != null && data.getDataTypeId() == 5) {
            ret = true;
        }

        return ret;
    }

    public final boolean isEditingAttribute() {
        return editingAttribute;
    }

    public final void setEditingAttribute(boolean editingAttribute) {
        this.editingAttribute = editingAttribute;
    }
}
