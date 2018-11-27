/*
 * Generated by XDoclet - Do not edit!
 */
package us.oh.state.epa.stars2.bo;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.framework.exception.DAOException;

/**
 * Service interface for CorrespondenceEJB.
 */
public interface CorrespondenceService {
	/**
	 * Locate all of the correspondence for a given facility.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.document.Correspondence[] searchCorrespondenceByFacility(
			java.lang.String facilityID)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Locate all of the correspondence for a given facility.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.document.Correspondence[] searchCorrespondenceByLinkedToId(
			java.lang.Integer linkedToId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Create a new row in the DC_CORRESPONDENCE table.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.document.Correspondence createCorrespondence(
			us.oh.state.epa.stars2.database.dbObjects.document.Correspondence correspondence)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			us.oh.state.epa.stars2.framework.exception.ValidationException,
			java.rmi.RemoteException;

	/**
	 * Create a new row in the DC_CORRESPONDENCE table.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.document.Correspondence createCorrespondence(
			us.oh.state.epa.stars2.database.dbObjects.document.Correspondence correspondence,
			us.oh.state.epa.stars2.database.dao.Transaction trans)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			us.oh.state.epa.stars2.framework.exception.ValidationException,
			java.rmi.RemoteException;

	/**
	 * Update a row in the DC_CORRESPONDENCE table.
	 */
	public void updateCorrespondence(
			us.oh.state.epa.stars2.database.dbObjects.document.Correspondence correspondence)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			us.oh.state.epa.stars2.framework.exception.ValidationException,
			java.rmi.RemoteException;

	/**
	 * Delete row in the DC_CORRESPONDENCE table.
	 */
	public void deleteCorrespondence(
			us.oh.state.epa.stars2.database.dbObjects.document.Correspondence correspondence)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Correspondence[] searchCorrespondence(
			us.oh.state.epa.stars2.database.dbObjects.document.Correspondence correspondence, java.lang.String dateField,
			java.sql.Timestamp startDate, java.sql.Timestamp endDate)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public boolean modifyCorrespondence(
			us.oh.state.epa.stars2.database.dbObjects.document.Correspondence correspondence
			)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.document.Correspondence retrieveCorrespondence(
			int correspondenceId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public void createFollowUpActionNotices() throws DAOException, RemoteException;

	public boolean doWorkflowsExist(Integer corrId) throws DAOException, RemoteException;
	
	public us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment createCorrespondenceAttachment(
			java.lang.Integer correspondenceId,
			us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment correspondenceAttachment,
			java.io.InputStream fileStream)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment createCorrespondenceAttachment(
			java.lang.Integer correspondenceId,
			us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment correspondenceAttachment
			)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment[] retrieveCorrespondenceAttachments(
			java.lang.Integer correspondenceId) throws us.oh.state.epa.stars2.framework.exception.DAOException;
	
	public us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment updateCorrespondenceAttachment(
			us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment correspondenceAttachment)
			 throws us.oh.state.epa.stars2.framework.exception.DAOException;
	
	public void removeCorrespondenceAttachment(
			us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment correspondenceAttachment)
			 throws us.oh.state.epa.stars2.framework.exception.DAOException;
	
	public void createCorrespondenceWithAttachment(us.oh.state.epa.stars2.database.dbObjects.document.Correspondence correspondence, 
			us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment attachment, 
			java.io.InputStream is)
		throws us.oh.state.epa.stars2.framework.exception.DAOException;

}