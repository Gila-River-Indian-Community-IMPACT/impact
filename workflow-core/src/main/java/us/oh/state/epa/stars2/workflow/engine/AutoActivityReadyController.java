package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: AutoActivityReadyController
 * </p>
 * 
 * <p>
 * Description: This class performs the "Ready" operation on an Activity whose
 * "performer type code" is "automatic". In this case, the Activity has an
 * associated automatic class that is instantiated and allowed to perform some
 * action. When the automatic class completes successfully, then this Activity
 * is changed to "Completed" and "Ready" commands are sent to all downstream
 * Activities. If the automatic class fails, this the Activity state is changed
 * to "Blocked" and the performer type code is change to "manual" so that a user
 * can retry this action after the problem is fixed.
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

public class AutoActivityReadyController extends ActivityController {
    /**
     * Constructor. Takes a reference to the automatic Activity to be readied.
     * The Activity is not validated to insure that it is an automatic Activity.
     * 
     * @param a
     *            Automatic Activity to be readied.
     */
    public AutoActivityReadyController(Activity a) {
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
        boolean success = false;

        // If (this Activity could not be marked "Ready") then do not proceed.
        synchronized (activity) {
            if (readyActivity(resp, rqst.getStartDt())) {
                // Change the status on the Activity to prevent another user
                // from
                // triggering the automatic command while we are executing it.
                activity.setStatusCd(Activity.PENDING);
                activity.setUserId(WorkFlowEngine.WORKFLOW_SYSTEM);

                Timestamp now = new Timestamp(System.currentTimeMillis());
                activity.setReadyDt(now);

                // Attempt to execute the automatic command. If that succeeds,
                // then
                // mark this Activity as "Completed". If it fails, "Block" this
                // Activity and set it for manual retry.
                try {
                    if (executeAutomaticCommand(resp)) {
                        completeActivity();
                        success = true;
                    } else {
                        blockActivity();
                    }

                    // Save the Activity's state to the database.
                    Controller.logger.debug(": AutoActivityReadyController.execute(): process_id=" + 
                			activity.getProcessId() + ", activity_template_id=" + 
                			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                    saveToDatabase();
                } catch (DAOException de) {
                    // If there was a problem with the database update, we go no
                    // further.
                    Controller.logger.error(de.getMessage(), de);
                    resp.addFailure(de.getMessage());
                }
            }
        }

        // If we successfully executed the automatic class, then create
        // a
        // "Complete" controller and let it change the state of our
        // Activity
        // to "Complete". This will also send "Ready" commands to any
        // downstream Activity objects.
        if (success) {
            IController c = Controller.createController(
                    WorkFlowEngine.ACTION_COMPLETE, activity);
            c.execute(resp, rqst, dataValues);
        } else {
            // If the automatic class failed, see if we need to
            // automatically
            // retry it. If we have retries left on the Activity, create
            // and
            // start a thread to wait a bit and then retry the automatic
            // command.
            Integer retryCnt = activity.getNumberOfRetries();

            // If no retries are expected, then just return.
            if ((retryCnt != null) && (retryCnt.intValue() != 0)) {
                AutoActivityRetryThread aart = new AutoActivityRetryThread(
                        activity);
                Thread tt = new Thread(aart, "AutoActivityReadyController-aart");
                tt.setDaemon(true);
                tt.start();
            }
        }
    }
}
