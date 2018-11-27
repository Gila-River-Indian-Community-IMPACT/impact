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
public class ProcessData extends BaseDB {
    private Integer dataTemplateId;
    private Integer processId;
    private Integer dataDetailId;
    private String dataValue;

    /**
     * 
     */
    public ProcessData() {
        super();
    }

    /**
     * @param old
     */
    public ProcessData(ProcessData old) {
        super(old);
        if (old != null) {
            setDataTemplateId(old.getDataTemplateId());
            setProcessId(old.getProcessId());
            setDataDetailId(old.getDataDetailId());
            setDataValue(old.getDataValue());
        }
    }

    public final Integer getDataDetailId() {
        return dataDetailId;
    }

    public final void setDataDetailId(Integer dataDetailId) {
        this.dataDetailId = dataDetailId;
    }

    public final Integer getDataTemplateId() {
        return dataTemplateId;
    }

    public final void setDataTemplateId(Integer dataTemplateId) {
        this.dataTemplateId = dataTemplateId;
    }

    public final String getDataValue() {
        return dataValue;
    }

    public final void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public final Integer getProcessId() {
        return processId;
    }

    public final void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public final void populate(ResultSet rs) {
        try {
            setDataTemplateId(AbstractDAO.getInteger(rs, "data_template_id"));
            setProcessId(AbstractDAO.getInteger(rs, "process_id"));
            setDataDetailId(AbstractDAO.getInteger(rs, "data_detail_id"));
            setDataValue(rs.getString("DATA_VAL"));
            setLastModified(AbstractDAO.getInteger(rs, "LAST_MODIFIED"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}
