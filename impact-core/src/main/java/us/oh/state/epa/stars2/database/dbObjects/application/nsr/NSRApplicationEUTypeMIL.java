package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeMIL extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeMIL:";
	// Just a placeholder. NSR application not in scope for AZ

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
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


	}

	public void validateRanges() {
	}
	


}