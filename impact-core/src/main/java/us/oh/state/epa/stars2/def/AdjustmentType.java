package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class AdjustmentType {
	public static final String SET_ORIGINAL_AMOUNT = "STOA";
	public static final String ADJUST_CURRENT_AMOUNT = "ADCA";
    private static final String defName = "AdjustmentType";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("iv_adjustment_type_def", "adjustment_type_cd",
                    "adjustment_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
