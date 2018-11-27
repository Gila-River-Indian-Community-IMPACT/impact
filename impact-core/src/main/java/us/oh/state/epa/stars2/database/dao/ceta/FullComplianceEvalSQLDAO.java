package us.oh.state.epa.stars2.database.dao.ceta;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.stereotype.Repository;


import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.FullComplianceEvalDAO;
import us.oh.state.epa.stars2.database.dao.PermitConditionDAO;
import us.oh.state.epa.stars2.database.dao.AbstractDAO.ConnectionHandler;
import us.oh.state.epa.stars2.database.dbObjects.ceta.AirProgramCompliance;
import us.oh.state.epa.stars2.database.dbObjects.ceta.AmbientConditions;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceApplicationSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceContinuousMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceEmissionsInventoryLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FcePermitCondition;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceScheduleRow;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceStackTestSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.InspectionNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SearchDateRange;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisitNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SvAttachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.def.AirProgramDef;
import us.oh.state.epa.stars2.def.ComplianceReportSearchDateByDef;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SiteVisitTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class FullComplianceEvalSQLDAO extends AbstractDAO implements FullComplianceEvalDAO {
	
	private Logger logger = Logger.getLogger(FullComplianceEvalSQLDAO.class);

	public static String visitsLockedMsg = "Existing Site Visits locked for date(s): ";

	@Resource
	private PermitConditionDAO readOnlyPermitConditionDAO;
	
    public FullComplianceEval createFce(FullComplianceEval newFce)
    		throws DAOException {
    	
        checkNull(newFce);
        FullComplianceEval ret = newFce;
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.createFce", false);
        int id;
        if (newFce.getId() == null) {
            id = nextSequenceVal("S_Fce_Id");
        } else {
            id = newFce.getId();
        }
        
        int i = 1;
        connHandler.setInteger(i++, id);
        connHandler.setInteger(i++, ret.getAssignedStaff());
        connHandler.setInteger(i++, ret.getEvaluator());
        connHandler.setInteger(i++, ret.getFpId());
        connHandler.setTimestamp(i++, ret.getScheduledTimestamp());
        connHandler.setTimestamp(i++, ret.getDateEvaluated());
        connHandler.setTimestamp(i++, ret.getDateReported());
        connHandler.setTimestamp(i++, ret.getEvalAfsDate());
        connHandler.setString(i++, ret.getEvalAfsId());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(ret.isUsEpaCommitted()));
        connHandler.setTimestamp(i++, ret.getSchedAfsDate());
        connHandler.setString(i++, ret.getSchedAfsId());
        connHandler.setString(i++, ret.getMemo());
        connHandler.setInteger(i++, ret.getFacilityHistId());
                connHandler.setInteger(i++, ret.getCreatedById());
        connHandler.setTimestamp(i++, ret.getCreatedDt());
        connHandler.setTimestamp(i++, ret.getDateSentToEPA());
        connHandler.setString(i++, ret.getInspectionType());
        connHandler.setString(i++,  AbstractDAO.translateBooleanToIndicator(ret.isLegacyInspection()));
        connHandler.setString(i++, ret.getFacilityStaffPresent());
        connHandler.setString(i++, ret.getInspectionReportStateCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(ret.isPrivatePropertyAccess()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(ret.isAnnouncedInspection()));
        connHandler.setString(i++,  AbstractDAO.translateBooleanToIndicator(ret.isPre10Legacy()));
        connHandler.update();

//      If we get here the INSERT must have succeeded, so set the important
//      data and return the object.
        ret.setId(id);
        ret.setLastModified(1);
        return ret;
    }

    public FullComplianceEval createFceGetFpId(String facilityId, FullComplianceEval newFce)
    		throws DAOException {
    	
        checkNull(newFce);
        FullComplianceEval ret = newFce;
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.createFceGetFpId", false);
        int id;
        if (newFce.getId() == null) {
            id = nextSequenceVal("S_Fce_Id");
            newFce.setId(id);
        } else {
            id = newFce.getId();
        }
        
        int i = 1;
        connHandler.setInteger(i++, id);
        connHandler.setInteger(i++, ret.getAssignedStaff());
        connHandler.setInteger(i++, ret.getEvaluator());
        connHandler.setString(i++, facilityId);  // used to get fpId
        connHandler.setTimestamp(i++, ret.getScheduledTimestamp());
        connHandler.setTimestamp(i++, ret.getDateEvaluated());
        connHandler.setTimestamp(i++, ret.getEvalAfsDate());
        connHandler.setString(i++, ret.getEvalAfsId());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(ret.isUsEpaCommitted()));
        connHandler.setTimestamp(i++, ret.getSchedAfsDate());
        connHandler.setString(i++, ret.getSchedAfsId());
        connHandler.setString(i++, ret.getMemo());
        connHandler.setInteger(i++, ret.getFacilityHistId());
        connHandler.setInteger(i++, ret.getCreatedById());
        connHandler.setTimestamp(i++, ret.getCreatedDt());
        connHandler.setTimestamp(i++, ret.getDateSentToEPA());
        connHandler.setString(i++, ret.getInspectionType());
        connHandler.setString(i++,  AbstractDAO.translateBooleanToIndicator(ret.isLegacyInspection()));
        connHandler.setString(i++,  AbstractDAO.translateBooleanToIndicator(ret.isPre10Legacy()));
        connHandler.update();
        
        return newFce;
    }
    
	public void modifyFce(FceScheduleRow fceR) throws DAOException {

		checkNull(fceR);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.modifyFceR", false);

		int i = 1;
		connHandler.setInteger(i++, fceR.getAssignedStaff());
		connHandler.setInteger(i++, fceR.getOldEvaluator());
		connHandler.setTimestamp(i++, fceR.getNextScheduled());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(fceR.isScheduledUsEpaCommitted()));
		connHandler.setInteger(i++, fceR.getScheduledLastModified() + 1);
		connHandler.setInteger(i++, fceR.getScheduledFceId());
		connHandler.setInteger(i++, fceR.getScheduledLastModified());

		boolean success = connHandler.update();
		if (!success) {
			throw new DAOException("Update of Inspection " + fceR.getScheduledFceId() + " failed");
		}
	}

    public void modifyFce(FullComplianceEval fce)
    throws DAOException {
        checkNull(fce);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.modifyFce", false);

        int i = 1;
        connHandler.setInteger(i++, fce.getAssignedStaff());
        connHandler.setInteger(i++, fce.getEvaluator());
        connHandler.setInteger(i++, fce.getFpId());
        connHandler.setTimestamp(i++, fce.getScheduledTimestamp());
        connHandler.setTimestamp(i++, fce.getDateEvaluated());
        connHandler.setTimestamp(i++, fce.getDateReported());
        connHandler.setTimestamp(i++, fce.getEvalAfsDate());
        connHandler.setString(i++, fce.getEvalAfsId());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(fce.isUsEpaCommitted()));
        connHandler.setTimestamp(i++, fce.getSchedAfsDate());
        connHandler.setString(i++, fce.getSchedAfsId());
        connHandler.setString(i++, fce.getMemo());
        connHandler.setInteger(i++, fce.getFacilityHistId());
        connHandler.setTimestamp(i++, fce.getDateSentToEPA());
        connHandler.setString(i++, fce.getInspectionType());
        connHandler.setString(i++,  AbstractDAO.translateBooleanToIndicator(fce.isLegacyInspection()));
        connHandler.setString(i++, fce.getFacilityStaffPresent());
        connHandler.setString(i++, fce.getInspectionReportStateCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(fce.isPrivatePropertyAccess()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(fce.isAnnouncedInspection()));
        connHandler.setString(i++, fce.getComplianceStatusCd());
        
        connHandler.setInteger(i++, fce.getLastModified() + 1);
        connHandler.setInteger(i++, fce.getId());
        connHandler.setInteger(i++, fce.getLastModified());
        
        boolean success = connHandler.update();
        if(!success) {
            throw new DAOException("Update of Inspection " + fce.getId() + " failed");
        }
        fce.setLastModified(fce.getLastModified() + 1);
    }

    public void removeFce(Integer fceId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.removeFce", false);

        connHandler.setInteger(1, fceId);
        connHandler.remove();
    }
    
    public void removeFceNotes(Integer fceId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.removeFceNotes", false);

        connHandler.setInteger(1, fceId);
        connHandler.remove();
    }

    public FullComplianceEval retrieveFce(Integer fceId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveByFceId", true);
        connHandler.setInteger(1, fceId);
        FullComplianceEval fce = (FullComplianceEval) connHandler.retrieve(FullComplianceEval.class);
        return fce;
    }
    
    public FullComplianceEval retrieveFce(String inspId)
    	    throws DAOException {
    	        ConnectionHandler connHandler = new ConnectionHandler(
    	                "CetaSQL.retrieveByInspId", true);
    	        connHandler.setString(1, inspId);
    	        FullComplianceEval fce = (FullComplianceEval) connHandler.retrieve(FullComplianceEval.class);
    	        return fce;
    	    }

    public FullComplianceEval[] retrieveFceBySearch(String inspId, String facilityId,
            String facilityName, String countyCd, String doLaaCd, String permitClassCd, String facilityTypeCd,
            Timestamp beginSched, Timestamp endSched,
            Timestamp beginDate, Timestamp endDate, Integer assignedStaff, 
            Integer evaluator, String usEpaCommitted, List<String> inspectionReportStateCds, String portable, String cmpId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(true);
        StringBuffer statementSQL;
        StringBuffer whereClause = new StringBuffer();
        statementSQL = new StringBuffer(loadSQL("CetaSQL.retrieveFceBySearch"));

        if (facilityId != null && facilityId.trim().length() > 0) {
        	facilityId = formatFacilityId(facilityId);
            whereClause.append(" AND LOWER(ff2.facility_id) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(facilityId.replace("*", "%")));
            whereClause.append("')");
        }

        if(countyCd != null) {
            whereClause.append(" AND ca.county_cd = '");
            whereClause.append(countyCd);
            whereClause.append("'");
        }

        if (facilityName != null && facilityName.trim().length() > 0) {
            whereClause.append(" AND LOWER(ff2.facility_nm) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(facilityName.replace("*", "%")));
            whereClause.append("')");
        }
        
        if (cmpId != null && cmpId.length() != 0) { 
			whereClause.append(" AND ccm.CMP_ID = '");
			whereClause.append(cmpId);
			whereClause.append("'");
        }
        if (doLaaCd != null) {
            whereClause.append(" AND ff2.do_laa_cd = '");
            whereClause.append(doLaaCd);
            whereClause.append("'");
        }

        if(usEpaCommitted != null && usEpaCommitted.equals("Y")) {
            whereClause.append(" AND cf.us_epa_committed = 'Y'");
            //whereClause.append(" AND cf.afs_id is not null");
        }

        if(usEpaCommitted != null && usEpaCommitted.equals("N")) {
            whereClause.append(" AND cf.us_epa_committed = 'N'");
            //whereClause.append(" AND cf.afs_id is null");
        }

        if (evaluator != null) {
            whereClause.append(" AND cf.evaluator_id = ");
            whereClause.append(evaluator.intValue());
        }

        if (inspectionReportStateCds != null && !inspectionReportStateCds.isEmpty()) {
        	whereClause.append(" AND (1 = 2 ");
			for (String inspectionReportStateCd : inspectionReportStateCds) {
				whereClause.append("OR cf.inspection_rpt_state_cd = '" + inspectionReportStateCd + "'");
			} 
			whereClause.append(")");
		}

        if (assignedStaff != null) {
            whereClause.append(" AND cf.staff_assigned_id = ");
            whereClause.append(assignedStaff.intValue());
        }

        if (inspId != null && inspId.length() != 0) {
        	inspId = formatId("INSP",inspId);
        	whereClause.append(" AND LOWER(cf.insp_id) LIKE ");
        	whereClause.append("LOWER('");
        	whereClause.append(SQLizeString(inspId.replace("*", "%")));
        	whereClause.append("')");
        }
        
        if (permitClassCd != null) {
        	statementSQL.append(" AND ff2.permit_classification_cd = '");
        	statementSQL.append(permitClassCd);
        	statementSQL.append("'");
		}
        
        if (facilityTypeCd != null) {
        	statementSQL.append(" AND ff2.facility_type_cd = '");
        	statementSQL.append(facilityTypeCd);
        	statementSQL.append("'");
		}

        if(beginSched != null) {
            whereClause.append(" AND cf.scheduled_dt >= ?");
        }

        if(endSched != null) {
            whereClause.append(" AND cf.scheduled_dt < ?");
        }

        if (beginDate != null) {
            whereClause.append(" AND cf.evaluated_dt >= ?");
        }

        if (endDate != null) {
            whereClause.append(" AND cf.evaluated_dt <= ?");
        }
        if(portable != null) {
        	whereClause.append(" AND ff2.portable = '");
            whereClause.append(portable);
            whereClause.append("'");
        }
        StringBuffer sortBy = new StringBuffer(" ORDER BY ff2.facility_id, cf.evaluated_dt DESC, cf.scheduled_dt DESC");

        statementSQL.append(whereClause.toString() + " " + sortBy.toString());
        connHandler.setSQLStringRaw(statementSQL.toString());

        int i = 1;
        if(beginSched != null) {
            connHandler.setTimestamp(i++, beginSched);
        }

        if(endSched != null) {
            connHandler.setTimestamp(i++, endSched);
        }

        if (beginDate != null) {
            connHandler.setTimestamp(i++, beginDate);
        }
        if (endDate != null) {
            connHandler.setTimestamp(i++, endDate);
        }
        ArrayList<FullComplianceEval> ret = connHandler
        .retrieveArray(FullComplianceEval.class);
        return ret.toArray(new FullComplianceEval[0]); 
    }

    public void createVisitEvaluator(Evaluator eval, Integer visitId)  throws DAOException {
        checkNull(eval);
        checkNull(eval.getEvaluator());
        checkNull(visitId);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.createVisitEvaluator", false);
        int i = 1;
        connHandler.setInteger(i++, visitId);
        connHandler.setInteger(i++, eval.getEvaluator());
        connHandler.update();
    }

    public void removeVisitEvaluators(Integer visitId) throws DAOException {
        checkNull(visitId);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.removeVisitEvaluators", false);
        int i = 1;
        connHandler.setInteger(i++, visitId);
        connHandler.remove();
    }

    public SiteVisit createSiteVisit(SiteVisit newVisit)
    throws DAOException {
        checkNull(newVisit);
        SiteVisit ret = newVisit;
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.createSiteVisit", false);

        Integer id = newVisit.getId();
        if(id == null) { // If a value is assigned, keep it.
            id = nextSequenceVal("S_Visit_Id");
        }
        int i = 1;
        connHandler.setInteger(i++, id);
        connHandler.setInteger(i++, ret.getFceId());
        connHandler.setInteger(i++, ret.getFpId());
        connHandler.setTimestamp(i++, ret.getVisitDate());
        connHandler.setString(i++, ret.getVisitType());
        connHandler.setString(i++, ret.getAnnounced());
        connHandler.setString(i++, ret.getMemo());
        connHandler.setString(i++, ret.getAfsId());
        connHandler.setTimestamp(i++, ret.getAfsDate());
        connHandler.setInteger(i++, ret.getFacilityHistId());
        connHandler.setInteger(i++, ret.getCreatedById());
        connHandler.setTimestamp(i++, ret.getCreatedDt());
        connHandler.setString(i++, ret.getSiteVisitVeCd());
        connHandler.setString(i++, ret.getComplianceIssued());
        
        
        connHandler.update();

//      If we get here the INSERT must have succeeded, so set the important
//      data and return the object.
        ret.setId(id);
        ret.setLastModified(1);
        return ret;
    }

    public void modifySiteVisit(SiteVisit visit)
    throws DAOException {
        checkNull(visit);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.modifySiteVisit", false);

        int i = 1;
        connHandler.setInteger(i++, visit.getFceId());
        connHandler.setInteger(i++, visit.getFpId());
        connHandler.setTimestamp(i++, visit.getVisitDate());
        connHandler.setString(i++, visit.getVisitType());
        connHandler.setString(i++, visit.getAnnounced());
        connHandler.setString(i++, visit.getMemo());
        connHandler.setString(i++, visit.getAfsId());
        connHandler.setTimestamp(i++, visit.getAfsDate());
        connHandler.setInteger(i++, visit.getFacilityHistId());
        connHandler.setString(i++, visit.getSiteVisitVeCd());
        connHandler.setString(i++, visit.getComplianceIssued());
        connHandler.setInteger(i++, visit.getLastModified() + 1);
        connHandler.setInteger(i++, visit.getId());
        connHandler.setInteger(i++, visit.getLastModified());

        boolean success = connHandler.update();
        if(!success) {
            throw new DAOException("Update of Site Visit " + visit.getId() + " failed");
        }
    }

    public void removeSiteVisit(Integer visitId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.removeSiteVisit", false);
        connHandler.setInteger(1, visitId);
        connHandler.remove();
    }
    
    public void removeSiteVisitNotes(Integer visitId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.removeSiteVisitNotes", false);
        connHandler.setInteger(1, visitId);
        connHandler.remove();
    }
    
    public void removeStackTestSiteVisit(Timestamp visitDt, 
            String facilityId) throws DAOException  {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.removeStackTestSiteVisit", false);
        connHandler.setTimestamp(1, visitDt);
        connHandler.setString(2, facilityId);
        connHandler.remove();
    }

    public DbInteger lockedStackTestSiteVisitCnt(Timestamp visitDt, 
            String facilityId) throws DAOException  {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.lockedStackTestSiteVisitCnt", false);
        connHandler.setTimestamp(1, visitDt);
        connHandler.setString(2, facilityId);
        return (DbInteger)connHandler.retrieve(DbInteger.class);
    }
    
    public SiteVisit retrieveStackTestVisitByDate(Timestamp visitDt, String facilityId)
    throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        SiteVisit visit = null;
        try {
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(loadSQL("CetaSQL.retrieveStackTestVisitByDate"));
            pStmt.setTimestamp(1, visitDt);
            pStmt.setString(2, facilityId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                visit = new SiteVisit();
                visit.populate(rs);
                do {
                    Evaluator e = new Evaluator();
                    e.populate(rs);
                    if(e.getEvaluator() != null) {
                        visit.getEvaluators().add(e);
                    }
                } while(rs.next());
            }
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        return visit;
    }
    
    public ArrayList<Evaluator> retrieveEmissionsTestWitnesses(String facilityId, Timestamp visitDt) 
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveEmissionsTestWitnesses", true);
        connHandler.setString(1, facilityId);
        connHandler.setTimestamp(2, visitDt);
        ArrayList<? extends Object> ret = connHandler
                .retrieveJavaObjectArray(Integer.class);
        ArrayList<Evaluator> rRet = new ArrayList<Evaluator>();
        for(Object o : ret) {
            rRet.add(new Evaluator((Integer)o));
        }
        ArrayList<Evaluator> orderedEvaluators = orderEvaluators(rRet);
        return orderedEvaluators;
    }

    public SiteVisit retrieveSiteVisit(Integer visitId)
    throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        SiteVisit visit = null;
        try {
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(loadSQL("CetaSQL.retrieveByVisitId"));
            pStmt.setInt(1, visitId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                visit = new SiteVisit();
                visit.populate(rs);
                do {
                    Evaluator e = new Evaluator();
                    e.populate(rs);
                    if(e.getEvaluator() != null) {
                        visit.getEvaluators().add(e);
                    }
                } while(rs.next());
                // order the evaluators
                visit.setEvaluators(orderEvaluators(visit.getEvaluators()));
            }
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (DAOException e) {
            logger.error("orderEvaluators failed", e);
            throw e;
        }catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        return visit;
    }

    public boolean isVisitDup(SiteVisit siteVisit)
    throws DAOException {
        boolean dup = false;
        ConnectionHandler connHandler = new ConnectionHandler(true);
        StringBuffer statementSQL;
        statementSQL = new StringBuffer(loadSQL("CetaSQL.isVisitDup"));

        if(siteVisit.getId() != null) {
            statementSQL.append(" AND csv.visit_id <> " + siteVisit.getId().intValue());
        }
        connHandler.setSQLStringRaw(statementSQL.toString());
        int i = 1;
        connHandler.setString(i++, siteVisit.getVisitType());
        connHandler.setString(i++, siteVisit.getFacilityId());
        connHandler.setTimestamp(i++, formatBeginOfDay(siteVisit.getVisitDate()));
        connHandler.setTimestamp(i++, formatEndOfDay(siteVisit.getVisitDate()));
        SiteVisit visit = (SiteVisit) connHandler
        .retrieve(SiteVisit.class);
        if(visit != null) {
            dup = true;
        }
        return dup;
    }

    public SiteVisit[] retrieveVisitsUnassigned(String facilityId) throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        SiteVisit visit = null;
        ArrayList<SiteVisit> visitList = new ArrayList<SiteVisit>();
        try {
            conn = getReadOnlyConnection();
            StringBuffer statementSQL;
            statementSQL = new StringBuffer(loadSQL("CetaSQL.retrieveVisitsUnassigned"));
            pStmt = conn.prepareStatement(statementSQL.toString());
            pStmt.setString(1, facilityId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) { // prime the loop
                do {
                    visit = new SiteVisit();
                    visit.populate(rs);
                    visitList.add(visit);
                    Integer currentVisitId = visit.getId();
                    do {
                        Evaluator e = new Evaluator();
                        e.populate(rs);
                        if(e.getEvaluator() != null) {
                            visit.getEvaluators().add(e);
                        }
                    } while(rs.next() && currentVisitId.equals(AbstractDAO.getInteger(rs, "visit_id")));
                } while(!rs.isAfterLast());
            };
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        return visitList.toArray(new SiteVisit[0]);
    }

    public SiteVisit[] retrieveSiteVisitsByFce(Integer fceId) throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        SiteVisit visit = null;
        ArrayList<SiteVisit> visitList = new ArrayList<SiteVisit>();
        try {
            conn = getReadOnlyConnection();
            StringBuffer statementSQL;
            statementSQL = new StringBuffer(loadSQL("CetaSQL.retrieveSiteVisitsByFce"));
            pStmt = conn.prepareStatement(statementSQL.toString());
            pStmt.setInt(1, fceId);
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) { // prime the loop
                do {
                    visit = new SiteVisit();
                    visit.populate(rs);
                    visitList.add(visit);
                    Integer currentVisitId = visit.getId();
                    do {
                        Evaluator e = new Evaluator();
                        e.populate(rs);
                        if(e.getEvaluator() != null) {
                            visit.getEvaluators().add(e);
                        }
                    } while(rs.next() && currentVisitId.equals(AbstractDAO.getInteger(rs, "visit_id")));
                } while(!rs.isAfterLast());
            };
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        return visitList.toArray(new SiteVisit[0]); 
    }

    public SiteVisit[] retrieveVisitBySearch(String siteId, String inspId, String facilityId, 
            Timestamp beginDate, Timestamp endVisitDt, String visitType,
            String announced, Integer evaluator, String facilityName,
            String doLaaCd, String countyCd,  String permitClassCd, String facilityTypeCd, String cmpId, String complianceIssued) throws DAOException {
        SiteVisit visit = null;
        ArrayList<SiteVisit> visitList = new ArrayList<SiteVisit>();
        boolean byEvaluator = evaluator != null;
        Connection conn = null;
        PreparedStatement pStmt = null;
        boolean isOnlyStackTest = false;
        boolean isNotIncludingStackTest = false;
        try {
            StringBuffer statementSQL1 = null;
            StringBuffer statementSQL2 = null;
            StringBuffer whereClause = new StringBuffer();

            // Construct the where clause
            if (facilityId != null && facilityId.trim().length() > 0) {
            	facilityId = formatFacilityId(facilityId);
                whereClause.append(" AND LOWER(ff2.facility_id) LIKE ");
                whereClause.append("LOWER('");
                whereClause.append(SQLizeString(facilityId.replace("*", "%")));
                whereClause.append("')");
            }

            if(countyCd != null) {
                whereClause.append(" AND ca.county_cd = '");
                whereClause.append(countyCd);
                whereClause.append("'");
            }

            if (facilityName != null && facilityName.trim().length() > 0) {
                whereClause.append(" AND LOWER(ff2.facility_nm) LIKE ");
                whereClause.append("LOWER('");
                whereClause.append(SQLizeString(facilityName.replace("*", "%")));
                whereClause.append("')");
            }
            
            if (cmpId != null && cmpId.length() != 0) { 
    			whereClause.append(" AND ccm.CMP_ID = '");
    			whereClause.append(cmpId);
    			whereClause.append("'");
            }

            if (doLaaCd != null) {
                whereClause.append(" AND ff2.do_laa_cd = '");
                whereClause.append(doLaaCd);
                whereClause.append("'");
            }

            if (permitClassCd != null) {
            	whereClause.append(" AND ff2.permit_classification_cd = '");
            	whereClause.append(permitClassCd);
            	whereClause.append("'");
    		}
            
            if (facilityTypeCd != null) {
            	whereClause.append(" AND ff2.facility_type_cd = '");
            	whereClause.append(facilityTypeCd);
            	whereClause.append("'");
    		}
            
            if (siteId != null && siteId.length() != 0) {
            	siteId = formatId("SITE",siteId);
            	whereClause.append(" AND LOWER(csv.site_id) LIKE ");
            	whereClause.append("LOWER('");
            	whereClause.append(SQLizeString(siteId.replace("*", "%")));
            	whereClause.append("')");
            }

            if (inspId != null && inspId.length() != 0) {
            	inspId = formatId("INSP",inspId);
            	whereClause.append(" AND LOWER(cf.insp_id) LIKE ");
            	whereClause.append("LOWER('");
            	whereClause.append(SQLizeString(inspId.replace("*", "%")));
            	whereClause.append("')");
            }

            if(visitType != null) {
                whereClause.append(" AND csv.site_visit_type_cd = '");
                whereClause.append(visitType);
                whereClause.append("'");
                if(SiteVisitTypeDef.STACK_TEST.equals(visitType)) {
                    isOnlyStackTest = true;
                } else {
                    isNotIncludingStackTest = true;
                }
            }

            if (announced != null) {
                whereClause.append(" AND csv.announced = '");
                whereClause.append(announced);
                whereClause.append("'");
            }
            
            if (complianceIssued != null) {
                whereClause.append(" AND csv.compliance_issued = '");
                whereClause.append(complianceIssued);
                whereClause.append("'");
            }

            if (beginDate != null) {
                whereClause.append(" AND csv.visit_dt >= ?");
            }

            Timestamp endDate = endVisitDt;
            if (endVisitDt != null) {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(endVisitDt.getTime());
                c.add(Calendar.DATE, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                endDate = new Timestamp(c.getTimeInMillis());
                endDate.setNanos(0);
                whereClause.append(" AND csv.visit_dt < ?");
            }
            
            /*  If evaluator is part of the search criteria the form is:
             *    retrieveVisitBySearchP1
             *    retrieveVisitBySearchP2
             *    whereClause --if there is one
             *    )
             *    
             *   Otherwise the form is
             *   retrieveVisitBySearchP1
             *   whereClause --if there is one
             *   
             *   Both then finish with
             *   ORDER by ff2.facility_id, csv.visit_dt DESC, csv.site_visit_type_cd
             */
            
            /*  If user/evaluator is part of the search criteria then use a two part query:
             *      retrieveVisitBySearchWithUser
             *      whereClause --if there is one
             *    
             *    which returns all the site visit Ids.
             *    
             *   Then the search
             *     retrieveVisitBySearchP1
             *     along with " WHERE csv.visit_id IN ("
             *        the ids gotten back from the first query
             *     along with ") "
             *    
             *   Otherwise the form is
             *     retrieveVisitBySearchP1
             *     whereClause --if there is one
             *   
             *   Both then finish with
             *   ORDER by ff2.facility_id, csv.visit_dt DESC, csv.site_visit_type_cd
             */
            String sortBy = " ORDER by ff2.facility_id, csv.visit_dt DESC, csv.site_visit_type_cd";

            statementSQL1 = null;
            if(byEvaluator) {
                if(!isNotIncludingStackTest) {
                    ConnectionHandler connHandler = new ConnectionHandler(true);
                    statementSQL1 = new StringBuffer(loadSQL("CetaSQL.retrieveStVisitBySearchWithUser"));
                    statementSQL1.append(whereClause.toString());
                    connHandler.setSQLStringRaw(statementSQL1.toString());

                    int i = 1;
                    connHandler.setInteger(i++, evaluator);
                    if (beginDate != null) {
                        connHandler.setTimestamp(i++, beginDate);
                    }
                    if (endDate != null) {
                        connHandler.setTimestamp(i++, endDate);
                    }
                    ArrayList<Integer> siteVisits = connHandler.retrieveJavaObjectArray(Integer.class);
                    if(siteVisits.size() > 0) {
                        statementSQL2 = new StringBuffer(loadSQL("CetaSQL.retrieveVisitBySearchP1"));
                        statementSQL2.append(" AND csv.visit_id IN (");
                        statementSQL2.append(siteVisits.get(0).toString());
                        int j = 1;
                        while(j < siteVisits.size()) {
                            statementSQL2.append("," + siteVisits.get(j++).toString());
                        }

                    }
                }
                if(!isOnlyStackTest) {
                    ConnectionHandler connHandler = new ConnectionHandler(true);
                    statementSQL1 = new StringBuffer(loadSQL("CetaSQL.retrieveOtherVisitBySearchWithUser"));
                    statementSQL1.append(whereClause.toString());
                    connHandler.setSQLStringRaw(statementSQL1.toString());

                    int i = 1;
                    connHandler.setInteger(i++, evaluator);
                    if (beginDate != null) {
                        connHandler.setTimestamp(i++, beginDate);
                    }
                    if (endDate != null) {
                        connHandler.setTimestamp(i++, endDate);
                    }
                    ArrayList<Integer> siteVisits = connHandler.retrieveJavaObjectArray(Integer.class);
                    if(siteVisits.size() > 0) {
                        if(statementSQL2 == null) {
                            statementSQL2 = new StringBuffer(loadSQL("CetaSQL.retrieveVisitBySearchP1"));
                            statementSQL2.append(" AND csv.visit_id IN (");
                        } else  statementSQL2.append(',');
                        statementSQL2.append(siteVisits.get(0).toString());
                        int j = 1;
                        while(j < siteVisits.size()) {
                            statementSQL2.append("," + siteVisits.get(j++).toString());
                        }

                    }
                }
                if(statementSQL2 != null) {
                    statementSQL2.append(")");
                    statementSQL2.append(sortBy);
                }
            } else {
                statementSQL2 = new StringBuffer(loadSQL("CetaSQL.retrieveVisitBySearchP1"));
                statementSQL2.append(whereClause.toString());
                statementSQL2.append(sortBy);
            }
            if(statementSQL2 != null) {
                conn = getReadOnlyConnection();
                pStmt = conn.prepareStatement(statementSQL2.toString());
                if(!byEvaluator) {
                    int i = 1;
                    if (beginDate != null) {
                        pStmt.setTimestamp(i++, beginDate);
                    }
                    if (endDate != null) {
                        pStmt.setTimestamp(i++, endDate);
                    }
                }
                ResultSet rs = pStmt.executeQuery();
                if (rs.next()) { // prime the loop
                    do {
                        visit = new SiteVisit();
                        visit.populate(rs);
                        Integer currentVisitId = visit.getId();

                        do {
                            Evaluator e = new Evaluator();
                            e.populate(rs);
                            //if(evaluator != null && evaluator.equals(e.getEvaluator())) include = true;
                            if(e.getEvaluator() != null) {
                                visit.getEvaluators().add(e);
                            }
                        } while(rs.next() && currentVisitId.equals(AbstractDAO.getInteger(rs, "visit_id")));
                        // order the evaluators
                        List<Evaluator> orderedEvaluators = orderEvaluators(visit.getEvaluators());
                        visit.setEvaluators(orderedEvaluators);
                        // if(evaluator == null || include) {
                        visitList.add(visit);
                        //}
                    } while(!rs.isAfterLast());
                };
            }
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (DAOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }catch (Exception e) {
            handleException(e, conn);
        } finally {
			if (pStmt != null) {
				closeStatement(pStmt);
			}
			if (conn != null) {
				handleClosing(conn);
			}
        }
        return visitList.toArray(new SiteVisit[0]);
    }

    public ArrayList<FceScheduleRow> needToSchedFirstFce(String facilityId, 
            String facilityNm, String doLaaCd, String countyCd,
            String permitClassCd, String facilityTypeCd, String cmpId) throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        FceScheduleRow fceRow = null;
        ArrayList<FceScheduleRow> fceList = new ArrayList<FceScheduleRow>();
        try {
            conn = getReadOnlyConnection();
            StringBuffer statementSQL;
            statementSQL = new StringBuffer(loadSQL("CetaSQL.needToSchedFirstFce"));
        	facilityId = formatFacilityId(facilityId);
            if (facilityId != null && facilityId.trim().length() > 0) {
                statementSQL.append(" AND LOWER(ff.facility_id) LIKE ");
                statementSQL.append("LOWER('");
                statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
                statementSQL.append("')");
            }

            if (facilityNm != null && facilityNm.trim().length() > 0) {
                statementSQL.append(" AND LOWER(ff.facility_nm) LIKE ");
                statementSQL.append("LOWER('");
                statementSQL.append(SQLizeString(facilityNm.replace("*", "%")));
                statementSQL.append("')");
            }
            
            if (permitClassCd != null) {
            	statementSQL.append(" AND ff.permit_classification_cd = '");
            	statementSQL.append(permitClassCd);
            	statementSQL.append("'");
    		}
            
            if (facilityTypeCd != null) {
            	statementSQL.append(" AND ff.facility_type_cd = '");
            	statementSQL.append(facilityTypeCd);
            	statementSQL.append("'");
    		}

            if (doLaaCd != null) {
            	statementSQL.append(" AND ff.do_laa_cd = '");
            	statementSQL.append(doLaaCd);
            	statementSQL.append("'");
            }

            if (countyCd != null) {
                statementSQL.append(" AND ca.county_cd = '");
                statementSQL.append(countyCd);
                statementSQL.append("'");
            }
            
            if (doLaaCd != null) {
            	statementSQL.append(" AND ff.do_laa_cd = '");
            	statementSQL.append(doLaaCd);
            	statementSQL.append("'");
            }
            
            if (cmpId != null && cmpId.trim().length() > 0) { 
            	statementSQL.append(" AND ccm.cmp_id = '");
            	statementSQL.append(cmpId);
            	statementSQL.append("'");
            }
            
            // if scheduled and not completed want the earliest scheduled, otherwise the latest completed date
            statementSQL.append("ORDER BY ff.facility_id");
            pStmt = conn.prepareStatement(statementSQL.toString());

            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) { // prime the loop
                fceRow = new FceScheduleRow();
                fceRow.populate(rs);
                fceList.add(fceRow);
            }; 
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        return fceList;
    }


    public ArrayList<FceScheduleRow> needToSchedFce(String facilityId, 
            String facilityNm, String doLaaCd, String countyCd,
            String permitClassCd, String facilityTypeCd, String cmpId) throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        FceScheduleRow fceRow = null;
        ArrayList<FceScheduleRow> fceList = new ArrayList<FceScheduleRow>();
        try {
            conn = getReadOnlyConnection();
            StringBuffer statementSQL;
            statementSQL = new StringBuffer(loadSQL("CetaSQL.needToSchedFce"));
            facilityId = formatFacilityId(facilityId);
            if (facilityId != null && facilityId.trim().length() > 0) {
                statementSQL.append(" AND LOWER(ff2.facility_id) LIKE ");
                statementSQL.append("LOWER('");
                statementSQL.append(SQLizeString(facilityId.replace("*", "%")));
                statementSQL.append("')");
            }

            if (facilityNm != null && facilityNm.trim().length() > 0) {
                statementSQL.append(" AND LOWER(ff2.facility_nm) LIKE ");
                statementSQL.append("LOWER('");
                statementSQL.append(SQLizeString(facilityNm.replace("*", "%")));
                statementSQL.append("')");
            }

            if (permitClassCd != null) {
            	statementSQL.append(" AND ff2.permit_classification_cd = '");
            	statementSQL.append(permitClassCd);
            	statementSQL.append("'");
    		}
            
            if (facilityTypeCd != null) {
            	statementSQL.append(" AND ff2.facility_type_cd = '");
            	statementSQL.append(facilityTypeCd);
            	statementSQL.append("'");
    		}
            
            if (doLaaCd != null) {
            	statementSQL.append(" AND ff2.do_laa_cd = '");
            	statementSQL.append(doLaaCd);
            	statementSQL.append("'");
            }
            if (countyCd != null) {
                statementSQL.append(" AND ca.county_cd = '");
                statementSQL.append(countyCd);
                statementSQL.append("'");
            }
            
            if (cmpId != null && cmpId.trim().length() > 0) { 
            	statementSQL.append(" AND ccm.cmp_id = '");
            	statementSQL.append(cmpId);
            	statementSQL.append("'");
            }
            
            // if scheduled and not completed want the earliest scheduled, otherwise the latest completed date
            statementSQL.append("ORDER BY ff2.facility_id, cf.evaluated_dt DESC, cf.scheduled_dt DESC");
            pStmt = conn.prepareStatement(statementSQL.toString());

            ResultSet rs = pStmt.executeQuery();
            // Note for a given facility, the scheduled ones are before completed ones because evaluated date null comes before those with dates (due to DESC)
            if (rs.next()) { // prime the loop
                do {
                    int maxFfy = 0;
                    fceRow = new FceScheduleRow();
                    fceRow.populate(rs);
                    String currentFacilityId = fceRow.getFacilityId();
                    FceScheduleRow holdCompleted = null;
                    FceScheduleRow holdScheduled = null;
                    if(fceRow.getLastCompleted() == null ) {
                        // latest scheduled/not completed keep it.
                    	holdScheduled = fceRow;
                        fceList.add(fceRow);
                        maxFfy = addFacilityRow(maxFfy, fceRow);
                    } else {
                        // first completed one, keep it.
                        holdCompleted = fceRow;
                        fceList.add(holdCompleted);
                        maxFfy = addFacilityRow(maxFfy, fceRow);
                    }
                    while(rs.next()) {
                        if(!currentFacilityId.equals(rs.getString("facility_id"))) break;  // next facility
                        fceRow = new FceScheduleRow();
                        fceRow.populate(rs);
                        if(fceRow.getLastCompleted() == null ) {
                        	// if first scheduled/not completed.
                        	if(holdScheduled == null) {
                        		holdScheduled = fceRow;
                        		fceList.add(fceRow);
                        		maxFfy = addFacilityRow( maxFfy, fceRow);
                        	}
                        } else {
                            if(holdCompleted == null) {
                                // first completed one, keep it.
                                holdCompleted = fceRow;
                                maxFfy = addFacilityRow(maxFfy, fceRow);
                                // if the scheduled one is earlier date; then don't keep it.  Scheduled ones are processed first.
                                if(holdScheduled != null && holdScheduled.getNextScheduled().before(holdCompleted.getLastCompleted())) {
                                	fceList.remove(fceList.size()  - 1);
                                }
                                fceList.add(fceRow);
                            }
                        }
                    }
                    // set order value for all rows in same facility.
                    // Order will be the FFY of the highest scheduled or completed Inspection.
                    for(FceScheduleRow fsr : fceList) {
                        fsr.setOrder(maxFfy);
                    }
                } while(!rs.isAfterLast());
            }; 
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        return fceList;
    }
    
    private int addFacilityRow(int maxFfy, FceScheduleRow r) {
        if(r.getNextScheduled() != null) {
            int y = getFfy(r.getNextScheduled());
            r.setNeededBy(y);
            if(y > maxFfy) maxFfy = y;
        }
        return maxFfy;
    }
    
//  Return Federal Fiscal Year.  It is the year that includes September
    int getFfy(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(ts);
        if(cal.get(Calendar.MONTH) < Calendar.SEPTEMBER) {
            return cal.get(Calendar.YEAR);
        } else {
            return cal.get(Calendar.YEAR) + 1;
        }
    }

    /**
     * @see FullComplianceEvalDAO#getUserId()
     */
    public DbInteger getUserId(String name) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.getUserId", false);
        connHandler.setString(1, name);
        DbInteger dbI = (DbInteger)connHandler.retrieve(DbInteger.class); 
        return dbI;
    }

    public final boolean modifyFceAttachment(FceAttachment doc)
    throws DAOException {
        checkNull(doc);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.modifyFceAttachment", false);

        int i=1;
        connHandler.setString(i++, doc.getDocTypeCd());
        connHandler.setInteger(i++, doc.getRefLastModified() + 1);
        connHandler.setInteger(i++, doc.getDocumentID());
        connHandler.setInteger(i++, doc.getFceId());
        connHandler.setInteger(i++, doc.getRefLastModified());
        return connHandler.update();
    }

    public FceAttachment createFceAttachment(FceAttachment fa) throws DAOException {
          checkNull(fa);
            ConnectionHandler connHandler = new ConnectionHandler(
                    "CetaSQL.createFceAttachment", false);

            int i = 1;
            connHandler.setInteger(i++, fa.getDocumentID());
            connHandler.setInteger(i++, fa.getFceId());
            connHandler.setString(i++, fa.getDocTypeCd());
            connHandler.update();
            fa.setLastModified(1);
            return fa;
    }

    public void deleteFceAttachment(FceAttachment att)
    throws DAOException {
        checkNull(att);
        checkNull(att.getFceId());
        checkNull(att.getDocumentID());
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.deleteFceAttachment", false);
        connHandler.setInteger(1, att.getFceId());
        connHandler.setInteger(2, att.getDocumentID());
        connHandler.remove();
    }

    public List<FceAttachment> retrieveFceAttachments(
            int visitId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveFceAttachments", true);
        connHandler.setInteger(1, visitId);
        return connHandler.retrieveArray(FceAttachment.class);
    }
    
    public final boolean modifySvAttachment(SvAttachment doc)
    throws DAOException {
        checkNull(doc);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.modifySvAttachment", false);

        int i=1;
        connHandler.setString(i++, doc.getDocTypeCd());
        connHandler.setInteger(i++, doc.getRefLastModified() + 1);
        connHandler.setInteger(i++, doc.getDocumentID());
        connHandler.setInteger(i++, doc.getVisitId());
        connHandler.setInteger(i++, doc.getRefLastModified());
        return connHandler.update();
    }

    public SvAttachment createSvAttachment(SvAttachment fa) throws DAOException {
          checkNull(fa);
            ConnectionHandler connHandler = new ConnectionHandler(
                    "CetaSQL.createSvAttachment", false);

            int i = 1;
            connHandler.setInteger(i++, fa.getDocumentID());
            connHandler.setInteger(i++, fa.getVisitId());
            connHandler.setString(i++, fa.getDocTypeCd());
            connHandler.update();
            fa.setLastModified(1);
            return fa;
    }

    public void deleteSvAttachment(SvAttachment att)
    throws DAOException {
        checkNull(att);
        checkNull(att.getVisitId());
        checkNull(att.getDocumentID());
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.deleteSvAttachment", false);
        connHandler.setInteger(1, att.getVisitId());
        connHandler.setInteger(2, att.getDocumentID());
        connHandler.remove();
    }

    public List<SvAttachment> retrieveSvAttachments(
            int fceId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveSvAttachments", true);
        connHandler.setInteger(1, fceId);
        return connHandler.retrieveArray(SvAttachment.class);
    }
    
    
    public List<FullComplianceEval> newAfsScheduledFCEs(Timestamp beginSched, Timestamp endSched) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.newAfsScheduledFCEs", true);
        connHandler.setTimestamp(1, beginSched);
        connHandler.setTimestamp(2, endSched);
        return connHandler.retrieveArray(FullComplianceEval.class);
    }
    
    public List<FullComplianceEval> newAfsFCEs() throws DAOException { 
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.newAfsFCEs", true);
        return connHandler.retrieveArray(FullComplianceEval.class);
    }
    
    public List<SiteVisit> newAfsSiteVisits() throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        SiteVisit visit = null;
        ArrayList<SiteVisit> visitList = new ArrayList<SiteVisit>();
        try {
            conn = getReadOnlyConnection();

            StringBuffer statementSQL;
            statementSQL = new StringBuffer(loadSQL("CetaSQL.newAfsSiteVisits"));
            pStmt = conn.prepareStatement(statementSQL.toString());

            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) { // prime the loop
                do {
                    visit = new SiteVisit();
                    visit.populate(rs);
                    Integer currentVisitId = visit.getId();
                    do {
                        Evaluator e = new Evaluator();
                        e.populate(rs);
                        if(e.getEvaluator() != null) {
                            visit.getEvaluators().add(e);
                        }
                    } while(rs.next() && currentVisitId.equals(AbstractDAO.getInteger(rs, "visit_id")));
                    // If for Stack Test, do not make available until at least
                    // part of the Stack Test has been submitted.
                    boolean include = true;
                    if(SiteVisitTypeDef.STACK_TEST.equals(visit.getVisitType())) {
                        List<DbInteger> stList = isStackTestSubmitted(visit.getVisitDate());
                        if(stList.size() == 0) {
                            include = false;
                        }
                    }
                    if(include) visitList.add(visit);
                } while(!rs.isAfterLast());
            }; 
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        return visitList;
    }
    
    public List<DbInteger> isStackTestSubmitted(Timestamp ts) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.isStackTestSubmitted", false);
        connHandler.setTimestamp(1, ts);; 
        return connHandler.retrieveArray(DbInteger.class);
    }
    
    public boolean afsLockFceComp(FullComplianceEval fce, Integer afsId)
    throws DAOException {
        checkNull(fce);
        checkNull(afsId);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.afsLockFceComp", false);

        int i=1;
        connHandler.setString(i++, convertAfsIdToString(afsId));
        connHandler.setInteger(i++, fce.getLastModified() + 1);
        connHandler.setString(i++, fce.getId());
        connHandler.setInteger(i++, fce.getLastModified());
        return connHandler.update();
    }
    
    public boolean afsSetDateFceComp(FullComplianceEval fce)
    throws DAOException {
        checkNull(fce);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.afsSetDateFceComp", false);

        int i=1;
        connHandler.setTimestamp(i++, fce.getEvalAfsDate());
        connHandler.setInteger(i++, fce.getLastModified() + 1);
        connHandler.setString(i++, fce.getId());
        connHandler.setInteger(i++, fce.getLastModified());
        return connHandler.update();
    }
    
    public boolean afsLockFceSched(FullComplianceEval fce)
    throws DAOException {
        checkNull(fce);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.afsLockFceSched", false);

        int i=1;
        connHandler.setString(i++, fce.getSchedAfsId());
        connHandler.setInteger(i++, fce.getLastModified() + 1);
        connHandler.setString(i++, fce.getId());
        connHandler.setInteger(i++, fce.getLastModified());
        return connHandler.update();
    }
    
    public boolean afsSetDateFceSched(FullComplianceEval fce)
    throws DAOException {
        checkNull(fce);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.afsSetDateFceSched", false);

        int i=1;
        connHandler.setTimestamp(i++, fce.getSchedAfsDate());
        connHandler.setInteger(i++, fce.getLastModified() + 1);
        connHandler.setString(i++, fce.getId());
        connHandler.setInteger(i++, fce.getLastModified());
        return connHandler.update();
    }
    
    public boolean afsLockSiteVisit(SiteVisit sv, Integer afsId)
    throws DAOException {
        checkNull(sv);
        checkNull(afsId);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.afsLockSiteVisit", false);

        int i=1;
        connHandler.setString(i++, convertAfsIdToString(afsId));
        connHandler.setInteger(i++, sv.getLastModified() + 1);
        connHandler.setString(i++, sv.getId());
        connHandler.setInteger(i++, sv.getLastModified());
        return connHandler.update();
    }
    
    public boolean afsSetDateSiteVisit(SiteVisit sv)
    throws DAOException {
        checkNull(sv);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.afsSetDateSiteVisit", false);

        int i=1;
        connHandler.setTimestamp(i++, sv.getAfsDate());
        connHandler.setInteger(i++, sv.getLastModified() + 1);
        connHandler.setString(i++, sv.getId());
        connHandler.setInteger(i++, sv.getLastModified());
        return connHandler.update();
    }
    
    public FullComplianceEval retrieveLastCompletedFce(String facilityId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveLastCompletedFce", true);
        connHandler.setString(1, facilityId);
        FullComplianceEval fce = (FullComplianceEval) connHandler.retrieve(FullComplianceEval.class);
        return fce;
    }
    
    public FullComplianceEval retrieveLastScheduledFce(String facilityId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveLastScheduledFce", true);
        connHandler.setString(1, facilityId);
        FullComplianceEval fce = (FullComplianceEval) connHandler.retrieve(FullComplianceEval.class);
        return fce;
    }
    

    public SiteVisit retrieveLastSiteVisit(String facilityId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveLastSiteVisit", true);
        connHandler.setString(1, facilityId);
        SiteVisit sv = (SiteVisit) connHandler.retrieve(SiteVisit.class);
        return sv;
    }
    
    public String fceAfsLocked(Integer fceId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.fceAfsLockedId", true);
        connHandler.setInteger(1, fceId);
        ArrayList<? extends Object> afsIds = connHandler
                .retrieveJavaObjectArray(String.class);
        if(afsIds.size() != 1) return null;
        if(afsIds.get(0) != null) return (String)afsIds.get(0);
        return null;
    }
    
    public String siteVisitAfsLocked(Integer siteVisitId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.siteVisitAfsLockedId", true);
        connHandler.setInteger(1, siteVisitId);
        ArrayList<? extends Object> afsIds = connHandler
                .retrieveJavaObjectArray(String.class);
        if(afsIds.size() != 1) return null;
        if(afsIds.get(0) != null) return (String)afsIds.get(0);
        return null;
    }
    
    public List<FullComplianceEval> retrieveSchedFceByAfsId(String scscId, String afsId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveSchedFceByAfsId", true);
        connHandler.setString(1, scscId);
        connHandler.setString(2, afsId);
        ArrayList<FullComplianceEval> ret = connHandler.retrieveArray(FullComplianceEval.class);
        return ret;
    }
    
    public List<FullComplianceEval> retrieveEvalFceByAfsId(String scscId, String afsId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveEvalFceByAfsId", true);
        connHandler.setString(1, scscId);
        connHandler.setString(2, afsId);
        ArrayList<FullComplianceEval> ret = connHandler.retrieveArray(FullComplianceEval.class);
        return ret;
    }
    
    public List<SiteVisit> retrieveSiteVisitByAfsId(String scscId, String afsId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveSiteVisitByAfsId", true);
        connHandler.setString(1, scscId);
        connHandler.setString(2, afsId);
        ArrayList<SiteVisit> ret = connHandler.retrieveArray(SiteVisit.class);
        return ret;
    }
    
    public ArrayList<Integer> complianceSearch(String facilityId, 
            String facilityNm, String operatingStatusCd, String doLaaCd, String countyCd,  List<String> selectedInspectClasses,
            List<String> selectedSipList, List<String> selectedMactList, 
            List<String> selectedTvList, List<String> selectedSmList,
            List<String> selectedNeshapsList, List<String> selectedNspsList,
            List<String> selectedPsdList, List<String> selectedNsrList) throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        ArrayList<Integer> fpList = new ArrayList<Integer>(1000);
        StringBuffer sqlThruTable = new StringBuffer(replaceSchema("SELECT DISTINCT ff.fp_id, ff.facility_id FROM %Schema%fp_facility ff"));
        try {
            boolean needAND = true;
            conn = getReadOnlyConnection();
            StringBuffer sqlWhere = new StringBuffer("ff.version_id = -1");
            
            if (facilityId != null && facilityId.trim().length() > 0) {
                if(needAND) sqlWhere.append(" AND ");
                needAND = true;
                sqlWhere.append(" LOWER(ff.facility_id) LIKE ");
                sqlWhere.append("LOWER('");
                sqlWhere.append(SQLizeString(facilityId.replace("*", "%")));
                sqlWhere.append("')");
            }

            if (facilityNm != null && facilityNm.trim().length() > 0) {
                if(needAND) sqlWhere.append(" AND ");
                needAND = true;
                sqlWhere.append(" LOWER(ff.facility_nm) LIKE ");
                sqlWhere.append("LOWER('");
                sqlWhere.append(SQLizeString(facilityNm.replace("*", "%")));
                sqlWhere.append("')");
            }
            
            if (operatingStatusCd != null) {
                if(needAND) sqlWhere.append(" AND ");
                needAND = true;
                sqlWhere.append(" ff.operating_status_cd = '");
                sqlWhere.append(operatingStatusCd);
                sqlWhere.append("'");
            }
            
            if (doLaaCd != null) {
                sqlWhere.append(" AND ff.do_laa_cd = '");
                sqlWhere.append(doLaaCd);
                sqlWhere.append("'");
            }

            if (countyCd != null) {
                sqlThruTable.append(replaceSchema(", %Schema%cm_address ca"));
                sqlThruTable.append(replaceSchema(", %Schema%fp_facility_address_xref faxr"));
                if(needAND) sqlWhere.append(" AND ");
                needAND = true;
                sqlWhere.append(" ff.fp_id = faxr.fp_id AND faxr.address_id = ca.address_id AND ca.end_dt is null AND ca.county_cd = '");
                sqlWhere.append(countyCd);
                sqlWhere.append("'");
            }
            
            StringBuffer oredItems = new StringBuffer();
            
            
            boolean needOR = false;
            
            if (selectedSipList != null && selectedSipList.size() > 0) {
                String start = "";
                for(String s : selectedSipList) {
                    oredItems.append(start + "ff.sip_comp_cd = '");
                    oredItems.append(s);
                    oredItems.append("'");
                    start = " OR ";
                }
                needOR = true;
            }
            
            if (selectedMactList != null && selectedMactList.size() > 0) {
                if(needOR) oredItems.append(" OR " );
                String start = "";
                for(String s : selectedMactList) {
                    oredItems.append(start + "ff.mact_comp_cd = '");
                    oredItems.append(s);
                    oredItems.append("'");
                    start = " OR ";
                }
                needOR = true;
            }
            
            if (selectedTvList != null && selectedTvList.size() > 0) {
                if(needOR) oredItems.append(" OR " );
                String start = " ff.air_program_cd = '" + AirProgramDef.TITLE_V + "' AND (";
                for(String s : selectedTvList) {
                    oredItems.append(start + "ff.air_program_comp_cd = '");
                    oredItems.append(s);
                    oredItems.append("'");
                    start = " OR ";
                }
                oredItems.append(")");
                needOR = true;
            }
            
            if (selectedSmList != null && selectedSmList.size() > 0) {
                if(needOR) oredItems.append(" OR " );
                String start = " ff.air_program_cd = '" + AirProgramDef.SM_FESOP + "' AND (";
                for(String s : selectedSmList) {
                    oredItems.append(start + "ff.air_program_comp_cd = '");
                    oredItems.append(s);
                    oredItems.append("'");
                    start = " OR ";
                }
                oredItems.append(")");
                needOR = true;
            }
            
            if (selectedNeshapsList != null) {
                if(needOR) oredItems.append(" OR " );
                sqlThruTable.append(replaceSchema(", %Schema%fp_facility_neshaps_xref neshx"));
                oredItems.append(" neshx.fp_id = ff.fp_id AND neshx.subpart_comp_cd IN (");
                boolean notFirst = false;
                for(String s : selectedNeshapsList) {
                    if(notFirst) {
                        oredItems.append(", ");
                    }
                    notFirst = true;
                    oredItems.append("'" + s + "'");
                }
                oredItems.append(")");
                needOR = true;
            }

            if (selectedNspsList != null) {
                if(needOR) oredItems.append(" OR " );
                sqlThruTable.append(replaceSchema(", %Schema%fp_facility_nsps_pollutants nspsx"));
                oredItems.append(" nspsx.fp_id = ff.fp_id AND nspsx.pollutant_comp_cd IN (");
                boolean notFirst = false;
                for(String s : selectedNspsList) {
                    if(notFirst) {
                        oredItems.append(", ");
                    }
                    notFirst = true;
                    oredItems.append("'" + s + "'");
                }
                oredItems.append(")");
                needOR = true;
            }

            if (selectedNsrList != null) {
                if(needOR) oredItems.append(" OR " );
                sqlThruTable.append(replaceSchema(", %Schema%fp_facility_nsr_pollutants nsrx"));
                oredItems.append(" nsrx.fp_id = ff.fp_id AND nsrx.pollutant_comp_cd IN (");
                boolean notFirst = false;
                for(String s : selectedNsrList) {
                    if(notFirst) {
                        oredItems.append(", ");
                    }
                    notFirst = true;
                    oredItems.append("'" + s + "'");
                }
                oredItems.append(")");
                needOR = true;
            }
            
            if (selectedPsdList != null) {
                if(needOR) oredItems.append(" OR " );
                sqlThruTable.append(replaceSchema(", %Schema%fp_facility_psd_pollutants psdx"));
                oredItems.append(" psdx.fp_id = ff.fp_id AND psdx.pollutant_comp_cd IN (");
                boolean notFirst = false;
                for(String s : selectedPsdList) {
                    if(notFirst) {
                        oredItems.append(", ");
                    }
                    notFirst = true;
                    oredItems.append("'" + s + "'");
                }
                oredItems.append(")");
            }
            
            if(needAND && oredItems.length() > 0) sqlWhere.append(" AND (");
            
            if(needAND && oredItems.length() > 0) oredItems.append(") ");

            String sql = sqlThruTable.toString() + " WHERE " + sqlWhere.toString() + oredItems.toString();
            pStmt = conn.prepareStatement(sql);

            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) { // prime the loop
                Integer fpId = AbstractDAO.getInteger(rs, "fp_id");
                fpList.add(fpId);
            }; 
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        }
        return fpList;
        
    }

    public ArrayList<AirProgramCompliance> complianceSearch(List<Integer> fpList) throws DAOException {
        PreparedStatement pStmt = null;
        Connection conn = null;
        ArrayList<AirProgramCompliance> compList = new ArrayList<AirProgramCompliance>(fpList.size());
        Facility fac = new Facility();
        for(Integer i : fpList) {
            AirProgramCompliance compRec = new AirProgramCompliance();
            conn = null;
            pStmt = null;

            try {
                conn = getReadOnlyConnection();
                pStmt = conn.prepareStatement(loadSQL("FacilitySQL.retrieveOnlyFacilityData"));
                pStmt.setInt(1, i);
                ResultSet rs = pStmt.executeQuery();
                if (rs.next()) {
                    fac.populate(rs);
                }
                rs.close();
                compRec.setDoLaaCd(fac.getDoLaaCd());
                compRec.setFacilityName(fac.getName());
                compRec.setFacilityId(fac.getFacilityId());
                compRec.setOperatingStatusCd(fac.getOperatingStatusCd());
                compRec.setShutdownDate(fac.getShutdownDate());
                compRec.setSipComp(fac.getSipCompCd());
                compRec.setMactComp(fac.getMactCompCd());
                if(AirProgramDef.TITLE_V.equals(fac.getAirProgramCd())) {
                    compRec.setTvComp(fac.getAirProgramCompCd());
                } else if(AirProgramDef.SM_FESOP.equals(fac.getAirProgramCd())) {
                    compRec.setSmComp(fac.getAirProgramCompCd());
                }
            } catch (Exception e) {
                handleException(e, conn);
            } finally {
                closeStatement(pStmt);
                handleClosing(conn);
            }
            
            try {
                conn = getReadOnlyConnection();
                pStmt = conn.prepareStatement(replaceSchema("SELECT DISTINCT subpart_comp_cd FROM %Schema%FP_FACILITY_NESHAPS_XREF WHERE fp_id = ? ORDER BY subpart_comp_cd"));
                pStmt.setInt(1, i);
                ResultSet rs = pStmt.executeQuery();
                if (rs.next()) {
                    compRec.setNeshapsComp(rs.getString("subpart_comp_cd"));
                }

            } catch (Exception e) {
                handleException(e, conn);
            } finally {
                closeStatement(pStmt);
                handleClosing(conn);
            }
            
            try {
                conn = getReadOnlyConnection();
                pStmt = conn.prepareStatement(replaceSchema("SELECT DISTINCT pollutant_comp_cd FROM %Schema%FP_FACILITY_NSPS_POLLUTANTS WHERE fp_id = ? ORDER BY pollutant_comp_cd"));
                pStmt.setInt(1, i);
                ResultSet rs = pStmt.executeQuery();
                if (rs.next()) {
                    compRec.setNspsComp(rs.getString("pollutant_comp_cd"));
                }

            } catch (Exception e) {
                handleException(e, conn);
            } finally {
                closeStatement(pStmt);
                handleClosing(conn);
            }
            
            try {
                conn = getReadOnlyConnection();
                pStmt = conn.prepareStatement(replaceSchema("SELECT DISTINCT pollutant_comp_cd FROM %Schema%FP_FACILITY_PSD_POLLUTANTS WHERE fp_id = ? ORDER BY pollutant_comp_cd"));
                pStmt.setInt(1, i);
                ResultSet rs = pStmt.executeQuery();
                if (rs.next()) {
                    compRec.setPsdComp(rs.getString("pollutant_comp_cd"));
                }

            } catch (Exception e) {
                handleException(e, conn);
            } finally {
                closeStatement(pStmt);
                handleClosing(conn);
            }

            try {
                conn = getReadOnlyConnection();
                pStmt = conn.prepareStatement(replaceSchema("SELECT DISTINCT pollutant_comp_cd FROM %Schema%FP_FACILITY_NSR_POLLUTANTS WHERE fp_id = ? ORDER BY pollutant_comp_cd"));
                pStmt.setInt(1, i);
                ResultSet rs = pStmt.executeQuery();
                if (rs.next()) {
                    compRec.setNsrComp(rs.getString("pollutant_comp_cd"));
                }

            } catch (Exception e) {
                handleException(e, conn);
            } finally {
                closeStatement(pStmt);
                handleClosing(conn);
            }
            compList.add(compRec);
        }
        return compList;
    }
    
    public ArrayList<Evaluator> orderEvaluators(List<Evaluator> eList) throws DAOException {
        if(eList == null || eList.size() == 0) return new ArrayList<Evaluator>();
        if(eList.size() == 1) {
            ArrayList<Evaluator> el = new ArrayList<Evaluator>();
            el.add(eList.get(0));
            return el;
        }
        PreparedStatement pStmt = null;
        Connection conn = getReadOnlyConnection();
        StringBuffer sql = new StringBuffer(loadSQL(("CetaSQL.orderEvaluators")));
        sql.append(" ( ");
        boolean notFirst = false;
        for(Evaluator e : eList) {
            if(notFirst) {
                sql.append(", ");
            }
            notFirst = true;
            sql.append(e.getEvaluator().toString());
        }
        //sql.append(" )");
        sql.append(" ) ORDER BY cud.last_nm, cud.first_nm");
        ArrayList<Evaluator> orderedList = new ArrayList<Evaluator>();
        try {
            pStmt = conn.prepareStatement(sql.toString());
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) { // prime the loop
                Integer userId = AbstractDAO.getInteger(rs, "user_id");
                orderedList.add(new Evaluator(userId));
            }; 
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
        	handleException(e, conn);
        } finally {
        	closeStatement(pStmt);
        	handleClosing(conn);
        }
        return orderedList;
    }


    public List<FullComplianceEval> fceNeedReminders() throws DAOException {
    	ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.fceNeedReminders", true);
    	// Start with last quarter of 2013--ignore anything earlier
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.DAY_OF_YEAR, 21);  // any Inspection starting within 21 days will be selected
    	Timestamp ts = new Timestamp(cal.getTimeInMillis());
        connHandler.setTimestamp(2, ts);
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, Calendar.OCTOBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);  // as long is it is no older than October 2013.
        ts = new Timestamp(cal.getTimeInMillis());
        connHandler.setTimestamp(1, ts);
        ArrayList<FullComplianceEval> ret = connHandler.retrieveArray(FullComplianceEval.class);
        return ret; 
    }
    
    /**
	 * @see us.oh.state.epa.stars2.database.dao.FullComplianceEvalDAO#retrieveInspectionNotes(int)
	 */
	public final Note[] retrieveInspectionNotes(int fceID) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveInspectionNotes", true);
		connHandler.setInteger(1, fceID);
		ArrayList<InspectionNote> ret = connHandler.retrieveArray(InspectionNote.class);

		return ret.toArray(new Note[0]);
	}
	
	/**
	 * @see us.oh.state.epa.stars2.database.dao.FullComplianceEvalDAO#createPermitNote(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public final void createInspectionNote(Integer fceId, Integer noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createInspectionNote", false);

		checkNull(fceId);
		checkNull(noteId);
		connHandler.setInteger(1, fceId);
		connHandler.setInteger(2, noteId);

		connHandler.update();
	}
	
	   /**
		 * @see us.oh.state.epa.stars2.database.dao.FullComplianceEvalDAO#retrieveSiteVisitNotes(int)
		 */
		public final Note[] retrieveSiteVisitNotes(int visitId) throws DAOException {
			ConnectionHandler connHandler = new ConnectionHandler(
					"CetaSQL.retrieveSiteVisitNotes", true);
			connHandler.setInteger(1, visitId);
			ArrayList<SiteVisitNote> ret = connHandler.retrieveArray(SiteVisitNote.class);

			return ret.toArray(new Note[0]);
		}
		
		/**
		 * @see us.oh.state.epa.stars2.database.dao.FullComplianceEvalDAO#createSiteVisitNote(java.lang.Integer,
		 *      java.lang.Integer)
		 */
		public final void createSiteVisitNote(Integer visitId, Integer noteId)
				throws DAOException {
			ConnectionHandler connHandler = new ConnectionHandler(
					"CetaSQL.createSiteVisitNote", false);

			checkNull(visitId);
			checkNull(noteId);
			connHandler.setInteger(1, visitId);
			connHandler.setInteger(2, noteId);

			connHandler.update();
		}
		
	/**
	 * Retrieves all uncompleted inspections that are not associated with stack
	 * tests or site visits.
	 */
	@Override
	public FullComplianceEval[] retrieveFcesWithoutSiteVisitsOrStackTests(
			String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveFceWithoutSVsOrSTs", true);
		connHandler.setString(1, facilityId);
		ArrayList<FullComplianceEval> ret = connHandler
				.retrieveArray(FullComplianceEval.class);
		return ret.toArray(new FullComplianceEval[0]);
	}
//	@Override
//	public void modifyFceLastInsp(FullComplianceEval fce) throws DAOException{
//		checkNull(fce);
//		ConnectionHandler connHandler = new ConnectionHandler(
//	                "CetaSQL.modifyFceLastInsp", false);
//	    int i = 1;
//	    connHandler.setString(i++, fce.getLastInspId());
//	    connHandler.setTimestamp(i++, fce.getLastInspDate());
//        connHandler.setInteger(i++, fce.getLastModified() + 1);
//        connHandler.setInteger(i++, fce.getId());
//        connHandler.setInteger(i++, fce.getLastModified());
//	    boolean success = connHandler.update();
//	    if(!success) {
//	       throw new DAOException("Update of Inspection " + fce.getId() + " failed");
//	    }
//	}
	
    public FullComplianceEval retrieveLastPriorCompletedFce(String facilityId, Integer fceId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.retrieveLastPriorCompletedFce", true);
        connHandler.setString(1, facilityId);
        connHandler.setString(2, fceId);
        FullComplianceEval fce = (FullComplianceEval) connHandler.retrieve(FullComplianceEval.class);
        return fce;
    }

	@Override
	public void createAdditionalAQDStaff(Integer aqdUserId, Integer fceId)
			throws DAOException {
		checkNull(aqdUserId);
		checkNull(fceId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createAdditionalAQDStaff", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, aqdUserId);
		connHandler.update();
	}
	
	@Override
	public void deleteAdditionalAQDStaffByFceId(Integer fceId)
			throws DAOException {
		checkNull(fceId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.deleteAdditionalAQDStaffByFceId", false);
		
		connHandler.setInteger(1, fceId);
		
		connHandler.remove();
		
		return;
	}
	
	@Override
	public List<Evaluator> retrieveAdditionalAQDStaffByFceId(Integer fceId)
			throws DAOException {
		checkNull(fceId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveAdditionalAQDStaffByFceId", true);

		connHandler.setInteger(1, fceId);
		ArrayList<Evaluator> ret = connHandler.retrieveArray(Evaluator.class); 

		return ret;
	}
	
//	@Override
//	public void modifyFceReportState(FullComplianceEval fce, Integer lastFceId) throws DAOException{
//		checkNull(fce);
//		ConnectionHandler connHandler = new ConnectionHandler(
//	                "CetaSQL.updateFcePrepare", false);
//	    int i = 1;
//	    connHandler.setInteger(i++, fce.getFpId());
//	    connHandler.setString(i++, fce.getInspectionReportStateCd());  
//	    connHandler.setInteger(i++, lastFceId);
//        connHandler.setInteger(i++, fce.getLastModified() + 1);
//        connHandler.setInteger(i++, fce.getId());
//        connHandler.setInteger(i++, fce.getLastModified());
//	    boolean success = connHandler.update();
//	    if(!success) {
//	       throw new DAOException("Prepare Inspection Report " + fce.getId() + " failed");
//	    }
//	}
	
	@Override
	public void modifyFcePrepare(FullComplianceEval fce) throws DAOException{
		checkNull(fce);
		ConnectionHandler connHandler = new ConnectionHandler(
	                "CetaSQL.updateFcePrepare", false);
	    int i = 1;
	    connHandler.setInteger(i++, fce.getFpId());
	    connHandler.setString(i++, fce.getInspectionReportStateCd());  
	    connHandler.setInteger(i++, fce.getLastFceId());
        connHandler.setInteger(i++, fce.getLastModified() + 1);
        connHandler.setInteger(i++, fce.getId());
        connHandler.setInteger(i++, fce.getLastModified());
	    boolean success = connHandler.update();
	    if(!success) {
	       throw new DAOException("Prepare Inspection Report " + fce.getId() + " failed");
	    }
	}

	@Override
	public void modifyFceComplete(FullComplianceEval fce) throws DAOException{
		checkNull(fce);
		ConnectionHandler connHandler = new ConnectionHandler(
	                "CetaSQL.updateFceComplete", false);
	    int i = 1;
	    connHandler.setInteger(i++, fce.getFpId()); 
	    connHandler.setString(i++, fce.getInspectionReportStateCd()); 
		connHandler.setTimestamp(i++,fce.getDateReported());
        connHandler.setInteger(i++, fce.getEvaluator());
        connHandler.setInteger(i++, fce.getLastModified() + 1);
        connHandler.setInteger(i++, fce.getId());
        connHandler.setInteger(i++, fce.getLastModified());
	    boolean success = connHandler.update();
	    if(!success) {
	       throw new DAOException("Complete Inspection Report " + fce.getId() + " failed");
	    }
	}
	
	@Override
	public void modifyFceReportState(FullComplianceEval fce) throws DAOException{
		checkNull(fce);
		ConnectionHandler connHandler = new ConnectionHandler(
	                "CetaSQL.updateFceReportState", false);
	    int i = 1;
	    connHandler.setString(i++, fce.getInspectionReportStateCd());
        connHandler.setInteger(i++, fce.getLastModified() + 1);
        connHandler.setInteger(i++, fce.getId());
        connHandler.setInteger(i++, fce.getLastModified());
	    boolean success = connHandler.update();
	    if(!success) {
	       throw new DAOException("Change Report State for Inspection " + fce.getId() + " failed");
	    }
	}

	@Override
	public void createFceAmbientConditions(AmbientConditions ambientConditions) throws DAOException {
		checkNull(ambientConditions);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.createFceAmbientCondition", false);
		int i = 1;
		connHandler.setInteger(i++, ambientConditions.getFceId());
		connHandler.setInteger(i++, ambientConditions.getInspectionDay());
		connHandler.setTimestamp(i++, ambientConditions.getInspectionDate());
		connHandler.setString(i++, ambientConditions.getArrivalTime());
		connHandler.setString(i++, ambientConditions.getDepartureTime());
		connHandler.setInteger(i++, ambientConditions.getAmbientTemperature());
		connHandler.setInteger(i++, ambientConditions.getWindSpeed());
		connHandler.setString(i++, ambientConditions.getWindDirectionCd());
		connHandler.setString(i++, ambientConditions.getSkyConditionCd());

		boolean success = connHandler.update();
		if (!success) {
			throw new DAOException(
					"Creation of Inspection Report Ambient Conditions for " + ambientConditions.getFceId() + " failed");
		}
	}

	@Override
	public void modifyFceAmbientConditions(AmbientConditions ambientConditions) throws DAOException {
		checkNull(ambientConditions);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.modifyFceAmbientCondition", false);
		int i = 1;
		connHandler.setTimestamp(i++, ambientConditions.getInspectionDate());
		connHandler.setString(i++, ambientConditions.getArrivalTime());
		connHandler.setString(i++, ambientConditions.getDepartureTime());
		connHandler.setInteger(i++, ambientConditions.getAmbientTemperature());
		connHandler.setInteger(i++, ambientConditions.getWindSpeed());
		connHandler.setString(i++, ambientConditions.getWindDirectionCd());
		connHandler.setString(i++, ambientConditions.getSkyConditionCd());

		connHandler.setInteger(i++, ambientConditions.getFceId());
		connHandler.setInteger(i++, ambientConditions.getInspectionDay());
		
		boolean success = connHandler.update();
		if (!success) {
			throw new DAOException(
					"Update of Inspection Report Ambient Conditions for " + ambientConditions.getFceId() + " failed");
		}
	}
	
	@Override
	public ArrayList<AmbientConditions> retrieveFceAmbientConditions(Integer fceId) throws DAOException {
		checkNull(fceId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFceAmbientConditions", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		
		ArrayList<AmbientConditions> ret = connHandler.retrieveArray(AmbientConditions.class); 

		return ret;
	}
	
	@Override
	public void deleteFceAmbientConditionsByFceId(Integer fceId) throws DAOException {
		checkNull(fceId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceAmbientConditionsByFceId", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
        connHandler.remove();
	}
	
	@Override
	public void updateFceObservationsAndConcerns(FullComplianceEval fce) throws DAOException {
		checkNull(fce);
		checkNull(fce.getId());

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.updateFceObservationsAndConcerns", false);
		int i = 1;
		connHandler.setString(i++, fce.getInspectionConcerns());
		connHandler.setString(i++, fce.getFileReview());
		connHandler.setString(i++, fce.getRegulatoryDiscussion());
		connHandler.setString(i++, fce.getPhysicalInspectionOfPlant());
		connHandler.setString(i++, fce.getAmbientMonitoring());
		connHandler.setString(i++, fce.getOtherInformation());
		connHandler.setInteger(i++, fce.getLastModified() + 1);
		
		connHandler.setInteger(i++, fce.getId());
		connHandler.setInteger(i++, fce.getLastModified());
		
		boolean success = connHandler.update();
		if (!success) {
			throw new DAOException(
					"Update of Inspection Report Observations And Concerns for " + fce.getId() + " failed");
		}
	}

	
	
	@Override
	public SearchDateRange retrieveFcePreservedSearchDateRange(Integer fceId, String snapshotTypeCd) throws DAOException{
		checkNull(fceId);
		checkNull(snapshotTypeCd);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFcePreservedSearchDateRange", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setString(i++, snapshotTypeCd);
		SearchDateRange ret = (SearchDateRange)connHandler.retrieve(SearchDateRange.class);
		return ret;
	}
	
	@Override
	public void deleteFcePreservedSearchDateRange(Integer fceId, String snapshotTypeCd) throws DAOException{
		checkNull(fceId);
		checkNull(snapshotTypeCd);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFcePreservedSearchDateRange", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setString(i++, snapshotTypeCd);
		connHandler.remove();
	}
	
	@Override
	public void addFceSnapshotSearchDateRange(Integer fceId, String snapshotTypeCd, Timestamp startDate, Timestamp endDate) throws DAOException{
		checkNull(fceId);
		checkNull(snapshotTypeCd);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceSnapshotSearchDateRange", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setString(i++, snapshotTypeCd);
		connHandler.setTimestamp(i++, startDate);
		connHandler.setTimestamp(i++, endDate);
		connHandler.update();
	}
	
	
	@Override
	public List<FceApplicationSearchLineItem> retrieveFceApplicationsBySearch(String facilityId, Timestamp startDt, Timestamp endDt) throws DAOException{
		checkNull(facilityId);
		checkNull(startDt);
		
		StringBuffer statementSQL = new StringBuffer(
				loadSQL("CetaSQL.retrieveFceApplicationsBySearch"));

		StringBuffer whereClause = new StringBuffer("");

        whereClause.append(" AND pa.received_date >= ?");
        
        if (endDt != null) {
            whereClause.append(" AND pa.received_date < ?");
        }
        StringBuffer sortBy = new StringBuffer(" ORDER BY pa.application_id");

        statementSQL.append(whereClause.toString() + " " + sortBy.toString());
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        int i = 1;
        connHandler.setString(i++, facilityId);
        
        if (startDt != null) {
            connHandler.setTimestamp(i++, startDt);
        }
        
        if (endDt != null) {
            connHandler.setTimestamp(i++, endDt);
        }

        ArrayList<FceApplicationSearchLineItem> ret = connHandler.retrieveArray(FceApplicationSearchLineItem.class);
        return ret;
	}
	
	@Override
	public List<FceApplicationSearchLineItem> retrieveFceApplicationListPreservedByFceId(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFceApplicationListPreservedByFceId", true);
		connHandler.setInteger(1, fceId);
		ArrayList<FceApplicationSearchLineItem> ret = connHandler.retrieveArray(FceApplicationSearchLineItem.class); 
		return ret;
	}

	@Override
	public void deleteFceApplicationPreservedList(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceApplicationPreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	@Override
	public void addFceApplicationSnapshotList(Integer fceId, Integer applicationId) throws DAOException{
		checkNull(fceId);
		checkNull(applicationId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceApplicationSnapshotList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, applicationId);
		connHandler.update();
	}
	
/*	@Override
	public void addFcePreservedList(Integer fceId, String snapshotTypeCd, Integer id) throws DAOException{
		checkNull(fceId);
		checkNull(snapshotTypeCd);
		checkNull(id);
		ConnectionHandler connHandler;
		if (snapshotTypeCd.equals(FCEPreSnapshotTypeDef.PA)){
			connHandler = new ConnectionHandler("CetaSQL.addFceApplicationsPreserved", false);
		}
		else {//need change query for different snapshotTypeCd
			connHandler = new ConnectionHandler("CetaSQL.addFceApplicationsPreserved", false);
		}
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, id);
		connHandler.update();
		
	}
	 
	@Override
	public void deleteFcePreservedList(Integer fceId, String snapshotTypeCd) throws DAOException{
		checkNull(fceId);
		checkNull(snapshotTypeCd);
		ConnectionHandler connHandler;
		if (snapshotTypeCd.equals(FCEPreSnapshotTypeDef.PA)){
			connHandler = new ConnectionHandler("CetaSQL.deleteFceApplicationsPreserved", false);
		}
		else { // need change query for different snapshotTypeCd
			connHandler = new ConnectionHandler("CetaSQL.deleteFceApplicationsPreserved", false);
		}
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
*/
	@Override
	public void createAssociatedPermitIdRef(Integer fceId, Integer permitId) throws DAOException {
		checkNull(fceId);
		checkNull(permitId);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.createAssociatedPermitIdRef", false);
		int i = 1;
		
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, permitId);
		
		boolean success = connHandler.update();
		if (!success) {
			throw new DAOException(
					"Error creating associated permit for Inspection failed");
		}
	}

	@Override
	public void deleteAssociatedPermitIdRef(Integer fceId, Integer permitId) throws DAOException {
		checkNull(fceId);
		checkNull(permitId);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteAssociatedPermitIdRef", false);
		int i = 1;
		
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, permitId);
		
		boolean success = connHandler.remove();
		if (!success) {
			throw new DAOException(
					"Error: deleting associated permit for Inspection failed");
		}
	}

	@Override
	public List<Integer> retrieveAssociatedPermitIdsByFceId(Integer fceId) throws DAOException {
		checkNull(fceId);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveAssociatedPermitIdsByFceId", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		
		return connHandler.retrieveJavaObjectArray(Integer.class);
	}
	
	@Override
	public void deleteAssociatedPermitIdRefsByFceId(Integer fceId) throws DAOException {
		checkNull(fceId);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteAssociatedPermitIdRefsByFceId", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		
		boolean success = connHandler.remove();
		if (!success) {
			throw new DAOException(
					"Error: deleting associated permits for Inspection failed");
		}
	}

	@Override
	public void deleteAssociatedPermitConditionIdRefByPermitIds(List<Integer> permitIds) throws DAOException {
		checkNull(permitIds);
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL;
		StringBuffer whereClause = new StringBuffer();

		statementSQL = new StringBuffer(loadSQL("CetaSQL.deleteAssociatedPermitConditionIdRefByPermitId"));
		
		whereClause.append(" AND (");
		for (int i = 0; i < permitIds.size(); i++) {
			Integer permitId = permitIds.get(i);
			whereClause.append("pc.PERMIT_ID="+permitId);
			if (i + 1 == permitIds.size()) {
				whereClause.append(") ");
			} else {
				whereClause.append(" OR ");
			}
		}
		
		statementSQL.append(whereClause.toString());
		connHandler.setSQLStringRaw(statementSQL.toString());
		connHandler.remove();
	}

	@Override
	public void associatePermitConditionsByPermitIds(List<Integer> permitIds, Integer fceId) throws DAOException {
		checkNull(permitIds);
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer statementSQL;
		StringBuffer whereClause = new StringBuffer();

		statementSQL = new StringBuffer(loadSQL("CetaSQL.associatePermitConditionIdsByPermitIds"));

		whereClause.append(" AND (");
		for (int i = 0; i < permitIds.size(); i++) {
			Integer permitId = permitIds.get(i);
			whereClause.append("pc.PERMIT_ID=" + permitId);
			if (i + 1 == permitIds.size()) {
				whereClause.append(") ");
			} else {
				whereClause.append(" OR ");
			}
		}

		statementSQL.append(whereClause.toString());
		connHandler.setSQLStringRaw(statementSQL.toString());
		int i = 1;
		connHandler.setInteger(i++, fceId);

		connHandler.update();
	}

	@Override
	public List<FcePermitCondition> retrieveAssociatedPermitConditionsByFceId(Integer fceId) throws DAOException {
		checkNull(fceId);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveAssociatedPermitConditionsByFceId",
				true);
		connHandler.setInteger(1, fceId);
		
		ArrayList<FcePermitCondition> searchResults = connHandler.retrieveArray(FcePermitCondition.class);
		
		for (PermitConditionSearchLineItem aResult : searchResults) {
			aResult.setAssociatedFpEuEpaEmuIds(readOnlyPermitConditionDAO
					.retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(aResult.getPermitConditionId()));
			aResult.setPermitConditionCategoryCds(readOnlyPermitConditionDAO
					.retrievePermitConditionCategoriesByConditionId(aResult.getPermitConditionId()));
			aResult.setSupersededByThis(readOnlyPermitConditionDAO
					.retrievePermitConditionsSupersededByThis(aResult.getPermitConditionId()));
			aResult.setComplianceStatusEventList(
					readOnlyPermitConditionDAO.retrieveComplianceStatusEventList(aResult.getPermitConditionId()));
		}
		
		return searchResults;
	}

	@Override
	public List<PermitConditionSearchLineItem> retrieveExcludedPermitConditionsByFceId(Integer fceId)
			throws DAOException {
		checkNull(fceId);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveExcludedPermitConditionsByFceId", true);
		connHandler.setInteger(1, fceId);
		
		ArrayList<PermitConditionSearchLineItem> searchResults = connHandler
				.retrieveArray(PermitConditionSearchLineItem.class);

		for (PermitConditionSearchLineItem aResult : searchResults) {
			aResult.setAssociatedFpEuEpaEmuIds(readOnlyPermitConditionDAO
					.retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(aResult.getPermitConditionId()));
			aResult.setPermitConditionCategoryCds(readOnlyPermitConditionDAO
					.retrievePermitConditionCategoriesByConditionId(aResult.getPermitConditionId()));
			aResult.setSupersededByThis(readOnlyPermitConditionDAO
					.retrievePermitConditionsSupersededByThis(aResult.getPermitConditionId()));
			aResult.setComplianceStatusEventList(
					readOnlyPermitConditionDAO.retrieveComplianceStatusEventList(aResult.getPermitConditionId()));
		}
		
		return searchResults;
	}
	
	
	@Override
	public void deleteFcePermitPreservedList(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFcePermitPreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	@Override
	public void addFcePermitSnapshotList(Integer fceId, Integer applicationId) throws DAOException{
		checkNull(fceId);
		checkNull(applicationId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFcePermitSnapshotList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, applicationId);
		connHandler.update();
	}
	
	@Override
	public List<Permit> retrieveFcePermitListPreservedByFceId(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ArrayList<Permit> ret = new ArrayList<Permit>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		
		try {
			conn = getConnection();
			pStmt = conn.prepareStatement(loadSQL("CetaSQL.retrieveFcePermitListPreservedByFceId"),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int i = 1;
			
			pStmt.setInt(i++, fceId);
			ResultSet res = pStmt.executeQuery();
			int numberOfHits = 0;
	
			while (res.next()) {
				String type = res.getString("permit_type_cd");
				Permit permit= new Permit();
				permit.setPermitID(res.getInt("permit_id"));
				permit.setLegacyPermitNumber(res.getString("legacy_permit_nbr"));
				permit.setPermitNumber(res.getString("permit_nbr"));
				permit.setPermitType(type);
				permit.setPermitGlobalStatusCD(res.getString("permit_global_status_cd"));
				permit.setPermitLevelStatusCd(res.getString("permit_level_status_cd"));
				permit.setActionType(res.getString("action_type"));
				permit.setPrimaryReasonCD(res.getString("primary_reason_cd"));
				String reasons = res.getString("all_reasons");
				ArrayList<String> allReasons = new ArrayList<String>();
				if (reasons != null && reasons.length() > 0) {
					StringTokenizer str = new StringTokenizer(reasons);
					while (str.hasMoreTokens()) {
						allReasons.add(str.nextToken());
					}
					permit.setPermitReasonCDs(allReasons);
				}
				permit.setExpirationDate(res.getTimestamp("expiration_date"));				
				permit.setFinalIssueDate(res.getTimestamp("final_issuance_date"));				
				
				if (type.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
					permit.setRecissionDate(res.getTimestamp("ptv_recission_date"));
					permit.setPermitBasisDt(res.getTimestamp("permit_basis_date"));
				} else if (type.equalsIgnoreCase(PermitTypeDef.NSR)) {
					permit.setRecissionDate(res.getTimestamp("pnsr_recission_date"));
				}
				permit.setDescription(res.getString("description"));
				
				ret.add(permit);
				numberOfHits++;
				if ((defaultSearchLimit > 0) && (numberOfHits >= defaultSearchLimit)) {
					break;
				}
			}
			res.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			handleException(ex, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return ret;
	}

	@Override
	public void createAssociatedPermitConditionIdRef(Integer fceId, Integer permitConditionId) throws DAOException {
		checkNull(fceId);
		checkNull(permitConditionId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.createAssociatedPermitConditionIdRef", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, permitConditionId);
		
		connHandler.update();
	}

	@Override
	public void deleteAssociatedPermitConditionIdRef(Integer fceId, Integer permitConditionId) throws DAOException {
		checkNull(fceId);
		checkNull(permitConditionId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteAssociatedPermitConditionIdRef", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, permitConditionId);
		
		connHandler.remove();
	}
	
	@Override
	public void modifyAssociatedPermitConditionIdRef(FcePermitCondition pc) throws DAOException {
		checkNull(pc);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.modifyAssociatedPermitConditionIdRef", false);
		int i = 1;
		connHandler.setString(i++, pc.getComplianceStatusCd());
		connHandler.setString(i++, pc.getComments());
		connHandler.setInteger(i++, pc.getLastModified() + 1);
		
		connHandler.setInteger(i++, pc.getFceId());
		connHandler.setInteger(i++, pc.getPermitConditionId());
		connHandler.setInteger(i++, pc.getLastModified());
		connHandler.update();
	}
	
	@Override
	public void deleteAssociatedPermitConditionIdRefsByFceId(Integer fceId) throws DAOException {
		checkNull(fceId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteAssociatedPermitConditionIdRefsByFceId", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	@Override
	public List<FceStackTestSearchLineItem> retrieveFceStackTestsBySearch(String facilityId, Timestamp startDt, Timestamp endDt) throws DAOException{
		checkNull(facilityId);
		checkNull(startDt);
		
		StringBuffer statementSQL = new StringBuffer(loadSQL("CetaSQL.retrieveFceStackTestsBySearch"));
		StringBuffer whereClause = new StringBuffer();
		
		whereClause.append(" AND cst.emission_test_state = 'sbmt'");
		
		if (startDt != null){
			whereClause.append(" AND cst.stack_test_id in (SELECT stack_test_id FROM dbo.ce_stack_test_visit_date_xref WHERE test_date >= ?");
			if (endDt != null){
				whereClause.append(" AND test_date <= ?");
			} 
			whereClause.append(")");
		}
		StringBuffer sortBy = new StringBuffer(" ORDER BY cst.stack_test_id");
		statementSQL.append(whereClause.toString() + sortBy.toString());
		
		ConnectionHandler connHandler = new ConnectionHandler(true);
	    connHandler.setSQLStringRaw(statementSQL.toString());

	    int i = 1;
	    connHandler.setString(i++, facilityId);
	        
	    if (startDt != null) {
	    	connHandler.setTimestamp(i++, startDt);
	    	if (endDt != null) {
	            connHandler.setTimestamp(i++, endDt);
	        }
	    }
	        
	    ArrayList<FceStackTestSearchLineItem> ret = connHandler.retrieveArray(FceStackTestSearchLineItem.class);
	    return ret;
	}

	@Override
	public void deleteFceStackTestPreservedList(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceStackTestPreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	@Override
	public void addFceStackTestSnapshotList(Integer fceId, Integer stackTestId) throws DAOException{
		checkNull(fceId);
		checkNull(stackTestId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceStackTestSnapshotList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, stackTestId);
		connHandler.update();
	}
	
	
	@Override
	public List<FceStackTestSearchLineItem> retrieveFceStackTestListPreservedByFceId(Integer fceId) throws DAOException{
		checkNull(fceId);
	
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFceStackTestListPreservedByFceId", true);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		
		ArrayList<FceStackTestSearchLineItem> ret = connHandler.retrieveArray(FceStackTestSearchLineItem.class);
		return ret;
	}

	@Override
	public List<SiteVisit> retrieveFceSiteVisitListPreservedByFceId(Integer fceId) throws DAOException {
		checkNull(fceId);

		PreparedStatement pStmt = null;
		Connection conn = null;
		SiteVisit visit = null;
		ArrayList<SiteVisit> visitList = new ArrayList<SiteVisit>();

		try {
			conn = getReadOnlyConnection();
			StringBuffer statementSQL;
			statementSQL = new StringBuffer(loadSQL("CetaSQL.retrieveFceSiteVisitListPreservedByFceId"));
			pStmt = conn.prepareStatement(statementSQL.toString());
			pStmt.setInt(1, fceId);

			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) { // prime the loop
				do {
					visit = new SiteVisit();
					visit.populate(rs);
					Integer currentVisitId = visit.getId();

					do {
						Evaluator e = new Evaluator();
						e.populate(rs);
						if (e.getEvaluator() != null) {
							visit.getEvaluators().add(e);
						}
					} while (rs.next() && currentVisitId.equals(AbstractDAO.getInteger(rs, "visit_id")));
					// order the evaluators
					List<Evaluator> orderedEvaluators = orderEvaluators(visit.getEvaluators());
					visit.setEvaluators(orderedEvaluators);
					// if(evaluator == null || include) {
					visitList.add(visit);
					// }
				} while (!rs.isAfterLast());
			}
		} catch (SQLException e) {
			handleException(e, conn);
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return visitList;
	}

	@Override
	public void deleteFceSiteVisitPreservedList(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceSiteVisitPreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	@Override
	public void addFceSiteVisitSnapshotList(Integer fceId, Integer siteVisitId) throws DAOException{
		checkNull(fceId);
		checkNull(siteVisitId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceSiteVisitSnapshotList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, siteVisitId);
		connHandler.update();
	}

	
	//Compliance Report
	
	@Override
	public void deleteFceComplianceReportPreservedList(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceComplianceReportPreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	
	@Override
	public void addFceComplianceReportSnapshotList(Integer fceId, Integer complianceReportId) throws DAOException{
		checkNull(fceId);
		checkNull(complianceReportId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceComplianceReportSnapshotList", false);   
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, complianceReportId);
		connHandler.update();
	}
	
	@Override
	public List<ComplianceReportList> retrieveFceComplianceReportListPreservedByFceId(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFceComplianceReportListPreservedByFceId", true);
		connHandler.setInteger(1, fceId);
		ArrayList<ComplianceReportList> ret = connHandler.retrieveArray(ComplianceReportList.class); 
		return ret;
	}

	@Override
	public List<FceAmbientMonitorLineItem> retrieveFceAmbientMonitorListPreservedByFceId(Integer fceId)
			throws DAOException {
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFceAmbientMonitorListPreservedByFceId", true);
		connHandler.setInteger(1, fceId);
		ArrayList<FceAmbientMonitorLineItem> ret = connHandler.retrieveArray(FceAmbientMonitorLineItem.class); 
		return ret;
	}

	@Override
	public void deleteFceAmbientMonitorPreservedList(Integer fceId) throws DAOException {
		checkNull(fceId);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceAmbientMonitorPreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}

	@Override
	public void addFceAmbientMonitorSnapshotList(Integer fceId, Integer ambientMonitorSiteId) throws DAOException {
		checkNull(fceId);
		checkNull(ambientMonitorSiteId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceAmbientMonitorSnapshotList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, ambientMonitorSiteId);
		connHandler.update();
	}

	//Correspondence
	@Override
	public void deleteFceCorrespondencePreservedList(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceCorrespondencePreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	
	@Override
	public void addFceCorrespondenceSnapshotList(Integer fceId, Integer correspondenceId) throws DAOException{
		checkNull(fceId);
		checkNull(correspondenceId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceCorrespondenceSnapshotList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, correspondenceId);
		connHandler.update();
	}
	
	
	@Override
	public List<Correspondence> retrieveFceCorrespondenceListPreservedByFceId(Integer fceId) throws DAOException{
		checkNull(fceId);
	
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFceCorrespondenceListPreservedByFceId", true);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		
		ArrayList<Correspondence> ret = connHandler.retrieveArray(Correspondence.class);
		return ret;
	}

	//Emissions Inventory
	public List<FceEmissionsInventoryLineItem> retrieveFceEmissionsInventoryBySearch(String facilityId, Timestamp startDt, Timestamp endDt) throws DAOException{

        checkNull(facilityId);
        checkNull(startDt);
        checkNull(endDt);
        
        StringBuffer statementSQL = new StringBuffer(loadSQL("CetaSQL.retrieveFceEmissionsInventoriesBySearch"));

        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL.toString());

        int i = 1;

        connHandler.setString(i++, SQLizeString(formatFacilityId(facilityId).replace("*", "%")));
        connHandler.setTimestamp(i++, startDt);
        connHandler.setTimestamp(i++, endDt);

        ArrayList<FceEmissionsInventoryLineItem> ret = connHandler.retrieveArray(FceEmissionsInventoryLineItem.class);
        return ret;
	}
	
	@Override
	public void deleteFceEmissionsInventoryPreservedList(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceEmissionsInventoryPreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	
	@Override
	public void addFceEmissionsInventorySnapshotList(Integer fceId, Integer emissionsRptId) throws DAOException{
		checkNull(fceId);
		checkNull(emissionsRptId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceEmissionsInventorySnapshotList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, emissionsRptId);
		connHandler.update();
	}
	
	
	@Override
	public List<FceEmissionsInventoryLineItem> retrieveFceEmissionsInventoryListPreservedByFceId(Integer fceId) throws DAOException{
		checkNull(fceId);
	
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFceEmissionsInventoryListPreservedByFceId", true);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		
		ArrayList<FceEmissionsInventoryLineItem> ret = connHandler.retrieveArray(FceEmissionsInventoryLineItem.class);
		return ret;
	}

	@Override
	public HashMap<String, Float> retrieveEmissionsInventoryPollutantTotalEmissions(Integer emissionsRptId) throws DAOException{
		checkNull(emissionsRptId);
		HashMap<String, Float> ret = new HashMap<String, Float>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		
		try {
			conn = getConnection();
			pStmt = conn.prepareStatement(loadSQL("CetaSQL.retrieveEmissionsInventoryPollutantTotalEmissions"),
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			int i = 1;
			
			pStmt.setInt(i++, emissionsRptId);
			ResultSet res = pStmt.executeQuery();
	
			while (res.next()) {
				String pollutantCd = res.getString("pollutant_cd");
				Float totalEmission = res.getFloat("total_emissions");
				ret.put(pollutantCd, totalEmission);
			}
			res.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			handleException(ex, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return ret;
	}
	
	@Override
	public List<FceContinuousMonitorLineItem> retrieveFceContinuousMonitorListPreservedByFceId(Integer fceId)
			throws DAOException {
		
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveFceContinuousMonitorListPreservedByFceId", true);
		connHandler.setInteger(1, fceId);
		ArrayList<FceContinuousMonitorLineItem> ret = connHandler.retrieveArray(FceContinuousMonitorLineItem.class); 
		return ret;
	}
	
	@Override
	public void deleteFceContinuousMonitorLimitPreservedList(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.deleteFceContinuousMonitorLimitPreservedList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.remove();
	}
	
	@Override
	public void addFceContinuousMonitorLimitSnapshotList(Integer fceId, Integer limitId) throws DAOException{
		checkNull(fceId);
		checkNull(limitId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.addFceContinuousMonitorLimitSnapshotList", false);
		int i = 1;
		connHandler.setInteger(i++, fceId);
		connHandler.setInteger(i++, limitId);
		connHandler.update();
	}

	@Override
	public void updateFceReferenceReviewStartDate(FullComplianceEval fce) throws DAOException{
		checkNull(fce);
		checkNull(fce.getId());
		checkNull(fce.getReferenceReviewStartDate());
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.updateFceReferenceReviewStartDate", false);
		int i = 1;
		connHandler.setTimestamp(i++, fce.getReferenceReviewStartDate());
		connHandler.setInteger(i++, fce.getLastModified() + 1);
		connHandler.setInteger(i++, fce.getId());
		connHandler.setInteger(i++, fce.getLastModified());

		boolean success = connHandler.update();
		if (!success) {
			throw new DAOException(
					"Update of Pre-inspection Start Date for " + fce.getId() + " failed");
		}
		fce.setLastModified(fce.getLastModified() + 1);
	}
	
	@Override
	public List<String> retrieveInspectionIdsForApplicationId(Integer appId) throws DAOException {

		checkNull(appId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForApplicationId", true);
		connHandler.setInteger(1, appId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}
	
	@Override
	public List<String> retrieveInspectionIdsForPermitId(Integer permitId) throws DAOException {

		checkNull(permitId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForPermitId", true);
		connHandler.setInteger(1, permitId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}

	@Override
	public List<String> retrieveInspectionIdsForStackTestId(Integer stackTestId) throws DAOException {

		checkNull(stackTestId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForStackTestId", true);
		connHandler.setInteger(1, stackTestId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}

	@Override
	public List<String> retrieveInspectionIdsForComplianceRptId(Integer complianceRptId) throws DAOException {

		checkNull(complianceRptId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForComplianceRptId", true);
		connHandler.setInteger(1, complianceRptId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}

	@Override
	public List<String> retrieveInspectionIdsForCorrespondenceId(Integer correspondenceId) throws DAOException {

		checkNull(correspondenceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForCorrespondenceId", true);
		connHandler.setInteger(1, correspondenceId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}

	@Override
	public List<String> retrieveInspectionIdsForEmissionRptId(Integer emissionsRptId) throws DAOException {

		checkNull(emissionsRptId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForEmissionRptId", true);
		connHandler.setInteger(1, emissionsRptId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}

	@Override
	public List<String> retrieveInspectionIdsForCemComId(Integer limitId) throws DAOException {

		checkNull(limitId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForCemComId", true);
		connHandler.setInteger(1, limitId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}

	@Override
	public Integer getMonitorLimitsIncludedInInspectionReportCount(List<Integer> limitIds) throws DAOException {

		checkNull(limitIds);
		StringBuffer statementSQL = new StringBuffer(loadSQL("CetaSQL.retrieveInspectionIdCountForCemComIds"));
		StringBuffer whereClause = new StringBuffer();
		whereClause.append("AND limit_id IN (");

		for (int i=0; i < limitIds.size(); i++) {
			whereClause.append(limitIds.get(i));
			if(!(i == limitIds.size()-1)){
				whereClause.append(", ");
			}
		}
		whereClause.append(")");
		
		statementSQL.append(whereClause);
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL.toString());

		Integer ret = (Integer) connHandler.retrieveJavaObject(Integer.class);

		return ret;
	}
	
	@Override
	public List<String> retrieveInspectionIdsForAmbientMonSiteId(Integer monitorSiteId) throws DAOException {

		checkNull(monitorSiteId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForAmbientMonSiteId", true);
		connHandler.setInteger(1, monitorSiteId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}

	@Override
	public List<String> retrieveInspectionIdsForSiteVisitId(Integer siteVisitId) throws DAOException {

		checkNull(siteVisitId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForSiteVisitId", true);
		connHandler.setInteger(1, siteVisitId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}
	
	@Override
	public List<ComplianceReportList> retrieveFceComplianceReportBySearch(String facilityId, Timestamp startDt, Timestamp endDt) throws DAOException{
		
		checkNull(facilityId);
		checkNull(startDt);
		String reportStatus = ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED;
		facilityId = formatFacilityId(facilityId);
		
        String statementSQL = loadSQL("ComplianceReportSQL.findComplianceReports");

        StringBuffer whereClause = new StringBuffer();
        whereClause.append(" AND LOWER(cr.facility_id) = LOWER('" + facilityId + "')");
        whereClause.append(" AND cr.report_status = '" + reportStatus + "'");
        
        if (startDt != null) {
            whereClause.append(" AND cr.received_date >= ?");
        }

        if (endDt != null) {
            whereClause.append(" AND cr.received_date < ?");
        }

        StringBuffer sortBy = new StringBuffer(" ORDER BY cr.report_id");
        
        statementSQL += whereClause.toString() + " " + sortBy.toString();
        
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL);
        int i = 1;
        if (startDt != null) {
        	connHandler.setTimestamp(i++, startDt);
        }
        if (endDt != null) {
        	connHandler.setTimestamp(i++, endDt);
        }
        ArrayList<ComplianceReportList> ret = connHandler.retrieveArray(ComplianceReportList.class, defaultSearchLimit);
        return ret;
	}

	@Override
	public Integer retireveFceAttachmentCountByType(Integer fceId, String fce_attachment_type_cd) throws DAOException{
		Integer cnt = 0;		
		checkNull(fceId);
		checkNull(fce_attachment_type_cd);

        int i = 1;
    
        PreparedStatement pStmt = null;
        Connection conn = null;
        try {
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(loadSQL("CetaSQL.retireveFceAttachmentCountByType"));
            pStmt.setInt(i++, fceId);
            pStmt.setString(i++, fce_attachment_type_cd);
            ResultSet rs = pStmt.executeQuery();	
			if (rs.next()) {
				cnt = rs.getInt("cnt");
			}
			rs.close();
        } catch (SQLException e) {
            handleException(e, conn);
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(pStmt);
            handleClosing(conn);
        } 
		return cnt;	
	}

	@Override
	public List<String> retrieveInspectionIdsForLastFceId(Integer fceId) throws DAOException{
		checkNull(fceId);
		
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveInspectionIdsForLastFceId", true);
		connHandler.setInteger(1, fceId);
		ArrayList<String> ret = connHandler.retrieveJavaObjectArray(String.class); 

		return ret;
	}
	
	@Override
	public void clearLastInspIdByFacility(String faciliityId) throws DAOException{
		//facilityId must be in the correct format
		checkNull(faciliityId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.clearLastInspId", false);
		int i = 1;
		connHandler.setString(i++, faciliityId);
		connHandler.remove();
	}
}
