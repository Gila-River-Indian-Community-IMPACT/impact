package us.wy.state.deq.impact.def;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class FCEInspectionReportStateDef {
	
	// ****************** Variables *******************
	private static final String defName = "FCEInspectionReportStateDef";
	private static final String tableName = "CE_FCE_INSPECTION_REPORT_STATE_DEF";
	private static final String keyFieldName = "INSPECTION_REPORT_STATE_CD";
	private static final String valueFieldName = "INSPECTION_REPORT_STATE_DSC";
	private static final String deprecatedFieldName = "DEPRECATED";

	public static final String INITIAL = "INI";
	public static final String PREPARE = "PRE";
	public static final String COMPLETE = "ICO";
	public static final String FINAL = "IRF";
	public static final String DEAD_ENDED = "IDE";
	
	// ****************** Public Static Methods *******************
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(tableName, keyFieldName, valueFieldName, deprecatedFieldName);

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
