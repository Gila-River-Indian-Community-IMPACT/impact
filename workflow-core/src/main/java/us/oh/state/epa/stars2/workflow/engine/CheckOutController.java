package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: CheckOutController
 * </p>
 * 
 * <p>
 * Description: Handles a check-out request for an Activity. A "check-out"
 * request assigns this Activity to a user, and blocks other users from checking
 * this Activity out. In addition, it also retrieves any service data associated
 * with this Activity so that the user can enter values and save them. Note that
 * the workflow engine is responsible for assigning the user Id before creating
 * this controller.
 * </p>
 * 
 * <p>
 * Check-out requests really apply only to manual Activities.
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

public class CheckOutController extends ActivityController {
    /**
     * Constructor. The input Activity is the Activity being checked out.
     * 
     * @param a
     *            Activity to be checked out.
     */
    public CheckOutController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Retrieves workflow data for user editing and changes
     * the state of the Activity to "checked out" to whatever user was added to
     * the Activity by the workflow engine. Any error or failure messages
     * generated will be added to "resp". Also, service detail data is added to
     * the "WorkFlowResponse" if there is any.
     * 
     * @param resp
     *            WorkFlowResponse for adding error or failure messages.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // First, see if we can add service data to the response object.
        // An Exception here indicates a database/software problem and is
        // fatal. So do this first so we don't have to "undo" the
        // state change to the Activity.
        synchronized (activity) {
            addWorkflowData(resp);

            // If we got the service data okay, update the state on the Activity
            // and save it to the database. A failure here is not regarded as
            // fatal because we leave the database in a consistent state where
            // the Activity could still be processed normally.
            activity.setStatusCd(Activity.IN_PROCESS);
            long curMillis = System.currentTimeMillis();
            Timestamp now = new Timestamp(curMillis);
            activity.setStartDt(now);

            long expDurMs = activity.getExpectedDuration();
            long jeoDurMs = activity.getJeopardyDuration();

            Timestamp dueTs = new Timestamp(curMillis + expDurMs);
            Timestamp jeoTs = new Timestamp(curMillis + jeoDurMs);

            activity.setDueDt(dueTs);
            activity.setJeopardyDt(jeoTs);

            try {
                Controller.logger.debug(": CheckOutController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
            }
        }
    }

}
