package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_PER_DUE_DATE_DEF table
 */
public class PTIOPERDueDateDef {
    // code for the "Not Applicable" entry in the table
    public static final String NOT_APPLICABLE = "0";
    private static final String defName = "PTIOPERDueDateDef";
    private static final String defStartName = "PTIOPERDueDateStartDef";
    private static final String defMonthName = "PTIOPERDueDateMonthDef";
    private static final String defEndName = "PTIOPERDueDateEndDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PER_DUE_DATE_DEF", "PER_DUE_DATE_CD",
                            "PER_DUE_DATE_dsc", "deprecated", "PER_DUE_DATE_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static DefData getStartOfPeriod() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defStartName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PER_DUE_DATE_DEF", "PER_DUE_DATE_CD",
                                     "REPORTING_START_DT", "deprecated");

            cfgMgr.addDef(defStartName, data);
        }
        return data;
    }
    
    public static DefData getEndOfPeriod() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defEndName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PER_DUE_DATE_DEF", "PER_DUE_DATE_CD",
                                   "REPORTING_END_DT", "deprecated");
            
            cfgMgr.addDef(defEndName, data);
        }
        return data;
    }
    
    public static DefData getDueDateMonthDay() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defMonthName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_PER_DUE_DATE_DEF", "PER_DUE_DATE_CD",
                                     "PER_DUE_DATE_DT ", "deprecated");
            
            cfgMgr.addDef(defMonthName, data);
        }
        return data;
    }
}
