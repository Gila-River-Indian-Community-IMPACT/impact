package us.oh.state.epa.stars2.webcommon.pdf.ceta;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StAttachment;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestVisitDate;
import us.oh.state.epa.stars2.database.dbObjects.ceta.TestedEmissionsUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.MultiEstabFacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.CetaStackTestAuditsDef;
import us.oh.state.epa.stars2.def.CetaStackTestCategoryDef;
import us.oh.state.epa.stars2.def.CetaStackTestMethodDef;
import us.oh.state.epa.stars2.def.CetaStackTestResultsDef;
import us.oh.state.epa.stars2.def.CetaStackTestTestingMethodDef;
import us.oh.state.epa.stars2.def.EmissionsTestStateDef;
import us.oh.state.epa.stars2.def.StAttachmentTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

@SuppressWarnings("serial")
public class EmissionsTestPdfGenerator extends PdfGeneratorBase {
	static Logger logger = Logger.getLogger(EmissionsTestPdfGenerator.class);
	protected static final DecimalFormat VALUE_FORMAT = new DecimalFormat(
			"#####0.0####");
	protected static String reportTypes = "";
	private float leftMargin = 10;
	private float rightMargin = 10;
	private float topMargin = 20;
	private float bottomMargin = 20;
	private Font data1Font = FontFactory
			.getFont(dataFontFamily, 8, Font.NORMAL);
	private Font data1FontRed = FontFactory.getFont(dataFontFamily, 8,
			Font.NORMAL, Color.RED);
	private Font normal1Font = FontFactory.getFont(defaultFontFamily, 8,
			Font.NORMAL);
	private Document document; // PDF document
	private StackTest test;
	private LinkedHashMap<String, String> contactTitles;
	@SuppressWarnings("unused")
	private MultiEstabFacilityList[] multiEstabFacilities;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
	private boolean hasTS = false;
	private final boolean internalApp = CompMgr.getAppName().equals(
			CommonConst.INTERNAL_APP);
	private final boolean publicApp = CompMgr.getAppName().equals(
			CommonConst.PUBLIC_APP);

	/**
	 * Constructor is protected to force users to call
	 * <code>getGeneratorForClass</code>
	 * 
	 */
	public EmissionsTestPdfGenerator() throws DocumentException {
		try {
			InfrastructureService infraBO = ServiceFactory.getInstance()
					.getInfrastructureService();

			SimpleDef[] tempDefs = infraBO.retrieveSimpleDefs("cm_title_def",
					"title_cd", "title_dsc", "deprecated", null);

			contactTitles = new LinkedHashMap<String, String>();
			for (SimpleDef tempDef : tempDefs) {
				contactTitles.put(tempDef.getCode(), tempDef.getDescription());
			}

		} catch (Exception e) {
			throw new DocumentException(e.getMessage());
		}

	}

	/**
	 * Generate a PDF file rendering the data within <code>TV and SMTV</code>.
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void generatePdf(Facility facility, StackTest test, OutputStream os,
			boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws IOException, DocumentException {
		document = new Document();
		PdfWriter.getInstance(document, os);

		document.setMargins(leftMargin, rightMargin, topMargin, bottomMargin);
		document.open();

		this.test = test;
		setSubmittedPDFDoc(isSubmittedPDFDoc);
		setIncludeAllAttachments(includeAllAttachments);
		setSubmitDate(test.getDateReceived());
		// add a title page
		String[] title = { "Stack Test " + test.getStckId(),
				facility.getName(), facility.getFacilityId() };
		
		Table titleTable = createTitleTable(title);
		titleTable.setAutoFillEmptyCells(false);
		document.add(titleTable);

		ListItem item1 = new ListItem();
		Table table = new Table(4);
		float[] widths = { 48, 48, 1, 1 };
		table.setWidths(widths);
		table.setBorder(0);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_CENTER);
		generate2Columns(
				table,
				"Scheduled Date:",
				test.getDateScheduled() != null ? dateFormat.format(test
						.getDateScheduled()) : "", false, null, null, false);
		// TO DO when we implement this for the portal, review/verify which
		// fields should not be visible.
		if (internalApp) {
			generate2Columns(
					table,
					"Date Results Received:",
					test.getDateReceived() != null ? dateFormat.format(test
							.getDateReceived()) : "", false, null, null, false);
			generate2Columns(
					table,
					"Date Results Reviewed:",
					test.getDateEvaluated() != null ? dateFormat.format(test
							.getDateEvaluated()) : "", false, null, null, false);
			generate2Columns(
					table,
					"Reviewer:",
					test.getReviewer() != null ? BasicUsersDef.getUserNm(test
							.getReviewer()) : "", false, null, null, false);
		}
		if (test.getFceId() != null) {
			generate2Columns(table, "Associated Inspection ID:", test
					.getFceId().toString(), false, null, null, false);
		}
		generate2Columns(table, "Company Conducting Test:",
				test.getCompany() == null ? "" : test.getCompany(), false,
				null, null, false);
		generate2Columns(table, "Audits:",
				test.getAuditsCd() != null ? CetaStackTestAuditsDef.getData()
						.getItems().getItemDesc(test.getAuditsCd()) : "",
				false, null, null, false);
		generate2Columns(table, "Category:",
				test.getCategoryCd() != null ? CetaStackTestCategoryDef
						.getData().getItems().getItemDesc(test.getCategoryCd())
						: "", false, null, null, false);
		generate2Columns(
				table,
				"Testing Method:",
				test.getTestingMethodCd() != null ? CetaStackTestTestingMethodDef
						.getData().getItems()
						.getItemDesc(test.getTestingMethodCd())
						: "", false, null, null, false);
		item1.add(table);
		document.add(item1);

		// Test Dates
		ListItem testDates = new ListItem();
		Table tds = generateTestDates(test.getVisitDates());
		tds.setAlignment(Element.ALIGN_CENTER);
		testDates.add(tds);
		document.add(testDates);

		if (!publicApp) {
			// Witnesses
			ListItem witnesses = new ListItem();
			Table wits = generateWitnesses(test.getWitnesses());
			wits.setAlignment(Element.ALIGN_CENTER);
			witnesses.add(wits);
			document.add(witnesses);
		}

		// print test-specific data
		String facilityLabel = facility.getName() + "("
				+ facility.getFacilityId() + ")";
		printReport(facilityLabel, hideTradeSecret);

		// close the document
		if (document != null) {
			document.close();
		}
	}

	private Table generateTestDates(java.util.List<TestVisitDate> testDates)
			throws DocumentException {
		Cell cell;

		Table table = new Table(1);
		table.setWidth(30);
		float[] widths = { 20 };
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Test Date(s)");
		table.addCell(cell);

		for (TestVisitDate ev : testDates) {
			cell = createCell(defaultLeading,
					dateFormat.format(ev.getTestDate()), data1Font);
			table.addCell(cell);
		}
		return table;
	}

	private Table generateWitnesses(java.util.List<Evaluator> witnesses)
			throws DocumentException {
		Cell cell;

		Table table = new Table(1);
		table.setWidth(30);
		float[] widths = { 20 };
		table.setWidths(widths);
		table.setPadding(2);

		if (test.getWitnesses() == null || test.getWitnesses().size() == 0) {
			cell = createCell("Not Witnessed");
			table.addCell(cell);
		} else {
			cell = createCell("Witness(es)");
			table.addCell(cell);

			for (Evaluator ev : witnesses) {
				cell = createCell(defaultLeading,
						BasicUsersDef.getUserNm(ev.getEvaluator()), data1Font);
				table.addCell(cell);
			}
		}
		return table;
	}

	private void printReport(String facilityLabel, boolean hideTradeSecret)
			throws IOException, DocumentException {
		// define footer - NOTE: footer must be added to document after
		// PdfWriter.getInstance and BEFORE document.open so footer will
		// appear on all pages (including first page)
		String one = facilityLabel;
		String two = "Stack Test: " + test.getStckId();
		declareFooter(one, two);
		document.setPageSize(PageSize.A4.rotate());
		document.setPageCount(0);
		document.newPage();

		Table titleTable = createTitleTable("Stack Test Detail", "");
		document.add(titleTable);

		List sections = new List(false, 20);

		// Stack Test Method
		sections.add(generateStackTestMethod(test.getStackTestMethodCd(),
				test.getConformedToTestMethod()));

		// EUs/SCCs Tested
		sections.add(generateEUsTested(test.getTestedEmissionsUnits()));

		// what used
		sections.add(generateWhatUsed(test.getControlEquipUsed(),
				test.getMonitoringEquip()));

		sections.add(generateTestResults(test.getTestedPollutants()));

		// Memo
		Table table;
		ListItem item2 = new ListItem();
		table = new Table(2);
		float[] widths2 = { 6, 94 };
		table.setWidths(widths2);
		table.setBorder(0);
		table.setPadding(2);
		generateColumnsWithBorder(table, "Memo:", test.getMemo() == null ? ""
				: test.getMemo(), false);
		item2.add(table);
		sections.add(item2);

		sections.add(generateAttachments(hideTradeSecret));

		if (internalApp) {
			sections.add(generateNotes());
		}

		document.add(sections);
	}

	private ListItem generateAttachments(boolean hideTradeSecret)
			throws IOException, DocumentException {
		/**
		 * Create table displaying information about documents.
		 * 
		 * @param documents
		 *            list of documents.
		 * @return
		 * @throws BadElementException
		 */

		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading, "Attachments",
				boldFont);
		item1.add(item1Title);

		int numCols = 6;
		if (hideTradeSecret) {
			numCols = 3;
		}

		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths_hideTradeScret = { 9, 27, 19 };
		float[] widths = { 9, 27, 19, 9, 9, 27 };

		if (hideTradeSecret) {
			table.setWidths(widths_hideTradeScret);
		} else {
			table.setWidths(widths);
		}

		table.addCell(createHeaderCell("Attachment ID"));
		table.addCell(createHeaderCell("Description"));
		table.addCell(createHeaderCell("Type"));
		if (!hideTradeSecret) {
			table.addCell(createHeaderCell("Public Document"));
			table.addCell(createHeaderCell("Trade Seceret Document"));
			table.addCell(createHeaderCell("Trade Secret Justification"));
		}
		java.util.List<StAttachment> attachments = new ArrayList<StAttachment>();
		attachments = test.getAttachments();
		if (attachments.size() == 0) {
			Cell cell = createCell("No attachments found");
			cell.setColspan(6);
			table.addCell(cell);
		} else {
			for (StAttachment attach : test.getAttachments()) {
				// don't include documents added after stack test was submitted
				if (isSubmittedPDFDoc() && test.getSubmittedDate() != null && attach.getLastModifiedTS() != null
						&& attach.getLastModifiedTS().after(test.getSubmittedDate())) {
					logger.debug("Excluding document "
							+ attach.getDocumentID()
							+ " from stack test PDF generator. Document last modified date ("
							+ attach.getLastModifiedTS()
							+ ") is after stack test submission date ("
							+ test.getSubmittedDate() + ")");
					continue;
				}
				
				// COL1 ->ID
				table.addCell(createDataCell(attach.getDocumentID().toString()));

				// COL2 ->Description
				table.addCell(createDataCell(attach.getDescription()));

				// COL3 ->Type
				table.addCell(createDataCell(StAttachmentTypeDef.getData()
						.getItems().getItemDesc(attach.getAttachmentType())));

				if (!hideTradeSecret) {
					table.addCell(createCheckCell(attach.getDocumentID() != null ? true
							: false));
					
					if (attach.getTradeSecretDocId() != null && (isIncludeAllAttachments() || (test.getSubmittedDate() != null && attach.getLastModifiedTS() != null
							&& attach.getLastModifiedTS().before(test.getSubmittedDate())) || attach.isTradeSecretAllowed())) {
						hasTS = true;
					}
					
					boolean ts = (attach.getTradeSecretDocId() != null && (isIncludeAllAttachments() || (isSubmittedPDFDoc() && test.getSubmittedDate() != null) || attach.isTradeSecretAllowed()))? true
							: false;
					table.addCell(createCheckCell(ts));
					
					String s = attach.getTradeSecretJustification();
					if (s == null)
						s = "";
					table.addCell(createCell((isIncludeAllAttachments() || (isSubmittedPDFDoc() && test.getSubmittedDate() != null) || attach.isTradeSecretAllowed())? s : ""));
				}
			}
		}
		item1.add(table);
		return item1;
	}

	protected ListItem generateNotes() throws DocumentException {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
		ListItem ret = new ListItem();
		ret.add(createItemTitle("Notes", ""));

		int numCols = 3;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1, 1, 2 };
		table.setWidths(widths);
		table.setAlignment(Element.ALIGN_LEFT);

		table.addCell(createHeaderCell("User Name"));
		table.addCell(createHeaderCell("Date"));
		table.addCell(createHeaderCell("Note"));

		if (test.getStackTestNotes() != null) {
			for (Note note : test.getStackTestNotes()) {
				table.addCell(createCell(BasicUsersDef.getUserNm(note
						.getUserId())));

				if (note.getDateEntered() != null) {
					table.addCell(createCell(dateFormat1.format(note
							.getDateEntered())));
				} else {
					table.addCell(createCell(""));
				}

				table.addCell(createCell(note.getNoteTxt()));
			}
		}

		ret.add(table);
		return ret;
	}

	private ListItem generateStackTestMethod(String method, String conformed)
			throws DocumentException {
		Cell cell;
		ListItem item1 = new ListItem();
		Table table = new Table(2);
		table.setWidth(100);
		float[] widths = { 50, 50 };
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Stack Test Method");
		table.addCell(cell);
		cell = createCell("Conformed to Test Method");
		table.addCell(cell);
		cell = createCell(defaultLeading,
				method == null ? "" : CetaStackTestMethodDef.getData()
						.getItems().getItemDesc(method), data1Font);
		table.addCell(cell);

		cell = createCell(defaultLeading, conformed == null ? "" : conformed,
				data1Font);
		table.addCell(cell);
		item1.add(table);
		return item1;
	}

	private ListItem generateEUsTested(java.util.List<TestedEmissionsUnit> eus)
			throws DocumentException {
		ListItem item1 = new ListItem();
		Cell cell;

		Phrase item1Title = new Paragraph(defaultLeading,
				"Emissions Units Tested/Processes Tested", boldFont);
		item1.add(item1Title);

		Table table = new Table(6);
		table.setWidth(100);
		float[] widths = { 7, 8, 10, 25, 25, 25 };
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("EU");
		table.addCell(cell);
		cell = createCell("SCC ID");
		table.addCell(cell);
		cell = createCell("EU Operating Status");
		table.addCell(cell);
		cell = createCell("Emissions Unit Description");
		table.addCell(cell);
		cell = createCell("Company Process Description");
		table.addCell(cell);
		cell = createCell("Associated Control Equipment");
		table.addCell(cell);

		for (TestedEmissionsUnit eu : eus) {
			cell = createCell(defaultLeading, eu.getEpaEmuId(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, eu.getSccs(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, eu.getOpStatus(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, eu.getDescription(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, eu.getProcessDescription(),
					data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, eu.getControlEquipment(),
					data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateWhatUsed(String control, String monitor)
			throws DocumentException {
		Cell cell;
		ListItem item1 = new ListItem();
		Table table = new Table(2);
		table.setWidth(100);
		float[] widths = { 50, 50 };
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Control Equipment Used");
		table.addCell(cell);
		cell = createCell("Monitoring Equipment Used");
		table.addCell(cell);

		cell = createCell(defaultLeading, control == null ? "" : control,
				data1Font);
		table.addCell(cell);

		cell = createCell(defaultLeading, monitor == null ? "" : monitor,
				data1Font);
		table.addCell(cell);
		item1.add(table);
		return item1;
	}

	private ListItem generateTestResults(StackTestedPollutant[] tested)
			throws DocumentException {
		ListItem item1 = new ListItem();
		Cell cell;

		Phrase item1Title = null;
		item1Title = new Paragraph(defaultLeading, "Stack Tests Results",
				boldFont);
		item1.add(item1Title);

		// CAP table
		Table table;

		table = new Table(9);
		table.setWidth(100);
		float[] widths = { 7, 7, 7, 12, 15, 15, 15, 15, 7 };
		table.setWidths(widths);

		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("EU");
		table.addCell(cell);
		cell = createCell("SCC ID");
		table.addCell(cell);
		cell = createCell("Code");
		table.addCell(cell);
		cell = createCell("Pollutant");
		table.addCell(cell);
		cell = createCell("Permitted Allowable Emissions Rate");
		table.addCell(cell);
		cell = createCell("Permitted Maximum Operating Rate");
		table.addCell(cell);
		cell = createCell("Tested Average Emission Rate");
		table.addCell(cell);
		cell = createCell("Tested Average Operating Rate");
		table.addCell(cell);
		cell = createCell("Results");
		table.addCell(cell);

		for (StackTestedPollutant stp : tested) {
			cell = createCell(defaultLeading, stp.getEpaEmuId(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, stp.getSccId(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, stp.getPollutantCd(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, stp.getPollutantDsc(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, stp.getPermitAllowRate(),
					data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, stp.getPermitMaxRate(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, stp.getTestRate(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, stp.getTestAvgOperRate(),
					data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, CetaStackTestResultsDef.getData()
					.getItems().getItemDesc(stp.getStackTestResultsCd()),
					data1Font);
			table.addCell(cell);
		}

		item1.add(table);
		return item1;
	}

	private void declareFooter(String one, String two) {
		HeaderFooter footer = new HeaderFooter(new Phrase(one
				+ "                          " + " Page ", normalFont),
				new Phrase("                                      " + two,
						normalFont));
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setBorder(0);
		document.setFooter(footer);
	}

	private void generateColumnsWithBorder(Table table, String name1,
			String value1, boolean attr1) {
		generate2ColumnsColorBorder(table, name1, value1, attr1, false);
	}

	private void generate2ColumnsColorBorder(Table table, String name1,
			String value1, boolean attr1, boolean red) {
		boolean val;
		Cell cell;
		Font font = data1Font;
		if (red) {
			font = data1FontRed;
		}

		cell = createCell(defaultLeading, name1, normal1Font);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);

		if (attr1) {
			val = value1.equals("true") ? true : false;
			cell = createCheckCell(val);
		} else {
			cell = createCell(defaultLeading, value1, font);
		}
		cell.setBorder(15);
		table.addCell(cell);
	}

	private void generate2Columns(Table table, String name1, String value1,
			boolean attr1, String name2, String value2, boolean attr2) {
		generate2ColumnsColor(table, name1, value1, attr1, false, name2,
				value2, attr2);
	}

	private void generate2ColumnsColor(Table table, String name1,
			String value1, boolean attr1, boolean red, String name2,
			String value2, boolean attr2) {
		boolean val;
		Cell cell;
		Font font = data1Font;
		if (red) {
			font = data1FontRed;
		}

		cell = createCell(defaultLeading, name1, normal1Font);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);

		if (attr1) {
			val = value1.equals("true") ? true : false;
			cell = createCheckCell(val);
		} else {
			cell = createCell(defaultLeading, value1, font);
		}
		cell.setBorder(0);

		if (name2 == null) {
			cell.setColspan(3);
		}
		table.addCell(cell);

		if (name2 != null) {
			cell = createCell(defaultLeading, name2, normal1Font);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setBorder(0);
			table.addCell(cell);

			if (attr2) {
				val = value2.equals("true") ? true : false;
				cell = createCheckCell(val);
			} else {
				cell = createCell(defaultLeading, value2, data1Font);
			}
			cell.setBorder(0);

			table.addCell(cell);
		}
	}

	public final float getBottomMargin() {
		return bottomMargin;
	}

	public final void setBottomMargin(float bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public final float getLeftMargin() {
		return leftMargin;
	}

	public final void setLeftMargin(float leftMargin) {
		this.leftMargin = leftMargin;
	}

	public final float getRightMargin() {
		return rightMargin;
	}

	public final void setRightMargin(float rightMargin) {
		this.rightMargin = rightMargin;
	}

	public final float getTopMargin() {
		return topMargin;
	}

	public final void setTopMargin(float topMargin) {
		this.topMargin = topMargin;
	}

	public boolean isHasTS() {
		return hasTS;
	}
}
