package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AppEUProductionHourRateDef {

	private static final String defName = "AppEUProductionHourRateDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PA_EU_PRODUCTION_HOUR_RATE_DEF", "type_cd", "type_dsc",
					"deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
