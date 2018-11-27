package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class DefaultFacilityRoleTypeDef {

private static final String defName = "DefaultFacilityRoleTypeDef";
    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
			data.loadFromDB("CM_DEFAULT_FACILITY_ROLE_DEF", "COUNTY_CD", 
							"FACILITY_ROLE_CD", "USER_ID");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
