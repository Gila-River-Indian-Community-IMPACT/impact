package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeORD extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeORD:";
	// Just a placeholder. NSR application not in scope for AZ

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);

	}

	@Override
	public int hashCode() {
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
