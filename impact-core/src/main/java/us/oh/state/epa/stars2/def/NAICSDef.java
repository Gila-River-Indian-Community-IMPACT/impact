package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class NAICSDef {
    private static final String defName = "NAICSDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_naics_def", "naics_cd", "naics_dsc", "deprecated", "naics_cd");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }

}
