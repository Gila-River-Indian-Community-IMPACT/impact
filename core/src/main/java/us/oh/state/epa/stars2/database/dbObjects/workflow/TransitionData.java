package us.oh.state.epa.stars2.database.dbObjects.workflow;

/**
 * TransitionData.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.3 $
 * @author $Author: kbradley $
 */
public class TransitionData implements java.io.Serializable {
    private Integer processTemplateId;
    private Integer fromActivityTemplateId;
    private Integer toActivityTemplateId;
    private String transitionDefCd;
    private String conditionVal;
    private Boolean deprecatedInd;
    private String transitionDefDsc;

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
    // fromActivityTemplateId
    // ------------------------------------------

    /**
     * FromActivityTemplateId
     * 
     * @return Integer
     */
    public final Integer getFromActivityTemplateId() {
        return fromActivityTemplateId;
    }

    /**
     * FromActivityTemplateId
     * 
     * @param fromActivityTemplateId
     */
    public final void setFromActivityTemplateId(Integer fromActivityTemplateId) {
        this.fromActivityTemplateId = fromActivityTemplateId;
    }

    // ------------------------------------------
    // toActivityTemplateId
    // ------------------------------------------

    /**
     * ToActivityTemplateId
     * 
     * @return Integer
     */
    public final Integer getToActivityTemplateId() {
        return toActivityTemplateId;
    }

    /**
     * ToActivityTemplateId
     * 
     * @param toActivityTemplateId
     */
    public final void setToActivityTemplateId(Integer toActivityTemplateId) {
        this.toActivityTemplateId = toActivityTemplateId;
    }

    // ------------------------------------------
    // transitionDefCd
    // ------------------------------------------

    /**
     * TransitionDefCd
     * 
     * @return String
     */
    public final String getTransitionDefCd() {
        return transitionDefCd;
    }

    /**
     * TransitionDefCd
     * 
     * @param transitionDefCd
     */
    public final void setTransitionDefCd(String transitionDefCd) {
        this.transitionDefCd = transitionDefCd;
    }

    // ------------------------------------------
    // conditionVal
    // ------------------------------------------

    /**
     * ConditionVal
     * 
     * @return String
     */
    public final String getConditionVal() {
        return conditionVal;
    }

    /**
     * ConditionVal
     * 
     * @param conditionVal
     */
    public final void setConditionVal(String conditionVal) {
        this.conditionVal = conditionVal;
    }

    // ------------------------------------------
    // deprecatedInd
    // ------------------------------------------

    /**
     * DeprecatedInd
     * 
     * @return String
     */
    public final Boolean getDeprecatedInd() {
        return deprecatedInd;
    }

    public final void setDeprecatedInd(String s) {
        if ((s == null) || (s.length() == 0)) {
            this.deprecatedInd = new Boolean(false);
            return;
        }

        s.trim();
        String bs = s.substring(0, 1);

        if (bs.compareToIgnoreCase("y") == 0) {
            this.deprecatedInd = new Boolean(true);
        } else {
            this.deprecatedInd = new Boolean(false);
        }
    }

    public final void setDeprecatedInd(Boolean newInd) {
        this.deprecatedInd = newInd;
    }

    // ------------------------------------------
    // transitionDefDsc
    // ------------------------------------------

    public final String getTransitionDefDsc() {
        return this.transitionDefDsc;
    }

    public final void setTransitionDefDsc(String newDsc) {
        this.transitionDefDsc = newDsc;
    }
}
