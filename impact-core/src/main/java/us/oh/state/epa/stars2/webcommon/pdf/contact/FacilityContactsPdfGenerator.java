package us.oh.state.epa.stars2.webcommon.pdf.contact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsRptInfo;
import us.oh.state.epa.stars2.database.dbObjects.facility.ControlEquipment;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPoint;
import us.oh.state.epa.stars2.database.dbObjects.facility.EgressPointCem;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EuEmission;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityCemComLimit;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityVersion;
import us.oh.state.epa.stars2.database.dbObjects.facility.MultiEstabFacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantsControlled;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitReplacement;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ApiGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.database.dbObjects.workflow.EnumDetail;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.CeOperatingStatusDef;
import us.oh.state.epa.stars2.def.CerrClassDef;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.District;
import us.oh.state.epa.stars2.def.EgOperatingStatusDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EuOperatingStatusDef;
import us.oh.state.epa.stars2.def.EuPollutantDef;
import us.oh.state.epa.stars2.def.FacilityAttachmentTypeDef;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.HortAccuracyMeasure;
import us.oh.state.epa.stars2.def.HortCollectionMethods;
import us.oh.state.epa.stars2.def.HortReferenceDatum;
import us.oh.state.epa.stars2.def.MaxAnnualThroughputUnitDef;
import us.oh.state.epa.stars2.def.NAICSDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.ReferencePoints;
import us.oh.state.epa.stars2.def.ReportStatusDef;
import us.oh.state.epa.stars2.def.SICDef;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.converter.PhoneNumberConverter;
import us.oh.state.epa.stars2.webcommon.converter.SccCodeConverter;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.wy.state.deq.impact.app.continuousMonitoring.ContinuousMonitorSearch;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitorEqt;
import us.wy.state.deq.impact.def.AbsLeadConcentrationPctTypeDef;
import us.wy.state.deq.impact.def.AbsSubstrateBlastedTypeDef;
import us.wy.state.deq.impact.def.AbsSubstrateRemovedTypeDef;
import us.wy.state.deq.impact.def.AptMaxThroughputUnitsDef;
import us.wy.state.deq.impact.def.BatchingTypeDef;
import us.wy.state.deq.impact.def.BurnerTypeDef;
import us.wy.state.deq.impact.def.CkdMaxAnnualThroughputUnitsDef;
import us.wy.state.deq.impact.def.CkdUnitTypeDef;
import us.wy.state.deq.impact.def.CmxMaxProductionRateUnitsDef;
import us.wy.state.deq.impact.def.CotApplicationMethodTypeDef;
import us.wy.state.deq.impact.def.CotCoatingOperationTypeDef;
import us.wy.state.deq.impact.def.CotMaterialBeingCoatedTypeDef;
import us.wy.state.deq.impact.def.CshMaxAnnualThroughputUnitDef;
import us.wy.state.deq.impact.def.CshUnitTypeDef;
import us.wy.state.deq.impact.def.DehydrationTypeDef;
import us.wy.state.deq.impact.def.EguUnitTypeDef;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;
import us.wy.state.deq.impact.def.EngineTypeDef;
import us.wy.state.deq.impact.def.EquipTypeDef;
import us.wy.state.deq.impact.def.EventTypeDef;
import us.wy.state.deq.impact.def.FatUnitTypeDef;
import us.wy.state.deq.impact.def.FiringTypeDef;
import us.wy.state.deq.impact.def.FlareDesignCapacityUnitsDef;
import us.wy.state.deq.impact.def.FuelTypeDef;
import us.wy.state.deq.impact.def.HmaMaxProductionRateUnitsDef;
import us.wy.state.deq.impact.def.IncineratorDesignCapacityUnitsDef;
import us.wy.state.deq.impact.def.IncineratorTypeDef;
import us.wy.state.deq.impact.def.LudMaxAnnualThroughputUnitsDef;
import us.wy.state.deq.impact.def.MaterialProducedDef;
import us.wy.state.deq.impact.def.MaterialTypeDef;
import us.wy.state.deq.impact.def.NamePlateRatingUnitsDef;
import us.wy.state.deq.impact.def.OrdExplosiveTypeDef;
import us.wy.state.deq.impact.def.PamEquipTypeDef;
import us.wy.state.deq.impact.def.PlantTypeDef;
import us.wy.state.deq.impact.def.PowerSourceTypeDef;
import us.wy.state.deq.impact.def.PrimaryFuelTypeDef;
import us.wy.state.deq.impact.def.PrnPressTypeDef;
import us.wy.state.deq.impact.def.PrnSubstrateFeedMethodTypeDef;
import us.wy.state.deq.impact.def.RemContaminantTypeDef;
import us.wy.state.deq.impact.def.RemContaminatedMaterialTypeDef;
import us.wy.state.deq.impact.def.SebUnitTypeDef;
import us.wy.state.deq.impact.def.SecondaryFuelTypeDef;
import us.wy.state.deq.impact.def.SemEquipTypeDef;
import us.wy.state.deq.impact.def.SepVesselTypeDef;
import us.wy.state.deq.impact.def.SiteRatingUnitsDef;
import us.wy.state.deq.impact.def.SruMaxAnnualThroughputUnitsDef;
import us.wy.state.deq.impact.def.SvcEquipTypeDef;
import us.wy.state.deq.impact.def.TkoMetalTypeDef;
import us.wy.state.deq.impact.def.TkoPlatingTypeDef;
import us.wy.state.deq.impact.def.TnkCapacityUnitDef;
import us.wy.state.deq.impact.def.TnkMaterialStoredTypeDef;
import us.wy.state.deq.impact.def.TnkMaxThroughputUnitDef;
import us.wy.state.deq.impact.def.UnitTypeDef;
import us.wy.state.deq.impact.def.WweEquipTypeDef;

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

public class FacilityContactsPdfGenerator extends PdfGeneratorBase {
	
	private static final long serialVersionUID = -7430939060975385983L;

	protected static final DecimalFormat VALUE_FORMAT = new DecimalFormat(
			"#####0.0####");
	protected static final DecimalFormat VALUE_FORMAT1 = new DecimalFormat(
			"#####0.0#");

	private float leftMargin = 10;
	private float rightMargin = 10;
	private float topMargin = 20;
	private float bottomMargin = 20;
	private Font data1Font = FontFactory
			.getFont(dataFontFamily, 8, Font.NORMAL);
	private Font normal1Font = FontFactory.getFont(defaultFontFamily, 8,
			Font.NORMAL);
	private Font normal2Font = FontFactory.getFont(defaultFontFamily, 7,
			Font.NORMAL);
	private Document document; // PDF document
	private Facility facility;
	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
	private LinkedHashMap<String, String> contactTitles;
	private MultiEstabFacilityList[] multiEstabFacilities;
	private static Logger logger = Logger.getLogger(FacilityContactsPdfGenerator.class);

	
	/**
	 * Constructor is protected to force users to call
	 * <code>getGeneratorForClass</code>
	 * 
	 */
	public FacilityContactsPdfGenerator() throws DocumentException {
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
	 * Generate a PDF file rendering the data within <code>facility</code>.
	 * 
	 * @param facility
	 *            - facility to be rendered.
	 * @param os
	 *            OutputStream to which file will be written.
	 * @throws IOException
	 * @throws DocumentException
	 */
	public void generatePdf(Facility facility, OutputStream os, String titleSuffix)
			throws IOException, DocumentException {
		
		document = new Document();
		PdfWriter.getInstance(document, os);

		document.setMargins(leftMargin, rightMargin, topMargin, bottomMargin);
		document.open();
		setSubmitDate(facility.getStartDate());
		
		if(null == titleSuffix) {
			titleSuffix = "";
		}

		String heading = "Facility Contacts " + titleSuffix;
		
		String[] titles = { heading,
				"Facility Name: " + facility.getName(),
				"ID: " + facility.getFacilityId(), "" };

		Table titleTable = createTitleTable(titles);
		titleTable.setAutoFillEmptyCells(false);
		document.add(titleTable);
		
		this.facility = facility;

		printFacilityContacts();

		// close the document
		if (document != null) {
			document.close();
		}
	}

	private void printFacilityContacts() throws IOException, DocumentException {
		
		// define footer - NOTE: footer must be added to document after
		// PdfWriter.getInstance and BEFORE document.open so footer will
		// appear on all pages (including first page)
		HeaderFooter footer = new HeaderFooter(new Phrase("   Page ",
				normalFont), new Phrase("             Facility Detail Report ("
				+ facility.getFacilityId() + "): " + facility.getName(),
				italicFont));

		footer.setAlignment(Element.ALIGN_LEFT);
		footer.setBorder(0);
		document.setFooter(footer);

		document.setPageCount(0);
		document.newPage();

		Table titleTable = createTitleTable(
				"Facility : " + facility.getFacilityId(), "");
		document.add(titleTable);

		List sections = new List(false, 20);
		sections.add(generateContacts());

		document.add(sections);

	}

	private void generateCemComLimit(FacilityCemComLimit limit) throws DocumentException {
		document.newPage();

		Table titleTable = createTitleTable(
				"CEM/COM/CMS Limit: " + limit.getLimId(), "");
		document.add(titleTable);

		List sections = new List(false, 20);
		sections.add(generateCemComLimitInfo(limit));
//		// sections.add(generateBuildingDimensionsData(egp));
//		if (!egp.isFugitive()) {
//			sections.add(generateTypeSpecData(egp));
//		}
//		sections.add(generateLatAndLogData(egp));
//		// sections.add(generateEISData(egp));
//		sections.add(generateCEMData(egp));

		document.add(sections);
	}

	private Object generateCemComLimitInfo(FacilityCemComLimit limit)
			throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"CEM/COM/CMS Limit Information", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		generate2Columns(table, "Limit ID:", limit.getLimId(),
				false, null, null, false);
		generate2Columns(table, "Monitor ID:", limit.getMonId(), false,
				null, null, false);
		generate2Columns(table, "Limit Description:",limit.getLimitDesc(), false,
				null, null, false);
		generate2Columns(table, "Source of Limit:", limit.getLimitSource(), false,
				null, null, false);
		generate2Columns(table, "Start Monitoring Limit:", formatDate(limit.getStartDate()), false,
				null, null, false);
		generate2Columns(table, "End Monitoring Limit:", formatDate(limit.getEndDate()), false,
				null, null, false);
		generate2Columns(table, "Additional Information:", limit.getAddlInfo(), false,
				null, null, false);
		item1.add(table);

		return item1;


	}

	/*private String formatDate(Timestamp date) {
		String s = null;
		if (null != date) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			s = DateUtils.toDateString(cal);
		}
		return s;
	}*/

	private void generateCemComMonitor(ContinuousMonitor monitor) throws DocumentException {
		document.newPage();

		Table titleTable = createTitleTable(
				"CEM/COM/CMS Monitor: " + monitor.getMonId(), "");
		document.add(titleTable);

		List sections = new List(false, 20);
		sections.add(generateContinuousMonitorInfo(monitor));
		sections.add(generateMonitorLimits(monitor));
		sections.add(generateMonitorTrackingDetails(monitor));
		
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			sections.add(generateNotes(monitor.getNotes()));
		}

		document.add(sections);
		
	}

	private Object generateMonitorLimits(ContinuousMonitor monitor)
			throws DocumentException {
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Limits Measured by the Monitor", boldFont);
		item1.add(item1Title);

		List sections = new List(false, 20);
		for (FacilityCemComLimit limit : monitor.getFacilityCemComLimitList()) {
			sections.add(generateMonitorLimitDetail(limit));
		}
		item1.add(sections);
		return item1;
	}

	private Object generateMonitorLimitDetail(FacilityCemComLimit limit)
			throws DocumentException {
		ListItem item1 = new ListItem();
	
		Phrase item1Title = new Paragraph(defaultLeading,
				"Facility CEM/COM/CMS Limit Detail", boldFont);
		item1.add(item1Title);
	
		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);
	
		generate2Columns(table, "Limit ID:", limit.getLimId(), false,
				null, null, false);
		generate2Columns(table, "Monitor ID:", limit.getMonId(), false,
				null, null, false);
		generate2Columns(table, "Limit Description:", limit.getLimitDesc(), false,
				null, null, false);
		generate2Columns(table, "Source of Limit:", limit.getLimitSource(), false,
				null, null, false);
		generate2Columns(table, "Start Monitoring Limit:", formatDate(limit.getStartDate()), false,
				null, null, false);
		generate2Columns(table, "End Monitoring Limit:", formatDate(limit.getEndDate()), false,
				null, null, false);
		generate2Columns(table, "Additional Information:", limit.getAddlInfo(), false,
				null, null, false);
		item1.add(table);
	
		return item1;
	}

	private Object generateMonitorTrackingDetails(ContinuousMonitor monitor)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
	
		Phrase item1Title = new Paragraph(defaultLeading,
				"Monitor Tracking Details", boldFont);
		item1.add(item1Title);

		List sections = new List(false, 20);
		for (ContinuousMonitorEqt equipment : monitor.getContinuousMonitorEqtList()) {
			sections.add(generateContinuousEquipmentDetail(equipment));
		}
		item1.add(sections);
		return item1;
	}

	private Object generateContinuousEquipmentDetail(ContinuousMonitorEqt equipment)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
	
		Phrase item1Title = new Paragraph(defaultLeading,
				"Monitor Equipment Detail", boldFont);
		item1.add(item1Title);
	
		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);
	
		generate2Columns(table, "Manufacturer:", equipment.getManufacturerName(), false,
				null, null, false);
		generate2Columns(table, "Model Number:", equipment.getModelNumber(), false,
				null, null, false);
		generate2Columns(table, "Serial Number:", equipment.getSerialNumber(), false,
				null, null, false);
		generate2Columns(table, "QA/QC Submitted Date:", formatDate(equipment.getQAQCSubmittedDate()), false,
				null, null, false);
		generate2Columns(table, "QA/QC Accepted Date:", formatDate(equipment.getQAQCAcceptedDate()), false,
				null, null, false);
		generate2Columns(table, "Install Date:", formatDate(equipment.getInstallDate()), false,
				null, null, false);
		generate2Columns(table, "Removal Date:", formatDate(equipment.getRemovalDate()), false,
				null, null, false);
		generate2Columns(table, "Additional Information:", equipment.getAddlInfo(), false,
				null, null, false);
		item1.add(table);
	
		return item1;
	}

	private Object generateContinuousMonitorInfo(ContinuousMonitor monitor)
			throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Continuous Monitor Information", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		generate2Columns(table, "Continuous Monitor ID:", monitor.getMonId(),
				false, null, null, false);
		generate2Columns(table, "Monitor Details:", monitor.getMonitorDetails(), false,
				null, null, false);
		generate2Columns(table, "Manufacturer:", monitor.getCurrentManufacturer(), false,
				null, null, false);
		generate2Columns(table, "Model Number:", monitor.getCurrentModelNumber(), false,
				null, null, false);
		generate2Columns(table, "Active Limits:", monitor.getActiveLimits(), false,
				null, null, false);
		generate2Columns(table, "Associated Object(s):", monitor.getAssociatedObjects(), false,
				null, null, false);
		item1.add(table);

		return item1;

	}

	private ListItem generateSicCodes() throws DocumentException {
		
		ListItem item1 = new ListItem();
		DefSelectItems defCodes = SICDef.getData().getItems();

		Phrase item1Title = new Paragraph(defaultLeading, "SIC Codes", boldFont);
		item1.add(item1Title);

		Table table = generateTable("SIC", defCodes, facility.getSicCds()
				.toArray(new String[0]), SICDef.class);
		item1.add(table);

		return item1;
	}

	private ListItem generateNaicsCodes() throws DocumentException {
		
		ListItem item1 = new ListItem();
		DefSelectItems defCodes = NAICSDef.getData().getItems();

		Phrase item1Title = new Paragraph(defaultLeading, "NAICS Codes",
				boldFont);
		item1.add(item1Title);

		Table table = generateTable("NAICS", defCodes, facility.getNaicsCds()
				.toArray(new String[0]), NAICSDef.class);
		item1.add(table);

		return item1;
	}

	private ListItem generateMultiEstablish() throws DocumentException {
		
		ListItem item1 = new ListItem();
		Cell cell;

		Phrase item1Title = new Paragraph(defaultLeading, "MultiEstablishment",
				boldFont);
		item1.add(item1Title);

		Table table = new Table(2);
		table.setWidth(80);
		float[] widths = { 30, 70 };
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Facility ID");
		table.addCell(cell);
		cell = createCell("Facility Name");
		table.addCell(cell);

		for (MultiEstabFacilityList tempFac : multiEstabFacilities) {
			cell = createCell(defaultLeading, tempFac.getFacilityId(),
					data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, tempFac.getName(), data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateFedRulesAppl() throws DocumentException {
		
		ListItem item1 = new ListItem();
		DefSelectItems defCodes;
		String temp;
		String temp1;

		Phrase item1Title = new Paragraph(defaultLeading, "Rules & Regs",
				boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		temp = (facility.isNsps()) ? "true" : "false";
		temp1 = (facility.isSec112()) ? "true" : "false";
		generate2Columns(table, "Subject to Part 60 NSPS:", temp, true,
				"Subject to 112(r) Accidental Release Prevention:", temp1, true);

		temp = (facility.isNeshaps()) ? "true" : "false";
		temp1 = (facility.isNsrNonattainment()) ? "true" : "false";
		generate2Columns(table, "Subject to Part 61 NESHAP:", temp, true,
				"Subject to non-attainment NSR:", temp1, true);

		temp = (facility.isMact()) ? "true" : "false";
		temp1 = (facility.isPsd()) ? "true" : "false";
		generate2Columns(table, "Subject Part 63 NESHAP:", temp, true,
				"Subject to PSD:", temp1, true);

		temp = (facility.isTivInd()) ? "true" : "false";
		generate2Columns(table, "Subject to Title IV Acid Rain:", temp, true,
				null, null, false);
		item1.add(table);

		if (facility.isNsps()) {
			defCodes = PTIONSPSSubpartDef.getData().getItems();
			table = generateSubTable("Part 60 NSPS Subparts");
			item1.add(table);
			table = generateTable("Part 60 NSPS Subpart", defCodes, facility
					.getNspsSubparts().toArray(new String[0]),
					PTIONSPSSubpartDef.class);
			item1.add(table);
		}

		if (facility.isNeshaps()) {
			defCodes = PTIONESHAPSSubpartDef.getData().getItems();
			table = generateSubTable("Part 61 NESHAP Subparts");
			item1.add(table);
			table = generateTable("Part 61 NESHAP Subpart", defCodes, facility
					.getNeshapsSubparts().toArray(new String[0]),
					PTIONESHAPSSubpartDef.class);
			item1.add(table);
		}

		if (facility.isMact()) {
			defCodes = PTIOMACTSubpartDef.getData().getItems();
			table = generateSubTable("Part 63 NESHAP Subparts");
			item1.add(table);
			table = generateTable("Part 63 NESHAP Subpart", defCodes, facility
					.getMactSubparts().toArray(new String[0]),
					PTIOMACTSubpartDef.class);
			item1.add(table);
		}

		return item1;
	}

	private ListItem generateFacAttachments() throws DocumentException {
		
		ListItem item1 = new ListItem();
		Cell cell;
		String temp;

		Phrase item1Title = new Paragraph(defaultLeading, "Attachments",
				boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		float[] widths = { 40, 20, 20, 20 };
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Description");
		table.addCell(cell);
		cell = createCell("Type");
		table.addCell(cell);
		cell = createCell("Modified By");
		table.addCell(cell);
		cell = createCell("Modified Date");
		table.addCell(cell);

		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.getAttachments();

		Attachments attachments = (Attachments) FacesUtil
				.getManagedBean("attachments");
		for (Attachment facAttachment : attachments.getAttachmentList()) {
			cell = createCell(defaultLeading, facAttachment.getDescription(),
					data1Font);
			table.addCell(cell);
			temp = FacilityAttachmentTypeDef.getData().getItems()
					.getItemDesc(facAttachment.getAttachmentType());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading,
					BasicUsersDef.getUserNm(facAttachment.getLastModifiedBy()),
					data1Font);
			table.addCell(cell);
			temp = facAttachment.getLastModifiedTS() == null ? null
					: dateFormat1.format(facAttachment.getLastModifiedTS());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	private Table generateSubTable(String title) throws DocumentException {
		
		Table table = new Table(1);
		table.setBorder(0);
		table.setAlignment(Element.ALIGN_LEFT);

		Cell cell = createCell(title);
		cell.setBorder(0);
		table.addCell(cell);

		cell = createCell("");
		cell.setBorder(0);
		table.addCell(cell);
		table.addCell(cell);

		return table;
	}

	private ListItem generateFacilityData() throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Facility Information", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp;
		String temp1;

		generate2Columns(table, "Facility ID:", facility.getFacilityId(),
				false, null, null, false);
		generate2Columns(table, "FacilityName:", facility.getName(), false,
				null, null, false);
		generate2Columns(table, "Facility Description:", facility.getDesc(),
				false, null, null, false);
		generate2Columns(table, "Company Name:",
				facility.getOwner() == null ? "" : facility.getOwner()
						.getCompany().getName(), false, null, null, false);

		temp = OperatingStatusDef.getData().getItems()
				.getItemDesc(facility.getOperatingStatusCd());
		if (facility.getOperatingStatusCd().equals(OperatingStatusDef.SD)) {
			temp1 = facility.getShutdownDate() == null ? null : dateFormat1
					.format(facility.getShutdownDate());
			generate2Columns(table, "Operating Status:", temp, false,
					"Shutdown Date:", temp1, false);
			generate2Columns(table, "AFS:", facility.getAfs(), false, null,
					null, false);
		} else {
			generate2Columns(table, "Operating Status:", temp, false, "AFS:",
					facility.getAfs(), false);
		}

		temp = PermitClassDef.getData().getItems()
				.getItemDesc(facility.getPermitClassCd());
		temp1 = FacilityTypeDef.getTextData().getItems()
				.getItemDesc(facility.getFacilityTypeCd());
		generate2Columns(table, "Facility Class:", temp, false,
				"Facility Type:", temp1, false);

		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			temp = CerrClassDef.getData().getItems()
					.getItemDesc(facility.getCerrClassCd());
			generate2Columns(table, "CERR Class:", temp, false,
					null, null, false);
		}
		item1.add(table);

		return item1;
	}

	private ListItem generateLocation() throws DocumentException {
		
		ListItem item1 = new ListItem();
		Cell cell;
		String temp;
		String temp1;

		Phrase item1Title = new Paragraph(defaultLeading, "Location", boldFont);
		item1.add(item1Title);

		Table table = new Table(6);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Physical Address");
		table.addCell(cell);
		cell = createCell("City");
		table.addCell(cell);
		cell = createCell("County");
		table.addCell(cell);
		cell = createCell("Lat/Long");
		table.addCell(cell);
		cell = createCell("PLSS");
		table.addCell(cell);
		cell = createCell("Effective Date");
		table.addCell(cell);

		for (Address address : facility.getAddressesList()) {
			cell = createCell(
					defaultLeading,
					address.getFullAddress() == null ? null : address
							.getFullAddress(), data1Font);
			table.addCell(cell);
			cell = createCell(
					defaultLeading,
					address.getCityName() == null ? null : address
							.getCityName(), data1Font);
			table.addCell(cell);
			temp = County.getData().getItems()
					.getItemDesc(address.getCountyCd());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading,
					address.getLatlong() == null ? null : address.getLatlong(),
					data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, address.getPlss() == null ? null
					: address.getPlss(), data1Font);
			table.addCell(cell);
			temp = address.getBeginDate() == null ? null : dateFormat1
					.format(address.getBeginDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		for (Address address : facility.getAddressesList()) {
			table = generateSubTable("Location Detail For : "
					+ address.getFullAddress());
			item1.add(table);

			table = new Table(4);
			table.setBorder(0);
			table.setPadding(2);
			table.setWidth(98);
			generate2Columns(table, "Latitude:", address.getLatitude(), false,
					"Longitude:", address.getLongitude(), false);
			generate2Columns(table, "Quarter Quarter:",
					address.getQuarterQuarter(), false, "Quarter:",
					address.getQuarter(), false);
			generate2Columns(table, "Section:", address.getSection(), false,
					null, null, false);
			generate2Columns(table, "Township:", address.getTownship(), false,
					"Range:", address.getRange(), false);
			temp = County.getData().getItems()
					.getItemDesc(address.getCountyCd());
			temp1 = State.getData().getItems().getItemDesc(address.getState());
			generate2Columns(table, "County:", temp, false, "State:", temp1,
					false);
			temp = District.getData().getItems()
					.getItemDesc(address.getDistrictCd());
			generate2Columns(table, "District:", temp, false, null, null, false);
			generate2Columns(table, "Physical Address 1:",
					address.getAddressLine1(), false, "Physical Address 2:",
					address.getAddressLine2(), false);
			generate2Columns(table, "City:", address.getCityName(), false,
					"Zip:", address.getZipCode(), false);
			temp = address.getBeginDate() == null ? null : dateFormat1
					.format(address.getBeginDate());

			generate2Columns(table, "Effective Date:", temp, false, null, null,
					false);

			item1.add(table);
		}

		return item1;
	}

	private ListItem generateApis() throws DocumentException {
		
		ListItem item1 = new ListItem();
		Cell cell;

		Phrase item1Title = new Paragraph(defaultLeading, "API", boldFont);
		item1.add(item1Title);

		Table table = new Table(1);
		// float[] widths = {30, 15, 55};
		// table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("API");
		table.addCell(cell);

		for (ApiGroup apiGroup : facility.getApis()) {
			cell = createCell(defaultLeading,
					apiGroup.getApiNo() == null ? null : apiGroup.getApiNo(),
					data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	protected final FacilityService facilityBO() throws RemoteException {
		FacilityService service = null;

		try {
			service = ServiceFactory.getInstance().getFacilityService();
		} catch (ServiceFactoryException sfe) {
			logger.error("getFacilityService fails");
		}

		return service;
	}

	protected final ContinuousMonitorSearch continuousMonitorSearch() throws RemoteException {
		ContinuousMonitorSearch search = null;

		try {
			search  = (ContinuousMonitorSearch) FacesUtil.getManagedBean("continuousMonitorSearch");
		} catch (Exception e) {
			logger.error("Locate ContinuousMonitorSearch bean fails: " + e,e);
		}

		return search;
	}

	private ListItem generateVersion() throws DocumentException {

		FacilityVersion[] facilities = null;

		try {
			facilities = facilityBO().retrieveAllFacilityVersions(
					facility.getFacilityId());
		} catch (RemoteException re) {
			logger.error("Get Facility facilies fails");
		}

		ListItem item1 = new ListItem();
		Cell cell;
		String temp;

		Phrase item1Title = new Paragraph(defaultLeading, "Version", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		// float[] widths = {30, 15, 55};
		// table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Version ID");
		table.addCell(cell);
		cell = createCell("Version Start Date");
		table.addCell(cell);
		cell = createCell("Version End Date");
		table.addCell(cell);
		cell = createCell("Preserved");
		table.addCell(cell);

		for (FacilityVersion facilityVersion : facilities) {
			cell = createCell(defaultLeading,
					facilityVersion.getVersionId() == -1 ? "CURRENT"
							: facilityVersion.getFpId().toString(), data1Font);
			table.addCell(cell);
			temp = facilityVersion.getStartDate() == null ? "" : dateFormat1
					.format(facilityVersion.getStartDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			temp = facilityVersion.getEndDate() == null ? "" : dateFormat1
					.format(facilityVersion.getEndDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			cell = createCheckCell(facilityVersion.isCopyOnChange());
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateNotes(java.util.List<? extends Note> notes)
			throws DocumentException {

		ListItem item1 = new ListItem();
		Cell cell;
		String temp;

		Phrase item1Title = new Paragraph(defaultLeading, "Notes", boldFont);
		item1.add(item1Title);

		Table table = new Table(3);
		// float[] widths = {30, 15, 55};
		// table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("User Name");
		table.addCell(cell);
		cell = createCell("Date");
		table.addCell(cell);
		cell = createCell("Note");
		table.addCell(cell);

		for (Note note : notes) {
			cell = createCell(defaultLeading,
					BasicUsersDef.getUserNm(note.getUserId()), 
					data1Font);
			table.addCell(cell);
			temp = note.getDateEntered() == null ? null : dateFormat1
					.format(note.getDateEntered());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, note.getNoteTxt(), data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generatePERDueDates() throws DocumentException {

		ListItem item1 = new ListItem();
		Cell cell;

		Phrase item1Title = new Paragraph(defaultLeading, "PER Due Dates",
				boldFont);
		item1.add(item1Title);

		Table table = new Table(2);
		table.setWidth(50);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Due Date");
		table.addCell(cell);
		cell = createCell("Effective Date");
		table.addCell(cell);

		item1.add(table);

		return item1;
	}

	private ListItem generateEmiReptCategory() throws DocumentException {

		ListItem item1 = new ListItem();
		Cell cell;
		String temp;

		Phrase item1Title = new Paragraph(defaultLeading,
				"Yearly Emissions Reporting Category", boldFont);
		item1.add(item1Title);

		Table table = null;

		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			table = new Table(5);
			float[] widths = { 10, 15, 10, 30, 35 };
			table.setWidths(widths);
		} else {
			table = new Table(4);
			float[] widths = { 17, 25, 18, 40 };
			table.setWidths(widths);
		}
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Year");
		table.addCell(cell);
		cell = createCell("Category");
		table.addCell(cell);
		cell = createCell("Enabled");
		table.addCell(cell);
		cell = createCell("Status");
		table.addCell(cell);
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			cell = createCell("Comment");
			table.addCell(cell);
		}

		ReportProfileBase reportProfile = (ReportProfileBase) FacesUtil
				.getManagedBean("reportProfile");
		ArrayList<EmissionsRptInfo> emRptInfo = reportProfile
				.getYearlyEmissionsReportInfo(facility.getFpId());

		for (EmissionsRptInfo rptInfo : emRptInfo) {
			temp = rptInfo.getYear() == null ? null : rptInfo.getYear()
					.toString();
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			temp = "";
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			cell = createCheckCell(rptInfo.getReportingEnabled());
			table.addCell(cell);
			temp = ReportStatusDef.getData().getItems()
					.getItemDesc(rptInfo.getState());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				cell = createCell(defaultLeading, rptInfo.getComment(),
						data1Font);
				table.addCell(cell);
			}
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateContacts() throws DocumentException {
		
		ListItem item1 = new ListItem();
		Cell cell;
		String temp;
		String temp1;

		Phrase item1Title = new Paragraph(defaultLeading, "Contacts", boldFont);
		item1.add(item1Title);

		Table table = new Table(6);
		// float[] widths = {10, 10, 10, 30, 40};
		// table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Contact Type");
		table.addCell(cell);
		cell = createCell("Contact Person");
		table.addCell(cell);
		cell = createCell("Phone Number");
		table.addCell(cell);
		cell = createCell("Email");
		table.addCell(cell);
		cell = createCell("Start Date");
		table.addCell(cell);
		cell = createCell("End Date");
		table.addCell(cell);

		java.util.List<Contact> allContacts;
		java.util.List<ContactUtil> facilityContacts = new ArrayList<ContactUtil>();
		try {
			FacilityService facilityBO = ServiceFactory.getInstance()
					.getFacilityService();
			// TODO figure out how to determine whether facility is from
			// Facility Change task or current facility on Portal
			// boolean useStaging = false;
			// if (!CompMgr.getAppName().equals(CommonConst.INTERNAL_APP) &&
			// facility.getVersionId() == -1) {
			// if (facility.isStaging()) {
			// useStaging = true;
			// }
			if (CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
				allContacts = new ArrayList<Contact>();
			} else {
				allContacts = facilityBO.retrieveFacilityContacts(
					facility.getFacilityId(), facility.isStaging());
			}
		} catch (Exception e) {
			throw new DocumentException(e.getMessage());
		}

		ContactUtil tempCU;
		for (Contact tempContact : allContacts) {
			if (tempContact.getContactTypes().isEmpty()) {
				facilityContacts.add(new ContactUtil(tempContact));
				continue;
			}

			for (ContactType tempCT : tempContact.getContactTypes()) {
				tempCU = new ContactUtil(tempContact, tempCT);
				facilityContacts.add(tempCU);
			}
		}

		PhoneNumberConverter phoneConvert = new PhoneNumberConverter();

		for (ContactUtil cont : facilityContacts) {
			temp = ContactTypeDef.getData().getItems()
					.getItemDesc(cont.getContactType().getContactTypeCd());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, cont.getContact().getName(),
					data1Font);
			table.addCell(cell);
			temp = phoneConvert.tryFormatPhoneNumber(cont.getContact()
					.getPhoneNo());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, cont.getContact()
					.getEmailAddressTxt(), data1Font);
			table.addCell(cell);
			temp = cont.getContactType().getStartDate() == null ? null
					: dateFormat1.format(cont.getContactType().getStartDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			temp = cont.getContactType().getEndDate() == null ? null
					: dateFormat1.format(cont.getContactType().getEndDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		for (Contact tempCont : allContacts) {
			table = generateSubTable("Contact Detail For : " + tempCont.getName());
			item1.add(table);

			table = new Table(4);
			table.setBorder(0);
			table.setPadding(2);
			table.setWidth(98);
			temp = tempCont.getTitleCd() == null ? null : contactTitles
					.get(tempCont.getTitleCd());
			generate2Columns(table, "Prefix:", temp, false, "First Name:",
					tempCont.getFirstNm(), false);
			generate2Columns(table, "Middle Name:", tempCont.getMiddleNm(),
					false, "Last Name:", tempCont.getLastNm(), false);
			generate2Columns(table, "Suffix:", tempCont.getSuffixCd(), false,
					null, null, false);
			generate2Columns(table, "Company Title:",
					tempCont.getCompanyTitle(), false,
					"Contact's Company Name:", tempCont.getCompanyName(),
					false);
			generate2Columns(table, "", "", false, "", "", false);

			generate2Columns(table, "Address 1:", tempCont.getAddress()
					.getAddressLine1(), false, null, null, false);
			generate2Columns(table, "Address 2:", tempCont.getAddress()
					.getAddressLine2(), false, null, null, false);
			generate2Columns(table, "City:", tempCont.getAddress()
					.getCityName(), false, "Zip Code:", tempCont.getAddress()
					.getZipCode(), false);
			temp = State.getData().getItems()
					.getItemDesc(tempCont.getAddress().getState());
			generate2Columns(table, "State:", temp, false, null, null, false);
			generate2Columns(table, "", "", false, "", "", false);

			temp = phoneConvert.tryFormatPhoneNumber(tempCont.getPhoneNo());
			generate2Columns(table, "Work Phone No:", temp, false,
					"Secondary Phone No.:", tempCont.getPhoneExtensionVal(),
					false);
			temp = phoneConvert.tryFormatPhoneNumber(tempCont
					.getSecondaryPhoneNo());
			generate2Columns(table, "Address 2:", temp, false,
					"Secondary Ext. No.:", tempCont.getSecondaryExtensionVal(),
					false);
			temp = phoneConvert.tryFormatPhoneNumber(tempCont
					.getMobilePhoneNo());
			temp1 = phoneConvert.tryFormatPhoneNumber(tempCont.getPagerNo());
			generate2Columns(table, "Mobile Phone No.:", temp, false,
					"Pager No.:", temp1, false);
			temp = phoneConvert.tryFormatPhoneNumber(tempCont.getFaxNo());
			generate2Columns(table, "Fax No:", temp, false, "Pager PIN No.:",
					tempCont.getPagerPinNo(), false);
			generate2Columns(table, "", "", false, "", "", false);

			generate2Columns(table, "Email:", tempCont.getEmailAddressTxt(), false, 
					"Secondary Email:", tempCont.getEmailAddressTxt2(), false);
			generate2Columns(table, "Email Pager Address:",
					tempCont.getEmailPagerAddress(), false, null, null, false);
			item1.add(table);
		}

		return item1;
	}

	private Table generateTable(String tableHeader, DefSelectItems defCodes,
			String[] codes, Class<? extends Object> defClass)
			throws DocumentException {
		
		Cell cell;
		String temp;

		Table table = new Table(1);
		table.setWidth(98);
		table.setBorder(0);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		// cell = createCell(tableHeader);
		// table.addCell(cell);

		for (String code : codes) {
			temp = defCodes.getItemDesc(code);
			if (defClass.equals(SICDef.class)
					|| defClass.equals(NAICSDef.class)) {
				temp = code + " " + temp;
			}
			cell = createCell(0, temp, data1Font);
			cell.setBorder(0);
			table.addCell(cell);
		}

		return table;
	}

	private void generate2Columns(Table table, String name1, String value1,
			boolean attr1, String name2, String value2, boolean attr2) {
		
		boolean val;
		Cell cell;

		cell = createCell(defaultLeading, name1, normal1Font);
		cell.setBorder(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell(cell);

		if (attr1) {
			val = value1.equals("true") ? true : false;
			cell = createCheckCell(val);
		} else {
			cell = createCell(defaultLeading, value1, data1Font);
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

	/* START : Emission UNit */

	private void generateEmissionUnit(EmissionUnit eu) throws DocumentException {
		
		document.newPage();

		Table titleTable = createTitleTable(
				"Emission Unit : " + eu.getEpaEmuId(), "");
		document.add(titleTable);

		List sections = new List(false, 20);
		sections.add(generateEmissionUnitData(eu));
		if (eu.isNonEngineReplacement()) {
			sections.add(generateEmissionUnitReplacementData(eu));
		} else if (eu.isEngineReplacement()) {
			sections.add(generateEngineEmissionUnitReplacementData(eu));
		}
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			sections.add(generateEuPermittedEmissions(eu));
		}
		sections.add(generateProcesses(eu));

		document.add(sections);
	}

	private ListItem generateEmissionUnitReplacementData(EmissionUnit eu)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
		
		Phrase item1Title = new Paragraph(defaultLeading,
				"Serial Number Tracking", boldFont);
		item1.add(item1Title);
		
		Cell cell;
		String temp;

		Table table = new Table(3);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Manufacturer Name");
		table.addCell(cell);
		cell = createCell("Serial Number");
		table.addCell(cell);
		cell = createCell("Effective Date");
		table.addCell(cell);

		for (EmissionUnitReplacement EmissionUnitReplacement : eu
				.getEmissionUnitReplacements()) {

			temp = EmissionUnitReplacement.getManufacturerName();
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);

			temp = EmissionUnitReplacement.getSerialNumber();
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);

			temp = toDateFormat1(EmissionUnitReplacement
					.getSerialNumberEffectiveDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}
	
	private ListItem generateEngineEmissionUnitReplacementData(EmissionUnit eu)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
		
		Phrase item1Title = new Paragraph(defaultLeading,
				"Serial Number Tracking", boldFont);
		item1.add(item1Title);
		
		Cell cell;
		String temp;

		Table table = new Table(8);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Serial Number");
		table.addCell(cell);
		
		cell = createCell("Manufacturer Name");
		table.addCell(cell);
		
		cell = createCell("Construction/Installation Commencement Date");
		table.addCell(cell);
		
		cell = createCell("Operation Commencement/Start-up Date");
		table.addCell(cell);
		
		cell = createCell("Order Date");
		table.addCell(cell);
		
		cell = createCell("Manufacture Date");
		table.addCell(cell);
		
		cell = createCell("Shutdown Date");
		table.addCell(cell);
		
		cell = createCell("Removal Date");
		table.addCell(cell);

		for (EmissionUnitReplacement EmissionUnitReplacement : eu
				.getEmissionUnitReplacements()) {

			temp = EmissionUnitReplacement.getSerialNumber();
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);

			temp = EmissionUnitReplacement.getManufacturerName();
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			
			temp = toDateFormat1(EmissionUnitReplacement.getInstallDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			
			temp = toDateFormat1(EmissionUnitReplacement.getOperationStartupDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			
			temp = toDateFormat1(EmissionUnitReplacement.getOrderDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			
			temp = toDateFormat1(EmissionUnitReplacement.getManufactureDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			
			temp = toDateFormat1(EmissionUnitReplacement.getShutdownDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			
			temp = toDateFormat1(EmissionUnitReplacement.getRemovalDate());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateEmissionUnitData(EmissionUnit eu) throws DocumentException {

		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading, "Emission Unit Information", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp;
		String temp1;
		generate2Columns(table, "AQD Emissions Unit ID:", eu.getEpaEmuId(), false, null, null, false);

		generate2Columns(table, "Emission Unit Type:", eu.getEmissionUnitTypeName(), false, null, null, false);

		/* START : Emission Unit Type Detail */
		if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.ABS)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp,
					false, null, null, false);

			temp = AbsSubstrateBlastedTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getSubstrateBlasted());
			generate2Columns(table, "Substrate being Blasted:", temp, false, null,
					null, false);

			if (temp.equalsIgnoreCase("Other")) {
				temp = toString(eu.getEmissionUnitType().getSubstrateBlastedOther());
				generate2Columns(table, "Substrate being Blasted (Other):", temp, false, null, null, false);
			}

			
			
			temp = AbsSubstrateRemovedTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getSubstrateRemoved());
			generate2Columns(table, "Substrate being Removed:", temp, false, null,
					null, false);
			
			if (temp.equalsIgnoreCase("Other")) {
				temp = toString(eu.getEmissionUnitType().getSubstrateRemovedOther());
				generate2Columns(table, "Substrate being Removed (Other):", temp, false, null,
						null, false);
			}

			
			if (temp.equalsIgnoreCase("Leaded Paint")) {
				temp = AbsLeadConcentrationPctTypeDef.getData().getItems()
						.getItemDesc(eu.getEmissionUnitType().getConcentrationOfLeadPct());
				generate2Columns(table, "Percentage Concentration Of Lead in Paint:", temp, false, null, null, false);
			}
			
			temp = toString(eu.getEmissionUnitType().getMaxAnnualUsageAbs());
			generate2Columns(table, "Maximum Annual Usage (tons/yr):", temp,
					false, null, null, false);
			
			if (eu.getEmissionUnitType().getBlastMediaCARBCertifiedFlag() != null) {
				temp = eu.getEmissionUnitType().getBlastMediaCARBCertifiedFlag().equals("Y") ? "Yes"
						: "No";
			generate2Columns(table, "Are Blast Media CARB Certified:", temp, false, null,
					null, false);
			}
			
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.ACB)) {

			temp = toString(eu.getEmissionUnitType().getFuelType());
			generate2Columns(table, "Type of Fuel:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxTimeOperated());
			generate2Columns(table, "Maximum Time Operated (hrs/yr):", temp, false,
					null, null, false);
		
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.AMN)) {

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getDesignCapacity());
			generate2Columns(table, "Design Capacity (MMscf/hr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.APT)) {

			temp = MaterialProducedDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getProducedMaterialType());
			generate2Columns(table, "Material Produced:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxThroughput());
			temp1 = AptMaxThroughputUnitsDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Throughput:", temp, false, "Units:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.BAK)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (tons/yr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.BGM)) {

			temp = toString(eu.getEmissionUnitType().getMaterialType());
			generate2Columns(table, "Name and Type of Material:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (tons/yr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.BOL)) {

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getHeatInputRating());
			generate2Columns(table, "Heat Input Rating (MMBtu/hr):", temp, false, null, null, false);

			temp = PrimaryFuelTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPrimaryFuelType());
			temp1 = SecondaryFuelTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getSecondaryFuelType());
			generate2Columns(table, "Primary Fuel Type:", temp, false, "Secondary Fuel Type:", temp1, false);

			temp = eu.getEmissionUnitType().getModelNameNumber();
			generate2Columns(table, "Model Name and Number:", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.BVC)) {

			temp = EventTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getEventType());
			generate2Columns(table, "Type of Event:", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.CCU)) {

			temp = toString(eu.getEmissionUnitType().getChargeRate());
			generate2Columns(table, "Charge Rate (barrels/hr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.CKD)) {

			temp = CkdUnitTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitType());
			generate2Columns(table, "Unit Type:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualThroughput());
			temp1 = CkdMaxAnnualThroughputUnitsDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false, "Units:", temp1, false);

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getHeatInputRating());
			generate2Columns(table, "Heat Input Rating (MMBtu/hr):", temp, false, null, null, false);

			temp = PrimaryFuelTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPrimaryFuelType());
			temp1 = SecondaryFuelTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getSecondaryFuelType());
			generate2Columns(table, "Primary Fuel Type:", temp, false, "Secondary Fuel Type:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.CMX)) {

			temp = BatchingTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getBatchingType());
			generate2Columns(table, "Type of Batching:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxProductionRate());
			temp1 = CmxMaxProductionRateUnitsDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Production Rate:", temp, false, "Units:", temp1, false);

			temp = PowerSourceTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPowerSource());
			generate2Columns(table, "Power Source:", temp, false, null, null, false);
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.COT)) {

			temp = CotCoatingOperationTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getTypeOfCoatingOperation());
			generate2Columns(table, "Type of Coating Operation:", temp,
					false, null, null, false);

			temp = CotMaterialBeingCoatedTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getTypeOfMaterialBeingCoated());
			generate2Columns(table, "Type of Material Being Coated:", temp,
					false, null, null, false);

			
			if(temp.equalsIgnoreCase("Other")){
				temp = toString(eu.getEmissionUnitType().getTypeOfMaterialBeingCoatedOther());
				generate2Columns(table, "Type of Material Being Coated (Other):", temp,
					false, null, null, false);
			}


			temp = toString(eu.getEmissionUnitType().getTypeOfProductBeingCoated());
			generate2Columns(table, "Type of Product Being Coated:", temp,
					false, null, null, false);


			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp,
					false, null, null, false);


			temp = toString(eu.getEmissionUnitType().getMaxAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (gal/yr):", temp, false,
					null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxPctVOC());
			generate2Columns(table, "Maximum % VOC:", temp, false,
					null, null, false);						

			temp = CotApplicationMethodTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getApplicationMethod());
			generate2Columns(table, "Application Method:", temp, false,
					null, null, false);						

			if(temp.equalsIgnoreCase("Other")){
				temp = toString(eu.getEmissionUnitType().getApplicationMethodOther());
				generate2Columns(table, "Application Method (Other):", temp, false,
					null, null, false);	
			}
			
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.CSH)) {

			temp = CshUnitTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitType());
			generate2Columns(table, "Type of Unit:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualThroughput());
			temp1 = CshMaxAnnualThroughputUnitDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false, "Units:", temp1, false);

			temp = eu.getEmissionUnitType().getModelNameNumber();
			generate2Columns(table, "Model Name and Number:", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.CTW)) {

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getDriftRate());
			temp1 = toString(eu.getEmissionUnitType().getDissolvedSolidsTotal());
			generate2Columns(table, "Drift Rate (%):", temp, false, "Total Dissolved Solids (ppm):", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.DHY)) {

			temp = DehydrationTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getDehydrationType());
			generate2Columns(table, "Dehydration Type:", temp, false, null, null, false);

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getDesignCapacity());
			generate2Columns(table, "Design Capacity (MMscf/day):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.DIS)) {

			temp = eu.getEmissionUnitType().getMaterialDescription();
			generate2Columns(table, "Material Description:", temp, false, null,
					null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualThroughput());
			temp1 = MaxAnnualThroughputUnitDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false,
					"Units:", temp1, false);

		}	else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.DRY)) {

			temp = toString(eu.getEmissionUnitType().getMaterialType());
			generate2Columns(table, "Type of Material:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualUsage());
			generate2Columns(table, "Maximum Annual Usage (gals/yr):", temp, false,
					null, null, false);	
			
			temp = toString(eu.getEmissionUnitType().getOperationType());
			generate2Columns(table, "Type of Operation:", temp,
					false, null, null, false);

	
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.EGU)) {

			temp = EguUnitTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitType());
			generate2Columns(table, "Unit Type:", temp, false, null, null, false);

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getHeatInputRating());
			generate2Columns(table, "Heat Input Rating (MMBtu/hr):", temp, false, null, null, false);

			temp = PrimaryFuelTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPrimaryFuelType());
			temp1 = SecondaryFuelTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getSecondaryFuelType());
			generate2Columns(table, "Primary Fuel Type:", temp, false, "Secondary Fuel Type:", temp1, false);

			temp = eu.getEmissionUnitType().getManufacturerName();
			temp1 = eu.getEmissionUnitType().getModelNameNumber();
			generate2Columns(table, "Manufacturer Name:", temp, false, "Model Name and Number:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.ENG)) {

			temp = toString(eu.getEmissionUnitType().getNamePlateRating());
			temp1 = NamePlateRatingUnitsDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Name Plate Rating:", temp, false, "Units:", temp1, false);

			temp = toString(eu.getEmissionUnitType().getSiteRating());
			temp1 = SiteRatingUnitsDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getSiteRatingUnitCd());
			generate2Columns(table, "Site Rating:", temp, false, "Units:", temp1, false);

			temp = PrimaryFuelTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPrimaryFuelType());
			temp1 = SecondaryFuelTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getSecondaryFuelType());
			generate2Columns(table, "Primary Fuel Type:", temp, false, "Secondary Fuel Type:", temp1, false);

			temp = eu.getEmissionUnitType().getModelNameNumber();
			temp1 = EngineTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getEngineType());
			generate2Columns(table, "Model Name and Number:", temp, false, "Engine:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.FAT)) {

			temp = FatUnitTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitType());
			generate2Columns(table, "Unit Type:", temp,
					false, null, null, false);
			
			if(temp.equalsIgnoreCase("Other")){
				temp = toString(eu.getEmissionUnitType().getUnitTypeOther());
				generate2Columns(table, "Unit Type (Other):", temp,
					false, null, null, false);
			}

			temp = toString(eu.getEmissionUnitType().getMaxProductionRate());
			generate2Columns(table, "Maximum Production Rate (tons/yr):", temp, false,
					null, null, false);			

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.FLR)) {

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getMaxDesignCapacity());
			temp1 = FlareDesignCapacityUnitsDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Design Capacity:", temp, false, "Units :", temp1, false);

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getMinDesignCapacity());
			temp1 = FlareDesignCapacityUnitsDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getCapacityUnit());
			generate2Columns(table, "Minimum Design Capacity:", temp, false, "Units :", temp1, false);

			temp = toString(eu.getEmissionUnitType().getPilotGasVolume());
			generate2Columns(table, "Pilot Gas Volume (scf/min):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.FUG)) {

			// No Detail.

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.GIN)) {

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (tons/yr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.GRI)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (tons/yr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.HET)) {

			temp = FiringTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getFiringType());
			generate2Columns(table, "Firing Type:", temp, false, null, null, false);

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getHeatInputRating());
			temp1 = UnitTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Heat Input Rating:", temp, false, "Units:", temp1, false);

			temp = PrimaryFuelTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPrimaryFuelType());
			temp1 = SecondaryFuelTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getSecondaryFuelType());
			generate2Columns(table, "Primary Fuel Type:", temp, false, "Secondary Fuel Type:", temp1, false);

			temp = toString(eu.getEmissionUnitType().getFuelHeatContent());
			generate2Columns(table, "Heat Content of Fuel (BTU/scf):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.HMA)) {

			temp = PlantTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPlantType());
			generate2Columns(table, "Plant Type:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxProductionRate());
			temp1 = HmaMaxProductionRateUnitsDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Plant Type:", temp, false, "Units:", temp1, false);

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getMaxBurnerDesignRate());
			generate2Columns(table, "Maximum Burner Design Rate (MMBtu/hr):", temp, false, null, null, false);

			temp = PowerSourceTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPowerSource());
			temp1 = FuelTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getFuelType());
			generate2Columns(table, "Power Source:", temp, false, "Fuel Type:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.INC)) {

			temp = IncineratorTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getIncineratorType());
			temp1 = BurnerTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getBurnerSystem());
			generate2Columns(table, "Incinerator Type:", temp, false, "Burner System:", temp1, false);

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getMaxDesignCapacity());
			temp1 = IncineratorDesignCapacityUnitsDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Design Capacity:", temp, false, "Units:", temp1, false);

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getMinDesignCapacity());
			temp1 = IncineratorDesignCapacityUnitsDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getCapacityUnit());
			generate2Columns(table, "Minimum Design Capacity:", temp, false, "Units:", temp1, false);

			temp = toString(eu.getEmissionUnitType().getPilotGasVolume());
			generate2Columns(table, "Pilot Gas Volume (scf/min):", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getPrimaryChamberOpTemp());
			generate2Columns(table, "Primary Chamber Operating Temperature (F):", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getSecondaryChamberOpTemp());
			generate2Columns(table, "Secondary Chamber Operating Temperature (F):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.LUD)) {

			temp = MaterialTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getMaterialType());
			generate2Columns(table, "Type of Material:", temp, false, null, null, false);

			temp = eu.getEmissionUnitType().getMaterialDescription();
			generate2Columns(table, "Material Description:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualThroughput());
			temp1 = LudMaxAnnualThroughputUnitsDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false, "Units:", temp1, false);
			
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.MAC)) {

			temp = toString(eu.getEmissionUnitType().getMaxTimeOperated());
			generate2Columns(table, "Maximum Time Operated (hrs/yr):", temp, false,
					null, null, false);

			temp = toString(eu.getEmissionUnitType().getAmtOfMaterialRemoved());
			generate2Columns(table, "Amount of Material Removed (lbs/yr):", temp, false, null, null, false);


		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.MAT)) {

			temp = eu.getEmissionUnitType().getMaterialDescription();
			generate2Columns(table, "Material Description:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			temp1 = MaxAnnualThroughputUnitDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false, "Units:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.MET)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			temp1 = MaxAnnualThroughputUnitDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false,
					"Units:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.MIL)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (tons/yr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.MIX)) {

			temp = eu.getEmissionUnitType().getMaterialDescription();
			generate2Columns(table, "Material Description:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualThroughput());
			temp1 = MaxAnnualThroughputUnitDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false, "Units:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.MLD)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (lbs/yr):", temp, false,
					null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.OEP)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			temp1 = MaxAnnualThroughputUnitDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false,
					"Units:", temp1, false);
			
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.ORD)) {

			temp = OrdExplosiveTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getTypeOfExplosive());
			generate2Columns(table, "Type of Explosive:", temp,
					false, null, null, false);

			if(temp.equalsIgnoreCase("Other")){
				temp = toString(eu.getEmissionUnitType().getTypeOfExplosiveOther());
				generate2Columns(table, "Type of Explosive (Other):", temp,
					false, null, null, false);
			}

			temp = toString(eu.getEmissionUnitType().getMaxNumberOfRoundsDetonated());
			generate2Columns(table, "Maximum Number of Rounds Detonated (#/yr):", temp, false,
					null, null, false);	

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.OZG)) {

			temp = toString(eu.getEmissionUnitType().getMaxOzoneGenerationRate());
			generate2Columns(table, "Maximum Ozone Generation Rate:", temp, false,
					null, null, false);	

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.PAM)) {

			temp = PamEquipTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getEquipmentType());
			generate2Columns(table, "Type of Equipment:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (tons/yr):", temp, false,
					null, null, false);
			
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.PEL)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (tons/yr):", temp, false,
					null, null, false);
					
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.PNE)) {

			temp = EquipTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getEquipmentType());
			generate2Columns(table, "Type of Equipment:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getBleedRate());
			generate2Columns(table, "Bleed rate (cu. ft/hr):", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getGasConsumptionRate());
			generate2Columns(table, "Gas Consumption Rate (cu. ft/hr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.PRN)) {

			temp = PrnPressTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getPressType());
			generate2Columns(table, "Press Type:", temp,
					false, null, null, false);

			if(temp.equalsIgnoreCase("Other")){
				temp = toString(eu.getEmissionUnitType().getPressTypeOther());
				generate2Columns(table, "Press Type (Other):", temp,
						false, null, null, false);
			}

			temp = PrnSubstrateFeedMethodTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getSubstrateFeedMethod());
			generate2Columns(table, "Substrate Feed Method:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getImpressionArea());
			generate2Columns(table, "Impression Area (sq in):", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualUsage());
			temp1 = MaxAnnualThroughputUnitDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Usage:", temp, false,
					"Units:", temp1, false);
			
			temp = toString(eu.getEmissionUnitType().getMaxPctVOC());
			generate2Columns(table, "Maximum % VOC:", temp, false,
					null, null, false);						

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.REM)) {

			temp = RemContaminantTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getTypeOfContaminantBeingTreated());
			generate2Columns(table, "Type of Contaminant being Treated:", temp,
					false, null, null, false);
			
			if(temp.equalsIgnoreCase("Other")) {
				temp = toString(eu.getEmissionUnitType().getOtherTypeOfContaminantBeingTreated());
				generate2Columns(table, "Type of Contaminant being Treated (Other):", temp, false,
						null, null, false);					
			}
			
			temp = RemContaminatedMaterialTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getContaminatedMaterial());
			generate2Columns(table, "Contaminated Material:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getVOCEmissionRate());
			generate2Columns(table, "VOC emission rate (lbs/day):", temp, false,
					null, null, false);	
			


		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.RES)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp,
					false, null, null, false);
			
			temp = toString(eu.getEmissionUnitType().getMaxAnnualUsage());
			generate2Columns(table, "Maximum Annual Usage (lbs/yr):", temp, false,
					null, null, false);	
			
			temp = toString(eu.getEmissionUnitType().getMaxPctVOC());
			generate2Columns(table, "Maximum % VOC Content:", temp, false,
					null, null, false);	
			
			temp = toString(eu.getEmissionUnitType().getMaxPctVOC());
			generate2Columns(table, "Maximum % Styrene Content:", temp, false,
					null, null, false);						
			
			temp = toString(eu.getEmissionUnitType().getApplicationMethod());
			generate2Columns(table, "Application Method:", temp,
					false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.SEB)) {

			temp = SebUnitTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitType());
			generate2Columns(table, "Unit Type:", temp, false, null, null, false);

			temp = eu.getEmissionUnitType().getUnitDesc();
			generate2Columns(table, "Unit Description:", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.SEM)) {

			temp = SemEquipTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getEquipmentType());
			generate2Columns(table, "Equipment Type:", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.SEP)) {

			temp = SepVesselTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getVesselType());
			temp1 = eu.getEmissionUnitType().isVesselHeated() ? "Yes" : "No";
			generate2Columns(table, "Type Of Vessel:", temp, false, "is Vessel Heated:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.SRU)) {

			temp = toString(eu.getEmissionUnitType().getMaxAnnualThroughput());
			temp1 = SruMaxAnnualThroughputUnitsDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput:", temp, false, "Units:", temp1, false);
			
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.STZ)) {

			temp = toString(eu.getEmissionUnitType().getFacilityType());
			generate2Columns(table, "Facility Type:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaterialType());
			generate2Columns(table, "Material Type:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualUsage());
			generate2Columns(table, "Maximum Annual Usage (lbs/yr):", temp, false,
					null, null, false);	


		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.SVC)) {

			temp = SvcEquipTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getEquipmentType());
			generate2Columns(table, "Equipment Type:", temp, false, null, null, false);

			if(temp.equalsIgnoreCase("Other")){
				temp = eu.getEmissionUnitType().getEquipmentTypeOther();
				generate2Columns(table, "Equipment Type (Other):", temp, false, null, null,
						false);
			}

			temp = toString(eu.getEmissionUnitType().getMaxAnnualUsage());
			generate2Columns(table, "Maximum Annual Throughput (lbs/yr):", temp, false,
					null, null, false);
			
			temp = toString(eu.getEmissionUnitType().getSolventType());
			generate2Columns(table, "Solvent Type:", temp, false,
					null, null, false);	
			
			} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.SVU)) {

			temp = toString(eu.getEmissionUnitType().getNameAndTypeOfMaterial());
			generate2Columns(table, "Name and Type of Material:", temp,
					false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAnnualThroughput());
			temp1 = MaxAnnualThroughputUnitDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Annual Throughput (lbs/yr):", temp, false,
					null, null, false);
			
			temp = toString(eu.getEmissionUnitType().getMaxPctVOC());
			generate2Columns(table, "Maximum % VOC:", temp, false,
					null, null, false);						
			
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.TAR)) {

			temp = toString(eu.getEmissionUnitType().getMaximumAnnualThroughput());
			generate2Columns(table, "Maximum Annual Throughput (tons/yr):", temp, false,
					null, null, false);

			temp = toString(eu.getEmissionUnitType().getHeatInputRating());
			generate2Columns(table, "Heat Input Rating (MMBtu/hr):", temp, false,
					null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.TGT)) {

			temp = toVALUE_FORMAT1(eu.getEmissionUnitType().getHeatInputRating());
			generate2Columns(table, "Heat Input Rating (MMBtu/hr):", temp, false, null, null, false);
		
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.TIM)) {

			temp = toString(eu.getEmissionUnitType().getMaxAmtOfRubberRemoved());
			generate2Columns(table, "Maximum Amount of Rubber Removed (lbs/yr):", temp, false, null, null, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.TKO)) {

			temp = toString(eu.getEmissionUnitType().getMaterialType());
			generate2Columns(table, "Material Type:", temp,
					false, null, null, false);

			if (eu.getEmissionUnitType().getPlatingLineFlag() != null) {
				temp = eu.getEmissionUnitType().getPlatingLineFlag().equals("Y") ? "Yes"
						: "No";
			generate2Columns(table, "Are Blast Media CARB Certified:", temp, false, null,
					null, false);
			}
			
			
			temp = TkoPlatingTypeDef.getData().getItems()
					.getItemDesc((eu.getEmissionUnitType().getPlatingType()));
			generate2Columns(table, "Type of Plating:", temp,
					false, null, null, false);

			if(temp.equalsIgnoreCase("Other")){
				temp = toString(eu.getEmissionUnitType().getPlatingTypeOther());
				generate2Columns(table, "Type of Plating (Other):", temp,
						false, null, null, false);
			}


			temp = TkoMetalTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getMetalType());
			generate2Columns(table, "Type of Metal:", temp,
					false, null, null, false);

		
			if(temp.equalsIgnoreCase("Other")){
				temp = toString(eu.getEmissionUnitType().getMetalTypeOther());
				generate2Columns(table, "Type of Metal (Other):", temp,
						false, null, null, false);
			}

			
			temp = toString(eu.getEmissionUnitType().getCapacity());
			generate2Columns(table, "Tank Capacity (gal):", temp, false,
					null, null, false);	
			
			temp = toString(eu.getEmissionUnitType().getMaxAnnualUsage());
			generate2Columns(table, "Maximum Annual Usage (lbs/yr):", temp, false,
					null, null, false);			
			
			temp = toString(eu.getEmissionUnitType().getConcentrationPct());
			generate2Columns(table, "Concentration %:", temp, false,
					null, null, false);						

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.TNK)) {

			temp = TnkMaterialStoredTypeDef.getData().getItems()
					.getItemDesc(eu.getEmissionUnitType().getMaterialTypeStored());
			generate2Columns(table, "Material Type:", temp, false, null, null, false);

			temp = eu.getEmissionUnitType().getMaterialTypeStoredDesc();
			generate2Columns(table, "Description of Material Stored:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getCapacity());
			temp1 = TnkCapacityUnitDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getCapacityUnit());
			generate2Columns(table, "Capacity:", temp, false, "Units:", temp1, false);

			temp = toString(eu.getEmissionUnitType().getMaxThroughput());
			temp1 = TnkMaxThroughputUnitDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getUnitCd());
			generate2Columns(table, "Maximum Throughput:", temp, false, "Units:", temp1, false);

		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.VNT)) {
			
			// No Detail.
			
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.WEL)) {

			temp = toString(eu.getEmissionUnitType().getWeldingProcess());
			generate2Columns(table, "Welding Process:", temp, false, null, null, false);

			temp = toString(eu.getEmissionUnitType().getMaxAmtOfElectrodeConsumed());
			generate2Columns(table, "Maximum Amount of Electrode Consumed (lbs/yr):", temp, false, null, null, false);
		} else if (eu.getEmissionUnitTypeCd().equals(EmissionUnitTypeDef.WWE)) {

			temp = toString(eu.getEmissionUnitType().getMaxAnnualWoodDustGenerated());
			generate2Columns(table, "Maximum Annual Wood Dust Generated (tons/yr):", temp, false, null, null, false);

			temp = WweEquipTypeDef.getData().getItems().getItemDesc(eu.getEmissionUnitType().getEquipmentType());
			generate2Columns(table, "Equipment Type:", temp, false, null, null, false);
			
			String powerRatingFlag = eu.getEmissionUnitType().getPowerRatingFlag();
			String powerRatingFlagStr="";
			if(powerRatingFlag.equalsIgnoreCase("Y")){ powerRatingFlagStr= "Yes";} else if(powerRatingFlag.equalsIgnoreCase("N")){ powerRatingFlagStr= "No";}
			generate2Columns(table, "Power Rating (hp) greater than 5:", powerRatingFlagStr, false, null,null, false);
	

		}
		
		/* End : Emission Unit Type Detail */

		generate2Columns(table, "AQD Description:", eu.getEuDesc(), false, null, null, false);
		generate2Columns(table, "Company Equipment ID:", eu.getCompanyId(), false, null, null, false);
		generate2Columns(table, "Company Equipment Description:", eu.getRegulatedUserDsc(), false, null, null, false);
		temp = EuOperatingStatusDef.getData().getItems().getItemDesc(eu.getOperatingStatusCd());
		if (eu.getOperatingStatusCd().equals(OperatingStatusDef.SD)) {
			temp1 = eu.getEuShutdownDate() == null ? null : dateFormat1.format(eu.getEuShutdownDate());
			generate2Columns(table, "Operating Status:", temp, false, "Shutdown Date:", temp1, false);
			if (facility.getPermitClassCd() != null && facility.getPermitClassCd().equals(PermitClassDef.TV)) {
				temp1 = eu.getEuShutdownNotificationDate() == null ? null
						: dateFormat1.format(eu.getEuShutdownNotificationDate());
				generate2Columns(table, "Shutdown Notification Date:", temp1, false, null, null, false);
			}
		} else {
			generate2Columns(table, "Operating Status:", temp, false, null, null, false);
		}

		generate2Columns(table, "", "", false, "", "", false);

		temp = toDateFormat1(eu.getEuInitInstallDate());
		generate2Columns(table, "Initial Construction Commencement Date:", temp, false, null, null, false);
		temp = toDateFormat1(eu.getEuInitStartupDate());
		generate2Columns(table, "Initial Operation Commencement Date:", temp, false, null, null, false);
		temp = toDateFormat1(eu.getEuInstallDate());
		generate2Columns(table, "Most Recent Construction/Modification Commencement Date:", temp, false, null, null,
				false);
		temp = toDateFormat1(eu.getEuStartupDate());
		generate2Columns(table, "Most Recent Operation Commencement Date:", temp, false, null, null, false);

		item1.add(table);

		return item1;
	}

	private ListItem generateEuPermittedEmissions(EmissionUnit eu)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
		Cell cell;
		String temp;

		Phrase item1Title = new Paragraph(defaultLeading,
				"Permitted Emissions", boldFont);
		item1.add(item1Title);

		Table table = new Table(6);
		float[] widths = { 27, 27, 27, 27, 27, 27};
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Pollutant");
		table.addCell(cell);
		cell = createCell("Potential Emissions (Lbs/hour)");
		table.addCell(cell);
		cell = createCell("Potential Emissions (Tons/Year)");
		table.addCell(cell);
		cell = createCell("Allowable Emissions (Lbs/Hour)");
		table.addCell(cell);
		cell = createCell("Allowable Emissions (Tons/Year");
		table.addCell(cell);
		cell = createCell("Comments");
		table.addCell(cell);

		for (EuEmission emission : eu.getEuEmissions()) {
			temp = EuPollutantDef.getData().getItems()
					.getItemDesc(emission.getPollutantCd());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);

			String potentialEmissionsLbsHourText = null;
			if (emission.getPotentialEmissionsLbsHour() != null) {
				potentialEmissionsLbsHourText = emission.getPotentialEmissionsLbsHour()
						.toString();
			}
			cell = createCell(defaultLeading, potentialEmissionsLbsHourText,
					data1Font);
			table.addCell(cell);

			String potentialEmissionsTonsYearText = null;
			if (emission.getPotentialEmissionsTonsYear() != null) {
				potentialEmissionsTonsYearText = emission.getPotentialEmissionsTonsYear()
						.toString();
			}
			cell = createCell(defaultLeading, potentialEmissionsTonsYearText,
					data1Font);
			table.addCell(cell);

			String allowableEmissionLbsHourText = null;
			if (emission.getAllowableEmissionsLbsHour() != null) {
				allowableEmissionLbsHourText = emission.getAllowableEmissionsLbsHour()
						.toString();
			}
			cell = createCell(defaultLeading, allowableEmissionLbsHourText,
					data1Font);
			table.addCell(cell);

			String allowableEmissionTonsYearText = null;
			if (emission.getAllowableEmissionsTonsYear() != null) {
				allowableEmissionTonsYearText = emission.getAllowableEmissionsTonsYear()
						.toString();
			}
			cell = createCell(defaultLeading, allowableEmissionTonsYearText,
					data1Font);
			table.addCell(cell);

			cell = createCell(defaultLeading, emission.getComment(), data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateProcesses(EmissionUnit eu)
			throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading, "Processes", boldFont);
		item1.add(item1Title);

		List sections = new List(false, 20);
		for (EmissionProcess emProc : eu.getEmissionProcesses()) {
			sections.add(generateEmissionProcess(emProc));
		}

		item1.add(sections);

		return item1;
	}

	private ListItem generateEmissionProcess(EmissionProcess emProc)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
		String temp;

		Phrase item1Title = new Paragraph(defaultLeading,
				"Emission Process Information", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		generate2Columns(table, "Process ID:", emProc.getProcessId(), false,
				null, null, false);
		generate2Columns(table, "Process Name:", emProc.getProcessName(),
				false, null, null, false);
		generate2Columns(table, "Company Process Description:",
				emProc.getEmissionProcessNm(), false, null, null, false);

		SccCodeConverter sccCodeConverter = new SccCodeConverter();
		temp = emProc.getSccId() == null ? null : sccCodeConverter
				.formatSccCode(emProc.getSccId());
		generate2Columns(table, "Source Classification Code (SCC):", temp,
				false, null, null, false);
		generate2Columns(table, "", "", false, "", "", false);
		item1.add(table);

		if (!emProc.getControlEquipments().isEmpty()) {
			ArrayList<String> ceIds = new ArrayList<String>();
			for (ControlEquipment ce : emProc.getControlEquipments()) {
				ceIds.add(ce.getControlEquipmentId());
			}
			table = generateSubTable(
					"Control equipment(s) directly associated with this process",
					ceIds.toArray(new String[0]));
			item1.add(table);
		}

		if (!emProc.getEgressPoints().isEmpty()) {
			ArrayList<String> egpIds = new ArrayList<String>();
			for (EgressPoint egp : emProc.getEgressPoints()) {
				egpIds.add(egp.getReleasePointId());
			}
			table = generateSubTable(
					"Release points(s) directly associated with this process",
					egpIds.toArray(new String[0]));
			item1.add(table);
		}

		return item1;
	}

	private Table generateSubTable(String title, String[] ids)
			throws DocumentException {
		
		Table table = generateSubTable(title);
		table.setPadding(2);

		Cell cell = createCell(defaultLeading, "", data1Font);
		cell.setBorder(0);
		table.addCell(cell);

		for (String id : ids) {
			cell = createCell(defaultLeading, id, data1Font);
			cell.setBorder(0);
			table.addCell(cell);
		}

		return table;

	}

	/* END : Emission Unit */

	/* START : Control Equipment */

	private void generateControlEquipment(ControlEquipment ce)
			throws DocumentException {
		
		document.newPage();

		Table titleTable = createTitleTable(
				"Control Equipment : " + ce.getControlEquipmentId(), "");
		document.add(titleTable);

		List sections = new List(false, 20);
		sections.add(generateContEquipData(ce));
		sections.add(generateContEquipSpecData(ce));
		sections.add(generateContPollutants(ce));
		sections.add(generateAssociatedCeEgps(ce));

		document.add(sections);
	}

	private ListItem generateContEquipData(ControlEquipment ce)
			throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Control Equipment Information", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp;
		String temp1;

		temp = ContEquipTypeDef.getData().getItems()
				.getItemDesc(ce.getEquipmentTypeCd());
		generate2Columns(table, "Equipment Type:", temp, false, null, null,
				false);
		generate2Columns(table, "Control Equipment ID:",
				ce.getControlEquipmentId(), false, null, null, false);
		generate2Columns(table, "AQD Description:", ce.getDapcDesc(), false,
				null, null, false);
		generate2Columns(table, "Company Control Equipment ID:", ce.getCompanyId(), false, null,
				null, false);
		generate2Columns(table, "Company Control Equipment Description:", ce.getRegUserDesc(),
				false, null, null, false);
		temp = CeOperatingStatusDef.getData().getItems()
				.getItemDesc(ce.getOperatingStatusCd());
		temp1 = ce.getContEquipInstallDate() == null ? null : dateFormat1
				.format(ce.getContEquipInstallDate());
		generate2Columns(table, "Operating Status:", temp, false,
				"Initial Installation Date:", temp1, false);
		generate2Columns(table, "Manufacturer:", ce.getManufacturer(), false,
				"Model:", ce.getModel(), false);

		item1.add(table);

		return item1;
	}

	private ListItem generateContEquipSpecData(ControlEquipment ce)
			throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Specific Equipment Type information", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp = null;
		String temp1 = null;
		EnumDetail[] enumData;

		for (DataDetail data : ce.getCeDataDetails()) {
			if (data.getDataTypeId().equals(5)) {
				enumData = data.getEnumDetails();
				for (EnumDetail enumTemp : enumData) {
					if (enumTemp.getEnumValue().equals(data.getDataDetailVal())) {
						temp1 = enumTemp.getEnumLabel();
						break;
					}
				}
			} else {
				temp1 = data.getDataDetailVal();
			}
			temp = data.getDataDetailLbl() + ":";
			generate2Columns(table, temp, temp1, false, null, null, false);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateContPollutants(ControlEquipment ce)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
		Cell cell;
		String temp;

		Phrase item1Title = new Paragraph(defaultLeading,
				"Pollutants Controlled", boldFont);
		item1.add(item1Title);

		Table table = new Table(5);
		float[] widths = { 40, 20, 20, 20, 20 };
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell("Pollutant");
		table.addCell(cell);
		cell = createCell("Design Control Efficiency(%)");
		table.addCell(cell);
		cell = createCell("Operating Control Efficiency(%)");
		table.addCell(cell);
		cell = createCell("Capture Efficiency(%)");
		table.addCell(cell);
		cell = createCell("Total Capture Control(%)");
		table.addCell(cell);

		for (PollutantsControlled poll : ce.getPollutantsControlled()) {
			temp = PollutantDef.getData().getItems()
					.getItemDesc(poll.getPollutantCd());
			cell = createCell(defaultLeading, temp, data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, poll.getDesignContEff(),
					data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, poll.getOperContEff(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, poll.getCaptureEff(), data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, poll.getTotCaptureEff(),
					data1Font);
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateAssociatedCeEgps(ControlEquipment ce)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
		Table table;

		Phrase item1Title = new Paragraph(defaultLeading,
				"Associated Control Equipments And Release Points", boldFont);
		item1.add(item1Title);

		if (!ce.getControlEquips().isEmpty()) {
			ArrayList<String> ceIds = new ArrayList<String>();
			for (ControlEquipment ce1 : ce.getControlEquips()) {
				ceIds.add(ce1.getControlEquipmentId());
			}
			table = generateSubTable(
					"Control equipment(s) directly associated with this control equipment",
					ceIds.toArray(new String[0]));
			item1.add(table);
		}

		if (!ce.getEgressPoints().isEmpty()) {
			ArrayList<String> egpIds = new ArrayList<String>();
			for (EgressPoint egp : ce.getEgressPoints()) {
				egpIds.add(egp.getReleasePointId());
			}
			table = generateSubTable(
					"Release points(s) directly associated with this control equipment",
					egpIds.toArray(new String[0]));
			item1.add(table);
		}

		return item1;
	}

	/* END : Control Equipment */

	/* START : Release Point */

	private void generateEgressPoint(EgressPoint egp) throws DocumentException {
		
		document.newPage();

		Table titleTable = createTitleTable(
				"Release Point : " + egp.getReleasePointId(), "");
		document.add(titleTable);

		List sections = new List(false, 20);
		sections.add(generateEgrPointData(egp));
		// sections.add(generateBuildingDimensionsData(egp));
		if (!egp.isFugitive()) {
			sections.add(generateTypeSpecData(egp));
		}
		sections.add(generateLatAndLogData(egp));
		// sections.add(generateEISData(egp));
//		sections.add(generateCEMData(egp));

		document.add(sections);
	}

	private ListItem generateEgrPointData(EgressPoint egp)
			throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Release Point Information", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp;

		generate2Columns(table, "Release Point ID:", egp.getReleasePointId(),
				false, null, null, false);
		temp = EgrPointTypeDef.getData().getItems()
				.getItemDesc(egp.getEgressPointTypeCd());
		generate2Columns(table, "Release Type:", temp, false, null, null, false);
		generate2Columns(table, "AQD Description:", egp.getDapcDesc(), false,
				null, null, false);
		generate2Columns(table, "Company Release Point ID:", egp.getEgressPointId(), false,
				null, null, false);
		generate2Columns(table, "Company Release Point Description:",
				egp.getRegulatedUserDsc(), false, null, null, false);
		temp = EgOperatingStatusDef.getData().getItems()
				.getItemDesc(egp.getOperatingStatusCd());
		generate2Columns(table, "Operating Status:", temp, false, null, null,
				false);
		if (egp.getEgressPointTypeCd().equals(EgrPointTypeDef.FUGITIVE)) {
			temp = toVALUE_FORMAT(egp.getReleaseHeight());
			generate2Columns(table, "Release Height (ft):", temp, false, null,
					null, false);
		} else if (egp.getEgressPointTypeCd().equals(EgrPointTypeDef.VERTICAL)
				|| egp.getEgressPointTypeCd()
						.equals(EgrPointTypeDef.HORIZONTAL)) {
			temp = toVALUE_FORMAT1(egp.getBaseElevation());
			generate2Columns(table, "Base Elevation (ft):", temp, false, null,
					null, false);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateBuildingDimensionsData(EgressPoint egp)
			throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading, "Building Dimension",
				boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp;
		String temp1;

		temp = egp.getBuildingLength() == null ? null : VALUE_FORMAT.format(egp
				.getBuildingLength());
		temp1 = egp.getBuildingWidth() == null ? null : VALUE_FORMAT.format(egp
				.getBuildingWidth());
		generate2Columns(table, "Length (ft)", temp, false, "Width (ft):",
				temp1, false);
		temp = egp.getBuildingHeight() == null ? null : VALUE_FORMAT.format(egp
				.getBuildingHeight());
		generate2Columns(table, "Height (ft):", temp, false, null, null, false);

		item1.add(table);

		return item1;
	}

	private ListItem generateLatAndLogData(EgressPoint egp)
			throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading,
				"Release Latitude and Longitude", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp = toVALUE_FORMAT(egp.getLatitudeDeg());
		String temp1 = toVALUE_FORMAT(egp.getLongitudeDeg());
		generate2Columns(table, "Latitude:", temp, false, "Longitude:", temp1,
				false);

		item1.add(table);

		return item1;
	}

	private ListItem generateTypeSpecData(EgressPoint egp)
			throws DocumentException {
		
		ListItem item1 = new ListItem();
		Phrase item1Title;

		item1Title = new Paragraph(defaultLeading, "Stack Details", boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp;
		String temp1;

		temp = toVALUE_FORMAT1(egp.getReleaseHeight());
		temp1 = toVALUE_FORMAT1(egp.getDiameter());
		generate2Columns(table, "Stack Height (ft):", temp, false,
				"Stack Diameter (ft):", temp1, false);

		temp = toVALUE_FORMAT1(egp.getExitGasVelocity());
		temp1 = toVALUE_FORMAT1(egp.getExitGasFlowAvg());
		generate2Columns(table, "Exit Gas Velocity (ft/s):", temp, false,
				"Exit Gas Flow Rate (acfm):", temp1, false);

		temp = toVALUE_FORMAT1(egp.getExitGasTempAvg());
		generate2Columns(table, "Exit Gas Temp (F):", temp, false, null, null,
				false);

		item1.add(table);

		return item1;
	}

	private ListItem generateEISData(EgressPoint egp) throws DocumentException {
		
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading, "EIS Information",
				boldFont);
		item1.add(item1Title);

		Table table = new Table(4);
		table.setBorder(0);
		table.setPadding(2);
		table.setWidth(98);

		String temp;

		temp = HortCollectionMethods.getData().getItems()
				.getItemDesc(egp.getHortCollectionMethodCd());
		generate2Columns(table, "Horizontal Collection Method:", temp, false,
				null, null, false);
		temp = HortAccuracyMeasure.getData().getItems()
				.getItemDesc(egp.getHortAccurancyMeasure());
		generate2Columns(table, "Horizontal Accuracy Measure:", temp, false,
				null, null, false);
		temp = ReferencePoints.getData().getItems()
				.getItemDesc(egp.getReferencePointCd());
		generate2Columns(table, "Reference Point:", temp, false, null, null,
				false);
		temp = HortReferenceDatum.getData().getItems()
				.getItemDesc(egp.getHortReferenceDatumCd());
		generate2Columns(table, "Horizontal Reference Datum:", temp, false,
				null, null, false);
		if (egp.isRenderSourceMapScaleNumber()) {
			temp = egp.getSourceMapScaleNumber() == null ? null : VALUE_FORMAT
					.format(egp.getSourceMapScaleNumber());
			generate2Columns(table, "Source Map Scale Number:", temp, false,
					null, null, false);
		}
		generate2Columns(table, "Coordinate Data Source Code:",
				egp.getCoordDataSourceCd(), false, null, null, false);
		if (egp.isFugitive()) {
			temp = egp.getPlumeTemp() == null ? null : VALUE_FORMAT.format(egp
					.getPlumeTemp());
			generate2Columns(table, "Plume Temp (F):", temp, false, null, null,
					false);
		}

		item1.add(table);

		return item1;
	}

	private ListItem generateCEMData(EgressPoint egp) throws DocumentException {
		
		ListItem item1 = new ListItem();
		Cell cell;

		Phrase item1Title = new Paragraph(defaultLeading, "CEM Data", boldFont);
		item1.add(item1Title);

		Table table = new Table(14);
		float[] widths = { 35, 5, 5, 5, 4, 5, 5, 5, 2, 5, 5, 6, 9, 4 };
		table.setWidths(widths);
		table.setPadding(2);
		table.setAlignment(Element.ALIGN_LEFT);

		cell = createCell(defaultLeading, "Description", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "H2S", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "SO2", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "NOX", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "CO", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "THC", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "HCL", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "HFL", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "O", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "TRS", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "CO2", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "FLOW", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "OPACITY", normal2Font);
		table.addCell(cell);
		cell = createCell(defaultLeading, "PM", normal2Font);
		table.addCell(cell);

		for (EgressPointCem cem : egp.getCems()) {
			cell = createCell(defaultLeading, cem.getCemDsc(), data1Font);
			table.addCell(cell);
			cell = createCheckCell(cem.isH2sFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isSo2Flag());
			table.addCell(cell);
			cell = createCheckCell(cem.isNoxFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isCoFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isThcFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isHclFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isHflFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isO1Flag());
			table.addCell(cell);
			cell = createCheckCell(cem.isTrsFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isCo2Flag());
			table.addCell(cell);
			cell = createCheckCell(cem.isFlowFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isOpacityFlag());
			table.addCell(cell);
			cell = createCheckCell(cem.isPmFlag());
			table.addCell(cell);
		}

		item1.add(table);

		return item1;
	}

	/* END : Release Point */

	private String toVALUE_FORMAT(Object obj) {
		return obj == null ? null : VALUE_FORMAT.format(obj);
	}

	private String toVALUE_FORMAT1(Object obj) {
		return obj == null ? null : VALUE_FORMAT1.format(obj);
	}

	private String toString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	private String toDateFormat1(Timestamp timestamp) {
		return timestamp == null ? null : dateFormat1.format(timestamp);
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

	@Override
	protected Paragraph getCertificationLanguage() throws DocumentException {
		
		String str = SystemPropertyDef.getSystemPropertyValue("DAPCAttestationMessage", null);
		if (facility != null) {
			try {
				InfrastructureService infrastructureBO = ServiceFactory
						.getInstance().getInfrastructureService();
				str = infrastructureBO.getDAPCAttestationMessage(facility
						.getPermitClassCd());
			} catch (ServiceFactoryException e) {
				logger.error("Exception while geting certification language", e);
			} catch (RemoteException e) {
				logger.error("Exception while geting certification language", e);
			}
		}
		// format the certification language better
		Paragraph attestation = new Paragraph(defaultLeading,
				formatCertificationLanguage(str), smallerBold);

		return attestation;
	}

	/*
	 * FOR TESTING ONLY
	 */
	public static void main(String[] args) {
		
		String dirPath = "C:\\Projects\\Stars2";
		try {
			FacilityService facilityBO = ServiceFactory.getInstance()
					.getFacilityService();
			Facility facility = facilityBO.retrieveFacilityProfile(48516,
					false, null);
			FacilityContactsPdfGenerator gen = new FacilityContactsPdfGenerator();
			FileOutputStream fos = new FileOutputStream(dirPath
					+ File.separator + facility.getFacilityId() + ".pdf");
			gen.setUserName("beverm");
			gen.generatePdf(facility, fos, null);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
