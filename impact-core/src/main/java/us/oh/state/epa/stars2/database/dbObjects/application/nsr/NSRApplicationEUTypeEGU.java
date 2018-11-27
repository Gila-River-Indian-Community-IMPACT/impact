package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.wy.state.deq.impact.def.EguUnitTypeDef;
import us.wy.state.deq.impact.def.PrimaryFuelTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeEGU extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeEGU:";

	private Float btu;
	private String unitsBtuCd;
	private Float fuelSulfur;
	private String unitsFuelSulfurCd;
	private Integer netElectricalOutput;
	private Integer grossElectricalOutput;
	private String turbineCycleTypeCd;
	private String coalUsageTypeCd;

	// fpEU data for validate
	private String fpEuUnitType;
	private String fpEuPrimaryFuelType;

	public NSRApplicationEUTypeEGU() {
		super();
	}

	public NSRApplicationEUTypeEGU(NSRApplicationEUTypeEGU old) {
		super(old);
		if (old != null) {
			setBtu(old.getBtu());
			setUnitsBtuCd(old.getUnitsBtuCd());
			setFuelSulfur(old.getFuelSulfur());
			setUnitsFuelSulfurCd(old.getUnitsFuelSulfurCd());
			setNetElectricalOutput(old.getNetElectricalOutput());
			setGrossElectricalOutput(old.getGrossElectricalOutput());
			setTurbineCycleTypeCd(old.getTurbineCycleTypeCd());
			setCoalUsageTypeCd(old.getCoalUsageTypeCd());
		}
	}

	public Float getBtu() {
		return btu;
	}

	public void setBtu(Float btu) {
		this.btu = btu;
	}

	public String getUnitsBtuCd() {
		return unitsBtuCd;
	}

	public void setUnitsBtuCd(String unitsBtuCd) {
		this.unitsBtuCd = unitsBtuCd;
	}

	public Float getFuelSulfur() {
		return fuelSulfur;
	}

	public void setFuelSulfur(Float fuelSulfur) {
		this.fuelSulfur = fuelSulfur;
	}

	public String getUnitsFuelSulfurCd() {
		return unitsFuelSulfurCd;
	}

	public void setUnitsFuelSulfurCd(String unitsFuelSulfurCd) {
		this.unitsFuelSulfurCd = unitsFuelSulfurCd;
	}

	public Integer getNetElectricalOutput() {
		return netElectricalOutput;
	}

	public void setNetElectricalOutput(Integer netElectricalOutput) {
		this.netElectricalOutput = netElectricalOutput;
	}

	public Integer getGrossElectricalOutput() {
		return grossElectricalOutput;
	}

	public void setGrossElectricalOutput(Integer grossElectricalOutput) {
		this.grossElectricalOutput = grossElectricalOutput;
	}

	public String getTurbineCycleTypeCd() {
		return turbineCycleTypeCd;
	}

	public void setTurbineCycleTypeCd(String turbineCycleTypeCd) {
		this.turbineCycleTypeCd = turbineCycleTypeCd;
	}

	public String getCoalUsageTypeCd() {
		return coalUsageTypeCd;
	}

	public void setCoalUsageTypeCd(String coalUsageTypeCd) {
		this.coalUsageTypeCd = coalUsageTypeCd;
	}

	public String getFpEuUnitType() {
		return fpEuUnitType;
	}

	public void setFpEuUnitType(String fpEuUnitType) {
		this.fpEuUnitType = fpEuUnitType;
		if (!isTurbine()) {
			setTurbineCycleTypeCd(null);
		}
	}

	public String getFpEuPrimaryFuelType() {
		return fpEuPrimaryFuelType;
	}

	public void setFpEuPrimaryFuelType(String fpEuPrimaryFuelType) {
		this.fpEuPrimaryFuelType = fpEuPrimaryFuelType;
		if (!isCoal()) {
			setCoalUsageTypeCd(null);
		}
	}

	public boolean isTurbine() {
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(this.fpEuUnitType)
				&& EguUnitTypeDef.TURBINE.equals(this.fpEuUnitType);
		return ret;
	}

	public boolean isCoal() {
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(this.fpEuPrimaryFuelType)
				&& PrimaryFuelTypeDef.COAL.equals(this.fpEuPrimaryFuelType);
		return ret;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setBtu(AbstractDAO.getFloat(rs, "btu"));
		setUnitsBtuCd(rs.getString("units_btu_cd"));
		setFuelSulfur(AbstractDAO.getFloat(rs, "fuel_sulfur"));
		setUnitsFuelSulfurCd(rs.getString("units_fuel_sulfur_cd"));
		setNetElectricalOutput(AbstractDAO.getInteger(rs,
				"net_electrical_output"));
		setGrossElectricalOutput(AbstractDAO.getInteger(rs,
				"gross_electrical_output"));
		setTurbineCycleTypeCd(rs.getString("turbine_cycle_type_cd"));
		setCoalUsageTypeCd(rs.getString("coal_usage_type_cd"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((btu == null) ? 0 : btu.hashCode());
		result = prime * result
				+ ((unitsBtuCd == null) ? 0 : unitsBtuCd.hashCode());
		result = prime * result
				+ ((fuelSulfur == null) ? 0 : fuelSulfur.hashCode());
		result = prime
				* result
				+ ((unitsFuelSulfurCd == null) ? 0 : unitsFuelSulfurCd
						.hashCode());
		result = prime
				* result
				+ ((netElectricalOutput == null) ? 0 : netElectricalOutput
						.hashCode());
		result = prime
				* result
				+ ((grossElectricalOutput == null) ? 0 : grossElectricalOutput
						.hashCode());
		result = prime
				* result
				+ ((turbineCycleTypeCd == null) ? 0 : turbineCycleTypeCd
						.hashCode());
		result = prime * result
				+ ((coalUsageTypeCd == null) ? 0 : coalUsageTypeCd.hashCode());

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
		final NSRApplicationEUTypeEGU other = (NSRApplicationEUTypeEGU) obj;
		if (btu == null) {
			if (other.btu != null)
				return false;
		} else if (!btu.equals(other.btu))
			return false;
		if (unitsBtuCd == null) {
			if (other.unitsBtuCd != null)
				return false;
		} else if (!unitsBtuCd.equals(other.unitsBtuCd))
			return false;
		if (fuelSulfur == null) {
			if (other.fuelSulfur != null)
				return false;
		} else if (!fuelSulfur.equals(other.fuelSulfur))
			return false;
		if (unitsFuelSulfurCd == null) {
			if (other.unitsFuelSulfurCd != null)
				return false;
		} else if (!unitsFuelSulfurCd.equals(other.unitsFuelSulfurCd))
			return false;
		if (netElectricalOutput == null) {
			if (other.netElectricalOutput != null)
				return false;
		} else if (!netElectricalOutput.equals(other.netElectricalOutput))
			return false;
		if (grossElectricalOutput == null) {
			if (other.grossElectricalOutput != null)
				return false;
		} else if (!grossElectricalOutput.equals(other.grossElectricalOutput))
			return false;
		if (turbineCycleTypeCd == null) {
			if (other.turbineCycleTypeCd != null)
				return false;
		} else if (!turbineCycleTypeCd.equals(other.turbineCycleTypeCd))
			return false;
		if (coalUsageTypeCd == null) {
			if (other.coalUsageTypeCd != null)
				return false;
		} else if (!coalUsageTypeCd.equals(other.coalUsageTypeCd))
			return false;
		return true;
	}

	@Override
	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();
		validateRanges();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public void requiredFields() {
		requiredField(this.btu, PAGE_VIEW_ID_PREFIX + "btu", "Btu Content",
				"btu");
		requiredField(this.unitsBtuCd, PAGE_VIEW_ID_PREFIX + "unitsBtuCd",
				"Units", "unitsBtuCd");
		requiredField(this.fuelSulfur, PAGE_VIEW_ID_PREFIX + "fuelSulfur",
				"Fuel Sulfur Content", "fuelSulfur");
		requiredField(this.unitsFuelSulfurCd, PAGE_VIEW_ID_PREFIX
				+ "unitsfuelsulfurcd", "Units", "unitsfuelsulfurcd");
		requiredField(this.netElectricalOutput, PAGE_VIEW_ID_PREFIX
				+ "netElectricalOutput", "Net Electrical Output (MW)",
				"netElectricalOutput");
		requiredField(this.grossElectricalOutput, PAGE_VIEW_ID_PREFIX
				+ "grossElectricalOutput", "Gross Electrical Output (MW)",
				"grossElectricalOutput");
		if (isTurbine()) {
			requiredField(this.turbineCycleTypeCd, PAGE_VIEW_ID_PREFIX
					+ "turbineCycleTypeCd", "Turbine Cycle Type",
					"turbineCycleTypeCd");
		}
		if (isCoal()) {
			requiredField(this.coalUsageTypeCd, PAGE_VIEW_ID_PREFIX
					+ "coalUsageTypeCd", "Coal Usage Type", "coalUsageTypeCd");
		}
	}

	public void validateRanges() {
		checkRangeValues(this.btu, new Float(0.01), new Float(Float.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "btu", "Btu Content");
		checkRangeValues(this.fuelSulfur, new Float(0.00), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelSulfur",
				"Fuel Sulfur Content");
		checkRangeValues(this.netElectricalOutput, new Integer(1), new Integer(
				Integer.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "netElectricalOutput",
				"Net Electrical Output (MW)");
		checkRangeValues(this.grossElectricalOutput, new Integer(1),
				new Integer(Integer.MAX_VALUE), PAGE_VIEW_ID_PREFIX
						+ "grossElectricalOutput",
				"Gross Electrical Output (MW)");
	}

	public void loadFpEuTypeData(EmissionUnitType fpEuType) {
		if (fpEuType != null) {
			this.setFpEuUnitType(fpEuType.getUnitType());
			this.setFpEuPrimaryFuelType(fpEuType.getPrimaryFuelType());
		}
	}
}
