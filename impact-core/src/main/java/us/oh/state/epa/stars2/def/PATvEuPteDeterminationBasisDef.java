package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_PTIO_APP_PURPOSE_DEF table
 */
public class PATvEuPteDeterminationBasisDef {
    
    public static final String OTHERS = "OTH";
    private static final String defName = "PATvEuPteDeterminationBasisDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_TV_EU_PTE_DETERMINATION_BASIS_DEF", "PTE_DETERMINATION_BASIS_CD",
                    "PTE_DETERMINATION_BASIS_DSC", "deprecated", "PTE_DETERMINATION_BASIS_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
