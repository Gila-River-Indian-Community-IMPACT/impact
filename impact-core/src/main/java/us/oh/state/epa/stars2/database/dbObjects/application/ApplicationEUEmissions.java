package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.PollutantDef;

@SuppressWarnings("serial")
public class ApplicationEUEmissions extends BaseDB {
	private int applicationEuId;
	private String euEmissionTableCd;
	private String pollutantCd;
	
	private String co2Equivalent;
	private String preCtlPotentialEmissions;
	private String potentialToEmit;
	private String unitCd;
	private String potentialToEmitLbHr;
	private String potentialToEmitTonYr;
	private String pteDeterminationBasisCd;

	public ApplicationEUEmissions() {
		super();

		setCo2Equivalent("0");
		setPreCtlPotentialEmissions("0");
		setPotentialToEmit("0");
		setUnitCd(null);
		setPotentialToEmitLbHr("0");
		setPotentialToEmitTonYr("0");
		setPteDeterminationBasisCd(null);

	}

	public ApplicationEUEmissions(Integer applicationEUID, String pollutantCd,
			String euEmissionTableCd) {
		this.applicationEuId = applicationEUID;
		this.pollutantCd = pollutantCd;
		this.euEmissionTableCd = euEmissionTableCd;
		
		setCo2Equivalent("0");
		setPreCtlPotentialEmissions("0");
		setPotentialToEmit("0");
		setUnitCd(null);
		setPotentialToEmitLbHr("0");
		setPotentialToEmitTonYr("0");
		setPteDeterminationBasisCd(null);

	}

	public ApplicationEUEmissions(ApplicationEUEmissions old) {
		super(old);
		setApplicationEuId(old.getApplicationEuId());
		setEuEmissionTableCd(old.getEuEmissionTableCd());
		setPollutantCd(old.getPollutantCd());

		setCo2Equivalent(old.getCo2Equivalent());
		setPreCtlPotentialEmissions(old.getPreCtlPotentialEmissions());
		setPotentialToEmit(old.getPotentialToEmit());
		setUnitCd(old.getUnitCd());
		setPotentialToEmitLbHr(old.getPotentialToEmitLbHr());
		setPotentialToEmitTonYr(old.getPotentialToEmitTonYr());
		setPteDeterminationBasisCd(old.getPteDeterminationBasisCd());
	}

	public void populate(ResultSet rs) throws SQLException {
		setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
		setPollutantCd(rs.getString("pollutant_cd"));
		setEuEmissionTableCd(rs.getString("eu_emission_table_cd"));
		
		setPreCtlPotentialEmissions(rs
				.getString("pre_ctl_potential_emission_ton_yr"));
		setPotentialToEmit(rs.getString("potential_to_emit"));
		setUnitCd(rs.getString("unit_cd"));
		setPotentialToEmitLbHr(rs.getString("potential_to_emit_lb_hr"));
		setPotentialToEmitTonYr(rs.getString("potential_to_emit_ton_yr"));
		setPteDeterminationBasisCd(rs.getString("pte_determination_basis_cd"));

		try {
			setCo2Equivalent(rs.getString("co2_equivalent"));
		} catch (SQLException e) {
			logger.warn("co2_equivalent field is missing");
		}
		setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
	}

	public final int getApplicationEuId() {
		return applicationEuId;
	}

	public final void setApplicationEuId(int applicationEUID) {
		this.applicationEuId = applicationEUID;
	}

	public final String getPollutantCd() {
		return pollutantCd;
	}

	public final void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}
	
	public final String getCo2Equivalent() {
		return co2Equivalent;
	}

	public final void setCo2Equivalent(String co2Equivalent) {
		// don't allow nulls
		if (co2Equivalent == null) {
			co2Equivalent = "0";
		}
		this.co2Equivalent = co2Equivalent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + applicationEuId;
		result = prime * result
				+ ((co2Equivalent == null) ? 0 : co2Equivalent.hashCode());
		result = prime
				* result
				+ ((euEmissionTableCd == null) ? 0 : euEmissionTableCd
						.hashCode());
		result = prime * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = prime * result
				+ ((potentialToEmit == null) ? 0 : potentialToEmit.hashCode());
		result = prime
				* result
				+ ((potentialToEmitLbHr == null) ? 0 : potentialToEmitLbHr
						.hashCode());
		result = prime
				* result
				+ ((potentialToEmitTonYr == null) ? 0 : potentialToEmitTonYr
						.hashCode());
		result = prime
				* result
				+ ((preCtlPotentialEmissions == null) ? 0
						: preCtlPotentialEmissions.hashCode());
		result = prime
				* result
				+ ((pteDeterminationBasisCd == null) ? 0
						: pteDeterminationBasisCd.hashCode());
		result = prime * result + ((unitCd == null) ? 0 : unitCd.hashCode());
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
		if (!(obj instanceof ApplicationEUEmissions)) {
			return false;
		}
		ApplicationEUEmissions other = (ApplicationEUEmissions) obj;
		if (applicationEuId != other.applicationEuId) {
			return false;
		}
		if (co2Equivalent == null) {
			if (other.co2Equivalent != null) {
				return false;
			}
		} else if (!co2Equivalent.equals(other.co2Equivalent)) {
			return false;
		}
		if (euEmissionTableCd == null) {
			if (other.euEmissionTableCd != null) {
				return false;
			}
		} else if (!euEmissionTableCd.equals(other.euEmissionTableCd)) {
			return false;
		}
		if (pollutantCd == null) {
			if (other.pollutantCd != null) {
				return false;
			}
		} else if (!pollutantCd.equals(other.pollutantCd)) {
			return false;
		}
		if (potentialToEmit == null) {
			if (other.potentialToEmit != null) {
				return false;
			}
		} else if (!potentialToEmit.equals(other.potentialToEmit)) {
			return false;
		}
		if (potentialToEmitLbHr == null) {
			if (other.potentialToEmitLbHr != null) {
				return false;
			}
		} else if (!potentialToEmitLbHr.equals(other.potentialToEmitLbHr)) {
			return false;
		}
		if (potentialToEmitTonYr == null) {
			if (other.potentialToEmitTonYr != null) {
				return false;
			}
		} else if (!potentialToEmitTonYr.equals(other.potentialToEmitTonYr)) {
			return false;
		}
		if (preCtlPotentialEmissions == null) {
			if (other.preCtlPotentialEmissions != null) {
				return false;
			}
		} else if (!preCtlPotentialEmissions
				.equals(other.preCtlPotentialEmissions)) {
			return false;
		}
		if (pteDeterminationBasisCd == null) {
			if (other.pteDeterminationBasisCd != null) {
				return false;
			}
		} else if (!pteDeterminationBasisCd
				.equals(other.pteDeterminationBasisCd)) {
			return false;
		}
		if (unitCd == null) {
			if (other.unitCd != null) {
				return false;
			}
		} else if (!unitCd.equals(other.unitCd)) {
			return false;
		}
		return true;
	}

	@Override
	public final ValidationMessage[] validate() {
		clearValidationMessages();
		requiredField(pollutantCd, "pollutant", "Pollutant");
		requiredField(preCtlPotentialEmissions, "preCtlPotentialEmissions",
				"Pre-Controlled Potential Emissions (tons/yr)");
		requiredField(potentialToEmit, "potentialToEmit",
				"Efficiency Standards Potential to Emit (PTE)");
		requiredField(unitCd, "unitCd",
				"Efficiency Standards Units");
		requiredField(potentialToEmitLbHr, "potentialToEmitLbHr",
				"Potential to Emit (PTE) (lbs/hr)");
		requiredField(potentialToEmitTonYr, "potentialToEmitTonYr",
				"Potential to Emit (PTE) (tons/year)");
		requiredField(pteDeterminationBasisCd, "AppBasisForDeterminationDropdown",
				"Basis for Determination");
		return super.validate();
	}
	
	public String getPollutantCategory() {
		String pollutantCategory = null;
		if (pollutantCd != null) {
			PollutantDef pollutantDef = (PollutantDef) PollutantDef.getData()
					.getItems().getItem(pollutantCd);
			pollutantCategory = pollutantDef.getCategory();
		}
		return pollutantCategory;
	}

	public String getEuEmissionTableCd() {
		return euEmissionTableCd;
	}

	public void setEuEmissionTableCd(String euEmissionTableCd) {
		this.euEmissionTableCd = euEmissionTableCd;
	}

	public boolean isValueSpecified() {
		return (preCtlPotentialEmissions != null && !preCtlPotentialEmissions.equals("0"))
				|| (potentialToEmit != null && !potentialToEmit.equals("0"))
				|| (potentialToEmitLbHr != null && !potentialToEmitLbHr.equals("0"))
				|| (potentialToEmitTonYr != null && !potentialToEmitTonYr.equals("0"));
	}

	public String getPreCtlPotentialEmissions() {
		return preCtlPotentialEmissions;
	}

	public void setPreCtlPotentialEmissions(String preCtlPotentialEmissions) {
		// don't allow nulls
		if (preCtlPotentialEmissions == null) {
			preCtlPotentialEmissions = "0";
		}
		this.preCtlPotentialEmissions = preCtlPotentialEmissions;
	}

	public String getPotentialToEmit() {
		return potentialToEmit;
	}

	public void setPotentialToEmit(String potentialToEmit) {
		// don't allow nulls
		if (potentialToEmit == null) {
			potentialToEmit = "0";
		}
		this.potentialToEmit = potentialToEmit;
	}

	public String getPotentialToEmitLbHr() {
		return potentialToEmitLbHr;
	}

	public void setPotentialToEmitLbHr(String potentialToEmitLbHr) {
		// don't allow nulls
		if (potentialToEmitLbHr == null) {
			potentialToEmitLbHr = "0";
		}
		this.potentialToEmitLbHr = potentialToEmitLbHr;
	}

	public String getPotentialToEmitTonYr() {
		return potentialToEmitTonYr;
	}

	public void setPotentialToEmitTonYr(String potentialToEmitTonYr) {
		// don't allow nulls
		if (potentialToEmitTonYr == null) {
			potentialToEmitTonYr = "0";
		}
		this.potentialToEmitTonYr = potentialToEmitTonYr;
	}

	public String getPteDeterminationBasisCd() {
		return pteDeterminationBasisCd;
	}

	public void setPteDeterminationBasisCd(String pteDeterminationBasisCd) {
		this.pteDeterminationBasisCd = pteDeterminationBasisCd;
	}

	public String getUnitCd() {
		return unitCd;
	}

	public void setUnitCd(String unitCd) {
		this.unitCd = unitCd;
	}

	public String getHapCapPollutant() {
		if (pollutantCd == null)
			return "capPollutant";

		return pollutantCd == "7439921" || pollutantCd == "HAP"
				|| pollutantCd == "HAPs" ? "hapPollutant" : "capPollutant";
	}

}
