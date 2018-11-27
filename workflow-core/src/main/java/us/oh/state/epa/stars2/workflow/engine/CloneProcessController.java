package us.oh.state.epa.stars2.workflow.engine;

import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class CloneProcessController extends ProcessFlowController {
	
    /**
     * Constructor. Takes a reference to the ProcessFlow to be readied.
     * 
     * @param pf
     *            ProcessFlow to be readied.
     */
    public CloneProcessController(ProcessFlow pf) {
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

        synchronized (processFlow) {
        	Controller.logger.debug(" Executing CloneProcess");
        	
        	WriteWorkFlowService wfBO = 
        			App.getApplicationContext().getBean(WriteWorkFlowService.class);
            try {
				wfBO.cloneProcess(rqst.getProcessId(), rqst.getUserId(), rqst.getExternalId());
			} catch (Exception e) {
                Controller.logger.error(e.getMessage(), e);
                resp.addError(e.getMessage());
			}
        }
    }
}
