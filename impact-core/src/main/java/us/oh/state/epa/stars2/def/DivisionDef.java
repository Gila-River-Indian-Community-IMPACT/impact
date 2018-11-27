package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class DivisionDef {
	public static final String defName = "DivisionDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_DIVISION_DEF", "DIVISION_CD",
					"DIVISION_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
