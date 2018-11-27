package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class RPRReasonDef {
    public static final String PBR = "PBR";
    public static final String EXEMPT = "EX";
    public static final String DE_MINIMIS = "DM";
    public static final String SHUTDOWN = "SD";
    public static final String ENFORCEMENT = "EF";
    public static final String OTHER = "OT";
    private static final String defName = "RPRReasonDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_RPR_REASON_DEF", "RPR_REASON_CD",
                            "RPR_REASON_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public static boolean isValid(String rprReasonCd) {

        if (getData().getItems() != null 
            && getData().getItems().getItemDesc(rprReasonCd) != null) {
            return true;
        }
        return false;
    }
}
