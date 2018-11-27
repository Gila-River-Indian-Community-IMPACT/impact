package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
public class ReportGroupTypeDef {
    // Note that these codes must match those used by class PermittClassDef
    //public static final String FER = "FER";
    //public static final String EIS = "EIS";
    //public static final String ES = "ES";
	public static final String TV = "TV";
    private static final String defName = "ReportGroupTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("SC_REPORT_GROUP_TYPE_DEF", "REPORT_GROUP_TYPE_CD",
                    "REPORT_GROUP_TYPE__DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}