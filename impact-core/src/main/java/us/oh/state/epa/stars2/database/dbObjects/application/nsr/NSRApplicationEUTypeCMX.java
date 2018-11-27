package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import oracle.cabo.data.jbo.servlet.event.SetRegionEventHandler;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeCMX extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeCMX:";
	private Integer maxAnnualProductionRate;
	private String maxAnnualProductionRateUnitsCd;
	private Integer avgHourlyProductionRate;
	private String avgHourlyProductionRateUnitsCd;

	public NSRApplicationEUTypeCMX() {
		super();
	}

	public NSRApplicationEUTypeCMX(NSRApplicationEUTypeCMX old) {
		super(old);
		if (old != null) {
			setMaxAnnualProductionRate(old.getMaxAnnualProductionRate());
			setAvgHourlyProductionRate(old.getAvgHourlyProductionRate());
			setMaxAnnualProductionRateUnitsCd(old.getMaxAnnualProductionRateUnitsCd());
			setAvgHourlyProductionRateUnitsCd(old.getAvgHourlyProductionRateUnitsCd());
		}
	}

	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setMaxAnnualProductionRate(AbstractDAO.getInteger(rs ,"max_annual_production_rate"));
		setMaxAnnualProductionRateUnitsCd(rs.getString("units_max_annual_production_rate_cd"));
		setAvgHourlyProductionRate(AbstractDAO.getInteger(rs ,"avg_hourly_production_rate"));
		setAvgHourlyProductionRateUnitsCd(rs.getString("units_avg_hourly_production_rate_cd"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((maxAnnualProductionRate == null) ? 0 : maxAnnualProductionRate.hashCode());
		result = prime * result
				+ ((maxAnnualProductionRateUnitsCd == null) ? 0 : maxAnnualProductionRateUnitsCd.hashCode());
		result = prime * result
				+ ((avgHourlyProductionRate == null) ? 0 : avgHourlyProductionRate.hashCode());
		result = prime * result
				+ ((avgHourlyProductionRateUnitsCd == null) ? 0 : avgHourlyProductionRateUnitsCd.hashCode());
		
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
		final NSRApplicationEUTypeCMX other = (NSRApplicationEUTypeCMX) obj;
		if (maxAnnualProductionRate == null) {
			if (other.maxAnnualProductionRate != null)
				return false;
		} else if (!maxAnnualProductionRate.equals(other.maxAnnualProductionRate))
			return false;
		if (maxAnnualProductionRateUnitsCd == null) {
			if (other.maxAnnualProductionRateUnitsCd != null)
				return false;
		} else if (!maxAnnualProductionRateUnitsCd.equals(other.maxAnnualProductionRateUnitsCd))
			return false;
		if (avgHourlyProductionRate == null) {
			if (other.avgHourlyProductionRate != null)
				return false;
		} else if (!avgHourlyProductionRate.equals(other.avgHourlyProductionRate))
			return false;
		if (avgHourlyProductionRateUnitsCd == null) {
			if (other.avgHourlyProductionRateUnitsCd != null)
				return false;
		} else if (!avgHourlyProductionRateUnitsCd.equals(other.avgHourlyProductionRateUnitsCd))
			return false;
		return true;
	}

	public Integer getMaxAnnualProductionRate() {
		return maxAnnualProductionRate;
	}

	public void setMaxAnnualProductionRate(Integer maxAnnualProductionRate) {
		this.maxAnnualProductionRate = maxAnnualProductionRate;
	}

	public String getMaxAnnualProductionRateUnitsCd() {
		return maxAnnualProductionRateUnitsCd;
	}

	public void setMaxAnnualProductionRateUnitsCd(
			String maxAnnualProductionRateUnitsCd) {
		this.maxAnnualProductionRateUnitsCd = maxAnnualProductionRateUnitsCd;
	}

	public Integer getAvgHourlyProductionRate() {
		return avgHourlyProductionRate;
	}

	public void setAvgHourlyProductionRate(Integer avgHourlyProductionRate) {
		this.avgHourlyProductionRate = avgHourlyProductionRate;
	}

	public String getAvgHourlyProductionRateUnitsCd() {
		return avgHourlyProductionRateUnitsCd;
	}

	public void setAvgHourlyProductionRateUnitsCd(
			String avgHourlyProductionRateUnitsCd) {
		this.avgHourlyProductionRateUnitsCd = avgHourlyProductionRateUnitsCd;
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
	
		requiredField(this.maxAnnualProductionRate,	PAGE_VIEW_ID_PREFIX + "maxAnnualProductionRate", 
				"Maximum Annual Production Rate", "maxAnnualProductionRate");
		requiredField(this.maxAnnualProductionRateUnitsCd, PAGE_VIEW_ID_PREFIX + "maxAnnualProductionRateUnitsCd",
				"Maximum Annual Production Rate Units", "maxAnnualProductionRateUnitsCd");
		requiredField(this.avgHourlyProductionRate,	PAGE_VIEW_ID_PREFIX + "avgHourlyProductionRate",
				"Average Hourly Production Rate", "avgHourlyProductionRate");
		requiredField(this.avgHourlyProductionRateUnitsCd, PAGE_VIEW_ID_PREFIX + "avgHourlyProductionRateUnitsCd",
				"Average Hourly Production Rate Units", "avgHourlyProductionRateUnitsCd");
		
		}
	
	public void validateRanges() {
		checkRangeValues(maxAnnualProductionRate, new Integer(1), Integer.MAX_VALUE,
				PAGE_VIEW_ID_PREFIX + "maxAnnualProductionRate",
				"Maximum Annual Production Rate");
		checkRangeValues(avgHourlyProductionRate, new Integer(1), Integer.MAX_VALUE,
				PAGE_VIEW_ID_PREFIX + "avgHourlyProductionRate",
				"Average Hourly Production Rate");
	}
}
