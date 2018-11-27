package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EgrPointTypeDef {
    public static final String FUGITIVE = "AVL";
    public static final String HORIZONTAL = "HOR";
    public static final String VERTICAL = "VER";
    private static final String defName = "EgrPointTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_EGRESS_POINT_TYPE_DEF", "EGRESS_POINT_TYPE_CD",
                    "EGRESS_POINT_TYPE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

	public static boolean isFugitive(EgressPoint egp) {
		return FUGITIVE.equals(egp.getEgressPointTypeCd());
	}
}
