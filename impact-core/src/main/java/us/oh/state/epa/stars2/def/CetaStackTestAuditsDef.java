package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class CetaStackTestAuditsDef {

private static final String defName = "CetaStackTestAuditsDef";

public static final String YES = "Y";
public static final String NO = "N";
public static final String NA = "NA";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();   
            data.loadFromDB("CE_Stack_Test_Audits_def", "StackTest_Audits_cd",
                    "StackTest_Audits_dsc", "DEPRECATED");
           

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
