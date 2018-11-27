package us.oh.state.epa.stars2.framework.util;

import org.apache.commons.validator.EmailValidator;

/**
 * This is a utility class is used to validate email addresses.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version 1.0
 * @author Tom Dixon
 */
public class ValidateEmailAddress {
	public static boolean isValidEmail(String email) {
		boolean ret = EmailValidator.getInstance().isValid(email);
		return ret;
	}
	
//    public static final boolean isValidEmail(String email) {
//        int n = 0;
//        int ii;
//        int len = 0;
//        int len2 = 0;
//        int digitCount = 0;
//        String token;
//        String token2 = "";
//        boolean dotFound = false;
//        boolean quoteFound = false;
//        StringTokenizer st;
//        StringTokenizer parser = new StringTokenizer(email, "@", false);
//
//        while (parser.hasMoreTokens()) {
//            n++;
//            token = parser.nextToken();
//            switch (n) {
//            case 1: // user portion
//                if (token.startsWith(".") || token.endsWith(".")) {
//                    return false;
//                }
//                st = new StringTokenizer(token, ".");
//                len2 = st.countTokens();
//
//                for (ii = 0; ii < len2; ii++) {
//                    token2 = st.nextToken();
//                    if (token2.startsWith("_") || token2.endsWith("_")) {
//                        return false;
//                    }
//                }
//                if ((token.startsWith("\"")) && (token.endsWith("\""))) {
//                    quoteFound = true;
//                } else {
//                    if ((token.startsWith("\"")) && (!token.endsWith("\""))) {
//                        return false;
//                    }
//                    if ((!token.startsWith("\"")) && (token.endsWith("\""))) {
//                        return false;
//                    }
//                }
//                st = new StringTokenizer(token, ".");
//                // may have only 2 '.' in the user portion, 0 if quotes are used
//                if ((st.countTokens() > 3)
//                        || ((quoteFound) && (st.countTokens() > 1))) {
//                    return false;
//                }
//                if (!quoteFound) {
//                    // check that the name does not include embedded tokens
//                    st = new StringTokenizer(token, "\"");
//                    if (st.countTokens() > 1) {
//                        return false;
//                    }
//                }
//                len = token.length();
//                for (ii = 0; ii < len; ii++) {
//                    // return false for any white space
//                    if (Character.isWhitespace(token.charAt(ii))) {
//                        return false;
//                    }
//                    // check if the character is alfa-numeric, '.' or '_'
//                    if (!Character.isLetterOrDigit(token.charAt(ii))
//                            && ('.' != token.charAt(ii))
//                            && ('_' != token.charAt(ii))
//                            && ('"' != token.charAt(ii))
//                            && ('-' != token.charAt(ii))) {
//                        return false;
//                    }
//                }
//                break;
//            case 2:
//                if (token.startsWith(".") || token.endsWith(".")) {
//                    return false;
//                }
//                st = new StringTokenizer(token, ".");
//                if (st.countTokens() < 2) {
//                    return false;
//                }
//                len2 = st.countTokens();
//                for (ii = 0; ii < len2; ii++) {
//                    token2 = st.nextToken();
//                    if (token2.startsWith("_") || token2.endsWith("_")) {
//                        return false;
//                    }
//                }
//                len = token.length();
//                if (!token.startsWith("[")) {
//                    dotFound = false;
//                    // if (token.startsWith("_") || token.endsWith("_"))
//                    // return false;
//                    for (ii = 0; ii < len; ii++) {
//                        // return false for any white space
//                        if (Character.isWhitespace(token.charAt(ii))) {
//                            return false;
//                        }
//                        // check if there are consecutive '.'
//                        if ('.' == token.charAt(ii)) {
//                            if (dotFound) {
//                                return false;
//                            }
//
//                            dotFound = true;
//                        } else {
//                            dotFound = false;
//                        }
//                        // check if the character is alfa-numeric, '.' or '_'
//                        if (!Character.isLetterOrDigit(token.charAt(ii))
//                                && ('.' != token.charAt(ii))
//                                && ('_' != token.charAt(ii))
//                                && ('-' != token.charAt(ii))) {
//                            return false;
//                        }
//                    }
//                    // want to check the 'tld' portion of the address and ensure
//                    // that it is only alpha-numeric.
//                    ii = token.lastIndexOf('.');
//                    if (ii > 0) {
//                        token2 = token.substring(ii + 1, len);
//                        len2 = token2.length();
//                        for (ii = 0; ii < len2; ii++) {
//                            if (!Character.isLetterOrDigit(token2.charAt(ii))) {
//                                return false;
//                            }
//                        }
//                    }
//
//                } else {
//                    // ip address
//                    if (!token.endsWith("]")) {
//                        return false;
//                    }
//                    token2 = token.substring(1, token.length() - 1);
//                    len2 = token2.length();
//                    dotFound = false;
//                    for (ii = 0; ii < len2; ii++) {
//                        if (!Character.isDigit(token2.charAt(ii))) {
//                            // not a digit so check if it is a '.'
//                            // do not permit consecutive '.'
//                            if ('.' == token2.charAt(ii)) {
//                                if (dotFound) {
//                                    return false;
//                                }
//
//                                dotFound = true;
//                                digitCount = 0;
//                            } else {
//                                return false;
//                            }
//                        } else {
//                            digitCount++;
//                            if (digitCount > 3) {
//                                return false;
//                            }
//                            dotFound = false;
//                        }
//                    }
//                }
//
//                if (n == 2) {
//                    ii = token.lastIndexOf('.');
//
//                    if ((len - ii) < 3) {
//                        return false;
//                    }
//                }
//                break;
//
//            default:
//                return false;
//            }
//        }
//        return (n == 2);
//    }
}
