package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PT_FEE_ADJUSTMENT_DEF table
 */
public class PermitFeeAdjustmentDef {
    public static final String NONE = "N";
    public static final String DOUBLE = "D";
    public static final String HALF = "H";
    public static final String NO_FEE = "NF";
    public static final String OTHER = "O";
    private static final String defName = "PermitFeeAdjustmentDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PT_FEE_ADJUSTMENT_DEF", "ADJUSTMENT_CD",
                            "ADJUSTMENT_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
