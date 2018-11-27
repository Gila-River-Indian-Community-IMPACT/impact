package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PortableGroupTypes {
    private static final String defName = "PortableGroupTypes";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_portable_group_type", "portable_group_type_cd",
                    "portable_group_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
