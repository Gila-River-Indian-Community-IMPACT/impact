package us.oh.state.epa.stars2.webcommon.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequestITR;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequestRPS;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequestSPA;
import us.oh.state.epa.stars2.database.dbObjects.application.TIVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.def.AppEUAPTUnitTypeDef;
import us.oh.state.epa.stars2.def.AppEUAmineTypeDef;
import us.oh.state.epa.stars2.def.AppEUBOLServiceTypeDef;
import us.oh.state.epa.stars2.def.AppEUBoilerTypeDef;
import us.oh.state.epa.stars2.def.AppEUBtuUnitsDef;
import us.oh.state.epa.stars2.def.AppEUCCUEquipTypeDef;
import us.oh.state.epa.stars2.def.AppEUCirculationPumpTypeDef;
import us.oh.state.epa.stars2.def.AppEUCirculationRateDef;
import us.oh.state.epa.stars2.def.AppEUCoalUsageTypeDef;
import us.oh.state.epa.stars2.def.AppEUCrusherTypeDef;
import us.oh.state.epa.stars2.def.AppEUENGTierRatingDef;
import us.oh.state.epa.stars2.def.AppEUEquipServiceTypeDef;
import us.oh.state.epa.stars2.def.AppEUFUGEmissionTypeDef;
import us.oh.state.epa.stars2.def.AppEUFlowRateUnitsDef;
import us.oh.state.epa.stars2.def.AppEUFuelConsumptionDef;
import us.oh.state.epa.stars2.def.AppEUFuelGasHeatingDef;
import us.oh.state.epa.stars2.def.AppEUFuelSulfurDef;
import us.oh.state.epa.stars2.def.AppEUINCPriFuelTypeDef;
import us.oh.state.epa.stars2.def.AppEUIgnitionDeviceTypeDef;
import us.oh.state.epa.stars2.def.AppEULUDThroughputUnitsDef;
import us.oh.state.epa.stars2.def.AppEUMateProcessedTypeDef;
import us.oh.state.epa.stars2.def.AppEUMateUsageDef;
import us.oh.state.epa.stars2.def.AppEUMaterialTypeDef;
import us.oh.state.epa.stars2.def.AppEUMotiveForceDef;
import us.oh.state.epa.stars2.def.AppEUNEGServiceTypeDef;
import us.oh.state.epa.stars2.def.AppEUPowerSourceDef;
import us.oh.state.epa.stars2.def.AppEUProductionHourRateDef;
import us.oh.state.epa.stars2.def.AppEUProductionRateDef;
import us.oh.state.epa.stars2.def.AppEUScreenDef;
import us.oh.state.epa.stars2.def.AppEUStockpileSizeDef;
import us.oh.state.epa.stars2.def.AppEUStockpileTypeDef;
import us.oh.state.epa.stars2.def.AppEUStrippingGasSourceDef;
import us.oh.state.epa.stars2.def.AppEUTNKThroughputUnitsDef;
import us.oh.state.epa.stars2.def.AppEUTurbineCycleTypeDef;
import us.oh.state.epa.stars2.def.AppEUWasteGasVolUnitsDef;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EACFormTypeDef;
import us.oh.state.epa.stars2.def.EmissionUnitsDef;
import us.oh.state.epa.stars2.def.NSREuFedRuleAppDef;
import us.oh.state.epa.stars2.def.PBRReasonDef;
import us.oh.state.epa.stars2.def.PBRRequestingRevocDef;
import us.oh.state.epa.stars2.def.PBRTypeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationEUPurposeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationPurposeDef;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl2Def;
import us.oh.state.epa.stars2.def.PTIOGeneralPermitTypeDef;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PTIORequestingFedLimitsDef;
import us.oh.state.epa.stars2.def.PTIORequestingFedLimitsReasonDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.RPCTypeDef;
import us.oh.state.epa.stars2.def.RPRReasonDef;
import us.oh.state.epa.stars2.def.RuleCitationDef;
import us.oh.state.epa.stars2.def.TVAppComplianceCertIntervalDef;
import us.oh.state.epa.stars2.def.TVApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.TVApplicationPurposeDef;
import us.oh.state.epa.stars2.def.TVCompRptFreqDef;
import us.oh.state.epa.stars2.def.TVComplianceStatusDef;
import us.oh.state.epa.stars2.def.TVEMComplianceStatusDef;
import us.oh.state.epa.stars2.def.TVFacWideReqOptionDef;
import us.oh.state.epa.stars2.def.TVFedRuleAppDef;
import us.oh.state.epa.stars2.def.TVIeuReasonDef;
import us.oh.state.epa.stars2.def.TVRuleCiteTypeDef;
import us.oh.state.epa.stars2.def.TVSection112RDef;
import us.oh.state.epa.stars2.def.TvReqBasisDef;
import us.oh.state.epa.stars2.def.TvRestrictionTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;

@SuppressWarnings("serial")
public class ApplicationReference extends AppBase {
	// use TreeMap to sort applications types
	private static Map<String, Class<? extends Application>> applicationTypes = new TreeMap<String, Class<? extends Application>>();
	private static Map<String, Class<? extends Application>> intentToRelTypes = new TreeMap<String, Class<? extends Application>>();
	private List<SelectItem> applicationClasses = new ArrayList<SelectItem>();
	private List<SelectItem> intentToRelClasses = new ArrayList<SelectItem>();
	
	private ApplicationService applicationService;

	public ApplicationReference() {
		applicationTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.PTIO_APPLICATION),
				PTIOApplication.class);
		applicationTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.TITLE_V_APPLICATION),
				TVApplication.class);
		//applicationTypes.put(ApplicationTypeDef.getData().getItems()
		//		.getItemDesc(ApplicationTypeDef.TITLE_IV_APPLICATION),
		//		TIVApplication.class);
		/*
		 * Remove PBR references in permit applications
		 * applicationTypes.put(ApplicationTypeDef
		 * .getData().getItems().getItemDesc(
		 * ApplicationTypeDef.PBR_NOTIFICATION), PBRNotification.class);
		 */
		//applicationTypes.put(ApplicationTypeDef.getData().getItems()
		//		.getItemDesc(ApplicationTypeDef.RPC_REQUEST), RPCRequest.class);
		//applicationTypes.put(ApplicationTypeDef.getData().getItems()
		//		.getItemDesc(ApplicationTypeDef.RPR_REQUEST), RPRRequest.class);
		//pplicationTypes.put(ApplicationTypeDef.getData().getItems()
		//		.getItemDesc(ApplicationTypeDef.RPE_REQUEST), RPERequest.class);
		/*
		applicationTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.INTENT_TO_RELOCATE),
				RelocateRequestITR.class);
		applicationTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.RELOCATE_TO_PREAPPROVED_SITE),
				RelocateRequestRPS.class);
		applicationTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.SITE_PRE_APPROVAL),
				RelocateRequestSPA.class);
		*/
		applicationTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.DELEGATION_OF_RESPONSIBILITY),
				DelegationRequest.class);

		for (Map.Entry<String, Class<? extends Application>> entry : applicationTypes
				.entrySet()) {
			applicationClasses.add(new SelectItem(entry.getValue(), entry
					.getKey()));
		}
/*
		intentToRelTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.INTENT_TO_RELOCATE),
				RelocateRequestITR.class);
		intentToRelTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.RELOCATE_TO_PREAPPROVED_SITE),
				RelocateRequestRPS.class);
		intentToRelTypes.put(ApplicationTypeDef.getData().getItems()
				.getItemDesc(ApplicationTypeDef.SITE_PRE_APPROVAL),
				RelocateRequestSPA.class);
*/
		for (Map.Entry<String, Class<? extends Application>> entry : intentToRelTypes
				.entrySet()) {
			intentToRelClasses.add(new SelectItem(entry.getValue(), entry
					.getKey()));
		}
	}

	/*
	 * Getter for application types
	 */

	public final List<SelectItem> getApplicationClasses() {
		List<SelectItem> filteredClasses = new ArrayList<SelectItem>();
		for (SelectItem item : applicationClasses) {
			if ((item.getValue().equals(RelocateRequestITR.class)
					|| item.getValue().equals(RelocateRequestRPS.class) || item
					.getValue().equals(RelocateRequestSPA.class))) {
				// if external or non-portal skip this one
				// Relocation Types - only for PORTABLE facilities
				FacilityProfileBase fp = (FacilityProfileBase) FacesUtil
						.getManagedBean("facilityProfile");
				if (!isInternalApp() || !fp.getFacility().getPortable()) {
					continue;
				}
			}

			// temporarily remove the following 3 choices for release 3.0
			if (item.getValue().equals(DelegationRequest.class)
					|| item.getValue().equals(RPERequest.class)
					|| item.getValue().equals(TIVApplication.class)) {
				continue;
			}

			filteredClasses.add(item);
		}
		return filteredClasses;
	}
	
	

	/*
	 * Getter for intent to relocate types
	 */

	public ApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public final List<SelectItem> getIntentToRelocateClasses() {
		return intentToRelClasses;
	}

	/*
	 * Getters for SelectItem's coming from DEF tables
	 */
	public final List<SelectItem> getApplicationTypeDefs() {
		return ApplicationTypeDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getPollutantDefs() {
		return PollutantDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getAllPollutantDefs() {
		return PollutantDef.getData().getItems().getAllItems();
	}

	public final DefSelectItems getApplicationDocTypeDefs() {
		return ApplicationDocumentTypeDef.getData().getItems();
	}

	public final DefSelectItems getTvApplicationDocTypeDefs() {
		return TVApplicationDocumentTypeDef.getData().getItems();
	}

	public final List<SelectItem> getTvAppPurposeDefs() {
		return TVApplicationPurposeDef.getData().getItems().getItems("");
	}
	
	public final List<SelectItem> getTvAppComplianceCertIntervalDefs() {
		return TVAppComplianceCertIntervalDef.getData().getItems().getItems("");
	}

	/**
	 * Limit Permit Reason to those applicable for the Title V Permit
	 * Application pick list.
	 * 
	 * @return
	 */
	public final List<SelectItem> getPermitReasonDefs() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		DefSelectItems items = PermitReasonsDef.getData().getItems();

		for (SelectItem item : items.getCurrentItems()) {
			if (item.getValue().equals(PermitReasonsDef.REOPENING)) {
				result.add(item);
			// } else if (item.getValue().equals(PermitReasonsDef.RESCIND)) {
			//	result.add(item);
			} else if (item.getValue().equals(PermitReasonsDef.SPM)) {
				result.add(new SelectItem(item.getValue(),
						"Significant Permit Modification"));
			} else if (item.getValue().equals(PermitReasonsDef.MPM)) {
				result.add(new SelectItem(item.getValue(),
						"Minor Permit Modification"));
			} else if (item.getValue().equals(PermitReasonsDef.APA)) {
				result.add(new SelectItem(item.getValue(),
						"Administrative Permit Amendment"));
			} else if (item.getValue().equals(PermitReasonsDef.CHANGE_502_B_10)) {
				result.add(item);
			}
		}

		return result;
	}

	public final DefSelectItems getPtioAppPurposeDefs() {
		return PTIOApplicationPurposeDef.getData().getItems();
	}

	public final List<SelectItem> getNspsSubpartDefs() {
		return PTIONSPSSubpartDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getNeshapSubpartDefs() {
		return PTIONESHAPSSubpartDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getMactSubpartDefs() {
		return PTIOMACTSubpartDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getRuleCiteTypeDefs() {
		return TVRuleCiteTypeDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getTvReqBasisDefs() {
		return TvReqBasisDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getTvRestrictionTypeDefs() {
		return TvRestrictionTypeDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getRuleCitationDefs() {
		return RuleCitationDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getTvIeuReasonDefs() {
		return TVIeuReasonDef.getData().getItems().getItems("");
	}

	public final List<SelectItem> getTvCompRptFreqDefs() {
		ArrayList<SelectItem> selectItems = new ArrayList<SelectItem>(
				TVCompRptFreqDef.getData().getItems().getItems(""));
		// ensure that "Other" is at the end of the list
		SelectItem otherItem = null;
		Iterator<SelectItem> it = selectItems.iterator();
		while (it.hasNext()) {
			SelectItem item = it.next();
			if (item.getValue().equals("OTH")) {
				otherItem = item;
				it.remove();
			}
		}
		if (otherItem != null) {
			selectItems.add(otherItem);
		}
		return selectItems;
	}

	public final List<SelectItem> getEmissionUnitsDefs() {
		return EmissionUnitsDef.getData().getItems().getItems("");
	}

	public final DefSelectItems getGeneralPermitTypeDefs() {
		return PTIOGeneralPermitTypeDef.getData().getItems();
	}

	public final DefSelectItems getPerDueDateDefs() {
		return PTIOPERDueDateDef.getData().getItems();
	}

	public final DefSelectItems getPtioEUPurposeDefs() {
		return PTIOApplicationEUPurposeDef.getData().getItems();
	}

	public final DefSelectItems getFedLimitsReasonDefs() {
		return PTIORequestingFedLimitsReasonDef.getData().getItems();
	}

	public final DefSelectItems getEacFormTypeDefs() {
		return EACFormTypeDef.getData().getItems();
	}

	public final DefSelectItems getComplianceStatusDefs() {
		return TVComplianceStatusDef.getData().getItems();
	}

	public final DefSelectItems getEmComplianceDefs() {
		return TVEMComplianceStatusDef.getData().getItems();
	}

	public final DefSelectItems getFederalRuleAppl1Defs() {
		return PTIOFedRuleAppl1Def.getData().getItems();
	}

	public final DefSelectItems getFederalRuleAppl2Defs() {
		return PTIOFedRuleAppl2Def.getData().getItems();
	}

	public final DefSelectItems getNsrEuFederalRuleAppDefs() {
		return NSREuFedRuleAppDef.getData().getItems();
	}

	public final DefSelectItems getRequestingFederalLimitsDefs() {
		return PTIORequestingFedLimitsDef.getData().getItems();
	}

	public final DefSelectItems getSection112RDefs() {
		return TVSection112RDef.getData().getItems();
	}

	public final DefSelectItems getPbrTypeDefs() {
		return PBRTypeDef.getData().getItems();
	}

	public final DefSelectItems getPbrReasonDefs() {
		return PBRReasonDef.getData().getItems();
	}

	public final DefSelectItems getPbrRequestingRevocDefs() {
		return PBRRequestingRevocDef.getData().getItems();
	}

	public final List<SelectItem> getRpcTypeDefs() {
		List<SelectItem> rpcTypeDefList = new ArrayList<SelectItem>();
		DefSelectItems rpcTypeDefItems = RPCTypeDef.getData().getItems();
		for (SelectItem item : rpcTypeDefItems.getCurrentItems()) {
			if (isInternalApp()
					&& !InfrastructureDefs.getCurrentUserAttrs()
							.isStars2Admin()
					&& !item.getValue().equals(RPCTypeDef.PTIO_ADMIN_MOD)) {
				// PTIO Admin Mod is only choice available for non-Admin
				// internal users
				continue;
			}
			rpcTypeDefList.add(item);
		}
		return rpcTypeDefList;
	}

	public final DefSelectItems getRprReasonDefs() {
		return RPRReasonDef.getData().getItems();
	}

	public final DefSelectItems getTvFedRuleAppDefs() {
		return TVFedRuleAppDef.getData().getItems();
	}

	public final DefSelectItems getTvFacWideReqOptionDefs() {
		return TVFacWideReqOptionDef.getData().getItems();
	}

	public final DefSelectItems getAppEUBoilerTypeDefs() {
		return AppEUBoilerTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUBOLServiceTypeDefs() {
		return AppEUBOLServiceTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUBtuUnitsDefs() {
		return AppEUBtuUnitsDef.getData().getItems();
	}

	public final DefSelectItems getAppEUCrusherTypeDefs() {
		return AppEUCrusherTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUEquipServiceTypeDefs() {
		return AppEUEquipServiceTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUFuelConsumptionDefs() {
		return AppEUFuelConsumptionDef.getData().getItems();
	}

	public final DefSelectItems getAppEUFuelGasHeatingDefs() {
		return AppEUFuelGasHeatingDef.getData().getItems();
	}

	public final DefSelectItems getAppEUFuelSulfurDefs() {
		return AppEUFuelSulfurDef.getData().getItems();
	}

	public final DefSelectItems getAppEUFUGEmissionTypeDefs() {
		return AppEUFUGEmissionTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEULUDThroughputUnitsDefs() {
		return AppEULUDThroughputUnitsDef.getData().getItems();
	}

	public final DefSelectItems getAppEUMaterialTypeDefs() {
		return AppEUMaterialTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUNEGServiceTypeDefs() {
		return AppEUNEGServiceTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUENGTierRatingDefs() {
		return AppEUENGTierRatingDef.getData().getItems();
	}

	public final DefSelectItems getAppEUPowerSourceDefs() {
		return AppEUPowerSourceDef.getData().getItems();
	}

	public final DefSelectItems getAppEUProductionRateDefs() {
		return AppEUProductionRateDef.getData().getItems();
	}
	
	public final DefSelectItems getAppEUProductionHourRateDefs() {
		return AppEUProductionHourRateDef.getData().getItems();
	}

	public final DefSelectItems getAppEUScreenDefs() {
		return AppEUScreenDef.getData().getItems();
	}

	public final DefSelectItems getAppEUStockpileSizeDefs() {
		return AppEUStockpileSizeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUStockpileTypeDefs() {
		return AppEUStockpileTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUTNKThroughputUnitsDefs() {
		return AppEUTNKThroughputUnitsDef.getData().getItems();
	}

	public final DefSelectItems getAppEUAmineTypeDefs() {
		return AppEUAmineTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUAPTUnitTypeDefs() {
		return AppEUAPTUnitTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUCCUEquipTypeDefs() {
		return AppEUCCUEquipTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUCirculationPumpTypeDefs() {
		return AppEUCirculationPumpTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUCirculationRateDefs() {
		return AppEUCirculationRateDef.getData().getItems();
	}

	public final DefSelectItems getAppEUFlowRateUnitsDefs() {
		return AppEUFlowRateUnitsDef.getData().getItems();
	}

	public final DefSelectItems getAppEUIgnitionDeviceTypeDefs() {
		return AppEUIgnitionDeviceTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUINCPriFuelTypeDefs() {
		return AppEUINCPriFuelTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUMateProcessedTypeDefs() {
		return AppEUMateProcessedTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUMateUsageDefs() {
		return AppEUMateUsageDef.getData().getItems();
	}

	public final DefSelectItems getAppEUMotiveForceDefs() {
		return AppEUMotiveForceDef.getData().getItems();
	}

	public final DefSelectItems getAppEUStrippingGasSourceDefs() {
		return AppEUStrippingGasSourceDef.getData().getItems();
	}

	public final DefSelectItems getAppEUTurbineCycleTypeDefs() {
		return AppEUTurbineCycleTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUCoalUsageTypeDefs() {
		return AppEUCoalUsageTypeDef.getData().getItems();
	}

	public final DefSelectItems getAppEUWasteGasVolUnitsDefs() {
		return AppEUWasteGasVolUnitsDef.getData().getItems();
	}

	/*
	 * Getters of individual values
	 */
	public final String getSubjectToSubpartFlag() {
		return PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART;
	}

	public final String getRequestingFedLimitsYesFlag() {
		return PTIORequestingFedLimitsDef.YES;
	}

	public final String getApplicationPurposeOtherCD() {
		return PTIOApplicationPurposeDef.OTHER;
	}

	public final String getEUPurposeNewInstallCD() {
		return PTIOApplicationEUPurposeDef.CONSTRUCTION;
	}

	/*
	 * public final String getEUPurposeInitialAppCD() { return
	 * PTIOApplicationEUPurposeDef.INITIAL_APPLICATION; }
	 */

	public final String getEUPurposeModificationBegunCD() {
		return PTIOApplicationEUPurposeDef.MODIFICATION;
	}

	/*
	 * public final String getEUPurposeModificationNotBegunCD() { return
	 * PTIOApplicationEUPurposeDef.MODIFICATION_NOT_BEGUN; }
	 */

	public final String getEUPurposeReconstructionCD() {
		return PTIOApplicationEUPurposeDef.RECONSTRUCTION;
	}

	/*
	 * public final String getEUPurposeRenewalCD() { return
	 * PTIOApplicationEUPurposeDef.RENEWAL; }
	 */

	public final String getEUPurposeOtherCD() {
		return PTIOApplicationEUPurposeDef.OTHER;
	}

	public final String getFedLimitsReasonOtherCD() {
		return PTIORequestingFedLimitsReasonDef.OTHER;
	}

	public final String getPbrRequestingRevocPTIOsFlag() {
		return PBRRequestingRevocDef.REVOCATE_PTIOS;
	}

	public final String getPbrRequestingRevocPTOsFlag() {
		return PBRRequestingRevocDef.REVOCATE_PTOS;
	}

	// Get a TableSorter for the Application Document Type Explanations
	public final TableSorter getApplicationDocumentTypeWrapper() {
		
		TableSorter table = new TableSorter();
		try {
			table.setWrappedData(getApplicationService().retrieveApplicationDocumentTypes().toArray());
		} catch (Exception e) {
			logger.error("FacilityReference.getApplicationDocumentTypeWrapper Error:"
					+ e.getMessage());
		}

		return table;
	}
	
	// Get a TableSorter for the Application Document Type Explanations
	public final TableSorter getTvApplicationDocumentTypeWrapper() {
		
		TableSorter table = new TableSorter();
		try {
			table.setWrappedData(getApplicationService().retrieveTvApplicationDocumentTypes().toArray());
		} catch (Exception e) {
			logger.error("FacilityReference.getTvApplicationDocumentTypeWrapper Error:"
					+ e.getMessage());
		}

		return table;
	}

}
