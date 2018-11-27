package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.Map;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityReferralType;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ControllerConfig;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetailBase;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.PerformerDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivityLight;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessData;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessDataTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

/**
 * <p>
 * Title: WorkFlowDAO
 * </p>
 * 
 * <p>
 * Description: Interface that defines all public methods of a WorkFlowDAO. This
 * is the "database level" layer of workflow processing. The objects defined at
 * this level usually map directly to database tables.
 * </p>
 * 
 * <p>
 * As is customary throughout much of the system, <tt>Template</tt> objects
 * define essentially static data, while another object will define specific
 * instance data. For example, the <tt>ProcessTemplate</tt> class defines a
 * single workflow process, such as its name, process type, expected duration,
 * etc., whereas the <tt>Process</tt> classe defines process-specific data
 * such as the parent process, start date, due date, jeopardy date, end date,
 * etc.
 * </p>
 * 
 * <p>
 * Note that other, higher level objects are used by the WorkFlow engine for
 * workflow and sales flow processing. These objects are defined in the
 * "app.bizobj.workflow" package.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author
 */
public interface WorkFlowDAO extends TransactableDAO {

	Integer findActiveProcessByExternalObject(Integer processTemplateId, 
			Integer externalId) throws DAOException;
	
	ActivityTemplate[] retrieveActivityTemplates()
			throws DAOException;
	
	String retrieveRoleCdByParent(String facilityRoleCd, String roleDiscrim)
			throws DAOException;

	/**
	 * @return
	 * @throws DAOException
	 */
	FacilityRoleDef[] retrieveFacilityRoleDefs() throws DAOException;

    SimpleIdDef[] retrieveWorkflowTempIdAndNm() throws DAOException;

    
    SimpleIdDef[] retrieveProcessGroupByType() throws DAOException;

    /**
     * Creates a <tt>ProcessTemplate</tt> object in the underlying database. A
     * key value is generated and assigned to the object prior to its insertion
     * into the database. The returned object is identical to the input object,
     * except that generated key value is included in the returned value.
     * 
     * @param pt
     *            ProcessTemplate to be inserted into the database.
     * 
     * @return The same ProcessTemplate, but with the assigned key.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessTemplate createProcessTemplate(ProcessTemplate pt)
            throws DAOException;

    /**
     * Creates an <tt>ActitivyTemplate</tt> object in the underlying database.
     * A key value is generated and assigned to the object prior to its
     * insertion into the database. The returned object is identical to the
     * input object, except that generated key value is included in the returned
     * value.
     * 
     * @param at
     *            ActivityTemplate to be inserted into the database.
     * 
     * @return The same ActivityTemplate, but with the assigned key.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplate createActivityTemplate(ActivityTemplate at)
            throws DAOException;

    /**
     * Creates an <tt>ActitivyTemplateDef</tt> object in the underlying
     * database. A key value is generated and assigned to the object prior to
     * its insertion into the database. The returned object is identical to the
     * input object, except that generated key value is included in the returned
     * value.
     * 
     * @param atd
     *            ActivityTemplateDef to be inserted into the database.
     * 
     * @return The same ActivityTemplateDef, but with the assigned key.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplateDef createActivityTemplateDef(ActivityTemplateDef atd)
            throws DAOException;

    /**
     * Creates a <tt>ProcessActivity</tt> object in the underlying database.
     * In the database, this table has a compound key consisting of "process_id"
     * and "activity_template_id". Therefore, no key is generated prior to
     * inserting the object in the database and the returned object is identical
     * to the input object.
     * 
     * @param pa
     *            ProcessActivity to be inserted into the database.
     * 
     * @return The same ProcessActivity.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessActivity createProcessActivity(ProcessActivity pa)
            throws DAOException;

    /**
     * Creates a <tt>Process</tt> object in the underlying database. A key
     * value is generated and assigned to the object prior to its insertion into
     * the database. The returned object is identical to the input object,
     * except that generated key value is included in the returned value.
     * 
     * @param p
     *            Process to be inserted into the database.
     * 
     * @return The same Process, but with the assigned key.
     * 
     * @throws DAOException
     *             Database access error.
     */
    WorkFlowProcess createProcess(WorkFlowProcess p) throws DAOException;

    /**
     * Creates a <tt>Transition</tt> object in the underlying database.
     * Currently, Transition objects map to the "Transition_Data" table. In the
     * database, this table has a compound key. Therefore, no key is generated
     * prior to inserting the object in the database and the returned object is
     * identical to the input object.
     * 
     * @param trans
     *            Transition to be inserted into the database.
     * 
     * @return The same Transition.
     * 
     * @throws DAOException
     *             Database access error.
     */
    Transition createTransition(Transition trans) throws DAOException;

    /**
     * Creates a <tt>DataField</tt> object in the underlying database.
     * Currently, DataField objects map to the "Process_Data" table. In the
     * database, this table has a compound key. Therefore, no key is generated
     * prior to inserting the object in the database and the returned object is
     * identical to the input object.
     * 
     * @param df
     *            DataField to be inserted into the database.
     * 
     * @return The same DataField.
     * 
     * @throws DAOException
     *             Database access error.
     */
    DataField createDataField(DataField df) throws DAOException;

    /**
     * Creates a <tt>ProcessDataTemplate</tt> object in the underlying
     * database. A key value is generated and assigned to the object prior to
     * its insertion into the database. The returned object is identical to the
     * input object, except that generated key value is included in the returned
     * value. Also, the "lastModified" timestamp is set.
     * 
     * @param ptd
     *            ProcessDataTemplate to be inserted into the database.
     * 
     * @return The same ProcessDataTemplate, but with the assigned key and
     *         timestamp.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessDataTemplate createProcessDataTemplate(ProcessDataTemplate ptd)
            throws DAOException;

    /**
     * Deprecates a ProcessTemplate plus all of its associated ActivityTemplates
     * and Transitions. Deprecation leaves the template in the database, but
     * marks it "unavailable" for future use. This way, referential integrity is
     * maintained for any previous processes that have used this template.
     * Returns "true" if "pt" corresponds to a database object and is
     * successfully deprecated. Otherwise, returns "false".
     * 
     * @param pt
     *            ProcessTemplate to be deprecated.
     * 
     * @return boolean True if "pt" was successfully deprecated.
     * 
     * @throws DAOException
     *             Database access error.
     */
    boolean deprecateProcessTemplate(ProcessTemplate pt) throws DAOException;

    /**
     * Marks all of the ProcessDataTemplate objects in the database that are
     * associated with "processTemplateId" as "deprecated", i.e., logically
     * deleted. Returns "true" if the update was successful or "false" if the
     * update failed.
     * 
     * @param processTemplateId
     *            Integer Process Template Id.
     * 
     * @return boolean "True" if successful.
     * 
     * @throws DAOException
     *             Database access error.
     */
    boolean deprecateProcessDataTemplate(Integer processTemplateId)
            throws DAOException;

    /**
     * @param pt
     * @return
     * @throws DAOException
     */
    boolean deprecateTransitions(ProcessTemplate pt) throws DAOException;

    /**
     * @param pt
     * @return
     * @throws DAOException
     */
    boolean deprecateSubFlowTemplate(ProcessTemplate pt) throws DAOException;

    /**
     * @param pt
     * @return
     * @throws DAOException
     */
    boolean deprecateActivityTemplates(ProcessTemplate pt) throws DAOException;

    /**
     * Modifies transition data in the database to be identical to the contents
     * of <tt>trans</tt>. Returns "true" if a matching record was found and
     * was modified. Returns "false" if no matching record was found.
     * 
     * @param trans
     *            Transition object containing values to be updated.
     * 
     * @return Transition
     * 
     * @throws DAOException
     *             Database access error.
     */
    Transition modifyTransition(Transition trans) throws DAOException;

    /**
     * Modifies an ActivityTemplate in the database to be identical to the
     * contents of <tt>at</tt>. Returns "true" if a matching record was found
     * and was modified. Returns "false" if no matching record was found.
     * 
     * @param at
     *            ActivityTemplate object containing values to be updated.
     * 
     * @return ActivityTemplate.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplate modifyActivityTemplate(ActivityTemplate at)
            throws DAOException;

    /**
     * Modifies a ProcessActivity in the database to be identical to the
     * contents of <tt>pa</tt>. Returns "true" if a matching record was found
     * and was modified. Returns "false" if no matching record was found.
     * 
     * @param pa
     *            ProcessActivity object containing values to be updated.
     * 
     * @return ProcessActivity
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessActivity modifyProcessActivity(ProcessActivity pa)
            throws DAOException;

    /**
     * Modifies data field data in the database to be identical to the contents
     * of <tt>df</tt>. Returns "true" if a matching record was found and was
     * modified. Returns "false" if no matching record was found.
     * 
     * @param df
     *            DataField object containing values to be updated.
     * 
     * @return DataField
     * 
     * @throws DAOException
     *             Database access error.
     */
    DataField modifyDataField(DataField df) throws DAOException;

    /**
     * Modifies a Process in the database to be identical to the contents of
     * <tt>p</tt>. Returns "true" if a matching record was found and was
     * modified. Returns "false" if no matching record was found.
     * 
     * @param p
     *            Process object containing values to be updated.
     * 
     * @return WorkFlowProcess
     * 
     * @throws DAOException
     *             Database access error.
     */
    WorkFlowProcess modifyProcess(WorkFlowProcess p) throws DAOException;

    /**
     * Modifies a ProcessTemplate in the database to be identical to the
     * contents of <tt>pt</tt>. Returns "true" if a matching record was found
     * and was modified. Returns "false" if no matching record was found.
     * 
     * @param pt
     *            ProcessTemplate object containing values to be updated.
     * 
     * @return ProcessTemplate
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessTemplate modifyProcessTemplate(ProcessTemplate pt)
            throws DAOException;

    /**
     * Modifies an ActivityTemplateDef in the database to be identical to the
     * contents of <tt>atd</tt>. Returns "true" if a matching record was
     * found and was modified. Returns "false" if no matching record was found.
     * 
     * @param atd
     *            ActivityTemplateDef object containing values to be updated.
     * 
     * @return ActivityTemplateDef
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplateDef modifyActivityTemplateDef(ActivityTemplateDef atd)
            throws DAOException;

    /**
     * @param atd
     * @throws DAOException
     */
    void removeActivityDetailData(ActivityTemplateDef atd) throws DAOException;

    /**
     * @param atd
     * @throws DAOException
     */
    ActivityTemplateDef createDependentDetailData(ActivityTemplateDef atd)
            throws DAOException;

    /**
     * Modifies a ProcessDataTemplate in the database to be identical to the
     * contents of <tt>ptd</tt>. Returns "true" if a matching record was
     * found and was modified. Returns "false" if no matching record was found.
     * In this case, modification consists of deprecating the original record
     * and then creating a new record, so the object key will change.
     * 
     * @param ptd
     *            ProcessDataTemplate object containing values to be updated.
     * 
     * @return ProcessDataTemplate
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessDataTemplate modifyProcessDataTemplate(ProcessDataTemplate ptd)
            throws DAOException;

    /**
     * Returns all of the <tt>ProcessActivity</tt> objects associated with the
     * specified process flow. These objects contain specific Activity instance
     * data for an Activity associated with a ProcessFlow. If no objects are
     * found for this process Id, then "null" is returned.
     * 
     * @param processId
     *            Id of the process flow.
     * 
     * @return ProcessActivity[] All ProcessActivities for this process.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessActivity[] retrieveProcessActivities(Integer processId)
            throws DAOException;

    /**
     * Returns all of the <tt>ProcessActivity</tt> objects in the Ready
     * Activity list
     * 
     * @param subStytem -
     *            prov
     * @param actStatus -
     *            ready/working
     * 
     * @return ProcessActivity[] All ProcessActivities in Readylist.
     * 
     * @throws DAOException
     *             Database access error.
     */

    ProcessActivity[] retrieveBlockedActivityList(String subSystem, String type)
            throws DAOException;

    /**
     * Returns all of the <tt>ProcessActivity</tt> objects in the Workingivity
     * list
     * 
     * @param subStytem -
     *            prov
     * @param actStatus -
     *            ready/working
     * @param user
     *            Id
     * 
     * @return ProcessActivity[] All ProcessActivities in Workinglist.
     * 
     * @throws DAOException
     *             Database access error.
     */

    ProcessActivity[] retrieveActivityList(String subSystem, String type,
            Integer userId) throws DAOException;
    
    

    /**
     * Returns all of the <tt>ProcessActivity</tt> objects whose name is
     * "activityName" and whose current state is "activityStatusCd". If no
     * matching values are found, a zero-length array is returned. The name must
     * match an "activity_template_nm" in the Activity_Template (or
     * Activity_Template_Def) table. The "status code" must be a value from the
     * "activity_status_cd" column of the "Activity_Status_Def" table.
     * 
     * @param activityName
     *            String Activity name.
     * @param activityStatusCd
     *            String Activity status code.
     * 
     * @return ProcessActivity[] All ProcessActivity objects whose name is
     *         "activityName" and whose current status is "activityStatusCd".
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessActivity[] retrieveActivityList(String activityName,
            String activityStatusCd) throws DAOException;

    ProcessActivity[] retrieveActivityListForFacilities(ProcessActivity pa, 
    		String[] facilities, Integer userIdForSearch) throws DAOException;
    
    /**
     * Returns all of the <tt>ProcessActivity</tt> objects that correspond to
     * all Activities based on Activity search criteria
     * 
     * @return ProcessActivity[] All Activity objects which meet the serch
     *         criteria.
     * 
     * @throws DAOException
     *             Database access error.
     */
    /*
     * ProcessActivity[] retrieveSearchActivityList (String subSystem,
     * DynamicObject object, ) throws DAOException ;
     */
    /**
     * Returns a <tt>ProcessActivity</tt> given its process Id and activity
     * template Id (i.e., the primary key). If no matching object is found, then
     * "null" is returned.
     * 
     * @param processId
     *            process Id.
     * @param activityTemplId
     *            activity template Id.
     * 
     * @return ProcessActivity matching object.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessActivity retrieveProcessActivity(Integer processId,
            Integer activityTemplId) throws DAOException;

    /**
     * Returns all of the <tt>Transition</tt> objects associated with the
     * specified process template. Transitions are created at the template level
     * and shared by all process flows created from that template. A Transition
     * can be "deprecated", meaning that it has been logically removed from the
     * current version of the template. If deprecated Transitions should be
     * included, then set <tt>includeAll</tt> to "true". If they should be
     * ignored, set "includeAll" to "false".
     * 
     * @param processTemplateId
     *            Id of the process template.
     * @param includeAll
     *            Set to "false" to exclude deprecated transistions.
     * 
     * @return Transition[] All Transitions for this process.
     * 
     * @throws DAOException
     *             Database access error.
     */
    Transition[] retrieveTransitions(Integer processTemplateId,
            boolean includeAll) throws DAOException;

    /**
     * Retrieves all of the <tt>DataField</tt> objects associated with the
     * process flow identified by "processId". The primary difference between
     * the values returned from this method and
     * "retrieveDataFieldsForProcessTemplate()" is that the values returned by
     * this method have the current values for the process flow. The other
     * method returns a DataField initialized to default settings. If no
     * DataFields corresponding to "processId" are found, then "null" is
     * returned.
     * 
     * @param processId
     *            Id of the process flow.
     * 
     * @return DataField[] All DataFields associated with this process.
     * 
     * @throws DAOException
     *             Database access error.
     */
    DataField[] retrieveDataFieldsForProcess(Integer processId)
            throws DAOException;

    /**
     * Retrieves all of the <tt>DataField</tt> objects associated with the
     * process template identified by "processTemplateId". The primary
     * difference between the values returned from this method and
     * "retrieveDataFieldsForProcess()" is that the values returned by this
     * method have default values for the process flow. The other method returns
     * a DataField with current process flow settings. If no DataFields
     * corresponding to "processTemplateId" are found, then "null" is returned.
     * 
     * @param processTemplateId
     *            Id of the process template.
     * 
     * @return DataField[] All DataFields associated with this template.
     * 
     * @throws DAOException
     *             Database access error.
     */
    DataField[] retrieveDataFieldsForProcessTemplate(Integer processTemplateId)
            throws DAOException;

    /**
     * Returns the <tt>ActivityTemplateDef</tt> object for the given "atdId"
     * (Activity Template Def Id). If no matching object is found, returns
     * "null".
     * 
     * @param atdId
     *            activity template def Id.
     * 
     * @return Matching ActivityTemplateDef object.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplateDef retrieveActivityTemplateDef(Integer atdId)
            throws DAOException;

    /**
     * Returns the sub-flow <tt>ActivityTemplateDef</tt> object for the given
     * "processTemplateId" and "processTemplateNm". This sub-flow record is used
     * to tie the process to the provisioning system.
     * 
     * @param processTemplateId
     *            Integer Process Template Id.
     * @param processTemplateNm
     *            String Process Template Name.
     * 
     * @return ActivityTemplateDef Subflow definition.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplateDef retrieveSubflowActivityTemplateDef(
            Integer processTemplateId, String processTemplateNm)
            throws DAOException;

    /**
     * Returns the sub-flow <tt>ActivityTemplateDef</tt> object whose name is
     * identified by "subflowName". The sub-flow name identifies a workflow
     * process. The "Activity_Template_Def" record will have this same name, and
     * will have a "performer_type_cd" of 'S' to indicate that it is a sub-flow
     * designation. The "processTemplateId" in the returned object identifies
     * the actual process template.
     * 
     * @param subflowName
     *            String The name of the sub-flow.
     * 
     * @return ActivityTemplateDef The Sub-flow designator object.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplateDef retrieveSubFlowDef(String subflowName)
            throws DAOException;

    /**
     * Returns all of the ActivityTemplateDef objects of the given type ordered
     * alphabetically by name. The <code>templateCd</code> must correspond to
     * one of the "activity_template_type_cd" values from the
     * Activity_Template_Type_Def table or a null/empty array will be returned.
     * 
     * @param templateCd
     *            activity template type code.
     * 
     * @return An array of ActivityTemplateDef objects for that type.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplateDef[] retrieveActivityTemplateDefs(String templateCd)
            throws DAOException;

    /**
     * Returns all of the ActivityTemplateDef objects that correspond to
     * sub-flows of a given process code. The "process code" must correspond to
     * one of the values from the "Process_Def" table. A sub-flow is simply a
     * special case of an ActivityTemplateDef and is stored as such in the
     * database.
     * 
     * @param processCd
     *            The process code or type.
     * 
     * @return ActivityTemplateDef[] An array of sub-flow objects.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplateDef[] retrieveSubFlowsByProcessCd(String processCd)
            throws DAOException;

    /**
     * Returns the <tt>ActivityTemplate</tt> object for the given
     * "activityTemplateId". If no matching object is found, returns "null".
     * 
     * @param activityTemplateId
     *            activity template Id.
     * 
     * @return Matching ActivityTemplate object.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplate retrieveActivityTemplate(Integer activityTemplateId)
            throws DAOException;

    /**
     * Retrieves all of the <tt>ActivityTemplate</tt> objects associated with
     * the process template identified by "processTemplateId". If no
     * ActivityTemplates corresponding to "processTemplateId" are found, then
     * "null" is returned.
     * 
     * @param processTemplateId
     *            Id of the process template.
     * 
     * @return ActivityTemplate[] All ActivityTemplate associated with this
     *         template.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ActivityTemplate[] retrieveActivityTemplatesForProcessTemplate(
            Integer processTemplateId) throws DAOException;

    /**
     * Retrieves all of the <tt>ProcessTemplate</tt> objects associated with
     * the process template identified by "procTemplId". If no ProcessTemplates
     * corresponding to "procTemplId" are found, then "null" is returned. Note
     * that the ProcessTemplate object has an embedded ActivityTemplate[] array
     * that is also instantiated by this retrieval.
     * 
     * @param procTemplId
     *            Id of the process template.
     * 
     * @return ProcessTemplate[] All ProcessTemplate associated with this Id.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessTemplate retrieveProcessTemplate(Integer procTemplId)
            throws DAOException;

    /**
     * Retrieves the ProcessTemplate associated with a particular order or
     * proposal. The "type" should be either "Order" or "Proposal". The "Id" is
     * the order or proposal Id. Returns the corresponding ProcessTemplate, if
     * found, or "null" if no match is found.
     * 
     * @param type
     *            Either "Order" or "Proposal"
     * @param id
     *            The corresponding order Id or proposal Id.
     * 
     * @return ProcessTemplate The ProcessTemplate for this composite.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessTemplate retrieveProcessTemplateForComposite(String type, Integer id)
            throws DAOException;

    /**
     * Returns the <tt>Process</tt> object for the given "processId". If no
     * matching object is found, returns "null".
     * 
     * @param processId
     *            process Id.
     * 
     * @return Matching Process object.
     * 
     * @throws DAOException
     *             Database access error.
     */
    WorkFlowProcess retrieveProcess(Integer processId) throws DAOException;

    /**
     * Returns all of the <tt>Process</tt> objects that correspond to all
     * process flows that have not yet been completed. A process flow is
     * considered complete when it has a non-null "end_dt". If no incomplete
     * processes are found, then "null" is returned.
     * 
     * @return Process[] All Process objects with "null" end dates.
     * 
     * @throws DAOException
     *             Database access error.
     */
    WorkFlowProcess[] retrieveInWorkProcesses() throws DAOException;

    /**
     * Returns the Process that is the subflow of a <code>ProcessActivity</code>
     * identified by its database key, process Id and activity template Id. If
     * the <code>ProcessActivity</code> has no subflow, then <tt>null</tt>
     * is returned.
     * 
     * @param processId
     *            Integer Process Id.
     * @param actTemplId
     *            Integer Activity Template Id.
     * 
     * @return Process Subflow process of the Activity, if any.
     * 
     * @throws DAOException
     *             Database access error.
     */
    WorkFlowProcess retrieveSubFlowProcess(Integer processId, Integer actTemplId)
            throws DAOException;

    /**
     * Retrieves all of the ProcessTemplates from the database. Process
     * templates have two additional state parameters, "deprecated" (meaning
     * that this template is no longer active on the system) and "dynamic"
     * (meaning that this process template represents a composite order object).
     * Deprecated process templates are logically equivalent to deleted
     * templates (but not actually deleted due to referential integrity
     * constraints). Note that any embedded components, such as activity
     * templates, etc., are NOT included in the retrieval.
     * 
     * @param includeDeprecated
     *            Set to "true" if deprecated templates should be included in
     *            the retrieval.
     * @param includeDynamic
     *            Set to "true" if dynamic templates should be included in the
     *            retrieval.
     * 
     * @return ProcessTemplate[] An array of ProcessTemplate[] objects.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessTemplate[] retrieveProcessTemplates(boolean includeDeprecated,
            boolean includeDynamic) throws DAOException;

    /**
     * Returns all of the ProcessDataTemplate objects from the database for the
     * specific process template Id. These objects identify workflow-specific
     * detail data items that are not bound directly to any specify workflow
     * activity. If the process has no process-specific data, then "null" or an
     * empty array is returned.
     * 
     * @param procTemplId
     *            The Id of the process template.
     * 
     * @return ProcessDataTemplate[] All process-specific service detail data.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessDataTemplate[] retrieveProcessDataTemplates(Integer procTemplId)
            throws DAOException;

    /**
     * Returns all of the ProcessDataTemplate objects from the database for the
     * specific product catalog def Id. These objects identify workflow-specific
     * detail data items that are not bound directly to any specify workflow
     * activity. If the process has no process-specific data, then "null" or an
     * empty array is returned.
     * 
     * @param catDefId
     *            Integer Product Catalog Def Id
     * @param wfType
     *            String WorkFlow type, e.g., "NEW" service.
     * 
     * @return ProcessDataTemplate[] Data objects for the entire workflow.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ProcessDataTemplate[] retrieveProcessDataTemplatesViaCatalog(
            Integer catDefId, String wfType) throws DAOException;

    /**
     * SPECIALTY METHOD. Moves a subflow from one Process to another. The reason
     * for moving the Subflow instead of trying to copy it is to preserve the
     * state of its Activities. This method is done as part of order splitting,
     * i.e., moving a service on an existing order to an new order. After all of
     * the templates and processes have been created, this method moves an
     * existing subflow from its old process to a new one. The new process is
     * assumed to be essentially empty, consisting of the "Decompose Order"
     * Activity which is linked by a single Transition to a "place holder"
     * Activity, which is in turn linked by a single Transition to the "Billing
     * Ready" Activity. The database tables are updated to move the subflow from
     * its old process (and old process template) to the new process (and
     * process template).
     * 
     * @param oldActivityTemplateId
     *            Old activity template Id.
     * @param oldParentProcessTemplId
     *            Old parent process template Id.
     * @param oldParentProcessId
     *            Old parent process Id.
     * @param newActivityTemplateId
     *            New activity template Id.
     * @param newParentProcessTemplId
     *            New parent process template Id.
     * @param newParentProcessId
     *            New parent process Id.
     * @param subflowProcessId
     *            The subflow process we are moving to a new order.
     * @param newOrderId
     *            The new order Id we are moving the subflow to.
     * 
     * @throws DAOException
     *             Database access error.
     */
    void moveSubFlow(Integer newParentProcessId, Integer newActivityTemplateId,
            Integer newOrderId, Integer subflowProcessId) throws DAOException;

    /**
     * @param processId
     * @throws DAOException
     */
    void removeProcessActivities(Integer processId) throws DAOException;

    /**
     * @param processId
     * @throws DAOException
     */
    void removeProcess(Integer processId) throws DAOException;

    /**
     * @param actTemplId
     * @param parentPtId
     * @throws DAOException
     */
    void removeTransitions(Integer actTemplId, Integer parentPtId)
            throws DAOException;

    /**
     * @param actTemplId
     * @param actProcessId
     * @throws DAOException
     */
    void removeProcessActivity(Integer actTemplId, Integer actProcessId,
            Integer actLoopCnt) throws DAOException;

    /**
     * @param actTemplId
     * @throws DAOException
     */
    void removeActivityTemplate(Integer actTemplId) throws DAOException;

    /**
     * @param subSystem
     * @param includeDeprecated
     * @param includeDynamic
     * @return
     * @thows DAOException
     */
    ProcessTemplate[] retrieveProcessTemplatesByType(String subSystem,
            boolean includeDeprecated, boolean includeDynamic)
            throws DAOException;

    /**
     * @param beginDate
     * @param endDate
     * @param subSystem
     * @return
     * @throws DAOException
     */
    ProcessActivity[] retrieveProcessActivityByDate(Timestamp beginDate,
            Timestamp endDate, String subSystem) throws DAOException;

    /**
     * Returns all ControllerConfig objects from the database.
     * 
     * @return ControllerConfig[] All ControllerConfig objects.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ControllerConfig[] retrieveAllControllerConfigs() throws DAOException;

    /**
     * Returns all controller configuration objects for a specific object type
     * (currently "Activity" or "ProcessFlow").
     * 
     * @param objectType
     *            String The name of the object type.
     * 
     * @return ControllerConfig[] All ControllerConfig objects for that type.
     * 
     * @throws DAOException
     *             Database access error.
     */
    ControllerConfig[] retrieveControllerConfigs(String objectType)
            throws DAOException;

    /**
     * Returns all of the object types from the database table containing
     * controller configuration information. Each object type String may occur
     * multiple times in the database, but is only returned once by this method.
     * 
     * @return String[] Array of object type names.
     * 
     * @throws DAOException
     *             Database access error.
     */
    String[] retrieveObjectTypes() throws DAOException;

    /**
     * @TODO Add Javadoc comment for retrieveServiceDetailDefBases(Integer
     *       atdId)
     * @param atdId
     * @return
     * @throws DAOException
     */
    DataDetailBase[] retrieveServiceDetailDefBases(Integer atdId)
            throws DAOException;

    /**
     * Returns contents of ActivityStatusDef Reference table
     * 
     * @return SimpleDef[] Array of ActivityStatusDef objects.
     * 
     * @throws DAOException
     *             Database access error.
     */
    SimpleDef[] retrieveActivityStatusDef() throws DAOException;

    /**
     * @see WorkFlowDAO#retrieveActivityTypesDef()
     */
    ActivityTemplateDef[] retrieveActivityTypes() throws DAOException;

    /**
     * Returns contents of ActivityStatusDef Reference table
     * 
     * @return SimpleDef[] Array of ActivityStatusDef objects.
     * 
     * @throws DAOException
     *             Database access error.
     */
    SimpleDef[] retrieveToDoStatusDef() throws DAOException;

    /**
     * 
     * Returns contents of ProcessDef Reference table
     * 
     * @return ProcessDef[] Array of ProcessDef objects.
     * 
     * @throws DAOException
     *             Database access error.
     */
    SimpleDef[] retrieveProcessDefs() throws DAOException;

    /**
     * 
     * Returns contents of PerformerDef Reference table
     * 
     * @return PerformerDef[] Array of ProcessDef objects.
     * 
     * @throws DAOException
     *             Database access error.
     */
    PerformerDef[] retrievePerformerDefs() throws DAOException;

    /**
     * Returns contents of TransitionDef Reference table
     * 
     * @return TransitionDef[] Array of TransitionDef objects.
     * 
     * @throws DAOException
     *             Database access error.
     */
    TransitionDef[] retrieveTransitionDef() throws DAOException;

    /**
     * Returns all of the <tt>ProcessActivity</tt> objects
     * 
     * @param pa -
     *            ProcessActivity
     * 
     * @return ProcessActivity[] All ProcessActivities in Workinglist.
     * 
     * @throws DAOException
     *             Database access error.
     */

    ProcessActivity[] retrieveActivityList(ProcessActivity pa)
            throws DAOException;

    /**
     * Create <tt>ProcessData</tt> objects
     * 
     * @param pds -
     *            ProcessData
     * 
     * @return
     * 
     * @throws DAOException
     *             Database access error.
     */
    void createProcessDatas(ProcessData[] pds) throws DAOException;

    /**
     * Returns all of the <tt>WorkFlowProcess</tt> objects
     * 
     * @param wp -
     *            WorkFlowProcess
     * 
     * @return WorkFlowProcess[] All WorkFlowProcess.
     * @throws DAOException
     *             Database access error.
     */
    WorkFlowProcess[] retrieveProcessList(WorkFlowProcess wp)
            throws DAOException;

    /**
     * @param processId
     * @return
     * @throws DAOException
     */
    WorkFlowProcess[] retrieveChildrenProcess(Integer processId)
            throws DAOException;

    /**
     * @param processId
     * @throws DAOException
     */
    void removeProcessData(Integer processId) throws DAOException;

    /**
     * @param fpId
     * @param facilityRoleCd
     * @return
     * @throws DAOException
     */
    Integer[] retrieveUserIdsOfFacility(Integer fpId, String facilityRoleCd)
            throws DAOException;

    /**
     * @param pn
     * @throws DAOException
     */
    void createProcessNote(ProcessNote pn) throws DAOException;

    /**
     * @param pn
     * @return
     * @throws DAOException
     */
    ProcessNote[] retrieveProcessNotes(ProcessNote pn) throws DAOException;

    /**
     * @param pt
     * @param at
     * @return
     * @throws DAOException
     */
    boolean updateInPlaceActivityTemplate(ProcessTemplate pt,
            ActivityTemplate at) throws DAOException;

    /**
     * @param pa
     * @return
     * @throws DAOException
     */
    ProcessActivity modifyProcessActivityViewedState(ProcessActivity pa)
            throws DAOException;

    /**
     * @return
     * @throws DAOException
     */
    ActivityReferralType[] retrieveActivityReferralTypes() throws DAOException;

    WorkFlowProcess[] retrieveParentActiveProcessFlow(Integer parentProcessId) throws DAOException;

    boolean modifyProcessNote(ProcessNote pn) throws DAOException;

    void removeProcessNotes(Integer processId) throws DAOException;

    ProcessActivityLight[] retrieveActivityListLight(ProcessActivity pa) throws DAOException;

    ProcessActivity retrieveActivity(ProcessActivity pa) throws DAOException;

    ProcessActivity modifyProcessActivityEndDate(ProcessActivity pa) throws DAOException;
    
    ProcessActivity[] retrieveActivitiesToUnDeadend(int permitId) throws DAOException ;

	Map<Integer, String> retrievePermittingActivityTypes() throws DAOException;
}
