package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceReportMonitorAndLimitAuditStatusDef {
	
	private static final String defName = "ComplianceReportMonitorAndLimitAuditStatusDef";
		
	public static final String PASS = "PASS";
	public static final String FAIL = "FAIL";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("CR_MONITOR_AND_LIMIT_AUDIT_STATUS_DEF", "STATUS_CD",
                            "STATUS_DSC", "DEPRECATED");
            cfgMgr.addDef(defName, data);
        }
        return data;
    }

}
