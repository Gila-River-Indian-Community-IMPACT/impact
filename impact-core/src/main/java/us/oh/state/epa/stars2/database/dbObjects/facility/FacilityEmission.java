package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.EuPollutantDef;
import us.wy.state.deq.impact.def.PermittedEmissionsUnitDef;

/**
 * @author
 * 
 */
public class FacilityEmission extends BaseDB {
	
	private static final long serialVersionUID = -5128397785396093484L;

	public static int MAX_SCALE = 6;
	private String pollutantCd;
	@Deprecated private BigDecimal potentialEmissionsVal;
	@Deprecated private BigDecimal allowableEmissionsVal;
	@Deprecated private String potentialEmissionsUnit;
	@Deprecated private String allowableEmissionsUnit;
	private BigDecimal allowableEmissionsLbsHour;
	private BigDecimal allowableEmissionsTonsYear;
	private BigDecimal potentialEmissionsLbsHour;
	private BigDecimal potentialEmissionsTonsYear;

	public FacilityEmission() {
		pollutantCd = "";
	}

	public FacilityEmission(FacilityEmission emission) {
		this.pollutantCd = emission.getPollutantCd();
		this.potentialEmissionsVal = emission.getPotentialEmissionsVal();
		this.allowableEmissionsVal = emission.getAllowableEmissionsVal();
		this.potentialEmissionsUnit = emission.getPotentialEmissionsUnit();
		this.allowableEmissionsUnit = emission.getAllowableEmissionsUnit();
		this.allowableEmissionsLbsHour = emission.allowableEmissionsLbsHour;
		this.allowableEmissionsTonsYear = emission.allowableEmissionsTonsYear;
		this.potentialEmissionsLbsHour = emission.potentialEmissionsLbsHour;
		this.potentialEmissionsTonsYear = emission.potentialEmissionsTonsYear;
	}

	/**
	 * @return
	 */
	public final String getPollutantCd() {
		return pollutantCd;
	}

	/**
	 * @param PollutantCd
	 */
	public final void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public void populate(ResultSet rs) {
		try {
			setPollutantCd(rs.getString("pollutant_cd"));
			setPotentialEmissionsVal(rs
					.getBigDecimal("potential_emissions_val"));
			setAllowableEmissionsVal(rs
					.getBigDecimal("allowable_emissions_val"));
			setPotentialEmissionsUnit(rs.getString("potential_emissions_unit"));
			setAllowableEmissionsUnit(rs.getString("allowable_emissions_unit"));
			
			setAllowableEmissionsLbsHour(rs.getBigDecimal("allowable_emissions_lbs_hour"));
			setAllowableEmissionsTonsYear(rs.getBigDecimal("allowable_emissions_tons_year"));
			setPotentialEmissionsLbsHour(rs.getBigDecimal("potential_emissions_lbs_hour"));
			setPotentialEmissionsTonsYear(rs.getBigDecimal("potential_emissions_tons_year"));
			
			
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}
	}

	public String getCategory() {
		return EuPollutantDef.getCategory(pollutantCd);
	}

	@Deprecated 	public BigDecimal getPotentialEmissionsVal() {
		return potentialEmissionsVal;
	}

	@Deprecated 	public void setPotentialEmissionsVal(BigDecimal potentialEmissionsVal) {
		potentialEmissionsVal = normalizeBigDecimal(potentialEmissionsVal);
		this.potentialEmissionsVal = potentialEmissionsVal;
	}

	private BigDecimal normalizeBigDecimal(BigDecimal potentialEmissionsVal) {
		if (potentialEmissionsVal != null) {
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(MAX_SCALE);
			df.setMinimumFractionDigits(0);
			df.setGroupingUsed(false);
			potentialEmissionsVal = new BigDecimal(df.format(potentialEmissionsVal));
		}
		return potentialEmissionsVal;
	}

	@Deprecated 	public BigDecimal getAllowableEmissionsVal() {
		return allowableEmissionsVal;
	}

	@Deprecated 	public void setAllowableEmissionsVal(BigDecimal allowableEmissionsVal) {
		allowableEmissionsVal = normalizeBigDecimal(allowableEmissionsVal);
		this.allowableEmissionsVal = allowableEmissionsVal;
	}

	@Deprecated 	public String getPotentialEmissionsUnit() {
		return potentialEmissionsUnit;
	}

	@Deprecated 	public void setPotentialEmissionsUnit(String potentialEmissionsUnit) {
		this.potentialEmissionsUnit = potentialEmissionsUnit;
	}

	@Deprecated 	public String getAllowableEmissionsUnit() {
		return allowableEmissionsUnit;
	}

	@Deprecated 	public void setAllowableEmissionsUnit(String allowableEmissionsUnit) {
		this.allowableEmissionsUnit = allowableEmissionsUnit;
	}

	public BigDecimal getUnitFactor(String unitCd) {
		BigDecimal ret = null;

		ret = new BigDecimal(PermittedEmissionsUnitDef.getFactorData()
				.getItems().getItemDesc(unitCd));

		return ret;
	}

	public BigDecimal getAllowableEmissionsLbsHour() {
		return allowableEmissionsLbsHour;
	}

	public void setAllowableEmissionsLbsHour(BigDecimal allowableEmissionsLbsHour) {
		this.allowableEmissionsLbsHour = allowableEmissionsLbsHour;
	}

	public BigDecimal getAllowableEmissionsTonsYear() {
		return allowableEmissionsTonsYear;
	}

	public void setAllowableEmissionsTonsYear(BigDecimal allowableEmissionsTonsYear) {
		this.allowableEmissionsTonsYear = allowableEmissionsTonsYear;
	}

	public BigDecimal getPotentialEmissionsLbsHour() {
		return potentialEmissionsLbsHour;
	}

	public void setPotentialEmissionsLbsHour(BigDecimal potentialEmissionsLbsHour) {
		this.potentialEmissionsLbsHour = potentialEmissionsLbsHour;
	}

	public BigDecimal getPotentialEmissionsTonsYear() {
		return potentialEmissionsTonsYear;
	}

	public void setPotentialEmissionsTonsYear(BigDecimal potentialEmissionsTonsYear) {
		this.potentialEmissionsTonsYear = potentialEmissionsTonsYear;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((allowableEmissionsLbsHour == null) ? 0
						: allowableEmissionsLbsHour.hashCode());
		result = prime
				* result
				+ ((allowableEmissionsTonsYear == null) ? 0
						: allowableEmissionsTonsYear.hashCode());
		result = prime
				* result
				+ ((allowableEmissionsUnit == null) ? 0
						: allowableEmissionsUnit.hashCode());
		result = prime
				* result
				+ ((allowableEmissionsVal == null) ? 0 : allowableEmissionsVal
						.hashCode());
		result = prime * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = prime
				* result
				+ ((potentialEmissionsLbsHour == null) ? 0
						: potentialEmissionsLbsHour.hashCode());
		result = prime
				* result
				+ ((potentialEmissionsTonsYear == null) ? 0
						: potentialEmissionsTonsYear.hashCode());
		result = prime
				* result
				+ ((potentialEmissionsUnit == null) ? 0
						: potentialEmissionsUnit.hashCode());
		result = prime
				* result
				+ ((potentialEmissionsVal == null) ? 0 : potentialEmissionsVal
						.hashCode());
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
		FacilityEmission other = (FacilityEmission) obj;
		if (allowableEmissionsLbsHour == null) {
			if (other.allowableEmissionsLbsHour != null)
				return false;
		} else if (!allowableEmissionsLbsHour
				.equals(other.allowableEmissionsLbsHour))
			return false;
		if (allowableEmissionsTonsYear == null) {
			if (other.allowableEmissionsTonsYear != null)
				return false;
		} else if (!allowableEmissionsTonsYear
				.equals(other.allowableEmissionsTonsYear))
			return false;
		if (allowableEmissionsUnit == null) {
			if (other.allowableEmissionsUnit != null)
				return false;
		} else if (!allowableEmissionsUnit.equals(other.allowableEmissionsUnit))
			return false;
		if (allowableEmissionsVal == null) {
			if (other.allowableEmissionsVal != null)
				return false;
		} else if (!allowableEmissionsVal.equals(other.allowableEmissionsVal))
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		if (potentialEmissionsLbsHour == null) {
			if (other.potentialEmissionsLbsHour != null)
				return false;
		} else if (!potentialEmissionsLbsHour
				.equals(other.potentialEmissionsLbsHour))
			return false;
		if (potentialEmissionsTonsYear == null) {
			if (other.potentialEmissionsTonsYear != null)
				return false;
		} else if (!potentialEmissionsTonsYear
				.equals(other.potentialEmissionsTonsYear))
			return false;
		if (potentialEmissionsUnit == null) {
			if (other.potentialEmissionsUnit != null)
				return false;
		} else if (!potentialEmissionsUnit.equals(other.potentialEmissionsUnit))
			return false;
		if (potentialEmissionsVal == null) {
			if (other.potentialEmissionsVal != null)
				return false;
		} else if (!potentialEmissionsVal.equals(other.potentialEmissionsVal))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FacilityEmission [pollutantCd=" + pollutantCd
				+ ", potentialEmissionsVal=" + potentialEmissionsVal
				+ ", allowableEmissionsVal=" + allowableEmissionsVal
				+ ", potentialEmissionsUnit=" + potentialEmissionsUnit
				+ ", allowableEmissionsUnit=" + allowableEmissionsUnit
				+ ", allowableEmissionsLbsHour=" + allowableEmissionsLbsHour
				+ ", allowableEmissionsTonsYear=" + allowableEmissionsTonsYear
				+ ", potentialEmissionsLbsHour=" + potentialEmissionsLbsHour
				+ ", potentialEmissionsTonsYear=" + potentialEmissionsTonsYear
				+ "]";
	}
	
	
}
