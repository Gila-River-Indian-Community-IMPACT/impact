
package us.oh.state.epa.stars2.bo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.bo.helpers.InfrastructureHelper;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.CorrespondenceDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.genericIssuance.GenericIssuance;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.GenericIssuanceTypeDef;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PTIORegulatoryStatus;
import us.oh.state.epa.stars2.def.PTIRegulatoryStatus;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

@Transactional(rollbackFor=Exception.class)
@Service
public class GenericIssuanceBO extends BaseBO implements GenericIssuanceService {

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public GenericIssuance retrieveGenericIssuance(Integer issuanceId) 
        throws DAOException {

        GenericIssuance gi = genericIssuanceDAO().retrieveGenericIssuance(issuanceId);

        if (gi != null && gi.getIssuanceDocId() != null) {
            Document doc = documentDAO().retrieveDocument(gi.getIssuanceDocId());
            CorrespondenceDocument gid = new CorrespondenceDocument(doc);
            gi.setIssuanceDoc(gid);
        }
        if (gi != null && gi.getAddrLabelDocId() != null) {
            Document doc = documentDAO().retrieveDocument(gi.getAddrLabelDocId());
            CorrespondenceDocument gid = new CorrespondenceDocument(doc);
            gi.setAddrLabelDoc(gid);
        }

        return gi;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public List<GenericIssuance> searchGenericIssuances(String facilityId,
                                                        Integer applicationId,
                                                        Integer permitId,
                                                        String typeCd) 
        throws DAOException {

        List<GenericIssuance> allGis 
            = genericIssuanceDAO().searchGenericIssuances(facilityId, applicationId,
                                                          permitId, typeCd, 0);
        
        for (GenericIssuance gi : allGis) {
            if (gi != null && gi.getIssuanceDocId() != null) {
                Document doc = documentDAO().retrieveDocument(gi.getIssuanceDocId());
                CorrespondenceDocument gid = new CorrespondenceDocument(doc);
                gi.setIssuanceDoc(gid);
            }
        }

        return allGis;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public GenericIssuance createGenericIssuance(GenericIssuance issuance) 
        throws DAOException {
 
        Transaction trans = TransactionFactory.createTransaction();

        GenericIssuance gi = null;

        try {
            saveDocument(issuance, trans);

            gi = genericIssuanceDAO(trans).createGenericIssuance(issuance);
            trans.complete();
        }
        catch (DAOException de) {
            cancelTransaction(trans, de);
        } 
        finally {
            closeTransaction(trans);
        }

        return gi;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void modifyGenericIssuance(GenericIssuance issuance) 
        throws DAOException {

        Transaction trans = TransactionFactory.createTransaction();

        try {
            modifyGenericIssuance(issuance, trans);
            trans.complete();
        }
        catch (DAOException de) {
            cancelTransaction(trans, de);
        } 
        finally {
            closeTransaction(trans);
        }

        return;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ValidationMessage[] issueGenericIssuance(GenericIssuance issuance, int userId, User user) 
        throws DAOException {

        ArrayList<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        if (issuance == null) {
            throw new DAOException("GenericIssuance paramter is null.");
        }

        String issuanceType = issuance.getIssuanceTypeCd();
        if (issuanceType == null || issuanceType.length() < 1) {
            throw new DAOException("GenericIssuance paramter is missing an issuanceTypeCd.");
        }

        ApplicationService appBO = null;
        PermitService permitBO = null;
        FacilityService facilityBO = null;
        InfrastructureService infraBO = null;
        CorrespondenceService corrBO = null;

        Application giApp = null;
        Permit giPermit = null;
        Permit oldPermit = null;
        boolean giAppNeedsUpdate = false;
        boolean giPermitNeedsUpdate = false;
        boolean oldPermitNeedsUpdate = false;

        Invoice newInv = null;
        Correspondence correspondence = null;

        try {
            facilityBO = ServiceFactory.getInstance().getFacilityService();
            infraBO = ServiceFactory.getInstance().getInfrastructureService();
            corrBO = ServiceFactory.getInstance().getCorrespondenceService();

            appBO = ServiceFactory.getInstance().getApplicationService();
            giApp = appBO.retrieveApplication(issuance.getApplicationId());
            if (issuance.getPermitId() != null){
                permitBO = ServiceFactory.getInstance().getPermitService();
                giPermit = permitBO.retrievePermit(issuance.getPermitId()).getPermit();
            }
            if (giApp instanceof RPERequest){
                if (((RPERequest)giApp).getPermitId() != null) {
                    oldPermit = permitBO.retrievePermit(((RPERequest)giApp).getPermitId()).getPermit();
                }
                else {
                    throw new DAOException("RPERequest does not have permit id.");
                }
            }
            if (giApp instanceof RPRRequest) {
                if (((RPRRequest)giApp).getPermitId() != null) {
                    oldPermit = permitBO.retrievePermit(((RPRRequest)giApp).getPermitId()).getPermit();
                }
                else {
                    throw new DAOException("RPRRequest does not have permit id.");
                }
            }

        }
        catch (Exception e) {
            logger.error("Unable to locate the application.", e);
            throw new DAOException("Unable to locate the application.", e);
        }

        try {
            // Fetch the latest version of the facility inventory.
            Facility facility = facilityBO.retrieveFacilityData(giApp.getFacilityId(), -1);
            facility = facilityBO.retrieveFacilityEditable(facility.getFpId(), userId);
            
            if (facility == null) {
                throw new NullPointerException("Could not locate facility for "
                            + "Facility ID = " + giApp.getFacilityId());
            }
            
            if (giApp != null) {
                correspondence = new Correspondence();
                correspondence.setDateGenerated(issuance.getIssuanceDate());
                correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
                correspondence.setFacilityID(giApp.getFacilityId());
                //correspondence.setDocumentID(issuance.getIssuanceDocId());
                correspondence.setAdditionalInfo(giApp.getApplicationNumber());
            }

            if (giApp != null && giApp instanceof RPRRequest) {
                if (issuanceType.equals(GenericIssuanceTypeDef.PROPOSED_PTIO_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_PROPOSED);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("129");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.PROPOSED_PTI_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_PROPOSED);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("122");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.PROPOSED_PTO_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_PROPOSED);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("130");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.PROPOSED_TVPTO_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_PROPOSED);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("134");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.PROPOSED_FINAL_PTIO_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_PROPOSED_FINAL);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("127");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.PROPOSED_FINAL_PTI_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_PROPOSED_FINAL);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("121");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.FINAL_PTIO_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_FINAL);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("126");
                    // Update the EUs for final issuance if the EU is included in the app.
                    ArrayList<PermitEU> revokedEUs = new ArrayList<PermitEU>();
                    for (PermitEU pe : oldPermit.getEus()) {
                        if (((RPRRequest) giApp).isRevokeEntirePermit()) {
                            revokedEUs.add(pe);
                        }
                        else {
                            for (ApplicationEU aeu : giApp.getIncludedEus()) {
                                if (aeu.getFpEU().getCorrEpaEmuId().equals(pe.getCorrEpaEMUID())) {
                                    revokedEUs.add(pe);
                                    break;
                                }
                            }
                        }
                    }
                    for (PermitEU pe : revokedEUs){
                        pe.setRevocationApplicationID(giApp.getApplicationID());
                        pe.setRevocationDate(issuance.getIssuanceDate());
                        pe.setPermitStatusCd(PermitStatusDef.REVOKED);
                        oldPermitNeedsUpdate = true;
                        
                        // revoke PTIO for EU, change eu permit status to none.
                        EmissionUnit eu = facility.getMatchingEmissionUnit(
                                pe.getCorrEpaEMUID());
                        eu.setPtioStatusCd(PTIORegulatoryStatus.NONE);
                        facilityBO.modifyEmissionUnit(eu, userId);
                    }
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.FINAL_PTI_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_FINAL);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("120");
                    // Update the EUs for final issuance if the EU is included in the app.
                    ArrayList<PermitEU> revokedEUs = new ArrayList<PermitEU>();
                    for (PermitEU pe : oldPermit.getEus()) {
                        if (((RPRRequest) giApp).isRevokeEntirePermit()) {
                            revokedEUs.add(pe);
                        }
                        else {
                            for (ApplicationEU aeu : giApp.getIncludedEus()) {
                                if (aeu.getFpEU().getCorrEpaEmuId().equals(pe.getCorrEpaEMUID())) {
                                    revokedEUs.add(pe);
                                    break;
                                }
                            }
                        }
                    }
                    for (PermitEU pe : revokedEUs){
                        pe.setRevocationApplicationID(giApp.getApplicationID());
                        pe.setRevocationDate(issuance.getIssuanceDate());
                        pe.setPermitStatusCd(PermitStatusDef.REVOKED);
                        oldPermitNeedsUpdate = true;
                        
                        // revoke PTI for EU, change eu PTI status to none.
                        EmissionUnit eu = facility.getMatchingEmissionUnit(
                                pe.getCorrEpaEMUID());
                        eu.setPtiStatusCd(PTIRegulatoryStatus.NONE);
                        facilityBO.modifyEmissionUnit(eu, userId);
                    }
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.FINAL_PTO_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_FINAL);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("131");
                    // Update the EUs for final issuance if the EU is included in the app.
                    ArrayList<PermitEU> revokedEUs = new ArrayList<PermitEU>();
                    for (PermitEU pe : oldPermit.getEus()) {
                        if (((RPRRequest) giApp).isRevokeEntirePermit()) {
                            revokedEUs.add(pe);
                        }
                        else {
                            for (ApplicationEU aeu : giApp.getIncludedEus()) {
                                if (aeu.getFpEU().getCorrEpaEmuId().equals(pe.getCorrEpaEMUID())) {
                                    revokedEUs.add(pe);
                                    break;
                                }
                            }
                        }
                    }
                    for (PermitEU pe : revokedEUs){
                        pe.setRevocationApplicationID(giApp.getApplicationID());
                        pe.setRevocationDate(issuance.getIssuanceDate());
                        pe.setPermitStatusCd(PermitStatusDef.REVOKED);
                        oldPermitNeedsUpdate = true;

                        // revoke state PTO for EU, change eu permit status to none. 
                        EmissionUnit eu = facility.getMatchingEmissionUnit(
                                pe.getCorrEpaEMUID());
                        eu.setPtioStatusCd(PTIORegulatoryStatus.NONE);
                        facilityBO.modifyEmissionUnit(eu, userId);
                    }
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.FINAL_TVPTO_REVOCATION)) {
                    ((RPRRequest) giApp).setDispositionFlag(RPRRequest.ISSUED_FINAL);
                    giAppNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("132");
                    // Update the EUs for final issuance. Title V Permit is all or nothing.
                    for (PermitEU pe : oldPermit.getEus()) {
                        pe.setRevocationApplicationID(giApp.getApplicationID());
                        pe.setRevocationDate(issuance.getIssuanceDate());
                        pe.setPermitStatusCd(PermitStatusDef.REVOKED);
                        oldPermitNeedsUpdate = true;
                    }
                }
                else {
                    throw new DAOException("GenericIssuance parameter has an unknown issuanceTypeCd.");
                }
            }
            else if (giApp != null && giApp instanceof RPERequest) {
                
                if (issuanceType.equals(GenericIssuanceTypeDef.TIME_EXTENSION_PTI)) {
                    correspondence.setCorrespondenceTypeCode("123");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.TIME_EXTENSION_PTIO)) {
                    correspondence.setCorrespondenceTypeCode("128");
                }
                else {
                    throw new DAOException("GenericIssuance parameter has an unknown issuanceTypeCd.");
                }
                
                if (giPermit.getTotalAmount() != 0) {
                    newInv = new Invoice();
                    newInv.setPermitId(giPermit.getPermitID());
                    newInv.setInvoiceStateCd(InvoiceState.READY_TO_INVOICE);
                    newInv.setRevenueTypeCd(RevenueTypeDef.PTI_FEE);
                    newInv.setCreationDate(issuance.getIssuanceDate());
                    newInv.setDueDate(Invoice.calculateDueDate(issuance.getIssuanceDate(), 30));
                    newInv.setOrigAmount(new Double(giPermit.getTotalAmount()));
                    newInv.setFacilityId(giPermit.getFacilityId());
                    newInv.setContact(null);//Invoice will initialize this
                }
                
                ((RPERequest) giApp).setDispositionFlag(RPERequest.ISSUED);
                giAppNeedsUpdate = true;
                
                for (PermitEU pe : oldPermit.getEus()) {
                    pe.setExtensionApplicationID(giApp.getApplicationID());
                    pe.setExtensionDate(issuance.getIssuanceDate());
                }
                oldPermitNeedsUpdate = true;
                
            }
            else if (giApp != null && giApp instanceof DelegationRequest) {
                //setup correspondence and wrapn for DOR
                if (issuanceType.equals(GenericIssuanceTypeDef.DELEGATION_DIRECTOR)) {
                    correspondence.setCorrespondenceTypeCode("111");
                } else if (issuanceType.equals(GenericIssuanceTypeDef.DELEGATION_APPROVED)) {
                    correspondence.setCorrespondenceTypeCode("112");
                } else if (issuanceType.equals(GenericIssuanceTypeDef.DELEGATION_DENIED)) {
                    correspondence.setCorrespondenceTypeCode("110");
                } else if (issuanceType.equals(GenericIssuanceTypeDef.DELEGATION_NTV_APPROVED)) {
                    correspondence.setCorrespondenceTypeCode("113");  //DOR 'acknowledged'
                }
            } else if (giApp != null && giApp instanceof RelocateRequest) {
                //setup correspondence for ITR
                if (issuanceType.equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_APPROVED)) {
                    correspondence.setCorrespondenceTypeCode("117");
                } else if (issuanceType.equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_APPROVED_COND)) {
                    correspondence.setCorrespondenceTypeCode("118");
                }  else if (issuanceType.equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_DENIED)) {
                    correspondence.setCorrespondenceTypeCode("119");
                } else if (issuanceType.equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_APPROVED_COND)) {
                    correspondence.setCorrespondenceTypeCode("114");
                } else if (issuanceType.equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_APPROVED)) {
                    correspondence.setCorrespondenceTypeCode("116");
                }  else if (issuanceType.equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_DENIED)) {
                    correspondence.setCorrespondenceTypeCode("115");
                }
            }
            else if (giPermit != null) { 
                if (issuanceType.equals(GenericIssuanceTypeDef.FINAL_PTI_DENIAL)) {
                    giPermit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_DENIAL);
                    giPermitNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("124");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.FINAL_PTIO_DENIAL)) {
                    giPermit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_DENIAL);
                    giPermitNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("125");
                }
                else if (issuanceType.equals(GenericIssuanceTypeDef.FINAL_TV_DENIAL)) {
                    giPermit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_DENIAL);
                    giPermitNeedsUpdate = true;
                    correspondence.setCorrespondenceTypeCode("133");
                }
                //else if (issuanceType.equals(GenericIssuanceTypeDef.FINAL_TIV_DENIAL)) {
                //    giPermit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_DENIAL);
                //    giPermitNeedsUpdate = true;
               //     correspondence.setCorrespondenceTypeCode("136");
                //}
                // Note: nothing special required for ITR and non-director DOR
                // issuances (e.g. no Permit or WRAPN objects)
            }
            
        }
        catch (Exception e) {
            logger.error("Unable to prepare generic issuance.", e);
            throw new DAOException("Unable to prepare generic issuance." 
                                   + e.getMessage(), e);
        }
        
        Transaction trans = TransactionFactory.createTransaction();
        try {
            if (giAppNeedsUpdate) {
                appBO.modifyApplication(giApp, trans, true);
            }
            if (giPermitNeedsUpdate) {
                permitBO.modifyPermit(giPermit, userId, trans);
            }
            if (oldPermitNeedsUpdate) {
                permitBO.modifyPermit(oldPermit, userId, trans);
            }
            if (correspondence != null) {
                corrBO.createCorrespondence(correspondence, trans);
            }
            if (newInv != null) {
                InfrastructureHelper infraHelper = new InfrastructureHelper();
                newInv = infraHelper.createInvoice(newInv, trans);
                logger.warn("New invoice id = " + newInv.getInvoiceId()
                        + ", last_mod = " + newInv.getLastModified());
                
                if (user != null)
                    infraBO.preparePostToRevenue(user, newInv, 
                        newInv.getCreationDate(), true);
                else {
                    String message = "Application Number "
                        + giApp.getApplicationNumber()
                        + ": "
                        + "Unable to post to revenue system: Could not locate user. ";
                    messages.add(new ValidationMessage("genericIssuance", message,
                                                       ValidationMessage.Severity.WARNING));
                    logger.error(message);
                }
            }
        
            issuance.setIssued(true);
        
            modifyGenericIssuance(issuance, trans);
            
            trans.complete();
            
        } catch (Exception e) {
            DAOException de = new DAOException("Unable to update the application.", e);
            cancelTransaction(trans, de);
            logger.error("Unable to update the application." + e.getMessage(), e);
            throw de;
        } finally {
            closeTransaction(trans);
        }
        

        return messages.toArray(new ValidationMessage[0]);
    }

    private void modifyGenericIssuance(GenericIssuance issuance,
            Transaction trans) throws DAOException {
        
        saveDocument(issuance, trans);

        genericIssuanceDAO(trans).modifyGenericIssuance(issuance);
    }

    private void saveDocument(GenericIssuance issuance, Transaction trans) throws DAOException {
        
        CorrespondenceDocument gid = issuance.getIssuanceDoc();
        if (gid != null) {
            if (gid.getLastModifiedTS() == null) {
                gid.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            }
            gid.setTemporary(false);

            if (gid.getDocumentID() == null) {
                Document newDoc = documentDAO(trans).createDocument(gid);
                gid = new CorrespondenceDocument(newDoc);
                issuance.setIssuanceDoc(gid);
            } else {
                documentDAO(trans).modifyDocument(gid);
            }

            // If we had an old version, mark it as temp for the document
            // reaper.
            Integer oldDocId = issuance.getIssuanceDocId();
            if (oldDocId != null && !gid.getDocumentID().equals(oldDocId)) {
                Document oldDoc = documentDAO(trans).retrieveDocument(
                        oldDocId.intValue());
                oldDoc.setTemporary(true);
                documentDAO(trans).modifyDocument(oldDoc);
            }
        }

        gid = issuance.getAddrLabelDoc();
        if (gid != null) {
            if (gid.getLastModifiedTS() == null) {
                gid.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            }
            gid.setTemporary(false);

            if (gid.getDocumentID() == null) {
                Document newDoc = documentDAO(trans).createDocument(gid);
                gid = new CorrespondenceDocument(newDoc);
                issuance.setAddrLabelDoc(gid);
            } else {
                documentDAO(trans).modifyDocument(gid);
            }

            // If we had an old version, mark it as temp for the document
            // reaper.
            Integer oldDocId = issuance.getAddrLabelDocId();
            if (oldDocId != null && !gid.getDocumentID().equals(oldDocId)) {
                Document oldDoc = documentDAO(trans).retrieveDocument(
                        oldDocId.intValue());
                oldDoc.setTemporary(true);
                documentDAO(trans).modifyDocument(oldDoc);
            }
        }
    }

    /**
     * @param userId 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void generateDocs(GenericIssuance issuance, Integer userId)
        throws DAOException {

        if (issuance == null) {
            throw new DAOException("GenericIssuance paramter is null.");
        }

        String issuanceType = issuance.getIssuanceTypeCd();
        if (issuanceType == null || issuanceType.length() < 1) {
            throw new DAOException("GenericIssuance paramter is missing an issuanceTypeCd.");
        }

        // Fetch old issuance and address docs.
        String templateCD = null;
        if (GenericIssuanceTypeDef.getTemplateCD() != null 
            && GenericIssuanceTypeDef.getTemplateCD().getItems() != null) {
            templateCD = GenericIssuanceTypeDef.getTemplateCD().getItems().getItemDesc(issuanceType);
        }

        TemplateDocument templateDoc = null;
        if (templateCD != null) {
            templateDoc = TemplateDocTypeDef.getTemplate(templateCD);
        }

        CorrespondenceDocument doc = new CorrespondenceDocument();
        doc.setLastModifiedBy(userId);
        doc.setDescription("Issuance Document");
        doc.setTemporary(false);

        if (templateDoc != null) {
            doc = generateTempDocument(issuance, templateDoc, doc);
            issuance.setIssuanceDoc(doc);
        }

        prepareAddressLabel(issuance, userId);

        modifyGenericIssuance(issuance);

    }

    private void prepareAddressLabel(GenericIssuance issuance, Integer userId) 
        throws DAOException {

        CorrespondenceDocument addrLabelDoc = new CorrespondenceDocument();
        
        /*
         * ITR and DOR applications use a different AddressLabel template
         */
        TemplateDocument addressLabelsTemplate;
        
        if (issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_APPROVED) 
            || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_APPROVED_COND) 
            || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.INTENT_TO_RELOCATE_DENIED) 
            || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_APPROVED) 
            || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_APPROVED_COND) 
            || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.SITE_PRE_APPROVAL_DENIED)) {
            //setup ITR template
            addressLabelsTemplate 
                = TemplateDocTypeDef.getTemplate(TemplateDocTypeDef.ITR_FAX_AND_ADDRESS_LABELS);
        } else if (issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.DELEGATION_APPROVED) 
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.DELEGATION_DENIED) 
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.DELEGATION_DIRECTOR) 
                || issuance.getIssuanceTypeCd().equals(GenericIssuanceTypeDef.DELEGATION_NTV_APPROVED)) {
            //setup DOR template    
            addressLabelsTemplate 
                    = TemplateDocTypeDef.getTemplate(TemplateDocTypeDef.DOR_MAILING_SHEETS);
            }
        else {
            addressLabelsTemplate 
                = TemplateDocTypeDef.getTemplate(TemplateDocTypeDef.ADDRESS_LABELS);
        }
        if (addressLabelsTemplate == null) {
            throw new DAOException("Cannot find template for address labels.");
        }
        
        // Generate the address labels.
        addrLabelDoc.setLastModifiedBy(userId);
        addrLabelDoc.setDescription("Address Label");
        addrLabelDoc = generateTempDocument(issuance, addressLabelsTemplate,
                                            addrLabelDoc);
        issuance.setAddrLabelDoc(addrLabelDoc);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public CorrespondenceDocument uploadTempDocument(CorrespondenceDocument doc, InputStream is)
        throws DAOException {

        if (doc == null || !doc.isValid()) {
            throw new DAOException("CorrespondenceDocument is not valid.");
        }

        try {
            doc.setTemporary(true);
            if (doc.getLastModifiedTS() == null) {
                doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            }
            doc.setUploadDate(doc.getLastModifiedTS());
            doc = (CorrespondenceDocument) documentDAO().createDocument(doc);
            DocumentUtil.createDocument(doc.getPath(), is);
        } 
        catch (IOException ioe) {
            try {
                DocumentUtil.removeDocument(doc.getPath());
            } 
            catch (IOException ioex) {
            }
            throw new DAOException("Unable to create document.", ioe);
        }

        return doc;
    }

    private CorrespondenceDocument generateTempDocument(GenericIssuance issuance, 
                                                        TemplateDocument templateDoc,
                                                        CorrespondenceDocument doc)
        throws DAOException {

        if (issuance == null) {
            throw new DAOException("Issuance object is missing.");
        }
       
        Application giApp = null;
        Facility facility = null;
        Permit permit = null;

        if (issuance.getApplicationId() != null) {

            try {
                ApplicationService appBO = null;
                appBO = ServiceFactory.getInstance().getApplicationService();
                giApp = appBO.retrieveApplication(issuance.getApplicationId());
            }
            catch (Exception e) {
                throw new DAOException("Unable to locate the ApplicationService.", e);
            }
            if (giApp == null) {
                throw new DAOException("Unable to locate Application " + issuance.getApplicationId() + ".");
            }
        }
        
        try {
            PermitService permitBO = null;
            PermitInfo pi = null;

            permitBO = ServiceFactory.getInstance().getPermitService();

            if (giApp.getPermitId() != null) {
                pi = permitBO.retrievePermit(giApp.getPermitId());
            }
            else if (issuance.getPermitId() != null) {
                pi = permitBO.retrievePermit(issuance.getPermitId());
            }
            
            // for bug 1875 to generate address label for generic issuance
            // need to use the permit on issuance object.
            if (templateDoc.getTemplateDocTypeCD().equalsIgnoreCase(TemplateDocTypeDef.ADDRESS_LABELS))
                pi = permitBO.retrievePermit(issuance.getPermitId());

            if (pi != null) {
                permit = pi.getPermit();
            }
        }
        catch (Exception e) {
            throw new DAOException("Unable to locate the PermitService.", e);
        }

        if (giApp != null && giApp.getFacility() != null) {
            try {
                FacilityService facBO = ServiceFactory.getInstance().getFacilityService();
                String facId = giApp.getFacility().getFacilityId();
                facility = facBO.retrieveFacility(facId);
                
                // Mentis 2227
                // There should only be EUs that are operating that appear on
                // the issued ITR.
                // Mantis 2918
                // Also include "Not Installed" EUs for ITRs.
                if (giApp instanceof RelocateRequest) {
                    ArrayList<EmissionUnit> eus = new ArrayList<EmissionUnit>();
                    for (EmissionUnit eu : facility.getEmissionUnits()) {
                        if (OperatingStatusDef.OP.equals(eu.getOperatingStatusCd()) ||
                        		OperatingStatusDef.NI.equals(eu.getOperatingStatusCd())) {
                            eus.add(eu);
                        }
                    }
                    facility.setEmissionUnits(eus);
                }

            }
            catch (Exception e) {
                throw new DAOException("Unable to locate the FacilityService.", e);
            }
        }
        else {
            throw new DAOException("Unable to locate the facility for Generic Issuance ID " 
                                   + issuance.getIssuanceId());
        }

        // Set the path elements of the generated doc.
        doc.setFacilityID(facility.getFacilityId());
        doc.setExtension(DocumentUtil.getFileExtension(templateDoc.getPath()));
        
        try {
            doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            doc.setUploadDate(doc.getLastModifiedTS());
            doc.setTemporary(true);
            doc = (CorrespondenceDocument) documentDAO().createDocument(doc);
            
            DocumentGenerationBean dgb = new DocumentGenerationBean();
            if (facility != null) {
                dgb.setFacility(facility);
            }
            if (giApp != null) {
                dgb.setApplication(giApp);
            }
            if (permit != null) {
                dgb.setPermit(permit);
            }
            dgb.setGenericIssuance(issuance);
            
            /*DocumentUtil.generateDocument(templateDoc.getPath(), dgb, 
                                          doc.getPath());*/
            DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dgb, doc.getPath());
        } 
        catch (IOException iex) {
            logger.error(iex.getMessage(), iex);
            throw new DAOException(iex.getMessage());
        } 
        catch (DocumentGenerationException dex) {
            logger.error(dex.getMessage(), dex);
            throw new DAOException(dex.getMessage());
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DAOException(e.getMessage(), e);
        }

        return doc;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void deleteGenericIssuance(GenericIssuance issuance, Integer userId) 
        throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();

        try {
            genericIssuanceDAO(trans).deleteGenericIssuance(issuance);
            
            CorrespondenceDocument gid = issuance.getIssuanceDoc();
            if (gid != null) {
                
                if (gid.getDocumentID() == null) {
                    
                }else {
                    
                    documentDAO(trans).removeDocument(gid);
                }
            }

            gid = issuance.getAddrLabelDoc();
            if (gid != null) {
                
                if (gid.getDocumentID() == null) {
                
                } else {
                    documentDAO(trans).removeDocument(gid);
                }
            }

            trans.complete();
        }
        catch (DAOException de) {
            cancelTransaction(trans, de);
        } 
        finally {
            closeTransaction(trans);
        }

        return;
    }
}
