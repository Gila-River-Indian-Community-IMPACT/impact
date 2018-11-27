package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.def.PermitReasonsDef;

/**
 * Junit test suite for the {@link Permit} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitTest {
	
	 // TODO: Fix against database
    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
    @Test
    @Ignore
    public void testPermitValid() {

        Permit permit = new Permit();
        assertFalse("PermitTest.testPermitValid() Empty Permit is valid.",
                permit.isValid());

        permit = new Permit();
        permit.setPermitType("BAD");
        assertFalse("PermitTest.testPermitValid() Bad permitType is valid.",
                permit.isValid());
        
        permit = new Permit();
        permit.setPermitType("PTIO");
        assertTrue("PermitTest.testPermitValid() Good permitType is invalid.",
                permit.isValid());

    }

    // TODO: Fix against database
    /**
     * Test the {@link Permit#equals(Object obj)} and {@link Permit#hashCode()}
     * methods.
     */
    @Test
    @Ignore
    public void testPermitEquals() {

        Permit permit1 = new Permit();
        Permit permit2 = new Permit();

        try {
            assertTrue(
                    "PermitTest.testPermitEquals() Self does not equal self.",
                    permit1.equals(permit1));

            permit1.setPermitNumber("12345");
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different permitNumber.", permit1
                    .equals(permit2));
            permit2.setPermitNumber(permit1.getPermitNumber());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same permitNumber.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setEracCaseNumber("23456");
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different eracCaseNumber.",
                    permit1.equals(permit2));
            permit2.setEracCaseNumber(permit1.getEracCaseNumber());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same eracCaseNumber.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setPermitGlobalStatusCD("Status Code");
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different permitGlobalStatusCD.",
                    permit1.equals(permit2));
            permit2.setPermitGlobalStatusCD(permit1.getPermitGlobalStatusCD());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same permitGlobalStatusCD.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setPermitType("A Permit Type");
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different permitType.", permit1
                    .equals(permit2));
            permit2.setPermitType(permit1.getPermitType());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same permitType.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setFacilityId("A Facility ID");
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different facilityId.", permit1
                    .equals(permit2));
            permit2.setFacilityId(permit1.getFacilityId());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same facilityId.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setFacilityNm("A Facility Name");
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different facilityNm.", permit1
                    .equals(permit2));
            permit2.setFacilityNm(permit1.getFacilityNm());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same facilityNm.",
                    permit1.hashCode(), permit2.hashCode());

            //permit1.setApplicationNumbers("Some Numbers");
            //assertFalse("PermitTest.testPermitEquals() "
            //        + "Self equals other with different applicationNumbers.",
            //        permit1.equals(permit2));
            //permit2.setApplicationNumbers(permit1.getApplicationNumbers());
            //assertEquals(
            //        "PermitTest.testPermitEquals() Hashcodes differ with same applicationNumbers.",
            //        permit1.hashCode(), permit2.hashCode());

            permit1.setOriginalPermitNo("Original Number");
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different originalPermitNo.",
                    permit1.equals(permit2));
            permit2.setOriginalPermitNo(permit1.getOriginalPermitNo());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same originalPermitNo.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setEffectiveDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different effectiveDate.",
                    permit1.equals(permit2));
            permit2.setEffectiveDate(permit1.getEffectiveDate());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same modEffectiveDate.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setModEffectiveDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different modEffectiveDate.",
                    permit1.equals(permit2));
            permit2.setModEffectiveDate(permit1.getModEffectiveDate());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same modEffectiveDate.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setExpirationDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different expirationDate.",
                    permit1.equals(permit2));
            permit2.setExpirationDate(permit1.getExpirationDate());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same expirationDate.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setPermitID(12345);
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different permitID.", permit1
                    .equals(permit2));
            permit2.setPermitID(permit1.getPermitID());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same permitID.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setFpId(1234);
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different fpId.", permit1
                    .equals(permit2));
            permit2.setFpId(permit1.getFpId());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same fpId.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setRushFlag(true);
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different rushFlag.", permit1
                    .equals(permit2));
            permit2.setRushFlag(permit1.isRushFlag());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same rushFlag.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setMact(true);
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different mact.", permit1
                    .equals(permit2));
            permit2.setMact(permit1.isMact());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same mact.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setNeshaps(true);
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different neshaps.", permit1
                    .equals(permit2));
            permit2.setNeshaps(permit1.isNeshaps());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same neshaps.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setNsps(true);
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different nsps.", permit1
                    .equals(permit2));
            permit2.setNsps(permit1.isNsps());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same nsps.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setIssueDraft(true);
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different issueDraft.", permit1
                    .equals(permit2));
            permit2.setIssueDraft(permit1.isIssueDraft());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same issueDraft.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setDapcHearingReqd(true);
            assertFalse("PermitTest.testPermitEquals() "
                    + "Self equals other with different dapcHearingReqd.",
                    permit1.equals(permit2));
            permit2.setDapcHearingReqd(permit1.isDapcHearingReqd());
            assertEquals(
                    "PermitTest.testPermitEquals() Hashcodes differ with same dapcHearingReqd.",
                    permit1.hashCode(), permit2.hashCode());

            assertTrue("PermitTest.testPermitEquals() "
                    + "Self not equal to other with all fields set the same.",
                    permit1.equals(permit2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("NoteTest.testPermitEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }

    // TODO: Fix against database
    @Test
    @Ignore
    public void testPrimaryReasonCD() {

        Permit permit1 = new Permit();
        String reason = permit1.getPrimaryReasonCD();
        assertTrue(
                "PermitTest.testPrimaryReasonCD() "
                        + "Initial primary reason is not PermitReasonsDef.NOT_ASSIGNED.",
                reason.equals(PermitReasonsDef.NOT_ASSIGNED));

        ArrayList<String> reasonCDs = new ArrayList<String>();
        reasonCDs.add(PermitReasonsDef.RENEWAL);
        permit1.setPermitReasonCDs(reasonCDs);
        reason = permit1.getPrimaryReasonCD();
        assertTrue("PermitTest.testPrimaryReasonCD() "
                + "new primary reason is not PermitReasonsDef.RENEWAL.", reason
                .equals(PermitReasonsDef.RENEWAL));

        reasonCDs.add(PermitReasonsDef.SPM);
        permit1.setPermitReasonCDs(reasonCDs);
        reason = permit1.getPrimaryReasonCD();
        assertTrue("PermitTest.testPrimaryReasonCD() "
                + "new primary reason is not PermitReasonsDef.RENEWAL.", reason
                .equals(PermitReasonsDef.RENEWAL));

    }



}
