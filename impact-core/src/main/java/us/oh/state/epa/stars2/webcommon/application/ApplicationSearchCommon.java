package us.oh.state.epa.stars2.webcommon.application;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.context.AdfFacesContext;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.app.relocation.Relocation;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dao.permit.PermitSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.application.DelegationRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.RPCRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPERequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RPRRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequestITR;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequestRPS;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequestSPA;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.RPCTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;

@SuppressWarnings("serial")
public class ApplicationSearchCommon extends AppBase {
    protected String applicationNumber;
    protected String facilityID;
    protected String facilityName;
    protected String countyCd;
    protected String doLaaCd;
    protected String applicationType;
    protected String ptioReasonCd;
    protected boolean legacyStatePTOFlag;
    protected String permitNumber;
	private String companyName;
    
    protected boolean hasSearchResults;
    protected String popupRedirectOutcome;
    protected String newApplicationFacilityID;
    protected String fromFacility = "false";
    protected ApplicationSearchResult[] applications;
    protected ApplicationSearchResult[] applicationsForThisFacility;
    
    /**
     * Fields for New Application Dialog
     */
    protected Application newApplication;

    // varaibles needed to specify correction/amendment to an application in the
    // new application popup (accessed from Facilities)
    protected boolean amendedApplication;
    protected boolean correctedApplication;
    protected boolean clonedApplication;
    protected String correctedReason;
    protected String previousApplicationNumber;
    
    protected String pbrTypeCd;
    protected String rpcTypeCd;
    protected Integer permitId;
    protected List<SelectItem> permitList;
    private String euId;
    protected boolean fromNewAppPopup = false;
    protected ApplicationSearchCommon backup = null;
    
    private ApplicationService applicationService;
    private PermitService permitService;

    public ApplicationSearchCommon() {
        super();
        
        cacheViewIDs.add("/applications/applicationSearch.jsp");
        cacheViewIDs.add("/facilities/applications.jsp");
    }

    public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public ApplicationService getApplicationService() {
		return applicationService;
	}

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public final List<ApplicationSearchResult> getApplications() {
        return Utility.createArrayList(applications);
    }

    public final String getFromFacility() {
        return fromFacility;
    }

    public final void setFromFacility(String fromFacility) {
        this.fromFacility = fromFacility;
    }

	public final String getCompanyName() {
		return companyName;
	}

	public final void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
    
    /*
     * GUI getters/setters
     */
    public final String getFacilityID() {
        return facilityID;
    }

    public final void setFacilityID(String facilityID) {
        // clear results of last query of applications for facility
        // if facility has changed.
        if (facilityID == null || !facilityID.equals(this.facilityID)) {
            applicationsForThisFacility = null;
        }
        this.facilityID = facilityID;
    }

    public final String getApplicationNumber() {
        return applicationNumber;
    }

    public final void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public final String getFacilityName() {
        return facilityName;
    }

    public final void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public final String getApplicationType() {
        return applicationType;
    }

    public final void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public final boolean getHasSearchResults() {
        return hasSearchResults;
    }

    public final Class<? extends Application> getNewApplicationClass() {
        return newApplication == null ? null : newApplication.getClass();
    }

    public final void setNewApplicationClass(Class<? extends Application> newApplicationClass) {
        permitList = null;
        if (newApplicationClass != null) {
            
            try {
                newApplication = newApplicationClass.newInstance();
                if (RPRRequest.class.equals(newApplicationClass)) {
                    permitList = buildRPRPermitList();
                //} else if (RPERequest.class.equals(newApplicationClass)) {
                //    permitList = buildRPEPermitList();
                } else if (RPCRequest.class.equals(newApplicationClass)) {
                    // PTIO Admin is only choice available to non-Admin internal users,
                    // so go ahead and set it now
                    if (isInternalApp() && !InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
                        setRpcTypeCd(RPCTypeDef.PTIO_ADMIN_MOD);
                    }
                } else if (RelocateRequestITR.class.equals(newApplicationClass) || RelocateRequestSPA.class.equals(newApplicationClass) || RelocateRequestRPS.class.equals(newApplicationClass)) {
                    //set the relocation managed bean type]
                    setRelocation(newApplicationClass);
                } else if (DelegationRequest.class.equals(newApplicationClass)) {
                    logger.debug("configuring new DOR record...");
                    DelegationRequest delegationRequest = (DelegationRequest)newApplication;
                    delegationRequest.setApplicationTypeCD(ApplicationTypeDef.DELEGATION_OF_RESPONSIBILITY);
                    
                    delegationRequest.setNewRecord(true);
                    delegationRequest.setApplicationTypeCD(delegationRequest.getApplicationTypeCD());
                    if (isInternalApp()) { 
                        FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
                        delegationRequest.setFacility(fp.getFacility());
	                    us.oh.state.epa.stars2.app.delegation.Delegation delegation = 
	                    	(us.oh.state.epa.stars2.app.delegation.Delegation) FacesUtil.getManagedBean("delegation");
	                    // delegationRequest.setUserId(InfrastructureDefs.getCurrentUserId());
	                    delegation.setDelegationRequest(delegationRequest);
	                    delegation.setEditable(true);
                    } else {
                    	us.oh.state.epa.stars2.portal.facility.FacilityProfile fp = 
                    		(us.oh.state.epa.stars2.portal.facility.FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
                        delegationRequest.setFacility(fp.getFacility());
	                    us.oh.state.epa.stars2.portal.delegation.Delegation delegation = 
	                    	(us.oh.state.epa.stars2.portal.delegation.Delegation) FacesUtil.getManagedBean("delegation");
	                    // delegationRequest.setUserId(InfrastructureDefs.getCurrentUserId());
	                    delegation.setDelegationRequest(delegationRequest);
	                    delegation.setEditable(true);
                    }
                }
            } catch (InstantiationException e) {
                logger.error(e.getMessage(), e);
                DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                DisplayUtil.displayError("A system error has occurred. Please contact System Administrator.");
            }
        } else {
            logger.warn("Null application type class");
        }
    }
    
    protected void setRelocation(Class<? extends Application> newApplicationClass) {
    	//    	set the relocation managed bean type]
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
        
        FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
        relocateRequest.setFacility(fp.getFacility());
        relocateRequest.setUserId(InfrastructureDefs.getCurrentUserId());
        
        relocateRequest.setNewRecord(true);
        relocateRequest.setApplicationTypeCD(relocateRequest.getApplicationTypeCD());
        relocation.setRelocateRequest(relocateRequest);
        relocation.setEditable(true);
    }

    public boolean isRelocationClass() {
        if (RelocateRequestITR.class.equals(getNewApplicationClass()) || RelocateRequestSPA.class.equals(getNewApplicationClass()) || RelocateRequestRPS.class.equals(getNewApplicationClass())) {
            return true;
        }
        return false;
    }

    public boolean isDelegationClass() {
        if (DelegationRequest.class.equals(getNewApplicationClass())) {
            return true;
        }
        return false;
    }
    
    /* IMPACT does not currently support RPE Permits
    public final List<SelectItem> buildRPEPermitList() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        List<SelectItem> pl = new ArrayList<SelectItem>();
        try {
            GregorianCalendar begin = new GregorianCalendar();
            begin.setTime(new Date(System.currentTimeMillis()));
            begin.add(Calendar.MONTH, -18);
            Timestamp bt = new Timestamp(begin.getTimeInMillis());
            
            List<Permit> ps = getPermitService().search(null, null, null, PermitTypeDef.TVPTI,
                    PermitReasonsDef.INITIAL_INSTALLATION, null, facilityID,
                    null, PermitGlobalStatusDef.ISSUED_FINAL, 
                    PermitSQLDAO.EFFECTIVE_DATE, bt, null, null, unlimitedResults());
            DisplayUtil.displayHitLimit(ps.size());
            for (Permit p : ps)
                if (p.getFinalIssueDate() != null)
                    pl.add(new SelectItem(p.getPermitID(),p.getPermitNumber() + " " + dateFormat.format(p.getFinalIssueDate())));
                else
                    pl.add(new SelectItem(p.getPermitID(),p.getPermitNumber()));
            
            ps = getPermitService().search(null, null, null, PermitTypeDef.TVPTI,
                    PermitReasonsDef.CHAPTER_31_MOD, null, facilityID,
                    null, PermitGlobalStatusDef.ISSUED_FINAL, 
                    PermitSQLDAO.EFFECTIVE_DATE, bt, null, null, false);
            for (Permit p : ps){
                SelectItem tp = null;
                if (p.getFinalIssueDate() != null)
                    tp = new SelectItem(p.getPermitID(),p.getPermitNumber() + " " + dateFormat.format(p.getFinalIssueDate()));
                else
                    tp = new SelectItem(p.getPermitID(),p.getPermitNumber());

                for (SelectItem si : pl)
                    if (si.getValue().equals(tp.getValue())){
                        pl.add(tp);
                        break;
                    }
            }
            
            ps = getPermitService().search(null, null, null, PermitTypeDef.NSR,
                    PermitReasonsDef.INITIAL_INSTALLATION, null, facilityID,
                    null, PermitGlobalStatusDef.ISSUED_FINAL, 
                    PermitSQLDAO.EFFECTIVE_DATE, bt, null, null, false);
            for (Permit p : ps)
                if (p.getFinalIssueDate() != null)
                    pl.add(new SelectItem(p.getPermitID(),p.getPermitNumber() + " " + dateFormat.format(p.getFinalIssueDate())));
                else
                    pl.add(new SelectItem(p.getPermitID(),p.getPermitNumber()));
            
            ps = getPermitService().search(null, null, null, PermitTypeDef.NSR,
                    PermitReasonsDef.CHAPTER_31_MOD, null, facilityID,
                    null, PermitGlobalStatusDef.ISSUED_FINAL, 
                    PermitSQLDAO.EFFECTIVE_DATE, bt, null, null, false);
            for (Permit p : ps){
                SelectItem tp = null;
                if (p.getFinalIssueDate() != null)
                    tp = new SelectItem(p.getPermitID(),p.getPermitNumber() + " " + dateFormat.format(p.getFinalIssueDate()));
                else
                    tp = new SelectItem(p.getPermitID(),p.getPermitNumber());
                
                for (SelectItem si : pl)
                    if (!si.getValue().equals(tp.getValue())){
                        pl.add(tp);
                        break;
                    }
            }
            
        } catch (RemoteException e) {
            DisplayUtil.displayError("Permit search failed");
            logger.error("permit search failed", e);
        }
        return pl;
    }
    */

    public final List<SelectItem> buildRPRPermitList() {
        List<SelectItem> pl = new ArrayList<SelectItem>();
        try {
            SimpleIdDef[] sd = getPermitService().retrieveRPRPermitList(facilityID);
            for (SimpleIdDef s : sd)
                pl.add(new SelectItem(s.getId(), s.getDescription()));
        } catch (RemoteException e) {
            DisplayUtil.displayError("Permit search failed");
            logger.error(e.getMessage(), e);
        }
        return pl;
    }
    
    public final List<SelectItem> buildRPCPermitList() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        List<SelectItem> permitSelectList = new ArrayList<SelectItem>();
        if (rpcTypeCd != null && newApplicationFacilityID != null) {
            try {
                List<Permit> permits = getApplicationService().retrievePermitsForRPCRequest(
                        rpcTypeCd, newApplicationFacilityID);
                
                // map to sort permits by issue date (and permit id)
                TreeMap<String, SelectItem> permitSelectItemMap = new TreeMap<String, SelectItem>();
                for (Permit permit : permits) {
                    String issueDateLabel = "";
                    String issueDateKey = "";
                    if (permit.getFinalIssueDate() != null) {
                        issueDateLabel = dateFormat.format(permit.getFinalIssueDate());
                        issueDateKey = String.valueOf(permit.getFinalIssueDate().getTime());
                    }
                    String permitType = "";
                    if (permit.getPermitType() != null) {
                        permitType = PermitTypeDef.getData().getItems().getItemDesc(permit.getPermitType());
                    }
                    SelectItem item = new SelectItem();
                    item.setValue(permit.getPermitID());
                    String itemListing = permit.getPermitNumber() + " " + 
                    permitType   + " " + issueDateLabel;
                    item.setLabel(itemListing);
                    item.setDescription(itemListing);
                    permitSelectItemMap.put(issueDateKey  + "_" + 
                            permit.getPermitID().toString(), item);
                }
                // add permits to drop down list in reverse chronological order
                List<String> keyList = new LinkedList<String>(permitSelectItemMap.keySet());
                Collections.reverse(keyList);
                for (String key : keyList) {
                    permitSelectList.add(permitSelectItemMap.get(key));
                }
            } catch (RemoteException e) {
                handleException(e);
            }
        }
        return permitSelectList;
    }

    public final String getNewApplicationFacilityID() {
        return newApplicationFacilityID;
    }

    public final void setNewApplicationFacilityID(String newApplicationFacilityID) {
        this.newApplicationFacilityID = newApplicationFacilityID;
        setFacilityID(newApplicationFacilityID);
    }

    public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }

    public final List<SelectItem> getPreviousApplicationNumbers() {
        List<SelectItem> appNumbers = new ArrayList<SelectItem>();
        TreeSet<String> appNumbersSet = new TreeSet<String>();
        if (newApplication != null) {
            try {
                applicationsForThisFacility 
                    = getApplicationService().retrieveApplicationSearchResults(null, null,facilityID, null, null,
                            null, null, null, false, null, null, null, true);
                    
                for (ApplicationSearchResult app : applicationsForThisFacility) {
                    // only PTIO or TV applications can show up in drop down
                    // and application type must match selected type
                    if ((ApplicationTypeDef.PTIO_APPLICATION.equals(app.getApplicationTypeCd()) ||
                            ApplicationTypeDef.TITLE_V_APPLICATION.equals(app.getApplicationTypeCd())) &&
                            app.getApplicationTypeCd().equals(newApplication.getApplicationTypeCD())) {
                        if (correctedApplication && app.getSubmittedDate() == null) {
                            continue;   // only submitted applications may be corrected/amended
                        }
                        appNumbersSet.add(app.getApplicationNumber());
                        
                    }
                }
                Iterator<String> it = appNumbersSet.iterator();
                while (it.hasNext()) {
                    String appNumber = it.next();
                    appNumbers.add(new SelectItem(appNumber, appNumber));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return appNumbers;
    }

    /*
     * Actions
     */
    public final String search() {
        try {
            // clear information for new application popup
            setNewApplicationClass(null);
            hasSearchResults = false;
            applications = getApplicationService().retrieveApplicationSearchResults(applicationNumber, euId,
                                                                            facilityID, facilityName, doLaaCd,
                                                                            countyCd, applicationType, ptioReasonCd,
                                                                            legacyStatePTOFlag, pbrTypeCd,
                                                                            permitNumber, companyName, 
                                                                            unlimitedResults());
            DisplayUtil.displayHitLimit(applications.length);
            if (applications.length == 0) {
            	if (fromFacility.equals("true")) {
            		DisplayUtil.displayInfo("There are no Applications available for this facility.");
            	} else {
            		DisplayUtil.displayInfo("There are no Applications that match the search criteria.");
            	}
            } else {
                hasSearchResults = true;
            }
        } catch (Exception ex) {
            DisplayUtil.displayError("Application search failed");
            logger.error(ex.getMessage(), ex);
        }
        return "applicationSearch";
    }
    
    public final String refresh() {
        String ret = "applicationSearch";
        if (backup != null) {
            copy(backup);
            backup = null;
            search();
        }
        return ret;
    }

    public final String reset() {
        applicationNumber = null;
        facilityID = null;
        facilityName = null;
        applicationType = null;
        applications = null;
        countyCd = null;
        doLaaCd = null;
        hasSearchResults = false;
        permitNumber = null;
        euId = null;
        pbrTypeCd = null;
        ptioReasonCd = null;
        companyName =null;
        resetNewApplication();
        return null; // stay on same page
    }
    
    /**
     * 2598
     * @param as
     */
    public void copy(ApplicationSearchCommon as) {
        setApplicationNumber(as.getApplicationNumber());
        setFacilityID(as.getFacilityID());
        setFacilityName(as.getFacilityName());
        setApplicationType(as.getApplicationType());
        setCountyCd(as.getCountyCd());
        setDoLaaCd(as.getDoLaaCd());
        setPermitNumber(as.getPermitNumber());
        setEuId(as.getEuId());
        setPbrTypeCd(as.getPbrTypeCd());
        setPtioReasonCd(as.getPtioReasonCd());
        setCompanyName(as.getCompanyName());
    }
    
    public void setBackup(ApplicationSearchCommon backup) {
        this.backup = backup;
    }

    public final String startNewApplication() {
        fromNewAppPopup = true;
        return "dialog:newApplication";
    }
    
    public void createNewApplication(ActionEvent event) {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            createNewApplicationInternal(event);
        } finally {
            clearButtonClicked();
        }
    }

    protected void createNewApplicationInternal(ActionEvent event) {
        boolean ok = true;
        if (newApplicationFacilityID != null
                && newApplicationFacilityID.length() > 0) {
            if (newApplication != null) {
                ApplicationDetailCommon applicationDetail = (ApplicationDetailCommon) FacesUtil
                .getManagedBean("applicationDetail");
                if (correctedApplication || amendedApplication) {
                    // correct/amend existing application
                    if (previousApplicationNumber == null) {
                        DisplayUtil
                        .displayWarning("Please provide the application number of the application being " +
                                (correctedApplication ? "corrected." : "amended."));
                        ok = false;
                    } else if (!amendedApplication && correctedReason == null) {
                        DisplayUtil
                        .displayWarning("Please provide a reason why the application is being corrected.");
                        ok = false;
                    } else if (applicationDetail.correctApplication(
                            previousApplicationNumber, amendedApplication,
                            correctedReason) < 0) {
                        DisplayUtil.displayError("Failed creating application");
                        ok = false;
                    }
                } else if (clonedApplication) {
                    if (previousApplicationNumber == null) {
                        DisplayUtil
                        .displayWarning("Please provide the application number of the application being copied");
                        ok = false;
                    } else if (applicationDetail.cloneApplication(previousApplicationNumber, newApplication.getClass()) < 0) {
                        DisplayUtil.displayError("Failed copying application");
                        ok = false;
                    }
                } else {
                    // create new application
                    Facility facility;

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
                            if (applicationDetail.newApplication(newApplication) < 0) {
                                DisplayUtil.displayError("Failed creating request");
                                ok = false;
                            } else {
                                // clear out fields related to creating an application
                                // so same data won't appear next time window is invoked.
                                resetNewApplication();
                            }
                        }
                    } catch (Exception ex) {
                        DisplayUtil.displayError("Failed creating request");
                        logger.error(ex.getMessage(), ex);
                        ok = false;
                    }
                }
                if (ok) {
                    // bring up new window in edit mode
                    applicationDetail.setEditMode(true);
                    popupRedirectOutcome = "applicationDetail";
                    
            		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_appDetail")).setDisabled(false);
                    FacesUtil.returnFromDialogAndRefresh();
                    fromNewAppPopup = false;
                }
            } else {
                DisplayUtil.displayWarning("Please select a request type");
                ok = false;
            }
        } else {
            DisplayUtil.displayWarning("Please enter a facility ID");
        }
    }

    public void resetNewApplication() {
        amendedApplication = false;
        correctedApplication = false;
        clonedApplication = false;
        correctedReason = null;
        previousApplicationNumber = null;
        newApplication = null;
        pbrTypeCd = null;
        rpcTypeCd = null;
        permitId = null;
        permitList = null;
    }

    public final void cancelNewApplication(ActionEvent actionEvent) {
        resetNewApplication();
        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
        fromNewAppPopup = false;
    }

    public final String getCountyCd() {
        return countyCd;
    }

    public final void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public final String getDoLaaCd() {
        return doLaaCd;
    }

    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public final List<SelectItem> getDoLaas() {
        return DoLaaDef.getData().getItems().getAllSearchItems();
    }

    public final boolean isCorrectable() {        
        List<String> correctableApplicationNumbers = new ArrayList<String>();
        // correctable only applies if "clone" check box is not checked
        if (!this.clonedApplication && (newApplication instanceof PTIOApplication ||
                newApplication instanceof TVApplication)) {
            try {
                applicationsForThisFacility = getApplicationService().retrieveApplicationSearchResults(
                        null, null,facilityID, null, null, null, null, null, false, null, null, null, true);
                
                for (ApplicationSearchResult app : applicationsForThisFacility) {
                    // only applications that have been previously submitted
                    // may be corrected/amended
                    if (app.getApplicationTypeCd().equals(newApplication.getApplicationTypeCD())
                            && app.getSubmittedDate() != null) {
                        correctableApplicationNumbers.add(app.getApplicationNumber());
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return correctableApplicationNumbers.size() > 0;
    }
    
    public final boolean isAmendable() {
        // Only PTI/PTIO applications may be amended by DAPC
        return (newApplication instanceof PTIOApplication && isCorrectable());
    }

    public final boolean isCloneable() {
        boolean cloneable = false;
        // the clone operation is available if newApplciationClass
        // is PTIO or TV and there is at least one PTIO or TV application
        // available for the selected facility.
        if (newApplication!= null) {
            if (newApplication instanceof PTIOApplication ||
                newApplication instanceof TVApplication) {
                try {
                    applicationsForThisFacility 
                        = getApplicationService().retrieveApplicationSearchResults(null, null,facilityID, 
                            null, null, null, null, null, false, null, null, null, true);
                    
                    for (ApplicationSearchResult app : applicationsForThisFacility) {
                        if (app.getApplicationTypeCd().equals(newApplication.getApplicationTypeCD())) {
                            cloneable = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return cloneable;
    }

    public final boolean isAmendedApplication() {
        return amendedApplication;
    }

    public final void setAmendedApplication(boolean amendmentToApplication) {
        this.amendedApplication = amendmentToApplication;
    }

    public final String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public final void setPreviousApplicationNumber(String correctedApplicationNumber) {
        this.previousApplicationNumber = correctedApplicationNumber;
    }

    public final boolean isCorrectedApplication() {
        return correctedApplication;
    }

    public final void setCorrectedApplication(boolean correctedApplication) {
        this.correctedApplication = correctedApplication;
    }

    public final String getCorrectedReason() {
        return correctedReason;
    }

    public final void setCorrectedReason(String correctionReason) {
        this.correctedReason = correctionReason;
    }

    public final boolean isClonedApplication() {
        return clonedApplication;
    }

    public final void setClonedApplication(boolean clonedApplication) {
        this.clonedApplication = clonedApplication;
        // force ammendedApplication and correctedApplication
        // to false if clonedApplication is set
        if (this.clonedApplication) {
            this.amendedApplication = false;
            this.correctedApplication = false;
        }
    }
    
    public final String getCorrectedApplicationPopupText() {
        return "Checking \"Facility-requested correction to application\" " +
                "labels the request as an update to an existing application.  " +
                " If WDEQ has already issued a final " +
                "permit for the existing permit, then the correction " +
                "will NOT be acted upon.";
    }

    public final boolean isPbrNotification() {
        return (newApplication != null && newApplication instanceof PBRNotification);
    }

    public final void setPbrNotification(boolean pbrNotification) {
//        this.pbrNotification = pbrNotification;
    }
    
    public final boolean isRpcRequest() {
        return (newApplication != null && newApplication instanceof RPCRequest);
    }
    
    /**
     * Test if the application selected in the search table can be copied. Only
     * applies to portal apps.
     * @return
     */
    public boolean isOkToCopy() {
        return false;
    }

    public final List<SelectItem> getPermitList() {
        return permitList;
    }

    public final void setPermitList(List<SelectItem> permitList) {
        this.permitList = permitList;
    }

    public final Application getNewApplication() {
        return newApplication;
    }

    public final void setNewApplication(Application newApplication) {
        this.newApplication = newApplication;
    }

    public final String getPbrTypeCd() {
        return pbrTypeCd;
    }

    public final void setPbrTypeCd(String pbrTypeCd) {
        this.pbrTypeCd = pbrTypeCd;
    }

    public final Integer getPermitId() {
        return permitId;
    }

    public final void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public final String getRpcTypeCd() {
        return rpcTypeCd;
    }

    public final void setRpcTypeCd(String rpcTypeCd) {
        this.rpcTypeCd = rpcTypeCd;
        if (newApplication instanceof RPCRequest) {
            permitList = null;  // clear out old permit list
            permitList = buildRPCPermitList();
        }
    }

    public void restoreCache() {
        // submitSearch();
    }

    public void clearCache() {
		// if (!fromNewAppPopup) {
		// applications = null;
		// hasSearchResults = false;
		// }
    }
    public String getPopupRedirectOutcome() {
        return popupRedirectOutcome;
    }
    public void setPopupRedirectOutcome(String popupRedirectOutcome) {
        this.popupRedirectOutcome = popupRedirectOutcome;
    }
    public final String getPermitNumber() {
        return permitNumber;
    }
    public final void setPermitNumber(String permitNumber) {
        this.permitNumber = permitNumber;
    }
    public final String getPtioReasonCd() {
        return ptioReasonCd;
    }
    public final void setPtioReasonCd(String ptioReasonCd) {
        this.ptioReasonCd = ptioReasonCd;
    }
    public final boolean isLegacyStatePTOFlag() {
        return legacyStatePTOFlag;
    }
    public final void setLegacyStatePTOFlag(boolean legacyStatePTOFlag) {
        this.legacyStatePTOFlag = legacyStatePTOFlag;
    }
    /**
     * @return the euId
     */
    public final String getEuId() {
        return euId;
    }
    /**
     * @param euId the euId to set
     */
    public final void setEuId(String euId) {
        this.euId = euId;
    }
}
