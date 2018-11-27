package us.oh.state.epa.stars2.workflow.engine;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;

import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: ActivityController
 * </p>
 * 
 * <p>
 * Description: This is the base class for Activity controllers. This class
 * provides common functionality needed by derived Activity controller classes.
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

abstract public class ActivityController extends Controller {
    protected static final String YES = "Y";
    private static final Integer STRING_CUSTOM_DETAIL_ID = new Integer(9);
    protected Activity activity;
    protected Integer orderId;

    /**
     * Constructor. Takes a reference to the Activity being controlled.
     * 
     * @param a
     *            Activity being operated on by controller.
     */
    public ActivityController(Activity a) {
        activity = a;
        ProcessFlow pf = a.getContainer();
        orderId = pf.getExternalId();
    }

    /**
     * Saves the Activity to the database. The Activity was previously assigned
     * via the constructor. Throws a DatabaseUpdateException if the update
     * fails.
     * 
     * @throws DAOException
     *             Database update failure.
     */
    protected void saveToDatabase() throws DAOException {
        try {
//            WorkFlowService workFlowBO = ServiceFactory.getInstance().getWorkFlowService();
            WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
            workFlowBO.updateActivity(activity);
//        } catch (ServiceFactoryException sfe) {
//            Controller.logException(sfe);
        } catch (RemoteException re) {
            Controller.logException(re);
            throw new DAOException(re.getMessage(),re);
        }
    }

    /**
     * Saves the Activity to the database. The Activity was previously assigned
     * via the constructor. Throws a DatabaseUpdateException if the update
     * fails.
     * 
     * @param trans
     * @throws DAOException
     */
    protected void saveToDatabase(Transaction trans) throws DAOException {
//        try {
//          WorkFlowService workFlowBO = ServiceFactory.getInstance().getWorkFlowService();
        WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
          workFlowBO.updateActivity(activity,trans);
//        } catch (ServiceFactoryException sfe) {
//            Controller.logException(sfe);
//        } catch (RemoteException re) {
//            Controller.logException(re);
//            throw new DAOException(re.getMessage());
//        }
    }   
    
    /**
     * Saves the Activity to the database. The Activity was previously assigned
     * via the constructor. Throws a DatabaseUpdateException if the update
     * fails.
     * 
     * @throws DAOException
     *             Database update failure.
     */
    protected void addToDatabase() throws DAOException {
        try {
//          WorkFlowService workFlowBO = ServiceFactory.getInstance().getWorkFlowService();
            WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
            workFlowBO.createActivity(activity);
//        } catch (ServiceFactoryException sfe) {
//            Controller.logException(sfe);
       } catch (RemoteException e) {
            Controller.logException(e);
            throw new DAOException(e.getMessage(),e);
        }
    }


	protected void redoActivity(WorkFlowRequest rqst) throws DAOException {
        //  Abandoned current task.
        activity.setStatusCd(Activity.ABANDONED);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        activity.setEndDt(now);
        activity.setCurrent("N");
        Controller.logger.debug(": ActivityController.redoActivity(): process_id=" + 
    			activity.getProcessId() + ", activity_template_id=" + 
    			activity.getActivityTemplateId() + ", loop_cnt=" + activity.getLoopCnt());
        saveToDatabase();
        
//        String roleDiscrim = extractRoleDiscrim(rqst);

        // Mantis 3001 - set user to default user defined in facility inventory roles
        /**
        WriteWorkFlowService wfBO = null;
		try {
			ProcessFlow pf = activity.getContainer();
			wfBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
//            wfBO = ServiceFactory.getInstance().getWriteWorkFlowService();
			wfBO.autoAssignTask(activity.getProcessActivity().getFpId(), 
					activity.getRoleCd(), activity.getProcessActivity(), pf.getUserId(),roleDiscrim);
            Controller.logger.debug(": ActivityController.redoActivity(): set activity user id to " + activity.getUserId());
//		} catch (ServiceFactoryException e) {
//            Controller.logger.error("Canot create instance of WorkflowService", e);
//			e.printStackTrace();
		} catch (RemoteException e) {
            Controller.logger.error("Error executing retrieveUserIdsOfFacility", e);
			logException(e);
			e.printStackTrace();
		} catch (Exception e) {
            Controller.logger.error("WHOA! Unexpected error", e);
			logException(e);
		}
         * 
         */

        // Add a new task for loop back.
        activity.setStatusCd(Activity.NOT_DONE);
        activity.setDueDt(null);
        activity.setJeopardyDt(null);
        activity.setStartDt(null);
        activity.setReadyDt(null);
        activity.setEndDt(null);
        activity.setCurrent("Y");
        activity.setLoopCnt(activity.getLoopCnt() + 1);
        
        addToDatabase();
    }

    /**
     * Returns "true" if all of the Activities connected to the input to this
     * Activity have finished. "Finished" is defined as the input Activity is
     * Completed or Skipped. If there are no inputs to this Activity, then by
     * definition all inputs to this Activity are finished. If any input
     * Activity is in a state other than "Completed" or "Skipped", then "false"
     * is returned.
     * 
     * @return boolean "True" if all input Activities to this Activity are
     *         finished.
     */
    protected boolean inputsAreFinished() {
        boolean finished = true;

        // Find the containing ProcessFlow for this Activity. Ask it to give
        // us the list of all input Activities to this Activity.
        ProcessFlow container = activity.getContainer();
        Activity[] inputs = container.getInputsTo(activity);

        // If we have no input Activities, then they are all "finished".
        if ((inputs != null) && (inputs.length > 0)) {
            // Iterate over the list of input Activities and check their
            // state.
            // If any one of them is not finished, then stop the search and
            // return "false".
            for (Activity aa : inputs) {

                if (!aa.isFinished()) {
                    finished = false;
                    break;
                }
            }
        }

        return finished;
    }

    /**
     * Changes the state of this Activity to "Ready". Verifies that the Activity
     * is in the proper state, "Not Done" before making the state change. If the
     * Activity is changed to "Ready", the "Ready date" for this Activity is
     * also set to "right now". Note that the Activity is not persisted to the
     * database. Any output messages generated by this method are added to
     * "resp". A message will be added to "resp" only in the event of a failure.
     * 
     * @param resp
     *            WorkFlowResponse for output messages.
     * @param startDt
     * 
     * @return boolean "True" if the Activity was changed to the Ready state.
     */
    protected boolean readyActivity(WorkFlowResponse resp, Timestamp startDt) {
        // Check our inputs. If they are not all finished, then we can't
        // "ready" this Activity.

        if (!inputsAreFinished()) {
            return false;
        }

        // Get the current Activity status. If the status is anything other
        // than "Not Done", then we don't want to touch this Activity.

        String currentStatus = activity.getStatusCd();

        // I added skipped for JXOR trans so that if any activity skiped the act
        // other act still can ready the act.  The PBR workflow changes
        if (!currentStatus.equals(Activity.NOT_DONE)
                && !currentStatus.equals(Activity.IN_PROCESS)
                && !currentStatus.equals(Activity.SKIPPED)) {
            String errMsg = "Activity state = [" + currentStatus + "] is "
                    + "incorrect for Ready.";

            Controller.logger.error(errMsg);
            resp.addFailure(errMsg);
            return false;
        }

        //
        // Everything is okay. If this step is already assigned, set the status
        // to IN_PROGRESS else Set the status to PENDING.
        //
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        if (startDt != null && startDt.getTime() != 0)
            now = startDt;

        long curMillis = now.getTime();
        
        activity.setReadyDt(now);
        activity.setStartDt(now);
        if (activity.getUserId() != null) {
            activity.setStatusCd(Activity.IN_PROCESS);
            long expDurMs = activity.getExpectedDuration();
            long jeoDurMs = activity.getJeopardyDuration();

            Timestamp dueTs = new Timestamp(curMillis + expDurMs);
            Timestamp jeoTs = new Timestamp(curMillis + jeoDurMs);

            activity.setDueDt(dueTs);
            activity.setJeopardyDt(jeoTs);
        } else {
            activity.setStatusCd(Activity.PENDING);
            Controller.logInfoMessage("Activity state changed to "
                    + Activity.PENDING);
        }

        return true;
    }

    /**
     * Changes the Activity status to "Completed" and sets the end date for the
     * Activity to "now". In this case, input status is not verified by this
     * method. Also, the Activity is not persisted to the database.
     */
    protected void completeActivity() {
        activity.setStatusCd(Activity.COMPLETED);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        activity.setEndDt(now);
        Controller.logInfoMessage("Activity state changed to "
                + Activity.COMPLETED);
    }

    /**
     * Changes the Activity status to "Skipped" and sets the end date for the
     * Activity to "now". In this case, input status is not verified by this
     * method. Also, the Activity is not persisted to the database.
     */
    protected void skipActivity() {
        activity.setStatusCd(Activity.SKIPPED);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        activity.setEndDt(now);
        Controller.logInfoMessage("Activity state changed to "
                + Activity.SKIPPED);
    }

    /**
     * Changes the Activity status to "Cancelled" and sets the end date for the
     * Activity to "now". In this case, input status is not verified by this
     * method. Also, the Activity is not persisted to the database.
     */
    protected void cancelActivity() {
        activity.setStatusCd(Activity.CANCELLED);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        activity.setEndDt(now);
        Controller.logInfoMessage("Activity state changed to "
                + Activity.CANCELLED);
    }

    /**
     * Changes the Activity status to one of the "Blocked" states, depending on
     * whether or automatic retry is enabled. In this case, input status is not
     * verified by this method. Also, the Activity is not persisted to the
     * database.
     */
    protected void blockActivity() {
        // Start by setting a default condition of "Blocked" and "Manual".

        String statusCode = Activity.BLOCKED;
        String performerCode = Activity.MANUAL_PERFORMER;

        // See if this Activity has any more retry attempts available. If
        // it does, then change the state to "Waiting for Retry" by an
        // "Automatic" Performer, i.e., the Workflow engine.

        Integer retryCnt = activity.getNumberOfRetries();

        if ((retryCnt != null) && (retryCnt.intValue() > 0)) {
            statusCode = Activity.WAIT_FOR_RETRY;
            performerCode = Activity.AUTOMATIC_PERFORMER;
        }

        // Set the Activity status.

        activity.setStatusCd(statusCode);
        activity.setPerformerTypeCd(performerCode);

        Controller.logInfoMessage("Activity state changed to " + statusCode);
    }

    /**
     * Adds any workflow data associated with the workflow processto the
     * WorkFlowResponse object. This will return the information to the user via
     * the servlet framework.
     * 
     * @param wfr
     *            The response object that will receive the workflow data.
     * 
     * @throws java.lang.Exception
     */
    protected void addWorkflowData(WorkFlowResponse wfr) {
        // Ask the containing ProcessFlow for any workflow data fields it
        // might have. If it has any, then add them to the workflow response.

        ProcessFlow pf = activity.getContainer();
        // String processCd = pf.getProcessCd() ;

        HashMap<String, String> dfs = pf.getDataAsHashMap();

        Iterator<String> kiter = dfs.keySet().iterator();

        while (kiter.hasNext()) {
            String dataName = kiter.next();
            String dataValue = dfs.get(kiter);
            wfr.addDataSpec(dataName, STRING_CUSTOM_DETAIL_ID, dataValue);
        }
    }

    /**
     * Retrieves service details from the operational tables and appends them to
     * "resp". Throws an Exception if there is a problem with the retrieval.
     * 
     * @param wfr
     *            WorkFlowReponse where service details will be added.
     * @param serviceId
     *            The service Id whose details are being requested.
     * 
     * @throws Exception
     *             Database access error.
     */
    protected void addServiceDetails(WorkFlowResponse wfr, Integer serviceId)
            throws Exception {
        /**
         * OrderServiceDetail[] sds = WorkFlowBO.retrieveOrderServiceDetails
         * (serviceId, orderiI) ;
         * 
         * if ((sds == null) || (sds.length == 0)) { return ; }
         * 
         * int sdCnt = sds.length ; OrderServiceDetail sd ; int i ; // For (each
         * service detail that was returned) get the label, the // type and the
         * current value. Add these to the WorkFlowResponse.
         * 
         * for (i = 0 ; i < sdCnt ; i++) { sd = sds[i] ; // String dataName =
         * sd.getServiceDetailDefLbl() ; // String dataType =
         * sd.getDataTypeDsc() ; // String dataValue = sd.getDetailVal() ;
         * 
         * String dataName = sd.getServiceDetailDefLbl() ; Integer
         * customDetailTypeId = sd.getCustomDetailTypeId() ; String dataValue =
         * sd.getDetailVal() ;
         * 
         * wfr.addDataSpec (dataName, customDetailTypeId, dataValue) ; }
         */
    }

    /**
     * Retrieves service details from the ordering tables and appends them to
     * "resp". Throws an Exception if there is a problem with the retrieval.
     * 
     * @param wfr
     *            WorkFlowReponse where service details will be added.
     * @param serviceId
     *            The service Id whose details are being requested.
     * @param proposalId
     *            The Id of the Proposal we are processing.
     * 
     * @throws Exception
     *             Database access error.
     */
    protected void addOrderServiceDetails(WorkFlowResponse wfr,
            Integer serviceId, Integer proposalId) throws Exception {
        /***********************************************************************
         * OrderServiceDetail[] osds = null ; osds =
         * WorkFlowBO.retrieveOrderServiceDetails (serviceId, proposalId) ; //
         * If there are no service details, then we are done.
         * 
         * if ((osds == null) || (osds.length == 0)) { return ; }
         * 
         * int sdCnt = osds.length ; OrderServiceDetail osd ;
         * 
         * int i ; // For (each service detail that was returned) get the label,
         * the // type and the current value. Add these to the WorkFlowResponse.
         * 
         * for (i = 0 ; i < sdCnt ; i++) { osd = osds[i] ;
         * 
         * String dataName = osd.getServiceDetailDefLbl() ; Integer
         * customDetailTypeId = osd.getCustomDetailTypeId() ; String dataValue =
         * osd.getDetailVal() ;
         * 
         * wfr.addDataSpec (dataName, customDetailTypeId, dataValue) ; }
         */
    }

    /**
     * Saves service detail data to the database, incorporating any changes made
     * by the user. This method either updates operational tables or ordering
     * system tables, based on the type of workflow process being executed.
     * 
     * @throws us.oh.state.epa.stars2.framework.exception.ApplicationException
     *             Database update error.
     */
    protected void saveServiceData() throws DAOException {
        // If this is a "provisioning" process, then retrieve the service
        // data from the operational tables. If this is a "sales flow"
        // process, then retrieve the service data from the ordering tables.
    	
        HashMap<String, String> dataMap = getDataMap();

        ProcessFlow pf = activity.getContainer();
        String processCd = pf.getProcessCd();
        
        logger.debug(" Trying to save service data with process code: " + processCd);

        try {
            // if (processCd.compareToIgnoreCase("prov") == 0) {
            if (processCd.equalsIgnoreCase(WorkflowProcessDef.PERMITTING)) {
                saveServiceDetails(dataMap, pf.getProcessId());
            } else if (processCd
                    .equalsIgnoreCase(WorkflowProcessDef.EMISSION_REPORTING)) {
                saveServiceDetails(dataMap, pf.getProcessId());
            } else if (processCd.equalsIgnoreCase(WorkflowProcessDef.OTHER)) {
                saveServiceDetails(dataMap, pf.getProcessId());
            } else if (processCd.compareToIgnoreCase("sfa") == 0) {
                saveOrderServiceDetails(dataMap, pf.getServiceId(), pf
                        .getExternalId());
            } else {
                saveServiceDetails(dataMap, pf.getProcessId());
            }

            pf.updateDataField(dataMap);
        } catch (DAOException de) {
            Controller.logException(de);
            throw de;
        }
    }
    
    protected void saveServiceData(Transaction trans) throws DAOException {
        // If this is a "provisioning" process, then retrieve the service
        // data from the operational tables. If this is a "sales flow"
        // process, then retrieve the service data from the ordering tables.
    	
        HashMap<String, String> dataMap = getDataMap();

        ProcessFlow pf = activity.getContainer();
        String processCd = pf.getProcessCd();
        
        logger.debug(" Trying to save service data with process code: " + processCd);

        try {
            // if (processCd.compareToIgnoreCase("prov") == 0) {
            if (processCd.equalsIgnoreCase(WorkflowProcessDef.PERMITTING)) {
                saveServiceDetails(dataMap, pf.getProcessId());
            } else if (processCd
                    .equalsIgnoreCase(WorkflowProcessDef.EMISSION_REPORTING)) {
                saveServiceDetails(dataMap, pf.getProcessId());
            } else if (processCd.equalsIgnoreCase(WorkflowProcessDef.OTHER)) {
                saveServiceDetails(dataMap, pf.getProcessId());
            } else if (processCd.compareToIgnoreCase("sfa") == 0) {
                saveOrderServiceDetails(dataMap, pf.getServiceId(), pf
                        .getExternalId());
            } else {
                saveServiceDetails(dataMap, pf.getProcessId(), trans);
            }

            pf.updateDataField(dataMap);
        } catch (DAOException de) {
            Controller.logException(de);
            throw de;
        }
    }    

    /**
     * Saves service details to the operational tables. Throws an Exception if
     * there is a problem with the retrieval.
     * 
     * @param dataMap
     *            HashMap containing service detail name-value pairs.
     * @param serviceId
     *            The service Id whose details are being requested.
     * 
     * @throws Exception
     *             Database access error.
     */
    private void saveServiceDetails(HashMap<String, String> dataMap, Integer processId)
            throws DAOException {
        try {
//            WriteWorkFlowService wfBO = ServiceFactory.getInstance().getWriteWorkFlowService();
            WriteWorkFlowService wfBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
            wfBO.modifyProcessData(dataMap, processId);
//        } catch (ServiceFactoryException sfe) {
//            Controller.logException(sfe);
        } catch (RemoteException re) {
            Controller.logException(re);
            throw new DAOException(re.getMessage());
        }

        /***********************************************************************
         * OrderServiceDetail[] sds = null;
         * WorkFlowBO.retrieveOrderServiceDetails (serviceId, orderiI) ;
         * 
         * if ((sds == null) || (sds.length == 0)) { return ; }
         * 
         * int sdCnt = sds.length ; OrderServiceDetail sd ; int i ; boolean
         * updated = false ;
         * 
         * For (each service detail that was returned) get the label, the type
         * and the current value. Add these to the WorkFlowResponse.
         * 
         * for (i = 0 ; i < sdCnt ; i++) { sd = sds[i] ;
         * 
         * String dataName = sd.getServiceDetailDefLbl() ;
         * 
         * String dataName = null; //sd.getServiceDetailDefLbl() ; String
         * dataValue = (String)(dataMap.get(dataName)) ;
         * 
         * if ((dataValue != null) && (dataValue.length() > 0)) {
         * //sd.setDetailVal (dataValue) ; updated = true ; } }
         * 
         * If we changed something, save it back to the database.
         * 
         * if (updated) { //WorkFlowBO.modifyOrderServiceDetails (sds) ; }
         */
    }
    
    private void saveServiceDetails(HashMap<String, String> dataMap, Integer processId, Transaction trans)
            throws DAOException {
        try {
//            WriteWorkFlowService wfBO = ServiceFactory.getInstance().getWriteWorkFlowService();
            WriteWorkFlowService wfBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
            if(trans != null){
            	wfBO.modifyProcessData(dataMap, processId, trans);
            } else {
            	wfBO.modifyProcessData(dataMap, processId);
            }
//        } catch (ServiceFactoryException sfe) {
//            Controller.logException(sfe);
        } catch (RemoteException re) {
            Controller.logException(re);
            throw new DAOException(re.getMessage());
        }
    }   

    /**
     * Saves service details to the ordering tables. Throws an Exception if
     * there is a problem with the update.
     * 
     * @param dataMap
     *            HashMap containing service detail name-value pairs.
     * @param serviceId
     *            The service Id whose details are being requested.
     * @param proposalId
     *            The Id of the Proposal we are processing.
     * 
     * @throws Exception
     *             Database access error.
     */
    private void saveOrderServiceDetails(HashMap<String, String> dataMap, Integer serviceId,
            Integer proposalId) {
        /**
         * OrderServiceDetail[] osds = null ; osds =
         * WorkFlowBO.retrieveOrderServiceDetails (serviceId, proposalId) ; //
         * If there are no service details, then we are done.
         * 
         * if ((osds == null) || (osds.length == 0)) { return ; }
         * 
         * int sdCnt = osds.length ; OrderServiceDetail osd ; boolean updated =
         * false ; int i ; // For (each service detail that was returned) get
         * the label, the // type and the current value. Add these to the
         * WorkFlowResponse.
         * 
         * for (i = 0 ; i < sdCnt ; i++) { osd = osds[i] ;
         * 
         * String dataName = osd.getServiceDetailDefLbl() ; String dataValue =
         * (String)(dataMap.get(dataName)) ;
         * 
         * if ((dataValue != null) && (dataValue.length() > 0)) {
         * osd.setDetailVal (dataValue) ; updated = true ; } } // If we changed
         * something, save it back to the database.
         * 
         * if (updated) { WorkFlowBO.modifyOrderServiceDetails (osds) ; }
         */
    }

    /**
     * Instantiates the automatic class handler for this Activity and executes
     * it. The automatic class name is expected to be derived from the
     * AbstractWorkFlowCommand. If there are any DataFields associated with the
     * ProcessFlow containing this Activity, their names and values are
     * collected in a HashMap and passed into the automatic class. Any error or
     * failure messages are appended to "resp".
     * 
     * @param resp
     *            WorkFlowResponse for error or failure messages.
     * 
     * @return "true" if the automatic command successfully executed. Otherwise,
     *         returns "false".
     */
    protected boolean executeAutomaticCommand(WorkFlowResponse resp) throws DAOException {
        // Get any DataFields associated with the containing ProcessFlow as
        // a HashMap. Then, append the "External Id" (order number or
        // proposal number) to the HashMap.

        ProcessFlow container = activity.getContainer();
        // Integer processTemplateId = container.getProcessTemplateId();
        ActivityTemplateDef atd = activity.getActivityTemplateDef();
        Integer activityTemplateDefId = atd.getActivityTemplateDefId();

        // ProcessTemplate pt = null;
        ActivityTemplateDef dbAtd = null;

        // Get the ProcessTemplate and ActivityTemplateDef from the database
        // so we are guaranteed to have the latest version of each for this
        // attempt.

        try {
//            ReadWorkFlowService wfBO = ServiceFactory.getInstance().getReadWorkFlowService();
        	ReadWorkFlowService wfBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
            // pt = wfDao.retrieveProcessTemplate(processTemplateId);
            dbAtd = wfBO.retrieveActivityTemplateDef(activityTemplateDefId);
//        } catch (ServiceFactoryException sfe) {
//            resp.addError(sfe.getMessage());
//            return false;            
        } catch (RemoteException daoe) {
        	logger.error(daoe.getMessage(),daoe);
            resp.addError(daoe.getMessage());
            throw new DAOException(daoe.getMessage(),daoe);
        }

        // Get the name of the automatic class name. If it is null or
        // empty, we can't instantiate it. Add an error message and bail
        // out.

        String autoClassName = dbAtd.getAutomaticClassNm();

        if ((autoClassName == null) || (autoClassName.length() == 0)) {
            String errMsg = "Auto Activity name = ["
                    + activity.getActivityName() + "] has NULL or "
                    + "empty String for automatic class name.";

            resp.addError(errMsg);
            Controller.logger.error(errMsg);

            return false;
        }

        boolean success = true; // Be optimistic...

        try {
            // Instantiate the automatic class. Run it through the standard
            // "Command" framework.

            Class<? extends Object> commandClass = Class.forName(autoClassName);
            AbstractWorkFlowCommand command = (AbstractWorkFlowCommand) commandClass
                    .newInstance();

            WorkFlowCmdData wfData = new WorkFlowCmdData(container
                    .getExternalId(), container.getServiceId(), container
                    .getAccountId(), container.getProcessId(), activity
                    .getActivityTemplateId(), container.getProcessCd(),
                    WorkFlowEngine.getInstance());

            if (command.validate(wfData)) // Let the command validate the
                                            // inputs
            {
                command.execute(wfData); // Execute the command.
            } else // The command failed to validate
            { // Append failure msgs to "resp"
                success = false;
                String[] msgs = command.getErrorMessages();

                for (int i = 0; i < msgs.length; i++) {
                    resp.addFailure(msgs[i]);
                }
            }
        } catch (ClassNotFoundException cnfe) // The error message for this
        { // error is cryptic, let's beef
            success = false; // it up a bit.
            Controller.logException(cnfe);

            String errMsg = "Class not found for automatic Activity: "
                    + activity.getActivityName() + ", class = "
                    + cnfe.getMessage();

            resp.addError(errMsg);
            throw new DAOException(cnfe.getMessage(),cnfe);
        } catch (Exception e) // Something is seriously wrong.
        { // Save the error message
            success = false;

            String errMsg = "Exception: " + e.getClass().getName()
                    + ", Activity: " + activity.getActivityName()
                    + ", Automatic class: " + autoClassName + ".";

            Controller.logException(e);
            resp.addError(errMsg);
            throw new DAOException(e.getMessage(),e);
        }

        return success; // Tell caller what happened
    }

    // decouple workflow
    // - event log no longer created from the wf side
//    protected void makeEventLog() throws DAOException {
//    	throw new RuntimeException("decouple workflow; not implemented yet");
//        try {
//            EventLog el = new EventLog();
//            StringBuffer note = new StringBuffer();
//            note.append("Task ");
//            note.append(activity.getActivityTemplate().getActivityTemplateNm());
//            note.append(" in ");
//            note.append(activity.getActivityTemplate().getProcessTemplateNm());
//            note.append(activity.getProcessActivity().getProcessId());
//            note.append(" complete.");
//
//            FacilityService facilityBO = ServiceFactory.getInstance().getFacilityService();
//            el.setFpId(activity.getContainer().getProcess().getFacilityId());
//            el.setUserId(activity.getProcessActivity().getUserId());
//            el.setDate(activity.getEndDt());
//            el.setEventTypeDefCd(activity.getContainer().getProcessCd());
//            el.setExternalId(activity.getContainer().getExternalId());
//            el.setNote(note.toString());
//
//            facilityBO.createEventLog(el);
//        } catch (ServiceFactoryException sfe) {
//            Controller.logException(sfe);
//        } catch (RemoteException re) {
//            Controller.logException(re);
//            throw new DAOException(re.getMessage());
//        }
//    }
}
