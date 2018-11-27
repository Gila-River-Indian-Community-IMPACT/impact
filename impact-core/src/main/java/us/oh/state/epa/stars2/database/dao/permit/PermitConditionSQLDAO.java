package us.oh.state.epa.stars2.database.dao.permit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.app.facility.PermitConditionSearchCriteria;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.PermitConditionDAO;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSupersession;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;

@Repository
public class PermitConditionSQLDAO extends AbstractDAO implements PermitConditionDAO {
	
	public final Logger logger = Logger.getLogger((PermitConditionSQLDAO.class)) ;

	@Override
	public List<PermitCondition> retrievePermitConditionList(int permitId) throws DAOException {

		checkNull(permitId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrievePermitConditionList", true);

		connHandler.setInteger(1, permitId);

		ArrayList<PermitCondition> permitConditionList = connHandler.retrieveArray(PermitCondition.class);
		if (null != permitConditionList && !permitConditionList.isEmpty()) {
			for (PermitCondition aPermitCondition : permitConditionList) {
				Integer permitConditionId = aPermitCondition.getPermitConditionId();
				aPermitCondition
						.setAssociatedCorrEuIds(retrieveAssociatedCorrEuIdsByPermitConditionId(permitConditionId));
				aPermitCondition.setAssociatedFpEuEpaEmuIds(
						retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(permitConditionId));
				aPermitCondition.setComplianceStatusEvents(retrieveComplianceStatusEventList(permitConditionId));
				aPermitCondition.setSupersededByThis(retrievePermitConditionsSupersededByThis(permitConditionId));
				aPermitCondition.setSupersedesThis(retrieveThisPermitConditionSupersededByList(permitConditionId));
				aPermitCondition.setPermitConditionCategoryCds(
						retrievePermitConditionCategoriesByConditionId(permitConditionId));
			}
		}

		return permitConditionList;
	}

	@Override
	public void removePermitConditionList(int permitId) throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removePermitConditionList", false);

		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}

	@Override
	public PermitCondition createPermitCondition(PermitCondition pc) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.createPermitCondition", false);

		checkNull(pc);
		int i = 1;
		int id = nextSequenceVal("S_Permit_PC_Id");
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, pc.getPermitId());
		connHandler.setString(i++, pc.getPermitConditionNumber());
		connHandler.setString(i++, pc.getPermitConditionStatusCd());
		connHandler.setInteger(i++, pc.getLastUpdatedById());
		connHandler.setTimestamp(i++, pc.getLastUpdatedDate());
		connHandler.setString(i++, pc.getFacilityWide());
		connHandler.setString(i++, pc.getConditionTextHTML());
		connHandler.setString(i++, pc.getConditionTextPlain());

		connHandler.update();

		pc.setPermitConditionId(id);
		pc.setLastModified(1);

		return pc;
	}

	@Override
	public void modifyPermitCondition(PermitCondition pc) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.modifyPermitCondition", false);

		checkNull(pc);
		int i = 1;

		connHandler.setString(i++, pc.getPermitConditionNumber());
		connHandler.setString(i++, pc.getPermitConditionStatusCd());
		connHandler.setInteger(i++, pc.getLastUpdatedById());
		connHandler.setTimestamp(i++, pc.getLastUpdatedDate());
		connHandler.setString(i++, pc.getFacilityWide());
		connHandler.setString(i++, pc.getConditionTextHTML());
		connHandler.setString(i++, pc.getConditionTextPlain());

		connHandler.setInteger(i++, pc.getLastModified() + 1);

		connHandler.setInteger(i++, pc.getPermitConditionId());
		connHandler.setInteger(i++, pc.getPermitId());
		connHandler.setInteger(i++, pc.getLastModified());

		connHandler.update();
	}

	@Override
	public void removePermitCondition(PermitCondition pc) throws DAOException {

		checkNull(pc);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removePermitCondition", false);

		int i = 1;
		connHandler.setInteger(i++, pc.getPermitConditionId());
		connHandler.setInteger(i++, pc.getPermitId());
		connHandler.setInteger(i++, pc.getLastModified());

		connHandler.remove();
	}

	@Override
	public boolean createAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException {

		checkNull(permitConditionId);
		checkNull(corrEpaEmuId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.createAssociatedCorrEuIdRef", true);

		int i = 1;
		connHandler.setInteger(i++, permitConditionId);
		connHandler.setInteger(i++, corrEpaEmuId);

		return connHandler.update();
	}

	@Override
	public List<Integer> retrieveAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException {

		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveAssociatedCorrEuIdsByPermitConditionId", true);

		connHandler.setInteger(1, permitConditionId);

		return connHandler.retrieveJavaObjectArray(Integer.class);
	}

	@Override
	public List<String> retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(Integer permitConditionId)
			throws DAOException {

		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId", true);

		connHandler.setInteger(1, permitConditionId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public void deleteAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException {

		checkNull(permitConditionId);
		checkNull(corrEpaEmuId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.deleteAssociatedCorrEuIdRef", true);

		int i = 1;
		connHandler.setInteger(i++, permitConditionId);
		connHandler.setInteger(i++, corrEpaEmuId);

		connHandler.remove();
	}

	@Override
	public void deleteAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException {

		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.deleteAssociatedCorrEuIdsByPermitConditionId",
				true);

		connHandler.setInteger(1, permitConditionId);

		connHandler.remove();
	}

	@Override
	public void markInactiveNSRPermitsToExpired() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.markInactiveNSRPermitsToExpired", true);

		connHandler.update();
	}

	@Override
	public boolean isDuplicatePermitConditionNumber(PermitCondition permitCondition) throws DAOException {

		if (Utility.isNullOrEmpty(permitCondition.getPermitConditionNumber())) {
			return true;
		}

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.isDuplicatePermitConditionNumber", false);
		connHandler.setString(1, permitCondition.getPermitConditionNumber().trim());
		connHandler.setInteger(2, permitCondition.getPermitId());
		connHandler.setInteger(3, permitCondition.getPermitConditionId());
		connHandler.setInteger(4, permitCondition.getPermitConditionId());
		Integer count = (Integer) connHandler.retrieveJavaObject(Integer.class);

		return count > 0;
	}

	@Override
	public PermitConditionSearchLineItem[] searchPermitConditions(PermitConditionSearchCriteria searchCriteria)
			throws DAOException {
		checkNull(searchCriteria);

		setDefaultSearchLimit(-1);
		
		StringBuffer sql = new StringBuffer("SELECT * FROM ( "); // outer select 

		String statementSQL = loadSQL("PermitSQL.searchPermitConditions");

		StringBuffer whereClause = new StringBuffer("");
		
		if (!Utility.isNullOrEmpty(searchCriteria.getFacilityId())) {
			whereClause.append(" AND LOWER(pt.FACILITY_ID) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchCriteria.getFacilityId().trim().replace("*", "%")));
			whereClause.append("')");
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getPermitNumber())) {
			whereClause.append(" AND LOWER(pt.PERMIT_NBR) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchCriteria.getPermitNumber().trim().replace("*", "%")));
			whereClause.append("')");
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getLegacyPermitNumber())) {
			whereClause.append(" AND LOWER(pt.LEGACY_PERMIT_NBR) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchCriteria.getLegacyPermitNumber().trim().replace("*", "%")));
			whereClause.append("')");
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getPermitTypeCd())) {
			whereClause.append(" AND LOWER(pt.PERMIT_TYPE_CD) = ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchCriteria.getPermitTypeCd().trim()));
			whereClause.append("')");
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getPermitLevelStatusCd())) {
			whereClause.append(" AND LOWER(pt.PERMIT_LEVEL_STATUS_CD) = ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchCriteria.getPermitLevelStatusCd().trim()));
			whereClause.append("')");
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getPermitConditionNumber())) {
			whereClause.append(" AND LOWER(pc.PERMIT_CONDITION_NUMBER) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchCriteria.getPermitConditionNumber().trim().replace("*", "%")));
			whereClause.append("')");
		}
		
		if (!Utility.isNullOrEmpty(searchCriteria.getPermitConditionStatusCd())) {
			whereClause.append(" AND LOWER(pc.CONDITION_STATUS_CD) = ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchCriteria.getPermitConditionStatusCd().trim()));
			whereClause.append("')");
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getPermitConditionCategoryCd())) {
			whereClause.append(" AND LOWER(pccx.CATEGORY_CODE) = ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(searchCriteria.getPermitConditionCategoryCd().trim()));
			whereClause.append("')");
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getConditionTextPlain())) {
			whereClause.append(" AND CONTAINS([CONDITION_TEXT_PLAIN], '");
			whereClause.append(SQLizeString(searchCriteria.getConditionTextPlain().trim().replace(" ", "%")));
			whereClause.append("')");
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getEuId())) {
			StringBuffer euWhereClause = new StringBuffer("");
			euWhereClause.append(" WHERE LOWER(feu.EPA_EMU_ID) LIKE LOWER('");
			euWhereClause.append(SQLizeString(searchCriteria.getEuId().trim().replace("*", "%")));
			euWhereClause.append("')");

			String where = loadSQL("PermitSQL.searchPermitConditionsByEuId").replace("%whereClause%",
					euWhereClause.toString());

			whereClause.append(" AND ");
			whereClause.append(where);
		}

		if (!Utility.isNullOrEmpty(searchCriteria.getDateBy())) {
			Timestamp beginDate = null == searchCriteria.getBeginDt() ? null
					: new Timestamp(searchCriteria.getBeginDt().getTime());
			Timestamp endDate = null == searchCriteria.getEndDt() ? null
					: new Timestamp(searchCriteria.getEndDt().getTime());

			if (null != beginDate) {
				whereClause.append(" AND ");
				whereClause.append(searchCriteria.getDateBy());
				whereClause.append(" >= ?");
			}

			if (endDate != null) {
				whereClause.append(" AND ");
				whereClause.append(searchCriteria.getDateBy());
				whereClause.append(" <= ? ");
			}
		}

		if (searchCriteria.isShowOnlyIssuedFinal()) {
			whereClause.append(" AND LOWER(pt.PERMIT_GLOBAL_STATUS_CD) = LOWER('");
			whereClause.append(PermitGlobalStatusDef.ISSUED_FINAL);
			whereClause.append("')");
		}

		statementSQL += whereClause.toString();
		
		sql.append(statementSQL.toString());
		sql.append(") res WHERE res.RN = 1 ");
		
		String sortBy = " ORDER BY res.PERMIT_CONDITION_ID desc";
		sql.append(sortBy);
		
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(sql.toString());

		// substitute begin and end dates
		int i = 1;
		if (null != searchCriteria.getBeginDt()) {
			connHandler.setTimestamp(i++, searchCriteria.getBeginDt());
		}

		if (null != searchCriteria.getEndDt()) {
			connHandler.setTimestamp(i++, searchCriteria.getEndDt());
		}

		List<PermitConditionSearchLineItem> searchResults = connHandler
				.retrieveArray(PermitConditionSearchLineItem.class, defaultSearchLimit);

		// update the resultset with associated eus
		for (PermitConditionSearchLineItem aResult : searchResults) {
			aResult.setAssociatedFpEuEpaEmuIds(
					retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(aResult.getPermitConditionId()));
			aResult.setPermitConditionCategoryCds(
					retrievePermitConditionCategoriesByConditionId(aResult.getPermitConditionId()));
			aResult.setSupersededByThis(retrievePermitConditionsSupersededByThis(aResult.getPermitConditionId()));
			aResult.setComplianceStatusEventList(retrieveComplianceStatusEventList(aResult.getPermitConditionId()));
		}

		return searchResults.toArray(new PermitConditionSearchLineItem[0]);
	}

	// Compliance status methods

	@Override
	public final ComplianceStatusEvent createComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.createComplianceStatusEvent", false);

		checkNull(cse);
		int i = 1;

		int id = nextSequenceVal("S_Permit_CSE_Id");
		connHandler.setInteger(i++, id);

		connHandler.setInteger(i++, cse.getPermitConditionId());
		connHandler.setTimestamp(i++, cse.getEventDate());
		connHandler.setString(i++, cse.getEventTypeCd());
		connHandler.setString(i++, cse.getStatus());
		connHandler.setInteger(i++, cse.getInspectionReference());
		connHandler.setInteger(i++, cse.getStackTestReference());
		connHandler.setInteger(i++, cse.getComplianceReportReference());
		connHandler.setInteger(i++, cse.getLastUpdatedById());
		connHandler.setTimestamp(i++, cse.getLastUpdatedDate());

		connHandler.update();

		cse.setComplianceStatusId(id);
		cse.setLastModified(1);

		return cse;
	}

	@Override
	public final void modifyComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.modifyComplianceStatusEvent", false);

		checkNull(cse);
		int i = 1;

		connHandler.setTimestamp(i++, cse.getEventDate());
		connHandler.setString(i++, cse.getEventTypeCd());
		connHandler.setString(i++, cse.getStatus());
		connHandler.setInteger(i++, cse.getInspectionReference());
		connHandler.setInteger(i++, cse.getStackTestReference());
		connHandler.setInteger(i++, cse.getComplianceReportReference());
		connHandler.setInteger(i++, cse.getLastUpdatedById());
		connHandler.setTimestamp(i++, cse.getLastUpdatedDate());
		connHandler.setInteger(i++, cse.getLastModified() + 1);

		connHandler.setInteger(i++, cse.getComplianceStatusId());
		connHandler.setInteger(i++, cse.getLastModified());

		connHandler.update();
	}

	@Override
	public final void removeComplianceStatusEvent(Integer complianceStatusId) throws DAOException {

		checkNull(complianceStatusId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removeComplianceStatusEvent", false);
		connHandler.setInteger(1, complianceStatusId);

		connHandler.remove();
	}

	@Override
	public final ComplianceStatusEvent retrieveComplianceStatusEvent(Integer complianceStatusId) throws DAOException {

		checkNull(complianceStatusId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrieveComplianceStatusEvent", false);
		connHandler.setInteger(1, complianceStatusId);

		return (ComplianceStatusEvent) connHandler.retrieve(ComplianceStatusEvent.class);
	}

	@Override
	public final List<ComplianceStatusEvent> retrieveComplianceStatusEventList(Integer permitConditionId) throws DAOException {

		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrieveComplianceStatusEventList", true);
		connHandler.setInteger(1, permitConditionId);

		return connHandler.retrieveArray(ComplianceStatusEvent.class);
	}

	@Override
	public final void removeComplianceStatusEventList(Integer permitConditionId) throws DAOException {

		checkNull(permitConditionId);
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removeComplianceStatusEventList", false);

		connHandler.setInteger(1, permitConditionId);
		connHandler.remove();
	}

	@Override
	public List<PermitConditionSupersession> retrievePermitConditionsSupersededByThis(Integer permitConditionId)
			throws DAOException {

		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrievePermitConditionsSupersededByThis", true);
		connHandler.setInteger(1, permitConditionId);

		return connHandler.retrieveArray(PermitConditionSupersession.class);

	}

	@Override
	public List<PermitConditionSupersession> retrieveThisPermitConditionSupersededByList(Integer permitConditionId)
			throws DAOException {

		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrieveThisPermitConditionSupersededByList", true);
		connHandler.setInteger(1, permitConditionId);

		return connHandler.retrieveArray(PermitConditionSupersession.class);

	}

	@Override
	public void createPermitConditionSupersession(PermitConditionSupersession pcs) throws DAOException {

		checkNull(pcs);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.createPermitConditionSupersession", false);
		int i = 1;

		connHandler.setInteger(i++, pcs.getSupersededPermitConditionId());
		connHandler.setInteger(i++, pcs.getSupersedingPermitConditionId());
		connHandler.setString(i++, pcs.getSupersedingOption());
		connHandler.setString(i++, pcs.getComments());
		pcs.setLastModified(1);

		connHandler.update();

	}

	/**
	 * @param pcs
	 * @throws DAOException
	 */
	@Override
	public void modifyPermitConditionSupersession(PermitConditionSupersession pcs) throws DAOException {

		checkNull(pcs);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.modifyPermitConditionSupersession", false);
		int i = 1;

		connHandler.setString(i++, pcs.getSupersedingOption());
		connHandler.setString(i++, pcs.getComments());
		connHandler.setInteger(i++, pcs.getLastModified() + 1);
		connHandler.setInteger(i++, pcs.getSupersededPermitConditionId());
		connHandler.setInteger(i++, pcs.getSupersedingPermitConditionId());
		connHandler.setInteger(i++, pcs.getLastModified());

		connHandler.update();

	}

	/**
	 * @param pc
	 * @throws DAOException
	 */
	@Override
	public void removePermitConditionSupersession(PermitConditionSupersession pcs) throws DAOException {

		checkNull(pcs);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removePermitConditionSupersession", false);
		int i = 1;

		connHandler.setInteger(i++, pcs.getSupersededPermitConditionId());
		connHandler.setInteger(i++, pcs.getSupersedingPermitConditionId());

		connHandler.remove();

	}

	/**
	 * @param permitConditionId
	 * @return PermitCondition represented by permitConditionId
	 * @throws DAOException
	 */
	@Override
	public PermitCondition retrievePermitConditionById(Integer permitConditionId) throws DAOException {
		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrievePermitConditionById", true);
		connHandler.setInteger(1, permitConditionId);

		return (PermitCondition) connHandler.retrieve(PermitCondition.class);
	}

	// Permit Condition Category methods
	@Override
	public boolean createPermitConditionCategory(Integer permitConditionId, String categoryCd)
			throws DAOException {
		checkNull(permitConditionId);
		checkNull(categoryCd);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.createPermitConditionCategory", false);
		int i = 1;
		connHandler.setInteger(i++, permitConditionId);
		connHandler.setString(i++, categoryCd);

		return connHandler.update();
	}

	@Override
	public List<String> retrievePermitConditionCategoriesByConditionId(Integer permitConditionId) throws DAOException {
		checkNull(permitConditionId);
		
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrievePermitConditionCategoriesByConditionId", false);
		connHandler.setInteger(1, permitConditionId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}
	
	@Override
	public void deletePermitConditionCategoriesByConditionId(Integer permitConditionId) throws DAOException {
		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.deletePermitConditionCategoriesByConditionId", false);
		connHandler.setString(1, permitConditionId);
		
		connHandler.remove();

	}

	@Override
	public PermitConditionSupersession retrievePermitConditionSupersession(
			Integer suoersededPermitConditionId, Integer supersedingPermitConditionId)
			throws DAOException {

		checkNull(suoersededPermitConditionId);
		checkNull(supersedingPermitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrievePermitConditionSupersession", false);
		int i = 1;

		connHandler.setInteger(i++, suoersededPermitConditionId);
		connHandler.setInteger(i++, supersedingPermitConditionId);

		return (PermitConditionSupersession) connHandler.retrieve(PermitConditionSupersession.class);
	}
	
	@Override
	public final List<ComplianceStatusEvent> retrieveComplianceStatusEventListByReferencedInspection(Integer inspectionId) throws DAOException {

		checkNull(inspectionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrieveComplianceStatusEventListByReferencedInspection", true);
		connHandler.setInteger(1, inspectionId);

		return connHandler.retrieveArray(ComplianceStatusEvent.class);
	}
	
	@Override
	public final List<ComplianceStatusEvent> retrieveComplianceStatusEventListByReferencedComplianceReport(Integer complianceReportId) throws DAOException {
		
		checkNull(complianceReportId);
		
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrieveComplianceStatusEventListByReferencedComplianceReport", true);
		connHandler.setInteger(1, complianceReportId);
		
		return connHandler.retrieveArray(ComplianceStatusEvent.class);
	}
	
	@Override
	public final List<ComplianceStatusEvent> retrieveComplianceStatusEventListByReferencedStackTest(Integer stackTestId) throws DAOException {
		
		checkNull(stackTestId);
		
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrieveComplianceStatusEventListByReferencedStackTest", true);
		connHandler.setInteger(1, stackTestId);
		
		return connHandler.retrieveArray(ComplianceStatusEvent.class);
	}
	
	@Override
	public final Integer retrieveComplianceStatusEventsCount(Integer permitConditionId) throws DAOException {

		checkNull(permitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrieveComplianceStatusEventsCount", true);
		connHandler.setInteger(1, permitConditionId);

		return (Integer) connHandler.retrieveJavaObject(Integer.class);
	}

	@Override
	public Integer retrievePermitConditionsCountByPermitIds(List<Integer> permitIds) throws DAOException {
		checkNull(permitIds);
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL;
		StringBuffer whereClause = new StringBuffer();

		statementSQL = new StringBuffer(loadSQL("PermitSQL.retrievePermitConditionsCountByPermitIds"));

		whereClause.append(" AND (");
		for (int i = 0; i < permitIds.size(); i++) {
			Integer permitId = permitIds.get(i);
			whereClause.append("PERMIT_ID=" + permitId);
			if (i + 1 == permitIds.size()) {
				whereClause.append(") ");
			} else {
				whereClause.append(" OR ");
			}
		}

		statementSQL.append(whereClause.toString());
		connHandler.setSQLStringRaw(statementSQL.toString());

		return (Integer) connHandler.retrieveJavaObject(Integer.class);
	}
	
	
		
	
	/**
	 * @param pc
	 * @throws DAOException
	 */
	@Override
	public void deletePermitConditionSupersession(Integer supersededPermitConditionId) throws DAOException {

		checkNull(supersededPermitConditionId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.deletePermitConditionSupersession", false);
		int i = 1;

		connHandler.setInteger(i++, supersededPermitConditionId);

		connHandler.remove();

	}

	
	
}
