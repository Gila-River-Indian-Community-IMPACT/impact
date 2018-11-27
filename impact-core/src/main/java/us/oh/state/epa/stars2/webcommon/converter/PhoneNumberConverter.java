/**
 * 
 */
package us.oh.state.epa.stars2.webcommon.converter;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.apache.log4j.Logger;

/**
 * @author Pyeh
 * 
 */
public class PhoneNumberConverter implements javax.faces.convert.Converter, java.io.Serializable {
    public static final String CONVERTER_ID = "us.oh.state.epa.stars2.webcommon.converter.PhoneNumberConverter";
    private static final String DEFAULT_FORMAT = "(###)###-####";
    private static final String[] LEGAL_SYMBOL = { "-", ".", " ", "(", ")" };
    private transient Logger logger;
    private String format;

    public PhoneNumberConverter() {
        logger = Logger.getLogger(this.getClass());  
    }
    
    /**
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.String)
     */
    public final Object getAsObject(FacesContext context, UIComponent component,
            String value) throws ConverterException {
        if (context == null) {
            throw new NullPointerException("FacesContext");
        }
        if (component == null) {
            throw new NullPointerException("UIComponent");
        }

        if (value != null) {
            value = value.trim();
            if (value.length() > 0) {
                String numberValue = value;
                for (String s : LEGAL_SYMBOL)
                    numberValue = numberValue.replace(s, "");

                if (!isNumber(numberValue)) {
                    throw new ConverterException(
                            "number field contains illegal characters.");
                } else if (numberValue.length() != 10) {
                    throw new ConverterException(
                            "number field must include the area code and contain a total of ten digits.");
                } else {
                    return numberValue;
                }
            }
        }
        return null;
    }

    /**
     * @param value
     * @return
     * 
     */
    private boolean isNumber(String value) {
        for (Character c : value.toCharArray())
            if (!Character.isDigit(c))
                return false;
        return true;
    }

    /**
     * @param value
     * 
     */
    public String formatPhoneNumber(String value) throws ConverterException {
        if (value.length() == 10) {
            return formatLength10PhoneNumber(value);
        } else if (value.length() == 11) {
            StringBuffer sb = new StringBuffer();
            sb.append(value.charAt(0));
            sb.append(formatLength10PhoneNumber(value.substring(1)));
            return sb.toString();
        } else {
            throw new ConverterException("Wrong length of number. " + value);
        }
    }
    
    public String tryFormatPhoneNumber(String value) {
        String result = value;
        try {
            if(value != null) {
                result = formatPhoneNumber(value);
            }
        } catch(Exception e) {
            // could not convert
        }
        return result;
    }

    /**
     * @param value
     * 
     */
    private String formatLength10PhoneNumber(String value) {
        String newValue = DEFAULT_FORMAT;
        if (format != null && format.length() != 0)
            newValue = format;

        for (char c : value.toCharArray())
            newValue = newValue.replaceFirst("#", c + "");

        return newValue;
    }

    /**
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
     *      javax.faces.component.UIComponent, java.lang.Object)
     */
    public final String getAsString(FacesContext context, UIComponent component,
            Object value) throws ConverterException {
        if (context == null) {
            throw new NullPointerException("FacesContext");
        }
        if (component == null) {
            throw new NullPointerException("UIComponent");
        }

        if (value == null) {
            return "";
        }

        String sValue;
        if (value instanceof String) {
            sValue = (String) value;
        } else {
            try {
                sValue = ((Character) value).toString();
            } catch (Exception e) {
                throw new ConverterException(e);
            }
        }

        if (sValue.length() <= 0) {
            return "";
        }

        try {
            return formatPhoneNumber(sValue);
        } catch (ConverterException e) {
            logger.warn(e);
            return sValue;
        }
    }

    public final String getFormat() {
        return format;
    }

    /**
     * The format of output string. Use "#" to represent digit. For example:
     * "(###)###-####" for "(123)123-1234"
     * 
     * @param format
     * 
     */
    public final void setFormat(String format) {
        this.format = format;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}
