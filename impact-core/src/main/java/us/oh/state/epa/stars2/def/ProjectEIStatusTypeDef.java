package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ProjectEIStatusTypeDef {
	
	public static final String defName = "ProjectEIStatusTypeDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_PROJECT_EI_STATUS_DEF", "STATUS_CD",
					"STATUS_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
