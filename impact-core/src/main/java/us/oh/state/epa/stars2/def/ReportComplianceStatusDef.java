package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * CR_COMPLIANCE_STATUS_DEF table
 */
public class ReportComplianceStatusDef {
    private static final String defName = "ReportComplianceStatusDef";
    public static final String SUBST_COMPLIANCE = "SU";
    public static final String UNDETERMINED = "UN";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CR_COMPLIANCE_STATUS_DEF",
                    "COMPLIANCE_STATUS_CD", "COMPLIANCE_STATUS_DSC",
                    "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    
}
