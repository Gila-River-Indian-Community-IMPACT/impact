package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * <p>
 * Title: Transition
 * </p>
 * 
 * <p>
 * Description: Transitions represent a logical connection between Activities in
 * a workflow process.
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

public class Transition extends BaseDB {
    // Constants that define whether this is a looping or non-looping
    // Transition.
    public static final String NOLOOP = "NL";
    public static final String INLINE = "IL";
    public static final String LOOP = "LP";

    // public static final String NO_OPERATION_VAL = "none" ;

    // Constants that define field comparison operators.
    public static final int OP_NO_OPERATION = 0;
    public static final int OP_EQUALS = 1; // "="
    public static final int OP_LT = 2; // "<"
    public static final int OP_LT_EQUALS = 3; // "<="
    public static final int OP_GT = 4; // ">"
    public static final int OP_GT_EQUALS = 5; // ">="
    public static final int OP_NOT_EQUALS = 6; // "!="
    private String transName;
    private Integer fromActivityId;
    private String fromActivityName;
    private Integer toActivityId;
    private String toActivityName;
    private String operation;
    private String condition;
    private String dataValue;
    private String dataName;
    private Integer processTemplateId;
    private String transitionCode;
    private String transitionDsc;
    private boolean deprecated;

    /**
     * @param old
     */
    public Transition(Transition old) {
        super(old);
        if (old != null) {
            setCondition(old.getCondition());
            setDataName(old.getDataName());
            setDataValue(old.getDataValue());
            setDeprecated(old.isDeprecated());
            setFromId(old.getFromId());
            setFromName(old.getFromName());
            setOperation(old.getOperation());
            setProcessTemplateId(old.getProcessTemplateId());
            setToId(old.getToId());
            setToName(old.getToName());
            setTransitionCode(old.getTransitionCode());
            setTransitionDsc(old.getTransitionDsc());
            setTransName(old.getTransName());
        }
    }

    public Transition() {
    }

    /**
     * Constructor. Specifies the fields needed to make a usable object in the
     * workflow engine. If the object is being constructed for writing to the
     * database, then the <tt>processTemplateId<tt> must also be set.  The
     *  <tt>transCode<tt> value must correspond to a value from the
     *  "Transition_Def" table.  The "transName" (Transition name) should
     *  conform to the format used by the ProcessFlow class:
     *
     *  "Tran <activity template Id>.<to activity template Id>"
     *
     *  For example: "Tran 1234.5678".
     *
     *  This format is not validated by this class (because it doesn't care one
     *  way or the other).
     *
     *  @param transName Transition name
     *  @param fromId from Activity Template Id
     *  @param fromName from Activity Template Name
     *  @param toId to Activity Template Id
     *  @param toName to Activity Template Name
     *  @param transCode transition code
     */
    public Transition(String transName, Integer fromId, String fromName,
            Integer toId, String toName, String transCode) {
        this.transName = transName;
        this.fromActivityId = fromId;
        this.fromActivityName = fromName;
        this.toActivityId = toId;
        this.toActivityName = toName;
        this.transitionCode = transCode;

        this.deprecated = false;
        this.operation = "";
    }
    
    public String toPrintable() {
        return "Transition[" + transName + "]:from=" + (fromActivityId==null?"-":fromActivityId.toString()) + "[" + fromActivityName +
            "],to=" + (toActivityId==null?"-":toActivityId.toString()) + "[" + toActivityName + "],op=" + operation + ",cond=" + condition +
            ",dataValue=" + dataValue + ", dataName=" + dataName + ",pTempId=" + (processTemplateId==null?"-":processTemplateId.toString()) +
            ",transition=" + transitionCode + "[" + transitionDsc + "]";
    }

    /**
     * Sets an optional condition string. A condition string is defined as a
     * [data name] [operation] [data value], e.g., "foo >= 34". The string is
     * parsed and split into its elements. Individual elements of the may be
     * retrieved via "getDataName()", "getOperation()", and "getDataValue()".
     * Returns <tt>false</tt> if the input condition string does not represent
     * a valid condition.
     * 
     * @param condition
     *            "[data name] [operation] [data value]" string
     * 
     * @return boolean <tt>true</tt> if the condition string was successfully
     *         parsed.
     */
    public final boolean setCondition(String condition) {
        if ((condition == null) || (condition.length() == 0)) {
            this.condition = null;
            return false;
        }

        int idx;

        // The order of comparison is important - e.g. we need to match
        // != before looking for =

        if ((idx = condition.indexOf("!=")) > 0) {
            dataName = condition.substring(0, idx);
            dataValue = condition.substring(idx + 2);
            operation = "!=";
        } else if ((idx = condition.indexOf("<=")) > 0) {
            dataName = condition.substring(0, idx);
            dataValue = condition.substring(idx + 2);
            operation = "<=";
        } else if ((idx = condition.indexOf(">=")) > 0) {
            dataName = condition.substring(0, idx);
            dataValue = condition.substring(idx + 2);
            operation = ">=";
        } else if ((idx = condition.indexOf("=")) > 0) {
            dataName = condition.substring(0, idx);
            dataValue = condition.substring(idx + 1);
            operation = "=";
        } else if ((idx = condition.indexOf("<")) > 0) {
            dataName = condition.substring(0, idx);
            dataValue = condition.substring(idx + 1);
            operation = "<";
        } else if ((idx = condition.indexOf(">")) > 0) {
            dataName = condition.substring(0, idx);
            dataValue = condition.substring(idx + 1);
            operation = ">";
        } else {
            return false;
        }

        // Trim excess white space off the strings and return.

        this.condition = condition.trim();
        this.dataName = this.dataName.trim();
        this.dataValue = this.dataValue.trim();

        return true;
    }

    /**
     * Returns the transition name. This is usually constructed by the
     * containing ProcessFlow.
     * 
     * @return the name of this transition.
     */
    public final String getTransName() {
        return this.transName;
    }

    /**
     * Sets the name of this transition.
     * 
     * @param transName
     *            new Transition name.
     */
    public final void setTransName(String transName) {
        this.transName = transName;
    }

    /**
     * Returns the Id of the "to" activity.
     * 
     * @return to activity Id.
     */
    public final Integer getToId() {
        return this.toActivityId;
    }

    /**
     * Sets the "to" activity Id.
     * 
     * @param activityId
     *            to activity Id
     */
    public final void setToId(Integer activityId) {
        this.toActivityId = activityId;
    }

    /**
     * Returns the name of the "to" activity.
     * 
     * @return to activity name.
     */
    public final String getToName() {
        return this.toActivityName;
    }

    /**
     * Sets the name of the "to" activity.
     * 
     * @param activityName
     *            to activity name.
     */
    public final void setToName(String activityName) {
        this.toActivityName = activityName;
    }

    /**
     * Returns the Id of the "from" activity.
     * 
     * @return from activity Id.
     */
    public final Integer getFromId() {
        return this.fromActivityId;
    }

    /**
     * Sets the Id of the "from" activity.
     * 
     * @param activityId
     *            to activity Id.
     */
    public final void setFromId(Integer activityId) {
        this.fromActivityId = activityId;
    }

    /**
     * Returns the name of the "from" activity.
     * 
     * @return from activity name.
     */
    public final String getFromName() {
        return this.fromActivityName;
    }

    /**
     * Sets the name of the "from" activity.
     * 
     * @param activityName
     *            from activity name.
     */
    public final void setFromName(String activityName) {
        this.fromActivityName = activityName;
    }

    /**
     * Returns <tt>true</tt> if the condition clause has been set and is
     * valid. Otherwise, returns <tt>false</tt>.
     * 
     * @return boolean <tt>true</tt> if we have a valid condition clause.
     */
    public final boolean isValidCondition() {
        return (this.condition != null);
    }

    /**
     * Returns the entire condition clause, if set. Otherwise, returns
     * <tt>null</tt>. Use "setCondition()" to set a condition clause.
     * 
     * @return String condition clause.
     */
    public final String getCondition() {
        // Attempt to construct the condition clause based on the current
        // data name, operation, and data value data members. Otherwise,
        // return whatever we have ...

        if ((this.dataName != null) && (this.dataName.length() > 0)
                && (this.operation != null) && (this.operation.length() > 0)
                && (this.dataValue != null) && (this.dataValue.length() > 0)) {
            StringBuffer sb = new StringBuffer();
            sb.append(this.dataName);
            sb.append(" ");
            sb.append(this.operation);
            sb.append(" ");
            sb.append(this.dataValue);

            this.condition = sb.toString();
        }

        return this.condition;
    }

    /**
     * Returns the current data value if set. If no value is set, returns
     * <tt>null</tt>. The data value is part of the condition clause set via
     * "setCondition()".
     * 
     * @return String data value.
     */
    public final String getDataValue() {
        return this.dataValue;
    }

    /**
     * Sets the data value portion of the condition clause. Note: This method is
     * provided primarily to support the <code>ObjectEditor</code>. If you
     * choose to use this method anyway, make sure you set the data name and
     * operation as well.
     * 
     * @param name
     *            String The name of the data field to use in the comparison.
     */
    public final void setDataValue(String value) {
        this.dataValue = value;
    }

    /**
     * Returns the current data name if set. If no name is set, returns
     * <tt>null</tt>. The data name is part of the condition clause set via
     * "setCondition()".
     * 
     * @return String data value.
     */
    public final String getDataName() {
        return this.dataName;
    }

    /**
     * Sets the data name portion of the condition clause. Note: This method is
     * provided primarily to support the <code>ObjectEditor</code>. If you
     * choose to use this method anyway, make sure you set the data value and
     * operation as well.
     * 
     * @param name
     *            String The name of the data field to use in the comparison.
     */
    public final void setDataName(String name) {
        this.dataName = name;
    }

    /**
     * Returns the current operation. This value will be one of the "OP" integer
     * constants defined in this class. If no condition clause has been set,
     * then OPNOOPERATION will be returned. Otherwise, the value returned will
     * correspond to the operator portion of the condition clause set via
     * "setCondtion()".
     * 
     * @return String current operation setting.
     */
    public final String getOperation() {
        return this.operation;
    }

    /**
     * Sets the data name portion of the condition clause. Note: This method is
     * provided primarily to support the <code>ObjectEditor</code>. If you
     * choose to use this method anyway, make sure you set the data value and
     * data name as well. The "op" value should correspond to one of the "OP"
     * constants in this class.
     * 
     * @param name
     *            String The name of the data field to use in the comparison.
     */
    public final void setOperation(String op) {
        this.operation = op;
    }

    /**
     * Returns the current process template Id if set. Otherwise, returns
     * <tt>null</tt>.
     * 
     * @return Integer process template Id.
     */
    public final Integer getProcessTemplateId() {
        return this.processTemplateId;
    }

    /**
     * Sets the process template Id. If this object is being for insertion into
     * the database, then this value is required and must satisfy referential
     * integrity constraints.
     * 
     * @param newId
     *            new process template Id.
     */
    public final void setProcessTemplateId(Integer newId) {
        this.processTemplateId = newId;
    }

    /**
     * Returns the transition code for this transition. This value will
     * correspond to one of the values from the "TransitionDef" table.
     * 
     * @return String transition code.
     */
    public final String getTransitionCode() {
        return this.transitionCode;
    }

    /**
     * Sets the transition code for this transition. This value must correspond
     * to one of the values from "TransitionDef" or this object will not be
     * insertable into the database.
     * 
     * @param newCode
     *            new transition code.
     */
    public final void setTransitionCode(String newCode) {
        this.transitionCode = newCode;
    }

    /**
     * Sets the transition description. This is the longer, user-friendly
     * version of the transition code. Note that this field is not required for
     * database insertions. It is simply a convenience used to display data to a
     * user.
     * 
     * @param dsc
     *            Transition description.
     */
    public final void setTransitionDsc(String dsc) {
        this.transitionDsc = dsc;
    }

    /**
     * Returns the transistion description corresponding to the transition code.
     * Note that this field is not required. However, the DAO will set this
     * field when the DAO creates this object.
     * 
     * @return String Transition description.
     */
    public final String getTransitionDsc() {
        return this.transitionDsc;
    }

    /**
     * Sets the value of the deprecated flag. A value of <tt>true</tt>
     * indicates that this Transition should not be used in any future work
     * flows. A value of <tt>false</tt> means that this Transition is still
     * available for use.
     * 
     * @param deprecated
     *            true => do not use this transition in future workflows.
     */
    public final void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    /**
     * Returns <tt>true</tt> if this transition has been deprecated.
     * Otherwise, returns <tt>false</tt>.
     * 
     * @return boolean "true" if this transition has been deprecated.
     */
    public final boolean isDeprecated() {
        return this.deprecated;
    }

    public final void populate(ResultSet rs) {
        try {
            // Start extracting the parameters we need for the constructor.
            // Use the DAO utility to look up the names of the "to" and "from"
            // activities. We can't do that in SQL because both activity Ids
            // reference the same table.

            setFromId(AbstractDAO.getInteger(rs, "from_activity_template_id"));
            setFromName(rs.getString("from_activity_template_nm"));
            setToId(AbstractDAO.getInteger(rs, "to_activity_template_id"));
            setToName(rs.getString("to_activity_template_nm"));
            setTransitionCode(rs.getString("transition_def_cd"));
            setTransitionDsc("Tran " + fromActivityId.toString() + "."
                    + toActivityId.toString());

            setTransName("Tran " + getFromId() + "." + getToId());

            String lCondition = rs.getString("condition_val");
            setCondition(lCondition);

            String transDsc = rs.getString("transition_def_dsc");
            setTransitionDsc(transDsc);

            Integer lProcessTemplateId = AbstractDAO.getInteger(rs,
                    "process_template_id");
            setProcessTemplateId(lProcessTemplateId);

            String deprecatedInd = rs.getString("deprecated_ind");
            boolean lDeprecated = AbstractDAO
                    .translateIndicatorToBoolean(deprecatedInd);
            setDeprecated(lDeprecated);
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    /* (non-Javadoc)
     * @see us.oh.state.epa.stars2.database.dbObjects.BaseDB#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return (obj != null 
                && this.getFromId().equals(((Transition)obj).getFromId())
                && this.getToId().equals(((Transition)obj).getToId()));
    }
}
