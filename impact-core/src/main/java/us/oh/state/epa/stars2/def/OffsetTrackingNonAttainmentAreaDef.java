package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class OffsetTrackingNonAttainmentAreaDef extends SimpleDef {
	
	private static final String defName = "OffsetTrackingNonAttainmentAreaDef";
	
	private Integer areaShape;
	private String attainmentStandardCd;
	private String nonAttainmentLevelCd;
	
	public Integer getAreaShape() {
		return areaShape;
	}
	
	public void setAreaShape(Integer areaShape) {
		this.areaShape = areaShape;
	}
	
	public String getAttainmentStandardCd() {
		return attainmentStandardCd;
	}
	
	public void setAttainmentStandardCd(String attainmentStandardCd) {
		this.attainmentStandardCd = attainmentStandardCd;
	}
	
	public String getNonAttainmentLevelCd() {
		return nonAttainmentLevelCd;
	}
	
	public void setNonAttainmentLevelCd(String nonAttainmentLevelCd) {
		this.nonAttainmentLevelCd = nonAttainmentLevelCd;
	}
	
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setAreaShape(AbstractDAO.getInteger(rs, "area_shape"));
			setAttainmentStandardCd(rs.getString("attainment_standard_cd"));
			setNonAttainmentLevelCd(rs.getString("non_attainment_level_cd"));
		}catch(SQLException sqle) {
			logger.error("Required field error");
		}
	}

	public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveNonAttainmentAreas", 
            		OffsetTrackingNonAttainmentAreaDef.class);
            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    /**
	 * Returns a list of active items in the attainment area definition list
	 */
	public static List<OffsetTrackingNonAttainmentAreaDef> getDefListItems() {
		
		List<OffsetTrackingNonAttainmentAreaDef> defListItems
						= new ArrayList<OffsetTrackingNonAttainmentAreaDef>();
			
		Collection<BaseDef> baseDefCollection 
						= getData().getItems().getCompleteItems().values();
		
		for(BaseDef bd : baseDefCollection) {
			if(!bd.isDeprecated()) {
				defListItems.add((OffsetTrackingNonAttainmentAreaDef)bd);
			}
		}
		
		return defListItems;
	}
    
}
