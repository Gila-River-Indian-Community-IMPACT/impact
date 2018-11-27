package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_TV_REQUIREMENT_BASIS_DEF table
 */
public class TvReqBasisDef {
    public static final String PERMIT = "0";
    public static final String WAIVER = "1";
    public static final String STATE_RULE = "2";
    private static final String defName = "TvReqBasisDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_TV_REQUIREMENT_BASIS_DEF", "REQ_BASIS_CD",
                    "REQ_BASIS_DSC", "DEPRECATED", "REQ_BASIS_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
