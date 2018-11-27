package us.oh.state.epa.stars2.app.facility;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.List;

import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.component.UIXTable;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRUM;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class UndeliveredMail  extends TaskBase  {

	private static final long serialVersionUID = 7865382287487958153L;

    private FacilityRUM facilityRUM;
    private boolean rumModify;
    private boolean rumFromFacility;
    private boolean editable;
    private Facility facility;
    private boolean staging;
    private boolean workflowEnabled=false;
    
	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    public boolean isWorkflowEnabled() {
        return workflowEnabled;
    }

    public void setWorkflowEnabled(boolean workflowEnabled) {
        this.workflowEnabled = workflowEnabled;
    }

    public UndeliveredMail() {
        super();
    }
    
    public final boolean getEditable() {
        return editable;
    }

    public final void setEditable(boolean editable) {
        this.editable = editable;
    }

    public final String startAddRUM() {
        setEditable(true);
        rumModify = false;
        facilityRUM = new FacilityRUM();
        facilityRUM.setFacilityId(facility.getFacilityId());
        facilityRUM.setUserId(InfrastructureDefs.getCurrentUserId());
        facilityRUM.setDisposition("und");
        facilityRUM.setNewRecord(true);
        facilityRUM
                .setOriginalMailDt(new Timestamp(System.currentTimeMillis()));
        facilityRUM
                .setCreatedDt(new Timestamp(System.currentTimeMillis()));
        return "dialog:rumDetail";
    }
    
    /* START CODE: facility RUM */

    public final String startViewRUM() {
        rumModify = false;
        return "dialog:rumDetail";
    }

    public final String startEditRUM(ActionEvent actionEvent) {
        setEditable(true);
        rumModify = true;
        return "dialog:rumDetail";
    }
    
    public final String startEditWFRUM(ActionEvent actionEvent) {
        setEditable(true);
        rumModify = true;
        return "rumWFDetail";
    }

    public final void applyEditRUM(ActionEvent actionEvent) {
        boolean operationOK = true;
        /*
         * 
         * ValidationMessage[] validationMessages = undeliveredMail.validate();
         * 
         * if (validationMessages.length > 0) { if
         * (displayValidationMessages("", validationMessages) == true) { return; } }
         */
        try {
            if (!rumModify) {
                getFacilityService().createFacilityRUM(facilityRUM);
            } else {
                // edit
                getFacilityService().modifyFacilityRUM(facilityRUM);
            }
        } catch (RemoteException re) {
        	handleException(re);
            operationOK = false;
        }

        if (operationOK) {
            if (rumModify) {
                DisplayUtil.displayInfo("Record updated successfully");
            } else {
                DisplayUtil.displayInfo("Record added successfully");
            }
        } else {
            cancelEditRUM();
            if (rumModify) {
                DisplayUtil
                        .displayError("Update failed: Unable to update the record");
            } else {
                DisplayUtil.displayError("Insert failed: Unable to add record");
            }
        }

        rumModify = false;
        setEditable(false);
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void applyEditWFRUM(ActionEvent actionEvent) {
        boolean operationOK = true;
        /*
         * 
         * ValidationMessage[] validationMessages = undeliveredMail.validate();
         * 
         * if (validationMessages.length > 0) { if
         * (displayValidationMessages("", validationMessages) == true) { return; } }
         */
        try {
          getFacilityService().modifyFacilityRUM(facilityRUM);
        } catch (RemoteException re) {
        	handleException(re);
            operationOK = false;
        }
        if (operationOK) {
            if (rumModify) {
                DisplayUtil.displayInfo("Record updated successfully");
            } else {
                DisplayUtil.displayInfo("Record added successfully");
            }
        } else {
            cancelEditWFRUM();
            if (rumModify) {
                DisplayUtil
                        .displayError("Update failed: Unable to update the record");
            } else {
                DisplayUtil.displayError("Insert failed: Unable to add record");
            }
        }

        rumModify = false;
        setEditable(false);
    }

    public final void cancelEditRUM() {
        facilityRUM = null;
        rumModify = false;
        setEditable(false);
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final void cancelEditWFRUM() {
        //we don't reset the object to null here since they may go back into edit mode
        rumModify = false;
        setEditable(false);
      //  FacesUtil.returnFromDialogAndRefresh();
    }

    public final FacilityRUM getFacilityRUM() {
        return facilityRUM;
    }

    public final FacilityRUM[] getFacilityRUMs() {
        FacilityRUM rum[] = new FacilityRUM[0];
        try {
            rum = getFacilityService().retrieveFacilityRUMs(facility.getFacilityId());
        } catch (Exception re) {
        	handleException(new RemoteException(re.getMessage()));
            DisplayUtil.displayError("Accessing facility RUM's failed");
        }
        return rum;
    }

    public final void setFacilityRUM(FacilityRUM facilityRUM) {
        this.facilityRUM = facilityRUM;
    }

    @Override
    public final void doSelected(UIXTable table) {
      
    }

    @Override
    public final String findOutcome(String url, String ret) {
       return "rumWFDetail";
    }

    @Override
    public final String getDoSelectedButtonText() {
        return "Finalize Selected";
    }

    @Override
    public final Integer getExternalId() {
        return facilityRUM.getRumId();
    }

    @Override
    public String getExternalNum() {
		return facilityRUM.getRumId().toString();
	}
    
    @Override
    public final boolean isDoSelectedButton(ProcessActivity activity) {
       
        return false;
    }

    @Override
    public final void setExternalId(Integer externalId) {
        try {
            facilityRUM= getFacilityService().retrieveFacilityRUM(externalId);
            facility = getFacilityService().retrieveFacility(facilityRUM.getFacilityId());
            this.setWorkflowEnabled(true);
            ((us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem)FacesUtil.getManagedBean("menuItem_undeliveredMail")).setDisabled(false);
        } catch (RemoteException re) {
        	handleException(re);
        }
    }

    @Override
    public final List<ValidationMessage> validate(Integer inActivityTemplateId) {
       
        return null;
    }

    public String toExternal() {
    	return "rumWFDetail";
	}
    
    public final Facility getFacility() {
        return facility;
    }

    public final void setFacility(Facility f) {
        this.facility = f;
    }
    
    public final void dialogDone() {
        return;
    }
    
    public final boolean isDisabledUpdateButton() {
        // System.out.println("Call to disabled update button");
        boolean ret = false;
        
        try {
        if (isPublicApp()) {
        	return true;
        }
        if(isReadOnlyUser()){
           	return true;
        }	
        if (!isInternalApp() && !staging) {
            ret = true;
        }

        if (facility != null && (facility.getVersionId() == -1 || facility.isCopyOnChange())) {
            ret = false;
        }
        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }
    
    public final boolean isStaging() {
        return staging;
    }

    public final void setStaging(boolean staging) {
        this.staging = staging;
    }

    public final boolean isRumFromFacility() {
        return rumFromFacility;
    }

    public final void setRumFromFacility(boolean rumFromFacility) {
        this.rumFromFacility = rumFromFacility;
    }

    public final boolean isRumModify() {
        return rumModify;
    }

    public final void setRumModify(boolean rumModify) {
        this.rumModify = rumModify;
    }
}
