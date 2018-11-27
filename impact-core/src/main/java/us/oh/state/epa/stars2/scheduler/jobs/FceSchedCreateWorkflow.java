package us.oh.state.epa.stars2.scheduler.jobs;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class FceSchedCreateWorkflow implements StatefulJob {
    private transient Logger logger;
    
    public FceSchedCreateWorkflow() {
        logger = Logger.getLogger(this.getClass());
    }

    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        logger.error("Initializing FceSchedCreateWorkflow");
        try {
            FullComplianceEvalService fceBO = ServiceFactory.getInstance().getFullComplianceEvalService();  
            fceBO.fceNeedReminders();
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
}
