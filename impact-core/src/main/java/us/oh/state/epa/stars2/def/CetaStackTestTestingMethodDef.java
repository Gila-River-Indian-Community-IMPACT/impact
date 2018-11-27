package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CetaStackTestTestingMethodDef {

	private static final String defName = "CetaStackTestTestingMethodDef";
	public static final String PORTABLE_ANALYZER = "PA";
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("CE_Stack_Test_Testing_Method_def",
					"StackTest_Method_cd", "StackTest_Method_dsc", "DEPRECATED");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
