package us.oh.state.epa.stars2.bo;

import static org.junit.Assert.fail;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityHistList;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
public class FacilityBOTest {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	protected transient Logger logger = Logger.getLogger(this.getClass());

//	public void testUpdateFacilityContactFromGateway() {
//		String facilityId = "0857040583";
//		try {
//			FacilityService facilityBO = ServiceFactory.getInstance().getFacilityService();
//			Facility facility = facilityBO.retrieveFacility(facilityId);
//			List<Contact> contacts = facility.getAllContactsList();
//			assert(contacts.size() > 0);
//			facilityBO.updateFacilityContactFromGateWay(facilityId, facility.getCorePlaceId(), contacts);
//		} catch (ServiceFactoryException e) {
//			fail("Exception getting facility service");
//		} catch (RemoteException e) {
//			fail("Exception retrieving or processing facility");
//		}
//	}

	// TODO: Disabled because it tries to test against database 
	@Test
	@Ignore
	public void testSearchFacilitiesHist() {
		String facilityId = "0121010003";
		try {
			FacilityService facilityBO = ServiceFactory.getInstance().getFacilityService();
			Timestamp date = makeDateFromString("2011-08-06");
			FacilityHistList fhl = facilityBO.searchFacilitiesHist(facilityId, date);
			if (fhl != null) {
				System.out.println(fhl.getFacilityId() + ", " + fhl.getFpId() + ", " + 
						(fhl.getStartDate() == null ? "" : dateFormat.format(fhl.getStartDate())) + ", "
						+ (fhl.getEndDate() == null ? " " : dateFormat.format(fhl.getEndDate())));
			} else {
				System.out.println("No match found for facilityId = " + facilityId + 
						", date = " + dateFormat.format(date));
			}
		} catch (ServiceFactoryException e) {
			fail("Exception getting facility service");
		} catch (RemoteException e) {
			fail("Exception retrieving or processing facility");
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
	
//	public void testCreateFacilityProfileFromGateWay() {
//		String facilityId = "0302020027";
//		try {
//			FacilityService facilityBO = ServiceFactory.getInstance().getFacilityService();
//			Facility facility = facilityBO.retrieveFacility(facilityId);
//			logger.error("************* FROM DB ********************");
//			facility = facilityBO.retrieveFacilityProfile(facility.getFpId());
//			logger.error("******************************************");
//			Transaction trans = TransactionFactory.createTransaction();
//			logger.error("************* IN CREATE FACILITY INVENTORY FROM GATEWAY ********************");
//			facilityBO.createFacilityProfileFromGateWay(facility, facility.getFpId(), trans);
//			logger.error("******************************************");
//		} catch (ServiceFactoryException e) {
//			fail("Exception getting facility service");
//		} catch (RemoteException e) {
//			fail("Exception retrieving or processing facility");
//		}
//	}
}
