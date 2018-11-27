package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.AppEUCirculationPumpTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeCTW extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeCTW:";

	private Integer cellFlowRate;
	private Integer circulationRate;
	private BigDecimal voc;
	private BigDecimal hap;
	private Integer cellNumber;

	public NSRApplicationEUTypeCTW() {
		super();
	}

	public NSRApplicationEUTypeCTW(NSRApplicationEUTypeCTW old) {
		super(old);
		if (old != null) {
			setCellFlowRate(old.getCellFlowRate());
			setCirculationRate(old.getCirculationRate());
			setVoc(old.getVoc());
			setHap(old.getHap());
			setCellNumber(old.getCellNumber());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setCellFlowRate(AbstractDAO.getInteger(rs, "cell_flow_rate"));
		setCirculationRate(AbstractDAO.getInteger(rs, "circulation_rate"));
		setVoc(rs.getBigDecimal("voc"));
		setHap(rs.getBigDecimal("hap"));
		setCellNumber(AbstractDAO.getInteger(rs, "number_of_cells"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((cellFlowRate == null) ? 0 : cellFlowRate.hashCode());
		result = prime * result
				+ ((circulationRate == null) ? 0 : circulationRate.hashCode());
		result = prime * result + ((voc == null) ? 0 : voc.hashCode());
		result = prime * result + ((hap == null) ? 0 : hap.hashCode());
		result = prime * result
				+ ((cellNumber == null) ? 0 : cellNumber.hashCode());
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
		final NSRApplicationEUTypeCTW other = (NSRApplicationEUTypeCTW) obj;
		if (cellFlowRate == null) {
			if (other.cellFlowRate != null)
				return false;
		} else if (!cellFlowRate.equals(other.cellFlowRate))
			return false;
		if (circulationRate == null) {
			if (other.circulationRate != null)
				return false;
		} else if (!circulationRate.equals(other.circulationRate))
			return false;
		if (voc == null) {
			if (other.voc != null)
				return false;
		} else if (!voc.equals(other.voc))
			return false;
		if (hap == null) {
			if (other.hap != null)
				return false;
		} else if (!hap.equals(other.hap))
			return false;
		if (cellNumber == null) {
			if (other.cellNumber != null)
				return false;
		} else if (!cellNumber.equals(other.cellNumber))
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
		requiredField(this.cellFlowRate, PAGE_VIEW_ID_PREFIX + "cellFlowRate",
				"Cell Flow Rate (cu. ft/min)", "cellFlowRate");

		requiredField(this.circulationRate, PAGE_VIEW_ID_PREFIX
				+ "circulationRate", "Circulation Rate (gallons/min)",
				"circulationRate");

		requiredField(this.voc, PAGE_VIEW_ID_PREFIX + "voc", "VOC Content (%)",
				"voc");

		requiredField(this.hap, PAGE_VIEW_ID_PREFIX + "hap", "HAP Content (%)",
				"hap");

		requiredField(this.cellNumber, PAGE_VIEW_ID_PREFIX + "cellNumber",
				"Number of cells", "cellNumber");

	}

	public void validateRanges() {
		checkRangeValues(cellFlowRate, new Integer(1), new Integer(
				Integer.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "cellFlowRate",
				"Cell Flow Rate (cu. ft/min)");
		checkRangeValues(circulationRate, new Integer(1), new Integer(
				Integer.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "circulationRate",
				"Circulation Rate (gallons/min)");
		checkRangeValues(voc, new BigDecimal(0), new BigDecimal(
				100),
				PAGE_VIEW_ID_PREFIX + "voc", "VOC Content (%)");
		checkRangeValues(hap, new BigDecimal(0), new BigDecimal(
				100),
				PAGE_VIEW_ID_PREFIX + "hap", "HAP Content (%)");
		checkRangeValues(cellNumber, new Integer(0), new Integer(100),
				PAGE_VIEW_ID_PREFIX + "cellNumber", "Number of cells");
	}

	public Integer getCellFlowRate() {
		return cellFlowRate;
	}

	public void setCellFlowRate(Integer cellFlowRate) {
		this.cellFlowRate = cellFlowRate;
	}

	public Integer getCirculationRate() {
		return circulationRate;
	}

	public void setCirculationRate(Integer circulationRate) {
		this.circulationRate = circulationRate;
	}

	public BigDecimal getVoc() {
		return voc;
	}

	public void setVoc(BigDecimal voc) {
		this.voc = voc;
	}

	public BigDecimal getHap() {
		return hap;
	}

	public void setHap(BigDecimal hap) {
		this.hap = hap;
	}

	public Integer getCellNumber() {
		return cellNumber;
	}

	public void setCellNumber(Integer cellNumber) {
		this.cellNumber = cellNumber;
	}
}
