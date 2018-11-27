package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ptio_fed_rule_app_cd column in the pa_ptio_fed_rule_app_def table
 */
public class NSREuFedRuleAppDef {
    private static final String defName = "NSREuFedRuleAppDef";
    
    public static final String NOT_AFFECTED = "0";
    public static final String AFFECTED_NOT_MODIFIED = "1";
    public static final String AFFECTED = "2";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pa_nsr_eu_fed_rule_app_def", "nsr_eu_fed_rule_app_cd",
                    "nsr_eu_fed_rule_app_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
