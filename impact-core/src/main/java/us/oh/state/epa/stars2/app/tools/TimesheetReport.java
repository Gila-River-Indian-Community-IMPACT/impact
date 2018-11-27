package us.oh.state.epa.stars2.app.tools;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.def.TimesheetFunctionTypeDef;
import us.oh.state.epa.stars2.def.TimesheetMonthlyReportTypeDef;
import us.oh.state.epa.stars2.def.TimesheetSectionTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.util.Pair;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

import com.aspose.words.Cell;
import com.aspose.words.Document;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.Row;
import com.aspose.words.RowCollection;
import com.aspose.words.Table;

public class TimesheetReport {

	private transient Logger logger = Logger.getLogger(this.getClass());

	public static final int MAX_HOURS_VALUE = 23;
	public static final int MAX_SECONDS_VALUE = 59;

	public static final int TIMESHEET_TABLE_INDEX = 1;

	private String reportMonth;
	private Integer reportYear;
	private String reportTypeCd;

	private UserDef userDef;

	private DateTime reportStartDate;
	private DateTime reportEndDate;

	private Integer daysInMonth;

	private boolean nonExempt = false;

	private ArrayList<TimeSheetRow> timesheetRows;

	private TmpDocument report;

	private List<Pair<String, String>> functionSectionPairs;
	private HashMap<String, HashMap<DateTime, ArrayList<TimeSheetRow>>> nonOverTimeHoursMap;
	private HashMap<String, HashMap<DateTime, ArrayList<TimeSheetRow>>> overTimeHoursMap;
	private HashMap<DateTime, Float> dailyTotalHours = new HashMap<DateTime, Float>();

	TimesheetReport() {
		super();
	}

	TimesheetReport(String reportMonth, Integer reportYear,
			String reportTypeCd, UserDef userDef,
			ArrayList<TimeSheetRow> timesheetRows) {
		super();
		this.reportMonth = reportMonth;
		this.reportYear = reportYear;
		this.reportTypeCd = reportTypeCd;
		this.timesheetRows = timesheetRows;
		this.userDef = userDef;

		// set the report start and end dates based on the given month and year
		if (!Utility.isNullOrEmpty(reportMonth)
				&& !Utility.isNullOrZero(reportYear)) {
			// set start date to the first day of the selected month
			this.reportStartDate = new DateTime(reportYear,
					Integer.parseInt(reportMonth), 1, 0, 0);

			this.daysInMonth = reportStartDate.dayOfMonth().getMaximumValue();

			// set end date to the last day of the selected month
			this.reportEndDate = new DateTime(reportYear,
					Integer.parseInt(reportMonth), daysInMonth,
					MAX_HOURS_VALUE, MAX_SECONDS_VALUE);
		}

		if (!Utility.isNullOrEmpty(reportTypeCd)
				&& reportTypeCd
						.equals(TimesheetMonthlyReportTypeDef.NON_EXEMPT_GROUPED_REPORT)) {
			nonExempt = true;
		}
	}

	public String getReportMonth() {
		return reportMonth;
	}

	public void setReportMonth(String reportMonth) {
		this.reportMonth = reportMonth;
	}

	public Integer getReportYear() {
		return reportYear;
	}

	public void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}

	public String getReportTypeCd() {
		return reportTypeCd;
	}

	public void setReportTypeCd(String reportTypeCd) {
		this.reportTypeCd = reportTypeCd;
	}

	public UserDef getUserDef() {
		return userDef;
	}

	public void setUserDef(UserDef userDef) {
		this.userDef = userDef;
	}

	public boolean isNonExempt() {
		return nonExempt;
	}

	public void setNonExempt(boolean nonExempt) {
		this.nonExempt = nonExempt;
	}

	public DateTime getReportStartDate() {
		return reportStartDate;
	}

	public void setReportStartDate(DateTime reportStartDate) {
		this.reportStartDate = reportStartDate;
	}

	public DateTime getReportEndDate() {
		return reportEndDate;
	}

	public void setReportEndDate(DateTime reportEndDate) {
		this.reportEndDate = reportEndDate;
	}

	public Integer getDaysInMonth() {
		return daysInMonth;
	}

	public void setDaysInMonth(Integer daysInMonth) {
		this.daysInMonth = daysInMonth;
	}

	public ArrayList<TimeSheetRow> getTimesheetRows() {
		return timesheetRows;
	}

	public void setTimesheetRows(ArrayList<TimeSheetRow> timesheetRows) {
		this.timesheetRows = timesheetRows;
	}

	public TmpDocument getReport() {
		return report;
	}

	public void setReport(TmpDocument report) {
		this.report = report;
	}

	public List<Pair<String, String>> getFunctionSectionPairs() {
		return functionSectionPairs;
	}

	public void setFunctionSectionPairs(
			List<Pair<String, String>> functionSectionPairs) {
		this.functionSectionPairs = functionSectionPairs;
	}

	public HashMap<String, HashMap<DateTime, ArrayList<TimeSheetRow>>> getNonOverTimeHoursMap() {
		return nonOverTimeHoursMap;
	}

	public void setNonOverTimeHoursMap(
			HashMap<String, HashMap<DateTime, ArrayList<TimeSheetRow>>> nonOverTimeHoursMap) {
		this.nonOverTimeHoursMap = nonOverTimeHoursMap;
	}

	public HashMap<String, HashMap<DateTime, ArrayList<TimeSheetRow>>> getOverTimeHoursMap() {
		return overTimeHoursMap;
	}

	public void setOverTimeHoursMap(
			HashMap<String, HashMap<DateTime, ArrayList<TimeSheetRow>>> overTimeHoursMap) {
		this.overTimeHoursMap = overTimeHoursMap;
	}

	public HashMap<DateTime, Float> getDailyTotalHours() {
		return dailyTotalHours;
	}

	public void setDailyTotalHours(HashMap<DateTime, Float> dailyTotalHours) {
		this.dailyTotalHours = dailyTotalHours;
	}

	private void initReportGeneration() {

		// initialize data structures needed for generating the report
		this.nonOverTimeHoursMap = new HashMap<String, HashMap<DateTime, ArrayList<TimeSheetRow>>>();
		this.overTimeHoursMap = new HashMap<String, HashMap<DateTime, ArrayList<TimeSheetRow>>>();
		this.functionSectionPairs = new ArrayList<Pair<String, String>>();
		this.dailyTotalHours = new HashMap<DateTime, Float>();

		buildFunctionSectionPairs();
		insertKeysIntoMap();
		buildTimeSheetHourMaps();
	}

	private void buildFunctionSectionPairs() {
		// get the function codes per the sort order as defined in the
		// definition list so that the rows in the report can be created
		// in the same order
		List<SelectItem> sortedFunctionList = TimesheetFunctionTypeDef
				.getSortOrderData().getItems().getAllItems();

		for (SelectItem si : sortedFunctionList) {
			String functionCd = (String) si.getValue();
			this.functionSectionPairs.add(new Pair<String, String>(functionCd,
					null));
		}

		// sections that need to be reported separately i.e., split out sections
		List<SelectItem> sections = TimesheetSectionTypeDef.getData()
				.getItems().getAllItems();
		for (SelectItem si : sections) {
			String sectionCd = (String) si.getValue();
			if (TimesheetSectionTypeDef.isReportSeparately(sectionCd)) {
				String functionCd = TimesheetSectionTypeDef
						.getFunctionCd(sectionCd);
				String sectionDisplayCode = getSectionDisplayCode(sectionCd);
				this.functionSectionPairs.add(new Pair<String, String>(
						functionCd, sectionDisplayCode));
			}
		}
	}

	private void insertKeysIntoMap() {
		if (null != this.functionSectionPairs) {
			for (Pair<String, String> p : this.functionSectionPairs) {
				String key = generateKey(p);
				String functionCd = p.getFirst();
				this.nonOverTimeHoursMap.put(key,
						new HashMap<DateTime, ArrayList<TimeSheetRow>>());
				if (isAddOverTimeRow(functionCd)) {
					this.overTimeHoursMap.put(key,
							new HashMap<DateTime, ArrayList<TimeSheetRow>>());
				}
			}
		}
	}

	private void buildTimeSheetHourMaps() {
		// build the hashmap(s) with the timesheet info
		for (TimeSheetRow tsr : timesheetRows) {
			DateTime tsEntryDate = new DateTime(tsr.getDate());
			// timesheet entry date should be in the month for which the report
			// is being generated
			if (isDateValidForReport(tsEntryDate)) {
				String functionCd = tsr.getFunction();
				String sectionCd = getSectionDisplayCode(tsr.getSection());
				
				String key = generateKey(new Pair<String, String>(functionCd,
						sectionCd));

				if (isAddOverTimeRow(functionCd) && tsr.isOvertime()) {
					addTimeSheetEntryToOverTimeHoursMap(key, tsr);
				} else {
					addTimeSheetEntryToNonOverTimeHoursMap(key, tsr);
				}
			}
		}
	}

	private final String generateKey(Pair<String, String> p) {
		String key;
		key = (null == p.getSecond()) ? p.getFirst() : p.getFirst() + "_"
				+ p.getSecond();

		return key;
	}

	private void addTimeSheetEntryToOverTimeHoursMap(String key,
			TimeSheetRow tsr) {
		HashMap<DateTime, ArrayList<TimeSheetRow>> dateHoursMap;
		ArrayList<TimeSheetRow> tsrList;
		DateTime date = new DateTime(tsr.getDate()).withTimeAtStartOfDay();

		dateHoursMap = this.overTimeHoursMap.get(key);
		if (null != dateHoursMap) {
			tsrList = dateHoursMap.get(date);
			if (null != tsrList) {
				tsrList.add(tsr);
			} else {
				tsrList = new ArrayList<TimeSheetRow>();
				tsrList.add(tsr);
			}

			dateHoursMap.put(date, tsrList);

			this.overTimeHoursMap.put(key, dateHoursMap);
		}
	}

	private void addTimeSheetEntryToNonOverTimeHoursMap(String key,
			TimeSheetRow tsr) {
		HashMap<DateTime, ArrayList<TimeSheetRow>> dateHoursMap;
		ArrayList<TimeSheetRow> tsrList;
		DateTime date = new DateTime(tsr.getDate()).withTimeAtStartOfDay();

		dateHoursMap = this.nonOverTimeHoursMap.get(key);
		if (null != dateHoursMap) {
			tsrList = dateHoursMap.get(date);
			if (null != tsrList) {
				tsrList.add(tsr);
			} else {
				tsrList = new ArrayList<TimeSheetRow>();
				tsrList.add(tsr);
			}

			dateHoursMap.put(date, tsrList);

			this.nonOverTimeHoursMap.put(key, dateHoursMap);
		}
	}

	/**
	 * Generates the monthly timesheet report in MS-Word
	 * 
	 * @return String
	 * @throws none
	 */
	public String generateMonthlyReport() {
		TemplateDocument reportTemplate = getTimeSheetReportTemplate(this.reportTypeCd);

		if (null == reportTemplate) {
			DisplayUtil.displayError("Could not find template document.");
			return null;
		}

		String fileName = reportTypeCd + "_" + userDef.getUserId() + "_"
				+ reportMonth + reportYear;

		this.report = new TmpDocument();
		this.report.setTmpFileName(fileName);
		this.report.setExtension("docx");

		String documentPath = report.getDirName() + File.separator
				+ "TimesheetReports" + report.getBasePath() + "."
				+ report.getExtension();

		DocumentGenerationBean dgb = new DocumentGenerationBean();
		dgb.setTimesheetInfo(userDef, reportEndDate.toString("MM/dd/yyyy"));

		try {
			// generate the basic report by performing keyword substitutions
			// only
			String docURL = DocumentUtil.generateAsposeDocument(
					reportTemplate.getTemplateDocPath(), dgb, documentPath);

			Document timesheetDoc = DocumentUtil.getDocument(documentPath);

			// initialize data structures needed for generating the report
			initReportGeneration();
			Row tsHourRows[] = createTimesheetRows(timesheetDoc);

			// insert the timesheet rows into the generated timesheet report
			if (updateReport(timesheetDoc, documentPath, tsHourRows)) {
				report.setGeneratedDocumentPath(docURL);
				return "dialog:downloadMonthlyTimesheetReport";
			} else {
				DisplayUtil
						.displayError("Failed to generate monthly timesheet report");
				return null;
			}

		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
			DisplayUtil.displayError(ioe.getMessage());
			return null;
		} catch (DocumentGenerationException dge) {
			logger.error(dge.getMessage());
			DisplayUtil.displayError(dge.getMessage());
			return null;
		}
	}

	/**
	 * Returns an array of Aspose row containing timesheet hours for each
	 * function against which hours are entered. The array will also include any
	 * function for which there are no hours but the function is marked as fixed
	 * in the definition list.
	 * 
	 * @param timesheetDoc
	 * @return returns an array of Aspose row
	 * @throws none
	 */
	private Row[] createTimesheetRows(Document timesheetDoc) {
		ArrayList<Row> rows = new ArrayList<Row>();
		Row row = null;

		for (Pair<String, String> p : this.functionSectionPairs) {
			String functionCd = p.getFirst();
			String sectionCd = Utility.isNullOrEmpty(p.getSecond()) ? "" : p
					.getSecond();
			String key = generateKey(p);
			HashMap<DateTime, ArrayList<TimeSheetRow>> dateHoursMap;

			dateHoursMap = this.nonOverTimeHoursMap.get(key);
			if ((null != dateHoursMap && !dateHoursMap.isEmpty())
					|| TimesheetFunctionTypeDef.isFixed(functionCd)) {
				row = createAsposeRow(timesheetDoc, functionCd, sectionCd,
						dateHoursMap, false);
				if (null != row) {
					rows.add(row);
				}
			}

			if (isAddOverTimeRow(functionCd)) {
				dateHoursMap = this.overTimeHoursMap.get(key);
				if ((null != dateHoursMap && !dateHoursMap.isEmpty())
						|| TimesheetFunctionTypeDef.isFixed(functionCd)) {
					row = createAsposeRow(timesheetDoc, functionCd, sectionCd,
							dateHoursMap, true);
					if (null != row) {
						rows.add(row);
					}
				}
			}
		}

		return rows.toArray(new Row[0]);
	}

	/**
	 * Creates an Aspose row containing timesheet hours for a given function
	 * 
	 * @param doc
	 * @param functionCd
	 * @param sectionCd
	 * @param dateHoursMap
	 * @parma overTime
	 * @return returns an Aspose row with timesheet hours for a given function
	 */
	private Row createAsposeRow(Document doc, String functionCd,
			String sectionCd,
			HashMap<DateTime, ArrayList<TimeSheetRow>> dateHoursMap,
			boolean overTime) {

		Table timesheetTable = DocumentUtil
				.getTable(doc, TIMESHEET_TABLE_INDEX);

		if (null == timesheetTable || timesheetTable.getRows().getCount() == 0) {
			DisplayUtil
					.displayError("Could not get the timesheet table from the template or the table has no rows");
			return null;
		}

		int numOfFixedColumns = timesheetTable.getRows().get(0).getCount();
		Cell[] cells = new Cell[numOfFixedColumns + daysInMonth];

		// create cells with total hours entered for each day in a given month
		int i = numOfFixedColumns;
		Float functionTotalHours = 0.0f; // total hours entered against the
		// given function
		float[] dailyTotals = computeDailyTotalHours(dateHoursMap);
		for (float hours : dailyTotals) {
			functionTotalHours += hours;
			String cellValue = hours == 0.0f ? "" : Float.toString(hours);
			cells[i++] = DocumentUtil.createNewCell(doc, cellValue,
					DocumentUtil.normalFont, ParagraphAlignment.RIGHT, 0);
		}

		// create cells for function, section, OT, and Total Hours
		String totalHoursCellValue = Float.toString(functionTotalHours);
		i = 0;
		cells[i++] = DocumentUtil.createNewCell(doc, functionCd,
				DocumentUtil.boldFont, ParagraphAlignment.CENTER, 0);
		cells[i++] = DocumentUtil.createNewCell(doc, sectionCd,
				DocumentUtil.boldFont, ParagraphAlignment.CENTER, 0);
		if (isNonExempt()) {
			// non-exempt report will have the OT column
			// set the value of the OT column to Y if there are overtime charges
			String overTimeCellValue = overTime ? "Y" : "";
			cells[i++] = DocumentUtil.createNewCell(doc, overTimeCellValue,
					DocumentUtil.boldFont, ParagraphAlignment.CENTER, 0);
		}
		cells[i++] = DocumentUtil.createNewCell(doc, totalHoursCellValue,
				DocumentUtil.boldFont, ParagraphAlignment.RIGHT, 0);

		return DocumentUtil.createRow(doc, cells);
	}

	/**
	 * Inserts timesheet hours into the generated report
	 * 
	 * @param timesheetDoc
	 * @param timesheetDocPath
	 * @param rows
	 * @return true if insert was successful, false otherwise
	 * @throws none
	 */
	private boolean updateReport(Document timesheetDoc,
			String timesheetDocPath, Row[] rows) {
		Table table = DocumentUtil
				.getTable(timesheetDoc, TIMESHEET_TABLE_INDEX);

		if (null == table || table.getRows().getCount() == 0) {
			DisplayUtil
					.displayError("Could not get the timesheet table from the template or the table has no rows");
			return false;
		}

		RowCollection rc = table.getRows();
		try {
			// add days to the header row
			Row header = rc.get(0);
			Color bkgrndColor = header.getCells().get(0).getCellFormat()
					.getShading().getBackgroundPatternColor();
			for (int i = 1; i <= daysInMonth; ++i) {
				Cell cell = DocumentUtil.createNewCell(timesheetDoc,
						Integer.toString(i), DocumentUtil.boldFont,
						ParagraphAlignment.CENTER, 0);
				cell.getCellFormat().getShading()
						.setBackgroundPatternColor(bkgrndColor);
				header.appendChild(cell);
			}

			// compute total hours for each day in the given month and add the
			// computed total to the total hours row i.e., 2nd row
			Row totalHoursRow = rc.get(1);
			bkgrndColor = totalHoursRow.getCells().get(0).getCellFormat()
					.getShading().getBackgroundPatternColor();
			HashMap<DateTime, Float> totalHoursMap = getDailyTotalHours();
			if (null != totalHoursMap) {
				Cell totalHoursCell = totalHoursRow.getLastCell(); // the total
																	// cell in
																	// the total
																	// hours row
				Float totalHours = 0.0f;
				for (DateTime date = reportStartDate; date
						.isBefore(reportEndDate.getMillis()); date = date
						.plusDays(1)) {
					Float dailyHours = totalHoursMap.get(date);
					if (null == dailyHours) {
						dailyHours = 0.0f;
					}
					totalHours += dailyHours;
					Cell cell = DocumentUtil.createNewCell(timesheetDoc,
							Float.toString(dailyHours), DocumentUtil.boldFont,
							ParagraphAlignment.RIGHT, 0);
					cell.getCellFormat().getShading()
							.setBackgroundPatternColor(bkgrndColor);
					totalHoursRow.appendChild(cell);
				}
				// insert the computed total hours for the given month in the
				// total cell of the total hours row
				totalHoursCell.getFirstParagraph().appendChild(
						DocumentUtil.createNewRun(timesheetDoc,
								Float.toString(totalHours),
								DocumentUtil.boldFont));
			}
		} catch (Exception e) {
			logger.error(
					"Could not get the cell background color" + e.getMessage(),
					e);
			return false;
		}

		// append empty cells to the remaining rows
		for (int j = 2; j < rc.getCount(); ++j) {
			Row row = rc.get(j);
			for (int i = 1; i <= daysInMonth; ++i) {
				row.appendChild(DocumentUtil.createNewCell(timesheetDoc, "",
						DocumentUtil.normalFont, ParagraphAlignment.CENTER, 0));
			}
		}

		// append timesheet rows after the first row
		Row refRow = rc.get(0);
		for (Row row : rows) {
			table.insertAfter(row, refRow);
			refRow = row;
		}

		return DocumentUtil.saveDocument(timesheetDoc, timesheetDocPath);
	}

	private float[] computeDailyTotalHours(
			HashMap<DateTime, ArrayList<TimeSheetRow>> dateHoursMap) {
		float[] dailyTotals = new float[this.daysInMonth];
		int i = 0;
		DateTime date = new DateTime(reportStartDate.getMillis());
		while (date.isBefore(reportEndDate.getMillis())) {
			ArrayList<TimeSheetRow> tsrList = null;
			Float totalHours = 0.0f;
			tsrList = dateHoursMap.get(date);
			if (null != tsrList && !tsrList.isEmpty()) {
				for (TimeSheetRow tsr : tsrList) {
					totalHours += tsr.getHours();
				}
			}
			dailyTotals[i++] = totalHours;

			Float dailyTotal = this.dailyTotalHours.get(date);
			if (null == dailyTotal) {
				dailyTotal = new Float(totalHours);
			} else {
				dailyTotal += totalHours;
			}
			this.dailyTotalHours.put(date, dailyTotal);

			date = date.plusDays(1);
		}
		return dailyTotals;
	}

	private boolean isDateValidForReport(DateTime tsEntryDate) {
		boolean valid = false;
		if (null != tsEntryDate
				&& (tsEntryDate.isAfter(reportStartDate.getMillis()) || tsEntryDate
						.isEqual(reportStartDate.getMillis()))
				&& (tsEntryDate.isBefore(reportEndDate.getMillis()) || tsEntryDate
						.isEqual(reportEndDate.getMillis()))) {
			valid = true;
		}

		return valid;
	}

	private TemplateDocument getTimeSheetReportTemplate(String reportTypeCd) {
		// get the right template based on the report type
		TemplateDocument timeSheetReportTemplate = null;
		if (reportTypeCd
				.equals(TimesheetMonthlyReportTypeDef.EXEMPT_GROUPED_REPORT)) {
			timeSheetReportTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.EXEMPT_GROUPED_TIMESHEET);
		} else if (reportTypeCd
				.equals(TimesheetMonthlyReportTypeDef.NON_EXEMPT_GROUPED_REPORT)) {
			timeSheetReportTemplate = TemplateDocTypeDef
					.getTemplate(TemplateDocTypeDef.NON_EXEMPT_GROUPED_TIMESHEET);
		}

		return timeSheetReportTemplate;
	}
	
	private String getSectionDisplayCode(String sectionCd) {
		String sectionDisplayCode = null;

		if (TimesheetSectionTypeDef.isReportSeparately(sectionCd)) {
			sectionDisplayCode = TimesheetSectionTypeDef
					.getDisplayCode(sectionCd);
		}

		return sectionDisplayCode;
	}
	
	private boolean isGrouped(String functionCd) {
		return TimesheetFunctionTypeDef.isGrouped(functionCd);
	}
	
	private boolean isAddOverTimeRow(String functionCd) {
		return isNonExempt() && !isGrouped(functionCd);
	}
}
