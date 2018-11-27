package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class FceReqInNextDef {

private static final String defName = "FceReqInNextDef";

public static final String ONE_YEAR = "1";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
//        if (data == null) {
//            data = new DefData();
//            data.loadFromDB("ct_stack_test_method_def", "stack_test_method_cd", 
//            "stack_test_method_dsc", "deprecated");
//            
//            cfgMgr.addDef(defName, data);
//        }
        
        if (data == null) {
            data = new DefData();
            // Note any items added should have the code for number of years ad the cd
            // Code "0" means any number of years.
            data.addItem(ONE_YEAR, "One Year");
            data.addItem("2", "Two Years");
            data.addItem("3", "Three Years");
            data.addItem("0", "Any Time");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
