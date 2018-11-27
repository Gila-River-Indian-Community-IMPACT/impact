package us.oh.state.epa.stars2.database.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.EUFee;
import us.oh.state.epa.stars2.database.dbObjects.permit.EmissionsOffset;
import us.oh.state.epa.stars2.database.dbObjects.permit.ExpiredPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.NSRFixedCharge;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitActivitySearch;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCC;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitChargePayment;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitWorkflowSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.def.NSRBillingBillableRateDef;
import us.oh.state.epa.stars2.def.NSRBillingStandardFeesDef;
import us.oh.state.epa.stars2.def.PermitWorkflowActivityBenchmarkDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface PermitDAO extends TransactableDAO {

	public PermitWorkflowActivityBenchmarkDef[] retrievePermitWorkflowActivityBenchmarkDefs()
			throws DAOException;

	public List<PermitWorkflowSearchResult> searchPermitWorkflows(String facilityId,
			String facilityNm, String permitNumber,	String applicationNumber,
			Integer userId, String permitType, String activityNm,
			String activityStatusCd, Date startDateFrom, Date startDateTo,
			Date endDateFrom, Date endDateTo, Integer processId, boolean filterSkipped, 
			boolean filterNonStarted)
			throws DAOException;

	/**
	 * @param permitNumber
	 * @param facilityID
	 * @param facilityName
	 * @param permitStatusCd
	 * @param endDate
	 * @param beginDate
	 * @param dateBy
	 * @param dateBy2
	 * @param facilityName2
	 * @param facilityID2
	 * @param permitNumber2
	 * @param maxNumberOfHits
	 * @return
	 * @throws DAOException
	 */
	List<Permit> searchPermits(String applicationNumber, String euId,
			String cmpId, String permitType, String permitReason, String permitLevelStatusCd,
			String legacyPermitNumber, String permitNumber, String facilityID, String facilityName,
			String permitStatusCd, String dateBy, Timestamp beginDate,
			Timestamp endDate, String permitEUStatusCd, boolean unlimitedResults, String permitFeeBalanceTypeCd)
			throws DAOException;
	
	/**
	 * @return
	 * @throws DAOException
	 */
	void markInactiveNSRPermitsToExpired() throws DAOException;
	
	/**
	 * @param cmpId
	 * @param facilityID
	 * @param facilityName
	 * @return
	 * @throws DAOException
	 */
	List<PTIOPermit> searchPermitsForFinalInvoice(String cmpId, String facilityID,
			String facilityName, boolean unlimitedResults) throws DAOException;

	/**
	 * @param permitId
	 * @return
	 * @throws DAOException
	 */
	Permit retrievePermit(Integer permitId) throws DAOException;

	/**
	 * @param permitNbr
	 * @return
	 * @throws DAOException
	 */
	Permit retrievePermit(String permitNbr) throws DAOException;

	/**
	 * @param permit
	 * @return
	 * @throws DAOException
	 */
	Permit createPermit(Permit permit) throws DAOException;

	/**
	 * @param permit
	 * @return
	 * @throws DAOException
	 */
	Permit createPTIOPermit(Permit permit) throws DAOException;

	/**
	 * @param permit
	 * @return
	 * @throws DAOException
	 */
	Permit createTVPermit(Permit permit) throws DAOException;

	/**
	 * @param permit
	 * @return
	 * @throws DAOException
	 */
	//Permit createRegPermit(Permit permit) throws DAOException;

	/**
	 * @param permit
	 * @return
	 * @throws DAOException
	 */
	boolean modifyPermit(Permit permit) throws DAOException;

	/**
	 * @param permit
	 * @return
	 * @throws DAOException
	 */
	boolean modifyPTIOPermit(Permit permit) throws DAOException;

	/**
	 * @param permit
	 * @return
	 * @throws DAOException
	 */
	boolean modifyTVPermit(Permit permit) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removePermit(Integer permitId) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removePTIOPermit(Integer permitId) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removeTVPermit(Integer permitId) throws DAOException;

	/**
     * 
     */
	void removePermitEUGroups(Integer permitId) throws DAOException;

	/**
	 * @param euGroup
	 * @throws DAOException
	 */
	void removeEUGroupEUs(Integer euGroupId) throws DAOException;

	/**
	 * @param permitEUGroupId
	 * @return
	 * @throws DAOException
	 */
	PermitEUGroup retrievePermitEUGroup(Integer euGroupId) throws DAOException;

	/**
	 * @param permit
	 * @param euGroup
	 * @return
	 * @throws DAOException
	 */
	PermitEUGroup createPermitEUGroup(PermitEUGroup euGroup)
			throws DAOException;

	/**
	 * @param euGroup
	 * @return
	 * @throws DAOException
	 */
	void modifyPermitEUGroup(PermitEUGroup euGroup) throws DAOException;

	/**
	 * @param euGroup
	 * @throws DAOException
	 */
	void removePermitEUGroup(PermitEUGroup euGroup) throws DAOException;

	/**
	 * @param permitEUId
	 * @return
	 * @throws DAOException
	 */
	PermitEU retrievePermitEU(Integer permitEUId) throws DAOException;

	/**
	 * @param eu
	 * @return
	 * @throws DAOException
	 */
	PermitEU createPermitEU(PermitEU eu) throws DAOException;

	/**
	 * @param eu
	 * @return
	 * @throws DAOException
	 */
	void modifyPermitEU(PermitEU eu) throws DAOException;

	/**
	 * @param euId
	 * @throws DAOException
	 */
	void removePermitEU(Integer euId) throws DAOException;

	PermitEU[] searchPermitEUsAcrossPermits(Integer corrEpaEMUID,
			boolean equals, String permitTypeCD) throws DAOException;

	/**
	 * @param permitId
	 * @return
	 * @throws DAOException
	 */
	PermitDocument[] retrievePermitDocuments(Integer permitId)
			throws DAOException;

	/**
	 * @param permit
	 * @param permitDoc
	 * @return
	 * @throws DAOException
	 */
	PermitDocument createPermitDocument(PermitDocument permitDoc)
			throws DAOException;

	/**
	 * @param permitDoc
	 * @return
	 * @throws DAOException
	 */
	boolean modifyPermitDocument(PermitDocument permitDoc) throws DAOException;

	/**
	 * @param permitDoc
	 * @throws DAOException
	 */
	void removePermitDocument(PermitDocument permitDoc) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removePermitDocuments(Integer permitId) throws DAOException;

	/**
	 * @return
	 * @throws DAOException
	 */
	int generatePermitSeqNo() throws DAOException;

	/**
	 * @return Permit[]
	 * @throws DAOException
	 */
	Permit[] retrieveAllPermits() throws DAOException;

	/**
	 * @param permitIds
	 * @return
	 * @throws DAOException
	 */
	Permit[] retrievePermits(Integer permitIds[]) throws DAOException;

	/**
	 * @param roleCd
	 * @param expirationDate
	 * @return
	 * @throws DAOException
	 */
	ExpiredPermit[] retrieveExpiredPTIOPermits(String roleCd,
			Timestamp expirationDate) throws DAOException;

	/**
	 * @param permitId
	 * @param applicationId
	 * @throws DAOException
	 */
	void createPermitApplication(int permitId, int applicationId)
			throws DAOException;

	/**
	 * @param permitId
	 * @param applications
	 * @throws DAOException
	 */
	List<Application> addPermitApplications(int permitId,
			List<Application> applications) throws DAOException;

	/**
	 * @param permitId
	 * @param applications
	 * @throws DAOException
	 */
	List<PermitDocument> addPermitDocuments(int permitId,
			List<PermitDocument> documents) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removePermitApplications(int permitId) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void markTempPermitDocuments(int permitId) throws DAOException;

	/**
	 * @param permitType
	 * @return
	 * @throws DAOException
	 * 
	 */
	SimpleDef[] retrievePermitReasons(String permitType) throws DAOException;

	/**
	 * @return
	 * @throws DAOException
	 * 
	 */
	SimpleDef[] retrieveAllPermitReasons() throws DAOException;

	/**
	 * @return
	 * @throws DAOException
	 * 
	 */
	SimpleDef[] retrieveAllPermitTypes() throws DAOException;

	/**
	 * @param pi
	 * @throws DAOException
	 */
	PermitIssuance createPermitIssuance(PermitIssuance pi) throws DAOException;

	/**
	 * @param permitId
	 * @param permitReasonCDs
	 * @throws DAOException
	 * 
	 */
	List<String> addPermitReasons(int permitId, List<String> permitReasonCDs)
			throws DAOException;

	/**
	 * @param pid
	 * @throws DAOException
	 * 
	 */
	void removePermitReasons(int permitId) throws DAOException;

	/**
	 * @param permitID
	 * @return
	 * @throws DAOException
	 * 
	 */
	LinkedHashMap<String, PermitIssuance> retrievePermitIssuances(int permitID)
			throws DAOException;

	/**
	 * @param permitIssuances
	 * @throws DAOException
	 * 
	 */
	boolean modifyPermitIssuance(PermitIssuance permitIssuance)
			throws DAOException;

	/**
	 * Retrieves all active permits for a given EU.
	 * 
	 * @param euId
	 * @return ArrayList
	 * @throws DAOException
	 */
	ArrayList<Permit> searchEuPermits(Integer euId) throws DAOException;

	/**
	 * Retrieves all permits for a given EU.
	 * 
	 * @param euId
	 * @return ArrayList
	 * @throws DAOException
	 */
	ArrayList<Permit> searchAllEuPermits(Integer euId) throws DAOException;

	/**
	 * @param facilityId
	 * @return ArrayList
	 * @throws DAOException
	 */
	ArrayList<Permit> searchActivePermitsForFacility(String facilityId)
			throws DAOException;

	/**
	 * @param permitId
	 * @param noteId
	 * @throws DAOException
	 */
	void createPermitNote(Integer permitId, Integer noteId) throws DAOException;

	/**
	 * @param permitID
	 * @return
	 * @throws DAOException
	 * 
	 */
	Note[] retrieveDapcComments(int permitID) throws DAOException;

	/**
	 * @param permitID
	 * @return
	 * @throws DAOException
	 */
	ArrayList<Contact> retrievePermitContacts(int permitID) throws DAOException;

	/**
	 * @param pid
	 * @throws DAOException
	 * 
	 */
	void removePermitContacts(int permitId) throws DAOException;

	/**
	 * @param permitId
	 * @param contactId
	 * @throws DAOException
	 * 
	 */
	void createPermitContact(int permitId, Integer contactId)
			throws DAOException;

	/**
	 * @param permitId
	 * @param contactId
	 * @throws DAOException
	 * 
	 */
	void removePermitContact(int permitId, Integer contactId)
			throws DAOException;

	/**
	 * Create subpart codes if isMact(), isNeshaps(), or isNsps().
	 * 
	 * @param permit
	 * @throws DAOException
	 */
	void createSubpartCDs(Permit permit) throws DAOException;

	/**
	 * Delete all subpart codes.
	 * 
	 * @param permit
	 * @throws DAOException
	 */
	void removeSubpartCDs(Permit permit) throws DAOException;

	/**
	 * @param permitId
	 * @return
	 * @throws DAOException
	 */
	List<PermitCC> retrievePermitCCList(int permitId) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removePermitCCList(int permitId) throws DAOException;

	/**
	 * @param pcc
	 * @return
	 * @throws DAOException
	 */
	PermitCC createPermitCC(PermitCC pcc) throws DAOException;

	/**
	 * @param pcc
	 * @throws DAOException
	 */
	void modifyPermitCC(PermitCC pcc) throws DAOException;

	/**
	 * @param pcc
	 * @throws DAOException
	 */
	void removePermitCC(PermitCC pcc) throws DAOException;
	
	/**
	 * @param permitId
	 * @return
	 * @throws DAOException
	 */
	List<PermitChargePayment> retrievePermitChargePaymentList(int permitId)
			throws DAOException;

	/**
	 * @param permitId
	 * @return
	 * @throws DAOException
	 */
	
	BigDecimal retrievePermitChargePaymentTotal(int permitId)
			throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removePermitChargePaymentList(int permitId) throws DAOException;

	/**
	 * @param pcp
	 * @return
	 * @throws DAOException
	 */
	PermitChargePayment createPermitChargePayment(PermitChargePayment pcp)
			throws DAOException;

	/**
	 * @param pcp
	 * @throws DAOException
	 */
	void modifyPermitChargePayment(PermitChargePayment pcp) throws DAOException;

	/**
	 * @param pcp
	 * @throws DAOException
	 */
	void removePermitChargePayment(PermitChargePayment pcp) throws DAOException;

	/**
	 * @param euFee
	 * @throws DAOException
	 */
	void modifyPermitEUFee(EUFee euFee) throws DAOException;

	/**
	 * @param fee
	 * @throws DAOException
	 */
	EUFee createPermitEUFee(EUFee fee) throws DAOException;

	/**
	 * @param euFeeId
	 * @throws DAOException
	 */
	void removePermitEUFee(int euFeeId) throws DAOException;

	/**
	 * @param documentID
	 * @throws DAOException
	 */
	void unMarkTempPermitDocuments(Integer documentID) throws DAOException;

	/**
	 * @param generalPermitTypeCd
	 * @return
	 * @throws DAOException
	 */
	SimpleDef[] retrieveModelGeneralPermitDefs(String generalPermitTypeCd)
			throws DAOException;

	/**
	 * @return
	 * @throws DAOException
	 */
	ArrayList<Integer> retrieveExpiredTVPermits(Timestamp endDate) throws DAOException;

	/**
	 * @param facilityEUID
	 * @throws DAOException
	 */
	SimpleIdDef[] retrieveSupersedablePermits(Integer facilityEUID)
			throws DAOException;

	/**
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 */
	SimpleIdDef[] retrieveSupersedableTVPermits(String facilityId)
			throws DAOException;

	/**
	 * @param permitId
	 * @param newStatusCd
	 * @throws DAOException
	 */
	void updatePermitEUsStatus(int permitId, String newStatusCd)
			throws DAOException;

	/**
	 * @param eu
	 * @throws DAOException
	 */
	ArrayList<String> retrieveFacilityNameForEU(PermitEU eu)
			throws DAOException;

	SimpleIdDef[] retrieveRPRPermitList(String facilityId) throws DAOException;

	/**
	 * Method used only to "fix" migrated permit documents for TVPTO and State
	 * PTO Applications.
	 * 
	 * @param permitTypeCd
	 * @return
	 * @throws DAOException
	 */
	public PermitDocument[] retrieveGoodMigratedPermitDocuments(
			String permitTypeCd) throws DAOException;

	/**
	 * Method used only to "fix" migrated permit documents for TVPTO and State
	 * PTO Applications.
	 * 
	 * @param permitTypeCd
	 * @return
	 * @throws DAOException
	 */
	public PermitDocument[] retrieveBadMigratedPermitDocuments(
			String permitTypeCd) throws DAOException;

	PermitActivitySearch[] searchPermitActivity(PermitActivitySearch pa)
			throws DAOException;

	Document[] retrievePermitIssuanceDocuments() throws DAOException;

	List<PermitIssuance> retrieveLatestPermitIssuanceFinal(int permitID)
			throws DAOException;

	public boolean modifyMakePermitUndead(Permit permit) throws DAOException;
	
	void removePermitApplication(int applicationId) throws DAOException;
	
	/**
	 * Check the duplicated permit number
	 * 
	 * @param permitNumber
	 * @return boolean
	 * @throws DAOException
	 */
	public boolean isDuplicatePermitNumber(String permitNumber) throws DAOException;
	
	public boolean isDuplicateLegacyPermitNumber(String legacyPermitNumber, String permitNumber) throws DAOException;
	
	public TimeSheetRow[] retrieveTimeSheetHoursFromAqds(String applicationNumbersString, String permitNumber) throws DAOException;
	
	public TimeSheetRow[] retrieveTimeSheetHoursFromImpact(String applicationNumbersString, String permitNumber) throws DAOException;
	
	public TimeSheetRow[] retrieveTimeSheetHours(String applicationNumbersString, String permitNumber) throws DAOException;
	
	public NSRBillingBillableRateDef[] retrieveBillableRatesDef() throws DAOException;
	
	/**
	 * @param permitId
	 * @return
	 * @throws DAOException
	 */
	List<NSRFixedCharge> retrieveNSRFixedChargeList(int permitId)
			throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	void removeNSRFixedChargeList(int permitId) throws DAOException;

	/**
	 * @param pcp
	 * @return
	 * @throws DAOException
	 */
	NSRFixedCharge createNSRFixedCharge(NSRFixedCharge nfc)
			throws DAOException;

	/**
	 * @param pcp
	 * @throws DAOException
	 */
	void modifyNSRFixedCharge(NSRFixedCharge nfc) throws DAOException;

	/**
	 * @param pcp
	 * @throws DAOException
	 */
	void removeNSRFixedCharge(NSRFixedCharge nfc) throws DAOException;
	
	public NSRBillingStandardFeesDef[] retrieveStandardFeeDef() throws DAOException;
	
	EmissionsOffset createPermitEmissionsOffset(EmissionsOffset emissionsOffset) throws DAOException;	
	
	EmissionsOffset retrievePermitEmissionsOffset(Integer emissionsOffsetId) throws DAOException;
	
	boolean modifyPermitEmissionsOffset(EmissionsOffset emissionsOffset) throws DAOException;
	
	void deletePermitEmissionsOffset(Integer emissonsOffsetId) throws DAOException;
	
	EmissionsOffset[] retrievePermitEmissionsOffsetByPermitId(Integer permitId) throws DAOException;
	
	void deletePermit(java.lang.Integer permitId) throws DAOException;
	
	void removePermitIssuances(java.lang.Integer permitId) throws DAOException;
	
	void removePermitNotes(java.lang.Integer permitId) throws DAOException;
	
	void removeAllEUGroups(java.lang.Integer permitId) throws DAOException;
	
	/**
	 * @param permitId
	 * @return
	 * @throws DAOException
	 */
	//List<PermitCondition> retrievePermitConditionList(int permitId) throws DAOException;

	/**
	 * @param permitId
	 * @throws DAOException
	 */
	//void removePermitConditionList(int permitId) throws DAOException;

	/**
	 * @param pc
	 * @return
	 * @throws DAOException
	 */
	//PermitCondition createPermitCondition(PermitCondition pc) throws DAOException;

	/**
	 * @param pc
	 * @throws DAOException
	 */
	//void modifyPermitCondition(PermitCondition pc) throws DAOException;

	/**
	 * @param pc
	 * @throws DAOException
	 */
	/*
	void removePermitCondition(PermitCondition pc) throws DAOException;
	
	boolean createAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException;

	List<Integer> retrieveAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;

	List<String> retrieveAssociatedFpEuEpaEmuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;

	void deleteAssociatedCorrEuIdRef(Integer permitConditionId, Integer corrEpaEmuId) throws DAOException;

	void deleteAssociatedCorrEuIdsByPermitConditionId(Integer permitConditionId) throws DAOException;
	*/
	
	/**
	 * Validate the permit condition number is duplicate within the permit or
	 * not.
	 * 
	 * @param permit
	 *            ID, permit condition number
	 * @return
	 * @throws DAOException
	 */
	
	/*
	boolean isDuplicatePermitConditionNumber(PermitCondition permitCondition) throws DAOException;

	ComplianceStatusEvent createComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException;

	List<ComplianceStatusEvent> retrieveComplianceStatusEventList(Integer permitConditionId) throws DAOException;

	void removeComplianceStatusEventList(Integer permitConditionId) throws DAOException;

	void modifyComplianceStatusEvent(ComplianceStatusEvent cse) throws DAOException;

	void removeComplianceStatusEvent(Integer complianceStatusId) throws DAOException;

	ComplianceStatusEvent retrieveComplianceStatusEvent(Integer complianceStatusId) throws DAOException;
	
	PermitConditionSearchLineItem[] searchPermitConditions(PermitConditionSearchCriteria searchCriteria)
			throws DAOException;
	*/
	
	/**
	 * 
	 * @param documentId
	 * @return PermitDocument
	 * @throws DAOException
	 */
	PermitDocument retrievePermitDocumentById(Integer documentId) throws DAOException;
	
	/**
	 * 
	 * @param permitNumber
	 * @return permitWorkflowProcessId
	 * @throws DAOException
	 */
	Integer retrievePermitWorkflowProcessId(String permitNumber) throws DAOException;

//	public void removePermitConditionCategoryXref(Integer permitID) throws DAOException;
//
//	void removePermitConditionEUXref(Integer permitId) throws DAOException;
	
	void removeSubpartCDs(final Integer permitId) throws DAOException;
	
	List<PermitEUGroup> retrievePermitEUGroups(final Integer permitId) throws DAOException;
	
	Integer[] retrievePermitApplicationIds(int permitId) throws DAOException;
	
	Permit retrievePermitBasic(final Integer permitId) throws DAOException;
	
	String[] retrievePermitApplicationNumbers(final Integer permitId) throws DAOException;
}
