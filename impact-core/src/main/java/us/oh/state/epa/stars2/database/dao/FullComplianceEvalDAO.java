package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
import us.oh.state.epa.stars2.database.dbObjects.ceta.SearchDateRange;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SvAttachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface FullComplianceEvalDAO extends TransactableDAO {
	
    FullComplianceEval createFce(FullComplianceEval newFce)  throws DAOException ;

    void modifyFce(FullComplianceEval fce) throws DAOException ;

    void modifyFce(FceScheduleRow fceR) throws DAOException ;

    void removeFce(Integer fceId) throws DAOException ;

    void removeFceNotes(Integer fceId) throws DAOException ;

    FullComplianceEval retrieveFce(Integer fceId) throws DAOException ;

    FullComplianceEval retrieveFce(String inspId) throws DAOException ;

    FullComplianceEval[] retrieveFceBySearch(String  inspId, String facilityId,
            String facilityName, String countyCd, String doLaaCd, String permitClassCd, String facilityTypeCd,
            Timestamp beginSched, Timestamp endSched,
            Timestamp beginDate, Timestamp endDate, Integer assignedStaff, 
            Integer evaluator, String usEpaCommitted, List<String> inspectionReportStateCds, String Portable, String cmpId) throws DAOException ;
    
    void createVisitEvaluator(Evaluator eval, Integer visitId)  throws DAOException ;

    void removeVisitEvaluators(Integer visitId)  throws DAOException ;

    SiteVisit createSiteVisit(SiteVisit newVisit)  throws DAOException ;

    void modifySiteVisit(SiteVisit visit) throws DAOException ;

    void removeSiteVisit(Integer visitId) throws DAOException ;

    void removeSiteVisitNotes(Integer visitId) throws DAOException ;

    SiteVisit retrieveSiteVisit(Integer visitId) throws DAOException ;

    boolean isVisitDup(SiteVisit siteVisit) throws DAOException ;

    SiteVisit[] retrieveVisitsUnassigned(String facilityId) throws DAOException ;

    SiteVisit[] retrieveSiteVisitsByFce(Integer fceId) throws DAOException;

    SiteVisit[] retrieveVisitBySearch(String siteId, String inspId, String facilityId, 
           Timestamp beginDate, Timestamp endDate, String visitType,
           String announced, Integer evaluator, String facilityName,
           String doLaaCd, String countyCd, String permitClassCd, String facilityTypeCd, String cmpId, String complianceIssued) throws DAOException ;

    ArrayList<FceScheduleRow> needToSchedFce(String facilityId, 
           String facilityNm, String doLaaCd, String countyCd, String permitClassCd, String facilityTypeCd, String cmpId) throws DAOException ;

    void removeStackTestSiteVisit(Timestamp visitDt, String facilityId) throws DAOException ;

    SiteVisit retrieveStackTestVisitByDate(Timestamp visitDt, String facilityId) throws DAOException ;

    DbInteger getUserId(String name) throws DAOException ;

    FullComplianceEval createFceGetFpId(String facilityId, FullComplianceEval newFce) throws DAOException ;

    ArrayList<FceScheduleRow> needToSchedFirstFce(String facilityId, 
           String facilityNm, String doLaaCd, String countyCd,
           String permitClassCd, String facilityTypeCd, String cmpId) throws DAOException ;

    void deleteFceAttachment(FceAttachment att) throws DAOException  ;

    List<FceAttachment> retrieveFceAttachments(int fceId) throws DAOException ;

    FceAttachment createFceAttachment(FceAttachment fa) throws DAOException ;

    boolean modifyFceAttachment(FceAttachment doc) throws DAOException ;

    void deleteSvAttachment(SvAttachment att) throws DAOException  ;

    List<SvAttachment> retrieveSvAttachments(int visitId) throws DAOException ;

    SvAttachment createSvAttachment(SvAttachment fa) throws DAOException ;

    boolean modifySvAttachment(SvAttachment doc) throws DAOException ;

    ArrayList<Evaluator> retrieveEmissionsTestWitnesses(String facilityId, Timestamp visitDt) throws DAOException ;

    public List<SiteVisit> newAfsSiteVisits() throws DAOException ;

    List<FullComplianceEval> newAfsFCEs() throws DAOException ;

    List<DbInteger> isStackTestSubmitted(Timestamp ts) throws DAOException ;

    List<FullComplianceEval> newAfsScheduledFCEs(Timestamp beginSched, Timestamp endSched) throws DAOException ;

    boolean afsLockFceComp(FullComplianceEval fce, Integer afsId) throws DAOException ;

    boolean afsLockFceSched(FullComplianceEval fce) throws DAOException ;

    boolean afsLockSiteVisit(SiteVisit sv, Integer afsId) throws DAOException ;

    FullComplianceEval retrieveLastCompletedFce(String facilityId) throws DAOException ;

    FullComplianceEval retrieveLastScheduledFce(String facilityId) throws DAOException ;

    SiteVisit retrieveLastSiteVisit(String facilityId) throws DAOException ;

    DbInteger lockedStackTestSiteVisitCnt(Timestamp visitDt, String facilityId) throws DAOException ;

    String fceAfsLocked(Integer fceId) throws DAOException ;

    boolean afsSetDateFceSched(FullComplianceEval fce) throws DAOException ;

    String siteVisitAfsLocked(Integer siteVisitId) throws DAOException ;

    boolean afsSetDateSiteVisit(SiteVisit sv) throws DAOException ;

    boolean afsSetDateFceComp(FullComplianceEval fce) throws DAOException ;

    List<FullComplianceEval> retrieveSchedFceByAfsId(String scscId, String afsId) throws DAOException ;

    List<FullComplianceEval> retrieveEvalFceByAfsId(String scscId, String afsId) throws DAOException ;

    List<SiteVisit> retrieveSiteVisitByAfsId(String scscId, String afsId) throws DAOException ;

    ArrayList<Integer> complianceSearch(String facilityId, 
           String facilityNm, String operatingStatusCd, String doLaaCd,
           String countyCd,  List<String> selectedInspectClasses,
           List<String> selectedSipList, List<String> selectedMactList, 
           List<String> selectedTvList, List<String> selectedSmList,
           List<String> selectedNeshapsList, List<String> selectedNspsList,
           List<String> selectedPsdList, List<String> selectedNsrList) throws DAOException ;

    ArrayList<AirProgramCompliance> complianceSearch(List<Integer> fpList) throws DAOException ;

    ArrayList<Evaluator> orderEvaluators(List<Evaluator> eList) throws DAOException ;

    List<FullComplianceEval> fceNeedReminders() throws DAOException ;

    Note[] retrieveInspectionNotes(int fceID) throws DAOException;

    void createInspectionNote(Integer fceId, Integer noteId) throws DAOException;

    Note[] retrieveSiteVisitNotes(int fceID) throws DAOException;

    void createSiteVisitNote(Integer fceId, Integer noteId) throws DAOException;

    FullComplianceEval[] retrieveFcesWithoutSiteVisitsOrStackTests(
			String facilityId) throws DAOException;
		
	public FullComplianceEval retrieveLastPriorCompletedFce(String facilityId, Integer fceId) throws DAOException;

	void createAdditionalAQDStaff(Integer aqdUserId, Integer fceId) throws DAOException;

	void deleteAdditionalAQDStaffByFceId(Integer fceId) throws DAOException;

	List<Evaluator> retrieveAdditionalAQDStaffByFceId(Integer fceId) throws DAOException;
	
	public void modifyFcePrepare(FullComplianceEval fce) throws DAOException;
	
	public void modifyFceComplete(FullComplianceEval fce) throws DAOException;

	public void modifyFceReportState(FullComplianceEval fce) throws DAOException;
	
	void createFceAmbientConditions(AmbientConditions ambientCondition) throws DAOException;

	void modifyFceAmbientConditions(AmbientConditions ambientCondition) throws DAOException;

	ArrayList<AmbientConditions> retrieveFceAmbientConditions(Integer fceId) throws DAOException;

	void deleteFceAmbientConditionsByFceId(Integer fceId) throws DAOException;

	void updateFceObservationsAndConcerns(FullComplianceEval fce) throws DAOException;
	
	//Search
	public List<FceApplicationSearchLineItem> retrieveFceApplicationsBySearch(String facilityId, Timestamp startDt, Timestamp endDt) throws DAOException;

	//Fce_Pre_Date_Range
	public SearchDateRange retrieveFcePreservedSearchDateRange(Integer fceId, String snapshotTypeCd) throws DAOException;

	public void deleteFcePreservedSearchDateRange(Integer fceId, String snapshotTypeCd) throws DAOException;

	public void addFceSnapshotSearchDateRange(Integer fceId, String snapshotTypeCd, Timestamp startDate, Timestamp endDate) throws DAOException;
	
	// Snapshot Preserved List - Application 
	public List<FceApplicationSearchLineItem> retrieveFceApplicationListPreservedByFceId(Integer fceId) throws DAOException;

	public void deleteFceApplicationPreservedList(Integer fceId) throws DAOException;

	public void addFceApplicationSnapshotList(Integer fceId, Integer applicationId) throws DAOException;
	
	//	public void addFcePreservedList(Integer fceId, String snapshotTypeCd, Integer id) throws DAOException;
	//	public void deleteFcePreservedList(Integer fceId, String snapshotTypeCd) throws DAOException;

	void createAssociatedPermitIdRef(Integer fceId, Integer permitId) throws DAOException;

	void deleteAssociatedPermitIdRef(Integer fceId, Integer permitId) throws DAOException;

	List<Integer> retrieveAssociatedPermitIdsByFceId(Integer fceId) throws DAOException;

	void deleteAssociatedPermitIdRefsByFceId(Integer fceId) throws DAOException;

	void deleteAssociatedPermitConditionIdRefByPermitIds(List<Integer> permitIds) throws DAOException;

	void associatePermitConditionsByPermitIds(List<Integer> permitIds, Integer fceId) throws DAOException;

	List<FcePermitCondition> retrieveAssociatedPermitConditionsByFceId(Integer fceId) throws DAOException;

	List<PermitConditionSearchLineItem> retrieveExcludedPermitConditionsByFceId(Integer fceId) throws DAOException;
	
	public void deleteFcePermitPreservedList(Integer fceId) throws DAOException;

	public void addFcePermitSnapshotList(Integer fceId, Integer applicationId) throws DAOException;

	public List<Permit> retrieveFcePermitListPreservedByFceId(Integer fceId) throws DAOException;

	void modifyAssociatedPermitConditionIdRef(FcePermitCondition pc) throws DAOException;

	void deleteAssociatedPermitConditionIdRefsByFceId(Integer fceId) throws DAOException;

	void deleteAssociatedPermitConditionIdRef(Integer fceId, Integer permitConditionId) throws DAOException;

	void createAssociatedPermitConditionIdRef(Integer fceId, Integer permitConditionId) throws DAOException;

	// Stack test
	public List<FceStackTestSearchLineItem> retrieveFceStackTestsBySearch(String facilityId, Timestamp startDt, Timestamp endDt) throws DAOException;

	public void deleteFceStackTestPreservedList(Integer fceId) throws DAOException;

	public void addFceStackTestSnapshotList(Integer fceId, Integer stackTestId) throws DAOException;

	public List<FceStackTestSearchLineItem> retrieveFceStackTestListPreservedByFceId(Integer fceId) throws DAOException;

	// Site visits
	public List<ComplianceReportList> retrieveFceComplianceReportBySearch(String facilityId, Timestamp startDt, Timestamp endDt) throws DAOException;
	
	List<SiteVisit> retrieveFceSiteVisitListPreservedByFceId(Integer fceId) throws DAOException;

	void deleteFceSiteVisitPreservedList(Integer fceId) throws DAOException;

	void addFceSiteVisitSnapshotList(Integer fceId, Integer siteVisitId) throws DAOException;
	
	// Compliance Report
	public void deleteFceComplianceReportPreservedList(Integer fceId) throws DAOException;

	public void addFceComplianceReportSnapshotList(Integer fceId, Integer complianceReportId) throws DAOException;

	public List<ComplianceReportList> retrieveFceComplianceReportListPreservedByFceId(Integer fceId) throws DAOException;
	
	// Ambient Monitors
	List<FceAmbientMonitorLineItem> retrieveFceAmbientMonitorListPreservedByFceId(Integer id) throws DAOException;

	void deleteFceAmbientMonitorPreservedList(Integer fceId) throws DAOException;

	void addFceAmbientMonitorSnapshotList(Integer fceId, Integer id) throws DAOException;
	
	// Correspondence
	public void deleteFceCorrespondencePreservedList(Integer fceId) throws DAOException;

	public void addFceCorrespondenceSnapshotList(Integer fceId, Integer correspondencetId) throws DAOException;

	public List<Correspondence> retrieveFceCorrespondenceListPreservedByFceId(Integer fceId) throws DAOException;

	//Emissions Inventory
	public List<FceEmissionsInventoryLineItem> retrieveFceEmissionsInventoryBySearch(String facilityId, Timestamp startDt, Timestamp endDt) throws DAOException;
	
	public void deleteFceEmissionsInventoryPreservedList(Integer fceId) throws DAOException;
	
	public void addFceEmissionsInventorySnapshotList(Integer fceId, Integer correspondencetId) throws DAOException;
	
	public List<FceEmissionsInventoryLineItem> retrieveFceEmissionsInventoryListPreservedByFceId(Integer fceId) throws DAOException;
	
	public HashMap<String, Float> retrieveEmissionsInventoryPollutantTotalEmissions(Integer emissionsRptId) throws DAOException;

	void deleteFceContinuousMonitorLimitPreservedList(Integer fceId) throws DAOException;

	void addFceContinuousMonitorLimitSnapshotList(Integer fceId, Integer limitId) throws DAOException;

	List<FceContinuousMonitorLineItem> retrieveFceContinuousMonitorListPreservedByFceId(Integer fceId)
			throws DAOException;

	public void updateFceReferenceReviewStartDate(FullComplianceEval fce) throws DAOException;
	
	List<String> retrieveInspectionIdsForPermitId(Integer permitId) throws DAOException;
	
	List<String> retrieveInspectionIdsForStackTestId(Integer stackTestId) throws DAOException;
	
	List<String> retrieveInspectionIdsForComplianceRptId(Integer complianceRptId) throws DAOException;
	
	List<String> retrieveInspectionIdsForCorrespondenceId(Integer correspondenceId) throws DAOException;
	
	List<String> retrieveInspectionIdsForEmissionRptId(Integer emissionsRptId) throws DAOException;
	
	List<String> retrieveInspectionIdsForCemComId(Integer monitorId) throws DAOException;
	
	List<String> retrieveInspectionIdsForSiteVisitId(Integer siteVisitId) throws DAOException;

	List<String> retrieveInspectionIdsForAmbientMonSiteId(Integer monitorSiteId) throws DAOException;

	List<String> retrieveInspectionIdsForApplicationId(Integer appId) throws DAOException;

	Integer getMonitorLimitsIncludedInInspectionReportCount(List<Integer> limitIds) throws DAOException;

	public Integer retireveFceAttachmentCountByType(Integer fceId, String fce_attachment_type_cd) throws DAOException;
	
	public List<String> retrieveInspectionIdsForLastFceId(Integer fceId) throws DAOException;
	
	public void clearLastInspIdByFacility(String faciliityId) throws DAOException;
}
