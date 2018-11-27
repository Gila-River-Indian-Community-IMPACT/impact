package us.oh.state.epa.stars2.framework.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * <p>Title: DurationInput</p>
 *
 * <p>Description: Input object for time durations.  Time durations are
 *                 typically stored in the database as "seconds".  However,
 *                 the actual duration is more suitably displayed and "days"
 *                 and "hours".  This class automatically converts between
 *                 "seconds" and "days/hours" so the user sees and edits
 *                 "days and hours", but the internal value is "seconds".</p>
 *
 * <p>The user is presented with separate fields for "days" and "hours".  These
 * fields are integer values greater than or equal to zero (at least, until
 * somebody invents a time travel machine).  A change notification will be
 * issued (if there is a notification listener) when either the "days" field or
 * the "hours" field is updated.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: MentorGen, LLC</p>
 * @author J. E. Collier
 * @version 1.0
 */

public class DurationInput extends AbstractInput {
    private JPanel glue;
    private JLabel daysLabel;
    private JTextField daysField;
    private JLabel hoursLabel;
    private JTextField hoursField;
    private DurationVerifier durVerifier;
    private Integer days;
    private Integer hours;

    /**
     * Constructor.
     *
     * @param propertyLabel The property label.
     * @param initValue The initial value in seconds.
     * @param editable "True" if the data is editable.
     * @param required "True" if the field is required.
     */
    public DurationInput(String propertyLabel, Integer initValue,
            boolean editable, boolean required) {
        super(propertyLabel, editable, required);
        setObjectValue(initValue);
    }

    /**
     * Framework method.  @see GuiInput.getObjectType().
     *
     * @return String the class name of the object returned by this object.
     */
    public final String getObjectType() {
        return Integer.class.getName();
    }

    /**
     * Overloaded framework method.  If the object value is being set, treat the
     * input object as an Integer (seconds), and update the contents of the
     * "Days" and "Hours" fields if available.  Also stores the value in the
     * base class.
     *
     * @param obj Integer value in seconds.
     */
    public final void setObjectValue(Object obj) {
        // Save the base class value first.
        super.setObjectValue(obj);
        Integer seconds = (Integer) obj;

        // If we don't have an Integer value, we are done.
        if (seconds != null) {
            // Convert the seconds to days and hours and update our respective
            // fields (if we have created them).
            int minutes = seconds / 60;
            int hrs = minutes / 60;

            int dayCnt = hrs / 24;
            int hourCnt = hrs % 24;

            this.days = new Integer(dayCnt);
            this.hours = new Integer(hourCnt);

            // If we don't have a verifier, then we have no "Days" and "Hours"
            // fields to update, and we are done.
            if (this.durVerifier != null) {
                // We have fields to update. Disable any verification so we
                // don't generate a stack overflow.
                this.durVerifier.setInUpdate(true);

                if (this.daysField != null) {
                    this.daysField.setText(this.days.toString());
                }

                if (this.hoursField != null) {
                    this.hoursField.setText(this.hours.toString());
                }

                // Re-enable verification.
                this.durVerifier.setInUpdate(false);
            }
        }
    }

    protected final boolean setVisualValue() {
        boolean ret = false;

        if (super.setVisualValue()) {
            Integer durValue = null;

            try {
                durValue = new Integer(this.getString());
            } catch (NumberFormatException nfe) {
            }

            if (durValue != null) {
                this.setObjectValue(durValue);
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Framework method.  @see AbstractInput.getVisual().
     *
     * @return JComponent The text field used to accept Integer input.
     */
    protected final JComponent getVisual() {
        //  "Glue" is going to glue the Days/Hours labels and text fields
        //  together.
        this.glue = new JPanel();
        this.glue.setLayout(new GridLayout(2, 2));

        this.daysLabel = new JLabel("Days", SwingConstants.CENTER);
        this.daysField = new JTextField(4);

        if (this.days == null) {
            this.days = new Integer(0);
        }

        this.daysField.setText(this.days.toString());

        this.hoursLabel = new JLabel("Hours", SwingConstants.CENTER);
        this.hoursField = new JTextField(4);

        if (this.hours == null) {
            this.hours = new Integer(0);
        }

        Object obj = this.getValue();

        if (obj == null) {
            this.setValue(new Integer(0));
        }

        this.hoursField.setText(this.hours.toString());

        this.glue.setLayout(new GridLayout(2, 2));
        this.glue.add(daysLabel);
        this.glue.add(daysField);
        this.glue.add(hoursLabel);
        this.glue.add(hoursField);
        this.daysField.setEditable(this.isEditable());
        this.hoursField.setEditable(this.isEditable());

        return this.glue;
    }

    /**
     * Framework method.  Returns the verifier object used to validate user
     * inputs.
     *
     * @param component Ignored.
     *
     * @return DurationVerifier object.
     */
    protected AbstractVerifier createVerifier(JComponent component) {
        this.durVerifier = new DurationVerifier(this.daysField,
                this.hoursField);
        return this.durVerifier;
    }
}
