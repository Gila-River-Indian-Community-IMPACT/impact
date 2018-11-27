package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CorrespondenceDirectionDef {
    private static final String defName = "CorrespondenceDirectionDef";
    public static final String INCOMING = "in";
    public static final String OUTGOING = "out";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("dc_correspondence_direction_def", "correspondence_direction_cd",
                    "correspondence_direction_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
