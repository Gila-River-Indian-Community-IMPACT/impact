package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class RelocationJFODef {
    public static final String APPROVE = "appr";
    public static final String APPROVE_WITH_CONDITIONS = "awc";
    public static final String DENY = "deny";
    private static final String defName = "RelocationJFODef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_RELOCATION_JFO_DEF", "JFO_RECOMMENDATION_CD",
                    "JFO_RECOMMENDATION_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}

