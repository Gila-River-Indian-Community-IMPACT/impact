package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * fp_attachment_type_def table
 */
public class FacilityAttachmentTypeDef {
    private static final String defName = "FacilityAttachmentTypeDef";
    public static final String RO_SIGNATURE = "10";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_attachment_type_def", "attachment_type_cd",
                    "attachment_type_dsc", "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
