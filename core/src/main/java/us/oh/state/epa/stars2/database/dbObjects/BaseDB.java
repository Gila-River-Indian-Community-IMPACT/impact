package us.oh.state.epa.stars2.database.dbObjects;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;

/**
 * BaseDB.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.0
 * @author Ken Bradley
 */
public abstract class BaseDB implements BaseDBObject, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	protected transient Logger logger;

	public final static String FLD_AUD_LOG_NO_VALUE = "NO VALUE";
	private final static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	protected HashMap<String, ValidationMessage> validationMessages = new HashMap<String, ValidationMessage>(1);
	protected boolean newObject;
	private HashMap<String, FieldAuditLog> fieldAuditLogs = new HashMap<String, FieldAuditLog>(1);
	private HashMap<String, FieldAuditLog> fieldEventLogs = new HashMap<String, FieldAuditLog>(1);

	private boolean dirty;
	private Integer lastModified;
	private boolean selected;
	private Integer numberOfResults;
	private boolean unlimitedResults;

	public BaseDB() {
		newObject = true;
		unlimitedResults = false;
		logger = Logger.getLogger(this.getClass());
	}

	public BaseDB(BaseDB old) {
		if (old != null) {
			setLastModified(old.getLastModified());
			setValidationMessages(old.getValidationMessages());
			setDirty(old.isDirty());
			setUnlimitedResults(old.isUnlimitedResults());

			for (FieldAuditLog log : old.getFieldAuditLogs()) {
				addFieldAuditLog(log);
			}
			for (FieldAuditLog log : old.getFieldEventLogs()) {
				addFieldEventLog(log);
			}
		}
		logger = Logger.getLogger(this.getClass());
	}

	public final FieldAuditLog[] getFieldAuditLogs() {
		return fieldAuditLogs.values().toArray(new FieldAuditLog[0]);
	}

	public final HashMap<String, ValidationMessage> getValidationMessages() {
		return validationMessages;
	}

	private void addFieldAuditLog(FieldAuditLog newLog) {
		this.fieldAuditLogs.put(newLog.getAttributeCd(), newLog);
	}

	private void setValidationMessages(HashMap<String, ValidationMessage> validationMessages) {
		this.validationMessages = validationMessages;
	}

	/**
	 * @param lastModified
	 */
	public final void setLastModified(Integer lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return
	 */
	public final Integer getLastModified() {
		return lastModified;
	}

	/**
	 * Returns a message array that contains each property for this DB object.
	 * <p>
	 * When overriding this method care should be taken to ensure that an array
	 * is always returned, and that all messages that would be generated based
	 * on the current state are returned, not just the first problem that is
	 * encountered.
	 * 
	 * @return messages for invalid properties.
	 */
	public ValidationMessage[] validate() {
		return new ArrayList<ValidationMessage>(validationMessages.values()).toArray(new ValidationMessage[0]);
	}

	/**
	 * Returns true if the internal state of this object is valid, for example
	 * if all required fields have been set to valid values.
	 * 
	 * @return true if the object is valid.
	 */
	public final boolean isValid() {
		return validate().length == 0;
	}

	public final boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	protected boolean isEquals(Object obj1, Object obj2) {
		if (obj1 != null) {
			if (obj1.equals(obj2)) {
				return true;
			}

			return false;
		} else if (obj2 != null) {
			return false;
		}

		return true;
	}

	// NOTE: If this class is ever modified, be sure it update both equals() and
	// hashCode()
	// method to be reflect the same type of changes.
	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (!getClass().equals(obj.getClass()))) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final BaseDB other = (BaseDB) obj;

		if (isDirty() != other.isDirty()) {
			return false;
		}

		// Either both null or equal values.
		if (((getLastModified() == null) && (other.getLastModified() != null))
				|| ((getLastModified() != null) && (other.getLastModified() == null)) || ((getLastModified() != null)
						&& (other.getLastModified() != null) && !(getLastModified().equals(other.getLastModified())))) {
			return false;
		}

		return true;

	}

	// override the standard hashcode algorithm
	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = 7;

		result = PRIME * result + (isDirty() ? 1 : 0);
		result = PRIME * result + ((getLastModified() == null) ? 0 : getLastModified().hashCode());

		return result;

	}

	public final void clearValidationMessages() {
		validationMessages.clear();
	}

	public final boolean isDirty() {
		return dirty;
	}

	public final void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public final void checkDirty(String attributeCd, String uniqueId, Object originalValue, Object newValue) {
		fieldChangeLog(attributeCd, uniqueId, originalValue, newValue, fieldAuditLogs, false);
		if (fieldAuditLogs.size() > 0) {
			this.setDirty(true);
		} else {
			this.setDirty(false);
		}
	}

	private final void fieldChangeLog(String attributeCd, String uniqueId, Object originalValue, Object newValue,
			HashMap<String, FieldAuditLog> fieldChangeLogs, boolean notNullOriginalValue) {
		if (newObject) {
			return;
		}

		String originalVal = "NO VALUE";
		if (originalValue != null && (!originalValue.toString().trim().equals(""))) {
			if (originalValue instanceof Timestamp) {
				originalVal = dateFormat.format(originalValue).toString();
			} else {
				originalVal = originalValue.toString().trim();
			}
		} else {
			if (notNullOriginalValue) {
				return;
			}
		}

		String newVal = "NO VALUE";
		if (newValue != null && (!newValue.toString().trim().equals(""))) {
			if (newValue instanceof Timestamp) {
				newVal = dateFormat.format(newValue).toString();
			} else {
				newVal = newValue.toString().trim();
			}
		}

		if (!originalVal.equals(newVal)) {
			// Check if this is the first time we have seen a request for a
			// change
			// of this property
			if (!fieldChangeLogs.containsKey(attributeCd)) {
				FieldAuditLog newLog = new FieldAuditLog(attributeCd, uniqueId, originalVal, newVal);
				fieldChangeLogs.put(attributeCd, newLog);
			} else {
				// Since this value has been changed once, check if the new
				// value
				// is the same as the original. If so remove it from the HashMap
				// since there really is no change.
				FieldAuditLog currentLog = fieldChangeLogs.get(attributeCd);
				if (currentLog.getOriginalValue().equals(newVal)) {
					fieldChangeLogs.remove(attributeCd);
				} else {
					currentLog.setNewValue(newVal);
					currentLog.setUniqueId(uniqueId);
				}
			}
		}
	}

	public final void fieldChangeEventLog(String attribute, String uniqueId, Object originalValue, Object newValue) {
		fieldChangeLog(attribute, uniqueId, originalValue, newValue, fieldEventLogs, false);
	}

	public final void fieldChangeEventLog(String attribute, String uniqueId, Object originalValue, Object newValue,
			boolean notNullOriginalValue) {
		fieldChangeLog(attribute, uniqueId, originalValue, newValue, fieldEventLogs, notNullOriginalValue);
	}

	/**
	 * @param value
	 * @param fieldName
	 * 
	 */
	protected final void requiredField(Object value, String fieldName) {

		if (value == null || (value instanceof String && ((String) value).trim().length() == 0)) {
			validationMessages.put(fieldName, new ValidationMessage(fieldName,
					"Attribute: " + fieldName + " is not set.", ValidationMessage.Severity.ERROR, null));
		} else {
			validationMessages.remove(fieldName);
		}
	}

	/**
	 * @param value
	 * @param fieldName
	 * @param fieldLabel
	 * 
	 */
	protected final boolean requiredField(Object value, String fieldName, String fieldLabel) {
		if (value == null || (value instanceof String && ((String) value).trim().length() == 0)) {
			validationMessages.put(fieldName, new ValidationMessage(fieldName,
					"Attribute " + fieldLabel + " is not set.", ValidationMessage.Severity.ERROR, null));
			return false;
		} else {
			validationMessages.remove(fieldName);
			return true;
		}
	}

	public final void requiredField(Object value, String fieldName, String fieldLabel, String referenceId,
			List<ValidationMessage> valMessages) {

		if (value == null || (value instanceof String && ((String) value).trim().length() == 0)) {
			valMessages.add(new ValidationMessage(fieldName, "Attribute " + fieldLabel + " is not set.",
					ValidationMessage.Severity.ERROR, referenceId));
		}
	}

	/**
	 * @param value
	 * @param fieldName
	 * @param fieldLabel
	 * @param referenceId
	 * 
	 */
	protected final boolean requiredField(Object value, String fieldName, String fieldLabel, String referenceId) {

		if (value == null || (value instanceof String && ((String) value).trim().length() == 0)) {
			validationMessages.put(fieldName, new ValidationMessage(fieldName,
					"Attribute " + fieldLabel + " is not set.", ValidationMessage.Severity.ERROR, referenceId));
			return false;
		} else {
			validationMessages.remove(fieldName);
			return true;
		}
	}

	/**
	 * @param value
	 * @param fieldName
	 * @param fieldLabel
	 * @param referenceId
	 * 
	 */
	protected final void requiredField(Object value, String fieldName, String fieldLabel, String referenceId,
			String name) {

		if (value == null || (value instanceof String && ((String) value).trim().length() == 0)) {
			validationMessages.put(fieldName, new ValidationMessage(fieldName,
					"Attribute " + fieldLabel + " is not set.", ValidationMessage.Severity.ERROR, referenceId, name));
		} else {
			validationMessages.remove(fieldName);
		}
	}

	protected final void checkRangeValues(Object value, Object minValue, Object maxValue, String fieldName,
			String fieldLabel) {
		checkRangeValues(value, minValue, maxValue, fieldName, fieldLabel, null);
	}

	protected final void checkRangeValues(Object value, Object minValue, Object maxValue, String fieldName,
			String fieldLabel, String referenceId) {

		if (value instanceof String && ((String) value).length() > 0) {
			try {
				if (minValue instanceof Integer || maxValue instanceof Integer) {
					// convert string to integer
					value = Integer.valueOf((String) value);
				} else if (minValue instanceof Float || maxValue instanceof Float) {
					// convert string to Float
					value = Float.valueOf((String) value);
				} else if (minValue instanceof Double || maxValue instanceof Double) {
					// convert string to Double
					value = Double.valueOf((String) value);
				}
			} catch (NumberFormatException nfe) {
				validationMessages.put(fieldName + "min",
						new ValidationMessage(fieldName,
								"The value of the attribute : " + fieldLabel + " is not a number",
								ValidationMessage.Severity.ERROR, referenceId));
			}
		}

		if (value instanceof Integer) {
			if (minValue != null) {
				if ((Integer) value < (Integer) minValue) {
					validationMessages.put(fieldName + "min",
							new ValidationMessage(fieldName,
									"The minimum value for the attribute : " + fieldLabel + " is " + minValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "min");
				}
			}
			if (maxValue != null) {
				if ((Integer) value > (Integer) maxValue) {
					validationMessages.put(fieldName + "max",
							new ValidationMessage(fieldName,
									"The maximum value for the attribute : " + fieldLabel + " is " + maxValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "max");
				}
			}

		} else if (value instanceof Float) {
			if (minValue != null) {
				if ((Float) value < (Float) minValue) {
					validationMessages.put(fieldName + "min",
							new ValidationMessage(fieldName,
									"The minimum value for the attribute : " + fieldLabel + " is " + minValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "min");
				}
			}
			if (maxValue != null) {
				if ((Float) value > (Float) maxValue) {
					validationMessages.put(fieldName + "max",
							new ValidationMessage(fieldName,
									"The maximum value for the attribute : " + fieldLabel + " is " + maxValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "max");
				}
			}

		} else if (value instanceof BigDecimal) {
			
			if (minValue != null) {
				int lessThan = ((BigDecimal) value).compareTo((BigDecimal) minValue);
				if (lessThan == -1) {
					validationMessages.put(fieldName + "min",
							new ValidationMessage(fieldName,
									"The minimum value for the attribute : " + fieldLabel + " is "
											+ ((BigDecimal) minValue).toPlainString(),
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "min");
				}
			}

			if (maxValue != null) {
				int moreThan = ((BigDecimal) value).compareTo((BigDecimal) maxValue);
				if (moreThan == 1) {
					validationMessages.put(fieldName + "max",
							new ValidationMessage(fieldName,
									"The maximum value for the attribute : " + fieldLabel + " is "
											+ ((BigDecimal) maxValue).toPlainString(),
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "max");
				}
			}

		} else if (value instanceof Double) {
			if (minValue != null) {
				if ((Double) value < (Double) minValue) {
					validationMessages.put(fieldName + "min",
							new ValidationMessage(fieldName,
									"The minimum value for the attribute : " + fieldLabel + " is " + minValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "min");
				}
			}
			if (maxValue != null) {
				if ((Double) value > (Double) maxValue) {
					validationMessages.put(fieldName + "max",
							new ValidationMessage(fieldName,
									"The maximum value for the attribute : " + fieldLabel + " is " + maxValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "max");
				}
			}

		} else if (value instanceof Long) {
			if (minValue != null) {
				if ((Long) value < (Long) minValue) {
					validationMessages.put(fieldName + "min",
							new ValidationMessage(fieldName,
									"The minimum value for the attribute : " + fieldLabel + " is " + minValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "min");
				}
			}
			if (maxValue != null) {
				if ((Long) value > (Long) maxValue) {
					validationMessages.put(fieldName + "max",
							new ValidationMessage(fieldName,
									"The maximum value for the attribute : " + fieldLabel + " is " + maxValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "max");
				}
			}

		} else if (value instanceof Short) {
			if (minValue != null) {
				if ((Short) value < (Short) minValue) {
					validationMessages.put(fieldName + "min",
							new ValidationMessage(fieldName,
									"The minimum value for the attribute : " + fieldLabel + " is " + minValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "min");
				}
			}
			if (maxValue != null) {
				if ((Short) value > (Short) maxValue) {
					validationMessages.put(fieldName + "max",
							new ValidationMessage(fieldName,
									"The maximum value for the attribute : " + fieldLabel + " is " + maxValue,
									ValidationMessage.Severity.ERROR, referenceId));
				} else {
					validationMessages.remove(fieldName + "max");
				}
			}
		} else if (null == value) {
			validationMessages.remove(fieldName + "min");
			validationMessages.remove(fieldName + "max");
		}
	}

	public static final boolean isNumeric(String number) {

		try {
			Integer.valueOf(number);
			if (!Character.isDigit(number.charAt(0))) {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public final boolean isNewObject() {
		return newObject;
	}

	// Remove all messages whose tag begins with specified string
	protected final void removeAll(String fieldNamePrefix) {
		for (String t : validationMessages.keySet()) {
			if (t.startsWith(fieldNamePrefix))
				validationMessages.remove(t);
		}
	}

	public byte[] toXMLStream() {

		logger.debug("Write XML data");
		XMLEncoder e = null;
		ByteArrayOutputStream ret = null;

		ret = new ByteArrayOutputStream();
		e = new XMLEncoder(ret);

		e.setPersistenceDelegate(BigDecimal.class, new DefaultPersistenceDelegate() {
			protected Expression instantiate(Object oldInstance, Encoder out) {
				BigDecimal bd = (BigDecimal) oldInstance;
				return new Expression(oldInstance, oldInstance.getClass(), "new", new Object[] { bd.toString() });
			}

			protected boolean mutatesTo(Object oldInstance, Object newInstance) {
				return oldInstance.equals(newInstance);
			}
		});

		if (e != null) {
			e.writeObject(this);
			e.close();
		}

		return ret.toByteArray();
	}

	public static final Object fromXMLStream(InputStream inStr) {

		XMLDecoder e = null;

		e = new XMLDecoder(inStr);

		Object ret = e.readObject();
		e.close();

		return ret;
	}

	public void setNewObject(boolean newObject) {
		this.newObject = newObject;
	}

	public FieldAuditLog[] getFieldEventLogs() {
		return fieldEventLogs.values().toArray(new FieldAuditLog[0]);
	}

	public void setFieldEventLogs(HashMap<String, FieldAuditLog> fieldEventLogs) {
		this.fieldEventLogs = fieldEventLogs;
	}

	private void addFieldEventLog(FieldAuditLog newLog) {
		this.fieldEventLogs.put(newLog.getAttributeCd(), newLog);
	}

	public final void setFieldAuditLogs(HashMap<String, FieldAuditLog> fieldAuditLogs) {
		this.fieldAuditLogs = fieldAuditLogs;
	}

	public final Integer getNumberOfResults() {
		return numberOfResults;
	}

	public final void setNumberOfResults(Integer numberOfResults) {
		this.numberOfResults = numberOfResults;
	}

	public boolean isUnlimitedResults() {
		return unlimitedResults;
	}

	public void setUnlimitedResults(boolean unlimitedResults) {
		this.unlimitedResults = unlimitedResults;
	}

	public void populate(ResultSet rs, int option) throws SQLException {

	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		in.defaultReadObject();
		logger = Logger.getLogger(this.getClass());
	}

	public static String numberToString(Double d) {

		String s = null;
		if (d != null) {
			String format;
			if (d == 0d)
				format = "#0";
			else if (d >= .001d) {
				format = "#.#####E00";
				if (d < 1000000d)
					format = "###,##0.";
				if (d < 100000d)
					format = "##,##0.#";
				if (d < 10000d)
					format = "#,##0.##";
				if (d < 1000d)
					format = "##0.###";
				if (d < 100d)
					format = "#0.####";
				if (d < 10d)
					format = "#.#####";
				if (d < 1d)
					format = "0.######";
				if (d < 0.1d)
					format = "0.#######";
				if (d < 0.01d)
					format = "0.########";
			} else
				format = "#.#####E00";

			DecimalFormat decFormat = new DecimalFormat(format);
			s = decFormat.format(d);
		}
		return s;
	}

	public static String numberToxxx_xx(Double d) {

		String s = null;
		if (d != null) {
			String format = "##0.##";
			DecimalFormat decFormat = new DecimalFormat(format);
			s = decFormat.format(d);
		}
		return s;
	}
	
}
