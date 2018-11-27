package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FacilityPurgeLog extends BaseDB {
	private Integer purgeLogId;
	private String facilityId;
	private String facilityName;
	private String companyId;
	private String companyName;
	private Timestamp shutdownDate;
	private Timestamp purgedDate;
	private Integer userId;

	public Integer getPurgeLogId() {
		return purgeLogId;
	}

	public void setPurgeLogId(Integer purgeLogId) {
		this.purgeLogId = purgeLogId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Timestamp getShutdownDate() {
		return shutdownDate;
	}

	public void setShutdownDate(Timestamp shutdownDate) {
		this.shutdownDate = shutdownDate;
	}

	public Timestamp getPurgedDate() {
		return purgedDate;
	}

	public void setPurgedDate(Timestamp purgedDate) {
		this.purgedDate = purgedDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		try {
			setPurgeLogId(AbstractDAO.getInteger(rs, "purge_log_id"));
			setFacilityId(rs.getString("facility_id"));
			setFacilityName(rs.getString("facility_name"));
			setCompanyId(rs.getString("company_id"));
			setCompanyName(rs.getString("company_name"));
			setShutdownDate(rs.getTimestamp("facility_shutdown_date"));
			setPurgedDate(rs.getTimestamp("date_purged"));
			setUserId(AbstractDAO.getInteger(rs, "user_id"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

}
