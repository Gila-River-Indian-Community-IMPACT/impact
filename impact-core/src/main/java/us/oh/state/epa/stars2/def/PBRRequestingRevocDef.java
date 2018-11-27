package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the ... column in the ... table
 */
public class PBRRequestingRevocDef {
    public static final String NO = "N";
    public static final String REVOCATE_PTIOS = "I";
    public static final String REVOCATE_PTOS = "O";
    private static final String defName = "PBRRequestingRevocDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(NO, "No");
            data.addItem(REVOCATE_PTIOS, "Yes, revocating PTI/PTIO's");
            data.addItem(REVOCATE_PTOS, "Yes, revocating PTO's");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
