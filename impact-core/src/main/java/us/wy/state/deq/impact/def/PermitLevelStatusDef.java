package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PermitLevelStatusDef {
	public static String ACTIVE = "A";
	public static String EXPIRED = "E";
	public static String RESCINDED = "R";
	public static String INVALID = "I";

	// ****************** Variables *******************
	private static final String defName = "PermitLevelStatusDef";
	private static final String tableName = "PT_PERMIT_LEVEL_STATUS_DEF";
	private static final String keyFieldName = "CODE";
	private static final String valueFieldName = "DESCRIPTION";
	private static final String deprecatedFieldName = "DEPRECATED";

	// ****************** Public Static Methods *******************
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(tableName, keyFieldName, valueFieldName,
					deprecatedFieldName);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}