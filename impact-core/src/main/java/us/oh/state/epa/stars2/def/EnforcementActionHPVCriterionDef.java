package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnforcementActionHPVCriterionDef {
	private static final String defName = "EnforcementActionHPVCriterionDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("CE_ENFORCEMENT_ACTION_HPV_CRITERION_DEF",
					"ENFORCEMENT_ACTION_HPV_CRITERION_CD",
					"ENFORCEMENT_ACTION_HPV_CRITERION_DSC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}

