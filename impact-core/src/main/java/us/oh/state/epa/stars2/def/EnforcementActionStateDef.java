package us.oh.state.epa.stars2.def;
/*  Removed enforcement action state in IMPACT 7.5

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnforcementActionStateDef {
	private static final String defName = "EnforcementActionStateDef";
	
	public static final String CREATED = "CR";
	public static final String UNDER_REVIEW = "UR";
	public static final String LOV_SENT = "LS";
	public static final String NOV_SENT = "NS";
	public static final String CLOSED = "CL";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("CE_ENFORCEMENT_ACTION_STATE_DEF", "ENFORCEMENT_ACTION_STATE_CD",
					"ENFORCEMENT_ACTION_STATE_DSC", "DEPRECATED");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
*/