package us.oh.state.epa.stars2.framework.gui;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * <p>
 * Title: IntegerVerifier
 * </p>
 *
 * <p>
 * Description: InputVerifier for integer data. Verifies that the user's input
 * value represents a valid integer and is within the optional minimum and
 * maximum values.
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 *
 * @author J. E. Collier
 * @version 1.0
 */

public class IntegerVerifier extends AbstractVerifier {
    private JTextComponent text;
    private Integer minValue;
    private Integer maxValue;

    /**
     * Constructor. Attaches this object to "text" as the
     * <code>InputVerifier</code>. Set "minValue" and/or "maxValue" to
     * <tt>null</tt> to disable minimum/maximum range checking.
     *
     * @param text
     *            The text input object, e.g., JTextField.
     * @param initValue
     *            The initial integer value.
     * @param minValue
     *            Optional minimum integer value.
     * @param maxValue
     *            Optional maximum integer value.
     */
    IntegerVerifier(JTextComponent text, Integer initValue, Integer minValue,
            Integer maxValue) {
        this.text = text;
        text.setInputVerifier(this);

        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Framework method. This implementation strips leading/trailing white space
     * off the user's input and updates the field with the trimmed string. Then,
     * calls the "verify()" method to perform the actual field validation. Note
     * that this is a typical usage of this method.
     *
     * @param input
     *            The component containing the user's input.
     *
     * @return boolean Results of the call to "verify()".
     */
    public final boolean shouldYieldFocus(JComponent input) {
        String trimmed = this.text.getText().trim();
        this.text.setText(trimmed);

        return this.verify(input);
    }

    /**
     * Framework method. Validates the user's input. Returns "true" if the input
     * component contains a valid integer value and that value is within the
     * optional minimum and maximum value range (inclusive). If the input value
     * is valid, change notifications are sent to the listener and "true" is
     * returned. If the value is invalid, an error message is sent to the
     * listener and "false" is returned.
     *
     * @param input
     *            The component containing the user's input.
     *
     * @return boolean "True" if the input is valid.
     */
    public final boolean verify(JComponent input) {
        return this.verify(this.text.getText());
    }

    public final boolean verify(String input) {
        Integer foo = null;
        boolean ret = false;

        // First check: Make sure the text in the field is actually an
        // integer value.
        try {
            foo = new Integer(input);
        } catch (NumberFormatException nfe) {
            this.sendErrorNotification("[" + input
                    + "] is not a valid integer.");
        }

        if (foo != null) {
            // If a minimum value has been specified, make sure our current
            // value
            // is >= the minimum value.
            if ((this.minValue != null) && (this.minValue.intValue() > foo.intValue())) {
                this.sendErrorNotification("Value must be greater than or equal to "
                                + this.minValue.toString() + ".");
            } else if ((this.maxValue != null) && (this.maxValue.intValue() < foo.intValue())) {
                // If a maximum value has been specified, make sure our current
                // value
                // is <= the maximum value.
                this.sendErrorNotification("Value must be less than or equal to "
                                + this.maxValue.toString() + ".");
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
