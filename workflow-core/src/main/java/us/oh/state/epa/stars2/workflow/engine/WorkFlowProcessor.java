package us.oh.state.epa.stars2.workflow.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.workflow.Activity;
import us.oh.state.epa.stars2.workflow.util.WorkFlowUtils;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

public class WorkFlowProcessor {
    private static Logger logger = Logger.getLogger(WorkFlowProcessor.class);

	private final String TASK_USERID = "Task UserId";

	@Autowired private ReadWorkFlowService readWorkFlowBO;
	
	@Autowired private WriteWorkFlowService writeWorkFlowBO;
    
    public WorkFlowResponse processCmd(WorkFlowRequest command) {
        WorkFlowResponse resp = new WorkFlowResponse();

        String cmdStr = command.toString();
        logger.debug(cmdStr);
        String action = command.getAction();

        try {
            resp = processCmdWithTransaction(command, resp, action);
        } catch (Throwable e) {
            logger.error(command.toString(), e);
            resp.addError(e.getMessage());
            resp.setRollbackNeeded(true);
        }

        return resp;
    }
    
    @Transactional(rollbackFor=Exception.class)
	public WorkFlowResponse processCmdWithTransaction(WorkFlowRequest command,
			WorkFlowResponse resp, String action) throws Exception {
		if (action.equals(WorkFlowEngine.ACTION_PING)) {
		    resp = new WorkFlowResponse();
		} else if (action.equals(WorkFlowEngine.ACTION_COMPOSITE)) {
		    resp = createComposite(command);
		} else if (action.equals(WorkFlowEngine.ACTION_INIT)) {
		    resp = createProcess(command);
		} else if (action.equals(WorkFlowEngine.ACTION_CREATE_TASK_PROCESS_FLOWS)) {
		    resp = createTaskProcessFlows(command);
		} else if (action.equals(WorkFlowEngine.ACTION_REMOVE_PROCESS_FLOWS)){
			Integer processId = command.getProcessId();
			validate(processId, "Process Id");
			ProcessFlow process = findProcessFlow2(processId);
			long ct = Thread.currentThread().getId();
			if (process.isLocked(ct)){
				logger.debug("Process is locked. " + processId);
				resp.addError("Process is being worked by others.");
				throw new Exception("process is locked");
			}
			try{
				resp = removeProcessFlows(command, resp, process);
			} catch (Throwable t) {
				logger.error("error occurred while processing request; rolling back tx");
				resp.setRollbackNeeded(true);
				throw new Exception("Processing failed.  Workflow action aborted, no changes committed.",t);
			} finally {
				process.unLock(ct);
			}
		} else {
		    Integer processId = command.getProcessId();
		    validate(processId, "Process Id");
		    ProcessFlow process = findProcessFlow(processId);
		    long ct = Thread.currentThread().getId();

		    if (process.isLocked(ct)) {
		        logger.debug("Process is locked. " + processId);
		        resp.addError("Process is being worked by others.");
		        throw new Exception("process is locked");
		    }

		    try {
		        if (action.equals(WorkFlowEngine.ACTION_CHECK_OUT)) {
		            resp = checkOut(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_RELEASE)) {
		            resp = release(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_SAVE)) {
		            resp = saveState(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_CHECK_IN)) {
		            resp = checkIn(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_CHECK_IN_TO)) {
		            resp = checkInTo(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_REASSIGN)) {
		            resp = reassign(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_UNDO)) {
		            resp = undo(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_REDO)) {
		            resp = redo(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_REFER)) {
		            resp = refer(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_UNREFER)) {
		            resp = unRefer(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_RETRY)) {
		            resp = retry(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_SPLIT)) {
		            resp = splitProcess(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_ROLLBACK_TERM_SVC)) {
		            resp = rollbackTerminateService(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_SKIP)) {
		            resp = skip(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_CANCEL_PROCESS)) {
		            resp = cancelProcess(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_UPDATE_ACTIVITY_VIEWED_STATE)) {
		            resp = updateActivityViewedState(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_SAVE_NOTE)) {
		            resp = saveNote(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_MODIFY_NOTE)) {
		            resp = modifyNote(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_CHANGE_END_DATE)) {
		            resp = changeEndDate(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_REMOVE_PROCESS_FLOWS)) {
		            resp = removeProcessFlows(command, resp, process);
		        } else if (action.equals(WorkFlowEngine.ACTION_CLONE_PROCESS)) {
		            resp = cloneProcess(command, resp, process);
		        } else {
		            resp = executeCommand(command, resp, process);
		        }

		    } catch (Throwable t) {
		    	logger.error("error occurred while processing request; rolling back tx");
                resp.setRollbackNeeded(true);
		    	throw new Exception("Processing failed.  Workflow action aborted, no changes committed.",t);
		    } finally {
		        process.unLock(ct);
		    }
		}
		if (resp.hasError()) {
			throw new Exception("Processing failed.  Workflow action aborted, no changes committed.");
		}
		return resp;
	}

    
    
    
    
    public void validate(Integer parameter, String name) throws Exception {
        if (parameter == null) {
            String errMsg = "Parameter [" + name + "] is missing from the "
                    + "input request.";

            logger.error(errMsg);
            throw new Exception(errMsg);
        }

        if (parameter.intValue() <= -1 && parameter.intValue() != -100) {
            String errMsg = "Invalid value for input parameter. Name = " + name
                    + ", value = " + parameter.toString() + ".";

            logger.error(errMsg);
            throw new Exception(errMsg);
        }
    }

    
    
    
    public ProcessFlow findProcessFlow(Integer processId) throws Exception {
//      ProcessFlow p = WorkFlowEngine._workFlows.get(processId);
//
//      if (p == null) {
//          ReadWorkFlowService workFlowBO = App.getApplicationContext().getBean(ReadWorkFlowService.class);
          ProcessFlow p = readWorkFlowBO.retrieveActiveProcessFlow(processId);
          if (p != null) {
//              updateProcessMap(WorkFlowEngine._workFlows, p);
          } else {
              String errMsg = "Active ProcessFlow not found for process Id = "
                      + processId.toString() + "--if it exists the end date is null.";

              logger.error(errMsg);
              throw new Exception(errMsg);
          }
//      }
      
      // move the processId to the begin of list
//      processIdList.remove(processId);
//      processIdList.addFirst(processId);

      return p;
  }

    public ProcessFlow findProcessFlow2(Integer processId) throws Exception {
    	ProcessFlow p = readWorkFlowBO.retrieveProcessFlow(processId);
    	if (p != null) {
    		
    	} else {
    		String errMsg = "ProcessFlow not found for process Id = " + processId.toString();
    		logger.error(errMsg);
    		throw new Exception(errMsg);
    	}
    	return p;
    }   
    
    
    public WorkFlowResponse createTaskProcessFlows(WorkFlowRequest command) throws Exception {
		WorkFlowResponse ret = new WorkFlowResponse();
        if (command != null) {
            validate(command.getUserId(), "User Id");
            validate(command.getWorkFlow(), "Workflow");
            
            //String cmd = rqst.getAction();
            //IController c = WorkFlowEngine.createController(cmd, process);
            String roleDiscrim = extractRoleDiscrim(command);
            
            HashMap<String,String> data = new HashMap<String,String>();
            String taskUserId = null;
            
        	for (int i = 0; i < command.getDataCount(); i++) {
        		String name = command.getDataName(i);
        		if (TASK_USERID.equals(name)) {
        			taskUserId = command.getDataValue(i);
        		} else {
        			data.put(name,command.getDataValue(i));
        		}
        	}
        	
//        	WriteWorkFlowService wfBO = 
//        			App.getApplicationContext().getBean(WriteWorkFlowService.class);
        	
            try {
            	// creating task process flows usually expects 0 for workflow id
//            	Integer workflowId = new Integer(0);
//            	if(command.getWorkFlow() != null) {
//            		workflowId = command.getWorkFlow();
//            	}
				writeWorkFlowBO.createTaskProcessFlows(command.getWorkFlow(), command.getAccountId(), 
						command.getOrderId(), command.getExpedite(), command.getStartDt(), 
						command.getDueDt(), command.getUserId(), data, Integer.parseInt(taskUserId), roleDiscrim);
			} catch (Exception e) {
                logger.error(e.getMessage(), e);
                ret.addError(e.getMessage());
                ret.setRollbackNeeded(true);
                throw e;
			}
            
            //c.execute(resp, rqst);
        } else {
            logger.error("Request was null.");
            ret.addError("Request was null.");
        }
        return ret;
	}




    public WorkFlowResponse checkOut(WorkFlowRequest rqst, WorkFlowResponse resp, 
            ProcessFlow process) throws Exception {
        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.

            Activity a = process.getActivity(rqst.getActivityId());

            // See if the Activity is ready for check-out. If somebody else
            // beat us to it, then just return a failure response. If the
            // Activity state is completely wrong for check-out, an Exception
            // will be thrown.
            if (validForCheckOut(a)) {
                // Create a controller object to handle the check-out. If there
                // are "side effects" or down-stream consequences, the
                // Controller
                // will take care of propagating those for us.

                a.setUserId(rqst.getUserId());
                String cmd = rqst.getAction();

                IController c = WorkFlowEngine.createController(cmd, a);

                c.execute(resp, rqst);
            } else {
                logger.error("This Activity has already been checked out.");
                resp.addError("This Activity has already been checked out.");
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }


    
    
    public WorkFlowResponse skip(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.

            Activity a = process.getActivity(rqst.getActivityId());

            // See if the Activity is ready for check-out. If somebody else
            // beat us to it, then just return a failure response. If the
            // Activity state is completely wrong for check-out, an Exception
            // will be thrown.

            if (validForSkipping(a)) {
                // Create a controller object to handle the check-out. If there
                // are "side effects" or down-stream consequences, the
                // Controller
                // will take care of propagating those for us.

                HashMap<String, String> dataValues = extractDataValues(rqst);

                a.setUserId(rqst.getUserId());
                String cmd = "Skip-One";

                IController c = WorkFlowEngine.createController(cmd, a);

                c.execute(resp, rqst, dataValues);
            } else {
                logger.debug("This Activity has already been by-passed.");
                resp.addError("This Activity has already been by-passed.");
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    /**
     * Releases a checked-out Activity. The Activity must currently be checked
     * out to this user in order to be released. Exceptions are thrown for a
     * variety of conditions, such as invalid input parameters, could not find
     * Activity, invalid state for release, etc.
     * 
     * @param rqst
     *            WorkFlowRequest containing check-out parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse The results of the check out request.
     * 
     * @throws java.lang.Exception
     *             Something is seriously wrong.
     */
    public WorkFlowResponse release(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.

            Activity a = process.getActivity(rqst.getActivityId());

            // See if the Activity is checked-out to this user. If not, then
            // the request is illegal so just return an error response.

            if (validForRelease(a, rqst.getUserId())) {
                // Set the Activity state and then create a controller object to
                // handle the database update.
                a.setStatusCd(Activity.PENDING);
                a.setStartDt(null);
                a.setUserId(null);
                a.setDueDt(null);
                a.setJeopardyDt(null);

                String cmd = rqst.getAction();

                IController c = WorkFlowEngine.createController(cmd, a);

                c.execute(resp, rqst);
            } else {
                String errMsg = "Invalid Activity state for release, status = ["
                        + a.getStatusCd()
                        + "], Request User Id = ["
                        + rqst.getUserId().toString() 
                        + "], Assigned User Id = ["
                        + a.getUserId() + "].";

                logger.debug(errMsg);
                resp.addError(errMsg);
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    /**
     * Changes an Activity from "checked out" back to the "ready" state. This is
     * similar to the "release" action, except "undo" is for administrators who
     * have permission to "undo" any check-out by any user.
     * 
     * @param rqst
     *            WorkFlowRequest containing undo parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse containing results of the "undo" request.
     * 
     * @throws java.lang.Exception
     *             State error or database access error.
     */
    public WorkFlowResponse undo(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.
            Activity a = process.getActivity(rqst.getActivityId());

            // All we want to know is whether or not this Activity is checked
            // out. If it is, we can undo it. If not, don't touch it.
            if (isCheckedOut(a)) {
                // Create a controller to handle the database update.
                String cmd = rqst.getAction();
                IController c = WorkFlowEngine.createController(cmd, a);
                c.execute(resp, rqst);
            } else {
                StringBuffer msg = new StringBuffer(300);
                msg.append("This Activity is not valid for undo, ");
                msg.append("status = [");
                msg.append(a.getStatusCd());
                msg.append("].  Only can undo In process status");

                String errMsg = msg.toString();

                logger.debug(errMsg);
                resp.addError(errMsg);
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    
    
    
    public WorkFlowResponse refer(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getProcessId(), "Process Id");
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.

            Activity a = process.getActivity(rqst.getActivityId());

            // See if the Activity is valid for check in. If the
            // Activity state is completely wrong for saving, an Exception
            // will be thrown.

            if (validForCheckIn(a, rqst.getUserId())) {
                HashMap<String, String> dataValues = extractDataValues(rqst);

                // Create a controller object to handle the check in. If there
                // are "side effects" or down-stream consequences (which there
                // will
                // not be for a "check in"), the Controller will take care of
                // propagating those for us.
                String cmd = rqst.getAction();

                IController c = WorkFlowEngine.createController(cmd, a);

                c.execute(resp, rqst, dataValues);
            } else {
                StringBuffer msg = new StringBuffer(500);
                msg.append("This Activity is not valid for referring, status = [");
                msg.append(a.getStatusCd());
                msg.append("], Requesting user Id = [");
                msg.append(rqst.getUserId().toString());
                msg.append("], assigned user Id = [");

                Integer assigned = a.getUserId();

                if (assigned == null) {
                    msg.append(" Not Assigned ");
                } else {
                    msg.append(assigned.toString());
                }
                msg.append("].");

                String errMsg = msg.toString();
                logger.debug(errMsg);
                resp.addError(errMsg);
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    /**
     * Support for a generic operation to terminate an activity that is in the
     * referred state.
     * 
     * @param rqst
     *            WorkFlowRequest containing undo parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse containing results of the "unRefer" request.
     * 
     * @throws java.lang.Exception
     *             State error or database access error.
     */
    public WorkFlowResponse unRefer(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");
            validate(rqst.getLoopCnt(), "Loop Cnt");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.
            Activity a = process.getActivity(rqst.getActivityId());

            HashMap<String, String> dataValues = extractDataValues(rqst);

            // Create a controller object to handle the check in. If there
            // are "side effects" or down-stream consequences (which there will
            // not be for a "check in"), the Controller will take care of
            // propagating those for us.
            String cmd = rqst.getAction();

            IController c = WorkFlowEngine.createController(cmd, a);

            //
            // There is a possibility that we already came out of the referred
            // state manually. If this is the case there is nothing to do.
            //
            if (a.getLoopCnt().equals(rqst.getLoopCnt())) {
                c.execute(resp, rqst, dataValues);
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    public WorkFlowResponse cancelProcess(WorkFlowRequest command, WorkFlowResponse resp, ProcessFlow process)
            throws Exception {
        logger.debug("Cancel Process request received: " + command);

        WorkFlowResponse ret = new WorkFlowResponse();

        // Extract the order Id and user Id and insure we have legitimate
        // values.
        if (command != null) {
            validate(command.getUserId(), "User Id");

            // Find the "top level" ProcessFlow for this order. If we don't find
            // one, something is wrong.

                IController c = WorkFlowEngine.createController(command
                        .getAction(), process);

                c.execute(ret, command);

        } else {
            logger.error("Request was null.");
            ret.addError("Request was null.");
        }

        return ret;
    }

    /**
     * Creates a "composite" in response to submittal of an order or a proposal
     * to the workflow engine. A "composite" consists of a custom
     * ProcessTemplate created for the order/proposal. Each service on the
     * order/proposal becomes an ActivityTemplate in the ProcessTemplate.
     * Finally, ProcessFlow objects are created for the order/proposal, and each
     * service on the order/proposal. All processes are "readied" for
     * provisioning or sales flow processing.
     * 
     * @param command
     *            The command object from the workflow manager.
     * 
     * @return A response to be returned to the workflow manager.
     * 
     * @throws java.lang.Exception
     *             Validation or database access error.
     */
    public WorkFlowResponse createComposite(WorkFlowRequest command)
            throws Exception {
        WorkFlowResponse ret = new WorkFlowResponse();

        // Validate our input parameters before we do anything else.
        if (command != null) {
            validate(command.getOrderId(), "External Id");
            validate(command.getUserId(), "User Id");
            validate(command.getAccountId(), "accountId");
            WorkFlowUtils.validateType(command.getType(), "WorkFlow Type");

            ProcessTemplate pt = null;
            ProcessFlow[] processes = null;
            IController c = null;
            
            String roleDiscrim = extractRoleDiscrim(command);

            // Extract the service mapping from the command object and translate
            // it into something we can hand to the workflow business object.
            ServiceTemplateMap[] svcMap = createServiceMap(command);

            // Wrap the database operations up in a "try/catch" block. If these
            // fail, we are seriously hosed.
            try {
//              WriteWorkFlowService workFlowBO = ServiceFactory.getInstance().getWriteWorkFlowService();
//              WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
                pt = writeWorkFlowBO.createCompositeTemplate(command.getType(),
                        command.getOrderId(), svcMap, false);

                // If we could not create the process template, something went
                // wrong.

                if (pt == null) {
                    throw new Exception("Process Template creation failed; "
                            + "Template type = [" + command.getType() + "], "
                            + "External Id = ["
                            + command.getOrderId().toString() + "].");
                }

                Integer ptid = pt.getProcessTemplateId();
                processes = writeWorkFlowBO.createProcessFlows(ptid, command
                        .getAccountId(), command.getOrderId(), command
                        .getExpedite(), command.getStartDt(), command
                        .getDueDt(), command.getUserId(), roleDiscrim);
            } catch (Exception e) {
                logger.error(this, e);

                String errMsg = e.getMessage();

                if ((errMsg == null) || (errMsg.length() == 0)) {
                    errMsg = e.getClass().getName() + " has been detected.";
                }

                ret.addError(errMsg);
                ret.setRollbackNeeded(true);
                throw e;
            }

            // If we get here, then we successfully created our template and
            // our ProcessFlows. Add the ProcessFlows to the map of active
            // ProcessFlows. Then, create "Ready" controller for the first
            // ProcessFlow and have it start up the process.
            if (!ret.getRollbackNeeded()) {
//                addProcessFlowsToMap(processes);
                c = WorkFlowEngine.createController(
                        WorkFlowEngine.ACTION_READY, processes[0]);
                c.execute(ret, command);
            }
        } else {
            logger.error("Request was null.");
            ret.addError("Request was null.");
        }

        return ret;
    }

    /**
     * Creates a "composite" in response to submittal of an order or a proposal
     * to the workflow engine. A "composite" consists of a custom
     * ProcessTemplate created for the order/proposal. Each service on the
     * order/proposal becomes an ActivityTemplate in the ProcessTemplate.
     * Finally, ProcessFlow objects are created for the order/proposal, and each
     * service on the order/proposal. All processes are "readied" for
     * provisioning or sales flow processing.
     * 
     * @param command
     *            The command object from the workflow manager.
     * 
     * @return A response to be returned to the workflow manager.
     * 
     * @throws java.lang.Exception
     *             Validation or database access error.
     */
    public WorkFlowResponse createProcess(WorkFlowRequest command)
            throws Exception {
        WorkFlowResponse ret = new WorkFlowResponse();

        // Validate our input parameters before we do anything else.
        if (command != null) {
        	logger.debug(" Validate the process");
            validate(command.getOrderId(), "External Id");
            validate(command.getUserId(), "User Id");
//            validate(command.getAccountId(), "accountId"); //TODO disable facility validation
            
            String roleDiscrim = extractRoleDiscrim(command);
            Integer assignedUserId = extractAssignedUser(command);

            ProcessFlow[] processes = null;
            IController c = null;

            try {
//              WorkFlowService workFlowBO = ServiceFactory.getInstance().getWorkFlowService();
//              WriteWorkFlowService workFlowBO = context.getBean(WriteWorkFlowService.class);
                logger.debug(" Creating the process flows");
                //DONE 2475
                // pass in role discrim
                processes = writeWorkFlowBO.createProcessFlows(
                        command.getWorkFlow(), command.getAccountId(), command
                                .getOrderId(), command.getExpedite(), command
                                .getStartDt(), command.getDueDt(), command
                                .getUserId(), roleDiscrim, assignedUserId,
                                command.getTransaction());
            } catch (Exception e) {
                logger.error(this, e);

                String errMsg = e.getMessage();

                if ((errMsg == null) || (errMsg.length() == 0)) {
                    errMsg = e.getClass().getName() + " has been detected.";
                }

                ret.addError(errMsg);
                ret.setRollbackNeeded(true);
                throw e;
            }
            
            logger.debug(" Process flows successfully created");

            // If we get here, then we successfully created our template and
            // our ProcessFlows. Add the ProcessFlows to the map of active
            // ProcessFlows. Then, create "Ready" controller for the first
            // ProcessFlow and have it start up the process.
            if (!ret.getRollbackNeeded()) {
//                addProcessFlowsToMap(processes);
                c = WorkFlowEngine.createController(
                        WorkFlowEngine.ACTION_READY, processes[0]);
                HashMap<String, String> dataValues = extractDataValues(command);
                c.execute(ret, command, dataValues);
                
                if ((dataValues != null) && (dataValues.size() > 0)) {
                    // Create a controller object to handle the save. If there
                    // are "side effects" or down-stream consequences (which
                    // there
                    // should
                    // not be for a "save"), the Controller will take care of
                    // propagating
                    // those for us.
                	logger.debug(" Doing a down-stream controller save");
                    Activity a = processes[0].getFirstActivity();
                    c = WorkFlowEngine.createController(WorkFlowEngine.ACTION_SAVE, a);
                    c.execute(ret, command, dataValues);
                }
            }
        } else {
            logger.error("Request was null.");
            ret.addError("Request was null.");
        }

        return ret;
    }
    public WorkFlowResponse saveState(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.

            Activity a = process.getActivity(rqst.getActivityId());

            // See if the Activity is valid for saving the data. If not, record
            // a tale of sorrow and woe to the response object.
            if (validForCheckIn(a, rqst.getUserId())) {
                // All we want to do is save service detail data. Extract it
                // from
                // the request object. If we don't have any, then we really have
                // no
                // reason to be here.
                HashMap<String, String> dataValues = extractDataValues(rqst);

                if ((dataValues != null) && (dataValues.size() > 0)) {
                    // Create a controller object to handle the save. If there
                    // are "side effects" or down-stream consequences (which
                    // there
                    // should
                    // not be for a "save"), the Controller will take care of
                    // propagating
                    // those for us.
                    String cmd = rqst.getAction();

                    IController c = WorkFlowEngine.createController(cmd, a);

                    c.execute(resp, rqst, dataValues);
                }
            } else {
                StringBuffer msg = new StringBuffer(500);
                msg
                        .append("This Activity is not valid for saving data, status = [");
                msg.append(a.getStatusCd());
                msg.append("], Requesting user Id = [");
                msg.append(rqst.getUserId().toString());
                msg.append("], assigned user Id = [");

                Integer assigned = a.getUserId();

                if (assigned == null) {
                    msg.append("null");
                } else {
                    msg.append(assigned.toString());
                }
                msg.append("].");

                String errMsg = msg.toString();

                logger.error(errMsg);
                resp.addError(errMsg);
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    /**
     * Checks in an Activity current assigned to a user. The Activity must be in
     * the "In Process" state and assigned to the user making the check in
     * request. If successful, the "Activity" will be marked "Completed" in the
     * database and any service data associated with the activity will be saved.
     * Checking in one Activity usually will have the side effect of readying
     * other Activities or completing a ProcessFlow.
     * 
     * @param rqst
     *            Check in request. Includes possible service data.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse The result of the check in request.
     * 
     * @throws java.lang.Exception
     *             Invalid state or database access error.
     */
    public WorkFlowResponse checkIn(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {
        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");
//            logger.error("DWL: CheckIn:  action=" + rqst.getAction() + ", type=" + rqst.getType() +
//                    ",activityId=" + rqst.getActivityId() + ", userId=" + rqst.getUserId(), 
//                    new Exception());  // #3206

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.
            Activity a = process.getActivity(rqst.getActivityId());

            // See if the Activity is valid for check in. If the
            // Activity state is completely wrong for saving, an Exception
            // will be thrown.
            if (validForCheckIn(a, rqst.getUserId())) {
                HashMap<String, String> dataValues = extractDataValues(rqst);

                // Create a controller object to handle the check in. If there
                // are "side effects" or down-stream consequences (which there
                // will
                // not be for a "check in"), the Controller will take care of
                // propagating those for us.
                String cmd = rqst.getAction();

                IController c = WorkFlowEngine.createController(cmd, a);

                c.execute(resp, rqst, dataValues);
            } else {
                StringBuffer msg = new StringBuffer(500);
                msg.append("This Activity [");
                msg.append(a.getActivityTemplateId());
                msg.append("] is not valid for saving data, status = [");
                msg.append(a.getStatusCd());
                msg.append("], Requesting user Id = [");
                msg.append(rqst.getUserId().toString());
                msg.append("], assigned user Id = [");

                Integer assigned = a.getUserId();

                if (assigned == null) {
                    msg.append(" Not Assigned");
                } else {
                    msg.append(assigned.toString());
                }
                msg.append("].");

                String errMsg = msg.toString();
                logger.error(errMsg);
                resp.addError(errMsg);
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }
    
    public WorkFlowResponse checkInTo(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");

            logger.debug("CheckInTo : process id = " + rqst.getProcessId() + " to activity id = " + rqst.getActivityId() + " start.");
            
            Activity a = process.getFirstActivity();
            ArrayList<Activity> as = new ArrayList<Activity>(); 
            LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
            as = findActivityPath(as, a, rqst.getActivityId(), data);

            WorkFlowRequest c = new WorkFlowRequest(WorkFlowEngine.ACTION_CHECK_IN);
            c.setProcessId(rqst.getProcessId());
            if (data != null) {
                for (String dataName : data.keySet()) {
                    String dataValue = data.get(dataName);
                    c.addDataPair(dataName, dataValue);
                }
            }
            
            for (int i = as.size(); i > 1; i--){
                a = as.get(i-1);
                c.setUserId(a.getUserId());
                c.setActivityId(a.getActivityId());
                logger.debug("CheckInTo : check in activity id = " + a.getActivityId());
                resp = checkIn(c, resp, process);
            }

            logger.debug("CheckInTo : process id = " + rqst.getProcessId() + " to activity id = " + rqst.getActivityId() + " finished.");
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    
    
    
    public WorkFlowResponse reassign(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            //validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");
            validate(rqst.getActivityId(), "Activity Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.
            Activity a = process.getActivity(rqst.getActivityId());

            if (isCheckedOut(a) || 
                    a.getStatusCd().equalsIgnoreCase(Activity.NOT_DONE) || 
                    a.getStatusCd().equalsIgnoreCase(Activity.REFERRED)) {
                // Assign the new user Id here. The controller will just save
                // the
                // Activity to the database.
                a.setUserId(rqst.getUserId());
                String cmd = rqst.getAction();
                IController c = WorkFlowEngine.createController(cmd, a);

                c.execute(resp, rqst);
            } else {
                StringBuffer msg = new StringBuffer(300);
                msg.append("This Activity is not valid for re-assignment, ");
                msg.append("status = [");
                msg.append(a.getStatusCd());
                msg.append("].  Only can re-assign In process activity.");

                String errMsg = msg.toString();
                logger.error(errMsg);
                resp.addError(errMsg);
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    /**
     * Attempts to "retry" an automatic Activity after it has been blocked due
     * to a failure. The Activity is expected to be marked as "blocked". If that
     * is true, then the automatic class associated with this Activity will be
     * reattempted. If it succeeds, then standard downstream processing, e.g.,
     * any follow-on Activities are made "ready", will be performed. If the
     * automatic Activity fails, the Activity will remain in the "blocked"
     * state.
     * 
     * @param rqst
     *            Contains retry information such as activity Id and process Id
 * @param process 
 * @param resp .
     * 
     * @return WorkFlowResponse containing the results of the request.
     * 
     * @throws java.lang.Exception
     *             Automatic class exception.
     */
    public WorkFlowResponse retry(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.
            Activity a = process.getActivity(rqst.getActivityId());

            if (validForRetry(a)) {
                // Assign the new user Id here. The controller will retry the
                // Activity. If that works, then downstream Activities will be
                // readied for processing.
                a.setUserId(rqst.getUserId());
                String cmd = rqst.getAction();
                IController c = WorkFlowEngine.createController(cmd, a);

                c.execute(resp, rqst);
            } else {
                StringBuffer msg = new StringBuffer(300);
                msg.append("This Activity is not valid for re-try, ");
                msg.append("status = [");
                msg.append(a.getStatusCd());
                msg.append("].");

                String errMsg = msg.toString();
                logger.error(errMsg);
                resp.addError(errMsg);
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    /**
     * Move a service that is currently on one order to another order. The new
     * order is assumed to have been created and the tables in the ordering
     * subsystem reflect the change. The Activity representing the service is
     * moved from its original ProcessFlow to the ProcessFlow for the new order.
     * 
     * @param rqst
     *            WorkFlowRequest object containing new parameters.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowReponse The results of this request.
     * 
     * @throws java.lang.Exception
     *             Any number of errors.
     */
    public WorkFlowResponse splitProcess(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getProcessId(), "Process Id");
            validate(rqst.getUserId(), "User Id");

            // Get reference to the Process AS IT ORIGINALLY EXISTED in the
            // the process map. We are going to need this in a minute. If we
            // can't find it, then an exception will be thrown.

            // Find the parent ProcessFlow and Activity for "split". Verify that
            // we have the correct number of in-bound and out-bound Transitions.
            // If we don't, it is an error.

            ProcessFlow oldParent = process.getParentProcess();
            Activity parentAct = process.getParentActivity();

            Transition[] inbound = oldParent.getInboundTransitions(parentAct);

            if ((inbound == null) || (inbound.length != 1)) {
                String errMsg = "Incorrect number of inbound Transitions to "
                        + "parent Activity.";

                logger.error(errMsg);
                throw new Exception(errMsg);
            }

            Transition[] outbound = oldParent.getOutboundTransitions(parentAct);

            if ((outbound == null) || (outbound.length != 1)) {
                String errMsg = "Incorrect number of outbound Transitions to "
                        + "parent Activity.";

                logger.error(errMsg);
                throw new Exception(errMsg);
            }

            // Get a handle to the Activity that is downstream from our parent
            // Activity. We need this now because we are going to throw away
            // its inbound Transition and we will need to send it a "Ready"
            // command after we have split the order.

            Integer downstreamId = outbound[0].getToId();
            Activity downstream = oldParent.getActivity(downstreamId);

            // The WorkFlow business object is going to go through conniptions
            // to create a proper ProcessFlow for the order split. In addition,
            // it will also the "old parent" to fix it up in the database to
            // remove the "parentAct" Activity from that process. We delegate
            // this to the business object so that we can do everything that
            // needs to be done in a single transaction (easier to clean up when
            // we fail).

            try {
//                WriteWorkFlowService workFlowBO = App.getApplicationContext().getBean(WriteWorkFlowService.class);
                ProcessFlow newPf = writeWorkFlowBO.createSplitProcessFlow(rqst
                        .getType(), rqst.getOrderId(), parentAct, oldParent,
                        process);

//                addProcessFlowToMap(newPf);

                // Now, send a "Ready" command to the downstream Activity in
                // "oldParent". If all of the other processes on this order are
                // done, this will complete that order. If it does, the
                // Controllers will update the in-memory workflows to remove
                // the process.

                IController c = WorkFlowEngine.createController(
                        WorkFlowEngine.ACTION_READY, downstream);
                c.execute(resp, rqst);

                // Ask the in-memory workflows table for the "oldParent". If
                // that
                // is now gone, then "oldParent" is completed.

//                Object o = WorkFlowEngine._workFlows.get(oldParent
//                        .getProcessId());
//
//                if (o == null) {
//                    String infoMsg = "Order #"
//                            + oldParent.getExternalId().toString()
//                            + " is completed.";
//
//                    logger.debug(infoMsg);
//                    resp.addInfoMsg(infoMsg);
//                }

                // One final thing to do: Send the process Id for the new
                // process
                // back to the WorkFlowManager so it can pass it up the food
                // chain
                // to its caller. We will do this using the "built-in" data
                // interface.

                resp.addDataSpec("processId", WorkFlowEngine.INTEGER_CUSTOM_DETAIL_ID, newPf
                        .getProcessId().toString());
            } catch (Exception e) {
                logger.error("Exception: " + e.getMessage(), e);
                resp.addError(e.getMessage());
                resp.setRollbackNeeded(true);
                throw e;
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

    /**
     * Handles a request to rollback a "Terminate Service Order" to the previous
     * step, i.e., the user does not want to terminate this service order and
     * would like to give it another try. The WorkFlowRequest must contain the
     * process Id and activity Id (activity template Id) of the "Terminate
     * Service Order" request. Also, it should contain the user Id of the user
     * making the request.
     * 
     * @param rqst
     *            WorkFlowRequest containing Activity key values.
     * @param process 
     * @param resp 
     * 
     * @return WorkFlowResponse The result of the request.
     * 
     * @throws java.lang.Exception
     *             Validation or database access error.
     */
    public WorkFlowResponse rollbackTerminateService(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process)
            throws Exception {

        // Extract the input parameters we need for rollback and validate
        // them.
        if (rqst != null) {
            validate(rqst.getUserId(), "User Id");
            validate(rqst.getActivityId(), "Activity Id");

            // Find the "Terminate Service Order" Activity and its parent
            // ProcessFlow. If we can't find either one, then throw an
            // Exception.
            // Make sure the Activity is in a state that valid for what we are
            // about to do to it.
            Activity deadmeat = process.getActivity(rqst.getActivityId());

            if (!validForRollback(deadmeat)) {
                // Create a Controller for the rollback and have it finish up
                // what
                // we need to do.
                IController c = WorkFlowEngine.createController(
                        WorkFlowEngine.ACTION_ROLLBACK_TERM_SVC, deadmeat);
                c.execute(resp, rqst);
            } else {
                resp.addFailure("Invalid state for Activity rollback.");
            }
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }


    public WorkFlowResponse redo(WorkFlowRequest rqst, WorkFlowResponse ret, 
            ProcessFlow process) throws Exception {

        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");

            Integer activityId = rqst.getActivityId();

            Activity a = process.getActivity(activityId);

            if (Activity.IN_PROCESS.equals(a.getStatusCd())) {
                logger.debug("Process is being looped. " + process.getProcessId());
                ret.addError("Process is being looped.");
                return ret;
            }
            IController c = null;

            String cmd = rqst.getAction();
            c = WorkFlowEngine.createController(cmd, a);
            c.execute(ret, rqst);

        } else {
            logger.error("Request was null.");
            ret.addError("Request was null.");
        }

        return ret;
    }

	public WorkFlowResponse updateActivityViewedState(
			WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {
        if (rqst != null) {
            validate(rqst.getProcessId(), "Process Id");
            validate(rqst.getActivityId(), "Activity Id");
            
            Activity a = process.getActivity(rqst.getActivityId());

            a.setUserId(rqst.getUserId());
            String cmd = rqst.getAction();

            IController c = WorkFlowEngine.createController(cmd, a);

            c.execute(resp, rqst);
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
	}

	/**
     * Find the command with controller and execute it.
     * Works for following actions: 
     * WorkFlowEngine.ACTION_CHANGE_DUE_DATE
     * WorkFlowEngine.ACTION_CHANGE_START_DATE
     * 
     * @param rqst
     * @param resp
     * @param process
     * @return
     * @throws Exception
     */
    public WorkFlowResponse executeCommand(WorkFlowRequest rqst, WorkFlowResponse resp, ProcessFlow process) throws Exception {

        if (rqst != null) {
        	logger.debug(" Creating controller for: " + rqst.getAction());
            IController c = WorkFlowEngine.createController(rqst.getAction(),
                    process);

            c.execute(resp, rqst);
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
    }

	public WorkFlowResponse saveNote(WorkFlowRequest rqst,
			WorkFlowResponse resp, ProcessFlow process) throws Exception  {
        if (rqst != null) {
            validate(rqst.getProcessId(), "Process Id");
            validate(rqst.getUserId(), "User Id");

            String cmd = rqst.getAction();
            IController c = WorkFlowEngine.createController(cmd, process);

            c.execute(resp, rqst);
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }
        return resp;
	}




    
    
    
    
	public WorkFlowResponse modifyNote(WorkFlowRequest rqst,
			WorkFlowResponse resp, ProcessFlow process) throws Exception  {
        if (rqst != null) {
            validate(rqst.getProcessId(), "Process Id");
            validate(rqst.getUserId(), "User Id");

            String cmd = rqst.getAction();
            IController c = WorkFlowEngine.createController(cmd, process);

            c.execute(resp, rqst);
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }
        return resp;
	}
	
	

	public WorkFlowResponse removeProcessFlows(WorkFlowRequest rqst,
			WorkFlowResponse resp, ProcessFlow process) throws Exception {
        if (rqst != null) {
            validate(rqst.getProcessId(), "Process Id");
            validate(rqst.getUserId(), "User Id");

            String cmd = rqst.getAction();
            IController c = WorkFlowEngine.createController(cmd, process);

            c.execute(resp, rqst);
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }
        return resp;
	}

	public WorkFlowResponse changeEndDate(WorkFlowRequest rqst,
			WorkFlowResponse resp, ProcessFlow process) throws Exception {
        // Extract the input parameters we need for check out and validate
        // them.
        if (rqst != null) {
            validate(rqst.getActivityId(), "Activity Id");
            validate(rqst.getUserId(), "User Id");

            // See if we can find the Activity in the active workflows. If not,
            // then something is wrong and an Exception is thrown.

            Activity a = process.getActivity(rqst.getActivityId());

            a.setUserId(rqst.getUserId());
            String cmd = rqst.getAction();

            IController c = WorkFlowEngine.createController(cmd, a);

            c.execute(resp, rqst);
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }

        return resp;
	}

	
	
	

    public WorkFlowResponse cloneProcess(WorkFlowRequest rqst,
			WorkFlowResponse resp, ProcessFlow process) throws Exception {
        if (rqst != null) {
            validate(rqst.getProcessId(), "Process Id");
            validate(rqst.getUserId(), "User Id");

            String cmd = rqst.getAction();
            IController c = WorkFlowEngine.createController(cmd, process);

            c.execute(resp, rqst);
        } else {
            logger.error("Request was null.");
            resp.addError("Request was null.");
        }
        return resp;
	}
    
    protected HashMap<String, String> extractDataValues(WorkFlowRequest cmd) {
        HashMap<String, String> dataValues = new HashMap<String, String>();

        for (int i = 0; i < cmd.getDataCount(); i++) {
            dataValues.put(cmd.getDataName(i), cmd.getDataValue(i));
        }

        return dataValues;
    }
    
    
    
    protected boolean validForCheckOut(Activity act) throws Exception {
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        // If the Activity is in the PENDING state, it is ready for check-out.

        if (aStatus.compareTo(Activity.PENDING) == 0) {
            ret = true;
        } else if (aStatus.compareTo(Activity.IN_PROCESS) == 0) {
            // If the Activity is IN_PROCESS, it is already checked out.
            ret = false;
        } else {
            // If we get here, something is really wrong.
            String errMsg = "Invalid Activity status = " + aStatus
                    + " for check out.";
            logger.error(errMsg);
            throw new Exception(errMsg);
        }

        return ret;
    }

    /**
     * Verifies that the Activity <code>a</code> is in a state suitable for
     * by-pass. The Activity is in one of three states: valid for skipping,
     * invalid for skipping, or somebody else has already skipped it. The way
     * this is represented is:
     * 
     * If the Activity is valid for skipping, return "true". If the Activity is
     * already skipped, return "false". If the Activity is in an invalid state
     * for by-pass, e.g., it is already completed, then throw an Exception.
     * 
     * @param act
     *            Activity to be validated.
     * 
     * @return boolean "true" if ready for by-pass, "false" if already skipped.
     * 
     * @throws java.lang.Exception
     *             Invalid Activity state for check-out.
     */
    protected boolean validForSkipping(Activity act) throws Exception {
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        // If the Activity is in the PENDING state, it is ready for by-pass.

        if (aStatus.compareTo(Activity.PENDING) == 0
                || aStatus.equalsIgnoreCase(Activity.IN_PROCESS)) {
            ret = true;
        } else if (aStatus.compareTo(Activity.SKIPPED) == 0) {
            // If the Activity is SKIPPED, it is already by-passed.
            ret = false;
        } else {
            // If we get here, something is really wrong.
            String errMsg = "Invalid Activity status = " + aStatus
                    + " for by-pass.";
            logger.error(errMsg);
            throw new Exception(errMsg);
        }

        return ret;
    }

    /**
     * Verifies that the Activity <code>a</code> is in a state suitable for
     * rollback. This means that this Activity has been designated as a terminal
     * activity and has been reached in the workflow. Now, the user wants to
     * back up a step and see if another branch of the workflow can be taken. If
     * the Activity is either checked-out or ready, then it is in a valid state.
     * Otherwise, throw an Exception.
     * 
     * If the Activity is valid for rollback, return "true". If the Activity is
     * in an invalid state for rollback, e.g., it is already completed, then
     * throw an Exception.
     * 
     * @param act
     *            Activity to be validated.
     * 
     * @return boolean "true" if ready for rollback.
     * 
     * @throws java.lang.Exception
     *             Invalid Activity state for rollback.
     */
    protected boolean validForRollback(Activity act) throws Exception {
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        // If the Activity is in the PENDING or IN_PROCESS state, it is ready
        // for rollback.
        if (aStatus.compareTo(Activity.PENDING) == 0
                || aStatus.compareTo(Activity.IN_PROCESS) == 0) {
            ret = true;
        } else {
            // If we get here, something is really wrong.
            String errMsg = "Invalid Activity status = " + aStatus
                    + " for rollback.";
            logger.error(errMsg);
            throw new Exception(errMsg);
        }

        return ret;
    }

    /**
     * Verifies that the Activity "a" is valid for checking in. To be valid, "a"
     * must be "In Process", i.e., checked out, to user "userId". Returns "true"
     * if the condition is satisfied, otherwise returns "false".
     * 
     * @param act
     *            Activity we are validating.
     * @param userId
     *            The Id of the user making the save request.
     * 
     * @return "true" if "act" is currently checked out to "user Id".
     */
    protected boolean validForCheckIn(Activity act, Integer userId) {
        String aStatus = null;
        Integer actUserId = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
            actUserId = act.getUserId();
        }

        if (aStatus.equals(Activity.IN_PROCESS) && (actUserId.equals(userId))) {
            ret = true;
        }

        return ret;
    }

 
    

	public String extractRoleDiscrim(WorkFlowRequest command) {
		String roleDiscrim = null;
		for (int i = 0; i < command.getDataCount(); i++) {
			if (WorkFlowEngine.ROLE_DISCRIMINATOR.equals(command.getDataName(i))) {
				roleDiscrim = command.getDataValue(i);
				break;
			}
		}
		return roleDiscrim;
	}

	public Integer extractAssignedUser(WorkFlowRequest command) {
		Integer assignedUserId = null;
		for (int i = 0; i < command.getDataCount(); i++) {
			if (WorkFlowEngine.ASSIGNED_USER.equals(command.getDataName(i))) {
				assignedUserId = Integer.valueOf(command.getDataValue(i));
				break;
			}
		}
		return assignedUserId;
	}
	
	
	
	
	
    protected boolean validForRelease(Activity act, Integer userId) {
        String aStatus = null;
        Integer aUser = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
            aUser = act.getUserId();
        }

        if (aStatus.equals(Activity.IN_PROCESS) && aUser.equals(userId)) {
            ret = true;
        }

        return ret;
    }
    
    
    
    
    
    protected boolean isCheckedOut(Activity a) {
        boolean ret = false;
        String aStatus = a.getStatusCd();

        if (aStatus.equals(Activity.IN_PROCESS)) {
            ret = true;
        }

        return ret;
    }
    
    
    
    
    protected boolean validForRetry(Activity act) {
        String aStatus = null;
        boolean ret = false;

        synchronized (act) {
            aStatus = act.getStatusCd();
        }

        if (aStatus.equals(Activity.BLOCKED)
                || aStatus.equals(Activity.WAIT_FOR_RETRY)) {
            ret = true;
        }

        return ret;
    }
    
    
    
    
    protected ServiceTemplateMap[] createServiceMap(WorkFlowRequest cmd) {
        ArrayList<ServiceTemplateMap> temp = new ArrayList<ServiceTemplateMap>();

        for (int i = 0; i < cmd.getServiceCount(); i++) {
            ServiceTemplateMap stm = new ServiceTemplateMap(
                    cmd.getServiceId(i), cmd.getActivityTemplateDefId(i));
            temp.add(stm);
        }

        return temp.toArray(new ServiceTemplateMap[0]);
    }
    
    
    
    public ArrayList<Activity> findActivityPath(ArrayList<Activity> as, 
            Activity a, Integer activityId, LinkedHashMap<String, String> data) {
        
        if (!a.getActivityId().equals(activityId)){
            if (!a.getOutboundTransitionType().equals(Activity.LOOP)){
                ProcessFlow pf = a.getContainer();
                Transition[] outbounds = pf.getOutboundTransitions(a);
                for (Transition t : outbounds){
                    Activity nextAct = pf.getActivity(t.getToId());
                    findActivityPath(as, nextAct, activityId, data);

                    if (as.size() != 0){ 
                        if(!ActivityStatusDef.COMPLETED.equalsIgnoreCase(a.getStatusCd())){
                            as.add(a);
                            data.put(t.getDataName(), t.getDataValue());
                        }
                        break;
                    } 
                }
            }
        }else{
            as.add(a);
        }
            
        return as;
    }


    protected Activity findActivity(Integer processId, Integer activityId)
            throws Exception {

    	Activity a = null;

        ProcessFlow p = findProcessFlow(processId);

        if (p != null) {
            a = p.getActivity(activityId);
            // a.setCurrent("Y");
        }

        if (a == null) {
            String errMsg = "Activity not found for process Id = "
                    + processId.toString() + ", Activity Id = "
                    + activityId.toString() + ".";

            logger.error(errMsg);
            throw new Exception(errMsg);
        }

        return a;
    }

    
}
