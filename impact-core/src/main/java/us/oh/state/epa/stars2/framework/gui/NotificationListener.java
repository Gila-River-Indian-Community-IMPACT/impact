package us.oh.state.epa.stars2.framework.gui;

/**
 * <p>
 * Title: NotificationListener
 * </p>
 *
 * <p>
 * Description: The NotificationListener receives events from the verifier
 * objects. The <code>AbstractInput</code> objects typically implement this
 * interface.
 * </p>
 *
 * <p>
 * Whenever a successful validation occurs, the Verifier object will update the
 * "string" and "object" values via "setStringValue()" and "setObjectValue()"
 * respectively, and then call "sendChangeNotification()" to tell the listener
 * to propogate the changes to its listeners.
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

public interface NotificationListener {
    /**
     * Message to the NotificationListener to propogate an error message to its
     * error listeners.
     *
     * @param errorMsg
     *            String The error message.
     */
    void sendErrorNotification(String errorMsg);

    /**
     * Save updated string value following successful validation.
     *
     * @param str
     *            New string value.
     */
    void setStringValue(String str);

    /**
     * Save updated object value following successful validation.
     *
     * @param pobj
     *            New object value.
     */
    void setObjectValue(Object obj);

    /**
     * Tells our listener to send its change notifications to its listeners.
     */
    void sendChangeNotification();
}
