package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CetaStackTestMethodDef {

private static final String defName = "CetaStackTestMethodDef";
    public static final String METHODOTHER = "other";
    public static final String PORTABLE_ANALYZER = "1A";
    
public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ce_stack_test_method_def", "stack_test_method_cd", 
            "stack_test_method_dsc", "deprecated", "sort_order");
            
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
