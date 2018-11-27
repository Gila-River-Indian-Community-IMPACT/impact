package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceTrackingEventTypeDef {

	public static final String COMPLIANCE_NOTE = "CN";
	public static final String COMPLIANCE_REPORT = "CR";
	public static final String INSPECTION = "IN";
	public static final String STACK_TEST = "ST";
	
	// ****************** Variables *******************
	private static final String defName = "ComplianceTrackingEventTypeDef";
	private static final String tableName = "PT_PERMIT_COMPLIANCE_TRACKING_EVENT_TYPE_DEF";
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