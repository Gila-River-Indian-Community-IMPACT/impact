package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class SiteVisitVisibleEmissionsDef {
    private static final String defName = "SiteVisitVisibleEmissionsDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_SITE_VISIT_VE_DEF", "SITE_VISIT_VE_CD","SITE_VISIT_VE_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
