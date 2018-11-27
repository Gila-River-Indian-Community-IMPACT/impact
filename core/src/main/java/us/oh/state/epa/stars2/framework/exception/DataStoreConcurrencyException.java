package us.oh.state.epa.stars2.framework.exception;

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

public class DataStoreConcurrencyException extends DAOException {
    /**
     * Constructor. Used to generate an exception that does not wrap another
     * exception.
     *
     * @param message
     *            A string message to be displayed with the exception.
     */
    public DataStoreConcurrencyException(final String message) {
        super(message);
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
    public DataStoreConcurrencyException(final String message, 
            final Throwable t) {
        super(message, t);
    }
}
