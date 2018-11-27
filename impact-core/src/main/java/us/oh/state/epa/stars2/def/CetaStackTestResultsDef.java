package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CetaStackTestResultsDef {

    private static final String defName = "CetaStackTestResultsDef";

    public static final String PASS = "PP";
    public static final String FAIL = "FF";
    public static final String RETEST_NEEDED = "INV";
    public static final String NOT_COMPLETED = "INC";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("ce_stack_test_result_def", "stack_test_result_cd", 
                    "stack_test_result_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
