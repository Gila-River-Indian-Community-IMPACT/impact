package us.oh.state.epa.stars2.portal.emissionsReport;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.oh.state.epa.stars2.webcommon.reports.ErNTVBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;

public class ReportDetail  extends ValidationBase {
	private Task task;
    private Integer reportId;
    private EmissionsReport emissionsReportRow;

    private ReportProfile tvProfile = null;
    private ErNTVDetail ntvProfile = null;

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
    
    public boolean getFromTODOList() {
        // Stub needed on portal side since jsp calls it.
        return false;
    }
    
    // This function invoked from .jsp, so stub funtion for it to work.
    public void setFromTODOList(boolean fromTODOList) {}
    
    public final void getReport() {
        if (emissionsReportRow == null
                || (!reportId.equals(emissionsReportRow.getEmissionsRptId()))) {
            try {
                emissionsReportRow = getEmissionsReportService().retrieveEmissionsReportRow(
                        reportId, false);
                if(emissionsReportRow == null) {
                    DisplayUtil.displayError("Report not found");
                }
            } catch (RemoteException re) {
                DisplayUtil.displayError("Failed to read report");
                logger.error(re.getMessage(), re);
            }
        }
    }

    public final Integer getReportId() {
        return reportId;
    }

    public final void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    // This called to bring up report showing in the portal tasks table
    // This is to read the report--this is not submitting the report.
    public final String portalSubmit(Task task) {
        try {
            EmissionsReport rpt = getEmissionsReportService().retrieveEmissionsReportRow(reportId, true);
            if(rpt != null) {
                return internalSubmit(task, false, true);
            }
            DisplayUtil.displayError("Report not found");
            return null;
        } catch (RemoteException re) {
            DisplayUtil.displayError("System Error");
            logger.error(re.getMessage(), re);
            return null;
        }
    }
    
    // This called from report found in a report search result table
    public final String submit() {
        return internalSubmit(null, false, false);
    }
    
    private final String internalSubmit(Task task, boolean isNTV, boolean staging) {
        String ret = null;
        if (isNTV) {
            ErNTVBase rProfileNTV = (ErNTVBase) FacesUtil
            .getManagedBean("erNTVDetail");
            rProfileNTV.setReportId(reportId);
            rProfileNTV.setNtvReport(null);
            rProfileNTV.setInStaging(staging);
            setNtvReportFlag(true);
            rProfileNTV.setCreateRTask(task);
            rProfileNTV.getReport1(rProfileNTV); // read info first
            // then navagate to page
            if(!rProfileNTV.isErr()) {
                if(!staging && isPortalApp()) {
                        ((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_emissionsReport")).setRendered(false);
                }
                ret = "emissionReport";
            }
        } else {
            ReportProfileBase reportProfile = (ReportProfileBase) FacesUtil
            .getManagedBean("reportProfile");
            reportProfile.setReportId(reportId);
            reportProfile.setReport(null);
            reportProfile.setInStaging(staging);
            setNtvReportFlag(false);
            reportProfile.setCreateRTask(task);
            reportProfile.getReport(reportProfile); // read info first
            if(!reportProfile.isErr()) {
                if(!staging && isPortalApp()) {
                    ((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_emissionsReport")).setRendered(false);
                }
                ret = "emissionReport";
            }
        }
		if (isPortalApp()) {
			if (!staging) {
				ret = "home.reports.reportDetail";
				((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_TVDetail")).setDisabled(true);
			} else {
				((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_TVDetail")).setDisabled(false);
			}
		} else if (isPublicApp()){
			ret = "home.reports.reportDetail";
		}
        
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
                        reportId, true);  // This is Staging database
                if(emissionsReportRow == null) {
                    DisplayUtil.displayError("Report not found");
                } else {
                        ReportProfileBase reportProfile = (ReportProfileBase) FacesUtil
                        .getManagedBean("reportProfile");
                        reportProfile.setReport(null);
                        reportProfile.getReport(reportProfile); // read info first;
                    return "emissionReport";
                }
            } catch (RemoteException re) {
                DisplayUtil.displayError("Failed to read report");
                logger.error(re.getMessage(), re);
            }
        }
        return null;
    }

//  For workflow

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
    // End For Workflow

    public final String validationDlgAction() {
    	String ret = null;
    	
        if(isNtvReport()) {
            ret = getNtvProfile().validationDlgAction();
        }
        
        ret = getTvProfile().validationDlgAction();
		if (ret != null) {
			if (ret.equals(getTvProfile().TV_REPORT)) {
				MyTasks mt = (MyTasks) FacesUtil.getManagedBean("myTasks");
				if (isPortalApp() && getTvProfile().isInStaging() && mt != null) {
					Task tmpTask = mt.findReportTask(this.reportId);
					if (tmpTask != null) {
						mt.setTaskId(tmpTask.getTaskId());
						mt.submitTask(true);
					}
				}
			}
		}
		        
        return ret;
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


	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}
