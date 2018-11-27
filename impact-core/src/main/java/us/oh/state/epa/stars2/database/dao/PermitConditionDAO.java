package us.oh.state.epa.stars2.database.dao;

import java.util.List;

import us.oh.state.epa.stars2.app.facility.PermitConditionSearchCriteria;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSupersession;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface PermitConditionDAO extends TransactableDAO {
	
	/**
	 * @param permitId
	 * @return
	 * @throws DAOException
	 */
	List<PermitCondition> retrievePermitConditionList(int permitId) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removePermitConditionList(int permitId) throws DAOException;

	/**
	 * @param pc
	 * @return
	 * @throws DAOException
	 */
	PermitCondition createPermitCondition(PermitCondition pc) throws DAOException;

	/**
	 * @param pc
	 * @throws DAOException
	 */
	void modifyPermitCondition(PermitCondition pc) throws DAOException;

	/**
	 * @param pc
	 * @throws DAOException
	 */
	void removePermitCondition(PermitCondition pc) throws DAOException;
	
	boolean createAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException;

	List<Integer> retrieveAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;

	List<String> retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;

	void deleteAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException;

	void deleteAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;
	
	void markInactiveNSRPermitsToExpired() throws DAOException;
	
	/**
	 * Validate the permit condition number is duplicate within the permit or
	 * not.
	 * 
	 * @param permit
	 *            ID, permit condition number
	 * @return
	 * @throws DAOException
	 */	
	boolean isDuplicatePermitConditionNumber(PermitCondition permitCondition) throws DAOException;

	PermitConditionSearchLineItem[] searchPermitConditions(PermitConditionSearchCriteria searchCriteria)
			throws DAOException;
			
	ComplianceStatusEvent createComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException;
	
	void modifyComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException;
	
	void removeComplianceStatusEvent(Integer complianceStatusId) throws DAOException;
	
	ComplianceStatusEvent retrieveComplianceStatusEvent(Integer complianceStatusId) throws DAOException;
	
	List<ComplianceStatusEvent> retrieveComplianceStatusEventList(Integer permitConditionId) throws DAOException;
	
	void removeComplianceStatusEventList(Integer permitConditionId) throws DAOException;
	
	/**
	 * @param permitConditionId
	 * @return a list of PermitConditions that were superseded by this PermitCondition
	 * @throws DAOException
	 */
	List<PermitConditionSupersession> retrievePermitConditionsSupersededByThis(Integer permitConditionId) throws DAOException;
	
	/**
	 * @param permitConditionId
	 * @return a list of PermitConditions that superseded this PermitCondition
	 * @throws DAOException
	 */
	List<PermitConditionSupersession> retrieveThisPermitConditionSupersededByList(Integer permitConditionId) throws DAOException;
	
	/**
	 * @param pcs
	 * @throws DAOException
	 */
	void createPermitConditionSupersession(PermitConditionSupersession pcs) throws DAOException;
	
	/**
	 * @param pcs
	 * @throws DAOException
	 */
	void modifyPermitConditionSupersession(PermitConditionSupersession pcs) throws DAOException;

	/**
	 * @param pc
	 * @throws DAOException
	 */
	void removePermitConditionSupersession(PermitConditionSupersession pcs) throws DAOException;	
	
	/**
	 * @param permitConditionId
	 * @return PermitCondition
	 * @throws DAOException
	 */
	PermitCondition retrievePermitConditionById(Integer permitConditionId) throws DAOException;
	
	boolean createPermitConditionCategory(Integer permitConditionId, String categoryCd) throws DAOException;

	void deletePermitConditionCategoriesByConditionId(Integer permitConditionId) throws DAOException;
	
	List<String> retrievePermitConditionCategoriesByConditionId(Integer permitConditionId) throws DAOException;

	PermitConditionSupersession retrievePermitConditionSupersession(Integer suoersededPermitConditionSupersession, 
			Integer supersedingPermitConditionSupersession)  throws DAOException;

	List<ComplianceStatusEvent> retrieveComplianceStatusEventListByReferencedInspection(Integer inspectionId)
			throws DAOException;

	List<ComplianceStatusEvent> retrieveComplianceStatusEventListByReferencedComplianceReport(
			Integer complianceReportId) throws DAOException;

	List<ComplianceStatusEvent> retrieveComplianceStatusEventListByReferencedStackTest(Integer stackTestId)
			throws DAOException;

	Integer retrieveComplianceStatusEventsCount(Integer permitConditionId) throws DAOException;

	Integer retrievePermitConditionsCountByPermitIds(List<Integer> permitIds) throws DAOException;

	void deletePermitConditionSupersession(Integer superseded_permit_condition_id) throws DAOException;

}
