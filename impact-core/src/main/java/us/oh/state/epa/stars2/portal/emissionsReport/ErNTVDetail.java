package us.oh.state.epa.stars2.portal.emissionsReport;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.reports.ErNTVBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;

@SuppressWarnings("serial")
public class ErNTVDetail extends ErNTVBase {

    public ErNTVDetail() {
        super();
    }
    
    public final EmissionsReport getReport1() {
        return super.getReport1(this);
    }
    
    public final EmissionsReport getReport2() {
        return super.getReport2(this);
    }
    
    public void deleteNtvReport() {
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
            ReportSearch reportSearch = (ReportSearch) FacesUtil.getManagedBean("reportSearch");
            reportSearch.setPopupRedirectOutcome("ERSearch");
            DisplayUtil.displayInfo(
                "Emissions Inventory successfully deleted");
        } else {
            DisplayUtil.displayError(
            "Failed to delete Emissions Inventory");
        }
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final String ntvSubmit() {
    	String ret = null;
    	MyTasks mt = (MyTasks) FacesUtil.getManagedBean("myTasks");
    	createRTask.setNtvReport(ntvReport);
    	SubmitTask submitTask = (SubmitTask) FacesUtil.getManagedBean("submitTask");
    	submitTask.setTitle("Submit Emissions Inventory");
    	submitTask.setNonROSubmission(!mt.isHasSubmit());
    	submitTask.setRoSubmissionRequired(true);
    	submitTask.setDapcAttestationRequired(true);
    	ret = submitTask.confirm();  // if it succeeds, go back to home page
    	return ret;
    }
    
    public final String getAttestationDocURL() {
        String url = null;
        EmissionsDocument doc;
		try {
			doc = getEmissionsReportService().createEmissionsAttestationDocument(ntvReport.getPrimary(), facility);
	        if (doc != null) {
	            url = doc.getDocURL();
	        } else {
	            // this should never happen
	            logger.error("Error creating attestation document for emissions inventory: " + report.getEmissionsRptId());
	        }
		} catch (RemoteException e) {
			handleException(e);
		}
        return url; // stay on same page
    }
}
