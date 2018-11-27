package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnforcementActionFRVTypeDef {
	private static final String defName = "EnforcementActionFRVTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("CE_ENFORCEMENT_ACTION_FRV_TYPE_DEF",
					"ENFORCEMENT_ACTION_FRV_TYPE_CD",
					"ENFORCEMENT_ACTION_FRV_TYPE_DSC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}

