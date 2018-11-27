package us.oh.state.epa.stars2.database.dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceDeviation;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.CompliancePerDetail;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportAttachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportCategoryInfo;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportLimit;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportMonitor;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.LimitTrendReportLineItem;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface ComplianceReportDAO extends TransactableDAO {
    /**
     * @param facilityID
     * @return
     * @throws DAOException
     */
    ComplianceReportList[] searchComplianceReportByFacility(String facilityID)
            throws DAOException;
    
    /**
     * @param complianceReport
     * @return
     * @throws DAOException
     */
    ComplianceReport createComplianceReport(ComplianceReport complianceReport)
            throws DAOException;

    /**
     * @param facilityId
     * @param reportStatus
     * @param startDate
     * @param endDate
     * @return
     * @throws DAOException
     */
    int retrievePerReportCount(String facilityID,String reportStatus,
            Timestamp startDate,Timestamp endDate) throws DAOException;
    
    /**
     * @param complianceReport
     * @param compliancePerDetail
     * @return
     * @throws DAOException
     */
    CompliancePerDetail createCompliancePerDetail(ComplianceReport complianceReport,CompliancePerDetail perDetail)
            throws DAOException;
    
    /**
     * @param complianceReport
     * @param attachment
     * @return
     * @throws DAOException
     */
    boolean createComplianceAttachment(ComplianceReport complianceReport,Attachment attachment)
            throws DAOException;
    
    /**
     * @param complianceReport
     * @param attachment
     * @return
     * @throws DAOException
     */
    boolean deleteComplianceAttachment(ComplianceReport complianceReport,ComplianceReportAttachment attachment)
            throws DAOException;
    
    /**
     * @param complianceDeviation
     * @return
     * @throws DAOException
     */
    ComplianceDeviation createComplianceDeviation(ComplianceDeviation complianceDeviation)
            throws DAOException;


    /**
     * @param complianceReport
     * @throws DAOException
     */
    void deleteComplianceReport(ComplianceReport complianceReport)
            throws DAOException;
    
    /**
     * @param complianceReport
     * @throws DAOException
     */
    void deleteComplianceReport(ComplianceReport complianceReport,boolean removeFiles)
            throws DAOException;
    
    /**
     * @param complianceDeviation
     * @throws DAOException
     */
    boolean deleteComplianceDeviation(ComplianceDeviation complianceDeviation)
            throws DAOException;

    /**
     * @param reportId 
     * @param complianceReport
     * @throws DAOException
     * 
     */
    ComplianceReportList[] searchComplianceReport(String reportCRPTId, String facilityID,
            String facililtyName, String doLaaCd, String reportType, String reportStatus, 
            String reportPeriod, String deviationsReported, String dateBy, Date dtBegin, 
            Date dtEnd, String reportAccepted, String otherTypeCd, String cmpId, String permitClassCd, String facilityTypeCd,
            boolean unlimitedResults, String dapcReviewComments) throws DAOException;

    /**
     * @param complianceReport
     * @throws DAOException
     */
    boolean modifyComplianceReport(ComplianceReport complianceReport)
            throws DAOException;
    
    /**
     * @param complianceDeviation
     * @throws DAOException
     */
    boolean modifyComplianceDeviation(ComplianceDeviation complianceDeviation)
            throws DAOException;

    /**
     * @param complianceReportId
     * @throws DAOException
     */
    ComplianceReport retrieveComplianceReport(int complianceReportId)
            throws DAOException;
    /**
     * @param reportId
     * @return
     * @throws DAOException
     */
    public CompliancePerDetail[] retrievePerDetails(int reportId) throws DAOException;
    
    /**
     * @param reportID
     * @return
     * @throws DAOException
     */
    public ComplianceDeviation[] retrieveDeviations(int reportID) throws DAOException;

    /**
     * @param complianceReportId
     * @throws DAOException
     */
    ComplianceReportAttachment[] retrieveAttachments(String reportType,String facilityID, int reportID)
        throws DAOException; 
    
    /**
     * @param reportID
     * @param publicDocId
     * @return
     * @throws DAOException
     */
    public void retrieveCRTradeSecretAttachmentInfo(ComplianceReportAttachment attachment)
    throws DAOException;
    
    /**
     * @param attachment
     * @throws DAOException
     */
    public void createCRTradeSecretAttachment(ComplianceReportAttachment attachment) throws DAOException;
    
    /**
     * @param attachment
     * @throws DAOException
     */
    public void modifyCRTradeSecretAttachment(ComplianceReportAttachment attachment) throws DAOException;
    
    /**
     * @param attachment
     * @throws DAOException
     */
    public void deleteCRTradeSecretAttachment(ComplianceReportAttachment attachment) throws DAOException;
    
    /**
     * @param complianceReport
     * @param attachment
     * @throws DAOException
     */
    ComplianceReportAttachment modifyComplianceAttachment(ComplianceReport compReport,ComplianceReportAttachment complianceAttachment)
            throws DAOException;
    
    List<ComplianceReportList> newAfsTvCc() throws DAOException ;
    boolean afsLockTvCc(ComplianceReportList crl, Integer afsId) throws DAOException ;
    String tvCcAfsLocked(Integer reportId) throws DAOException ;
    List<ComplianceReportList> retrieveTvCcByAfsId(String scscId, String afsId) throws DAOException ;
    boolean afsSetDateTvCc(ComplianceReportList tvCc) throws DAOException ;
    
    Note[] retrieveNotes(int reportId) throws DAOException;
	void createNote(Integer reportId, Integer noteId) throws DAOException;
	
	/**
     * @param reportId
     * @param noteId
     * @throws DAOException
     */
	public boolean removeNote(Integer reportId, Integer noteId) throws DAOException;
	
	public void setComplianceReportValidatedFlag(Integer stId,
			boolean validated) throws DAOException;
	
	public ComplianceReportCategoryInfo[] retrieveComplianceReportCategoryInfo()
			throws DAOException; 
	
	// Compliance Report Limits

	/**
	 * @param fpId
	 * @throws DAOException
	 */
	List<ComplianceReportLimit> retrieveComplianceReportLimitListByFpId(
			Integer fpId) throws DAOException;

	/**
	 * @param monitorId
	 * @throws DAOException
	 */
	List<ComplianceReportLimit> retrieveComplianceReportLimitListByMonitorId(
			Integer monitorId) throws DAOException;

	/**
	 * @param monitorId
	 * @throws DAOException
	 */
	void removeComplianceReportLimitList(int monitorId) throws DAOException;

	/**
	 * @param crl
	 * @return
	 * @throws DAOException
	 */
	ComplianceReportLimit createComplianceReportLimit(ComplianceReportLimit crl)
			throws DAOException;

	/**
	 * @param crl
	 * @throws DAOException
	 */
	void modifyComplianceReportLimit(ComplianceReportLimit crl)
			throws DAOException;

	/**
	 * @param crl
	 * @throws DAOException
	 */
	void removeComplianceReportLimit(ComplianceReportLimit crl)
			throws DAOException;

	ComplianceReportLimit retrieveComplianceReportLimitByMonitorIdAndCorrId(
			Integer monitorId, Integer corrLimitId) throws DAOException;

	/**
	 * Create a new ComplianceReportMonitor.
	 * 
	 * @param complianceReportMonitor
	 * @return
	 * @throws DAOException
	 */
	ComplianceReportMonitor createComplianceReportMonitor(
			ComplianceReportMonitor complianceReportMonitor)
			throws DAOException;

	/**
	 * Delete ComplianceReportMonitor.
	 * 
	 * @param complianceReportMonitor
	 * @return
	 * @throws DAOException
	 */
	void deleteComplianceReportMonitor(
			ComplianceReportMonitor complianceReportMonitor)
			throws DAOException;

	/**
	 * Update ComplianceReportMonitor.
	 * 
	 * @param complianceReportMonitor
	 * @return
	 * @throws DAOException
	 */
	boolean modifyComplianceReportMonitor(
			ComplianceReportMonitor complianceReportMonitor)
			throws DAOException;

	/**
	 * Retrieve ComplianceReportMonitor.
	 * 
	 * @param complianceReportMonitor
	 * @return
	 * @throws DAOException
	 */
	public ComplianceReportMonitor retrieveComplianceReportMonitor(
			Integer complianceReportMonitor) throws DAOException;

	/**
	 * @param reportId
	 * @throws DAOException
	 */
	public ComplianceReportMonitor[] retrieveComplianceReportMonitorListByReportId(
			Integer reportId) throws DAOException;

	/**
	 * @param reportId
	 * @throws DAOException
	 */
	public ComplianceReportLimit[] retrieveComplianceReportLimitListByReportId(
			Integer reportId) throws DAOException;
	
	ArrayList<LimitTrendReportLineItem> retrieveLimitTrendData(
			Integer corrLimitId, String facilityId) throws DAOException;
	
	ArrayList<LimitTrendReportLineItem> retrievePeriodicLimitTrendData(
			Integer corrLimitId, String facilityId) throws DAOException;
	
	boolean setActiveComplianceReportsValidatedFlag(Integer fpId,
			boolean validatedFlag) throws DAOException;
	
	/**
	 * @param tradeSecretDocId
	 * @return ComplianceReportAttachment
	 * @throws DAOException
	 */
	ComplianceReportAttachment retrieveCRTradeSecretAttachmentInfoById(Integer tradeSecretDocId) throws DAOException;

}
