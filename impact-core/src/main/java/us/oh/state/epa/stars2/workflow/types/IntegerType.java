package us.oh.state.epa.stars2.workflow.types;

import java.text.ParseException;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;

/**
 * The following keys are optional:
 * <ul>
 * <li>min-value</li>
 * <li>max-value</li>
 * <li>format-mask</li>
 * </ul>
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
public class IntegerType extends AbstractNumber {
    private Integer minValue;
    private Integer maxValue;

    public final void init(DataDetail dd) {
        super.init(dd);

        if (min != null) {
            minValue = new Integer(min);
        }

        if (max != null) {
            maxValue = new Integer(max);
        }

        valid(true);
    }

    public final void fromString(String value) {
        stringValue = value;

        if (value == null || value.trim().length() == 0) {
            super.fromString(value);
        } else {
            try {
                int number = formatter.parse(value).intValue();

                if (minValue != null && number < minValue.intValue()) {
                    StringBuffer b = new StringBuffer(" must be at least ");
                    b.append(minValue.toString() + ".");
                    setErrorMessage(b.toString());
                    valid(false);
                } else if (maxValue != null && number > maxValue.intValue()) {
                    StringBuffer b = new StringBuffer(" can be no larger than ");
                    b.append(maxValue.toString() + ".");
                    setErrorMessage(b.toString());
                    valid(false);
                } else {
                    objectValue = new Integer(number);
                    valid(true);
                }
            } catch (ParseException pe) {
                setErrorMessage(" is not a valid number.");
                valid(false);
            }
        }
    }

    /**
     * The parameter must be a java.lang.Integer. If it is not, the state of
     * this instance will not change, but it will become invalid. If this
     * happens, you can make the object valid by calling validate;
     */
    public final void value(Object object) {
        if (object == null) {
            super.value(object);
        } else if (object instanceof Integer) {
            int value = ((Number) object).intValue();

            if (minValue != null && value < minValue.intValue()) {
                StringBuffer b = new StringBuffer(" must be at least ");
                b.append(minValue.toString() + ".");
                setErrorMessage(b.toString());
                valid(false);
            } else if (maxValue != null && value > maxValue.intValue()) {
                StringBuffer b = new StringBuffer(" can be no larger than ");
                b.append(" can be no larger than ");
                b.append(maxValue.toString() + ".");
                setErrorMessage(b.toString());
                valid(false);
            } else {
                objectValue = new Integer(value);
                stringValue = formatter.format(value);
                valid(true);
            }
        } else {
            generateCannotCastMessage(object);
        }
    }

    public final boolean validate() {
        if (objectValue == null || stringValue == null) {
            super.validate();
        } else {
            try {
                int number = formatter.parse(stringValue).intValue();

                if (number == ((Integer) objectValue).intValue()) {
                    valid(true);
                } else {
                    valid(false);
                }
            } catch (ParseException pe) {
                valid(false);
            }
        }

        return valid();
    }

    /**
     * Returns the name of the type of object used to store data.
     * 
     * @return String object class name.
     */
    public final String getObjectType() {
        return Integer.class.getName();
    }

}
