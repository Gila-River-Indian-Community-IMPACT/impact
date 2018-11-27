package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;



public class SiteVisitTypeDef {
    // The first three of these only used in migration from old CETA system (run 1/25/2013)
    public static final String COMPLIANCE_EVALUATION = "CE";
    public static final String COMPLIANT_INVESTIGATION = "CI";
    public static final String PERMIT_RELATED_ISSUES = "PI";
    public static final String STACK_TEST = "ST";
    private static final String defName = "SiteVisitTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ce_site_visit_type_def", "site_visit_type_cd",
                    "site_visit_type_dsc", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static boolean isStackTest(String s) {
        return STACK_TEST.equals(s);
    }
}


