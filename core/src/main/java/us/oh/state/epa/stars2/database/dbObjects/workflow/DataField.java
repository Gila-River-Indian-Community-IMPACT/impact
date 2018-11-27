package us.oh.state.epa.stars2.database.dbObjects.workflow;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

/**
 * <p>
 * Title: DataField
 * </p>
 * 
 * <p>
 * Description: DataFields are used in the workflow to hold data values which in
 * turn are used to make branching decisions.
 * </p>
 * 
 * <p>
 * This object is closely related to the "Process_Data" and
 * "Process_Data_Template" tables.
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

public class DataField extends BaseDB {
    private Integer dataTypeId;
    private Integer customDetailTypeId;
    private Integer activityTemplateId;
    private Integer dataTemplateId;
    private Integer serviceDetailId;
    private Integer processId;
    private String dataName;
    private String dataValue;
    private String enumCd;
    private String unitCd;
    private String dataDetailDsc;
    private String dataDetailLbl;
    private boolean readOnly;
    private boolean required;
    private boolean visible;
    private Integer minVal;
    private Integer maxVal;
    private DataDetail dataDetail;

    /**
     * Constructor. An object created by this constructor will not be suitable
     * or valid for storage in the database.
     * 
     */
    public DataField() {
    }

    /**
     * Constructor. An object created by this constructor will not be suitable
     * or valid for storage in the database.
     * 
     * @param fieldName
     *            The name of this data field.
     */
    public DataField(String fieldName) {
        dataName = fieldName;
        setDirty(true);
    }

    /**
     * Legacy constructor. An object created via this constructor is not
     * specified enough for storage in the database.
     * 
     * @param fieldName
     *            The name of this data field.
     * @param dataTemplateId
     *            Foreign key to "Process_Data_Template" table.
     * @param dataTypeId
     *            Foreign key to "Custom_Detail_Type_Def" table.
     * @param currentValue
     *            The current value of the field.
     */
    public DataField(String fieldName, Integer dataTemplateIdIn,
            Integer customDetailTypeIdIn, String currentValue) {
        dataName = fieldName;
        dataTemplateId = dataTemplateIdIn;
        customDetailTypeId = customDetailTypeIdIn;
        dataValue = currentValue;
        setDirty(true);
    }

    /**
     * Fully specified constructor. An object created via this constructor is
     * valid and can be stored in the database (provided referential integrity
     * constraints are satisfied).
     * 
     * @param fieldName
     *            The name of this data field.
     * @param dataTemplateId
     *            Foreign key to "Process_Data_Template" table.
     * @param customDetailTypeId
     *            Foreign key to "Custom_Detail_Type_Def" table.
     * @param processId
     *            Foreign key to "Process" table.
     * @param currentValue
     *            The current value of the field.
     */
    public DataField(String fieldName, Integer dataTemplateIdIn,
            Integer customDetailTypeIdIn, Integer processIdIn,
            String currentValue) {
        dataName = fieldName;
        dataTemplateId = dataTemplateIdIn;
        customDetailTypeId = customDetailTypeIdIn;
        processId = processIdIn;
        dataValue = currentValue;

        setDirty(true);
    }

    /**
     * @param old
     */
    public DataField(DataField old) {
        super(old);
        if (old != null) {
            setDataTypeId(old.getDataTypeId());
            setCustomDetailTypeId(old.getCustomDetailTypeId());
            setActivityTemplateId(old.getActivityTemplateId());
            setDataTemplateId(old.getDataTemplateId());
            setServiceDetailId(old.getServiceDetailId());
            setProcessId(old.getProcessId());
            setDataName(old.getDataName());
            setDataValue(old.getDataValue());
            setEnumCd(old.getEnumCd());
            setUnitCd(old.getUnitCd());
            setDataDetailDsc(old.getDataDetailDsc());
            setDataDetailLbl(old.getDataDetailLbl());
            setReadOnly(old.isReadOnly());
            setRequired(old.isRequired());
            setVisible(old.isVisible());
            setMinVal(old.getMinVal());
            setMaxVal(old.getMaxVal());
            setDataDetail(old.getDataDetail());
        }
    }

    public final String toPrintable() {
        return "DataField: dataTypeId=" + (dataTypeId==null?"-":dataTypeId.toString()) + ",activityTemplateId=" + (activityTemplateId==null?"-":activityTemplateId.toString()) +
             ",dataTemplateId=" + (dataTemplateId==null?"-":dataTemplateId.toString()) + ",processId=" + (processId==null?"-":processId.toString()) + ",dataName=" + dataName +
             ",dataValue=" + dataValue + ",enumCd=" + enumCd + ",dataDetailLbl=" + dataDetailLbl + ",dataDetailDsc=" + dataDetailDsc;
    }
    
    public final DataDetail getDataDetail() {
        return dataDetail;
    }

    public final void setDataDetail(DataDetail dataDetail) {
        this.dataDetail = dataDetail;
    }

    public final String getDataDetailDsc() {
        return dataDetailDsc;
    }

    public final void setDataDetailDsc(String dataDetailDsc) {
        this.dataDetailDsc = dataDetailDsc;
    }

    public final String getDataDetailLbl() {
        return dataDetailLbl;
    }

    public final void setDataDetailLbl(String dataDetailLbl) {
        this.dataDetailLbl = dataDetailLbl;
    }

    public final Integer getMaxVal() {
        return maxVal;
    }

    public final void setMaxVal(Integer maxVal) {
        this.maxVal = maxVal;
    }

    public final Integer getMinVal() {
        return minVal;
    }

    public final void setMinVal(Integer minVal) {
        this.minVal = minVal;
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

    public final String getUnitCd() {
        return unitCd;
    }

    public final void setUnitCd(String unitCd) {
        this.unitCd = unitCd;
    }

    public final boolean isVisible() {
        return visible;
    }

    public final void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns the current data value for this field. The <tt>String</tt>
     * value may need to be passed to an appropriate object constructor, for
     * example <tt>Integer</tt> or <tt>Double</tt>.
     * 
     * @return String representation of the current field value.
     */
    public final String getDataValue() {
        return dataValue;
    }

    /**
     * Sets a new field value for this object. The <code>newValue</code> is
     * not validated in any way by this method.
     * 
     * @param newValue
     *            String representation of a new field value.
     */
    public final void setDataValue(String newValue) {
        dataValue = newValue;
        setDirty(true);
    }

    /**
     * Returns <tt>true</tt> if this object has been significantly modified.
     * Basically, a key value has been modified and this object should be
     * written to the database in order to preserve that value.
     * 
     * @return boolean <tt>true</tt> if database write is needed.
     */
    public final boolean isModified() {
        return isDirty();
    }

    /**
     * Clears the "modified" flag. This method should be called immediately
     * after writing this object to the database.
     */
    public final void clearModified() {
        setDirty(false);
    }

    /**
     * Sets the process Id associated with this object. This is a foreign key
     * value to the "Process" table and associates this <tt>DataField</tt>
     * with a specific <tt>ProcessFlow</tt>. This value is required for
     * storing this object in the database.
     * 
     * @param newId
     *            new process Id.
     */
    public final void setProcessId(Integer newId) {
        processId = newId;
    }

    /**
     * Returns the current process Id associated with this object.
     * 
     * @return Integer Current process Id.
     */
    public final Integer getProcessId() {
        return processId;
    }

    /**
     * Sets the name of the data field to a new value. The "newName" should be
     * the "data_template_nm" associated with the "data_template_id" in the
     * "Process_Data_Template" table.
     * 
     * @param newName
     *            New data template name.
     */
    public final void setDataName(String newName) {
        dataName = newName;
    }

    /**
     * Returns the current data name.
     * 
     * @return String data template name.
     */
    public final String getDataName() {
        return dataName;
    }

    /**
     * Sets the data template Id for this data field. This is a foreign key
     * value to the "Process_Data_Template" table and associates this
     * <code>DataField</code> with a data template.
     * 
     * @param newId
     *            New data template Id.
     */
    public final void setDataTemplateId(Integer newId) {
        dataTemplateId = newId;
        setDirty(true);
    }

    /**
     * Returns the current data template Id for this object.
     * 
     * @return Integer current data template Id.
     */
    public final Integer getDataTemplateId() {
        return dataTemplateId;
    }

    /**
     * Sets the service detail Id for this data field. This is a foreign key
     * value to the "Service_Detail" table and associates this object with
     * service detail data (user-defined data associated with a workflow).
     * 
     * @param newId
     *            new service detail Id.
     */
    public final void setServiceDetailId(Integer newId) {
        serviceDetailId = newId;
        setDirty(true);
    }

    /**
     * Returns the current service detail Id for this object.
     * 
     * @return Integer current service detail Id.
     */
    public final Integer getServiceDetailId() {
        return serviceDetailId;
    }

    /**
     * Sets the data type Id for this data field. This is an integer value from
     * the "data_type_id" column of the "Data_Type_Def" table. This value is
     * included as a convenience and is not persisted to the database.
     * 
     * @param newTypeId
     *            new data type Id.
     */
    public final void setDataTypeId(Integer newTypeId) {
        dataTypeId = newTypeId;
    }

    /**
     * Returns the current data type Id for this object. This specifies how to
     * handle the "data value" associated with this object, e.g., "Integer",
     * etc.
     * 
     * @return Integer data type Id.
     */
    public final Integer getDataTypeId() {
        return dataTypeId;
    }

    public final Integer getCustomDetailTypeId() {
        return customDetailTypeId;
    }

    public final void setCustomDetailTypeId(Integer detailTypeId) {
        customDetailTypeId = detailTypeId;
    }

    public final void set_dataTemplateId(Integer templateId) {
        dataTemplateId = templateId;
    }

    public final String getEnumCd() {
        return enumCd;
    }

    public final void setEnumCd(String enumCd) {
        this.enumCd = enumCd;
    }

    public final EnumDetail[] getEnums() {
        return dataDetail.getEnumDetails();
    }

    public final void setEnums(EnumDetail[] enums) {
        this.dataDetail.setEnumDetails(enums);
    }

    public final Integer getActivityTemplateId() {
        return activityTemplateId;
    }

    public final void setActivityTemplateId(Integer activityTemplateId) {
        this.activityTemplateId = activityTemplateId;
    }

    /**
     * Creates a <code>DataField</code> object based on the current row of the
     * <tt>ResultSet</tt>. The ResultSet is assumed to be "pointing" to a
     * valid row, and is not altered in any way by this method.
     * 
     * @param rs
     *            ResultSet containing DataField data.
     * 
     * @return DataField object constructed from database data.
     * 
     * @throws java.lang.Exception
     *             Database/ResultSet access error.
     */
    public final void populate(ResultSet rs) {
        try {
            // Start extracting parameters needed by the constructor.

            setDataTemplateId(AbstractDAO.getInteger(rs, "data_template_id"));
            setProcessId(AbstractDAO.getInteger(rs, "process_id"));

            setDataName(rs.getString("data_template_nm"));

            // It is very possible that we have no entry for "data_val" in the
            // Process_Data table. Use a DAOUtils method to look it up if we
            // do have a value.

            setDataValue(rs.getString("data_val"));

            // Before we can create the actual object, we need the data type.
            // Normally, this would have been done in the SQL statement.
            // However, retrieval of this value requires a multi-table "hop"
            // across tables where the foreign key can be NULL. The NULLs
            // really shouldn't be allowed, but the schema allows it anyway.
            // So, I will do the "hop" here.

            setCustomDetailTypeId(AbstractDAO.getInteger(rs, "data_detail_id"));
            setUnitCd(rs.getString("UNIT_CD"));
            setDataDetailDsc(rs.getString("DATA_DETAIL_DSC"));
            setDataDetailLbl(rs.getString("DATA_DETAIL_LBL"));
            setReadOnly(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("READ_ONLY")));
            setRequired(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("REQUIRED")));
            setVisible(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("VISIBLE")));
            setMinVal(getStringToInteger(rs.getString("MIN_VAL")));
            setMaxVal(getStringToInteger(rs.getString("MAX_VAL")));

            setActivityTemplateId(AbstractDAO.getInteger(rs,
                    "ACTIVITY_TEMPLATE_ID"));
            setDataTypeId(AbstractDAO.getInteger(rs, "DATA_TYPE_ID"));
            setEnumCd(rs.getString("ENUM_CD"));

            setLastModified(AbstractDAO.getInteger(rs, "LAST_MODIFIED"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    private Integer getStringToInteger(String string) {
        Integer ret = null;
        if (string != null) {
            ret = new Integer(string);
        }

        return ret;
    }
}
