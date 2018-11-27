package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EmissionsTestStateDef {
    public static final String SUBMITTED = "sbmt";
    public static final String DRAFT = "drft";
    public static final String REMINDER = "rmdr";
    private static final String defName = "EmissionsTestStateDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_Emissions_test_state_def", "Emissions_test_state_cd",
                    "Emissions_test_state_dsc", "DEPRECATED");
            
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
