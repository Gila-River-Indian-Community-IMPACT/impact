package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class USEPAOutcomeDef {
    public static final String NO_RESPONSE = "N";
    public static final String APPROVED = "A";
    public static final String REJECTED = "R";
    public static final String WAIVED = "W";
    private static final String defName = "USEPAOutcomeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(NO_RESPONSE, "No response");
            data.addItem(APPROVED, "Approved");
            data.addItem(REJECTED, "Rejected");
            data.addItem(WAIVED, "Waived");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }

    public static boolean isValid(String outcomeCd) {
        if (outcomeCd == null) {
            return false;
        }
        if (outcomeCd.equals(NO_RESPONSE) || outcomeCd.equals(APPROVED)
                || outcomeCd.equals(REJECTED) || outcomeCd.equals(WAIVED)) {
            return true;
        }
        return false;
    }
}
