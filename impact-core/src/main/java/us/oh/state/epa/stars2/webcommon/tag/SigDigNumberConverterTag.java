package us.oh.state.epa.stars2.webcommon.tag;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.webapp.ConverterTag;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;

import us.oh.state.epa.stars2.webcommon.converter.SigDigNumberConverter;

/**
 * Tag class for us.oh.state.epa.stars2.webcommon.converter.SigDigNumberConverter.
 *
 */
@SuppressWarnings("serial")
public class SigDigNumberConverterTag extends ConverterTag implements java.io.Serializable {
    private String pattern;
    private String maximumValue;
    private String minimumValue;
    private boolean nonNumericAllowed;
    
    public SigDigNumberConverterTag() {
        super();
        setConverterId(SigDigNumberConverter.CONVERTER_ID);
    }

    @Override
    protected Converter createConverter() throws JspException {
        SigDigNumberConverter converter = (SigDigNumberConverter)super.createConverter();

        converter.setPattern(eval(pattern));
        converter.setNonNumericAllowed(nonNumericAllowed);
        if (maximumValue != null) {
            String maxValueStr = eval(maximumValue);
            try {
                Double maxValue = Double.parseDouble(maxValueStr);
                converter.setMaximumValue(maxValue);
            } catch (NumberFormatException nfe) {
                throw new JspException("Exception converting maximumValue [" +
                        maxValueStr + " to a double (unevaluated string = " 
                        + maximumValue + ")", nfe);
            }
        }

        if (minimumValue != null) {
            String maxValueStr = eval(minimumValue);
            try {
                Double maxValue = Double.parseDouble(maxValueStr);
                converter.setMinimumValue(maxValue);
            } catch (NumberFormatException nfe) {
                throw new JspException("Exception converting minimumValue [" +
                        maxValueStr + " to a double (unevaluated string = " 
                        + minimumValue + ")", nfe);
            }
        }

        return converter;
    }

    @Override
    public void release() {
        super.release();
        pattern = null;
    }

    public final String getPattern() {
        return pattern;
    }

    public final void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public final boolean isNonNumericAllowed() {
        return nonNumericAllowed;
    }

    public final void setNonNumericAllowed(boolean nonNumericAllowed) {
        this.nonNumericAllowed = nonNumericAllowed;
    }
    
    /**
     * Evaluate an EL expression and return the result.
     * @param expression
     * @return
     */
    public static String eval(String expression) {
        String evalExpression = expression;
        if (expression != null && UIComponentTag.isValueReference(expression)) {
           FacesContext context = FacesContext.getCurrentInstance();
           Application app = context.getApplication();
           evalExpression = (String)app.createValueBinding(expression).getValue(context);
        }
        
        return evalExpression;
     }

    public final String getMaximumValue() {
        return maximumValue;
    }

    public final void setMaximumValue(String maximumValue) {
        this.maximumValue = maximumValue;
    }

	public final String getMinimumValue() {
		return minimumValue;
	}

	public final void setMinimumValue(String minimumValue) {
		this.minimumValue = minimumValue;
	}

}
