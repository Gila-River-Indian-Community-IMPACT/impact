package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ProjectLetterTypeDef {
	
	public static final String defName = "ProjectLetterTypeDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_LETTER_TYPE_DEF", "LETTER_TYPE_CD",
					"LETTER_TYPE_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
