package us.oh.state.epa.stars2.scheduler.jobs;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;

@Component
public class CheckForInactiveUserJob implements Job  {
    private transient Logger logger;
    @Resource private InfrastructureDAO readOnlyInfrastructureDAO;
//    @Resource private WorkFlowService readOnlyWorkFlowService;
   
    public CheckForInactiveUserJob() {
        logger = Logger.getLogger(this.getClass());
        logger.debug("CheckForInactiveUserJob constructed");
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.debug("CheckForInactiveUserJob is executing.");

        try {
            checkForInactiveUsers();
        }
        catch (Exception e) {
            logger.error("CheckForInactiveUserJob failed. ", e);
            throw new JobExecutionException("CheckForInactiveUserJob failed. " 
                                            + e.getMessage(), e, false);
        }
        
        logger.debug("CheckForInactiveUserJob completed.");
    }
    
    private void checkForInactiveUsers() {
    	throw new RuntimeException("decouple workflow");
//        InfrastructureDAO infraDAO = (InfrastructureDAO)DAOFactory.getDAO("InfrastructureDAO");
//        WorkFlowService wfBO = null;
//        try {
////        	wfBO = ServiceFactory.getInstance().getWorkFlowService();
//			UserDef[] inactiveUsers = readOnlyInfrastructureDAO.retrieveInactiveUserDefs();
//			for (UserDef ud : inactiveUsers) {
////				readOnlyWorkFlowService.reassignCurrentActivitiesForUser(ud.getUserId());
//			}
//		} catch (DAOException e) {
//			logger.error("Exception retrieving data for inactive users", e);
////		} catch (ServiceFactoryException e) {
////			logger.error("Exception accessing WorkFlowService", e);
//		} catch (RemoteException e) {
//			logger.error("Exception reassigning activities for inactive user", e);
//		}
    }
    
    

    /**
     * For testing only
     * @param args
     */
    public static final void main(String[] args) {
        CheckForInactiveUserJob job = new CheckForInactiveUserJob();
        job.checkForInactiveUsers();
    }
}
