package us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;
import us.wy.state.deq.impact.def.EquipTypeDef;
import us.wy.state.deq.impact.def.FugitiveComponentDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeLiquidDef;

public class EmissionUnitType extends BaseDB {
	
	private static final long serialVersionUID = 6410330128798792070L;

	private Integer emuId;
	private String emissionUnitTypeCd;

	// Shared by more than one type.
	private String unitCd;
	private String serialNumber;
	private Timestamp serialNumberEffectiveDate;
	private String manufacturerName;
	private String modelNameNumber;
	private Integer maxProductionRate;
	private String powerSource;
	private Long maxAnnualThroughput;
	private BigDecimal pilotGasVolume;
	private BigDecimal heatInputRating;
	private BigDecimal designCapacity;
	private String primaryFuelType;
	private String secondaryFuelType;
	private BigDecimal maxDesignCapacity;
	private BigDecimal minDesignCapacity;
	//AZ specific	
	private BigDecimal maximumAnnualThroughput;
	private Integer maxAnnualUsage;

	// Engine specific.
	private BigDecimal namePlateRating;
	private String engineType;
	private BigDecimal siteRating;
	private String siteRatingUnitCd;
	private String typeOfUse;

	// Boiler specific.

	// Hot mix asphalt specific.
	private String plantType;
	private BigDecimal maxBurnerDesignRate;
	private String fuelType;

	// Concrete batch cement mixer specific.
	private String batchingType;

	// Loading unloading specific.
	private String materialType;
	private String materialDescription;

	// Storage tanks specific.
	private String materialTypeStored;
	private String liquidMaterialTypeStored;
	private String materialTypeStoredDesc;
	private Integer capacity;
	private String capacityUnit;
	private BigDecimal maxThroughput;
	private String submergedFillPipeFlag;
	private String tankLocation;

	// Crushing screening handler specific.
	private String unitType;

	// Flare specific.

	// Incinerator specific.
	private String incineratorType;
	private String burnerSystem;
	private Integer primaryChamberOpTemp;
	private Integer secondaryChamberOpTemp;

	// Dehydration specific.
	private String dehydrationType;

	// Heaters chillers specific.
	private String firingType;
	private Integer fuelHeatContent;
	private String fuelHeatContentUnitsCd;

	// Pneumatic equipment specific.
	private String equipmentType;
	private BigDecimal bleedRate;
	private BigDecimal gasConsumptionRate;
	private Short eqptCount;

	// Blow down venting specific.
	private String eventType;

	// Cracking coking unit specific.
	private Integer chargeRate;

	// Cooling tower specific.
	private Float driftRate;
	private BigDecimal dissolvedSolidsTotal;

	// Amine unit specific.

	// Sulfur recovery unit specific.

	// Tail gas treatment unit specific.

	// Process vents specific.

	// Calciner kiln dryer specific.

	// Acid plant prill tower specific.
	private String producedMaterialType;

	// Electric generating unit specific.

	// Spray booth electroplating specific.
	private String unitDesc;

	// Separators treaters specific.
	private String vesselType;
	private boolean vesselHeated;

	// Bakery specific.
	private String nameAndTypeOfMaterial;

	// Abrasive Blasting specific.
	private String applicationMethod;
	private String substrateBlasted;
	private String substrateRemoved;
	private String concentrationOfLeadPct;
	private String blastMediaCARBCertifiedFlag;
	private Integer maxNumberOfTimesBlastMediaReclaimedForReuse;
	private BigDecimal maxAnnualUsageAbs;
	private String substrateRemovedOther;
	private String substrateBlastedOther;

	// Welding specific.
	private String weldingProcess;
	private Integer maxAmtOfElectrodeConsumed;
	
	// Air Curtain Burner.
	private Integer maxTimeOperated;
	
	// Deep Fat Frying.
	private String unitTypeOther;
	
	// Tank, Open.
	private String platingLineFlag;
	private String platingType;
	private String platingTypeOther;
	private String metalType;
	private String metalTypeOther;
	private Integer concentrationPct;

	// Printing Line.
	private String pressType;
	private String pressTypeOther;
	private String substrateFeedMethod;
	private Integer impressionArea;
	private Integer maxPctVOC;
	
	// Polyester Resin.
	private Integer maxPctStyrene;
	
	//Sterilizer
	private String facilityType;

	//Dry Cleaning Type
	private String dryCleaningType;
	private String operationType;

	//Ozone Generator
	private Integer maxOzoneGenerationRate;
	
	//SVC
	private String equipmentTypeOther;
	private String solventType;
	
	//Tire Manufacturing
	private Integer maxAmtOfRubberRemoved;
	
	//Wood Working Equipment
	private BigDecimal maxAnnualWoodDustGenerated;
	private String powerRatingFlag;
	
	//Ordnance Detonation/Explosive Testing 
	private String typeOfExplosive;
	private String typeOfExplosiveOther;
	private Integer maxNumberOfRoundsDetonated;
	
	//Machining Equipment
	private Integer amtOfMaterialRemoved;
	
	//Water and Soil Remediation
	private String typeOfContaminantBeingTreated;
	private String otherTypeOfContaminantBeingTreated;
	private String contaminatedMaterial;
	private Integer VOCEmissionRate;
	
	//Spray Booth or Coating Line
	private String typeOfCoatingOperation;
	private String typeOfMaterialBeingCoated;
	private String typeOfMaterialBeingCoatedOther;
	private String typeOfProductBeingCoated;
	private String applicationMethodOther;

	// Must use pageViewId prefix for proper error display.
	private String pageViewId;
	
	private static final Integer MAX_VAL_INTEGER_FOR_EU=new Integer(999999);
	private static final BigDecimal MAX_VAL_FLOAT_FOR_EU = new BigDecimal("999999.99", new MathContext(8, RoundingMode.HALF_EVEN));
	private static final BigDecimal MIN_VAL_FLOAT_FOR_EU = new BigDecimal("0.01", new MathContext(2, RoundingMode.HALF_EVEN));
	private static final Integer MAX_VAL_PERCENTAGE=new Integer(100);
	private static final Short MAX_VAL_INTEGER_FOR_EU_COUNT = new Short((short) 999);
	private static final Integer MAX_VAL_INTEGER_FOR_FUG_COMPONENT = new Integer(999);

	//FUG: total count of components
	private List<Component> components = new ArrayList<Component>();
	private boolean fugitiveLeaks = false;
	
	
	public Integer getEmuId() {
		return emuId;
	}

	public void setEmuId(Integer emuId) {
		this.emuId = emuId;
	}

	public String getEmissionUnitTypeCd() {
		return emissionUnitTypeCd;
	}

	public void setEmissionUnitTypeCd(String emissionUnitTypeCd) {
		this.emissionUnitTypeCd = emissionUnitTypeCd;
	}

	public String getUnitCd() {
		return unitCd;
	}

	public void setUnitCd(String units) {
		this.unitCd = units;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Timestamp getSerialNumberEffectiveDate() {
		return serialNumberEffectiveDate;
	}

	public void setSerialNumberEffectiveDate(Timestamp serialNumberEffectiveDate) {
		this.serialNumberEffectiveDate = serialNumberEffectiveDate;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getModelNameNumber() {
		return modelNameNumber;
	}

	public void setModelNameNumber(String modelNameNumber) {
		this.modelNameNumber = modelNameNumber;
	}

	public Integer getMaxProductionRate() {
		return maxProductionRate;
	}

	public void setMaxProductionRate(Integer maxProductionRate) {
		this.maxProductionRate = maxProductionRate;
	}

	public String getPowerSource() {
		return powerSource;
	}

	public void setPowerSource(String powerSource) {
		this.powerSource = powerSource;
	}

	public Long getMaxAnnualThroughput() {
		return maxAnnualThroughput;
	}

	public void setMaxAnnualThroughput(Long maxAnnualThroughput) {
		this.maxAnnualThroughput = maxAnnualThroughput;
	}

	public BigDecimal getPilotGasVolume() {
		return pilotGasVolume;
	}

	public void setPilotGasVolume(BigDecimal pilotGasVolume) {
		this.pilotGasVolume = pilotGasVolume;
	}

	public BigDecimal getHeatInputRating() {
		return heatInputRating;
	}

	public void setHeatInputRating(BigDecimal heatInputRating) {
		this.heatInputRating = heatInputRating;
	}

	public BigDecimal getDesignCapacity() {
		return designCapacity;
	}

	public void setDesignCapacity(BigDecimal designCapacity) {
		this.designCapacity = designCapacity;
	}

	public String getPrimaryFuelType() {
		return primaryFuelType;
	}

	public void setPrimaryFuelType(String primaryFuelType) {
		this.primaryFuelType = primaryFuelType;
	}

	public String getSecondaryFuelType() {
		return secondaryFuelType;
	}

	public void setSecondaryFuelType(String secondaryFuelType) {
		this.secondaryFuelType = secondaryFuelType;
	}

	public BigDecimal getNamePlateRating() {
		return namePlateRating;
	}

	public void setNamePlateRating(BigDecimal namePlateRating) {
		this.namePlateRating = namePlateRating;
	}

	public String getEngineType() {
		return engineType;
	}

	public void setTypeOfUse(String typeOfUse){
		this.typeOfUse=typeOfUse;
	}

	public String getTypeOfUse(){
		return typeOfUse;
	}
	
	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}

	public String getPlantType() {
		return plantType;
	}

	public void setPlantType(String plantType) {
		this.plantType = plantType;
	}

	public BigDecimal getMaxBurnerDesignRate() {
		return maxBurnerDesignRate;
	}

	public void setMaxBurnerDesignRate(BigDecimal maxBurnerDesignRate) {
		this.maxBurnerDesignRate = maxBurnerDesignRate;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public String getBatchingType() {
		return batchingType;
	}

	public void setBatchingType(String batchingType) {
		this.batchingType = batchingType;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getMaterialDescription() {
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}

	public String getMaterialTypeStored() {
		return materialTypeStored;
	}

	public void setMaterialTypeStored(String materialTypeStored) {
		this.materialTypeStored = materialTypeStored;
	}

	public String getMaterialTypeStoredDesc() {
		return materialTypeStoredDesc;
	}

	public void setMaterialTypeStoredDesc(String materialTypeStoredDesc) {
		this.materialTypeStoredDesc = materialTypeStoredDesc;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public BigDecimal getMaxThroughput() {
		return maxThroughput;
	}

	public void setMaxThroughput(BigDecimal maxThroughput) {
		this.maxThroughput = maxThroughput;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public BigDecimal getMaxDesignCapacity() {
		return maxDesignCapacity;
	}

	public void setMaxDesignCapacity(BigDecimal maxDesignCapacity) {
		this.maxDesignCapacity = maxDesignCapacity;
	}

	public BigDecimal getMinDesignCapacity() {
		return minDesignCapacity;
	}

	public void setMinDesignCapacity(BigDecimal minDesignCapacity) {
		this.minDesignCapacity = minDesignCapacity;
	}

	public String getIncineratorType() {
		return incineratorType;
	}

	public void setIncineratorType(String incineratorType) {
		this.incineratorType = incineratorType;
	}

	public String getBurnerSystem() {
		return burnerSystem;
	}

	public void setBurnerSystem(String burnerSystem) {
		this.burnerSystem = burnerSystem;
	}

	public Integer getPrimaryChamberOpTemp() {
		return primaryChamberOpTemp;
	}

	public void setPrimaryChamberOpTemp(Integer primaryChamberOpTemp) {
		this.primaryChamberOpTemp = primaryChamberOpTemp;
	}

	public Integer getSecondaryChamberOpTemp() {
		return secondaryChamberOpTemp;
	}

	public void setSecondaryChamberOpTemp(Integer secondaryChamberOpTemp) {
		this.secondaryChamberOpTemp = secondaryChamberOpTemp;
	}

	public String getDehydrationType() {
		return dehydrationType;
	}

	public void setDehydrationType(String dehydrationType) {
		this.dehydrationType = dehydrationType;
	}

	public String getFiringType() {
		return firingType;
	}

	public void setFiringType(String firingType) {
		this.firingType = firingType;
	}

	public Integer getFuelHeatContent() {
		return fuelHeatContent;
	}

	public void setFuelHeatContent(Integer fuelHeatContent) {
		this.fuelHeatContent = fuelHeatContent;
	}

	public String getFuelHeatContentUnitsCd() {
		return fuelHeatContentUnitsCd;
	}

	public void setFuelHeatContentUnitsCd(String fuelHeatContentUnitsCd) {
		this.fuelHeatContentUnitsCd = fuelHeatContentUnitsCd;
	}

	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
		if(this.equipmentType!=null){
			if(! equipmentType.equalsIgnoreCase("other")){
				setEquipmentTypeOther(null);
			}
		}
	}

	public BigDecimal getBleedRate() {
		return bleedRate;
	}

	public void setBleedRate(BigDecimal bleedRate) {
		this.bleedRate = bleedRate;
	}

	public BigDecimal getGasConsumptionRate() {
		return gasConsumptionRate;
	}

	public void setGasConsumptionRate(BigDecimal gasConsumptionRate) {
		this.gasConsumptionRate = gasConsumptionRate;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Integer getChargeRate() {
		return chargeRate;
	}

	public void setChargeRate(Integer chargeRate) {
		this.chargeRate = chargeRate;
	}

	public Float getDriftRate() {
		return driftRate;
	}

	public void setDriftRate(Float driftRate) {
		this.driftRate = driftRate;
	}

	public BigDecimal getDissolvedSolidsTotal() {
		return dissolvedSolidsTotal;
	}

	public void setDissolvedSolidsTotal(BigDecimal dissolvedSolidsTotal) {
		this.dissolvedSolidsTotal = dissolvedSolidsTotal;
	}

	public String getProducedMaterialType() {
		return producedMaterialType;
	}

	public void setProducedMaterialType(String producedMaterialType) {
		this.producedMaterialType = producedMaterialType;
	}

	public String getUnitDesc() {
		return unitDesc;
	}

	public void setUnitDesc(String unitDesc) {
		this.unitDesc = unitDesc;
	}

	public String getVesselType() {
		return vesselType;
	}

	public void setVesselType(String vesselType) {
		this.vesselType = vesselType;
	}

	public boolean isVesselHeated() {
		return vesselHeated;
	}

	public void setVesselHeated(boolean vesselHeated) {
		this.vesselHeated = vesselHeated;
	}

	public String getCapacityUnit() {
		return capacityUnit;
	}

	public void setCapacityUnit(String capacityUnit) {
		this.capacityUnit = capacityUnit;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setEmuId(AbstractDAO.getInteger(rs, "emu_id"));
			setUnitCd(rs.getString("units"));
			setCapacityUnit(rs.getString("capacity_unit"));
			setSerialNumber(rs.getString("serial_number"));
			setSerialNumberEffectiveDate(rs.getTimestamp("serial_number_effective_date"));
			setManufacturerName(rs.getString("manufacture_name"));
			setModelNameNumber(rs.getString("modelname_and_number"));
			setMaxProductionRate(AbstractDAO.getInteger(rs, "max_production_rate"));
			setPowerSource(rs.getString("power_source"));
			setMaxAnnualThroughput(rs.getLong("max_annual_throughput"));
			setPilotGasVolume(rs.getBigDecimal("pilot_gas_volume"));
			setHeatInputRating(rs.getBigDecimal("heat_input_rating"));
			setDesignCapacity(rs.getBigDecimal("design_capacity"));
			setPrimaryFuelType(rs.getString("primary_fuel_type"));
			setSecondaryFuelType(rs.getString("secondary_fuel_type"));
			setNamePlateRating(rs.getBigDecimal("name_plate_rating"));
			setEngineType(rs.getString("engine_type"));
			setPlantType(rs.getString("plant_type"));
			setMaxBurnerDesignRate(rs.getBigDecimal("max_burner_design_rate"));
			setFuelType(rs.getString("fuel_type"));
			setBatchingType(rs.getString("type_of_batching"));
			setMaterialType(rs.getString("type_of_material"));
			setMaterialDescription(rs.getString("material_description"));
			setMaterialTypeStored(rs.getString("type_of_material_stored"));
			setLiquidMaterialTypeStored(rs.getString("type_of_liquid_material_stored"));
			setMaterialTypeStoredDesc(rs.getString("material_stored_description"));
			setCapacity(AbstractDAO.getInteger(rs, "capacity"));
			setMaxThroughput(rs.getBigDecimal("max_throughput"));
			setUnitType(rs.getString("unit_type"));
			setMaxDesignCapacity(rs.getBigDecimal("max_design_capacity"));
			setMinDesignCapacity(rs.getBigDecimal("min_design_capacity"));
			setIncineratorType(rs.getString("incinerator_type"));
			setBurnerSystem(rs.getString("burner_system"));
			setPrimaryChamberOpTemp(AbstractDAO.getInteger(rs, "primary_chamber_operating_temp"));
			setSecondaryChamberOpTemp(AbstractDAO.getInteger(rs, "secondary_chamber_operating_temp"));
			setDehydrationType(rs.getString("dehydration_type"));
			setFiringType(rs.getString("firing_type"));
			setFuelHeatContent(AbstractDAO.getInteger(rs, "heat_content_of_fuel"));
			setFuelHeatContentUnitsCd(rs.getString("heat_content_of_fuel_units_cd"));
			setEquipmentType(rs.getString("type_of_equipment"));
			setBleedRate(rs.getBigDecimal("bleed_rate"));
			setGasConsumptionRate(rs.getBigDecimal("gas_consumption_rate"));
			setEventType(rs.getString("type_of_event"));
			setChargeRate(AbstractDAO.getInteger(rs, "charge_rate"));
			setDriftRate(rs.getFloat("drift_rate"));
			setDissolvedSolidsTotal(rs.getBigDecimal("total_dissolved_solids"));
			setProducedMaterialType(rs.getString("material_produced"));
			setUnitDesc(rs.getString("unit_description"));
			setVesselType(rs.getString("vessel_type"));
			setVesselHeated(AbstractDAO.translateIndicatorToBoolean(rs.getString("vessel_heated")));
			setSiteRating(rs.getBigDecimal("site_rating"));
			setSiteRatingUnitCd(rs.getString("site_rating_unit_cd"));
			setNameAndTypeOfMaterial(rs.getString("name_and_type_of_material"));
			setApplicationMethod(rs.getString("application_method"));
			setWeldingProcess(rs.getString("welding_process"));
			setMaxAmtOfElectrodeConsumed(AbstractDAO.getInteger(rs,"max_amt_of_electrode_consumed"));
			setMaxTimeOperated(AbstractDAO.getInteger(rs,"max_time_operated"));
			setSubstrateBlasted(rs.getString("substrate_blasted"));
			setSubstrateRemoved(rs.getString("substrate_removed"));
			setConcentrationOfLeadPct(rs.getString("concentration_of_lead_pct"));
			setMaxAnnualUsage(AbstractDAO.getInteger(rs,"max_annual_usage"));
			setMaxNumberOfTimesBlastMediaReclaimedForReuse(AbstractDAO.getInteger(rs,"max_number_of_times_blastmedia_reclaimed_for_reuse"));
			setBlastMediaCARBCertifiedFlag(rs.getString("is_blastmedia_carb_certified"));
			setSubstrateRemovedOther(rs.getString("substrate_removed_other"));
			setSubstrateBlastedOther(rs.getString("substrate_blasted_other"));
			setUnitTypeOther(rs.getString("unit_type_other"));
			setPlatingLineFlag(rs.getString("plating_line_flag"));
			setPlatingType(rs.getString("plating_type"));
			setPlatingTypeOther(rs.getString("plating_type_other"));
			setMetalType(rs.getString("metal_type"));
			setMetalTypeOther(rs.getString("metal_type_other"));
			setConcentrationPct(AbstractDAO.getInteger(rs,"concentration_pct"));
			setPressType(rs.getString("press_type"));
			setPressTypeOther(rs.getString("press_type_other"));
			setSubstrateFeedMethod(rs.getString("substrate_feed_method"));
			setImpressionArea(AbstractDAO.getInteger(rs,"impression_area"));
			setMaxPctVOC(AbstractDAO.getInteger(rs,"max_pct_VOC"));
			setMaxPctStyrene(AbstractDAO.getInteger(rs,"max_pct_styrene"));
			setMaxOzoneGenerationRate(AbstractDAO.getInteger(rs, "max_ozone_generation_rate"));
			setOperationType(rs.getString("type_of_operation"));
			setDryCleaningType(rs.getString("type_of_dry_cleaning")); 
			setFacilityType(rs.getString("facility_type"));
			setEquipmentTypeOther(rs.getString("equipment_type_other"));
			setSolventType(rs.getString("solvent_type"));
			setMaximumAnnualThroughput(rs.getBigDecimal("maximum_annual_throughput"));
			setMaxAnnualUsageAbs(rs.getBigDecimal("max_annual_usage_abs"));
			setMaxAmtOfRubberRemoved(AbstractDAO.getInteger(rs,"max_amt_of_rubber_removed"));
			setMaxAnnualWoodDustGenerated(rs.getBigDecimal("max_annual_wood_dust_generated"));
			setPowerRatingFlag(rs.getString("power_rating_flag"));
			setTypeOfExplosive(rs.getString("type_of_explosive"));
			setTypeOfExplosiveOther(rs.getString("type_of_explosive_other"));
			setMaxNumberOfRoundsDetonated(AbstractDAO.getInteger(rs,"max_number_of_rounds_detonated"));
			setAmtOfMaterialRemoved(AbstractDAO.getInteger(rs, "amt_of_material_removed"));
			setTypeOfContaminantBeingTreated(rs.getString("contaminant_type"));
			setOtherTypeOfContaminantBeingTreated(rs.getString("other_contaminant_type"));
			setContaminatedMaterial(rs.getString("contaminated_material"));
			setVOCEmissionRate(AbstractDAO.getInteger(rs, "voc_emission_rate"));
			setTypeOfCoatingOperation(rs.getString("type_of_coating_operation"));
			setTypeOfMaterialBeingCoated(rs.getString("type_of_material_being_coated"));
			setTypeOfMaterialBeingCoatedOther(rs.getString("type_of_material_being_coated_other"));
			setTypeOfProductBeingCoated(rs.getString("type_of_product_being_coated"));
			setApplicationMethodOther(rs.getString("application_method_other"));
			setTypeOfUse(rs.getString("type_of_use"));
			setEqptCount(AbstractDAO.getShort(rs, "eqpt_Count"));
			setSubmergedFillPipeFlag(rs.getString("submerged_fill_pipe_flag"));
			setTankLocation(rs.getString("tank_location"));
			setFugitiveLeaks(AbstractDAO.translateIndicatorToBoolean(rs.getString("fugitive_leaks_flag")));
		} catch (SQLException sqle) {
			logger.error("Required field error");
		} finally {
			newObject = false;
		}
	}

	public final ValidationMessage[] validate() {
		return validate(emissionUnitTypeCd);
	}

	public final ValidationMessage[] validate(String emissionUnitTypeCd) {

		clearValidationMessages();

		if (emissionUnitTypeCd == null) {
			return new ArrayList<ValidationMessage>(validationMessages.values()).toArray(new ValidationMessage[0]);
		}

		pageViewId = "emissionUnitType" + emissionUnitTypeCd + ":";

		// Add new EU types in alphabetical order! Validation methods should also
		// be added in alphabetical order.
		if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.ABS)) {
			validateABS();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.ACB)) {
			validateACB();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.AMN)) {
			validateAMN();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.APT)) {
			validateAPT();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.BAK)) {
			validateBAK();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.BGM)) {
			validateBGM();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.BOL)) {
			validateBOL();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.BVC)) {
			validateBVC();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CCU)) {
			validateCCU();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CKD)) {
			validateCKD();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CMX)) {
			validateCMX();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.COT)) {
			validateCOT();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CSH)) {
			validateCSH();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.CTW)) {
			validateCTW();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.DHY)) {
			validateDHY();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.DIS)) {
			validateDIS();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.DRY)) {
			validateDRY();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.EGU)) {
			validateEGU();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.ENG)) {
			validateENG();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.FAT)) {
			validateFAT();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.FLR)) {
			validateFLR();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.FUG)) {
			validateFUG();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.GIN)) {
			validateGIN();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.GRI)) {
			validateGRI();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.HET)) {
			validateHET();			
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.HMA)) {
			validateHMA();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.INC)) {
			validateINC();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.LUD)) {
			validateLUD();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.MAC)) {
			validateMAC();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.MAT)) {
			validateMAT();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.MET)) {
			validateMET();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.MIL)) {
			validateMIL();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.MIX)) {
			validateMIX();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.MLD)) {
			validateMLD();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.OEP)) {
			validateOEP();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.ORD)) {
			validateORD();
		} else if(emissionUnitTypeCd.equals(EmissionUnitTypeDef.OZG)){
			validateOZG();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.PAM)) {
			validatePAM();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.PEL)) {
			validatePEL();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.PNE)) {
			validatePNE();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.PRN)) {
			validatePRN();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.REM)) {
			validateREM();			
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.RES)) {
			validateRES();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SEB)) {
			validateSEB();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SEM)) {
			validateSEM();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SEP)) {
			validateSEP();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SRU)) {
			validateSRU();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SVC))	{
			validateSVC();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.STZ)) {
			validateSTZ();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.SVU)) {
			validateSVU();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.TAR)) {
			validateTAR();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.TGT)) {
			validateTGT();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.TIM)) {
			validateTIM();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.TKO)) {
			validateTKO();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.TNK)) {
			validateTNK();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.VNT)) {
			validateVNT();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.WEL)) {
			validateWEL();
		} else if (emissionUnitTypeCd.equals(EmissionUnitTypeDef.WWE)) {
			validateWWE();
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	private void validateABS() {

		requiredField(applicationMethod, pageViewId + "applicationMethod", "Application Method");
		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(substrateBlasted, pageViewId + "substrateBlasted", "Substrate being Blasted");
		requiredField(substrateRemoved, pageViewId + "substrateRemoved", "Substrate being Removed");
		if(substrateRemoved!=null){
			if(substrateRemoved.equalsIgnoreCase("Leaded Paint")){
				requiredField(concentrationOfLeadPct, pageViewId + "concentrationOfLeadPct", "Percentage Concentration Of Lead in Paint");

			}
		}
		
		if(substrateRemoved!=null){
			if(substrateRemoved.equalsIgnoreCase("other")){
				requiredField(substrateRemovedOther, pageViewId + "substrateRemovedOther", "Substrate Removed (Other)");

			}
		}

		if(substrateBlasted!=null){
			if(substrateBlasted.equalsIgnoreCase("other")){
				requiredField(substrateBlastedOther, pageViewId + "substrateBlastedOther", "Substrate Blasted (Other)");

			}
		}

		
		requiredField(blastMediaCARBCertifiedFlag, pageViewId + "blastMediaCARBCertifiedFlag", "Are Blast Media CARB Certified?");
		requiredField(maxAnnualUsageAbs, pageViewId + "maxAnnualUsageAbs", "Maximum Annual Usage (tons/yr)");

		checkRangeValues(maxAnnualUsageAbs, MIN_VAL_FLOAT_FOR_EU, MAX_VAL_FLOAT_FOR_EU,
				pageViewId + "maxAnnualUsageAbs", "Maximum Annual Throughput (tons/yr)");
		requiredField(maxNumberOfTimesBlastMediaReclaimedForReuse, pageViewId + "maxNumberOfTimesBlastMediaReclaimedForReuse", "Max number of times Blast Media is reclaimed for reuse? (times/yr)");
		checkRangeValues(maxNumberOfTimesBlastMediaReclaimedForReuse, new Integer(0),MAX_VAL_INTEGER_FOR_EU,
				pageViewId + "maxNumberOfTimesBlastMediaReclaimedForReuse", "maxNumberOfTimesBlastMediaReclaimedForReuse");

	}

	private void validateACB() {
		
		requiredField(fuelType, pageViewId + "fuelType", "Fuel Type");
		requiredField(maxTimeOperated, pageViewId + "maxTimeOperated", "Maximum Time Operated (hrs/yr)");

		checkRangeValues(maxTimeOperated, new Integer(0), new Integer(8760), pageViewId + "maxTimeOperated",
				"Maximum Time Operated (hrs/yr)");

	}

	private void validateAMN() {

		requiredField(designCapacity, pageViewId + "designCapacity",
				"Design Capacity");

		checkRangeValues(designCapacity, new BigDecimal("0.01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "designCapacity",
				"Design Capacity (MMscf/hr)");
	}

	private void validateAPT() {

		requiredField(producedMaterialType, pageViewId + "MaterialProduced",
				"Material Produced");
		requiredField(maxThroughput, pageViewId + "maximumThroughput",
				"Maximum Throughput");
		requiredField(unitCd, pageViewId + "Units", "Units");

		checkRangeValues(maxThroughput, new BigDecimal(1), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "maximumThroughput",
				"Maximum Throughput");

	}

	private void validateBAK() {

		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU, pageViewId + "maximumAnnualThroughput",
				"Maximum Annual Throughput (tons/yr)");

	}

	private void validateBGM() {
		
		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial",
				"Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput",
				"Maximum Annual Throughput");
	
		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU, MAX_VAL_FLOAT_FOR_EU, pageViewId + "maximumAnnualThroughput",
				"Maximum Annual Throughput (tons/yr)");
	}

	private void validateBOL() {
		
		requiredField(heatInputRating, pageViewId + "heatInputRating",
				"Heat Input Rating");
		requiredField(primaryFuelType, pageViewId + "primaryFuelType",
				"Primary Fuel Type");
		requiredField(secondaryFuelType, pageViewId + "secondaryFuelType",
				"Secondary Fuel Type");
		requiredField(modelNameNumber, pageViewId + "modelNameAndNumber",
				"Model Name and Number");

		checkRangeValues(heatInputRating, new BigDecimal("0.01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "heatInputRating",
				"Heat Input Rating");

	}
	
	private void validateBVC() {
		requiredField(eventType, pageViewId + "eventType", "Type of Event");
	}

	private void validateCCU() {

		requiredField(chargeRate, pageViewId + "chargeRate",
				"Maximum/Design Charge Rate");

	}

	private void validateCKD() {
		
		requiredField(unitType, pageViewId + "unitType", "Unit Type");
		requiredField(maxAnnualThroughput, pageViewId + "maximumThroughput", "Maximum Annual Throughput");
		requiredField(unitCd, pageViewId + "Units", "Units");
		requiredField(heatInputRating, pageViewId + "heatInputRating", "Heat Input Rating");
		requiredField(primaryFuelType, pageViewId + "primaryFuelType", "Primary Fuel Type");
		requiredField(secondaryFuelType, pageViewId + "secondaryFuelType", "Secondary Fuel Type");

		checkRangeValues(maxAnnualThroughput, new Long(1), new Long(Long.MAX_VALUE), pageViewId + "maximumThroughput",
				"Maximum Annual Throughput");
		checkRangeValues(heatInputRating, new BigDecimal("0.01"), new BigDecimal(Float.MAX_VALUE),
				pageViewId + "heatInputRating", "Heat Input Rating");

	}

	private void validateCMX() {
		
		requiredField(batchingType, pageViewId + "batchingType",
				"Type of Batching");
		requiredField(maxProductionRate, pageViewId + "maxProdRate",
				"Maximum Production Rate");
		requiredField(unitCd, pageViewId + "unitsCd", "Units");
		requiredField(powerSource, pageViewId + "powerSource", "Power Source");

		checkRangeValues(maxProductionRate, new Integer(1), new Integer(
				Integer.MAX_VALUE), pageViewId + "maxProdRate",
				"Maximum Production Rate");

	}

	
	public void validateCOT() {
		requiredField(typeOfCoatingOperation, pageViewId + "typeOfCoatingOperation", "Type of Coating Operation");
		requiredField(typeOfMaterialBeingCoated, pageViewId + "typeOfMaterialBeingCoated",
				"Type of Material Being Coated");
		if(typeOfMaterialBeingCoated!=null){
			if(typeOfMaterialBeingCoated.equalsIgnoreCase("other")){
				requiredField(typeOfMaterialBeingCoatedOther, pageViewId + "typeOfMaterialBeingCoatedOther", "Type of Material Being Coated (Other)");
			}
			
		}
		requiredField(typeOfProductBeingCoated, pageViewId + "typeOfProductBeingCoated",
				"Type of Product Being Coated");
		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name & Type of Material");
		requiredField(maxAnnualThroughput, pageViewId + "maxAnnualThroughput", "Maximum Throughput (gal/yr)");
		requiredField(maxPctVOC, pageViewId + "maxPctVOC", "Maximum % VOC Content");
		requiredField(applicationMethod, pageViewId + "applicationMethod", "Application Material");
		
		if (applicationMethod != null) {
			if (applicationMethod.equalsIgnoreCase("other")) {
				requiredField(applicationMethodOther, pageViewId + "applicationMethodOther",
						"Application Method (Other)");
			}
		}

		
		
		checkRangeValues(maxAnnualThroughput, new Long(1), new Long(999999), pageViewId + "maximumThroughput",
				"Maximum Throughput (gal/yr)");
		checkRangeValues(maxPctVOC, new Integer(0), new Integer(100), pageViewId + "maxPctVOC",
				"Maximum % VOC Content");
	}
	
	
	private void validateCSH() {
		
		requiredField(unitType, pageViewId + "unitType", "Type of Unit");

		requiredField(maxAnnualThroughput, pageViewId + "maxAnnualThroughput",
				"Maximum Annual Throughput");
		requiredField(unitCd, pageViewId + "unitCd", "Units");
		requiredField(modelNameNumber, pageViewId + "modelNameAndNumber",
				"Model Name and Number");

		checkRangeValues(maxAnnualThroughput, new Long(1), new Long(
				Long.MAX_VALUE), pageViewId + "maxAnnualThroughput",
				"Maximum Annual Throughput");

	}

	private void validateCTW() {

		requiredField(driftRate, pageViewId + "driftRate", "Drift Rate");
		requiredField(dissolvedSolidsTotal, pageViewId + "dissolvedSolids",
				"Total Dissolved Solids (ppm)");

		checkRangeValues(driftRate, new Float(0.0), new Float(100.0),
				pageViewId + "driftRate", "Drift Rate (%)");
		checkRangeValues(dissolvedSolidsTotal, new BigDecimal(1), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "dissolvedSolids",
				"Total Dissolved Solids (ppm)");

	}

	private void validateDHY() {
		
		requiredField(dehydrationType, pageViewId + "dehydrationType",
				"Dehydration Type");
		requiredField(designCapacity, pageViewId + "designCapacity",
				"Design Capacity");

		checkRangeValues(designCapacity, new BigDecimal("0.01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "designCapacity",
				"Design Capacity");

	}

	private void validateDIS() {

		requiredField(materialDescription, pageViewId + "materialDescription", "Material Description");
		requiredField(maxAnnualThroughput, pageViewId + "maxAnnualThroughput", "Maximum Annual Throughput");
		requiredField(unitCd, pageViewId + "unitCd", "Units");	

		checkRangeValues(maxAnnualThroughput, new Long(1), new Long(MAX_VAL_INTEGER_FOR_EU), pageViewId + "maxAnnualThroughput",
				"Maximum Annual Throughput");

	}

	private void validateDRY() {
	
		requiredField(dryCleaningType, pageViewId + "dryCleaningType", "Type of Dry Cleaning");
		requiredField(maxAnnualUsage, pageViewId + "maxAnnualUsage", "Maximum Annual Usage (gals/yr)");
		requiredField(operationType, pageViewId + "operationType", "Type of Opeartion");
		
		checkRangeValues(maxAnnualUsage, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "maxAnnualUsage",
				"Maximum Annual Usage (gals/yr)");
	}


	private void validateEGU() {
		
		requiredField(heatInputRating, pageViewId + "heatInputRating",
				"Heat Input Rating");
		requiredField(primaryFuelType, pageViewId + "primaryFuelType",
				"Primary Fuel Type");
		requiredField(secondaryFuelType, pageViewId + "secondaryFuelType",
				"Secondary Fuel Type");
		requiredField(manufacturerName, pageViewId + "manufacturerName",
				"Manufacturer Name");
		requiredField(modelNameNumber, pageViewId + "modelNameAndNumber",
				"Model Name and Number");
		requiredField(unitType, pageViewId + "unitType", "Unit Type");

		checkRangeValues(heatInputRating, new BigDecimal("0.01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "heatInputRating",
				"Heat Input Rating");
	}

	private void validateENG() {

		if(!isNamePlateRatingEmptyOrZero()){
			requiredField(unitCd, pageViewId + "namePlateRatingUnitsCd",
					"Name Plate Rating Units");
		}
		
		if(!isSiteRatingEmptyOrZero()){
			requiredField(siteRatingUnitCd, pageViewId + "siteRatingUnitsCd",
					"Site Rating Units");
		}
		
		requiredField(primaryFuelType, pageViewId + "primaryFuelType",
				"Primary Fuel Type");
		requiredField(secondaryFuelType, pageViewId + "secondaryFuelType",
				"Secondary Fuel Type");
		requiredField(modelNameNumber, pageViewId + "modelNameAndNumber",
				"Model Name and Number");
		requiredField(engineType, pageViewId + "engine", "Engine");
//		requiredField(typeOfUse, pageViewId + "typeOfUse",
//				"Type of Use");


	}

	private void validateFAT() {

		requiredField(unitType, pageViewId + "unitType", "Type of Unit");
		requiredField(maxProductionRate, pageViewId + "maxProdRate", "Maximum Production Rate (tons/yr)");
		
		checkRangeValues(maxProductionRate, new Integer(1),MAX_VAL_INTEGER_FOR_EU, pageViewId + "maxProductionRate",
				"Maximum Production Rate (tons/yr)");
	
		if(unitType!=null){
			if(unitType.equalsIgnoreCase("other")){
				requiredField(unitTypeOther, pageViewId + "unitTypeOther", "Type of Unit (Other)");

			}
		}

	}
	
	private void validateFLR() {
		
		requiredField(maxDesignCapacity, pageViewId + "maxDesignCapacity",
				"Maximum Design Capacity");
		requiredField(minDesignCapacity, pageViewId + "minDesignCapacity",
				"Minimum Design Capacity");
		requiredField(unitCd, pageViewId + "unitCd",
				"Maximum Design Capacity Units");
		requiredField(capacityUnit, pageViewId + "capacityUnit",
				"Minimum Design Capacity Units");

		checkRangeValues(maxDesignCapacity, new BigDecimal(".01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "maxDesignCapacity",
				"Maximum Design Capacity");
		checkRangeValues(minDesignCapacity, new BigDecimal(".01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "minDesignCapacity",
				"Minimum Design Capacity");

		if (unitCd != null && capacityUnit != null) {
			
			if (unitCd.equals(capacityUnit)) {
				if (minDesignCapacity != null && maxDesignCapacity != null) {
					if (minDesignCapacity.compareTo(maxDesignCapacity) > 0) {
						addValidationMessages(
								pageViewId + "maxDesignCapacity",
								"Maximum Design Capacity must be greater than Minimum Design Capacity.");
					}
				}
			}	
			else {
				// Add warning only if min/max design units aren't same.
				validationMessages.put(pageViewId, new ValidationMessage(pageViewId,
						"Units of Maximum Design Capacity and Minimum Design Capacity are not same",
						ValidationMessage.Severity.WARNING));
			}
		}


		// Optional
		checkRangeValues(pilotGasVolume, new BigDecimal("0"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "pilotGasVolume",
				"Pilot Gas Volume (scf/min)");

	}

	private void validateFUG() {
		// TODO Auto-generated method stub
		List<ValidationMessage> msgs = new ArrayList<ValidationMessage>();
		if (EmissionUnitTypeDef.FUG.equals(this.emissionUnitTypeCd) && this.fugitiveLeaks){
			
			for (Component c : this.components){
				msgs.addAll(Arrays.asList(c.validate()));
			}

			for (ValidationMessage msg:msgs){
				validationMessages.put(msg.getProperty(), msg);
			}
			
			if (!msgs.isEmpty()){
				return;
			} else {
				Integer count = 0;
				for (Component c : this.components){
					count = count + c.getGas() + c.getHeavyOil() + c.getLightOil() + c.getWater();
				}
				if (count == 0){
					validationMessages.put("Total Count of Component Table", 
						new ValidationMessage(pageViewId, "You must enter at least one component count in the table", ValidationMessage.Severity.ERROR));
				}
			}
		}
	}

	private void validateGIN() {
		
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");
	
		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU, MAX_VAL_FLOAT_FOR_EU, pageViewId + "maximumAnnualThroughput",
				"Maximum Annual Throughput (tons/yr)");

	}

	private void validateGRI() {
		
		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

	}

	private void validateHET() {
		
		requiredField(firingType, pageViewId + "firingType", "Firing Type");
		requiredField(heatInputRating, pageViewId + "heatInputRating",
				"Heat Input Rating");
		
		checkRangeValues(heatInputRating, new BigDecimal("0.01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "heatInputRating",
				"Heat Input Rating");

		requiredField(unitCd, pageViewId + "unitCd", "Heat Input Rating Units");
		requiredField(primaryFuelType, pageViewId + "primaryFuelType",
				"Primary Fuel Type");
		requiredField(secondaryFuelType, pageViewId + "secondaryFuelType",
				"Secondary Fuel Type");
		requiredField(fuelHeatContent, pageViewId + "fuelHeatContent",
				"Heat Content of Fuel");
		requiredField(fuelHeatContentUnitsCd, pageViewId + "fuelHeatContentUnitsCd",
				"Heat Content of Fuel Units");

	}

	private void validateHMA() {
		
		requiredField(plantType, pageViewId + "plantType", "Plant Type");
		requiredField(maxProductionRate, pageViewId + "maxProdRate",
				"Maximum Production Rate");
		requiredField(unitCd, pageViewId + "unitsCd", "Units");
		requiredField(maxBurnerDesignRate, pageViewId + "maxBurnerDesignRate",
				"Maximum Burner Design Rate");
		requiredField(powerSource, pageViewId + "powerSource", "Power Source");
		requiredField(fuelType, pageViewId + "fuelType", "Fuel Type");

		checkRangeValues(maxProductionRate, new Integer(1), new Integer(
				Integer.MAX_VALUE), pageViewId + "maxProdRate",
				"Maximum Production Rate");

		checkRangeValues(maxBurnerDesignRate, new BigDecimal("0.01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "maxBurnerDesignRate",
				"Maximum Burner Design Rate");

	}

	private void validateINC() {
		
		requiredField(incineratorType, pageViewId + "incineratorType",
				"Incinerator Type");
		requiredField(burnerSystem, pageViewId + "burnerSystem",
				"Burner System");

		requiredField(maxDesignCapacity, pageViewId + "maxDesignCapacity",
				"Maximum Design Capacity");
		requiredField(unitCd, pageViewId + "unitCd",
				"Maximum Design Capacity Units");
		requiredField(minDesignCapacity, pageViewId + "minDesignCapacity",
				"Minimum Design Capacity");
		requiredField(capacityUnit, pageViewId + "capacityUnit",
				"Minimum Design Capacity Units");

		checkRangeValues(maxDesignCapacity, new BigDecimal(".01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "maxDesignCapacity",
				"Maximum Design Capacity");
		checkRangeValues(minDesignCapacity, new BigDecimal(".01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "minDesignCapacity",
				"Minimum Design Capacity");

		if (unitCd != null && capacityUnit != null) {

			if (unitCd.equals(capacityUnit)) {
				if (minDesignCapacity != null && maxDesignCapacity != null) {
					if (minDesignCapacity.compareTo(maxDesignCapacity) > 0) {
						addValidationMessages(
								pageViewId + "maxDesignCapacity",
								"Maximum Design Capacity must be greater than Minimum Design Capacity.");
					}
				}
			}	
			else {
				// add only warning if min/max design units aren't same
				validationMessages.put(pageViewId, new ValidationMessage(pageViewId,
						"Units of Maximum Design Capacity and Minimum Design Capacity are not same",
						ValidationMessage.Severity.WARNING));
			}
		}
	

		// Optional
		checkRangeValues(pilotGasVolume, new BigDecimal("0"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "pilotGasVolume",
				"Pilot Gas Volume (scf/min)");

		if (!Utility.isNullOrZero(primaryChamberOpTemp)) {
			checkRangeValues(primaryChamberOpTemp, new Integer(1), new Integer(
					Integer.MAX_VALUE), pageViewId + "primaryChamberOpTemp",
					"Primary Chamber Operating Temp");
		}
		
		if (!Utility.isNullOrZero(secondaryChamberOpTemp)) {
			checkRangeValues(secondaryChamberOpTemp, new Integer(1),
					new Integer(Integer.MAX_VALUE), pageViewId
							+ "secondaryChamberOpTemp",
					"Secondary Chamber Operating Temp");
		}

	}

	private void validateLUD() {
		
		requiredField(materialType, pageViewId + "materialType",
				"Type of Material");
		requiredField(materialDescription, pageViewId + "materialDescription",
				"Material Description");
		requiredField(maxAnnualThroughput, pageViewId + "maxAnnualThroughput",
				"Maximum Annual Throughput");
		requiredField(unitCd, pageViewId + "unitCd", "Units");

		checkRangeValues(maxAnnualThroughput, new Long(1), new Long(
				Long.MAX_VALUE), pageViewId + "maxAnnualThroughput",
				"Maximum Annual Throughput");

	}

	private void validateMAC() {

		requiredField(maxTimeOperated, pageViewId + "maxTimeOperated", "Maximum Time Operated (hrs/yr)");
		requiredField(amtOfMaterialRemoved, pageViewId + "amtOfMaterialRemoved", "Amount of Material Removed (lbs/yr)");

		checkRangeValues(maxTimeOperated, new Integer(0), new Integer(8760), pageViewId + "maxTimeOperated",
				"Maximum Time Operated (hrs/yr)");
		checkRangeValues(amtOfMaterialRemoved, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "amtOfMaterialRemoved",
				"Amount of Material Removed (lbs/yr)");

	}

	private void validateMAT() {
	
		requiredField(materialDescription, pageViewId + "materialDescription", "Material Description");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU ,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

	}
	
	private void validateMET() {
		
		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput");
		requiredField(unitCd, pageViewId + "unitCd", "Units");		

	
		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU ,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput");

	}
	
	private void validateMIL() {
		
		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial",
				"Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");
	}
		
	private void validateMIX() {

		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU ,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput ");

		requiredField(unitCd, pageViewId + "unitCd", "Units");		
	}
	
	private void validateMLD() {

		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU, MAX_VAL_FLOAT_FOR_EU,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");	

	}

	private void validateOEP(){
		
		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput");
		requiredField(unitCd, pageViewId + "unitCd", "Units");		
	
		checkRangeValues(maximumAnnualThroughput,MIN_VAL_FLOAT_FOR_EU, MAX_VAL_FLOAT_FOR_EU,
				pageViewId + "maximumThroughput", "Maximum Annual Throughput");
			
	}

	private void validateORD(){
		
		requiredField(typeOfExplosive, pageViewId + "typeOfExplosive", "Type of Explosive");
		if(typeOfExplosive!=null){
			if(typeOfExplosive.equalsIgnoreCase("other")){
				requiredField(typeOfExplosiveOther, pageViewId + "typeOfExplosiveOther", "Type of Explosive (Other)");
				
			}
		}
		requiredField(maxNumberOfRoundsDetonated, pageViewId + "maxNumberOfRoundsDetonated", "Maximum Number of Rounds Detonated (#/yr)");
	
		checkRangeValues(maxNumberOfRoundsDetonated,new Integer(0), MAX_VAL_INTEGER_FOR_EU,
				pageViewId + "maxNumberOfRoundsDetonated", "Maximum Number of Rounds Detonated (#/yr)");
			
	}

	private void validateOZG(){
	
		requiredField(maxOzoneGenerationRate,pageViewId + "maxOzoneGenerationRate","Maximum Ozone Generation Rate (lbs/yr)");
		
		checkRangeValues(maxOzoneGenerationRate,new Integer(1),MAX_VAL_INTEGER_FOR_EU, pageViewId + "maxOzoneGenerationRate","Maximum Ozone Generation Rate (lbs/yr)");
	}

	private void validatePAM() {
		
		requiredField(equipmentType, pageViewId + "equipmentType", "Type of Equipment");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU ,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

	}

	private void validatePEL() {

		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU ,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");	

	}

	private void validatePNE() {

		requiredField(equipmentType, pageViewId + "equipmentType",
				"Type of Equipment");
		requiredField(eqptCount, pageViewId + "eqptCount", "Total Count of Equipment");
		
		checkRangeValues(eqptCount, new Short((short) 1), MAX_VAL_INTEGER_FOR_EU_COUNT, pageViewId + "eqptCount", "Total Count of Equipment");
		
		if (equipmentType != null) {
			if (equipmentType.equals("Controller")) {
				requiredField(bleedRate, pageViewId + "bleedRate", "Bleed rate");
				checkRangeValues(bleedRate, new BigDecimal("0"), new BigDecimal(
						Float.MAX_VALUE), pageViewId + "bleedRate",
						"Bleed rate");
			} else if(equipmentType.equals("Pump")) {
				requiredField(gasConsumptionRate, pageViewId + "gasConsumptionRate", "Gas Consumption Rate ");
				checkRangeValues(gasConsumptionRate, new BigDecimal("0"), new BigDecimal(
						Float.MAX_VALUE), pageViewId + "gasConsumptionRate",
						"Gas Consumption Rate");
			}
		}
		
	}

	private void validatePRN() {
		
		requiredField(pressType, pageViewId + "pressType", "Press Type");
		if (pressType != null) {
			if (pressType.equalsIgnoreCase("other")) {
				requiredField(pressTypeOther, pageViewId + "pressTypeOther", "Press Type (Other)");
			}
		}
		requiredField(substrateFeedMethod, pageViewId + "substrateFeedMethod", "Substrate Feed Method");
		requiredField(impressionArea, pageViewId + "impressionArea", "Impression Area (sq in)");
		requiredField(maxAnnualUsage, pageViewId + "maxAnnualUsage", "Maximum Annual Usage");
		requiredField(unitCd, pageViewId + "unitCd", "Units");
		requiredField(maxPctVOC, pageViewId + "maxPctVOC", "Maximum % VOC");

		checkRangeValues(maxAnnualUsage, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "maxAnnualUsage",
				"Maximum Annual Usage");
		checkRangeValues(impressionArea, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "impressionArea",
				"Impression Area (sq in)");
		checkRangeValues(maxPctVOC, new Integer(0), MAX_VAL_PERCENTAGE, pageViewId + "maxPctVOC", "Maximum % VOC");

	}
	
	private void validateREM() {

		requiredField(typeOfContaminantBeingTreated, pageViewId + "typeOfContaminantBeingTreated", "Type of Contaminant being Treated");
		if(typeOfContaminantBeingTreated!=null){
			if(typeOfContaminantBeingTreated.equalsIgnoreCase("other")){
				requiredField(otherTypeOfContaminantBeingTreated, pageViewId + "otherTypeOfContaminantBeingTreated", "Type of Contaminant being Treated (Other)");
			}
		}
		requiredField(contaminatedMaterial, pageViewId + "contaminatedMaterial", "Contaminated Material");
		requiredField(VOCEmissionRate, pageViewId + "VOCEmissionRate", "VOC emission rate (lbs/day)");
		
		checkRangeValues(VOCEmissionRate, new Integer(0),MAX_VAL_INTEGER_FOR_EU, pageViewId + "VOCEmissionRate", "VOC emission rate (lbs/day)");

	}
	
	private void validateRES() {

		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maxAnnualUsage, pageViewId + "maxAnnualUsage", "Maximum Annual Usage");
		requiredField(maxPctVOC, pageViewId + "maxPctVOC", "Maximum % VOC Content");
		requiredField(maxPctStyrene, pageViewId + "maxPctStyrene", "Maximum % Styrene Content");
		requiredField(applicationMethod, pageViewId + "applicationMethod", "Application Method");
		
		checkRangeValues(maxPctVOC, new Integer(0),MAX_VAL_PERCENTAGE, pageViewId + "maxPctVOC", "Maximum % VOC Content");
		checkRangeValues(maxPctStyrene, new Integer(0), MAX_VAL_PERCENTAGE, pageViewId + "maxPctStyrene", "Maximum % Styrene Content");
		checkRangeValues(maxAnnualUsage, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "maxAnnualUsage",
				"Maximum Annual Usage");


	}

	private void validateSEB() {

		requiredField(unitType, pageViewId + "unitType", "Unit Type");
		requiredField(unitDesc, pageViewId + "unitDescription",
				"Unit Description");

	}

	private void validateSEM(){

		requiredField(equipmentType, pageViewId + "equipmentType",
				"Type of Equipment");

	}

	private void validateSEP() {

		requiredField(vesselType, pageViewId + "typeOfVessel", "Type Of Vessel");
		requiredField(vesselHeated, pageViewId + "vesselHeated",
				"is Vessel Heated");

	}

	private void validateSRU() {

		requiredField(maxAnnualThroughput, pageViewId + "maxAnnualThroughput",
				"Maximum Annual Throughput");
		requiredField(unitCd, pageViewId + "unitsCd", "Units");

		checkRangeValues(maxAnnualThroughput, new Long(1), new Long(
				Long.MAX_VALUE), pageViewId + "maxAnnualThroughput",
				"Maximum Annual Throughput");

	}

	private void validateSTZ() {
		
		requiredField(facilityType, pageViewId + "facilityType", "Facility Type");
		requiredField(maxAnnualUsage, pageViewId + "maxAnnualUsage", "Maximum Annual Usage (lbs/yr)");
		requiredField(materialType, pageViewId + "materialType", "Type of Material");

		checkRangeValues(maxAnnualUsage, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "maxAnnualUsage",
				"Maximum Annual Usage (lbs/yr)");

	}

	private void validateSVC(){
	
		requiredField(equipmentType, pageViewId + "equipmentType", "Equipment Type");
		if(equipmentType!=null){
			if(equipmentType.equalsIgnoreCase("other")){
				requiredField(equipmentTypeOther, pageViewId + "equipmentTypeOther", "Equipment Type (Other)");
			}
		}
		requiredField(solventType, pageViewId + "solventType", "Solvent Type");
		requiredField(maxAnnualUsage, pageViewId + "maxAnnualUsage", "Maximum Annual Usage (lbs/yr)");

		checkRangeValues(maxAnnualUsage, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "maxAnnualUsage",
			"Maximum Annual Usage (lbs/yr)");


	}
	
	private void validateSVU() {
	
		requiredField(nameAndTypeOfMaterial, pageViewId + "nameAndTypeOfMaterial", "Name and Type of Material");
		requiredField(maxAnnualThroughput, pageViewId + "maxAnnualThroughput", "Maximum Annual Throughput (lbs/yr)");
		requiredField(maxPctVOC, pageViewId + "maxPctVOC", "Maximum % VOC Content");

		checkRangeValues(maxAnnualThroughput, new Long(1), new Long(MAX_VAL_INTEGER_FOR_EU),
				pageViewId + "maxAnnualThroughput", "Maximum Annual Throughput  (lbs/yr)");
		checkRangeValues(maxPctVOC, new Integer(0), MAX_VAL_PERCENTAGE, pageViewId + "maxPctVOC", "Maximum % VOC Content");
			
	}
	
	private void validateTAR() {

		requiredField(heatInputRating, pageViewId + "heatInputRating", "Heat Input Rating (MMBtu/hr)");
		requiredField(maximumAnnualThroughput, pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");

		checkRangeValues(maximumAnnualThroughput, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU,
				pageViewId + "maximumAnnualThroughput", "Maximum Annual Throughput (tons/yr)");
		checkRangeValues(heatInputRating, MIN_VAL_FLOAT_FOR_EU, MAX_VAL_FLOAT_FOR_EU,
				pageViewId + "heatInputRating", "Heat Input Rating (MMBtu/hr)");

	}
	
	private void validateTGT() {

		requiredField(heatInputRating, pageViewId + "heatInputRating",
				"Heat Input Rating");

		checkRangeValues(heatInputRating, new BigDecimal("0.01"), new BigDecimal(
				Float.MAX_VALUE), pageViewId + "heatInputRating",
				"Heat Input Rating (MMBtu/hr)");

	}

	
	private void validateTIM() {

		requiredField(maxAmtOfRubberRemoved, pageViewId + "maxAmtOfRubberRemoved",
				"Maximum Amount of Rubber Removed (lbs/yr)");

		checkRangeValues(maxAmtOfRubberRemoved, new Integer(1), MAX_VAL_INTEGER_FOR_EU,
				pageViewId + "maxAmtOfRubberRemoved", "Maximum Amount of Rubber Removed (lbs/yr)");

	}
	
	
	private void validateTKO() {
	
		requiredField(materialType, pageViewId + "materialType", "Type of Material");
		requiredField(platingLineFlag, pageViewId + "platingLineFlag", "Is this for a Plating Line");
		requiredField(metalType, pageViewId + "metalType", "Type of Metal");
		
		if (platingLineFlag != null) {
			if (platingLineFlag.equalsIgnoreCase("Y")) {
				requiredField(platingType, pageViewId + "platingType", "Type of Plating");
			}
		}
		if (platingType != null) {
			if (platingType.equalsIgnoreCase("other")) {
				requiredField(platingTypeOther, pageViewId + "platingTypeOther", "Type of Plating (Other)");

			}
		}

		if (metalType != null) {
			if (metalType.equalsIgnoreCase("other")) {
				requiredField(metalTypeOther, pageViewId + "metalTypeOther", "Type of Metal (Other)");

			}
		}

		requiredField(maxAnnualUsage, pageViewId + "maxAnnualUsage", "Maximum Annual Usage (lbs/yr)");
		requiredField(capacity, pageViewId + "capacity", "Tank Capacity (gal)");
		requiredField(concentrationPct, pageViewId + "concentrationPct", "Concentration %");

		checkRangeValues(maxAnnualUsage, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "maxAnnualUsage",
				"Maximum Annual Usage (lbs/yr)");
		checkRangeValues(capacity, new Integer(1), MAX_VAL_INTEGER_FOR_EU, pageViewId + "capacity", "Tank Capacity (gal)");
		checkRangeValues(concentrationPct, new Integer(0), MAX_VAL_PERCENTAGE, pageViewId + "concentrationPct",
				"Concentration %");

	}

	private void validateTNK() {

		requiredField(materialTypeStored, pageViewId + "materialTypeStored", "Material Type");
		
		if (null != getMaterialTypeStored()) {
			if (getMaterialTypeStored().equals(TnkMaterialStoredTypeDef.LIQUID)) {
				requiredField(liquidMaterialTypeStored, pageViewId + "liquidMaterialTypeStored", "Type of Liquid");
				
				if (null != getLiquidMaterialTypeStored()) {
					if (getLiquidMaterialTypeStored().equals(TnkMaterialStoredTypeLiquidDef.OTHER)) {
						requiredField(materialTypeStoredDesc, pageViewId + "materialTypeStoredDesc", "Description of Material Stored");
					}
				}
			} else if (!getMaterialTypeStored().equals(TnkMaterialStoredTypeDef.GASOLINE)){
				requiredField(materialTypeStoredDesc, pageViewId + "materialTypeStoredDesc", "Description of Material Stored");
			}
		}

		requiredField(capacity, pageViewId + "capacity", "Capacity");
		requiredField(capacityUnit, pageViewId + "capacityUnit", "Capacity Units");

		requiredField(maxThroughput, pageViewId + "maxThroughput", "Maximum Throughput");
		requiredField(unitCd, pageViewId + "unitCd", "Maximum Throughput Units");

		checkRangeValues(capacity, new Integer(1), new Integer(Integer.MAX_VALUE), pageViewId + "capacity", "Capacity");
		checkRangeValues(maxThroughput, new BigDecimal(1), new BigDecimal(Float.MAX_VALUE),
				pageViewId + "maxThroughput", "Maximum Throughput");

	}

	private void validateVNT() {
		// TODO Auto-generated method stub
	}

	private void validateWEL() {
		
		requiredField(weldingProcess, pageViewId + "weldingProceess",
				"Welding Process");
		requiredField(maxAmtOfElectrodeConsumed, pageViewId + "maxAmtOfElectrodeConsumed", "Maximum Amount of Electrode Consumed (lbs/yr)");
		
		
		checkRangeValues(maxAmtOfElectrodeConsumed, new Integer(1),MAX_VAL_INTEGER_FOR_EU,
				pageViewId + "maxAmtOfElectrodeConsumed", "Maximum Amount of Electrode Consumed (lbs/yr)");

	}


	private void validateWWE() {
		
		requiredField(maxAnnualWoodDustGenerated, pageViewId + "maxAnnualWoodDustGenerated",
				"Maximum Annual Wood Dust Generated (tons/yr)");
		requiredField(equipmentType, pageViewId + "equipmentType", "Equipment Type");
		requiredField(powerRatingFlag, pageViewId + "powerRatingFlag", "Power Rating (hp) greater than 5 ");

		
		checkRangeValues(maxAnnualWoodDustGenerated, MIN_VAL_FLOAT_FOR_EU,MAX_VAL_FLOAT_FOR_EU,
				pageViewId + "maxAnnualWoodDustGenerated", "Maximum Annual Wood Dust Generated (tons/yr)");

	}

	
	private void addValidationMessages(String fieldName, String errorMsg) {
		validationMessages.put(fieldName, new ValidationMessage(fieldName,
				errorMsg, ValidationMessage.Severity.ERROR, fieldName));
	}
	
	public BigDecimal getSiteRating() {
		return siteRating;
	}

	public void setSiteRating(BigDecimal siteRating) {
		this.siteRating = siteRating;
	}

	public String getSiteRatingUnitCd() {
		return siteRatingUnitCd;
	}

	public void setSiteRatingUnitCd(String siteRatingUnitCd) {
		this.siteRatingUnitCd = siteRatingUnitCd;
	}
	
	public boolean isNamePlateRatingEmptyOrZero() {
		boolean ret = true;
		if (namePlateRating != null && !namePlateRating.equals("") && (namePlateRating.compareTo(new BigDecimal("0")) != 0)) {
			ret = false;
		}
		return ret;
	}
	
	public boolean isSiteRatingEmptyOrZero() {
		boolean ret = true;
		if (siteRating != null && !siteRating.equals("") && (siteRating.compareTo(new BigDecimal("0")) != 0)) {
			ret = false;
		}
		return ret;
	}

	public String getPageViewId() {
		return pageViewId;
	}

	public void setPageViewId(String pageViewId) {
		this.pageViewId = pageViewId;
	}

	public String getNameAndTypeOfMaterial() {
		return nameAndTypeOfMaterial;
	}

	public void setNameAndTypeOfMaterial(String nameAndTypeOfMaterial) {
		this.nameAndTypeOfMaterial = nameAndTypeOfMaterial;
	}
	
	public String getApplicationMethod() {
		return applicationMethod;
	}

	public void setApplicationMethod(String applicationMethod) {
		this.applicationMethod = applicationMethod;
	}

	public String getWeldingProcess() {
		return weldingProcess;
	}

	public void setWeldingProcess(String weldingProcess) {
		this.weldingProcess = weldingProcess;
	}

	public Integer getMaxAmtOfElectrodeConsumed() {
		return maxAmtOfElectrodeConsumed;
	}

	public void setMaxAmtOfElectrodeConsumed(Integer maxAmtOfElectrodeConsumed) {
		this.maxAmtOfElectrodeConsumed = maxAmtOfElectrodeConsumed;
	}

	public Integer getMaxTimeOperated() {
		return maxTimeOperated;
	}

	public void setMaxTimeOperated(Integer maxTimeOperated) {
		this.maxTimeOperated = maxTimeOperated;
	}

	public String getSubstrateBlasted() {
		return substrateBlasted;
	}

	public void setSubstrateBlasted(String substrateBlasted) {
		this.substrateBlasted = substrateBlasted;
		if (this.substrateBlasted != null) {
			if (! this.substrateBlasted.equalsIgnoreCase("other")) {
				setSubstrateBlastedOther(null);

			}
		}
	}

	public String getSubstrateRemoved() {
		return substrateRemoved;
	}

	public void setSubstrateRemoved(String substrateRemoved) {
		this.substrateRemoved = substrateRemoved;
		if (this.substrateRemoved != null) {
			if (!this.substrateRemoved.equalsIgnoreCase("leadedpaint")) {
				setConcentrationOfLeadPct(null);
			}

		}
		if (this.substrateRemoved != null) {
			if (!this.substrateRemoved.equalsIgnoreCase("other")) {
				setSubstrateRemovedOther(null);
			}

		}

	}

	public String getConcentrationOfLeadPct() {
		return concentrationOfLeadPct;
	}

	public void setConcentrationOfLeadPct(String concentrationOfLeadPct) {
		this.concentrationOfLeadPct = concentrationOfLeadPct;
	}

	public String getBlastMediaCARBCertifiedFlag() {
		return blastMediaCARBCertifiedFlag;
	}

	public void setBlastMediaCARBCertifiedFlag(String blastMediaCARBCertifiedFlag) {
		this.blastMediaCARBCertifiedFlag = blastMediaCARBCertifiedFlag;
	}

	public Integer getMaxNumberOfTimesBlastMediaReclaimedForReuse() {
		return maxNumberOfTimesBlastMediaReclaimedForReuse;
	}

	public void setMaxNumberOfTimesBlastMediaReclaimedForReuse(Integer maxNumberOfTimesBlastMediaReclaimedForReuse) {
		this.maxNumberOfTimesBlastMediaReclaimedForReuse = maxNumberOfTimesBlastMediaReclaimedForReuse;
	}

	public Integer getMaxAnnualUsage() {
		return maxAnnualUsage;
	}

	public void setMaxAnnualUsage(Integer maxAnnualUsage) {
		this.maxAnnualUsage = maxAnnualUsage;
	}

	public String getUnitTypeOther() {
		return unitTypeOther;
	}

	public void setUnitTypeOther(String unitTypeOther) {
		this.unitTypeOther = unitTypeOther;
	}

	public String getSubstrateRemovedOther() {
		return substrateRemovedOther;
	}

	public void setSubstrateRemovedOther(String substrateRemovedOther) {
		this.substrateRemovedOther = substrateRemovedOther;
	}

	public String getSubstrateBlastedOther() {
		return substrateBlastedOther;
	}

	public void setSubstrateBlastedOther(String substrateBlastedOther) {
		this.substrateBlastedOther = substrateBlastedOther;
	}
	
	public String getPlatingLineFlag() {
		return platingLineFlag;
	}

	public void setPlatingLineFlag(String platingLineFlag) {
		this.platingLineFlag = platingLineFlag;
	}

	public String getPlatingType() {
		return platingType;
	}

	public void setPlatingType(String platingType) {
		this.platingType = platingType;
		if(this.platingType!=null){
			if(!this.platingType.equalsIgnoreCase("other")){
				setPlatingTypeOther(null);
			}
		}
	}

	public String getPlatingTypeOther() {
		return platingTypeOther;
	}

	public void setPlatingTypeOther(String platingTypeOther) {
		this.platingTypeOther = platingTypeOther;
	}

	public String getMetalType() {
		return metalType;
	}

	public void setMetalType(String metalType) {
		this.metalType = metalType;
		if(this.metalType!=null){
			if(!this.metalType.equalsIgnoreCase("other")){
				setMetalTypeOther(null);
			}
		}
	}

	public String getMetalTypeOther() {
		return metalTypeOther;
	}

	public void setMetalTypeOther(String metalTypeOther) {
		this.metalTypeOther = metalTypeOther;
	}

	public Integer getConcentrationPct() {
		return concentrationPct;
	}

	public void setConcentrationPct(Integer concentrationPct) {
		this.concentrationPct = concentrationPct;
	}

	public String getPressType() {
		return pressType;
	}

	public void setPressType(String pressType) {
		this.pressType = pressType;
		if(this.pressType!=null){
			if(! this.pressType.equalsIgnoreCase("other")){
				setPressTypeOther(null);
			}
		}
	}

	public String getPressTypeOther() {
		return pressTypeOther;
	}

	public void setPressTypeOther(String pressTypeOther) {
		this.pressTypeOther = pressTypeOther;
	}

	public String getSubstrateFeedMethod() {
		return substrateFeedMethod;
	}

	public void setSubstrateFeedMethod(String substrateFeedMethod) {
		this.substrateFeedMethod = substrateFeedMethod;
	}

	public Integer getImpressionArea() {
		return impressionArea;
	}

	public void setImpressionArea(Integer impressionArea) {
		this.impressionArea = impressionArea;
	}

	public Integer getMaxPctVOC() {
		return maxPctVOC;
	}

	public void setMaxPctVOC(Integer maxPctVOC) {
		this.maxPctVOC = maxPctVOC;
	}

	public Integer getMaxPctStyrene() {
		return maxPctStyrene;
	}

	public void setMaxPctStyrene(Integer maxPctStyrene) {
		this.maxPctStyrene = maxPctStyrene;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getDryCleaningType() {
		return dryCleaningType;
	}

	public void setDryCleaningType(String dryCleaningType) {
		this.dryCleaningType = dryCleaningType;
	}

	public Integer getMaxOzoneGenerationRate() {
		return maxOzoneGenerationRate;
	}

	public void setMaxOzoneGenerationRate(Integer maxOzoneGenerationRate) {
		this.maxOzoneGenerationRate = maxOzoneGenerationRate;
	}

	public String getEquipmentTypeOther() {
		return equipmentTypeOther;
	}

	public void setEquipmentTypeOther(String equipmentTypeOther) {
		this.equipmentTypeOther = equipmentTypeOther;
	}

	public String getSolventType() {
		return solventType;
	}

	public void setSolventType(String solventType) {
		this.solventType = solventType;
	}

	public void platingLineFlagListener(ValueChangeEvent event) {
		if (this.platingLineFlag != null) {
			if (this.platingLineFlag.equalsIgnoreCase("N")) {
				setPlatingTypeOther(null);
				setPlatingType(null);

			}

		}
	}
	
	public boolean isPlatingLine() {
		boolean ret = false;
		if (platingLineFlag != null && !platingLineFlag.equals("") && (platingLineFlag.equalsIgnoreCase("Y"))) {
			ret = true;
		}
		return ret;
	}

	public boolean isOtherPlatingType() {
		boolean ret = false;
		if (this.platingType != null && !this.platingType.equals("") && (this.platingType.equalsIgnoreCase("Other"))) {
			ret = true;
		}
		return ret;
	}

	public boolean isOtherMetalType() {
		boolean ret = false;
		if (this.metalType != null && !this.metalType.equals("") && (this.metalType.equalsIgnoreCase("Other"))) {
			ret = true;
		}
		return ret;
	}
	
	public void metalTypeListener(ValueChangeEvent event) {
		if (this.metalType != null) {
			if (! this.metalType.equalsIgnoreCase("other")) {
				setMetalTypeOther(null);

			}

		}
	}

	public void substrateRemovedListener(ValueChangeEvent event) {
		if (this.substrateRemoved != null) {
			if (! this.substrateRemoved.equalsIgnoreCase("leadedpaint")) {
				setConcentrationOfLeadPct(null);

			}
			if (! this.substrateRemoved.equalsIgnoreCase("other")) {
				setSubstrateRemovedOther(null);

			}

		}
	}
	

	public void substrateBlastedListener(ValueChangeEvent event) {
		if (this.substrateBlasted != null) {
			if (! this.substrateBlasted.equalsIgnoreCase("other")) {
				setSubstrateBlastedOther(null);

			}

		}
	}

	public BigDecimal getMaximumAnnualThroughput() {
		return maximumAnnualThroughput;
	}

	public void setMaximumAnnualThroughput(BigDecimal maximumAnnualThroughput) {
		this.maximumAnnualThroughput = maximumAnnualThroughput;
	}

	public BigDecimal getMaxAnnualUsageAbs() {
		return maxAnnualUsageAbs;
	}

	public void setMaxAnnualUsageAbs(BigDecimal maxAnnualUsageAbs) {
		this.maxAnnualUsageAbs = maxAnnualUsageAbs;
	}

	

	public Integer getMaxAmtOfRubberRemoved() {
		return maxAmtOfRubberRemoved;
	}

	public void setMaxAmtOfRubberRemoved(Integer maxAmtOfRubberRemoved) {
		this.maxAmtOfRubberRemoved = maxAmtOfRubberRemoved;
	}
	
	public BigDecimal getMaxAnnualWoodDustGenerated() {
		return maxAnnualWoodDustGenerated;
	}

	public void setMaxAnnualWoodDustGenerated(BigDecimal maxAnnualWoodDustGenerated) {
		this.maxAnnualWoodDustGenerated = maxAnnualWoodDustGenerated;
	}

	public String getPowerRatingFlag() {
		return powerRatingFlag;
	}

	public void setPowerRatingFlag(String powerRatingFlag) {
		this.powerRatingFlag = powerRatingFlag;
	}

	public String getTypeOfExplosive(){
		return typeOfExplosive;
	}
	
	public void setTypeOfExplosive(String typeOfExplosive){
		this.typeOfExplosive=typeOfExplosive;
		if(this.typeOfExplosive!=null){
			if(! typeOfExplosive.equalsIgnoreCase("other")){
				setTypeOfExplosiveOther(null);
			}
		}
	}
	public String getTypeOfExplosiveOther(){
		return typeOfExplosiveOther;
	}
	
	public void setTypeOfExplosiveOther(String typeOfExplosiveOther){
		this.typeOfExplosiveOther=typeOfExplosiveOther;
	}

	public Integer getMaxNumberOfRoundsDetonated(){
		return maxNumberOfRoundsDetonated;
	}
	
	public void setMaxNumberOfRoundsDetonated(Integer maxNumberOfRoundsDetonated){
		this.maxNumberOfRoundsDetonated= maxNumberOfRoundsDetonated;
	}
	
	public void typeOfExplosiveListener(ValueChangeEvent event) {
		if (this.typeOfExplosive != null) {
			if (! this.typeOfExplosive.equalsIgnoreCase("other")) {
				setTypeOfExplosiveOther(null);

			}

		}
	}

	public Integer getAmtOfMaterialRemoved() {
		return amtOfMaterialRemoved;
	}

	public void setAmtOfMaterialRemoved(Integer amtOfMaterialRemoved) {
		this.amtOfMaterialRemoved = amtOfMaterialRemoved;
	}


	public String getTypeOfContaminantBeingTreated() {
		return typeOfContaminantBeingTreated;
	}


	public void setTypeOfContaminantBeingTreated(String typeOfContaminantBeingTreated) {
		this.typeOfContaminantBeingTreated = typeOfContaminantBeingTreated;
		if (this.typeOfContaminantBeingTreated != null) {
			if (! this.typeOfContaminantBeingTreated.equalsIgnoreCase("other")) {
				setOtherTypeOfContaminantBeingTreated(null);
			}
		}

	}

	public String getOtherTypeOfContaminantBeingTreated() {
		return otherTypeOfContaminantBeingTreated;
	}

	public void setOtherTypeOfContaminantBeingTreated(String otherTypeOfContaminantBeingTreated) {
		this.otherTypeOfContaminantBeingTreated = otherTypeOfContaminantBeingTreated;
	}

	public String getContaminatedMaterial() {
		return contaminatedMaterial;
	}

	public void setContaminatedMaterial(String contaminatedMaterial) {
		this.contaminatedMaterial = contaminatedMaterial;
	}

	public Integer getVOCEmissionRate() {
		return VOCEmissionRate;
	}

	public void setVOCEmissionRate(Integer vOCEmissionRate) {
		VOCEmissionRate = vOCEmissionRate;
	}

	public boolean isTypeOfContaminantBeingTreatedOther(){
		boolean ret = false;
		if (this.typeOfContaminantBeingTreated != null && !this.typeOfContaminantBeingTreated.equals("") && (this.typeOfContaminantBeingTreated.equalsIgnoreCase("Other"))) {
			ret = true;
		}
		return ret;
		
	}
	
	public void typeOfContaminantBeingTreatedListener(ValueChangeEvent event) {
		if (this.typeOfContaminantBeingTreated != null) {
			if (! this.typeOfContaminantBeingTreated.equalsIgnoreCase("other")) {
				setOtherTypeOfContaminantBeingTreated(null);

			}

		}
	}

	public String getTypeOfCoatingOperation() {
		return typeOfCoatingOperation;
	}

	public void setTypeOfCoatingOperation(String typeOfCoatingOperation) {
		this.typeOfCoatingOperation = typeOfCoatingOperation;
	}

	public String getTypeOfMaterialBeingCoated() {
		return typeOfMaterialBeingCoated;
	}

	public void setTypeOfMaterialBeingCoated(String typeOfMaterialBeingCoated) {
		this.typeOfMaterialBeingCoated = typeOfMaterialBeingCoated;
	}


	public String getTypeOfMaterialBeingCoatedOther() {
		return typeOfMaterialBeingCoatedOther;
	}

	public void setTypeOfMaterialBeingCoatedOther(String typeOfMaterialBeingCoatedOther) {
		this.typeOfMaterialBeingCoatedOther = typeOfMaterialBeingCoatedOther;
	}

	public String getTypeOfProductBeingCoated() {
		return typeOfProductBeingCoated;
	}

	public void setTypeOfProductBeingCoated(String typeOfProductBeingCoated) {
		this.typeOfProductBeingCoated = typeOfProductBeingCoated;
	}

	public String getApplicationMethodOther() {
		return applicationMethodOther;
	}

	public void setApplicationMethodOther(String applicationMethodOther) {
		this.applicationMethodOther = applicationMethodOther;
	}
	
	public void typeOfMaterialBeingCoatedListener(ValueChangeEvent event) {
		if (this.typeOfMaterialBeingCoated != null) {
			if (! this.typeOfMaterialBeingCoated.equalsIgnoreCase("other")) {
				setTypeOfMaterialBeingCoatedOther(null);

			}

		}
	}


	public void applicationMethodListener(ValueChangeEvent event) {
		if (this.applicationMethod != null) {
			if (! this.applicationMethod.equalsIgnoreCase("other")) {
				setApplicationMethodOther(null);

			}

		}
	}

	
	public String getSubmergedFillPipeFlag() {
		return submergedFillPipeFlag;
	}

	public void setSubmergedFillPipeFlag(String submergedFillPipeFlag) {
		this.submergedFillPipeFlag = submergedFillPipeFlag;
	}

	public String getTankLocation() {
		return tankLocation;
	}

	public void setTankLocation(String tankLocation) {
		this.tankLocation = tankLocation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((applicationMethod == null) ? 0 : applicationMethod.hashCode());
		result = prime * result + ((applicationMethodOther == null) ? 0 : applicationMethodOther.hashCode());
		result = prime * result + ((batchingType == null) ? 0 : batchingType.hashCode());
		result = prime * result + ((blastMediaCARBCertifiedFlag == null) ? 0 : blastMediaCARBCertifiedFlag.hashCode());
		result = prime * result + ((bleedRate == null) ? 0 : bleedRate.hashCode());
		result = prime * result + ((burnerSystem == null) ? 0 : burnerSystem.hashCode());
		result = prime * result + ((capacity == null) ? 0 : capacity.hashCode());
		result = prime * result + ((capacityUnit == null) ? 0 : capacityUnit.hashCode());
		result = prime * result + ((chargeRate == null) ? 0 : chargeRate.hashCode());
		result = prime * result + ((concentrationOfLeadPct == null) ? 0 : concentrationOfLeadPct.hashCode());
		result = prime * result + ((concentrationPct == null) ? 0 : concentrationPct.hashCode());
		result = prime * result + ((dehydrationType == null) ? 0 : dehydrationType.hashCode());
		result = prime * result + ((designCapacity == null) ? 0 : designCapacity.hashCode());
		result = prime * result + ((dissolvedSolidsTotal == null) ? 0 : dissolvedSolidsTotal.hashCode());
		result = prime * result + ((driftRate == null) ? 0 : driftRate.hashCode());
		result = prime * result + ((dryCleaningType == null) ? 0 : dryCleaningType.hashCode());
		result = prime * result + ((emissionUnitTypeCd == null) ? 0 : emissionUnitTypeCd.hashCode());
		result = prime * result + ((emuId == null) ? 0 : emuId.hashCode());
		result = prime * result + ((engineType == null) ? 0 : engineType.hashCode());
		result = prime * result + ((equipmentType == null) ? 0 : equipmentType.hashCode());
		result = prime * result + ((equipmentTypeOther == null) ? 0 : equipmentTypeOther.hashCode());
		result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result + ((facilityType == null) ? 0 : facilityType.hashCode());
		result = prime * result + ((firingType == null) ? 0 : firingType.hashCode());
		result = prime * result + ((fuelHeatContent == null) ? 0 : fuelHeatContent.hashCode());
		result = prime * result + ((fuelHeatContentUnitsCd == null) ? 0 : fuelHeatContentUnitsCd.hashCode());
		result = prime * result + ((fuelType == null) ? 0 : fuelType.hashCode());
		result = prime * result + ((fugitiveLeaks) ? 1 : 1000);
		result = prime * result + ((gasConsumptionRate == null) ? 0 : gasConsumptionRate.hashCode());
		result = prime * result + ((heatInputRating == null) ? 0 : heatInputRating.hashCode());
		result = prime * result + ((impressionArea == null) ? 0 : impressionArea.hashCode());
		result = prime * result + ((incineratorType == null) ? 0 : incineratorType.hashCode());
		result = prime * result + ((manufacturerName == null) ? 0 : manufacturerName.hashCode());
		result = prime * result + ((materialDescription == null) ? 0 : materialDescription.hashCode());
		result = prime * result + ((materialType == null) ? 0 : materialType.hashCode());
		result = prime * result + ((materialTypeStored == null) ? 0 : materialTypeStored.hashCode());
		result = prime * result + ((liquidMaterialTypeStored == null) ? 0 : liquidMaterialTypeStored.hashCode());
		result = prime * result + ((materialTypeStoredDesc == null) ? 0 : materialTypeStoredDesc.hashCode());
		result = prime * result + ((maxAmtOfElectrodeConsumed == null) ? 0 : maxAmtOfElectrodeConsumed.hashCode());
		result = prime * result + ((maxAmtOfRubberRemoved == null) ? 0 : maxAmtOfRubberRemoved.hashCode());
		result = prime * result + ((maxAnnualThroughput == null) ? 0 : maxAnnualThroughput.hashCode());
		result = prime * result + ((maxAnnualUsage == null) ? 0 : maxAnnualUsage.hashCode());
		result = prime * result + ((maxAnnualUsageAbs == null) ? 0 : maxAnnualUsageAbs.hashCode());
		result = prime * result + ((maxAnnualWoodDustGenerated == null) ? 0 : maxAnnualWoodDustGenerated.hashCode());
		result = prime * result + ((maxBurnerDesignRate == null) ? 0 : maxBurnerDesignRate.hashCode());
		result = prime * result + ((maxDesignCapacity == null) ? 0 : maxDesignCapacity.hashCode());
		result = prime * result + ((maximumAnnualThroughput == null) ? 0 : maximumAnnualThroughput.hashCode());
		result = prime * result + ((maxNumberOfTimesBlastMediaReclaimedForReuse == null) ? 0
				: maxNumberOfTimesBlastMediaReclaimedForReuse.hashCode());
		result = prime * result + ((maxOzoneGenerationRate == null) ? 0 : maxOzoneGenerationRate.hashCode());				
		result = prime * result + ((maxPctStyrene == null) ? 0 : maxPctStyrene.hashCode());
		result = prime * result + ((maxPctVOC == null) ? 0 : maxPctVOC.hashCode());
		result = prime * result + ((maxProductionRate == null) ? 0 : maxProductionRate.hashCode());
		result = prime * result + ((maxThroughput == null) ? 0 : maxThroughput.hashCode());
		result = prime * result + ((maxTimeOperated == null) ? 0 : maxTimeOperated.hashCode());
		result = prime * result + ((metalType == null) ? 0 : metalType.hashCode());
		result = prime * result + ((metalTypeOther == null) ? 0 : metalTypeOther.hashCode());
		result = prime * result + ((minDesignCapacity == null) ? 0 : minDesignCapacity.hashCode());
		result = prime * result + ((modelNameNumber == null) ? 0 : modelNameNumber.hashCode());
		result = prime * result + ((nameAndTypeOfMaterial == null) ? 0 : nameAndTypeOfMaterial.hashCode());
		result = prime * result + ((namePlateRating == null) ? 0 : namePlateRating.hashCode());
		result = prime * result + ((operationType == null) ? 0 : operationType.hashCode());
		result = prime * result + ((pageViewId == null) ? 0 : pageViewId.hashCode());
		result = prime * result + ((pilotGasVolume == null) ? 0 : pilotGasVolume.hashCode());
		result = prime * result + ((plantType == null) ? 0 : plantType.hashCode());
		result = prime * result + ((platingLineFlag == null) ? 0 : platingLineFlag.hashCode());
		result = prime * result + ((platingType == null) ? 0 : platingType.hashCode());
		result = prime * result + ((platingTypeOther == null) ? 0 : platingTypeOther.hashCode());
		result = prime * result + ((powerRatingFlag == null) ? 0 : powerRatingFlag.hashCode());
		result = prime * result + ((powerSource == null) ? 0 : powerSource.hashCode());
		result = prime * result + ((pressType == null) ? 0 : pressType.hashCode());
		result = prime * result + ((pressTypeOther == null) ? 0 : pressTypeOther.hashCode());
		result = prime * result + ((primaryChamberOpTemp == null) ? 0 : primaryChamberOpTemp.hashCode());
		result = prime * result + ((primaryFuelType == null) ? 0 : primaryFuelType.hashCode());
		result = prime * result + ((producedMaterialType == null) ? 0 : producedMaterialType.hashCode());
		result = prime * result + ((secondaryChamberOpTemp == null) ? 0 : secondaryChamberOpTemp.hashCode());
		result = prime * result + ((secondaryFuelType == null) ? 0 : secondaryFuelType.hashCode());
		result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
		result = prime * result + ((serialNumberEffectiveDate == null) ? 0 : serialNumberEffectiveDate.hashCode());
		result = prime * result + ((siteRating == null) ? 0 : siteRating.hashCode());
		result = prime * result + ((siteRatingUnitCd == null) ? 0 : siteRatingUnitCd.hashCode());
		result = prime * result + ((solventType == null) ? 0 : solventType.hashCode());
		result = prime * result + ((submergedFillPipeFlag == null) ? 0 : submergedFillPipeFlag.hashCode());
		result = prime * result + ((substrateBlasted == null) ? 0 : substrateBlasted.hashCode());
		result = prime * result + ((substrateFeedMethod == null) ? 0 : substrateFeedMethod.hashCode());
		result = prime * result + ((substrateRemoved == null) ? 0 : substrateRemoved.hashCode());
		result = prime * result + ((substrateRemovedOther == null) ? 0 : substrateRemovedOther.hashCode());
		result = prime * result + ((tankLocation == null) ? 0 : tankLocation.hashCode());
		result = prime * result + ((typeOfContaminantBeingTreated == null) ? 0 : typeOfContaminantBeingTreated.hashCode());
		result = prime * result + ((typeOfExplosive == null) ? 0 : typeOfExplosive.hashCode());
		result = prime * result + ((typeOfExplosiveOther == null) ? 0 : typeOfExplosiveOther.hashCode());
		result = prime * result + ((typeOfCoatingOperation == null) ? 0 : typeOfCoatingOperation.hashCode());
		result = prime * result + ((typeOfMaterialBeingCoated == null) ? 0 : typeOfMaterialBeingCoated.hashCode());
		result = prime * result + ((typeOfMaterialBeingCoatedOther == null) ? 0 : typeOfMaterialBeingCoatedOther.hashCode());
		result = prime * result + ((typeOfProductBeingCoated == null) ? 0 : typeOfProductBeingCoated.hashCode());
		result = prime * result + ((typeOfUse==null)? 0 : typeOfUse.hashCode());
		result = prime * result + ((unitCd == null) ? 0 : unitCd.hashCode());
		result = prime * result + ((unitDesc == null) ? 0 : unitDesc.hashCode());
		result = prime * result + ((unitType == null) ? 0 : unitType.hashCode());
		result = prime * result + ((unitTypeOther == null) ? 0 : unitTypeOther.hashCode());
		result = prime * result + (vesselHeated ? 1231 : 1237);
		result = prime * result + ((vesselType == null) ? 0 : vesselType.hashCode());
		result = prime * result + ((VOCEmissionRate == null) ? 0 : VOCEmissionRate.hashCode());
		result = prime * result + ((weldingProcess == null) ? 0 : weldingProcess.hashCode());
		result = prime * result + ((components == null) ? 0 : components.hashCode());
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
		EmissionUnitType other = (EmissionUnitType) obj;
		if (applicationMethod == null) {
			if (other.applicationMethod != null)
				return false;
		} else if (!applicationMethod.equals(other.applicationMethod))
			return false;
		if (applicationMethodOther == null) {
			if (other.applicationMethodOther != null)
				return false;
		} else if (!applicationMethodOther.equals(other.applicationMethodOther))
			return false;
		if (batchingType == null) {
			if (other.batchingType != null)
				return false;
		} else if (!batchingType.equals(other.batchingType))
			return false;
		if (blastMediaCARBCertifiedFlag == null) {
			if (other.blastMediaCARBCertifiedFlag != null)
				return false;
		} else if (!blastMediaCARBCertifiedFlag.equals(other.blastMediaCARBCertifiedFlag))
			return false;
		if (bleedRate == null) {
			if (other.bleedRate != null)
				return false;
		} else if (!bleedRate.equals(other.bleedRate))
			return false;
		if (burnerSystem == null) {
			if (other.burnerSystem != null)
				return false;
		} else if (!burnerSystem.equals(other.burnerSystem))
			return false;
		if (capacity == null) {
			if (other.capacity != null)
				return false;
		} else if (!capacity.equals(other.capacity))
			return false;
		if (capacityUnit == null) {
			if (other.capacityUnit != null)
				return false;
		} else if (!capacityUnit.equals(other.capacityUnit))
			return false;
		if (chargeRate == null) {
			if (other.chargeRate != null)
				return false;
		} else if (!chargeRate.equals(other.chargeRate))
			return false;
		if (concentrationOfLeadPct == null) {
			if (other.concentrationOfLeadPct != null)
				return false;
		} else if (!concentrationOfLeadPct.equals(other.concentrationOfLeadPct))
			return false;
		if (concentrationPct == null) {
			if (other.concentrationPct != null)
				return false;
		} else if (!concentrationPct.equals(other.concentrationPct))
			return false;
		if (dehydrationType == null) {
			if (other.dehydrationType != null)
				return false;
		} else if (!dehydrationType.equals(other.dehydrationType))
			return false;
		if (contaminatedMaterial == null) {
			if (other.contaminatedMaterial != null)
				return false;
		} else if (!contaminatedMaterial.equals(other.contaminatedMaterial))
			return false;
		if (designCapacity == null) {
			if (other.designCapacity != null)
				return false;
		} else if (!designCapacity.equals(other.designCapacity))
			return false;
		if (dissolvedSolidsTotal == null) {
			if (other.dissolvedSolidsTotal != null)
				return false;
		} else if (!dissolvedSolidsTotal.equals(other.dissolvedSolidsTotal))
			return false;
		if (driftRate == null) {
			if (other.driftRate != null)
				return false;
		} else if (!driftRate.equals(other.driftRate))
			return false;
		if (dryCleaningType == null) {
			if (other.dryCleaningType != null)
				return false;
		} else if (!driftRate.equals(other.driftRate))
			return false;
		if (emissionUnitTypeCd == null) {
			if (other.emissionUnitTypeCd != null)
				return false;
		} else if (!emissionUnitTypeCd.equals(other.emissionUnitTypeCd))
			return false;
		if (emuId == null) {
			if (other.emuId != null)
				return false;
		} else if (!emuId.equals(other.emuId))
			return false;
		if (engineType == null) {
			if (other.engineType != null)
				return false;
		} else if (!engineType.equals(other.engineType))
			return false;
		if (equipmentType == null) {
			if (other.equipmentType != null)
				return false;
		} else if (!equipmentType.equals(other.equipmentType))
			return false;
		if (equipmentTypeOther == null) {
			if (other.equipmentTypeOther != null)
				return false;
		} else if (!equipmentTypeOther.equals(other.equipmentTypeOther))
			return false;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		if (firingType == null) {
			if (other.firingType != null)
				return false;
		} else if (!firingType.equals(other.firingType))
			return false;
		if (fuelHeatContent == null) {
			if (other.fuelHeatContent != null)
				return false;
		} else if (!fuelHeatContent.equals(other.fuelHeatContent))
			return false;
		if (fuelHeatContentUnitsCd == null) {
			if (other.fuelHeatContentUnitsCd != null)
				return false;
		} else if (!fuelHeatContentUnitsCd.equals(other.fuelHeatContentUnitsCd))
			return false;
		if (fuelType == null) {
			if (other.fuelType != null)
				return false;
		} else if (!fuelType.equals(other.fuelType))
			return false;
		if (fugitiveLeaks != other.fugitiveLeaks)
			return false;
		if (gasConsumptionRate == null) {
			if (other.gasConsumptionRate != null)
				return false;
		} else if (!gasConsumptionRate.equals(other.gasConsumptionRate))
			return false;
		if (heatInputRating == null) {
			if (other.heatInputRating != null)
				return false;
		} else if (!heatInputRating.equals(other.heatInputRating))
			return false;
		if (impressionArea == null) {
			if (other.impressionArea != null)
				return false;
		} else if (!impressionArea.equals(other.impressionArea))
			return false;
		if (incineratorType == null) {
			if (other.incineratorType != null)
				return false;
		} else if (!incineratorType.equals(other.incineratorType))
			return false;
		if (manufacturerName == null) {
			if (other.manufacturerName != null)
				return false;
		} else if (!manufacturerName.equals(other.manufacturerName))
			return false;
		if (materialDescription == null) {
			if (other.materialDescription != null)
				return false;
		} else if (!materialDescription.equals(other.materialDescription))
			return false;
		if (materialType == null) {
			if (other.materialType != null)
				return false;
		} else if (!materialType.equals(other.materialType))
			return false;
		if (materialTypeStored == null) {
			if (other.materialTypeStored != null)
				return false;
		} else if (!materialTypeStored.equals(other.materialTypeStored))
			return false;
		if (liquidMaterialTypeStored == null) {
			if (other.liquidMaterialTypeStored != null)
				return false;
		} else if (!liquidMaterialTypeStored.equals(other.liquidMaterialTypeStored))
			return false;
		if (materialTypeStoredDesc == null) {
			if (other.materialTypeStoredDesc != null)
				return false;
		} else if (!materialTypeStoredDesc.equals(other.materialTypeStoredDesc))
			return false;
		if (maxAmtOfElectrodeConsumed == null) {
			if (other.maxAmtOfElectrodeConsumed != null)
				return false;
		} else if (!maxAmtOfElectrodeConsumed.equals(other.maxAmtOfElectrodeConsumed))
			return false;
		if (maxAnnualThroughput == null) {
			if (other.maxAnnualThroughput != null)
				return false;
		} 
		if (maxAmtOfRubberRemoved == null) {
			if (other.maxAmtOfRubberRemoved != null)
				return false;
		} else if (!maxAmtOfRubberRemoved.equals(other.maxAmtOfRubberRemoved))
			return false;
		if (maxAmtOfRubberRemoved == null) {
			if (other.maxAmtOfRubberRemoved != null)
				return false;
		}
		else if (!maxAnnualThroughput.equals(other.maxAnnualThroughput))
			return false;
		if (maxAnnualUsage == null) {
			if (other.maxAnnualUsage != null)
				return false;
		} else if (!maxAnnualUsage.equals(other.maxAnnualUsage))
			return false;
		if (maxAnnualUsageAbs == null) {
			if (other.maxAnnualUsageAbs != null)
				return false;
		} else if (!maxAnnualUsageAbs.equals(other.maxAnnualUsageAbs))
			return false;
		if (maxAnnualWoodDustGenerated == null) {
			if (other.maxAnnualWoodDustGenerated != null)
				return false;
		} else if (!maxAnnualWoodDustGenerated.equals(other.maxAnnualWoodDustGenerated))
			return false;
		if (maxBurnerDesignRate == null) {
			if (other.maxBurnerDesignRate != null)
				return false;
		} else if (!maxBurnerDesignRate.equals(other.maxBurnerDesignRate))
			return false;
		if (maxDesignCapacity == null) {
			if (other.maxDesignCapacity != null)
				return false;
		} else if (!maxDesignCapacity.equals(other.maxDesignCapacity))
			return false;
		if (maximumAnnualThroughput == null) {
			if (other.maximumAnnualThroughput != null)
				return false;
		} else if (!maximumAnnualThroughput.equals(other.maximumAnnualThroughput))
			return false;
		if (maxNumberOfRoundsDetonated == null) {
			if (other.maxNumberOfRoundsDetonated != null)
				return false;
		} else if (!maxNumberOfRoundsDetonated
				.equals(other.maxNumberOfRoundsDetonated))
			return false;
		if (maxNumberOfTimesBlastMediaReclaimedForReuse == null) {
			if (other.maxNumberOfTimesBlastMediaReclaimedForReuse != null)
				return false;
		} else if (!maxNumberOfTimesBlastMediaReclaimedForReuse
				.equals(other.maxNumberOfTimesBlastMediaReclaimedForReuse))
			return false;
		if (maxOzoneGenerationRate == null) {
			if (other.maxOzoneGenerationRate != null)
				return false;
		} else if (!maxOzoneGenerationRate.equals(other.maxOzoneGenerationRate))
			return false;
		if (maxPctStyrene == null) {
			if (other.maxPctStyrene != null)
				return false;
		} else if (!maxPctStyrene.equals(other.maxPctStyrene))
			return false;
		if (maxPctVOC == null) {
			if (other.maxPctVOC != null)
				return false;
		} else if (!maxPctVOC.equals(other.maxPctVOC))
			return false;
		if (maxProductionRate == null) {
			if (other.maxProductionRate != null)
				return false;
		} else if (!maxProductionRate.equals(other.maxProductionRate))
			return false;
		if (maxThroughput == null) {
			if (other.maxThroughput != null)
				return false;
		} else if (!maxThroughput.equals(other.maxThroughput))
			return false;
		if (maxTimeOperated == null) {
			if (other.maxTimeOperated != null)
				return false;
		} else if (!maxTimeOperated.equals(other.maxTimeOperated))
			return false;
		if (metalType == null) {
			if (other.metalType != null)
				return false;
		} else if (!metalType.equals(other.metalType))
			return false;
		if (metalTypeOther == null) {
			if (other.metalTypeOther != null)
				return false;
		} else if (!metalTypeOther.equals(other.metalTypeOther))
			return false;
		if (minDesignCapacity == null) {
			if (other.minDesignCapacity != null)
				return false;
		} else if (!minDesignCapacity.equals(other.minDesignCapacity))
			return false;
		if (modelNameNumber == null) {
			if (other.modelNameNumber != null)
				return false;
		} else if (!modelNameNumber.equals(other.modelNameNumber))
			return false;
		if (nameAndTypeOfMaterial == null) {
			if (other.nameAndTypeOfMaterial != null)
				return false;
		} else if (!nameAndTypeOfMaterial.equals(other.nameAndTypeOfMaterial))
			return false;
		if (namePlateRating == null) {
			if (other.namePlateRating != null)
				return false;
		} else if (!namePlateRating.equals(other.namePlateRating))
			return false;
		if (operationType == null) {
			if (other.operationType != null)
				return false;
		} else if (!operationType.equals(other.operationType))
			return false;
		if (otherTypeOfContaminantBeingTreated == null) {
			if (other.otherTypeOfContaminantBeingTreated != null)
				return false;
		} else if (!otherTypeOfContaminantBeingTreated.equals(other.otherTypeOfContaminantBeingTreated))
			return false;
		if (pageViewId == null) {
			if (other.pageViewId != null)
				return false;
		} else if (!pageViewId.equals(other.pageViewId))
			return false;
		if (pilotGasVolume == null) {
			if (other.pilotGasVolume != null)
				return false;
		} else if (!pilotGasVolume.equals(other.pilotGasVolume))
			return false;
		if (plantType == null) {
			if (other.plantType != null)
				return false;
		} else if (!plantType.equals(other.plantType))
			return false;
		if (platingLineFlag == null) {
			if (other.platingLineFlag != null)
				return false;
		} else if (!platingLineFlag.equals(other.platingLineFlag))
			return false;
		if (platingType == null) {
			if (other.platingType != null)
				return false;
		} else if (!platingType.equals(other.platingType))
			return false;
		if (platingTypeOther == null) {
			if (other.platingTypeOther != null)
				return false;
		} else if (!platingTypeOther.equals(other.platingTypeOther))
			return false;
		if (powerRatingFlag == null) {
			if (other.powerRatingFlag != null)
				return false;
		} else if (!powerRatingFlag.equals(other.powerRatingFlag))
			return false;
		if (powerSource == null) {
			if (other.powerSource != null)
				return false;
		} else if (!powerSource.equals(other.powerSource))
			return false;
		if (pressType == null) {
			if (other.pressType != null)
				return false;
		} else if (!pressType.equals(other.pressType))
			return false;
		if (pressTypeOther == null) {
			if (other.pressTypeOther != null)
				return false;
		} else if (!pressTypeOther.equals(other.pressTypeOther))
			return false;
		if (primaryChamberOpTemp == null) {
			if (other.primaryChamberOpTemp != null)
				return false;
		} else if (!primaryChamberOpTemp.equals(other.primaryChamberOpTemp))
			return false;
		if (primaryFuelType == null) {
			if (other.primaryFuelType != null)
				return false;
		} else if (!primaryFuelType.equals(other.primaryFuelType))
			return false;
		if (producedMaterialType == null) {
			if (other.producedMaterialType != null)
				return false;
		} else if (!producedMaterialType.equals(other.producedMaterialType))
			return false;
		if (secondaryChamberOpTemp == null) {
			if (other.secondaryChamberOpTemp != null)
				return false;
		} else if (!secondaryChamberOpTemp.equals(other.secondaryChamberOpTemp))
			return false;
		if (secondaryFuelType == null) {
			if (other.secondaryFuelType != null)
				return false;
		} else if (!secondaryFuelType.equals(other.secondaryFuelType))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		if (serialNumberEffectiveDate == null) {
			if (other.serialNumberEffectiveDate != null)
				return false;
		} else if (!serialNumberEffectiveDate.equals(other.serialNumberEffectiveDate))
			return false;
		if (siteRating == null) {
			if (other.siteRating != null)
				return false;
		} else if (!siteRating.equals(other.siteRating))
			return false;
		if (siteRatingUnitCd == null) {
			if (other.siteRatingUnitCd != null)
				return false;
		} else if (!siteRatingUnitCd.equals(other.siteRatingUnitCd))
			return false;
		if (solventType == null) {
			if (other.solventType != null)
				return false;
		} else if (!solventType.equals(other.solventType))
			return false;
		if (submergedFillPipeFlag == null) {
			if (other.submergedFillPipeFlag != null)
				return false;
		} else if (!submergedFillPipeFlag.equals(other.submergedFillPipeFlag))
			return false;
		if (substrateBlasted == null) {
			if (other.substrateBlasted != null)
				return false;
		} else if (!substrateBlasted.equals(other.substrateBlasted))
			return false;
		if (substrateFeedMethod == null) {
			if (other.substrateFeedMethod != null)
				return false;
		} else if (!substrateFeedMethod.equals(other.substrateFeedMethod))
			return false;
		if (substrateRemoved == null) {
			if (other.substrateRemoved != null)
				return false;
		} else if (!substrateRemoved.equals(other.substrateRemoved))
			return false;
		if (substrateRemovedOther == null) {
			if (other.substrateRemovedOther != null)
				return false;
		} else if (!substrateRemovedOther.equals(other.substrateRemovedOther))
			return false;
		if (tankLocation == null) {
			if (other.tankLocation != null)
				return false;
		} else if (!tankLocation.equals(other.tankLocation))
			return false;
		if (typeOfCoatingOperation == null) {
			if (other.typeOfCoatingOperation != null)
				return false;
		} else if (!typeOfCoatingOperation.equals(other.typeOfCoatingOperation))
			return false;		
		if(typeOfContaminantBeingTreated==null){
			if(other.typeOfContaminantBeingTreated!=null)
				return false;				
		}else if(!typeOfContaminantBeingTreated.equals(other.typeOfContaminantBeingTreated))
				return false;
		if (typeOfMaterialBeingCoated == null) {
			if (other.typeOfMaterialBeingCoated != null)
				return false;
		} else if (!typeOfMaterialBeingCoated.equals(other.typeOfMaterialBeingCoated))
			return false;
		if(typeOfMaterialBeingCoatedOther==null){
			if(other.typeOfMaterialBeingCoatedOther!=null)
				return false;
		} else if(!typeOfMaterialBeingCoatedOther.equals(other.typeOfMaterialBeingCoatedOther))
			return false;
		if(typeOfProductBeingCoated==null){
			if(other.typeOfProductBeingCoated!=null)
				return false;
		} else if(!typeOfProductBeingCoated.equals(other.typeOfProductBeingCoated))
			return false;
	//////	
		if (typeOfExplosive == null) {
			if (other.typeOfExplosive != null)
				return false;
		} else if (!typeOfExplosive.equals(other.typeOfExplosive))
			return false;
		if (typeOfExplosiveOther == null) {
			if (other.typeOfExplosiveOther != null)
				return false;
		} else if (!typeOfExplosiveOther.equals(other.typeOfExplosiveOther))
			return false;
		if (typeOfUse==null){
			if(other.typeOfUse!=null)
				return false;
			else if (!typeOfUse.equals(other.typeOfUse))
				return false;
		}
		if (unitCd == null) {
			if (other.unitCd != null)
				return false;
		} else if (!unitCd.equals(other.unitCd))
			return false;
		if (unitDesc == null) {
			if (other.unitDesc != null)
				return false;
		} else if (!unitDesc.equals(other.unitDesc))
			return false;
		if (unitType == null) {
			if (other.unitType != null)
				return false;
		} else if (!unitType.equals(other.unitType))
			return false;
		if (unitTypeOther == null) {
			if (other.unitTypeOther != null)
				return false;
		} else if (!unitTypeOther.equals(other.unitTypeOther))
			return false;
		if (vesselHeated != other.vesselHeated)
			return false;
		if (vesselType == null) {
			if (other.vesselType != null)
				return false;
		} else if (!vesselType.equals(other.vesselType))
			return false;
		if (VOCEmissionRate == null) {
			if (other.VOCEmissionRate != null)
				return false;
		} else if (!VOCEmissionRate.equals(other.VOCEmissionRate))
			return false;
		if (weldingProcess == null) {
			if (other.weldingProcess != null)
				return false;
		} else if (!weldingProcess.equals(other.weldingProcess))
			return false;
		if (components == null){
			if (other.components != null)
				return false;
		} else if (other.components == null){
			return false;
		} else {
			for (Component c1: components){
				for (Component c2: other.components){
					if (c1.getComponentCd() != null && c1.getComponentCd().equals(c2.getComponentCd())){
						if (!c1.equals(c2)){
							return false;
						}
						break;
					}
				}
			}
		}
		return true;
	}
	
	public String getLiquidMaterialTypeStored() {
		return liquidMaterialTypeStored;
	}
	
	public void setLiquidMaterialTypeStored(String liquidMaterialTypeStored) {
		this.liquidMaterialTypeStored = liquidMaterialTypeStored;
	}
	
	//Fugitive specific


	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}
	
	public static List<Component> initializeComponentList(Integer emuId){
		List<Component> ret = new ArrayList<Component>();
		
		List<FugitiveComponentDef> fugComponentDefs = FugitiveComponentDef.getFugComponentDefs();

		for (FugitiveComponentDef fugComponentDef : fugComponentDefs){
			Component cpt = new Component(emuId, fugComponentDef.getCode());
			ret.add(cpt);
		}
		return ret;
	}
	
	
	public boolean isFugitiveLeaks() {
		return fugitiveLeaks;
	}

	public void setFugitiveLeaks(boolean fugitiveLeaks) {
		this.fugitiveLeaks = fugitiveLeaks;
	}
	
	public Short getEqptCount() {
		return eqptCount;
	}

	public void setEqptCount(Short eqptCount) {
		this.eqptCount = eqptCount;
	}
	
}
