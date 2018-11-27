package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class DelegationRequestTypeDef {
    public static final String DELEGATION_DLG = "DLG";
    public static final String DELEGATION_RO = "RO";
    private static final String defName = "DelegationTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(DELEGATION_DLG, "Delegation of Authority");
            data.addItem(DELEGATION_RO, "Responsible Official");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
