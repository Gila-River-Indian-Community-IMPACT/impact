package us.oh.state.epa.stars2.def;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class MaterialDef {
    private static final String defName = "MaterialDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("rp_material_def", "material_cd", "material_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static List<SelectItem> getAllMaterials() {
        return getData().getItems().getAllItems();
    }
    
	public static List<SelectItem> getSccMaterials(String sccId) {

		List<SelectItem> materials = new ArrayList<SelectItem>();

		if (Utility.isNullOrEmpty(sccId)) {
			return getAllMaterials();
		}

		try {

			SimpleDef[] tempArray = null;
			tempArray = ServiceFactory.getInstance().getEmissionsReportService()
					.retrieveSccMaterials(sccId);

			for (SimpleDef tempState : tempArray) {
				SelectItem si = new SelectItem(tempState.getCode(),
						tempState.getDescription());
				materials.add(si);
			}

		} catch (ServiceFactoryException sfe) {
			// logger.error(sfe.getMessage(), sfe);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		} catch (RemoteException re) {
			// logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}

		return materials;
	}
	
	public static Object getMaterialCode(String itemDesc) {
		Object ret = null;
		if(!Utility.isNullOrEmpty(itemDesc)) {
			ret = getData().getItems().getItemCode(itemDesc);
		}
		
		return ret;
	}
}
