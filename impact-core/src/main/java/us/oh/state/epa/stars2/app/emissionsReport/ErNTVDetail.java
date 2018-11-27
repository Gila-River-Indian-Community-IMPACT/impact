package us.oh.state.epa.stars2.app.emissionsReport;

import java.rmi.RemoteException;



import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.reports.ErNTVBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;


@SuppressWarnings("serial")
public class ErNTVDetail extends ErNTVBase {
    boolean approveNeedsRevision = false;  // which button was clicked? Approve or Approve/Revision

    public ErNTVDetail() {
        super();
    }
    
    private EmissionsReportService emissionsReportService;
    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}
    
    public final EmissionsReport getReport1() {
        return super.getReport1(this);
    }
    
    public final EmissionsReport getReport2() {
        return super.getReport2(this);
    }
    
    public String ntvSubmit() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return ntvSubmitInternal(ReportReceivedStatusDef.SUBMITTED);
        } finally {
            clearButtonClicked();
        }
    }
    
    public String ntvSubmitCaution() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
        try {
            return ntvSubmitInternal(ReportReceivedStatusDef.SUBMITTED_CAUTION);
        } finally {
            clearButtonClicked();
        }
    }
    
    public final void toStateRR() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            toStateRRInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void toStateRRInternal() {
        boolean ok = true;
        try {
            getEmissionsReportService().modifyEmissionsReportState(ntvReport,
                    ReportReceivedStatusDef.REVISION_REQUESTED);
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
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
    
    public final void toStateSC() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            toStateSCInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void toStateSCInternal() {
        boolean ok = true;
        try {
            getEmissionsReportService().modifyEmissionsReportState(ntvReport,
                    ReportReceivedStatusDef.SUBMITTED_CAUTION);
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ok = false;
        }
        if (ok) {
            String stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                    ReportReceivedStatusDef.SUBMITTED_CAUTION);
            DisplayUtil.displayInfo("Submitted report state changed to " + stateDesc);
        } else {
            DisplayUtil.displayError("Failed to change the state of the report");
        }
    }
    
    public final void toStateSS() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            toStateSSInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void toStateSSInternal() {
        boolean ok = true;
        try {
            getEmissionsReportService().modifyEmissionsReportState(ntvReport,
                    ReportReceivedStatusDef.SUBMITTED);
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ok = false;
        }
        if (ok) {
            String stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                    ReportReceivedStatusDef.SUBMITTED);
            DisplayUtil.displayInfo("Submitted report state changed to " + stateDesc);
        } else {
            DisplayUtil.displayError("Failed to change the state of the report");
        }
    }
    
    public final String approveConfirmAA() {
        approveNeedsRevision = false;
        return "dialog:confirmApprovePopup";
    }
    
    public final String approveConfirmAR() {
        approveNeedsRevision = true;
        return "dialog:confirmApprovePopup";
    }
    
    public final void approveCancel() {
        FacesUtil.returnFromDialogAndRefresh();
        return;
    }
    
    public final void confirmedApprove() {
        if(approveNeedsRevision) {
            toStateAR();
        } else {
            toStateAA();
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void toStateAR() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            toStateARInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private final void toStateARInternal() {
        if (!ntvReport.getPrimary().isApproved()) {
            if(revisedReportNotProcessedMsg != null) {
                DisplayUtil.displayError(revisedReportNotProcessedMsg);
            } else {
                ntvDolaaApprove(ReportReceivedStatusDef.DOLAA_APPROVED_RR);
            }
            return;
        }
        
        boolean ok = true;
        try {
            getEmissionsReportService().modifyEmissionsReportState(ntvReport,
                    ReportReceivedStatusDef.DOLAA_APPROVED_RR);
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ok = false;
        }
        if (ok) {
            String stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                    ReportReceivedStatusDef.DOLAA_APPROVED_RR);
            DisplayUtil
                    .displayInfo("Approved report state changed to " +  stateDesc);
        } else {
            DisplayUtil
                    .displayError("Failed to change the state of the report");
        }
    }
    
    public final void toStateAA() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            toStateAAInternal();
        } finally {
            clearButtonClicked();
        }
    }

    private final void toStateAAInternal() {
        if (!ntvReport.getPrimary().isApproved()) {
            if(revisedReportNotProcessedMsg != null) {
                DisplayUtil.displayError(revisedReportNotProcessedMsg);
            } else {
                ntvDolaaApprove(ReportReceivedStatusDef.DOLAA_APPROVED);
            }
            return;
        }
        boolean ok = true;
        try {
            getEmissionsReportService().modifyEmissionsReportState(ntvReport,
                    ReportReceivedStatusDef.DOLAA_APPROVED);
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
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
        return "dialog:confirmNtvNotNeeded";
    }
    
    public final void toStateNN() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            toStateNNInternal();
        } finally {
            clearButtonClicked();
        }
        FacesUtil.returnFromDialogAndRefresh();
    }

    private
    final void toStateNNInternal() {
        boolean ok = true;
        if(revisedReportNotProcessedMsg != null) {
            DisplayUtil.displayError(revisedReportNotProcessedMsg);
            return;
        }
        try {
        	getEmissionsReportService().cancelEmissionsReport(ntvReport.getPrimary().getEmissionsRptId());        	
        } catch (RemoteException re) {
            handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
            ok = false;
        }
        if (ok) {
            String stateDesc;
            if(ReportReceivedStatusDef.isSubmittedCode(ntvReport.getPrimary().getRptReceivedStatusCd())) {
                stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                        ReportReceivedStatusDef.REPORT_NOT_NEEDED);
                DisplayUtil.displayInfo("Submitted report state changed to " + stateDesc);
            } else {
                stateDesc = ReportReceivedStatusDef.getData().getItems().getItemDesc(
                        ReportReceivedStatusDef.APPROVED_REPORT_NOT_NEEDED);
                DisplayUtil.displayInfo("Approved report state changed to " + stateDesc);
            }
            // reread the report
            ntvReport = null;
        } else {
            DisplayUtil.displayError("Failed to change the state of the report");
        }
    }

    public final void deleteNtvReport() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            deleteNtvReportInternal();
        } finally {
            clearButtonClicked();
        }
    }
    
    private void deleteNtvReportInternal() {
        boolean ok = deleteNtvReportI();
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
    }
    
    private final void ntvDolaaApprove(String state) {
        boolean ok = internalNtvValidate(true);
        Integer conflictRpt = null;
        boolean ok2 = true;
        try {
            if(ntvReport.getReport1() != null) {
                conflictRpt = getEmissionsReportService().conflictingReportForApproval(facility.getFacilityId(),
                        ntvReport.getReport1());
            }
        } catch(RemoteException re) {
            handleException(reportId, re);
            ok2 = false;
        }
        if(ok2) {
            if(conflictRpt != null) {
                DisplayUtil.displayError(conflictMsg1 + conflictRpt.intValue() + conflictMsg2);
                ok = false;
            }
            // Do second report regardless of conflict with the first
            if(ntvReport.getReport2() != null) {
                try {
                    conflictRpt = getEmissionsReportService().conflictingReportForApproval(facility.getFacilityId(),
                            ntvReport.getReport2());
                } catch(RemoteException re) {
                    handleException(reportId, re);
                    ok2 = false;
                }
                if(ok2) {
                    if(conflictRpt != null) {
                        DisplayUtil.displayError(conflictMsg1 + conflictRpt.intValue() + conflictMsg2);
                        ok = false;
                    }
                }
            }
        }
        if(ok) {
            try {
                ok = getEmissionsReportService().ntvDolaaApprove1(
                        ntvReport, facility, InfrastructureDefs.getCurrentUserId());
                if(ok) {
                    ntvReport = getEmissionsReportService().ntvDolaaApprove2(
                            ntvReport, facility, state, InfrastructureDefs.getCurrentUserId(),
                            isExistingPurchaseOwner());
                    if(ntvReport.getPrimary().getBillingContactFailureMsg() != null) {
                        DisplayUtil.displayError(ntvReport.getPrimary().getBillingContactFailureMsg());
                        // report remains unchanged
                        return;
                    }
                    if(ntvReport.getShutdownToDoData() != null &&
                            ntvReport.getShutdownToDoData().size() > 0) {
                        getFacilityService().performShutdownToDo(facility,
                                InfrastructureDefs.getCurrentUserId(),
                                ntvReport.getShutdownToDoData());
                    }
                }
            } catch (DAOException re) {
                handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
                ok = false;
            } catch (RemoteException re) {
                handleException(ntvReport.getPrimary().getEmissionsRptId(), re);
                ok = false;
            }
        }
        if (ok) {
            DisplayUtil.displayInfo("DO/LAA Approve successful");
        } else {
            err = true;
            DisplayUtil.displayError("Approve failed");
        }
        ntvReport = null; // trigger to re-read/reprocess report
        getReport1();
        return;
    }
    
    
	
	
}
