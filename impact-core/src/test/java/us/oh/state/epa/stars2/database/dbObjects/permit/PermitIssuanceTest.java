package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.*;

import org.junit.Test;



/**
 * Junit test suite for the {@link PermitIssuance} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitIssuanceTest {

    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
    public void testPermitIssuanceValid() {

        PermitIssuance issuance = new PermitIssuance();
        assertFalse(
                "PermitIssuanceTest.testValid() Empty PermitIssuance is valid.",
                issuance.isValid());

        issuance.setIssuanceTypeCd("Code Red");
        assertFalse(
                "PermitIssuanceTest.testValid() PermitIssuance valid after setIssuanceTypeCd().",
                issuance.isValid());

        issuance.setIssuanceStatusCd("Low Status");
        assertFalse(
                "PermitIssuanceTest.testValid() PermitIssuance valid after setIssuanceStatusCd().",
                issuance.isValid());

        issuance.setPermitId(123980);
        assertTrue(
                "PermitIssuanceTest.testValid() PermitIssuance is not valid after setPermitId().",
                issuance.isValid());
    }

    /**
     * Test the {@link PermitIssuance#equals(Object obj)} and
     * {@link PermitIssuance#hashCode()} methods.
     */
	@Test
    public void testPermitIssuanceEquals() {

        try {
        } catch (Exception e) {
            e.printStackTrace();
            fail("PermitIssuanceTest.testPermitIssuanceEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }

}
