package us.oh.state.epa.stars2.workflow.engine;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: AutoProcessReadyThread
 * </p>
 * 
 * <p>
 * Description: Thread responsible for readying a ProcessFlow that is waiting to
 * be readied. The ProcessFlow is in the waiting state because it cannot begin
 * provisioning yet.
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
public class AutoProcessReadyThread implements Runnable {
    private ProcessFlow pf;
    private Timestamp readyDt;

    /**
     * Constructor.
     * 
     * @param pf
     *            ProcessFlow ProcessFlow waiting to be readied.
     * @param readyDt
     *            Timestamp The date/time it should be readied at.
     */
    public AutoProcessReadyThread(ProcessFlow pf, Timestamp readyDt) {
        this.pf = pf;
        this.readyDt = readyDt;
    }

    /**
     * Framework method. Sleeps this thread until sometime shortly after
     * "readyDt" (see constructor). When the timer expires, attempts to ready
     * "pf" (see constructor).
     */
    public void run() {
        // Sleep as long as we need, but with an extra 5 minutes or so.
        long sleepIntvl = readyDt.getTime() - System.currentTimeMillis()
                + 300000L;

        try {
            Thread.sleep(sleepIntvl);
        } catch (InterruptedException ie) {
            Controller
                    .logInfoMessage("Auto Process Ready thread was interrupted.");
            Controller.logger.error(ie.getMessage(), ie);
            return;
        }

        Controller.logInfoMessage("Time to wake process name = ["
                + pf.getProcess().getProcessTemplateNm() + "].");

        // Get a lock on the ProcessFlow. If it is ready to be "readied",
        // then create a controller to do the job for us.
        synchronized (pf) {
            if (!validForReady()) {
                return;
            }

            WorkFlowProcess pp = pf.getProcess();
            Integer processTemplateId = pp.getProcessTemplateId();
            Integer serviceId = pp.getServiceId();

            try {
                ProcessTemplate pt = retrieveProcessTemplate(processTemplateId);

//                WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
                WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);

                // Set the end and jeopardy dates for the process.
                workFlowBO.setProcessDates(pt, serviceId, pp, null, null);

                // Tell the parent Activity the process is now "in process".

                Activity parent = pf.getParentActivity();
                parent.setStatusCd("IP");

                workFlowBO.updateActivity(parent);

                // Create the controller and let it ready the process.

                IController cc = Controller.createController(
                        WorkFlowEngine.ACTION_READY, pf);

                WorkFlowResponse resp = new WorkFlowResponse();
                cc.execute(resp, new WorkFlowRequest());
//            } catch (ServiceFactoryException sfe) {
//                Controller.logger.error(sfe.getMessage(), sfe);
            } catch (RemoteException re) {
                String errMsg = re.getMessage();

                if ((errMsg == null) || (errMsg.length() == 0)) {
                    errMsg = re.getClass().getName();
                }

                Controller.logger.error(errMsg);
            }
        }
    }

    /**
     * Returns the ProcessTemplate given its process template Id. Returns "null"
     * if no matching object is found in the database.
     * 
     * @param ptId
     *            Integer Process Template Id.
     * 
     * @return ProcessTemplate Corresponding database object.
     * 
     * @throws Exception
     *             Database access error.
     */
    protected ProcessTemplate retrieveProcessTemplate(Integer ptId)
            throws DAOException {
        ProcessTemplate pt = null;

        try {
//            ReadWorkFlowService wfBO = ServiceFactory.getInstance()
//                    .getReadWorkFlowService();
        	ReadWorkFlowService wfBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
            pt = wfBO.retrieveProcessTemplate(ptId);
//        } catch (ServiceFactoryException sfe) {
//            Controller.logger.error(sfe.getMessage(), sfe);

        } catch (RemoteException re) {
            Controller.logger.error(re.getMessage(), re);
        }

        return pt;
    }

    /**
     * Verifies that the Process is still in a state that is valid to be
     * readied. Returns "true" if it is, otherwise returns "false".
     * 
     * @return boolean "True" if Process is waiting to be readied.
     */
    protected boolean validForReady() {
        Timestamp rdyDt = pf.getReadyDt();
        Timestamp startDt = pf.getStartDt();
        Timestamp endDt = pf.getEndDt();
        String processName = pf.getProcess().getProcessTemplateNm();

        // The state of the process is deduced from its dates. If this
        // process is waiting for ready, then the ready date will be non-null,
        // and the start and end dates will both be null.
        if ((rdyDt == null) || (startDt != null) || (endDt != null)) {
            Controller.logInfoMessage("Date inconsistency; Process name = ["
                    + processName + "] is not eligible for " + "readying.");
            return false;
        }

        // Check the state of our parent activity, if we have one. It should
        // indicate that Activity is waiting for a process ready command.
        Activity parent = pf.getParentActivity();
        String parentStatus = parent.getStatusCd();

        if ((parentStatus == null) || !parentStatus.equals("NRDY")) {
            Controller.logInfoMessage("Process Name = [" + processName
                    + "]: Parent Activity status incorrect " + "for readying.");
            return false;
        }

        // Check the current time. If it is earlier than the ready date,
        // then it's too early to ready this process.
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (now.before(rdyDt)) {
            Controller
                    .logInfoMessage("It is too soon to ready Process Name = ["
                            + processName + "].");
            return false;
        }

        return true; // Everything is cool...
    }
}
