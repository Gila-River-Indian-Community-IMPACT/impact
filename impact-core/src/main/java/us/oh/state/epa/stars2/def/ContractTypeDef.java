package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ContractTypeDef {

	public static final String defName = "ContractTypeDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_CONTRACT_TYPE_DEF", "CONTRACT_TYPE_CD",
					"CONTRACT_TYPE_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
