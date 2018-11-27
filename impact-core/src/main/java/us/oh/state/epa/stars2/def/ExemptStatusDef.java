package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
public class ExemptStatusDef {
    public final static String NA = "na";
    public final static String DE_MINIMIS = "dem";
    public final static String EXEMPT = "exm";
    private static final String defName = "ExemptStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_EXEMPT_STATUS", "EXEMPT_STATUS_CD",
                    "EXEMPT_STATUS_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
