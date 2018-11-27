package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/**
 * <p>
 * Title: DynamicSubFlowsCommand
 * </p>
 * 
 * <p>
 * Description: This is a base class that provides services for classes that
 * need to create dynamic sub-flows or workflows in the workflow engine.
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
abstract public class DynamicSubFlowsCommand extends AbstractWorkFlowCommand {
    protected Integer subFlowActTemplId;
    protected Integer sfProcessTemplId;
    protected ActivityTemplateDef sfDef;

    /**
     * Constructor.
     */
    public DynamicSubFlowsCommand() {
    }

    /**
     * Framework method. Derived classes must override this method.
     * 
     * @param wfData
     *            WorkFlowCmdData WorkFlow data.
     * 
     * @throws Exception
     *             Something went wrong.
     */
    abstract public void execute(WorkFlowCmdData wfData) throws Exception;

    /**
     * Returns the <code>ActivityTemplateDef</code> object that represents the
     * sub-flow to be creating to continue provisioning this service. The
     * sub-flow is constructed in the workflow designer. The name of the
     * sub-flow is associated with an Activity in the workflow (as an
     * "activity_data" value on an Activity_Template record in the database).
     * This method looks up this value, finds the corresponding sub-flow
     * definition, and returns that definition to the caller.
     * 
     * @param wfData
     *            WorkFlowCmdData Workflow command data.
     * 
     * @return ActivityTemplateDef The sub-flow definition object.
     * 
     * @throws Exception
     *             Missing/incorrect value or database access error.
     */
    protected ActivityTemplateDef getSubflowActivity(WorkFlowCmdData wfData)
            throws Exception {
        // First, find the ActivityTemplate object for the step we are
        // currently executing in the database. Then, get the name of the
        // sub-flow from its "activity_data" field value.

//        ReadWorkFlowService wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
    	ReadWorkFlowService wfBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
        Integer actTemplId = wfData.getActivityId();

        ActivityTemplate currentAt = wfBO.retrieveActivityTemplate(actTemplId);
        String subflowName = currentAt.getActivityData();

        // If we don't have a sub-flow name, be very explicit in the
        // Exception we throw.

        if ((subflowName == null) || (subflowName.length() == 0)) {
            throw new Exception("The name of the subflow was not identified "
                    + "(activity_data) for ActivityTemplate name = ["
                    + currentAt.getActivityTemplateNm() + "], ID = ["
                    + actTemplId.toString() + "].");
        }

        // We have a sub-flow name. See if we can retrieve a sub-flow object
        // for that name. If not, again be explicit in the Exception.

        sfDef = wfBO.retrieveSubFlowDef(subflowName);

        if (sfDef == null) {
            throw new Exception("Subflow Definition for name = [" + subflowName
                    + "] was not found in the Activity_Template_Def "
                    + "table.");
        }

        subFlowActTemplId = sfDef.getActivityTemplateDefId();
        sfProcessTemplId = sfDef.getProcessTemplateId();

        return sfDef; // We found it!
    }

    /**
     * A <code>WorkFlowResponse</code> object is often returned by the
     * workflow engine. Extract any useful information from it and save it.
     * 
     * @param resp
     *            WorkFlowResponse From the workflow engine.
     */
    protected void logMessages(WorkFlowResponse resp) {
        String[] errorMsgs = resp.getErrorMessages();
        // Error messages.
        if ((errorMsgs != null) && (errorMsgs.length > 0)) {
            for (String tempMsg : errorMsgs) {
                addErrorMessage(tempMsg);
            }
        }

        // Failure messages.
        String[] failureMsgs = resp.getFailureMessages();

        if ((failureMsgs != null) && (failureMsgs.length > 0)) {
            for (String tempMsg : failureMsgs) {
                addErrorMessage(tempMsg);
            }
        }

        // Info messages.
        String[] infoMsgs = resp.getInfoMessages();

        if ((infoMsgs != null) && (infoMsgs.length > 0)) {
            for (String tempMsg : infoMsgs) {
                addInfoMessage(tempMsg);
            }
        }

        // Recommendation messages.
        String[] reccMsgs = resp.getRecommendationMessages();

        if ((reccMsgs != null) && (reccMsgs.length > 0)) {
            for (String tempMsg : reccMsgs) {
                addInfoMessage(tempMsg);
            }
        }
    }
}
