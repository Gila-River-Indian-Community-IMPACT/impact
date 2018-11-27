package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class RUMReasonDef {
    private static final String defName = "RUMReasonDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("DC_UNDELIVERED_REASON_DEF",
                    "UNDELIVERED_REASON_CD", "UNDELIVERED_REASON_DSC",
                    "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
