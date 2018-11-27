package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EventTypeDef {
private static final String defName = "EventTypeDef";
    
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
			data.loadFromDB("FP_EVENT_TYPE_DEF", "EVENT_TYPE_CD", 
							"EVENT_TYPE_DSC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
