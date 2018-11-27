package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TVClassification {
    public static final String NOT_APPLICABLE = "na";
    public static final String NON_INSIGNIFICANT = "nins";
    public static final String TRIVIAL = "trv";
    public static final String INSIG = "ins";
    public static final String INSIG_NO_APP_REQS = "innr";
    private static final String defName = "TVClassification";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_tv_classification_def", "tv_classification_cd",
                    "tv_classification_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
