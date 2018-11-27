package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * CR_TVCC_COMPLIANCE_STATUS_DEF table
 */
public class TVCCComplianceStatusDef {
    private static final String defName = "TVCCComplianceStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CR_TVCC_COMPLIANCE_STATUS_DEF",
                    "COMPLIANCE_STATUS_CD", "COMPLIANCE_STATUS_DSC",
                    "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static boolean isComply(String cd) {
        if("COMP".equals(cd) || "ICMP".equals(cd)) return true;
        return false;
    }
    
    public static boolean isNotComply(String cd) {
        if("NCMP".equals(cd)) return true;
        return false;
    }
}
