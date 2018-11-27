package us.wy.state.deq.impact.bo;

import java.util.LinkedHashMap;
import java.util.Map;

import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;

public interface ReadWorkFlowService {
	
	/**
	 * Returns all available ServiceDetailDef objects from the database. Returns
	 * "null" if no objects are found. Note that the returned object is the
	 * workflow designer's "bizObj" version of ServiceDetailDef.
	 * 
	 * @return ServiceDetailDef[] All available objects from the database.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail[] retrieveDataDetails()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all of the <code>TransitionDef</code> objects currently defined
	 * in the system. If no transition defs are found, then an empty array is
	 * returned.
	 * 
	 * @return TransitionDef[] All transition details in the system.
	 * @throws DAOException
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionDef[] retrieveTransitionDef()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all of the <code>ActivityStatusDef</code> objects currently
	 * defined in the system. If no ActivityStatus defs are found, then an empty
	 * array is returned.
	 * 
	 * @return ActivityStatusDef[] All ActivityStatus details in the system.
	 * @throws DAOException
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef[] retrieveActivityStatusDef()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns subset of the <code>ActivityStatusDef</code> objects currently
	 * defined in the system. If no ActivityStatus defs are found, then an empty
	 * array is returned.
	 * 
	 * @return ActivityStatusDef[] All ActivityStatus details in the system.
	 * @throws DAOException
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef[] retrieveToDoStatusDef()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all of the <code>ActivityStatusDef</code> objects currently
	 * defined in the system. If no ActivityStatus defs are found, then an empty
	 * array is returned.
	 * 
	 * @return ActivityStatusDef[] All ActivityStatus details in the system.
	 * @throws DAOException
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef[] retrieveProcessDefs()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all of the <code>ActivityStatusDef</code> objects currently
	 * defined in the system. If no ActivityStatus defs are found, then an empty
	 * array is returned.
	 * 
	 * @return ActivityStatusDef[] All ActivityStatus details in the system.
	 * @throws DAOException
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.PerformerDef[] retrievePerformerDefs()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all of the <code>ActivityStatusDef</code> objects currently
	 * defined in the system. If no ActivityStatus defs are found, then an empty
	 * array is returned.
	 * 
	 * @return ActivityStatusDef[] All ActivityStatus details in the system.
	 * @throws DAOException
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef[] retrieveActivityTypes()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

    ActivityTemplate[] retrieveActivityTemplates()
            throws DAOException;

	/**
	 * Returns all currently active ProcessTemplates from the database. These
	 * correspond to workflows that are currently available to the rest of the
	 * system.
	 * 
	 * @return ProcessTemplate[] All currently active (and therefore editable)
	 *         ProcessTemplates.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate[] retrieveTemplateData()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all of the currently active ActivityTemplateDef objects from the
	 * database for "templateCd", i.e., provisioning ActivityTemplateDef
	 * objects, sales flow ActivityTemplateDef objects, etc.
	 * 
	 * @param templateCd
	 *            The kind of process template being edited.
	 * @return ActivityTemplateDef[] All active activeActivityTemplateDef
	 *         objects that support that kind of process template.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef[] retrieveActivityTemplateDefs(
			java.lang.String templateCd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Retrieves all of the sub-flows for the given process code. The process
	 * code is either "prov" (normal provisioning) or "sfa" (sales flow).
	 * Returns null or empty array if no sub-flows were found.
	 * 
	 * @param processCd
	 *            Process code for provisioning or sales flow.
	 * @return An array of ActivityTemplateDef objects, one per sub-flow.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef[] retrieveSubFlowsByProcessCd(
			java.lang.String processCd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all of the Activity objects currently associated with
	 * "processTemplateId" in the database.
	 * 
	 * @param processTemplateId
	 *            Unique database key value.
	 * @return Activity[] All of the process template's Activity objects.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.workflow.Activity[] retrieveActivities(
			java.lang.Integer processTemplateId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all of the Transitions, i.e., the links between Activities,
	 * defined for a ProcessTemplate from the database.
	 * 
	 * @param processTemplateId
	 *            Integer key value.
	 * @return Transition[] All of the Transitions identified for this process.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.Transition[] retrieveTransitions(
			java.lang.Integer processTemplateId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns all process data templates from the database for the given
	 * process template Id.
	 * 
	 * @param processTemplateId
	 *            process template Id.
	 * @return ProcessDataTemplate[] Process-specific service detail data.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessDataTemplate[] retrieveProcessTemplateData(
			java.lang.Integer processTemplateId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns an array of Activity objects based on a "processTemplateId". This
	 * means that all of the template components are present in the Activity,
	 * but no Process-specific information will be included. Returns "null" if
	 * the "processTemplateId" is not found in the database.
	 * 
	 * @param container
	 *            The ProcessFlow that will contain these Activities.
	 * @return Activity[] An array of partially specified Activity objects.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.workflow.Activity[] retrieveTemplateActivities(
			us.oh.state.epa.stars2.workflow.engine.ProcessFlow container)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Looks up an ActivityTemplateDef object given its activity template def
	 * Id. I did it this way to avoid using the same DAO connection to do
	 * database reads and inserts/updates (Postgres seems to have a problem with
	 * this if you do it too much). If no matching object is found in the
	 * database, "null" is returned.
	 * 
	 * @param atdId
	 *            activity template def Id.
	 * @return ActivityTemplateDef.
	 * @throws java.lang.Exception
	 *             Database access error.
	 */
	public us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef retrieveActivityTemplateDef(
			java.lang.Integer atdId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess retrieveProcess(
			java.lang.Integer processId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity[] retrieveProcessActivities(
			java.lang.Integer processId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate retrieveActivityTemplate(
			java.lang.Integer actId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.Transition[] retrieveTransitions(
			java.lang.Integer processTemplateId, boolean includeAll)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess retrieveSubFlowProcess(
			java.lang.Integer processId, java.lang.Integer actTemplId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;


	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity[] retrieveActivityList(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity pa)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

    public ProcessActivity[] retrieveActivityListForFacilities(ProcessActivity pa, String[] facilities, Integer userIdForSearch)
            throws DAOException;
    
	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity[] retrieveActivitiesToUnDeadend(
			int permitId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity retrieveActivity(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity pa)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivityLight[] retrieveActivityListLight(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity pa)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public java.util.LinkedHashMap<java.lang.String, java.lang.Integer> retrieveWorkflowTempIdAndNm()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public java.util.LinkedHashMap<java.lang.String, java.lang.String> retrieveProcessTemplatesByType(
			java.lang.String type)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef[] retrieveProcessGroupByType()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;


	public java.util.LinkedHashMap<java.lang.String, java.lang.String> getStateDef()
			throws java.rmi.RemoteException;

	public java.util.LinkedHashMap<java.lang.String, java.lang.String> getStatusDef()
			throws java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess[] retrieveProcessList(
			us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess wp)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.DataField[] retrieveDataFieldsForProcess(
			java.lang.Integer processId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Retrieve users of the facility.
	 * 
	 * @param el
	 *            EventLog The object to filter.
	 * @throws DAOException
	 *             Database access error.
	 */
	public java.lang.Integer[] retrieveUserIdsOfFacility(
			java.lang.Integer fpId, java.lang.String facilityRoleCd)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote[] retrieveProcessNotes(
			us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote pn)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	/**
	 * Returns the active ProcessFlow of processId from the database. These are
	 * ProcessFlows with a "null" end date, meaning that they have not yet been
	 * completed. Note that the return is simply an array of validated
	 * ProcessFlows. Parent or subflow relationships among the returned
	 * ProcessFlow objects are not established or "connected" by this method.
	 * 
	 * @return ProcessFlow Currently active ProcessFlow of processId.
	 * @throws DAOException
	 *             Database access error or invalid ProcessFlow.
	 */
	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow retrieveActiveProcessFlow(
			java.lang.Integer processId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.workflow.engine.ProcessFlow[] retrieveParentActiveProcessFlow(
			java.lang.Integer parentProcessId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityReferralType[] retrieveActivityReferralTypes()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate retrieveProcessTemplateForComposite(
			java.lang.String type, java.lang.Integer id)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ControllerConfig[] retrieveAllControllerConfigs()
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate retrieveProcessTemplate(
			int ptId)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate retrieveProcessTemplate(
			int ptId, us.oh.state.epa.stars2.database.dao.Transaction trans)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;

	public us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef retrieveSubFlowDef(
			java.lang.String subFlowName)
			throws us.oh.state.epa.stars2.framework.exception.DAOException,
			java.rmi.RemoteException;


	public java.lang.String getParameter(java.lang.String parm)
			throws java.rmi.RemoteException;

	public java.lang.String getParameterValue(java.lang.String parameterName,
			java.lang.String defaultValue) throws java.rmi.RemoteException;

	public java.lang.Integer getParameterValue(java.lang.String parameterName,
			java.lang.Integer defaultValue) throws java.rmi.RemoteException;

	public LinkedHashMap<String, Integer> retrieveWorkflowTempIdAndNm(Transaction trans) throws DAOException;

	public Map<Integer, String> retrievePermittingActivityTypes() throws DAOException;
	
	
	Integer findActiveProcessByExternalObject(Integer processTemplateId, 
			Integer externalId) throws DAOException;

	public ProcessFlow retrieveProcessFlow(Integer processId) throws DAOException;
}
