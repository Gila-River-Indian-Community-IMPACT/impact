package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
public class EgrPointShapeDef {
    public static final String OT = "otr"; // other type
    private static final String defName = "EgrPointShapeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_EGRESS_POINT_SHAPE_DEF",
                    "EGRESS_POINT_SHAPE_CD", "EGRESS_POINT_SHAPE_DSC",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
