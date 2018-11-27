package us.oh.state.epa.stars2.database.adhoc;

public class DataGridCellDef extends DataGridCell implements java.io.Serializable  {
    private String dataType;
    private int maximumLength;
    private boolean allowNull = true;
    private boolean primaryKey;
    private String pickListTable = "";
    private String pickListColumn = "";
    private String pickListDisplay = "";
    private String pickListTableAlias = "";
    private String orderBy = "";
    private boolean required=true;

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getPickListColumnAlias() {
        return pickListTableAlias;
    }

    public void setPickListColumnAlias(String pickListTableAlias) {
        this.pickListTableAlias = pickListTableAlias;
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

    public final String getPickListTable() {
        return pickListTable;
    }

    public final void setPickListTable(String pickListTable) {
        this.pickListTable = pickListTable;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public final boolean isAllowNull() {
        return allowNull;
    }

    public final void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    public final String getDataType() {
        return dataType;
    }

    public final void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public final boolean isPrimaryKey() {
        return primaryKey;
    }

    public final void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public final boolean isPickList() {
        boolean ret = false;

        if (this.getPickListTable().trim().length() > 0) {
            ret = true;
        }

        return ret;
    }

    public final int getMaximumLength() {
        return maximumLength;
    }

    public final void setMaximumLength(int maximumLength) {
        this.maximumLength = maximumLength;
    }

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

}
