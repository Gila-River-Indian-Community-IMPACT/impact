package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class RevenueState {

	public static final String PD = "15pd";

	public static final String NP = "16np";

	public static final String AGO = "14ao";
    
    public static final String AGO_POTENTIAL = "13ao";

	private static final String defName = "RevenueState";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
            data.loadFromDB("IV_REVENUE_STATE_DEF",
                    "REVENUE_STATE_CD", "REVENUE_STATE_DSC",
                    "deprecated", "REVENUE_STATE_CD");


			cfgMgr.addDef(defName, data);
		}
		return data;
	}
}