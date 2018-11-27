package us.oh.state.epa.stars2.webcommon.converter;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.apache.log4j.Logger;

/**
 * Converter class used to ensure that numbers entered into an inputText component
 * match a specified pattern and do not exceed the specified number of integral
 * or fractional digits. This class will also ensure that trailing zeroes after
 * the decimal point will not be truncated.
 * 
 * The converted value will be returned as a String object and should be stored
 * in the database as a String in order to maintain trailing zeroes.
 */
@SuppressWarnings("serial")
public class SigDigNumberConverter implements javax.faces.convert.Converter, java.io.Serializable {
    public static final String CONVERTER_ID = "us.oh.state.epa.stars2.webcommon.converter.SigDigNumberConverter";
    private static Logger logger = Logger.getLogger(SigDigNumberConverter.class);
    private static String DEFAULT_PATTERN = "###,##0.##";
    
    // pattern needs to be available for serialization
    private String pattern = DEFAULT_PATTERN;
    private boolean nonNumericAllowed;
    private Double maximumValue = 0.0;
    private Double minimumValue = 0.0;
    
    // the fields below must be recalculated when restored from serialization,
    // so they are marked as transient
    private transient int maximumIntegerDigits = 6;
    private transient double internalMaxValue = 1000000.0;
    private transient DecimalFormat decimalFormat;
    private transient boolean initialized;

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

        // check validity of value, but don't change original input
        if (value != null) {
                // make sure number conforms to desired format
                value = formatNumber(value).replace(",", ""); // TFS 6169;
        }
        return value;
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

        String sValue = "";
        if (value instanceof String) {
            sValue = (String)value;
        } else {
            // this class only supports String values
        	String error = "Illegal component value type: " +
                    value.getClass().getName();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Invalid value entered: " + error, error);
            throw new ConverterException(message);
        }

        if (sValue.length() <= 0) {
            return "";
        }

        return sValue;
    }
    
    /**
     * Set the pattern used to specify formatting.
     * @see java.text.DecimalFormat
     * 
     * @param pattern
     */
    public void setPattern(String pattern) {
        if (pattern != null) {
            if (!pattern.equals(this.pattern)) {
                // no need to re-initialize if pattern has not changed
                initialized = false;
            }
            this.pattern = pattern;
        }
    }
    
    /**
     * Get the pattern used to format values.
     * @return
     */
    public String getPattern() {
        return pattern;
    }
    
    /**
     * Validate value to make sure it conforms to pattern and return the 
     * original value (with 'e' converted to 'E' and leading or trailing
     * spaces removed).
     * @param value String to be formatted/validated.
     * @return value.
     * @throws ConverterException
     */
    public String formatNumber(String value) throws ConverterException {
        if (value == null || value.trim().length() == 0) {
            return "";
        }
        
        initialize();

        // convert to upper case to properly handle exponents
        String workValue = value.trim().toUpperCase();
        double valueDouble = 0.0;
        if (workValue.length() > 0) {
            try {
                // make sure we have a valid number
                valueDouble = decimalFormat.parse(workValue).doubleValue();
            } catch (ParseException e) {
                String error = "Input does not match expected pattern: " 
                        + pattern + ".";
            	FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Invalid value entered: " + error, error);
                throw new ConverterException(message, e);
            }
            
            if (!nonNumericAllowed) {
                try {
                    workValue = workValue.replaceAll(",", "");
                    @SuppressWarnings("unused")
                    Double test = Double.valueOf(workValue);
                } catch (NumberFormatException e) {
                	String error = "Non-numeric characters are not allowed.";
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Invalid value entered: " + error, error);
                    throw new ConverterException(message, e);
                }
            }
            
            // make sure value does not exceed maximum
            if (exceedsMaxValue(valueDouble)) {
            	String error = "Must be less than or equal to " + internalMaxValue + ".";
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Invalid value entered: " + error, error);
                throw new ConverterException(message);
            } else if (lessThanMinValue(valueDouble)) {
            	String error = "Must be greater than or equal to " + minimumValue + ".";
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Invalid value entered: " + error, error);
                throw new ConverterException(message);
            }
        }
        
        int fractionalDigits = 0;
        Pattern fractionPattern = Pattern.compile(".*[0-9]*\\.([0-9]+).*");
        Matcher m = fractionPattern.matcher(value);
        if (m.matches()) {
            String fractionalValue = m.group(1);
            fractionalDigits = fractionalValue.length();
        }
        
        // limit input to specified number of digits
        if (fractionalDigits > decimalFormat.getMaximumFractionDigits()) {
        	String error = "A maximum of " + 
                    decimalFormat.getMaximumFractionDigits() 
                    + " fractional digits are allowed.";
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Invalid value entered: " + error, error);
            throw new ConverterException(message);
        }
        
        return value;
    }
    
    private boolean exceedsMaxValue (double value) {
        boolean ret = false;
        if (maximumValue > 0.0 && value > maximumValue) {
            ret = true;
        } else if (maximumValue <= 0.0) {
            ret = (value > internalMaxValue);
        }
        return ret;
    }
    
    private boolean lessThanMinValue (double value) {
    	boolean ret = false;
    	ret = (value < minimumValue);
    	return ret;
    }
    
    /**
     * Create a DecimalFormat object based on pattern and perform some additional
     * initialization needed for this class. If pattern is not a valid pattern for
     * DecimalFormat, DEFAULT_PATTERN will be used instead.
     * @return
     */
    private void initialize() {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat();
            initialized = false;
        }
        
        // only initialize data as needed to improve performance
        if (!initialized) {
            try {
                decimalFormat.applyPattern(pattern);
            } catch (IllegalArgumentException e) {
                decimalFormat.applyPattern(DEFAULT_PATTERN);
                logger.error("Invalid pattern specified for SigDigConverter: [" 
                        + pattern + "] defaulting to " + DEFAULT_PATTERN);
            }
            maximumIntegerDigits = 0;
            int index = 0;
            while (index < pattern.length() && pattern.charAt(index) != '.') {
                if (pattern.charAt(index) == '#' || pattern.charAt(index) == '0') {
                    maximumIntegerDigits++; 
                }
                index++;
            }
            
            decimalFormat.setGroupingUsed(true);
            
            StringBuilder maxValueStr = new StringBuilder("1");
            for (int i=0; i<maximumIntegerDigits; i++) {
                maxValueStr.append("0");
            }
            
            if (maximumValue <= 0.0) {
                internalMaxValue = Double.parseDouble(maxValueStr.toString());
            } else {
                internalMaxValue = maximumValue;
            }
            
            initialized = true;
        }
    }
    
    /**
     * main is provided for test purposes only.
     * @param args
     */
    public static void main(String[] args) {
        String[] testNumbers = {
                "123.45 mg",
                "123.45",
                "1,234.56",
                "12.3e3",
//                "12.345e-3",
//                "ABCX",
//                "1000293",
//                "1.00",
//                "1.10",
////                "1234.56",
////                "12,345.67",
//                "1,234.56E-13",
//                "1234.56E-13",
//                ".123456E-3",
//                "0.123456E-3",
//                ".1234567809",
//                "98765432328",
//                "9876543232.",
//                "9.8765432328",
//                ".9876543232",
//                "1,234.56E-3",
//                "-1.0",
//                "0.020001",
//                ".02000",
//                ".0000000123",
//                "1234.50E0",
//                "1234.50E00",
//                "1234.50E+0",
//                "1234.50E+00",
//                "1234.50E-0",
//                "1234.50E-00",
//                "5.20",
//                "5.2 E01",
//                "5.2E 01",
//                "1234.50E00",
//                "1.23450E+0",
//                "1.23450E+00",
//                "1.23450E-0",
//                "1.23450E-00",
//                "24,989.12345678",
//                "12345.678E7",
//                "1.23 lb/MMBtu",
//                "1.2345 lb/MMBtu",
        };
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "###.##";
        converter.setPattern(pattern);
        System.out.println("Testing with pattern: " + pattern + ", non-numeric characters allowed");
        for (String number : testNumbers) {
            try {
                System.out.print(number + "\t");
                converter.formatNumber(number);
                System.out.println(number + " ok");
            } catch (ConverterException e) {
                logger.error(e.getMessage());
            }
        }
        converter.setNonNumericAllowed(false);
        System.out.println("Testing with pattern: " + pattern + ", non-numeric characters NOT allowed");
        for (String number : testNumbers) {
            try {
                System.out.print(number + "\t");
                converter.formatNumber(number);
                System.out.println(number + " ok");
            } catch (ConverterException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * By default, a SigDigNumber may contain non-numeric characters after the
     * numeric value (e.g units after a number like "2.3 mg"). This flag will
     * be set if non-numeric characters are not allowed.
     * @return numericOnly flag (true or false).
     */
    public final boolean isNonNumericAllowed() {
        return nonNumericAllowed;
    }

    /**
     * By default, a SigDigNumber may contain non-numeric characters after the
     * numeric value (e.g units after a number like "2.3 mg"). This flag should
     * be set if non-numeric characters are not allowed.
     * @param numericOnly
     */
    public final void setNonNumericAllowed(boolean nonNumericAllowed) {
        this.nonNumericAllowed = nonNumericAllowed;
    }

    public final Double getMaximumValue() {
        return maximumValue;
    }

    public final void setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
    }

	public final Double getMinimumValue() {
		return minimumValue;
	}

	public final void setMinimumValue(Double minimumValue) {
		this.minimumValue = minimumValue;
	}
}
