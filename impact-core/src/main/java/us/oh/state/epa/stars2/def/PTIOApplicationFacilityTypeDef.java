package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_PTIO_APP_FACILITY_TYPE_DEF table
 */
public class PTIOApplicationFacilityTypeDef {
    private static final String defName = "PTIOApplicationFacilityTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PTIO_APP_FACILITY_TYPE_DEF", "PTIO_APP_FACILITY_TYPE_CD",
                    "PTIO_APP_FACILITY_TYPE_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}