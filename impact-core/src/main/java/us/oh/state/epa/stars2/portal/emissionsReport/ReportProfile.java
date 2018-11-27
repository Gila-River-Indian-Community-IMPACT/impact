package us.oh.state.epa.stars2.portal.emissionsReport;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;

@SuppressWarnings("serial")
public class ReportProfile extends ReportProfileBase {
	
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
    
    public final String submitReport() {
    	String ret = null;
    	MyTasks mt = (MyTasks) FacesUtil.getManagedBean("myTasks");
    	createRTask.setReport(report);
    	createRTask.setFacility(facility);
    	SubmitTask submitTask = (SubmitTask) FacesUtil.getManagedBean("submitTask");
    	submitTask.setRoSubmissionRequired(true);
    	submitTask.setDapcAttestationRequired(true);
    	submitTask.setNonROSubmission(!mt.isHasSubmit());
    	submitTask.setTitle("Submit Emissions Inventory");
    	submitTask.setDocuments(getReportDocuments());
    	ret = submitTask.confirm();  // if it succeeds, go back to home page
    	return ret;
    }
    
    public final void startCreateReportDone() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            String ret = startCreateReportDoneI();
            if(null != ret) {
                MyTasks mt = (MyTasks) FacesUtil.getManagedBean("myTasks");
                mt.setPageRedirect(ret);
                if(createRTask != null) {
                    mt.renderEmissionReportMenu(createRTask);
                    mt.refreshTasks(createRTask.getFacility().getFacilityId());
                }
				((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_TVDetail"))
						.setDisabled(false);
            }
            FacesUtil.returnFromDialogAndRefresh();
        } finally {
            clearButtonClicked();
        }
    }

    public final void startReportCopy() {
        String ret = startReportCopyI();
        if(null != ret) {
            MyTasks mt = (MyTasks) FacesUtil
            .getManagedBean("myTasks");
            mt.setPageRedirect(ret);
            if(createRTask != null) {
                mt.renderEmissionReportMenu(createRTask);
                mt.refreshTasks(createRTask.getFacility().getFacilityId()); 
            }
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_TVDetail"))
					.setDisabled(false);
        }
    }
    
    // Do no notes processing.
    protected void processNotes(EmissionsReport rpt) {}
    
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
        /*  List of fpIds displayed is the list from the AQD database
         * for all profiles under this facility id.
         * The one currently assigned to is unavailable.
         * 
         * Special cases:
         * > Current could be selected but it may not
         *   be currrent in Staging.  In this case use the current
         *   from staging unless it was already the one the report
         *   is associated with.
         * > An fpId may be selected this is current in staging
         *   but not current in the AQD database.
         *   
         *   All cases:
         *   1) AQD current selected and report not tied to Staging current
         *      (note that the two fpIds may be different or the same)
         *   2) Staging current selected
         *   3) Some other fpId selected 
         */
        String giveMsg = null;
        boolean ok = true;
        boolean bypass = false;
        try {
            MyTasks mt = (MyTasks) FacesUtil
            .getManagedBean("myTasks");
            String loginName = mt.getLoginName();
            Integer corePlaceId = mt.getCorePlaceId();
            mt.refreshTasks(facilityId);
            // Relocate task for report
            Integer prevFpId = report.getFpId();
            createRTask = mt.findReportTask(report.getEmissionsRptId());
            if(createRTask == null) {
                DisplayUtil.displayError("System error. Please contact system administrator");
                logger.error("Failed to find Task object for Emissions Inventory " + report.getEmissionsRptId());
                ok = false;
            }
            Task oldFTask = null;
            if(ok) {
                // Find current profile task
                oldFTask = mt.findFacilityTask(prevFpId);
                if(oldFTask == null) {
                    // Should have found the task.
                    String s = "Failed to locate Facility Inventory in MyTasks";
                    DisplayUtil.displayError(s);
                    logger.error(s);
                    ok = false;
                }
            }
            // Find current in Staging
            Task stagingCurr = mt.findCurrentFacilityTask();
            //Are we switching to current in staging
            if(stagingCurr != null) {
                if(fpId.equals(stagingCurr.getFpId()) // Case 2
                        || versionId.equals(-1) // case 1
                ) {
                    if(versionId.equals(-1) && prevFpId.equals(stagingCurr.getFpId())) {
                        //  Already tied to a current and cannot switch.
                        giveMsg = "Inventory already associated with Current Profile in Staging (" +
                        stagingCurr.getFpId() + ").  Cannot select more recent Current from the Read Only database (" +
                        fpId + ").  No changes made.";
                        bypass = true;
                    } else {
                        giveMsg = "Report successfully associated with facility inventory " +
                        stagingCurr.getFpId() + "--which is the current profile in Staging.";
                    }
                    //  Change variables to assign to staging current
                    versionId = -1;
                    fpId = stagingCurr.getFpId();
                }
            }
            if(ok && !bypass) {
                // Try to locate task for replacement profile
                Task replaceFTask = mt.findFacilityTask(fpId);
                Task newFTask = null;
                if(replaceFTask == null) {
                    // replacement not in staging yet
                    if(versionId.equals(-1)) { // versionId set from associateProfile.jsp page
                        newFTask = MyTasks.createNewTask("Facility Inventory Change", Task.TaskType.FC,
                                true, null, facility.getFacilityId(), fpId, "current", corePlaceId, loginName);
                    } else {
                        newFTask = MyTasks.createNewTask("Historic Facility Inventory " + fpId, Task.TaskType.FCH,
                                true, null, facility.getFacilityId(), fpId, "current", corePlaceId, loginName);
                    }
                    newFTask.setFacility(facility); // so facility name will appear with task description
                    newFTask.setTaskInternalId(fpId);
                    newFTask.setReferenceCount(1);
                }
                // Let BO do all the heavy lifting
                getEmissionsReportService().changeProfile(report, facility, createRTask, oldFTask, replaceFTask, newFTask);
                mt.refreshTasks(facility.getFacilityId());
            }
        }  catch (RemoteException re) {
            handleException(report.getEmissionsRptId(), re);
            ok = false;
        }
        if(ok && !bypass) {
            fpId = createRTask.getFpId();
            try {
                facility = getFacilityProfile();
                if(facility == null) {
                    DisplayUtil.displayError("Failed to read replacement profile " + fpId);
                    logger.error("Failed to read replacement profile " + fpId);
                    ok = false;
                }
            }  catch (RemoteException re) {
                handleException(report.getEmissionsRptId(), re);
                logger.error("Failed to read facility " + fpId + " staging = " + inStaging, re);
                ok = false;
            }
        }
        if(ok && !bypass) {
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
                handleException(report.getEmissionsRptId(), re);
                ok = false;
            }
        }
        if (ok) {
            if(giveMsg == null) {
            DisplayUtil.displayInfo(
                    "Emissions Inventory successfully associated with facility inventory " +
                    fpId);
            } else {
                DisplayUtil.displayWarning(giveMsg);
            }
        } else {
            DisplayUtil.displayError("Failed to change association");
            err = true;
        }
		FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final String getAttestationDocURL() {
        String url = null;
        EmissionsDocument doc;
		try {
			doc = getEmissionsReportService().createEmissionsAttestationDocument(report, facility);
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
