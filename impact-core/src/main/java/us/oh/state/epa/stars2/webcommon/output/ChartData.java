package us.oh.state.epa.stars2.webcommon.output;

import java.awt.Color;

public class ChartData implements java.io.Serializable {
    private String label;
    private String qparam;
    private Number value;
    private Color color;

    public final String getQparam() {
        return qparam;
    }

    public final void setQparam(String qparam) {
        this.qparam = qparam;
    }

    public final Color getColor() {
        return color;
    }

    public final void setColor(Color color) {
        this.color = color;
    }

    public final void setColor(String colorString) {
        this.color = new Color(Integer.parseInt(colorString, 16));
    }

    public final String getLabel() {
        return label;
    }

    public final void setLabel(String label) {
        this.label = label;
    }

    public final Number getValue() {
        return value;
    }

    public final void setValue(Number value) {
        this.value = value;
    }
}
