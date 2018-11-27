package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import oracle.cabo.data.jbo.servlet.event.SetRegionEventHandler;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.AppEUCirculationPumpTypeDef;
import us.oh.state.epa.stars2.def.AppEUFUGEmissionTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeHMA extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeHMA:";
	private String manufactureName;
	private String modelNameAndNum;
	private Float fuelSulfurContent;
	private String hmaSulfurUnitsCd;
	private Float fuelConsumption;
	private String hmafuelConsUnitsCd;
	private Float fuelGasHeatingVal;
	private String fuelGasHeatingUnitsCd;
	private Float stackVolumetricFlow;
	private String plantProcessRecycledAsphaltCd;
	private Integer maxRapPercent;

	public NSRApplicationEUTypeHMA() {
		super();
	}

	public NSRApplicationEUTypeHMA(NSRApplicationEUTypeHMA old) {
		super(old);
		if (old != null) {
			setManufactureName(old.getManufactureName());
			setModelNameAndNum(old.getModelNameAndNum());
			setFuelSulfurContent(old.getFuelSulfurContent());
			setHmaSulfurUnitsCd(old.getHmaSulfurUnitsCd());
			setFuelConsumption(old.fuelConsumption);
			setHmafuelConsUnitsCd(old.getHmafuelConsUnitsCd());
			setFuelGasHeatingVal(old.getFuelGasHeatingVal());
			setFuelGasHeatingUnitsCd(old.getFuelGasHeatingUnitsCd());
			setStackVolumetricFlow(old.getStackVolumetricFlow());
			setPlantProcessRecycledAsphaltCd(old
					.getPlantProcessRecycledAsphaltCd());
			setMaxRapPercent(old.getMaxRapPercent());

		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setManufactureName(rs.getString("manufacture"));
		setModelNameAndNum(rs.getString("model_number"));
		setFuelSulfurContent(AbstractDAO.getFloat(rs, "fuel_sulfur"));
		setHmaSulfurUnitsCd(rs.getString("units_fuel_sulfur_cd"));
		setFuelConsumption(AbstractDAO.getFloat(rs, "fuel_consumption"));
		setHmafuelConsUnitsCd(rs.getString("units_fuel_consumption_cd"));
		setFuelGasHeatingVal(AbstractDAO.getFloat(rs, "fuel_gas_heating"));
		setFuelGasHeatingUnitsCd(rs.getString("units_fuel_gas_heating_cd"));
		setStackVolumetricFlow(AbstractDAO
				.getFloat(rs, "stack_volumetric_flow"));
		setPlantProcessRecycledAsphaltCd(rs
				.getString("plant_processes_recycled_asphalt_cd"));
		setMaxRapPercent(AbstractDAO.getInteger(rs, "max_percent_rap"));

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((manufactureName == null) ? 0 : manufactureName.hashCode());
		result = prime
				* result
				+ ((fuelSulfurContent == null) ? 0 : fuelSulfurContent
						.hashCode());
		result = prime
				* result
				+ ((hmaSulfurUnitsCd == null) ? 0 : hmaSulfurUnitsCd.hashCode());
		result = prime * result
				+ ((fuelConsumption == null) ? 0 : fuelConsumption.hashCode());
		result = prime
				* result
				+ ((hmafuelConsUnitsCd == null) ? 0 : hmafuelConsUnitsCd
						.hashCode());
		result = prime
				* result
				+ ((fuelGasHeatingVal == null) ? 0 : fuelGasHeatingVal
						.hashCode());
		result = prime
				* result
				+ ((fuelGasHeatingUnitsCd == null) ? 0 : fuelGasHeatingUnitsCd
						.hashCode());
		result = prime
				* result
				+ ((stackVolumetricFlow == null) ? 0 : stackVolumetricFlow
						.hashCode());
		result = prime * result
				+ ((maxRapPercent == null) ? 0 : maxRapPercent.hashCode());
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
		final NSRApplicationEUTypeHMA other = (NSRApplicationEUTypeHMA) obj;
		if (manufactureName == null) {
			if (other.manufactureName != null)
				return false;
		} else if (!manufactureName.equals(other.manufactureName))
			return false;
		if (fuelSulfurContent == null) {
			if (other.fuelSulfurContent != null)
				return false;
		} else if (!fuelSulfurContent.equals(other.fuelSulfurContent))
			return false;
		if (hmaSulfurUnitsCd == null) {
			if (other.hmaSulfurUnitsCd != null)
				return false;
		} else if (!hmaSulfurUnitsCd.equals(other.hmaSulfurUnitsCd))
			return false;
		if (fuelConsumption == null) {
			if (other.fuelConsumption != null)
				return false;
		} else if (!fuelConsumption.equals(other.fuelConsumption))
			return false;
		if (hmafuelConsUnitsCd == null) {
			if (other.hmafuelConsUnitsCd != null)
				return false;
		} else if (!hmafuelConsUnitsCd.equals(other.hmafuelConsUnitsCd))
			return false;
		if (fuelGasHeatingVal == null) {
			if (other.fuelGasHeatingVal != null)
				return false;
		} else if (!fuelGasHeatingVal.equals(other.fuelGasHeatingVal))
			return false;
		if (fuelGasHeatingUnitsCd == null) {
			if (other.fuelGasHeatingUnitsCd != null)
				return false;
		} else if (!fuelGasHeatingUnitsCd.equals(other.fuelGasHeatingUnitsCd))
			return false;
		if (stackVolumetricFlow == null) {
			if (other.stackVolumetricFlow != null)
				return false;
		} else if (!stackVolumetricFlow.equals(other.stackVolumetricFlow))
			return false;
		if (maxRapPercent == null) {
			if (other.maxRapPercent != null)
				return false;
		} else if (!maxRapPercent.equals(other.maxRapPercent))
			return false;
		return true;
	}

	public String getManufactureName() {
		return manufactureName;
	}

	public void setManufactureName(String manufactureName) {
		this.manufactureName = manufactureName;
	}

	public String getModelNameAndNum() {
		return modelNameAndNum;
	}

	public void setModelNameAndNum(String modelNameAndNum) {
		this.modelNameAndNum = modelNameAndNum;
	}

	public Float getFuelSulfurContent() {
		return fuelSulfurContent;
	}

	public void setFuelSulfurContent(Float fuelSulfurContent) {
		this.fuelSulfurContent = fuelSulfurContent;
	}

	public String getHmaSulfurUnitsCd() {
		return hmaSulfurUnitsCd;
	}

	public void setHmaSulfurUnitsCd(String hmaSulfurUnitsCd) {
		this.hmaSulfurUnitsCd = hmaSulfurUnitsCd;
	}

	public Float getFuelConsumption() {
		return fuelConsumption;
	}

	public void setFuelConsumption(Float fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}

	public String getHmafuelConsUnitsCd() {
		return hmafuelConsUnitsCd;
	}

	public void setHmafuelConsUnitsCd(String hmafuelConsUnitsCd) {
		this.hmafuelConsUnitsCd = hmafuelConsUnitsCd;
	}

	public Float getFuelGasHeatingVal() {
		return fuelGasHeatingVal;
	}

	public void setFuelGasHeatingVal(Float fuelGasHeatingVal) {
		this.fuelGasHeatingVal = fuelGasHeatingVal;
	}

	public String getFuelGasHeatingUnitsCd() {
		return fuelGasHeatingUnitsCd;
	}

	public void setFuelGasHeatingUnitsCd(String fuelGasHeatingUnitsCd) {
		this.fuelGasHeatingUnitsCd = fuelGasHeatingUnitsCd;
	}

	public Float getStackVolumetricFlow() {
		return stackVolumetricFlow;
	}

	public void setStackVolumetricFlow(Float stackVolumetricFlow) {
		this.stackVolumetricFlow = stackVolumetricFlow;
	}

	public Integer getMaxRapPercent() {
		return maxRapPercent;
	}

	public void setMaxRapPercent(Integer maxRapPercent) {
		this.maxRapPercent = maxRapPercent;
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

		requiredField(this.fuelSulfurContent, PAGE_VIEW_ID_PREFIX
				+ "fuelSulfurContent", "Fuel Sulfur Content",
				"fuelSulfurContent");

		requiredField(this.hmaSulfurUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "hmaSulfurUnitsCd", "Fuel Sulfur Content Units",
				"hmaSulfurUnitsCd");

		requiredField(this.fuelConsumption, PAGE_VIEW_ID_PREFIX
				+ "fuelConsumption", "Fuel Consumption", "fuelConsumption");

		requiredField(this.hmafuelConsUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "hmafuelConsUnitsCd", "Fuel Consumption Units",
				"hmafuelConsUnitsCd");
		requiredField(this.fuelGasHeatingVal, PAGE_VIEW_ID_PREFIX
				+ "fuelGasHeatingVal", "Fuel Gas Heating Value",
				"fuelGasHeatingVal");
		requiredField(this.fuelGasHeatingUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "fuelGasHeatingUnitsCd", "Fuel Gas Heating Value Units",
				"fuelGasHeatingUnitsCd");
		requiredField(this.stackVolumetricFlow, PAGE_VIEW_ID_PREFIX
				+ "stackVolumetricFlow", "Stack Volumetric Flow (dscfm)",
				"stackVolumetricFlow");
		requiredField(this.plantProcessRecycledAsphaltCd, PAGE_VIEW_ID_PREFIX
				+ "plantProcessRecycledAsphaltCd",
				"Plant Processes Recycled Asphalt",
				"plantProcessRecycledAsphaltCd");
		if (isPlantProcessRecycledAsphaltCdSelected()) {
			requiredField(this.maxRapPercent, PAGE_VIEW_ID_PREFIX
					+ "maxRapPercent", "Maximum Percent RAP (%)",
					"maxRapPercent");
		}
	}

	public void validateRanges() {
		checkRangeValues(fuelSulfurContent, new Float(0.00), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelSulfurContent",
				"Fuel Sulfur Content");
		checkRangeValues(fuelConsumption, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelConsumption",
				"Fuel Consumption");
		checkRangeValues(fuelGasHeatingVal, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelGasHeatingVal",
				"Fuel Gas Heating Value");
		checkRangeValues(stackVolumetricFlow, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "stackVolumetricFlow",
				"Stack Volumetric Flow (dscfm)");
		if (isPlantProcessRecycledAsphaltCdSelected()) {
			checkRangeValues(maxRapPercent, new Integer(0), new Integer(100),
					PAGE_VIEW_ID_PREFIX + "maxRapPercent",
					"Maximum Percent RAP (%)");
		}
	}

	public String getPlantProcessRecycledAsphaltCd() {
		return plantProcessRecycledAsphaltCd;
	}

	public void setPlantProcessRecycledAsphaltCd(
			String plantProcessRecycledAsphaltCd) {
		this.plantProcessRecycledAsphaltCd = plantProcessRecycledAsphaltCd;
	}

	public boolean isPlantProcessRecycledAsphalt() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(plantProcessRecycledAsphaltCd)
				&& this.plantProcessRecycledAsphaltCd.equals("Y");
		return ret;
	}

	public boolean isPlantProcessRecycledAsphaltCdSelected() {
		boolean plantProcessRecycledAsphaltCdSelected = false;
		if (getPlantProcessRecycledAsphaltCd() != null
				&& getPlantProcessRecycledAsphaltCd().equals("Y"))
			plantProcessRecycledAsphaltCdSelected = true;
		return plantProcessRecycledAsphaltCdSelected;
	}
}
