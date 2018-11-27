package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class ModelingExtractResult extends BaseDB {

	private String companyName;
	
	private String facilityName;
	
	private String facilityType;
	
	private Double distanceToFacility;
	
	private Double distanceToReleasePoint;
	
	private String modelingSourceId;
	
	private String aqdSourceId;
	
	private String sourceDesc;
	
	private Integer utmEasting;
	
	private Integer utmNorthing;
	
	private String utmDatum;
	
	private Integer utmZone;
	
	private String releasePointId;
	
	private Double releasePointBaseElevation;
	
	private Double releasePointStackHeight;
	
	private Double releasePointTemp;
	
	private Double releasePointExitVelocity;
	
	private Double releasePointStackDiameter;

	private String pollutantCd;
	
	private BigDecimal pollutantShortTermLimit;
	
	private BigDecimal pollutantLongTermLimit;
	
	private Double releasePointLatitudeDegrees;
	
	private Double releasePointLongitudeDegrees;
	
	private BigDecimal pollutantAllowableEmissionsLbsHour;
	
	private BigDecimal pollutantAllowableEmissionsTonsYear;
	
	private BigDecimal pollutantPotentialEmissionsLbsHour;
	
	private BigDecimal pollutantPotentialEmissionsTonsYear;
	
	private EmissionsLimitType pollutantShortTermEmissionsLimitType;
	
	private EmissionsLimitType pollutantLongTermEmissionsLimitType;
	
	public enum EmissionsLimitType {
		ALLOWABLE ("Allowable"),
		POTENTIAL ("Potential");

		private String label;
		
		EmissionsLimitType(String label) {
			this.label = label;
		}
		public String getLabel() { return label; }
	}

	public ModelingExtractResult(ModelingExtractResult result) {
		super();
		this.companyName = result.companyName;
		this.facilityName = result.facilityName;
		this.facilityType = result.facilityType;
		this.distanceToFacility = result.distanceToFacility;
		this.distanceToReleasePoint = result.distanceToReleasePoint;
		this.modelingSourceId = result.modelingSourceId;
		this.aqdSourceId = result.aqdSourceId;
		this.sourceDesc = result.sourceDesc;
		this.utmEasting = result.utmEasting;
		this.utmNorthing = result.utmNorthing;
		this.utmDatum = result.utmDatum;
		this.utmZone = result.utmZone;
		this.releasePointId = result.releasePointId;
		this.releasePointBaseElevation = result.releasePointBaseElevation;
		this.releasePointStackHeight = result.releasePointStackHeight;
		this.releasePointTemp = result.releasePointTemp;
		this.releasePointExitVelocity = result.releasePointExitVelocity;
		this.releasePointStackDiameter = result.releasePointStackDiameter;
		this.releasePointLatitudeDegrees = result.releasePointLatitudeDegrees;
		this.releasePointLongitudeDegrees = result.releasePointLongitudeDegrees;
		this.pollutantCd = result.pollutantCd;
		this.pollutantShortTermLimit = result.pollutantShortTermLimit;
		this.pollutantLongTermLimit = result.pollutantLongTermLimit;
		this.pollutantAllowableEmissionsLbsHour = result.pollutantAllowableEmissionsLbsHour;
		this.pollutantAllowableEmissionsTonsYear = result.pollutantAllowableEmissionsTonsYear;
		this.pollutantPotentialEmissionsLbsHour = result.pollutantPotentialEmissionsLbsHour;
		this.pollutantPotentialEmissionsTonsYear = result.pollutantPotentialEmissionsTonsYear;
		this.pollutantShortTermEmissionsLimitType = result.pollutantShortTermEmissionsLimitType;
		this.pollutantLongTermEmissionsLimitType = result.pollutantLongTermEmissionsLimitType;
	}
	
	public ModelingExtractResult() {
		super();
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		setAqdSourceId(rs.getString("epa_emu_id"));
		setCompanyName(rs.getString("company_name"));
		setDistanceToFacility(new Double(rs.getInt("f_distance"))/1000);
		setDistanceToReleasePoint(new Double(rs.getInt("rp_distance"))/1000);
		setFacilityName(rs.getString("facility_nm"));
		setFacilityType(rs.getString("facility_type_dsc"));
		setModelingSourceId(rs.getString("emu_id"));

		setPollutantAllowableEmissionsLbsHour(rs.getBigDecimal("allowable_emissions_lbs_hour"));
		setPollutantAllowableEmissionsTonsYear(rs.getBigDecimal("allowable_emissions_tons_year"));
		
		setPollutantCd(rs.getString("pollutant_cd"));
		
		setPollutantPotentialEmissionsLbsHour(rs.getBigDecimal("potential_emissions_lbs_hour"));
		setPollutantPotentialEmissionsTonsYear(rs.getBigDecimal("potential_emissions_tons_year"));

		setReleasePointId(rs.getString("release_point_id"));
		setReleasePointBaseElevation(rs.getDouble("base_elevation"));
		setReleasePointExitVelocity(rs.getDouble("exit_gas_velocity"));
		setReleasePointStackDiameter(rs.getDouble("diameter"));
		setReleasePointStackHeight(rs.getDouble("release_height"));
		setReleasePointTemp(rs.getDouble("exit_gas_temp_avg"));

		setReleasePointLatitudeDegrees(rs.getDouble("rp_lat"));
		setReleasePointLongitudeDegrees(rs.getDouble("rp_lon"));
		setSourceDesc(rs.getString("eu_desc"));
	}

	
	public String getReleasePointId() {
		return releasePointId;
	}

	public void setReleasePointId(String releasePointId) {
		this.releasePointId = releasePointId;
	}

	public EmissionsLimitType getPollutantLongTermEmissionsLimitType() {
		return pollutantLongTermEmissionsLimitType;
	}
	
	public void setPollutantLongTermEmissionsLimitType(
			EmissionsLimitType pollutantLongTermEmissionsLimitType) {
		this.pollutantLongTermEmissionsLimitType = pollutantLongTermEmissionsLimitType;
	}
	
	public Double getDistanceToFacility() {
		return distanceToFacility;
	}

	public void setDistanceToFacility(Double distanceToFacility) {
		this.distanceToFacility = distanceToFacility;
	}

	public Double getDistanceToReleasePoint() {
		return distanceToReleasePoint;
	}

	public void setDistanceToReleasePoint(Double distanceToReleasePoint) {
		this.distanceToReleasePoint = distanceToReleasePoint;
	}

	public Double getReleasePointBaseElevation() {
		return releasePointBaseElevation;
	}

	public void setReleasePointBaseElevation(Double releasePointBaseElevation) {
		this.releasePointBaseElevation = releasePointBaseElevation;
	}

	public Double getReleasePointStackHeight() {
		return releasePointStackHeight;
	}

	public void setReleasePointStackHeight(Double releasePointStackHeight) {
		this.releasePointStackHeight = releasePointStackHeight;
	}

	public Double getReleasePointTemp() {
		return releasePointTemp;
	}

	public void setReleasePointTemp(Double releasePointTemp) {
		this.releasePointTemp = releasePointTemp;
	}

	public Double getReleasePointExitVelocity() {
		return releasePointExitVelocity;
	}

	public void setReleasePointExitVelocity(Double releasePointExitVelocity) {
		this.releasePointExitVelocity = releasePointExitVelocity;
	}

	public Double getReleasePointStackDiameter() {
		return releasePointStackDiameter;
	}

	public void setReleasePointStackDiameter(Double releasePointStackDiameter) {
		this.releasePointStackDiameter = releasePointStackDiameter;
	}

	public BigDecimal getPollutantAllowableEmissionsLbsHour() {
		return pollutantAllowableEmissionsLbsHour;
	}

	public void setPollutantAllowableEmissionsLbsHour(
			BigDecimal pollutantAllowableEmissionsValue) {
		this.pollutantAllowableEmissionsLbsHour = pollutantAllowableEmissionsValue;
	}

	public BigDecimal getPollutantAllowableEmissionsTonsYear() {
		return pollutantAllowableEmissionsTonsYear;
	}

	public void setPollutantAllowableEmissionsTonsYear(
			BigDecimal pollutantAllowableEmissionsUnit) {
		this.pollutantAllowableEmissionsTonsYear = pollutantAllowableEmissionsUnit;
	}

	public BigDecimal getPollutantPotentialEmissionsLbsHour() {
		return pollutantPotentialEmissionsLbsHour;
	}

	public void setPollutantPotentialEmissionsLbsHour(
			BigDecimal pollutantPotentialEmissionsValue) {
		this.pollutantPotentialEmissionsLbsHour = pollutantPotentialEmissionsValue;
	}

	public BigDecimal getPollutantPotentialEmissionsTonsYear() {
		return pollutantPotentialEmissionsTonsYear;
	}

	public void setPollutantPotentialEmissionsTonsYear(
			BigDecimal pollutantPotentialEmissionsUnit) {
		this.pollutantPotentialEmissionsTonsYear = pollutantPotentialEmissionsUnit;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getModelingSourceId() {
		return modelingSourceId;
	}

	public void setModelingSourceId(String modelingSourceId) {
		this.modelingSourceId = modelingSourceId;
	}

	public String getAqdSourceId() {
		return aqdSourceId;
	}

	public void setAqdSourceId(String aqdSourceId) {
		this.aqdSourceId = aqdSourceId;
	}

	public String getSourceDesc() {
		return sourceDesc;
	}

	public void setSourceDesc(String sourceDesc) {
		this.sourceDesc = sourceDesc;
	}

	public Integer getUtmEasting() {
		return utmEasting;
	}

	public void setUtmEasting(Integer utmEasting) {
		this.utmEasting = utmEasting;
	}

	public Integer getUtmNorthing() {
		return utmNorthing;
	}

	public void setUtmNorthing(Integer utmNorthing) {
		this.utmNorthing = utmNorthing;
	}

	public String getUtmDatum() {
		return utmDatum;
	}

	public void setUtmDatum(String utmDatum) {
		this.utmDatum = utmDatum;
	}

	public Integer getUtmZone() {
		return utmZone;
	}

	public void setUtmZone(Integer utmZone) {
		this.utmZone = utmZone;
	}

	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public BigDecimal getPollutantShortTermLimit() {
		return pollutantShortTermLimit;
	}

	public void setPollutantShortTermLimit(
			BigDecimal pollutantShortTermLimit) {
		this.pollutantShortTermLimit = pollutantShortTermLimit;
	}

	public BigDecimal getPollutantLongTermLimit() {
		return pollutantLongTermLimit;
	}

	public void setPollutantLongTermLimit(
			BigDecimal pollutantLongTermLimit) {
		this.pollutantLongTermLimit = pollutantLongTermLimit;
	}

	public Double getReleasePointLatitudeDegrees() {
		return releasePointLatitudeDegrees;
	}

	public void setReleasePointLatitudeDegrees(Double releasePointLatitudeDegrees) {
		this.releasePointLatitudeDegrees = releasePointLatitudeDegrees;
	}

	public Double getReleasePointLongitudeDegrees() {
		return releasePointLongitudeDegrees;
	}

	public void setReleasePointLongitudeDegrees(Double releasePointLongitudeDegrees) {
		this.releasePointLongitudeDegrees = releasePointLongitudeDegrees;
	}

	public EmissionsLimitType getPollutantShortTermEmissionsLimitType() {
		return pollutantShortTermEmissionsLimitType;
	}

	public void setPollutantShortTermEmissionsLimitType(
			EmissionsLimitType pollutantEmissionsLimitType) {
		this.pollutantShortTermEmissionsLimitType = pollutantEmissionsLimitType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((aqdSourceId == null) ? 0 : aqdSourceId.hashCode());
		result = prime * result
				+ ((companyName == null) ? 0 : companyName.hashCode());
		result = prime
				* result
				+ ((distanceToFacility == null) ? 0 : distanceToFacility
						.hashCode());
		result = prime
				* result
				+ ((distanceToReleasePoint == null) ? 0
						: distanceToReleasePoint.hashCode());
		result = prime * result
				+ ((facilityName == null) ? 0 : facilityName.hashCode());
		result = prime * result
				+ ((facilityType == null) ? 0 : facilityType.hashCode());
		result = prime
				* result
				+ ((modelingSourceId == null) ? 0 : modelingSourceId.hashCode());
		result = prime
				* result
				+ ((pollutantAllowableEmissionsLbsHour == null) ? 0
						: pollutantAllowableEmissionsLbsHour.hashCode());
		result = prime
				* result
				+ ((pollutantAllowableEmissionsTonsYear == null) ? 0
						: pollutantAllowableEmissionsTonsYear.hashCode());
		result = prime * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = prime
				* result
				+ ((pollutantLongTermEmissionsLimitType == null) ? 0
						: pollutantLongTermEmissionsLimitType.hashCode());
		result = prime
				* result
				+ ((pollutantLongTermLimit == null) ? 0
						: pollutantLongTermLimit.hashCode());
		result = prime
				* result
				+ ((pollutantPotentialEmissionsLbsHour == null) ? 0
						: pollutantPotentialEmissionsLbsHour.hashCode());
		result = prime
				* result
				+ ((pollutantPotentialEmissionsTonsYear == null) ? 0
						: pollutantPotentialEmissionsTonsYear.hashCode());
		result = prime
				* result
				+ ((pollutantShortTermEmissionsLimitType == null) ? 0
						: pollutantShortTermEmissionsLimitType.hashCode());
		result = prime
				* result
				+ ((pollutantShortTermLimit == null) ? 0
						: pollutantShortTermLimit.hashCode());
		result = prime
				* result
				+ ((releasePointBaseElevation == null) ? 0
						: releasePointBaseElevation.hashCode());
		result = prime
				* result
				+ ((releasePointExitVelocity == null) ? 0
						: releasePointExitVelocity.hashCode());
		result = prime * result
				+ ((releasePointId == null) ? 0 : releasePointId.hashCode());
		result = prime
				* result
				+ ((releasePointLatitudeDegrees == null) ? 0
						: releasePointLatitudeDegrees.hashCode());
		result = prime
				* result
				+ ((releasePointLongitudeDegrees == null) ? 0
						: releasePointLongitudeDegrees.hashCode());
		result = prime
				* result
				+ ((releasePointStackDiameter == null) ? 0
						: releasePointStackDiameter.hashCode());
		result = prime
				* result
				+ ((releasePointStackHeight == null) ? 0
						: releasePointStackHeight.hashCode());
		result = prime
				* result
				+ ((releasePointTemp == null) ? 0 : releasePointTemp.hashCode());
		result = prime * result
				+ ((sourceDesc == null) ? 0 : sourceDesc.hashCode());
		result = prime * result
				+ ((utmDatum == null) ? 0 : utmDatum.hashCode());
		result = prime * result
				+ ((utmEasting == null) ? 0 : utmEasting.hashCode());
		result = prime * result
				+ ((utmNorthing == null) ? 0 : utmNorthing.hashCode());
		result = prime * result + ((utmZone == null) ? 0 : utmZone.hashCode());
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
		ModelingExtractResult other = (ModelingExtractResult) obj;
		if (aqdSourceId == null) {
			if (other.aqdSourceId != null)
				return false;
		} else if (!aqdSourceId.equals(other.aqdSourceId))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (distanceToFacility == null) {
			if (other.distanceToFacility != null)
				return false;
		} else if (!distanceToFacility.equals(other.distanceToFacility))
			return false;
		if (distanceToReleasePoint == null) {
			if (other.distanceToReleasePoint != null)
				return false;
		} else if (!distanceToReleasePoint.equals(other.distanceToReleasePoint))
			return false;
		if (facilityName == null) {
			if (other.facilityName != null)
				return false;
		} else if (!facilityName.equals(other.facilityName))
			return false;
		if (facilityType == null) {
			if (other.facilityType != null)
				return false;
		} else if (!facilityType.equals(other.facilityType))
			return false;
		if (modelingSourceId == null) {
			if (other.modelingSourceId != null)
				return false;
		} else if (!modelingSourceId.equals(other.modelingSourceId))
			return false;
		if (pollutantAllowableEmissionsLbsHour == null) {
			if (other.pollutantAllowableEmissionsLbsHour != null)
				return false;
		} else if (!pollutantAllowableEmissionsLbsHour
				.equals(other.pollutantAllowableEmissionsLbsHour))
			return false;
		if (pollutantAllowableEmissionsTonsYear == null) {
			if (other.pollutantAllowableEmissionsTonsYear != null)
				return false;
		} else if (!pollutantAllowableEmissionsTonsYear
				.equals(other.pollutantAllowableEmissionsTonsYear))
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		if (pollutantLongTermEmissionsLimitType != other.pollutantLongTermEmissionsLimitType)
			return false;
		if (pollutantLongTermLimit == null) {
			if (other.pollutantLongTermLimit != null)
				return false;
		} else if (!pollutantLongTermLimit.equals(other.pollutantLongTermLimit))
			return false;
		if (pollutantPotentialEmissionsLbsHour == null) {
			if (other.pollutantPotentialEmissionsLbsHour != null)
				return false;
		} else if (!pollutantPotentialEmissionsLbsHour
				.equals(other.pollutantPotentialEmissionsLbsHour))
			return false;
		if (pollutantPotentialEmissionsTonsYear == null) {
			if (other.pollutantPotentialEmissionsTonsYear != null)
				return false;
		} else if (!pollutantPotentialEmissionsTonsYear
				.equals(other.pollutantPotentialEmissionsTonsYear))
			return false;
		if (pollutantShortTermEmissionsLimitType != other.pollutantShortTermEmissionsLimitType)
			return false;
		if (pollutantShortTermLimit == null) {
			if (other.pollutantShortTermLimit != null)
				return false;
		} else if (!pollutantShortTermLimit
				.equals(other.pollutantShortTermLimit))
			return false;
		if (releasePointBaseElevation == null) {
			if (other.releasePointBaseElevation != null)
				return false;
		} else if (!releasePointBaseElevation
				.equals(other.releasePointBaseElevation))
			return false;
		if (releasePointExitVelocity == null) {
			if (other.releasePointExitVelocity != null)
				return false;
		} else if (!releasePointExitVelocity
				.equals(other.releasePointExitVelocity))
			return false;
		if (releasePointId == null) {
			if (other.releasePointId != null)
				return false;
		} else if (!releasePointId.equals(other.releasePointId))
			return false;
		if (releasePointLatitudeDegrees == null) {
			if (other.releasePointLatitudeDegrees != null)
				return false;
		} else if (!releasePointLatitudeDegrees
				.equals(other.releasePointLatitudeDegrees))
			return false;
		if (releasePointLongitudeDegrees == null) {
			if (other.releasePointLongitudeDegrees != null)
				return false;
		} else if (!releasePointLongitudeDegrees
				.equals(other.releasePointLongitudeDegrees))
			return false;
		if (releasePointStackDiameter == null) {
			if (other.releasePointStackDiameter != null)
				return false;
		} else if (!releasePointStackDiameter
				.equals(other.releasePointStackDiameter))
			return false;
		if (releasePointStackHeight == null) {
			if (other.releasePointStackHeight != null)
				return false;
		} else if (!releasePointStackHeight
				.equals(other.releasePointStackHeight))
			return false;
		if (releasePointTemp == null) {
			if (other.releasePointTemp != null)
				return false;
		} else if (!releasePointTemp.equals(other.releasePointTemp))
			return false;
		if (sourceDesc == null) {
			if (other.sourceDesc != null)
				return false;
		} else if (!sourceDesc.equals(other.sourceDesc))
			return false;
		if (utmDatum == null) {
			if (other.utmDatum != null)
				return false;
		} else if (!utmDatum.equals(other.utmDatum))
			return false;
		if (utmEasting == null) {
			if (other.utmEasting != null)
				return false;
		} else if (!utmEasting.equals(other.utmEasting))
			return false;
		if (utmNorthing == null) {
			if (other.utmNorthing != null)
				return false;
		} else if (!utmNorthing.equals(other.utmNorthing))
			return false;
		if (utmZone == null) {
			if (other.utmZone != null)
				return false;
		} else if (!utmZone.equals(other.utmZone))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ModelingExtractResult [companyName=" + companyName
				+ ", facilityName=" + facilityName + ", facilityType="
				+ facilityType + ", distanceToFacility=" + distanceToFacility
				+ ", distanceToReleasePoint=" + distanceToReleasePoint
				+ ", modelingSourceId=" + modelingSourceId + ", aqdSourceId="
				+ aqdSourceId + ", sourceDesc=" + sourceDesc + ", utmEasting="
				+ utmEasting + ", utmNorthing=" + utmNorthing + ", utmDatum="
				+ utmDatum + ", utmZone=" + utmZone + ", releasePointId="
				+ releasePointId + ", releasePointBaseElevation="
				+ releasePointBaseElevation + ", releasePointStackHeight="
				+ releasePointStackHeight + ", releasePointTemp="
				+ releasePointTemp + ", releasePointExitVelocity="
				+ releasePointExitVelocity + ", releasePointStackDiameter="
				+ releasePointStackDiameter + ", pollutantCd=" + pollutantCd
				+ ", pollutantShortTermLimit=" + pollutantShortTermLimit
				+ ", pollutantLongTermLimit=" + pollutantLongTermLimit
				+ ", releasePointLatitudeDegrees="
				+ releasePointLatitudeDegrees
				+ ", releasePointLongitudeDegrees="
				+ releasePointLongitudeDegrees
				+ ", pollutantAllowableEmissionsLbsHour="
				+ pollutantAllowableEmissionsLbsHour
				+ ", pollutantAllowableEmissionsTonsYear="
				+ pollutantAllowableEmissionsTonsYear
				+ ", pollutantPotentialEmissionsLbsHour="
				+ pollutantPotentialEmissionsLbsHour
				+ ", pollutantPotentialEmissionsTonsYear="
				+ pollutantPotentialEmissionsTonsYear
				+ ", pollutantShortTermEmissionsLimitType="
				+ pollutantShortTermEmissionsLimitType
				+ ", pollutantLongTermEmissionsLimitType="
				+ pollutantLongTermEmissionsLimitType + "]";
	}

}
