package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class TVEMComplianceStatusDef {
    private static final String IN_COMPLIANCE = "C";
    private static final String NOT_IN_COMPLIANCE = "N";
    private static final String NOT_APPLICABLE = "A";
    private static final String defName = "TVEMComplianceStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(IN_COMPLIANCE, "In compliance");
            data.addItem(NOT_IN_COMPLIANCE, "Not in compliance");
            data.addItem(NOT_APPLICABLE, "Not applicable");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
