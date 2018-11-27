package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class RelocationAttachmentTypeDef {
    private static final String defName = "RelocationAttachmentTypeDef";
    public static final String RO_SIGNATURE = "10";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_ITR_TYPE_DEF", "ITR_ATTACHMENT_TYPE_CD", "ITR_ATTACHMENT_TYPE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
