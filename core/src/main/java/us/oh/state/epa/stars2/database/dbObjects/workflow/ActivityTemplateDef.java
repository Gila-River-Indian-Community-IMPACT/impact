package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * ActivityTemplateDef
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.9 $
 * @author $Author: cmeier $
 */
public class ActivityTemplateDef extends BaseDB {
    static final long serialVersionUID = 1L;
    private Integer activityTemplateDefId;
    private String activityTemplateNm;
    private String activityTemplateDsc;
    private String performerTypeCd;
    private String activityUrl;
    private String automaticClassNm;
    private Integer processTemplateId;
    private Boolean hiddenInd;
    private String performerTypeDsc;
    private String terminalInd;
    private ArrayList<Integer> depServiceDetailDefIds;
    private ArrayList<String> depServiceDetailDefNames;
    private String activityTemplateTypeCd;
    private String activityTemplateTypeDsc;
    private Integer undoActivityTemplateDefId;
    private Integer expectedDuration;
    private Integer jeopardyDuration;
    private Integer numberOfRetries;
    private Integer retryInterval;
    private String deprecatedInd;
    private String undoActivityTemplateDefNm;

    /**
     * @param old
     */
    public ActivityTemplateDef(ActivityTemplateDef old) {
        super(old);
        if (old != null) {
            setActivityTemplateDefId(old.getActivityTemplateDefId());
            setActivityTemplateNm(old.getActivityTemplateNm());
            setActivityTemplateDsc(old.getActivityTemplateDsc());
            setPerformerTypeCd(old.getPerformerTypeCd());
            setActivityUrl(old.getActivityUrl());
            setAutomaticClassNm(old.getAutomaticClassNm());
            setProcessTemplateId(old.getProcessTemplateId());
            setHiddenInd(old.getHiddenInd());
            setPerformerTypeDsc(old.getPerformerTypeDsc());
            setTerminalInd(old.getTerminalInd());
            Integer[] ids = old.getDependentDetailIds();
            for (Integer id : ids)
                addDependentDetailId(id);
            String[] nms = old.getDependentDetailNames();
            for (String nm : nms)
                addDependentDetailName(nm);
            setActivityTemplateTypeCd(old.getActivityTemplateTypeCd());
            setActivityTemplateTypeDsc(old.getActivityTemplateTypeDsc());
            setUndoActivityTemplateDefId(old.getUndoActivityTemplateDefId());
            setExpectedDuration(old.getExpectedDuration());
            setJeopardyDuration(old.getJeopardyDuration());
            setNumberOfRetries(old.getNumberOfRetries());
            setRetryInterval(old.getRetryInterval());
            setDeprecatedInd(old.getDeprecatedInd());
            setUndoActivityTemplateDefNm(old.getUndoActivityTemplateDefNm());
        }
    }

    /**
     * Constructor.
     */
    public ActivityTemplateDef() {
        expectedDuration = new Integer(-1);
        jeopardyDuration = new Integer(-1);
        depServiceDetailDefIds = new ArrayList<Integer>();
        depServiceDetailDefNames = new ArrayList<String>();
    }

    public final Integer getNumberOfRetries() {
        return numberOfRetries;
    }

    public final void setNumberOfRetries(Integer retryCnt) {
        numberOfRetries = retryCnt;
    }

    public final Integer getRetryInterval() {
        return retryInterval;
    }

    public final void setRetryInterval(Integer newInterval) {
        retryInterval = newInterval;
    }

    public final String getDeprecatedInd() {
        return deprecatedInd;
    }

    public final void setDeprecatedInd(String depInd) {
        deprecatedInd = depInd;
    }

    // ------------------------------------------
    // activityTemplateDefId
    // ------------------------------------------

    /**
     * ActivityTemplateDefId
     * 
     * @return Integer
     */
    public final Integer getActivityTemplateDefId() {
        return activityTemplateDefId;
    }

    /**
     * ActivityTemplateDefId
     * 
     * @param activityTemplateDefId
     */
    public final void setActivityTemplateDefId(Integer inActivityTemplateDefId) {
        activityTemplateDefId = inActivityTemplateDefId;
    }

    // ------------------------------------------
    // activityTemplateTypeCd
    // ------------------------------------------

    public final String getActivityTemplateTypeCd() {
        return activityTemplateTypeCd;
    }

    public final void setActivityTemplateTypeCd(String cd) {
        activityTemplateTypeCd = cd;
    }

    // ------------------------------------------
    // activityTemplateTypeDsc
    // ------------------------------------------

    public final String getActivityTemplateTypeDsc() {
        return activityTemplateTypeDsc;
    }

    public final void setActivityTemplateTypeDsc(String dsc) {
        activityTemplateTypeDsc = dsc;
    }

    // ------------------------------------------
    // undoActivityTemplateDefId
    // ------------------------------------------

    public final Integer getUndoActivityTemplateDefId() {
        return undoActivityTemplateDefId;
    }

    public final void setUndoActivityTemplateDefId(Integer id) {
        undoActivityTemplateDefId = id;
    }

    public final String getUndoActivityTemplateDefNm() {
        return undoActivityTemplateDefNm;
    }

    public final void setUndoActivityTemplateDefNm(String newName) {
        undoActivityTemplateDefNm = newName;
    }

    // ------------------------------------------
    // terminalInd
    // ------------------------------------------

    public final void setTerminalInd(String ind) {
        terminalInd = ind;
    }

    public final String getTerminalInd() {
        if ((terminalInd == null) || (terminalInd.length() == 0)) {
            terminalInd = "N";
        }

        return terminalInd;
    }

    // ------------------------------------------
    // activityTemplateNm
    // ------------------------------------------

    /**
     * ActivityTemplateNm
     * 
     * @return String
     */
    public final String getActivityTemplateNm() {
        return activityTemplateNm;
    }

    /**
     * ActivityTemplateNm
     * 
     * @param activityTemplateNm
     */
    public final void setActivityTemplateNm(String inActivityTemplateNm) {
        activityTemplateNm = inActivityTemplateNm;
    }

    // ------------------------------------------
    // activityTemplateDsc
    // ------------------------------------------

    /**
     * ActivityTemplateDsc
     * 
     * @return String
     */
    public final String getActivityTemplateDsc() {
        return activityTemplateDsc;
    }

    /**
     * ActivityTemplateDsc
     * 
     * @param activityTemplateDsc
     */
    public final void setActivityTemplateDsc(String inActivityTemplateDsc) {
        activityTemplateDsc = inActivityTemplateDsc;
    }

    // ------------------------------------------
    // performerTypeCd
    // ------------------------------------------

    /**
     * PerformerTypeCd
     * 
     * @return String
     */
    public final String getPerformerTypeCd() {
        return performerTypeCd;
    }

    /**
     * PerformerTypeCd
     * 
     * @param performerTypeCd
     */
    public final void setPerformerTypeCd(String inPerformerTypeCd) {
        performerTypeCd = inPerformerTypeCd;
    }

    // ------------------------------------------
    // activityUrl
    // ------------------------------------------

    /**
     * ActivityUrl
     * 
     * @return String
     */
    public final String getActivityUrl() {
        return activityUrl;
    }

    /**
     * ActivityUrl
     * 
     * @param activityUrl
     */
    public final void setActivityUrl(String inActivityUrl) {
        activityUrl = inActivityUrl;
    }

    // ------------------------------------------
    // automaticClassNm
    // ------------------------------------------

    /**
     * AutomaticClassNm
     * 
     * @return String
     */
    public final String getAutomaticClassNm() {
        return automaticClassNm;
    }

    /**
     * AutomaticClassNm
     * 
     * @param automaticClassNm
     */
    public final void setAutomaticClassNm(String inAutomaticClassNm) {
        automaticClassNm = inAutomaticClassNm;
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
    public final void setProcessTemplateId(Integer inProcessTemplateId) {
        processTemplateId = inProcessTemplateId;
    }

    // ------------------------------------------
    // expectedDuration
    // ------------------------------------------

    public final Integer getExpectedDuration() {
        return expectedDuration;
    }

    public final void setExpectedDuration(Integer newDur) {
        expectedDuration = newDur;
    }

    // ------------------------------------------
    // Jeopardy Duration
    // ------------------------------------------

    public final Integer getJeopardyDuration() {
        return jeopardyDuration;
    }

    public final void setJeopardyDuration(Integer newDur) {
        jeopardyDuration = newDur;
    }

    // ------------------------------------------
    // hiddenInd
    // ------------------------------------------

    // NOTE: If we ever do a base class for all DB objects, a method
    // similar to this should be added to the base class for setting
    // Boolean data members.

    public final void setHiddenInd(String s) {
        if ((s == null) || (s.length() == 0)) {
            hiddenInd = new Boolean(false);
            return;
        }

        s.trim();
        String bs = s.substring(0, 1);

        if (bs.compareToIgnoreCase("y") == 0) {
            hiddenInd = new Boolean(true);
        } else {
            hiddenInd = new Boolean(false);
        }
    }

    public final void setHiddenInd(Boolean b) {
        hiddenInd = b;
    }

    public final Boolean getHiddenInd() {
        return hiddenInd;
    }

    public final String getHiddenIndStr() {
        if (hiddenInd == null || !hiddenInd.booleanValue()) {
            return new String("N");
        }

        return new String("Y");
    }

    // ------------------------------------------
    // performerTypeDsc
    // ------------------------------------------

    public final void setPerformerTypeDsc(String newDsc) {
        performerTypeDsc = newDsc;
    }

    public final String getPerformerTypeDsc() {
        return performerTypeDsc;
    }

    // ----------------------------------------------
    // Identifies dependent service detail def Ids
    // ----------------------------------------------

    public final void addDependentDetailId(Integer dep) {
        depServiceDetailDefIds.add(dep);
    }

    public final void setDependentDetailIds(ArrayList<Integer> deps) {
        if (deps == null) {
            depServiceDetailDefIds = new ArrayList<Integer>();
        }
        depServiceDetailDefIds = deps;
    }

    public final Integer[] getDependentDetailIds() {
        return depServiceDetailDefIds.toArray(new Integer[0]);
    }

    // ----------------------------------------------
    // Identifies dependent service detail def Ids
    // ----------------------------------------------

    public final void addDependentDetailName(String name) {
        depServiceDetailDefNames.add(name);
    }

    /*
     * public final void setDependentDetailNames(String[] deps) {
     * depServiceDetailDefNames = new ArrayList<String>();
     * 
     * if ((deps == null) || (deps.length == 0)) { return; }
     * 
     * for (String tempStr : deps) { addDependentDetailName(tempStr); } }
     */
    public final void setDependentDetailNames(ArrayList<String> deps) {
        if (deps == null) {
            depServiceDetailDefNames = new ArrayList<String>();
        }
        depServiceDetailDefNames = deps;
    }

    public final String[] getDependentDetailNames() {
        return depServiceDetailDefNames.toArray(new String[0]);
    }

    /**
     * Creates an <code>ActivityTemplateDef</code> object based on the current
     * row of the <tt>ResultSet</tt>. The ResultSet is assumed to be
     * "pointing" to a valid row, and is not altered in any way by this method.
     * 
     * MUST: call populateDependentDetails see example retrieveActivityTypes
     * 
     * @param rs
     *            ResultSet containing ActivityTemplateDef data.
     * 
     * @return ActivityTemplateDef object constructed from database data.
     * 
     * @throws java.lang.Exception
     *             Database/ResultSet access error.
     */
    public final void populate(ResultSet rs) {
        try {
            String actTemplTypeCd = rs.getString("activity_template_type_cd");
            String actTemplTypeDsc = rs.getString("activity_template_type_dsc");

            // Build the object and set its values.
            setActivityTemplateDefId(AbstractDAO.getInteger(rs,
                    "activity_template_def_id"));
            setActivityTemplateNm(rs.getString("activity_template_nm"));
            setActivityTemplateDsc(rs.getString("activity_template_dsc"));
            setPerformerTypeCd(rs.getString("performer_type_cd"));
            setAutomaticClassNm(rs.getString("automatic_class_nm"));
            setProcessTemplateId(AbstractDAO.getInteger(rs,
                    "process_template_id"));
            setHiddenInd(new Boolean(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("hidden_ind"))));
            setActivityUrl(rs.getString("activity_url"));
            setPerformerTypeDsc(rs.getString("performer_type_dsc"));
            setTerminalInd(rs.getString("terminal_ind"));
            setActivityTemplateTypeCd(actTemplTypeCd);
            setActivityTemplateTypeDsc(actTemplTypeDsc);
            setUndoActivityTemplateDefId(AbstractDAO.getInteger(rs,
                    "undo_activity_template_def_id"));
            setExpectedDuration(AbstractDAO.getInteger(rs, "expected_duration"));
            setJeopardyDuration(AbstractDAO.getInteger(rs, "jeopardy_duration"));
            setNumberOfRetries(AbstractDAO.getInteger(rs, "number_of_retries"));
            setRetryInterval(AbstractDAO.getInteger(rs, "retry_interval"));
            setDeprecatedInd(rs.getString("deprecated_ind"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));

        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}
