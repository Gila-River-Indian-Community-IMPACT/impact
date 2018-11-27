package us.oh.state.epa.stars2.database.dbObjects.ves;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class TransferLogEntry extends BaseDB {

	private static final long serialVersionUID = 2755848537165470117L;

	private Long id;
	
	private String status;
	
	private String username;
	
	private Long duration;
	
	private Timestamp startDate;
	
	private Timestamp endDate;

	private String type;
	
	private String message;

	private Integer reportingYear;
	
	private String facilityTypes;
	
	private Integer progressPercent;
	
	private String domain;

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setId(rs.getLong("transfer_id"));
			Long duration = rs.getLong("duration");
			setDuration(rs.wasNull()? null : duration);
			setEndDate(rs.getTimestamp("end_dt"));
			setLastModified(rs.getInt("last_modified"));
			setMessage(rs.getString("message"));
			setStartDate(rs.getTimestamp("start_dt"));
			setStatus(rs.getString("status"));
			setType(rs.getString("transfer_type"));
			setUsername(rs.getString("created_by"));
			setReportingYear(0 == rs.getInt("reporting_year")? null : rs.getInt("reporting_year"));
			setProgressPercent(rs.getInt("progress_percent"));
			setFacilityTypes(rs.getString("facility_types"));
			setDomain(rs.getString("domain"));
        } catch (SQLException sqle) {
            logger.warn(sqle.getMessage());
        }	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getDuration() {
		return duration;
	}
	
	public String getDurationFormatted() {
		PeriodFormatter formatter = new PeriodFormatterBuilder()
				.minimumPrintedDigits(2)
				.appendHours()
				.appendSuffix("h")
				.appendSeparator(":")
				.appendMinutes()
				.appendSuffix("m")
				.appendSeparator(":")
				.appendSeconds()
				.appendSuffix("s")
				.toFormatter();

		Duration d = null;
		if (null != getDuration()) {
			d = new Duration(getDuration());
		} else {
			if (null != getStartDate()) {
				d = new Duration((new Date()).getTime() - getStartDate().getTime());
			}
		}
		return formatter.print(d.toPeriod());
	}

	public void setDuration(Long duration) {
		this.duration = duration;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getReportingYear() {
		return reportingYear;
	}

	public void setReportingYear(Integer reportingYear) {
		this.reportingYear = reportingYear;
	}

	public String getFacilityTypes() {
		return facilityTypes;
	}

	public void setFacilityTypes(String facilityTypes) {
		this.facilityTypes = facilityTypes;
	}

	public Integer getProgressPercent() {
		return progressPercent;
	}

	public void setProgressPercent(Integer progressPercent) {
		this.progressPercent = progressPercent;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	
}
