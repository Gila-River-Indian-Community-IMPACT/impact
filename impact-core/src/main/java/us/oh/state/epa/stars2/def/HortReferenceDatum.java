package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class HortReferenceDatum {
    public static final String W1984 = "003";
    private static final String defName = "HortReferenceDatum";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_hort_reference_datum",
                    "hort_reference_datum_cd", "hort_reference_datum_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
