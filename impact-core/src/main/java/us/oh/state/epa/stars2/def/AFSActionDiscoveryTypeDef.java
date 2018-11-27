package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AFSActionDiscoveryTypeDef {
	public static final String FCE = "F";
	public static final String PCE_OTHER = "P";
	public static final String TITLE_V_ANNUAL_COMPLIANCE_CERTIFICATIONS = "T";
	public static final String STACK_TEST = "S";
	public static final String OFF_SITE_HPV = "O";
	public static final String SITE_VISIT = "P";
	private static final String defName = "AFSActionDiscoveryTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("CE_ENF_DISCOVERY_TYPE_DEF",
					"ENF_DISCOVERY_TYPE_CD", "ENF_DISCOVERY_TYPE_DSC",
					"DEPRECATED");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
