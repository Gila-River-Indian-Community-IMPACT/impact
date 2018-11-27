package us.oh.state.epa.stars2.app.reports;

public class ColumnHeader implements java.io.Serializable {
    private String label;
    private String width;
    private boolean editable;

    public ColumnHeader() {
    }

    public ColumnHeader(String label, String width, boolean editable) {
        this.label = label;
        this.width = width;
        this.editable = editable;
    }

    /**
     * @return Column label
     */
    public final String getLabel() {
        return label;
    }

    /**
     * @return Column Width
     */
    public final String getWidth() {
        return width;
    }

    /**
     * @return Is column editable
     */
    public final boolean isEditable() {
        return editable;
    }

    /**
     * @param label
     */
    public final void setLabel(String label) {
        this.label = label;
    }

    /**
     * @param width
     */
    public final void setWidth(String width) {
        this.width = width;
    }

    /**
     * @param editable
     */
    public final void setEditable(boolean editable) {
        this.editable = editable;
    }
}
