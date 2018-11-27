/**
 * 
 */
package us.oh.state.epa.stars2.webcommon.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

/**
 * @author Pyeh
 * 
 */
public class SccCodeConverter implements javax.faces.convert.Converter, java.io.Serializable {
    public static final String CONVERTER_ID = "us.oh.state.epa.stars2.webcommon.converter.SCCCodeConverter";

    public SccCodeConverter() {
        super();
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

        if (value != null && (!value.equals(""))) {
            value = value.trim();
            if ((value.length() != 8) && (value.length() != 11)) {
                throw new ConverterException(
                        "Wrong SCC code format. Correct format is 12233344 or 1-22-333-44");
            }

            if (value.length() == 11) {
                if (value.charAt(1) != '-' || value.charAt(4) != '-'
                        || value.charAt(8) != '-') {
                    throw new ConverterException(
                            "Wrong SCC code format. Correct format is 12233344 or 1-22-333-44");
                }
                value.replaceAll("-", "");
            }
            return value;
        }
        return "";
    }

    /**
     * @param value
     * 
     */
    public String formatSccCode(String value) throws ConverterException {
        String ret;
        if (value.length() == 8) {
            ret = value.substring(0, 1) + "-" + value.substring(1, 3) + "-"
                    + value.substring(3, 6) + "-" + value.substring(6, 8);
            return ret;
        }

        throw new ConverterException("SCC code must be 8 characters long. "
                + value);
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
            return formatSccCode(sValue);
        } catch (ConverterException e) {
            // logger.warn(e);
            return sValue;
        }
    }
}
