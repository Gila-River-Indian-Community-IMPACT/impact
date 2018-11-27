package us.oh.state.epa.stars2.database.dao.ceta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.EnforcementActionDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementActionEvent;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementNote;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EnforcementActionSQLDAO extends AbstractDAO implements EnforcementActionDAO {
	
	protected transient Logger logger = Logger.getLogger(this.getClass());
	
	public EnforcementAction createEnforcementAction(EnforcementAction enforcementAction)
			throws DAOException {
        checkNull(enforcementAction);
        enforcementAction.setEnforcementActionId(nextSequenceVal("ce_enforcement_action_id", enforcementAction.getEnforcementActionId()));
        ConnectionHandler connHandler = new ConnectionHandler(
                "EnforcementActionSQL.createEnforcementAction", false);

        int i = 1;
        connHandler.setInteger(i++, enforcementAction.getEnforcementActionId());
        connHandler.setString(i++, enforcementAction.getFacilityId());
        connHandler.setInteger(i++, enforcementAction.getCreatorId());
        connHandler.setTimestamp(i++, enforcementAction.getCreatedDate());
        //remark
        connHandler.setString(i++, enforcementAction.getOtherDescription());
        connHandler.setString(i++, enforcementAction.getPotentialViolationDescription());
        connHandler.setString(i++, enforcementAction.getEvidence());
        connHandler.setString(i++, enforcementAction.getEnvironmentalImpactDescription());
        connHandler.setString(i++, enforcementAction.getEnforcementActionType());
        connHandler.setString(i++, enforcementAction.getDocketNumber());
        connHandler.setString(i++, enforcementAction.getMemo());
        connHandler.setTimestamp(i++, enforcementAction.getPotentialViolationStartDate());
        connHandler.setTimestamp(i++, enforcementAction.getPotentialViolationEndDate());
        connHandler.setString(i++, enforcementAction.getOtherSARequirementsMet());
        connHandler.setString(i++, enforcementAction.getEvidenceAttached());
        connHandler.setString(i++, enforcementAction.getEnvironmentalImpact());        
        connHandler.setString(i++, enforcementAction.getEnforcementActionHPVCriterion());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(enforcementAction.isLegacyFlag()));
        connHandler.setString(i++, enforcementAction.getEnforcementActionFRVType());
        connHandler.setString(i++, enforcementAction.getSepFlag());
        connHandler.setDouble(i++, enforcementAction.getSepOffsetAmount());
        connHandler.setDouble(i++, enforcementAction.getPenaltyAmount());
        connHandler.setString(i++, enforcementAction.getOtherSARequirements());
        //
        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        enforcementAction.setLastModified(1);

        return enforcementAction;
	}
	
	public void deleteEnforcementAction(EnforcementAction ea)
			throws DAOException {

		checkNull(ea);

		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.deleteEnforcementAction", false);

		int i = 1;
		connHandler.setInteger(i++, ea.getEnforcementActionId());
		connHandler.setInteger(i++, ea.getLastModified());

		connHandler.remove();
		
		return;
	}
	
	public boolean modifyEnforcementAction(EnforcementAction enforcementAction)
			throws DAOException {
        checkNull(enforcementAction);
        
        ConnectionHandler connHandler = new ConnectionHandler(
                "EnforcementActionSQL.modifyEnforcementAction", false);

        int i = 1;
        connHandler.setString(i++, enforcementAction.getOtherDescription());
        connHandler.setString(i++, enforcementAction.getPotentialViolationDescription());
        connHandler.setString(i++, enforcementAction.getEvidence());
        connHandler.setString(i++, enforcementAction.getEnvironmentalImpactDescription());
        connHandler.setString(i++, enforcementAction.getEnforcementActionType());
        connHandler.setString(i++, enforcementAction.getEnforcementActionHPVCriterion());
        connHandler.setString(i++, enforcementAction.getEnforcementActionFRVType());
        connHandler.setString(i++, enforcementAction.getDocketNumber());
        connHandler.setString(i++, enforcementAction.getMemo());
        connHandler.setTimestamp(i++, enforcementAction.getCreatedDate());
        connHandler.setTimestamp(i++, enforcementAction.getPotentialViolationStartDate());
        connHandler.setTimestamp(i++, enforcementAction.getPotentialViolationEndDate());
        connHandler.setString(i++, enforcementAction.getOtherSARequirements());
        connHandler.setString(i++, enforcementAction.getOtherSARequirementsMet());
        connHandler.setString(i++, enforcementAction.getEvidenceAttached());
        connHandler.setString(i++, enforcementAction.getEnvironmentalImpact());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(enforcementAction.isLegacyFlag()));
        connHandler.setString(i++, enforcementAction.getSepFlag());
        connHandler.setDouble(i++, enforcementAction.getSepOffsetAmount());
        connHandler.setDouble(i++, enforcementAction.getPenaltyAmount());
        
        connHandler.setInteger(i++, enforcementAction.getLastModified() + 1);
        
        connHandler.setInteger(i++, enforcementAction.getEnforcementActionId());
               
        return connHandler.update();
	}
	
	public EnforcementAction retrieveEnforcementAction(Integer enforcementActionId)
			throws DAOException {
		EnforcementAction ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
            conn = getReadOnlyConnection();
            pStmt = conn
                    .prepareStatement(loadSQL("EnforcementActionSQL.retrieveEnforcementAction"));

            pStmt.setInt(1, enforcementActionId);

            ResultSet rs = pStmt.executeQuery();

            if (rs.next()) {
            	ret = new EnforcementAction();
            	ret.populate(rs);
            	ret.setReferralTypes(retrieveReferralTypeCDs(enforcementActionId));
            }
            rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		
		return ret;
	}
	
	public void createReferalType(String referralTypeCd, Integer enforcementActionId)
			throws DAOException {
		checkNull(referralTypeCd);
		checkNull(enforcementActionId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.createReferralType", false);
		int i = 1;
		connHandler.setString(i++, referralTypeCd);
		connHandler.setInteger(i++, enforcementActionId);
		connHandler.update();
	}
	
	public void deleteReferralTypeById(Integer enforcementActionId) throws DAOException {
		checkNull(enforcementActionId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.deleteReferralTypeById", false);
		
		connHandler.setInteger(1, enforcementActionId);
		
		connHandler.remove();
		
		return;
	}
	
	private List<String> retrieveReferralTypeCDs(Integer enforcementActionId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.retrieveReferralTypeCDs", true);

		connHandler.setInteger(1, enforcementActionId);
		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret;
	}
	
	public List<EnforcementAction> searchEnforcementActions(String facilityId,
			String facilityName, String doLaaCd, String countyCd,
			String addToHPVList,
			String eventCd,
			Timestamp beginActionDt, Timestamp endActionDt, Integer evaluator, 
			String enforceId, List<String> enfStateCases, String enfCaseNum, String actionTypeCd,
			String cmpId, String docketNumber)
			throws DAOException {
		List<EnforcementAction> results = new ArrayList<EnforcementAction>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		
		StringBuilder statementSQL = new StringBuilder(loadSQL("EnforcementActionSQL.searchEnforcementActions"));        
		StringBuilder whereClause = new StringBuilder("");
		String conjunction = "AND ";
		/*
		if (evaluator != null) {
			statementSQL.append("\nJOIN %Schema%ce_enforcement_evaluator_xref eex ON (a.action_id = eex.action_id)\n");
            whereClause.append(conjunction + "eex.user_id = ");
            whereClause.append(evaluator);
            conjunction = "\nAND ";
		}
		*/
		if (facilityName != null && facilityName.trim().length() > 0) {
            whereClause.append(conjunction + "LOWER(facility_nm) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(facilityName.trim().replace("*", "%")));
            whereClause.append("')");
            conjunction = "\nAND ";
        }
        
        if (facilityId != null && facilityId.trim().length() > 0) {
        	facilityId = formatFacilityId(facilityId);
            whereClause.append(conjunction + "LOWER(ff.facility_id) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(facilityId.trim().replace("*", "%")));
            whereClause.append("')");
            conjunction = "\nAND ";
        }
        
        if (countyCd != null) {
            whereClause.append(conjunction + "ca.county_cd = '");
            whereClause.append(countyCd);
            whereClause.append("'");
            conjunction = "\nAND ";
        }
        if (doLaaCd != null) {
            whereClause.append(conjunction + "ff.do_laa_cd = '");
            whereClause.append(doLaaCd);
            whereClause.append("'");
            conjunction = "\nAND ";
        }
        
        if (cmpId != null) {
            whereClause.append(conjunction + "ccm.cmp_id = '");
            whereClause.append(cmpId);
            whereClause.append("'");
            conjunction = "\nAND ";
        }
        
        if (enforceId != null && enforceId.length() != 0) {
        	enforceId = formatId("ENF", enforceId);
			whereClause.append(" AND LOWER(ea.enf_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(enforceId.replace("*", "%")));
			whereClause.append("')");
			conjunction = "\nAND ";
		}
        
        if (docketNumber != null) {
			whereClause.append(conjunction + " LOWER(ea.docket_number) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(docketNumber.replace("*", "%")));
			whereClause.append("')");
			conjunction = "\nAND ";
        }
        
        if (actionTypeCd != null) {
            whereClause.append(conjunction + "ea.enforcement_action_type = '");
            whereClause.append(actionTypeCd);
            whereClause.append("'");
            conjunction = "\nAND ";
        }
        /*
        if (addToHPVList != null) {
            whereClause.append(conjunction + "ea.add_to_hpv_list_flag = '");
            whereClause.append(addToHPVList);
            whereClause.append("'");
            conjunction = "\nAND ";
        }
        if (enfStateCases != null) {
        	whereClause.append(conjunction + "(");
        	conjunction = " ";
        	for (String enfCaseStateCd : enfStateCases){
        		whereClause.append(conjunction + " e.enf_case_state_cd = '");
        		whereClause.append(enfCaseStateCd);
        		whereClause.append("'");
        		conjunction = " OR ";
        	}
        	whereClause.append(")");
        	conjunction = "\nAND ";
        }
        */
        
        if (beginActionDt != null || endActionDt != null) {

        	whereClause.append(conjunction);
			whereClause.append("(");
			if (beginActionDt != null) {
				whereClause.append(" eae.event_dt ");
				whereClause.append(" >= ? ");
				if (endActionDt != null) {
					whereClause.append(" AND ");
				}
			}
			if (endActionDt != null) {
				whereClause.append(" eae.event_dt ");
				whereClause.append(" <= ? ");
			}
			whereClause.append(" ) ");
			conjunction = "\nAND ";
		}
        
        if (eventCd != null) {
            whereClause.append(conjunction + "eae.enforcement_action_event_cd = '");
            whereClause.append(eventCd);
            whereClause.append("'");
            conjunction = "\nAND ";
        }

        StringBuilder sortBy = new StringBuilder("\nORDER BY ea.facility_id, ea.enforcement_action_id");
        statementSQL.append(whereClause.toString() + " " + sortBy.toString());
        
        //logger.debug("statementSQL = " + statementSQL);

		try {
			Integer lastId = null;
			EnforcementAction enf = null;
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(replaceSchema(statementSQL.toString()));
            int i = 1;
            if (beginActionDt != null) {
            	pStmt.setTimestamp(i, formatBeginOfDay(beginActionDt));
                i++;
            }
            if (endActionDt != null) {
            	pStmt.setTimestamp(i, formatEndOfDay(endActionDt));
                i++;
            }
            ResultSet rs = pStmt.executeQuery();
            
			while (rs.next()) {
				Integer enforcementId = AbstractDAO.getInteger(rs, "enforcement_action_id");
				if (enforcementId != null && !enforcementId.equals(lastId)) {
	            	enf = new EnforcementAction();
	            	enf.populate(rs);
	            	lastId = enforcementId;
	            	results.add(enf);
				}
            }
            rs.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return results;
	}
	
	public EnforcementAttachment createEnforcementAttachment(EnforcementAttachment ea) throws DAOException {
	      checkNull(ea);
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "EnforcementActionSQL.createEnforcementAttachment", false);

	        int i = 1;
	        connHandler.setInteger(i++, ea.getDocumentID());
	        connHandler.setInteger(i++, ea.getEnforcementId());
	        connHandler.setString(i++, ea.getDocTypeCd());
	        connHandler.update();
      
	        ea.setLastModified(1);

	        return ea;
	
	}
	
	public List<EnforcementAttachment> retrieveEnforcementAttachments(
			int enforcementId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EnforcementActionSQL.retrieveEnforcementAttachments", true);
        connHandler.setInteger(1, enforcementId);
        List<EnforcementAttachment> rtn = connHandler.retrieveArray(EnforcementAttachment.class);
        //for (EnforcementAttachment a : rtn) {
        	//Integer acpId = EnforcementAttachmentTypeDef.isACPType(a.getAttachmentType()) ? a.getDocumentID() : 0;
        	//a.setTradeSecretDocId(acpId);
        //}
        return rtn;
	}
	
	public final boolean modifyEnforcementAttachment(EnforcementAttachment doc)
			throws DAOException {
		checkNull(doc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.modifyEnforcementAttachment", false);

		int i = 1;
		connHandler.setString(i++, doc.getDocTypeCd());
		connHandler.setInteger(i++, doc.getRefLastModified() + 1);
		connHandler.setInteger(i++, doc.getDocumentID());
		connHandler.setInteger(i++, doc.getEnforcementId());
		connHandler.setInteger(i++, doc.getRefLastModified());
		return connHandler.update();
	}
	
	public void deleteEnforcementAttachment(EnforcementAttachment att)
			throws DAOException {
        checkNull(att);
        checkNull(att.getEnforcementId());
        checkNull(att.getDocumentID());
        ConnectionHandler connHandler = new ConnectionHandler(
                "EnforcementActionSQL.deleteEnforcementAttachment", false);
        connHandler.setInteger(1, att.getEnforcementId());
        connHandler.setInteger(2, att.getDocumentID());
        connHandler.remove();

	}
	
	public List<EnforcementNote> retrieveEnforcementNotes(int enforcementId)
			throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EnforcementActionSQL.retrieveEnforcementNotes", true);
        connHandler.setInteger(1, enforcementId);
        return connHandler.retrieveArray(EnforcementNote.class);
	}
	
	public void addEnforcementNote(int enforcementId, int noteId)
			throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "EnforcementActionSQL.addEnforcementNote", false);
        connHandler.setInteger(1, enforcementId);
        connHandler.setInteger(2, noteId);
        connHandler.update();
	}
	
	public void deleteEnforcementNotes(int enforcementId) throws DAOException {
		checkNull(enforcementId);
	    ConnectionHandler connHandler = new ConnectionHandler(
                "EnforcementActionSQL.deleteEnforcementNotes", false);
        connHandler.setInteger(1, enforcementId);
        connHandler.remove();

	}
	
	
	public final List<EnforcementActionEvent> retrieveEnforcementActionEventList(
			int enforcementActionId) throws DAOException {

		checkNull(enforcementActionId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.retrieveEnforcementActionEventList", true);

		connHandler.setInteger(1, enforcementActionId);

		ArrayList<EnforcementActionEvent> ret = new ArrayList<EnforcementActionEvent>();
		ArrayList<EnforcementActionEvent> base = connHandler
				.retrieveArray(EnforcementActionEvent.class);
		for (BaseDBObject bd : base) {
			ret.add((EnforcementActionEvent) bd);
		}

		return ret;
	}

	public final void removeEnforcementActionEventList(int enforcementActionId)
			throws DAOException {

		checkNull(enforcementActionId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.removeEnforcementActionEventList", false);

		connHandler.setInteger(1, enforcementActionId);
		connHandler.remove();
	}

	public final EnforcementActionEvent createEnforcementActionEvent(
			EnforcementActionEvent eae) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.createEnforcementActionEvent", false);

		checkNull(eae);
		int i = 1;
		
		int id = nextSequenceVal("S_Enforcement_Action_Event_Id");
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, eae.getEnforcementActionId());
		connHandler.setString(i++, eae.getEventCd());
		connHandler.setTimestamp(i++, eae.getEventDate());
		connHandler.setInteger(i++, eae.getAddedBy());

		connHandler.update();

		eae.setEventId(id);
		eae.setLastModified(1);

		return eae;
	}

	public final void modifyEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.modifyEnforcementActionEvent", false);

		checkNull(eae);
		int i = 1;
		connHandler.setInteger(i++, eae.getEventId());
		connHandler.setInteger(i++, eae.getEnforcementActionId());
		connHandler.setString(i++, eae.getEventCd());
		connHandler.setTimestamp(i++, eae.getEventDate());
		connHandler.setInteger(i++, eae.getAddedBy());

		connHandler.setInteger(i++, eae.getLastModified() + 1);

		connHandler.setInteger(i++, eae.getEventId());
		connHandler.setInteger(i++, eae.getEnforcementActionId());
		connHandler.setInteger(i++, eae.getLastModified());

		connHandler.update();
	}

	public final void removeEnforcementActionEvent(EnforcementActionEvent eae)
			throws DAOException {

		checkNull(eae);

		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.removeEnforcementActionEvent", false);

		int i = 1;
		connHandler.setInteger(i++, eae.getEventId());
		connHandler.setInteger(i++, eae.getEnforcementActionId());
		connHandler.setInteger(i++, eae.getLastModified());

		connHandler.remove();
	}
	
	public final EnforcementAction[] retrieveEnforcementActionByFacilityId(String facilityId)
		throws DAOException {
		checkNull(facilityId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"EnforcementActionSQL.retrieveEnforcementActionByFacilityId", true);

		connHandler.setString(1, facilityId);

		List<EnforcementAction> ret = connHandler.retrieveArray(EnforcementAction.class);
		
		return ret.toArray(new EnforcementAction[0]);
	}
	
}
