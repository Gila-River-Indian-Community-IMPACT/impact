package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CerrClassDef {
	private static final String tableName = "FP_CERR_CLASS_DEF";
	private static final String keyField = "CERR_CLASS_CD";
	private static final String displayField = "CERR_CLASS_DESC";
	private static final String deprecatedField = "DEPRECATED";
	private static final String defName = "CerrClassDef";
    public static final String CAP = "cap";
    public static final String NON = "non";
    public static final String SYN = "syn";

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
