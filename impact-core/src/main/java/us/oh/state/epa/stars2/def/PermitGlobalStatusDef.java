package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.DAOException;

/*
 * The codes declared in this class must match the content of the
 * PT_PERMIT_GLOBAL_STATUS_DEF table
 */
public class PermitGlobalStatusDef {
    public static final String NONE = "N";
    public static final String DEAD_ENDED = "E";
    public static final String ISSUED_DRAFT = "D";
    //public static final String ISSUED_PPP = "PPP";
    public static final String ISSUED_PP = "PP";
    public static final String ISSUED_FINAL = "F";
    public static final String ISSUED_DENIAL = "ID";  // Issued Withdrawal
    public static final String DENIAL_PENDING = "DP"; // Withdrawal Pending
    private static final String defName = "PermitGlobalStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PT_PERMIT_GLOBAL_STATUS_DEF",
                            "PERMIT_GLOBAL_STATUS_CD", "PERMIT_GLOBAL_STATUS_DSC",
                            "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public static boolean isValid(String permitGlobalStatusCd) {

        if (permitGlobalStatusCd == null) {
            return false;
        }
        if (permitGlobalStatusCd.equals(NONE)
            || permitGlobalStatusCd.equals(DEAD_ENDED)
            || permitGlobalStatusCd.equals(ISSUED_DRAFT)
        //    || permitGlobalStatusCd.equals(ISSUED_PPP)
            || permitGlobalStatusCd.equals(ISSUED_PP)
            || permitGlobalStatusCd.equals(ISSUED_FINAL)
            || permitGlobalStatusCd.equals(ISSUED_DENIAL)
            || permitGlobalStatusCd.equals(DENIAL_PENDING)) {
            return true;
        }
        return false;
    }

    /**
     * @param issuanceType
     * @return
     * @throws DAOException
     * 
     */
    public static String find(String issuanceType) throws DAOException {

        if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.Final)) {
            return ISSUED_FINAL;
        }
        else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            return ISSUED_DRAFT;
        }
        else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.PP)) {
            return ISSUED_PP;
        }
        //else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.PPP)) {
        //    return ISSUED_PPP;
        //}
        else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.DENIED)) {
            return ISSUED_DENIAL;
        }
        else {
            throw new DAOException("Cannot find correct PermitGlobalStatus for "
                                   + issuanceType);
        }
    }
}
