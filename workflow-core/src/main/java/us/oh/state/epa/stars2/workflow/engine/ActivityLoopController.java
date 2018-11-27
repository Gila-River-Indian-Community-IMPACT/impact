package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivityGotoController
 * </p>
 * 
 * <p>
 * Description: Takes an Activity that has previously been completed and returns
 * it to the "Ready" state so somebody can redo the operation. Also, any
 * downstream Activities that may have been skipped are reset to the "Not Done"
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
 * @author S. Wooster
 * @version 1.0
 */

public class ActivityLoopController extends ActivityController {
    /**
     * Constructor.
     * 
     * @param a
     *            The Activity being undone.
     */
    public ActivityLoopController(Activity a) {
        super(a);
    }

    /**
     * 
     * Starting at the redo activity, mark every activity downstream as not done
     * in preparation for executing each activity.
     * 
     * @param resp
     *            WorkFlowResponse being returned to calling application.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {

        synchronized (activity) {

            if (Activity.IN_PROCESS.equals(activity.getStatusCd())
                    || Activity.PENDING.equals(activity.getStatusCd())) {
                activity.setStatusCd(Activity.NOT_DONE);
                activity.setDueDt(null);
                activity.setJeopardyDt(null);
                activity.setStartDt(null);
                activity.setReadyDt(null);
                activity.setEndDt(null);
            }

            try {
                Controller.logger.debug(": ActivityLoopController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
                return;
            }

        }
        //
        // Special Case - Do not follow loops
        if (activity.getOutTransitionCode().equals(Activity.LOOP)) {
            return;
        }

        // Get the parent ProcessFlow and then get all of the outbound
        // transitions from this Activity. If we don't have any outbound
        // transistions, then something weird is going on, but we don't care.

        ProcessFlow parent = activity.getContainer();
        Transition[] outbounds = parent.getOutboundTransitions(activity);

        if ((outbounds != null) && (outbounds.length > 0)) {
            Activity downstream;
            // For each outbound Activity, send an "unskip" control to it.
            for (Transition tt : outbounds) {
                Integer downstreamId = tt.getToId();
                downstream = parent.getActivity(downstreamId);
                IController c = Controller.createController(
                        WorkFlowEngine.ACTION_LOOP, downstream);
                c.execute(resp, new WorkFlowRequest());
            }
        }
    }
}
