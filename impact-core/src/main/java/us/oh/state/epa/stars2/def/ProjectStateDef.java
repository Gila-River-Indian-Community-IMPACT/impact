package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ProjectStateDef {
	
	private static final String defName = "ProjectStateDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_PROJECT_STATE_DEF", "PROJECT_STATE_CD",
					"PROJECT_STATE_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
