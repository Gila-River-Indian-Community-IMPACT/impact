package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class TimesheetMonthlyReportTypeDef {
	
    public static final String EXEMPT_GROUPED_REPORT = "EG";
    public static final String NON_EXEMPT_GROUPED_REPORT = "NEG";
	private static final String defName = "TimesheetMonthlyReportTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("ts_monthly_report_type_def", "monthly_report_type_cd",
                            "monthly_report_type_dsc", "deprecated");
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
    
