package us.oh.state.epa.stars2.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.DAOManager;
import us.oh.state.epa.stars2.database.dao.DetailDataDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
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
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessData;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessDataTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;
import us.oh.state.epa.stars2.scheduler.Stars2Scheduler;
import us.oh.state.epa.stars2.workflow.Activity;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;
import us.oh.state.epa.stars2.workflow.engine.ServiceTemplateMap;
import us.oh.state.epa.stars2.workflow.engine.WorkFlowEngine;
import us.oh.state.epa.stars2.workflow.util.WorkFlowUtils;
import us.wy.state.deq.impact.bo.WriteWorkFlowService;

/**
 * <p>
 * Title: WorkFlowBO
 * </p>
 * 
 * <p>
 * Description: This is the Business Object for WorkFlow.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author kbradley
 * @version 1.0
 */
@Transactional(rollbackFor=Exception.class)
//@Service 
// NOTICE!! Do not annotate as an @Service since its interface is not always 
// present on the classpath (depending on the component it is packaged in); 
// must be explicitly defined in the xml config, but only in applications that
// include the interface
public class WriteWorkFlowBO //extends BaseBO 
	implements WriteWorkFlowService {
	
	private Logger logger = Logger.getLogger(WriteWorkFlowBO.class);
	
    public static final String recurringClass = "us.oh.state.epa.stars2.scheduler.jobs.WorkflowReferralJob";
    
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
     * Creates a new ServiceDetailDef object in the database.
     * 
     * @param sdd
     *            ServiceDetailDef The object to be created.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void createServiceDetailDef(DataDetail cdtd) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        DetailDataDAO ddDao = detailDataDAO(trans);

        // This thing is actually made up of a couple of different parts.
        try {
            ddDao.createDataDetail(cdtd);

            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }
        return;
    }

    private void cancelTransaction(Transaction trans, DAOException de) throws DAOException {
        logger.error(de.getMessage(), de);
        throw de;
	}

	private void closeTransaction(Transaction trans) {
//      if (trans != null) {
//      trans.close();
//  }
	}

	/**
     * Updates an existing ServiceDetailDef object in the database.
     * 
     * @param sdd
     *            ServiceDetailDef The object to be updated.
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void updateServiceDetailDef(DataDetail cdtd) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        DetailDataDAO ddDao = detailDataDAO(trans);

        try {
            ddDao.modifyDataDetail(cdtd);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }
        return;
    }

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
     * 
     * @return boolean "True" if the update was successful.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean createProcessTemplate(ProcessTemplate pt,
            Activity[] activities, Transition[] transitions,
            ProcessDataTemplate[] detailData) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO wfDao = workFlowDAO(trans);
        boolean result = false;

        try {
            // Create the new workflow.
            result = createProcessTemplate(pt, null, activities, transitions,
                    detailData, wfDao);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return result;
    }

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
     * 
     * @return boolean "True" if the update was successful.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean updateProcessTemplate(ProcessTemplate pt,
            Activity[] activities, Transition[] transitions,
            ProcessDataTemplate[] detailData) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO wfDao = workFlowDAO(trans);
        boolean result = false;

        try {
            // If this process template is currently assigned to any workflows,
            // we need to update those workflows to "point" to the new version
            // of the template. Retrieve the object that does this "pointing".
            // If this object exists, we will "point" it to the new process
            // template.
            ActivityTemplateDef subflowDef = wfDao
                    .retrieveSubflowActivityTemplateDef(pt
                            .getProcessTemplateId(), pt.getProcessTemplateNm());

            // Deprecate any previous version of the workflow.
            deprecateProcessTemplate(pt, wfDao);

            // Create a brand new workflow.
            result = createProcessTemplate(pt, subflowDef, activities,
                    transitions, detailData, wfDao);

            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return result;
    }

    /**
     * Updates a workflow that has minimal changes in place. The changes are
     * limited to the position activities that are displayed in the workflow
     * Java 2D image.
     * 
     * @param pt
     *            ProcessTemplate for the workflow.
     * @param activities
     *            The Activities in the workflow.
     * 
     * @return boolean "True" if the update was successful.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean updateInPlaceActivityTemplate(ProcessTemplate pt,
            Activity[] activities) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO wfDao = workFlowDAO(trans);
        boolean result = false;

        try {
            for (Activity a : activities) {
                ActivityTemplate at = a.getActivityTemplate();
                result = wfDao.updateInPlaceActivityTemplate(pt, at);
            }

            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return result;
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
     * Creates an entire array of <tt>Transition</tt> objects in the
     * underlying database. See "createTransition()" for specific details.
     * 
     * @param transArray
     *            Array of Transition objects.
     * 
     * @throws DAOException
     *             Database access error.
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void createTransitions(Transition[] transArray) throws DAOException {
        if ((transArray != null) && (transArray.length != 0)) {
            Transaction trans = TransactionFactory.createTransaction();
            WorkFlowDAO workflowDAO = workFlowDAO(trans);

            workflowDAO.setTransaction(trans);

            try {
                createTransitions(transArray, workflowDAO);
                trans.complete();
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }
        return;
    }

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
     * 
     * @return ProcessFlow A new ProcessFlow containing a full template and
     *         "subflow".
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessFlow createSplitProcessFlow(String type, Integer newOrderId,
            Activity oldParentAct, ProcessFlow oldParentProc,
            ProcessFlow subflow) throws DAOException {
        ProcessFlow newPf = null;
        WorkFlowDAO wfDao = null;
        Transaction trans = TransactionFactory.createTransaction();

        // Extract some values we are going to need in a moment.

        Integer oldPtId = oldParentProc.getProcessTemplateId();
        Integer subflowPtId = subflow.getProcessTemplateId();

        Integer accountId = oldParentProc.getAccountId();
        Integer serviceId = oldParentAct.getServiceId();

        try {
            // We need the ProcessTemplate for the sub-flow. Read it in first
            // and then switch the DAO over to external transaction control.
            // We will pass the DAO into any method that needs to store stuff
            // in the database.

            wfDao = workFlowDAO();
            ProcessTemplate splitPt = wfDao
                    .retrieveProcessTemplate(subflowPtId);

            wfDao.setTransaction(trans);

            // Create a new ProcessTemplate for the new ProcessFlow. The
            // new template will have a "place holder" ActivityTemplate that
            // we will use in a moment.

            ProcessTemplate newPt = createSplitProcessTemplate(type,
                    newOrderId, wfDao);

            // Copy the expected and jeopardy durations for the new template
            // from the sub-flow template.
            newPt.setExpectedDuration(splitPt.getExpectedDuration());
            newPt.setJeopardyDuration(splitPt.getJeopardyDuration());

            newPt = wfDao.modifyProcessTemplate(newPt);

            Integer newPtId = newPt.getProcessTemplateId();

            // Find the "place holder" ActivityTemplate and its Id.

            ActivityTemplate placeHolder = null;
            ActivityTemplate[] ats = newPt.getActivityTemplate();

            if ((ats != null) && (ats.length != 0)) {
                // For (each ActivityTemplate we have) see if it the "place
                // holder".
                // If we find it, return it.
                for (ActivityTemplate at : ats) {
                    String atdName = at.getActivityTemplateNm();

                    if (atdName.equals("PlaceHolder")) {
                        placeHolder = at;
                        break;
                    }
                }
            }

            Integer placeHolderATID = null;

            if (placeHolder != null) {
                placeHolderATID = placeHolder.getActivityTemplateId();
            }

            // Copy the state, location, etc., from the old parent Activity
            // template to the "place holder" template.

            copyActivityTemplate(oldParentAct.getActivityTemplate(),
                    placeHolder, wfDao);

            // Create a ProcessFlow for the new template.

            newPf = createSplitProcessFlow(newPt, newOrderId, accountId,
                    serviceId, wfDao);

            Integer processId = newPf.getProcessId();

            // Copy any DataFields in the original process to the new process.

            copyDataFields(oldPtId, newPtId, processId, wfDao);

            // Find the "place holder" Activity in the new ProcessFlow.
            // Re-parent "subflow" to the new ProcessFlow and place holder
            // Activity.

            Activity newParentAct = null;

            if (placeHolderATID != null) {
                Activity[] activities = newPf.getActivities();
                if ((activities != null) && (activities.length != 0)) {
                    // For (each Activity in the process) see if its activity
                    // template
                    // Id matches the one we want. If so, return it.

                    for (Activity srch : activities) {
                        if (placeHolderATID
                                .equals(srch.getActivityTemplateId())) {
                            newParentAct = srch;
                            break;
                        }
                    }
                }
            }
            // Perform "database surgery" to remove the subflow from its
            // original parent Activity and ProcessFlow. Move it to the
            // new parent Activity and ProcessFlow.

            moveActivity(subflow, newParentAct, newPf, newOrderId, wfDao);

            // Remove the old parent Activity from the old ProcessFlow because
            // it now no longer has a subflow and is therefore useless.

            oldParentProc.removeActivity(oldParentAct);

            // Recompute the geometry, i.e., box positions, of the original
            // ProcessFlow (minus the Activity we just took out of it) and the
            // new ProcessFlow.

            updateGeometry(oldParentProc, wfDao);
            updateGeometry(newPf, wfDao);

            trans.complete(); // We be done ...
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return newPf;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessTemplate createProcessTemplate(ProcessTemplate pt)
            throws DAOException {
        if (pt != null) {
            Transaction trans = TransactionFactory.createTransaction();
            WorkFlowDAO workflowDAO = workFlowDAO(trans);

            workflowDAO.setTransaction(trans);
            try {
                pt = createProcessTemplate(pt, workflowDAO);

                trans.complete();
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }
        return pt;
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
     * Returns all of the active ProcessFlows from the database. These are
     * ProcessFlows with a "null" end date, meaning that they have not yet been
     * completed. Note that the return is simply an array of validated
     * ProcessFlows. Parent or subflow relationships among the returned
     * ProcessFlow objects are not established or "connected" by this method.
     * 
     * @return ProcessFlow[] All currently active ProcessFlows.
     * 
     * @throws DAOException
     *             Database access error or invalid ProcessFlow.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    /*
    This should never use yehp
    public ProcessFlow[] retrieveActiveProcessFlows(Transaction trans) throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO(trans);

        // Use the DAO to retrieve all active "Process" objects. If we don't
        // have any, we are done.
        WorkFlowProcess[] activeProcesses = wfDao.retrieveInWorkProcesses();

        // If we have nothing, return nothing.
        ArrayList<ProcessFlow> pfs = new ArrayList<ProcessFlow>();

        if ((activeProcesses != null) && (activeProcesses.length != 0)) {
            // FOR (each active Process) create a ProcessFlow for it. Then
            // retrieve the "guts" of the ProcessFlow and assign them to the
            // process.
            for (WorkFlowProcess p : activeProcesses) {
                ProcessFlow pf = new ProcessFlow(p);

                Integer processTemplateId = p.getProcessTemplateId();

                Activity[] pfActs = retrieveActivitiesForProcess(pf);
                Transition[] pfTrans = wfDao.retrieveTransitions(
                        processTemplateId, true);

                try {
                    DataField[] pfFields = wfDao
                            .retrieveDataFieldsForProcess(pf.getProcessId());

                    pf.addActivities(pfActs);
                    pf.addDataFields(pfFields);
                    pf.addTransitions(pfTrans);

                    pf.validate();
                } catch (Exception e) {
                    logger.error(e);
                }

                pfs.add(pf);
            }
        }

        return pfs.toArray(new ProcessFlow[0]);
    }
    */

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
     * @ejb.transaction type="Required"
     */
    public boolean modifyProcessDataTemplate(ProcessDataTemplate ptd)
            throws DAOException {
        boolean ret = false;

        if (ptd != null) {
            Transaction trans = TransactionFactory.createTransaction();
            WorkFlowDAO workflowDAO = workFlowDAO(trans);
            try {
                ptd = workflowDAO.modifyProcessDataTemplate(ptd);

                workflowDAO.createProcessDataTemplate(ptd);

                trans.complete();
                ret = true;
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }
        return ret;
    }

    /**
     * @param atd
     * @return ActivityTemplateDef
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ActivityTemplateDef createActivityTemplateDef(ActivityTemplateDef atd)
            throws DAOException {
        ActivityTemplateDef ret = null;

        if (atd != null) {
            Transaction trans = TransactionFactory.createTransaction();
            WorkFlowDAO workflowDAO = workFlowDAO(trans);
            try {
                ret = workflowDAO.createActivityTemplateDef(atd);

                workflowDAO.createDependentDetailData(atd);

                trans.complete();
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }
        return ret;
    }

    /**
     * @param atd
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean modifyActivityTemplateDef(ActivityTemplateDef atd)
            throws DAOException {
        boolean ret = false;

        if (atd != null) {
            Transaction trans = TransactionFactory.createTransaction();
            WorkFlowDAO workflowDAO = workFlowDAO(trans);
            try {
                atd = workflowDAO.modifyActivityTemplateDef(atd);

                workflowDAO.removeActivityDetailData(atd);

                workflowDAO.createDependentDetailData(atd);

                trans.complete();
                ret = true;
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }
        return ret;
    }

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
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeActivityFromProcess(Integer actTemplId,
            Integer actProcessId, Integer actLoopCnt, Integer parentPtId,
            Integer subflowProcessId) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO workflowDAO = workFlowDAO(trans);

        try {
            workflowDAO.removeProcessActivities(subflowProcessId);

            workflowDAO.removeProcess(subflowProcessId);

            workflowDAO.removeTransitions(actTemplId, parentPtId);

            workflowDAO.removeProcessActivity(actTemplId, actProcessId,
                    actLoopCnt);

            workflowDAO.removeActivityTemplate(actTemplId);

            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return;
    }

    /**
     * Updates the ProcessFlow data in the database with the contents of "pf".
     * 
     * @param pf
     *            ProcessFlow to be updated to the database.
     * 
     * @return The same ProcessFlow after being updated.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessFlow updateProcessFlow(ProcessFlow pf) throws DAOException {
        WorkFlowProcess wfp = workFlowDAO().modifyProcess(pf.getProcess());
        pf.setProcess(wfp);
        return pf;
    }

    /**
     * Updates the ProcessFlow data in the database with the contents of "pf".
     * 
     * @param pf
     *            ProcessFlow to be updated to the database.
     * 
     * @return The same ProcessFlow after being updated.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessFlow updateProcessFlow(ProcessFlow pf, Transaction trans) throws DAOException {
        WorkFlowProcess wfp = workFlowDAO(trans).modifyProcess(pf.getProcess());
        pf.setProcess(wfp);
        return pf;
    }

    /**
     * @param type
     * @param extId
     * @param map
     * @param subFlow
     * @return
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessTemplate createCompositeTemplate(String type, Integer extId,
            ServiceTemplateMap[] map, boolean subFlow) throws DAOException {
        // Create a basic ProcessTemplate. This is just a skeleton, i.e.,
        // it doesn't have any ActivityTemplates and has not been saved to
        // the database yet. Adjust the overall time duration for this
        // template to be the maximum from its service orders.
        ProcessTemplate pt = WorkFlowUtils.initializeProcessTemplate(type,
                extId);

        String ptName = null;

        if (subFlow) {
            ptName = "Sub-Flow: " + extId.toString();
            pt.setProcessTemplateNm(ptName);
            pt.setProcessTemplateDsc(ptName);
            adjustDurations(pt, map);
        }

        // Using this template, create an ActivityTemplate representing
        // "Decompose Order" and another to represent order completion.
        // If we have more than one Service on the order, then change the
        // inbound and outbound transitions for the first and last
        // Activity templates to reflect the fact that we are doing the
        // Services in parallel.

        ActivityTemplate beginTemplate;
        ActivityTemplate endTemplate;

        if (subFlow) {
            beginTemplate = WorkFlowUtils.initializeActivityTemplate(true,
                    WorkFlowUtils.NOOP, null);
            endTemplate = WorkFlowUtils.initializeActivityTemplate(false,
                    WorkFlowUtils.NOOP, null);
        } else {
            beginTemplate = WorkFlowUtils.initializeActivityTemplate(true,
                    WorkFlowUtils.DECOMPOSE, pt.getProcessCd());
            endTemplate = WorkFlowUtils.initializeActivityTemplate(false,
                    WorkFlowUtils.DECOMPOSE, pt.getProcessCd());
        }

        if ((map != null) && (map.length > 1)) {
            beginTemplate.setOutTransitionDefCd("SA");
            endTemplate.setInTransitionDefCd("JA");
        }

        pt.addActivityTemplate(beginTemplate);
        pt.addActivityTemplate(endTemplate);

        // Now, create ActivityTemplates for each service on the
        // order/proposal. There may not be any, and that's okay.

        ArrayList<ActivityTemplate> svcTemplates = createServiceTemplates(map,
                extId);

        for (ActivityTemplate at : svcTemplates
                .toArray(new ActivityTemplate[0])) {
            if (subFlow) {
                at.setActivityTemplateNm(pt.getProcessTemplateNm());
            }
            pt.addActivityTemplate(at);
        }

        // Since there is a GUI interface to provision, we have to set the
        // location of each "box" on the screen. This is a function of the
        // size of each box and the number of boxes we have to draw.

        setLocations(beginTemplate, endTemplate, svcTemplates);

        // We need to save the info we've gathered thus far to the database
        // so we can get some key values. We need the keys to create
        // transitions. We'll set this up to do everything in a single
        // DB transaction so we can roll it all back in the event of a
        // failure.
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO workflowDao = workFlowDAO(trans);

        try {
            // Create the ProcessTemplate in the database. This will also
            // automatically create the ActivityTemplates.
            pt = createProcessTemplate(pt, workflowDao);

            // Now, create the transitions or "links" between the various
            // ActivityTemplate objects.
            Transition[] links = createTransitions(beginTemplate, endTemplate,
                    svcTemplates);

            // Save the Transitions to the database.
            for (Transition t : links) {
                workflowDao.createTransition(t);
            }

            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return pt;
    }

    /**
     * Updates an Activity object to the database. The object is assumed to be
     * fully specified, i.e., all key values are set, and valid. The Activity is
     * broken up into its constituent parts and each part is updated in the
     * database as needed.
     * 
     * @param act
     *            Activity to be updated in the database.
     * 
     * @return The Activity after it has been updated.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Activity updateActivity(Activity act) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO wfDao = workFlowDAO(trans);

        // Create a DAO and pass it off to the protected version of this
        // method. The protected version will do the update and we will
        // control the Transaction.
        try {
            updateActivity(act, wfDao);

            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return act;
    }

    /**
     * Updates an Activity object to the database. The object is assumed to be
     * fully specified, i.e., all key values are set, and valid. The Activity is
     * broken up into its constituent parts and each part is updated in the
     * database as needed.
     * 
     * @param act
     * @param trans
     * @return
     * @throws DAOException
     */
    public Activity updateActivity(Activity act, Transaction trans) throws DAOException {
        if(trans == null){
        	logger.debug(" Updating process activity with no active transaction");
        	updateActivity(act);
        } else {
        	logger.debug(" Updating process activity with an active transaction");
        	WorkFlowDAO wfDao = workFlowDAO(trans);
        	updateActivity(act, wfDao);
        }
        return act;
    }    
    
    /**
     * Creates an Activity object to the database. The object is assumed to be
     * fully specified, i.e., all key values are set, and valid. The Activity is
     * broken up into its constituent parts and each part is updated in the
     * database as needed. This creation is required to support the generic goto
     * mechanism.
     * 
     * @param act
     *            Activity to be added in the database.
     * 
     * @return The Activity after it has been created.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Activity createActivity(Activity act) throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO wfDao = workFlowDAO(trans);

        // Create a DAO and pass it off to the protected version of this
        // method. The protected version will do the update and we will
        // control the Transaction.
        try {
            createActivity(act, wfDao);

            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return act;
    }

    /**
     * Cancels the order associated with "deadmeat". This removes the order from
     * the provisioning process.
     * 
     * @param deadmeat
     *            The Process that is being canceled.
     * 
     * @throws java.lang.Exception
     *             Database access error or missing OrderHeader.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void cancelProcess(ProcessFlow deadmeat) throws DAOException {
        if (deadmeat != null) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            deadmeat.setEndDt(now);

            Transaction trans = TransactionFactory.createTransaction();
            WorkFlowDAO wfDao = workFlowDAO(trans);

            try {
                cancelProcess(deadmeat, now, wfDao);

                trans.complete();
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }
        return;
    }

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
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeActivity(ProcessFlow parent, Activity deadmeat,
            Integer orderId) throws DAOException {
        // Get all of the various Ids we are going to need.

        Integer parentPtId = parent.getProcessTemplateId();

        Integer procTemplId = deadmeat.getActivityTemplateId();
        Integer actProcessId = deadmeat.getProcessId();
        Integer actLoopCnt = deadmeat.getLoopCnt();

        ProcessFlow subflow = deadmeat.getSubFlow();
        Integer subflowProcessId = subflow.getProcessId();

        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO wfDao = workFlowDAO(trans);

        try {
            // Retrieve the child services before we start using the DAO to
            // write data (some JDBC connections don't handle reading and
            // writing at the same time very well).

            // Remove the Activity from its Process in the database, especially
            // its Process Template. We also have to remove the subflow at
            // the same time.
            wfDao.removeProcessActivities(subflowProcessId);

            wfDao.removeProcess(subflowProcessId);

            wfDao.removeTransitions(procTemplId, parentPtId);

            wfDao.removeProcessActivity(procTemplId, actProcessId, actLoopCnt);

            wfDao.removeActivityTemplate(procTemplId);

            // Remove the Activity from the parent process and then adjust the
            // graphical layout of the process to compensate for the "recently
            // departed" Activity.

            parent.removeActivity(deadmeat);

            updateGeometry(parent, wfDao);

            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return;
    }

    /**
     * Creates ActivityTemplate objects for each service in the service map.
     * This is a skeleton object and has not been saved to the database.
     * 
     * @param map
     *            Map of Service Ids to Activity Template Def Ids.
     * @param orderId
     *            The Id of the order we are processing.
     * 
     * @return ArrayList of ActivityTemplate objects, one per Service.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ArrayList<ActivityTemplate> createServiceTemplates(
            ServiceTemplateMap[] map, Integer orderId) throws DAOException {
        ArrayList<ActivityTemplate> results = new ArrayList<ActivityTemplate>();

        // If we have no services in the map, we are already done.
        if ((map != null) && (map.length != 0)) {
            ServiceTemplateMap stm;
            Integer serviceId;
            Integer atdId;

            int i = 0;

            // For (each service in the map) use the activity template def Id
            // to retrieve the ActivityTemplateDef object. Using this object,
            // create an ActivityTemplate for the service.

            while (i < map.length) {
                stm = map[i++];
                serviceId = stm.getServiceId();
                atdId = stm.getActivityTemplateDefId();

                // Create the object and assign it some "safe" values.
                ActivityTemplate at = new ActivityTemplate();

                at.setActivityTemplateDefId(atdId);
                at.setServiceId(serviceId);
                at.setExpectedDuration(new Integer(1));
                at.setMilestoneInd("Y");
                at.setInitTaskInd("N");
                at.setInTransitionDefCd("IL");
                at.setOutTransitionDefCd("IL");
                at.setDeprecatedInd("N");

                results.add(at);
            }
        }

        return results;
    }

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
     * 
     * @throws java.lang.Exception
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void adjustDurations(ProcessTemplate pt, ServiceTemplateMap[] map)
            throws DAOException {
        // If we don't have any services, then we don't need to adjust
        // anything.
        if ((map != null) && (map.length > 0)) {
            // Get the current expected and jeopardy durations from the
            // template.
            // We will use those as a starting point.

            Integer currentExp = pt.getExpectedDuration();
            Integer currentJeo = pt.getJeopardyDuration();

            ServiceTemplateMap stm;
            Integer atdId;
            ProcessTemplate atdPt;
            int i;

            WorkFlowDAO workflowDao = workFlowDAO();

            // For (each entry in the service map), use the activity template
            // def Id to get the ActivityTemplateDef object that provisions the
            // service. Use the ActivityTemplateDef object to get the
            // ProcessTemplate for this service. The ProcessTemplate has the
            // numbers we need.
            for (i = 0; i < map.length; i++) {
                stm = map[i];
                atdId = stm.getActivityTemplateDefId();
                ActivityTemplateDef atd = workflowDao
                        .retrieveActivityTemplateDef(atdId);
                Integer atdPtId = atd.getProcessTemplateId();

                if ((atdPtId == null) || (atdPtId.intValue() == 0)) {
                    continue; // This is probably an error
                }

                // Get the ProcessTemplate and get the durations from it.
                // If they are longer than our current durations, then save 'em.
                atdPt = workflowDao.retrieveProcessTemplate(atdPtId);
                Integer expectDur = atdPt.getExpectedDuration();
                Integer jeopardyDur = atdPt.getJeopardyDuration();

                if (expectDur.compareTo(currentExp) > 0) {
                    currentExp = expectDur;
                }

                if (jeopardyDur.compareTo(currentJeo) > 0) {
                    currentJeo = jeopardyDur;
                }
            }

            // Save the longest durations to the ProcessTemplate.
            pt.setExpectedDuration(currentExp);
            pt.setJeopardyDuration(currentJeo);
        }
        return;
    }

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
     * 
     * @return Transition[] A array of "In-Line" transitions.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public Transition[] createTransitions(ActivityTemplate decompose,
            ActivityTemplate orderComplete, ArrayList<ActivityTemplate> svcTemplates)
            throws DAOException {
        ArrayList<Transition> ret = new ArrayList<Transition>();
        Transition t;

        // If there are no service ActivityTemplates, then just link the
        // "decompose" template to the "done" template.
        if ((svcTemplates == null) || (svcTemplates.size() == 0)) {
            t = WorkFlowUtils.createTransition(decompose, orderComplete, "NL");
            ret.add(t);
        } else {
            // For (each service template) hooks its input to the output of
            // "decompose" and its output to the input of "done".
            for (ActivityTemplate at : svcTemplates) {
                t = WorkFlowUtils.createTransition(decompose, at, "NL");
                ret.add(t);
                t = WorkFlowUtils.createTransition(at, orderComplete, "NL");
                ret.add(t);
            }
        }

        return ret.toArray(new Transition[0]);
    }

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
     * 
     * @return ProcessFlow[] An array of all ProcessFlows created for this
     *         template.
     * 
     * @throws java.lang.Exception
     *             Invalid Process Template Id or database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    @Override
    public ProcessFlow[] createProcessFlows(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer userId, String roleDiscrim)
            throws DAOException {
        ArrayList<ProcessFlow> flows = new ArrayList<ProcessFlow>();

        // Create our top-level ProcessFlow. Then, recursively create any
        // sub-flows this ProcessFlow needs.

        ProcessFlow pf = createProcessFlow(processTemplateId, accountId,
                externalId, expedite, startDt, dueDt, null, null, null, userId, roleDiscrim);

        if (pf != null) {
            ArrayList<ProcessFlow> subflows = createSubFlows(pf, userId, roleDiscrim, null);

            // Add this process to the output ArrayList, then add all the
            // sub-flows to the output ArrayList.

            flows.add(pf);
            flows.addAll(subflows);
        }

        // Format the output and return it.
        return flows.toArray(new ProcessFlow[0]);
    }
    
    @Override
    //DONE 2475
    // new role discrim arg
    public ProcessFlow[] createProcessFlows(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer userId, String roleDiscrim, 
            Integer assignedUserId, Transaction trans)
            throws DAOException {
        ArrayList<ProcessFlow> flows = new ArrayList<ProcessFlow>();

        // Create our top-level ProcessFlow. Then, recursively create any
        // sub-flows this ProcessFlow needs.

        ProcessFlow pf = createProcessFlow(processTemplateId, accountId,
                externalId, expedite, startDt, dueDt, null, null, null, userId,
                roleDiscrim, assignedUserId, trans);

        if (pf != null) {
        	logger.debug(" Create sub flows for process");
            ArrayList<ProcessFlow> subflows = createSubFlows(pf, userId, roleDiscrim, trans);

            // Add this process to the output ArrayList, then add all the
            // sub-flows to the output ArrayList.

            flows.add(pf);
            flows.addAll(subflows);
        }

        // Format the output and return it.
        return flows.toArray(new ProcessFlow[0]);
    }    

    /**
     * 
     * @throws java.lang.Exception
     *             Invalid Process Template Id or database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    @Override
    public ProcessFlow createTaskProcessFlows(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer userId,
            HashMap<String, String> data, Integer taskUserId, String roleDiscrim)
            throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        ProcessFlow pf = null;
        
        try {
            pf = createTaskProcessFlows(processTemplateId, accountId, externalId, expedite, startDt, dueDt, userId, data, taskUserId, roleDiscrim, trans);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return pf;
    }
    
    /**
     * 
     * @throws java.lang.Exception
     *             Invalid Process Template Id or database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    @Override
    public ProcessFlow createTaskProcessFlows(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer userId,
            HashMap<String, String> data, Integer taskUserId, String roleDiscrim, Transaction trans)
            throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO(trans);
        ProcessFlow pf = null;
        
        //DONE 2475
        // does this need to support role discrim??
        pf = createProcessFlow(processTemplateId, accountId, externalId,
                expedite, startDt, dueDt, null, null, null, userId, roleDiscrim, trans);

        modifyProcessData(data, pf.getProcessId(), trans);

        Activity a = pf.getFirstActivity();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        logger.debug("Setting activity user id to: " + taskUserId);
        a.setUserId(taskUserId);
        a.setStatusCd(Activity.IN_PROCESS);
        a.setStartDt(now);
        a.setReadyDt(now);
        a.setDueDt(a.getContainer().getDueDt());
        a.setJeopardyDt(pf.getProcess().getJeopardyDt());

        updateActivity(a, wfDao);

        return pf;
    }

    /**
     * @param processTemplateId
     * @param accountId
     * @param externalId
     * @param expedite
     * @param startDt
     * @param dueDt
     * @param serviceId
     * @param parentProcess
     * @param parentActivity
     * @param userId
     * @return
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    private ProcessFlow createProcessFlow(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer serviceId,
            ProcessFlow parentProcess, Activity parentActivity, Integer userId,
            String roleDiscrim)
            throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        ProcessFlow pf = null;
        try {
            pf = createProcessFlow(processTemplateId, accountId, externalId,
                    expedite, startDt, dueDt, serviceId, parentProcess,
                    parentActivity, userId, roleDiscrim, trans);
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return pf;
    }
   
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
     * 
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void setProcessDates(ProcessTemplate pt, Integer serviceId,
            WorkFlowProcess p, Timestamp startDt, Timestamp dueDt)
            throws DAOException {

        Timestamp jeoDt;

        // set start date to current time
        if (startDt == null || startDt.getTime() == 0) {
            startDt = new Timestamp(System.currentTimeMillis());
        }

        long startTime = startDt.getTime();
        if (dueDt == null || dueDt.getTime() == 0) {
            dueDt = new Timestamp(startTime
                    + pt.getExpectedDuration().longValue() * 1000);
            jeoDt = new Timestamp(startTime
                    + pt.getJeopardyDuration().longValue() * 1000);
        } else {
            long dueTime = (long) ((dueDt.getTime() - startTime) * .80);
            if (dueTime > 0) {
                jeoDt = new Timestamp(dueTime + startTime);
            } else {
                jeoDt = dueDt;
            }
        }

        p.setDueDt(dueDt);
        p.setJeopardyDt(jeoDt);
        p.setStartDt(startDt);

        return;
    }

    private void createTransitions(Transition[] transArray,
            WorkFlowDAO workflowDAO) throws DAOException {
        if ((transArray != null) && (transArray.length != 0)) {
            for (Transition t : transArray) {
                workflowDAO.createTransition(t);
            }

        }
        return;
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
     * 
     * @return boolean "True" if the process was deprecated.
     * 
     * @throws java.lang.Exception
     *             DB access error or "object out of date" error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean deprecateProcessTemplate(ProcessTemplate pt)
            throws DAOException {
        boolean ret = false;
        Integer ptKey = pt.getProcessTemplateId();

        if ((ptKey != null) && (ptKey.intValue() > 0)) {
            Transaction trans = TransactionFactory.createTransaction();
            WorkFlowDAO workflowDAO = workFlowDAO(trans);
            try {
                deprecateProcessTemplate(pt, workflowDAO);

                trans.complete();
                ret = true;
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }

        return ret;
    }

    private void deprecateProcessTemplate(ProcessTemplate pt, WorkFlowDAO wfDAO)
            throws DAOException {
        Integer ptKey = pt.getProcessTemplateId();

        if ((ptKey != null) && (ptKey.intValue() > 0)) {
            // If we get here, we are good to go. Deprecate the process and
            // all of its components.
            wfDAO.deprecateProcessTemplate(pt);

            wfDAO.deprecateActivityTemplates(pt);

            wfDAO.deprecateTransitions(pt);

            wfDAO.deprecateSubFlowTemplate(pt);

            wfDAO.deprecateProcessDataTemplate(ptKey);
        }
        return;
    }

    /**
     * Creates a brand new workflow in the database. Returns "true" if the
     * creation was successful. Otherwise, returns "false".
     * 
     * @param pt
     *            ProcessTemplate for the workflow.
     * @param activities
     *            The Activities in the workflow.
     * @param transitions
     *            The Transitions between Activities.
     * @param detailData
     *            The service detail data for the workflow.
     * @param wfDao
     *            The DAO to use for this action.
     * 
     * @return boolean "True" if the update was successful.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    private boolean createProcessTemplate(ProcessTemplate pt,
            ActivityTemplateDef subflowDef, Activity[] activities,
            Transition[] transitions, ProcessDataTemplate[] detailData,
            WorkFlowDAO wfDao) throws DAOException {
        boolean result = true;

        pt = createProcessTemplate(pt, wfDao);
        Integer newPtKey = pt.getProcessTemplateId();

        // All process templates can also be subflows. To make this a
        // sub-flow, create a sub-flow ActivityTemplateDef object and
        // insert it into the database.

        if (subflowDef == null) {
            subflowDef = new ActivityTemplateDef();

            subflowDef.setActivityTemplateNm(pt.getProcessTemplateNm());
            subflowDef.setActivityTemplateDsc(pt.getProcessTemplateNm());
            subflowDef.setPerformerTypeCd("S");
            subflowDef.setProcessTemplateId(pt.getProcessTemplateId());
            subflowDef.setActivityTemplateTypeCd(pt.getProcessCd());
            subflowDef.setTerminalInd("N");
            subflowDef.setHiddenInd("N");
            subflowDef.setDeprecatedInd("N");
            subflowDef.setExpectedDuration(pt.getExpectedDuration());

            wfDao.createActivityTemplateDef(subflowDef);
        } else {
            subflowDef.setProcessTemplateId(pt.getProcessTemplateId());
            subflowDef.setDeprecatedInd("N");
            subflowDef = wfDao.modifyActivityTemplateDef(subflowDef);
            wfDao.removeActivityDetailData(subflowDef);
        }

        wfDao.createDependentDetailData(subflowDef);

        // If we have no activities (which should NEVER happen), then we
        // are done (Transitions don't matter without Activities).

        if ((activities != null) && (activities.length != 0)) {
            HashMap<Integer, Integer> actKeyMap = new HashMap<Integer, Integer>();
            for (Activity a : activities) {
                Integer oldActKey = a.getActivityId();

                ActivityTemplate newAt = a.getActivityTemplate();

                // Initialize the ActivityTemplate with the retry counter and
                // interval from the ActivityTemplateDef. If an automatic
                // activity
                // fails, we will count down the retry counter when we do the
                // automatic retry.

                // ActivityTemplateDef atd = a.getActivityTemplateDef () ;
                // newAt.setNumberOfRetries (atd.getNumberOfRetries()) ;
                // newAt.setRetryInterval (atd.getRetryInterval()) ;

                newAt.setProcessTemplateId(newPtKey);
                newAt = wfDao.createActivityTemplate(newAt);
                Integer newActKey = newAt.getActivityTemplateId();

                a.setActivityTemplate(newAt);
                actKeyMap.put(oldActKey, newActKey);
            }

            // Update the process detail data objects. We have both a new
            // process template Id and new Activity Template Ids.

            if ((detailData != null) && (detailData.length > 0)) {
                for (ProcessDataTemplate pdt : detailData) {
                    pdt.setProcessTemplateId(newPtKey);

                    Integer oldActTemplId = pdt.getActivityTemplateId();

                    if (oldActTemplId != null) {
                        Integer newActTemplId = actKeyMap.get(oldActTemplId);
                        pdt.setActivityTemplateId(newActTemplId);
                    }

                    wfDao.createProcessDataTemplate(pdt);
                }
            }

            // Save the Transitions to the database.

            if ((transitions != null) && (transitions.length != 0)) {
                for (Transition t : transitions) {
                    t.setProcessTemplateId(newPtKey);

                    Integer oldFromId = t.getFromId();
                    Integer oldToId = t.getToId();

                    Integer newFromId = actKeyMap.get(oldFromId);
                    t.setFromId(newFromId);

                    Integer newToId = actKeyMap.get(oldToId);
                    t.setToId(newToId);

                    wfDao.createTransition(t);
                }
            }
        }

        return result;
    }

    /**
     * Cancels a ProcessFlow by setting its end date to "now" and persists the
     * ProcessFlow to the database. In addition, cancels any child ProcessFlows
     * using the same technique.
     * 
     * @param deadmeat
     *            ProcessFlow to be canceled.
     * @param endTs
     *            Timestamp to use for the end date.
     * @param wfDao
     *            DAO that will modify the database.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    private void cancelProcess(ProcessFlow deadmeat, Timestamp endTs,
            WorkFlowDAO wfDao) throws DAOException {
        // Set the ending Timestamp on the ProcessFlow and update the
        // database.
        deadmeat.setEndDt(endTs);
        deadmeat.setProcess(wfDao.modifyProcess(deadmeat.getProcess()));

        // Get a list of all the Activities in this ProcessFlow. If there
        // are none, then we are done.
        Activity[] deadBabies = deadmeat.getActivities();

        if ((deadBabies != null) && (deadBabies.length != 0)) {
            // For each Activity, see if the Activity has a sub-flow. If it
            // does, cancel the sub-flow using recursion. Mark each Activity
            // as "Skipped" (regardless of its original state) so that if a
            // user ever views the process, they can easily see what happened.
            for (Activity a : deadBabies) {
                ProcessFlow subflow = a.getSubFlow();

                if (subflow != null) {
                    cancelProcess(subflow, endTs, wfDao);
                }

                a.setStatusCd(Activity.SKIPPED);
                a.setProcessActivity(wfDao.modifyProcessActivity(a
                        .getProcessActivity()));
                // a.setActivityTemplate(wfDao.modifyActivityTemplate(a
                // .getActivityTemplate()));
            }
        }
        return;
    }

    /**
     * Sets the X and Y locations for the various ActivityTemplate objects for a
     * composite ProcessTemplate. Basically, this is an attempt to layout "1" to
     * "n" boxes in a sane manner. The "decompose" and "orderComplete" templates
     * are drawn as the endpoints of the graphical representation. The service
     * templates are drawn as all the stuff between the two.
     * 
     * @param decompose
     *            ActivityTemplate.
     * @param orderComplete
     *            ActivityTemplate.
     * @param svcTemplates
     *            ActivityTemplates for the services.
     */
    private void setLocations(ActivityTemplate decompose,
            ActivityTemplate orderComplete, ArrayList<ActivityTemplate> svcTemplates) {
        // JEC NOTE: I didn't figure this stuff out. Go ask Sam...

        int yincr = 50;
        int ysize = 50;

        int serviceCnt = svcTemplates.size();
        int nCols = (serviceCnt / 7) + 1;

        int xloc = 130 - (nCols * 10);
        int xend = 670 + (nCols * 10);
        int xincr = 300 - (nCols * 50);

        int yloc = 460 - ((serviceCnt / 2) * 100);

        if (nCols > 1) {
            yloc = 160;
        }

        int ystart = yloc;

        decompose.setXloc(new Integer(xloc));
        decompose.setYloc(new Integer(460));

        orderComplete.setXloc(new Integer(xend));
        orderComplete.setYloc(new Integer(460));

        xloc = xloc + xincr;

        int i;
        int cnt = 0;
        ActivityTemplate at;

        for (i = 0; i < svcTemplates.size(); i++) {
            at = svcTemplates.get(i);
            at.setXloc(new Integer(xloc));
            at.setYloc(new Integer(yloc));

            yloc = yloc + ysize + yincr;
            cnt++;

            if (cnt == 7) {
                cnt = 0;
                yloc = ystart;
                xloc = xloc + xincr;
            }
        }
        return;
    }
    public ProcessFlow createProcessFlow(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer serviceId,
            ProcessFlow parentProcess, Activity parentActivity, Integer userId,
            String roleDiscrim, Transaction trans) throws DAOException {
    	return createProcessFlow( processTemplateId,
                 accountId,  externalId,  expedite,
                 startDt,  dueDt,  serviceId,
                 parentProcess,  parentActivity,  userId,
                 roleDiscrim, null, trans);
    }

    /**
     * Creates a new ProcessFlow based on the process template identified by
     * <tt>processTemplateId</tt>. In addition to creating the ProcessFlow,
     * process flow information is also saved in the database. Note that the
     * ProcessFlow has all of its Activities (also saved to the database),
     * DataFields, and Transitions. The "parentProcess" and "parentActivity"
     * objects identify parent process and activity if this ProcessFlow is a
     * sub-flow of another process. If this ProcessFlow is not a sub-flow, then
     * set these values to <tt>null</tt>. If there is no ProcessTemplate in
     * the database corresponding to processTemplateId, then an Exception will
     * be thrown.
     * 
     * @param processTemplateId
     *            process template Id.
     * @param accountId
     *            The Id of the account on the associated order.
     * @param externalId
     *            Usually, an order number or proposal number.
     * @param dueDt
     *            null means get expactedDuration from DB
     * @param serviceId
     *            The Id of the service associated with this order.
     * @param parentProcess
     *            The parent process for this sub-process.
     * @param parentActivity
     *            The parent activity for this sub-process.
     * @param trans
     * 
     * @return ProcessFlow A fully specified ProcessFlow object.
     * 
     * @throws java.lang.Exception
     *             No ProcessTemplate or database access error.
     */
    //TODO why isnt there an interface method for this?
    //DONE 2475
    // new role discrim arg
    public ProcessFlow createProcessFlow(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer serviceId,
            ProcessFlow parentProcess, Activity parentActivity, Integer userId,
            String roleDiscrim, Integer assignedUserId, Transaction trans) throws DAOException {
    	if(trans == null){
    		trans = TransactionFactory.createTransaction();
    	}
    	
        WorkFlowDAO wfDao = workFlowDAO(trans);
        
        logger.debug(" Retrieving process templates");
        ProcessTemplate pt = wfDao.retrieveProcessTemplate(processTemplateId);

        // If we didn't get a ProcessTemplate, then thrown an Exception.
        if (pt == null) {
            throw new DAOException("No ProcessTemplate found for process "
                    + "template Id = " + processTemplateId.toString() + ".");
        }

        logger.debug(" Retrieving data fields for process templates");
        DataField[] ptFields = wfDao
                .retrieveDataFieldsForProcessTemplate(processTemplateId);

        logger.debug(" Retrieving transitions process templates");
        Transition[] pfTransitions = wfDao.retrieveTransitions(
                processTemplateId, true);

        // Now, create the "Process" and fill in all of the information.
        
        WorkFlowProcess p = new WorkFlowProcess();

        p.setProcessTemplateId(processTemplateId);
        p.setProcessTemplateNm(pt.getProcessTemplateNm());

        // See if this process can begin provisioning now. If not, a ready
        // date when it can be provisioned will be returned. If "readyDt"
        // is null, then set the process dates for provisioning.

        // Timestamp readyDt = WorkFlowBO.getReadyDate (pt, header) ;

        // if (readyDt != null) {
        // p.setReadyDt(readyDt);
        // } else {
        setProcessDates(pt, serviceId, p, startDt, dueDt);
        // }

        p.setFacilityId(accountId);
        p.setExternalId(externalId);
        p.setExpedite(expedite);
        p.setServiceId(serviceId);
        p.setProcessCd(pt.getProcessCd());
        p.setUserId(userId);

        // If the process we are creating is a sub-flow, extract parent
        // information.

        if (parentProcess != null) {
            p.setParentProcessId(parentProcess.getProcessId());
        }

        if (parentActivity != null) {
            p.setActivityTemplateId(parentActivity.getActivityTemplateId());
        }

        // What we are going to do next is save the Process to the
        // database. This will get us a "process Id" key which we need to
        // create the Activity objects for this process. When we create
        // the Activity components, we will need to save those components
        // to the database. We are going to write everything to the database
        // in a single Transaction so that clean up in the event of an error
        // is easy and leaves the database in a consistent and usable state.

        //wfDao.setTransaction(trans);

        ProcessFlow pf = null;

        // Save the "Process" to the database to get us a key, then use
        // the "Process" to make a ProcessFlow.
        logger.debug(" Creating the workflow process");
        wfDao.createProcess(p);
        logger.debug("Created workflow process.\nProcess Id = " + p.getProcessId() +
                ", external id = " + p.getExternalId() + ", start date = " + p.getStartDt().toString() +
                ", facility id = " + p.getFacilityId() + ", userId = " + p.getUserId());
        pf = new ProcessFlow(p);

        pf.setParentProcess(parentProcess);
        pf.setParentActivity(parentActivity);

        if (parentActivity != null) {
            parentActivity.setSubFlow(pf);
        }

        // Add the DataFields and Transitions to the ProcessFlow.
        logger.debug(" Adding data fields and transitions");
        DataField[] processDfs = WorkFlowUtils.loadDataFields(pf, ptFields);
        try {
            pf.addDataFields(processDfs);
            // debugPrint(processDfs); // dennis reza debugging
            pf.addTransitions(pfTransitions);
        } catch (Exception e) {
            logger.error(e);
        }
        ArrayList<ProcessData> als = new ArrayList<ProcessData>();
        boolean updated = false;
        for (DataField d : processDfs) {
            ProcessData pd = new ProcessData();
            pd.setDataDetailId(d.getCustomDetailTypeId());
            pd.setDataTemplateId(d.getDataTemplateId());
            pd.setDataValue(d.getDataValue());
            pd.setProcessId(d.getProcessId());
            als.add(pd);
            updated = true;
        }
        if (updated) {
            wfDao.createProcessDatas(als.toArray(new ProcessData[0]));
        }

        // Read in all of the ActivityTemplates for this Process Template.
        // We will use these to construct the Activity objects.
        logger.debug(" Retrieving the activity templates for process template");
        ActivityTemplate[] ats = wfDao
                .retrieveActivityTemplatesForProcessTemplate(processTemplateId);

        // If we don't have Activity Templates, we don't have Activities.

        if ((ats == null) || (ats.length == 0)) {
            return pf;
        }

        int i;
        ActivityTemplate at;

        // For each (Activity Template) create a ProcessActivity object
        // (which is the "guts" of the Activity object). Save the
        // ProcessActivity to the database and then create an Activity
        // from all of its parts. Add the Activity to the ProcessFlow.

        logger.debug(" Create process activities for process flow");
        for (i = 0; i < ats.length; i++) {
            at = ats[i];
            Integer atdId = at.getActivityTemplateDefId();
            logger.debug(" Retrieving acitvity template definition data...");
            ActivityTemplateDef atd = wfDao.retrieveActivityTemplateDef(atdId);

            ProcessActivity pa = new ProcessActivity();

            pa.setProcessId(p.getProcessId());
            pa.setLoopCnt(new Integer(1));
            pa.setActivityTemplateId(at.getActivityTemplateId());
            pa.setCurrent("Y");
            pa.setPerformerTypeCd(atd.getPerformerTypeCd());
            pa.setNumberOfRetries(atd.getNumberOfRetries());
            pa.setRetryInterval(atd.getRetryInterval());
            String as;
            if (at.isInitTask() && pf.getParentProcess() == null) {
                pa.setReadyDt(startDt);
                as = Activity.IN_PROCESS;
            } else {
                as = Activity.NOT_DONE;
            }
            pa.setActivityStatusCd(as);

            //
            // You may not want to do this task if your system
            // requires self assignment from a pool of READY tasks
            if (!pf.getProcessCd().equalsIgnoreCase("task")) {
                if (null != assignedUserId) {
                	pa.setUserId(assignedUserId);
                } else {
	            	//DONE 2475
	            	// pass in role discrim
	            	logger.debug(" Assigning task...");
	                autoAssignTask(accountId, at.getRoleCd(), pa, userId, roleDiscrim);  // dwl passing userId is new ----since new workflow; should be OK
                }
            }
            // TRACE CODE FOR TESTING1 for mantis #2207
            ProcessActivity paSave = pa;
            // END TRACE CODE FOR TESTING1
            logger.debug(" Creating process activity...");
            wfDao.createProcessActivity(pa);
            pa = wfDao.retrieveProcessActivity(pa.getProcessId(), pa
                    .getActivityTemplateId());
            // TRACE CODE FOR TESTING2  for mantis #2207
            if(pa == null) {
                logger.error("pa is null, previous values were:" +
                        " processId" + paSave.getProcessId() +
                        " loopCnt" + paSave.getLoopCnt() +
                        " activityTemplateId" + paSave.getActivityTemplateId() +
                        " current" + paSave.getCurrent() +
                        " performerTypeCd" + paSave.getPerformerTypeCd() +
                        " numberOfRetries" + paSave.getNumberOfRetries() +
                        " retryInterval" + paSave.getRetryInterval() +
                        " activityStatusCd" + paSave.getActivityStatusCd() +
                        " readyDt" + paSave.getReadyDt()
                        );
                // set pa to the saved version if retrieval fails
                pa = paSave;
                pa.setContactId(1); // set default contact id
            }
            // TRACE CODE FOR TESTING2
            Activity a = new Activity(pf, pa, at, atd);
            pf.addActivity(a);
        }

        //trans.complete(); // Happy! Happy! Joy! Joy!

        return pf;
    }

    private Integer getAssignedUser(DataField[] processDfs) {
    	Integer assignedUserId = null;
    	for (DataField df : processDfs) {
    		if (WorkFlowEngine.ASSIGNED_USER.equals(df.getDataName())) {
    			assignedUserId = Integer.valueOf(df.getDataValue());
    		}
    	}
		return assignedUserId;
	}

	/**
     * @param fpId
     * @param roleCd
     * @param pa
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    @Override
    //DONE 2475
    // new role discrim arg
    public void autoAssignTask(Integer fpId, String roleCd,
            ProcessActivity pa, Integer userId, String roleDiscrim) throws DAOException { // DWL  passing userId is new
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
        //
        // Auto assign userIds to avoid tasks which need to be checked
        // out - Note: the auto assignment task will not be needed
        //
        // logger.error("DWL: fp_id and role_cd and userId" + fpId.toString() + " " + roleCd + " " + userId, new Exception());  //DWL temporary for debugging #3206
        logger.debug("fp_id and role_cd " + fpId + " " + roleCd);
        //DONE 2475
        // pass in role discrim
        logger.debug("Retrieving user ids from facility...");
        Integer[] userIds = retrieveUserIdsOfFacility(fpId, roleCd, roleDiscrim);
        Integer uid;
        boolean workflowRole = false;
        
        // decouple workflow
//      FacilityDAO facDao = (FacilityDAO) (DAOFactory.getDAO("FacilityDAO"));
//      FacilityDAO facDao = (FacilityDAO) getDAO(FacilityDAO.class);
//        
        logger.debug("Retrieving facility roles...");
        FacilityRoleDef[] facilityRoles = workFlowDAO().retrieveFacilityRoleDefs();
        
         //
         // Typically tasks are assigned to the user who is assigned the particular facility  
         // role for the given facility with the exception of FacilityRole of Type "Workflow".
         // In this special case the task is assigned to whomever initiated the workflow.
         //
        for (FacilityRoleDef roleDef : facilityRoles) {
        	
        	if (roleCd != null && roleCd.equals(roleDef.getCode())) {
        		if ("W".equals(roleDef.getType()))
        		    workflowRole = true;
        		break;
        	}
        }
        
        if (workflowRole) {
        	uid = userId;
        } // dWL from workflowRole variable to "else" below it is new
        else if (userIds != null && userIds.length != 0) {
            uid = userIds[0];
        } else {
        	logger.debug("Retrieving user ids from facility...");
            userIds = retrieveUserIdsOfFacility(fpId, null, roleDiscrim);
            if (userIds != null && userIds.length != 0) {
                uid = userIds[0];
            }else{
                logger.debug("WARN] Cannot find any user on fpId " + fpId + " use user id 1");
                uid = 1;
            }
        }

        if (pa.getPerformerTypeCd().equalsIgnoreCase("A")) {
        	logger.debug("performer type cd = 'A', assigning task to Admin user");
            pa.setUserId(1); // Admin user
        } else {
        	logger.debug("performer type cd != 'A', assigning task to user: " + uid);
            pa.setUserId(uid);
        }
        // logger.error("pa.userId was set to " + pa.getUserId(), new Exception());  // DWL  temp code for debugging
    }

    /**
     * Recursively creates all of the sub-flow ProcessFlows for "pf". The
     * returned <tt>ArrayList</tt> will never be null, but may be empty if
     * "pf" has no sub-flows.
     * 
     * @param pf
     *            Parent ProcessFlow.
     * 
     * @return ArrayList of sub-flow ProcessFlows.
     * 
     * @throws java.lang.Exception
     *             Invalid Process Template Id or database access error.
     */
    private ArrayList<ProcessFlow> createSubFlows(ProcessFlow pf, Integer userId, String roleDiscrim, Transaction trans)
            throws DAOException {
        ArrayList<ProcessFlow> subflows = new ArrayList<ProcessFlow>();

        // Sub-flows indicated by Activity objects whose "performer type
        // code" (in the database) indicates that there are sub-flows.
        // Start by asking for all of the Activity objects for "pf". If
        // we don't have an Activities, we can't have any sub-flows.
        Activity[] activities = pf.getActivities();

        if ((activities != null) && (activities.length != 0)) {
            // Iterate over the list of Activities and see which Activities are
            // associated with sub-flows. When we find one, create a
            // ProcessFlow for the sub-flow. Then, recursive check for sub-flows
            // of that ProcessFlow.
            for (Activity a : activities) {
                String performerTypeCd = a.getPerformerTypeCd();

                // Ignore any Activity that is not associated with a sub-flow.
                if (!performerTypeCd.equals("S")) {
                    continue;
                }

                // This Activity has a sub-flow. Create a ProcessFlow for it.
                Integer processTemplateId = a.getServiceProcessTemplateId();
                Integer serviceId = a.getServiceId();
                Integer accountId = pf.getAccountId();
                Integer externalId = pf.getExternalId();
                String expedite = pf.getExpedite();
                Timestamp dueDt = pf.getDueDt();

                ProcessFlow subflow = createProcessFlow(processTemplateId,
                        accountId, externalId, expedite, null, dueDt,
                        serviceId, pf, a, userId, roleDiscrim, trans);
                subflows.add(subflow);

                // update parent subflow due date and jeopardy date if it is
                // before the corresponding date in the child process
                WorkFlowProcess parentProcess = pf.getProcess();
                WorkFlowProcess childProcess = subflow.getProcess();

                Timestamp parentDueDt = parentProcess.getDueDt();
                Timestamp childDueDt = childProcess.getDueDt();

                if ((parentDueDt != null) && (childDueDt != null)) {
                    if (parentDueDt.before(childDueDt)) {
                        parentProcess.setDueDt(childDueDt);
                    }
                    if (parentProcess.getJeopardyDt().before(
                            childProcess.getJeopardyDt())) {
                        parentProcess.setJeopardyDt(childProcess
                                .getJeopardyDt());
                    }
                }
                // Create any sub-flows for the "subflow" we just created. Add
                // them to our output array.
                ArrayList<ProcessFlow> temp = createSubFlows(subflow, userId, roleDiscrim, trans);
                subflows.addAll(temp);
            }
        }

        return subflows;
    }
    
    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessActivity updateLegacyProcessActivity(ProcessActivity pa) throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO();
        wfDao.modifyProcessActivity(pa);
        return pa;
    }

    private void updateActivity(Activity act, WorkFlowDAO wfDao)
            throws DAOException {
        act.setProcessActivity(wfDao.modifyProcessActivity(act
                .getProcessActivity()));
        // act.setActivityTemplate(wfDao.modifyActivityTemplate(act
        // .getActivityTemplate()));
    }

    private void createActivity(Activity act, WorkFlowDAO wfDao)
            throws DAOException {
        act.setProcessActivity(wfDao.createProcessActivity(act
                .getProcessActivity()));
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessActivity updateProcessActivityViewedState(ProcessActivity pa)
            throws DAOException {
        return workFlowDAO().modifyProcessActivityViewedState(pa);
    }
    
    /**
     * By bug number 1640, user need a ability to change the referral end date
     * which is not allowed in workflow engine to do when complete (check in) 
     * the task (activity).  
     * 
     * This method should not be used in other place.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessActivity modifyProcessActivityEndDate(ProcessActivity pa)
            throws DAOException {
        return workFlowDAO().modifyProcessActivityEndDate(pa);
    }
    
    /**
     * Creates a new ProcessFlow for an order split. This ProcessFlow will be
     * based on the ProcessTemplate "pt" and will be persisted to the database.
     * Note that this creates the new ProcessFlow, but does NOT associate the
     * subflow with the new ProcessFlow.
     * 
     * @param pt
     *            ProcessTemplate for the ProcessFlow.
     * @param orderId
     *            The new order Id for the order split.
     * @param accountId
     *            The Id of the account associated with the new order.
     * @param serviceId
     *            The Id of the service being moved to the new order.
     * @param wfDao
     *            The DAO used to persist the ProcessFlow to the database.
     * 
     * @return ProcessFlow The new ProcessFlow for the order split.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    private ProcessFlow createSplitProcessFlow(ProcessTemplate pt,
            Integer orderId, Integer accountId, Integer serviceId,
            WorkFlowDAO wfDao) throws DAOException {
        ProcessFlow pf = null;

        // See if the input ProcessTemplate has expected/jeopardy time
        // durations. If it does, convert "seconds" to "milliseconds".
        /*
         * long expDuration = 0l ; long jeoDuration = 0l ;
         * 
         * Integer foo = pt.getExpectedDuration() ;
         * 
         * if (foo != null) { expDuration = (pt.getExpectedDuration().intValue() *
         * 1000) ; }
         * 
         * foo = pt.getJeopardyDuration() ;
         * 
         * if (foo != null) { jeoDuration = (pt.getJeopardyDuration().intValue() *
         * 1000) ; }
         */
        Integer processTemplateId = pt.getProcessTemplateId();

        // Retrieve all of the Transitions for this ProcessTemplate.

        Transition[] pfTransitions = wfDao.retrieveTransitions(
                processTemplateId, true);

        // Now, create the "Process" and fill in all of the information.

        WorkFlowProcess p = new WorkFlowProcess();

        setProcessDates(pt, serviceId, p, null, null);
        p.setProcessTemplateId(processTemplateId);
        p.setFacilityId(accountId);
        p.setExternalId(orderId);
        p.setProcessCd(pt.getProcessCd());
        p.setProcessTemplateNm(pt.getProcessTemplateNm());

        wfDao.createProcess(p);
        pf = new ProcessFlow(p);

        // Add all of the Transitions to the ProcessFlow.
        try {
            pf.addTransitions(pfTransitions);
        } catch (Exception e) {
            logger.error(e);
        }
        // Read in all of the ActivityTemplates for this Process Template.
        // We will use these to construct the Activity objects.

        ActivityTemplate[] ats = wfDao
                .retrieveActivityTemplatesForProcessTemplate(processTemplateId);

        // If we don't have Activity Templates, we don't have Activities.

        if ((ats != null) && (ats.length > 0)) {
            int i;
            ActivityTemplate at;

            // For each (Activity Template) create a ProcessActivity object
            // (which is the "guts" of the Activity object). Save the
            // ProcessActivity to the database and then create an Activity
            // from all of its parts. Add the Activity to the ProcessFlow.

            for (i = 0; i < ats.length; i++) {
                at = ats[i];
                Integer atdId = at.getActivityTemplateDefId();
                ActivityTemplateDef atd = wfDao
                        .retrieveActivityTemplateDef(atdId);

                ProcessActivity pa = new ProcessActivity();

                pa.setProcessId(p.getProcessId());
                pa.setActivityTemplateId(at.getActivityTemplateId());

                pa.setActivityStatusCd(Activity.NOT_DONE);
                pa.setPerformerTypeCd(atd.getPerformerTypeCd());

                wfDao.createProcessActivity(pa);
                pa = wfDao.retrieveProcessActivity(pa.getProcessId(), pa
                        .getActivityTemplateId());
                Activity a = new Activity(pf, pa, at, atd);

                if (a.isInitTask()) {
                    a.setStatusCd(Activity.COMPLETED);
                    Timestamp tt = new Timestamp(System.currentTimeMillis());
                    a.setStartDt(tt);
                    a.setDueDt(tt);
                    a.setEndDt(tt);
                    a.setReadyDt(tt);
                    a.setProcessActivity(wfDao.modifyProcessActivity(a
                            .getProcessActivity()));
                }

                pf.addActivity(a);
            }
        }

        return pf;
    }

    /*
     * private Timestamp getReadyDate(ProcessTemplate pt) // OrderHeader header) {
     * Integer earliestProvIntvl = pt.getEarliestProvInterval();
     * 
     * if ((earliestProvIntvl == null) || (earliestProvIntvl.intValue() <= 0)) {
     * return null; } // Timestamp custNeedDt = header.getCustomerNeedDate() ; //
     * if (custNeedDt == null) // { // return null ; // }
     * 
     * //long epiMsec = earliestProvIntvl.longValue() * 1000L; // Milliseconds //
     * long custNeedMsec = custNeedDt.getTime() ; // long epDateMsec =
     * custNeedMsec - epiMsec ; // Timestamp earliestProvDate = new Timestamp
     * (epDateMsec) ; // earliestProvDate.toString()) ;
     * 
     * Timestamp now = new Timestamp(System.currentTimeMillis()); // if
     * (now.before(earliestProvDate)) { // return earliestProvDate ; }
     * 
     * return null; }
     */
    private void copyDataFields(Integer oldPtId, Integer newPtId,
            Integer newProcessId, WorkFlowDAO writeDao) throws DAOException {
        /*
         * DataField[] parentDfs = writeDao
         * .retrieveDataFieldsForProcessTemplate(oldPtId);
         * 
         * if ((parentDfs != null) && (parentDfs.length != 0)) { for (DataField
         * df : parentDfs) { Integer serviceDetailDefId =
         * df.getServiceDetailDefId(); String dataTemplateNm = df.getDataName();
         * ProcessDataTemplate pdt = new ProcessDataTemplate();
         * 
         * pdt.setServiceDetailDefId(serviceDetailDefId);
         * pdt.setDataTemplateId(newPtId);
         * pdt.setDataTemplateNm(dataTemplateNm);
         * 
         * writeDao.createProcessDataTemplate(pdt);
         * df.setProcessTemplateId(pdt.getProcessTemplateId());
         * df.setDataTemplateId(pdt.getDataTemplateId());
         * writeDao.createDataField(df); } }
         */
    }

    private ProcessTemplate createProcessTemplate(ProcessTemplate pt,
            WorkFlowDAO workFlowDAO) throws DAOException {
        if (pt != null) {
            workFlowDAO.createProcessTemplate(pt);
            // If this ProcessTemplate has any associated ActivityTemplate
            // objects, then these ActivityTemplate objects will need to be
            // inserted. Note that we must update their
            // "process_template_id"
            // values before inserting them into the database.

            ActivityTemplate[] actTemplates = pt.getActivityTemplate();

            if ((actTemplates != null) && (actTemplates.length > 0)) {
                Integer ptKey = pt.getProcessTemplateId();

                for (ActivityTemplate at : actTemplates) {
                    at.setProcessTemplateId(ptKey);
                    workFlowDAO.createActivityTemplate(at);
                }
            }

        }
        return pt;
    }

    /**
     * Creates a new ProcessTemplate for an order split. This template consists
     * of a "Decompose Order" ActivityTemplate linked to a "place holder"
     * ActivityTemplate, linked to a "Billing Ready" ActivityTemplate. The new
     * template is created and persisted to the database.
     * 
     * @param type
     *            Order type, either "prov" or "sfa".
     * @param extId
     *            External Id, i.e., the order/proposal number.
     * @param wfDao
     *            DAO used for database persistence.
     * 
     * @return ProcessTemplate A new ProcessTemplate suitable for an order
     *         split.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    private ProcessTemplate createSplitProcessTemplate(String type,
            Integer extId, WorkFlowDAO wfDao) throws DAOException {
        // Create a basic ProcessTemplate. This is just a skeleton, i.e.,
        // it doesn't have any ActivityTemplates and has not been saved to
        // the database yet. Adjust the overall time duration for this
        // template to be the maximum from its service orders.
        ProcessTemplate pt = WorkFlowUtils.initializeProcessTemplate(type,
                extId);
        pt.setDynamicInd("Y");

        // Using this template, create an ActivityTemplate representing
        // "Decompose Order" and another to represent order completion.
        ActivityTemplate decompose = WorkFlowUtils.initializeActivityTemplate(
                false, WorkFlowUtils.DECOMPOSE, pt.getProcessCd());
        ActivityTemplate orderComplete = WorkFlowUtils
                .initializeActivityTemplate(true, WorkFlowUtils.DECOMPOSE, pt
                        .getProcessCd());

        pt.addActivityTemplate(decompose);
        pt.addActivityTemplate(orderComplete);

        // Create a "place holder" ActivityTemplate which will eventually
        // become the parent of the subflow being moved to the new order.
        ActivityTemplate placeHolder = new ActivityTemplate();
        placeHolder.setActivityTemplateNm("PlaceHolder");

        pt.addActivityTemplate(placeHolder);
        ArrayList<ActivityTemplate> atArray = new ArrayList<ActivityTemplate>(1);
        atArray.add(placeHolder);

        // Create the ProcessTemplate in the database. This will also
        // automatically create the ActivityTemplates.
        createProcessTemplate(pt, wfDao);

        // Now, create the transitions or "links" between the various
        // ActivityTemplate objects.
        Transition[] links = createTransitions(decompose, orderComplete,
                atArray);

        // Save the Transitions to the database.
        createTransitions(links, wfDao);

        return pt;
    }

    /**
     * Updates the location of all the boxes used in the graphical
     * representation of "pf". Updates box locations to the database.
     * 
     * @param pf
     *            ProcessFlow whose geometry is being updated.
     * @param wfDao
     *            DAO used to persist "geometry" to the database.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    private void updateGeometry(ProcessFlow pf, WorkFlowDAO wfDao)
            throws DAOException {
        // The "geometry" is actually stored in the ActivityTemplate.
        // We need to distinguish between the "Decompose Order" Activity,
        // the "Billing Ready" Activity (the two endpoints in the diagram)
        // and all of the other Activities in the process (the stuff in the
        // middle.
        ActivityTemplate firstAt = null;
        ActivityTemplate lastAt = null;
        ArrayList<ActivityTemplate> templateArray = new ArrayList<ActivityTemplate>();

        // Get all of the Activities for this process. If it has no
        // Activities, it has no geometry either.

        Activity[] activities = pf.getActivities();

        if ((activities != null) && (activities.length != 0)) {
            // For (each Activity) separate the Activity as either "Decompose"
            // "Order Complete" or "everthing else".
            for (Activity a : activities) {
                ActivityTemplate at = a.getActivityTemplate();
                Transition[] obTrans = pf.getOutboundTransitions(a);

                if (at.getInitTaskInd().equals("Y")) {
                    firstAt = at;
                } else if ((obTrans == null) || (obTrans.length == 0)) {
                    lastAt = at;
                } else {
                    templateArray.add(at);
                }
            }

            // Now that we have everything separated by category, use the helper
            // method to do the actual computation.
            setLocations(firstAt, lastAt, templateArray);

            // Update all of the ActivityTemplate objects to the database to
            // persist their new locations.
            firstAt = wfDao.modifyActivityTemplate(firstAt);
            lastAt = wfDao.modifyActivityTemplate(lastAt);

            for (ActivityTemplate at : templateArray) {
                at = wfDao.modifyActivityTemplate(at);
            }
        }
        return;
    }

    /**
     * Moves "subflow" from its current parent Activity and ProcessFlow to a new
     * parent Activity and ProcessFlow. The parent data contained in "subflow"
     * is assumed to be correct, i.e., the original parent Activity and
     * ProcessFlow. The database is updated to reflect the move.
     * 
     * @param subflow
     *            The subflow being moved to a new ProcessFlow.
     * @param newParentAct
     *            The new parent Activity for subflow.
     * @param newParentProc
     *            The new parent ProcessFlow for subflow.
     * @param newOrderId
     *            The associated new order Id for the split.
     * @param wfDao
     *            DAO used to update the database.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    private void moveActivity(ProcessFlow subflow, Activity newParentAct,
            ProcessFlow newParentProc, Integer newOrderId, WorkFlowDAO wfDao)
            throws DAOException {
        // Extract the old parent Activity and ProcessFlow from the subflow.
        // Use the old parents to get their activity template Id, process
        // template Id, and process Id.
        Activity oldParentAct = subflow.getParentActivity();

        // Now, get the activity template Id, process template Id, and
        // process Id for the new parent. Also, get the process Id for the
        // subflow.

        Integer newPtId = newParentProc.getProcessTemplateId();
        Integer newProcId = newParentProc.getProcessId();

        Integer subflowProcessId = subflow.getProcessId();

        // Update the new parent Activity with dates and status from the old
        // parent Activity.

        newParentAct.setDueDt(oldParentAct.getDueDt());
        newParentAct.setEndDt(oldParentAct.getEndDt());
        newParentAct.setReadyDt(oldParentAct.getReadyDt());
        newParentAct.setStartDt(oldParentAct.getStartDt());
        newParentAct.setStatusCd(oldParentAct.getStatusCd());

        // Update the new parent Activity to the database.

        updateActivity(newParentAct, wfDao);

        // Move the "subflow" from the original parent to the new parent
        // in the database.
        wfDao.moveSubFlow(newProcId, newPtId, newOrderId, subflowProcessId);

        // Now that the database is updated, update the in-memory version to
        // reflect the change.

        subflow.setParentActivity(newParentAct);
        subflow.setParentProcess(newParentProc);
        subflow.setExternalId(newOrderId);

        newParentAct.setSubFlow(subflow);

        return;
    }

    /**
     * Copies state values from "src" to "dest". This does not modify any of the
     * "linkage" data, so "dest" remains a part of whatever ProcessTemplate it
     * currently belongs to. After the state values are copied, "dest" is
     * updated to the database.
     * 
     * @param src
     *            ActivityTemplate whose state is being copied.
     * @param dest
     *            ActivityTemplate receiving "src"s state.
     * @param wfDao
     *            DAO used for updating the database.
     * 
     * @throws java.lang.Exception
     *             Database access error.
     */
    private void copyActivityTemplate(ActivityTemplate src,
            ActivityTemplate dest, WorkFlowDAO wfDao) throws DAOException {
        if ((src != null) && (dest != null)) {
            dest.setActivityTemplateDefId(src.getActivityTemplateDefId());
            dest.setServiceId(src.getServiceId());
            dest.setExpectedDuration(src.getExpectedDuration());
            dest.setMilestoneInd(src.getMilestoneInd());
            dest.setInitTaskInd(src.getInitTaskInd());
            dest.setInTransitionDefCd(src.getInTransitionDefCd());
            dest.setOutTransitionDefCd(src.getOutTransitionDefCd());
            dest.setXloc(src.getXloc());
            dest.setYloc(src.getYloc());
            dest.setActivityTemplateNm(src.getActivityTemplateNm());
            dest.setDeprecatedInd(src.getDeprecatedInd());

            dest = wfDao.modifyActivityTemplate(dest);
        }
        return;
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
        return workFlowDAO().retrieveActivityListLight(pa);
    } 

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public LinkedHashMap<String, Integer> retrieveWorkflowTempIdAndNm()
            throws DAOException {
    	LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
    	
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
        SimpleIdDef[] sid = workFlowDAO().retrieveWorkflowTempIdAndNm();

        for (SimpleIdDef temp : sid) {
            ret.put(temp.getDescription(), temp.getId());
        }

        return ret;
    }
    
    public LinkedHashMap<String, Integer> retrieveWorkflowTempIdAndNm(Transaction trans)
            throws DAOException {
    	LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
    	
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
        SimpleIdDef[] sid = workFlowDAO().retrieveWorkflowTempIdAndNm();

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
        ProcessTemplate[] pts = workFlowDAO().retrieveProcessTemplatesByType(type, true, false);

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
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
        return workFlowDAO().retrieveProcessGroupByType();
//    	return null;
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void createProcessDatas(ProcessData[] datas) throws DAOException {
        workFlowDAO().createProcessDatas(datas);
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
    public WorkFlowProcess[] retrieveProcessList(WorkFlowProcess wp)
            throws DAOException {
        return workFlowDAO().retrieveProcessList(wp);
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
     * SPECIALTY METHOD. Completely removes processFlows from database.
     * 
     * @param processId
     *            processId of process to remove
     * @param userId
     *            userId
     * 
     * @throws DAOException
     *             Database access error.
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void removeProcessFlows(Integer processId, Integer userId)
            throws DAOException {
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO wfDAO = workFlowDAO(trans);

        try {
            WorkFlowProcess p = wfDAO.retrieveProcess(processId);
            p.setUserId(userId);
            removeChildrenProcesses(p, wfDAO);
            trans.complete();
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return;
    }

    private void removeChildrenProcesses(WorkFlowProcess p, WorkFlowDAO wfDAO)
            throws DAOException {
        // Remove all children processes from current process.
        WorkFlowProcess[] wcps = wfDAO
                .retrieveChildrenProcess(p.getProcessId());
        for (WorkFlowProcess tp : wcps) {
            removeChildrenProcesses(tp, wfDAO);
        }
        
        // removeProcessFlows is only used in activity profile for dismiss a 
        // info task.
        // Mantis # 1867 asked to not have even log created for dismiss info task.
        /*EventLog el = new EventLog();
        StringBuffer note = new StringBuffer();
        String subject = null;
        String comment = null;
        if (p.getProcessCd().equalsIgnoreCase(WorkflowProcessDef.TODO_TASK)) {
            DataField[] dfs = wfDAO.retrieveDataFieldsForProcess(p.getProcessId());
            for (DataField df : dfs) {
                if (df.getCustomDetailTypeId() == 1) {
                    subject = df.getDataValue();
                } else if (df.getCustomDetailTypeId() == 2) {
                    comment = df.getDataValue();
                }
            }
        }
        if (subject != null) {
            note.append(subject);
            note.append(" is dismissed and comment is '");
            note.append(comment);
            note.append("'");
        } else {
            note.append("WorkFlow Process ");
            note.append(p.getProcessTemplateNm());
            note.append(" (");
            note.append(p.getProcessId());
            note.append(") is removed from Database.");
        }

        el.setFpId(p.getFacilityId());
        el.setUserId(p.getUserId());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        el.setDate(now);
        el.setEventTypeDefCd(p.getProcessCd());
        el.setNote(note.toString());

        try {
            FacilityHelper facHelper = new FacilityHelper();
            facHelper.createEventLog(el);
        } catch (RemoteException re) {
        	logger.error("Error generating EventLog", re);
            throw new DAOException("Error generating EventLog", re);
        }*/
        
        wfDAO.removeProcessData(p.getProcessId());
        wfDAO.removeProcessActivities(p.getProcessId());
        wfDAO.removeProcessNotes(p.getProcessId());
        wfDAO.removeProcess(p.getProcessId());

        return;
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
    //DONE 2475
    // new role discrim arg
    private Integer[] retrieveUserIdsOfFacility(Integer fpId,
            String facilityRoleCd, String roleDiscrim) throws DAOException {
    	
    	logger.debug("Retrieving Role code by parent...");
    	String tempfacilityRoleCd = 
    			workFlowDAO().retrieveRoleCdByParent(facilityRoleCd, roleDiscrim);
    	
    	if (null != tempfacilityRoleCd) {
    		facilityRoleCd = tempfacilityRoleCd;
    	}

    	logger.debug("Retrieving user ids from facility...");
        return workFlowDAO().retrieveUserIdsOfFacility(fpId, facilityRoleCd);
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void createProcessNote(ProcessNote pn) throws DAOException {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        pn.setPostedDt(now);
        workFlowDAO().createProcessNote(pn);
    }
    
    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean modifyProcessNote(ProcessNote pn) throws DAOException {
        return workFlowDAO().modifyProcessNote(pn);
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public void createProcessNote(ProcessNote pn, Transaction trans) throws DAOException {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        pn.setPostedDt(now);
        workFlowDAO(trans).createProcessNote(pn);
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
     * @param atd
     * @return
     * @throws DAOException
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean modifyProcessData(HashMap<String, String> dataMap, Integer processId)
            throws DAOException {
        boolean ret = false;
        Transaction trans = TransactionFactory.createTransaction();

        if (dataMap != null) {
            try {
                modifyProcessData(dataMap, processId, trans);
                ret = true;
                trans.complete();
            } catch (DAOException de) {
                cancelTransaction(trans, de);
            } finally {
                closeTransaction(trans);
            }
        }
        return ret;
    }

    /**
     * @param dataMap
     * @param processId
     * @param trans
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public boolean modifyProcessData(HashMap<String, String> dataMap, Integer processId,
            Transaction trans) throws DAOException {
    	boolean ret = false;
        WorkFlowDAO wfDao = workFlowDAO();
        wfDao.setTransaction(trans);

        DataField[] tdata = wfDao.retrieveDataFieldsForProcess(processId);
        for (DataField pdt : tdata) {
            String dataName = pdt.getDataName();
            String dataValue = dataMap.get(dataName);

            if (dataValue != null
                    && !dataValue.equalsIgnoreCase(pdt.getDataValue())) {
                pdt.setDataValue(dataValue);
                // If we changed something, save it back to the
                // database.
                wfDao.modifyDataField(pdt);
            }
        }
        
        ret = true;

        return ret;
    }

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public WorkFlowProcess cloneProcess(Integer processId, Integer userId, Integer externalId)
            throws DAOException {
    	WorkFlowProcess p = null;
    	
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
        Transaction trans = TransactionFactory.createTransaction();
        WorkFlowDAO wfDAO = workFlowDAO(trans);

        try {
            p = wfDAO.retrieveProcess(processId);
            p.setExternalId(externalId);

            // decouple workflow
//            if (WorkFlowProcess.PERMIT_WORKFLOW_NAME.equalsIgnoreCase(p.getProcessTemplateNm())) {
            	
//                PermitService pBO = ServiceFactory.getInstance().getPermitService();
//                Permit permit 
//                    = pBO.retrievePermit(p.getExternalId()).getPermit();
//                permit.setPermitNumber(null);
//                permit.setLegacyPermit(false); // Chris: If I'm cloning it in STARS2, then the newly created (cloned) permit is *not* a legacy permit.
//                permit.setDocuments(null);
//                permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.NONE);
//                permit.setPermitIssuances(new LinkedHashMap<String, PermitIssuance>());
//                permit.setEuGroups(null);
//                permit = pBO.createPermit(permit, userId, trans);
//              p.setExternalId(permit.getPermitID());
//            }
            /*Remove PBR references in permit applications
				else if (WorkFlowProcess.PBR.equalsIgnoreCase(p.getProcessTemplateNm())) {
                PermitService pBO = ServiceFactory.getInstance().getPermitService();
                Permit permit = pBO.retrievePermit(p.getExternalId()).getPermit();
                if (permit instanceof PBRPermit) {
                    int includedAppsCount = 0;
                    ApplicationService appBO = ServiceFactory.getInstance().getApplicationService();
                    PBRPermit pbrPermitClone = new PBRPermit();
                    pbrPermitClone.setFacilityId(permit.getFacilityId());
                    List<Application> permitApps = permit.getApplications();
                    for (Application app : permitApps) {
                        if (app instanceof PBRNotification) {
                            Application appClone = appBO.createPBRCopy((PBRNotification)app);
                            pbrPermitClone.setPermitNumber(appClone.getApplicationNumber());
                            pbrPermitClone.addNewApplication(appClone);
                            includedAppsCount++;
                        } else {
                            // this should never happen
                            logger.error("Non-PBR application " + app.getApplicationNumber() + 
                                    " associated with permit " + permit.getPermitNumber() + 
                            " will be excluded from cloned permit.");
                        }
                    }
                    if (includedAppsCount > 0) {
                        // copy cloned permit to 'permit' variable to continue processing
                        permit = pbrPermitClone;
                        permit = pBO.createPermit(permit, userId, trans);
                        p.setExternalId(permit.getPermitID());
                    } else {
                        // this should never happen
                        logger.error("No PBR applications associated with permit " + 
                                permit.getPermitNumber() + " attempt to clone workflow " + 
                                processId + " failed.");
                        throw new RemoteException("Unable to clone PBR workflow - no PBR applications found.");
                    }
                } else {
                    // this should never happen
                    logger.error("Unable to clone PBR workflow " + processId + 
                            " - associated permit " + permit.getPermitNumber() + 
                            " is not a PBR permit");
                    throw new RemoteException("Invalid PBR workflow. Permit is not a PBR permit. " +
                    		"Not eligible for cloning.");
                }
            }*/

            // Clone process wf_process
            p = wfDAO.createProcess(p);
            DataField[] dfs = wfDAO.retrieveDataFieldsForProcess(processId);

            // Clone wf_process_data
            boolean updated = false;
            ArrayList<ProcessData> als = new ArrayList<ProcessData>();
            for (DataField d : dfs) {
                ProcessData pd = new ProcessData();
                pd.setDataDetailId(d.getCustomDetailTypeId());
                pd.setDataTemplateId(d.getDataTemplateId());
                pd.setDataValue(d.getDataValue());
                pd.setProcessId(p.getProcessId());
                als.add(pd);
                updated = true;
            }
            if (updated) {
                wfDAO.createProcessDatas(als.toArray(new ProcessData[0]));
            }

            // Clone wf_process_activity
            ProcessActivity pa = new ProcessActivity();
            pa.setProcessId(processId);
            pa.setUnlimitedResults(true);
            ProcessActivity[] ats = wfDAO.retrieveActivityList(pa);

            for (ProcessActivity a : ats) {
                a.setProcessId(p.getProcessId());
                wfDAO.createProcessActivity(a);
            }

            // Clone wf_process_note
            ProcessNote pn = new ProcessNote();
            pn.setProcessId(processId);
            ProcessNote[] notes = wfDAO.retrieveProcessNotes(pn);
            ProcessNote n;
            for (int i = notes.length; i > 0; i--) {
                n = notes[i - 1];
                n.setProcessId(p.getProcessId());
                wfDAO.createProcessNote(n);
            }

            // Create note on both processes
            StringBuffer sb = new StringBuffer();
            sb.append("Process ID ");
            sb.append(processId);
            sb.append(" is cloned to new process ID ");
            sb.append(p.getProcessId());

            pn = new ProcessNote();
            pn.setPostedDt(new Timestamp(System.currentTimeMillis()));
            pn.setUserId(userId);
            pn.setNote(sb.toString());
            pn.setProcessId(processId);
            wfDAO.createProcessNote(pn);
            pn.setProcessId(p.getProcessId());
            wfDAO.createProcessNote(pn);

            trans.complete();
            
        }
        catch (DAOException de) {
            cancelTransaction(trans, de);
        }
        finally {
            closeTransaction(trans);
        }

        return p;
    }

    /**
     * This BO is explictly written to migrate non-completed workflows from
     * legacy applications such as Stars to Stars2. The workflows created at
     * various stages of completion. The activityTemplateDefId will indicate the
     * current task that should be marked as active (note: we are assuming only
     * one active task per workflow process instance - no paraellel tasks). All
     * other tasks will be marked as skipped.
     * 
     * Migrate ProcessFlow based on the process template identified by
     * <tt>processTemplateId</tt>. In addition to creating the ProcessFlow,
     * process flow information is also saved in the database. Note that the
     * ProcessFlow has all of its Activities (also saved to the database),
     * DataFields, and Transitions. The "parentProcess" and "parentActivity"
     * objects identify parent process and activity if this ProcessFlow is a
     * sub-flow of another process. If this ProcessFlow is not a sub-flow, then
     * set these values to <tt>null</tt>. If there is no ProcessTemplate in
     * the database corresponding to processTemplateId, then an Exception will
     * be thrown.
     * 
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
     * 
     * @return ProcessFlow A fully specified ProcessFlow object.
     * 
     * @throws java.lang.Exception
     *             No ProcessTemplate or database access error.
     * 
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    @Override
    public ProcessFlow migrateProcessFlow(Integer processTemplateId,
            Integer accountId, Integer externalId, String expedite,
            Timestamp startDt, Timestamp dueDt, Integer serviceId,
            ProcessFlow parentProcess, Activity parentActivity, Integer userId,
            Integer activityTemplateDefId, Timestamp actStartDt)
            throws DAOException {
        WorkFlowDAO wfDao = workFlowDAO();
        ProcessTemplate pt = wfDao.retrieveProcessTemplate(processTemplateId);

        // If we didn't get a ProcessTemplate, then thrown an Exception.

        if (pt == null) {
            throw new DAOException("No ProcessTemplate found for process "
                    + "template Id = " + processTemplateId.toString() + ".");
        }

        DataField[] ptFields = wfDao
                .retrieveDataFieldsForProcessTemplate(processTemplateId);

        Transition[] pfTransitions = wfDao.retrieveTransitions(
                processTemplateId, true);

        // Now, create the "Process" and fill in all of the information.

        WorkFlowProcess p = new WorkFlowProcess();

        p.setProcessTemplateId(processTemplateId);
        p.setProcessTemplateNm(pt.getProcessTemplateNm());

        // See if this process can begin provisioning now. If not, a ready
        // date when it can be provisioned will be returned. If "readyDt"
        // is null, then set the process dates for provisioning.

        // Timestamp readyDt = WorkFlowBO.getReadyDate (pt, header) ;

        // if (readyDt != null) {
        // p.setReadyDt(readyDt);
        // } else {
        setProcessDates(pt, serviceId, p, startDt, dueDt);
        // }

        p.setFacilityId(accountId);
        p.setExternalId(externalId);
        p.setExpedite(expedite);
        p.setServiceId(serviceId);
        p.setProcessCd(pt.getProcessCd());
        p.setUserId(userId);

        // If the process we are creating is a sub-flow, extract parent
        // information.

        if (parentProcess != null) {
            p.setParentProcessId(parentProcess.getProcessId());
        }

        if (parentActivity != null) {
            p.setActivityTemplateId(parentActivity.getActivityTemplateId());
        }

        // What we are going to do next is save the Process to the
        // database. This will get us a "process Id" key which we need to
        // create the Activity objects for this process. When we create
        // the Activity components, we will need to save those components
        // to the database. We are going to write everything to the database
        // in a single Transaction so that clean up in the event of an error
        // is easy and leaves the database in a consistent and usable state.

        Transaction trans = TransactionFactory.createTransaction();
        wfDao.setTransaction(trans);

        ProcessFlow pf = null;

        try {
            // Save the "Process" to the database to get us a key, then use
            // the "Process" to make a ProcessFlow.

            wfDao.createProcess(p);
            pf = new ProcessFlow(p);

            pf.setParentProcess(parentProcess);
            pf.setParentActivity(parentActivity);

            if (parentActivity != null) {
                parentActivity.setSubFlow(pf);
            }

            // Add the DataFields and Transitions to the ProcessFlow.

            DataField[] processDfs = WorkFlowUtils.loadDataFields(pf, ptFields);
            try {
                pf.addDataFields(processDfs);
                pf.addTransitions(pfTransitions);
            } catch (Exception e) {
                logger.error(e);
            }
            ArrayList<ProcessData> als = new ArrayList<ProcessData>();
            boolean updated = false;
            for (DataField d : processDfs) {
                ProcessData pd = new ProcessData();
                pd.setDataDetailId(d.getCustomDetailTypeId());
                pd.setDataTemplateId(d.getDataTemplateId());
                pd.setDataValue(d.getDataValue());
                pd.setProcessId(d.getProcessId());
                als.add(pd);
                updated = true;
            }
            if (updated) {
                wfDao.createProcessDatas(als.toArray(new ProcessData[0]));
            }

            // Read in all of the ActivityTemplates for this Process Template.
            // We will use these to construct the Activity objects.

            ActivityTemplate[] ats = wfDao
                    .retrieveActivityTemplatesForProcessTemplate(processTemplateId);

            // If we don't have Activity Templates, we don't have Activities.

            if ((ats != null) && (ats.length > 0)) {
                int i;
                ActivityTemplate at;

                // For each (Activity Template) create a ProcessActivity object
                // (which is the "guts" of the Activity object). Save the
                // ProcessActivity to the database and then create an Activity
                // from all of its parts. Add the Activity to the ProcessFlow.
                // ProcessActivity refPa = null;
                ActivityTemplate refAt = null;
                for (i = 0; i < ats.length; i++) {
                    at = ats[i];
                    Integer atdId = at.getActivityTemplateDefId();
                    ActivityTemplateDef atd = wfDao
                            .retrieveActivityTemplateDef(atdId);

                    ProcessActivity pa = new ProcessActivity();

                    pa.setProcessId(p.getProcessId());
                    pa.setLoopCnt(new Integer(1));
                    pa.setActivityTemplateId(at.getActivityTemplateId());

                    //
                    // For migrated workflows we are marking all tasks as
                    // skipped
                    // except for what the migration code tells us is the
                    // current
                    // task/activity.
                    //
                    if (atdId.equals(activityTemplateDefId)) {
                        pa.setStartDt(actStartDt);
                        pa.setActivityStatusCd(Activity.IN_PROCESS);
                        // refPa = pa;
                        refAt = at;
                    } else {
                        pa.setActivityStatusCd(Activity.SKIPPED);
                    }

                    pa.setCurrent("Y");
                    pa.setPerformerTypeCd(atd.getPerformerTypeCd());
                    pa.setNumberOfRetries(atd.getNumberOfRetries());
                    pa.setRetryInterval(atd.getRetryInterval());

                    //
                    // You may not want to do this task if your system
                    // requires self assignment from a pool of READY tasks
                    if (!pf.getProcessCd().equalsIgnoreCase("task")) {
                        autoAssignTask(accountId, at.getRoleCd(), pa, userId, null); //dwl userId is new
                    }

                    wfDao.createProcessActivity(pa);
                    pa = wfDao.retrieveProcessActivity(pa.getProcessId(), pa
                            .getActivityTemplateId());
                    Activity a = new Activity(pf, pa, at, atd);

                    pf.addActivity(a);
                }

                Activity a = null;

                if (refAt != null) {
                    a = pf.getActivity(refAt.getActivityTemplateId());
                }

                setDownStreamStates(pf, a, wfDao);

                trans.complete();
            }
        } catch (DAOException de) {
            cancelTransaction(trans, de);
        } finally {
            closeTransaction(trans);
        }

        return pf;
    }

    /**
     * Private helper function to be used only for Stars2 migration. Sets all
     * downstream states to NOT_DONE
     */
    private void setDownStreamStates(ProcessFlow pf, Activity a,
            WorkFlowDAO wfDao) throws DAOException {
        //
        // Special Case - Do not follow loops
        if (!a.getOutTransitionCode().equals(Activity.LOOP)) {
            Transition[] outbounds = pf.getOutboundTransitions(a);

            // FOR (each outbound Transition) send a "skip" control to the
            // Activity on the other end.
            for (Transition tt : outbounds) {
                Integer toActivityId = tt.getToId();
                Activity inbound = pf.getActivity(toActivityId);
                inbound.setStatusCd(Activity.NOT_DONE);
                updateActivity(inbound, wfDao);
                setDownStreamStates(pf, inbound, wfDao);
            }
        }
        return;
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
    
    private final void debugPrint(DataField[] dfs) {
        boolean anyTrue = false;
        for(DataField d : dfs) {
            boolean logFlag = false;
            if(new Integer(806).equals(d.getActivityTemplateId())) {
                logFlag = true;
                anyTrue = true;
            }
            if(new Integer(1006).equals(d.getActivityTemplateId())) {
                logFlag = true;
                anyTrue = true;
            }
            if(logFlag) { 
                logger.error("INFO ONLY for dennis/reza: " + d.toPrintable());
            }
        }
        if(anyTrue) {
            logger.error("Found some 806, 1006", new Exception());
        }
    }

    /**
     * @param userId 
     * @param name
     * @param jobClass
     * @throws DAOException
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public void scheduleRecurring(Integer processId, Integer activityId,
            Integer loopCnt, Date dateToRun, Integer userId) throws DAOException {
//    	//TODO decouple workflow
    	// this emthod was recently worked on by jon ... check the new changes
    	// before proceeding
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
        Stars2Scheduler scheduler = null;
        JobDetail job;

        if ((processId != null) && (activityId != null) && (loopCnt != null)
                && dateToRun != null && userId != null) {
            try {
                scheduler = (Stars2Scheduler) CompMgr
                        .newInstance("app.Scheduler");
                int i = 0;
                while(scheduler.getScheduler() == null && i < 1000) {
                	// give some time for the scheduler to become available
                }
                if (scheduler.getScheduler() == null) {
                	// scheduler still not ready, something is wrong
                	throw new DAOException("Unable to initialize scheduler.");
                }
            } catch (UnableToStartException utse) {
                logger.error(utse);
                throw new DAOException(utse.getMessage());
            }

            try {
            	JobKey jobKey = 
            			JobKey.jobKey(Stars2Scheduler.recurringJobName,Stars2Scheduler.recurringJobName);
                job = scheduler.getScheduler().getJobDetail(jobKey);
            } catch (SchedulerException se) {
                logger.error(se);
                throw new DAOException(se.getMessage());
            }

            // Assume it's the first time this method has been called and add
            // job
            // to scheduler.
            if (job == null) {
                Class<? extends Job> jobClass;

                try {
                    jobClass = (Class<? extends Job>) Class.forName(recurringClass);
                } catch (ClassNotFoundException cnf) {
                    logger.error(cnf);
                    throw new DAOException(cnf.getMessage());
                }

                job = new JobDetailImpl(Stars2Scheduler.recurringJobName,
                		Stars2Scheduler.recurringJobName,jobClass,true,true);

                scheduler.addJob(job);
            }

            String triggerName = Stars2Scheduler.buildTriggerName(
                    processId, activityId, loopCnt);

            JobDataMap dataMap = new JobDataMap();

            dataMap.putAsString("processId", processId);
            dataMap.putAsString("activityId", activityId);
            dataMap.putAsString("loopCnt", loopCnt);
            dataMap.putAsString("userId", userId);

            try {
                SimpleTriggerImpl trigger = new SimpleTriggerImpl(triggerName,
                        Stars2Scheduler.recurringJobName, dateToRun);

                trigger.setJobName(Stars2Scheduler.recurringJobName);
                trigger.setJobGroup(Stars2Scheduler.recurringJobName);

                trigger.setJobDataMap(dataMap);

                if (scheduler.getScheduler().checkExists(trigger.getKey())) {
                	scheduler.getScheduler().rescheduleJob(trigger.getKey(), trigger);
                } else {
                	scheduler.getScheduler().scheduleJob(trigger);
                }
            } catch (SchedulerException se) {
                logger.error(se);
                throw new DAOException(se.getMessage());
            }
        }

        return;
    }

    /**
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessActivity updateProcessActivity(ProcessActivity pa)
            throws DAOException {
        ProcessFlow p = retrieveActiveProcessFlow(pa.getProcessId());
        if (p != null) {
            Activity a = p.getActivity(pa.getActivityTemplateId());
            logger.debug("Updating activity user id to: " + pa.getUserId());
            a.setUserId(pa.getUserId());
            updateActivity(a);
        }
        return pa;
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
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
     */
    public ProcessActivity modifyProcessActivity(ProcessActivity processActivity)
            throws DAOException {
        return workFlowDAO().modifyProcessActivity(processActivity);
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

	@Override
	public ProcessFlow[] createProcessFlows(Integer processTemplateId,
			Integer accountId, Integer externalId, String expedite,
			Timestamp startDt, Timestamp dueDt, Integer userId,
			String roleDiscrim, Transaction trans)
			throws DAOException {
		return createProcessFlows( processTemplateId,
				 accountId,  externalId,  expedite,
				 startDt,  dueDt,  userId,
				 roleDiscrim, null, trans);
	}

    //TODO decouple workflow ... this method calls the workflow manager, which is
    //sillybackwards.  does anyone ever call this method anyway?
    /**
     * Reassign activities assigned to userId to the default user for that activity
     * (based on facility role code). If the default user is the same as the specified
     * user, reassign the activity to Stars2 Admin.
     * 
     * @param userId - id of user whose tasks are to be reassigned
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     * 
    public void reassignCurrentActivitiesForUser(int userId) throws DAOException {
//    	// decouple workflow
//    	// decouple workflow
//    	throw new RuntimeException(" decouple workflow; not implemented yet");
    	ProcessActivity search = new ProcessActivity();
    	search.setUserId(userId);
    	search.setCurrent("Y");
    	search.setActivityStatusCd("IP");
        WorkFlowManager wfm = new WorkFlowManager();
        int reassignedCount = 0;
        logger.debug("Checking tasks for inactive user id: " + userId + "...");
    	
    	ProcessActivity[] activities = workFlowDAO().retrieveActivityList(search);
    	for (ProcessActivity pa : activities) {
    		Integer activityTemplateId = pa.getActivityTemplateId();
    		if (activityTemplateId == null || activityTemplateId == 0) {
    			// skip tasks with null or 0 (info) activity id
    			continue;
    		}
    		ActivityTemplate at = workFlowDAO().retrieveActivityTemplate(activityTemplateId);
    		if (at != null) {
    			String facilityId = pa.getFacilityId();
    			String roleCd = at.getRoleCd();
//    			Facility facility = facilityDAO().retrieveFacility(facilityId);
    			if (facility != null) {
    				HashMap<String, FacilityRole> facilityRoles = facility.getFacilityRoles();
    				if (facilityRoles != null) {
    					FacilityRole facilityRole = facilityRoles.get(roleCd);
    					if (facilityRole != null) {
    						int newUserId = facilityRole.getUserId();
    						if (newUserId == userId) {
    							// assign to Administrator if task is still assigned
    							// to original user
    							// TODO instead of this, generate event log?
    							newUserId = CommonConst.ADMIN_USER_ID;
    						}
    						logger.debug("Reassigning task "  + 
    	                    			pa.getProcessId() + "-" + pa.getActivityTemplateId() +
    	                    			"-" + pa.getLoopCnt() + " from user id " + userId +
    	                    			" to userId " + newUserId);
    	                    WorkFlowResponse resp = wfm.reAssign(pa.getProcessId(), 
    	                            pa.getActivityTemplateId(), newUserId);
    	                    if (resp.hasError() || resp.hasFailed()) {
    	                    	logger.error("Failed reassigning task: " + 
    	                    			pa.getProcessId() + "-" + pa.getActivityTemplateId() +
    	                    			"-" + pa.getLoopCnt());
    	                        for (String s : resp.getErrorMessages()){
    	                            logger.error(s);
    	                        }
    	                        for (String s : resp.getRecommendationMessages()){
    	                            logger.debug(s);
    	                        }
    	                    } else { // reassignment was successful, add note saying so
    	                    	++reassignedCount;
    	                    	try {
    	                    		writeReassignNote(pa, userId, newUserId);
    	                    	} catch (DAOException e) {
    	                    		logger.error("Exception adding note to reassigned task.",  e);
    	                    	}
    	                    }
    					}
    				} else {
    					logger.error("Unable to find roles for facility: " + facilityId);
    				}
    			} else {
					logger.error("Unable to retrieve facility data for facility: " + facilityId);
    			}
    		} else {
    			logger.debug("No current activities found assigned to userId: " + userId);
    		}
    		
    	}
        logger.debug(reassignedCount + " tasks reassigned for user id: " + userId);
    }
    
    private void writeReassignNote(ProcessActivity tpa, Integer oldUserId, Integer currentUserId) throws DAOException {
    	// decouple workflow
    	// decouple workflow
    	throw new RuntimeException(" decouple workflow; not implemented yet");
//    	UserDef oldUser = infrastructureDAO().retrieveUserDef(oldUserId);
//    	UserDef newUser = infrastructureDAO().retrieveUserDef(currentUserId);
//        String oldUserName = "unknown";
//        if (oldUser != null && oldUser.getContact() != null) {
//        	if (oldUser.getContact().getLastNm() != null) {
//        		oldUserName = oldUser.getContact().getLastNm() + ", ";
//        	}
//        	if (oldUser.getContact().getFirstNm() != null) {
//        		oldUserName += oldUser.getContact().getFirstNm();
//        	}
//        }
//        String newUserName = "unknown";
//        if (newUser != null && newUser.getContact() != null) {
//        	if (newUser.getContact().getLastNm() != null) {
//        		newUserName = newUser.getContact().getLastNm() + ", ";
//        	}
//        	if (newUser.getContact().getFirstNm() != null) {
//        		newUserName += newUser.getContact().getFirstNm();
//        	}
//        }
//        StringBuffer sb = new StringBuffer();
//        sb.append("Reassign '");
//        sb.append(tpa.getActivityTemplateNm());
//        sb.append("' from user ");
//        sb.append(oldUserName);
//        sb.append(" to ");
//        sb.append(newUserName);
//
//        ProcessNote pn = new ProcessNote();
//        pn.setProcessId(tpa.getProcessId());
//        pn.setUserId(currentUserId);
//        pn.setNote(sb.toString());
//        try {
//        	createProcessNote(pn);
//        } catch (RemoteException re) {
//            logger.error("Cannot create process note.", re);
//        }
    }
     */
	
	public void removeScheduledTrigger(String triggerName, String jobName)
			throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((jobName != null) && (jobName.compareTo("") != 0)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");
			} catch (UnableToStartException utse) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, utse);
				throw new DAOException("Exception for triggerName "
						+ triggerName + " and jobName " + jobName, utse);
			}

			scheduler.removeTrigger(triggerName, jobName);
		}

		return;
	}
}
