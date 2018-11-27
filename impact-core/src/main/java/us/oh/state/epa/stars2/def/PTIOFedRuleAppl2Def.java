package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ptio_fed_rule_app_cd column in the pa_ptio_fed_rule_app_def table
 */
public class PTIOFedRuleAppl2Def {
    private static final String defName = "PTIOFedRuleAppl2Def";
    
    public static final String SUBJECT_TO_REG = "2";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addExcludedKey(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART);
            data.addExcludedKey(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT);
            data.loadFromDB("pa_ptio_fed_rule_app_def", "ptio_fed_rule_app_cd",
                    "ptio_fed_rule_app_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
