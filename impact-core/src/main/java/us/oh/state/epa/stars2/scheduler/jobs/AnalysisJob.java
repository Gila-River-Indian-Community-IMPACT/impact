package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.bo.AnalysisService;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

/**
 * Allowed actions are: 
 *  PERReminderNotice (U-22-Autonomous_Analysis-1-1)
 *  PEROverdueNotice (U-22-Autonomous_Analysis-1-2)
 *  TVCertOverdueNotice (U-22-Autonomous_Analysis-1-3)
 *  TVSMTVFERNoticeViolation (U-22-Autonomous_Analysis-1-15)
 *  PTIOExpirationNotice (U-22-Autonomous_Analysis-1-4, U-22-Autonomous_Analyses-1-9)
 *  StatePTOExpirationNotice (U-22-Autonomous_Analysis-1-5, U-22-Autonomous_Analysis-1-10)
 *  TVPTOExpirations (U-22-Autonomous_Analysis-1-6, U-22-Autonomous_Analysis-1-8)
 *  TVPTOExpirationWarning (U-22-Autonomous_Analysis-1-7)
 *  TVPTOLateApplication (U-22-Autonomous_Analysis-1-11)
 *  DraftIssueNoPublicNotice (U-22-Autonomous_Analyses-1-12)
 *  DraftNewspaperRequestNoHearingDate (U-22-Autonomous_Analyses-1-13)
 *  PPPIssueDateNoCertMailReceipt (U-22-Autonomous_Analysis-1-14)
 * 
 * @author Pyeh
 * 
 */
public class AnalysisJob implements Job {
    private transient Logger logger;

    public AnalysisJob() {
        logger = Logger.getLogger(this.getClass());
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        logger.debug("AnalysisJob is executing.");

        JobDataMap dataMap = context.getMergedJobDataMap();

        String action = (String)dataMap.get("action");
        String error = "Action : " + action + " -- ";
        
        try {
            AnalysisService analysisBO = ServiceFactory.getInstance()
                    .getAnalysisService();
            if (action == null){
                logger.error("Action is not setted in schedule data.", new Exception());
            }

            logger.debug("Invoking analysis action: " + action);
            if (action.equalsIgnoreCase("PERReminderNotice")){
                analysisBO.PERReminderNotice();
            //}else if (action.equalsIgnoreCase("PEROverdueNotice")){
            //    analysisBO.PEROverdueNotice();
            }else if (action.equalsIgnoreCase("TVCertOverdueNotice")){
                analysisBO.TVCertOverdueNotice();
            }else if (action.equalsIgnoreCase("TVSMTVFERNoticeViolation")){
                analysisBO.TVSMTVFERNoticeViolation();
            }else if (action.equalsIgnoreCase("PTIOExpirationNotice")){
                analysisBO.PTIOExpirationNotice();
            //}else if (action.equalsIgnoreCase("StatePTOExpirationNotice")){
            //    analysisBO.StatePTOExpirationNotice();
            }else if (action.equalsIgnoreCase("TVPTOExpirations")){
                analysisBO.TVPTOExpirations();
            }else if (action.equalsIgnoreCase("TVPTOExpirationWarning")){
                analysisBO.TVPTOExpirationWarning();
            }else if (action.equalsIgnoreCase("TVPTOLateApplication")){
                analysisBO.TVPTOLateApplication();
            }else if (action.equalsIgnoreCase("DraftIssueNoPublicNotice")){
                analysisBO.DraftIssueNoPublicNotice();
            }else if (action.equalsIgnoreCase("DraftNewspaperRequestNoHearingDate")){
                analysisBO.DraftNewspaperRequestNoHearingDate();
            //}else if (action.equalsIgnoreCase("PPPIssueDateNoCertMailReceipt")){
            //    analysisBO.PPPIssueDateNoCertMailReceipt();
            } else if (action.equalsIgnoreCase("addAppEUsToPermits")) {
            	analysisBO.addAppEUsToPermits();
            }else{
                logger.error("Action " + action + " is not listed in AnalysisJob", new Exception());
            }
            logger.debug("Completed analysis action: " + action);
            
        } catch (ServiceFactoryException sfe) {
            error += sfe.toString();
            logger.error(action + " failed: " + error, sfe);
        } catch (IllegalArgumentException e) {
            error += e.toString();
            logger.error(action + " failed: " + error, e);
        } catch (SecurityException e) {
            error += e.toString();
            logger.error(action + " failed: " + error, e);
        } catch (Exception e) {
            error += e.toString();
            logger.error(action + " failed: " + error, e);
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
    
    public static void main(String[] args) {
        AnalysisService analysisBO;
		try {
			analysisBO = ServiceFactory.getInstance().getAnalysisService();
	        analysisBO.addAppEUsToPermits();
		} catch (ServiceFactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DAOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
