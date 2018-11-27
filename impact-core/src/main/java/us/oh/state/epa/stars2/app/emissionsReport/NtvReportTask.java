package us.oh.state.epa.stars2.app.emissionsReport;

import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.app.invoice.InvoiceDetail;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;

@SuppressWarnings("serial")
public class NtvReportTask extends ReportTask {
    private ReportDetail mainProfile = null;
    
	// Begin implementation of abstract and other methods from TaskBase
	public String findOutcome(String url, String ret) {
	    if (url.contains("report")) {
	        ReportSearch reportSearch = (ReportSearch) FacesUtil
	        .getManagedBean("reportSearch");
	        reportSearch.setNtvReport(true);
	        ret = "emissionReport";
	    } else if (url.contains("invoice")) {
	        InvoiceDetail invD = (InvoiceDetail) FacesUtil
	        .getManagedBean("invoiceDetail");
	        try {
	            Invoice inv = setUpInvoice(mainProfile.getExternalId());

	            if (inv != null) {
	                mainProfile.setReturnExternalId(inv.getInvoiceId());
	                invD.setInvoiceId(mainProfile.getReturnExternalId());
	                invD.submit();
	                mainProfile.setWorkflowProcessId(getWorkflowProcessId());
	                mainProfile.setWorkflowActivityId(getWorkflowActivityId());
	                mainProfile.setFromTODOList(getFromTODOList());

	                ret = "invoiceDetail";
	            }
	        } catch(DAOException daoe) {
	            DisplayUtil.displayError("Failed to read invoice for report " + mainProfile.getExternalId());
	        }
	    }
	    return ret;
	}

	public ReportDetail getMainProfile() {
		return mainProfile;
	}

	public void setMainProfile(ReportDetail mainProfile) {
		this.mainProfile = mainProfile;
	}

	public Integer getExternalId() {
		return mainProfile.getReturnExternalId();
	}

	public String getExternalNum() {
		return mainProfile.getExternalNum();
	}

	public void setExternalId(Integer externalId) {
		// Save this for use after getExternalName() called
		mainProfile.setExternalId(externalId);
        mainProfile.getReportRow();
	}

	public String toExternal() {
		// Do we need to see if Invoice has been reached?
		mainProfile.submit();
		return "emissionReport";
	}

	public String getExternalName(ProcessActivity activity) {
		String ret = super.getExternalName(activity);
		String url = activity.getActivityUrl();

		if (url.contains("report")) {
			mainProfile.setReportId(mainProfile.getExternalId());
			ret = "Emissions Inventory";
		} 
		
		if(url.contains("invoice")){
			ret = "Invoice";
		}				
		// TODO CHECK THIS
/*		try {
			ActivityTemplate at = workFlowBO().retrieveActivityTemplate(
					activity.getActivityTemplateId());
			ProcessTemplate pt = workFlowBO().retrieveProcessTemplate(
					at.getProcessTemplateId());
*/
			super.templateName = "Blue Card Review";// pt.getProcessTemplateNm();
/*		} catch (Exception re) {

		}*/
		return ret;
	}

	@Override
	public final void cancellation(){
    	cancellation(mainProfile.getExternalId());
    }
	
	public String getTemplateName() {
		return templateName;
	}

	@Override
	public final List<ValidationMessage> validate(Integer inActivityTemplateId) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
		if (122 == inActivityTemplateId.intValue()) {
			if(!mainProfile.reportNotNeeded()){
				if (!mainProfile.reportApproved()) {
					// Report not yet approved.
					messages.add(new ValidationMessage("Report",
						"Report must first be approved.",
						ValidationMessage.Severity.ERROR));
				}
			}
		} else {
			// TODO Add additional workflow step validations
		}

		return messages;
	}

	public boolean getEditMode() {
		return mainProfile.getEditMode();
	}

	// Uses getValidationDlgReference() defined below

	// Uses validationDlgAction() defined below
}
