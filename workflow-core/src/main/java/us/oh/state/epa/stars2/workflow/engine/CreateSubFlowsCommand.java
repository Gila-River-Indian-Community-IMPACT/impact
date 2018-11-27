package us.oh.state.epa.stars2.workflow.engine;

//import us.oh.state.epa.stars2.database.db.order.OrderService;
import us.oh.state.epa.stars2.bo.WriteWorkFlowBO;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: CreateSubFlowsCommand
 * </p>
 * 
 * <p>
 * Description: A workflow engine "plug-in" that looks at the quantity of an
 * order service, and then spawns a sub-flow for each service. In addition, new
 * order service objects are created to track each service as it progresses
 * through provisioning.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */
public class CreateSubFlowsCommand extends DynamicSubFlowsCommand {
    // The "No-Op Branch" Activity Template is used as both an initial and
    // final Activity. The sub-flows will exist as services between these
    // two Activities.
    static final String NO_OP_TEMPLATE_NAME = "No-Op Branch";
    static final int NO_OP_TEMPLATE_ID = 5;

    /**
     * Constructor.
     */
    public CreateSubFlowsCommand() {
    }

    /**
     * Framework method. This is the method that will be called by the workflow
     * engine whenever it is time to create dynamic sub-flows. The "wfData"
     * objects contains workflow process and order information needed to create
     * the subflows.
     * 
     * @param wfData
     *            WorkFlowCmdData Workflow information.
     * 
     * @throws Exception
     *             Usually, database access error.
     */
    public void execute(WorkFlowCmdData wfData) throws Exception {
    }

    /**
     * Creates a composite process template to "glue" the sub-flows together.
     * 
     * @param type
     *            String Order type (usually provisioning).
     * @param extId
     *            Integer The Order Id of the order being processed.
     * @param map
     *            ServiceTemplateMap[] OrderService to ProcessTemplate map.
     * 
     * @return ProcessTemplate A process template for these subflows.
     * 
     * @throws Exception
     *             Database access error.
     */
    public ProcessTemplate createCompositeTemplate(String type, Integer extId,
            ServiceTemplateMap[] map) throws Exception {
//        WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
        WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
        return workFlowBO.createCompositeTemplate(type, extId, map, true);
    }

    /**
     * Hooks a sub-flow identified by "pf" to its parent Activity, identified by
     * its process Id and activity Id. This relationship is updated both in the
     * workflow engine and in the database.
     * 
     * @param processId
     *            Integer Process Id of the parent Activity.
     * @param activityId
     *            Integer Activity template Id of the parent Activity.
     * @param pf
     *            ProcessFlow The sub-flow process.
     * @param engine
     *            WorkFlowEngine The work-flow engine containing current
     *            processes.
     * 
     * @throws Exception
     *             Database access error.
     */
    protected void connectSubflow(Integer processId, Integer activityId,
            ProcessFlow pf, WorkFlowEngine engine) throws Exception {
        // Find the parent activity and process in the workflow engine.

        Activity parentAct = engine.findActivity(processId, activityId);
        ProcessFlow parentPF = engine.findProcessFlow(processId);

        // Make "pf" a sub-flow of the parent process and activity.

        pf.setParentProcess(parentPF);
        pf.setParentActivity(parentAct);
        parentAct.setSubFlow(pf);

        // Update everybody that needs it to the database.

        WriteWorkFlowBO workFlowBO = new WriteWorkFlowBO();
        workFlowBO.updateActivity(parentAct);
        pf = workFlowBO.updateProcessFlow(pf);
    }
}
