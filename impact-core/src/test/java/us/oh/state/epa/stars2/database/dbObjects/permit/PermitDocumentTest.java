package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Test;


/**
 * Junit test suite for the {@link PermitDocument} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitDocumentTest {

    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
    public void testPermitDocumentValid() {

        PermitDocument pd = new PermitDocument();
        assertFalse(
                "PermitDocumentTest.testPermitDocumentValid() Empty permitEUGroup is valid.",
                pd.isValid());

        pd.setLastModifiedBy(123);
        pd.setLastModifiedTS(new Timestamp(Calendar.getInstance()
                .getTimeInMillis()));
        pd
                .setUploadDate(new Timestamp(Calendar.getInstance()
                        .getTimeInMillis()));
        pd.setExtension(".pdf");
        pd.setFacilityID("A-Facility-ID");

        pd.setPermitDocTypeCD("A permit doc type code");
        assertFalse(
                "PermitDocumentTest.testPermitDocumentValid() Valid after setPermitDocTypeCD().",
                pd.isValid());

        pd.setPermitId(10101);
        assertTrue(
                "PermitDocumentTest.testPermitDocumentValid() Not valid after setPermitId().",
                pd.isValid());

    }

    /**
     * Test the {@link PermitDocument#equals(Object obj)} and
     * {@link PermitDocument#hashCode()} methods.
     */
	@Test
    public void testPermitDocumentEquals() {

        PermitDocument document1 = new PermitDocument();
        PermitDocument document2 = new PermitDocument();

        try {
            assertTrue(
                    "PermitDocumentTest.testPermitDocumentEquals() Self does not equal self.",
                    document1.equals(document1));

            document1.setPermitId(new Integer(12345));
            assertFalse("PermitDocumentTest.testPermitDocumentEquals() "
                    + "Self equals other with different permitId.", document1
                    .equals(document2));
            document2.setPermitId(document1.getPermitId());
            assertEquals(
                    "PermitDocumentTest.testPermitDocumentEquals() Hashcodes differ with same permitId.",
                    document1.hashCode(), document2.hashCode());

            document1.setPermitDocTypeCD("A mythical document code");
            assertFalse("PermitDocumentTest.testPermitDocumentEquals() "
                    + "Self equals other with different permitDocTypeCD.",
                    document1.equals(document2));
            document2.setPermitDocTypeCD(document1.getPermitDocTypeCD());

            document1.setIssuanceStageFlag("A mythical stage flag");
            assertFalse("PermitDocumentTest.testPermitDocumentEquals() "
                    + "Self equals other with different issuanceStageFlag.",
                    document1.equals(document2));
            document2.setIssuanceStageFlag(document1.getIssuanceStageFlag());
            assertTrue("PermitDocumentTest.testPermitDocumentEquals() "
                    + "Self not equal to other with same issuanceStageFlag.",
                    document1.equals(document2));
            assertEquals(
                    "PermitDocumentTest.testPermitDocumentEquals() Hashcodes differ with same issuanceStageFlag.",
                    document1.hashCode(), document2.hashCode());
        } catch (Exception e) {
            e.printStackTrace();
            fail("PermitDocumentTest.testPermitDocumentEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }

}
