package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class Country {
	public static final String US = "US";	// USA
	public static final String CA = "CA";	// Canada
	private static final String defName = "Country";
	
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_country_def", "country_cd", "country_dsc", null);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
