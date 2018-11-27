package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_EAC_FORM_TYPE_DEF table
 */
public class EACFormTypeDef {
    private static final String defName = "EACFormTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_EAC_FORM_TYPE_DEF", "EAC_FORM_TYPE_CD",
                    "EAC_FORM_TYPE_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
