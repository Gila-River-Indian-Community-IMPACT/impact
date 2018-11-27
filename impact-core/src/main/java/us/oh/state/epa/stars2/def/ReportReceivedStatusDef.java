package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

// This class should not be used directly--except in class ReportTree.
// Use ReportStatusDef or ReportOfEmissionsStateDef instead.
public class ReportReceivedStatusDef {
    public static final String NO_REPORT_NEEDED = "00no"; // Don't file report
    public static final String REPORT_NOT_REQUESTED = "01nr"; // report not yet asked for.
    public static final String NOT_FILED = "02nf"; // when report is being
                                                    // prepared by DEM
    public static final String REMINDER_SENT = "03rs";
    public static final String SECOND_REMINDER_SENT = "05nv";
    public static final String SUBMITTED = "08ur";
    public static final String DOLAA_APPROVED = "10aa";
    public static final String DOLAA_APPROVED_RR = "09ar";
    public static final String REVISION_REQUESTED = "07rr"; // only submitted
    public static final String SUBMITTED_CAUTION = "07sc";
    public static final String REPORT_NOT_NEEDED = "06nn"; // Indicate existing submitted report prior or invalid.
    public static final String APPROVED_REPORT_NOT_NEEDED = "11nn"; // approved inventory prior or invalid
    private static final String defName = "ReportReceivedStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("FP_RPT_RECEIVED_STATUS_DEF", "RPT_RECEIVED_STATUS_CD", "RPT_RECEIVED_STATUS_DSC", "deprecated", "RPT_RECEIVED_STATUS_CD");
            
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static boolean isApprovedCode(String cd) {
        if(DOLAA_APPROVED.equals(cd) || DOLAA_APPROVED_RR.equals(cd)) return true;
        else return false;
    }
    
    public static boolean isHandledNotApprovedCode(String cd) {
        if(REVISION_REQUESTED.equals(cd) || REPORT_NOT_NEEDED.equals(cd)
                || APPROVED_REPORT_NOT_NEEDED.equals(cd)) return true;
        else return false;
    }
    
    public static boolean isSubmittedCode(String cd) {
        if(SUBMITTED.equals(cd) || SUBMITTED_CAUTION.equals(cd)) return true;
        else return false;
    }
    
    public static boolean isNotNeededCode(String cd) {
        if(REPORT_NOT_NEEDED.equals(cd) || APPROVED_REPORT_NOT_NEEDED.equals(cd)) return true;
        else return false;
    }
}
