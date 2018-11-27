package us.oh.state.epa.stars2.workflow.engine;

import java.util.HashMap;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: Controller
 * </p>
 * 
 * <p>
 * Description: Base class for Workflow Acitivity and ProcessFlow controllers.
 * Defines the public interface and provides factory methods for creating other
 * controller types.
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
 * @version 1.0
 */

abstract public class Controller implements IController {
    private static final String SF_COMPLETE = "Subflow-Complete";
    private static final String AUTO_READY = "Auto-Ready";
    private static final String SF_READY = "Subflow-Ready";
//    private static final String SKIP_ONE = "Skip-One";
    private static final String MAN_READY = "Manual-Ready";
    private static final String CREATE_SUBFLOWS = "Create-Subflows";
    protected static Logger logger = Logger.getLogger(Controller.class);
    protected HashMap<String, String> dataValues; // Data values associated with a request.

    /**
     * Constructor.
     */
    protected Controller() {
    }

    /**
     * Framework method. Executes the controller object. All controllers are
     * expected to add any information, failure, or error messages to "resp".
     * 
     * @param resp
     *            Response object that will eventually be returned to whoever
     *            asked for this action.
     */
    abstract public void execute(WorkFlowResponse resp, WorkFlowRequest rqst);

    /**
     * Alternate Framework method. Some actions will have associated data values
     * that need to be saved to the database. In that case, use this method to
     * execute the controller. The <code>dataValues</code> will be saved to a
     * data member and may be retrieved by the derived controller either
     * directly or via "getDataMap()". After saving the data map, this method
     * calls the <tt>abstract</tt> "execute()" to perform the actual
     * operation.
     * 
     * @param resp
     *            WorkFlowResponse being returned to the manager.
     * @param dataValues
     *            Data values needed by the derived controller class.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst,
            HashMap<String, String> dataValues) {
        this.dataValues = dataValues;
        execute(resp, rqst);
    }

    /**
     * Factory method for creating Activity controllers. The "command" must
     * correspond to one of the ACTION strings defined in the WorkFlowEngine.
     * The Activity "a" is the activity that will be operated on by the
     * controller. In the (unlikely) event that "command" is not a recognized
     * command string, a special <tt>UnknownActionController</tt> will be
     * returned by this method. Attempts to execute the
     * <tt>UnknownActionController</tt> will simply indicate that the
     * requested action is unknown.
     * 
     * @param command
     *            Command string used to select the controller type.
     * @param act
     *            Activity to be controlled by the Controller.
     * 
     * @return IController The appropriate controller for this command and
     *         Activity type.
     */
    static public IController createController(String command, Activity act) {
//        Controller.logger.error("DWL: called createContoller:  command=" + command +
//                ", performerType=" + act.getPerformerTypeCd(), new Exception());  // #3206
        
        ControllerFactory factory = ControllerFactory.getInstance();

        if (factory == null) {
            Controller.logger.error("No ControllerFactory found.");

            return new UnknownActionController(command);
        }

        if (command.equals(WorkFlowEngine.ACTION_COMPLETE)) {
            // We need a different completion controller depending on whether
            // the Activity we want to complete is a sub-flow Activity or not.
            // For non-subflow Activities, the "Complete" controller doesn't
            // save completion state (check-ins and auto Activities do their
            // own state and then use a Completion controller to figure out
            // what to do next). A sub-flow completion controller needs to
            // save completion state.

            String performerType = act.getPerformerTypeCd();

            if ((performerType.equals(Activity.SUBFLOW_PERFORMER))
                    || (performerType.equals(Activity.DYNAMIC_SF_PERFORMER))) {
                return factory.createControl(Controller.SF_COMPLETE, act);
            }

            return factory.createControl(WorkFlowEngine.ACTION_COMPLETE, act);
        } else if (command.equals(WorkFlowEngine.ACTION_READY)) {
            // "Ready" controllers are also dependent on what kind of Activity
            // we are actually going to "ready". Look at the Activity's
            // performer type code to see what kind of controller we need.

            String performerType = act.getPerformerTypeCd();
//            // start #3206
//            logger.error("DWL: Picking type of controller:  performerTypeCd=" + performerType, new Exception());  // DENNNIS  #3206
//            ProcessActivity debugPA = act.getProcessActivity();
//            if(debugPA != null) {
//                logger.error("DWL: activity info: performerTypeCd=" + debugPA.getPerformerTypeCd() +
//                        ", activityTemplateId=" + debugPA.getActivityTemplateId() +
//                        ", processId=" + debugPA.getProcessId() +
//                        ", userId=" + debugPA.getUserId() +
//                        ", oldUserId=" + debugPA.getOldUserId() +
//                        ", ownerId=" + debugPA.getOwnerId() +
//                        ", activityStatusCd=" + debugPA.getActivityStatusCd()); // #3206
//            } else {
//                logger.error("DWL: activity info: <--processActivity in inbound is null"); // #3206
//            }
//            // end #3206
            
            if (performerType.equals(Activity.AUTOMATIC_PERFORMER)) {
                return factory.createControl(Controller.AUTO_READY, act);
            } else if (performerType.equals(Activity.DYNAMIC_SF_PERFORMER)) {
                return factory.createControl(Controller.CREATE_SUBFLOWS, act);
            } else if (performerType.equals(Activity.SUBFLOW_PERFORMER)) {
                return factory.createControl(Controller.SF_READY, act);
            } else {
                return factory.createControl(Controller.MAN_READY, act);
            }
        }

        IController cc = factory.createControl(command, act);
        return cc;
    }

    /**
     * Factory method for creating ProcessFlow controllers. The "command" must
     * correspond to one of the ACTION strings defined in the WorkFlowEngine.
     * The ProcessFlow "pf" is the process that will be operated on by the
     * controller. In the (unlikely) event that "command" is not a recognized
     * command string, a special <tt>UnknownActionController</tt> will be
     * returned by this method. Attempts to execute the
     * <tt>UnknownActionController</tt> will simply indicate that the
     * requested action is unknown.
     * 
     * @param command
     *            Command string used to select the controller type.
     * @param pf
     *            ProcessFlow to be controlled by the Controller.
     * 
     * @return IController The appropriate controller for this command and
     *         ProcessFlow.
     */
    static public IController createController(String command, ProcessFlow pf) {
        ControllerFactory factory = ControllerFactory.getInstance();

        if (factory == null) {
            Controller.logger.error("No ControllerFactory found.");

            return new UnknownActionController(command);
        }

        IController cc = factory.createControl(command, pf);
        return cc;
    }

    /**
     * Returns the data fields HashMap. If no HashMap was assigned, an empty
     * HashMap is returned to caller.
     * 
     * @return HashMap data field name-value pairs HashMap.
     */
    protected HashMap<String, String> getDataMap() {
        if (dataValues == null) {
            dataValues = new HashMap<String, String>();
        }

        return dataValues;
    }

    /**
     * Logs a Controller exception.
     * 
     * @param e
     *            The exception to be logged.
     */
    static protected void logException(Exception e) throws DAOException {
        logger.error("Controller Exception", e);
        
        throw new DAOException(e.getMessage());
    }

    /**
     * Logs a controller error message.
     * 
     * @param msg
     *            The message to be logged.
     */
//    static protected void logErrorMessage(String msg) {
//        logger.error(msg);
//    }

    /**
     * Logs a controller info message.
     * 
     * @param msg
     *            The message to be logged.
     */
    static protected void logInfoMessage(String msg) {
        logger.debug(msg);
    }
    
	protected String extractRoleDiscrim(WorkFlowRequest command) {
		String roleDiscrim = null;
		for (int i = 0; i < command.getDataCount(); i++) {
			if (WorkFlowEngine.ROLE_DISCRIMINATOR.equals(command.getDataName(i))) {
				roleDiscrim = command.getDataValue(i);
				break;
			}
		}
		return roleDiscrim;
	}

	protected Integer extractAssignedUser(WorkFlowRequest command) {
		Integer assignedUserId = null;
		for (int i = 0; i < command.getDataCount(); i++) {
			if (WorkFlowEngine.ASSIGNED_USER.equals(command.getDataName(i))) {
				assignedUserId = Integer.valueOf(command.getDataValue(i));
				break;
			}
		}
		return assignedUserId;
	}

}
