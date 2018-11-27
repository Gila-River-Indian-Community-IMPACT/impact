package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Calendar;

import org.junit.Test;


/**
 * Junit test suite for the {@link Note} class.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1
 */
public class NoteTest {

    /**
     * Make sure that validation messages are correctly handled as fields are
     * modified.
     */
	@Test
    public void testNoteValid() {

        Note note = new Note();
        assertFalse("NoteTest.testValid() Empty note is valid.", note.isValid());
        note.setUserId(0011223333);
        assertFalse("NoteTest.testValid() Note valid after setUserId().", 
                    note.isValid());
        note.setDateEntered(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        assertTrue("NoteTest.testValid() Note not Valid after setDateEntered().",
                   note.isValid());

    }

    /**
     * Test the {@link Note#equals(Object obj)} and {@link Note#hashCode()}
     * methods.
     */
    @Test
    public void testNoteEquals() {

        Note note1 = new Note();
        Note note2 = new Note();

        try {
            assertTrue("NoteTest.testNoteEquals() Self does not equal self.",
                       note1.equals(note1));

            note1.setDateEntered(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            assertFalse("NoteTest.testNoteEquals() Self equals other with different timestamp.",
                        note1.equals(note2));

            note2.setDateEntered(note1.getDateEntered());
            assertTrue("NoteTest.testNoteEquals() Self not equal to other with same timestamp.",
                       note1.equals(note2));
            assertEquals("NoteTest.testNoteEquals() Hashcodes differ with same timestamp.",
                         note1.hashCode(), note2.hashCode());

            note1.setNoteId(new Integer(12345));
            assertFalse("NoteTest.testNoteEquals() Self equals other with different noteId.",
                        note1.equals(note2));
                        
            note2.setNoteId(note1.getNoteId());
            assertTrue("NoteTest.testNoteEquals() Self not equal to other with same noteId.",
                       note1.equals(note2));
                    
            assertEquals("NoteTest.testNoteEquals() Hashcodes differ with same noteId.",
                         note1.hashCode(), note2.hashCode());

            note1.setNoteTxt("Hello, Wayne's World!");
            assertFalse("NoteTest.testNoteEquals() Self equals other with different noteTxt.",
                        note1.equals(note2));
                    
            note2.setNoteTxt(note1.getNoteTxt());
            assertTrue("NoteTest.testNoteEquals() Self not equal to other with same noteTxt.",
                       note1.equals(note2));
                    
            assertEquals("NoteTest.testNoteEquals() Hashcodes differ with same noteTxt.",
                         note1.hashCode(), note2.hashCode());

            note1.setNoteTypeCd("Note Code");
            assertFalse("NoteTest.testNoteEquals() Self equals other with different noteTypeCd.",
                        note1.equals(note2));

            note2.setNoteTypeCd(note1.getNoteTypeCd());
            assertTrue("NoteTest.testNoteEquals() Self not equal to other with same noteTypeCd.",
                       note1.equals(note2));
            
            assertEquals("NoteTest.testNoteEquals() Hashcodes differ with same noteTypeCd.",
                         note1.hashCode(), note2.hashCode());

            note1.setUserId(new Integer(12345));
            assertFalse("NoteTest.testNoteEquals() Self equals other with different userId.",
                        note1.equals(note2));
                    
            note2.setUserId(note1.getUserId());
            assertTrue("NoteTest.testNoteEquals() Self not equal to other with same userId.",
                       note1.equals(note2));
                    
            assertEquals("NoteTest.testNoteEquals() Hashcodes differ with same userId.",
                         note1.hashCode(), note2.hashCode());

        }
        catch (Exception e) {
            e.printStackTrace();
            fail("NoteTest.testNoteEquals() threw an unexpected exception"
                 + e.getMessage());
        }

    }


}
