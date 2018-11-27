package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.webcommon.reports.ReportBaseCommon;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
@SuppressWarnings("serial")
public class NspsPollutantDef extends PollutantDef {
    private static final String defName = "NspsPollutantDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrievePollutants",
            		NspsPollutantDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static NspsPollutantDef getPollutantBaseDef(String cd)
    throws ApplicationException{
        NspsPollutantDef bd = (NspsPollutantDef)NspsPollutantDef.getData().getItems().getItem(cd);
        if(null == bd){
            throw new ApplicationException(" (" + ReportBaseCommon.dataProb +
                    ": pollutant_cd \"" + cd + "\" not found)");
        }
        return bd;
    }
    
    public boolean isDeprecated() {
        return ( super.isDeprecated() || ( !isNsps() ));
    }
}
