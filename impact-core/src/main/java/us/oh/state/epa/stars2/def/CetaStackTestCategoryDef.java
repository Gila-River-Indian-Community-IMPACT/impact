package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CetaStackTestCategoryDef {

	private static final String defName = "CetaStackTestCategoryDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("CE_Stack_Test_Category_def",
					"StackTest_Category_cd", "StackTest_Category_dsc",
					"DEPRECATED");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
