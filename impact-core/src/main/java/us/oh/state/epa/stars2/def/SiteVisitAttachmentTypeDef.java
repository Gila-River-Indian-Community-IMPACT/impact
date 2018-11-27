package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class SiteVisitAttachmentTypeDef {
    private static final String defName = "SiteVisitAttachmentTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_SV_ATTACHMENT_TYPE_DEF", "sv_attachment_type_cd", 
            		"sv_attachment_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
