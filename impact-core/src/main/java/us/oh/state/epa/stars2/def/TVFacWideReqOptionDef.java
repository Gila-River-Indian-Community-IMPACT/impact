package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TVFacWideReqOptionDef {
	public static final String SUBJECT = "1";
	public static final String NOT_SUBJECT = "2";
	
	private static final String defName = "TVFacWideReqOptionDef";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("pa_tv_fac_wide_req_opt_def", "option_cd",
					"option_dsc", "deprecated", "option_cd");

			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}
