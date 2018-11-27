package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MaterialIOTypeDef {

private static final String defName = "MaterialIOTypeDef";
    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
			data.loadFromDB("CM_MATERIAL_IO_DEF", "MATERIAL_IO_CD", 
							"MATERIAL_IO_DSC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
