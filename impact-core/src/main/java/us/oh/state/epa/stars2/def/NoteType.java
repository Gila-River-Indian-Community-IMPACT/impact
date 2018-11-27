package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class NoteType {
    public static final String DAPC = "dapc";
    public static final String APPC = "appc";
    public static final String OWNR = "ownr";
    public static final String HIST = "hist";
    public static final String IDCH = "idch";
    public static final String USER = "user";
    private static final String defName = "NoteType";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(DAPC, "AQD Comment");
            data.addItem(APPC, "Applicant Comment");
            data.addItem(OWNR, "Owner Comment");
            data.addItem(HIST, "Historical Comment");
            data.addItem(IDCH, "ID Changed Comment");
            data.addItem(USER, "System User");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
