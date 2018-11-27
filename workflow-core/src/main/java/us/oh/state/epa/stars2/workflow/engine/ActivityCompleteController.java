package us.oh.state.epa.stars2.workflow.engine;

import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.def.DataTypeDef;
import us.oh.state.epa.stars2.util.FieldComparator;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ActivityCompleteController
 * </p>
 * 
 * <p>
 * Description: This controller responds to activity completion by examining
 * outbound Transitions to see who needs to be notified about completion of
 * Activity "a". There is a wide range of possible responses. The purpose for
 * this controller is to encapsulate the decision making and processing for this
 * wide range of possible responses.
 * </p>
 * 
 * <p>
 * Note that this controller does not save Activity state to the database.
 * Typically, the Activity should be marked as "Completed" and saved to the
 * database before creating this controller.
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

public class ActivityCompleteController extends ActivityController {
    /**
     * Constructor. Takes the Activity marked as "Completed".
     * 
     * @param a
     *            Activity marked as "Completed".
     */
    public ActivityCompleteController(Activity a) {
        super(a);
    }

    /**
     * Framework method. Looks at the outbound Transitions to see what to
     * "Ready" next. Error and failure messages are appended to "resp".
     * 
     * @param resp
     *            WorkFlowReponse for error and failure messages.
     */
    public void execute(WorkFlowResponse resp, WorkFlowRequest rqst) {
        // Get the ProcessFlow that contains this Activity and ask the
        // ProcessFlow for all of the Activity's outbound Transitions.

        ProcessFlow container = this.activity.getContainer();
        Transition[] outbounds = container
                .getOutboundTransitions(this.activity);

//        // debug code
//        // Only log transitions if they have the correct from id.
//        Integer actId = this.activity.getActivityTemplateId();
//        boolean logFlag = false;
//        if(new Integer(806).equals(actId)) logFlag = true;
//        if(new Integer(1006).equals(actId)) logFlag = true;
//        if(logFlag) { 
//            // log the outbound transition information 
//            logger.error("INFO ONLY for dennis/reza: " + outbounds.length + " Transitions from " + actId, new Exception());
//            for(Transition t : outbounds) {
//                logger.error("INFO ONLY for dennis/reza: " + t.toPrintable());
//                DataField dField = container.getDataField(t.getDataName());
//                logger.error("INFO ONLY for dennis/reza: " + dField.toPrintable());  
//            }
//            logger.error("INFO ONLY for dennis/reza DataFieldMap " + container.getDataFields());
//        }
        
        // Zero outbound Transitions implies that the ProcessFlow containing
        // this Activity has been completed. Create a
        // ProcessCompleteController and let it handle the process.

        if ((outbounds == null) || (outbounds.length == 0)) {
            String msg = "Process ID = " + container.getProcessId().toString()
                    + " is completed.";

            Controller.logInfoMessage(msg);
            resp.addInfoMsg(msg);

            IController cc = Controller.createController(
                    WorkFlowEngine.ACTION_COMPLETE, container);
            cc.execute(resp, new WorkFlowRequest());
            return;
        }

        // If we only have one outbound Transition, we are going to have to
        // take that path. Create a "Ready" controller for that Activity
        // and tell it to execute().

        if (outbounds.length == 1) {
            Transition tt = outbounds[0];
            Integer toActivityId = tt.getToId();
            Activity inbound = container.getActivity(toActivityId);
            
//            // start #3206
//            ProcessActivity debugPA = inbound.getProcessActivity();
//            if(debugPA != null) {
//                logger.error("DWL: inbound Activity info:  toActivityId=" + toActivityId +
//                        ", performerTypeCd=" + debugPA.getPerformerTypeCd() +
//                        ", activityTemplateId=" + debugPA.getActivityTemplateId() +
//                        ", processId=" + debugPA.getProcessId() +
//                        ", userId=" + debugPA.getUserId() +
//                        ", oldUserId=" + debugPA.getOldUserId() +
//                        ", ownerId=" + debugPA.getOwnerId() +
//                        ", activityStatusCd=" + debugPA.getActivityStatusCd()); // #3206
//            } else {
//                logger.error("DWL: inbound Activity info:  toActivityId=" + toActivityId +
//                    "<--processActivity in inbound is null"); // #3206
//            }
//            // end #3206
            IController cc = null;
            String outTransType = this.activity.getOutboundTransitionType();

            //
            // Special case, if the outbound transition is a loop we need
            // to prepare all downstream activites for re-excuting them
            // 
            if (outTransType.equals(Activity.LOOP)) {
                cc = Controller.createController(WorkFlowEngine.ACTION_REDO,
                        inbound);
                cc.execute(resp, rqst, dataValues);
            }

            cc = Controller.createController(WorkFlowEngine.ACTION_READY,
                    inbound);

            // Since completing activity do not want to change assigned user
            // logger.error("DWL: Ready for Action_Ready"); // #3206
            rqst.setLeaveUserSame(true);  // #3206
            cc.execute(resp, rqst, dataValues);
            return;
        }

        // If we get here, we have multiple outbound Transitions from this
        // activity. What we do next depends on the type of the outbound
        // transitions. A "split AND" or "inline" type means ready all of
        // the Activities on the other end of these Transitions.

        String outTransType = this.activity.getOutboundTransitionType();

        if (outTransType.equals(Activity.SPLIT_AND)
                || outTransType.equals(Activity.INLINE)) {
            this.readyAllTransitions(container, outbounds, resp);
            return;
        }

        // If we get here, we have a "split XOR" outbound Transition type.
        // This means that we have to evaluate the test condition for each
        // transition and "ready" along any Transition whose test condition
        // is satisfied. If the test condition is not met, then we "skip"
        // along that Transition.

        this.processTransitions(container, outbounds, resp);
    }

    /**
     * Readies all Activity objects connected to the output of this Activity.
     * The array "tt" contains all of the outbound Transitions for the current
     * Activity. "pf" is the ProcessFlow that contains this Activity. "resp" is
     * updated with any messages generated as part of the ready process.
     * 
     * @param pf
     *            ProcessFlow containing our Activity.
     * @param tt
     *            All outbound Transitions for our Activity.
     * @param resp
     *            WorkFlowResponse.
     */
    protected void readyAllTransitions(ProcessFlow pf, Transition[] tt,
            WorkFlowResponse resp) {
        int i;
        Activity a;
        Transition t;
        IController c;

        // This is easy. FOR (each outbound Transition) get the Activity
        // on the other end of the Transition and send it a "ready" command.

        for (i = 0; i < tt.length; i++) {
            t = tt[i];
            Integer toActId = t.getToId();
            a = pf.getActivity(toActId);
            c = Controller.createController(WorkFlowEngine.ACTION_READY, a);
            // Since completing activity do not want to change assigned user
            // logger.error("DWL: readyAllTransitions:  Ready for Action_Ready", new Exception()); // #3206
            WorkFlowRequest wfr = new WorkFlowRequest();
            wfr.setLeaveUserSame(true);  // #3206
            c.execute(resp, wfr);
        }
    }

    /**
     * Processes all outbound Transitions based on the value of a DataField
     * associated with the output of this Activity. In this case, each
     * Transition is expected to define a criteria for travel down that leg, for
     * example, "foo <= 2" ("foo" will define the name of the DataField). If the
     * criteria is met, then the Activity on the other end of the Transition
     * will be sent a "ready" command. If not, the Activity will be sent a
     * "skip" command.
     * 
     * @param pf
     *            ProcessFlow containing our Activity.
     * @param tt
     *            All outbound Transitions for our Activity.
     * @param resp
     *            WorkFlowResponse.
     */
    protected void processTransitions(ProcessFlow pf, Transition[] tt,
            WorkFlowResponse resp) {
        int i;
        // Activity a ;
        Transition t;
        IController c;

        // This is not so easy. FOR (each outbound Transition) get the
        // Activity at the other end. Get the DataField that has the
        // value and compare that value to the Transition criteria.

        ArrayList <Activity> readyActivities = new ArrayList<Activity>();
        
        //TODO duplicate code ... need to cleanup
        for (i = 0; i < tt.length; i++) {
            try {
            	t = tt[i];
            	Integer toActId = t.getToId();
            	Activity nextAct = pf.getActivity(toActId);
            	String dataName = t.getDataName();
            	String operation = t.getOperation();
            	String transValue = t.getDataValue();
                DataField df = pf.getDataField(dataName);
                String dfValue = df.getDataValue();
                String dfType = DataTypeDef.getData().getItems().getItemDesc(
                        df.getDataTypeId()); // "String";
                if ((dfType == null) || (dfValue == null) || (dfValue.length() == 0)) {
                    Controller.logger.error("DataField name = [" + dataName
                            + "] has " + "NULL type or value for process " + pf.getProcessId());
                    Controller.logger.error("Data field type = [" + dfType
                            + "].");
                    Controller.logger.error("Data field value = [" + dfValue
                            + "].");
                    throw new RuntimeException("Unable to process transition(s).");
                }

                FieldComparator fc = FieldComparator.create(dfType, operation);
                boolean result = fc.compare(dfValue, transValue);


                if (result) {
                    readyActivities.add(nextAct); 
                }                
            } catch (Exception e) {
                Controller.logger.error(e.getMessage(), e);
                resp.addError(e.getMessage());
            	throw new RuntimeException(e);
            }
        }

        for (i = 0; i < tt.length; i++) {

            // Get the data name, operation, and selection value from the
            // Transition.

            
//            //  Debug logging if targeted from id
//            boolean logFlag = false;
//            if(new Integer(806).equals(t.getFromId())) logFlag = true;
//            if(new Integer(1006).equals(t.getFromId())) logFlag = true;
//            if(new Integer(121).equals(t.getFromId())) logFlag = true;

            try {
            	t = tt[i];
            	Integer toActId = t.getToId();
            	Activity nextAct = pf.getActivity(toActId);
            	String dataName = t.getDataName();
            	String operation = t.getOperation();
            	String transValue = t.getDataValue();
                // Get the DataField corresponding to the Transition's data
                // name.
                // Get the data field type and value.

                DataField df = pf.getDataField(dataName);
                String dfValue = df.getDataValue();
                String dfType = DataTypeDef.getData().getItems().getItemDesc(
                        df.getDataTypeId()); // "String";
                // //dtlType.getObjectType();
                if ((dfType == null) || (dfValue == null) || (dfValue.length() == 0)) {
                    Controller.logger.error("DataField name = [" + dataName
                            + "] has " + "NULL type or value for process " + pf.getProcessId());
                    Controller.logger.error("Data field type = [" + dfType
                            + "].");
                    Controller.logger.error("Data field value = [" + dfValue
                            + "].");
                    throw new RuntimeException("Unable to process transition(s).");
                }

                // Use a FieldComparator to do the comparison. An exception here
                // means that a developer has goofed up.

                FieldComparator fc = FieldComparator.create(dfType, operation);
                boolean result = fc.compare(dfValue, transValue);
//                if(logFlag) {
//                    logger.error("INFO ONLY for dennis/reza: Compare returned " + (result?"True":"False")+ " based upon " + t.toPrintable());
//                    logger.error("INFO " + "dfType=" + dfType + ", operation=" + operation + ",dfValue=" + dfValue + ", transValue=" + transValue);
//                }

                // If the transition criteria was met, we will send a "ready".
                // If not, we will send a "skip".

                if (!result) {
                	// Create a Controller for the next Activity and execute it.

                    c = Controller.createController(WorkFlowEngine.ACTION_SKIP, 
                            nextAct);
                    WorkFlowRequest request = new WorkFlowRequest();
                    String doNoSkipId = null;
                    for (Activity a : readyActivities) {
                    	doNoSkipId = a.getActivityId().toString();
                    }
                    request.addDataPair(WorkFlowEngine.DO_NOT_SKIP, doNoSkipId);
                    c.execute(resp, request);
                }
                
                
            } catch (Exception e) {
                Controller.logger.error(e.getMessage(), e);
                resp.addError(e.getMessage());
            	throw new RuntimeException(e);
            }
        }
        for (Activity a : readyActivities){
        	c = Controller.createController(WorkFlowEngine.ACTION_READY, a);
        	WorkFlowRequest wfr = new WorkFlowRequest();
        	// Since completing activity do not want to change assigned user
        	// logger.error("DWL: processTransitions:  Ready for Action_Ready", new Exception()); // #3206
        	wfr.setLeaveUserSame(true);  // #3206
        	c.execute(resp, wfr);
        }
        
    }
}

