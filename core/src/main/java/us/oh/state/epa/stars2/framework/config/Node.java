package us.oh.state.epa.stars2.framework.config;

import java.io.Serializable;

/**
 * An XML tree Node.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version Revision: 1.5
 * @author Author: wilcoxa
 */
public interface Node extends Serializable {

    /**
     * Retrieves the name of the node
     * 
     * @return String name of node
     */
    String getName();

    /**
     * Retrieves full path of node
     * 
     * @return String full path of node
     */
    String getFullPath();

    /**
     * Retrieves text of node
     * 
     * @return String text of node
     */
    String getText();

    /**
     * Retrieves value of given propertyName as a String
     * 
     * @param propertyName
     *            Property Name of value to return
     * @return value for given propertyName, returned as String, or null if
     *         propertyName not present
     */
    String getAsString(String propertyName);

    /**
     * Retrieves value of given propertyName as a String
     * 
     * @param propertyName
     *            Property Name of value to return
     * @param defaultValue
     *            Default value of property to return if property not set
     * @return value for given propertyName, returned as String
     */
    String getAsString(String propertyName, String defaultValue);

    /**
     * Retrieves value of given propertyName as a int
     * 
     * @param propertyName
     *            Property Name of value to return
     * @return value for given propertyName, returned as an int, or null if
     *         propertyName not present
     */
    int getAsInt(String propertyName);

    /**
     * Retrieves value of given propertyName as a int
     * 
     * @param propertyName
     *            Property Name of value to return
     * @param defaultValue
     *            Default value of property to return if property not set
     * @return value for given propertyName, returned as an int
     */
    int getAsInt(String propertyName, int defaultValue);

    /**
     * Retrieves value of given propertyName as a double.
     * 
     * @param propertyName
     *            Property Name of value to return
     * @return value for given propertyName, returned as an double, or null if
     *         propertyName not present
     */
    double getAsDouble(String propertyName);

    /**
     * Retrieves value of given propertyName as a double.
     * 
     * @param propertyName
     *            Property Name of value to return
     * @param defaultValue
     *            Default value of property to return if property not set
     * @return value for given propertyName, returned as a double.
     */
    double getAsDouble(String propertyName, double defaultValue);

    /**
     * Retrieves value of given propertyName as a boolean
     * 
     * @param propertyName
     *            Property Name of value to return
     * @return value for given propertyName. If propertyName not set, false is
     *         returned
     */
    boolean getAsBoolean(String propertyName);

    /**
     * Retrieves value of given propertyName as a boolean
     * 
     * @param propertyName
     *            Property Name of value to return
     * @param defaultValue
     *            Default value of property to return if property not set
     * @return value for given propertyName, returned as an int
     */
    boolean getAsBoolean(String propertyName, boolean defaultValue);

    /**
     * Checks if attribute exists in node.
     * 
     * @param propertyName
     *            Attribute name to check existence
     * @return true attribute exists, false is doesn't
     */
    boolean attributeExists(String propertyName);

    /**
     * Retrieves an array of all Attribute names for this node
     * 
     * @return An array containing all attribute names for this node
     */
    String[] getAttributeNames();
}
