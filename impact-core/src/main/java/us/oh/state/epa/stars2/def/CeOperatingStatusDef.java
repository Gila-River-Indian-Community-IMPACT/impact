package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Control Equipment Operating Status */

public class CeOperatingStatusDef {
    public static final String OP = "op";
    public static final String NOP = "nop";
    private static final String defName = "CeOperatingStatusDef";
    private static DefData data;
    private static long refreshTime;
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data1 = cfgMgr.getDef(defName);
        
        if (data1 == null) {
        	data1 = new DefData();
        	data1.loadFromDB("fp_operating_status_def", "operating_status_cd",
                    "operating_status_dsc", "deprecated");
            cfgMgr.addDef(defName, data1);
            refreshTime = -1;
        }
        
        DefSelectItems selItems = data1.getItems();
        
        if (data1.getRefreshTime() != refreshTime) {
        	data = new DefData();
        	data.addItem(OP, selItems.getItemDesc(OP), selItems.isItemDepricated(OP));
        	data.addItem(NOP, selItems.getItemDesc(NOP), selItems.isItemDepricated(NOP));
        	refreshTime = data1.getRefreshTime();
        }
        
        return data;
    }
}
