package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CorrespondenceCategoryDef {
    private static final String defName = "CorrespondenceCategoryDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("dc_correspondence_form_category_def", "correspondence_category_cd",
                    "correspondence_category_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
