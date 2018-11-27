package us.oh.state.epa.stars2.webcommon.pdf.project;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.AgencyDef;
import us.oh.state.epa.stars2.def.BLMFieldOfficeDef;
import us.oh.state.epa.stars2.def.BasicUsersDef;
import us.oh.state.epa.stars2.def.BudgetFunctionDef;
import us.oh.state.epa.stars2.def.ContractStatusDef;
import us.oh.state.epa.stars2.def.ContractTypeDef;
import us.oh.state.epa.stars2.def.DivisionDef;
import us.oh.state.epa.stars2.def.NEPACateogryTypeDef;
import us.oh.state.epa.stars2.def.NEPALevelTypeDef;
import us.oh.state.epa.stars2.def.NationalForestDef;
import us.oh.state.epa.stars2.def.NationalParkDef;
import us.oh.state.epa.stars2.def.ProjectAttachmentTypeDef;
import us.oh.state.epa.stars2.def.ProjectGrantStatusDef;
import us.oh.state.epa.stars2.def.ProjectLetterTypeDef;
import us.oh.state.epa.stars2.def.ProjectOutreachCategoryDef;
import us.oh.state.epa.stars2.def.ProjectStageDef;
import us.oh.state.epa.stars2.def.ProjectStateDef;
import us.oh.state.epa.stars2.def.ProjectTrackingEventTypeDef;
import us.oh.state.epa.stars2.def.ProjectTypeDef;
import us.oh.state.epa.stars2.def.ShapeDef;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;
import us.oh.state.epa.stars2.webcommon.pdf.PdfGeneratorBase;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Budget;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Contract;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.GrantProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.LetterProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.NEPAProject;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.Project;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectAttachment;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectAttachmentInfo;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectNote;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectTrackingEvent;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementTags;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

@SuppressWarnings("serial")
public class ProjectTrackingPdfGenerator extends PdfGeneratorBase {
	static Logger logger = Logger.getLogger(ProjectTrackingPdfGenerator.class);
    protected static final DecimalFormat VALUE_FORMAT = new DecimalFormat("#####0.0####");
    protected static String reportTypes = "";
    private float leftMargin = 10;
    private float rightMargin = 10;
    private float topMargin = 20;
    private float bottomMargin = 20;
    private Font data1Font = FontFactory.getFont(dataFontFamily, 8, Font.NORMAL);
    private Document document;    // PDF document
    private SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private LinkedHashMap<String, String> contactTitles;    

    /**
     * Constructor is protected to force users to call <code>getGeneratorForClass</code>
     *
     */
    public ProjectTrackingPdfGenerator() throws DocumentException{
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

    public boolean generatePdf(Project project, OutputStream os, 
    		boolean includeAllAttachments) 
    				throws IOException, DocumentException {
        document = new Document();
        PdfWriter.getInstance(document, os);
        
        document.setMargins(leftMargin, rightMargin, topMargin, bottomMargin);
        document.open();
        
        // add a title page
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Timestamp docDate = null;
        if (docDate == null) {
            docDate = new Timestamp(System.currentTimeMillis());
        }
        String projectType = getProjectType(project);
        String[] title = {
        		projectType + 
        		" Project Tracking " +
        	    project.getProjectNumber(),
                dateFormat.format(docDate)};
        setIncludeAllAttachments(includeAllAttachments);
        Table titleTable = createTitleTable(title);
        titleTable.setAutoFillEmptyCells(false);
        document.add(titleTable);

        printProject(project);
        
        // close the document
        if (document != null) {
            document.close();
        }
        
        return true;
    }

	private String getProjectType(Project project) {
		String projectType = "";
        if (ProjectTypeDef.NEPA.equals(project.getProjectTypeCd())) {
        	projectType = "NEPA";
        }
        else
        if (ProjectTypeDef.CONTRACTS.equals(project.getProjectTypeCd())) {
        	projectType = "Contracts";
        }
        else
        if (ProjectTypeDef.GRANTS.equals(project.getProjectTypeCd())) {
        	projectType = "Grants";
        }
        else
        if (ProjectTypeDef.LETTERS.equals(project.getProjectTypeCd())) {
        	projectType = "Letters";
        }
		return projectType;
	}
    
    private void printProject(Project project)throws IOException, DocumentException {

		// define footer - NOTE: footer must be added to document after
		// PdfWriter.getInstance and BEFORE document.open so footer will
		// appear on all pages (including first page)
        String projectType = getProjectType(project);
		String subtitle = projectType + " Project Tracking";
		HeaderFooter footer = new HeaderFooter(new Phrase(
				"Air Quality Division" + "                          "
						+ " Page ", normalFont), new Phrase(
				"                                      " + subtitle + " "
						+ project.getProjectNumber(), normalFont));
		footer.setAlignment(Element.ALIGN_CENTER);
		footer.setBorder(0);
		document.setFooter(footer);
		document.setPageCount(0);
		document.newPage();

		// add a title to the document
		String title = "Air Quality Division";
		Table titleTable = createTitleTable(title, subtitle);
		document.add(titleTable);

		document.add(printHeader(project));
		
		List sections = new List(false, 20);

		sections.add(printProjectInfo(project));
		
		sections.add(printProjectLeads(project));

		sections.add(printProjectTypeSpecificInfo(project));

		sections.add(printTrackingEvents(project));
		
		sections.add(printAttachments(project));

		sections.add(printNotes(project.getNotes()));

		document.add(sections);
    }

	private ListItem printTrackingEvents(Project project) throws DocumentException {
		ListItem trackingEventsItem = new ListItem();
		Phrase trackingEventsItemTitle = new Paragraph(defaultLeading, "Tracking Events", boldFont);
		trackingEventsItem.add(trackingEventsItemTitle);
		

		int numCols = 9;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 16, 24, 16, 16, 16, 24, 16, 20, 16 };
		table.setWidths(widths);

		table.addCell(createHeaderCell("Event ID"));
		table.addCell(createHeaderCell("Event Description"));
		table.addCell(createHeaderCell("Event Date"));
		table.addCell(createHeaderCell("Event Type"));
		table.addCell(createHeaderCell("Event Status"));
		table.addCell(createHeaderCell("Issues To Address"));
		table.addCell(createHeaderCell("Response Due Date"));
		table.addCell(createHeaderCell("Attachment ID(s)"));
		table.addCell(createHeaderCell("Added By"));
		java.util.List<ProjectTrackingEvent> events = project.getTrackingEvents();
		if (events.size() == 0) {
			Cell cell = createCell("No events found");
			cell.setColspan(9);
			table.addCell(cell);
		} else {
			for (ProjectTrackingEvent event : events) {
				
				table.addCell(createCell(defaultLeading, data1Font, event.getEventNbr()));

				table.addCell(createCell(defaultLeading, data1Font, event.getEventDescription()));

				table.addCell(createCell(defaultLeading, data1Font, 
						dateFormat1.format(event.getEventDate())));
				
				table.addCell(createCell(defaultLeading, data1Font, ProjectTrackingEventTypeDef
	    				.getData().getItems().getItemDesc(event.getEventTypeCd())));

				table.addCell(createCell(defaultLeading, data1Font, event.getEventStatus()));
				
				table.addCell(createCell(defaultLeading, data1Font, event.getIssuesToAddress()));
				
				table.addCell(createCell(defaultLeading, data1Font, 
						null == event.getResponseDueDate()? "" :
						dateFormat1.format(event.getResponseDueDate())));
				
				java.util.List<ProjectAttachmentInfo> attachmentInfos = 
						event.getAssociatedAttachmentsInfo();
				java.util.List<String> attachmentIds = new ArrayList<String>();
				for (ProjectAttachmentInfo info : attachmentInfos) {
					attachmentIds.add(""+info.getDocumentId());
				}
				table.addCell(createCell(defaultLeading, data1Font, attachmentIds.toArray(new String[0])));
				
				table.addCell(createCell(defaultLeading, data1Font, BasicUsersDef
						.getAllUserData().getItems().getItemDesc(event.getAddedByUserId())));
			}
		}
		trackingEventsItem.add(table);
		
		
		
		
		return trackingEventsItem;
	}

	private ListItem printProjectTypeSpecificInfo(Project project) throws DocumentException {
		ListItem projectTypeSpecificInfoItem = new ListItem();
		Phrase projectTypeSpecificInfoItemTitle = new Paragraph(defaultLeading, "Project Type Specific Information", boldFont);
		projectTypeSpecificInfoItem.add(projectTypeSpecificInfoItemTitle);

		if (ProjectTypeDef.CONTRACTS.equals(project.getProjectTypeCd()) &&
        		(project instanceof Contract)) {

    		Table projectInfoTable = new Table(4);
    		projectInfoTable.setBorder(Rectangle.NO_BORDER);
    		projectInfoTable.setWidth(80);
    		projectInfoTable.setPadding(2);
    		float[] widths = { 1, 1, 1, 1 };
    		projectInfoTable.setWidths(widths);
    		

    		projectInfoTable.addCell(createHeaderCellLabel("Contract Type:"));
    		projectInfoTable.addCell(createHeaderCellValue(ContractTypeDef
    				.getData().getItems().getItemDesc(((Contract)project).getContractTypeCd())));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Vendor Name:"));
    		projectInfoTable.addCell(createHeaderCellValue(((Contract)project).getVendorName()));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Vendor Number:"));
    		projectInfoTable.addCell(createHeaderCellValue(((Contract)project).getVendorNumber()));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Contract Status:"));
    		projectInfoTable.addCell(createHeaderCellValue(ContractStatusDef
    				.getData().getItems().getItemDesc(((Contract)project).getContractStatusCd())));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Contract Expiration Date:"));
    		projectInfoTable.addCell(createHeaderCellValue(
    				null == ((Contract)project).getContractExpirationDate()? "" :
    					dateFormat1.format(((Contract)project).getContractExpirationDate())));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Monitoring Operations End Date:"));
    		projectInfoTable.addCell(createHeaderCellValue(
    				null == ((Contract)project).getMonitoringOperationsEndDate()? "" :
    					dateFormat1.format(((Contract)project).getMonitoringOperationsEndDate())));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Total Award:"));
    		projectInfoTable.addCell(createHeaderCellValue(
    				null == ((Contract)project).getTotalAward()? "" :
    					((Contract)project).getTotalAward().toString()));

    		projectInfoTable.addCell(createHeaderCellLabel("MSA Number:"));
    		projectInfoTable.addCell(createHeaderCellValue(((Contract)project).getMSANumber()));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("OCIO Approval:"));
    		projectInfoTable.addCell(createHeaderCellValue(
    				null == ((Contract)project).getOCIOApproval()? "" :
    				"Y".equals(((Contract)project).getOCIOApproval())? "Yes" : "No"));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("CON Number:"));
    		projectInfoTable.addCell(createHeaderCellValue(
    				null == ((Contract)project).getContractNumber()? "" :
    					((Contract)project).getContractNumber().toString()));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("AG Heat Ticket Number:"));
    		projectInfoTable.addCell(createHeaderCellValue(
    				null == ((Contract)project).getAGHeatTicketNumber()? "" :
    					((Contract)project).getAGHeatTicketNumber().toString()));
    		
    		printAcctContacts(project, projectInfoTable);
    		
    		projectTypeSpecificInfoItem.add(projectInfoTable);

    		printBudgets(project, projectTypeSpecificInfoItem);
    		
    	}
        else
        if (ProjectTypeDef.GRANTS.equals(project.getProjectTypeCd()) &&
        		(project instanceof GrantProject)) {

    		Table projectInfoTable = new Table(4);
    		projectInfoTable.setBorder(Rectangle.NO_BORDER);
    		projectInfoTable.setWidth(80);
    		projectInfoTable.setPadding(2);
    		float[] widths = { 1, 1, 1, 1 };
    		projectInfoTable.setWidths(widths);
    		

    		projectInfoTable.addCell(createHeaderCellLabel("Outreach Category:"));
    		projectInfoTable.addCell(createHeaderCellValue(ProjectOutreachCategoryDef
    				.getData().getItems().getItemDesc(((GrantProject)project).getOutreachCategoryCd())));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Accountant Contacts:"));
    		java.util.List<String> acctContacts = new ArrayList<String>();
    		for (Stars2Object cd : ((GrantProject)project).getAccountantUserIds()) {
    			acctContacts.add(BasicUsersDef
						.getAllUserData().getItems().getItemDesc(cd.getValue()));
    		}
    		projectInfoTable.addCell(createHeaderCellValue(acctContacts.toArray(new String[0])));

    		
    		projectInfoTable.addCell(createHeaderCellLabel("Grant Status:"));
    		projectInfoTable.addCell(createHeaderCellValue(ProjectGrantStatusDef
    				.getData().getItems().getItemDesc(((GrantProject)project).getGrantStatusCd())));
    		
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Total Dollar Amount:"));
    		projectInfoTable.addCell(createHeaderCellValue(
    				null == ((GrantProject)project).getTotalAmount()? "" :
    					((GrantProject)project).getTotalAmount().toString()));

    		
    		projectTypeSpecificInfoItem.add(projectInfoTable);

    	}
        else
        if (ProjectTypeDef.LETTERS.equals(project.getProjectTypeCd()) &&
        		(project instanceof LetterProject)) {

    		Table projectInfoTable = new Table(4);
    		projectInfoTable.setBorder(Rectangle.NO_BORDER);
    		projectInfoTable.setWidth(80);
    		projectInfoTable.setPadding(2);
    		float[] widths = { 1, 1, 1, 1 };
    		projectInfoTable.setWidths(widths);
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Letter Type:"));
    		projectInfoTable.addCell(createHeaderCellValue(ProjectLetterTypeDef
    				.getData().getItems().getItemDesc(((LetterProject)project).getLetterTypeCd())));
    		
    		
    		projectTypeSpecificInfoItem.add(projectInfoTable);
    	}
        else
        if (ProjectTypeDef.NEPA.equals(project.getProjectTypeCd()) &&
        		(project instanceof NEPAProject)) {
        	
    		Table projectInfoTable = new Table(4);
    		projectInfoTable.setBorder(Rectangle.NO_BORDER);
    		projectInfoTable.setWidth(80);
    		projectInfoTable.setPadding(2);
    		float[] widths = { 1, 1, 1, 1 };
    		projectInfoTable.setWidths(widths);
    		
    		projectInfoTable.addCell(createHeaderCellLabel("NEPA Category:"));
    		projectInfoTable.addCell(createHeaderCellValue(NEPACateogryTypeDef
    				.getData().getItems().getItemDesc(((NEPAProject)project).getCategoryCd())));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Project Stage:"));
    		projectInfoTable.addCell(createHeaderCellValue(ProjectStageDef
    				.getData().getItems().getItemDesc(((NEPAProject)project).getProjectStageCd())));

    		projectInfoTable.addCell(createHeaderCellLabel("NEPA Levels:"));
    		java.util.List<String> levels = new ArrayList<String>();
    		for (String cd : ((NEPAProject)project).getLevelCds()) {
    			levels.add(NEPALevelTypeDef.getData().getItems().getItemDesc(cd));
    		}
    		projectInfoTable.addCell(createHeaderCellValue(levels.toArray(new String[0])));

    		projectInfoTable.addCell(createHeaderCellLabel("Modeling Required:"));
    		projectInfoTable.addCell(createHeaderCellValue(
    				null == ((NEPAProject)project).getModelingRequired()? "" :
    					"Y".equals(((NEPAProject)project).getModelingRequired())? "Yes" : "No"));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Lead Agencies:"));
    		java.util.List<String> leadAgencies = new ArrayList<String>();
    		for (String cd : ((NEPAProject)project).getLeadAgencyCds()) {
    			leadAgencies.add(AgencyDef.getData().getItems().getItemDesc(cd));
    		}
    		projectInfoTable.addCell(createHeaderCellValue(leadAgencies.toArray(new String[0])));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("BLM Field Offices:"));
    		java.util.List<String>  blmFieldOffices = new ArrayList<String>();
    		for (String cd : ((NEPAProject)project).getBLMFieldOfficeCds()) {
    			blmFieldOffices.add(BLMFieldOfficeDef.getData().getItems().getItemDesc(cd));
    		}
    		projectInfoTable.addCell(createHeaderCellValue(blmFieldOffices.toArray(new String[0])));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("USFS National Forests:"));
    		java.util.List<String> usfsNationalForests =  new ArrayList<String>();
    		for (String cd : ((NEPAProject)project).getNationalForestCds()) {
    			usfsNationalForests.add(NationalForestDef.getData().getItems().getItemDesc(cd));
    		}
    		projectInfoTable.addCell(createHeaderCellValue(usfsNationalForests.toArray(new String[0])));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("National Park Service Managed Areas:"));
    		java.util.List<String>  nationalParkServiceManagedAreas =  new ArrayList<String>();
    		for (String cd : ((NEPAProject)project).getNationalParkCds()) {
    			nationalParkServiceManagedAreas.add(NationalParkDef.getData().getItems().getItemDesc(cd));
    		}
    		projectInfoTable.addCell(createHeaderCellValue(nationalParkServiceManagedAreas.toArray(new String[0])));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("External Agency Contact:"));
    		projectInfoTable.addCell(createHeaderCellValue(((NEPAProject)project).getExtAgencyContact()));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Phone Number:"));
    		projectInfoTable.addCell(createHeaderCellValue(((NEPAProject)project).getExtAgencyContactPhone()));
    		
    		projectInfoTable.addCell(createHeaderCellLabel("Project Area (Map):"));
    		projectInfoTable.addCell(createHeaderCellValue(ShapeDef
    				.getData().getItems().getItemDesc(((NEPAProject)project).getStrShapeId())));

    		projectTypeSpecificInfoItem.add(projectInfoTable);

    	}
		return projectTypeSpecificInfoItem;
	}

	private void printBudgets(Project project,
			ListItem projectTypeSpecificInfoItem) throws BadElementException {

		int numCols = 4;
		Table budgetTable = new Table(numCols);
		budgetTable.setWidth(98);
		budgetTable.setPadding(2);
		float[] budgetTableWidths = { 9, 27, 19, 9 };
		budgetTable.setWidths(budgetTableWidths);
		int rowId = 0;

		Cell titleCell = createHeaderCell("Budget");
		titleCell.setColspan(4);
		budgetTable.addCell(titleCell);
		
		budgetTable.addCell(createHeaderCell("Row ID"));
		budgetTable.addCell(createHeaderCell("BFY"));
		budgetTable.addCell(createHeaderCell("Function"));
		budgetTable.addCell(createHeaderCell("Amount"));
		java.util.List<Budget> budgets = ((Contract)project).getBudgetList();
		if (budgets.size() == 0) {
			Cell cell = createCell("No budgets found");
			cell.setColspan(4);
			budgetTable.addCell(cell);
		} else {
			for (Budget budget : budgets) {
				budgetTable.addCell(createDataCell("" + ++rowId));
				budgetTable.addCell(createDataCell(budget.getBFY().toString().substring(2)));
				budgetTable.addCell(createDataCell(BudgetFunctionDef
						.getData().getItems().getItemDesc(budget.getBudgetFunctionCd())));
				budgetTable.addCell(createDataCell("" + budget.getAmount()));
			}
		}
		projectTypeSpecificInfoItem.add(budgetTable);
	}

	private void printAcctContacts(Project project, Table projectInfoTable)
			throws BadElementException {
		projectInfoTable.addCell(createHeaderCellLabel("Accountant Contacts:"));
		java.util.List<String> accountants = new ArrayList<String>();
		for (Stars2Object obj : ((Contract)project).getAccountantUserIds()) {
			accountants.add(BasicUsersDef
					.getAllUserData().getItems().getItemDesc(
					obj.getValue()));
		}
		projectInfoTable.addCell(createHeaderCellValue(accountants.toArray(new String[0])));
	}


	private ListItem printProjectInfo(Project project)
			throws BadElementException {
		ListItem projectInfoItem = new ListItem();
		Phrase projectInfoItemTitle = new Paragraph(defaultLeading, "Project Information", boldFont);
		projectInfoItem.add(projectInfoItemTitle);

		Table projectInfoTable = new Table(2);
		projectInfoTable.setBorder(Rectangle.NO_BORDER);
		projectInfoTable.setWidth(100);
		projectInfoTable.setPadding(2);
		projectInfoTable.setAlignment(ElementTags.ALIGN_LEFT);
		float[] widths = { 1, 3 };
		projectInfoTable.setWidths(widths);
		
		projectInfoTable.addCell(createHeaderCellLabel("Project Name:"));
		projectInfoTable.addCell(createHeaderCellValue(project.getProjectName()));
		
		projectInfoTable.addCell(createHeaderCellLabel("Project Status:"));
		projectInfoTable.addCell(createHeaderCellValue(ProjectStateDef
				.getData().getItems().getItemDesc(project.getProjectStateCd())));

		projectInfoTable.addCell(createHeaderCellLabel("Divisions:"));
		java.util.List<String> divisions = new ArrayList<String>();
		for (String cd : project.getProjectDivisionCds()) {
			divisions.add(DivisionDef.getData().getItems().getItemDesc(cd));
		}
		projectInfoTable.addCell(createHeaderCellValue(divisions.toArray(new String[0])));

		projectInfoTable.addCell(createHeaderCellLabel("Project Description:"));
		projectInfoTable.addCell(createHeaderCellValue(project.getProjectDescription()));

		projectInfoTable.addCell(createHeaderCellLabel("Project Summary:"));
		projectInfoTable.addCell(createHeaderCellValue(project.getProjectSummary()));

		projectInfoTable.addCell(createHeaderCellLabel("Link to External Agency Web Site Docs:"));
		projectInfoTable.addCell(createHeaderCellValue(project.getExtAgencyWebsiteUri()));

		projectInfoItem.add(projectInfoTable);
		return projectInfoItem;
	}

	private ListItem printProjectLeads(Project project)
			throws BadElementException {
		ListItem projectLeadsItem = new ListItem();
		Phrase projectLeadsItemTitle = new Paragraph(defaultLeading, "Project Lead(s)", boldFont);
		projectLeadsItem.add(projectLeadsItemTitle);
		
		Table projectLeadsTable = new Table(1);
		projectLeadsTable.setBorder(Rectangle.NO_BORDER);
		projectLeadsTable.setWidth(50);
		projectLeadsTable.setPadding(2);
		projectLeadsTable.setAlignment(ElementTags.ALIGN_LEFT);
		float[] projectLeadsWidths = { 1 };
		projectLeadsTable.setWidths(projectLeadsWidths);

		for (Stars2Object obj : project.getProjectLeadUserIds()) {
			Integer leadUserId = (Integer)obj.getValue();
			projectLeadsTable.addCell(createHeaderCellValue(BasicUsersDef
					.getAllUserData().getItems().getItemDesc(leadUserId)));
		}
		
		projectLeadsItem.add(projectLeadsTable);
		return projectLeadsItem;
	}
    
    
	private Table printHeader(Project project) throws IOException,
		DocumentException {
		Table table = new Table(6);
		table.setBorder(Rectangle.NO_BORDER);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 1, 1, 1, 1, 1, 1 };
		table.setWidths(widths);
		
		table.addCell(createHeaderCellLabel("Project ID:"));
		table.addCell(createHeaderCellValue(project.getProjectNumber()));
		
		table.addCell(createHeaderCellLabel("Creator:"));
		table.addCell(createHeaderCellValue(BasicUsersDef
				.getAllUserData().getItems().getItemDesc(project.getCreatorId())));

        printId(project, table);
		
		table.addCell(createHeaderCellLabel("Project Type:"));
		table.addCell(createHeaderCellValue(ProjectTypeDef.getData()
				.getItems().getItemDesc(project.getProjectTypeCd())));
		
		if (project.getCreatedDate() != null) {
			table.addCell(createHeaderCellLabel("Created Date:"));
			table.addCell(createHeaderCellValue(dateFormat.format(project.getCreatedDate())));
		}
		
		return table;
	}

	private void printId(Project project, Table table) {
		if (ProjectTypeDef.CONTRACTS.equals(project.getProjectTypeCd()) &&
        		(project instanceof Contract)) {
        	table.addCell(createHeaderCellLabel("Contract ID:"));
        	table.addCell(createHeaderCellValue(((Contract)project).getContractId()));
    	}
        else
        if (ProjectTypeDef.GRANTS.equals(project.getProjectTypeCd()) &&
        		(project instanceof GrantProject)) {
        	table.addCell(createHeaderCellLabel("Grant ID:"));
        	table.addCell(createHeaderCellValue(((GrantProject)project).getGrantId()));
    	}
        else
        if (ProjectTypeDef.LETTERS.equals(project.getProjectTypeCd()) &&
        		(project instanceof LetterProject)) {
        	table.addCell(createHeaderCellLabel("Letter ID:"));
        	table.addCell(createHeaderCellValue(((LetterProject)project).getLetterId()));
    	}
        else
        if (ProjectTypeDef.NEPA.equals(project.getProjectTypeCd()) &&
        		(project instanceof NEPAProject)) {
        	table.addCell(createHeaderCellLabel("NEPA ID:"));
        	table.addCell(createHeaderCellValue(((NEPAProject)project).getNEPAId()));
    	}
	}


	private ListItem printAttachments(Project project) throws IOException, DocumentException {
		ListItem item1 = new ListItem();

		Phrase item1Title = new Paragraph(defaultLeading, "Attachments",
				boldFont);
		item1.add(item1Title);

		int numCols = 6;
		Table table = new Table(numCols);
		table.setWidth(98);
		table.setPadding(2);
		float[] widths = { 12, 16, 16, 16, 8, 16 };
		table.setWidths(widths);

		table.addCell(createHeaderCell("Attachment ID"));
		table.addCell(createHeaderCell("Type"));
		table.addCell(createHeaderCell("Description"));
		table.addCell(createHeaderCell("Tracking Event ID"));
		table.addCell(createHeaderCell("Uploaded By"));
		table.addCell(createHeaderCell("Upload Date"));
		java.util.List<ProjectAttachment> attachments = project.getAttachments();
		if (attachments.size() == 0) {
			Cell cell = createCell("No attachments found");
			cell.setColspan(6);
			table.addCell(cell);
		} else {
			for (ProjectAttachment doc : attachments) {
				
				table.addCell(createDataCell(doc.getDocumentID().toString()));

				table.addCell(createDataCell(ProjectAttachmentTypeDef
						.getData().getItems()
						.getItemDesc(doc.getAttachment().getAttachmentType())));

				table.addCell(createDataCell(doc.getDescription()));

				table.addCell(createDataCell(null == doc.getTrackingEventId()? "" :
					getProjectTrackingEventNbr(project, doc.getTrackingEventId())));

				table.addCell(createDataCell(BasicUsersDef
						.getAllUserData().getItems().getItemDesc(doc.getLastModifiedBy())));

				table.addCell(createDataCell(dateFormat1.format(doc.getUploadDate())));

			}
		}
		item1.add(table);
		return item1;
	}
	
	private String getProjectTrackingEventNbr(Project project, Integer eventId) {
		String eventNbr = null;
		for (ProjectTrackingEvent event : project.getTrackingEvents()) {
			if (eventId.equals(event.getEventId())) {
				eventNbr =  event.getEventNbr();
				break;
			}
		}
		return eventNbr;
	}

    private ListItem printNotes(java.util.List<ProjectNote> notes) throws DocumentException{
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
        
        for (ProjectNote note : notes) {
            cell = createCell(defaultLeading, BasicUsersDef
					.getAllUserData().getItems().getItemDesc(note.getUserId()), data1Font);
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

}