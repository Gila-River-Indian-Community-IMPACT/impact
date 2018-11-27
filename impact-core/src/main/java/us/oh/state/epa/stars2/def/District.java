package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class District {
    private static final String defName = "District";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_do_laa_def", "do_laa_cd", "do_laa_dsc", null);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
