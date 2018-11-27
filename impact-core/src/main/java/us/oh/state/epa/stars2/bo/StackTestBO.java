package us.oh.state.epa.stars2.bo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.faces.model.SelectItem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.CompEnfFacilityDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.FullComplianceEvalDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.StackTestDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dao.ceta.FullComplianceEvalSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestDocument;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestMethodPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestNote;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestVisitDate;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedEmissionsUnit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedScc;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.util.DbInteger;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.CetaStackTestMethodDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.SiteVisitTypeDef;
import us.oh.state.epa.stars2.def.StAttachmentTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.framework.exception.WorkflowRollbackException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;
import us.oh.state.epa.stars2.webcommon.pdf.ceta.EmissionsTestPdfGenerator;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

import com.lowagie.text.DocumentException;

/** Business object. */
@Transactional(rollbackFor = Exception.class)
@Service
public class StackTestBO extends BaseBO implements StackTestService {

	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * @param stackTestId
	 * @return StackTest
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public StackTest retrieveStackTestRowOnly(int stackTestId)
			throws DAOException {
		StackTest st = null;
		try {
			st = stackTestDAO().retrieveStackTest(stackTestId);
		} catch (DAOException de) {
			logger.error("stackTestId=" + stackTestId + " " + de.getMessage(),
					de);
			throw de;
		}
		return st;
	}

	/**
	 * @param stackTestId
	 * @return StackTest
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public StackTest retrieveStackTest(int stackTestId, boolean readOnly) throws DAOException {
		StackTest st = null;
		
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		try {
			st = stackTestDAO.retrieveStackTest(stackTestId);

			if (st == null) {
				return st;
			}

			Evaluator[] e = stackTestDAO.retrieveStackTestWitnesses(
					stackTestId);
			ArrayList<Evaluator> eList = new ArrayList<Evaluator>();
			for (Evaluator ev : e) {
				eList.add(ev);
			}
			st.setWitnesses(eList);

			FacilityService facilityBO = null;
			;
			facilityBO = ServiceFactory.getInstance().getFacilityService();
			st.setAssociatedFacility(facilityBO.retrieveFacilityProfile(st
					.getFpId(),!readOnly));
			if (st.getAssociatedFacility() == null) {
				throw new DAOException("facilityBO.retrieveFacilityProfile("
						+ st.getFpId() + ") failed to find the profile");
			}
			/*
			 * Removed this for IMPACT. Replaced with button to associate with
			 * current facility inventory. if
			 * (!EmissionsTestStateDef.SUBMITTED.equals(st
			 * .getEmissionTestState()) &&
			 * st.getAssociatedFacility().getVersionId() != -1) { // replace //
			 * with current facility inventory // The updated fpId will be //
			 * stored // if the record is actually // updated.
			 * st.setAssociatedFacility(facilityBO.retrieveFacility(st
			 * .getAssociatedFacility().getFacilityId())); if
			 * (st.getAssociatedFacility() == null) { throw new
			 * DAOException("facilityBO.retrieveFacility(" + st.getFacilityId()
			 * + ") failed to find the profile"); }
			 * st.setFpId(st.getAssociatedFacility().getFpId()); }
			 * 
			 * st.setAssociatedFacility(facilityBO.retrieveFacilityProfile(st
			 * .getFpId())); if (st.getAssociatedFacility() == null) { throw new
			 * DAOException("facilityBO.retrieveFacilityProfile(" + st.getFpId()
			 * + ") failed to find the profile"); }
			 */
			retrieveStackTestPollInfo(st, st.getAssociatedFacility(), readOnly);
			List<StAttachment> attachments = retrieveStAttachments(stackTestId, readOnly);
			st.setAttachments(attachments);
			st.setStackTestNotes(stackTestDAO().retrieveStackTestNotes(
					st.getId()));
			st.setAfsLocked();
			
			List<ComplianceStatusEvent> cse = permitConditionDAO().retrieveComplianceStatusEventListByReferencedStackTest(stackTestId);
			st.setAssocComplianceStatusEvents(cse);
			List<String> inspIds = fullComplianceEvalDAO().retrieveInspectionIdsForStackTestId(stackTestId);
			st.setInspectionsReferencedIn(inspIds);
		} catch (DAOException de) {
			logger.error("stackTestId=" + stackTestId + " " + de.getMessage(),
					de);
			throw de;
		} catch (RemoteException de) {
			String s = "stackTestId=" + stackTestId + " " + de.getMessage();
			logger.error(s, de);
			throw new DAOException(s, de);
		} catch (ServiceFactoryException de) {
			String s = "stackTestId=" + stackTestId + " " + de.getMessage();
			logger.error(s, de);
			throw new DAOException(s, de);
		}
		return st;
	}
	
	@Override
	public StackTest retrieveBasicStackTestWithPollutants(Integer stackTestId, Facility facility) throws DAOException {
		StackTest st = null;

		
		try {
			st = stackTestDAO().retrieveStackTest(stackTestId);
			if (st == null) {
				return st;
			}
			
			Evaluator[] e = stackTestDAO().retrieveStackTestWitnesses(
					st.getId());
			ArrayList<Evaluator> eList = new ArrayList<Evaluator>();
			for (Evaluator ev : e) {
				eList.add(ev);
			}
			st.setWitnesses(eList);
			
			st.setAssociatedFacility(facility);
			
			retrieveStackTestPollInfo(st, st.getAssociatedFacility(), true);
			
			st.setAfsLocked();
		} catch (DAOException de) {
			logger.error("stackTestId=" + stackTestId + " " + de.getMessage(),
					de);
			throw de;
		}
		return st;
	}

	/**
	 * @param stackTest
	 * @param f
	 * @return void
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	private void retrieveStackTestPollInfo(StackTest stackTest, Facility f, boolean readOnly)
			throws DAOException {
		// This function is used to retrieve pollutant and pollutant test
		// information from database
		// It also populates the table of EUs/SCCs in the stackTest.
		
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		if (stackTest == null)
			return;
		try {
			List<StackTestedPollutant> testPolls = stackTestDAO
					.retrieveTestPollutants(stackTest.getId());
			ArrayList<StackTestedPollutant> pollsToDisplay = new ArrayList<StackTestedPollutant>();
			HashSet<String> testedPolls = new HashSet<String>();
			Integer testedEu = null;
			String testedEuSccId = null;
			EmissionUnit eu = null;
			// first populate table with all tested pollutants. The order will
			// be EU first and pollutant second
			String euScc = null;
			for (StackTestedPollutant ssT : testPolls) {
				if (ssT.getTestedEu() != null) {
					// Is it next EU?

					if (testedEu != null && testedEu.equals(ssT.getTestedEu())
							&& testedEuSccId != null
							&& testedEuSccId.equals(ssT.getSccId())) {
						// we've processed this Eu

					} else {
						// this is the first row for this Eu and SccId
						eu = f.getMatchingEmissionUnit(ssT.getTestedEu());
						TestedEmissionsUnit teu = new TestedEmissionsUnit(eu);

						if (ssT.getSccId() != null) {
							if (eu != null) {

								euScc = ssT.getSccId();
								teu.setSccs(euScc);
								EmissionProcess p = eu.findProcess(euScc);
								if (p != null) {
									teu.setControlEquipment(p
											.getControlEquipment());
									teu.setProcessDescription(p
											.getEmissionProcessNm());
								}
								euScc = null; // reset
							}

						}
						stackTest.addTestedEmissionsUnit(teu);
						// We have processed this Eu
						testedEu = ssT.getTestedEu();
						testedEuSccId = ssT.getSccId();
					}
				}

				if (ssT.getPollutantCd() != null) {
					testedPolls.add(ssT.getPollutantCd());

				}

				// keep all the test records
				if (ssT.getTestedEu() != null) {
					// populate with relevent information
					StackTestedPollutant ssM = new StackTestedPollutant(
							stackTest, eu, ssT);
					ssM.setSuperSelected(true);
					ssM.setChangedToFailed(false);
					pollsToDisplay.add(ssM);
				}

			}

			StackTestMethodPollutant[] methodPolls = null;
			ArrayList<StackTestedPollutant> newMethodPolls = new ArrayList<StackTestedPollutant>();
			if (stackTest.getStackTestMethodCd() != null) {
				methodPolls = stackTestDAO().retrieveMethodPollutants(
						stackTest.getStackTestMethodCd());
				// Set in description and whether deprecated
				// will not be any pollutants if no method specified
				for (StackTestedPollutant ptd : pollsToDisplay) {
					for (StackTestMethodPollutant stmp : methodPolls) {
						if (stmp.getPollutantCd().equals(ptd.getPollutantCd())) {
							ptd.setDeprecated(stmp.isDeprecated());
							ptd.setPollutantDsc(stmp.getPollutantDsc());
							break;
						}
					}
				}
				// Determine the pollutants selected and make the all pollutants
				// list.
				int numValidPolls = 0;
				StackTestedPollutant uniqueOne = null;
				for (StackTestMethodPollutant stmp : methodPolls) {
					if (testedPolls.contains(stmp.getPollutantCd())) {
						stmp.setSelected(true);

					}

					StackTestedPollutant stp = new StackTestedPollutant(
							stackTest, stmp.getPollutantCd());
					stp.setSuperSelected(stmp.isSelected());
					stp.setDeprecated(stmp.isDeprecated());
					if (!stp.isDeprecated() || stp.isSelected()) {
						newMethodPolls.add(stp);
					}
					if (!stmp.isDeprecated()) {
						numValidPolls++;
						uniqueOne = stp;
					}
				}
				if (numValidPolls == 1)
					uniqueOne.setSuperSelected(true);
			}

			Collections.sort(pollsToDisplay);
			Collections.sort(stackTest.getTestedEmissionsUnits());
			stackTest.setTestedPollutants(pollsToDisplay
					.toArray(new StackTestedPollutant[0]));
			TableSorter testedPollutantsWrapper = new TableSorter();
			testedPollutantsWrapper.setWrappedData(pollsToDisplay);
			stackTest.setTestedPollutantsWrapper(testedPollutantsWrapper);
			stackTest.setAllMethodPollutants(newMethodPolls
					.toArray(new StackTestedPollutant[0]));

		} catch (DAOException de) {
			logger.error(
					"stackTestId=" + stackTest.getId() + " " + de.getMessage(),
					de);
			throw de;
		}
	}

	/**
	 * @param stackTestId
	 * @return List<StackTestedPollutant>
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<StackTestedPollutant> retrieveStackTestPollOnly(
			Integer stackTestId) throws DAOException {
		// This function is used to retrieve pollutant and pollutant test
		// information from database
		// to be used for AFS Import
		checkNull(stackTestId);
		List<StackTestedPollutant> testPolls = null;
		try {
			testPolls = stackTestDAO().retrieveTestPollutants(stackTestId);
		} catch (DAOException de) {
			logger.error("stackTestId=" + stackTestId + " " + de.getMessage(),
					de);
			throw de;
		}
		return testPolls;
	}

	/**
	 * @param stackTest
	 * @return void
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void initializeStackTestPollInfo(StackTest stackTest)
			throws DAOException {
		// This function is used when the method has changed
		if (stackTest == null)
			return;
		if (stackTest.getStackTestMethodCd() == null) {
			stackTest.setTestedPollutants(new StackTestedPollutant[0]);
			TableSorter testedPollutantsWrapper = new TableSorter();
			testedPollutantsWrapper.setWrappedData(new StackTestedPollutant[0]);
			stackTest.setTestedPollutantsWrapper(testedPollutantsWrapper);
			stackTest.setAllMethodPollutants(new StackTestedPollutant[0]);
		}
		try {
			StackTestMethodPollutant[] methodPolls = stackTestDAO()
					.retrieveMethodPollutants(stackTest.getStackTestMethodCd());
			ArrayList<StackTestedPollutant> pollsToDisplay = new ArrayList<StackTestedPollutant>();
			// count number of active pollutants
			int cnt = 0;
			for (StackTestMethodPollutant stmp : methodPolls) {
				if (!stmp.isDeprecated())
					cnt++;
			}
			stackTest.setTestedPollutants(pollsToDisplay
					.toArray(new StackTestedPollutant[0]));
			TableSorter testedPollutantsWrapper = new TableSorter();
			testedPollutantsWrapper.setWrappedData(pollsToDisplay);
			stackTest.setTestedPollutantsWrapper(testedPollutantsWrapper);

			// Determine all the pollutants--without EU or SCC and whether
			// tested or not.
			ArrayList<StackTestedPollutant> pollList = new ArrayList<StackTestedPollutant>();
			for (StackTestMethodPollutant stmp : methodPolls) {
				StackTestedPollutant stp = new StackTestedPollutant(stackTest,
						stmp.getPollutantCd());
				stp.setSuperSelected(cnt == 1);
				stp.setDeprecated(stmp.isDeprecated());
				if (stmp.isDeprecated()) {
					// Do we keep a deprecated pollutant
					for (StackTestedPollutant stpI : stackTest
							.getTestedPollutants()) {
						if (stpI.getPollutantCd().equals(stmp.getPollutantCd())) {
							stp.setSuperSelected(true);
							pollList.add(stp);
							break;
						}
					}

				} else
					pollList.add(stp);
			}
			stackTest.setAllMethodPollutants(pollList
					.toArray(new StackTestedPollutant[0]));
		} catch (DAOException de) {
			logger.error(
					"stackTestId=" + stackTest.getId() + " " + de.getMessage(),
					de);
			throw de;
		}
	}

	/**
	 * @param facilityId
	 * @param stackTest
	 * @return StackTest
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public StackTest createStackTest(Facility facility, StackTest stackTest,
			boolean readOnly)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		StackTest ret = null;

		try {
			ret = createStackTest(facility, stackTest, trans, readOnly);
			trans.complete();
		} catch (DAOException de) {
			logger.error(
					"facilityId=" + facility.getFacilityId() + "; "
							+ "stackTestId=" + stackTest.getId() + " "
							+ de.getMessage(), de);
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param facilityId
	 * @param stackTest
	 * @param trans
	 * @return StackTest
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private StackTest createStackTest(Facility facility, StackTest stackTest,
			Transaction trans, boolean readOnly) throws DAOException {
		StackTest st = null;
		
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		//StackTestDAO stackTestDAO = stackTestDAO(trans);
		st = stackTestDAO.createStackTest(stackTest);
		// Create the test visit dates
		for (TestVisitDate tvd : stackTest.getVisitDates()) {
			stackTestDAO.createTestVisitDate(tvd.getTestDate(), st.getId());
		}

		// Create the witnesses
		for (Evaluator ev : stackTest.getWitnesses()) {
			if (ev.getEvaluator() == null)
				continue;
			stackTestDAO.createStackTestWitness(ev.getEvaluator(), st.getId());
		}

		if (readOnly) {
			if (stackTest.isWitnessesExist()) {
				// create the date records.
				createDeleteVisit(facility, st.getId(),
						new ArrayList<TestVisitDate>(), st.getVisitDates(),
						trans, readOnly);
			}
		}

		// Create the test pollutants
		if (stackTest.getTestedPollutants().length > 0) {
			for (StackTestedPollutant ss : stackTest.getTestedPollutants()) {
				ss.setStackTestId(stackTest.getId());
				ss.setSccId(TestedScc.removeAllParens(ss.getSccId()));
				stackTestDAO.createTestPollutant(ss);
			}
		} else {
			// No test results so if there are selected pollutants, write them
			// out by themselves.
			for (StackTestedPollutant stp : stackTest.getAllMethodPollutants()) {
				if (stp.isSelected()) {
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				}
			}
			// If no test results then write out all the selected EUs/SCCs
			for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
				if (teu.getSccs().length() == 0) {
					StackTestedPollutant stp = new StackTestedPollutant();
					stp.setEu(teu.getEu());
					stp.setSccId("");
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				} else {
					StackTestedPollutant stp = new StackTestedPollutant();
					stp.setEu(teu.getEu());
					stp.setSccId(TestedScc.removeAllParens(teu.getSccs()));
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				}
			}
		}

		// check to see if any EUs without SCC IDs. If so write them out by
		// themselves.
		// for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
		// if (teu.getSccs().length() == 0) {
		// StackTestedPollutant stp = new StackTestedPollutant();
		// stp.setEu(teu.getEu());
		// stp.setStackTestId(stackTest.getId());
		// stackTestDAO.createTestPollutant(stp);
		// }
		// }

		// create directory for stack test documents
		if (readOnly) {
			String path = null;
			try {
				path = getStackTestDir(facility, st);
				File dir = new File(path);
				File parentDir = dir.getParentFile();
				if (!parentDir.exists()) {
					try {
						DocumentUtil.mkDir(parentDir.getPath());
					} catch (IOException e) {
						logger.error(
								"Exception creating parent directory for file store directory "
										+ path + " (continuing)", e);
					}
				}
				DocumentUtil.mkDir(path);
			} catch (IOException e) {
				logger.error("Exception creating file store directory " + path,
						e);
				throw new DAOException(
						"Exception creating file store directory", e);
			}
		}
		return st;
	}

	/**
	 * @param facilityId
	 * @param stackTest
	 * @param fh
	 * @return StackTest
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public StackTest createMigratedStackTest(Facility facility,
			StackTest stackTest, FacilityHistory fh) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		StackTest ret = null;

		try {
			ret = createMigratedStackTest(facility, stackTest, fh, trans);
			trans.complete();
		} catch (DAOException de) {
			logger.error(
					"facilityId=" + facility.getFacilityId() + "; "
							+ "stackTestId=" + stackTest.getId() + " "
							+ de.getMessage(), de);
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param facilityId
	 * @param stackTest
	 * @param fh
	 * @param trans
	 * @return StackTest
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private StackTest createMigratedStackTest(Facility facility,
			StackTest stackTest, FacilityHistory fh, Transaction trans)
			throws DAOException {
		StackTest st = null;
		StackTestDAO stackTestDAO = stackTestDAO(trans);
		st = stackTestDAO.createStackTest(stackTest);
		// Create the test visit dates
		for (TestVisitDate tvd : stackTest.getVisitDates()) {
			stackTestDAO.createTestVisitDate(tvd.getTestDate(),
					stackTest.getId());
		}

		// Create the witnesses
		for (Evaluator ev : stackTest.getWitnesses()) {
			if (ev.getEvaluator() == null)
				continue;
			stackTestDAO.createStackTestWitness(ev.getEvaluator(),
					stackTest.getId());
		}
		if (stackTest.isWitnessesExist()) {
			// create the date records.
			createMigratedVisit(facility, fh, st.getId(), st.getVisitDates(),
					trans);
		}

		// Create the test pollutants
		if (stackTest.getTestedPollutants().length > 0) {
			for (StackTestedPollutant ss : stackTest.getTestedPollutants()) {
				ss.setStackTestId(stackTest.getId());
				ss.setSccId(TestedScc.removeAllParens(ss.getSccId()));
				stackTestDAO.createTestPollutant(ss);
			}
		} else {
			// No test results so if there are selected pollutants, write them
			// out by themselves.
			for (StackTestedPollutant stp : stackTest.getAllMethodPollutants()) {
				if (stp.isSelected()) {
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				}
			}
			// If no test results then write out all the selected EUs/SCCs
			for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
				if (teu.getSccs().length() == 0) {
					StackTestedPollutant stp = new StackTestedPollutant();
					stp.setEu(teu.getEu());
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				} else {
					StackTestedPollutant stp = new StackTestedPollutant();
					stp.setEu(teu.getEu());
					stp.setSccId(TestedScc.removeAllParens(teu.getSccs()));
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				}
			}
		}

		// check to see if any EUs without SCC IDs. If so write them out by
		// themselves.
		for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
			if (teu.getSccs().length() == 0) {
				StackTestedPollutant stp = new StackTestedPollutant();
				stp.setEu(teu.getEu());
				stp.setStackTestId(stackTest.getId());
				stackTestDAO.createTestPollutant(stp);
			}
		}

		// create directory for stack test documents
		String path = null;
		try {
			path = getStackTestDir(facility, st);
			File dir = new File(path);
			File parentDir = dir.getParentFile();
			if (!parentDir.exists()) {
				DocumentUtil.mkDir(parentDir.getPath());
			}
			DocumentUtil.mkDir(path);
		} catch (IOException e) {
			logger.error("Exception creating file store directory " + path, e);
			throw new DAOException("Exception creating file store directory", e);
		}
		return st;
	}

	private String getStackTestDir(Facility facility, StackTest st) {
		return "/Facilities/" + facility.getFacilityId() + "/EmissionsTest/"
				+ st.getId();
	}

	/**
	 * @param facilityId
	 * @param stackTest
	 * @param oldDates
	 * @throws WorkflowRollbackException 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyStackTest(Facility facility, StackTest stackTest,
			List<TestVisitDate> oldDates, Integer userId,
			boolean readOnly) throws DAOException {
		try {
			modifyStackTestInternal(facility, stackTest, oldDates, userId, readOnly);
		} catch (WorkflowRollbackException e) {
			logger.debug("Removing the workflow process");
			try {
				removeProcessFlows(e.getExtId(), e.getWfId(), userId);
			} catch (Exception e2) {
				logger.error("FAILED attempt to remove workflow for "
						+ e.getWfId(), e2);
			}
			throw new DAOException("modifyStackTest failed");
		}
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	private void modifyStackTestInternal(Facility facility, StackTest stackTest,
			List<TestVisitDate> oldDates, Integer userId,
			boolean readOnly) throws DAOException, WorkflowRollbackException {
		Transaction trans = TransactionFactory.createTransaction();
		Integer workflowId = null;
		try {
			modifyStackTest(facility, stackTest, oldDates, trans, readOnly);

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				InfrastructureDAO infraDAO = infrastructureDAO(trans);
				logger.debug("logging changes for stack test to FAL");
				// we only update this if we aren't in staging
				infraDAO.createFieldAuditLogs(stackTest.getFacilityId(),
						stackTest.getFacilityNm(), userId,
						stackTest.getFieldAuditLogs());
			}

			if (stackTest.isTestBeingSubmitted()) {
				if (!stackTest.isLegacyFlag()) {
					// create workflow for stack test
					workflowId = createWorkflow(stackTest, null, userId, 
							new Timestamp(stackTest.getSubmittedDate().getTime()));

					FacilityDAO facDAO = facilityDAO(trans);
					Facility f = facDAO.retrieveFacility(stackTest.getFpId());
					if (!f.isCopyOnChange()) {
						f.setCopyOnChange(true);
						facDAO.modifyFacility(f);
					}
					
				}
				stackTest.setTestBeingSubmitted(false);
			}
		} catch (Exception e) {
			logger.error(
					"facilityId=" + facility.getFacilityId() + "; "
							+ "stackTestId=" + stackTest.getId() + " "
							+ e.getMessage(), e);
			if (stackTest.isTestBeingSubmitted() && !stackTest.isLegacyFlag() 
					&& null != workflowId) {
				throw new WorkflowRollbackException(
						"Exception while submitting stack test "
								+ stackTest.getId(), e, workflowId, 
								stackTest.getId());
			}
			throw new DAOException(e.getMessage());
		}
	}
	
	/**
	 * @param stackTest
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void assignStackTest(StackTest stackTest, Integer userId)
			throws DAOException {

		try {

			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				// we only update this if we aren't in staging
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				createWorkflow(stackTest, null, userId, ts);
			}
		} catch (DAOException de) {
			if (de.prettyMsgIsNull()) {
				logger.error(
						"stackTestId=" + stackTest.getId() + " "
								+ de.getMessage(), de);
			} else {
				throw de;
			}
		} catch (RemoteException e) {
			logger.error(
					"stackTestId=" + stackTest.getId() + " " + e.getMessage(),
					e);
		} catch (ServiceFactoryException e) {
			logger.error(
					"stackTestId=" + stackTest.getId() + " " + e.getMessage(),
					e);

			throw new DAOException(
					"Could not retrieve workflow service object.");
		} finally {
		}
		return;
	}

	private Integer createWorkflow(StackTest stackTest, Integer fpId,
			Integer userId, Timestamp startDt) throws DAOException, ServiceFactoryException,
			RemoteException {
		// create workflow
		ReadWorkFlowService wfBO = ServiceFactory.getInstance()
				.getReadWorkFlowService();
		Integer workflowId = null;
		Timestamp dueDt = null;

		logger.debug("Creating stack test workflow");
		String ptName = WorkFlowProcess.STACK_TESTS;

		workflowId = wfBO.retrieveWorkflowTempIdAndNm().get(ptName);
		if (workflowId == null) {
			String s = "Failed to find workflow for \"" + ptName
					+ "\" for Stack Test " + stackTest.getStckId();
			logger.error(s);
			throw new DAOException(s);
		}

		String rush = "N";

		if (fpId == null) {
			FacilityDAO fd = facilityDAO();
			Facility fp = fd.retrieveFacility(stackTest.getFacilityId());
			fpId = fp.getFpId();
		}

		logger.debug(" creating Stack Test workflow with workflowId: "
				+ workflowId + ", Stack Test ID " + stackTest.getStckId()
				+ ", facility Id: " + stackTest.getFacilityId() + ", UID: "
				+ userId + ", Due Date: " + dueDt);
		WorkFlowManager wfm = new WorkFlowManager();
		WorkFlowResponse resp = wfm.submitProcess(workflowId,
				stackTest.getId(), fpId, userId, rush, startDt, dueDt, null);

		if (resp.hasError() || resp.hasFailed()) {
			StringBuffer errMsg = new StringBuffer();
			StringBuffer recomMsg = new StringBuffer();
			String[] errorMsgs = resp.getErrorMessages();
			String[] recomMsgs = resp.getRecommendationMessages();
			for (String msg : errorMsgs) {
				errMsg.append(msg + " ");
			}
			for (String msg : recomMsgs) {
				recomMsg.append(msg + " ");
			}
			String s = "Error encountered trying to create workflow for stack test "
					+ stackTest.getStckId() + ": " + errMsg.toString();
			logger.error(s);
			throw new DAOException(s + " " + recomMsg);
		}
		return workflowId;
	}

	/**
	 * @param facilityId
	 * @param stackTest
	 * @param oldDates
	 * @param trans
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private void modifyStackTest(Facility facility, StackTest stackTest,
			List<TestVisitDate> oldDates, Transaction trans,
			boolean readOnly)
			throws DAOException {
		
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));

		//StackTestDAO stackTestDAO = stackTestDAO(trans);
		stackTestDAO.modifyStackTest(stackTest);
		// Replace the test visit dates
		stackTestDAO.removeTestVisitDates(stackTest.getId());
		for (TestVisitDate tvd : stackTest.getVisitDates()) {
			stackTestDAO.createTestVisitDate(tvd.getTestDate(),
					stackTest.getId());
		}
		// Replace the test pollutants
		stackTestDAO.removeTestPollutants(stackTest.getId());

		if (stackTest.getTestedPollutants().length > 0) {
			for (StackTestedPollutant ss : stackTest.getTestedPollutants()) {
				ss.setStackTestId(stackTest.getId());
				ss.setSccId(TestedScc.removeAllParens(ss.getSccId()));
				stackTestDAO.createTestPollutant(ss);
			}
		} else {
			// No test results so if there are selected pollutants, write them
			// out by themselves.
			for (StackTestedPollutant stp : stackTest.getAllMethodPollutants()) {
				if (stp.isSelected()) {
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				}
			}
			// If no test results then write out all the selected EUs/SCCs
			for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
				if (teu.getSccs().length() == 0) {
					StackTestedPollutant stp = new StackTestedPollutant();
					stp.setEu(teu.getEu());
					stp.setSccId("");
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				} else {
					StackTestedPollutant stp = new StackTestedPollutant();
					stp.setEu(teu.getEu());
					stp.setSccId(TestedScc.removeAllParens(teu.getSccs()));
					stp.setStackTestId(stackTest.getId());
					stackTestDAO.createTestPollutant(stp);
				}
			}
		}

		// check to see if any EUs without SCC IDs. If so write them out by
		// themselves.
		// for (TestedEmissionsUnit teu : stackTest.getTestedEmissionsUnits()) {
		// if (teu.getSccs().length() == 0) {
		// StackTestedPollutant stp = new StackTestedPollutant();
		// stp.setEu(teu.getEu());
		// stp.setStackTestId(stackTest.getId());
		// stackTestDAO.createTestPollutant(stp);
		// }
		// }
		// Replace the witnesses
		stackTestDAO.removeStackTestWitnesses(stackTest.getId());
		int numWitnesses = 0;
		for (Evaluator ev : stackTest.getWitnesses()) {
			if (ev.getEvaluator() == null)
				continue;
			numWitnesses++;
			stackTestDAO.createStackTestWitness(ev.getEvaluator(),
					stackTest.getId());
		}
		if (readOnly) {
		List<TestVisitDate> newDates = new ArrayList<TestVisitDate>();
		if (numWitnesses > 0)
			newDates = stackTest.getVisitDates();
		createDeleteVisit(facility, stackTest.getId(), oldDates, newDates,
				trans, readOnly);
		}
	}

	private void createDeleteVisit(Facility facility, Integer stackTestId,
			List<TestVisitDate> oldDates, List<TestVisitDate> newDates,
			Transaction trans, boolean readOnly) throws DAOException {
		/*
		 * Need to compare the original list of dates with the current list. If
		 * any dates added then: See if Site Vist (for stack test) already
		 * exists and if not create one. If any dates deleted, then: See if
		 * there are any existing stack tests that have that date and if not
		 * then delete the visit (for Stack Test)--unless it has been AFS
		 * locked.
		 */
		ArrayList<TestVisitDate> saveOldDates = new ArrayList<TestVisitDate>();
		saveOldDates.addAll(oldDates);
		try {
			//StackTestDAO stackTestDAO = stackTestDAO(trans);
			
			StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
					: CommonConst.STAGING_SCHEMA));
			
			FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
			// provides dates that this test no longer contains.
			oldDates.removeAll(newDates);
			StringBuffer datesLocked = new StringBuffer("");
			for (TestVisitDate vd : oldDates) {
				// if no other test uses this date, delete visit with this date
				DbInteger cnt = stackTestDAO.cntStackTestsWithDate(stackTestId,
						vd.getTestDate(), facility.getFacilityId());
				if (cnt.getCnt().intValue() == 0) {
					DbInteger locked = fceDAO.lockedStackTestSiteVisitCnt(
							vd.getTestDate(), facility.getFacilityId());
					if (locked.getCnt().intValue() > 0) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(vd.getTestDate().getTime());
						datesLocked.append(sdf.format(calendar.getTime()));
						datesLocked.append(" ");
					}
					// Delete the visit. It will not be deleted if the
					// transaction fails.
					fceDAO.removeStackTestSiteVisit(vd.getTestDate(),
							facility.getFacilityId());
				}
			}
			if (datesLocked.length() > 0) {
				String s = FullComplianceEvalSQLDAO.visitsLockedMsg
						+ datesLocked + "cannot be deleted.";
				throw new DAOException(s, s);
			}
			// provides dates that are new to this test.
			newDates.removeAll(saveOldDates);
			for (TestVisitDate vd : newDates) {
				// if this visit date does not exist, add it.
				// If it exists as locked or unlocked, then do nothing.
				SiteVisit sv = fceDAO.retrieveStackTestVisitByDate(
						vd.getTestDate(), facility.getFacilityId());
				if (sv == null) {
					// create the visit.
					SiteVisit newVisit = new SiteVisit(null, null,
							facility.getFpId(), new ArrayList<Evaluator>(),
							vd.getTestDate(), null,
							SiteVisitTypeDef.STACK_TEST, null);
					newVisit.setFacilityId(facility.getFacilityId());
					FullComplianceEvalService fceBO = ServiceFactory
							.getInstance().getFullComplianceEvalService(); // TODO
																			// dennis
																			// should
																			// we
																			// add
																			// dates?
					fceBO.createSiteVisit(newVisit);
				}
			}
		} catch (DAOException de) {
			if (de.prettyMsgIsNull()) {
				logger.error("facilityId=" + facility.getFacilityId() + "; "
						+ "stackTestId=" + stackTestId + " " + de.getMessage(),
						de);
				cancelTransaction(trans, de);
			} else {
				throw de;
			}
		} catch (RemoteException de) {
			logger.error("facilityId=" + facility.getFacilityId() + "; "
					+ "stackTestId=" + stackTestId + " " + de.getMessage(), de);
			cancelTransaction(trans, de);
		} catch (ServiceFactoryException sfe) {
			String s = "facilityId=" + facility.getFacilityId() + "; "
					+ "stackTestId=" + stackTestId + " " + sfe.getMessage();
			logger.error(s, sfe);
			if (trans != null) {
				trans.cancel();
			}
			throw new DAOException(s, sfe);
		} finally {
			closeTransaction(trans);
		}
	}

	private void createMigratedVisit(Facility facility, FacilityHistory fh,
			Integer stackTestId, List<TestVisitDate> newDates, Transaction trans)
			throws DAOException {
		try {
			FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO(trans);
			// will only be one date
			for (TestVisitDate vd : newDates) {
				// if this visit date does not exist, add it.
				// If it exists as locked or unlocked, then do nothing.
				SiteVisit sv = fceDAO.retrieveStackTestVisitByDate(
						vd.getTestDate(), facility.getFacilityId());
				if (sv == null) {
					// Create the history object (just need main part)
					CompEnfFacilityDAO compEnfFacilityDAO = compEnfFacilityDAO(trans);
					FacilityHistory ret = compEnfFacilityDAO
							.createFacilityHistory(fh);
					// create the visit.
					SiteVisit newVisit = new SiteVisit(null, null,
							facility.getFpId(), new ArrayList<Evaluator>(),
							vd.getTestDate(), null,
							SiteVisitTypeDef.STACK_TEST, null);
					newVisit.setFacilityHistId(ret.getFacilityHistId());
					newVisit.setFacilityId(facility.getFacilityId());
					fceDAO.createSiteVisit(newVisit);
				}
			}
		} catch (DAOException de) {
			logger.error("facilityId=" + facility.getFacilityId() + "; "
					+ "stackTestId=" + stackTestId + " " + de.getMessage(), de);
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * @param facilityId
	 * @param stackTest
	 * @return void
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void deleteStackTest(Facility facility, StackTest stackTest, boolean readOnly, boolean deleteAttachmentFiles)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			deleteStackTest(facility, stackTest, trans, readOnly, deleteAttachmentFiles);
		} catch (DAOException de) {
			logger.error(
					"facilityId=" + facility.getFacilityId() + "; "
							+ "stackTestId=" + stackTest.getId() + " "
							+ de.getMessage(), de);
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * @param facilityId
	 * @param stackTest
	 * @param trans
	 * @return void
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private void deleteStackTest(Facility facility, StackTest stackTest,
			Transaction trans, boolean readOnly, boolean deleteAttachmentFiles) throws DAOException {
		
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		// StackTestDAO stackTestDAO = stackTestDAO(trans);
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		//DocumentDAO documentDAO = documentDAO(trans);
		// Remove the test visit dates
		stackTestDAO.removeTestVisitDates(stackTest.getId());
		if (readOnly) {
			try {
				createDeleteVisit(facility, stackTest.getId(),
						stackTest.getVisitDates(),
						new ArrayList<TestVisitDate>(), trans, readOnly);
			} catch (DAOException de) {
				throw de;
			}
		}
		// Remove the test pollutants
		stackTestDAO.removeTestPollutants(stackTest.getId());
		// Remove any witnesses
		stackTestDAO.removeStackTestWitnesses(stackTest.getId());
		// Remove attachments
		for (StAttachment att : stackTest.getAttachments()) {
			removeStAttachment(att, trans, readOnly, deleteAttachmentFiles);
			// cleanup document in staging
			if(!readOnly) {
				if (null != att.getDocumentID()) {
					documentDAO.removeDocumentReference(att.getDocumentID());
				}
			}
		}
		
		if (readOnly) {
			// Removing notes
			stackTestDAO.removeStackTestNotes(stackTest.getId());
			for (Note stNote : stackTest.getStackTestNotes())
				infraDAO.removeNote(stNote.getNoteId());
		}
		
		// Delete main record
		stackTestDAO.removeStackTest(stackTest.getId());

	}

	/**
	 * @param doc
	 * @return void
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeStAttachment(StAttachment doc, boolean readOnly) throws DAOException {
		Transaction trans = null;

		try {
			trans = TransactionFactory.createTransaction();
			removeStAttachment(doc, trans, readOnly, isPortalApp());
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
	}

	private void removeStAttachment(StAttachment doc, Transaction trans, boolean readOnly,
			boolean deleteAttachmentFiles) throws DAOException {
		//StackTestDAO stackTestDAO = stackTestDAO(trans);
		//DocumentDAO docDAO = documentDAO(trans);
		Document tsDocument = null;
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		DocumentDAO docDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		// first, flag document as temporary so it will be cleaned up later
		doc.setTemporary(true);
		docDAO.modifyDocument(doc);

		// then delete trade secret attachment (if any)
		if (doc.getTradeSecretDocId() != null) {
			tsDocument = docDAO.retrieveDocument(doc
					.getTradeSecretDocId());
			if (tsDocument != null) {
				tsDocument.setTemporary(true);
				docDAO.modifyDocument(tsDocument);
			} else {
				logger.error("No document found in stack test "
						+ doc.getStackTestId() + " for trade secret document: "
						+ doc.getTradeSecretDocId());
			}
			stackTestDAO.deleteStTradeSecretAttachment(doc);
		}
		
		if (isPortalApp() && deleteAttachmentFiles){
			docDAO.removeDocument(doc);
			if (tsDocument != null) {
				docDAO.removeDocument(tsDocument);
			}
		}
		
		stackTestDAO.deleteStAttachment(doc);
	}

	/**
	 * @param facilityId
	 * @param stackTestId
	 * @param fceId
	 * @param facilityName
	 * @param doLaaCd
	 * @param countyCd
	 * @param inspectClassCd
	 * @param beginDate
	 * @param endDate
	 * @parm failedPolls
	 * @param reviewer
	 * @param stackTestMethodCd
	 * @return StackTest[]
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public StackTest[] retrieveStackTestsBySearch(String facilityId,
			Integer stackTestId, Integer fceId, String facilityName,
			String doLaaCd, String countyCd,
			String permitClassCd, String facilityTypeCd, String dateBy, Timestamp beginDate,
			Timestamp endDate, boolean failedPolls, Integer reviewer,
			String stackTestMethodCd, String emissionTestState, String inspId,
			String stckId, String cmpId) throws DAOException {
		StackTest[] stackTests = null;
		try {
			stackTests = stackTestDAO().retrieveStackTestsBySearch(facilityId,
					stackTestId, fceId, facilityName, doLaaCd, countyCd,
					permitClassCd, facilityTypeCd, dateBy, beginDate, endDate, failedPolls,
					reviewer, stackTestMethodCd, emissionTestState, inspId,
					stckId, cmpId);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
		return stackTests;
	}

	/**
	 * @param facilityId
	 * @param visitDate
	 * @return StackTest[]
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public StackTest[] searchStackTests(String facilityId, Timestamp visitDate)
			throws DAOException {
		StackTest[] stackTests = searchStackTests(facilityId);
		ArrayList<StackTest> list = new ArrayList<StackTest>();
		TestVisitDate vd = new TestVisitDate(visitDate);
		for (StackTest st : stackTests) {
			if (st.getVisitDates().contains(vd)) {
				list.add(st);
			}
		}
		HashSet<Integer> wList = new HashSet<Integer>();
		for (StackTest st : list) {
			Evaluator[] eList = stackTestDAO().retrieveStackTestWitnesses(
					st.getId());
			for (Evaluator e : eList) {
				wList.add(e.getEvaluator());
			}
		}
		// have complete list of witnesses
		ArrayList<Evaluator> eList = new ArrayList<Evaluator>();
		for (Integer i : wList) {
			eList.add(new Evaluator(i));
		}
		FullComplianceEvalDAO fceDAO = fullComplianceEvalDAO();
		List<Evaluator> orderedList = fceDAO.orderEvaluators(eList);
		// put into first StackTest
		if (list.size() > 0) {
			list.get(0).setWitnesses(orderedList);
		}
		return list.toArray(new StackTest[0]);
	}

	/**
	 * @param facilityId
	 * @return StackTest[]
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public StackTest[] searchStackTests(String facilityId) throws DAOException {
		StackTest[] stackTests = null;
		try {
			stackTests = stackTestDAO().searchStackTests(facilityId);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
			throw de;
		}
		return stackTests;
	}
	
	public StackTest[] searchStackTests(String facilityId, String emissionTestState) throws DAOException {
		StackTest[] stackTests = null;
		try {
			stackTests = stackTestDAO().searchStackTests(facilityId, emissionTestState);
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
			throw de;
		}
		return stackTests;
	}

	/**
	 * @param fceId
	 * @return StackTest[]
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 */
	public StackTest[] retrieveStacktestByFce(Integer fceId)
			throws DAOException {
		StackTest[] stackTests = null;
		try {
			stackTests = stackTestDAO().retrieveStacktestByFce(fceId);
		} catch (DAOException de) {
			logger.error("fceId=" + fceId + " " + de.getMessage(), de);
			throw de;
		}
		return stackTests;
	}

	/**
	 * @param stackTestId
	 * @return List<StAttachment>
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 * 
	 */
	public List<StAttachment> retrieveStAttachments(Integer stackTestId, boolean readOnly)
			throws DAOException {
		List<StAttachment> attachments = null;
		
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		checkNull(stackTestId);
		try {
			attachments = stackTestDAO.retrieveStAttachments(stackTestId);
			for (StAttachment sa : attachments) {
				stackTestDAO.retrieveStTradeSecretAttachmentInfo(sa);
				// need to retrieve trade secret document to build URL
				if (sa.getTradeSecretDocId() != null) {
					Document tsDoc = documentDAO.retrieveDocument(
							sa.getTradeSecretDocId());
					if (tsDoc != null) {
						sa.setTradeSecretDocURL(tsDoc.getDocURL());
					} else {
						// should never happen
						logger.error("Unable to retrieve Trade Secret Document: "
								+ sa.getTradeSecretDocId()
								+ " for Stack Test "
								+ sa.getStackTestId());
					}
				}
			}
		} catch (RemoteException de) {
			logger.error("stackTestId=" + stackTestId + " " + de.getMessage(),
					de);
			throw new DAOException(de.getMessage(), de);
		}
		return attachments;
	}

	/**
	 * @param facilityId
	 * @return StackTest[]
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 */
	public StackTest[] retrieveStacktestsUnassigned(String facilityId)
			throws DAOException {
		StackTest[] stackTests = null;
		try {
			stackTests = stackTestDAO()
					.retrieveStacktestsUnassigned(facilityId);
		} catch (DAOException de) {
			logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
			throw de;
		}
		return stackTests;
	}

	/**
	 * @param fceId
	 * @param StackTest
	 *            []
	 * @returns void
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * 
	 */
	public void saveReassign(Integer fceId, StackTest[] tests)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		StackTestDAO stackTestDAO = stackTestDAO(trans);
		StackTest stCopy = null;
		try {
			for (StackTest st : tests) {
				stCopy = st;
				if (st.isSelected()) {
					st.setFceId(fceId);
					stackTestDAO.modifyStackTest(st);
				}
			}
		} catch (DAOException de) {
			logger.error(
					"fceId=" + fceId + "; " + "siteVisitId=" + stCopy.getId()
							+ " " + de.getMessage(), de);
			throw de;
		}
	}

	/**
	 * @return HashMap<String, SelectString[]>
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public HashMap<String, StackTestMethodPollutant[]> retrieveAllMethodPolls()
			throws DAOException {
		HashMap<String, StackTestMethodPollutant[]> rtn = new HashMap<String, StackTestMethodPollutant[]>();
		for (SelectItem si : CetaStackTestMethodDef.getData().getItems()
				.getAllItems()) {
			try {
				StackTestMethodPollutant[] methodPolls = stackTestDAO()
						.retrieveMethodPollutants((String) si.getValue());
				rtn.put((String) si.getValue(), methodPolls);
			} catch (DAOException de) {
				logger.error(
						"methodId=" + (String) si.getValue() + " "
								+ de.getMessage(), de);
				throw de;
			}
		}
		return rtn;
	}

	// /**
	// * @param st
	// * @return Integer
	// * @throws DAOException
	// *
	// * @ejb.interface-method view-type="remote"
	// * @ejb.transaction type="NotSupported"
	// */
	// public Integer[] duplicateStackTests(StackTest st) throws DAOException {
	// Integer[] dups = null;
	// try {
	// dups = stackTestDAO().duplicateStackTests(st);
	// } catch(DAOException de) {
	// logger.error("duplicateStackTests failed,  stack test id =" + st.getId()
	// + " " + de.getMessage(), de);
	// throw de;
	// }
	// return dups;
	// }

	/**
	 * @param doc
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyStAttachment(StAttachment doc, boolean readOnly) throws DAOException {
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			//StackTestDAO stackTestDAO = stackTestDAO(trans);
			
			StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
					: CommonConst.STAGING_SCHEMA));
			
			DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
					: CommonConst.STAGING_SCHEMA));
			
			//DocumentDAO documentDAO = documentDAO(trans);
			
			doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
			if(doc.getUploadDate() == null) {
				doc.setUploadDate(doc.getLastModifiedTS());
			}	
			stackTestDAO.modifyStAttachment(doc);
			documentDAO.modifyDocument(doc);
			if (doc.getTradeSecretDocId() != null) {
				stackTestDAO.modifyStTradeSecretAttachment(doc);
			}
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return;
	}

	private Attachment createStTradeSecretAttachment(
			Attachment publicAttachment, Attachment tsAttachment,
			InputStream fileStream, Transaction trans,
			boolean readOnly) throws DAOException {
		//StackTestDAO stackTestDAO = stackTestDAO(trans);
		
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));

		try {
			//tsAttachment = (Attachment) documentDAO(trans).createDocument(
			//		tsAttachment);
			tsAttachment = (Attachment) documentDAO.createDocument(
					tsAttachment);
			publicAttachment.setTradeSecretDocId(tsAttachment.getDocumentID());
			StAttachment sa = new StAttachment(publicAttachment);
			sa.setTradeSecretDocId(publicAttachment.getTradeSecretDocId());
			sa.setTradeSecretJustification(publicAttachment
					.getTradeSecretJustification());
			stackTestDAO.createStTradeSecretAttachment(sa);
			DocumentUtil.createDocument(tsAttachment.getPath(), fileStream);
			publicAttachment.setTradeSecretDocURL(tsAttachment.getDocURL());
			trans.complete();
		} catch (IOException ioe) {
			try {
				DocumentUtil.removeDocument(tsAttachment.getPath());
			} catch (IOException ioex) {
				logger.error("Exception while attempting to delete document: "
						+ tsAttachment.getPath(), ioex);
			}
			throw new DAOException(ioe.getMessage(), ioe);
		}

		return publicAttachment;
	}

	/**
	 * Create a new row in the Attachment table.
	 * 
	 * @param st
	 * @param attachment
	 * @param fileStream
	 * @return Attachment
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Attachment createStAttachment(StackTest st, Attachment attachment,
			InputStream fileStream, Attachment tsAttachment,
			InputStream tsInputStream,
			boolean readOnly) throws DAOException, ValidationException {
		Transaction trans = TransactionFactory.createTransaction();
		//StackTestDAO stackTestDAO = stackTestDAO(trans);
		
		StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));
		
		DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));

		try {
			// add document info to database
			// NOTE: This needs to be done before file is created in file store
			// since document id obtained from createDocument method is used as
			// the file name for the file store file
			//attachment = (Attachment) documentDAO(trans).createDocument(
			//		attachment);
			attachment = (Attachment) documentDAO.createDocument(
					attachment);
			
			DocumentUtil.createDocument(attachment.getPath(), fileStream);
			stackTestDAO.createStAttachment(new StAttachment(attachment));
			if (tsAttachment != null) {
				// need to set object id here because it defaults to 0
				attachment.setObjectId(st.getId().toString());
				createStTradeSecretAttachment(attachment, tsAttachment,
						tsInputStream, trans, readOnly);
			}
			trans.complete();

		} catch (DAOException e) {
			try {
				// need to delete public document if adding trade secret
				// document fails
				DocumentUtil.removeDocument(attachment.getPath());
			} catch (IOException ioex) {
				logger.error("Exception while attempting to delete document: "
						+ attachment.getPath(), ioex);
			}
			cancelTransaction(trans, e);
		} catch (IOException e) {
			cancelTransaction(trans, new RemoteException(e.getMessage(), e));
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return attachment;
	}

	/**
	 * @return List<StackTest>
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<StackTestedPollutant> newAfsStackTests() throws DAOException {
		StackTestDAO stackTestDAO = stackTestDAO();
		List<StackTestedPollutant> individualTests = null;
		try {
			individualTests = stackTestDAO.newAfsStackTests();
		} catch (DAOException de) {
			logger.error(de.getMessage(), de);
			throw de;
		}
		return individualTests;
	}

	/**
	 * @return List<StackTest>
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void createInspectionAssociation(Integer stackTestId,
			String inspectionId) throws DAOException {
		StackTestDAO stackTestDAO = stackTestDAO();
		Integer fceId = null;
		if (inspectionId != null) {
			fceId = stackTestDAO.getFceId(inspectionId);
		}
		if (fceId != null) {
			stackTestDAO.createInspectionAssociation(stackTestId, fceId);
		}

	}

	/**
	 * 
	 * @param facilityId
	 * @param StackTestedPollutant
	 * @return Integer
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer afsLockStackTestPollutant(String scscId,
			StackTestedPollutant stp) throws DAOException {
		Transaction trans = null;
		Integer id = null;
		try {
			trans = TransactionFactory.createTransaction();
			StackTestDAO stackTestDAO = stackTestDAO(trans);
			id = stackTestDAO.getAfsId(scscId);
			stackTestDAO.updateAfsId(scscId, id);
			stackTestDAO.afsLockStackTestPollutant(stp, id);
			stackTestDAO.updateStackTestLastModifiedOnly(stp.getStackTestId(),
					stp.getStackTestLastMod());
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return id;
	}

	/**
	 * 
	 * @param facilityId
	 * @param StackTestedPollutant
	 * @return Integer
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer afsSetDateStackTestPollutant(StackTestedPollutant stp)
			throws DAOException {
		Transaction trans = null;
		Integer id = null;
		try {
			trans = TransactionFactory.createTransaction();
			StackTestDAO stackTestDAO = stackTestDAO(trans);
			stackTestDAO.afsSetDateStackTestPollutant(stp);
			stackTestDAO.updateStackTestLastModifiedOnly(stp.getStackTestId(),
					stp.getStackTestLastMod());
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return id;
	}

	/**
	 * 
	 * @param scscId
	 * @param afsId
	 * @return List<StackTestedPollutant>
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public List<StackTestedPollutant> retrieveTestPollutant(String scscId,
			String afsId) throws DAOException {
		Transaction trans = null;
		List<StackTestedPollutant> rtn = null;
		checkNull(scscId);
		checkNull(afsId);
		try {
			rtn = stackTestDAO().retrieveTestPollutant(scscId, afsId);
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return rtn;
	}

	/**
	 * @param facilityId
	 * @return Timestamp
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 */
	public Timestamp retrieveLastStackTestDate(String facilityId)
			throws DAOException {
		// This finds the last test date of any stack test performed.
		Timestamp ts = null;
		try {
			ts = stackTestDAO().retrieveLastStackTestDate(facilityId);
		} catch (DAOException de) {
			logger.error("facilityId=" + facilityId + " " + de.getMessage(), de);
			throw de;
		}
		return ts;
	}

	/**
	 * @param stackTestId
	 * @return Timestamp
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * 
	 */
	public Timestamp retrieveLastStackTestDate(Integer stackTestId)
			throws DAOException {
		// This finds the last test date of the specified stack test.
		Timestamp ts = null;
		try {
			ts = stackTestDAO().retrieveLastStackTestDate(stackTestId);
		} catch (DAOException de) {
			logger.error("StackTestId=" + stackTestId + " " + de.getMessage(),
					de);
			throw de;
		}
		return ts;
	}

	/**
	 * Create a zip file containing the four AFS 9.x files.
	 * 
	 * @param String
	 * @param String
	 * @param String
	 * @param String
	 * @param String
	 * @param String
	 * @return Document
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void generateTempAttachmentZipFile(String fileNameComponent,
			String file91Nm, String file92Nm, String file93Nm, String file94Nm,
			String zNm) throws DAOException {
		OutputStream os = null;
		try {
			os = DocumentUtil.createDocumentStream(File.separator + zNm);
		} catch (IOException e) {
			String s = "Failed to createDocumentStream, path=" + zNm
					+ ", os is " + ((os != null) ? "not null" : "null")
					+ " for " + fileNameComponent;
			logger.error(s, e);
			throw new DAOException(s, e);
		}
		ZipOutputStream zos = new ZipOutputStream(os);
		try {
			InputStream docIS = null;

			docIS = DocumentUtil.getDocumentAsStream(File.separator + file91Nm);
			if (docIS != null) {
				try {
					addEntryToZip("f9.1_" + fileNameComponent + ".txt", docIS,
							zos);
				} catch (IOException ioe) {
					String s = "addEntryToZip(f9.1) failed for "
							+ fileNameComponent;
					logger.error(s, ioe);
					throw new DAOException(s, ioe);
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}

			docIS = DocumentUtil.getDocumentAsStream(File.separator + file92Nm);
			if (docIS != null) {
				try {
					addEntryToZip("f9.2_" + fileNameComponent + ".txt", docIS,
							zos);
				} catch (IOException ioe) {
					String s = "addEntryToZip(f9.2) failed for "
							+ fileNameComponent;
					logger.error(s, ioe);
					throw new DAOException(s, ioe);
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}

			docIS = DocumentUtil.getDocumentAsStream(File.separator + file93Nm);
			if (docIS != null) {
				try {
					addEntryToZip("f9.3_" + fileNameComponent + ".txt", docIS,
							zos);
				} catch (IOException ioe) {
					String s = "addEntryToZip(f9.3) failed for "
							+ fileNameComponent;
					logger.error(s, ioe);
					throw new DAOException(s, ioe);
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}

			docIS = DocumentUtil.getDocumentAsStream(File.separator + file94Nm);
			if (docIS != null) {
				try {
					addEntryToZip("f9.4_" + fileNameComponent + ".txt", docIS,
							zos);
				} catch (IOException ioe) {
					String s = "addEntryToZip(f9.4) failed for "
							+ fileNameComponent;
					logger.error(s, ioe);
					throw new DAOException(s, ioe);
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}

		} catch (IOException e) {
			String s = "Failed to zip attachments, path=" + zNm + ", os is "
					+ ((os != null) ? "not null" : "null") + " for "
					+ fileNameComponent;
			logger.error(s, e);
		} finally {
			try {
				zos.close();
				os.close();
			} catch (FileNotFoundException e) {
				String s = "Failed to zip attachments, path=" + zNm
						+ ", os is " + ((os != null) ? "not null" : "null")
						+ " for " + fileNameComponent;
				logger.error(s, e);
			} catch (IOException e) {
				String s = "Failed to zip attachments, path=" + zNm
						+ ", os is " + ((os != null) ? "not null" : "null")
						+ " for " + fileNameComponent;
				logger.error(s, e);
			}
		}
	}

	/**
	 * 
	 * @param facilityId
	 * @param stackTestMethodCd
	 * @return StackTest[]
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public StackTest[] retrieveStackTestsForValidate(String facilityId,
			String stackTestMethodCd) throws DAOException {
		return retrieveStackTestsBySearch(facilityId, null, null, null, null,null,
				null, null, null, null, null, false, null, stackTestMethodCd, null,
				null, null, null);
	}

	/**
	 * Get list of documents associated with the Stack Test.
	 * 
	 * @param EmissionsReport
	 * @return List<Document>
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<Document> getPrintableAttachmentList(StackTest test,
			boolean readOnly, Boolean hideTradeSecret) throws DAOException {
		List<Document> docList = new ArrayList<Document>();

		// if hideTradeSecret is null; that means this is requested during
		// submit and return trade secret version if exists otherwise public
		// version.

		// add attachments
		StAttachment attachments[] = test.getAttachments().toArray(
				new StAttachment[0]);

		boolean hTS;
		if (isPublicApp()) {
			hTS = true;
		} else if (hideTradeSecret != null) {
			hTS = hideTradeSecret;
		} else {
			hTS = false;
		}

		boolean hasTS = generateAttachments(docList, attachments, readOnly, hTS, test.getSubmittedDate());
		if (hideTradeSecret != null && !hideTradeSecret && !hasTS) {
			// indicate that trade secret version should not have been
			// requested.
			return null;
		}

		return docList;
	}

	private boolean generateAttachments(List<Document> docList,
			StAttachment attachments[], boolean readOnly, Boolean hTS, Timestamp submittedDate)
			throws DAOException {
		Document doc;
		boolean hasTS = false;
		// DocumentDAO documentDAO =
		// documentDAO(getSchema(CommonConst.READONLY_SCHEMA));
		DocumentDAO documentDAO = documentDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
				: CommonConst.STAGING_SCHEMA));

		for (StAttachment attach : attachments) {
			// skip attachments that were added by AQD
			// don't include documents added after stack test was submitted
			if (((CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP) || CompMgr.getAppName().equals(CommonConst.PUBLIC_APP))
					&& DefData.isDapcAttachmentOnly(attach.getAttachmentType())) || (submittedDate != null
							&& attach.getLastModifiedTS() != null
							&& attach.getLastModifiedTS().after(submittedDate))) {
				logger.debug("Excluding document "
						+ attach.getDocumentID()
						+ " from stack test printable attachment list. Document last modified date ("
						+ attach.getLastModifiedTS()
						+ ") is after stack test submission date ("
						+ submittedDate + ")");
				
				if (attach.getTradeSecretDocId() != null && attach.isTradeSecretAllowed()) {
					hasTS = true;
				}
				continue;
			}

			logger.debug(" Stack Test Attachment; description = "
					+ attach.getDescription());
			if (attach.getDocumentID() != null) {
				doc = documentDAO.retrieveDocument(attach.getDocumentID());
				// document description may not be in synch with application
				// document
				doc.setDescription(attach.getDescription());
				if (!doc.isTemporary()) {
					docList.add(doc);
				}

				if (!hTS && attach.getTradeSecretDocId() != null && (submittedDate != null || attach.isTradeSecretAllowed())) {
					doc = documentDAO.retrieveDocument(attach
							.getTradeSecretDocId());
					if (doc != null) {
						// document description may not be in synch with
						// stack test document
						doc.setDescription(attach.getDescription()
								+ " - Trade Secret version");
						if (!doc.isTemporary()) {
							docList.add(doc);
							hasTS = true;
						}
					}
				}
			}
		}
		return hasTS;
	}

	private StackTestDocument getStackTestDocument(StackTest test,
			boolean hideTradeSecret) {
		StackTestDocument doc = new StackTestDocument();
		String submittedDocDesc = "Printable View of What Will Be Submitted from Data Entered";
		if (test.getSubmittedDate() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			submittedDocDesc = "Printable View of Stack Test Data Submitted on "
					+ dateFormat.format(test.getSubmittedDate());
		}
		// Set the path elements of the temp doc.
		doc.setDescription(submittedDocDesc
				+ (hideTradeSecret ? "" : " with trade secret data"));
		doc.setFacilityID(test.getFacilityId());
		doc.setStackTestId(test.getId());
		doc.setTemporary(true);
		doc.setOverloadFileName("StackTest" + test.getStckId()
				+ (hideTradeSecret ? "" : "_TS") + ".pdf");
		return doc;
	}
	
	private SubmissionLog getSubmissionLogForStackTest(StackTest test) {
		SubmissionLog log = null;
		SubmissionLog searchSubmissionLog = new SubmissionLog();
		Task t = new Task();
		HashMap<TaskType, String> taskTypeDescs = t.getTaskTypeDescs();
		searchSubmissionLog.setFacilityId(test.getFacilityId());
		searchSubmissionLog.setSubmissionType(taskTypeDescs.get(TaskType.ST));
		
		try {
			int count = 0;
			for (SubmissionLog tmp : facilityDAO().searchSubmissionLog(
					searchSubmissionLog, test.getSubmittedDate(),
					test.getSubmittedDate())) {
				log = tmp;
				count++;
			}
			if (count > 0) {
				logger.error("Multiple submissions found for stack test: "
						+ test.getStckId() + ". Setting user id to "
						+ log.getGatewayUserName());
			} else if (count == 0) {
				logger.error("No submissions found for stack test: "
							+ test.getStckId());
			}
		} catch (DAOException e) {
			logger.error("Exception retrieving stack test from submission log"
					+ test.getStckId(), e);
		}
		return log;
	}
	
	/**
	 * Return pdf version of stack test as an InputStream.
	 * 
	 * @param test
	 * @param hideTradeSecret
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	private StackTestDocument createStackTestDocument(Facility facility, StackTest test,
			String userName, boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		StackTestDocument testDoc = null;
		try {
			testDoc = getStackTestDocument(test, hideTradeSecret);
			OutputStream os = DocumentUtil.createDocumentStream(testDoc
					.getPath());
			try {
				EmissionsTestPdfGenerator generator = new EmissionsTestPdfGenerator();
				generator.setUserName(userName);
				generator.generatePdf(facility, test, os, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);

				if (!hideTradeSecret) {
					PdfGeneratorBase.addTradeSecretWatermark(testDoc.getPath());
				}
			} catch (DocumentException e) {
				logger.error(
						"Exception writing stack test to stream for "
								+ test.getStckId(), e);
				throw new IOException(
						"Exception writing stack test to stream for "
								+ test.getStckId());
			}
			os.close();
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting stack test as stream for "
							+ test.getStckId(), e);
		}
		return testDoc;
	}
	
	/**
	 * Get list of documents associated with the TV/SMTV emission report.
	 * 
	 * @param
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<Document> getPrintableDocumentList(Facility facility,
			TmpDocument facilityDoc, StackTest test, String user,
			boolean readOnly, Boolean hideTradeSecret) throws DAOException {
		
		List<Document> docList = new ArrayList<Document>();

		// if hideTradeSecret is null; that means this is requested during
		// submit and return trade secret version if exists otherwise public
		// version.
		boolean hTS;
		if (hideTradeSecret != null) {
			hTS = hideTradeSecret;
		} else {
			hTS = false;
		}
		
		StackTestDocument submittedTestDoc = null;
		// use previously generated PDF file if stack test has been submitted
		if (test.getSubmittedDate() != null && !isPublicApp()) { 
			// get submitted pdf document
			submittedTestDoc = getStackTestDocument(test, hTS);
			try {
				if (submittedTestDoc != null && DocumentUtil.canRead(submittedTestDoc.getPath())) {
					// use existing doc if already generated
					docList.add(submittedTestDoc);
				} else {
					// generate new doc if it does not already exist
					String userName = null;
					SubmissionLog log = getSubmissionLogForStackTest(test);
					if (log != null) {
						userName = log.getGatewayUserName();
					}
					submittedTestDoc = createStackTestDocument(facility,
							test, userName, hTS, true, false);
					if (submittedTestDoc != null
							&& DocumentUtil.canRead(submittedTestDoc.getPath())) {
						docList.add(submittedTestDoc);
					}
				}
			} catch (IOException e) {
				logger.error(
						"Exception checking on submitted PDF document for "
								+ test.getStckId(), e);
			}
		}
		
		TmpDocument sumRpt = new TmpDocument();
		generateTempEmissionsTest(facility, test, user, sumRpt, hTS, false, false);
		docList.add(sumRpt);

		if (facilityDoc != null && !isPublicApp()) {
			docList.add(facilityDoc);
		}

		Document zipDoc = null;
		if (test.getSubmittedDate() != null && !isPublicApp()) { 
			zipDoc = generateTempAttachmentZipFile(test,
				facility.getFacilityId(), facilityDoc, null, submittedTestDoc, readOnly, hTS);
		} else {
			zipDoc = generateTempAttachmentZipFile(test,
				facility.getFacilityId(), facilityDoc, sumRpt, null, readOnly, hTS);
		}
		if (zipDoc != null) {
			docList.add(zipDoc);
		}
		
		return docList;
	}

	/**
	 * Create a zip file containing stack test data and all its related
	 * attachments and download its contents.
	 * 
	 * @param StackTest
	 * @param facilityId
	 * @param TmpDocument
	 * @param TmpDocument
	 * @return Document
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document generateTempAttachmentZipFile(StackTest test,
			String facilityId, TmpDocument facilityPdf,
			TmpDocument emissionsTestPdf, StackTestDocument submittedTestDoc, boolean readOnly,
			Boolean hideTradeSecret) {

		TmpDocument erDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		erDoc.setDescription("Stack Test Package zip file");
		erDoc.setFacilityID(facilityId);
		erDoc.setTemporary(true);
		if(hideTradeSecret) {
			erDoc.setTmpFileName(test.getStckId() + ".zip");
		} else {
			erDoc.setTmpFileName(test.getStckId() + "_TS" + ".zip");
		}
		
		erDoc.setContentType(Document.CONTENT_TYPE_ZIP);

		// make sure temporary directory exists
		OutputStream os = null;
		try {
			DocumentUtil.mkDirs(erDoc.getDirName());
			os = DocumentUtil.createDocumentStream(erDoc.getPath());
		} catch (IOException e) {
			String s = "Failed to createDocumentStream, path="
					+ erDoc.getPath() + ", os is "
					+ ((os != null) ? "not null" : "null") + " for stack test "
					+ test.getId();
			logger.error(s, e);
			erDoc = null;
		}
		ZipOutputStream zos = new ZipOutputStream(os);
		boolean ok = false;
		try {
			ok = zipAttachments(test, zos, facilityPdf, emissionsTestPdf, submittedTestDoc,
					hideTradeSecret, readOnly);
		} catch (IOException e) {
			String s = "Failed to zip attachments, path=" + erDoc.getPath()
					+ ", os is " + ((os != null) ? "not null" : "null")
					+ " for stack test " + test.getId();
			logger.error(s, e);
			erDoc = null;
		} finally {
			try {
				zos.close();
				os.close();
			} catch (FileNotFoundException e) {
				String s = "Failed to zip attachments, path=" + erDoc.getPath()
						+ ", os is " + ((os != null) ? "not null" : "null")
						+ " for stack test " + test.getId();
				logger.error(s, e);
				erDoc = null;
			} catch (IOException e) {
				String s = "Failed to zip attachments, path=" + erDoc.getPath()
						+ ", os is " + ((os != null) ? "not null" : "null")
						+ " for stack test " + test.getId();
				logger.error(s, e);
				erDoc = null;
			}
		}
		if (!ok)
			erDoc = null;
		return erDoc;
	}

	private boolean zipAttachments(StackTest test, ZipOutputStream zos,
			TmpDocument facilityPdf, TmpDocument emissionsTestPdf, StackTestDocument submittedTestDoc,
			Boolean hideTradeSecret, boolean readOnly) throws DAOException {
		DocumentService docBO = null;
		// int attachCnt = 0;
		try {
			docBO = ServiceFactory.getInstance().getDocumentService();
		} catch (ServiceFactoryException e) {
			logger.error("Exception accessing DocumentService for stack test "
					+ test.getId(), e);
			throw new DAOException(
					"Exception accessing DocumentService for stack test "
							+ test.getId(), e);
		}

		InputStream docIS = null;
		if (submittedTestDoc != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(submittedTestDoc
						.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ submittedTestDoc.getPath()
						+ ") failed for submitted stack test pdf " + test.getId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ submittedTestDoc.getPath()
						+ ") failed for submitted stack test pdf " + test.getId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(submittedTestDoc.getOverloadFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error("addEntryToZip) failed for submitted stack test pdf "
							+ test.getId(), ioe);
					return false;
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}
		}
		
		docIS = null;
		if (emissionsTestPdf != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(emissionsTestPdf
						.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ emissionsTestPdf.getPath()
						+ ") failed for stack test pdf " + test.getId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error("DocumentUtil.getDocumentAsStream("
						+ emissionsTestPdf.getPath()
						+ ") failed for stack test pdf " + test.getId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(emissionsTestPdf.getTmpFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error("addEntryToZip) failed for stack test pfd "
							+ test.getId(), ioe);
					return false;
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}
		}

		docIS = null;
		if (facilityPdf != null) {
			try {
				docIS = DocumentUtil.getDocumentAsStream(facilityPdf.getPath());
				// attachCnt++;
			} catch (FileNotFoundException e) {
				docIS = null;
				logger.error(
						"DocumentUtil.getDocumentAsStream("
								+ facilityPdf.getPath()
								+ ") failed for facility pdf for stack test "
								+ test.getId(), e);
				return false;
			} catch (IOException e) {
				docIS = null;
				logger.error(
						"DocumentUtil.getDocumentAsStream("
								+ facilityPdf.getPath()
								+ ") failed for facility pdf for stack test "
								+ test.getId(), e);
				return false;
			}
			if (docIS != null) {
				try {
					addEntryToZip(facilityPdf.getTmpFileName(), docIS, zos);
				} catch (IOException ioe) {
					logger.error(
							"addEntryToZip) failed for facility pdf for stack test "
									+ test.getId(), ioe);
					return false;
				} finally {
					try {
						docIS.close();
					} catch (IOException e) {
						;
					}
				}
			}
		}

		// add attachments to zip file
		List<StAttachment> attachmentList = new ArrayList<StAttachment>();
		attachmentList.addAll(test.getAttachments());
		HashSet<Integer> docIdSet = new HashSet<Integer>();
		for (StAttachment attachment : attachmentList) {
			// don't include documents added after stack test was submitted
			if (test.getSubmittedDate() != null
					&& attachment.getLastModifiedTS() != null
					&& attachment.getLastModifiedTS().after(
							test.getSubmittedDate())) {
				logger.debug("Excluding document "
						+ attachment.getDocumentID()
						+ " from stack test zip file. Document last modified date ("
						+ attachment.getLastModifiedTS()
						+ ") is after stack test submission date ("
						+ test.getSubmittedDate() + ")");
				continue;
			}
						
			if (attachment.getDocumentID() != null
					&& !docIdSet.contains(attachment.getDocumentID())) {
				docIdSet.add(attachment.getDocumentID());
				Document doc = null;
				try {
					doc = docBO.getDocumentByID(attachment.getDocumentID(),
							readOnly);
				} catch (RemoteException re) {
					logger.error(
							"docBO.getDocumentByID("
									+ attachment.getDocumentID()
									+ ") failed for stack test " + test.getId(),
							re);
					return false;
				}
				docIS = null;
				if (doc != null && !doc.isTemporary()) {
					try {
						docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
						// attachCnt++;
					} catch (FileNotFoundException e) {
						docIS = null;
						logger.error(
								"DocumentUtil.getDocumentAsStream("
										+ doc.getPath()
										+ ") failed for stack test "
										+ test.getId(), e);
						return false;
					} catch (IOException e) {
						docIS = null;
						logger.error(
								"DocumentUtil.getDocumentAsStream("
										+ doc.getPath()
										+ ") failed for stack test "
										+ test.getId(), e);
						return false;
					}
					if (docIS != null) {
						try {
							addEntryToZip(
									getNameForDoc(attachment.getDocTypeCd(),
											doc.getDocumentID().toString(),
											null, doc.getExtension()), docIS,
									zos);
						} catch (IOException ioe) {
							logger.error(
									"addEntryToZip(" + doc.getDescription()
											+ ") failed for stack test "
											+ test.getId(), ioe);
							return false;
						} finally {
							try {
								docIS.close();
							} catch (IOException e) {
								;
							}
						}
					}
				} else {
					String msg = "No document found with id "
							+ attachment.getDocumentID();
					if (doc != null) {
						msg = "Document " + attachment.getDocumentID()
								+ " is temporary";
					}
					logger.error(msg, new Exception());
					return false;
				}
			}

			if (!hideTradeSecret) {
				Document doc = null;
				if (attachment.getTradeSecretDocId() != null
						&& !docIdSet.contains(attachment.getTradeSecretDocId()) && (test.getSubmittedDate() != null || attachment.isTradeSecretAllowed())) {
					docIdSet.add(attachment.getTradeSecretDocId());
					try {
						doc = docBO.getDocumentByID(
								attachment.getTradeSecretDocId(), readOnly);

					} catch (RemoteException re) {
						logger.error(
								"docBO.getDocumentByID("
										+ attachment.getDocumentID()
										+ ") failed for stack test "
										+ test.getId(), re);
						return false;
					}
					if (doc != null && !doc.isTemporary()) {
						docIS = null;
						try {
							docIS = DocumentUtil.getDocumentAsStream(doc
									.getPath());
							// attachCnt++;
						} catch (FileNotFoundException e) {
							docIS = null;
							logger.error(
									"DocumentUtil.getDocumentAsStream("
											+ doc.getPath()
											+ ") failed for stack test "
											+ test.getId(), e);
							return false;
						} catch (IOException e) {
							docIS = null;
							logger.error(
									"DocumentUtil.getDocumentAsStream("
											+ doc.getPath()
											+ ") failed for stack test "
											+ test.getId(), e);
							return false;
						}

						if (docIS != null) {
							try {
								addEntryToZip(
										getNameForDoc(
												attachment.getDocTypeCd(), doc
														.getDocumentID()
														.toString(), "_TS",
												doc.getExtension()), docIS, zos);

							} catch (IOException ioe) {
								logger.error(
										"addEntryToZip(" + doc.getDescription()
												+ ") failed for stack test "
												+ test.getId(), ioe);
								return false;
							} finally {
								try {
									docIS.close();
								} catch (IOException e) {
									;
								}
							}
						}

					} else if (doc == null) {
						logger.error("No document found with id "
								+ attachment.getDocumentID()
								+ " for stack test " + test.getId());
					}
				}
			}
		}
		return true;
	}

	/**
	 * Create a more descriptive name for attachments to be included in a zip
	 * file.
	 * 
	 * @param docTypeCd
	 *            document type code.
	 * @param doc
	 *            document record.
	 * @return
	 */
	private String getNameForDoc(String docTypeCd, String docId, String suffix,
			String extension) {
		StringBuffer docName = new StringBuffer();
		if (docTypeCd != null) {
			docName.append(StAttachmentTypeDef.getData().getItems()
					.getItemDesc(docTypeCd)
					+ "_");
		}
		docName.append(docId);
		if (suffix != null) {
			docName.append(suffix);
		}
		if (extension != null && extension.length() > 0) {
			docName.append("." + extension);
		}
		return docName.toString();
	}

	private boolean generateTempEmissionsTest(Facility facility,
			StackTest test, String user, TmpDocument rptDoc,
			boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		boolean rtn = false;
		try {
			// Set the path elements of the temp doc.
			rptDoc.setDescription("Stack Test");
			rptDoc.setFacilityID(facility.getFacilityId());
			rptDoc.setTemporary(true);
			rptDoc.setTmpFileName("stackTest_" + test.getId()
					+ (hideTradeSecret ? "" : "_TS") + ".pdf");
			// the items below are not needed since this document data is not
			// stored in the database
			// appDoc.setLastModifiedBy();
			// appDoc.setLastModifiedTS(new
			// Timestamp(System.currentTimeMillis()));
			// appDoc.setUploadDate(appDoc.getLastModifiedTS());
			DocumentUtil.mkDir(rptDoc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(rptDoc
					.getPath());
			rtn = writeEmissionsTestToStream(facility, test, os, rptDoc,
					hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
			os.close();

		} catch (Exception ex) {
			logger.error("Cannot generate stack test ", ex);
			throw new DAOException("Cannot generate stack test", ex);
		}
		return rtn;
	}

	private boolean writeEmissionsTestToStream(Facility facility,
			StackTest test, OutputStream os, TmpDocument doc,
			boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws IOException {
		boolean rtn = false;
		try {
			// make sure we have all Facility information
			EmissionsTestPdfGenerator generator = new EmissionsTestPdfGenerator();
			generator.generatePdf(facility, test, os, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
			// rtn = generator.isHasTS();
			// if (rtn)
			// PdfGeneratorBase.addTradeSecretWatermarkHorizontal(doc
			// .getPath());
		} catch (DocumentException e) {
			logger.error(
					"Exception writing emission report to stream for stack test "
							+ test.getId(), e);
			throw new IOException(
					"Exception writing emission report to stream for report "
							+ test.getId());
		}
		return rtn;
	}

	/**
	 * Returns all of the StackTest comments by stackTest ID.
	 * 
	 * @param int The stackTest ID
	 * 
	 * @return Note[] All comments of this StackTest.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Note[] retrieveStackTestNotes(int stackTestID) throws DAOException {
		return stackTestDAO().retrieveStackTestNotes(stackTestID);
	}

	public StackTestNote createStackTestNote(StackTestNote stackTestNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		StackTestNote ret = null;

		try {
			ret = createStackTestNote(stackTestNote, trans);

			if (ret != null) {
				trans.complete();
			} else {
				trans.cancel();
				logger.error("Failed to insert StackTest Note");
			}
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param stackTestNote
	 * @param trans
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public StackTestNote createStackTestNote(StackTestNote stackTestNote,
			Transaction trans) throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		StackTestDAO stackTestDAO = stackTestDAO(trans);

		Note tempNote = infraDAO.createNote(stackTestNote);

		if (tempNote != null) {
			stackTestNote.setNoteId(tempNote.getNoteId());

			stackTestDAO.createStackTestNote(stackTestNote.getStackTestId(),
					stackTestNote.getNoteId());
		} else
			stackTestNote = null;
		return stackTestNote;
	}

	/**
	 * 
	 * @return boolean <tt>true</tt> if a record was updated.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyStackTestNote(StackTestNote stackTestNote)
			throws DAOException {
		return infrastructureDAO().modifyNote(stackTestNote);
	}

	/**
	 * Synchronize <code>stackTest</code> with the current view of the facility
	 * profile.
	 * 
	 * @param stackTest
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @return true if changes were made to synchronize the stackTest with the
	 *         facility inventory, false otherwise
	 */
	public boolean synchStackTestWithCurrentFacilityProfile(StackTest stackTest)
			throws DAOException {
		Transaction trans = null;
		boolean modified = false;

		FacilityDAO facDAO = facilityDAO(getSchema(CommonConst.STAGING_SCHEMA));
		Facility currentFacility = null;
		Facility stFacility = stackTest.getAssociatedFacility();
		currentFacility = facDAO.retrieveFacility(stFacility.getFacilityId());
		if (!currentFacility.getFpId().equals(stFacility.getFpId())
				|| !currentFacility.getLastModified().equals(
						stFacility.getLastModified())) {
			try {
				trans = TransactionFactory.createTransaction();
				synchStackTestWithCurrentFacilityProfile(stackTest,
						currentFacility, trans);
				trans.complete();
				stackTest = retrieveStackTest(stackTest.getId(), false);
				stFacility = facDAO.retrieveFacilityData(stFacility.getFpId());
				//StackTestNote note = createFacilityChangedNote(stackTest,
				//		stFacility, currentFacility);
				//createStackTestNote(note);
				modified = true;
			} catch (DAOException de) {
				cancelTransaction(trans, de);
			} finally {
				closeTransaction(trans);
			}
		}

		return modified;
	}

	private void synchStackTestWithCurrentFacilityProfile(StackTest stackTest,
			Facility currentFacility, Transaction trans) throws DAOException {

		if (!EmissionsTestStateDef.SUBMITTED.equals(stackTest
				.getEmissionTestState())
				&& stackTest.getAssociatedFacility().getVersionId() != -1) { // replace
																				// with
																				// current
																				// facility
																				// profile
			// The updated fpId will be stored
			// when the record is updated.
			stackTest.setAssociatedFacility(currentFacility);
			if (stackTest.getAssociatedFacility() == null) {
				throw new DAOException("facilityBO.retrieveFacility("
						+ stackTest.getFacilityId()
						+ ") failed to find the profile");
			}
			stackTest.setFpId(stackTest.getAssociatedFacility().getFpId());
			stackTest.setValidated(false);
		}
	}

	private StackTestNote createFacilityChangedNote(StackTest stackTest,
			Facility oldFacility, Facility newFacility) {
		StackTestNote note = new StackTestNote();
		String noteTxt = "The facility inventory for this Stack Test was updated to the current version on  "
				+ new Timestamp(System.currentTimeMillis()).toString();
		note.setNoteTxt(noteTxt);
		note.setStackTestId(stackTest.getId());
		note.setDateEntered(new Timestamp(System.currentTimeMillis()));
		note.setNoteTypeCd(NoteType.DAPC);
		note.setUserId(InfrastructureDefs.getCurrentUserId());
		return note;
	}
	
	/**
	 * 
	 * @param task
	 * @throws DAOException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void addSubmissionAttachments(Task task) throws DAOException,
			IOException, FileNotFoundException {
		StackTest stackTest = task.getStackTest();

		// don't want duplicate attachments
		task.getAttachments().clear();

		// add zip file with application attachments to task for submission
		if (stackTest.isHasAttachments()) {
			logger.debug("zipping stack test attachments...");
			Document zipDoc = generateTempStackTestAttachmentZipFile(
					stackTest, null, null, false, false);
			us.oh.state.epa.portal.datasubmit.Attachment submitAttachment = new us.oh.state.epa.portal.datasubmit.Attachment(
					Integer.toString(stackTest.getId()),
					"text/zip", DocumentUtil.getFileStoreRootPath()
							+ zipDoc.getPath(), null, null);
			submitAttachment.setSystemFilename(DocumentUtil
					.getFileStoreRootPath() + zipDoc.getPath());
			task.getAttachments().add(submitAttachment);
			logger.debug("Done zipping stack test attachments.");
		}

		// generate PDF related to this submission
		StackTestDocument rpt = new StackTestDocument();
		// NOTE: for now, set hideTradeSecret to true, until portal requirements
		// are available.
		createStackTestPDFDocument(stackTest, rpt,
				task.getUserName(), true, false, true);
		Document zipDoc = generateTempStackTestPDFZipFile(stackTest);
		if (zipDoc != null) {
			// logger.error("Debug #2966: generateTempstackTestPDFZipFile: attachment description"
			// + zipDoc.getDescription() +
			// ", basePath " + zipDoc.getBasePath());
			us.oh.state.epa.portal.datasubmit.Attachment submitAttachment = new us.oh.state.epa.portal.datasubmit.Attachment(
					Integer.toString(stackTest.getId()),
					"text/zip", DocumentUtil.getFileStoreRootPath()
							+ zipDoc.getPath(), null, null);
			submitAttachment.setSystemFilename(DocumentUtil
					.getFileStoreRootPath() + zipDoc.getPath());
			task.getAttachments().add(submitAttachment);
		} // else {
		// logger.error("generateTempStackTestPDFZipFile is null");
		// }
	}
	
	/**
	 * Return pdf version of application as an InputStream.
	 * 
	 * @param app
	 * @param hideTradeSecret
	 * @return void
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	private boolean createStackTestPDFDocument(
			StackTest stackTest, StackTestDocument rpt,
			String userName, boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws DAOException {
		boolean rtn = false;
		try {
			getStackTestPDFDocument(stackTest, rpt, hideTradeSecret);
			OutputStream os = DocumentUtil.createDocumentStream(rpt.getPath());
			try {
				EmissionsTestPdfGenerator generator = new EmissionsTestPdfGenerator();
				generator.setUserName(userName);
				generator.setAttestationAttached(userName != null
						&& reportHasAttestationDocument(stackTest));
				generator.generatePdf(stackTest.getAssociatedFacility(), stackTest, os, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
				rtn = generator.isHasTS();
				// There is no trade secret information in the Stack Test
				// itself.
				// Only attachments can be designated as trade secret.
				if(rtn)
				 PdfGeneratorBase.addTradeSecretWatermarkHorizontal(rpt.getPath());
			} catch (DocumentException e) {
				throw new IOException(
						"Exception writing stack test to stream");
			//} catch (ServiceFactoryException e) {
			//	throw new DAOException("Exception creating PDF Generator", e);
			} finally {
				if (os != null) {
					os.close();
				}
			}
		} catch (IOException e) {
			throw new DAOException(
					"Exception getting application report as stream", e);
		}
		return rtn;
	}
	
	/**
	 * Create a zip file containing application pdf files.
	 * 
	 * @param app
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document generateTempStackTestPDFZipFile(
			StackTest stackTest) throws FileNotFoundException,
			IOException {

		TmpDocument appDoc = new TmpDocument();
		// Set the path elements of the temp doc.
		appDoc.setDescription("Stack Test report (pdf) zip file");
		appDoc.setFacilityID(stackTest.getFacilityId());
		appDoc.setTemporary(true);
		appDoc.setTmpFileName(stackTest.getId() + "PDFReport.zip");

		// make sure temporary directory exists
		DocumentUtil.mkDirs(appDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(appDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		zipStackTestPDFReportFiles(stackTest, zos);
		zos.close();
		os.close();

		return appDoc;
	}
	
	private void zipStackTestPDFReportFiles(
			StackTest stackTest, ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		StackTestDocument nonTSDoc = new StackTestDocument();
		StackTestDocument tsDoc = new StackTestDocument();
		List<StackTestDocument> attachmentList = new ArrayList<StackTestDocument>();
		getStackTestPDFDocument(stackTest, nonTSDoc, true);
		getStackTestPDFDocument(stackTest, tsDoc, false);
		if (nonTSDoc != null && DocumentUtil.canRead(nonTSDoc.getPath())) {
			attachmentList.add(nonTSDoc);
		}
		if (tsDoc != null && DocumentUtil.canRead(tsDoc.getPath())) {
			attachmentList.add(tsDoc);
		}

		// add attachments to zip file
		for (StackTestDocument attachment : attachmentList) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(attachment.getPath());
			} catch (FileNotFoundException e) {
				String errorMsg = attachment.getDescription();
				if (errorMsg.length() > 50) {
					errorMsg = errorMsg.substring(0, 47) + "...";
				}
				throw new FileNotFoundException(errorMsg);
			}
			addEntryToZip(attachment.getDescription(), docIS, zos);

			if (docIS != null) {
				docIS.close();
			}
		}
	}
	
	/**
	 * Create a zip file containing application data and all its related
	 * attachments and download its contents.
	 * 
	 * @param app
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document generateTempStackTestAttachmentZipFile(
			StackTest stackTest, StackTestDocument submittedRpt,
			TmpDocument rpt, boolean hideTradeSecret, boolean readOnly)
			throws FileNotFoundException, IOException {

		TmpDocument tmpDoc;
		tmpDoc = new TmpDocument();
		tmpDoc.setDescription("Stack Test ZIP file");
		tmpDoc.setFacilityID(stackTest.getFacilityId());
		tmpDoc.setTemporary(true);
		tmpDoc.setTmpFileName("stackTest_"
				+ stackTest.getId() + ".zip");
		DocumentUtil.mkDirs(tmpDoc.getDirName());
		OutputStream os = DocumentUtil.createDocumentStream(tmpDoc.getPath());
		ZipOutputStream zos = new ZipOutputStream(os);
		boolean hasFiles = zipAttachments(stackTest, submittedRpt, rpt,
				hideTradeSecret, zos, readOnly);
		if (!hasFiles)
			return null;
		else {
			zos.close();
			os.close();
			return tmpDoc;
		}
	}

	private boolean zipAttachments(StackTest stackTest,
			StackTestDocument submittedRpt, TmpDocument rpt,
			boolean hideTradeSecret, ZipOutputStream zos, boolean readOnly)
			throws FileNotFoundException, IOException {
		DocumentService docBO = null;
		int attachCnt = 0;
		try {
			docBO = ServiceFactory.getInstance().getDocumentService();
		} catch (ServiceFactoryException e) {
			logger.error("Exception accessing DocumentService", e);
			throw new IOException("Exception accessing DocumentService");
		}

		// add attachments to zip file
		HashSet<Integer> docIdSet = new HashSet<Integer>();
		List<StAttachment> a = stackTest.getAttachments();

		if (submittedRpt != null) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(submittedRpt.getPath());
				attachCnt++;
			} catch (FileNotFoundException e) {
				throw new RemoteException("submitted stack test " +
						stackTest.getId() + " pdf not found", e);
			}
			addEntryToZip(submittedRpt.getOverloadFileName(), docIS, zos);
			docIS.close();
		}

		if (rpt != null) {
			InputStream docIS = null;
			try {
				docIS = DocumentUtil.getDocumentAsStream(rpt.getPath());
				attachCnt++;
			} catch (FileNotFoundException e) {
				throw new RemoteException("stack test "
						+ stackTest.getId() + " pdf not found", e);
			}
			addEntryToZip(rpt.getTmpFileName(), docIS, zos);
			docIS.close();
		}

		//for (int i = 0; i < a.size(); i++) {
		for (StAttachment att : stackTest.getAttachments()) {
			// skip attachments that were added by DAPC
			// don't include documents added after stack test was submitted
			if (DefData.isDapcAttachmentOnly(att.getAttachmentType()) || (stackTest.getSubmittedDate() != null
					&& att.getLastModifiedTS() != null
					&& att.getLastModifiedTS().after(
							stackTest.getSubmittedDate()))) {
				logger.debug("Excluding document "
						+ att.getDocumentID()
						+ " from stack test zip file. Document last modified date ("
						+ att.getLastModifiedTS()
						+ ") is after stack test submission date ("
						+ stackTest.getSubmittedDate() + ")");
				continue;
			}
			
			if (att.getDocumentID() != null
					&& !docIdSet.contains(att.getDocumentID())) {
				docIdSet.add(att.getDocumentID());
				Document doc = docBO.getDocumentByID(att.getDocumentID(),
						readOnly);
				if (doc != null && !doc.isTemporary()) {
					InputStream docIS = null;
					try {
						docIS = DocumentUtil.getDocumentAsStream(doc.getPath());
						attachCnt++;
					} catch (FileNotFoundException e) {
						throw new FileNotFoundException(att.getDescription());
					}
					addEntryToZip(getNameForAttachment(att, ""), docIS, zos);
					docIS.close();
				} else if (doc == null) {
					logger.error("No document found with id "
							+ att.getDocumentID() + " for stack test "
							+ stackTest.getId());
				}
			}
			if (!hideTradeSecret) {
				if (att.getTradeSecretDocId() != null
						&& !docIdSet.contains(att.getTradeSecretDocId()) && (stackTest.getSubmittedDate() != null || att.isTradeSecretAllowed())) {
					docIdSet.add(att.getTradeSecretDocId());
					Document doc = docBO.getDocumentByID(
							att.getTradeSecretDocId(), readOnly);
					if (doc != null && !doc.isTemporary()) {
						InputStream docIS = null;
						try {
							docIS = DocumentUtil.getDocumentAsStream(doc
									.getPath());
							attachCnt++;
						} catch (FileNotFoundException e) {
							throw new FileNotFoundException(
									att.getDescription());
						}
						addEntryToZip(getNameForAttachment(att, "_TS"), docIS,
								zos);
						docIS.close();
					} else if (doc == null) {
						logger.error("No document found with id "
								+ att.getDocumentID()
								+ " for stack test "
								+ stackTest.getId());
					}
				}
			}
		}
		return attachCnt > 0;
	}
	
	private String getNameForAttachment(StAttachment doc,
			String suffix) {
		StringBuffer docName = new StringBuffer();
		// add description here?
		docName.append(doc.getDocumentID());
		docName.append(suffix);
		if (doc.getExtension() != null && doc.getExtension().length() > 0) {
			docName.append("." + doc.getExtension());
		}
		return docName.toString();
	}
	
	public void getStackTestPDFDocument(
			StackTest stackTest, StackTestDocument doc, boolean hideTradeSecret) {
		// Set the path elements of the temp doc.
		String submittedDocDesc = "Printable View of What Will Be Submitted from Data Entered";
		if (stackTest.getSubmittedDate() != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			submittedDocDesc = "Printable View of Stack Test Data Submitted on "
					+ dateFormat.format(stackTest.getSubmittedDate());
		}
		// Set the path elements of the temp doc.
		doc.setDescription(submittedDocDesc
				+ (hideTradeSecret ? "" : " with trade secret data"));
		doc.setFacilityID(stackTest.getFacilityId());
		doc.setTemporary(true);
		doc.setStackTestId(stackTest.getId());
		doc.setOverloadFileName("StackTest"
				+ stackTest.getId() 
				+ (hideTradeSecret ? "" : "_TS") + ".pdf"); // Dennis just removed added facility id on front
		return;
	}
	
	private boolean reportHasAttestationDocument(
			StackTest stackTest) {
		boolean hasAttestationDocument = false;
		//for (StAttachment doc : stackTest.getAttachments()) {
		//	if (RO_SIGNATURE_DOC_TYPE.equals(doc.getDocTypeCd())) {
		//		hasAttestationDocument = true;
		//		break;
		//	}
		//}
		return hasAttestationDocument;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public StackTest createStackTestFromGateway(StackTest st,
			Facility facility, Transaction trans) throws RemoteException {
		// check to see if StackTest has already been created on the internal
		// system
		StackTest result = null;

		try {
			result = retrieveStackTest(st.getId(), true);
		} catch (DAOException e) {
			// "Invalid stack test id" error is thrown when id does not exist
			// in the
			// database. This is to be expected. Log a message if a different
			// error occurs.
			if (e.getMessage() == null
					|| !e.getMessage().startsWith("Invalid stack test id:")) {
				logger.error(
						"Exception while checking to see if gateway stack test ("
								+ st.getId() + ") exists", e);
			}
		}

		// only create stack test if it doesn't exist.
		if (result == null) {
			// make sure facility data is the same as the data submitted with
			// the "change facility inventory" task
			if (facility != null) {
				st.setAssociatedFacility(facility);
				// The updated fpId will be stored
				// when the record is updated.
				if (st.getAssociatedFacility() == null) {
					throw new DAOException("facilityBO.retrieveFacility("
							+ st.getFacilityId()
							+ ") failed to find the profile");
				}
				st.setFpId(st.getAssociatedFacility().getFpId());
			}

			// set received date to current time (this will also be the submit
			// date)
			st.setDateReceived(new Timestamp(System.currentTimeMillis()));
			st.setSubmittedDate(st.getDateReceived());
			st.setCreatedById(CommonConst.GATEWAY_USER_ID);
			st.setEmissionTestState(EmissionsTestStateDef.SUBMITTED);

			logger.debug("Creating stack test " + st.getStckId() + "from portal");
			// create the stack test
			result = createStackTest(facility, st, true);
			logger.debug("Successfully created stack test " + st.getStckId() + "from portal");
			
			// create the stack test attachments
			logger.debug("Creating stack test attachments for " + st.getStckId() + "from portal");
			for(StAttachment sta : result.getAttachments()) {
				logger.debug("Creating stack test attachment for " + sta.getDocumentID());
				createStackTestAttachmentFromPortal(result, sta, trans); 
				
			}
			logger.debug("Successfully created stack test attachments for " + st.getStckId() + "from portal");

			// create stacktest workflow
			logger.debug("Creating workflow for stack test " + st.getStckId());
			try {
				createWorkflow(result, null, result.getCreatedById(), new Timestamp(st
						.getSubmittedDate().getTime()));
			} catch (RemoteException re) {
				logger.error(
						"Unable to create workflow for submitted Stack Test "
								+ result.getStckId(), re);
				throw new DAOException(re.getLocalizedMessage(), re);
			} catch (ServiceFactoryException sfe) {
				logger.error(
						"Unable to create workflow for submitted Stack Test "
								+ result.getStckId(), sfe);
				throw new DAOException(sfe.getLocalizedMessage(), sfe);
			}
			logger.debug("Successfully created workflow for stack test " + st.getStckId());
		}

		return result;
	}
	
	public StAttachment createStackTestAttachmentFromPortal(
			StackTest stackTest, StAttachment attachment,
			Transaction trans) throws DAOException {
		StackTestDAO stackTestDAO = stackTestDAO(trans);
		try {
			attachment = (StAttachment) documentDAO(trans)
					.createDocument(attachment);
			
			stackTestDAO.createStAttachment(attachment);
			if (attachment.getTradeSecretDocId() != null) {
				StAttachment tsAttachment = new StAttachment();
				tsAttachment.setLastModifiedTS(attachment.getLastModifiedTS());
				tsAttachment.setUploadDate(tsAttachment.getLastModifiedTS());
				tsAttachment.setExtension(DocumentUtil
						.getFileExtension(attachment.getTradeSecretDocURL()));
				tsAttachment.setLastModifiedBy(CommonConst.GATEWAY_USER_ID);
				tsAttachment.setReportId(attachment.getReportId());
				// need object id to be set to put file in correct directory
				tsAttachment.setObjectId(String.valueOf(attachment
						.getReportId()));
				tsAttachment.setFacilityID(attachment.getFacilityID());
				tsAttachment.setSubPath("StackTest");
				tsAttachment.setTradeSecretDocId(attachment.getTradeSecretDocId());
				tsAttachment.setStackTestId(attachment.getStackTestId());
				tsAttachment.setTradeSecretJustification(attachment.getTradeSecretJustification());
				tsAttachment.setTradeSecretDocURL(attachment.getTradeSecretDocURL());
				tsAttachment.setDocumentID(attachment.getTradeSecretDocId());  // temporarily set document ID to the trade secret document ID in order to create the DC_DOCUMENT
				tsAttachment = (StAttachment) documentDAO(trans)
						.createDocument(tsAttachment);
				tsAttachment.setDocumentID(attachment.getDocumentID());        // now, set the document id back to the public document ID in order to create the CE_ST_TRADE_SECRET_ATTACHMENTS row
				stackTestDAO.createStTradeSecretAttachment(tsAttachment);
			}
		} catch (DAOException e) { // Throw it all away if we have an Exception
			throw e;
		}
		return attachment;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public StackTest createStagingStackTest(StackTest newStackTest,
			Integer fpId, Transaction trans) throws DAOException {
		logger.debug("Creating the staged stack test (createStagingStackTest)");
		FacilityDAO facDAO = facilityDAO(trans);
		Facility facility = facDAO.retrieveFacility(fpId);
		newStackTest.setAssociatedFacility(facility);
		newStackTest.setFpId(fpId);
		newStackTest = createStackTest(facility, newStackTest, false);
		DisplayUtil.clearQueuedMessages();
		return newStackTest;
	}
	
	/**
	 * Set the "validated" flag for <code>st</code> to <code>validated</code>.
	 * This method exists to avoid a full update of all stack test attributes
	 * when all that is needed is to set the validated flag.
	 * 
	 * @param st
	 *            the stack test
	 * @param validated
	 *            <code>true</code> or <code>false</code> to indicate whether
	 *            the stack test is validated.
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean setValidatedFlag(StackTest st, boolean validated, boolean readOnly)
			throws DAOException {
		Transaction trans = null;
		boolean ret = true;

		try {
			trans = TransactionFactory.createTransaction();
			//StackTestDAO stDAO = stackTestDAO(trans);
			StackTestDAO stackTestDAO = stackTestDAO(getSchema(readOnly ? CommonConst.READONLY_SCHEMA
					: CommonConst.STAGING_SCHEMA));
			st.setValidated(validated);
			stackTestDAO.setStackTestValidatedFlag(st.getId(),
					validated);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}
	

	public List<ValidationMessage> validateStackTest(int stacktestID, ValidationMessage vm,
			boolean useReadOnlyDB) throws RemoteException,
			ServiceFactoryException {
		// StackTest is valid unless an error is recorded
		boolean stIsValid = true;
		LinkedList<ValidationMessage> retVal = new LinkedList<ValidationMessage>();

		// Retrieve StackTest data from DB
		StackTest st = retrieveStackTest(stacktestID, useReadOnlyDB);
		Facility stFacility = ServiceFactory
				.getInstance()
				.getFacilityService()
				.retrieveFacilityProfile(st.getAssociatedFacility().getFpId(),
						true);
		st.setAssociatedFacility(stFacility);

		// get list of emission unit ids
		List<EmissionUnit> includedEUs = new ArrayList<EmissionUnit>();
		// List<Integer> euIds = new ArrayList<Integer>();
		for (TestedEmissionsUnit stEu : st.getTestedEmissionsUnits()) {
			// get facility inventory EU objects
			stEu.setEu(st.getAssociatedFacility().getEmissionUnit(
					stEu.getEu().getEmuId()));
			includedEUs.add(stEu.getEu());
		}

		
		// Perform stacktest validation
		validateStRequiredFields(st, vm, retVal);

		if (!st.isLegacyFlag()) {
			List<ValidationMessage> fpVal = null;
			
			// validate facility inventory
			fpVal = FacilityValidation.validateStackTest(stFacility, includedEUs);
			
			// add facility validation messages to validation message popup
			boolean hasFpErrors = false;
			for (ValidationMessage fpMsg : fpVal) {
				retVal.add(fpMsg);
				if (fpMsg.getSeverity()
						.equals(ValidationMessage.Severity.ERROR)) {
					hasFpErrors = true;
				}
			}
			if (hasFpErrors && isInternalApp()) {
				// add info message stating that app may need to be updated
				// to reflect changes to facility inventory.
				retVal.addFirst(new ValidationMessage(
						"stackDetail",
						"If the facility inventory has recently been updated, "
								+ "you may need to click the 'Associate with Current Facility Inventory' "
								+ "button to avoid reporting errors that have already been corrected.",
						ValidationMessage.Severity.INFO, null));
			}

			for (ValidationMessage message : retVal) {
				if (message.getSeverity().equals(
						ValidationMessage.Severity.ERROR)) {
					stIsValid = false;
					break;
				}
			}
		}

		// Save updated stacktest data to DB
		setValidatedFlag(st,
				stIsValid, useReadOnlyDB);

		return retVal;
	}
	
	private void validateStRequiredFields(StackTest st, ValidationMessage vm,
			List<ValidationMessage> messages) {
		ValidationMessage[] validResults = st.validate(vm);
		for (ValidationMessage validResult : validResults) {
			messages.add(new ValidationMessage(validResult.getProperty(),
					validResult.getMessage(), validResult.getSeverity(),
					null));
		}
		
		
	}

	@Override
	public StAttachment retrieveStTradeSecretAttachmentInfoById(Integer tradeSecretDocId) throws DAOException {
		return stackTestDAO().retrieveStTradeSecretAttachmentInfoById(tradeSecretDocId);
	}
}