package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class DesignCapacityDef {
    public static String NA = "na";
    public static String BO = "bo";
    public static String TU = "tu";
    public static String GE = "ge";
    private static final String defName = "DesignCapacityDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_design_capacity_def", "design_capacity_cd",
                    "design_capacity_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
