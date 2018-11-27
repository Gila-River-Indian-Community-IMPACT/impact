package us.oh.state.epa.stars2.def;

import org.springframework.beans.factory.annotation.Autowired;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class State {
	
	public static final String DEFAULT_STATE = SystemPropertyDef.getSystemPropertyValue("StateCd", null);
    public static final String OUT_STATE_COUNTY = "23";
    public static final String OUT_STATE_COUNTY_NAME = "Out_of_state_county";
    public static final String OUT_STATE_CITY = "Out_of_state_city";
    private static final String defName = "State";
    private static final String defCtyName = "StateCountry";
    private static final String defAdjStateName = "AdjacentState";
    
    @Autowired SystemPropertyDef systemPropertyDef;
	
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_state_def", "state_cd", "state_nm", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static DefData getCountryData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defCtyName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_state_def", "state_cd", "country_cd", "deprecated");

            cfgMgr.addDef(defCtyName, data);
        }

        return data;
    }
    
    public static String getCountryCd(String stateCd) {
    	return State.getCountryData().getItems().getDescFromAllItem(stateCd);
    }
    
    public static DefData getAdjacentStateData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defAdjStateName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveAdjacentStates", SimpleDef.class);

            cfgMgr.addDef(defAdjStateName, data);
        }

        return data;
    }
    
}
