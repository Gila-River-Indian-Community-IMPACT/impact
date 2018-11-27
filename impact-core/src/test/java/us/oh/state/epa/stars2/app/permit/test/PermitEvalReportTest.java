package us.oh.state.epa.stars2.app.permit.test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.app.permit.PermitEvalReport;

/**
 * Tests the {@link us.oh.state.epa.stars2.app.permit.PermitEvalReport} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitEvalReportTest {
    /** List of facility IDs which will be used to generate PERs. */
    ArrayList<String> facilities;
    String ID1 = "0124000115";
    String ID2 = "0125000827";
    String ID3 = "0125001402";
    String ID4 = "0125001972";
    String ID5 = "0125009999";
    String ID6 = "0204010173";
    String ID7 = "0388010133";
    String ID8 = "1409010001";

    /**
     * Test the
     * {@link us.oh.state.epa.stars2.app.permit.PermitEvalReport#generatePERs}
     * method.
     */
    @Ignore
    @Test
    public final void testGeneratePERs() {

        try {
            facilities = new ArrayList<String>();
            facilities.add(ID6);
            PermitEvalReport per = new PermitEvalReport();
            per.setFacilityIds(facilities);
            per.generatePERs();
            String lastErrors = per.getLastErrors();
            assertNull(
                    "PermitEvalReport.testGeneratePERs() Last errors were not null. "
                            + lastErrors, lastErrors);
        } catch (Exception e) {
            e.printStackTrace();
            fail("PermitEvalReport threw an unexpected exception"
                    + e.getMessage());
        }

    }

}
