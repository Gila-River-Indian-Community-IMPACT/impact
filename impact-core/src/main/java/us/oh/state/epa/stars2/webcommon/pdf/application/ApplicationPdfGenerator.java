package us.oh.state.epa.stars2.webcommon.pdf.application;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationNote;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.TIVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.ApplicationType;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.TVApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;

@SuppressWarnings("serial")
public abstract class ApplicationPdfGenerator extends PdfGeneratorBase {

	protected DecimalFormat capValueFormat = new DecimalFormat("###,##0.##");
	protected DecimalFormat hapValueFormat = new DecimalFormat("###.######");
	protected static SimpleDateFormat dateFormat1 = new SimpleDateFormat(
			"MM/dd/yyyy");
	protected static DecimalFormat VALUE_FORMAT = new DecimalFormat(
			"###,###,###,###,###,##0.00");
	protected static final DecimalFormat DFLT_EMISSIONS_VALUE_FORMAT = new DecimalFormat(
			"###,###,###,###,###,###.########");
	protected static final DecimalFormat EMISSIONS_VALUE_FORMAT_HAP = new DecimalFormat(
			"###,###,###,###,###.########");

	protected float leftMargin = 10;
	protected float rightMargin = 10;
	protected float topMargin = 20;
	protected float bottomMargin = 20;

	protected Font tradeSecretFont = FontFactory.getFont(dataFontFamily,
			defaultFontSize, Font.NORMAL);
	protected Color tradeSecretIndicatorColor = Color.RED;

	protected boolean hideTradeSecret;
	protected boolean containsTradeSecret = false;

	protected PdfWriter pdfWriter;
	protected Document document; // PDF document

	protected Application application;

	protected static Logger logger = Logger
			.getLogger(ApplicationPdfGenerator.class);

	/**
	 * Constructor is protected to force users to call
	 * <code>getGeneratorForClass</code>
	 * 
	 */
	protected ApplicationPdfGenerator() {
		tradeSecretFont.setColor(tradeSecretIndicatorColor);
	}

	/**
	 * Retrieve the appropriate subclass to generate the type of application
	 * specified by <code>appClass</code>
	 * 
	 * @param appClass
	 *            class of application to be rendered.
	 * @return
	 */
	public static ApplicationPdfGenerator getGeneratorForClass(
			Class<? extends Application> appClass) {
		ApplicationPdfGenerator result = null;
		if (appClass.equals(PTIOApplication.class)) {
			result = new PTIOApplicationPdfGenerator();
		} else if (appClass.equals(TVApplication.class)) {
			result = new TVApplicationPdfGenerator();
		} else if (appClass.equals(PBRNotification.class)) {
			result = new PBRNotificationPdfGenerator();
		} else if (appClass.equals(RPCRequest.class)) {
			result = new RPCRequestPdfGenerator();
		} else if (appClass.equals(TIVApplication.class)) {
			result = new TIVApplicationPdfGenerator();
		}
		return result;
	}

	/**
	 * Generate a PDF file rendering the data within <code>app</code>.
	 * 
	 * @param app
	 *            application to be rendered
	 * @param documentDir
	 *            directory in which PDF files will be created.
	 * @param hideTradeSecret
	 *            flag indicating if trade secret information should be excluded
	 *            from the output generated.
	 * @throws IOException
	 * @throws DocumentException
	 */
	public File generatePdf(Application app, String documentDir,
			boolean hideTradeSecret) throws IOException, DocumentException {
		document = new Document();
		File generatedFile = new File(documentDir, app.getApplicationNumber()
				+ ".pdf");
		pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(
				generatedFile));

		generatePdf(app, hideTradeSecret, false, true);

		return generatedFile;
	}

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
	public void generatePdf(Application app, OutputStream os,
			boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws IOException, DocumentException {
		document = new Document();
		pdfWriter = PdfWriter.getInstance(document, os);

		generatePdf(app, hideTradeSecret, isSubmittedPDFDoc, includeAllAttachments);
	}

	protected void generatePdf(Application app, boolean hideTradeSecret, boolean isSubmittedPDFDoc, boolean includeAllAttachments)
			throws IOException, DocumentException {
		this.hideTradeSecret = hideTradeSecret;
		application = app;
		document.setMargins(leftMargin, rightMargin, topMargin, bottomMargin);
		document.open();

		// display submitted date (or current date if application is not
		// submitted.
		Timestamp docDate = app.getSubmittedDate();
		if (docDate == null) {
			docDate = new Timestamp(System.currentTimeMillis());
		}

		// add a title page common to all applications
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		String[] title = {
				ApplicationTypeDef.getData().getItems()
						.getItemDesc(app.getApplicationTypeCD())
						+ " " + app.getApplicationNumber(),
				app.getFacility().getName(), app.getFacilityId(),
				dateFormat.format(docDate) };
		setSubmitDate(app.getSubmittedDate());
		setSubmittedPDFDoc(isSubmittedPDFDoc);
		setIncludeAllAttachments(includeAllAttachments);
		Table titleTable = createTitleTable(title);
		titleTable.setAutoFillEmptyCells(false);
		document.add(titleTable);

		if (!isAttestationOnly()) {
			// print application-specific data
			printApplication(app);
		}

		// close the document
		if (document != null) {
			document.close();
		}
	}

	/**
	 * Print data for the application.
	 * 
	 * @param app
	 * @throws IOException
	 * @throws DocumentException
	 */
	protected abstract void printApplication(Application app)
			throws IOException, DocumentException;

	protected HeaderFooter createFooter(Application app) {
		String appDsc = ApplicationTypeDef.getData().getItems()
				.getItemDesc(app.getApplicationTypeCD());
		String facName = app.getFacilityName() + " - " + app.getFacilityId();
		StringBuffer facNameBuffer = new StringBuffer();
		int maxFacLength = facName.length();
		if (facName.length() > 60) {
			maxFacLength = facName.length() / 2;
		}
		if (facName.length() > maxFacLength) {
			String[] nameParts = facName.split("\\s");
			int lineLength = 0;
			for (String name : nameParts) {
				if (lineLength + name.length() + 1 > maxFacLength) {
					facNameBuffer.append("\n");
					lineLength = 0;
				} else {
					if (lineLength > 0) {
						facNameBuffer.append(" ");
					}
				}
				facNameBuffer.append(name);
				lineLength += name.length();
			}
			while (lineLength++ < 60) {
				facNameBuffer.append(' ');
			}
		} else {
			facNameBuffer.append(facName);
			while (facNameBuffer.length() < 60) {
				facNameBuffer.append(' ');
			}
		}
		StringBuffer appName = new StringBuffer(appDsc + " - "
				+ app.getApplicationNumber());
		while (appName.length() < 60) {
			appName.insert(0, ' ');
		}
		HeaderFooter footer = new HeaderFooter(new Phrase(
				facNameBuffer.toString() + " Page ", normalFont), new Phrase(
				appName.toString(), normalFont));
		footer.setAlignment(Element.ALIGN_LEFT);
		footer.setBorder(0);
		return footer;
	}

	// protected HeaderFooter createFooter(Application app, String
	// sectionPhrase) {
	// String appDsc =
	// ApplicationTypeDef.getData().getItems().getItemDesc(app.getApplicationTypeCD());
	// HeaderFooter footer = new HeaderFooter(new
	// Phrase("Ohio EPA, Division of Air Pollution Control" +
	// "                          "
	// + " Page ", normalFont),
	// new Phrase( "                                      " +
	// appDsc + sectionPhrase, normalFont));
	// footer.setAlignment(Element.ALIGN_CENTER);
	// footer.setBorder(0);
	// return footer;
	// }

	/**
	 * Create table displaying information about documents.
	 * 
	 * @param documents
	 *            list of documents.
	 * @return
	 * @throws BadElementException
	 */
	protected Table createDocumentTable(
			java.util.List<ApplicationDocumentRef> documents,
			String applicationType) throws BadElementException {
		int numCols = hideTradeSecret ? 4 : 6;
		Table table = generateTable(numCols);
		
		if (hideTradeSecret) {
			float[] widths = { 1, 1, 2, 2 };
			table.setWidths(widths);
		} else {
			float[] widths = { 1, 1, 2, 2, 1, 2 };
			table.setWidths(widths);
		}

		table.addCell(createHeaderCell("Required Attachment"));
		table.addCell(createHeaderCell("Public Document Id"));
		table.addCell(createHeaderCell("Attachment Type"));
		table.addCell(createHeaderCell("Description"));
		if (!hideTradeSecret) {
			table.addCell(createHeaderCell("Trade Secret Document Id"));
			table.addCell(createHeaderCell("Trade Secret Justification"));
		}

		for (ApplicationDocumentRef doc : documents) {
			// don't include documents added after application was submitted
			if (isSubmittedPDFDoc() && getSubmitDate() != null && doc.getLastModifiedTS() != null
					&& doc.getLastModifiedTS().after(getSubmitDate())) {
				logger.debug("Excluding document "
						+ doc.getApplicationDocId()
						+ " from application PDF generator. Document last modified date ("
						+ doc.getLastModifiedTS()
						+ ") is after application submission date ("
						+ getSubmitDate() + ")");
				continue;
			}
			
			Cell requiredDocCell = createCheckCell(doc.isRequiredDoc());
			requiredDocCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(requiredDocCell);

			if (doc.getDocumentId() != null) {
				table.addCell(createDataCell(doc.getApplicationDocId()
						.toString()));
			} else {
				table.addCell(new Cell());
			}

			if (ApplicationType.TITLE_V.equals(applicationType)) {
				table.addCell(createDataCell(TVApplicationDocumentTypeDef
						.getData().getItems()
						.getItemDesc(doc.getApplicationDocumentTypeCD())));
			} else {
				table.addCell(createDataCell(ApplicationDocumentTypeDef
						.getData().getItems()
						.getItemDesc(doc.getApplicationDocumentTypeCD())));
			}
			table.addCell(createDataCell(doc.getDescription()));

			if (!hideTradeSecret) {
				if (doc.getTradeSecretDocId() != null) {
					table.addCell(createCell(((isIncludeAllAttachments() || (isSubmittedPDFDoc() && getSubmitDate() != null) || doc.isTradeSecretAllowed())? doc.getTradeSecretDocId()
							.toString() : ""), tradeSecretFont));
				} else {
					table.addCell(new Cell());
				}
				if (doc.getTradeSecretReason() != null) {
					table.addCell(createCell(((isIncludeAllAttachments() || (isSubmittedPDFDoc() && getSubmitDate() != null) || doc.isTradeSecretAllowed())? doc.getTradeSecretReason() : ""),
							tradeSecretFont));
				} else {
					table.addCell(new Cell());
				}
			}
		}

		return table;
	}

	protected ListItem getAttachments(String title,
			java.util.List<ApplicationDocumentRef> documents,
			String applicationType) throws BadElementException {
		ListItem ret = new ListItem();
		ret.add(createItemTitle(title, ""));
		ret.add(createDocumentTable(documents, applicationType));
		return ret;
	}

	/**
	 * Create table for displaying emission unit data.
	 * 
	 * @param app
	 *            the application.
	 * @return
	 * @throws DocumentException
	 */
	public Table createEUTable(Application app) throws DocumentException {
		Table euTable = new Table(3);
		euTable.setWidth(98);
		euTable.setPadding(2);
		float[] widths = { 1, 2, 2 };
		euTable.setWidths(widths);

		// headers
		euTable.addCell(createCell("Emissions Unit ID"));

		Paragraph coEuIdTitle = new Paragraph(defaultLeading);
		coEuIdTitle.add(new Phrase(defaultLeading, "Company Equipment ID",
				boldFont));
		coEuIdTitle.add(new Phrase(defaultLeading,
				" (company's name for air contaminant source)", normalFont));
		euTable.addCell(new Cell(coEuIdTitle));

		Paragraph eqptDesc = new Paragraph(defaultLeading);
		eqptDesc.add(new Phrase(defaultLeading, "Equipment Description",
				boldFont));
		eqptDesc.add(new Phrase(
				defaultLeading,
				" (List all equipment that are a part of this air contaminant source)",
				normalFont));
		euTable.addCell(new Cell(eqptDesc));

		for (ApplicationEU appEU : app.getIncludedEus()) {
			EmissionUnit fpEU = app.getFacility().getEmissionUnit(
					appEU.getFpEU().getEmuId());
			if (fpEU == null) {
				logger.error("No Facility EU found matching EmuId: "
						+ appEU.getFpEU().getEmuId()
						+ " in facility with fp_id: "
						+ app.getFacility().getFpId());
				continue;
			}
			euTable.addCell(createDataCell(fpEU.getEpaEmuId()));
			euTable.addCell(createDataCell(fpEU.getCompanyId()));

			StringBuffer eqptList = new StringBuffer();
			for (EmissionProcess ep : fpEU.getEmissionProcesses()) {
				for (ControlEquipment ce : ep.getControlEquipments()) {
					String desc = ce.getRegUserDesc();
					if (desc == null) {
						desc = ce.getControlEquipmentId();
					}
					eqptList.append(desc + ", ");
				}
			}
			// get rid of trailing ", "
			if (eqptList.length() > 0) {
				eqptList.deleteCharAt(eqptList.length() - 1);
				eqptList.deleteCharAt(eqptList.length() - 1);
			}

			euTable.addCell(createDataCell(eqptList.toString()));
		}

		return euTable;
	}

	/*
	 * FOR TESTING ONLY
	 */
	public static void main(String[] args) {
		String dirPath = DocumentUtil.getFileStoreRootPath() + File.separator
				+ "tmp";
		try {
			ApplicationService appBO = ServiceFactory.getInstance()
					.getApplicationService();
			FacilityService facilityBO = ServiceFactory.getInstance()
					.getFacilityService();
			Application app = appBO.retrieveApplicationWithAllEUs("A0036829");
			// need to retrieve more information for facility EUs
			app.setFacility(facilityBO.retrieveFacilityProfile(app
					.getFacility().getFpId(), false, null));
			ApplicationPdfGenerator gen = getGeneratorForClass(app.getClass());
			FileOutputStream fos = new FileOutputStream(dirPath
					+ File.separator + app.getApplicationNumber() + ".pdf");
			gen.setUserName("test");
			gen.generatePdf(app, fos, false, false, true);
			fos.close();

			PdfGeneratorBase.addTradeSecretWatermark(dirPath + File.separator
					+ app.getApplicationNumber() + ".pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final float getBottomMargin() {
		return bottomMargin;
	}

	public final void setBottomMargin(float bottomMargin) {
		this.bottomMargin = bottomMargin;
	}

	public final boolean isHideTradeSecret() {
		return hideTradeSecret;
	}

	public final void setHideTradeSecret(boolean hideTradeSecret) {
		this.hideTradeSecret = hideTradeSecret;
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

	public final Font getTradeSecretFont() {
		return tradeSecretFont;
	}

	public final void setTradeSecretFont(Font tradeSecretFont) {
		this.tradeSecretFont = tradeSecretFont;
	}

	public final Color getTradeSecretIndicatorColor() {
		return tradeSecretIndicatorColor;
	}

	public final void setTradeSecretIndicatorColor(
			Color tradeSecretIndicatorColor) {
		this.tradeSecretIndicatorColor = tradeSecretIndicatorColor;
	}

	public final DecimalFormat getHapValueFormat() {
		return hapValueFormat;
	}

	public final void setHapValueFormat(DecimalFormat hapValueFormat) {
		if (hapValueFormat != null) {
			this.hapValueFormat = hapValueFormat;
		}
	}

	@Override
	protected Paragraph getCertificationLanguage() throws DocumentException {
		String str = "";
		if (application.getFacility() != null) {
			try {
				InfrastructureService infrastructureBO = ServiceFactory
						.getInstance().getInfrastructureService();
				str = infrastructureBO.getDAPCAttestationMessage(application
						.getFacility().getPermitClassCd());
			} catch (ServiceFactoryException e) {
				logger.error("Exception while geting certification language", e);
			} catch (RemoteException e) {
				logger.error("Exception while geting certification language", e);
			}
		} else {
			logger.error("No facility data found for application: "
					+ application.getApplicationNumber()
					+ ". Using blank attestation message.");
		}
		// format the certification language better
		Paragraph attestation = new Paragraph(defaultLeading,
				formatCertificationLanguage(str), smallerBold);

		return attestation;
	}

	protected ListItem generateNotes(java.util.List<ApplicationNote> appNotes)
			throws DocumentException {

		ListItem ret = new ListItem();
		ret.add(createItemTitle("Notes", ""));

		Table table = generateTable(3);
		float[] widths = { 1, 1, 2 };
		table.setWidths(widths);
		table.setAlignment(Element.ALIGN_LEFT);

		table.addCell(createHeaderCell("User Name"));
		table.addCell(createHeaderCell("Date"));
		table.addCell(createHeaderCell("Note"));

		for (ApplicationNote note : appNotes) {
			table.addCell(createCell(BasicUsersDef.getUserNm(note.getUserId())));
			table.addCell(createCell(toDateFormat1(note.getDateEntered())));
			table.addCell(createCell(note.getNoteTxt()));
		}

		ret.add(table);
		return ret;
	}

	public boolean isContainsTradeSecret() {
		return containsTradeSecret;
	}

	protected boolean isInternal() {
		return CompMgr.getAppName().equals(CommonConst.INTERNAL_APP);
	}

	protected boolean isPortal() {
		return CompMgr.getAppName().equals(CommonConst.EXTERNAL_APP);
	}

	protected boolean isPublic() {
		return CompMgr.getAppName().equals(CommonConst.PUBLIC_APP);
	}

	/* Start - Sub for Table and Cell */
	protected Table generateTable() throws BadElementException {
		Table table = new Table(1);
		table.setWidth(98f);
		table.setPadding(2);
		return table;
	}

	protected Table generateTable(int col) throws BadElementException {
		Table table = new Table(col);
		table.setWidth(98f);
		table.setPadding(2);
		return table;
	}

	protected Table generateQATable(float[] widths) throws BadElementException {
		Table table = new Table(2);
		table.setWidth(98f);
		table.setBorder(0);
		table.setSpacing(2);
		table.setTop(0);
		table.setWidths(widths);
		return table;
	}

	protected Table generateQATableShortQ() throws BadElementException {
		float[] widths = { 1, 2 };
		return generateQATable(widths);
	}

	protected Table generateQATableLongQ() throws BadElementException {
		float[] widths = { 2, 1 };
		return generateQATable(widths);
	}

	protected Table generateQATable() throws BadElementException {
		float[] widths = { 1, 1 };
		return generateQATable(widths);
	}

	protected void generateQAColumns(Table table, String question, String answer) {

		Cell questionCell = createCell(question, 0);
		questionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

		Cell answerCell = createDataCell(answer, 0);
		table.addCell(questionCell);
		table.addCell(answerCell);
	}

	protected void generateHeaderCell(Table header, Cell cell, String label,
			String value) {

		header.addCell(cell);

		cell = createCell(label, boldFont, 0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		header.addCell(cell);

		cell = createDataCell(value, 0);
		header.addCell(cell);
	}

	protected Cell createDataCell(String dataText, int border) {
		Cell cell = createDataCell(dataText);
		cell.setBorder(border);
		return cell;
	}

	protected Cell createCell(String cellText, int border) {
		Cell cell = createCell(cellText);
		cell.setBorder(border);
		return cell;
	}

	protected Cell createCell(String cellText, Font font, int border) {
		Cell cell = createCell(cellText, font);
		cell.setBorder(border);
		return cell;
	}

	protected Paragraph createOneLine(String text, Font font) {
		Paragraph ret = new Paragraph(defaultLeading);
		ret.add(new Phrase(defaultLeading, text, font));
		return ret;
	}

	protected List createItemList() {
		return new List(false, 20);
	}

	protected ListItem createItem(String itemDes, Font font) {
		ListItem item = new ListItem();
		item.add(new Phrase(itemDes, font));
		return item;
	}

	protected Cell createHeaderCellRowspan(String headerText, int row) {
		Cell header = createHeaderCell(headerText);
		header.setRowspan(row);
		return header;
	}

	protected Cell createHeaderCellColspan(String headerText, int col) {
		Cell header = createHeaderCell(headerText);
		header.setColspan(col);
		return header;
	}

	protected Cell createCellwihtDes(String title, String des) {
		Cell ret = new Cell();
		ret.setBorder(0);
		Paragraph paragraph = new Paragraph(defaultLeading);
		paragraph.add(new Paragraph(defaultLeading, title, boldFont));
		paragraph.add(new Paragraph(defaultLeading, des, italicFont));
		ret.add(paragraph);
		return ret;
	}

	protected Cell createNumberCell(String text) {
		Cell tempCell = createDataCell(text);
		tempCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		return tempCell;
	}

	protected void generate2Columns(Table table, String name1, String value1,
			boolean attr1, String name2, String value2, boolean attr2) {
		boolean val;
		Cell cell;

		cell = createCell(defaultLeading, name1, normalFont);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);

		if (attr1) {
			val = value1.equals("true") ? true : false;
			cell = createCheckCell(val);
		} else {
			cell = createCell(defaultLeading, value1, dataFont);
		}
		cell.setBorder(0);

		if (name2 == null) {
			cell.setColspan(3);
		}
		table.addCell(cell);

		if (name2 != null) {
			cell = createCell(defaultLeading, name2, normalFont);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setBorder(0);
			table.addCell(cell);

			if (attr2) {
				val = value2.equals("true") ? true : false;
				cell = createCheckCell(val);
			} else {
				cell = createCell(defaultLeading, value2, dataFont);
			}
			cell.setBorder(0);

			table.addCell(cell);
		}
	}

	/* End - Sub for Table and Cell */

	/* Start - format */
	protected String toDateFormat1(Timestamp timestamp) {
		return timestamp == null ? null : dateFormat1.format(timestamp);
	}

	protected String toVALUE_FORMAT(Float obj) {
		return obj == null ? null : VALUE_FORMAT.format(obj);
	}
	
	protected String toVALUE_FORMAT(BigDecimal obj) {
		return obj == null ? null : VALUE_FORMAT.format(obj);
	}

	protected String toYesNo(boolean bool) {
		return bool ? "Yes" : "No";
	}

	protected String toYesNo(String str) {
		if (str == null)
			return null;

		return str.equals("Y") ? "Yes" : "No";
	}

	protected boolean isEqual(String str1, String str2) {
		return str1 != null && str1.equals(str2);
	}

	protected String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	// capEmissionsValueFormat, capLbHrEmissionsValueFormat,
	// ghgEmissionsValueFormat
	private String toDFLT_EMISSIONS_VALUE_FORMAT(String obj) {
		if (Utility.isNullOrEmpty(obj)) {
			return null;
		} else {
			return DFLT_EMISSIONS_VALUE_FORMAT.format(new BigDecimal(obj));
		}
	}

	// hapEmissionsValueFormat
	private String toEMISSIONS_VALUE_FORMAT_HAP(String obj) {
		if (Utility.isNullOrEmpty(obj)) {
			return null;
		} else {
			return EMISSIONS_VALUE_FORMAT_HAP.format(new BigDecimal(obj));
		}
	}

	protected String toEMISSIONS_VALUE_FORMAT(String obj, boolean isHap) {
		if (!Utility.isNullOrEmpty(obj)) {
			obj = obj.replace(",", "");
		}
		if (isHap) {
			return toEMISSIONS_VALUE_FORMAT_HAP(obj);
		} else {
			return toDFLT_EMISSIONS_VALUE_FORMAT(obj);
		}
	}

	/* End - format */

	/* Start - Subpart table */
	protected boolean isShowSubparts(String flag) {
		boolean showSubparts = false;
		showSubparts = flag != null
				&& (flag.equals(PTIOFedRuleAppl1Def.SUBJECT_TO_SUBPART) || flag
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		return showSubparts;
	}

	protected Table createNSPSTable(java.util.List<String> nspsSubpartCodes)
			throws BadElementException {
		Table table = generateTable();
		table.addCell(createHeaderCell("NSPS Subpart"));
		for (String subpart : nspsSubpartCodes) {
			table.addCell(createDataCell(PTIONSPSSubpartDef.getData()
					.getItems().getItemDesc(subpart)));
		}

		return table;
	}

	protected Table createNESHAPTable(java.util.List<String> neshapSubpartCodes)
			throws BadElementException {
		Table table = generateTable();
		table.addCell(createHeaderCell("Part 61 NESHAP Subpart"));
		for (String subpart : neshapSubpartCodes) {
			table.addCell(createDataCell(PTIONESHAPSSubpartDef.getData()
					.getItems().getItemDesc(subpart)));
		}

		return table;
	}

	protected Table createMACTTable(java.util.List<String> mactSubpartCodes)
			throws BadElementException {
		Table table = generateTable();
		table.addCell(createHeaderCell("Part 63 NESHAP Subpart"));
		for (String subpart : mactSubpartCodes) {
			table.addCell(createDataCell(PTIOMACTSubpartDef.getData()
					.getItems().getItemDesc(subpart)));
		}

		return table;
	}

	/* End - Subpart table */

}
