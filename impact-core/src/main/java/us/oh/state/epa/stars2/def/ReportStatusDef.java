package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

// These are states in the yearly reporting category table for the report
public class ReportStatusDef extends ReportReceivedStatusDef {
    private static final String defName = "ReportStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_RPT_RECEIVED_STATUS_DEF",
                    "RPT_RECEIVED_STATUS_CD", "RPT_RECEIVED_STATUS_DSC",
                    "deprecated", "RPT_RECEIVED_STATUS_CD");

            cfgMgr.addDef(defName, data);
        }
        data.addExcludedKey(NO_REPORT_NEEDED);
        data.addExcludedKey(NOT_FILED);
        data.addExcludedKey(REMINDER_SENT);
        data.addExcludedKey(SECOND_REMINDER_SENT);
        data.addExcludedKey(DOLAA_APPROVED);
        data.addExcludedKey(DOLAA_APPROVED_RR);
        data.addExcludedKey(REVISION_REQUESTED);
        data.addExcludedKey(SUBMITTED_CAUTION);
        data.addExcludedKey(REPORT_NOT_NEEDED);
        data.addExcludedKey(APPROVED_REPORT_NOT_NEEDED);
        return data;
    }
    
    public static boolean reportCompleted(String state) {
        boolean rtn = false;
        if(DOLAA_APPROVED.equals(state) ||
                DOLAA_APPROVED_RR.equals(state) ||
                REPORT_NOT_NEEDED.equals(state) ||
                APPROVED_REPORT_NOT_NEEDED.equals(state)) {
            rtn = true;
        }
        return rtn;
    }
}
