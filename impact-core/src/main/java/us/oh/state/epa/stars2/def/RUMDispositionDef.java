package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class RUMDispositionDef {
    private static final String DISPOSITION_UNDELIVERED = "und";
    private static final String DISPOSITION_COMPLETED_RESENT = "cr";
    private static final String DISPOSITION_COMPLETED_NOT_RESENT = "cnr";
    private static final String defName = "RUMDispositionDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(DISPOSITION_UNDELIVERED, "Undelivered");
            data.addItem(DISPOSITION_COMPLETED_RESENT, "Completed - Resent");
            data.addItem(DISPOSITION_COMPLETED_NOT_RESENT,
                    "Completed - Not Resent");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
