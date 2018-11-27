package us.oh.state.epa.stars2.database.dbObjects.application;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.PollutantDef;

@SuppressWarnings("serial")
public class ApplicationEUFugitiveLeaks extends BaseDB {
	public static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeFUG:";
	private int applicationEuId;
	private Integer fugitiveLeaksId;
	private String equipmentServiceTypeCd;
	private Integer equipmentTypeNumber;
	private Float leakRate;
	private BigDecimal percentVoc;

	public ApplicationEUFugitiveLeaks() {
		super();
	}

	public ApplicationEUFugitiveLeaks(Integer applicationEUId) {
		this.applicationEuId = applicationEUId;
	}

	public ApplicationEUFugitiveLeaks(ApplicationEUFugitiveLeaks old) {
		super(old);
		setApplicationEuId(old.getApplicationEuId());
		setEquipmentServiceTypeCd(old.getEquipmentServiceTypeCd());
		setLeakRate(old.getLeakRate());
		setPercentVoc(old.getPercentVoc());
	}

	public void populate(ResultSet rs) throws SQLException {
		setFugitiveLeaksId(AbstractDAO.getInteger(rs, "fugitive_leaks_id"));
		setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
		setEquipmentServiceTypeCd(rs.getString("equipment_service_type_cd"));
		setEquipmentTypeNumber(AbstractDAO.getInteger(rs,
				"number_of_each_equipment_type"));
		setLeakRate(rs.getFloat("leak_rate"));
		setPercentVoc(rs.getBigDecimal("percent_voc"));
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((fugitiveLeaksId == null) ? 0 : fugitiveLeaksId.hashCode());
		result = PRIME
				* result
				+ ((equipmentServiceTypeCd == null) ? 0
						: equipmentServiceTypeCd.hashCode());
		result = PRIME
				* result
				+ ((equipmentTypeNumber == null) ? 0 : equipmentTypeNumber
						.hashCode());
		result = PRIME * result + applicationEuId;
		result = PRIME * result
				+ ((leakRate == null) ? 0 : leakRate.hashCode());
		result = PRIME * result
				+ ((percentVoc == null) ? 0 : percentVoc.hashCode());

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
		final ApplicationEUFugitiveLeaks other = (ApplicationEUFugitiveLeaks) obj;
		if (fugitiveLeaksId == null) {
			if (other.fugitiveLeaksId != null)
				return false;
		} else if (!fugitiveLeaksId.equals(other.fugitiveLeaksId))
			return false;
		if (equipmentServiceTypeCd == null) {
			if (other.equipmentServiceTypeCd != null)
				return false;
		} else if (!equipmentServiceTypeCd.equals(other.equipmentServiceTypeCd))
			return false;
		if (equipmentTypeNumber == null) {
			if (other.equipmentTypeNumber != null)
				return false;
		} else if (!equipmentTypeNumber.equals(other.equipmentTypeNumber))
			return false;
		if (applicationEuId != other.applicationEuId)
			return false;
		if (leakRate == null) {
			if (other.leakRate != null)
				return false;
		} else if (!leakRate.equals(other.leakRate))
			return false;
		if (percentVoc == null) {
			if (other.percentVoc != null)
				return false;
		} else if (!percentVoc.equals(other.percentVoc))
			return false;
		return true;
	}

	@Override
	public final ValidationMessage[] validate() {
		clearValidationMessages();
		requiredFields();
		validateRanges();
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public void requiredFields() {
		requiredField(this.equipmentServiceTypeCd, PAGE_VIEW_ID_PREFIX
				+ "equipmentServiceTypeCd", "Equipment and Service Type",
				"equipmentServiceTypeCd");
		requiredField(this.equipmentTypeNumber, PAGE_VIEW_ID_PREFIX
				+ "equipmentTypeNumber", "Number of New or Modified Equipment Types",
				"equipmentTypeNumber");
		requiredField(this.leakRate, PAGE_VIEW_ID_PREFIX + "leakRate",
				"Leak Rate (ppm)", "leakRate");
	}

	public void validateRanges() {
		checkRangeValues(equipmentTypeNumber, new Integer(1),
				Integer.MAX_VALUE, PAGE_VIEW_ID_PREFIX + "equipmentTypeNumber",
				"Number of New or Modified Equipment Types");
		checkRangeValues(leakRate, new Float(0.01), new Float(Float.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "leakRate", "Leak Rate (ppm)");
		checkRangeValues(percentVoc, new BigDecimal(0), new BigDecimal(100),
				PAGE_VIEW_ID_PREFIX + "percentVoc", "Percent VOC");
	}

	public String getEquipmentServiceTypeCd() {
		return equipmentServiceTypeCd;
	}

	public void setEquipmentServiceTypeCd(String equipmentServiceTypeCd) {
		this.equipmentServiceTypeCd = equipmentServiceTypeCd;
	}

	public Integer getEquipmentTypeNumber() {
		return equipmentTypeNumber;
	}

	public void setEquipmentTypeNumber(Integer equipmentTypeNumber) {
		this.equipmentTypeNumber = equipmentTypeNumber;
	}

	public Float getLeakRate() {
		return leakRate;
	}

	public void setLeakRate(Float leakRate) {
		this.leakRate = leakRate;
	}

	public BigDecimal getPercentVoc() {
		return percentVoc;
	}

	public void setPercentVoc(BigDecimal percentVoc) {
		this.percentVoc = percentVoc;
	}

	public int getApplicationEuId() {
		return applicationEuId;
	}

	public void setApplicationEuId(int applicationEuId) {
		this.applicationEuId = applicationEuId;
	}

	public Integer getFugitiveLeaksId() {
		return fugitiveLeaksId;
	}

	public void setFugitiveLeaksId(Integer fugitiveLeaksId) {
		this.fugitiveLeaksId = fugitiveLeaksId;
	}
}
