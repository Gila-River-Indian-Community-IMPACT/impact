package us.oh.state.epa.stars2.framework.exception;

/**
 * Application exceptions represent a misconfiguration of the system or bad user
 * data. They do not represent a failure of a software component that is not
 * part of the application such as <code>java.sql.SQLException</code> and
 * <code>java.io.IOException</code>
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
public class ApplicationException extends Exception implements BaseException {
    private String prettyMsg = null;  //  If this string is set, use it in DisplayUnil Statements
    /**
     * @TODO Write javadoc for ApplicationException
     */
    public ApplicationException() {
        super();
        prettyMsg = null;
    }

    /**
     * @TODO Write javadoc for ApplicationException(String)
     * @param message
     */
    public ApplicationException(final String message) {
        super(message);
        prettyMsg = null;
    }
    
    public ApplicationException(final String message, final String prettyMsg) {
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
