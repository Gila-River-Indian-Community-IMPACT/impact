package us.oh.state.epa.stars2.bo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.app.facility.PermitConditionSearchCriteria;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSupersession;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;

@Transactional(rollbackFor = Exception.class)
@Service
public class PermitConditionBO extends BaseBO implements PermitConditionService {

	@Override
	public List<PermitCondition> retrievePermitConditionList(Integer permitId) throws DAOException {

		List<PermitCondition> pcpList = null;

		pcpList = permitConditionDAO().retrievePermitConditionList(permitId);
		
		for (PermitCondition pc : pcpList) {
			pc.setSupersedesThis(retrieveThisConditionSupersededByList(pc.getPermitConditionId()));
			pc.setSupersededByThis(retrieveThisConditionSupersedesList(pc.getPermitConditionId()));
		}

		return pcpList;
	}

	@Override
	public void createPermitCondition(PermitCondition pc, List<Integer> corrEuIdsToAssociate,
			List<Integer> corrEuIdsToDisassociate) throws DAOException {

		permitConditionDAO().createPermitCondition(pc);
		
		for(String categoryCd : pc.getPermitConditionCategoryCds()) {
			createPermitConditionCategory(pc.getPermitConditionId(), categoryCd);
		}

		if (!Utility.isNullOrEmpty(pc.getFacilityWide())) {
			if (pc.getFacilityWide().equalsIgnoreCase("Y")) {
				deleteAssociatedCorrEuIdsByPermitConditionId(pc.getPermitConditionId());
			} else {
				updateAssociatedCorrEuIds(pc.getPermitConditionId(), corrEuIdsToAssociate, corrEuIdsToDisassociate);
			}
		}
	}

	@Override
	public void modifyPermitCondition(PermitCondition pc, List<Integer> corrEuIdsToAssociate,
			List<Integer> corrEuIdsToDisassociate) throws DAOException {

		permitConditionDAO().modifyPermitCondition(pc);

		// update category codes
		deletePermitConditionCategoriesByConditionId(pc.getPermitConditionId());
		for(String categoryCd : pc.getPermitConditionCategoryCds()) {
			createPermitConditionCategory(pc.getPermitConditionId(), categoryCd);
		}
		
		if (Utility.isNullOrEmpty(pc.getFacilityWide()) || pc.getFacilityWide().equalsIgnoreCase("Y")) {
			deleteAssociatedCorrEuIdsByPermitConditionId(pc.getPermitConditionId());
		} else {
			updateAssociatedCorrEuIds(pc.getPermitConditionId(), corrEuIdsToAssociate, corrEuIdsToDisassociate);
		}
	}
	
	@Override
	public void modifyPermitCondition(PermitCondition pc) throws DAOException {
		permitConditionDAO().modifyPermitCondition(pc);		
	}


	@Override
	public void removePermitCondition(PermitCondition pc) throws DAOException {

		permitConditionDAO().deleteAssociatedCorrEuIdsByPermitConditionId(pc.getPermitConditionId());
		permitConditionDAO().removeComplianceStatusEventList(pc.getPermitConditionId());
		permitConditionDAO().deletePermitConditionCategoriesByConditionId(pc.getPermitConditionId());
		permitConditionDAO().removePermitCondition(pc);

	}

	@Override
	public boolean createAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException {
		return permitConditionDAO().createAssociatedCorrEuIdRef(permitConditionId, corrEpaEmuId);
	}

	@Override
	public List<Integer> retrieveAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException {
		return permitConditionDAO().retrieveAssociatedCorrEuIdsByPermitConditionId(permitConditionId);
	}

	@Override
	public List<String> retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(Integer permitConditionId)
			throws DAOException {
		return permitConditionDAO().retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(permitConditionId);
	}

	@Override
	public void updateAssociatedCorrEuIds(Integer permitConditionId, List<Integer> corrEuIdsToAssociate,
			List<Integer> corrEuIdsToDisassoicate) throws DAOException {

		// Add rows for the newly associated EUs.
		for (Integer corrEpaEmuId : corrEuIdsToAssociate) {
			createAssociatedCorrEuIdRef(permitConditionId, corrEpaEmuId);
		}

		// Delete rows for the disassociated EUs.
		for (Integer corrEpaEmuId : corrEuIdsToDisassoicate) {
			deleteAssociatedCorrEuIdRef(permitConditionId, corrEpaEmuId);
		}
	}

	@Override
	public void deleteAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException {
		permitConditionDAO().deleteAssociatedCorrEuIdRef(permitConditionId, corrEpaEmuId);
	}

	@Override
	public void deleteAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException {
		permitConditionDAO().deleteAssociatedCorrEuIdsByPermitConditionId(permitConditionId);
	}

	@Override
	public boolean isDuplicatePermitConditionNumber(PermitCondition permitCondition) throws DAOException {

		try {

			return permitConditionDAO().isDuplicatePermitConditionNumber(permitCondition);
		} catch (DAOException e) {
			logger.debug("isDuplicatePermitConditionNumber Error: Permit ID: "
					+ permitCondition.getPermitId() + " Permit Condition Number: " + permitCondition.getPermitConditionNumber());

			return true;
		}
	}

	@Override
	public PermitConditionSearchLineItem[] searchPermitConditions(PermitConditionSearchCriteria searchCriteria)
			throws DAOException {
		return permitConditionDAO().searchPermitConditions(searchCriteria);
	}
	
	@Override
	public void createComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException {
		permitConditionDAO().createComplianceStatusEvent(cse);
	}

	@Override
	public void modifyComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException {
		permitConditionDAO().modifyComplianceStatusEvent(cse);
	}

	@Override
	public void removeComplianceStatusEvent(Integer permitConditionId) throws DAOException {
		permitConditionDAO().removeComplianceStatusEvent(permitConditionId);
	}

	@Override
	public ComplianceStatusEvent retrieveComplianceStatusEvent(Integer complianceStatusId) throws DAOException {
		return permitConditionDAO().retrieveComplianceStatusEvent(complianceStatusId);
	}

	@Override
	public List<ComplianceStatusEvent> retrieveComplianceStatusEventList(Integer permitConditionId)
			throws DAOException {

		List<ComplianceStatusEvent> cseList = null;
		cseList = permitConditionDAO().retrieveComplianceStatusEventList(permitConditionId);

		return cseList;
	}

	@Override
	public void removeComplianceStatusEventList(Integer permitConditionId) throws DAOException {
		permitConditionDAO().removeComplianceStatusEventList(permitConditionId);
	}
	
	@Override
	public List<PermitConditionSupersession> retrieveThisConditionSupersededByList(Integer permitConditionId) 
			throws DAOException {
		return permitConditionDAO().retrieveThisPermitConditionSupersededByList(permitConditionId);
	}

	@Override
	public List<PermitConditionSupersession> retrieveThisConditionSupersedesList(Integer permitConditionId)
			throws DAOException {
		return permitConditionDAO().retrievePermitConditionsSupersededByThis(permitConditionId);
	}

	/**
	 * @param permitConditionId
	 * @return PermitCondition matching permitConditionId
	 * @throws DAOException
	 */
	@Override
	public PermitCondition retrievePermitConditionById(Integer permitConditionId) throws DAOException {
		PermitCondition permitCondition = permitConditionDAO().retrievePermitConditionById(permitConditionId);
		if(null != permitCondition) {
			permitCondition.setAssociatedCorrEuIds(retrieveAssociatedCorrEuIdsByPermitConditionId(permitConditionId));
			permitCondition
					.setAssociatedFpEuEpaEmuIds(retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(permitConditionId));
			permitCondition.setComplianceStatusEvents(retrieveComplianceStatusEventList(permitConditionId));
			permitCondition.setPermitConditionCategoryCds(retrievePermitConditionCategoriesByConditionId(permitConditionId));
			// superseeding/supeseded info
			permitCondition.setSupersedesThis(retrieveThisConditionSupersededByList(permitConditionId));
			permitCondition.setSupersededByThis(retrieveThisConditionSupersedesList(permitConditionId));
		}
		return permitCondition;
	}

	@Override
	public void createPermitConditionSupersession(PermitConditionSupersession cs) throws DAOException {
		// create a supersession for every superseded permit condition
		for(Integer supersededPermitConditionId : cs.getSupersededPermitConditionIds()) {
			PermitConditionSupersession temp = new PermitConditionSupersession(cs);
			temp.setSupersededPermitConditionId(supersededPermitConditionId);
			permitConditionDAO().createPermitConditionSupersession(temp);
		}
	}

	@Override
	public void modifyPermitConditionSupersession(PermitConditionSupersession cs) throws DAOException {
		permitConditionDAO().modifyPermitConditionSupersession(cs);
	}	

	@Override
	public boolean createPermitConditionCategory(Integer permitConditionId, String categoryCd)
			throws DAOException {
		return permitConditionDAO().createPermitConditionCategory(permitConditionId, categoryCd);
	}
		
	@Override
	public List<String> retrievePermitConditionCategoriesByConditionId(Integer permitConditionId) throws DAOException {
		return permitConditionDAO(). retrievePermitConditionCategoriesByConditionId(permitConditionId);
	}
	
	@Override
	public void deletePermitConditionCategoriesByConditionId(Integer permitConditionId) throws DAOException {
		permitConditionDAO().deletePermitConditionCategoriesByConditionId(permitConditionId);
	}
	
	@Override
	public PermitConditionSupersession retrievePermitConditionSupersession(Integer suoersededPermitConditionSupersession, 
			Integer supersedingPermitConditionSupersession)  throws DAOException {
		
		return permitConditionDAO().retrievePermitConditionSupersession(suoersededPermitConditionSupersession, supersedingPermitConditionSupersession);
		
	}

	@Override
	public void deletePermitConditionSupersession(PermitConditionSupersession cs) throws DAOException {
		permitConditionDAO().removePermitConditionSupersession(cs);
	}

	@Override
	public Integer retrievePermitConditionsCountByPermitIds(List<Integer> permitIds) throws DAOException {
		if(permitIds!=null && !permitIds.isEmpty()) {
			return permitConditionDAO().retrievePermitConditionsCountByPermitIds(permitIds);
		} else {
			return 0;
		}
	}

	@Override
	public void deletePermitConditionSupersessionForPurge(Integer supersededPermitConditionId) throws DAOException {

		permitConditionDAO().deletePermitConditionSupersession(supersededPermitConditionId);
		
	}
}
