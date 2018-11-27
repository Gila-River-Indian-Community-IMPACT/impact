package us.oh.state.epa.stars2.framework.exception;

/**
 * System Exceptions are exceptions that occur outside of the software.
 * For example, <code>java.sql.SQLException</code> and <code>
 * java.io.IOException</code> should normally be translated as system
 * exceptions.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT><DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT><DD>Mentorgen, LLC</DD>
 * </DL>
 * @version    1.0
 * @author     Andrew Wilcox
 */
public class SystemException extends RuntimeException implements BaseException {

    /**
     * @TODO Write Javadoc for SystemException 
     */
    public SystemException() {
        super();
    }

    /**
     * @TODO Write Javadoc for SystemException(String message)
     * @param message
     */
    public SystemException(String message) {
        super(message);
    }
}
