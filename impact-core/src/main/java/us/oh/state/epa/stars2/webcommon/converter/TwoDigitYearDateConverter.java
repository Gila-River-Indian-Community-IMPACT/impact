package us.oh.state.epa.stars2.webcommon.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import oracle.adf.view.faces.context.AdfFacesContext;

//This converter is for SelectDateInput tag with autosubmit=true and validator
//When it convert the input string from UI, it tries to convert the string to the foramt M/d/yyyy first if possible.
//And then call the method of ADF DateTimeConverter to go through the standard converting procedures.


//ADF DateTimeConverter uses Java.text.DateFormat.parse method to parse input date string, and It pass the variable "pattern" to the parse method
//If the pattern is M/d/yyyy (which is also default pattern used by ADF DateTimeConverter), and the year in input date string is only two digits,
//then the Java.text.DateFormat.parse method will consider the "two-digit-year" to be the exact year that the two-digit-year input mean.
//(It will not use the defaultCenturyYear attribute to add any leading digit before the two-digit-year input)
//So any UI input string in M/d/yy format using M/d/yyyy pattern will result in converting the string to M/d/00yy. 




public class TwoDigitYearDateConverter extends oracle.adfinternal.view.faces.convert.DateTimeConverter {

	public TwoDigitYearDateConverter() {}
	
	public TwoDigitYearDateConverter(String pattern){
		super(pattern);
	}
	
	public TwoDigitYearDateConverter(String pattern, String secondaryPattern){
		super(pattern, secondaryPattern);
	}
	

	public Object getAsObject(FacesContext context, UIComponent component, String value){
		//Manipulate the String input to M/d/yyyy format
		if (value != null){
			value = value.trim();
			if (!value.isEmpty()) {
				SimpleDateFormat parseFormat = new SimpleDateFormat("M/d/yy");
				DateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
				AdfFacesContext adfContext = AdfFacesContext.getCurrentInstance();
				Calendar cal = new GregorianCalendar(adfContext.getTwoDigitYearStart(), 0, 0);
				parseFormat.set2DigitYearStart(cal.getTime());
				Date temp = null;
				try {
					temp = parseFormat.parse(value);
				} 
				catch(Exception ex){
					// do not throw any exception here. throw
				}
				finally{
					if (temp != null){
						value = dateFormat.format(temp);
					}
				}
			}
		}
		Object date = super.getAsObject(context, component, value);
		return date;
	}


	public String getAsString(FacesContext context, UIComponent component, Object value){
		return super.getAsString(context, component, value);
	}

}
