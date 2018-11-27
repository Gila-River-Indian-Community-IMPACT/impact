package us.oh.state.epa.stars2.webcommon.facility;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.FacilityBO;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage.Severity;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityOwner;
import us.oh.state.epa.stars2.database.dbObjects.facility.HydrocarbonAnalysisPollutant;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantsControlled;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.def.CeOperatingStatusDef;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.ValidationControl;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.FacilityEmissionFlow;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.reports.ControlInfoRow;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.ReportTemplates;

@SuppressWarnings("serial")
public class FacilityValidation implements java.io.Serializable {
	
	private static Logger logger = Logger.getLogger(FacilityValidation.class);
	private static boolean turnedOn = false;

	public static final String NOSTACK_PROPERTY_SUFFIX = "numEgrPntsS";

	// This is used to easily turn validations into error or warning
	// errorOrWarningStackFugitive -- validations between profile and EIS report
	// to
	// make stack/fugitive emissions in report consistent with configuration of
	// profile.
	static Severity errorOrWarningStackFugitive = ValidationMessage.Severity.ERROR;

	private static Facility getFacilityAndCommonValidate(ValidationControl vc,
			Integer fpId, boolean staging, List<ValidationMessage> messages) {
		
		if (fpId == null) {
			logger.error("facility ID is null");
			return null;
		}

		Facility facility = null;
		try {
			FacilityService facilityBO = ServiceFactory.getInstance()
					.getFacilityService();
			facility = facilityBO.retrieveFacilityProfile(fpId, staging);
			if (facility == null) {
				logger.error("Cannot locate facility with fpID = " + fpId);
			} else {
				validateCommonAttributes(vc, facility, messages);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		if (facility == null) {
			messages.add(new ValidationMessage("facility",
					"Unable to retrieve facility",
					ValidationMessage.Severity.ERROR));
		}
		return facility;
	}

	private static void validateCommonFacility(ValidationControl vc,
			Facility facility, List<ValidationMessage> messages) {
		
		if (facility == null) {
			logger.error("No facility to validate");
		} else {
			validateCommonAttributes(vc, facility, messages);
		}
		
		if (facility == null) {
			messages.add(new ValidationMessage("facility",
					"Unable to retrieve facility",
					ValidationMessage.Severity.ERROR));
		}
	}
	
	private static void requiredEgressPointField(Object value,
			String fieldName, String fieldLabel, String referenceId,
			List<ValidationMessage> valMessages, String text) {
		if (value == null || value.equals("")) {
			valMessages.add(new ValidationMessage(fieldName, "Attribute "
					+ text + " " + fieldLabel + " is not set.",
					ValidationMessage.Severity.ERROR, referenceId));
		}
	}

	private static void requiredField(Object value, String fieldName,
			String fieldLabel, String referenceId,
			List<ValidationMessage> valMessages, String text) {
		if (value == null || value.equals("")) {
			valMessages.add(new ValidationMessage(fieldName, text
					+ ": Attribute " + fieldLabel + " is not set.",
					ValidationMessage.Severity.ERROR, referenceId));
		}
	}

	private static void requiredField(Object value, String fieldName,
			String fieldLabel, String referenceId,
			List<ValidationMessage> valMessages, String text, String id) {
		if (value == null || value.equals("")) {
			valMessages.add(new ValidationMessage(fieldName, text
					+ ": Attribute " + fieldLabel + " is not set.",
					ValidationMessage.Severity.ERROR, referenceId, id));
		}
	}

	private static void validateCommonAttributes(ValidationControl vc,
			Facility fac, List<ValidationMessage> messages) {
		
		if (ValidationControl.check(ValidationControl.FAC_BASIC, vc, logger)) {
			for (ValidationMessage facValMsg : fac.checkBasicFacility()) {
				messages.add(new ValidationMessage(facValMsg.getProperty(),
						"Facility: " + facValMsg.getMessage(), facValMsg
								.getSeverity(), "facility"));
			}
		}

		if (ValidationControl.check(ValidationControl.FAC_LOC, vc, logger)) {
			for (ValidationMessage addrValMsg : fac.getPhyAddr()
					.validateFacilityLocation()) {
				messages.add(new ValidationMessage(addrValMsg.getProperty(),
						"Facility: " + addrValMsg.getMessage(), addrValMsg
								.getSeverity(), "facility"));
			}

		}

		if (ValidationControl.check(ValidationControl.FAC_AFS, vc, logger)) {
			ValidationMessage afsValMsg = fac.checkAFS();
			if (afsValMsg != null) {
				messages.add(new ValidationMessage(afsValMsg.getProperty(),
						"Facility: " + afsValMsg.getMessage(), afsValMsg
								.getSeverity(), "facility"));
			}
		}

		if (ValidationControl.check(ValidationControl.FAC_OWNER, vc, logger)) {
			if (fac.getOwner() == null 
					|| (fac.getOwner() != null && fac.getOwner().getCompany() == null)) {
				messages.add(new ValidationMessage("facilityOwner",
						"Facility:  does not have any current owner",
						ValidationMessage.Severity.ERROR, "facility"));
			}
		}

		if (ValidationControl.check(ValidationControl.ENV_CONT, vc, logger)) {
			if (fac.getEnvironmentalContact() == null) {
				messages.add(new ValidationMessage("envContact",
						"Facility:  does not have an Environmental Contact",
						ValidationMessage.Severity.ERROR, "contact"));
			}
		}

		if (ValidationControl.check(ValidationControl.BILL_CONT, vc, logger)) {
			if (fac.getBillingContact() == null) {
				messages.add(new ValidationMessage("billContact",
						"Facility:  does not have Billing Contact",
						ValidationMessage.Severity.ERROR, "contact"));
			}
		}

		if (ValidationControl.check(ValidationControl.ONSITE_CONT, vc, logger)) {
			if (fac.getOnSiteContacts().length == 0) {
				messages.add(new ValidationMessage("Onsite",
						"Facility:  does not have On Site contact",
						ValidationMessage.Severity.ERROR, "contact"));
			}
		}

		if (ValidationControl.check(ValidationControl.OFFICAL_CONT, vc, logger)) {
			if (fac.getResponsibleOfficals().length == 0) {
				messages.add(new ValidationMessage(
						"responsibleOfficial",
						"Facility:  does not have Responsible Official contact",
						ValidationMessage.Severity.ERROR, "contact"));
			}
		}

		if (ValidationControl.check(ValidationControl.FAC_NCIS, vc, logger)) {
			if (fac.getNaicsCds().isEmpty()) {
				messages.add(new ValidationMessage(FacilityProfileBase.FAC_NO_NAICS,
						"Facility: Required attribute is not set: NAICS codes",
						ValidationMessage.Severity.ERROR, "facility"));
			}
		}

		if (ValidationControl.check(ValidationControl.FAC_API, vc, logger)) {
			if (fac.getFacilityTypeCd() != null 
					&& FacilityTypeDef.hasApis(fac.getFacilityTypeCd())
					&& FacilityTypeDef.requiresApis(fac.getFacilityTypeCd())
					&& fac.getApis().isEmpty()) {
				messages.add(new ValidationMessage(
						FacilityProfileBase.FAC_NO_API,
						"Facility: Required attribute is not set: API Numbers",
						ValidationMessage.Severity.ERROR, "facility"));				
			}
		}
		
		if (ValidationControl.check(ValidationControl.FAC_FED_RULE, vc, logger)) {
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				for (ValidationMessage fedValMsg : fac.checkFedRules()) {
					messages.add(new ValidationMessage(fedValMsg.getProperty(),
							"Facility: " + fedValMsg.getMessage(), fedValMsg
									.getSeverity(),
							FacilityProfileBase.RULES_REFERENCE));
				}
			}
		}

		if (ValidationControl
				.check(ValidationControl.FAC_OP_STATUS, vc, logger)) {
			for (ValidationMessage opValMsg : fac.checkOpStatus()) {
				messages.add(new ValidationMessage(opValMsg.getProperty(),
						"Facility: " + opValMsg.getMessage(), opValMsg
								.getSeverity(), "facility"));
			}
		}
		
		if (ValidationControl.check(ValidationControl.NSR_BILL_CONT, vc, logger)) {
			if (fac.getNSRBillingContact() == null) {
				messages.add(new ValidationMessage("nsrBillContact",
						"Facility: does not have NSR Billing Contact",
						ValidationMessage.Severity.ERROR, "contact"));
			}
		}
	}

	private static void checkCeTypeRequiredAttribute(ControlEquipment ce,
			List<ValidationMessage> messages, Integer data_id) {
		DataDetail dataDetail = ce.getCeTypeData(data_id);
		if (dataDetail != null) {
			String temp = "";
			if (dataDetail.getDataDetailVal() != null) {
				temp = dataDetail.getDataDetailVal().replaceAll(" ", "");
			}
			if (temp.length() == 0) {
				messages.add(new ValidationMessage(dataDetail.getJspId(),
						"Control Equipment [" + ce.getControlEquipmentId()
								+ "]" + ": Attribute "
								+ dataDetail.getDataDetailLbl()
								+ " is not set.",
						ValidationMessage.Severity.ERROR, "controlEquipment:"
								+ ce.getControlEquipmentId()));
			}
		}
	}

	private static void checkCeTypeRequiredAttributes(ValidationControl vc,
			ControlEquipment ce, List<ValidationMessage> messages,
			Integer[] data_ids) {
		if (ValidationControl.check(ValidationControl.CE_ADDNL, vc, logger)) {
			for (int i = 0; i < data_ids.length; i++) {
				checkCeTypeRequiredAttribute(ce, messages, data_ids[i]);
			}
		}
	}

	private static void checkOtherSubTypeAttribute(ControlEquipment ce,
			List<ValidationMessage> messages, Integer type_dd_id,
			Integer other_dd_id) {
		DataDetail type = ce.getCeTypeData(type_dd_id);
		if (type.getDataDetailVal() == null
				|| type.getDataDetailVal().trim().equals("")) {
			return;
		}
	}

	private static void validateCeTypeData(ValidationControl vc,
			ControlEquipment ce, List<ValidationMessage> messages) {
		if (!ValidationControl.check(ValidationControl.CE_ADDNL, vc, logger))
			return;
		DataDetail type;
		if (ce.getEquipmentTypeCd() != null) {
			for (DataDetail tempDetail : ce.getCeDataDetails().toArray(
					new DataDetail[0])) {
				if (tempDetail.isRequired()) {
					checkCeTypeRequiredAttribute(ce, messages,
							tempDetail.getDataDetailId());
				}
			}

			String equipmentTypeCd = ce.getEquipmentTypeCd();

			if (equipmentTypeCd.equals(ContEquipTypeDef.ADS)) {
				checkOtherSubTypeAttribute(ce, messages,
						ContEquipTypeDef.CA_TYPE_DD_ID,
						ContEquipTypeDef.CA_OTHERS_DD_ID);
				type = ce.getCeTypeData(ContEquipTypeDef.CA_TYPE_DD_ID);
				if (type.getDataDetailVal() == null
						|| type.getDataDetailVal().equals("")) {
					return;
				}
				if (type.getDataDetailVal().equals(
						ContEquipTypeDef.CA_COCENTRAT_TYPE)) {
					checkCeTypeRequiredAttributes(vc, ce, messages,
							ContEquipTypeDef.CA_CONCEN_DD_IDS);
				} else {
					checkCeTypeRequiredAttributes(vc, ce, messages,
							ContEquipTypeDef.CA_OTHER_CONCEN_DD_IDS);
				}
			} else if (equipmentTypeCd.equals(ContEquipTypeDef.CON)) {
				checkOtherSubTypeAttribute(ce, messages,
						ContEquipTypeDef.CD_TYPE_DD_ID,
						ContEquipTypeDef.CD_OTHERS_DD_ID);
				type = ce.getCeTypeData(ContEquipTypeDef.CD_TYPE_DD_ID);
				if (type.getDataDetailVal() == null
						|| type.getDataDetailVal().equals("")) {
					return;
				}
				if (!type.getDataDetailVal().equals(
						ContEquipTypeDef.CD_FREEBOARD_TYPE)) {
					checkCeTypeRequiredAttributes(vc, ce, messages,
							ContEquipTypeDef.CD_MAX_EXH_GAS_TEMP_DD_IDS);
				}
			} else if (equipmentTypeCd.equals(ContEquipTypeDef.FLA)) {
				checkOtherSubTypeAttribute(ce, messages,
						ContEquipTypeDef.FR_TYPE_DD_ID,
						ContEquipTypeDef.FR_OTHERS_DD_ID);
				type = ce.getCeTypeData(ContEquipTypeDef.FR_TYPE_DD_ID);
				if (type.getDataDetailVal() == null
						|| type.getDataDetailVal().equals("")) {
					return;
				}
				if (type.getDataDetailVal().equals(
						ContEquipTypeDef.FR_ELEVATEDOPEN_TYPE)) {
					checkCeTypeRequiredAttribute(ce, messages,
							ContEquipTypeDef.FR_ELEV_OPEN_DD_ID);
				}

				type = ce
						.getCeTypeData(ContEquipTypeDef.FR_FLAME_PRES_SENSOR_DD_ID);
				if (type.getDataDetailVal() == null
						|| type.getDataDetailVal().equals("")) {
					return;
				}
				if (type.getDataDetailVal().equals(
						ContEquipTypeDef.FR_PRES_SENSOR_YES)) {
					checkCeTypeRequiredAttribute(ce, messages,
							ContEquipTypeDef.FR_FLAME_PRES_TYPE_DD_ID);
				}

			} else if (equipmentTypeCd.equals(ContEquipTypeDef.ESP)) {
				checkOtherSubTypeAttribute(ce, messages,
						ContEquipTypeDef.EP_TYPE_DD_ID,
						ContEquipTypeDef.EP_OTHERS_DD_ID);
			} else if (equipmentTypeCd.equals(ContEquipTypeDef.BAG)) {
				checkOtherSubTypeAttribute(ce, messages,
						ContEquipTypeDef.FB_TYPE_DD_ID,
						ContEquipTypeDef.FB_OTHERS_DD_ID);
			} else if (equipmentTypeCd.equals(ContEquipTypeDef.CYC)) {
				checkOtherSubTypeAttribute(ce, messages,
						ContEquipTypeDef.CM_TYPE_DD_ID,
						ContEquipTypeDef.CM_OTHERS_DD_ID);
			} else if (equipmentTypeCd.equals(ContEquipTypeDef.WSC)) {
				checkOtherSubTypeAttribute(ce, messages,
						ContEquipTypeDef.WS_TYPE_DD_ID,
						ContEquipTypeDef.WS_OTHERS_DD_ID);
			} else if (equipmentTypeCd.equals(ContEquipTypeDef.PAF)) {
				type = ce.getCeTypeData(ContEquipTypeDef.PB_TYPE_DD_ID);
				if (type.getDataDetailVal() == null
						|| type.getDataDetailVal().equals("")) {
					return;
				}
				if (type.getDataDetailVal().equals(
						ContEquipTypeDef.PB_PAINT_BOOTH_TYPE)) {
					checkCeTypeRequiredAttribute(ce, messages,
							ContEquipTypeDef.PB_CHG_FRQ_DD_ID);
				} else if (type.getDataDetailVal().equals(
						ContEquipTypeDef.OTHERS)) {
					checkCeTypeRequiredAttribute(ce, messages,
							ContEquipTypeDef.PB_CHG_FRQ_DD_ID);
				}
			} else if (equipmentTypeCd.equals(ContEquipTypeDef.FDS)) {
				checkOtherSubTypeAttribute(ce, messages,
						ContEquipTypeDef.FS_TYPE_DD_ID,
						ContEquipTypeDef.FS_OTHERS_DD_ID);
			} else if (equipmentTypeCd.equals(ContEquipTypeDef.CNC)) {
				type = ce.getCeTypeData(ContEquipTypeDef.CR_TYPE_DD_ID);
				if (type.getDataDetailVal() == null
						|| type.getDataDetailVal().equals("")) {
					return;
				}
				if (type.getDataDetailVal().equals(
						ContEquipTypeDef.CR_NONSETECT_TYPE)) {
					checkCeTypeRequiredAttributes(vc, ce, messages,
							ContEquipTypeDef.CR_NONSELECT_DD_IDS);
				} else {
					checkCeTypeRequiredAttributes(vc, ce, messages,
							ContEquipTypeDef.CR_SELECT_DD_IDS);
				}
			}
		}
	}

	private static void validateInOutletGasAttrs(ValidationControl vc,
			ControlEquipment ce, List<ValidationMessage> messages) {
		if (ValidationControl.check(ValidationControl.CE_GAS, vc, logger)) {
			// check for inlet and outlet Gas temp and flow rates
			checkCeTypeRequiredAttribute(ce, messages,
					ContEquipTypeDef.IN_GAS_FLOW_RATE);
			checkCeTypeRequiredAttribute(ce, messages,
					ContEquipTypeDef.OUT_GAS_FLOW_RATE);
			checkCeTypeRequiredAttribute(ce, messages,
					ContEquipTypeDef.IN_GAS_TEMP);
			checkCeTypeRequiredAttribute(ce, messages,
					ContEquipTypeDef.OUT_GAS_TEMP);
		}
	}

	private static void validateControEquipmentBase(ValidationControl vc,
			ControlEquipment ce, List<ValidationMessage> valMessages) {
		// Do not need to validate control equipment ID
		
		// Only require Initial Install date if CE Operating Status is Operating.
		if (ce.getOperatingStatusCd().equals(CeOperatingStatusDef.OP)) {
			if (ValidationControl.check(ValidationControl.CE_INIT_INSTALL, vc,
					logger)) {
				requiredField(ce.getContEquipInstallDate(), "installDate",
						"Initial Installation Date",
						"controlEquipment:" + ce.getControlEquipmentId(),
						valMessages,
						"Control Equipment [" + ce.getControlEquipmentId()
								+ "]");
			}
		}
		if (ValidationControl.check(ValidationControl.CE_BASIC, vc, logger)) {
			requiredField(ce.getRegUserDesc(), "regUserDesc",
					"Company Control Equipment Description",
					"controlEquipment:" + ce.getControlEquipmentId(),
					valMessages,
					"Control Equipment [" + ce.getControlEquipmentId() + "]");
			requiredField(ce.getOperatingStatusCd(), "ceOperatingStatusCd",
					"Operating Status",
					"controlEquipment:" + ce.getControlEquipmentId(),
					valMessages,
					"Control Equipment [" + ce.getControlEquipmentId() + "]");
			requiredField(ce.getEquipmentTypeCd(), "CntEquipType",
					"Control Equipment Type",
					"controlEquipment:" + ce.getControlEquipmentId(),
					valMessages,
					"Control Equipment [" + ce.getControlEquipmentId() + "]");
			requiredField(ce.getCompanyId(), "companyId", "Company Control Equipment Id",
					"controlEquipment:" + ce.getControlEquipmentId(),
					valMessages,
					"Control Equipment [" + ce.getControlEquipmentId() + "]");

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				requiredField(ce.getDapcDesc(), "aqdDesc", "AQD Description",
						"controlEquipment:" + ce.getControlEquipmentId(),
						valMessages,
						"Control Equipment [" + ce.getControlEquipmentId()
								+ "]");
			}

			if (ce.getPollutantsControlled().isEmpty()) {
				valMessages.add(new ValidationMessage("CePollutantTab",
						"Control Equipment [" + ce.getControlEquipmentId()
								+ "]"
								+ ": does not have any controlled pollutant",
						ValidationMessage.Severity.ERROR, "controlEquipment:"
								+ ce.getControlEquipmentId()));
			} else {
				boolean found = false;
				boolean foundNull = false;
				Float zero = new Float(0.0);
				for (PollutantsControlled poll : ce.getPollutantsControlled()) {
					if (StringUtils.isBlank(poll.getCaptureEff()) || StringUtils.isBlank(poll.getDesignContEff())
							|| StringUtils.isBlank(poll.getOperContEff())) {
						foundNull = true;
						found = true;
						break;
					}
					if (!poll.getCaptureEff().equals(zero)
							&& !poll.getDesignContEff().equals(zero)
							&& !poll.getOperContEff().equals(zero)) {
						found = true;
						break;
					}
				}
				if (foundNull) {
					valMessages
							.add(new ValidationMessage(
									"CePollutantTab",
									"Control Equipment ["
											+ ce.getControlEquipmentId()
											+ "]"
											+ ": has controlled pollutants with data incompletely set",
									ValidationMessage.Severity.ERROR,
									"controlEquipment:"
											+ ce.getControlEquipmentId()));
				}
				if (!found) {
					valMessages
							.add(new ValidationMessage(
									"CePollutantTab",
									"Control Equipment ["
											+ ce.getControlEquipmentId()
											+ "]"
											+ ": does not have any controlled pollutant with all data set",
									ValidationMessage.Severity.ERROR,
									"controlEquipment:"
											+ ce.getControlEquipmentId()));
				}
			}

			// validate flow factors.
			if (ce.getCeEmissionFlows().size() > 0) {
				boolean allZero = true;
				boolean missing = false;
				for (FacilityEmissionFlow fef : ce.getCeEmissionFlows()) {
					if (fef.getFlowFactor() != 0f) {
						allZero = false;
					}
					if (fef.getFlowFactor() == FacilityBO.MISSING_FLOW_PERCENT) {
						missing = true;
					}
				}
				if (allZero) {
					valMessages
							.add(new ValidationMessage(
									"zeroFlowF",
									"Control Equipment ["
											+ ce.getControlEquipmentId()
											+ "]"
											+ ": specifies all Flow Percentages as zero",
									ValidationMessage.Severity.ERROR,
									"controlEquipment:"
											+ ce.getControlEquipmentId()));
				}
				if (missing) {
					valMessages.add(new ValidationMessage("missingFlowF",
							"Control Equipment [" + ce.getControlEquipmentId()
									+ "]"
									+ ": is missing some Flow Percentages",
							ValidationMessage.Severity.ERROR,
							"controlEquipment:" + ce.getControlEquipmentId()));
				}
			}
		}
	}

	public static EgressPointCnt validateFacilityControlEquipment(Facility facility, ControlEquipment ce,
			List<ValidationMessage> valMessages, HashMap<String, Integer> valObject) {
		ValidationControl vc = new ValidationControl();
		vc.setFacilityCheck(true);
		return validateControEquipment(vc, facility, ce, valMessages, valObject, false);
	}
	
	private static EgressPointCnt validateControEquipment(ValidationControl vc,
			Facility facility, ControlEquipment ce,
			List<ValidationMessage> valMessages,
			HashMap<String, Integer> valObject, boolean forEpaEis) {
		int numFEgrPnts = 0;
		int numSEgrPnts = 0;
		if (valObject.containsKey("CEF:" + ce.getControlEquipmentId())) {
			return new EgressPointCnt(valObject.get("CEF:"
					+ ce.getControlEquipmentId()), valObject.get("CES:"
					+ ce.getControlEquipmentId()));
		}

		validateControEquipmentBase(vc, ce, valMessages);
		for (EgressPoint egp : ce.getEgressPoints()) {
			if (EgrPointTypeDef.isFugitive(egp)) {
				numFEgrPnts++;
			} else {
				FacilityEmissionFlow fef;
				fef = FacilityEmissionFlow.getEmissionFlow(
						ce.getCeEmissionFlows(),
						FacilityEmissionFlow.STACK_TYPE,
						egp.getReleasePointId());
				// account for stack if flow can reach it.
				if (fef != null && fef.getFlowFactor() != null) {
					if (fef.getPercentValue() > 0) {
						numSEgrPnts++;
					}
				} else {
					// this case should not occur
					numSEgrPnts++;
				}
			}
		}

		validateCeTypeData(vc, ce, valMessages);

		for (EgressPoint tempEGP : ce.getEgressPoints()) {
			validateEgressPoint(vc, facility, tempEGP, valMessages, valObject,
					forEpaEis);
		}

		// validateInOutletGasAttrs(vc, ce, valMessages);

		EgressPointCnt totCnt = new EgressPointCnt(numFEgrPnts, numSEgrPnts);
		for (ControlEquipment tempCE : ce.getControlEquips()) {
			FacilityEmissionFlow fef;
			fef = FacilityEmissionFlow.getEmissionFlow(ce.getCeEmissionFlows(),
					FacilityEmissionFlow.CE_TYPE,
					tempCE.getControlEquipmentId());
			// account for control equipment if flow can reach it.
			boolean canReach = false;
			if (fef != null && fef.getFlowFactor() != null) {
				if (fef.getPercentValue() > 0) {
					canReach = true;
				}
			} else {
				// this case should not occur
				canReach = true;
			}
			if (canReach) {
				totCnt.add(validateControEquipment(vc, facility, tempCE,
						valMessages, valObject, forEpaEis));
			}
		}
		valObject.put("CEF:" + ce.getControlEquipmentId(), totCnt.getFCnt());
		valObject.put("CES:" + ce.getControlEquipmentId(), totCnt.getSCnt());
		return totCnt;
	}

	/*
	 * release point
	 */
	private static void validateEgressPoint(ValidationControl vc,
			Facility facility, EgressPoint egp,
			List<ValidationMessage> valMessages,
			HashMap<String, Integer> valObject, boolean forEpaEis) {

		if (ValidationControl.check(ValidationControl.RP_BASIC, vc, logger)) {
			requiredField(egp.getRegulatedUserDsc(), "egrPntRegulatedUserDsc",
					"Company Release Point Description Description",
					"releasePoint:" + egp.getReleasePointId(), valMessages,
					"Release Point [" + egp.getReleasePointId() + "]");

			requiredField(egp.getEgressPointTypeCd(), "egressPointTypeCd",
					"Release Type", "releasePoint:" + egp.getReleasePointId(),
					valMessages, "Release Point [" + egp.getReleasePointId()
							+ "]");

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				requiredField(egp.getDapcDesc(), "egpDapcDesc",
						"AQD Description",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}

			requiredField(egp.getEgressPointId(), "egressPointId",
					"Company Release Point ID", "releasePoint:" + egp.getReleasePointId(),
					valMessages, "Release Point [" + egp.getReleasePointId()
							+ "]");
		}

		if (ValidationControl.check(ValidationControl.RP_OP_STATUS, vc, logger)) {
			requiredField(egp.getOperatingStatusCd(), "egOperatingStatusCd",
					"Operating status",
					"releasePoint:" + egp.getReleasePointId(), valMessages,
					"Release Point [" + egp.getReleasePointId() + "]");
		}

//		if (ValidationControl.check(ValidationControl.RP_CEM, vc, logger)) {
//			for (ValidationMessage valMsg : egp.checkCemTable()) {
//				valMessages.add(new ValidationMessage(valMsg.getProperty(),
//						"Release Point [" + egp.getReleasePointId() + "]"
//								+ ": " + valMsg.getMessage(), valMsg
//								.getSeverity(), "releasePoint:"
//								+ egp.getReleasePointId()));
//			}
//		}

		if (egp.getEgressPointTypeCd() == null) {
			return;
		}

		if (!egp.isFugitive()) {
			if (ValidationControl.check(ValidationControl.RP_NON_FUG_LOC, vc,
					logger)) {
				for (ValidationMessage valMsg : egp.checkLocation()) {
					valMessages.add(new ValidationMessage(valMsg.getProperty(),
							"Release Point [" + egp.getReleasePointId() + "]"
									+ ": " + valMsg.getMessage(), valMsg
									.getSeverity(), "releasePoint:"
									+ egp.getReleasePointId()));
				}
			}

			if (ValidationControl.check(ValidationControl.RP_ADD, vc, logger)) {
				for (ValidationMessage valMsg : egp.checkAttributes()) {
					valMessages.add(new ValidationMessage(valMsg.getProperty(),
							"Release Point [" + egp.getReleasePointId() + "]"
									+ ": " + valMsg.getMessage(), valMsg
									.getSeverity(), "releasePoint:"
									+ egp.getReleasePointId()));
				}
			}

			if (ValidationControl.check(ValidationControl.STACK_REL_HEIGHT, vc,
					logger)) {
				requiredField(egp.getReleaseHeight(), "releaseHeight",
						"Release Height (ft)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}
			/*if (ValidationControl.check(ValidationControl.STACK_FENCE, vc,
					logger)) {
				requiredField(egp.getStackFencelineDistance(),
						"stackFencelineDistance", "Fenceline Distance (ft)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}
			if (ValidationControl.check(ValidationControl.STACK_BUILDING, vc,
					logger)) {
				requiredEgressPointField(egp.getBuildingLength(),
						"buildingLength", "Building Length (ft)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
				requiredEgressPointField(egp.getBuildingWidth(),
						"buildingWidth", "Building Width (ft)", "releasePoint:"
								+ egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
				requiredEgressPointField(egp.getBuildingHeight(),
						"buildingHeight", "Building Height (ft)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}
			if (ValidationControl.check(ValidationControl.STACK_SHAPE, vc,
					logger)) {
				requiredEgressPointField(egp.getEgressPointShapeCd(),
						"egressPointShapeCd", "Shape",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}
			if (ValidationControl.check(ValidationControl.STACK_XSECT, vc,
					logger)) {
				requiredEgressPointField(egp.getCrossSectArea(),
						"crossSectArea", "Cross Sectional Area (square ft)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}*/
			if (ValidationControl.check(ValidationControl.STACK_DIAM, vc,
					logger)) {

				requiredEgressPointField(egp.getDiameter(), "diameter",
						"Diameter (ft)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}
			/*if (ValidationControl.check(ValidationControl.STACK_FLOW_TEMP, vc,
					logger)) {
				requiredEgressPointField(egp.getExitGasTempMax(),
						"exitGasTempMax", "Temp At Max. Oper (F)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
				requiredEgressPointField(egp.getExitGasTempAvg(),
						"exitGasTempAvg", "Temp At Avg. Oper (F)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
				requiredEgressPointField(egp.getExitGasFlowMax(),
						"exitGasFlowMax", "Flow At Max. Oper (F)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
				requiredEgressPointField(egp.getExitGasFlowAvg(),
						"exitGasFlowAvg", "Flow At Avg. Oper (F)",
						"releasePoint:" + egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}*/
		}

		if (egp.isFugitive()) {
			if (ValidationControl.check(ValidationControl.RP_FUG_LOC, vc,
					logger)) {
				for (ValidationMessage valMsg : egp.checkLocation()) {
					valMessages.add(new ValidationMessage(valMsg.getProperty(),
							"Release Point [" + egp.getReleasePointId() + "]"
									+ ": " + valMsg.getMessage(), valMsg
									.getSeverity(), "releasePoint:"
									+ egp.getReleasePointId()));
				}
			}

			if (ValidationControl.check(ValidationControl.RP_RELEASE_HEIGHT,
					vc, logger)) {
				requiredEgressPointField(egp.getReleaseHeight(),
						"areaReleaseHeight", "Release Height", "releasePoint:"
								+ egp.getReleasePointId(), valMessages,
						"Release Point [" + egp.getReleasePointId() + "]");
			}

			if (egp.getReleaseHeight() != null) {
				ValidationMessage valMsg = egp.checkReleaseHeight();
				if (valMsg != null) {
					valMessages.add(new ValidationMessage(valMsg.getProperty(),
							"Release Point [" + egp.getReleasePointId() + "]"
									+ ": " + valMsg.getMessage(), valMsg
									.getSeverity(), "releasePoint:"
									+ egp.getReleasePointId()));
				}
			}

		}
	}

	public static EgressPointCnt validateFacilityEmissionProcess(Facility facility, EmissionProcess ep, String epaEmuId,
			List<ValidationMessage> valMessages) {
		ValidationControl vc = new ValidationControl();
		vc.setFacilityCheck(true);
		return validateEmissionProcess(vc, facility, ep, epaEmuId, valMessages, null, false);
	}
	
	/*
	 * emission process
	 */

	private static EgressPointCnt validateEmissionProcess(ValidationControl vc,
			Facility facility, EmissionProcess ep, String epaEmuId,
			List<ValidationMessage> valMessages,
			HashMap<String, Integer> valObject, boolean forEpaEis) {
		if (ValidationControl.check(ValidationControl.PROC_DESC, vc, logger)) {
			requiredField(ep.getEmissionProcessNm(), "processId",
					"Company Process Description",
					"emissionProcess:" + ep.getProcessId(), valMessages,
					"Emission Process [" + ep.getProcessId() + "]", epaEmuId);
		}
		if (ValidationControl.check(ValidationControl.PROC_SCC, vc, logger)) {
			requiredField(ep.getSccId(), "sccId",
					"Source Classification Code (SCC)",
					"emissionProcess:" + ep.getProcessId(), valMessages,
					"Emission Process [" + ep.getProcessId() + "]", epaEmuId);
		}
		int numFEgrPnts = 0;
		int numSEgrPnts = 0;
		for (EgressPoint egp : ep.getEgressPoints()) {
			if (EgrPointTypeDef.isFugitive(egp)) {
				numFEgrPnts++;
				// validate fugitive Release Point
				validateEgressPoint(vc, facility, egp, valMessages, valObject,
						forEpaEis);
			} else {
				FacilityEmissionFlow fef;
				fef = FacilityEmissionFlow.getEmissionFlow(
						ep.getEpEmissionFlows(),
						FacilityEmissionFlow.STACK_TYPE,
						egp.getReleasePointId());
				// account for stack if flow can reach it.
				if (fef != null && fef.getFlowFactor() != null) {
					if (fef.getPercentValue() > 0) {
						numSEgrPnts++;
						// validate stack
						validateEgressPoint(vc, facility, egp, valMessages,
								valObject, forEpaEis);
					}
				} else {
					// this case should not occur
					numSEgrPnts++;
				}
			}
		}

		// validate flow factors.
		if (ep.getEpEmissionFlows().size() > 0) {
			boolean allZero = true;
			boolean missing = false;
			for (FacilityEmissionFlow fef : ep.getEpEmissionFlows()) {
				if (fef.getFlowFactor() != 0f) {
					allZero = false;
				}
				if (fef.getFlowFactor() == FacilityBO.MISSING_FLOW_PERCENT) {
					missing = true;
				}
			}
			if (allZero) {
				valMessages.add(new ValidationMessage("zeroPFlowF",
						"Emission Process [" + ep.getProcessId() + "]"
								+ ": specifies all Flow Percentages as zero",
						ValidationMessage.Severity.ERROR, "emissionProcess:"
								+ ep.getProcessId()));
			}
			if (missing) {
				valMessages.add(new ValidationMessage("missingPFlowF",
						"Emission Process [" + ep.getProcessId() + "]"
								+ ": is missing some Flow Percentages",
						ValidationMessage.Severity.ERROR, "emissionProcess:"
								+ ep.getProcessId()));
			}
		}

		return new EgressPointCnt(numFEgrPnts, numSEgrPnts);
	}

	public static void validateFacilityEmissionUnit(Facility facility, EmissionUnit eu, List<ValidationMessage> euMsgs) {
		ValidationControl vc = new ValidationControl();
		vc.setFacilityCheck(true);
		
		validateEmissionUnit(vc, facility, eu, euMsgs);
	}
	
	/*
	 * emission unit
	 */
	private static boolean validateEmissionUnit(ValidationControl vc,
			Facility facility, EmissionUnit eu,
			List<ValidationMessage> valMessages) {
		if (ValidationControl.check(ValidationControl.EU_BASIC, vc, logger)) {
			requiredField(eu.getOperatingStatusCd(), "euOperatingStatus",
					"Operating Status", "emissionUnit:" + eu.getEpaEmuId(),
					valMessages, "Emission unit", eu.getEpaEmuId());

			requiredField(eu.getRegulatedUserDsc(), "regulatedUserDsc",
					"Company Equipment Description", "emissionUnit:" + eu.getEpaEmuId(),
					valMessages, "Emission unit", eu.getEpaEmuId());

			requiredField(eu.getCompanyId(), "companyId", "Company Equipment ID",
					"emissionUnit:" + eu.getEpaEmuId(), valMessages,
					"Emission unit", eu.getEpaEmuId());

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				requiredField(eu.getEuDesc(), "euDesc", "AQD Description",
						"emissionUnit:" + eu.getEpaEmuId(), valMessages,
						"Emission unit", eu.getEpaEmuId());
			}

			requiredField(eu.getEmissionUnitTypeCd(), "emissionUnitType",
					"Emissions Unit Type", "emissionUnit:" + eu.getEpaEmuId(),
					valMessages, "Emission unit", eu.getEpaEmuId());
		}

		if (ValidationControl.check(ValidationControl.EU_ADD, vc, logger)) {
			for (ValidationMessage euTypeValMsg : eu.getEmissionUnitType()
					.validate()) {
				valMessages.add(new ValidationMessage(euTypeValMsg
						.getProperty(), "Emission unit: "
						+ euTypeValMsg.getMessage(),
						euTypeValMsg.getSeverity(), "emissionUnit:"
								+ eu.getEpaEmuId(), eu.getEpaEmuId()));
			}
		}

		if (ValidationControl.check(ValidationControl.EU_REP, vc, logger)) {
			for (ValidationMessage repValMsg : eu
					.checkEmissionUnitReplacements()) {
				valMessages.add(new ValidationMessage(repValMsg.getProperty(),
						"Emission unit: " + repValMsg.getMessage(), repValMsg
								.getSeverity(), "emissionUnit:"
								+ eu.getEpaEmuId(), eu.getEpaEmuId()));
			}
		}
		
		if (ValidationControl.check(ValidationControl.EU_ENG_ST, vc, logger)) {
			for (ValidationMessage repValMsg : eu
					.checkEngineEmissionUnitSerialNumbers()) {
				valMessages.add(new ValidationMessage(repValMsg.getProperty(),
						"Emission unit: " + repValMsg.getMessage(), repValMsg
								.getSeverity(), "emissionUnit:"
								+ eu.getEpaEmuId(), eu.getEpaEmuId()));
			}
		}		

		if (ValidationControl.check(ValidationControl.EU_PROCS, vc, logger)) {
			if (eu.getEmissionProcesses().size() == 0) {
				valMessages.add(new ValidationMessage(FacilityProfileBase.EU_NOPROCESSES_PREFIX,
						"Emission unit: does not have any processes",
						ValidationMessage.Severity.ERROR, "emissionUnit:"
								+ eu.getEpaEmuId(), eu.getEpaEmuId()));
			}
		}

		if (ValidationControl.check(ValidationControl.EU_PTE, vc, logger)) {
			for (ValidationMessage pteValMsg : eu.checkEuEmissionsTable()) {
				valMessages.add(new ValidationMessage(pteValMsg.getProperty(),
						"Emission unit: " + pteValMsg.getMessage(), pteValMsg
								.getSeverity(), "emissionUnit:"
								+ eu.getEpaEmuId(), eu.getEpaEmuId()));
			}
		}

		validateEmissionsReportDates(vc, facility, eu, valMessages);

		return true;
	}

	// function to add navigation functionality

	public static List<ValidationMessage> addNavToValidationMessages(
			Integer fpId, String facilityId, List<ValidationMessage> messages) {
		if (messages.size() == 0) {
			return messages;
		}

		String refID;
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		for (ValidationMessage valMsg : messages) {
			refID = valMsg.getReferenceID();
			if (refID.startsWith(FacilityProfileBase.CONTACTS_REFERENCE)) {
				valMsg.setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":" + refID + ":"
						+ FacilityProfileBase.CONTACTS_OUTCOME);
			} else if (refID.startsWith(FacilityProfileBase.RULES_REFERENCE)) {
				valMsg.setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":" + refID + ":"
						+ FacilityProfileBase.RULES_OUTCOME);
			} else if (valMsg.getProperty().endsWith(NOSTACK_PROPERTY_SUFFIX)) {
				StringTokenizer st = new StringTokenizer(refID, ":");
				st.nextToken();

				valMsg.setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":"
						+ FacilityProfileBase.EP_NOSTACK_PREFIX + ":"
						+ st.nextToken() + ":"
						+ FacilityProfileBase.FAC_OUTCOME);
			} else if (valMsg.getProperty().equalsIgnoreCase(FacilityProfileBase.EU_NOPROCESSES_PREFIX)) {
				StringTokenizer st = new StringTokenizer(refID, ":");
				st.nextToken();
				valMsg.setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":"
						+ FacilityProfileBase.EU_NOPROCESSES_PREFIX + ":"
						+ st.nextToken() + ":"
						+ FacilityProfileBase.FAC_OUTCOME);
			} else if (valMsg.getProperty().equalsIgnoreCase(FacilityProfileBase.FAC_NO_NAICS)) {
				valMsg.setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":" + refID + ":"
						+ FacilityProfileBase.FAC_OUTCOME + ":" + FacilityProfileBase.FAC_NO_NAICS);
			} else if (valMsg.getProperty().equalsIgnoreCase(FacilityProfileBase.FAC_NO_API)) {
				valMsg.setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":" + refID + ":"
						+ FacilityProfileBase.FAC_OUTCOME + ":" + FacilityProfileBase.FAC_NO_API);
			} else {
				
				valMsg.setReferenceID(ValidationBase.FACILITY_TAG + ":"
						+ facilityId + ":" + refID + ":"
						+ FacilityProfileBase.FAC_OUTCOME);
			}
				
			valMessages.add(valMsg);
		}

		FacilityProfileBase facProfile = (FacilityProfileBase) FacesUtil
				.getManagedBean("facilityProfile");
		if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
			facProfile.setStaging(true);
			facProfile.setEditStaging(true);
		}

		facProfile.setFacAfterValid(fpId);

		return valMessages;
	}

	/*
	 * EIS Emission report : emission unit
	 */
	private static void validateEISemissionUnit(ValidationControl vc,
			EmissionsReport report, Facility facility, EmissionUnit eu,
			List<ValidationMessage> valMessages,
			TreeMap<String, ValidationMessage> uniqueMessages,
			HashMap<String, Integer> valObject, boolean emissionsReporting,
			boolean forEpaEis) {
		if (eu == null) {
			logger.error("invalid Emission Unit ID passed.");
			return;
		}

		if (!validateEmissionUnit(vc, facility, eu, valMessages)) {
			return;
		}

		/*
		 * check for required attribute: -- Boiler/Turbine/Generator Design
		 * Capacity -- is not needed. The attribute is automatically set by SCC
		 * codes of processes.
		 */
		// if (ValidationControl.check(ValidationControl.EU_CAP, vc, logger)) {
		// if (eu.getDesignCapacityCd() != null
		// && !eu.getDesignCapacityCd().equals(DesignCapacityDef.NA)) {
		// requiredField(eu.getDesignCapacityUnitsCd(),
		// "designCapacityUnitsCd", "Design Capacity Units",
		// "emissionUnit:" + eu.getEpaEmuId(), valMessages,
		// "Emission unit", eu.getEpaEmuId());
		// requiredField(eu.getDesignCapacityUnitsVal(),
		// "designCapacityUnitsVal", "Design Capacity",
		// "emissionUnit:" + eu.getEpaEmuId(), valMessages,
		// "Emission unit", eu.getEpaEmuId());
		// }
		// }

		boolean EuHasFugitives = false;
		EmissionsReportEUGroup euG = null;
		// validate process
		int totFep = 0;
		for (EmissionProcess tempEP : eu.getEmissionProcesses().toArray(
				new EmissionProcess[0])) {

			EgressPointCnt totEpc = validateEmissionProcess(vc, facility,
					tempEP, eu.getEpaEmuId(), valMessages, valObject, forEpaEis);

			for (ControlEquipment tempCE : tempEP.getControlEquipments()) {
				FacilityEmissionFlow fef;
				fef = FacilityEmissionFlow.getEmissionFlow(
						tempEP.getEpEmissionFlows(),
						FacilityEmissionFlow.CE_TYPE,
						tempCE.getControlEquipmentId());
				// account for control equipment if flow can reach it.
				boolean canReach = false;
				if (fef != null && fef.getFlowFactor() != null) {
					if (fef.getPercentValue() > 0) {
						canReach = true;
					}
				} else {
					// this case should not occur
					canReach = true;
				}
				if (canReach) {
					EgressPointCnt epc = validateControEquipment(vc, facility,
							tempCE, valMessages, valObject, forEpaEis);
					for (EgressPoint egp : tempCE.getEgressPoints()) {
						validateEgressPoint(vc, facility, egp, valMessages,
								valObject, forEpaEis);
					}
					totEpc.add(epc);
				}
			}

			totFep = totFep + totEpc.getFCnt();
			SccCode sccCd = tempEP.getSccCode();
			String sccId = null;
			if (sccCd != null) {
				sccId = sccCd.getSccId();
			}

			EmissionsReportEU rptEU = null;
			rptEU = report.getEu(eu.getCorrEpaEmuId());
			if (rptEU == null) {
				logger.error("Unable to locate EmissionsReportEU in report "
						+ report.getEmissionsRptId() + " with correlation Id "
						+ eu.getCorrEpaEmuId()
						+ ".  Validation of release points ignored.");
				return;
			}
			EmissionsReportPeriod prtPeriod = rptEU.findPeriod(sccId);
			euG = null;
			if (prtPeriod == null) {
				// look for it in a group
				euG = report.findEuGContainingEU(eu.getCorrEpaEmuId(), sccId);
				if (euG == null) {
					logger.error("Unable to locate process with SCC Id "
							+ sccId
							+ " belonging to emission unit with correlation Id "
							+ eu.getCorrEpaEmuId() + " in report "
							+ report.getEmissionsRptId()
							+ ".  Validation of release points ignored.");
					return;
				}
				prtPeriod = euG.getPeriod();
			}

			// periodFireRows and scReports do not need good values because not
			// used the way getEmissions is called.
			FireRow[] periodFireRows = new FireRow[0];
			List<ReportTemplates> scList = report.getServiceCatalogs();
			ReportTemplates scReports = new ReportTemplates();
			if (scList != null && scList.size() == 1) {
				scReports = scList.get(0);
			}

			List<EmissionRow> periodEmissions = null;
			prtPeriod.setFireRows(periodFireRows);
			try {
				periodEmissions = EmissionRow.getEmissions(
						report.getReportYear(), prtPeriod, scReports, false,
						true, new Integer(10), logger);
			} catch (ApplicationException ae) {

			}

			int maxPollCdBuf = 40;
			StringBuffer pollsWithFugThatShouldNot = new StringBuffer();
			StringBuffer pollsWithoutFugThatShould = new StringBuffer();
			StringBuffer pollsWithStkThatShouldNot = new StringBuffer();
			StringBuffer pollsWithoutStkThatShould = new StringBuffer();
			for (EmissionRow er : periodEmissions) {
				if (er.getEmissionCalcMethodCd() == null)
					continue;
				if (er.getFugitiveEmissions() != null
						&& er.getFugitiveEmissionsV() != 0d)
					EuHasFugitives = true;
				// if factor method we will not find a mismatch.
				if (EmissionCalcMethodDef.isFactorMethod(er
						.getEmissionCalcMethodCd()))
					continue;
				try {
					List<ControlInfoRow> l = ControlInfoRow
							.generateControlMatrix(facility, tempEP, prtPeriod,
									er, false, false);
					if (ControlInfoRow.isProblems()) {
						String s2 = ControlInfoRow.getProblems().toString()
								+ " for report " + report.getEmissionsRptId()
								+ " and emission unit " + eu.getEpaEmuId()
								+ " and scc " + prtPeriod.getSccId()
								+ ", pollutantCd " + er.getPollutantCd();
						valMessages.add(new ValidationMessage("emissionRow"
								+ er.getPollutantCd(), s2,
								ValidationMessage.Severity.ERROR,
								"emissionUnit:" + eu.getEpaEmuId(), eu
										.getEpaEmuId()));
					}
					if (pollsWithoutFugThatShould.length() <= maxPollCdBuf) {
						if (ControlInfoRow.fugitiveTotal(l) != 0d
								&& er.getFugitiveEmissions() != null
								&& er.getFugitiveEmissionsV() == 0d
								&& er.getStackEmissions() != null
								&& er.getStackEmissionsV() != 0d) {
							if (pollsWithoutFugThatShould.length() > 0)
								pollsWithoutFugThatShould.append(", ");
							pollsWithoutFugThatShould.append(er
									.getPollutantCd());
						}
					}
					if (pollsWithFugThatShouldNot.length() <= maxPollCdBuf) {
						if (ControlInfoRow.fugitiveTotal(l) == 0d
								&& er.getFugitiveEmissions() != null
								&& er.getFugitiveEmissionsV() != 0d) {
							if (pollsWithFugThatShouldNot.length() > 0)
								pollsWithFugThatShouldNot.append(", ");
							pollsWithFugThatShouldNot.append(er
									.getPollutantCd());
						}
					}
					if (pollsWithoutStkThatShould.length() <= maxPollCdBuf) {
						if (ControlInfoRow.stackTotal(l) != 0d
								&& er.getStackEmissions() != null
								&& er.getStackEmissionsV() == 0d
								&& er.getFugitiveEmissions() != null
								&& er.getFugitiveEmissionsV() != 0d) {
							if (pollsWithoutStkThatShould.length() > 0)
								pollsWithoutStkThatShould.append(", ");
							pollsWithoutStkThatShould.append(er
									.getPollutantCd());
						}
					}
					if (pollsWithStkThatShouldNot.length() <= maxPollCdBuf) {
						if (ControlInfoRow.stackTotal(l) == 0d
								&& er.getStackEmissions() != null
								&& er.getStackEmissionsV() != 0d) {
							if (pollsWithStkThatShouldNot.length() > 0)
								pollsWithStkThatShouldNot.append(", ");
							pollsWithStkThatShouldNot.append(er
									.getPollutantCd());
						}
					}
				} catch (Exception e) {
					String s = "Failed to determine if fugitive/stack emissions possible for pollutantCd "
							+ er.getPollutantCd()
							+ " in facility "
							+ facility.getFacilityId()
							+ " in EU "
							+ eu.getEpaEmuId()
							+ "("
							+ eu.getCorrEpaEmuId()
							+ ") and process "
							+ prtPeriod.getProcessId()
							+ "--validation skipped.";
					logger.error(s, e);
				}
			}

			if (pollsWithStkThatShouldNot.length() > 0) {
				// No Stack but report period specifies stack emissions
				if (euG == null) {
					String key = eu.getEpaEmuId() + NOSTACK_PROPERTY_SUFFIX;
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Process \""
													+ tempEP.getProcessId()
													+ "\" does not terminate at any Stack release point (OR control efficiency is 100%) but stack emissions specified for pollutant(s) "
													+ pollsWithStkThatShouldNot
													+ " for this process.",
											errorOrWarningStackFugitive,
											"emissionProcess:"
													+ tempEP.getProcessId(), eu
													.getEpaEmuId()));
				} else { // is group
					String key = euG.getReportEuGroupName() + NOSTACK_PROPERTY_SUFFIX;
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Processes in report group \""
													+ euG.getReportEuGroupName()
													+ "\" do not terminate at any Stack release point (OR control efficiency is 100%) but stack emissions specified for pollutant(s) "
													+ pollsWithStkThatShouldNot
													+ "."
													+ "  A representative process is \""
													+ tempEP.getProcessId()
													+ "\".",
											errorOrWarningStackFugitive,
											"emissionProcess:"
													+ tempEP.getProcessId(), eu
													.getEpaEmuId()));
				}
			}
			if (pollsWithoutStkThatShould.length() > 0) {
				// Is a stack but no stack emissions in report period (but are
				// fugitive emissions)
				if (euG == null) {
					String key = eu.getEpaEmuId() + "numEgrPntsNS";
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Process \""
													+ tempEP.getProcessId()
													+ " \" does not specify stack emissions for pollutant(s) "
													+ pollsWithoutStkThatShould
													+ " even though the facility inventory defines a stack.  This includes pollutants:  "
													+ pollsWithoutStkThatShould
															.toString(),
											errorOrWarningStackFugitive,
											"emissionProcess:"
													+ tempEP.getProcessId(), eu
													.getEpaEmuId()));
				} else { // is group
					String key = euG.getReportEuGroupName() + "numEgrPntsNS";
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Processes in report group \""
													+ euG.getReportEuGroupName()
													+ " \" do not specify stack emissions for pollutant(s) "
													+ pollsWithoutStkThatShould
													+ " even though the facility inventory defines a stack.  This includes pollutants:  "
													+ pollsWithoutStkThatShould
															.toString()
													+ ".  A representative process is \""
													+ tempEP.getProcessId()
													+ "\".",
											errorOrWarningStackFugitive,
											"emissionProcess:"
													+ tempEP.getProcessId(), eu
													.getEpaEmuId()));
				}
			}

			if (pollsWithoutFugThatShould.length() > 0) {
				// Equipment indicates that fugitives would have been generated
				// and the report indicates zero fugitives but no fugitive
				// release points.
				if (euG == null) {
					String key = eu.getEpaEmuId() + "numEgrPntsNF";
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Process \""
													+ tempEP.getProcessId()
													+ " \" does not specify fugitive emissions for some pollutants even though the facility inventory indicates fugitives (less than 100% capture and/or no stack).  This includes pollutants:  "
													+ pollsWithoutFugThatShould
															.toString(),
											errorOrWarningStackFugitive,
											"emissionProcess:"
													+ tempEP.getProcessId(), eu
													.getEpaEmuId()));
				} else { // is a group
					String key = euG.getReportEuGroupName() + "numEgrPntsNFG"
							+ euG.getReportEuGroupName();
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Processes in report group \""
													+ euG.getReportEuGroupName()
													+ "\" do not specify fugitive emissions for some pollutants even though the facility inventory indicates fugitives (less than 100% capture and/or no stack)."
													+ "  A representative process is \""
													+ tempEP.getProcessId()
													+ "\".  This includes pollutants:  "
													+ pollsWithoutFugThatShould
															.toString(),
											errorOrWarningStackFugitive,
											"emissionProcess:"
													+ tempEP.getProcessId(), eu
													.getEpaEmuId()));
				}
			}
			if (turnedOn && pollsWithFugThatShouldNot.length() > 0) {
				// Equipment indicates that fugitives cannot be generated
				// but the report indicates fugitives.
				if (euG == null) {
					String key = eu.getEpaEmuId() + "numEgrPntsxNF";
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Process \""
													+ tempEP.getProcessId()
													+ " \" specifies fugitive emissions for some pollutants even though the facility inventory indicates that fugitives cannot be generated (100% capture and stacks).  This includes pollutants:  "
													+ pollsWithFugThatShouldNot
															.toString(),
											errorOrWarningStackFugitive,
											"emissionProcess:"
													+ tempEP.getProcessId(), eu
													.getEpaEmuId()));
				} else { // is a group
					String key = euG.getReportEuGroupName() + "numEgrPntsxNFG"
							+ euG.getReportEuGroupName();
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Processes in report group \""
													+ euG.getReportEuGroupName()
													+ "\" specifies fugitive emissions for some pollutants even though the facility inventory indicates that fugitives cannot be generated (100% capture and stacks)."
													+ ".  This includes pollutants:  "
													+ pollsWithFugThatShouldNot
															.toString()
													+ ".  A representative process is \""
													+ tempEP.getProcessId()
													+ "\".",
											errorOrWarningStackFugitive,
											"emissionProcess:"
													+ tempEP.getProcessId(), eu
													.getEpaEmuId()));
				}
			}
		}

		// Are there any fugitive emissions specified in report for this EU.
		if (turnedOn) {
			if (totFep > 0) {
				return;
			}
			if (EuHasFugitives) {
				if (euG == null) {
					String key = eu.getEpaEmuId() + "numEgrPntsF";
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Emission Unit does not specify any fugitive release point even though fugitive emissions are specified in Emission Unit "
													+ eu.getEpaEmuId(),
											errorOrWarningStackFugitive,
											"emissionUnit:" + eu.getEpaEmuId(),
											eu.getEpaEmuId()));
				} else { // is group
					String key = euG.getReportEuGroupName() + "numEgrPntsF";
					uniqueMessages
							.put(key,
									new ValidationMessage(
											key,
											"Processes in report group \""
													+ euG.getReportEuGroupName()
													+ "\" specify fugitive emissions but the Emission Units doenot specify any fugitive release point.",
											errorOrWarningStackFugitive,
											"emissionUnit:" + eu.getEpaEmuId(),
											eu.getEpaEmuId()));
				}
			}
		}
	}

	/*
	 * Validate NTV Emission report
	 */

	public static final List<ValidationMessage> validateNTVemissionReport(
			Integer fpId, boolean skipOwner) throws ValidationException {
	/*	ValidationControl vc = new ValidationControl();
		vc.setNtvEr(true);
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		Facility facility = getFacilityAndCommonValidate(vc, fpId, false,
				messages);

		if (facility == null) {
			throw new ValidationException(
					"Cannot access Facility with fp ID : " + fpId);
		}

		return addNavToValidationMessages(fpId, facility.getFacilityId(),
				messages);
*/		return new ArrayList<ValidationMessage>();
	}

	public static List<ValidationMessage> determineMissingBilling(
			Facility facility, Integer reportYear) {
		return determineMissingBilling(facility, reportYear, false);
	}

	public static List<ValidationMessage> determineMissingBilling(
			Facility facility, Integer reportYear, boolean forEpaEis) {
		Calendar cal = Calendar.getInstance();
		cal.set(reportYear, Calendar.DECEMBER, 31, 23, 59, 59);
		Timestamp anchor = new Timestamp(cal.getTimeInMillis());
		anchor.setNanos(0);
		Timestamp contactRefDate = facility.latestOwnerRefDate(anchor);
		ContactUtil activeBilling = facility.getActiveContact(
				ContactTypeDef.BILL, contactRefDate);
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		ValidationMessage vMsg = null;
		if (activeBilling == null || activeBilling.getContact() == null) {
			String err = null;
			if (contactRefDate == null) {
				err = "There is no active Billing Contact";
			} else {
				cal.setTimeInMillis(contactRefDate.getTime());
				DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
				Date theDate = cal.getTime();
				String dt = df.format(theDate);
				err = "There is no active Billing Contact up through date "
						+ dt + ", when ownership changed.";
			}
			vMsg = new ValidationMessage("MissingBilling", "Facility:  " + err,
					ValidationMessage.Severity.ERROR, "contact");
			messages.add(vMsg);
		}
		if (forEpaEis) {
			return messages;
		} else {
			return addNavToValidationMessages(facility.getFpId(),
					facility.getFacilityId(), messages);
		}
	}

	/*
	 * Check operating status and dates; including shutdown
	 */
	public static final void validateEmissionsReportDates(ValidationControl vc,
			Facility fac, EmissionUnit eu, List<ValidationMessage> messages) {
		if (ValidationControl.check(ValidationControl.EU_OP, vc, logger)) {
			for (ValidationMessage opValMsg : eu.checkOpStatus(fac
					.getPermitClassCd())) {
				messages.add(new ValidationMessage(opValMsg.getProperty(),
						"Emission unit: " + opValMsg.getMessage(), opValMsg
								.getSeverity(), "emissionUnit:"
								+ eu.getEpaEmuId(), eu.getEpaEmuId()));
			}
		}
	}

	public static final List<ValidationMessage> validateEISemissionReport(
			EmissionsReport report, Facility facility, List<Integer> euIds,
			List<Integer> exceedsThresholdEuIds) throws ValidationException {
		return validateEISemissionReport(report, facility, euIds,
				exceedsThresholdEuIds, false);
	}

	/*
	 * Validate EIS Emission report
	 */
	public static final List<ValidationMessage> validateEISemissionReport(
			EmissionsReport report, Facility facility, List<Integer> euIds,
			List<Integer> exceedsThresholdEuIds, boolean forEpaEis)
			throws ValidationException {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		TreeMap<String, ValidationMessage> uniqueMessages = new TreeMap<String, ValidationMessage>();

		HashMap<String, Integer> valObject = new HashMap<String, Integer>();
		ValidationControl vc = new ValidationControl();
		vc.setSmtvTvEr(true);
		//if (report.getReportYear() > 2007 && report.isRptEIS()) {
		//	vc.setErEis(true);
		//}

		validateCommonAttributes(vc, facility, messages);
		if (!euIds.isEmpty()) {
			for (Integer euId : euIds) {
				vc.setErThreasholdExceeded(exceedsThresholdEuIds.contains(euId));
				validateEISemissionUnit(vc, report, facility,
						facility.getEmissionUnit(euId), messages,
						uniqueMessages, valObject, true, forEpaEis);
			}
		}
		String lastLabel = "";
		for (ValidationMessage vm : uniqueMessages.values()) {
			if (lastLabel.equals(vm.getProperty())) {
				continue;
			}
			// keep only one message per group.
			lastLabel = vm.getProperty();
			messages.add(vm);
		}
		if (forEpaEis) {
			return messages;
		} else {
			return addNavToValidationMessages(facility.getFpId(),
					facility.getFacilityId(), messages);
		}
	}

	/* END of Validation of emission reports */

	/* START of Validation of PBR Notification */
	public static final List<ValidationMessage> validatePBRnotification(
			Integer fpId, List<EmissionUnit> eus) throws ValidationException {
		ValidationControl vc = new ValidationControl();
		vc.setPbrNotif(true);
		return validateApplication(fpId, eus, vc);
	}

	public static final List<ValidationMessage> validateApplication(
			Integer fpId, List<EmissionUnit> eus, ValidationControl vc)
			throws ValidationException {
		return validateApplication(null, fpId, eus, vc);
	}
	
	public static final List<ValidationMessage> validateApplication(
			Facility facility, List<EmissionUnit> eus, ValidationControl vc)
			throws ValidationException {
		return validateApplication(facility, null, eus, vc);
	}
	
	/*
	 * called by all validations from ApplicationBO
	 */
	public static final List<ValidationMessage> validateApplication(
			Facility facility, Integer fpId, List<EmissionUnit> eus, ValidationControl vc)
			throws ValidationException {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		
		if(facility == null) {
			facility = getFacilityAndCommonValidate(vc, fpId, true,
					messages);
		} else {
			logger.debug("Validating common facility attributes...");
			validateCommonFacility(vc, facility, messages);
			fpId = facility.getFpId();
		}

		if (facility == null) {
			throw new ValidationException(
					"Cannot access Facility with fp ID : " + fpId);
		}

		HashMap<String, Integer> valObject = new HashMap<String, Integer>();

		if (eus != null && !eus.isEmpty()) {
			logger.debug("Validating facility emissions units...");
			for (EmissionUnit eu : eus) {
				EmissionUnit facilityEU = facility.getEmissionUnit(eu
						.getEmuId());
				if (facilityEU != null) {
					// // copy attributes from application's version of EU to
					// facility's EU
					// // this is needed because these attributes are not copied
					// to the database.
					// facilityEU.setDemEgPntModeling(eu.isDemEgPntModeling());
					// facilityEU.setPsdOrScreenModeling(eu.isPsdOrScreenModeling());
					// Assume flags only set if it is a PTI/PTIO application
					vc.setDemEgPntModeling(eu.isDemEgPntModeling());
					vc.setPsdScreenModeling(eu.isPsdOrScreenModeling());
					
					validatePTIandPTIOemissionUnit(vc, facility, facilityEU,
							messages, valObject);

				} else {
					logger.error("Attempt to validate data for emission unit "
							+ eu.getEmuId()
							+ " which was not found in facility with fp_id = "
							+ fpId);
				}
			}
		}

		// if (!euIds.isEmpty()) {
		// for (Integer euId : euIds) {
		// validateTVPTOemissionUnit(facility, facility.getEmissionUnit(euId),
		// messages, valObject);
		// }
		// }

		logger.debug("Adding validation messages...");
		return addNavToValidationMessages(fpId, facility.getFacilityId(),
				messages);
	}

	/* END of Validation of Title V Permit Application */

	/* START of Validation of TV PTI/PTIO Permit Application */

	/*
	 * PTI/PTIO Permit Application : emission process
	 */
	private static void validatePTIandPTIOemissionProc(ValidationControl vc,
			Facility facility, EmissionProcess ep, String epaEmuId,
			List<ValidationMessage> valMessages,
			HashMap<String, Integer> valObject, EmissionUnit eu) {
		validateEmissionProcess(vc, facility, ep, epaEmuId, valMessages,
				valObject, false);

		// Check to see if there is any control equipment -- not a current
		// validation check
		// if(ValidationControl.check(ValidationControl.CE_ADDNL, vc, logger)) {
		// if(ep.getControlEquipments().size() == 0) {
		// valMessages.add(new ValidationMessage("noCE",
		// "Process \"" + ep.getProcessId() +
		// " \" has no control equipment",
		// ValidationMessage.Severity.ERROR,
		// "emissionProcess:" + ep.getProcessId(), eu.getEpaEmuId()));;
		// }
		// }

		for (ControlEquipment tempCE : ep.getControlEquipments()) {
			validateControEquipment(vc, facility, tempCE, valMessages,
					valObject, false);
		}
	}

	/*
	 * PTI/PTIO Permit Application : Emission Unit
	 */
	private static void validatePTIandPTIOemissionUnit(ValidationControl vc,
			Facility facility, EmissionUnit eu,
			List<ValidationMessage> valMessages,
			HashMap<String, Integer> valObject) {
		if (eu == null) {
			logger.error("invalid Emission Unit ID passed.");
			return;
		}

		if (validateEmissionUnit(vc, facility, eu, valMessages)) {
			// validate process
			for (EmissionProcess tempEP : eu.getEmissionProcesses().toArray(
					new EmissionProcess[0])) {
				validatePTIandPTIOemissionProc(vc, facility, tempEP,
						eu.getEpaEmuId(), valMessages, valObject, eu);
			}
		}
		return;
	}

	/*
	 * Title V Permit Application submission
	 */
	public static final List<ValidationMessage> validateTVPTOpermitApplication(
			Facility facility, List<EmissionUnit> eus) throws ValidationException {
		ValidationControl vc = new ValidationControl();
		vc.setTvPtoPermitApp(true);
		return validateApplication(facility, eus, vc);
	}

	/*
	 * PTI/PTIO Permit Application submission and PER(if web form)
	 * demEgPntModeling: flag to indicate that the DEMuser has indicated in the
	 * application that the release point emissions will trigger modelling
	 */
	public static final List<ValidationMessage> validatePTIandPTIOpermitApplication(
			Facility facility, List<EmissionUnit> eus) throws ValidationException {
		ValidationControl vc = new ValidationControl();
		vc.setTvPtiPtioPermitApp(true);
		return validateApplication(facility, eus, vc);
	}

	private static String formatContactdate(Contact contact, ContactType cType) {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy ");
		String ret = contact.getNameOnly() + " (Start Date : "
				+ df.format(cType.getStartDate()) + "; End Date : ";
		if (cType.getEndDate() == null) {
			ret += "CURRENT)";
		} else {
			ret += df.format(cType.getEndDate()) + ")";
		}
		return ret;
	}

	private static void generateOverlapValMessages(
			List<ValidationMessage> valMessages, String attribute, String str,
			Contact contact, ContactType cType, boolean contactModify,
			ContactType contactType) {
		valMessages.add(new ValidationMessage(attribute, str
				+ formatContactdate(contact, cType),
				ValidationMessage.Severity.ERROR, "contact:"
						+ cType.getContactId()));
	}

	public static List<ValidationMessage> validateFacilityOwnerTimeout(
			String facilityId, FacilityOwner owner) {
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		if (owner.getEndDate() != null) {
			if (owner.getEndDate().before(owner.getStartDate())) {
				valMessages
						.add(new ValidationMessage(
								"ownershipDateChange",
								"Error encountered when timing out existing owner for this facility. Owner "
										+ owner.getCompany().getName() + " (" 
										+ owner.getCompany().getCmpId()
										+ ") could not be timed out because existing owner start date is after the change of ownership date (end date). "
										+ "For Facility ID ("
										+ facilityId + ")",
								ValidationMessage.Severity.ERROR, "Facility: "
										+ facilityId));
				return valMessages;
			}
		} else {
			valMessages
					.add(new ValidationMessage("ownershipDateChange",
							"An ownership change date must be provided.",
							ValidationMessage.Severity.ERROR, "Facility: "
									+ facilityId));
			return valMessages;
		}

		return valMessages;
	}

	public static List<ValidationMessage> validateContactTypeTimeOut(
			ContactType newContactType, Contact contact) {
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();

		if (newContactType.getEndDate() != null) {

			if (newContactType.getEndDate().before(
					newContactType.getStartDate())) {
				valMessages
						.add(new ValidationMessage(
								"ownershipDateChange",
								"Error encountered when timing out existing contact types for this facility. Contact "
										+ contact.getFirstNm() + " " + contact.getLastNm() + " ("
										+ contact.getCntId()
										+ ") could not be timed out because existing contact type start date is after the change of ownership date (end date). "
										+ "For Facility ID ("
										+ newContactType.getFacilityId() + ")",
								ValidationMessage.Severity.ERROR, "Facility: "
										+ newContactType.getFacilityId()));
				return valMessages;
			}
		} else {
			valMessages.add(new ValidationMessage("ownershipDateChange",
					"An ownership change date must be provided.",
					ValidationMessage.Severity.ERROR, "contact:"
							+ newContactType.getContactId()));
			return valMessages;
		}

		return valMessages;
	}

	public static List<ValidationMessage> validateAddContactType(
			ContactType newContactType, List<Contact> facilityContacts) {
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		Timestamp startDate = newContactType.getStartDate();
		Timestamp endDate = newContactType.getEndDate();
		Integer contactId = newContactType.getContactId();
		String contactTypeCd = newContactType.getContactTypeCd();
		String facilityId = newContactType.getFacilityId();

		if (endDate != null) {
			if (startDate.after(endDate)) {
				valMessages.add(new ValidationMessage("endDate",
						"Contact's end date must be after start date",
						ValidationMessage.Severity.ERROR, "contact:"
								+ contactId));
				return valMessages;
			}
		}

		for (Contact facilityContact : facilityContacts) {
			for (ContactType facilityCtype : facilityContact.getContactTypes()) {
				Integer facContactId = facilityCtype.getContactId();
				String facContactTypeCd = facilityCtype.getContactTypeCd();
				Timestamp facContactStartDate = facilityCtype.getStartDate();
				Timestamp facContactEndDate = facilityCtype.getEndDate();
				String facContactFacilityId = facilityCtype.getFacilityId();

				if (!facilityId.equals(facContactFacilityId)) {
					continue;
				}

				if (!contactId.equals(facContactId)) {
					continue;
				}

				if (!contactTypeCd.equals(facContactTypeCd)) {
					continue;
				}

				if (facContactEndDate == null) {
					if (endDate == null) {
						valMessages
								.add(new ValidationMessage(
										"endDate",
										"Contact type already exists with same contact person and is active. For fp_id = "
												+ facilityId,
										ValidationMessage.Severity.ERROR,
										"contact:" + contactId));
						return valMessages;
					}

					if (startDate.after(facContactStartDate)) {
						valMessages
								.add(new ValidationMessage(
										"startDate",
										"Contact type's start date must be before existing contact type record with same contact person. For fp_id = "
												+ facilityId,
										ValidationMessage.Severity.ERROR,
										"contact:" + contactId));
						return valMessages;
					}

					if (endDate.after(facContactStartDate)) {
						valMessages
								.add(new ValidationMessage(
										"endDate",
										"Contact type's end date must be before existing contact type record start date. For fp_id = "
												+ facilityId,
										ValidationMessage.Severity.ERROR,
										"contact:" + contactId));
						return valMessages;
					}

					if (startDate.equals(facContactStartDate)) {
						valMessages
								.add(new ValidationMessage(
										"startDate",
										"Contact type's start date cannot be the same as existing contact type record with same contact person. For fp_id = "
												+ facilityId,
										ValidationMessage.Severity.ERROR,
										"contact:" + contactId));
						return valMessages;
					}
				} else {
					if (endDate == null) {
						if (startDate.before(facContactEndDate)) {
							valMessages
									.add(new ValidationMessage(
											"startDate",
											"An active contact type's start date cannot be before the end date of an existing contact type record with same contact person. For fp_id = "
													+ facilityId,
											ValidationMessage.Severity.ERROR,
											"contact:" + contactId));
							return valMessages;
						}
						if (startDate.equals(facContactStartDate)) {
							valMessages
									.add(new ValidationMessage(
											"startDate",
											"Contact type's start date cannot be the same as existing contact type record with same contact person. For fp_id = "
													+ facilityId,
											ValidationMessage.Severity.ERROR,
											"contact:" + contactId));
							return valMessages;
						}
					} else {
						if (startDate.equals(facContactStartDate)) {
							valMessages
									.add(new ValidationMessage(
											"startDate",
											"Contact type's start date cannot equal the start date of an existing contact type record with same contact person. For fp_id = "
													+ facilityId,
											ValidationMessage.Severity.ERROR,
											"contact:" + contactId));
							return valMessages;
						}

						if (startDate.before(facContactEndDate)) {

							if (startDate.after(facContactStartDate)) {
								valMessages
										.add(new ValidationMessage(
												"startDate",
												"Contact type's start date must be before existing contact type record with same contact person. For fp_id = "
														+ facilityId,
												ValidationMessage.Severity.ERROR,
												"contact:" + contactId));
								return valMessages;
							}

							if (endDate.after(facContactStartDate)) {
								valMessages
										.add(new ValidationMessage(
												"endDate",
												"Contact type's end date must be before existing contact type record with same contact person. For fp_id = "
														+ facilityId,
												ValidationMessage.Severity.ERROR,
												"contact:" + contactId));
								return valMessages;

							}
						}
					}
				}
			}
		}

		return valMessages;
	}

	public static List<ValidationMessage> validateEditContactType(
			ContactType oldContactType, ContactType newContactType,
			List<Contact> allContacts, List<Contact> globalContacts,
			boolean contactModify) {
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		if (oldContactType == null && contactModify) {
			// this is delete
			return valMessages;
		}

		if (newContactType.getEndDate() != null
				&& newContactType.getEndDate().before(
						newContactType.getStartDate())) {
			valMessages.add(new ValidationMessage("endDate",
					"Invalid End Date; It must be after Start Date",
					ValidationMessage.Severity.ERROR, "contact:"
							+ newContactType.getContactId()));
			return valMessages;
		}

		java.util.Date date = new java.util.Date();
		Timestamp currentTime = new Timestamp(date.getTime());

		if (newContactType.getEndDate() != null
				&& newContactType.getEndDate().after(currentTime)) {
			valMessages.add(new ValidationMessage("endDate",
					"An end date cannot be placed in the future.",
					ValidationMessage.Severity.ERROR, "contact:"
							+ newContactType.getContactId()));
			return valMessages;
		}

		if (newContactType.getStartDate() != null
				&& newContactType.getStartDate().after(currentTime)) {
			valMessages.add(new ValidationMessage("startDate",
					"A start date cannot be placed in the future.",
					ValidationMessage.Severity.ERROR, "contact:"
							+ newContactType.getContactId()));
			return valMessages;
		}

		for (Contact contact : globalContacts) {
			for (ContactType cType : contact.getContactTypes()) {
				if (cType.getContactTypeCd().equals(
						newContactType.getContactTypeCd())
						&& cType.getContactId().equals(
								newContactType.getContactId())) {
					if (!contact.isActive()
							&& newContactType.getEndDate() == null) {
						valMessages
								.add(new ValidationMessage(
										"contact",
										"Contact type cannot be marked active if contact is marked as inactive.",
										ValidationMessage.Severity.ERROR,
										"contact:"
												+ newContactType.getContactId()));
						return valMessages;
					}

					if (contactModify) {

						// changing the start date
						if (newContactType.getStartDate() != null) {
							if (cType.getStartDate() != null) {
								if (!oldContactType.getStartDate().equals(
										cType.getStartDate())) {
									if (newContactType.getStartDate().after(
											cType.getStartDate())
											&& newContactType.getStartDate()
													.before(cType.getEndDate())
											|| newContactType.getStartDate()
													.equals(cType
															.getStartDate())) {
										// start date is either before or after
										valMessages
												.add(new ValidationMessage(
														"startDate",
														"Invalid Start Date; new start date conflicts with an existing effective date period.",
														ValidationMessage.Severity.ERROR,
														"contact:"
																+ newContactType
																		.getContactId()));
										return valMessages;
									}
								}
							}
						}

						// changing an end date
						if (newContactType.getEndDate() != null) {
							if (cType.getEndDate() != null) {
								if (oldContactType.getEndDate() != null) {
									if (!oldContactType.getEndDate().equals(
											cType.getEndDate())) {
										if (newContactType.getEndDate().after(
												cType.getStartDate())
												&& newContactType.getEndDate()
														.before(cType
																.getEndDate())
												|| newContactType
														.getEndDate()
														.after(cType
																.getStartDate())
												&& newContactType.getEndDate()
														.equals(cType
																.getEndDate())
												|| newContactType
														.getStartDate()
														.before(cType
																.getStartDate())
												&& newContactType.getEndDate()
														.after(cType
																.getEndDate())) {
											// start date is either before or
											// after
											valMessages
													.add(new ValidationMessage(
															"endDate",
															"Invalid End Date; new end date conflicts with an existing effective date period.",
															ValidationMessage.Severity.ERROR,
															"contact:"
																	+ newContactType
																			.getContactId()));
											return valMessages;
										}
									} else {
										if (!oldContactType.getStartDate()
												.equals(cType.getStartDate())) {
											if (newContactType
													.getEndDate()
													.after(cType.getStartDate())
													&& newContactType
															.getEndDate()
															.before(cType
																	.getEndDate())
													|| newContactType
															.getEndDate()
															.after(cType
																	.getStartDate())
													&& newContactType
															.getEndDate()
															.equals(cType
																	.getEndDate())
													|| newContactType
															.getStartDate()
															.before(cType
																	.getStartDate())
													&& newContactType
															.getEndDate()
															.after(cType
																	.getEndDate())) {
												// start date is either before
												// or
												// after
												valMessages
														.add(new ValidationMessage(
																"endDate",
																"Invalid End Date; new end date conflicts with an existing effective date period.",
																ValidationMessage.Severity.ERROR,
																"contact:"
																		+ newContactType
																				.getContactId()));
												return valMessages;
											}
										}
									}
								}
							} else {
								// another end date is null
								if (oldContactType.getEndDate() != null) {
									if (newContactType.getEndDate().after(
											cType.getStartDate())) {
										valMessages
												.add(new ValidationMessage(
														"endDate",
														"Invalid End Date; new end date conflicts with an existing effective date period.",
														ValidationMessage.Severity.ERROR,
														"contact:"
																+ newContactType
																		.getContactId()));
										return valMessages;
									}
								}
							}
						}

						// changing the end date to null
						if (newContactType.getEndDate() == null) {
							if (oldContactType.getEndDate() != null) {
								if (cType.getEndDate() != null) {
									if (!oldContactType.getStartDate().equals(
											cType.getStartDate())) {
										if (newContactType.getStartDate()
												.before(cType.getEndDate())) {
											valMessages
													.add(new ValidationMessage(
															"endDate",
															"Invalid Start Date; new start date conflicts with an existing effective date period.",
															ValidationMessage.Severity.ERROR,
															"contact:"
																	+ newContactType
																			.getContactId()));
											return valMessages;
										}
									}
								} else {
									valMessages
											.add(new ValidationMessage(
													"endDate",
													"Invalid End Date; new end date conflicts with an existing effective date period.",
													ValidationMessage.Severity.ERROR,
													"contact:"
															+ newContactType
																	.getContactId()));
									return valMessages;
								}
							} else {
								if (cType.getEndDate() != null) {
									if (!oldContactType.getStartDate().equals(
											cType.getStartDate())) {

										if (newContactType.getStartDate()
												.before(cType.getEndDate())) {
											valMessages
													.add(new ValidationMessage(
															"endDate",
															"Invalid Start Date; new start date conflicts with an existing effective date period.",
															ValidationMessage.Severity.ERROR,
															"contact:"
																	+ newContactType
																			.getContactId()));
											return valMessages;
										}
									}
								}
							}
						}
					} else {
						// new contact type
						if (newContactType.getStartDate().equals(
								cType.getStartDate())) {
							// same start date for new contact type
							valMessages
									.add(new ValidationMessage(
											"startDate",
											"Invalid Start Date; new start date conflicts with an existing effective date period.",
											ValidationMessage.Severity.ERROR,
											"contact:"
													+ newContactType
															.getContactId()));
							return valMessages;
						}

						if (cType.getEndDate() != null) {
							if (newContactType.getStartDate().after(
									cType.getStartDate())) {
								// new start date is after old start date
								if (newContactType.getStartDate().before(
										cType.getEndDate())) {
									valMessages
											.add(new ValidationMessage(
													"startDate",
													"Invalid Start Date; new start date conflicts with an existing effective date period.",
													ValidationMessage.Severity.ERROR,
													"contact:"
															+ newContactType
																	.getContactId()));
									return valMessages;
								}

							}

							if (newContactType.getStartDate().before(
									cType.getStartDate())) {
								// new start date is after old start date
								if (newContactType.getEndDate() != null) {
									if (newContactType.getEndDate().before(
											cType.getStartDate())) {
										valMessages
												.add(new ValidationMessage(
														"startDate",
														"Invalid Start Date; new start date conflicts with an existing effective date period.",
														ValidationMessage.Severity.ERROR,
														"contact:"
																+ newContactType
																		.getContactId()));
										return valMessages;
									}
								} else {
									valMessages
											.add(new ValidationMessage(
													"startDate",
													"Invalid Start Date; new start date conflicts with an existing effective date period.",
													ValidationMessage.Severity.ERROR,
													"contact:"
															+ newContactType
																	.getContactId()));
									return valMessages;
								}

							}
						}
					}

				}

			}
		}

		return valMessages;
	}

	private static Timestamp wholeDay(Timestamp ts) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp rtn = new Timestamp(cal.getTimeInMillis());
		rtn.setNanos(0);
		return rtn;
	}

	public static List<ValidationMessage> validateSubmitContacts(
			Facility facility, List<Contact> allContacts) {
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		boolean hasEnvironmentalContact = false;
		boolean hasResonsibleOffcialContact = false;

		for (Contact contact : allContacts) {
			if (contact.isCurrentContactType(ContactTypeDef.ENVI)) {
				hasEnvironmentalContact = true;
			}
			if (contact.isCurrentContactType(ContactTypeDef.RSOF)) {
				hasResonsibleOffcialContact = true;
			}

		}
		if (facility != null && null != facility.getPermitClassCd()) {
			if (facility.getPermitClassCd().equals(PermitClassDef.TV)) {
				if (!hasResonsibleOffcialContact) {
					valMessages.add(new ValidationMessage(
							"Responsible Official",
							"Current Responsible Official contact is not set.",
							ValidationMessage.Severity.ERROR, "contact"));
				}
			}

			if (!hasEnvironmentalContact) {
				valMessages.add(new ValidationMessage("Environmental",
						"Current Environmental contact is not set.",
						ValidationMessage.Severity.ERROR, "contact"));
			}
		}

		return valMessages;
	}

	/*
	 * Validation for RAPM
	 */
	public static final List<ValidationMessage> validateRAPM(Integer fpId)
			throws ValidationException {
		ValidationControl vc = new ValidationControl();
		vc.setReqAdminPermitMod(true);
		return validateApplication(fpId, null, vc); // null indicates no EUs to
													// evaluate
	}

	/*
	 * TIV Acid Rain Application submission
	 */
	public static final List<ValidationMessage> validateTIVAcidRainApplication(
			Integer fpId, List<EmissionUnit> eus) throws ValidationException {
		ValidationControl vc = new ValidationControl();
		vc.setTivAcidRain(true);
		return validateApplication(fpId, eus, vc);
	}
	
	/*
	 *
	 */
	public static final List<ValidationMessage> validateStackTest(
			Facility facility, List<EmissionUnit> eus) throws ValidationException {
		ValidationControl vc = new ValidationControl();
		vc.setStackTest(true);
		return validateStackTest(facility, eus, vc);
	}
	
	public static final List<ValidationMessage> validateStackTest(
			Facility facility, List<EmissionUnit> eus, ValidationControl vc)
			throws ValidationException {
		return validateStackTest(facility, null, eus, vc);
	}
	
	/*
	 * called by all validations from StackTestBO
	 */
	public static final List<ValidationMessage> validateStackTest(
			Facility facility, Integer fpId, List<EmissionUnit> eus, ValidationControl vc)
			throws ValidationException {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		
		if(facility == null) {
			facility = getFacilityAndCommonValidate(vc, fpId, true,
					messages);
		} else {
			logger.debug("Validating common facility attributes...");
			validateCommonFacility(vc, facility, messages);
			fpId = facility.getFpId();
		}

		if (facility == null) {
			throw new ValidationException(
					"Cannot access Facility with fp ID : " + fpId);
		}

		HashMap<String, Integer> valObject = new HashMap<String, Integer>();

		if (eus != null && !eus.isEmpty()) {
			logger.debug("Validating facility emissions units...");
			for (EmissionUnit eu : eus) {
				EmissionUnit facilityEU = facility.getEmissionUnit(eu
						.getEmuId());
				if (facilityEU != null) {
					vc.setDemEgPntModeling(eu.isDemEgPntModeling());
					vc.setPsdScreenModeling(eu.isPsdOrScreenModeling());
					
					validatePTIandPTIOemissionUnit(vc, facility, facilityEU,
							messages, valObject);

				} else {
					logger.error("Attempt to validate data for emission unit "
							+ eu.getEmuId()
							+ " which was not found in facility with fp_id = "
							+ fpId);
				}
			}
		}

		logger.debug("Adding validation messages...");
		return addNavToValidationMessages(fpId, facility.getFacilityId(),
				messages);
	}
	
	
	public static final List<ValidationMessage> validateCemsComsRataReport(
			Integer fpId) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		
		ValidationControl vc = new ValidationControl();
		vc.setCemsComsRataReport(true);

		Facility facility = null;
		try {
			FacilityService facilityBO = ServiceFactory.getInstance()
					.getFacilityService();
			facility = facilityBO.retrieveFacilityProfile(fpId, true);
			if (facility == null) {
				throw new ValidationException(
						"Cannot access Facility with fp ID : " + fpId);
			} else {
				validateCommonFacility(vc, facility, messages);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return addNavToValidationMessages(fpId, facility.getFacilityId(),
				messages);
	}
	
	public static final List<ValidationMessage> validateFacility(Facility facility, Integer fpId,
			List<EmissionUnit> eus) throws ValidationException {

		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		ValidationControl vc = new ValidationControl();
		vc.setFacilityCheck(true);

		if (facility == null) {
			facility = getFacilityAndCommonValidate(vc, fpId, true, messages);
		} else {
			logger.debug("Validating common facility attributes...");
			validateCommonFacility(vc, facility, messages);
			fpId = facility.getFpId();
		}

		if (facility == null) {
			throw new ValidationException("Cannot access Facility with fp ID: " + fpId + ".");
		} 
		
		//validation for HC analysis section if the facility type has HC Analysis
		if (FacilityTypeDef.hasHCAnalysis(facility.getFacilityTypeCd())){
			validateFacHydrocarbonAnalysisTable(facility, messages);
			//add validation for second and third table if necessary
			validateFacHydrocarbonAnalysisSameplDetail(facility, messages);
			// decane properties
			validateHydrocarbonAnalysisDecaneProperties(facility, messages);
		}
		
		return messages;

	}
	
	
	public static final void validateFacHydrocarbonAnalysisTable(Facility facility, List<ValidationMessage> messages){
		//1. if any cell in a column is field, then all cells in that column must be filled
		//2. each value should between 0 - 100 if the cell is filled
		//3. check Totals -- Warning message if aqdEmissionFactorGroup is null
		
		List<ValidationMessage> msgs = facility.validateHydrocarbonAnalysis();
		messages.addAll(msgs);
	}
	
	public static final void validateFacHydrocarbonAnalysisSameplDetail(Facility facility,
			List<ValidationMessage> messages) {
		if (null != facility && null != facility.getHydrocarbonAnalysisSampleDetail()) {
			messages.addAll(facility.validateHydrocarbonAnalysisSampleDetail());
		}
	}

	private static void validateHydrocarbonAnalysisDecaneProperties(final Facility facility,
			List<ValidationMessage> messages) {
		if (null != facility && null != facility.getDecaneProperties()) {
			messages.addAll(Arrays.asList(facility.getDecaneProperties().validate()));
		}
	}

}
