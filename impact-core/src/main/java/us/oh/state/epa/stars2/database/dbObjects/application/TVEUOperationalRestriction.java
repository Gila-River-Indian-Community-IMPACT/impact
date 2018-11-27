package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class TVEUOperationalRestriction extends BaseDB {
	private Integer applicationEuId;
	private String reqBasisCd;
	private String ruleCite;
	private String restrictionTypeCd;
	private String restrictionDesc;
	private String complianceFlag;
	private String complianceMethod;

	public TVEUOperationalRestriction() {
		super();
		requiredFields();
	}

	public TVEUOperationalRestriction(TVEUOperationalRestriction old) {
		super(old);
		this.applicationEuId = old.applicationEuId;
		this.reqBasisCd = old.reqBasisCd;
		this.ruleCite = old.ruleCite;
		this.restrictionTypeCd = old.restrictionTypeCd;
		this.restrictionDesc = old.restrictionDesc;
		this.complianceFlag = old.complianceFlag;
		this.complianceMethod = old.complianceMethod;
	}

	public void populate(ResultSet rs) throws SQLException {
		try {
			setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
			setReqBasisCd(rs.getString("req_basis_cd"));
			setRuleCite(rs.getString("rule_cite"));
			setRestrictionTypeCd(rs.getString("rest_type_cd"));
			setRestrictionDesc(rs.getString("restriction_desc"));
			setComplianceFlag(rs.getString("compliance_flag"));
			setComplianceMethod(rs.getString("compliance_method"));
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

	public String getRestrictionTypeCd() {
		return restrictionTypeCd;
	}

	public void setRestrictionTypeCd(String restrictionTypeCd) {
		this.restrictionTypeCd = restrictionTypeCd;
	}

	public String getRestrictionDesc() {
		return restrictionDesc;
	}

	public void setRestrictionDesc(String restrictionDesc) {
		this.restrictionDesc = restrictionDesc;
	}

	public String getComplianceFlag() {
		return complianceFlag;
	}

	public void setComplianceFlag(String complianceFlag) {
		this.complianceFlag = complianceFlag;
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
		result = PRIME
				* result
				+ ((restrictionTypeCd == null) ? 0 : restrictionTypeCd
						.hashCode());
		result = PRIME * result
				+ ((restrictionDesc == null) ? 0 : restrictionDesc.hashCode());
		result = PRIME * result
				+ ((complianceFlag == null) ? 0 : complianceFlag.hashCode());
		result = PRIME
				* result
				+ ((complianceMethod == null) ? 0 : complianceMethod.hashCode());
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
		final TVEUOperationalRestriction other = (TVEUOperationalRestriction) obj;
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
		if (restrictionTypeCd == null) {
			if (other.restrictionTypeCd != null)
				return false;
		} else if (!restrictionTypeCd.equals(other.restrictionTypeCd))
			return false;
		if (restrictionDesc == null) {
			if (other.restrictionDesc != null)
				return false;
		} else if (!restrictionDesc.equals(other.restrictionDesc))
			return false;
		if (complianceFlag == null) {
			if (other.complianceFlag != null)
				return false;
		} else if (!complianceFlag.equals(other.complianceFlag))
			return false;
		if (complianceMethod == null) {
			if (other.complianceMethod != null)
				return false;
		} else if (!complianceMethod.equals(other.complianceMethod))
			return false;
		return true;
	}

	public void requiredFields() {
		requiredField(this.reqBasisCd, "reqBasis", "Requirement Basis",
				"reqBasis");

		requiredField(this.ruleCite, "ruleCite",
				"Permit/Waiver/State Rule/Federal Standard Cite", "ruleCite");

		requiredField(this.restrictionTypeCd, "restrictionType",
				"Restriction Type", "restrictionType");

		requiredField(this.restrictionDesc, "restrictionDesc", "Description of Restriction",
				"restrictionDesc");

		requiredField(this.complianceFlag, "complianceBox", "In Compliance?",
				"complianceBox");
		
		requiredField(this.complianceMethod, "complianceMethod",
				"Method to Determine Compliance", "complianceMethod");
	}

	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
}
