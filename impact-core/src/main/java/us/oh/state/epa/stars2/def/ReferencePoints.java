package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class ReferencePoints implements java.io.Serializable  {
    public static final String R106 = "106";
    private static final String defName = "ReferencePoints";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("fp_reference_point", "reference_point_cd",
                    "reference_point_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
