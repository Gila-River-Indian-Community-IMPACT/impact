package us.oh.state.epa.stars2.workflow.engine;

/**
 * <p>
 * Title: WorkFlowException
 * </p>
 * 
 * <p>
 * Description: This is a "place-holder" exception that allows users of the
 * <code>WorkFlowManager</code> to tell the difference between errors in
 * communication with the workflow engine. When things go wrong, they will
 * either fail due to a communications error or because the WorkFlowEngine was
 * "unhappy" about something. This exception handles the case where
 * communications worked, but the workflow was "unhappy".
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
 */

public class WorkFlowException extends Exception {
    /**
     * Constructor.
     * 
     * @param msg
     *            The exception message.
     */
    public WorkFlowException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            The Exception message.
     * @param t
     *            the "root cause" exception.
     */
    public WorkFlowException(String msg, Throwable t) {
        super(msg, t);
    }
}
