package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;


/*
 * The codes declared in this class must match the codes declared in
 * PermitClassDef class
 * 
 * NOTE:  This class excludes TV_A.  Class EmissionReportsRealDef 
 *        includes it.
 */
public class EmissionReportsDef {
    // Note that these codes must match those used by class PermittClassDef
    public static final String NTV = "ntv";
    public static final String TV = "tv";
    public static final String SMTV = "smtv";
    public static final String NONE = "none";
    private static final String defName = "EmissionReportsDef";

    public static String reportingCategory(String permitingClassification) {
        String v = null;
        v = permitingClassification; // Depending upon both reference tables
                                        // using same codes for same value.
        if (permitingClassification == null) {
            v = EmissionReportsDef.NONE;
        }
        return v;
    }
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_EMISSION_REPORTING_DEF", "EMISSIONS_RPT_CD",
                    "EMISSIONS_RPT_DSC", "deprecated");
            
            cfgMgr.addDef(defName, data);
        }
        
        return data;
    }
}
