package us.wy.state.deq.impact.database.dbObjects.monitoring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.def.MonitorReportTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class MonitorReport extends BaseDB {

	private Integer reportId;
	private String mrptId;
	private Integer monitorGroupId;
	private String mgrpId;
	private String facilityId;
	private String facilityName;
	private Integer currentFpId;
	private String reportTypeCd;
	private Integer year;
	private Integer quarter;
	private boolean legacyReport;
	private String description;
	private boolean validated;
	private boolean submitted;
	private Integer createdById;
	
	private Attachment[] attachments; // has to be an array for the XML encoding to work correctly
	
	private boolean staging = false;
	// AQD Staff
	private transient Timestamp submittedDate;
	private transient Timestamp monitoringReviewDate;
	private transient Integer monitoringReviewerId;
	private transient Timestamp complianceReviewDate;
	private transient Integer complianceReviewerId;
	private transient String reviewStatusCd;
	private transient String complianceStatusCd;
	private transient String permitNumber;
	private transient String reportAcceptedCd;
	private transient String aqdComments;
	
 	public MonitorReport() {
		super();
	}
	
	public MonitorReport(MonitorReport old) {
		super();
		if(null != old) {
			setReportId(old.getReportId());
			setMrptId(old.getMrptId());
			setMonitorGroupId(old.getMonitorGroupId());
			setReportTypeCd(old.getReportTypeCd());
			setYear(old.getYear());
			setQuarter(old.getQuarter());
			setLegacyReport(old.isLegacyReport());
			setDescription(old.getDescription());
			setValidated(old.isValidated());
			setSubmitted(old.isSubmitted());
			setSubmittedDate(old.getSubmittedDate());
			setMonitoringReviewDate(old.getMonitoringReviewDate());
			setMonitoringReviewerId(old.getMonitoringReviewerId());
			setComplianceReviewDate(old.getComplianceReviewDate());
			setComplianceReviewerId(old.getComplianceReviewerId());
			setReviewStatusCd(old.getReviewStatusCd());
			setComplianceStatusCd(old.getComplianceStatusCd());
			setPermitNumber(old.getPermitNumber());
			setReportAcceptedCd(old.getReportAcceptedCd());
			setAqdComments(old.getAqdComments());
			setLastModified(old.getLastModified());
			setCreatedById(old.getCreatedById());
		}
	}
	
	
	

	public String getMgrpId() {
		return mgrpId;
	}

	public void setMgrpId(String mgrpId) {
		this.mgrpId = mgrpId;
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

	public boolean isStaging() {
		return staging;
	}

	public void setStaging(boolean staging) {
		this.staging = staging;
	}

	public Integer getReportId() {
		return reportId;
	}
	
	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}
	
	public Integer getMonitorGroupId() {
		return monitorGroupId;
	}
	
	public void setMonitorGroupId(Integer monitorGroupId) {
		this.monitorGroupId = monitorGroupId;
	}
	
	public String getReportTypeCd() {
		return reportTypeCd;
	}
	
	public void setReportTypeCd(String reportTypeCd) {
		this.reportTypeCd = reportTypeCd;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Integer getQuarter() {
		return quarter;
	}
	
	public void setQuarter(Integer quarter) {
		this.quarter = quarter;
	}
	
	public boolean isLegacyReport() {
		return legacyReport;
	}
	
	public void setLegacyReport(boolean legacyReport) {
		this.legacyReport = legacyReport;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isSubmitted() {
		return submitted;
	}
	
	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}
	
	public Timestamp getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Timestamp submittedDate) {
		this.submittedDate = submittedDate;
	}

	public Timestamp getMonitoringReviewDate() {
		return monitoringReviewDate;
	}

	public void setMonitoringReviewDate(Timestamp monitoringReviewDate) {
		this.monitoringReviewDate = monitoringReviewDate;
	}

	public Integer getMonitoringReviewerId() {
		return monitoringReviewerId;
	}

	public void setMonitoringReviewerId(Integer monitoringReviewerId) {
		this.monitoringReviewerId = monitoringReviewerId;
	}

	public Timestamp getComplianceReviewDate() {
		return complianceReviewDate;
	}

	public void setComplianceReviewDate(Timestamp complianceReviewDate) {
		this.complianceReviewDate = complianceReviewDate;
	}

	public Integer getComplianceReviewerId() {
		return complianceReviewerId;
	}

	public void setComplianceReviewerId(Integer complianceReviewerId) {
		this.complianceReviewerId = complianceReviewerId;
	}

	public String getReviewStatusCd() {
		return reviewStatusCd;
	}

	public void setReviewStatusCd(String reviewStatusCd) {
		this.reviewStatusCd = reviewStatusCd;
	}

	public String getComplianceStatusCd() {
		return complianceStatusCd;
	}

	public void setComplianceStatusCd(String complianceStatusCd) {
		this.complianceStatusCd = complianceStatusCd;
	}

	public String getPermitNumber() {
		return permitNumber;
	}

	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	public String getReportAcceptedCd() {
		return reportAcceptedCd;
	}

	public void setReportAcceptedCd(String reportAcceptedCd) {
		this.reportAcceptedCd = reportAcceptedCd;
	}

	public String getAqdComments() {
		return aqdComments;
	}

	public void setAqdComments(String aqdComments) {
		this.aqdComments = aqdComments;
	}
	
	public String getMrptId() {
		return mrptId;
	}

	public void setMrptId(String mrptId) {
		this.mrptId = mrptId;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	
	public Integer getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		setReportId(AbstractDAO.getInteger(rs,"report_id"));
		setMrptId(rs.getString("mrpt_id"));
		setMonitorGroupId(AbstractDAO.getInteger(rs, "monitor_group_id"));
		setReportTypeCd(rs.getString("report_type_cd"));
		setYear(AbstractDAO.getInteger(rs, "year"));
		setQuarter(AbstractDAO.getInteger(rs, "quarter"));
		setLegacyReport(AbstractDAO.translateIndicatorToBoolean(rs.getString("legacy_report")));
		setDescription(rs.getString("description"));
		setValidated(AbstractDAO.translateIndicatorToBoolean(rs.getString("validated")));
		setSubmitted(AbstractDAO.translateIndicatorToBoolean(rs.getString("submitted")));
		setSubmittedDate(rs.getTimestamp("submitted_date"));
		setMonitoringReviewDate(rs.getTimestamp("monitoring_review_date"));
		setMonitoringReviewerId(AbstractDAO.getInteger(rs, "monitoring_reviewer_id"));
		setComplianceReviewDate(rs.getTimestamp("compliance_review_date"));
		setComplianceReviewerId(AbstractDAO.getInteger(rs, "compliance_reviewer_id"));
		setReviewStatusCd(rs.getString("review_status_cd"));
		setComplianceStatusCd(rs.getString("compliance_status_cd"));
		setPermitNumber(rs.getString("permit_nbr"));
		setReportAcceptedCd(rs.getString("rpt_accepted_cd"));
		setAqdComments(rs.getString("aqd_comments"));
		setCreatedById(AbstractDAO.getInteger(rs, "created_by_id"));
		setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		
	}
	
	public boolean isAnnualReportType() {
		return null != reportTypeCd ? reportTypeCd.equalsIgnoreCase(MonitorReportTypeDef.ANNUAL) : false;
	}
	
	public String getTruncatedDescription() {
		String ret = description;

		if(!Utility.isNullOrEmpty(ret) && ret.length() > 20) {		
			ret = description.substring(0, 20) + "...";
		}
		
		return ret;
	}
	
	public Attachment[] getAttachments() {
		if (attachments == null)
			return new Attachment[0];
		return attachments;
	}

	public boolean hasAttachments() {
		return getAttachments().length > 0;
	}

	public void setAttachments(Attachment[] attachments) {
		this.attachments = attachments;
	}

	public Integer getCurrentFpId() {
		return currentFpId;
	}

	public void setCurrentFpId(Integer currentFpId) {
		this.currentFpId = currentFpId;
	}

}
