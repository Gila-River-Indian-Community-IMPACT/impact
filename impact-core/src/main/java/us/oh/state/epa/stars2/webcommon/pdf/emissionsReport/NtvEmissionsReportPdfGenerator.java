package us.oh.state.epa.stars2.webcommon.pdf.emissionsReport;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedHashMap;

import javax.faces.model.SelectItem;

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
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionTotal;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportNote;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.MultiEstabFacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.EmissionsAttachmentTypeDef;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.NonToxicPollutantDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.NtvReport;

@SuppressWarnings("serial")
public class NtvEmissionsReportPdfGenerator extends PdfGeneratorBase {
	static Logger logger = Logger.getLogger(EmissionsReportPdfGenerator.class);
	protected static final DecimalFormat VALUE_FORMAT = new DecimalFormat(
			"#####0.0####");
	private float leftMargin = 10;
	private float rightMargin = 10;
	private float topMargin = 20;
	private float bottomMargin = 20;
	private Font data1Font = FontFactory.getFont(dataFontFamily, 8, Font.NORMAL);
	private Font normal1Font = FontFactory.getFont(defaultFontFamily, 8, Font.NORMAL);
	private Document document; // PDF document
	private NtvReport ntvRpt;
	private EmissionsReport primary;
    private String category;
	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
	private LinkedHashMap<Integer, String> userList;
	private LinkedHashMap<String, String> contactTitles;
	private boolean containsTS = false;

	@SuppressWarnings("unused")
	private MultiEstabFacilityList[] multiEstabFacilities;

	/**
	 * Constructor is protected to force users to call <code>getGeneratorForClass</code>
	 *
	 */
	public NtvEmissionsReportPdfGenerator() throws DocumentException {
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
	
	private void generate2Columns(Table table, String name1, String value1, boolean attr1, 
    		String name2, String value2, boolean attr2) {
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

	/**
	 * Generate a PDF file rendering the data within <code>TV and SMTV</code>.
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	public boolean generatePdf(Facility facility, NtvReport ntvRpt,
			StringBuffer currentOwnersAddresses, StringBuffer prevOwnerAddress,
			StringBuffer newOwnerAddress, StringBuffer currentBillingAddress,
			StringBuffer newBillingAddress, StringBuffer currentPrimaryAddress,
			StringBuffer newPrimaryAddress, Integer prevRptEvenYear,
			Float prevRptEvenYearTons, String prevRptEvenYearString,
			Collection<SelectItem> fee1PickList, java.util.List<EmissionTotal> fer1,
			java.util.List<EmissionTotal> es1, boolean nonAttain1,
			boolean incluldeES1, Integer prevRptOddYear,
			Float prevRptOddYearTons, String prevRptOddYearString,
			Collection<SelectItem> fee2PickList, java.util.List<EmissionTotal> fer2,
			java.util.List<EmissionTotal> es2, boolean nonAttain2,
			boolean incluldeES2, OutputStream os) throws IOException,
			DocumentException {
		document = new Document();
		PdfWriter.getInstance(document, os);

		document.setMargins(leftMargin, rightMargin, topMargin, bottomMargin);
		document.open();
		
		primary = ntvRpt.getPrimary();
		
        
		Table titleTable = createTitleTable(category + " Emissions Inventory (" + 
				+ primary.getEmissionsRptId() + ") for " + ntvRpt.getYears().toString(), 
				"Facility Name: " + facility.getName() + "\nID: " + facility.getFacilityId());
		titleTable.setAutoFillEmptyCells(false);
		document.add(titleTable);
		
		this.ntvRpt = ntvRpt;
		
		if (!isAttestationOnly()) {
			printNtvReport(facility, currentOwnersAddresses, prevOwnerAddress, newOwnerAddress, 
					currentBillingAddress, newBillingAddress, currentPrimaryAddress, newPrimaryAddress,
					prevRptEvenYear, prevRptEvenYearTons, prevRptEvenYearString,
					prevRptOddYear, prevRptOddYearTons, prevRptOddYearString,
					fee1PickList, fee2PickList, fer1, fer2,
					es1, nonAttain1, incluldeES1,
					es2, nonAttain2, incluldeES2);
		}
		
//		 close the document
        if (document != null) {
            document.close();
        }
        return containsTS;
	}

	private void printNtvReport(Facility facility,
	        StringBuffer currentOwnersAddresses, StringBuffer prevOwnerAddress,
			StringBuffer newOwnerAddress, 
			StringBuffer currentBillingAddress, StringBuffer newBillingAddress, 
			StringBuffer currentPrimaryAddress, StringBuffer newPrimaryAddress, 
			Integer prevRptEvenYear, Float prevRptEvenYearTons, String prevRptEvenYearString,
			Integer prevRptOddYear, Float prevRptOddYearTons, String prevRptOddYearString,
			Collection<SelectItem> fee1PickList, Collection<SelectItem> fee2PickList,
			java.util.List<EmissionTotal> fer1, java.util.List<EmissionTotal> fer2,
			java.util.List<EmissionTotal> es1, boolean nonAttain1, boolean incluldeES1,
			java.util.List<EmissionTotal> es2, boolean nonAttain2, boolean incluldeES2
				) throws IOException, DocumentException {
		// define footer - NOTE: footer must be added to document after
		// PdfWriter.getInstance and BEFORE document.open so footer will
		// appear on all pages (including first page)
        String facilityLabel = facility.getName() + "(" + facility.getFacilityId() + ")";
		HeaderFooter footer = new HeaderFooter(new Phrase(
                facilityLabel
						+ "       " + " Page ", normalFont),
				new Phrase("       "
						+ category + " Emissions Inventory for " + ntvRpt.getYears().toString(), normalFont));
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setBorder(0);
		document.setFooter(footer);

		document.setPageCount(0);
		document.newPage();

		Table titleTable = createTitleTable("Emissions Inventory : "
				+ primary.getEmissionsRptId(), "");
		document.add(titleTable);

		List sections = new List(false, 20);
		sections.add(generateHeaderReportData());
		sections.add(generateOwnerShutDownChgs());
		if (currentOwnersAddresses != null) {
			sections.add(generateInfo("Current Owner(s) of Record", currentOwnersAddresses.toString()));
		}
		if (prevOwnerAddress != null) {
			sections.add(generateInfo("Previous Owner Forwarding Address", prevOwnerAddress.toString()));
		}
		if (newOwnerAddress != null) {
			sections.add(generateInfo("New Owner Information", newOwnerAddress.toString()));
		}
		if (currentBillingAddress != null) {
			sections.add(generateInfo("Current Billing Contact Information of Record", currentBillingAddress.toString()));
		}
		if (newBillingAddress != null) {
			sections.add(generateInfo("My Updated Billing Contact Information", newBillingAddress.toString()));
		}
		if (currentPrimaryAddress != null) {
			sections.add(generateInfo("Current Primary Contact Information of Record", currentPrimaryAddress.toString()));
		}
		if (newPrimaryAddress != null) {
			sections.add(generateInfo("My Updated Primary Contact Information", newPrimaryAddress.toString()));
		}
		if (ntvRpt.getReport1() != null || ntvRpt.getReport2() != null) {
			sections.add(generatePreviousYearsEmiInfo(prevRptEvenYear,
					prevRptEvenYearTons, prevRptEvenYearString, prevRptOddYear,
					prevRptOddYearTons, prevRptOddYearString));
			sections.add(generateYearsEmi(fee1PickList, fee2PickList));
		}
		
		if (fer1 != null && fer1.size() > 0) {
			sections.add(generateAddEmiInfo(fer1, ntvRpt.getReport1()));
		}
		
		if (fer2 != null && fer2.size() > 0) {
			sections.add(generateAddEmiInfo(fer2, ntvRpt.getReport2()));
		}
		
		if (ntvRpt.getReport1() != null) {
			sections.add(generateEmiStatement(ntvRpt.getReport1(), es1, nonAttain1, incluldeES1));
		}
		
		if (ntvRpt.getReport2() != null) {
			sections.add(generateEmiStatement(ntvRpt.getReport2(), es2, nonAttain2, incluldeES2));
		}
		
		sections.add(generateAttachments());

		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			sections.add(generateNotes());
		}

		document.add(sections);
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
        generate2Columns(table, "Report Category:", null, false, null, null, false);
        temp = primary.getRptReceivedStatusDate() == null ? null : dateFormat1.format(primary.getRptReceivedStatusDate());
        temp1 = primary.getRptApprovedStatusDate() == null ? null : dateFormat1.format(primary.getRptApprovedStatusDate());
        String dateName;
        if(primary.isApproved()) {
            dateName = "Approved Date";
        } else {
            dateName = "Completed Date";
        }
        if(temp1 == null) {
            // don't display if no date yet
            dateName = " ";
        }
        generate2Columns(table, "Submitted Date:", temp, false, dateName, temp1, false);
        temp = ReportReceivedStatusDef.getData().getItems().getItemDesc(primary.getRptReceivedStatusCd());
        
        item1.add(table);

        return item1;              
    }
	
	private ListItem generateOwnerShutDownChgs() throws DocumentException {     
        ListItem item1 = new ListItem();
        
        Phrase item1Title = new Paragraph(defaultLeading, "Owner and/or Shutdown Changes", boldFont);
        item1.add(item1Title);
        
        Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        String temp;
        String temp1;
        temp = primary.getReceiveDate() == null ? null : dateFormat1.format(primary.getReceiveDate());
        generate2Columns(table, "Received Date by DAPC:", temp, false, 
        				"New Facility Name", primary.getFacilityNm(), false);
        if (primary.getTransferDate() == null) {
        	temp = "false";
        	temp1 = null;
        } else {
        	temp = "true";
        	temp1 = dateFormat1.format(primary.getTransferDate());
        }
        generate2Columns(table, "Ownership Change:", temp, true, "Ownership Change Transfer Date", temp1, false);
        temp = "";
        if (primary.getTransferDate() != null) {
        	if (primary.isNewOwner()) {
        		temp = "I/The company purchased this facility";
        	} else {
        		temp = "I/The company sold this facility";
        	}
        }
        temp1 = primary.isProvideBothYears() ? "true" : "false";
        generate2Columns(table, "Ownership Change Reason:", temp, false, "Report and Pay Agreement:", temp1, true);
        
        if (primary.getShutdownDate() == null) {
        	temp = "false";
        	temp1 = null;
        } else {
        	temp = "true";
        	temp1 = dateFormat1.format(primary.getShutdownDate());
        }
        generate2Columns(table, "Contaminant Sources Shutdown:", temp, true, "Shutdown Date:", temp1, false);
        
        item1.add(table);

        return item1;              
    }
	
	private ListItem generatePreviousYearsEmiInfo(
			Integer prevRptEvenYear, Float prevRptEvenYearTons, String prevRptEvenYearString,
			Integer prevRptOddYear, Float prevRptOddYearTons, String prevRptOddYearString) throws DocumentException {
		ListItem item1 = new ListItem();
		Phrase item1Title = new Paragraph(defaultLeading, "Previous Years Emissions Information", boldFont);
        item1.add(item1Title);
        
        Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        String temp;
        String temp1;
        
        if (prevRptEvenYearTons == null) {
        	temp = prevRptEvenYearString;
        } else {
        	temp = prevRptEvenYearTons.toString();
        }
        if (prevRptOddYearTons == null) {
        	temp1 = prevRptOddYearString;
        } else {
        	temp1 = prevRptOddYearTons.toString();
        }
        generate2Columns(table, "Year " + prevRptEvenYear + ":", temp, false, 
        		"Year " + prevRptOddYear + ":", temp1, false);
        
        item1.add(table);

        return item1;
	}
	
	private String getFeeDesc(Collection<SelectItem> feePickList,
			EmissionsReport rpt) {
		String temp = null;
		String temp1;

		for (SelectItem st : feePickList) {
			temp1 = st.getValue().toString();
			if (temp1.equals(rpt.getFeeId().toString())) {
				temp = st.getLabel();
				break;
			}
		}
		return temp;
	}
	
	private ListItem generateYearsEmi(Collection<SelectItem> fee1PickList, 
			 Collection<SelectItem> fee2PickList) throws DocumentException {
		ListItem item1 = new ListItem();
		Phrase item1Title = new Paragraph(defaultLeading, "Total for all the pollutants facility-wide", boldFont);
        item1.add(item1Title);
        
        Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        String temp = null;
        
        EmissionsReport rpt1 = ntvRpt.getReport1();
        
        if(rpt1 != null && rpt1.getFeeId() != null) {
        	temp = getFeeDesc(fee1PickList, rpt1);
        	generate2Columns(table, "Year " + rpt1.getReportYear() + ":", temp, false, 
            		null, null, false);
        }

        EmissionsReport rpt2 = ntvRpt.getReport2();
        
        if(rpt2 != null && rpt2.getFeeId() != null) {
        	temp = getFeeDesc(fee2PickList, rpt2);
        	generate2Columns(table, "Year " + rpt2.getReportYear() + ":", temp, false, 
            		null, null, false);
        }     
        
        item1.add(table);

        return item1;
	}
	
	private ListItem generateAddEmiInfo(java.util.List<EmissionTotal> fer, EmissionsReport rpt) 
			throws DocumentException{
        ListItem item1 = new ListItem();
        Cell cell;
        String temp;
        
        Phrase item1Title = new Paragraph(defaultLeading, 
        			"Year " + rpt.getReportYear() + " Additional Emissions Information", boldFont);
        item1.add(item1Title);
        
        Table table = new Table(2);
        table.setWidth(98);
        float[] widths = {80, 20};
        table.setWidths(widths);
        table.setPadding(2);
        table.setAlignment(Element.ALIGN_LEFT);
        
        cell = createCell("Pollutant, amount expressed in tons per year");
        table.addCell(cell);
        cell = createCell("TPY");
        table.addCell(cell);
             
        for (EmissionTotal et : fer) {
        	temp = NonToxicPollutantDef.getData().getItems().getItemDesc(et.getPollutantCd());
            cell = createCell(defaultLeading, temp, data1Font);
            table.addCell(cell);
            temp = et.getTotalEmissions() == null ? null : et.getTotalEmissions().toString();
            cell = createCell(defaultLeading, temp, data1Font);
            table.addCell(cell);
        }
        
        item1.add(table);
        
        table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        temp = " ";
        if(rpt.getTotalEmissions() != null) {
        	temp = EmissionRow.getTotalString(rpt.getTotalEmissions().doubleValue());
        }
        
        generate2Columns(table, "Total Chargeable Pollutants:", temp, false, 
        		null, null, false);
        
        item1.add(table);
        
        return item1;
    }
	
	private ListItem generateEmiStatement(
			EmissionsReport rpt, java.util.List<EmissionTotal> es, 
			boolean nonAttain, boolean incluldeES) throws DocumentException {     
        ListItem item1 = new ListItem();
        
        Phrase item1Title = new Paragraph(defaultLeading, "Year " + rpt.getReportYear() + " Emission Statement", boldFont);
        item1.add(item1Title);
        
        Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        String temp = nonAttain == false ? "false" : "true";
        String temp1 = incluldeES == false ? "false" : "true";
        generate2Columns(table, "NonAttainment County:", temp, true, "Report Included", temp1, true);      
        
        item1.add(table);
        
        if (es == null || es.isEmpty()) {
        	return item1;
        }
        
        table = new Table(2);
        table.setWidth(98);
        float[] widths = {80, 20};
        table.setWidths(widths);
        table.setPadding(2);
        table.setAlignment(Element.ALIGN_LEFT);
        
        Cell cell = createCell("Pollutant, amount expressed in tons per year");
        table.addCell(cell);
        cell = createCell("TPY");
        table.addCell(cell);
             
        for (EmissionTotal et : es) {
        	temp = NonToxicPollutantDef.getData().getItems().getItemDesc(et.getPollutantCd());
            cell = createCell(defaultLeading, temp, data1Font);
            table.addCell(cell);
            temp = et.getTotalEmissions() == null ? null : et.getTotalEmissions().toString();
            cell = createCell(defaultLeading, temp, data1Font);
            table.addCell(cell);
        }
        
        item1.add(table);     

        return item1;              
    }
	
	private ListItem generateInfo(String header, String ownerInfo) throws DocumentException {     
        ListItem item1 = new ListItem();
        
        Phrase item1Title = new Paragraph(defaultLeading, header, boldFont);
        item1.add(item1Title);
        
        Table table = new Table(4);
        table.setBorder(0);
        table.setPadding(2);
        table.setWidth(98);
        
        if (ownerInfo != null)
        	generate2Columns(table, "", ownerInfo.replaceAll("<br>", "\n"), false, null, null, false);
        
        item1.add(table);

        return item1;              
    }
	
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
        
        for (EmissionsReportNote note : primary.getNotes()) {
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
    	
    	Table table = new Table(5);
    	table.setWidth(100);
    	float[] widths = {30, 20, 10, 10, 30};
    	table.setWidths(widths);
    	table.setPadding(2);
    	table.setAlignment(Element.ALIGN_LEFT);
    	
    	cell = createCell("Description");
    	table.addCell(cell);
    	cell = createCell("Type");
    	table.addCell(cell);
    	cell = createCell("Public Document");
    	table.addCell(cell);
    	cell = createCell("Trade Seceret Document");
    	table.addCell(cell);
    	cell = createCell("Trade secret Justification");
    	table.addCell(cell);
    	
    	EmissionsDocumentRef attachments[] = primary.getAttachments().toArray(new EmissionsDocumentRef[0]);
    	
    	for (EmissionsDocumentRef attach : attachments) {
    		if(attach.getTradeSecret()) containsTS = true;
    		cell = createCell(defaultLeading, attach.getDescription(), data1Font);
    		table.addCell(cell);
    		cell = createCell(defaultLeading, EmissionsAttachmentTypeDef.getData().getItems().getItemDesc(attach.getEmissionsDocumentTypeCD()), data1Font);
    		table.addCell(cell);
    		cell = createCheckCell((attach.getDocumentId() != null ? true : false));
    		table.addCell(cell);
    		cell = createCheckCell(attach.getTradeSecret());
    		table.addCell(cell);
    		cell = createCell(defaultLeading, attach.getTradeSecretReason(), data1Font);
    		table.addCell(cell);
    	}
    	
    	item1.add(table);
    	
    	return item1;
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
    	String confidentialMsg = SystemPropertyDef.getSystemPropertyValue("ConfidentialAttestationMessage", null);
    	if (confidentialMsg == null) {
    		confidentialMsg = "";
        }
        String str = SystemPropertyDef.getSystemPropertyValue("NTVERAttestationMessage", null);
        if (str == null) {
        	logger.error("No value specified for parameter: NTVERAttestationMessage");
        	str = "";
        }
        str = confidentialMsg + str;
        
        // format the certification language better
        Paragraph attestation = new Paragraph(defaultLeading, formatCertificationLanguage(str), smallerBold);

        return attestation;
	}
}