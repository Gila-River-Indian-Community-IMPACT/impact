package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class DesignCapacityUnitsDef {
    public static final String NA = "na";
    private static final String defName = "DesignCapacityUnitsDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_design_capacity_units_def",
                    "design_capacity_units_cd", "design_capacity_units_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
