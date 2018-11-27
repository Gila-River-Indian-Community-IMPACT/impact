package us.oh.state.epa.stars2.database.dao;

import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface DocumentDAO extends TransactableDAO {

    /**
     * @param documentID
     * @return
     * @throws DAOException
     */
    Document retrieveDocument(int documentID) throws DAOException;
    
    /**
     * @return
     * @throws DAOException
     */
    Document[] retrieveMigratedPdfDocuments() throws DAOException;

    /**
     * @param documentID
     * @return
     * @throws DAOException
     */
    Document[] retrieveDocuments(int documentID[]) throws DAOException;

    /**
     * @param documentPath
     * @return
     * @throws DAOException
     */
    Document[] retrieveDocumentsByPath(String documentPath) throws DAOException;

    /**
     * @param document
     * @return
     * @throws DAOException
     */
    Document createDocument(Document document) throws DAOException;

    /**
     * @param document
     * @throws DAOException
     */
    void modifyDocument(Document document) throws DAOException;

    /**
     * @param document
     * @throws DAOException
     */
    void removeDocument(Document document) throws DAOException;

    /**
     * @param fullTextQuery
     * @param maxNumberOfHits
     * @return
     * @throws DAOException
     */
    Document[] searchDocumentsFullText(String fullTextQuery, int maxNumberOfHits)
            throws DAOException;

    /**
     * @throws DAOException
     */
    void refreshFullTextIndex() throws DAOException;

    /**
     * @return
     * @throws DAOException
     */
    TemplateDocument retrieveTemplateDocument(String docId)
        throws DAOException;

    /**
     * @return
     * @throws DAOException
     */
    TemplateDocument[] retrieveTemplateDocuments() throws DAOException;

    /**
     * Set the "temp_flag" field to "N" for the document corresponding to
     * <code>documentID</code>.
     * 
     * @param documentID
     *            document key.
     * @throws DAOException
     */
    void unMarkTempDocument(Integer documentID) throws DAOException;

    /**
     * Removes temporary documents older than daysOld.
     *
     * @throws DAOException
     */
    boolean removeTemporaryDocuments(Integer daysOld, boolean logFailures) throws DAOException;
    
    /**
     * Retrieves all documents not marked as temporary that are at least
     * daysOld days old.
     * @return
     * @throws DAOException
     */
    Document[] retrieveNonTemporaryDocuments(Integer daysOld) throws DAOException;

    /*
     * Returns the next ID portion for creating subdirectories under /tmp.
     * A user may create a tmp dir for bulk operations with path 
     * FILE_STORE_ROOT_PATH/tmp/ID using this method.
     */
    String getNextTmpDir() throws DAOException;
    
    /**
     * Removes the database reference to the document. The physical document 
     * will not be deleted.
     * @param documentId
     * @throws DAOException
     */
    void removeDocumentReference(Integer documentId) throws DAOException;

}
