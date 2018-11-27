package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class FieldAuditLogCategoryDef {
    private static final String defName = "FieldAuditLogCategoryDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveFldAudLogCategories",
                    SimpleDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
