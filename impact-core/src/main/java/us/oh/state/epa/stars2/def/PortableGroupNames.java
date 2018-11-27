package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PortableGroupNames {
    private static final String defName = "PortableGroupNames";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_portable_group_def", "portable_group_cd",
                    "portable_group_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
