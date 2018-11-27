package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class LimitTrendReportLineItem extends BaseDB {
	private Integer reportId;
	private String reportCRPTId;
	private String monId;
	private String monitorDetails;
	private String limId;
	private String limitDesc;
	private Integer reportYear;
	private Integer reportQuarter;
	private Timestamp receivedDate;
	private Timestamp QAQCAcceptedDate;
	private String facilityId;
	private String auditStatus;
	private Boolean certification;
	private Timestamp testDate;
	private String limitStatus;
	private String otherTypeCd;
	
	public LimitTrendReportLineItem() {
		super();
	}
	
	public LimitTrendReportLineItem(LimitTrendReportLineItem old) {
		super();
		if(null != old) {
			setReportId(old.getReportId());
			setReportCRPTId(old.getReportCRPTId());
			setMonId(old.getMonId());
			setMonitorDetails(old.getMonitorDetails());
			setLimId(old.getLimId());
			setLimitDesc(old.getLimitDesc());
			setReportYear(old.getReportYear());
			setReportQuarter(old.getReportQuarter());
			setReceivedDate(old.getReceivedDate());
			setQAQCAcceptedDate(old.getQAQCAcceptedDate());
			setFacilityId(old.getFacilityId());
			setAuditStatus(old.getAuditStatus());
			setCertification(old.getCertification());
			setTestDate(old.getTestDate());
			setLimitStatus(old.getLimitStatus());
			setOtherTypeCd(old.getOtherTypeCd());
		}
	}
	
	public String getAuditType() {
		return "21".equals(getOtherTypeCd())? "RATA" : null;
	}
	
	public String getStatus() {
		return null == getAuditStatus()? getLimitStatus() : getAuditStatus();
	}

	public String getCertificationYesNo() {
		return getCertification()? "Yes" : null;
	}
	
	public String getOtherTypeCd() {
		return otherTypeCd;
	}

	public void setOtherTypeCd(String otherTypeCd) {
		this.otherTypeCd = otherTypeCd;
	}

	public String getLimitStatus() {
		return limitStatus;
	}

	public void setLimitStatus(String limitStatus) {
		this.limitStatus = limitStatus;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Boolean getCertification() {
		return certification;
	}

	public void setCertification(Boolean certification) {
		this.certification = certification;
	}

	public Timestamp getTestDate() {
		return testDate;
	}

	public void setTestDate(Timestamp testDate) {
		this.testDate = testDate;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getReportCRPTId() {
		return reportCRPTId;
	}

	public void setReportCRPTId(String reportCRPTId) {
		this.reportCRPTId = reportCRPTId;
	}

	public String getMonId() {
		return monId;
	}

	public void setMonId(String monId) {
		this.monId = monId;
	}

	public String getMonitorDetails() {
		return monitorDetails;
	}

	public void setMonitorDetails(String monitorDetails) {
		this.monitorDetails = monitorDetails;
	}

	public String getLimId() {
		return limId;
	}

	public void setLimId(String limId) {
		this.limId = limId;
	}

	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}

	public Integer getReportYear() {
		return reportYear;
	}

	public void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}

	public Integer getReportQuarter() {
		return reportQuarter;
	}

	public void setReportQuarter(Integer reportQuarter) {
		this.reportQuarter = reportQuarter;
	}

	public Timestamp getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Timestamp receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Timestamp getQAQCAcceptedDate() {
		return QAQCAcceptedDate;
	}

	public void setQAQCAcceptedDate(Timestamp qAQCAcceptedDate) {
		QAQCAcceptedDate = qAQCAcceptedDate;
	}
	
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setReportId(AbstractDAO.getInteger(rs, "report_id"));
			setReportCRPTId(rs.getString("crpt_id"));
			setMonId(rs.getString("mon_id"));
			setMonitorDetails(rs.getString("monitor_desc"));
			setLimId(rs.getString("lim_id"));
			setLimitDesc(rs.getString("limit_desc"));
			setReportYear(AbstractDAO.getInteger(rs, "report_yr"));
			setReportQuarter(AbstractDAO.getInteger(rs, "report_qtr"));
			setReceivedDate(rs.getTimestamp("received_date"));
			setQAQCAcceptedDate(rs.getTimestamp("qaqc_accepted_date"));
			setFacilityId(rs.getString("facility_id"));
			setAuditStatus(rs.getString("audit_status"));
			setCertification(AbstractDAO.translateIndicatorToBoolean(rs.getString("certification_flag")));
			setTestDate(rs.getTimestamp("test_dt"));
			setLimitStatus(rs.getString("limit_status"));
			setOtherTypeCd(rs.getString("other_type_cd"));
		}
	}
}
