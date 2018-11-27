package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceReportDeviationsDef {
    private static final String COMPLIANCE_DEVIATIONS_YES = "yes";
    private static final String COMPLIANCE_DEVIATIONS_NO = "no";
    private static final String defName = "ComplianceReportDeviationsDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(COMPLIANCE_DEVIATIONS_YES, "Yes");
            data.addItem(COMPLIANCE_DEVIATIONS_NO, "No");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
