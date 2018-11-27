package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PTIRegulatoryStatus {
    public final static String NONE = "none";
    public final static String ACTIVE = "act";
    public final static String PBR = "pbr";
    private static final String defName = "PTIRegulatoryStatus";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_pti_regulatory_status",
                    "pti_regulatory_status_cd", "pti_regulatory_status_dsc",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }
}
