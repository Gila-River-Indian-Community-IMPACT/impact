package us.oh.state.epa.stars2.webcommon.bean;

public class NameValue implements java.io.Serializable {
    private String name;
    private Object value;

    public NameValue() {
    }

    public NameValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final Object getValue() {
        return value;
    }

    public final void setValue(Object value) {
        this.value = value;
    }
}
