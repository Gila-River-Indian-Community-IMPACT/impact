package us.oh.state.epa.stars2.workflow.engine;

import java.util.HashMap;

import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class CreateTaskProcessFlowsController extends ProcessFlowController {
	
	private final String TASK_USERID = "Task UserId";
	
    /**
     * Constructor. Takes a reference to the ProcessFlow to be readied.
     * 
     * @param pf
     *            ProcessFlow to be readied.
     */
    public CreateTaskProcessFlowsController(ProcessFlow pf) {
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
    	HashMap<String,String> data = new HashMap<String,String>();
    	String taskUserId = null;
    	for (int i = 0; i < rqst.getDataCount(); i++) {
    		String name = rqst.getDataName(i);
    		if (TASK_USERID.equals(name)) {
    			taskUserId = rqst.getDataValue(i);
    		} else {
    			data.put(name,rqst.getDataValue(i));
    		}
    	}
    	
        synchronized (processFlow) {
        	Controller.logger.debug(" Executing ProcessReadyController");
        	
        	WriteWorkFlowService wfBO = 
        			App.getApplicationContext().getBean(WriteWorkFlowService.class);
            try {
				wfBO.createTaskProcessFlows(rqst.getWorkFlow(), rqst.getAccountId(), 
						rqst.getOrderId(), rqst.getExpedite(), rqst.getStartDt(), 
						null, rqst.getUserId(), data, Integer.parseInt(taskUserId),null);
			} catch (Exception e) {
                Controller.logger.error(e.getMessage(), e);
                resp.addError(e.getMessage());
			}
        }
    }
}
