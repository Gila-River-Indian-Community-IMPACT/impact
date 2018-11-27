package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.def.CshUnitTypeDef;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeCSH extends NSRApplicationEUType {

	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeCSH:";
	private String crushedMaterialType;
	private String crusherTypeCd;
	private Timestamp crusherManufactureDate;
	private String crusherPowerSourceCd;
	private Integer maxCrusherCapacity;
	private String screenCd;
	private String screenType;
	private String materialScreenedType;
	private Timestamp screenManufactureDate;
	private String screenPowerSourceCd;
	private String operatingInConjunctionCd;
	private Integer maxScreeingCapacity;
	private Integer conveyorTransferDropPoints;
	private String materialTransferredType;
	private String detailedDescription;

	// fpEU data for validation
	private String fpEuUnitType;

	public NSRApplicationEUTypeCSH() {
		super();
	}

	public NSRApplicationEUTypeCSH(NSRApplicationEUTypeCSH old) {
		super(old);
		if (old != null) {
			setCrushedMaterialType(old.getCrushedMaterialType());
			setCrusherTypeCd(old.getCrusherTypeCd());
			setCrusherManufactureDate(old.getCrusherManufactureDate());
			setCrusherPowerSourceCd(old.getCrusherPowerSourceCd());
			setMaxCrusherCapacity(old.getMaxCrusherCapacity());
			setScreenCd(old.getScreenCd());
			setScreenType(old.getScreenType());
			setMaterialScreenedType(old.getMaterialScreenedType());
			setScreenManufactureDate(old.getScreenManufactureDate());
			setScreenPowerSourceCd(old.getScreenPowerSourceCd());
			setOperatingInConjunctionCd(old.getOperatingInConjunctionCd());
			setMaxScreeingCapacity(old.getMaxScreeingCapacity());
			setConveyorTransferDropPoints(old.getConveyorTransferDropPoints());
			setMaterialTransferredType(old.getMaterialTransferredType());
			setDetailedDescription(old.getDetailedDescription());
			setFpEuUnitType(old.getFpEuUnitType());
		}
	}

	public String getCrushedMaterialType() {
		return crushedMaterialType;
	}

	public void setCrushedMaterialType(String crushedMaterialType) {
		this.crushedMaterialType = crushedMaterialType;
	}

	public String getCrusherTypeCd() {
		return crusherTypeCd;
	}

	public void setCrusherTypeCd(String crusherTypeCd) {
		this.crusherTypeCd = crusherTypeCd;
	}

	public Timestamp getCrusherManufactureDate() {
		return crusherManufactureDate;
	}

	public void setCrusherManufactureDate(Timestamp crusherManufactureDate) {
		this.crusherManufactureDate = crusherManufactureDate;
	}

	public String getCrusherPowerSourceCd() {
		return crusherPowerSourceCd;
	}

	public void setCrusherPowerSourceCd(String crusherPowerSourceCd) {
		this.crusherPowerSourceCd = crusherPowerSourceCd;
	}

	public Integer getMaxCrusherCapacity() {
		return maxCrusherCapacity;
	}

	public void setMaxCrusherCapacity(Integer maxCrusherCapacity) {
		this.maxCrusherCapacity = maxCrusherCapacity;
	}

	public String getScreenCd() {
		return screenCd;
	}

	public void setScreenCd(String screenCd) {
		this.screenCd = screenCd;
	}

	public String getScreenType() {
		return screenType;
	}

	public void setScreenType(String screenType) {
		this.screenType = screenType;
	}

	public String getMaterialScreenedType() {
		return materialScreenedType;
	}

	public void setMaterialScreenedType(String materialScreenedType) {
		this.materialScreenedType = materialScreenedType;
	}

	public Timestamp getScreenManufactureDate() {
		return screenManufactureDate;
	}

	public void setScreenManufactureDate(Timestamp screenManufactureDate) {
		this.screenManufactureDate = screenManufactureDate;
	}

	public String getScreenPowerSourceCd() {
		return screenPowerSourceCd;
	}

	public void setScreenPowerSourceCd(String screenPowerSourceCd) {
		this.screenPowerSourceCd = screenPowerSourceCd;
	}

	public String getOperatingInConjunctionCd() {
		return operatingInConjunctionCd;
	}

	public void setOperatingInConjunctionCd(String operatingInConjunctionCd) {
		this.operatingInConjunctionCd = operatingInConjunctionCd;

		if (!isOperatingInConjunction()) {
			setMaxScreeingCapacity(null);
		}
	}

	public Integer getMaxScreeingCapacity() {
		return maxScreeingCapacity;
	}

	public void setMaxScreeingCapacity(Integer maxScreeingCapacity) {
		this.maxScreeingCapacity = maxScreeingCapacity;
	}

	public Integer getConveyorTransferDropPoints() {
		return conveyorTransferDropPoints;
	}

	public void setConveyorTransferDropPoints(Integer conveyorTransferDropPoints) {
		this.conveyorTransferDropPoints = conveyorTransferDropPoints;
	}

	public String getMaterialTransferredType() {
		return materialTransferredType;
	}

	public void setMaterialTransferredType(String materialTransferredType) {
		this.materialTransferredType = materialTransferredType;
	}

	public String getDetailedDescription() {
		return detailedDescription;
	}

	public void setDetailedDescription(String detailedDescription) {
		this.detailedDescription = detailedDescription;
	}

	public String getFpEuUnitType() {
		return fpEuUnitType;
	}

	public void setFpEuUnitType(String fpEuUnitType) {
		this.fpEuUnitType = fpEuUnitType;
		cleanUpDataByFpEuUnitType();
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setCrushedMaterialType(rs.getString("type_of_material_crushed"));
		setCrusherTypeCd(rs.getString("type_of_crusher_cd"));
		setCrusherManufactureDate(rs.getTimestamp("crushing_manufacture_date"));
		setCrusherPowerSourceCd(rs.getString("crushing_power_source_cd"));
		setMaxCrusherCapacity(AbstractDAO
				.getInteger(rs, "max_crusher_capacity"));
		setScreenCd(rs.getString("screen_cd"));
		setScreenType(rs.getString("screen_type"));
		setMaterialScreenedType(rs.getString("type_of_material_screened"));
		setScreenManufactureDate(rs.getTimestamp("screening_manufacture_date"));
		setScreenPowerSourceCd(rs.getString("screening_power_source_cd"));
		setOperatingInConjunctionCd(rs.getString("operating_in_conjunction_cd"));
		setMaxScreeingCapacity(AbstractDAO.getInteger(rs,
				"max_screening_capacity"));
		setConveyorTransferDropPoints(AbstractDAO.getInteger(rs,
				"number_of_conveyor_transfer_and_drop_points"));
		setMaterialTransferredType(rs
				.getString("type_of_material_being_transferred"));
		setDetailedDescription(rs.getString("detailed_description"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((crushedMaterialType == null) ? 0 : crushedMaterialType
						.hashCode());
		result = prime * result
				+ ((crusherTypeCd == null) ? 0 : crusherTypeCd.hashCode());
		result = prime
				* result
				+ ((crusherManufactureDate == null) ? 0
						: crusherManufactureDate.hashCode());
		result = prime
				* result
				+ ((crusherPowerSourceCd == null) ? 0 : crusherPowerSourceCd
						.hashCode());
		result = prime
				* result
				+ ((maxCrusherCapacity == null) ? 0 : maxCrusherCapacity
						.hashCode());
		result = prime * result
				+ ((screenCd == null) ? 0 : screenCd.hashCode());
		result = prime * result
				+ ((screenType == null) ? 0 : screenType.hashCode());
		result = prime
				* result
				+ ((materialScreenedType == null) ? 0 : materialScreenedType
						.hashCode());
		result = prime
				* result
				+ ((screenManufactureDate == null) ? 0 : screenManufactureDate
						.hashCode());
		result = prime
				* result
				+ ((screenPowerSourceCd == null) ? 0 : screenPowerSourceCd
						.hashCode());
		result = prime
				* result
				+ ((operatingInConjunctionCd == null) ? 0
						: operatingInConjunctionCd.hashCode());
		result = prime
				* result
				+ ((maxScreeingCapacity == null) ? 0 : maxScreeingCapacity
						.hashCode());
		result = prime
				* result
				+ ((conveyorTransferDropPoints == null) ? 0
						: conveyorTransferDropPoints.hashCode());
		result = prime
				* result
				+ ((materialTransferredType == null) ? 0
						: materialTransferredType.hashCode());
		result = prime
				* result
				+ ((detailedDescription == null) ? 0 : detailedDescription
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
		final NSRApplicationEUTypeCSH other = (NSRApplicationEUTypeCSH) obj;
		if (crushedMaterialType == null) {
			if (other.crushedMaterialType != null)
				return false;
		} else if (!crushedMaterialType.equals(other.crushedMaterialType))
			return false;

		if (crusherTypeCd == null) {
			if (other.crusherTypeCd != null)
				return false;
		} else if (!crusherTypeCd.equals(other.crusherTypeCd))
			return false;

		if (crusherManufactureDate == null) {
			if (other.crusherManufactureDate != null)
				return false;
		} else if (!crusherManufactureDate.equals(other.crusherManufactureDate))
			return false;

		if (crusherPowerSourceCd == null) {
			if (other.crusherPowerSourceCd != null)
				return false;
		} else if (!crusherPowerSourceCd.equals(other.crusherPowerSourceCd))
			return false;

		if (maxCrusherCapacity == null) {
			if (other.maxCrusherCapacity != null)
				return false;
		} else if (!maxCrusherCapacity.equals(other.maxCrusherCapacity))
			return false;

		if (screenCd == null) {
			if (other.screenCd != null)
				return false;
		} else if (!screenCd.equals(other.screenCd))
			return false;

		if (screenType == null) {
			if (other.screenType != null)
				return false;
		} else if (!screenType.equals(other.screenType))
			return false;

		if (materialScreenedType == null) {
			if (other.materialScreenedType != null)
				return false;
		} else if (!materialScreenedType.equals(other.materialScreenedType))
			return false;

		if (screenManufactureDate == null) {
			if (other.screenManufactureDate != null)
				return false;
		} else if (!screenManufactureDate.equals(other.screenManufactureDate))
			return false;

		if (screenPowerSourceCd == null) {
			if (other.screenPowerSourceCd != null)
				return false;
		} else if (!screenPowerSourceCd.equals(other.screenPowerSourceCd))
			return false;

		if (operatingInConjunctionCd == null) {
			if (other.operatingInConjunctionCd != null)
				return false;
		} else if (!operatingInConjunctionCd
				.equals(other.operatingInConjunctionCd))
			return false;

		if (maxScreeingCapacity == null) {
			if (other.maxScreeingCapacity != null)
				return false;
		} else if (!maxScreeingCapacity.equals(other.maxScreeingCapacity))
			return false;

		if (conveyorTransferDropPoints == null) {
			if (other.conveyorTransferDropPoints != null)
				return false;
		} else if (!conveyorTransferDropPoints
				.equals(other.conveyorTransferDropPoints))
			return false;

		if (materialTransferredType == null) {
			if (other.materialTransferredType != null)
				return false;
		} else if (!materialTransferredType
				.equals(other.materialTransferredType))
			return false;

		if (detailedDescription == null) {
			if (other.detailedDescription != null)
				return false;
		} else if (!detailedDescription.equals(other.detailedDescription))
			return false;

		return true;
	}

	public void loadFpEuTypeData(EmissionUnitType fpEuType) {
		if (fpEuType != null) {
			this.setFpEuUnitType(fpEuType.getUnitType());
		}
	}

	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();
		validateRanges();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public boolean isOperatingInConjunction() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(operatingInConjunctionCd)
				&& this.operatingInConjunctionCd.equals("Yes");
		return ret;
	}

	public boolean isCrushing() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(fpEuUnitType)
				&& CshUnitTypeDef.CRUSHING.equals(this.fpEuUnitType);
		return ret;
	}

	public boolean isScreening() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(fpEuUnitType)
				&& CshUnitTypeDef.SCREENING.equals(this.fpEuUnitType);
		return ret;
	}

	public boolean isHandling() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(fpEuUnitType)
				&& CshUnitTypeDef.HANDLING.equals(this.fpEuUnitType);
		return ret;
	}

	public boolean isOther() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(fpEuUnitType)
				&& CshUnitTypeDef.OTHER.equals(this.fpEuUnitType);
		return ret;
	}

	public void requiredFields() {
		if (isCrushing()) {
			requiredField(this.crushedMaterialType, PAGE_VIEW_ID_PREFIX
					+ "crushedMaterialType", "Type of Material Crushed",
					"crushedMaterialType");
			requiredField(this.crusherTypeCd, PAGE_VIEW_ID_PREFIX
					+ "crusherTypeCd", "Type of Crusher", "crusherTypeCd");
			requiredField(this.crusherPowerSourceCd, PAGE_VIEW_ID_PREFIX
					+ "crusherPowerSourceCd", "Power Source",
					"crusherPowerSourceCd");
			requiredField(this.maxCrusherCapacity, PAGE_VIEW_ID_PREFIX
					+ "maxCrusherCapacity", "Max Crusher Capacity (tons/hr)",
					"maxCrusherCapacity");
		} else if (isScreening()) {
			requiredField(this.screenCd, PAGE_VIEW_ID_PREFIX + "screenCd",
					"Screen", "screenCd");
			requiredField(this.screenType, PAGE_VIEW_ID_PREFIX + "screenType",
					"Screen Type", "screenType");
			requiredField(this.materialScreenedType, PAGE_VIEW_ID_PREFIX
					+ "materialScreenedType", "Type of Material Screened",
					"materialScreenedType");
			requiredField(this.screenPowerSourceCd, PAGE_VIEW_ID_PREFIX
					+ "screenPowerSourceCd", "Power Source",
					"screenPowerSourceCd");
			requiredField(this.operatingInConjunctionCd, PAGE_VIEW_ID_PREFIX
					+ "operatingInConjunctionCd",
					"Operating in Conjunction with a Crusher",
					"operatingInConjunctionCd");
			if (isOperatingInConjunction()) {
				requiredField(this.maxScreeingCapacity, PAGE_VIEW_ID_PREFIX
						+ "maxScreeingCapacity",
						"Max Screeing Capacity (tons/hr)",
						"maxScreeingCapacity");
			}
		} else if (isHandling()) {
			requiredField(this.conveyorTransferDropPoints, PAGE_VIEW_ID_PREFIX
					+ "conveyorTransferDropPoints",
					"Number of Conveyor transfer and drop points",
					"conveyorTransferDropPoints");
			requiredField(this.materialTransferredType, PAGE_VIEW_ID_PREFIX
					+ "materialTransferredType",
					"Type of Material being Transferred",
					"materialTransferredType");
		} else if (isOther()) {
			requiredField(this.detailedDescription, PAGE_VIEW_ID_PREFIX
					+ "detailedDescription", "Detailed Description of Unit",
					"detailedDescription");
		}
	}

	private void validateRanges() {
		checkRangeValues(this.maxCrusherCapacity, new Integer(0), new Integer(
				Integer.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "maxCrusherCapacity",
				"Max Crusher Capacity (tons/hr)");
		checkRangeValues(this.maxScreeingCapacity, new Integer(0), new Integer(
				Integer.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "maxScreeingCapacity",
				"Max Screening Capacity (tons/hr)");
		checkRangeValues(this.conveyorTransferDropPoints, new Integer(0),
				new Integer(Integer.MAX_VALUE), PAGE_VIEW_ID_PREFIX
						+ "conveyorTransferDropPoints",
				"Number of conveyor transfer and drop points");
	}

	private void cleanUpDataByFpEuUnitType() {
		if (isCrushing()) {
			// setCrushedMaterialType(null);
			// setCrusherTypeCd(null);
			// setCrusherManufactureDate(null);
			// setCrusherPowerSourceCd(null);
			// setMaxCrusherCapacity(null);
			setScreenCd(null);
			setScreenType(null);
			setMaterialScreenedType(null);
			setScreenManufactureDate(null);
			setScreenPowerSourceCd(null);
			setOperatingInConjunctionCd(null);
			setMaxScreeingCapacity(null);
			setConveyorTransferDropPoints(null);
			setMaterialTransferredType(null);
			setDetailedDescription(null);
		} else if (isScreening()) {
			setCrushedMaterialType(null);
			setCrusherTypeCd(null);
			setCrusherManufactureDate(null);
			setCrusherPowerSourceCd(null);
			setMaxCrusherCapacity(null);
			// setScreenCd(null);
			// setScreenType(null);
			// setMaterialScreenedType(null);
			// setScreenManufactureDate(null);
			// setScreenPowerSourceCd(null);
			// setOperatingInConjunctionCd(null);
			// setMaxScreeingCapacity(null);
			setConveyorTransferDropPoints(null);
			setMaterialTransferredType(null);
			setDetailedDescription(null);

		} else if (isHandling()) {
			setCrushedMaterialType(null);
			setCrusherTypeCd(null);
			setCrusherManufactureDate(null);
			setCrusherPowerSourceCd(null);
			setMaxCrusherCapacity(null);
			setScreenCd(null);
			setScreenType(null);
			setMaterialScreenedType(null);
			setScreenManufactureDate(null);
			setScreenPowerSourceCd(null);
			setOperatingInConjunctionCd(null);
			setMaxScreeingCapacity(null);
			// setConveyorTransferDropPoints(null);
			// setMaterialTransferredType(null);
			setDetailedDescription(null);
		} else if (isOther()) {
			setCrushedMaterialType(null);
			setCrusherTypeCd(null);
			setCrusherManufactureDate(null);
			setCrusherPowerSourceCd(null);
			setMaxCrusherCapacity(null);
			setScreenCd(null);
			setScreenType(null);
			setMaterialScreenedType(null);
			setScreenManufactureDate(null);
			setScreenPowerSourceCd(null);
			setOperatingInConjunctionCd(null);
			setMaxScreeingCapacity(null);
			setConveyorTransferDropPoints(null);
			setMaterialTransferredType(null);
			// setDetailedDescription(null);
		}

	}
}
