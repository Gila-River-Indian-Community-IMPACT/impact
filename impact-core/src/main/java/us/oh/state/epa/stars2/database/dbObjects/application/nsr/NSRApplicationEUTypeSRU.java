package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeSRU extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeSRU:";

	private BigDecimal inletSulfurConc;
	private BigDecimal outletSulfurConc;
	private Float designCapacity;

	public NSRApplicationEUTypeSRU() {
		super();
	}

	public NSRApplicationEUTypeSRU(NSRApplicationEUTypeSRU old) {
		super(old);
		if (old != null) {
			setInletSulfurConc(old.getInletSulfurConc());
			setOutletSulfurConc(old.getOutletSulfurConc());
			setDesignCapacity(old.getDesignCapacity());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setInletSulfurConc(rs.getBigDecimal(
				"inlet_sulfur_concentration"));
		setOutletSulfurConc(rs.getBigDecimal(
				"outlet_sulfur_concentration"));
		setDesignCapacity(AbstractDAO.getFloat(rs, "design_capacity"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((inletSulfurConc == null) ? 0 : inletSulfurConc.hashCode());
		result = prime
				* result
				+ ((outletSulfurConc == null) ? 0 : outletSulfurConc.hashCode());
		result = prime * result
				+ ((designCapacity == null) ? 0 : designCapacity.hashCode());
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
		final NSRApplicationEUTypeSRU other = (NSRApplicationEUTypeSRU) obj;
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
		if (designCapacity == null) {
			if (other.designCapacity != null)
				return false;
		} else if (!designCapacity.equals(other.designCapacity))
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
		requiredField(this.inletSulfurConc, PAGE_VIEW_ID_PREFIX
				+ "inletSulfurConc", "Inlet Sulfur Concentration (%)",
				"inletSulfurConc");

		requiredField(this.outletSulfurConc, PAGE_VIEW_ID_PREFIX
				+ "outletSulfurConc", "Outlet Sulfur Concentration (%)",
				"outletSulfurConc");

		requiredField(this.designCapacity, PAGE_VIEW_ID_PREFIX
				+ "designCapacity", "Design Capacity (MMscf/day)",
				"designCapacity");

	}

	public void validateRanges() {
		checkRangeValues(inletSulfurConc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "inletSulfurConc",
				"Inlet Sulfur Concentration (%)");
		checkRangeValues(outletSulfurConc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "outletSulfurConc",
				"Outlet Sulfur Concentration (%)");
		checkRangeValues(designCapacity, new Float(0.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "designCapacity",
				"Design Capacity (MMscf/day)");
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

	public Float getDesignCapacity() {
		return designCapacity;
	}

	public void setDesignCapacity(Float designCapacity) {
		this.designCapacity = designCapacity;
	}

}
