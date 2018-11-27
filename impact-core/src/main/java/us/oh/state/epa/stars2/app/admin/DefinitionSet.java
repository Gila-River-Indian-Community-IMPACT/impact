package us.oh.state.epa.stars2.app.admin;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.webcommon.AppBase;

public class DefinitionSet extends AppBase {
    private boolean deprecate;
    private String deprecateModel;
    private boolean update;
    private boolean create;
    private String table;
    private String label;
    private String contentType;
    private String columnPrefix;
    private ArrayList<DefinitionField> fields = new ArrayList<DefinitionField>(0);
    private String description;
    private String path;
    private String validationClass = "";
    private String importClass = "";
    private boolean custom;
    private String orderBy = "";

    public DefinitionSet() {
        logger = Logger.getLogger(this.getClass());
    }
    
    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final boolean isSupportsImport() {
        boolean ret = false;

        if (importClass.length() > 0) {
            ret = true;
        }
        return ret;
    }

    public final String getColumnPrefix() {
        return columnPrefix;
    }

    public final void addDefinitionField(DefinitionField df) {
        fields.add(df);
    }

    public final void addDefinitionField(String dbColumn, String inLabel,
            String inUpdate, String inputType, String pickListTable,
            String pickListColumn, String pickListDisplay, String maxLength) {
        DefinitionField df = new DefinitionField();
        df.setDbColumn(dbColumn);
        df.setInputType(inputType);
        df.setLabel(inLabel);
        try {
            df.setMaxLength(Integer.parseInt(maxLength));
        } catch (NumberFormatException nfe) {
            logger.error(nfe.getMessage(), nfe);
        }
        df.setPickListTable(pickListTable);
        df.setPickListColumn(pickListColumn);
        df.setPickListDisplay(pickListDisplay);
        if (inUpdate.equals("T")) {
            df.setUpdatable(true);
        } else {
            df.setUpdatable(false);
        }
        fields.add(df);
    }

    public final String[] getFieldLabels() {
        // return (String[])fields.toArray();
        String[] labels = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            DefinitionField f = fields.get(i);
            labels[i] = f.getLabel();
        }
        return labels;
    }

    public final ArrayList<DefinitionField> getFields() {
        return fields;
    }

    public final void setColumnPrefix(String columnPrefix) {
        this.columnPrefix = columnPrefix;
    }

    public final String getContentType() {
        return contentType;
    }

    public final void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final String getTable() {
        return table;
    }

    public final void setTable(String table) {
        this.table = table;
    }

    public final boolean isCreate() {
        return create;
    }

    public final void setCreate(boolean create) {
        this.create = create;
    }

    public final boolean isDeprecate() {
        return deprecate;
    }

    public final void setDeprecate(boolean deprecate) {
        this.deprecate = deprecate;
    }

    public final String getDeprecateModel() {
        return deprecateModel;
    }

    public final void setDeprecateModel(String deprecateModel) {
        this.deprecateModel = deprecateModel;
    }

    public final boolean isUpdate() {
        return update;
    }

    public final void setUpdate(boolean update) {
        this.update = update;
    }

    public final String getPath() {
        return path;
    }

    public final void setPath(String path) {
        this.path = path;
    }

    public final String getImportClass() {
        return importClass;
    }

    public final void setImportClass(String importClass) {
        this.importClass = importClass;
    }

    public final boolean isCustom() {
        return custom;
    }

    public final void setCustom(boolean custom) {
        this.custom = custom;
    }

    public final String getValidationClass() {
        return validationClass;
    }

    public final void setValidationClass(String validationClass) {
        this.validationClass = validationClass;
    }

    public final boolean isAllowImport() {
        boolean ret = false;
        
        if (importClass.length() > 0) {
            ret = true;
        }

        return ret;
    }

    public final boolean isRequiresValidation() {
        boolean ret = false;
        
        if (validationClass.length() > 0) {
            ret = true;
        }

        return ret;
    }

    public final String getOrderBy() {
        return orderBy;
    }

    public final void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
