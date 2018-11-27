package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceContinuousMonitorLineItem;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;

@SuppressWarnings("serial")
public class FacilityCemComLimit extends BaseDB implements Comparable<FacilityCemComLimit>{

	private Integer limitId;
	private Integer monitorId;
	private String limitDesc;
	private String limitSource;
	private Timestamp startDate;
	private Timestamp endDate;
	private String addlInfo;
	private Integer addedBy;
	private String limId;
	private String monId;
	private Integer corrLimitId;
	private String monDesc;

	public FacilityCemComLimit() {

		this.requiredField(limitId, "limit_id");
		this.requiredField(limitDesc, "limit_desc");
		this.requiredField(limitSource, "limit_source");
		this.requiredField(startDate, "start_dt");
		this.requiredField(addedBy, "added_by");

		setDirty(false);

	}

	public FacilityCemComLimit(FacilityCemComLimit old) {

		super(old);

		setLimitId(old.getLimitId());
		setMonitorId(old.getMonitorId());
		setLimitDesc(old.getLimitDesc());
		setLimitSource(old.getLimitSource());
		setStartDate(old.getStartDate());
		setEndDate(old.getEndDate());
		setAddlInfo(old.getAddlInfo());
		setAddedBy(old.getAddedBy());
		setLimId(old.getLimId());
		setMonId(old.getMonId());
		setCorrLimitId(old.getCorrLimitId());
		setMonDesc(old.getMonDesc());

		setLastModified(old.getLastModified());
		setDirty(old.isDirty());
	}

	public FacilityCemComLimit(FceContinuousMonitorLineItem limit) {
		super(limit);

		setLimitId(limit.getLimitId());
		setMonitorId(limit.getMonitorId());
		setLimitDesc(limit.getLimitDesc());
		setLimitSource(limit.getLimitSource());
		setStartDate(limit.getLimitStartDate());
		setEndDate(limit.getLimitEndDate());
		setAddlInfo(limit.getAddlInfo());
		setLimId(limit.getLimId());
		setMonId(limit.getMonId());
		setCorrLimitId(limit.getCorrLimitId());
		setMonDesc(limit.getMonitorDesc());
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
			setMonId(rs.getString("mon_id"));
			setMonDesc(rs.getString("mon_desc"));
			
			setLastModified(AbstractDAO.getInteger(rs, "fccl_lm"));
			setDirty(false);
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}

	}

	
	public String getMonDesc() {
		return monDesc;
	}

	public void setMonDesc(String monDesc) {
		this.monDesc = monDesc;
	}

	public Integer getLimitId() {
		return limitId;
	}

	public void setLimitId(Integer limitId) {
		this.limitId = limitId;
		this.requiredField(limitId, "limit_id");
		setDirty(true);
	}

	public Integer getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(Integer monitorId) {
		this.monitorId = monitorId;
		setDirty(true);
	}

	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
		this.requiredField(limitDesc, "limit_desc");
		setDirty(true);
	}
	
	public String getLimitSource() {
		return limitSource;
	}

	public void setLimitSource(String limitSource) {
		this.limitSource = limitSource;
		setDirty(true);
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
		this.requiredField(startDate, "start_dt");
		setDirty(true);
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public String getAddlInfo() {
		return addlInfo;
	}

	public void setAddlInfo(String addlInfo) {
		this.addlInfo = addlInfo;
	}

	public Integer getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(Integer addedBy) {
		this.addedBy = addedBy;
		this.requiredField(addedBy, "added_by");
		setDirty(true);
	}
	
	public String getLimId() {
		return limId;
	}

	public void setLimId(String limId) {
		this.limId = limId;
	}
	
	public String getMonId() {
		return monId;
	}

	public void setMonId(String monId) {
		this.monId = monId;
	}

	public Integer getCorrLimitId() {
		return corrLimitId;
	}

	public void setCorrLimitId(Integer corrLimitId) {
		this.corrLimitId = corrLimitId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((addedBy == null) ? 0 : addedBy.hashCode());
		result = prime * result
				+ ((addlInfo == null) ? 0 : addlInfo.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((limId == null) ? 0 : limId.hashCode());
		result = prime * result
				+ ((limitDesc == null) ? 0 : limitDesc.hashCode());
		result = prime * result + ((limitId == null) ? 0 : limitId.hashCode());
		result = prime * result
				+ ((limitSource == null) ? 0 : limitSource.hashCode());
		result = prime * result + ((monId == null) ? 0 : monId.hashCode());
		result = prime * result
				+ ((monitorId == null) ? 0 : monitorId.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
				+ ((corrLimitId == null) ? 0 : corrLimitId.hashCode());
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
		if (!(obj instanceof FacilityCemComLimit)) {
			return false;
		}
		FacilityCemComLimit other = (FacilityCemComLimit) obj;
		if (addedBy == null) {
			if (other.addedBy != null) {
				return false;
			}
		} else if (!addedBy.equals(other.addedBy)) {
			return false;
		}
		if (addlInfo == null) {
			if (other.addlInfo != null) {
				return false;
			}
		} else if (!addlInfo.equals(other.addlInfo)) {
			return false;
		}
		if (endDate == null) {
			if (other.endDate != null) {
				return false;
			}
		} else if (!endDate.equals(other.endDate)) {
			return false;
		}
		if (limId == null) {
			if (other.limId != null) {
				return false;
			}
		} else if (!limId.equals(other.limId)) {
			return false;
		}
		if (limitDesc == null) {
			if (other.limitDesc != null) {
				return false;
			}
		} else if (!limitDesc.equals(other.limitDesc)) {
			return false;
		}
		if (limitId == null) {
			if (other.limitId != null) {
				return false;
			}
		} else if (!limitId.equals(other.limitId)) {
			return false;
		}
		if (limitSource == null) {
			if (other.limitSource != null) {
				return false;
			}
		} else if (!limitSource.equals(other.limitSource)) {
			return false;
		}
		if (monId == null) {
			if (other.monId != null) {
				return false;
			}
		} else if (!monId.equals(other.monId)) {
			return false;
		}
		if (monitorId == null) {
			if (other.monitorId != null) {
				return false;
			}
		} else if (!monitorId.equals(other.monitorId)) {
			return false;
		}
		if (startDate == null) {
			if (other.startDate != null) {
				return false;
			}
		} else if (!startDate.equals(other.startDate)) {
			return false;
		}
		if (corrLimitId == null) {
			if (other.corrLimitId != null) {
				return false;
			}
		} else if (!corrLimitId.equals(other.corrLimitId)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(FacilityCemComLimit match) {
		int ret = -1;

		if (match != null) {
			if (match.getLimId() != null && this.getLimId() != null) {
				return this.getLimId().compareTo(match.getLimId());
			}
		}

		return ret;
	}
}
