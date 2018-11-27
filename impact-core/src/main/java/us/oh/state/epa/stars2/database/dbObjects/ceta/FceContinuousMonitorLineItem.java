package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class FceContinuousMonitorLineItem extends BaseDB {

	private static final long serialVersionUID = 1L;

	private Integer limitId;
	private Integer corrLimitId;
	private String limId;
	private Integer monitorId;
	private String monId;
	private String monitorDesc;
	private Timestamp monitorInstallDate;
	private Timestamp lastAuditDate;
	private String limitDesc;
	private String limitSource;
	private Timestamp limitStartDate;
	private Timestamp limitEndDate;
	private String addlInfo;

	@Override
	public void populate(ResultSet rs) throws SQLException {
		setLimitId(AbstractDAO.getInteger(rs, "limit_id"));
		setCorrLimitId(AbstractDAO.getInteger(rs, "corr_limit_id"));
		setLimId(rs.getString("lim_id"));
		setMonitorId(AbstractDAO.getInteger(rs, "monitor_id"));
		setMonId(rs.getString("mon_id"));
		setMonitorDesc(rs.getString("monitor_desc"));
		setMonitorInstallDate(rs.getTimestamp("install_date"));
		setLastAuditDate(rs.getTimestamp("last_audit_dt"));
		setLimitDesc(rs.getString("limit_desc"));
		setLimitSource(rs.getString("limit_source"));
		setLimitStartDate(rs.getTimestamp("start_dt"));
		setLimitEndDate(rs.getTimestamp("end_dt"));
		setAddlInfo(rs.getString("addl_info"));
	}

	public Integer getLimitId() {
		return limitId;
	}

	public void setLimitId(Integer limitId) {
		this.limitId = limitId;
	}

	public Integer getCorrLimitId() {
		return corrLimitId;
	}

	public void setCorrLimitId(Integer corrLimitId) {
		this.corrLimitId = corrLimitId;
	}

	public String getLimId() {
		return limId;
	}

	public void setLimId(String limId) {
		this.limId = limId;
	}

	public Integer getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Integer monitorId) {
		this.monitorId = monitorId;
	}

	public String getMonId() {
		return monId;
	}

	public void setMonId(String monId) {
		this.monId = monId;
	}

	public String getMonitorDesc() {
		return monitorDesc;
	}

	public void setMonitorDesc(String monitorDesc) {
		this.monitorDesc = monitorDesc;
	}

	public Timestamp getMonitorInstallDate() {
		return monitorInstallDate;
	}

	public void setMonitorInstallDate(Timestamp monitorInstallDate) {
		this.monitorInstallDate = monitorInstallDate;
	}

	public Timestamp getLastAuditDate() {
		return lastAuditDate;
	}

	public void setLastAuditDate(Timestamp lastAuditDate) {
		this.lastAuditDate = lastAuditDate;
	}

	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}

	public String getLimitSource() {
		return limitSource;
	}

	public void setLimitSource(String limitSource) {
		this.limitSource = limitSource;
	}

	public Timestamp getLimitStartDate() {
		return limitStartDate;
	}

	public void setLimitStartDate(Timestamp limitStartDate) {
		this.limitStartDate = limitStartDate;
	}

	public Timestamp getLimitEndDate() {
		return limitEndDate;
	}

	public void setLimitEndDate(Timestamp limitEndDate) {
		this.limitEndDate = limitEndDate;
	}

	public String getAddlInfo() {
		return addlInfo;
	}

	public void setAddlInfo(String addlInfo) {
		this.addlInfo = addlInfo;
	}

}
