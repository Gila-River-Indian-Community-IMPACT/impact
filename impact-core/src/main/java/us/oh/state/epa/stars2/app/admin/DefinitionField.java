package us.oh.state.epa.stars2.app.admin;

import us.oh.state.epa.stars2.webcommon.AppBase;


@SuppressWarnings("serial")
public class DefinitionField extends AppBase {
    private boolean updatable;
    private String inputType;
    private String pickListTable;
    private String pickListColumn;
    private String pickListDisplay;
    private String orderBy;
    private int maxLength;
    private String dbColumn;
    private String label;

    public DefinitionField() {
    }
    
    public final String getDbColumn() {
        return dbColumn;
    }

    public final void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    public final String getInputType() {
        return inputType;
    }

    public final void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final int getMaxLength() {
        return maxLength;
    }

    public final void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public final String getPickListTable() {
        return pickListTable;
    }

    public final void setPickListTable(String pickListTable) {
        this.pickListTable = pickListTable;
    }

    public final boolean isUpdatable() {
        return updatable;
    }

    public final void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public final String getPickListColumn() {
        return pickListColumn;
    }

    public final void setPickListColumn(String pickListColumn) {
        this.pickListColumn = pickListColumn;
    }

    public final String getPickListDisplay() {
        return pickListDisplay;
    }

    public final void setPickListDisplay(String pickListDisplay) {
        this.pickListDisplay = pickListDisplay;
    }

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
}
