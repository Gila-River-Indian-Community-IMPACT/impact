package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;

/**
 * Junit test suite for the {@link EUFee} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class EUFeeTest {

    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
    public void testEUFeeValid() {

        EUFee euf = new EUFee();
        assertFalse("EUFeeTest.testEUFeeValid() Empty EUFee is valid.", euf
                .isValid());

        euf.setAdjustmentCd("NF");
        assertFalse(
                "EUFeeTest.testEUFeeValid() Valid after setAdjustmentCd().",
                euf.isValid());

        euf.setFeeCategoryId(1234);
        assertTrue(
                "EUFeeTest.testEUFeeValid() Valid after setFeeCatagoryId().",
                euf.isValid());

    }

    /**
     * Test the {@link EUFee#equals(Object obj)} and {@link EUFee#hashCode()}
     * methods.
     */
	@Test
    public void testEUFeeEquals() {

        EUFee fee1 = new EUFee();
        EUFee fee2 = new EUFee();

        try {
            assertTrue("EUFeeTest.testEUFeeEquals() Self does not equal self.",
                    fee1.equals(fee1));

            fee1.setEUFeeId(new Integer(12345));
            assertFalse("EUFeeTest.testEUFeeEquals() "
                    + "Self equals other with different EUFeeId.", fee1
                    .equals(fee2));
            fee2.setEUFeeId(fee1.getEUFeeId());
            assertEquals(
                    "EUFeeTest.testEUFeeEquals() Hashcodes differ with same EUFeeID.",
                    fee1.hashCode(), fee2.hashCode());

            fee1.getFee().setFeeId(12345);
            assertFalse("EUFeeTest.testEUFeeEquals() "
                    + "Self equals other with different feeId.", fee1
                    .equals(fee2));
            // Force setDirty.
            Fee tmpFee = fee1.getFee();
            fee1.setFee(tmpFee);
            fee2.setFee(fee1.getFee());
            assertEquals(
                    "EUFeeTest.testEUFeeEquals() Hashcodes differ with same feeID.",
                    fee1.hashCode(), fee2.hashCode());

            fee1.setFeeCategoryId(123);
            assertFalse("EUFeeTest.testEUFeeEquals() "
                    + "Self equals other with different feeCategoryId.", fee1
                    .equals(fee2));
            fee2.setFeeCategoryId(fee1.getFeeCategoryId());
            assertEquals(
                    "EUFeeTest.testEUFeeEquals() Hashcodes differ with same feeCategoryId.",
                    fee1.hashCode(), fee2.hashCode());

            fee1.setAdjustedAmount(new Double(123.45));
            assertFalse("EUFeeTest.testEUFeeEquals() "
                    + "Self equals other with different adjustedAmount.", fee1
                    .equals(fee2));
            fee2.setAdjustedAmount(fee1.getAdjustedAmount());
            assertEquals(
                    "EUFeeTest.testEUFeeEquals() Hashcodes differ with same adjustedAmount.",
                    fee1.hashCode(), fee2.hashCode());

            fee1.setAdjustmentCd("D");
            assertFalse("EUFeeTest.testEUFeeEquals() "
                    + "Self equals other with different adjustmentCd.", fee1
                    .equals(fee2));
            fee2.setAdjustmentCd(fee1.getAdjustmentCd());
            assertTrue("EUFeeTest.testEUFeeEquals() "
                    + "Self not equal to other with same adjustedAmount.", fee1
                    .equals(fee2));

        } catch (Exception e) {
            e.printStackTrace();
            fail("EUFeeTest.testEUFeeEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }


}
