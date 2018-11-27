package us.oh.state.epa.stars2.framework.exception;

/**
 * UnableToStartException.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version 1.0
 * @author Andrew Wilcox
 */
public class UnableToStartException extends ApplicationException {
    private Throwable nested;

    /**
     * @TODO Write Javadoc for UnableToStartException
     */
    public UnableToStartException() {
        super();
    }

    /**
     * @TODO Write Javadoc for UnableToStartException(String message)
     * @param message
     */
    public UnableToStartException(String message) {
        super(message);
    }

    /**
     * @TODO Write Javadoc for UnableToStartException(Throwable nested)
     * @param nested
     */
    public UnableToStartException(Throwable nested) {
        super(nested.getMessage());
        this.nested = nested;
    }

    /**
     * @see java.lang.Throwable#printStackTrace()
     */
    public final void printStackTrace() {
        if (nested != null) {
            nested.printStackTrace();
        } else {
            super.printStackTrace();
        }
    }
}
