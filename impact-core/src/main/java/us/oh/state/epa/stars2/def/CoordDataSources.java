package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CoordDataSources {
    public static final String C080 = "080";
    private static final String defName = "CoordDataSources";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_coordinate_date_source",
                    "coordinate_data_source_cd", "coordinate_data_source_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
