package us.oh.state.epa.stars2.workflow.engine;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;

import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.workflow.Activity;

/**
 * <p>
 * Title: ProcessFlow
 * </p>
 * 
 * <p>
 * Description: This class describes an active workflow process. It contains
 * Activity objects, Transition objects, and DataField objects. The Activities
 * are the individual steps in the workflow. Transitions link Activities
 * together. DataFields contain values that may be used to make decisions about
 * which Transition to take which moving from a completed Activity to the next
 * Activity.
 * </p>
 * 
 * <p>
 * A ProcessFlow can also be a child process of another Activity in a parent
 * ProcessFlow. A ProcessFlow may also have sub-flows.
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
 * @version 1.0
 */

public class ProcessFlow {
    private Activity parentActivity;
    private ProcessFlow parentProcess;
    private WorkFlowProcess process;
    private Hashtable<Integer, Activity> activities = new Hashtable<Integer, Activity>();
    private Hashtable<String, Transition> transitions = new Hashtable<String, Transition>();
    private Hashtable<String, DataField> dataFields = new Hashtable<String, DataField>();
    private Long threadId;

    /**
     * Constructor. Only WorkFlow Business Object should construct ProcessFlow
     * objects.
     * 
     * @param p
     *            Process data from the database.
     */
    public ProcessFlow(WorkFlowProcess p) {
        process = p;
    }

    /**
     * Sets the parent Activity for this object to "parent". That makes this
     * ProcessFlow a sub-flow of that Activity.
     * 
     * @param parent
     *            Parent Activity.
     */
    public final void setParentActivity(Activity parent) {
        // Update the "logical" view of the relationship, i.e., the view from
        // inside the workflow engine.

        parentActivity = parent;

        // Update the database "hook" or the part we persist to the database.

        if (parent != null) {
            Integer parentActTempId = parent.getActivityTemplateId();
            Integer parentProcId = parent.getProcessId();

            process.setActivityTemplateId(parentActTempId);
            process.setParentProcessId(parentProcId);
        }
    }

    /**
     * Returns the first Activity for this ProcessFlow.
     * 
     * @return Activity
     */
    public final Activity getFirstActivity() {
        Activity act = null;

        for (Integer key : activities.keySet()) {
            act = activities.get(key);
            if (act.isInitTask()) {
                break;
            }
        }

        return act;
    }

    /**
     * Returns the parent Activity for this ProcessFlow. If this ProcessFlow is
     * not a sub-flow, i.e., has no parent Activity, then "null" is returned.
     * 
     * @return Parent Activity.
     */
    public final Activity getParentActivity() {
        return parentActivity;
    }

    /**
     * Returns the process Id for this object. The process Id is the database
     * key for this object.
     * 
     * @return process Id.
     */
    public final Integer getProcessId() {
        return process.getProcessId();
    }

    /**
     * Returns the process code for this process. This corresponds to a
     * "process_cd" value in the Process_Def table. Process codes identify the
     * type of process, either "prov" (Provisioning), "sfa" (Sales Flow), etc.
     * 
     * @return Process code.
     */
    public final String getProcessCd() {
        return process.getProcessCd();
    }

    /**
     * Adds an Activity to this ProcessFlow. Note that the Activity has a
     * two-field key, "process Id" and "activity template Id". The Activity "a"
     * should have the same "process Id" value as this object. However, that
     * constraint is not verified or enforced by this method. The Activity
     * itself is not altered by addition to this ProcessFlow.
     * 
     * @param a
     *            Activity to be added to this ProcessFlow.
     */
    public final void addActivity(Activity a) {
        activities.put(a.getActivityTemplateId(), a);
    }

    /**
     * Returns the process Id of any associated parent ProcessFlow. The parent
     * ProcessFlow also contains the parent Activity. If this process has no
     * parent, then "null" is returned.
     * 
     * @return parent Process Id.
     */
    public final Integer getParentProcessId() {
        return process.getParentProcessId();
    }

    /**
     * Returns the activity template Id of the parent activity (if there is
     * one). Using this value coupled with the value returned by
     * "getParentProcessId()", the parent Activity is uniquely specified. If
     * there is no parent Activity, then this method returns "null".
     * 
     * @return parent activity template Id.
     */
    public final Integer getParentActivityTemplateId() {
        return process.getActivityTemplateId();
    }

    /**
     * Returns the Id of the process template used to create this ProcessFlow.
     * 
     * @return process template Id.
     */
    public final Integer getProcessTemplateId() {
        return process.getProcessTemplateId();
    }

    /**
     * Assigns a parent process to this process. This process becomes a sub-flow
     * of the parent process.
     * 
     * @param parent
     *            parent process.
     */
    public final void setParentProcess(ProcessFlow parent) {
        parentProcess = parent;
    }

    /**
     * Returns the parent process of this process. If this ProcessFlow has no
     * parent, then "null" is returned.
     * 
     * @return parent ProcessFlow.
     */
    public final ProcessFlow getParentProcess() {
        return parentProcess;
    }

    /**
     * Returns the service Id of the service being altered by this ProcessFlow.
     * If this is a provisioning ("prov") flow, then this value identifies a
     * Service in the Service table. If this is a sales flow ("sfa"), then the
     * Service exists only in the Ord_Service table (ordering subsystem). The
     * order or proposal Id (external Id) is also needed to access the service
     * in the Ord_Service table.
     * 
     * @return service Id.
     */
    public final Integer getServiceId() {
        return process.getServiceId();
    }

    /**
     * Returns the external Id for this ProcessFlow. This is either an order Id
     * or a proposal Id.
     * 
     * @return order/proposal Id.
     */
    public final Integer getExternalId() {
        return process.getExternalId();
    }

    /**
     * Sets the expedite flag for this ProcessFlow. Note that this does not
     * update this value in the database; it simply updates the in-memory value.
     * 
     * @param newId
     *            Integer New order/proposal Id.
     */
    public final void setExpedite(String expedite) {
        process.setExpedite(expedite);
    }

    /**
     * Returns the expedite flag for this ProcessFlow.
     * 
     * @return order/proposal Id.
     */
    public final String getExpedite() {
        return process.getExpedite();
    }

    /**
     * Returns the User ID for this ProcessFlow.
     * 
     * @return order/proposal Id.
     */
    public final Integer getUserId() {
        return process.getUserId();
    }

    /**
     * Sets the external Id for this ProcessFlow. Note that this does not update
     * this value in the database; it simply updates the in-memory value.
     * 
     * @param newId
     *            Integer New order/proposal Id.
     */
    public final void setExternalId(Integer newId) {
        process.setExternalId(newId);
    }

    /**
     * Returns the account Id for the account associated with this ProcessFlow.
     * 
     * @return account Id.
     */
    public final Integer getAccountId() {
        return process.getFacilityId();
    }

    /**
     * Returns the starting date for this ProcessFlow. If this value has not
     * been assigned, then returns "null".
     * 
     * @return Timestamp start date.
     */
    public final Timestamp getStartDt() {
        return process.getStartDt();
    }

    /**
     * Sets the start date for this ProcessFlow.
     * 
     * @param ts
     *            start date.
     */
    public final void setStartDt(Timestamp ts) {
        process.setStartDt(ts);
    }

    /**
     * Returns the ending date for this ProcessFlow. If this value has not been
     * assigned, then returns "null".
     * 
     * @return Timestamp start date.
     */
    public final Timestamp getEndDt() {
        return process.getEndDt();
    }

    /**
     * Sets the end date for this ProcessFlow.
     * 
     * @param ts
     *            end date.
     */
    public final void setEndDt(Timestamp ts) {
        process.setEndDt(ts);
    }

    /**
     * Returns the ready date for this ProcessFlow. If this value is non-null,
     * the ProcessFlow should not be "readied" until this date is acheived.
     * 
     * @return Timestamp ready date.
     */
    public final Timestamp getReadyDt() {
        return process.getReadyDt();
    }

    /**
     * Sets the ready date for this ProcessFlow. This value should only be set
     * to a non-null value if the ProcessFlow needs to wait until this date to
     * be readied for execution.
     * 
     * @param ts
     *            ready date.
     */
    public final void setReadyDt(Timestamp ts) {
        process.setReadyDt(ts);
    }

    /**
     * Returns the due date for this ProcessFlow. If this value is non-null, the
     * ProcessFlow should not be "readied" until this date is acheived.
     * 
     * @return Timestamp due date.
     */
    public final Timestamp getDueDt() {
        return process.getDueDt();
    }

    /**
     * Sets the due date for this ProcessFlow. This value should only be set to
     * a non-null value if the ProcessFlow needs to wait until this date to be
     * readied for execution.
     * 
     * @param ts
     *            due date.
     */
    public final void setDueDt(Timestamp dueDt) {
        process.setDueDt(dueDt);
    }

    public final Timestamp getJeopardyDt() {
        return process.getJeopardyDt();
    }

    public final void setJeopardyDt(Timestamp jeopardyDt) {
        process.setJeopardyDt(jeopardyDt);
    }

    /**
     * Returns a <tt>HashMap</tt> all of the current DataFields associated
     * with this ProcessFlow, along with their values. The key on the HashMap is
     * the DataField name. The value is the DataField value. If this ProcessFlow
     * has no associated DataFields, then an empty HashMap is returned.
     * 
     * @return HashMap containing DataField name-value pairs.
     */
    public final HashMap<String, String> getDataAsHashMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        for (String key : dataFields.keySet()) {
            map.put(key, dataFields.get(key).toString());
        }

        return map;
    }

    /**
     * Returns all of the Transition objects that link the output of Activity
     * "a" to the inputs of other Activity objects within this ProcessFlow. If
     * there are no outbound Transitions, then an empty array is returned.
     * 
     * @param a
     *            Activity whose outbound Transitions are being requested.
     * 
     * @return An array of all outbound Transitions from "a".
     */
    public final Transition[] getOutboundTransitions(Activity a) {
        ArrayList<Transition> ret = new ArrayList<Transition>();

        Integer actId = a.getActivityTemplateId();

        // Iterate over all of the Transitions in this process. Select
        // every Transition whose "from Id" matches the "Id" of "a".
        for (Transition t : transitions.values()) {
            if (actId.compareTo(t.getFromId()) == 0) {
                ret.add(t);
            }
        }

        return ret.toArray(new Transition[0]);
    }

    /**
     * Returns all of the Transition objects that link the input of Activity "a"
     * to the outputs of other Activity objects within this ProcessFlow. If
     * there are no inbound Transitions, then an empty array is returned.
     * 
     * @param a
     *            Activity whose inbound Transitions are being requested.
     * 
     * @return An array of all inbound Transitions from "a".
     */
    public final Transition[] getInboundTransitions(Activity a) {
        ArrayList<Transition> ret = new ArrayList<Transition>();

        Integer actId = a.getActivityTemplateId();

        // Iterate over all of the Transitions in this process. Select
        // every Transition whose "to Id" matches the "Id" of "a".
        for (Transition t : transitions.values()) {
            if (actId.compareTo(t.getToId()) == 0) {
                ret.add(t);
            }
        }

        return ret.toArray(new Transition[0]);
    }

    /**
     * Returns all of the Activity objects that are inputs to Activity "a"
     * within this ProcessFlow. If there are no input activities, then an empty
     * array is returned.
     * 
     * @param a
     *            Activity whose inbound Transitions are being requested.
     * 
     * @return An array of all inbound Transitions from "a".
     */
    public final Activity[] getInputsTo(Activity a) {
        ArrayList<Activity> ret = new ArrayList<Activity>();

        Integer actId = a.getActivityTemplateId();

        // Iterate over all of the Transitions in this process. Select
        // every Transition whose "to Id" matches the "Id" of "a".
        for (Transition t : transitions.values()) {
            Integer toId = t.getToId();

            if (toId.compareTo(actId) != 0) {
                continue;
            }

            Integer fromId = t.getFromId();
            Activity from = getActivity(fromId);
            ret.add(from);
        }

        return ret.toArray(new Activity[0]);
    }

    /**
     * Returns the Activity from this ProcessFlow identified by
     * "activityTemplateId". If no matching value is found, then "null" is
     * returned.
     * 
     * @param activityTemplateId
     *            Unique activity identifier in this process.
     * 
     * @return Activity corresponding to this Id.
     */
    public final Activity getActivity(Integer activityTemplateId) {
        return activities.get(activityTemplateId);
    }

    /**
     * Returns an array of all Activity objects contained in this ProcessFlow.
     * In the unlikely event that this ProcessFlow has no Activities, then an
     * empty array is returned.
     * 
     * @return an array of all Activities contained in this process.
     */
    public final Activity[] getActivities() {
        return new ArrayList<Activity>(activities.values())
                .toArray(new Activity[0]);
    }

    /**
     * Removes Activity "deadmeat" from this object. Returns "true" if the
     * Activity was found in this object and removed. Returns "false" if the
     * Activity was not found in this object. Any inbound or outbound
     * Transitions to/from this Activity are also removed. Note that this object
     * is NOT persisted to the database. If the state of this object needs to be
     * persisted, it will need to be done elsewhere.
     * 
     * @param deadmeat
     *            Activity to be removed from this ProcessFlow.
     * 
     * @return boolean "true" if the Activity was found and removed.
     */
    public final boolean removeActivity(Activity deadmeat) {
        boolean ret = false;

        // Get the Activity Template Id from "deadmeat" and see if we can
        // retrieve that Activity from our mapping.

        Integer activityTemplateId = deadmeat.getActivityTemplateId();
        Object o = activities.remove(activityTemplateId);

        if (o != null) {
            Activity a = (Activity) o;
            Transition[] deadInbounds = getInboundTransitions(a);

            if ((deadInbounds != null) && (deadInbounds.length > 0)) {
                for (Transition t : deadInbounds) {
                    removeTransition(t);
                }
            }

            Transition[] deadOutbounds = getOutboundTransitions(a);

            if ((deadOutbounds != null) && (deadOutbounds.length > 0)) {
                for (Transition t : deadOutbounds) {
                    removeTransition(t);
                }
            }
            ret = true;
        }

        return ret;
    }

    /**
     * Removes Transition "deadmeat" from this object. Returns "true" if the
     * Transition was found in this object and removed. Returns "false" if the
     * Transition was not found in this object. Note that only the Transition is
     * removed. The Activities on either end of the Transition are not altered
     * by this method. Also, this object is NOT persisted to the database. If
     * the state of this object needs to be persisted, it will need to be done
     * elsewhere.
     * 
     * @param deadmeat
     *            Transition to be removed from this ProcessFlow.
     * 
     * @return boolean "true" if the Transition was found and removed.
     */
    public final boolean removeTransition(Transition deadmeat) {
        return (transitions.remove(deadmeat.getTransName()) != null);
    }

    /**
     * Performs a series of consistency checks to verify that this process flow
     * is valid. This method should be called after adding all Activity,
     * DataField and Transition objects to this ProcessFlow to insure that it is
     * correct and usable. If this method returns without throwing an Exception,
     * then this ProcessFlow is valid.
     * 
     * @throws java.lang.Exception
     *             ProcessFlow is invalid.
     */
    public final void validate() throws Exception {
        // Activities will validate themselves.
        for (Activity a : activities.values()) {
            a.validate();
        }

        // Verify the Transitions reference Activities and DataFields in this
        // ProcessFlow.

        for (Transition t : transitions.values()) {
            // A Transition is valid if it references valid Activities in this
            // ProcessFlow. If a given Activity does not exist, throw an
            // Exception.
            Activity a = getActivity(t.getToId());

            if (a == null) {
                throw new Exception("Invalid TO transition: " + t.toString());
            }

            a = getActivity(t.getFromId());

            if (a == null) {
                throw new Exception("Invalid FROM transition: " + t.toString());
            }

            // Referential Integrity test: if this Transition has a
            // "condition" make sure the condition variable refers to
            // a DataField associated with this ProcessFlow.
            if (t.getDataName() != null) {
                DataField df = dataFields.get(t.getDataName());

                if (df == null) {
                    throw new Exception("Invalid condition variable: "
                            + t.toString());
                }
            }
        }
    }

    /**
     * Adds an array of Activity objects to this ProcessFlow. The Activity
     * objects are each verified to be proper components of this ProcessFlow,
     * i.e., the process Id of the activity is the same as the process Id for
     * this object. If they are not, then an exception is thrown.
     * 
     * @param acts
     *            An array of Activity objects.
     * 
     * @throws Exception
     *             An Activity in the array does not belong to this process.
     */
    public final void addActivities(Activity[] acts) throws Exception {
        // If we have no incoming Activities, we have nothing to do.
        if ((acts != null) && (acts.length != 0)) {
            // For (Each Activity in the input array) Verify that it really
            // belongs to this Process. If not, throw an Exception. Otherwise,
            // put it in our Hashtable for easy lookup.
            for (Activity a : acts) {
                Integer aProcId = a.getProcessId();

                if (aProcId.compareTo(getProcessId()) != 0) {
                    throw new Exception("Activity name = ["
                            + a.getActivityName() + "], process Id = "
                            + aProcId.toString()
                            + "is not part of Process Id = "
                            + getProcessId().toString() + ".");
                }

                activities.put(a.getActivityTemplateId(), a);
            }
        }
    }

    /**
     * Adds an array of DataField objects to this ProcessFlow. The DataField
     * objects are each verified to be proper components of this ProcessFlow,
     * i.e., the process Id of the DataField is the same as the process Id for
     * this object. If they are not, then an exception is thrown.
     * 
     * @param dataFields
     *            An array of DataField objects.
     * 
     * @throws Exception
     *             A DataField in the array does not belong to this process.
     */
    public final void addDataFields(DataField[] inDataFields) throws Exception {
        for (DataField df : inDataFields) {
            Integer dfProcId = df.getProcessId();

            if (dfProcId.compareTo(getProcessId()) != 0) {
                throw new Exception("DataField name = [" + df.getDataName()
                        + "], process Id = " + dfProcId.toString()
                        + "is not part of Process Id = "
                        + getProcessId().toString() + ".");
            }

            DataField d = dataFields.get(df.getDataName());
            if (d != null) {
                d.setDataValue(df.getDataValue());
            } else {
                dataFields.put(df.getDataName(), df);
            }
        }
    }

    /**
     * Returns the <tt>DataField</tt> whose name is "fieldName". If
     * "fieldName" is not found, then "null" is returned.
     * 
     * @param fieldName
     *            The name of the DataField.
     * 
     * @return The DataField.
     */
    public final DataField getDataField(String fieldName) {
        return dataFields.get(fieldName);
    }
    
    public final String getDataFields() {
        StringBuffer sb = new StringBuffer();
        for(Entry<String, DataField> es :dataFields.entrySet()) {
            sb.append(es.getKey() + ":" + es.getValue().getDataName() + "," + es.getValue().getDataValue() + "," +
                    es.getValue().getDataDetailLbl() + "," + es.getValue().getDataDetailDsc() + "; ");
        }
        return sb.toString();
    }

    public final void updateDataField(HashMap<String, String> dataMap) {
        for (String key : dataFields.keySet()) {
            DataField df = dataFields.get(key);
            String dataName = df.getDataName();
            String dataValue = dataMap.get(dataName);

            if (dataValue != null
                    && !dataValue.equalsIgnoreCase(df.getDataValue())) {
                df.setDataValue(dataValue);
            }
        }
    }

    /**
     * Adds an array of Transition objects to this ProcessFlow. The Transition
     * objects are each verified to be proper components of this ProcessFlow,
     * i.e., the process template Id of the Transition is the same as the
     * process template Id for this object. If they are not, then an exception
     * is thrown.
     * 
     * @param transitions
     *            An array of Transition objects.
     * 
     * @throws Exception
     *             A Transition in the array does not belong to this process.
     */
    public final void addTransitions(Transition[] inTransitions) throws Exception {
        // For (Each Transition in the input array) Verify that it really
        // belongs to the Template for this Process. If not, throw an
        // Exception. Otherwise, put it in our Hashtable for easy lookup.
        for (Transition trans : inTransitions) {
            Integer transPtId = trans.getProcessTemplateId();

            if (transPtId.compareTo(process.getProcessTemplateId()) != 0) {
                throw new Exception("Transition name = [" + trans.getDataName()
                        + "], process Id = " + transPtId.toString()
                        + "is not part of Process Template Id = "
                        + process.getProcessTemplateId().toString() + ".");
            }
            transitions.put(trans.getTransName(), trans);
        }
    }

    /**
     * Returns the <tt>Process</tt> data member of a ProcessFlow. The
     * <tt>Process</tt> data member contains all of the variable data of a
     * ProcessFlow. This method is provided strictly to support updating a
     * ProcessFlow via the WorkFlow business object.
     * 
     * @return Process the Process data member.
     */
    public final WorkFlowProcess getProcess() {
        return process;
    }

    public final void setProcess(WorkFlowProcess p) {
        process = p;
    }

    public final synchronized boolean isLocked(long threadId) {
        boolean ret = true;
        if (this.threadId == null) {
            this.threadId = new Long(threadId);
            ret = false;
        }
        return ret;
    }

    public final void unLock(long threadId) {
        if (this.threadId.equals(threadId))
            this.threadId = null;
    }
}
