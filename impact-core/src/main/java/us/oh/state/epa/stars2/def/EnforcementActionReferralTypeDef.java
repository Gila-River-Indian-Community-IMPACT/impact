package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnforcementActionReferralTypeDef {
	private static final String defName = "EnforcementActionReferralTypeDef";
	
	public static final String FACILITY_INSPECTION = "FI";
	public static final String COMPLAINT_INVESTIGATION = "CI";
	public static final String STACK_TEST = "ST";
	public static final String ENGINE_TESTING = "EI";
	public static final String OPERATING_WITHOUT_PERMIT = "OWP";
	public static final String OTHER = "OTH";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("CE_ENFORCEMENT_ACTION_REFERRAL_TYPE_DEF", "ENFORCEMENT_ACTION_REFERRAL_TYPE_CD",
					"ENFORCEMENT_ACTION_REFERRAL_TYPE_DSC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
