package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.framework.util.LocationCalculator;

/**
 * @author Kbradley Note: (TO DO LATER) some attributes can go to base later.
 */
public class FacilityList extends MultiEstabFacilityList 
	implements Comparable<FacilityList> {
	
	private static final long serialVersionUID = -2017961423549348738L;

	private Address phyAddr;
	private String doLaaCd;
	private String operatingStatusCd;
	private String reportingTypeCd;
	private String permitClassCd;
	private String permitStatusCd;
	private boolean portable;

	// Used for NTV Blue Card generation (GenerateBulkEmRptReminder.java)
	// to temporarily hold the years needed.
	private transient Integer year;
	private transient Integer yearEven;
	private transient Integer yearOdd;
	private transient boolean ntvSelected;

	// used for Task Reassignments and User Task Reassignments
	private transient Integer userId;
	private transient Integer processId;
	private transient Integer activityTemplateId;
	private transient Integer loopCnt;
	private transient String activityTemplateNm;

	// used for User Facility Roles
	private transient String roleCd;

	// used for Facility Type
	private String facilityTypeCd;

	private String companyName;
	private String cmpId;

	private transient Timestamp startDate; // Used in memory not in database
	private transient Timestamp endDate; // Used in memory not in database
	
	public String getFacilityTypeDesc() {
		DefSelectItems defs = FacilityTypeDef.getTextData().getItems();
		return defs.getItemDesc(facilityTypeCd);
	}
	
	public String getCountyName() {
		return null == getPhyAddr()? null : getPhyAddr().getCountyName();
	}

	public String getLatLong() {
		return null == getPhyAddr()? null : getPhyAddr().getLatlong();
	}
	
	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public boolean whatIsSelected() {
		boolean rtn = true;
		if (yearEven != null || yearOdd != null) {
			rtn = ntvSelected;
		}
		return rtn;
	}

	public String getRoleCd() {
		return roleCd;
	}

	public void setRoleCd(String roleCd) {
		this.roleCd = roleCd;
	}

	public FacilityList() {
		super();
	}

	public FacilityList(FacilityList old) {
		super(old);
		setPhyAddr(old.getPhyAddr());
		setDoLaaCd(old.getDoLaaCd());
		setOperatingStatusCd(old.getOperatingStatusCd());
		setReportingTypeCd(old.getReportingTypeCd());
		setPermitClassCd(old.getPermitClassCd());
		setPermitStatusCd(old.getPermitStatusCd());
		setPortable(old.isPortable());
		setYear(old.getYear());
		setYearEven(old.getYearEven());
		setYearOdd(old.getYearOdd());
		setUserId(old.getUserId());
		setProcessId(old.getProcessId());
		setActivityTemplateId(old.getActivityTemplateId());
		setLoopCnt(old.getLoopCnt());
		setActivityTemplateNm(old.getActivityTemplateNm());
		setFacilityTypeCd(old.getFacilityTypeCd());
		setCompanyName(old.getCompanyName());
		setCmpId(old.getCmpId());

	}

	public final String getCountyCd() {
		return phyAddr.getCountyCd();
	}

	public final String getPermitClassCd() {
		return permitClassCd;
	}

	public final void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

	public final String getPermitStatusCd() {
		return permitStatusCd;
	}

	public final void setPermitStatusCd(String permitStatusCd) {
		this.permitStatusCd = permitStatusCd;
	}

	/**
	 * @param phyAddr
	 */
	public final void setPhyAddr(Address phyAddr) {
		this.phyAddr = phyAddr;
	}

	/**
	 * @return
	 */
	public final Address getPhyAddr() {
		return phyAddr;
	}

	public final String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public final void setOperatingStatusCd(String operatingStatusCd) {
		this.operatingStatusCd = operatingStatusCd;
	}

	public final String getReportingTypeCd() {
		return reportingTypeCd;
	}

	public final void setReportingTypeCd(String reportingTypeCd) {
		this.reportingTypeCd = reportingTypeCd;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public final void populate(ResultSet rs) {
		try {
			super.populate(rs);

			Address tempPhyAddr = new Address();
			tempPhyAddr.populate(rs);
			setPhyAddr(tempPhyAddr);

			setOperatingStatusCd(rs.getString("operating_status_cd"));
			setDoLaaCd(rs.getString("do_laa_cd"));
			setPermitClassCd(rs.getString("permit_classification_cd"));
			setPermitStatusCd(rs.getString("permit_status_cd"));
			setReportingTypeCd(EmissionReportsDef
					.reportingCategory(permitClassCd));
			setPortable(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("portable")));
			setLastModified(AbstractDAO.getInteger(rs, "facility_lm"));
			setFacilityTypeCd(rs.getString("facility_type_cd"));
			setCompanyName(rs.getString("name"));
			setCmpId(rs.getString("cmp_id"));

			try {
				setStartDate(rs.getTimestamp("start_date"));
			} catch (SQLException e) {
				//logger.error("bad start date");
			}

			try {
				setEndDate(rs.getTimestamp("end_date"));
			} catch (SQLException e) {
				//logger.error("bad end date");
			}

		} catch (SQLException sqle) {
			logger.error("Required field error");
		}
	}

	public String getDoLaaCd() {
		return doLaaCd;
	}

	public void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public boolean isPortable() {
		return portable;
	}

	public void setPortable(boolean portable) {
		this.portable = portable;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getYearEven() {
		return yearEven;
	}

	public void setYearEven(Integer yearEven) {
		this.yearEven = yearEven;
	}

	public Integer getYearOdd() {
		return yearOdd;
	}

	public void setYearOdd(Integer yearOdd) {
		this.yearOdd = yearOdd;
	}

	public final Integer getUserId() {
		return userId;
	}

	public final void setUserId(Integer userId) {
		this.userId = userId;
	}

	public final Integer getProcessId() {
		return processId;
	}

	public final void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public final Integer getActivityTemplateId() {
		return activityTemplateId;
	}

	public final void setActivityTemplateId(Integer activityTemplateId) {
		this.activityTemplateId = activityTemplateId;
	}

	public final Integer getLoopCnt() {
		return loopCnt;
	}

	public final void setLoopCnt(Integer loopCnt) {
		this.loopCnt = loopCnt;
	}

	public final String getActivityTemplateNm() {
		return activityTemplateNm;
	}

	public final void setActivityTemplateNm(String activityTemplateNm) {
		this.activityTemplateNm = activityTemplateNm;
	}

	public boolean isNtvSelected() {
		return ntvSelected;
	}

	public void setNtvSelected(boolean ntvSelected) {
		this.ntvSelected = ntvSelected;
	}

	public final String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public final void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public final String getGoogleMapsURL() {
		return LocationCalculator.getGoogleMapsURL(phyAddr.getLatitude(),
				phyAddr.getLongitude(), super.getName());
	}

	@Override
	public int compareTo(FacilityList other) {
		String otherFacilityId = other.getFacilityId();
		return getFacilityId().compareTo(otherFacilityId);
	}
}
