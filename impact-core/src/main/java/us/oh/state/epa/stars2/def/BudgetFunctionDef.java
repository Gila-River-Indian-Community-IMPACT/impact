package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class BudgetFunctionDef {
	
	public static final String defName = "BudgetFunctionDef";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("PO_BUDGET_FUNCTION_DEF", "BUDGET_FUNCTION_CD",
					"BUDGET_FUNCTION_DSC", "deprecated");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}

}
