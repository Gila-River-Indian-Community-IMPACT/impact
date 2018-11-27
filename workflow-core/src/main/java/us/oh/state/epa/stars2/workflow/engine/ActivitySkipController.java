package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivitySkipController
 * </p>
 * 
 * <p>
 * Description: Handles sending a "skip" command to an Activity. When skipping
 * an Activity, the Activity is marked "skipped" and a "skip" command is sent to
 * all Activities downstream from the current Activity. Note that "skip"
 * commands are not propogated upwards to the containing ProcessFlow. Either
 * other branches will be taken to complete the ProcessFlow or a terminal
 * Activity will be hit. The terminal Activity will allow the user to terminate
 * the ProcessFlow.
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

public class ActivitySkipController extends ActivityController {
	
	
    /**
     * Constructor. Takes a reference to the Activity to be skipped.
     * 
     * @param a
     *            Activity to be skipped.
     */
    public ActivitySkipController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Marks this Activity as "skipped" in the database and
     * attempts to send "skip" messages to downstream Activities.
     * 
     * @param resp
     *            WorkFlowReponse for error and failure messages.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // If not all of the inputs to this Activity are done, then return
        // because we don't know if we are going to skip this or not.

        if (!this.inputsAreFinished()) {
            return;
        }

        String doNotSkipId = extractDoNotSkipId(rqst);
        
        if (null != doNotSkipId && !"".equals(doNotSkipId) && doNotSkipId.equals(activity.getActivityId().toString())) {
        	return;
        }
        		
        // Mark this Activity as "skipped" and then save the state to the
        // database.

        synchronized (this.activity) {
            try {
                this.skipActivity();
                Controller.logger.debug(": ActivitySkipController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                this.saveToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage());
                resp.addFailure(de.getMessage());
                return;
            }
        }

        // Get the containing ProcessFlow. Use this to get all of the
        // outbound Transitions for this Activity.

        ProcessFlow container = this.activity.getContainer();
        Transition[] outbounds = container
                .getOutboundTransitions(this.activity);

        // If there are no outbound Transitions, then stop. Note that we are
        // NOT sending a "skip" message to the containing ProcessFlow like
        // we would a "complete" message.

        if ((outbounds == null) || (outbounds.length == 0)) {
            // If we get here, then an entire service order may have been
            // skipped. Rather than attempt to make a decision about what
            // to do about it, build an info message telling the user that
            // she/he might have to deal with this.

            /*
             * Integer orderId = container.getExternalId(); Integer serviceId =
             * container.getServiceId();
             */

            StringBuffer sb = new StringBuffer(200);
            /*
             * sb.append("Service Order #"); sb.append(serviceId.toString());
             * sb.append(" on Order #"); sb.append(orderId.toString());
             */
            sb.append(activity.getActivityName());
            sb.append(" may need to be manually skipped.");

            resp.addInfoMsg(sb.toString());
        } else {

            //
            // Special Case - Do not follow loops
            if (activity.getOutTransitionCode().equals(Activity.LOOP)) {
                return;
            }

            // FOR (each outbound Transition) send a "skip" control to the
            // Activity on the other end.
            for (Transition tt : outbounds) {
                Integer toActivityId = tt.getToId();
                Activity inbound = container.getActivity(toActivityId);
                
                String action = WorkFlowEngine.ACTION_SKIP;
                
                IController c = Controller.createController(
                        action, inbound);
                WorkFlowRequest request = new WorkFlowRequest();
                request.addDataPair(WorkFlowEngine.DO_NOT_SKIP, doNotSkipId);
                c.execute(resp, request);
            }
        }
    }

	private String extractDoNotSkipId(WorkFlowRequest rqst) {
		for (int i = 0; i < rqst.getDataCount(); i++) {
			String name = rqst.getDataName(i);
			if (WorkFlowEngine.DO_NOT_SKIP.equals(name)) {
				return rqst.getDataValue(i);
			}
		}
		return null;
	}
}
