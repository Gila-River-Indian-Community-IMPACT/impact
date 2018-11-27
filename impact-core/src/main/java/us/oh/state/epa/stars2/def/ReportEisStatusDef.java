package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ReportEisStatusDef {
    public static final String NONE = "1no";
    public static final String NOT_FILED = "2nf";  // when report is being prepared by DEM
    public static final String SUBMITTED = "3ur";
    public static final String EIS_APPROVED = "4aa";
    private static final String defName = "ReportEisStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("RP_EIS_STATUS_DEF",
                    "EIS_STATUS_CD", "EIS_STATUS_DSC",
                    "deprecated", "EIS_STATUS_CD");
            
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
