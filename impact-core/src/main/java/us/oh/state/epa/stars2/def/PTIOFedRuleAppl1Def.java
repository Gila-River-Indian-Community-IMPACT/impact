package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ptio_fed_rule_app_cd column in the pa_ptio_fed_rule_app_def table
 */
public class PTIOFedRuleAppl1Def {
    public static final String NOT_AFFECTED = "0";
    public static final String UNKNOWN = "1";
    public static final String SUBJECT_TO_REGULATION = "2";
    public static final String SUBJECT_TO_SUBPART = "3";
    public static final String SUBJECT_BUT_EXEMPT = "4";
    private static final String defName = "PTIOFedRuleAppl1Def";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addExcludedKey(SUBJECT_TO_REGULATION);
            data.loadFromDB("pa_ptio_fed_rule_app_def", "ptio_fed_rule_app_cd",
                    "ptio_fed_rule_app_dsc", "deprecated",
                    "ptio_fed_rule_app_cd");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
