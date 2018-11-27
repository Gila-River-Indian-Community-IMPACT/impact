package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class CancelWorkflowsJob implements Job {
    private transient Logger logger;

    public CancelWorkflowsJob() {
        logger = Logger.getLogger(this.getClass());
        logger.debug("CancelWorkflowsJob is constructed.");
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
//    	// decouple workflow
//    	throw new RuntimeException("decouple workflow; method not implemented");
        try {
            logger.debug("CancelWorkflowsJob is executing...");
            JobDataMap dataMap = context.getMergedJobDataMap();
            Integer processId = dataMap.getIntFromString("processId");
            Integer externalId  = dataMap.getIntFromString("externalId");
            logger.debug("Cancelling workflow with processId = " + processId +
                    " and externalId = " + externalId);
            WorkFlowManager wfm = new WorkFlowManager();
            ProcessFlow pf = retrieveProcessFlow(processId, externalId);
            WriteWorkFlowService wfBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
            pf.getProcess().setExternalId(null);
            wfBO.updateProcessFlow(pf);
            // retrieve process to be sure it has been modified.
            ReadWorkFlowService readWfBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
            WorkFlowProcess process = readWfBO.retrieveProcess(pf.getProcessId());
            if (process.getExternalId() == null) {
                WorkFlowResponse response = wfm.cancelProcess(processId, CommonConst.ADMIN_USER_ID);
                if (response.hasError() || response.hasFailed()) {
                    String[] errorMsgs = response.getErrorMessages();
                    String[] recomMsgs = response.getRecommendationMessages();
                    String error = "Workflow response problem: ";
                    for (String msg : errorMsgs) {
                        error += msg + " ";
                    }
                    for (String msg : recomMsgs) {
                        error += msg + " ";
                    }
                    logger.error("Error while cancelling workflow: " + error);
                }
                logger.debug("Cancelled workflow with id = " + processId + " for facility " +
                        process.getFacilityId() + " after setting external id to null. " +
                        "Former external id was " + externalId);
            } else {
                logger.error("Unable to cancel process " + processId + ". " +
                        "Failed setting external id to null.");
            }
            logger.debug("CancelWorkflowsJob has completed.");
        }
        catch (Exception e) {
            logger.error("CancelWorkflowsJob failed. ", e);
            throw new JobExecutionException("CancelWorkflowsJob failed. " 
                                            + e.getMessage(), e, false);
        }

    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }

    
    private ProcessFlow retrieveProcessFlow(Integer processId, Integer externalId) throws RemoteException {
        ReadWorkFlowService wfBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
        ProcessFlow permitProcessFlow = null;
        WorkFlowProcess wfProcess = new WorkFlowProcess();
        wfProcess.setProcessId(processId);
        wfProcess.setExternalId(externalId);
        wfProcess.setCurrent(true);
        int activeProcessFlowCount = 0;
        WorkFlowProcess[] processes = wfBO.retrieveProcessList(wfProcess);
        for (WorkFlowProcess process : processes) {
            if (!process.getProcessId().equals(processId)) {
                continue;
            }
            ProcessFlow tmpProcessFlow = wfBO.retrieveActiveProcessFlow(process.getProcessId());
            if (tmpProcessFlow == null || tmpProcessFlow.getEndDt() != null) {
                continue;
            }
            permitProcessFlow = tmpProcessFlow;
            activeProcessFlowCount++;
        }
        if (activeProcessFlowCount != 1) {
            logger.error("Unexpected result querying work flow processes for external id " + externalId
                   + " " + activeProcessFlowCount + " work flow processes found.");
            permitProcessFlow = null;
        }
        return permitProcessFlow;
    }
}
