package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ProjectGrantStatusDef {
	
	public static final String defName = "ProjectGrantStatusDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_GRANT_STATUS_DEF", "GRANT_STATUS_CD",
					"GRANT_STATUS_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}


}
