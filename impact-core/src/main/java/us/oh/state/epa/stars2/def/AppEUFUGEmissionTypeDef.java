package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AppEUFUGEmissionTypeDef {
	public static final String HAUL_ROAD = "1";
	public static final String EXPOSED_ACREAGE = "2";
	public static final String STOCKPILE = "3";
	public static final String BLASTING = "4";
	public static final String FIGUTIVE_LEAK_AT_OG = "5";
	public static final String OTHER = "6";
	private static final String defName = "AppEUFUGEmissionTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PA_EU_FUG_EMISSION_TYPE_DEF", "type_cd",
					"type_dsc", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}