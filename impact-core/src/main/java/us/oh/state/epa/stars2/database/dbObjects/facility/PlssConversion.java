package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class PlssConversion extends BaseDB{

	private static final long serialVersionUID = -6850337539451812856L;
	private String latitude;
	private String longitude;
	private String section;
	private String township;
	private String range;
	private String distance;
	
	public final String getLatitude() {
		return this.latitude;
	}

	public final void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public final String getLongitude() {
		return longitude;
	}

	public final void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public final String getSection() {
		return this.section;
	}

	public final void setSection(String section) {
		this.section = section;
	}
	
	public final String getTownship() {
		return this.township;
	}

	public final void setTownship(String township) {
		this.township = township;
	}
	
	public final String getRange() {
		return this.range;
	}
	
	public final void setRange(String range) {
		this.range = range;
	}
	
	public final String getDistance() {
		return this.distance;
	}
	
	public final void setDistance(String distance) {
		this.distance = distance;
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setLatitude(rs.getString("latitude"));
			setLongitude(rs.getString("longitude"));
			setSection(rs.getString("section"));
			setTownship(rs.getString("township"));
			setRange(rs.getString("range"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

}
