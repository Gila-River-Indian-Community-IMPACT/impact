package us.oh.state.epa.stars2.framework.gui;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * <p>Title: IntegerInput</p>
 *
 * <p>Description: This object accepts an integer value from a user.  A
 *                 text field, <code>JTextField</code>, is used to accept the
 *                 user's input.  The contents of the text field are verified
 *                 to represent an integer value.  If range checking is
 *                 specified, then it is verified as well.</p>
 *
 * <p>Range checking is flexible.  To disable all range checking, specify
 * <tt>null</tt> for both the minimum and maximum values.  To specify a
 * minimum value, provide a value for "minValue".  Similarly, to specify a
 * maximum value, provide a value for "maxValue".</p>.
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: MentorGen, LLC</p>
 * @author J. E. Collier
 * @version 1.0
 */

public class IntegerInput extends AbstractInput {
    private JTextField textField;
    private Integer initValue;
    private Integer minValue;
    private Integer maxValue;

    /**
     * Constructor.  The "propertyLabel" is the label that will be associated
     * with the text field.  It should be "fairly unique".  The "initValue"
     * is the initial value for the field.  "minValue" is an optional minimum
     * value specification.  "maxValue" is an optional maximum value
     * specification.  If the user should be permitted to edit the field, then
     * set "editable" to "true".  If the field is being displayed for information
     * only, set "editable" to "false".  If a value is required for this field,
     * set "required" to "true".
     *
     * @param propertyLabel The label associated with this text field.
     * @param initValue The initial value for the field.
     * @param minValue The minimum value to be accepted (inclusive).
     * @param maxValue The maximum value to be accepted (inclusive).
     * @param editable "True" if this field is editable by the user.
     * @param required "True" if a value is required for this field.
     */
    public IntegerInput(String propertyLabel, Integer initValue,
            Integer minValue, Integer maxValue, boolean editable,
            boolean required) {
        super(propertyLabel, editable, required);

        this.initValue = initValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        //      this.editable = editable ;

        //  Only initialize this object if we have an initial value.

        if (initValue != null) {
            this.setStringValue(initValue.toString());
            this.setObjectValue(initValue);
        }
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
     * Framework method.  @see AbstractInput.getVisual().
     *
     * @return JComponent The text field used to accept Integer input.
     */
    protected final JComponent getVisual() {
        this.textField = new JTextField(12);

        if (this.initValue != null) {
            this.textField.setText(this.initValue.toString());
        }

        this.textField.setEditable(this.isEditable());

        if (this.isEditable()) {
            this.textField.setForeground(Color.BLUE);
        }

        return this.textField;
    }

    /**
     * Framework method.  @see AbstractInput.createVerifier(JComponent).
     *
     * @param component Basically the same component we gave to the framework.
     *
     * @return AbstractVerifier The object used to verify the user's input.
     */
    protected final AbstractVerifier createVerifier(JComponent component) {
        JTextComponent tc = (JTextComponent) component;
        IntegerVerifier iv = new IntegerVerifier(tc, this.initValue,
                this.minValue, this.maxValue);

        return iv;
    }
}
