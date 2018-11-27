package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * DD_ENUM_DEF table
 */
public class EnumDef {
    private static final String defName = "EnumDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("DD_ENUM_DEF", "ENUM_CD", "ENUM_DSC", "DEPRECATED", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
