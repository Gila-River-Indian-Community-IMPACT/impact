package us.wy.state.deq.impact.database.dbObjects.workflow;

import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.PermitReasonsDef;

@SuppressWarnings("serial")
public class ImpactWorkFlowProcess extends WorkFlowProcess {
	
	public ImpactWorkFlowProcess(WorkFlowProcess old) {
		super(old);
	}


	// decouple workflow ... 
	public final String getPermitReasonDsc() {
		String permitReasonDsc = "";
		if (getPermitReasonCd() != null) {
			permitReasonDsc = PermitReasonsDef.getData().getItems().getItemDesc(getPermitReasonCd());
		}
		return permitReasonDsc;
//		throw new RuntimeException("decouple workflow");
	}
}
