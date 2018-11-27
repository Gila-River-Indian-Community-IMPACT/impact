package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class TVCompRptFreqDef {
    private static final String defName = "TVCompRptFreqDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_TV_COMPRPT_FREQ_DEF", "TV_COMPRPT_FREQ_CD",
                    "TV_COMPRPT_FREQ_DSC", "deprecated", "TV_COMPRPT_FREQ_DSC");

            cfgMgr.addDef(defName, data);
        }
        
        return data;
    }
}
