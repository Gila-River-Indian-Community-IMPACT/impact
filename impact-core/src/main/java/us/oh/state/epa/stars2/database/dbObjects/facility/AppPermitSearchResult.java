package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;

@SuppressWarnings("serial")
public class AppPermitSearchResult extends BaseDB {
	private String id;
	private String company;
	private String facility;
	private String description;
	private String facilityId;
	private String appPermitTypeCd;
	
	private static final Map<String,String> APP_PERMIT_TYPE_MAP = new HashMap<String,String>();
	
	static {
		APP_PERMIT_TYPE_MAP.put("NSR", "NSR");
		APP_PERMIT_TYPE_MAP.put("TVPTO", "Title V");
		APP_PERMIT_TYPE_MAP.put("PTIO", "NSR");
		APP_PERMIT_TYPE_MAP.put("TV", "Title V");
	}
	
	public static final int APP_PERMIT_TYPE_NSR = 1;
	
	public static final int APP_PERMIT_TYPE_TV = 2;
	
	public static final int APP_PERMIT_TYPE_BOTH = 3;
	
	public static final int SEARCH_TYPE_APPLICATIONS = 1;
	
	public static final int SEARCH_TYPE_PERMITS = 2;
	
	public static final int SEARCH_TYPE_BOTH = 3;
	
	public String getAppPermitType() {
		return APP_PERMIT_TYPE_MAP.get(getAppPermitTypeCd());
	}
	
	public String getAppPermitTypeCd() {
		return appPermitTypeCd;
	}

	public void setAppPermitTypeCd(String appPermitTypeCd) {
		this.appPermitTypeCd = appPermitTypeCd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		setId(rs.getString("number"));
		setCompany(rs.getString("company_name"));
		setFacility(rs.getString("facility_name"));
		setDescription(rs.getString("description"));
		setFacilityId(rs.getString("facility_id"));
		setAppPermitTypeCd(rs.getString("app_permit_type_cd"));
	}

	public boolean isNsr() {
		return PermitTypeDef.NSR.equals(getAppPermitTypeCd()) ||
				ApplicationTypeDef.PTIO_APPLICATION.equals(getAppPermitTypeCd());
	}

	public boolean isTv() {
		return PermitTypeDef.TV_PTO.equals(getAppPermitTypeCd()) ||
				ApplicationTypeDef.TITLE_V_APPLICATION.equals(getAppPermitTypeCd());
	}

}
