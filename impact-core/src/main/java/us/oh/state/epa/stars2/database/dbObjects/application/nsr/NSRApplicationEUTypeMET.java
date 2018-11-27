package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

public class NSRApplicationEUTypeMET extends NSRApplicationEUType {

	private static final long serialVersionUID = -6907274229154458954L;

	// Just a placeholder. NSR application not in scope for AZ
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeMET:";

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
