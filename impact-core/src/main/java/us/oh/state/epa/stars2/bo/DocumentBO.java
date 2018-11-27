package us.oh.state.epa.stars2.bo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;

@Transactional(rollbackFor=Exception.class)
@Service
public class DocumentBO extends BaseBO implements DocumentService {

	private DocumentDAO getDocumentDAO(boolean staging) throws DAOException {
    	String schema = null;
        if (CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP)) {
        	if (staging) {
        		schema = "Staging";
        	} else {
        		schema = "ReadOnly";
        	}
        }

        return documentDAO(schema);
    }
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document getDocumentByID(int documentID, boolean readOnlyDB) throws DAOException {
        // readOnlyDB is true if the call is made from the 3rd level menu of Portal Air Services
        // otherwise the code works as before to determine which schema to use.
        DocumentDAO docDAO = null;
        if(readOnlyDB) {
            docDAO = documentDAO();
        } else {
            if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
                docDAO = documentDAO();
            } else if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
            	docDAO = documentDAO();
            } else {
                docDAO = documentDAO(CommonConst.STAGING_SCHEMA);
            }
        }
        
        return docDAO.retrieveDocument(documentID);
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document getReadOnlyDocumentByID(int documentID) throws DAOException {
        DocumentDAO docDAO = documentDAO(CommonConst.READONLY_SCHEMA);
        return docDAO.retrieveDocument(documentID);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document[] getDocumentsByID(int filter[]) throws DAOException {
        return documentDAO().retrieveDocuments(filter);
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document[] getMigratedPdfDocuments() throws DAOException {
        return documentDAO().retrieveMigratedPdfDocuments();
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document[] getDocumentsByPath(String documentPath) throws DAOException {
        return documentDAO().retrieveDocumentsByPath(documentPath);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void setDocumentModified(int documentID, int userID)
            throws DAOException {
        Transaction tx = TransactionFactory.createTransaction();
        DocumentDAO dao = documentDAO(tx);

        try {
            Document doc = dao.retrieveDocument(documentID);
            doc.setLastModifiedBy(userID);
            doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            dao.modifyDocument(doc);

            tx.complete();
        }
        catch (DAOException de) {
            cancelTransaction(tx, de);
        }
        finally {
            closeTransaction(tx);
        }
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void setDocumentCheckedOut(int documentID, int userID)
            throws DAOException {

        Transaction tx = TransactionFactory.createTransaction();
        DocumentDAO dao = documentDAO(tx);

        try {
            Document doc = dao.retrieveDocument(documentID);
            doc.setLastCheckoutBy(userID);
            dao.modifyDocument(doc);

            tx.complete();
        }
        catch (DAOException de) {
            cancelTransaction(tx, de);
        }
        finally {
            closeTransaction(tx);
        }
    }

    /**
     * Retrieves the list of documents that match a given full-text query and/or
     * type
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document[] searchDocumentsFullText(String fullTextQuery,
            int maxNumberOfHits) throws DAOException {

        return documentDAO().searchDocumentsFullText(fullTextQuery, maxNumberOfHits);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public TemplateDocument retrieveTemplateDocument(String documentID)
        throws DAOException {
        return documentDAO().retrieveTemplateDocument(documentID);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public TemplateDocument[] getTemplateDocuments() throws DAOException {
        return documentDAO().retrieveTemplateDocuments();
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Document uploadTempDocument(Document doc, String pathToFile,
                                       InputStream fileStream) throws DAOException {
        checkNull(doc);
        
        if (!doc.isValid()) {
            logger.error("Document invalid.");
            throw new DAOException("Document invalid.");
        }

        try {
            doc.setTemporary(true);
            if (doc.getLastModifiedTS() == null) {
                doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            }
            doc.setUploadDate(doc.getLastModifiedTS());
            documentDAO().createDocument(doc);
            DocumentUtil.createDocument(doc.getPath(), fileStream);
        } 
        catch (IOException ioe) {
            try {
                DocumentUtil.removeDocument(doc.getPath());
            }
            catch (IOException ioex) {
                logger.error(ioex.getMessage(), ioex);
            }
            throw new DAOException("Unable to create document.", ioe);
        }

        return doc;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Document uploadDocument(Document doc, String pathToFile,
                                   InputStream fileStream, Transaction trans)
        throws DAOException {
        checkNull(doc);
        if (!doc.isValid()) {
            logger.error("Document is invalid.");
            throw new DAOException("Document is invalid.");
        }

        try {
            doc.setTemporary(false);
            if (doc.getLastModifiedTS() == null) {
                doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            }
            doc.setUploadDate(doc.getLastModifiedTS());
            documentDAO(trans).createDocument(doc);
            DocumentUtil.createDocument(doc.getPath(), fileStream);
        } 
        catch (IOException ioe) {
            try {
                DocumentUtil.removeDocument(doc.getPath());
            }
            catch (IOException ioex) {
                logger.error(ioex.getMessage(), ioex);
            }
            throw new DAOException("Unable to create document.", ioe);
        }

        return doc;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Document uploadDocument(Document doc,
                                   InputStream fileStream, boolean staging)
        throws DAOException {
        checkNull(doc);

        try {
            doc.setTemporary(false);
            if (doc.getLastModifiedTS() == null) {
                doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            }
            doc.setUploadDate(doc.getLastModifiedTS());
            doc = getDocumentDAO(staging).createDocument(doc);
            DocumentUtil.createDocument(doc.getPath(), fileStream);
        } catch (IOException ioe) {
            try {
                DocumentUtil.removeDocument(doc.getPath());
            } catch (IOException ioex) {
                logger.error(ioex.getMessage(), ioex);
            }
            throw new DAOException("Unable to create document.", ioe);
        }

        return doc;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Document updateDocument(Document doc) throws DAOException {
        checkNull(doc);

        try {
            if (doc.getLastModifiedTS() == null) {
                doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
            }
            doc.setUploadDate(doc.getLastModifiedTS());
            documentDAO().modifyDocument(doc);
        }
        catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            throw new DAOException("Unable to modify document/attachment.", ioe);
        }
        return doc;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Document deleteDocument(Document doc) throws DAOException {
        try {
            documentDAO().removeDocument(doc);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            throw new DAOException("Unable to delete document.", ioe);
        }
        return doc;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeTemporaryDocuments(Integer daysOld) throws DAOException {
    	boolean logFailures = true;
    	// limit to requesting delete at most 100 times.
    	for(int i = 0; i < 100; i++) {
    		boolean deletedFiles = documentDAO().removeTemporaryDocuments(daysOld, logFailures);
    		logFailures = false;
    		if(!deletedFiles) break;
    	}
    }
        
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Document[] retrieveNonTemporaryDocuments(Integer daysOld) throws DAOException {
        return documentDAO().retrieveNonTemporaryDocuments(daysOld);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public String createTmpDir(String userName) throws DAOException {
        String dirName = null;
        checkNull(userName);
        if (userName.length() < 1) {
            throw new DAOException("Missing userName parameter.");
        }
        try {
            dirName = File.separator + "tmp" + File.separator + userName;
            try {
                DocumentUtil.mkDir(dirName);
            }
            catch (Exception e) {
                // Eat this. We just want to create the tmp dir for the user in case it 
                // does not exist.
                //throw new DAOException("Could not create tmp dir " + dirName + ".");
            }
            dirName += File.separator + Long.toString(Calendar.getInstance().getTimeInMillis());
            DocumentUtil.mkDir(dirName);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DAOException("Could not create tmp dir " + dirName + ".");
        }
        return dirName;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public String createFacilityTmpDir(String facilityId, String userName) throws DAOException {
        String dirName = null;
        checkNull(facilityId);
        checkNull(userName);
        
        if (facilityId.length() < 1) {
            throw new DAOException("Missing facilityId parameter.");
        }
        if (userName.length() < 1) {
            throw new DAOException("Missing userName parameter.");
        }
        try {
            dirName = File.separator + "Facilities" + File.separator + facilityId 
                + File.separator + "tmp" + File.separator + userName;
            try {
                DocumentUtil.mkDir(dirName);
            }
            catch (Exception e) {
                // Eat this. We just want to create the tmp dir for the user in case it 
                // does not exist.
                //throw new DAOException("Could not create tmp dir " + dirName + ".");
            }
            // Adds a timestamp to dirName. Comment out if not required.
            // dirName += File.separator + Long.toString(Calendar.getInstance().getTimeInMillis());
            DocumentUtil.mkDir(dirName);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DAOException("Could not create tmp dir " + dirName + ".");
        }
        return dirName;
    }

}
