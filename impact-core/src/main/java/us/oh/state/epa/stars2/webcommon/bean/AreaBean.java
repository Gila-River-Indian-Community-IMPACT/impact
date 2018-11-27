package us.oh.state.epa.stars2.webcommon.bean;

/**
 * AreaMapBean
 * 
 * @author yehp
 * 
 */
@SuppressWarnings("serial")
public class AreaBean implements java.io.Serializable {
    private final static String defaultReturn = " ";
    private String shape;
    private String coords;
    private String href;
    private String title;

    /**
     * @param shape
     * @param coords
     * @param href
     */
    public AreaBean(String shape, String coords, String href) {
        this.shape = shape;
        this.coords = coords;
        this.href = href;
    }

    /**
     * @param shape
     * @param coords
     * @param _href
     * @param title
     */
    public AreaBean(String shape, String coords, String href, String title) {
        this.title = title;
        this.shape = shape;
        this.coords = coords;
        this.href = href;
    }

    public final String getTitle() {
        String ret = defaultReturn;
        if (title != null) {
            ret = title;
        }

        return ret;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final String getCoords() {
        String ret = defaultReturn;
        if (coords != null) {
            ret = coords;
        }

        return ret;
    }

    public final void setCoords(String coords) {
        this.coords = coords;
    }

    public final String getHref() {
        String ret = defaultReturn;
        if (href != null) {
            ret = href;
        }

        return ret;
    }

    public final void setHref(String href) {
        this.href = href;
    }

    public final String getShape() {
        String ret = defaultReturn;
        if (shape != null) {
            ret = shape;
        }

        return ret;
    }

    public final void setShape(String shape) {
        this.shape = shape;
    }

    /**
     * @param name
     * @param value
     */
    public final void set(String name, String value) {
        name = name.trim();
        value = value.trim();
        if (name.equalsIgnoreCase("SHAPE")) {
            shape = value;
        } else if (name.equalsIgnoreCase("COORDS")) {
            coords = value;
        } else if (name.equalsIgnoreCase("href")) {
            href = value;
        } else if (name.equalsIgnoreCase("title")) {
            title = value;
        }
    }
}
