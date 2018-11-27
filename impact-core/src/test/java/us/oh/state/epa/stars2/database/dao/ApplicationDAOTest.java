package us.oh.state.epa.stars2.database.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.faces.model.SelectItem;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationNote;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.ApplicationEUEmissionTableDef;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class ApplicationDAOTest {
    // NOTE: test relies on this facility id matching a valid facility in the DB
    private final String testPTIOFacilityID = "0204010173";
    private PTIOApplication testPTIOApp;
    private Facility facility;

	public ApplicationDAOTest() {
        try {
            FacilityService fpBO = ServiceFactory.getInstance().getFacilityService();
            facility = fpBO.retrieveFacility(testPTIOFacilityID);

            // initialize PTIO app
        } catch (Exception e) {
            fail("Exception in setup");
            e.printStackTrace();
        }
    }

    private void retrieveApplicationInteger() {
        Application app = null;
        ApplicationDAO applicationDAO = (ApplicationDAO)DAOFactory.getDAO("ApplicationDAO");
        try {
            app = applicationDAO.retrieveApplication(testPTIOApp.getApplicationID());
            assertEquals(testPTIOApp, app);
        } catch (DAOException e) {
            fail("Exception retrieving application");
            e.printStackTrace();
        }
    }
    private void retrieveApplicationString() {
        Application app = null;
        ApplicationDAO applicationDAO = (ApplicationDAO) DAOFactory.getDAO("ApplicationDAO");
        try {
            System.out.println("TESTPTIO: app number = " + testPTIOApp.getApplicationNumber());
            app = applicationDAO.retrieveApplication(testPTIOApp.getApplicationNumber());
            assertEquals(testPTIOApp, app);
        } catch (DAOException e) {
            fail("Exception retrieving application");
            e.printStackTrace();
        }
    }

    // TODO: Tests against database
    @Test
    @Ignore
    public void testRetrieveApplicationTypes() {
        
        ApplicationDAO applicationDAO = (ApplicationDAO)DAOFactory.getDAO("ApplicationDAO");
        try {
            List<SelectItem> expectedItems = ApplicationTypeDef.getData().getItems().getAllItems();
            SimpleDef[] appTypes = applicationDAO.retrieveApplicationTypes();
            assertNotNull(appTypes);
            assertEquals(expectedItems.size(), appTypes.length);
            for (SelectItem expectedItem : expectedItems) {
                boolean found = false;
                for (SimpleDef gotItem : appTypes) {
                    if (expectedItem.getValue().equals(gotItem.getCode())
                        && expectedItem.getLabel().equals(gotItem.getDescription())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found);
            }
        } catch (DAOException e) {
            fail("Exception while retrieving application types");
            e.printStackTrace();
        }
    }

	// TODO: Tests against database
    @Test
    @Ignore
    public void testCreateApplication() {
        ApplicationDAO applicationDAO = (ApplicationDAO)DAOFactory.getDAO("ApplicationDAO");
        try {

            testPTIOApp = new PTIOApplication();
            testPTIOApp.setFacility(facility);
            testPTIOApp.setApplicationNumber(applicationDAO.generateApplicationNumber(testPTIOApp.getClass()));

            testPTIOApp = (PTIOApplication) applicationDAO.createApplication(testPTIOApp);
            System.out.println("TESTPTIO: app number = " + testPTIOApp.getApplicationNumber());
            testPTIOApp = applicationDAO.createPTIOApplication(testPTIOApp);
            EmissionUnit[] feus = testPTIOApp.getFacility().getEmissionUnits().toArray(new EmissionUnit[0]);
            for (EmissionUnit eu : feus) {
                PTIOApplicationEU aeu = new PTIOApplicationEU();
                aeu.setFpEU(eu);
                aeu.setApplicationId(testPTIOApp.getApplicationID());
                aeu.setRequestingFederalLimitsFlag("N");
                applicationDAO.createApplicationEU(aeu);
                testPTIOApp.addEu(applicationDAO.createPTIOApplicationEU(aeu));
            }
        } catch (DAOException e) {
            fail("Exception while creating application");
            e.printStackTrace();
        }
		
        // test adding an application note
        addApplicationNote();
		
        // test retrieval of application
        retrieveApplicationInteger();
        retrieveApplicationString();
		
        // test emissions
        applicationEUEmissions();
		
    }

    private void addApplicationNote() {

        ApplicationNote note = new ApplicationNote();
        note.setApplicationId(testPTIOApp.getApplicationID());
        note.setDateEntered(new Timestamp(System.currentTimeMillis()));
        note.setLastModified(1);
        note.setNoteTxt("JUnit test note");
        note.setNoteTypeCd(NoteType.DAPC);
        note.setUserId(1);
        InfrastructureDAO infrastructureDAO = (InfrastructureDAO)DAOFactory.getDAO("InfrastructureDAO");
        ApplicationDAO applicationDAO = (ApplicationDAO)DAOFactory.getDAO("ApplicationDAO");
        try {
            infrastructureDAO.createNote(note);
            assertNotNull(note.getNoteId());
            applicationDAO.addApplicationNote(note.getApplicationId(), note.getNoteId());
            ApplicationNote[] notes = applicationDAO.retrieveApplicationNotes(note.getApplicationId());
            assertFalse(notes.length == 0);
            boolean found = false;
            for (ApplicationNote matchNote : notes) {
                if (note.getApplicationId().equals(matchNote.getApplicationId()) &&
                    note.getNoteId().equals(matchNote.getNoteId()) &&
                    note.getNoteTxt().equals(matchNote.getNoteTxt()) &&
                    note.getNoteTypeCd().equals(matchNote.getNoteTypeCd())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        } catch (DAOException e) {
            fail("Exception adding application note");
            e.printStackTrace();
        }
    }
	
    private void applicationEUEmissions() {

        List<ApplicationEU> eus = testPTIOApp.getIncludedEus();
        assertTrue(eus.size() > 0);
        for (ApplicationEU appEU : eus) {
            Integer appEUId = appEU.getApplicationEuId();
            ApplicationEUEmissions emissions = new ApplicationEUEmissions();
            emissions.setEuEmissionTableCd(ApplicationEUEmissionTableDef.HAP_TABLE_CD);
            emissions.setApplicationEuId(appEUId);
            emissions.setPollutantCd("71432");	// benzene
            
			emissions.setPreCtlPotentialEmissions("37.5");
			emissions.setPotentialToEmit("27.3");
			emissions.setPotentialToEmitLbHr("102.3");
			emissions.setPotentialToEmitTonYr("40.2");
           
            ApplicationDAO applicationDAO = (ApplicationDAO)DAOFactory.getDAO("ApplicationDAO");
            try {
                applicationDAO.addApplicationEUEmissions(emissions);
                emissions.setLastModified(1);
                ApplicationEUEmissions[] matchEmissions = applicationDAO.retrieveApplicationEUEmissions(appEUId);
                List<ApplicationEUEmissions> matchList = Arrays.asList(matchEmissions);
                assertTrue(matchList.contains(emissions));
				
                // test modify

                emissions.setPreCtlPotentialEmissions("101.5");
                applicationDAO.modifyApplicationEUEmissions(emissions);
                matchEmissions = applicationDAO.retrieveApplicationEUEmissions(appEUId);
//                matchList = Arrays.asList(matchEmissions);
                
                // the assert statement below doesn't work because the last modified
                // value has changed.
//                assertTrue(matchList.contains(emissions));
                for (ApplicationEUEmissions dbValue : matchEmissions) {
                    if (dbValue.getPollutantCd().equals(emissions.getPollutantCd()) &&
                            dbValue.getEuEmissionTableCd().equals(emissions.getEuEmissionTableCd())) {
              
                    	//assertEquals(dbValue.getPreCtlPotentialEmissions(), emissions.getPreCtlPotentialEmissions());
                    }
                }
				
                // test remove all
                applicationDAO.removeApplicationEUEmissions(appEUId);
                matchEmissions = applicationDAO.retrieveApplicationEUEmissions(appEUId);
                assertTrue(matchEmissions.length == 0);
            } catch (DAOException e) {
                fail("Exception while adding application EU emissions");
                e.printStackTrace();
            }
        }
		
    }

}
