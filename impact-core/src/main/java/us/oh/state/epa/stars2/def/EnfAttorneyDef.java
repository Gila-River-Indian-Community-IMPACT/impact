package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class EnfAttorneyDef {
	private static final String defName = "EnfAttorneyDef";
	public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_ENFORCEMENT_ATTORNEY_DEF", "ENF_ATTORNEY_CD",
                    "ENF_ATTORNEY_DSC", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
	
	public final DefSelectItems getEnfAttorneyDef() {
        return EnfAttorneyDef.getData().getItems();
    }
}
