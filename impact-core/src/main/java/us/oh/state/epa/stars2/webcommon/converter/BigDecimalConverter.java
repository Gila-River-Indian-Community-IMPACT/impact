package us.oh.state.epa.stars2.webcommon.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class BigDecimalConverter implements Converter, java.io.Serializable {
	
	public static final int DEFAULT_MIN_FRACTION_DIGITS = 2;
	public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
	
	private RoundingMode roundingMode = DEFAULT_ROUNDING_MODE;
	private int minFractionDigits = DEFAULT_MIN_FRACTION_DIGITS;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) throws ConverterException {
		Object ret = null;
		if (context == null) {
			throw new NullPointerException("FacesContext");
		}
		if (component == null) {
			throw new NullPointerException("UIComponent");
		}
		
				
		if(!Utility.isNullOrEmpty(value)) {
			try {
				ret = (new BigDecimal(value.replace(",", ""))
						.setScale(this.minFractionDigits, this.roundingMode));
			} catch (NumberFormatException e) {
				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Invalid value entered",
						"Value not a number");
				throw new ConverterException(message);
			}
		}

		return ret;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
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
		
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.US);
		df.setParseBigDecimal(true);
		df.setMinimumFractionDigits(this.minFractionDigits);
		
		return (df.format((BigDecimal)value));
	}

	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	public int getMinFractionDigits() {
		return minFractionDigits;
	}

	public void setMinFractionDigits(int minFractionDigits) {
		this.minFractionDigits = minFractionDigits;
	}
	
}
