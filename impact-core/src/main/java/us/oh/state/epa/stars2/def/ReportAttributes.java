package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ReportAttributes {
    private static final String defName = "ReportAttributes";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_attributes_def", "attributes_cd",
                    "attributes_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
