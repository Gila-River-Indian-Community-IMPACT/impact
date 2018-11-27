package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class NEPALevelTypeDef {

	public static final String defName = "NEPALevelTypeDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_NEPA_LEVEL_DEF", "LEVEL_CD",
					"LEVEL_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
