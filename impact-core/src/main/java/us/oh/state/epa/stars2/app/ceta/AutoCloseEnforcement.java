package us.oh.state.epa.stars2.app.ceta;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.workflow.engine.AbstractWorkFlowCommand;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowCmdData;


public class AutoCloseEnforcement extends AbstractWorkFlowCommand{
	protected transient Logger logger;
	public AutoCloseEnforcement() {
	}

	public void execute(WorkFlowCmdData wfData) throws Exception {
        Integer enforcementId = wfData.getExternalId();
        
        try {
        	//EnforcementService enforcementBO = ServiceFactory.getInstance().getEnforcementService();
        	//enforcementBO.closeEnforcemet(enforcementId);
        } catch (Exception e) {
        	logger.error("Not able to close enforcement for ID " + enforcementId, e);
        }       
    }
}
