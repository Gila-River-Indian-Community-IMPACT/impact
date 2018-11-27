package us.oh.state.epa.stars2.workflow.types;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;

/**
 * The base class used by most classes that want to implement the
 * <code>Type</code> interface. Provides basic facilites for holding metadata
 * and for validation. A subclass must implement at least the following <br>
 * <code>
 *  public String toString();
 *  public void fromString(String value);
 *  public Object value();
 *  public void value(Object object);
 *  public boolean validate();
 *  protected void typeInit();
 * </code><br>
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
public abstract class AbstractType implements Type {
    protected String stringValue;
    protected Object objectValue;
    private boolean valid = true;
    private String message;

    public void init(DataDetail dd) {
        stringValue = dd.getDataDetailVal();
    }
    /**
     * Convert object to a String
     * 
     * @return String repesentation of object
     */
    public String toString() {
        return stringValue;
    }

    /**
     * Covert object from String to internal representation
     */
    public void fromString(String value) {
        if (value == null || value.length() == 0) {
            objectValue = null;
            valid(true);
        }
    }

    /**
     * Return the value of the object
     * 
     * @return the value of the object
     */
    public Object value() {
        return objectValue;
    }

    /**
     * Sets the internal object to the given object
     * 
     * @param object
     *            value to set internal object to
     */
    public void value(Object object) {
        if (object == null) {
            objectValue = null;
            stringValue = null;
            valid(true);
        }
    }

    /**
     * Validate the object
     * 
     * @return True the object is valid, false the object is not valid
     */
    public boolean validate() {
        if (objectValue == null || stringValue == null) {
            valid(true);
        }

        return valid();
    }

    public String getErrorMessage() {
        return message;
    }

    protected void setErrorMessage(String errorMessage) {
        message = errorMessage;
    }

    public boolean valid() {
        return valid;
    }

    protected void valid(boolean inValid) {
        if (inValid) {
            message = null;
        }

        this.valid = inValid;
    }
    
    /**
     * Generates an error message saying the caller passed the wrong type
     * to the <code>value(object)</code> method.
     * (you get this message by calling <code>errorMessage()
     * </code>). Also sets the valid flag to <code>false</code>
     */
    protected void generateCannotCastMessage(Object attempted) {
        StringBuffer b = new StringBuffer("You cannot change the value of ");
        b.append(" using a ");
        b.append(attempted.getClass().getName());
        b.append(".");
        setErrorMessage(b.toString());
        valid(false);
    }
}
