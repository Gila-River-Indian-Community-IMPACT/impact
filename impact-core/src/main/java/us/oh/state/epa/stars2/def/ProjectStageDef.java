package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ProjectStageDef {
	
	public static final String defName = "ProjectStageDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_PROJECT_STAGE_DEF", "PROJECT_STAGE_CD",
					"PROJECT_STAGE_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
