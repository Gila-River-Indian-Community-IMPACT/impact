package us.oh.state.epa.stars2.webcommon.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEUCategory;
import us.oh.state.epa.stars2.def.CompanyOutcomeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef;
import us.oh.state.epa.stars2.def.NSRBillingFeeTypeDef;
import us.oh.state.epa.stars2.def.NSRInvoicePaidDef;
import us.oh.state.epa.stars2.def.OffsetTrackingAttainmentStandardDef;
import us.oh.state.epa.stars2.def.OffsetTrackingNonAttainmentAreaDef;
import us.oh.state.epa.stars2.def.PTIOGeneralPermitTypeDef;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PermitActionTypeDef;
import us.oh.state.epa.stars2.def.PermitDocIssuanceStageDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.PermitFeeAdjustmentDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitReceivedCommentsDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PublicNoticeTypeDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.def.USEPAOutcomeDef;
import us.oh.state.epa.stars2.util.Pair;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.ELMap;
import us.wy.state.deq.impact.def.ComplianceTrackingEventTypeDef;
import us.wy.state.deq.impact.def.NewspaperDef;
import us.wy.state.deq.impact.def.PermitConditionCategoryDef;
import us.wy.state.deq.impact.def.PermitConditionStatusDef;
import us.wy.state.deq.impact.def.PermitConditionSupersedenceStatusDef;
import us.wy.state.deq.impact.def.PermitLevelStatusDef;

@SuppressWarnings("serial")
public class PermitReference extends AppBase {
    private List<SelectItem> permitObjectTypes = new ArrayList<SelectItem>(5);
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

    public PermitReference() {
        //permitObjectTypes.add(new SelectItem(new Pair<Class<? extends Permit>, Boolean>(
        //        PTIOPermit.class, true), "PTI"));
        permitObjectTypes.add(new SelectItem(new Pair<Class<? extends Permit>, Boolean>(
                PTIOPermit.class, false), PermitTypeDef.NSR));
        permitObjectTypes.add(new SelectItem(new Pair<Class<? extends Permit>, Boolean>(
                TVPermit.class, true),"Title V"));
    }

    /*
     * Getters for SelectItem's
     */
    public final List<SelectItem> getPermitObjectTypes() {
        return permitObjectTypes;
    }

    public final List<SelectItem> getPermitTypes() {
        List<SelectItem> ts = PermitTypeDef.getData().getItems().getItems(
                getParent().getValue());
        List<SelectItem> rs = new ArrayList<SelectItem>();
        for (SelectItem si : ts){
            //if (si.getValue().equals(PermitTypeDef.RPE) || si.getValue().equals(PermitTypeDef.RPR))
            if (si.getValue().equals(PermitTypeDef.RPR))

                continue;

            rs.add(si);
        }
        return rs;
    }

    public final List<SelectItem> getCompanyOutcomeDefs() {
        return CompanyOutcomeDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getUsepaOutcomeDefs() {
        return USEPAOutcomeDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getPermitStatusDefs() {
        return PermitStatusDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getGeneralPermitTypeDefs() {
        return PTIOGeneralPermitTypeDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getNspsSubpartDefs() {
        return PTIONSPSSubpartDef.getData().getItems().getItems("");
    }

    public final List<SelectItem> getNeshapsSubpartDefs() {
        return PTIONESHAPSSubpartDef.getData().getItems().getItems("");
    }

    public final List<SelectItem> getMactSubpartDefs() {
        return PTIOMACTSubpartDef.getData().getItems().getItems("");
    }

    public final DefSelectItems getPermitIssuanceStageDefs() {
        return PermitDocIssuanceStageDef.getData().getItems();
    }

    public final List<SelectItem> getPermitGlobalStatusDefs() {
        return PermitGlobalStatusDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getIssuanceStatusDefs() {
        return IssuanceStatusDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getPerDueDateDefs() {
        return PTIOPERDueDateDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getPermitDocTypeDefs() {
        return PermitDocTypeDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getPermitFeeAdjustmentDefs() {
        return PermitFeeAdjustmentDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getPublicNoticeTypes() {
        return PublicNoticeTypeDef.getData().getItems().getItems(
                getParent().getValue());
    }
    
    public final List<SelectItem> getPermitActionTypes() {
        return PermitActionTypeDef.getData().getItems().getItems(
                getParent().getValue());
    }
    
    public final List<SelectItem> getInvoicePaidDefs() {
        return NSRInvoicePaidDef.getData().getItems().getItems(
                getParent().getValue());
    }
    
    public final List<SelectItem> getReceivedCommentsTypes() {
        return PermitReceivedCommentsDef.getData().getItems().getItems(
                getParent().getValue());
    }

    public final List<SelectItem> getEuCategories() {
        List<SelectItem> si = new ArrayList<SelectItem>();
        try {
            SCEUCategory[] cs = getInfrastructureService().retrieveEUCategories();
            for (SCEUCategory s : cs)
                si.add(new SelectItem(s.getCategoryId(), s.getCategoryDsc()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return si;
    }

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public final Map<Permit, String> getPermitType() {
        return new ELMap() {
            public Object get(Object object) {
                return ((Permit) object).getPermitType();
            }
        };
    }

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public final Map<String, List<SelectItem>> getTemplateDocDefs() {
        return new ELMap() {
            public Object get(Object object) {
                String templateDocTypeCD = (String) object;
                return TemplateDocTypeDef.getTemplates(templateDocTypeCD);
            }
        };
    }

    /*
     * 
     */
    public final String getIntroPackageCD() {
        return PermitDocTypeDef.INTRO_PACKAGE;
    }

    public final String getIssuanceDocCD() {
        return PermitDocTypeDef.ISSUANCE_DOCUMENT;
    }

/*    public final String getStatementBasisCD() {
        return PermitDocTypeDef.STATEMENT_BASIS;
    }

    public final String getResponseToCommentsCD() {
        return PermitDocTypeDef.RESPONSE_TO_COMMENTS;
    }*/

    public final String getAddressLabelsCD() {
        return PermitDocTypeDef.ADDRESS_LABELS;
    }

    public final String getFaxCoverCD() {
        return PermitDocTypeDef.FAX_COVER_SHEET;
    }
    
    public final String getPermitStrategySummaryWriteupCD() {
        return PermitDocTypeDef.PERMIT_STRATEGY_SUMMARY_WRITE_UP;
    }

    public final String getFormBCD() {
        return PermitDocTypeDef.FORM_B;
    }

    public final String getPublicNoticeCD() {
        return PermitDocTypeDef.PUBLIC_NOTICE;
    }

    public final String getHearingNoticeCD() {
        return PermitDocTypeDef.HEARING_NOTICE;
    }

    public final String getInstallCertCD() {
        return PermitDocTypeDef.INSTALLATION_CERTIFICATE;
    }

    public final String getDraftFlag() {
        return PermitDocIssuanceStageDef.DRAFT;
    }
    
    public final String getDenialFlag() {
        return PermitDocIssuanceStageDef.DENIAL;
    }

    public final String getFinalFlag() {
        return PermitDocIssuanceStageDef.FINAL;
    }

    public final String getPpFlag() {
        return PermitDocIssuanceStageDef.PROPOSED;
    }
    
    //public final String getPppFlag() {
    //    return PermitDocIssuanceStageDef.PRELIMINARY_PROPOSED;
    //}
    
    public final String getAllFlag() {
        return PermitDocIssuanceStageDef.ALL;
    }

    public final String getIssuanceStatusIssued() {
        return IssuanceStatusDef.ISSUED;
    }

    public final String getIssuanceStatusReady() {
        return IssuanceStatusDef.READY;
    }

    public final String getIssuanceStatusNotReady() {
        return IssuanceStatusDef.NOT_READY;
    }
    
    public final String getIssuanceStatusSkipped() {
        return IssuanceStatusDef.SKIPPED;
    }
    
    public final String getDraftIssuanceType() {
        return PermitIssuanceTypeDef.Draft;
    }
    
    public final String getDeniedIssuanceType() {
        return PermitIssuanceTypeDef.DENIED;
    }
    
    public final String getPpIssuanceType() {
        return PermitIssuanceTypeDef.PP;
    }
    
    //public final String getPppIssuanceType() {
    //    return PermitIssuanceTypeDef.PPP;
    //}
    
    public final String getFinalIssuanceType() {
        return PermitIssuanceTypeDef.Final;
    }
    
    public final List<SelectItem> getNewspaperDefs() {
        return NewspaperDef.getData().getItems().getItems(
                getParent().getValue());
    }
    
    public final List<SelectItem> getNSRBillingChargePaymentTypeDefs() {
        return NSRBillingChargePaymentTypeDef.getData().getItems().getItems(
                getParent().getValue());
    }
    
    public final List<SelectItem> getNSRBillingFeeTypeDefs() {
        return NSRBillingFeeTypeDef.getData().getItems().getItems(
                getParent().getValue());
    }
    
    public final DefSelectItems getPermitActionTypeDefs() {
    	return PermitActionTypeDef.getData().getItems();
	}
    
    public final DefSelectItems getOffsetTrackingNonAttainmentAreaDefs() {
    	return OffsetTrackingNonAttainmentAreaDef.getData().getItems();
    }
    
    public final DefSelectItems getOffsetTrackingAttainmentStandardDefs() {
    	return OffsetTrackingAttainmentStandardDef.getData().getItems();
    }
    
    public final DefSelectItems getPermitCategoryDefs() {
        return PermitConditionCategoryDef.getData().getItems();
    }
    
    public final DefSelectItems getPermitLevelStatusDefs() {
    	return PermitLevelStatusDef.getData().getItems();
    }
    
    public final DefSelectItems getPermitConditionCategoryDefs() {
    	return PermitConditionCategoryDef.getData().getItems();
    }
    
    public final DefSelectItems getPermitConditionStatusDefs() {
    	return PermitConditionStatusDef.getData().getItems();
    }
    
    public final DefSelectItems getComplianceTrackingEventTypeDefs() {
    	return ComplianceTrackingEventTypeDef.getData().getItems();
    }

    public final DefSelectItems getConditionSupersedenceStatusDefs() {
    	return PermitConditionSupersedenceStatusDef.getData().getItems();
    }
    
    public final List<SelectItem> getPermitConditionSupersedenceStatusDefs() {
    	return PermitConditionSupersedenceStatusDef.getData().getItems().getItems(
                getParent().getValue());
    }
    
}
