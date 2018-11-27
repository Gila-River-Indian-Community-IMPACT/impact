package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
public class TransitStatusDef {
    public static String GOING_TO_TV = "mtv";
    public static String NONE = "none";
    private static final String defName = "TransitStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_TRANSITIONAL_STATUS_DEF",
                            "TRANSITIONAL_STATUS_CD", "TRANSITIONAL_STATUS_DSC",
                            "deprecated");
            
            cfgMgr.addDef(defName, data);
        }

        return data;
    }
}
