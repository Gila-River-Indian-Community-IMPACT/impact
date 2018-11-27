package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;

@SuppressWarnings("serial")
public class ComplianceReportMonitor extends ContinuousMonitor {

	private Integer crMonitorId;
	private Integer reportId;
	private String auditStatus;
	private Timestamp testDate;
	private boolean certificationFlag;

	private String truncatedMonitorDetails;
	private String truncatedCurrentSerialNumber;

	private List<ComplianceReportLimit> _complianceReportLimitList;

	public ComplianceReportMonitor() {
		super();
	}

	public ComplianceReportMonitor(ComplianceReportMonitor old) {
		super(old);
		setCrMonitorId(old.getCrMonitorId());
		setReportId(old.getReportId());
		setAuditStatus(old.getAuditStatus());
		setTestDate(old.getTestDate());
		setCertificationFlag(old.isCertificationFlag());
	}

	public ComplianceReportMonitor(ContinuousMonitor monitor) {
		super(monitor);
	}

	public void populate(ResultSet rs) throws SQLException {
		try {
			super.populate(rs);

			setCrMonitorId(AbstractDAO.getInteger(rs, "cr_monitor_id"));
			setReportId(AbstractDAO.getInteger(rs, "report_id"));
			setContinuousMonitorId(AbstractDAO.getInteger(rs, "monitor_id"));
			setAuditStatus(rs.getString("audit_status"));
			setTestDate(rs.getTimestamp("test_dt"));
			setCertificationFlag(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("certification_flag")));

			setLastModified(AbstractDAO.getInteger(rs, "ccrm_lm")); // overwrite
																	// lastModified
																	// with
																	// value
																	// from
																	// cr_compliance_report_limit
																	// table

		} catch (SQLException sqle) {
			logger.error(sqle.getMessage());
		} finally {

		}
	}

	public final List<ComplianceReportLimit> getComplianceReportLimitList() {
		if (_complianceReportLimitList == null) {
			_complianceReportLimitList = new ArrayList<ComplianceReportLimit>(0);
		}
		return _complianceReportLimitList;
	}

	public final void setComplianceReportLimitList(
			List<ComplianceReportLimit> crlList) {
		_complianceReportLimitList = crlList;
		if (_complianceReportLimitList == null) {
			_complianceReportLimitList = new ArrayList<ComplianceReportLimit>(0);
		}
	}

	public String getSectionLabel() {
		String ret = null;
		ret = "Monitor: " + getMonId();

		return ret;
	}

	public final void setMonitorDetails(String monitorDetails) {
		super.setMonitorDetails(monitorDetails);
		if (monitorDetails != null && monitorDetails.length() > 50) {
			setTruncatedMonitorDetails(monitorDetails.substring(0, 50) + "...");
		} else {
			setTruncatedMonitorDetails(monitorDetails);
		}
		setDirty(true);
	}
	
	public final void setCurrentSerialNumber(String serialNumber) {
		super.setCurrentSerialNumber(serialNumber);
		if (serialNumber != null && serialNumber.length() > 50) {
			setTruncatedCurrentSerialNumber(serialNumber.substring(0, 50) + "...");
		} else {
			setTruncatedCurrentSerialNumber(serialNumber);
		}
		setDirty(true);
	}

	public Integer getCrMonitorId() {
		return crMonitorId;
	}

	public void setCrMonitorId(Integer crMonitorId) {
		this.crMonitorId = crMonitorId;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Timestamp getTestDate() {
		return testDate;
	}

	public void setTestDate(Timestamp testDate) {
		this.testDate = testDate;
	}

	public boolean isCertificationFlag() {
		return certificationFlag;
	}

	public void setCertificationFlag(boolean certificationFlag) {
		this.certificationFlag = certificationFlag;
	}

	public String getTruncatedMonitorDetails() {
		return truncatedMonitorDetails;
	}

	public void setTruncatedMonitorDetails(String truncatedMonitorDetails) {
		this.truncatedMonitorDetails = truncatedMonitorDetails;
	}
	
	public String getTruncatedCurrentSerialNumber() {
		return truncatedCurrentSerialNumber;
	}

	public void setTruncatedCurrentSerialNumber(String truncatedCurrentSerialNumber) {
		this.truncatedCurrentSerialNumber = truncatedCurrentSerialNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((_complianceReportLimitList == null) ? 0
						: _complianceReportLimitList.hashCode());
		result = prime * result
				+ ((auditStatus == null) ? 0 : auditStatus.hashCode());
		result = prime * result + (certificationFlag ? 1231 : 1237);
		result = prime * result
				+ ((crMonitorId == null) ? 0 : crMonitorId.hashCode());
		result = prime * result
				+ ((reportId == null) ? 0 : reportId.hashCode());
		result = prime * result
				+ ((testDate == null) ? 0 : testDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ComplianceReportMonitor)) {
			return false;
		}
		ComplianceReportMonitor other = (ComplianceReportMonitor) obj;
		if (_complianceReportLimitList == null) {
			if (other._complianceReportLimitList != null) {
				return false;
			}
		} else if (!_complianceReportLimitList
				.equals(other._complianceReportLimitList)) {
			return false;
		}
		if (auditStatus == null) {
			if (other.auditStatus != null) {
				return false;
			}
		} else if (!auditStatus.equals(other.auditStatus)) {
			return false;
		}
		if (certificationFlag != other.certificationFlag) {
			return false;
		}
		if (crMonitorId == null) {
			if (other.crMonitorId != null) {
				return false;
			}
		} else if (!crMonitorId.equals(other.crMonitorId)) {
			return false;
		}
		if (reportId == null) {
			if (other.reportId != null) {
				return false;
			}
		} else if (!reportId.equals(other.reportId)) {
			return false;
		}
		if (testDate == null) {
			if (other.testDate != null) {
				return false;
			}
		} else if (!testDate.equals(other.testDate)) {
			return false;
		}
		return true;
	}
	
	public String getCertificationFlagYesNo() {
		return isCertificationFlag() ? "Yes" : "No";
	}

}
