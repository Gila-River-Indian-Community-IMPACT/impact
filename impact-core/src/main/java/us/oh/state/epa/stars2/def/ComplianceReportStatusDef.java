package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceReportStatusDef {
    public static final String COMPLIANCE_STATUS_SUBMITTED = "sbmt";
    public static final String COMPLIANCE_STATUS_DRAFT = "drft";
    private static final String defName = "ComplianceReportStatusDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(COMPLIANCE_STATUS_SUBMITTED, "Submitted");
            data.addItem(COMPLIANCE_STATUS_DRAFT, "Draft");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
