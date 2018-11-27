package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class FceInspectionTypeDef {
	private static final String defName = "FceInspectionTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_FCE_INSPECTION_TYPE_DEF", "fce_inspection_type_cd", 
            		"fce_inspection_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}