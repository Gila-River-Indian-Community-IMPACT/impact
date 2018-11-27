package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TvRestrictionTypeDef {
    private static final String defName = "TvRestrictionTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_TV_RESTRICTION_TYPE_DEF", "REST_TYPE_CD",
                    "REST_TYPE_DSC", "DEPRECATED", "REST_TYPE_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
