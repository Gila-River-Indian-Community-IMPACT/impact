package us.oh.state.epa.stars2.webcommon.pdf.compliance;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceDeviation;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.CompliancePerDetail;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportAttachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportLimit;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportMonitor;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.ComplianceReportAcceptedDef;
import us.oh.state.epa.stars2.def.ComplianceReportAllSubAttachmentTypesDef;
import us.oh.state.epa.stars2.def.ComplianceReportAllSubtypesDef;
import us.oh.state.epa.stars2.def.ComplianceReportMonitorAndLimitAuditStatusDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.ReportComplianceStatusDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;

@SuppressWarnings("serial")
public class ComplianceReportPdfGenerator extends PdfGeneratorBase {
	protected PdfWriter pdfWriter;
	protected Document document; // PDF document
	protected ComplianceReport complianceReport;

	protected float leftMargin = 10;
	protected float rightMargin = 10;
	protected float topMargin = 20;
	protected float bottomMargin = 20;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
	private final boolean internalApp = CompMgr.getAppName().equals(
			CommonConst.INTERNAL_APP);
	private final boolean publicApp = CompMgr.getAppName().equals(
			CommonConst.PUBLIC_APP);
	private boolean hasTS = false;
	private static Logger logger = Logger
			.getLogger(ComplianceReportPdfGenerator.class);
	
	static enum ReportCategory {QUARTERLY_CEM_COM, ANNUAL_RATA, OTHER};

	public ComplianceReportPdfGenerator() {
		super();
	}

	/*
	 * FOR TESTING ONLY
	 */
	// public static void main(String[] args) {
	//
	// String dirPath = "C:\\Projects\\Stars2";
	// // dirPath= "C:\\Documents and Settings\\dapc_user\\Desktop";
	// int reportId=5603;
	// try {
	// ComplianceReportService crBO =
	// ServiceFactory.getInstance().getComplianceReportService();
	// ComplianceReport cr=null;
	// try {
	// cr = crBO.retrieveComplianceReport(reportId);
	// } catch (Exception e) {}
	// if (cr != null) {
	// ComplianceReportPdfGenerator gen = new ComplianceReportPdfGenerator();
	// gen.setUserName("test");
	// FileOutputStream fos = new FileOutputStream(
	// dirPath + File.separator + cr.getReportId() +
	// "_"+System.currentTimeMillis()+".pdf");
	// gen.generatePdf(cr, fos);
	// fos.close();
	// } else {
	// System.out.println("No compliance report found");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	/**
	 * Generate a PDF file rendering the data within <code>app</code>.
	 * 
	 * @param app
	 *            application to be rendered.
	 * @param os
	 *            OutputStream to which file will be written.
	 * @param hideTradeSecret
	 *            flag indicating if trade secret information should be excluded
	 *            from the output generated.
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void generatePdf(ComplianceReport cr, OutputStream os,
			boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws ServiceFactoryException,
			IOException, DocumentException {
		complianceReport = cr;
		document = new Document(PageSize.A4.rotate());
		pdfWriter = PdfWriter.getInstance(document, os);
		setSubmitDate(cr.getSubmittedDate());
		generatePdf(cr, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
	}

	protected void generatePdf(ComplianceReport cr, boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments)
			throws IOException, DocumentException {
		complianceReport = cr;
		document.setMargins(leftMargin, rightMargin, topMargin, bottomMargin);
		setSubmitDate(cr.getSubmittedDate());
		setSubmittedPDFDoc(isSubmittedPDFDoc);
		setIncludeAllAttachments(includeAllAttachments);

		Timestamp docDate = cr.getSubmittedDate();
		if (docDate == null) {
			docDate = new Timestamp(System.currentTimeMillis());
		}

		String title = "";
		if (cr.getReportType().equalsIgnoreCase(
				ComplianceReportTypeDef.COMPLIANCE_TYPE_ONE)) {
			title = "One Time Reports";
		} else if (cr.getReportType().equalsIgnoreCase(
				ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS)) {
			title = "CEMS/COMS/RATA Reporting";
		} else if(cr.getReportType().equalsIgnoreCase(
				ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER)) {
			title = "Other Compliance Report";
		} else {
			title = "Generic Compliance Report";
		}

		// add a title page common to all applications
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		String[] titles = { title + " " + cr.getReportCRPTId(),
				cr.getFacilityNm(), cr.getFacilityId(),
				dateFormat.format(docDate) };
		Table titlesTable = createTitleTable(titles);
		titlesTable.setAutoFillEmptyCells(false);

		document.open();
		document.add(titlesTable);

		if (!isAttestationOnly()) {
			printComplianceReport(cr, hideTradeSecret);
		}

		// close the document
		if (document != null) {
			document.close();
		}
	}

	private void printComplianceReport(ComplianceReport cr,
			boolean hideTradeSecret) throws DocumentException, IOException {

		// define footer - NOTE: footer must be added to document after
		// PdfWriter.getInstance and BEFORE document.open so footer will
		// appear on all pages (including first page)
		String subtitle = "";
		if (cr.getReportType().equalsIgnoreCase(
				ComplianceReportTypeDef.COMPLIANCE_TYPE_ONE)) {
			subtitle = "One Time Reports";
		} else if (cr.getReportType().equalsIgnoreCase(
				ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS)) {
			subtitle = "CEMS/COMS/RATA Reporting";
		} else if(cr.getReportType().equalsIgnoreCase(
				ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER)){
			subtitle = "Other Compliance Report";
		} else {
			subtitle = "Generic Compliance Report";
		}
		HeaderFooter footer = new HeaderFooter(new Phrase(
				"Air Quality Division" + "                          "
						+ " Page ", normalFont), new Phrase(
				"                                      " + subtitle + " "
						+ cr.getReportCRPTId(), normalFont));
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setBorder(0);
		document.setFooter(footer);
		document.setPageCount(0);
		document.newPage();

		// add a title to the document
		String title = "Air Quality Division";
		Table titleTable = createTitleTable(title, subtitle);
		document.add(titleTable);

		// add text explaining application document
		Table appExplTable = createTextTable(defaultLeading, "", italicFont);
		appExplTable.setPadding(5);
		appExplTable.setWidth(100);
		document.add(appExplTable);

		List sections = new List(false, 20);

		document.add(printHeader(cr));

		// add "For OEPA use only" table IFF not coming through Portal
		if (internalApp) {
			sections.add(printOEPASection(cr));
		}
		
		sections.add(printReportCategory(cr));
		
		// add monitors and limits
		if(cr.isSecondGenerationCemComRataRpt() && !cr.isLegacyFlag()) {
			sections.add(generateMonitorsAndLimits(cr));
		}
		
		sections.add(printDescriptionOther(cr));

		sections.add(printAttachments(cr, hideTradeSecret));

		if (internalApp) {
			sections.add(generateNotes(cr.getNotes()));
		}

		if (cr.getTargetFacilities().length > 0) {
			sections.add(printAdditionalFacilities(cr.getTargetFacilities()));
		}
		
		document.add(sections);
	}

	private Object printAdditionalFacilities(FacilityList[] targetFacilities)
				throws IOException, DocumentException {
		ListItem item1 = new ListItem();
	
		Phrase item1Title = new Paragraph(defaultLeading, "Additional Facilities",
				boldFont);
		item1.add(item1Title);
	
		int numCols = 7;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 8, 24, 16, 8, 24, 16, 24 };
		table.setWidths(widths);
	
	
		table.addCell(createHeaderCell("Facility ID"));
		table.addCell(createHeaderCell("Facility Name"));
		table.addCell(createHeaderCell("Operating Status"));
		table.addCell(createHeaderCell("Facility Class"));
		table.addCell(createHeaderCell("Facility Type"));
		table.addCell(createHeaderCell("County"));
		table.addCell(createHeaderCell("Lat/Long"));
		if (targetFacilities.length == 0) {
			Cell cell = createCell("No facilities found");
			cell.setColspan(7);
			table.addCell(cell);
		} else {
			for (FacilityList f : targetFacilities) {
	
				// COL1 ->ID
				table.addCell(createDataCell(f.getFacilityId().toString()));
	
				// COL2 ->Description
				table.addCell(createDataCell(f.getName()));
	
				// COL3 ->Type
				table.addCell(createDataCell(OperatingStatusDef
						.getData().getItems()
						.getItemDesc(f.getOperatingStatusCd())));
	
				// COL4 ->Type
				table.addCell(createDataCell(PermitClassDef
						.getData().getItems()
						.getItemDesc(f.getPermitClassCd())));
	
	
	
				// COL5 ->Type
				table.addCell(createDataCell(FacilityTypeDef
						.getTextData().getItems()
						.getItemDesc(f.getFacilityTypeCd())));
	
				// COL6 ->Type
				table.addCell(createDataCell(null == f.getCountyName()? "" : f.getCountyName()));
	
				// COL7 ->Type
				table.addCell(createDataCell(null == f.getLatLong()? "" : f.getLatLong()));
			}
		}
		item1.add(table);
		return item1;
	}

	private ListItem printOEPASection(ComplianceReport cr) throws IOException,
			DocumentException {
		ListItem item1 = new ListItem();
		Phrase item1Title = new Paragraph(defaultLeading, "AQD Staff", boldFont);
		item1.add(item1Title);

		Table table = new Table(6);
		table.setBorder(Rectangle.NO_BORDER);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1, 1, 1, 1, 1, 1 };
		table.setWidths(widths);

		InfrastructureDefs infraDefs;
		try {
			infraDefs = (InfrastructureDefs) FacesUtil
					.getManagedBean("infraDefs");
		} catch (Exception e) {
			infraDefs = new InfrastructureDefs();
		}

		// ROW 1
		table.addCell(createHeaderCellLabel("Received On:"));
		if (cr.getReceivedDate() != null) {
			table.addCell(createHeaderCellValue(dateFormat.format(cr
					.getReceivedDate())));
		} else {
			table.addCell(createHeaderCellValue(""));
		}

		table.addCell(createHeaderCellLabel("Reviewed By:"));
		table.addCell(createHeaderCellValue(BasicUsersDef.getUserNm(cr
				.getDapcReviewer())));

		table.addCell(createHeaderCellLabel("Permit Number:"));
		table.addCell(createHeaderCellValue(cr.getPermitNumber()));

		// ROW 2
		table.addCell(createHeaderCellLabel("Reviewed On:"));
		if (cr.getDapcDateReviewed() != null) {
			table.addCell(createHeaderCellValue(dateFormat.format(cr
					.getDapcDateReviewed())));
		} else {
			table.addCell(createHeaderCellValue(""));
		}

		table.addCell(createHeaderCellLabel("Report Accepted:"));
		table.addCell(createHeaderCellValue(ComplianceReportAcceptedDef
				.getData().getItems().getItemDesc(cr.getDapcAccepted())));

		table.addCell(createHeaderCellLabel("Compliance Status:"));
		table.addCell(createHeaderCellValue(ReportComplianceStatusDef.getData()
				.getItems().getItemDesc(cr.getComplianceStatusCd())));

		// ROW 3
		table.addCell(createHeaderCellLabel("Comments:"));
		Cell cell2 = createCell(cr.getDapcReviewComments());
		cell2.setBorder(Rectangle.NO_BORDER);
		cell2.setColspan(5);
		table.addCell(cell2);

		item1.add(table);
		return item1;
	}

	private ListItem printAttachments(ComplianceReport cr,
			boolean hideTradeSecret) throws IOException, DocumentException {
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
		ComplianceReportAttachment attachment[] = cr.getAttachments();
		if (attachment.length == 0) {
			Cell cell = createCell("No attachments found");
			cell.setColspan(6);
			table.addCell(cell);
		} else {
			for (ComplianceReportAttachment doc : attachment) {
				// don't include documents added after compliance report was submitted
				if (isSubmittedPDFDoc() && getSubmitDate() != null && doc.getLastModifiedTS() != null
						&& doc.getLastModifiedTS().after(getSubmitDate())) {
					logger.debug("Excluding document "
							+ doc.getDocumentID()
							+ " from compliance report PDF generator. Document last modified date ("
							+ doc.getLastModifiedTS()
							+ ") is after compliance report submission date ("
							+ getSubmitDate() + ")");
					continue;
				}
				
				// COL1 ->ID
				table.addCell(createDataCell(doc.getDocumentID().toString()));

				// COL2 ->Description
				table.addCell(createDataCell(doc.getDescription()));

				// COL3 ->Type
				table.addCell(createDataCell(ComplianceReportAllSubAttachmentTypesDef
						.getData().getItems()
						.getItemDesc(doc.getAttachmentType())));

				if (!hideTradeSecret) {
					table.addCell(createCheckCell(doc.getDocumentID() != null ? true : false));
					
					if (doc.getTradeSecretDocId() != null && (isIncludeAllAttachments() || (getSubmitDate() != null && doc.getLastModifiedTS() != null
							&& doc.getLastModifiedTS().before(getSubmitDate())) || doc.isTradeSecretAllowed())) {
						hasTS = true;
					}

					boolean ts = (doc.getTradeSecretDocId() != null && (isIncludeAllAttachments() || (isSubmittedPDFDoc() && getSubmitDate() != null) || doc.isTradeSecretAllowed()))? true : false;
					table.addCell(createCheckCell(ts));
					String s = doc.getTradeSecretJustification();
					if (s == null) s = "";
					table.addCell(createCell((isIncludeAllAttachments() || (isSubmittedPDFDoc() && getSubmitDate() != null) || doc.isTradeSecretAllowed())? s : ""));
				}
			}
		}
		item1.add(table);
		return item1;
	}

	private Table printHeader(ComplianceReport cr) throws IOException,
			DocumentException {
		Table table = new Table(6);
		table.setBorder(Rectangle.NO_BORDER);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1, 1, 1, 1, 1, 1 };
		table.setWidths(widths);

		table.addCell(createHeaderCellLabel("Facility ID:"));
		table.addCell(createHeaderCellValue(cr.getFacilityId()));

		table.addCell(createHeaderCellLabel("Report ID:"));
		table.addCell(createHeaderCellValue(cr.getReportCRPTId()));

		if (cr.getSubmittedDate() != null) {
			table.addCell(createHeaderCellLabel("Submitted Date:"));
			table.addCell(createHeaderCellValue(dateFormat.format(cr
					.getSubmittedDate())));
		}

		table.addCell(createHeaderCellLabel("Facility Name:"));
		table.addCell(createHeaderCellValue(cr.getFacilityNm()));

		table.addCell(createHeaderCellLabel("Report Type:"));
		table.addCell(createHeaderCellValue(ComplianceReportTypeDef.getData()
				.getItems().getItemDesc(cr.getReportType())));

		if (cr.getSubmittedDate() != null && !publicApp) {
			table.addCell(createHeaderCellLabel(""));
			table.addCell(createHeaderCellLabel(""));

			table.addCell(createHeaderCellLabel("Entered by:"));
			table.addCell(createHeaderCellValue(cr.getSubmitValue()));

		}

		return table;
	}

	private ListItem printDescriptionTV(ComplianceReport cr)
			throws IOException, DocumentException {
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(
				defaultLeading,
				"Any Material Information Not Established Through the Applicable Permit Terms and Conditions That May Indicate Non-Compliance",
				boldFont);
		item1.add(item1Title);
		Phrase subTitle = new Paragraph(
				defaultLeading,
				"For each peice of material information, identify the emissions unit or briefly describe the requirement and then provide a description of the material information. For insignificant emissions units, include the permit number or SIP-based applicable requirement rule reference. If you are attaching a Title V Compliance Certification form that includes this information you do not need to duplicate it here.",
				normalFont);
		item1.add(subTitle);
		int numCols = 1;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1 };
		table.setWidths(widths);
		table.addCell(createHeaderCellValue(cr.getComments()));
		item1.add(table);
		return item1;
	}

	private ListItem printDescriptionPER(ComplianceReport cr)
			throws IOException, DocumentException {
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Additional Information & Corrections", boldFont);
		item1.add(item1Title);
		Phrase subTitle = new Paragraph(
				defaultLeading,
				"Please list below any additional information you need to communicate. At a minimum, identify if you have any EU(s) that were permanently shutdown, EU(s) that will not be installed or modified, or EU(s) not in operation during the reporting period. See the 'PER Form FAQ' document for an explanation and examples from the system's Reference Page.",
				normalFont);
		item1.add(subTitle);
		int numCols = 1;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1 };
		table.setWidths(widths);
		table.addCell(createHeaderCellValue(cr.getComments()));
		item1.add(table);
		return item1;
	}

	private ListItem printDescriptionOther(ComplianceReport cr)
			throws IOException, DocumentException {
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Description, Reporting Period and/or Date(s)", boldFont);
		item1.add(item1Title);
		Phrase subTitle = new Paragraph(
				defaultLeading,
				"Enter the reporting period and due date if applicable. Also summarize the contents of the attached compliance report, including the test date, notification date, and any notable issues. Attach the compliance report below.",
				normalFont);
		item1.add(subTitle);
		int numCols = 1;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1 };
		table.setWidths(widths);
		table.addCell(createHeaderCellValue(cr.getComments()));
		item1.add(table);
		return item1;
	}
	
	private ListItem printReportCategory(ComplianceReport cr)
			throws IOException, DocumentException {

		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading, "Report Category",
				boldFont);
		item1.add(item1Title);
		int numCols = 6;
		Table table = new Table(numCols);

		table.setBorder(Rectangle.NO_BORDER);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1, 1, 1, 1, 1, 1 };
		table.setWidths(widths);

		table.addCell(createHeaderCellLabel("Category:"));
		String dsc = "";
		if (cr.getOtherCategoryCd() != null) {
			dsc = ComplianceReportAllSubtypesDef.getData().getItems()
					.getItemDesc(cr.getOtherCategoryCd());
		}
		table.addCell(createHeaderCellValue(dsc));

		// If CEM/COM Quarterly Report or Rata Report, print year and quarter.
		if (cr.isCemsComsRataRpt()) {

			table.addCell(createHeaderCellLabel("Report Year:"));
			if(null != cr.getReportYear()) {
				table.addCell(createHeaderCellValue(cr.getReportYear().toString()));
			}

			table.addCell(createHeaderCellLabel("Report Quarter:"));
			if(null !=cr.getReportQuarter()) {
				table.addCell(createHeaderCellValue(cr.getReportQuarter()
						.toString()));
			}
		}

		item1.add(table);
		return item1;
	}

	private ListItem printPERRecords(ComplianceReport cr) throws IOException,
			DocumentException {
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Detailed EU Information", boldFont);
		item1.add(item1Title);
		Phrase subTitle = new Paragraph(
				defaultLeading,
				"The Emission Unit(s) listed below has been issued a PTIO(s). Complete the table below by identifying whether deviations or exceedances of operational restrictions(OR)/emission limitations(EL) and/or monitoring, record keeping or reporting (MRR) occured by selecting either Yes or No. If any of the date fields do not identify an actual date, you are required to update your Facility Inventory for each applicable emissions unit(s).",
				normalFont);
		item1.add(subTitle);
		int numCols = 9;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1, 3, 2, 2, 2, 2, 1, 1, 6 };
		table.setWidths(widths);

		table.addCell(createHeaderCell(""));
		table.addCell(createHeaderCell(""));
		table.addCell(createHeaderCell(""));
		Cell cell = createHeaderCell("Dates");
		cell.setColspan(3);
		table.addCell(cell);
		Cell cell2 = createHeaderCell("Deviations or Exceedances From:");
		cell2.setColspan(2);
		table.addCell(cell2);
		table.addCell(createHeaderCell(""));

		table.addCell(createHeaderCell("AQD EU ID"));
		table.addCell(createHeaderCell("AQD EU Description"));
		table.addCell(createHeaderCell("Permit Number: Effective Date"));
		table.addCell(createHeaderCell("Completion of Initial Installation"));
		table.addCell(createHeaderCell("Begin Installation/ Modification"));
		table.addCell(createHeaderCell("Commence Operation after Installation or Latest Modification"));
		table.addCell(createHeaderCell("OR/EL"));
		table.addCell(createHeaderCell("MRR"));
		table.addCell(createHeaderCell("Notes"));

		CompliancePerDetail perDetails[] = cr.getPerDetails();
		if (perDetails.length == 0) {
			Cell cell3 = createCell("No records found");
			cell3.setColspan(9);
			table.addCell(cell3);
		} else {
			for (CompliancePerDetail detail : perDetails) {
				// COL1 ->OEPA ID
				table.addCell(createDataCell(detail.getEpaEmuId()));

				// COL2 ->AQD EU Description
				table.addCell(createDataCell(detail.getCompanyId()));

				// COL3 ->permit #
				String permitInfo = detail.getPermitInfo();
				if (permitInfo != null) {
					permitInfo = permitInfo.replaceAll("; ", "\n");
				}
				table.addCell(createDataCell(permitInfo));

				// COL4 ->Completion of initial install
				if (detail.getInitialInstallComplete() != null) {
					table.addCell(createDataCell(dateFormat.format(detail
							.getInitialInstallComplete())));
				} else {
					table.addCell(createDataCell(""));
				}

				// COL5 ->Begin Install/Modification
				if (detail.getModificationBegun() != null) {
					table.addCell(createDataCell(dateFormat.format(detail
							.getModificationBegun())));
				} else {
					table.addCell(createDataCell(""));
				}

				// COL6 ->Commence Operation
				if (detail.getCommencedOperation() != null) {
					table.addCell(createDataCell(dateFormat.format(detail
							.getCommencedOperation())));
				} else {
					table.addCell(createDataCell(""));
				}
				// COL7 ->OR/EL
				table.addCell(createDataCell(detail.getDeviations()));

				// COL8 ->MRR Deviations
				table.addCell(createDataCell(detail.getDeviationsFromMRR()));

				// COL9 ->Notes
				table.addCell(createDataCell(detail.getComment()));
			}
		}
		item1.add(table);
		return item1;
	}

	private ListItem printDeviationsTV(ComplianceReport cr) throws IOException,
			DocumentException {
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Identification of Intermittent Compliance (IC)", boldFont);
		item1.add(item1Title);
		Phrase subTitle = new Paragraph(
				defaultLeading,
				"Intermittent compliance may be identified through either the following table or by attaching the information below. When attaching a document in lieu of using the table below, the document must meet the required format and content requirements for annual certifications. You can download a 'Title V Compliance Certification' form that meets these requirements along with instructions and examples from the system's Reference Page. Except as indicated in this section, the Material Information Section below, or any attachments submitted in lieu of using this sections, submittal of this report shall indicate all emissions units subject to one or more applicable requirements operated in continuous compliance with all federally enforceable permit terms and conditions throughout the reporting period identified above.",
				normalFont);
		item1.add(subTitle);
		int numCols = 6;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1, 2, 2, 4, 2, 4 };
		table.setWidths(widths);

		table.addCell(createHeaderCell(""));
		table.addCell(createHeaderCell(""));
		table.addCell(createHeaderCell(""));
		table.addCell(createHeaderCell(""));
		Cell cellx = createHeaderCell("Excursions/Deviations (one of the following must be provided)");
		cellx.setColspan(2);
		table.addCell(cellx);

		table.addCell(createHeaderCell("IC ID"));
		table.addCell(createHeaderCell("EU ID"));
		table.addCell(createHeaderCell("Emission Limitation/Control Measure or Permit Term No."));
		table.addCell(createHeaderCell("Compliance Method"));
		table.addCell(createHeaderCell("Report Date of Those Documented Within Excursion/Deviation Reports Submitted to DO/LAA"));
		table.addCell(createHeaderCell("Explain the Date, Nature, Duration, and Probable Cause of the Excursion/Deviation, as well as any Corrective Action Taken"));

		ComplianceDeviation deviations[] = cr.getDeviationReports();
		if (deviations.length == 0) {
			Cell cell = createCell("No records found");
			cell.setColspan(6);
			table.addCell(cell);
		} else {
			for (ComplianceDeviation dev : deviations) {
				// COL1 ->ID
				table.addCell(createDataCell(Integer.toString(dev
						.getDeviationId())));

				// COL2 ->Identifier
				table.addCell(createDataCell(dev.getIdentifier()));

				// COL3 ->Compliance Method
				table.addCell(createDataCell(dev.getPerDescription()));

				// COL4 ->Cause
				table.addCell(createDataCell(dev.getTvccComplianceMethod()));

				// COL5 ->Start Date
				if (dev.getStartDate() != null) {
					table.addCell(createDataCell(dateFormat.format(dev
							.getStartDate())));
				} else {
					table.addCell(createDataCell(""));
				}
				// COL6 ->Corrective Action
				table.addCell(createDataCell(dev.getTvccExcursionsSubmitted()));
			}
		}
		item1.add(table);
		return item1;
	}

	private ListItem printDeviationsPER(ComplianceReport cr)
			throws IOException, DocumentException {
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Deviation, Exceedance, or Visible Emissions Detail", boldFont);
		item1.add(item1Title);
		Phrase subTitle = new Paragraph(
				defaultLeading,
				"Additional information is required for each deviation or exceedance that prompted a 'yes' answer in the Detailed EU Information table above and for any visible emissions (VE) incident that occured during the reporting period. This information may be identified through either the following table or by attaching the information below. When attaching a document in lieu of using the table below, the document must meet the content requirements identified in the hard copy PER you received. Examples are available in the 'PER FAQs' document available from the system's Reference Page.",
				normalFont);
		item1.add(subTitle);
		int numCols = 7;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1, 1, 1, 1, 1, 1, 1 };
		table.setWidths(widths);

		table.addCell(createHeaderCell("Dev/Exc/VE ID"));
		table.addCell(createHeaderCell("AQD EU ID"));
		table.addCell(createHeaderCell("Start Date"));
		table.addCell(createHeaderCell("End Date"));
		table.addCell(createHeaderCell("Duration"));
		table.addCell(createHeaderCell("Description of Deviation or Exceedance and Probable Cause"));
		table.addCell(createHeaderCell("Description of Corrective Actions. If none, describe why not"));

		ComplianceDeviation deviations[] = cr.getDeviationReports();
		if (deviations.length == 0) {
			Cell cell = createCell("No records found");
			cell.setColspan(7);
			table.addCell(cell);
		} else {
			for (ComplianceDeviation dev : deviations) {
				// COL1 ->ID
				table.addCell(createDataCell(Integer.toString(dev
						.getDeviationId())));

				// COL2 ->Identifier
				table.addCell(createDataCell(dev.getIdentifier()));

				// COL3 ->Start Date
				if (dev.getStartDate() != null) {
					table.addCell(createDataCell(dateFormat.format(dev
							.getStartDate())));
				} else {
					table.addCell(createDataCell(""));
				}
				// COL4 ->End Date
				if (dev.getEndDate() != null) {
					table.addCell(createDataCell(dateFormat.format(dev
							.getEndDate())));
				} else {
					table.addCell(createDataCell(""));
				}
				// COL5 ->Compliance Method
				table.addCell(createDataCell(dev.getTvccComplianceMethod()));

				// COL6 ->Describe Deviation Modified
				table.addCell(createDataCell(dev.getPerDescription()));

				// COL7 ->Cause
				table.addCell(createDataCell(dev.getPerProbableCause()));

				// COL8 ->Corrective Action
				table.addCell(createDataCell(dev.getPerCorrectiveAction()));
			}
		}
		item1.add(table);
		return item1;
	}

	protected Paragraph getCertificationLanguage() throws DocumentException {
		String str = "";
		FacilityService facilityBO;
		try {
			facilityBO = ServiceFactory.getInstance().getFacilityService();
			Facility facility = facilityBO.retrieveFacility(complianceReport
					.getFacilityId());
			if (facility != null) {
				ComplianceReportService crBO = ServiceFactory.getInstance()
						.getComplianceReportService();
				str = crBO.getDAPCAttestationMessage(complianceReport,
						facility.getPermitClassCd());
			} else {
				logger.error("No facility data found for facility id : "
						+ complianceReport.getFacilityId()
						+ ". Using default attestation message.");
			}
		} catch (ServiceFactoryException e) {
			logger.error("Exception while geting certification language", e);
		} catch (RemoteException e) {
			logger.error("Exception while geting certification language", e);
		}

		int noteIdx = str.indexOf("Note:");
		if (noteIdx > 0) {
			certificationNote = str.substring(noteIdx);
			str = str.substring(0, noteIdx);
		}

		// format the certification language better
		Paragraph attestation = new Paragraph(defaultLeading,
				formatCertificationLanguage(str), smallerBold);

		return attestation;
	}

	public final ComplianceReport getComplianceReport() {
		return complianceReport;
	}

	public final void setComplianceReport(ComplianceReport complianceReport) {
		this.complianceReport = complianceReport;
	}

	public boolean isHasTS() {
		return hasTS;
	}

	protected ListItem generateNotes(Note[] crNotes) throws DocumentException {
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

		if (crNotes != null) {
			for (Note note : crNotes) {
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
	
	private Object generateMonitorsAndLimits(ComplianceReport cr)
			throws IOException, DocumentException {
		
		ListItem monitorsAndLimits = new ListItem();
		ReportCategory rptCategory;
		Phrase sectionTitle;

		if (cr.isSecondGenerationQuarterlyCemComRpt()) {
			rptCategory = ReportCategory.QUARTERLY_CEM_COM;
			sectionTitle = new Paragraph(defaultLeading,
					"CEM/COM/CMS Monitor Audits and Limits", boldFont);
		} else if (cr.isSecondGenerationAnnualRATARpt()) {
			rptCategory = ReportCategory.ANNUAL_RATA;
			sectionTitle = new Paragraph(defaultLeading,
					"CEM/COM/CMS Monitor Certifications and Limits", boldFont);
		} else {
			rptCategory = ReportCategory.OTHER;
			throw new DocumentException(
					"Invalid report category. Compliance report category should be either Quarterly CEM/COM or Annual RATA");
		}

		monitorsAndLimits.add(sectionTitle);

		List monitors = new List(false, 20);
		for (ComplianceReportMonitor crm : cr.getCompReportMonitorList()) {
			monitors.add(generateMonitor(crm, rptCategory));
		}
		monitorsAndLimits.add(monitors);

		return monitorsAndLimits;
	}

	private Object generateMonitor(ComplianceReportMonitor crm,
			ReportCategory reportCategory) throws IOException,
			DocumentException {
		
		ListItem monitor = new ListItem();

		Phrase sectionTitle = new Paragraph(defaultLeading, crm.getMonId(),
				boldFont);
		monitor.add(sectionTitle);

		int numCols;
		float[] widths;

		if (reportCategory == ReportCategory.QUARTERLY_CEM_COM) {
			numCols = 8;
			widths = new float[] {10, 45, 10, 10, 10, 10, 10, 5};
		} else if (reportCategory == ReportCategory.ANNUAL_RATA) {
			numCols = 6;
			widths = new float[] {10, 45, 10, 10, 10, 5};
		} else {
			logger.debug("Compliance report category is neither Quarterly CEM/COM or Annual RATA");
			throw new DocumentException(
					"Invalid report category. Compliance report category should be either Quarterly CEM/COM or Annual RATA");
		}

		Table table = new Table(numCols);

		table.setBorder(Rectangle.NO_BORDER);
		table.setWidth(98);
		table.setPadding(2);
		table.setWidths(widths);

		table.addCell(createHeaderCellLabel("Monitor Details:"));
		table.addCell(createHeaderCellValue(crm.getMonitorDetails()));

		if (reportCategory == ReportCategory.QUARTERLY_CEM_COM) {
			table.addCell(createHeaderCellLabel("QA/QC Accepted Date:"));
			table.addCell(createHeaderCellValue(formatDate(crm
					.getCurrentQAQCAcceptedDate())));

			table.addCell(createHeaderCellLabel("Audit Date:"));
			table.addCell(createHeaderCellValue(formatDate(crm.getTestDate())));

			table.addCell(createHeaderCellLabel("Audit Result:"));
			table.addCell(createHeaderCellValue(ComplianceReportMonitorAndLimitAuditStatusDef
					.getData().getItems().getItemDesc(crm.getAuditStatus())));
		} else if (reportCategory == ReportCategory.ANNUAL_RATA) {
			// Annual RATA report
			table.addCell(createHeaderCellLabel("Test Date:"));
			table.addCell(createHeaderCellValue(formatDate(crm.getTestDate())));

			table.addCell(createHeaderCellLabel("This is a Certification:"));
			table.addCell(createHeaderCellValue(crm.isCertificationFlag() ? "Yes"
					: "No"));
		}

		monitor.add(table);

		List limits = new List(false, 20);
		limits.add(generateLimit((ArrayList<ComplianceReportLimit>) crm
				.getComplianceReportLimitList(), reportCategory));
		monitor.add(limits);
		
		return monitor;
	}

	private Object generateLimit(ArrayList<ComplianceReportLimit> limits,
			ReportCategory reportCategory) throws IOException,
			DocumentException {

		ListItem limit = new ListItem();

		int numCols;
		float[] widths;

		if (reportCategory == ReportCategory.QUARTERLY_CEM_COM) {
			numCols = 4;
			widths = new float[] {5, 5, 50, 10};
		} else if (reportCategory == ReportCategory.ANNUAL_RATA) {
			numCols = 5;
			widths = new float[] {5, 5, 50, 10, 5};
		} else {
			logger.debug("Compliance report category is neither Quarterly CEM/COM or Annual RATA");
			throw new DocumentException(
					"Invalid report category. Compliance report category should be either Quarterly CEM/COM or Annual RATA");
		}

		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		table.setWidths(widths);

		table.addCell(createHeaderCell("Included in Report"));
		table.addCell(createHeaderCell("Limit"));
		table.addCell(createHeaderCell("Limit Description"));
		table.addCell(createHeaderCell("End Monitoring Limit"));
		if (reportCategory == ReportCategory.ANNUAL_RATA) {
			table.addCell(createHeaderCell("RATA Result"));
		}

		for (ComplianceReportLimit crl : limits) {
			table.addCell(createDataCell(crl.isIncludedFlag() ? "Yes" : "No"));
			table.addCell(createDataCell(crl.getLimId()));
			table.addCell(createDataCell(crl.getLimitDesc()));
			table.addCell(createDataCell(formatDate(crl.getEndDate())));
			if (reportCategory == ReportCategory.ANNUAL_RATA) {
				table.addCell(createDataCell(ComplianceReportMonitorAndLimitAuditStatusDef
						.getData().getItems().getItemDesc(crl.getLimitStatus())));
			}
		}

		limit.add(table);

		return limit;
	}
}
