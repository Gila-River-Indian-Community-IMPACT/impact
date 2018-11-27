package us.oh.state.epa.stars2.workflow.types;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;

/**
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @author Sam Wooster
 */
public interface Type {
    /**
     * Initializes the "type" with the supplied datafield value.
     */
    void init(DataDetail dd);

    /**
     * Returns the <code>String</code> representation of this type. The
     * default value is <code>null</code>.
     */
    String toString();

    /**
     * This method will (attempt to) convert the given <code>String</code>
     * into the underlying type. A subseqent call to <code>toString()</code>
     * will yeild <code>value</code>, regardless of whether the instance is
     * able to convert to the underlying type. For example, if you called: <br>
     * <code>integerType.fromString("abc");</code> <br>
     * calling <code>toString()</code> will return 'abc'. You can determine
     * wether or not the conversion was successful by calling <code>valid()
     * </code>.
     * If you are returned <code>false</code>, you can get the nature of the
     * error (in order to show to the end user) by calling <code>
     * errorMessage()</code>.
     * <p>
     * On the other hand, if the conversion was successful, calling
     * <code>value()</code> will return the <code>String</code> converted to
     * the underlying type. In cases where <code>fromString()</code> fails,
     * calling <code>value()
     * </code> will give you the same result that you
     * would have gotton before calling <code>fromString()</code>.
     * <p>
     * If you call <code>fromString()</code> with a <code>null</code> value,
     * both the underlying type and the <code>String</code> representation
     * will be made <code>null</code> as long as you don't have <code>
     * required='yes'</code>
     * in your metadata. In the case where the instance is required and
     * <code>null</code> is passed in, the <code>String</code>
     * representation will become <code>null</code>, the underlying object
     * will be untouched and the valid flag will be set to <code>false</code>.
     * <p>
     * In short: <br>
     * <code>
     * +---------------------------------------------------------------------+
     * |               |  non-null valid input  |    invalid input   | null* |
     * +-------------------------------------------------------------+-------+
     * | Required      |           A            |        B           |   C   |
     * +---------------+------------------------+--------------------+-------+
     * | Not Required  |           D            |        E           |   F   |
     * +-------------------------------------------------------------+-------+
     * (* zero length strings are programatically treated the same way)
     *
     *                    A
     * +-----------------------------------+
     * | valid                | yes        |
     * | String representation| new value  |
     * | Object representation| new value  |
     * +-----------------------------------+
     *
     *                    B
     * +-----------------------------------+
     * | valid                | no         |
     * | String representation| new value  |
     * | Object representation| old value  |
     * +-----------------------------------+
     *
     *                    C
     * +-----------------------------------+
     * | valid                | no         |
     * | String representation| new value  |
     * | Object representation| old value  |
     * +-----------------------------------+
     *
     *                    D
     * +-----------------------------------+
     * | valid                | yes        |
     * | String representation| new value  |
     * | Object representation| new value  |
     * +-----------------------------------+
     *
     *                    E
     * +-----------------------------------+
     * | valid                | no         |
     * | String representation| new value  |
     * | Object representation| new value  |
     * +-----------------------------------+
     *
     *                    F
     * +----------------------------------+
     * | valid                | yes       |
     * | String representation| new value |
     * | Object representation| null      |
     * +----------------------------------+
     *
     * </code>
     */
    void fromString(String value);

    /**
     * @return The current value of the underlying object. The default value is
     *         <code>null</code>
     */
    Object value();

    /**
     * The parameter must be of the matching Java type. If it is not, the state
     * of this instance will not change, but it will become invalid. If this
     * happens, you can make the object valid by calling <code>validate()</code>
     */
    void value(Object object);

    /**
     * Will return <code>false</code> if the <code>String</code> and
     * underlying repsentations don't match OR if <code>value(object)</code>
     * is called with an argument of the wrong type.
     */
    boolean valid();

    /**
     * Re-evaluate the state of the instance by comparing the <code>
     * String</code>
     * value to the underlying value. Useful if you call
     * <code>value(object)</code> and <code>valid()</code> changes from
     * <code>true</code> to <code>false</code>
     */
    boolean validate();

    /**
     * Returns the class name for the underlying object type. For example, if
     * the data is stored as an "Integer", then this method returns
     * "java.lang.Integer".
     * 
     * @return String class name for underlying data object.
     */
    String getObjectType();
}
