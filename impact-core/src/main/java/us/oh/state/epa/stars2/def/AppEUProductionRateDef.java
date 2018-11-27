package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AppEUProductionRateDef {

	private static final String defName = "AppEUProductionRateDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PA_EU_PRODUCTION_RATE_DEF", "type_cd", "type_dsc",
					"deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
