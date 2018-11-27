package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: ProcessReadyController
 * </p>
 * 
 * <p>
 * Description: Marks ProcesFlow as ready by finding its initial Activity (or
 * Activities) and marking them as ready. Updates the start date on the
 * ProcessFlow to "now" and saves this value to the database.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */

class ProcessReadyController extends ProcessFlowController {
    /**
     * Constructor. Takes a reference to the ProcessFlow to be readied.
     * 
     * @param pf
     *            ProcessFlow to be readied.
     */
    public ProcessReadyController(ProcessFlow pf) {
        super(pf);
    }

    /**
     * Framework method. Sets the start date for the ProcessFlow and readies all
     * initial Activities (there is probably only one).
     * 
     * @param resp
     *            Used to add failure or error messages.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        synchronized (processFlow) {
        	Controller.logger.debug(" Executing ProcessReadyController");
        	
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp readyDt = processFlow.getReadyDt();

            // Check to see if this process has a ready date. If so, then it
            // cannot be readied until that date is past. Put it in a thread
            // and let the thread take care of readying the process.
            if ((readyDt != null) && (now.before(readyDt))) {
                String processName = processFlow.getProcess()
                        .getProcessTemplateNm();

                Controller.logInfoMessage("Creating Process Ready Thread for "
                        + "ProcessName = [" + processName + "].");

                AutoProcessReadyThread aprt = new AutoProcessReadyThread(
                        processFlow, readyDt);

                Thread tt = new Thread(aprt, "ProcessReadyController-aprt");
                tt.setDaemon(true);
                tt.start();

                // Set the status on the parent activity so the system user
                // can see that this process is not yet ready to be provisioned.
                Activity parent = processFlow.getParentActivity();
                parent.setStatusCd("NRDY");

                try {
//                	WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
                    WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
                    workFlowBO.updateActivity(parent);
                } catch (Exception e) {
                    Controller.logger.error(e.getMessage(), e);
                }

                return;
            }

            // If we get here, this process is ready to be readied. Do it.
            processFlow.setReadyDt(null);
            Timestamp startDt = processFlow.getStartDt();
            if (startDt == null || startDt.getTime() == 0) {
                if (rqst.getStartDt().getTime() != 0)
                    now = rqst.getStartDt();
                processFlow.setStartDt(now);
            }

            try {
            	Controller.logger.debug(" Saving process flow to database");
                saveToDatabase(rqst.getTransaction());
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addError(de.getMessage());
                return;
            }
        }

        // Get an array of all of the Activity objects associated with this
        // Procss Flow. If it has no Activities, then we are done.
        Activity[] activities = processFlow.getActivities();

        if ((activities != null) && (activities.length > 0)) {
            // For (each Activity) see if the Activity is an initial task.
            // If so, ready it.
            for (Activity a : activities) {
                if (a.isInitTask()) { // Ignore the Activity if it is not
                    readyActivity(a, rqst, resp); // Ready the initial task.
                }
            }
        }
    }

    /**
     * Sets a "Ready" command to an Activity. This Activity should be an initial
     * task, but this is not validated by this method. Any failures or errors
     * encountered along the way will be added to "resp".
     * 
     * @param a
     *            Activity to be readied.
     * @param rqst
     * @param resp
     *            Response object for failure or error messages.
     */
    protected void readyActivity(Activity a, WorkFlowRequest rqst,
            WorkFlowResponse resp) {
        IController c = null;
        c = Controller.createController(WorkFlowEngine.ACTION_READY, a);
        c.execute(resp, rqst, dataValues);
    }
}
