package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * EnumDetail.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.0
 * @author Mentorgen, LLC
 */
public class EnumDetail extends BaseDB {
    private String enumCd;
    private String enumDsc; // from enum_def table
    private Integer enumDetailId;
    private String enumLabel;
    private String enumValue;

    /**
     * 
     */
    public EnumDetail() {
        super();
    }

    /**
     * @param old
     */
    public EnumDetail(EnumDetail old) {
        super(old);
        if (old != null) {
            setEnumCd(old.getEnumCd());
            setEnumDsc(old.getEnumDsc());
            setEnumDetailId(old.getEnumDetailId());
            setEnumLabel(old.getEnumLabel());
            setEnumValue(old.getEnumValue());
        }
    }

    /**
     * EnumDetailId
     * 
     * @return Integer
     */
    public final Integer getEnumDetailId() {
        return enumDetailId;
    }

    /**
     * EnumDetailId
     * 
     * @param enumDetailId
     */
    public final void setEnumDetailId(Integer enumDetailId) {
        this.enumDetailId = enumDetailId;
    }

    /**
     * EnumCd
     * 
     * @return String
     */
    public final String getEnumCd() {
        return enumCd;
    }

    /**
     * EnumCd
     * 
     * @param enumCd
     */
    public final void setEnumCd(String enumCd) {
        this.enumCd = enumCd;
    }

    /**
     * EnumDetailLabel
     * 
     * @return String
     */
    public final String getEnumLabel() {
        return enumLabel;
    }

    /**
     * EnumLabel
     * 
     * @param enumLabel
     */
    public final void setEnumLabel(String enumLabel) {
        this.enumLabel = enumLabel;
    }

    /**
     * Getter for property enumDsc.
     * 
     * @return Value of property enumDsc.
     * 
     */
    public final String getEnumDsc() {
        return this.enumDsc;
    }

    /**
     * Setter for property enumDsc.
     * 
     * @param enumDsc
     *            New value of property enumDsc.
     * 
     */
    public final void setEnumDsc(String enumDsc) {
        this.enumDsc = enumDsc;
    }

    /**
     * @return Returns the _enumValue.
     */
    public final String getEnumValue() {
        return enumValue;
    }

    /**
     * @param value
     *            The _enumValue to set.
     */
    public final void setEnumValue(String value) {
        enumValue = value;
    }

    public final void populate(ResultSet rs) {
        try {
            setEnumDetailId(AbstractDAO.getInteger(rs, "enum_detail_id"));
            setEnumCd(rs.getString("enum_cd"));
            setEnumLabel(rs.getString("enum_label"));
            setEnumValue(rs.getString("enum_value"));
            setEnumDsc(rs.getString("enum_dsc"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }
}
