package us.oh.state.epa.stars2.database.dbObjects.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Runs a junit test suite for the {@link Correspondence} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class CorrespondenceTest {

    // Make sure these are in the db.
    private static final String NODOCREQD = "38";

    private static final String DOCREQD = "1";

    // TODO Fix against database lookup
    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
    @Test
    @Ignore
    public void testCorrespondenceValid() {

        Correspondence correspondence = new Correspondence();
        assertFalse("CorrespondenceTest.testValid() Empty correspondence is valid.",
                    correspondence.isValid());

        correspondence.setFacilityID("00-11-22-3333");
        assertFalse("CorrespondenceTest.testValid() Valid after setFacilityID().",
                    correspondence.isValid());

        correspondence.setDateGenerated(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        assertFalse("CorrespondenceTest.testValid() Valid after setDateGenerated().",
                    correspondence.isValid());
        
        correspondence.setCorrespondenceTypeCode(NODOCREQD);
        assertTrue("CorrespondenceTest.testValid() Invalid after setCorrespondenceTypeCode(NODOCREQD).",
                   correspondence.isValid());

        correspondence.setCorrespondenceTypeCode(DOCREQD);
        assertFalse("CorrespondenceTest.testValid() Valid after setCorrespondenceTypeCode(DOCREQD).",
                    correspondence.isValid());

        correspondence.setDocument(new Document());
        assertTrue("CorrespondenceTest.testValid() Invalid after setDocument().",
                   correspondence.isValid());
    }

    // TODO Fix against database lookup
    /**
     * Test the {@link Correspondence#equals(Object obj)} and
     * {@link Correspondence#hashCode()} methods.
     */
    @Test
    @Ignore
    public void testCorrespondenceEquals() {

        Correspondence corr1 = new Correspondence();
        Correspondence corr2 = new Correspondence();

        try {

            assertTrue("CorrespondenceTest.testCorrespondenceEquals() Self does not equal self.",
                       corr1.equals(corr1));

            corr1.setDateGenerated(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            assertFalse("CorrespondenceTest.testCorrespondenceEquals() Self equals other with different timestamp.",
                        corr1.equals(corr2));
            corr2.setDateGenerated(corr1.getDateGenerated());
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() Self not equal to other with same timestamp.",
                       corr1.equals(corr2));
            assertEquals("CorrespondenceTest.testCorrespondenceEquals() Hashcodes differ with same timestamp.",
                         corr1.hashCode(), corr2.hashCode());

            corr1.setCorrespondenceID(new Integer(12345));
            assertFalse("CorrespondenceTest.testCorrespondenceEquals() "
                        + "Self equals other with different correspondenceID.",
                        corr1.equals(corr2));
            corr2.setCorrespondenceID(corr1.getCorrespondenceID());
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() "
                       + "Self not equal to other with same correspondenceID.",
                       corr1.equals(corr2));
            assertEquals("CorrespondenceTest.testCorrespondenceEquals() Hashcodes differ with same correspondenceID.",
                         corr1.hashCode(), corr2.hashCode());

            corr1.setRUMProcessID(new Integer(67890));
            assertFalse("CorrespondenceTest.testCorrespondenceEquals() Self equals other with different RUMProcessID.",
                        corr1.equals(corr2));
            corr2.setRUMProcessID(corr1.getRUMProcessID());
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() Self not equal to other with same RUMProcessID.",
                       corr1.equals(corr2));
            assertEquals("CorrespondenceTest.testCorrespondenceEquals() Hashcodes differ with same RUMProcessID.",
                         corr1.hashCode(), corr2.hashCode());

            corr1.setCorrespondenceTypeCode(NODOCREQD);
            assertFalse("CorrespondenceTest.testCorrespondenceEquals() "
                        + "Self equals other with different correspondenceTypeCode.",
                        corr1.equals(corr2));
            corr2.setCorrespondenceTypeCode(corr1.getCorrespondenceTypeCode());
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() "
                       + "Self not equal to other with same correspondenceTypeCode.",
                       corr1.equals(corr2));
            assertEquals("CorrespondenceTest.testCorrespondenceEquals() "
                         + "Hashcodes differ with same correspondenceTypeCode.",
                         corr1.hashCode(), corr2.hashCode());

            corr1.setFacilityID("00-11-22-3333");
            assertFalse("CorrespondenceTest.testCorrespondenceEquals() Self equals other with different facilityID.",
                        corr1.equals(corr2));
            corr2.setFacilityID(corr1.getFacilityID());
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() Self not equal to other with same facilityID.",
                       corr1.equals(corr2));
            assertEquals("CorrespondenceTest.testCorrespondenceEquals() Hashcodes differ with same facilityID.",
                         corr1.hashCode(), corr2.hashCode());

            corr1.setCertifiedMailTrackId("tracking id");
            assertFalse("CorrespondenceTest.testCorrespondenceEquals() "
                        + "Self equals other with different certifiedMailTrackId.",
                        corr1.equals(corr2));
            corr2.setCertifiedMailTrackId(corr1.getCertifiedMailTrackId());
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() "
                       + "Self not equal to other with same certifiedMailTrackId.",
                       corr1.equals(corr2));
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() "
                       + "Other not equal to self with same certifiedMailTrackId.",
                       corr2.equals(corr1));
            assertEquals("CorrespondenceTest.testCorrespondenceEquals() "
                         + "Hashcodes differ with same certifiedMailTrackId.",
                         corr1.hashCode(), corr2.hashCode());
            
            corr1.setCertifiedMailRcptDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            assertFalse("CorrespondenceTest.testCorrespondenceEquals() "
                        + "Self equals other with different certifiedMailRcptDate.",
                        corr1.equals(corr2));
            corr2.setCertifiedMailRcptDate(corr1.getCertifiedMailRcptDate());
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() "
                       + "Self not equal to other with same certifiedMailRcptDate.",
                       corr1.equals(corr2));
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() "
                       + "Other not equal to self with same certifiedMailRcptDate.",
                       corr2.equals(corr1));
            assertEquals("CorrespondenceTest.testCorrespondenceEquals() "
                         + "Hashcodes differ with same certifiedMailRcptDate.",
                         corr1.hashCode(), corr2.hashCode());

            corr2 = new Correspondence(corr1);
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() Self not equal to other after copy ctor.",
                       corr1.equals(corr2));
            assertTrue("CorrespondenceTest.testCorrespondenceEquals() Other not equal to self after copy ctor.",
                       corr2.equals(corr1));
            assertEquals("CorrespondenceTest.testCorrespondenceEquals() Hashcodes differ after copy ctor.",
                         corr1.hashCode(), corr2.hashCode());
            
        } 
        catch (Exception e) {
            e.printStackTrace();
            fail("CorrespondenceTest.testCorrespondenceEquals() threw an unexpected exception"
                 + e.getMessage());
        }

    }

}
