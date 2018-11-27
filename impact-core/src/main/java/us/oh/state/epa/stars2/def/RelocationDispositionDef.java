package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class RelocationDispositionDef {
    public static final String NOTIFICATION_RECEIVED = "notr";
    public static final String APPROVED = "appr";
    public static final String APPROVED_CONDITIONALLY = "acon";
    public static final String DENIED = "dend";
    public static final String RETURNED = "retd";
    private static final String defName = "RelocationDispositionDef";
    private static final String defApprovedName = "RelocationDispositionApprDef";
    private static final String defApprovedCondName = "RelocationDispositionApprCondDef";
    private static final String defDeniedName = "RelocationDispositionDeniedDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_RELOCATION_DISPOSITION_DEF", "REQUEST_DISPOSITION_CD",
                    "REQUEST_DISPOSITION_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static DefData getApprovedData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defApprovedName);
        
        if (data == null) {
            data = new DefData();
            data.addExcludedKey(RelocationDispositionDef.APPROVED_CONDITIONALLY);
            data.addExcludedKey(RelocationDispositionDef.DENIED);
            data.loadFromDB("PA_RELOCATION_DISPOSITION_DEF", "REQUEST_DISPOSITION_CD",
                    "REQUEST_DISPOSITION_DSC", "deprecated");

            cfgMgr.addDef(defApprovedName, data);
        }
        return data;
    }
    
    public static DefData getApprovedCondData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defApprovedCondName);
        
        if (data == null) {
            data = new DefData();
            data.addExcludedKey(RelocationDispositionDef.APPROVED);
            data.addExcludedKey(RelocationDispositionDef.DENIED);
            data.loadFromDB("PA_RELOCATION_DISPOSITION_DEF", "REQUEST_DISPOSITION_CD",
                    "REQUEST_DISPOSITION_DSC", "deprecated");

            cfgMgr.addDef(defApprovedCondName, data);
        }
        return data;
    }
    
    public static DefData getDeniedData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defDeniedName);
        
        if (data == null) {
            data = new DefData();
            data.addExcludedKey(RelocationDispositionDef.APPROVED);
            data.addExcludedKey(RelocationDispositionDef.APPROVED_CONDITIONALLY);
            data.loadFromDB("PA_RELOCATION_DISPOSITION_DEF", "REQUEST_DISPOSITION_CD",
                    "REQUEST_DISPOSITION_DSC", "deprecated");

            cfgMgr.addDef(defDeniedName, data);
        }
        return data;
    }
}

