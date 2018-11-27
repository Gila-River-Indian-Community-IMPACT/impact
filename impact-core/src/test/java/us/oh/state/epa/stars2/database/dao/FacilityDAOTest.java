package us.oh.state.epa.stars2.database.dao;

import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public class FacilityDAOTest {

    public static void main(String[] args) {
    }

    protected void setUp() throws Exception {
        //super.setUp();
    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.modifyFacility(Facility)'
     */
    public void testModifyFacility() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveFacility(Integer)'
     */
    public void testRetrieveFacility() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveFacilities()'
     */
    public void testRetrieveFacilities() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.createEmissionUnit(EmissionUnit)'
     */
    public void testCreateEmissionUnit() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.modifyEmissionUnit(EmissionUnit)'
     */
    public void testModifyEmissionUnit() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveEmissionUnit(Integer,
     * Integer)'
     */
    public void testRetrieveEmissionUnit() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveFacilityEmissionUnits(Integer)'
     */
    public void testRetrieveFacilityEmissionUnits() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.createEmissionProcess(EmissionProcess)'
     */
    public void testCreateEmissionProcess() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.modifyEmissionProcess(EmissionProcess)'
     */
    public void testModifyEmissionProcess() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveEmissionProcess(Integer,
     * Integer)'
     */
    public void testRetrieveEmissionProcess() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveFacilityEmissionProcesses(Integer)'
     */
    public void testRetrieveFacilityEmissionProcesses() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.createControlEquipment(ControlEquipment)'
     */
    public void testCreateControlEquipment() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.modifyControlEquipment(ControlEquipment)'
     */
    public void testModifyControlEquipment() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveControlEquipment(Integer,
     * Integer)'
     */
    public void testRetrieveControlEquipment() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveFacilityControlEquipment(Integer)'
     */
    public void testRetrieveFacilityControlEquipment() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveEgressPoint(Integer,
     * Integer)'
     */
    public void testRetrieveEgressPoint() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveFacilityEgressPoints(Integer)'
     */
    public void testRetrieveFacilityEgressPoints() {

    }

    /*
     * Test method for
     * 'us.oh.state.epa.stars2.database.dao.FacilityDAO.retrieveFacilityTypes()'
     */
    public void testRetrieveFacilityTypes() {

    }

    /*
     * Test method for 'us.oh.state.epa.stars2.database.dao.FacilityDAO.searchFacilities(String, String, String)'
     */
    public void testSearchFacilities() {

    }
    
    // TODO: Must fix against database
    @Test
    @Ignore
    public void testSearchSubmissionLog() {
    	FacilityDAO facilityDAO = (FacilityDAO)DAOFactory.getDAO("FacilityDAO");
    	try {
    		SubmissionLog searchObj = new SubmissionLog();
    		searchObj.setFacilityId("0388010003");
    		SubmissionLog[] results = facilityDAO.searchSubmissionLog(searchObj, null, null);
    		assertTrue(results.length > 0);
    	} catch (DAOException e) {
    		assert(false);
    		System.err.println(e.getMessage());
    	}
    }
    
    // TODO: Must fix against database
    @Test
    @Ignore
    public void testCreateSubmissionLog() {
    	FacilityDAO facilityDAO = (FacilityDAO)DAOFactory.getDAO("FacilityDAO");
    	try {
    		SubmissionLog obj = new SubmissionLog();
    		Random random = new Random(System.currentTimeMillis());
    		int taskId = random.nextInt(10000);
    		int taskName = random.nextInt(10);
    		obj.setGatewaySubmissionId("PS:0388010003:" + taskId + ":" + taskName);
    		obj.setSubmissionDt(new Timestamp(System.currentTimeMillis()));
    		obj.setFacilityId("0388010003");
    		obj.setSubmissionType("Test");
    		obj.setGatewayUserName("bogus");
    		obj.setDocumentId(11);
    		obj.setNonROSubmission(true);
    		facilityDAO.createSubmissionLog(obj);
    	} catch (DAOException e) {
    		assert(false);
    		System.err.println(e.getMessage());
    	}
    }

}
