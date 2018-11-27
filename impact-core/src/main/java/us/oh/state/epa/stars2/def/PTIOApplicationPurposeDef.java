package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_PTIO_APP_PURPOSE_DEF table
 */
public class PTIOApplicationPurposeDef {
    public static final String CONSTRUCTION = "0";
    public static final String SYNTHETIC_MINOR = "1";
    public static final String MODIFICATION = "2";
    public static final String TEMPORARY_PERMIT = "3";
    public static final String RECONSTRUCTION = "4";
    public static final String OTHER = "5";
    private static final String defName = "PTIOApplicationPurposeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PTIO_APP_PURPOSE_DEF", "PTIO_APP_PURPOSE_CD",
                    "PTIO_APP_PURPOSE_DSC", "deprecated", "PTIO_APP_PURPOSE_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
