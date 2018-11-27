package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the codes declared in
 * EmissionReportsDef class
 * 
 * USE this class when it is to display category of actual reports
 * 
 * NOTE:  This class includes TV_A.  Class EmissionReportsDef does not.
 *        It also excludes None
 */
public class EmissionReportsRealDef {
    // Note that these codes must match those used by class PermittClassDef
    public static final String NTV = "ntv";
    public static final String TV = "tv";
    public static final String SMTV = "smtv";
    public static final String NONE = "none";
    private static final String defName = "EmissionReportsReadDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_EMISSION_REPORTING_DEF", "EMISSIONS_RPT_CD",
                    "EMISSIONS_RPT_DSC", "deprecated");

            data.addExcludedKey(NONE);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
