package us.oh.state.epa.stars2.app.ceta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.def.FceAttachmentTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.def.FCEInspectionReportStateDef;

public class InspectionWorkFlowValidation {
	
	private InspectionWorkFlowValidation() { 
		/*
		 * there is no need for instantiating this class
		 * because all the methods are static
		 */
	}
	
	private static Logger logger = Logger.getLogger(InspectionWorkFlowValidation.class);
	 
	private static final Integer INSPECTION_COMPLETED_ACTIVITY_TEMPLATE_ID = 1300;
	private static final Integer DISTRICT_ENGINEER_REVIEW_ACTIVITY_TEMPLATE_ID = 1301;
	private static final Integer PROGRAM_MANAGER_REVIEW_ACTIVITY_TEMPLATE_ID = 1302;
	private static final Integer ADMINISTRATOR_REVIEW_ACTIVITY_TEMPLATE_ID = 1303;
	private static final Integer FINALIZATION_ACTIVITY_TEMPLATE_ID = 1304;
	
	private static final String CETA_FCE_DETAIL_REF = "ceta.fceDetail";
	
	private static enum WorflowActivity {
		INSPECTION_COMPLETED, 
		DISTRICT_ENGINEER_REVIEW, 
		PROGRAM_MANAGER_REVIEW, 
		ADMINISTRATOR_REVIEW, 
		FINIALIZAION
	}

	private static HashMap<Integer, WorflowActivity> activityTemplateMap 
					= new HashMap<Integer, WorflowActivity>();

	static {
		activityTemplateMap.put(
				INSPECTION_COMPLETED_ACTIVITY_TEMPLATE_ID, WorflowActivity.INSPECTION_COMPLETED);
		
		activityTemplateMap.put(
				DISTRICT_ENGINEER_REVIEW_ACTIVITY_TEMPLATE_ID, WorflowActivity.DISTRICT_ENGINEER_REVIEW);
		
		activityTemplateMap.put(
				PROGRAM_MANAGER_REVIEW_ACTIVITY_TEMPLATE_ID, WorflowActivity.PROGRAM_MANAGER_REVIEW);
		
		activityTemplateMap.put(
				ADMINISTRATOR_REVIEW_ACTIVITY_TEMPLATE_ID, WorflowActivity.ADMINISTRATOR_REVIEW);
		
		activityTemplateMap.put(
				FINALIZATION_ACTIVITY_TEMPLATE_ID, WorflowActivity.FINIALIZAION);
	}
	
	/**
	 * Validates the given workflow activity is ready for check in
	 * @param fce
	 * @param inActivityTemplateId
	 * @return List<ValidationMessage>
	 * @throws none
	 */
	public static List<ValidationMessage> validate(final FullComplianceEval fce, final Integer inActivityTemplateId) {
		
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		
		if (null == activityTemplateMap.get(inActivityTemplateId)) {
			
			String errMsg = "Invalid workflow activity id " + "(" + inActivityTemplateId + "). Please contact the system administrator.";
			
			logger.error("Invalid activity template id: " + inActivityTemplateId);
			
			valMessages.add(new ValidationMessage("inActivityTemplateId", errMsg, ValidationMessage.Severity.ERROR));
			
		} else {
		
			switch (activityTemplateMap.get(inActivityTemplateId)) {
	
				case INSPECTION_COMPLETED:
		
					valMessages = validateInspectionCompletedActivityCheckIn(fce);
		
					break;
		
				case DISTRICT_ENGINEER_REVIEW:
		
					valMessages = validateDistrictEngineerReviewActivityCheckIn(fce);
		
					break;
		
				case PROGRAM_MANAGER_REVIEW:
		
					valMessages = validateProgramManagerReviewActivityCheckIn(fce);
		
					break;
		
				case ADMINISTRATOR_REVIEW:
		
					valMessages = validateAdministratorReviewActivityCheckIn(fce);
		
					break;
		
				case FINIALIZAION:
		
					valMessages = validateFinalizationActivityCheckIn(fce);
		
					break;
		
				default:
		
					break;
			}
		}

		return valMessages;

	}

	/**
	 * Validates the inspection completed workflow activity is ready for check in
	 * 
	 * @param fce
	 * @return List<ValidationMessage>
	 * @throws none
	 */
	private static List<ValidationMessage> validateInspectionCompletedActivityCheckIn(final FullComplianceEval fce) {

		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		
		// inspection should be in the completed state but if it is already in
		// the finalized state, then there is no need to check for completed state
		ValidationMessage valMsg = checkInspectionReportStateIsThis(fce, FCEInspectionReportStateDef.FINAL);
		if (null != valMsg) {
			valMsg = checkInspectionReportStateIsThis(fce, FCEInspectionReportStateDef.COMPLETE);
			if (null != valMsg) {
				valMessages.add(valMsg);
			}
		}
		
		// perform basic validation
		valMessages.addAll(performBasicValidation(fce));
		
		return valMessages;
	}

	/**
	 * Validates the district engineer review workflow activity is ready for
	 * check in
	 * 
	 * @param fce
	 * @return List<ValidationMessage>
	 * @throws none
	 */
	private static List<ValidationMessage> validateDistrictEngineerReviewActivityCheckIn(final FullComplianceEval fce) {

		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		
		valMessages = validateInspectionCompletedActivityCheckIn(fce);

		return valMessages;
	}

	/**
	 * Validates the program manager review workflow activity is ready for check
	 * in
	 * 
	 * @param fce
	 * @return List<ValidationMessage>
	 * @throws none
	 */
	private static List<ValidationMessage> validateProgramManagerReviewActivityCheckIn(final FullComplianceEval fce) {

		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		
		valMessages = validateInspectionCompletedActivityCheckIn(fce);

		return valMessages;
	}

	/**
	 * Validates the administrator review workflow activity is ready for check
	 * in
	 * 
	 * @param fce
	 * @return List<ValidationMessage>
	 * @throws none
	 */
	private static List<ValidationMessage> validateAdministratorReviewActivityCheckIn(final FullComplianceEval fce) {

		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		
		valMessages = validateInspectionCompletedActivityCheckIn(fce);

		return valMessages;
	}

	/**
	 * Validates the finalization workflow activity is ready for check in
	 * 
	 * @param fce
	 * @return List<ValidationMessage>
	 * @throws none
	 */
	private static List<ValidationMessage> validateFinalizationActivityCheckIn(final FullComplianceEval fce) {

		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		
		// inspection should be in the finalized state
		ValidationMessage valMsg = checkInspectionReportStateIsThis(fce, FCEInspectionReportStateDef.FINAL);
		if (null != valMsg) {
			valMessages.add(valMsg);
		}
		
		// perform basic validation
		valMessages.addAll(performBasicValidation(fce));
				
		
		return valMessages;
	}
	
	/** 
	 * Performs basic validations for checking in an inspection report workflow activity
	 * @param fce
	 * @return List<ValidationMessage>
	 * @throws none
	 */
	private static List<ValidationMessage> performBasicValidation(final FullComplianceEval fce) {
		
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		
		ValidationMessage valMsg = null;
		
		// inspection must have at least one attachment of type inspection report
		valMsg = checkThisAttachmentIsPresent(fce, FceAttachmentTypeDef.INSPECTION_REPORT);
		if (null != valMsg) {
			valMessages.add(valMsg);
		}

		// inspection must have valid inspection date
		if (null == fce.getDayOneAmbientConditions()
				|| null == fce.getDayOneAmbientConditions().getInspectionDate()) {
			valMessages.add(new ValidationMessage("dayOneInspectionDate",
					"Inspection does not have a valid inspection date.", ValidationMessage.Severity.ERROR, CETA_FCE_DETAIL_REF));
		}
		
		// inspection must have a valid value for compliance status
		if (Utility.isNullOrEmpty(fce.getComplianceStatusCd())) {
			valMessages.add(new ValidationMessage("complianceStatusCd",
					"Compliance status is not set in the inspection.", ValidationMessage.Severity.ERROR, CETA_FCE_DETAIL_REF));
		}
		
		return valMessages;
	}
	
	/**
	 * Checks whether the inspection is in the given report state
	 * @param fce
	 * @param inspectionReportStateCd
	 * @return ValidationMessage with the error if the inspection is not in the given
	 * report state, otherwise null
	 */
	private static ValidationMessage checkInspectionReportStateIsThis(final FullComplianceEval fce,
			final String inspectionReportStateCd) {
		
		ValidationMessage valMsg = null;
		
		if (!fce.getInspectionReportStateCd().equals(inspectionReportStateCd)) {
			
			String inspState = FCEInspectionReportStateDef.getData().getItems().getItemDesc(inspectionReportStateCd);
			
			valMsg = new ValidationMessage("inspectionReportStateCd", "Inspection state is not " + inspState + ".",
					ValidationMessage.Severity.ERROR, CETA_FCE_DETAIL_REF);
		}
		
		return valMsg;
	}
	
	
	/**
	 * Checks if the inspection has a given attachment type
	 * @param fce
	 * @param attachmentTypeCd
	 * @return  ValidationMessage with the error if the inspection does not have the given
	 * attachment, otherwise null
	 */
	private static ValidationMessage checkThisAttachmentIsPresent(final FullComplianceEval fce,
			final String attachmentTypeCd) {
		
		ValidationMessage valMsg = null;
		boolean attachmentFound = false;
		
		if (null != fce.getAttachments()) {
			for (FceAttachment attachment : fce.getAttachments()) {
				if (attachment.getAttachmentType().equals(attachmentTypeCd)) {
					attachmentFound = true;
					break;
				}
			}
		}
		
		if (!attachmentFound) {
			
			String attachmentDesc = FceAttachmentTypeDef.getData().getItems().getItemDesc(attachmentTypeCd);
			
			valMsg = new ValidationMessage("attachmentType",
					"Inspection has no " + attachmentDesc + ".",
					ValidationMessage.Severity.ERROR, CETA_FCE_DETAIL_REF);
		}
		
		return valMsg;
	}
}
