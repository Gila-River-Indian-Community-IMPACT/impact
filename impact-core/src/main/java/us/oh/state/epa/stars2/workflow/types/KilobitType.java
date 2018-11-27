package us.oh.state.epa.stars2.workflow.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class KilobitType extends AbstractType {
    public static final String GIGABITS = "Gb";
    public static final String MEGABITS = "Mb";
    public static final String KILOBITS = "kb";
    public static final int GB_FACTOR = 1024 * 1024;
    public static final int MB_FACTOR = 1024;
    public static final int KB_FACTOR = 1;
    public static final int ONE_K = 1024;
    private static final String STRING_ERROR = " must be of the form N Gb or N Mb or N kb where N is a decimal number";

    public final void init(DataDetail dd) {
        super.init(dd);

        valid(true);
    }

    public final void fromString(String value) {
        stringValue = value;

        if (value == null || value.trim().length() == 0) {
            super.fromString(value);
        } else {
            StringBuffer sbValue = new StringBuffer(value.trim());
            objectValue = parseString(sbValue);
            if (objectValue != null) {
                stringValue = sbValue.toString();
                valid(true);
            } else {
                invalid();
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
            // assume Integer passed in is in kilobits and store that value
            objectValue = object;

            // create a string representation that displays the appropriate
            // suffix (kB, MB, GB) representing this object
            double dblVal = ((Integer) objectValue).doubleValue();
            String suffix = KilobitType.KILOBITS;

            // convert to megabits if > 1 K bits
            if (dblVal > KilobitType.ONE_K) {
                dblVal = dblVal / KilobitType.ONE_K;
                suffix = KilobitType.MEGABITS;

                // convert to gigabits if > 1 K Megabits
                if (dblVal > KilobitType.ONE_K) {
                    dblVal = dblVal / KilobitType.ONE_K;
                    suffix = KilobitType.GIGABITS;
                }
            }

            stringValue = String.valueOf(dblVal) + " " + suffix;

        } else {
            generateCannotCastMessage(object);
        }
    }

    private void invalid() {
        setErrorMessage(STRING_ERROR);
        valid(false);
    }

    /*
     * parseString parses the value argument and returns the equivalent value in
     * kilobits as a Integer. Returns null if value is not a valid represention
     * of this type
     */
    public static Integer parseString(StringBuffer value) {
        Integer ret = null;
        Pattern pattern = null;
        Matcher matcher = null;
        boolean valid = false;
        int factor = 0;
        String suffix = "";

        // gigabits
        pattern = Pattern.compile("(\\d+\\.?\\d*)\\s*[G|g][B|b]*");
        matcher = pattern.matcher(value);
        if ((valid = matcher.matches()) == true) {
            factor = GB_FACTOR;
            suffix = GIGABITS;
        }

        // megabits
        if (!valid) {
            pattern = Pattern.compile("(\\d+\\.?\\d*)\\s*[M|m][B|b]*");
            matcher = pattern.matcher(value);
            if ((valid = matcher.matches()) == true) {
                factor = MB_FACTOR;
                suffix = MEGABITS;
            }
        }

        // kilobits
        if (!valid) {
            pattern = Pattern.compile("(\\d+\\.?\\d*)\\s*[K|k][B|b]*");
            matcher = pattern.matcher(value);
            if ((valid = matcher.matches()) == true) {
                factor = KB_FACTOR;
                suffix = KILOBITS;
            }
        }

        if (valid) {
            try {
                // convert String representation to number of kilobits
                Double kbValue = new Double(Double
                        .parseDouble(matcher.group(1))
                        * factor);
                // number of kilobits should be expressed as an Integer
                ret = new Integer(kbValue.intValue());
                value.replace(0, value.length(), matcher.group(1) + " "
                        + suffix);
            } catch (NumberFormatException nfe) {
                ret = null;
            }
        }
        return ret;
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
