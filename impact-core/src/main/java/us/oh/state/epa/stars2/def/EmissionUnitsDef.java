package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class EmissionUnitsDef {
    private static final String defName = "EmissionUnitsDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_EMISSION_UNITS_DEF", "EMISSION_UNITS_CD",
                    "EMISSION_UNITS_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
