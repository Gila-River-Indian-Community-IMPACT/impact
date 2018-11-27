package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Release Point Operating Status */

public class PermitActionTypeDef {
    private static final String defName = "PermitActionTypeDef";
    public static final String PERMIT = "0";
    public static final String WAIVER = "1";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PT_PERMIT_ACTION_TYPE_DEF", "ACTION_TYPE_CD",
                            "ACTION_TYPE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }
     
}
