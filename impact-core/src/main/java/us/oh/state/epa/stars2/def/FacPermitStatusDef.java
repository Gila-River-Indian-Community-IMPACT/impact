package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class FacPermitStatusDef {
    public static final String NA = "N";
    public static final String ACTIVE = "A";
    public static final String EXTENDED = "EX";
    public static final String EXPIRED = "E";
    public static final String PENDING_INITIAL = "PI";
    private static final String defName = "FacPermitStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_permit_status_def", "permit_status_cd",
                            "permit_status_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public static boolean isValid(String permitStatusCd) {
        if (getData().getItems() != null 
            && getData().getItems().getItemDesc(permitStatusCd) != null) {
            return true;
        }
        return false;
    }
}