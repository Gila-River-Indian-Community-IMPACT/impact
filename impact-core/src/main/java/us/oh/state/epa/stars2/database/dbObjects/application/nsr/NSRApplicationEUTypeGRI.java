package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

public class NSRApplicationEUTypeGRI extends NSRApplicationEUType {

	private static final long serialVersionUID = 4852415159213078224L;

	/******** Variables **********/
	private static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeGRI:";

	public NSRApplicationEUTypeGRI() {
		super();
	}

	public NSRApplicationEUTypeGRI(NSRApplicationEUTypeGRI old) {
		super(old);
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
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
