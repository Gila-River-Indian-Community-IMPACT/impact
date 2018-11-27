package us.oh.state.epa.stars2.framework.gui;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * <p>Title: FloatVerifier</p>
 *
 * <p>Description: InputVerifier for floating point data.  Verifies that the
 *                 user's input value represents a valid float and is within the
 *                 optional minimum and maximum values.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: MentorGen, LLC</p>
 * @author J. E. Collier
 * @version 1.0
 */

public class FloatVerifier extends AbstractVerifier {
    private JTextComponent text;
    private Float minValue;
    private Float maxValue;

    /**
     * Constructor.  Attaches this object to "text" as the
     * <code>InputVerifier</code>.  Set "minValue" and/or "maxValue" to
     * <tt>null</tt> to disable minimum/maximum range checking.
     *
     * @param text The text input object, e.g., JTextField.
     * @param initValue The initial Float value.
     * @param minValue Optional minimum Float value.
     * @param maxValue Optional maximum Float value.
     */
    FloatVerifier(JTextComponent text, Float initValue, Float minValue,
            Float maxValue) {
        this.text = text;
        text.setInputVerifier(this);

        this.minValue = minValue;
        this.maxValue = maxValue;
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
        String trimmed = text.getText().trim();
        text.setText(trimmed);

        return this.verify(input);
    }

    /**
     * Framework method.  Validates the user's input.  Returns "true" if the
     * input component contains a valid Float value and that value is within
     * the optional minimum and maximum value range (inclusive).  If the input
     * value is valid, change notifications are sent to the listener and "true"
     * is returned.  If the value is invalid, an error message is sent to the
     * listener and "false" is returned.
     *
     * @param input The component containing the user's input.
     *
     * @return boolean "True" if the input is valid.
     */
    public final boolean verify(JComponent input) {
        String fieldValue = text.getText();
        return this.verify(fieldValue);
    }

    /**
     * Validates the user's input.  Returns "true" if the input component
     * contains a valid Float value and that value is within the optional minimum
     * and maximum value range (inclusive).  If the input value is valid, change
     * notifications are sent to the listener and "true" is returned.  If the
     * value is invalid, an error message is sent to the listener and "false" is
     * returned.
     *
     * @param input String The String value to be verified.
     *
     * @return boolean "True" if "input" is a valid floating point value.
     */
    public final boolean verify(String input) {
        Float foo = null;
        boolean ret = false;

        // First check: Make sure the text in the field is actually an
        // Float value.
        try {
            foo = new Float(input);
        } catch (NumberFormatException nfe) {
            this.sendErrorNotification("[" + input + "] is not a valid Float.");
        }

        if (foo != null) {
            // If a minimum value has been specified, make sure our current
            // value
            // is >= the minimum value.
            if ((minValue != null) && (minValue.floatValue() > foo.floatValue())) {
                this.sendErrorNotification("Value must be greater than or equal to "
                                + minValue.toString() + ".");
            } else if ((maxValue != null) && (maxValue.floatValue() < foo.floatValue())) {
                // If a maximum value has been specified, make sure our current
                // value
                // is <= the maximum value.
                this.sendErrorNotification("Value must be less than or equal to "
                                + maxValue.toString() + ".");
            } else {
                // If we get here, the value is acceptable. Update the string
                // and
                // object values and then send the change notification.
                this.updateStringValue(foo.toString());
                this.updateObjectValue(foo);

                this.sendChangeNotification();
                
                ret = true;
            }
        }

        return ret;
    }
}
