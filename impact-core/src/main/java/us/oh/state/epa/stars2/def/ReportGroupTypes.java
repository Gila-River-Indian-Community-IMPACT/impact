package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ReportGroupTypes {
    //public static final String FERType = "FER";
    //public static final String EISType = "EIS";
   // public static final String ESType = "ES";
	public static final String TVType = "TV";
    private static final String defName = "ReportGroupTypes";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("sc_report_group_type_def", "report_group_type_cd",
                    "report_group_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
