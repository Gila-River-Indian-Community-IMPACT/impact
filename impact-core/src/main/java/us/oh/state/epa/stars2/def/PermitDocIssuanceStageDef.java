package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class PermitDocIssuanceStageDef {
    public static final String DRAFT = "D";
    // public static final String PRELIMINARY_PROPOSED = "2";
    public static final String PROPOSED = "P";
    public static final String FINAL = "F";
    public static final String DENIAL = "N";   // Withdrawal/Inactive
    public static final String ALL = "";
    private static final String defName = "PermitDocIssuanceStageDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(DRAFT, "Draft");
            //data.addItem(PRELIMINARY_PROPOSED, "Preliminary proposed");
            data.addItem(PROPOSED, "Proposed");
            data.addItem(FINAL, "Final");
            data.addItem(DENIAL, "Denial");
            // Single doc in permit (Response to Comments)
            data.addItem(ALL, "All");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    /**
     * @param issuanceType
     * @return
     * 
     */
    public static String getStage(String issuanceType) {
        String ret = ALL;

        if (issuanceType == null || issuanceType.length() < 1)
            ret = ALL;
        else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            ret = DRAFT;
        }
        else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.Final)) {
            ret = FINAL;
        }
        else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.PP)) {
            ret = PROPOSED;
        }
        //else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.PPP)) {
        //    ret = PRELIMINARY_PROPOSED;
        //}
        else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.DENIED)) {
            ret = DENIAL;
        }

        return ret;
    }

    public static String findIssuanceDocDesc(String issuanceStageFlag,
                                             String permitType) {
        String ret = null;
        String type = "";
        
        //if (PermitTypeDef.TVPTI.equalsIgnoreCase(permitType)) {
        //    type = " PTI";
        //}
        //else 
        if (PermitTypeDef.NSR.equalsIgnoreCase(permitType)) {
            type = " PTIO";
        }
        else if (PermitTypeDef.TV_PTO.equalsIgnoreCase(permitType)) {
            type = " Title V Permit";
        }

        if (FINAL.equalsIgnoreCase(issuanceStageFlag)) {
            ret = "Final";
        }
        else if (DRAFT.equalsIgnoreCase(issuanceStageFlag)) {
            ret = "Draft";
        }
        //else if (PRELIMINARY_PROPOSED.equalsIgnoreCase(issuanceStageFlag)) {
        //    ret = "Preliminary Proposed";
       //}
        else if (PROPOSED.equalsIgnoreCase(issuanceStageFlag)) {
            ret = "Proposed";
        }

        return ret + type;
    }
}
