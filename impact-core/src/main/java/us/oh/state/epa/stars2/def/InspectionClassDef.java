package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class InspectionClassDef {
	
	private static final String tableName = "FP_INSPECTION_CLASS_DEF";
	private static final String keyField = "INSPECTION_CLASS_CD";
	private static final String displayField = "INSPECTION_CLASS_CD";
	private static final String deprecatedField = "DEPRECATED";
	private static final String defName = "InspectionClassDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB(tableName, keyField, displayField, deprecatedField);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}