package us.oh.state.epa.stars2.workflow.engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.SystemException;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

/**
 * Title: WorkFlowManager.
 * 
 * <p>
 * Description: The WorkFlowManager provides the interface between OSSM
 * operational components and the workflow engine. Objects wishing to use the
 * services of the workflow engine instantiate a WorkFlowManager and then use
 * the manager to communicate with the engine.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author S. Wooster, J. E. Collier
 */
//@Component
@SuppressWarnings("serial")
public class WorkFlowManager implements java.io.Serializable {
	
    static final String START_UP = "Startup";

	public static final String ROLE_DISCRIMINATOR = WorkFlowEngine.ROLE_DISCRIMINATOR;
	
	public static final String ASSIGNED_USER = WorkFlowEngine.ASSIGNED_USER;
	
    // The following define default values related to setting up the socket
    // connection to the workflow engine.
    private static Logger logger = Logger.getLogger(WorkFlowManager.class);

    /**
     * Constructor.
     */
    public WorkFlowManager() {
    }
    
    /**
     * Submits a proposal to the workflow engine. Basically, this method calls
     * "submitComposite()" with a 'sales flow' tag.
     * 
     * @param dod
     *            See "submitComposite()" for required parameters.
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public WorkFlowResponse submitProposal(Integer orderId, Integer accountId,
            Integer userId, LinkedHashMap<Integer, Integer> serviceOrders) {
        WorkFlowResponse wfr = this.submitComposite("sfa", orderId, accountId,
                userId, serviceOrders);

        // If we already have an error, just return.
        if (wfr.hasError() || wfr.hasFailed()) {
            return wfr;
        }

        // See if the Workflow engine successfully created a ProcessTemplate
        // for this order. If not, we need to roll the order back.
        boolean accepted = this.validateComposite("Proposal", orderId);

        if (!accepted) {
            wfr.addError("The Workflow Engine failed to create a sales flow "
                    + "for Proposal " + orderId.toString() + ".");

            wfr.setRollbackNeeded(true);
        }

        return wfr;
    }

    public final WorkFlowResponse cancelProcess(Integer processId, Integer userId) {
        WorkFlowRequest wfr = createWorkFlowRequest(WorkFlowEngine.ACTION_CANCEL_PROCESS, processId, null);

        wfr.setUserId(userId);

        // Send the workflow request object to the workflow engine.
        // When we get a response back, check it for errors or failures.
        // Any error or failure is an exception.
        return this.sendWorkFlowRequest(wfr, true);
    }
    
    public final WorkFlowResponse cancelProcess(Integer processId, Integer userId, String note) {
        WorkFlowRequest wfr = createWorkFlowRequest(WorkFlowEngine.ACTION_CANCEL_PROCESS, processId, null);

        wfr.setUserId(userId);
        
        if(!Utility.isNullOrEmpty(note.trim())){
        	wfr.addDataPair(WorkFlowRequest.CANCEL_CUSTOM_NOTE, note);
        }

        // Send the workflow request object to the workflow engine.
        // When we get a response back, check it for errors or failures.
        // Any error or failure is an exception.
        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Submits a composite request (an order or proposal) to the workflow
     * engine. The "type" identifies the workflow path to follow, either
     * provisioning (for orders) or sales flow (for proposals). The
     * DynamicObject must contain an "order-id" (the proposal Id is an order
     * Id), the "account-id" for the account that this order is associated with,
     * the "user-id" of the user submitting the composite, and one or more
     * "service-order" pairs consisting of a "service Id" and the associated
     * "activity template def Id". The "activity template def Id" identifies the
     * workflow to use for orders of this type, e.g., "new service", "move",
     * etc., or the sales flow for proposals for this service.
     * 
     * @param type
     *            Identifies the workflow type, either provisioning or sales
     *            flow.
     * @param dod
     *            DynamicObject containing input parameters.
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public final WorkFlowResponse submitComposite(String type, Integer orderId,
            Integer accountId, Integer userId,
            LinkedHashMap<Integer, Integer> serviceOrders) {
        // Create the request object and fill in some values.
        WorkFlowRequest wfr = new WorkFlowRequest(
                WorkFlowEngine.ACTION_COMPOSITE);

        wfr.setType(type);
        wfr.setOrderId(orderId);
        wfr.setAccountId(accountId);
        wfr.setUserId(userId);

        // Service Orders will come in pairs, ServiceId +
        // ActivityTemplateDefId.
        for (Integer serviceId : serviceOrders.keySet()) {
            Integer templateId = serviceOrders.get(serviceId);

            wfr.addServicePair(serviceId, templateId);
        }

        // Send the workflow request object to the workflow engine.
        // When we get a response back, check it for errors or failures.
        // Any error or failure is an exception.

        WorkFlowResponse resp = this.sendWorkFlowRequest(wfr, true);

        return resp;
    }

    /**
     * Submits a init request to the workflow engine to create a new process.
     * The "type" identifies the workflow path to follow, either provisioning
     * (for orders) or sales flow (for proposals).
     * 
     * @param ptid
     *            Identifies the process template
     * @param LinkedHashMap
     *            Workflow revelent data
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public final WorkFlowResponse submitProcess(Integer ptid, Integer orderId,
            Integer accountId, Integer userId, String expedite,
            Timestamp startDt, Timestamp dueDt, HashMap<String, String> data) {
        if(ptid == null) {
            logger.error("Process Template Id is null--THIS WILL CAUSE AN ERROR. OrderId=" + orderId +
                 ", accountId=" + accountId);
        }
        // Create the request object and fill in some values.
        WorkFlowRequest wfr = new WorkFlowRequest(WorkFlowEngine.ACTION_INIT);

        wfr.setWorkFlow(ptid);
        wfr.setOrderId(orderId);
        wfr.setAccountId(accountId);
        wfr.setUserId(userId);
        wfr.setExpedite(expedite);
        wfr.setStartDt(startDt);
        wfr.setDueDt(dueDt);

        // Service Orders will come in pairs, ServiceId +
        // ActivityTemplateDefId.
        if (data != null) {
            for (String dataName : data.keySet()) {
                String dataValue = data.get(dataName);

                wfr.addDataPair(dataName, dataValue);
            }
        }

        // Send the workflow request object to the workflow engine.
        // When we get a response back, check it for errors or failures.
        // Any error or failure is an exception.
        return this.sendWorkFlowRequest(wfr, true);
    }
    
    public final WorkFlowResponse submitProcess(Integer ptid, Integer orderId,
            Integer accountId, Integer userId, String expedite,
            Timestamp startDt, Timestamp dueDt, HashMap<String, String> data, Transaction trans) {
        if(ptid == null) {
            logger.error("Process Template Id is null--THIS WILL CAUSE AN ERROR. OrderId=" + orderId +
                 ", accountId=" + accountId);
        }
        // Create the request object and fill in some values.
        WorkFlowRequest wfr = new WorkFlowRequest(WorkFlowEngine.ACTION_INIT);

        wfr.setWorkFlow(ptid);
        wfr.setOrderId(orderId);
        wfr.setAccountId(accountId);
        wfr.setUserId(userId);
        wfr.setExpedite(expedite);
        wfr.setStartDt(startDt);
        wfr.setDueDt(dueDt);
        wfr.setTransaction(trans);

        // Service Orders will come in pairs, ServiceId +
        // ActivityTemplateDefId.
        if (data != null) {
            for (String dataName : data.keySet()) {
                String dataValue = data.get(dataName);

                wfr.addDataPair(dataName, dataValue);
            }
        }

        // Send the workflow request object to the workflow engine.
        // When we get a response back, check it for errors or failures.
        // Any error or failure is an exception.
        return this.sendWorkFlowRequest(wfr, true);
    }   

    /**
     * Handles activity "check-in" and "save" requests. These are very similar
     * in how they handle workflow data, but "check-in" marks an Activity as
     * completed and may have additional state transitions. From the workflow
     * manager's perspective, processing is identical, except we change the
     * <tt>action</tt> string.
     * 
     * @param action
     *            Identifies whethere we are doing a check-in or save.
     * @param processId
     *            Identifies the activity being checked out.
     * @param activityId
     *            Identifies the activity being checked out.
     * @param userId
     *            The user who is checking the activity out.
     * @param tdata
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    protected final WorkFlowResponse checkInSave(String action, Integer processId,
            Integer activityId, Integer userId,
            LinkedHashMap<String, String> data) {
        WorkFlowRequest wfr = createWorkFlowRequest(action, processId, activityId);

        wfr.setUserId(userId);
        // Service Orders will come in pairs, ServiceId +
        // ActivityTemplateDefId.
        if (data != null) {
            for (String dataName : data.keySet()) {
                String dataValue = data.get(dataName);
                wfr.addDataPair(dataName, dataValue);
            }
        }

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Checks in an assigned Activity.
     * 
     * @param processId
     *            Identifies the activity being checked out.
     * @param activityId
     *            Identifies the activity being checked out.
     * @param userId
     *            The user who is checking the activity out.
     * @param tdata
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public final WorkFlowResponse checkIn(Integer processId, Integer activityId,
            Integer userId, LinkedHashMap<String, String> tdata) {
        return this.checkInSave(WorkFlowEngine.ACTION_CHECK_IN, processId,
                activityId, userId, tdata);
    }
    
    public final WorkFlowResponse checkInTo(Integer processId, Integer activityId) {
        return this.checkInSave(WorkFlowEngine.ACTION_CHECK_IN_TO, processId,
                activityId, 1, null);
    }

    /**
     * Saves service data for an Activity currently checked out to "userId". The
     * Activity is left in the "checked out" state.
     * 
     * @param processId
     *            Identifies the activity being checked out.
     * @param activityId
     *            Identifies the activity being checked out.
     * @param userId
     *            The user who is checking the activity out.
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public final WorkFlowResponse saveState(Integer processId, Integer activityId,
            Integer userId, LinkedHashMap<String, String> tdata) {
        return this.checkInSave(WorkFlowEngine.ACTION_SAVE, processId,
                activityId, userId, tdata);
    }

    /**
     * Sends a "ping" or "are you there?" message to the workflow engine. If the
     * workflow engine is alive and well, it will send a "success" response
     * back.
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public final WorkFlowResponse ping() {
        return this.sendWorkFlowRequest(new WorkFlowRequest(WorkFlowEngine.ACTION_PING), true);
    }

    /**
     * Checks out an Activity, i.e., marks the Activity as assigned to a user
     * for work. An activity that is checked out by one user may not be accessed
     * or altered by another user. The user that has checked out an Activity
     * must eventually check it in again (most common) or release it.
     * 
     * @param processId
     *            Identifies the activity being checked out.
     * @param activityId
     *            Identifies the activity being checked out.
     * @param userId
     *            The user who is checking the activity out.
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public final WorkFlowResponse checkOut(Integer processId, Integer activityId,
            Integer userId) {
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_CHECK_OUT, processId, activityId);

        wfr.setUserId(userId);

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Skips an Activity, i.e., marks the Activity as "bp passed". Skipping this
     * Activity may also cause other, downstream Actitivies to be skipped.
     * 
     * @param processId
     *            Part of the Activity key.
     * @param activityId
     *            Part of the Activity key.
     * @param userId
     *            The user who is requesting this action.
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public final WorkFlowResponse skip(Integer processId, Integer activityId,
            Integer userId, LinkedHashMap<String, String> data) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_SKIP, processId, activityId);

        wfr.setUserId(userId);
        // Service Orders will come in pairs, ServiceId +
        // ActivityTemplateDefId.
        if (data != null) {
            for (String dataName : data.keySet()) {
                String dataValue = data.get(dataName);
                wfr.addDataPair(dataName, dataValue);
            }
        }

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Releases a checked out Activity, i.e., unassigns it from the user and
     * returns the Activity to the "ready" state. The Activity must be checked
     * out by this user in order for this to succeed.
     * 
     * @param processId
     *            Identifies the activity being checked out.
     * @param activityId
     *            Identifies the activity being checked out.
     * @param userId
     *            The user who is checking the activity out.
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */

    public final WorkFlowResponse release(Integer processId, Integer activityId,
            Integer userId) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_RELEASE, processId, activityId);

        wfr.setUserId(userId);

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Retries an automatic Activity that is currently "blocked" due to a
     * failure on a previous attempt. The Activity must be in a "blocked" state
     * or the retry will not be attempted.
     * 
     * @param processId
     *            Identifies the activity being re-attempted.
     * @param activityId
     *            Identifies the activity being re-attempted.
     * @param userId
     *            The user who is making the request.
     * 
     * @return WorkFlowResponse Messages from the workflow engine.
     */
    public final WorkFlowResponse retry(Integer processId, Integer activityId,
            Integer userId) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_RETRY, processId, activityId);

        wfr.setUserId(userId);

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Undoes (or unassigns) a check-out on an Activity. This is similar to
     * "release()" in effect, but "undo" allows an Administrator the ability to
     * un-assign an Activity, i.e., return it to the "Ready" state.
     * 
     * @param processId
     *            Integer The process Id of the Activity to be unassigned.
     * @param activityId
     *            Integer The activity Id of the Activity to be unassigned.
     * @param userId
     *            Integer The Id of the user making the request.
     * 
     * @return WorkFlowResponse Response from the workflow engine.
     */
    public final WorkFlowResponse unDo(Integer processId, Integer activityId,
            Integer userId) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_UNDO, processId, activityId);

        // Put our parameters into the workflow request object. Then send it
        // to the workflow engine. When we get a response back, check it
        // for errors or failures. Any error or failure is an exception.
        wfr.setUserId(userId);

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Undoes (or unassigns) a check-out on an Activity. This is similar to
     * "release()" in effect, but "undo" allows an Administrator the ability to
     * un-assign an Activity, i.e., return it to the "Ready" state.
     * 
     * @param processId
     *            Integer The process Id of the Activity to be unassigned.
     * @param activityId
     *            Integer The activity Id of the Activity to be unassigned.
     * 
     * @return WorkFlowResponse Response from the workflow engine.
     */
    public final WorkFlowResponse redo(Integer processId, Integer activityId) {
        return this.sendWorkFlowRequest(createWorkFlowRequest(
                WorkFlowEngine.ACTION_REDO, processId, activityId), true);
    }

    /**
     * Refer a check-out Activity.
     * 
     * @param processId
     *            Integer The process Id of the Activity to be unassigned.
     * @param activityId
     *            Integer The activity Id of the Activity to be unassigned.
     * @param userId
     *            Integer The Id of the user making the request.
     * @param extendProcessEndDate
     * 
     * @return WorkFlowResponse Response from the workflow engine.
     */
    public final WorkFlowResponse refer(Integer processId, Integer activityId,
            Integer userId, Timestamp dueDt, Integer activityReferralTypeId,
            String extendProcessEndDate) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_REFER, processId, activityId);

        // Put our parameters into the workflow request object. Then send it
        // to the workflow engine. When we get a response back, check it
        // for errors or failures. Any error or failure is an exception.
        wfr.setUserId(userId);
        wfr.setDueDt(dueDt);
        // activityReferralTypeId use contact id
        wfr.setContactId(activityReferralTypeId);
        // extendProcessEndDate use expedite to pass.
        wfr.setExpedite(extendProcessEndDate);

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Refer a check-out Activity.
     * 
     * @param processId
     *            Integer The process Id of the Activity to be unassigned.
     * @param activityId
     *            Integer The activity Id of the Activity to be unassigned.
     * @param userId
     *            Integer The Id of the user making the request.
     * 
     * @return WorkFlowResponse Response from the workflow engine.
     */
    public final WorkFlowResponse unRefer(Integer processId, Integer activityId,
            Integer loopCnt, Integer userId, Timestamp endDate) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_UNREFER, processId, activityId);

        // Put our parameters into the workflow request object. Then send it
        // to the workflow engine. When we get a response back, check it
        // for errors or failures. Any error or failure is an exception.
        wfr.setLoopCnt(loopCnt);
        wfr.setUserId(userId);
        wfr.setEndDt(endDate);

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Reassigns a checked out Activity to another user. This is intended for
     * Administrators. The process Id and activity Id identify specific activity
     * that is being reassigned. The new user Id is the Id of the user who will
     * be assigned the Activity.
     * 
     * @param processId
     *            Integer
     * @param activityId
     *            Integer
     * @param newUserId
     *            Integer User who will be assigned this activity.
     * 
     * @return WorkFlowResponse Response from the workflow engine.
     */
    public final WorkFlowResponse reAssign(Integer processId, Integer activityId,
            Integer newUserId) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_REASSIGN, processId, activityId);

        // Put our parameters into the workflow request object. Then send it
        // to the workflow engine. When we get a response back, check it
        // for errors or failures. Any error or failure is an exception.
        wfr.setUserId(newUserId);

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * Extracts returned data field values from the WorkFlowResponse and returns
     * them in a new DynamicObject. The "originals" DynamicObject may be null.
     * If it is, a new DynamicObject is created and returned to caller. If the
     * "originals" DynamicObject is not null, it is reused and returned to the
     * caller.
     * 
     * @param originals
     *            Set of original service details.
     * @param resp
     *            Response from the workflow engine.
     * 
     * @return LinkedHashMap containing workflow details.
     */
    protected final LinkedHashMap<String, String> extractWorkflowData(
            LinkedHashMap<String, String> originals, WorkFlowResponse resp) {
        LinkedHashMap<String, String> dataFields;

        // If we don't have original service detail values, create a new
        // DynamicObject to hold service detail values. Otherwise, re-use
        // the one we were given in the input.
        if (originals != null) {
            dataFields = originals;
        } else {
            dataFields = new LinkedHashMap<String, String>();
        }

        int fieldCnt = resp.getDataCount();
        int ii;
        String dataName;
        String dataValue;

        // Ask the workflow response to give us the data name, type, and
        // value for each service detail.

        for (ii = 0; ii < fieldCnt; ii++) {
            dataName = resp.getDataName(ii);
            // customDetailTypeId = resp.getCustomDetailTypeId(ii);
            dataValue = resp.getDataValue(ii);

            dataFields.put(dataName, dataValue);
        }

        return dataFields;
    }

    /**
     * Copies name-value data pairs from <code>data</code> to <code>req</code>.
     * The data pairs correspond to workflow data fields and will be updated in
     * the database by the workflow engine.
     * 
     * @param data
     *            LinkedHashMap containing data name-value pairs.
     * @param req
     *            WorkFlowRequest these name-value pairs are added to.
     */
    protected final void saveWorkflowData(LinkedHashMap<String, String> data,
            WorkFlowRequest req) {
        for (String dataName : data.keySet()) {
            String dataValue = data.get(dataName);
            req.addDataPair(dataName, dataValue);
        }
    }

    /**
     * Verifies that the ProcessTemplate for this composite was successfully
     * created in the database by the workflow engine. Returns "true" if the
     * ProcessTemplate was found, or "false" if it was not found (or a database
     * access error was detected). The "type" should be either "Order" or
     * "Proposal" and "id" is the corresponding order Id or proposal Id.
     * 
     * @param type
     *            String, either "Order" or "Proposal".
     * @param id
     *            Integer order Id or proposal Id.
     * 
     * @return boolean "true" if ProcessTemplate for this order/proposal was
     *         found.
     */
    protected final boolean validateComposite(String type, Integer id) {
        boolean result = false;

        try {
//            ReadWorkFlowService wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
        	ReadWorkFlowService wfBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
            ProcessTemplate pt = wfBO.retrieveProcessTemplateForComposite(type, id);
            result = (pt != null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * Sends a WorkFlowRequest object to the work flow engine, reads the
     * WorkFlowResponse from the workflow engine, and returns the response to
     * caller without evaluating the response in any way. Note that several
     * statements in this method are commented out. These are intended to assist
     * in debugging and testing.
     * 
     * @param wfr
     *            WorkFlowRequest
     * @param restart TODO
     * 
     * @return WorkFlowResponse Response from the workflow engine.
     */
    synchronized protected WorkFlowResponse sendWorkFlowRequest(
            WorkFlowRequest wfr, boolean restart) {
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        WorkFlowResponse resp = null;

        boolean rollbackNeeded = true;

        try {
            // Get a socket connection to the workflow engine. Create a
            // couple of Object stream decorators so we can write an object
            // to the socket.

        	logger.debug(" Getting workflow socket");
            socket = this.getWorkFlowSocket();
            
            logger.debug(" Creating workflow output stream");
            oos = new ObjectOutputStream(socket.getOutputStream());
            
            logger.debug(" Creating workflow input stream");
            ois = new ObjectInputStream(socket.getInputStream());

            oos.writeObject(wfr);
            oos.flush();
            rollbackNeeded = false;
            
            resp = (WorkFlowResponse) (ois.readObject());

            if (resp.hasError() || resp.hasFailed()) {
                rollbackNeeded = true;
            }
        } catch (ConnectException ce) {
            // If the connection is refused by the WorkFlow Engine, it doesn't
            // provide a useful message. Catch the exception and create an
            // error message with something useful in it.
            if (!START_UP.equalsIgnoreCase(wfr.getType())) {
                logger.error(wfr.toString(), ce);
            }
            if (restart){
                try {
                    logger.debug("WorkFlow engine not found.  Try to restart...");
                    CompMgr.newInstance("app.WorkFlow");
                    resp = sendWorkFlowRequest(wfr, false);
                } catch (UnableToStartException e) {
                    logger.error(wfr.toString(), e);
                    resp = new WorkFlowResponse();
                    resp.addError("Connection refused by WorkFlow Engine and cannot restart it.");
                    resp.addRecommendation("Talk to system administrator.");
                    resp.setRollbackNeeded(true);
                }
            }else{
                resp = new WorkFlowResponse();
                resp.addError("Connection refused by WorkFlow Engine.");
                resp.addRecommendation("Restart the engine and retry the request.");
                resp.setRollbackNeeded(true);
            }
        } catch (UnknownHostException uhe) {
            // This is another case where the exception doesn't really provide
            // a useful message. Catch it and create something useful.
            if (restart){
                try {
                    logger.debug("WorkFlow engine Unknown Host.  Try to restart...");
                    CompMgr.newInstance("app.WorkFlow");
                    resp = sendWorkFlowRequest(wfr, false);
                } catch (UnableToStartException e) {
                    logger.error(wfr.toString(), e);
                    resp = new WorkFlowResponse();
                    resp.addError("Unknown Host and cannot restart engine.");
                    resp.addRecommendation("Talk to system administrator.");
                    resp.setRollbackNeeded(true);
                }
            }else{
                logger.error(wfr.toString(), uhe);
                resp = new WorkFlowResponse();
                resp.addError("Unknown Host: [" + uhe.getMessage() + "].");
                resp.addRecommendation("Restart the engine and retry the request.");
                resp.setRollbackNeeded(true);
            }
        } catch (NullPointerException npe) {
            logger.error(wfr.toString(), npe);
            resp = new WorkFlowResponse();
            resp.addError("Data error in database. Restart the engine and retry the request.");
            resp.setRollbackNeeded(true);
        } catch (SocketTimeoutException ste) {
            if (restart){
                logger.debug("SocketTimeoutException. Sending request second time.", ste);
                resp = sendWorkFlowRequest(wfr, false);
            }else{
                logger.error(wfr.toString(), ste);
                resp = new WorkFlowResponse();
                InetAddress addr = socket.getInetAddress();
                resp.addError("Engine host address : " + addr.getHostAddress() 
                        + ". Watting for engine time is out.  Check log on the engine host to find out the problem.");
                resp.setRollbackNeeded(true);
            }
        } catch (SystemException se){
            if (restart){
                try {
                	//TODO RESOLVE
//                    Object we = CompMgr.newInstance("app.WorkFlow");
//                    if (we != null) {
//                        DaemonInfo dInfo = ((WorkFlowEngine) we).getDInfo();
//                        workFlowEngine.updateDaemonInfo(dInfo, true);
//                    }
                    resp = sendWorkFlowRequest(wfr, false);
                } catch (Exception e) {
                    logger.error(wfr.toString(), e);
                    resp = new WorkFlowResponse();
                    resp.addError("Unknown Host and cannot restart engine.");
                    resp.addRecommendation("Talk to system administrator.");
                    resp.setRollbackNeeded(true);
                }
            }else{
                logger.error(wfr.toString(), se);
                resp = new WorkFlowResponse();
                resp.addError("Workflow processing failed.");
                resp.addRecommendation("Retry the request momentarily.");
                resp.setRollbackNeeded(true);
            }
        } catch (Exception e) {
            if (!START_UP.equalsIgnoreCase(wfr.getType())) {
                logger.error(wfr.toString(), e);
            }
            if (restart){
                try {
                    logger.error("This is a very bad exception. Catch add in is needed.");
                    logger.error(e.toString() + " Try to restart engine...");
                    CompMgr.newInstance("app.WorkFlow");
                    resp = sendWorkFlowRequest(wfr, false);
                } catch (UnableToStartException se) {
                    logger.error(wfr.toString(), se);
                    resp = new WorkFlowResponse();
                    resp.addError(e.toString() + " and cannot restart engine.");
                    resp.addRecommendation("Talk to system administrator.");
                    resp.setRollbackNeeded(true);
                }
            }else{
                resp = new WorkFlowResponse();
                logger.error(e.toString() + " Rolling back then restarting Workflow engine.");
                resp.addError("Workflow processing failed.");
                resp.addRecommendation("Retry the request momentarily.");
                resp.setRollbackNeeded(rollbackNeeded);
            }
        } finally {
        	wfr.setTransaction(null);
        	
            if (oos != null) {
                try {
                    logger.debug("Closing the ObjectOutputStream.");
                    oos.close(); // I hate having to do this...
                } catch (IOException ioe) {
                    logger.error(wfr.toString(), ioe);
                } catch (Exception e) {
                    logger.error("Unhandle exception in oos.close.  This should never happen.");
                    logger.error(wfr.toString(), e);
                }
            }

            if (ois != null) {
                try {
                    logger.debug("Closing the ObjectInputStream.");
                    ois.close();
                } catch (IOException ioe) {
                    logger.error(wfr.toString(), ioe);
                } catch (Exception e) {
                    logger.error("Unhandle exception in ois.close.  This should never happen.");
                }
            }

            if (socket != null) {
                try {
                    logger.debug("Closing the Socket.");
                    socket.close();
                } catch (IOException ioe) {
                    logger.error(wfr.toString(), ioe);
                } catch (Exception e) {
                    logger.error("Unhandle exception in socket.close.  This should never happen.");
                }
            }
        }

        if (resp != null) {
            logger.debug("Response = " + resp.toString());
        }
        
        return resp;
    }

    /**
     * Returns a socket connection to the workflow engine currently associated
     * with this database. The socket is conditioned with a read timeout value
     * of 20 seconds, i.e., if the manager attempts to read from the socket, but
     * gets nothing for 20 seconds, then a SocketTimeoutException will be
     * thrown.
     * 
     * @return Socket A socket connection to the workflow engine.
     * 
     * @throws java.lang.Exception
     *             Database access error or Socket exception.
     */
    protected final Socket getWorkFlowSocket() throws Exception {
//        DaemonInfo dInfo = null;
        Socket socket = null;

        // When the Workflow engine initialized, it wrote its hostname and
        // port number to the database. We will retrieve them and use them
        // to set up our socket.
//        String hostName = InetAddress.getLocalHost().getHostName();
//        logger.debug("Searching for hostname: " + hostName);
//        dInfo = WorkFlowEngine.lookupDaemonInfo(hostName);
        
//        if (dInfo == null){
//            throw new SystemException("Cannot find workflow engine from DB.");
//        }
        
        
        
        
        
        // decouple workflow ... get wf engine hostname:port from environmental entries
        
        String hostName = "";
        Integer port = null;
        
		String hostJndiName = Config.findNode("app.workflowEngine.host")
				.getAsString("jndiName");
		String portJndiName = Config.findNode("app.workflowEngine.port")
				.getAsString("jndiName");
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			hostName = (String) envContext.lookup(hostJndiName);
			port = (Integer) envContext.lookup(portJndiName);
		} catch (NamingException ne) {
			throw new RuntimeException(ne);
		}


        // Create the socket and assign it to the host name and port number
        // for the workflow engine.
        socket = new Socket();
//        InetSocketAddress addr = new InetSocketAddress(dInfo.getHostName(),
//                dInfo.getPortNumber());
        InetSocketAddress addr = new InetSocketAddress(hostName,port);

        // Set a Connection timeout value in milliseconds. Connect the
        // socket to the workflow engine and return it.
        // int timeout = 300000 ; // Increase wait for debugging
        int timeout = 60000;

        socket.connect(addr, timeout);
//        socket.setSoTimeout(1); //TODO uncomment socket timeout

        return socket;
    }

    /**
     * 
     * 
     * @param processId
     *            Integer
     * @param activityId
     *            Integer
     * @param jeopardyDt
     * @param dueDt
     * 
     * @return WorkFlowResponse Response from the workflow engine.
     */
    public final WorkFlowResponse changeDueDate(Integer processId,
            Integer activityId, Timestamp jeopardyDt, Timestamp dueDt) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_CHANGE_DUE_DATE, processId, activityId);
 
        // Put our parameters into the workflow request object. Then send it
        // to the workflow engine. When we get a response back, check it
        // for errors or failures. Any error or failure is an exception.
        wfr.setJeopardyDt(jeopardyDt);
        wfr.setDueDt(dueDt);

        return this.sendWorkFlowRequest(wfr, true);
    }

    /**
     * @param processId
     * @param activityId
     * @param startDt
     * @return
     */
    public final WorkFlowResponse changeStartDate(Integer processId,
            Integer activityId, Timestamp startDt) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_CHANGE_START_DATE, processId, activityId);

        // Put our parameters into the workflow request object. Then send it
        // to the workflow engine. When we get a response back, check it
        // for errors or failures. Any error or failure is an exception.
        wfr.setStartDt(startDt);

        return this.sendWorkFlowRequest(wfr, true);
    }
    
    private WorkFlowRequest createWorkFlowRequest(String action, Integer processId,
            Integer activityId) {
        WorkFlowRequest wfr = new WorkFlowRequest(action);
        
        wfr.setProcessId(processId);
        wfr.setActivityId(activityId);
        
        return wfr;
    }

	public WorkFlowResponse updateActivityViewedState(Integer processId, Integer activityId) {
        WorkFlowRequest wfr = createWorkFlowRequest(
        		WorkFlowEngine.ACTION_UPDATE_ACTIVITY_VIEWED_STATE, processId, activityId);
        return this.sendWorkFlowRequest(wfr, true);
	}

    public WorkFlowResponse saveNote(Integer processId, Integer userId, String value) {
    	WorkFlowRequest wfr = new WorkFlowRequest(WorkFlowEngine.ACTION_SAVE_NOTE);
    	wfr.setProcessId(processId);
    	wfr.setUserId(userId);
    	wfr.addDataPair(WorkFlowRequest.SAVE_NOTE_VALUE, value);
        return this.sendWorkFlowRequest(wfr, true);
    }
    
	public WorkFlowResponse modifyNote(Integer processId, Integer userId,
			Integer noteId, Integer lastModified, String value) {
    	WorkFlowRequest wfr = new WorkFlowRequest(WorkFlowEngine.ACTION_MODIFY_NOTE);
    	wfr.setProcessId(processId);
    	wfr.setUserId(userId);
    	wfr.addDataPair("noteId", String.valueOf(noteId));
    	wfr.addDataPair("lastModified", String.valueOf(lastModified));
    	wfr.addDataPair(WorkFlowRequest.SAVE_NOTE_VALUE, value);
        return this.sendWorkFlowRequest(wfr, true);
	}

	public WorkFlowResponse changeEndDate(Integer processId, Integer activityId,
			Timestamp endDt, Integer userId) {
        // Create the request object and send it to the engine.
        WorkFlowRequest wfr = createWorkFlowRequest(
                WorkFlowEngine.ACTION_CHANGE_END_DATE, processId, activityId);

        // Put our parameters into the workflow request object. Then send it
        // to the workflow engine. When we get a response back, check it
        // for errors or failures. Any error or failure is an exception.
        wfr.setEndDt(endDt);
        wfr.setUserId(userId);

        return this.sendWorkFlowRequest(wfr, true);
	}

	public WorkFlowResponse removeProcessFlows(Integer processId,
			Integer userId) {
    	WorkFlowRequest wfr = new WorkFlowRequest(WorkFlowEngine.ACTION_REMOVE_PROCESS_FLOWS);
    	wfr.setProcessId(processId);
    	wfr.setUserId(userId);
        return this.sendWorkFlowRequest(wfr, true);
	}

	public WorkFlowResponse createTaskProcessFlows(Integer processTemplateId,
			Integer fpId, Integer externalId, String expedite, Timestamp startDt,
			Timestamp dueDt, Integer createBy, HashMap<String, String> data) {
        if(processTemplateId == null) {
            logger.error("Process Template Id is null--THIS WILL CAUSE AN ERROR. OrderId=" + externalId +
                 ", accountId=" + fpId);
        }
        // Create the request object and fill in some values.
        WorkFlowRequest wfr = new WorkFlowRequest(WorkFlowEngine.ACTION_CREATE_TASK_PROCESS_FLOWS);

        wfr.setWorkFlow(processTemplateId);
        wfr.setOrderId(externalId);
        wfr.setAccountId(fpId);
        wfr.setUserId(createBy);
        wfr.setExpedite(expedite);
        wfr.setStartDt(startDt);
        wfr.setDueDt(dueDt);

        // Service Orders will come in pairs, ServiceId +
        // ActivityTemplateDefId.
        if (data != null) {
            for (String dataName : data.keySet()) {
                String dataValue = data.get(dataName);

                wfr.addDataPair(dataName, dataValue);
            }
        }

        // Send the workflow request object to the workflow engine.
        // When we get a response back, check it for errors or failures.
        // Any error or failure is an exception.
        return this.sendWorkFlowRequest(wfr, true);
	}
	
	public WorkFlowResponse cloneProcess(WorkFlowProcess process) {
		//processid
		//userid
		//externalid
    	WorkFlowRequest wfr = new WorkFlowRequest(WorkFlowEngine.ACTION_CLONE_PROCESS);
    	wfr.setProcessId(process.getProcessId());
    	wfr.setUserId(process.getUserId());
    	wfr.setExternalId(process.getExternalId());
        return this.sendWorkFlowRequest(wfr, true);
	}
}
