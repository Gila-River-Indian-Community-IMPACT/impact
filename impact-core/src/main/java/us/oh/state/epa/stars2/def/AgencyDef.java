package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AgencyDef {
	public static final String defName = "AgencyDef";
	
	public static final String BLM = "BLM";
	public static final String NATIONAL_PARK_SERVICE = "NPS";
	public static final String US_FOREST_SERVICE = "UFS";
	public static final String FWS = "FWS";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_AGENCY_DEF", "AGENCY_CD",
					"AGENCY_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
