package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class WorkflowProcessDef {
    public static final String FACILITY_PROFILE_CHANGE = "fpch";
    public static final String EMISSION_REPORTING = "erpt";
    public static final String PERMITTING = "perm";
    public static final String OTHER = "oth";
    public static final String TODO_TASK = "task";
    public static final String CORRESPONDENCE = "cor";
    public static final String INSPECTION = "insp";
    private static final String defName = "WorkflowProcessDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("WF_PROCESS_DEF", "PROCESS_CD", "PROCESS_DSC",
                    "DEPRECATED");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public static boolean isValid(String cd) {
        if (cd == null) {
            return false;
        }
        if (cd.equals(FACILITY_PROFILE_CHANGE) || cd.equals(EMISSION_REPORTING)
                || cd.equals(PERMITTING) || cd.equals(TODO_TASK)) {
            return true;
        }
        return false;
    }
}
