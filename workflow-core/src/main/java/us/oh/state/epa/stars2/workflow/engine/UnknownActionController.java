package us.oh.state.epa.stars2.workflow.engine;

/**
 * <p>
 * Title: UnknownActionController
 * </p>
 * 
 * <p>
 * Description: Controller object created by the Controller factory whenever it
 * is asked to create a controller for an unknown action. The "execute()" method
 * responds by adding an error message to the response object indicating that an
 * unknown action was requested.
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

class UnknownActionController extends Controller {
    private String unknownAction;

    /**
     * Constructor. "UnknownAction" is the unknown action string.
     * 
     * @param unknownAction
     *            The unknown action that was requested.
     */
    UnknownActionController(String unknownAction) {
        this.unknownAction = unknownAction;
    }

    /**
     * Framework method. Adds an error message to the response object indicating
     * that an unknown action was requested.
     * 
     * @param resp
     *            Response object.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        String errMsg = "Unknown Action requested: [" + this.unknownAction
                + "].";

        Controller.logger.error(errMsg);
        resp.addError(errMsg);
    }
}
