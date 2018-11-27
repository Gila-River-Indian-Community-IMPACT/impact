package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * IMPORTANT: This feature is a post-warranty addition. To minimize database
 * changes an existing Y/N field (fac25Mill) is being used to store the 
 * disposition. For that reason the Def values are slightly different than
 * typical def disposition values.  They are only 1 character (vs. the typical 4)
 */
public class DelegationDispositionDef {
    public static final String DELEGATION_DISPOSITION_APPROVE = "Y";
    public static final String DELEGATION_DISPOSITION_DENY = "N";
    private static final String defName = "DelegationDispositionDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.addItem(DELEGATION_DISPOSITION_APPROVE, "Approve");
            data.addItem(DELEGATION_DISPOSITION_DENY, "Deny");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}

