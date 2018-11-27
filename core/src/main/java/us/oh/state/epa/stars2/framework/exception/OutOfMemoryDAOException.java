package us.oh.state.epa.stars2.framework.exception;

public class OutOfMemoryDAOException extends DAOException {

    /**
     * Constructor. Used to generate an exception that does not wrap another
     * exception.
     *
     * @param message
     *            A string message to be displayed with the exception.
     */
    public OutOfMemoryDAOException(final String message) {
        super(message);
    }
}