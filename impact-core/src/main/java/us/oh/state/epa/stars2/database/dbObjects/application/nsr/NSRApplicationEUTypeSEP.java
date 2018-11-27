package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeSEP extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeSEP:";
	
	private Integer operatingTemperature;
	private Float operatingPressure;

	public NSRApplicationEUTypeSEP() {
		super();
	}

	public NSRApplicationEUTypeSEP(NSRApplicationEUTypeSEP old) {
		super(old);
		if (old != null) {
			setOperatingTemperature(old.getOperatingTemperature());
			setOperatingPressure(old.getOperatingPressure());
		}
	}

	public Integer getOperatingTemperature() {
		return operatingTemperature;
	}

	public void setOperatingTemperature(Integer operatingTemperature) {
		this.operatingTemperature = operatingTemperature;
	}

	public Float getOperatingPressure() {
		return operatingPressure;
	}

	public void setOperatingPressure(Float operatingPressure) {
		this.operatingPressure = operatingPressure;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setOperatingTemperature(AbstractDAO.getInteger(rs,
				"operating_temperature"));
		setOperatingPressure(AbstractDAO.getFloat(rs, "operating_pressure"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((operatingTemperature == null) ? 0 : operatingTemperature
						.hashCode());
		result = prime
				* result
				+ ((operatingPressure == null) ? 0 : operatingPressure
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
		final NSRApplicationEUTypeSEP other = (NSRApplicationEUTypeSEP) obj;
		if (operatingTemperature == null) {
			if (other.operatingTemperature != null)
				return false;
		} else if (!operatingTemperature.equals(other.operatingTemperature))
			return false;
		if (operatingPressure == null) {
			if (other.operatingPressure != null)
				return false;
		} else if (!operatingPressure.equals(other.operatingPressure))
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
		requiredField(this.operatingTemperature, PAGE_VIEW_ID_PREFIX + "operatingTemperature", "Operating Temperature (F)", "operatingTemperature");
		requiredField(this.operatingPressure, PAGE_VIEW_ID_PREFIX + "operatingPressure", "Operating Pressure (psig)", "operatingPressure");

	}

	public void validateRanges() {
		  checkRangeValues(this.operatingTemperature, new Integer(1), new Integer(Integer.MAX_VALUE),PAGE_VIEW_ID_PREFIX + "operatingTemperature", "Operating Temperature (F)");
		  checkRangeValues(this.operatingPressure, new Float(0.01), new Float(Float.MAX_VALUE),PAGE_VIEW_ID_PREFIX + "operatingPressure", "Operating Pressure (psig)");
	}
	

}
