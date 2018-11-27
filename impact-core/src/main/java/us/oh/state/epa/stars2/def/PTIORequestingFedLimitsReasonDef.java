package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_FEDERAL_LIMITS_REASON_DEF table
 */
public class PTIORequestingFedLimitsReasonDef {
    public static final String AVOID_BEING_MAJOR_TV_SOURCE = "0";
    public static final String AVOID_BEING_MAJOR_GHG_SOURCE = "01";
    public static final String AVOID_BEING_MAJOR_MACT_SOURCE = "1";
    public static final String AVOID_BEING_MAJOR_STATIONARY_SOURCE = "2";
    public static final String AVOID_BEING_MAJOR_MODIFICATION = "3";
    public static final String AVOID_AIR_DISP_MODEL_REQUIREMNTS = "4";
    public static final String AVOID_BAT_REQUIREMENTS = "5";
    public static final String OTHER = "6";
    private static final String defName = "PTIORequestingFedLimitsReasonDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_FEDERAL_LIMITS_REASON_DEF",
                    "FEDERAL_LIMITS_REASON_CD", "FEDERAL_LIMITS_REASON_DSC",
                    "deprecated", "FEDERAL_LIMITS_REASON_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
