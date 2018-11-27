package us.oh.state.epa.stars2.workflow.engine;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.Transaction;

/**
 * <p>
 * Title: WorkFlowRequest
 * </p>
 * 
 * <p>
 * Description: Contains a workflow request to be sent from the
 * <code>WorkFlowManager</code> to the <code>WorkFlowEngine</code>.
 * </p>
 * A workflow request consists an "Action" and a set of optional data values.
 * The simplest action is a "Ping" request (basically, "are you there?") and
 * involves no additional data values. All possible data fields are available,
 * but most requests do not need every field.
 * </p>
 * 
 * <p>
 * Note that this object is designed for optimal serialization. At the time I
 * wrote this, standard Java serialization took about 325 msec (on my PC) to
 * send a "Ping" request to the workflow engine and receive a response. By
 * implementing my own "readObject()" and "writeObject()", I cut this down to
 * about 60 msec. This is still a little higher than raw text, but is tolerable.
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
 */

public class WorkFlowRequest implements Serializable {
	
	private static final long serialVersionUID = 6199177693521799669L;
    private String action;
    private String type;
    private Integer contactId;
    private Integer userId;
    private Integer workflow;
    private Integer processId;
    private Integer externalId;
    private Integer activityId;
    private Integer loopCnt;
    private Integer accountId;
    private Integer orderId;
    private String expedite;
    private Timestamp startDt;
    private Timestamp jeopardyDt;
    private Timestamp dueDt;
    private Timestamp endDt;
    private ArrayList<DataPair> services = new ArrayList<DataPair>();
    private ArrayList<DataPair> data = new ArrayList<DataPair>();

    private ArrayList<Integer> path;  // let redo controller know what path it is taking.
    private boolean leaveUserSame = false;
    
    private static Transaction transaction;
    private static Integer INIT_INT = -1;
    public static String CANCEL_CUSTOM_NOTE = "note";
    public static String SAVE_NOTE_VALUE = "value";
    
    /**
     * Constructor. This constructor is intended to support serialization
     * (though it does build a "Ping" request). Application code should use the
     * other constructor.
     */
    public WorkFlowRequest() {
        action = WorkFlowEngine.ACTION_PING;

        init();
    }

    /**
     * Constructor. Builds an object using the specified action string. Action
     * strings are defined as <tt>ACTION</tt> string constants in
     * <tt>WorkFlowEngine</tt>, for example "ACTION_PING",
     * "ACTION_CHECK_OUT", etc.
     * 
     * @param action
     *            The action being requested.
     */
    public WorkFlowRequest(String action) {
        this.action = action;

        init();
    }

    /**
     * Returns the "Action" String associated with this WorkFlowRequest. This
     * value can only be set in the constructor.
     * 
     * @return String Work flow engine action string.
     */
    public final String getAction() {
        return action;
    }

	/**
     * Sets the workflow type. Usually, this is done when submitting an order or
     * a proposal to the workflow engine. The request type is either "prov"
     * (provisioning) for an order or "sfa" (sales force automation) for a
     * proposal.
     * 
     * @param type
     *            Workflow type.
     */
    public final void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the current workflow type. If this value has not been set,
     * returns an empty string.
     * 
     * @return String workflow type.
     */
    public final String getType() {
        return type;
    }

    /**
     * Sets the user Id for the request. This is used primarily by actions that
     * are associated with a system user, for example, check-out, or re-assign
     * activity. The user Id must correspond to an active user Id in the
     * User_Def table of the database or the corresponding action in the
     * workflow engine will probably fail.
     * 
     * @param userId
     *            user Id.
     */
    public final void setUserId(Integer userId) {
        if (userId == null) {
            this.userId = INIT_INT;
        } else {
            this.userId = userId;
        }
    }

    /**
     * Returns the user Id. If this value has not been set, returns "null".
     * 
     * @return User Id.
     */
    public final Integer getUserId() {
        Integer ret = null;
        if (!userId.equals(INIT_INT)) {
            ret = userId;
        }

        return ret;
    }

    /**
     * Sets the contact Id for the request. This is used primarily when an
     * activity/tasked is referred to an outside contact (e.g. the End Customer)
     * 
     * @param contactId
     */
    public final void setContactId(Integer contactId) {
        if (contactId == null) {
            this.contactId = INIT_INT;
        } else {
            this.contactId = contactId;
        }
    }

    /**
     * Returns the Contact Id. If this value has not been set, returns "null".
     * 
     * @return Contact Id.
     */
    public final Integer getContactId() {
        Integer ret = null;
        if (!contactId.equals(INIT_INT)) {
            ret = contactId;
        }

        return ret;
    }

    /**
     * Sets the workflow Id. This is used to initialize a new workflow process.
     * 
     * @param workflow
     *            workflow Id.
     */
    public final void setWorkFlow(Integer workflow) {
        this.workflow = workflow;
    }

    /**
     * Returns the workflow Id. If this value has not been set, returns "null".
     * 
     * @return workflow Id.
     */
    public final Integer getWorkFlow() {
        Integer ret = null;
        if (!workflow.equals(INIT_INT)) {
            ret = workflow;
        }

        return ret;
    }

    /**
     * Sets the process Id. Most of the actions that manipulate Activity objects
     * need the process Id as part of the key to the Activity.
     * 
     * @param id
     *            Process Id.
     */
    public final void setProcessId(Integer id) {
        this.processId = id;
    }

    /**
     * Returns the process Id. If this value has not been set, returns "null".
     * 
     * @return process Id.
     */
    public final Integer getProcessId() {
        Integer ret = null;
        if (!processId.equals(INIT_INT)) {
            ret = processId;
        }

        return ret;
    }

    /**
     * Sets the external Id. This is used when submitting an order or proposal
     * to identify the order/proposal Id.
     * 
     * @param extId
     *            order/proposal Id.
     */
    public final void setExternalId(Integer extId) {
        externalId = extId;
    }

    /**
     * Returns the external Id. If this value has not been set, returns "null".
     * 
     * @return external Id.
     */
    public final Integer getExternalId() {
        Integer ret = null;
        if (!externalId.equals(INIT_INT)) {
            ret = externalId;
        }

        return ret;
    }

    /**
     * Sets the activity Id. Most of the actions that manipulate Activity
     * objects require this value. Note that this value actually corresponds to
     * an activity_template_id in the database.
     * 
     * @param actId
     *            activity Id.
     */
    public final void setActivityId(Integer actId) {
        if (actId == null) {
            activityId = INIT_INT;
        } else {
            activityId = actId;
        }
    }

    /**
     * Returns the activity Id. If this value has not been set, returns "null".
     * 
     * @return activity Id.
     */
    public final Integer getActivityId() {
        Integer ret = null;
        if (!activityId.equals(INIT_INT)) {
            ret = activityId;
        }

        return ret;
    }

    /**
     * Sets the loop count.
     * 
     * @param loop
     *            loop Count.
     */
    public final void setLoopCnt(Integer loop) {
        if (loopCnt == null) {
            loopCnt = INIT_INT;
        } else {
            loopCnt = loop;
        }
    }

    /**
     * Returns the Loop Count. If this value has not been set, returns "null".
     * 
     * @return loop count.
     */
    public final Integer getLoopCnt() {
        Integer ret = null;
        if (!loopCnt.equals(INIT_INT)) {
            ret = loopCnt;
        }

        return ret;
    }

    /**
     * Sets the account Id. I'm not sure if this is currently used.
     * 
     * @param accountId
     *            account Id.
     */
    public final void setAccountId(Integer accountId) {
        if (accountId == null) {
            this.accountId = INIT_INT;
        } else {
            this.accountId = accountId;
        }
    }

    /**
     * Returns the account Id. If this value has not been set, returns "null".
     * 
     * @return account Id.
     */
    public final Integer getAccountId() {
        Integer ret = null;
        if (!accountId.equals(INIT_INT)) {
            ret = accountId;
        }

        return ret;
    }

    /**
     * Sets the order Id.
     * 
     * @param orderId
     *            order Id.
     */
    public final void setOrderId(Integer orderId) {
        if (orderId == null) {
            this.orderId = INIT_INT;
        } else {
            this.orderId = orderId;
        }
    }

    /**
     * Returns the order Id. If this value has not been set, returns "null".
     * 
     * @return order Id.
     */
    public final Integer getOrderId() {
        Integer ret = null;
        if (!orderId.equals(INIT_INT)) {
            ret = orderId;
        }

        return ret;
    }

    /**
     * Sets expedite (to idicate this process is high priority).
     * 
     * @param expedite
     *            expedite.
     */
    public final void setExpedite(String expedite) {
        this.expedite = expedite;
    }

    /**
     * Returns the value of the expedite field.
     * 
     * @return expedite.
     */
    public final String getExpedite() {
        return expedite;
    }

    /**
     * Sets start Date.
     * 
     * @param startDt
     *            startDt.
     */
    public final void setStartDt(Timestamp startDt) {
        this.startDt = startDt;
    }

    /**
     * Returns the Start Date.
     * 
     * @return startDt.
     */
    public final Timestamp getStartDt() {
        return startDt;
    }

    /**
     * Sets Due Date (to indicate this process is high priority).
     * 
     * @param dueDt
     *            dueDt.
     */
    public final void setDueDt(Timestamp dueDt) {
        this.dueDt = dueDt;
    }

    /**
     * Returns the Due Date.
     * 
     * @return dueDt.
     */
    public final Timestamp getDueDt() {
        return dueDt;
    }

    public final Timestamp getJeopardyDt() {
        return jeopardyDt;
    }

    public final void setJeopardyDt(Timestamp jeopardyDt) {
        this.jeopardyDt = jeopardyDt;
    }
    
    

    public Timestamp getEndDt() {
		return endDt;
	}

	public void setEndDt(Timestamp endDt) {
		this.endDt = endDt;
	}

	/**
     * Adds a service Id, activity template def Id pair to the request. This is
     * done when submitting an order for provisioning. The "service Id" is the
     * Id of the service being provisioned and the "activity template def Id"
     * identifies the provisioning workflow.
     * 
     * @param serviceId
     *            service Id.
     * @param activityTemplateDefId
     *            activity template def Id.
     * 
     * @return int The index of this pair in the array of service pairs.
     */
    public final int addServicePair(Integer serviceId, Integer activityTemplateDefId) {
        int idx = services.size();

        DataPair dp = new DataPair();
        dp.name = serviceId.toString();
        dp.value = activityTemplateDefId.toString();

        services.add(dp);
        return idx;
    }

    /**
     * Returns the total number of service pairs assigned to this object. To
     * access individual values, use this method to retrieve the count, and then
     * iterate over the number of values to retrieve service Ids via the
     * "getServiceId()" method and activity template def Ids via the
     * "getActivityTemplateDefId()" method.
     * 
     * @return int total number of service pairs in this object.
     */
    public final int getServiceCount() {
        return services.size();
    }

    /**
     * Returns the "idx" service Id value. If "idx" is out of range (see
     * "getServiceCount()"), then a runtime exception will be generated. Values
     * are always returned in the same order they are added.
     * 
     * @param idx
     *            index into the service Id array.
     * 
     * @return Integer service Id.
     */
    public final Integer getServiceId(int idx) {
        DataPair dp = services.get(idx);
        return new Integer(dp.name);
    }

    /**
     * Returns the "idx" activity template def Id value. If "idx" is out of
     * range (see "getServiceCount()"), then a runtime exception will be
     * generated. Values are always returned in the same order they are added.
     * 
     * @param idx
     *            index into the activity template def Id array.
     * 
     * @return Integer activity template def Id.
     */
    public final Integer getActivityTemplateDefId(int idx) {
        DataPair dp = services.get(idx);
        return new Integer(dp.value);
    }

    /**
     * Adds a data name-value pair to the request. Data pairs identify service
     * data that is typically manipulated during workflow process. Often, these
     * values are displayed to the user during check-in/check-out. The data name
     * associates this value with a display object in the web application.
     * 
     * @param name
     *            Data object name.
     * @param value
     *            Data object value.
     * 
     * @return int The index of this pair in the array of data pairs.
     */
    public final int addDataPair(String name, String value) {
        int idx = data.size();

        DataPair dp = new DataPair();
        dp.name = name;
        dp.value = value;

        data.add(dp);
        return idx;
    }

    /**
     * Returns the total number of data pairs assigned to this object. To access
     * individual values, use this method to retrieve the count, and then
     * iterate over the number of values to retrieve data names via the
     * "getDataName()" method and data values via the "getDataValue()" method.
     * 
     * @return int total number of data pairs in this object.
     */
    public final int getDataCount() {
        return data.size();
    }

    /**
     * Returns the "idx" data name. If "idx" is out of range (see
     * "getDataCount()"), then a runtime exception will be generated. Values are
     * always returned in the same order they are added.
     * 
     * @param idx
     *            index into the data pairs array.
     * 
     * @return String data object name.
     */
    public final String getDataName(int idx) {
        DataPair dp = data.get(idx);
        return dp.name;
    }

    /**
     * Returns the "idx" data value. If "idx" is out of range (see
     * "getDataCount()"), then a runtime exception will be generated. Values are
     * always returned in the same order they are added.
     * 
     * @param idx
     *            index into the data pairs array.
     * 
     * @return String data object value.
     */
    public final String getDataValue(int idx) {
        DataPair dp = data.get(idx);
        return dp.value;
    }

    /**
     * Returns a String representation of this object. The value returned by
     * this method conforms to the legacy method for sending requests from a
     * WorkFlowManager to the WorkFlowEngine.
     * 
     * @return String A string representation of this object.
     */
    public final String toString() {
        StringBuffer sb = new StringBuffer(300);

        sb.append("Action=");
        sb.append(action);

        addString(sb, type, "Type");

        addInteger(sb, userId, "User");
        addInteger(sb, contactId, "Contact");
        addInteger(sb, workflow, "Workflow");
        addInteger(sb, processId, "Id");
        addInteger(sb, externalId, "ExtId");
        addInteger(sb, activityId, "ActId");
        addInteger(sb, loopCnt, "LoopCnt");
        addInteger(sb, accountId, "AccountId");
        addInteger(sb, orderId, "NewOrderId");
        addString(sb, expedite, "Expedite");

        addDate(sb, startDt, "StartDt");
        addDate(sb, jeopardyDt, "JeopardyDt");
        addDate(sb, dueDt, "DueDt");
        addDate(sb, endDt, "EndDt");

        addStringArray(sb, services, "Service");
        addStringArray(sb, data, "Data");
        

        return sb.toString();
    }

    /**
     * Utility method used by "toString()" to format a String data member for
     * output. Effectively, this method treats a null String or an empty String
     * as if it didn't exist at all. If "dm" has an actual value, then ",<tag>=<dm>"
     * is appended to the StringBuffer. Otherwise, nothing is appended.
     * 
     * @param sb
     *            StringBuffer we are currently using.
     * @param dm
     *            The String data member we are processing.
     * @param tag
     *            The tag to associate with this Sting.
     */
    private void addString(StringBuffer sb, String dm, String tag) {
        if ((dm != null) && (dm.length() > 0)) {
            sb.append(",");
            sb.append(tag);
            sb.append("=");
            sb.append(dm);
        }
    }

    /**
     * Utility method used by "toString()" to format an Integer data member for
     * output. Effectively, this method treats a null Integer or a zero ("0")
     * Integer as if it didn't exist at all. If "dm" has a non-zero value, then ",<tag>=<dm>"
     * is appended to the StringBuffer. Otherwise, nothing is appended.
     * 
     * @param sb
     *            StringBuffer we are currently using.
     * @param dm
     *            The Integer data member we are processing.
     * @param tag
     *            The tag to associate with this Integer.
     */
    private void addInteger(StringBuffer sb, Integer dm, String tag) {
        if ((dm != null) && (dm.intValue() > 0)) {
            sb.append(",");
            sb.append(tag);
            sb.append("=");
            sb.append(dm);
        }
    }

    /**
     * Utility method used by "toString()" to format an Integer data member for
     * output. Effectively, this method treats a null Integer or a zero ("0")
     * Integer as if it didn't exist at all. If "dm" has a non-zero value, then ",<tag>=<dm>"
     * is appended to the StringBuffer. Otherwise, nothing is appended.
     * 
     * @param sb
     *            StringBuffer we are currently using.
     * @param dm
     *            The Timestamp data member we are processing.
     * @param tag
     *            The tag to associate with this Timestamp.
     */
    private void addDate(StringBuffer sb, Timestamp dm, String tag) {
        if ((dm != null) && (dm.getTime() > 0)) {
            sb.append(",");
            sb.append(tag);
            sb.append("=");
            sb.append(dm.toString());
        }
    }

    /**
     * Utility method used by "toString()" to format a DataPair array for
     * output. Effectively, this method treats a null or size zero ("0")
     * ArrayList as if it didn't exist at all. If "a" has values, then ",<tag>=<dm>"
     * is appended to the StringBuffer for each value in "a". Otherwise, nothing
     * is appended.
     * 
     * @param sb
     *            StringBuffer we are currently using.
     * @param a
     *            The ArrayList data member we are processing.
     * @param tag
     *            The tag to associate with each element of the array.
     */
    private void addStringArray(StringBuffer sb, ArrayList<DataPair> a,
            String tag) {
        if ((a != null) && (a.size() > 0)) {
            for (DataPair dp : a) {
                sb.append(",");

                sb.append(tag);
                sb.append("=");
                sb.append(dp.name);
                sb.append(":");
                sb.append(dp.value);
            }
        }
    }

    /**
     * Writes this object to "stream" during serialization. See header comments
     * for an explanation of why this method exists. In essence, what this
     * method does is convert all of the data members to UTF Strings and writes
     * these UTF Strings to the output stream.
     * 
     * @param stream
     *            Output stream.
     * 
     * @throws IOException
     *             Data conversion/formating error.
     */
    private void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        // Write our individual data members to the output stream. Note that
        // the order they are written here MUST BE THE SAME as the order they
        // are read in "readObject()". If you do not do this, your code is
        // going to die...

        stream.writeUTF(action);
        stream.writeUTF(type);

        stream.writeUTF(userId.toString());
        stream.writeUTF(contactId.toString());
        stream.writeUTF(workflow.toString());
        stream.writeUTF(processId.toString());
        stream.writeUTF(externalId.toString());
        stream.writeUTF(activityId.toString());
        stream.writeUTF(loopCnt.toString());
        stream.writeUTF(accountId.toString());
        stream.writeUTF(orderId.toString());
        stream.writeUTF(expedite);

        if (startDt != null) {
            stream.writeLong(startDt.getTime());
        } else {
            stream.writeLong(0);
        }

        if (jeopardyDt != null) {
            stream.writeLong(jeopardyDt.getTime());
        } else {
            stream.writeLong(0);
        }

        if (dueDt != null) {
            stream.writeLong(dueDt.getTime());
        } else {
            stream.writeLong(0);
        }

        if (endDt != null) {
            stream.writeLong(endDt.getTime());
        } else {
            stream.writeLong(0);
        }

        // Now, write the services array. First write out how many of these
        // we actually have.

        int serviceCnt = services.size();
        stream.writeUTF(Integer.toString(serviceCnt));

        for (DataPair dp : services) {
            stream.writeUTF(dp.name);
            stream.writeUTF(dp.value);
        }

        // Now, write the data name-value pairs array. First write out how
        // many we actually have.

        int dataCnt = data.size();
        stream.writeUTF(Integer.toString(dataCnt));

        for (DataPair dp : data) {
            stream.writeUTF(dp.name);
            stream.writeUTF(dp.value);
        }
    }

    /**
     * Reads this object from "stream" during serialization. See header comments
     * for an explanation of why this method exists. In essence, what this
     * method does is read all of the data members as UTF Strings and then
     * converts these UTF Strings back into data members.
     * 
     * @param stream
     *            Output stream.
     * 
     * @throws IOException
     *             Data conversion/formating error.
     * @throws ClassNotFoundException
     *             Could not find class definition.
     */
    private void readObject(java.io.ObjectInputStream stream)
            throws IOException {
        // Read this stuff in the same order it was written. Note that I
        // use "s" so you can easily see the intermediate value on the
        // debugger.

        action = stream.readUTF();
        type = stream.readUTF();

        String s = stream.readUTF();
        userId = new Integer(s);

        s = stream.readUTF();
        contactId = new Integer(s);

        s = stream.readUTF();
        workflow = new Integer(s);

        s = stream.readUTF();
        processId = new Integer(s);

        s = stream.readUTF();
        externalId = new Integer(s);

        s = stream.readUTF();
        activityId = new Integer(s);

        s = stream.readUTF();
        loopCnt = new Integer(s);

        s = stream.readUTF();
        accountId = new Integer(s);

        s = stream.readUTF();
        orderId = new Integer(s);

        expedite = stream.readUTF();

        Long t = stream.readLong();
        startDt = new Timestamp(t);

        t = stream.readLong();
        jeopardyDt = new Timestamp(t);

        t = stream.readLong();
        dueDt = new Timestamp(t);

        t = stream.readLong();
        endDt = new Timestamp(t);

        // Now, read in all of the service data (if we have any).

        s = stream.readUTF();
        Integer temp = new Integer(s);
        int scnt = temp.intValue();

        int i;
        DataPair dp;

        services = new ArrayList<DataPair>(scnt);
        if (scnt > 0) {
            for (i = 0; i < scnt; i++) {
                dp = new DataPair();
                dp.name = stream.readUTF();
                dp.value = stream.readUTF();
                services.add(dp);
            }
        }
        // Read in the data name-value pairs.

        s = stream.readUTF();
        temp = new Integer(s);
        int dataCnt = temp.intValue();

        data = new ArrayList<DataPair>();
        if (dataCnt > 0) {
            for (i = 0; i < dataCnt; i++) {
                dp = new DataPair();
                dp.name = stream.readUTF();
                dp.value = stream.readUTF();
                data.add(dp);
            }
        }
    }

    private void init() {
        // NOTE: If we assign ALL data members a default, "empty" value,
        // serialization will proceed much more efficiently.

        type = "";
        userId = new Integer(INIT_INT);
        contactId = new Integer(INIT_INT);
        workflow = new Integer(INIT_INT);
        processId = new Integer(INIT_INT);
        externalId = new Integer(INIT_INT);
        activityId = new Integer(INIT_INT);
        loopCnt = new Integer(INIT_INT);
        accountId = new Integer(INIT_INT);
        orderId = new Integer(INIT_INT);
        expedite = new String("N");
        startDt = new Timestamp(0);
        jeopardyDt = new Timestamp(0);
        dueDt = new Timestamp(0);
        endDt = new Timestamp(0);

        services = new ArrayList<DataPair>();
        data = new ArrayList<DataPair>();
        path = new ArrayList<Integer>();
    }


    public void addToPath(Integer activityId) {
        if (path == null)
            path = new ArrayList<Integer>();
        path.add(activityId);
    }

    public boolean inPath(Integer activityId) {
        return path.contains(activityId);
    }

    public void removeFromPath(Integer activityId) {
        if (path != null)
            path.remove(activityId);
    }

    public boolean isLeaveUserSame() {
        return leaveUserSame;
    }

    public void setLeaveUserSame(boolean leaveUserSame) {
        this.leaveUserSame = leaveUserSame;
    }

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		WorkFlowRequest.transaction = transaction;
	}
	

}

//Several requests involve variable length name-value pairs. This is a
//little utility class that helps us keep track of these pairs.
class DataPair implements Serializable {
	private static final long serialVersionUID = 1171408376944600512L;
	String name;
	String value;
}
