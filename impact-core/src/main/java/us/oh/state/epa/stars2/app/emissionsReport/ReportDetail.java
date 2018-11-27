package us.oh.state.epa.stars2.app.emissionsReport;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import oracle.adf.view.faces.component.UIXTable;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.invoice.InvoiceDetail;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.WorkflowProcessDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;

public class ReportDetail extends TaskBase {

	private static final long serialVersionUID = -148198869309056100L;

	private Integer reportId;
    private EmissionsReport emissionsReportRow;
    
    private ReportProfile tvProfile = null;
    private ErNTVDetail ntvProfile = null;

    // For workflow
    NtvReportTask ntvReportTask = null;
    TvReportTask tvReportTask = null;
    transient UIXTable reviewTable;
    
    private EmissionsReportService emissionsReportService;
    
    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

    public ReportDetail() {
        super();
    }

//  public final EmissionsReport getEmissionsReport() {
//  return emissionsReport;
//  }

//  public final void setEmissionsReport(EmissionsReport emissionsReport) {
//  this.emissionsReport = emissionsReport;
//  }

    public final void getReportRow() {
            try {
                emissionsReportRow = getEmissionsReportService().retrieveEmissionsReportRow(
                        reportId, false);  // this is AQD database.
                if(emissionsReportRow == null) {
                    DisplayUtil.displayError("Report not found");
                } else {
                    ReportSearch reportSearch = (ReportSearch) FacesUtil
                    .getManagedBean("reportSearch");
                    reportSearch.setNtvReport(false);
                }
            } catch (RemoteException re) {
                DisplayUtil.displayError("Failed to read report");
                logger.error(re.getMessage(), re);
            }
    }

    public final Integer getReportId() {
        return reportId;
    }

    public final void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
    
    public String submit() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return submitInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final String submitInternal() {
        String ret = null;
        getReportRow();
            ReportDetail rd = (ReportDetail)FacesUtil
            .getManagedBean("reportDetail");
                ReportProfile reportProfile = (ReportProfile) FacesUtil
                .getManagedBean("reportProfile");
                reportProfile.setReportId(reportId);
                reportProfile.setReport(null);
                reportProfile.setInStaging(false);
                rd.setNtvReport(false);
                reportProfile.getReport(reportProfile); // read info first
                setNtvReport(false);
                ret = "emissionReport";
        return ret;
    }
    
    public final String refresh() {
        if(reportId == null) {
            logger.error("refresh() called but reportId is null");
            DisplayUtil.displayError("Failed to reach emissions inventory");
            return null;
        } else {
            try {
                emissionsReportRow = getEmissionsReportService().retrieveEmissionsReportRow(
                        reportId, false);
                if(emissionsReportRow == null) {
                    DisplayUtil.displayError("Report not found");
                }
                	ReportProfile reportProfile = (ReportProfile) FacesUtil
                    .getManagedBean("reportProfile");
                    reportProfile.setReport(null);
                    reportProfile.getReport(reportProfile); // read info first;
                return "emissionReport";
            } catch (RemoteException re) {
                DisplayUtil.displayError("Failed to read report");
                logger.error(re.getMessage(), re);
            }
        }
        return null;
    }

//  For workflow

    protected Integer externalId;  //  Id given by work flow --will be report Id
    protected Integer returnExternalId;  // Id given back.  May be report or invoice

    public void setNtvReportTask(NtvReportTask ntvReportTask) {
        this.ntvReportTask = ntvReportTask;
    }

    public void setTvReportTask(TvReportTask tvReportTask) {
        this.tvReportTask = tvReportTask;
    }

    public final List<ValidationMessage> validate(Integer inActivityTemplateId) {
        return getReportTask().validate(inActivityTemplateId);
    }

    public String findOutcome(String url, String ret) {
        return getReportTask().findOutcome(url, ret);
    }

    // Should be able move actual code up to this class
    public String getExternalName(ProcessActivity activity) {
        return getReportTask().getExternalName(activity);
    }

    public String getExternalNum() {
    	return emissionsReportRow.getEmissionsInventoryId();
    }

    public Integer getReturnExternalId() {
        return returnExternalId;
    }

    public void setExternalId(Integer externalId) {
        this.reportId = externalId;
        this.externalId = externalId;
        submitInternal();
    }

    public void setReturnExternalId(Integer returnExternalId) {
        this.returnExternalId = returnExternalId;
    }

    public String toExternal() {
        return getReportTask().toExternal();
    }

    public Integer getExternalId() {
        return externalId;
    }
    
    public ReportTask getReportTask() {
    	ReportTask reportTask = null;
        if (isNtvReport()) {
        	reportTask = (ReportTask) FacesUtil.getManagedBean("ntvReportTask");
        } else {
        	reportTask = (ReportTask) FacesUtil.getManagedBean("tvReportTask");
        }
        return reportTask;
    }

    public boolean isDoSelectedButton(ProcessActivity activity) {
        return getReportTask().isDoSelectedButton(activity);
    }

    public String getDoSelectedConfirmMsg() {
        return getReportTask().getDoSelectedConfirmMsg();
    }

    public String getDoSelectedConfirmType() {
        return getReportTask().getDoSelectedConfirmType();
    }

    public String getDoSelectedButtonText() {
        return getReportTask().getDoSelectedButtonText();
    }

    public void doSelected(UIXTable table) {
        getReportTask().doSelected(table);
    }

    public String doSelectedReview(){
    	return getReportTask().doSelectedReview(reviewTable);
    }
    
    public final String checkInSelected() {
        getReportTask().setEnableCompletionButton(false);
		return getReportTask().checkInSelected(reviewTable);
	}
    
    public String goToCurrentWorkflow() {
        return getReportTask().goToCurrentWorkflow();
    }

    public String goToAllWorkflows() {
        setProcessTypeCd(WorkflowProcessDef.EMISSION_REPORTING);
        return getReportTask().goToAllWorkflows();
    }

    public Integer getWorkflowProcessId() {
        return getReportTask().getWorkflowProcessId();
    }

    public void setWorkflowProcessId(Integer workflowProcessId) {
        getReportTask().setWorkflowProcessId(workflowProcessId);
    }

    public Integer getWorkflowActivityId() {
        return getReportTask().getWorkflowActivityId();
    }

    public void setWorkflowActivityId(Integer workflowActivityId) {
        getReportTask().setWorkflowActivityId(workflowActivityId);
    }

    public boolean getFromTODOList() {
        return getReportTask().getFromTODOList();
    }

    public void setFromTODOList(boolean fromTODOList) {
        getReportTask().setFromTODOList(fromTODOList);
    }

    public String getInvoiceDtMsg(){
        return getReportTask().getInvoiceMsg();
    }

    public boolean isPostState(){
        return getReportTask().isPostState();
    }

    public Timestamp getInvoiceDt() {
    	return getReportTask().getInvoiceDate();
    }

    public void setInvoiceDt(Timestamp invoiceDt) {
        getReportTask().setInvoiceDate(invoiceDt);
    }
    
    public Timestamp getMaxInvoiceDate() {

    	Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        int lastDay = 30;
        Integer days = SystemPropertyDef.getSystemPropertyValueAsInteger("Max_Future_Invoice_Days", 30);
        if (days != null && days > 1) {
        	lastDay = days;
        }
        cal.add(Calendar.DAY_OF_MONTH, lastDay);
        Timestamp maxInvoiceDate = new Timestamp(cal.getTimeInMillis());
        return maxInvoiceDate;
    }

    public Timestamp getMinInvoiceDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Timestamp minInvoiceDate = new Timestamp(cal.getTimeInMillis());
        return minInvoiceDate;
    }

    public final String doAggregatePost(){
        String ret;
        ReportTask reportTask = null;
        reportTask = getReportTask();
        if(reportTask.getInvoiceDate() != null){
            try{
                reportTask.doPost();
            } catch(DAOException e) {
                DisplayUtil.displayError("A portion of the aggregate post failed.  You may try starting over.");
            }
           // ret = "dialog:invPostList";            
            ret = SUCCESS;
            FacesUtil.returnFromDialogAndRefresh();
        }else{
            DisplayUtil.displayError("Please select a Invoice Date");
            ret = "dialog:changeInvoiceDate";
        }

        return ret; 
    } 
    
    public final boolean isEnableCompletionButton() {
        return getReportTask().isEnableCompletionButton();
    }
    
    public final boolean isEnablePostAdjustButton() {
        return getReportTask().isEnablePostAdjustButton();
    }

    /*public final String doAggregatePrint(){
        String ret;
        getReportTask().doPrint();
        ret = "dialog:invPostList"; 

        return ret;
    }*/

    public boolean getEditMode() {
        if(isNtvReport()) {
            return getNtvProfile().isEditable()
            || getNtvProfile().isEditable1() || getNtvProfile().isAdminEditable();
        }
        return getTvProfile().isEditable() || getTvProfile().isEditableM();
    }

    public boolean reportApproved() {
        if(isNtvReport()) {
            return getNtvProfile().isApproved();
        }
        
        return getTvProfile().isApproved();
    }
    
    public boolean reportNotNeeded() {
        if(isNtvReport()) {
            return getNtvProfile().isReportNotNeeded();
        } 
        
        return getTvProfile().isReportNotNeeded();
    }
    // End For Workflow

    public final String validationDlgAction() {
        if(isNtvReport()) {
            return getNtvProfile().validationDlgAction();
        }
            
        return getTvProfile().validationDlgAction();
    }
    
    public final String getValidationDlgReference() {
        if(isNtvReport()) {
            return getNtvProfile().getValidationDlgReference();
        }
            
        return getTvProfile().getValidationDlgReference();
    }
    
    public void setValidationDlgReference(String validationDlgReference) {
        if(isNtvReport()) {
            getNtvProfile().setValidationDlgReference(validationDlgReference);
        } else {
            getTvProfile().setValidationDlgReference(validationDlgReference);
        }
    }
    
    public boolean isDisallowClick() {
        if(isNtvReport()) {
            return getNtvProfile().isDisallowClick();
        } 
        
        return getTvProfile().isDisallowClick();
    }
    
    public boolean isOpenedForEdit() {
        if(isNtvReport()) {
            return getNtvProfile().isOpenedForEdit();
        } 
        
        return getTvProfile().isOpenedForEdit();
    }

    public ErNTVDetail getNtvProfile() {
        ntvProfile = (ErNTVDetail)FacesUtil
        .getManagedBean("erNTVDetail");
        return ntvProfile;
    }

    public ReportProfile getTvProfile() {
        tvProfile = (ReportProfile)FacesUtil
        .getManagedBean("reportProfile");
        return tvProfile;
    }
    
    private boolean isNtvReport() {
        ReportSearch reportSearch = (ReportSearch) FacesUtil
        .getManagedBean("reportSearch");
        return reportSearch.isNtvReport();
    }
    
    private void setNtvReport(boolean isNtvReport) {
        ReportSearch reportSearch = (ReportSearch) FacesUtil
        .getManagedBean("reportSearch");
        reportSearch.setNtvReport(isNtvReport);
    }

    public EmissionsReport getEmissionsReportRow() {
        return emissionsReportRow;
    }

    public void setEmissionsReportRow(EmissionsReport emissionsReportRow) {
        this.emissionsReportRow = emissionsReportRow;
    }
    
    public UIXTable getReviewTable() {
		return reviewTable;
	}

	public void setReviewTable(UIXTable reviewTable) {
		this.reviewTable = reviewTable;
	}
	
	public String viewInvoice() {
		String ret = null;
		InvoiceDetail invDetail = (InvoiceDetail) FacesUtil
				.getManagedBean("invoiceDetail");
		Invoice inv = null;
		try {
//			if (isNtvReport()) {
//				if(emissionsReportRow == null){
//					getReportRow();
//				}
//				if(emissionsReportRow.getCompanionReport() != null){
//					if(emissionsReportRow.getCompanionReport() < reportId){
//						reportId = emissionsReportRow.getCompanionReport(); 
//					}
//				}
//            }
		    inv = getReportTask().setUpInvoice(reportId);			

			if (inv != null) {
				invDetail.setInvoiceId(inv.getInvoiceId());
				ret = invDetail.submit();
			} else {
				DisplayUtil.displayError("No Invoice Found");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			DisplayUtil.displayError("Failed to retrieve Invoice");
		}
		return ret;
	}
}
