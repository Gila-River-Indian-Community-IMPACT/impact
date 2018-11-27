package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class OffsetTrackingDefaultMultiplierDef extends SimpleDef {
	
	private static final String defName = "OffsetTrackingDefaultMultiplierDef";
	
	private String pollutantCd;
	private String areaCd;
	private Float multiplier;
	private Timestamp effectiveDate;
	 
	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public String getAreaCd() {
		return areaCd;
	}

	public void setAreaCd(String areaCd) {
		this.areaCd = areaCd;
	}

	public Float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Float multiplier) {
		this.multiplier = multiplier;
	}

	public Timestamp getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	public final void populate(ResultSet rs) {
        super.populate(rs);

        try {
        	setPollutantCd(rs.getString("pollutant_cd"));
        	setAreaCd(rs.getString("area_cd"));
        	setMultiplier(rs.getFloat("multiplier"));
        	setEffectiveDate(rs.getTimestamp("effective_date"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);

		if (data == null) {
			data = new DefData();
			data.loadFromDB("InfrastructureSQL.retrieveDefaultOffsetMultipliers", 
								OffsetTrackingDefaultMultiplierDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	
	/**
	 * Returns a list of active items in the default multiplier definition list
	 */
	public static List<OffsetTrackingDefaultMultiplierDef> getDefListItems() {
		
		List<OffsetTrackingDefaultMultiplierDef> defListItems
						= new ArrayList<OffsetTrackingDefaultMultiplierDef>();
			
		Collection<BaseDef> baseDefCollection 
						= getData().getItems().getCompleteItems().values();
		
		for(BaseDef bd : baseDefCollection) {
			if(!bd.isDeprecated()) {
				defListItems.add((OffsetTrackingDefaultMultiplierDef)bd);
			}
		}
		
		return defListItems;
	}
}
