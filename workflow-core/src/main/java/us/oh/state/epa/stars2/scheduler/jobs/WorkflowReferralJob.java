package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.scheduler.TimestampCalculator;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowManager;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowResponse;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class WorkflowReferralJob implements Job {
    private transient Logger logger;

    public WorkflowReferralJob() {
        logger = Logger.getLogger(this.getClass());
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        logger.debug("WorkFlowReferralJob is executing.");

        JobDataMap dataMap = context.getMergedJobDataMap();

        Integer processId = dataMap.getIntFromString("processId");
        Integer activityId = dataMap.getIntFromString("activityId");
        Integer loopCnt = dataMap.getIntFromString("loopCnt");
        Integer userId = dataMap.getIntFromString("userId");
        try {
	        WorkFlowManager wfm = new WorkFlowManager();
	
	        WorkFlowResponse resp = wfm.unRefer(processId, activityId, loopCnt,
	                userId, null);
	
	        // If for some reason the unRefer fails we will attempt to redo
	        // this the next day. We expect that unRefer should not fail.
	        if (resp.hasError() || resp.hasFailed()) {
	            try {
	                WriteWorkFlowService wfBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
	                Date nextDate = new Date(TimestampCalculator.get(1, new Timestamp(System.currentTimeMillis())).getTime());
	               
	                wfBO.scheduleRecurring(processId, activityId, loopCnt, nextDate, userId);
	                logger.debug("Rescheduled to run again at: " + nextDate);
	            } catch (RemoteException re) {
	                logger.error("WorkflowReferral: cannot reschedule, processId="
	                        + processId + ",activityId=" + activityId + ",loopCnt="
	                        + loopCnt);
	                throw re;
	            }
	        }
        } catch (Exception e) {
            logger.error(e);
            JobExecutionException qrtze = new JobExecutionException(e);
            logger.debug("Refire count: " + context.getRefireCount());
            if (context.getRefireCount() < 2) {
            	logger.debug("Setting refireImmediately = true");
            	qrtze.setRefireImmediately(true);
            }
        	logger.debug("Throwing JobExecutionException");
            throw qrtze;
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}
