package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PAEuPTEUnitsDef {
	
	//****************** Variables ******************* 	
    private static final String defName = "PaEuPteUnitsDef";
    private static final String tableName = "PA_EU_PTE_UNITS_DEF";
    private static final String keyFieldName = "UNIT_CD";
    private static final String valueFieldName = "UNIT_DSC";
    private static final String deprecatedFieldName = "DEPRECATED";


	//****************** Public Static Methods *******************
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB(tableName,
                    keyFieldName, valueFieldName,
                    deprecatedFieldName);

            cfgMgr.addDef(defName, data);
        }
        return data;
    } 
}
