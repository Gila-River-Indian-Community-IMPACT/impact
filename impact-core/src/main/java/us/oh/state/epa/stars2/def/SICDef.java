package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class SICDef {
    private static final String defName = "SICDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_sic_def", "sic_cd", "sic_dsc", "deprecated", "sic_cd");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
