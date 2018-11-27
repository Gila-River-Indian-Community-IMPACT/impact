package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TransactionTypeDef {
	public static final String DEBIT = "Dr";
	public static final String CREDIT = "Cr";
	
	private static final String defName = "TransactionTypeDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("CM_TRANSACTION_TYPE_DEF", "TRANSACTION_TYPE_CODE", "TRANSACTION_TYPE_DESCRIPTION",
								"DEPRECATED", "TRANSACTION_TYPE_DESCRIPTION");
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}