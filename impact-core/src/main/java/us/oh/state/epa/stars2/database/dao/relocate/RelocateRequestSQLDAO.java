package us.oh.state.epa.stars2.database.dao.relocate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.RelocateRequestDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocationAddtlAddr;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;

@Repository
public class RelocateRequestSQLDAO extends AbstractDAO implements RelocateRequestDAO {

	private Logger logger = Logger.getLogger(RelocateRequestSQLDAO.class);

    public RelocateRequest createRelocateRequest(RelocateRequest relocateReq)
            throws DAOException {
        relocateReq.setLastModified(1);
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.createRelocateRequest", false);
        Integer id = relocateReq.getRequestId();
        relocateReq.setRequestId(nextSequenceVal("S_ITR_ID", id));
        /*
        1 request_id
        2 form_complete
        3 facility_compliant
        4 site_preapproved
        5 jfo_recommendation_cd
        6 request_disposition_cd
        7 future_address
        8 target_county_cd,
        9 special_text
        10 user_id
        11 application_id
        12 date_received
        */
        
        connHandler.setInteger(1, relocateReq.getRequestId());
        connHandler.setString(2,AbstractDAO.translateBooleanToIndicator(relocateReq.isFormComplete()));
        connHandler.setString(3,AbstractDAO.translateBooleanToIndicator(relocateReq.isFacilityCompliant()));
        connHandler.setString(4,AbstractDAO.translateBooleanToIndicator(relocateReq.isSitePreApproved()));
		if (Utility.isNullOrEmpty(relocateReq.getJfoRecommendation())) {
			relocateReq.setJfoRecommendation(null);
		}
        connHandler.setString(5,relocateReq.getJfoRecommendation());
        
        if(Utility.isNullOrEmpty(relocateReq.getRequestDisposition())){
        	relocateReq.setRequestDisposition(null);
        }
        connHandler.setString(6,relocateReq.getRequestDisposition());
        connHandler.setString(7,relocateReq.getFutureAddress());
        connHandler.setString(8,relocateReq.getTargetCountyCd());
        connHandler.setString(9, relocateReq.getSpecialText());
        connHandler.setInteger(10, relocateReq.getUserId());
        connHandler.setInteger(11,relocateReq.getApplicationID());
        connHandler.setTimestamp(12,relocateReq.getReceivedDate());
        connHandler.update();
        relocateReq.setLastModified(1);
        relocateReq.setRequestLastModified(1);
        return relocateReq;
    }

    public RelocateRequest deleteRelocateRequest(RelocateRequest relocateReq)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.deleteRelocateRequest", false);
        connHandler.setInteger(1, relocateReq.getRequestId());
        connHandler.remove();
        return null;
    }

    public RelocateRequest modifyRelocateRequest(RelocateRequest relocateReq)
            throws DAOException {

        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.updateRelocateRequest", false);
        connHandler.setString(1,AbstractDAO.translateBooleanToIndicator(relocateReq.isFormComplete()));
        connHandler.setString(2,AbstractDAO.translateBooleanToIndicator(relocateReq.isFacilityCompliant()));
        connHandler.setString(3,AbstractDAO.translateBooleanToIndicator(relocateReq.isSitePreApproved()));
		if (Utility.isNullOrEmpty(relocateReq.getJfoRecommendation())) {
			relocateReq.setJfoRecommendation(null);
		}
        connHandler.setString(4,relocateReq.getJfoRecommendation());
        connHandler.setString(5,relocateReq.getRequestDisposition());
        connHandler.setString(6,relocateReq.getFutureAddress());
        connHandler.setString(7,relocateReq.getTargetCountyCd());
        connHandler.setString(8, relocateReq.getSpecialText());
        connHandler.setInteger(9, relocateReq.getUserId());
        connHandler.setInteger(10,relocateReq.getApplicationID());
        connHandler.setTimestamp(11, relocateReq.getReceivedDate());
        connHandler.setInteger(12,relocateReq.getRequestLastModified()+1);
        connHandler.setInteger(13,relocateReq.getRequestId());
        connHandler.setInteger(14,relocateReq.getRequestLastModified());
        connHandler.update();
        relocateReq.setRequestLastModified(relocateReq.getLastModified()+1);
        return relocateReq;
    }

    public RelocateRequest retrieveRelocateRequest(int requestId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.retrieveRelocateRequestById", true);
        connHandler.setString(1, requestId);
      //  RelocateRequest req = (RelocateRequest) connHandler
      //  .retrieve(RelocateRequest.class);
      //  req.setAttachments(retrieveAttachments(req
      //          .getFacilityId(), req.getApplicationID()));
       
      //  return req;
        return (RelocateRequest) connHandler.retrieve(RelocateRequest.class); 
    }

    public RelocateRequest retrievePortalRelocateRequest(int requestId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.retrievePortalRelocateRequestById", true);
        connHandler.setString(1, requestId);
        return (RelocateRequest) connHandler.retrieve(RelocateRequest.class); 
    }
    
    public RelocateRequest retrieveRelocateRequestByAppId(int applicationId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                 "RelocateRequestSQL.retrieveRelocateRequestByAppId", true);
        connHandler.setString(1, applicationId);
      
        return (RelocateRequest) connHandler.retrieve(RelocateRequest.class); 
    }
    
    public Attachment[] retrieveAttachments(String facilityID, int reportID) throws DAOException {
            ConnectionHandler connHandler = new ConnectionHandler(
                    "RelocateRequestSQL.retrieveRelocationAttachments", true);
            connHandler.setString(1, facilityID);
            connHandler.setInteger(2, reportID);
            ArrayList<Attachment> ret = connHandler
                    .retrieveArray(Attachment.class);
            return ret.toArray(new Attachment[0]);
    }
    
    public RelocateRequest[] retrieveRelocateRequests(String facilityId)
            throws DAOException {
            ConnectionHandler connHandler = new ConnectionHandler(
                    "RelocateRequestSQL.retrieveRelocateRequestsByFacility", true);
            connHandler.setString(1, facilityId);
            ArrayList<RelocateRequest> ret = connHandler
                    .retrieveArray(RelocateRequest.class);
            RelocateRequest[] result = ret.toArray(new RelocateRequest[0]);
            for (int i=0;i<result.length;i++) {
                result[i].setAttachments(retrieveAttachments(facilityId, result[i].getApplicationID()));
            }
            return result;
    }

    public boolean createRelocationAttachment(RelocateRequest relocateReq,
            Attachment attachment) throws DAOException {
            Attachment ret = attachment;

            ConnectionHandler connHandler = new ConnectionHandler(
                    "RelocateRequestSQL.createRelocationAttachment", false);
            connHandler.setInteger(1, attachment.getDocumentID());
            connHandler.setInteger(2, relocateReq.getApplicationID());
            connHandler.setString(3, attachment.getDocTypeCd());
            connHandler.setInteger(4, 1);
            connHandler.update();
            ret.setLastModified(1);
        return true;
    }

    public boolean deleteRelocationAttachment(RelocateRequest relocateReq,
            Attachment attachment, boolean deleteFile) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.deleteRelocationAttachment", false);
        connHandler.setInteger(1, attachment.getDocumentID());
        connHandler.setInteger(2, relocateReq.getApplicationID());
        connHandler.remove();
        try {
        	removeRows("dc_document", "document_id", attachment.getDocumentID());
        } catch (DAOException e) {
        	logger.error("Exception while deleting dc_document record for relocation document", e);
        }
        if (deleteFile) {
            try {
                DocumentUtil.removeDocument(attachment.getPath());
            } catch (IOException ioe) {
                logger.error("Could not delete ITR (Relocation) ATTACHMENT "
                                + attachment.getPath(), ioe);
            }
        }
        return false;
    }

    public void deleteRelocationAttachment(RelocateRequest relocateReq,
            Attachment attachment) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.deleteRelocationAttachment", false);
        connHandler.setInteger(1, attachment.getDocumentID());
        connHandler.setInteger(2, relocateReq.getApplicationID());
        connHandler.remove();
    }

    public Attachment modifyRelocationAttachment(RelocateRequest relocateReq,
            Attachment attachment) throws DAOException {
        Attachment ret = attachment;
        int i = 1;
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.updateRelocationAttachment", false);
        logger.error("attempting to update " + attachment.getDocumentID());
        connHandler.setInteger(i++, attachment.getDocumentID());
        connHandler.setString(i++, attachment.getDocTypeCd());
        connHandler.setInteger(i++, attachment.getRefLastModified() + 1);
        connHandler.setInteger(i++, attachment.getDocumentID());
        connHandler.setInteger(i++, attachment.getRefLastModified());
        connHandler.update();
        ret.setLastModified(1);
        return ret;
    }

    public final SimpleDef[] retrievePreApprovedAddressesForFacility(String facilityId) 
        throws DAOException {
    	checkNull(facilityId);
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.retrievePreApprovedAddressesForFacility", true);
        connHandler.setString(1, facilityId);
        connHandler.setString(2, facilityId);
        ArrayList<SimpleDef> ret = connHandler
                .retrieveArray(SimpleDef.class);
        return ret.toArray(new SimpleDef[0]);
    }

	public RelocationAddtlAddr createRelocationAddtlAddr(RelocationAddtlAddr addr)
			throws DAOException {
		RelocationAddtlAddr ret = addr;

        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.createRelocationAddtlAddr", false);
        addr.setAddtlAddrId(nextSequenceVal("S_ITR_Addtl_Addr_Id", ret.getAddtlAddrId()));
        int i = 1;
        connHandler.setInteger(i++, addr.getAddtlAddrId());
        connHandler.setInteger(i++, addr.getRequestId());
        connHandler.setString(i++, addr.getFutureAddress());
        connHandler.setString(i++, addr.getTargetCountyCd());
        connHandler.update();
        ret.setLastModified(1);
        
        return ret;
	}

	public void deleteRelocationAddtlAddrs(int requestId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.deleteRelocationAddtlAddrs", false);
        connHandler.setInteger(1, requestId);
        connHandler.remove();
		
	}

	public RelocationAddtlAddr[] retrieveRelocationAddtlAddrs(int requestId)
			throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "RelocateRequestSQL.retrieveRelocationAddtlAddrs", true);
        connHandler.setInteger(1, requestId);
        ArrayList<RelocationAddtlAddr> ret = connHandler
                .retrieveArray(RelocationAddtlAddr.class);
        RelocationAddtlAddr[] result = ret.toArray(new RelocationAddtlAddr[0]);
        return result;
	}

	public int getMaxAddtlAddrId(int requestId) throws DAOException {
        // default to 1 if no additional addresses exist for this request
        int result = 1;
        ResultSet resultSet = null;

        Connection conn = null;
        PreparedStatement psSelect = null;

        try {
            conn = getConnection();
            psSelect = conn.prepareStatement(loadSQL("RelocateRequestSQL.getMaxAddtlAddrId"));
            psSelect.setInt(1, requestId);
            resultSet = psSelect.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getInt(1) + 1;
            }
            resultSet.close();
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(psSelect);
            handleClosing(conn);
        }

        return result;
	}
    
}
