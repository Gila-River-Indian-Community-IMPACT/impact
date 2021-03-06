package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class CompanyOutcomeDef {
    public static final String NOT_WAIVED = "N";
    public static final String WAIVED = "W";
    private static final String defName = "CompanyOutcomeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(NOT_WAIVED, "Not Waived");
            data.addItem(WAIVED, "Waived");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
