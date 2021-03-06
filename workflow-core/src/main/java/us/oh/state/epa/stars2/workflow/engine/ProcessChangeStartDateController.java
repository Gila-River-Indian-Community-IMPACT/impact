package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ProcessChangeDueDateController
 * </p>
 * 
 * <p>
 * Description: Marks ProcesFlow as ready by finding its initial Activity (or
 * Activities) and marking them as ready. Updates the start date on the
 * ProcessFlow to "now" and saves this value to the database.
 * </p>
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

class ProcessChangeStartDateController extends ProcessFlowController {
    /**
     * Constructor. Takes a reference to the ProcessFlow to be readied.
     * 
     * @param pf
     *            ProcessFlow to be readied.
     */
    public ProcessChangeStartDateController(ProcessFlow pf) {
        super(pf);
    }

    /**
     * Framework method. Sets the start date for the ProcessFlow and readies all
     * initial Activities (there is probably only one).
     * 
     * @param resp
     *            Used to add failure or error messages.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        Integer activityId = rqst.getActivityId();
        Activity a = null;

        if (activityId != null && activityId != 0) {
            a = processFlow.getActivity(activityId);
            IController c = Controller.createController(
                    WorkFlowEngine.ACTION_ACTIVITY_START_DATE, a);
            c.execute(resp, rqst);
        } else
            synchronized (processFlow) {
                Timestamp startDt = rqst.getStartDt();

                processFlow.setStartDt(startDt);
                try {
                    saveToDatabase();
                } catch (DAOException de) {
                    Controller.logger.error(de.getMessage(), de);
                    resp.addError(de.getMessage());
                    return;
                }
            }
    }
}
