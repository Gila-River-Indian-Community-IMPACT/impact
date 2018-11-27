package us.oh.state.epa.stars2.portal.application;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.portal.facility.FacilityProfile;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.portal.relocation.Relocation;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.application.ApplicationDetailCommon;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;

@SuppressWarnings("serial")
public class ApplicationDetail extends ApplicationDetailCommon {
    private Task task;
    private MyTasks myTasks;

    public final Task getTask() {
        return task;
    }

    public final void setTask(Task task) {
        this.task = task;
    }
    
    public final String applySubmitFromPortal() {
        String ret = null;
        try {
        	if (null != application) {
    			reloadApplicationWithAllEUs();
    		}
        	
			if (getApplicationService().checkApplicationExistsInReadOnlySchema(
					application.getApplicationID())) {
				return "dialog:duplicateApplicationSubmission";
			}
    		
            // load document data into application
            // this needs to be done so Document data can be sent
            // to the internal application
            getApplicationService().loadAllDocuments(application, false);

            // don't want duplicate attachments
            task.getAttachments().clear();
            task.setApplication(application);

            // need to retrieve current facility information because
            // changes to the facility made while correcting validation errors
            // may not be reflected in the copy of the facility stored in the
            // application
            getApplicationService().synchAppWithCurrentFacilityProfile(application);
            
            // need to retrieve facility by fp_id to get all data needed
            task.setFacility(getFacilityService().retrieveFacilityProfile(application.getFacility().getFpId(), true));
            
            MyTasks myTasks = (MyTasks)FacesUtil.getManagedBean("myTasks");
            SubmitTask submitTask = (SubmitTask) FacesUtil.getManagedBean("submitTask");
            submitTask.setDapcAttestationRequired(true);
            submitTask.setRoSubmissionRequired(true);
            submitTask.setNonROSubmission(!myTasks.isHasSubmit());
            submitTask.setTitle("Submit Application");
            
            preparePrintableDocumentList(true);
            submitTask.setDocuments(getPrintableDocumentList());
            ret = submitTask.confirm();

        } catch (RemoteException re) {
            handleException(re);
        }
        return ret;
    }
    
    // method called by "Application Detail" tab to refresh data
    public final String refresh() {
        reload();
        return "applications";
    }
    
    protected void loadApplicationData() {
        try {
            if (!readOnly && !isRelocationClass()) {
                // make sure changes to the facility inventory are picked up automatically
                // (don't do this for read only applications since it changes the data)
                getApplicationService().synchGatewayAppWithCurrentFacilityProfile(application);
            }
        } catch (RemoteException e) {
            handleException(e);
        }
        super.loadApplicationData();
		if (isPortalApp()) {
			if (myTasks == null) {
				myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
			}
			myTasks.setPageRedirect(null);

			// clear out my tasks to be sure we don't hold on to a stale
			// reference
			myTasks = null;
		}
    }

    public final MyTasks getMyTasks() {
    	if (myTasks == null) {
			myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
		}
        return myTasks;
    }

    public final void setMyTasks(MyTasks myTasks) {
        this.myTasks = myTasks;
    }

    protected void setRelocation() {
    	Relocation gi = (Relocation)FacesUtil.getManagedBean("relocation");
    	gi.setStaging(staging);
        gi.setRelocateRequest((RelocateRequest)application);
    }
    
	public String validationDlgAction() {
		String ret = super.validationDlgAction();
		if (ret != null) {
			if (ret.equals(APP_DETAIL_MANAGED_BEAN)) {
				if (isPortalApp() && staging && getMyTasks() != null) {
					Task tmpTask = getMyTasks().findApplicationTask(this.application.getApplicationID());
					if (tmpTask != null) {
						getMyTasks().setTaskId(tmpTask.getTaskId());
						getMyTasks().submitTask(true);
					}
				}
			}
		}
		return ret;
	}
	
	public String showFacilityProfile() {
		String ret = FacilityProfileBase.FAC_OUTCOME;
		FacilityProfile facProfile = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		if (isPublicApp()) {
			boolean staging = false;
			facProfile.initFacilityProfile(application.getFacility().getFpId(),
					staging);
		} else {
			facProfile.initFacilityProfile(application.getFacility().getFpId(),
				!isInternalApp() && !readOnly);
		}

		if (isPublicApp()) {
			ret = FacilityProfileBase.HOME_FAC_OUTCOME;
		} else if (isPortalApp()) {
			if (!facProfile.isStaging()) {
				ret = FacilityProfileBase.HOME_FAC_OUTCOME;
			} else {
				if (facProfile.isEditStaging() && getMyTasks() != null) {
					Task tmpTask = getMyTasks().findFacilityTask(
							this.application.getFacility().getFpId());
					if (tmpTask != null) {
						getMyTasks().setTaskId(tmpTask.getTaskId());
						getMyTasks().submitTask(false);
					}
				}
			}
		}
		return ret;
	}
}
