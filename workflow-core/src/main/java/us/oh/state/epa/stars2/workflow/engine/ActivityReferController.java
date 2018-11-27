package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.framework.exception.DAOException;
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

public class ActivityReferController extends ActivityController {

    /**
     * Constructor.
     * 
     * @param a
     *            The Activity being undone.
     */
    public ActivityReferController(Activity a) {
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
        // Check the status of the Activity. If it is not in the "Completed"
        // state, then don't touch it.
        ProcessFlow pf = null;
        synchronized (activity) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            try {
                saveServiceData();
                completeActivity();
                activity.setCurrent("N");
//                if (YES.equalsIgnoreCase(activity.getActivityTemplate()
//                        .getMilestoneInd())) {
                	// decouple workflow ... makeEventLog
//                    makeEventLog();
//                }
                Controller.logger.debug(": ActivityReferController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addError(de.getMessage());
                return;
            }

            // Save the current due date in Jeopardy for unrefer.
            activity.setJeopardyDt(activity.getDueDt());
            activity.setDueDt(rqst.getDueDt());
            activity.setExtendProcessEndDate(rqst.getExpedite());
            long interval = activity.getDueDt().getTime() - now.getTime();

            if (YES.equalsIgnoreCase(activity.getExtendProcessEndDate())) {
                pf = activity.getContainer();
                rqst.setActivityId(null);
                rqst.setDueDt(new Timestamp(pf.getDueDt().getTime()
                                + interval));
                rqst.setJeopardyDt(new Timestamp(pf.getJeopardyDt().getTime()
                        + interval));
            }
            activity.setStatusCd(Activity.REFERRED);
            activity.setStartDt(now);
            activity.setReadyDt(now);
            activity.setEndDt(null);
            activity.setCurrent("Y");
            activity.setActivityReferralTypeId(rqst.getContactId());
            activity.setLoopCnt(activity.getLoopCnt() + 1);

            try {
                addToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                resp.addFailure(de.getMessage());
                return;
            }
        }

        // update workflow due date
        if (pf != null) {
            IController c = WorkFlowEngine.createController(
                    WorkFlowEngine.ACTION_CHANGE_DUE_DATE, pf);

            c.execute(resp, rqst);
        }

        WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
        try {
			workFlowBO.scheduleRecurring(activity.getProcessId(), activity.getActivityTemplateId(),
					activity.getLoopCnt(), activity.getDueDt(), 
					activity.getUserId());
		} catch (Exception e) {
            Controller.logger.error(e.getMessage(), e);
            resp.addError(e.getMessage());
            return;
		}


    }
}
