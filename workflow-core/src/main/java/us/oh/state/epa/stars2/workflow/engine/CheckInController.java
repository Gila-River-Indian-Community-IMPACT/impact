package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: CheckInController
 * </p>
 * 
 * <p>
 * Description: Handles check-in for an Activity. The check-in request is
 * assumed to be valid. All service detail data is updated (if any) and the
 * Activity is updated to "Complete". Also, any downstream Activities or
 * ProcessFlows that need to be readied are readied.
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

public class CheckInController extends ActivityController {
    /**
     * Constructor.
     * 
     * @param a
     *            Activity to be checked in.
     */
    public CheckInController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Marks the Activity input via the constructor as "done"
     * and saves this state to the database. Then, creates a "Completion"
     * Controller to figure out what to do next.
     * 
     * @param resp
     *            WorkFlowResponse being saved to return to calling application.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // Save any service detail data to the database. Update the
        // Activity state to "Completed" and save it to the database.
        // If any of these steps fails, then save the error message and
        // return.
        synchronized (activity) {
            String statusCd = activity.getStatusCd();
            Timestamp endDt = activity.getEndDt();
            try {
                saveServiceData();
                completeActivity();
                if (activity.getActivityTemplate().getMilestoneInd()
                        .equalsIgnoreCase("Y")) {
//                    makeEventLog(); //decouple workflow
                }
                Controller.logger.debug(": CheckInController.execute(): process_id=" + activity.getProcessId() + 
                        ", activity_template_id=" + activity.getActivityTemplateId() +
                        ", userId=" + activity.getUserId() +
            			", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                activity.setStatusCd(statusCd);
                activity.setEndDt(endDt);
                Controller.logger.error(de.getMessage(), de);
                resp.addError(de.getMessage());
                return;
            }
        }
        // If we get here, create a "Complete" controller to figure out who
        // cares about our recent success.
        IController c = Controller.createController(
                WorkFlowEngine.ACTION_COMPLETE, activity);
        c.execute(resp, new WorkFlowRequest());
    }
}
