package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeBOL extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeBOL:";
	private String boilerTypeCd;
	private Float btuContent;
	private String bolBtuUnitsCd;
	private Float fuelSulfurContent;
	private BigDecimal fuelAshContent;
	private String bolSulfurUnitsCd;
	private String serviceTypeCd;

	public NSRApplicationEUTypeBOL() {
		super();
	}

	public NSRApplicationEUTypeBOL(NSRApplicationEUTypeBOL old) {
		super(old);
		if (old != null) {
			setBoilerTypeCd(old.getBoilerTypeCd());
			setBtuContent(old.getBtuContent());
			setBolBtuUnitsCd(old.getBolBtuUnitsCd());
			setFuelSulfurContent(old.getFuelSulfurContent());
			setBolSulfurUnitsCd(old.getBolSulfurUnitsCd());
			setFuelAshContent(old.getFuelAshContent());
			setServiceTypeCd(old.getServiceTypeCd());

		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setBoilerTypeCd(rs.getString("boiler_type_cd"));
		setBtuContent(AbstractDAO.getFloat(rs, "btu"));
		setBolBtuUnitsCd(rs.getString("units_btu_cd"));
		setFuelSulfurContent(AbstractDAO.getFloat(rs, "fuel_sulfur"));
		setBolSulfurUnitsCd(rs.getString("units_fuel_sulfur_cd"));
		setFuelAshContent(rs.getBigDecimal("fuel_ash"));
		setServiceTypeCd(rs.getString("service_type_cd"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((boilerTypeCd == null) ? 0 : boilerTypeCd.hashCode());
		result = prime * result
				+ ((btuContent == null) ? 0 : btuContent.hashCode());
		result = prime * result
				+ ((bolBtuUnitsCd == null) ? 0 : bolBtuUnitsCd.hashCode());
		result = prime
				* result
				+ ((fuelSulfurContent == null) ? 0 : fuelSulfurContent
						.hashCode());
		result = prime
				* result
				+ ((bolSulfurUnitsCd == null) ? 0 : bolSulfurUnitsCd.hashCode());
		result = prime * result
				+ ((fuelAshContent == null) ? 0 : fuelAshContent.hashCode());
		result = prime * result
				+ ((serviceTypeCd == null) ? 0 : serviceTypeCd.hashCode());
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
		final NSRApplicationEUTypeBOL other = (NSRApplicationEUTypeBOL) obj;
		if (boilerTypeCd == null) {
			if (other.boilerTypeCd != null)
				return false;
		} else if (!boilerTypeCd.equals(other.boilerTypeCd))
			return false;
		if (btuContent == null) {
			if (other.btuContent != null)
				return false;
		} else if (!btuContent.equals(other.btuContent))
			return false;
		if (bolBtuUnitsCd == null) {
			if (other.bolBtuUnitsCd != null)
				return false;
		} else if (!bolBtuUnitsCd.equals(other.bolBtuUnitsCd))
			return false;
		if (fuelSulfurContent == null) {
			if (other.fuelSulfurContent != null)
				return false;
		} else if (!fuelSulfurContent.equals(other.fuelSulfurContent))
			return false;
		if (bolSulfurUnitsCd == null) {
			if (other.bolSulfurUnitsCd != null)
				return false;
		} else if (!bolSulfurUnitsCd.equals(other.bolSulfurUnitsCd))
			return false;
		if (fuelAshContent == null) {
			if (other.fuelAshContent != null)
				return false;
		} else if (!fuelAshContent.equals(other.fuelAshContent))
			return false;
		if (serviceTypeCd == null) {
			if (other.serviceTypeCd != null)
				return false;
		} else if (!serviceTypeCd.equals(other.serviceTypeCd))
			return false;
		return true;
	}

	public Float getBtuContent() {
		return btuContent;
	}

	public void setBtuContent(Float btuContent) {
		this.btuContent = btuContent;
	}

	public String getBolBtuUnitsCd() {
		return bolBtuUnitsCd;
	}

	public void setBolBtuUnitsCd(String bolBtuUnitsCd) {
		this.bolBtuUnitsCd = bolBtuUnitsCd;
	}

	public Float getFuelSulfurContent() {
		return fuelSulfurContent;
	}

	public void setFuelSulfurContent(Float fuelSulfurContent) {
		this.fuelSulfurContent = fuelSulfurContent;
	}

	public BigDecimal getFuelAshContent() {
		return fuelAshContent;
	}

	public void setFuelAshContent(BigDecimal fuelAshContent) {
		this.fuelAshContent = fuelAshContent;
	}

	public String getBolSulfurUnitsCd() {
		return bolSulfurUnitsCd;
	}

	public void setBolSulfurUnitsCd(String bolSulfurUnitsCd) {
		this.bolSulfurUnitsCd = bolSulfurUnitsCd;
	}

	public String getBoilerTypeCd() {
		return boilerTypeCd;
	}

	public void setBoilerTypeCd(String boilerTypeCd) {
		this.boilerTypeCd = boilerTypeCd;
	}

	public String getServiceTypeCd() {
		return serviceTypeCd;
	}

	public void setServiceTypeCd(String serviceTypeCd) {
		this.serviceTypeCd = serviceTypeCd;
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
		requiredField(this.boilerTypeCd, PAGE_VIEW_ID_PREFIX + "boilerType",
				"Boiler Type", "boilerTypeCd");

		requiredField(this.btuContent, PAGE_VIEW_ID_PREFIX + "btuContent",
				"Btu Content", "btuContent");

		requiredField(this.bolBtuUnitsCd,
				PAGE_VIEW_ID_PREFIX + "bolBtuUnitsCd", "Btu Content Units",
				"bolBtuUnitsCd");

		requiredField(this.fuelSulfurContent, PAGE_VIEW_ID_PREFIX
				+ "fuelSulfurContent", "Fuel Sulfur Content",
				"fuelSulfurContent");

		requiredField(this.bolSulfurUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "bolSulfurUnitsCd", "Fuel Sulfur Content Units",
				"bolSulfurUnitsCd");

		requiredField(this.fuelAshContent, PAGE_VIEW_ID_PREFIX
				+ "fuelAshContent", "Fuel Ash Content (%)", "fuelAshContent");
		requiredField(this.serviceTypeCd,
				PAGE_VIEW_ID_PREFIX + "serviceTypeCd", "Type of Service",
				"serviceTypeCd");
	}

	public void validateRanges() {
		checkRangeValues(fuelSulfurContent, new Float(0.00), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "fuelSulfurContent",
				"Fuel Sulfur Content");
		checkRangeValues(btuContent, new Float(0.01),
				new Float(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "btuContent",
				"Btu Content");
		checkRangeValues(fuelAshContent, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "fuelAshContent", "Fuel Ash Content (%)");
	}

}
