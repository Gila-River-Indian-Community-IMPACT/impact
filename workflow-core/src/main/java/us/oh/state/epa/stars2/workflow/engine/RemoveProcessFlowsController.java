package us.oh.state.epa.stars2.workflow.engine;

import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class RemoveProcessFlowsController extends ProcessFlowController {
    /**
     * Constructor. Takes a reference to the ProcessFlow to be readied.
     * 
     * @param pf
     *            ProcessFlow to be readied.
     */
    public RemoveProcessFlowsController(ProcessFlow pf) {
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
        try {
            WriteWorkFlowService workFlowBO = 
            		App.getApplicationContext().getBean(WriteWorkFlowService.class);
            workFlowBO.removeProcessFlows(rqst.getProcessId(), rqst.getUserId());

        } catch (Exception e) {
            Controller.logger.error(e.getMessage(), e);
            resp.addFailure(e.getMessage());
        }
    	
    }
}