package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class PBRReasonDef {
    public static final String INITIAL = "I";
    public static final String EQUIPMENT_MODIFICATION = "EM";
    public static final String OWNERSHIP_CHANGE = "OC";
    public static final String UNKNOWN = "U";
    public static final String REVISION_CORRECTION = "RC";
    private static final String defName = "PBRReasonDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PBR_REASON_DEF",
                            "pbr_reason_cd", "pbr_reason_dsc",
                            "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public static boolean isValid(String pbrReasonCd) {

        if (getData().getItems() != null 
            && getData().getItems().getItemDesc(pbrReasonCd) != null) {
            return true;
        }
        return false;
    }

}
