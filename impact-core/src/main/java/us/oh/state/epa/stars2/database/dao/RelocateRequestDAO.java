package us.oh.state.epa.stars2.database.dao;

import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocationAddtlAddr;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface RelocateRequestDAO extends TransactableDAO {
  
    /**
     * @param relocateRequest
     * @return
     * @throws DAOException
     */
    RelocateRequest createRelocateRequest(RelocateRequest relocateReq)
            throws DAOException;
    
    /**
     * @param relocateRequest
     * @return
     * @throws DAOException
     */
    RelocateRequest deleteRelocateRequest(RelocateRequest relocateReq)
            throws DAOException;
    
    /**
     * @param relocateRequest
     * @return
     * @throws DAOException
     */
    RelocateRequest modifyRelocateRequest(RelocateRequest relocateReq)
            throws DAOException;
    
    /**
     * @param requestId
     * @return
     * @throws DAOException
     */
    RelocateRequest retrieveRelocateRequest(int requestid)
            throws DAOException;
    
    /**
     * 
     * @param requestId
     * @return
     * @throws DAOException
     */
    RelocateRequest retrievePortalRelocateRequest(int requestId) throws DAOException;
    
    /**
     * @param applicationId
     * @return
     * @throws DAOException
     */
    RelocateRequest retrieveRelocateRequestByAppId(int applicationId)
            throws DAOException;
    
    /**
     * @param facilityId
     * @return
     * @throws DAOException
     */
    RelocateRequest[] retrieveRelocateRequests(String facilityId)
            throws DAOException;
    
    /**
     * @param relocateRequest
     * @param attachment
     * @return
     * @throws DAOException
     */
    boolean createRelocationAttachment(RelocateRequest relocateReq,Attachment attachment)
            throws DAOException;
    
    /**
     * @param relocateRequest
     * @param attachment
     * @return
     * @throws DAOException
     */
    boolean deleteRelocationAttachment(RelocateRequest relocateReq,Attachment attachment,boolean deleteFile)
            throws DAOException;
    
    /**
     * 
     * @param relocateReq
     * @param attachment
     * @throws DAOException
     */
    void deleteRelocationAttachment(RelocateRequest relocateReq, Attachment attachment) throws DAOException;
    
    /**
     * @param relocateRequest
     * @param attachment
     * @throws DAOException
     */
    Attachment modifyRelocationAttachment(RelocateRequest relocateReq,Attachment attachment)
            throws DAOException;
    

    /**
     * @param complianceReportId
     * @throws DAOException
     */
    Attachment[] retrieveAttachments(String facilityID, int reportID)
        throws DAOException;
    

    /**
     * Retrieve list of pre-approved addresses for the specified facility.
     * @param facilityId
     * @return
     * @throws DAOException
     */
    public SimpleDef[] retrievePreApprovedAddressesForFacility (String facilityId)
        throws DAOException;
    
    /**
     * Create an additional address for a relocation request.
     * @param addr
     * @throws DAOException
     */
    RelocationAddtlAddr createRelocationAddtlAddr(RelocationAddtlAddr addr) throws DAOException; 
    
    /**
     * Retrieve all additional addresses for a relocation request.
     * @param request_id
     * @return
     * @throws DAOException
     */
    RelocationAddtlAddr[] retrieveRelocationAddtlAddrs(int request_id) throws DAOException;
    
    /**
     * Delete all additional addresses for a relocation request.
     * @param request_id
     * @throws DAOException
     */
    void deleteRelocationAddtlAddrs(int request_id) throws DAOException;
    
    /**
     * Retrieve next available additional address id for the specified request.
     * @param request_id
     * @return
     * @throws DAOException
     */
    int getMaxAddtlAddrId (int request_id) throws DAOException;
}
