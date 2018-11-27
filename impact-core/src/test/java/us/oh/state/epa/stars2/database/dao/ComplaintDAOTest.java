package us.oh.state.epa.stars2.database.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dbObjects.ceta.Complaint;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public class ComplaintDAOTest  {

private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
// TODO Tests against database
	@Test
	@Ignore
	public void testComplaint() {
		ComplaintDAO complaintDAO = (ComplaintDAO)DAOFactory.getDAO("ComplaintDAO");
		try {
			// delete information that may be in database from a previous test
			complaintDAO.deleteComplaint(99);
			
		} catch (DAOException e) {
			fail();
			e.printStackTrace();
		}
					
			// initialize an complaint object
		Complaint complaint = new Complaint();				
		complaint.setComplaintId(99);
		complaint.setDoLaaCd("CDO");
		complaint.setYear("2011");
		complaint.setMonth("01");
		complaint.setHighPriority(99);
		complaint.setNonHighPriority(99);
		complaint.setOther(99);
		complaint.setOpenBurning(99);
		complaint.setAntiTamperingInspections(99);
		complaint.setLastModified(1);
			
			try{	
				
				// create an complaint record in the database and ensure
				// the data is the same once it's created
				Complaint checkComplaint = complaintDAO.createComplaint(complaint);
				assertNotNull(checkComplaint);
				assertEquals(complaint, checkComplaint);
				
				// retrieve a complaint record (this will also retrieve the modified action)
				// and ensure complaintF data has not changed
				Complaint complaint2 = complaintDAO.retrieveComplaint(99);
				assertEquals(complaint, complaint2);
			
				// modification complaint
					
				complaint.setYear("2012");
				complaint.setMonth("01");
				complaint.setHighPriority(100);
				complaint.setNonHighPriority(100);
				complaint.setOther(100);
				complaint.setOpenBurning(100);
				complaint.setAntiTamperingInspections(100);
				complaintDAO.modifyComplaint(complaint);
				Complaint complaint3 = complaintDAO.retrieveComplaint(99);
				assertEquals(complaint3.getYear(), "2012");
				assertEquals(complaint3.getMonth(), "01");
				assertEquals(complaint3.getHighPriority().intValue(), 100);
				assertEquals(complaint3.getNonHighPriority().intValue(), 100);
				assertEquals(complaint3.getOther().intValue(), 100);
				assertEquals(complaint3.getOpenBurning().intValue(), 100);
				assertEquals(complaint3.getAntiTamperingInspections().intValue(), 100);
				assertEquals(complaint3.getLastModified().intValue(), 2);
				
				
				
				//clean up
				complaintDAO.deleteComplaint(99);
			
		} catch (DAOException e) {
			fail();
			e.printStackTrace();
		}
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
