package us.oh.state.epa.stars2.webcommon.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.document.CorrespondenceDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.genericIssuance.GenericIssuance;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.GenericIssuanceTypeDef;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

/*
 * Check business rules for permit prior to issuance.
 */
public class GeneralIssuanceValidation implements java.io.Serializable {
    private static Logger logger = Logger.getLogger(GeneralIssuanceValidation.class);

    public GeneralIssuanceValidation() {
        super();
    }
    
    public static List<ValidationMessage> issueValidation(GenericIssuance issuance) {

        ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        if (issuance == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance parameter is null.",
                                               ValidationMessage.Severity.ERROR));
            return messages;
        }
        
        ValidationMessage[] vms = issuance.validate();
        for (ValidationMessage vm : vms) {
            messages.add(vm);
        }

        if (issuance.getIssuanceTypeCd() != null 
            && (issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.PROPOSED_PTIO_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.PROPOSED_FINAL_PTIO_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.FINAL_PTIO_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.PROPOSED_PTI_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.PROPOSED_FINAL_PTI_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.FINAL_PTI_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.PROPOSED_PTO_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.FINAL_PTO_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.PROPOSED_TVPTO_REVOCATION)
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.FINAL_TVPTO_REVOCATION))) {

            messages.addAll(validateRPRRequest(issuance));
        }
        else if (issuance.getIssuanceTypeCd() != null 
                 && (issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.TIME_EXTENSION_PTI)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.TIME_EXTENSION_PTIO))) {

            messages.addAll(validateRPERequest(issuance));
        }
        else if (issuance.getIssuanceTypeCd() != null 
                 && (issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.FINAL_PTIO_DENIAL)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.FINAL_PTI_DENIAL)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.FINAL_TV_DENIAL))) {

            messages.addAll(validateDenial(issuance));
        }
        else if (issuance.getIssuanceTypeCd() != null 
                 && (issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_APPROVED)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_DENIED)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_APPROVED)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_DENIED)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.DELEGATION_DENIED)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.DELEGATION_NTV_APPROVED)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.DELEGATION_APPROVED)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.DELEGATION_DIRECTOR)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.PER_DUE_DATE_CHANGE)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_APPROVED_COND)
                     || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_APPROVED_COND))) {
            // Do nothing.
        }
        else {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance type code " + issuance.getIssuanceTypeCd()
                                               + " is not a recognized code.",
                                               ValidationMessage.Severity.ERROR));
        }

        return messages;
    }

    public static List<ValidationMessage> validateRPRRequest(GenericIssuance issuance) {

        ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        CorrespondenceDocument doc = issuance.getIssuanceDoc();
        if (doc == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance document is missing.",
                                               ValidationMessage.Severity.ERROR));
        }
        else {
            ValidationMessage[] vms = doc.validate();
            for (ValidationMessage vm : vms) {
                messages.add(vm);
            }
        }
        CorrespondenceDocument addrDoc = issuance.getAddrLabelDoc();
        if (addrDoc == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance address label document is missing.",
                                               ValidationMessage.Severity.WARNING));
        }
        else {
            ValidationMessage[] vms = addrDoc.validate();
            for (ValidationMessage vm : vms) {
                messages.add(vm);
            }
        }
        String docDesc = issuance.getIssuanceDocDesc();
        if (docDesc == null || docDesc.length() < 1) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing a document description.",
                                               ValidationMessage.Severity.ERROR));
        }

        if (issuance.getIssuanceDate() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing an issuance date.",
                                               ValidationMessage.Severity.ERROR));
        }

        if (issuance.getPublicNoticeText() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance has no public notice text.",
                                               ValidationMessage.Severity.ERROR));
        }

        if (issuance.getApplicationId() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing an application ID.",
                                               ValidationMessage.Severity.ERROR));
        }
        else {
            try {
                ApplicationService appBO 
                    = ServiceFactory.getInstance().getApplicationService();
                PermitService permitBO
                    = ServiceFactory.getInstance().getPermitService();

                Application giApp = appBO.retrieveApplication(issuance.getApplicationId());
                if (giApp == null) {
                    messages.add(new ValidationMessage("Application",
                                                       "Unable to retrieve RPR application.",
                                                       ValidationMessage.Severity.ERROR));
                }
                else {
                    if (giApp.getReceivedDate() == null) {
                        messages.add(new ValidationMessage("Application",
                                                           "RPR application has no revocation request date "
                                                           + "(application received date). ",
                                                           ValidationMessage.Severity.ERROR));
                    }

                    if (giApp.getPermitId() == null) {
                        messages.add(new ValidationMessage("GeneralIssuance",
                                                           "GenericIssuance is missing a permit ID.",
                                                           ValidationMessage.Severity.ERROR));
                    }
                    else {

                        PermitInfo revokedPermitInfo 
                            = permitBO.retrievePermit(giApp.getPermitId());
                        Permit revokedPermit = null;
                        if (revokedPermitInfo == null 
                            || (revokedPermit = revokedPermitInfo.getPermit()) == null) {
                            messages.add(new ValidationMessage("Application",
                                                               "Could not locate a permit to be revoked.",
                                                               ValidationMessage.Severity.ERROR));
                        }
                        else {
                            
                            HashMap<Integer, PermitEU> euMap = new HashMap<Integer, PermitEU>();
                            
                            List<PermitEU> peus = revokedPermit.getEus();
                            if (peus.isEmpty()) {
                                messages.add(new ValidationMessage("Permit",
                                                                   "Permit does not have any emission units.",
                                                                   ValidationMessage.Severity.ERROR));
                            }
                            else {
                                for (PermitEU peu : peus) {
                                    euMap.put(peu.getCorrEpaEMUID(), peu);
                                }
                            }
                            
                            List<ApplicationEU> aeus = giApp.getIncludedEus();
                            if (aeus.isEmpty()) {
                                messages.add(new ValidationMessage("Application",
                                                                   "Application must include at least one"
                                                                   + "emission unit.",
                                                                   ValidationMessage.Severity.ERROR));
                            }
                            
                            for (ApplicationEU aeu : aeus) {
                                if (aeu.getFpEU() == null) {
                                    messages.add(new ValidationMessage("Application",
                                                                       "Application emission unit missing "
                                                                       + "facility emission unit.",
                                                                       ValidationMessage.Severity.ERROR));
                                    continue;
                                }
                                if (!((RPRRequest) giApp).isRevokeEntirePermit()
                                    && (aeu.getEuText() == null || aeu.getEuText().length() < 1)) {
                                    messages.add(new ValidationMessage("Application",
                                                                       "Application emission unit "
                                                                       + aeu.getFpEU().getCompanyId()
                                                                       + " is missing a basis for revocation.",
                                                                       ValidationMessage.Severity.ERROR,
                                                                       aeu.getFpEU().getCompanyId()));
                                }
                                euMap.remove(aeu.getFpEU().getCorrEpaEmuId());
                            }

                            if (((RPRRequest) giApp).isRevokeEntirePermit()) {
                                if (((RPRRequest) giApp).getBasisForRevocation() == null 
                                    || ((RPRRequest) giApp).getBasisForRevocation().length() < 1) {
                                    messages.add(new ValidationMessage("Application",
                                                                       "Application must have a basis for "
                                                                       + "revocation if the entire permit "
                                                                       + "is being revoked.",
                                                                       ValidationMessage.Severity.ERROR));
                                }
                                if (!euMap.isEmpty()) {
                                    messages.add(new ValidationMessage("Application",
                                                                       "Application must include all permit"
                                                                       + "emission units if entire permit "
                                                                       + "is being revoked.",
                                                                       ValidationMessage.Severity.ERROR));
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                messages.add(new ValidationMessage("Application",
                                                   "Unable to retrieve applications. " + e.getMessage(),
                                                   ValidationMessage.Severity.ERROR));
            }
        }

        return messages;

    }

    public static List<ValidationMessage> validateRPERequest(GenericIssuance issuance) {

        ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        Permit extendedPermit = null;

        CorrespondenceDocument doc = issuance.getIssuanceDoc();
        if (doc == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance document is missing.",
                                               ValidationMessage.Severity.ERROR));
        }
        else {
            ValidationMessage[] vms = doc.validate();
            for (ValidationMessage vm : vms) {
                messages.add(vm);
            }
        }
        CorrespondenceDocument addrDoc = issuance.getAddrLabelDoc();
        if (addrDoc == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance address label document is missing.",
                                               ValidationMessage.Severity.WARNING));
        }
        else {
            ValidationMessage[] vms = addrDoc.validate();
            for (ValidationMessage vm : vms) {
                messages.add(vm);
            }
        }
        String docDesc = issuance.getIssuanceDocDesc();
        if (docDesc == null || docDesc.length() < 1) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing a document description.",
                                               ValidationMessage.Severity.ERROR));
        }

        if (issuance.getApplicationId() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing an application ID.",
                                               ValidationMessage.Severity.ERROR));
        }
        if (issuance.getApplicationId() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing an application ID.",
                                               ValidationMessage.Severity.ERROR));
        }
        else {
            try {
                ApplicationService appBO 
                    = ServiceFactory.getInstance().getApplicationService();
                PermitService permitBO
                    = ServiceFactory.getInstance().getPermitService();

                Application giApp = appBO.retrieveApplication(issuance.getApplicationId());
                if (giApp == null) {
                    messages.add(new ValidationMessage("Application",
                                                       "Unable to retrieve RPE application.",
                                                       ValidationMessage.Severity.ERROR));
                }
                else {
                    if (giApp.getReceivedDate() == null) {
                        messages.add(new ValidationMessage("Application",
                                                           "RPE application has no received date. ",
                                                           ValidationMessage.Severity.ERROR));
                    }

                    if (giApp.getPermitId() == null) {
                        messages.add(new ValidationMessage("GeneralIssuance",
                                                           "GenericIssuance is missing a permit ID.",
                                                           ValidationMessage.Severity.ERROR));
                    }
                    else {

                        PermitInfo extendedPermitInfo 
                            = permitBO.retrievePermit(giApp.getPermitId());
                        if (extendedPermitInfo == null 
                            || (extendedPermit = extendedPermitInfo.getPermit()) == null) {
                            messages.add(new ValidationMessage("Application",
                                                               "Could not locate a permit to be extended.",
                                                               ValidationMessage.Severity.ERROR));
                        }
                        else {

                            // LSRD 2.3.3-PTI_PTO_PTIO-22-5
                            //
                            // STARS2 shall provide the following QA/QC check for the RPE object at the DO/LAA
                            // Approval step of the workflow. One of the checks will examine the Begin
                            // Installation or Modification date for each of the EUs in the permit. If the
                            // date exists, then STARS2 shall generate a warning message with wording to the
                            // effect "Installation or modification of one or more emissions units in this permit
                            // has already begun, a permit extension request is not applicable in this case".
                            //
                            List<PermitEU> peus = extendedPermit.getEus();
                            if (peus.isEmpty()) {
                                messages.add(new ValidationMessage("Permit",
                                                                   "Permit does not have any emission units.",
                                                                   ValidationMessage.Severity.ERROR));
                            }
                            else {
                                Facility facility = extendedPermitInfo.getCurrentFacility();
                                for (PermitEU peu : peus) {
                                    EmissionUnit feu = facility.getMatchingEmissionUnit(peu.getCorrEpaEMUID());
                                    if (feu.getEuInstallDate() != null 
                                            && extendedPermit.getFinalIssueDate().before(feu.getEuInstallDate())) {
                                        messages.add(new ValidationMessage("Facility",
                                                                           "Installation or modification of one or "
                                                                           + "more emissions units in this permit has "
                                                                           + "already begun. A permit extension "
                                                                           + "request is not applicable in this case.",
                                                                           ValidationMessage.Severity.ERROR, 
                                                                           feu.getEpaEmuId()));
                                        break;
                                    }
                                }
                            }

                            // LSRD 2.3.3-PTI_PTO_PTIO-22-6:
                            //
                            // STARS2 shall provide the following QA/QC check at the DO/LAA Approval step of 
                            // the workflow to determine if an RPE record with the following characteristics
                            // already exists:
                            // -> Same facility id
                            // -> Same referenced PTI/PTIO permit
                            // -> Does not have a disposition of 'Denied’
                            //
                            // If such an RPE record exists, STARS2 shall test the disposition attribute of the
                            // RPE record. If it is equal to 'Granted’, then STARS2 shall generate a warning
                            // message with wording to the effect "An extension has already been granted for this
                            // permit, a maximum of one extension can be granted". If the disposition is equal to
                            // 'Pending’, then STARS2 shall generate a warning message (warning only, not an error)
                            // with wording to the effect "There already exists a pending RPE record for this permit".
                            //
                            if (extendedPermit != null && extendedPermit.getFacilityId() != null) {
                                ApplicationSearchResult[] asr 
                                    = appBO.retrieveApplicationSearchResults(null, null,extendedPermit.getFacilityId(),
                                            null, null, null, ApplicationTypeDef.RPE_REQUEST, null, false, null, 
                                            null, null, true);
                                if (asr.length > 1) {
                                    boolean hasPending = false;
                                    for (ApplicationSearchResult sr : asr) {
                                        Application anApp = appBO.retrieveApplication(sr.getApplicationId());
                                        if (anApp instanceof RPERequest) {
                                            if (((RPERequest) anApp).getPermitId() != null
                                                && ((RPERequest) anApp).getPermitId().equals(extendedPermit.getPermitID())) {
                                                if (((RPERequest) anApp).getDispositionFlag() == null  && !hasPending) {
                                                    messages.add(new ValidationMessage("Application",
                                                                                       "There already exists a "
                                                                                       + "pending RPE record for "
                                                                                       + "this permit.",
                                                                                       ValidationMessage.Severity.WARNING));
                                                    hasPending = true;
                                                }
                                                else if  (((RPERequest) anApp).getDispositionFlag().equals(RPERequest.DENIED)
                                                     || ((RPERequest) anApp).getDispositionFlag().equals(RPERequest.DEAD_ENDED)) {
                                                    continue;
                                                }
                                                else if (((RPERequest) anApp).getDispositionFlag().equals(RPERequest.ISSUED)) {
                                                    messages.add(new ValidationMessage("Application",
                                                                                       "An extension has already "
                                                                                       + "been granted for this "
                                                                                       + "permit. A maximum of one "
                                                                                       + "extension can be granted.",
                                                                                       ValidationMessage.Severity.WARNING));
                                                }
                                            }
                                            else {
                                                continue;
                                            }
                                        }
                                        else {
                                            messages.add(new ValidationMessage("Application",
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
                }
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
                messages.add(new ValidationMessage("Application",
                                                   "Unable to retrieve applications. " + e.getMessage(),
                                                   ValidationMessage.Severity.ERROR));
            }
        }

        if (issuance.getIssuanceDate() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing an issuance date.",
                                               ValidationMessage.Severity.ERROR));
        }

        if (issuance.getPublicNoticeText() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance has no public notice text.",
                                               ValidationMessage.Severity.ERROR));
        }

        return messages;

    }

    public static List<ValidationMessage> validateDenial(GenericIssuance issuance) {

        ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        CorrespondenceDocument doc = issuance.getIssuanceDoc();
        if (doc == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance document is missing.",
                                               ValidationMessage.Severity.ERROR));
        }
        else {
            ValidationMessage[] vms = doc.validate();
            for (ValidationMessage vm : vms) {
                messages.add(vm);
            }
        }
        CorrespondenceDocument addrDoc = issuance.getAddrLabelDoc();
        if (addrDoc == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance address label document is missing.",
                                               ValidationMessage.Severity.WARNING));
        }
        else {
            ValidationMessage[] vms = addrDoc.validate();
            for (ValidationMessage vm : vms) {
                messages.add(vm);
            }
        }
        String docDesc = issuance.getIssuanceDocDesc();
        if (docDesc == null || docDesc.length() < 1) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing a document description.",
                                               ValidationMessage.Severity.ERROR));
        }

        if (issuance.getIssuanceDate() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance is missing an issuance date.",
                                               ValidationMessage.Severity.ERROR));
        }

        if (issuance.getPublicNoticeText() == null) {
            messages.add(new ValidationMessage("GeneralIssuance",
                                               "GenericIssuance has no public notice text.",
                                               ValidationMessage.Severity.ERROR));
        }

        return messages;

    }

}
