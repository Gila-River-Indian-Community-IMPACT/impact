package us.oh.state.epa.stars2.database.dao.ceta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.StackTestDAO;
import us.oh.state.epa.stars2.database.dao.AbstractDAO.ConnectionHandler;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestMethodPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestVisitDate;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedEmissionsUnit;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportAttachment;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.CetaStackTestResultsDef;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class StackTestSQLDAO extends AbstractDAO implements StackTestDAO {

	private Logger logger = Logger.getLogger(StackTestSQLDAO.class);
	
	public static final String TEST_DATE = "filter_by_test_date";
	public static final String SCHEDULED_DATE = "cst.date_scheduled";
	public static final String RESULTS_RECEIVED_DATE = "cst.date_received";
	public static final String RESULTS_REVIEWD_DATE = "cst.date_evaluated";

	public StackTest createStackTest(StackTest stackTest) throws DAOException {
		checkNull(stackTest);
		StackTest ret = stackTest;
		if (ret.getId() == null) {
			ret.setId(nextSequenceVal("S_Stack_Test_Id", ret.getId()));
		}
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createStackTest", false);

		int i = 1;
		connHandler.setInteger(i++, ret.getId());
		connHandler.setInteger(i++, ret.getFceId());
		connHandler.setString(i++, ret.getFpId());
		connHandler.setTimestamp(i++, ret.getDateScheduled());
		connHandler.setTimestamp(i++, ret.getDateReceived());
		connHandler.setInteger(i++, ret.getReviewer());
		connHandler.setTimestamp(i++, ret.getDateEvaluated());
		connHandler.setTimestamp(i++, ret.getReminderDate());
		connHandler.setString(i++, ret.getStackTestMethodCd());
		// connHandler.setString(i++,
		// AbstractDAO.translateBooleanToIndicator(ret.getConformance()));
		connHandler.setString(i++, ret.getConformedToTestMethod());
		connHandler.setString(i++, ret.getControlEquipUsed());
		connHandler.setString(i++, ret.getMonitoringEquip());
		connHandler.setString(i++, ret.getMemo());
		connHandler.setString(i++, ret.getCompany());
		connHandler.setString(i++, ret.getAuditsCd());
		connHandler.setString(i++, ret.getEmissionTestState());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isLegacyFlag()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForPti()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForPtio()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForPto()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForTv()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForFeptio()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForNsps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForMact()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForBif()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForTiv()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.isForOther()));
		connHandler.setInteger(i++, ret.getCreatedById());
		connHandler.setTimestamp(i++, ret.getCreatedDt());
		connHandler.setTimestamp(i++, ret.getSubmittedDate());
		connHandler.setString(i++, ret.getCategoryCd());
		connHandler.setString(i++, ret.getTestingMethodCd());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(ret.getValidated()));
		connHandler.update();

		// If we get here the INSERT must have succeeded,
		// so set last modified to 1 (which is the default value in the
		// database)
		ret.setLastModified(1);

		return ret;
	}

	public void createTestVisitDate(Timestamp dt, Integer stackTestId)
			throws DAOException {
		checkNull(dt);
		checkNull(stackTestId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createTestVisitDate", false);
		int i = 1;
		connHandler.setInteger(i++, stackTestId);
		connHandler.setTimestamp(i++, dt);
		connHandler.update();
	}

	public void createStackTestWitness(Integer user, Integer stackTestId)
			throws DAOException {
		checkNull(user);
		checkNull(stackTestId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createStackTestWitness", false);
		int i = 1;
		connHandler.setInteger(i++, stackTestId);
		connHandler.setInteger(i++, user);
		connHandler.update();
	}

	public void removeTestVisitDates(Integer stackTestId) throws DAOException {
		checkNull(stackTestId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.removeTestVisitDates", false);
		int i = 1;
		connHandler.setInteger(i++, stackTestId);
		connHandler.remove();
	}

	public void removeStackTestWitnesses(Integer stackTestId)
			throws DAOException {
		checkNull(stackTestId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.removeStackTestWitnesses", false);
		int i = 1;
		connHandler.setInteger(i++, stackTestId);
		connHandler.remove();
	}

	public void createTestPollutant(StackTestedPollutant stp)
			throws DAOException {
		checkNull(stp);
		checkNull(stp.getStackTestId());
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createTestPollutant", false);
		int i = 1;
		connHandler.setInteger(i++, stp.getStackTestId());
		connHandler.setString(i++, stp.getPollutantCd());
		connHandler.setTimestamp(i++, stp.getAfsSentDate());
		connHandler.setString(i++, stp.getAfsId());
		connHandler.setString(i++, stp.getStackTestResultsCd());
		connHandler.setString(i++, stp.getPermitAllowRate());
		connHandler.setString(i++, stp.getPermitMaxRate());
		connHandler.setString(i++, stp.getTestRate());
		connHandler.setString(i++, stp.getTestAvgOperRate());
		connHandler.setInteger(i++, stp.getTestedEu());
		connHandler.setString(i++, stp.getSccId());
		connHandler.update();
	}

	public void removeTestPollutants(Integer stackTestId) throws DAOException {
		checkNull(stackTestId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.removeTestPollutants", false);
		int i = 1;
		connHandler.setInteger(i++, stackTestId);
		connHandler.remove();
	}

	public boolean modifyStackTest(StackTest stackTest) throws DAOException {
		checkNull(stackTest);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.modifyStackTest", false);

		int i = 1;
		connHandler.setInteger(i++, stackTest.getFceId());
		connHandler.setInteger(i++, stackTest.getFpId());
		connHandler.setTimestamp(i++, stackTest.getDateScheduled());
		connHandler.setTimestamp(i++, stackTest.getDateReceived());
		connHandler.setInteger(i++, stackTest.getReviewer());
		connHandler.setTimestamp(i++, stackTest.getDateEvaluated());
		connHandler.setTimestamp(i++, stackTest.getReminderDate());
		connHandler.setString(i++, stackTest.getStackTestMethodCd());
		// connHandler.setString(i++, stackTest.getStackTestResultsCd());
		// connHandler.setString(i++, AbstractDAO
		// .translateBooleanToIndicator(stackTest.getConformance()));
		connHandler.setString(i++, stackTest.getConformedToTestMethod());
		connHandler.setString(i++, stackTest.getControlEquipUsed());
		connHandler.setString(i++, stackTest.getMonitoringEquip());
		connHandler.setString(i++, stackTest.getMemo());
		connHandler.setString(i++, stackTest.getCompany());
		connHandler.setString(i++, stackTest.getAuditsCd());
		connHandler.setString(i++, stackTest.getEmissionTestState());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(stackTest.isLegacyFlag()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(stackTest.isForPti()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(stackTest.isForPtio()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(stackTest.isForPto()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(stackTest.isForTv()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(stackTest.isForFeptio()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(stackTest.isForNsps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(stackTest.isForMact()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(stackTest.isForBif()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(stackTest.isForTiv()));
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(stackTest.isForOther()));
		connHandler.setTimestamp(i++, stackTest.getSubmittedDate());
		// connHandler.setTimestamp(i++, stackTest.getAfsSentDate());
		// connHandler.setInteger(i++, stackTest.getAfsId());
		connHandler.setInteger(i++, stackTest.getLastModified() + 1);
		connHandler.setString(i++, stackTest.getCategoryCd());
		connHandler.setString(i++, stackTest.getTestingMethodCd());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(stackTest.getValidated()));
		connHandler.setInteger(i++, stackTest.getId());
		connHandler.setInteger(i++, stackTest.getLastModified());

		return connHandler.update();
	}

	public void removeStackTest(int stackTestId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.removeStackTest", false);
		connHandler.setInteger(1, stackTestId);
		connHandler.remove();
	}

	public void removeStackTestNotes(int stackTestId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.removeStackTestNotes", false);
		connHandler.setInteger(1, stackTestId);
		connHandler.remove();
	}

	public StackTest retrieveStackTest(int stackTestId) throws DAOException {
		PreparedStatement pStmt = null;
		Connection conn = null;
		StackTest stackTest = null;
		;
		try {
			conn = getReadOnlyConnection();
			StringBuffer statementSQL;
			statementSQL = new StringBuffer(
					loadSQL("CetaSQL.retrieveStackTest"));
			pStmt = conn.prepareStatement(statementSQL.toString());
			pStmt.setInt(1, stackTestId);
			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) { // prime the loop
				stackTest = new StackTest();
				stackTest.populate(rs);
				do {
					TestVisitDate vd = new TestVisitDate();
					vd.populate(rs);
					if (vd.getTestDate() != null) {
						if (stackTest.getFirstVisitDate() == null) {
							stackTest.setFirstVisitDate(vd.getTestDate());
						}
						stackTest.getVisitDates().add(vd);
					}
				} while (rs.next());
			}
			;
		} catch (SQLException e) {
			handleException(e, conn);
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return stackTest;
	}

	public Evaluator[] retrieveStackTestWitnesses(int stackTestId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveStackTestWitnesses", true);
		connHandler.setInteger(1, stackTestId);
		ArrayList<Evaluator> ret = connHandler.retrieveArray(Evaluator.class);
		return ret.toArray(new Evaluator[0]);
	}

	public List<StackTestedPollutant> retrieveTestPollutants(int stackTestId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveTestPollutants", true);
		connHandler.setInteger(1, stackTestId);
		ArrayList<StackTestedPollutant> ret = connHandler
				.retrieveArray(StackTestedPollutant.class);
		for (StackTestedPollutant stp : ret) {
			// set selected flag for all
			stp.setSuperSelected(true);
			stp.setPollutantDsc(PollutantDef.getData().getItems()
					.getDescFromAllItem(stp.getPollutantCd()));
		}
		return ret;
	}

	public StackTestMethodPollutant[] retrieveMethodPollutants(
			String stackTestMethodCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveMethodPollutants", true);
		connHandler.setString(1, stackTestMethodCd);
		ArrayList<StackTestMethodPollutant> ret = connHandler
				.retrieveArray(StackTestMethodPollutant.class);
		return ret.toArray(new StackTestMethodPollutant[0]);
	}

	public DbInteger cntStackTestsWithDate(Integer stackTestId,
			Timestamp visitDate, String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.cntStackTestsWithDate", false);
		int i = 1;
		connHandler.setTimestamp(i++, visitDate);
		connHandler.setInteger(i++, stackTestId);
		connHandler.setString(i++, facilityId);
		return (DbInteger) connHandler.retrieve(DbInteger.class);
	}

	public StackTest[] searchStackTests(String facilityId) throws DAOException {
		checkNull(facilityId);

		return retrieveStackTestsBySearch(facilityId, null, null, null, null, null,
				null, null, null, null, null, false, null, null, null, null, null,
				null);
	}

	public StackTest[] searchStackTests(String facilityId, String emissionTestState) throws DAOException {
		checkNull(facilityId);

		return retrieveStackTestsBySearch(facilityId, null, null, null, null, null,
				null, null, null, null, null, false, null, null, emissionTestState, null, null,
				null);
	}
	
	public StackTest[] retrieveStacktestByFce(Integer fceId)
			throws DAOException {
		checkNull(fceId);

		return retrieveStackTestsBySearch(null, null, fceId, false, null, null, null,
				null, null, null, null, null, false, null, null, null, null, null,
				null);
	}

	public StackTest[] retrieveStacktestsUnassigned(String facilityId)
			throws DAOException {
		checkNull(facilityId);

		return retrieveStackTestsBySearch(facilityId, null, null, true, null, null,
				null, null, null, null, null, null, false, null, null, null, null,
				null, null);
	}

	public StackTest[] retrieveStackTestsBySearch(String facilityId,
			Integer stackTestId, Integer fceId, String facilityName,
			String doLaaCd, String countyCd,
			String permitClassCd, String facilityTypeCd, String dateBy, Timestamp beginDate,
			Timestamp endDate, boolean failedPolls, Integer reviewer,
			String stackTestMethodCd, String emissionTestState, String inspId,
			String stckId, String cmpId) throws DAOException {

		return retrieveStackTestsBySearch(facilityId, stackTestId, fceId,
				false, facilityName, doLaaCd, countyCd, permitClassCd, facilityTypeCd,
				dateBy, beginDate, endDate, failedPolls, reviewer, stackTestMethodCd,
				emissionTestState, inspId, stckId, cmpId);
	}

	private StackTest[] retrieveStackTestsBySearch(String facilityId,
			Integer stackTestId, Integer fceId, boolean fceUnassigned,
			String facilityName, String doLaaCd, String countyCd,
			String permitClassCd, String facilityTypeCd, String dateBy,
			Timestamp beginDate, Timestamp endDate, boolean failedPolls,
			Integer reviewer, String stackTestMethodCd,
			String emissionTestState, String inspId, String stckId, String cmpId)
			throws DAOException {
		PreparedStatement pStmt = null;
		Connection conn = null;
		StackTest stackTest = null;
		ArrayList<StackTest> stackTestList = new ArrayList<StackTest>();
		try {
			conn = getReadOnlyConnection();

			StringBuffer statementSQL;
			StringBuffer whereClause = new StringBuffer();
			statementSQL = new StringBuffer(
					loadSQL("CetaSQL.retrieveStackTestsBySearch"));

			if (facilityId != null && facilityId.trim().length() > 0) {
				facilityId = formatFacilityId(facilityId);
				whereClause.append(" AND LOWER(ff2.facility_id) LIKE ");
				whereClause.append("LOWER('");
				whereClause.append(SQLizeString(facilityId.replace("*", "%")));
				whereClause.append("')");
			}

			if (countyCd != null) {
				whereClause.append(" AND ca.county_cd = '");
				whereClause.append(countyCd);
				whereClause.append("'");
			}

			if (facilityName != null && facilityName.trim().length() > 0) {
				whereClause.append(" AND LOWER(ff2.facility_nm) LIKE ");
				whereClause.append("LOWER('");
				whereClause
						.append(SQLizeString(facilityName.replace("*", "%")));
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

			if (emissionTestState != null) {
				whereClause.append(" AND cst.emission_test_state = '");
				whereClause.append(emissionTestState);
				whereClause.append("'");
			}

			if (fceId != null) {
				whereClause.append(" AND cst.fce_id = ");
				whereClause.append(fceId.intValue());
			} else if (fceUnassigned) {
				whereClause.append(" AND cst.fce_id is null");
			}

			if (inspId != null && inspId.length() != 0) {
				inspId = formatId("INSP", inspId);
				whereClause.append(" AND LOWER(cf.insp_id) LIKE ");
				whereClause.append("LOWER('");
				whereClause.append(SQLizeString(inspId.replace("*", "%")));
				whereClause.append("')");
			}

			if (stckId != null && stckId.length() != 0) {
				stckId = formatId("STCK", stckId);
				whereClause.append(" AND LOWER(cst.stck_id) LIKE ");
				whereClause.append("LOWER('");
				whereClause.append(SQLizeString(stckId.replace("*", "%")));
				whereClause.append("')");
			}

			if (stackTestId != null) {
				whereClause.append(" AND cst.stack_test_id = ");
				whereClause.append(stackTestId.toString());
			}

			if (stackTestMethodCd != null) {
				whereClause.append(" AND cst.stack_test_method_cd = '");
				whereClause.append(stackTestMethodCd);
				whereClause.append("'");
			}

			if (reviewer != null) {
				whereClause.append(" AND cst.reviewer_id = ");
				whereClause.append(reviewer.toString());
			}

			if (dateBy != null && dateBy.trim().length() != 0
					&& (beginDate != null || endDate != null)
					&& !dateBy.trim().equals(StackTestSQLDAO.TEST_DATE)) {

				whereClause.append(" AND ");
				whereClause.append("(");
				if (beginDate != null) {
					whereClause.append(dateBy);
					whereClause.append(" >= ? ");
					if (endDate != null) {
						whereClause.append(" AND ");
					}
				}
				if (endDate != null) {
					whereClause.append(dateBy);
					whereClause.append(" <= ? ");
				}
				whereClause.append(" ) ");
			}
			
			// Additional whereClause(s) for IMPACT Public website.
	    	if(CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
	    		// retrieve only if the report is submitted.
	    		whereClause.append(" AND cst.emission_test_state = '");
				whereClause.append(EmissionsTestStateDef.SUBMITTED);
				whereClause.append("'");
				
				// retrieve only if the report review date is before today
				whereClause.append(" AND (cst.date_evaluated is not null)");
				whereClause.append(" AND CONVERT(date, cst.date_evaluated) < CONVERT(date, GETDATE())");
	        }

			String sortBy = " ORDER by ff2.facility_id, cst.stack_test_id DESC, cst.reviewer_id";

			statementSQL.append(whereClause.toString() + " " + sortBy);

			//logger.debug("SQL = " + replaceSchema(statementSQL.toString()));
			//logger.debug("beginDate = " + beginDate.toString());
			//logger.debug("endDate = " + endDate.toString());

			pStmt = conn
					.prepareStatement(replaceSchema(statementSQL.toString()));

			int i = 1;
			if (dateBy != null && dateBy.trim().length() != 0
					&& beginDate != null
					&& !dateBy.trim().equals(StackTestSQLDAO.TEST_DATE)) {

				pStmt.setTimestamp(i, formatBeginOfDay(beginDate));
				i++;
			}
			if (dateBy != null && dateBy.trim().length() != 0
					&& endDate != null
					&& !dateBy.trim().equals(StackTestSQLDAO.TEST_DATE)) {

				pStmt.setTimestamp(i, formatEndOfDay(endDate));
				i++;
			}
			
			logger.debug ("SQL = " + replaceSchema(statementSQL.toString()));
			

			// logger.debug(statementSQL.toString());

			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) { // prime the loop
				do {
					boolean locked = false;
					boolean unlocked = false;
					String afsId = null;

					stackTest = new StackTest();
					stackTest.populate(rs);
					boolean inDateRange = false;
					Integer currentStackTestId = stackTest.getId();
					stackTest.setFailedPollutants(new StringBuffer());
					HashSet<String> fPolls = new HashSet<String>();
					HashSet<String> testedEus = new HashSet<String>();
					ArrayList<TestedEmissionsUnit> teus = new ArrayList<TestedEmissionsUnit>();
					do {
						afsId = rs.getString("afs_id");
						String pollutantCd = rs.getString("pollutant_cd");
						String testedEuNm = rs.getString("epa_emu_id");
						Integer euCorrId = AbstractDAO.getInteger(rs,
								"corr_epa_emu_id");
						if (testedEuNm != null) {
							testedEus.add(testedEuNm);
							TestedEmissionsUnit t = new TestedEmissionsUnit();
							t.setEpaEmuId(testedEuNm);
							t.setTestedEu(euCorrId);
							teus.add(t);
						}
						String testResult = rs
								.getString("stack_test_result_cd");
						if (CetaStackTestResultsDef.FAIL.equals(testResult)) {
							BaseDef bd = PollutantDef.getData().getItem(
									pollutantCd);
							String poll = "unknown";
							if (bd != null) {
								poll = bd.getDescription();
								fPolls.add(poll);
							}
						}
						if (afsId == null) {
							unlocked = true;
						} else {
							locked = true;
							stackTest.setAfsLockedTrue();
						}
						if (locked && unlocked) {
							stackTest.setAfsPartialLocked(" partial");
						}

						boolean itemInDateRange = true;

						TestVisitDate vd = new TestVisitDate();
						vd.populate(rs);
						if (vd.getTestDate() != null) {
							// See if we already have this date
							boolean different = true;
							for (TestVisitDate vdTemp : stackTest
									.getVisitDates()) {
								if (vdTemp.getTestDate().equals(
										vd.getTestDate())) {
									different = false;
									break;
								}
							}
							if (different) {
								if (stackTest.getFirstVisitDate() == null) {
									stackTest.setFirstVisitDate(vd
											.getTestDate());
								}
								stackTest.getVisitDates().add(vd);
								if (dateBy != null
										&& dateBy.trim().length() != 0
										&& dateBy.trim().equals(
												StackTestSQLDAO.TEST_DATE)
										&& beginDate != null
										&& beginDate.after(vd.getTestDate())) {
									itemInDateRange = false;
								}
								if (dateBy != null
										&& dateBy.trim().length() != 0
										&& dateBy.trim().equals(
												StackTestSQLDAO.TEST_DATE)
										&& endDate != null
										&& endDate.before(vd.getTestDate())) {
									itemInDateRange = false;
								}
							} else if (dateBy != null
									&& dateBy.trim().length() != 0
									&& dateBy.trim().equals(
											StackTestSQLDAO.TEST_DATE)) {
								// Already had one opportunity to include
								// the
								// stack test. Don't need a second chance.
								itemInDateRange = false;
							}
						} else if (dateBy != null
								&& dateBy.trim().length() != 0
								&& dateBy.trim().equals(
										StackTestSQLDAO.TEST_DATE)) {
							itemInDateRange = false;
						}

						if (itemInDateRange) {
							inDateRange = true;
						}

					} while (rs.next()
							&& currentStackTestId.equals(AbstractDAO
									.getInteger(rs, "stack_test_id")));

					if (inDateRange || (beginDate == null && endDate == null)) { // Include
						// if
						// no
						// date
						// range
						// or
						// if
						// within
						// date
						// range
						if (!failedPolls || fPolls.size() > 0) {
							stackTestList.add(stackTest);
							// process failed polllutants
							if (fPolls.size() > 0) {
								ArrayList<String> fPollsList = new ArrayList<String>();
								for (String s : fPolls) {
									fPollsList.add(s);
								}
								Collections.sort(fPollsList);
								for (String s : fPollsList) {
									if (stackTest.getFailedPollutants()
											.length() > 0) {
										stackTest.getFailedPollutants().append(
												"; ");
									}
									stackTest.getFailedPollutants().append(s);
								}
							}
							// process the eus
							ArrayList<String> eusList = new ArrayList<String>();
							StringBuffer eus = new StringBuffer();
							for (String ii : testedEus) {
								if (eus.length() > 0)
									eus.append(", ");
								eusList.add(ii);
							}
							if (eusList.size() > 1) {
								Collections.sort(eusList);
							}
							for (String ii : eusList) {
								if (eus.length() > 0)
									eus.append(", ");
								eus.append(ii);
							}
							stackTest.setTestedEus(eus.toString());
							stackTest.setTestedEmissionsUnits(teus);
						}
					}
				} while (!rs.isAfterLast());
			}
			;
		} catch (SQLException e) {
			handleException(e, conn);
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return stackTestList.toArray(new StackTest[0]);
	}

	public void modifyStTradeSecretAttachment(StAttachment sa)
			throws DAOException {
		checkNull(sa);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.modifyStTradeSecretAttachment", false);
		int i = 1;
		connHandler.setString(i++, sa.getTradeSecretJustification());
		connHandler.setInteger(i++, sa.getRefLastModified() + 1);
		connHandler.setInteger(i++, sa.getTradeSecretDocId());
		connHandler.setInteger(i++, sa.getRefLastModified());
		connHandler.update();
	}

	public final boolean modifyStAttachment(StAttachment doc)
			throws DAOException {
		checkNull(doc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.modifyStAttachment", false);

		int i = 1;
		connHandler.setString(i++, doc.getDocTypeCd());
		connHandler.setInteger(i++, doc.getRefLastModified() + 1);
		connHandler.setInteger(i++, doc.getDocumentID());
		connHandler.setInteger(i++, doc.getStackTestId());
		connHandler.setInteger(i++, doc.getRefLastModified());
		return connHandler.update();
	}

	public void createStTradeSecretAttachment(StAttachment sa)
			throws DAOException {
		checkNull(sa);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createStTradeSecretAttachment", false);
		int i = 1;
		connHandler.setInteger(i++, sa.getTradeSecretDocId());
		connHandler.setInteger(i++, sa.getStackTestId());
		connHandler.setInteger(i++, sa.getDocumentID());
		connHandler.setString(i++, sa.getTradeSecretJustification());
		connHandler.update();
		sa.setLastModified(1);
	}

	public StAttachment createStAttachment(StAttachment sa) throws DAOException {
		checkNull(sa);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createStAttachment", false);

		int i = 1;
		connHandler.setInteger(i++, sa.getDocumentID());
		connHandler.setInteger(i++, sa.getStackTestId());
		connHandler.setString(i++, sa.getDocTypeCd());
		connHandler.update();
		sa.setLastModified(1);
		return sa;
	}

	public void deleteStTradeSecretAttachment(StAttachment sa)
			throws DAOException {
		checkNull(sa);
		checkNull(sa.getTradeSecretDocId());
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.deleteStTradeSecretAttachment", false);
		connHandler.setInteger(1, sa.getTradeSecretDocId());
		connHandler.remove();
	}

	public void deleteStAttachment(StAttachment doc) throws DAOException {
		checkNull(doc);
		checkNull(doc.getStackTestId());
		checkNull(doc.getDocumentID());
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.deleteStAttachment", false);
		connHandler.setInteger(1, doc.getStackTestId());
		connHandler.setInteger(2, doc.getDocumentID());
		connHandler.remove();
	}

	public void retrieveStTradeSecretAttachmentInfo(StAttachment sa)
			throws DAOException {
		checkNull(sa);
		ResultSet resultSet = null;
		Connection conn = null;
		PreparedStatement psSelect = null;

		try {
			conn = getConnection();
			psSelect = conn
					.prepareStatement(loadSQL("CetaSQL.retrieveStTradeSecretAttachmentInfo"));
			psSelect.setInt(1, sa.getStackTestId());
			psSelect.setInt(2, sa.getDocumentID());
			resultSet = psSelect.executeQuery();

			while (resultSet.next()) {
				sa.setTradeSecretDocId(resultSet.getInt("document_id"));
				sa.setTradeSecretJustification(resultSet
						.getString("trade_secret_reason"));
			}
			resultSet.close();
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(psSelect);
			handleClosing(conn);
		}
	}

	public List<StAttachment> retrieveStAttachments(int stackTestId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveStAttachments", true);
		connHandler.setInteger(1, stackTestId);
		return connHandler.retrieveArray(StAttachment.class);
	}

	// Duplicates are OK because there may be multiple operating scenarios
	// public Integer[] duplicateStackTests(StackTest st) throws DAOException {
	// checkNull(st);
	// // DENNIS need to finish this
	// //if(st.getVisitDates() == null || st.getVisitDates().size() == 0 ||
	// //st.getTestedEu() == null || st.getStackTestMethodCd() == null) {
	// //// No reason to compare because not complete
	// //return new Integer[0];
	// //}
	// StringBuffer statementSQL;
	// statementSQL = null;
	// if(st.getId() == null) {
	// statementSQL = new
	// StringBuffer(loadSQL("CetaSQL.duplicateStackTestPart1Null"));
	// } else {
	// statementSQL = new
	// StringBuffer(loadSQL("CetaSQL.duplicateStackTestPart1"));
	// }
	// statementSQL.append("( ");
	// for(int i=0; i<st.getVisitDates().size(); i++) {
	// statementSQL.append(loadSQL("CetaSQL.duplicateStackTestPart2"));
	// if(i < st.getVisitDates().size() - 1) {
	// statementSQL.append(" OR ");
	// }
	// }
	// statementSQL.append(") ");
	// ConnectionHandler connHandler = new ConnectionHandler(true);
	// connHandler.setSQLStringRaw(statementSQL.toString());
	// int i = 1;
	// connHandler.setString(i++, st.getFacilityId());
	// if(st.getId() != null) {
	// connHandler.setInteger(i++, st.getId());
	// }
	// // DENNIS need to finish this
	// connHandler.setInteger(i++, null); //st.getTestedEu());
	// connHandler.setString(i++, st.getStackTestMethodCd());

	// for(TestVisitDate tvd : st.getVisitDates()) {
	// connHandler.setTimestamp(i++, tvd.getTestDate());
	// }
	// ArrayList<? extends Object> ret =
	// connHandler.retrieveJavaObjectArray(Integer.class);
	// return ret.toArray(new Integer[0]);
	// }

	public List<StackTestedPollutant> newAfsStackTests() throws DAOException {
		PreparedStatement pStmt = null;
		Connection conn = null;
		ArrayList<StackTestedPollutant> testList = new ArrayList<StackTestedPollutant>();
		try {
			conn = getReadOnlyConnection();

			StringBuffer statementSQL;

			statementSQL = new StringBuffer(loadSQL("CetaSQL.newAfsStackTests"));
			pStmt = conn.prepareStatement(statementSQL.toString());

			ResultSet rs = pStmt.executeQuery();
			if (rs.next()) { // prime the loop
				do {
					StackTestedPollutant test = new StackTestedPollutant();
					StackTestedPollutant test2;
					test.populate(rs);
					Integer currentStackTestId = test.getStackTestId();
					String currentPollutant = test.getPollutantCd();
					Integer currentEu = test.getTestedEu();
					String currentScc = test.getSccId();
					while (rs.next()) {
						test2 = new StackTestedPollutant();
						test2.populate(rs);
						if (currentStackTestId.equals(AbstractDAO.getInteger(
								rs, "stack_test_id"))
								&& currentPollutant.equals(rs
										.getString("pollutant_cd"))
								&& currentEu.equals(AbstractDAO.getInteger(rs,
										"tested_eu"))
								&& currentScc.equals(rs.getString("scc_id"))) {
							test = test2;
						} else
							break;
					}
					testList.add(test);
				} while (!rs.isAfterLast());
			}
			;
		} catch (SQLException e) {
			handleException(e, conn);
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		return testList;
	}

	public boolean updateStackTestLastModifiedOnly(Integer stackTestId,
			Integer lastModified) throws DAOException {
		checkNull(stackTestId);
		checkNull(lastModified);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.updateStackTestLastModifiedOnly", false);

		int i = 1;
		connHandler.setInteger(i++, lastModified + 1);
		connHandler.setInteger(i++, lastModified);
		return connHandler.update();
	}

	public boolean afsLockStackTestPollutant(StackTestedPollutant stp,
			Integer afsId) throws DAOException {
		checkNull(stp);
		checkNull(afsId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.afsLockStackTestPollutant", false);

		int i = 1;
		connHandler.setString(i++, convertAfsIdToString(afsId));
		connHandler.setInteger(i++, stp.getStackTestId());
		connHandler.setInteger(i++, stp.getTestedEu());
		connHandler.setString(i++, stp.getPollutantCd());
		return connHandler.update();
	}

	public boolean afsSetDateStackTestPollutant(StackTestedPollutant stp)
			throws DAOException {
		checkNull(stp);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.afsSetDateStackTestPollutant", false);

		int i = 1;
		connHandler.setTimestamp(i++, stp.getAfsSentDate());
		connHandler.setInteger(i++, stp.getStackTestId());
		connHandler.setString(i++, stp.getAfsId());
		return connHandler.update();
	}

	public Integer getAfsId(String scscId) throws DAOException {
		checkNull(scscId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.getAfsId", true);

		int i = 1;
		connHandler.setString(i++, scscId);
		Integer rtn = (Integer) connHandler.retrieveJavaObject(Integer.class);
		if (rtn == null) {
			String s = "The table, CE_NEXT_AFS_ID, must not have an entry for facility with SCSC ID "
					+ scscId;
			DAOException e = new DAOException(s, s);
			throw e;
		}
		return rtn;
	}

	public void createNewAfsId(String scscId) throws DAOException {
		checkNull(scscId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createNewAfsId", false);
		int i = 1;
		connHandler.setString(i++, scscId);
		connHandler.update();
	}

	public boolean updateAfsId(String scscId, Integer current)
			throws DAOException {
		checkNull(scscId);
		checkNull(current);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.updateAfsId", false);

		int i = 1;
		connHandler.setInteger(i++, current + 1);
		connHandler.setString(i++, scscId);
		connHandler.setInteger(i++, current);
		return connHandler.update();
	}

	public boolean updateDoubleAfsId(String scscId, Integer current)
			throws DAOException {
		checkNull(scscId);
		checkNull(current);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.updateAfsId", false);

		int i = 1;
		connHandler.setInteger(i++, current + 1);
		connHandler.setString(i++, scscId);
		connHandler.setInteger(i++, current - 1);
		return connHandler.update();
	}

	public boolean updateTripleAfsId(String scscId, Integer current)
			throws DAOException {
		checkNull(scscId);
		checkNull(current);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.updateAfsId", false);

		int i = 1;
		connHandler.setInteger(i++, current + 1);
		connHandler.setString(i++, scscId);
		connHandler.setInteger(i++, current - 2);
		return connHandler.update();
	}

	public boolean updateMultipleAfsId(String scscId, Integer current,
			int newValue) throws DAOException {
		// current is the value before updating.
		// new value is to be the next unassigned value
		checkNull(scscId);
		checkNull(current);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.updateAfsId", false);

		int i = 1;
		connHandler.setInteger(i++, newValue);
		connHandler.setString(i++, scscId);
		connHandler.setInteger(i++, current);
		return connHandler.update();
	}

	public Timestamp retrieveLastStackTestDate(String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveLastStackTestDate", true);
		connHandler.setString(1, facilityId);
		Timestamp st = (Timestamp) connHandler
				.retrieveJavaObject(Timestamp.class);
		return st;
	}

	public Timestamp retrieveLastStackTestDate(Integer stackTestId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveLastStackTestDateById", true);
		connHandler.setInteger(1, stackTestId);
		Timestamp st = (Timestamp) connHandler
				.retrieveJavaObject(Timestamp.class);
		return st;
	}

	public String stackTestAfsLocked(Integer stackTestId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.stackTestAfsLockedId", true);
		connHandler.setInteger(1, stackTestId);
		ArrayList<? extends Object> afsIds = connHandler
				.retrieveJavaObjectArray(String.class);
		if (afsIds.size() > 0)
			return (String) afsIds.get(0);
		return null;
	}

	public List<StackTestedPollutant> retrieveTestPollutant(String scscId,
			String afsId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrievePollutantTestByAfsId", true);
		connHandler.setString(1, scscId);
		connHandler.setString(2, afsId);
		ArrayList<StackTestedPollutant> ret = connHandler
				.retrieveArray(StackTestedPollutant.class);
		return ret;
	}

	public final Note[] retrieveStackTestNotes(int stackTestID)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.retrieveStackTestNotes", true);
		connHandler.setInteger(1, stackTestID);
		ArrayList<StackTestNote> ret = connHandler
				.retrieveArray(StackTestNote.class);

		return ret.toArray(new Note[0]);
	}

	public final void createStackTestNote(Integer stackTestID, Integer noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createStackTestNote", false);

		checkNull(stackTestID);
		checkNull(noteId);
		connHandler.setInteger(1, stackTestID);
		connHandler.setInteger(2, noteId);

		connHandler.update();
	}
	
	public int getFceId(String inspectionId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.getInspectionId", false);

		connHandler.setString(1, inspectionId);
		

		Integer rtn = (Integer) connHandler.retrieveJavaObject(Integer.class);
		
		return rtn;
	}
	
	public final void createInspectionAssociation(Integer stackTestId, Integer fceId)
			throws DAOException{
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.createInspectionAssociation", false);

		connHandler.setInteger(1, fceId);
		connHandler.setInteger(2, stackTestId);	
		

		connHandler.update();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.StackTestDAO#
	 * setStackTestValidatedFlag(java.lang.Integer, boolean)
	 */
	public final void setStackTestValidatedFlag(Integer stId,
			boolean validated) throws DAOException {
		checkNull(stId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"CetaSQL.setStackTestValidatedFlag", false);
		connHandler.setString(1,
				AbstractDAO.translateBooleanToIndicator(validated));
		connHandler.setInteger(2, stId);
		connHandler.remove();
	}

	@Override
	public StAttachment retrieveStTradeSecretAttachmentInfoById(Integer tradeSecretDocId) throws DAOException {

		checkNull(tradeSecretDocId);

		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveStTradeSecretAttachmentInfoById", true);

		connHandler.setInteger(1, tradeSecretDocId);

		return (StAttachment) connHandler.retrieve(StAttachment.class);
	}
	
	@Override
	public List<StackTestedPollutant> retrieveTestPollutantsAndEus(Integer stackTestId) throws DAOException {
		checkNull(stackTestId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveTestPollutantsAndEus", true);
		connHandler.setInteger(1, stackTestId);
		ArrayList<StackTestedPollutant> ret = connHandler
				.retrieveArray(StackTestedPollutant.class);
		for (StackTestedPollutant stp : ret) {
			// set selected flag for all
			stp.setSuperSelected(true);
			stp.setPollutantDsc(PollutantDef.getData().getItems()
					.getDescFromAllItem(stp.getPollutantCd()));
		}
		return ret;
	}
	
	@Override
	public List<TestVisitDate> retrieveStackTestDatesById(Integer stackTestId) throws DAOException {
		checkNull(stackTestId);
		ConnectionHandler connHandler = new ConnectionHandler("CetaSQL.retrieveStackTestDatesById", true);
		connHandler.setInteger(1, stackTestId);
		ArrayList<TestVisitDate> ret = connHandler.retrieveArray(TestVisitDate.class);
		return ret;
	}
	
	
}
