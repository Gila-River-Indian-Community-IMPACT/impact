package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * dd_data_type_def table
 */
public class DataTypeDef {
    private static final String defName = "DataTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData(true);
            data.loadFromDB("DD_DATA_TYPE_DEF", "DATA_TYPE_ID",
                    "DATA_TYPE_NM", null);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
