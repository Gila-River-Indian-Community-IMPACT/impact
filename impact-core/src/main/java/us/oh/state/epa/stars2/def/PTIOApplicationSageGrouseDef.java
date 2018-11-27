package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_PTIO_APP_PURPOSE_DEF table
 */
public class PTIOApplicationSageGrouseDef {
    public static final String CHECK_COMPLETED_BY_ANOTHER_AGENCY = "0";
    public static final String CHECK_COMPLETED_BY_AQD = "1";
    private static final String defName = "PTIOApplicationSageGrouseDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PTIO_APP_SAGEGROUSE_DEF", "PTIO_APP_SAGEGROUSE_CD",
                    "PTIO_APP_SAGEGROUSE_DSC", "deprecated", "PTIO_APP_SAGEGROUSE_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
