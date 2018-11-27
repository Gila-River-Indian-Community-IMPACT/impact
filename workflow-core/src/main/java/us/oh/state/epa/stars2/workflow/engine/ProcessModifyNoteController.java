package us.oh.state.epa.stars2.workflow.engine;

import java.rmi.RemoteException;
import java.util.Map;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class ProcessModifyNoteController extends ProcessFlowController {

	public ProcessModifyNoteController(ProcessFlow pf) {
		super(pf);
	}

	@Override
	public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
		
		Map<String,String> data = mapRequestData(rqst);
		
		WriteWorkFlowService writeWorkFlowService = 
				App.getApplicationContext().getBean(WriteWorkFlowService.class);
		ProcessNote note = new ProcessNote();
		note.setLastModified(Integer.parseInt(data.get("lastModified")));
		note.setNoteId(Integer.parseInt(data.get("noteId")));
		note.setNote(data.get("value"));
		try {
			writeWorkFlowService.modifyProcessNote(note);
		} catch (RemoteException re) {
            Controller.logger.error(re.getMessage(), re);
            resp.addError(re.getMessage());
		}
		
	}

	private Map<String,String> mapRequestData(WorkFlowRequest rqst) {
		for (int i = 0; i < rqst.getDataCount(); i++) {
			getDataMap().put(rqst.getDataName(i),rqst.getDataValue(i));
		}
		return getDataMap();
	}
	

}
