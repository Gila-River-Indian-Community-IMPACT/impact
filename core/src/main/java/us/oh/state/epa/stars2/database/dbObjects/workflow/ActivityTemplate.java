package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * ActivityTemplate
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * <DL>
 * 
 * @version $Revision: 1.6 $
 * @author $Author: cmeier $
 */
public class ActivityTemplate extends BaseDB {
    static final long serialVersionUID = 1L;
    private Integer activityTemplateId;
    private Integer processTemplateId;
    private String processTemplateNm;
    private Integer activityTemplateDefId;
    private Integer expectedDuration;
    private Integer jeopardyDuration;
    private Integer serviceId;
    private String milestoneInd;
    private String initTaskInd;
    private Integer xLoc;
    private Integer yLoc;
    private String inTransitionDefCd;
    private String outTransitionDefCd;
    private String activityTemplateNm;
    private String deprecatedInd;
    private String aggregate;
    private String roleCd;
    private String roleDsc;
    private String activityData;

    public ActivityTemplate() {
        expectedDuration = new Integer(-1);
        jeopardyDuration = new Integer(-1);
    }

    /**
     * @param old
     */
    public ActivityTemplate(ActivityTemplate old) {
        super(old);
        if (old != null) {
            setActivityTemplateId(old.getActivityTemplateId());
            setProcessTemplateId(old.getProcessTemplateId());
            setProcessTemplateNm(old.getProcessTemplateNm());
            setActivityTemplateDefId(old.getActivityTemplateDefId());
            setExpectedDuration(old.getExpectedDuration());
            setJeopardyDuration(old.getJeopardyDuration());
            setServiceId(old.getServiceId());
            setMilestoneInd(old.getMilestoneInd());
            setInitTaskInd(old.getInitTaskInd());
            setXloc(old.getXloc());
            setYloc(old.getYloc());
            setInTransitionDefCd(old.getInTransitionDefCd());
            setOutTransitionDefCd(old.getOutTransitionDefCd());
            setActivityTemplateNm(old.getActivityTemplateNm());
            setDeprecatedInd(old.getDeprecatedInd());
            setAggregate(old.getAggregate());
            setRoleCd(old.getRoleCd());
            setRoleDsc(old.getRoleDsc());
            setActivityData(old.getActivityData());
        }
    }

    public final void populate(ResultSet rs) {
        try {
            setActivityTemplateId(AbstractDAO.getInteger(rs,
                    "activity_template_id"));
            setProcessTemplateId(AbstractDAO.getInteger(rs,
                    "process_template_id"));
            setActivityTemplateDefId(AbstractDAO.getInteger(rs,
                    "activity_template_def_id"));
            setServiceId(AbstractDAO.getInteger(rs, "service_id"));
            setExpectedDuration(AbstractDAO.getInteger(rs, "expected_duration"));
            setJeopardyDuration(AbstractDAO.getInteger(rs, "jeopardy_duration"));
            setMilestoneInd(rs.getString("milestone_ind"));
            setInitTaskInd(rs.getString("init_task_ind"));
            setInTransitionDefCd(rs.getString("in_transition_def_cd"));
            setOutTransitionDefCd(rs.getString("out_transition_def_cd"));
            setXloc(AbstractDAO.getInteger(rs, "x_loc"));
            setYloc(AbstractDAO.getInteger(rs, "y_loc"));
            setActivityTemplateNm(rs.getString("activity_template_nm"));
            setDeprecatedInd(rs.getString("deprecated_ind"));
            setProcessTemplateNm(rs.getString("process_template_nm"));
            setActivityData(rs.getString("activity_data"));
            setAggregate(rs.getString("aggregate"));
            setRoleCd(rs.getString("facility_role_cd"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public final String getAggregate() {
        return aggregate;
    }

    public final void setAggregate(String aggregate) {
        this.aggregate = aggregate;
    }

    public final String getRoleCd() {
        return roleCd;
    }

    public final void setRoleCd(String roleCd) {
        this.roleCd = roleCd;
    }

    public final String getRoleDsc() {
        return roleDsc;
    }

    public final void setRoleDsc(String roleDsc) {
        this.roleDsc = roleDsc;
    }

    /**
     * ActivityTemplateId.
     * 
     * @return Integer
     */
    public final Integer getActivityTemplateId() {
        return activityTemplateId;
    }

    /**
     * ActivityTemplateId.
     * 
     * @param activityTemplateId
     */
    public final void setActivityTemplateId(Integer activityTemplateId) {
        this.activityTemplateId = activityTemplateId;
    }

    /**
     * ProcessTemplateId.
     * 
     * @return Integer
     */
    public final Integer getProcessTemplateId() {
        return processTemplateId;
    }

    /**
     * ProcessTemplateId.
     * 
     * @param processTemplateId
     */
    public final void setProcessTemplateId(Integer processTemplateId) {
        this.processTemplateId = processTemplateId;
    }

    public final String getProcessTemplateNm() {
        return this.processTemplateNm;
    }

    public final void setProcessTemplateNm(String newName) {
        this.processTemplateNm = newName;
    }

    /**
     * ActivityTemplateDefId.
     * 
     * @return Integer
     */
    public final Integer getActivityTemplateDefId() {
        return activityTemplateDefId;
    }

    /**
     * ActivityTemplateDefId.
     * 
     * @param activityTemplateDefId
     */
    public final void setActivityTemplateDefId(Integer activityTemplateDefId) {
        this.activityTemplateDefId = activityTemplateDefId;
    }

    /**
     * ServiceId.
     * 
     * @return Integer
     */
    public final Integer getServiceId() {
        return serviceId;
    }

    /**
     * ServiceId.
     * 
     * @param serviceId
     */
    public final void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * ExpectedDuration.
     * 
     * @return Integer
     */
    public final Integer getExpectedDuration() {
        return expectedDuration;
    }

    /**
     * ExpectedDuration.
     * 
     * @param expectedDuration
     */
    public final void setExpectedDuration(Integer expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    public final Integer getJeopardyDuration() {
        return this.jeopardyDuration;
    }

    public final void setJeopardyDuration(Integer newDur) {
        this.jeopardyDuration = newDur;
    }

    public final String getActivityData() {
        return this.activityData;
    }

    public final void setActivityData(String ad) {
        this.activityData = ad;
    }

    /**
     * MilesstoneInd.
     * 
     * @return String
     */
    public final String getMilestoneInd() {
        return milestoneInd;
    }

    /**
     * MilestoneInd.
     * 
     * @param milestoneInd
     */
    public final void setMilestoneInd(String milestoneInd) {
        this.milestoneInd = milestoneInd;
    }

    /**
     * InitTaskInd.
     * 
     * @return String
     */
    public final String getInitTaskInd() {
        return initTaskInd;
    }

    /**
     * Returns whether or not this is an initial activity as a boolean value.
     * 
     * @return "true" if this is an initial task, otherwise "false".
     */
    public final boolean isInitTask() {
        return (this.initTaskInd.compareToIgnoreCase("Y") == 0);
    }

    /**
     * InitTaskInd.
     * 
     * @param initTaskInd
     */
    public final void setInitTaskInd(String initTaskInd) {
        this.initTaskInd = initTaskInd;
    }

    /**
     * xLoc.
     * 
     * @return Integer
     */
    public final Integer getXloc() {
        return xLoc;
    }

    /**
     * xLoc.
     * 
     * @param xLoc
     */
    public final void setXloc(Integer xLoc) {
        this.xLoc = xLoc;
    }

    /**
     * yLoc.
     * 
     * @return Integer
     */
    public final Integer getYloc() {
        return yLoc;
    }

    /**
     * yLoc.
     * 
     * @param yLoc
     */
    public final void setYloc(Integer yLoc) {
        this.yLoc = yLoc;
    }

    /**
     * inTransitionDefCd.
     * 
     * @return String
     */
    public final String getInTransitionDefCd() {
        return inTransitionDefCd;
    }

    /**
     * inTransitionDefCd.
     * 
     * @param inTransitionDefCd
     */
    public final void setInTransitionDefCd(String inTransitionDefCd) {
        this.inTransitionDefCd = inTransitionDefCd;
    }

    /**
     * outTransitionDefCd.
     * 
     * @return String
     */
    public final String getOutTransitionDefCd() {
        return outTransitionDefCd;
    }

    /**
     * outTransitionDefCd.
     * 
     * @param outTransitionDefCd
     */
    public final void setOutTransitionDefCd(String outTransitionDefCd) {
        this.outTransitionDefCd = outTransitionDefCd;
    }

    /**
     * ActivityTemplateNm.
     * 
     * @return String
     */
    public final String getActivityTemplateNm() {
        return activityTemplateNm;
    }

    /**
     * ActivityTemplateNm.
     * 
     * @param activityTemplateNm
     */
    public final void setActivityTemplateNm(String activityTemplateNm) {
        this.activityTemplateNm = activityTemplateNm;
    }

    /**
     * DeprecatedInd.
     * 
     * @return String
     */
    public final String getDeprecatedInd() {
        return deprecatedInd;
    }

    /**
     * DeprecatedInd.
     * 
     * @param deprecatedInd
     */
    public final void setDeprecatedInd(String deprecatedInd) {
        this.deprecatedInd = deprecatedInd;
    }
}
