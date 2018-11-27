package us.oh.state.epa.stars2.workflow.types;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.4 $
 * @author Sam Wooster
 */
public class ANIType extends AbstractType {
    private static final String STRING_ERROR = " must be of the form XXX-XXX-XXXX where X is a digit.";

    public final void init(DataDetail initialData) {

    }

    protected final void typeInit() {
        // nothing to do
    }

    /**
     * @see AbstractType#toString()
     */
    public final String toString() {
        return formatNumber(stringValue);
    }

    /**
     * @see AbstractType#fromString(String value)
     */
    public final void fromString(String value) {
        stringValue = value;

        if (value == null || value.length() == 0) {
            stringValue = "";
            objectValue = stringValue;
            valid(true);
            return;
        } else if (value.length() != 10 && value.length() != 12
                && value.length() != 13) {
            setErrorMessage(STRING_ERROR);
            valid(false);
            return;
        }

        String tempValue = new String(ANIType.unFormatNumber(value));

        try {
            Long.parseLong(tempValue);

        } catch (NumberFormatException NFE) {
            setErrorMessage(" Should only have digit.");
            valid(false);
            return;
        }

        if ((value.charAt(3) != '-' || value.charAt(7) != '-')
                && (value.charAt(0) != '(' || value.charAt(4) != ')' || value
                        .charAt(8) != '-')
                && (value.charAt(3) != '.' || value.charAt(7) != '.')
                && (tempValue.length() != 10)) {
            setErrorMessage(STRING_ERROR);
            valid(false);
            return;
        } else if (tempValue.charAt(0) == '0') {
            setErrorMessage(" should not have the Area Code starting with a '0'. ");
            valid(false);
            return;
        } else if (tempValue.charAt(3) == '0') {
            setErrorMessage(" should not have a prefix as '0'. ");
            valid(false);
            return;
        } else {
            stringValue = value;
            objectValue = formatNumber(stringValue);
            valid(true);
            return;
        }

    }

    /**
     * @see AbstractType#value()
     */
    public final Object value() {
        return formatNumber((String) objectValue);
    }

    /**
     * @see AbstractType#value(Object object)
     */
    public final void value(Object object) {
        if (object == null || object.toString().length() == 0) {
            stringValue = "";
            objectValue = stringValue;
            valid(true);
            return;
        } else if (object instanceof String) {
            String value = (String) object;
            if (value.length() != 10 && value.length() != 12
                    && value.length() != 13) {
                setErrorMessage(STRING_ERROR);
                valid(false);
                return;
            }
            stringValue = formatNumber(value);
            objectValue = stringValue;
            valid(true);
            return;
        }
    }

    /**
     * @see AbstractType#validate()
     */
    public final boolean validate() {
        return valid();
    }

    public static String unFormatNumber(String number) {
        StringBuffer ret = new StringBuffer("");
        if (number != null) {
            char[] chars = number.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if ((chars[i] != '(') && (chars[i] != ')') && (chars[i] != '.')
                        && (chars[i] != ' ') && (chars[i] != '-')) {
                    ret.append(chars[i]);
                }
            }

            if ((ret.length() == 11) && (ret.charAt(0) == '1')) {
                ret = new StringBuffer(ret.substring(1));
            }
        }

        return ret.toString();
    }

    /**
     * Formats the long into ###-###-#### format.
     */
    public static String formatNumber(long number) {
        StringBuffer sb = new StringBuffer();
        String s = String.valueOf(number);
        if (s.length() == 10) {
            sb.append(s.substring(0, 3));
            sb.append("-");
            sb.append(s.substring(3, 6));
            sb.append("-");
            sb.append(s.substring(6));
            s = sb.toString();
        }
        return s;
    }

    /**
     * Formats the String into ###-###-#### format.
     */
    public static String formatNumber(String number) {
        StringBuffer sb = new StringBuffer("");

        if (number != null && !number.equals("")) {
            String s = unFormatNumber(number);

            // Currently this will only handle domestic 7 and 10 digit
            // numbers...
            if (s.length() == 7) {
                sb.append(s.substring(0, 3));
                sb.append("-");
                sb.append(s.substring(3));
            } else if (s.length() == 10) {
                sb.append(s.substring(0, 3));
                sb.append("-");
                sb.append(s.substring(3, 6));
                sb.append("-");
                sb.append(s.substring(6));
            } else {
                sb.append(number);
            }
        }

        return sb.toString();
    }

    /**
     * Returns the name of the type of object used to store data.
     * 
     * @return String object class name.
     */
    public final String getObjectType() {
        return String.class.getName();
    }
}
