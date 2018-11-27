package us.oh.state.epa.stars2.scheduler.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class MarkInactiveNSRPermitsToExpiredJob implements Job {

	private transient Logger logger;
	
	
	public MarkInactiveNSRPermitsToExpiredJob() {
        logger = Logger.getLogger(this.getClass());
        logger.info("MarkInactiveNSRPermitsToExpiredJob is constructed.");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			logger.info("MarkInactiveNSRPermitsToExpiredJob is executing.");
			PermitService permitBO = ServiceFactory.getInstance().getPermitService();
			permitBO.markInactiveNSRPermitsToExpired();
		} catch (Exception e) {
			logger.error("Error while executing MarkInactiveNSRPermitsToExpiredJob", e);
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
