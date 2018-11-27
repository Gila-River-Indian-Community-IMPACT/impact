package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivityUndoController
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
 * @author J. E. Collier
 * @version 1.0
 */

public class ActivityUndoController extends ActivityController {
    /**
     * Constructor.
     * 
     * @param a
     *            The Activity being undone.
     */
    public ActivityUndoController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Puts the current Activity in the "Ready" state and
     * sends "un-skip" messages to all downstream Activities.
     * 
     * @param resp
     *            WorkFlowResponse being returned to calling application.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // Check the status of the Activity. If it is not in the "Completed"
        // state, then don't touch it.

        synchronized (activity) {
            String status = activity.getStatusCd();

            if (Activity.COMPLETED.compareTo(status) != 0) {
                return;
            }

            // How we handle the Activity depends on what kind of performer it
            // is. If the Activity represents a sub-flow, don't touch it.
            // If the performer is automatic, block the activity to force a
            // manual retry. If the performer is manual, just mark it "ready".

            String perfTypeCd = activity.getPerformerTypeCd();

            if (perfTypeCd.equals(Activity.SUBFLOW_PERFORMER)) {
                return;
            } else if (perfTypeCd.equals(Activity.AUTOMATIC_PERFORMER)) {
                blockActivity();
            } else if (perfTypeCd.equals(Activity.MANUAL_PERFORMER)) {
                // Mark this Activity as "Ready".

                Timestamp ts = new Timestamp(System.currentTimeMillis());
                long curMillis = System.currentTimeMillis();

                if (activity.getUserId() != null) {
                    activity.setStatusCd(Activity.IN_PROCESS);
                    activity.setStartDt(ts);
                    long expDurMs = activity.getExpectedDuration();
                    long jeoDurMs = activity.getJeopardyDuration();

                    Timestamp dueTs = new Timestamp(curMillis + expDurMs);
                    Timestamp jeoTs = new Timestamp(curMillis + jeoDurMs);

                    activity.setDueDt(dueTs);
                    activity.setJeopardyDt(jeoTs);
                } else {
                    activity.setStatusCd(Activity.PENDING);
                    activity.setStartDt(null);
                    activity.setDueDt(null);
                }

                activity.setEndDt(null);
                activity.setReadyDt(ts);
            }

            // Save the Activity to the database.

            try {
                Controller.logger.debug(": ActivityUndoController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
                return;
            }
        }

        // Get the parent ProcessFlow and then get all of the outbound
        // transitions from this Activity. If we don't have any outbound
        // transistions, then something weird is going on, but we don't care.

        ProcessFlow parent = activity.getContainer();
        Transition[] outbounds = parent.getOutboundTransitions(activity);

        //
        // Special Case - Do not follow loops
        if (Activity.LOOP.equals(activity.getOutTransitionCode())) {
            return;
        }

        if ((outbounds != null) && (outbounds.length > 0)) {
            Activity downstream;
            // For each outbound Activity, send an "unskip" control to it.
            for (Transition tt : outbounds) {
                Integer downstreamId = tt.getToId();
                downstream = parent.getActivity(downstreamId);
                IController c = Controller.createController(
                        WorkFlowEngine.ACTION_UNSKIP, downstream);
                c.execute(resp, new WorkFlowRequest());
            }
        }
    }
}
