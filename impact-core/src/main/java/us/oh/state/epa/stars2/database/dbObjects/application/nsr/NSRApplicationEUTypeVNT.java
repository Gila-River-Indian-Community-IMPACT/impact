package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeVNT extends NSRApplicationEUType {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeVNT:";

	private Float flowRateThroughput;
	private String flowRateThroughputUnitsCd;
	private BigDecimal vocConc;
	private BigDecimal hapsConc;

	public NSRApplicationEUTypeVNT() {
		super();
	}

	public NSRApplicationEUTypeVNT(NSRApplicationEUTypeVNT old) {
		super(old);
		if (old != null) {
			setFlowRateThroughput(old.getFlowRateThroughput());
			setFlowRateThroughputUnitsCd(old.getFlowRateThroughputUnitsCd());
			setVocConc(old.getVocConc());
			setHapsConc(old.getHapsConc());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);
		setFlowRateThroughput(AbstractDAO.getFloat(rs,
				"flow_rate_or_throughput"));
		setFlowRateThroughputUnitsCd(rs
				.getString("units_flow_rate_or_throughput_cd"));
		setVocConc(rs.getBigDecimal("voc_concentration"));
		setHapsConc(rs.getBigDecimal("haps_concentration"));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((flowRateThroughput == null) ? 0 : flowRateThroughput
						.hashCode());
		result = prime
				* result
				+ ((flowRateThroughputUnitsCd == null) ? 0
						: flowRateThroughputUnitsCd.hashCode());
		result = prime * result + ((vocConc == null) ? 0 : vocConc.hashCode());
		result = prime * result
				+ ((hapsConc == null) ? 0 : hapsConc.hashCode());
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
		final NSRApplicationEUTypeVNT other = (NSRApplicationEUTypeVNT) obj;
		if (flowRateThroughput == null) {
			if (other.flowRateThroughput != null)
				return false;
		} else if (!flowRateThroughput.equals(other.flowRateThroughput))
			return false;
		if (flowRateThroughputUnitsCd == null) {
			if (other.flowRateThroughputUnitsCd != null)
				return false;
		} else if (!flowRateThroughputUnitsCd
				.equals(other.flowRateThroughputUnitsCd))
			return false;
		if (vocConc == null) {
			if (other.vocConc != null)
				return false;
		} else if (!vocConc.equals(other.vocConc))
			return false;
		if (hapsConc == null) {
			if (other.hapsConc != null)
				return false;
		} else if (!hapsConc.equals(other.hapsConc))
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
		requiredField(this.flowRateThroughput, PAGE_VIEW_ID_PREFIX
				+ "flowRateThroughput", "Flow Rate or Throughput",
				"flowRateThroughput");

		requiredField(this.flowRateThroughputUnitsCd, PAGE_VIEW_ID_PREFIX
				+ "flowRateThroughputUnitsCd", "Flow Rate or Throughput Units",
				"flowRateThroughputUnitsCd");

		requiredField(this.vocConc, PAGE_VIEW_ID_PREFIX + "vocConc",
				"VOC Concentration (%)", "vocConc");

		requiredField(this.hapsConc, PAGE_VIEW_ID_PREFIX + "hapsConc",
				"HAPs Concentration (%)", "hapsConc");

	}

	public void validateRanges() {
		checkRangeValues(flowRateThroughput, new Float(.01), new Float(
				Float.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "flowRateThroughput",
				"Flow Rate or Throughput");
		checkRangeValues(vocConc, new BigDecimal(0), new BigDecimal(
				100),
				PAGE_VIEW_ID_PREFIX + "vocConc", "VOC Concentration (%)");
		checkRangeValues(hapsConc, new BigDecimal(0), new BigDecimal(
				100),
				PAGE_VIEW_ID_PREFIX + "hapsConc", "HAPs Concentration (%)");
	}

	public Float getFlowRateThroughput() {
		return flowRateThroughput;
	}

	public void setFlowRateThroughput(Float flowRateThroughput) {
		this.flowRateThroughput = flowRateThroughput;
	}

	public String getFlowRateThroughputUnitsCd() {
		return flowRateThroughputUnitsCd;
	}

	public void setFlowRateThroughputUnitsCd(String flowRateThroughputUnitsCd) {
		this.flowRateThroughputUnitsCd = flowRateThroughputUnitsCd;
	}

	public BigDecimal getVocConc() {
		return vocConc;
	}

	public void setVocConc(BigDecimal vocConc) {
		this.vocConc = vocConc;
	}

	public BigDecimal getHapsConc() {
		return hapsConc;
	}

	public void setHapsConc(BigDecimal hapsConc) {
		this.hapsConc = hapsConc;
	}

}
