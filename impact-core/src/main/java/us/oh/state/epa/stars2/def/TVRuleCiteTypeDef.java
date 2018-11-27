package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_TV_RULE_CITE_TYPE_DEF table
 */
public class TVRuleCiteTypeDef {
    public static final String RULE = "0";
    public static final String MACT = "1";
    public static final String NESHAP = "2";
    public static final String NSPS = "3";
    private static final String defName = "TVRuleCiteTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_TV_RULE_CITE_TYPE_DEF", "TV_RULE_CITE_TYPE_CD",
                    "TV_RULE_CITE_TYPE_DSC", "DEPRECATED", "TV_RULE_CITE_TYPE_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
