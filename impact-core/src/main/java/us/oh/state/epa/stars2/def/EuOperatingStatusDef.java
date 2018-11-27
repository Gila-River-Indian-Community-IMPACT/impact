package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/* Emission Unit Operating Status */

public class EuOperatingStatusDef {
    public static final String NI = "ni";
    public static final String IV = "iv";
    public static final String IA = "ia";
    public static final String OP = "op";
    public static final String SD = "sd";
    private static final String defName = "EuOperatingStatusDef";
    private static final String defDemName = "EuOperatingStatusDemDef";
    private static DefData data;
    private static DefData demData;
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
        	data.addItem(NI, selItems.getItemDesc(NI), selItems.isItemDepricated(NI));
        	data.addItem(IV, selItems.getItemDesc(IV), selItems.isItemDepricated(IV));
        	data.addItem(OP, selItems.getItemDesc(OP), selItems.isItemDepricated(OP));
        	data.addItem(SD, selItems.getItemDesc(SD), selItems.isItemDepricated(SD));
        	refreshTime = data1.getRefreshTime();
        }
        
        return data;
    }

    public static DefData getDemData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data1 = cfgMgr.getDef(defDemName);
        
        if (data1== null) {
            data1 = new DefData(true);
            data1.loadFromDB("fp_operating_status_def", "operating_status_cd",
                    "operating_status_dsc", "deprecated");
            cfgMgr.addDef(defDemName, data1);
            refreshTime = -1;
        }
        
        DefSelectItems selItems = data1.getItems();
        
        if (data1.getRefreshTime() != refreshTime) {
        	demData = new DefData();
        	demData.addItem(NI, selItems.getItemDesc(NI), selItems.isItemDepricated(NI));
        	demData.addItem(IV, selItems.getItemDesc(IV), selItems.isItemDepricated(IV));
        	demData.addItem(OP, selItems.getItemDesc(OP), selItems.isItemDepricated(OP));
        	refreshTime = data1.getRefreshTime();
    	}
        
        return data;
    }
}
