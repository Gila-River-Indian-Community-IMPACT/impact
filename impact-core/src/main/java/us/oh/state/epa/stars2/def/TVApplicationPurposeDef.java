package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_TV_APP_PURPOSE_DEF table
 */
public class TVApplicationPurposeDef {
    public static final String INITIAL_APPLICATION = "0";
    public static final String RENEWAL = "1";
    public static final String REVISION_MODIFICATION_REOPENING = "2";
    private static final String defName = "TVApplicationPurposeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_TV_APP_PURPOSE_DEF", "TV_APP_PURPOSE_CD",
                    "TV_APP_PURPOSE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
