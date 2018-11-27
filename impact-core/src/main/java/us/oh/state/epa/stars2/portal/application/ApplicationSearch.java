package us.oh.state.epa.stars2.portal.application;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.context.AdfFacesContext;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.portal.relocation.Relocation;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.application.ApplicationSearchCommon;

public class ApplicationSearch extends ApplicationSearchCommon {
    private ApplicationSearchResult selectedApp;
    protected MyTasks myTasks;
    
    public final String startCopyApplication() {
        return "dialog:copyApplication";
    }
    
    protected MyTasks getMyTasks() {
    	if (myTasks == null) {
    		myTasks = (MyTasks) FacesUtil.getManagedBean("myTasks");
    	}
    	return myTasks;
    }
    
    public final void createNewApplication(ActionEvent event) {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            createNewApplicationInternal(event);
        } finally {
            clearButtonClicked();
        }
    }
    
    protected final void createNewApplicationInternal(ActionEvent event) {
        if (newApplicationFacilityID != null
                && newApplicationFacilityID.length() > 0) {

            MyTasks myTasks = getMyTasks();
            
            if (newApplication != null) {
                if (correctedApplication) {
                    // correct/amend existing application
                    if (previousApplicationNumber == null) {
                        DisplayUtil
                        .displayWarning("Please provide the application number of the application being corrected/amended");
                    } else if (!amendedApplication && correctedReason == null) {
                        DisplayUtil
                        .displayWarning("Please provide a reason why the application is being corrected.");
                    } else {
                        try {
                            Application appToCopy = getApplicationService().retrieveApplicationWithAllEUs(previousApplicationNumber);
                            if (appToCopy != null) {
                                appToCopy.setApplicationCorrected(correctedApplication);
                                appToCopy.setApplicationCorrectedReason(correctedReason);
                                myTasks.setPageRedirect(myTasks.copyApplication(appToCopy));
                                FacesUtil.returnFromDialogAndRefresh();
                                resetNewApplication();
                            } else {
                                DisplayUtil.displayError("Failed copying application. " +
                                        "No application exists with application number " + 
                                        previousApplicationNumber);
                            }
                        } catch (RemoteException e) {
                            handleException(e);
                        }
                    }
                } else if (clonedApplication) {
                    if (previousApplicationNumber == null) {
                        DisplayUtil
                        .displayWarning("Please provide the application number of the application being copied");
                    } else {
                        try {
                        	Application appToCopy = getApplicationService().retrieveApplicationWithAllEUs(previousApplicationNumber);
                            appToCopy.setApplicationCorrected(false);
                            appToCopy.setApplicationCorrectedReason(null);
                            myTasks.setPageRedirect(myTasks.copyApplication(appToCopy));
                            FacesUtil.returnFromDialogAndRefresh();
                            resetNewApplication();
                        } catch (RemoteException e) {
                            handleException(e);
                        }
                    }
                } else {
                    // create new application
                    Facility facility;
                    boolean ok = true;

                    try {
                        facility = ServiceFactory.getInstance()
                        .getFacilityService().retrieveFacility(
                                newApplicationFacilityID);
                        newApplication.setFacility(facility);

                        if (newApplication instanceof PBRNotification) {
                            if (pbrTypeCd == null) {
                                DisplayUtil.displayWarning("Please select a PBR type");
                                ok = false;
                            } else {
                                ((PBRNotification)newApplication).setPbrTypeCd(pbrTypeCd);
                            }
                        } else if (newApplication instanceof RPRRequest){
                            if (permitId == null) {
                                DisplayUtil.displayWarning("Please select a permit number");
                                ok = false;
                            } else {
                                ((RPRRequest)newApplication).setPermitId(permitId);
                            }
                        } else if (newApplication instanceof RPERequest){
                            if (permitId == null) {
                                DisplayUtil.displayWarning("Please select a permit number");
                                ok = false;
                            } else {
                                ((RPERequest)newApplication).setPermitId(permitId);
                                Permit permit = getPermitService().retrievePermit(permitId).getPermit();
                                GregorianCalendar tdgc = new GregorianCalendar();
                                tdgc.setTime(permit.getEffectiveDate());
                                tdgc.add(Calendar.MONTH, 30);
                                Timestamp td = new Timestamp(tdgc.getTimeInMillis());
                                ((RPERequest)newApplication).setTerminationDate(td);
                            }
                        } else if (newApplication instanceof RPCRequest) {
                            if (rpcTypeCd == null) {
                                DisplayUtil.displayWarning("Please select a modification type");
                                ok = false;
                            }
                            if (permitId == null) {
                                DisplayUtil.displayWarning("Please select a permit number");
                                ok = false;
                            }
                            if (ok) {
                                ((RPCRequest)newApplication).setRpcTypeCd(rpcTypeCd);
                                ((RPCRequest)newApplication).setPermitId(permitId);
                            }
                        }
                        if (ok) {
                            String redirectTo = myTasks.createApplication(newApplication);
                            myTasks.setPageRedirect(redirectTo);
                            FacesUtil.returnFromDialogAndRefresh();
                            resetNewApplication();
                        }
                    } catch (Exception ex) {
                        // UI error is displayed by myTasks.createApplication()
//                        DisplayUtil.displayError("Failed creating request");
                        logger.error(ex.getMessage(), ex);
                        ok = false;
                    }
                }
            } else {
                DisplayUtil.displayWarning("Please select a request type");
            }
        } else {
            DisplayUtil.displayWarning("Please enter a facility ID");
        }
    }

    public final void createApplicationCopy(ActionEvent actionEvent) {
        boolean ok = true;
        MyTasks myTasks = getMyTasks();
        if (isCorrectedApplication() && getCorrectedReason() == null) {
            DisplayUtil
            .displayWarning("Please provide a reason why the application is being corrected.");
            ok = false;
        }
        if (ok) {
            try {
                if (selectedApp == null) {
                    throw new RemoteException("Application error: selectedApp is null");
                }
                // retrieve all data for application since data in selectedApp
                // only contains what's needed for display in the search table
                Application appToCopy = getApplicationService().retrieveApplicationWithAllEUs(selectedApp.getApplicationNumber());
                if (appToCopy != null) {
                    appToCopy.setApplicationCorrected(isCorrectedApplication());
                    appToCopy.setApplicationCorrectedReason(getCorrectedReason());
                    appToCopy.setApplicationAmended(isAmendedApplication());
                    popupRedirectOutcome = myTasks.copyApplication(appToCopy);
                    FacesUtil.returnFromDialogAndRefresh();
                } else {
                    DisplayUtil.displayError("Failed copying application. " +
                            "No application exists with application number " + 
                            selectedApp.getApplicationNumber());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                DisplayUtil.displayError("Failed creating application copy.");
            }
        }
    }

    public final void cancelApplicationCopy(ActionEvent actionEvent) {
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
    }
    
    public final boolean isOkToCopy() {
        ApplicationSearchResult oldApp = (ApplicationSearchResult)FacesUtil.getManagedBean("app");
        return  (oldApp != null && 
                (ApplicationTypeDef.PTIO_APPLICATION.equals(oldApp.getApplicationTypeCd()) ||
                 ApplicationTypeDef.TITLE_V_APPLICATION.equals(oldApp.getApplicationTypeCd())));
    }
    
    public final boolean isSelectedAppCorrectable() {
        boolean correctable = selectedApp != null && 
            (ApplicationTypeDef.PTIO_APPLICATION.equals(selectedApp.getApplicationTypeCd()) ||
                    ApplicationTypeDef.TITLE_V_APPLICATION.equals(selectedApp.getApplicationTypeCd())) &&
                selectedApp.getSubmittedDate() != null;
        return correctable;
    }

    public final boolean isCopyEnabled() {
        boolean enabled = false;
        for (ApplicationSearchResult app : getApplications()) {
            if (ApplicationTypeDef.PTIO_APPLICATION.equals(app.getApplicationTypeCd()) ||
                    ApplicationTypeDef.TITLE_V_APPLICATION.equals(app.getApplicationTypeCd())) {
                enabled = true;
                break;
            }
        }
        return enabled;
     }

    public final ApplicationSearchResult getSelectedApp() {
        return selectedApp;
    }

    public final void setSelectedApp(ApplicationSearchResult selectedApp) {
        this.selectedApp = selectedApp;
    }

    @Override
    public void initActionTable(ActionEvent event) {
        super.initActionTable(event);
        if (actionTable != null) {
            List<Object> selectedRows = getSelectedActionTableRows();
            if (selectedRows.size() == 1) {
                selectedApp = (ApplicationSearchResult)selectedRows.get(0);
            } else {
                logger.error("Coding Error. Expected one row to be selected.");
            }
        }
    }
    
    public final Class<? extends Application> getNewPortalApplicationClass() {
        return newApplication == null ? null : newApplication.getClass();
    }
    
    public final void setNewPortalApplicationClass(Class<? extends Application> newApplicationClass) {
    	if (newApplicationClass != null) {

			try {
				newApplication = newApplicationClass.newInstance();
				// set the portal relocation managed bean type
				RelocateRequest relocateRequest = new RelocateRequest();

				/*
				if (RelocateRequestITR.class.equals(newApplicationClass)) {
					relocateRequest.setApplicationTypeCD(ApplicationTypeDef.INTENT_TO_RELOCATE);
				} else if (RelocateRequestSPA.class.equals(newApplicationClass)) {
					relocateRequest.setApplicationTypeCD(ApplicationTypeDef.SITE_PRE_APPROVAL);
				} else {
					relocateRequest.setApplicationTypeCD(ApplicationTypeDef.RELOCATE_TO_PREAPPROVED_SITE);
				}*/

				Relocation relocation = (Relocation) FacesUtil.getManagedBean("relocation");

				relocateRequest.setApplicationTypeCD(relocateRequest.getApplicationTypeCD());
				relocation.setNewPortalRelReq(relocateRequest);
				relocation.setEditable(false);
			} catch (InstantiationException e) {
				logger.error(e.getMessage(), e);
				DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
			} catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
            }
		}
    }
    
    protected void setRelocation(Class<? extends Application> newApplicationClass) {   	
    	// set the relocation managed bean type
        RelocateRequest relocateRequest = (RelocateRequest)newApplication;

        /*
        if (RelocateRequestITR.class.equals(newApplicationClass)) {
            relocateRequest.setApplicationTypeCD(ApplicationTypeDef.INTENT_TO_RELOCATE);
        }  else if (RelocateRequestSPA.class.equals(newApplicationClass)) {
            relocateRequest.setApplicationTypeCD(ApplicationTypeDef.SITE_PRE_APPROVAL);
        }  else {
            relocateRequest.setApplicationTypeCD(ApplicationTypeDef.RELOCATE_TO_PREAPPROVED_SITE);
        }
        */

        Relocation relocation = (Relocation) FacesUtil.getManagedBean("relocation");
    	relocateRequest.setFacility(getMyTasks().getFacility());
        relocateRequest.setUserId(CommonConst.GATEWAY_USER_ID);
        
        relocateRequest.setNewRecord(true);
        relocateRequest.setApplicationTypeCD(relocateRequest.getApplicationTypeCD());
        relocation.setRelocateRequest(relocateRequest);
        relocation.setEditable(true);
    }   
}
