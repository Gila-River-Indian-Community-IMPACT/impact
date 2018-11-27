package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class InspectionClassTypeDef {

private static final String defName = "InspectionClassTypeDef";
    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
			data.loadFromDB("FP_INSPECTION_CLASS_DEF", "INSPECTION_CLASS_CD", 
							"INSPECTION_CLASS_DESC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}