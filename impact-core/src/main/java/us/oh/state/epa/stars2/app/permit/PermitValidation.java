package us.oh.state.epa.stars2.app.permit;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.GenericIssuanceService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.genericIssuance.GenericIssuance;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitChargePayment;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.def.GenericIssuanceTypeDef;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitActionTypeDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitReceivedCommentsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.def.USEPAOutcomeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;

/*
 * Check business rules for permit prior to issuance.
 */
@SuppressWarnings("serial")
public class PermitValidation implements java.io.Serializable {
	private static Logger logger = Logger.getLogger(PermitValidation.class);

	private static final Integer PBR_REVOCATION_DETERMINATION = 483;
	private static final Integer PBR_REVOCATION = 484;
	private static final Integer PBR_POST_FINAL = 485;
	private static final Integer PBR_RETURN = 482;

	private static final Integer RPR_ISSUE_PROPOSE = 455;
	private static final Integer RPR_ISSUE_FINAL = 457;
	private static final Integer RPR_ISSUE_PROPOSE_FINAL = 456;
	private static final Integer RPR_ISSUE_FINAL_2 = 459;
	private static final Integer RPR_ISSUE_RETURN = 452;

	//private static final Integer RPE_DOLAA_APPROVE = 471;
	//private static final Integer RPE_ISSUE = 473;

	private static List<Integer> rprIssueFinalValidate = new ArrayList<Integer>(
			2);
	private static List<Integer> technicalReviewValidateCheckIn = new ArrayList<Integer>(
			2);
	private static List<Integer> permitApprovalValidateCheckIn = new ArrayList<Integer>(
			2);
	private static List<Integer> issueDraftValidate = new ArrayList<Integer>(2);
	private static List<Integer> referralDateValidate = new ArrayList<Integer>(
			2);
	private static List<Integer> issueFinalValidate = new ArrayList<Integer>(2);
	// private static List<Integer> issuePPValidate = new ArrayList<Integer>(2);
	// private static List<Integer> issuePPPValidate = new
	// ArrayList<Integer>(2);
	private static List<Integer> issueDraftValidateCheckIn = new ArrayList<Integer>(
			2);
	private static List<Integer> issueFinalValidateCheckIn = new ArrayList<Integer>(
			2);
	// private static List<Integer> issuePPValidateCheckIn = new
	// ArrayList<Integer>(
	// 2);
	// private static List<Integer> issuePPPValidateCheckIn = new
	// ArrayList<Integer>(2);

	private static List<Integer> denialPendingValidateCheckIn = new ArrayList<Integer>(
			2);
	private static List<Integer> issueDenialValidateCheckIn = new ArrayList<Integer>(
			2);

	private static Map<Integer, Activity> activityGivenTemplateId = new HashMap<Integer, Activity>();

	public enum Activity {
		RECEIPT_LETTER, COMPLETENESS_REVIEW, TECH_REVIEW_DRAFT_PERMIT_WAIVER, PERMIT_PEER_REVIEW, MANAGER_SUPERVISOR_REVIEW, PREPARE_PUBLIC_NOTICE_PACKAGE, REVIEW_PUBLIC_NOTICE, COMPLETE_PUBLIC_NOTICE, PREPARE_PROPOSED_PERMIT, PREPARE_PROPOSED_PERMIT_PACKAGE, COMPLETE_PROPOSED_PERMIT, LOOP_TECH_REVIEW_DRAFT_PERMIT_WAIVER, AQD_ADMINISTRATOR_FOR_REVIEW, WDEQ_DIRECTOR_FOR_REVIEW, PREPARE_FINAL, ISSUE_FINAL, PREPARE_INACTIVE_WITHDRAWN, SUPERVISOR_REVIEW, ISSUE_INACTIVE_WITHDRAWN
	}

	static {
		// 2268 Validation at DO/LAA technically complete task is not occurring
		technicalReviewValidateCheckIn.add(385); // DO/LAA Technical Review
		permitApprovalValidateCheckIn.add(383); // DO/LAA Permit Approval
		// 2364
		permitApprovalValidateCheckIn.add(382); // CO Permit Approval
		permitApprovalValidateCheckIn.add(386); // CO Permit Review
		permitApprovalValidateCheckIn.add(400); // CO Review Determination

		issueDraftValidateCheckIn.add(384); // Issue Draft
		issueDraftValidate.add(409); // Prepare Deaft

		referralDateValidate.add(388); // Enter referral Date task

		issueFinalValidateCheckIn.add(401); // Issue Final
		issueFinalValidate.add(412); // Prepare Final

		// issuePPValidateCheckIn.add(407); // Issue PP
		// issuePPValidate.add(411); // Prepare PP

		// issuePPPValidateCheckIn.add(408); // Issue PPP
		// issuePPPValidate.add(410); // Prepare PPP

		denialPendingValidateCheckIn.add(390); // DO/LAA Write Denial
		issueDenialValidateCheckIn.add(395); // Issue Denial

		rprIssueFinalValidate.add(RPR_ISSUE_FINAL_2);
		rprIssueFinalValidate.add(RPR_ISSUE_FINAL);

		activityGivenTemplateId.put(1184, Activity.RECEIPT_LETTER);
		activityGivenTemplateId.put(1185, Activity.COMPLETENESS_REVIEW);
		activityGivenTemplateId.put(1186,
				Activity.TECH_REVIEW_DRAFT_PERMIT_WAIVER);
		activityGivenTemplateId.put(1188, Activity.PERMIT_PEER_REVIEW);
		activityGivenTemplateId.put(1191, Activity.MANAGER_SUPERVISOR_REVIEW);
		activityGivenTemplateId.put(1192,
				Activity.PREPARE_PUBLIC_NOTICE_PACKAGE);
		activityGivenTemplateId.put(1195, Activity.REVIEW_PUBLIC_NOTICE);
		activityGivenTemplateId.put(1200, Activity.COMPLETE_PUBLIC_NOTICE);
		activityGivenTemplateId.put(1193, Activity.PREPARE_PROPOSED_PERMIT);
		activityGivenTemplateId.put(1196,
				Activity.PREPARE_PROPOSED_PERMIT_PACKAGE);
		activityGivenTemplateId.put(1202, Activity.COMPLETE_PROPOSED_PERMIT);
		activityGivenTemplateId.put(1201,
				Activity.LOOP_TECH_REVIEW_DRAFT_PERMIT_WAIVER);
		activityGivenTemplateId
				.put(1194, Activity.AQD_ADMINISTRATOR_FOR_REVIEW);
		activityGivenTemplateId.put(1197, Activity.WDEQ_DIRECTOR_FOR_REVIEW);
		activityGivenTemplateId.put(1198, Activity.PREPARE_FINAL);
		activityGivenTemplateId.put(1199, Activity.ISSUE_FINAL);
		activityGivenTemplateId.put(1187, Activity.PREPARE_INACTIVE_WITHDRAWN);
		activityGivenTemplateId.put(1189, Activity.SUPERVISOR_REVIEW);
		activityGivenTemplateId.put(1190, Activity.ISSUE_INACTIVE_WITHDRAWN);

	}

	public static List<ValidationMessage> validate(Permit permit,
			Integer inActivityTemplateId) {

		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
/*
		if (permit.getPermitType() != null
				&& permit.getPermitType().equals(PermitTypeDef.RPR)) {

			if (rprIssueFinalValidate.contains(inActivityTemplateId)) {
				messages = PermitValidation.validateRPRFinal(permit);
			} else if (RPR_ISSUE_PROPOSE.equals(inActivityTemplateId)) {
				messages = PermitValidation.validateRPRProposed(permit);
			}
			// bug 1876
			// workflow id numbers for rprs and make sure that the
			// validateRPRPermit method is called for PROPOSED_FINAL just like
			// it is called for a simple PROPOSED
			else if (RPR_ISSUE_PROPOSE_FINAL.equals(inActivityTemplateId)) {
				messages = PermitValidation.validateRPRPermit(permit);
			} else if (RPR_ISSUE_RETURN.equals(inActivityTemplateId)) {
				messages = PermitValidation.validateRPRReturn(permit);
			}
		} else if (permit.getPermitType() != null
				&& permit.getPermitType().equals(PermitTypeDef.RPE)) {

			if (RPE_DOLAA_APPROVE.equals(inActivityTemplateId)) {
				messages = PermitValidation.validateRPEPermit(permit);
			} else if (RPE_ISSUE.equals(inActivityTemplateId)) {
				messages = PermitValidation
						.validateRPEPermitIssuanceCheckIn(permit);
			}
		} else {
			*/
		/*

			if (technicalReviewValidateCheckIn.contains(inActivityTemplateId)) {
				messages = PermitValidation.validateTechnicalReview(permit);
				// Mantis 2978 - add permit approval checks to this step
				messages = PermitValidation.validatePermitApproval(permit);
			} else if (permitApprovalValidateCheckIn
					.contains(inActivityTemplateId)) {
				messages = PermitValidation.validatePermitApproval(permit);
			} else if (issueDraftValidate.contains(inActivityTemplateId)) {
				messages = PermitValidation.validateDraftIssuance(permit);
			} else if (issueDraftValidateCheckIn.contains(inActivityTemplateId)) {
				messages = PermitValidation
						.validateDraftIssuanceCheckIn(permit);
			} else if (referralDateValidate.contains(inActivityTemplateId)) {
				messages = PermitValidation.validateRefferralDate(permit);
			} else if (issueFinalValidate.contains(inActivityTemplateId)) {
				messages = PermitValidation.validateFinalIssuanceStars2(permit);
			} else if (issueFinalValidateCheckIn.contains(inActivityTemplateId)) {
				messages = PermitValidation
						.validateFinalIssuanceCheckIn(permit);
				// } else if (issuePPValidate.contains(inActivityTemplateId)) {
				// messages = PermitValidation.validatePPIssuance(permit);
				// } else if
				// (issuePPValidateCheckIn.contains(inActivityTemplateId)) {
				// messages =
				// PermitValidation.validatePPIssuanceCheckIn(permit);
			}
			// else if (issuePPPValidate.contains(inActivityTemplateId)) {
			// messages = PermitValidation.validatePPPIssuance(permit);
			// }
			// else if (issuePPPValidateCheckIn.contains(inActivityTemplateId))
			// {
			// messages = PermitValidation.validatePPPIssuanceCheckIn(permit);
			//
			else if (denialPendingValidateCheckIn
					.contains(inActivityTemplateId)) {
				messages = PermitValidation.validateDenialPending(permit);
			} else if (issueDenialValidateCheckIn
					.contains(inActivityTemplateId)) {
				messages = PermitValidation.validateIssueDenial(permit);
			}
			
			*/

			switch (activityGivenTemplateId.get(inActivityTemplateId)) {
			case RECEIPT_LETTER:
				messages = PermitValidation.validateReceiptLetter(permit);
				break;
			case COMPLETENESS_REVIEW:
				messages = PermitValidation.validateCompletenessReview(permit);
				break;
			case TECH_REVIEW_DRAFT_PERMIT_WAIVER:
				messages = PermitValidation.validateTechReviews(permit);
				break;
			case PERMIT_PEER_REVIEW:
				messages = PermitValidation.validatePermitPeerReview(permit);
				break;
			case MANAGER_SUPERVISOR_REVIEW:
				messages = PermitValidation
						.validateManagerSupervisorReview(permit);
				break;
			case PREPARE_PUBLIC_NOTICE_PACKAGE:
				messages = PermitValidation
						.validatePreparePublicNoticePackage(permit);
				break;
			case REVIEW_PUBLIC_NOTICE:
				messages = PermitValidation.validateReviewPublicNotice(permit);
				break;
			case COMPLETE_PUBLIC_NOTICE:
				messages = PermitValidation.validateCompletePublicNotice(permit);
				break;
			case PREPARE_PROPOSED_PERMIT:
				messages = PermitValidation
						.validatePrepareProposedPermit(permit);
				break;
			case PREPARE_PROPOSED_PERMIT_PACKAGE:
				messages = PermitValidation
						.validatePrepareProposedPermitPackage(permit);
				break;
			case COMPLETE_PROPOSED_PERMIT:
				messages = PermitValidation.validateCompleteProposedPermit(permit);
				break;
			case LOOP_TECH_REVIEW_DRAFT_PERMIT_WAIVER:
				messages = PermitValidation
						.validateLoopTechReviewDraftPermitWaiver(permit);
				break;
			case AQD_ADMINISTRATOR_FOR_REVIEW:
				messages = PermitValidation
						.validateAQDAdministratorForReview(permit);
				break;
			case WDEQ_DIRECTOR_FOR_REVIEW:
				messages = PermitValidation
						.validateWDEQDirectorForReview(permit);
				break;
			case PREPARE_FINAL:
				messages = PermitValidation.validatePrepareFinal(permit);
				break;
			case ISSUE_FINAL:
				messages = PermitValidation.validateIssueFinal(permit);
				break;
			case PREPARE_INACTIVE_WITHDRAWN:
				messages = PermitValidation
						.validatePrepareInactiveWithdrawn(permit);
				break;
			case SUPERVISOR_REVIEW:
				messages = PermitValidation.validateSupervisorReview(permit);
				break;
			case ISSUE_INACTIVE_WITHDRAWN:
				messages = PermitValidation
						.validateIssueInactiveWithdrawn(permit);
				break;
			}
		//}
		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;
	}

	private static List<ValidationMessage> validateTechnicalReview(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		// See if the permit has been denied or a denial is pending.
		if (permit.getPermitGlobalStatusCD() != null
				&& !(permit.getPermitGlobalStatusCD().equals(
						PermitGlobalStatusDef.ISSUED_DENIAL) || permit
						.getPermitGlobalStatusCD().equals(
								PermitGlobalStatusDef.DENIAL_PENDING))) {
			messages.addAll(validateBasicPermit(permit));
			if (messages.size() != 0) {
				ValidationMessage vm = new ValidationMessage(
						"Permit",
						"To avoid validations, Click on 'Deny Permit' in permit detail before complete this task.  ",
						ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(1, vm);
			}
		}

		return messages;
	}
	
	/*

	private static List<ValidationMessage> validatePermitApproval(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		if (!PermitReasonsDef.INITIAL.equals(permit.getPrimaryReasonCD())
				&& !PermitReasonsDef.INITIAL_INSTALLATION.equals(permit
						.getPrimaryReasonCD())) {
			StringBuilder nonSupercededEuList = new StringBuilder();
			if (PermitTypeDef.NSR.equals(permit.getPermitType())
					|| PermitTypeDef.TVPTI.equals(permit.getPermitType())) {
				for (PermitEU eu : permit.getEus()) {
					if (eu.getSupersededPermitID() == null) {
						nonSupercededEuList.append(eu.getFpEU().getEpaEmuId()
								+ ", ");
					}
				}
				if (nonSupercededEuList.length() > 2) {
					messages.add(new ValidationMessage("Permit",
							"The following Permit EUs must designate a superseded permit: "
									+ nonSupercededEuList.substring(0,
											nonSupercededEuList.length() - 2),
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef()));
				}
			} else if (PermitTypeDef.TV_PTO.equals(permit.getPermitType())) {
				if (((TVPermit) permit).getSupersededPermitID() == null) {
					messages.add(new ValidationMessage("Permit",
							"A superseded permit must be designated.",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef()));
				}
			}
		}

		return messages;
	}
	*/

	public PermitValidation() {
		super();
	}

	/*
	 * Corresponds to the DO/LAA signoff workflow step.
	 */
	public static List<ValidationMessage> validateBasicPermit(Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		
		// If any the the applications for this permit are identified as Known Incomplete, we should not be here.
		messages = validateKnownIncompleteNSRApp(permit);
		if (messages.size() > 0) {
			return messages;
		}

		// Simple first step is to see what permit thinks of itself.
		ValidationMessage[] vms = permit.validate();
		for (ValidationMessage vm : vms) {
			messages.add(vm);
		}

		// Make sure the permit object class matches the type code.
		PTIOPermit ptioPermit = null;
		TVPermit tvPermit = null;
		String desc = null;
		// if (permit.getPermitType().equals(PermitTypeDef.TVPTI)
		// || permit.getPermitType().equals(PermitTypeDef.NSR)) {
		if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
			if (!(permit instanceof PTIOPermit)) {
				messages.add(new ValidationMessage("Permit",
						"Permit should be a PTIOPermit object.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			} else {
				ptioPermit = (PTIOPermit) permit;
				desc = "ptioDesc";
			}
		} else if (permit.getPermitType().equals(PermitTypeDef.TV_PTO)) {
			if (!(permit instanceof TVPermit)) {
				messages.add(new ValidationMessage("Permit",
						"Permit should be a TVPermit object.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			} else {
				tvPermit = (TVPermit) permit;
				desc = "tvDesc";
			}
		}
		/*
		 * TIV_PTO doesn't apply to IMPACT else if
		 * (permit.getPermitType().equals(PermitTypeDef.TIV_PTO)) { if (!(permit
		 * instanceof TIVPermit)) { messages.add(new ValidationMessage("Permit",
		 * "Permit should be a TIVPermit object.",
		 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); } else {
		 * tvPermit = (TVPermit) permit; desc = "tivDesc"; } }
		 */

		// if (desc != null && permit.getDescription() == null) {
		if (desc != null && Utility.isNullOrEmpty(permit.getDescription())) {
			messages.add(new ValidationMessage(desc,
					"Permit has no description.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
		}

		// See if the permit has been made inactive/withdrawn for is
		// inactive/withdrawal pending.
		if (permit.getPermitGlobalStatusCD() != null
				&& permit.getPermitGlobalStatusCD().equals(
						PermitGlobalStatusDef.ISSUED_DENIAL)) {
			messages.add(new ValidationMessage("Permit",
					"Permit status is withdrawal issued.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
		}
		if (permit.getPermitGlobalStatusCD() != null
				&& permit.getPermitGlobalStatusCD().equals(
						PermitGlobalStatusDef.DENIAL_PENDING)) {
			messages.add(new ValidationMessage("Permit",
					"Permit status is withdrawal pending.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
		}

		// Make sure the facility is not permanently shutdown.
		Facility facility = null;
		String perDueDateCode = null;

		if (permit.getFacilityId() == null) {
			messages.add(new ValidationMessage("facilityId",
					"Permit has no facility ID.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
		} else {
			try {
				FacilityService facilityBO = ServiceFactory.getInstance()
						.getFacilityService();
				facility = facilityBO.retrieveFacility(permit.getFacilityId());
				if (facility == null) {
					messages.add(new ValidationMessage("facilityId",
							"Cannot locate facility with ID = "
									+ permit.getFacilityId() + ".",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				} else if (facility.getOperatingStatusCd().equals(
						OperatingStatusDef.SD)) {
					messages.add(new ValidationMessage("facilityId",
							"Facility operating status is shutdown.",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				}
				/*
				 * Doesn't apply to IMPACT else if (facility.getPermitClassCd()
				 * != null &&
				 * facility.getPermitClassCd().equals(PermitClassDef.TV) &&
				 * ptioPermit != null &&
				 * ptioPermit.getPermitType().equals(PermitTypeDef.NSR)) {
				 * 
				 * messages.add(new ValidationMessage("facilityId",
				 * "All NSR/TV permits for facility " + permit.getFacilityId() +
				 * " must be issued at the same time.",
				 * ValidationMessage.Severity.WARNING,
				 * buildPermitProfileRef())); }
				 */
				if (facility != null) {
					// perDueDateCode = facility.getPERDueDateCd();
				}

			} catch (Exception e) {
				logger.error("Cannot retrieve facility: " + e.getMessage(), e);
				messages.add(new ValidationMessage("Facility",
						"Unable to retrieve facility. " + e.getMessage(),
						ValidationMessage.Severity.ERROR));
			}
		}

		// Check the PermitEUGroups and PermitEUs. Store the EUs away for a
		// later comparison.
		HashMap<Integer, PermitEU> permitEUs = new HashMap<Integer, PermitEU>();
		// list of all permit EU corr ids
		ArrayList<Integer> permitEUCorrIds = new ArrayList<Integer>();
		if (permit.getEuGroups().isEmpty()) {
			// If we have no EUGroups.
			// display info message if permit type is Title V and permit reason is
			// reopening or minor mod or administrative ammendment or 502(b)(10) change
			// else display error
			if(permit.getPermitType().equals(PermitTypeDef.TV_PTO)
					&& (permit.getPermitReasonCDs().contains(PermitReasonsDef.REOPENING)
							|| permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
							|| permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)
							|| permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10))) {
				messages.add(new ValidationMessage("Emission Units",
						"Permit has no emission units.",
						ValidationMessage.Severity.INFO, buildPermitProfileRef(),permit.getPermitID()));
			} else {
			messages.add(new ValidationMessage("Emission Units",
					"Permit has no emission units.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
			}
		} else {
			int i = 0;
			for (PermitEUGroup peug : permit.getEuGroups()) {
				// Is each EUGroup valid?
				vms = peug.validate();
				for (ValidationMessage vm : vms) {
					messages.add(vm);
				}
				for (PermitEU peu : peug.getPermitEUs()) {
					// Is each EU valid?
					i++;
					vms = peu.validate();
					for (ValidationMessage vm : vms) {
						messages.add(vm);
					}

					permitEUs.put(peu.getCorrEpaEMUID(), peu);
					permitEUCorrIds.add(peu.getCorrEpaEMUID());
					// Make sure that the EU is not permanently shutdown.
					if (facility != null) {
						if (peu.getFacilityEUID() == null) {
							messages.add(new ValidationMessage(
									"Emission Units", "Permit emission unit "
											+ peu.getPermitEUID() + " has no "
											+ "facility emission unit ID.",
									ValidationMessage.Severity.ERROR,
									buildPermitProfileRef(),permit.getPermitID()));
						} else {
							EmissionUnit permitFeu = peu.getFpEU();
							EmissionUnit fEU = facility
									.getMatchingEmissionUnit(peu
											.getCorrEpaEMUID());
							if (fEU == null) {
								messages.add(new ValidationMessage(
										"Emission Units",
										"Cannot locate facility "
												+ "emission unit (corrID) "
												+ peu.getCorrEpaEMUID() + ".",
										ValidationMessage.Severity.ERROR,
										buildEURef(Integer.toString(peu
												.getPermitEUID())),permit.getPermitID()));
							} else if (!permitFeu.getEpaEmuId().equals(
									fEU.getEpaEmuId())) {
								messages.add(new ValidationMessage(
										"Emission Units",
										"Permit emission unit "
												+ permitFeu.getEpaEmuId()
												+ " may need to be synched with more recent "
												+ "facility emission unit "
												+ fEU.getEpaEmuId() + ".",
										ValidationMessage.Severity.ERROR,
										buildEURef(Integer.toString(peu
												.getPermitEUID())), fEU
												.getEpaEmuId(),permit.getPermitID()));
							} else if (fEU.getOperatingStatusCd().equals(
									OperatingStatusDef.SD)) {
								messages.add(new ValidationMessage(
										"Emission Units",
										"Facility emission unit "
												+ "operating status is shutdown for permit "
												+ "emission unit "
												+ fEU.getEpaEmuId() + ".",
										ValidationMessage.Severity.ERROR,
										buildEURef(Integer.toString(peu
												.getPermitEUID())), fEU
												.getEpaEmuId(),permit.getPermitID()));
							}
							/*
							 * TV EU Classification is not used by IMPACT else
							 * if (tvPermit != null && fEU.getTvClassCd() ==
							 * null) { messages.add(new
							 * ValidationMessage("Emission Units",
							 * "Facility emission unit " +
							 * "is missing a TV EU Classification " +
							 * "for permit emission unit " + fEU.getEpaEmuId() +
							 * ".", ValidationMessage.Severity.ERROR,
							 * "emissionUnit:" + fEU.getEpaEmuId(),
							 * fEU.getEpaEmuId())); }
							 */

							/*
							 * General Permit not valid for WY if
							 * (permit.isGeneralPermit() &&
							 * (peu.getGeneralPermitTypeCd() == null ||
							 * peu.getModelGeneralPermitCd() == null)) {
							 * messages.add(new
							 * ValidationMessage("Emission Units",
							 * "Permit emission unit " + fEU.getEpaEmuId() +
							 * " is missing " +
							 * "a general permit type or category.",
							 * ValidationMessage.Severity.ERROR,
							 * buildPermitProfileRef(), fEU.getEpaEmuId())); }
							 * 
							 * if (!permit.isGeneralPermit() &&
							 * (peu.getGeneralPermitTypeCd() != null ||
							 * peu.getModelGeneralPermitCd() != null)) {
							 * messages.add(new ValidationMessage("Permit",
							 * "Permit emission unit " + fEU.getEpaEmuId() +
							 * " has a " + "general permit type or category " +
							 * "but the permit is not marked as a " +
							 * "General Permit.",
							 * ValidationMessage.Severity.ERROR,
							 * buildPermitProfileRef(), fEU.getEpaEmuId())); }
							 */

							/*
							 * No fees per EU and no temporary EUs for IMPACT if
							 * ((ptioPermit != null) &&
							 * (!ptioPermit.getPermitReasonCDs
							 * ().contains(PermitReasonsDef.RENEWAL)) &&
							 * (peu.getEuFee() == null ||
							 * peu.getEuFee().getFeeCategoryId() == null ||
							 * peu.getEuFee().getFee() == null)) {
							 * messages.add(new
							 * ValidationMessage("Emission Units",
							 * "Permit emission unit " + fEU.getEpaEmuId() +
							 * " is missing " + "a fee.",
							 * ValidationMessage.Severity.WARNING,
							 * buildEURef(Integer
							 * .toString(peu.getPermitEUID())),
							 * fEU.getEpaEmuId())); }
							 * 
							 * // Check for Z and TMP EUs. if ((ptioPermit !=
							 * null || (tvPermit != null &&
							 * !peug.isInsignificantEuGroup())) &&
							 * (fEU.getEpaEmuId() == null ||
							 * fEU.getEpaEmuId().startsWith("Z") ||
							 * fEU.getEpaEmuId().startsWith("TMP"))) {
							 * messages.add(new
							 * ValidationMessage("Emission Units",
							 * "Facility emission unit " + fEU.getEpaEmuId() +
							 * " has a temporary ID.",
							 * ValidationMessage.Severity.ERROR,
							 * buildEURef(Integer.toString(peu.getPermitEUID()))
							 * , fEU.getEpaEmuId())); }
							 */
						}
					} else {
						messages.add(new ValidationMessage(
								"Emission Units",
								"Cannot locate facility " + "emission unit "
										+ peu.getFacilityEUID() + ".",
								ValidationMessage.Severity.ERROR,
								buildEURef(Integer.toString(peu.getPermitEUID())),
								peu.getFacilityEUID() + "", permit.getPermitID()));
					}
				}
			}
			if (!permit.getEuGroups().isEmpty() && i == 0) {
				// If none of our EUGroups have EUs.
				// display info message if permit type is Title V and permit reason is
				// reopening or minor mod or administrative ammendment or 502(b)(10) change
				// else display error
				if(permit.getPermitType().equals(PermitTypeDef.TV_PTO)
						&& (permit.getPermitReasonCDs().contains(PermitReasonsDef.REOPENING)
								|| permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
								|| permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)
								|| permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10))) {
					messages.add(new ValidationMessage("Emission Units",
							"Permit has no emission units.",
							ValidationMessage.Severity.INFO, buildPermitProfileRef(),permit.getPermitID()));
				} else {
				messages.add(new ValidationMessage("Emission Units",
						"Permit has no emission units.",
						ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
				}
			}
		}

		// Revised this secontion for IMPACT ... see below
		// Check the permit's documents.
		// boolean hasTermsAndCond = false;
		// boolean hasStatementOfBasis = false;
		// boolean hasSynthMinor = false;
		// boolean hasResponseToComments = false;
		/*
		 * if (permit.getDocuments().isEmpty()) { messages.add(new
		 * ValidationMessage("Documents", "Permit has no documents.",
		 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); } else {
		 * for (PermitDocument doc : permit.getDocuments()) { vms =
		 * doc.validate(); for (ValidationMessage vm : vms) { messages.add(vm);
		 * }
		 * 
		 * if
		 * (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.TERMS_CONDITIONS))
		 * { hasTermsAndCond = true; } else if
		 * (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.STATEMENT_BASIS)) {
		 * hasStatementOfBasis = true; } else if
		 * (doc.getPermitDocTypeCD().equals
		 * (PermitDocTypeDef.PERMIT_STRATEGY_SUMMARY_WRITE_UP)) { hasSynthMinor
		 * = true; } else if
		 * (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.RESPONSE_TO_COMMENTS
		 * )) { hasResponseToComments = true; } } }
		 */
		// Check the permit's documents.

		// boolean hasStatementOfBasis = false;
//		boolean hasResponseToComments = false;
		boolean hasPublicNotice = false;
		for (PermitDocument doc : permit.getDocuments()) {
			vms = doc.validate();
			for (ValidationMessage vm : vms) {
				messages.add(vm);
			}
			/*
			 * if (doc.getPermitDocTypeCD().equals(
			 * PermitDocTypeDef.STATEMENT_BASIS)) { hasStatementOfBasis = true;
			 * } else
			 */
			/*if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.RESPONSE_TO_COMMENTS)) {
				hasResponseToComments = true;
			} else*/ 
			if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.NSR_PUBLIC_NOTICE_DOCUMENT) || 
					doc.getPermitDocTypeCD().equals(PermitDocTypeDef.TV_PUBLIC_NOTICE_DOCUMENT)) {
				hasPublicNotice = true;
			}
		}

		/*
		 * if (!hasTermsAndCond) { messages.add(new ValidationMessage("TCTab",
		 * "Permit has no Terms and Conditions document.",
		 * ValidationMessage.Severity.ERROR, buildTCRef())); }
		 */

		if (ptioPermit != null) {

			/*
			 * if ((ptioPermit.getPermitType().equals(PermitTypeDef.TVPTI) ||
			 * (ptioPermit.getPermitType().equals(PermitTypeDef.NSR) &&
			 * ptioPermit
			 * .getPermitReasonCDs().contains(PermitReasonsDef.INITIAL_INSTALLATION
			 * ))) && (ptioPermit.isNetting() || ptioPermit.isFeptio() ||
			 * ptioPermit.isSmtv()) && (!hasSynthMinor)) { messages.add(new
			 * ValidationMessage("PermitStrategy",
			 * "Permit has no Permit Strategy document.",
			 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); }
			 * 
			 * // Check FEPTIO. if (ptioPermit.getPermitType() != null &&
			 * ptioPermit.getPermitType().equals(PermitTypeDef.NSR) &&
			 * !ptioPermit.isFeptio() && permit.isIssueDraft() &&
			 * permit.getDraftIssueDate() == null) { messages.add(new
			 * ValidationMessage("Permit",
			 * "Permit is marked to be issued draft, but FEPTIO flag is not checked."
			 * , ValidationMessage.Severity.WARNING, buildPermitProfileRef()));
			 * }
			 */

			// check not reauired after implementing NSR Billing feature
			// Check the permit's fees (PTI/PTIO Non-Renewel only).
//			if ((!ptioPermit.getPermitReasonCDs().contains(
//					PermitReasonsDef.RENEWAL))
//					&& (ptioPermit.getTotalAmount() <= 0.0)
//					&& permit.getPermitGlobalStatusCD().equals(
//							PermitGlobalStatusDef.ISSUED_DRAFT)) {
//				messages.add(new ValidationMessage("PermitFee",
//						"Permit fee is less than or equal to zero.",
//						ValidationMessage.Severity.WARNING,
//						buildFeeSummaryRef(),permit.getPermitID()));
//			}
			/*
			 * if
			 * ((ptioPermit.getPermitReasonCDs().contains(PermitReasonsDef.RENEWAL
			 * )) && (ptioPermit.getTotalAmount() > 0.0)) { messages.add(new
			 * ValidationMessage("PermitFee",
			 * "Permit fees are not allowed for a permit renewal.",
			 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); }
			 */

		}

		if ((ptioPermit != null || tvPermit != null)
				&& permit.getPermitReasonCDs().contains(
						PermitReasonsDef.NOT_ASSIGNED)) {
			messages.add(new ValidationMessage("Permit",
					"Permit has no assigned reason.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
		}

		/*
		 * if (tvPermit != null &&
		 * (permit.getPermitReasonCDs().contains(PermitReasonsDef.APA) ||
		 * permit.
		 * getPermitReasonCDs().contains(PermitReasonsDef.CHAPTER_31_MOD))) {
		 * messages.add(new ValidationMessage("Permit",
		 * "FIX ME: Need permit reason codes for cases where a permit mod " +
		 * "requires us to know the id/number of the permit being modded.",
		 * ValidationMessage.Severity.WARNING, buildPermitProfileRef())); }
		 */

		/*
		 * if (!hasStatementOfBasis &&
		 * permit.getPermitType().equals(PermitTypeDef.TV_PTO)) { boolean
		 * needsSOB = false; for (String reason : permit.getPermitReasonCDs()) {
		 * if (!reason.equals(PermitReasonsDef.APA) &&
		 * !reason.equals(PermitReasonsDef.OFF_PERMIT_CHANGE)) { needsSOB =
		 * true; break; } } if (needsSOB) { messages.add(new
		 * ValidationMessage("Documents",
		 * "Permit has no Statement of Basis document.",
		 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); } }
		 */

		/*if (permit.getPermitGlobalStatusCD().equals(
				PermitGlobalStatusDef.ISSUED_DRAFT)
				&& !hasResponseToComments) {
			messages.add(new ValidationMessage("Documents",
					"Permit has no Response To Comments document.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef()));
		}*/

		if ((permit.getPermitGlobalStatusCD()
				.equals(PermitGlobalStatusDef.NONE))) {
			messages.addAll(validateTechReview(permit));
		} else if ((permit.getPermitGlobalStatusCD().equals(PermitGlobalStatusDef.ISSUED_DRAFT)) || (permit.getPermitGlobalStatusCD().equals(PermitGlobalStatusDef.ISSUED_PP))
																										|| (permit.getPermitGlobalStatusCD().equals(PermitGlobalStatusDef.ISSUED_FINAL))){
			messages.addAll(validateTechReview2(permit));
		}

		boolean needsPublicNotice = false;
		if (!hasPublicNotice) {
			if (permit.getPermitType().equals(PermitTypeDef.NSR)
					&& (((PTIOPermit) permit).getPermitActionType() != null && ((PTIOPermit) permit)
							.isActionTypePermit()) && permit.isIssueDraft()) {
				needsPublicNotice = true;
			}
			else if(permit.getPermitType().equals(PermitTypeDef.TV_PTO) 
					    && !permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
					    && !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
					    && !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)
						&& (permit.getPermitGlobalStatusCD() != null 
							&& (permit.getPermitGlobalStatusCD().equalsIgnoreCase(PermitGlobalStatusDef.ISSUED_DRAFT)
								|| permit.getPermitGlobalStatusCD().equalsIgnoreCase(PermitGlobalStatusDef.ISSUED_PP)
								|| permit.getPermitGlobalStatusCD().equalsIgnoreCase(PermitGlobalStatusDef.ISSUED_FINAL)))) {
					needsPublicNotice = true;
			}	
			if (needsPublicNotice) {
				messages.add(new ValidationMessage("Documents",
						"Permit has no Public Notice document.",
						ValidationMessage.Severity.ERROR,
						buildDraftRef(),permit.getPermitID()));
			}
		}

		/*
		 * Does not apply to IMPACT // Check for overlapping active permits. try
		 * { PermitService permitBO =
		 * ServiceFactory.getInstance().getPermitService(); List<Permit>
		 * allPermitsForFacility = permitBO.search(null, null, null, null, null,
		 * null, permit.getFacilityId(), null,
		 * PermitGlobalStatusDef.ISSUED_FINAL, null, null, null, null, true);
		 * 
		 * for (Permit aPermit : allPermitsForFacility) {
		 * 
		 * // We are a PTO and other is either a PTO or a PTIO, // or we are a
		 * PTIO and other is a PTO. if (!permit.equals(aPermit) &&
		 * ((permit.getPermitType().equals(PermitTypeDef.TV_PTO) &&
		 * aPermit.getPermitType().equals(PermitTypeDef.NSR)) ||
		 * (permit.getPermitType().equals(PermitTypeDef.NSR) &&
		 * aPermit.getPermitType().equals(PermitTypeDef.TV_PTO)))) {
		 * 
		 * boolean isActive = false; aPermit =
		 * permitBO.retrievePermit(aPermit.getPermitNumber()); for
		 * (PermitEUGroup peug : aPermit.getEuGroups()) { for (PermitEU peu :
		 * peug.getPermitEUs()) { if
		 * (peu.getPermitStatusCd().equals(PermitStatusDef.ACTIVE)) { isActive =
		 * true; break; } } if (isActive) { break; } }
		 * 
		 * if (isActive) {
		 * 
		 * if (permit.getPermitType().equals(PermitTypeDef.TV_PTO) &&
		 * aPermit.getPermitType().equals(PermitTypeDef.NSR)) { messages.add(new
		 * ValidationMessage("Permit", "This facility will will now be" +
		 * " operating under a Title V permit." + " Permit " +
		 * aPermit.getPermitNumber() + " is an active PTIO and must be turned" +
		 * " into a PTI upon final issuance of this permit.",
		 * ValidationMessage.Severity.WARNING, buildPermitProfileRef())); } else
		 * if (permit.getPermitType().equals(PermitTypeDef.NSR) &&
		 * aPermit.getPermitType().equals(PermitTypeDef.TV_PTO)) {
		 * messages.add(new ValidationMessage("Permit", "Permit " +
		 * aPermit.getPermitNumber() + " is an active Title V permit and" +
		 * " must be revoked or allowed to expire" +
		 * " prior to issuing any PTIOs to this facility.",
		 * ValidationMessage.Severity.WARNING, buildPermitProfileRef())); } } }
		 * } } catch (Exception e) {
		 * logger.error("Cannot retrieve all permits for facility: " +
		 * e.getMessage(), e); messages.add(new ValidationMessage("FacilityId",
		 * "Unable to retrieve all permits for facility with ID = " +
		 * permit.getFacilityId() + ". " + e.getMessage(),
		 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); }
		 */

		// Check applications.
		try {
			if (facility != null) {

				Timestamp mostRecentAppDate = null;
				Timestamp oldestAssociatedAppDate = null;

				HashMap<Integer, EmissionUnit> removedEUs = new HashMap<Integer, EmissionUnit>();
				List<ValidationMessage> fpMessages = new ArrayList<ValidationMessage>();

				ApplicationService applicationBO = ServiceFactory.getInstance()
						.getApplicationService();
				for (Application permitApp : permit.getApplications()) {

					permitApp = applicationBO.retrieveApplication(permitApp
							.getApplicationID());

					if (permitApp.getSubmittedDate() == null) {
						continue;
					}

					if (mostRecentAppDate == null) {
						mostRecentAppDate = permitApp.getReceivedDate();
						if (mostRecentAppDate == null) {
							messages.add(new ValidationMessage("Application",
									"Submitted application has no received date "
											+ "for application number "
											+ permitApp.getApplicationNumber()
											+ ".",
									ValidationMessage.Severity.WARNING,
									buildPermitProfileRef(),permit.getPermitID()));
							continue;
						}
					} else if (mostRecentAppDate.compareTo(permitApp
							.getReceivedDate()) < 0) {
						mostRecentAppDate = permitApp.getReceivedDate();
					}
					if (oldestAssociatedAppDate == null) {
						oldestAssociatedAppDate = permitApp.getReceivedDate();
					} else if (oldestAssociatedAppDate.compareTo(permitApp
							.getReceivedDate()) > 0) {
						oldestAssociatedAppDate = permitApp.getReceivedDate();
					}

					List<ApplicationEU> appEUList = permitApp.getIncludedEus();

					for (ApplicationEU appEU : appEUList) {

						EmissionUnit oldEu = appEU.getFpEU();
						EmissionUnit fpEU = facility
								.getMatchingEmissionUnit(oldEu
										.getCorrEpaEmuId());

						if (fpEU != null
								&& !fpEU.getOperatingStatusCd().equals(
										OperatingStatusDef.SD)) {
							if (tvPermit != null
									//&& !tvPermit.getPermitType()
									//		.equalsIgnoreCase(
									//				PermitTypeDef.TIV_PTO)
																			) { // Already
																				// checked
																				// this
																				// condition
																				// (missing
																				// TvClassCode)
																				// above.
								if (fpEU.getTvClassCd() == null) {
									permitEUs.remove(fpEU.getCorrEpaEmuId());
									removedEUs
											.put(fpEU.getCorrEpaEmuId(), fpEU);
								} // Make sure, for Title V Permit, that all
									// insignificant and non-insignificant EUs
									// in the Application(s) are in the permit.
								if (fpEU.getTvClassCd() != null
										&& (fpEU.getTvClassCd()
												.equals(TVClassification.NON_INSIGNIFICANT) || fpEU
												.getTvClassCd().equals(
														TVClassification.INSIG))) { // Don't
																					// double
																					// count!
									if (!removedEUs.containsKey(fpEU
											.getCorrEpaEmuId())
											&& permitEUs.remove(fpEU
													.getCorrEpaEmuId()) == null) {
										fpMessages
												.add(new ValidationMessage(
														"Emission Units",
														"Application emission unit "
																+ "(Insignificant "
																+ "or Non-insignificant) is missing "
																+ "from permit. "
																+ "Application emission unit ID is "
																+ oldEu.getEpaEmuId()
																+ ". Current facility EU ID is "
																+ fpEU.getEpaEmuId()
																+ ".",
														ValidationMessage.Severity.WARNING,
														"emissionUnit:"
																+ fpEU.getEpaEmuId(),
														fpEU.getEpaEmuId()));
									}
									// this seems redundant, but if there are
									// two applications with the same EU, this
									// is needed to ensure the EU ends up in the
									// removedEUs map

									removedEUs
											.put(fpEU.getCorrEpaEmuId(), fpEU);
								} else {
									permitEUs.remove(fpEU.getCorrEpaEmuId());
									removedEUs
											.put(fpEU.getCorrEpaEmuId(), fpEU);
									// fpMessages
									// .add(new ValidationMessage(
									// "Emission Units",
									// "Application contains an emission unit "
									// +
									// "which does not have a TV class code of "
									// + "either insignificant or "
									// + "non-insignificant. "
									// + "Application emission unit ID is "
									// + oldEu.getEpaEmuId()
									// + ". Current facility EU ID is "
									// + fpEU.getEpaEmuId()
									// + ".",
									// ValidationMessage.Severity.WARNING,
									// "emissionUnit:"
									// + fpEU.getEpaEmuId(),
									// fpEU.getEpaEmuId()));
								}
							} // END: if (tvPermit != null)
							else {
								permitEUs.remove(fpEU.getCorrEpaEmuId());
								removedEUs.put(fpEU.getCorrEpaEmuId(), fpEU);
							}

						} // END: if (fpEU != null)

					} // END: for (ApplicationEU appEU : appEUList)

				} // END: for (Application permitApp : permit.getApplications())

				messages.addAll(FacilityValidation.addNavToValidationMessages(
						facility.getFpId(), facility.getFacilityId(),
						fpMessages));

				// Should be empty if all permit EUs are found in an
				// application.
				if (!permitEUs.isEmpty()) {
					for (PermitEU peu : permitEUs.values()) {
						try {
							String euid = peu.getFpEU().getEpaEmuId();
							messages.add(new ValidationMessage(
									"Emission Units",
									"Permit contains an emission unit "
											+ "which cannot be found in an application. "
											+ "Permit EU ID is " + euid + ".",
									ValidationMessage.Severity.WARNING,
									buildEURef(Integer.toString(peu
											.getPermitEUID())), euid,permit.getPermitID()));
						} catch (Exception e) {
							logger.error(
									"Unexpected exception while trying to obtain facility EU from permit "
											+ "EU for permit EU DB ID "
											+ peu.getPermitEUID() + ".", e);
							messages.add(new ValidationMessage(
									"Emission Units",
									"Permit contains an emission unit "
											+ "which cannot be found in an application. "
											+ "Permit EU DB ID is "
											+ peu.getPermitEUID()
											+ " (EU ID not available due to "
											+ "unexpected exception "
											+ e.getClass().getName() + ").",
									ValidationMessage.Severity.WARNING,
									buildEURef(Integer.toString(peu
											.getPermitEUID())), peu
											.getPermitEUID() + "",permit.getPermitID()));
						}
					}
				}

				if (mostRecentAppDate != null) {
					ApplicationSearchResult[] allApps = applicationBO
							.retrieveApplicationSearchResults(null, null,
									permit.getFacilityId(), null, null, null,
									null, null, false, null, null, null, true);
					for (ApplicationSearchResult app : allApps) {
						if (app.getReceivedDate() != null
								&& mostRecentAppDate.compareTo(app
										.getReceivedDate()) < 0) {
							// check to see if application includes EUs that are
							// part of the
							// permit (Mantis 2636)
							boolean appHasPermitEUs = false;
							Application application = applicationBO
									.retrieveApplication(app.getApplicationId());
							for (ApplicationEU appEU : application
									.getIncludedEus()) {
								if (permitEUCorrIds.contains(appEU.getFpEU()
										.getCorrEpaEmuId())) {
									appHasPermitEUs = true;
									break;
								}
							}
							if (appHasPermitEUs) {
								messages.add(new ValidationMessage(
										"Application", "Facility ("
												+ permit.getFacilityId()
												+ ") has a more recent "
												+ "application ("
												+ app.getApplicationNumber()
												+ ").",
										ValidationMessage.Severity.WARNING,
										buildPermitProfileRef(),permit.getPermitID()));
							}
						}
					}
				} else {
					messages.add(new ValidationMessage("Application",
							"Cannot determine most recent application "
									+ "date for permit number "
									+ permit.getPermitNumber() + ".",
							ValidationMessage.Severity.WARNING,
							buildPermitProfileRef(),permit.getPermitID()));
				}
			} else {
				messages.add(new ValidationMessage("Application",
						"Unable to retrieve any applications "
								+ "for permit number "
								+ permit.getPermitNumber() + ".",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
		}
		
		// for NSR wavier check for update facility pte table flag
		if (permit instanceof PTIOPermit 
				&& !((PTIOPermit)permit).isActionTypePermit()) {
			if (!permit.isUpdateFacilityDtlPTETableComments()) {
					messages.add(new ValidationMessage("updateFacilityDtlPTETable",
							"Check/Update Facility Inventory PTE Table is not set.",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
			}
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;
	}

	/*
	 * Draft issuance QA/QC checks.
	 */
	/*
	public static List<ValidationMessage> validateDraftIssuance(Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		for (ValidationMessage vm : messages) {
			if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
				vm.setSeverity(ValidationMessage.Severity.INFO);
				infoMessages.add(vm);
			}
		}
		for (ValidationMessage vm : infoMessages) {
			messages.remove(vm);
		}

		// Check reason codes to see if reason code allows permit to be issued
		// draft.
		boolean isSigMod = false;
		boolean isReopening = false;
		boolean isRescind = false;
		boolean isAPA = false;
		boolean isMinorMod = false;
		boolean isOffPermitChange = false;

		for (String reason : permit.getPermitReasonCDs()) {
			if (reason.equals(PermitReasonsDef.SPM)) {
				isSigMod = true;
			} else if (reason.equals(PermitReasonsDef.REOPENING)) {
				isReopening = true;
			} else if (reason.equals(PermitReasonsDef.REVOKE_REISSUE)) {
				isRevokeReissue = true;
			} else if (reason.equals(PermitReasonsDef.APA)) {
				isAPA = true;
			} else if (reason.equals(PermitReasonsDef.MPM)) {
				isMinorMod = true;
			} else if (reason.equals(PermitReasonsDef.OFF_PERMIT_CHANGE)) {
				isOffPermitChange = true;
			}
		}

		if (!isSigMod && !isReopening && !isRevokeReissue) {
			String unless = "unless it is also an SPM, a Reopening, or a Rescind/Reissue.";
			if (isAPA) {
				messages.add(new ValidationMessage("Permit",
						"This APA may not be issued draft " + unless,
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef()));
			}
			if (isMinorMod) {
				messages.add(new ValidationMessage("Permit",
						"This minor permit modification may not be issued draft "
								+ unless, ValidationMessage.Severity.ERROR,
						buildPermitProfileRef()));
			}
			if (isOffPermitChange) {
				messages.add(new ValidationMessage("Permit",
						"This Off Permit Change may not be issued draft "
								+ unless, ValidationMessage.Severity.ERROR,
						buildPermitProfileRef()));
			}
		}

		// Check the Issuance Date.
		if (permit.getDraftIssueDate() == null) {
			messages.add(new ValidationMessage("draftIssuanceStatus",
					"Draft Issuance Date is not set.",
					ValidationMessage.Severity.ERROR, buildDraftRef()));
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(1980, 0, 1, 0, 0, 0);
			if (permit.getDraftIssueDate().getTime() < cal.getTimeInMillis()) {
				messages.add(new ValidationMessage("draftIssueDate",
						"Draft Issuance year is before 1980.",
						ValidationMessage.Severity.ERROR, buildDraftRef()));
			}
		}

		// Check the permit's documents.
		boolean hasIntroPkg = false;
		boolean hasIssuanceDoc = false;
		for (PermitDocument doc : permit.getDocuments()) {
			ValidationMessage[] vms = doc.validate();
			for (ValidationMessage vm : vms) {
				messages.add(vm);
			}
			if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.INTRO_PACKAGE)) {
				hasIntroPkg = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.ISSUANCE_DOCUMENT)
					&& doc.getIssuanceStageFlag() == null) {
				messages.add(new ValidationMessage("draftIssuanceDocument",
						"A document has been marked as an issuance document "
								+ " but has no associated issuance stage.",
						ValidationMessage.Severity.WARNING, buildDraftRef()));
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.ISSUANCE_DOCUMENT)
					&& doc.getIssuanceStageFlag().equals(
							PermitDocIssuanceStageDef.DRAFT)) {
				hasIssuanceDoc = true;
			}
		}

		if (IssuanceStatusDef.NOT_READY.equalsIgnoreCase(permit
				.getDraftIssuanceStatusCd())) {
			messages.add(new ValidationMessage("draftIssuanceStatus",
					"Draft Issuance status is not ready.",
					ValidationMessage.Severity.ERROR, buildDraftRef()));
		}

		if (!hasIntroPkg) {
			messages.add(new ValidationMessage("draftIP",
					"Permit has no Introductory Package document.",
					ValidationMessage.Severity.ERROR, buildDraftRef()));
		}
		if (!hasIssuanceDoc) {
			messages.add(new ValidationMessage("draftIssuanceDocument",
					"Permit has no Issuance document.",
					ValidationMessage.Severity.ERROR, buildDraftRef()));
		}

		messages.addAll(infoMessages);

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;
	}
	*/

	/*
     * 
     */
	/*
	public static List<ValidationMessage> validateDraftIssuanceCheckIn(
			Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		if (!IssuanceStatusDef.ISSUED.equalsIgnoreCase(permit
				.getDraftIssuanceStatusCd())) {
			messages.add(new ValidationMessage("draftIssuanceStatus",
					"Draft Issuance status is not Issued.",
					ValidationMessage.Severity.ERROR, buildDraftRef()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;
	}
	*/

	public static List<ValidationMessage> validateRefferralDate(Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		/*
		 * if (PermitGlobalStatusDef.ISSUED_PPP.equalsIgnoreCase(
		 * permit.getPermitGlobalStatusCD())){ PermitIssuance di =
		 * ((TVPermit)permit).getPppIssuance(); if (di.getPublicCommentEndDate()
		 * == null && !((TVPermit)permit).isPppReviewWaived() ) {
		 * messages.add(new ValidationMessage("endOfCommentPeriod",
		 * "The Comment Period End Date  for the PPP must be entered.",
		 * ValidationMessage.Severity.ERROR, buildPppRef())); } } else {
		 */
		PermitIssuance di = permit.getDraftIssuance();
		if (di.getPublicCommentEndDate() == null) {
			messages.add(new ValidationMessage("endOfCommentPeriod",
					"The draft public notice Publish Date and Comments Period End Date "
							+ "must be entered.",
					ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
		}
		// }

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;
	}

	/*
	 * PPP issuance QA/QC checks.
	 */
	/*
	 * public static List<ValidationMessage> validatePPPIssuance(Permit permit)
	 * {
	 * 
	 * ArrayList<ValidationMessage> messages = new
	 * ArrayList<ValidationMessage>(validateBasicPermit(permit));
	 * ArrayList<ValidationMessage> infoMessages = new
	 * ArrayList<ValidationMessage>();
	 * 
	 * for (ValidationMessage vm : messages) { if (vm.getSeverity() ==
	 * ValidationMessage.Severity.WARNING) {
	 * vm.setSeverity(ValidationMessage.Severity.INFO); infoMessages.add(vm); }
	 * } for (ValidationMessage vm : infoMessages) { messages.remove(vm); }
	 * 
	 * if (!(permit instanceof TVPermit)) { messages.add(new
	 * ValidationMessage("TVPermit", "Permit is not a TVPermit.",
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef()));
	 * 
	 * if (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
	 * (vm.getMessage() != null &&
	 * !vm.getMessage().startsWith("Return to initial permit ")) { vm = new
	 * ValidationMessage("Permit", "Return to initial permit " +
	 * permit.getPermitNumber() + ".", ValidationMessage.Severity.INFO,
	 * buildPermitProfileRef()); messages.add(0, vm); } } return messages; }
	 * 
	 * // Check reason codes to see if reason code allows permit to be issued
	 * PPP. boolean isSigMod = false; boolean isReopening = false; boolean
	 * isRevokeReissue = false; boolean isAPA = false; boolean isMinorMod =
	 * false; boolean isOffPermitChange = false; boolean isInitial = false;
	 * boolean isRenewal = false;
	 * 
	 * for (String reason : permit.getPermitReasonCDs()) { if
	 * (reason.equals(PermitReasonsDef.SPM)) { isSigMod = true; } else if
	 * (reason.equals(PermitReasonsDef.REOPENING)) { isReopening = true; } else
	 * if (reason.equals(PermitReasonsDef.REVOKE_REISSUE)) { isRevokeReissue =
	 * true; } else if (reason.equals(PermitReasonsDef.APA)) { isAPA = true; }
	 * else if (reason.equals(PermitReasonsDef.MPM)) { isMinorMod = true; } else
	 * if (reason.equals(PermitReasonsDef.OFF_PERMIT_CHANGE)) {
	 * isOffPermitChange = true; } else if
	 * (reason.equals(PermitReasonsDef.INITIAL)) { isInitial = true; } else if
	 * (reason.equals(PermitReasonsDef.RENEWAL)) { isRenewal = true; } }
	 * 
	 * if (!isSigMod && !isReopening && !isRevokeReissue) { String unless =
	 * "unless it is also an SPM, a Reopening, or a Rescind/Reissue."; if
	 * (isAPA) { messages.add(new ValidationMessage("Permit",
	 * "This APA may not be issued PPP " + unless,
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); } if
	 * (isMinorMod) { messages.add(new ValidationMessage("Permit",
	 * "This minor permit modification may not be issued PPP " + unless,
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); } if
	 * (isOffPermitChange) { messages.add(new ValidationMessage("Permit",
	 * "This Off Permit Change may not be issued PPP " + unless,
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); } }
	 * 
	 * // Check for previous draft issuance. if (isSigMod || isReopening ||
	 * isRevokeReissue || isInitial || isRenewal) {
	 * 
	 * PermitIssuance pi = ((TVPermit) permit).getDraftIssuance(); if (pi !=
	 * null) { if (pi.getIssuanceStatusCd() == null ||
	 * !pi.getIssuanceStatusCd().equals(IssuanceStatusDef.ISSUED)) {
	 * messages.add(new ValidationMessage("draftIssuance",
	 * "Permit must first be issued draft.", ValidationMessage.Severity.ERROR,
	 * buildDraftRef())); } Timestamp endOfCmnt = pi.getPublicCommentEndDate();
	 * Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
	 * if (endOfCmnt == null) { messages.add(new
	 * ValidationMessage("endOfCommentPeriod",
	 * "Permit has no end of draft comment period.",
	 * ValidationMessage.Severity.ERROR, buildDraftRef())); } else if
	 * (endOfCmnt.after(now)) { messages.add(new
	 * ValidationMessage("endOfCommentPeriod",
	 * "End of draft comment period has not been reached.",
	 * ValidationMessage.Severity.ERROR, buildDraftRef())); } } else {
	 * messages.add(new ValidationMessage("draftIssuance",
	 * "Permit must first be issued draft.", ValidationMessage.Severity.ERROR,
	 * buildPppRef())); } }
	 * 
	 * if (((TVPermit) permit).getPppIssueDate() == null) { messages.add(new
	 * ValidationMessage("pppIssuanceStatus", "PPP Issuance Date is not set.",
	 * ValidationMessage.Severity.ERROR, buildPppRef())); } else { Calendar cal
	 * = Calendar.getInstance(); cal.set(1980, 0, 1, 0, 0, 0); if (((TVPermit)
	 * permit).getPppIssueDate().getTime() < cal.getTimeInMillis()) {
	 * messages.add(new ValidationMessage("pppIssueDate",
	 * "PPP Issuance year is before 1980.", ValidationMessage.Severity.ERROR,
	 * buildPppRef())); } }
	 * 
	 * if (IssuanceStatusDef.NOT_READY.equalsIgnoreCase(((TVPermit) permit)
	 * .getPppIssuanceStatusCd())) { messages.add(new
	 * ValidationMessage("pppIssuanceStatus",
	 * "PPP Issuance status is not ready.", ValidationMessage.Severity.ERROR,
	 * buildPppRef())); }
	 * 
	 * // Check the permit's documents. boolean hasIntroPkg = false; boolean
	 * hasIssuanceDoc = false; for (PermitDocument doc : permit.getDocuments())
	 * { ValidationMessage[] vms = doc.validate(); for (ValidationMessage vm :
	 * vms) { messages.add(vm); } if
	 * (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.INTRO_PACKAGE)) {
	 * hasIntroPkg = true; } else if
	 * (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.ISSUANCE_DOCUMENT) &&
	 * doc
	 * .getIssuanceStageFlag().equals(PermitDocIssuanceStageDef.PRELIMINARY_PROPOSED
	 * )) { hasIssuanceDoc = true; } }
	 * 
	 * if (!hasIntroPkg) { messages.add(new ValidationMessage("ipUploadButton",
	 * "Permit has no Introductory Package document.",
	 * ValidationMessage.Severity.ERROR, buildPppRef())); } if (!hasIssuanceDoc)
	 * { messages.add(new ValidationMessage("idUploadButton",
	 * "Permit has no Issuance document.", ValidationMessage.Severity.ERROR,
	 * buildPppRef())); }
	 * 
	 * messages.addAll(infoMessages);
	 * 
	 * if (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
	 * (vm.getMessage() != null &&
	 * !vm.getMessage().startsWith("Return to initial permit ")) { vm = new
	 * ValidationMessage("Permit", "Return to initial permit " +
	 * permit.getPermitNumber() + ".", ValidationMessage.Severity.INFO,
	 * buildPermitProfileRef()); messages.add(0, vm); } } return messages; }
	 */
	/*
     * 
     */
	/*
	 * public static List<ValidationMessage> validatePPPIssuanceCheckIn(Permit
	 * permit) {
	 * 
	 * ArrayList<ValidationMessage> messages = new
	 * ArrayList<ValidationMessage>();
	 * 
	 * if (!(permit instanceof TVPermit)) { messages.add(new
	 * ValidationMessage("TVPermit", "Permit is not a TVPermit.",
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); if
	 * (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
	 * (vm.getMessage() != null &&
	 * !vm.getMessage().startsWith("Return to initial permit ")) { vm = new
	 * ValidationMessage("Permit", "Return to initial permit " +
	 * permit.getPermitNumber() + ".", ValidationMessage.Severity.INFO,
	 * buildPermitProfileRef()); messages.add(0, vm); } } return messages; }
	 * 
	 * if (!IssuanceStatusDef.ISSUED.equalsIgnoreCase(((TVPermit)
	 * permit).getPppIssuanceStatusCd())) messages.add(new
	 * ValidationMessage("pppIssuanceStatus",
	 * "PPP Issuance status is not Issued.", ValidationMessage.Severity.ERROR,
	 * buildPppRef()));
	 * 
	 * if (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
	 * (vm.getMessage() != null &&
	 * !vm.getMessage().startsWith("Return to initial permit ")) { vm = new
	 * ValidationMessage("Permit", "Return to initial permit " +
	 * permit.getPermitNumber() + ".", ValidationMessage.Severity.INFO,
	 * buildPermitProfileRef()); messages.add(0, vm); } } return messages; }
	 */

	/*
	 * PP issuance QA/QC checks. STARS2
	 */
	/*
	 * public static List<ValidationMessage> validatePPIssuance(Permit permit) {
	 * 
	 * ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
	 * validateBasicPermit(permit)); ArrayList<ValidationMessage> infoMessages =
	 * new ArrayList<ValidationMessage>();
	 * 
	 * for (ValidationMessage vm : messages) { if (vm.getSeverity() ==
	 * ValidationMessage.Severity.WARNING) {
	 * vm.setSeverity(ValidationMessage.Severity.INFO); infoMessages.add(vm); }
	 * } for (ValidationMessage vm : infoMessages) { messages.remove(vm); }
	 * 
	 * if (!(permit instanceof TVPermit)) { messages.add(new
	 * ValidationMessage("TVPermit", "Permit is not a TVPermit.",
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); if
	 * (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
	 * (vm.getMessage() != null && !vm.getMessage().startsWith(
	 * "Return to initial permit ")) { vm = new ValidationMessage("Permit",
	 * "Return to initial permit " + permit.getPermitNumber() + ".",
	 * ValidationMessage.Severity.INFO, buildPermitProfileRef());
	 * messages.add(0, vm); } } return messages; }
	 * 
	 * // Check reason codes to see if reason code allows permit to be issued //
	 * PP. boolean isSigMod = false; boolean isReopening = false; boolean
	 * isRevokeReissue = false; boolean isAPA = false; boolean isOffPermitChange
	 * = false; boolean isInitial = false; boolean isRenewal = false;
	 * 
	 * for (String reason : permit.getPermitReasonCDs()) { if
	 * (reason.equals(PermitReasonsDef.SPM)) { isSigMod = true; } else if
	 * (reason.equals(PermitReasonsDef.REOPENING)) { isReopening = true; } else
	 * if (reason.equals(PermitReasonsDef.REVOKE_REISSUE)) { isRevokeReissue =
	 * true; } else if (reason.equals(PermitReasonsDef.APA)) { isAPA = true; }
	 * else if (reason.equals(PermitReasonsDef.OFF_PERMIT_CHANGE)) {
	 * isOffPermitChange = true; } else if
	 * (reason.equals(PermitReasonsDef.INITIAL)) { isInitial = true; } else if
	 * (reason.equals(PermitReasonsDef.RENEWAL)) { isRenewal = true; } }
	 * 
	 * if (!isSigMod && !isReopening && !isRevokeReissue) { String unless =
	 * "unless it is also an SPM, a Reopening, or a Rescind/Reissue."; if
	 * (isAPA) { messages.add(new ValidationMessage("Permit",
	 * "This APA may not be issued PP " + unless,
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); } if
	 * (isOffPermitChange) { messages.add(new ValidationMessage( "Permit",
	 * "This Off Permit Change may not be issued PP " + unless,
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); } }
	 * 
	 * // Check for previous PPP issuance.
	 * 
	 * if ((isSigMod || isReopening || isRevokeReissue || isInitial ||
	 * isRenewal) && !((TVPermit) permit).isPppReviewWaived()) {
	 * 
	 * PermitIssuance pi = ((TVPermit) permit).getPppIssuance(); if (pi != null)
	 * { if (pi.getIssuanceStatusCd() == null ||
	 * !pi.getIssuanceStatusCd().equals(IssuanceStatusDef.ISSUED)) {
	 * messages.add(new ValidationMessage("pppIssuance",
	 * "Permit must first be issued PPP.", ValidationMessage.Severity.ERROR,
	 * buildPppRef())); } Timestamp endOfCmnt = pi.getPublicCommentEndDate();
	 * Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
	 * if (endOfCmnt == null) { messages.add(new
	 * ValidationMessage("endOfCommentPeriod",
	 * "Permit has no end of PPP comment period.",
	 * ValidationMessage.Severity.ERROR, buildPppRef())); } else if
	 * (endOfCmnt.after(now)) { messages.add(new
	 * ValidationMessage("endOfCommentPeriod",
	 * "End of PPP comment period has not been reached.",
	 * ValidationMessage.Severity.ERROR, buildPppRef())); } } else {
	 * messages.add(new ValidationMessage("pppIssuance",
	 * "Permit must first be issued PPP.", ValidationMessage.Severity.ERROR,
	 * buildPppRef())); } }
	 * 
	 * 
	 * if (((TVPermit) permit).getPpIssueDate() == null) { messages.add(new
	 * ValidationMessage("ppIssuanceStatus", "PP Issuance Date is not set.",
	 * ValidationMessage.Severity.ERROR, buildPpRef())); } else { Calendar cal =
	 * Calendar.getInstance(); cal.set(1980, 0, 1, 0, 0, 0); if (((TVPermit)
	 * permit).getPpIssueDate().getTime() < cal .getTimeInMillis()) {
	 * messages.add(new ValidationMessage("ppIssueDate",
	 * "PP Issuance year is before 1980.", ValidationMessage.Severity.ERROR,
	 * buildPpRef())); } }
	 * 
	 * if (IssuanceStatusDef.NOT_READY.equalsIgnoreCase(((TVPermit) permit)
	 * .getPpIssuanceStatusCd())) { messages.add(new
	 * ValidationMessage("ppIssuanceStatus", "PP Issuance status is not ready.",
	 * ValidationMessage.Severity.ERROR, buildPpRef())); }
	 * 
	 * // Check the permit's documents. boolean hasIntroPkg = false; boolean
	 * hasIssuanceDoc = false; for (PermitDocument doc : permit.getDocuments())
	 * { ValidationMessage[] vms = doc.validate(); for (ValidationMessage vm :
	 * vms) { messages.add(vm); } if
	 * (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.INTRO_PACKAGE)) {
	 * hasIntroPkg = true; } else if (doc.getPermitDocTypeCD().equals(
	 * PermitDocTypeDef.ISSUANCE_DOCUMENT) && doc.getIssuanceStageFlag().equals(
	 * PermitDocIssuanceStageDef.PROPOSED)) { hasIssuanceDoc = true; } } if
	 * (!hasIntroPkg) { messages.add(new ValidationMessage("ipUploadButton",
	 * "Permit has no Introductory Package document.",
	 * ValidationMessage.Severity.ERROR, buildPpRef())); } if (!hasIssuanceDoc)
	 * { messages.add(new ValidationMessage("idUploadButton",
	 * "Permit has no Issuance document.", ValidationMessage.Severity.ERROR,
	 * buildPpRef())); }
	 * 
	 * messages.addAll(infoMessages);
	 * 
	 * if (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
	 * (vm.getMessage() != null &&
	 * !vm.getMessage().startsWith("Return to initial permit ")) { vm = new
	 * ValidationMessage("Permit", "Return to initial permit " +
	 * permit.getPermitNumber() + ".", ValidationMessage.Severity.INFO,
	 * buildPermitProfileRef()); messages.add(0, vm); } } return messages; }
	 */

	/*
	 * public static List<ValidationMessage> validatePPIssuanceCheckIn( Permit
	 * permit) {
	 * 
	 * ArrayList<ValidationMessage> messages = new
	 * ArrayList<ValidationMessage>();
	 * 
	 * if (!(permit instanceof TVPermit)) { messages.add(new
	 * ValidationMessage("TVPermit", "Permit is not a TVPermit.",
	 * ValidationMessage.Severity.ERROR, buildPermitProfileRef())); if
	 * (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
	 * (vm.getMessage() != null && !vm.getMessage().startsWith(
	 * "Return to initial permit ")) { vm = new ValidationMessage("Permit",
	 * "Return to initial permit " + permit.getPermitNumber() + ".",
	 * ValidationMessage.Severity.INFO, buildPermitProfileRef());
	 * messages.add(0, vm); } } return messages; }
	 * 
	 * if (!IssuanceStatusDef.ISSUED.equalsIgnoreCase(((TVPermit) permit)
	 * .getPpIssuanceStatusCd()))
	 * 
	 * messages.add(new ValidationMessage("ppIssuanceStatus",
	 * "PP Issuance status is not Issued.", ValidationMessage.Severity.ERROR,
	 * buildPpRef()));
	 * 
	 * if (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
	 * (vm.getMessage() != null &&
	 * !vm.getMessage().startsWith("Return to initial permit ")) { vm = new
	 * ValidationMessage("Permit", "Return to initial permit " +
	 * permit.getPermitNumber() + ".", ValidationMessage.Severity.INFO,
	 * buildPermitProfileRef()); messages.add(0, vm); } } return messages; }
	 */

	/*
	 * Final issuance QA/QC checks.
	 */
	/*
	public static List<ValidationMessage> validateFinalIssuanceStars2(
			Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		for (ValidationMessage vm : messages) {
			if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
				vm.setSeverity(ValidationMessage.Severity.INFO);
				infoMessages.add(vm);
			}
		}
		for (ValidationMessage vm : infoMessages) {
			messages.remove(vm);
		}

		// Check reason codes to see if reason code requires previous issuance
		// type.
		boolean isSigMod = false;
		boolean isReopening = false;
		boolean isRevokeReissue = false;
		boolean isMinorMod = false;
		boolean isInitial = false;
		boolean isRenewal = false;

		for (String reason : permit.getPermitReasonCDs()) {
			if (reason.equals(PermitReasonsDef.SPM)) {
				isSigMod = true;
			} else if (reason.equals(PermitReasonsDef.REOPENING)) {
				isReopening = true;
			} else if (reason.equals(PermitReasonsDef.REVOKE_REISSUE)) {
				isRevokeReissue = true;
			} else if (reason.equals(PermitReasonsDef.MPM)) {
				isMinorMod = true;
			} else if (reason.equals(PermitReasonsDef.INITIAL)) {
				isInitial = true;
			} else if (reason.equals(PermitReasonsDef.RENEWAL)) {
				isRenewal = true;
			}
		}

		if (permit instanceof TVPermit) {

			// Check for previous PP issuance.
			if ((isMinorMod || isSigMod || isReopening || isRevokeReissue
					|| isInitial || isRenewal)
					&& (((TVPermit) permit).getUsepaOutcomeCd() != null && !((TVPermit) permit)
							.getUsepaOutcomeCd().equals(USEPAOutcomeDef.WAIVED))) {

				PermitIssuance pi = ((TVPermit) permit).getPpIssuance();
				if (pi != null) {
					if (pi.getIssuanceStatusCd() == null
							|| !pi.getIssuanceStatusCd().equals(
									IssuanceStatusDef.ISSUED)) {
						messages.add(new ValidationMessage("ppIssuance",
								"Permit must first be issued PP.",
								ValidationMessage.Severity.ERROR, buildPpRef()));
					}
					Timestamp usepaCompleteDate = ((TVPermit) permit)
							.getUsepaCompleteDate();
					Timestamp now = new Timestamp(Calendar.getInstance()
							.getTimeInMillis());
					if (usepaCompleteDate == null) {
						messages.add(new ValidationMessage("usepaCompleteDate",
								"Permit has no end of PP comment period.",
								ValidationMessage.Severity.ERROR, buildPpRef()));
					} else if (usepaCompleteDate.after(now)) {
						messages.add(new ValidationMessage(
								"usepaCompleteDate",
								"End of PP USEPA Review End Date period has not been reached.",
								ValidationMessage.Severity.ERROR, buildPpRef()));
					}
				} else {
					messages.add(new ValidationMessage("ppIssuance",
							"Permit must first be issued PP.",
							ValidationMessage.Severity.ERROR, buildPpRef()));
				}
			}

		} else if (permit.getPermitType().equals(PermitTypeDef.NSR)
				|| permit.getPermitType().equals(PermitTypeDef.TVPTI)) {

			// Check for previous draft issuance.
			if (permit.isIssueDraft()) {

				PermitIssuance pi = permit.getDraftIssuance();
				if (pi != null) {
					if (pi.getIssuanceStatusCd() == null
							|| !pi.getIssuanceStatusCd().equals(
									IssuanceStatusDef.ISSUED)) {
						messages.add(new ValidationMessage("draftIssuance",
								"Permit must first be issued draft.",
								ValidationMessage.Severity.ERROR,
								buildDraftRef()));
					}
					Timestamp endOfCmnt = pi.getPublicCommentEndDate();
					Timestamp now = new Timestamp(Calendar.getInstance()
							.getTimeInMillis());
					if (endOfCmnt == null) {
						messages.add(new ValidationMessage(
								"endOfCommentPeriod",
								"Permit has no end of draft comment period.",
								ValidationMessage.Severity.ERROR,
								buildDraftRef()));
					} else if (endOfCmnt.after(now)) {
						messages.add(new ValidationMessage(
								"endOfCommentPeriod",
								"End of draft comment period has not been reached.",
								ValidationMessage.Severity.ERROR,
								buildDraftRef()));
					}
				} else {
					messages.add(new ValidationMessage("draftIssuance",
							"Permit must first be issued draft.",
							ValidationMessage.Severity.ERROR, buildDraftRef()));
				}
			}

		}

		// check that there is a billing address if there are fees.
		PTIOPermit ptioPermit = null;
		if (permit instanceof PTIOPermit) {
			ptioPermit = (PTIOPermit) permit;
			if (ptioPermit.getTotalAmount() != null
					&& ptioPermit.getTotalAmount() > 0d) {
				if (ptioPermit.getFacilityId() != null) {
					Facility f = new Facility();
					f.setFacilityId(ptioPermit.getFacilityId());
					// Get all the contacts for the facility
					try {
						FacilityService facilityBO = null;
						facilityBO = ServiceFactory.getInstance()
								.getFacilityService();
						f.setAllContacts(facilityBO.retrieveFacilityContacts(f
								.getFacilityId()));
						if (f.getBillingContact() == null) {
							messages.add(new ValidationMessage(
									"Facility",
									"There is no active billing contact for the facility.",
									ValidationMessage.Severity.ERROR,
									buildAppProfileRef()));
						}
					} catch (ServiceFactoryException sfe) {
						logger.error("getFacilityService() failed for permit "
								+ permit.getPermitNumber(), sfe);
						messages.add(new ValidationMessage("Facility",
								"Failed to retrieve contacts for facility: "
										+ sfe.getMessage(),
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef()));
					} catch (RemoteException re) {
						logger.error("retrieveFacilityAddresses() failed "
								+ permit.getPermitNumber(), re);
						messages.add(new ValidationMessage("Facility",
								"Failed to retrieve contacts for facility: "
										+ re.getMessage(),
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef()));
					}
				}
			}
		}

		// Check the permit's documents.
		boolean hasIntroPkg = false;
		boolean hasIssuanceDoc = false;
		for (PermitDocument doc : permit.getDocuments()) {

			ValidationMessage[] vms = doc.validate();
			for (ValidationMessage vm : vms) {
				messages.add(vm);
			}
			if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.INTRO_PACKAGE)) {
				hasIntroPkg = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.ISSUANCE_DOCUMENT)
					&& doc.getIssuanceStageFlag().equals(
							PermitDocIssuanceStageDef.FINAL)) {
				hasIssuanceDoc = true;
			}
		}
		if (!hasIntroPkg) {
			messages.add(new ValidationMessage("finalIntroductoryPackage",
					"Permit has no Introductory Package document.",
					ValidationMessage.Severity.ERROR, buildFinalRef()));
		}
		if (!hasIssuanceDoc) {
			messages.add(new ValidationMessage("finalIssuanceDocument",
					"Permit has no Issuance document.",
					ValidationMessage.Severity.ERROR, buildFinalRef()));
		}

		if (IssuanceStatusDef.NOT_READY.equalsIgnoreCase(permit
				.getFinalIssuanceStatusCd())) {
			messages.add(new ValidationMessage("finalIssuanceStatus",
					"Final Issuance status is not ready.",
					ValidationMessage.Severity.ERROR, buildFinalRef()));
		}

		if (permit.getFinalIssueDate() == null) {
			messages.add(new ValidationMessage("finalIssuanceStatus",
					"Final Issuance Date is not set.",
					ValidationMessage.Severity.ERROR, buildFinalRef()));
		} else {
			Calendar cal = Calendar.getInstance();
			cal.set(1980, 0, 1, 0, 0, 0);
			if (permit.getFinalIssueDate().getTime() < cal.getTimeInMillis()) {
				messages.add(new ValidationMessage("finalIssueDate",
						"Final Issuance year is before 1980.",
						ValidationMessage.Severity.ERROR, buildFinalRef()));
			}
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long now = cal.getTimeInMillis();

		if (permit.getEffectiveDate() == null) {
			messages.add(new ValidationMessage("effectiveDate",
					"Effective Date is not set.",
					ValidationMessage.Severity.ERROR, buildFinalRef()));
		} else if (permit.getEffectiveDate().getTime() < now) {
			messages.add(new ValidationMessage("effectiveDate",
					"The effective date for the permit must be a future date.",
					ValidationMessage.Severity.ERROR, buildFinalRef()));
		}

		messages.addAll(infoMessages);

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;
	}
	*/

	/*
     * 
     */
	/*
	public static List<ValidationMessage> validateFinalIssuanceCheckIn(
			Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		if (!IssuanceStatusDef.ISSUED.equalsIgnoreCase(permit
				.getFinalIssuanceStatusCd()))
			messages.add(new ValidationMessage("finalIssuanceStatus",
					"Final Issuance status is not Issued.",
					ValidationMessage.Severity.ERROR, buildFinalRef()));

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;
	}
	*/

	private static List<ValidationMessage> validateIssueDenial(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		if (!PermitGlobalStatusDef.ISSUED_DENIAL.equalsIgnoreCase(permit
				.getPermitGlobalStatusCD()))
			messages.add(new ValidationMessage("denyPermit",
					"Permit status is not Issue Denial.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;
	}

	private static List<ValidationMessage> validateDenialPending(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		if (!PermitGlobalStatusDef.DENIAL_PENDING.equalsIgnoreCase(permit
				.getPermitGlobalStatusCD()))
			messages.add(new ValidationMessage("denyPermit",
					"Permit status is not Denial Pending. Click on 'Deny Permit' "
							+ "in permit detail.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;
	}
/*
	public static List<ValidationMessage> validatePBRPermit(Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		// Simple first step is to see what pbr thinks of itself.
		ValidationMessage[] vms = permit.validate();
		for (ValidationMessage vm : vms) {
			messages.add(vm);
		}

		try {

			boolean hasPBRNotification = false;
			boolean hasOtherApplications = false;
			boolean hasEUs = false;
			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			PermitService permitBO = ServiceFactory.getInstance()
					.getPermitService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof PBRNotification
						&& permitApp.getApplicationNumber().equals(
								permit.getPermitNumber())) {
					hasPBRNotification = true;
					if (permitApp.getIncludedEus() != null
							&& !permitApp.getIncludedEus().isEmpty()) {
						hasEUs = true;
					}
					// Indicate what Permit EUs are active because of PTIO, PTI
					// or State PTO permits

					for (ApplicationEU aeu : permitApp.getEus()) {
						if (!aeu.isExcluded()) {
							loop: for (PermitEUGroup eug : permit.getEuGroups()) {
								for (PermitEU peu : eug.getPermitEUs()) {
									if (peu.getCorrEpaEMUID().equals(
											aeu.getFpEU().getCorrEpaEmuId())) {
										List<Permit> permits = permitBO
												.searchEuPermits(aeu.getFpEU()
														.getCorrEpaEmuId());
										for (Permit p : permits) {
											if (PermitTypeDef.NSR.equals(p
													.getPermitType())
													|| PermitTypeDef.SPTO
															.equals(p
																	.getPermitType())
													|| PermitTypeDef.TVPTI
															.equals(p
																	.getPermitType())) {
												peu.setActiveWithoutPBR(true);
												continue loop;
											}
										}
										continue loop;
									}
								}
							}
						}
					}
				} else {
					hasOtherApplications = true;
				}
			}
			if (!hasPBRNotification) {
				messages.add(new ValidationMessage("Application",
						"Unable to locate any PBRNotification applications. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef()));
			}
			if (!hasOtherApplications) {
				messages.add(new ValidationMessage("Application",
						"PBRNotification has other applications. ",
						ValidationMessage.Severity.WARNING,
						buildAppProfileRef()));
			}
			if (hasPBRNotification && !hasEUs) {
				messages.add(new ValidationMessage(
						"Application",
						"PBRNotification application does not contain any emission units. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef()));
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;
	}

	public static List<ValidationMessage> validatePBRRevocation(Permit permit) {

		List<ValidationMessage> messages = validatePBRPermit(permit);

		try {
			for (PermitEUGroup eug : permit.getEuGroups()) {
				for (PermitEU peu : eug.getPermitEUs()) {
					if (peu.isActiveWithoutPBR()) {
						messages.add(new ValidationMessage(
								"Emission Unit" + peu.getFpEU().getEpaEmuId(),
								"Emission Unit "
										+ peu.getFpEU().getEpaEmuId()
										+ " with Active status. Emissions Unit must first be issued RPR.",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef()));
					}
					//
					// else if
					// (peu.getPermitStatusCd().equals(PermitStatusDef.EXPIRED))
					// { messages.add(new ValidationMessage("Emission Units",
					// "Emissions Unit with Expired status. Emissions Unit must first be issued RPR."
					// , ValidationMessage.Severity.ERROR,
					// buildAppProfileRef())); }
					//
				}
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;

	}

	public static List<ValidationMessage> validatePBRPostFinal(Permit permit) {

		List<ValidationMessage> messages = validatePBRPermit(permit);

		try {

			boolean hasPBRNotification = false;
			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof PBRNotification) {
					hasPBRNotification = true;
					String disp = ((PBRNotification) permitApp)
							.getDispositionFlag();
					if (disp == null || !disp.equals(PBRNotification.ACCEPTED)) {
						messages.add(new ValidationMessage("Application",
								"PBR disposition is not set to accepted.",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef()));
					}
				}
			}
			if (!hasPBRNotification) {
				messages.add(new ValidationMessage("Application",
						"Unable to locate any PBRNotification applications. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef()));
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;

	}

	public static List<ValidationMessage> validatePBRReturn(Permit permit) {

		List<ValidationMessage> messages = validatePBRPermit(permit);

		try {

			boolean hasPBRNotification = false;
			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof PBRNotification) {
					hasPBRNotification = true;
					String disp = ((PBRNotification) permitApp)
							.getDispositionFlag();
					if (disp == null || !disp.equals(PBRNotification.DENIED)) {
						messages.add(new ValidationMessage("Application",
								"PBR disposition is not set to denied.",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef()));
					}
				}
			}
			if (!hasPBRNotification) {
				messages.add(new ValidationMessage("Application",
						"Unable to locate any PBRNotification applications. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef()));
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;

	}
*/
	public static List<ValidationMessage> validateRPRPermit(Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		// Simple first step is to see what pbr thinks of itself.
		ValidationMessage[] vms = permit.validate();
		for (ValidationMessage vm : vms) {
			messages.add(vm);
		}

		try {

			boolean hasGenericIssuance = false;
			boolean hasRPRRequest = false;
			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			GenericIssuanceService giBO = ServiceFactory.getInstance()
					.getGenericIssuanceService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof RPRRequest) {

					List<GenericIssuance> genIssuanceList = giBO
							.searchGenericIssuances(null,
									permitApp.getApplicationID(), null, null);
					for (GenericIssuance genIssuance : genIssuanceList) {
						if (genIssuance != null
								&& genIssuance.getIssuanceTypeCd() != null
								&& (genIssuance
										.getIssuanceTypeCd()
										.equals(GenericIssuanceTypeDef.PROPOSED_PTIO_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.PROPOSED_PTI_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.PROPOSED_PTO_REVOCATION)
										|| genIssuance
												.getIssuanceTypeCd()
												.equals(GenericIssuanceTypeDef.PROPOSED_TVPTO_REVOCATION)
										|| genIssuance
												.getIssuanceTypeCd()
												.equals(GenericIssuanceTypeDef.FINAL_PTIO_REVOCATION)
										|| genIssuance
												.getIssuanceTypeCd()
												.equals(GenericIssuanceTypeDef.PROPOSED_FINAL_PTIO_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.FINAL_PTI_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.PROPOSED_FINAL_PTI_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.FINAL_PTO_REVOCATION) 
												|| genIssuance
										.getIssuanceTypeCd()
										.equals(GenericIssuanceTypeDef.FINAL_TVPTO_REVOCATION))) {
							hasGenericIssuance = true;
						}
					}

					if (!hasGenericIssuance) {
						messages.add(new ValidationMessage(
								"Application",
								"Unable to locate any proposed GenericIssuances. ",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef(),permit.getPermitID()));
					}

					hasRPRRequest = true;
					if (permitApp.getReceivedDate() == null) {
						messages.add(new ValidationMessage("Application",
								"RPR application has no revocation request date "
										+ "(application received date). ",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef(),permit.getPermitID()));
					}
					if (((RPRRequest) permitApp).getPermitId() == null) {
						messages.add(new ValidationMessage("Application",
								"RPR application has no permit id for the "
										+ "permit being revoked. ",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef(),permit.getPermitID()));
					} else {
						PermitService permitBO = ServiceFactory.getInstance()
								.getPermitService();
						PermitInfo revokedPermitInfo = permitBO
								.retrievePermit(((RPRRequest) permitApp)
										.getPermitId());
						if (revokedPermitInfo == null
								|| revokedPermitInfo.getPermit() == null) {
							messages.add(new ValidationMessage("Application",
									"Could not locate a permit to be revoked.",
									ValidationMessage.Severity.ERROR,
									buildAppProfileRef(),permit.getPermitID()));
						} else {

							HashMap<Integer, PermitEU> euMap = new HashMap<Integer, PermitEU>();

							List<PermitEU> peus = permit.getEus();
							if (peus.isEmpty()) {
								messages.add(new ValidationMessage(
										"Permit",
										"Permit does not have any emission units.",
										ValidationMessage.Severity.ERROR,
										buildAppProfileRef(),permit.getPermitID()));
							} else {
								for (PermitEU peu : peus) {
									euMap.put(peu.getCorrEpaEMUID(), peu);
								}
							}

							List<ApplicationEU> aeus = permitApp
									.getIncludedEus();
							if (aeus.isEmpty()) {
								messages.add(new ValidationMessage(
										"Application",
										"Application must include at least one"
												+ "emission unit.",
										ValidationMessage.Severity.ERROR,
										buildAppProfileRef(),permit.getPermitID()));
							}

							for (ApplicationEU aeu : aeus) {
								if (aeu.getFpEU() == null) {
									messages.add(new ValidationMessage(
											"Application",
											"Application emission unit missing "
													+ "facility emission unit. "
													+ aeu.getEuText(),
											ValidationMessage.Severity.ERROR,
											buildAppProfileRef(),permit.getPermitID()));
									continue;
								}
								if (!((RPRRequest) permitApp)
										.isRevokeEntirePermit()
										&& (aeu.getEuText() == null || aeu
												.getEuText().length() < 1)) {
									messages.add(new ValidationMessage(
											"Application",
											"Application emission unit "
													+ aeu.getFpEU()
															.getEpaEmuId()
													+ " is missing a basis for revocation.",
											ValidationMessage.Severity.ERROR,
											buildAppProfileRef(), aeu.getFpEU()
													.getEpaEmuId(),permit.getPermitID()));
								}
								euMap.remove(aeu.getFpEU().getCorrEpaEmuId());
							}

							if (((RPRRequest) permitApp).isRevokeEntirePermit()) {
								if (((RPRRequest) permitApp)
										.getBasisForRevocation() == null
										|| ((RPRRequest) permitApp)
												.getBasisForRevocation()
												.length() < 1) {
									messages.add(new ValidationMessage(
											"Application",
											"Application must have a basis for "
													+ "revocation if the entire permit "
													+ "is being revoked.",
											ValidationMessage.Severity.ERROR,
											buildAppProfileRef(),permit.getPermitID()));
								}
								if (!euMap.isEmpty()) {
									messages.add(new ValidationMessage(
											"Application",
											"Application must include all permit"
													+ "emission units if entire permit "
													+ "is being revoked.",
											ValidationMessage.Severity.ERROR,
											buildAppProfileRef(),permit.getPermitID()));
								}
							}
						}
					}
				}
			}
			if (!hasRPRRequest) {
				messages.add(new ValidationMessage("Application",
						"Unable to locate any RPRRequest applications. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef(),permit.getPermitID()));
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef(),permit.getPermitID()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;

	}

	public static List<ValidationMessage> validateRPRProposed(Permit permit) {

		List<ValidationMessage> messages = validateRPRPermit(permit);

		try {

			boolean hasGenericIssuance = false;
			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			GenericIssuanceService giBO = ServiceFactory.getInstance()
					.getGenericIssuanceService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof RPRRequest) {
					String disp = ((RPRRequest) permitApp).getDispositionFlag();
					if (disp == null
							|| !disp.equals(RPRRequest.ISSUED_PROPOSED)) {
						messages.add(new ValidationMessage("Application",
								"RPR disposition flag is not set to "
										+ "RPRRequest.ISSUED_PROPOSED.",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef(),permit.getPermitID()));
					}
					List<GenericIssuance> genIssuanceList = giBO
							.searchGenericIssuances(null,
									permitApp.getApplicationID(), null, null);
					for (GenericIssuance genIssuance : genIssuanceList) {
						if (genIssuance != null
								&& genIssuance.getIssuanceTypeCd() != null
								&& (genIssuance
										.getIssuanceTypeCd()
										.equals(GenericIssuanceTypeDef.PROPOSED_PTIO_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.PROPOSED_PTI_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.PROPOSED_PTO_REVOCATION)
										|| genIssuance
												.getIssuanceTypeCd()
												.equals(GenericIssuanceTypeDef.PROPOSED_TVPTO_REVOCATION)
										|| genIssuance
												.getIssuanceTypeCd()
												.equals(GenericIssuanceTypeDef.PROPOSED_FINAL_PTIO_REVOCATION) 
										//|| genIssuance
										//.getIssuanceTypeCd()
										//.equals(GenericIssuanceTypeDef.PROPOSED_FINAL_PTI_REVOCATION)
												)) {
							hasGenericIssuance = true;
						}
					}
				}
			}
			if (!hasGenericIssuance) {
				messages.add(new ValidationMessage("Application",
						"Unable to locate any proposed GenericIssuances. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef(),permit.getPermitID()));
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef(),permit.getPermitID()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;
	}

	public static List<ValidationMessage> validateRPRFinal(Permit permit) {

		List<ValidationMessage> messages = validateRPRPermit(permit);

		try {

			boolean hasGenericIssuance = false;
			boolean hasIssuedDate = false;
			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			GenericIssuanceService giBO = ServiceFactory.getInstance()
					.getGenericIssuanceService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof RPRRequest) {
					String disp = ((RPRRequest) permitApp).getDispositionFlag();
					if (disp == null || !disp.equals(RPRRequest.ISSUED_FINAL)) {
						messages.add(new ValidationMessage("Application",
								"RPR disposition flag is not set to "
										+ "RPRRequest.ISSUED_FINAL.",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef(),permit.getPermitID()));
					}
					List<GenericIssuance> genIssuanceList = giBO
							.searchGenericIssuances(null,
									permitApp.getApplicationID(), null, null);
					for (GenericIssuance genIssuance : genIssuanceList) {
						if (genIssuance != null
								&& genIssuance.getIssuanceTypeCd() != null
								&& (genIssuance
										.getIssuanceTypeCd()
										.equals(GenericIssuanceTypeDef.FINAL_PTIO_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.FINAL_PTI_REVOCATION)
										//|| genIssuance
										//		.getIssuanceTypeCd()
										//		.equals(GenericIssuanceTypeDef.FINAL_PTO_REVOCATION) 
										|| genIssuance
										.getIssuanceTypeCd()
										.equals(GenericIssuanceTypeDef.FINAL_TVPTO_REVOCATION))) {
							hasGenericIssuance = true;
							if (genIssuance.getIssuanceDate() != null) {
								hasIssuedDate = true;
							}
						}
					}
				}
			}
			if (!hasGenericIssuance) {
				messages.add(new ValidationMessage("Application",
						"Unable to locate any final GenericIssuances. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef(),permit.getPermitID()));
			}
			if (!hasIssuedDate) {
				messages.add(new ValidationMessage("Application",
						"No issued date found. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef(),permit.getPermitID()));
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef(),permit.getPermitID()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;
	}

	public static List<ValidationMessage> validateRPRReturn(Permit permit) {

		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		try {

			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof RPRRequest) {
					String disp = ((RPRRequest) permitApp).getDispositionFlag();
					if (disp == null || !disp.equals(RPRRequest.RETURNED)) {
						messages.add(new ValidationMessage("Application",
								"RPR disposition flag is not set to "
										+ "RPRRequest.RETURNED.",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef(),permit.getPermitID()));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef(),permit.getPermitID()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		return messages;
	}
/*
	public static List<ValidationMessage> validateRPEPermit(Permit permit) {

		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		try {

			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof RPERequest) {
					if (((RPERequest) permitApp).getPermitId() == null) {
						messages.add(new ValidationMessage("Application",
								"Application is missing a permit ID.",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef()));
					} else {

						PermitService permitBO = ServiceFactory.getInstance()
								.getPermitService();
						PermitInfo extendedPermitInfo = permitBO
								.retrievePermit(((RPERequest) permitApp)
										.getPermitId());
						Permit extendedPermit = null;
						if (extendedPermitInfo == null
								|| (extendedPermit = extendedPermitInfo
										.getPermit()) == null) {
							messages.add(new ValidationMessage(
									"Application",
									"Could not locate a permit to be extended.",
									ValidationMessage.Severity.ERROR,
									buildAppProfileRef()));
						} else {

							// LSRD 2.3.3-PTI_PTO_PTIO-22-5
							//
							// STARS2 shall provide the following QA/QC check
							// for the RPE object at the DO/LAA
							// Approval step of the workflow. One of the checks
							// will examine the Begin
							// Installation or Modification date for each of the
							// EUs in the permit. If the
							// date exists, then STARS2 shall generate a warning
							// message with wording to the
							// effect "Installation or modification of one or
							// more emissions units in this permit
							// has already begun, a permit extension request is
							// not applicable in this case".
							//
							List<PermitEU> peus = extendedPermit.getEus();
							if (peus.isEmpty()) {
								messages.add(new ValidationMessage(
										"Permit",
										"Permit does not have any emission units.",
										ValidationMessage.Severity.ERROR,
										buildAppProfileRef()));
							} else {
*/
								/*
								 * 2089 The RPE was being issued for permit
								 * number 02-22810 which was issued in 11/2007.
								 * This error appeared for emissions unit P001.
								 * When I look at the current facility inventory
								 * for this facility/EU, the "Begin
								 * Installation/Modification Date:" is 2/1/1998.
								 * That warning is not valid because the permit
								 * issuance date is AFTER the date in the
								 * profile.
								 * 
								 * If the permit issuance date was BEFORE that
								 * date, the Warning would be valid and in fact
								 * should be an ERROR, not a warning.
								 */
	/*
								Facility facility = extendedPermitInfo
										.getCurrentFacility();
								for (PermitEU peu : peus) {
									EmissionUnit feu = facility
											.getMatchingEmissionUnit(peu
													.getCorrEpaEMUID());
									if (feu.getEuInstallDate() != null
											&& extendedPermit
													.getFinalIssueDate()
													.before(feu
															.getEuInstallDate())) {
										messages.add(new ValidationMessage(
												"Facility",
												"Installation or modification of one or "
														+ "more emissions units in this permit has "
														+ "already begun. A permit extension "
														+ "request is not applicable in this case.",
												ValidationMessage.Severity.ERROR,
												buildAppProfileRef(), feu
														.getEpaEmuId()));
										break;
									}
								}
							}

							// LSRD 2.3.3-PTI_PTO_PTIO-22-6:
							//
							// STARS2 shall provide the following QA/QC check at
							// the DO/LAA Approval step of
							// the workflow to determine if an RPE record with
							// the following characteristics
							// already exists:
							// -> Same facility id
							// -> Same referenced PTI/PTIO permit
							// -> Does not have a disposition of 'Denied'
							//
							// If such an RPE record exists, STARS2 shall test
							// the disposition attribute of the
							// RPE record. If it is equal to 'Granted', then
							// STARS2 shall generate a warning
							// message with wording to the effect "An extension
							// has already been granted for this
							// permit, a maximum of one extension can be
							// granted". If the disposition is equal to
							// 'Pending', then STARS2 shall generate a warning
							// message (warning only, not an error)
							// with wording to the effect
							// "There already exists a pending RPE record for this permit".
							//
							if (extendedPermit != null
									&& extendedPermit.getFacilityId() != null) {
								ApplicationSearchResult[] asr = applicationBO
										.retrieveApplicationSearchResults(null,
												null,
												extendedPermit.getFacilityId(),
												null, null, null,
												ApplicationTypeDef.RPE_REQUEST,
												null, false, null, null, null,
												true);
								if (asr.length > 1) {
									boolean hasPending = false;
									for (ApplicationSearchResult sr : asr) {
										Application anApp = applicationBO
												.retrieveApplication(sr
														.getApplicationId());
										if (anApp instanceof RPERequest) {
											if (((RPERequest) anApp)
													.getPermitId() != null
													&& ((RPERequest) anApp)
															.getPermitId()
															.equals(extendedPermit
																	.getPermitID())) {
												if (((RPERequest) anApp)
														.getDispositionFlag() == null
														&& !hasPending) {
													messages.add(new ValidationMessage(
															"Application",
															"There already exists a "
																	+ "pending RPE record for "
																	+ "this permit.",
															ValidationMessage.Severity.WARNING));
													hasPending = true;
												} else if (((RPERequest) anApp)
														.getDispositionFlag()
														.equals(RPERequest.DENIED)
														|| ((RPERequest) anApp)
																.getDispositionFlag()
																.equals(RPERequest.DEAD_ENDED)) {
													continue;
												} else if (((RPERequest) anApp)
														.getDispositionFlag()
														.equals(RPERequest.ISSUED)) {
													messages.add(new ValidationMessage(
															"Application",
															"An extension has already "
																	+ "been granted for this "
																	+ "permit. A maximum of one "
																	+ "extension can be granted.",
															ValidationMessage.Severity.WARNING));
												}
											} else {
												continue;
											}
										} else {
											messages.add(new ValidationMessage(
													"Application",
													"Application number "
															+ anApp.getApplicationNumber()
															+ " is not an RPERequest. Contact "
															+ "a system administrator.",
													ValidationMessage.Severity.ERROR));
										}
									}
								}
							}
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;
	}

	public static List<ValidationMessage> validateRPEPermitIssuanceCheckIn(
			Permit permit) {
		List<ValidationMessage> messages = validateRPEPermit(permit);

		try {
			boolean hasGenericIssuance = false;
			boolean hasIssuedDate = false;
			ApplicationService applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			GenericIssuanceService giBO = ServiceFactory.getInstance()
					.getGenericIssuanceService();
			for (Application permitApp : permit.getApplications()) {
				permitApp = applicationBO.retrieveApplication(permitApp
						.getApplicationID());
				if (permitApp instanceof RPERequest) {
					String disp = ((RPERequest) permitApp).getDispositionFlag();
					if (disp == null || !disp.equals(RPERequest.ISSUED)) {
						messages.add(new ValidationMessage("Application",
								"RPE disposition flag is not set to "
										+ "RPERequest.ISSUED.",
								ValidationMessage.Severity.ERROR,
								buildAppProfileRef()));
					}
					List<GenericIssuance> genIssuanceList = giBO
							.searchGenericIssuances(null,
									permitApp.getApplicationID(), null, null);
					for (GenericIssuance genIssuance : genIssuanceList) {
						if (genIssuance != null
								&& genIssuance.getIssuanceTypeCd() != null
								&& (genIssuance
										.getIssuanceTypeCd()
										.equals(GenericIssuanceTypeDef.TIME_EXTENSION_PTI) || genIssuance
										.getIssuanceTypeCd()
										.equals(GenericIssuanceTypeDef.TIME_EXTENSION_PTIO))) {
							hasGenericIssuance = true;
							if (genIssuance.getIssuanceDate() != null) {
								hasIssuedDate = true;
							}
						}
					}
				}
			}
			if (!hasGenericIssuance) {
				messages.add(new ValidationMessage("Application",
						"Unable to locate any final GenericIssuances. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef()));
			}
			if (!hasIssuedDate) {
				messages.add(new ValidationMessage("Application",
						"No issued date found. ",
						ValidationMessage.Severity.ERROR, buildAppProfileRef()));
			}
		} catch (Exception e) {
			logger.error("Cannot retrieve applications: " + e.getMessage(), e);
			messages.add(new ValidationMessage("Application",
					"Unable to retrieve applications. " + e.getMessage(),
					ValidationMessage.Severity.ERROR, buildAppProfileRef()));
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef());
				messages.add(0, vm);
			}
		}
		return messages;
	}
*/
	public static List<ValidationMessage> validatePublicNoticePackage(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		for (ValidationMessage vm : messages) {
			if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
					vm.setSeverity(ValidationMessage.Severity.INFO);
					infoMessages.add(vm);
			}
		}
		for (ValidationMessage vm : infoMessages) {
			messages.remove(vm);
		}
		
		// determine if permit is even suppose to publish Public Notice
		boolean publishPublicNotice = true;
		if (permit instanceof PTIOPermit) {
			PTIOPermit nsrPermit = (PTIOPermit) permit;
			String actionType = nsrPermit.getPermitActionType();
			if (!Utility.isNullOrEmpty(actionType)
					&& actionType.equals(PermitActionTypeDef.WAIVER)) {
				publishPublicNotice = false;
				messages.add(new ValidationMessage(
						"permitActionType",
						"Permit is being issued Waiver, cannot prepare Public Notice Package.",
						ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
			} else {
				boolean issueDraft = nsrPermit.isIssueDraft();
				if (!issueDraft) {
					publishPublicNotice = false;
					messages.add(new ValidationMessage(
							"issueDraft",
							"Public Notice is not needed, cannot prepare Public Notice Package.",
							ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));

				}
			}
		}

		if (publishPublicNotice) {
			// Check reason codes to see if reason code allows permit to be
			// issued
			// draft.
			boolean isSigMod = false;
			boolean isReopening = false;
			// boolean isRescind = false;
			boolean isAPA = false;
			boolean isMinorMod = false;
			boolean is502b10Change = false;

			for (String reason : permit.getPermitReasonCDs()) {
				if (reason.equals(PermitReasonsDef.SPM)) {
					isSigMod = true;
				} else if (reason.equals(PermitReasonsDef.REOPENING)) {
					isReopening = true;
				// } else if (reason.equals(PermitReasonsDef.RESCIND)) {
				//	isRescind = true;
				} else if (reason.equals(PermitReasonsDef.APA)) {
					isAPA = true;
				} else if (reason.equals(PermitReasonsDef.MPM)) {
					isMinorMod = true;
				} else if (reason.equals(PermitReasonsDef.CHANGE_502_B_10)) {
					is502b10Change = true;
				}
			}

			if (!isSigMod && !isReopening /* && !isRescind */) {
				// String unless = "unless it is also an Significant Permit Modification, a Reopening, or a Rescind.";
				String unless = "unless it is also an Significant Permit Modification, or a Reopening.";
				if (isAPA) {
					messages.add(new ValidationMessage("Permit",
							"This Administrative Permit Amendment may not publish a Public Notice "
									+ unless, ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				}
				if (isMinorMod) {
					messages.add(new ValidationMessage("Permit",
							"This Minor Permit Modification may not publish a Public Notice "
									+ unless, ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				}
				if (is502b10Change) {
					messages.add(new ValidationMessage("Permit",
							"This 502(b)(10) Change may not publish a Public Notice "
									+ unless, ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				}
			}

			PermitIssuance pi = permit.getDraftIssuance();

			if (!permit.getPermitType().equals(PermitTypeDef.TV_PTO) || (!isMinorMod && !is502b10Change && !isAPA)) {
				if (pi.getPublicCommentEndDate() == null) {
					messages.add(new ValidationMessage("endOfCommentPeriod",
							"The Comments Period End Date " + "must be entered.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}

				if (pi.getPublicNoticePublishDate() == null) {
					messages.add(new ValidationMessage("pd",
							"The Newspaper Publish Date " + "must be entered.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}

				if (IssuanceStatusDef.NOT_READY.equalsIgnoreCase(permit
						.getDraftIssuanceStatusCd())) {
					messages.add(new ValidationMessage("draftIssuanceStatus",
							"Draft Publication status is not ready.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}

				if (Utility.isNullOrEmpty(permit.getPublicNoticeNewspaperCd())) {
					messages.add(new ValidationMessage("publicNoticeNewspaperCd",
							"Public notice newspaper is not selected.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}
			}

			if (permit instanceof PTIOPermit) {
				PTIOPermit nsrPermit = (PTIOPermit) permit;
				if (permit.getDraftIssueDate() == null) {
					messages.add(new ValidationMessage("draftIssuanceStatus",
							"Tech Analysis date is not set.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}

				// the following check is not needed any more after implemeting NSR Billing feature
				/*
				if ((!nsrPermit.getPermitReasonCDs().contains(
						PermitReasonsDef.RENEWAL))
						&& (nsrPermit.getTotalAmount() <= 0.0)) {
					messages.add(new ValidationMessage("PermitFee",
							"Permit fee is less than or equal to zero.",
							ValidationMessage.Severity.WARNING,
							buildFeeSummaryRef(),permit.getPermitID()));
				}
				*/
				
				// check if initial invoice has been generated
				if(((PTIOPermit) permit).isBillable()) {
					boolean hasInitialInvoice = false;
					for(PermitDocument pDoc : permit.getDocuments()) {
						if(pDoc.getPermitDocTypeCD().equalsIgnoreCase(PermitDocTypeDef.INITIAL_INVOICE)) {
							hasInitialInvoice = true;
							break;
						}
					}
					if(!hasInitialInvoice) {
						messages.add(new ValidationMessage("publishDraftInitialInvoiceDocument",
								"Permit has no Inital Invoice document.",
								ValidationMessage.Severity.ERROR, buildFeeSummaryRef(), permit.getPermitID()));
					}
				}
			}
			
			// check documents for public notice
			boolean hasCompanyCoverLetterDraft = false;
			for (PermitDocument doc : permit.getDocuments()) {

				ValidationMessage[] vms = doc.validate();
				for (ValidationMessage vm : vms) {
					messages.add(vm);
				}
				if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.COMPANY_COVER_LETTER_DRAFT)) {
					hasCompanyCoverLetterDraft = true;
				}
			}
			
			if (!hasCompanyCoverLetterDraft && permit instanceof TVPermit && !isMinorMod && !is502b10Change && !isAPA) {
				messages.add(new ValidationMessage("publishDraftCompanyCoverLetter",
						"Permit has no Company Cover Letter Draft.",
						ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
			}
		}

		// check for public notice document for TV permit
		if(permit.getPermitType().equals(PermitTypeDef.TV_PTO)) {
			messages.addAll(validateMgrSupervisorReview(permit));
		}
		
		messages.addAll(infoMessages);

		// For TV permit, remove the following validation message if it is present
		// Permit contains an emission unit which cannot be found in an application Permit EU ID is XXXXXX. (TFS task 4647)
		if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO))
			messages = removeMsgFromValidationMessages(messages, "Permit contains an emission unit which cannot be found in an application");	
		
		return messages;
	}

	public static List<ValidationMessage> validatePreparePublicNoticePackage(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validatePublicNoticePackage(permit));

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		
		return messages;
	}

	public static List<ValidationMessage> validateReviewPublicNotice(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validatePublicNoticePackage(permit));

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}

		return messages;
	}

	public static List<ValidationMessage> validateFinalIssuance(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();
		ArrayList<ValidationMessage> fpMessages = new ArrayList<ValidationMessage>();

		for (ValidationMessage vm : messages) {
			if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
				vm.setSeverity(ValidationMessage.Severity.INFO);
				infoMessages.add(vm);
			}
		}
		for (ValidationMessage vm : infoMessages) {
			messages.remove(vm);
		}

		// Check reason codes to see if reason code requires previous issuance
		// type.
		boolean isSigMod = false;
		boolean isReopening = false;
		// boolean isRescind = false;
		boolean isMinorMod = false;
		boolean isInitial = false;
		boolean isRenewal = false;
		boolean is502 = false;
		boolean isAPA = false;

		for (String reason : permit.getPermitReasonCDs()) {
			if (reason.equals(PermitReasonsDef.SPM)) {
				isSigMod = true;
			} else if (reason.equals(PermitReasonsDef.REOPENING)) {
				isReopening = true;
			// } else if (reason.equals(PermitReasonsDef.RESCIND)) {
			//	isRescind = true;
			} else if (reason.equals(PermitReasonsDef.MPM)) {
				isMinorMod = true;
			} else if (reason.equals(PermitReasonsDef.INITIAL)) {
				isInitial = true;
			} else if (reason.equals(PermitReasonsDef.RENEWAL)) {
				isRenewal = true;
			} else if (reason.equals(PermitReasonsDef.CHANGE_502_B_10)) {
				is502 = true;	
			} else if (reason.equals(PermitReasonsDef.APA)) {
				isAPA = true;					
			}
		}

		if (permit instanceof TVPermit) {

			// Check for previous PP issuance.
			if ((isMinorMod || isSigMod || isReopening // || isRescind
					|| isInitial || isRenewal)
					&& (((TVPermit) permit).getUsepaOutcomeCd() != null && !((TVPermit) permit)
							.getUsepaOutcomeCd().equals(USEPAOutcomeDef.WAIVED))) {

				PermitIssuance pi = ((TVPermit) permit).getPpIssuance();
				if (pi != null) {
					if (pi.getIssuanceStatusCd() == null
							|| !pi.getIssuanceStatusCd().equals(
									IssuanceStatusDef.ISSUED)) {
						messages.add(new ValidationMessage("ppIssuance",
								"Permit must first be issued PP.",
								ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
					}
				} else {
					messages.add(new ValidationMessage("ppIssuance",
							"Permit must first be issued PP.",
							ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
				}
			}

		} else if (permit.getPermitType().equals(PermitTypeDef.NSR)
				//|| permit.getPermitType().equals(PermitTypeDef.TVPTI)
				) {

			boolean publishPublicNotice = true;
			if (permit instanceof PTIOPermit) {
				PTIOPermit nsrPermit = (PTIOPermit) permit;
				boolean issueDraft = nsrPermit.isIssueDraft();
				String actionType = nsrPermit.getPermitActionType();

				if (!issueDraft) {
					publishPublicNotice = false;
				}

				if (!Utility.isNullOrEmpty(actionType)
						&& actionType.equals(PermitActionTypeDef.WAIVER)) {
					publishPublicNotice = false;
				}
			}

			// Check for previous draft issuance.
			if (publishPublicNotice) {
				PermitIssuance pi = permit.getDraftIssuance();
				if (pi != null) {
					if (pi.getIssuanceStatusCd() == null
							|| !pi.getIssuanceStatusCd().equals(
									IssuanceStatusDef.ISSUED)) {
						messages.add(new ValidationMessage("draftIssuance",
								"Permit must first publish Public Notice.",
								ValidationMessage.Severity.ERROR,
								buildDraftRef(),permit.getPermitID()));
					}
					Timestamp endOfCmnt = pi.getPublicCommentEndDate();
					Timestamp now = new Timestamp(Calendar.getInstance()
							.getTimeInMillis());
					if (endOfCmnt == null) {
						messages.add(new ValidationMessage(
								"endOfCommentPeriod",
								"Permit has no end of draft comment period.",
								ValidationMessage.Severity.ERROR,
								buildDraftRef(),permit.getPermitID()));
					} else if (endOfCmnt.after(now)) {
						messages.add(new ValidationMessage(
								"endOfCommentPeriod",
								"End of draft comment period has not been reached.",
								ValidationMessage.Severity.ERROR,
								buildDraftRef(),permit.getPermitID()));
					}
				} else {
					messages.add(new ValidationMessage("draftIssuance",
							"Permit must first publish Public Notice.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}
			}

		}

		// following check not required after implementing NSR Billing feature
		// check that there is a billing address if there are fees.
//		PTIOPermit ptioPermit = null;
//		if (permit instanceof PTIOPermit) {
//			ptioPermit = (PTIOPermit) permit;
//			if (ptioPermit.getTotalAmount() != null
//					&& ptioPermit.getTotalAmount() > 0d) {
//				if (ptioPermit.getFacilityId() != null) {
//					Facility f = new Facility();
//					f.setFacilityId(ptioPermit.getFacilityId());
//					f.setFpId(ptioPermit.getFpId());
//					// Get all the contacts for the facility
//					try {
//						FacilityService facilityBO = null;
//						facilityBO = ServiceFactory.getInstance()
//								.getFacilityService();
//						f.setAllContacts(facilityBO.retrieveFacilityContacts(f
//								.getFacilityId()));
//						if (f.getBillingContact() == null) {
//							fpMessages.add(new ValidationMessage(
//									"Facility",
//									"There is no active billing contact for the facility.",
//									ValidationMessage.Severity.ERROR,
//									buildContactsRef(f.getFacilityId()),permit.getPermitID()));
//						}
//					} catch (ServiceFactoryException sfe) {
//						logger.error("getFacilityService() failed for permit "
//								+ permit.getPermitNumber(), sfe);
//						messages.add(new ValidationMessage("Facility",
//								"Failed to retrieve contacts for facility: "
//										+ sfe.getMessage(),
//								ValidationMessage.Severity.ERROR,
//								buildAppProfileRef(),permit.getPermitID()));
//					} catch (RemoteException re) {
//						logger.error("retrieveFacilityAddresses() failed "
//								+ permit.getPermitNumber(), re);
//						messages.add(new ValidationMessage("Facility",
//								"Failed to retrieve contacts for facility: "
//										+ re.getMessage(),
//								ValidationMessage.Severity.ERROR,
//								buildAppProfileRef(),permit.getPermitID()));
//					}
//					
//					messages.addAll(FacilityValidation.addNavToValidationMessages(
//							f.getFpId(), f.getFacilityId(),
//							fpMessages));
//				}
//			}
//		}

		if (IssuanceStatusDef.NOT_READY.equalsIgnoreCase(permit
				.getFinalIssuanceStatusCd())) {
			messages.add(new ValidationMessage("finalIssuanceStatus",
					"Final Issuance status is not ready.",
					ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
		}

		if (permit.getFinalIssueDate() == null) {
			messages.add(new ValidationMessage("finalIssuanceStatus",
					"Final Issuance Date is not set.",
					ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
		}

		boolean needsCommentTransmittalDoc = false;
		if (permit instanceof TVPermit) {
			if (permit.getExpirationDate() == null) {
				messages.add(new ValidationMessage("expDate",
						"Expiration Date must be set.",
						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
			}

			if (permit.getEffectiveDate() == null) {
				messages.add(new ValidationMessage("effDate",
						"Effective Date must be set.",
						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
			}

			if (!permit.getPermitType().equals(PermitTypeDef.TV_PTO) || (!is502 && !isAPA)) {
				Timestamp now = new Timestamp(Calendar.getInstance()
						.getTimeInMillis());
				Timestamp usepaReviewEndDate = ((TVPermit) permit)
						.getUsepaCompleteDate();
				if (usepaReviewEndDate == null) {
					messages.add(new ValidationMessage(
							"usepaReviewEndDate",
							"Proposed Permit USEPA Review End Date period must be entered.",
							ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
				} else {
					if (usepaReviewEndDate.after(now)) {
						messages.add(new ValidationMessage(
								"usepaReviewEndDate",
								"End of Proposed Permit USEPA Review End Date period has not been reached.",
								ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
					}
				}
			}
		} else if (permit instanceof PTIOPermit) {
			PTIOPermit nsrPermit = (PTIOPermit) permit;
			// skip this check for wavier
			if(nsrPermit.isActionTypePermit()) {
				PermitIssuance pi = permit.getDraftIssuance();
				if (pi.getNewspaperAffidavitDate() == null) {
					messages.add(new ValidationMessage("newspaperAffidavitDate",
							"Newspaper Affidavit Date is not set.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}
			}

			// below check not required after implementing NSR Billing feature
//			String invoicePaid = nsrPermit.getInvoicePaid();
//			if (!Utility.isNullOrEmpty(invoicePaid)) {
//				if (invoicePaid.equals(NSRInvoicePaidDef.NO)) {
//					messages.add(new ValidationMessage(
//							"invoicePaid",
//							"Cannot issue Final permit until invoice has been paid.",
//							ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
//				}
//			} else {
//				messages.add(new ValidationMessage("invoicePaid",
//						"Invoice Paid must be answered.",
//						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
//			}

			if (nsrPermit.isCommentTransmittalRequired()) {
				String commentTransmittalLettersSent = nsrPermit
						.getCommentTransmittalLettersSentFlag();
				if (commentTransmittalLettersSent != null) {
					if (commentTransmittalLettersSent.equalsIgnoreCase("Y")) {
						needsCommentTransmittalDoc = true;
					} else {
						messages.add(new ValidationMessage(
								"commentTransmittalLettersSent",
								"Comment Transmittal Letters must be sent in order to issue Final.",
								ValidationMessage.Severity.ERROR,
								buildFinalRef(),permit.getPermitID()));
					}
				} else {
					messages.add(new ValidationMessage(
							"commentTransmittalLettersSent",
							"Comment Transmittal Letters Sent must be answered.",
							ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
				}
			}
			
			// check if previous invoice(s) have been paid in full
			if(nsrPermit.isBillable()) {
				boolean hascheckNumber = false;
				boolean zeroBalance = false;
				boolean hasTxnAmt = false;
				
				BigDecimal totalBalance = nsrPermit.getCurrentTotal();
				if(null != totalBalance) { 
					if(totalBalance.compareTo(BigDecimal.ZERO) <= 0) {
						zeroBalance = true;
					}
				}
				
				if(zeroBalance) {
					List<PermitChargePayment> pcpList = nsrPermit.getPermitChargePaymentList();
					for(PermitChargePayment apcp : pcpList) {
						if(apcp.getTransactionType().equalsIgnoreCase(NSRBillingChargePaymentTypeDef.PAYMENT)
							&& (null != apcp.getTransactionAmount() && apcp.getTransactionAmount() > 0)) {
							hasTxnAmt = true;
							if(!Utility.isNullOrEmpty(apcp.getCheckNumber())) {
								hascheckNumber = true;
								break;
							}
						}	
					}
				}
			
				// Must be zero balance. 
				// And If there is a non-zero payment amount, the amount must have an associated check number. 
				if(!((zeroBalance && !hasTxnAmt)
					|| (zeroBalance && hasTxnAmt && hascheckNumber))) {
					messages.add(new ValidationMessage(
							"initialInvoiceNotPaid",
							"Must have a check number and zero balance.",
							ValidationMessage.Severity.ERROR, buildFeeSummaryRef(), permit.getPermitID()));
				}
			}
		}

		// Check the permit's documents.
		boolean hasNewsPaperAffadavit = false;
		boolean hasNSRIssuanceDoc = false;
		boolean hasFinalTVPermitDoc = false;
		boolean hasFinalStatementOfBasis = false;
		for (PermitDocument doc : permit.getDocuments()) {

			ValidationMessage[] vms = doc.validate();
			for (ValidationMessage vm : vms) {
				messages.add(vm);
			}
			if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.NEWS_PAPER_AFFADAVIT)) {
				hasNewsPaperAffadavit = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.NSR_FINAL_PERMIT_WAIVER_PACKAGE) ) {
				hasNSRIssuanceDoc = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.FINAL_TV_PERMIT_DOCUMENT)) {
				hasFinalTVPermitDoc = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.FINAL_STATEMENT_BASIS)
			) {
				hasFinalStatementOfBasis = true;
			}
		}
		if (!hasNewsPaperAffadavit && permit instanceof PTIOPermit) {
			// skip this check for waiver
			if(((PTIOPermit)permit).isActionTypePermit()) {
				messages.add(new ValidationMessage("finalIssueNewsPaperAffadavit",
						"Permit has no Newspaper Affidavit.",
						ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
			}
		}

		if (!hasNSRIssuanceDoc && permit instanceof PTIOPermit) {
			messages.add(new ValidationMessage("finalIssuanceDocument",
					"Permit has no NSR Final Permit/Waiver Package.",
					ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
		}
		
		if (!hasFinalTVPermitDoc && permit instanceof TVPermit
				/* && !permit.getPermitReasonCDs().contains(PermitReasonsDef.RESCIND) */) {
			messages.add(new ValidationMessage("finalTVPermitDocument",
					"Permit has no Final TV Permit Document.",
					ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
		}
		
		if (!hasFinalStatementOfBasis && permit instanceof TVPermit 
				&& (!permit.getPermitType().equals(PermitTypeDef.TV_PTO) || (!is502 && !isAPA))) {
			messages.add(new ValidationMessage("finalStatementOfBasis",
					"Permit has no Final Statement of Basis.",
					ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
		}
		
		messages.addAll(infoMessages);
		
		// For TV permit, remove the following validation message if it is present
		// Permit contains an emission unit which cannot be found in an application Permit EU ID is XXXXXX. (TFS task 4647)
		if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO))
			messages = removeMsgFromValidationMessages(messages, "Permit contains an emission unit which cannot be found in an application");

		return messages;
	}

	public static List<ValidationMessage> validatePrepareFinal(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateFinalIssuance(permit));

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}

		return messages;
	}

	public static List<ValidationMessage> validateCompletenessReview(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateReceiptLetter(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		for (ValidationMessage vm : messages) {
			if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
				vm.setSeverity(ValidationMessage.Severity.INFO);
				infoMessages.add(vm);
			}
		}
		for (ValidationMessage vm : infoMessages) {
			messages.remove(vm);
		}
		// Check the permit's documents.
		boolean hasCompleteNessLetter = false;
		boolean hasEPAReceiptNotification = false;
		boolean hasFLMReceiptNotification = false;
		for (PermitDocument doc : permit.getDocuments()) {

			ValidationMessage[] vms = doc.validate();
			for (ValidationMessage vm : vms) {
				messages.add(vm);
			}
			if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.NSR_COMPLETENESS_LETTER) || doc.getPermitDocTypeCD().equals(PermitDocTypeDef.TV_COMPLETENESS_LETTER)) {
				hasCompleteNessLetter = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.EPA_RECEIPT_NOTIFICATION)) {
				hasEPAReceiptNotification = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.FLM_RECEIPT_NOTIFICATION)) {
				hasFLMReceiptNotification = true;
			}
		}

		if (permit instanceof PTIOPermit) {
			if ((((PTIOPermit) permit).getSubjectToPSD() == null
					|| (((PTIOPermit) permit).getSubjectToPSD()
							.equalsIgnoreCase("N")))
					&& (((PTIOPermit) permit).getSubjectToNANSR() == null
					|| ((PTIOPermit) permit).getSubjectToNANSR()
							.equalsIgnoreCase("N"))) {
				if (!hasEPAReceiptNotification)
					hasEPAReceiptNotification = true;
			}
			if (((PTIOPermit) permit).getSubjectToPSD() == null
					|| ((PTIOPermit) permit).getSubjectToPSD()
							.equalsIgnoreCase("N")) {
				if (!hasFLMReceiptNotification)
					hasFLMReceiptNotification = true;
			}

			if (((PTIOPermit) permit).getModelingRequired() == null
					|| ((PTIOPermit) permit).getModelingRequired()
							.equalsIgnoreCase("")) {
				messages.add(new ValidationMessage("modelingRequired",
						"Modeling is not set.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}

			if (((PTIOPermit) permit).getPermitActionType() == null
					|| ((PTIOPermit) permit).getPermitActionType()
							.equalsIgnoreCase("")) {
				messages.add(new ValidationMessage("permitActionType",
						"Type of Action is not set.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}

			if (((PTIOPermit) permit).getPermitActionType() != null
					&& ((PTIOPermit) permit).isActionTypePermit()) {
				if (((PTIOPermit) permit).getSubjectToPSD() == null
						|| ((PTIOPermit) permit).getSubjectToPSD()
								.equalsIgnoreCase("")) {
					messages.add(new ValidationMessage("subjectToPsd",
							"Subject to PSD is not set.",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				}
				if (((PTIOPermit) permit).getSubjectToNANSR() == null
						|| ((PTIOPermit) permit).getSubjectToNANSR()
								.equalsIgnoreCase("")) {
					messages.add(new ValidationMessage("subjectToNANSR",
							"Subject to Non-Attainment NSR is not set.",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				}
			}
			if (((PTIOPermit) permit).getOtherTypeOfDemonstrationReq() == null
					|| ((PTIOPermit) permit).getOtherTypeOfDemonstrationReq()
							.equalsIgnoreCase("")) {
				messages.add(new ValidationMessage(
						"otherTypeOfDemonstrationReq",
						"Other Type of Demonstration is not set.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}

			if (!hasEPAReceiptNotification) {
				messages.add(new ValidationMessage(
						"completeEPAReceiptNotification",
						"Permit has no EPA Receipt Notification.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}
			if (!hasFLMReceiptNotification) {
				messages.add(new ValidationMessage(
						"completeFLMReceiptNotification",
						"Permit has no FLM Receipt Notification.",
						ValidationMessage.Severity.ERROR,
						buildDraftRef(),permit.getPermitID()));
			}

		}
		if (!hasCompleteNessLetter 
				&& (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
				|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.REOPENING)
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
			messages.add(new ValidationMessage("completeNessLetter",
					"Permit has no Completeness Letter.",
					ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
		}

		return messages;
	}

	public static List<ValidationMessage> validateTechReviews(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		// ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		// for (ValidationMessage vm : messages) {
		// if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
		// vm.setSeverity(ValidationMessage.Severity.INFO);
		// infoMessages.add(vm);
		// }
		// }
		// for (ValidationMessage vm : infoMessages) {
		// messages.remove(vm);
		// }
		
		return messages;
	}

	public static List<ValidationMessage> validateTechReview(Permit permit) {
	
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		
		// If any the the applications for this permit are identified as Known
		// Incomplete, we should not be here.
		messages = validateKnownIncompleteNSRApp(permit);
		if (messages.size() > 0) {
			return messages;
		}
		messages = new ArrayList<ValidationMessage>(
				validateCompletenessReview(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		// for (ValidationMessage vm : messages) {
		// if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
		// vm.setSeverity(ValidationMessage.Severity.INFO);
		// infoMessages.add(vm);
		// }
		// }
		// for (ValidationMessage vm : infoMessages) {
		// messages.remove(vm);
		// }

		if (permit instanceof PTIOPermit) {
			if (((PTIOPermit) permit).getModelingRequired() != null
					&& ((PTIOPermit) permit).getModelingRequired().equals("Y")) {
				if (((PTIOPermit) permit).getModelingCompletedDate() == null) {
					messages.add(new ValidationMessage("modelingCompletedDate",
							"Modeling Completed is not set.",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				}
			}
			boolean hasEPADraftNotification = false;
			boolean isEPADraftNotificationNeeded = false; 
			boolean hasFLMDraftNotification = false;
			boolean hasAnalysisDocument = false;
			boolean hasPermitDocument = false;
			for (PermitDocument doc : permit.getDocuments()) {

				ValidationMessage[] vms = doc.validate();
				for (ValidationMessage vm : vms) {
					messages.add(vm);
				}
				if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.EPA_DRAFT_NOTIFICATION)) {
					hasEPADraftNotification = true;
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.FLM_DRAFT_NOTIFICATION)) {
					hasFLMDraftNotification = true;
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.ANALYSIS_DOCUMENT)) {
					hasAnalysisDocument = true;
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.PERMIT_DOCUMENT)) {
					hasPermitDocument = true;
				}
			}
			if (((((PTIOPermit) permit).getSubjectToPSD() != null && ((PTIOPermit) permit).getSubjectToPSD().equalsIgnoreCase("Y"))
					|| (((PTIOPermit) permit).getSubjectToNANSR() != null && ((PTIOPermit) permit).getSubjectToNANSR().equalsIgnoreCase("Y"))))
				isEPADraftNotificationNeeded = true;
				
			
			if (((PTIOPermit) permit).getSubjectToPSD() == null
					|| ((PTIOPermit) permit).getSubjectToPSD()
							.equalsIgnoreCase("N")) {
				if (!hasFLMDraftNotification)
					hasFLMDraftNotification = true;
			}

			if (((PTIOPermit) permit).getPermitActionType() == null
					|| ((PTIOPermit) permit).getPermitActionType()
							.equalsIgnoreCase("")) {
				messages.add(new ValidationMessage("permitActionType",
						"Type of Action is not set.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			} else if (!((PTIOPermit) permit).isActionTypePermit()) {
				if (!hasAnalysisDocument)
					hasAnalysisDocument = true;
			}
			if (isEPADraftNotificationNeeded && !hasEPADraftNotification) {
				messages.add(new ValidationMessage("techEPADraftNotification",
						"Permit has no EPA Draft Notification.",
						ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
			}
			if (!hasFLMDraftNotification) {
				messages.add(new ValidationMessage("techFLMDraftNotification",
						"Permit has no FLM Draft Notification.",
						ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
			}
			if (!hasAnalysisDocument) {
				messages.add(new ValidationMessage("techAnalysisDocument",
						"Permit has no Analysis Document.",
						ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
			}
			if(!hasPermitDocument && (!((PTIOPermit)permit).isActionTypePermit() || !((PTIOPermit)permit).isIssueDraft())) {
				messages.add(new ValidationMessage("techPermitDocument",
						"Permit has no Permit/Waiver Document.",
						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
			}
			if ("Y".equals(((PTIOPermit)permit).getOtherTypeOfDemonstrationReq()) &&
					!((PTIOPermit)permit).getOffsetInformationVerified()) {
				messages.add(new ValidationMessage("offsetInformationVerified",
						"Demonstration/Offsets not verified.",
						ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
			}
		} else if (permit instanceof TVPermit) {
			if (!permit.isUpdateFacilityDtlPTETableComments()
					&& (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
							|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
									&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
				messages.add(new ValidationMessage(
						"updateFacilityDtlPTETable",
						"Check/Update Facility Inventory PTE Table is not set.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}

			boolean hasstatementOfBasisDoc = false;
			boolean hasDraftTVPermitDoc = false;
			for (PermitDocument doc : permit.getDocuments()) {

				ValidationMessage[] vms = doc.validate();
				for (ValidationMessage vm : vms) {
					messages.add(vm);
				}
				if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.DRAFT_STATEMENT_BASIS)) {
					hasstatementOfBasisDoc = true;
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.DRAFT_TV_PERMIT_DOCUMENT)) {
					hasDraftTVPermitDoc = true;
				}
			}

			if (!hasDraftTVPermitDoc /* && !permit.getPermitReasonCDs().contains(PermitReasonsDef.RESCIND) */) {
				messages.add(new ValidationMessage("draftPermitDoc",
						"Permit has no Draft TV Permit Document.",
						ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
			}
			if (!hasstatementOfBasisDoc
					&& (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
							|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
									&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
				messages.add(new ValidationMessage("statementOfBasisDoc",
						"Permit has no Draft Statement of Basis Document.",
						ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
			}

		}
		return messages;
	}

	public static List<ValidationMessage> validateTechReview2(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateTechReview(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		boolean hasEPATransmittalNotification = false;
		boolean isEPATransmittalNotificationNeeded = false;
		boolean hasFLMTransmittalNotification = false;
		boolean hasCountyClerkTransmittalLetter = false;
		boolean hasCommentsDocument = false;
		boolean hasResponseToCommentsDocument = false;
		boolean hasPermitDocument = false;
		for (PermitDocument doc : permit.getDocuments()) {

			ValidationMessage[] vms = doc.validate();
			for (ValidationMessage vm : vms) {
				messages.add(vm);
			}
			if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.EPA_TRANSMITTAL_LETTER)) {
				hasEPATransmittalNotification = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.FLM_TRANSMITTAL_LETTER)) {
				hasFLMTransmittalNotification = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.COUNTY_CLERK_TRANSMITTAL_LETTER)) {
				hasCountyClerkTransmittalLetter = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.PERMIT_DOCUMENT)) {
				hasPermitDocument = true;
			} else if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.NSR_RESPONSE_TO_COMMENTS) || doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.TV_RESPONSE_TO_COMMENTS)) {
				hasResponseToCommentsDocument = true;
			} else if (doc.getPermitDocTypeCD().equals(PermitDocTypeDef.TV_COMMENTS)
					|| doc.getPermitDocTypeCD().equals(PermitDocTypeDef.NSR_COMMENTS)) {
				hasCommentsDocument = true;
			}
		}
		// skip this check for NSR waiver
		if(permit instanceof TVPermit
				|| (permit instanceof PTIOPermit &&
						((PTIOPermit)permit).isActionTypePermit())) {
			if (permit.getReceivedComments() == null && (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
					|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
							&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
							&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
				messages.add(new ValidationMessage("receivedComments",
						"Received Comments is not set.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			} else if (permit.getReceivedComments() != null) {
				if (permit.getReceivedComments().equalsIgnoreCase(PermitReceivedCommentsDef.NO)) {
					if (!hasResponseToCommentsDocument)
					hasResponseToCommentsDocument = true;
					
					if (!hasCommentsDocument)
						hasCommentsDocument = true;
				}
				
				if (!hasResponseToCommentsDocument && permit instanceof TVPermit 
						&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
						&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
						&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)) {
					messages.add(new ValidationMessage(
							"techResponseToCommentsDocuments",
							"Permit has no Response to Comments Document.",
							ValidationMessage.Severity.ERROR,
							buildPpRef(),permit.getPermitID()));
				}
				
				if (!hasResponseToCommentsDocument && permit instanceof PTIOPermit) {
					messages.add(new ValidationMessage(
							"techResponseToCommentsDocuments",
							"Permit has no Response to Comments Document.",
							ValidationMessage.Severity.ERROR,
							buildFinalRef(),permit.getPermitID()));
				}
				
				if (!hasCommentsDocument && permit instanceof TVPermit) {
					messages.add(new ValidationMessage("techCommentsDocuments",
							"Permit has no Comments Document.",
							ValidationMessage.Severity.ERROR,
							buildPpRef(),permit.getPermitID()));
				}

				if (!hasCommentsDocument && permit instanceof PTIOPermit) {
					messages.add(new ValidationMessage("techCommentsDocuments",
							"Permit has no Comments Document.",
							ValidationMessage.Severity.ERROR,
							buildFinalRef(),permit.getPermitID()));
				}

			}
		}

		if (permit instanceof PTIOPermit) {
			// skip this check for waiver
			if(((PTIOPermit)permit).isActionTypePermit()) {
				if (!permit.isUpdateFacilityDtlPTETableComments()) {
					messages.add(new ValidationMessage("updateFacilityDtlPTETable",
							"Check/Update Facility Inventory PTE Table is not set.",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID()));
				}
			}	
			if (((PTIOPermit) permit).getModelingRequired() != null
					&& ((PTIOPermit) permit).getModelingRequired().equals("Y")) {
				if (((PTIOPermit) permit).getModelingCompletedDate() == null) {
					messages.add(new ValidationMessage("modelingCompletedDate",
							"Modeling Completed is not set.",
							ValidationMessage.Severity.ERROR,
							buildPermitProfileRef(),permit.getPermitID())); 
				}
			}
			
			if (((((PTIOPermit) permit).getSubjectToPSD() != null && ((PTIOPermit) permit).getSubjectToPSD().equalsIgnoreCase("Y"))
					|| (((PTIOPermit) permit).getSubjectToNANSR() != null && ((PTIOPermit) permit).getSubjectToNANSR().equalsIgnoreCase("Y"))))
				isEPATransmittalNotificationNeeded = true;
			
			if (((PTIOPermit) permit).getSubjectToPSD() == null
					|| ((PTIOPermit) permit).getSubjectToPSD()
							.equalsIgnoreCase("N")) {
				if (!hasFLMTransmittalNotification)
					hasFLMTransmittalNotification = true;

				if (!hasCountyClerkTransmittalLetter)
					hasCountyClerkTransmittalLetter = true;

			}
			if (isEPATransmittalNotificationNeeded && !hasEPATransmittalNotification) {
				messages.add(new ValidationMessage("techEPATransmittalLetter",
						"Permit has no EPA Transmittal Letter.",
						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
			}
			if (!hasFLMTransmittalNotification) {
				messages.add(new ValidationMessage("techFLMTransmittalLetter",
						"Permit has no FLM Transmittal Letter.",
						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
			}
			if (!hasCountyClerkTransmittalLetter) {
				messages.add(new ValidationMessage(
						"techCountyClerkTransmittalLetter",
						"Permit has no County Clerk Transmittal Letter.",
						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
			}
			if (!hasPermitDocument && ((PTIOPermit)permit).isActionTypePermit() && ((PTIOPermit)permit).isIssueDraft()) {
				messages.add(new ValidationMessage("techPermitDocument",
						"Permit has no Permit/Waiver Document.",
						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
			}

		}
		
		return messages;
	}

	// ReceiptLetter
	private static List<ValidationMessage> validateReceiptLetter(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
	
		if (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
				|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.REOPENING)
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA))) {
			boolean isReceiptLetterAttached = false;

			if (!permit.isReceiptLetterSent()) {
				messages.add(new ValidationMessage("Documents",
						"The permit's Receipt Letter sent indicator is not set.",
						ValidationMessage.Severity.ERROR, buildPermitProfileRef()));
			}

			// Check if the receipt letter is attached
			for (PermitDocument permitDoc : permit.getDocuments()) {
				if (permitDoc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.NSR_RECEIPT_LETTER) || permitDoc.getPermitDocTypeCD().equals(PermitDocTypeDef.TV_RECEIPT_LETTER)) {
					isReceiptLetterAttached = true;
					break;
				}
			}
			if (!isReceiptLetterAttached) {
				messages.add(new ValidationMessage("Documents",
						"Permit has no Receipt Letter.",
						ValidationMessage.Severity.ERROR, buildPermitProfileRef(),permit.getPermitID()));
			}
		}

		return messages;
	}

	private static List<ValidationMessage> validateInactiveWithdrawn(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateCompletenessReview(permit));

		boolean isInactiveWithdrawnDocAttached = false;
		
		if(!permit.getPermitGlobalStatusCD().equals(PermitGlobalStatusDef.DENIAL_PENDING)) {
			messages.add(new ValidationMessage("Permit",
					"Permit is not marked for Withdrawal.",
					ValidationMessage.Severity.ERROR,
					buildPermitProfileRef(),permit.getPermitID()));
		}

		if (permit instanceof PTIOPermit) {

			for (PermitDocument permitDoc : permit.getDocuments()) {
				if (permitDoc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.INACTIVE_WITHDRAWN_DOCUMENT)) {
					isInactiveWithdrawnDocAttached = true;
					break;
				}
			}

			if (!isInactiveWithdrawnDocAttached) {
				messages.add(new ValidationMessage("Documents",
						"Inactive/Withdrawn Document is not attached.",
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}

		}

		return messages;
	}

	// Prepare Inactive/Withdrawn
	private static List<ValidationMessage> validatePrepareInactiveWithdrawn(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateInactiveWithdrawn(permit));

		return messages;
	}

	// Complete Proposed Permit
	private static List<ValidationMessage> validateCompleteProposedPermit(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(validateBasicPermit(permit));

		if (permit instanceof TVPermit 
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)) {

			Timestamp usepaReceivedPermitDate = ((TVPermit) permit)
					.getUsepaReceivedPermitDate();
			if (usepaReceivedPermitDate == null) {
				messages.add(new ValidationMessage("usepaReceivedPermitDate",
						"The Date USEPA Received Permit must be entered.",
						ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
			}
			
			Timestamp usepaCompleteDate = ((TVPermit) permit)
					.getUsepaCompleteDate();
			if (usepaCompleteDate == null) {
				messages.add(new ValidationMessage("usepaCompleteDate",
						"The USEPA Review End Date must be entered.",
						ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
			}

			if (!IssuanceStatusDef.ISSUED.equalsIgnoreCase(((TVPermit) permit).getPpIssuanceStatusCd())) {
				messages.add(new ValidationMessage("ppIssuanceStatus",
						"Proposed Permit Publication status is not published.",
						ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
			}
				 
		}
		
		// For TV permit, remove the following validation message if it is present
		// Permit contains an emission unit which cannot be found in an application Permit EU ID is XXXXXX. (TFS task 4647)
		if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO))
			messages = removeMsgFromValidationMessages(messages, "Permit contains an emission unit which cannot be found in an application");

		return messages;
	}

	private static List<ValidationMessage> validateIssueInactiveWithdrawn(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		if(!permit.getPermitGlobalStatusCD().equals(PermitGlobalStatusDef.ISSUED_DENIAL)) {
				messages.add(new ValidationMessage("Permit",
					"Permit has not been issued withdrawal.",
					ValidationMessage.Severity.ERROR,
					buildPermitProfileRef(),permit.getPermitID()));
		}
				
		return messages;
	}

	private static List<ValidationMessage> validateSupervisorReview(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateInactiveWithdrawn(permit));

		return messages;
	}

	private static List<ValidationMessage> validateIssueFinal(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateFinalIssuance(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		for (ValidationMessage vm : messages) {
			if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
				vm.setSeverity(ValidationMessage.Severity.INFO);
				infoMessages.add(vm);
			}
		}
		for (ValidationMessage vm : infoMessages) {
			messages.remove(vm);
		}

		if (!IssuanceStatusDef.ISSUED.equalsIgnoreCase(permit
				.getFinalIssuanceStatusCd()))
			messages.add(new ValidationMessage("finalIssuanceStatus",
					"Final Issuance status is not Issued.",
					ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
		if (permit instanceof PTIOPermit) {
			if (((PTIOPermit) permit).getPermitSentOutDate() == null) {
				messages.add(new ValidationMessage("permitSentOutDate",
						"Permit Sent Out Date is not set.",
						ValidationMessage.Severity.ERROR,
						buildFinalRef(),permit.getPermitID()));
			}
		} else if (permit instanceof TVPermit) {
			boolean hasCompanyCoverLetter = false;
			boolean hasNewsPaperAffadavit = false;
			for (PermitDocument doc : permit.getDocuments()) {

				ValidationMessage[] vms = doc.validate();
				for (ValidationMessage vm : vms) {
					messages.add(vm);
				}
				if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.COMPANY_COVER_LETTER_FINAL)) {
					hasCompanyCoverLetter = true;
				} else if (doc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.NEWS_PAPER_AFFADAVIT)) {
					hasNewsPaperAffadavit = true;
				}
			}
			
			if (!hasCompanyCoverLetter) {
				messages.add(new ValidationMessage("finalIssueCompanyCoverLetter",
						"Permit has no Company Cover Letter Final.",
						ValidationMessage.Severity.ERROR, buildFinalRef(),permit.getPermitID()));
			}
			if (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
					|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
							&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
							&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA))) {
				if (!hasNewsPaperAffadavit) {
					messages.add(new ValidationMessage("finalIssueNewsPaperAffadavit",
							"Permit has no newspaper affidavit.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}

				PermitIssuance pi = permit.getDraftIssuance();
				if (pi.getNewspaperAffidavitDate() == null) {
					messages.add(new ValidationMessage("newspaperAffidavitDate",
							"Newspaper Affidavit Date is not set.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}
			}
		}

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		
		return messages;
	}

	private static List<ValidationMessage> validateWDEQDirectorForReview(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));

		// For TV permit, remove the following validation message if it is present
		// Permit contains an emission unit which cannot be found in an application Permit EU ID is XXXXXX. (TFS task 4647)
		if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO))
			messages = removeMsgFromValidationMessages(messages, "Permit contains an emission unit which cannot be found in an application");
		
		return messages;
	}

	private static List<ValidationMessage> validateAQDAdministratorForReview(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		
		// For TV permit, remove the following validation message if it is present
		// Permit contains an emission unit which cannot be found in an application Permit EU ID is XXXXXX. (TFS task 4647)
		if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO))
			messages = removeMsgFromValidationMessages(messages, "Permit contains an emission unit which cannot be found in an application");

		return messages;
	}

	private static List<ValidationMessage> validateLoopTechReviewDraftPermitWaiver(
			Permit permit) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		return messages;
	}

	private static List<ValidationMessage> validatePreparePP(Permit permit) {

		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		for (ValidationMessage vm : messages) {
			if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
				vm.setSeverity(ValidationMessage.Severity.INFO);
				infoMessages.add(vm);
			}
		}
		for (ValidationMessage vm : infoMessages) {
			messages.remove(vm);
		}

		// if this is NSR permit then you shouldn't be here
		if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
			messages.add(new ValidationMessage(
					"Permit",
					"Permit being published is NSR, cannot prepare Proposed Permit.",
					ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
			return messages;
		}

		/*
		 * 
		 * if (!(permit instanceof TVPermit)) { messages.add(new
		 * ValidationMessage("TVPermit", "Permit is not a TVPermit.",
		 * ValidationMessage.Severity.ERROR, buildPermitProfileRef()));
		 * 
		 * if (messages.size() > 0) { ValidationMessage vm = messages.get(0); if
		 * (vm.getMessage() != null && !vm.getMessage().startsWith(
		 * "Return to initial permit ")) { vm = new ValidationMessage("Permit",
		 * "Return to initial permit " + permit.getPermitNumber() + ".",
		 * ValidationMessage.Severity.INFO, buildPermitProfileRef());
		 * messages.add(0, vm); } } return messages; }
		 */

		// Check reason codes to see if reason code allows permit to be issued
		// PP
		boolean isSigMod = false;
		boolean isReopening = false;
		// boolean isRescind = false;
		boolean isAPA = false;
		boolean isMinorMod = false;
		boolean is502b10Change = false;
		boolean isInitial = false;
		boolean isRenewal = false;

		for (String reason : permit.getPermitReasonCDs()) {
			if (reason.equals(PermitReasonsDef.SPM)) {
				isSigMod = true;
			} else if (reason.equals(PermitReasonsDef.REOPENING)) {
				isReopening = true;
			// } else if (reason.equals(PermitReasonsDef.RESCIND)) {
			//	isRescind = true;
			} else if (reason.equals(PermitReasonsDef.APA)) {
				isAPA = true;
			} else if (reason.equals(PermitReasonsDef.MPM)) {
				isMinorMod = true;
			} else if (reason.equals(PermitReasonsDef.CHANGE_502_B_10)) {
				is502b10Change = true;
			} else if (reason.equals(PermitReasonsDef.INITIAL)) {
				isInitial = true;
			} else if (reason.equals(PermitReasonsDef.RENEWAL)) {
				isRenewal = true;
			}
		}

		if (!isSigMod && !isReopening && !isMinorMod /* && !isRescind */) {
			// String unless = "unless it is also an SPM, a Reopening, or a Rescind.";
			String unless = "unless it is also an SPM, a MPM, or a Reopening.";
			if (isAPA) {
				messages.add(new ValidationMessage("Permit",
						"This APA may not be published PP " + unless,
						ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}
			/*
			if (isMinorMod) {
				messages.add(new ValidationMessage("Permit",
						"This minor permit modification may not be published PP "
								+ unless, ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}
			*/
			if (is502b10Change) {
				messages.add(new ValidationMessage("Permit",
						"This 502(b)(10) Change may not be published PP "
								+ unless, ValidationMessage.Severity.ERROR,
						buildPermitProfileRef(),permit.getPermitID()));
			}
		}

		// Check for previous draft issuance.

		if (isSigMod || isReopening || isInitial || isRenewal) {

			PermitIssuance pi = ((TVPermit) permit).getDraftIssuance();
			if (pi != null) {
				if (pi.getIssuanceStatusCd() == null
						|| !pi.getIssuanceStatusCd().equals(
								IssuanceStatusDef.ISSUED)) {
					messages.add(new ValidationMessage("draftIssuance",
							"Public Notice must first be published.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}
				Timestamp endOfCmnt = pi.getPublicCommentEndDate();
				Timestamp now = new Timestamp(Calendar.getInstance()
						.getTimeInMillis());
				if (endOfCmnt == null) {
					messages.add(new ValidationMessage("endOfCommentPeriod",
							"Permit has no end of draft comment period.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				} else if (endOfCmnt.after(now)) {
					messages.add(new ValidationMessage(
							"endOfCommentPeriod",
							"End of draft comment period has not been reached.",
							ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));
				}
			} else {
				messages.add(new ValidationMessage("draftIssuance",
						"Public Notice must first be published.",
						ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
			}
		}

		// Check the permit's documents.
		boolean hasPPDoc = false;
		boolean hasPPSOB = false;
		for (PermitDocument doc : permit.getDocuments()) {
			ValidationMessage[] vms = doc.validate();
			for (ValidationMessage vm : vms) {
				messages.add(vm);
			}
			if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.PROPOSED_TV_PERMIT_DOCUMENT)
			// TO DO verify that this is obsolete...in STARS2, an issuance
			// document had to have a stage.
			// In IMPACT, we have a separate document type for each stage.
			// && doc.getIssuanceStageFlag().equals(
			// PermitDocIssuanceStageDef.PROPOSED)
			) {
				hasPPDoc = true;
			} else if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.PROPOSED_STATEMENT_BASIS)) {
				hasPPSOB = true;
			}
		}

		if (!hasPPDoc /* && !permit.getPermitReasonCDs().contains(PermitReasonsDef.RESCIND) */) {
			messages.add(new ValidationMessage("proposedTVPermit",
					"Permit has no Proposed TV Permit document.",
					ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
		}
		if (!hasPPSOB && (!permit.getPermitType().equals(PermitTypeDef.TV_PTO) || (!is502b10Change && !isAPA))) {
			messages.add(new ValidationMessage("proposedStatementofBasis",
					"Permit has no Proposed Statement of Basis.",
					ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
		}

		messages.addAll(infoMessages);

		return messages;
	}

	public static List<ValidationMessage> validatePreparePPPackage(Permit permit) {

		// If we get this far, we need to first run the validations for the
		// previous step.
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validatePreparePP(permit));
		ArrayList<ValidationMessage> infoMessages = new ArrayList<ValidationMessage>();

		for (ValidationMessage vm : messages) {
			if (vm.getSeverity() == ValidationMessage.Severity.WARNING) {
				vm.setSeverity(ValidationMessage.Severity.INFO);
				infoMessages.add(vm);
			}
		}
		for (ValidationMessage vm : infoMessages) {
			messages.remove(vm);
		}

		// if this is NSR permit then you shouldn't be here
		// Note: Returning here to avoid casting errors further on.
		if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
			return messages;
		}

		/*
		 * duplicate - also called by validatePreparePP if (!(permit instanceof
		 * TVPermit)) { messages.add(new ValidationMessage("TVPermit",
		 * "Permit is not a TVPermit.", ValidationMessage.Severity.ERROR,
		 * buildPermitProfileRef())); if (messages.size() > 0) {
		 * ValidationMessage vm = messages.get(0); if (vm.getMessage() != null
		 * && !vm.getMessage().startsWith( "Return to initial permit ")) { vm =
		 * new ValidationMessage("Permit", "Return to initial permit " +
		 * permit.getPermitNumber() + ".", ValidationMessage.Severity.INFO,
		 * buildPermitProfileRef()); messages.add(0, vm); } } return messages; }
		 */

		/*
		 * if (!IssuanceStatusDef.ISSUED.equalsIgnoreCase(((TVPermit) permit)
		 * .getPpIssuanceStatusCd())) {
		 * 
		 * messages.add(new ValidationMessage("ppIssuanceStatus",
		 * "PP Publication status is not Published.",
		 * ValidationMessage.Severity.ERROR, buildPpRef())); }
		 */

		// check EPA Date and EPA cover letter doc
		Timestamp sentToEpaDate = ((TVPermit) permit).getUsepaPermitSentDate();
		if (sentToEpaDate == null && (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
				|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
			messages.add(new ValidationMessage("sentToEpaDate",
					"Permit has no value for Date Permit Sent to USEPA.",
					ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
		}

		boolean hasEPACoverLetter = false;
		for (PermitDocument doc : permit.getDocuments()) {

			ValidationMessage[] vms = doc.validate();
			for (ValidationMessage vm : vms) {
				messages.add(vm);
			}

			if (doc.getPermitDocTypeCD().equals(
					PermitDocTypeDef.EPA_COVER_LETTER_PP)) {
				hasEPACoverLetter = true;
			}

		}
		if (!hasEPACoverLetter && (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
				|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
				&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
			messages.add(new ValidationMessage("EPACoverLetterPP",
					"Permit has no EPA Cover Letter PP.",
					ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
		}

		if (IssuanceStatusDef.NOT_READY.equalsIgnoreCase(((TVPermit) permit)
				.getPpIssuanceStatusCd()) && (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
						|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
								&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
			messages.add(new ValidationMessage("ppIssuanceStatus",
					"PP Publication status is not ready.",
					ValidationMessage.Severity.ERROR, buildPpRef(),permit.getPermitID()));
		}
		
		// For TV permit, remove the following validation message if it is present
		// Permit contains an emission unit which cannot be found in an application Permit EU ID is XXXXXX. (TFS task 4647)
		if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO))
			messages = removeMsgFromValidationMessages(messages, "Permit contains an emission unit which cannot be found in an application");

		return messages;
	}

	public static List<ValidationMessage> validatePrepareProposedPermit(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validatePreparePP(permit));

		// if this is NSR permit then you shouldn't be here
		/*
		 * if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
		 * messages.add(new ValidationMessage( "Permit",
		 * "Permit being published is NSR, cannot prepare Proposed Permit.",
		 * ValidationMessage.Severity.ERROR, buildDraftRef())); }
		 */

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		
		// For TV permit, remove the following validation message if it is present
		// Permit contains an emission unit which cannot be found in an application Permit EU ID is XXXXXX. (TFS task 4647)
		if(permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO))
			messages = removeMsgFromValidationMessages(messages, "Permit contains an emission unit which cannot be found in an application");

		return messages;
	}

	public static List<ValidationMessage> validatePrepareProposedPermitPackage(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validatePreparePPPackage(permit));

		// if this is NSR permit then you shouldn't be here
		/*
		 * if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
		 * messages.add(new ValidationMessage( "Permit",
		 * "Permit being published is NSR, cannot prepare Proposed Permit.",
		 * ValidationMessage.Severity.ERROR, buildDraftRef())); }
		 */

		if (messages.size() > 0) {
			ValidationMessage vm = messages.get(0);
			if (vm.getMessage() != null
					&& !vm.getMessage().startsWith("Return to initial permit ")) {
				vm = new ValidationMessage("Permit",
						"Return to initial permit " + permit.getPermitNumber()
								+ ".", ValidationMessage.Severity.INFO,
						buildPermitProfileRef(),permit.getPermitID());
				messages.add(0, vm);
			}
		}
		
		return messages;
	}

	private static List<ValidationMessage> validateManagerSupervisorReview(
			Permit permit) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));
		// For TV permit, check for public notice document here only if the
		// permit status is none. 
		// Basic permit validation handles this check when the permit status
		// is issued draft or issued pp
		if(permit.getPermitType().equals(PermitTypeDef.TV_PTO)
			&& (permit.getPermitGlobalStatusCD() != null
				&& permit.getPermitGlobalStatusCD().equalsIgnoreCase(PermitGlobalStatusDef.NONE))){
			messages.addAll(validateMgrSupervisorReview(permit));
		}
		return messages;
	}

	private static List<ValidationMessage> validatePermitPeerReview(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>(
				validateBasicPermit(permit));

		return messages;
	}

	private static boolean checkDocType(List<PermitDocument> documents,
			String docType) {
		for (PermitDocument doc : documents) {
			if (doc.getPermitDocTypeCD().equals(docType)) {
				return true;
			}

		}
		return false;
	}

	public static final String PERMIT_PROFILE_OUTCOME = "permitProfile";

	public static final String PERMIT_TC_OUTCOME = "permits.detail.tcList";

	public static final String DRAFT_OUTCOME = "permits.detail.draftIssuance";

	// public static final String PPP_OUTCOME = "permits.detail.PPPIssuance";

	public static final String PP_OUTCOME = "permits.detail.PPIssuance";

	public static final String FINAL_OUTCOME = "permits.detail.finalIssuance";
	
	public static final String FEE_SUMMARY_OUTCOME = "permits.detail.feeSummary";
	
	static final String SEPARATOR = ":";

	public static String buildEUNodeRef(String epaEmuId) {
		return PermitDetail.EU_NODE_TYPE + SEPARATOR + epaEmuId;
	}

	public static String buildExcludeEUNodeRef(String emuId) {
		return PermitDetail.EXCLUDE_EU_NODE_TYPE + SEPARATOR + emuId;
	}

	private static String buildEURef(String epaEmuId) {
		return buildEUNodeRef(epaEmuId) + SEPARATOR + PERMIT_PROFILE_OUTCOME;
	}

	private static String buildPermitProfileRef() {
		return PermitDetail.PERMIT_NODE_TYPE + SEPARATOR
				+ PERMIT_PROFILE_OUTCOME;
	}

	private static String buildAppProfileRef() {
		return PermitDetail.APP_NODE_TYPE + SEPARATOR + "applicationDetail";
	}

	private static String buildTCRef() {
		return PermitDetail.PERMIT_NODE_TYPE + SEPARATOR + PERMIT_TC_OUTCOME;
	}

	private static String buildDraftRef() {
		return PermitDetail.PERMIT_NODE_TYPE + SEPARATOR + DRAFT_OUTCOME;
	}
	
	private static String buildFeeSummaryRef() {
		return PermitDetail.PERMIT_NODE_TYPE + SEPARATOR + FEE_SUMMARY_OUTCOME;
	}

	// private static String buildPppRef() {
	// return PermitDetail.PERMIT_NODE_TYPE + SEPARATOR + PPP_OUTCOME;
	// }

	private static String buildPpRef() {
		return PermitDetail.PERMIT_NODE_TYPE + SEPARATOR + PP_OUTCOME;
	}

	private static String buildFinalRef() {
		return PermitDetail.PERMIT_NODE_TYPE + SEPARATOR + FINAL_OUTCOME;
	}
	
	private static String buildContactsRef(String facilityId) {
		return FacilityProfileBase.CONTACTS_REFERENCE;
		//return ValidationBase.FACILITY_TAG + SEPARATOR + facilityId + SEPARATOR + FacilityProfileBase.CONTACTS_REFERENCE + SEPARATOR + FacilityProfileBase.CONTACTS_OUTCOME;
	}
	
	public static List<ValidationMessage> validateMgrSupervisorReview(Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		boolean hasPublicNoticeDocument = false;
		// skip this check for rescinded permits
		//if(!permit.getPermitReasonCDs().contains(PermitReasonsDef.RESCIND)) {
			for (PermitDocument permitDoc : permit.getDocuments()) {
				if (permitDoc.getPermitDocTypeCD().equals(
						PermitDocTypeDef.TV_PUBLIC_NOTICE_DOCUMENT)){
						hasPublicNoticeDocument = true;
						break;
				}
			}			
			if(!hasPublicNoticeDocument && (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
					|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
							&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
							&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
				messages.add(new ValidationMessage("Documents",
							"Permit has no Public Notice document.",
							ValidationMessage.Severity.ERROR,
							buildDraftRef(),permit.getPermitID()));
			}	
		//}

		return messages;
	}
	
	// Complete Public Notice
	private static List<ValidationMessage> validateCompletePublicNotice(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		if (!IssuanceStatusDef.ISSUED.equalsIgnoreCase(permit.getDraftIssuanceStatusCd()) && (!permit.getPermitType().equals(PermitTypeDef.TV_PTO)
						|| (!permit.getPermitReasonCDs().contains(PermitReasonsDef.MPM)
								&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.CHANGE_502_B_10)
								&& !permit.getPermitReasonCDs().contains(PermitReasonsDef.APA)))) {
			messages.add(new ValidationMessage("draftIssuanceStatus",
					"Draft Publication status is not published.",
					ValidationMessage.Severity.ERROR, buildDraftRef(),permit.getPermitID()));

		}

		return messages;
	}
	
	private static ArrayList<ValidationMessage> removeMsgFromValidationMessages(ArrayList<ValidationMessage> vmList, String message) {
		
		ArrayList<ValidationMessage> msgsToRemove = new ArrayList<ValidationMessage>();
		
		for(ValidationMessage vm : vmList) {
			if(vm.getMessage().contains(message))
				msgsToRemove.add(vm);
		}
		
		for(ValidationMessage vm : msgsToRemove)
			vmList.remove(vm);
	
		return vmList;
	}
	
	private static ArrayList<ValidationMessage> validateKnownIncompleteNSRApp(
			Permit permit) {
		ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		// if this is NSR permit then you shouldn't be here
		if (permit.getPermitType().equals(PermitTypeDef.NSR)) {

			try {

				ApplicationService applicationBO = ServiceFactory.getInstance()
						.getApplicationService();
				for (Application permitApp : permit.getApplications()) {

					permitApp = applicationBO.retrieveApplication(permitApp
							.getApplicationID());

					if (permitApp instanceof PTIOApplication) {
						if (((PTIOApplication) permitApp)
								.isKnownIncompleteNSRApp()) {
							messages.add(new ValidationMessage(
									"Permit",
									"Permit has at least one application identified as Known Incomplete. The only valid workflow option is Prepare Inactive/Withdrawn.",
									ValidationMessage.Severity.ERROR,
									buildDraftRef(), permit.getPermitID()));
							return messages;
						}
					}

				} // END: for (Application permitApp : permit.getApplications())

			} catch (Exception e) {
				logger.error("Cannot retrieve applications: " + e.getMessage(),
						e);
				messages.add(new ValidationMessage("Application",
						"Unable to retrieve applications. " + e.getMessage(),
						ValidationMessage.Severity.ERROR, buildAppProfileRef(),
						permit.getPermitID()));
				return messages;
			}

		}

		return messages;
	}
}