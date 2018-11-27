package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.framework.util.Utility;

public class TVApplicationEU extends ApplicationEU {
	private String monitorReq;
	private String monitorReqDsc;
	private String complyWEnhMonitor;
	private String tvIeuReasonCd;
	private String applicableReqs;
	private TVEUOperatingScenario normalOperatingScenario;
	private List<TVApplicableReq> applicableRequirements;
	private List<TVApplicableReq> stateOnlyRequirements;
	private List<TVEUOperatingScenario> alternateOperatingScenarios;

	private String nspsApplicableFlag;
	private List<String> nspsSubpartCodes;
	private String neshapApplicableFlag;
	private List<String> neshapSubpartCodes;
	private String mactApplicableFlag;
	private List<String> mactSubpartCodes;
	private String federalRuleApplicabilityExplanation;

	private List<TVEUPollutantLimit> pollutantLimits;
	private List<TVEUOperationalRestriction> operationalRestrictions;

	public TVApplicationEU() {
		super();
	}

	public TVApplicationEU(TVApplicationEU eu) {
		super(eu);
		this.monitorReq = eu.monitorReq;
		this.monitorReqDsc = eu.monitorReqDsc;
		this.complyWEnhMonitor = eu.complyWEnhMonitor;
		this.tvIeuReasonCd = eu.tvIeuReasonCd;
		this.applicableReqs = eu.applicableReqs;
		this.normalOperatingScenario = eu.normalOperatingScenario;
		this.applicableRequirements = eu.applicableRequirements;
		this.stateOnlyRequirements = eu.stateOnlyRequirements;
		this.alternateOperatingScenarios = eu.alternateOperatingScenarios;

		setNspsApplicableFlag(eu.getNspsApplicableFlag());
		setNspsSubpartCodes(eu.getNspsSubpartCodes());
		setNeshapApplicableFlag(eu.getNeshapApplicableFlag());
		setNeshapSubpartCodes(eu.getNeshapSubpartCodes());
		setMactApplicableFlag(eu.getMactApplicableFlag());
		setMactSubpartCodes(eu.getMactSubpartCodes());
		setFederalRuleApplicabilityExplanation(eu
				.getFederalRuleApplicabilityExplanation());
		
		setPollutantLimits(eu.getPollutantLimits());
		setOperationalRestrictions(eu.getOperationalRestrictions());
	}

	/**
	 * Copy a PTIO EU.
	 * 
	 * @param ptioEU
	 */
	public TVApplicationEU(PTIOApplicationEU ptioEU) {
		// most of the real copy code is in ApplicationBO
		setFpEU(ptioEU.getFpEU());
	}

	@Override
	public void populate(ResultSet rs) {
		super.populate(rs);
		try {
			setMonitorReq(rs.getString("monitor_req"));
			setMonitorReqDsc(rs.getString("monitor_req_dsc"));
			setComplyWEnhMonitor(rs.getString("comply_w_enh_monitor"));
			setTvIeuReasonCd(rs.getString("tv_ieu_reason_cd"));
			setApplicableReqs(rs.getString("applicable_reqs"));

			setNspsApplicableFlag(rs.getString("tv_nsps_applicable_flag"));
			setNeshapApplicableFlag(rs.getString("tv_neshaps_applicable_flag"));
			setMactApplicableFlag(rs.getString("tv_mact_applicable_flag"));
			setFederalRuleApplicabilityExplanation(rs
					.getString("tv_federal_rule_appl_explanation"));
		} catch (SQLException e) {
			logger.error("Required field error");
		}
	}

	public final String getApplicableReqs() {
		return applicableReqs;
	}

	public final void setApplicableReqs(String applicableReqs) {
		this.applicableReqs = applicableReqs;
	}

	public final boolean isComplyWEnhMonitoring() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(complyWEnhMonitor) &&
				AbstractDAO.translateIndicatorToBoolean(complyWEnhMonitor);
		return ret;
	}

	public String getComplyWEnhMonitor() {
		return complyWEnhMonitor;
	}
	
	public final void setComplyWEnhMonitor(String complyWEnhMonitor) {
		this.complyWEnhMonitor = complyWEnhMonitor;
	}

	public final boolean isMonitorRequired() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(monitorReq) &&
				AbstractDAO.translateIndicatorToBoolean(monitorReq);
		return ret;
	}
	
	public String getMonitorReq() {
		return monitorReq;
	}

	public final void setMonitorReq(String monitorReq) {
		this.monitorReq = monitorReq;
	}

	public final String getMonitorReqDsc() {
		return monitorReqDsc;
	}

	public final void setMonitorReqDsc(String monitorReqDsc) {
		this.monitorReqDsc = monitorReqDsc;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((applicableReqs == null) ? 0 : applicableReqs.hashCode());
		result = PRIME * result
				+ ((tvIeuReasonCd == null) ? 0 : tvIeuReasonCd.hashCode());
		result = PRIME * result + (AbstractDAO.translateIndicatorToBoolean(complyWEnhMonitor) ? 1231 : 1237);
		result = PRIME * result + (AbstractDAO.translateIndicatorToBoolean(monitorReq) ? 1231 : 1237);
		result = PRIME * result
				+ ((monitorReqDsc == null) ? 0 : monitorReqDsc.hashCode());
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
		final TVApplicationEU other = (TVApplicationEU) obj;
		if (applicableReqs == null) {
			if (other.applicableReqs != null)
				return false;
		} else if (!applicableReqs.equals(other.applicableReqs))
			return false;
		if (tvIeuReasonCd == null) {
			if (other.tvIeuReasonCd != null)
				return false;
		} else if (!tvIeuReasonCd.equals(other.tvIeuReasonCd))
			return false;
		if (complyWEnhMonitor != other.complyWEnhMonitor)
			return false;
		if (mactApplicableFlag == null) {
			if (other.mactApplicableFlag != null)
				return false;
		} else if (!mactApplicableFlag.equals(other.mactApplicableFlag))
			return false;
		if (neshapApplicableFlag == null) {
			if (other.neshapApplicableFlag != null)
				return false;
		} else if (!neshapApplicableFlag.equals(other.neshapApplicableFlag))
			return false;
		if (nspsApplicableFlag == null) {
			if (other.nspsApplicableFlag != null)
				return false;
		} else if (!nspsApplicableFlag.equals(other.nspsApplicableFlag))
			return false;
		if (monitorReq != other.monitorReq)
			return false;
		if (monitorReqDsc == null) {
			if (other.monitorReqDsc != null)
				return false;
		} else if (!monitorReqDsc.equals(other.monitorReqDsc))
			return false;

		return true;
	}

	public final TVEUOperatingScenario getNormalOperatingScenario() {
		return normalOperatingScenario;
	}

	public final void setNormalOperatingScenario(
			TVEUOperatingScenario normalOperatingScenario) {
		this.normalOperatingScenario = normalOperatingScenario;
	}

	public final List<TVEUOperatingScenario> getAlternateOperatingScenarios() {
		if (alternateOperatingScenarios == null) {
			alternateOperatingScenarios = new ArrayList<TVEUOperatingScenario>();
		}
		return alternateOperatingScenarios;
	}

	public final void setAlternateOperatingScenarios(
			List<TVEUOperatingScenario> alternateOperatingScenarios) {
		this.alternateOperatingScenarios = new ArrayList<TVEUOperatingScenario>();
		if (alternateOperatingScenarios != null) {
			this.alternateOperatingScenarios
					.addAll(alternateOperatingScenarios);
		}
	}

	public final List<TVApplicableReq> getApplicableRequirements() {
		if (applicableRequirements == null) {
			applicableRequirements = new ArrayList<TVApplicableReq>();
		}
		return applicableRequirements;
	}

	public final void setApplicableRequirements(
			List<TVApplicableReq> applicableRequirements) {
		this.applicableRequirements = new ArrayList<TVApplicableReq>();
		if (applicableRequirements != null) {
			this.applicableRequirements.addAll(applicableRequirements);
		}
	}

	public final List<TVApplicableReq> getStateOnlyRequirements() {
		if (stateOnlyRequirements == null) {
			stateOnlyRequirements = new ArrayList<TVApplicableReq>();
		}
		return stateOnlyRequirements;
	}

	public final void setStateOnlyRequirements(
			List<TVApplicableReq> stateOnlyRequirements) {
		this.stateOnlyRequirements = new ArrayList<TVApplicableReq>();
		if (stateOnlyRequirements != null) {
			this.stateOnlyRequirements.addAll(stateOnlyRequirements);
		}
	}

	public final String getTvIeuReasonCd() {
		return tvIeuReasonCd;
	}

	public final void setTvIeuReasonCd(String tvIeuReasonCd) {
		this.tvIeuReasonCd = tvIeuReasonCd;
	}

	public final String getNspsApplicableFlag() {
		return nspsApplicableFlag;
	}

	public final void setNspsApplicableFlag(String nspsApplicableFlag) {
		this.nspsApplicableFlag = nspsApplicableFlag;
	}

	public final String getNeshapApplicableFlag() {
		return neshapApplicableFlag;
	}

	public final void setNeshapApplicableFlag(String neshapApplicableFlag) {
		this.neshapApplicableFlag = neshapApplicableFlag;
	}

	public final String getMactApplicableFlag() {
		return mactApplicableFlag;
	}

	public final void setMactApplicableFlag(String mactApplicableFlag) {
		this.mactApplicableFlag = mactApplicableFlag;
	}

	public final List<String> getNspsSubpartCodes() {
		if (nspsSubpartCodes == null) {
			nspsSubpartCodes = new ArrayList<String>();
		}
		return nspsSubpartCodes;
	}

	public final void setNspsSubpartCodes(List<String> nspsSubpartCodes) {
		this.nspsSubpartCodes = new ArrayList<String>();
		if (nspsSubpartCodes != null) {
			this.nspsSubpartCodes.addAll(nspsSubpartCodes);
		}
	}

	public final List<String> getMactSubpartCodes() {
		if (mactSubpartCodes == null) {
			mactSubpartCodes = new ArrayList<String>();
		}
		return mactSubpartCodes;
	}

	public final void setMactSubpartCodes(List<String> mactSubpartCodes) {
		this.mactSubpartCodes = new ArrayList<String>();
		if (mactSubpartCodes != null) {
			this.mactSubpartCodes.addAll(mactSubpartCodes);
		}
	}

	public final List<String> getNeshapSubpartCodes() {
		if (neshapSubpartCodes == null) {
			neshapSubpartCodes = new ArrayList<String>();
		}
		return neshapSubpartCodes;
	}

	public final void setNeshapSubpartCodes(List<String> neshapSubpartCodes) {
		this.neshapSubpartCodes = new ArrayList<String>();
		if (neshapSubpartCodes != null) {
			this.neshapSubpartCodes.addAll(neshapSubpartCodes);
		}
	}

	public final boolean getFedRulesExemption() {
		boolean exempt = false;
		exempt = (getNspsApplicableFlag() != null && (getNspsApplicableFlag()
				.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)))
				|| (getNeshapApplicableFlag() != null && getNeshapApplicableFlag()
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT))
				|| (getMactApplicableFlag() != null && getMactApplicableFlag()
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		return exempt;
	}

	public final String getFederalRuleApplicabilityExplanation() {
		if (null != federalRuleApplicabilityExplanation && 
				!getFedRulesExemption()) {
			federalRuleApplicabilityExplanation = null;
		}
		return federalRuleApplicabilityExplanation;
	}

	public final void setFederalRuleApplicabilityExplanation(
			String federalRuleApplicabilityExplanation) {
		this.federalRuleApplicabilityExplanation = federalRuleApplicabilityExplanation;
	}

	public List<TVEUPollutantLimit> getPollutantLimits() {
		if (pollutantLimits == null) {
			pollutantLimits = new ArrayList<TVEUPollutantLimit>();
		}

		return pollutantLimits;
	}

	public void setPollutantLimits(List<TVEUPollutantLimit> pollutantLimits) {
		this.pollutantLimits = new ArrayList<TVEUPollutantLimit>();
		if (pollutantLimits != null) {
			this.pollutantLimits.addAll(pollutantLimits);
		}
	}

	public List<TVEUOperationalRestriction> getOperationalRestrictions() {
		if (operationalRestrictions == null) {
			operationalRestrictions = new ArrayList<TVEUOperationalRestriction>();
		}

		return operationalRestrictions;
	}

	public void setOperationalRestrictions(
			List<TVEUOperationalRestriction> operationalRestrictions) {
		this.operationalRestrictions = new ArrayList<TVEUOperationalRestriction>();
		if (operationalRestrictions != null) {
			this.operationalRestrictions.addAll(operationalRestrictions);
		}
	}

}
