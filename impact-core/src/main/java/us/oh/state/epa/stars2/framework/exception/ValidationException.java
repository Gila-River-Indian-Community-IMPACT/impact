package us.oh.state.epa.stars2.framework.exception;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

/**
 * Provides an exception that can contain a series of {@link ValidationMessage}.
 * Can be thrown by a business object that wants to inform the caller of any
 * business rule violations.
 * 
 * @author Charles Meier (cmeier@acm.org)
 * @version 1.0
 */

public class ValidationException extends RemoteException {
    private ValidationMessage[] _messages;

    /**
     * Construct an exception that does not wrap another exception.
     * 
     * @param message
     *            A string message to be displayed with the exception.
     */
    public ValidationException(final String message) {
        super(message);
    }

    /**
     * Construct an exception that wraps another exception.
     * 
     * @param message
     *            A string message to be displayed with the exception.
     * @param t
     *            A <tt>Throwable</tt> to be wrapped inside this exception.
     */
    public ValidationException(final String message, final Throwable t) {
        super(message, t);
    }

    /**
     * Construct an exception that wraps another exception and contains an array
     * of {@link ValidationMessage}.
     * 
     * @param message
     *            A string message to be displayed with the exception.
     * @param t
     *            A <tt>Throwable</tt> to be wrapped inside this exception.
     * @param messages
     *            An array of validation messages describing the problems
     *            encountered by a business object.
     */
    public ValidationException(final String message, final Throwable t,
            final ValidationMessage[] messages) {
        super(message, t);
        _messages = messages;
    }

    public ValidationMessage[] getValidationMessages() {
        return _messages;
    }

    public void setValidationMessages(final ValidationMessage[] messages) {
        _messages = messages;
    }

}
