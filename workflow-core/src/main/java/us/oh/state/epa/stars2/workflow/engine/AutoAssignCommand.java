package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: AutoAssignCommand
 * </p>
 * 
 * <p>
 * Description: Auto assigns all activities for a given workflow based on the
 * facility level roles.
 * </p>
 * 
 * @author S. Wooster
 * @version 1.0
 */
public class AutoAssignCommand extends AbstractWorkFlowCommand {
    /**
     * Constructor.
     */
    public AutoAssignCommand() {
    }

    /**
     * Framework method. Called by the workflow engine whenever the associated
     * Activity is to be automatically executed.
     * 
     * @param wfData
     *            WorkFlowCmdData
     * 
     * @throws Exception
     *             Any number of reasons.
     */
    public void execute(WorkFlowCmdData wfData) throws Exception {
        WorkFlowEngine wfe = wfData.getEngine();
        Integer processId = wfData.getProcessId();
        ProcessFlow pf = wfe.findProcessFlow(processId);
        Integer fpId = wfData.getAccountId();

        Activity[] acts = pf.getActivities();
//        WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
        WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);

        for (Activity a : acts) {
            workFlowBO.autoAssignTask(fpId, a.getRoleCd(), a
                    .getProcessActivity(), pf.getUserId(),null);
            
            a.setProcessActivity(workFlowBO.modifyProcessActivity(a
                    .getProcessActivity()));
        }
    }
}
