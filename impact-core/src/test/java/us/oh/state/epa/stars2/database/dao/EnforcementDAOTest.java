package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Ignore;
import org.junit.Test;

public class EnforcementDAOTest {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	

//	public void testSetUpDirectories() {
//        EnforcementDAO enfDAO = (EnforcementDAO)DAOFactory.getDAO("EnforcementDAO");
//		try {
//			List<Enforcement> enfs = enfDAO.searchEnforcements(null, null, null, null, null, null, null, null, null, null, null);
//			for (Enforcement enf : enfs) {
//	            try {
//					String path = "/Facilities/" + enf.getFacilityId()
//			        + "/Enforcement/";
//					DocumentUtil.mkDir(path);
//					path = "/Facilities/" + enf.getFacilityId()
//			        + "/Enforcement/" + enf.getEnforcementId();
//					DocumentUtil.mkDir(path);
//		            System.out.println(path);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		} catch (DAOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	// TODO Enable after fixed against database and stars2 updates
	@Test
	@Ignore
	public void testEnforcement() {
//		CompEnfFacilityDAO cefDAO = (CompEnfFacilityDAO)DAOFactory.getDAO("CompEnfFacilityDAO");
//        EnforcementDAO enfDAO = (EnforcementDAO)DAOFactory.getDAO("EnforcementDAO");
//		try {
//			// delete information that may be in database from a previous test
//			
//			enfDAO.deleteEvaluatorsForEnforcementAction(1);
//			enfDAO.deleteEnforcementAction(1);
//			enfDAO.deleteViolationsForEnforcement(1);
//			enfDAO.deleteEnforcementMilestone(1);
//			enfDAO.deleteEnforcement(1);
//			cefDAO.deleteFacilityHistory(1);
//		} catch (DAOException e) {
//			fail();
//			e.printStackTrace();
//		}
//        
//		// initialize a FacilityHistory object (needed to create an EnforcementAction)
//		FacilityHistory fh = new FacilityHistory();
//		fh.setFacilityHistId(1);
//		fh.setAirProgramCompCd("Y");
//		fh.setMact(true);
//		fh.setMactCompCd("Y");
//		fh.setNeshaps(false);
//		fh.setNsps(false);
//		fh.setNsrNonAttainment(false);
//		fh.setPsd(false);
//		fh.setSipCompCd("Y");
//		fh.setStartDate(makeDateFromString("2011-02-12"));
//		
//		// initialize an Enforcement object
//		Enforcement enf = new Enforcement();
//		enf.setEnforcementId(1);
//		enf.setFpId(60350);
//		enf.setAddToHPVList(true);
//		enf.setEnfFormOfActionCd("F");
//		enf.addViolationTypeCode("GC8");
//		enf.setDayZeroDate(makeDateFromString("2009-06-12"));
//		enf.setDiscoveredDate(makeDateFromString("2009-06-12"));
//		enf.setLastModified(1);
//
//		// initialize an EnforcementAction
//        EnforcementAction action = new EnforcementAction();
//		action.setActionId(1);
//		action.setEnforcementId(1);
//		action.setFacilityHistId(1);
//		action.setActionDate(makeDateFromString("2009-07-29"));
//		action.setEnfActionTypeCd("L1");
//		action.setEnfViolationTypeCd("E");
//		action.addEvaluator(4280);
//		action.setFinalSepAmount("0.00");
//		action.setFinalCashAmount("0.00");
//		action.setLastModified(1);
//		
//		// initialize an enforcement milestone
//		EnforcementMilestone milestone = new EnforcementMilestone();
//		milestone.setMilestoneId(1);
//		milestone.setEnforcementId(1);
//		milestone.setOrderDate(makeDateFromString("2011-02-28"));
//		milestone.setCaseSettlementCd("foc");
//		milestone.setMilestoneOrRequirements("milestone or requirements");
//		milestone.setPaymentAmount("12345.67");
//		milestone.setDeadlineDate(makeDateFromString("2011-08-28"));
//		milestone.setCompletionDate(makeDateFromString("2011-09-28"));
//		milestone.setMemo("This is a test memo");
//		
//		try {
//			// create a FacilityHistory record in the database and ensure
//			// the data is the same once it's created
//			FacilityHistory checkFh = cefDAO.createFacilityHistory(fh);
//			assertNotNull(checkFh);
//			assertEquals(fh, checkFh);
//
//			// create an Enforcement record in the database and ensure
//			// the data is the same once it's created
//			Enforcement checkEnf = enfDAO.createEnforcement(enf);
//			assertNotNull(checkEnf);
//			assertEquals(enf, checkEnf);
//
//			// add violations
//			enfDAO.addViolationForEnforcement(1, "GC7");
//			enfDAO.addViolationForEnforcement(1, "GC8");
//			enfDAO.addViolationForEnforcement(1, "GC9");
//			
//			EnforcementMilestone checkMilestone = enfDAO.createEnforcementMilestone(milestone);
//			assertNotNull(checkMilestone);
//			assertEquals(milestone, checkMilestone);
//
//			// create an EnforcementAction record in the database and ensure
//			// the data is the same once it's created
//			EnforcementAction checkAction = enfDAO.createEnforcementAction(action);
//			assertNotNull(checkAction);
//			assertEquals(action, checkAction);
//			
//			// add evaluators
//			enfDAO.addEvaluatorForEnforcementAction(1, 1005);
//			enfDAO.addEvaluatorForEnforcementAction(1, 1009);
//			
//			// add an AFS sent date and Id to the action and modify its record in the database
//			action.setAfsSentDate(new Timestamp(System.currentTimeMillis()));
//			action.setAfsId("1");
//			
//			enfDAO.modifyEnforcementAction(action);
//			
//			// retrieve the enforcement record (this will also retrieve the modified action)
//			// and ensure the enforcement data has not changed
//			Enforcement enf2 = enfDAO.retrieveEnforcement(1);
//			assertEquals(enf, enf2);
//			
//			// retrieve the action from the Enforcement and make sure the modified fields are correct
//			EnforcementAction act = enf2.getLatestEnforcementAction();
//			assertEquals(act.getAfsSentDate(), action.getAfsSentDate());
//			assertEquals(act.getAfsId(), action.getAfsId());
//			
//			List<EnforcementMilestone> milestoneList = enfDAO.retrieveMilestonesForEnforcment(1);
//			assertEquals(milestoneList.size(), 1);
//			assertEquals(milestone, milestoneList.get(0));
//			
//			assert(act.getEvaluators().contains(1005));
//			assert(act.getEvaluators().contains(1009));
//			
//			// make sure violations added to the enforcement are still there
//			assert(enf2.getViolationTypeCodes().contains("GC7"));
//			assert(enf2.getViolationTypeCodes().contains("GC8"));
//			assert(enf2.getViolationTypeCodes().contains("GC9"));
//			
//			// test modification of enforcement
//			enf.setDayZeroDate(makeDateFromString("2011-03-01"));
//			enfDAO.modifyEnforcement(enf);
//			Enforcement enf3 = enfDAO.retrieveEnforcement(1);
//			assertEquals(enf3.getDayZeroDate(), makeDateFromString("2011-03-01"));
//			assertEquals(enf3.getLastModified().intValue(), 2);
//			
//			// test modification of milestone
//			milestone.setCaseSettlementCd("cto");
//			enfDAO.modifyEnforcementMilestone(milestone);
//			EnforcementMilestone em3 = enfDAO.retrieveEnforcementMilestone(1);
//			assertEquals(em3.getCaseSettlementCd(), "cto");
//			assertEquals(em3.getLastModified().intValue(), 2);
//			
//			// clean up
//			enfDAO.deleteEvaluatorsForEnforcementAction(1);
//			enfDAO.deleteEnforcementAction(1);
//			enfDAO.deleteViolationsForEnforcement(1);
//			enfDAO.deleteEnforcementMilestone(1);
//			enfDAO.deleteEnforcement(1);
//			cefDAO.deleteFacilityHistory(1);
//			
//		} catch (DAOException e) {
//			fail();
//			e.printStackTrace();
//		}
	}
	
	private final Timestamp makeDateFromString(String dateStr) {
		Timestamp ts = null;
		try {
			ts = new Timestamp(dateFormat.parse(dateStr).getTime());
		} catch (ParseException e) {
			System.err.println("Failed parsing date string " + dateStr);
		}
		return ts;
	}

}
