package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class TVSection112RDef {
    private static final String PLAN_NOT_REQUIRED = "N";
    private static final String PLAN_SUBMITTED = "S";
    private static final String PLAN_REQUIRED_NOT_SUBMITTED = "I";
    private static final String defName = "TVSection112RDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(PLAN_NOT_REQUIRED, "Plan not required");
            data.addItem(PLAN_SUBMITTED, "Plan submitted");
            data.addItem(PLAN_REQUIRED_NOT_SUBMITTED,
                    "Plan required not submitted");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
