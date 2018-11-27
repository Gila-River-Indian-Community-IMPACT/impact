package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class FieldAuditLogAttributeDef {
    private static final String defName = "FieldAuditLogAttributeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fl_attribute_category_def", "attribute_cd",
                    "attribute_dsc", "deprecated", "attribute_dsc");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
