package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class SiteVisitNoEmissionTestTypeDef {
    private static final String defName = "SiteVisitNoEmissionTestTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ce_site_visit_type_def", "site_visit_type_cd",
                    "site_visit_type_dsc", "DEPRECATED");
            data.addExcludedKey(SiteVisitTypeDef.STACK_TEST);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
