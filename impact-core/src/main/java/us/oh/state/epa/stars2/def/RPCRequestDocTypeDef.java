package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PA_APPLICATION_DOC_TYPE_DEF table
 */
public class RPCRequestDocTypeDef {
    private static final String defName = "RPCRequestDocTypeDef";
    public static final String RO_SIGNATURE = "10";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_RPC_REQUEST_DOC_TYPE_DEF",
                    "RPC_REQUEST_DOC_TYPE_CD", "RPC_REQUEST_DOC_TYPE_DSC",
                    "deprecated", "RPC_REQUEST_DOC_TYPE_CD");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}
