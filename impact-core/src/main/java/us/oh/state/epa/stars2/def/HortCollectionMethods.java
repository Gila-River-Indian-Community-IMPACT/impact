package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class HortCollectionMethods {
    public static final String GPS = "028";
    private static final String defName = "HortCollectionMethods";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_hort_collection_method",
                    "hort_collection_method_cd", "hort_collection_method_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
