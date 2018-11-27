package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: SubFlowActivityReadyController
 * </p>
 * 
 * <p>
 * Description: Readies an Activity that has a sub-flow ProcessFlow. In this
 * case, the Activity is more of a "placeholder" in its containing ProcessFlow
 * for the sub-flow ProcessFlow. It responds to the "ready" command by setting
 * the state of the Activity to "Subflow in Process" and then sends a "Ready"
 * command to the sub-flow.
 * </p>
 * 
 * <p>
 * This class validates the beginning state of the Activity, i.e., makes sure
 * the Activity is in a state that can be readied. It then updates the state of
 * the Activity and sends a "ready" command on to the sub-flow ProcessFlow.
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

public class SubFlowActivityReadyController extends ActivityController {
    /**
     * Constructor. Takes a reference to the Activity to be readied.
     * 
     * @param a
     *            Sub-flow Activity to be readied.
     */
    public SubFlowActivityReadyController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Changes the state of the Activity to "subflow in
     * process" and then sends a "ready" command to the subflow process. State
     * is persisted to the database. Any error or failure messages generated are
     * added to "resp".
     * 
     * @param resp
     *            WorkFlowResponse where error and failure messages are added.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // First, ready this Activity. If this fails (due to bad input
        // state), then just return.

        Timestamp startDt = rqst.getStartDt();
        synchronized (activity) {
            if (!readyActivity(resp, startDt)) {
                return;
            }

            // Set the status of this Activity to "subflow in process" and set
            // start date and ready date Timestamps to "now".
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (startDt != null && startDt.getTime() != 0)
                now = startDt;

            activity.setStatusCd(Activity.SUBFLOW_IN_PROCESS);
            activity.setStartDt(now);
            activity.setReadyDt(now);

            // Compute an expected duration for this Activity by adding the
            // expected duration to our starting Timestamp. Use this sum to
            // set the due date.

            long expDur = activity.getExpectedDuration();
            Timestamp dueDate = new Timestamp(expDur + now.getTime());
            activity.setDueDt(dueDate);

            // Save the current state of this Activity to the database. If that
            // fails, we won't send the "ready" command to the subflow. Failure
            // is not regarded as serious since the database state can be
            // recovered.

            try {
                Controller.logger.debug(": SubFlowActivityReadyController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();

                // Send the ready command to the subflow.

                ProcessFlow subflow = activity.getSubFlow();

                IController cc = Controller.createController(
                        WorkFlowEngine.ACTION_READY, subflow);
                cc.execute(resp, new WorkFlowRequest());
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
            }
        }
    }
}
