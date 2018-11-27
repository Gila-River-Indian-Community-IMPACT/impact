package us.oh.state.epa.stars2.def;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * IS_ISSUANCE_TYPE_DEF table.
 */
public class GenericIssuanceTypeDef {

    public static final String PROPOSED_PTIO_REVOCATION = "PTIO_P_RV";
    public static final String FINAL_PTIO_REVOCATION = "PTIO_F_RV";
    public static final String PROPOSED_FINAL_PTIO_REVOCATION = "PTIO_PF_RV";
    
    public static final String PROPOSED_PTI_REVOCATION = "PTI_P_RV";
    public static final String FINAL_PTI_REVOCATION = "PTI_F_RV";
    public static final String PROPOSED_FINAL_PTI_REVOCATION = "PTI_PF_RV";
    
    public static final String PROPOSED_PTO_REVOCATION = "PTO_P_RV";
    public static final String FINAL_PTO_REVOCATION = "PTO_F_RV";
    
    public static final String PROPOSED_TVPTO_REVOCATION = "TVPTO_P_RV";
    public static final String FINAL_TVPTO_REVOCATION = "TVPTO_F_RV";
    
   public static final String TIME_EXTENSION_PTI = "PTI_TE";
    public static final String TIME_EXTENSION_PTIO = "PTIO_TE";
    
    //public static final String FINAL_TIV_DENIAL = "TIVPTO_DENI";
    public static final String FINAL_TV_DENIAL = "TVPTO_DENI";
    public static final String FINAL_PTI_DENIAL = "PTI_DENI";
    public static final String FINAL_PTIO_DENIAL = "PTIO_DENI";

    public static final String INTENT_TO_RELOCATE_APPROVED = "ITR_PSRA";
    public static final String INTENT_TO_RELOCATE_APPROVED_COND = "ITR_PSRA_C";
    public static final String INTENT_TO_RELOCATE_DENIED = "ITR_PSRD";
    public static final String SITE_PRE_APPROVAL_APPROVED = "ITR_PSRP";
    public static final String SITE_PRE_APPROVAL_APPROVED_COND = "ITR_PSRP_C";
    public static final String SITE_PRE_APPROVAL_DENIED = "ITR_PSPD";
    
    public static final String DELEGATION_DENIED = "DOR_DENIED";
    public static final String DELEGATION_NTV_APPROVED = "DOR_NTV_APPR";
    public static final String DELEGATION_APPROVED = "DOR_APPR";
    public static final String DELEGATION_DIRECTOR = "DOR_DIR";
    
    public static final String PER_DUE_DATE_CHANGE = "PER_DUE_DATE";
    
    private static LinkedHashMap<String, SelectItem> sis;
    private static final String defName = "GenericIssuanceTypeDef";
    private static final String defWRName = "GenericIssuanceWrapnWR";
    private static final String defPNName = "GenericIssuanceWrapnPN";
    private static final String defTemplateName = "GenericIssuanceTemplate";
    private static final String defWrapnName = "GenericIssuanceWrapnCD";    //wrapn CD value
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("is_issuance_type_def", "issuance_type_cd",
                            "issuance_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
            sis = new LinkedHashMap<String, SelectItem>();
            for (SelectItem si : data.getItems().getAllItems())
                sis.put(si.getValue().toString(), si);
        }

        return data;
    }

    public static DefData getWRData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defWRName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("is_issuance_type_def", "issuance_type_cd",
                            "wrapn_wr_flag", "deprecated");

            cfgMgr.addDef(defWRName, data);
        }

        return data;
    }

    public static DefData getPNData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defPNName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("is_issuance_type_def", "issuance_type_cd",
                            "wrapn_pn_flag", "deprecated");

            cfgMgr.addDef(defPNName, data);
        }

        return data;
    }
    
    public static DefData getWrapnData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defWrapnName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("is_issuance_type_def", "issuance_type_cd",
                            "wrapn_cd", "deprecated");

            cfgMgr.addDef(defWrapnName, data);
        }

        return data;
    }

    public static List<SelectItem> getTypes(BaseDB in, Permit permit) {
        List<SelectItem> ret = new ArrayList<SelectItem>();
        getData();
/*
        if (in instanceof RPRRequest) {
            if (permit instanceof PTIOPermit) {
                if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.SPTO)) {
                    ret.add(sis.get(PROPOSED_PTO_REVOCATION));
                    ret.add(sis.get(FINAL_PTO_REVOCATION));
                }
                else if (((PTIOPermit) permit).isTv()) {
                    ret.add(sis.get(PROPOSED_PTI_REVOCATION));
                    ret.add(sis.get(FINAL_PTI_REVOCATION));
                    ret.add(sis.get(PROPOSED_FINAL_PTI_REVOCATION));
                }
                else {
                    ret.add(sis.get(PROPOSED_PTIO_REVOCATION));
                    ret.add(sis.get(FINAL_PTIO_REVOCATION));
                    ret.add(sis.get(PROPOSED_FINAL_PTIO_REVOCATION));
                }
            }
            else if (permit instanceof TVPermit) {
                ret.add(sis.get(PROPOSED_TVPTO_REVOCATION));
                ret.add(sis.get(FINAL_TVPTO_REVOCATION));
            }
        }
        else if (in instanceof RPERequest) {
            if (permit instanceof PTIOPermit && !((PTIOPermit) permit).isTv()) {
                ret.add(sis.get(TIME_EXTENSION_PTIO));
            }
            else {
                ret.add(sis.get(TIME_EXTENSION_PTI));
            }
        } 
        else */ if (in instanceof Permit) {
            //if (permit instanceof TIVPermit){
            //    ret.add(sis.get(FINAL_TIV_DENIAL));
           // }
           // else 
            	if (permit instanceof TVPermit) {
                ret.add(sis.get(FINAL_TV_DENIAL));
            }
            //else if (((PTIOPermit)permit).isTv()) {
            //    ret.add(sis.get(FINAL_PTI_DENIAL));
            //}
            else {
                ret.add(sis.get(FINAL_PTIO_DENIAL));
            }
        } else if (in instanceof RelocateRequest) {
            //which type we include depends on the state of the request

            RelocateRequest rr = (RelocateRequest)in;
            if (rr.getApplicationTypeCD().equals(RelocationTypeDef.INTENT_TO_RELOCATE)) {
                if (rr.getJfoRecommendation().equals(RelocationJFODef.APPROVE)) {
                    ret.add(sis.get(INTENT_TO_RELOCATE_APPROVED));
                } else if (rr.getJfoRecommendation().equals(RelocationJFODef.APPROVE_WITH_CONDITIONS)) {
                    ret.add(sis.get(INTENT_TO_RELOCATE_APPROVED_COND));
                } else if (rr.getJfoRecommendation().equals(RelocationJFODef.DENY)) {
                    ret.add(sis.get(INTENT_TO_RELOCATE_DENIED));
                }
            } else if (rr.getApplicationTypeCD().equals(RelocationTypeDef.SITE_PRE_APPROVAL)) {
                if (rr.getJfoRecommendation().equals(RelocationJFODef.APPROVE)) {
                    ret.add(sis.get(SITE_PRE_APPROVAL_APPROVED));
                } else if (rr.getJfoRecommendation().equals(RelocationJFODef.APPROVE_WITH_CONDITIONS)) {
                    ret.add(sis.get(SITE_PRE_APPROVAL_APPROVED_COND));
                } else if (rr.getJfoRecommendation().equals(RelocationJFODef.DENY)) {
                    ret.add(sis.get(SITE_PRE_APPROVAL_DENIED));
                }
            }
        } else if (in instanceof DelegationRequest) {
            DelegationRequest dr = (DelegationRequest)in;
            if (dr.getRequestDispositionCd()!=null 
                    && dr.getRequestDispositionCd().equals(DelegationDispositionDef.DELEGATION_DISPOSITION_DENY)) {
                ret.add(sis.get(DELEGATION_DENIED));
            } else if (dr.isTitleV() && dr.isFac25MilOr250EmpQualified()) {
                ret.add(sis.get(DELEGATION_APPROVED));
            } else if (dr.isTitleV() && !dr.isFac25MilOr250EmpQualified()) {
                ret.add(sis.get(DELEGATION_DIRECTOR));
            } else if (!dr.isTitleV()) {
                ret.add(sis.get(DELEGATION_NTV_APPROVED));
            }
        }
        return ret;
    }

    public static DefData getTemplateCD() {

        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defTemplateName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("is_issuance_type_def", "issuance_type_cd",
                            "template_doc_type_cd", "deprecated");
            
            cfgMgr.addDef(defTemplateName, data);
        }
        return data;
    }
    
}
