package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;





public class FacilityOnlyRoleDef extends SimpleDef {
    private static final String defName = "FacilityOnlyRoleDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FacilitySQL.retrieveFacilityOnlyRoleDefs", FacilityOnlyRoleDef.class); 

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
 
}
