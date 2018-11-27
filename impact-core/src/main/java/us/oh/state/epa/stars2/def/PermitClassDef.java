package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the codes declared in
 * EmissionReportsDef class
 */
public class PermitClassDef {
    // Note that the first four must match the codes used in class
    // EmisionReportDef
    public static final String NTV = "ntv";
    public static final String TV = "tv";
    public static final String SMTV = "smtv";
    public static final String NONE = "none";
    
    private static final String defName = "PermitClassDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_PERMIT_CLASSIFICATION_DEF",
                    "PERMIT_CLASSIFICATION_CD", "PERMIT_CLASSIFICATION_DSC",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }   
}
