package us.oh.state.epa.stars2.bo;

import java.util.List;

import us.oh.state.epa.stars2.app.facility.PermitConditionSearchCriteria;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSupersession;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface PermitConditionService {

	public List<PermitCondition> retrievePermitConditionList(Integer permitId) throws DAOException;

	public void createPermitCondition(PermitCondition pc, List<Integer> corrEuIdsToAssociate,
			List<Integer> corrEuIdsToDisassociate) throws DAOException;

	public void modifyPermitCondition(PermitCondition pc, List<Integer> corrEuIdsToAssociate,
			List<Integer> corrEuIdsToDisassociate) throws DAOException;
	
	public void modifyPermitCondition(PermitCondition pc) throws DAOException;

	public void removePermitCondition(PermitCondition pc) throws DAOException;

	boolean createAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException;

	List<Integer> retrieveAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;

	List<String> retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;

	void updateAssociatedCorrEuIds(Integer permitConditionId, List<Integer> corrEuIdsToAssociate,
			List<Integer> corrEuIdsToDisassoicate) throws DAOException;

	void deleteAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException;

	void deleteAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;

	public boolean isDuplicatePermitConditionNumber(PermitCondition permitCondition) throws DAOException;

	PermitConditionSearchLineItem[] searchPermitConditions(PermitConditionSearchCriteria searchCriteria)
			throws DAOException;
			
	void createComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException;

	void modifyComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException;

	void removeComplianceStatusEvent(Integer permitConditionId) throws DAOException;

	List<ComplianceStatusEvent> retrieveComplianceStatusEventList(Integer permitConditionId) throws DAOException;

	void removeComplianceStatusEventList(Integer permitConditionId) throws DAOException;

	ComplianceStatusEvent retrieveComplianceStatusEvent(Integer complianceStatusId) throws DAOException;
	
	List<PermitConditionSupersession> retrieveThisConditionSupersededByList(Integer permitConditionId) throws DAOException;

	List<PermitConditionSupersession> retrieveThisConditionSupersedesList(Integer permitConditionId) throws DAOException;
	
	PermitCondition retrievePermitConditionById(Integer permitConditionId) throws DAOException;
	
	void createPermitConditionSupersession(PermitConditionSupersession cs) throws DAOException;

	void modifyPermitConditionSupersession(PermitConditionSupersession cs) throws DAOException;

	void deletePermitConditionSupersession(PermitConditionSupersession cs) throws DAOException;

	boolean createPermitConditionCategory(Integer permitConditionId, String categoryCd) throws DAOException;

	void deletePermitConditionCategoriesByConditionId(Integer permitConditionId) throws DAOException;

	List<String> retrievePermitConditionCategoriesByConditionId(Integer permitConditionId) throws DAOException;
		
	PermitConditionSupersession retrievePermitConditionSupersession(Integer SuoersededPermitConditionSupersession, 
			Integer SupersedingPermitConditionSupersession)  throws DAOException;

	Integer retrievePermitConditionsCountByPermitIds(List<Integer> permitIds) throws DAOException;

	public void deletePermitConditionSupersessionForPurge(Integer supersededPermitConditionId) throws DAOException;

}
