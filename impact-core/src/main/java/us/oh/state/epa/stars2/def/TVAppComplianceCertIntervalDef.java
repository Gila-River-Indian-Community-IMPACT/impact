package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_TV_COMPLIANCE_CERT_INTERVAL_DEF table
 */
public class TVAppComplianceCertIntervalDef {

    private static final String defName = "TVAppComplianceCertIntervalDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_TV_COMPLIANCE_CERT_INTERVAL_DEF", "COMPLIANCE_CERT_INTERVAL_CD",
                    "COMPLIANCE_CERT_INTERVAL_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}