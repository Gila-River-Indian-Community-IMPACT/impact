package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.def.PermitStatusDef;

/**
 * Junit test suite for the {@link PermitEU} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitEUTest {

	//TODO: Fix against database
    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
	@Ignore
    public void testPermitEUValid() {

        PermitEU pEU = new PermitEU();
        assertFalse(
                "PermitEUTest.testPermitEUValid() Empty PermitEU is valid.",
                pEU.isValid());

        pEU.setPermitEUGroupID(0011223333);
        assertFalse(
                "PermitEUTest.testPermitEUValid() PermitEU valid after setPermitEUGroupID().",
                pEU.isValid());

        pEU.setPermitStatusCd(PermitStatusDef.EXTENDED);
        assertFalse(
                "PermitEUTest.testPermitEUValid() PermitEU valid after setPermitStatusCd().",
                pEU.isValid());

        pEU.setFacilityEUID(0011223333);
        pEU.setCorrEpaEMUID(0011223333);
        assertFalse(
                "PermitEUTest.testPermitEUValid() PermitEU valid after setFacilityEUID().",
                pEU.isValid());

    }

	// TODO: Fix against database
    /**
     * Test the {@link PermitEU#equals(Object obj)} and
     * {@link PermitEU#hashCode()} methods.
     */
    @Test
    @Ignore
    public void testPermitEUEquals() {

        PermitEU eu1 = new PermitEU();
        PermitEU eu2 = new PermitEU();

        try {
            assertTrue(
                    "PermitEUTest.testPermitEUEquals() Self does not equal self.",
                    eu1.equals(eu1));

            eu1.setPermitEUID(new Integer(12345));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different permitEUId.", eu1
                    .equals(eu2));
            eu2.setPermitEUID(eu1.getPermitEUID());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same permitEUID.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setPermitEUGroupID(new Integer(23456));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different permitEUGroupID.", eu1
                    .equals(eu2));
            eu2.setPermitEUGroupID(eu1.getPermitEUGroupID());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same permitEUGroupID.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setFacilityEUID(new Integer(3344));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different facilityEUID.", eu1
                    .equals(eu2));
            eu2.setFacilityEUID(eu1.getFacilityEUID());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same facilityEUID.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setBatID(new Integer(3344));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different batID.", eu1
                    .equals(eu2));
            eu2.setBatID(eu1.getBatID());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same batID.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setEUFeeID(new Integer(3344));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different euFeeID.", eu1
                    .equals(eu2));
            eu2.setEUFeeID(eu1.getEUFeeID());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same euFeeID.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setPermitStatusCd("Status Code");
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different permitStatusCd.", eu1
                    .equals(eu2));
            eu2.setPermitStatusCd(eu1.getPermitStatusCd());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same permitStatusCd.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setGeneralPermitTypeCd("ABCD");
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different generalPermitTypeCd.",
                    eu1.equals(eu2));
            eu2.setGeneralPermitTypeCd(eu1.getGeneralPermitTypeCd());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same generalPermitTypeCd.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setModelGeneralPermitCd("EFGH");
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different modelGeneralPermitCd.",
                    eu1.equals(eu2));
            eu2.setModelGeneralPermitCd(eu1.getModelGeneralPermitCd());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same modelGeneralPermitCd.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setDapcDescription("You are ugly!");
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different dapcDescription.", eu1
                    .equals(eu2));
            eu2.setDapcDescription(eu1.getDapcDescription());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same dapcDescription.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setCompanyId("A Company ID");
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different companyId.", eu1
                    .equals(eu2));
            eu2.setCompanyId(eu1.getCompanyId());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same companyId.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setRevocationApplicationID(99999);
            assertFalse(
                    "PermitEUTest.testPermitEUEquals() "
                            + "Self equals other with different revocationApplicationID.",
                    eu1.equals(eu2));
            eu2.setRevocationApplicationID(eu1.getRevocationApplicationID());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same revocationApplicationID.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setRevocationDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different revocationDate.", eu1
                    .equals(eu2));
            eu2.setRevocationDate(eu1.getRevocationDate());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same revocationDate.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setExtensionApplicationID(99999);
            assertFalse(
                    "PermitEUTest.testPermitEUEquals() "
                            + "Self equals other with different extensionApplicationID.",
                    eu1.equals(eu2));
            eu2.setExtensionApplicationID(eu1.getExtensionApplicationID());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same extensionApplicationID.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setExtensionDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different extensionDate.", eu1
                    .equals(eu2));
            eu2.setExtensionDate(eu1.getExtensionDate());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same extensionDate.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setTerminatedDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different terminatedDate.", eu1
                    .equals(eu2));
            eu2.setTerminatedDate(eu1.getTerminatedDate());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same terminatedDate.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setSupersededDate(new Timestamp(Calendar.getInstance()
                    .getTimeInMillis()));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different supersededDate.", eu1
                    .equals(eu2));
            eu2.setSupersededDate(eu1.getSupersededDate());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same supersededDate.",
                    eu1.hashCode(), eu2.hashCode());

            eu1.setSupersededPermitID(new Integer(64));
            assertFalse("PermitEUTest.testPermitEUEquals() "
                    + "Self equals other with different supersededPermitID.",
                    eu1.equals(eu2));
            eu2.setSupersededPermitID(eu1.getSupersededPermitID());
            assertEquals(
                    "PermitEUTest.testPermitEUEquals() Hashcodes differ with same supersededPermitID.",
                    eu1.hashCode(), eu2.hashCode());

            assertTrue("PermitEUTest.testPermitEUEquals() "
                    + "Self not equal to other with same fields.", eu1
                    .equals(eu2));
        } catch (Exception e) {
            e.printStackTrace();
            fail("PermitEUTest.testPermitEUEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }



}
