package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class NationalForestDef {
	
	public static final String defName = "NationalForestDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_NATIONAL_FOREST_DEF", "NATIONAL_FOREST_CD",
					"NATIONAL_FOREST_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
