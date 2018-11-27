package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CountyOzoneAttainmentTypeDef {
	private static final String defName = "CountyOzoneAttainmentTypeDef";
	    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
			data.loadFromDB("CM_COUNTY_COMPLIANCE", "YEAR", 
							"COUNTY_CD", "IN_COMPLIANCE");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}