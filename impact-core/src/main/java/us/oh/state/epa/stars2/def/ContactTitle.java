package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ContactTitle {
    private static final String defName = "ContactTitle";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_title_def", "title_cd", "title_dsc", "deprecated");
            
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
