package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class PTIORequestingFedLimitsDef {
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String NOT_SURE = "U";
    private static final String defName = "PTIORequestingFedLimitsDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(YES, "Yes");
            data.addItem(NO, "No");
            data.addItem(NOT_SURE, "Not sure");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
