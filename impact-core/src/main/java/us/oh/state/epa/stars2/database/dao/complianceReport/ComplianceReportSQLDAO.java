
package us.oh.state.epa.stars2.database.dao.complianceReport;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ComplianceReportDAO;
import us.oh.state.epa.stars2.database.dao.AbstractDAO.ConnectionHandler;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceDeviation;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.CompliancePerDetail;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportAttachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportCategoryInfo;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportLimit;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportMonitor;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportNote;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.LimitTrendReportLineItem;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.ComplianceReportSearchDateByDef;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.TimestampUtil;

@Repository
public class ComplianceReportSQLDAO extends AbstractDAO implements
        ComplianceReportDAO {  

    public ComplianceReportAttachment modifyComplianceAttachment(ComplianceReport compReport,
    		ComplianceReportAttachment attachment) throws DAOException {
    	ComplianceReportAttachment ret = attachment;

        ConnectionHandler connHandler = null;
     
        connHandler = new ConnectionHandler("ComplianceReportSQL.updateComplianceOtherAttachment", false);
        
        if (attachment.getDocTypeCd() == null) {
            attachment.setDocTypeCd("0");
        }
        
        int i = 1;
        connHandler.setInteger(i++, attachment.getDocumentID());
        connHandler.setString(i++, attachment.getDocTypeCd());
        connHandler.setInteger(i++, attachment.getRefLastModified() + 1);
        connHandler.setInteger(i++, attachment.getDocumentID());
        connHandler.setInteger(i++, attachment.getRefLastModified());
        
        connHandler.update();
        ret.setLastModified(1);

        return ret;
    }

    public ComplianceReport createComplianceReport(ComplianceReport newReport)
            throws DAOException {
        checkNull(newReport);
        ComplianceReport ret = newReport;
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.createComplianceReport", false);
        Integer id = nextSequenceVal("S_CR_Report_Id",newReport.getReportId());
        int i=1;
        connHandler.setInteger(i++, id);
        connHandler.setString(i++, newReport.getReportType());
        connHandler.setInteger(i++, newReport.getUserId());
        connHandler.setString(i++, newReport.getFacilityId());
        connHandler.setTimestamp(i++, newReport.getReceivedDate());
        connHandler.setTimestamp(i++, newReport.getSubmittedDate());
        connHandler.setString(i++, newReport.getComments());
        connHandler.setString(i++, newReport.getReportStatus());
        connHandler.setInteger(i++, null);
        connHandler.setTimestamp(i++, newReport.getDapcDateReviewed());
        connHandler.setString(i++, newReport.getDapcDeviationsReported());
        connHandler.setString(i++, newReport.getComplianceStatusCd());
        connHandler.setString(i++, newReport.getDapcReviewResultsTVCC());
        connHandler.setString(i++, newReport.getDapcReviewComments());
        connHandler.setString(i++, newReport.getDapcAccepted());
        connHandler.setTimestamp(i++, newReport.getPerDueDate());
        connHandler.setString(i++, newReport.getTvccReportingYear());
        connHandler.setString(i++, newReport.getTvccDeviationDeclaration());
        connHandler.setString(i++, newReport.getTvccAfsId());
        connHandler.setTimestamp(i++, newReport.getTvccAfsSentDate());
        connHandler.setTimestamp(i++, newReport.getPerStartDate());
        connHandler.setTimestamp(i++, newReport.getPerEndDate());
        connHandler.setString(i++, newReport.getOtherCategoryCd());
        connHandler.setString(i++, newReport.getPerDueDateCd());
        connHandler.setTimestamp(i++, newReport.getFinalActionDate());
        connHandler.setString(i++, newReport.getPermitNumber());
        connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(newReport.getValidated()));
        connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(newReport.isLegacyFlag()));

		connHandler.setInteger(i++, newReport.getReportYear());
		connHandler.setInteger(i++, newReport.getReportQuarter());
		connHandler.setInteger(i++, newReport.getFpId());
        
        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setReportId(id);
        ret.setLastModified(1);
        return ret;
    }

    public ComplianceDeviation createComplianceDeviation(
            ComplianceDeviation newDeviation) throws DAOException {
        ComplianceDeviation ret = newDeviation;

        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.createComplianceDeviation", false);

        Integer id = nextSequenceVal("S_CR_Deviation_Id");
        connHandler.setInteger(1, id);
        connHandler.setInteger(2, newDeviation.getReportId());
        connHandler.setTimestamp(3, newDeviation.getStartDate());
        connHandler.setTimestamp(4, newDeviation.getEndDate());
        connHandler.setString(5, newDeviation.getIdentifier());
        connHandler.setString(6, newDeviation.getControlPermit());
        connHandler.setString(7, newDeviation.getPerDescription());
        connHandler.setString(8, newDeviation.getPerProbableCause());
        connHandler.setString(9, newDeviation.getPerCorrectiveAction());
        connHandler.setString(10, newDeviation.getTvccComplianceMethod());
        connHandler.setString(11, newDeviation.getTvccExcursionsSubmitted());
        connHandler.setString(12, newDeviation.getTvccExcursionsOther());
        connHandler.setInteger(13, 1);
        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setDeviationId(id);
        ret.setLastModified(1);
        return ret;
    }

    public boolean createComplianceAttachment(ComplianceReport compReport,
    		Attachment newAttachment)
            throws DAOException {
    	Attachment ret = newAttachment;
        ConnectionHandler connHandler = null;
        
        connHandler = new ConnectionHandler("ComplianceReportSQL.createComplianceOtherAttachment", false);
        if (newAttachment.getDocTypeCd() == null) {
            newAttachment.setDocTypeCd("0");
        }if (newAttachment.getDocTypeCd() == null) {
            newAttachment.setDocTypeCd("0");
        }
        connHandler.setInteger(1, newAttachment.getDocumentID());
        connHandler.setInteger(2, compReport.getReportId());
        connHandler.setString(3, newAttachment.getDocTypeCd());
        connHandler.setInteger(4, 1);
        connHandler.update();
        ret.setLastModified(1);
        return true;
    }

    public CompliancePerDetail createCompliancePerDetail(
            ComplianceReport compReport, CompliancePerDetail perDetail)
            throws DAOException {

        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.createCompliancePerDetail", false);
        connHandler.setInteger(1, compReport.getReportId());
        connHandler.setInteger(2, perDetail.getEUId());
        connHandler.setInteger(3, perDetail.getPermitId());
        connHandler.setTimestamp(4, perDetail.getInitialInstallComplete());
        connHandler.setTimestamp(5, perDetail.getModificationBegun());
        connHandler.setTimestamp(6, perDetail.getCommencedOperation());
        connHandler.setString(7, perDetail.getDeviations());
        connHandler.setString(8, perDetail.getDeviationsFromMRR());
        connHandler.setString(9, perDetail.getComment());
        connHandler.setInteger(10, 1);
        connHandler.update();

        return perDetail;
    }

    public void deleteComplianceReport(ComplianceReport complianceReport)
            throws DAOException {

        // PER Details
        /*
         * Get a temp listing of the attachments so we can delete these files if
         * everything else is successful.
         */

        deleteCompliancePerDetail(complianceReport);
        // Deviations
        ComplianceDeviation de[] = complianceReport.getDeviationReports();
        if (de != null) {
            for (int i = 0; i < de.length; i++) {
                deleteComplianceDeviation(de[i]);
            }
        }

        // Report
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.deleteComplianceReport", false);
        connHandler.setInteger(1, complianceReport.getReportId());
        connHandler.remove();

        // if all of the above was successful then we can delete the attachments
    }
    
    public void deleteComplianceReport(ComplianceReport complianceReport,boolean removeFiles)
    throws DAOException {
        
//      PER Details
        /*
         * Get a temp listing of the attachments so we can delete these files if
         * everything else is successful.
         */

        deleteCompliancePerDetail(complianceReport);
//      Deviations
        ComplianceDeviation de[] = complianceReport.getDeviationReports();
        if (de != null) {
            for (int i = 0; i < de.length; i++) {
                deleteComplianceDeviation(de[i]);
            }
        }

//      Report
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.deleteComplianceReport", false);
        connHandler.setInteger(1, complianceReport.getReportId());
        connHandler.remove();

//      if all of the above was successful then we can delete the attachments
    }

    public boolean deleteComplianceDeviation(
            ComplianceDeviation complianceDeviation) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.deleteComplianceDeviation", false);
        connHandler.setInteger(1, complianceDeviation.getDeviationId());
        connHandler.remove();
        return true;
    }

    public boolean deleteCompliancePerDetail(ComplianceReport complianceReport) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.deleteCompliancePerDetail", false);
        connHandler.setInteger(1, complianceReport.getReportId());
        connHandler.remove();
        return true;
    }

    public boolean modifyComplianceDeviation(
            ComplianceDeviation complianceDeviation) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.updateComplianceDeviation", false);
        
        checkNull(complianceDeviation.getIdentifier());
/* 2512 and those two dates are nullable in database.  place a meaningless value 
 * into database for the not nullable fields is not a good DAO process too.  
 * Have identifier check for null to give correct error message to caller. YEHP
 * 
        if (complianceDeviation.getStartDate() == null) {
            complianceDeviation.setStartDate(new Timestamp(System
                    .currentTimeMillis()));
        }
        if (complianceDeviation.getEndDate() == null) {
            complianceDeviation.setEndDate(new Timestamp(System
                    .currentTimeMillis()));
        }
        if (complianceDeviation.getIdentifier() == null) {
            complianceDeviation.setIdentifier("");
        }*/
        connHandler.setTimestamp(1, complianceDeviation.getStartDate());
        connHandler.setTimestamp(2, complianceDeviation.getEndDate());
        connHandler.setString(3, complianceDeviation.getIdentifier());
        connHandler.setString(4, complianceDeviation.getControlPermit());
        connHandler.setString(5, complianceDeviation.getPerDescription());
        connHandler.setString(6, complianceDeviation.getPerProbableCause());
        connHandler.setString(7, complianceDeviation.getPerCorrectiveAction());
        connHandler.setString(8, complianceDeviation.getTvccComplianceMethod());
        connHandler.setString(9, complianceDeviation
                .getTvccExcursionsSubmitted());
        connHandler.setString(10, complianceDeviation.getTvccExcursionsOther());
        connHandler.setInteger(11, 1);
        connHandler.setInteger(12, complianceDeviation.getDeviationId());
        connHandler.update();

        return true;
    }

    public boolean modifyComplianceReport(ComplianceReport complianceReport)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.updateComplianceReport", false);
        boolean ret = false;

        int i = 1;
        connHandler.setString(i++, complianceReport.getComments());
        connHandler.setString(i++, complianceReport.getReportStatus());
        connHandler.setInteger(i++, complianceReport.getDapcReviewer());
        connHandler.setTimestamp(i++, complianceReport.getDapcDateReviewed());
        connHandler.setString(i++, complianceReport.getDapcDeviationsReported());
        connHandler.setString(i++, complianceReport.getComplianceStatusCd());
        connHandler.setString(i++, complianceReport.getDapcReviewResultsTVCC());
        connHandler.setString(i++, complianceReport.getDapcReviewComments());
        connHandler.setString(i++, complianceReport.getDapcAccepted());
        connHandler.setTimestamp(i++, complianceReport.getPerDueDate());
        connHandler.setString(i++, complianceReport.getTvccReportingYear());
        connHandler.setString(i++, complianceReport.getTvccDeviationDeclaration());
        connHandler.setString(i++, complianceReport.getTvccAfsId());
        connHandler.setTimestamp(i++, complianceReport.getTvccAfsSentDate());
        connHandler.setTimestamp(i++, complianceReport.getPerStartDate());
        connHandler.setTimestamp(i++, complianceReport.getPerEndDate());
        connHandler.setTimestamp(i++, complianceReport.getReceivedDate());
        connHandler.setTimestamp(i++, complianceReport.getSubmittedDate());
        connHandler.setString(i++, complianceReport.getOtherCategoryCd());
        connHandler.setString(i++, complianceReport.getPerDueDateCd());
        connHandler.setInteger(i++, complianceReport.getLastModified()+1);
        connHandler.setInteger(i++, complianceReport.getUserId());
        connHandler.setTimestamp(i++, complianceReport.getFinalActionDate());
        connHandler.setString(i++, complianceReport.getPermitNumber());
        connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(complianceReport.getValidated()));
        connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(complianceReport.isLegacyFlag()));

		connHandler.setInteger(i++, complianceReport.getReportYear());
		connHandler.setInteger(i++, complianceReport.getReportQuarter());
		connHandler.setInteger(i++, complianceReport.getFpId());

        connHandler.setInteger(i++, complianceReport.getReportId());
        connHandler.setInteger(i++, complianceReport.getLastModified());
        ret = connHandler.update();

        /*
        // go update each of the PER details (if there are any)
        if (ComplianceReportTypeDef.COMPLIANCE_TYPE_PER.equals(complianceReport.getReportType())) {
            CompliancePerDetail per[] = complianceReport.getPerDetails();
            for (int j = 0; j < per.length; j++) {
                modifyCompliancePerDetail(complianceReport.getReportId(),
                        per[j]);
            }
        }
        */
        return ret;
    }

    private void modifyCompliancePerDetail(int reportId, CompliancePerDetail per)
            throws DAOException {
        /*
         * 1. PER_DETAIL_COMMENT=?, 2. OPERATING=?, 3. PHYSICAL_CHANGE=?, 4.
         * DEVIATIONS=?, 5. MRR_DEVIATIONS=? 6. AIR_TOXICS_CHANGE, 7
         * LAST_MODIFIED=? 8. WHERE REPORT_ID=? 9 CORR_EPA_EMU_ID=?
         * updateCompliancePerDetail
         */
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.updateCompliancePerDetail", true);

        int i = 1;
        connHandler.setString(i++, per.getComment());
        connHandler.setString(i++, per.getOperate());
        connHandler.setString(i++, per.getPhysicalChange());
        connHandler.setString(i++, per.getDeviations());
        connHandler.setString(i++, per.getDeviationsFromMRR());
        connHandler.setString(i++, per.getAirToxicsChange());
        connHandler.setTimestamp(i++, per.getInitialInstallComplete());
        connHandler.setTimestamp(i++, per.getModificationBegun());
        connHandler.setTimestamp(i++, per.getCommencedOperation());
        connHandler.setInteger(i++, per.getLastModified()+1);
        connHandler.setInteger(i++, reportId);
        connHandler.setInteger(i++, per.getEUId());
        connHandler.setInteger(i++, per.getLastModified());
        connHandler.update();
    }

    public ComplianceReport retrieveComplianceReport(int reportId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.retrieveReportById", true);
        connHandler.setInteger(1, reportId);
        ComplianceReport cr = (ComplianceReport) connHandler
                .retrieve(ComplianceReport.class);

        return cr;
    }

    public ComplianceReportList[] searchComplianceReport(String reportCRPTId, String facilityId,
            String facilityName, String doLaaCd, String reportType, String reportStatus,
            String reportYear, String deviationsReported, String dateBy,
            Date dateBegin, Date dateEnd, String reportAccepted,
            String otherTypeCd, String cmpId, String permitClassCd, String facilityTypeCd, boolean unlimitedResults, String dapcReviewComments)
            	throws DAOException {

        String statementSQL = loadSQL("ComplianceReportSQL.findComplianceReports");

        StringBuffer whereClause = new StringBuffer("");
        
        if (reportCRPTId != null && reportCRPTId.trim().length() > 0) {
        	reportCRPTId = formatId("CRPT",reportCRPTId);
        	whereClause.append(" AND LOWER(cr.crpt_id) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(reportCRPTId.replace("*", "%")));
            whereClause.append("')");
        }
        
        if (facilityId != null && facilityId.trim().length() > 0) {
        	facilityId = formatFacilityId(facilityId);
            whereClause.append(" AND LOWER(cr.facility_id) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(facilityId.replace("*", "%")));
            whereClause.append("')");
        }

        if (facilityName != null && facilityName.trim().length() > 0) {
            whereClause.append(" AND LOWER(ff.FACILITY_NM) LIKE ");
            whereClause.append("LOWER('");
            whereClause.append(SQLizeString(facilityName.replace("*", "%")));
            whereClause.append("')");
        }
        
        if (doLaaCd != null) {
            whereClause.append(" AND ff.do_laa_cd = '");
            whereClause.append(doLaaCd);
            whereClause.append("'");
        }

        if (reportType != null) {
            whereClause.append(" AND cr.report_type_cd = '");
            whereClause.append(reportType);
            whereClause.append("'");
        }

        if (reportType != null && (ComplianceReportTypeDef.anyOtherType(reportType))) {
            if (otherTypeCd != null && otherTypeCd.length() > 0) {
                whereClause.append(" AND cr.other_type_cd = '");
                whereClause.append(otherTypeCd);
                whereClause.append("'");
            }
        }

        if (reportStatus != null && reportStatus.trim().length() > 0) {
            whereClause.append(" AND cr.report_status = '");
            whereClause.append(reportStatus);
            whereClause.append("'");
        }

        if (reportAccepted != null && reportAccepted.trim().length() > 0) {
            whereClause.append(" AND cr.dapc_accepted = '");
            whereClause.append(reportAccepted);
            whereClause.append("'");
        }
        
        if(dapcReviewComments != null && dapcReviewComments.trim().length() > 0) {
        	whereClause.append(" AND LOWER(cr.dapc_comments) LIKE ");
        	whereClause.append("LOWER('");
        	whereClause.append(SQLizeString(dapcReviewComments.replace("*", "%")));
        	whereClause.append("')");
        }

        if (cmpId != null && cmpId.trim().length() > 0) { 
			whereClause.append(" AND ccm.cmp_id = '");
			whereClause.append(cmpId);
			whereClause.append("'");
        }
        
        if (permitClassCd != null) {
        	whereClause.append(" AND ff.permit_classification_cd = '");
        	whereClause.append(permitClassCd);
        	whereClause.append("'");
		}
        
        if (facilityTypeCd != null) {
        	whereClause.append(" AND ff.facility_type_cd = '");
        	whereClause.append(facilityTypeCd);
        	whereClause.append("'");
		}
        
        /*
        if (reportYear != null && reportYear.trim().length() > 0) {
            if (ComplianceReportTypeDef.COMPLIANCE_TYPE_TVCC.equals(reportType)) {
                whereClause.append(" AND TVCC_REPORTING_YEAR = " + reportYear);
            } else if (ComplianceReportTypeDef.COMPLIANCE_TYPE_PER.equals(reportType)) {
                whereClause
                        .append(" AND EXTRACT(YEAR FROM CR.PER_START_DATE) = "
                                + reportYear);
            }
        }
        */

        if (dateBy != null) {
            if (dateBy
                    .equals(ComplianceReportSearchDateByDef.COMPLIANCE_REPORT_RECEIVED_DT)) {
                if (dateBegin != null) {
                    whereClause.append(" AND CR.RECEIVED_DATE >= convert(dateTime2(6),'"
                            + dateBegin + " 0:0:0')");
                }

                if (dateEnd != null) {
                    whereClause.append(" AND CR.RECEIVED_DATE <= convert(dateTime2(6),'"
                            + dateEnd + " 23:59:59')");
                }
            } else if (dateBy
                    .equals(ComplianceReportSearchDateByDef.COMPLIANCE_REPORT_REVIEWED_DT)) {
                if (dateBegin != null) {
                    whereClause
                            .append(" AND CR.DAPC_REVIEWED_DATE >= convert(dateTime2(6),'"
                                    + dateBegin + " 0:0:0')");
                }

                if (dateEnd != null) {
                    whereClause
                            .append(" AND CR.DAPC_REVIEWED_DATE <= convert(dateTime2(6),'"
                                    + dateEnd + " 23:59:59')");
                }
            }
        }

        StringBuffer sortBy = new StringBuffer(
                " ORDER BY CR.SUBMITTED_DATE DESC,cr.facility_id");
        statementSQL += whereClause.toString() + " " + sortBy.toString();
        
        //System.out.println(statementSQL);
        ConnectionHandler connHandler = new ConnectionHandler(true);

        connHandler.setSQLStringRaw(statementSQL);

        ArrayList<ComplianceReportList> ret = connHandler
                .retrieveArray(ComplianceReportList.class, defaultSearchLimit);

        return ret.toArray(new ComplianceReportList[0]);

    }

    public ComplianceReportList[] searchComplianceReportByFacility(
            String facilityID) throws DAOException {
    	
    	checkNull(facilityID);
    	
    	String statementSQL = loadSQL("ComplianceReportSQL.retrieveReportsByFacility");
    	
    	String orderByClause = "ORDER BY cr.submitted_date DESC,cr.facility_id";
    	
    	StringBuffer whereClause = new StringBuffer();
    	
    	// additional where clauses for IMPACT public
    	if(CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
    		// retrieve only if the report is submitted
    		whereClause.append(" AND cr.report_status = '");
    		whereClause.append(ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED);
    		whereClause.append("'");
    		
    		// retrieve only if the report reviewed date is before today
    		whereClause.append(" AND (cr.dapc_reviewed_date is not null");
    		whereClause.append(" AND CONVERT(date, cr.dapc_reviewed_date) < CONVERT(date, GETDATE())) ");
    	}
    	
    	statementSQL += whereClause + orderByClause;
    	
    	ConnectionHandler connHandler = new ConnectionHandler(true);
    	connHandler.setSQLStringRaw(statementSQL);
    	connHandler.setString(1, facilityID);

    	List<ComplianceReportList> ret = connHandler.retrieveArray(ComplianceReportList.class, defaultSearchLimit);
		
    	return ret.toArray(new ComplianceReportList[0]);
    }

    public int retrievePerReportCount(String facilityID, String reportStatus,
            Timestamp startDate, Timestamp endDate) throws DAOException {
        startDate = TimestampUtil.setToEndofPreviousDate(startDate);
        endDate = TimestampUtil.setToStartofNextDate(endDate);
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.retrievePerReportCount", true);
        connHandler.setString(1, facilityID);
        connHandler.setString(2, reportStatus);
        connHandler.setTimestamp(3, startDate);
        connHandler.setTimestamp(4, endDate);
        return ((Integer) connHandler
                .retrieveJavaObject(java.lang.Integer.class)).intValue();
    }

    public ComplianceReportAttachment[] retrieveAttachments(String reportType,
            String facilityID, int reportID) throws DAOException {
        ConnectionHandler connHandler = null; 
        
        // if (ComplianceReportTypeDef.COMPLIANCE_TYPE_PER.equals(reportType)) {
        //    connHandler = new ConnectionHandler(
        //             "ComplianceReportSQL.retrievePERAttachments", true);
        // } else if (ComplianceReportTypeDef.COMPLIANCE_TYPE_TVCC.equals(reportType)) {
        //    connHandler = new ConnectionHandler(
        //            "ComplianceReportSQL.retrieveTVCCAttachments", true);
        // } else {
            connHandler = new ConnectionHandler(
                    "ComplianceReportSQL.retrieveOtherAttachments", true);
        // }
        ComplianceReportAttachment[] result = null;
        if (connHandler != null) {
            connHandler.setString(1, facilityID);
            connHandler.setInteger(2, reportID);
            ArrayList<ComplianceReportAttachment> ret = connHandler.retrieveArray(ComplianceReportAttachment.class);
            result = ret.toArray(new ComplianceReportAttachment[0]);
        }
        return result;
    }
    
    public void retrieveCRTradeSecretAttachmentInfo(ComplianceReportAttachment attachment)
        throws DAOException {
        checkNull(attachment);
        ResultSet resultSet = null;

        Connection conn = null;
        PreparedStatement psSelect = null;        
        try {
            conn = getConnection();
            psSelect = conn.prepareStatement(loadSQL("ComplianceReportSQL.retrieveCRTradeSecretAttachmentInfo"));
            psSelect.setInt(1, attachment.getReportId());
            psSelect.setInt(2, attachment.getDocumentID());
            resultSet = psSelect.executeQuery();

            while (resultSet.next()) {
                attachment.setTradeSecretDocId(resultSet.getInt("document_id"));
                attachment.setTradeSecretJustification(resultSet.getString("trade_secret_reason"));
            }
            resultSet.close();
        } catch (Exception e) {
            handleException(e, conn);
        } finally {
            closeStatement(psSelect);
            handleClosing(conn);
        }
    }
    
    public void createCRTradeSecretAttachment(ComplianceReportAttachment attachment) throws DAOException {

        checkNull(attachment);

        ConnectionHandler connHandler 
            = new ConnectionHandler("ComplianceReportSQL.createCRTradeSecretAttachment", false);

        int i = 1;
        connHandler.setInteger(i++, attachment.getTradeSecretDocId());
        connHandler.setInteger(i++, attachment.getReportId());
        connHandler.setInteger(i++, attachment.getDocumentID());
        connHandler.setString(i++, attachment.getTradeSecretJustification());

        connHandler.update();
    }
    
    public void modifyCRTradeSecretAttachment(ComplianceReportAttachment attachment) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.modifyCRTradeSecretAttachment", true);
        int i = 1;
        connHandler.setString(i++, attachment.getTradeSecretJustification());
        connHandler.setInteger(i++, attachment.getTradeSecretDocId());
        connHandler.update();
    }
    
    public void deleteCRTradeSecretAttachment(ComplianceReportAttachment attachment) throws DAOException {
        checkNull(attachment);
        checkNull(attachment.getTradeSecretDocId());
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.deleteCRTradeSecretAttachment", true);
        connHandler.setInteger(1, attachment.getTradeSecretDocId());
        connHandler.remove();
    }

    public CompliancePerDetail[] retrievePerDetails(int reportId)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.retrieveCompliancePerDetails", true);
        connHandler.setInteger(1, reportId);
        ArrayList<CompliancePerDetail> ret = connHandler
                .retrieveArray(CompliancePerDetail.class);
        return ret.toArray(new CompliancePerDetail[0]);
    }

    public ComplianceDeviation[] retrieveDeviations(int reportID)
            throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.retrieveComplianceDeviations", true);
        connHandler.setInteger(1, reportID);
        ArrayList<ComplianceDeviation> ret = connHandler
                .retrieveArray(ComplianceDeviation.class);
        return ret.toArray(new ComplianceDeviation[0]);
    }

    public boolean deleteComplianceAttachment(
            ComplianceReport complianceReport, ComplianceReportAttachment attachment)
    throws DAOException {
        ConnectionHandler connHandler = null;
    
        connHandler = new ConnectionHandler("ComplianceReportSQL.deleteComplianceOtherAttachment", false);
        if (attachment.getTradeSecretDocId() != null) {
            deleteCRTradeSecretAttachment(attachment);
        }

        boolean ret = false;
        if (connHandler != null) {
            connHandler.setInteger(1, attachment.getDocumentID());
            connHandler.setInteger(2, complianceReport.getReportId());
            connHandler.remove();
            ret = true;
        }
        return ret;
    }
    
    public List<ComplianceReportList> newAfsTvCc() throws DAOException {
        String statementSQL = loadSQL("CetaSQL.newAfsTvCc");
        ConnectionHandler connHandler = new ConnectionHandler(true);
        connHandler.setSQLStringRaw(statementSQL);
        ArrayList<ComplianceReportList> ret = connHandler.retrieveArray(ComplianceReportList.class);
        return ret;
    }

    public boolean afsLockTvCc(ComplianceReportList crl, Integer afsId) throws DAOException {
        checkNull(crl);
        checkNull(afsId);
        ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.afsLockTvCc", false);
        int i=1;
        connHandler.setString(i++,  convertAfsIdToString(afsId));
        connHandler.setInteger(i++, crl.getLastModified() + 1);
        connHandler.setInteger(i++, crl.getReportId());
        connHandler.setInteger(i++, crl.getLastModified());
        return connHandler.update();
    }
    
    public String tvCcAfsLocked(Integer reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.tvCcAfsLockedId", true);
        connHandler.setInteger(1, reportId);
        ArrayList<? extends Object> afsIds = connHandler
                .retrieveJavaObjectArray(String.class);
        if(afsIds.size() != 1) return null;
        if(afsIds.get(0) != null) return (String)afsIds.get(0);
        return null;
    }
    
    public List<ComplianceReportList> retrieveTvCcByAfsId(String scscId, String afsId)
        throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.retrieveTvCcByAfsId", true);
        connHandler.setString(1, scscId);
        connHandler.setString(2, afsId);
        ArrayList<ComplianceReportList> ret = connHandler.retrieveArray(ComplianceReportList.class);
        return ret;
    }
    
    public boolean afsSetDateTvCc(ComplianceReportList tvCc)
    throws DAOException {
        checkNull(tvCc);
        ConnectionHandler connHandler = new ConnectionHandler(
                "ComplianceReportSQL.afsSetDateTvCc", false);
        int i=1;
        connHandler.setTimestamp(i++, tvCc.getTvccAfsSentDate());
        connHandler.setInteger(i++, tvCc.getLastModified() + 1);
        connHandler.setString(i++, tvCc.getReportId());
        connHandler.setInteger(i++, tvCc.getLastModified());
        return connHandler.update();
    }
    
    /**
  	 * @see us.oh.state.epa.stars2.database.dao.ComplianceReportDAO#retrieveNotes(int)
  	 */
  	public final Note[] retrieveNotes(int reportId) throws DAOException {
  		ConnectionHandler connHandler = new ConnectionHandler(
  				"ComplianceReportSQL.retrieveNotes", true);
  		connHandler.setInteger(1, reportId);
  		ArrayList<ComplianceReportNote> ret = connHandler.retrieveArray(ComplianceReportNote.class);
  		return ret.toArray(new Note[0]);
  	}
  	
  	/**
  	 * @see us.oh.state.epa.stars2.database.dao.ComplianceReportDAO#createNote(java.lang.Integer, java.lang.Integer)
  	 */
  	public final void createNote(Integer reportId, Integer noteId)
  			throws DAOException {
  		ConnectionHandler connHandler = new ConnectionHandler(
  				"ComplianceReportSQL.createNote", false);
  		checkNull(reportId);
  		checkNull(noteId);
  		connHandler.setInteger(1, reportId);
  		connHandler.setInteger(2, noteId);
  		connHandler.update();
  	}    
  	
  	/**
  	 * @see us.oh.state.epa.stars2.database.dao.ComplianceReportDAO#createNote(java.lang.Integer, java.lang.Integer)
  	 */
  	public final boolean removeNote(Integer reportId, Integer noteId)
  			throws DAOException {
  		boolean ret = false;
  		
  		checkNull(reportId);
  		checkNull(noteId);
  		
  		ConnectionHandler connHandler = new ConnectionHandler(
  				"ComplianceReportSQL.removeNote", false);
  		if (connHandler != null) {
  			connHandler.setInteger(1, reportId);
  			connHandler.setInteger(2, noteId);
  			ret = connHandler.remove();
  		}
  		
  		return ret;
  	}    
  	
  	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ComplianceReportDAO#
	 * setComplianceReportValidatedFlag(java.lang.Integer, boolean)
	 */
	public final void setComplianceReportValidatedFlag(Integer reportId,
			boolean validated) throws DAOException {
		checkNull(reportId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.setComplianceReportValidatedFlag", false);
		connHandler.setString(1,
				AbstractDAO.translateBooleanToIndicator(validated));
		connHandler.setInteger(2, reportId);
		connHandler.update();
	}
	
	public final ComplianceReportCategoryInfo[] retrieveComplianceReportCategoryInfo()
			throws DAOException {
		List<ComplianceReportCategoryInfo> crptInfos = new ArrayList<ComplianceReportCategoryInfo>();
		ConnectionHandler connHandler = new ConnectionHandler("ComplianceReportSQL.retrieveComplianceReportCategoryInfo", false);
		
		crptInfos = connHandler.retrieveArray(ComplianceReportCategoryInfo.class);
		
		return crptInfos.toArray(new ComplianceReportCategoryInfo[0]);
	}
	
	// Compliance Report Limits

	public final List<ComplianceReportLimit> retrieveComplianceReportLimitListByFpId(
			Integer fpId) throws DAOException {

		checkNull(fpId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.retrieveComplianceReportLimitListByFpId",
				true);

		connHandler.setInteger(1, fpId);

		ArrayList<ComplianceReportLimit> ret = new ArrayList<ComplianceReportLimit>();
		ArrayList<ComplianceReportLimit> base = connHandler
				.retrieveArray(ComplianceReportLimit.class);
		for (BaseDBObject bd : base) {
			ret.add((ComplianceReportLimit) bd);
		}

		return ret;
	}

	public final List<ComplianceReportLimit> retrieveComplianceReportLimitListByMonitorId(
			Integer monitorId) throws DAOException {

		checkNull(monitorId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.retrieveComplianceReportLimitListByMonitorId",
				true);

		connHandler.setInteger(1, monitorId);

		ArrayList<ComplianceReportLimit> ret = new ArrayList<ComplianceReportLimit>();
		ArrayList<ComplianceReportLimit> base = connHandler
				.retrieveArray(ComplianceReportLimit.class);
		for (BaseDBObject bd : base) {
			ret.add((ComplianceReportLimit) bd);
		}

		return ret;
	}

	public final void removeComplianceReportLimitList(int monitorId)
			throws DAOException {

		checkNull(monitorId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.removeComplianceReportLimitList", false);

		connHandler.setInteger(1, monitorId);
		connHandler.remove();
	}

	public final ComplianceReportLimit createComplianceReportLimit(
			ComplianceReportLimit crl) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.createComplianceReportLimit", false);

		checkNull(crl);
		int i = 1;

		int id = nextSequenceVal("S_Compliance_Report_Limit_Id", crl.getCrLimitId());

		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, crl.getLimitId());
		connHandler.setInteger(i++, crl.getCrMonitorId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(crl.isIncludedFlag()));
		connHandler.setString(i++, crl.getLimitStatus());

		connHandler.update();

		crl.setCrLimitId(id);
		crl.setLastModified(1);

		return crl;
	}

	public final void modifyComplianceReportLimit(ComplianceReportLimit crl)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.modifyComplianceReportLimit", false);

		checkNull(crl);
		int i = 1;
		connHandler.setInteger(i++, crl.getLimitId());
		connHandler.setInteger(i++, crl.getCrMonitorId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(crl.isIncludedFlag()));
		
		connHandler.setString(i++, crl.getLimitStatus());

		connHandler.setInteger(i++, crl.getLastModified() + 1);

		connHandler.setInteger(i++, crl.getCrLimitId());
		connHandler.setInteger(i++, crl.getLastModified());

		connHandler.update();
	}

	public final void removeComplianceReportLimit(ComplianceReportLimit crl)
			throws DAOException {

		checkNull(crl);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.removeComplianceReportLimit", false);

		int i = 1;
		connHandler.setInteger(i++, crl.getCrLimitId());

		connHandler.remove();
	}

	public final List<ComplianceReportMonitor> retrieveFacilityComplianceReportMonitorList(
			Integer fpId) throws DAOException {

		checkNull(fpId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.retrieveComplianceReportMonitorByFpId",
				true);

		connHandler.setInteger(1, fpId);

		ArrayList<ComplianceReportMonitor> ret = new ArrayList<ComplianceReportMonitor>();
		ArrayList<ComplianceReportMonitor> base = connHandler
				.retrieveArray(ComplianceReportMonitor.class);
		for (BaseDBObject bd : base) {
			ret.add((ComplianceReportMonitor) bd);
		}

		return ret;
	}

	@Override
	public ComplianceReportLimit retrieveComplianceReportLimitByMonitorIdAndCorrId(
			Integer monitorId, Integer corrLimitId) throws DAOException {
		checkNull(monitorId);
		checkNull(corrLimitId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.retrieveComplianceReportLimitByMonitorIdAndCorrId",
				true);

		connHandler.setInteger(1, monitorId);
		connHandler.setInteger(2, corrLimitId);

		return (ComplianceReportLimit) connHandler
				.retrieve(ComplianceReportLimit.class);

	}

	public ComplianceReportMonitor createComplianceReportMonitor(
			ComplianceReportMonitor crMonitor) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.createComplianceReportMonitor", false);

		checkNull(crMonitor);
		int i = 1;

		int id = nextSequenceVal("S_Compliance_Report_Monitor_Id", crMonitor.getCrMonitorId());

		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, crMonitor.getReportId());
		connHandler.setInteger(i++, crMonitor.getContinuousMonitorId());
		connHandler.setString(i++, crMonitor.getAuditStatus());
		connHandler.setTimestamp(i++, crMonitor.getTestDate());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(crMonitor.isCertificationFlag()));

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		crMonitor.setCrMonitorId(id);
		crMonitor.setLastModified(1);

		return crMonitor;
	}

	public void deleteComplianceReportMonitor(ComplianceReportMonitor crm)
			throws DAOException {

		checkNull(crm);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.deleteComplianceReportMonitor", false);

		int i = 1;

		connHandler.setInteger(i++, crm.getCrMonitorId());

		connHandler.remove();

		return;
	}

	public boolean modifyComplianceReportMonitor(
			ComplianceReportMonitor crMonitor) throws DAOException {
		checkNull(crMonitor);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.modifyComplianceReportMonitor", false);

		int i = 1;
		connHandler.setInteger(i++, crMonitor.getReportId());
		connHandler.setInteger(i++, crMonitor.getContinuousMonitorId());
		connHandler.setString(i++, crMonitor.getAuditStatus());
		connHandler.setTimestamp(i++, crMonitor.getTestDate());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(crMonitor.isCertificationFlag()));
		connHandler.setInteger(i++, crMonitor.getLastModified() + 1);

		connHandler.setInteger(i++, crMonitor.getCrMonitorId());
		connHandler.setInteger(i++, crMonitor.getLastModified());

		return connHandler.update();
	}

	public ComplianceReportMonitor retrieveComplianceReportMonitor(
			Integer crMonitorId) throws DAOException {
		ComplianceReportMonitor ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ComplianceReportSQL.retrieveComplianceReportMonitor"));

			pStmt.setInt(1, crMonitorId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = new ComplianceReportMonitor();
				ret.populate(rs);
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

	public final ComplianceReportMonitor[] retrieveComplianceReportMonitorListByReportId(
			Integer reportId) throws DAOException {

		checkNull(reportId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.retrieveComplianceReportMonitorListByReportId",
				true);

		connHandler.setInteger(1, reportId);

		ArrayList<ComplianceReportMonitor> ret = new ArrayList<ComplianceReportMonitor>();
		ArrayList<ComplianceReportMonitor> base = connHandler
				.retrieveArray(ComplianceReportMonitor.class);
		for (BaseDBObject bd : base) {
			ret.add((ComplianceReportMonitor) bd);
		}

		ComplianceReportMonitor[] result = null;
		result = ret.toArray(new ComplianceReportMonitor[0]);

		return result;
	}

	public final ComplianceReportLimit[] retrieveComplianceReportLimitListByReportId(
			Integer reportId) throws DAOException {

		checkNull(reportId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.retrieveComplianceReportLimitListByReportId",
				true);

		connHandler.setInteger(1, reportId);

		ArrayList<ComplianceReportLimit> ret = new ArrayList<ComplianceReportLimit>();
		ArrayList<ComplianceReportLimit> base = connHandler
				.retrieveArray(ComplianceReportLimit.class);
		for (BaseDBObject bd : base) {
			ret.add((ComplianceReportLimit) bd);
		}

		ComplianceReportLimit[] result = null;
		result = ret.toArray(new ComplianceReportLimit[0]);

		return result;
	}
	
	public ArrayList<LimitTrendReportLineItem> retrievePeriodicLimitTrendData(
			Integer corrLimitId, String facilityId) throws DAOException {
		StringBuffer whereClause = new StringBuffer();
		whereClause.append("AND cr.OTHER_TYPE_CD = '24'");
		return retrieveLimitTrendData(corrLimitId, facilityId, whereClause);
	}
	
	public ArrayList<LimitTrendReportLineItem> retrieveLimitTrendData(
				Integer corrLimitId, String facilityId, StringBuffer whereClause) 
						throws DAOException {
		String sqlStatement = loadSQL("ComplianceReportSQL.retrieveLimitTrendData");

		if (null != corrLimitId) {
			whereClause.append(" AND lim.CORR_LIMIT_ID = " + corrLimitId);
		}

		if (!Utility.isNullOrEmpty(facilityId)) {
			whereClause.append(" AND fp.FACILITY_ID = '"
					+ SQLizeString(facilityId).trim() + "'");
		}

		if (whereClause.length() > 0) {
			sqlStatement += whereClause;
		}
		
		String orderBy = " ORDER BY cr.REPORT_YR DESC, cr.REPORT_QTR DESC, cr.CRPT_ID DESC";
		if (!Utility.isNullOrEmpty(facilityId)) {
			// use a different order by clause for overall facility trend report
			orderBy = " ORDER BY cm.MON_ID DESC, lim.LIM_ID DESC, cr.REPORT_YR DESC, cr.REPORT_QTR DESC, cr.CRPT_ID DESC";
		}

		sqlStatement += orderBy;

		ConnectionHandler connectionHandler = new ConnectionHandler(true);
		connectionHandler.setSQLStringRaw(sqlStatement);

		return connectionHandler.retrieveArray(LimitTrendReportLineItem.class);
	}

	@Override
	public ArrayList<LimitTrendReportLineItem> retrieveLimitTrendData(
			Integer corrLimitId, String facilityId) throws DAOException {
		return retrieveLimitTrendData(corrLimitId, facilityId, new StringBuffer());
	}
	
	@Override
	public boolean setActiveComplianceReportsValidatedFlag(Integer fpId,
			boolean validatedFlag) throws DAOException {
		checkNull(fpId);

		ConnectionHandler conn = new ConnectionHandler(
				"ComplianceReportSQL.setActiveComplianceReportsValidatedFlag",
				true);

		conn.setString(1,
				AbstractDAO.translateBooleanToIndicator(validatedFlag));
		conn.setInteger(2, fpId);

		return conn.updateNoCheck();
	}

	@Override
	public ComplianceReportAttachment retrieveCRTradeSecretAttachmentInfoById(Integer tradeSecretDocId)
			throws DAOException {
		checkNull(tradeSecretDocId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ComplianceReportSQL.retrieveCRTradeSecretAttachmentInfoById", true);

		connHandler.setInteger(1, tradeSecretDocId);

		return (ComplianceReportAttachment) connHandler.retrieve(ComplianceReportAttachment.class);
	}
	
	
}
