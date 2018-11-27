package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_APPLICATION_DOC_TYPE_DEF table
 */
public class PBRNotifDocTypeDef {
    private static final String defName = "PBRNotifDocTypeDef";
    public static final String PBR_SUPPLEMENTAL_FORMS = "0";
    public static final String OTHER = "1";
    public static final String RO_SIGNATURE = "10";
    
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PBR_NOTIF_DOC_TYPE_DEF",
                    "PBR_NOTIF_DOC_TYPE_CD", "PBR_NOTIF_DOC_TYPE_DSC",
                    "deprecated", "PBR_NOTIF_DOC_TYPE_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
