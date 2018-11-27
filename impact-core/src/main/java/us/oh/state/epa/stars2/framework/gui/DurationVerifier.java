package us.oh.state.epa.stars2.framework.gui;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import us.oh.state.epa.stars2.framework.exception.ApplicationException;

/**
 * <p>
 * Title: DurationVerifier
 * </p>
 * 
 * <p>
 * Description: The Verifier for the DurationInput object. The input object has
 * two fields, "Days" and "Hours", both of which must be validated.
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

public class DurationVerifier extends AbstractVerifier {
    private JTextComponent daysField;
    private JTextComponent hoursField;
    private boolean inUpdate;

    /**
     * Constructor. The parameters are both the input components.
     * 
     * @param daysField
     *            The "Days" text field.
     * @param hoursField
     *            The "Hours" text field.
     */
    public DurationVerifier(JTextComponent daysField, JTextComponent hoursField) {
        this.daysField = daysField;
        this.hoursField = hoursField;
        this.inUpdate = false;

        daysField.setInputVerifier(this);
        hoursField.setInputVerifier(this);
    }

    /**
     * Framework method. This implementation strips leading/trailing white space
     * off both user input fields and updates each field with the trimmed
     * string. Then, calls the "verify()" method to perform the actual field
     * validation. Note that this is a typical usage of this method.
     * 
     * @param input
     *            The component containing the user's input.
     * 
     * @return boolean Results of the call to "verify()".
     */
    public final boolean shouldYieldFocus(JComponent input) {
        // Trim the "Days" field.
        String days = this.daysField.getText();
        days = days.trim();
        this.daysField.setText(days);

        // Trim the "Hours" field.
        String hours = this.hoursField.getText();
        hours = hours.trim();
        this.hoursField.setText(hours);

        return this.verify(input);
    }

    /**
     * Framework method. Validates the user's input. Returns "true" if both
     * input components contains a valid integer value and that value is greater
     * than or equal to zero. If the input value is valid, change notifications
     * are sent to the listener (once per updated field) and "true" is returned.
     * If the value in either field is invalid, an error message is sent to the
     * listener and "false" is returned.
     * 
     * @param input
     *            The component containing the user's input.
     * 
     * @return boolean "True" if the input is valid.
     */
    public final boolean verify(JComponent input) {
        boolean ret = true;
        // If we are in update mode, the input has already been validated and
        // we are simply updating the displayed values.
        if (!this.inUpdate) {
            Integer days = null;
            Integer hours = null;

            // This is a bit unusual, but what we are doing is validating one
            // field at a time. If the first one fails, stop validating and
            // display the error message.
            try {
                days = this.validateDays(this.daysField);
                hours = this.validateHours(this.hoursField);
            } catch (ApplicationException ae) {
                ret = false;
            }

            if (ret) {
                // Convert both "Days" and "Hours" into seconds and save this as
                // our
                // new object value. Then send out the appropriate
                // notifications.
                int secs = (days.intValue() * 24 * 60 * 60)
                        + (hours.intValue() * 60 * 60);
                Integer seconds = new Integer(secs);

                this.updateObjectValue(seconds);
                this.updateStringValue(seconds.toString());

                this.sendChangeNotification();
            }
        }

        return ret;
    }

    /**
     * Verifies that the String "input" value can be converted to a non-negative
     * Integer value. Returns "true" if it can, or "false" of it fails.
     * 
     * @param input
     *            String Candidate integer value.
     * 
     * @return boolean True if "input" represents an integer >= 0.
     */
    public final boolean verify(String input) {
        Integer foo = null;
        boolean rslt = false;

        try {
            foo = new Integer(input);

            if (foo.intValue() >= 0) {
                rslt = true;
            }
        } catch (Exception e) {
            rslt = false;
        }

        return rslt;
    }

    /**
     * Indicates whether the input object is "in update" or not. If set to
     * "true", the contents of the object have already been validated and we
     * simply need to update the displays. The primary reason for doing this is
     * to adjust the display if the user enters a value greater than "24" in the
     * hours field.
     * 
     * @param inUpdate
     *            "True" if we are in the middle of an update.
     */
    final void setInUpdate(boolean inUpdate) {
        this.inUpdate = inUpdate;
    }

    /**
     * Verifies that the contents of the "Days" field represent an integer value
     * greater than or equal to zero. If not, appropriate error messages are
     * triggered.
     * 
     * @param textField
     *            The text field containing "Days" data.
     * 
     * @return Integer The Integer equivalent of "Days".
     * 
     * @throws java.lang.Exception
     *             Invalid data.
     */
    private Integer validateDays(JTextComponent textField)
            throws ApplicationException {
        Integer foo = null;
        String fieldValue = textField.getText();

        // First check: Make sure the text in the field is actually an
        // integer value.

        try {
            foo = new Integer(fieldValue);
        } catch (NumberFormatException nfe) {
            this.sendErrorNotification("[" + fieldValue + "] is not a valid "
                    + "integer for days.");
            throw new ApplicationException(nfe.getMessage());
        }

        // Since we can't travel backwards in time, the "Days" value must be
        // >= zero.

        if (foo.intValue() < 0) {
            this.sendErrorNotification("Days cannot be less than zero.");
            throw new ApplicationException("Days cannot be less than zero.");
        }

        return foo;
    }

    /**
     * Verifies that the contents of the "Hours" field represent an integer
     * value greater than or equal to zero. If not, appropriate error messages
     * are triggered.
     * 
     * @param textField
     *            The text field containing "Hours" data.
     * 
     * @return Integer The Integer equivalent of "Hours".
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.ApplicationException
     *             Invalid data.
     */
    private Integer validateHours(JTextComponent textField)
            throws ApplicationException {
        Integer foo = null;
        String fieldValue = textField.getText();

        // First check: Make sure the text in the field is actually an
        // integer value.

        try {
            foo = new Integer(fieldValue);
        } catch (NumberFormatException nfe) {
            this.sendErrorNotification("[" + fieldValue + "] is not a valid "
                    + "integer for hours.");
            throw new ApplicationException(nfe.getMessage());
        }

        // Since we can't travel backwards in time, the "Hours" value must be
        // >= zero.

        if (foo.intValue() < 0) {
            this.sendErrorNotification("Hours cannot be less than zero.");
            throw new ApplicationException("Hours cannot be less than zero.");
        }

        return foo;
    }
}
