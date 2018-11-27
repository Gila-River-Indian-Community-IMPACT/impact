package us.oh.state.epa.stars2.bo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

/**
 * Runs a junit test suite for the {@link CorrespondenceBO} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class CorrespondenceBOTest {

    // Make sure these are in the db.
    private static final String NODOCREQD = "38";

    private static final String FACILITYID = "0204010173";

    /**
     * Test the
     * {@link CorrespondenceBO#createCorrespondence(Correspondence correspondence)},
     * {@link CorrespondenceBO#searchCorrespondence(String facilityID)},
     * {@link CorrespondenceBO#updateCorrespondence(Correspondence correspondence)},
     * and
     * {@link CorrespondenceBO#deleteCorrespondence(Correspondence correspondence)}
     * methods.
     */
    @Test
    @Ignore
    public final void testCorrespondenceBO() {

        try {

            CorrespondenceService correspondenceBO 
                = ServiceFactory.getInstance().getCorrespondenceService();

            try {
                correspondenceBO.createCorrespondence(new Correspondence());
                fail("CorrespondenceBOTest.testCorrespondenceBO() Created an invalid Correspondence.");
            } catch (ValidationException ve) {
            }

            try {
                correspondenceBO.updateCorrespondence(new Correspondence());
                fail("CorrespondenceBOTest.testCorrespondenceBO() Updated an invalid Correspondence.");
            } catch (ValidationException ve) {
            }

            Correspondence correspondence = new Correspondence();
            correspondence.setFacilityID(FACILITYID);
            correspondence.setDateGenerated(new Timestamp(Calendar
                    .getInstance().getTimeInMillis()));
            correspondence.setCorrespondenceTypeCode(NODOCREQD);

            for (ValidationMessage msg : correspondence.validate()) {
                System.err.println("Correspondence not valid: "
                        + msg.getMessage());
            }

            Correspondence newCorr 
                = correspondenceBO.createCorrespondence(correspondence);
            assertNotNull("CorrespondenceBOTest.testCorrespondenceBO() newCorr is null.",
                          newCorr);
            assertNotNull("CorrespondenceBOTest.testCorrespondenceBO() newCorr has null ID.",
                          newCorr.getCorrespondenceID());

            boolean hasNewCorr = false;
            Correspondence[] retCorr 
                = correspondenceBO.searchCorrespondence(correspondence, null, null, null);
            if (retCorr.length == 0) {
                fail(" CorrespondenceBOTest.testCorrespondenceBO() No correspondence found.");
            }
            System.out.println("old id = " + newCorr.getCorrespondenceID() + ", ret.len = " + retCorr.length);
            for (Correspondence foundCorr : retCorr) {
                System.out.println("  new id = " + foundCorr.getCorrespondenceID());
                if (newCorr.equals(foundCorr)) {
                    hasNewCorr = true;
                    break;
                }
            }
            assertTrue("CorrespondenceBOTest.testCorrespondenceBO() Did not find newCorr.",
                       hasNewCorr);

            newCorr.setDocument(new Document());
            correspondenceBO.updateCorrespondence(newCorr);

            correspondenceBO.deleteCorrespondence(newCorr);
            hasNewCorr = false;
            for (Correspondence foundCorr 
                     : correspondenceBO.searchCorrespondence(correspondence, null, null, null)) {
                if (newCorr.equals(foundCorr)) {
                    hasNewCorr = true;
                    break;
                }
            }
            assertFalse(
                    "CorrespondenceBOTest.testCorrespondenceBO() Did not delete newCorr.",
                    hasNewCorr);

        } catch (Exception e) {
            e.printStackTrace();
            fail("CorrespondenceBOTest.testCorrespondenceBO() threw an unexpected exception"
                    + e.getMessage());
        }

    }

}
