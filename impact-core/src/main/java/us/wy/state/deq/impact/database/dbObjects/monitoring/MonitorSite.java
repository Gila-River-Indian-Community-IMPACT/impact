package us.wy.state.deq.impact.database.dbObjects.monitoring;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.MonitorStatusDef;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.util.ImpactUtils;

@SuppressWarnings("serial")
public class MonitorSite extends BaseDB {

	private String groupName;

	private String groupDescription;

	private Integer groupId;

	private String mgrpId;

	private String siteName;

	private Integer siteId;

	private String mstId;

	private String county;

	private String countyName;

	private String facilityId;

	private String facilityName;

	private String companyId;

	private String companyName;

	private String status;

	private String aqsSiteId;

	private String latLon;

	private String siteObjective;

	private boolean inAqs = true;

	private Date startDate;

	private Date endDate;

	private BigDecimal latitude;

	private BigDecimal longitude;

	private Integer elevation;

	private String wyvisnet;

	private Integer fpId;

	private List<MonitorSiteNote> notes;

	private List<String> inspectionsReferencedIn;

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setAqsSiteId(rs.getString("aqs_site_id"));
			setCompanyId(rs.getString("cmp_id"));
			setCompanyName(rs.getString("company_name"));
			setCounty(rs.getString("county_cd"));
			setFacilityId(rs.getString("facility_id"));
			setFacilityName(rs.getString("facility_nm"));
			setFpId(rs.getInt("fp_id"));
			setMgrpId(rs.getString("mgrp_id"));
			setGroupId(rs.getInt("group_id"));
			setGroupName(rs.getString("group_name"));
			setLatLon(rs.getString("latlong"));
			setMstId(rs.getString("mst_id"));
			setSiteId(rs.getInt("site_id"));
			setSiteName(rs.getString("site_name"));
			setSiteObjective(rs.getString("site_objective"));
			setStatus(rs.getString("status_cd"));
			setInAqs(AbstractDAO.translateIndicatorToBoolean(rs.getString("exists_in_aqs")));
			setStartDate(rs.getDate("start_date"));
			setEndDate(rs.getDate("end_date"));
			setLatitude(rs.getBigDecimal("latitude"));
			setLongitude(rs.getBigDecimal("longitude"));
			setWyvisnet(rs.getString("wyvisnet_uri"));
			setGroupDescription(rs.getString("group_description"));
			setElevation(AbstractDAO.getInteger(rs, "elevation"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	public String getGoogleMapsURL() {
		return LocationCalculator.getGoogleMapsURL(getLatitude().floatValue(), getLongitude().floatValue(),
				getSiteName());
	}

	public String getStatusValue() {
		String value = null;
		BaseDef item = MonitorStatusDef.getData().getItem(getStatus());
		if (null != item) {
			value = item.getDescription();
		}
		return value;
	}

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getWyvisnet() {
		return ImpactUtils.toUri(wyvisnet);
	}

	public void setWyvisnet(String wyvisnet) {
		this.wyvisnet = wyvisnet;
	}

	public Integer getElevation() {
		return elevation;
	}

	public void setElevation(Integer elevation) {
		this.elevation = elevation;
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

	public boolean isInAqs() {
		return inAqs;
	}

	public void setInAqs(boolean inAqs) {
		this.inAqs = inAqs;
	}

	public String getMgrpId() {
		return mgrpId;
	}

	public void setMgrpId(String mgrpId) {
		this.mgrpId = mgrpId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSiteObjective() {
		return siteObjective;
	}

	public void setSiteObjective(String siteObjective) {
		this.siteObjective = siteObjective;
	}

	public String getAqsSiteId() {
		return aqsSiteId;
	}

	public void setAqsSiteId(String aqsSiteId) {
		this.aqsSiteId = aqsSiteId;
	}

	public String getLatLon() {
		return latLon;
	}

	public void setLatLon(String latLon) {
		this.latLon = latLon;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMstId() {
		return mstId;
	}

	public void setMstId(String mstId) {
		this.mstId = mstId;
	}

	@Override
	public ValidationMessage[] validate() {
		this.validationMessages.clear();

		if (Utility.isNullOrEmpty(this.getSiteName())) {
			ValidationMessage valMsg = new ValidationMessage("siteName", "Set a site name.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("siteName", valMsg);
		}

		if (this.isInAqs()) {
			if (Utility.isNullOrEmpty(this.getAqsSiteId())) {
				ValidationMessage valMsg = new ValidationMessage("aqsSiteId", "Set an AQS site ID.",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("aqsSiteId", valMsg);
			} else if (!validateAqsIdFormat(getAqsSiteId())) {
				ValidationMessage valMsg = new ValidationMessage("aqsSiteId",
						"AQS site ID must be of the format XX-XXX-XXXX", ValidationMessage.Severity.ERROR);
				this.validationMessages.put("aqsSiteId", valMsg);
			}
		}

		if (Utility.isNullOrEmpty(this.getSiteObjective())) {
			ValidationMessage valMsg = new ValidationMessage("siteObjective", "Set a site objective.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("siteObjective", valMsg);
		}

		if (this.getStartDate() == null) {
			ValidationMessage valMsg = new ValidationMessage("startDate", "Set a start date.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("startDate", valMsg);
		}

		if (this.getStatus() == null) {
			ValidationMessage valMsg = new ValidationMessage("status", "Set a status.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("status", valMsg);
		}

		String statusActiveCd = MonitorStatusDef.getActiveCode();
		if (null == statusActiveCd) {
			throw new RuntimeException("Cannot find monitor status active def code, unable to validate monitor site.");
		}
		if (!statusActiveCd.equals(this.getStatus()) && this.getEndDate() == null) {
			ValidationMessage valMsg = new ValidationMessage("endDate", "Set an end date.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("endDate", valMsg);
		}

		if (this.getStartDate() != null && this.getEndDate() != null && !statusActiveCd.equals(this.getStatus())
				&& this.getEndDate().before(this.getStartDate())) {
			ValidationMessage valMsg = new ValidationMessage("endDate", "End date is before start date.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("endDate", valMsg);
		}

		if (this.getLatitude() == null) {
			ValidationMessage valMsg = new ValidationMessage("latitude", "Set a latitude.",
					ValidationMessage.Severity.ERROR); // TODO latitude bounds
			this.validationMessages.put("latitude", valMsg);
		} else {
			checkRangeValues(this.getLatitude(), new BigDecimal(41.0), new BigDecimal(45.0), "latitude", "Latitude",
					"Monitor group: " + getGroupId());
		}

		if (this.getLongitude() == null) {
			ValidationMessage valMsg = new ValidationMessage("longitude", "Set a longitude.",
					ValidationMessage.Severity.ERROR); // TODO longitude bounds
			this.validationMessages.put("longitude", valMsg);
		} else {
			checkRangeValues(this.getLongitude(), new BigDecimal(-111.06), new BigDecimal(-104.05), "longitude",
					"Longitude", "Monitor group: " + getGroupId());
		}

		if (this.getCounty() == null) {
			ValidationMessage valMsg = new ValidationMessage("county", "Set a county.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("county", valMsg);
		}

		if (this.getElevation() != null) {
			checkRangeValues(this.getElevation(), basdElevationMim, basdElevationMax, "elevation", "Elevation",
					"Monitor group: " + getGroupId());
		}

		return new ArrayList<ValidationMessage>(validationMessages.values()).toArray(new ValidationMessage[0]);
	}

	private static final Integer basdElevationMax = 13809;
	private static final Integer basdElevationMim = 3101;

	private boolean validateAqsIdFormat(String aqsSiteId) {
		// regex: (\d{2})-(\d{3})-(\d{4})
		return null == aqsSiteId ? false : aqsSiteId.matches("(\\d{2})-(\\d{3})-(\\d{4})");
	}

	public final List<MonitorSiteNote> getNotes() {
		if (notes == null) {
			notes = new ArrayList<MonitorSiteNote>();
		}
		return notes;
	}

	public final void setNotes(List<MonitorSiteNote> notes) {
		this.notes = new ArrayList<MonitorSiteNote>();
		if (notes != null) {
			this.notes.addAll(notes);
		}
	}

	public final void addNote(MonitorSiteNote a) {
		if (notes == null) {
			notes = new ArrayList<MonitorSiteNote>();
		}
		if (a != null) {
			notes.add(a);
		}
	}

	public List<String> getInspectionsReferencedIn() {
		if (inspectionsReferencedIn == null)
			return new ArrayList<String>();
		return inspectionsReferencedIn;
	}

	public void setInspectionsReferencedIn(List<String> inspectionsReferencedIn) {
		this.inspectionsReferencedIn = inspectionsReferencedIn;
	}

}
