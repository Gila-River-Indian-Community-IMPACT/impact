package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AppEUStockpileSizeDef {

	private static final String defName = "AppEUStockpileSizeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PA_EU_STOCKPILE_SIZE_DEF", "type_cd", "type_dsc",
					"deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}