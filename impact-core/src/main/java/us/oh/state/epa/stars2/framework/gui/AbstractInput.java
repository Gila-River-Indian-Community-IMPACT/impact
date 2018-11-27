package us.oh.state.epa.stars2.framework.gui;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

/**
 * <p>
 * Title: AbstractInput
 * </p>
 *
 * <p>
 * Description: Base class for the implementation of the <code>GuiInput</code>
 * interface. All concrete classes that implement the <code>GuiInput</code>
 * interface should extend this class.
 * </p>
 *
 * <p>
 * The <code>GuiInput</code> consists of a label and some form of GUI object
 * that the user may interact with; for example, a <code>JTextField</code>.
 * Most input objects will include some form of input verifier that validates
 * the user input either on request or whenever keyboard focus is about to leave
 * the object. This class has no specific knowledge of what kind of GUI object
 * is used, or what kind of validation is done. See the
 * <code>AbstractVerifier</code> class for details about input validation.
 * <p>
 *
 * <p>
 * The class relies heavily on the Template pattern. At the proper time, the
 * derived class must provide the Swing component to be used for user input, and
 * the verifier object that will be used to validate that input. This class will
 * be responsible for seeing that operations are performed correctly.
 * <p>
 *
 * <p>
 * This class supports two different types of listeners:
 * <code>ErrorListener</code> and <code>NotificationListener</code>.
 * Whenever an input value fails verification, the error listener interface is
 * invoked to allow the application to process the error message, i.e., display
 * it to the user or log it (or both). The notification listener is invoked
 * whenever an input value is successfully validated. This supports the
 * situation where a user entry in one field alters the available selections in
 * other fields.
 * </p>
 *
 * <p>
 * This class supports two different models for input validation: user input
 * validation and "framework" validation. User input validation occurs after the
 * user has entered data and keyboard focus is about to leave the input object.
 * The verifier is invoked to verify the user's input. If the input is invalid,
 * keyboard focus remains locked in the incorrect field and all error listeners
 * for that field are notified of the error.
 * </p>
 *
 * <p>
 * "Framework" validation is validation that is requested by the application.
 * This could be part of a cross-field validation or simply to validate an
 * individual field. In this case, error and change notifications are not
 * invoked. Instead, the application reads the current value and/or error
 * message directly from this object.
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

public abstract class AbstractInput implements GuiInput, NotificationListener {
    protected boolean frameValidation;
    protected JComponent visual;
    private String label;
    private boolean required;
    private boolean editable;
    private String errorMessage;
    private String stringValue;
    private Object objectValue;
    private AbstractVerifier verifier;
    private ArrayList<ErrorListener> errorListeners;
    private ArrayList<ChangeListener> changeListeners;
    private JPanel panel;

    /**
     * Constructor. The "label" the label for the input object. It will be
     * positioned to the left of the actual input object. If it does not have a
     * trailing ":", one will be appended to it. If "required" is set to "true",
     * then a valid value is required for this field. If "required" is set to
     * "false", the field may be blank. However, if it has a non-blank value,
     * that value is subject to input verification.
     *
     * @param label
     *            The label for the GUI component.
     * @param editable
     *            "True" if the user is allowed to edit this field.
     * @param required
     *            "True" if a valid value is required for this field.
     */
    protected AbstractInput(String label, boolean editable, boolean required) {
        this.changeListeners = new ArrayList<ChangeListener>();
        this.errorListeners = new ArrayList<ErrorListener>();
        this.frameValidation = false;

        // Look for a colon in the label. If we don't find one, add it.

        int idx = label.indexOf(":");

        if (idx < 0) {
            label = label + ": ";
        }

        // Save this for later.

        this.label = label;
        this.required = required;
        this.editable = editable;
    }

    /**
     * Framework method. Extensions of this class should return a JComponent
     * that will be used for user input. For example, many class extensions will
     * return a <code>JTextField</code> as their visual component. This should
     * be a single object (or composite object) that accepts user input and not
     * a JPanel or other collection object. This method is invoked by this class
     * at the proper time.
     *
     * @return Swing component the user will interact with.
     */
    abstract protected JComponent getVisual();

    /**
     * Framework method. Extensions of this class should return an InputVerifier
     * that will be used to validate user input. For example, a class that
     * requires Integer input values would return an IntegerVerifier object.
     * This method is invoked by this class at the proper time.
     *
     * @param component
     *            The Component the the verifier should be attached to.
     *
     * @return The Verifier for that object.
     */
    abstract protected AbstractVerifier createVerifier(JComponent component);

    /**
     * @see GuiInput#getObjectType().
     */
    abstract public String getObjectType();

    /**
     * @see GuiInput#setString(String).
     */
    public void setString(String str) {
        this.stringValue = str.trim();
        this.objectValue = this.stringValue;
        visual.setEnabled(isEditable());
        this.setVisualValue();
    }

    /**
     * @see GuiInput#getString().
     */
    public final String getString() {
        return this.stringValue;
    }

    /**
     * @see GuiInput#setValue(Object).
     */
    public final void setValue(Object obj) {
        this.objectValue = obj;
        this.stringValue = obj.toString();
        this.setVisualValue();
    }

    /**
     * @see GuiInput#getValue().
     */
    public final Object getValue() {
        return this.objectValue;
    }

    /**
     * Adds an <code>ErrorListener</code> to this object. In the event that
     * the user input is invalidate, the error message will be sent to all error
     * listeners.
     *
     * @param listener
     *            ErrorListener to be added.
     */
    public final void addErrorListener(ErrorListener listener) {
        this.errorListeners.add(listener);
    }

    /**
     * Removes an <code>ErrorListener</code> from this object.
     *
     * @param listener
     *            ErrorListener to be removed.
     */
    public final boolean removeErrorListener(ErrorListener listener) {
        return this.errorListeners.remove(listener);
    }

    /**
     * Adds a <code>ChangeListener</code> to this object. This interface is
     * only invoked after user input has been successfully validated. The
     * ChangeListener gives the application to react immediately to the input.
     * For example, a particular input value may affect the available choices
     * elsewhere in the editor.
     *
     * @param listener
     *            ChangeListener to be added.
     */
    public final void addChangeListener(ChangeListener listener) {
        this.changeListeners.add(listener);
    }

    /**
     * Removes an <code>ChangeListener</code> from this object.
     *
     * @param listener
     *            ChangeListener to be removed.
     */
    public final boolean removeChangeListener(ChangeListener listener) {
        return this.changeListeners.remove(listener);
    }

    /**
     * Creates the JComponent container, label, and input object. In addition,
     * also creates the Verifier object for user input. This is done in this way
     * to avoid kloodging up a way to do this in the constructor. Instead, the
     * application instantiates the appropriate input object and then calls this
     * method to finish creating all of the components.
     *
     * @return JComponent A JPanel, label, etc. for accepting user input.
     */
    public final JComponent getComponent() {
        // Create a panel to glue everything together.
        this.panel = new JPanel();
        LayoutManager mgr = new BorderLayout();
        this.panel.setLayout(mgr);
        // panel.setBorder (new LineBorder (Color.ORANGE)) ;

        // Create a label using the label passed in via the constructor.
        JLabel lLabel = new JLabel(this.label, SwingConstants.RIGHT);

        // Call the framework method to have the derived object create the
        // visual input component.
        this.visual = this.getVisual();
        lLabel.setLabelFor(this.visual);
        this.panel.add(lLabel);
        this.panel.add(this.visual, BorderLayout.EAST);
        this.setVisualValue();

        // Call the framework method to have the derived object create the
        // verifier.
        this.verifier = this.createVerifier(this.visual);

        if (this.verifier != null) {
            this.verifier.addNotificationListener(this);
        }

        return this.panel;
    }

    /**
     * Sets visibility for the entire object. Set "visible" to "true" to make
     * the object visible and "false" to make it non-visible.
     *
     * @param visible
     *            boolean "True" if object should be visible.
     */
    public final void setVisible(boolean visible) {
        this.panel.setVisible(visible);
    }

    /**
     * @see GuiInput#isFocusOwner().
     */
    public final boolean isFocusOwner() {
        return this.visual.isFocusOwner();
    }

    /**
     * @see GuiInput#isEditable().
     */
    public final boolean isEditable() {
        return this.editable;
    }

    /**
     * @see GuiInput#requiresValue().
     */
    public final boolean requiresValue() {
        return this.required;
    }

    /**
     * @see GuiInput#isAssigned().
     */
    public boolean isAssigned() {
        return (this.objectValue != null);
    }

    /**
     * @see GuiInput#getLabel().
     */
    public final String getLabel() {
        return this.label;
    }

    /**
     * @see GuiInput#getErrorMessage().
     */
    public final String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * @see GuiInput#validateValue().
     */
    synchronized public final boolean validateValue() {
        // Set the "frameValidation" flag to "true" so we don't propogate the
        // error message to the error listeners. Instead, the error message
        // will be returned via "getErrorMessage()".

        this.frameValidation = true;

        // Let the verifier do the work.

        boolean result = true;

        if (this.verifier != null) {
            result = this.verifier.verify(this.visual);
        }

        this.frameValidation = false;

        return result;
    }

    /**
     * Sends the error message, "errorMsg", to all current error listeners.
     *
     * @param errorMsg
     *            String error message.
     */
    public final void sendErrorNotification(String errorMsg) {
        // Preserve the error message for possible retrieval.

        this.errorMessage = errorMsg;

        // If we are doing "framework validation" (as opposed to directly
        // validating a user input), do not notify the error listeners of
        // the error message.
        if (!this.frameValidation) {
            ErrorListener listener;
            int i;
            int lCnt = this.errorListeners.size();

            // For (each error listener), send it the error message.

            for (i = 0; i < lCnt; i++) {
                listener = this.errorListeners.get(i);
                listener.setErrorMessage(this, errorMsg);
            }
        }
    }

    /**
     * Clears out any left over error messages. In general, old error messages
     * should be cleared at the beginning of validation to avoid leaving
     * incorrect, left-over information in view.
     */
    public final void clearErrorNotification() {
        this.errorMessage = ""; // Clear any previous message

        if (!this.frameValidation) { // If we are doing framework validation,
            ErrorListener listener;
            int i;
            int lCnt = this.errorListeners.size();

            // For (each error listener) tell the listener to clear the message.

            for (i = 0; i < lCnt; i++) {
                listener = this.errorListeners.get(i);
                listener.clearErrorMessage();
            }
        }
    }

    /**
     * Interface method used to support change notification. Once an input
     * object has successfully completed validation, the verifier calls this
     * method (via event listener) to update the <tt>String</tt> version of
     * the object value.
     *
     * @param str
     *            String version of the object value.
     */
    public void setStringValue(String str) {
        this.stringValue = str;
    }

    /**
     * Interface method used to support change notification. Once an input
     * object has successfully completed validation, the verifier calls this
     * method (via event listener) to update the <tt>Object</tt> version of
     * the object value.
     *
     * @param obj
     *            Object version of the object value.
     */
    public void setObjectValue(Object obj) {
        this.objectValue = obj;
    }

    /**
     * Interface method to support change notification. After verifying the
     * input value and setting the string and object values, this method is
     * called by the verifier to issue change notifications to any change
     * listeners.
     */
    public final void sendChangeNotification() {
        this.clearErrorNotification(); // Clear any expired error messages

        if (!this.frameValidation) { // If we are doing "framework"
            ChangeListener listener;
            int i;
            int lCnt = this.changeListeners.size();

            // For (each change listener) send it the change notification.

            for (i = 0; i < lCnt; i++) {
                listener = this.changeListeners.get(i);
                listener.valueChanged(this);
            }
        }
    }

    /**
     * Framework method. Allows the visual value, i.e., the part that is
     * displayed to the user, to be set without triggering a bunch of events.
     * Returns "true" if successful, or "false" if the request fails.
     *
     * @return boolean "True" is visual value was set.
     */
    protected boolean setVisualValue() {
        boolean ret = true;

        if ((this.visual == null)
                || ((this.objectValue == null) && (this.stringValue == null))) {
            ret = false;
        } else {
            String newValue = this.stringValue;

            if (this.objectValue != null) {
                newValue = this.objectValue.toString();
            }

            this.frameValidation = true;

            boolean successful = false;

            if (this.verifier != null) {
                successful = this.verifier.verify(newValue);
            }

            if (successful) {
                if (this.visual instanceof JTextComponent) {
                    JTextComponent jtc = (JTextComponent) (this.visual);
                    jtc.setText(newValue);
                }
            } else {
                ret = false;
            }
        }

        return ret;
    }
}
