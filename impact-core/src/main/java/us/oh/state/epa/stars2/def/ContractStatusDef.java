package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ContractStatusDef {

	public static final String defName = "ContractStatusDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_CONTRACT_STATUS_DEF", "CONTRACT_STATUS_CD",
					"CONTRACT_STATUS_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
