package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.AppEUCirculationPumpTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeAMN extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeAMN:";

	private BigDecimal inletCO2Conc;
	private BigDecimal inletH2SConc;
	private BigDecimal acidGasCO2Conc;
	private BigDecimal acidGasH2SConc;
	private Float amineCircRate;
	private String amineCircUnitsCd;
	private String amineTypeCd;
	private Integer inletGasTemp;
	private Float inletGasPressure;
	private Float outletGasFlowRate;
	private Float acidGasFlowRate;
	private String amineCircPumpTypeCd;
	private Float pumpVolumeRatio;
	private BigDecimal maxLeanAmineCircRate;
	private BigDecimal actualLeanAmineCircRate;
	private String motiveGasPumpSource;

	public NSRApplicationEUTypeAMN() {
		super();
	}

	public NSRApplicationEUTypeAMN(NSRApplicationEUTypeAMN old) {
		super(old);
		if (old != null) {
			setInletCO2Conc(old.getInletCO2Conc());
			setInletH2SConc(old.getInletH2SConc());
			setAcidGasCO2Conc(old.getAcidGasCO2Conc());
			setAcidGasH2SConc(old.getAcidGasH2SConc());
			setAmineCircRate(old.getAmineCircRate());
			setAmineCircUnitsCd(old.getAmineCircUnitsCd());
			setAmineTypeCd(old.getAmineTypeCd());
			setInletGasTemp(old.getInletGasTemp());
			setInletGasPressure(old.getInletGasPressure());
			setOutletGasFlowRate(old.getOutletGasFlowRate());
			setAcidGasFlowRate(old.getAcidGasFlowRate());
			setAmineCircPumpTypeCd(old.getAmineCircPumpTypeCd());
			setPumpVolumeRatio(old.getPumpVolumeRatio());
			setMaxLeanAmineCircRate(old.getMaxLeanAmineCircRate());
			setActualLeanAmineCircRate(old.getActualLeanAmineCircRate());
			setMotiveGasPumpSource(old.getMotiveGasPumpSource());
		}
	}

	public BigDecimal getInletCO2Conc() {
		return inletCO2Conc;
	}

	public void setInletCO2Conc(BigDecimal inletCO2Conc) {
		this.inletCO2Conc = inletCO2Conc;
	}

	public BigDecimal getInletH2SConc() {
		return inletH2SConc;
	}

	public void setInletH2SConc(BigDecimal inletH2SConc) {
		this.inletH2SConc = inletH2SConc;
	}

	public BigDecimal getAcidGasCO2Conc() {
		return acidGasCO2Conc;
	}

	public void setAcidGasCO2Conc(BigDecimal acidGasCO2Conc) {
		this.acidGasCO2Conc = acidGasCO2Conc;
	}

	public BigDecimal getAcidGasH2SConc() {
		return acidGasH2SConc;
	}

	public void setAcidGasH2SConc(BigDecimal acidGasH2SConc) {
		this.acidGasH2SConc = acidGasH2SConc;
	}

	public Float getAmineCircRate() {
		return amineCircRate;
	}

	public void setAmineCircRate(Float amineCircRate) {
		this.amineCircRate = amineCircRate;
	}

	public String getAmineCircUnitsCd() {
		return amineCircUnitsCd;
	}

	public void setAmineCircUnitsCd(String amineCircUnitsCd) {
		this.amineCircUnitsCd = amineCircUnitsCd;
	}

	public String getAmineTypeCd() {
		return amineTypeCd;
	}

	public void setAmineTypeCd(String amineTypeCd) {
		this.amineTypeCd = amineTypeCd;
	}

	public Integer getInletGasTemp() {
		return inletGasTemp;
	}

	public void setInletGasTemp(Integer inletGasTemp) {
		this.inletGasTemp = inletGasTemp;
	}

	public Float getInletGasPressure() {
		return inletGasPressure;
	}

	public void setInletGasPressure(Float inletGasPressure) {
		this.inletGasPressure = inletGasPressure;
	}

	public Float getOutletGasFlowRate() {
		return outletGasFlowRate;
	}

	public void setOutletGasFlowRate(Float outletGasFlowRate) {
		this.outletGasFlowRate = outletGasFlowRate;
	}

	public Float getAcidGasFlowRate() {
		return acidGasFlowRate;
	}

	public void setAcidGasFlowRate(Float acidGasFlowRate) {
		this.acidGasFlowRate = acidGasFlowRate;
	}

	public String getAmineCircPumpTypeCd() {
		return amineCircPumpTypeCd;
	}

	public void setAmineCircPumpTypeCd(String amineCircPumpTypeCd) {
		this.amineCircPumpTypeCd = amineCircPumpTypeCd;

		if (!isCirculationPumpTypeGas()) {
			setPumpVolumeRatio(null);
		}
	}

	public Float getPumpVolumeRatio() {
		return pumpVolumeRatio;
	}

	public void setPumpVolumeRatio(Float pumpVolumeRatio) {
		this.pumpVolumeRatio = pumpVolumeRatio;
	}

	public BigDecimal getMaxLeanAmineCircRate() {
		return maxLeanAmineCircRate;
	}

	public void setMaxLeanAmineCircRate(BigDecimal maxLeanAmineCircRate) {
		this.maxLeanAmineCircRate = maxLeanAmineCircRate;
	}

	public BigDecimal getActualLeanAmineCircRate() {
		return actualLeanAmineCircRate;
	}

	public void setActualLeanAmineCircRate(BigDecimal actualLeanAmineCircRate) {
		this.actualLeanAmineCircRate = actualLeanAmineCircRate;
	}

	public String getMotiveGasPumpSource() {
		return motiveGasPumpSource;
	}

	public void setMotiveGasPumpSource(String motiveGasPumpSource) {
		this.motiveGasPumpSource = motiveGasPumpSource;
	}

	public boolean isCirculationPumpTypeGas() {
		boolean ret = false;

		ret = !Utility.isNullOrEmpty(this.amineCircPumpTypeCd)
				&& this.amineCircPumpTypeCd
						.equals(AppEUCirculationPumpTypeDef.GAS);

		return ret;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setInletCO2Conc(rs.getBigDecimal("inlet_c02_conc"));
		setInletH2SConc(rs.getBigDecimal("inlet_h2s_conc"));
		setAcidGasCO2Conc(rs.getBigDecimal("acid_gas_c02_conc"));
		setAcidGasH2SConc(rs.getBigDecimal("acid_gas_h2s_conc"));
		setAmineCircRate(AbstractDAO.getFloat(rs, "amine_circ_rate"));
		setAmineCircUnitsCd(rs.getString("amine_circ_units_cd"));
		setAmineTypeCd(rs.getString("amine_type_cd"));
		setInletGasTemp(AbstractDAO.getInteger(rs, "inlet_gas_temp"));
		setInletGasPressure(AbstractDAO.getFloat(rs, "inlet_gas_pressure"));
		setOutletGasFlowRate(AbstractDAO.getFloat(rs, "outlet_gas_flow_rate"));
		setAcidGasFlowRate(AbstractDAO.getFloat(rs, "acid_gas_flow_rate"));
		setAmineCircPumpTypeCd(rs.getString("amine_circ_pump_type_cd"));
		setPumpVolumeRatio(AbstractDAO.getFloat(rs, "pump_volume_ratio"));
		setMaxLeanAmineCircRate(rs.getBigDecimal(
				"max_lean_amine_circ_rate"));
		setActualLeanAmineCircRate(rs.getBigDecimal(
				"actual_lean_amine_circ_rate"));
		setMotiveGasPumpSource(rs.getString("motive_gas_pump_source"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((inletCO2Conc == null) ? 0 : inletCO2Conc.hashCode());
		result = prime * result
				+ ((inletH2SConc == null) ? 0 : inletH2SConc.hashCode());
		result = prime * result
				+ ((acidGasCO2Conc == null) ? 0 : acidGasCO2Conc.hashCode());
		result = prime * result
				+ ((amineCircRate == null) ? 0 : amineCircRate.hashCode());
		result = prime
				* result
				+ ((amineCircUnitsCd == null) ? 0 : amineCircUnitsCd.hashCode());
		result = prime * result
				+ ((amineTypeCd == null) ? 0 : amineTypeCd.hashCode());
		result = prime * result
				+ ((inletGasTemp == null) ? 0 : inletGasTemp.hashCode());
		result = prime
				* result
				+ ((inletGasPressure == null) ? 0 : inletGasPressure.hashCode());
		result = prime
				* result
				+ ((outletGasFlowRate == null) ? 0 : outletGasFlowRate
						.hashCode());
		result = prime * result
				+ ((acidGasFlowRate == null) ? 0 : acidGasFlowRate.hashCode());
		result = prime
				* result
				+ ((amineCircPumpTypeCd == null) ? 0 : amineCircPumpTypeCd
						.hashCode());
		result = prime * result
				+ ((pumpVolumeRatio == null) ? 0 : pumpVolumeRatio.hashCode());
		result = prime
				* result
				+ ((maxLeanAmineCircRate == null) ? 0 : maxLeanAmineCircRate
						.hashCode());
		result = prime
				* result
				+ ((actualLeanAmineCircRate == null) ? 0
						: actualLeanAmineCircRate.hashCode());
		result = prime
				* result
				+ ((motiveGasPumpSource == null) ? 0 : motiveGasPumpSource
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
		final NSRApplicationEUTypeAMN other = (NSRApplicationEUTypeAMN) obj;
		if (inletCO2Conc == null) {
			if (other.inletCO2Conc != null)
				return false;
		} else if (!inletCO2Conc.equals(other.inletCO2Conc))
			return false;
		if (inletH2SConc == null) {
			if (other.inletH2SConc != null)
				return false;
		} else if (!inletH2SConc.equals(other.inletH2SConc))
			return false;
		if (acidGasCO2Conc == null) {
			if (other.acidGasCO2Conc != null)
				return false;
		} else if (!acidGasCO2Conc.equals(other.acidGasCO2Conc))
			return false;
		if (acidGasH2SConc == null) {
			if (other.acidGasH2SConc != null)
				return false;
		} else if (!acidGasH2SConc.equals(other.acidGasH2SConc))
			return false;
		if (amineCircRate == null) {
			if (other.amineCircRate != null)
				return false;
		} else if (!amineCircRate.equals(other.amineCircRate))
			return false;
		if (amineCircUnitsCd == null) {
			if (other.amineCircUnitsCd != null)
				return false;
		} else if (!amineCircUnitsCd.equals(other.amineCircUnitsCd))
			return false;
		if (amineTypeCd == null) {
			if (other.amineTypeCd != null)
				return false;
		} else if (!amineTypeCd.equals(other.amineTypeCd))
			return false;
		if (inletGasTemp == null) {
			if (other.inletGasTemp != null)
				return false;
		} else if (!inletGasTemp.equals(other.inletGasTemp))
			return false;
		if (inletGasPressure == null) {
			if (other.inletGasPressure != null)
				return false;
		} else if (!inletGasPressure.equals(other.inletGasPressure))
			return false;
		if (outletGasFlowRate == null) {
			if (other.outletGasFlowRate != null)
				return false;
		} else if (!outletGasFlowRate.equals(other.outletGasFlowRate))
			return false;
		if (acidGasFlowRate == null) {
			if (other.acidGasFlowRate != null)
				return false;
		} else if (!acidGasFlowRate.equals(other.acidGasFlowRate))
			return false;
		if (amineCircPumpTypeCd == null) {
			if (other.amineCircPumpTypeCd != null)
				return false;
		} else if (!amineCircPumpTypeCd.equals(other.amineCircPumpTypeCd))
			return false;
		if (pumpVolumeRatio == null) {
			if (other.pumpVolumeRatio != null)
				return false;
		} else if (!pumpVolumeRatio.equals(other.pumpVolumeRatio))
			return false;
		if (maxLeanAmineCircRate == null) {
			if (other.maxLeanAmineCircRate != null)
				return false;
		} else if (!maxLeanAmineCircRate.equals(other.maxLeanAmineCircRate))
			return false;
		if (actualLeanAmineCircRate == null) {
			if (other.actualLeanAmineCircRate != null)
				return false;
		} else if (!actualLeanAmineCircRate
				.equals(other.actualLeanAmineCircRate))
			return false;
		if (motiveGasPumpSource == null) {
			if (other.motiveGasPumpSource != null)
				return false;
		} else if (!motiveGasPumpSource.equals(other.motiveGasPumpSource))
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
		requiredField(this.inletCO2Conc, PAGE_VIEW_ID_PREFIX + "inletCO2Conc",
				"Inlet CO2 Concentration (%)", "inletCO2Conc");

		requiredField(this.inletH2SConc, PAGE_VIEW_ID_PREFIX + "inletH2SConc",
				"Inlet H2S Concentration (%)", "inletH2SConc");

		requiredField(this.acidGasCO2Conc, PAGE_VIEW_ID_PREFIX
				+ "acidGasCO2Conc", "Acid Gas CO2 Concentration (%)",
				"acidGasCO2Conc");

		requiredField(this.acidGasH2SConc, PAGE_VIEW_ID_PREFIX
				+ "acidGasH2SConc", "Acid Gas H2S Concentration (%)",
				"acidGasH2SConc");

		requiredField(this.amineCircRate,
				PAGE_VIEW_ID_PREFIX + "amineCircRate",
				"Amine Circulation Rate", "amineCircRate");

		requiredField(this.amineCircUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "amineCircUnitsCd", "Amine Circulation Rate Units", "amineCircUnitsCd");

		requiredField(this.amineTypeCd, PAGE_VIEW_ID_PREFIX + "amineTypeCd",
				"Type of Amine", "amineTypeCd");

		requiredField(this.inletGasTemp, PAGE_VIEW_ID_PREFIX + "inletGasTemp",
				"Temperature of Inlet Gas (F)", "inletGasTemp");

		requiredField(this.inletGasPressure, PAGE_VIEW_ID_PREFIX
				+ "inletGasPressure", "Pressure of Inlet Gas (psig)",
				"inletGasPressure");

		requiredField(this.outletGasFlowRate, PAGE_VIEW_ID_PREFIX
				+ "outletGasFlowRate", "Flow Rate of Outlet Gas (MMscf/day)",
				"outletGasFlowRate");

		requiredField(this.acidGasFlowRate, PAGE_VIEW_ID_PREFIX
				+ "acidGasFlowRate", "Flow Rate of Acid Gas (MMscf/day)",
				"acidGasFlowRate");

		requiredField(this.amineCircPumpTypeCd, PAGE_VIEW_ID_PREFIX
				+ "amineCircPumpTypeCd", "Type of Amine Circulation Pump",
				"amineCircPumpTypeCd");

		// TODO read from definition list
		if (isCirculationPumpTypeGas()) {
			requiredField(this.pumpVolumeRatio, PAGE_VIEW_ID_PREFIX
					+ "pumpVolumeRatio", "Pump Volume Ratio (acfm/gpm)",
					"pumpVolumeRatio");
		}

		requiredField(this.maxLeanAmineCircRate, PAGE_VIEW_ID_PREFIX
				+ "maxLeanAmineCircRate",
				"Maximum LEAN Amine Circulation Rate (gallons/minute)",
				"maxLeanAmineCircRate");

		requiredField(this.actualLeanAmineCircRate, PAGE_VIEW_ID_PREFIX
				+ "actualLeanAmineCircRate",
				"Actual LEAN Amine Circulation Rate (gallons/minute)",
				"actualLeanAmineCircRate");

		requiredField(this.motiveGasPumpSource, PAGE_VIEW_ID_PREFIX
				+ "motiveGasPumpSource", "Source of Motive Gas for Pump",
				"motiveGasPumpSource");

	}

	public void validateRanges() {
		checkRangeValues(inletCO2Conc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "inletCO2Conc",
				"Inlet CO2 Concentration (%)");
		checkRangeValues(inletH2SConc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "inletH2SConc",
				"Inlet H2S Concentration (%)");
		checkRangeValues(acidGasCO2Conc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "acidGasCO2Conc",
				"Acid Gas CO2 Concentration (%)");
		checkRangeValues(acidGasH2SConc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "acidGasH2SConc",
				"Acid Gas H2S Concentration (%)");
		checkRangeValues(amineCircRate, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "amineCircRate",
				"Amine Circulation Rate");
		checkRangeValues(inletGasTemp, new Integer(1), new Integer(
				Integer.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "inletGasTemp",
				"Temperature of Inlet Gas (F)");
		checkRangeValues(inletGasPressure, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "inletGasPressure",
				"Pressure of Inlet Gas (psig)");
		checkRangeValues(outletGasFlowRate, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "outletGasFlowRate",
				"Flow Rate of Outlet Gas (MMscf/day)");
		checkRangeValues(acidGasFlowRate, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "acidGasFlowRate",
				"Flow Rate of Acid Gas (MMscf/day)");

		if (isCirculationPumpTypeGas()) {
			checkRangeValues(pumpVolumeRatio, new Float(0.01), new Float(
					Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "pumpVolumeRatio",
					"Pump Volume Ratio (acfm/gpm)");
		}
		checkRangeValues(maxLeanAmineCircRate, new BigDecimal(1), new BigDecimal(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX
				+ "maxLeanAmineCircRate",
				"Maximum LEAN Amine Circulation Rate (gallons/minute)");
		checkRangeValues(actualLeanAmineCircRate, new BigDecimal(1), new BigDecimal(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX
				+ "actualLeanAmineCircRate",
				"Actual LEAN Amine Circulation Rate (gallons/minute)");
	}
}
