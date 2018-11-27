package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.AppEUCCUEquipTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class NSRApplicationEUTypeCCU extends NSRApplicationEUType {

	/******** Variables **********/
	private static String PAGE_VIEW_ID_PREFIX = "nsrAppEmissionUnitTypeCCU:";

	private Integer applicationEUId;
	private Integer requestedChargeRate;
	private String odorMaskingAgentUsedCd;
	private Integer batchCycleTime;
	private Integer quenchCycleTime;
	private Float quenchGasVolums;
	private BigDecimal sulfurContentOfQuenchGas;
	private String typeOfEquipmentCd;

	/******** Properties **********/
	public Integer getApplicationEUId() {
		return applicationEUId;
	}

	public void setApplicationEUId(Integer applicationEUId) {
		this.applicationEUId = applicationEUId;
	}

	public Integer getRequestedChargeRate() {
		return requestedChargeRate;
	}

	public void setRequestedChargeRate(Integer requestedChargeRate) {
		this.requestedChargeRate = requestedChargeRate;
		requiredFieldRequestedChargeRate();
	}

	public String getOdorMaskingAgentUsedCd() {
		return odorMaskingAgentUsedCd;
	}

	public void setOdorMaskingAgentUsedCd(String odorMaskingAgentUsedCd) {
		this.odorMaskingAgentUsedCd = odorMaskingAgentUsedCd;
		requiredFieldOdorMaskingAgentUsedCd();
	}

	public Integer getBatchCycleTime() {
		return batchCycleTime;
	}

	public void setBatchCycleTime(Integer batchCycleTime) {
		this.batchCycleTime = batchCycleTime;
		requiredFieldBatchCycleTime();
	}

	public Integer getQuenchCycleTime() {
		return quenchCycleTime;
	}

	public void setQuenchCycleTime(Integer quenchCycleTime) {
		this.quenchCycleTime = quenchCycleTime;
	}

	public Float getQuenchGasVolums() {
		return quenchGasVolums;
	}

	public void setQuenchGasVolums(Float quenchGasVolums) {
		this.quenchGasVolums = quenchGasVolums;
		requiredFieldQuenchGasVolums();
	}

	public BigDecimal getSulfurContentOfQuenchGas() {
		return sulfurContentOfQuenchGas;
	}

	public void setSulfurContentOfQuenchGas(BigDecimal sulfurContentOfQuenchGas) {
		this.sulfurContentOfQuenchGas = sulfurContentOfQuenchGas;
	}

	public String getTypeOfEquipmentCd() {
		return typeOfEquipmentCd;
	}

	public void setTypeOfEquipmentCd(String typeOfEquipmentCd) {
		this.typeOfEquipmentCd = typeOfEquipmentCd;
		if (!isEquipTypeDelayedCoking() && !isEquipTypeOther()) {
			setOdorMaskingAgentUsedCd(null);
			setBatchCycleTime(null);
			setQuenchCycleTime(null);
			setQuenchGasVolums(null);
			setSulfurContentOfQuenchGas(null);
		}
		requiredFieldSulfurContentOfQuenchGas();
	}

	public NSRApplicationEUTypeCCU() {
		super();
	}

	public NSRApplicationEUTypeCCU(NSRApplicationEUTypeCCU old) {
		super(old);
		if (old != null) {
			setRequestedChargeRate(old.getRequestedChargeRate());
			setOdorMaskingAgentUsedCd(old.getOdorMaskingAgentUsedCd());
			setBatchCycleTime(old.getBatchCycleTime());
			setQuenchCycleTime(old.getQuenchCycleTime());
			setQuenchGasVolums(old.getQuenchGasVolums());
			setSulfurContentOfQuenchGas(old.getSulfurContentOfQuenchGas());
			setTypeOfEquipmentCd(old.getTypeOfEquipmentCd());
		}
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		super.populate(rs);

		setApplicationEUId(AbstractDAO.getInteger(rs, "application_eu_id"));
		setRequestedChargeRate(AbstractDAO.getInteger(rs,
				"requested_charge_rate"));
		setOdorMaskingAgentUsedCd(rs.getString("odor_masking_agent_used_cd"));
		setBatchCycleTime(AbstractDAO.getInteger(rs, "batch_cycle_time"));
		setQuenchCycleTime(AbstractDAO.getInteger(rs, "quench_cycle_time"));
		setQuenchGasVolums(AbstractDAO.getFloat(rs, "quench_gas_volums"));
		setSulfurContentOfQuenchGas(rs.getBigDecimal(
				"sulfur_content_of_quench_gas"));
		setTypeOfEquipmentCd(rs.getString("type_of_equipment_cd"));
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
		requiredFieldRequestedChargeRate();
		if (isEquipTypeDelayedCoking() || isEquipTypeOther()) {
			requiredFieldOdorMaskingAgentUsedCd();
			requiredFieldBatchCycleTime();
			requiredFieldQuenchCycleTime();
			requiredFieldQuenchGasVolums();
			requiredFieldSulfurContentOfQuenchGas();
		}
	}

	public void validateRanges() {
		checkRangeValues(this.requestedChargeRate, new Integer(1), new Integer(
				Integer.MAX_VALUE),
				PAGE_VIEW_ID_PREFIX + "requestedChargeRate",
				"Requested Charge Rate (Barrels/day)");
		
		if (isEquipTypeDelayedCoking() || isEquipTypeOther()) {

			checkRangeValues(this.batchCycleTime, new Integer(1), new Integer(
					Integer.MAX_VALUE), PAGE_VIEW_ID_PREFIX + "batchCycleTime",
					"Batch Cycle Time (hr)");

			checkRangeValues(this.quenchCycleTime, new Integer(1), new Integer(
					Integer.MAX_VALUE),
					PAGE_VIEW_ID_PREFIX + "quenchCycleTime",
					"Quench Cycle Time (hr)");

			checkRangeValues(this.quenchGasVolums, 0.01f, Float.MAX_VALUE,
					PAGE_VIEW_ID_PREFIX + "quenchGasVolums",
					"Quench Gas Volume (scf/hr)");

			checkRangeValues(this.sulfurContentOfQuenchGas, new BigDecimal(0),
					new BigDecimal(100), PAGE_VIEW_ID_PREFIX
							+ "sulfurContentOfQuenchGas",
					"Sulfur Content of Quench Gas (%)");
		}
	}

	private void requiredFieldRequestedChargeRate() {
		requiredField(this.requestedChargeRate, PAGE_VIEW_ID_PREFIX + "requestedChargeRate",
				"Requested Charge Rate (Barrels/day)", "requestedChargeRate");
	}

	private void requiredFieldOdorMaskingAgentUsedCd() {
		requiredField(this.odorMaskingAgentUsedCd, PAGE_VIEW_ID_PREFIX + "odorMaskingAgentUsedCd",
				"Odor Masking Agent Used", "odorMaskingAgentUsedCd");
	}

	private void requiredFieldBatchCycleTime() {
		requiredField(this.batchCycleTime, PAGE_VIEW_ID_PREFIX
				+ "batchCycleTime", "Batch Cycle Time (hr)", "batchCycleTime");
	}

	private void requiredFieldQuenchCycleTime() {
		requiredField(this.quenchCycleTime, PAGE_VIEW_ID_PREFIX
				+ "quenchCycleTime", "Quench Cycle Time (hr)",
				"quenchCycleTime");
	}

	private void requiredFieldQuenchGasVolums() {
		requiredField(this.quenchGasVolums, PAGE_VIEW_ID_PREFIX
				+ "quenchGasVolums", "Quench Gas Volume (scf/hr)",
				"quenchGasVolums");
	}

	private void requiredFieldSulfurContentOfQuenchGas() {
		requiredField(this.typeOfEquipmentCd, PAGE_VIEW_ID_PREFIX
				+ "typeOfEquipmentCd", "Type of Equipment", "typeOfEquipmentCd");
	}
	
	public boolean isEquipTypeDelayedCoking() {
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(this.typeOfEquipmentCd)
				&& AppEUCCUEquipTypeDef.DELAYED_COKING.equals(this.typeOfEquipmentCd);
		return ret;
	}
	
	public boolean isEquipTypeOther() {
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(this.typeOfEquipmentCd)
				&& AppEUCCUEquipTypeDef.OTHER.equals(this.typeOfEquipmentCd);
		return ret;
	}
}
