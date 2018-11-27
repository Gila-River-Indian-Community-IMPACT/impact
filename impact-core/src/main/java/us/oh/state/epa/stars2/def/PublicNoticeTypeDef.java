package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Release Point Operating Status */

public class PublicNoticeTypeDef {
    public static final String STANDARD_WORDING = "SW";
    public static final String PSD_STANDARD_WORDING = "PSW";
    public static final String CUSTOMIZED_WORDING = "CW";
    public static final String CUSTOMIZED_WORDING_IN_SUPPLIED_FILE = "SF";
    private static final String defName = "PublicNoticeTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.addItem(STANDARD_WORDING, "Standard Wording");
            data.addItem(PSD_STANDARD_WORDING, "PSD Standard Wording");
            data.addItem(CUSTOMIZED_WORDING, "Customized Wording");
            data.addItem(CUSTOMIZED_WORDING_IN_SUPPLIED_FILE,
                    "Customized Wording in Supplied File");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static DefData getData(String type) {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
       
            data = new DefData();
            data.addItem(STANDARD_WORDING, "Standard Wording");
            if(type != null && type.equals(PermitTypeDef.NSR)){
            	data.addItem(PSD_STANDARD_WORDING, "PSD Standard Wording");
            }
            data.addItem(CUSTOMIZED_WORDING, "Customized Wording");
            data.addItem(CUSTOMIZED_WORDING_IN_SUPPLIED_FILE,
                    "Customized Wording in Supplied File");

            cfgMgr.addDef(defName, data);
        
        return data;
    }
}
