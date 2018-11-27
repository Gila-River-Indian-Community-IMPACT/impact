package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class GeoPolygonDef extends SimpleDef {
	
	private static final String defName = "GeoPolygonDef";

	private String label = null;
	private Double latitudeSwDegrees = null;
	private Double longitudeSwDegrees = null;
	private Double latitudeSeDegrees = null;
	private Double longitudeSeDegrees = null;
	private Double latitudeNeDegrees = null;
	private Double longitudeNeDegrees = null;
	private Double latitudeNwDegrees = null;
	private Double longitudeNwDegrees = null;
	
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
            data.loadFromDB("FacilitySQL.retrieveGeoPolygonDefs", 
            		GeoPolygonDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	

    public void populate(ResultSet rs) {
        super.populate(rs);
        try {
        	setCode(rs.getString("code"));
        	setLabel(rs.getString("label"));
        	setDescription(rs.getString("description"));
        	
        	setLatitudeNeDegrees(rs.getDouble("lat_ne_deg"));
        	setLatitudeNwDegrees(rs.getDouble("lat_nw_deg"));
        	setLatitudeSeDegrees(rs.getDouble("lat_se_deg"));
        	setLatitudeSwDegrees(rs.getDouble("lat_sw_deg"));
        	
        	setLongitudeNeDegrees(rs.getDouble("lon_ne_deg"));
        	setLongitudeNwDegrees(rs.getDouble("lon_nw_deg"));
        	setLongitudeSeDegrees(rs.getDouble("lon_se_deg"));
        	setLongitudeSwDegrees(rs.getDouble("lon_sw_deg"));
        	
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
        }
    }
    
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Double getLatitudeSwDegrees() {
		return latitudeSwDegrees;
	}
	public void setLatitudeSwDegrees(Double latitudeSwDegrees) {
		this.latitudeSwDegrees = latitudeSwDegrees;
	}
	public Double getLongitudeSwDegrees() {
		return longitudeSwDegrees;
	}
	public void setLongitudeSwDegrees(Double longitudeSwDegrees) {
		this.longitudeSwDegrees = longitudeSwDegrees;
	}
	public Double getLatitudeSeDegrees() {
		return latitudeSeDegrees;
	}
	public void setLatitudeSeDegrees(Double latitudeSeDegrees) {
		this.latitudeSeDegrees = latitudeSeDegrees;
	}
	public Double getLongitudeSeDegrees() {
		return longitudeSeDegrees;
	}
	public void setLongitudeSeDegrees(Double longitudeSeDegrees) {
		this.longitudeSeDegrees = longitudeSeDegrees;
	}
	public Double getLatitudeNeDegrees() {
		return latitudeNeDegrees;
	}
	public void setLatitudeNeDegrees(Double latitudeNeDegrees) {
		this.latitudeNeDegrees = latitudeNeDegrees;
	}
	public Double getLongitudeNeDegrees() {
		return longitudeNeDegrees;
	}
	public void setLongitudeNeDegrees(Double longitudeNeDegrees) {
		this.longitudeNeDegrees = longitudeNeDegrees;
	}
	public Double getLatitudeNwDegrees() {
		return latitudeNwDegrees;
	}
	public void setLatitudeNwDegrees(Double latitudeNwDegrees) {
		this.latitudeNwDegrees = latitudeNwDegrees;
	}
	public Double getLongitudeNwDegrees() {
		return longitudeNwDegrees;
	}
	public void setLongitudeNwDegrees(Double longitudeNwDegrees) {
		this.longitudeNwDegrees = longitudeNwDegrees;
	}
	
	
}
