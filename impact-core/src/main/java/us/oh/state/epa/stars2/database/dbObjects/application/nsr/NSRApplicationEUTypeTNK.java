package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeDef;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeTNK extends NSRApplicationEUType {

	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeTNK:";
	private BigDecimal maxHourlyThroughput;
	private String maxHourlyThroughputUnitsCd;
	private String isTankHeated;
	private Float operatingPressure;
	private Float vaporPressureOfMaterialStored;

	// fpEU data for validation
	private String fpEuMaterialTypeStored;

	public NSRApplicationEUTypeTNK() {
		super();
	}

	public NSRApplicationEUTypeTNK(NSRApplicationEUTypeTNK old) {
		super(old);
		if (old != null) {
			setMaxHourlyThroughput(old.getMaxHourlyThroughput());
			setMaxHourlyThroughputUnitsCd(old.getMaxHourlyThroughputUnitsCd());
			setIsTankHeated(old.getIsTankHeated());
			setOperatingPressure(old.getOperatingPressure());
			setVaporPressureOfMaterialStored(old
					.getVaporPressureOfMaterialStored());
			setFpEuMaterialTypeStored(old.getFpEuMaterialTypeStored());
		}
	}

	public BigDecimal getMaxHourlyThroughput() {
		return maxHourlyThroughput;
	}

	public void setMaxHourlyThroughput(BigDecimal maxHourlyThroughput) {
		this.maxHourlyThroughput = maxHourlyThroughput;
	}

	public String getMaxHourlyThroughputUnitsCd() {
		return maxHourlyThroughputUnitsCd;
	}

	public void setMaxHourlyThroughputUnitsCd(String maxHourlyThroughputUnitsCd) {
		this.maxHourlyThroughputUnitsCd = maxHourlyThroughputUnitsCd;
	}

	public String getIsTankHeated() {
		return isTankHeated;
	}

	public void setIsTankHeated(String isTankHeated) {
		this.isTankHeated = isTankHeated;
	}

	public Float getOperatingPressure() {
		return operatingPressure;
	}

	public void setOperatingPressure(Float operatingPressure) {
		this.operatingPressure = operatingPressure;
	}

	public Float getVaporPressureOfMaterialStored() {
		return vaporPressureOfMaterialStored;
	}

	public void setVaporPressureOfMaterialStored(
			Float vaporPressureOfMaterialStored) {
		this.vaporPressureOfMaterialStored = vaporPressureOfMaterialStored;
	}

	public String getFpEuMaterialTypeStored() {
		return fpEuMaterialTypeStored;
	}

	public void setFpEuMaterialTypeStored(String fpEuMaterialTypeStored) {
		this.fpEuMaterialTypeStored = fpEuMaterialTypeStored;
		if (!isLiquid()) {
			this.setOperatingPressure(null);
			this.setVaporPressureOfMaterialStored(null);
		}
	}

	public boolean isLiquid() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(fpEuMaterialTypeStored)
				&& TnkMaterialStoredTypeDef.LIQUID
						.equals(this.fpEuMaterialTypeStored);
		return ret;
	}

	public void loadFpEuTypeData(EmissionUnitType fpEuType) {
		if (fpEuType != null) {
			this.setFpEuMaterialTypeStored(fpEuType.getMaterialTypeStored());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setMaxHourlyThroughput(rs.getBigDecimal(
				"max_hourly_throughput"));
		setMaxHourlyThroughputUnitsCd(rs
				.getString("units_max_hourly_throughput_cd"));
		setIsTankHeated(rs.getString("is_tank_heated_cd"));
		setOperatingPressure(AbstractDAO.getFloat(rs, "operating_pressure"));
		setVaporPressureOfMaterialStored(AbstractDAO.getFloat(rs,
				"vapor_pressure_of_material_stored"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((maxHourlyThroughput == null) ? 0 : maxHourlyThroughput
						.hashCode());
		result = prime
				* result
				+ ((maxHourlyThroughputUnitsCd == null) ? 0
						: maxHourlyThroughputUnitsCd.hashCode());
		result = prime * result
				+ ((isTankHeated == null) ? 0 : isTankHeated.hashCode());
		result = prime
				* result
				+ ((operatingPressure == null) ? 0 : operatingPressure
						.hashCode());
		result = prime
				* result
				+ ((vaporPressureOfMaterialStored == null) ? 0
						: vaporPressureOfMaterialStored.hashCode());
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
		final NSRApplicationEUTypeTNK other = (NSRApplicationEUTypeTNK) obj;
		if (maxHourlyThroughput == null) {
			if (other.maxHourlyThroughput != null)
				return false;
		} else if (!maxHourlyThroughput.equals(other.maxHourlyThroughput))
			return false;
		if (maxHourlyThroughputUnitsCd == null) {
			if (other.maxHourlyThroughputUnitsCd != null)
				return false;
		} else if (!maxHourlyThroughputUnitsCd
				.equals(other.maxHourlyThroughputUnitsCd))
			return false;
		if (isTankHeated == null) {
			if (other.isTankHeated != null)
				return false;
		} else if (!isTankHeated.equals(other.isTankHeated))
			return false;
		if (operatingPressure == null) {
			if (other.operatingPressure != null)
				return false;
		} else if (!operatingPressure.equals(other.operatingPressure))
			return false;
		if (vaporPressureOfMaterialStored == null) {
			if (other.vaporPressureOfMaterialStored != null)
				return false;
		} else if (!vaporPressureOfMaterialStored
				.equals(other.vaporPressureOfMaterialStored))
			return false;
		return true;
	}

	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();
		validateRanges();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public void requiredFields() {
		requiredField(this.isTankHeated, PAGE_VIEW_ID_PREFIX + "isTankHeated",
				"Is Tank Heated", "isTankHeated");
		if (isLiquid()) {
			requiredField(this.operatingPressure, PAGE_VIEW_ID_PREFIX
					+ "operatingPressure", "Operating Pressure",
					"operatingPressure");
			requiredField(this.vaporPressureOfMaterialStored,
					PAGE_VIEW_ID_PREFIX + "vaporPressureOfMaterialStored",
					"Vapor Pressure of Material Stored",
					"vaporPressureOfMaterialStored");
		}
	}

	private void validateRanges() {
		checkRangeValues(this.maxHourlyThroughput, new BigDecimal(0), new BigDecimal(
				Float.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "maxHourlyThroughput",
				"Maximum Hourly Throughput");
		checkRangeValues(this.operatingPressure, new Float(0.0), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "operatingPressure",
				"Operating Pressure (psig)");
		checkRangeValues(this.vaporPressureOfMaterialStored, new Float(0.01),
				new Float(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX
						+ "vaporPressureOfMaterialStored",
				"Vapor Pressure of Material Stored (psig)");
	}
}
