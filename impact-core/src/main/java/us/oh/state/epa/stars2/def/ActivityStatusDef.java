package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * WF_ACTIVITY_STATUS_DEF table
 */
public class ActivityStatusDef {
    public static final String COMPLETED = "CM";
    public static final String IN_PROCESS = "IP";
    public static final String SUBFLOW_IN_PROCESS = "SP";
    public static final String PENDING = "PD";
    public static final String SKIPPED = "SK";
    public static final String NOT_COMPLETED = "ND";
    public static final String BLOCKED = "BK";
    public static final String WAITING_FOR_AUTO_RETRY = "WRTY";
    public static final String PROCESS_NOT_READY_FOR_PROVISIONING = "NRDY";
    public static final String CANCELLED = "CNC";
    public static final String REFERRED = "RF";
    public static final String UNREFERRED = "URF";
    public static final String ABANDONED = "AD";
    // INSERT INTO WF_ACTIVITY_STATUS_DEF (ACTIVITY_STATUS_CD,ACTIVITY_STATUS_DSC,LAST_MODIFIED) 
    // VALUES ('AD','Abandoned',1);
    private static final String defName = "ActivityStatusDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("WF_ACTIVITY_STATUS_DEF", "ACTIVITY_STATUS_CD",
                    "ACTIVITY_STATUS_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
