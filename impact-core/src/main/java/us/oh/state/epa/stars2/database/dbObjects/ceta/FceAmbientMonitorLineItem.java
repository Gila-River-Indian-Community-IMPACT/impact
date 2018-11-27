package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.MonitorParamTypeDef;
import us.oh.state.epa.stars2.def.MonitorTypeDef;
import us.oh.state.epa.stars2.def.WxParamDef;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;
import us.oh.state.epa.stars2.framework.util.Utility;

public class FceAmbientMonitorLineItem extends BaseDB {

	private static final long serialVersionUID = 1L;

	private Integer monitorId;

	private Integer siteId;

	private String mstId;

	private String siteName;

	private String type;

	private String name;

	private String parameter; // ambient monitor parameter

	private String parameterMet; // meteorological monitor parameter

	private String status;

	private Date startDate;

	private Date endDate;

	private String frequencyCode;

	private String mntrId;

	private String parameterDesc;

	private String aqsSiteId;
	
	private String siteStatus;

	private String latLon;
	
	private BigDecimal latitude;
	
	private BigDecimal longitude;
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setMonitorId(AbstractDAO.getInteger(rs, "monitor_id"));
			setSiteId(AbstractDAO.getInteger(rs, "site_id"));
			setType(rs.getString("type_cd"));
			setParameter(rs.getString("parameter_cd"));
			setStatus(rs.getString("status_cd"));
			setName(rs.getString("name"));
			setStartDate(rs.getDate("site_start_date"));
			setEndDate(rs.getDate("site_end_date"));
			setFrequencyCode(rs.getString("freq_cd"));
			setMntrId(rs.getString("mntr_id"));
			setMstId(rs.getString("mst_id"));
			setSiteName(rs.getString("site_name"));
			setParameterMet(rs.getString("met_parameter_cd"));
			setAqsSiteId(rs.getString("aqs_site_id"));
			setSiteStatus(rs.getString("site_status"));
			setLatLon(rs.getString("latlong"));
			setLatitude(rs.getBigDecimal("latitude"));
			setLongitude(rs.getBigDecimal("longitude"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	public  String getGoogleMapsURL() {
		return LocationCalculator.getGoogleMapsURL(
				getLatitude().floatValue(),
				getLongitude().floatValue(), 
				getSiteName());
	}
	
	public final String getParameterDesc() {
		this.parameterDesc = null;
		if (this.isTypeAmbient()) {
			this.parameterDesc = MonitorParamTypeDef.getData().getItems().getItemDesc(getParameter());
		} else if (this.isTypeMeteorological()) {
			this.parameterDesc = WxParamDef.getData().getItems().getItemDesc(getParameterMet());
		}
		return this.parameterDesc;
	}

	public final boolean isTypeMeteorological() {
		if (!Utility.isNullOrEmpty(this.getType())) {
			if (this.getType().equals(MonitorTypeDef.METEOROLOGICAL)) {
				return true;
			}

		}
		return false;
	}

	public final boolean isTypeAmbient() {
		if (!Utility.isNullOrEmpty(this.getType())) {
			if (this.getType().equals(MonitorTypeDef.AMBIENT_AIR)) {
				return true;
			}

		}
		return false;
	}

	public Integer getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Integer monitorId) {
		this.monitorId = monitorId;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getMstId() {
		return mstId;
	}

	public void setMstId(String mstId) {
		this.mstId = mstId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getFrequencyCode() {
		return frequencyCode;
	}

	public void setFrequencyCode(String frequencyCode) {
		this.frequencyCode = frequencyCode;
	}

	public String getMntrId() {
		return mntrId;
	}

	public void setMntrId(String mntrId) {
		this.mntrId = mntrId;
	}

	public String getParameterMet() {
		return parameterMet;
	}

	public void setParameterMet(String parameterMet) {
		this.parameterMet = parameterMet;
	}

	public String getAqsSiteId() {
		return aqsSiteId;
	}

	public void setAqsSiteId(String aqsSiteId) {
		this.aqsSiteId = aqsSiteId;
	}

	public String getSiteStatus() {
		return siteStatus;
	}

	public void setSiteStatus(String siteStatus) {
		this.siteStatus = siteStatus;
	}

	public String getLatLon() {
		return latLon;
	}

	public void setLatLon(String latLon) {
		this.latLon = latLon;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}


}
