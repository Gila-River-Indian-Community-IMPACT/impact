package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * this version excludes the 'not yet reviewed' and is to be displayed once a report's review status
 * has been set to a value other than not yet reviewed.
 */
public class ComplianceReportAcceptedReviewedDef {
    public static final String COMPLIANCE_REPORT_ACCEPTED = "yes";
    public static final String COMPLIANCE_REPORT_DENIED = "no";
    public static final String COMPLIANCE_REPORT_REVISION_REQ = "norr";
    private static final String defName = "ComplianceReportAcceptedReviewedDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(COMPLIANCE_REPORT_REVISION_REQ, "No-Revision Requested");
            data.addItem(COMPLIANCE_REPORT_DENIED, "No");
            data.addItem(COMPLIANCE_REPORT_ACCEPTED, "Yes");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
