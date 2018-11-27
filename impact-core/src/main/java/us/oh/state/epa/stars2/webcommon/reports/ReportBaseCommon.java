package us.oh.state.epa.stars2.webcommon.reports;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportNote;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.OpenDocumentUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class ReportBaseCommon extends ValidationBase {
	
	private static final long serialVersionUID = -7370259023982908012L;

	protected String conflictMsg1 = "This report cannot be approved because report ";
    protected String conflictMsg2 = " is not a revision and is not in an Approved state or state \"Emissions Inventory Prior/Invalid\".";
    public final static String dataProb = "Reporting data error";
    // For report or first NTV report
    protected ReportTemplates scReports = new ReportTemplates();
    protected ReportTemplates locatedScReports;
    // For second NTV report (older year)
    protected ReportTemplates scReports2 = new ReportTemplates();
    protected ReportTemplates locatedScReports2;
    protected List<ReportTemplates> associatedScReports = new ArrayList<ReportTemplates>();
    
    protected Integer reportId = new Integer(-1);
    protected Integer savedReportId  = new Integer(-2);
    
    boolean adminEditable = false;
    protected boolean err = false;  // cannot display page
    protected boolean ok;
    protected Boolean passedValidation = null;
    protected Boolean falseValue = new Boolean(false);
    protected Boolean trueValue = new Boolean(true);

    // Report for TV, SMTV & primary report for NTV
    // for NTV, getPrimaryReport() used to get report
    // don't want to execute getReport() for it.
    protected EmissionsReport report;
    protected EmissionsReport report1;  // First report for NTV
    protected EmissionsReport report2;  // Second report for NTV
    protected String revisedReportNotProcessedMsg = null;
    
    protected String facilityId;
    protected Integer fpId;
    protected Integer versionId; // only used when changing facility inventories
    protected Facility facility;    

    private boolean editable;
    private boolean editable1;  // used in NTV for contact changes.
    // Used for NTV to know whether to start in admin edit mode.
    private boolean fireEditable;
    private boolean yearlyInfoEditable;
    protected boolean reloadRpt = true;
    
    protected Task createRTask = null;  // for report
    protected Task createFTask = null;  // for profile in portal
    
    // Attachments
    protected List<EmissionsDocumentRef> attachments;
    protected EmissionsDocumentRef emissionsDoc;
    protected boolean docUpdate;
    protected boolean okToEdit;
    protected static final String DOC_JSP_VAR = "doc";
    protected UploadedFile fileToUpload;
    protected UploadedFileInfo publicFileInfo;
    protected UploadedFile tsFileToUpload;
    protected UploadedFileInfo tsFileInfo;
    protected EmissionsDocumentRef downloadDoc;
    protected static final String DOCUMENT_DIALOG = "dialog:emissionsDoc";
    protected static final String TS_DOC_DOWNLOAD_DIALOG = "dialog:rptConfirmTSDownload";
    protected static final String DOCUMENT_DIALOG_NTV = "dialog:emissionsDocNTV";
    protected static final String TS_DOC_DOWNLOAD_DIALOG_NTV = "dialog:rptConfirmTSDownloadNTV";
    protected static final String DOC_EMISS_INVOICE = "Invoice";
    
    //  Notes
    protected TableSorter notesWrapper;
    protected EmissionsReportNote reportNote;
    protected EmissionsReportNote modifyEINote;
    protected EmissionsReportNote[] reportNotes;
    protected boolean noteModify;
    
    //  set when report in MyTasks.processTask()ER: and MyTasks.createEmissionReport, cleared if in ReadOnly
    // Also set from ReportDetail.submit()
    protected boolean inStaging;
    
    // Flag that can be turned on by STARS2ADMIN to
    // continue editing after submitted.
    protected boolean openedForEdit = false;
    // Flag that indicates that an Emissions Inventory has been Reopened for Edit.
    // Used to indicate that Validate button should be available after EI has been submitted.
    protected boolean hasBeenOpenedForEdit = false;
    
    //  Note that values kept at ReportSearch used for web
    // display rather than submittedReports variable.
    protected EmissionsReportSearch[] submittedReports;
    protected int notFiledReport;
    protected int submittedReport;
    protected int submittedReportPrim;
    protected int notFiledReport2;
    protected Integer notFiledReportComp;
    protected Integer notFiledReport2Comp;
    protected int submittedReport2;
    protected Integer submittedReportComp;
    protected Integer submittedReport2Comp;
    
    protected boolean hideTradeSecret = false;
    protected List<Document> reportDocuments;
    protected List<Document> reportAttachments;
    protected Document facilityDoc = null;  // for pdf of facility profile
    protected TmpDocument reportDoc = null;  // for pdf of report
    protected TmpDocument reportSummaryDoc = null;  // for pdf of report summary
    
    private EmissionsReportService emissionsReportService;
    private boolean deleteDocAllowed = true;
    private boolean noteReadOnly;
    
    
    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

    public ReportBaseCommon() {
        super();
        notesWrapper = new TableSorter();
        setTag(ValidationBase.REPORT_TAG);
    }
    
    public List<ReportTemplates> getAssociatedScReports() {
		return associatedScReports;
	}

	public void setAssociatedScReports(List<ReportTemplates> associatedScReports) {
		this.associatedScReports = associatedScReports;
	}

	/*
     * Retrieve the report definitions needed.
     */
    protected ReportTemplates retrieveSCEmissionsReports(
            EmissionsReport rpt, String facilityId)
                       throws RemoteException {
        Integer yr = rpt.getReportYear();
        String contentType = rpt.getContentTypeCd();
		return getEmissionsReportService().retrieveSCEmissionsReports(yr,
				contentType, facilityId);
    }


    protected List<ReportTemplates> retrieveAssociatedSCEmissionsReports(
			EmissionsReport report)
					throws RemoteException {
        return getEmissionsReportService().retrieveAssociatedSCEmissionsReports(report,isInStaging());
	}

    // This function is protected against reports not being located.
    // TODO Validation should verify that reports are located.
    // TODO Don't want the lack of reports to prevent filing a report
    protected void reportsNeeded(ReportTemplates needed,
            ReportTemplates located, EmissionsReport rpt) {
        if (!rpt.isSubmitted()) { // Only adjust report types if not submitted.
            //rpt.clearRptTypes();
            needed.setNull();

            //rpt.setRptFER(true);  // must always be true.

            // frequency equal zero means never due otherwise
            // due every frequency years, where reference year is
            // one of the years the report is due.
            //SCEmissionsReport locatedSc = located.getSc();
            //if(null != locatedSc) { // protect against report definition not found
            //    if(0 != locatedSc.getReportFrequencyYear().intValue() &&
            //            0 == (rpt.getReportYear().intValue() -
            //                    locatedSc.getReferenceYear())%
            //                    locatedSc.getReportFrequencyYear()){
                    //rpt.setRptEIS(true);
                    //rpt.setEisRequired(true);
            //    } else {
                    // Leave rptEIS true if it were set to true, but
                    // make EIS no longer required.
                    // rpt.setRptEIS(false);
                    //rpt.setEisRequired(false);
            //    }
            //}
            //rpt.resetRptTypes();
        }
        //if(rpt.isRptFER()) needed.setScFER(located);
        needed.setSc(located);
        //if(rpt.isRptES()){ // Flag set according to attainment only upon report creation
        //    needed.setScES(located);
        //}
        //if(rpt.isRptEIS()) needed.setScEIS(located);
    }
    
    public final boolean isApproved() {
        // For jsp and ReportDetail only
        return report.isApproved();
    }
    
    public final boolean isReportNotNeeded(){
        // for jsp and ReportDetail only
    	return report.isReportNotNeeded();
    }
    
    public boolean isClosedForEdit() {
        // returns true if cannot do further edits
        return report.isSubmitted() && !openedForEdit;
    }
    
    public final Integer getReportId() {
        return reportId;
    }

    public final void setReportId(Integer reportId) {
        this.reportId = reportId;
        // Make sure that the report is read again.
        // When you traverse back to the report, the facility
        // profile may have been changed. The report must be
        // read again to again compare it with the facility inventory.
        this.report = null;
    }

    public final void setReport(EmissionsReport report) {
        this.report = report;
    }

    public boolean isInStaging() {
        return inStaging;
    }

    public void setInStaging(boolean inStaging) {
        this.inStaging = inStaging;
    }
    
    public final Integer getUserID() {
        return InfrastructureDefs.getCurrentUserId();
    }
    
    protected Task callNTV_create(NtvReport rpt, Facility facil) {
        Task rtn = null;
        Task createRTask = new Task();
        createRTask.setTaskType(Task.TaskType.ER);
        if(rpt.getPrimary().getReportModified() != null) {
            createRTask.setTaskType(Task.TaskType.R_ER);
        }
        createRTask.setEmissionsReportOperation("n");
        createRTask.setNtvReport(rpt);
        createRTask.setFacilityId(facil.getFacilityId());
        createRTask.setCorePlaceId(facil.getCorePlaceId());
        createRTask.setFacility(facil);
        createRTask.setUserName(getUserName());
        createRTask.setFpId(null);
        createRTask.setDependent(false);
        createRTask.setDependentTaskId(null);
        createRTask.setReferenceCount(0);
        try {
            getEmissionsReportService().generateNewNtvReport(createRTask);
            rtn = createRTask;
        } catch (RemoteException re) {
            handleException(rpt.getPrimary().getEmissionsRptId(), re);
            DisplayUtil.displayError("Failed to create NTV report");
        }
        return rtn;
    }
    
    // Create a new report or a revised report.  The revised report may
    // have the values of the report being revised (rpt != null) or
    // it may just specify the id of that report (rptMod != null)--
    // This later is in the case of a NTV report being revised into TV/SMTV
    //
    // The profile tied to may be the current one or a historic one.
    protected Task callTV_SMTV_create(Facility facil, Integer yr, String contentTypeCd, 
    		EmissionsReport rpt, Integer rptRevised){
        Task rtn = null;
        EmissionsReport newReport = new EmissionsReport();
        if(rpt != null) {
            newReport = rpt;
        }
        newReport.setEmissionsRptId(null);
        newReport.setReportYear(yr);
        newReport.setCreateContentTypeCd(contentTypeCd);
        newReport.setRptReceivedStatusCd(ReportReceivedStatusDef.NOT_FILED);
        newReport.setFpId(facil.getFpId());
        // Set the submitted report ID into the modified report.
        // This is passed as argument because we may revise but create an empty report
        newReport.setReportModified(rptRevised);
        Task.TaskType tt = Task.TaskType.ER;
        if(rptRevised != null) {
            tt = Task.TaskType.R_ER;
        }
        boolean ok = true;
        //try {
        //    getEmissionsReportService().setEsEis(newReport, facil, yr);
        //} catch(RemoteException re) {
        //    DisplayUtil.displayError("Failed to determine ES or EIS status");
        //    ok = false;
        //}
        Task createRTask = null;
        Task createFTask = null;
        if(ok) {
            if(isPortalApp()) {
                MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
                if(facil.getVersionId().equals(-1)) {  // For current profile
                    // create facility task for current if needed
                    createFTask = myTasks.changeFacilityProfileTask(3);
                    if(createFTask == null) {
                        ok = false;
                        DisplayUtil.displayError("Failed to locate Current Facility Inventory " + facil.getFpId());
                        ok = false;
                    }
                } else { // Other profile tied to.
                    String desc = "Historic Facility Inventory " + facil.getFpId();
                    createFTask = myTasks.changeHistFacilityProfileTask(facil.getFpId(), desc);
                    createFTask.setFacility(facil); // so facility name will appear with task description
                    if(createFTask == null) {
                        ok = false;
                        DisplayUtil.displayError("Failed to locate Historic Facility Inventory " + facil.getFpId());
                        ok = false;
                    }
                }
                if(ok) {
                    createRTask = MyTasks.createNewTask("", tt,
                            true, createFTask.getTaskId(), facil.getFacilityId(),
                            createFTask.getFpId(), "current", facil.getCorePlaceId(), getUserName());
                    // Use the correct fp_id (if current may differ from read only area)
                    facil.setFpId(createFTask.getFpId());
                    newReport.setFpId(createFTask.getFpId());
                }

            } else {
                createRTask = new Task();
                createRTask.setTaskDescription(""); 
                createRTask.setTaskType(tt);
                createRTask.setDependent(true);
                createRTask.setDependentTaskId(null);
                createRTask.setVersion("current");
                createRTask.setFacilityId(facil.getFacilityId());
                createRTask.setFpId(facil.getFpId());
                createRTask.setCorePlaceId(facil.getCorePlaceId());
                createRTask.setUserName(getUserName());
            }
        }
        if(ok) {
            createRTask.setFacility(facil);
            createRTask.setEmissionsReportOperation("n");
            createRTask.setReport(newReport);
            try {
                getEmissionsReportService().generateNewReport(createRTask, createFTask, rpt);
                rtn = createRTask;
            } catch (RemoteException re) {
                logger.error("Failed to create emissions inventory for facility "
                        + facil.getFacilityId(), re);
                DisplayUtil.displayError("Failed to create emissions inventory");
            }
            if (rtn != null) {
                // Switch over to this new report.
                reportId = createRTask.getReport().getEmissionsRptId();                
            }
        }
        return rtn;
    }

    // Look for newer reports for same year
    protected void locateNewestRpts(Integer yr, String contentTypeCd, Integer yr2,  String contentTypeCd2, String facId)
    throws RemoteException {
        // look for reports for the same year
        // If app side, then submittedReports are all reports.
        EmissionsReportSearch[] inprogressReports = getEmissionsReports(facId, true);
        notFiledReport = -1;
        notFiledReport2 = -1;
        notFiledReportComp = null;
        notFiledReport2Comp = null;
        submittedReport = -1;
        submittedReport2 = -1;
        submittedReportComp = null;
        submittedReport2Comp = null;
        if(yr != null && !Utility.isNullOrEmpty(contentTypeCd) && inprogressReports != null) {
            for (EmissionsReportSearch sRes : inprogressReports) {
                // Look for a report not submitted yet
                if ((sRes.getYear().equals(yr)) && (sRes.getContentTypeCd().equals(contentTypeCd))) {
                    if (sRes.getReportingState().equals(
                            ReportReceivedStatusDef.NOT_FILED)) {
                        notFiledReport = sRes.getEmissionsRptId();
                        notFiledReportComp = sRes.getCompanionReport();
                    }else {
                        // Find highest submitted report number (lastest)
                        if (-1 == submittedReport
                                || submittedReport < sRes.getEmissionsRptId()
                                .intValue()) {
                            submittedReport = sRes.getEmissionsRptId();
                            submittedReportComp = sRes.getCompanionReport();
                        }
                    }
                }
            }
        }
        if(yr2 != null && !Utility.isNullOrEmpty(contentTypeCd2) && inprogressReports != null) {
            for (EmissionsReportSearch sRes : inprogressReports) {
                // Look for a report not submitted yet
                if ((sRes.getYear().equals(yr2)) && (sRes.getContentTypeCd().equals(contentTypeCd2))) {
                    if (sRes.getReportingState().equals(
                            ReportReceivedStatusDef.NOT_FILED)) {
                        notFiledReport2 = sRes.getEmissionsRptId();
                        notFiledReport2Comp = sRes.getCompanionReport();
                    }else {
                        // Find highest submitted report number (lastest)
                        if (-1 == submittedReport2
                                || submittedReport2 < sRes.getEmissionsRptId()
                                .intValue()) {
                            submittedReport2 = sRes.getEmissionsRptId();
                            submittedReport2Comp = sRes.getCompanionReport();
                        }
                    }
                }
            }
        }
        if(!isInternalApp()) {
            // Note that submitted portal reports searched last (read/only DB)
            // because that is the list to offer if doing report data copy
            submittedReports = getEmissionsReports(facId, false);
        } else {
            submittedReports = inprogressReports;
        }

        if(yr != null && !Utility.isNullOrEmpty(contentTypeCd) && submittedReports != null) {
            for (EmissionsReportSearch sRes : submittedReports) {
            	if ((sRes.getYear().equals(yr)) && (sRes.getContentTypeCd().equals(contentTypeCd))) {
                    if (sRes.getReportingState().equals(
                            ReportReceivedStatusDef.NOT_FILED)) {
                        notFiledReport = sRes.getEmissionsRptId();
                        notFiledReportComp = sRes.getCompanionReport();
                    }else {
                        // Find highest submitted report number (lastest)
                        if (-1 == submittedReport
                                || submittedReport < sRes.getEmissionsRptId()
                                .intValue()) {
                            submittedReport = sRes.getEmissionsRptId();
                            submittedReportComp = sRes.getCompanionReport();
                        }
                    }
                }
            }
        }
        if(yr2 != null && !Utility.isNullOrEmpty(contentTypeCd2) && submittedReports != null) {
            for (EmissionsReportSearch sRes : submittedReports) {
            	if ((sRes.getYear().equals(yr2)) && (sRes.getContentTypeCd().equals(contentTypeCd2))) {
                    if (sRes.getReportingState().equals(
                            ReportReceivedStatusDef.NOT_FILED)) {
                        notFiledReport2 = sRes.getEmissionsRptId();
                        notFiledReport2Comp = sRes.getCompanionReport();
                    } else {
                        // Find highest submitted report number (lastest)
                        if (-1 == submittedReport2
                                || submittedReport2 < sRes.getEmissionsRptId()
                                .intValue()) {
                            submittedReport2 = sRes.getEmissionsRptId();
                            submittedReport2Comp = sRes.getCompanionReport();
                        }
                    }
                }
            }
        }
    }
    
    boolean anyReports() {
        if(notFiledReport == -1 &&
                notFiledReport2 == -1 &&
                submittedReport == -1 &&
                submittedReport2 == -1) {
            return false;
        } else {
            return true;
        }
    }
    
    protected int getMinId(int i1, Integer i2) {
        // return the smaller number
        int rtn = i1;
        if(i2 != null) {
            if(i1 == -1) {
                rtn = i2;
            } else {
                if(i2 != -1) {
                    rtn =  i1<i2?i1:i2;
                }
            }
        }
        return rtn;
    }
    
    // Return the reports for this facility
    protected EmissionsReportSearch[] getEmissionsReports(String facilityId,
            boolean staging) throws RemoteException {
        ReportSearch reportSearch = (ReportSearch) FacesUtil
                .getManagedBean("reportSearch");
        EmissionsReportSearch saveRS = reportSearch.getSearchObj();
        reportSearch.reset();
        reportSearch.setFromFacility("true");
        reportSearch.setFacilityId(facilityId);
        reportSearch.internalSubmitSearch(staging);
        reportSearch.setSearchObj(saveRS);
        if(reportSearch.getReports() == null) {
            throw new RemoteException("internalSubmitSearch failed");
        }
        return reportSearch.getReports();
    }
    
    protected void processNotes(EmissionsReport rpt) {
        reportNotes = rpt.getNotes().toArray(
                new EmissionsReportNote[0]);
        notesWrapper.setWrappedData(reportNotes);
    }
    
    

    public final TableSorter getNotesWrapper() {
        return notesWrapper;
    }
    
    public final String startViewNote() {
        noteModify = false;
        return "dialog:reportNoteDetail";
    }

    public final String startEditNote() {
        noteModify = true;
        reportNote = new EmissionsReportNote(modifyEINote);
        if (reportNote.getUserId().equals(InfrastructureDefs.getCurrentUserId())) {
    		noteReadOnly = false;
    	} else {
    		noteReadOnly = true;
    	}
    	
        return "dialog:reportNoteDetail";
    }

    public final String startAddNote() {
        noteModify = false;
        noteReadOnly = false;
        reportNote = new EmissionsReportNote();
        reportNote.setNoteTypeCd(NoteType.DAPC);
        reportNote.setEmissionsRptId(report.getEmissionsRptId());
        reportNote.setUserId(InfrastructureDefs.getCurrentUserId());
        reportNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
        return "dialog:reportNoteDetail";
    }

    public final void cancelEditNote() {
        reportNote = null;
        noteModify = false;
        noteReadOnly = false;
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void closeDialog() {
    	reportNote = null;
    	noteReadOnly = false;
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    /**
     * Reload notes for current emissions inventory.
     */
    protected final String reloadNotes() {
    	loadNotes(report.getEmissionsRptId());
    	return FacesUtil.getCurrentPage(); // stay on same page
    }

    private void loadNotes(int reportId) {
    	try {
    		EmissionsReportNote[] notes = getEmissionsReportService().retrieveReportNotes(reportId);
    		report.setNotes(Arrays.asList(notes));
    		reportNotes = notes;
            notesWrapper.setWrappedData(notes);
    	} catch (RemoteException ex) {
    		DisplayUtil.displayError("cannot load emissions inventory notes");
    		handleException(ex);
    		return;
    	}
    }
       
    protected boolean isNtvReport() {
        ReportSearch reportSearch = (ReportSearch) FacesUtil
        .getManagedBean("reportSearch");
        return reportSearch.isNtvReport();
    }
    
    protected void setNtvReportFlag(boolean isNtvReport) {
        ReportSearch reportSearch = (ReportSearch) FacesUtil
        .getManagedBean("reportSearch");
        reportSearch.setNtvReport(isNtvReport);
    }
    
    // attachments start
    
    public final String startEditDoc() {
        try {
            EmissionsDocumentRef emissionsDocTemp =
                (EmissionsDocumentRef) FacesUtil.getManagedBean(DOC_JSP_VAR);
            // make sure doc is not null
            if (emissionsDocTemp != null) {
                // make sure doc has an id
                if (emissionsDocTemp.getEmissionsDocId() != null) {
                    emissionsDoc = getEmissionsReportService().retrieveEmissionsDocument(
                            emissionsDocTemp.getEmissionsDocId(), inStaging);
                    if(emissionsDoc == null) {
                        throw new DAOException("Failed to locate EmissionsDocument with id "
                                + emissionsDocTemp.getEmissionsDocId()
                                + " for emissions inventory " + report.getEmissionsInventoryId());
                    }
                    docUpdate = true;
                    okToEdit = getOkToEditAttachment();
                } else {
                    logger.error("For reportId=" + report.getEmissionsInventoryId() + ", invalid Emission Report document with no id in document table");
                }
            } else {
                logger.error("For reportId=" + report.getEmissionsInventoryId() + ", unable to retrieve managed bean for document in Emission Report document table");
            }
        } catch (RemoteException e) {
            handleException(report.getEmissionsRptId(), e);
        }
        return documentDialogNav();
    }
    
    public final String startAddDoc() {
        emissionsDoc = new EmissionsDocumentRef();
        okToEdit = true;
        docUpdate = false;
        return documentDialogNav();
    }
    
    private String documentDialogNav() {
        String ret = DOCUMENT_DIALOG;
        
        return ret;
    }
    
    public final void applyEditDoc(ActionEvent actionEvent) {

    	if (publicFileInfo == null && fileToUpload != null) {
    		publicFileInfo = new UploadedFileInfo(fileToUpload);
    	}
    	if (isTradeSecretAllowed() && tsFileInfo == null && tsFileToUpload != null) {
    		tsFileInfo = new UploadedFileInfo(tsFileToUpload);
    	}

    	List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

    	// make sure document description and type are provided
    	if (emissionsDoc.getDescription() == null || emissionsDoc.getDescription().trim().equals("")) {
    		validationMessages.add(new ValidationMessage("descriptionText", "Attribute " + "Description" + " is not set.",
    				ValidationMessage.Severity.ERROR, "descriptionText"));
    	}
    	if (emissionsDoc.getEmissionsDocumentTypeCD() == null || emissionsDoc.getEmissionsDocumentTypeCD().trim().equals("")) {
    		validationMessages.add(new ValidationMessage("docTypeChoice", "Attribute " + "Attachment Type" + " is not set.",
    				ValidationMessage.Severity.ERROR, "docTypeChoice"));
    	}
    	
    	// make sure document file is provided
    	if (!docUpdate && publicFileInfo == null && emissionsDoc.getDocumentId() == null) {
    		validationMessages.add(new ValidationMessage(
    				"publicFile", "Must specify a public document",
    				ValidationMessage.Severity.ERROR, "publicFile"));
    	}
    	if (docUpdate && publicFileInfo == null && emissionsDoc.getDocumentId() == null) {
    		validationMessages.add(new ValidationMessage(
    				"publicFile", "Must specify a public document",
    				ValidationMessage.Severity.ERROR, "publicFile"));
    	}
    	
		if (fileToUpload != null && !DocumentUtil.isValidFileExtension(fileToUpload)){
			validationMessages.add(new ValidationMessage("documentId",
					DocumentUtil.invalidFileExtensionMessage(null), ValidationMessage.Severity.ERROR, "publicFile")); 
			publicFileInfo = null;
		}
		
    	// make sure a justification is provided for trade secret document
    	if (isTradeSecretAllowed()) { 
			if (tsFileToUpload != null && !DocumentUtil.isValidFileExtension(tsFileToUpload)){
				validationMessages.add(new ValidationMessage("tradeSecretDocId",
						DocumentUtil.invalidFileExtensionMessage("trade secret"), ValidationMessage.Severity.ERROR, "tradeSecretFile")); 
				tsFileInfo = null;
			} else {
				
				if (tsFileInfo != null || emissionsDoc.getTradeSecretDocId() != null) {
	    			if (emissionsDoc.getTradeSecretReason() == null || emissionsDoc.getTradeSecretReason().trim().equals("")) {
	    				validationMessages.add(new ValidationMessage("tradeSecretReason", "Attribute " + "Trade Secret Justification" + " is not set.",
	    						ValidationMessage.Severity.ERROR, "tradeSecretReason"));
	    			}
	    		}
	    		//  make sure a trade secret document provided if justification given
	    		if(!docUpdate) {  // Just check when creating attachment
	    			if (emissionsDoc.getTradeSecretReason() != null && emissionsDoc.getTradeSecretReason().trim().length() > 0) {
	    				if (tsFileInfo == null && emissionsDoc.getTradeSecretDocId() == null) {
	    					validationMessages.add(new ValidationMessage("tradeSecretFile", "Attribute " + "Trade Secret File" + " is not set.",
	    							ValidationMessage.Severity.ERROR, "tradeSecretFile"));
	    				}
	    			}
	    		}
			}    		

    	}

    	if (validationMessages.size() == 0) {
    		if (docUpdate) {
    			// modify database instance of document
    			try {
    				getEmissionsReportService().modifyEmissionsDocument(emissionsDoc);
    				getEmissionsReportService().loadDocuments(emissionsDoc, inStaging);
    				ListIterator<EmissionsDocumentRef> iterator = attachments.listIterator();
    				while (iterator.hasNext()) {
    					EmissionsDocumentRef matchDoc = iterator.next();
    					if (matchDoc.getEmissionsDocId().equals(emissionsDoc.getEmissionsDocId())) {
    						iterator.remove();
    						iterator.add(emissionsDoc);
    					}
    				}
    				report.setAttachments(attachments);
    				if((report.getRptReceivedStatusDate() == null || openedForEdit) && report.isValidated()) {
    	            	setPassedValidation(false);
    	            	getEmissionsReportService().setValidatedFlag(report, false);
    	            }
    				DisplayUtil.displayInfo("The attachment information has been updated.");
    				FacesUtil.returnFromDialogAndRefresh();
    			} catch (RemoteException re) {
    				handleException(report.getEmissionsRptId(), re);
    			}
    		} else {
    			// new document
    			// upload document and create database entry pointing to it
    			emissionsDoc.setEmissionsRptId(report.getEmissionsRptId());
    			try {
    				// add document information to the system
    				Integer userId;
    				if(this.isInternalApp()) {
    					userId = getUserID();
    				}else {
    					userId = CommonConst.GATEWAY_USER_ID;
    				}
    				if (isTradeSecretAllowed()) { 
    					emissionsDoc = getEmissionsReportService().uploadEmissionsDocument(
    						facility, report, emissionsDoc, publicFileInfo, tsFileInfo, 
    						userId);
    				} else {
    					emissionsDoc.setTradeSecretReason(null);
    					emissionsDoc = getEmissionsReportService().uploadEmissionsDocument(
        					facility, report, emissionsDoc, publicFileInfo, null, 
        					userId);
    				}
    				getEmissionsReportService().loadDocuments(emissionsDoc, inStaging);
    				// add document to local list of documents
    				attachments.add(emissionsDoc);
    				report.setAttachments(attachments);

    				// clean up temporary variables
    				fileToUpload = null;
    				if (publicFileInfo != null) {
    					publicFileInfo.cleanup();
    					publicFileInfo = null;
    				}
    				tsFileToUpload = null;
    				if (tsFileInfo != null) {
    					tsFileInfo.cleanup();
    					tsFileInfo = null;
    				}
    				emissionsDoc = null;
    				if((report.getRptReceivedStatusDate() == null || openedForEdit) && report.isValidated()) {
    	            	setPassedValidation(false);
    	            	getEmissionsReportService().setValidatedFlag(report, false);
    	            }
    				DisplayUtil.displayInfo("The attachment has been added.");
    				FacesUtil.returnFromDialogAndRefresh();
    			} catch (RemoteException re) {
    				handleException(report.getEmissionsRptId(), re);
    			}
    		}
    	} else {
    		displayValidationMessages("", validationMessages.toArray(new ValidationMessage[0]));
    	}
    }

    public final void cancelEditDoc(ActionEvent actionEvent) {
        docUpdate = true;
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public final void docDialogDone(ReturnEvent returnEvent) {
        docUpdate = true;
        emissionsDoc = null;
        if (publicFileInfo != null) {
            publicFileInfo.cleanup();
            publicFileInfo = null;
        }
        if (tsFileInfo != null) {
            tsFileInfo.cleanup();
            tsFileInfo = null;
        }
    }

    public final void removeEditDoc(ActionEvent actionEvent) {
        // remove document from database
        try {
            getEmissionsReportService().removeEmissionsDocument(emissionsDoc);
            // remove document from local object
            ListIterator<EmissionsDocumentRef> iterator = attachments.listIterator();
            while (iterator.hasNext()) {
                EmissionsDocumentRef matchDoc = iterator.next();
                if (matchDoc.getEmissionsDocId().equals(emissionsDoc.getEmissionsDocId())) {
                    iterator.remove();
                }
            }
            report.setAttachments(attachments);
            if((report.getRptReceivedStatusDate() == null || openedForEdit) && report.isValidated()) {
            	setPassedValidation(false);
            	getEmissionsReportService().setValidatedFlag(report, false);
            }
            DisplayUtil.displayInfo("The attachment has been removed.");
            FacesUtil.returnFromDialogAndRefresh();
        } catch (RemoteException re) {
            handleException(reportId, re);
        }
        
    }

    public final String startViewDoc() {
        emissionsDoc = (EmissionsDocumentRef) FacesUtil.getManagedBean("doc");
        return documentDialogNav();
    }

    public final void closeViewDoc(ActionEvent actionEvent) {
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public final String getPublicDocURL() {
        String url = null;
        EmissionsDocumentRef docRef = (EmissionsDocumentRef) FacesUtil
                    .getManagedBean(DOC_JSP_VAR);
        Document doc = docRef.getPublicDoc();
        if (doc != null) {
            url = doc.getDocURL();
        } else {
            // this should never happen
            logger.error("No public document found for Emission Report document id: " + 
                    docRef.getEmissionsDocId() + " and Emissions InventoryId=" + report.getEmissionsInventoryId());
        }
        return url; // stay on same page
    }
    
    public final String getTradeSecretDocURL() {    
       String url = null;
       if (isPublicApp()) {
    	   return null;
       }
        // downloadDoc is set in the startDownloadTSDoc method for internal apps
        // but this method is called directly by the portal (bypassing startDownloadTSDoc)
        // so we need to retrieve the value if it is not already set
        if (downloadDoc == null || isPortalApp()) {
            downloadDoc = (EmissionsDocumentRef) FacesUtil.getManagedBean(DOC_JSP_VAR);
        }
        if (downloadDoc == null || downloadDoc.getTradeSecretDocId() == null) {
            DisplayUtil.displayError("No Trade Secret document is available.");
        } else {
            Document doc = downloadDoc.getTradeSecretDoc();
            if (doc != null) {
                url = doc.getDocURL();
                //downloadDocument will retrieve the FILE_STORE_ROOT_PATH.
                //url = doc.getDirName() + doc.getBasePath();
            } else {
                // this should never happen
                logger.error("No public document found for Emission Report document id: " + 
                        downloadDoc.getEmissionsDocId()
                        + " and reportId=" + report.getEmissionsInventoryId());
            }
        }
        
        /*if(url != null){
            try {               
                OpenDocumentUtil.downloadDocument(url);
            } catch (IOException e) {
                DisplayUtil.displayError("A system error has occurred. " +
                        "Please contact System Administrator.");
                logger.error(e.getMessage(), e);
            }
        } */       
        return url; // stay on same page
    }
    
    
    public String getTsConfirmMessage() {
    	return SystemPropertyDef.getSystemPropertyValue("TsConfirmMessage", null);
    }
    
    public final String startDownloadTSDoc() {
        String ret = TS_DOC_DOWNLOAD_DIALOG;
        downloadDoc = (EmissionsDocumentRef) FacesUtil.getManagedBean(DOC_JSP_VAR);
        return ret;
    }

    public final String downloadTSDoc() {
    	if (isPublicApp()) {
    		return null;
    	}
        // downloadDoc is set in the startDownloadTSDoc method for internal apps
        // but this method is called directly by the portal (bypassing startDownloadTSDoc)
        // so we need to retrieve the value if it is not already set
        if (downloadDoc == null) {
            downloadDoc = (EmissionsDocumentRef) FacesUtil.getManagedBean(DOC_JSP_VAR);
        }
        if (downloadDoc == null || downloadDoc.getTradeSecretDocId() == null) {
            DisplayUtil.displayError("No Trade Secret document is available.");
        } else {
            try {
                //getEmissionsReportService().loadDocuments(downloadDoc, inStaging);
                Document tsDoc = downloadDoc.getTradeSecretDoc();
                OpenDocumentUtil.downloadDocument(tsDoc.getPath());
            } catch (RemoteException e) {
                handleException(reportId, e);
            } catch (IOException e) {
                handleException(reportId, e);
            }
        }
        
        return null;
    }
    
    public final void cancelDownloadTSDoc(ActionEvent event) {
        downloadDoc = null;
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public final boolean isDeleteDocs() {
        return isInternalEditable() && !isClosedForEdit();
    }
    
    public final boolean getOkToEditAttachment() {
        // docUpdate is also true when viewing datagrid of attachments.
    	if (isPublicApp()) {
    		return false;
    	}
        boolean rtnOkToEdit = true;
        
        if (isGeneralUser()) {
        	EmissionsDocumentRef docRef = (EmissionsDocumentRef) FacesUtil.getManagedBean(DOC_JSP_VAR);
        	if (docRef == null) {
        		// New attachmentt
        		docRef = emissionsDoc;
        	}
        	Timestamp lastModifiedTS = docRef.getLastModifiedTS();
        	Timestamp rptSubmitDt = report.getRptReceivedStatusDate();

        	if(lastModifiedTS != null && rptSubmitDt != null){
        		if(lastModifiedTS.before(rptSubmitDt)){
        			rtnOkToEdit = false;
        		}
        	}
        } else if (isReadOnlyUser()) {
        	rtnOkToEdit = false;
        } else if (isPortalApp() && report.getRptReceivedStatusDate() != null) { // Portal Existing attachment. User cannot add/edit attachment after submit
        	rtnOkToEdit = false;
        }
        
        // don't allow edit if viewing report from third level menu on eBiz
        boolean disallow = isPortalApp() && !inStaging;
        return (rtnOkToEdit) && !disallow;
    }
    
    public final boolean isTradeSecretVisible() {
    	if (isPublicApp()) {
    		return false;
    	}
        return isPortalApp() || InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("reports.viewtradesecret");
    }
    
    // attachments end
    
    public boolean isStars2Admin() {
        return isInternalApp() && InfrastructureDefs.getCurrentUserAttrs().isStars2Admin();
    }
    
    public boolean isModTV_SMTV() {
    	if (isPublicApp()) {
    		return false;
    	}
        return isPortalApp() || InfrastructureDefs.getCurrentUserAttrs().isStars2Admin() ||  InfrastructureDefs.getCurrentUserAttrs().isGeneralUser() ;
    }
    
    public boolean isFerEsApprover(){
    	if (isPublicApp()) {
    		return false;
    	}
    	
        return isInternalApp() && InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("reports.FerEsApprove");
    }
    
    public boolean isEisApprover(){
    	if (isPublicApp()) {
    		return false;
    	}
    	
        return isInternalApp() && InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("reports.EisApprove");
    }
    
    public boolean isNtvIssuance(){//this role implements create/edit/submit of ntv reports.
    	if (isPublicApp()) {
    		return false;
    	}
        return isPortalApp() || InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("reports.NtvIssuance");
    }
    
    public boolean isInvoicer(){
        return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("invoices.posttorevenues");
    }
    
    public boolean isFacilityReportEnabler(){
        return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid("facilities.profile.enableReporting");
    }
    
    public boolean isSubmitted() {
        // used by jsp only
        return report.isSubmitted();
    }
    
    public boolean isShowSubmitButton() {
        return !isEditable() && !report.isSubmitted() && (isHasPassedValidation() || report.isValidated());
    }
    
    public final boolean isInternalEditable(){    	
    	boolean ret = true;
    	if (isPublicApp()) {
    		ret = false;
    	} else if(isPortalApp() && !isInStaging()){
    		ret = false;
    	}
    	return ret;
    }
    
    public final boolean isNoteModify() {
        return noteModify;
    }

    public final EmissionsReportNote getReportNote() {
        return reportNote;
    }

    public final void setReportNote(EmissionsReportNote reportNote) {
        this.reportNote = reportNote;
    }
    
    public final EmissionsReportNote getModifyEINote() {
        return modifyEINote;
    }

    public final void setModifyEINote(EmissionsReportNote modifyEINote) {
        this.modifyEINote = modifyEINote;
    }
    
    public final boolean isEditable() {
        return editable;
    }

    public final void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public final boolean isFireEditable() {
        return fireEditable;
    }

    public final void setFireEditable(boolean fireEditable) {
        this.fireEditable = fireEditable;
    }

    public final boolean isYearlyInfoEditable() {
        return yearlyInfoEditable;
    }

    public final void setYearlyInfoEditable(boolean yearlyInfoEditable) {
        this.yearlyInfoEditable = yearlyInfoEditable;
    }
    
    public boolean isEditable1() {
        return editable1;
    }

    public void setEditable1(boolean editable1) {
        this.editable1 = editable1;
    }

    public EmissionsDocumentRef getEmissionsDoc() {
        return emissionsDoc;
    }

    public void setEmissionsDoc(EmissionsDocumentRef emissionsDoc) {
        this.emissionsDoc = emissionsDoc;
    }

    public UploadedFile getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(UploadedFile fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public UploadedFile getTsFileToUpload() {
        return tsFileToUpload;
    }

    public void setTsFileToUpload(UploadedFile tsFileToUpload) {
        this.tsFileToUpload = tsFileToUpload;
    }

    public List<EmissionsDocumentRef> getAttachments() {
        return attachments;
    }

    public boolean isDocUpdate() {
        return docUpdate;
    }

    public void setDocUpdate(boolean docUpdate) {
        this.docUpdate = docUpdate;
    }
    
    protected String getUserName() {
    	String s = new String();
        if (isInternalApp()) {
            return InfrastructureDefs.getCurrentUserAttrs().getUserName();
        } else if (isPortalApp()) {
        	MyTasks myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
        	return myTasks.getUserName();
        } else {
        	return s;
        }
    }

    public List<Document> getReportDocuments() {
        return reportDocuments;
    }

    public List<Document> getReportAttachments() {
    	return reportAttachments;
    }
    
    public boolean isHideTradeSecret() {
        return hideTradeSecret;
    }

    public void setHideTradeSecret(boolean hideTradeSecret) {
        this.hideTradeSecret = hideTradeSecret;
    }

    public Integer getSavedReportId() {
        return savedReportId;
    }

    public void setSavedReportId(Integer savedReportId) {
        this.savedReportId = savedReportId;
    }

    public Task getCreateFTask() {
        return createFTask;
    }

    public void setCreateFTask(Task createFTask) {
        this.createFTask = createFTask;
    }

    public Task getCreateRTask() {
        return createRTask;
    }

    public void setCreateRTask(Task createRTask) {
        this.createRTask = createRTask;
    }

    public boolean isErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public boolean isOpenedForEdit() {
        return openedForEdit;
    }
    
    public boolean isHasBeenOpenedForEdit() {
        return hasBeenOpenedForEdit;
    }

    public boolean isReloadRpt() {
        return reloadRpt;
    }

    public void setReloadRpt(boolean reloadRpt) {
        this.reloadRpt = reloadRpt;
    }

    public boolean isOkToEdit() {
        return okToEdit;
    }

    public final UploadedFileInfo getPublicFileInfo() {
        return publicFileInfo;
    }

    public final UploadedFileInfo getTsFileInfo() {
        return tsFileInfo;
    }
    
    protected void handleException(Integer rptId, Exception re) {
        if (re != null) {
            if (re instanceof DataStoreConcurrencyException) {
                DisplayUtil.displayWarning("The data you are trying to update is out of synch with the data store. Please try again.");
                logger.error("For reportId= " + rptId + ": " + re.getMessage(), re);
            } else if(re instanceof ApplicationException) {
                if(!((ApplicationException)re).prettyMsgIsNull()) {
                        DisplayUtil.displayError(((ApplicationException)re).getPrettyMsg());
                } else {
                DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
                logger.error("For reportId " +
                        rptId + ", failed due to " + dataProb + ": " + re.getMessage(), re);
                }
            } else if(re instanceof DAOException) {
                logger.error("For reportId " +
                        rptId + ", failed due to : " + re.getMessage(), re);
                if(((DAOException)re).getPrettyMsg() != null) {
                    DisplayUtil.displayError(((DAOException)re).getPrettyMsg() + "  Operation not performed.");
                } else {
                    DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
                }
            } else {
                DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
                logger.error("For reportId= " + rptId + ": " + re.getMessage(), re);
            }
        }
    }

    public boolean isHasBeenValidated() {
        return passedValidation != null;
    }
    
    public boolean isHasPassedValidation() {
        return passedValidation != null && passedValidation.booleanValue();
    }

    public void setPassedValidation(Boolean passedValidation) {
        this.passedValidation = passedValidation;
    }
    
    public boolean isAdminEditable() {
        return adminEditable;
    }

    public void setAdminEditable(boolean adminEditable) {
        this.adminEditable = adminEditable;
    }
    
    public boolean isTradeSecretAllowed() {
    	if (isPublicApp()) {
    		return false;
    	}
    	boolean ret = false;
    	
    	if (emissionsDoc != null) {
    		ret = emissionsDoc.isTradeSecretAllowed();
    	}

		return ret;
	}
    
    public boolean isDeleteDocAllowed() {
		return deleteDocAllowed;
	}

	public void setDeleteDocAllowed(boolean deleteDocAllowed) {
		this.deleteDocAllowed = deleteDocAllowed;
	}
	
	public final boolean isNoteReadOnly() {
		if(isReadOnlyUser()){
			return true;
		}
		
    	return noteReadOnly;
    }

    public final void setNoteReadOnly(boolean noteReadOnly) {
    	this.noteReadOnly = noteReadOnly;
    }
    
    
    
    public boolean isGeneralUser() {
        return isInternalApp() && InfrastructureDefs.getCurrentUserAttrs().isGeneralUser();
    }
}
