package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.framework.util.Utility;

public class DataDetail extends BaseDB {
    private Integer dataDetailId;
    private String dataDetailLbl;
    private String dataDetailDsc;
    private Integer dataTypeId;
    private String enumCd;
    private String minVal;
    private String maxVal;
    private String unitDefCd;
    private String unitDefLbl;
    private String dataTypeNm;
    private String formatMask;
    private String dataDetailVal;
    private boolean required;
    private boolean readOnly;
    private boolean visible;
    private ArrayList<EnumDetail> enumDetails;

    public DataDetail() {
        enumDetails = new ArrayList<EnumDetail>();
    }

    /**
     * @param old
     */
    public DataDetail(DataDetail old) {
        super(old);
        if (old != null) {
            setDataDetailId(old.getDataDetailId());
            setDataDetailLbl(old.getDataDetailLbl());
            setDataDetailDsc(old.getDataDetailDsc());
            setDataTypeId(old.getDataTypeId());
            setEnumCd(old.getEnumCd());
            setMinVal(old.getMinVal());
            setMaxVal(old.getMaxVal());
            setUnitDefCd(old.getUnitDefCd());
            setUnitDefLbl(old.getUnitDefLbl());
            setDataDetailVal(old.getDataDetailVal());
            setRequired(old.isRequired());
            setReadOnly(old.isReadOnly());
            setVisible(old.isVisible());
            setEnumDetails(old.getEnumDetails());
            setDataTypeNm(old.getDataTypeNm());
            setFormatMask(old.getFormatMask());
            setLastModified(old.getLastModified());
        }
    }

    public final void populate(ResultSet rs) {
        try {
            setDataDetailId(AbstractDAO.getInteger(rs, "data_detail_id"));
            setDataDetailLbl(rs.getString("data_detail_lbl"));
            setDataDetailDsc(rs.getString("data_detail_dsc"));
            setDataTypeId(AbstractDAO.getInteger(rs, "data_type_id"));
            setEnumCd(rs.getString("enum_cd"));
            setMinVal(rs.getString("min_val"));
            setMaxVal(rs.getString("max_val"));
            setUnitDefCd(rs.getString("unit_cd"));
            setReadOnly(AbstractDAO.translateIndicatorToBoolean(rs.getString("read_only")));
            setRequired(AbstractDAO.translateIndicatorToBoolean(rs.getString("required")));
            setVisible(AbstractDAO.translateIndicatorToBoolean(rs.getString("visible")));
            setDataTypeNm(rs.getString("data_type_nm"));
            setFormatMask(rs.getString("format_mask"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
            setUnitDefLbl(rs.getString("unit_lbl"));
            setDataDetailVal(rs.getString("default_val"));

        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }

        try {
            // data_val only in retrieveDataDetailForWorkFlow
            String datav = rs.getString("data_val");
            if (datav != null) {
                setDataDetailVal(datav);
            }
        } catch (SQLException sqle) {
            logger.debug("Optional field error: " + sqle.getMessage());
        }

    }

    /**
     * Getter for property dataTypeId.
     * 
     * @return Value of property dataTypeId.
     * 
     */
    public final Integer getDataDetailId() {
        return dataDetailId;
    }

    /**
     * Setter for property dataTypeId.
     * 
     * @param dataTypeId
     *            New value of property dataTypeId.
     * 
     */
    public final void setDataDetailId(Integer dataDetailId) {
        this.dataDetailId = dataDetailId;
    }

    /**
     * Getter for property enumCd.
     * 
     * @return Value of property enumCd.
     * 
     */
    public final String getEnumCd() {
        return enumCd;
    }

    /**
     * Setter for property enumCd.
     * 
     * @param enumCd
     *            New value of property enumCd.
     * 
     */
    public final void setEnumCd(String enumCd) {
        this.enumCd = enumCd;
    }

    /**
     * Getter for property minVal.
     * 
     * @return Value of property minVal.
     * 
     */
    public final String getMinVal() {
        return minVal;
    }

    /**
     * Setter for property minVal.
     * 
     * @param minVal
     *            New value of property minVal.
     * 
     */
    public final void setMinVal(String minVal) {
        this.minVal = minVal;
    }

    /**
     * Getter for property maxVal.
     * 
     * @return Value of property maxVal.
     * 
     */
    public final String getMaxVal() {
        return maxVal;
    }

    /**
     * Setter for property maxVal.
     * 
     * @param maxVal
     *            New value of property maxVal.
     * 
     */
    public final void setMaxVal(String maxVal) {
        this.maxVal = maxVal;
    }

    /**
     * Getter for property unitDefCd.
     * 
     * @return Value of property unitDefCd.
     * 
     */
    public final String getUnitDefCd() {
        return unitDefCd;
    }

    /**
     * Setter for property unitDefCd.
     * 
     * @param unitDefCd
     *            New value of property unitDefCd.
     * 
     */
    public final void setUnitDefCd(String unitDefCd) {
        this.unitDefCd = unitDefCd;
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

    /**
     * Getter for property formatMask.
     * 
     * @return Value of property formatMask.
     * 
     */
    public final String getFormatMask() {
        return formatMask;
    }

    /**
     * Setter for property formatMask.
     * 
     * @param formatMask
     *            New value of property formatMask.
     * 
     */
    public final void setFormatMask(String formatMask) {
        this.formatMask = formatMask;
    }

    public final String getDataDetailVal() {
        return dataDetailVal;
    }

    public final void setDataDetailVal(String dataDetailVal) {
        this.dataDetailVal = dataDetailVal;
    }

    public final Integer getDataTypeId() {
        return dataTypeId;
    }

    public final void setDataTypeId(Integer dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    public final EnumDetail[] getEnumDetails() {
        return enumDetails.toArray(new EnumDetail[0]);
    }

    public final void setEnumDetails(EnumDetail enumDetails[]) {
        this.enumDetails = Utility.createArrayList(enumDetails);
    }

    public final void addEnumDetail(EnumDetail enumDetail) {
        if (enumDetails == null) {
            enumDetails = new ArrayList<EnumDetail>();
        }

        this.enumDetails.add(enumDetail);
    }

    public final String getDataDetailLbl() {
        return dataDetailLbl;
    }

    public final void setDataDetailLbl(String dataDetailLbl) {
        this.dataDetailLbl = dataDetailLbl;
    }

    public final String getDataDetailDsc() {
        return dataDetailDsc;
    }

    public final void setDataDetailDsc(String dataDetailDsc) {
        this.dataDetailDsc = dataDetailDsc;
    }

    public final boolean isReadOnly() {
        return readOnly;
    }

    public final void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public final boolean isRequired() {
        return required;
    }

    public final void setRequired(boolean required) {
        this.required = required;
    }

    public final boolean isVisible() {
        return visible;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    public final int compareTo(Object obj) {
        if (obj instanceof DataDetail) {
            DataDetail rhs = (DataDetail) obj;

            return (this.getDataDetailId() - rhs.getDataDetailId());
        }
        return 0;
    }

    public int hashCode() {
        int result = super.hashCode();

        return result + dataDetailId.hashCode() + dataDetailLbl.hashCode()
                + dataDetailVal.hashCode();
    }

    public final boolean equals(Object obj) {
        if (obj instanceof DataDetail) {
            DataDetail rhs = (DataDetail) obj;
            return this.getDataDetailId().equals(rhs.getDataDetailId());
        }
        return false;
    }

    public final String getJspId() {
        return "ID" + dataDetailId;
    }

    public final String getUnitDefLbl() {
        return unitDefLbl;
    }

    public final void setUnitDefLbl(String unitDefLbl) {
        this.unitDefLbl = unitDefLbl;
    }
}
