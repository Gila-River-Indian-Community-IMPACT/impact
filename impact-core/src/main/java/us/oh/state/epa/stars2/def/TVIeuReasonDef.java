package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class TVIeuReasonDef {
    private static final String defName = "TVIeuReasonDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_TV_IEU_REASON_DEF", "TV_IEU_REASON_CD",
                    "TV_IEU_REASON_DSC", "deprecated", "SORT_ORDER");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
