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
public class DoubleType extends AbstractNumber {
    protected double minValue = -1;
    protected double maxValue = -1;

    public final void init(DataDetail dd) {
        super.init(dd);

        if (min != null) {
            minValue = new Double(min).doubleValue();
        }

        if (max != null) {
            maxValue = new Double(max).doubleValue();
        }

        valid(true);
    }

    public final void fromString(String value) {
        stringValue = value;

        if (value == null || value.trim().length() == 0) {
            super.fromString(value);

            return;
        }

        try {
            Number number = formatter.parse(value);
            double dub = number.doubleValue();

            if (minValue > -1 && dub < minValue) {
                StringBuffer b = new StringBuffer(" must be at least ");
                b.append(minValue + ".");
                setErrorMessage(b.toString());
                valid(false);
            } else if (maxValue > -1 && dub > maxValue) {
                StringBuffer b = new StringBuffer(" can't be larger than ");
                b.append(maxValue + ".");
                setErrorMessage(b.toString());
                valid(false);
            } else {
                objectValue = new Double(dub);
                valid(true);
            }
        } catch (ParseException pe) {
            setErrorMessage(" is not a valid number.");
            valid(false);
        }
    }

    /**
     * The parameter must be a java.lang.Double. If it is not, the state of this
     * instance will not change, but it will become invalid. If this happens,
     * you can make the object valid by calling validate;
     */
    public final void value(Object object) {
        if (object == null) {
            super.value(object);

            return;

        } else if (object instanceof Number) {
            double number = ((Number) object).doubleValue();

            if (minValue > -1 && number < minValue) {
                StringBuffer b = new StringBuffer(" must be at least ");
                b.append(minValue + ".");
                setErrorMessage(b.toString());
                valid(false);
            } else if (maxValue > -1 && number > maxValue) {
                StringBuffer b = new StringBuffer(" can't be larger than ");
                b.append(maxValue + ".");
                setErrorMessage(b.toString());
                valid(false);
            } else {
                Double value = new Double(number);
                objectValue = value;
                stringValue = formatter.format(value);
                valid(true);
            }
            return;

        } else {
            generateCannotCastMessage(object);
        }
    }

    public final boolean validate() {
        return super.validate();
    }

    /**
     * Returns the name of the type of object used to store data.
     * 
     * @return String object class name.
     */
    public final String getObjectType() {
        return Double.class.getName();
    }

}
