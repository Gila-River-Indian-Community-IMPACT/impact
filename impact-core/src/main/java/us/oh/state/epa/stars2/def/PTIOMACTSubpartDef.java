package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;

/*
 * The codes declared in this class must match the content of the
 * PA_MACT_SUBPART_DEF table
 */
@SuppressWarnings("serial")
public class PTIOMACTSubpartDef extends SimpleDef  {
    private static final String defName = "PTIOMACTSubpartDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveMactSubparts", 
                    PTIOMACTSubpartDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static PTIOMACTSubpartDef getSubpartDef(String cd)
    throws ApplicationException{
        PTIOMACTSubpartDef vc = (PTIOMACTSubpartDef)PTIOMACTSubpartDef.getData().getItems().getItem(cd);
        return vc;
    }
}
