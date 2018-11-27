/*
 * Generated by XDoclet - Do not edit!
 */
package us.oh.state.epa.stars2.bo;

/**
 * Service interface for DocumentEJB.
 */
public interface DocumentService {

	public us.oh.state.epa.stars2.database.dbObjects.document.Document getDocumentByID(
			int documentID, boolean readOnlyDB)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document getReadOnlyDocumentByID(
			int documentID)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document[] getDocumentsByID(
			int[] filter)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document[] getMigratedPdfDocuments()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document[] getDocumentsByPath(
			java.lang.String documentPath)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public void setDocumentModified(int documentID, int userID)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public void setDocumentCheckedOut(int documentID, int userID)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Retrieves the list of documents that match a given full-text query and/or
	 * type
	 */
	public us.oh.state.epa.stars2.database.dbObjects.document.Document[] searchDocumentsFullText(
			java.lang.String fullTextQuery, int maxNumberOfHits)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument retrieveTemplateDocument(
			java.lang.String documentID)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument[] getTemplateDocuments()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document uploadTempDocument(
			us.oh.state.epa.stars2.database.dbObjects.document.Document doc,
			java.lang.String pathToFile, java.io.InputStream fileStream)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document uploadDocument(
			us.oh.state.epa.stars2.database.dbObjects.document.Document doc,
			java.lang.String pathToFile, java.io.InputStream fileStream,
			us.oh.state.epa.stars2.database.dao.Transaction trans)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document uploadDocument(
			us.oh.state.epa.stars2.database.dbObjects.document.Document doc,
			java.io.InputStream fileStream, boolean staging)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document updateDocument(
			us.oh.state.epa.stars2.database.dbObjects.document.Document doc)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document deleteDocument(
			us.oh.state.epa.stars2.database.dbObjects.document.Document doc)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public void removeTemporaryDocuments(java.lang.Integer daysOld)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Document[] retrieveNonTemporaryDocuments(
			java.lang.Integer daysOld)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public java.lang.String createTmpDir(java.lang.String userName)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public java.lang.String createFacilityTmpDir(java.lang.String facilityId,
			java.lang.String userName)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

}
