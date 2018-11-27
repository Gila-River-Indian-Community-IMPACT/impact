package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class DdUnitDef {
    private static final String defName = "DdUnitDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("DD_UNIT_DEF", "UNIT_CD",
                    "UNIT_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
