package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeTGT extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeTGT:";

	private Float exhaustFlowRate;
	private BigDecimal inletSulfurConc;
	private BigDecimal outletSulfurConc;

	public NSRApplicationEUTypeTGT() {
		super();
	}

	public NSRApplicationEUTypeTGT(NSRApplicationEUTypeTGT old) {
		super(old);
		if (old != null) {
			setExhaustFlowRate(old.getExhaustFlowRate());
			setInletSulfurConc(old.getInletSulfurConc());
			setOutletSulfurConc(old.getOutletSulfurConc());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setExhaustFlowRate(AbstractDAO.getFloat(rs, "exhaust_flow_rate"));
		setInletSulfurConc(rs.getBigDecimal(
				"inlet_sulfur_concentration"));
		setOutletSulfurConc(rs.getBigDecimal(
				"outlet_sulfur_concentration"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((exhaustFlowRate == null) ? 0 : exhaustFlowRate.hashCode());
		result = prime * result
				+ ((inletSulfurConc == null) ? 0 : inletSulfurConc.hashCode());
		result = prime
				* result
				+ ((outletSulfurConc == null) ? 0 : outletSulfurConc.hashCode());
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
		final NSRApplicationEUTypeTGT other = (NSRApplicationEUTypeTGT) obj;
		if (exhaustFlowRate == null) {
			if (other.exhaustFlowRate != null)
				return false;
		} else if (!exhaustFlowRate.equals(other.exhaustFlowRate))
			return false;
		if (inletSulfurConc == null) {
			if (other.inletSulfurConc != null)
				return false;
		} else if (!inletSulfurConc.equals(other.inletSulfurConc))
			return false;
		if (outletSulfurConc == null) {
			if (other.outletSulfurConc != null)
				return false;
		} else if (!outletSulfurConc.equals(other.outletSulfurConc))
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
		requiredField(this.exhaustFlowRate, PAGE_VIEW_ID_PREFIX
				+ "exhaustFlowRate", "Exhaust Flow Rate (acfm/hr)",
				"exhaustFlowRate");

		requiredField(this.inletSulfurConc, PAGE_VIEW_ID_PREFIX
				+ "inletSulfurConc", "Inlet Sulfur Concentration (%)",
				"inletSulfurConc");

		requiredField(this.outletSulfurConc, PAGE_VIEW_ID_PREFIX
				+ "outletSulfurConc", "Outlet Sulfur Concentration (%)",
				"outletSulfurConc");

	}

	public void validateRanges() {
		checkRangeValues(exhaustFlowRate, new Float(.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "exhaustFlowRate",
				"Exhaust Flow Rate (acfm/hr)");
		checkRangeValues(inletSulfurConc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "inletSulfurConc",
				"Inlet Sulfur Concentration (%)");
		checkRangeValues(outletSulfurConc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "outletSulfurConc",
				"Outlet Sulfur Concentration (%)");
	}

	public Float getExhaustFlowRate() {
		return exhaustFlowRate;
	}

	public void setExhaustFlowRate(Float exhaustFlowRate) {
		this.exhaustFlowRate = exhaustFlowRate;
	}

	public BigDecimal getInletSulfurConc() {
		return inletSulfurConc;
	}

	public void setInletSulfurConc(BigDecimal inletSulfurConc) {
		this.inletSulfurConc = inletSulfurConc;
	}

	public BigDecimal getOutletSulfurConc() {
		return outletSulfurConc;
	}

	public void setOutletSulfurConc(BigDecimal outletSulfurConc) {
		this.outletSulfurConc = outletSulfurConc;
	}
}
