package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivityUnskipController
 * </p>
 * 
 * <p>
 * Description: Handles sending a "unskip" command to an Activity. When
 * un-skipping an Activity, the Activity is marked "Not Done" and an "un-skip"
 * command is sent to all Activities downstream from the current Activity. Note
 * that "un-skip" commands are not propogated upwards to the containing
 * ProcessFlow. ProcessFlow.
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

public class ActivityUnskipController extends ActivityController {
    /**
     * Constructor. Takes a reference to the Activity to be skipped.
     * 
     * @param a
     *            Activity to be skipped.
     */
    public ActivityUnskipController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Marks this Activity as "Not Done" in the database and
     * attempts to send "un-skip" messages to downstream Activities.
     * 
     * @param resp
     *            WorkFlowReponse for error and failure messages.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // If this Activity is not in the "skipped" state, then don't touch
        // it or do anything else.
        synchronized (activity) {
            String status = activity.getStatusCd();

            if (Activity.SKIPPED.compareTo(status) == 0) {
                // Mark this Activity as "Not Done" and then save the state to
                // the
                // database.
                activity.setStatusCd(Activity.NOT_DONE);
                activity.setStartDt(null);
                activity.setEndDt(null);
                activity.setReadyDt(null);
                activity.setDueDt(null);

                try {
                    Controller.logger.debug(": ActivityUnskipController.execute(): process_id=" + 
                			activity.getProcessId() + ", activity_template_id=" + 
                			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                    saveToDatabase();
                } catch (DAOException de) {
                    Controller.logger.error(de.getMessage(), de);
                    resp.addFailure(de.getMessage());
                    return;
                }
            }
        }
        //
        // Special Case - Do not follow loops
        if (!Activity.LOOP.equals(activity.getOutTransitionCode())) {
            // Get the containing ProcessFlow. Use this to get all of the
            // outbound Transitions for this Activity.
            ProcessFlow container = activity.getContainer();
            Transition[] outbounds = container.getOutboundTransitions(activity);

            // FOR (each outbound Transition) send an "un-skip" control to the
            // Activity on the other end.
            for (Transition tt : outbounds) {
                Integer toActivityId = tt.getToId();
                Activity inbound = container.getActivity(toActivityId);
                IController c = Controller.createController(
                        WorkFlowEngine.ACTION_UNSKIP, inbound);
                c.execute(resp, new WorkFlowRequest());
            }
        }
    }
}
