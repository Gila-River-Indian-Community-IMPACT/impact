package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ProjectOutreachCategoryDef {
	
	public static final String defName = "ProjectOutreachCategoryDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_OUTREACH_CATEGORY_DEF", "OUTREACH_CATEGORY_CD",
					"OUTREACH_CATEGORY_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
