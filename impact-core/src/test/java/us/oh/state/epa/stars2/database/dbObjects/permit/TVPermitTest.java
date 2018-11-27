package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;


/**
 * Junit test suite for the {@link TVPermit} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class TVPermitTest {

    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
	@Ignore
    public void testTVPermitValid() {

        TVPermit permit = new TVPermit();
        assertFalse(
                "TVPermitTest.testTVPermitValid() Empty TVPermit is valid.",
                permit.isValid());

        permit.setPermitType("TVPTO");
        assertFalse(
                "TVPermitTest.testTVPermitValid() TVPermit with no usepaOutcomeCd is valid.",
                permit.isValid());

        permit.setUsepaOutcomeCd("BAD");
        assertFalse(
                "TVPermitTest.testTVPermitValid() TVPermit with bad usepaOutcomeCd is valid.",
                permit.isValid());

        permit.setUsepaOutcomeCd("R");
        assertTrue(
                "TVPermitTest.testTVPermitValid() TVPermit with good usepaOutcomeCd is invalid.",
                permit.isValid());

    }

    /**
     * Test the {@link TVPermit#equals(Object obj)} and
     * {@link TVPermit#hashCode()} methods.
     */
	@Test
	@Ignore
    public void testTVPermitEquals() {

        TVPermit permit1 = new TVPermit();
        TVPermit permit2 = new TVPermit();

        try {

            assertTrue(
                    "TVPermitTest.testTVPermitEquals() Self does not equal self.",
                    permit1.equals(permit1));

            permit1.setPermitNumber("12345");
            permit2.setPermitNumber(permit1.getPermitNumber());
            permit1.setEracCaseNumber("23456");
            permit2.setEracCaseNumber(permit1.getEracCaseNumber());
            permit1.setPermitGlobalStatusCD("Status Code");
            permit2.setPermitGlobalStatusCD(permit1.getPermitGlobalStatusCD());
            permit1.setPermitType("A Permit Type");
            permit2.setPermitType(permit1.getPermitType());
            permit1.setFacilityId("A Facility ID");
            permit2.setFacilityId(permit1.getFacilityId());
            permit1.setFacilityNm("A Facility Name");
            permit2.setFacilityNm(permit1.getFacilityNm());
            //permit1.setApplicationNumbers("Some Numbers");
            //permit2.setApplicationNumbers(permit1.getApplicationNumbers());
            permit1.setOriginalPermitNo("Original Number");
            permit2.setOriginalPermitNo(permit1.getOriginalPermitNo());
            permit1.setEffectiveDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            permit2.setEffectiveDate(permit1.getEffectiveDate());
            permit1.setModEffectiveDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            permit2.setModEffectiveDate(permit1.getModEffectiveDate());
            permit1.setExpirationDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            permit2.setExpirationDate(permit1.getExpirationDate());
            permit1.setPermitID(12345);
            permit2.setPermitID(permit1.getPermitID());
            permit1.setFpId(1234);
            permit2.setFpId(permit1.getFpId());
            permit1.setRushFlag(true);
            permit2.setRushFlag(permit1.isRushFlag());
            //permit1.setPppReviewWaived(true);

            //assertFalse("TVPermitTest.testTVPermitEquals() "
            //        + "Self equals other with different pppReviewWaived.",
            //        permit1.equals(permit2));
            //permit2.setPppReviewWaived(permit1.isPppReviewWaived());
            //assertEquals(
            //        "TVPermitTest.testTVPermitEquals() Hashcodes differ with same pppReviewWaived.",
            //        permit1.hashCode(), permit2.hashCode());

            permit1.setUsepaExpedited(true);
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different usepaExpedited.",
                    permit1.equals(permit2));
            permit2.setUsepaExpedited(permit1.isUsepaExpedited());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same usepaExpedited.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setUsepaOutcomeCd("R");
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different usepaOutcomeCd.",
                    permit1.equals(permit2));
            permit2.setUsepaOutcomeCd(permit1.getUsepaOutcomeCd());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same usepaOutcomeCd.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setUsepaCompleteDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different usepaCompleteDate.",
                    permit1.equals(permit2));
            permit2.setUsepaCompleteDate(permit1.getUsepaCompleteDate());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same usepaCompleteDate.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setAcidRain(true);
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different acidRain.", permit1
                    .equals(permit2));
            permit2.setAcidRain(permit1.isAcidRain());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same acidRain.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setAcidDesc("Purple Haze");
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different acidDesc.", permit1
                    .equals(permit2));
            permit2.setAcidDesc(permit1.getAcidDesc());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same acidDesc.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setCam(true);
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different cam.", permit1
                    .equals(permit2));
            permit2.setCam(permit1.isCam());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same cam.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setCamDesc("Climb Higher!");
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different camDesc.", permit1
                    .equals(permit2));
            permit2.setCamDesc(permit1.getCamDesc());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same camDesc.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setSec112(true);
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different sec112.", permit1
                    .equals(permit2));
            permit2.setSec112(permit1.isSec112());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same sec112.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setSec112Desc("Climb Higher!");
            assertFalse("TVPermitTest.testTVPermitEquals() "
                    + "Self equals other with different sec112Desc.", permit1
                    .equals(permit2));
            permit2.setSec112Desc(permit1.getSec112Desc());
            assertEquals(
                    "TVPermitTest.testTVPermitEquals() Hashcodes differ with same sec112Desc.",
                    permit1.hashCode(), permit2.hashCode());

            assertTrue("TVPermitTest.testTVPermitEquals() "
                    + "Self does not equal other with identical fields.",
                    permit1.equals(permit2));

        } catch (Exception e) {
            e.printStackTrace();
            fail("NoteTest.testTVPermitEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }

}
