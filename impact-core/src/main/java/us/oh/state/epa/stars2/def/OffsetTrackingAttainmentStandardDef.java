package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class OffsetTrackingAttainmentStandardDef extends SimpleDef {
	
	private static final String defName = "OffsetTrackingAttainmentStandardDef";
	
	public static final String OZONE_2008 = "STD10";
	public static final String PM25_2012 = "STD40";
	public static final String PM10_1987 = "STD70";
	public static final String SO2_2010 = "STD80";
	public static final String SO2_1978 = "STD90";
	public static final String LEAD_2008 = "STD100";
	public static final String CO_1971 = "STD120";
	public static final String NO2_1971 = "STD130";
	public static final String NO2_2010 = "STD140";
    
	public String authority;
	
	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setAuthority(rs.getString("authority"));
		}catch(SQLException sqle) {
			logger.error("Required field error");
		}
	}

	public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveAttainmentStandards", 
            					OffsetTrackingAttainmentStandardDef.class);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    /**
	 * Returns a list of active items in the attainment standards definition list
	 */
	public static List<OffsetTrackingAttainmentStandardDef> getDefListItems() {
		
		List<OffsetTrackingAttainmentStandardDef> defListItems
						= new ArrayList<OffsetTrackingAttainmentStandardDef>();
			
		Collection<BaseDef> baseDefCollection 
						= getData().getItems().getCompleteItems().values();
		
		for(BaseDef bd : baseDefCollection) {
			if(!bd.isDeprecated()) {
				defListItems.add((OffsetTrackingAttainmentStandardDef)bd);
			}
		}
		
		return defListItems;
	}

}