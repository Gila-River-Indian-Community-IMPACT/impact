package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PermitConditionStatusDef {
	public static String ACTIVE = "A";
	public static String SUPERSEDED = "S";
	public static String PARTIALLY_SUPERSEDED = "PS";

	// ****************** Variables *******************
	private static final String defName = "PermitConditionStatusDef";
	private static final String tableName = "PT_PERMIT_CONDITION_STATUS_DEF";
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