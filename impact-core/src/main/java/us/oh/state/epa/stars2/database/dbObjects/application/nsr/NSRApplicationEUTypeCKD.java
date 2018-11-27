package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeCKD extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeCKD:";

	private Float btu;
	private String unitsBtuCd;
	private Float fuelSulfur;
	private String unitsFuelSulfurCd;
	private String typeOfMaterialProcessedCd;
	private String fuelFiredSourceFlag = "Y";

	public NSRApplicationEUTypeCKD() {
		super();
	}

	public NSRApplicationEUTypeCKD(NSRApplicationEUTypeCKD old) {
		super(old);
		if (old != null) {
			setBtu(old.getBtu());
			setUnitsBtuCd(old.getUnitsBtuCd());
			setFuelSulfur(old.getFuelSulfur());
			setUnitsFuelSulfurCd(old.getUnitsFuelSulfurCd());
			setTypeOfMaterialProcessedCd(old.getTypeOfMaterialProcessedCd());
			setFuelFiredSourceFlag(old.getFuelFiredSourceFlag());
		}
	}

	public String getFuelFiredSourceFlag() {
		return fuelFiredSourceFlag;
	}

	public void setFuelFiredSourceFlag(String fuelFiredSourceFlag) {
		this.fuelFiredSourceFlag = fuelFiredSourceFlag;
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

	public String getTypeOfMaterialProcessedCd() {
		return typeOfMaterialProcessedCd;
	}

	public void setTypeOfMaterialProcessedCd(String typeOfMaterialProcessedCd) {
		this.typeOfMaterialProcessedCd = typeOfMaterialProcessedCd;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setBtu(AbstractDAO.getFloat(rs, "btu"));
		setUnitsBtuCd(rs.getString("units_btu_cd"));
		setFuelSulfur(AbstractDAO.getFloat(rs, "fuel_sulfur"));
		setUnitsFuelSulfurCd(rs.getString("units_fuel_sulfur_cd"));
		setTypeOfMaterialProcessedCd(rs
				.getString("type_of_material_processed_cd"));
		setFuelFiredSourceFlag(rs.getString("fuel_fired_source_flag"));
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
				+ ((typeOfMaterialProcessedCd == null) ? 0
						: typeOfMaterialProcessedCd.hashCode());
		result = prime
				* result
				+ ((fuelFiredSourceFlag == null) ? 0
						: fuelFiredSourceFlag.hashCode());
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
		final NSRApplicationEUTypeCKD other = (NSRApplicationEUTypeCKD) obj;
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
		if (typeOfMaterialProcessedCd == null) {
			if (other.typeOfMaterialProcessedCd != null)
				return false;
		} else if (!typeOfMaterialProcessedCd
				.equals(other.typeOfMaterialProcessedCd))
			return false;
		if (fuelFiredSourceFlag == null) {
			if (other.fuelFiredSourceFlag != null)
				return false;
		} else if (!fuelFiredSourceFlag
				.equals(other.fuelFiredSourceFlag))
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
		requiredField(this.fuelFiredSourceFlag, PAGE_VIEW_ID_PREFIX + "fuelFiredSource", "Fuel-fired Source",
				"fuelFiredSource");
		if (isFuelFiredSource()) {
			requiredField(this.btu, PAGE_VIEW_ID_PREFIX + "btu", "Btu Content",
					"btu");
			requiredField(this.unitsBtuCd, PAGE_VIEW_ID_PREFIX + "unitsBtuCd",
					"Units", "unitsBtuCd");
			requiredField(this.fuelSulfur, PAGE_VIEW_ID_PREFIX + "fuelSulfur",
					"Fuel Sulfur Content", "fuelSulfur");
			requiredField(this.unitsFuelSulfurCd, PAGE_VIEW_ID_PREFIX
					+ "unitsFuelSulfurCd", "Units", "unitsFuelSulfurCd");
		}
		requiredField(this.typeOfMaterialProcessedCd, PAGE_VIEW_ID_PREFIX
				+ "typeOfMaterialProcessedCd", "Type of Material Processed",
				"typeOfMaterialProcessedCd");
	}

	public boolean isFuelFiredSource() {
		return AbstractDAO.translateIndicatorToBoolean(fuelFiredSourceFlag);
	}

	public void validateRanges() {
		checkRangeValues(this.btu, new Float(0.01), new Float(Float.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "btu", "Btu Content");
		checkRangeValues(this.fuelSulfur, new Float(0.00), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelSulfur",
				"Fuel Sulfur Content");
	}

}
