package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeINC extends NSRApplicationEUType {

	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeINC:";
	private String primaryFuelType;
	private Float btuContent;
	private String btuContentUnitsCd;
	private Float fuelSulfurContent;
	private String fuelSulfurContentUnitsCd;

	public NSRApplicationEUTypeINC() {
		super();
	}

	public NSRApplicationEUTypeINC(NSRApplicationEUTypeINC old) {
		super(old);
		if (old != null) {
			setPrimaryFuelType(old.getPrimaryFuelType());
			setBtuContent(old.getBtuContent());
			setBtuContentUnitsCd(old.getBtuContentUnitsCd());
			setFuelSulfurContent(old.getFuelSulfurContent());
			setFuelSulfurContentUnitsCd(old.getFuelSulfurContentUnitsCd());
		}
	}

	public String getPrimaryFuelType() {
		return primaryFuelType;
	}

	public void setPrimaryFuelType(String primaryFuelType) {
		this.primaryFuelType = primaryFuelType;
	}

	public Float getBtuContent() {
		return btuContent;
	}

	public void setBtuContent(Float btuContent) {
		this.btuContent = btuContent;
	}

	public String getBtuContentUnitsCd() {
		return btuContentUnitsCd;
	}

	public void setBtuContentUnitsCd(String btuContentUnitsCd) {
		this.btuContentUnitsCd = btuContentUnitsCd;
	}

	public Float getFuelSulfurContent() {
		return fuelSulfurContent;
	}

	public void setFuelSulfurContent(Float fuelSulfurContent) {
		this.fuelSulfurContent = fuelSulfurContent;
	}

	public String getFuelSulfurContentUnitsCd() {
		return fuelSulfurContentUnitsCd;
	}

	public void setFuelSulfurContentUnitsCd(String fuelSulfurContentUnitsCd) {
		this.fuelSulfurContentUnitsCd = fuelSulfurContentUnitsCd;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setPrimaryFuelType(rs.getString("primary_fuel_type_cd"));
		setBtuContent(AbstractDAO.getFloat(rs, "btu"));
		setBtuContentUnitsCd(rs.getString("units_btu_cd"));
		setFuelSulfurContent(AbstractDAO.getFloat(rs, "fuel_sulfur"));
		setFuelSulfurContentUnitsCd(rs.getString("units_fuel_sulfur_cd"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((primaryFuelType == null) ? 0 : primaryFuelType.hashCode());
		result = prime * result
				+ ((btuContent == null) ? 0 : btuContent.hashCode());
		result = prime
				* result
				+ ((btuContentUnitsCd == null) ? 0 : btuContentUnitsCd
						.hashCode());
		result = prime
				* result
				+ ((fuelSulfurContent == null) ? 0 : fuelSulfurContent
						.hashCode());
		result = prime
				* result
				+ ((fuelSulfurContentUnitsCd == null) ? 0
						: fuelSulfurContentUnitsCd.hashCode());
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
		final NSRApplicationEUTypeINC other = (NSRApplicationEUTypeINC) obj;
		if (primaryFuelType == null) {
			if (other.primaryFuelType != null)
				return false;
		} else if (!primaryFuelType.equals(other.primaryFuelType))
			return false;
		if (btuContent == null) {
			if (other.btuContent != null)
				return false;
		} else if (!btuContent.equals(other.btuContent))
			return false;
		if (btuContentUnitsCd == null) {
			if (other.btuContentUnitsCd != null)
				return false;
		} else if (!btuContentUnitsCd.equals(other.btuContentUnitsCd))
			return false;
		if (fuelSulfurContent == null) {
			if (other.fuelSulfurContent != null)
				return false;
		} else if (!fuelSulfurContent.equals(other.fuelSulfurContent))
			return false;
		if (fuelSulfurContentUnitsCd == null) {
			if (other.fuelSulfurContentUnitsCd != null)
				return false;
		} else if (!fuelSulfurContentUnitsCd
				.equals(other.fuelSulfurContentUnitsCd))
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
		requiredField(this.primaryFuelType, PAGE_VIEW_ID_PREFIX
				+ "primaryFuelType", "Primary Fuel Type", "primaryFuelType");
		requiredField(this.btuContent, PAGE_VIEW_ID_PREFIX + "btuContent",
				"Btu Content", "btuContent");
		requiredField(this.btuContentUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "btuContentUnitsCd", "Btu Content Units", "btuContentUnitsCd");
		requiredField(this.fuelSulfurContent, PAGE_VIEW_ID_PREFIX
				+ "fuelSulfurContent", "Fuel Sulfur Content",
				"fuelSulfurContent");
		requiredField(this.fuelSulfurContentUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "fuelSulfurContentUnitsCd", "Fuel Sulfur Content Units",
				"fuelSulfurContentUnitsCd");
	}

	private void validateRanges() {
		checkRangeValues(this.btuContent, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "btuContent",
				"Btu Content");
		checkRangeValues(this.fuelSulfurContent, new Float(0.00), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelSulfurContent",
				"Fuel Sulfur Content");
	}
}
