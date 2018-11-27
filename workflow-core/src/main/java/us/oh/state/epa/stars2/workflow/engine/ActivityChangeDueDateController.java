package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivityChangeDueDateController
 * </p>
 * 
 * <p>
 * Description: Simply saves the Activity identified in the constructor to the
 * database. The workflow engine has modified the Activity in some way and the
 * only thing that needs to be done now is save it to the database. No
 * downstream activities or other other controllers are triggered by this
 * controller.
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author Poshan
 * @version 1.0
 */

public class ActivityChangeDueDateController extends ActivityController {
    /**
     * Constructor. Activity to be re-assigned.
     * 
     * @param a
     *            Activity to be re-assigned.
     */
    public ActivityChangeDueDateController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Saves the Activity to the database. Any failure or
     * error messages are added to "wfr".
     * 
     * @param wfr
     *            WorkFlowResponse for adding messages.
     */
    public void execute(WorkFlowResponse wfr, WorkFlowRequest rqst) {
        Timestamp dueDt = rqst.getDueDt();
        Timestamp jeopardyDt = rqst.getJeopardyDt();
        synchronized (activity) {
            // Change due date on this activity and process
            if (!activity.getStatusCd().equalsIgnoreCase("IP")) {
                String error = "This activity is not In Process.  The due date cannot be changed.";
                Controller.logger.error(error);
                wfr.addError(error);
                return;
            }
            if (dueDt.before(jeopardyDt)) {
                String error = "Jeopardy Date cannot be later than Due Date.";
                Controller.logger.error(error);
                wfr.addError(error);
                return;
            }

            activity.setDueDt(dueDt);
            activity.setJeopardyDt(jeopardyDt);
            try {
                Controller.logger.debug(": ActivityChangeDueDateController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage());
                wfr.addFailure(de.getMessage());
            }
        }
    }
}
