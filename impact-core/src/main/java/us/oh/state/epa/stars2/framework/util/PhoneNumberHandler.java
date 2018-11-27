package us.oh.state.epa.stars2.framework.util;

/**
 * Static utility methods.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.4 $
 * @author $Author: kbradley $
 */
public class PhoneNumberHandler {
    /**
     * Formats phone number into preferred North American format.
     * 
     * @param number -
     *            Number to format.
     * 
     * @return Formatted number.
     */
    public static final String formatNumber(String number) {
        StringBuffer sb = new StringBuffer("");

        if (number != null && !number.equals("")) {
            String s = unFormatNumber(number);

            // Currently this will only handle domestic 7 and 10 digit
            // numbers...
            if (number.length() == 7) {
                sb.append(s.substring(0, 3));
                sb.append("-");
                sb.append(s.substring(3));
            } else if (number.length() == 10) {
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
     * Formats phone number into preferred North American format.
     * 
     * @param number -
     *            Number to format.
     * 
     * @return Formatted number.
     */
    public static final String formatNumber(long number) {
        StringBuffer sb = new StringBuffer();
        String s = String.valueOf(number);
        sb.append(s.substring(0, 3));
        sb.append("-");
        sb.append(s.substring(3, 6));
        sb.append("-");
        sb.append(s.substring(6));
        return sb.toString();
    }

    /**
     * Strips North American formatting from the given phone number.
     * 
     * @param number -
     *            Number to unformat.
     * 
     * @return Unformatted number.
     */
    public static final String unFormatNumber(String number) {

        if ((number == null) || (number.length() == 0)) {
            return null;
        }

        char[] chars = number.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            if ((chars[i] != '(') && (chars[i] != ')') && (chars[i] != '.')
                    && (chars[i] != ' ') && (chars[i] != '-')) {
                sb.append(chars[i]);
            }
        }

        if ((sb.length() == 11) && (sb.charAt(0) == '1')) {
            sb = new StringBuffer(sb.substring(1));
        }

        /*
         * StringTokenizer st = new StringTokenizer(number, "-");
         * while(st.hasMoreTokens()) { sb.append(st.nextToken()); }
         */
        return sb.toString();
    }

    /**
     * Validates phone for North American formatting.
     * 
     * @param number -
     *            Number to validate.
     * @param errorString - .
     * 
     * @return True number is formatted correctly. False number is not formatted
     *         correctly.
     */
    public static final boolean validNumber(String number,
            StringBuffer errorString) {
        boolean ret = false;

        if (!number.equals("")) {
            String tempNumber = PhoneNumberHandler.unFormatNumber(number);

            // If the number doesn't have 10 digits or the first number of the
            // NPA
            // is '0'
            // or if the first number of the NXX is '0', then it is not a valid
            // number.
            // if ((tempNumber.length() != 10) ||
            // (tempNumber.charAt(0) == '0') ||
            // (tempNumber.charAt(3) == '0')) {
            if (tempNumber.length() != 10) {
                errorString.append("must be 10 digits. ");
            } else if (tempNumber.charAt(0) == '0') {
                errorString
                        .append("should not have the Area Code starting with a '0'. ");
            } else if (tempNumber.length() > 3) {
                if (tempNumber.charAt(3) == '0') {
                    errorString.append("should not have a prefix as '0'. ");
                } else {
                    ret = true;
                }
            } else {
                ret = true;
            }
        } else {
            errorString.append("must not be null. ");
        }

        return ret;
    }
}
