package us.oh.state.epa.stars2.database.dao.application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUFugitiveLeaks;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUMaterialUsed;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationNote;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
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
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
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
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.TVApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;

/**
 * @author Pmontoto
 * 
 */
@Repository
public class ApplicationSQLDAO extends AbstractDAO implements ApplicationDAO {
	
	private Logger logger = Logger.getLogger(ApplicationSQLDAO.class);

	
	/**
	 * @see ApplicationDAO#retrieveApplication(Integer applicationId)
	 */
	public final Application retrieveApplication(Integer applicationId)
			throws DAOException {
		Application ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		checkNull(applicationId);

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ApplicationSQL.retrieveApplication"));
			pStmt.setInt(1, applicationId);
			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = populateApplication(rs);
			}
		} catch (Exception e) {
			logger.error("applicationId=" + applicationId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	public final Application retrievePortalRelocationApplication(
			Integer applicationId) throws DAOException {
		Application ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		checkNull(applicationId);

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ApplicationSQL.retrievePortalRelocationApplication"));
			pStmt.setInt(1, applicationId);
			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = populateApplication(rs);
			}
		} catch (Exception e) {
			logger.error("applicationId=" + applicationId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see ApplicationDAO#retrieveApplication(String applicationNumber)
	 */
	public final Application retrieveApplication(String applicationNumber)
			throws DAOException {
		Application ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ApplicationSQL.retrieveApplicationByNumber"));

			pStmt.setString(1, applicationNumber);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = populateApplication(rs);
			}
		} catch (Exception e) {
			logger.error("applicationNumer=" + applicationNumber, e);
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @see ApplicationDAO#retrieveApplication(Integer applicationId)
	 */
	public final SimpleDef[] retrieveApplicationTypes() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationTypes", true);

		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see ApplicationDAO#retrievePTIOApplicationPurposeCds(Integer appId)
	 */
	public final String[] retrievePTIOApplicationPurposeCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePTIOApplicationPurposeCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<? extends Object> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#retrieveTVApplicationReasonCds(Integer appId)
	 */
	public final String[] retrieveTVApplicationReasonCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicationReasonCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<? extends Object> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#removePTIOApplicationPurposeCds(Integer appId)
	 */
	public final void removePTIOApplicationPurposeCds(Integer appId)
			throws DAOException {
		logger.trace("DLTRACE --> removePTIOApplicationPurposeCds");
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePTIOApplicationPurposeCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeTVApplicationReasonCds(Integer appId)
	 */
	public final void removeTVApplicationReasonCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVApplicationReasonCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#searchApplications(String applicationNumber, String
	 *      facilityId, String facilityName, String doLaaCd, String countyCd,
	 *      String applicationType)
	 */
	public final Application[] searchApplications(String applicationNumber,
			String facilityId, String facilityName, String doLaaCd,
			String countyCd, String applicationType) throws DAOException {

		String statementSQL = loadSQL("ApplicationSQL.findApplications");

		StringBuffer whereClause = new StringBuffer("");
		String where = " WHERE ";

		if (applicationNumber != null && applicationNumber.trim().length() > 0) {
			if (whereClause.length() != 0) {
				whereClause.append(" AND ");
			} else {
				whereClause.append(where);
			}

			whereClause.append(" LOWER(application_nbr) LIKE ");
			whereClause.append("LOWER('");
			whereClause
					.append(SQLizeString(applicationNumber.replace("*", "%")));
			whereClause.append("')");
		}

		if (facilityId != null && facilityId.trim().length() > 0) {
			if (whereClause.length() != 0) {
				whereClause.append(" AND ");
			} else {
				whereClause.append(where);
			}

			whereClause.append(" LOWER(facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityId.replace("*", "%")));
			whereClause.append("')");
		}

		if (facilityName != null && facilityName.trim().length() > 0) {
			if (whereClause.length() != 0) {
				whereClause.append(" AND ");
			} else {
				whereClause.append(where);
			}

			whereClause.append(" LOWER(facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.replace("*", "%")));
			whereClause.append("')");
		}

		if (applicationType != null) {
			if (whereClause.length() != 0) {
				whereClause.append(" AND ");
			} else {
				whereClause.append(where);
			}

			whereClause.append(" application_type_cd = '");
			whereClause.append(applicationType);
			whereClause.append("'");
		}

		if (doLaaCd != null) {
			if (whereClause.length() != 0) {
				whereClause.append(" AND ");
			} else {
				whereClause.append(where);
			}

			whereClause.append(" do_laa_cd = '");
			whereClause.append(doLaaCd);
			whereClause.append("'");
		}

		if (countyCd != null) {
			if (whereClause.length() != 0) {
				whereClause.append(" AND ");
			} else {
				whereClause.append(where);
			}

			whereClause.append(" county_cd = '");
			whereClause.append(countyCd);
			whereClause.append("'");
		}

		// sort by descending submitted date, forcing values with null
		// submitted date to the end of the list
		StringBuffer sortBy = new StringBuffer(
				" ORDER BY received_date, submitted_date, application_nbr");

		statementSQL += whereClause.toString() + " " + sortBy.toString();

		ArrayList<Application> ret = new ArrayList<Application>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn.prepareStatement(statementSQL);

			ResultSet rs = pStmt.executeQuery();
			int i = 0;
			while (rs.next()) {
				Application app = populateApplication(rs);
				// // add individual county codes for each address
				// app.getCountyCds().add(rs.getString("county_cd"));
				// app.getCountyNms().add(rs.getString("county_nm"));
				ret.add(app);
				i++;
				if ((defaultSearchLimit > 0) && (i >= defaultSearchLimit)) {
					break;
				}
			}
		} catch (Exception e) {
			logger.error("WHERE_CLAUSE: " + whereClause.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret.toArray(new Application[0]);
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationEmissionUnit(Integer appEuId)
	 */
	public final ApplicationEU retrieveApplicationEmissionUnit(Integer appEuId)
			throws DAOException {

		ApplicationEU ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			pStmt = conn
					.prepareStatement(loadSQL("ApplicationSQL.retrieveApplicationEmissionUnit"));

			pStmt.setInt(1, appEuId);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				rs.getInt("is_ptio_eu");
				if (!rs.wasNull()) {
					ret = new PTIOApplicationEU();
					ret.populate(rs);
				} else {
					ret = new TVApplicationEU();
					ret.populate(rs);
				}
				rs.close();
			}
		} catch (Exception e) {
			logger.error("appEuId=" + appEuId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return ret;
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationEmissionUnits(Integer appId)
	 */
	public final ApplicationEU[] retrieveApplicationEmissionUnits(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationEmissionUnits", true);

		connHandler.setInteger(1, appId);

		ArrayList<ApplicationEU> tempArray = connHandler
				.retrieveArray(ApplicationEU.class);

		return tempArray.toArray(new ApplicationEU[0]);
	}

	/**
	 * @see ApplicationDAO#retrievePTIOApplicationEmissionUnits(Integer appId)
	 */
	public final PTIOApplicationEU[] retrievePTIOApplicationEmissionUnits(
			Integer appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePTIOApplicationEmissionUnits", true);

		connHandler.setInteger(1, appId);

		ArrayList<PTIOApplicationEU> tempArray = connHandler
				.retrieveArray(PTIOApplicationEU.class);

		return tempArray.toArray(new PTIOApplicationEU[0]);
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationEmissionUnits(Integer appId)
	 */
	public final Permit[] retrievePermitsForApplication(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationPermits", true);

		connHandler.setInteger(1, appId);

		ArrayList<Permit> tempArray = connHandler.retrieveArray(Permit.class);

		return tempArray.toArray(new Permit[0]);
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationDocument(Integer appDocId)
	 */
	public final ApplicationDocumentRef retrieveApplicationDocument(
			Integer appDocId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocument", true);

		connHandler.setInteger(1, appDocId);

		return (ApplicationDocumentRef) connHandler
				.retrieve(ApplicationDocumentRef.class);
	}
	
	/**
	 * @see ApplicationDAO#retrieveNSRApplicationDocument(Integer appDocId)
	 */
	public final NSRApplicationDocumentRef retrieveNSRApplicationDocument(
			Integer appDocId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocument", true);

		connHandler.setInteger(1, appDocId);

		return (NSRApplicationDocumentRef) connHandler
				.retrieve(NSRApplicationDocumentRef.class);
	}
	
	/**
	 * @see ApplicationDAO#retrieveTVApplicationDocument(Integer appDocId)
	 */
	public final TVApplicationDocumentRef retrieveTVApplicationDocument(
			Integer appDocId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocument", true);

		connHandler.setInteger(1, appDocId);

		return (TVApplicationDocumentRef) connHandler
				.retrieve(TVApplicationDocumentRef.class);
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationDocuments(Integer appId)
	 */
	public final ApplicationDocumentRef[] retrieveApplicationDocuments(
			Integer appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocuments", true);

		connHandler.setInteger(1, appId);

		ArrayList<ApplicationDocumentRef> tempArray = connHandler
				.retrieveArray(ApplicationDocumentRef.class);

		return tempArray.toArray(new ApplicationDocumentRef[0]);
	}
	
	/**
	 * @see ApplicationDAO#retrieveNSRApplicationDocuments(Integer appId)
	 */
	public final NSRApplicationDocumentRef[] retrieveNSRApplicationDocuments(
			Integer appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocuments", true);

		connHandler.setInteger(1, appId);

		ArrayList<NSRApplicationDocumentRef> tempArray = connHandler
				.retrieveArray(NSRApplicationDocumentRef.class);

		return tempArray.toArray(new NSRApplicationDocumentRef[0]);
	}
	
	/**
	 * @see ApplicationDAO#retrieveTVApplicationDocuments(Integer appId)
	 */
	public final TVApplicationDocumentRef[] retrieveTVApplicationDocuments(
			Integer appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocuments", true);

		connHandler.setInteger(1, appId);

		ArrayList<TVApplicationDocumentRef> tempArray = connHandler
				.retrieveArray(TVApplicationDocumentRef.class);

		return tempArray.toArray(new TVApplicationDocumentRef[0]);
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationEUDocuments(Integer appId)
	 */
	public final ApplicationDocumentRef[] retrieveApplicationEUDocuments(
			Integer appEuId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationEUDocuments", true);

		connHandler.setInteger(1, appEuId);

		ArrayList<ApplicationDocumentRef> tempArray = connHandler
				.retrieveArray(ApplicationDocumentRef.class);

		return tempArray.toArray(new ApplicationDocumentRef[0]);
	}

	/**
	 * @see ApplicationDAO#retrieveNSRApplicationEUDocuments(Integer appId)
	 */
	public final NSRApplicationDocumentRef[] retrieveNSRApplicationEUDocuments(
			Integer appEuId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationEUDocuments", true);

		connHandler.setInteger(1, appEuId);

		ArrayList<NSRApplicationDocumentRef> tempArray = connHandler
				.retrieveArray(NSRApplicationDocumentRef.class);

		return tempArray.toArray(new NSRApplicationDocumentRef[0]);
	}
	
	/**
	 * @see ApplicationDAO#retrieveTVApplicationEUDocuments(Integer appId)
	 */
	public final TVApplicationDocumentRef[] retrieveTVApplicationEUDocuments(
			Integer appEuId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationEUDocuments", true);

		connHandler.setInteger(1, appEuId);

		ArrayList<TVApplicationDocumentRef> tempArray = connHandler
				.retrieveArray(TVApplicationDocumentRef.class);

		return tempArray.toArray(new TVApplicationDocumentRef[0]);
	}
	
	/**
	 * @see ApplicationDAO#createApplication(Application newApp)
	 */
	public final Application createApplication(Application newApp)
			throws DAOException {
		checkNull(newApp);
		Application ret = newApp;

		ret.setApplicationID(nextSequenceVal("PA_Application_Id",
				ret.getApplicationID()));

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createApplication", false);

		int i = 1;
		connHandler.setInteger(i++, newApp.getApplicationID());
		connHandler.setString(i++, newApp.getApplicationTypeCD());
		connHandler.setInteger(i++, newApp.getFacility().getFpId());

		if (newApp.getContact() != null) {
			connHandler.setInteger(i++, newApp.getContact().getContactId());
		} else {
			connHandler.setInteger(i++, null);
		}

		connHandler.setString(i++, newApp.getPreviousApplicationNumber());
		connHandler.setString(i++, newApp.getApplicationNumber());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(newApp.getValidated()));
		connHandler.setTimestamp(i++, newApp.getReceivedDate());
		connHandler.setTimestamp(i++, newApp.getSubmittedDate());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(newApp.isLegacy()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newApp.isApplicationAmended()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newApp.isApplicationCorrected()));
		connHandler.setString(i++, newApp.getApplicationCorrectedReason());
		connHandler.setString(i++, newApp.getApplicationDesc());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyApplication(Application app)
	 */
	public final boolean modifyApplication(Application app) throws DAOException {
		checkNull(app);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyApplication", false);

		int i = 1;
		if (app.getContact() != null) {
			connHandler.setInteger(i++, app.getContact().getContactId());
		} else {
			connHandler.setInteger(i++, null);
		}
		connHandler.setString(i++, app.getPreviousApplicationNumber());
		connHandler.setString(i++, app.getApplicationNumber());
		connHandler.setInteger(i++, app.getFacility().getFpId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(app.getValidated()));
		connHandler.setTimestamp(i++, app.getReceivedDate());
		connHandler.setTimestamp(i++, app.getSubmittedDate());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(app
				.isApplicationAmended()));
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(app
				.isApplicationCorrected()));
		connHandler.setString(i++, app.getApplicationCorrectedReason());
		connHandler.setString(i++, app.getApplicationDesc());
		connHandler.setString(i++, app.getGeneralPermit());
		connHandler.setInteger(i++, app.getLastModified() + 1);
		connHandler.setInteger(i++, app.getApplicationID());
		connHandler.setInteger(i++, app.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createPTIOApplicationPurpose(Integer appId, String
	 *      purposeCd)
	 */
	public final boolean createPTIOApplicationPurpose(Integer appId,
			String purposeCd) throws DAOException {
		checkNull(appId);
		checkNull(purposeCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createPTIOApplicationPurpose", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, purposeCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createPTIOApplicationPurpose(Integer appId, String
	 *      purposeCd)
	 */
	public final boolean createPTIOApplicationSageGrouse(Integer appId,
			String sageGrouseCd) throws DAOException {
		checkNull(appId);
		checkNull(sageGrouseCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createPTIOApplicationSageGrouseCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, sageGrouseCd);

		return connHandler.update();
	}

	public final boolean createTVApplicationReason(Integer appId,
			String reasonCd) throws DAOException {
		checkNull(appId);
		checkNull(reasonCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createTVApplicationReason", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, reasonCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createPTIOApplication(PTIOApplication newPTIOApp)
	 */
	public final PTIOApplication createPTIOApplication(
			PTIOApplication newPTIOApp) throws DAOException {
		checkNull(newPTIOApp);
		PTIOApplication ret = newPTIOApp;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createPTIOApplication", false);

		int i = 1;
		connHandler.setInteger(i++, newPTIOApp.getApplicationID());
		connHandler.setString(i++, newPTIOApp.getOtherPurposeDesc());
		connHandler.setString(i++, newPTIOApp.getGeneralPermitTypeCD());
		connHandler.setString(i++, newPTIOApp.getRequestedPERDueDateCD());
		connHandler.setString(i++, newPTIOApp.getChangedPERDueDateCD());
		connHandler.setString(i++, newPTIOApp.getNspsApplicableFlag());
		connHandler.setString(i++, newPTIOApp.getNeshapApplicableFlag());
		connHandler.setString(i++, newPTIOApp.getMactApplicableFlag());
		connHandler.setString(i++, newPTIOApp.getGhgApplicableFlag());
		connHandler.setString(i++, newPTIOApp.getPsdApplicableFlag());
		connHandler.setString(i++, newPTIOApp.getNsrApplicableFlag());
		connHandler.setString(i++, newPTIOApp.getRiskManagementPlanFlag());
		connHandler.setString(i++, newPTIOApp.getTitleIVFlag());
		connHandler.setString(i++,
				newPTIOApp.getFederalRuleApplicabilityExplanation());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newPTIOApp.isQualifyExpress()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newPTIOApp.isRequestExpress()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newPTIOApp.isTradeSecret()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newPTIOApp.isLegacyStatePTOApp()));
		connHandler.setString(i++, newPTIOApp.getPotentialTitleVFlag());
		connHandler.setString(i++, newPTIOApp.getSageGrouseCd());
		connHandler.setString(i++, newPTIOApp.getSageGrouseAgencyName());

		connHandler.setString(i++, newPTIOApp.getLandUsePlanningFlag());
		connHandler.setString(i++, newPTIOApp.getFacilityChangedLocationFlag());
		connHandler.setString(i++, newPTIOApp.getContainH2SFlag());
		connHandler.setString(i++, newPTIOApp.getDivisionContacedFlag());
		connHandler.setString(i++, newPTIOApp.getModelingContactFlag());
		connHandler.setString(i++, newPTIOApp.getModelingAnalysisFlag());
		connHandler.setString(i++, newPTIOApp.getPreventionPsdFlag());
		connHandler.setString(i++, newPTIOApp.getPreAppMeetingFlag());
		connHandler.setString(i++, newPTIOApp.getModelingProtocolSubmitFlag());
		connHandler.setString(i++, newPTIOApp.getAqrvAnalysisSubmitFlag());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newPTIOApp.isKnownIncompleteNSRApp()));

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyPTIOApplication(PTIOApplication ptioApp)
	 */
	public final boolean modifyPTIOApplication(PTIOApplication ptioApp)
			throws DAOException {
		checkNull(ptioApp);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyPTIOApplication", false);

		int i = 1;
		connHandler.setString(i++, ptioApp.getOtherPurposeDesc());
		connHandler.setString(i++, ptioApp.getGeneralPermitTypeCD());
		connHandler.setString(i++, ptioApp.getRequestedPERDueDateCD());
		connHandler.setString(i++, ptioApp.getChangedPERDueDateCD());
		connHandler.setString(i++, ptioApp.getNspsApplicableFlag());
		connHandler.setString(i++, ptioApp.getNeshapApplicableFlag());
		connHandler.setString(i++, ptioApp.getMactApplicableFlag());
		connHandler.setString(i++, ptioApp.getGhgApplicableFlag());
		connHandler.setString(i++, ptioApp.getPsdApplicableFlag());
		connHandler.setString(i++, ptioApp.getNsrApplicableFlag());
		connHandler.setString(i++, ptioApp.getRiskManagementPlanFlag());
		connHandler.setString(i++, ptioApp.getTitleIVFlag());
		connHandler.setString(i++,
				ptioApp.getFederalRuleApplicabilityExplanation());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(ptioApp.isQualifyExpress()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(ptioApp.isRequestExpress()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(ptioApp.isTradeSecret()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(ptioApp.isLegacyStatePTOApp()));
		connHandler.setString(i++, ptioApp.getLandUsePlanningFlag());
		connHandler.setString(i++, ptioApp.getFacilityChangedLocationFlag());
		connHandler.setString(i++, ptioApp.getPotentialTitleVFlag());

		connHandler.setString(i++, ptioApp.getContainH2SFlag());
		connHandler.setString(i++, ptioApp.getDivisionContacedFlag());

		connHandler.setString(i++, ptioApp.getSageGrouseCd());
		connHandler.setString(i++, ptioApp.getSageGrouseAgencyName());

		connHandler.setString(i++, ptioApp.getModelingContactFlag());
		connHandler.setString(i++, ptioApp.getModelingAnalysisFlag());
		connHandler.setString(i++, ptioApp.getPreventionPsdFlag());
		connHandler.setString(i++, ptioApp.getPreAppMeetingFlag());
		connHandler.setString(i++, ptioApp.getModelingProtocolSubmitFlag());
		connHandler.setString(i++, ptioApp.getAqrvAnalysisSubmitFlag());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(ptioApp.isKnownIncompleteNSRApp()));

		connHandler.setInteger(i++, ptioApp.getLastModified() + 1);
		connHandler.setInteger(i++, ptioApp.getApplicationID());
		connHandler.setInteger(i++, ptioApp.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#modifyPTIOApplication(PTIOApplication ptioApp)
	 */
	public final boolean modifyPTIOApplicationEU(PTIOApplicationEU ptioAppEu)
			throws DAOException {
		checkNull(ptioAppEu);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyPTIOApplicationEU", false);

		int i = 1;
		connHandler.setTimestamp(i++, ptioAppEu.getWorkStartDate());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ptioAppEu
						.isWorkStartAfterPermit()));
		connHandler.setTimestamp(i++, ptioAppEu.getOperationBeginDate());
		connHandler.setString(i++, ptioAppEu.getReconstructionDesc());
		connHandler.setInteger(i++, ptioAppEu.getShutdownYears());
		connHandler.setString(i++, ptioAppEu.getRequestingFederalLimitsFlag());
		connHandler.setString(i++, ptioAppEu.getFederalLimitsOtherReasonDesc());
		connHandler.setString(i++, ptioAppEu.getModificationDesc());
		connHandler.setString(i++, ptioAppEu.getGeneralPermitTypeCd());
		connHandler.setString(i++, ptioAppEu.getModelGeneralPermitCd());
		connHandler.setString(i++, ptioAppEu.getBactFlag());
		connHandler.setString(i++, ptioAppEu.getLaerFlag());
		connHandler.setString(i++, ptioAppEu.getGhgApplicableFlag());
		connHandler.setString(i++, ptioAppEu.getNspsApplicableFlag());
		connHandler.setString(i++, ptioAppEu.getNeshapApplicableFlag());
		connHandler.setString(i++, ptioAppEu.getMactApplicableFlag());
		connHandler.setString(i++, ptioAppEu.getPsdApplicableFlag());
		connHandler.setString(i++, ptioAppEu.getNsrApplicableFlag());
		connHandler.setString(i++, ptioAppEu.getRiskManagementPlanFlag());
		connHandler.setString(i++, ptioAppEu.getTitleIVFlag());
		connHandler.setString(i++,
				ptioAppEu.getFederalRuleApplicabilityExplanation());
		connHandler.setString(i++, ptioAppEu.getPsdBACTFlag());
		connHandler.setString(i++, ptioAppEu.getNsrLAERFlag());

		connHandler.setInteger(i++, ptioAppEu.getOpSchedHrsDay());
		connHandler.setInteger(i++, ptioAppEu.getOpSchedHrsYr());

		connHandler.setInteger(i++, ptioAppEu.getLastModified() + 1);
		connHandler.setInteger(i++, ptioAppEu.getApplicationEuId());
		connHandler.setInteger(i++, ptioAppEu.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createTVApplication(TVApplication newTVApp)
	 */
	public final TVApplication createTVApplication(TVApplication newTVApp)
			throws DAOException {
		checkNull(newTVApp);
		TVApplication ret = newTVApp;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createTVApplication", false);

		int i = 1;
		connHandler.setInteger(i++, newTVApp.getApplicationID());
		connHandler.setString(i++, newTVApp.getTvApplicationPurposeCd());
		connHandler.setString(i++, newTVApp.getOperationsDsc());
		connHandler.setString(i++, newTVApp.getSubjectTo112R());
		connHandler.setString(i++, newTVApp.getPlanSubmittedUnder112R());
		connHandler.setString(i++, newTVApp.getSubjectToTIV());
		connHandler.setString(i++, newTVApp.getAqdTvApplicationNumber());
		connHandler.setString(i++, newTVApp.getNspsApplicableFlag());
		connHandler.setString(i++, newTVApp.getNeshapApplicableFlag());
		connHandler.setString(i++, newTVApp.getMactApplicableFlag());
		connHandler.setString(i++, newTVApp.getPsdApplicableFlag());
		connHandler.setString(i++, newTVApp.getNsrApplicableFlag());
		connHandler.setString(i++, newTVApp.getFacilityWideRequirementFlag());
		connHandler.setString(i++, newTVApp.getProposedExemptions());
		connHandler.setString(i++, newTVApp.getAmbientMonitoring());
		connHandler.setString(i++, newTVApp.getAlternateOperatingScenarios());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newTVApp.isLegacyStateTVApp()));
		connHandler.setTimestamp(i++, newTVApp.getRiskManagementPlanSubmitDate());
		connHandler.setString(i++, newTVApp.getComplianceCertSubmitFrequency());
		connHandler.setString(i++, newTVApp.getComplianceWithApplicableEnhancedMonitoring());
		connHandler.setString(i++, newTVApp.getComplianceRequirementsNotMet());
		connHandler.setString(i++, newTVApp.getSubjectToEngineConfigRestrictions());
		connHandler.setString(i++, newTVApp.getNsrPermitNumber());
		connHandler.setString(i++, newTVApp.getSubjectToWAQSR());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyTVApplication(TVApplication tvApp)
	 */
	public final boolean modifyTVApplication(TVApplication tvApp)
			throws DAOException {
		checkNull(tvApp);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyTVApplication", false);

		int i = 1;
		connHandler.setString(i++, tvApp.getTvApplicationPurposeCd());
		connHandler.setString(i++, tvApp.getOperationsDsc());
		connHandler.setString(i++, tvApp.getSubjectTo112R());
		connHandler.setString(i++, tvApp.getPlanSubmittedUnder112R());
		connHandler.setString(i++, tvApp.getSubjectToTIV());
		connHandler.setInteger(i++, tvApp.getLastModified() + 1);
		connHandler.setString(i++, tvApp.getAqdTvApplicationNumber());
		connHandler.setString(i++, tvApp.getNspsApplicableFlag());
		connHandler.setString(i++, tvApp.getNeshapApplicableFlag());
		connHandler.setString(i++, tvApp.getMactApplicableFlag());
		connHandler.setString(i++, tvApp.getPsdApplicableFlag());
		connHandler.setString(i++, tvApp.getNsrApplicableFlag());
		connHandler.setString(i++, tvApp.getFacilityWideRequirementFlag());
		connHandler.setString(i++, tvApp.getProposedExemptions());
		connHandler.setString(i++, tvApp.getAmbientMonitoring());
		connHandler.setString(i++, tvApp.getAlternateOperatingScenarios());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tvApp.isLegacyStateTVApp()));
		connHandler.setTimestamp(i++, tvApp.getRiskManagementPlanSubmitDate());
		connHandler.setString(i++, tvApp.getComplianceCertSubmitFrequency());
		connHandler.setString(i++, tvApp.getComplianceWithApplicableEnhancedMonitoring());
		connHandler.setString(i++, tvApp.getComplianceRequirementsNotMet());
		connHandler.setString(i++, tvApp.getSubjectToEngineConfigRestrictions());
		connHandler.setString(i++, tvApp.getNsrPermitNumber());
		connHandler.setString(i++, tvApp.getSubjectToWAQSR());
		connHandler.setInteger(i++, tvApp.getApplicationID());
		connHandler.setInteger(i++, tvApp.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createApplicationDocument(ApplicationDocumentRef
	 *      newAppDoc)
	 */
	public final ApplicationDocumentRef createApplicationDocument(
			ApplicationDocumentRef newAppDoc) throws DAOException {
		checkNull(newAppDoc);
		ApplicationDocumentRef ret = newAppDoc;

		newAppDoc.setApplicationDocId(nextSequenceVal("S_Application_Doc_Id",
				newAppDoc.getApplicationDocId()));

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createApplicationDocument", false);

		int i = 1;
		connHandler.setInteger(i++, newAppDoc.getApplicationDocId());
		connHandler.setString(i++, newAppDoc.getApplicationDocumentTypeCD());
		connHandler.setString(i++, newAppDoc.getEacFormTypeCD());
		connHandler.setInteger(i++, newAppDoc.getApplicationId());
		connHandler.setInteger(i++, newAppDoc.getApplicationEUId());
		connHandler.setInteger(i++, newAppDoc.getDocumentId());
		connHandler.setString(i++, newAppDoc.getDescription());
		connHandler.setInteger(i++, newAppDoc.getTradeSecretDocId());
		connHandler.setString(i++, newAppDoc.getTradeSecretReason());
		connHandler.setBoolean(i++, newAppDoc.isRequiredDoc());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyApplicationDocument(ApplicationDocumentRef
	 *      appDoc)
	 */
	public final boolean modifyApplicationDocument(ApplicationDocumentRef appDoc)
			throws DAOException {
		checkNull(appDoc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyApplicationDocument", false);

		int i = 1;
		connHandler.setString(i++, appDoc.getApplicationDocumentTypeCD());
		connHandler.setString(i++, appDoc.getEacFormTypeCD());
		connHandler.setInteger(i++, appDoc.getDocumentId());
		connHandler.setString(i++, appDoc.getDescription());
		connHandler.setInteger(i++, appDoc.getTradeSecretDocId());
		connHandler.setString(i++, appDoc.getTradeSecretReason());
		connHandler.setInteger(i++, appDoc.getLastModified() + 1);
		connHandler.setBoolean(i++, appDoc.isRequiredDoc());
		connHandler.setInteger(i++, appDoc.getApplicationDocId());
		connHandler.setInteger(i++, appDoc.getLastModified());
		
		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#markTempApplicationDocuments(Integer appId)
	 */
	public final void markTempApplicationDocuments(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.markTempApplicationBaseDocuments", false);
		connHandler.setInteger(1, appId);
		connHandler.setInteger(2, appId);
		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#markTempPBRNotificationDocuments(Integer appId)
	 */
	public final void markTempPBRNotificationDocuments(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.markTempPBRNotificationDocuments", false);
		connHandler.setInteger(1, appId);
		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#markTempRPCRequestDocuments(Integer appId)
	 */
	public final void markTempRPCRequestDocuments(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.markTempRPCRequestDocuments", false);
		connHandler.setInteger(1, appId);
		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeApplicationDocuments(Integer appId)
	 */
	public final void removeApplicationDocuments(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationDocuments", false);
		connHandler.setInteger(1, appId);
		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeApplicationDocument(Integer docId)
	 */
	public final void removeApplicationDocument(Integer docId)
			throws DAOException {
		checkNull(docId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationDocument", false);
		connHandler.setInteger(1, docId);
		connHandler.remove();
	}

	public final void removeApplicationDocumentDocId(Integer applicationDocId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationDocumentDocId", false);
		connHandler.setInteger(1, applicationDocId);
		connHandler.update();
	}

	public final void updateApplicationValidatedFlagByAppDocId(
			Integer applicationDocId, boolean validatedFlag)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.updateApplicationValidatedFlagByAppDocId",
				false);

		connHandler.setBoolean(1, validatedFlag);
		connHandler.setInteger(2, applicationDocId);

		connHandler.update();
	}

	/**
	 * @see ApplicationDAO#markTempApplicationEUDocuments(Integer appEUId)
	 */
	public final void markTempApplicationEUDocuments(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.markTempApplicationEUBaseDocuments", false);
		connHandler.setInteger(1, appEUId);
		connHandler.setInteger(2, appEUId);
		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeApplicationEUDocuments(Integer appEUId)
	 */
	public final void removeApplicationEUDocuments(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationEUDocuments", false);
		connHandler.setInteger(1, appEUId);
		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#createApplicationEU(ApplicationEU newAppEu)
	 */
	public final ApplicationEU createApplicationEU(ApplicationEU newAppEu)
			throws DAOException {
		checkNull(newAppEu);
		ApplicationEU ret = newAppEu;

		ret.setApplicationEuId(nextSequenceVal("PA_Application_EU_Id",
				ret.getApplicationEuId()));

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createApplicationEU", false);

		int i = 1;
		connHandler.setInteger(i++, ret.getApplicationEuId());
		connHandler.setInteger(i++, ret.getApplicationId());
		connHandler.setInteger(i++, ret.getFpEU().getEmuId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isExcluded()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isNotIncludable()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.getValidated()));
		connHandler.setString(i++, ret.getEuText());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyApplicationEU(ApplicationEU appEu)
	 */
	public final boolean modifyApplicationEU(ApplicationEU appEu)
			throws DAOException {
		checkNull(appEu);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyApplicationEU", false);

		int i = 1;
		connHandler.setInteger(i++, appEu.getFpEU().getEmuId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(appEu.isExcluded()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(appEu.isNotIncludable()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(appEu.getValidated()));
		connHandler.setString(i++, appEu.getEuText());
		connHandler.setInteger(i++, appEu.getLastModified() + 1);
		connHandler.setInteger(i++, appEu.getApplicationEuId());
		connHandler.setInteger(i++, appEu.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#removeApplicationEUs(Integer appId)
	 */
	public final void removeApplicationEUs(Integer appId) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationEUs", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeApplicationEU(Integer appEuId)
	 */
	public final void removeApplicationEU(Integer appEuId) throws DAOException {
		checkNull(appEuId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationEU", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeApplicationPTIOEUs(Integer appId)
	 */
	public final void removeApplicationPTIOEUs(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationPTIOEUs", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeTVApplicationEU(Integer appEuId)
	 */
	public final void removeTVApplicationEU(Integer appEuId)
			throws DAOException {
		checkNull(appEuId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVApplicationEU", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();
	}

	public final void removeRPCRequest(int appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeRPCRequest", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	public final void removeRPERequest(int appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeRPERequest", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	public final void removeRPRRequest(int appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeRPRRequest", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	public final void removePBRNotification(int appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePBRNotification", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removePTIOApplicationEU(Integer appId)
	 */
	public final void removePTIOApplicationEU(Integer appEuId)
			throws DAOException {
		checkNull(appEuId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePTIOApplicationEU", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#createPBRNotification(PBRNotification
	 *      newPBRNotification)
	 */
	public final PBRNotification createPBRNotification(
			PBRNotification newPBRNotification) throws DAOException {

		checkNull(newPBRNotification);
		PBRNotification ret = newPBRNotification;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createPBRNotification", false);

		int i = 1;
		connHandler.setInteger(i++, newPBRNotification.getApplicationID());
		connHandler.setString(i++, newPBRNotification.getPbrReasonCd());
		connHandler.setString(i++, newPBRNotification.getPbrTypeCd());
		connHandler.setString(i++,
				translateBooleanToIndicator(newPBRNotification
						.isRequestingRevocationFlag()));
		connHandler.setString(i++, newPBRNotification.getRegulatedCmntyDsc());
		connHandler.setString(i++, newPBRNotification.getDispositionFlag());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyPBRNotification(PBRNotification
	 *      pbrNotification)
	 */
	public final boolean modifyPBRNotification(PBRNotification pbrNotification)
			throws DAOException {
		checkNull(pbrNotification);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyPBRNotification", false);

		int i = 1;
		connHandler.setString(i++, pbrNotification.getPbrReasonCd());
		connHandler.setString(i++, pbrNotification.getPbrTypeCd());
		connHandler.setString(i++, translateBooleanToIndicator(pbrNotification
				.isRequestingRevocationFlag()));
		connHandler.setString(i++, pbrNotification.getRegulatedCmntyDsc());
		connHandler.setString(i++, pbrNotification.getDispositionFlag());
		connHandler.setInteger(i++, pbrNotification.getLastModified() + 1);
		connHandler.setInteger(i++, pbrNotification.getApplicationID());
		connHandler.setInteger(i++, pbrNotification.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createRPCRequest(RPCRequest newRPCRequest)
	 */
	public final RPCRequest createRPCRequest(RPCRequest newRPCRequest)
			throws DAOException {

		checkNull(newRPCRequest);
		RPCRequest ret = newRPCRequest;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createRPCRequest", false);

		int i = 1;
		connHandler.setInteger(i++, newRPCRequest.getApplicationID());
		connHandler.setString(i++, newRPCRequest.getRpcTypeCd());
		connHandler.setInteger(i++, newRPCRequest.getPermitId());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyRPCRequest(RPCRequest rpcRequest)
	 */
	public final boolean modifyRPCRequest(RPCRequest rpcRequest)
			throws DAOException {

		checkNull(rpcRequest);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyRPCRequest", false);

		int i = 1;
		connHandler.setString(i++, rpcRequest.getRpcTypeCd());
		connHandler.setInteger(i++, rpcRequest.getPermitId());
		connHandler.setInteger(i++, rpcRequest.getLastModified() + 1);

		connHandler.setInteger(i++, rpcRequest.getApplicationID());
		connHandler.setInteger(i++, rpcRequest.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createRPERequest(RPERequest newRPERequest)
	 */
	public final RPERequest createRPERequest(RPERequest newRPERequest)
			throws DAOException {

		checkNull(newRPERequest);
		RPERequest ret = newRPERequest;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createRPERequest", false);

		int i = 1;
		connHandler.setInteger(i++, newRPERequest.getApplicationID());
		connHandler.setInteger(i++, newRPERequest.getPermitId());
		connHandler.setTimestamp(i++, newRPERequest.getTerminationDate());
		connHandler.setString(i++, newRPERequest.getRegulatedCmntyDsc());
		connHandler.setString(i++, newRPERequest.getDispositionFlag());
		connHandler.setFloat(i++, newRPERequest.getOtherAdjustment());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyRPERequest(RPERequest rpeRequest)
	 */
	public final boolean modifyRPERequest(RPERequest rpeRequest)
			throws DAOException {

		checkNull(rpeRequest);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyRPERequest", false);

		int i = 1;
		connHandler.setInteger(i++, rpeRequest.getPermitId());
		connHandler.setTimestamp(i++, rpeRequest.getTerminationDate());
		connHandler.setString(i++, rpeRequest.getRegulatedCmntyDsc());
		connHandler.setString(i++, rpeRequest.getDispositionFlag());
		connHandler.setFloat(i++, rpeRequest.getOtherAdjustment());
		connHandler.setInteger(i++, rpeRequest.getLastModified() + 1);

		connHandler.setInteger(i++, rpeRequest.getApplicationID());
		connHandler.setInteger(i++, rpeRequest.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createRPRRequest(RPRRequest newRPRRequest)
	 */
	public final RPRRequest createRPRRequest(RPRRequest newRPRRequest)
			throws DAOException {

		checkNull(newRPRRequest);
		RPRRequest ret = newRPRRequest;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createRPRRequest", false);

		int i = 1;
		connHandler.setInteger(i++, newRPRRequest.getApplicationID());
		connHandler.setInteger(i++, newRPRRequest.getPermitId());
		connHandler.setString(i++, newRPRRequest.getRprReasonCd());
		connHandler.setString(i++, newRPRRequest.getRegulatedCmntyDsc());
		connHandler.setString(i++, newRPRRequest.getDispositionFlag());
		connHandler.setString(i++, translateBooleanToIndicator(newRPRRequest
				.isRevokeEntirePermit()));
		connHandler.setString(i++, newRPRRequest.getBasisForRevocation());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyRPRRequest(RPRRequest rprRequest)
	 */
	public final boolean modifyRPRRequest(RPRRequest rprRequest)
			throws DAOException {
		checkNull(rprRequest);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyRPRRequest", false);

		int i = 1;
		connHandler.setInteger(i++, rprRequest.getPermitId());
		connHandler.setString(i++, rprRequest.getRprReasonCd());
		connHandler.setString(i++, rprRequest.getRegulatedCmntyDsc());
		connHandler.setString(i++, rprRequest.getDispositionFlag());
		connHandler.setString(i++,
				translateBooleanToIndicator(rprRequest.isRevokeEntirePermit()));
		connHandler.setString(i++, rprRequest.getBasisForRevocation());
		connHandler.setInteger(i++, rprRequest.getLastModified() + 1);

		connHandler.setInteger(i++, rprRequest.getApplicationID());
		connHandler.setInteger(i++, rprRequest.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#generateApplicationSeqNo()
	 */
	public final String generateApplicationNumber(
			Class<? extends Application> applicationClass) throws DAOException {
		String pattern = "A0000000"; // default pattern for PTI/PTIO and TV
										// applications
		String sequenceNm = "PA_Application_Nbr";
		int maxDigits = 7;

		if (applicationClass.equals(RPCRequest.class)) {
			pattern = "M0000000";
			sequenceNm = "PA_RPC_Nbr";
		} else if (applicationClass.equals(RPRRequest.class)) {
			pattern = "RSC00000";
			sequenceNm = "PA_RPR_Nbr";
			maxDigits = 5;
		} else if (applicationClass.equals(RPERequest.class)) {
			pattern = "EXT00000";
			sequenceNm = "PA_RPE_Nbr";
			maxDigits = 5;
		} else if (applicationClass.equals(PBRNotification.class)) {
			pattern = "PBR00000";
			sequenceNm = "PA_PBR_Nbr";
			maxDigits = 5;
		} else if (applicationClass.equals(RelocateRequest.class)) {
			pattern = "REL00000";
			sequenceNm = "PA_REL_Nbr";
			maxDigits = 5;
		} else if (applicationClass.equals(DelegationRequest.class)) {
			pattern = "DOR00000";
			sequenceNm = "PA_DOR_Nbr";
			maxDigits = 5;
		}

		String result = null;
		try {
			DecimalFormat format = new DecimalFormat(pattern);
			format.setMaximumIntegerDigits(maxDigits);
			result = format.format(nextSequenceVal(sequenceNm));
		} catch (IllegalArgumentException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * @see ApplicationDAO#generateApplicableRequirementSeqNo()
	 */
	public final int generateApplicableRequirementSeqNo() throws DAOException {
		Integer seqNo = nextSequenceVal("S_TV_Applicable_Req_Id");
		return seqNo.intValue();
	}

	/**
	 * @see ApplicationDAO#createPTIOApplicationEU(PTIOApplication newPTIOAppEu)
	 */
	public final PTIOApplicationEU createPTIOApplicationEU(
			PTIOApplicationEU newPTIOAppEu) throws DAOException {
		checkNull(newPTIOAppEu);
		PTIOApplicationEU ret = newPTIOAppEu;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createPTIOApplicationEU", false);

		int i = 1;
		connHandler.setInteger(i++, newPTIOAppEu.getApplicationEuId());
		connHandler.setTimestamp(i++, newPTIOAppEu.getWorkStartDate());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(newPTIOAppEu
						.isWorkStartAfterPermit()));
		connHandler.setTimestamp(i++, newPTIOAppEu.getOperationBeginDate());
		connHandler.setString(i++, newPTIOAppEu.getReconstructionDesc());
		connHandler.setInteger(i++, newPTIOAppEu.getShutdownYears());
		connHandler.setString(i++,
				newPTIOAppEu.getRequestingFederalLimitsFlag());
		connHandler.setString(i++,
				newPTIOAppEu.getFederalLimitsOtherReasonDesc());
		connHandler.setString(i++, newPTIOAppEu.getModificationDesc());
		connHandler.setString(i++, newPTIOAppEu.getGeneralPermitTypeCd());
		connHandler.setString(i++, newPTIOAppEu.getModelGeneralPermitCd());
		connHandler.setString(i++, newPTIOAppEu.getBactFlag());
		connHandler.setString(i++, newPTIOAppEu.getLaerFlag());

		connHandler.setString(i++, newPTIOAppEu.getGhgApplicableFlag());
		connHandler.setString(i++, newPTIOAppEu.getNspsApplicableFlag());
		connHandler.setString(i++, newPTIOAppEu.getNeshapApplicableFlag());
		connHandler.setString(i++, newPTIOAppEu.getMactApplicableFlag());
		connHandler.setString(i++, newPTIOAppEu.getPsdApplicableFlag());
		connHandler.setString(i++, newPTIOAppEu.getNsrApplicableFlag());
		connHandler.setString(i++, newPTIOAppEu.getRiskManagementPlanFlag());
		connHandler.setString(i++, newPTIOAppEu.getTitleIVFlag());
		connHandler.setString(i++,
				newPTIOAppEu.getFederalRuleApplicabilityExplanation());
		connHandler.setString(i++, newPTIOAppEu.getPsdBACTFlag());
		connHandler.setString(i++, newPTIOAppEu.getNsrLAERFlag());

		connHandler.setInteger(i++, newPTIOAppEu.getOpSchedHrsDay());
		connHandler.setInteger(i++, newPTIOAppEu.getOpSchedHrsYr());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#removeApplication(int appId)
	 */
	public final void removeApplication(int appId) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplication", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removePTIOApplication(int appId)
	 */
	public final void removePTIOApplication(int appId) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePTIOApplication", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeTVApplication(int appId)
	 */
	public final void removeTVApplication(int appId) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVApplication", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationsIn(Integer[])
	 */
	public final Application[] retrieveApplicationsIn(Integer appIds[])
			throws DAOException {
		ArrayList<Application> ret = new ArrayList<Application>();

		if (appIds.length > 0) {
			String statementSQL = loadSQL("ApplicationSQL.findApplications");

			StringBuffer whereClause = new StringBuffer(
					"WHERE application_id IN (" + appIds[0]);

			for (int i = 1; i < appIds.length; i++) {
				whereClause.append("," + appIds[i]);
			}

			statementSQL += whereClause.toString() + ")";

			Connection conn = null;
			PreparedStatement pStmt = null;

			try {
				conn = getReadOnlyConnection();
				pStmt = conn.prepareStatement(statementSQL);

				ResultSet rs = pStmt.executeQuery();

				while (rs.next()) {
					ret.add(populateApplication(rs));
				}
			} catch (Exception e) {
				logger.error("whereClause: " + whereClause.toString(), e);
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		}

		return ret.toArray(new Application[0]);
	}

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	private Application populateApplication(ResultSet rs) throws SQLException,
			DAOException {
		String appType = rs.getString("application_type_cd");
		Application ret = null;

		if (appType.compareToIgnoreCase("PTIO") == 0) {
			ret = new PTIOApplication();
		} else if (appType.compareToIgnoreCase("PBR") == 0) {
			ret = new PBRNotification();
		} else if (appType.compareToIgnoreCase("TV") == 0) {
			ret = new TVApplication();
		} else if (appType.compareToIgnoreCase("RPC") == 0) {
			ret = new RPCRequest();
		} else if (appType.compareToIgnoreCase("RPE") == 0) {
			ret = new RPERequest();
		} else if (appType.compareToIgnoreCase("RPR") == 0) {
			ret = new RPRRequest();
		} else if (appType.compareToIgnoreCase("RPS") == 0
				|| appType.compareToIgnoreCase("ITR") == 0
				|| appType.compareToIgnoreCase("SPA") == 0) {
			ret = new RelocateRequest();
		} else if (appType.compareToIgnoreCase("DOR") == 0) {
			ret = new DelegationRequest();
		} else if (appType.compareToIgnoreCase("TIV") == 0) {
			ret = new TIVApplication();
		} else {
			logger.error("Unable to find class for Application Type : "
					+ appType);
		}

		if (ret != null) {
			ret.populate(rs);
		}

		return ret;
	}

	/**
	 * @see ApplicationDAO#addApplicationNote(int fpId, String facilityId, int
	 *      noteId)
	 */
	public final void addApplicationNote(int applicationId, int noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addApplicationNote", false);

		connHandler.setInteger(1, applicationId);
		connHandler.setInteger(2, noteId);

		connHandler.update();

		return;
	}

	/**
	 * @see ApplicationDAO#removeApplicationNotes(int applicationId)
	 */
	public final void removeApplicationNotes(int applicationId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationNotes", false);

		connHandler.setInteger(1, applicationId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationNotes(String applicationId)
	 */
	public final ApplicationNote[] retrieveApplicationNotes(int applicationId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationNotes", true);

		connHandler.setInteger(1, applicationId);

		ArrayList<ApplicationNote> ret = connHandler
				.retrieveArray(ApplicationNote.class);

		return ret.toArray(new ApplicationNote[0]);
	}

	/**
	 * @see ApplicationDAO#retrievePTIOEUPurposeCds(Integer appEUId)
	 */
	public final String[] retrievePTIOEUPurposeCds(Integer appEUId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePTIOEUPurposeCds", true);

		connHandler.setInteger(1, appEUId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#removePTIOEUPurposeCds(Integer appEUId)
	 */
	public final void removePTIOEUPurposeCds(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePTIOEUPurposeCds", false);

		connHandler.setInteger(1, appEUId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#createPTIOEUPurpose(Integer appEUId, String
	 *      purposeCd)
	 */
	public final boolean createPTIOEUPurpose(Integer appEUId, String purposeCd)
			throws DAOException {
		checkNull(appEUId);
		checkNull(purposeCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createPTIOEUPurpose", false);

		connHandler.setInteger(1, appEUId);
		connHandler.setString(2, purposeCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#createPTIOEUFedLimitReason(Integer appEUId, String
	 *      reasonCd)
	 */
	public final boolean createPTIOEUFedLimitReason(Integer appEUId,
			String reasonCd) throws DAOException {
		checkNull(appEUId);
		checkNull(reasonCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createPTIOEUFedLimit", false);

		connHandler.setInteger(1, appEUId);
		connHandler.setString(2, reasonCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#removePTIOEUFedLimitReasonCds(Integer appEUId)
	 */
	public final void removePTIOEUFedLimitReasonCds(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePTIOEUFedLimitCds", false);

		connHandler.setInteger(1, appEUId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#retrievePTIOEUFedLimitReasonCds(Integer appEUId)
	 */
	public final String[] retrievePTIOEUFedLimitReasonCds(Integer appEUId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePTIOEUFedLimitCds", true);

		connHandler.setInteger(1, appEUId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#addPTIOAppMACTSubpartCd(Integer appId, String
	 *      subpartCd)
	 */
	public final boolean addPTIOAppMACTSubpartCd(Integer appId, String subpartCd)
			throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addPTIOAppMACTSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#addPTIOAppNESHAPSubpartCd(Integer appId, String
	 *      subpartCd)
	 */
	public final boolean addPTIOAppNESHAPSubpartCd(Integer appId,
			String subpartCd) throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addPTIOAppNESHAPSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#addPTIOAppNSPSSubpartCd(Integer appId, String
	 *      subpartCd)
	 */
	public final boolean addPTIOAppNSPSSubpartCd(Integer appId, String subpartCd)
			throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addPTIOAppNSPSSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#removePTIOAppMACTSubpartCds(Integer appId)
	 */
	public final void removePTIOAppMACTSubpartCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePTIOAppMACTSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removePTIOAppNESHAPSubpartCds(Integer appId)
	 */
	public final void removePTIOAppNESHAPSubpartCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePTIOAppNESHAPSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removePTIOAppNSPSSubpartCds(Integer appId)
	 */
	public final void removePTIOAppNSPSSubpartCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePTIOAppNSPSSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#retrievePTIOAppMACTSubpartCds(Integer appId)
	 */
	public final String[] retrievePTIOAppMACTSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePTIOAppMACTSubpartCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#retrievePTIOAppNESHAPSubpartCds(Integer appId)
	 */
	public final String[] retrievePTIOAppNESHAPSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePTIOAppNESHAPSubpartCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#retrievePTIOAppNSPSSubpartCds(Integer appId)
	 */
	public final String[] retrievePTIOAppNSPSSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePTIOAppNSPSSubpartCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#addApplicationEUEmissions(ApplicationEUEmissions
	 *      emissions)
	 */
	public final ApplicationEUEmissions addApplicationEUEmissions(
			ApplicationEUEmissions emissions) throws DAOException {

		checkNull(emissions);
		ApplicationEUEmissions ret = emissions;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addApplicationEUEmissions", false);

		int i = 1;
		connHandler.setInteger(i++, emissions.getApplicationEuId());
		connHandler.setString(i++, emissions.getPollutantCd());

		connHandler.setString(i++, emissions.getEuEmissionTableCd());

		connHandler.setString(i++, emissions.getPreCtlPotentialEmissions());
		connHandler.setString(i++, emissions.getPotentialToEmit());
		connHandler.setString(i++, emissions.getUnitCd());
		connHandler.setString(i++, emissions.getPotentialToEmitLbHr());
		connHandler.setString(i++, emissions.getPotentialToEmitTonYr());
		connHandler.setString(i++, emissions.getPteDeterminationBasisCd());
		connHandler.setString(i++, emissions.getCo2Equivalent());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * addTVApplicationEUEmissions
	 * (us.oh.state.epa.stars2.database.dbObjects.application
	 * .TVApplicationEUEmissions)
	 */
	public final TVApplicationEUEmissions addTVApplicationEUEmissions(
			TVApplicationEUEmissions emissions) throws DAOException {

		checkNull(emissions);
		TVApplicationEUEmissions ret = emissions;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTVApplicationEUEmissions", false);

		int i = 1;
		connHandler.setInteger(i++, emissions.getApplicationEuId());
		connHandler.setInteger(i++, emissions.getTvEuOperatingScenarioId());
		connHandler.setString(i++, emissions.getPollutantCd());
		connHandler.setString(i++, emissions.getEuEmissionTableCd());
		connHandler.setString(i++, emissions.getPteTonsYr());
		connHandler.setString(i++, emissions.getCo2Equivalent());
		connHandler.setString(i++, emissions.getPteDeterminationBasis());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(emissions
								.isDetBasisTradeSecret()));
		connHandler.setString(i++, emissions.getReasonDetBasisTradeSecret());
		connHandler.setString(i++, emissions.getApplicableReq());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see ApplicationDAO#modifyApplicationEUEmissions(ApplicationEUEmissions
	 *      emissions)
	 */
	public final boolean modifyApplicationEUEmissions(
			ApplicationEUEmissions emissions) throws DAOException {
		checkNull(emissions);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyApplicationEUEmissions", false);

		int i = 1;
		
		connHandler.setString(i++, emissions.getPreCtlPotentialEmissions());
		connHandler.setString(i++, emissions.getPotentialToEmit());
		connHandler.setString(i++, emissions.getUnitCd());
		connHandler.setString(i++, emissions.getPotentialToEmitLbHr());
		connHandler.setString(i++, emissions.getPotentialToEmitTonYr());
		connHandler.setString(i++, emissions.getPteDeterminationBasisCd());

		connHandler.setInteger(i++, emissions.getLastModified() + 1);
		connHandler.setInteger(i++, emissions.getApplicationEuId());
		connHandler.setString(i++, emissions.getPollutantCd());
		connHandler.setString(i++, emissions.getEuEmissionTableCd());
		connHandler.setInteger(i++, emissions.getLastModified());

		return connHandler.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * modifyTVApplicationEUEmissions
	 * (us.oh.state.epa.stars2.database.dbObjects.application
	 * .TVApplicationEUEmissions)
	 */
	public final boolean modifyTVApplicationEUEmissions(
			TVApplicationEUEmissions emissions) throws DAOException {

		checkNull(emissions);
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyTVApplicationEUEmissions", false);

		int i = 1;
		connHandler.setString(i++, emissions.getPteTonsYr());
		connHandler.setString(i++, emissions.getCo2Equivalent());
		connHandler.setString(i++, emissions.getPteDeterminationBasis());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(emissions
								.isDetBasisTradeSecret()));
		connHandler.setString(i++, emissions.getReasonDetBasisTradeSecret());
		connHandler.setString(i++, emissions.getApplicableReq());
		connHandler.setInteger(i++, emissions.getLastModified() + 1);
		connHandler.setInteger(i++, emissions.getApplicationEuId());
		connHandler.setInteger(i++, emissions.getTvEuOperatingScenarioId());
		connHandler.setString(i++, emissions.getPollutantCd());
		connHandler.setString(i++, emissions.getEuEmissionTableCd());
		connHandler.setInteger(i++, emissions.getLastModified());

		ret = connHandler.update();

		return ret;
	}

	/**
	 * @see ApplicationDAO#removeApplicationEUEmissions(Integer appEUId, String
	 *      pollutantCd)
	 */
	public final void removeApplicationEUEmissions(
			ApplicationEUEmissions emissions) throws DAOException {
		checkNull(emissions.getApplicationEuId());
		checkNull(emissions.getPollutantCd());
		checkNull(emissions.getEuEmissionTableCd());
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeApplicationEUEmissions", false);

		connHandler.setInteger(1, emissions.getApplicationEuId());
		connHandler.setString(2, emissions.getPollutantCd());
		connHandler.setString(3, emissions.getEuEmissionTableCd());

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVApplicationEUEmissions
	 * (us.oh.state.epa.stars2.database.dbObjects.application
	 * .TVApplicationEUEmissions)
	 */
	public final void removeTVApplicationEUEmissions(
			TVApplicationEUEmissions emissions) throws DAOException {
		checkNull(emissions.getApplicationEuId());
		checkNull(emissions.getPollutantCd());
		checkNull(emissions.getTvEuOperatingScenarioId());
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVApplicationEUEmission", false);

		connHandler.setInteger(1, emissions.getApplicationEuId());
		connHandler.setString(2, emissions.getPollutantCd());
		connHandler.setInteger(3, emissions.getTvEuOperatingScenarioId());

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVApplicationEUEmissionsForScenario(java.lang.Integer,
	 * java.lang.Integer)
	 */
	public final void removeTVApplicationEUEmissionsForScenario(
			Integer appEuId, Integer tvOpScenarioId) throws DAOException {
		checkNull(appEuId);
		checkNull(tvOpScenarioId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVApplicationEUEmissionsForScenario",
				false);

		connHandler.setInteger(1, appEuId);
		connHandler.setInteger(2, tvOpScenarioId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removeApplicationEUEmissions(Integer appEUId)
	 */
	public final void removeApplicationEUEmissions(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeAllApplicationEUEmissionsForEU", false);

		connHandler.setInteger(1, appEUId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVApplicationEUEmissions(java.lang.Integer)
	 */
	public final void removeTVApplicationEUEmissions(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeAllTVApplicationEUEmissionsForEU", false);

		connHandler.setInteger(1, appEUId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#retrieveApplicationEUEmissions(Integer appEUId)
	 */
	public final ApplicationEUEmissions[] retrieveApplicationEUEmissions(
			Integer appEUId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationEUEmissions", true);
		checkNull(appEUId);

		connHandler.setInteger(1, appEUId);

		ArrayList<ApplicationEUEmissions> ret = connHandler
				.retrieveArray(ApplicationEUEmissions.class);

		return ret.toArray(new ApplicationEUEmissions[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * retrieveTVApplicationEUEmissions(java.lang.Integer)
	 */
	public final TVApplicationEUEmissions[] retrieveTVApplicationEUEmissions(
			Integer appEUId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicationEUEmissions", true);
		checkNull(appEUId);

		connHandler.setInteger(1, appEUId);

		ArrayList<TVApplicationEUEmissions> ret = connHandler
				.retrieveArray(TVApplicationEUEmissions.class);

		return ret.toArray(new TVApplicationEUEmissions[0]);
	}

	/**
	 * @see ApplicationDAO#retrieveRenewalApplications(String facilityId,
	 *      Timestamp upperDate, Timestamp lowerDate)
	 */
	public final Integer retrieveRenewalApplications(String facilityId,
			Timestamp upperDate, Timestamp lowerDate) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveRenewalApplication", true);

		connHandler.setString(1, facilityId);
		connHandler.setTimestamp(2, upperDate); // >
		connHandler.setTimestamp(3, lowerDate); // <

		return (Integer) connHandler.retrieveJavaObject(Integer.class);
	}

	/**
	 * @see ApplicationDAO#retrieveRenewalApplicationByEU(String corrEpaEmuId,
	 *      Timestamp submittedDateBegin, Timestamp submittedDateEnd)
	 */
	public Integer retrieveRenewalApplicationByEU(Integer corrEpaEmuId,
			Timestamp submittedDateBegin, Timestamp submittedDateEnd)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveRenewalApplicationByEU", true);

		connHandler.setInteger(1, corrEpaEmuId);
		connHandler.setTimestamp(2, submittedDateBegin);
		connHandler.setTimestamp(3, submittedDateEnd);

		return (Integer) connHandler.retrieveJavaObject(Integer.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#createTVApplicationEU
	 * (us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEU)
	 */
	public final TVApplicationEU createTVApplicationEU(
			TVApplicationEU newTVAppEu) throws DAOException {
		checkNull(newTVAppEu);
		TVApplicationEU ret = newTVAppEu;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createTVApplicationEU", false);

		int i = 1;
		connHandler.setInteger(i++, newTVAppEu.getApplicationEuId());
		connHandler.setString(i++, newTVAppEu.getMonitorReq());
		connHandler.setString(i++, newTVAppEu.getMonitorReqDsc());
		connHandler.setString(i++, newTVAppEu.getComplyWEnhMonitor());
		connHandler.setString(i++, newTVAppEu.getTvIeuReasonCd());
		connHandler.setString(i++, newTVAppEu.getApplicableReqs());
		connHandler.setString(i++, newTVAppEu.getNspsApplicableFlag());
		connHandler.setString(i++, newTVAppEu.getNeshapApplicableFlag());
		connHandler.setString(i++, newTVAppEu.getMactApplicableFlag());
		connHandler.setString(i++,
				newTVAppEu.getFederalRuleApplicabilityExplanation());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#modifyTVApplicationEU
	 * (us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEU)
	 */
	public final boolean modifyTVApplicationEU(TVApplicationEU tvAppEu)
			throws DAOException {
		checkNull(tvAppEu);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyTVApplicationEU", false);
		int i = 1;
		connHandler
				.setString(i++, tvAppEu.getMonitorReq());
		connHandler.setString(i++, tvAppEu.getMonitorReqDsc());
		connHandler.setString(i++, tvAppEu.getComplyWEnhMonitor());
		connHandler.setString(i++, tvAppEu.getTvIeuReasonCd());
		connHandler.setString(i++, tvAppEu.getApplicableReqs());

		connHandler.setString(i++, tvAppEu.getNspsApplicableFlag());
		connHandler.setString(i++, tvAppEu.getNeshapApplicableFlag());
		connHandler.setString(i++, tvAppEu.getMactApplicableFlag());
		connHandler.setString(i++,
				tvAppEu.getFederalRuleApplicabilityExplanation());

		connHandler.setInteger(i++, tvAppEu.getLastModified() + 1);
		connHandler.setInteger(i++, tvAppEu.getApplicationEuId());
		connHandler.setInteger(i++, tvAppEu.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#retrieveTVApplicationEmissionUnits(Integer appId)
	 */
	public final TVApplicationEU[] retrieveTVApplicationEmissionUnits(
			Integer appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicationEmissionUnits", true);

		connHandler.setInteger(1, appId);

		ArrayList<TVApplicationEU> tempArray = connHandler
				.retrieveArray(TVApplicationEU.class);

		return tempArray.toArray(new TVApplicationEU[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * setApplicationValidatedFlag(java.lang.Integer, boolean)
	 */
	public final void setApplicationValidatedFlag(Integer appId,
			boolean validated) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.setApplicationValidatedFlag", false);
		connHandler.setString(1,
				AbstractDAO.translateBooleanToIndicator(validated));
		connHandler.setInteger(2, appId);
		connHandler.remove();
	}
	
	public final void setApplicationEUValidatedFlag(Integer appEuId,
			boolean validated) throws DAOException {
		checkNull(appEuId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.setApplicationEUValidatedFlag", false);
		connHandler.setString(1,
				AbstractDAO.translateBooleanToIndicator(validated));
		connHandler.setInteger(2, appEuId);
		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#markApplicationSubmitted
	 * (us.oh.state.epa.stars2.database.dbObjects.application.Application)
	 */
	public final void markApplicationSubmitted(Application app)
			throws DAOException {
		checkNull(app);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.markApplicationSubmitted", false);
		connHandler.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		connHandler.setInteger(2, app.getApplicationID());
		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#addTVEUOperatingScenario
	 * (
	 * us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperatingScenario
	 * )
	 */
	public final TVEUOperatingScenario createTVEUOperatingScenario(
			TVEUOperatingScenario scenario) throws DAOException {
		checkNull(scenario);
		TVEUOperatingScenario ret = scenario;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTVEUOperatingScenario", false);
		connHandler.setInteger(1, scenario.getApplicationEuId());
		connHandler.setInteger(2, scenario.getTvEuOperatingScenarioId());
		connHandler.setString(3, scenario.getTvEuOperatingScenarioNm());
		connHandler.setInteger(4, scenario.getOpSchedHrsDay());
		connHandler.setInteger(5, scenario.getOpSchedHrsYr());
		connHandler.setString(6, AbstractDAO
				.translateBooleanToIndicator(scenario.isOpSchedTradeSecret()));
		connHandler.setString(7, scenario.getOpAosAutherized());
		connHandler.setString(8, scenario.getOpSchedTradeSecretReason());
		connHandler.setString(9, scenario.getOperationLimits());
		connHandler.setString(10, scenario.getOperationLimitsDsc());
		connHandler.setTimestamp(11, scenario.getEngOrderDate());
		connHandler.setTimestamp(12, scenario.getEngManufactureDate());

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		connHandler.update();

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#getNextScenarioId(
	 * java.lang.Integer)
	 */
	public final int getNextScenarioId(Integer appEuId) throws DAOException {
		Integer result = null;
		ResultSet resultSet = null;

		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("ApplicationSQL.getMaxOpScenarioId"));
			psSelect.setInt(1, appEuId);
			resultSet = psSelect.executeQuery();

			if (!resultSet.next()) {
				throw new DAOException(
						"No scenarios exist for Application EU: " + appEuId);
			}

			result = new Integer(resultSet.getInt(1) + 1);
			resultSet.close();
		} catch (Exception e) {
			logger.error("appEuId=" + appEuId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}

		return result;
	}

	public final int getNextAltScenarioPteReqId(Integer appEuId,
			Integer scenarioId) throws DAOException {
		// default to 1 if no ids exist for this eu and scenario
		int result = 1;
		ResultSet resultSet = null;

		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("ApplicationSQL.getMaxAltScenarioPteReqId"));
			psSelect.setInt(1, appEuId);
			psSelect.setInt(2, scenarioId);
			resultSet = psSelect.executeQuery();

			if (resultSet.next()) {
				result = resultSet.getInt(1) + 1;
			}
			resultSet.close();
		} catch (Exception e) {
			logger.error("appEuId=" + appEuId.toString() + ", scenarioId="
					+ scenarioId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * modifyTVEUOperatingScenario
	 * (us.oh.state.epa.stars2.database.dbObjects.application
	 * .TVEUOperatingScenario)
	 */
	public final boolean modifyTVEUOperatingScenario(
			TVEUOperatingScenario scenario) throws DAOException {
		checkNull(scenario);
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyTVEUOperatingScenario", false);

		int i = 1;
		connHandler.setString(i++, scenario.getTvEuOperatingScenarioNm());
		connHandler.setInteger(i++, scenario.getOpSchedHrsDay());
		connHandler.setInteger(i++, scenario.getOpSchedHrsYr());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(scenario.isOpSchedTradeSecret()));
		connHandler.setString(i++, scenario.getOpAosAutherized());
		connHandler.setString(i++, scenario.getOpSchedTradeSecretReason());
		connHandler.setString(i++, scenario.getOperationLimits());
		connHandler.setString(i++, scenario.getOperationLimitsDsc());
		connHandler.setTimestamp(i++, scenario.getEngOrderDate());
		connHandler.setTimestamp(i++, scenario.getEngManufactureDate());
		connHandler.setInteger(i++, scenario.getLastModified() + 1);
		connHandler.setInteger(i++, scenario.getApplicationEuId());
		connHandler.setInteger(i++, scenario.getTvEuOperatingScenarioId());
		connHandler.setInteger(i++, scenario.getLastModified());

		ret = connHandler.update();

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVEUOperatingScenario
	 * (us.oh.state.epa.stars2.database.dbObjects.application
	 * .TVEUOperatingScenario)
	 */
	public final void removeTVEUOperatingScenario(TVEUOperatingScenario scenario)
			throws DAOException {
		checkNull(scenario);
		checkNull(scenario.getApplicationEuId());
		checkNull(scenario.getTvEuOperatingScenarioId());
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVEUOperatingScenario", false);

		connHandler.setInteger(1, scenario.getApplicationEuId());
		connHandler.setInteger(2, scenario.getTvEuOperatingScenarioId());

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVEUOperatingScenarios(java.lang.Integer)
	 */
	public final void removeTVEUOperatingScenarios(Integer appEuId)
			throws DAOException {
		checkNull(appEuId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVEUOperatingScenariosForEU", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * retrieveTVEUOperatingScenarios(java.lang.Integer)
	 */
	public final TVEUOperatingScenario[] retrieveTVEUOperatingScenarios(
			Integer appEUId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVOperatingScenariosForEU", true);
		checkNull(appEUId);

		connHandler.setInteger(1, appEUId);

		ArrayList<TVEUOperatingScenario> ret = connHandler
				.retrieveArray(TVEUOperatingScenario.class);

		return ret.toArray(new TVEUOperatingScenario[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * retrieveTVApplicationEUEmissions(java.lang.Integer, java.lang.Integer)
	 */
	public final TVApplicationEUEmissions[] retrieveTVApplicationEUEmissions(
			Integer appEUId, Integer tvEuOperatingScenarioId)
			throws DAOException {
		checkNull(appEUId);
		checkNull(tvEuOperatingScenarioId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicationEUEmissionsForScenario",
				true);

		connHandler.setInteger(1, appEUId);
		connHandler.setInteger(2, tvEuOperatingScenarioId);

		ArrayList<TVApplicationEUEmissions> ret = connHandler
				.retrieveArray(TVApplicationEUEmissions.class);

		return ret.toArray(new TVApplicationEUEmissions[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#addTVApplicableReq
	 * (us.oh.state.epa.stars2.database.dbObjects.application.TVApplicableReq)
	 */
	public final TVApplicableReq addTVApplicableReq(TVApplicableReq appReq)
			throws DAOException {
		checkNull(appReq);
		TVApplicableReq ret = appReq;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTVApplicableReq", false);

		int i = 1;
		connHandler.setInteger(i++, appReq.getTvApplicableReqId());
		connHandler.setInteger(i++, appReq.getApplicationId());
		connHandler.setInteger(i++, appReq.getApplicationEuId());
		connHandler.setInteger(i++, appReq.getTvEuGroupId());
		connHandler.setString(i++, appReq.getPollutantCd());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(appReq.isStateOnly()));
		connHandler.setString(i++, appReq.getTvRuleCiteTypeCd());

		connHandler.setString(i++, appReq.getAllowableRuleCiteCd());
		connHandler.setString(i++, appReq.getAllowablePermitCite());
		connHandler.setString(i++, appReq.getAllowableValue());

		connHandler.setString(i++, appReq.getMonitoringRuleCiteCd());
		connHandler.setString(i++, appReq.getMonitoringPermitCite());
		connHandler.setString(i++, appReq.getMonitoringValue());

		connHandler.setString(i++, appReq.getRecordKeepingRuleCiteCd());
		connHandler.setString(i++, appReq.getRecordKeepingPermitCite());
		connHandler.setString(i++, appReq.getRecordKeepingValue());

		connHandler.setString(i++, appReq.getReportingRuleCiteCd());
		connHandler.setString(i++, appReq.getReportingPermitCite());
		connHandler.setString(i++, appReq.getTvCompRptFreqCd());
		connHandler.setString(i++, appReq.getReportingOtherDsc());

		connHandler.setString(i++, appReq.getTestingRuleCiteCd());
		connHandler.setString(i++, appReq.getTestingPermitCite());
		connHandler.setString(i++, appReq.getTestingValue());

		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(appReq.isComplianceStatus()));

		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(appReq
						.isComplianceObligationsStatus()));

		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(appReq
						.isProposedExemptionsStatus()));

		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(appReq
						.isProposedAltLimitsStatus()));

		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(appReq
						.isProposedTestChangesStatus()));

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#removeTVApplicableReqs
	 * (java.lang.Integer)
	 */
	public final void removeTVApplicableReqs(Integer appId) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVApplicableReqs", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVApplicableReqsForEU(java.lang.Integer)
	 */
	public final void removeTVApplicableReqsForEU(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVApplicableReqsForEU", false);

		connHandler.setInteger(1, appEUId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVApplicableReqsForEUGroup(java.lang.Integer)
	 */
	public final void removeTVApplicableReqsForEUGroup(Integer euGroupId)
			throws DAOException {
		checkNull(euGroupId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVApplicableReqsForEUGroup", false);

		connHandler.setInteger(1, euGroupId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#retrieveTVApplicableReqs
	 * (java.lang.Integer)
	 */
	public final TVApplicableReq[] retrieveTVApplicableReqs(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicableReqs", true);

		connHandler.setInteger(1, appId);

		ArrayList<TVApplicableReq> ret = connHandler
				.retrieveArray(TVApplicableReq.class);

		return ret.toArray(new TVApplicableReq[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * retrieveTVApplicableReqsForEU(java.lang.Integer)
	 */
	public final TVApplicableReq[] retrieveTVApplicableReqsForEU(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicableReqsForEU", true);

		connHandler.setInteger(1, appEUId);

		ArrayList<TVApplicableReq> ret = connHandler
				.retrieveArray(TVApplicableReq.class);

		return ret.toArray(new TVApplicableReq[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * retrieveTVApplicableReqsForEUGroup(java.lang.Integer)
	 */
	public final TVApplicableReq[] retrieveTVApplicableReqsForEUGroup(
			Integer euGroupId) throws DAOException {
		checkNull(euGroupId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicableReqsForEUGroup", true);

		connHandler.setInteger(1, euGroupId);

		ArrayList<TVApplicableReq> ret = connHandler
				.retrieveArray(TVApplicableReq.class);

		return ret.toArray(new TVApplicableReq[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#createTvEuGroup(us
	 * .oh.state.epa.stars2.database.dbObjects.application.TVEUGroup)
	 */
	public final TVEUGroup createTvEuGroup(TVEUGroup group) throws DAOException {
		checkNull(group);
		TVEUGroup ret = group;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createTvEuGroup", false);

		group.setTvEuGroupId(nextSequenceVal("S_TV_EU_Group_Id",
				group.getTvEuGroupId()));

		int i = 1;
		connHandler.setInteger(i++, group.getTvEuGroupId());
		connHandler.setInteger(i++, group.getApplicationId());
		connHandler.setString(i++, group.getTvEuGroupName());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * retrieveTvEuGroupsForApplication(java.lang.Integer)
	 */
	public final TVEUGroup[] retrieveTvEuGroupsForApplication(Integer appId)
			throws DAOException {
		ArrayList<TVEUGroup> result = new ArrayList<TVEUGroup>();
		checkNull(appId);
		ResultSet resultSet = null;

		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("ApplicationSQL.retrieveTvEuGroupsForApplication"));
			psSelect.setInt(1, appId);
			resultSet = psSelect.executeQuery();

			while (resultSet.next()) {
				TVEUGroup group = retrieveTvEuGroup(resultSet.getInt(1));
				result.add(group);
			}
			resultSet.close();
		} catch (Exception e) {
			logger.error("appId=" + appId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}
		return result.toArray(new TVEUGroup[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#retrieveTvEuGroup(
	 * java.lang.Integer)
	 */
	public final TVEUGroup retrieveTvEuGroup(Integer groupId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvEuGroup", true);

		connHandler.setInteger(1, groupId);

		return (TVEUGroup) connHandler.retrieve(TVEUGroup.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#modifyTvEuGroup(us
	 * .oh.state.epa.stars2.database.dbObjects.application.TVEUGroup)
	 */
	public final boolean modifyTvEuGroup(TVEUGroup group) throws DAOException {
		checkNull(group);
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyTvEuGroup", false);
		int i = 1;
		connHandler.setString(i++, group.getTvEuGroupName());
		connHandler.setInteger(i++, group.getLastModified() + 1);
		connHandler.setInteger(i++, group.getTvEuGroupId());
		connHandler.setInteger(i++, group.getApplicationId());
		connHandler.setInteger(i++, group.getLastModified());

		ret = connHandler.update();

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#addTvEuToGroup(java
	 * .lang.Integer, java.lang.Integer)
	 */
	public final boolean addTvEuToGroup(Integer appEuId, Integer groupId)
			throws DAOException {
		checkNull(appEuId);
		checkNull(groupId);
		boolean ret = false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvEuToGroup", false);
		int i = 1;
		connHandler.setInteger(i++, appEuId);
		connHandler.setInteger(i++, groupId);

		ret = connHandler.update();

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#removeTvEusFromGroup
	 * (java.lang.Integer)
	 */
	public final void removeTvEusFromGroup(Integer groupId) throws DAOException {
		checkNull(groupId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvEusFromGroup", false);

		connHandler.setInteger(1, groupId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#removeTvEuGroup(java
	 * .lang.Integer)
	 */
	public final void removeTvEuGroup(Integer groupId) throws DAOException {
		checkNull(groupId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvEuGroup", false);

		connHandler.setInteger(1, groupId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#addTVAltScenarioPteReq
	 * (
	 * us.oh.state.epa.stars2.database.dbObjects.application.TVAltScenarioPteReq
	 * )
	 */
	public final TVAltScenarioPteReq addTVAltScenarioPteReq(
			TVAltScenarioPteReq req) throws DAOException {

		checkNull(req);
		TVAltScenarioPteReq ret = req;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createTVAltScenarioPteReq", false);

		int i = 1;

		connHandler.setInteger(i++, req.getTvAltScenarioPteReqId());
		connHandler.setInteger(i++, req.getApplicationEuId());
		connHandler.setInteger(i++, req.getTvEuOperatingScenarioId());
		connHandler.setString(i++, req.getPollutantCd());
		connHandler.setFloat(i++, req.getAllowable());
		connHandler.setString(i++, req.getEmissionUnitsCd());
		connHandler.setString(i++, req.getApplicableReq());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVAltScenarioPteReqsForEu(java.lang.Integer, java.lang.Integer)
	 */
	public final void removeTVAltScenarioPteReqsForEu(Integer appEuId)
			throws DAOException {
		checkNull(appEuId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVAltScenarioPteReqsForEu", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVAltScenarioPteReqsForScenario(java.lang.Integer,
	 * java.lang.Integer)
	 */
	public final void removeTVAltScenarioPteReqsForScenario(Integer appEuId,
			Integer scenarioId) throws DAOException {
		checkNull(appEuId);
		checkNull(scenarioId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVAltScenarioPteReqsForScenario", false);

		connHandler.setInteger(1, appEuId);
		connHandler.setInteger(2, scenarioId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * retrieveTVAltScenarioPteReqsForScenario(java.lang.Integer,
	 * java.lang.Integer)
	 */
	public final TVAltScenarioPteReq[] retrieveTVAltScenarioPteReqsForScenario(
			Integer appEUId, Integer tvEuOperatingScenarioId)
			throws DAOException {
		checkNull(appEUId);
		checkNull(tvEuOperatingScenarioId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVAltScenarioPteReqsForScenario", true);

		connHandler.setInteger(1, appEUId);
		connHandler.setInteger(2, tvEuOperatingScenarioId);

		ArrayList<TVAltScenarioPteReq> ret = connHandler
				.retrieveArray(TVAltScenarioPteReq.class);

		return ret.toArray(new TVAltScenarioPteReq[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#modifyTVAltScenarioPteReq
	 * (
	 * us.oh.state.epa.stars2.database.dbObjects.application.TVAltScenarioPteReq
	 * )
	 */
	public final boolean modifyTVAltScenarioPteReq(TVAltScenarioPteReq req)
			throws DAOException {
		checkNull(req);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyTVAltScenarioPteReq", false);
		int i = 1;
		connHandler.setString(i++, req.getPollutantCd());
		connHandler.setFloat(i++, req.getAllowable());
		connHandler.setString(i++, req.getEmissionUnitsCd());
		connHandler.setString(i++, req.getApplicableReq());
		connHandler.setInteger(i++, req.getLastModified() + 1);
		connHandler.setInteger(i++, req.getTvAltScenarioPteReqId());
		connHandler.setInteger(i++, req.getApplicationEuId());
		connHandler.setInteger(i++, req.getTvEuOperatingScenarioId());
		connHandler.setInteger(i++, req.getLastModified());

		return connHandler.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.oh.state.epa.stars2.database.dao.ApplicationDAO#addTVPteAdjustment
	 * (us.oh.state.epa.stars2.database.dbObjects.application.TVPteAdjustment)
	 */
	public final TVPteAdjustment addTVPteAdjustment(TVPteAdjustment pte)
			throws DAOException {

		checkNull(pte);
		TVPteAdjustment ret = pte;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTVPteAdjustment", false);

		int i = 1;
		connHandler.setInteger(i++, pte.getApplicationId());
		connHandler.setString(i++, pte.getEuEmissionTableCd());
		connHandler.setString(i++, pte.getPollutantCd());
		connHandler.setString(i++, pte.getPteAdjusted());
		// connHandler.setFloat(i++, pte.getPteAdjusted());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * removeTVPteAdjustmentForApplication(java.lang.Integer)
	 */
	public final void removeTVPteAdjustmentForApplication(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVPteAdjustmentForApplication", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.ApplicationDAO#
	 * retrieveTVPteAdjustmentForApplication(java.lang.Integer)
	 */
	public final TVPteAdjustment[] retrieveTVPteAdjustmentForApplication(
			Integer appId) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVPteAdjustmentForApplication", true);

		connHandler.setInteger(1, appId);

		ArrayList<TVPteAdjustment> ret = connHandler
				.retrieveArray(TVPteAdjustment.class);

		return ret.toArray(new TVPteAdjustment[0]);
	}

	public final TVCompliance addTvCompliance(TVCompliance compliance)
			throws DAOException {
		checkNull(compliance);
		TVCompliance ret = compliance;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvCompliance", false);

		int i = 1;
		connHandler.setInteger(i++, compliance.getTvApplicableReqId());
		connHandler.setInteger(i++, compliance.getComplianceId());
		connHandler.setString(i++, compliance.getComplianceApproachReq());
		connHandler.setString(i++, compliance.getComplianceApproach());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public final TVComplianceObligations addTvComplianceObligations(
			TVComplianceObligations obligations) throws DAOException {
		checkNull(obligations);
		TVComplianceObligations ret = obligations;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvComplianceObligations", false);

		int i = 1;
		connHandler.setInteger(i++, obligations.getTvApplicableReqId());
		connHandler.setInteger(i++, obligations.getComplianceObligationsId());
		connHandler.setString(i++, obligations.getComplianceObligationsReq());
		connHandler.setString(i++, obligations.getComplianceObligationsLimit());
		connHandler.setString(i++, obligations.getComplianceObligationsBasis());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public final TVProposedAltLimits addTvProposedAltLimits(
			TVProposedAltLimits altLimits) throws DAOException {
		checkNull(altLimits);
		TVProposedAltLimits ret = altLimits;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvProposedAltLimits", false);

		int i = 1;
		connHandler.setInteger(i++, altLimits.getTvApplicableReqId());
		connHandler.setInteger(i++, altLimits.getProposedAltLimitsId());
		connHandler.setString(i++, altLimits.getProposedAltLimitsReq());
		connHandler.setString(i++, altLimits.getProposedAltLimits());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public final TVProposedExemptions addTvProposedExemptions(
			TVProposedExemptions exemptions) throws DAOException {
		checkNull(exemptions);
		TVProposedExemptions ret = exemptions;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvProposedExemptions", false);

		int i = 1;
		connHandler.setInteger(i++, exemptions.getTvApplicableReqId());
		connHandler.setInteger(i++, exemptions.getProposedExemptionsId());
		connHandler.setString(i++, exemptions.getProposedExemptionsReq());
		connHandler.setString(i++, exemptions.getProposedExemptions());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public final TVProposedTestChanges addTvProposedTestChanges(
			TVProposedTestChanges testChanges) throws DAOException {
		checkNull(testChanges);
		TVProposedTestChanges ret = testChanges;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvProposedTestChanges", false);

		int i = 1;
		connHandler.setInteger(i++, testChanges.getTvApplicableReqId());
		connHandler.setInteger(i++, testChanges.getProposedTestChangesId());
		connHandler.setString(i++, testChanges.getProposedTestChangesReq());
		connHandler.setString(i++, testChanges.getProposedTestChanges());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public int getMaxComplianceId(Integer tvApplicableReqId)
			throws DAOException {
		// default to 1 if no ids exist for this applicable requirement
		int result = 1;
		ResultSet resultSet = null;

		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("ApplicationSQL.getMaxComplianceId"));
			psSelect.setInt(1, tvApplicableReqId);
			resultSet = psSelect.executeQuery();

			if (resultSet.next()) {
				result = resultSet.getInt(1) + 1;
			}
			resultSet.close();
		} catch (Exception e) {
			logger.error("tvApplicableReqId=" + tvApplicableReqId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}

		return result;
	}

	public int getMaxComplianceObligationsId(Integer tvApplicableReqId)
			throws DAOException {
		// default to 1 if no ids exist for this applicable requirement
		int result = 1;
		ResultSet resultSet = null;

		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("ApplicationSQL.getMaxComplianceObligationsId"));
			psSelect.setInt(1, tvApplicableReqId);
			resultSet = psSelect.executeQuery();

			if (resultSet.next()) {
				result = resultSet.getInt(1) + 1;
			}
			resultSet.close();
		} catch (Exception e) {
			logger.error("tvApplicableReqId=" + tvApplicableReqId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}

		return result;
	}

	public int getMaxProposedAltLimitsId(Integer tvApplicableReqId)
			throws DAOException {
		// default to 1 if no ids exist for this applicable requirement
		int result = 1;
		ResultSet resultSet = null;

		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("ApplicationSQL.getMaxProposedAltLimitsId"));
			psSelect.setInt(1, tvApplicableReqId);
			resultSet = psSelect.executeQuery();

			if (resultSet.next()) {
				result = resultSet.getInt(1) + 1;
			}
			resultSet.close();
		} catch (Exception e) {
			logger.error("tvApplicableReqId=" + tvApplicableReqId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}

		return result;
	}

	public int getMaxProposedExemptionsId(Integer tvApplicableReqId)
			throws DAOException {
		// default to 1 if no ids exist for this applicable requirement
		int result = 1;
		ResultSet resultSet = null;

		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("ApplicationSQL.getMaxProposedExemptionsId"));
			psSelect.setInt(1, tvApplicableReqId);
			resultSet = psSelect.executeQuery();

			if (resultSet.next()) {
				result = resultSet.getInt(1) + 1;
			}
			resultSet.close();
		} catch (Exception e) {
			logger.error("tvApplicableReqId=" + tvApplicableReqId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}

		return result;
	}

	public int getMaxProposedTestChangesId(Integer tvApplicableReqId)
			throws DAOException {
		// default to 1 if no ids exist for this applicable requirement
		int result = 1;
		ResultSet resultSet = null;

		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("ApplicationSQL.getMaxProposedTestChangesId"));
			psSelect.setInt(1, tvApplicableReqId);
			resultSet = psSelect.executeQuery();

			if (resultSet.next()) {
				result = resultSet.getInt(1) + 1;
			}
			resultSet.close();
		} catch (Exception e) {
			logger.error("tvApplicableReqId=" + tvApplicableReqId.toString(), e);
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}

		return result;
	}

	public void removeTvComplianceForAppReq(Integer tvApplicableReqId)
			throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvComplianceForAppReq", false);

		connHandler.setInteger(1, tvApplicableReqId);

		connHandler.remove();
	}

	public void removeTvComplianceObligationsForAppReq(Integer tvApplicableReqId)
			throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvComplianceObligationsForAppReq", false);

		connHandler.setInteger(1, tvApplicableReqId);

		connHandler.remove();
	}

	public void removeTvProposedAltLimitsForAppReq(Integer tvApplicableReqId)
			throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvProposedAltLimitsForAppReq", false);

		connHandler.setInteger(1, tvApplicableReqId);

		connHandler.remove();
	}

	public void removeTvProposedExemptionsForAppReq(Integer tvApplicableReqId)
			throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvProposedExemptionsForAppReq", false);

		connHandler.setInteger(1, tvApplicableReqId);

		connHandler.remove();
	}

	public void removeTvProposedTestChangesForAppReq(Integer tvApplicableReqId)
			throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvProposedTestChangesForAppReq", false);

		connHandler.setInteger(1, tvApplicableReqId);

		connHandler.remove();
	}

	public final TVCompliance[] retrieveTvComplianceForAppReq(
			Integer tvApplicableReqId) throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvComplianceForAppReq", true);

		connHandler.setInteger(1, tvApplicableReqId);

		ArrayList<TVCompliance> ret = connHandler
				.retrieveArray(TVCompliance.class);

		return ret.toArray(new TVCompliance[0]);
	}

	public final TVComplianceObligations[] retrieveTvComplianceObligationsForAppReq(
			Integer tvApplicableReqId) throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvComplianceObligationsForAppReq", true);

		connHandler.setInteger(1, tvApplicableReqId);

		ArrayList<TVComplianceObligations> ret = connHandler
				.retrieveArray(TVComplianceObligations.class);

		return ret.toArray(new TVComplianceObligations[0]);
	}

	public final TVProposedAltLimits[] retrieveTvProposedAltLimitsForAppReq(
			Integer tvApplicableReqId) throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvProposedAltLimitsForAppReq", true);

		connHandler.setInteger(1, tvApplicableReqId);

		ArrayList<TVProposedAltLimits> ret = connHandler
				.retrieveArray(TVProposedAltLimits.class);

		return ret.toArray(new TVProposedAltLimits[0]);
	}

	public final TVProposedExemptions[] retrieveTvProposedExemptionsForAppReq(
			Integer tvApplicableReqId) throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvProposedExemptionsForAppReq", true);

		connHandler.setInteger(1, tvApplicableReqId);

		ArrayList<TVProposedExemptions> ret = connHandler
				.retrieveArray(TVProposedExemptions.class);

		return ret.toArray(new TVProposedExemptions[0]);
	}

	public final TVProposedTestChanges[] retrieveTvProposedTestChangesForAppReq(
			Integer tvApplicableReqId) throws DAOException {
		checkNull(tvApplicableReqId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvProposedTestChangesForAppReq", true);

		connHandler.setInteger(1, tvApplicableReqId);

		ArrayList<TVProposedTestChanges> ret = connHandler
				.retrieveArray(TVProposedTestChanges.class);

		return ret.toArray(new TVProposedTestChanges[0]);
	}

	public final String[] retrievePermitTypeForRPCType(String rpcTypeCd)
			throws DAOException {
		checkNull(rpcTypeCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePermitTypeForRPCType", true);

		connHandler.setString(1, rpcTypeCd);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	public PBRNotificationDocument createPBRNotificationDocument(
			PBRNotificationDocument doc) throws DAOException {
		checkNull(doc);
		PBRNotificationDocument ret = doc;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createPBRNotificationDocument", false);

		int i = 1;
		connHandler.setInteger(i++, doc.getDocumentID());
		connHandler.setInteger(i++, doc.getApplicationId());
		connHandler.setString(i++, doc.getDocTypeCd());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public RPCRequestDocument createRPCRequestDocument(RPCRequestDocument doc)
			throws DAOException {
		checkNull(doc);
		RPCRequestDocument ret = doc;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createRPCRequestDocument", false);

		int i = 1;
		connHandler.setInteger(i++, doc.getDocumentID());
		connHandler.setInteger(i++, doc.getApplicationId());
		connHandler.setString(i++, doc.getDocTypeCd());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public boolean modifyPBRNotificationDocument(PBRNotificationDocument doc)
			throws DAOException {
		checkNull(doc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyPBRNotificationDocument", false);

		int i = 1;
		connHandler.setString(i++, doc.getDocTypeCd());
		connHandler.setInteger(i++, doc.getLastModified() + 1);
		connHandler.setInteger(i++, doc.getApplicationId());
		connHandler.setInteger(i++, doc.getDocumentID());
		connHandler.setInteger(i++, doc.getLastModified());

		return connHandler.update();
	}

	public boolean modifyRPCRequestDocument(RPCRequestDocument doc)
			throws DAOException {
		checkNull(doc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyRPCRequestDocument", false);

		int i = 1;
		connHandler.setString(i++, doc.getDocTypeCd());
		connHandler.setInteger(i++, doc.getLastModified() + 1);
		connHandler.setInteger(i++, doc.getApplicationId());
		connHandler.setInteger(i++, doc.getDocumentID());
		connHandler.setInteger(i++, doc.getLastModified());

		return connHandler.update();
	}

	public void removePBRNotificationDocument(PBRNotificationDocument doc)
			throws DAOException {
		checkNull(doc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePBRNotificationDocument", false);
		connHandler.setInteger(1, doc.getApplicationId());
		connHandler.setInteger(2, doc.getDocumentID());
		connHandler.remove();
	}

	public void removePBRNotificationDocuments(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removePBRNotificationDocuments", false);
		connHandler.setInteger(1, appId);
		connHandler.remove();
	}

	public void removeRPCRequestDocument(RPCRequestDocument doc)
			throws DAOException {
		checkNull(doc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeRPCRequestDocument", false);
		connHandler.setInteger(1, doc.getApplicationId());
		connHandler.setInteger(2, doc.getDocumentID());
		connHandler.remove();
	}

	public void removeRPCRequestDocuments(Integer appId) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeRPCRequestDocuments", false);
		connHandler.setInteger(1, appId);
		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#retrievePBRNotificationDocument(Integer appId,
	 *      Integer docId)
	 */
	public final PBRNotificationDocument retrievePBRNotificationDocument(
			Integer appId, Integer docId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePBRNotificationDocument", true);

		connHandler.setInteger(1, appId);
		connHandler.setInteger(2, docId);

		return (PBRNotificationDocument) connHandler
				.retrieve(PBRNotificationDocument.class);
	}

	/**
	 * @see ApplicationDAO#retrievePBRNotificationDocuments(Integer appId)
	 */
	public final PBRNotificationDocument[] retrievePBRNotificationDocuments(
			Integer appId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievePBRNotificationDocuments", true);

		connHandler.setInteger(1, appId);

		ArrayList<PBRNotificationDocument> tempArray = connHandler
				.retrieveArray(PBRNotificationDocument.class);

		return tempArray.toArray(new PBRNotificationDocument[0]);
	}

	/**
	 * @see ApplicationDAO#retrieveRPCRequestDocument(Integer appId, Integer
	 *      docId)
	 */
	public final RPCRequestDocument retrieveRPCRequestDocument(Integer appId,
			Integer docId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveRPCRequestDocument", true);

		connHandler.setInteger(1, appId);
		connHandler.setInteger(2, docId);

		return (RPCRequestDocument) connHandler
				.retrieve(RPCRequestDocument.class);
	}

	/**
	 * @see ApplicationDAO#retrieveRPCRequestDocuments(Integer appId)
	 */
	public final RPCRequestDocument[] retrieveRPCRequestDocuments(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveRPCRequestDocuments", true);

		connHandler.setInteger(1, appId);

		ArrayList<RPCRequestDocument> tempArray = connHandler
				.retrieveArray(RPCRequestDocument.class);

		return tempArray.toArray(new RPCRequestDocument[0]);
	}

	public final ApplicationSearchResult[] retrieveApplicationSearchResults(
			String applicationNumber, String euId, String facilityId,
			String facilityName, String doLaaCd, String countyCd,
			String applicationType, String ptioReasonCd,
			boolean legacyStatePTOFlag, String pbrTypeCd, String permitNumber,
			String companyName, boolean unlimitedResults) throws DAOException {

		StringBuffer statementSQL = new StringBuffer(
				loadSQL("ApplicationSQL.retrieveApplicationSearchResults"));

		StringBuffer whereClause = new StringBuffer("");
		String conjuct = " WHERE ";

		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}

		if (applicationNumber != null && applicationNumber.trim().length() > 0) {
			whereClause.append(conjuct);
			conjuct = " AND ";
			whereClause.append(" LOWER(pa.application_nbr) LIKE ");
			whereClause.append("LOWER('");
			whereClause
					.append(SQLizeString(applicationNumber.replace("*", "%")));
			whereClause.append("')");
		}

		// bug 1996
		if (euId != null && euId.length() != 0) {
			if (euId.contains("*")) {
				euId = SQLizeString(euId.replace('*', '%'));
			}
			whereClause.append(conjuct);
			conjuct = " AND ";

			String euWhere = loadSQL("ApplicationSQL.applicationSearchWithEuId");

			if (euId.contains("%")) {
				euWhere = euWhere.replace("%ApplicationSearchEUWhere%",
						"LOWER(feu.EPA_EMU_ID) like LOWER('" + euId + "')");
			} else {
				euWhere = euWhere.replace("%ApplicationSearchEUWhere%",
						"LOWER(feu.EPA_EMU_ID) = LOWER('" + euId + "')");
			}
			whereClause.append(euWhere);
		}

		if (facilityId != null && facilityId.trim().length() > 0) {
			facilityId = formatFacilityId(facilityId);
			whereClause.append(conjuct);
			conjuct = " AND ";
			whereClause.append(" LOWER(ff.facility_id) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityId.replace("*", "%")));
			whereClause.append("')");
		}

		if (facilityName != null && facilityName.trim().length() > 0) {
			whereClause.append(conjuct);
			conjuct = " AND ";
			whereClause.append(" LOWER(ff.facility_nm) LIKE ");
			whereClause.append("LOWER('");
			whereClause.append(SQLizeString(facilityName.replace("*", "%")));
			whereClause.append("')");
		}

		if (applicationType != null) {
			whereClause.append(conjuct);
			conjuct = " AND ";
			whereClause.append(" pa.application_type_cd = '");
			whereClause.append(applicationType);
			whereClause.append("'");
		}

		if (ApplicationTypeDef.PTIO_APPLICATION.equals(applicationType)) {
			if (ptioReasonCd != null) {
				String permitClause = loadSQL("ApplicationSQL.applicationSearchByPTIOTypeAndClause");
				whereClause.append(conjuct);
				conjuct = " AND ";
				whereClause.append(permitClause.replace("APP_PURPOSE_CD",
						ptioReasonCd));
			}
			if (legacyStatePTOFlag) {
				String permitClause = loadSQL("ApplicationSQL.applicationSearchByLegacyPTOAndClause");
				whereClause.append(conjuct);
				conjuct = " AND ";
				whereClause.append(permitClause);
			}
		} /*
		 * Remove PBR references in permit applications else if
		 * (ApplicationTypeDef.PBR_NOTIFICATION.equals(applicationType) &&
		 * pbrTypeCd != null) { String permitClause =
		 * loadSQL("ApplicationSQL.applicationSearchByBRTypeAndClause");
		 * whereClause.append(conjuct); conjuct = " AND ";
		 * whereClause.append(permitClause.replace("PBR_TYPE_CD", pbrTypeCd)); }
		 */
		if (doLaaCd != null) {
			whereClause.append(conjuct);
			conjuct = " AND ";
			whereClause.append(" ff.do_laa_cd = '");
			whereClause.append(doLaaCd);
			whereClause.append("'");
		}

		if (countyCd != null) {
			whereClause.append(conjuct);
			conjuct = " AND ";
			whereClause.append(" county_cd = '");
			whereClause.append(countyCd);
			whereClause.append("'");
		}

		if (permitNumber != null && permitNumber.trim().length() > 0) {
			String permitClause = loadSQL("ApplicationSQL.applicationSearchByPermitAndClause");
			whereClause.append(conjuct);
			conjuct = " AND ";
			whereClause.append(permitClause.replace("PERMIT_NBR",
					SQLizeString(permitNumber.replace("*", "%"))));
		}

		if (companyName != null) {
			whereClause.append(conjuct);
			conjuct = " AND ";
			whereClause.append(" ccm.CMP_ID = '");
			whereClause.append(companyName);
			whereClause.append("'");
		}
		
		// In Public website, show Applications in Submitted State only.
    	if(CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
    		whereClause.append(conjuct);
    		conjuct = " AND ";
    		whereClause.append(" pa.submitted_date is not null ");
    	}

		// sort by descending submitted date, forcing values with null
		// submitted date to the end of the list
		StringBuffer sortBy = new StringBuffer(
				" ORDER BY submitted_date desc, received_date desc, application_nbr");

		statementSQL.append(whereClause.toString());
		statementSQL.append(" ");
		statementSQL.append(sortBy.toString());

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL.toString());
		ArrayList<ApplicationSearchResult> ret = connHandler.retrieveArray(
				ApplicationSearchResult.class, defaultSearchLimit);
		
		logger.debug("Retrieve applications SQL = " + statementSQL);

		// TODO figure out how to make this work better - want to avoid querying
		// ALL permits/applciations
		HashMap<Integer, String> permitNumberMap = retrievePermitNumbersForApplications(whereClause
				.toString());
		for (ApplicationSearchResult res : ret) {
			res.setPermitNumbers(permitNumberMap.get(res.getApplicationId()));
		}

		return ret.toArray(new ApplicationSearchResult[0]);
	}

	/**
	 * Retrieve a mapping of application id to a comma separated string listing
	 * the permit numbers associated with that application.
	 * 
	 * @param applicationNumber
	 *            if not null, limit results to applications matching the
	 *            specified application number.
	 * @param permitNumber
	 *            if not null, limits results to permits matching the specified
	 *            permit number.
	 * @return
	 * @throws DAOException
	 */
	private HashMap<Integer, String> retrievePermitNumbersForApplications(
			String whereClause) throws DAOException {
		HashMap<Integer, String> result = new HashMap<Integer, String>();

		String statementSQL = loadSQL("ApplicationSQL.retrievePermitNumbersForApplications");

		if (whereClause == null || whereClause.trim().length() == 0) {
			statementSQL = statementSQL.replaceAll("WHERE_CLAUSE", "");
		} else {
			statementSQL = statementSQL.replaceAll("WHERE_CLAUSE", whereClause);
		}
		
		logger.debug("Retrieve permit numbers for applications SQL = " + statementSQL);

		ResultSet resultSet = null;
		Connection conn = null;
		PreparedStatement psSelect = null;
		conn = getConnection();
		try {
			psSelect = conn.prepareStatement(statementSQL);
			resultSet = psSelect.executeQuery();

			while (resultSet.next()) {
				Integer applicationId = getInteger(resultSet, 1);
				String permitNumberList = result.get(applicationId);
				if (permitNumberList == null) {
					permitNumberList = resultSet.getString(2);
				} else {
					permitNumberList = permitNumberList + ", "
							+ resultSet.getString(2);
				}
				result.put(applicationId, permitNumberList);
			}
		} catch (SQLException e) {
			logger.error("WHERE_CLAUSE: " + whereClause, e);
			handleException(e, conn);
		} finally {
			handleClosing(conn);
		}

		return result;
	}

	public final String[] retrieveApplicationPermitNumbers(int applicationID)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationPermitNumbers", true);

		connHandler.setInteger(1, applicationID);

		ArrayList<String> tempNumbers = connHandler
				.retrieveJavaObjectArray(String.class);

		return tempNumbers.toArray(new String[0]);
	}

	public PBRExport[] retrievePBRExport() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrivePBRExport", true);

		ArrayList<PBRExport> ret = connHandler.retrieveArray(PBRExport.class);

		return ret.toArray(new PBRExport[0]);
	}

	public TIVApplication createTIVApplication(TIVApplication newTIVApp)
			throws DAOException {
		checkNull(newTIVApp);
		TIVApplication ret = newTIVApp;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createTIVApplication", false);

		int i = 1;
		connHandler.setInteger(i++, newTIVApp.getApplicationID());
		connHandler.setString(i++, newTIVApp.getAppPurposeCd());
		connHandler.setString(i++, newTIVApp.getReasonCd());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	public boolean modifyTIVApplication(TIVApplication tivApp)
			throws DAOException {
		checkNull(tivApp);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyTIVApplication", false);

		int i = 1;
		connHandler.setString(i++, tivApp.getAppPurposeCd());
		connHandler.setString(i++, tivApp.getReasonCd());
		connHandler.setInteger(i++, tivApp.getLastModified() + 1);
		connHandler.setInteger(i++, tivApp.getApplicationID());
		connHandler.setInteger(i++, tivApp.getLastModified());

		return connHandler.update();
	}

	public int retrieveAttachmentReferenceCount(int applicationDocumentId,
			int documentId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveAttachmentReferenceCount", true);
		connHandler.setInteger(1, documentId);
		connHandler.setInteger(2, documentId);
		connHandler.setInteger(3, applicationDocumentId);
		return ((Integer) connHandler
				.retrieveJavaObject(java.lang.Integer.class)).intValue();
	}

	public Integer[] retrieveOrphanEus(int applicationId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveOrphanEus", true);

		connHandler.setInteger(1, applicationId);

		ArrayList<Integer> euIds = connHandler
				.retrieveJavaObjectArray(Integer.class);

		return euIds.toArray(new Integer[0]);
	}

	@Override
	public NSRApplicationBACTEmission[] retrieveNSRApplicationBactEmissions(
			Integer appEUId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveNSRApplicationBACTEmissions", true);
		checkNull(appEUId);

		connHandler.setInteger(1, appEUId);

		ArrayList<NSRApplicationBACTEmission> ret = connHandler
				.retrieveArray(NSRApplicationBACTEmission.class);

		return ret.toArray(new NSRApplicationBACTEmission[0]);
	}

	@Override
	public void removeBACTEmissions(Integer applicationEuId)
			throws DAOException {
		checkNull(applicationEuId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeBACTEmissions", false);

		connHandler.setInteger(1, applicationEuId);

		connHandler.remove();

	}

	@Override
	public NSRApplicationBACTEmission addBACTEmission(
			NSRApplicationBACTEmission bactEmission) throws DAOException {
		checkNull(bactEmission);
		NSRApplicationBACTEmission ret = bactEmission;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addBACTEmission", false);

		int i = 1;
		connHandler.setInteger(i++, bactEmission.getApplicationEuId());
		connHandler.setString(i++, bactEmission.getPollutantCd());
		connHandler.setString(i++, bactEmission.getBact());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	@Override
	public NSRApplicationLAEREmission[] retrieveNSRApplicationLaerEmissions(
			Integer appEUId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveNSRApplicationLAEREmissions", true);
		checkNull(appEUId);

		connHandler.setInteger(1, appEUId);

		ArrayList<NSRApplicationLAEREmission> ret = connHandler
				.retrieveArray(NSRApplicationLAEREmission.class);

		return ret.toArray(new NSRApplicationLAEREmission[0]);
	}

	@Override
	public void removeLAEREmissions(Integer applicationEuId)
			throws DAOException {
		checkNull(applicationEuId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeLAEREmissions", false);

		connHandler.setInteger(1, applicationEuId);

		connHandler.remove();

	}

	@Override
	public NSRApplicationLAEREmission addLAEREmission(
			NSRApplicationLAEREmission laerEmission) throws DAOException {
		checkNull(laerEmission);
		NSRApplicationLAEREmission ret = laerEmission;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addLAEREmission", false);

		int i = 1;
		connHandler.setInteger(i++, laerEmission.getApplicationEuId());
		connHandler.setString(i++, laerEmission.getPollutantCd());
		connHandler.setString(i++, laerEmission.getLaer());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	@Override
	public boolean checkApplicationTypeIsInDb(Integer applicationId,
			Integer appEuId, String typeCd, boolean isRequired)
			throws DAOException {
		if (applicationId == null)
			return false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.checkApplicationTypeIsInDb", false);

		connHandler.setInteger(1, applicationId);
		connHandler.setString(2, typeCd);
		connHandler.setBoolean(3, isRequired);
		connHandler.setInteger(4, appEuId);
		connHandler.setInteger(5, appEuId);

		Integer result = (Integer) connHandler
				.retrieveJavaObject(Integer.class);

		if (result > 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean removeReqruiedAttachmnetApplicationDoc(
			Integer applicationId, Integer appEuId, String typeCd)
			throws DAOException {
		if (applicationId == null)
			return false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeReqruiedAttachmnetApplicationDoc", false);

		connHandler.setInteger(1, applicationId);
		connHandler.setString(2, typeCd);
		connHandler.setInteger(3, appEuId);
		connHandler.setInteger(4, appEuId);

		return connHandler.remove();
	}

	@Override
	public boolean changeReqruiedAttachmnetToOption(Integer applicationId,
			Integer appEuId, String typeCd) throws DAOException {
		if (applicationId == null)
			return false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.changeReqruiedAttachmnetToOption", false);

		connHandler.setInteger(1, applicationId);
		connHandler.setString(2, typeCd);
		connHandler.setInteger(3, appEuId);
		connHandler.setInteger(4, appEuId);

		return connHandler.remove();
	}

	@Override
	public boolean changeOptionAttachmnetToRequired(Integer applicationId,
			Integer appEuId, String typeCd) throws DAOException {
		if (applicationId == null)
			return false;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.changeOptionAttachmentToReqruied", false);

		connHandler.setInteger(1, applicationId);
		connHandler.setString(2, typeCd);
		connHandler.setInteger(3, appEuId);
		connHandler.setInteger(4, appEuId);

		return connHandler.remove();
	}

	@Override
	public Integer retrieveRequiredApplicationDocId(Integer applicationId,
			Integer emissionUnitId, String typeCd) throws DAOException {
		if (applicationId == null)
			return 0;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveRequiredApplicationDocId", false);

		connHandler.setInteger(1, applicationId);
		connHandler.setString(2, typeCd);
		connHandler.setInteger(3, emissionUnitId);
		connHandler.setInteger(4, emissionUnitId);

		return (Integer) connHandler.retrieveJavaObject(Integer.class);
	}

	/**
	 * @see ApplicationDAO#addPTIOAppMACTSubpartCd(Integer appId, String
	 *      subpartCd)
	 */
	@Override
	public List<FacilityWideRequirement> retrieveFacilityWideRequirements(
			Integer applicationId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrievefacilityWideRequirements", false);

		connHandler.setInteger(1, applicationId);

		return connHandler.retrieveArray(FacilityWideRequirement.class);
	}

	@Override
	public void createFacilityWideRequirement(FacilityWideRequirement facWideReq)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createFacilityWideRequirements", false);

		connHandler.setInteger(1, facWideReq.getApplicationId());
		connHandler.setString(2, facWideReq.getDescription());
		connHandler.setString(3, facWideReq.getProposedMethod());

		connHandler.update();
	}

	@Override
	public void modifyFacilityWideRequirement(FacilityWideRequirement facWideReq)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyFacilityWideRequirements", false);

		connHandler.setString(1, facWideReq.getDescription());
		connHandler.setString(2, facWideReq.getProposedMethod());
		connHandler.setInteger(3, facWideReq.getRequirementId());

		connHandler.update();
	}

	@Override
	public void removeFacilityWideRequirement(Integer requirementId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeFacilityWideRequirement", false);

		connHandler.setInteger(1, requirementId);

		connHandler.remove();
	}
	
	@Override
	public void removeFacilityWideRequirements(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeFacilityWideRequirements", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#addPTIOAppMACTSubpartCd(Integer appId, String
	 *      subpartCd)
	 */
	@Override
	public final boolean addTvEuAppMACTSubpartCd(Integer appId, String subpartCd)
			throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvEuAppMACTSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#addPTIOAppNESHAPSubpartCd(Integer appId, String
	 *      subpartCd)
	 */
	@Override
	public final boolean addTvEuAppNESHAPSubpartCd(Integer appId,
			String subpartCd) throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvEuAppNESHAPSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#addPTIOAppNSPSSubpartCd(Integer appId, String
	 *      subpartCd)
	 */
	@Override
	public final boolean addTvEuAppNSPSSubpartCd(Integer appId, String subpartCd)
			throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvEuAppNSPSSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	/**
	 * @see ApplicationDAO#removePTIOAppMACTSubpartCds(Integer appId)
	 */
	@Override
	public final void removeTvEuAppMACTSubpartCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvEuAppMACTSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removePTIOAppNESHAPSubpartCds(Integer appId)
	 */
	@Override
	public final void removeTvEuAppNESHAPSubpartCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvEuAppNESHAPSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#removePTIOAppNSPSSubpartCds(Integer appId)
	 */
	@Override
	public final void removeTvEuAppNSPSSubpartCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvEuAppNSPSSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	/**
	 * @see ApplicationDAO#retrievePTIOAppMACTSubpartCds(Integer appId)
	 */
	@Override
	public final String[] retrieveTvEuAppMACTSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvEuAppMACTSubpartCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#retrievePTIOAppNESHAPSubpartCds(Integer appId)
	 */
	@Override
	public final String[] retrieveTvEuAppNESHAPSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvEuAppNESHAPSubpartCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	/**
	 * @see ApplicationDAO#retrievePTIOAppNSPSSubpartCds(Integer appId)
	 */
	@Override
	public final String[] retrieveTvEuAppNSPSSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvEuAppNSPSSubpartCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	@Override
	public final boolean addTvAppMACTSubpartCd(Integer appId, String subpartCd)
			throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvAppMACTSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	@Override
	public final boolean addTvAppNESHAPSubpartCd(Integer appId, String subpartCd)
			throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvAppNESHAPSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	@Override
	public final boolean addTvAppNSPSSubpartCd(Integer appId, String subpartCd)
			throws DAOException {
		checkNull(appId);
		checkNull(subpartCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTvAppNSPSSubpartCd", false);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, subpartCd);

		return connHandler.update();
	}

	@Override
	public final void removeTvAppNESHAPSubpartCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvAppNESHAPSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	@Override
	public final void removeTvAppNSPSSubpartCds(Integer appId)
			throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvAppNSPSSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	@Override
	public void removeTvAppMACTSubpartCds(Integer appId) throws DAOException {
		checkNull(appId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTvAppMACTSubpartCds", false);

		connHandler.setInteger(1, appId);

		connHandler.remove();
	}

	@Override
	public final List<String> retrieveTvAppMACTSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvAppMACTSubpartCds", true);

		connHandler.setInteger(1, appId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public final List<String> retrieveTvAppNESHAPSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvAppNESHAPSubpartCds", true);

		connHandler.setInteger(1, appId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	@Override
	public final List<String> retrieveTvAppNSPSSubpartCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTvAppNSPSSubpartCds", true);

		connHandler.setInteger(1, appId);

		return connHandler.retrieveJavaObjectArray(String.class);
	}

	public final TVEUPollutantLimit[] retrieveTVEUPollutantLimits(
			Integer appEUId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVEUPollutantLimits", true);
		checkNull(appEUId);

		connHandler.setInteger(1, appEUId);

		ArrayList<TVEUPollutantLimit> ret = connHandler
				.retrieveArray(TVEUPollutantLimit.class);

		return ret.toArray(new TVEUPollutantLimit[0]);
	}

	@Override
	public final void removeTVEUPollutantLimits(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVEUPollutantLimits", false);

		connHandler.setInteger(1, appEUId);

		connHandler.remove();
	}

	@Override
	public final TVEUPollutantLimit addTVEUPollutantLimit(Integer appEuId,
			TVEUPollutantLimit pollutantLimit) throws DAOException {
		TVEUPollutantLimit ret = pollutantLimit;
		checkNull(pollutantLimit);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTVEUPollutantLimit", false);

		int i = 1;
		connHandler.setInteger(i++, appEuId);
		connHandler.setString(i++, pollutantLimit.getReqBasisCd());
		connHandler.setString(i++, pollutantLimit.getRuleCite());
		connHandler.setString(i++, pollutantLimit.getNumLimitUnit());
		connHandler.setString(i++, pollutantLimit.getPollutantCd());
		connHandler.setString(i++, pollutantLimit.getAvgPeriod());
		connHandler.setString(i++, pollutantLimit.getComplianceFlag());
		connHandler.setDouble(i++, pollutantLimit.getPotentialEmissions());
		connHandler.setString(i++, pollutantLimit.getDeterminationBasisCd());
		connHandler.setString(i++, pollutantLimit.getCamFlag());
		connHandler.setString(i++, pollutantLimit.getComplianceMethod());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(pollutantLimit.isControlled()));

		connHandler.update();

		return ret;
	}

	@Override
	public final TVEUOperationalRestriction[] retrieveTVEUOperationalRestrictions(
			Integer appEUId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVEUOperationalRestrictions", true);
		checkNull(appEUId);

		connHandler.setInteger(1, appEUId);

		ArrayList<TVEUOperationalRestriction> ret = connHandler
				.retrieveArray(TVEUOperationalRestriction.class);

		return ret.toArray(new TVEUOperationalRestriction[0]);
	}

	@Override
	public final void removeTVEUOperationalRestrictions(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeTVEUOperationalRestrictions", false);

		connHandler.setInteger(1, appEUId);

		connHandler.remove();
	}

	@Override
	public final TVEUOperationalRestriction addTVEUOperationalRestriction(
			Integer appEuId, TVEUOperationalRestriction operationalRestriction)
			throws DAOException {
		TVEUOperationalRestriction ret = operationalRestriction;
		checkNull(operationalRestriction);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addTVEUOperationalRestriction", false);

		int i = 1;
		connHandler.setInteger(i++, appEuId);
		connHandler.setString(i++, operationalRestriction.getReqBasisCd());
		connHandler.setString(i++, operationalRestriction.getRuleCite());
		connHandler.setString(i++,
				operationalRestriction.getRestrictionTypeCd());
		connHandler.setString(i++, operationalRestriction.getRestrictionDesc());
		connHandler.setString(i++, operationalRestriction.getComplianceFlag());
		connHandler
				.setString(i++, operationalRestriction.getComplianceMethod());

		connHandler.update();

		return ret;
	}

	@Override
	public ArrayList<ApplicationDocumentTypeDef> retrieveApplicationDocumentTypes()
			throws DAOException {
		ArrayList<ApplicationDocumentTypeDef> ret = null;
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocTypes", false);
		try {
			ret = connHandler.retrieveArray(ApplicationDocumentTypeDef.class);
		} catch (DAOException ex) {
			throw ex;
		}

		return ret;
	}

	@Override
	public ArrayList<TVApplicationDocumentTypeDef> retrieveTvApplicationDocumentTypes()
			throws DAOException {
		ArrayList<TVApplicationDocumentTypeDef> ret = null;
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicationDocTypes", false);
		try {
			ret = connHandler.retrieveArray(TVApplicationDocumentTypeDef.class);
		} catch (DAOException ex) {
			throw ex;
		}

		return ret;
	}
	
	@Override
	public final void setActiveApplicationsValidatedFlag(Integer fpId,
			boolean validated) throws DAOException {
		checkNull(fpId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.setActiveApplicationsValidatedFlag", false);
		connHandler.setString(1,
				AbstractDAO.translateBooleanToIndicator(validated));
		connHandler.setInteger(2, fpId);
		connHandler.updateNoCheck();
	}
	
	@Override
	public ApplicationEUFugitiveLeaks createApplicationEUFugitiveLeaks(
			ApplicationEUFugitiveLeaks figitiveLeaks)
			throws DAOException {
		ApplicationEUFugitiveLeaks ret = figitiveLeaks;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.createApplicationEUFugitiveLeaks", false);

		Integer fugutiveLeaksId = nextSequenceVal("S_FugitiveLeaks_Id");

		int index = 1;
		connHandler.setInteger(index++, fugutiveLeaksId);
		int i = 1;
		connHandler.setInteger(i++, fugutiveLeaksId);
		connHandler.setInteger(i++, figitiveLeaks.getApplicationEuId());
		connHandler.setString(i++, figitiveLeaks.getEquipmentServiceTypeCd());
		connHandler.setInteger(i++, figitiveLeaks.getEquipmentTypeNumber());
		connHandler.setFloat(i++, figitiveLeaks.getLeakRate());
		connHandler.setBigDecimal(i++, figitiveLeaks.getPercentVoc());
		connHandler.update();
		ret.setFugitiveLeaksId(fugutiveLeaksId);
		ret.setLastModified(1);

		return ret;
	}
	
	@Override
	public List<ApplicationEUFugitiveLeaks> retrieveApplicationEUFugitiveLeaks(
			Integer appEuId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationEUFugitiveLeaks", false);

		connHandler.setInteger(1, appEuId);

		List<ApplicationEUFugitiveLeaks> applicationEUFugitiveLeaks = connHandler
				.retrieveArray(ApplicationEUFugitiveLeaks.class);

		return applicationEUFugitiveLeaks;
	}
	

	@Override
	public void removeApplicationEUFugitiveLeaks(Integer appEuId)
			throws DAOException {
		 ConnectionHandler connHandler = new ConnectionHandler(
		 "ApplicationSQL.removeAllApplicationEUFugitiveLeaks", false);
		
		 connHandler.setInteger(1, appEuId);
		
		 connHandler.remove();



	}
	
	public final ApplicationEUMaterialUsed addApplicationEUMaterialUsed(
			ApplicationEUMaterialUsed materialUsed) throws DAOException {

		checkNull(materialUsed);
		ApplicationEUMaterialUsed ret = materialUsed;

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.addApplicationEUMaterialUsed", false);

		int i = 1;
		connHandler.setInteger(i++, materialUsed.getApplicationEuId());
		connHandler.setString(i++, materialUsed.getMaterialUsedCd());
		connHandler.setString(i++, materialUsed.getMaterialAmount());
		connHandler.setString(i++, materialUsed.getUnitCd());
		connHandler.update();
		ret.setLastModified(1);

		return ret;
	}
	
	public final boolean modifyApplicationEUMaterialUsed(
			ApplicationEUMaterialUsed materialUsed) throws DAOException {
		checkNull(materialUsed);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.modifyApplicationEUMaterialUsed", false);

		int i = 1;
		connHandler.setString(i++, materialUsed.getMaterialAmount());
		connHandler.setString(i++, materialUsed.getUnitCd());
		connHandler.setInteger(i++, materialUsed.getLastModified() + 1);
		connHandler.setInteger(i++, materialUsed.getApplicationEuId());
		connHandler.setString(i++, materialUsed.getMaterialUsedCd());
		connHandler.setInteger(i++, materialUsed.getLastModified());

		return connHandler.update();
	}
	
	public final void removeApplicationEUMaterialUsed(Integer appEUId)
			throws DAOException {
		checkNull(appEUId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.removeAllApplicationEUMaterialUsedForEU", false);

		connHandler.setInteger(1, appEUId);

		connHandler.remove();
	}
	
	public final ApplicationEUMaterialUsed[] retrieveApplicationEUMaterialUsed(
			Integer appEUId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationEUMaterialUsed", true);
		checkNull(appEUId);

		connHandler.setInteger(1, appEUId);

		ArrayList<ApplicationEUMaterialUsed> ret = connHandler
				.retrieveArray(ApplicationEUMaterialUsed.class);

		return ret.toArray(new ApplicationEUMaterialUsed[0]);
	}
	
	@Override
	public final ApplicationDocumentTypeDef retrieveApplicationDocTypeDef(String applicationDocumentTypeCD)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocTypeDef", false);
		
		connHandler.setString(1, applicationDocumentTypeCD);
		
		return (ApplicationDocumentTypeDef) connHandler.retrieve(ApplicationDocumentTypeDef.class);
	}
	
	@Override
	public final ApplicationDocumentTypeDef retrieveTVApplicationDocTypeDef(String applicationDocumentTypeCD)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicationDocTypeDef", false);
		
		connHandler.setString(1, applicationDocumentTypeCD);
		
		return (ApplicationDocumentTypeDef) connHandler.retrieve(ApplicationDocumentTypeDef.class);
	}

	@Override
	public Boolean applicationExists(TimeSheetRow nsrTimeSheetRow)
			throws DAOException {
		checkNull(nsrTimeSheetRow);
		String appNum = nsrTimeSheetRow.getNsrId();
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.applicationExists", false);
		connHandler.setString(1, appNum);
		
		return appNum.equals(connHandler.retrieveJavaObject(String.class));
	}
	
	/**
	 * @see ApplicationDAO#retrieveUniquePTIOPurposeCds(Integer appId)
	 */
	public final String[] retrieveUniquePTIOPurposeCds(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveUniquePTIOPurposeCds", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}
	
	/**
	 * @see ApplicationDAO#retrieveModificationDescList(Integer appId)
	 */
	public final String[] retrieveModificationDescList(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveModificationDescList", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}
	
	public final String retrieveTVApplicationEUEmissionsForPollutant(
			Integer appId, String pollutantCd, String euEmissionTableCd)
			throws DAOException {
		checkNull(appId);
		checkNull(pollutantCd);
		checkNull(euEmissionTableCd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveTVApplicationEUEmissionsForPollutant",
				true);

		connHandler.setInteger(1, appId);
		connHandler.setString(2, pollutantCd);
		connHandler.setString(3, euEmissionTableCd);

		return (String) connHandler.retrieveJavaObject(String.class);
	}
	
	public final String[] retrieveApplicationHAPPollutantList(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationHAPPollutantList", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}
	
	public final String[] retrieveApplicationGHGPollutantList(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationGHGPollutantList", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}
	
	public final String[] retrieveApplicationOTHPollutantList(Integer appId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationOTHPollutantList", true);

		connHandler.setInteger(1, appId);

		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret.toArray(new String[0]);
	}

	@Override
	public ApplicationDocument retrieveApplicationDocumentByTradeSecrectDocId(Integer tradeSecretDocId)
			throws DAOException {

		checkNull(tradeSecretDocId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationSQL.retrieveApplicationDocumentByTradeSecrectDocId", true);

		connHandler.setInteger(1, tradeSecretDocId);

		return (ApplicationDocument) connHandler.retrieve(ApplicationDocument.class);
	}

	@Override
	public Timestamp retrieveApplicationSubmittedDate(Integer applicationId) throws DAOException {
		checkNull(applicationId);

		ConnectionHandler connHandler = new ConnectionHandler("ApplicationSQL.retrieveApplicationSubmittedDate", true);
		connHandler.setInteger(1, applicationId);

		return (Timestamp) connHandler.retrieveJavaObject(Timestamp.class);
	}

	@Override
	public Application retrieveBasicApplicationById(Integer applicationId) throws DAOException {
		checkNull(applicationId);
		
		ConnectionHandler connHandler = new ConnectionHandler("ApplicationSQL.retrieveBasicApplicationById", true);
		
		connHandler.setInteger(1, applicationId);
		
		return (Application)connHandler.retrieve(Application.class);
	}

	@Override
	public Application retrieveBasicApplicationByNbr(String applicationNbr) throws DAOException {
		checkNull(applicationNbr);
		
		ConnectionHandler connHandler = new ConnectionHandler("ApplicationSQL.retrieveBasicApplicationByNbr", true);
		
		connHandler.setString(1, applicationNbr);
		
		return (Application)connHandler.retrieve(Application.class);
	}

}
