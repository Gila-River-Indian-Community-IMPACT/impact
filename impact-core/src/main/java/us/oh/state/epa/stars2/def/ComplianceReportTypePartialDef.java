package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceReportTypePartialDef {
    private static final String defName = "ComplianceReportTypePartialDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("cr_report_type_def", "report_type_cd", "report_type_dsc", "deprecated");
            // data.addExcludedKey(ComplianceReportTypeDef.COMPLIANCE_TYPE_TVCC);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
