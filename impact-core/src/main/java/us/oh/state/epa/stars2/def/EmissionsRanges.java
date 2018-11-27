package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EmissionsRanges {
    private static final String defName = "EmissionsRanges";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("rp_emissions_range_def", "emissions_range_cd",
                    "emissions_range_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
