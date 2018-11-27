package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;


/**
 * Junit test suite for the {@link PTIOPermit} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PTIOPermitTest {

	// TODO: Fix against database
    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
	@Ignore
    public void testPTIOPermitValid() {

        PTIOPermit permit = new PTIOPermit();
        assertFalse("PTIOPermitTest.testPTIOPermitValid() Empty PTIOPermit is valid.",
                   permit.isValid());
        
        permit.setPerDueDateCD("0");
        assertTrue("PTIOPermitTest.testPTIOPermitValid() PER Due Date is invalid.",
                   permit.isValid());

        permit.setConvertedToPTI(true);
        assertFalse("PTIOPermitTest.testPTIOPermitValid() True convertedToPTI is valid.",
                    permit.isValid());

        permit.setConvertedToPTIDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        assertTrue("PTIOPermitTest.testPTIOPermitValid() PTIOPermit with good convertedToPTIODate is invalid.",
                   permit.isValid());

    }

    //TODO: Fix against database
    /**
     * Test the {@link PTIOPermit#equals(Object obj)} and
     * {@link PTIOPermit#hashCode()} methods.
     */
    @Test
    @Ignore
    public void testPTIOPermitEquals() {

        PTIOPermit permit1 = new PTIOPermit();
        PTIOPermit permit2 = new PTIOPermit();

        try {

            assertTrue(
                    "PTIOPermitTest.testPTIOPermitEquals() Self does not equal self.",
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

            permit1.setTv(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different Tv.", permit1
                    .equals(permit2));
            permit2.setTv(permit1.isTv());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same Tv.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setConvertedToPTI(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different convertedToPTI.",
                    permit1.equals(permit2));
            permit2.setConvertedToPTI(permit1.isConvertedToPTI());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same convertedToPTI.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setConvertedToPTIDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different convertedToPTIDate.",
                    permit1.equals(permit2));
            permit2.setConvertedToPTIDate(permit1.getConvertedToPTIDate());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same convertedToPTIDate.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setPerDueDateCD("0");
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different perDueDateCD.", permit1
                    .equals(permit2));
            permit2.setPerDueDateCD(permit1.getPerDueDateCD());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same perDueDateCD.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setNetting(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different netting.", permit1
                    .equals(permit2));
            permit2.setNetting(permit1.isNetting());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same netting.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setEmissionsOffsets(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different emissionsOffsets.",
                    permit1.equals(permit2));
            permit2.setEmissionsOffsets(permit1.isEmissionsOffsets());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same emissionsOffsets.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setSmtv(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different smtv.", permit1
                    .equals(permit2));
            permit2.setSmtv(permit1.isSmtv());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same smtv.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setCem(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different cem.", permit1
                    .equals(permit2));
            permit2.setCem(permit1.isCem());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same cem.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setModelingSubmitted(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different modelingSubmitted.",
                    permit1.equals(permit2));
            permit2.setModelingSubmitted(permit1.isModelingSubmitted());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same modelingSubmitted.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setPsd(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different psd.", permit1
                    .equals(permit2));
            permit2.setPsd(permit1.isPsd());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same psd.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setToxicReview(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different toxicReview.", permit1
                    .equals(permit2));
            permit2.setToxicReview(permit1.isToxicReview());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same toxicReview.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setFeptio(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different feptio.", permit1
                    .equals(permit2));
            permit2.setFeptio(permit1.isFeptio());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same feptio.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setFullCostRecovery(true);
            assertFalse("PTIOPermitTest.testPermitEquals() "
                    + "Self equals other with different fullCostRecovery.",
                    permit1.equals(permit2));
            permit2.setFullCostRecovery(permit1.isFullCostRecovery());
            assertEquals(
                    "PTIOPermitTest.testPermitEquals() Hashcodes differ with same fullCostrecovery.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setExpress(true);
            assertFalse("PTIOPermitTest.testPermitEquals() "
                    + "Self equals other with different express.", permit1
                    .equals(permit2));
            permit2.setExpress(permit1.isExpress());
            assertEquals(
                    "PTIOPermitTest.testPermitEquals() Hashcodes differ with same express.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setOtherAdjustment(new Double(1.98));
            assertFalse("PTIOPermitTest.testPermitEquals() "
                    + "Self equals other with different otherAdjustment.",
                    permit1.equals(permit2));
            permit2.setOtherAdjustment(permit1.getOtherAdjustment());
            assertEquals(
                    "PTIOPermitTest.testPermitEquals() Hashcodes differ with same otherAdjustment.",
                    permit1.hashCode(), permit2.hashCode());
            
            permit1.setInitialInvoice(new Double(2.98));
            assertFalse("PTIOPermitTest.testPermitEquals() "
                    + "Self equals other with different initialInvoice.",
                    permit1.equals(permit2));
            permit2.setInitialInvoice(permit1.getInitialInvoice());
            assertEquals(
                    "PTIOPermitTest.testPermitEquals() Hashcodes differ with same initialInvoice.",
                    permit1.hashCode(), permit2.hashCode());
            
            permit1.setFinalInvoice(new Double(3.98));
            assertFalse("PTIOPermitTest.testPermitEquals() "
                    + "Self equals other with different finalInvoice.",
                    permit1.equals(permit2));
            permit2.setFinalInvoice(permit1.getFinalInvoice());
            assertEquals(
                    "PTIOPermitTest.testPermitEquals() Hashcodes differ with same finalInvoice.",
                    permit1.hashCode(), permit2.hashCode());

            permit1.setGeneralPermit(true);
            assertFalse("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self equals other with different generalPermit.",
                    permit1.equals(permit2));
            permit2.setGeneralPermit(permit1.isGeneralPermit());
            assertEquals(
                    "PTIOPermitTest.testPTIOPermitEquals() Hashcodes differ with same generalPermit.",
                    permit1.hashCode(), permit2.hashCode());

            assertTrue("PTIOPermitTest.testPTIOPermitEquals() "
                    + "Self does not equal other with same rpeNumber.", permit1
                    .equals(permit2));

        } catch (Exception e) {
            e.printStackTrace();
            fail("PTIOPermitTest.testPTIOPermitEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }

}
