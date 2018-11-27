package us.oh.state.epa.stars2.framework.gui;

import javax.swing.InputVerifier;
import javax.swing.JComponent;

/**
 * <p>
 * Title: AbstractVerifier
 * </p>
 *
 * <p>
 * Description: Abstract implementation of the <code>InputVerifier</code>
 * class. Mostly, this class defines the listener mechanisms between the derived
 * class and the <code>AbstractInput</code> class.
 * </p>
 *
 * <p>
 * All derived classes are required to implement the "verify()" method. All
 * derived classes should also implement the "shouldYieldFocus()" method. See
 * <code>javax.swing.InputVerifier</code> for differences between the two
 * methods. A good use for "shouldYieldFocus()" is to remove leading/trailing
 * whitespace from the input value before final validation in "verify()".
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

abstract public class AbstractVerifier extends InputVerifier {
    private NotificationListener listener;

    /**
     * Constructor.
     */
    protected AbstractVerifier() {
    }

    /**
     * Framework method. This method is actually specified in
     * <code>InputVerifier</code>. Verifies that the contents of "input" are
     * valid.
     *
     * @param input
     *            The visual component whose contents need to be verified.
     *
     * @return boolean "True" if the contents of "input" are valid, otherwise
     *         "false".
     */
    abstract public boolean verify(JComponent input);

    /**
     * Framework method. Similar to previous method, except verifies that the
     * input value would be a valid value if it were placed in the component.
     *
     * @param input
     *            String Candidate component value.
     *
     * @return boolean "true" if "input" is valid, otherwise "false".
     */
    abstract public boolean verify(String input);

    /**
     * Adds a <code>NotificationListener</code> to this object. This class
     * supports only one listener (usually an <code>AbstractInput</code>).
     * That listener will receive all errors, updates, etc.
     *
     * @param listener
     *            Notification listener.
     */
    void addNotificationListener(NotificationListener inListener) {
        this.listener = inListener;
    }

    /**
     * Sends an error notification to the listener. Derived class objects are
     * expected to call this method whenever an input error has been detected.
     *
     * @param errMsg
     *            Error message.
     */
    protected void sendErrorNotification(String errMsg) {
        this.listener.sendErrorNotification(errMsg);
    }

    /**
     * Updates the string version of the object value after the input value has
     * been validated. This is sent to the notification listener. Derived class
     * objects are expected to call this method after successfully validating
     * the input.
     *
     * @param strValue
     *            String version of the object value.
     */
    protected void updateStringValue(String strValue) {
        this.listener.setStringValue(strValue);
    }

    /**
     * Updates the object version of the object value after the input value has
     * been validated. This is sent to the notification listener. Derived class
     * objects are expected to call this method after successfully validating
     * the input.
     *
     * @param objValue
     *            Object version of the object value.
     */
    protected void updateObjectValue(Object objValue) {
        this.listener.setObjectValue(objValue);
    }

    /**
     * Tells the listener to send the change notification to its listeners. This
     * method should be called by derived class objects after validating the
     * input value, and then updating the string and object values.
     */
    protected void sendChangeNotification() {
        this.listener.sendChangeNotification();
    }
}
