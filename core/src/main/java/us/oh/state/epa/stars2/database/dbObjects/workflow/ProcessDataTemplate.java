package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * ProcessDataTemplate
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
public class ProcessDataTemplate extends BaseDB {
    private Integer dataTemplateId;
    private Integer serviceDetailDefId;
    private Integer processTemplateId;
    private String dataTemplateNm;
    private Integer lineOrderNum;
    private String oeRequiredInd;
    private String serviceDetailDefNm;
    private String deprecatedInd;
    private Integer activityTemplateId;

    /**
     * 
     */
    public ProcessDataTemplate() {
        super();
    }

    /**
     * @param old
     */
    public ProcessDataTemplate(ProcessDataTemplate old) {
        super(old);
        if (old != null) {
            setDataTemplateId(old.getDataTemplateId());
            setServiceDetailDefId(old.getServiceDetailDefId());
            setProcessTemplateId(old.getProcessTemplateId());
            setDataTemplateNm(old.getDataTemplateNm());
            setLineOrderNum(old.getLineOrderNum());
            setOeRequiredInd(old.getOeRequiredInd());
            setServiceDetailDefNm(old.getServiceDetailDefNm());
            setDeprecatedInd(old.getDeprecatedInd());
            setActivityTemplateId(old.getActivityTemplateId());
        }
    }

    // ------------------------------------------
    // dataId
    // ------------------------------------------

    /**
     * DataTemplateId
     * 
     * @return Integer
     */
    public final Integer getDataTemplateId() {
        return dataTemplateId;
    }

    /**
     * DataTemplateId
     * 
     * @param dataTemplateId
     */
    public final void setDataTemplateId(Integer dataTemplateId) {
        this.dataTemplateId = dataTemplateId;
    }

    // ------------------------------------------
    // dataTemplateDefId
    // ------------------------------------------

    /**
     * ServiceDetailDefId
     * 
     * @return Integer
     */
    public final Integer getServiceDetailDefId() {
        return serviceDetailDefId;
    }

    /**
     * ServiceDetailDefId
     * 
     * @param serviceDetailDefId
     */
    public final void setServiceDetailDefId(Integer serviceDetailDefId) {
        this.serviceDetailDefId = serviceDetailDefId;
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
    // dataTemplateNm
    // ------------------------------------------

    /**
     * DataTemplateNm
     * 
     * @return String
     */
    public final String getDataTemplateNm() {
        return dataTemplateNm;
    }

    /**
     * DataTemplateNm
     * 
     * @param dataTemplateNm
     */
    public final void setDataTemplateNm(String dataTemplateNm) {
        this.dataTemplateNm = dataTemplateNm;
    }

    // ------------------------------------------
    // serviceDetailDefNm
    // ------------------------------------------

    public final String getServiceDetailDefNm() {
        return this.serviceDetailDefNm;
    }

    public final void setServiceDetailDefNm(String newName) {
        this.serviceDetailDefNm = newName;
    }

    // ------------------------------------------
    // deprecatedInd
    // ------------------------------------------

    public final String getDeprecatedInd() {
        return this.deprecatedInd;
    }

    public final void setDeprecatedInd(String dep) {
        this.deprecatedInd = dep;
    }

    /**
     * @return Returns the lineOrderNum.
     */
    public final Integer getLineOrderNum() {
        return lineOrderNum;
    }

    /**
     * @param orderNum
     *            The lineOrderNum to set.
     */
    public final void setLineOrderNum(Integer orderNum) {
        lineOrderNum = orderNum;
    }

    /**
     * @return Returns the oeRequiredInd.
     */
    public final String getOeRequiredInd() {
        return oeRequiredInd;
    }

    /**
     * @param requiredInd
     *            The oeRequiredInd to set.
     */
    public final void setOeRequiredInd(String requiredInd) {
        oeRequiredInd = requiredInd;
    }

    // ------------------------------------------
    // Activity Template Id
    // ------------------------------------------

    public final Integer getActivityTemplateId() {
        return this.activityTemplateId;
    }

    public final void setActivityTemplateId(Integer id) {
        this.activityTemplateId = id;
    }

    public final void populate(ResultSet rs) {
        try {
            setDataTemplateId(AbstractDAO.getInteger(rs, "data_template_id"));
            setDataTemplateNm(rs.getString("data_template_nm"));
            setProcessTemplateId(AbstractDAO.getInteger(rs, "process_template_id"));
            setServiceDetailDefId(AbstractDAO.getInteger(rs, "data_detail_id"));
            setServiceDetailDefNm(rs.getString("data_detail_lbl"));
            setDeprecatedInd(rs.getString("deprecated_ind"));
            setLineOrderNum(AbstractDAO.getInteger(rs, "line_order_num"));
            setActivityTemplateId(AbstractDAO.getInteger(rs, "activity_template_id"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}
