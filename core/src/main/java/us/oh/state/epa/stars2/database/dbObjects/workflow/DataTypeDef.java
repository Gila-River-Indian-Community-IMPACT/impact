/*
 * CustomDetailTypeDef.java
 *
 * Created on March 5, 2004, 12:39 PM
 */

package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author pyeh
 *
 */
public final class DataTypeDef extends BaseDB {
    private Integer dataTypeId;
    private String dataTypeNm;
    private String dataTypePanel;

    /**
     * 
     */
    public DataTypeDef() {
        super();
    }

    /**
     * @param old
     */
    public DataTypeDef(DataTypeDef old) {
        super(old);
        if (old != null) {
            setDataTypeId(old.getDataTypeId());
            setDataTypeNm(old.getDataTypeNm());
            setDataTypePanel(old.getDataTypePanel());
        }
    }

    /**
     * Getter for property dataTypeId.
     * 
     * @return Value of property dataTypeId.
     * 
     */
    public final Integer getDataTypeId() {
        return dataTypeId;
    }

    /**
     * Setter for property dataTypeId.
     * 
     * @param dataTypeId
     *            New value of property dataTypeId.
     * 
     */
    public final void setDataTypeId(Integer dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    /**
     * Getter for property dataTypeNm.
     * 
     * @return Value of property dataTypeNm.
     * 
     */
    public final String getDataTypeNm() {
        return dataTypeNm;
    }

    /**
     * Setter for property dataTypeNm.
     * 
     * @param dataTypeNm
     *            New value of property dataTypeNm.
     * 
     */
    public final void setDataTypeNm(String dataTypeNm) {
        this.dataTypeNm = dataTypeNm;
    }

    public final String getDataTypePanel() {
        return dataTypePanel;
    }

    public final void setDataTypePanel(String dataTypePanel) {
        this.dataTypePanel = dataTypePanel;
    }

    public final void populate(ResultSet rs) {
        try {
            setDataTypeId(AbstractDAO.getInteger(rs, "data_type_id"));
            setDataTypeNm(rs.getString("data_type_nm"));
            setDataTypePanel(rs.getString("data_type_panel"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}
