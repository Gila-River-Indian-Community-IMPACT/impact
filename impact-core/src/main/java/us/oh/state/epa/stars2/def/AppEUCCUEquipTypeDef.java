package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AppEUCCUEquipTypeDef {

	public static final String FLUID_CATALYTIC_CRACKING = "1";
	public static final String HYDRO_CRACKING = "2";
	public static final String DELAYED_COKING = "3";
	public static final String OTHER = "4";
	
	private static final String defName = "AppEUCCUEquipTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PA_EU_CCU_EQUIP_TYPE_DEF", "type_cd", "type_dsc",
					"deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}