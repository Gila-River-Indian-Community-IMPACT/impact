package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.scheduler.Stars2Scheduler;
import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: ActivityGotoController
 * </p>
 * 
 * <p>
 * Description: Takes an IN_PROGRESS activity and refers this task to an
 * external entity such as the End Customer. Essentially we are terminating this
 * activity and creating a new activity that's state is "REFERRED". What this
 * looks like is that we are looping back to the current.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author S. Wooster
 * @version 1.0
 */

public class ActivityUnReferController extends ActivityController {
    /**
     * Constructor.
     * 
     * @param a
     *            The Activity being undone.
     */
    public ActivityUnReferController(Activity a) {
        super(a);
    }

    /**
     * Framework method. In preparation for looping back and redo-ing activites
     * we are creating new Activities objects for every activites to redo.
     * 
     * @param resp
     *            WorkFlowResponse being returned to calling application.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        ProcessFlow pf = null;
        synchronized (activity) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            try {
                saveServiceData();
                activity.setCurrent("N");
                activity.setStatusCd(Activity.UN_REFERRED);
                if (null != rqst.getEndDt()) {
                	activity.setEndDt(rqst.getEndDt());
                } else {
                	activity.setEndDt(now);
                }
//                if (YES.equalsIgnoreCase(activity.getActivityTemplate()
//                        .getMilestoneInd())) {
                	// decouple workflow ... makeEventLog
//                  makeEventLog();
//                }
                Controller.logger.debug(": ActivityUnReferController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addError(de.getMessage());
                return;
            }

            // when refer with extend end date, we add the total due day into
            // process due date.  Now take them off from process due date.
            long interval = activity.getDueDt().getTime()
                    - activity.getEndDt().getTime();
            if (YES.equalsIgnoreCase(activity.getExtendProcessEndDate())) {
                pf = activity.getContainer();
                rqst.setActivityId(null);
                rqst.setDueDt(new Timestamp(pf.getDueDt().getTime()
                                - interval));
                rqst.setJeopardyDt(new Timestamp(pf.getJeopardyDt().getTime()
                        - interval));
            }
            
            // Add refer days into new activity old due date which we save in
            // refer task as jeopardy date.
            interval = activity.getEndDt().getTime()
                    - activity.getStartDt().getTime();
            activity.setDueDt(new Timestamp(activity.getJeopardyDt().getTime()
                    + interval));
            activity.setJeopardyDt(new Timestamp(activity.getDueDt().getTime()
                    - activity.getJeopardyDuration()));
            activity.setStatusCd(Activity.IN_PROCESS);
            if (null != rqst.getEndDt()) {
            	activity.setStartDt(rqst.getEndDt());
            } else {
            	activity.setStartDt(now);
            }
            activity.setReadyDt(now);
            activity.setEndDt(null);
            activity.setCurrent("Y");
            activity.setLoopCnt(activity.getLoopCnt() + 1);
            activity.setExtendProcessEndDate("N");

            try {
                addToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
                return;
            }
        }
        if (pf != null) {
            IController c = WorkFlowEngine.createController(
                    WorkFlowEngine.ACTION_CHANGE_DUE_DATE, pf);

            c.execute(resp, rqst);
        }
        
		// delete the unrefer trigger that was added when the activity was
		// referred
		WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
		try {
			String jobName = Stars2Scheduler.recurringJobName;
			Integer processId = activity.getProcessId();
			Integer activityId = activity.getActivityId();
			// since unreferral increments the loop count of the activity, we need to subtract 1
			// to get the trigger name that was added when the activity was originally referred
			Integer loopCnt = (null != activity.getLoopCnt()) ? activity.getLoopCnt() - 1 : null;
			if (null != processId && null != activityId && null != loopCnt) {
				String triggerName = Stars2Scheduler.buildTriggerName(processId, activityId, loopCnt);
				workFlowBO.removeScheduledTrigger(triggerName, jobName);
			}
		} catch (Exception e) {
			Controller.logger.error(e.getMessage(), e);
			resp.addError(e.getMessage());
			return;
		}
    }
}
