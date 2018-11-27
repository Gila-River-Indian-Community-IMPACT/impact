package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the
 * constraint on the RPC_TYPE_CD column in the PA_RPC_TYPE_DEF table
 */
public class RPCTypeDef {
    public static final String TV_OFF_PERMIT_CHANGE = "TVOPC";
    public static final String TV_ADMIN_PERMIT_AMENDMENT = "TVAPA";
    public static final String PTIO_ADMIN_MOD = "PTIOAM";
    private static final String defName = "RPCTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PA_RPC_TYPE_DEF", "RPC_TYPE_CD",
                    "RPC_TYPE_DSC", "DEPRECATED", "RPC_TYPE_DSC");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
}

