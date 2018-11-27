package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
	
/* Release Point Operating Status */

public class PermitReceivedCommentsDef extends SimpleDef {
	public static final String NO = "NO";
    private static final String defName = "PermitReceivedCommentsDef";
   
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PermitSQL.retrieveReceivedCommentsTypes", PermitReceivedCommentsDef.class);
            //data.loadFromDB("PT_PERMIT_RECEIVED_COMMENTS_DEF", "TYPE_CD",
            //                "TYPE_DSC", "deprecated");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }
     
}
