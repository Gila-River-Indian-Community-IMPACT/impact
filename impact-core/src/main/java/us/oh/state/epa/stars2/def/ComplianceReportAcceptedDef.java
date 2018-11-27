package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceReportAcceptedDef {
    public static final String COMPLIANCE_REPORT_ACCEPTED = "yes";
    public static final String COMPLIANCE_REPORT_DENIED = "no";
    public static final String COMPLIANCE_REPORT_REVISION_REQ = "norr";
    public static final String COMPLIANCE_REPORT_UNREVIEWED = "nyr";
    private static final String defName = "ComplianceReportAcceptedDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(COMPLIANCE_REPORT_REVISION_REQ, "No-Revision Requested");
            data.addItem(COMPLIANCE_REPORT_DENIED, "No");
            data.addItem(COMPLIANCE_REPORT_ACCEPTED, "Yes");
            data.addItem(COMPLIANCE_REPORT_UNREVIEWED, "Not Yet Reviewed");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
