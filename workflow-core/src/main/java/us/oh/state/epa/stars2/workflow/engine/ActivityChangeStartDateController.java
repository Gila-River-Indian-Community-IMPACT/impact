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

public class ActivityChangeStartDateController extends ActivityController {
    /**
     * Constructor. Activity to be re-assigned.
     * 
     * @param a
     *            Activity to be re-assigned.
     */
    public ActivityChangeStartDateController(Activity a) {
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
        Timestamp startDt = rqst.getStartDt();
        synchronized (activity) {
            activity.setStartDt(startDt);
            try {
                Controller.logger.debug(": ActivityChangeStartDateController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                wfr.addFailure(de.getMessage());
            }
        }

        if (activity.isInitTask() && activity.getLoopCnt().equals(1)) {
            rqst.setActivityId(null);
            IController c = WorkFlowEngine.createController(
                    WorkFlowEngine.ACTION_CHANGE_START_DATE, activity
                            .getContainer());

            c.execute(wfr, rqst);
        }
    }
}
