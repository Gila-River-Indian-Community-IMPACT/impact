package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivityRollbackController
 * </p>
 * 
 * <p>
 * Description: Used with "Terminate Service Order" Activity to reset the
 * Activity to a "Not Done" state and undo the transition that put us in that
 * state.
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

public class ActivityRollbackController extends ActivityController {
    /**
     * Constructor.
     * 
     * @param a
     *            The Activity that is being rolled back.
     */
    public ActivityRollbackController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Rolls back the Activity passed in the constructor to
     * the "Not Done" state and saves this state to the database. Then, sends an
     * "Undo" request to all upstream Activities.
     * 
     * @param resp
     *            WorkFlowResponse being returned to calling application.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // Mark this Activity as "Not Done", i.e., the initial state.

        synchronized (activity) {
            activity.setStatusCd(Activity.NOT_DONE);
            activity.setStartDt(null);
            activity.setEndDt(null);
            activity.setReadyDt(null);
            activity.setDueDt(null);

            // Save the Activity to the database.

            try {
                Controller.logger.debug(": ActivityRollbackController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (Exception e) {
                Controller.logger.error(e.getMessage(), e);
                resp.addFailure(e.getMessage());
                return;
            }
        }
        // Get the parent ProcessFlow and then get all of the inbound
        // transitions to this Activity. If we don't have any inbound
        // transistions, then something weird is going on.
        ProcessFlow parent = activity.getContainer();
        Transition[] inbounds = parent.getInboundTransitions(activity);

        if (inbounds != null) {
            Activity upstream;
            // For each inbound Activity, send an "undo" control back to it.
            for (Transition tt : inbounds) {
                Integer upstreamId = tt.getFromId();
                upstream = parent.getActivity(upstreamId);
                IController cc = Controller.createController(
                        WorkFlowEngine.ACTION_UNDO_ROLLBACK, upstream);
                cc.execute(resp, new WorkFlowRequest());
            }
        } else {
            String errMsg = "No inbound Transitions to Activity = ["
                    + activity.getActivityName() + "].";

            Controller.logger.error(errMsg);
            resp.addError(errMsg);
        }
        return;
    }
}
