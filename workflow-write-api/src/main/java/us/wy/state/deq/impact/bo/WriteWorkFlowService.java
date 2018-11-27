package us.wy.state.deq.impact.bo;

import java.sql.Timestamp;
import java.util.HashMap;

import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;

public interface WriteWorkFlowService {
	/**
	 * Creates a new ServiceDetailDef object in the database.
	 * 
	 * @param sdd
	 *            ServiceDetailDef The object to be created.
	 * @throws DAOException
	 *             Database access error.
	 */
	public void createServiceDetailDef(
			us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail cdtd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Updates an existing ServiceDetailDef object in the database.
	 * 
	 * @param sdd
	 *            ServiceDetailDef The object to be updated.
	 * @throws DAOException
	 *             Database access error.
	 */
	public void updateServiceDetailDef(
			us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail cdtd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Creates a brand new workflow in the database. In this case, no previous
	 * version of the workflow is assumed to exist. If that is possible, then
	 * call "updateProcessTemplate()" instead. Returns "true" if the creation
	 * was successful. Otherwise, returns "false".
	 * 
	 * @param pt
	 *            ProcessTemplate for the workflow.
	 * @param activities
	 *            The Activities in the workflow.
	 * @param transitions
	 *            The Transitions between Activities.
	 * @param detailData
	 *            The service detail data for the workflow.
	 * @return boolean="True"
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public boolean createProcessTemplate(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate pt,
			us.oh.state.epa.stars2.workflow.Activity[] activities,
			us.oh.state.epa.stars2.database.dbObjects.workflow.Transition[] transitions,
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessDataTemplate[] detailData)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Updates a workflow. Basically, any pre-existing version of the work is
	 * deprecated and a new workflow is created in its place. Returns "true" if
	 * the update was successful. Otherwise, returns "false". This method is
	 * safe to call even if there is no previous version of the workflow.
	 * 
	 * @param pt
	 *            ProcessTemplate for the workflow.
	 * @param activities
	 *            The Activities in the workflow.
	 * @param transitions
	 *            The Transitions between Activities.
	 * @param detailData
	 *            The service detail data for the workflow.
	 * @return boolean="True"
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public boolean updateProcessTemplate(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate pt,
			us.oh.state.epa.stars2.workflow.Activity[] activities,
			us.oh.state.epa.stars2.database.dbObjects.workflow.Transition[] transitions,
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessDataTemplate[] detailData)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Updates a workflow that has minimal changes in place. The changes are
	 * limited to the position activities that are displayed in the workflow
	 * Java 2D image.
	 * 
	 * @param pt
	 *            ProcessTemplate for the workflow.
	 * @param activities
	 *            The Activities in the workflow.
	 * @return boolean="True"
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public boolean updateInPlaceActivityTemplate(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate pt,
			us.oh.state.epa.stars2.workflow.Activity[] activities)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Creates an entire array of <tt>Transition</tt> objects in the underlying
	 * database. See "createTransition()" for specific details.
	 * 
	 * @param transArray
	 *            Array of Transition objects.
	 * @throws DAOException
	 *             Database access error.
	 */
	public void createTransitions(
			us.oh.state.epa.stars2.database.dbObjects.workflow.Transition[] transArray)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Create a ProcessFlow for an order split. This includes constructing a
	 * ProcessTemplate and a Process, and then moving the "subflow" from its
	 * original ProcessFlow to the new one. The "oldParentProc" is modified to
	 * reflect the removal of the subflow (and its immediate parent Activity)
	 * and its 'geometry' is recomputed, i.e., the location of its boxes in the
	 * graphical representation.
	 * 
	 * @param type
	 *            Process type, either "prov" or "sfa".
	 * @param newOrderId
	 *            The new order Id to use for the process.
	 * @param oldParentAct
	 *            The original parent Activity of the subflow.
	 * @param oldParentProc
	 *            The original parent process of the subflow.
	 * @param subflow
	 *            The subflow being moved to a new process.
	 * @return ProcessFlow A new ProcessFlow containing a full template and
	 *         "subflow".
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow createSplitProcessFlow(
			java.lang.String type, java.lang.Integer newOrderId,
			us.oh.state.epa.stars2.workflow.Activity oldParentAct,
			us.oh.state.epa.stars2.workflow.engine.ProcessFlow oldParentProc,
			us.oh.state.epa.stars2.workflow.engine.ProcessFlow subflow)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate createProcessTemplate(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate pt)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public boolean modifyProcessDataTemplate(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessDataTemplate ptd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef createActivityTemplateDef(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef atd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public boolean modifyActivityTemplateDef(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef atd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * SPECIALTY METHOD. Completely removes an Activity from a process in the
	 * database. The Activity being removed is assumed to be the parent of a
	 * subflow process. This method is done when a service is being terminated
	 * or deleted from an order that is in provisioning. Only workflow-related
	 * tables are updated by this method, i.e., the order in the ordering
	 * subsystem is not affected. The parent ProcessFlow refers to the parent
	 * process that contains the Activity being removed. Basically, this is the
	 * composite ProcessFlow for the order. The subflow ProcessFlow refers to
	 * the subflow of the Activity being deleted. Basically, this is the
	 * workflow that provisions this service on this order. The subflow process
	 * Id should be "null" if Activity has no subflow.
	 * 
	 * @param actTemplId
	 *            activity template Id of the Activity being deleted.
	 * @param actProcessId
	 *            process Id of the Activity being deleted.
	 * @param parentPtId
	 *            process template Id of the parent ProcessFlow.
	 * @param subflowProcessId
	 *            subflow process Id.
	 * @throws DAOException
	 *             Database access error.
	 */
	public void removeActivityFromProcess(java.lang.Integer actTemplId,
			java.lang.Integer actProcessId, java.lang.Integer actLoopCnt,
			java.lang.Integer parentPtId, java.lang.Integer subflowProcessId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Updates the ProcessFlow data in the database with the contents of "pf".
	 * 
	 * @param pf
	 *            ProcessFlow to be updated to the database.
	 * @return The same ProcessFlow after being updated.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow updateProcessFlow(
			us.oh.state.epa.stars2.workflow.engine.ProcessFlow pf)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Updates the ProcessFlow data in the database with the contents of "pf".
	 * 
	 * @param pf
	 *            ProcessFlow to be updated to the database.
	 * @return The same ProcessFlow after being updated.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow updateProcessFlow(
			us.oh.state.epa.stars2.workflow.engine.ProcessFlow pf,
			us.oh.state.epa.stars2.database.dao.Transaction trans)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate createCompositeTemplate(
			java.lang.String type, java.lang.Integer extId,
			us.oh.state.epa.stars2.workflow.engine.ServiceTemplateMap[] map,
			boolean subFlow)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Updates an Activity object to the database. The object is assumed to be
	 * fully specified, i.e., all key values are set, and valid. The Activity is
	 * broken up into its constituent parts and each part is updated in the
	 * database as needed.
	 * 
	 * @param act
	 *            Activity to be updated in the database.
	 * @return The Activity after it has been updated.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.workflow.Activity updateActivity(
			us.oh.state.epa.stars2.workflow.Activity act)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Creates an Activity object to the database. The object is assumed to be
	 * fully specified, i.e., all key values are set, and valid. The Activity is
	 * broken up into its constituent parts and each part is updated in the
	 * database as needed. This creation is required to support the generic goto
	 * mechanism.
	 * 
	 * @param act
	 *            Activity to be added in the database.
	 * @return The Activity after it has been created.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.workflow.Activity createActivity(
			us.oh.state.epa.stars2.workflow.Activity act)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Cancels the order associated with "deadmeat". This removes the order from
	 * the provisioning process.
	 * 
	 * @param deadmeat
	 *            The Process that is being canceled.
	 * @throws java.lang.Exception
	 *             Database access error or missing OrderHeader.
	 */
	public void cancelProcess(
			us.oh.state.epa.stars2.workflow.engine.ProcessFlow deadmeat)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Removes an Activity from the parent ProcessFlow. The Activity is removed
	 * in both the in-memory data and the database. The orderId is used to
	 * remove the associated service from the order.
	 * 
	 * @param parent
	 *            Parent process.
	 * @param deadmeat
	 *            Activity to be removed from the parent process.
	 * @param orderId
	 *            The associated order Id.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public void removeActivity(
			us.oh.state.epa.stars2.workflow.engine.ProcessFlow parent,
			us.oh.state.epa.stars2.workflow.Activity deadmeat,
			java.lang.Integer orderId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Creates ActivityTemplate objects for each service in the service map.
	 * This is a skeleton object and has not been saved to the database.
	 * 
	 * @param map
	 *            Map of Service Ids to Activity Template Def Ids.
	 * @param orderId
	 *            The Id of the order we are processing.
	 * @return ArrayList of ActivityTemplate objects, one per Service.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public java.util.ArrayList<us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate> createServiceTemplates(
			us.oh.state.epa.stars2.workflow.engine.ServiceTemplateMap[] map,
			java.lang.Integer orderId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Adjusts the expected and jeopardy durations for ProcessTemplate based on
	 * the values of the sub-processes for each service. The ProcessTemplate
	 * "pt" represents a "Composite" template that is expected to work on all
	 * services in parallel. The overall time duration for the ProcessTemplate
	 * will be the duration of whichever service will take the longest.
	 * 
	 * @param pt
	 *            The ProcessTemplate whose time durations are being adjusted.
	 * @param map
	 *            The services expected to be on this ProcessTemplate.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public void adjustDurations(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate pt,
			us.oh.state.epa.stars2.workflow.engine.ServiceTemplateMap[] map)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Creates Transition objects for a composite ProcessTemplate. In this case,
	 * we don't actually need the ProcessTemplate, just the ActivityTemplates
	 * that are contained in the ProcessTemplate. The Transitions returned by
	 * this method link the output of the decompose template to the input of
	 * each service template. The outputs of each service template are in turn
	 * linked to the input to the "orderComplete" object.
	 * 
	 * @param decompose
	 *            The "Decompose Order/Proposal" ActivityTemplate.
	 * @param orderComplete
	 *            The "Billing/Proposal Ready" ActivityTemplate.
	 * @param svcTemplates
	 *            The ActivityTemplate objects for each service on the order.
	 * @return Transition[] A array of "In-Line" transitions.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.Transition[] createTransitions(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate decompose,
			us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate orderComplete,
			java.util.ArrayList<us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate> svcTemplates)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Creates a <tt>ProcessFlow</tt> based on the Process Template identified
	 * by "processTemplateId". In addition, creates any sub process flows
	 * required to support the top level process flow. The returned array of
	 * ProcessFlow is in the order of creation. Thus, the "zero-th" element is
	 * the parent ProcessFlow for all of the ProcessFlow objects in the array.
	 * Sub-flow creation is recursive; if a sub-flow has sub-flows, these will
	 * be included in the returned array at some index value greater than the
	 * index of the parent process flow.
	 * 
	 * @param processTemplateId
	 *            Process Template Id.
	 * @param accountId
	 *            Account Id.
	 * @param externalId
	 *            The order or proposal Id.
	 * @return ProcessFlow[] An array of all ProcessFlows created for this
	 *         template.
	 * @throws java.lang.Exception
	 *             Invalid Process Template Id or database access error.
	 */
	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow[] createProcessFlows(
			java.lang.Integer processTemplateId, java.lang.Integer accountId,
			java.lang.Integer externalId, java.lang.String expedite,
			java.sql.Timestamp startDt, java.sql.Timestamp dueDt,
			java.lang.Integer userId, String roleDiscrim)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow createTaskProcessFlows(
			java.lang.Integer processTemplateId, java.lang.Integer accountId,
			java.lang.Integer externalId, java.lang.String expedite,
			java.sql.Timestamp startDt, java.sql.Timestamp dueDt,
			java.lang.Integer userId,
			java.util.HashMap<java.lang.String, java.lang.String> data,
			java.lang.Integer taskUserId, String roleDiscrim)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow createTaskProcessFlows(
			java.lang.Integer processTemplateId, java.lang.Integer accountId,
			java.lang.Integer externalId, java.lang.String expedite,
			java.sql.Timestamp startDt, java.sql.Timestamp dueDt,
			java.lang.Integer userId,
			java.util.HashMap<java.lang.String, java.lang.String> data,
			java.lang.Integer taskUserId, String roleDiscrim,
			us.oh.state.epa.stars2.database.dao.Transaction trans)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Set due date and jeopardy date for Process p to the values defined by the
	 * SLA for the service identified by serviceId or the values defined in the
	 * ProcessTemplate object pt if no SLA values are defined.
	 * 
	 * @param pt
	 *            ProcessTemplate
	 * @param serviceId
	 *            Service Id
	 * @param p
	 *            Process being constructed
	 * @throws DAOException
	 */
	public void setProcessDates(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate pt,
			java.lang.Integer serviceId,
			us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess p,
			java.sql.Timestamp startDt, java.sql.Timestamp dueDt)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	/**
	 * Marks a ProcessTemplate and all of its subordinate ActivityTemplates and
	 * Transitions as deprecated. The ProcessTemplate is checked to insure it is
	 * not "out of date" prior to updating it. Returns "true" if the update was
	 * successful. Returns "false" if the ProcessTemplate does not correspond to
	 * any actual database object.
	 * 
	 * @param pt
	 *            ProcessTemplate to be deprecated.
	 * @param conn
	 *            JDBC Connection.
	 * @return boolean="True"
	 * @throws java.lang.Exception
	 *             DB access error or "object out of date" error.
	 */
	public boolean deprecateProcessTemplate(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate pt)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	public void autoAssignTask(
			java.lang.Integer fpId,
			java.lang.String roleCd,
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity pa,
			java.lang.Integer userId, String roleDiscrim)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;	

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity updateLegacyProcessActivity(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity pa)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity updateProcessActivityViewedState(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity pa)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * By bug number 1640, user need a ability to change the referral end date
	 * which is not allowed in workflow engine to do when complete (check in)
	 * the task (activity). This method should not be used in other place.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity modifyProcessActivityEndDate(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity pa)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public void createProcessDatas(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessData[] datas)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	/**
	 * SPECIALTY METHOD. Completely removes processFlows from database.
	 * 
	 * @param processId
	 *            processId of process to remove
	 * @param userId
	 *            userId
	 * @throws DAOException
	 *             Database access error.
	 */
	public void removeProcessFlows(java.lang.Integer processId,
			java.lang.Integer userId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;


	public void createProcessNote(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote pn)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public boolean modifyProcessNote(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote pn)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public void createProcessNote(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote pn,
			us.oh.state.epa.stars2.database.dao.Transaction trans)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	public boolean modifyProcessData(
			java.util.HashMap<java.lang.String, java.lang.String> dataMap,
			java.lang.Integer processId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess cloneProcess(
			java.lang.Integer processId, java.lang.Integer userId, Integer externalId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * This BO is explictly written to migrate non-completed workflows from
	 * legacy applications such as Stars to Stars2. The workflows created at
	 * various stages of completion. The activityTemplateDefId will indicate the
	 * current task that should be marked as active (note: we are assuming only
	 * one active task per workflow process instance - no paraellel tasks). All
	 * other tasks will be marked as skipped. Migrate ProcessFlow based on the
	 * process template identified by <tt>processTemplateId</tt>. In addition to
	 * creating the ProcessFlow, process flow information is also saved in the
	 * database. Note that the ProcessFlow has all of its Activities (also saved
	 * to the database), DataFields, and Transitions. The "parentProcess" and
	 * "parentActivity" objects identify parent process and activity if this
	 * ProcessFlow is a sub-flow of another process. If this ProcessFlow is not
	 * a sub-flow, then set these values to <tt>null</tt>. If there is no
	 * ProcessTemplate in the database corresponding to processTemplateId, then
	 * an Exception will be thrown.
	 * 
	 * @param processTemplateId
	 *            process template Id.
	 * @param accountId
	 *            The Id of the account on the associated order.
	 * @param externalId
	 *            Usually, an order number or proposal number.
	 * @param serviceId
	 *            The Id of the service associated with this order.
	 * @param parentProcess
	 *            The parent process for this sub-process.
	 * @param parentActivity
	 *            The parent activity for this sub-process.
	 * @param actvityTemplateDefId
	 *            The current active/in progress task
	 * @param actStartDt
	 *            The date that this task was started.
	 * @return ProcessFlow A fully specified ProcessFlow object.
	 * @throws java.lang.Exception
	 *             No ProcessTemplate or database access error.
	 */
	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow migrateProcessFlow(
			java.lang.Integer processTemplateId, java.lang.Integer accountId,
			java.lang.Integer externalId, java.lang.String expedite,
			java.sql.Timestamp startDt, java.sql.Timestamp dueDt,
			java.lang.Integer serviceId,
			us.oh.state.epa.stars2.workflow.engine.ProcessFlow parentProcess,
			us.oh.state.epa.stars2.workflow.Activity parentActivity,
			java.lang.Integer userId, java.lang.Integer activityTemplateDefId,
			java.sql.Timestamp actStartDt)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;
	
	public void scheduleRecurring(java.lang.Integer processId,
			java.lang.Integer activityId, java.lang.Integer loopCnt,
			java.util.Date dateToRun, java.lang.Integer userId)
					throws us.oh.state.epa.stars2.framework.exception.DAOException,
					java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity updateProcessActivity(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity pa)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity modifyProcessActivity(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity processActivity)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	// decouple workflow ... doesn't appear to be used; otherwise needs rework
	/**
	 * Reassign activities assigned to userId to the default user for that
	 * activity (based on facility role code). If the default user is the same
	 * as the specified user, reassign the activity to Stars2 Admin.
	 * 
	 * @param userId
	 *            - id of user whose tasks are to be reassigned
	 */
//	public void reassignCurrentActivitiesForUser(int userId)
//			throws us.oh.state.epa.stars2.framework.exception.DAOException,
//			java.rmi.RemoteException;
//	
	
    public ProcessFlow[] createProcessFlows(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer userId, String roleDiscrim, Transaction trans)
            throws DAOException; 
    
    public ProcessFlow[] createProcessFlows(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer userId, String roleDiscrim, 
            Integer assignedUserId, Transaction trans)
            throws DAOException; 
    
    Activity updateActivity(Activity act, Transaction trans) throws DAOException; 
    
    public boolean modifyProcessData(HashMap<String, String> dataMap, Integer processId,
            Transaction trans) throws DAOException;
    
    void removeScheduledTrigger(String triggerName, String jobName) throws DAOException;

}
