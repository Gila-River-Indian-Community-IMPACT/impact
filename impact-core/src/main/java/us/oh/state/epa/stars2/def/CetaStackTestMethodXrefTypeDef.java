package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CetaStackTestMethodXrefTypeDef {

	private static final String defName = "CetaStackTestMethodXrefTypeDef";
    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
			data.loadFromDB("CE_STACK_TEST_METHOD_POLL_XREF", "STACK_TEST_METHOD_CD", 
							"POLLUTANT_CD", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
