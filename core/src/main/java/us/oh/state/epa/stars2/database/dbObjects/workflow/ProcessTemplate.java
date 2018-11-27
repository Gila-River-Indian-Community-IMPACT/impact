package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * ProcessTemplate
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.5 $
 * @author $Author: cmeier $
 */
public class ProcessTemplate extends BaseDB {
    private Integer processTemplateId;
    private String processCd;
    private String processTemplateNm;
    private String processTemplateDsc;
    private Integer expectedDuration;
    private Integer jeopardyDuration;
    private String dynamicInd;
    private String deprecatedInd;
    private Integer earliestProvInterval;
    private Integer minProvInterval;
    private ActivityTemplate[] activityTemplate;
    private Vector<ActivityTemplate> actTemp = new Vector<ActivityTemplate>();

    /**
     * @param old
     */
    public ProcessTemplate(ProcessTemplate old) {
        super(old);
        if (old != null) {
            setProcessTemplateId(old.getProcessTemplateId());
            setProcessCd(old.getProcessCd());
            setProcessTemplateNm(old.getProcessTemplateNm());
            setProcessTemplateDsc(old.getProcessTemplateDsc());
            setExpectedDuration(old.getExpectedDuration());
            setJeopardyDuration(old.getJeopardyDuration());
            setDynamicInd(old.getDynamicInd());
            setDeprecatedInd(old.getDeprecatedInd());
            setEarliestProvInterval(old.getEarliestProvInterval());
            setMinProvInterval(old.getMinProvInterval());
            setActivityTemplate(old.getActivityTemplate());
        }
    }

    public ProcessTemplate() {
        expectedDuration = new Integer(-1);
        jeopardyDuration = new Integer(-1);
        earliestProvInterval = new Integer(-1);
        minProvInterval = new Integer(-1);
    }

    // ------------------------------------------
    // processTemplateId
    // ------------------------------------------

    /**
     * ProcessTemplateId
     * 
     * @return Integer
     */
    public final Integer getProcessTemplateId() {
        return processTemplateId;
    }

    /**
     * ProcessTemplateId
     * 
     * @param processTemplateId
     */
    public final void setProcessTemplateId(Integer processTemplateId) {
        this.processTemplateId = processTemplateId;
    }

    // ------------------------------------------
    // processCd
    // ------------------------------------------

    /**
     * ProcessCd
     * 
     * @return String
     */
    public final String getProcessCd() {
        return processCd;
    }

    /**
     * ProcessCd
     * 
     * @param processCd
     */
    public final void setProcessCd(String processCd) {
        this.processCd = processCd;
    }

    // ------------------------------------------
    // expectedDuration
    // ------------------------------------------

    /**
     * ExpectedDuration in seconds.
     * 
     * @return Integer seconds.
     */
    public final Integer getExpectedDuration() {
        return expectedDuration;
    }

    /**
     * ExpectedDuration
     * 
     * @param expectedDuration
     */
    public final void setExpectedDuration(Integer expectedDuration) {
        this.expectedDuration = expectedDuration;
    }

    // ------------------------------------------
    // jeopardyDuration
    // ------------------------------------------

    /**
     * JeopardyDuration in seconds.
     * 
     * @return Integer seconds.
     */
    public final Integer getJeopardyDuration() {
        return jeopardyDuration;
    }

    /**
     * JeopardyDuration
     * 
     * @param jeopardyDuration
     */
    public final void setJeopardyDuration(Integer jeopardyDuration) {
        this.jeopardyDuration = jeopardyDuration;
    }

    // ------------------------------------------
    // processTemplateNm
    // ------------------------------------------

    /**
     * ProcessTemplateNm
     * 
     * @return String
     */
    public final String getProcessTemplateNm() {
        return processTemplateNm;
    }

    /**
     * ProcessTemplateNm
     * 
     * @param processTemplateNm
     */
    public final void setProcessTemplateNm(String processTemplateNm) {
        this.processTemplateNm = processTemplateNm;
    }

    // ------------------------------------------
    // processTemplateDsc
    // ------------------------------------------

    /**
     * ProcessTemplateDsc
     * 
     * @return String
     */
    public final String getProcessTemplateDsc() {
        return processTemplateDsc;
    }

    /**
     * ProcessTemplateDsc
     * 
     * @param processTemplateDsc
     */
    public final void setProcessTemplateDsc(String processTemplateDsc) {
        this.processTemplateDsc = processTemplateDsc;
    }

    // ------------------------------------------
    // dynamicInd
    // ------------------------------------------

    /**
     * DynamicInd
     * 
     * @return String
     */
    public final String getDynamicInd() {
        return dynamicInd;
    }

    /**
     * DynamicInd
     * 
     * @param dynamicInd
     */
    public final void setDynamicInd(String dynamicInd) {
        this.dynamicInd = dynamicInd;
    }

    // ------------------------------------------
    // deprecatedInd
    // ------------------------------------------

    /**
     * DeprecatedInd
     * 
     * @return String
     */
    public final String getDeprecatedInd() {
        return deprecatedInd;
    }

    /**
     * DeprecatedInd
     * 
     * @param deprecatedInd
     */
    public final void setDeprecatedInd(String deprecatedInd) {
        this.deprecatedInd = deprecatedInd;
    }

    // ------------------------------------------
    // activityTemplate
    // ------------------------------------------

    /**
     * ActivityTemplate
     * 
     * @return ActivityTemplate[]
     */
    public final ActivityTemplate[] getActivityTemplate() {
        if ((this.activityTemplate == null)
                || (this.activityTemplate.length == 0)) {
            this.buildActivityTemplateArray();
        }

        return activityTemplate;
    }

    /**
     * ActivityTemplate - Returns the 'index' element of the array.
     * 
     * @param index
     * @return ActivityTemplate
     */
    public final ActivityTemplate getActivityTemplate(int index) {
        return activityTemplate[index];
    }

    /**
     * ActivityTemplate
     * 
     * @param activityTemplate
     */
    public final void setActivityTemplate(ActivityTemplate[] activityTemplate) {
        this.activityTemplate = activityTemplate;
    }

    /**
     * ActivityTemplate - Sets the 'index' element of the array to the value of
     * activityTemplate
     * 
     * @param index
     * @param activityTemplate
     */
    public final void setActivityTemplate(int index,
            ActivityTemplate activityTemplate) {
        this.activityTemplate[index] = activityTemplate;
    }

    public final void addActivityTemplate(ActivityTemplate inActivityTemplate) {
        actTemp.add(inActivityTemplate);
    }

    public final void buildActivityTemplateArray() {
        activityTemplate = actTemp.toArray(new ActivityTemplate[0]);
    }

    public final int activityTemplateArraySize() {
        return actTemp.size();
    }

    /**
     * Returns the earliest provisioning interval in seconds.
     * 
     * @return Integer Earliest provisioning interval.
     */
    public final Integer getEarliestProvInterval() {
        return this.earliestProvInterval;
    }

    /**
     * Sets the earliest provisioning interval in seconds.
     * 
     * @param newIntvl
     *            Integer Earliest provisioning interval.
     */
    public final void setEarliestProvInterval(Integer newIntvl) {
        this.earliestProvInterval = newIntvl;
    }

    public final Integer getMinProvInterval() {
        return this.minProvInterval;
    }

    public final void setMinProvInterval(Integer newIntvl) {
        this.minProvInterval = newIntvl;
    }

    public final void populate(ResultSet rs) {
        try {
            setProcessTemplateId(AbstractDAO.getInteger(rs,
                    "process_template_id"));
            setProcessCd(rs.getString("process_cd"));
            setProcessTemplateNm(rs.getString("process_template_nm"));
            setProcessTemplateDsc(rs.getString("process_template_dsc"));
            setExpectedDuration(AbstractDAO.getInteger(rs, "expected_duration"));
            setJeopardyDuration(AbstractDAO.getInteger(rs, "jeopardy_duration"));
            setDynamicInd(rs.getString("dynamic_ind"));
            setDeprecatedInd(rs.getString("deprecated_ind"));
            setEarliestProvInterval(AbstractDAO.getInteger(rs,
                    "earliest_prov_interval"));
            setMinProvInterval(AbstractDAO.getInteger(rs, "min_prov_interval"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}
