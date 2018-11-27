package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUFugitiveLeaks;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUMaterialUsed;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationNote;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.FacilityWideRequirement;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationBACTEmission;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.NSRApplicationLAEREmission;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRExport;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotificationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequestDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TIVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVAltScenarioPteReq;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicableReq;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVCompliance;
import us.oh.state.epa.stars2.database.dbObjects.application.TVComplianceObligations;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperatingScenario;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperationalRestriction;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUPollutantLimit;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedAltLimits;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedExemptions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedTestChanges;
import us.oh.state.epa.stars2.database.dbObjects.application.TVPteAdjustment;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.TVApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface ApplicationDAO extends TransactableDAO {
	/**
	 * Retrieves an application for the given applicationID
	 * 
	 * @param applicationId
	 * @return
	 * @throws DAOException
	 */
	Application retrieveApplication(Integer applicationId) throws DAOException;

	/**
	 * 
	 * @param applicationId
	 * @return
	 * @throws DAOException
	 */
	Application retrievePortalRelocationApplication(Integer applicationId)
			throws DAOException;

	/**
	 * @param applicationNumber
	 * @return
	 * @throws DAOException
	 */
	Application retrieveApplication(String applicationNumber)
			throws DAOException;

	/**
	 * @param applicationId
	 * @return
	 * @throws DAOException
	 */
	SimpleDef[] retrieveApplicationTypes() throws DAOException;

	/**
	 * @param facilityId
	 * @return
	 * @throws DAOException
	 */
	Application[] searchApplications(String applicationNumber,
			String facilityId, String facilityName, String doLaaCd,
			String countyCd, String applicationType) throws DAOException;

	/**
	 * Retrieve application information to be displayed in a search table.
	 * 
	 * @param applicationNumber
	 *            application number to match in search.
	 * @param facilityId
	 *            facility id to match in search.
	 * @param facilityName
	 *            facility name to match in search.
	 * @param doLaaCd
	 *            DO LAA code to match in search.
	 * @param countyCd
	 *            county code to match in search.
	 * @param applicationType
	 *            application type to match in search.
	 * @return array of ApplicationSearchResult objects.
	 * @throws DAOException
	 */
	public ApplicationSearchResult[] retrieveApplicationSearchResults(
			String applicationNumber, String euId, String facilityId,
			String facilityName, String doLaaCd, String countyCd,
			String applicationType, String ptioReasonCd,
			boolean legacyStatePTOFlag, String pbrTypeCd, String permitNumber,
			String companyName, boolean unlimitedResults) throws DAOException;

	/**
	 * @param appEuId
	 * @return
	 * @throws DAOException
	 */
	ApplicationEU retrieveApplicationEmissionUnit(Integer appEuId)
			throws DAOException;

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	ApplicationEU[] retrieveApplicationEmissionUnits(Integer appId)
			throws DAOException;

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	PTIOApplicationEU[] retrievePTIOApplicationEmissionUnits(Integer appId)
			throws DAOException;

	/**
	 * Retrieve all emissions units related to the TV application identified by
	 * <code>appId</code>
	 * 
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	TVApplicationEU[] retrieveTVApplicationEmissionUnits(Integer appId)
			throws DAOException;

	/**
	 * Retrieve the application document record identified by appDocId.
	 * 
	 * @param appDocId
	 *            application document identifier.
	 * @return
	 * @throws DAOException
	 */
	ApplicationDocumentRef retrieveApplicationDocument(Integer appDocId)
			throws DAOException;
	
	/**
	 * Retrieve the NSR application document record identified by appDocId.
	 * 
	 * @param appDocId
	 *            application document identifier.
	 * @return
	 * @throws DAOException
	 */
	NSRApplicationDocumentRef retrieveNSRApplicationDocument(Integer appDocId)
			throws DAOException;
	
	/**
	 * Retrieve the TV application document record identified by appDocId.
	 * 
	 * @param appDocId
	 *            application document identifier.
	 * @return
	 * @throws DAOException
	 */
	TVApplicationDocumentRef retrieveTVApplicationDocument(Integer appDocId)
			throws DAOException;
	
	/**
	 * Retrieve all application-wide documents for the application identified by
	 * appId.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return
	 * @throws DAOException
	 */
	ApplicationDocumentRef[] retrieveApplicationDocuments(Integer appId)
			throws DAOException;

	/**
	 * Retrieve all NSR application-wide documents for the application identified by
	 * appId.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return
	 * @throws DAOException
	 */
	NSRApplicationDocumentRef[] retrieveNSRApplicationDocuments(Integer appId)
			throws DAOException;
	
	/**
	 * Retrieve all TV application-wide documents for the application identified by
	 * appId.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return
	 * @throws DAOException
	 */
	TVApplicationDocumentRef[] retrieveTVApplicationDocuments(Integer appId)
			throws DAOException;
	
	/**
	 * Retrieve all documents related to the emissions unit identified by
	 * appEuId.
	 * 
	 * @param appEuId
	 *            application emissions unit identifier.
	 * @return
	 * @throws DAOException
	 */
	ApplicationDocumentRef[] retrieveApplicationEUDocuments(Integer appEuId)
			throws DAOException;
	
	/**
	 * Retrieve all NSR documents related to the emissions unit identified by
	 * appEuId.
	 * 
	 * @param appEuId
	 *            application emissions unit identifier.
	 * @return
	 * @throws DAOException
	 */
	NSRApplicationDocumentRef[] retrieveNSRApplicationEUDocuments(Integer appEuId)
			throws DAOException;
	
	/**
	 * Retrieve all TV documents related to the emissions unit identified by
	 * appEuId.
	 * 
	 * @param appEuId
	 *            application emissions unit identifier.
	 * @return
	 * @throws DAOException
	 */
	TVApplicationDocumentRef[] retrieveTVApplicationEUDocuments(Integer appEuId)
			throws DAOException;

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	Permit[] retrievePermitsForApplication(Integer appId) throws DAOException;

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	boolean createPTIOApplicationPurpose(Integer appId, String purposeCd)
			throws DAOException;

	/**
	 * 
	 * @param appId
	 * @param reasonCd
	 * @return
	 * @throws DAOException
	 */
	boolean createTVApplicationReason(Integer appId, String reasonCd)
			throws DAOException;

	/**
	 * @param newApp
	 * @return
	 * @throws DAOException
	 */
	Application createApplication(Application newApp) throws DAOException;

	/**
	 * @param app
	 * @return
	 * @throws DAOException
	 */
	boolean modifyApplication(Application app) throws DAOException;

	/**
	 * @param newPTIOApp
	 * @return
	 * @throws DAOException
	 */
	PTIOApplication createPTIOApplication(PTIOApplication newPTIOApp)
			throws DAOException;

	/**
	 * @param newTVApp
	 * @return
	 * @throws DAOException
	 */
	TVApplication createTVApplication(TVApplication newTVApp)
			throws DAOException;

	/**
	 * @param newTIVApp
	 * @return
	 * @throws DAOException
	 */
	TIVApplication createTIVApplication(TIVApplication newTIVApp)
			throws DAOException;

	/**
	 * @param newPTIOAppEu
	 * @return
	 * @throws DAOException
	 */
	PTIOApplicationEU createPTIOApplicationEU(PTIOApplicationEU newPTIOAppEu)
			throws DAOException;

	/**
	 * @param newTVAppEu
	 * @return
	 * @throws DAOException
	 */
	TVApplicationEU createTVApplicationEU(TVApplicationEU newTVAppEu)
			throws DAOException;

	/**
	 * @param ptioApp
	 * @return
	 * @throws DAOException
	 */
	boolean modifyPTIOApplication(PTIOApplication ptioApp) throws DAOException;

	/**
	 * @param tvApp
	 * @return
	 * @throws DAOException
	 */
	boolean modifyTVApplication(TVApplication tvApp) throws DAOException;

	/**
	 * @param tivApp
	 * @return
	 * @throws DAOException
	 */
	boolean modifyTIVApplication(TIVApplication tivApp) throws DAOException;

	/**
	 * @param ptioAppEu
	 * @return
	 * @throws DAOException
	 */
	boolean modifyPTIOApplicationEU(PTIOApplicationEU ptioAppEu)
			throws DAOException;

	/**
	 * @param tvAppEu
	 * @return
	 * @throws DAOException
	 */
	boolean modifyTVApplicationEU(TVApplicationEU tvAppEu) throws DAOException;

	/**
	 * Create an ApplicationDocumentRef object.
	 * 
	 * @param newAppDoc
	 * @return
	 * @throws DAOException
	 */
	ApplicationDocumentRef createApplicationDocument(
			ApplicationDocumentRef newAppDoc) throws DAOException;

	/**
	 * Modify the application document appDoc.
	 * 
	 * @param appDoc
	 * @return
	 * @throws DAOException
	 */
	boolean modifyApplicationDocument(ApplicationDocumentRef appDoc)
			throws DAOException;

	/**
	 * Mark all documents associated with the application identified by appId as
	 * temporary.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return
	 * @throws DAOException
	 */
	void markTempApplicationDocuments(Integer appId) throws DAOException;

	/**
	 * Mark all documents associated with the PBR Notification identified by
	 * appId as temporary.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return
	 * @throws DAOException
	 */
	void markTempPBRNotificationDocuments(Integer appId) throws DAOException;

	/**
	 * Mark all documents associated with the RPC Request identified by appId as
	 * temporary.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return
	 * @throws DAOException
	 */
	void markTempRPCRequestDocuments(Integer appId) throws DAOException;

	/**
	 * Remove all documents related to the application identified by <code>
	 * appId</code>.
	 * 
	 * @param appId
	 * @throws DAOException
	 */
	void removeApplicationDocuments(Integer appId) throws DAOException;

	/**
	 * Remove the application document identified by <code>appDocId</code>.
	 * 
	 * @param appDocId
	 *            application document identifier.
	 * @throws DAOException
	 */
	void removeApplicationDocument(Integer appDocId) throws DAOException;

	void removeApplicationDocumentDocId(Integer applicationDocId)
			throws DAOException;

	/**
	 * Mark all documents associated with the application emissions unit
	 * identified by appEUId as temporary.
	 * 
	 * @param appEUId
	 *            application emissions unit.
	 * @return
	 * @throws DAOException
	 */
	void markTempApplicationEUDocuments(Integer appEUId) throws DAOException;

	/**
	 * Remove all documents associated with the application emissions unit
	 * identified by appEUId.
	 * 
	 * @param appEUId
	 *            application emissions unit idenitfier.
	 * @return
	 * @throws DAOException
	 */
	void removeApplicationEUDocuments(Integer appEUId) throws DAOException;

	/**
	 * Retrieve the application document record identified by appDocId.
	 * 
	 * @param appId
	 *            application identifier.
	 * @param docId
	 *            document identifier.
	 * @return
	 * @throws DAOException
	 */
	PBRNotificationDocument retrievePBRNotificationDocument(Integer appId,
			Integer docId) throws DAOException;

	/**
	 * Retrieve all application-wide documents for the PBR application
	 * identified by appId.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return
	 * @throws DAOException
	 */
	PBRNotificationDocument[] retrievePBRNotificationDocuments(Integer appId)
			throws DAOException;

	/**
	 * Create a PBRNotificationDocument object.
	 * 
	 * @param doc
	 * @return
	 * @throws DAOException
	 */
	PBRNotificationDocument createPBRNotificationDocument(
			PBRNotificationDocument doc) throws DAOException;

	/**
	 * Modify the PBRNotificationDocument doc.
	 * 
	 * @param doc
	 * @return
	 * @throws DAOException
	 */
	boolean modifyPBRNotificationDocument(PBRNotificationDocument doc)
			throws DAOException;

	/**
	 * Remove the PBRNotificationDocument identified by <code>docId</code>.
	 * 
	 * @param doc
	 *            PBRNotificationDocument to be removed.
	 * @throws DAOException
	 */
	void removePBRNotificationDocument(PBRNotificationDocument doc)
			throws DAOException;

	/**
	 * Remove the PBRNotificationDocuments associated with the application
	 * identified by <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @throws DAOException
	 */
	void removePBRNotificationDocuments(Integer appId) throws DAOException;

	/**
	 * Retrieve the application document record identified by appDocId.
	 * 
	 * @param appId
	 *            application identifier.
	 * @param docId
	 *            document identifier.
	 * @return
	 * @throws DAOException
	 */
	RPCRequestDocument retrieveRPCRequestDocument(Integer appId, Integer docId)
			throws DAOException;

	/**
	 * Retrieve all application-wide documents for the PBR application
	 * identified by appId.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return
	 * @throws DAOException
	 */
	RPCRequestDocument[] retrieveRPCRequestDocuments(Integer appId)
			throws DAOException;

	/**
	 * Create a RPCRequestDocument object.
	 * 
	 * @param doc
	 * @return
	 * @throws DAOException
	 */
	RPCRequestDocument createRPCRequestDocument(RPCRequestDocument doc)
			throws DAOException;

	/**
	 * Modify the RPCRequestDocument doc.
	 * 
	 * @param doc
	 * @return
	 * @throws DAOException
	 */
	boolean modifyRPCRequestDocument(RPCRequestDocument doc)
			throws DAOException;

	/**
	 * Remove the RPCRequestDocument identified by <code>docId</code>.
	 * 
	 * @param doc
	 *            RPCRequestDocument to be removed.
	 * @throws DAOException
	 */
	void removeRPCRequestDocument(RPCRequestDocument doc) throws DAOException;

	/**
	 * Remove the RPCRequestDocuments associated with the application identified
	 * by <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @throws DAOException
	 */
	void removeRPCRequestDocuments(Integer appId) throws DAOException;

	/**
	 * @param newAppEu
	 * @return
	 * @throws DAOException
	 */
	ApplicationEU createApplicationEU(ApplicationEU newAppEu)
			throws DAOException;

	/**
	 * @param appEu
	 * @return
	 * @throws DAOException
	 */
	boolean modifyApplicationEU(ApplicationEU appEu) throws DAOException;

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	void removeApplicationEUs(Integer appId) throws DAOException;

	/**
	 * @param appEuId
	 * @return
	 * @throws DAOException
	 */
	void removeApplicationEU(Integer appEuId) throws DAOException;

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	void removeApplicationPTIOEUs(Integer appId) throws DAOException;

	/**
	 * @param appEuId
	 * @return
	 * @throws DAOException
	 */
	void removePTIOApplicationEU(Integer appEuId) throws DAOException;

	/**
	 * @param appEuId
	 * @return
	 * @throws DAOException
	 */
	void removeTVApplicationEU(Integer appEuId) throws DAOException;

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	void removePTIOApplicationPurposeCds(Integer appId) throws DAOException;

	// void removePTIOApplicationSageGrouseCds(Integer appId) throws
	// DAOException;

	/**
	 * 
	 * @param appId
	 * @throws DAOException
	 */
	void removeTVApplicationReasonCds(Integer appId) throws DAOException;

	/**
	 * @param newPBRNotification
	 * @return
	 * @throws DAOException
	 */
	PBRNotification createPBRNotification(PBRNotification newPBRNotification)
			throws DAOException;

	/**
	 * @param pbrNotification
	 * @return
	 * @throws DAOException
	 */
	boolean modifyPBRNotification(PBRNotification pbrNotification)
			throws DAOException;

	/**
	 * Create an RPC request.
	 * 
	 * @param newRPCRequest
	 * @return
	 * @throws DAOException
	 */
	RPCRequest createRPCRequest(RPCRequest newRPCRequest) throws DAOException;

	/**
	 * Modify an RPC request.
	 * 
	 * @param rpcRequest
	 * @return
	 * @throws DAOException
	 */
	boolean modifyRPCRequest(RPCRequest rpcRequest) throws DAOException;

	/**
	 * Retrieve the type(s) of permits that may be changed given the RPC Type
	 * Code specified.
	 * 
	 * @param rpcTypeCd
	 *            RPC type code.
	 * @return associated permit type code(s).
	 * @throws DAOException
	 */
	String[] retrievePermitTypeForRPCType(String rpcTypeCd) throws DAOException;

	/**
	 * @param newRPERequest
	 * @return
	 * @throws DAOException
	 */
	RPERequest createRPERequest(RPERequest newRPERequest) throws DAOException;

	/**
	 * @param rpeRequest
	 * @return
	 * @throws DAOException
	 */
	boolean modifyRPERequest(RPERequest rpeRequest) throws DAOException;

	/**
	 * @param newRPRRequest
	 * @return
	 * @throws DAOException
	 */
	RPRRequest createRPRRequest(RPRRequest newRPRRequest) throws DAOException;

	/**
	 * @param rprRequest
	 * @return
	 * @throws DAOException
	 */
	boolean modifyRPRRequest(RPRRequest rprRequest) throws DAOException;

	/**
	 * @param
	 * @return
	 * @throws DAOException
	 */
	String generateApplicationNumber(
			Class<? extends Application> applicationClass) throws DAOException;

	/**
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	void removeApplication(int appId) throws DAOException;

	/**
	 * @param
	 * @return
	 * @throws DAOException
	 */
	void removePTIOApplication(int appId) throws DAOException;

	/**
	 * @param
	 * @return
	 * @throws DAOException
	 */
	void removeTVApplication(int appId) throws DAOException;

	void removeRPCRequest(int appId) throws DAOException;

	void removeRPERequest(int appId) throws DAOException;

	void removeRPRRequest(int appId) throws DAOException;

	void removePBRNotification(int appId) throws DAOException;

	Application[] retrieveApplicationsIn(Integer[] appIds) throws DAOException;

	/**
	 * Associate the application identified by <code>applicationId</code> with
	 * the note identified by <code>noteId</code>.
	 * 
	 * @param applicationId
	 *            application identifier.
	 * @param noteId
	 *            note identifier.
	 * @throws DAOException
	 */
	void addApplicationNote(int applicationId, int noteId) throws DAOException;

	/**
	 * Retrieve the notes associated with the application identified by
	 * <code>applicationId</code>.
	 * 
	 * @param applicationId
	 *            application identifier.
	 * @return array of <code>ApplicationNote</code> objects associated with
	 *         <code>applicationId</code>.
	 */
	ApplicationNote[] retrieveApplicationNotes(int applicationId)
			throws DAOException;

	/**
	 * Delete all application notes associated with <code>applicationId</code>.
	 * This only deletes the association between the note and the application,
	 * to delete the actual note contents, the
	 * <code>InfrastructureDAO.removeNote</code> method should be called.
	 * 
	 * @param applicationId
	 *            id of application for which notes should be deleted.
	 * @throws DAOException
	 */
	void removeApplicationNotes(int applicationId) throws DAOException;

	/**
	 * Retrieve the EU Installation/Modification purpose codes set for the
	 * application and EU defined by <code>appEUId</code>.
	 * 
	 * @param appEUId
	 *            identifier for application/EU.
	 * @return array of purpose code values.
	 * @throws DAOException
	 */
	String[] retrievePTIOEUPurposeCds(Integer appEUId) throws DAOException;

	/**
	 * Retrieve the purpose of application codes set for the application
	 * identified by <code>appId</code>.
	 * 
	 * @param appId
	 *            application id.
	 * @return array of application codes.
	 * @throws DAOException
	 */
	String[] retrievePTIOApplicationPurposeCds(Integer appId)
			throws DAOException;

	/**
	 * 
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	String[] retrieveTVApplicationReasonCds(Integer appId) throws DAOException;

	/**
	 * Remove all EU Installation/Modification purpose codes set for the
	 * application and EU defined by <code>appEUId</code>.
	 * 
	 * @param appEUId
	 *            identifier for application/EU.
	 * @throws DAOException
	 */
	void removePTIOEUPurposeCds(Integer appEUId) throws DAOException;

	/**
	 * Add <code>purposeCd</code> to the set of EU Installation/Modification
	 * purpose codes set for the application and EU defined by
	 * <code>appEUId</code>.
	 * 
	 * @param appEUId
	 *            identifier for application/EU.
	 * @param purposeCd
	 *            purpose code.
	 * @return <code>true</code> on success.
	 */
	boolean createPTIOEUPurpose(Integer appEUId, String purposeCd)
			throws DAOException;

	/**
	 * Retrieve the reason codes for federally enforceable limits for the
	 * application and EU defined by <code>appEUId</code>.
	 * 
	 * @param appEUId
	 *            identifier for application/EU.
	 * @return array of reason code values.
	 * @throws DAOException
	 */
	String[] retrievePTIOEUFedLimitReasonCds(Integer appEUId)
			throws DAOException;

	/**
	 * Remove all reason codes for federally enforceable limits set for the
	 * application and EU defined by <code>appEUId</code>.
	 * 
	 * @param appEUId
	 *            identifier for application/EU.
	 * @throws DAOException
	 */
	void removePTIOEUFedLimitReasonCds(Integer appEUId) throws DAOException;

	/**
	 * Add <code>reasonCd</code> to the set of federally enforceable limits set
	 * for the application and EU defined by <code>appEUId</code>.
	 * 
	 * @param appEUId
	 *            identifier for application/EU.
	 * @param reasonCd
	 *            reason code.
	 * @return <code>true</code> on success.
	 */
	boolean createPTIOEUFedLimitReason(Integer appEUId, String reasonCd)
			throws DAOException;

	/**
	 * Retrieve the NESHAP subpart codes for the application identified by
	 * <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return array of NESHAP code values.
	 * @throws DAOException
	 */
	String[] retrievePTIOAppNESHAPSubpartCds(Integer appId) throws DAOException;

	/**
	 * Remove all NESHAPS subpart codes for the application identified by
	 * <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @throws DAOException
	 */
	void removePTIOAppNESHAPSubpartCds(Integer appId) throws DAOException;

	/**
	 * Add <code>subpartCd</code> to the set of NESHAPS Subpart codes set for
	 * the application identified by <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @param subpartCd
	 *            NESHAPS subpart code.
	 * @return <code>true</code> on success.
	 */
	boolean addPTIOAppNESHAPSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	/**
	 * Retrieve the Part 60 NSPS subpart codes for the application identified by
	 * <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return array of Part 60 NSPS code values.
	 * @throws DAOException
	 */
	String[] retrievePTIOAppNSPSSubpartCds(Integer appId) throws DAOException;

	/**
	 * Remove all Part 60 NSPS subpart codes for the application identified by
	 * <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @throws DAOException
	 */
	void removePTIOAppNSPSSubpartCds(Integer appId) throws DAOException;

	/**
	 * Add <code>subpartCd</code> to the set of Part 60 NSPS Subpart codes set
	 * for the application identified by <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @param subpartCd
	 *            Part 60 NSPS subpart code.
	 * @return <code>true</code> on success.
	 */
	boolean addPTIOAppNSPSSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	/**
	 * Retrieve the Part 63 NESHAP subpart codes for the application identified
	 * by <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @return array of Part 63 NESHAP code values.
	 * @throws DAOException
	 */
	String[] retrievePTIOAppMACTSubpartCds(Integer appId) throws DAOException;

	/**
	 * Remove all Part 63 NESHAP subpart codes for the application identified by
	 * <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @throws DAOException
	 */
	void removePTIOAppMACTSubpartCds(Integer appId) throws DAOException;

	/**
	 * Add <code>subpartCd</code> to the set of Part 63 NESHAP Subpart codes set
	 * for the application identified by <code>appId</code>.
	 * 
	 * @param appId
	 *            application identifier.
	 * @param subpartCd
	 *            Part 63 NESHAP subpart code.
	 * @return <code>true</code> on success.
	 */
	boolean addPTIOAppMACTSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	/**
	 * Add new application EU emissions data to the database.
	 * 
	 * @param emissions
	 *            emissions data for a particular application's EU and
	 *            pollutant.
	 * @return the object created.
	 * @throws DAOException
	 */
	ApplicationEUEmissions addApplicationEUEmissions(
			ApplicationEUEmissions emissions) throws DAOException;

	/**
	 * Retrieve all emissions data for the specified application's EU.
	 * 
	 * @param appEUId
	 *            application EU identifier.
	 * @return array of emissions data.
	 * @throws DAOException
	 */
	ApplicationEUEmissions[] retrieveApplicationEUEmissions(Integer appEUId)
			throws DAOException;

	/**
	 * Remove emissions data for a given application EU and pollutant.
	 * 
	 * @param emissions
	 *            emissions object to be removed
	 * @throws DAOException
	 */
	void removeApplicationEUEmissions(ApplicationEUEmissions emissions)
			throws DAOException;

	/**
	 * Remove all emissions data for a given application EU.
	 * 
	 * @param appEUId
	 *            application EU identifier.
	 * @throws DAOException
	 */
	void removeApplicationEUEmissions(Integer appEUId) throws DAOException;

	/**
	 * Update the application EU emissions data in the database.
	 * 
	 * @param emissions
	 *            data to be updated.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 * @throws DAOException
	 */
	boolean modifyApplicationEUEmissions(ApplicationEUEmissions emissions)
			throws DAOException;

	/**
	 * Add new TV application EU emissions data to the database.
	 * 
	 * @param emissions
	 *            emissions data for a particular TV application's EU and
	 *            pollutant.
	 * @return the object created.
	 * @throws DAOException
	 */
	TVApplicationEUEmissions addTVApplicationEUEmissions(
			TVApplicationEUEmissions emissions) throws DAOException;

	/**
	 * Retrieve all TV emissions data for the specified application's EU.
	 * 
	 * @param appEUId
	 *            TV application EU identifier.
	 * @return array of emissions data.
	 * @throws DAOException
	 */
	TVApplicationEUEmissions[] retrieveTVApplicationEUEmissions(Integer appEUId)
			throws DAOException;

	/**
	 * Retrieve all TV emissions data for the specified TV application's EU and
	 * operating scenario
	 * 
	 * @param appEUId
	 *            TV application EU identifier.
	 * @param tvEuOperatingScenarioId
	 *            opearating scenario id
	 * @return array of emissions data.
	 * @throws DAOException
	 */
	TVApplicationEUEmissions[] retrieveTVApplicationEUEmissions(
			Integer appEUId, Integer tvEuOperatingScenarioId)
			throws DAOException;

	/**
	 * Remove emissions data for a given TV application EU and pollutant.
	 * 
	 * @param emissions
	 *            emissions object to be removed
	 * @throws DAOException
	 */
	void removeTVApplicationEUEmissions(TVApplicationEUEmissions emissions)
			throws DAOException;

	/**
	 * Remove all emissions data for a given application EU.
	 * 
	 * @param appEUId
	 *            application EU identifier.
	 * @throws DAOException
	 */
	void removeTVApplicationEUEmissions(Integer appEUId) throws DAOException;

	/**
	 * Remove all emissions information for the operating scenario identified by
	 * <code>appEuId</code> and <code>tvOpScenarioId</code>.
	 * 
	 * @param appEuId
	 * @param tvOpScenarioId
	 * @throws DAOException
	 */
	public void removeTVApplicationEUEmissionsForScenario(Integer appEuId,
			Integer tvOpScenarioId) throws DAOException;

	/**
	 * Update the application EU emissions data in the database.
	 * 
	 * @param emissions
	 *            data to be updated.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 * @throws DAOException
	 */
	boolean modifyTVApplicationEUEmissions(TVApplicationEUEmissions emissions)
			throws DAOException;

	/**
	 * Create new TV application EU scenario data to the database. Note: The
	 * scenario id must be set by callers of this method. The scenario id should
	 * be zero for the "normal" scenario. For alternate scenarios, the id should
	 * be obtained by calling the <code>getNextScenarioId</code> method.
	 * 
	 * @param scenario
	 *            scenario data for a particular TV application's EU.
	 * @return the object created.
	 * @throws DAOException
	 */
	public TVEUOperatingScenario createTVEUOperatingScenario(
			TVEUOperatingScenario scenario) throws DAOException;

	/**
	 * Get the Id for the next operating scenario to be added to the TV
	 * application EU. This method should only be called for a TV application EU
	 * that has at least a "normal" or base operating scenario. The scenario id
	 * for the normal operating scenario must always be zero so it can be easily
	 * identified.
	 * 
	 * @param appEuId
	 * @return
	 * @throws DAOException
	 */
	public int getNextScenarioId(Integer appEuId) throws DAOException;

	/**
	 * Get the Id for the next alternate scenario PTE requirement objecy to be
	 * added to the EU Alternate Scenario identified by appEuId and scenarioId.
	 * 
	 * @param appEuId
	 *            Application EU identifier.
	 * @param scenarioId
	 *            alternate scenario id.
	 * @return
	 * @throws DAOException
	 */
	public int getNextAltScenarioPteReqId(Integer appEuId, Integer scenarioId)
			throws DAOException;

	/**
	 * Retrieve all scenario data for the specified TV application's EU.
	 * 
	 * @param appEUId
	 *            TV application EU identifier.
	 * @return array of emissions data.
	 * @throws DAOException
	 */
	TVEUOperatingScenario[] retrieveTVEUOperatingScenarios(Integer appEUId)
			throws DAOException;

	/**
	 * Remove the specified operating scenario data for a TV application EU.
	 * 
	 * @param scenario
	 *            scenario object to be removed
	 * @throws DAOException
	 */
	void removeTVEUOperatingScenario(TVEUOperatingScenario scenario)
			throws DAOException;

	/**
	 * Remove all operating scenarios for the TV application EU identified by
	 * <code>appEuId</code>
	 * 
	 * @param appEuId
	 * @throws DAOException
	 */
	public void removeTVEUOperatingScenarios(Integer appEuId)
			throws DAOException;

	/**
	 * Update the application EU emissions data in the database.
	 * 
	 * @param emissions
	 *            data to be updated.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 * @throws DAOException
	 */
	boolean modifyTVEUOperatingScenario(TVEUOperatingScenario scenario)
			throws DAOException;

	/**
	 * 
	 * @param facilityId
	 * @param upperDate
	 * @param lowerDate
	 * @return
	 * @throws DAOException
	 */
	Integer retrieveRenewalApplications(String facilityId, Timestamp upperDate,
			Timestamp lowerDate) throws DAOException;

	/**
	 * retrieve the first application id which has the corrEpaEmuId and
	 * submitted date between submittedDateBegin and submittedDateEnd
	 * 
	 * @param corrEpaEmuId
	 * @param submittedDateBegin
	 * @param submittedDateEnd
	 * @return
	 * @throws DAOException
	 */
	Integer retrieveRenewalApplicationByEU(Integer corrEpaEmuId,
			Timestamp submittedDateBegin, Timestamp submittedDateEnd)
			throws DAOException;

	/**
	 * Update the validated flag for the application identified by
	 * <code>appId</code> setting it to <code>validated</code>.
	 * 
	 * @param appId
	 *            id of application to be modified.
	 * @param validated
	 *            value for the validated flag.
	 * @throws DAOException
	 */
	public void setApplicationValidatedFlag(Integer appId, boolean validated)
			throws DAOException;

	/**
	 * Set the submitted_date column for <code>app</code> to the current time to
	 * indicate the application has been submitted.
	 * 
	 * @param app
	 * @throws DAOException
	 */
	public void markApplicationSubmitted(Application app) throws DAOException;

	/**
	 * Get next sequence number for applicable requirements.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public int generateApplicableRequirementSeqNo() throws DAOException;

	/**
	 * Add a TVApplicableReq object to the database.
	 * 
	 * @param appReq
	 * @return
	 * @throws DAOException
	 */
	public TVApplicableReq addTVApplicableReq(TVApplicableReq appReq)
			throws DAOException;

	/**
	 * Retrieve all applicable requirement data for the specified TV
	 * application.
	 * 
	 * @param appEUId
	 *            TV application identifier.
	 * @return array of applicable requirement data.
	 * @throws DAOException
	 */
	public TVApplicableReq[] retrieveTVApplicableReqs(Integer appId)
			throws DAOException;

	/**
	 * Retrieve all applicable requirement data for the specified TV application
	 * EU.
	 * 
	 * @param appEUId
	 *            TV application EU identifier.
	 * @return array of applicable requirement data.
	 * @throws DAOException
	 */
	public TVApplicableReq[] retrieveTVApplicableReqsForEU(Integer appEUId)
			throws DAOException;

	/**
	 * Retrieve all applicable requirement data for the specified TV application
	 * EU Group.
	 * 
	 * @param groupId
	 *            TV application EU Group identifier.
	 * @return array of applicable requirement data.
	 * @throws DAOException
	 */
	public TVApplicableReq[] retrieveTVApplicableReqsForEUGroup(Integer groupId)
			throws DAOException;

	/**
	 * Remove all operating scenarios for the TV application identified by
	 * <code>appId</code>
	 * 
	 * @param appId
	 *            application id
	 * @throws DAOException
	 */
	public void removeTVApplicableReqs(Integer appId) throws DAOException;

	/**
	 * Remove all operating scenarios for the TV application EU identified by
	 * <code>appEUId</code>
	 * 
	 * @param appEUId
	 *            application EU id
	 * @throws DAOException
	 */
	public void removeTVApplicableReqsForEU(Integer appEUId)
			throws DAOException;

	/**
	 * Remove all operating scenarios for the TV application EU Group identified
	 * by <code>groupId</code>
	 * 
	 * @param groupId
	 *            application EU Group id
	 * @throws DAOException
	 */
	public void removeTVApplicableReqsForEUGroup(Integer groupId)
			throws DAOException;

	/**
	 * Create a Title V Application EU group.
	 * 
	 * @param group
	 * @return
	 * @throws DAOException
	 */
	public TVEUGroup createTvEuGroup(TVEUGroup group) throws DAOException;

	/**
	 * Retreive Title V Application EU Groups for the application identified by
	 * appId.
	 * 
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	public TVEUGroup[] retrieveTvEuGroupsForApplication(Integer appId)
			throws DAOException;

	/**
	 * Retreive Title V Application EU Group identified by groupId.
	 * 
	 * @param groupId
	 * @return
	 * @throws DAOException
	 */
	public TVEUGroup retrieveTvEuGroup(Integer groupId) throws DAOException;

	/**
	 * Modify the data in the specified Title V Application EU Group.
	 * 
	 * @param group
	 * @return
	 * @throws DAOException
	 */
	public boolean modifyTvEuGroup(TVEUGroup group) throws DAOException;

	/**
	 * Add a Title V Application EU to a group.
	 * 
	 * @param appEuId
	 * @param groupId
	 * @return
	 * @throws DAOException
	 */
	public boolean addTvEuToGroup(Integer appEuId, Integer groupId)
			throws DAOException;

	/**
	 * Remove all Title V Application EUs from the group identified by groupId.
	 * 
	 * @param groupId
	 * @throws DAOException
	 */
	public void removeTvEusFromGroup(Integer groupId) throws DAOException;

	/**
	 * Remove the Title V Application EU group identified by groupId.
	 * 
	 * @param groupId
	 * @throws DAOException
	 */
	public void removeTvEuGroup(Integer groupId) throws DAOException;

	/**
	 * Retrieve all PTE applicable requirements for the scenario identified by
	 * appEUId and tvEuOperatingScenarioId.
	 * 
	 * @param appEUId
	 *            application EU id.
	 * @param tvEuOperatingScenarioId
	 *            operating scenario id.
	 * @return
	 * @throws DAOException
	 */
	public TVAltScenarioPteReq[] retrieveTVAltScenarioPteReqsForScenario(
			Integer appEUId, Integer tvEuOperatingScenarioId)
			throws DAOException;

	/**
	 * Add PTE applicable requirements data contained in req.
	 * 
	 * @param req
	 *            PTE applicable requirements data.
	 * @return
	 * @throws DAOException
	 */
	public TVAltScenarioPteReq addTVAltScenarioPteReq(TVAltScenarioPteReq req)
			throws DAOException;

	/**
	 * Modify a PTE applicable requirements record.
	 * 
	 * @param req
	 *            PTE applicable requirements data.
	 * @return
	 * @throws DAOException
	 */
	public boolean modifyTVAltScenarioPteReq(TVAltScenarioPteReq req)
			throws DAOException;

	/**
	 * Remove all PTE applicable requirements for the emissions unit identified
	 * by appEUId.
	 * 
	 * @param appEUId
	 *            application EU id.
	 * @return
	 * @throws DAOException
	 */
	public void removeTVAltScenarioPteReqsForEu(Integer appEuId)
			throws DAOException;

	/**
	 * Remove all PTE applicable requirements for the alternate operating
	 * scenario identified by scenarioId.
	 * 
	 * @param appEUId
	 *            application EU id.
	 * @param scenarioId
	 *            alternate operating scenario id.
	 * @return
	 * @throws DAOException
	 */
	public void removeTVAltScenarioPteReqsForScenario(Integer appEuId,
			Integer scenarioId) throws DAOException;

	/**
	 * Create a Title V Facility-wide PTE record.
	 * 
	 * @param pte
	 * @return
	 * @throws DAOException
	 */
	public TVPteAdjustment addTVPteAdjustment(TVPteAdjustment pte)
			throws DAOException;

	/**
	 * Remove a Title V Facility-wide PTE record.
	 * 
	 * @param appId
	 * @throws DAOException
	 */
	public void removeTVPteAdjustmentForApplication(Integer appId)
			throws DAOException;

	/**
	 * Retrieve the Title V Facility-wide PTE records for an application.
	 * 
	 * @param appId
	 * @return
	 * @throws DAOException
	 */
	public TVPteAdjustment[] retrieveTVPteAdjustmentForApplication(Integer appId)
			throws DAOException;

	public TVCompliance addTvCompliance(TVCompliance compliance)
			throws DAOException;

	public TVCompliance[] retrieveTvComplianceForAppReq(
			Integer tvApplicableReqId) throws DAOException;

	public void removeTvComplianceForAppReq(Integer tvApplicableReqId)
			throws DAOException;

	public int getMaxComplianceId(Integer tvApplicableReqId)
			throws DAOException;

	public TVComplianceObligations addTvComplianceObligations(
			TVComplianceObligations obligations) throws DAOException;

	public TVComplianceObligations[] retrieveTvComplianceObligationsForAppReq(
			Integer tvApplicableReqId) throws DAOException;

	public void removeTvComplianceObligationsForAppReq(Integer tvApplicableReqId)
			throws DAOException;

	public int getMaxComplianceObligationsId(Integer tvApplicableReqId)
			throws DAOException;

	public TVProposedExemptions addTvProposedExemptions(
			TVProposedExemptions exemptions) throws DAOException;

	public TVProposedExemptions[] retrieveTvProposedExemptionsForAppReq(
			Integer tvApplicableReqId) throws DAOException;

	public void removeTvProposedExemptionsForAppReq(Integer tvApplicableReqId)
			throws DAOException;

	public int getMaxProposedExemptionsId(Integer tvApplicableReqId)
			throws DAOException;

	public TVProposedAltLimits addTvProposedAltLimits(
			TVProposedAltLimits altLimits) throws DAOException;

	public TVProposedAltLimits[] retrieveTvProposedAltLimitsForAppReq(
			Integer tvApplicableReqId) throws DAOException;

	public void removeTvProposedAltLimitsForAppReq(Integer tvApplicableReqId)
			throws DAOException;

	public int getMaxProposedAltLimitsId(Integer tvApplicableReqId)
			throws DAOException;

	public TVProposedTestChanges addTvProposedTestChanges(
			TVProposedTestChanges testChanges) throws DAOException;

	public TVProposedTestChanges[] retrieveTvProposedTestChangesForAppReq(
			Integer tvApplicableReqId) throws DAOException;

	public void removeTvProposedTestChangesForAppReq(Integer tvApplicableReqId)
			throws DAOException;

	public int getMaxProposedTestChangesId(Integer tvApplicableReqId)
			throws DAOException;

	public String[] retrieveApplicationPermitNumbers(int permitID)
			throws DAOException;

	/**
	 * Retrieve data for PBR report which is exported as a tab-separated file
	 * and picked up by AQD to display in a web page.
	 * 
	 * @return
	 */
	public PBRExport[] retrievePBRExport() throws DAOException;

	public int retrieveAttachmentReferenceCount(int applicationDocumentId,
			int documentId) throws DAOException;

	/**
	 * Retrieve an array of application_eu_id's for EUs that are associated with
	 * an application, but are not included in the facility inventory that
	 * corresponds to the application. These "orphan" EUs are created when a
	 * user adds an EU to the facility inventory while an application is
	 * associated with that profile and then deletes the EU from the profile.
	 * This causes EU information to remain in the application since deleting it
	 * from the facility inventory does not automatically cause the data to be
	 * deleted from the application. See Mantis Issue #2906.
	 * 
	 * @param applicationId
	 * @return
	 * @throws DAOException
	 */
	public Integer[] retrieveOrphanEus(int applicationId) throws DAOException;

	/**
	 * Retrieves an array of NSR-specific best available control technologies
	 * for various emissions.
	 * 
	 * @param appEUId
	 * @return
	 * @throws DAOException
	 */
	NSRApplicationBACTEmission[] retrieveNSRApplicationBactEmissions(
			Integer appEUId) throws DAOException;

	/**
	 * Removes all BACT emissions with the given application EU id.
	 * 
	 * @param applicationEuId
	 * @throws DAOException
	 */
	void removeBACTEmissions(Integer applicationEuId) throws DAOException;

	/**
	 * Adds a new BACT emission to database.
	 * 
	 * @param bactEmission
	 * @return
	 * @throws DAOException
	 */
	NSRApplicationBACTEmission addBACTEmission(
			NSRApplicationBACTEmission bactEmission) throws DAOException;

	/**
	 * Retrieves an array of NSR-specific best available control technologies
	 * for various emissions.
	 * 
	 * @param appEUId
	 * @return
	 * @throws DAOException
	 */
	NSRApplicationLAEREmission[] retrieveNSRApplicationLaerEmissions(
			Integer appEUId) throws DAOException;

	/**
	 * Removes all LAER emissions with the given application EU id.
	 * 
	 * @param applicationEuId
	 * @throws DAOException
	 */
	void removeLAEREmissions(Integer applicationEuId) throws DAOException;

	/**
	 * Adds a new LAER emission to database.
	 * 
	 * @param laerEmission
	 * @return
	 * @throws DAOException
	 */
	NSRApplicationLAEREmission addLAEREmission(
			NSRApplicationLAEREmission laerEmission) throws DAOException;

	boolean checkApplicationTypeIsInDb(Integer applicationId,
			Integer emissionUnitId, String typeCd, boolean isRequired)
			throws DAOException;

	boolean removeReqruiedAttachmnetApplicationDoc(Integer applicationId,
			Integer appEuId, String typeCd) throws DAOException;

	boolean changeReqruiedAttachmnetToOption(Integer applicationId,
			Integer appEuId, String typeCd) throws DAOException;

	Integer retrieveRequiredApplicationDocId(Integer applicationId,
			Integer appEuId, String typeCd) throws DAOException;

	void updateApplicationValidatedFlagByAppDocId(Integer applicationDocId,
			boolean validatedFlag) throws DAOException;

	List<FacilityWideRequirement> retrieveFacilityWideRequirements(
			Integer applicationId) throws DAOException;

	void createFacilityWideRequirement(FacilityWideRequirement facWideReq)
			throws DAOException;

	void modifyFacilityWideRequirement(FacilityWideRequirement facWideReq)
			throws DAOException;

	void removeFacilityWideRequirement(Integer requirementId)
			throws DAOException;

	String[] retrieveTvEuAppNSPSSubpartCds(Integer appId) throws DAOException;

	String[] retrieveTvEuAppNESHAPSubpartCds(Integer appId) throws DAOException;

	void removeTvEuAppNSPSSubpartCds(Integer appId) throws DAOException;

	String[] retrieveTvEuAppMACTSubpartCds(Integer appId) throws DAOException;

	void removeTvEuAppNESHAPSubpartCds(Integer appId) throws DAOException;

	void removeTvEuAppMACTSubpartCds(Integer appId) throws DAOException;

	boolean addTvEuAppNSPSSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	boolean addTvEuAppNESHAPSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	boolean addTvEuAppMACTSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	boolean addTvAppMACTSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	boolean addTvAppNESHAPSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	boolean addTvAppNSPSSubpartCd(Integer appId, String subpartCd)
			throws DAOException;

	void removeTvAppMACTSubpartCds(Integer appId) throws DAOException;

	void removeTvAppNESHAPSubpartCds(Integer appId) throws DAOException;

	void removeTvAppNSPSSubpartCds(Integer appId) throws DAOException;

	List<String> retrieveTvAppMACTSubpartCds(Integer appId) throws DAOException;

	List<String> retrieveTvAppNESHAPSubpartCds(Integer appId)
			throws DAOException;

	List<String> retrieveTvAppNSPSSubpartCds(Integer appId) throws DAOException;

	/**
	 * Retrieves pollutant limits for a given application EU
	 * 
	 * @param applicationEuId
	 *            Id of the application EU that may or may not contain pollutant
	 *            limits
	 * @return TVApplicationEUPollutantLimits
	 * @throws DAOException
	 */
	TVEUPollutantLimit[] retrieveTVEUPollutantLimits(Integer applicationEuId)
			throws DAOException;

	/**
	 * Removes all pollutant limits from a given application EU.
	 * 
	 * @param appEUId
	 *            Id of application EU that will have pollutant limits removed
	 * @throws DAOException
	 */
	void removeTVEUPollutantLimits(Integer appEUId) throws DAOException;

	/**
	 * Adds a pollutant limit to the given the application EU.
	 * 
	 * @param appEuId
	 *            Id of the application where the pollutant limit is being added
	 *            to
	 * @param pollutantLimit
	 *            Pollutant limit being added
	 * @return TVApplicationEUPollutantLimits
	 * @throws DAOException
	 */
	TVEUPollutantLimit addTVEUPollutantLimit(Integer appEuId,
			TVEUPollutantLimit pollutantLimit) throws DAOException;

	/**
	 * Adds an operational restriction to the given application EU
	 * 
	 * @param appEuId
	 *            Id of the application where the operational restriction is
	 *            being added to
	 * @param operationalRestriction
	 *            Operational restriction being added
	 * @return TVEUOperationalRestriction
	 * @throws DAOException
	 */
	TVEUOperationalRestriction addTVEUOperationalRestriction(Integer appEuId,
			TVEUOperationalRestriction operationalRestriction)
			throws DAOException;

	/**
	 * Removes all operational restrictions from the given application EU.
	 * 
	 * @param appEUId
	 *            Id of the application EU in which its operational restrictions
	 *            are being removed.
	 * @throws DAOException
	 */
	void removeTVEUOperationalRestrictions(Integer appEUId) throws DAOException;

	/**
	 * Retrieves all operation restrictions from the given application EU.
	 * 
	 * @param appEUId
	 *            The id of application EU which may or may not contain
	 *            operational restrictions.
	 * @return TVEUOperationalRestriction
	 * @throws DAOException
	 */
	TVEUOperationalRestriction[] retrieveTVEUOperationalRestrictions(
			Integer appEUId) throws DAOException;

	boolean changeOptionAttachmnetToRequired(Integer applicationId,
			Integer appEuId, String typeCd) throws DAOException;

	ArrayList<ApplicationDocumentTypeDef> retrieveApplicationDocumentTypes()
			throws DAOException;

	ArrayList<TVApplicationDocumentTypeDef> retrieveTvApplicationDocumentTypes()
			throws DAOException;

	void setActiveApplicationsValidatedFlag(Integer fpId, boolean validated)
			throws DAOException;
	List<ApplicationEUFugitiveLeaks> retrieveApplicationEUFugitiveLeaks(Integer appEUId)
			throws DAOException;

	ApplicationEUFugitiveLeaks createApplicationEUFugitiveLeaks(
			ApplicationEUFugitiveLeaks applicationEUFugitiveLeaks)
			throws DAOException;

	void removeApplicationEUFugitiveLeaks(Integer appEUId) throws DAOException;
	
	ApplicationEUMaterialUsed addApplicationEUMaterialUsed(
			ApplicationEUMaterialUsed materialUsed) throws DAOException ;
	
	boolean modifyApplicationEUMaterialUsed(
			ApplicationEUMaterialUsed materialUsed) throws DAOException;
	
	void removeApplicationEUMaterialUsed(Integer appEUId)
			throws DAOException;
	ApplicationEUMaterialUsed[] retrieveApplicationEUMaterialUsed(
			Integer appEUId) throws DAOException;
	

	ApplicationDocumentTypeDef retrieveApplicationDocTypeDef(String applicationDocumentTypeCD)
			throws DAOException;

	void setApplicationEUValidatedFlag(Integer applicationEUId, boolean b) throws DAOException;

	ApplicationDocumentTypeDef retrieveTVApplicationDocTypeDef(
			String applicationDocumentTypeCD) throws DAOException;

	public void removeFacilityWideRequirements(Integer appId) throws DAOException;

	Boolean applicationExists(TimeSheetRow nsrTimeSheetRow) throws DAOException;
	
	/**
	 * Retrieve the unique Application Installation/Modification purpose codes set for the
	 * application defined by <code>applicationId</code>.
	 * 
	 * @param applicationId
	 *            identifier for application
	 * @return array of purpose code values.
	 * @throws DAOException
	 */
	String[] retrieveUniquePTIOPurposeCds(Integer applicationId) throws DAOException;
	
	/**
	 * Retrieve a list of Modification Description values for the
	 * application defined by <code>applicationId</code>.
	 * 
	 * @param applicationId
	 *            identifier for application
	 * @return array of modification description values.
	 * @throws DAOException
	 */
	String[] retrieveModificationDescList(Integer applicationId) throws DAOException;
	
	/**
	 * Retrieve the pte SUM for given pollutant defined by <code>pollutant</code> and 
	 * application defined by <code>applicationId</code>.
	 * 
	 * @param applicationId
	 *            identifier for application
	 * @param pollutantCd
	 *            identifier for pollutantCd
	 * @return String containing SUM of pte_tons_yr for given pollutant and application
	 * 		across all included and active EUs.
	 * @throws DAOException
	 */
	String retrieveTVApplicationEUEmissionsForPollutant(
			Integer applicationId, String pollutantCd, String euEmissionTableCd)
			throws DAOException; 
	
	String[] retrieveApplicationHAPPollutantList(Integer appId)
			throws DAOException;
	
	String[] retrieveApplicationGHGPollutantList(Integer appId)
			throws DAOException;
	
	String[] retrieveApplicationOTHPollutantList(Integer appId)
			throws DAOException;
	
	/**
	 * @param tradeSecretDocId
	 * @return ApplicationDocument
	 * @throws DAOException
	 */
	ApplicationDocument retrieveApplicationDocumentByTradeSecrectDocId(Integer tradeSecretDocId) throws DAOException;
	
	/**
	 * @param applicationId
	 * @return Submitted date of this application. Null if the application is not submitted.
	 * @throws DAOException
	 */
	Timestamp retrieveApplicationSubmittedDate(Integer applicationId) throws DAOException;
	
	/**
	 * 
	 * @param applicationId
	 * @return Application
	 * @throws DAOException
	 */
	Application retrieveBasicApplicationById(Integer applicationId) throws DAOException;
	
	/**
	 * 
	 * @param applicationNbr
	 * @return Application
	 * @throws DAOException
	 */
	Application retrieveBasicApplicationByNbr(String applicationNbr) throws DAOException;
}
