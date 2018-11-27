package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;


public class TVFedRuleAppDef {
    public static final String SUBJECT_TO_REGULATION = "1";
    public static final String UNKNOWN = "2";
    public static final String NOT_AFFECTED = "3";
    
    private static final String defName = "TVFedRuleAppDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pa_tv_fed_rule_app_def", "tv_fed_rule_app_cd",
                    "tv_fed_rule_app_dsc", "deprecated",
                    "tv_fed_rule_app_cd");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
