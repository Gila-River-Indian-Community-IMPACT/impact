package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class BLMFieldOfficeDef {
	
	public static final String defName = "BLMFieldOfficeDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_BLM_FIELD_OFFICE_DEF", "FIELD_OFFICE_CD",
					"FIELD_OFFICE_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
