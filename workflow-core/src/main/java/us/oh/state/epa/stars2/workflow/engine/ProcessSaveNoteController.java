package us.oh.state.epa.stars2.workflow.engine;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class ProcessSaveNoteController extends ProcessFlowController {

	public ProcessSaveNoteController(ProcessFlow pf) {
		super(pf);
	}

	@Override
	public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
		WriteWorkFlowService writeWorkFlowService = 
				App.getApplicationContext().getBean(WriteWorkFlowService.class);
	    ProcessNote pn = new ProcessNote();
	    pn.setProcessId(rqst.getProcessId());
	    pn.setUserId(rqst.getUserId());
	    pn.setNote(getNoteValue(rqst));
	    try {
	        writeWorkFlowService.createProcessNote(pn);
	    } catch (RemoteException re) {
            Controller.logger.error(re.getMessage(), re);
            resp.addError(re.getMessage());
	    }
	}

	// decouple workflow ... is this the proper way to pass data such as this?
	private String getNoteValue(WorkFlowRequest rqst) {
		String value = null;
		if(rqst.getDataCount() > 0) {
			for(int i = 0; i < rqst.getDataCount(); i++) {
				if(rqst.getDataName(i).equalsIgnoreCase(WorkFlowRequest.SAVE_NOTE_VALUE)) {
					value = rqst.getDataValue(i);
					break;
				}
			}
		} else {
			throw new RuntimeException("Can't find note value.");
		}
		
		if (1 == rqst.getDataCount()) {
			value = rqst.getDataValue(0);
		} 
		return value;
	}

}

