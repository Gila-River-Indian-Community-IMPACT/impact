package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivityRetryController
 * </p>
 * 
 * <p>
 * Description:
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

public class ActivityRetryController extends ActivityController {
    public ActivityRetryController(Activity a) {
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
        // Change the status on the Activity to prevent another user from
        // triggering the automatic command while we are executing it.

        boolean success = true;

        synchronized (activity) {
            activity.setStatusCd(Activity.PENDING);
            activity.setUserId(WorkFlowEngine.WORKFLOW_SYSTEM);

            // Attempt to execute the automatic command. If that succeeds, then
            // mark this Activity as "Completed". If it fails, "Block" this
            // Activity and set it for either automatic retry (if we non-zero
            // retry attempts) or manual retry.

            try {
                if (executeAutomaticCommand(resp)) {
                    completeActivity(); // This activity is done
                    success = true;
                } else {
                    success = false;
                    setNumberOfRetries(); // Update the retry attempts
                    blockActivity(); // "Block" the Activity
                }

                // Save the Activity's state to the database.

                Controller.logger.debug(": ActivityRetryController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                // If there was a problem with the database update, we go no
                // further.

                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
                return;
            }
        }

        // If we successfully executed the automatic class, then create a
        // "Complete" controller and let it change the state of our Activity
        // to "Complete". This will also send "Ready" commands to any
        // downstream Activity objects.

        if (success) {
            IController c = Controller.createController(
                    WorkFlowEngine.ACTION_COMPLETE, activity);
            c.execute(resp, new WorkFlowRequest());
        }
    }

    /**
     * Sets the number of remaining retry attempts for this automatic activity.
     * Either sets this value to zero (if not set previously) or decrements the
     * current number of retries by one and sets that value.
     */
    protected void setNumberOfRetries() {
        // Get the current number of retries. If none is set, set it to zero.
        Integer foo = activity.getNumberOfRetries();

        if (foo == null) {
            activity.setNumberOfRetries(new Integer(0));
        } else {
            // Decrement the current retry count by one and set that as the new
            // retry count.

            int oogh = foo.intValue();
            oogh--;
            activity.setNumberOfRetries(new Integer(oogh));
        }
    }
}
