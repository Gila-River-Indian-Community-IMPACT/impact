package us.oh.state.epa.stars2.framework.gui;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * <p>Title: TextComponentVerifier</p>
 *
 * <p>Description: Input verifier for text data.  The only constraint enforced
 *                 is the optional maximum character count.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: MentorGen, LLC</p>
 * @author J. E. Collier
 * @version 1.0
 */

public class TextComponentVerifier extends AbstractVerifier {
    private JTextComponent text;
    private Integer maxCharCnt;

    /**
     * Constructor.  Attaches this object to "text" as the
     * <code>InputVerifier</code>.  Set "maxCharCnt" to <tt>null</tt> to disable
     * character count range checking.
     *
     * @param text The text input object, e.g., JTextField.
     * @param maxCharCnt Optional maximum character count.
     */
    TextComponentVerifier(JTextComponent text, Integer maxCharCnt) {
        this.text = text;
        this.text.setInputVerifier(this);

        this.maxCharCnt = maxCharCnt;
    }

    /**
     * Framework method.  This implementation strips leading/trailing white
     * space off the user's input and updates the field with the trimmed
     * string.  Then, calls the "verify()" method to perform the actual field
     * validation.  Note that this is a typical usage of this method.
     *
     * @param input The component containing the user's input.
     *
     * @return boolean Results of the call to "verify()".
     */
    public final boolean shouldYieldFocus(JComponent input) {
        String trimmed = this.text.getText().trim();
        this.text.setText(trimmed);

        return this.verify(input);
    }

    /**
     * Framework method.  Validates the user's input.  Returns "true" if the
     * input component is a string (which is pretty easy) and does not exceed
     * the number of characters specified by "maxCharCnt".  If the input value
     * is valid, change notifications are sent to the listener and "true" is
     * returned.  If the value is invalid, an error message is sent to the
     * listener and "false" is returned.
     *
     * @param input The component containing the user's input.
     *
     * @return boolean "True" if the input is valid.
     */
    public final boolean verify(JComponent input) {
        //  Get the user's input.  If we don't have a value, set it to an
        //  empty string.
        String lText = this.text.getText();
        return this.verify(lText);
    }

    public final boolean verify(String inText) {
        boolean ret = false;

        if (inText == null) {
            inText = "";
        }

        // If we have a maximum character count check, do that check now.

        if ((this.maxCharCnt != null) && (this.maxCharCnt.intValue() > 0)
                && (inText.length() > this.maxCharCnt.intValue())) {
            this.sendErrorNotification("Maximum number of characters ("
                    + this.maxCharCnt.toString() + ") exceeded.");
        } else {
            // If we get here, the value is acceptable. Update the string and
            // object values and then send the change notification.
            this.updateStringValue(inText);
            this.updateObjectValue(inText);

            this.sendChangeNotification();
            ret = true;
        }

        return ret;
    }
}
