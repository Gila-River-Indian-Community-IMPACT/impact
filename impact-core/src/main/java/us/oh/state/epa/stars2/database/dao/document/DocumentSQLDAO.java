package us.oh.state.epa.stars2.database.dao.document;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;

@Repository
public class DocumentSQLDAO extends AbstractDAO implements DocumentDAO {

	private Logger logger = Logger.getLogger(DocumentSQLDAO.class);
	
    public static final String FULL_TEXT_INDEX_NAME = "document_idx";
    public static final String FULL_TEXT_INDEX_SPACE = "4M";

    /**
     * @see DocumentDAO#retrieveDocument(documentID)
     */
    public final Document retrieveDocument(int documentID) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DocumentSQL.retrieveDocument", true);

        connHandler.setInteger(1, documentID);

        return (Document) connHandler.retrieve(Document.class);
    }

    public final Document[] retrieveDocumentsByPath(String documentPath)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "DocumentSQL.retrieveDocumentsByPath", true);

        connHandler.setString(1, documentPath);
        ArrayList<Document> ret = connHandler.retrieveArray(Document.class);

        return ret.toArray(new Document[0]);
    }
    
    public final Document[] retrieveMigratedPdfDocuments() throws DAOException {
        ConnectionHandler connHandler = new 
            ConnectionHandler("DocumentSQL.retrieveMigratedPdfDocuments", true);

        ArrayList<Document> ret = connHandler.retrieveArray(Document.class);

        return ret.toArray(new Document[0]);
    }

    /**
     * @see DocumentDAO#retrieveDocuments(int documentID[])
     */
    public final Document[] retrieveDocuments(int documentID[]) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(true);
//      String query = DAOFactory.loadSQL("DocumentSQL.retrieveDocuments");
      String query = getSqlLoader().find("DocumentSQL.retrieveDocuments",null);

        // convert the int array into a comma-delimited string list
        String temp = "";
        for (int i = 0; i < documentID.length; i++) {
            if (i == 0) {
                temp = Integer.toString(documentID[i]);
            } else {
                temp = temp + "," + documentID[i];
            }
        }
        query = query.replaceAll("\\?", temp);
        connHandler.setSQLStringRaw(query);

        ArrayList<Document> ret = connHandler.retrieveArray(Document.class);

        return ret.toArray(new Document[0]);
    }

    /**
     * @see DocumentDAO#createDocument(doc)
     */
    public final Document createDocument(Document doc) throws DAOException {
        checkNull(doc);

        ConnectionHandler connHandler = new ConnectionHandler(
                "DocumentSQL.createDocument", false);

        int i = 1;
        doc.setDocumentID(nextSequenceVal("S_Document_Id", doc.getDocumentID()));

        connHandler.setInteger(i++, doc.getDocumentID());
        connHandler.setString(i++, doc.getFacilityID());
        connHandler.setInteger(i++, doc.getLastCheckoutBy());
        connHandler.setInteger(i++, doc.getLastModifiedBy());
        connHandler.setTimestamp(i++, doc.getLastModifiedTS());
        connHandler.setString(i++, doc.getBasePath());
        connHandler.setTimestamp(i++, doc.getUploadDate());
        connHandler.setString(i++, doc.getDescription());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(doc.isTemporary()));
        
        connHandler.update();

        doc.setLastModified(1);

        return doc;
    }

    /**
     * @see DocumentDAO#modifyDocument(doc)
     */
    public final void modifyDocument(Document doc) throws DAOException {
        checkNull(doc);
        ConnectionHandler connHandler = new ConnectionHandler(
                "DocumentSQL.modifyDocument", false);

        int i = 1;

        connHandler.setString(i++, doc.getFacilityID());
        connHandler.setInteger(i++, doc.getLastCheckoutBy());
        connHandler.setInteger(i++, doc.getLastModifiedBy());
        connHandler.setTimestamp(i++, new Timestamp(System.currentTimeMillis()));
        connHandler.setString(i++, doc.getBasePath());
        connHandler.setTimestamp(i++, doc.getUploadDate());
        connHandler.setString(i++, doc.getDescription());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(doc
                .isTemporary()));
        connHandler.setInteger(i++, doc.getDocumentID());

        if (connHandler.update()) {
            // update last modified to reflect what's in the database
            doc.setLastModified(doc.getLastModified() + 1);
        }

    }

    /**
     * @see DocumentDAO#removeDocument(doc)
     */
    public final void removeDocument(Document doc) throws DAOException {
        checkNull(doc);
        
        try {
            removeRows("dc_document", "document_id", doc.getDocumentID());
            DocumentUtil.removeDocument(doc.getPath());
        }
        catch (IOException ioe) {
            throw new DAOException("Could not delete document " + doc.getPath(), ioe);
        }
    }

    /**
     * @see DocumentDAO#searchDocumentsFullText(fullTextQuery, maxNumberOfHits)
     */
    public final Document[] searchDocumentsFullText(String fullTextQuery,
            int maxNumberOfHits) throws DAOException {
        StringBuffer sql = new StringBuffer(
                loadSQL("DocumentSQL.retrieveDocument"));

        StringBuffer sqlWhere = new StringBuffer("1 = 1");
        if (fullTextQuery != null && fullTextQuery.length() > 0) {
            sqlWhere.append(" and contains(d.path, ?) > 0");
        }

        if (sqlWhere.length() > 0) {
            sql.append(" where " + sqlWhere);
        }

        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(sql.toString());

        logger.debug("search doc query: " + sql);
        int paramIdx = 1;
        if (fullTextQuery != null && fullTextQuery.length() > 0) {
            String escapedFullTextQuery;

            // The minus sign (-) is an operator in Oracle Text, escape it
            if (fullTextQuery.indexOf("-") < 0) {
                escapedFullTextQuery = fullTextQuery;
            } else {
                escapedFullTextQuery = fullTextQuery.replace("-", "\\-");
            }
            connHandler.setString(paramIdx++, escapedFullTextQuery);
        }

        ArrayList<Document> ret = connHandler.retrieveArray(Document.class,
                maxNumberOfHits);

        return ret.toArray(new Document[0]);
    }

    /**
     * @see DocumentDAO#refreshFullTextIndex()
     */
    public final void refreshFullTextIndex() throws DAOException {
        CallableStatement ps = null;
        Connection conn = null;

        try {
            conn = getReadOnlyConnection();
            ps = conn.prepareCall("{call ctx_ddl.sync_index (?, ?)}");
            ps.setString(1, FULL_TEXT_INDEX_NAME);
            ps.setString(2, FULL_TEXT_INDEX_SPACE);
            ps.execute();
        } catch (Exception ex) {
            handleException(ex, conn);
        } finally {
            closeStatement(ps);
            handleClosing(conn);
        }
    }

    /**
     * @see DocumentDAO#retrieveTemplateDocument()
     */
    public final TemplateDocument retrieveTemplateDocument(String documentID) throws DAOException {

        ConnectionHandler connHandler 
            = new ConnectionHandler("DocumentSQL.retrieveTemplateDocument", true);

        //connHandler.setInteger(1, documentID);
        connHandler.setString(1, documentID);

        return (TemplateDocument) connHandler.retrieve(TemplateDocument.class);

    }

    /**
     * @see DocumentDAO#retrieveTemplateDocuments()
     */
    public final TemplateDocument[] retrieveTemplateDocuments() throws DAOException {

        ConnectionHandler connHandler 
            = new ConnectionHandler("DocumentSQL.retrieveTemplateDocuments", true);
                
        ArrayList<TemplateDocument> ret 
            = connHandler.retrieveArray(TemplateDocument.class);

        return ret.toArray(new TemplateDocument[0]);
    }

    /**
     * @see DocumentDAO#unMarkTempPermitDocuments(int permitId)
     */
    public final void unMarkTempDocument(Integer documentID) throws DAOException {

        checkNull(documentID);
        ConnectionHandler connHandler 
            = new ConnectionHandler("DocumentSQL.unMarkTempDocument", false);
        connHandler.setInteger(1, documentID);
        connHandler.remove();
    }

    /**
     * @see DocumentDAO#removeTemporaryDocuments(Integer daysOld)
     */
    public final boolean removeTemporaryDocuments(Integer daysOld, boolean logFailures) throws DAOException {

        checkNull(daysOld);
        ConnectionHandler connHandler 
            = new ConnectionHandler("DocumentSQL.selectTemporaryDocuments", false);

        Timestamp ts 
            = new Timestamp(Calendar.getInstance().getTimeInMillis() - (daysOld.longValue() * 86400000));
        connHandler.setTimestamp(1, ts);

        ArrayList<Document> docs 
            = connHandler.retrieveArray(Document.class);
        int cnt = 1000;
        boolean deletedFiles = false;
        for (BaseDBObject doc : docs) {
            Document tmpDoc = (Document) doc;
            String s = "temporary docID = " + tmpDoc.getDocumentID() + ", path = " + tmpDoc.getPath();
            logger.debug("Removing " + s);
            try {
                removeDocument(tmpDoc);
                cnt--;
                deletedFiles = true;
                if(cnt <= 0) break;
            } catch (DAOException e) {
            	if(logFailures) logger.error("Failed to remove " + s, e);
            }
        }
        return deletedFiles;
    }
    
    public final Document[] retrieveNonTemporaryDocuments(Integer daysOld) throws DAOException {

        ConnectionHandler connHandler 
            = new ConnectionHandler("DocumentSQL.selectAllNonTemporaryDocuments", true);
        Timestamp ts 
            = new Timestamp(Calendar.getInstance().getTimeInMillis() - (daysOld.longValue() * 86400000));
        connHandler.setTimestamp(1, ts);

        ArrayList<Document> ret = connHandler.retrieveArray(Document.class);

        return ret.toArray(new Document[0]);
    }

    /*
     * @see DocumentDAO#getNextTmpDir()
     */
    public final String getNextTmpDir() throws DAOException {
        return nextSequenceVal("S_TmpDir_Id").toString();
    }
    
    /**
     * @see DocumentDAO#removeDocumentReference(doc)
     */
    public final void removeDocumentReference(Integer documentId) throws DAOException {
           removeRows("dc_document", "document_id", documentId);
    }

}
