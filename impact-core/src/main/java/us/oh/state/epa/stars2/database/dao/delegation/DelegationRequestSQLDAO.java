package us.oh.state.epa.stars2.database.dao.delegation;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.DelegationRequestDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;

@Repository
public class DelegationRequestSQLDAO extends AbstractDAO implements DelegationRequestDAO {

	private Logger logger = Logger.getLogger(DelegationRequestSQLDAO.class);

    public DelegationRequest createDelegationRequest(DelegationRequest req)
            throws DAOException {
        req.setLastModified(1);
        ConnectionHandler connHandler = new ConnectionHandler(
                "DelegationRequestSQL.createDelegationRequest", false);
        /*
        1 application_id
        2 effective_date
        3 originator_fname
        4 originator_lname
        5 originator_title
        6 originator_address1
        7 originator_address2
        8 originator_city
        9 originator_state_cd
        10 originator_zip
        11 originator_phone_no
        12 corporate_qualified
        13 request_type_cd
        14 250_person_qualified
        15 25_mil_qualified
        16 successor_qualified
        17 assignee_fname
        18 assignee_lname
        19 assignee_title
        20 assignee_address1
        21 assignee_address2
        22 assignee_city
        23 assignee_state_cd
        24 assignee_zip
        25 last_modified as pad_lm
        */
        connHandler.setInteger(1, req.getApplicationID());
        try {
            connHandler.setTimestamp(2,req.getEffectiveDate());
        } catch (Exception e) {
            //may bea null effective date
        }
        connHandler.setString(3, req.getOrigFirstName());
        connHandler.setString(4, req.getOrigLastName());
        connHandler.setString(5, req.getOrigTitle());
        connHandler.setString(6, req.getOrigAddress1());
        connHandler.setString(7, req.getOrigAddress2());
        connHandler.setString(8, req.getOrigCity());
        connHandler.setString(9, req.getOrigStateCd());
        connHandler.setString(10, req.getOrigZip());
        connHandler.setString(11, req.getOrigPhoneNo());
        connHandler.setString(12,AbstractDAO.translateBooleanToIndicator(req.isFacCorpQualified()));
        connHandler.setString(13,req.getRequestTypeCd());
        connHandler.setString(14,AbstractDAO.translateBooleanToIndicator(req.isFac25MilOr250EmpQualified()));
        connHandler.setString(15,req.getRequestDispositionCd());
        connHandler.setString(16,AbstractDAO.translateBooleanToIndicator(req.isFacSuccessorQualified()));
        connHandler.setString(17, req.getAssigFirstName());
        connHandler.setString(18, req.getAssigLastName());
        connHandler.setString(19, req.getAssigTitle());
        connHandler.setString(20, req.getAssigAddress1());
        connHandler.setString(21, req.getAssigAddress2());
        connHandler.setString(22, req.getAssigCity());
        connHandler.setString(23, req.getAssigStateCd());
        connHandler.setString(24, req.getAssigZip());
        connHandler.update();
        req.setDelegationLastModified(1);
        return req;
    }

    public DelegationRequest deleteDelegationRequest(DelegationRequest delegationReq)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DelegationRequestSQL.deleteDelegationRequest", false);
        connHandler.setInteger(1, delegationReq.getApplicationID());
        connHandler.remove();
        return null;
    }

    public DelegationRequest modifyDelegationRequest(DelegationRequest delegationReq)
            throws DAOException {

        ConnectionHandler connHandler = new ConnectionHandler(
                "DelegationRequestSQL.updateDelegationRequest", false);
        //set the update part of the SQL

        try {
            connHandler.setTimestamp(1,delegationReq.getEffectiveDate());
        } catch (Exception e) {
            //null is accepted for this field
        }
        connHandler.setString(2,delegationReq.getOrigFirstName());
        connHandler.setString(3,delegationReq.getOrigLastName());
        connHandler.setString(4,delegationReq.getOrigTitle());
        connHandler.setString(5,delegationReq.getOrigAddress1());
        connHandler.setString(6,delegationReq.getOrigAddress2());
        connHandler.setString(7,delegationReq.getOrigCity());
        connHandler.setString(8,delegationReq.getOrigStateCd());
        connHandler.setString(9,delegationReq.getOrigZip());
        connHandler.setString(10,delegationReq.getOrigPhoneNo());
        connHandler.setString(11,AbstractDAO.translateBooleanToIndicator(delegationReq.isFacCorpQualified()));
        connHandler.setString(12,delegationReq.getRequestTypeCd());
        connHandler.setString(13,AbstractDAO.translateBooleanToIndicator(delegationReq.isFac25MilOr250EmpQualified()));
        connHandler.setString(14,delegationReq.getRequestDispositionCd());
        connHandler.setString(15,AbstractDAO.translateBooleanToIndicator(delegationReq.isFacSuccessorQualified()));
        connHandler.setString(16,delegationReq.getAssigFirstName());
        connHandler.setString(17,delegationReq.getAssigLastName());
        connHandler.setString(18,delegationReq.getAssigTitle());
        connHandler.setString(19,delegationReq.getAssigAddress1());
        connHandler.setString(20,delegationReq.getAssigAddress2());
        connHandler.setString(21, delegationReq.getAssigCity());
        connHandler.setString(22, delegationReq.getAssigStateCd());
        connHandler.setString(23,delegationReq.getAssigZip());
        connHandler.setInteger(24,delegationReq.getDelegationLastModified()+1);

        //set the Where part of the SQL
        connHandler.setInteger(25,delegationReq.getApplicationID());
        connHandler.setInteger(26,delegationReq.getDelegationLastModified());
        
        connHandler.update();
        delegationReq.setDelegationLastModified(delegationReq.getLastModified()+1);
        return delegationReq;
    }

    public DelegationRequest retrieveDelegationRequest(int applicationId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DelegationRequestSQL.retrieveDelegationRequestById", true);
        connHandler.setString(1, applicationId);
        DelegationRequest req = (DelegationRequest) connHandler
        .retrieve(DelegationRequest.class);
        req.setAttachments(retrieveAttachments(req
                .getFacilityId(), req.getApplicationID()));
        return req;
    }

    public DelegationRequest[] searchDelegationRequest(String facilityId)
            throws DAOException {
        //this is handled by the Application BO & SQLDAO since DOR is now a type of Application
        return null;
    }
    
    public boolean createDelegationAttachment(DelegationRequest delegationReq,
            Attachment attachment) throws DAOException {
            Attachment ret = attachment;

            ConnectionHandler connHandler = new ConnectionHandler(
                    "DelegationRequestSQL.createDelegationAttachment", false);
            connHandler.setInteger(1, attachment.getDocumentID());
            connHandler.setInteger(2, delegationReq.getApplicationID());
            connHandler.setInteger(3, 1);
            connHandler.update();
            ret.setLastModified(1);
        return true;
    }
    

    public boolean deleteDelegationAttachment(DelegationRequest delegationReq,
            Attachment attachment, boolean deleteFile) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DelegationRequestSQL.deleteDelegationAttachment", false);
        connHandler.setInteger(1, attachment.getDocumentID());
        connHandler.setInteger(2, delegationReq.getApplicationID());
        connHandler.update();
        removeRows("dc_document", "document_id", attachment.getDocumentID());
        if (deleteFile) {
            try {
                DocumentUtil.removeDocument(attachment.getPath());
            } catch (IOException ioe) {
                throw new DAOException(
                        "Could not delete DOR (Delegation) ATTACHMENT "
                                + attachment.getPath(), ioe);
            }
        }
        return false;
    }

    public Attachment modifyDelegationAttachment(DelegationRequest delegationReq,
            Attachment attachment) throws DAOException {
        Attachment ret = attachment;
        int i = 1;
        ConnectionHandler connHandler = new ConnectionHandler(
                "DelegationRequestSQL.updateDelegationAttachment", false);
        logger.debug("attempting to update " + attachment.getDocumentID());
        connHandler.setInteger(i++, attachment.getRefLastModified() + 1);
        connHandler.setInteger(i++, attachment.getDocumentID());
        connHandler.setInteger(i++, attachment.getRefLastModified());
        connHandler.update();
        ret.setLastModified(1);
        return ret;
    }

    public Attachment[] retrieveAttachments(String facilityID, int reportID) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DelegationRequestSQL.retrieveDelegationAttachments", true);
        logger.debug("Retriving attachments for " + facilityID + "  and " + reportID);
        connHandler.setString(1, facilityID);
        connHandler.setInteger(2, reportID);
        ArrayList<Attachment> ret = connHandler
                .retrieveArray(Attachment.class);
        logger.debug("Result set size: " + ret.size());
        return ret.toArray(new Attachment[0]);
}

}
