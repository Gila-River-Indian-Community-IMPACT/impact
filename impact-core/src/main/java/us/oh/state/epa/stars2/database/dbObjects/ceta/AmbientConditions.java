package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

public class AmbientConditions extends CetaBaseDB {

	private static final long serialVersionUID = 1L;
	public static final int AMBIENT_CONDITIONS_DAY_ONE = 1;
	public static final int AMBIENT_CONDITIONS_DAY_TWO = 2;
	private static final int AMBIENT_TEMPERATURE_MIN_VALUE = -70;
	private static final int AMBIENT_TEMPERATURE_MAX_VALUE = 120;
	private static final int WIND_SPEED_MIN_VALUE = 0;
	private static final int WIND_SPEED_MAX_VALUE = 140;

	private Integer fceId;
	private Integer inspectionDay;
	private Timestamp inspectionDate;
	private String arrivalTime;
	private String departureTime;
	private Integer ambientTemperature;
	private String windDirectionCd;
	private Integer windSpeed;
	private String skyConditionCd;

	public AmbientConditions() {
		super();
	}

	public AmbientConditions(Integer inspectionDay) {
		super();
		this.inspectionDay = inspectionDay;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		setInspectionDay(AbstractDAO.getInteger(rs, "inspection_day"));
		setInspectionDate(rs.getTimestamp("inspection_date"));
		setArrivalTime(rs.getString("arrival_time"));
		setDepartureTime(rs.getString("departure_time"));
		setAmbientTemperature(AbstractDAO.getInteger(rs, "ambient_temperature"));
		setWindSpeed(AbstractDAO.getInteger(rs, "wind_speed"));
		setWindDirectionCd(rs.getString("wind_direction_cd"));
		setSkyConditionCd(rs.getString("sky_conditions_cd"));
	}

	public final ValidationMessage[] validate() {
		String dayLabel = "";
		if (inspectionDay.equals(1)) {
			dayLabel = "Day One";
		} else if (inspectionDay.equals(2)) {
			dayLabel = "Day Two";
		}
		
		validationMessages.clear();
		checkRangeValues(ambientTemperature, AMBIENT_TEMPERATURE_MIN_VALUE, AMBIENT_TEMPERATURE_MAX_VALUE,
				"ambientTemperature", dayLabel + " Ambient Temperature");
		checkRangeValues(windSpeed, WIND_SPEED_MIN_VALUE, WIND_SPEED_MAX_VALUE, "windSpeed", dayLabel + " Wind Speed");

		if (getDepartureTime() != null) {
			if (getArrivalTime() == null) {
				validationMessages.put("arrivalTime",
						new ValidationMessage("arrivalTime",
								dayLabel + " Departure Time was entered without Arrival Time.",
								ValidationMessage.Severity.ERROR, null));
			} else {
				try {
					Date dep = new SimpleDateFormat("HHmm").parse(getDepartureTime());
					Date arr = new SimpleDateFormat("HHmm").parse(getArrivalTime());
					if (dep.before(arr) || dep.equals(arr)) {
						validationMessages.put("departureTime",
								new ValidationMessage("departureTime",
										dayLabel + " Departure Time has to be after Arrival Time.",
										ValidationMessage.Severity.ERROR, null));
					}
				} catch (ParseException e1) {
					validationMessages.put("departureTime", new ValidationMessage("departureTime",
							dayLabel + " Error validating Departure Time", ValidationMessage.Severity.ERROR, null));
				}
			}
		}

		if (inspectionDate == null && (ambientTemperature != null || windSpeed != null || arrivalTime != null
				|| departureTime != null || windDirectionCd != null || skyConditionCd != null)) {
			validationMessages.put("inspectionDate", new ValidationMessage("inspectionDate",
					dayLabel + " Inspection Date is empty, but other fields have values", ValidationMessage.Severity.ERROR, null));
		}

		return new ArrayList<ValidationMessage>(validationMessages.values()).toArray(new ValidationMessage[0]);
	}

	public Integer getFceId() {
		return fceId;
	}

	public void setFceId(Integer fceId) {
		this.fceId = fceId;
	}

	public Integer getInspectionDay() {
		return inspectionDay;
	}

	public void setInspectionDay(Integer inspectionDay) {
		this.inspectionDay = inspectionDay;
	}

	public Timestamp getInspectionDate() {
		return inspectionDate;
	}

	public void setInspectionDate(Timestamp inspectionDate) {
		this.inspectionDate = inspectionDate;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public Integer getAmbientTemperature() {
		return ambientTemperature;
	}

	public void setAmbientTemperature(Integer ambientTemperature) {
		this.ambientTemperature = ambientTemperature;
	}

	public String getWindDirectionCd() {
		return windDirectionCd;
	}

	public void setWindDirectionCd(String windDirectionCd) {
		this.windDirectionCd = windDirectionCd;
	}

	public Integer getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(Integer windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getSkyConditionCd() {
		return skyConditionCd;
	}

	public void setSkyConditionCd(String skyConditionCd) {
		this.skyConditionCd = skyConditionCd;
	}

}
