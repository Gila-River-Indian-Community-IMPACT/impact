package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementActionEvent;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface EnforcementActionDAO extends TransactableDAO {
	
	/**
	 * Create a new EnforcementAction.
	 * @param enforcementAction
	 * @return
	 * @throws DAOException
	 */
	EnforcementAction createEnforcementAction(EnforcementAction enforcementAction) throws DAOException;
	
	/**
	 * Delete EnforcementAction.
	 * @param enforcementAction
	 * @return
	 * @throws DAOException
	 */
	void deleteEnforcementAction(EnforcementAction enforcementAction) throws DAOException;
	
	/**
	 * Update EnforcementAction.
	 * @param enforcementAction
	 * @return
	 * @throws DAOException
	 */
	boolean modifyEnforcementAction(EnforcementAction enforcementAction) throws DAOException;
	
	/**
	 * Retrieve EnforcementAction.
	 * @param enforcementActionid
	 * @return
	 * @throws DAOException
	 */
	public EnforcementAction retrieveEnforcementAction(Integer enforcementActionId) throws DAOException;
	
	/**
	 * Create enforcement action referral type.
	 * @param referralTypeCd
	 * @param enforcementActionid
	 * @return
	 * @throws DAOException
	 */
	public void createReferalType(String referralTypeCd, Integer enforcementActionId) throws DAOException;
	
	/**
	 * Delete enforcement action referral type.
	 * @param enforcementActionid
	 * @return
	 * @throws DAOException
	 */
	public void deleteReferralTypeById(Integer enforcementActionId) throws DAOException;
	
	/**
	 * Retrieve all Enforcement objects for the specified search parameters. All
	 * EnforcementActions related to the Enforcements will be retrieved as well.
	 * @param facilityId
	 * @param facilityName
	 * @param doLaaCd
	 * @param countyCd
	 * @param naicsCd
	 * @param addToHPVList
	 * @param beginActionDt
	 * @param endActionDt
	 * @param evaluator
	 * @return
	 * @throws DAOException
	 */
	List<EnforcementAction> searchEnforcementActions(String facilityId, String facilityName, String doLaaCd,
			String countyCd, String addToHPVList, 
			String eventCd,
			Timestamp beginActionDt, Timestamp endActionDt,
			Integer evaluator, String enforcementId, List<String> enfStateCases, String enfCaseNum,
			String actionTypeCd, String cmpId,
			String docketNumber) throws DAOException;
	/**
	 * Retrieve all attachments for the enforcement identified by enforcementId.
	 * @param enforcementId
	 * @return
	 * @throws DAOException
	 */
	List<EnforcementAttachment> retrieveEnforcementAttachments(int enforcementId) throws DAOException;
	
	/**
	 * Retrieve all notes for the enforcement identified by enforcementId.
	 * @param enforcementId
	 * @return
	 * @throws DAOException
	 */
	List<EnforcementNote> retrieveEnforcementNotes(int enforcementId) throws DAOException;
	
	/**
	 * Add a new enforcement note.
	 * @param enforcementId
	 * @param noteId
	 * @throws DAOException
	 */
	void addEnforcementNote(int enforcementId, int noteId) throws DAOException;
	
	/**
	 * Delete all notes for the enforcement identified by enforcementId.
	 * @param enforcementId
	 * @throws DAOException
	 */
	void deleteEnforcementNotes(int enforcementId) throws DAOException;
	
	/**
	 * Delete attachment for the enforcement attachment identified by att.
	 * @param att
	 * @return
	 * @throws DAOException
	 */
	void deleteEnforcementAttachment(EnforcementAttachment att) throws DAOException;
	 
	/**
	 * Create attachment for the enforcement attachment identified by fa.
	 * @param fa
	 * @return
	 * @throws DAOException
	 */
	EnforcementAttachment createEnforcementAttachment(EnforcementAttachment fa) throws DAOException;
	
	/**
	 * Modify attachment for the enforcement attachment identified by doc.
	 * @param doc
	 * @return
	 * @throws DAOException
	 */
	boolean modifyEnforcementAttachment(EnforcementAttachment doc) throws DAOException;
	
	/**
	 * @param enforcementActionId
	 * @return
	 * @throws DAOException
	 */
	List<EnforcementActionEvent> retrieveEnforcementActionEventList(int enforcementActionId)
			throws DAOException;

	/**
	 * @param enforcementActionId
	 * @throws DAOException
	 */
	void removeEnforcementActionEventList(int enforcementActionId) throws DAOException;

	/**
	 * @param eae
	 * @return
	 * @throws DAOException
	 */
	EnforcementActionEvent createEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException;

	/**
	 * @param eae
	 * @throws DAOException
	 */
	void modifyEnforcementActionEvent(EnforcementActionEvent eae) throws DAOException;

	/**
	 * @param eae
	 * @throws DAOException
	 */
	void removeEnforcementActionEvent(EnforcementActionEvent eae) throws DAOException;
	
	EnforcementAction[] retrieveEnforcementActionByFacilityId(String facilityId)
			throws DAOException;
}