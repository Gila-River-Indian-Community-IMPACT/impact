package us.oh.state.epa.stars2.framework.gui;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * <p>Title: StringInput</p>
 *
 * <p>Description: This is your basic text field.  The input object is a
 * <code>JTextField</code>.  The only validation that is performed is insuring
 * that the optional character count is not exceeded.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: MentorGen, LLC</p>
 * @author J. E. Collier
 * @version 1.0
 */

public class StringInput extends AbstractInput {
    private JTextField textField;
    private String initValue;
    private Integer maxCharCnt;

    /**
     * Constructor.  The "propertyLabel" is the label that will be associated
     * with the text field.  It should be "fairly unique".  The "initValue"
     * is the initial value for the field.  "maxCharCnt" is an optional maximum
     * number of characters specification (an underlying persistence mechanism
     * may have a limit to the number of characters it accepts for this value).
     * If the user should be permitted to edit the field, then set "editable" to
     * "true".  If the field is being displayed for information only, set
     * "editable" to "false".  If a value is required for this field, set
     * "required" to "true".
     *
     * @param propertyLabel The label associated with this text field.
     * @param initValue The initial value for the field.
     * @param maxCharCnt Optional maximum number of characters accepted for
     *                   for this field.
     * @param editable "True" if this field is editable by the user.
     * @param required "True" if a value is required for this field.
     */
    public StringInput(String propertyLabel, String initValue,
            Integer maxCharCnt, boolean editable, boolean required) {
        super(propertyLabel, editable, required);
        this.initValue = initValue;
        this.maxCharCnt = maxCharCnt;

        this.setStringValue(initValue);
        this.setObjectValue(initValue);
    }

    /**
     * Framework method.  @see GuiInput.getObjectType().
     *
     * @return String the class name of the object returned by this object.
     */
    public final String getObjectType() {
        return String.class.getName();
    }

    /**
     * Framework method.  @see AbstractInput.getVisual().
     *
     * @return JComponent The text field used to accept string input.
     */
    protected final JComponent getVisual() {
        this.textField = new JTextField(12);
        this.textField.setText(this.initValue);
        this.textField.setEditable(this.isEditable());

        return this.textField;
    }

    /**
     * Overload method.  Sets the value of this object via a string.  In this
     * case, the "Object" value is also assigned.
     *
     * @param str String New String value.
     */
    public final void setString(String str) {
        String foo = str.trim();
        super.setObjectValue(foo);
        super.setString(foo);
    }

    /**
     * Returns "true" if this object is currently assigned a value, or "false"
     * if this object does not currently have a value.
     *
     * @return boolean "true" if this object is assigned a value.
     */
    public final boolean isAssigned() {
        Object obj = this.getValue();
        boolean ret = false;

        if ((obj != null) && (((String) obj).length() > 0)) {
            ret = true;
        }

        return ret;
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
        TextComponentVerifier tcv = new TextComponentVerifier(tc,
                this.maxCharCnt);
        return tcv;
    }
}
