package us.oh.state.epa.stars2.webcommon.documentgeneration;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.aspose.words.ControlChar;
import com.aspose.words.Document;
import com.aspose.words.IReplacingCallback;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.ReplaceAction;
import com.aspose.words.ReplacingArgs;
import com.aspose.words.Run;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.converter.PhoneNumberConverter;
import us.wy.state.deq.impact.App;

public abstract class DocumentBuilder implements Serializable {
	
	private static final long serialVersionUID = 7171003774393503328L;

	public final static String DEFAULT_DATE_TIME_FORMAT = "MM/dd/yyyy";
	public final static int DEFAULT_TRUNCATE_LENGTH = 250;
	public final static String EMPTY_STRING = "";
	public final static String NEW_LINE = "\n";
	
	protected Logger logger = Logger.getLogger(DocumentBuilder.class);
	
	protected Map<String, Object> tagValueMap = new HashMap<String, Object>();
	
	/**
	 * Set up the data needed for the document generation
	 *
	 * @throws DocumentGenerationException
	 */
	abstract public void loadData() throws DocumentGenerationException;
	
	/**
	 * Set the id of the object for which the document is being generated
	 * 
	 * @param id
	 */
	abstract public <E> void setId(final E id);
	
	/**
	 * Generates a document from the given template
	 * 
	 * @param template
	 * @return Document
	 * 
	 * @throws DocumentGenerationException
	 */
	protected final Document generateDocument(final Document template) throws DocumentGenerationException {

		Document document = null;
		
		if (null != template) {

			try {
	
				// clone the template and replace the tags in the cloned document
				document = template.deepClone();
	
				// perform tag substitution
				Iterator<String> iter = this.tagValueMap.keySet().iterator();
	
				while (iter.hasNext()) {
	
					String tag = (String) iter.next();
					
					Object value = this.tagValueMap.get(tag);
	
					String regex = "\\<" + tag + "\\>";
					
					if (value instanceof List) {

						// substitute the tag with a table
						document.getRange().replace(Pattern.compile(regex), new TableSubstitutor(value) , false);
						
					} else {
						
						// substitute the tag with the value
						String strVal = Utility.stringValueOf(value);
		
						if (null != strVal) {
							
							// TFS 8250
							// the substitution string may contain line breaks, but Aspose 
							// does not honor the line breaks when performing the substitution.
							// to get around this issue, we will replace the new lines in the
							// substitution string with Aspose's line break control character
							strVal = strVal.replace(NEW_LINE, ControlChar.LINE_BREAK);
							
							document.getRange().replace(Pattern.compile(regex), strVal);
							
						}
					}
				}
			}  catch (Exception e) {
				// since aspose only throws Exception, we need a catch all catch block
				logger.error("Failed to generate document " + e.getMessage(), e);
				throw new DocumentGenerationException(e.getMessage(), e);
			}
		}

		return document;
	}
	
	
	// utility methods
	
	/**
	 * Constructs the user name as first name and last name and returns the constructed user name
	 * @param userId
	 * @return userName
	 * @throws RemoteException
	 * @throws ServiceFactoryException
	 */
	protected String retrieveUserName(final Integer userId) throws RemoteException {

		String userName = null;

		if (null != userId) {

			UserDef ud = getInfrastructureService().retrieveUserDef(userId);

			if (null != ud) {

				String fName = ud.getUserFirstNm();
				String lName = ud.getUserLastNm();

				if (null != fName) {
					userName = fName;
				}

				if (null != lName) {
					userName = (null != userName) ? userName + " " + lName : lName;
				}
				
				if (!AbstractDAO.translateIndicatorToBoolean(ud.getActiveInd())) {
					userName = userName + DefSelectItems.INACTIVE;
				}
			}
		}

		return userName;
	}
	
	
	/**
	 * Returns formatted date
	 * 
	 * @param dateTimeFormat
	 * @param Timestamp
	 * @return Date formatted in dateTimeFormat
	 */
	protected String getFormattedDate(final String dateTimeFormat, final Timestamp date) {

		String formattedDate = null;

		if (null != dateTimeFormat && null != date) {

			DateFormat formatter = new SimpleDateFormat(dateTimeFormat);

			formattedDate = formatter.format(new Date(date.getTime()));
		}

		return formattedDate;
	}
	
	/**
	 * Returns formatted date
	 * 
	 * @param dateTimeFormat
	 * @param Date
	 * @return Date formatted in dateTimeFormat
	 */
	protected String getFormattedDate(final String dateTimeFormat, final Date date) {

		String formattedDate = null;

		if (null != dateTimeFormat && null != date) {

			DateFormat formatter = new SimpleDateFormat(dateTimeFormat);

			formattedDate = formatter.format(new Date(date.getTime()));
		}

		return formattedDate;
	}

	
	/**
	 * Returns phone number in the NNN-NNN-NNNN format
	 * @param phoneNumber
	 * @return Formatted phone number
	 */
	protected String getFormattedPhoneNbr(final String phoneNumber) {
		
		return new PhoneNumberConverter().tryFormatPhoneNumber(phoneNumber);
		
	}
	
	/**
	 * Returns string truncated to 250 characters
	 * 
	 * @param value
	 * @return truncated string
	 */
	protected String truncate(final String value) {
		return truncate(value, DEFAULT_TRUNCATE_LENGTH);
	}

	/**
	 * Returns string truncated to truncatedAt characters
	 * 
	 * @param value
	 * @param truncateAt
	 * @return truncated string
	 */
	protected String truncate(final String value, final int truncateAt) {

		if (!Utility.isNullOrEmpty(value) && value.length() > truncateAt) {
			return value.substring(0, truncateAt) + "...";
		} else {
			return value;
		}
	}
	
	// service methods
	
	protected FacilityService getFacilityService() {
		return App.getApplicationContext().getBean(FacilityService.class);
	}

	protected InfrastructureService getInfrastructureService() {
		return App.getApplicationContext().getBean(InfrastructureService.class);
	}

	protected FullComplianceEvalService getFullComplianceEvalService() {
		return App.getApplicationContext().getBean(FullComplianceEvalService.class);
	}
	
	
	/**
	 * Replaces each tag in the excludedTags list with an empty string
	 * 
	 * @param excludedTags
	 * @return none
	 * @throws none
	 */
	public final void removeExcludedTags(List<String> excludedTags) {
		if (excludedTags != null) {
			for (String tag : excludedTags) {
				tagValueMap.put(tag, EMPTY_STRING);
			}
		}
	}
	
}


class TableSubstitutor implements IReplacingCallback {

	private List<?> table;

	public TableSubstitutor(Object obj) {

		if (obj instanceof List) {

			this.table = (List<?>) obj;
		}
	}

	@Override
	public int replacing(ReplacingArgs arg) throws Exception {

		Run tag = (Run) arg.getMatchNode();

		Document document = (Document) tag.getDocument();

		com.aspose.words.DocumentBuilder db = new com.aspose.words.DocumentBuilder(document);

		// move the cursor to where the tag is found and begin inserting a table
		db.moveTo(tag);
		
		if (null != this.table) {

			db.startTable();
			
			// left align the table contents
			db.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
			
			for (int i = 0; i < this.table.size(); ++i) {
	
				if (this.table.get(i) instanceof List) {
	
					List<?> row = (List<?>) this.table.get(i);
	
					for (int j = 0; j < row.size(); ++j) {
						
						// make the first row repeatable across all the
						// pages the table spans
						if (0 == i) {
							db.getRowFormat().setHeadingFormat(true);
						} else {
							db.getRowFormat().setHeadingFormat(false);
						}
						
						// do not allow the row to break across pages
						db.getRowFormat().setAllowBreakAcrossPages(false);
						
						Object val = row.get(j);
	
						db.insertCell();
						
						String strVal = Utility.stringValueOf(val);
						
						if (null != strVal) {
							
							db.write(Utility.stringValueOf(val));
						} 
					}
				}
	
				db.endRow();
			}
	
			db.endTable();
		}

		return ReplaceAction.REPLACE;
	}

}