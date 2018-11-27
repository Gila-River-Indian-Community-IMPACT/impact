package us.oh.state.epa.stars2.database.dao;

import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface DelegationRequestDAO extends TransactableDAO {
  
    /**
     * @param delegationRequest
     * @return
     * @throws DAOException
     */
    DelegationRequest createDelegationRequest(DelegationRequest delegation)
            throws DAOException;
    
    /**
     * @param delegationRequest
     * @return
     * @throws DAOException
     */
    DelegationRequest deleteDelegationRequest(DelegationRequest delegation)
            throws DAOException;
    
    /**
     * @param delegationRequest
     * @return
     * @throws DAOException
     */
    DelegationRequest modifyDelegationRequest(DelegationRequest delegation)
            throws DAOException;
    
    /**
     * @param delegationRequest
     * @return
     * @throws DAOException
     */
    DelegationRequest retrieveDelegationRequest(int delegationId)
            throws DAOException;
    
    /**
     * @param delegationRequest
     * @return
     * @throws DAOException
     */
    DelegationRequest[] searchDelegationRequest(String delegationId)
            throws DAOException;
    
    /**
     * @param delegationRequest
     * @param attachment
     * @return
     * @throws DAOException
     */
    boolean createDelegationAttachment(DelegationRequest delegation,Attachment attachment)
            throws DAOException;
    
    /**
     * @param delegationRequest
     * @param attachment
     * @return
     * @throws DAOException
     */
    boolean deleteDelegationAttachment(DelegationRequest delegation,Attachment attachment,boolean deleteFile)
            throws DAOException;
    
    
    /**
     * @param delegationRequest
     * @param attachment
     * @throws DAOException
     */
    Attachment modifyDelegationAttachment(DelegationRequest delegation,Attachment attachment)
            throws DAOException;
    

    /**
     * @param facilityID
     * @param reportID
     * @throws DAOException
     */
    Attachment[] retrieveAttachments(String facilityID, int reportID)
        throws DAOException; 
}
