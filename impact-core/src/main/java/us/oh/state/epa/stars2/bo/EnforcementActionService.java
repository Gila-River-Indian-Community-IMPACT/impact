/*
 * Generated by XDoclet - Do not edit!
 */
package us.oh.state.epa.stars2.bo;

import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementActionEvent;
import us.oh.state.epa.stars2.framework.exception.DAOException;


/**
 * Service interface for EnforcementEJB.
 */
public interface EnforcementActionService {

	public us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction createEnforcementAction(
			us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction enforcementAction)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException,
			us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
	
	public void deleteEnforcementAction(
			us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction enforcementAction)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public boolean modifyEnforcementAction(
			us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction enforcementAction)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction retrieveEnforcementAction(
			java.lang.Integer enforcementActionId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public void createReferalType(java.lang.String referralTypeCd, java.lang.Integer enforcementActionId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;
	
	public void deleteReferralTypeById(java.lang.Integer enforcementActionId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException, java.rmi.RemoteException;;
			
	public java.util.List<us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction> searchEnforcementActions(
			java.lang.String facilityId, java.lang.String facilityName,
			java.lang.String doLaaCd, java.lang.String countyCd,
			java.lang.String addToHPVList, 
			java.lang.String eventCd, java.sql.Timestamp beginActionDt,
			java.sql.Timestamp endActionDt, java.lang.Integer evaluator,
			java.lang.String enforcementId,
			java.util.List<java.lang.String> enfStateCases,
			java.lang.String enfCaseNum, java.lang.String actionTypeCd,
			java.lang.String cmpId,
			java.lang.String docketNumber)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public java.util.List<us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAttachment> retrieveEnforcementAttachments(
			int enforcementId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote createEnforcementNote(
			us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote note)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public void modifyEnforcementNote(
			us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote note)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public java.util.List<us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote> retrieveEnforcementNotes(
			int enforcementActionId)	
			throws us.oh.state.epa.stars2.framework.exception.DAOException,	java.rmi.RemoteException;
	
	/**
	 * Create a new row in the Attachment table.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment createEnforcementAttachment(
			us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction enf,
			us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment attachment,
			java.io.InputStream fileStream)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			us.oh.state.epa.stars2.framework.exception.ValidationException,
			java.rmi.RemoteException;

	public void removeEnforcementAttachment(
			us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAttachment doc)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public void modifyEnforcementAttachment(
			us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAttachment doc)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public List<EnforcementActionEvent> retrieveEnforcementActionEventList(
			Integer enforcementActionId) throws DAOException;

	public void createEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException;

	public void modifyEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException;

	public void removeEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException;
	
	EnforcementAction[] retrieveEnforcementActionByFacilityId(String facilityId)
			throws DAOException;
			
}