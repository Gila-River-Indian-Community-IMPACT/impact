package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


/**
 * Junit test suite for the {@link PermitEUGroup} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitEUGroupTest {

    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
    public void testPermitEUGroupValid() {

        PermitEUGroup peug = new PermitEUGroup();
        assertFalse(
                "PermitEUGroupTest.testPermitEUGroupValid() Empty permitEUGroup is valid.",
                peug.isValid());
        peug.addPermitEU(new PermitEU());
        assertFalse(
                "PermitEUGroupTest.testPermitEUGroupValid() permitEUGroup is valid with no PermitID.",
                peug.isValid());
        peug.setPermitID(10101);
        assertTrue(
                "PermitEUGroupTest.testPermitEUGroupValid() Not valid after setPermitID().",
                peug.isValid());

    }

    /**
     * Test the {@link PermitEUGroup#equals(Object obj)} and
     * {@link PermitEUGroup#hashCode()} methods.
     */
	@Test
    public void testPermitEUGroupEquals() {

        PermitEUGroup group1 = new PermitEUGroup();
        PermitEUGroup group2 = new PermitEUGroup();

        try {
            assertTrue(
                    "PermitEUGroupTest.testPermitEUGroupEquals() Self does not equal self.",
                    group1.equals(group1));

            group1.setPermitEUGroupID(new Integer(12345));
            assertFalse("PermitEUGroupTest.testPermitEUGroupEquals() "
                    + "Self equals other with different permitEUGroupId.",
                    group1.equals(group2));
            group2.setPermitEUGroupID(group1.getPermitEUGroupID());
            assertEquals(
                    "PermitEUGroupTest.testPermitEUGroupEquals() Hashcodes differ with same permitEUGroupID.",
                    group1.hashCode(), group2.hashCode());

            group1.setPermitID(new Integer(12345));
            assertFalse("PermitEUGroupTest.testPermitEUGroupEquals() "
                    + "Self equals other with different permitId.", group1
                    .equals(group2));
            group2.setPermitID(group1.getPermitID());
            assertEquals(
                    "PermitEUGroupTest.testPermitEUGroupEquals() Hashcodes differ with same permitID.",
                    group1.hashCode(), group2.hashCode());

            group1.setName("A mythical EUGroup");
            assertFalse("PermitEUGroupTest.testPermitEUGroupEquals() "
                    + "Self equals other with different name.", group1
                    .equals(group2));
            group2.setName(group1.getName());
            assertTrue("PermitEUGroupTest.testPermitEUGroupEquals() "
                    + "Self not equal to other with same name.", group1
                    .equals(group2));
            assertEquals(
                    "PermitEUGroupTest.testPermitEUGroupEquals() Hashcodes differ with same name.",
                    group1.hashCode(), group2.hashCode());
        } catch (Exception e) {
            e.printStackTrace();
            fail("PermitEUGroupTest.testPermitEUGroupEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }

}
