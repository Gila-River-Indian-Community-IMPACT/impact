/*******************************************************************************
 * $Workfile: ServiceFactoryException.java $
 * Copyright 2001-2003 Cardinal Health
 *
 *******************************************************************************
 *
 * Revision History:
 *
 * $Log: ServiceFactoryException.java,v $
 * Revision 1.2  2007/05/01 20:10:20  kbradley
 * *** empty log message ***
 *
 * Revision 1.1  2007/02/06 19:39:04  kbradley
 * This is a major code commit to support having the portal and application is the same source tree.
 *
 * Revision 1.1  2007/01/12 20:39:19  kbradley
 * Initial Source Load
 *
 * Revision 1.4  2006/05/15 14:25:45  dapc_user
 * *** empty log message ***
 *
 * Revision 1.3  2006/05/02 20:05:30  dapc_user
 * *** empty log message ***
 *
 * Revision 1.2  2006/05/01 20:12:03  dapc_user
 * *** empty log message ***
 *
 * Revision 1.1  2006/04/13 18:50:04  dapc_user
 * *** empty log message ***
 *
 * 
 * 1     3/26/03 1:48p Aharshba
 * Created
 *******************************************************************************/
package us.oh.state.epa.stars2.webcommon;

/**
 * This class implements an exception that can be thrown by the ServiceFactory.
 * 
 * @version $Revision: 1.2 $
 */
public class ServiceFactoryException extends Exception {
    private Exception exception;

    /**
     * Creates a new ServiceFactoryException wrapping another exception, and
     * with a detail message.
     * 
     * @param message
     *            the detail message.
     * @param exception
     *            the wrapped exception.
     */
    public ServiceFactoryException(final String message,
            final Exception exception) {
        super(message);
        this.exception = exception;
        return;
    }

    /**
     * Creates a ServiceFactoryException with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public ServiceFactoryException(final String message) {
        this(message, null);
        return;
    }

    /**
     * Creates a new ServiceFactoryException wrapping another exception, and
     * with no detail message.
     * 
     * @param exception
     *            the wrapped exception.
     */
    public ServiceFactoryException(final Exception exception) {
        this(null, exception);
        return;
    }

    /**
     * Gets the wrapped exception.
     * 
     * @return the wrapped exception.
     */
    public final Exception getException() {
        return exception;
    }

    /**
     * Retrieves (recursively) the root cause exception.
     * 
     * @return the root cause exception.
     */
    public final Exception getRootCause() {
        Exception ret = exception;

        if (exception != null) {
            if (exception instanceof ServiceFactoryException) {
                ret = ((ServiceFactoryException) exception).getRootCause();
            }
        } else {
            ret = this;
        }

        return ret;
    }

    public final String toString() {
        String ret = super.toString();

        if (exception != null) {
            ret = exception.toString();
        }

        return ret;
    }
}
