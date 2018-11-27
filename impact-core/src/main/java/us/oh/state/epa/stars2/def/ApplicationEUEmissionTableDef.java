package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ApplicationEUEmissionTableDef {
    public static final String CAP_TABLE_CD = "CAP";
    public static final String HAP_TABLE_CD = "HAP";
    public static final String GHG_TABLE_CD = "GHG";
    public static final String OTH_TABLE_CD = "OTH";
    private static final String defName = "ApplicationEUEmissionTableDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_EU_EMISSION_TABLE_DEF",
                    "EU_EMISSION_TABLE_CD", "EU_EMISSION_TABLE_DSC",
                    null);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
