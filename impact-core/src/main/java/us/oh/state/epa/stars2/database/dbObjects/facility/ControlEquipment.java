package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.def.CeOperatingStatusDef;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.FacilityEmissionFlow;

public class ControlEquipment extends FacilityNode implements
		Comparable<ControlEquipment> {
	private int ceId;
	private String controlEquipmentId;
	private String manufacturer;
	private String model;
	private String regUserDesc; // Company Description
	private String dapcDesc;
	private String equipmentTypeCd;
	private String operatingStatusCd;
	private Timestamp contEquipInstallDate;
	@SuppressWarnings("unused")
	private Long contEquipInstallDateLong;
	private List<ControlEquipment> controlEquips = new ArrayList<ControlEquipment>(
			0);
	private List<EgressPoint> egressPoints = new ArrayList<EgressPoint>(0);
	private List<DataDetail> ceTypeData = new ArrayList<DataDetail>(0);
	private List<PollutantsControlled> pollutantsControlled = new ArrayList<PollutantsControlled>(
			0);
	private List<FacilityEmissionFlow> ceEmissionFlows = new ArrayList<FacilityEmissionFlow>(
			0);
	private String operatingStatusDsc;
	private String associatedEpaEuIds; // Associated EU Ids; Not in database
	private String companyId;
	private String wiseViewId;

	public ControlEquipment() {
		super();
		// set to default operating Status
		operatingStatusCd = CeOperatingStatusDef.OP;
		requiredFields();
	}

	public ControlEquipment(ControlEquipment ce) {
		super(ce);

		if (ce != null) {
			newObject = false;
			this.setManufacturer(ce.getManufacturer());
			this.setModel(ce.getModel());
			this.setRegUserDesc(ce.getRegUserDesc());
			this.setDapcDesc(ce.getDapcDesc());
			this.setEquipmentTypeCd(ce.getEquipmentTypeCd());
			this.setOperatingStatusCd(ce.getOperatingStatusCd());
			this.setContEquipInstallDate(ce.getContEquipInstallDate());
			this.controlEquips.addAll(ce.controlEquips);
			this.ceTypeData.addAll(ce.ceTypeData);
			this.pollutantsControlled.addAll(ce.pollutantsControlled);
			this.ceEmissionFlows.addAll(ce.ceEmissionFlows);
			this.setOperatingStatusDsc(ce.getOperatingStatusDsc());
			this.setCeId(ce.ceId);
		}
		requiredFields();
	}

	public void requiredFields() {
		String msgStr = getMsgStr();

		requiredField(operatingStatusCd, "ceOperatingStatusCd",
				msgStr + "Operating Status", "controlEquipment:" + controlEquipmentId);
		requiredField(companyId, "companyId", msgStr + "Company Control Equipment ID", "controlEquipment:"
				+ controlEquipmentId);
		requiredField(equipmentTypeCd, "CntEquipType",
				msgStr + "Control Equipment Type", "controlEquipment:"
						+ controlEquipmentId);
		requiredField(regUserDesc, "regUserDesc", msgStr + "Company Control Equipment Description",
				"controlEquipment:" + controlEquipmentId);

		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			requiredField(dapcDesc, "aqdDesc", msgStr + "AQD Description",
					"controlEquipment:" + controlEquipmentId);
		}

		return;
	}

	public String getMouseOverTip() {
		String mouseOverTip = dapcDesc;
		if (dapcDesc != null && !dapcDesc.equals("")) {
			if (dapcDesc.length() > 40) {
				mouseOverTip = dapcDesc.substring(0, 39) + "...";
			} else {
				mouseOverTip = dapcDesc;
			}
		}
		return mouseOverTip;
	}

	public int getCeId() {
		return ceId;
	}

	public void setCeId(int ceId) {
		this.ceId = ceId;
	}

	/**
	 * @return
	 */
	public final String getControlEquipmentId() {
		return controlEquipmentId;
	}

	/**
	 * @param controlEquipmentNm
	 */
	public final void setControlEquipmentId(String controlEquipmentId) {
		this.controlEquipmentId = controlEquipmentId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		requiredField(companyId, "companyId", "Company Control Equipment ID");
		checkDirty("ceid", companyId, this.companyId, companyId);

		this.companyId = companyId;
	}

	/**
	 * @return
	 */
	public final String getRegUserDesc() {
		return regUserDesc;
	}

	/**
	 * @param regUserDesc
	 */
	public final void setRegUserDesc(String regUserDesc) {
		this.regUserDesc = regUserDesc;
	}

	/**
	 * @return
	 */
	public final String getDapcDesc() {
		return dapcDesc;
	}

	/**
	 * @param dapcDesc
	 */
	public final void setDapcDesc(String dapcDesc) {
		this.dapcDesc = dapcDesc;
	}

	/**
	 * @return
	 */
	public final String getEquipmentTypeCd() {
		return equipmentTypeCd;
	}

	/**
	 * @param equipmentTypeCd
	 */
	public final void setEquipmentTypeCd(String equipmentTypeCd) {
		checkDirty("cety", controlEquipmentId, ContEquipTypeDef.getData()
				.getItems().getItemDesc(this.equipmentTypeCd), ContEquipTypeDef
				.getData().getItems().getItemDesc(equipmentTypeCd));
		this.equipmentTypeCd = equipmentTypeCd;
	}

	/**
	 * @return
	 */
	public final List<ControlEquipment> getControlEquips() {
		return controlEquips;
	}

	public final ControlEquipment getLastControlEquip() {
		return controlEquips.get(controlEquips.size() - 1);
	}

	/**
	 * @param controlEquipment
	 */
	public final void addControlEquipment(ControlEquipment controlEquipment) {
		if (controlEquipment != null) {
			this.controlEquips.add(controlEquipment);
		}
	}

	/**
	 * @return
	 */
	public final List<EgressPoint> getEgressPoints() {
		return egressPoints;
	}

	/**
	 * @param egressPoint
	 */
	public final void addEgressPoint(EgressPoint egressPoint) {
		if (egressPoint != null) {
			this.egressPoints.add(egressPoint);
		}
	}

	/**
	 * @return
	 */
	public final String getManufacturer() {
		return manufacturer;
	}

	/**
	 * @param manufacture
	 */
	public final void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * @return
	 */
	public final String getModel() {
		return model;
	}

	/**
	 * @param model
	 */
	public final void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return
	 */
	public final Timestamp getContEquipInstallDate() {
		return contEquipInstallDate;
	}

	/**
	 * @param contEquipInstallDate
	 */
	public final void setContEquipInstallDate(Timestamp contEquipInstallDate) {
		this.contEquipInstallDate = contEquipInstallDate;
	}

	/**
	 * @param operatingStatusCd
	 */
	public final void setOperatingStatusCd(String operatingStatusCd) {
		requiredField(operatingStatusCd, "ceOperatingStatusCd",
				"Operating Status4", "controlEquipment:" + controlEquipmentId);
		checkDirty(
				"ceos",
				this.controlEquipmentId,
				CeOperatingStatusDef.getData().getItems()
						.getItemDesc(this.operatingStatusCd),
				CeOperatingStatusDef.getData().getItems()
						.getItemDesc(operatingStatusCd));
		fieldChangeEventLog(
				"fceo",
				this.controlEquipmentId,
				CeOperatingStatusDef.getData().getItems()
						.getItemDesc(this.operatingStatusCd),
				CeOperatingStatusDef.getData().getItems()
						.getItemDesc(operatingStatusCd));
		this.operatingStatusCd = operatingStatusCd;
	}

	/**
	 * @return
	 */
	public final String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	/**
	 * @return
	 */
	public final String getOperatingStatusDsc() {
		return operatingStatusDsc;
	}

	/**
	 * @param operatingStatusDsc
	 */
	public final void setOperatingStatusDsc(String operatingStatusDsc) {
		this.operatingStatusDsc = operatingStatusDsc;
	}

	public final List<PollutantsControlled> getPollutantsControlled() {
		return pollutantsControlled;
	}

	public final void setPollutantsControlled(
			List<PollutantsControlled> pollutantsControlled) {
		if (pollutantsControlled == null) {
			pollutantsControlled = new ArrayList<PollutantsControlled>(0);
		}
		this.pollutantsControlled = pollutantsControlled;
	}

	public final void addPollutantsControlled(PollutantsControlled pollCont) {
		pollutantsControlled.add(pollCont);
	}

	public final void removePollutantsControlled(PollutantsControlled pollCont) {
		checkDirty("cepd", controlEquipmentId, PollutantDef.getData()
				.getItems().getItemDesc(pollCont.getPollutantCd()),
				BaseDB.FLD_AUD_LOG_NO_VALUE);
		pollutantsControlled.remove(pollCont);
	}

	public final void addCeTypeData(Integer key, DataDetail value) {
		if (ceTypeData != null) {
			ceTypeData.add(value);
		}
	}

	public final DataDetail getCeTypeData(Integer key) {
		DataDetail ret = null;
		if (ceTypeData != null) {
			for (DataDetail temp : ceTypeData) {
				if (temp.getDataDetailId().equals(key)) {
					ret = temp;
					break;
				}
			}
		}

		return ret;
	}

	public final List<DataDetail> getCeDataDetails() {
		return ceTypeData;
	}

	public final void setCeDataDetails(List<DataDetail> dataDetails) {
		this.ceTypeData = dataDetails;
	}

	public final void setCeDataDetails(DataDetail[] dataDetails) {
		ceTypeData = new ArrayList<DataDetail>(dataDetails.length);
		for (DataDetail tempData : dataDetails) {
			ceTypeData.add(tempData);
		}
	}

	public final ControlEquipment findControlEquipment(String contEquipId) {
		for (ControlEquipment tempCE : controlEquips) {
			if (tempCE.getControlEquipmentId().equals(contEquipId)) {
				return tempCE;
			}
		}
		return null;
	}

	public final EgressPoint findEgressPoint(String releasePointId) {
		for (EgressPoint tempEGP : egressPoints) {
			if (tempEGP.getReleasePointId().equals(releasePointId)) {
				return tempEGP;
			}
		}
		return null;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.BaseDBObject#populate(java.sql.ResultSet)
	 */
	public final void populate(ResultSet rs) {
		try {
			super.populate(rs);
			setCeId(rs.getInt("ce_id"));
			setCompanyId(rs.getString("company_id"));
			setControlEquipmentId(rs.getString("control_equip_id"));
			setFpNodeId(AbstractDAO.getInteger(rs, "controlEquip_nodeId"));
			setLastModified(AbstractDAO.getInteger(rs, "controlEquipment_lm"));
			setManufacturer(rs.getString("manufacturer"));
			setModel(rs.getString("model"));
			setDapcDesc(rs.getString("dapc_dsc"));
			setRegUserDesc(rs.getString("regulated_user_dsc"));
			setEquipmentTypeCd(rs.getString("equipment_type_cd"));
			setContEquipInstallDate(rs.getTimestamp("initial_install_dt"));
			setOperatingStatusCd(rs.getString("operating_status_cd"));
			setWiseViewId(rs.getString("wiseview_id"));

		} catch (SQLException sqle) {
			logger.error("Required field error");
		} finally {
			newObject = false;
		}
	}

	private void validatePollContTable() {
		HashMap<String, Integer> values = new HashMap<String, Integer>(0);
		ValidationMessage validationMessage;

		String msgStr = getMsgStr();
		
		if (pollutantsControlled.size() == 0) {
			validationMessage = new ValidationMessage(
					"pollutantCd",
					msgStr + "Pollutant not specified in the Pollutants Controlled table",
					ValidationMessage.Severity.ERROR, "controlEquipment:"
							+ controlEquipmentId);
			validationMessages.put("pollutantCd", validationMessage);
		}

		for (PollutantsControlled tempPoll : pollutantsControlled) {
			String temp = tempPoll.getPollutantCd();
			if (temp == null || temp.equals("")) {
				validationMessage = new ValidationMessage(
						"pollutantCd",
						msgStr + "Pollutant not specified in the Pollutants Controlled table",
						ValidationMessage.Severity.ERROR, "controlEquipment:"
								+ controlEquipmentId);
				validationMessages.put("pollutantCd", validationMessage);
				return;
			} else if (values.containsKey(temp)) {
				validationMessage = new ValidationMessage(
						"pollutantCd",
						msgStr + "Duplicate Pollutant in the Pollutants Controlled table",
						ValidationMessage.Severity.ERROR, "controlEquipment:"
								+ controlEquipmentId);
				validationMessages.put("pollutantCd", validationMessage);
				return;
			} else {
				values.put(temp, 1);
			}
			String poll = tempPoll.getPollutantCd();
			try {
				poll = PollutantDef.getPollutantBaseDef(
						tempPoll.getPollutantCd()).getDescription();
			} catch (ApplicationException ae) {
				;
			}
			requiredField(tempPoll.getCaptureEff(), "captureEff" + poll,
					msgStr  + "Capture Efficiency for " + poll);
			requiredField(tempPoll.getOperContEff(), "controlEff" + poll,
					msgStr + "Operating Control Efficiency for " + poll);
			requiredField(tempPoll.getDesignContEff(), "designEff" + poll,
					msgStr + "Design Control Efficiency for " + poll);
			// boolean captureOK = true;
			// if(tempPoll.getCaptureEff() == null ||
			// tempPoll.getCaptureEff().length() == 0) {
			// captureOK = false;
			// }
			// if(captureOK) {
			// try {
			// double c = Double.parseDouble(tempPoll.getCaptureEff());
			// if( c <= 0d) captureOK = false;
			// } catch (NumberFormatException nfe) {
			// captureOK = false;
			// }
			// }
			// if(!captureOK) {
			// String s2 = "pollutantCdCapture" + tempPoll.getPollutantCd();
			// String poll = tempPoll.getPollutantCd();
			// try {
			// poll =
			// PollutantDef.getPollutantBaseDef(tempPoll.getPollutantCd()).getDescription();
			// } catch (ApplicationException ae) {
			// ;
			// }
			// validationMessage = new ValidationMessage(
			// s2,
			// "The Capture Efficiency for pollutant " + poll +
			// " is usually much greater than 0%",
			// ValidationMessage.Severity.WARNING, "controlEquipment:"
			// + controlEquipmentId);
			// validationMessages.put(s2, validationMessage);
			// }
		}
		// validationMessages.remove("pollutantCd");
	}

	private void checkCeTypeRequiredAttribute(Integer data_id) {
		String msgStr = getMsgStr();
		
		DataDetail dataDetail = getCeTypeData(data_id);
		validationMessages.put(dataDetail.getJspId(),
				new ValidationMessage(dataDetail.getJspId(), 
						msgStr + "Attribute " 
						+ dataDetail.getDataDetailLbl() + " is not set.",
						ValidationMessage.Severity.ERROR, "controlEquipment:"
								+ controlEquipmentId));
		if (dataDetail.getDataDetailVal() != null) {
			String temp = dataDetail.getDataDetailVal().replaceAll(" ", "");
			if (temp.length() != 0) {
				validationMessages.remove(dataDetail.getJspId());
			}
		}
	}

	private void checkCeTypeRequiredAttributes(Integer[] data_ids) {
		for (int i = 0; i < data_ids.length; i++) {
			checkCeTypeRequiredAttribute(data_ids[i]);
		}
	}

	private void checkOtherSubTypeAttribute(Integer type_dd_id,
			Integer other_dd_id) {
		DataDetail type = getCeTypeData(type_dd_id);
		if (type.getDataDetailVal() == null
				|| type.getDataDetailVal().equals("")) {
			return;
		}
	}

	private void validateCeTypeData() {
		DataDetail type;

		for (DataDetail tempDetail : ceTypeData) {
			if (tempDetail.isRequired()) {
				checkCeTypeRequiredAttribute(tempDetail.getDataDetailId());
			}
		}

		if (equipmentTypeCd.equals(ContEquipTypeDef.ADS)) {
			checkOtherSubTypeAttribute(ContEquipTypeDef.CA_TYPE_DD_ID,
					ContEquipTypeDef.CA_OTHERS_DD_ID);
			type = getCeTypeData(ContEquipTypeDef.CA_TYPE_DD_ID);
			if (type.getDataDetailVal() == null
					|| type.getDataDetailVal().equals("")) {
				return;
			}
			if (type.getDataDetailVal().equals(
					ContEquipTypeDef.CA_COCENTRAT_TYPE)) {
				checkCeTypeRequiredAttributes(ContEquipTypeDef.CA_CONCEN_DD_IDS);
			} else {
				checkCeTypeRequiredAttributes(ContEquipTypeDef.CA_OTHER_CONCEN_DD_IDS);
			}
		} else if (equipmentTypeCd.equals(ContEquipTypeDef.CON)) {
			checkOtherSubTypeAttribute(ContEquipTypeDef.CD_TYPE_DD_ID,
					ContEquipTypeDef.CD_OTHERS_DD_ID);
			type = getCeTypeData(ContEquipTypeDef.CD_TYPE_DD_ID);
			if (type.getDataDetailVal() == null
					|| type.getDataDetailVal().equals("")) {
				return;
			}
			if (!type.getDataDetailVal().equals(
					ContEquipTypeDef.CD_FREEBOARD_TYPE)) {
				checkCeTypeRequiredAttributes(ContEquipTypeDef.CD_MAX_EXH_GAS_TEMP_DD_IDS);
			}
		} else if (equipmentTypeCd.equals(ContEquipTypeDef.FLA)) {
			checkOtherSubTypeAttribute(ContEquipTypeDef.FR_TYPE_DD_ID,
					ContEquipTypeDef.FR_OTHERS_DD_ID);
			type = getCeTypeData(ContEquipTypeDef.FR_TYPE_DD_ID);
			if (type.getDataDetailVal() == null
					|| type.getDataDetailVal().equals("")) {
				return;
			}
			if (type.getDataDetailVal().equals(
					ContEquipTypeDef.FR_ELEVATEDOPEN_TYPE)) {
				checkCeTypeRequiredAttribute(ContEquipTypeDef.FR_ELEV_OPEN_DD_ID);
			}

			type = getCeTypeData(ContEquipTypeDef.FR_FLAME_PRES_SENSOR_DD_ID);
			if (type.getDataDetailVal() == null
					|| type.getDataDetailVal().equals("")) {
				return;
			}
			if (type.getDataDetailVal().equals(
					ContEquipTypeDef.FR_PRES_SENSOR_YES)) {
				checkCeTypeRequiredAttribute(ContEquipTypeDef.FR_FLAME_PRES_TYPE_DD_ID);
			}

		} else if (equipmentTypeCd.equals(ContEquipTypeDef.ESP)) {
			checkOtherSubTypeAttribute(ContEquipTypeDef.EP_TYPE_DD_ID,
					ContEquipTypeDef.EP_OTHERS_DD_ID);
		} else if (equipmentTypeCd.equals(ContEquipTypeDef.BAG)) {
			checkOtherSubTypeAttribute(ContEquipTypeDef.FB_TYPE_DD_ID,
					ContEquipTypeDef.FB_OTHERS_DD_ID);
		} else if (equipmentTypeCd.equals(ContEquipTypeDef.CYC)) {
			checkOtherSubTypeAttribute(ContEquipTypeDef.CM_TYPE_DD_ID,
					ContEquipTypeDef.CM_OTHERS_DD_ID);
		} else if (equipmentTypeCd.equals(ContEquipTypeDef.WSC)) {
			checkOtherSubTypeAttribute(ContEquipTypeDef.WS_TYPE_DD_ID,
					ContEquipTypeDef.WS_OTHERS_DD_ID);
		} else if (equipmentTypeCd.equals(ContEquipTypeDef.PAF)) {
			type = getCeTypeData(ContEquipTypeDef.PB_TYPE_DD_ID);
			if (type.getDataDetailVal() == null
					|| type.getDataDetailVal().equals("")) {
				return;
			}
			if (type.getDataDetailVal().equals(
					ContEquipTypeDef.PB_PAINT_BOOTH_TYPE)) {
				checkCeTypeRequiredAttribute(ContEquipTypeDef.PB_CHG_FRQ_DD_ID);
			} else if (type.getDataDetailVal().equals(ContEquipTypeDef.OTHERS)) {
				checkCeTypeRequiredAttribute(ContEquipTypeDef.PB_CHG_FRQ_DD_ID);
			}
		} else if (equipmentTypeCd.equals(ContEquipTypeDef.FDS)) {
			checkOtherSubTypeAttribute(ContEquipTypeDef.FS_TYPE_DD_ID,
					ContEquipTypeDef.FS_OTHERS_DD_ID);
		} else if (equipmentTypeCd.equals(ContEquipTypeDef.CNC)) {
			type = getCeTypeData(ContEquipTypeDef.CR_TYPE_DD_ID);
			if (type.getDataDetailVal() == null
					|| type.getDataDetailVal().equals("")) {
				return;
			}
			if (type.getDataDetailVal().equals(
					ContEquipTypeDef.CR_NONSETECT_TYPE)) {
				checkCeTypeRequiredAttributes(ContEquipTypeDef.CR_NONSELECT_DD_IDS);
			} else {
				checkCeTypeRequiredAttributes(ContEquipTypeDef.CR_SELECT_DD_IDS);
			}
		}
	}

	public final ValidationMessage[] validate() {
		this.clearValidationMessages();
		requiredFields();

		if (equipmentTypeCd != null) {
			validateCeTypeData();
		}

		validatePollContTable();

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	private boolean checkAddControlEquipForLoop(ControlEquipment tempCE,
			String inControlEquipmentId) {
		boolean loop = false;

		if (tempCE.getControlEquipmentId().equals(inControlEquipmentId)) {
			return true;
		}

		if (tempCE.getControlEquips() == null) {
			return false;
		}

		for (ControlEquipment tempCE1 : tempCE.getControlEquips()) {
			loop = checkAddControlEquipForLoop(tempCE1, inControlEquipmentId);
			if (loop) {
				break;
			}
		}

		return loop;
	}

	public final ValidationMessage[] validateAddContEquip(ControlEquipment toCE) {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>(
				0);

		if (controlEquipmentId.equals(toCE.getControlEquipmentId())) {
			validMessages.add(new ValidationMessage("validateAddCE",
					"Cannot add control equipment with the same ID ",
					ValidationMessage.Severity.ERROR, "controlEquipment:"
							+ controlEquipmentId));
		}

		if (findControlEquipment(toCE.getControlEquipmentId()) != null) {
			validMessages.add(new ValidationMessage("validateAddCE",
					"Cannot add control equipment: "
							+ toCE.getControlEquipmentId()
							+ " ; it is duplicate control equipment",
					ValidationMessage.Severity.ERROR, "controlEquipment:"
							+ controlEquipmentId));
		}

		boolean loop = false;

		// check for loop
		if (toCE.getControlEquips() != null) {
			for (ControlEquipment tempCE : toCE.getControlEquips()) {
				loop = checkAddControlEquipForLoop(tempCE, controlEquipmentId);
				if (loop) {
					break;
				}
			}
		}

		if (loop) {
			validMessages.add(new ValidationMessage("validateAddCE",
					"Cannot add control equipment: "
							+ toCE.getControlEquipmentId()
							+ " ; It is in loop;",
					ValidationMessage.Severity.ERROR, "controlEquipment:"
							+ controlEquipmentId));
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	public final ValidationMessage[] validateAddEgressPoint(EgressPoint toEGP) {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>(
				0);

		if (findEgressPoint(toEGP.getReleasePointId()) != null) {
			validMessages.add(new ValidationMessage("validateAddEGP",
					"Cannot add release point: " + toEGP.getReleasePointId()
							+ " ; it is duplicate release point",
					ValidationMessage.Severity.ERROR, "controlEquipment:"
							+ controlEquipmentId));
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	/* START of Validation of emission reports */

	public final void validateFERandES(List<ValidationMessage> valMessages) {
		// no need to check for ID.

		requiredField(regUserDesc, "regUserDesc", "Company Description",
				"controlEquipment:" + controlEquipmentId, valMessages);
		requiredField(operatingStatusCd, "ceOperatingStatusCd",
				"Operating Status2", "controlEquipment:" + controlEquipmentId,
				valMessages);
		requiredField(equipmentTypeCd, "CntEquipType", "Equipment Type",
				"controlEquipment:" + controlEquipmentId, valMessages);
		if (pollutantsControlled.isEmpty()) {
			valMessages.add(new ValidationMessage("CePollutantTab",
					"Control equipment: " + controlEquipmentId
							+ "does not have any controlled pollutants",
					ValidationMessage.Severity.ERROR, "controlEquipment:"
							+ controlEquipmentId));
		} else {
			// What do we need to do?
		}
	}

	public final void setControlEquips(List<ControlEquipment> controlEquips) {
		this.controlEquips = controlEquips;
	}

	public final void setEgressPoints(List<EgressPoint> egressPoints) {
		this.egressPoints = egressPoints;
	}

	public final Long getContEquipInstallDateLong() {
		if (contEquipInstallDate == null) {
			return null;
		}
		return contEquipInstallDate.getTime();
	}

	public final void setContEquipInstallDateLong(Long contEquipInstallDateLong) {
		if (contEquipInstallDateLong != null) {
			contEquipInstallDate = new Timestamp(contEquipInstallDateLong);
		}
	}

	public void copyControlEquipmentData(ControlEquipment dapcContEquip) {
		setDapcDesc(dapcContEquip.getDapcDesc());
	}

	public PollutantsControlled getPollCont(PollutantsControlled pollCont) {
		PollutantsControlled ret = null;
		for (PollutantsControlled tempPoll : pollutantsControlled) {
			if (tempPoll.getPollutantCd().equals(pollCont.getPollutantCd())) {
				ret = tempPoll;
				break;
			}
		}
		return ret;
	}

	public PollutantsControlled getPollCont(String pollCd) {
		PollutantsControlled ret = null;
		for (PollutantsControlled tempPoll : pollutantsControlled) {
			if (tempPoll.getPollutantCd().equals(pollCd)) {
				ret = tempPoll;
				break;
			}
		}
		return ret;
	}

	public float captureEfficencyOverall() {
		float sum = 0;
		int cnt = 0;
		if (pollutantsControlled != null) {
			for (PollutantsControlled pc : pollutantsControlled) {
				float value = 0;
				if (pc.getCaptureEff().length() > 0) {
					cnt++;
					value = Float.parseFloat(pc.getCaptureEff());
				}
				sum = sum + value;
			}
		}
		float avg = 0;
		if (cnt > 0) {
			avg = sum / cnt;
		}
		return avg;
	}

	public EgressPoint getEgressPoint(EgressPoint egp) {
		EgressPoint ret = null;
		for (EgressPoint tempEGP : egressPoints) {
			if (tempEGP.getCorrelationId().equals(egp.getCorrelationId())) {
				ret = tempEGP;
				break;
			}
		}
		return ret;
	}

	public ControlEquipment getControlEquipment(ControlEquipment ce) {
		ControlEquipment ret = null;
		for (ControlEquipment tempCE : controlEquips) {
			if (tempCE.getCorrelationId().equals(ce.getCorrelationId())) {
				ret = tempCE;
				break;
			}
		}
		return ret;
	}

	public String getAssociatedEpaEuIds() {
		return associatedEpaEuIds;
	}

	public void setAssociatedEpaEuIds(String epaEuId) {
		if (epaEuId != null) {
			if (associatedEpaEuIds != null) {
				if (!associatedEpaEuIds.contains(epaEuId)) {
					associatedEpaEuIds += ", " + epaEuId;
				}
			} else {
				associatedEpaEuIds = epaEuId;
			}
		}
	}

	public void addEgressPoints(HashSet<EgressPoint> set) {
		set.addAll(egressPoints);
		for (ControlEquipment ce : controlEquips) {
			ce.addEgressPoints(set);
		}
		return;
	}

	public List<FacilityEmissionFlow> getCeEmissionFlows() {
		return ceEmissionFlows;
	}

	public List<FacilityEmissionFlow> getCeEmissionFlows(Facility f,
			String pollutantCd) {
		List<FacilityEmissionFlow> revisedFef = new ArrayList<FacilityEmissionFlow>(
				ceEmissionFlows.size());
		for (FacilityEmissionFlow fef : ceEmissionFlows) {
			if (FacilityEmissionFlow.CE_TYPE.equals(fef.getType())) {
				ControlEquipment ce = f.getControlEquipment(fef.getId());
				PollutantsControlled pc = ce.getPollCont(pollutantCd);
				if (pc != null && pc.isCaptureEffZero()) {
					FacilityEmissionFlow zeroFef = new FacilityEmissionFlow(fef);
					revisedFef.add(zeroFef);
				} else {
					FacilityEmissionFlow copyFef = new FacilityEmissionFlow(fef);
					copyFef.setPercent(fef.getPercent());
					revisedFef.add(copyFef);
				}
			} else {
				FacilityEmissionFlow copyFef = new FacilityEmissionFlow(fef);
				copyFef.setPercent(fef.getPercent());
				revisedFef.add(copyFef);
			}
		}
		return revisedFef;
	}

	public void setCeEmissionFlows(List<FacilityEmissionFlow> ceEmissionFlows) {
		this.ceEmissionFlows = ceEmissionFlows;
	}

	/**
	 * @param emissionFlow
	 */
	public final void addceEmissionFlow(FacilityEmissionFlow emissionFlow) {
		if (emissionFlow != null) {
			this.ceEmissionFlows.add(emissionFlow);
		}
	}

	public final boolean isEmissionFlowsAccess() {
		boolean showTable = false;
		if (ceEmissionFlows.size() > 1) {
			showTable = true;
		} else if (ceEmissionFlows.size() == 1
				&& ceEmissionFlows.get(0).getFlowFactor() <= 0) {
			showTable = true;
		}
		return showTable;
	}

	public final void addAllControlEquipments(HashSet<ControlEquipment> ce2) {
		ce2.addAll(controlEquips);
		for (ControlEquipment ce : controlEquips) {
			ce.addAllControlEquipments(ce2);
		}
	}

	public String getWiseViewId() {
		return wiseViewId;
	}

	public void setWiseViewId(String wiseViewId) {
		this.wiseViewId = wiseViewId;
	}

	@Override
	public int compareTo(ControlEquipment o) {
		return this.controlEquipmentId.compareTo(o.getControlEquipmentId());
	}
	
	public boolean checkForMonitoredPollutant(String pollutantCd, Integer year) {
		boolean ret = false;
		
		checkCem: for (EgressPoint releasePoint : getEgressPoints()) {
			if(!releasePoint.getOperatingStatusCd().equalsIgnoreCase(EgOperatingStatusDef.OP)) {
				continue;
			}
			for (EgressPointCem cem : releasePoint.getCems()) {
				if (cem.isPollutantMonitored(pollutantCd)) {
					ret = true;
					break checkCem;
				} else {
					continue;
				}
			}
		}
		
		if(ret) {
			return ret;
		} else {
			for(ControlEquipment ce : getControlEquips()) {
				if(!ce.getOperatingStatusCd().equalsIgnoreCase(CeOperatingStatusDef.OP)) {
					continue;
				}  else {
					if(ce.getContEquipInstallDate() != null) {
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date(ce.getContEquipInstallDate().getTime()));
						int ceInstallYear = cal.get(Calendar.YEAR);
						if(ceInstallYear > year) {
							continue;
						}
					}
				}
				ret = ce.checkForMonitoredPollutant(pollutantCd, year);
				if(ret) {
					break;
				}
			}
			return ret;
		}
	}
	
	// to identify control equipment id in the validation error msg
	private String getMsgStr() {
		String msgStr;
        if (controlEquipmentId == null || controlEquipmentId.equals("")) {
            String s = regUserDesc;
            if(s == null) {
                s = dapcDesc;
            }
            if (s != null) {
            	msgStr = "Control Equipment with description [" + s + "]: ";
            } else {
                msgStr = "Control Equipment: ";            	
            }
        } else {
            msgStr = "Control Equipment [" + controlEquipmentId + "]: ";
        }
        
        return msgStr;
	}
}
