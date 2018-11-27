package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class BasicUsersDef extends SimpleIdDef {
    private static final String defName = "BasicUsersDef";
    private static final String activeUserdefName = "ActiveUsersDef";
    private static final String allUserdefName = "AllUsersDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData(true);
            data.loadFromDB("InfrastructureSQL.retrieveBasicUsers", BasicUsersDef.class);

            cfgMgr.addDef(defName, data);
        } 
        
        return data;
    }
    
    public static String getUserNm(Integer userId) {
        return getData().getItems().getDescFromAllItem(userId);
    }
    
    public static boolean getAllUserStatus(Integer userId) {
        return getData().getItems().isItemCurrent(userId);
    }
    
    public static DefData getAllActiveUserData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(activeUserdefName);
        
        if (data == null) {
            data = new DefData(true);
            data.loadFromDB("InfrastructureSQL.retrieveActiveUserList", BasicUsersDef.class);

            cfgMgr.addDef(activeUserdefName, data);
        } 
        
        return data;
    }
    
    public static DefData getAllUserData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(allUserdefName);
        
        if (data == null) {
            data = new DefData(true);
            data.loadFromDB("InfrastructureSQL.retrieveAllUsersList", BasicUsersDef.class);

            cfgMgr.addDef(allUserdefName, data);
        } 
        
        return data;
    }
}
