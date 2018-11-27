package us.oh.state.epa.stars2.database.dbObjects.application;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class TVEUOperatingScenario extends BaseDB {
	private Integer tvEuOperatingScenarioId;
	private String tvEuOperatingScenarioNm;
	private Integer applicationEuId;
	private Integer opSchedHrsDay;
	private Integer opSchedHrsYr;
	private Timestamp engOrderDate;
	private Long engOrderDateLong;
	private Timestamp engManufactureDate;
	private Long engManufactureDateLong;
	private boolean opSchedTradeSecret;
	private String opAosAutherized;
	private String opSchedTradeSecretReason;
	private String operationLimits;
	private String operationLimitsDsc;
	private List<TVApplicationEUEmissions> capEmissions;
	private List<TVApplicationEUEmissions> hapEmissions;
	private List<TVApplicationEUEmissions> ghgEmissions;
	private List<TVApplicationEUEmissions> othEmissions;
	private List<TVAltScenarioPteReq> pteRequirements;
	public BigDecimal hapsTotal;
	public BigDecimal ghgsTotal;

	public BigDecimal getHapsTotal() {
		calculateHAPsEUPollutantTotal();
		return hapsTotal;
	}

	public void setHapsTotal(BigDecimal hapsTotal) {
		this.hapsTotal = hapsTotal;
	}

	public BigDecimal getGhgsTotal() {
		calculateGHGsEUPollutantTotal();
		return ghgsTotal;
	}

	public void setGhgsTotal(BigDecimal ghgsTotal) {
		this.ghgsTotal = ghgsTotal;
	}

	public TVEUOperatingScenario() {
		super();
	}

	public TVEUOperatingScenario(TVEUOperatingScenario old) {
		super(old);
		if (old != null) {
			//this.capEmissions = old.capEmissions;
			//this.hapEmissions = old.hapEmissions;
			//this.ghgEmissions = old.ghgEmissions;
			//this.othEmissions = old.othEmissions;
			//this.pteRequirements = old.pteRequirements;
			
			
			setTvEuOperatingScenarioId(old.tvEuOperatingScenarioId);
			setTvEuOperatingScenarioNm(old.tvEuOperatingScenarioNm);
			setApplicationEuId(old.applicationEuId);
			setOpSchedHrsDay(old.opSchedHrsDay);
			setOpSchedHrsYr(old.opSchedHrsYr);
			setEngOrderDate(old.engOrderDate);
			setEngOrderDateLong(old.engOrderDateLong);
			setEngManufactureDate(old.engManufactureDate);
			setEngManufactureDateLong(old.engOrderDateLong);
			setOpSchedTradeSecret(old.opSchedTradeSecret);
			setOpAosAutherized(old.opAosAutherized);
			setOpSchedTradeSecretReason(old.opSchedTradeSecretReason);
			setOperationLimits(old.operationLimits);
			setOperationLimitsDsc(old.operationLimitsDsc);
			
			this.capEmissions = TVApplicationEUEmissions.cloneList(old.capEmissions);
			this.hapEmissions = TVApplicationEUEmissions.cloneList(old.hapEmissions);
			this.ghgEmissions = TVApplicationEUEmissions.cloneList(old.ghgEmissions);
			this.othEmissions = TVApplicationEUEmissions.cloneList(old.othEmissions);
			this.pteRequirements = TVAltScenarioPteReq.cloneList(old.pteRequirements);
			
			setHapsTotal(old.hapsTotal);
			setGhgsTotal(old.ghgsTotal);
		}
	}

	public void populate(ResultSet rs) throws SQLException {
		setTvEuOperatingScenarioId(AbstractDAO.getInteger(rs,
				"tv_eu_operating_scenario_id"));
		setTvEuOperatingScenarioNm(rs.getString("tv_eu_operating_scenario_nm"));
		setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
		setOpSchedHrsDay(AbstractDAO.getInteger(rs, "op_sched_hrs_day"));
		setOpSchedHrsYr(AbstractDAO.getInteger(rs, "op_sched_hrs_yr"));
		setOpSchedTradeSecret(AbstractDAO.translateIndicatorToBoolean(rs
				.getString("op_sched_trade_secret")));
		setOpAosAutherized(rs.getString("op_aos_autherized"));
		setOpSchedTradeSecretReason(rs
				.getString("reason_op_sched_trade_secret"));
		setOperationLimits(rs.getString("operation_limits"));
		setOperationLimitsDsc(rs.getString("operation_limits_dsc"));
		setEngOrderDate(rs.getTimestamp("engine_order_dt"));
		setEngManufactureDate(rs.getTimestamp("engine_manufacture_dt"));
		setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
	}

	public final Integer getApplicationEuId() {
		return applicationEuId;
	}

	public final void setApplicationEuId(Integer applicationEuId) {
		this.applicationEuId = applicationEuId;
	}

	public final boolean isSourceOperationLimits() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(operationLimits)
				&& AbstractDAO.translateIndicatorToBoolean(operationLimits);
		return ret;
	}

	public String getOperationLimits() {
		return operationLimits;
	}

	public final void setOperationLimits(String operationLimits) {
		this.operationLimits = operationLimits;
	}

	public final String getOperationLimitsDsc() {
		return operationLimitsDsc;
	}

	public final void setOperationLimitsDsc(String operationLimitsDsc) {
		this.operationLimitsDsc = operationLimitsDsc;
	}

	public final Integer getOpSchedHrsDay() {
		return opSchedHrsDay;
	}

	public final void setOpSchedHrsDay(Integer opSchedHrsDay) {
		this.opSchedHrsDay = opSchedHrsDay;
	}

	public final Integer getOpSchedHrsYr() {
		return opSchedHrsYr;
	}

	public final void setOpSchedHrsYr(Integer opSchedHrsYr) {
		this.opSchedHrsYr = opSchedHrsYr;
	}

	public final boolean isOpSchedTradeSecret() {
		return opSchedTradeSecret;
	}

	public final void setOpSchedTradeSecret(boolean opSchedTradeSecret) {
		this.opSchedTradeSecret = opSchedTradeSecret;
	}

	public final String getOpSchedTradeSecretReason() {
		return opSchedTradeSecretReason;
	}

	public final void setOpSchedTradeSecretReason(
			String opSchedTradeSecretReason) {
		this.opSchedTradeSecretReason = opSchedTradeSecretReason;
	}

	public final Integer getTvEuOperatingScenarioId() {
		return tvEuOperatingScenarioId;
	}

	public final void setTvEuOperatingScenarioId(Integer tvEuOperatingScenarioId) {
		this.tvEuOperatingScenarioId = tvEuOperatingScenarioId;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((applicationEuId == null) ? 0 : applicationEuId.hashCode());
		result = PRIME * result
				+ ((opSchedHrsDay == null) ? 0 : opSchedHrsDay.hashCode());
		result = PRIME * result
				+ ((opSchedHrsYr == null) ? 0 : opSchedHrsYr.hashCode());
		result = PRIME * result + (opSchedTradeSecret ? 1231 : 1237);
		result = PRIME
				* result
				+ ((opSchedTradeSecretReason == null) ? 0
						: opSchedTradeSecretReason.hashCode());
		result = PRIME
				* result
				+ (AbstractDAO.translateIndicatorToBoolean(operationLimits) ? 1231
						: 1237);
		result = PRIME
				* result
				+ ((operationLimitsDsc == null) ? 0 : operationLimitsDsc
						.hashCode());
		result = PRIME
				* result
				+ ((tvEuOperatingScenarioId == null) ? 0
						: tvEuOperatingScenarioId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TVEUOperatingScenario other = (TVEUOperatingScenario) obj;
		if (applicationEuId == null) {
			if (other.applicationEuId != null)
				return false;
		} else if (!applicationEuId.equals(other.applicationEuId))
			return false;
		if (opSchedHrsDay == null) {
			if (other.opSchedHrsDay != null)
				return false;
		} else if (!opSchedHrsDay.equals(other.opSchedHrsDay))
			return false;
		if (opSchedHrsYr == null) {
			if (other.opSchedHrsYr != null)
				return false;
		} else if (!opSchedHrsYr.equals(other.opSchedHrsYr))
			return false;
		if (opSchedTradeSecret != other.opSchedTradeSecret)
			return false;
		if (opAosAutherized != other.opAosAutherized)
			return false;
		if (opSchedTradeSecretReason == null) {
			if (other.opSchedTradeSecretReason != null)
				return false;
		} else if (!opSchedTradeSecretReason
				.equals(other.opSchedTradeSecretReason))
			return false;
		if (operationLimits != other.operationLimits)
			return false;
		if (operationLimitsDsc == null) {
			if (other.operationLimitsDsc != null)
				return false;
		} else if (!operationLimitsDsc.equals(other.operationLimitsDsc))
			return false;
		if (tvEuOperatingScenarioId == null) {
			if (other.tvEuOperatingScenarioId != null)
				return false;
		} else if (!tvEuOperatingScenarioId
				.equals(other.tvEuOperatingScenarioId))
			return false;
		return true;
	}

	public final List<TVApplicationEUEmissions> getCapEmissions() {
		if (capEmissions == null) {
			capEmissions = new ArrayList<TVApplicationEUEmissions>();
		}
		return capEmissions;
	}

	public final void setCapEmissions(
			List<TVApplicationEUEmissions> capEmissions) {
		this.capEmissions = new ArrayList<TVApplicationEUEmissions>();
		if (capEmissions != null) {
			this.capEmissions.addAll(capEmissions);
		}
	}

	public final List<TVApplicationEUEmissions> getHapEmissions() {
		if (hapEmissions == null) {
			hapEmissions = new ArrayList<TVApplicationEUEmissions>();
			// calculateEUPollutantTotal();
		}
		return hapEmissions;
	}

	public final void setHapEmissions(
			List<TVApplicationEUEmissions> hapEmissions) {
		this.hapEmissions = new ArrayList<TVApplicationEUEmissions>();
		if (hapEmissions != null) {
			this.hapEmissions.addAll(hapEmissions);
		}
	}

	public final List<TVApplicationEUEmissions> getOthEmissions() {
		if (othEmissions == null) {
			othEmissions = new ArrayList<TVApplicationEUEmissions>();
		}
		return othEmissions;
	}

	public final void setOthEmissions(
			List<TVApplicationEUEmissions> othEmissions) {
		this.othEmissions = new ArrayList<TVApplicationEUEmissions>();
		if (othEmissions != null) {
			this.othEmissions.addAll(othEmissions);
		}
	}

	public final List<TVApplicationEUEmissions> getGhgEmissions() {
		if (ghgEmissions == null) {
			ghgEmissions = new ArrayList<TVApplicationEUEmissions>();
		}
		return ghgEmissions;
	}

	public final void setGhgEmissions(
			List<TVApplicationEUEmissions> ghgEmissions) {
		this.ghgEmissions = new ArrayList<TVApplicationEUEmissions>();
		if (ghgEmissions != null) {
			this.ghgEmissions.addAll(ghgEmissions);
		}
	}

	public final String getTvEuOperatingScenarioNm() {
		String name = tvEuOperatingScenarioNm;
		if (name == null && this.tvEuOperatingScenarioId != null) {
			name = "Alternate Scenario "
					+ this.tvEuOperatingScenarioId.toString();
		}
		return name;
	}

	public final void setTvEuOperatingScenarioNm(String tvEuOperatingScenarioNm) {
		this.tvEuOperatingScenarioNm = tvEuOperatingScenarioNm;
	}

	public final List<TVAltScenarioPteReq> getPteRequirements() {
		if (pteRequirements == null) {
			pteRequirements = new ArrayList<TVAltScenarioPteReq>();
		}
		return pteRequirements;
	}

	public final void setPteRequirements(
			List<TVAltScenarioPteReq> pteRequirements) {
		this.pteRequirements = new ArrayList<TVAltScenarioPteReq>();
		if (pteRequirements != null) {
			this.pteRequirements.addAll(pteRequirements);
		}
	}

	public Timestamp getEngOrderDate() {
		return engOrderDate;
	}

	public void setEngOrderDate(Timestamp engOrderDate) {
		this.engOrderDate = engOrderDate;
	}

	public Timestamp getEngManufactureDate() {
		return engManufactureDate;
	}

	public void setEngManufactureDate(Timestamp engManufactureDate) {
		this.engManufactureDate = engManufactureDate;
	}

	public Long getEngOrderDateLong() {
		if (engOrderDate == null) {
			return null;
		}
		return engOrderDate.getTime();
	}

	public void setEngOrderDateLong(Long engOrderDateLong) {
		if (engOrderDateLong != null) {
			engOrderDate = new Timestamp(engOrderDateLong);
		}
	}

	public Long getEngManufactureDateLong() {
		if (engManufactureDate == null) {
			return null;
		}
		return engManufactureDate.getTime();
	}

	public void setEngManufactureDateLong(Long engManufactureDateLong) {
		if (engManufactureDateLong != null) {
			engManufactureDate = new Timestamp(engManufactureDateLong);
		}
	}

	public boolean isOpAosAutherizedAllowed() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(opAosAutherized)
				&& AbstractDAO.translateIndicatorToBoolean(opAosAutherized);
		return ret;
	}

	public String getOpAosAutherized() {
		return opAosAutherized;
	}

	public void setOpAosAutherized(String opAosAutherized) {
		this.opAosAutherized = opAosAutherized;
	}

	private void calculateHAPsEUPollutantTotal() {
		hapsTotal = new BigDecimal("0");
		for (TVApplicationEUEmissions hapEmission : hapEmissions) {
				hapsTotal = hapsTotal
			          .add(new BigDecimal(hapEmission.getPteTonsYr()));

		}
	}

	private void calculateGHGsEUPollutantTotal() {
		ghgsTotal = new BigDecimal("0");
		for (TVApplicationEUEmissions ghgEmission : ghgEmissions) {
				ghgsTotal = ghgsTotal
			          .add(new BigDecimal(ghgEmission.getPteTonsYr()));
		}
	}

}
