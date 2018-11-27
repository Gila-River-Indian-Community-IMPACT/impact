package us.oh.state.epa.stars2.webcommon.bean;

import us.oh.state.epa.stars2.def.AirProgramDef;
import us.oh.state.epa.stars2.def.AirProgramsDef;
import us.oh.state.epa.stars2.def.CetaStackTestAuditsDef;
import us.oh.state.epa.stars2.def.CetaStackTestCategoryDef;
import us.oh.state.epa.stars2.def.CetaStackTestMethodDef;
import us.oh.state.epa.stars2.def.CetaStackTestResultsDef;
import us.oh.state.epa.stars2.def.CetaStackTestTestingMethodDef;
import us.oh.state.epa.stars2.def.ComplianceNESHAPPollutantDef;
import us.oh.state.epa.stars2.def.ComplianceStatusDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.EnfMilestoneCaseSettlementsDef;
import us.oh.state.epa.stars2.def.FceInspectionTypeDef;
import us.oh.state.epa.stars2.def.GovtFacilityTypeDef;
import us.oh.state.epa.stars2.def.MonthDef;
import us.oh.state.epa.stars2.def.OEPAViolationTypeDef;
import us.oh.state.epa.stars2.def.SiteVisitNoEmissionTestTypeDef;
import us.oh.state.epa.stars2.def.SiteVisitTypeDef;
import us.oh.state.epa.stars2.def.SiteVisitVisibleEmissionsDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.wy.state.deq.impact.def.FCEInspectionReportStateDef;
import us.wy.state.deq.impact.def.OverallInspectionComplianceStatusDef;
import us.wy.state.deq.impact.def.PermitConditionComplianceStatusDef;
import us.wy.state.deq.impact.def.SkyConditionsDef;
import us.wy.state.deq.impact.def.WindDirectionDef;

@SuppressWarnings("serial")
public class ComplianceEvaluationDefs extends AppBase {
    public final DefSelectItems getGovtFacilityTypes() {
        return GovtFacilityTypeDef.getData().getItems();
    }
    
/*    public final DefSelectItems getInspectionClassDefs() {
        return InspectionClassDef.getData().getItems();
    } */
    
    public final DefSelectItems getAirProgramDefs() {
        return AirProgramDef.getData().getItems();
    }
    
    public final DefSelectItems getComplianceStatusDefs() {
        return ComplianceStatusDef.getData().getItems();
    }
    
    public final DefSelectItems getAirPrograms() {
        return AirProgramsDef.getData().getItems();
    }
    
//    public final DefSelectItems getCompliancePollutants() {
//        return CompliancePollutantDef.getData().getItems();
//    }
    
    public final DefSelectItems getComplianceNESHAPPollutants() {
        return ComplianceNESHAPPollutantDef.getData().getItems();
    }
    
    public final DefSelectItems getOepaViolationTypes() {
        return OEPAViolationTypeDef.getData().getItems();
    }
    
    /*public final DefSelectItems getAfsViolationTypeCodes() {
        return AFSViolationTypeCodesDef.getData().getItems();
    }*/
    
    /*public final DefSelectItems getAfsEnforcementTypeDef() {
        return AFSEnforcementTypeDef.getData().getItems();
    }*/
    
    /*public final DefSelectItems getAfsActionDiscoveryTypeDef() {
        return AFSActionDiscoveryTypeDef.getData().getItems();
    }*/
    
    public final DefSelectItems getSiteVisitTypeDef() {
        return SiteVisitTypeDef.getData().getItems();
    }
    
    public final DefSelectItems getEnfMilestoneCaseSettlements() {
    	return EnfMilestoneCaseSettlementsDef.getData().getItems();
    }
    
    public final DefSelectItems getMonthDef() {
    	return MonthDef.getData().getItems();
    }
    
    public final DefSelectItems getEmissionsTestStateDef() {
        return EmissionsTestStateDef.getData().getItems();
    }
    
    public final DefSelectItems getSiteVisitNoEmissionTestTypeDef() {
        return SiteVisitNoEmissionTestTypeDef.getData().getItems();
    }
    
    public final DefSelectItems getStackTestMethodDef() {
        return CetaStackTestMethodDef.getData().getItems();
    }
    
//    public final DefSelectItems getFceReqInNextDef() {
//        return FceReqInNextDef.getData().getItems();
//    }
//    
    public final DefSelectItems getStackTestResultsDef() {
        return CetaStackTestResultsDef.getData().getItems();
    }
    
    public final DefSelectItems getStackTestAuditsDef() {
        return CetaStackTestAuditsDef.getData().getItems();
    }
    
    public final DefSelectItems getSiteVisitVEDef() {
        return SiteVisitVisibleEmissionsDef.getData().getItems();
    }
    
    public final DefSelectItems getInspectionTypeDefs() {
        return FceInspectionTypeDef.getData().getItems();
    }
    
    public final DefSelectItems getStackTestCategoryDef() {
        return CetaStackTestCategoryDef.getData().getItems();
    }
    
    public final DefSelectItems getStackTestTestingMethodDef() {
        return CetaStackTestTestingMethodDef.getData().getItems();
    }
    
    public final DefSelectItems getFceInspectionReportStateDef() {
        return FCEInspectionReportStateDef.getData().getItems();
    }
    
    public final DefSelectItems getSkyConditionsDef() {
        return SkyConditionsDef.getData().getItems();
    }
    
    public final DefSelectItems getWindDirectionDef() {
        return WindDirectionDef.getData().getItems();
    }
    
    public final DefSelectItems getPermitConditionComplianceStatusDef() {
        return PermitConditionComplianceStatusDef.getData().getItems();
    }
    
    public final DefSelectItems getOverallInspectionComplianceStatusDef() {
    	return OverallInspectionComplianceStatusDef.getData().getItems();
    }
}
