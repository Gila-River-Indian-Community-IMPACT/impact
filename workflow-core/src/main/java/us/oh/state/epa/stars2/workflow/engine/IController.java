package us.oh.state.epa.stars2.workflow.engine;

import java.util.HashMap;

/**
 * <p>
 * Title: IController
 * </p>
 * 
 * <p>
 * Description: Interface that defines Workflow engine controller classes.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */
public interface IController {
    /**
     * Framework method. Executes the controller object. All controllers are
     * expected to add any information, failure, or error messages to "resp".
     * 
     * @param resp
     *            Response object that will eventually be returned to whoever
     *            asked for this action.
     */
    void execute(WorkFlowResponse resp, WorkFlowRequest rqst);

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
    void execute(WorkFlowResponse resp, WorkFlowRequest rqst, HashMap<String, String> dataValues);
}
