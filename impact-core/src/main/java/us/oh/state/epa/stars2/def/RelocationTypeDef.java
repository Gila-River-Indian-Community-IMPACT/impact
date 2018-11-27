package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class RelocationTypeDef {
    public static final String RELOCATE_TO_PREAPPROVED_SITE = "RPS";
    public static final String SITE_PRE_APPROVAL = "SPA";
    public static final String INTENT_TO_RELOCATE = "ITR";
    private static final String defName = "RelocationTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_RELOCATION_TYPE_DEF", "REQUEST_TYPE_CD",
                    "REQUEST_TYPE_DSC", "deprecated", "REQUEST_TYPE_DSC");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}

