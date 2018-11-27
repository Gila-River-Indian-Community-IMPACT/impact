package us.oh.state.epa.stars2.framework.exception;

import java.rmi.RemoteException;

/**
 * Title: DAOException.
 *
 * <p>
 * Description: This class is a wrapper class for exceptions encountered by the
 * DAO framework. In general, any exception generated inside the DAO framework,
 * e.g., <tt>SQLException</tt>, can be wrapped inside a DAOException. This
 * allows applications to field a single exception type.
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 *
 * @author J. E. Collier
 * @version 1.0
 */

public class DAOException extends RemoteException {
    private String prettyMsg = null;  //  If this string is set, use it in DisplayUnil Statements
    /**
     * Constructor. Used to generate an exception that does not wrap another
     * exception.
     *
     * @param message
     *            A string message to be displayed with the exception.
     */
    public DAOException(final String message) {
        super(message);
        prettyMsg = null;
    }
    
    /**
     * Constructor. Used to generate an exception that does not wrap another
     * exception.
     *
     * @param message
     *            A string message to be displayed with the exception.
     * @param prettyMsg
     *            A string message to be displayed to the user.
     */
    public DAOException(final String message, final String prettyMsg) {
        super(message);
        this.prettyMsg = prettyMsg;
    }

    /**
     * Constructor. Used to wrap another exception, such as an
     * <tt>SQLException</tt> or <tt>IOException</tt> inside this exception.
     * In this case, the wrapped exception, <tt>t</tt>, is the exception
     * associated with the original error condition.
     *
     * @param message
     *            A string message to be displayed with the exception.
     * @param t
     *            A <tt>Throwable</tt> to be wrapped inside this exception.
     */
    public DAOException(final String message, final Throwable t) {
        super(message, t);
        prettyMsg = null;
    }
    
    /**
     * Constructor. Used to wrap another exception, such as an
     * <tt>SQLException</tt> or <tt>IOException</tt> inside this exception.
     * In this case, the wrapped exception, <tt>t</tt>, is the exception
     * associated with the original error condition.
     *
     * @param message
     *            A string message to be displayed with the exception.
     * @param prettyMsg
     *            A string message to be displayed to the user.
     * @param t
     *            A <tt>Throwable</tt> to be wrapped inside this exception.
     */
    public DAOException(final String message, final String prettyMsg, final Throwable t) {
        super(message);
        this.prettyMsg = prettyMsg;
    }

    public String getPrettyMsg() {
        return prettyMsg;
    }
    
    public boolean prettyMsgIsNull() {
        return prettyMsg == null;
    }
}
