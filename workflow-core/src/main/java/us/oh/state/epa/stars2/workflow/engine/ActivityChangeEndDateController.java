package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class ActivityChangeEndDateController extends ActivityController {
    /**
     * Constructor. Activity to be re-assigned.
     * 
     * @param a
     *            Activity to be re-assigned.
     */
    public ActivityChangeEndDateController(Activity a) {
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
        Timestamp endDt = rqst.getEndDt();
        synchronized (activity) {
            try {
                WriteWorkFlowService workFlowBO = 
                		App.getApplicationContext().getBean(WriteWorkFlowService.class);
                activity.setEndDt(endDt);
                workFlowBO.modifyProcessActivityEndDate(activity.getProcessActivity());

            } catch (Exception e) {
                Controller.logger.error(e.getMessage(), e);
                wfr.addFailure(e.getMessage());
            }
        }

    }
}
