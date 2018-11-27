package us.oh.state.epa.stars2.bo;

import java.rmi.RemoteException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitInfo;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

/**
 * Runs a junit test suite for the {@link PermitBO} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitBOTest {

    /**
     * 
     */
//    public final void testPrepareMultiMediaLetter() {
//
//        try {
//
//            @SuppressWarnings("unused")
//            PermitService permitBO = ServiceFactory.getInstance().getPermitService();
//
//            @SuppressWarnings("unused")
//            int permitId = 502;
//            @SuppressWarnings("unused")
//            int userId = 1;
//            @SuppressWarnings("unused")
//            String issuanceType = PermitIssuanceTypeDef.Draft;
//
//            //permitBO.prepareMultiMediaLetter(permitId, userId, issuanceType);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            fail("testPrepareMultiMediaLetter failed: " + e.getMessage());
//        }
//
//    }
	
	// TODO: Fix - tests against database
	@Test
	@Ignore
    public final void testSearchPermits() {
        PermitService permitBO;
		try {
			permitBO = ServiceFactory.getInstance().getPermitService();
	        List<Permit> permits = permitBO.search(null, null, null, "PTIO",
						"R", null, null, null, null, null, null, null, null, null, null, true, null);
	        int i=0;
	        for (Permit permit : permits) {
				if (!"N".equals(permit.getPermitGlobalStatusCD()) && !"D".equals(permit.getPermitGlobalStatusCD())) {
					continue;
				}
	        	PermitInfo pi = permitBO.retrievePermit(permit.getPermitID());
	        	Permit tmp = pi.getPermit();
	        	boolean allEUsSD = (tmp.getEus().size() > 0);
//	        	if (!allEUsSD) {
//	        		System.out.println("No EUs for permit " + tmp.getPermitNumber());
//	        	}
	        	for (PermitEU eu : tmp.getEus()) {
	        		if (!"sd".equals(eu.getFpEU().getOperatingStatusCd())) {
//	        			System.out.println("Permit " + tmp.getPermitNumber() 
//	        					+ ", EU " + eu.getFpEU().getEpaEmuId() + " - " + eu.getFpEU().getOperatingStatusCd());
	        			allEUsSD = false;
	        			break;
	        		}
	        	}
	        	if (allEUsSD) {
	        		System.out.println(permit.getFacilityId() + ", " + permit.getPermitNumber());
	        	}
//	        	if (++i %100 == 0) {
//	        		System.out.println(i);
//	        	}
	        }
	        System.out.println("Done");
		} catch (ServiceFactoryException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }

    /**
     * This method is called before a test is executed and is used to setup any
     * needed preconditions for the test.
     */
    protected void setUp() {
    }

    /**
     * This method is called after a test is executed and is used to cleanup
     * after the test.
     */
    protected void tearDown() {
    }


}
