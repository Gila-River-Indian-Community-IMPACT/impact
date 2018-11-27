package us.oh.state.epa.stars2.framework.gui;

/**
 * <p>
 * Title: ErrorListener
 * </p>
 *
 * <p>
 * Description: This interface provides the ability to receive error messages
 * from a GUI input object at the time when focus is about to leave the object.
 * When this is about to occur, a field level validator is invoked and
 * determines whether or not the user input is valid. If it is, then the
 * "clearMessage()" method is called to remove any prior error message. If the
 * user input is invalid, then "setErrorMessage()" is called to identify the
 * error.
 * </p>
 *
 * <p>
 * Applications are strongly encouraged to implement this interface. Since the
 * error message will only occur if the field input is invalid, the application
 * should display the error to the user so the user can correct the input.
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

public interface ErrorListener {
    /**
     * Allows the field to clear any prior error messages once data has been
     * entered into the field successfully.
     */
    void clearErrorMessage();

    /**
     * Sends an error message to the listener. "Src" identifies the source of
     * the message. "Msg" is the actual error message. It is up to the Listener
     * to decide how the message is to be used.
     *
     * @param src
     *            The GuiInput that is originating the error message.
     * @param msg
     *            The error message.
     */
    void setErrorMessage(GuiInput src, String msg);
}
