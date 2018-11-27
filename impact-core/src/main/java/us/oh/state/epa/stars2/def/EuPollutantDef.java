package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
@SuppressWarnings("serial")
public class EuPollutantDef extends PollutantDef {
    public static String HAP = "HAP";
    private static final String defName = "EuPollutantDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrievePollutants",
            		EuPollutantDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public boolean isDeprecated() {
        return ( super.isDeprecated() || ( !isEmissionsTable() ));
    }
    
    public static String getCategory(String pollutant) {
    	try {
    		String catg =PollutantDef.getTheCategory(pollutant);
    		if(catg.toLowerCase().contains("hap")) {
    			return "HAP";
    		} else if(catg.toLowerCase().contains("cap")) {
    			return "CAP";
    		} else {
    			return "OTHER";
    		}
    	} catch (ApplicationException ae) {
    		return "OTHER";
    	}
    }
}
