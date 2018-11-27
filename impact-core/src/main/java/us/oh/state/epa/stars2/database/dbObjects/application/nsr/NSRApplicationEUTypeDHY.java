package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.AppEUCirculationRateDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeDHY extends NSRApplicationEUType {

	/******** Variables **********/
	private static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeDHY:";

	private Integer applicationEUId;
	private Integer temperatureOfWetGas;
	private Float pressureOfWetGas;
	private BigDecimal waterContentOfWetGas;
	private Float flowRateOfDryGas;
	private Float waterContentOfDryGas;
	private String manufactureNameOfGlycolCircPump;
	private String modelNameAndNoOfGlycolCircPump;
	private String typeOfGlycolCirculationPumpCd;
	private Float pumpVolumeRatio;
	private BigDecimal maxLeanGlycolCirculationRate;
	private BigDecimal actualLeanGlycolCirculationRate;
	private String sourceOfMotiveGasForPump;
	private String additionalGasStrippingCd;
	private String sourceOfStrippingGasCd;
	private Float strippingGasRate;
	private String includeGlycolFlashTankSeparatorCd;
	private Float flashTankOffGasStream;
	private String flashVaporsRouted;
	private Integer operatingTemperature;
	private Float operatingPressure;
	private String isVesselHeatedCd;

	/******** Properties **********/
	public Integer getApplicationEUId() {
		return applicationEUId;
	}

	public void setApplicationEUId(Integer applicationEUId) {
		this.applicationEUId = applicationEUId;
	}

	public Integer getTemperatureOfWetGas() {
		return temperatureOfWetGas;
	}

	public void setTemperatureOfWetGas(Integer temperatureOfWetGas) {
		this.temperatureOfWetGas = temperatureOfWetGas;
		requiredFieldTemperatureOfWetGas();
	}

	public Float getPressureOfWetGas() {
		return pressureOfWetGas;
	}

	public void setPressureOfWetGas(Float pressureOfWetGas) {
		this.pressureOfWetGas = pressureOfWetGas;
		requiredFieldPressureOfWetGas();
	}

	public BigDecimal getWaterContentOfWetGas() {
		return waterContentOfWetGas;
	}

	public void setWaterContentOfWetGas(BigDecimal waterContentOfWetGas) {
		this.waterContentOfWetGas = waterContentOfWetGas;
	}

	public Float getFlowRateOfDryGas() {
		return flowRateOfDryGas;
	}

	public void setFlowRateOfDryGas(Float flowRateOfDryGas) {
		this.flowRateOfDryGas = flowRateOfDryGas;
		requiredFieldFlowRateOfDryGas();
	}

	public Float getWaterContentOfDryGas() {
		return waterContentOfDryGas;
	}

	public void setWaterContentOfDryGas(Float waterContentOfDryGas) {
		this.waterContentOfDryGas = waterContentOfDryGas;
		requiredFieldWaterContentOfDryGas();
	}

	public String getManufactureNameOfGlycolCircPump() {
		return manufactureNameOfGlycolCircPump;
	}

	public void setManufactureNameOfGlycolCircPump(
			String manufactureNameOfGlycolCircPump) {
		this.manufactureNameOfGlycolCircPump = manufactureNameOfGlycolCircPump;
		requiredFieldManufactureNameOfGlycolCircPump();
	}

	public String getModelNameAndNoOfGlycolCircPump() {
		return modelNameAndNoOfGlycolCircPump;
	}

	public void setModelNameAndNoOfGlycolCircPump(
			String modelNameAndNoOfGlycolCircPump) {
		this.modelNameAndNoOfGlycolCircPump = modelNameAndNoOfGlycolCircPump;
		requiredFieldModelNameAndNoOfGlycolCircPump();
	}

	public String getTypeOfGlycolCirculationPumpCd() {
		return typeOfGlycolCirculationPumpCd;
	}

	public void setTypeOfGlycolCirculationPumpCd(
			String typeOfGlycolCirculationPumpCd) {
		this.typeOfGlycolCirculationPumpCd = typeOfGlycolCirculationPumpCd;

		if (!isGasClycolCirculationPump()) {
			resetClycolCirculationPumpRelatedFields();
		}

		requiredFieldTypeOfGlycolCirculationPumpCd();
	}

	public boolean isGasClycolCirculationPump() {
		return !Utility.isNullOrEmpty(typeOfGlycolCirculationPumpCd)
				&& typeOfGlycolCirculationPumpCd
						.equals(AppEUCirculationRateDef.Gas);
	}

	public Float getPumpVolumeRatio() {
		return pumpVolumeRatio;
	}

	public void setPumpVolumeRatio(Float pumpVolumeRatio) {
		this.pumpVolumeRatio = pumpVolumeRatio;
		requiredFieldPumpVolumeRatio();
	}

	public BigDecimal getMaxLeanGlycolCirculationRate() {
		return maxLeanGlycolCirculationRate;
	}

	public void setMaxLeanGlycolCirculationRate(
			BigDecimal maxLeanGlycolCirculationRate) {
		this.maxLeanGlycolCirculationRate = maxLeanGlycolCirculationRate;
		requiredFieldMaxLeanGlycolCirculationRate();
	}

	public BigDecimal getActualLeanGlycolCirculationRate() {
		return actualLeanGlycolCirculationRate;
	}

	public void setActualLeanGlycolCirculationRate(
			BigDecimal actualLeanGlycolCirculationRate) {
		this.actualLeanGlycolCirculationRate = actualLeanGlycolCirculationRate;
		requiredFieldActualLeanGlycolCirculationRate();
	}

	public String getSourceOfMotiveGasForPump() {
		return sourceOfMotiveGasForPump;
	}

	public void setSourceOfMotiveGasForPump(String sourceOfMotiveGasForPump) {
		this.sourceOfMotiveGasForPump = sourceOfMotiveGasForPump;
		requiredFieldSourceOfMotiveGasForPump();
	}

	public String getAdditionalGasStrippingCd() {
		return additionalGasStrippingCd;
	}

	public void setAdditionalGasStrippingCd(String additionalGasStrippingCd) {
		this.additionalGasStrippingCd = additionalGasStrippingCd;

		if (!checkAdditionalGasStripping()) {
			resetAdditionalGasStrippingRelatedFields();
		}

		requiredFieldAdditionalGasStrippingCd();
	}

	public String getSourceOfStrippingGasCd() {
		return sourceOfStrippingGasCd;
	}

	public void setSourceOfStrippingGasCd(String sourceOfStrippingGasCd) {
		this.sourceOfStrippingGasCd = sourceOfStrippingGasCd;
		requiredFieldSourceOfStrippingGasCd();
	}

	public Float getStrippingGasRate() {
		return strippingGasRate;
	}

	public void setStrippingGasRate(Float strippingGasRate) {
		this.strippingGasRate = strippingGasRate;
		requiredFieldStrippingGasRate();
	}

	public String getIncludeGlycolFlashTankSeparatorCd() {
		return includeGlycolFlashTankSeparatorCd;
	}

	public void setIncludeGlycolFlashTankSeparatorCd(
			String includeGlycolFlashTankSeparatorCd) {
		this.includeGlycolFlashTankSeparatorCd = includeGlycolFlashTankSeparatorCd;

		if (!checkIncludeGlycolFlashTankSeparator()) {
			resetIncludeGlycolFlashRelatedFields();
		}

		requiredFieldIncludeGlycolFlashTankSeparatorCd();
	}

	public Float getFlashTankOffGasStream() {
		return flashTankOffGasStream;
	}

	public void setFlashTankOffGasStream(Float flashTankOffGasStream) {
		this.flashTankOffGasStream = flashTankOffGasStream;
		requiredFieldFlashTankOffGasStream();
	}

	public String getFlashVaporsRouted() {
		return flashVaporsRouted;
	}

	public void setFlashVaporsRouted(String flashVaporsRouted) {
		this.flashVaporsRouted = flashVaporsRouted;
		requiredFieldFlashVaporsRouted();
	}

	public Integer getOperatingTemperature() {
		return operatingTemperature;
	}

	public void setOperatingTemperature(Integer operatingTemperature) {
		this.operatingTemperature = operatingTemperature;
		requiredFieldOperatingTemperature();
	}

	public Float getOperatingPressure() {
		return operatingPressure;
	}

	public void setOperatingPressure(Float operatingPressure) {
		this.operatingPressure = operatingPressure;
		requiredFieldOperatingPressure();
	}

	public String getIsVesselHeatedCd() {
		return isVesselHeatedCd;
	}

	public void setIsVesselHeatedCd(String isVesselHeatedCd) {
		this.isVesselHeatedCd = isVesselHeatedCd;
		requiredFieldIsVesselHeatedCd();
	}

	public NSRApplicationEUTypeDHY() {
		super();
	}

	public NSRApplicationEUTypeDHY(NSRApplicationEUTypeDHY old) {
		super(old);
		if (old != null) {
			setTemperatureOfWetGas(old.getTemperatureOfWetGas());
			setPressureOfWetGas(old.getPressureOfWetGas());
			setWaterContentOfWetGas(old.getWaterContentOfWetGas());
			setFlowRateOfDryGas(old.getFlowRateOfDryGas());
			setWaterContentOfDryGas(old.getWaterContentOfDryGas());
			setManufactureNameOfGlycolCircPump(old
					.getManufactureNameOfGlycolCircPump());
			setModelNameAndNoOfGlycolCircPump(old
					.getModelNameAndNoOfGlycolCircPump());
			setTypeOfGlycolCirculationPumpCd(old
					.getTypeOfGlycolCirculationPumpCd());
			setPumpVolumeRatio(old.getPumpVolumeRatio());
			setMaxLeanGlycolCirculationRate(old
					.getMaxLeanGlycolCirculationRate());
			setActualLeanGlycolCirculationRate(old
					.getActualLeanGlycolCirculationRate());
			setSourceOfMotiveGasForPump(old.getSourceOfMotiveGasForPump());
			setAdditionalGasStrippingCd(old.getAdditionalGasStrippingCd());
			setSourceOfStrippingGasCd(old.getSourceOfStrippingGasCd());
			setStrippingGasRate(old.getStrippingGasRate());
			setIncludeGlycolFlashTankSeparatorCd(old
					.getIncludeGlycolFlashTankSeparatorCd());
			setFlashTankOffGasStream(old.getFlashTankOffGasStream());
			setFlashVaporsRouted(old.getFlashVaporsRouted());
			setOperatingTemperature(old.getOperatingTemperature());
			setOperatingPressure(old.getOperatingPressure());
			setIsVesselHeatedCd(old.getIsVesselHeatedCd());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);

		setApplicationEUId(AbstractDAO.getInteger(rs, "application_eu_id"));
		setTemperatureOfWetGas(AbstractDAO.getInteger(rs,
				"temperature_of_wet_gas"));
		setPressureOfWetGas(AbstractDAO.getFloat(rs, "pressure_of_wet_gas"));
		setWaterContentOfWetGas(rs.getBigDecimal("water_content_of_wet_gas"));
		setFlowRateOfDryGas(AbstractDAO.getFloat(rs, "flow_rate_of_dry_gas"));
		setWaterContentOfDryGas(AbstractDAO.getFloat(rs,
				"water_content_of_dry_gas"));
		setManufactureNameOfGlycolCircPump(rs
				.getString("manufacture_name_of_glycol_circulation_pump"));
		setModelNameAndNoOfGlycolCircPump(rs
				.getString("model_name_and_number_of_glycol_circulation_pump"));
		setTypeOfGlycolCirculationPumpCd(rs
				.getString("type_of_glycol_circulation_pump_cd"));
		setPumpVolumeRatio(AbstractDAO.getFloat(rs, "pump_volume_ratio"));
		setMaxLeanGlycolCirculationRate(rs.getBigDecimal("max_lean_glycol_circulation_rate"));
		setActualLeanGlycolCirculationRate(rs.getBigDecimal("actual_lean_glycol_circulation_rate"));
		setSourceOfMotiveGasForPump(rs
				.getString("source_of_motive_gas_for_pump"));
		setAdditionalGasStrippingCd(rs.getString("additional_gas_stripping_cd"));
		setSourceOfStrippingGasCd(rs.getString("source_of_stripping_gas_cd"));
		setStrippingGasRate(AbstractDAO.getFloat(rs, "stripping_gas_rate"));
		setIncludeGlycolFlashTankSeparatorCd(rs
				.getString("include_glycol_flash_tank_separator_cd"));
		setFlashTankOffGasStream(AbstractDAO.getFloat(rs,
				"flash_tank_off_gas_stream"));
		setFlashVaporsRouted(rs.getString("flash_vapors_routed"));
		setOperatingTemperature(AbstractDAO.getInteger(rs,
				"operating_temperature"));
		setOperatingPressure(AbstractDAO.getFloat(rs, "operating_pressure"));
		setIsVesselHeatedCd(rs.getString("is_vessel_heated_cd"));
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
		requiredFieldTemperatureOfWetGas();
		requiredFieldPressureOfWetGas();
		requiredFieldFlowRateOfDryGas();
		requiredFieldWaterContentOfDryGas();
		requiredFieldManufactureNameOfGlycolCircPump();
		requiredFieldModelNameAndNoOfGlycolCircPump();
		requiredFieldTypeOfGlycolCirculationPumpCd();
		requiredFieldPumpVolumeRatio();
		requiredFieldMaxLeanGlycolCirculationRate();
		requiredFieldActualLeanGlycolCirculationRate();
		requiredFieldSourceOfMotiveGasForPump();
		requiredFieldAdditionalGasStrippingCd();
		requiredFieldSourceOfStrippingGasCd();
		requiredFieldStrippingGasRate();
		requiredFieldIncludeGlycolFlashTankSeparatorCd();
		requiredFieldFlashTankOffGasStream();
		requiredFieldFlashVaporsRouted();
		requiredFieldOperatingTemperature();
		requiredFieldOperatingPressure();
		requiredFieldIsVesselHeatedCd();
	}

	public void validateRanges() {
		checkRangeValues(this.temperatureOfWetGas, new Integer(1), new Integer(
				Integer.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "temperatureOfWetGas",
				"Temperature of Wet Gas (F)");

		checkRangeValues(this.pressureOfWetGas, 0.01f, Float.MAX_VALUE,
				PAGE_VIEW_ID_PREFIX + "pressureOfWetGas",
				"Pressure of Wet Gas (psig)");

		checkRangeValues(this.waterContentOfWetGas, new BigDecimal("0.01"),
				new BigDecimal(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX
						+ "waterContentOfWetGas",
				"Water Content of Wet Gas (lbs H2O/MMscf)");

		checkRangeValues(this.flowRateOfDryGas, 0.01f, Float.MAX_VALUE,
				PAGE_VIEW_ID_PREFIX + "flowRateOfDryGas",
				"Flow Rate of Dry Gas (MMscfd)");

		checkRangeValues(this.waterContentOfDryGas, 0.01f, Float.MAX_VALUE,
				PAGE_VIEW_ID_PREFIX + "waterContentOfDryGas",
				"Water Content of Dry Gas (lbs H2O/MMscf)");

		if (!this.isGasClycolCirculationPump())
			return;

		checkRangeValues(this.pumpVolumeRatio, 0.01f, Float.MAX_VALUE,
				PAGE_VIEW_ID_PREFIX + "pumpVolumeRatio",
				"Pump Volume Ratio (acfm/gpm)");

		checkRangeValues(this.maxLeanGlycolCirculationRate, new BigDecimal("0.01"),
				new BigDecimal(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX
						+ "maxLeanGlycolCirculationRate",
				"Maximum LEAN Glycol Circulation Rate (gallons/minute)");

		checkRangeValues(this.actualLeanGlycolCirculationRate, new BigDecimal("0.01"),
				new BigDecimal(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX
						+ "actualLeanGlycolCirculationRate",
				"Actual LEAN Glycol Circulation Rate (gallons/minute)");

		if (checkAdditionalGasStripping()) {
			checkRangeValues(this.strippingGasRate, 0.01f, Float.MAX_VALUE,
					PAGE_VIEW_ID_PREFIX + "strippingGasRate",
					"Stripping Gas Rate (scf/minute)");
		}

		if (!checkIncludeGlycolFlashTankSeparator())
			return;

		checkRangeValues(this.flashTankOffGasStream, 0.01f, Float.MAX_VALUE,
				PAGE_VIEW_ID_PREFIX + "flashTankOffGasStream",
				"Flash Tank Off Gas Stream (scf/hr)");

		checkRangeValues(this.operatingPressure, 0.01f, Float.MAX_VALUE,
				PAGE_VIEW_ID_PREFIX + "operatingPressure",
				"Operating Pressure (psig)");

	}

	private void requiredFieldTemperatureOfWetGas() {
		requiredField(this.temperatureOfWetGas, PAGE_VIEW_ID_PREFIX
				+ "temperatureOfWetGas", "Temperature of Wet Gas (F)",
				"temperatureOfWetGas");
	}

	private void requiredFieldPressureOfWetGas() {
		requiredField(this.pressureOfWetGas, PAGE_VIEW_ID_PREFIX
				+ "pressureOfWetGas", "Pressure of Wet Gas (psig)",
				"pressureOfWetGas");
	}

	private void requiredFieldFlowRateOfDryGas() {
		requiredField(this.flowRateOfDryGas, PAGE_VIEW_ID_PREFIX
				+ "flowRateOfDryGas", "Flow Rate of Dry Gas (MMscfd)",
				"flowRateOfDryGas");
	}

	private void requiredFieldWaterContentOfDryGas() {
		requiredField(this.waterContentOfDryGas, PAGE_VIEW_ID_PREFIX
				+ "waterContentOfDryGas",
				"Water Content of Dry Gas (lbs H2O/MMscf)",
				"waterContentOfDryGas");
	}

	private void requiredFieldManufactureNameOfGlycolCircPump() {
		requiredField(this.manufactureNameOfGlycolCircPump, PAGE_VIEW_ID_PREFIX
				+ "manufactureNameOfGlycolCircPump",
				"Manufacturer Name of Glycol Circulation Pump ",
				"manufactureNameOfGlycolCircPump");
	}

	private void requiredFieldModelNameAndNoOfGlycolCircPump() {
		requiredField(this.modelNameAndNoOfGlycolCircPump, PAGE_VIEW_ID_PREFIX
				+ "modelNameAndNoOfGlycolCircPump",
				"Model Name and Number of Glycol Circulation Pump",
				"modelNameAndNoOfGlycolCircPump");
	}

	private void requiredFieldTypeOfGlycolCirculationPumpCd() {
		requiredField(this.typeOfGlycolCirculationPumpCd, PAGE_VIEW_ID_PREFIX
				+ "typeOfGlycolCirculationPumpCd",
				"Type of Glycol Circulation Pump",
				"typeOfGlycolCirculationPumpCd");
	}

	private void requiredFieldPumpVolumeRatio() {
		if (!this.isGasClycolCirculationPump())
			return;

		requiredField(this.pumpVolumeRatio, PAGE_VIEW_ID_PREFIX
				+ "pumpVolumeRatio", "Pump Volume Ratio (acfm/gpm)",
				"pumpVolumeRatio");
	}

	private void requiredFieldMaxLeanGlycolCirculationRate() {
		if (!this.isGasClycolCirculationPump())
			return;

		requiredField(this.maxLeanGlycolCirculationRate, PAGE_VIEW_ID_PREFIX
				+ "maxLeanGlycolCirculationRate",
				"Maximum LEAN Glycol Circulation Rate (gallons/minute)",
				"maxLeanGlycolCirculationRate");
	}

	private void requiredFieldActualLeanGlycolCirculationRate() {
		if (!this.isGasClycolCirculationPump())
			return;

		requiredField(this.actualLeanGlycolCirculationRate, PAGE_VIEW_ID_PREFIX
				+ "actualLeanGlycolCirculationRate",
				"Actual LEAN Glycol Circulation Rate (gallons/minute)",
				"actualLeanGlycolCirculationRate");
	}

	private void requiredFieldSourceOfMotiveGasForPump() {
		if (!this.isGasClycolCirculationPump())
			return;

		requiredField(this.sourceOfMotiveGasForPump, PAGE_VIEW_ID_PREFIX
				+ "sourceOfMotiveGasForPump", "Source of Motive Gas for Pump",
				"sourceOfMotiveGasForPump");
	}

	private void requiredFieldAdditionalGasStrippingCd() {
		if (!this.isGasClycolCirculationPump())
			return;

		requiredField(this.additionalGasStrippingCd, PAGE_VIEW_ID_PREFIX
				+ "additionalGasStrippingCd", "Additional Gas Stripping",
				"additionalGasStrippingCd");
	}

	private void requiredFieldSourceOfStrippingGasCd() {
		if (!checkAdditionalGasStripping())
			return;

		requiredField(this.sourceOfStrippingGasCd, PAGE_VIEW_ID_PREFIX
				+ "sourceOfStrippingGasCd", "Source of Stripping Gas",
				"sourceOfStrippingGasCd");
	}

	private void requiredFieldStrippingGasRate() {
		if (!checkAdditionalGasStripping())
			return;

		requiredField(this.strippingGasRate, PAGE_VIEW_ID_PREFIX
				+ "strippingGasRate", "Stripping Gas Rate (scf/minute)",
				"strippingGasRate");
	}

	private void requiredFieldIncludeGlycolFlashTankSeparatorCd() {
		if (!this.isGasClycolCirculationPump())
			return;

		requiredField(this.includeGlycolFlashTankSeparatorCd,
				PAGE_VIEW_ID_PREFIX + "includeGlycolFlashTankSeparatorCd",
				"Include Glycol Flash Tank/Separator",
				"includeGlycolFlashTankSeparatorCd");
	}

	private void requiredFieldFlashTankOffGasStream() {
		if (!checkIncludeGlycolFlashTankSeparator())
			return;

		requiredField(this.flashTankOffGasStream, PAGE_VIEW_ID_PREFIX
				+ "flashTankOffGasStream",
				"Flash Tank Off Gas Stream (scf/hr)", "flashTankOffGasStream");
	}

	private void requiredFieldFlashVaporsRouted() {
		if (!checkIncludeGlycolFlashTankSeparator())
			return;

		requiredField(this.flashVaporsRouted, PAGE_VIEW_ID_PREFIX
				+ "flashVaporsRouted", "Where are Flash vapors Routed",
				"flashVaporsRouted");
	}

	private void requiredFieldOperatingTemperature() {
		if (!checkIncludeGlycolFlashTankSeparator())
			return;

		requiredField(this.operatingTemperature, PAGE_VIEW_ID_PREFIX
				+ "operatingTemperature", "Operating Temperature (F)",
				"operatingTemperature");
	}

	private void requiredFieldOperatingPressure() {
		if (!checkIncludeGlycolFlashTankSeparator())
			return;

		requiredField(this.operatingPressure, PAGE_VIEW_ID_PREFIX
				+ "operatingPressure", "Operating Pressure (psig)",
				"operatingPressure");
	}

	private void requiredFieldIsVesselHeatedCd() {
		if (!checkIncludeGlycolFlashTankSeparator())
			return;

		requiredField(this.isVesselHeatedCd, PAGE_VIEW_ID_PREFIX
				+ "isVesselHeatedCd", "Is vessel heated", "isVesselHeatedCd");

	}

	private boolean checkAdditionalGasStripping() {
		return !Utility.isNullOrEmpty(this.additionalGasStrippingCd)
				&& this.additionalGasStrippingCd.equalsIgnoreCase("Y");
	}

	private boolean checkIncludeGlycolFlashTankSeparator() {
		return !Utility.isNullOrEmpty(this.includeGlycolFlashTankSeparatorCd)
				&& this.includeGlycolFlashTankSeparatorCd.equalsIgnoreCase("Y");
	}

	private void resetClycolCirculationPumpRelatedFields() {
		this.pumpVolumeRatio = null;
		this.maxLeanGlycolCirculationRate = null;
		this.actualLeanGlycolCirculationRate = null;
		this.sourceOfMotiveGasForPump = null;
		this.additionalGasStrippingCd = null;
		this.includeGlycolFlashTankSeparatorCd = null;

		resetAdditionalGasStrippingRelatedFields();
		resetIncludeGlycolFlashRelatedFields();
	}

	private void resetAdditionalGasStrippingRelatedFields() {
		this.sourceOfStrippingGasCd = null;
		this.strippingGasRate = null;
	}

	private void resetIncludeGlycolFlashRelatedFields() {
		this.flashTankOffGasStream = null;
		this.flashVaporsRouted = null;
		this.operatingTemperature = null;
		this.operatingPressure = null;
		this.isVesselHeatedCd = null;
	}

}