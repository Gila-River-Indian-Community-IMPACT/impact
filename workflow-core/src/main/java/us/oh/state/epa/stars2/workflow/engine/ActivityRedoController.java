package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivityGotoController
 * </p>
 * 
 * <p>
 * Description: Takes an Activity that has previously been completed and returns
 * it to the "Ready" state so somebody can redo the operation. Also, any
 * downstream Activities that may have been skipped are reset to the "Not Done"
 * state.
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

public class ActivityRedoController extends ActivityController {
    /**
     * Constructor.
     * 
     * @param a
     *            The Activity being undone.
     */
    public ActivityRedoController(Activity a) {
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

        boolean wasSkipped = false;
        synchronized (activity) {
            // this is already a not done activity return
            if (Activity.NOT_DONE.equals(activity.getStatusCd())) {
                return;
            }
            if (Activity.SKIPPED.equals(activity.getStatusCd())) {
                wasSkipped = true;
            }
            try {
                if (Activity.IN_PROCESS.equals(activity.getStatusCd()) || Activity.PENDING.equals(activity.getStatusCd())) {
                    redoActivity(rqst);
                    return;

                }
                activity.setCurrent("N");
                Controller.logger.debug(": ActivityRedoController.execute(): process_id=" + 
            			activity.getProcessId() + ", activity_template_id=" + 
            			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
                saveToDatabase();
                
//                String roleDiscrim = extractRoleDiscrim(rqst);
                
                // #3206 -- DWL, I moved the code to here from where it was below.
                // Mantis 3001 - set user to default user defined in facility inventory roles
                /**
                WriteWorkFlowService wfBO = null;
                try {
                    ProcessFlow pf = activity.getContainer();
//                    wfBO = ServiceFactory.getInstance().getWriteWorkFlowService();
                    wfBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
                    wfBO.autoAssignTask(activity.getProcessActivity().getFpId(), 
                            activity.getRoleCd(), activity.getProcessActivity(), pf.getUserId(), roleDiscrim);
                    Controller.logger.debug(": ActivityRedoController.execute(): set activity user id to " + activity.getUserId());
//                } catch (ServiceFactoryException e) {
//                    Controller.logger.error("Canot create instance of WorkflowService");
//                    e.printStackTrace();
                } catch (RemoteException e) {
                    Controller.logger.error("Error executing retrieveUserIdsOfFacility");
                    logException(e);
                    e.printStackTrace();
                } catch (Exception e) {
                    Controller.logger.error("WHOA! Unexpected error");
                    logException(e);
                }
                 **/

                if (activity.getActivityId().equals(rqst.getActivityId()) && !wasSkipped){
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    long curMillis = ts.getTime();

                    if (activity.getUserId() != null) {
                        activity.setStatusCd(Activity.IN_PROCESS);
                        activity.setStartDt(ts);
                        long expDurMs = activity.getExpectedDuration();
                        long jeoDurMs = activity.getJeopardyDuration();

                        Timestamp dueTs = new Timestamp(curMillis + expDurMs);
                        Timestamp jeoTs = new Timestamp(curMillis + jeoDurMs);
                        
//                        // Mantis 3001 - set user to default user defined in facility inventory roles
//                        WorkFlowService wfBO = null;
//						try {
//							ProcessFlow pf = activity.getContainer();
//							wfBO = ServiceFactory.getInstance().getWorkFlowService();
//							wfBO.autoAssignTask(activity.getProcessActivity().getFpId(), 
//									activity.getRoleCd(), activity.getProcessActivity(), pf.getUserId());
//                            Controller.logger.debug(": ActivityRedoController.execute(): set activity user id to " + activity.getUserId());
//						} catch (ServiceFactoryException e) {
//                            Controller.logger.error("Canot create instance of WorkflowService");
//							e.printStackTrace();
//						} catch (RemoteException e) {
//                            Controller.logger.error("Error executing retrieveUserIdsOfFacility");
//							logException(e);
//							e.printStackTrace();
//						} catch (Exception e) {
//                            Controller.logger.error("WHOA! Unexpected error");
//							logException(e);
//						}

                        activity.setDueDt(dueTs);
                        activity.setJeopardyDt(jeoTs);
                    } else {
                        activity.setStatusCd(Activity.PENDING);
                        activity.setStartDt(null);
                        activity.setJeopardyDt(null);
                        activity.setDueDt(null);
                    }

                    activity.setReadyDt(ts);
                    activity.setEndDt(null);
                } else {
                    activity.setStatusCd(Activity.NOT_DONE);
                    activity.setDueDt(null);
                    activity.setJeopardyDt(null);
                    activity.setStartDt(null);
                    activity.setReadyDt(null);
                    activity.setEndDt(null);
                }

                activity.setCurrent("Y");
                activity.setLoopCnt(activity.getLoopCnt() + 1);

                addToDatabase();
            } catch (DAOException de) {
                Controller.logger.error(de.getMessage(), de);
                String et = "Activity info in engine : ProcessId [" + activity.getProcessActivity().getProcessId() + "] ActivityTemplateId [" + activity.getProcessActivity().getActivityTemplateId() + "] LoopCnt [" + activity.getProcessActivity().getLoopCnt() + "] LastModified [" + activity.getActivityTemplate().getLastModified() + "]";
                Controller.logger.error(et);
                resp.addFailure(de.getMessage());
                return;
            }
        }

        if (Activity.LOOP.equals(activity.getOutTransitionCode()) && wasSkipped) {
            return;
        }

        // Get the parent ProcessFlow and then get all of the outbound
        // transitions from this Activity. If we don't have any outbound
        // transistions, then something weird is going on, but we don't care.
        ProcessFlow parent = activity.getContainer();
        rqst.addToPath(activity.getActivityId());
        
        Transition[] outbounds = parent.getOutboundTransitions(activity);

        if ((outbounds != null) && (outbounds.length > 0)) {
            // For each outbound Activity, send an "redo" control to it.
            for (Transition tt : outbounds) {
                Integer downstreamId = tt.getToId();
                // if we been downstream activity already.
                if (rqst.inPath(downstreamId))
                    continue;

                Activity downstream = parent.getActivity(downstreamId);
                IController c = Controller.createController(
                        WorkFlowEngine.ACTION_REDO, downstream);
                
                // Controller.logger.error("INFO - DWL:  downsdtreamId=" + downstreamId + ", origActivityId=" + rqst.getActivityId()); // 3206
                c.execute(resp, rqst);
            }
        }
        
        rqst.removeFromPath(activity.getActivityId());
    }
}
