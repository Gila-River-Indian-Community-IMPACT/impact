package us.wy.state.deq.impact.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class MapOptionsConfigDef extends SimpleDef{
	// ****************** Variables *******************
	private static final String defName = "MapOptionsConfigDef";
	
	private Double centerLat;
	private Double centerLng;
	private Integer zoomLevel;

	// ****************** Public Static Methods *******************	
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrieveMapOptionsConfig", 
            		MapOptionsConfigDef.class);

            cfgMgr.addDef(defName, data);
        } 
        return data;
    }
	
	public Double getCenterLat() {
		return centerLat;
	}

	public void setCenterLat(Double centerLat) {
		this.centerLat = centerLat;
	}

	public Double getCenterLng() {
		return centerLng;
	}

	public void setCenterLng(Double centerLng) {
		this.centerLng = centerLng;
	}

	public Integer getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(Integer zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public void populate(ResultSet rs){
		super.populate(rs);
		try{
			setCenterLat(rs.getDouble("center_lat"));
			setCenterLng(rs.getDouble("center_lng"));
			setZoomLevel(AbstractDAO.getInteger(rs, "zoom_level"));
			
		} catch (SQLException sqle){
			logger.warn("Optional field: " + sqle.getMessage());
		}
	}

	    
}
