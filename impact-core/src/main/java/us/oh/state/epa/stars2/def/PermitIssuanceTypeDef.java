package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PT_PERMIT_ISSUANCE_TYPE_DEF table
 */
public class PermitIssuanceTypeDef {
    public static final String Draft = "D";
    public static final String Final = "F";
    public static final String PP = "PP";
    //public static final String PPP = "PPP";
    public static final String DENIED = "ID";    // Issued Withdrawal
    private static final String defName = "PermitIssuanceTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PT_PERMIT_ISSUANCE_TYPE_DEF", "ISSUANCE_TYPE_CD",
                            "ISSUANCE_TYPE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }

        return data;

    }

    public static boolean isValid(String permitIssuanceType) {

        if (permitIssuanceType == null) {
            return false;
        }
        if (permitIssuanceType.equals(Draft)
                || permitIssuanceType.equals(Final)
                || permitIssuanceType.equals(PP)
         //       || permitIssuanceType.equals(PPP)
                || permitIssuanceType.equals(DENIED)) {
            return true;
        }
        return false;
    }
}
