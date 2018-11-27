package us.oh.state.epa.stars2.workflow.engine;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.def.FacilityRoleDef;

/**
 * <p>
 * Title: AbstractWorkFlowCommand
 * </p>
 * 
 * <p>
 * Description: This is the base class for commands that "plug into" the
 * workflow engine. This class provides common functionality needed by derived
 * command classes.
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
 * @version 1.2
 */
public abstract class AbstractWorkFlowCommand {
    private static Logger logger = Logger.getLogger(AbstractWorkFlowCommand.class);
    private boolean valid = true;
    private LinkedList<String> errors;
    private LinkedList<String> infoMsgs;

    private FacilityRoleDef[] facilityRoles;

    public FacilityRoleDef[] getFacilityRoles() {
		return facilityRoles;
	}

	public void setFacilityRoles(FacilityRoleDef[] facilityRoles) {
		this.facilityRoles = facilityRoles;
	}
	
    /**
     * All derived classes must implement this method.
     * 
     * @param wfData
     *            WorkFlowCmdData Information about the order, service, workflow
     *            steps, etc., so the command can perform its service.
     * 
     * @throws Exception
     *             Something went wrong.
     */
    abstract public void execute(WorkFlowCmdData wfData) throws Exception;

    /**
     * Framework method. This method is called before "execute()" to allow a
     * derived class object to validate workflow data. If the input is not
     * valid, the derived class should log an error message and return
     * <code>false</code>. The the input is valid, the derived class should
     * return <code>true</code>. This class provides a default implementation
     * that simply returns <code>true</code>.
     * 
     * @param wfData
     *            WorkFlowCmdData Information being passed from the workflow
     *            engine to the command object.
     * 
     * @return boolean "true" if the data in the input object is valid.
     */
    public boolean validate(WorkFlowCmdData wfData) {
        return true;
    }

    /**
     * Returns overall success status of the command. If the derived object logs
     * an exception or adds an error message, this value will automatically be
     * set to false.
     * 
     * @return boolean False if error is detected; otherwise true.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Used by the workflow engine to retrieve any error messages set by the
     * derived class. The workflow engine will both log these messages and
     * append them to the response object sent back to the requesting
     * application.
     * 
     * @return String[] Array of error messages.
     */
    public String[] getErrorMessages() {
        return errors.toArray(new String[0]);
    }

    /**
     * Used by the workflow engine to retrieve any information messages set by
     * the derived class. The workflow engine will both log these messages and
     * append them to the response object sent back to the requesting
     * application.
     * 
     * @return String[] Array of error messages.
     */
    public String[] getInfoMessages() {
        return infoMsgs.toArray(new String[0]);
    }

    /**
     * Allows the derived class to append "message" to the current list of error
     * messages. These messages will be retrieved by the workflow engine via the
     * "getErrorMessages()" method. Adding an error message also automatically
     * sets the "valid" flag (returned via the "isValid()" method) to
     * <code>false</code>.
     * 
     * @param message
     *            String Error message.
     */
    protected void addErrorMessage(String message) {
        if (errors == null) {
            errors = new LinkedList<String>();
        }

        errors.add(message);
        valid = false;
    }

    /**
     * Allows the derived class to append "message" to the current list of info
     * messages. These messages will be retrieved by the workflow engine via the
     * "getInfoMessages()" method.
     * 
     * @param message
     *            String Informational message.
     */
    protected void addInfoMessage(String message) {
        if (infoMsgs == null) {
            infoMsgs = new LinkedList<String>();
        }

        infoMsgs.add(message);
    }

    /**
     * Allows the derived class to log an Exception to the workflow engine logs.
     * In addition, the Exception error message is added to the list of error
     * messages (via "addErrorMessage()").
     * 
     * @param e
     *            Exception Exception to be logged.
     */
    protected void logException(Exception e) {
        String msg = e.getMessage();

        // Some exceptions don't have an actual message. If this is one of
        // those, then the class name of the Exception is the message.

        if ((msg == null) || (msg.length() == 0)) {
            msg = e.getClass().getName();
        }

        addErrorMessage(msg);
        logger.error(msg, e);
    }
}
