package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * These are the choices for state/status
 * for an emissions inventory
 */

public class ReportOfEmissionsStateDef extends ReportReceivedStatusDef{
    private static final String defName = "ReportOfEmissionsStateDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_RPT_RECEIVED_STATUS_DEF",
                    "RPT_RECEIVED_STATUS_CD", "RPT_RECEIVED_STATUS_DSC",
                    "deprecated", "RPT_RECEIVED_STATUS_CD");
            
            data.addExcludedKey(ReportReceivedStatusDef.NO_REPORT_NEEDED);
            data.addExcludedKey(ReportReceivedStatusDef.REPORT_NOT_REQUESTED);
            data.addExcludedKey(ReportReceivedStatusDef.REMINDER_SENT);
            data.addExcludedKey(ReportReceivedStatusDef.SECOND_REMINDER_SENT);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
