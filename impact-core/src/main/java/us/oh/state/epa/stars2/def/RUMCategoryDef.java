package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class RUMCategoryDef {
    private static final String defName = "RUMCategoryDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("DC_CORRESPONDENCE_CATEGORY_DEF",
                    "CORRESPONDENCE_CATEGORY_CD",
                    "CORRESPONDENCE_CATEGORY_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
