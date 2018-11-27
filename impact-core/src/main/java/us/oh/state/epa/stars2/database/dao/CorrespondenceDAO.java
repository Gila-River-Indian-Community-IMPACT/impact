package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface CorrespondenceDAO extends TransactableDAO {
    /**
     * @param facilityID
     * @return
     * @throws DAOException
     */
    Correspondence[] searchCorrespondenceByFacility(String facilityID)
            throws DAOException;
    
    /**
     * @param linkedToID
     * @return
     * @throws DAOException
     */
    Correspondence[] searchCorrespondenceByLinkedToId(Integer linkedToID)
            throws DAOException;

    /**
     * @param correspondence
     * @return
     * @throws DAOException
     */
    Correspondence createCorrespondence(Correspondence correspondence)
            throws DAOException;

    /**
     * @param correspondence
     * @throws DAOException
     */
    void updateCorrespondence(Correspondence correspondence)
            throws DAOException;

    /**
     * @param correspondence
     * @throws DAOException
     */
    void deleteCorrespondence(Correspondence correspondence)
            throws DAOException;

    /**
     * @param correspondenceID
     * @param noteID
     * @throws DAOException
     */
    void createNoteXref(Integer correspondenceID, Integer noteID)
            throws DAOException;

    /**
     * @param correspondenceID
     * @param noteID
     * @throws DAOException
     */
    void deleteNoteXref(Integer correspondenceID, Integer noteID)
            throws DAOException;

    /**
     * @param correspondence
     * @throws DAOException
     * 
     */
    Correspondence[] searchCorrespondence(Correspondence correspondence)
      		throws DAOException;
    
    /**
     * 
     * @param searchObj
     * @param startDate
     * @param endDate
     * @return
     * @throws DAOException
     */
    Correspondence[] searchCorrespondenceByDate(Correspondence searchObj, String dateField,
            Timestamp startDate, Timestamp endDate) throws DAOException;

    /**
     * @param correspondence
     * @throws DAOException
     */
    boolean modifyCorrespondence(Correspondence correspondence)
            throws DAOException;

    /**
     * @param correspondenceId
     * @throws DAOException
     */
    Correspondence retrieveCorrespondence(int correspondenceId)
            throws DAOException;

	public List<Correspondence> retrievePotentialFollowUpCorrespondence()
			throws DAOException;
	
	Attachment createCorrespondenceAttachment(Integer correspondenceId, Attachment attachment)
			throws DAOException;
	
	public Attachment[] retrieveCorrespondenceAttachments(Integer correspondenceId)
			throws DAOException;
	
	public Attachment updateCorrespondenceAttachment(Attachment attachment)
			throws DAOException;
	
	public void removeCorrespondenceAttachment(Attachment attachment)
			throws DAOException;

	public Correspondence retrieveCorrespondenceAttachmentCount(Correspondence correspondence) throws DAOException;
}
