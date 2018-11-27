package us.oh.state.epa.stars2.app.emissionsReport;

import java.rmi.RemoteException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.def.ReportEisStatusDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;
/* 
 * approveReport() -- from facilityRep.jsp
 * approveEISReport() -- from facilityRep.jsp
 * 
 */

public class ReportProfile extends ReportProfileBase {
	
	private static final long serialVersionUID = -39883164474111566L;

	public ReportProfile() {
        super();
    }
      
    public final EmissionsReport getReport() {
        return super.getReport(this);
    }
    
//    public final String refresh() {
//        report = null;
//        if(reportId == null) {
//            logger.error("refresh() called but reportId is null");
//            DisplayUtil.displayError("Failed to reach emissions inventory");
//            err = true;
//            return null;
//        } else {
//            super.getReport(this);
//            return TV_REPORT;
//        }
//    }
    
    public String submitReport() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return submitReportInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private String submitReportInternal() {
        try {
            getEmissionsReportService().submitReport(report, facility,
                    reportEmissions, scReports.getSc(), getUserID(), false, openedForEdit);
            DisplayUtil.displayInfo("Submit successful");
        } catch (RemoteException re) {
            DisplayUtil.displayError("Submit failed");
            handleException(re);
        }
        try { // Read the report back in regardless
            report = getEmissionsReportService().retrieveEmissionsReport(report.getEmissionsRptId(), false);
            byClickEmiUnitOrGrp = null;
            recreateTreeFromReport(treeNodeId(report));
        } catch (RemoteException re) {
            DisplayUtil.displayError("Failed to re-read the report");
            handleException(re);
            err = true; // don't display the report
        }
        return null;
    }
    
    public String toStateRR() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            toStateRRInternal();
        } finally {
            clearButtonClicked();
        }
        return null;
    }

    private final void toStateRRInternal() {
        boolean ok = true;
        try {
            getEmissionsReportService().modifyEmissionsReportState(report,
                    ReportReceivedStatusDef.REVISION_REQUESTED);
        } catch (RemoteException re) {
            handleException(reportId, re);
            ok = false;
        }
        if (ok) {
            String stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                    ReportReceivedStatusDef.REVISION_REQUESTED);
            DisplayUtil.displayInfo("Submitted report state changed to " + stateDesc);
        } else {
            DisplayUtil.displayError("Failed to change the state of the report");
        }
    }
    
    public String toStateAR() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            toStateARInternal();
        } finally {
            clearButtonClicked();
        }
        return null;
    }

    private final void toStateARInternal() {
        if (!report.isApproved()) {
            if(revisedReportNotProcessedMsg != null) {
                DisplayUtil.displayError(revisedReportNotProcessedMsg);
            } else {
                approveReport(ReportReceivedStatusDef.DOLAA_APPROVED_RR);
            }
            return;
        }
        
        boolean ok = true;
        try {
            getEmissionsReportService().modifyEmissionsReportState(report,
                    ReportReceivedStatusDef.DOLAA_APPROVED_RR);
        } catch (RemoteException re) {
            handleException(reportId, re);
            ok = false;
        }
        if (ok) {
            String stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                    ReportReceivedStatusDef.DOLAA_APPROVED_RR);
            DisplayUtil
                    .displayInfo("Approved report state changed to " + stateDesc);
        } else {
            DisplayUtil
                    .displayError("Failed to change the state of the report");
        }
    }
    
    public String toStateAA() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            toStateAAInternal();
        } finally {
            clearButtonClicked();
        }
        return null;
    }

    private final void toStateAAInternal() {
        if (!report.isApproved()) {
            if(revisedReportNotProcessedMsg != null) {
                DisplayUtil.displayError(revisedReportNotProcessedMsg);
            } else {
                approveReport(ReportReceivedStatusDef.DOLAA_APPROVED);
            }
            return;
        }
        boolean ok = true;
        try {
            getEmissionsReportService().modifyEmissionsReportState(report,
                    ReportReceivedStatusDef.DOLAA_APPROVED);
        } catch (RemoteException re) {
            handleException(reportId, re);
            ok = false;
        }
        if (ok) {
            String stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                    ReportReceivedStatusDef.DOLAA_APPROVED);
            DisplayUtil
                    .displayInfo("Approved report state changed to " + stateDesc);
        } else {
            DisplayUtil
                    .displayError("Failed to change the state of the report");
        }
    }
    
    public String confirmToStateNN() {
        return "dialog:confirmNotNeeded";
    }
    
    public String toStateNN() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            toStateNNInternal();
        } finally {
            clearButtonClicked();
        }
        FacesUtil.returnFromDialogAndRefresh();
        return null;
    }

    private final void toStateNNInternal() {
        boolean ok = true;
        if(revisedReportNotProcessedMsg != null) {
            DisplayUtil.displayError(revisedReportNotProcessedMsg);
            return;
        }
        try {
        	getEmissionsReportService().cancelEmissionsReport(report.getEmissionsRptId());        	
        } catch (RemoteException re) {
            handleException(reportId, re);
            ok = false;
        }
        if (ok) {
            String stateDesc;
            if(ReportReceivedStatusDef.isSubmittedCode(report.getRptReceivedStatusCd())) {
                stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                        ReportReceivedStatusDef.REPORT_NOT_NEEDED);
                DisplayUtil.displayInfo("Submitted report state changed to " + stateDesc);
            } else {
                stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                        ReportReceivedStatusDef.APPROVED_REPORT_NOT_NEEDED);
                DisplayUtil.displayInfo("Approved report state changed to " + stateDesc);
            }
            // reread the report
            report = null;
        } else {
            DisplayUtil.displayError("Failed to change the state of the report");
        }
    }

    private final void approveReport(String state) {
        boolean ok = true;
        Integer conflictRpt = null;
        try {
            conflictRpt = getEmissionsReportService().conflictingReportForApproval(facility.getFacilityId(), report);
        } catch(RemoteException re) {
            handleException(reportId, re);
            ok = false;
        }
        if(ok) {
            if(conflictRpt != null) {
                DisplayUtil.displayError(conflictMsg1 + conflictRpt.intValue() + conflictMsg2);
                ok = false;
            }
        }
        if(ok) {
            try {
                // determine emissions taking Phase 1 Boilers into consideration.
                ArrayList<EmissionRow> aReportEmissions = EmissionRow.getEmissions(report, false,
                        true, new Integer(1), scReports, logger, true);
                report = getEmissionsReportService().approveReport(facility, report, state, aReportEmissions);
                if(report.getBillingContactFailureMsg() != null) {
                    DisplayUtil.displayError(report.getBillingContactFailureMsg());
                    // report remains unchanged
                    return;
                }
            } catch (RemoteException re) {
                handleException(reportId, re);
                ok = false;
            } catch (ApplicationException e) {
                handleException(reportId, e);
                ok = false;
            }
        }
        if (ok) {
            DisplayUtil.displayInfo("Approve successful");
        } else {
            DisplayUtil.displayError("Approve failed");
            return;
        }
        if(ok) {
            try {
                report = getEmissionsReportService().retrieveEmissionsReport(report.getEmissionsRptId(), false);
                recreateTreeFromReport(current);
            } catch (RemoteException re) {
                handleException(reportId, re);
                DisplayUtil.displayError("Failed to read report");
                ok = false;
                err = true;
            }
        }
    }
    
    public void approveEISReport() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            approveEISReportInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void approveEISReportInternal() {
        report.setEisStatusCd(ReportEisStatusDef.EIS_APPROVED);
        setEditable(false);
        boolean ok = true;
        try {
            // Don't change report state but use call to set eis state change.
            getEmissionsReportService().modifyEmissionsReportState(report,
                    report.getRptReceivedStatusCd());
        } catch (RemoteException re) {
            handleException(reportId, re);
            ok = false;
        }
        if (ok) {
            DisplayUtil.displayInfo("EIS Approval successful");
        } else {
            DisplayUtil.displayError("Failed to save EIS Approval");
        }
    }


    public final void startCreateReportDone() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            String ret = startCreateReportDoneI();
            ReportSearch rs = (ReportSearch) FacesUtil
            .getManagedBean("reportSearch");
            rs.setPopupRedirectOutcome(ret);
            FacesUtil.returnFromDialogAndRefresh();
        } finally {
            clearButtonClicked();
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void startReportCopy() {
        String ret = startReportCopyI();
        if(ret != null) {
            ReportSearch rs = (ReportSearch) FacesUtil
            .getManagedBean("reportSearch");
            rs.setPopupRedirectOutcome(ret);
        }
        FacesUtil.returnFromDialogAndRefresh();
        //AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public void deleteReport() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            boolean ok = deleteReportI();
            if (ok) {
                ReportSearch reportSearch = (ReportSearch) FacesUtil
                .getManagedBean("reportSearch");
                reportSearch.setPopupRedirectOutcome("ERSearch");
                DisplayUtil.displayInfo(
                "Emissions Inventory successfully deleted");
                ((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_TVDetail"))
                .setDisabled(true);
            } else {
                DisplayUtil.displayError(
                "Failed to delete Emissions Inventory");
            }
            FacesUtil.returnFromDialogAndRefresh();
        } finally {
            clearButtonClicked();
        }
    }
    
    public void associateProfile() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            associateProfileInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private void associateProfileInternal() {
        boolean ok = true;
        try {
            facility = getFacilityProfile();
            if(facility == null) {
                ok = false;
                DisplayUtil.displayError("System error. Please contact system administrator");
                logger.error("Failed to read profile " + fpId);
            }
        }  catch (RemoteException re) {
            handleException(re);
            ok = false;
        }
        try {
            report.setFpId(fpId);
            getEmissionsReportService().modifyEmissionsReport(facility, scReports, report, null, null, openedForEdit);
        }  catch (RemoteException re) {
            handleException(re);
            ok = false;
        }
        if(ok) {
            try {
                report = getEmissionsReportService().retrieveEmissionsReport(report.getEmissionsRptId(), true);
                if(report == null) {
                    ok = false;
                }
                if(ok) {
                    byClickEmiUnitOrGrp = null;
                    getEmissionsReportService().reportFacilityMatch(facility, report, scReports);
                    getEmissionsReportService().locatePeriodNames(facility, report);
                    recreateTreeFromReport(current);
                }
            } catch (RemoteException re) {
                handleException(reportId, re);
                ok = false;
            }
        }
        if (ok) {
            DisplayUtil.displayInfo(
                    "Emissions Inventory successfully associated with facility inventory " +
                    fpId);
        } else {
            DisplayUtil.displayError("Failed to change association");
            err = true;
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public String confirmReopenEdit() {
        return "dialog:confirmReopenEdit";
    }
    
    public String toReopenEdit() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        FacesUtil.returnFromDialogAndRefresh();
        try {
            reopenEdit();
        } finally {
            clearButtonClicked();
        }
        return null;
    }
    
    // Used to cancel Reopen Edit
    public final void cancelReopenEdit() {
    	if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
     
        try {
        	cancelEditTS();
        } finally {
            clearButtonClicked();
            openedForEdit = false;
            hasBeenOpenedForEdit = false;
        }
        DisplayUtil
        .displayInfo("Reopen Edit Canceled.");
    }
  
//	public boolean isEiConfidentialDataAccess() {
//		boolean ret = false;
//
//		if (isInternalApp()) {
//			ret = InfrastructureDefs.getCurrentUserAttrs()
//					.isCurrentUseCaseValid("EIConfidentialDataAccess");
//		}
//
//		return ret;
//	}

    
    //public void generateInvoice(){
    	
    	/*try{
    		/*EmissionsDocumentRef emissionsDocTemp =
               (EmissionsDocumentRef) FacesUtil.getManagedBean(TemplateDocTypeDef.TV_FEE_INVOICE);
             // make sure doc is not null
             if (emissionsDocTemp != null) {
                 // make sure doc has an id
                 if (emissionsDocTemp.getEmissionsDocId() != null) {
                     emissionsDoc = getEmissionsReportService().retrieveEmissionsDocument(
                             emissionsDocTemp.getEmissionsDocId(), inStaging);
                     if(emissionsDoc == null) {
                         throw new DAOException("Failed to locate EmissionsDocument with id "
                                 + emissionsDocTemp.getEmissionsDocId()
                                 + " for emissions inventory " + report.getEmissionsRptId());
                     }*/
                    
                    //emissionsDoc = generateTempDoc(emissionsDoc, DOC_EMISS_INVOICE);   
                /* } else {
                     logger.error("For reportId=" + report.getEmissionsRptId() + ", invalid Emission Report document with no id in document table");
                 }
             } else {
                 logger.error("For reportId=" + report.getEmissionsRptId() + ", unable to retrieve managed bean for document in Emission Report document table");
             }
         } catch (RemoteException e) {
             handleException(report.getEmissionsRptId(), e);
         }*/
        	//return "dialog:openGenDoc";    
    	
    //}
    
    /*public final String  generateInvoice() {
	    TemplateDocument templateDoc = TemplateDocTypeDef
	            .getTemplate(TemplateDocTypeDef.TV_FEE_INVOICE);
	    String docURL=null;
	    doc = new EmissionsDocument();    
	   
	    try {
	    	if(templateDoc!=null)
	    		docURL = getEmissionsReportService().generateTempDocument(getReport(), facility, templateDoc);
	    	doc.setGeneratedDocumentPath(docURL);
	        
	    }catch (Exception ex) {
	        if (ex instanceof DAOException && ex.getCause() == null) {
	            DisplayUtil.displayError(ex.getMessage());
	           
	        }
	        DisplayUtil.displayError(ex.getMessage() + " "
	                + templateDoc.getPath());	        
	       
	    }	
	    return "dialog:openReportGenDoc";
	}*/
}
