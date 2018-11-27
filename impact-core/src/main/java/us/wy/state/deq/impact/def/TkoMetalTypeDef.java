package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TkoMetalTypeDef {
	
	//****************** Variables ******************* 
    private static final String defName = "TkoMetalTypeDef";
    private static final String tableName = "CM_TKO_METAL_TYPE_DEF";
    private static final String keyFieldName = "TYPE_CD";
    private static final String valueFieldName = "TYPE_DSC";
    private static final String deprecatedFieldName = "DEPRECATED";
    private static final String sortOrder = "SORT_ORDER";   

    
	//****************** Public Static Methods *******************
    public static DefData getData() {
    	
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB(tableName,
                    keyFieldName, valueFieldName,
                    deprecatedFieldName,sortOrder);

            cfgMgr.addDef(defName, data);
        }
        return data;
        
    }

}

