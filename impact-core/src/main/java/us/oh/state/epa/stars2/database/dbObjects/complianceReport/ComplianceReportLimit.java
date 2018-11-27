package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;

@SuppressWarnings("serial")
public class ComplianceReportLimit extends FacilityCemComLimit {

	private Integer crLimitId;

	private Integer crMonitorId;

	private boolean includedFlag;

	private String limitStatus;

	public ComplianceReportLimit() {
		super();
	}

	public ComplianceReportLimit(ComplianceReportLimit old) {

		super(old);

		setCrLimitId(old.getCrLimitId());
		setCrMonitorId(old.getCrMonitorId());
		setIncludedFlag(old.isIncludedFlag());
		setLimitStatus(old.getLimitStatus());
	}

	public ComplianceReportLimit(FacilityCemComLimit limit) {

		super(limit);
	}

	public void populate(ResultSet rs) {

		try {
			setLimitId(AbstractDAO.getInteger(rs, "limit_id"));
			setMonitorId(AbstractDAO.getInteger(rs, "monitor_id"));
			setLimitDesc(rs.getString("limit_desc"));
			setLimitSource(rs.getString("limit_source"));
			setStartDate(rs.getTimestamp("start_dt"));
			setEndDate(rs.getTimestamp("end_dt"));
			setAddlInfo(rs.getString("addl_info"));
			setAddedBy(AbstractDAO.getInteger(rs, "added_by"));
			setLimId(rs.getString("lim_id"));
			setCorrLimitId(AbstractDAO.getInteger(rs, "corr_limit_id"));

			setCrLimitId(AbstractDAO.getInteger(rs, "cr_limit_id"));
			setLimitId(AbstractDAO.getInteger(rs, "limit_id"));
			setCrMonitorId(AbstractDAO.getInteger(rs, "cr_monitor_id"));
			setIncludedFlag(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("included_flag")));
			setLimitStatus(rs.getString("limit_status"));
			setLastModified(AbstractDAO.getInteger(rs, "ccrl_lm")); // overwrite
																	// lastModified
																	// with
																	// value
																	// from
																	// cr_compliance_report_limit
																	// table

			setDirty(false);
		} catch (SQLException sqle) {
			logger.debug("Required field error");
		}

	}

	public String getIncludedFlagYesNo() {
		return isIncludedFlag() ? "Yes" : "No";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((crLimitId == null) ? 0 : crLimitId.hashCode());
		result = prime * result
				+ ((crMonitorId == null) ? 0 : crMonitorId.hashCode());
		result = prime * result + (includedFlag ? 1231 : 1237);
		result = prime * result
				+ ((limitStatus == null) ? 0 : limitStatus.hashCode());
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
		if (!(obj instanceof ComplianceReportLimit)) {
			return false;
		}
		ComplianceReportLimit other = (ComplianceReportLimit) obj;
		if (crLimitId == null) {
			if (other.crLimitId != null) {
				return false;
			}
		} else if (!crLimitId.equals(other.crLimitId)) {
			return false;
		}
		if (crMonitorId == null) {
			if (other.crMonitorId != null) {
				return false;
			}
		} else if (!crMonitorId.equals(other.crMonitorId)) {
			return false;
		}
		if (includedFlag != other.includedFlag) {
			return false;
		}
		if (limitStatus == null) {
			if (other.limitStatus != null) {
				return false;
			}
		} else if (!limitStatus.equals(other.limitStatus)) {
			return false;
		}
		return true;
	}

	public Integer getCrLimitId() {
		return crLimitId;
	}

	public void setCrLimitId(Integer crLimitId) {
		this.crLimitId = crLimitId;
	}

	public Integer getCrMonitorId() {
		return crMonitorId;
	}

	public void setCrMonitorId(Integer crMonitorId) {
		this.crMonitorId = crMonitorId;
	}

	public boolean isIncludedFlag() {
		return includedFlag;
	}

	public void setIncludedFlag(boolean includedFlag) {
		this.includedFlag = includedFlag;
	}

	public String getLimitStatus() {
		return limitStatus;
	}

	public void setLimitStatus(String limitStatus) {
		this.limitStatus = limitStatus;
	}
}
