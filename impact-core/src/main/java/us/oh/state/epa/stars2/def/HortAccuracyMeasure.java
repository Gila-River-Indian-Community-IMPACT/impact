package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class HortAccuracyMeasure {
    public static final String M100 = "100";
    private static final String defName = "HortAccuracyMeasure";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_hort_accuracy_meas",
                    "hort_accuracy_meas_cd", "hort_accuracy_meas_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
