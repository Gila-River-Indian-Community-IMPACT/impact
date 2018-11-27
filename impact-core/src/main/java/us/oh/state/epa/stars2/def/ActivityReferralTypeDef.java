package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * WF_ACTIVITY_REFERRAL_TYPE_DEF table
 */
public class ActivityReferralTypeDef {
    public static final Integer FACILITY = 1;
    public static final Integer FACILITY_CONSULTANT = 2;
    public static final Integer FACILITY_ATTORNEY = 3;
    public static final Integer FACILITY_MODELING = 4;
    public static final Integer CO_DAPC = 5;
    public static final Integer CO_LEGAL = 6;
    public static final Integer CO_DIRECTOR = 7;
    public static final Integer CO_OTHER_DIVISION = 8;
    public static final Integer USEPA_REGION = 9;
    public static final Integer USEPA_WASHINGTON = 10;
    public static final Integer USEPA_RTP = 11;
    public static final Integer ATTORNEY_GENERAL_OFFICE = 12;
    public static final Integer HOLD_FOROEPA_RULE_CHANGE = 13;
    public static final Integer HOLD_FOR_USEPA_SIP_APPROVAL = 14;
    public static final Integer HOLD_FOR_APPEAL_RESOLUTION = 15;
    public static final Integer HOLD_FOR_ENFORCEMENT_RESOLUTION = 16;
    private static final String defName = "ActivityReferralTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData(true);
            data.loadFromDB("WF_ACTIVITY_REFERRAL_TYPE_DEF",
                    "ACTIVITY_REFERRAL_TYPE_ID", "ACTIVITY_REFERRAL_TYPE_DSC",
                    "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
