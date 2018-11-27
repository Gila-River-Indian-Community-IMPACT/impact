package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * dd_data_type_def table
 */
public class RuleCitationDef {
    private static final String defName = "RuleCitationDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CM_RULE_CITATION_DEF", "RULE_CITATION_CD",
                    "RULE_CITATION_DSC", "DEPRECATED", "RULE_CITATION_DSC");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
