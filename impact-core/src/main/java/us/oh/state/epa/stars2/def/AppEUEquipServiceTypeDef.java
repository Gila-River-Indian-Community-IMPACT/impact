package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AppEUEquipServiceTypeDef {

	private static final String defName = "AppEUEquipServiceTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		
			data = new DefData();
			data.loadFromDB("PA_EU_EQUIP_SERVICE_TYPE_DEF", "type_cd",
					"type_dsc", "deprecated");

			cfgMgr.addDef(defName, data);
		
		return data;
	}
}
