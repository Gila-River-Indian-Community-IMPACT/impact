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
public class OffsetTrackingContributingPollutantDef extends SimpleDef {

	private static final String defName = "OffsetTrackingContributingPollutantDef";
	
	private String attainmentStandardCd;
	private String pollutantCd;
	
	public String getAttainmentStandardCd() {
		return attainmentStandardCd;
	}

	public void setAttainmentStandardCd(String attainmentStandardCd) {
		this.attainmentStandardCd = attainmentStandardCd;
	}

	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setAttainmentStandardCd(rs.getString("attainment_standard_cd"));
			setPollutantCd(rs.getString("pollutant_cd"));
			
		}catch(SQLException sqle) {
			logger.error("Required field error");
		}
	}

	public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveContributingPollutants", 
            		OffsetTrackingContributingPollutantDef.class);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    /**
	 * Returns a list of active items in the contributing pollutants definition list
	 */
	public static List<OffsetTrackingContributingPollutantDef> getDefListItems() {
		
		List<OffsetTrackingContributingPollutantDef> defListItems
						= new ArrayList<OffsetTrackingContributingPollutantDef>();
			
		Collection<BaseDef> baseDefCollection 
						= getData().getItems().getCompleteItems().values();
		
		for(BaseDef bd : baseDefCollection) {
			if(!bd.isDeprecated()) {
				defListItems.add((OffsetTrackingContributingPollutantDef)bd);
			}
		}
		
		return defListItems;
	}
}
