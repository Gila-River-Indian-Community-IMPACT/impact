package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class TVComplianceStatusDef {
    private static final String COMPLIANT = "C";
    private static final String NON_COMPLIANT_CE_REQUIRED = "R";
    private static final String NON_COMPLIANT_OTHER = "O";
    private static final String defName = "TVComplianceStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(COMPLIANT, "Compliant");
            data.addItem(NON_COMPLIANT_CE_REQUIRED,
                    "Non compliant, control equipment required");
            data.addItem(NON_COMPLIANT_OTHER, "Non compliant, other");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
