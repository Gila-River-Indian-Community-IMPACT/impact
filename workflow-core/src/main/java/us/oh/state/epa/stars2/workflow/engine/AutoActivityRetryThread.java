package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: AutoActivityRetryThread
 * </p>
 * 
 * <p>
 * Description: A "thread" to retry an automatic Activity that has been
 * attempted, but failed. The thread looks at the retry interval and the number
 * of retries to attempt. The thread sleeps for the "retry interval" and then
 * reattempts the automatic Activity. If it succeeds, we are done. If it fails,
 * the number of retry attempts is decremented. If the number of retry attempts
 * reaches zero, the Activity is placed in the "Blocked" state under manual
 * control.
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
public class AutoActivityRetryThread implements Runnable {
    private Activity act;

    /**
     * Constructor. "Blocked" is the automatic Activity that has failed.
     * 
     * @param blocked
     *            Activity failed automatic Activity.
     */
    public AutoActivityRetryThread(Activity blocked) {
        act = blocked;
    }

    /**
     * Framework method. Attempts to re-execute the automatic Activity until it
     * either succeeds or the number of retry attempts is all used up.
     */
    public void run() {
        // Get the number of retry attempts and interval.
        int retryCnt = getRetryCount();
        int retryIntvl = getRetryInterval();

        // While (we have retry attempts we can use), sleep for retry interval
        // and then attempt to execute the automatic Activity.
        while (retryCnt > 0) {
            try {
                Thread.sleep(retryIntvl);
            } catch (InterruptedException ie) {
                Controller.logger.error("Auto Activity Retry thread was interrupted.");
                Controller.logger.error(ie.getMessage(), ie);
                return;
            }

            // Lock the Activity so nobody else can mess with it.
            synchronized (act) {

                // The Activity may be in a state where we should not mess with
                // it any further, e.g., a system user requested a "retry" and
                // that "retry" succeeded. If so, then do nothing further.
                if (isValidForRetry()) {
                    // Create a Controller to handle the retry request.

                    Controller
                            .logInfoMessage("Attempting automatic retry of blocked "
                                    + "Activity: "
                                    + act.getActivityName()
                                    + ".");

                    IController retry = Controller.createController(
                            WorkFlowEngine.ACTION_RETRY, act);

                    WorkFlowResponse resp = new WorkFlowResponse();
                    retry.execute(resp, new WorkFlowRequest());

                    // If the retry failed, decrement the retry count. It has
                    // already been saved to the database by the Controller.
                    if (resp.hasFailed() || resp.hasError()) {
                        String[] msgs = resp.getErrorMessages();

                        if (msgs != null) {
                            int i;
                            for (i = 0; i < msgs.length; i++) {
                                Controller.logger.error(msgs[i]);
                            }
                        }
                        retryCnt--;
                    }
                }
            }
        }
    }

    /**
     * Get the current (or initial) retry count from the Activity. If no retry
     * count was specified, then the retry count is zero.
     * 
     * @return int Activity retry count.
     */
    protected int getRetryCount() {
        Integer foo = act.getNumberOfRetries();
        int ret = 0;

        if (foo != null) {
            ret = foo.intValue();
        }

        return ret;
    }

    /**
     * Verifies that the Activity is in a state that is valid for a retry
     * attempt. If so, returns "true". If the state is invalid, returns "false".
     * 
     * @return boolean "true" if valid for retry.
     */
    protected boolean isValidForRetry() {
        // Get the Activity status. If it is an acceptable state, return
        // "true".
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        if (aStatus.equals(Activity.BLOCKED)
                || aStatus.equals(Activity.WAIT_FOR_RETRY)) {
            ret = true;
        } else {
            // If we get here, the Activity status is not valid for retry. Log
            // a message and tell caller the sorriful answer.
            Controller.logInfoMessage("Invalid state for automatic retry: "
                    + aStatus);
        }
        return ret;
    }

    /**
     * Returns the current retry interval (in milliseconds) for this Activity.
     * If no interval value is defined in the Activity, then returns zero.
     * 
     * @return int retry interval (in milliseconds).
     */
    protected int getRetryInterval() {
        // Get the retry interval from the Activity. If it doesn't have one,
        // return zero.
        Integer foo = act.getRetryInterval();
        int ret = 0;

        if (foo != null) {
            // In the Activity, the retry interval is in "minutes". Java likes
            // "milliseconds", so it.
            int minutes = foo.intValue();
            int millisecs = minutes * 60 * 1000;

            ret = millisecs;
        }
        return ret;
    }
}
