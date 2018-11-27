package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ShapeDef {
	
	public static final String defName = "ShapeDef";
	
	public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
			data.loadFromDB("InfrastructureSQL.retrieveShapeDefs", SimpleDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
