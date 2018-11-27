package us.oh.state.epa.stars2.workflow.engine;

import java.util.HashMap;

import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: AutoAssignCommand
 * </p>
 * 
 * <p>
 * Description: Starts a new workflow and sets the start date of the child
 * workflow to the start date of the spawning workflow.
 * </p>
 * 
 * @author S. Wooster
 * @version 1.0
 */
public class AutoCreateWorkflowCommand extends AbstractWorkFlowCommand {
    /**
     * Constructor.
     */
    public AutoCreateWorkflowCommand() {
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
        IController c = null;
        WorkFlowResponse resp = new WorkFlowResponse();

        WorkFlowRequest command = new WorkFlowRequest();
        command.setAccountId(wfData.getAccountId());
        command.setUserId(pf.getUserId());
        command.setOrderId(pf.getExternalId());
        command.setExternalId(pf.getExternalId());
        command.setExpedite(pf.getExpedite());
        command.setDueDt(pf.getDueDt());
        command.setStartDt(pf.getStartDt());
        command.setProcessId(processId);

        //
        // The workflow template to choose will be set and stored in
        // the workflow data.
        Integer ptid = new Integer(101);
        command.setProcessId(ptid);

        // wfe.createProcess(command);
//        WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
        WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
        ProcessFlow[] processes = null;

        try {
            processes = workFlowBO.createProcessFlows(ptid, wfData
                    .getAccountId(), pf.getExternalId(), pf.getExpedite(), pf
                    .getStartDt(), pf.getDueDt(), pf.getUserId(),null);

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
//        wfe.addProcessFlowsToMap(processes);

        if (processes != null) {
            c = WorkFlowEngine.createController(WorkFlowEngine.ACTION_READY,
                    processes[0]);
            HashMap<String, String> dataValues = wfe.extractDataValues(command);
            c.execute(resp, command, dataValues);
        }
    }
}
