package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PT_ISSUANCE_STATUS_DEF table.
 */
public class IssuanceStatusDef {
    public static final String NOT_READY = "N";
    public static final String READY = "R";
    public static final String ISSUED = "I";
    public static final String SKIPPED = "S";
    private static final String defName = "IssuanceStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PT_ISSUANCE_STATUS_DEF", "ISSUANCE_STATUS_CD",
                            "ISSUANCE_STATUS_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }

}
