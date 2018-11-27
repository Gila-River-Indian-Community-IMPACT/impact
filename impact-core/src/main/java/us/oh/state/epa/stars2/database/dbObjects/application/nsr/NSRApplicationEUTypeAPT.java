package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.AppEUAPTUnitTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeAPT extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeAPT:";

	private String unitTypeCd;
	private Float exhaustFlowRate;

	public NSRApplicationEUTypeAPT() {
		super();
	}

	public NSRApplicationEUTypeAPT(NSRApplicationEUTypeAPT old) {
		super(old);
		if (old != null) {
			setUnitTypeCd(old.getUnitTypeCd());
			setExhaustFlowRate(old.getExhaustFlowRate());
		}
	}

	public String getUnitTypeCd() {
		return unitTypeCd;
	}

	public void setUnitTypeCd(String unitTypeCd) {
		this.unitTypeCd = unitTypeCd;
		if (!isPrillTower()) {
			setExhaustFlowRate(null);
		}
	}

	public Float getExhaustFlowRate() {
		return exhaustFlowRate;
	}

	public void setExhaustFlowRate(Float exhaustFlowRate) {
		this.exhaustFlowRate = exhaustFlowRate;
	}

	public boolean isPrillTower() {
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(this.unitTypeCd)
				&& AppEUAPTUnitTypeDef.PRILL_TOWER.equals(this.unitTypeCd);
		return ret;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setUnitTypeCd(rs.getString("unit_type_cd"));
		setExhaustFlowRate(AbstractDAO.getFloat(rs, "exhaust_flow_rate"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((unitTypeCd == null) ? 0 : unitTypeCd.hashCode());
		result = prime * result
				+ ((exhaustFlowRate == null) ? 0 : exhaustFlowRate.hashCode());
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
		final NSRApplicationEUTypeAPT other = (NSRApplicationEUTypeAPT) obj;
		if (unitTypeCd == null) {
			if (other.unitTypeCd != null)
				return false;
		} else if (!unitTypeCd.equals(other.unitTypeCd))
			return false;
		if (exhaustFlowRate == null) {
			if (other.exhaustFlowRate != null)
				return false;
		} else if (!exhaustFlowRate.equals(other.exhaustFlowRate))
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
		requiredField(this.unitTypeCd, PAGE_VIEW_ID_PREFIX + "unitTypeCd",
				"Unit Type", "unitTypeCd");
		if (isPrillTower()) {
			requiredField(this.exhaustFlowRate, PAGE_VIEW_ID_PREFIX
					+ "exhaustFlowRate", "Exhaust Flow Rate (acfm)",
					"exhaustFlowRate");
		}
	}

	public void validateRanges() {
		if (isPrillTower()) {
			checkRangeValues(this.exhaustFlowRate, new Float(0.01), new Float(
					Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "exhaustFlowRate",
					"Exhaust Flow Rate (acfm)");
		}
	}
}
