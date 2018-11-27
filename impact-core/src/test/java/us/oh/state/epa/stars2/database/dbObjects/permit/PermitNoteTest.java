package us.oh.state.epa.stars2.database.dbObjects.permit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Test;

/**
 * Junit test suite for the {@link PermitNote} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class PermitNoteTest {

    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
    public void testPermitNoteValid() {
        PermitNote note = new PermitNote();
        assertFalse("PermitNoteTest.testPermitValid() Empty note is valid.",
                note.isValid());
        note.setPermitId(0011223333);
        assertFalse(
                "PermitNoteTest.testPermitNoteValid() PermitNote valid after setPermitId().",
                note.isValid());

        note.setUserId(0011223333);
        assertFalse(
                "PermitNoteTest.testPermitNoteValid() Note valid after setUserId().",
                note.isValid());
        note.setDateEntered(new Timestamp(Calendar.getInstance()
                .getTimeInMillis()));
        assertTrue(
                "PermitNoteTest.testPermitValid() Note not Valid after setDateEntered().",
                note.isValid());
    }

    /**
     * Test the {@link PermitNote#equals(Object obj)} and
     * {@link PermitNote#hashCode()} methods.
     */
	@Test
    public void testPermitNoteEquals() {

        PermitNote note1 = new PermitNote();
        PermitNote note2 = new PermitNote();

        try {
            assertTrue(
                    "PermitNoteTest.testPermitNoteEquals() Self does not equal self.",
                    note1.equals(note1));

            note1.setPermitId(new Integer(12345));
            assertFalse("PermitNoteTest.testPermitNoteEquals() "
                    + "Self equals other with different permitId.", note1
                    .equals(note2));
            note2.setPermitId(note1.getPermitId());
            assertTrue("PermitNoteTest.testPermitNoteEquals() "
                    + "Self not equal to other with same permitId.", note1
                    .equals(note2));
            assertEquals(
                    "PermitNoteTest.testPermitNoteEquals() Hashcodes differ with same permitId.",
                    note1.hashCode(), note2.hashCode());
        } catch (Exception e) {
            e.printStackTrace();
            fail("NoteTest.testPermitNoteEquals() threw an unexpected exception"
                    + e.getMessage());
        }

    }



}
