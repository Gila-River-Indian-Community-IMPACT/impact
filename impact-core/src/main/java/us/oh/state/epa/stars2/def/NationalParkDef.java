package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class NationalParkDef {
	
public static final String defName = "NationalParkDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_NATIONAL_PARK_DEF", "NATIONAL_PARK_CD",
					"NATIONAL_PARK_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
