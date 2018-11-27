package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.cabo.data.jbo.servlet.event.SetRegionEventHandler;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUFugitiveLeaks;
import us.oh.state.epa.stars2.def.AppEUFUGEmissionTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeFUG extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeFUG:";
	/*public static String PAGE_VIEW_ID_PREFIX_HAUL_ROAD = "nsrAppEuTypeFugHaulRoad:";
	public static String PAGE_VIEW_ID_PREFIX_EXPOSED_ACREAGE = "nsrAppEuTypeFugExposedAcreage:";
	public static String PAGE_VIEW_ID_PREFIX_STOCKPILE = "nsrAppEuTypeFugStockpile:";
	public static String PAGE_VIEW_ID_PREFIX_BLASTING = "nsrAppEuTypeBlasting:";
	public static String PAGE_VIEW_ID_PREFIX_FUG_LEAKS_OIL_GAS = "nsrAppEuTypeFugitiveLeaksOilGas:";
	public static String PAGE_VIEW_ID_OTHER = "nsrAppEuTypeFugOther:";*/
		private String fugitiveEmissionTypeCd;
	private Float maxDistanceMateriaHauled;
	private String truckType1;
	private Integer type1TrucksCount;
	private Integer type1TrucksCapacity;
	private Integer type1TrucksEmptyWeight;
	private String truckType2;
	private Integer type2TrucksCount;
	private Integer type2TrucksCapacity;
	private Integer type2TrucksEmptyWeight;
	private String truckType3;
	private Integer type3TrucksCount;
	private Integer type3TrucksCapacity;
	private Integer type3TrucksEmptyWeight;
	private String truckType4;
	private Integer type4TrucksCount;
	private Integer type4TrucksCapacity;
	private Integer type4TrucksEmptyWeight;
	private Float acreageSubjectToWindErosion;
	private String stockPileTypeCd;
	private Float materialAddedRemovedFromPileDay;
	private Float materialAddedRemovedFromPileYr;
	private Integer stockPilesCount;
	private Integer stockPileSize;
	private String stockPileUnitCd;
	private Integer blastsPerYearNumber;
	private String blastingAgentUsedType;
	private Float blastingAgentUsedAmount;
	private Float blastHorizontalArea;
	private String materialBlastedTypeCd;
	private String fugitiveSourceDescription;
	public NSRApplicationEUTypeFUG() {
		super();
	}

	public NSRApplicationEUTypeFUG(NSRApplicationEUTypeFUG old) {
		super(old);
		if (old != null) {
			setFugitiveEmissionTypeCd(old.getFugitiveEmissionTypeCd());
			setMaxDistanceMateriaHauled(old.getMaxDistanceMateriaHauled());
			setTruckType1(old.getTruckType1());
			setType1TrucksCount(old.getType1TrucksCount());
			setType1TrucksCapacity(old.getType1TrucksCapacity());
			setType1TrucksEmptyWeight(old.getType1TrucksEmptyWeight());
			setTruckType2(old.getTruckType2());
			setType2TrucksCount(old.getType2TrucksCount());
			setType2TrucksCapacity(old.getType2TrucksCapacity());
			setType2TrucksEmptyWeight(old.getType2TrucksEmptyWeight());
			setTruckType3(old.getTruckType3());
			setType3TrucksCount(old.getType3TrucksCount());
			setType3TrucksCapacity(old.getType3TrucksCapacity());
			setType3TrucksEmptyWeight(old.getType3TrucksEmptyWeight());
			setTruckType4(old.getTruckType4());
			setType4TrucksCount(old.getType4TrucksCount());
			setType4TrucksCapacity(old.getType4TrucksCapacity());
			setType4TrucksEmptyWeight(old.getType4TrucksEmptyWeight());
			setAcreageSubjectToWindErosion(old.getAcreageSubjectToWindErosion());
			setStockPileTypeCd(old.getStockPileTypeCd());
			setMaterialAddedRemovedFromPileDay(old.getMaterialAddedRemovedFromPileDay());
			setMaterialAddedRemovedFromPileYr(old.getMaterialAddedRemovedFromPileYr());
			setStockPilesCount(old.getStockPilesCount());
			setStockPileSize(old.getStockPileSize());
			setStockPileUnitCd(old.getStockPileUnitCd());
			setBlastsPerYearNumber(old.getBlastsPerYearNumber());
			setBlastingAgentUsedType(old.getBlastingAgentUsedType());
			setBlastingAgentUsedAmount(old.getBlastingAgentUsedAmount());
			setBlastHorizontalArea(old.getBlastHorizontalArea());
			setMaterialBlastedTypeCd(old.getMaterialBlastedTypeCd());
			setFugitiveSourceDescription(old.getFugitiveSourceDescription());
		
		}
	}

	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setFugitiveEmissionTypeCd(rs.getString("fugitive_emission_type_cd"));
		setMaxDistanceMateriaHauled(AbstractDAO.getFloat(rs,"max_distance_materia_hauled"));
		setTruckType1(rs.getString("truck_type_1"));
		setType1TrucksCount(AbstractDAO.getInteger(rs ,"number_of_type_1_trucks"));
		setType1TrucksCapacity(AbstractDAO.getInteger(rs ,"capacity_of_type_1_trucks"));
		setType1TrucksEmptyWeight(AbstractDAO.getInteger(rs ,"empty_weight_of_type_1_trucks"));
		setTruckType2(rs.getString("truck_type_2"));
		setType2TrucksCount(AbstractDAO.getInteger(rs ,"number_of_type_2_trucks"));
		setType2TrucksCapacity(AbstractDAO.getInteger(rs ,"capacity_of_type_2_trucks"));
		setType2TrucksEmptyWeight(AbstractDAO.getInteger(rs ,"empty_weight_of_type_2_trucks"));
		setTruckType3(rs.getString("truck_type_3"));
		setType3TrucksCount(AbstractDAO.getInteger(rs ,"number_of_type_3_trucks"));
		setType3TrucksCapacity(AbstractDAO.getInteger(rs ,"capacity_of_type_3_trucks"));
		setType3TrucksEmptyWeight(AbstractDAO.getInteger(rs ,"empty_weight_of_type_3_trucks"));
		setTruckType4(rs.getString("truck_type_4"));
		setType4TrucksCount(AbstractDAO.getInteger(rs ,"number_of_type_4_trucks"));
		setType4TrucksCapacity(AbstractDAO.getInteger(rs ,"capacity_of_type_4_trucks"));
		setType4TrucksEmptyWeight(AbstractDAO.getInteger(rs ,"empty_weight_of_type_4_trucks"));
		setAcreageSubjectToWindErosion(AbstractDAO.getFloat(rs,"acreage_subject_to_wind_erosion"));
		setStockPileTypeCd(rs.getString("stockpile_type_cd"));
		setMaterialAddedRemovedFromPileDay(AbstractDAO.getFloat(rs,"material_added_removed_from_pile_day"));
		setMaterialAddedRemovedFromPileYr(AbstractDAO.getFloat(rs,"material_added_removed_from_pile_yr"));
		setStockPilesCount(AbstractDAO.getInteger(rs ,"number_of_stockpiles"));
		setStockPileSize(AbstractDAO.getInteger(rs ,"size_of_stockpile"));
		setStockPileUnitCd(rs.getString("units_stockpile_cd"));
		setBlastsPerYearNumber(AbstractDAO.getInteger(rs, "number_of_blasts_per_year"));
		setBlastingAgentUsedType(rs.getString("blasting_agent_used_type"));
		setBlastingAgentUsedAmount(AbstractDAO.getFloat(rs, "amount_of_blasting_agent_used"));
		setBlastHorizontalArea(AbstractDAO.getFloat(rs, "horizontal_area_of_blast"));
		setMaterialBlastedTypeCd(rs.getString("material_blasted_type_cd"));
		setFugitiveSourceDescription(rs.getString("detailed_description_of_fugitive_source"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((fugitiveEmissionTypeCd == null) ? 0 : fugitiveEmissionTypeCd.hashCode());
		result = prime * result
				+ ((truckType1 == null) ? 0 : truckType1.hashCode());
		result = prime * result
				+ ((type1TrucksCount == null) ? 0 : type1TrucksCount.hashCode());
		result = prime * result
				+ ((type1TrucksCapacity == null) ? 0 : type1TrucksCapacity.hashCode());
		result = prime * result
				+ ((type1TrucksEmptyWeight == null) ? 0 : type1TrucksEmptyWeight.hashCode());
		result = prime * result
				+ ((truckType2 == null) ? 0 : truckType2.hashCode());
		result = prime * result
				+ ((type2TrucksCount == null) ? 0 : type2TrucksCount.hashCode());
		result = prime * result
				+ ((type2TrucksCapacity == null) ? 0 : type2TrucksCapacity.hashCode());
		result = prime * result
				+ ((type2TrucksEmptyWeight == null) ? 0 : type2TrucksEmptyWeight.hashCode());
		result = prime * result
				+ ((truckType3 == null) ? 0 : truckType3.hashCode());
		result = prime * result
				+ ((type3TrucksCount == null) ? 0 : type3TrucksCount.hashCode());
		result = prime * result
				+ ((type3TrucksCapacity == null) ? 0 : type3TrucksCapacity.hashCode());
		result = prime * result
				+ ((type3TrucksEmptyWeight == null) ? 0 : type3TrucksEmptyWeight.hashCode());
		result = prime * result
				+ ((truckType4 == null) ? 0 : truckType4.hashCode());
		result = prime * result
				+ ((type4TrucksCount == null) ? 0 : type4TrucksCount.hashCode());
		result = prime * result
				+ ((type4TrucksCapacity == null) ? 0 : type4TrucksCapacity.hashCode());
		result = prime * result
				+ ((type4TrucksEmptyWeight == null) ? 0 : type4TrucksEmptyWeight.hashCode());
		result = prime * result
				+ ((acreageSubjectToWindErosion == null) ? 0 : acreageSubjectToWindErosion.hashCode());
		result = prime * result
				+ ((stockPileTypeCd == null) ? 0 : stockPileTypeCd.hashCode());
		result = prime * result
				+ ((materialAddedRemovedFromPileDay == null) ? 0 : materialAddedRemovedFromPileDay.hashCode());
		result = prime * result
				+ ((materialAddedRemovedFromPileYr == null) ? 0 : materialAddedRemovedFromPileYr.hashCode());
		result = prime * result
				+ ((stockPilesCount == null) ? 0 : stockPilesCount.hashCode());
		result = prime * result
				+ ((stockPileSize == null) ? 0 : stockPileSize.hashCode());
		result = prime * result
				+ ((stockPileUnitCd == null) ? 0 : stockPileUnitCd.hashCode());
		result = prime * result
				+ ((blastsPerYearNumber == null) ? 0 : blastsPerYearNumber.hashCode());
		result = prime * result
				+ ((blastingAgentUsedType == null) ? 0 : blastingAgentUsedType.hashCode());
		result = prime * result
				+ ((blastingAgentUsedAmount == null) ? 0 : blastingAgentUsedAmount.hashCode());
		result = prime * result
				+ ((blastHorizontalArea == null) ? 0 : blastHorizontalArea.hashCode());
		result = prime * result
				+ ((materialBlastedTypeCd == null) ? 0 : materialBlastedTypeCd.hashCode());
		result = prime * result
				+ ((fugitiveSourceDescription == null) ? 0 : fugitiveSourceDescription.hashCode());
		
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
		final NSRApplicationEUTypeFUG other = (NSRApplicationEUTypeFUG) obj;
		if (fugitiveEmissionTypeCd == null) {
			if (other.fugitiveEmissionTypeCd != null)
				return false;
		} else if (!fugitiveEmissionTypeCd.equals(other.fugitiveEmissionTypeCd))
			return false;
		if (maxDistanceMateriaHauled == null) {
			if (other.maxDistanceMateriaHauled != null)
				return false;
		} else if (!maxDistanceMateriaHauled.equals(other.maxDistanceMateriaHauled))
			return false;
		if (truckType1 == null) {
			if (other.truckType1 != null)
				return false;
		} else if (!truckType1.equals(other.truckType1))
			return false;
		if (type1TrucksCount == null) {
			if (other.type1TrucksCount != null)
				return false;
		} else if (!type1TrucksCount.equals(other.type1TrucksCount))
			return false;
		if (type1TrucksCapacity == null) {
			if (other.type1TrucksCapacity != null)
				return false;
		} else if (!type1TrucksCapacity.equals(other.type1TrucksCapacity))
			return false;
		if (type1TrucksEmptyWeight == null) {
			if (other.type1TrucksEmptyWeight != null)
				return false;
		} else if (!type1TrucksEmptyWeight.equals(other.type1TrucksEmptyWeight))
			return false;
		if (truckType2 == null) {
			if (other.truckType2 != null)
				return false;
		} else if (!truckType2.equals(other.truckType2))
			return false;
		if (type2TrucksCount == null) {
			if (other.type2TrucksCount != null)
				return false;
		} else if (!type2TrucksCount.equals(other.type2TrucksCount))
			return false;
		if (type2TrucksCapacity == null) {
			if (other.type2TrucksCapacity != null)
				return false;
		} else if (!type2TrucksCapacity.equals(other.type2TrucksCapacity))
			return false;
		if (type2TrucksEmptyWeight == null) {
			if (other.type2TrucksEmptyWeight != null)
				return false;
		} else if (!type2TrucksEmptyWeight.equals(other.type2TrucksEmptyWeight))
			return false;
		if (truckType3 == null) {
			if (other.truckType3 != null)
				return false;
		} else if (!truckType3.equals(other.truckType3))
			return false;
		if (type3TrucksCount == null) {
			if (other.type3TrucksCount != null)
				return false;
		} else if (!type3TrucksCount.equals(other.type3TrucksCount))
			return false;
		if (type3TrucksCapacity == null) {
			if (other.type3TrucksCapacity != null)
				return false;
		} else if (!type3TrucksCapacity.equals(other.type3TrucksCapacity))
			return false;
		if (type3TrucksEmptyWeight == null) {
			if (other.type3TrucksEmptyWeight != null)
				return false;
		} else if (!type3TrucksEmptyWeight.equals(other.type3TrucksEmptyWeight))
			return false;
		if (truckType4 == null) {
			if (other.truckType4 != null)
				return false;
		} else if (!truckType4.equals(other.truckType4))
			return false;
		if (type4TrucksCount == null) {
			if (other.type4TrucksCount != null)
				return false;
		} else if (!type4TrucksCount.equals(other.type4TrucksCount))
			return false;
		if (type4TrucksCapacity == null) {
			if (other.type4TrucksCapacity != null)
				return false;
		} else if (!type4TrucksCapacity.equals(other.type4TrucksCapacity))
			return false;
		if (type4TrucksEmptyWeight == null) {
			if (other.type4TrucksEmptyWeight != null)
				return false;
		} else if (!type4TrucksEmptyWeight.equals(other.type4TrucksEmptyWeight))
			return false;
		if (acreageSubjectToWindErosion == null) {
			if (other.acreageSubjectToWindErosion != null)
				return false;
		} else if (!acreageSubjectToWindErosion.equals(other.acreageSubjectToWindErosion))
			return false;
		if (stockPileTypeCd == null) {
			if (other.stockPileTypeCd != null)
				return false;
		} else if (!stockPileTypeCd.equals(other.stockPileTypeCd))
			return false;
		if (materialAddedRemovedFromPileDay == null) {
			if (other.materialAddedRemovedFromPileDay != null)
				return false;
		} else if (!materialAddedRemovedFromPileDay.equals(other.materialAddedRemovedFromPileDay))
			return false;
		if (materialAddedRemovedFromPileYr == null) {
			if (other.materialAddedRemovedFromPileYr != null)
				return false;
		} else if (!materialAddedRemovedFromPileYr.equals(other.materialAddedRemovedFromPileYr))
			return false;
		if (stockPilesCount == null) {
			if (other.stockPilesCount != null)
				return false;
		} else if (!stockPilesCount.equals(other.stockPilesCount))
			return false;
		if (stockPileSize == null) {
			if (other.stockPileSize != null)
				return false;
		} else if (!stockPileSize.equals(other.stockPileSize))
			return false;
		if (stockPileUnitCd == null) {
			if (other.stockPileUnitCd != null)
				return false;
		} else if (!stockPileUnitCd.equals(other.stockPileUnitCd))
			return false;
		if (blastsPerYearNumber == null) {
			if (other.blastsPerYearNumber != null)
				return false;
		} else if (!blastsPerYearNumber.equals(other.blastsPerYearNumber))
			return false;
		if (blastingAgentUsedType == null) {
			if (other.blastingAgentUsedType != null)
				return false;
		} else if (!blastingAgentUsedType.equals(other.blastingAgentUsedType))
			return false;
		if (blastingAgentUsedAmount == null) {
			if (other.blastingAgentUsedAmount != null)
				return false;
		} else if (!blastingAgentUsedAmount.equals(other.blastingAgentUsedAmount))
			return false;
		if (blastHorizontalArea == null) {
			if (other.blastHorizontalArea != null)
				return false;
		} else if (!blastHorizontalArea.equals(other.blastHorizontalArea))
			return false;
		if (materialBlastedTypeCd == null) {
			if (other.materialBlastedTypeCd != null)
				return false;
		} else if (!materialBlastedTypeCd.equals(other.materialBlastedTypeCd))
			return false;
		if (fugitiveSourceDescription == null) {
			if (other.fugitiveSourceDescription != null)
				return false;
		} else if (!fugitiveSourceDescription.equals(other.fugitiveSourceDescription))
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
		requiredField(this.fugitiveEmissionTypeCd, PAGE_VIEW_ID_PREFIX + "fugitiveEmissionTypeCd",
				"Type of Fugitive Emission", "fugitiveEmissionTypeCd");
		if (!Utility.isNullOrEmpty(this.fugitiveEmissionTypeCd))
		{
			if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.HAUL_ROAD))
			{
				requiredField(this.maxDistanceMateriaHauled, PAGE_VIEW_ID_PREFIX + "maxDistanceMateriaHauled",
						"Maximum Distance Material will be Hauled (or until Reaching Pavement) (miles)", "maxDistanceMateriaHauled");

				requiredField(this.truckType1, PAGE_VIEW_ID_PREFIX + "truckType1", 
						"Truck Type 1", "truckType1");

				requiredField(this.type1TrucksCount, PAGE_VIEW_ID_PREFIX
						+ "type1TrucksCount", "Number of Type 1 Trucks", "type1TrucksCount");

				requiredField(this.type1TrucksCapacity,	PAGE_VIEW_ID_PREFIX + "type1TrucksCapacity",
						"Capacity of Type 1 Trucks (tons)", "type1TrucksCapacity");

				requiredField(this.type1TrucksEmptyWeight, PAGE_VIEW_ID_PREFIX + "type1TrucksEmptyWeight",
						"Empty Weight of Type 1 Trucks (lbs)", "type1TrucksEmptyWeight");
				
			} else if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.EXPOSED_ACREAGE)) {
				
			requiredField(this.acreageSubjectToWindErosion,	PAGE_VIEW_ID_PREFIX + "acreageSubjectToWindErosion",
						"Acreage Subject to Wind Erosion (acres)", "acreageSubjectToWindErosion");
			} else if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.STOCKPILE)) {				
				requiredField(this.stockPileTypeCd, PAGE_VIEW_ID_PREFIX + "stockPileTypeCd",
						"Type of Stockpile", "stockPileTypeCd");

				requiredField(this.stockPilesCount,	PAGE_VIEW_ID_PREFIX + "stockPilesCount", 
						"Number of Stockpiles", "stockPilesCount");

				requiredField(this.stockPileSize, PAGE_VIEW_ID_PREFIX + "stockPileSize", 
						"Size of Stockpile", "stockPileSize");

				requiredField(this.stockPileUnitCd,	PAGE_VIEW_ID_PREFIX + "stockPileUnitCd", 
						"Units", "stockPileUnitCd");
			} else if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.BLASTING)) {
				requiredField(this.blastsPerYearNumber, PAGE_VIEW_ID_PREFIX + "blastsPerYearNumber",
						"Number of Blasts per year", "blastsPerYearNumber");
				requiredField(this.blastingAgentUsedType,	PAGE_VIEW_ID_PREFIX + "blastingAgentUsedType", 
						"Type of Blasting Agent Used", "blastingAgentUsedType");
				requiredField(this.blastingAgentUsedAmount, PAGE_VIEW_ID_PREFIX + "blastingAgentUsedAmount",
						"Amount of Blasting Agent Used (tons/yr)", "blastingAgentUsedAmount");
				requiredField(this.blastHorizontalArea,	PAGE_VIEW_ID_PREFIX + "blastHorizontalArea",
						"Horizontal Area of Blast (cu. ft)", "blastHorizontalArea");
				requiredField(this.materialBlastedTypeCd, PAGE_VIEW_ID_PREFIX + "materialBlastedTypeCd",
						"Type of material blasted", "materialBlastedTypeCd");
			} else if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.OTHER)) {
				requiredField(this.fugitiveSourceDescription, PAGE_VIEW_ID_PREFIX + "fugitiveSourceDescription",
						"Detailed Description of Fugitive Source", "fugitiveSourceDescription");
			}
		}
	}
	
	public void validateRanges() {
		if (!Utility.isNullOrEmpty(this.fugitiveEmissionTypeCd))
		{
			if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.HAUL_ROAD))
			{
				checkRangeValues(maxDistanceMateriaHauled, new Float(0.01), Float.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "maxDistanceMateriaHauled",
						"Maximum Distance Material will be Hauled (or until Reaching Pavement) (miles)");
				checkRangeValues(type1TrucksCount, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type1TrucksCount",
						"Number of Type 1 Trucks");
				checkRangeValues(type1TrucksCapacity, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type1TrucksCapacity",
						"Capacity of Type 1 Trucks (tons)");
				checkRangeValues(type1TrucksEmptyWeight, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type1TrucksEmptyWeight",
						"Empty Weight of Type 1 Trucks (lbs)");
				checkRangeValues(type2TrucksCount, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type2TrucksCount",
						"Number of Type 2 Trucks");
				checkRangeValues(type2TrucksCapacity, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type2TrucksCapacity",
						"Capacity of Type 2 Trucks (tons)");
				checkRangeValues(type2TrucksEmptyWeight, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type2TrucksEmptyWeight",
						"Empty Weight of Type 2 Trucks (lbs)");

				checkRangeValues(type3TrucksCount, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type3TrucksCount",
						"Number of Type 3 Trucks");
				checkRangeValues(type3TrucksCapacity, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type3TrucksCapacity",
						"Capacity of Type 3 Trucks (tons)");
				checkRangeValues(type3TrucksEmptyWeight, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type3TrucksEmptyWeight",
						"Empty Weight of Type 3 Trucks (lbs)");

				checkRangeValues(type4TrucksCount, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type4TrucksCount",
						"Number of Type 4 Trucks");
				checkRangeValues(type1TrucksCapacity, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type4TrucksCapacity",
						"Capacity of Type 4 Trucks (tons)");
				checkRangeValues(type4TrucksEmptyWeight, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "type4TrucksEmptyWeight",
						"Empty Weight of Type 4 Trucks (lbs)");
			} else if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.EXPOSED_ACREAGE)) {

				checkRangeValues(acreageSubjectToWindErosion, new Float(0.01), Float.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "acreageSubjectToWindErosion",
						"Acreage Subject to Wind Erosion (acres)");
			} else if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.STOCKPILE)) {		
				checkRangeValues(materialAddedRemovedFromPileDay, new Float(0.01), Float.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "materialAddedRemovedFromPileDay",
						"Material Added/Removed from Pile (tons/day)");
				checkRangeValues(materialAddedRemovedFromPileYr, new Float(0.01), Float.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "materialAddedRemovedFromPileYr",
						"Material Added/Removed from Pile (tons/yr)");
				checkRangeValues(stockPilesCount, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "stockPilesCount",
						"Number of Stockpiles");
				checkRangeValues(stockPileSize, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "stockPileSize",
						"Size of Stockpile");
			} else if (this.fugitiveEmissionTypeCd.equals(AppEUFUGEmissionTypeDef.BLASTING)) {
				checkRangeValues(blastsPerYearNumber, new Integer(1), Integer.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "blastsPerYearNumber",
						"Number of Blasts per year");
				checkRangeValues(blastingAgentUsedAmount, new Float(0.01), Float.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "blastingAgentUsedAmount",
						"Amount of Blasting Agent Used (tons/yr)");
				checkRangeValues(blastHorizontalArea, new Float(0.01), Float.MAX_VALUE,
						PAGE_VIEW_ID_PREFIX + "blastHorizontalArea",
						"Horizontal Area of Blast (cu. ft)");
			} 
		}
	}

	public String getFugitiveEmissionTypeCd() {
		return fugitiveEmissionTypeCd;
	}

	public void setFugitiveEmissionTypeCd(String fugitiveEmissionTypeCd) {
		this.fugitiveEmissionTypeCd = fugitiveEmissionTypeCd;
		if (getFugitiveEmissionTypeCd()!= null)
		{
			if (getFugitiveEmissionTypeCd().equals(AppEUFUGEmissionTypeDef.HAUL_ROAD)) {
				resetFugutiveTypeExposedAcreageFields();
				resetFugutiveTypeStockpileFields();
				resetFugutiveTypeBlastingFields();
				resetFugutiveTypeFugitiveOtherFields();
			} else if (getFugitiveEmissionTypeCd().equals(AppEUFUGEmissionTypeDef.EXPOSED_ACREAGE)) {
				resetFugutiveTypeHaulRoadFields();
				resetFugutiveTypeStockpileFields();
				resetFugutiveTypeBlastingFields();
				resetFugutiveTypeFugitiveOtherFields();
			} else if (getFugitiveEmissionTypeCd().equals(AppEUFUGEmissionTypeDef.STOCKPILE)) {
				resetFugutiveTypeHaulRoadFields();
				resetFugutiveTypeExposedAcreageFields();
				resetFugutiveTypeBlastingFields();
				resetFugutiveTypeFugitiveOtherFields();
			} else if (getFugitiveEmissionTypeCd().equals(AppEUFUGEmissionTypeDef.BLASTING)) {
				resetFugutiveTypeHaulRoadFields();
				resetFugutiveTypeExposedAcreageFields();
				resetFugutiveTypeStockpileFields();
				resetFugutiveTypeFugitiveOtherFields();
			}else if (getFugitiveEmissionTypeCd().equals(AppEUFUGEmissionTypeDef.FIGUTIVE_LEAK_AT_OG)) {
				resetFugutiveTypeHaulRoadFields();
				resetFugutiveTypeExposedAcreageFields();
				resetFugutiveTypeStockpileFields();
				resetFugutiveTypeBlastingFields();
				resetFugutiveTypeFugitiveOtherFields();
			} else if (getFugitiveEmissionTypeCd().equals(AppEUFUGEmissionTypeDef.OTHER)) {
				resetFugutiveTypeHaulRoadFields();
				resetFugutiveTypeExposedAcreageFields();
				resetFugutiveTypeStockpileFields();
				resetFugutiveTypeBlastingFields();
			}
		}

	}

	public Float getMaxDistanceMateriaHauled() {
		return maxDistanceMateriaHauled;
	}

	public void setMaxDistanceMateriaHauled(Float maxDistanceMateriaHauled) {
		this.maxDistanceMateriaHauled = maxDistanceMateriaHauled;
	}

	public String getTruckType1() {
		return truckType1;
	}

	public void setTruckType1(String truckType1) {
		this.truckType1 = truckType1;
	}

	public Integer getType1TrucksCount() {
		return type1TrucksCount;
	}

	public void setType1TrucksCount(Integer type1TrucksCount) {
		this.type1TrucksCount = type1TrucksCount;
	}

	public Integer getType1TrucksCapacity() {
		return type1TrucksCapacity;
	}

	public void setType1TrucksCapacity(Integer type1TrucksCapacity) {
		this.type1TrucksCapacity = type1TrucksCapacity;
	}

	public Integer getType1TrucksEmptyWeight() {
		return type1TrucksEmptyWeight;
	}

	public void setType1TrucksEmptyWeight(Integer type1TrucksEmptyWeight) {
		this.type1TrucksEmptyWeight = type1TrucksEmptyWeight;
	}

	public String getTruckType2() {
		return truckType2;
	}

	public void setTruckType2(String truckType2) {
		this.truckType2 = truckType2;
	}

	public Integer getType2TrucksCount() {
		return type2TrucksCount;
	}

	public void setType2TrucksCount(Integer type2TrucksCount) {
		this.type2TrucksCount = type2TrucksCount;
	}

	public Integer getType2TrucksCapacity() {
		return type2TrucksCapacity;
	}

	public void setType2TrucksCapacity(Integer type2TrucksCapacity) {
		this.type2TrucksCapacity = type2TrucksCapacity;
	}

	public Integer getType2TrucksEmptyWeight() {
		return type2TrucksEmptyWeight;
	}

	public void setType2TrucksEmptyWeight(Integer type2TrucksEmptyWeight) {
		this.type2TrucksEmptyWeight = type2TrucksEmptyWeight;
	}

	public String getTruckType3() {
		return truckType3;
	}

	public void setTruckType3(String truckType3) {
		this.truckType3 = truckType3;
	}

	public Integer getType3TrucksCount() {
		return type3TrucksCount;
	}

	public void setType3TrucksCount(Integer type3TrucksCount) {
		this.type3TrucksCount = type3TrucksCount;
	}

	public Integer getType3TrucksCapacity() {
		return type3TrucksCapacity;
	}

	public void setType3TrucksCapacity(Integer type3TrucksCapacity) {
		this.type3TrucksCapacity = type3TrucksCapacity;
	}

	public Integer getType3TrucksEmptyWeight() {
		return type3TrucksEmptyWeight;
	}

	public void setType3TrucksEmptyWeight(Integer type3TrucksEmptyWeight) {
		this.type3TrucksEmptyWeight = type3TrucksEmptyWeight;
	}

	public String getTruckType4() {
		return truckType4;
	}

	public void setTruckType4(String truckType4) {
		this.truckType4 = truckType4;
	}

	public Integer getType4TrucksCount() {
		return type4TrucksCount;
	}

	public void setType4TrucksCount(Integer type4TrucksCount) {
		this.type4TrucksCount = type4TrucksCount;
	}

	public Integer getType4TrucksCapacity() {
		return type4TrucksCapacity;
	}

	public void setType4TrucksCapacity(Integer type4TrucksCapacity) {
		this.type4TrucksCapacity = type4TrucksCapacity;
	}

	public Integer getType4TrucksEmptyWeight() {
		return type4TrucksEmptyWeight;
	}

	public void setType4TrucksEmptyWeight(Integer type4TrucksEmptyWeight) {
		this.type4TrucksEmptyWeight = type4TrucksEmptyWeight;
	}

	public Float getAcreageSubjectToWindErosion() {
		return acreageSubjectToWindErosion;
	}

	public void setAcreageSubjectToWindErosion(Float acreageSubjectToWindErosion) {
		this.acreageSubjectToWindErosion = acreageSubjectToWindErosion;
	}

	public String getStockPileTypeCd() {
		return stockPileTypeCd;
	}

	public void setStockPileTypeCd(String stockPileTypeCd) {
		this.stockPileTypeCd = stockPileTypeCd;
	}

	public Float getMaterialAddedRemovedFromPileDay() {
		return materialAddedRemovedFromPileDay;
	}

	public void setMaterialAddedRemovedFromPileDay(Float materialAddedRemovedFromPileDay) {
		this.materialAddedRemovedFromPileDay = materialAddedRemovedFromPileDay;
	}

	public Float getMaterialAddedRemovedFromPileYr() {
		return materialAddedRemovedFromPileYr;
	}

	public void setMaterialAddedRemovedFromPileYr(Float materialAddedRemovedFromPileYr) {
		this.materialAddedRemovedFromPileYr = materialAddedRemovedFromPileYr;
	}

	public Integer getStockPilesCount() {
		return stockPilesCount;
	}

	public void setStockPilesCount(Integer stockPilesCount) {
		this.stockPilesCount = stockPilesCount;
	}

	public Integer getStockPileSize() {
		return stockPileSize;
	}

	public void setStockPileSize(Integer stockPileSize) {
		this.stockPileSize = stockPileSize;
	}

	public String getStockPileUnitCd() {
		return stockPileUnitCd;
	}

	public void setStockPileUnitCd(String stockPileUnitCd) {
		this.stockPileUnitCd = stockPileUnitCd;
	}

	public Integer getBlastsPerYearNumber() {
		return blastsPerYearNumber;
	}

	public void setBlastsPerYearNumber(Integer blastsPerYearNumber) {
		this.blastsPerYearNumber = blastsPerYearNumber;
	}

	public String getBlastingAgentUsedType() {
		return blastingAgentUsedType;
	}

	public void setBlastingAgentUsedType(String blastingAgentUsedType) {
		this.blastingAgentUsedType = blastingAgentUsedType;
	}

	public Float getBlastingAgentUsedAmount() {
		return blastingAgentUsedAmount;
	}

	public void setBlastingAgentUsedAmount(Float blastingAgentUsedAmount) {
		this.blastingAgentUsedAmount = blastingAgentUsedAmount;
	}

	public Float getBlastHorizontalArea() {
		return blastHorizontalArea;
	}

	public void setBlastHorizontalArea(Float blastHorizontalArea) {
		this.blastHorizontalArea = blastHorizontalArea;
	}

	public String getFugitiveSourceDescription() {
		return fugitiveSourceDescription;
	}

	public void setFugitiveSourceDescription(String fugitiveSourceDescription) {
		this.fugitiveSourceDescription = fugitiveSourceDescription;
	}

	public String getMaterialBlastedTypeCd() {
		return materialBlastedTypeCd;
	}
	
	public void setMaterialBlastedTypeCd(String materialBlastedTypeCd) {
		this.materialBlastedTypeCd = materialBlastedTypeCd;
	}
	
	private void resetFugutiveTypeHaulRoadFields()
	{
		this.maxDistanceMateriaHauled = null;
		this.truckType1 = null;
		this.type1TrucksCount = null;;
		this.type1TrucksCapacity = null;;
		this.type1TrucksEmptyWeight = null;
		this.truckType2 = null;
		this.type2TrucksCount = null;
		this.type2TrucksCapacity = null;
		this.type2TrucksEmptyWeight = null;
		this.truckType3 = null;
		this.type3TrucksCount = null;
		this.type3TrucksCapacity = null;
		this.type3TrucksEmptyWeight = null;
		this.truckType4 = null;
		this.type4TrucksCount = null;
		this.type4TrucksCapacity = null;
		this.type4TrucksEmptyWeight = null;
	}
	
	private void resetFugutiveTypeExposedAcreageFields()
	{
		this.acreageSubjectToWindErosion = null;
	}
	
	private void resetFugutiveTypeStockpileFields()
	{
		this.stockPileTypeCd = null;
		this.materialAddedRemovedFromPileDay = null;
		this.materialAddedRemovedFromPileYr = null;
		this.stockPilesCount = null;
		this.stockPileSize = null;
		this.stockPileUnitCd = null;
	}
	
	private void resetFugutiveTypeBlastingFields()
	{
		this.blastsPerYearNumber = null;
		this.blastingAgentUsedType = null;
		this.blastingAgentUsedAmount = null ;
		this.blastHorizontalArea = null;
		this.materialBlastedTypeCd = null;
		
	}
	
	private void resetFugutiveTypeFugitiveOtherFields()
	{
		this.fugitiveSourceDescription = null;
		
		
	}
}
