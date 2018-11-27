package us.oh.state.epa.stars2.scheduler.jobs;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class CorrespondenceScheduleFollowUpAction implements StatefulJob {
    private transient Logger logger;
    
    public CorrespondenceScheduleFollowUpAction() {
        logger = Logger.getLogger(this.getClass());
    }

    public void execute(JobExecutionContext context)
        throws JobExecutionException {
        logger.debug("Initializing CorrespondenceScheduleFollowUpAction");
        try {
            CorrespondenceService corrBO = ServiceFactory.getInstance().getCorrespondenceService();  
            corrBO.createFollowUpActionNotices();
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
