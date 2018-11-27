package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class TVEUPollutantLimit extends BaseDB {
	public static String CONTROLLED = "controlled";
	public static String NOT_CONTROLLED = "not-controlled";
	private Integer applicationEuId;
	private String reqBasisCd;
	private String ruleCite;
	private String numLimitUnit;
	private String pollutantCd;
	private String avgPeriod;
	private String complianceFlag;
	private Double potentialEmissions;
	private String determinationBasisCd;
	private String camFlag;
	private String complianceMethod;

	private boolean controlled;

	public TVEUPollutantLimit() {
		super();
		requiredFields();
	}

	public TVEUPollutantLimit(TVEUPollutantLimit old) {
		super(old);
		this.applicationEuId = old.applicationEuId;
		this.reqBasisCd = old.reqBasisCd;
		this.ruleCite = old.ruleCite;
		this.numLimitUnit = old.numLimitUnit;
		this.pollutantCd = old.pollutantCd;
		this.avgPeriod = old.avgPeriod;
		this.complianceFlag = old.complianceFlag;
		this.potentialEmissions = old.potentialEmissions;
		this.determinationBasisCd = old.determinationBasisCd;
		this.camFlag = old.camFlag;
		this.complianceMethod = old.complianceMethod;
		this.controlled = old.controlled;
	}

	public void populate(ResultSet rs) throws SQLException {
		try {
			setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
			setReqBasisCd(rs.getString("req_basis_cd"));
			setRuleCite(rs.getString("rule_cite"));
			setNumLimitUnit(rs.getString("num_limit_unit"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setAvgPeriod(rs.getString("avg_period"));
			setComplianceFlag(rs.getString("compliance_flag"));
			setPotentialEmissions(rs.getDouble("potential_emissions"));
			setDeterminationBasisCd(rs.getString("determination_basis_cd"));
			setCamFlag(rs.getString("cam_flag"));
			setComplianceMethod(rs.getString("compliance_method"));
			setControlled(AbstractDAO.translateIndicatorToBoolean(rs.getString("controlled")));
		} catch (SQLException e) {
			logger.error("Required field error");
		}
	}

	public final Integer getApplicationEuId() {
		return applicationEuId;
	}

	public final void setApplicationEuId(Integer applicationEuId) {
		this.applicationEuId = applicationEuId;
	}

	public String getReqBasisCd() {
		return reqBasisCd;
	}

	public void setReqBasisCd(String reqBasisCd) {
		this.reqBasisCd = reqBasisCd;
	}

	public String getRuleCite() {
		return ruleCite;
	}

	public void setRuleCite(String ruleCite) {
		this.ruleCite = ruleCite;
	}

	public String getNumLimitUnit() {
		return numLimitUnit;
	}

	public void setNumLimitUnit(String numLimitUnit) {
		this.numLimitUnit = numLimitUnit;
	}

	public final String getPollutantCd() {
		return pollutantCd;
	}

	public final void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public String getAvgPeriod() {
		return avgPeriod;
	}

	public void setAvgPeriod(String avgPeriod) {
		this.avgPeriod = avgPeriod;
	}

	public String getComplianceFlag() {
		return complianceFlag;
	}

	public void setComplianceFlag(String complianceFlag) {
		this.complianceFlag = complianceFlag;
	}

	public Double getPotentialEmissions() {
		return potentialEmissions;
	}

	public void setPotentialEmissions(Double potentialEmissions) {
		this.potentialEmissions = potentialEmissions;
	}

	public String getDeterminationBasisCd() {
		return determinationBasisCd;
	}

	public void setDeterminationBasisCd(String determinationBasisCd) {
		this.determinationBasisCd = determinationBasisCd;
	}

	public String getCamFlag() {
		return camFlag;
	}

	public void setCamFlag(String camFlag) {
		// reset complianceMethod if camFlag is changed from no to yes
		if(!isCamCompliant() && AbstractDAO.translateIndicatorToBoolean(camFlag)) {
			setComplianceMethod(null);
		}
		this.camFlag = camFlag;
	}

	public String getComplianceMethod() {
		return complianceMethod;
	}

	public void setComplianceMethod(String complianceMethod) {
		this.complianceMethod = complianceMethod;
	}

	public boolean isCompliant() {
		boolean ret = AbstractDAO.translateIndicatorToBoolean(complianceFlag);
		return ret;
	}

	public boolean isCamCompliant() {
		boolean ret = AbstractDAO.translateIndicatorToBoolean(camFlag);
		return ret;
	}

	public boolean isControlled() {
		return controlled;
	}

	public void setControlled(boolean controlled) {
		this.controlled = controlled;

		if (!this.controlled) {
			setPotentialEmissions(null);
			setDeterminationBasisCd(null);

			if (this.camFlag != null && !isCamCompliant()) {
				setComplianceMethod(null);
			}

			setCamFlag(null);
		}
	}

	public String getPollutantStatus() {
		String ret = NOT_CONTROLLED;

		if (this.controlled) {
			ret = CONTROLLED;
		}

		return ret;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((applicationEuId == null) ? 0 : applicationEuId.hashCode());
		result = PRIME * result
				+ ((reqBasisCd == null) ? 0 : reqBasisCd.hashCode());
		result = PRIME * result
				+ ((ruleCite == null) ? 0 : ruleCite.hashCode());
		result = PRIME * result
				+ ((numLimitUnit == null) ? 0 : numLimitUnit.hashCode());
		result = PRIME * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = PRIME * result
				+ ((avgPeriod == null) ? 0 : avgPeriod.hashCode());
		result = PRIME * result
				+ ((complianceFlag == null) ? 0 : complianceFlag.hashCode());
		result = PRIME
				* result
				+ ((potentialEmissions == null) ? 0 : potentialEmissions
						.hashCode());
		result = PRIME
				* result
				+ ((determinationBasisCd == null) ? 0 : determinationBasisCd
						.hashCode());
		result = PRIME * result + ((camFlag == null) ? 0 : camFlag.hashCode());
		result = PRIME
				* result
				+ ((complianceMethod == null) ? 0 : complianceMethod.hashCode());
		result = PRIME * result + (controlled ? 1231 : 1237);
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
		final TVEUPollutantLimit other = (TVEUPollutantLimit) obj;
		if (applicationEuId == null) {
			if (other.applicationEuId != null)
				return false;
		} else if (!applicationEuId.equals(other.applicationEuId))
			return false;
		if (reqBasisCd == null) {
			if (other.reqBasisCd != null)
				return false;
		} else if (!reqBasisCd.equals(other.reqBasisCd))
			return false;
		if (ruleCite == null) {
			if (other.ruleCite != null)
				return false;
		} else if (!ruleCite.equals(other.ruleCite))
			return false;
		if (numLimitUnit == null) {
			if (other.numLimitUnit != null)
				return false;
		} else if (!numLimitUnit.equals(other.numLimitUnit))
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		if (avgPeriod == null) {
			if (other.avgPeriod != null)
				return false;
		} else if (!avgPeriod.equals(other.avgPeriod))
			return false;
		if (complianceFlag == null) {
			if (other.complianceFlag != null)
				return false;
		} else if (!complianceFlag.equals(other.complianceFlag))
			return false;
		if (potentialEmissions == null) {
			if (other.potentialEmissions != null)
				return false;
		} else if (!potentialEmissions.equals(other.potentialEmissions))
			return false;
		if (determinationBasisCd == null) {
			if (other.determinationBasisCd != null)
				return false;
		} else if (!determinationBasisCd.equals(other.determinationBasisCd))
			return false;
		if (camFlag == null) {
			if (other.camFlag != null)
				return false;
		} else if (!camFlag.equals(other.camFlag))
			return false;
		if (complianceMethod == null) {
			if (other.complianceMethod != null)
				return false;
		} else if (!complianceMethod.equals(other.complianceMethod))
			return false;
		if(controlled != other.controlled)
			return false;
		return true;
	}

	public void requiredFields() {
		requiredField(this.pollutantCd, "pollutantChoice", "Pollutant",
				"pollutantChoice");

		requiredField(this.reqBasisCd, "reqBasis", "Requirement Basis",
				"reqBasis");

		requiredField(this.ruleCite, "ruleCite",
				"Permit/Waiver/State Rule/Federal Standard Cite", "ruleCite");

		requiredField(this.numLimitUnit, "numLimitUnit",
				"Numeric Limit and Unit", "numLimitUnit");

		requiredField(this.avgPeriod, "avgPeriod", "Averaging Period",
				"avgPeriod");

		requiredField(this.complianceFlag, "complianceBox", "In Compliance?",
				"complianceBox");

		if (this.controlled) {
			requiredField(this.potentialEmissions, "potentialEmissions",
					"Pre-Controlled Potential Emissions (tons/yr)",
					"potentialEmissions");

			requiredField(this.determinationBasisCd, "determinationMethod",
					"Method for Determination", "determinationMethod");

			requiredField(
					this.camFlag,
					"camFlag",
					"Is this Control Device Subject to Compliance Assurance Monitoring (CAM)",
					"camFlag");

			if (!isCamCompliant()) {
				requiredField(this.complianceMethod, "complianceMethod",
						"Method to Determine Compliance", "complianceMethod");
			}
		} else {
			requiredField(this.complianceMethod, "complianceMethod",
					"Method to Determine Compliance", "complianceMethod");
		}
	}

	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();

		if (this.potentialEmissions != null) {
			checkRangeValues(this.potentialEmissions, new Double(.01),
					new Double(2000000000), "potentialEmissions",
					"Pre-Controlled Potential Emissions (tons/yr)");
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
}
