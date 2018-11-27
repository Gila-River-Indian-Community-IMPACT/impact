package us.oh.state.epa.stars2.workflow;

import java.awt.Point;
import java.sql.Timestamp;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionDef;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.workflow.engine.ProcessFlow;

/**
 * <p>
 * Title: Activity
 * </p>
 * 
 * <p>
 * Description: This object defines a workflow activity. An Activity is composed
 * of a template--which defines common attributes--and process-specific data.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @version 1.0
 */

public class Activity {
    // Activity status definitions, from "Activity_Status_Def".
    public static final String NOT_DONE = ActivityStatusDef.NOT_COMPLETED;
    public static final String SKIPPED = ActivityStatusDef.SKIPPED;
    public static final String PENDING = ActivityStatusDef.PENDING;
    public static final String IN_PROCESS = ActivityStatusDef.IN_PROCESS;
    public static final String SUBFLOW_IN_PROCESS = ActivityStatusDef.SUBFLOW_IN_PROCESS;
    public static final String COMPLETED = ActivityStatusDef.COMPLETED;
    public static final String BLOCKED = ActivityStatusDef.BLOCKED;
    public static final String REFERRED = ActivityStatusDef.REFERRED;
    public static final String UN_REFERRED = ActivityStatusDef.UNREFERRED;
    public static final String CANCELLED = ActivityStatusDef.CANCELLED;
    public static final String ABANDONED = ActivityStatusDef.ABANDONED;
    public static final String WAIT_FOR_RETRY = "WRTY";

    // Transition definitions, from "Transition_Def". Transitions define
    // the "links" between activities. The proper state of the current
    // Acitivity depends on the state of all of its in-bound activities.
    // The path we choose for the next activity depends on its out-bound
    // Transitions.
    public static final String INLINE = "IL"; // Inbound/outbound Transition.
    public static final String LOOP = "LP"; // Outbound Transition.
    public static final String SPLIT_XOR = "SX"; // Outbound Transition.
    public static final String SPLIT_AND = "SA"; // Outbound Transition.
    public static final String JOIN_XOR = "JX"; // Inbound Transition.
    public static final String JOIN_AND = "JA"; // Inbound Transition.
    // Performer type codes from the "Performer_Type_Def" table.
    public static final String AUTOMATIC_PERFORMER = "A";
    public static final String MANUAL_PERFORMER = "M";
    public static final String SUBFLOW_PERFORMER = "S";
    public static final String DYNAMIC_SF_PERFORMER = "DSF";
    // "Decompose Order" is a special kind of Activity that has its own
    // special way of being handled.
    public static final int DECOMPOSE_ORDER = 1;
    // The name of a DynamicObject field that we expect to find. Usually,
    // this will be the order Id or proposal Id.
    public static final String EXTERNAL_ID = "external-id";
    public static final String SERVICE_ID = "service-id";
    public static final String ACCOUNT_ID = "account-id";
    public static final String PROCESS_CD = "process-cd";
    // Valid values for activity version - support for "Goto" operation
    public static final int CURRENT = 1;
    public static final int OLD = 1;
    // Default values for durations if these values are not set. This is
    // one day (24 * 60 minutes * 60 seconds).
    public static final int DFLT_DURATION = 24 * 60 * 60 * 1000;
    private static int pseudoKey;
    private ProcessFlow process; // The PF that contains this Activity
    private ProcessFlow subflow; // The PF contained BY this Activity
    // An Activity consists of three parts, the Activity Template Definition,
    // e.g., "Decompose Order", "Billing Ready", etc., the Activity Template
    // (built by the Designer as a step in a workflow/salesflow process),
    // and a Process Activity, i.e., instance-specific data for this
    // Activity. We already have database objects that define these values,
    // so I am going to cheat (for now) and use those objects.
    private ActivityTemplateDef atd;
    private ActivityTemplate at;
    private ProcessActivity pa;
    private Integer contactId;

    /**
     * Constructor. The business object uses this constructor after reading
     * objects from the database.
     * 
     * @param at
     *            ActivityTemplate database object.
     * @param atDef
     *            ActivityTemplateDef database object.
     */
    public Activity(ActivityTemplate inAt, ActivityTemplateDef atDef) {
        at = inAt;
        atd = atDef;
    }

    /**
     * Constructor. Used by the application to create a new Activity to be
     * inserted into the process template currently being edited. The
     * "pseudoKey" should be set to value less than zero to indicate that the
     * object has not been inserted into the database yet. Later, when the
     * object is actually stored to the database, it will be given a database
     * key.
     * 
     * @param pseudoKey
     *            A placeholder key. This should be unique.
     * @param atDef
     *            Associated ActivityTemplateDef object.
     */
    public Activity(Integer pseudoKey, ActivityTemplateDef atDef) {
        atd = atDef;

        // Create the object and give it some default values.

        at = new ActivityTemplate();

        at.setActivityTemplateId(pseudoKey);
        at.setActivityTemplateDefId(atDef.getActivityTemplateDefId());
        at.setActivityTemplateNm(atDef.getActivityTemplateNm());
        at.setDeprecatedInd("N");
        at.setExpectedDuration(atDef.getExpectedDuration());
        at.setJeopardyDuration(atDef.getJeopardyDuration());
        at.setInitTaskInd("N");
        at.setInTransitionDefCd(Transition.NOLOOP);
        at.setMilestoneInd("N");
        at.setAggregate("N");
        at.setOutTransitionDefCd(Transition.NOLOOP);
        at.setProcessTemplateId(null);
        at.setProcessTemplateNm("");
        at.setServiceId(null);
        at.setXloc(new Integer(Utils.ACT_START_X));
        at.setYloc(new Integer(Utils.ACT_START_Y));
    }

    /**
     * Constructor. This constructor is used by the business object to construct
     * an <code>Activity</code> on behalf of a <tt>ProcessFlow</tt>.
     * 
     * @param container
     *            The ProcessFlow that contains this Activity.
     * @param pa
     *            ProcessActivity object from the database.
     * @param at
     *            ActivityTemplate object from the database.
     * @param atd
     *            ActivityTemplateDef object from the database.
     */
    public Activity(ProcessFlow container, ProcessActivity inPa,
            ActivityTemplate inAt, ActivityTemplateDef inAtd) {
        process = container;
        pa = inPa;
        at = inAt;
        atd = inAtd;

        // Initialize activity-specific instance data with the performer type
        // from the template. We may override the activity-specific code
        // later on during processing.

        setPerformerTypeCd(atd.getPerformerTypeCd());
    }

    /**
     * Associates the ProcessFlow <code>subProcess</code> with this Activity
     * as a sub-flow. This means that this Activity will not be completed until
     * its sub-flow is completed. Activity objects that contain sub-flows are
     * essentially "place-holder" activities in their own ProcessFlow. Note that
     * if <code>subProcess</code> is not null, then <code>subProcess</code>
     * will automatically have its parent Activity set to this object.
     * 
     * @param subProcess
     *            ProcessFlow that is a sub-flow of this Activity.
     */
    public final void setSubFlow(ProcessFlow subProcess) {
        subflow = subProcess;

        if (subProcess != null) {
            subProcess.setParentActivity(this);
            setPerformerTypeCd(Activity.SUBFLOW_PERFORMER);
        }
    }

    /**
     * Returns the current sub-flow for this Activity. If no sub-flow is
     * defined, then returns "null".
     * 
     * @return ProcessFlow sub-flow of this Activity.
     */
    public final ProcessFlow getSubFlow() {
        return subflow;
    }

    /**
     * Returns the parent ProcessFlow for this Activity. All Activities require
     * a parent or container ProcessFlow, so this value will always be set.
     * 
     * @return The parent ProcessFlow.
     */
    public final ProcessFlow getContainer() {
        return process;
    }

    /**
     * Sets the performer type code for this Activity to a new value. Note that
     * the type code must correspond to a value from the "Performer_Type_Def"
     * table (See PERFORMER constants for this class) or this Activity cannot be
     * written to the database.
     * 
     * @param newCd
     *            New performer type code.
     */
    public final void setPerformerTypeCd(String newCd) {
        pa.setPerformerTypeCd(newCd);
    }

    /**
     * Returns the performer type code for this Activity. These values will be
     * one of the entries in the "performer_type_cd" column of the
     * Peformer_Type_Def table. All Activities will have this value.
     * 
     * @return performer type code.
     */
    public final String getPerformerTypeCd() {
        return pa.getPerformerTypeCd();
    }

    /**
     * Returns the Id of the activity template used to create this Activity.
     * This value coupled with the process Id provides a unique database key for
     * this object.
     * 
     * @return activity template Id.
     */
    public final Integer getActivityTemplateId() {
        return at.getActivityTemplateId();
    }

    /**
     * Returns the Id of the process that contains this Activity. This is part
     * of the database key for this object.
     * 
     * @return process Id.
     */
    public final Integer getProcessId() {
        return pa.getProcessId();
    }

    /**
     * Returns the loopCnt that contained in this Activity. This is part of the
     * database key for this object.
     * 
     * @return loop_cnt.
     */
    public final Integer getLoopCnt() {
        return pa.getLoopCnt();
    }

    /**
     * Returns the current that contained in this Activity. This is part of the
     * database key for this object.
     * 
     * @return loop_cnt.
     */
    public final String getCurrent() {
        return pa.getCurrent();
    }

    /**
     * Returns the name of the template for this activity.
     * 
     * @return activity template name.
     */
    public final String getActivityName() {
        return at.getActivityTemplateNm();
    }

    /**
     * Returns the loopCnt that contained in this Activity. This is part of the
     * database key for this object.
     * 
     * @return loop_cnt.
     */
    public final void setLoopCnt(Integer loopCnt) {
        pa.setLoopCnt(loopCnt);
    }

    /**
     * Returns the loopCnt that contained in this Activity. This is part of the
     * database key for this object.
     * 
     * @return loop_cnt.
     */
    public final void setCurrent(String current) {
        pa.setCurrent(current);
    }

    /**
     * Sets the start date and time for this activity. This should be done when
     * the activity is checked out.
     * 
     * @param t
     *            Start date.
     */
    public final void setStartDt(Timestamp t) {
        pa.setStartDt(t);
    }

    /**
     * Returns the start date and time for this Activity. This value will be
     * "null" if the Activity has not been processed in any way.
     * 
     * @return start date.
     */
    public final Timestamp getStartDt() {
        return pa.getStartDt();
    }

    /**
     * Sets the ready date and time for this activity. This should be done when
     * the activity is made ready for processing.
     * 
     * @param t
     *            ready date.
     */
    public final void setReadyDt(Timestamp t) {
        pa.setReadyDt(t);
    }

    /**
     * Returns the ready date and time for this Activity. This value will be
     * "null" if the Activity has not been readied.
     * 
     * @return ready date.
     */
    public final Timestamp getReadyDt() {
        return pa.getReadyDt();
    }

    /**
     * Sets the due date and time for this activity. This calculated as the sum
     * of "right now" plus the expected duration of this activity. The due date
     * should be set when the activity is first checked out for processing.
     * 
     * @param t
     *            due date.
     */
    public final void setDueDt(Timestamp t) {
        pa.setDueDt(t);
    }

    /**
     * Returns the due date and time for this Activity. This value will be
     * "null" if the Activity has not been checked out yet.
     * 
     * @return due date.
     */
    public final Timestamp getDueDt() {
        return pa.getDueDt();
    }

    /**
     * Sets the end date and time for this activity. This should be set when the
     * Activity is completed.
     * 
     * @param t
     *            end date.
     */
    public final void setEndDt(Timestamp t) {
        pa.setEndDt(t);
    }

    /**
     * Returns the end date and time for this Activity. This value will be
     * "null" if the Activity has not been checked in yet.
     * 
     * @return end date.
     */
    public final Timestamp getEndDt() {
        return pa.getEndDt();
    }

    /**
     * Sets the jeopardy date and time for this activity. This should be set
     * when the Activity is checked out, and cleared if the activity is released
     * from check out.
     * 
     * @param t
     *            jeopardy date.
     */
    public final void setJeopardyDt(Timestamp t) {
        pa.setJeopardyDt(t);
    }

    /**
     * Returns the jeopardy date and time for this Activity. This value will be
     * "null" if the Activity has not been checked in yet.
     * 
     * @return jeopardy date.
     */
    public final Timestamp getJeopardyDt() {
        return pa.getJeopardyDt();
    }

    /**
     * Returns the name of the class that will handle automatic processing. This
     * is valid only for automatic Activities. This is always a fully specified
     * class name, e.g., "us.oh.state.epa.stars2.workflow.CloseOrder".
     * 
     * @return automatic class name.
     */
    public final String getAutomaticClassName() {
        return atd.getAutomaticClassNm();
    }

    /**
     * Returns the type of outbound transitions from this Activity. These values
     * are defined in the "transition_def_cd" column of the "Transition_Def"
     * table.
     * 
     * @return String transition type code.
     */
    public final String getOutboundTransitionType() {
        return at.getOutTransitionDefCd();
    }

    /**
     * Returns the expected duration for this activity in milliseconds.
     * 
     * @return int Expected duration.
     */
    public final long getExpectedDuration() {
        long dur = Activity.DFLT_DURATION;

        Integer expDur = at.getExpectedDuration();

        if (expDur != null) {
            dur = expDur;
            dur = dur*1000;
        }

        return dur;
    }

    /**
     * Returns the jeopardy duration for this activity in milliseconds.
     * 
     * @return int Jeopardy duration.
     */
    public final long getJeopardyDuration() {
        long dur = Activity.DFLT_DURATION;

        Integer jeoDur = at.getJeopardyDuration();

        if (jeoDur != null) {
            dur = jeoDur;
            dur = dur*1000;
        }

        return dur;
    }

    /**
     * Returns the Id of the process template used to construct this Activity.
     * 
     * @return process template Id.
     */
    public final Integer getProcessTemplateId() {
        return at.getProcessTemplateId();
    }

    /**
     * Returns the Id of the service being altered by this Activity. This could
     * be "null" if this Activity does not directly alter a service.
     * 
     * @return service Id.
     */
    public final Integer getServiceId() {
        return at.getServiceId();
    }

    /**
     * Returns the Id of the service process template for this Activity.
     * 
     * @return service process template Id.
     */
    public final Integer getServiceProcessTemplateId() {
        return atd.getProcessTemplateId();
    }

    /**
     * Returns "true" if this is the initial task for its containing
     * ProcessFlow. An initial task is defined as a task that has no inbound
     * Transitions.
     * 
     * @return boolean "true" if this is an initial task.
     */
    public final boolean isInitTask() {
        return at.isInitTask();
    }

    /**
     * Returns "true" if this Activity is finished, either "completed" or
     * "skipped". Otherwise, returns "false".
     * 
     * @return boolean "true" if this Activity is done.
     */
    public final boolean isFinished() {
        boolean ret = false;
        String cs = getStatusCd();

        if (cs.equals(Activity.COMPLETED) || cs.equals(Activity.SKIPPED)) {
            ret = true;
        }

        if (at.getOutTransitionDefCd().equals(Activity.LOOP)) {
            ret = true;
        }

        return ret;
    }

    /**
     * Returns the number of remaining automatic retry attempts for this
     * Activity. When an attempt to execute an automatic Activity fails, an
     * automatic retry of the Activity should be scheduled if this value is
     * greater than zero. This method will not return "null". Instead, it will
     * return an Integer with value zero.
     * 
     * @return Integer Number of remaining retry attempts.
     */
    public final Integer getNumberOfRetries() {
        Integer foo = pa.getNumberOfRetries();

        if (foo == null) {
            foo = new Integer(0);
        }

        return foo;
    }

    /**
     * Sets the number of remaining automatic retry attempts for this Activity.
     * This value does not apply to manual Activities. Setting this parameter to
     * a value less than or equal to zero terminates any future automatic
     * retries.
     * 
     * @param numRt
     *            Integer Number of automatic retry attempts.
     */
    public final void setNumberOfRetries(Integer numRt) {
        pa.setNumberOfRetries(numRt);
    }

    /**
     * Returns the current retry interval in minutes for automatic retry
     * attempts. This method never returns "null". If no retry interval is
     * specified, returns zero.
     * 
     * @return Integer Autmatic retry interval (minutes).
     */
    public final Integer getRetryInterval() {
        Integer foo = pa.getRetryInterval();

        if (foo == null) {
            foo = new Integer(0);
        }

        return foo;
    }

    /**
     * Sets the current retry interval in minutes for automatic retry attempts,
     * in minutes. The method assumes you are smart enough not to set this to a
     * negative value. This parameter has no effect on a manual Activity.
     * 
     * @param retryIntvl
     *            Integer Automatic retry interval (minutes).
     */
    public final void setRetryInterval(Integer retryIntvl) {
        pa.setRetryInterval(retryIntvl);
    }

    public final void validate() throws Exception {

    }

    // ********************************************************************
    // The following methods are intended for use by DAOs and business
    // objects only. If Java had a "friend" mechanism, these methods would
    // not be public.
    // ********************************************************************

    public final ProcessActivity getProcessActivity() {
        return pa;
    }

    public final void setProcessActivity(ProcessActivity pa) {
        this.pa = pa;
    }

    public final ActivityTemplate getActivityTemplate() {
        return at;
    }

    public final ActivityTemplateDef getActivityTemplateDef() {
        return atd;
    }

    public final String getStatusCd() {
        return pa.getActivityStatusCd();
    }

    public final void setStatusCd(String status) {
        pa.setActivityStatusCd(status);
    }

    public final Integer getUserId() {
        return pa.getUserId();
    }

    public final void setUserId(Integer userId) {
        pa.setUserId(userId);
    }

    /**
     * Returns the location of this object in "logical" coordinates, i.e., "x"
     * and "y" range from 0 to 1000.
     * 
     * @return Point (x, y) location for this object.
     */
    public final Point getUpperLeft() {
        Integer x = at.getXloc();
        Integer y = at.getYloc();

        Point p = new Point(x.intValue(), y.intValue());
        return p;
    }

    /**
     * Returns the name of this object. This is what gets displayed in the
     * corresponding graphical object.
     * 
     * @return String name.
     */
    public final String getName() {
        return atd.getActivityTemplateNm();
    }

    /**
     * Returns the transition code for the output of this object. The transition
     * code determines whether or not the Activity has multiple output
     * Transitions, and how they should behave (see description of the
     * Transition class).
     * 
     * @return String Output transition code.
     */
    public final String getOutTransitionCode() {
        return at.getOutTransitionDefCd();
    }

    /**
     * Returns the "activity Id" for this object. This is the key value. A
     * pseudo-key will have a value less than zero. A database key will have a
     * value greater than zero.
     * 
     * @return Integer Activity Id.
     */
    public final Integer getActivityId() {
        return at.getActivityTemplateId();
    }

    /**
     * Returns the activity template def Id for this object. This represents the
     * foreign key relationship between Activity_Template (an instance of an
     * Activity template) and Activity_Template_Def (a class or kind of Activity
     * template).
     * 
     * @return Integer Activity Template Def Id.
     */
    public final Integer getActivityTemplateDefId() {
        return atd.getActivityTemplateDefId();
    }

    /**
     * Returns the X location of this object in logical coordinates, i.e., a
     * value from 0 to 1000 where 1000 represents the full width of the drawing
     * area.
     * 
     * @return Integer X location.
     */
    public final Integer getXloc() {
        return at.getXloc();
    }

    /**
     * Sets the X location of this object in logical coordinates, i.e., a value
     * from 0 to 1000 where 1000 represents the full width of the drawing area.
     * 
     * @param xloc
     *            Integer logical x location.
     */
    public final void setXloc(Integer xloc) {
        at.setXloc(xloc);
    }

    /**
     * Returns the Y location of this object in logical coordinates, i.e., a
     * value from 0 to 1000 where 1000 represents the full height of the drawing
     * area.
     * 
     * @return Integer Y location.
     */
    public final Integer getYloc() {
        return at.getYloc();
    }

    /**
     * Sets the Y location of this object in logical coordinates, i.e., a value
     * from 0 to 1000 where 1000 represents the full height of the drawing area.
     * 
     * @param yloc
     *            Integer logical y location.
     */
    public final void setYloc(Integer yloc) {
        at.setYloc(yloc);
    }

    public final String getActivityData() {
        return at.getActivityData();
    }

    public final void setActivityData(String adata) {
        at.setActivityData(adata);
    }

    public final String getPerformerTypeDsc() {
        return atd.getPerformerTypeDsc();
    }

    public final String getTerminalInd() {
        return atd.getTerminalInd();
    }

    public final String getMilestoneInd() {
        return at.getMilestoneInd();
    }

    public final void setMilestoneInd(String ind) {
        at.setMilestoneInd(ind);
    }

    public final String getInitTaskInd() {
        return at.getInitTaskInd();
    }

    public final void setInitTaskInd(String ind) {
        at.setInitTaskInd(ind);
    }

    public final String getInTransitionDefCd() {
        return at.getInTransitionDefCd();
    }

    public final void setInTransitionDefCd(String cd) {
        at.setInTransitionDefCd(cd);
    }

    public final String getOutTransitionDefCd() {
        return at.getOutTransitionDefCd();
    }

    public final void setOutTransitionDefCd(String cd) {
        at.setOutTransitionDefCd(cd);
    }

    public final void setExpectedDuration(Integer dur) {
        at.setExpectedDuration(dur);
    }

    public final void setJeopardyDuration(Integer dur) {
        at.setJeopardyDuration(dur);
    }

    public final String getAggregate() {
        return at.getAggregate();
    }

    public final void setAggregate(String agg) {
        at.setAggregate(agg);
    }

    public final String getRoleCd() {
        return at.getRoleCd();
    }

    public final void setRoleCd(String cd) {
        at.setRoleCd(cd);
    }

    public final String[] getDependentDetailNames() {
        return atd.getDependentDetailNames();
    }

    public final Integer[] getDependentDetailIds() {
        return atd.getDependentDetailIds();
    }

    public final boolean isSubflow() {
        boolean rslt = false;
        String pcode = atd.getPerformerTypeCd();

        if ((pcode != null) && (pcode.equals("S"))) {
            rslt = true;
        }

        return rslt;
    }

    public final boolean isDynamicSubflow() {
        boolean rslt = false;
        String pcode = atd.getPerformerTypeCd();

        if ((pcode != null) && (pcode.equals("DSF"))) {
            rslt = true;
        }

        return rslt;
    }

    public final SubFlow createSubFlow() {
        return new SubFlow(at, atd);
    }

    /**
     * Returns all of the available Transition definitions for Activities. These
     * method returns all available definitions. The application may want to
     * filter this list to input transitions and/or output transitions.
     * 
     * @return TransitionDef[] The currently available Transition definitions.
     */
    public final TransitionDef[] getAllTransitionDefs() {
        // BLATANT HACK WARNING: These values should probably be read in
        // from the database. However, I don't want to spawn another
        // SwingWorker, write the DAO code, and adjust the schema and
        // database generation scripts to to that right now. So, I am
        // faking it.
        TransitionDef[] tdefs = new TransitionDef[7];

        // Transition Type are applicable to Input transition or output
        // transition or both.
        tdefs[0] = new TransitionDef(INLINE, "In Line", true, true);

        // Joins are input only.
        tdefs[1] = new TransitionDef(JOIN_AND, "Join-And", true, false);
        tdefs[2] = new TransitionDef(JOIN_XOR, "Join-XOr", true, false);

        // Loop and No Loop are both input and output.
        tdefs[3] = new TransitionDef("LP", "Loop", true, true);
        tdefs[4] = new TransitionDef("NL", "No Loop", true, true);

        // Splits are output only.
        tdefs[5] = new TransitionDef(SPLIT_AND, "Split-And", false, true);
        tdefs[6] = new TransitionDef(SPLIT_XOR, "Split-XOr", false, true);

        return tdefs;
    }

    /**
     * Returns all available input TransitionDefs.
     * 
     * @return TransitionDef[] All available input TransitionDefs.
     */
    public final TransitionDef[] getInputTransitionDefs() {
        ArrayList<TransitionDef> ret = new ArrayList<TransitionDef>();
        TransitionDef[] allTrans = getAllTransitionDefs();

        if ((allTrans != null) && (allTrans.length != 0)) {
            for (TransitionDef td : allTrans) {
                if (td.isInputTransition()) {
                    ret.add(td);
                }
            }
        }

        return ret.toArray(new TransitionDef[0]);
    }

    /**
     * Returns all available output TransitionDefs.
     * 
     * @return TransitionDef[] All available output TransitionDefs.
     */
    public final TransitionDef[] getOutputTransitionDefs() {
        ArrayList<TransitionDef> ret = new ArrayList<TransitionDef>();
        TransitionDef[] allTrans = getAllTransitionDefs();

        if ((allTrans != null) && (allTrans.length != 0)) {
            for (TransitionDef td : allTrans) {
                if (td.isOutputTransition()) {
                    ret.add(td);
                }
            }
        }

        return ret.toArray(new TransitionDef[0]);
    }

    public final void setActivityTemplate(ActivityTemplate actTemplate) {
        at = actTemplate;
    }

    /**
     * Method to generate a new, unique pseudo-key value. Pseudo-keys are always
     * less than zero and need to be replace with database keys whenever the
     * Activity is inserted into the database.
     * 
     * @return Integer pseudo-key value.
     */
    synchronized public static Integer getPseudoKey() {
        pseudoKey--;
        return new Integer(pseudoKey);
    }

    public final void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public final Integer getContactId() {
        return contactId;
    }

    public final void setExtendProcessEndDate(String extendProcessEndDate) {
        pa.setExtendProcessEndDate(extendProcessEndDate);
    }

    public final void setActivityReferralTypeId(Integer activityReferralTypeId) {
        pa.setActivityReferralTypeId(activityReferralTypeId);
    }

    public final String getExtendProcessEndDate() {
        return pa.getExtendProcessEndDate();
    }
}
