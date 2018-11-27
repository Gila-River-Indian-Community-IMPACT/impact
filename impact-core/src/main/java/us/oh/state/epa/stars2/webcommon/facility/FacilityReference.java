package us.oh.state.epa.stars2.webcommon.facility;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.def.CeOperatingStatusDef;
import us.oh.state.epa.stars2.def.CerrClassDef;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.ContentTypeDef;
import us.oh.state.epa.stars2.def.CoordDataSources;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.DesignCapacityDef;
import us.oh.state.epa.stars2.def.DesignCapacityUnitsDef;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.EgrPointShapeDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.EmissionReportsTemplateDef;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.EmissionsAttachmentTypeDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.EuPollutantDef;
import us.oh.state.epa.stars2.def.ExemptStatusDef;
import us.oh.state.epa.stars2.def.FacPermitStatusDef;
import us.oh.state.epa.stars2.def.FacilityAttachmentTypeDef;
import us.oh.state.epa.stars2.def.FacilityRequestStatusDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.HortAccuracyMeasure;
import us.oh.state.epa.stars2.def.HortCollectionMethods;
import us.oh.state.epa.stars2.def.HortReferenceDatum;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.MaxAnnualThroughputUnitEutypeDef;
import us.oh.state.epa.stars2.def.NonToxicPollutantDef;
import us.oh.state.epa.stars2.def.NspsPollutantDef;
import us.oh.state.epa.stars2.def.NsrPollutantDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PollutantCategoryDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.PortableGroupNames;
import us.oh.state.epa.stars2.def.PortableGroupTypes;
import us.oh.state.epa.stars2.def.PsdPollutantDef;
import us.oh.state.epa.stars2.def.ReferencePoints;
import us.oh.state.epa.stars2.def.RegulatoryRequirementTypeDef;
import us.oh.state.epa.stars2.def.ReportEisStatusDef;
import us.oh.state.epa.stars2.def.ReportOfEmissionsStateDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.ReportStatusDef;
import us.oh.state.epa.stars2.def.RevenueSearchState;
import us.oh.state.epa.stars2.def.SccCodesDef;
import us.oh.state.epa.stars2.def.TransitStatusDef;
import us.oh.state.epa.stars2.def.UnitDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.wy.state.deq.impact.def.AbsApplicationMethodTypeDef;
import us.wy.state.deq.impact.def.AbsLeadConcentrationPctTypeDef;
import us.wy.state.deq.impact.def.AbsSubstrateBlastedTypeDef;
import us.wy.state.deq.impact.def.AbsSubstrateRemovedTypeDef;
import us.wy.state.deq.impact.def.AptMaxThroughputUnitsDef;
import us.wy.state.deq.impact.def.AqdEmissionFactorGroupDef;
import us.wy.state.deq.impact.def.BatchingTypeDef;
import us.wy.state.deq.impact.def.BurnerTypeDef;
import us.wy.state.deq.impact.def.CkdMaxAnnualThroughputUnitsDef;
import us.wy.state.deq.impact.def.CkdUnitTypeDef;
import us.wy.state.deq.impact.def.CmxMaxProductionRateUnitsDef;
import us.wy.state.deq.impact.def.CotApplicationMethodTypeDef;
import us.wy.state.deq.impact.def.CotCoatingOperationTypeDef;
import us.wy.state.deq.impact.def.CotMaterialBeingCoatedTypeDef;
import us.wy.state.deq.impact.def.CshMaxAnnualThroughputUnitDef;
import us.wy.state.deq.impact.def.CshMaxAnnualThroughputUnitsDef;
import us.wy.state.deq.impact.def.CshUnitTypeDef;
import us.wy.state.deq.impact.def.DehydrationTypeDef;
import us.wy.state.deq.impact.def.DryTypeOfDryCleaningTypeDef;
import us.wy.state.deq.impact.def.DryTypeOfOperationTypeDef;
import us.wy.state.deq.impact.def.EguUnitTypeDef;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;
import us.wy.state.deq.impact.def.EngTypeOfUseDef;
import us.wy.state.deq.impact.def.EngineTypeDef;
import us.wy.state.deq.impact.def.EquipTypeDef;
import us.wy.state.deq.impact.def.EventTypeDef;
import us.wy.state.deq.impact.def.FatUnitTypeDef;
import us.wy.state.deq.impact.def.FiringTypeDef;
import us.wy.state.deq.impact.def.FlareDesignCapacityUnitsDef;
import us.wy.state.deq.impact.def.FuelTypeDef;
import us.wy.state.deq.impact.def.HeatContentFuelUnitsDef;
import us.wy.state.deq.impact.def.HmaMaxProductionRateUnitsDef;
import us.wy.state.deq.impact.def.IncineratorDesignCapacityUnitsDef;
import us.wy.state.deq.impact.def.IncineratorTypeDef;
import us.wy.state.deq.impact.def.LudMaxAnnualThroughputUnitsDef;
import us.wy.state.deq.impact.def.MaterialProducedDef;
import us.wy.state.deq.impact.def.MaterialTypeDef;
import us.wy.state.deq.impact.def.NamePlateRatingUnitsDef;
import us.wy.state.deq.impact.def.OrdExplosiveTypeDef;
import us.wy.state.deq.impact.def.PamEquipTypeDef;
import us.wy.state.deq.impact.def.PermittedEmissionsUnitDef;
import us.wy.state.deq.impact.def.PlantTypeDef;
import us.wy.state.deq.impact.def.PowerSourceTypeDef;
import us.wy.state.deq.impact.def.PrimaryFuelTypeDef;
import us.wy.state.deq.impact.def.PrnPressTypeDef;
import us.wy.state.deq.impact.def.PrnSubstrateFeedMethodTypeDef;
import us.wy.state.deq.impact.def.RemContaminantTypeDef;
import us.wy.state.deq.impact.def.RemContaminatedMaterialTypeDef;
import us.wy.state.deq.impact.def.ResApplicationMethodTypeDef;
import us.wy.state.deq.impact.def.SebUnitTypeDef;
import us.wy.state.deq.impact.def.SecondaryFuelTypeDef;
import us.wy.state.deq.impact.def.SemEquipTypeDef;
import us.wy.state.deq.impact.def.SepVesselTypeDef;
import us.wy.state.deq.impact.def.SiteRatingUnitsDef;
import us.wy.state.deq.impact.def.SruMaxAnnualThroughputUnitsDef;
import us.wy.state.deq.impact.def.SvcEquipTypeDef;
import us.wy.state.deq.impact.def.TkoMetalTypeDef;
import us.wy.state.deq.impact.def.TkoPlatingTypeDef;
import us.wy.state.deq.impact.def.TnkCapacityUnitDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeLiquidDef;
import us.wy.state.deq.impact.def.TnkMaxThroughputUnitDef;
import us.wy.state.deq.impact.def.TnkTankLocationDef;
import us.wy.state.deq.impact.def.UnitTypeDef;
import us.wy.state.deq.impact.def.WeldingProcessTypeDef;
import us.wy.state.deq.impact.def.WweEquipTypeDef;

public class FacilityReference extends AppBase {

	private static final long serialVersionUID = 5593055287139502185L;

	// TODO: Should be calling a service, not a dao.
	private FacilityDAO facilityDAO; 

	public FacilityDAO getFacilityDAO() {
		return facilityDAO;
	}

	public void setFacilityDAO(FacilityDAO facilityDAO) {
		this.facilityDAO = facilityDAO;
	}

	public final DefSelectItems getInvoiceStates() {
		return InvoiceState.getData().getItems();
	}

	public final DefSelectItems getCerrClassDefs() {
		return CerrClassDef.getData().getItems();
	}

	public final DefSelectItems getRevenueSearchStates() {
		return RevenueSearchState.getData().getItems();
	}

	public final DefSelectItems getPollutantDefs() {
		return PollutantDef.getData().getItems();
	}
	
	public final DefSelectItems getPollutantCategoryDefs() {
		return PollutantCategoryDef.getData().getItems();
	}

	public final DefSelectItems getNspsPollutantDefs() {
		return NspsPollutantDef.getData().getItems();
	}

	public final DefSelectItems getPsdPollutantDefs() {
		return PsdPollutantDef.getData().getItems();
	}

	public final DefSelectItems getNsrPollutantDefs() {
		return NsrPollutantDef.getData().getItems();
	}

	public final DefSelectItems getEuPollutantDefs() {
		return EuPollutantDef.getData().getItems();
	}
	
	public final DefSelectItems getPermittedEmissionUnitDefs() {
		return PermittedEmissionsUnitDef.getData().getItems();
	}

	public final Map<String, String> getEuPollutantDescs() {
		Map<String, String> pollutantDescs = new LinkedHashMap<String, String>();

		for (SelectItem item : EuPollutantDef.getData().getItems()
				.getCurrentItems()) {
			pollutantDescs.put((String) item.getValue().toString(),
					item.getLabel());
		}

		return pollutantDescs;
	}

	public final Map<String, String> getPollutantDescs() {
		Map<String, String> pollutantDescs = new LinkedHashMap<String, String>();

		for (SelectItem item : PollutantDef.getData().getItems()
				.getCurrentItems()) {
			pollutantDescs.put((String) item.getValue().toString(),
					item.getLabel());
		}

		return pollutantDescs;
	}

	public final DefSelectItems getNonToxicPollutantDefs() {
		return NonToxicPollutantDef.getData().getItems();
	}
	
	public final DefSelectItems getEgrPntShapeDefs() {
		return EgrPointShapeDef.getData().getItems();
	}

	public final DefSelectItems getEgrPntTypeDefs() {
		return EgrPointTypeDef.getData().getItems();
	}

	public final DefSelectItems getOperatingStatusDefs() {
		return OperatingStatusDef.getData().getItems();
	}

	public final DefSelectItems getDemOperatingStatusDefs() {
		return OperatingStatusDef.getDemData().getItems();
	}

	public final DefSelectItems getEuOperatingStatusDefs() {
		return EuOperatingStatusDef.getData().getItems();
	}

	public final DefSelectItems getDemEuOperatingStatusDefs() {
		return EuOperatingStatusDef.getDemData().getItems();
	}

	public final DefSelectItems getCeOperatingStatusDefs() {
		return CeOperatingStatusDef.getData().getItems();
	}

	public final DefSelectItems getEgOperatingStatusDefs() {
		return EgOperatingStatusDef.getData().getItems();
	}

	public final DefSelectItems getEmissionReportsDefs() {
		return EmissionReportsDef.getData().getItems();
	}

	public final DefSelectItems getEmissionReportsRealDefs() {
		return EmissionReportsRealDef.getData().getItems();
	}

	public final DefSelectItems getEmissionReportsTemplateDefs() {
		return EmissionReportsTemplateDef.getData().getItems();
	}

	public final DefSelectItems getEmissionsAttachmentTypeDefs() {
		return EmissionsAttachmentTypeDef.getData().getItems();
	}

	public final DefSelectItems getExcludeAQDEmissionsAttachmentTypeDefs() {
        return EmissionsAttachmentTypeDef.getExcludeAQDTypeData().getItems();
    }
	
	public final DefSelectItems getEmissionCalcMethodDefs() {
		return EmissionCalcMethodDef.getData().getItems();
	}
	
	public final DefSelectItems getUserSelectableEmissionCalcMethodDefs() {
		return EmissionCalcMethodDef.getUserSelectableData().getItems();
	}

	public final DefSelectItems getPermitClassDefs() {
		return PermitClassDef.getData().getItems();
	}

	public final DefSelectItems getTransitStatusDefs() {
		return TransitStatusDef.getData().getItems();
	}

	public final DefSelectItems getReportReceivedStatusDefs() {
		return ReportReceivedStatusDef.getData().getItems();
	}

	public final DefSelectItems getReportOfEmissionsStateDefs() {
		return ReportOfEmissionsStateDef.getData().getItems();
	}

	public final DefSelectItems getReportStatusDefs() {
		return ReportStatusDef.getData().getItems();
	}

	public final DefSelectItems getReportEisStatusDefs() {
		return ReportEisStatusDef.getData().getItems();
	}

	public final DefSelectItems getUnitDefs() {
		return UnitDef.getData().getItems();
	}

	public final DefSelectItems getEmissionUnitReportingDefs() {
		return EmissionUnitReportingDef.getData().getItems();
	}

	public final DefSelectItems getEmissionUnitTypeDefs() {
		return EmissionUnitTypeDef.getData().getItems();
	}

	public final DefSelectItems getMaterialDefs() {
		return MaterialDef.getData().getItems();
	}

	public final DefSelectItems getExemptStatusDefs() {
		return ExemptStatusDef.getData().getItems();
	}

	public final DefSelectItems getCoordDataSources() {
		return CoordDataSources.getData().getItems();
	}

	public final DefSelectItems getReferencePoints() {
		return ReferencePoints.getData().getItems();
	}

	public final DefSelectItems getHortReferenceDatum() {
		return HortReferenceDatum.getData().getItems();
	}

	public final DefSelectItems getHortAccuracyMeasure() {
		return HortAccuracyMeasure.getData().getItems();
	}

	public final DefSelectItems getHortCollectionMethods() {
		return HortCollectionMethods.getData().getItems();
	}

	public final DefSelectItems getPortableGroupNames() {
		return PortableGroupNames.getData().getItems();
	}

	public final DefSelectItems getPortableGroupTypes() {
		return PortableGroupTypes.getData().getItems();
	}

	public final DefSelectItems getFacilityTypeTextDefs() {
		return FacilityTypeDef.getTextData().getItems();
	}

	public final DefSelectItems getFacilityTypeDefs() {
		return FacilityTypeDef.getData().getItems();
	}

	public final DefSelectItems getContEquipTypeDefs() {
		return ContEquipTypeDef.getData().getItems();
	}

	/*
	 * public DefSelectItems getDesignCapacityDefs() { return
	 * DesignCapacityDef.getData().getItems(); }
	 * 
	 * public DefSelectItems getDesignCapacityUnitsDefs() { return
	 * DesignCapacityUnitsDef.getData().getItems(); }
	 */
	public final List<SelectItem> getDesignCapacityDefs() {
		return DesignCapacityDef.getData().getItems()
				.getItems(getParent().getValue());
	}

	public final List<SelectItem> getDesignCapacityUnitsDefs() {
		return DesignCapacityUnitsDef.getData().getItems()
				.getItems(getParent().getValue());
	}

	public final DefSelectItems getFacilityAttachmentTypes() {
		return FacilityAttachmentTypeDef.getData().getItems();
	}

	public final DefSelectItems getPermitStatusDefsSearch() {
		return FacPermitStatusDef.getData().getItems();
	}

	public final List<SelectItem> getPermitStatusDefs() {
		return FacPermitStatusDef.getData().getItems()
				.getItems(getParent().getValue());
	}

	// Get a TableSorter for the Emission Unit Type help information
	public final TableSorter getEmissionUnitTypeWrapper() {

		TableSorter table = new TableSorter();
		try {
			// FacilityDAO dao = (FacilityDAO) DAOFactory.getDAO("FacilityDAO");
			table.setWrappedData(facilityDAO.retrieveEmissionUnitTypeDefs()
					.toArray());
		} catch (DAOException e) {
			logger.error("FacilityReference.getEmissionUnitTypeWrapper Error:"
					+ e.getMessage());
		}

		return table;
	}

	public final DefSelectItems getDehydrationTypeDefs() {
		return DehydrationTypeDef.getData().getItems();
	}

	public final DefSelectItems getPrimaryFuelTypeDefs() {
		return PrimaryFuelTypeDef.getData().getItems();
	}

	public final DefSelectItems getSecondaryFuelTypeDefs() {
		return SecondaryFuelTypeDef.getData().getItems();
	}

	public final DefSelectItems getUnitTypeDefs() {
		return UnitTypeDef.getData().getItems();
	}

	public final DefSelectItems getHeatContentFuelUnitsDefs() {
		return HeatContentFuelUnitsDef.getData().getItems();
	}

	public final DefSelectItems getFiringTypeDefs() {
		return FiringTypeDef.getData().getItems();
	}

	public final DefSelectItems getEquipTypeDefs() {
		return EquipTypeDef.getData().getItems();
	}

	public final DefSelectItems getEventTypeDefs() {
		return EventTypeDef.getData().getItems();
	}

	public final DefSelectItems getNamePlateRatingUnitsDefs() {
		return NamePlateRatingUnitsDef.getData().getItems();
	}

	public final DefSelectItems getEngineTypeDefs() {
		return EngineTypeDef.getData().getItems();
	}

	public final DefSelectItems getIncineratorTypeDefs() {
		return IncineratorTypeDef.getData().getItems();
	}

	public final DefSelectItems getBurnerTypeDefs() {
		return BurnerTypeDef.getData().getItems();
	}

	public final DefSelectItems getHmaMaxProductionRateUnitsDefs() {
		return HmaMaxProductionRateUnitsDef.getData().getItems();
	}

	public final DefSelectItems getCmxMaxProductionRateUnitsDefs() {
		return CmxMaxProductionRateUnitsDef.getData().getItems();
	}

	public final DefSelectItems getPowerSourceTypeDefs() {
		return PowerSourceTypeDef.getData().getItems();
	}

	public final DefSelectItems getFuelTypeDefs() {
		return FuelTypeDef.getData().getItems();
	}

	public final DefSelectItems getPlantTypeDefs() {
		return PlantTypeDef.getData().getItems();
	}

	public final DefSelectItems getBatchingTypeDefs() {
		return BatchingTypeDef.getData().getItems();
	}

	public final DefSelectItems getSruMaxAnnualThroughputUnitsDefs() {
		return SruMaxAnnualThroughputUnitsDef.getData().getItems();
	}

	public final DefSelectItems getIncineratorDesignCapacityUnitsDefs() {
		return IncineratorDesignCapacityUnitsDef.getData().getItems();
	}

	public final DefSelectItems getCshUnitTypeDefs() {
		return CshUnitTypeDef.getData().getItems();
	}

	public final DefSelectItems getCshMaxAnnualThroughputUnitDefs() {
		return CshMaxAnnualThroughputUnitDef.getData().getItems();
	}

	public final DefSelectItems getTnkMaxThroughputUnitDefs() {
		return TnkMaxThroughputUnitDef.getData().getItems();
	}

	public final DefSelectItems getTnkCapacityUnitDefs() {
		return TnkCapacityUnitDef.getData().getItems();
	}

	public final DefSelectItems getMaterialTypeDefs() {
		return MaterialTypeDef.getData().getItems();
	}

	public final DefSelectItems getTnkMaterialStoredTypeDefs() {
		return TnkMaterialStoredTypeDef.getData().getItems();
	}

	public final DefSelectItems getCkdUnitTypeDef() {
		return CkdUnitTypeDef.getData().getItems();
	}

	public final DefSelectItems getMaterialProducedDef() {
		return MaterialProducedDef.getData().getItems();
	}

	public final DefSelectItems getEguUnitTypeDef() {
		return EguUnitTypeDef.getData().getItems();
	}

	public final DefSelectItems getSepVesselTypeDef() {
		return SepVesselTypeDef.getData().getItems();
	}

	public final DefSelectItems getSebUnitTypeDef() {
		return SebUnitTypeDef.getData().getItems();
	}

	public final DefSelectItems getLudMaxAnnualThroughputUnitsDefs() {
		return LudMaxAnnualThroughputUnitsDef.getData().getItems();
	}

	public final DefSelectItems getCkdMaxAnnualThroughputUnitsDefs() {
		return CkdMaxAnnualThroughputUnitsDef.getData().getItems();
	}

	public final DefSelectItems getAptMaxThroughputUnitsDefs() {
		return AptMaxThroughputUnitsDef.getData().getItems();
	}

	public final DefSelectItems getCshMaxAnnualThroughputUnitsDefs() {
		return CshMaxAnnualThroughputUnitsDef.getData().getItems();
	}
	
	public final DefSelectItems getSiteRatingUnitsDefs() {
		return SiteRatingUnitsDef.getData().getItems();
	}
	
	public final DefSelectItems getFacilityRequestStatusDefs() {
		return FacilityRequestStatusDef.getData().getItems();
	}
	
	public final DefSelectItems getFlareDesignCapacityUnitsDefs() {
		return FlareDesignCapacityUnitsDef.getData().getItems();
	}
	
	public final DefSelectItems getContentTypeDefs() {
		return ContentTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getRegulatoryRequirementDefs() {
		return RegulatoryRequirementTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getSccCodesDefs() {
		return SccCodesDef.getData().getItems();
	}
	
	public final DefSelectItems getAbsApplicationMethodTypeDef() {
		return AbsApplicationMethodTypeDef.getData().getItems();
	}

	public final DefSelectItems getWeldingProcessTypeDef() {
		return WeldingProcessTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getDisMaxAnnualThroughputUnitsDefs() {
		
		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.DIS_CD);
	}
	
	public final DefSelectItems getOepMaxAnnualThroughputUnitsDefs() {

		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.OEP_CD);
	}

	public final DefSelectItems getMatMaxAnnualThroughputUnitsDefs() {

		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.MAT_CD);
	}

	public final DefSelectItems getMetMaxAnnualThroughputUnitsDefs() {
		
		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.MET_CD);
	}
	
	public final DefSelectItems getMixMaxAnnualThroughputUnitsDefs() {
		
		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.MIX_CD);
	}

	public final DefSelectItems getMldMaxAnnualThroughputUnitsDefs() {
		
		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.MLD_CD);
	}

	public final DefSelectItems getPelMaxAnnualThroughputUnitsDefs() {
		
		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.PEL_CD);
	}
	
	public final DefSelectItems getPrnMaxAnnualThroughputUnitsDefs() {
		
		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.PRN_CD);
	}
	
	public final DefSelectItems getSvuMaxAnnualThroughputUnitsDefs() {
		
		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.SVU_CD);
	}
	
	public final DefSelectItems getPamMaxAnnualThroughputUnitsDefs() {
		
		return MaxAnnualThroughputUnitEutypeDef
				.getMaxAnnualThroughputUnit(MaxAnnualThroughputUnitEutypeDef.PAM_CD);
	}

	public final DefSelectItems getAbsSubstrateBlastedTypeDef() {
		return AbsSubstrateBlastedTypeDef.getData().getItems();
	}

	public final DefSelectItems getAbsSubstrateRemovedTypeDef() {
		return AbsSubstrateRemovedTypeDef.getData().getItems();
	}
	public final DefSelectItems getAbsLeadConcentrationPctTypeDef() {
		return AbsLeadConcentrationPctTypeDef.getData().getItems();
	}

	public final DefSelectItems getFatUnitTypeDef() {
		return FatUnitTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getTkoPlatingTypeDef() {
		return TkoPlatingTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getTkoMetalTypeDef() {
		return TkoMetalTypeDef.getData().getItems();
	}

	public final DefSelectItems getPrnPressTypeDef() {
		return PrnPressTypeDef.getData().getItems();
	}

	public final DefSelectItems getPrnSubstrateFeedMethodTypeDef() {
		return PrnSubstrateFeedMethodTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getResApplicationMethodTypeDef() {
		return ResApplicationMethodTypeDef.getData().getItems();
	}

	public final DefSelectItems getDryOperationTypeDef() {
		return DryTypeOfOperationTypeDef.getData().getItems();
	}

	public final DefSelectItems getDryTypeOfDryCleaningTypeDef() {
		return DryTypeOfDryCleaningTypeDef.getData().getItems();
	}

	public final DefSelectItems getPamEquipTypeDefs() {
		return PamEquipTypeDef.getData().getItems();
	}

	public final DefSelectItems getSemEquipTypeDefs() {
		return SemEquipTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getSvcEquipTypeDefs() {
		return SvcEquipTypeDef.getData().getItems();
	}

	public final DefSelectItems getWweEquipTypeDefs() {
		return WweEquipTypeDef.getData().getItems();
	}

	public final DefSelectItems getOrdExplosiveTypeDef() {
		return OrdExplosiveTypeDef.getData().getItems();
	}
	public final DefSelectItems getRemContaminantTypeDef() {
		return RemContaminantTypeDef.getData().getItems();
	}
	public final DefSelectItems getRemContaminatedMaterialTypeDef() {
		return RemContaminatedMaterialTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getCotCoatingOperationTypeDef() {
		return CotCoatingOperationTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getCotMaterialBeingCoatedTypeDef() {
		return CotMaterialBeingCoatedTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getCotApplicationMethodTypeDef() {
		return CotApplicationMethodTypeDef.getData().getItems();
	}
	
	public final DefSelectItems getTnkMaterialStoredTypeLiquidDefs() {
		return TnkMaterialStoredTypeLiquidDef.getData().getItems();
	}

	public final DefSelectItems getEngTypeOfUseDefs() {
		return EngTypeOfUseDef.getData().getItems();
	}

	public final DefSelectItems getTnkTankLocationTypeDefs() { 
		return TnkTankLocationDef.getData().getItems();
	}
	
	public final DefSelectItems getAqdEmissionFactorGroupDefs() { 
		return AqdEmissionFactorGroupDef.getData().getItems();
	}
}
