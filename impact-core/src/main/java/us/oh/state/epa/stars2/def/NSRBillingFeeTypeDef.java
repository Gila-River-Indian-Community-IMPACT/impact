package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class NSRBillingFeeTypeDef {
	public static String INITIAL_INVOICE = "II";
	public static String FINAL_INVOICE = "FI";
	public static String SPECIAL_INVOICE = "SI";
	
	private static final String defName = "NSRBillingFeeTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("NSR_BILLING_FEE_TYPE_DEF", "CODE", "DESCRIPTION", "DEPRECATED", "DESCRIPTION");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
