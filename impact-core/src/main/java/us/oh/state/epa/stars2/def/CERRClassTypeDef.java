package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CERRClassTypeDef {

private static final String defName = "CERRClassTypeDef";
    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
			data.loadFromDB("FP_CERR_CLASS_DEF", "CERR_CLASS_CD", 
							"CERR_CLASS_DESC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
