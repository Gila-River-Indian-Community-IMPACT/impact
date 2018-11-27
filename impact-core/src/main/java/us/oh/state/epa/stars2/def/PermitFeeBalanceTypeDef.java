package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PermitFeeBalanceTypeDef {
	private static final String defName = "PermitFeeBalanceTypeDef";
    public static final String ALL_BALANCES = "AB";
    public static final String NON_ZERO_BALANCES = "NZB";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PT_PERMIT_FEE_BALANCE_TYPE_DEF", "PERMIT_FEE_BALANCE_TYPE_CD",
                            "PERMIT_FEE_BALANCE_TYPE_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }
}
