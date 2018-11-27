package us.oh.state.epa.stars2.workflow.engine;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: CreateSubFlowsController
 * </p>
 * 
 * <p>
 * Description: Controller to create dynamic sub-flows for an Activity.
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
public class CreateSubFlowsController extends ActivityController {
    /**
     * Constructor. The Activity "act" will become the parent Activity for the
     * sub-flows that are created.
     * 
     * @param act
     *            Activity Parent for sub-flows.
     */
    public CreateSubFlowsController(Activity act) {
        super(act);
    }

    /**
     * Framework method. Executed by the workflow engine to create sub-flows.
     * Response messages are appended to "resp".
     * 
     * @param resp
     *            WorkFlowResponse Response messages are added to
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        boolean success = false;

        // If (this Activity could not be marked "Ready") then do not proceed.
        synchronized (activity) {
            Timestamp startDt = rqst.getStartDt();
            if (readyActivity(resp, startDt)) {
                // Change the status on the Activity to prevent another user
                // from
                // triggering the automatic command while we are executing it.
                activity.setStatusCd(Activity.PENDING);
                activity.setUserId(WorkFlowEngine.WORKFLOW_SYSTEM);

                Timestamp now = new Timestamp(System.currentTimeMillis());
                if (startDt != null && startDt.getTime() != 0)
                    now = startDt;
                
                activity.setReadyDt(now);

                // Attempt to execute the automatic command. If that succeeds,
                // then mark this Activity as a sub-flow parent. If it fails,
                // "Block" this Activity and set it for manual retry.
                try {
                    if (executeAutomaticCommand(resp)) {
                        activity.setStartDt(now);
                        activity.setStatusCd("IP");
                        activity.setPerformerTypeCd("S");
                        success = true;
                    } else {
                        blockActivity();
                    }

                    // Save the Activity's state to the database. Also, save the
                    // Process and Parent process state to the database.
                    Controller.logger.debug(": CreateSubFlowsController.execute(): process_id=" + 
                			activity.getProcessId() + ", activity_template_id=" + 
                			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                    saveToDatabase();

                    ProcessFlow process = activity.getContainer();
                    ProcessFlow parent = process.getParentProcess();

//                    WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
                    WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
                    process = workFlowBO.updateProcessFlow(process);
                    parent = workFlowBO.updateProcessFlow(parent);
//                } catch (ServiceFactoryException sfe) {
//                    Controller.logger.error(sfe.getMessage(), sfe);
                } catch (RemoteException re) {
                    // If there was a problem with the database update, we go no
                    // further.
                    Controller.logger.error(re.getMessage(), re);
                    resp.addFailure(re.getMessage());
                }

                // If we successfully executed the automatic class, then create
                // a
                // "Complete" controller and let it change the state of our
                // Activity
                // to "Complete". This will also send "Ready" commands to any
                // downstream Activity objects.
                if (success) {
                    ProcessFlow subflow = activity.getSubFlow();

                    if (subflow == null) {
                        Controller.logger.error("Sub-Flow process not found.");
                    } else {

                        IController ic = Controller.createController(
                                WorkFlowEngine.ACTION_READY, subflow);
                        ic.execute(resp, new WorkFlowRequest());
                    }
                }
            }
        }
    }
}
