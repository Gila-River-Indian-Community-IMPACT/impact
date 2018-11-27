package us.oh.state.epa.stars2.workflow.engine;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: ManualActivityReadyController
 * </p>
 * 
 * <p>
 * Description: Marks a Manual Activity as "Ready". This makes the Activity
 * eligible for check-out by a user. Activity state is verified before checking
 * it out.
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

public class ManualActivityReadyController extends ActivityController {
    /**
     * Constructor. Takes a reference to the Activity being made ready.
     * 
     * @param a
     *            Manual Activity to be made ready.
     */
    public ManualActivityReadyController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Verifies that the Activity is in the proper state to be
     * made ready. Changes the Activity state to "Ready" and saves it to the
     * database. Any errors or failures are appended to the WorkFlowResponse
     * object.
     * 
     * @param resp
     *            WorkFlowResponse
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // First, mark the Activity as "Ready". If we can't do this (wrong
        // state), then just return.
        synchronized (activity) {
        	Controller.logger.debug(" Executing ManualActivityReadyController");
            if (readyActivity(resp, rqst.getStartDt())) {
                // Save the Activity to the database. Note that a failure is not
                // a major problem because we leave database in a consistent
                // that
                // can easily be re-checked out.
                try {
                    //saveServiceData(); 
                    
                    if(!rqst.isLeaveUserSame()) {  // #3206
                    	
                    	String roleDiscrim = extractRoleDiscrim(rqst);
                        Integer assignedUserId = extractAssignedUser(rqst);
                    	
                        // Mantis 3001 - set user to default user defined in facility inventory roles
                        WriteWorkFlowService wfBO = null;
                        try {
//                            Controller.logger.error("DWL: action=" + rqst.getAction() +
//                                    ", type=" + rqst.getType() +
//                                    ", activityId=" + rqst.getActivityId() +
//                                    ", userId=" + rqst.getUserId() +
//                                    ", accountId=" + rqst.getAccountId(), new Exception()); // #3206

                            ProcessFlow pf = activity.getContainer();
//                            wfBO = ServiceFactory.getInstance().getWriteWorkFlowService();
                            wfBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
                            if (null != assignedUserId) {
                            	activity.setUserId(assignedUserId);
                            } else {
	                            wfBO.autoAssignTask(activity.getProcessActivity().getFpId(), 
	                                    activity.getRoleCd(), activity.getProcessActivity(), pf.getUserId(),roleDiscrim);
                            }
                            Controller.logger.debug(": ActivityRedoController.execute(): set activity user id to " + activity.getUserId());
//                        } catch (ServiceFactoryException e) {
//                            Controller.logger.error("Canot create instance of WorkflowService", e);
//                            e.printStackTrace();
                        } catch (RemoteException e) {
                            Controller.logger.error("Error executing retrieveUserIdsOfFacility", e);
                            logException(e);
                            e.printStackTrace();
                        } catch (Exception e) {
                            Controller.logger.error("WHOA! Unexpected error", e);
                            logException(e);
                        }
                        Controller.logger.debug(": ManualActivityReadyController.execute(): process_id=" + 
                                activity.getProcessId() + ", activity_template_id=" + 
                                activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt() +
                                ", userId=" + rqst.getUserId());
                    }
                    
                    Controller.logger.debug(" Saving activity changes to database.");
                    saveToDatabase(rqst.getTransaction());
                } catch (DAOException de) {
                    Controller.logger.error(de.getMessage(), de);
                    activity.setStatusCd(Activity.NOT_DONE);
                    resp.addFailure(de.getMessage());
                }
            }
        }
    }
}
