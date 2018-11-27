package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Control Equipment Operating Status */

public class PollutantCategoryDef {
  
    private static final String defName = "PollutantCategoryDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_pollutant_category_def", "category",
                    "category_dsc", "DEPRECATED", "category");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
