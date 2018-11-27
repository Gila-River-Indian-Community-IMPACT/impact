package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class OEPAViolationTypeDef {
    public static final String EMISSION = "E";
    public static final String ADMINISTRATION = "A";
    private static final String defName = "OEPAViolationTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_ENF_VIOLATION_TYPE_DEF", "ENF_VIOLATION_TYPE_CD",
                    "ENF_VIOLATION_TYPE_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
