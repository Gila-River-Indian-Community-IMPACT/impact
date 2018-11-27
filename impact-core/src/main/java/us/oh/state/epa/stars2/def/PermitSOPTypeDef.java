package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class PermitSOPTypeDef {
    public static final String PRELIMINARY = "P";
    public static final String CO = "C";
    public static final String ALL = "A";
    private static final String defName = "PermitSOPTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(PRELIMINARY, "DO/LAA Preliminary Review");
            data.addItem(CO, "CO Review");
            data.addItem(ALL, "All");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
}
