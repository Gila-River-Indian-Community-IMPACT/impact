package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnfMilestoneCaseSettlementsDef {
    public static final String FIND_ORDERS_CONSENSUAL = "foc";
    public static final String FIND_ORDERS_UNILATERAL = "fou";
    public static final String CONSENT_ORDER = "csto";
    public static final String COURT_ORDER = "cto";
    private static final String defName = "EnfMilestoneCaseSettlementsDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_CASE_SETTLEMENT_DEF", "CASE_SETTLEMENT_CD",
                    "CASE_SETTLEMENT_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
