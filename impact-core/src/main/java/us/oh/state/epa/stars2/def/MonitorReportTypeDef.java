package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class MonitorReportTypeDef {
    private static final String defName = "MonitorReportTypeDef";
//    public static final String RO_SIGNATURE = "10";
    public static final String ANNUAL = "ANN";
    public static final String QUARTERLY = "QTR";
    public static final String OTHER = "OTH";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("MO_MONITOR_RPT_TYPE_DEF", "MO_MONITOR_RPT_TYPE_CD", "MO_MONITOR_RPT_TYPE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
