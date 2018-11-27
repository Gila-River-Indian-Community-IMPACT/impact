package us.oh.state.epa.stars2.database.dbObjects;

/**
 * A validation message represents a DB object bean property name and a text
 * description of the problem with that property's current value that makes it
 * invalid.
 * 
 * property is the Component ID in JSP page for validation pop up window.
 *      for example: <af:selectInputDate label="Effective Date :"
                        id="effectiveDate" ..... />
                     the property has to be set to "effectiveDate"
 * 
 * @author Ken Bradley
 */
public class ValidationMessage implements java.io.Serializable {
    /**
     * A convenience message to represent a property that is not valid because
     * it has not yet been set.
     */
    public static final String NOT_SET = "Property has not been set.";
    private String property_;
    private String message_;
    private String euId;
    private Severity severity_;
    private String referenceID_;
    private Integer permitId;

    public static enum Severity {
        INFO, WARNING, ERROR
    }

    /**
     * Calls this(property, NOT_SET);
     * 
     * @param property
     *            the property that that has not yet been set. (Component ID in JSP page.)
     */
    public ValidationMessage(String property) {
        this(property, NOT_SET);
    }

    /**
     * Creates a validation message for the given property.
     * 
     * @param property
     *            the property that is not valid (Component ID in JSP page.)
     * @param message
     *            the reason the property is not valid.
     */
    public ValidationMessage(String property, String message) {
        property_ = property;
        message_ = message;
    }

    /**
     * Creates a validation message for the given property.
     * 
     * @param property
     *            the property that is not valid (Component ID in JSP page.)
     * @param message
     *            the reason the property is not valid.
     */
    public ValidationMessage(String property, String message, Severity severity) {

        property_ = property;
        message_ = message;
        severity_ = severity;
    }

    /**
     * Creates a validation message for the given property.
     * 
     * @param property
     *            the property that is not valid (Component ID in JSP page.)
     * @param message
     *            the reason the property is not valid.
     */
    public ValidationMessage(String property, String message,
            Severity severity, String referenceID) {
        property_ = property;
        message_ = message;
        severity_ = severity;
        referenceID_ = referenceID;
    }
    
    /**
     * Creates a validation message for the given property.
     * 
     * @param property
     *            the property that is not valid (Component ID in JSP page.)
     * @param message
     *            the reason the property is not valid.
     */
    public ValidationMessage(String property, String message,
            Severity severity, String referenceID, String euId) {
        property_ = property;
        message_ = message;
        severity_ = severity;
        referenceID_ = referenceID;
        this.euId = euId;
    }
    
    /**
     * Creates a validation message for the given property.
     * 
     * @param property
     *            the property that is not valid (Component ID in JSP page.)
     * @param message
     *            the reason the property is not valid.
     */
    public ValidationMessage(String property, String message,
            Severity severity, String referenceID, Integer permitId) {
        property_ = property;
        message_ = message;
        severity_ = severity;
        referenceID_ = referenceID;
        this.permitId = permitId;
    }
    
    /**
     * Creates a validation message for the given property.
     * 
     * @param property
     *            the property that is not valid (Component ID in JSP page.)
     * @param message
     *            the reason the property is not valid.
     */
    public ValidationMessage(String property, String message,
            Severity severity, String referenceID, String euId, Integer permitId) {
        property_ = property;
        message_ = message;
        severity_ = severity;
        referenceID_ = referenceID;
        this.euId = euId;
        this.permitId = permitId;
    }

    /**
     * Returns the property name
     * 
     * @return the property name
     */
    public final String getProperty() {
        return property_;
    }

    /**
     * Returns the message
     * 
     * @return the message
     */
    public final String getMessage() {
        return message_;
    }
    
    public final void setMessage(String message){
    	message_ = message;
    }

    /**
     * Returns the severity
     * 
     * @return the severity
     */
    public final Severity getSeverity() {
        return severity_;
    }

    /**
     * Sets the message severity.
     */
    public final void setSeverity(Severity severity) {
        severity_ = severity;
    }
    
    /**
     * Returns the referenceID
     * 
     * @return the referenceID
     */
    public final String getReferenceID() {
        return referenceID_;
    }

    public final void setReferenceID(String referenceID) {
        referenceID_ = referenceID;
    }

    @Override
    public final String toString() {
        return property_ + ": " + message_;
    }

    /**
     * @return the euId
     */
    public final String getEuId() {
        return euId;
    }

    /**
     * @param euId the euId to set
     */
    public final void setEuId(String euId) {
        this.euId = euId;
    }

	public Integer getPermitId() {
		return permitId;
	}

	public void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}
}
