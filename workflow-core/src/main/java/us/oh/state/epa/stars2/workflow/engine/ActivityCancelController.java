package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author yehp
 * @version 1.0
 */

public class ActivityCancelController extends ActivityController {
    /**
     * Constructor. Takes a reference to the automatic Activity to be readied.
     * The Activity is not validated to insure that it is an automatic Activity.
     * 
     * @param a
     *            Automatic Activity to be readied.
     */
    public ActivityCancelController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Marks this Activity as "Pending" and then attempts to
     * execute the associated automatic command handler. If that succeeds, sends
     * "Ready" commands to all downstream Activities. If the automatic command
     * handler fails, changes this Activity to "Blocked" and "Manual". Any error
     * or failure messages generated are added to "resp".
     * 
     * @param resp
     *            WorkFlowResponse object for recording errors or failures.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        synchronized (activity) {
            try {
                this.cancelActivity();
                Controller.logger.debug(": ActivityCancelController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                this.saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage());
                resp.addFailure(de.getMessage());
                return;
            }
        }
        // Ready following activity is no needed for cancel.
        /*IController c = Controller.createController(
                WorkFlowEngine.ACTION_COMPLETE, activity);
        c.execute(resp, rqst, dataValues);*/
    }
}
