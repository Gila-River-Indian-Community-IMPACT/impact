package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class RevenueSearchState {
    public static final String PD = "15pd";
    public static final String NP = "16np";
    public static final String AGO = "14ao";
    public static final String AGO_POTENTIAL = "13ao";
    public static final String AGO_POTENTIAL_UNPAID = "11aoU";
    public static final String AGO_POTENTIAL_PAID = "12aoP";

	private static final String defName = "RevenueSearchState";

	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
            data.addItem(NP, RevenueState.getData().getItems().getItemDesc(RevenueState.NP));
			data.addItem(AGO_POTENTIAL_UNPAID, RevenueState.getData().getItems().getItemDesc(RevenueState.AGO_POTENTIAL) +
                    " (" + RevenueState.getData().getItems().getItemDesc(RevenueState.NP) + ")");
            data.addItem(AGO_POTENTIAL_PAID, RevenueState.getData().getItems().getItemDesc(RevenueState.AGO_POTENTIAL) +
                    " (" + RevenueState.getData().getItems().getItemDesc(RevenueState.PD) + ")");
            // data.addItem(AGO_POTENTIAL, RevenueState.getData().getItems().getItemDesc(RevenueState.AGO_POTENTIAL));
            data.addItem(AGO, RevenueState.getData().getItems().getItemDesc(RevenueState.AGO));
            data.addItem(PD, RevenueState.getData().getItems().getItemDesc(RevenueState.PD));
            cfgMgr.addDef(defName, data);
		}
		return data;
	}
}