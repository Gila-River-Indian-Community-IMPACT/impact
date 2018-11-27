package us.oh.state.epa.stars2.webcommon.pdf.emissionsReport;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

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
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsMaterialActionUnits;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportNote;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsVariable;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.MultiEstabFacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.ContentTypeDef;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.EmissionsAttachmentTypeDef;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.UnitDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.converter.SccCodeConverter;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportTemplates;

@SuppressWarnings("serial")
public class EmissionsReportPdfGenerator extends PdfGeneratorBase {
	static Logger logger = Logger.getLogger(EmissionsReportPdfGenerator.class);
    protected static final DecimalFormat VALUE_FORMAT = new DecimalFormat("#####0.0####");
    protected static String reportTypes = "";
    private float leftMargin = 10;
    private float rightMargin = 10;
    private float topMargin = 20;
    private float bottomMargin = 20;
    private Font data1Font = FontFactory.getFont(dataFontFamily, 8, Font.NORMAL);
    private Font data1FontRed = FontFactory.getFont(dataFontFamily, 8, Font.NORMAL, Color.RED);
    private Font normal1Font = FontFactory.getFont(defaultFontFamily, 8, Font.NORMAL);    
    private Document document;    // PDF document
    private EmissionsReport report;
    private ReportTemplates scReports;
    private boolean hideTradeSecret;
    private boolean completRpt;
    private Facility facility;
    private boolean anyTradeSecret = false;
    private SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
    private LinkedHashMap<String, String> contactTitles;    
    @SuppressWarnings("unused")
    private MultiEstabFacilityList[] multiEstabFacilities;
    private EuSummaryAll summaryTable = new EuSummaryAll();
    
    /**
     * Constructor is protected to force users to call <code>getGeneratorForClass</code>
     *
     */
    public EmissionsReportPdfGenerator() throws DocumentException{
        try {
            InfrastructureService infraBO = ServiceFactory.getInstance().getInfrastructureService();
            
            SimpleDef[] tempDefs = infraBO.retrieveSimpleDefs(
                    "cm_title_def", "title_cd", "title_dsc", "deprecated", null);

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
    public boolean generatePdf(Facility facility, EmissionsReport report, 
    		ReportTemplates scReports, boolean hideTradeSecret, boolean completRpt, 
    		OutputStream os, boolean isSubmittedPDFDoc, boolean includeAllAttachments) throws IOException, DocumentException {
        document = new Document();
        PdfWriter.getInstance(document, os);
        
        document.setMargins(leftMargin, rightMargin, topMargin, bottomMargin);
        document.open();

        this.report = report;
        this.facility = facility;
        this.scReports = scReports;
        this.hideTradeSecret = hideTradeSecret;
        this.completRpt = completRpt;
        anyTradeSecret = false;
        
        // add a title page
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Timestamp docDate = report.getReceiveDate();
        if (docDate == null) {
            docDate = new Timestamp(System.currentTimeMillis());
        }
        String[] title = {
        		" " +
	        		reportTypes + " Emissions Inventory (" + report.getEmissionsInventoryId() + 
	        		") for " + report.getReportYear().toString(),
	        		facility.getName(),
	        		facility.getFacilityId(),
                dateFormat.format(docDate)};
        setSubmitDate(report.getRptReceivedStatusDate());
        setSubmittedPDFDoc(isSubmittedPDFDoc);
        setIncludeAllAttachments(includeAllAttachments);
        Table titleTable = createTitleTable(title);
        titleTable.setAutoFillEmptyCells(false);
        document.add(titleTable);
        if (!isAttestationOnly()) {
	        // print application-specific data
	        String facilityLabel = facility.getName() + "(" + facility.getFacilityId() + ")";
	        /*
	        if(report.isRptEIS()) {
	            reportTypes = "FER";
	        }
	        if(report.isRptEIS()) {
	            reportTypes = reportTypes + ((reportTypes.length())>0?"/EIS":"EIS");
	        }
	        if(report.isRptES()) {
	            reportTypes = reportTypes + ((reportTypes.length())>0?"/ES":"ES");
	        }
	        */
	      
	        printReport(facilityLabel);
        }

        // close the document
        if (document != null) {
            document.close();
        }
        
        return anyTradeSecret;
    }
    
    private void printReport(String facilityLabel)throws IOException, DocumentException {
        // define footer - NOTE: footer must be added to document after
        // PdfWriter.getInstance and BEFORE document.open so footer will
        // appear on all pages (including first page)
        String one = facilityLabel;
        String two = reportTypes + " " + report.getReportYear();
        declareFooter(one, two);
        document.setPageSize(PageSize.A4.rotate());
        document.setPageCount(0);
        document.newPage();

        Table titleTable = createTitleTable(report.getReportYear() + " Emissions Summary Report : " + report.getEmissionsInventoryId(), "");
        document.add(titleTable);
           
        List sections = new List(false, 20);
        sections.add(generateHeaderReportData());
        //sections.add(generateSummaryReportData());
        
        ArrayList<EmissionRow> pE;
    	
    	Integer percentDiff = new Integer(1);

        try {
            summaryTable.generateSummary(report);
            pE = EmissionRow.getEmissions(report, false,
                    true, percentDiff, scReports, logger, false);
            summaryTable.copyReportTotals(pE);
        } catch(ApplicationException re) {
        	throw new DocumentException(re.getMessage());
        }
        
        String totalChargable = " ";
        
        if(report.getTotalEmissions() != null) {
        	totalChargable = EmissionsReport.numberToString(report.getTotalEmissions().doubleValue());
        }
        
        sections.add(generateRepEmissions(1, pE, totalChargable, false));
        
        if (!CompMgr.getAppName().equals(CommonConst.PUBLIC_APP)) {
        	sections.add(generateAttachments());
        }
        
        if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
        	sections.add(generateNotes());
        }
        document.add(sections);
        
        sections = new List(false, 20);
        generateNonDetailedTable();
        summaryTable.generateSummaryTablePDF(document);
        
        if (completRpt) {
        	for(EmissionsReportEUGroup g : report.getEuGroups()) {
        		generateEuGroups(g, facilityLabel);
        	}
        	
        	for(EmissionsReportEU eu : report.getEus()) {
        		if (eu.getPeriods() == null || eu.getPeriods().isEmpty()) {
                    continue; // skip ones that are just for groups
        		}
        		generateEmissionUnit(eu, facilityLabel);
        	}
        }   
    }
   
    private void generateEuGroups(EmissionsReportEUGroup euGrp, String facilityLabel) throws DocumentException {		
        String one = facilityLabel;
        String two = euGrp.getReportEuGroupName() + ": " +
            reportTypes + " " + report.getReportYear();
        declareFooter(one, two);
        document.newPage();
		Table titleTable = createTitleTable("Emissions Unit Group Summary: "
				+ euGrp.getReportEuGroupName(), "");
		document.add(titleTable);

		List sections = new List(false, 20);
		sections.add(generateEuGroup(euGrp));
		sections.add(generateEuGroupData(euGrp));

		ArrayList<EmissionRow> pE;
		FireRow[] periodFireRows = null;
    	Integer percentDiff = new Integer(1);

        try {
            euGrp.getPeriod().setFireRows(periodFireRows);
        	pE = EmissionRow.getEmissions(report.getReportYear(),
                    euGrp.getPeriod(), scReports, false, true, percentDiff, logger);
        } catch(ApplicationException re) {
        	throw new DocumentException(re.getMessage());
        }
        
        Double sum = EmissionRow.getTotalEmissions(euGrp, report.getReportYear(),
                scReports, logger);

        sections.add(generateRepEmissions(2, pE,  EmissionsReport.numberToString(sum), false));
        
        sections.add(generateEmiRptPeriod(euGrp.getPeriod(), 1));
        
		document.add(sections);
	}
    
    private ListItem generateEuGroup(EmissionsReportEUGroup euGrp) throws DocumentException {     
    	ListItem item1 = new ListItem();
        
        Phrase item1Title = new Paragraph(defaultLeading, "Group", boldFont);
    	item1.add(item1Title);
    	
    	Table table = new Table(4);
    	table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
    	generate2Columns(table, "Emissions Unit Group Name:", euGrp.getReportEuGroupName(), false, null, null, false);
    	
        item1.add(table);
    	return item1;              
    }
    
    private ListItem generateEuGroupData(EmissionsReportEUGroup euGrp) throws DocumentException {     
    	ListItem item1 = new ListItem();
        
        Phrase item1Title = new Paragraph(defaultLeading, "Current Group Members", boldFont);
    	item1.add(item1Title);
    	
    	Table table = new Table(1);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        Cell cell;
        
        ArrayList<String> allEus = new ArrayList<String>(0);       
        getAllGrpEUs(facility, report, euGrp, allEus);
        for (String eu : allEus) {
        	cell = createCell(defaultLeading, eu, normal1Font);
        	cell.setBorder(0);
        	table.addCell(cell);
        }
    	
        item1.add(table);
    	return item1;              
    }
    
    private final void getAllGrpEUs(Facility fac,
            EmissionsReport rpt, EmissionsReportEUGroup euG,
            ArrayList<String> allEus) {
        // This part is similar to first part of ReportProfileBase.getAllGrpEUs().

        allEus.clear();
        // The SCC for the group
        String sccId = euG.getPeriod().getSccId();
        // add in those already in the group
        EmissionProcess repEuP = null;
        for (Integer euId : euG.getEus()) {
            EmissionsReportEU e = rpt.getEu(euId);
            // Get process name
            String pName = "";
            EmissionUnit facilityEu = fac.getMatchingEmissionUnit(e.getCorrEpaEmuId());
            if(null != facilityEu) {
                EmissionProcess euP = facilityEu.findProcess(sccId);
                if(null != euP) { // should have found it.
                    repEuP = euP; // get representative facility process
                    if(null != euP.getProcessId()) {
                        pName = euP.getProcessId();
                    }
                }
            }
            allEus.add(new String(e.getEpaEmuId() + ":" + pName));
        }
        if(repEuP == null) {
        // TODO This may be true for migrated groups.  If migrated flag, could keep this warning   
        //    logger.warn("In ReportProfileBase.getAllGrpEUs, could not find representative EU for group \"" +
        //            euG.getReportEuGroupName() + "\" in emissions inventory " +
        //            rpt.getEmissionsRptId() + " for facility " + fac.getFacilityId());
            return;
        }
    }
    
    private ListItem generateEmiRptPeriod(EmissionsReportPeriod erPeriod, int type) throws DocumentException {
    	ListItem item1 = new ListItem();
    	
    	Phrase item1Title = new Paragraph(defaultLeading, "Process & Emissions Detail", boldFont);
    	item1.add(item1Title);
    	
    	Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        if (type == 2) {
        	generate2Columns(table, "Name:", erPeriod.getTreeLabel(), false, null, null, false);
        }
        
        SccCodeConverter sccCodeConverter = new SccCodeConverter();
        String temp = erPeriod.getSccId() == null ? null : sccCodeConverter.formatSccCode(erPeriod.getSccId());
        generate2Columns(table, "Source Classification Code (SCC):", temp, false, null, null, false);
        
        item1.add(table);
        
        List sections = new List(false, 20);
    	
        sections.add(generateErPeriodMatInfo(erPeriod));
        
        ArrayList<EmissionRow> pE;
        FireRow[] periodFireRows = null;
    	Integer percentDiff = new Integer(1);

        try {
            erPeriod.setFireRows(periodFireRows);
        	pE = EmissionRow.getEmissions(
                    report.getReportYear(),
                    erPeriod, scReports, false, true, percentDiff, logger);
        } catch(ApplicationException re) {
        	throw new DocumentException(re.getMessage());
        }
        
        Double sum = EmissionRow.getTotalEmissions(erPeriod, report.getReportYear(),
                scReports, logger);
        
        generatePeriodEmissions(sections, erPeriod, pE, EmissionsReport.numberToString(sum));
    	
    	item1.add(sections);
    	
    	return item1;
    }
    
    private ListItem generateErPeriodMatInfo(EmissionsReportPeriod erPeriod) throws DocumentException {
    	ListItem item1 = new ListItem();
    	
    	Phrase item1Title = new Paragraph(defaultLeading, "Material Information, Annual Average Operating Schedule & Throughput Percent", boldFont);
    	item1.add(item1Title);
    	
    	Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        String temp1;
        if (erPeriod.isTradeSecretS()) {
        	anyTradeSecret = true;
        }

        boolean all = true;
        if (erPeriod.isTradeSecretS()) {
        	generate2Columns(table, "Schedule/Material/Variables/Factors/Explanations contain Trade Secrets:", "Yes",  
    				false, "Trade Secret Reason:", erPeriod.getTradeSecretSText(), false);
        	if (hideTradeSecret) {
        		all = false;
        	}
        } else {
            generate2Columns(table, "Schedule/Material/Variables/Factors/Explanations contain Trade Secrets:", "No", false, null, null, false);
        }
        String hpdTemp = "XX";
        String dpwTemp = "X";
        String wpyTemp = "XX";
        String hpyTemp = "XXXX";
        String fhophrsTemp = "XX";
        String shophrsTemp = "XX";
        if(all) {
            hpdTemp = erPeriod.getHoursPerDay() == null ? "" : erPeriod
                    .getHoursPerDay().toString();
            dpwTemp = erPeriod.getDaysPerWeek() == null ? "" : erPeriod
                    .getDaysPerWeek().toString();
            wpyTemp = erPeriod.getWeeksPerYear() == null ? "" : erPeriod
                    .getWeeksPerYear().toString();
            hpyTemp = erPeriod.getHoursPerYear() == null ? "" : erPeriod
                    .getHoursPerYear().toString();
            fhophrsTemp = erPeriod.getFirstHalfHrsOfOperationPct() == null ? "" : erPeriod
                    .getFirstHalfHrsOfOperationPct().toString();
            shophrsTemp = erPeriod.getSecondHalfHrsOfOperationPct() == null ? "" : erPeriod
                    .getSecondHalfHrsOfOperationPct().toString();
        }

        temp1 = erPeriod.getWinterThroughputPct() == null ? "" : erPeriod
                .getWinterThroughputPct().toString();
        generate2ColumnsColor(table, "Maximum Hours Per Day:", hpdTemp, false, erPeriod.isTradeSecretS(),
                "Winter (Dec - Feb)%:", temp1, false);
        temp1 = erPeriod.getSpringThroughputPct() == null ? "" : erPeriod
                .getSpringThroughputPct().toString();
        generate2ColumnsColor(table, "Maximum Days Per Week:", dpwTemp, false,  erPeriod.isTradeSecretS(),
                "Spring (March-May)%:", temp1, false);
        temp1 = erPeriod.getSummerThroughputPct() == null ? "" : erPeriod
                .getSummerThroughputPct().toString();
        generate2ColumnsColor(table, "Maximum Weeks Per Year:", wpyTemp, false, erPeriod.isTradeSecretS(),
                "Summer (June-Aug)%:", temp1, false);
        temp1 = erPeriod.getFallThroughputPct() == null ? "" : erPeriod
                .getFallThroughputPct().toString();
        generate2ColumnsColor(table, "Actual Hours:", hpyTemp, false, erPeriod.isTradeSecretS(),
                "Fall (Sept-Nov)%:", temp1, false);
        if(!fhophrsTemp.equalsIgnoreCase(shophrsTemp)){
        generate2ColumnsColor(table, "Percent of Hours of Operation (First Half Year):", fhophrsTemp, false, erPeriod.isTradeSecretS(),
                "", "", false);
        generate2ColumnsColor(table, "Percent of Hours of Operation (Second Half Year):", shophrsTemp, false, erPeriod.isTradeSecretS(),
                "", "", false);
        }
        
        item1.add(table);  
        
        Table matTable = generateMaterialInfo(erPeriod);
        if (matTable != null) {
        	item1.add(matTable);
        }
        
        if (erPeriod.getVars().size() > 0) {
        	Table emiVarTab = generateEmiVars(erPeriod);
        	item1.add(emiVarTab);
        }
        
    	return item1;
    }
    
    private Table generateMaterialInfo(EmissionsReportPeriod erPeriod) throws DocumentException {       
        EmissionsMaterialActionUnits selMat = null;
        
        for (EmissionsMaterialActionUnits matInfo : erPeriod.getMaus()) {
        	if (matInfo.isBelongs()) {
        		selMat = matInfo;
        		break;
        	}
        }
        
        if (selMat == null) {
        	return null;
        }

        Cell cell;
        Table table = new Table(4);
        float[] widths = {10, 8, 8, 8};
        table.setWidths(widths);
        table.setPadding(2);
        table.setAlignment(Element.ALIGN_LEFT);
        
        cell = createCell("Material");
        table.addCell(cell);
        cell = createCell("Material Action");
        table.addCell(cell);
        cell = createCell("Throughput");
        table.addCell(cell);
        cell = createCell("X Units");
        table.addCell(cell);
        
        String temp = MaterialDef.getData().getItems().getItemDesc(selMat.getMaterial());
        String temp1 = selMat.getAction();
        cell = createCell(defaultLeading, temp, data1Font);
        table.addCell(cell);
        cell = createCell(defaultLeading, temp1, data1Font);
        table.addCell(cell);
        String temp2;
        if (erPeriod.isTradeSecretS()) {
            anyTradeSecret = true;
            if (!hideTradeSecret) {
                temp2 =  selMat.getThroughput();
            } else {
                temp2 = "XXXXX";
            }
            cell = createCell(defaultLeading, temp2, data1FontRed);
        } else {
            temp2 = selMat.getThroughput();
            cell = createCell(defaultLeading, temp2, data1Font);
        }
        table.addCell(cell);
        String temp3 = UnitDef.getData().getItems().getItemDesc(selMat.getMeasure());
        cell = createCell(defaultLeading, temp3, data1Font);
        table.addCell(cell);
        return table;
    }
    
    private Table generateEmiVars(EmissionsReportPeriod erPeriod) throws DocumentException{
        Cell cell;
        
        Table table = new Table(3);
        //table.setWidth(100);
        float[] widths = {10, 8, 20};
        table.setWidths(widths);
        table.setPadding(2);
        table.setAlignment(Element.ALIGN_LEFT);
        
        cell = createCell("Variable");
        table.addCell(cell);
        cell = createCell("Amount");
        table.addCell(cell);
        cell = createCell("Meaning");
        table.addCell(cell);
        
        String temp;
        String temp1;
        String temp2;
        
        for (EmissionsVariable ev : erPeriod.getVars()) {
            temp = ev.getVariable();
            cell = createCell(defaultLeading, temp, data1Font);
            table.addCell(cell);
        	temp1 = "XXXXX";
        	if (erPeriod.isTradeSecretS()) {
        		anyTradeSecret = true;
        		if (!hideTradeSecret) {
        			temp1 = ev.getValue();
        		}
                cell = createCell(defaultLeading, temp1, data1FontRed);
        	} else {
    			temp1 = ev.getValue();
                cell = createCell(defaultLeading, temp1, data1Font);
        	}  
    		table.addCell(cell);
            temp2 = ev.getMeaning();
            if(temp2 == null) {
                temp2 = "";
            }
            cell = createCell(defaultLeading, temp2, data1Font);
            table.addCell(cell);
        }
               
        return table;
    }
    
    private ListItem generateHeaderReportData() throws DocumentException {     
        ListItem item1 = new ListItem();
        
        Phrase item1Title = new Paragraph(defaultLeading, "Report Data", boldFont);
        item1.add(item1Title);
        
        Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        String temp;
        String temp1;
        ReportProfileBase rp = (ReportProfileBase)FacesUtil.getManagedBean("reportProfile");
        generate2Columns(table, 
        		"Content Type:", ContentTypeDef.getData().getItems().getItemDesc(report.getContentTypeCd()), 
        		false, 
        		"Regulatory Requirement(s):", rp.getRegulatoryRequirements(),
        		false);
        temp = report.getRptReceivedStatusDate() == null ? null : dateFormat1.format(report.getRptReceivedStatusDate());
        temp1 = report.getRptApprovedStatusDate() == null ? null : dateFormat1.format(report.getRptApprovedStatusDate());
        String dateName;
        if(report.isApproved()) {
            dateName = "Approved Date";
        } else {
            dateName = "Completed Date";
        }
        if(temp1 == null) {
            // don't display if no date yet
            dateName = " ";
        }
        generate2Columns(table, "Submitted Date:", temp, false, dateName, temp1, false);
        temp = ReportReceivedStatusDef.getData().getItems().getItemDesc(report.getRptReceivedStatusCd());
        
        item1.add(table);

        return item1;              
    }
    /*
    private ListItem generateSummaryReportData() throws DocumentException {     
        ListItem item1 = new ListItem();
        
        Phrase item1Title = new Paragraph(defaultLeading, "Reports Included", boldFont);
        item1.add(item1Title);
        
        Table table = new Table(6);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(50);
        
        Cell cell;
        cell = createCell(5, "FER:", normal1Font);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = createCheckCell(report.isRptFER());
        cell.setBorder(0);
        table.addCell(cell);
        
        cell = createCell(5, "ES:", normal1Font);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = createCheckCell(report.isRptES());
        cell.setBorder(0);
        table.addCell(cell);
        
        cell = createCell(5, "EIS:", normal1Font);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = createCheckCell(report.isRptEIS());
        cell.setBorder(0);
        table.addCell(cell);
        item1.add(table);
        return item1;              
    }
    */
    
    private ListItem generateNotes() throws DocumentException{
        ListItem item1 = new ListItem();
        Cell cell;
        String temp;
        
        Phrase item1Title = new Paragraph(defaultLeading, "Notes", boldFont);
        item1.add(item1Title);
        
        Table table = new Table(3);
        table.setWidth(100);
        float[] widths = {30, 15, 55};
        table.setWidths(widths);
        table.setPadding(2);
        table.setAlignment(Element.ALIGN_LEFT);
        
        cell = createCell("User Name");
        table.addCell(cell);
        cell = createCell("Date");
        table.addCell(cell);
        cell = createCell("Note");
        table.addCell(cell);
        
        for (EmissionsReportNote note : report.getNotes()) {
            cell = createCell(defaultLeading, BasicUsersDef.getUserNm(note.getUserId()), data1Font);
            table.addCell(cell);
            temp = note.getDateEntered() == null ? null : dateFormat1.format(note.getDateEntered());
            cell = createCell(defaultLeading, temp, data1Font);
            table.addCell(cell);
            cell = createCell(defaultLeading, note.getNoteTxt(), data1Font);
            table.addCell(cell);
        }
        
        item1.add(table);       
        return item1;
    }
    
    private ListItem generateAttachments() throws DocumentException{
    	ListItem item1 = new ListItem();
    	Cell cell;
    	
    	Phrase item1Title = new Paragraph(defaultLeading, "Attachments", boldFont);
    	item1.add(item1Title);
    	
    	int numCols = hideTradeSecret ? 4 : 6;
    	Table table = new Table(numCols);
    	table.setWidth(100);
    	
    	if (hideTradeSecret) {
    		float[] widths = {9, 27, 19, 9};
    		table.setWidths(widths);
    	} else {
    		float[] widths = {9, 27, 19, 9, 9, 27};
    		table.setWidths(widths);
    	}

    	table.setPadding(2);
    	table.setAlignment(Element.ALIGN_LEFT);
    	
    	cell = createCell("Attachment ID");
    	table.addCell(cell);
    	cell = createCell("Description");
    	table.addCell(cell);
    	cell = createCell("Type");
    	table.addCell(cell);
    	cell = createCell("Public Document");
    	table.addCell(cell);
    	if (!hideTradeSecret) {
    		cell = createCell("Trade Seceret Document");
    		table.addCell(cell);
    		cell = createCell("Trade secret Justification");
    		table.addCell(cell);
    	}
    	
    	EmissionsDocumentRef attachments[] = report.getAttachments().toArray(new EmissionsDocumentRef[0]);
    	
    	for (EmissionsDocumentRef attach : attachments) {
    		// don't include documents added after emissions inventory was submitted
    		if (isSubmittedPDFDoc() && getSubmitDate() != null && attach.getLastModifiedTS() != null
    				&& attach.getLastModifiedTS().after(getSubmitDate())) {
    			logger.debug("Excluding document "
    					+ attach.getEmissionsDocId()
    					+ " from emissions inventory PDF generator. Document last modified date ("
    					+ attach.getLastModifiedTS()
    					+ ") is after emissions inventory submission date ("
    					+ getSubmitDate() + ")");
    			continue;
    		}
    		
    		cell = createCell(defaultLeading, attach.getEmissionsDocId().toString(), data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, attach.getDescription(), data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, EmissionsAttachmentTypeDef.getData().getItems().getItemDesc(attach.getEmissionsDocumentTypeCD()), data1Font);
    		table.addCell(cell);
    		cell = createCheckCell((attach.getDocumentId() != null ? true : false));
    		table.addCell(cell);
    		if(attach.getTradeSecret()) anyTradeSecret = true;
    		if (!hideTradeSecret) {
    			cell = createCheckCell((isIncludeAllAttachments() || (isSubmittedPDFDoc() && getSubmitDate() != null) || attach.isTradeSecretAllowed())? attach.getTradeSecret() : false);
    			table.addCell(cell);
    			cell = createCell(defaultLeading, ((isIncludeAllAttachments() || (isSubmittedPDFDoc() && getSubmitDate() != null) || attach.isTradeSecretAllowed())? attach.getTradeSecretReason() : ""), data1Font);
    			table.addCell(cell);
    		}
    	}
    	
    	item1.add(table);
    	
    	return item1;
    }
    
    private ListItem generateRepEmissions(int type, ArrayList<EmissionRow> pE,
    		String totalChargable, boolean isEU) throws DocumentException{
    	ListItem item1 = new ListItem();
    	Cell cell;
    	
    	Phrase item1Title = null;
    	if (type == 1) {
    		item1Title = new Paragraph(defaultLeading, "Facility Emissions", boldFont);
    	} else if (type == 2) {
    		item1Title = new Paragraph(defaultLeading, "Group Emissions", boldFont);
    	} else if (type == 3) {
    		item1Title = new Paragraph(defaultLeading, "Unit Emissions", boldFont);
    	}
    	item1.add(item1Title);
        
    	// CAP table
        Table table;
        if(isEU){
            table = new Table(6);
            table.setWidth(100);
            float[] widths = {45, 10, 10, 9, 4, 4};
            table.setWidths(widths); 
        }else{
            table = new Table(5);
            table.setWidth(100);
            float[] widths = {48, 10, 10, 9, 4};
            table.setWidths(widths);
        }
    	table.setPadding(2);
    	table.setAlignment(Element.ALIGN_LEFT);
    	
    	cell = createCell("Pollutant");
    	table.addCell(cell);
    	//if(!isEU) {
    	//cell = createCell("Code");
    	//table.addCell(cell);
    	//}
    	//cell = createCell("$");
    	//table.addCell(cell);
    	cell = createCell("Fugitive Amount");
    	table.addCell(cell);
    	cell = createCell("Stack Amount");
    	table.addCell(cell);
    	cell = createCell("Total");
    	table.addCell(cell);
    	cell = createCell("Units");
    	table.addCell(cell);
        if(isEU) {
            cell = createCell("Further Validations Required");
            table.addCell(cell);
        }
    	
//    	boolean anyChargable = false;
    	Integer capHapI = EmissionRow.secondTab1stRow(pE);
        int endPoint = 0;
        if(capHapI != null) {
            endPoint = capHapI - 1;
        }
        for(int i = 0; i <= endPoint; i++) {
            EmissionRow emission = pE.get(i);
    	//for (EmissionRow emission : pE) {
    		cell = createCell(defaultLeading, emission.getPollutant(), data1Font);
    		table.addCell(cell);
    		//cell = createCell(defaultLeading, emission.getPollutantCd(), data1Font);
    		//table.addCell(cell);
    		if (emission.isChargeable()) {
//    			anyChargable = true;
    		}
    		//cell = createCheckCell(emission.isChargeable());
    		//table.addCell(cell);
    		cell = createCell(defaultLeading, emission.getFugitiveEmissions(), data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, emission.getStackEmissions(), data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, emission.getTotalEmissions(), data1Font);
    		table.addCell(cell);
            String s = UnitDef.getData().getItems().getItemDesc(emission.getEmissionsUnitNumerator());
    		cell = createCell(defaultLeading, s, data1Font);
    		table.addCell(cell);
            if(isEU) {
                cell = createCheckCell(emission.isExceedThresholdQA());
                table.addCell(cell);
            }
    	}
    	
        /*if (true) { //anyChargable) {
			cell = createCell(defaultLeading, "Total of Chargeable Pollutants",
					data1Font);
			table.addCell(cell);
			//cell = createCell(defaultLeading, "", data1Font);
			//table.addCell(cell);
			//cell = createCheckCell(false);
			//table.addCell(cell);
			cell = createCell(defaultLeading, "", data1Font);
			table.addCell(cell);
            cell = createCell(defaultLeading, "", data1Font);
            table.addCell(cell);
			cell = createCell(defaultLeading, totalChargable, data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, "TONS", data1Font);
			table.addCell(cell);
		}*/
    	
    	item1.add(table);
        
        table = new Table(5);
        table.setWidth(100);
        float[] widths2 = {49, 10, 10, 9, 4};
        table.setWidths(widths2);
        table.setPadding(2);
        table.setAlignment(Element.ALIGN_LEFT);
        
        cell = createCell("Pollutant");
        table.addCell(cell);
        //cell = createCell("Code");
        //table.addCell(cell);
        cell = createCell("Fugitive Amount");
        table.addCell(cell);
        cell = createCell("Stack Amount");
        table.addCell(cell);
        cell = createCell("Total");
        table.addCell(cell);
        cell = createCell("Units");
        table.addCell(cell);
        
        if(endPoint < pE.size() - 1) {
            item1Title = new Paragraph(defaultLeading, "______________________________________________________________________________________________________________________", boldFont);
            item1.add(item1Title);
            String hapNonAttestationMsg = SystemPropertyDef.getSystemPropertyValue("HAP_EMISSIONS_NON_ATTESTATION_MSG", null);
            item1Title = new Paragraph(defaultLeading, hapNonAttestationMsg, smallerNormal);
            item1.add(item1Title);
            // HAP table
            for(int i = endPoint + 1; i < pE.size(); i++) {
                EmissionRow emission = pE.get(i);
                //for (EmissionRow emission : pE) {
                cell = createCell(defaultLeading, emission.getPollutant(), data1Font);
                table.addCell(cell);
                //cell = createCell(defaultLeading, emission.getPollutantCd(), data1Font);
                //table.addCell(cell);
                cell = createCell(defaultLeading, emission.getFugitiveEmissions(), data1Font);
                table.addCell(cell);
                cell = createCell(defaultLeading, emission.getStackEmissions(), data1Font);
                table.addCell(cell);
                cell = createCell(defaultLeading, emission.getTotalEmissions(), data1Font);
                table.addCell(cell);
                String s = UnitDef.getData().getItems().getItemDesc(emission.getEmissionsUnitNumerator());
                cell = createCell(defaultLeading, s, data1Font);
                table.addCell(cell);
            }
            item1.add(table);
        }
        return item1;  
    }
    
    private void generatePeriodEmissions(List sections, EmissionsReportPeriod erPeriod, ArrayList<EmissionRow> pE,
            String totalChargable) throws DocumentException{
        Integer capHapI = EmissionRow.secondTab1stRow(pE);
        int endPoint = 0;
        if(capHapI != null) {
            endPoint = capHapI - 1;
        }
        sections.add(generatePeriodEmissions(erPeriod, pE, 0, endPoint, totalChargable));
        if(endPoint < pE.size() - 1) {
            ListItem item = new ListItem();
            Paragraph item1Title = new Paragraph(defaultLeading, "______________________________________________________________________________________________________________________", boldFont);
            item.add(item1Title);
            String hapNonAttestationMsg = SystemPropertyDef.getSystemPropertyValue("HAP_EMISSIONS_NON_ATTESTATION_MSG", null);
            item1Title = new Paragraph(defaultLeading, hapNonAttestationMsg, smallerNormal);
            item.add(item1Title);
            sections.add(item);
            sections.add(generatePeriodEmissions(erPeriod, pE, endPoint + 1, pE.size() - 1, totalChargable));
        }
    }
    
    private ListItem generatePeriodEmissions(EmissionsReportPeriod erPeriod, ArrayList<EmissionRow> pE,
    		int start, int stop, String totalChargable) throws DocumentException{
        // document.setPageSize(PageSize.A4.rotate());
        // document.newPage();
    	
    	ListItem item1 = new ListItem();
    	Cell cell;
    	
    	Phrase item1Title = null;
    	item1Title = new Paragraph(defaultLeading, "Process Emissions", boldFont);
    	item1.add(item1Title);
    	
    	Table table;
    	float[] widths = {10, 7, 5, 6, 5, 8, 8, 8, 3.5f, 38.5f};
        float[] widthsHAP = {10, 7, 5, 6, 5 , 8, 8, 8, 3.5f, 39.5f};
        if(start == 0) {
            table = new Table(10);
            table.setWidth(100);
            table.setWidths(widths);
        } else {
            table = new Table(10);
            table.setWidth(100);
            table.setWidths(widthsHAP);
        }
    	table.setPadding(2);
    	table.setAlignment(Element.ALIGN_LEFT);
    	
    	cell = createCell("Pollutant");
        
    	table.addCell(cell);
    	//cell = createCell("Code");
    	//table.addCell(cell);
       // if(start == 0) {
         //   cell = createCell("$");
    	 //   table.addCell(cell);
        //}
    	cell = createCell("Method Used");
    	table.addCell(cell);
    	cell = createCell("Hours UnCont.");
    	table.addCell(cell);
    	cell = createCell("UnCont. Factor (LBS/X)");
    	table.addCell(cell);
    	cell = createCell("Time-Based Factor (LBS/Hour)");
    	table.addCell(cell);
    	cell = createCell("Fugitive Amount");
    	table.addCell(cell);
    	cell = createCell("Stack Amount");
    	table.addCell(cell);
    	cell = createCell("Total");
    	table.addCell(cell);
    	cell = createCell("Units");
        //cell.setWidth("5%");
    	table.addCell(cell);
    	cell = createCell("Explanation");
        //cell.setWidth("36%");
    	table.addCell(cell);
    	
    	//boolean anyChargable = false;
    	String temp;
    	
        for(int i = start; i <= stop; i++) {
            EmissionRow emission = pE.get(i);
    	//for (EmissionRow emission : pE) {
    		cell = createCell(defaultLeading, emission.getPollutant(), data1Font);
    		table.addCell(cell);
    		//cell = createCell(defaultLeading, emission.getPollutantCd(), data1Font);
    		//table.addCell(cell);
    		if(start == 0) {
//    		    if (emission.isChargeable()) {
//    		        anyChargable = true;
//    		    }
    		   // cell = createCheckCell(emission.isChargeable());
    		    //table.addCell(cell);
    		}
    		temp = EmissionCalcMethodDef.getData().getItems().getItemDesc(emission.getEmissionCalcMethodCd());
    		//
    		if (emission.getFactorNumericValueOverride()){
    			temp = temp + " (Uncontrolled factor input by user)";
    		}
    		//
    		cell = createCell(defaultLeading, temp, data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, emission.getAnnualAdjust(), data1Font);
    		table.addCell(cell);
    		if (erPeriod.isTradeSecretS()) {
    			anyTradeSecret = true;
    			temp = hideTradeSecret == true ? "XXXXX" : emission.getFactorNumericValue();
                if(emission.getFactorNumericValue() == null ||
                        emission.getFactorNumericValue().length() == 0) {
                    temp = "";
                }
    			cell = createCell(defaultLeading, temp, data1FontRed);
        		table.addCell(cell);
    		} else {
    			cell = createCell(defaultLeading, emission.getFactorNumericValue(), data1Font);
        		table.addCell(cell);
    		}
    		cell = createCell(defaultLeading, emission.getTimeBasedFactorNumericValue(), data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, emission.getFugitiveEmissions(), data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, emission.getStackEmissions(), data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, emission.getTotalEmissions(), data1Font);
    		table.addCell(cell);
            String s = UnitDef.getData().getItems().getItemDesc(emission.getEmissionsUnitNumerator());
    		cell = createCell(defaultLeading, s, data1Font);
    		table.addCell(cell);
    		if (erPeriod.isTradeSecretS()) {
    			anyTradeSecret = true;
    			temp = hideTradeSecret == true ? "XXXXXXXXXXXXXXXXXXXXXX" : emission.getExplanation();
                if(emission.getExplanation() == null ||
                        emission.getExplanation().length() == 0) {
                    temp = "";
                }
    			cell = createCell(defaultLeading, temp, data1FontRed);
        		table.addCell(cell);
    		} else {
    			cell = createCell(defaultLeading, emission.getExplanation(), data1Font);
        		table.addCell(cell);
    		}
    	}
    	
    	/*if (start == 0) { //anyChargable) {
			cell = createCell(defaultLeading, "Total of Chargeable Pollutants",
					data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, "", data1Font);
			table.addCell(cell);
			//cell = createCheckCell(false);
			//table.addCell(cell);
			cell = createCell(defaultLeading, "", data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, "", data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, "", data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, "", data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, "", data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, totalChargable, data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, "TONS", data1Font);
			table.addCell(cell);
			cell = createCell(defaultLeading, "", data1Font);
			table.addCell(cell);
		}*/
    	
    	item1.add(table);
    	
    	// document.setPageSize(PageSize.A4);
        // document.newPage();
        
    	return item1;  
    }
    
    private void generateNonDetailedTable() throws DocumentException {        
        List sections = new List(false, 20);
        Table table;
        float[] widths = {10, 20, 70};
        table = new Table(3);
        table.setWidth(100);
        table.setWidths(widths);
        table.setPadding(2);
        table.setAlignment(Element.ALIGN_LEFT);
        
        Cell cell = createCell("Emission Unit");
        
        table.addCell(cell);
        cell = createCell("Why Excluded");
        table.addCell(cell);
        cell = createCell("Company Equipment ID");
        table.addCell(cell);
        boolean tableEmpty = true;
        for(EmissionsReportEU eu : report.getEus()) {
            if(!eu.isEg71OrZero()) {
                continue;
            }
            tableEmpty = false;
            cell = createCell(eu.getEpaEmuId());
            table.addCell(cell);
            if(eu.isZeroEmissions()) {
                cell = createCell("Did Not Operate");
                table.addCell(cell);
            } else {
                boolean foundReason = false;
                String reasonStr = "";
                if(scReports.getSc().exemptionReason(eu.getExemptStatusCd()).length() > 0) {
                    reasonStr = "Exemption Status = " + scReports.getSc().exemptionReason(eu.getExemptStatusCd());
                    foundReason = true;
                }
                if(scReports.getSc().tvClassificationReason(eu.getTvClassCd()).length() > 0) {
                    if(reasonStr.length() > 0) reasonStr = reasonStr + "; ";
                    reasonStr = reasonStr + "TV Classification = " + scReports.getSc().tvClassificationReason(eu.getTvClassCd());
                    foundReason = true;
                }
                if(foundReason) {
                    cell = createCell(reasonStr);
                } else {
                    cell = createCell("Less Than Reporting Requirement");
                }
                table.addCell(cell);
            }
            cell = createCell(eu.getCompanyId());
            table.addCell(cell);
        }
        if(!tableEmpty) {
            Table titleTable = createTitleTable("Emission Units Without Detailed Emissions", "");
            document.add(titleTable);
            ListItem item = new ListItem();
            item.add(table);
            sections.add(item);
            document.add(sections);
        }   
    }
    
    /* START : Emission UNit */
    
    private void generateEmissionUnit(EmissionsReportEU eu, String facilityLabel) throws DocumentException {        
        String one = facilityLabel;
        String two = eu.getEpaEmuId() + ": " +
            reportTypes + " " + report.getReportYear();
        declareFooter(one, two);
        document.newPage();
        Table titleTable = createTitleTable("Emission Unit Summary: " + eu.getEpaEmuId(), "");
        document.add(titleTable);
        
        List sections = new List(false, 20);
        sections.add(generateEmissionUnitData(eu));
        
        ArrayList<EmissionRow> pE;
    	
    	Integer percentDiff = new Integer(1);

        try {
        	pE = EmissionRow.getEmissions(eu, false,
            		percentDiff, scReports, logger);
        } catch(ApplicationException re) {
        	throw new DocumentException(re.getMessage());
        }
        
        Double sum = EmissionRow.getTotalEmissions(eu, report.getReportYear(),
                scReports, logger);
        
        
        
        sections.add(generateRepEmissions(3, pE, EmissionsReport.numberToString(sum), true));
        
        sections.add(generateProcesses(eu));
        
        document.add(sections);     
    }
    
    private void declareFooter(String one, String two) {
        HeaderFooter footer = new HeaderFooter(new Phrase(one +
                "                          " + " Page ", normalFont), 
                new Phrase( "                                      " + 
                        two, normalFont));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setBorder(0);
        document.setFooter(footer);
    }
    
    private void generate2Columns(Table table, String name1, String value1, boolean attr1, 
    		String name2, String value2, boolean attr2) {
        generate2ColumnsColor(table, name1, value1, attr1, 
                false,
                name2, value2, attr2);
    }
    
    private void generate2ColumnsColor(Table table, String name1, String value1, boolean attr1,
            boolean red, 
            String name2, String value2, boolean attr2) {
        boolean val;
        Cell cell;
        Font font = data1Font;
        if(red) {
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
    
    private ListItem generateEmissionUnitData(EmissionsReportEU eu) throws DocumentException {     
        ListItem item1 = new ListItem();
        
        Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        String temp;
        temp = (eu.isForceDetailedReporting()) ? "Forced Detailed Reporting" : "Detailed Reporting";
        
        generate2Columns(table, "Emissions Unit ID:", eu.getEpaEmuId(), false, temp, null, false);
        generate2Columns(table, "AQD Description:", eu.getDapcDescription(), false, null, null, false);
        
        item1.add(table);
        
        return item1;              
    }
    
    private ListItem generateProcesses(EmissionsReportEU eu) throws DocumentException{
        ListItem item1 = new ListItem();
        
        Phrase item1Title = new Paragraph(defaultLeading, "Processes", boldFont);
        item1.add(item1Title);
        
        List sections = new List(false, 20);
        for (EmissionsReportPeriod p : eu.getPeriods()) {
        	sections.add(generateEmiRptPeriod(p, 2));
        }
        
         item1.add(sections);
        
        return item1;
    }
    
    /* END : Emission Unit */
    
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
    
    protected class EuSummaryAll{
        EuSummary totals = new EuSummary("Total", "");
        ArrayList<String> pollutantNames = new ArrayList<String>(10);
        ArrayList<EuSummary> summaryAll = new ArrayList<EuSummary>(100);
        
        ArrayList<String> getPollutantNames() {
            return pollutantNames;
        }
        
        void copyReportTotals(ArrayList<EmissionRow> pE) {
            // Blind copy done because done before we have determined pollutantNames.
            EmissionsReportPeriod p = new EmissionsReportPeriod();
            totals.copyEmissions(pollutantNames, pE, p);
            //summaryAll.add(totals);
        }
        
        void generateSummary(EmissionsReport report) {
            ArrayList<EmissionRow> pE;
            FireRow[] periodFireRows = null;
            Integer percentDiff = new Integer(1);

            for(EmissionsReportEUGroup g : report.getEuGroups()) {
                EmissionsReportPeriod p = g.getPeriod();
                if(p != null) {
                    EuSummary euS = new EuSummary(g.getReportEuGroupName(),
                            p.convertSCC());
                    summaryAll.add(euS);
                    try {
                        p.setFireRows(periodFireRows);
                        pE = EmissionRow.getEmissions(report.getReportYear(),
                                p, scReports, false, true, percentDiff, logger);
                        euS.copyEmissions(pollutantNames, pE, p);
                    } catch(ApplicationException ae) {
                        logger.error("failure for emissions inventory " + report.getEmissionsRptId()
                                + ", period " + p.getEmissionPeriodId(), ae);
                        // effect for user is a blank line of emissions in report
                    }
                }
            }

            for(EmissionsReportEU eu : report.getEus()) {
                if (eu.getPeriods() == null || eu.getPeriods().isEmpty()) {
                    continue; // skip ones that are just for groups
                }
                for (EmissionsReportPeriod p : eu.getPeriods()) {
                    EuSummary euS = new EuSummary(eu.getEpaEmuId(),
                            p.convertSCC());
                    summaryAll.add(euS);
                    try {
                        p.setFireRows(periodFireRows);
                        pE = EmissionRow.getEmissions(report.getReportYear(),
                                p, scReports, false, true, percentDiff, logger);
                        euS.copyEmissions(pollutantNames, pE, p);
                    } catch(ApplicationException ae) {
                        logger.error("failure for emissions inventory " + report.getEmissionsRptId()
                                + ", period " + p.getEmissionPeriodId(), ae);
                        // effect for user is a blank line of emissions in report
                    }
                }
            }

        }

        protected void generateSummaryTablePDF(Document document)
        throws DocumentException{
            summaryAll.add(totals);  // add the total line last.
            Cell cell;
            int numLinesPerPage = 29;
            int maxColumns = 11;
            int numEmissionColumns = pollutantNames.size();
            int numSplitTables = (numEmissionColumns + maxColumns -  1) / maxColumns;
            if(numSplitTables == 0) {
                numSplitTables = 1;
            }
            int numLinesPerTable = numLinesPerPage - numSplitTables - 1; // account for space between
            numLinesPerTable = numLinesPerTable - numSplitTables;  // account for headings
            numLinesPerTable = numLinesPerTable/numSplitTables;  // how many per table
            if(numLinesPerTable < 5) {
                numLinesPerTable = summaryAll.size();
            }
            
            // How do we break up the units into separate tables for a single unit is all on one page
            int beginIndexT[] = new int[summaryAll.size() + 1]; // "+1" for special case of no emission rows
            beginIndexT[0] = 0;
            int beginAtIndexT = 0;
            while (beginIndexT[beginAtIndexT] < summaryAll.size()) {

                int endAtT = beginIndexT[beginAtIndexT] + numLinesPerTable - 1;
                if(endAtT > summaryAll.size() - 1) {
                    endAtT = summaryAll.size() - 1;
                }
                beginAtIndexT++;
                beginIndexT[beginAtIndexT] = endAtT + 1;
            }
            
            beginAtIndexT = 0;
            while(beginIndexT[beginAtIndexT] < summaryAll.size()) {
                ListItem item1 = new ListItem();
                List sections = new List(false, 20);

                int beginAtT = beginIndexT[beginAtIndexT];
                beginAtIndexT++;
                int endAtT = beginIndexT[beginAtIndexT] - 1;

                int colPerTable = (numEmissionColumns + numSplitTables - 1) / numSplitTables;
                int beginIndex[] = new int[10];
                beginIndex[0] = 0;
                int beginAtIndex = 0;
                while (beginIndex[beginAtIndex] < numEmissionColumns) {

                    int endAt = beginIndex[beginAtIndex] + colPerTable - 1;
                    if(endAt > numEmissionColumns - 1) {
                        endAt = numEmissionColumns - 1;
                    }
                    beginAtIndex++;
                    beginIndex[beginAtIndex] = endAt + 1;
                }
                beginAtIndex = 0;
                while(beginIndex[beginAtIndex] < numEmissionColumns) {
                    int beginAt = beginIndex[beginAtIndex];
                    beginAtIndex++;
                    int endAt = beginIndex[beginAtIndex] - 1;
                    int numColumnsThisTable = endAt - beginAt + 1;
                    Table table = new Table(numColumnsThisTable + 2);
                    table.setAlignment(Element.ALIGN_LEFT);

                    float[] widths = new float[numColumnsThisTable + 2];
                    widths[0] = 6.9f;
                    widths[1] = 7.37f;
                    float columnWidth = (100f - (widths[0] + widths[1]))/colPerTable;
                    float totWidth = widths[0] + widths[1];
                    for(int j=0; j< numColumnsThisTable; j++) {
                        widths[j + 2] = columnWidth;
                        totWidth = totWidth + columnWidth;
                    }
                    if(totWidth > 100f) {
                        totWidth = 100f;
                    }
                    table.setWidth(totWidth);
                    table.setWidths(widths);
                    table.setPadding(2);
                    // 1.0f logger.error("table.getBorderWidthTop()" + table.getBorderWidthTop());
                    table.setBorderWidth(0f);
                    
                    if(beginAtIndex == 1){ // just for first table
                        if(numSplitTables > 1 && numLinesPerTable < summaryAll.size()) {
                            document.newPage();
                        }
                        if(beginAtIndexT == 1) {
                            cell = createCell(defaultLeading, "Report Pollutant Summary: Total Emissions (Tons)", boldFont);
                        } else {
                            cell = createCell(defaultLeading, "Report Pollutant Summary (continued)", boldFont);
                        }
                        cell.setColspan(numColumnsThisTable + 2);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);
                    }

                    cell = createCell(defaultLeading, "Unit", boldFont);
                    table.addCell(cell);
                    cell = createCell(defaultLeading, "SCC Id", boldFont);
                    table.addCell(cell);
                    for(int j=beginAt; j<= endAt; j++) {
                        String s = pollutantNames.get(j);
                        cell = createCell(defaultLeading, s, boldFont);
                        table.addCell(cell);
                    }

                    for(int j=beginAtT; j<= endAtT; j++) {
                        EuSummary euS = summaryAll.get(j);
                        euS.addRow(table, beginAt, endAt);
                    }

                    item1.add(table);
                }
                sections.add(item1);
                document.add(sections);
            }  
        }
    }
    
    protected class EuSummary {
        // This assumes that each EU/SCC will have pollutant information in a row
        // Notice that since just the required pollutants are in the table, all
        // the processes will have these same pollutants in the same order.
        String EuGrpName;
        String sccId;
        ArrayList<String> pollutantTotals = new ArrayList<String>(10);
        
        EuSummary(String name, String sccId) {
            EuGrpName = name;
            this.sccId = sccId;
        }
        
        void copyEmissions(ArrayList<String> pollutantNames, ArrayList<EmissionRow> pE,
                EmissionsReportPeriod p) {
            boolean populateNames = false;
            if(pollutantNames.size() == 0) {
                populateNames = true;
            }
            int rowNum = 0;
            for(EmissionRow eR : pE) {
                if(eR.getOrder() == null) {
                    break;  // End of report defined pollutants
                }
                if(populateNames) {
                    // We do report totals first to get all pollutants
                    pollutantNames.add(eR.getPollutantCd());
                } else {
                    while(rowNum < pollutantNames.size()) {
                        if(!pollutantNames.get(rowNum).equals(eR.getPollutantCd())) {
                            // User just sees missing emissions
                            pollutantTotals.add(""); // skipping over
                            rowNum++;
                        } else {
                            break;
                        }
                    }
                    if(rowNum >= pollutantNames.size()) {
                        logger.error("For EmissionsReportPeriod " + p.getEmissionPeriodId() +
                                ", the pollutantCode=" +
                                eR.getPollutantCd() + " not found in pollutantNames");
                        break;
                    }
                }
                double tot = eR.getFugitiveEmissionsV() + eR.getStackEmissionsV();
                if(eR.getEmissionsUnitNumerator().equals(EmissionUnitReportingDef.POUNDS)) {
                    tot = EmissionUnitReportingDef.convert(EmissionUnitReportingDef.POUNDS,
                            tot, EmissionUnitReportingDef.TONS);  
                }
                pollutantTotals.add(EmissionsReport.numberToString(tot));
                rowNum++;
            }
        }

        void addRow(Table table, int start, int end) {
            Cell cell = createCell(defaultLeading, EuGrpName, boldFont);
            table.addCell(cell);
            cell = createCell(defaultLeading, sccId, boldFont);
            table.addCell(cell);
            String pollTot = "";
            for(int i=start; i <= end; i++) {
                if(i > pollutantTotals.size() - 1) {
                    pollTot = "";
                } else {
                    pollTot = pollutantTotals.get(i);
                }
                cell = createDataCell(pollTot);
                table.addCell(cell);
            }
        }
    }

    @Override
	protected Paragraph getCertificationLanguage() throws DocumentException {
        String str = "";
        if (facility != null) {
        	try {
        		EmissionsReportService emissionsReportBO = ServiceFactory.getInstance().getEmissionsReportService();
        		str = emissionsReportBO.getDAPCAttestationMessage(facility.getPermitClassCd(), report);
        	} catch (ServiceFactoryException e) {
        		logger.error("Exception while geting certification language", e);
        	} catch (RemoteException e) {
        		logger.error("Exception while geting certification language", e);
        	}
        } else {
        	logger.error("No facility data found for report: " + report.getEmissionsRptId()
        			+ ". Using blank attestation message.");
        }
        // format the certification language better
        Paragraph attestation = new Paragraph(defaultLeading, formatCertificationLanguage(str), smallerBold);

        return attestation;
	}
}