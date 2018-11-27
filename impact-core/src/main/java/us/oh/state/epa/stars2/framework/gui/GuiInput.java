package us.oh.state.epa.stars2.framework.gui;

/**
 * <p>
 * Title: GuiInput
 * </p>
 *
 * <p>
 * Description: Defines a common interface for GUI input objects. Each GuiInput
 * may consist of different Swing components and different kinds of verifiers.
 * However, they will all honor this interface.
 * </p>
 *
 * <p>
 * Specific details about how this methods work together are provided in
 * <code>AbstractInput</code>.
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

public interface GuiInput {
    /**
     * Identifies the underlying class name for the data object managed by this.
     * For example, if the input output is supposed to be an Integer, this
     * method should return "java.lang.Integer". This is used with the
     * Reflection mechanism to automatically map setter and getter methods.
     *
     * @return String Name of the underlying object type.
     */
    String getObjectType();

    /**
     * Initialization method. Allows the object to be initialized with a
     * "string" version of its value. The value entered here will be displayed
     * to the user, and will be overridden by any value the user inputs to the
     * object.
     *
     * @param svalue
     *            String version of an object value.
     */
    void setString(String svalue);

    /**
     * Returns the current "string" version of the object value. This should
     * only be called when you know that all user inputs have been completed.
     *
     * @return String Current string value.
     */
    String getString();

    /**
     * Initialization method. Allows the object to be initialized with an
     * "object" version of its value. The value entered here will be displayed
     * to the user, and will be overridden by any value the user inputs to the
     * object.
     *
     * @param obj
     *            Object version of an object value.
     */
    void setValue(Object obj);

    /**
     * Returns the current "object" version of the object value. This should
     * only be called when you know that all user inputs have been completed.
     *
     * @return Object Current object value.
     */
    Object getValue();

    /**
     * Returns "true" if the user may edit the value in this object. Returns
     * "false" if the field is "read-only".
     *
     * @return boolean "True" if the user can edit the field.
     */
    boolean isEditable();

    /**
     * Returns "true" if this field requires a value, otherwise returns false.
     * Fields that require a value are not permitted to be blank. Fields that do
     * not require a value may be blank. However, if the field has a value, it
     * must pass field level validation.
     *
     * @return boolean "True" if this field requires a value.
     */
    boolean requiresValue();

    /**
     * Returns "true" if this field is currently assigned a valid value.
     * Otherwise, returns "false".
     *
     * @return boolean "True" if this field has a valid value.
     */
    boolean isAssigned();

    /**
     * Returns the current label for the GUI input object. This is useful for
     * annotating error messages.
     *
     * @return String Current label.
     */
    String getLabel();

    /**
     * Returns "true" if this visual component currently owns keyboard focus, or
     * "false" if it does not.
     *
     * @return boolean "True" if this component currently has keyboard focus.
     */
    boolean isFocusOwner();

    /**
     * Performs "framework" validation, i.e., verifies that the contents of this
     * object are valid, but does not invoke the notification mechanisms.
     * Returns "true" if the contents are valid and will update the "string" and
     * "object" values retrieved via "getString()" and "getObject()". Returns
     * "false" if an error was detected. In that case, the error message can be
     * retrieved via "getErrorMessage()".
     *
     * @return boolean "True" if the contents of this object are valid.
     */
    boolean validateValue();

    /**
     * Returns any current error message associated with this object. Typically,
     * this method is called whenever "validateValue()" returns "false".
     *
     * @return String error message.
     */
    String getErrorMessage();
}
