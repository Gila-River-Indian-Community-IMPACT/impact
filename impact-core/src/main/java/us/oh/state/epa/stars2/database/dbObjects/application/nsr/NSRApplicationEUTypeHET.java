package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeHET extends NSRApplicationEUType {

	/******** Variables **********/
	private static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeHET:";

	private Integer applicationEUId;
	private BigDecimal fuelsulfur;
	private String unitsFuelSulfurCd;

	/******** Properties **********/
	public Integer getApplicationEUId() {
		return applicationEUId;
	}

	public void setApplicationEUId(Integer applicationEUId) {
		this.applicationEUId = applicationEUId;
	}

	public BigDecimal getFuelsulfur() {
		return fuelsulfur;
	}

	public void setFuelsulfur(BigDecimal fuelsulfur) {
		this.fuelsulfur = fuelsulfur;
	}

	public String getUnitsFuelSulfurCd() {
		return unitsFuelSulfurCd;
	}

	public void setUnitsFuelSulfurCd(String unitsFuelSulfurCd) {
		this.unitsFuelSulfurCd = unitsFuelSulfurCd;
	}

	public NSRApplicationEUTypeHET() {
		super();
	}

	public NSRApplicationEUTypeHET(NSRApplicationEUTypeHET old) {
		super(old);
		if (old != null) {
			setFuelsulfur(old.getFuelsulfur());
			setUnitsFuelSulfurCd(old.getUnitsFuelSulfurCd());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);

		setApplicationEUId(AbstractDAO.getInteger(rs, "application_eu_id"));
		setFuelsulfur(rs.getBigDecimal("fuel_sulfur"));
		setUnitsFuelSulfurCd(rs.getString("units_fuel_sulfur_cd"));
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
		if (!Utility.isNullOrZero(this.fuelsulfur)) {
			requiredFieldUnitsFuelSulfurCd();
		}
	}

	public void validateRanges() {
		checkRangeValues(this.fuelsulfur, new BigDecimal(".00"),
				new BigDecimal(Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX
						+ "fuelsulfur", "Fuel Sulfur Content");
	}

	private void requiredFieldUnitsFuelSulfurCd() {
		requiredField(this.unitsFuelSulfurCd, PAGE_VIEW_ID_PREFIX
				+ "unitsFuelSulfurCd", "Fuel Sulfur Content Units",
				"unitsFuelSulfurCd");
	}

}