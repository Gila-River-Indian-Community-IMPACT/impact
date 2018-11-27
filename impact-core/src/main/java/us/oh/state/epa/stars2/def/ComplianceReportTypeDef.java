package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceReportTypeDef {
    // public static final String COMPLIANCE_TYPE_TVCC = "tvcc";
    // public static final String COMPLIANCE_TYPE_PER = "per";
    public static final String COMPLIANCE_TYPE_ONE = "one";
    public static final String COMPLIANCE_TYPE_CEMS = "cems";
    public static final String COMPLIANCE_TYPE_TESTING = "test";
    public static final String COMPLIANCE_TYPE_OTHER = "othr";
    // public static final String COMPLIANCE_TYPE_SMBR = "rsmb";
    private static final String defName = "ComplianceReportTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("cr_report_type_def", "report_type_cd", "report_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        
        return data;
    }
    
    public static boolean anyOtherType(String typeCd) {
        boolean rtn = false;
        if(COMPLIANCE_TYPE_OTHER.equals(typeCd) ||
                /* COMPLIANCE_TYPE_TESTING.equals(typeCd) || */
                COMPLIANCE_TYPE_CEMS.equals(typeCd) ||
                /* COMPLIANCE_TYPE_SMBR.equals(typeCd) || */
                COMPLIANCE_TYPE_ONE.equals(typeCd)) {
            rtn = true;
        }
        return rtn;
    }
}
