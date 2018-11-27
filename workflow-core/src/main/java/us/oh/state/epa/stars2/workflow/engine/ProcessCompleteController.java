package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ProcessCompleteController
 * </p>
 * 
 * <p>
 * Description: This controller handles reception of a "Complete" command for
 * this ProcessFlow.
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

public class ProcessCompleteController extends ProcessFlowController {
    /**
     * Constructor. Takes a reference to the ProcessFlow being completed.
     * 
     * @param pf
     *            ProcessFlow being completed.
     */
    public ProcessCompleteController(ProcessFlow pf) {
        super(pf);
    }

    /**
     * Framework method. Marks this ProcessFlow as "completed" and saves its
     * state to the database. Also removes the ProcessFlow from the in-memory
     * table of active ProcessFlows. If this ProcessFlow is the sub-flow of an
     * Activity, sends a "Complete" command to the parent Activity. Error and
     * failure messages are added to "resp".
     * 
     * @param resp
     *            WorkFlowResponse that gets error and failure messages.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // Set the end date on the process flow and save it to the database.
        synchronized (this.processFlow) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            this.processFlow.setEndDt(now);

            try {
                this.saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
                return;
            }
        }
        // Remove this ProcessFlow from the table of active ProcessFlows.
        // Then, see if we have a parent Activity for this ProcessFlow.
//        WorkFlowEngine._workFlows.remove(this.processFlow.getProcessId());
        Activity parentAct = this.processFlow.getParentActivity();

        if (parentAct == null) { // If there is no parent activity
            Integer orderId = this.processFlow.getExternalId();
            String msg = "Order #" + orderId.toString() + " is completed.";
            resp.addInfoMsg(msg);
        } else {

            // Create a Controller for the parent Activity and send it a
            // "Complete" command.

            IController c = Controller.createController(
                    WorkFlowEngine.ACTION_COMPLETE, parentAct);
            c.execute(resp, new WorkFlowRequest());
        }
    }
}
