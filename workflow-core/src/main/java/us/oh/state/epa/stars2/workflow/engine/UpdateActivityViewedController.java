package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class UpdateActivityViewedController extends ActivityController {

	public UpdateActivityViewedController(Activity a) {
		super(a);
	}

	@Override
	public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {

		ProcessActivity pa = activity.getProcessActivity();
		WriteWorkFlowService writeWorkflowService = App.getApplicationContext().getBean(WriteWorkFlowService.class);
        try {
        	writeWorkflowService.updateProcessActivityViewedState(pa);
        	Controller.logger.debug("process activity viewed state updated");
        } catch (Exception de) {
            Controller.logger.error(de.getMessage(), de);
            resp.addFailure(de.getMessage());
        }
		
	}

}
