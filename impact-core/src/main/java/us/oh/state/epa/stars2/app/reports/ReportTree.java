package us.oh.state.epa.stars2.app.reports;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import oracle.adf.view.faces.component.core.layout.CorePanelForm;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ReportAttribute;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ReportDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.database.dbObjects.workflow.EnumDetail;
import us.oh.state.epa.stars2.def.ActivityReferralTypeDef;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.ComplianceOtherTypeDef;
import us.oh.state.epa.stars2.def.ContactTitle;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.def.DOLAA;
import us.oh.state.epa.stars2.def.DesignCapacityDef;
import us.oh.state.epa.stars2.def.DesignCapacityUnitsDef;
import us.oh.state.epa.stars2.def.DraftPublicNoticeDef;
import us.oh.state.epa.stars2.def.EACFormTypeDef;
import us.oh.state.epa.stars2.def.EgrPointShapeDef;
import us.oh.state.epa.stars2.def.EgrPointTypeDef;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.EmissionUnitsDef;
import us.oh.state.epa.stars2.def.FacilityAttachmentTypeDef;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.def.IssuanceStatusDef;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.NAICSDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PBRNotifDocTypeDef;
import us.oh.state.epa.stars2.def.PBRReasonDef;
import us.oh.state.epa.stars2.def.PBRTypeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationPurposeDef;
import us.oh.state.epa.stars2.def.PTIOGeneralPermitTypeDef;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIOModelGeneralPermitDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitStatusDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.PortableGroupTypes;
import us.oh.state.epa.stars2.def.RPCRequestDocTypeDef;
import us.oh.state.epa.stars2.def.RPCTypeDef;
import us.oh.state.epa.stars2.def.RPRReasonDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.RuleCitationDef;
import us.oh.state.epa.stars2.def.SICDef;
import us.oh.state.epa.stars2.def.State;
import us.oh.state.epa.stars2.def.TVCompRptFreqDef;
import us.oh.state.epa.stars2.def.TVIeuReasonDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.def.TransitStatusDef;
import us.oh.state.epa.stars2.def.USEPAOutcomeDef;
import us.oh.state.epa.stars2.def.UnitDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.util.CheckVariable;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.component.BuildComponent;

/**
 * @author Kbradley
 * 
 */
@SuppressWarnings("serial")
public class ReportTree extends TreeBase {
    private String outputType = "PDF";
    private CommonConst.ExportType exportType = CommonConst.ExportType.PDF;
    private String reportDirPrefix;
    private ReportDef report;
    private boolean addingReport;
    private boolean editingReport;
    private transient CorePanelForm data;
    private transient HashMap<Integer, ReportDef> reports;
    private LinkedHashMap<String, String> attrTypes = null;
    
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

    public ReportTree() {
        super();
        init();
    }

    public final CorePanelForm getData() {
        return data;
    }

    public final void setData(CorePanelForm data) {
        this.data = data;
    }

    public final LinkedHashMap<String, String> getAttributeTypes() {
        if (attrTypes == null) {
            LoadAttributeTypes();
        }

        return attrTypes;
    }

    public final void setSelectedTreeNode(TreeNode selectedTreeNode) {
        super.setSelectedTreeNode(selectedTreeNode);
        reset();

        getReport();
    }

    public final ReportDef getReport() {
        if (selectedTreeNode.getType().equals("report") && !addingReport) {
            if ((report == null)
                || (selectedTreeNode.getIdentifier().compareTo(report.getId().toString()) != 0)) {

                report = reports.get(new Integer(selectedTreeNode.getIdentifier()));
                
                try {
                    report = getInfrastructureService().retrieveReport(report.getId());
                    buildData();
                } catch (RemoteException re) {
                    logger.error(re.getMessage(), re);
                    DisplayUtil.displayError("System error. Please contact system administrator");
                }
            }
        }

        return report;
    }

    @SuppressWarnings("unchecked")
    public final TreeModelBase getTreeData() {
        if (treeData == null) {
            init();
            TreeNodeBase root = new TreeNodeBase("root", "Reports", "root",
                    false);
            treeData = new TreeModelBase(root);
            ArrayList<String> treePath = new ArrayList<String>();
            treePath.add("0");

            HashMap<String, TreeNodeBase> reportGroups = new HashMap<String, TreeNodeBase>();
            HashMap<String, TreeNodeBase> freeStandingReports = new HashMap<String, TreeNodeBase>();

            int groupNum = 0;

            retrieveReports();

            for (ReportDef lReport : reports.values()) {
                TreeNodeBase reportNode = new TreeNodeBase("report", lReport
                        .getName(), lReport.getId().toString(), false);

                if (lReport.getGroupNm() != null) {
                    TreeNodeBase groupNode = reportGroups.get(lReport
                            .getGroupNm());

                    if (groupNode == null) {
                        treePath.add("0:" + Integer.toString(groupNum));

                        groupNode = new TreeNodeBase("group", lReport
                                .getGroupNm(), Integer.toString(groupNum++),
                                false);

                        reportGroups.put(lReport.getGroupNm(), groupNode);
                    }

                    groupNode.getChildren().add(reportNode);
                } else {
                    freeStandingReports.put(lReport.getName(), reportNode);
                }
            }

            // Add all the groups to the root tree
            for (TreeNodeBase groupNode : reportGroups.values()) {
                root.getChildren().add(groupNode);
            }

            // Add the reports that aren't in groups to the tree
            for (TreeNodeBase reportNode : freeStandingReports.values()) {
                root.getChildren().add(reportNode);
            }

            TreeStateBase treeState = new TreeStateBase();

            treeState.expandPath(treePath.toArray(new String[0]));
            treeData.setTreeState(treeState);
            selectedTreeNode = root;
            current = "root";
        }
        return treeData;
    }

    public final String downloadReport() {
    	
    	//TODO 
    	
    	return null;
    }

    public final String generateReport() {

        String retVal = "ReportGenerated";

        if (report != null) {
            init();
            mapAttributeData();
            
            JRExporter exporter;
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response 
                = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            
            String tempNm = report.getJasperDefFile();
            String returnFileNm = new String(tempNm.substring(tempNm.lastIndexOf('/') + 1));
            if (returnFileNm.lastIndexOf('.') > 0) {
                returnFileNm = returnFileNm.substring(0, returnFileNm.lastIndexOf('.'));
            }
            
            switch (exportType) {
            case PDF:
                exporter = new JRPdfExporter();
                response.setContentType("application/pdf");
                returnFileNm = returnFileNm + ".pdf";
                break;
            case RTF:
                exporter = new JRRtfExporter();
                response.setContentType("application/rtf");
                returnFileNm = returnFileNm + ".rtf";
                break;
            case CSV:
                exporter = new JRCsvExporter();
                response.setContentType("application/csv");
                returnFileNm = returnFileNm + ".csv";
                break;
            case EXCEL:
                exporter = new JExcelApiExporter();
                response.setContentType("application/xls");
                returnFileNm = returnFileNm + ".xls";
                break;
            default:
                exporter = new JRHtmlExporter();
                response.setContentType("text/html");
                returnFileNm = returnFileNm + ".html";
                break;
            }
            
            response.addHeader("Content-Disposition", "inline; filename="
                               + returnFileNm);
            
            Map<String, Object> parmMap = buildParmMap();

            try {
                JasperPrint print 
                    = getInfrastructureService().retrieveReport(reportDirPrefix + report.getJasperDefFile(), parmMap);
                
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,
                                      response.getOutputStream());
                exporter.exportReport();
            }
            catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
                DisplayUtil.displayError("Error generating report.");
                retVal = "ReportError";
            }
            catch (JRException jre) {
                logger.error(jre.getMessage(), jre);
                DisplayUtil.displayError("Error generating report.");
                retVal = "ReportError";
            }

            facesContext.responseComplete();
        }
        else {
            DisplayUtil.displayError("Error generating report. Report is null.");
            retVal = "ReportError";
        }

        return retVal;
    }

    public final String reset() {
        setAddingReport(false);
        setEditingReport(false);

        report = null;

        getReport();

        return CANCELLED;
    }

    private void init() {
        if (reportDirPrefix == null) {
            ConfigManager cfgMgr = ConfigManagerFactory.configManager();
            Node root = cfgMgr.getNode("app.ManagementReports");

            CheckVariable.notNull(root);
            reportDirPrefix = root.getAsString("directory-prefix");
        }
        
        cacheViewIDs.add("/mgmt/mgmtReports.jsp");
    }

    public final String getOutputType() {
        return outputType;
    }

    public final void setOutputType(String outputType) {
        this.outputType = outputType;

        if (outputType.compareToIgnoreCase("PDF") == 0) {
            exportType = CommonConst.ExportType.PDF;
        } else if (outputType.compareToIgnoreCase("RTF") == 0) {
            exportType = CommonConst.ExportType.RTF;
        } else if (outputType.compareToIgnoreCase("EXCEL") == 0) {
            exportType = CommonConst.ExportType.EXCEL;
        } else if (outputType.compareToIgnoreCase("CSV") == 0) {
            exportType = CommonConst.ExportType.CSV;
        } else {
            exportType = CommonConst.ExportType.HTML;
        }
    }

    public final String addReport() {
        report = new ReportDef();

        setAddingReport(true);

        if (selectedTreeNode.getType().equals("group")) {
            report.setGroupNm(selectedTreeNode.getDescription());
        }

        return "ReportAdded";
    }

    public final String saveReport() {
        init();
		String errorClientIdPrefix = "saveReport:";
		if (!displayValidationMessages(errorClientIdPrefix,
				report.validate())) {
	        try {
	            if (report.getId() == null) {
	                getInfrastructureService().createReport(report);
	            } else {
	                getInfrastructureService().modifyReport(report);
	            }
	        } catch (RemoteException re) {
	            logger.error(re.getMessage(), re);
	            DisplayUtil.displayError("System error. Please contact system administrator");
	        }
	
	        reset();
	
	        treeData = null;
	
	        reports.clear();
	        reports = null;
		}
        
        return "ReportSaved";
    }

    public final String editReport() {
        setEditingReport(true);

        return "ReportEditable";
    }
    
    public final void deleteReport() {
        ConfirmWindow cw = (ConfirmWindow) FacesUtil
                .getManagedBean("confirmWindow");

        if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
            return;
        }
        
        try{
            getInfrastructureService().removeReport(report);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        treeData = null;
    }
    
    public final boolean isReportEditor() {
        return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("mgmtReportEdit");
    }

    public final String cloneReport() {
        setAddingReport(true);
        setEditingReport(true);

        report = new ReportDef(report);

        report.setId(null);
        report.setName("");

        return "ReportCloned";
    }

    private Map<String, Object> buildParmMap() {

        HashMap<String, Object> ret = new HashMap<String, Object>();

        if ((report != null) && (report.getAttributes().size() > 0)) {
            for (ReportAttribute attr : report.getAttributes()) {
                ret.put(attr.getCode(), attr.getValue());
            }
            ret.put("SUBREPORT_DIR", reportDirPrefix);
        }
        return ret;
    }

    public final String getOnClickScript() {
        return "document.forms[0].target = '_blank'; setTimeout('document.forms[0].target = \"_self\"', 0); return true;";
    }

    public final boolean isAddingReport() {
        return addingReport;
    }

    public final void setAddingReport(boolean addingReport) {
        this.addingReport = addingReport;
    }

    public final boolean isEditingReport() {
        return editingReport;
    }

    public final void setEditingReport(boolean editingReport) {
        this.editingReport = editingReport;
    }

    public final ReportAttribute getNewReportAttributeObject() {
        return new ReportAttribute();
    }

    private void LoadAttributeTypes() {
        attrTypes = new LinkedHashMap<String, String>();

        attrTypes.put("Integer", "Integer");
        attrTypes.put("String", "String");
        attrTypes.put("Float", "Float");
        attrTypes.put("Date", "Timestamp");
        attrTypes.put("Permit Global Status", "PermitStatus");
        attrTypes.put("Operating Status", "OperStatus");
        attrTypes.put("Contact Type", "ContactType");
        attrTypes.put("County", "County");
        attrTypes.put("Default Facility Role", "DefaultFacRole");
        attrTypes.put("DO/LAA", "DOLAA");
        attrTypes.put("Material I/O", "MaterialIO");
        attrTypes.put("Pollutant", "Pollutant");
        attrTypes.put("Rule Citation", "RuleCitation");
        attrTypes.put("State", "State");
        attrTypes.put("Title", "Title");
        attrTypes.put("User", "User");
        attrTypes.put("Wrapn", "Wrapn");
        attrTypes.put("Compliance Other Type", "OtherType");
        attrTypes.put("PER Attachment Type", "PERAttachmentType");
        attrTypes.put("TVCC Attachment Type", "TVCCAttachmentType");
        attrTypes.put("Correspondence Type", "CorrespondenceType");
        attrTypes.put("Template Doc Type", "TemplateDocType");
        attrTypes.put("Facility Attachment Type", "FacAttachType");
        attrTypes.put("Design Capacity", "DesignCap");
        attrTypes.put("Design Capacity Units", "DesignCapUnits");
        attrTypes.put("Release Point Shape", "EgressPointShape");
        attrTypes.put("Release Point Type", "EgressPointType");
        attrTypes.put("Emission Reporting", "EmissionRpting");
        attrTypes.put("Event Type", "EventType");
        attrTypes.put("Facility Role", "FacilityRole");
        attrTypes.put("NAICS", "NAICS");
        attrTypes.put("Facility Class", "PermitClass");
        attrTypes.put("Portable Group", "PortableGroup");
        attrTypes.put("Report Received Status", "RptRecvStatus");
        attrTypes.put("SIC", "SIC");
        attrTypes.put("Transitional Status", "TransitionalStatus");
        attrTypes.put("Issuance Type", "IssuanceType");
        attrTypes.put("Application Doc Type", "AppDocType");
        attrTypes.put("Application Type", "ApplicationType");
        attrTypes.put("EAC Form Type", "EACFormType");
        attrTypes.put("Emission Units", "EmissionUnits");
        /* General Permit not valid for WY 
         * attrTypes.put("General Permit Type", "GeneralPermitType"); */
        attrTypes.put("Part 63 NESHAP Subpart", "MACT");
        attrTypes.put("Model General Permit", "Model General Permit");
        attrTypes.put("Part 61 NESHAP Subpart", "NESHAPS");
        attrTypes.put("Part 60 NSPS Subpart", "NSPS");
        attrTypes.put("PBR Notification Doc Type", "PBRNotifDocType");
        attrTypes.put("PBR Reason", "PBRReason");
        attrTypes.put("PBR Type", "PBRType");
        attrTypes.put("PER Due Date", "PERDueDate");
        attrTypes.put("PTIO Application Purpose", "PTIOAppPurpose");
        attrTypes.put("RPC Request Doc Type", "RPCRequestDocType");
        attrTypes.put("RPC Type", "RPCType");
        attrTypes.put("RPR Reason", "RPRReason");
        attrTypes.put("TV Annual Compliance Report Frequency", "TVComplRptFreq");
        attrTypes.put("TV IEU Reason", "TVIEUReason");
        attrTypes.put("BAT Rate Basis", "BATRateBasis");
        attrTypes.put("Draft Public Notice", "DraftPublicNotice");
        attrTypes.put("Fee Adjustment", "FeeAdjust");
        attrTypes.put("Permit Issuance Status", "IssuanceStatus");
        attrTypes.put("Permit Doc Type", "PermitDocType");
        attrTypes.put("Permit Global Status", "PermitGlobalStatus");
        attrTypes.put("Permit Issuance Type", "PermitIssuanceType");
        attrTypes.put("Permit Type", "PermitType");
        attrTypes.put("Permit Reason", "PermitReason");
        attrTypes.put("US EPA Outcome", "USEPAOutcome");
        attrTypes.put("Report Attachment Type", "RptAttachType");
        attrTypes.put("Emission Calc Method", "EmissionCalcMethod");
        attrTypes.put("Report Material", "RptMaterial");
        attrTypes.put("Report Unit", "RptUnit");
        attrTypes.put("Activity Referral Type", "ActReferType");
        attrTypes.put("Activity Status", "ActStatus");
        attrTypes.put("Activity Template Type", "ActTemplateType");
        attrTypes.put("Permit SOP", "PermitSOP");
        attrTypes.put("Reporting Year", "reportingYear");
        attrTypes.put("Year", "year");
    }

    @SuppressWarnings("unchecked")
    private void buildData() {

        BuildComponent.cleanUp(data);

        data = new CorePanelForm();
        List children = data.getChildren();
        children.clear();

        if ((report != null) && (report.getAttributes() != null)) {

            for (ReportAttribute attr : report.getAttributes()) {

                BuildComponent bc = new BuildComponent();
                DataDetail dd = new DataDetail();
                int dataTypeId = determineDataId(attr.getType());
                EnumDetail[] enums = null;

                if (dataTypeId == 5) {
                    enums = buildEnumList(attr.getType());
                }
                
                dd.setDataDetailDsc(attr.getDescription());
                dd.setDataDetailLbl(attr.getDescription());
                
                if (attr.getValue() != null) {
                    dd.setDataDetailVal(attr.getValue().toString());
                }

                if (attr.getType().equalsIgnoreCase("Timestamp")){
                    SimpleDateFormat format =
                        new SimpleDateFormat("MM/dd/yyyy");

                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    dd.setDataDetailVal(format.format(now));
                }
                
                dd.setDataTypeId(dataTypeId);
                dd.setVisible(true);
                
                if (enums != null) {
                    dd.setEnumDetails(enums);
                }
                
                bc.setDataDetail(dd);
                bc.setReadOnly(false);
                bc.setRequired(true);
                if (!"YesNo".equals(attr.getType()) && !"YorN".equals(attr.getType())) {
                    bc.setUnselectedLabel("None");
                }
                
                children.add(bc.byDataTypeId());
            }
        }
    }

    private Integer determineDataId(String dataType) {
        Integer ret = null;

        if (dataType.equalsIgnoreCase("String")) {
            ret = 1;
        } else if (dataType.equalsIgnoreCase("Integer")) {
            ret = 2;
        } else if (dataType.equalsIgnoreCase("Timestamp")) {
            ret = 3;
        } else if (dataType.equalsIgnoreCase("Float")) {
            ret = 4;
        } else {
            ret = 5;
        }

        return ret;
    }

    private EnumDetail[] buildEnumList(String type) {
        List<EnumDetail> enums = new ArrayList<EnumDetail>();

        if (type.equals("PermitStatus")) {
            enums = buildEnumList(PermitStatusDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("OperStatus")) {
            enums = buildEnumList(OperatingStatusDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("ContactType")) {
            enums = buildEnumList(ContactTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("County")) {
            enums = buildEnumList(County.getData().getItems().getAllItems());
        } else if (type.equals("DefaultFacRole")) {
            // enums =
            // buildEnumList(ContactTypeDef.getData().getItems().getAllItems());
        } else if (type.equals("DOLAA")) {
            enums = buildEnumList(DOLAA.getData().getItems().getAllItems());
        } else if (type.equals("MaterialIO")) {
            // enums =
            // buildEnumList(ContactTypeDef.getData().getItems().getAllItems());
        } else if (type.equals("Pollutant")) {
            enums = buildEnumList(PollutantDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("RuleCitation")) {
            enums = buildEnumList(RuleCitationDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("State")) {
            enums = buildEnumList(State.getData().getItems().getAllItems());
        } else if (type.equals("Title")) {
            enums = buildEnumList(ContactTitle.getData().getItems().getAllItems());
        } else if (type.equals("User")) {
            // enums = buildEnumList(State.getData().getItems().getAllItems());
        } else if (type.equals("Wrapn")) {
            // enums =
            // buildEnumList(WrapnDef.getData().getItems().getAllItems());
        } else if (type.equals("OtherType")) {
            enums = buildEnumList(ComplianceOtherTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PERAttachmentType")) {
            // enums =
            // buildEnumList(ComplianceOtherTypeDef.getData().getItems().getAllItems());
        } else if (type.equals("TVCCAttachmentType")) {
            // enums =
            // buildEnumList(ComplianceOtherTypeDef.getData().getItems().getAllItems());
        } else if (type.equals("CorrespondenceType")) {
            enums = buildEnumList(CorrespondenceDef.getDescriptionData()
                    .getItems().getAllItems());
        } else if (type.equals("TemplateDocType")) {
            enums = buildEnumList(TemplateDocTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("FacAttachType")) {
            enums = buildEnumList(FacilityAttachmentTypeDef.getData()
                    .getItems().getAllItems());
        } else if (type.equals("DesignCap")) {
            enums = buildEnumList(DesignCapacityDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("DesignCapUnits")) {
            enums = buildEnumList(DesignCapacityUnitsDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("EgressPointShape")) {
            enums = buildEnumList(EgrPointShapeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("EgressPointType")) {
            enums = buildEnumList(EgrPointTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("EmissionRpting")) {
            enums = buildEnumList(EmissionReportsDef.getData().getItems()
                    .getAllItems());
        }else if (type.equals("reportingYear")) {
            int newest = Calendar.getInstance().get(Calendar.YEAR) - 1;
            enums = buildEnumIntList(newest, 1993);
        } else if (type.equals("year")) {
            int newest = Calendar.getInstance().get(Calendar.YEAR);
            enums = buildEnumIntList(newest, 2007);
        } else if (type.equals("EventType")) {
            // enums =
            // buildEnumList(EmissionReportsDef.getData().getItems().getAllItems());
        } else if (type.equals("FacilityRole")) {
            enums = buildEnumList(FacilityRoleDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("NAICS")) {
            enums = buildForamattedEnumList(NAICSDef.getData().getItems().getAllItems());
        } else if (type.equals("PermitClass")) {
            enums = buildEnumList(PermitClassDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PortableGroup")) {
            enums = buildEnumList(PortableGroupTypes.getData().getItems()
                    .getAllItems());
        } else if (type.equals("RptRecvStatus")) {
            enums = buildEnumList(ReportReceivedStatusDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("SIC")) {
            enums = buildForamattedEnumList(SICDef.getData().getItems().getAllItems());
        } else if (type.equals("TransitionalStatus")) {
            enums = buildEnumList(TransitStatusDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("AppDocType")) {
            enums = buildEnumList(ApplicationDocumentTypeDef.getData()
                    .getItems().getAllItems());
        } else if (type.equals("ApplicationType")) {
            enums = buildEnumList(ApplicationTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("EACFormType")) {
            enums = buildEnumList(EACFormTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("EmissionUnits")) {
            enums = buildEnumList(EmissionUnitsDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("GeneralPermitType")) {
            enums = buildEnumList(PTIOGeneralPermitTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("MACT")) {
             enums = buildEnumList(PTIOMACTSubpartDef.getData().getItems().getAllItems());
        } else if (type.equals("ModelGeneralPermit")) {
            enums = buildEnumList(PTIOModelGeneralPermitDef.getData()
                    .getItems().getAllItems());
        } else if (type.equals("NESHAPS")) {
            enums = buildEnumList(PTIONESHAPSSubpartDef.getData().getItems().getAllItems());
        } else if (type.equals("NSPS")) {
             enums = buildEnumList(PTIONSPSSubpartDef.getData().getItems().getAllItems());
        } else if (type.equals("PBRNotifDocType")) {
            enums = buildEnumList(PBRNotifDocTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PBRReason")) {
            enums = buildEnumList(PBRReasonDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PBRType")) {
            enums = buildEnumList(PBRTypeDef.getData().getItems().getAllItems());
        } else if (type.equals("PERDueDate")) {
            enums = buildEnumList(PTIOPERDueDateDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PTIOAppPurpose")) {
            enums = buildEnumList(PTIOApplicationPurposeDef.getData()
                    .getItems().getAllItems());
        } else if (type.equals("RPCRequestDocType")) {
            enums = buildEnumList(RPCRequestDocTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("RPCType")) {
            enums = buildEnumList(RPCTypeDef.getData().getItems().getAllItems());
        } else if (type.equals("RPRReason")) {
            enums = buildEnumList(RPRReasonDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("TVComplRptFreq")) {
            enums = buildEnumList(TVCompRptFreqDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("TVIEUReason")) {
            enums = buildEnumList(TVIeuReasonDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("BATRateBasis")) {
            // enums =
            // buildEnumList(BATRateDef.getData().getItems().getAllItems());
        } else if (type.equals("DraftPublicNotice")) {
            enums = buildEnumList(DraftPublicNoticeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("FeeAdjust")) {
            // enums =
            // buildEnumList(FeeAdjustmentDef.getData().getItems().getAllItems());
        } else if (type.equals("IssuanceStatus")) {
            enums = buildEnumList(IssuanceStatusDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PermitDocType")) {
            enums = buildEnumList(PermitDocTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PermitGlobalStatus")) {
            enums = buildEnumList(PermitGlobalStatusDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PermitIssuanceType")) {
            enums = buildEnumList(PermitIssuanceTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PermitType")) {
            enums = buildEnumList(PermitTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("PermitReason")) {
            enums = buildEnumList(PermitReasonsDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("USEPAOutcome")) {
            enums = buildEnumList(USEPAOutcomeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("RptAttachType")) {
            // enums =
            // buildEnumList(ReportAttachmentDef.getData().getItems().getAllItems());
        } else if (type.equals("EmissionCalcMethod")) {
            enums = buildEnumList(EmissionCalcMethodDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("RptMaterial")) {
            enums = buildEnumList(MaterialDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("RptUnit")) {
            enums = buildEnumList(UnitDef.getData().getItems().getAllItems());
        } else if (type.equals("ActReferType")) {
            enums = buildEnumList(ActivityReferralTypeDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("ActStatus")) {
            enums = buildEnumList(ActivityStatusDef.getData().getItems()
                    .getAllItems());
        } else if (type.equals("ActTemplateType")) {
            // enums =
            // buildEnumList(ActivityTemplateTypeDef.getData().getItems().getAllItems());
        } else if (type.equals("PermitSOP")) {
            // enums =
            // buildEnumList(PermitSOPDef.getData().getItems().getAllItems());
        } else if (type.equals("YesNo")) {
            List<SelectItem> defs = new ArrayList<SelectItem>();
            defs.add(new SelectItem("0", "No"));
            defs.add(new SelectItem("1", "Yes"));
            enums = buildEnumList(defs);
        } else if (type.equals("YorN")) {
            List<SelectItem> defs = new ArrayList<SelectItem>();
            defs.add(new SelectItem("%", "Yes or No"));
            defs.add(new SelectItem("N", "No"));
            defs.add(new SelectItem("Y", "Yes"));
            enums = buildEnumList(defs);
        }

        return enums.toArray(new EnumDetail[0]);
    }

    private List<EnumDetail> buildEnumList(List<SelectItem> defs) {
        List<EnumDetail> enums = new ArrayList<EnumDetail>();
        EnumDetail tempEnum = null;

        for (SelectItem item : defs) {
            tempEnum = new EnumDetail();
            tempEnum.setEnumValue((String) item.getValue());
            tempEnum.setEnumLabel(item.getLabel());
            tempEnum.setEnumDsc(item.getLabel());

            enums.add(tempEnum);
        }

        return enums;
    }
    
    private List<EnumDetail> buildEnumIntList(int newest, int oldest) {
        List<EnumDetail> enums = new ArrayList<EnumDetail>();
        EnumDetail tempEnum = null;

        for (int i = newest; i >= oldest; i--) {
            tempEnum = new EnumDetail();
            String y = Integer.toString(i);
            tempEnum.setEnumValue(y);
            tempEnum.setEnumLabel(y);
            tempEnum.setEnumDsc(y);

            enums.add(tempEnum);
        }

        return enums;
    }
    
    private List<EnumDetail> buildForamattedEnumList(List<SelectItem> defs) {
        List<EnumDetail> enums = new ArrayList<EnumDetail>();
        EnumDetail tempEnum = null;

        for (SelectItem item : defs) {
            tempEnum = new EnumDetail();
            tempEnum.setEnumValue((String) item.getValue());
            tempEnum.setEnumLabel(item.getValue() + "  " + item.getLabel());
            tempEnum.setEnumDsc(item.getValue() + "  " + item.getLabel());

            enums.add(tempEnum);
        }

        return enums;
    }

    private void mapAttributeData() {

        if ((data != null) && (report != null)) {

            LinkedHashMap<String, String> tdata 
                = BuildComponent.getDataToHashMap(data);

            List<ReportAttribute> attrs = report.getAttributes();

            if ((tdata != null) && (attrs != null)) {

                for (ReportAttribute attr : attrs) {

                    if (attr.getType().equalsIgnoreCase("Integer")) {
                        String v = tdata.get(attr.getDescription());
                        if (v == null || (v != null && v.length() == 0)) {
                            attr.setValue(null);
                        }
                        else {
                            attr.setValue(new Integer(v));
                        }
                    } 
                    else if (attr.getType().equalsIgnoreCase("Float")) {
                        String v = tdata.get(attr.getDescription());
                        if (v == null || (v != null && v.length() == 0)) {
                            attr.setValue(null);
                        }
                        else {
                            attr.setValue(new Float(v)); 
                        }
                    } 
                    else if (attr.getType().equalsIgnoreCase("Timestamp")) {
                        String v = tdata.get(attr.getDescription());
                        if (v == null || (v != null && v.length() == 0)) {
                            attr.setValue(null);
                        }
                        else {
                            attr.setValue(FacesUtil.convertYear(new Timestamp(new Long(v))));
                        }
                    } 
                    else if (tdata.get(attr.getDescription()) != null 
                             && tdata.get(attr.getDescription()).length() > 0) {
                        attr.setValue(tdata.get(attr.getDescription()));
                    }
                    else {
                        attr.setValue(null);
                    }
                }
            }
        }
    }
    
    public void clearCache() {
        super.clearCache();
        
        if (reports != null) {
            reports.clear();
            reports = null;
        }
    }
    
    private void retrieveReports() {
        if (reports == null) {
            try {
                ReportDef[] tempReports = getInfrastructureService()
                        .retrieveReportDefs();

                reports = new HashMap<Integer, ReportDef>();

                for (ReportDef lReport : tempReports) {
                    reports.put(lReport.getId(), lReport);
                }
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("Error retrieving reports");
            }
        }
    }

    public final CommonConst.ExportType getExportType() {
        return exportType;
    }

    public final void setExportType(CommonConst.ExportType exportType) {
        this.exportType = exportType;
    }

    public final String getReportDirPrefix() {
        return reportDirPrefix;
    }

    public final void setReportDirPrefix(String reportDirPrefix) {
        this.reportDirPrefix = reportDirPrefix;
    }

    public final HashMap<Integer, ReportDef> getReports() {
        return reports;
    }

    public final void setReports(HashMap<Integer, ReportDef> reports) {
        this.reports = reports;
    }

    public final LinkedHashMap<String, String> getAttrTypes() {
        return attrTypes;
    }

    public final void setAttrTypes(LinkedHashMap<String, String> attrTypes) {
        this.attrTypes = attrTypes;
    }

    public final void setReport(ReportDef report) {
        this.report = report;
    }
}
