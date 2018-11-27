package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceReportSearchDateByDef {
    public static final String COMPLIANCE_REPORT_REVIEWED_DT = "rvwd";
    public static final String COMPLIANCE_REPORT_RECEIVED_DT = "sbmt";
    private static final String defName = "ComplianceReportSearchDateByDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(COMPLIANCE_REPORT_RECEIVED_DT, "Received");
            data.addItem(COMPLIANCE_REPORT_REVIEWED_DT, "Reviewed");
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
