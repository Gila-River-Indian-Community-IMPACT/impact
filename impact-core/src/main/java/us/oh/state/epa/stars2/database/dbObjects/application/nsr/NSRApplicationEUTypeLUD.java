package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")

public class NSRApplicationEUTypeLUD extends NSRApplicationEUType {
	
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeLUD:";
	private Integer maxHourlyThroughput;
	private String maxHourlyThroughputUnitsCd;
	private String detailedDescription;
	
	public NSRApplicationEUTypeLUD() {
		super();
	}
	
	public NSRApplicationEUTypeLUD(NSRApplicationEUTypeLUD old){
		super(old);
		if(old != null) {
			setMaxHourlyThroughput(old.getMaxHourlyThroughput());
			setMaxHourlyThroughputUnitsCd(old.getMaxHourlyThroughputUnitsCd());
			setDetailedDescription(old.getDetailedDescription());
		}
	}
	
	public Integer getMaxHourlyThroughput() {
		return maxHourlyThroughput;
	}
	
	public void setMaxHourlyThroughput(Integer maxHourlyThroughput) {
		this.maxHourlyThroughput = maxHourlyThroughput;
	}
	
	public String getMaxHourlyThroughputUnitsCd() {
		return maxHourlyThroughputUnitsCd;
	}
	
	public void setMaxHourlyThroughputUnitsCd(String maxHourlyThroughputUnitsCd) {
		this.maxHourlyThroughputUnitsCd = maxHourlyThroughputUnitsCd;
	}
	
	public String getDetailedDescription() {
		return detailedDescription;
	}

	public void setDetailedDescription(String detailedDescription) {
		this.detailedDescription = detailedDescription;
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setMaxHourlyThroughput(AbstractDAO.getInteger(rs, "max_hourly_throughput"));
		setMaxHourlyThroughputUnitsCd(rs.getString("units_max_hourly_throughput_cd"));
		setDetailedDescription(rs.getString("detailed_description"));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((maxHourlyThroughput == null) ? 0 : maxHourlyThroughput.hashCode());
		result = prime * result
				+ ((maxHourlyThroughputUnitsCd == null) ? 0 : maxHourlyThroughputUnitsCd.hashCode());
		result = prime * result
				+ ((detailedDescription == null) ? 0 : detailedDescription.hashCode());
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
		final NSRApplicationEUTypeLUD other = (NSRApplicationEUTypeLUD) obj;
		if (maxHourlyThroughput == null) {
			if (other.maxHourlyThroughput != null)
				return false;
		} else if (!maxHourlyThroughput.equals(other.maxHourlyThroughput))
			return false;
		if (maxHourlyThroughputUnitsCd == null) {
			if (other.maxHourlyThroughputUnitsCd != null)
				return false;
		} else if (!maxHourlyThroughputUnitsCd.equals(other.maxHourlyThroughputUnitsCd))
			return false;
		if (detailedDescription == null) {
			if (other.detailedDescription != null)
				return false;
		} else if (!detailedDescription.equals(other.detailedDescription))
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
		requiredField(this.detailedDescription, PAGE_VIEW_ID_PREFIX + "detailedDescription",
				"Detailed Description", "detailedDescription");
	}
	
	private void validateRanges() {
		checkRangeValues(this.maxHourlyThroughput, new Integer(0), new Integer(Integer.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "maxHourlyThroughput", "Maximum Hourly Throughput");
	}
}