package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EmissionsRptIntervalTypeDef {

private static final String defName = "EmissionsRptIntervalTypeDef";
    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
			data.loadFromDB("FP_EMISSIONS_RPT_INTERVAL_DEF", "EMISSIONS_RPT_INTERVAL_CD", 
							"EMISSIONS_RPT_INTERVAL_DESC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
