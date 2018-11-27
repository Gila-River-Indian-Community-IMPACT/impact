package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ActionsDef {
    private static final String defName = "ActionsDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("rp_actions", "action_cd", 
                            "material_io_cd", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
