package us.oh.state.epa.stars2.bo;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.DAOManager;
import us.oh.state.epa.stars2.database.dao.DetailDataDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.WorkFlowDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityReferralType;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ControllerConfig;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.PerformerDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivityLight;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessDataTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.workflow.Activity;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;

@Transactional(rollbackFor=Exception.class)
@Service
public class ReadWorkFlowBO //extends BaseBO 
	implements ReadWorkFlowService  {

	private Logger logger = Logger.getLogger(ReadWorkFlowBO.class);
	
	@Autowired DAOManager daoManager;
	
    private DetailDataDAO detailDataDAO() {
    	return detailDataDAO(null);
    }

    private DetailDataDAO detailDataDAO(Transaction trans) {
    	return (DetailDataDAO)daoManager.getDAO(DetailDataDAO.class, trans);
    }

    private WorkFlowDAO workFlowDAO() {
    	return workFlowDAO(null);
    }
    
    private WorkFlowDAO workFlowDAO(Transaction trans) {
    	return (WorkFlowDAO)daoManager.getDAO(WorkFlowDAO.class, trans);
    }
    
    /**
     * Returns all available ServiceDetailDef objects from the database. Returns
     * "null" if no objects are found. Note that the returned object is the
     * workflow designer's "bizObj" version of ServiceDetailDef.
     * 
     * @return ServiceDetailDef[] All available objects from the database.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public DataDetail[] retrieveDataDetails() throws DAOException {
        DetailDataDAO ddDao = detailDataDAO();

        // Temp - May not need this - talk to KEN
        DataDetail[] details = ddDao.retrieveDataDetails();

        for (int ii = 0; ii < details.length; ii++) {
            details[ii] = ddDao.retrieveDataDetail(details[ii]
                    .getDataDetailId());
        }

        return details;
    }

    /**
     * Returns all of the <code>TransitionDef</code> objects currently defined
     * in the system. If no transition defs are found, then an empty array is
     * returned.
     * 
     * @return TransitionDef[] All transition details in the system.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public TransitionDef[] retrieveTransitionDef() throws DAOException {
        return workFlowDAO().retrieveTransitionDef();
    }
    
    /**
     * Returns all of the <code>ActivityStatusDef</code> objects currently
     * defined in the system. If no ActivityStatus defs are found, then an empty
     * array is returned.
     * 
     * @return ActivityStatusDef[] All ActivityStatus details in the system.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public SimpleDef[] retrieveActivityStatusDef() throws DAOException {
        return workFlowDAO().retrieveActivityStatusDef();
    }

    /**
     * Returns subset of the <code>ActivityStatusDef</code> objects currently
     * defined in the system. If no ActivityStatus defs are found, then an empty
     * array is returned.
     * 
     * @return ActivityStatusDef[] All ActivityStatus details in the system.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public SimpleDef[] retrieveToDoStatusDef() throws DAOException {
        return workFlowDAO().retrieveToDoStatusDef();
    }

    /**
     * Returns all of the <code>ActivityStatusDef</code> objects currently
     * defined in the system. If no ActivityStatus defs are found, then an empty
     * array is returned.
     * 
     * @return ActivityStatusDef[] All ActivityStatus details in the system.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public SimpleDef[] retrieveProcessDefs() throws DAOException {
        return workFlowDAO().retrieveProcessDefs();
    }

    /**
     * Returns all of the <code>ActivityStatusDef</code> objects currently
     * defined in the system. If no ActivityStatus defs are found, then an empty
     * array is returned.
     * 
     * @return ActivityStatusDef[] All ActivityStatus details in the system.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public PerformerDef[] retrievePerformerDefs() throws DAOException {
        return workFlowDAO().retrievePerformerDefs();
    }

    /**
     * Returns all of the <code>ActivityStatusDef</code> objects currently
     * defined in the system. If no ActivityStatus defs are found, then an empty
     * array is returned.
     * 
     * @return ActivityStatusDef[] All ActivityStatus details in the system.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ActivityTemplateDef[] retrieveActivityTypes() throws DAOException {
        return workFlowDAO().retrieveActivityTypes();
    }

    /**
     * Returns all currently active ProcessTemplates from the database. These
     * correspond to workflows that are currently available to the rest of the
     * system.
     * 
     * @return ProcessTemplate[] All currently active (and therefore editable)
     *         ProcessTemplates.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessTemplate[] retrieveTemplateData() throws DAOException {
        return workFlowDAO().retrieveProcessTemplates(false, false);
    }

    /**
     * Returns all of the currently active ActivityTemplateDef objects from the
     * database for "templateCd", i.e., provisioning ActivityTemplateDef
     * objects, sales flow ActivityTemplateDef objects, etc.
     * 
     * @param templateCd
     *            The kind of process template being edited.
     * 
     * @return ActivityTemplateDef[] All active activeActivityTemplateDef
     *         objects that support that kind of process template.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ActivityTemplateDef[] retrieveActivityTemplateDefs(String templateCd)
            throws DAOException {
        return workFlowDAO().retrieveActivityTemplateDefs(templateCd);
    }

    /**
     * Retrieves all of the sub-flows for the given process code. The process
     * code is either "prov" (normal provisioning) or "sfa" (sales flow).
     * Returns null or empty array if no sub-flows were found.
     * 
     * @param processCd
     *            Process code for provisioning or sales flow.
     * 
     * @return An array of ActivityTemplateDef objects, one per sub-flow.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ActivityTemplateDef[] retrieveSubFlowsByProcessCd(String processCd)
            throws DAOException {
        return workFlowDAO().retrieveSubFlowsByProcessCd(processCd);
    }

    /**
     * Returns all of the Activity objects currently associated with
     * "processTemplateId" in the database.
     * 
     * @param processTemplateId
     *            Unique database key value.
     * 
     * @return Activity[] All of the process template's Activity objects.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Activity[] retrieveActivities(Integer processTemplateId)
            throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO();

        // The object we are returning is actually composed of two database
        // objects, an ActivityTemplate and an ActivityTemplateDef. Retrieve
        // the ActivityTemplates first since it has the processTemplateId
        // foreign key we need to select the Activities.
        ActivityTemplate[] ats = wfDao
                .retrieveActivityTemplatesForProcessTemplate(processTemplateId);

        // If this process has no ActivityTemplates, we are done.
        ArrayList<Activity> results = new ArrayList<Activity>();

        if ((ats != null) && (ats.length != 0)) {
            ActivityTemplateDef atd;

            // For (each ActivityTemplate we have) retrieve its corresponding
            // ActivityTemplateDef object. Use both objects to construct the
            // Activity.

            for (ActivityTemplate a : ats) {
                Integer atdId = a.getActivityTemplateDefId();
                atd = wfDao.retrieveActivityTemplateDef(atdId);
                results.add(new Activity(a, atd));
            }
        }

        return results.toArray(new Activity[0]);
    }

    /**
     * Returns all of the Transitions, i.e., the links between Activities,
     * defined for a ProcessTemplate from the database.
     * 
     * @param processTemplateId
     *            Integer key value.
     * 
     * @return Transition[] All of the Transitions identified for this process.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Transition[] retrieveTransitions(Integer processTemplateId)
            throws DAOException {
        return workFlowDAO().retrieveTransitions(processTemplateId, false);
    }

    /**
     * Returns all process data templates from the database for the given
     * process template Id.
     * 
     * @param processTemplateId
     *            process template Id.
     * 
     * @return ProcessDataTemplate[] Process-specific service detail data.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessDataTemplate[] retrieveProcessTemplateData(
            Integer processTemplateId) throws DAOException {
        return workFlowDAO().retrieveProcessDataTemplates(processTemplateId);
    }

    /**
     * Returns an array of Activity objects based on a "processTemplateId". This
     * means that all of the template components are present in the Activity,
     * but no Process-specific information will be included. Returns "null" if
     * the "processTemplateId" is not found in the database.
     * 
     * @param container
     *            The ProcessFlow that will contain these Activities.
     * 
     * @return Activity[] An array of partially specified Activity objects.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Activity[] retrieveTemplateActivities(ProcessFlow container)
            throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO();

        // What we are going to do here is retrieve the component parts and
        // then build the array of Activity objects from the components.
        //
        // First, get the ActivityTemplates.
        ActivityTemplate[] ats = wfDao
                .retrieveActivityTemplatesForProcessTemplate(container
                        .getProcessTemplateId());

        ArrayList<Activity> activities = new ArrayList<Activity>();
        if ((ats != null) && (ats.length != 0)) {
            for (ActivityTemplate at : ats) {
                ActivityTemplateDef atd = wfDao.retrieveActivityTemplateDef(at
                        .getActivityTemplateDefId());

                activities.add(new Activity(container, new ProcessActivity(),
                        at, atd));
            }
        }

        return activities.toArray(new Activity[0]);
    }

    /**
     * Looks up an ActivityTemplateDef object given its activity template def
     * Id. I did it this way to avoid using the same DAO connection to do
     * database reads and inserts/updates (Postgres seems to have a problem with
     * this if you do it too much). If no matching object is found in the
     * database, "null" is returned.
     * 
     * @param atdId
     *            activity template def Id.
     * 
     * @return ActivityTemplateDef.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ActivityTemplateDef retrieveActivityTemplateDef(Integer atdId)
            throws DAOException {
        return workFlowDAO().retrieveActivityTemplateDef(atdId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public WorkFlowProcess retrieveProcess(Integer processId)
            throws DAOException {
        return workFlowDAO().retrieveProcess(processId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessActivity[] retrieveProcessActivities(Integer processId)
            throws DAOException {
        return workFlowDAO().retrieveProcessActivities(processId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ActivityTemplate retrieveActivityTemplate(Integer actId)
            throws DAOException {
        return workFlowDAO().retrieveActivityTemplate(actId);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ActivityTemplate[] retrieveActivityTemplates()
            throws DAOException {
        return workFlowDAO().retrieveActivityTemplates();
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Transition[] retrieveTransitions(Integer processTemplateId,
            boolean includeAll) throws DAOException {
        return workFlowDAO().retrieveTransitions(processTemplateId, includeAll);
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public WorkFlowProcess retrieveSubFlowProcess(Integer processId,
            Integer actTemplId) throws DAOException {
        return workFlowDAO().retrieveSubFlowProcess(processId, actTemplId);
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessActivity[] retrieveActivityList(ProcessActivity pa)
            throws DAOException {
        return workFlowDAO().retrieveActivityList(pa);
    }
    
    public ProcessActivity[] retrieveActivityListForFacilities(ProcessActivity pa, String[] facilities, Integer userIdForSearch)
            throws DAOException {
        return workFlowDAO().retrieveActivityListForFacilities(pa, facilities, userIdForSearch);
    }
    
    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessActivity[] retrieveActivitiesToUnDeadend(int permitId)
            throws DAOException {
        return workFlowDAO().retrieveActivitiesToUnDeadend(permitId);
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessActivity retrieveActivity(ProcessActivity pa)
            throws DAOException {
        return workFlowDAO().retrieveActivity(pa);
    }
    
    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessActivityLight[] retrieveActivityListLight(ProcessActivity pa)
            throws DAOException {
    	ProcessActivityLight[] activities = 
    			workFlowDAO().retrieveActivityListLight(pa);
    	return activities;
    } 

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public LinkedHashMap<String, Integer> retrieveWorkflowTempIdAndNm()
            throws DAOException {
    	return retrieveWorkflowTempIdAndNm(null);
    }
    
    public LinkedHashMap<String, Integer> retrieveWorkflowTempIdAndNm(Transaction trans)
            throws DAOException {
        SimpleIdDef[] sid = workFlowDAO(trans).retrieveWorkflowTempIdAndNm();

        LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
        for (SimpleIdDef temp : sid) {
            ret.put(temp.getDescription(), temp.getId());
        }

        return ret;
    }    
    
    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public LinkedHashMap<String, String> retrieveProcessTemplatesByType(String type)
            throws DAOException {
        ProcessTemplate[] pts = workFlowDAO().retrieveProcessTemplatesByType(type, false, false);

        LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        for (ProcessTemplate temp : pts) {
            ret.put(temp.getProcessTemplateNm(), temp.getProcessTemplateNm());
        }

        return ret;
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public SimpleIdDef[] retrieveProcessGroupByType() throws DAOException {
        return workFlowDAO().retrieveProcessGroupByType();
    }


    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public WorkFlowProcess[] retrieveProcessList(WorkFlowProcess wp)
            throws DAOException {
        return workFlowDAO().retrieveProcessList(wp);
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public LinkedHashMap<String, String> getStateDef() {
        LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
        temp.put(WorkFlowProcess.STATE_COMPLETED_DESC,
                WorkFlowProcess.STATE_COMPLETED_CD);
        temp.put(WorkFlowProcess.STATE_IN_PROCESS_DESC,
                WorkFlowProcess.STATE_IN_PROCESS_CD);
        temp.put(WorkFlowProcess.STATE_CANCELLED_DESC,
                WorkFlowProcess.STATE_CANCELLED_CD);
        return temp;
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public LinkedHashMap<String, String> getStatusDef() {
        LinkedHashMap<String, String> temp = new LinkedHashMap<String, String>();
        temp.put(WorkFlowProcess.STATUS_OK_DESC, WorkFlowProcess.STATUS_OK_CD);
        temp.put(WorkFlowProcess.STATUS_JEOPARDY_DESC,
                WorkFlowProcess.STATUS_JEOPARDY_CD);
        temp.put(WorkFlowProcess.STATUS_LATE_DESC,
                WorkFlowProcess.STATUS_LATE_CD);
        return temp;
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public DataField[] retrieveDataFieldsForProcess(Integer processId)
            throws DAOException {
        return workFlowDAO().retrieveDataFieldsForProcess(processId);
    }


    /**
     * Retrieve users of the facility.
     * 
     * @param el
     *            EventLog The object to filter.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Integer[] retrieveUserIdsOfFacility(Integer fpId,
            String facilityRoleCd) throws DAOException {
        return workFlowDAO().retrieveUserIdsOfFacility(fpId, facilityRoleCd);
    }

    /**
     * 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessNote[] retrieveProcessNotes(ProcessNote pn)
            throws DAOException {
        return workFlowDAO().retrieveProcessNotes(pn);
    }

    /**
     * Returns the active ProcessFlow of processId from the database. These are
     * ProcessFlows with a "null" end date, meaning that they have not yet been
     * completed. Note that the return is simply an array of validated
     * ProcessFlows. Parent or subflow relationships among the returned
     * ProcessFlow objects are not established or "connected" by this method.
     * 
     * @return ProcessFlow Currently active ProcessFlow of processId.
     * 
     * @throws DAOException
     *             Database access error or invalid ProcessFlow.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessFlow retrieveActiveProcessFlow(Integer processId)
            throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO();

        // Use the DAO to retrieve all active "Process" objects. If we don't
        // have any, we are done.

        WorkFlowProcess p = wfDao.retrieveProcess(processId);

        ProcessFlow pf = buildProcessFlow(p, wfDao);

        return pf;
    }
    
    /**
     * 
     * 
     * @return ProcessFlow Currently active ProcessFlow of processId.
     * 
     * @throws DAOException
     *             Database access error or invalid ProcessFlow.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessFlow[] retrieveParentActiveProcessFlow(Integer parentProcessId)
            throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO();
        List<ProcessFlow> ps = new ArrayList<ProcessFlow>();
        // Use the DAO to retrieve all active "Process" objects. If we don't
        // have any, we are done.
        WorkFlowProcess[] wps = wfDao
                .retrieveParentActiveProcessFlow(parentProcessId);
        for (WorkFlowProcess wp : wps) {
            ProcessFlow pf = buildProcessFlow(wp, wfDao);
            ps.add(pf);
        }

        return ps.toArray(new ProcessFlow[0]);
    }

    private ProcessFlow buildProcessFlow(WorkFlowProcess p, WorkFlowDAO wfDao) throws DAOException {
        ProcessFlow pf = null;
        if ((p != null) && (p.getEndDt() == null)) {
            pf = new ProcessFlow(p);
            Integer processTemplateId = p.getProcessTemplateId();

            Activity[] pfActs = retrieveActivitiesForProcess(pf);
            Transition[] pfTrans = wfDao.retrieveTransitions(processTemplateId,
                    true);

            try {
                DataField[] pfFields = wfDao.retrieveDataFieldsForProcess(pf
                        .getProcessId());

                pf.addActivities(pfActs);
                pf.addDataFields(pfFields);
                // debugPrint(pfFields); // Dennis Reza debugging
                pf.addTransitions(pfTrans);

                pf.validate();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return pf;
    }


    /**
     * Returns an array of Activity objects for a "processId". This includes
     * both template and process-specific information. If the process Id is not
     * found in the database, then "null" is returned.
     * 
     * @param pf
     *            Parent ProcessFlow.
     * 
     * @return Activity[] An array of fully-specified Activity objects.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    private Activity[] retrieveActivitiesForProcess(ProcessFlow pf)
            throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO();

        // The Activity object is a composite of three components. We
        // can use the "processId" to get the first component. From that,
        // we extract the activity template Id to get the ActivityTemplate.
        // From that, we can get the activity template def Id to get the
        // ActivityTemplateDef.

        Integer processId = pf.getProcessId();
        ProcessActivity[] paList = wfDao.retrieveProcessActivities(processId);
        ArrayList<Activity> actvities = new ArrayList<Activity>();

        if ((paList != null) && (paList.length != 0)) {
            ActivityTemplate at;
            ActivityTemplateDef atDef;

            for (ProcessActivity pa : paList) {
                at = wfDao.retrieveActivityTemplate(pa.getActivityTemplateId());

                atDef = wfDao.retrieveActivityTemplateDef(at
                        .getActivityTemplateDefId());

                actvities.add(new Activity(pf, pa, at, atDef));
            }
        }

        return actvities.toArray(new Activity[0]);
    }

    /**
     * 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ActivityReferralType[] retrieveActivityReferralTypes()
            throws DAOException {
        return workFlowDAO().retrieveActivityReferralTypes();
    }
    /**
     * 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessTemplate retrieveProcessTemplateForComposite(String type,
            Integer id) throws DAOException {
        return workFlowDAO().retrieveProcessTemplateForComposite(type, id);
    }
    
    /**
     * 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ControllerConfig[] retrieveAllControllerConfigs() throws DAOException {
        return workFlowDAO().retrieveAllControllerConfigs();
    }
    
    /**
     * 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessTemplate retrieveProcessTemplate(int ptId) throws DAOException {
        return workFlowDAO().retrieveProcessTemplate(ptId);
    }
    
    /**
     * 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ProcessTemplate retrieveProcessTemplate(int ptId, Transaction trans) throws DAOException {
        return workFlowDAO(trans).retrieveProcessTemplate(ptId);
    }
    
    /**
     * 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public ActivityTemplateDef retrieveSubFlowDef(String subFlowName) throws DAOException {
        return workFlowDAO().retrieveSubFlowDef(subFlowName);
    }

    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public String getParameter(String parm) {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        Node root = cfgMgr.getNode("app.params." + parm);
        if (root == null) {
            logger.error("Unable to find parameter app.params." + parm);
            return null;
        }
        String value = root.getAsString("value");

        return value;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public String getParameterValue(String parameterName, String defaultValue) {
        String result = defaultValue;
        String strValue = getParameter(parameterName);
        if (strValue != null) {
            result = strValue;
        }
        return result;
      }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public Integer getParameterValue(String parameterName, Integer defaultValue) {
        Integer result = defaultValue;
        String strValue = getParameter(parameterName);
        if (strValue != null) {
            try {
                result = Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                logger.error("Invalid value specified for parameter '" 
                        + parameterName + "' defaulting to " + defaultValue);
            }
        }
        return result;
    }

	@Override
	public Map<Integer, String> retrievePermittingActivityTypes() throws DAOException {
        return workFlowDAO().retrievePermittingActivityTypes();
	}

	@Override
	public Integer findActiveProcessByExternalObject(Integer processTemplateId, 
			Integer externalId)
			throws DAOException {
        return workFlowDAO().findActiveProcessByExternalObject(
        		processTemplateId, externalId);
	}

	
    public ProcessFlow retrieveProcessFlow(Integer processId)
            throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO();
        WorkFlowProcess p = wfDao.retrieveProcess(processId);
        ProcessFlow pf = null;
        if (p != null) {
            pf = new ProcessFlow(p);
            Integer processTemplateId = p.getProcessTemplateId();

            Activity[] pfActs = retrieveActivitiesForProcess(pf);
            Transition[] pfTrans = wfDao.retrieveTransitions(processTemplateId,
                    true);

            try {
                DataField[] pfFields = wfDao.retrieveDataFieldsForProcess(pf
                        .getProcessId());

                pf.addActivities(pfActs);
                pf.addDataFields(pfFields);
                pf.addTransitions(pfTrans);

                pf.validate();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return pf;
    }
}
