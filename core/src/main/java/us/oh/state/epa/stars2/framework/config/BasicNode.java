package us.oh.state.epa.stars2.framework.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * An implementation of Node.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version Revision: 1.2
 * @author Author: wilcoxa
 */
@SuppressWarnings("serial")
public class BasicNode implements Node {
    private String text;
    private String name;
    private String fullPath;
    private Map<String, String> attributes = new HashMap<String, String>();

    /**
     * @param name
     * @param fullPath
     */
    public BasicNode(String name, String fullPath) {
        this.name = name.intern();
        if (fullPath != null) {
        	this.fullPath = fullPath.intern();
        }
    }

    /**
     * @see Node#getName()
     */
    public final String getName() {
        return name;
    }

    /**
     * @see Node#getFullPath()
     */
    public final String getFullPath() {
        return fullPath;
    }

    /**
     * @see Node#getText()
     */
    public final String getText() {
        return text;
    }

    /**
     * @see Node#getAsString(String propertyName)
     */
    public final String getAsString(String propertyName) {
        return attributes.get(propertyName);
    }

    /**
     * @see Node#getAsString(String propertyName, String defaultValue)
     */
    public final String getAsString(String propertyName, String defaultValue) {
        String value = getAsString(propertyName);
        return (value != null) ? value : defaultValue;
    }

    /**
     * @see Node#getAsInt(String propertyName)
     */
    public final int getAsInt(String propertyName) {
        return getAsInt(propertyName, 0);
    }

    /**
     * @see Node#getAsInt(String propertyName, int defaultValue)
     */
    public final int getAsInt(String propertyName, int defaultValue) {
        String value = attributes.get(propertyName);
        int ret = defaultValue;

        if (value != null) {
            ret = Integer.parseInt(value);
        }

        return ret;
    }

    /**
     * @see Node#getAsDouble(String propertyName)
     */
    public double getAsDouble(String propertyName) {
        return getAsDouble(propertyName, 0.0);
    }

    /**
     * @see Node#getAsDouble(String propertyName, double defaultValue)
     */
    public double getAsDouble(String propertyName, double defaultValue) {

        String value = attributes.get(propertyName);

        if (value == null) {
            return defaultValue;
        }

        return Double.parseDouble(value);
    }

    /**
     * @see Node#getAsBoolean(String propertyName)
     */
    public final boolean getAsBoolean(String propertyName) {
        return getAsBoolean(propertyName, false);
    }

    /**
     * @see Node#getAsBoolean(String propertyName, boolean defaultValue)
     */
    public final boolean getAsBoolean(String propertyName, boolean defaultValue) {
        String value = attributes.get(propertyName);

        if (value == null) {
            return defaultValue;
        }

        String token = value.trim().toLowerCase().intern();
        return (token == "true" || token == "yes");
    }

    /**
     * @see Node#attributeExists(String propertyName)
     */
    public final boolean attributeExists(String propertyName) {
        return attributes.get(propertyName) != null;
    }

    /**
     * @see Node#getAttributeNames()
     */
    public final String[] getAttributeNames() {
        return attributes.keySet().toArray(new String[0]);
    }

    /**
     * Saves an attribute name-value pair. Both <code>name</code> and
     * <code>value</code> must be non-empty Strings. If either is
     * <tt>null</tt> or empty, then the name-value pair will not be added to
     * attributes settings.
     * 
     * @param name
     *            The name of the attribute we want to set.
     * @param value
     *            The value we want to associate with this name.
     */
    public final void setAttribute(String name, String value) {
        if ((name != null) && (name.length() > 0) && (value != null)
                && (value.length() > 0)) {
            this.set(name, value);
        }
    }

    // ---------------------------------------------------------- package
    // visibility

    final void set(String name, String value) {
        synchronized (attributes) {
            attributes.put(name, value);
        }
    }

    final Properties getAttributes() {
        Properties p = new Properties();
        p.putAll(attributes);
        return p;
    }

    final void setText(String text) {
        this.text = text;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        String[] ans = this.getAttributeNames();
        for (int i = 0; i < ans.length; i++) {
            sb.append(" ");
            sb.append(ans[i]);
            sb.append("=\"");
            sb.append(this.getAsString(ans[i]));
            sb.append("\"");
        }
        return sb.toString();
    }
}
