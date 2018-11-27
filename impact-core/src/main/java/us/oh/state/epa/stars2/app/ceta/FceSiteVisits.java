package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FacilityHistoryService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Evaluator;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityComplianceStatus;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.database.dbObjects.ceta.SiteVisit;
import us.oh.state.epa.stars2.database.dbObjects.ceta.VisitBase;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.def.AirProgramsDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

/*
 * Backing bean for Inspection/Site Visit work.
 */

@SuppressWarnings("serial")
public class FceSiteVisits extends ValidationBase {
    private String facilityId;
    private Integer fceId;
    private Integer fpId;
	private boolean fromFacility = false;
	private boolean hasSearchResults;
    protected String popupRedirectOutcome;
    private FullComplianceEval[] fceList;
    private Timestamp visitsFrom;
    private Timestamp visitsTo;
    private String typeOfNewVisit;
    private SiteVisit[] allSiteVisits = new SiteVisit[0];
    boolean haveCalledInitFCEs = false;
    
    private boolean okToEditComplianceStatus = false;
    private FacilityHistory facilityHistory;  // from fce, site visit or enforcement detail
    private Facility histFacility; // facility object for facilityHistory

    private List<FacilityComplianceStatus> airPrograms;

    private TableSorter nspsPollutantsWrapper;
    private TableSorter psdPollutantsWrapper;
    private TableSorter nsrPollutantsWrapper;
    private TableSorter neshapsSubpartsWrapper;
    
    private boolean disableHistoryEdit;  // read from jsp to indicate whether edit allowed.
   // private String afsId;  // set from jsp to indicate whether object is AFS locked.
    
    private FacilityHistoryService facilityHistoryService;
	private FacilityService facilityService;
	private FullComplianceEvalService fullComplianceEvalService;

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

    
    public FacilityHistoryService getFacilityHistoryService() {
		return facilityHistoryService;
	}
	public void setFacilityHistoryService(
			FacilityHistoryService facilityHistoryService) {
		this.facilityHistoryService = facilityHistoryService;
	}
    public FceSiteVisits() {
        super();
    }
    
    public final String submitSearch() {
    	fceList = null;
    	hasSearchResults = false;
    	if (fceId != null) {
			try {
				FullComplianceEval st = getFullComplianceEvalService().retrieveFce(facilityId, fceId);
				if (st != null) {
		    		fceList = new FullComplianceEval[1];
					fceList[0] = st;
					hasSearchResults = true;
				}
			} catch (RemoteException e) {
				handleException(e);
			}
    	}
    	return SUCCESS;
    }

    public final String reset() {
    	facilityId = null;
    	fceId = null;
    	hasSearchResults = false;
    	return SUCCESS;
    }
    
    // called in case site visits have been changed.
    public void silentInitFCEs() {
        if(haveCalledInitFCEs) {
            initFCEs();
        }
    }
    
    public String initFCEs() {
        haveCalledInitFCEs = true;
        reset();
        FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
        facilityId = fp.getFacilityId();
        fpId = fp.getFpId();
        if (facilityId != null && facilityId.trim().length() > 0) {
            try {
                fceList = getFullComplianceEvalService().retrieveFceBySearch(facilityId);
                allSiteVisits = getFullComplianceEvalService().searchSiteVisits(facilityId, visitsFrom, visitsTo);
                if (fceList.length == 0)
                	DisplayUtil.displayInfo("There are no Inspections available for this facility.");
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return "facilities.profile.FCEs";
    }
    
    public String initSiteVisits() {
        haveCalledInitFCEs = true;
        reset();
        FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
        facilityId = fp.getFacilityId();
        fpId = fp.getFpId();
        if (facilityId != null && facilityId.trim().length() > 0) {
            try {
                fceList = getFullComplianceEvalService().retrieveFceBySearch(facilityId);
                allSiteVisits = getFullComplianceEvalService().searchSiteVisits(facilityId, visitsFrom, visitsTo);
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        return "facilities.profile.siteVisits";
    }
    
    public String initSiteVisitsForCancel() {
        haveCalledInitFCEs = true;
        reset();
        FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
        facilityId = fp.getFacilityId();
        fpId = fp.getFpId();
        if (facilityId != null && facilityId.trim().length() > 0) {
            try {
                fceList = getFullComplianceEvalService().retrieveFceBySearch(facilityId);
                allSiteVisits = getFullComplianceEvalService().searchSiteVisits(facilityId, visitsFrom, visitsTo);
            } catch (RemoteException re) {
                handleException(re);
            }
        }
        if (isFromFacility())
        	return "facilities.profile.siteVisits";
        else
        	return "ceta.fceDetail";
    }
    
    // Create a new Inspection
    public final String newFCE() {
        FceDetail fceDetail = (FceDetail)FacesUtil.getManagedBean("fceDetail");
        fceDetail.editNewFCE(facilityId);
        return "ceta.fceDetail";
    }

    public final String newAssocStackTest() {
        StackTestDetail stackTestDetail = (StackTestDetail)FacesUtil.getManagedBean("stackTestDetail");
        return stackTestDetail.newAssocStackTest(facilityId, fpId, fceId);
    }

    public final String newAssocVisit() {
        typeOfNewVisit = "Create Site Visit Associated with Inspection " + fceId;
        return newInternalVisit(fceId);
    }

    public final String newVisit() {
        typeOfNewVisit = "Create Site Visit";
        fromFacility = true;
        return newInternalVisit(null);
    }
    
    public final String newInternalVisit(Integer fceId) {
        SiteVisitDetail siteVisitDetail = (SiteVisitDetail)FacesUtil.getManagedBean("siteVisitDetail");
        SiteVisit sv = new SiteVisit();
        Evaluator e = new Evaluator(InfrastructureDefs.getCurrentUserId());
        sv.getEvaluators().add(e);
        sv.setFacilityId(facilityId);
        sv.setFpId(fpId);
        sv.setFceId(fceId);
        siteVisitDetail.setFacilityId(facilityId);
        siteVisitDetail.setSiteVisit(sv);
        siteVisitDetail.setBlankOutPage(false);
        siteVisitDetail.editNewVisit();
        setHistFacilityInfo(facilityId);
        siteVisitDetail.setFacility(histFacility);
        siteVisitDetail.initializeAttachmentBean();
        return "ceta.siteVisitDetail";
    }
    
    public final void allVisits() {
        visitsFrom = null;
        visitsTo = null;
        try {
            allSiteVisits = getFullComplianceEvalService().searchSiteVisits(facilityId, visitsFrom, visitsTo);
        }catch(RemoteException re) {
            handleException(re);
        }
    }
    
    public final void addNeshapsSubpart() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            // Add row to table.
            facilityHistory.getNeshapsCompStatusList().add(new FacilityComplianceStatus());
            neshapsSubpartsWrapper = new TableSorter();
            neshapsSubpartsWrapper.setWrappedData(facilityHistory.getNeshapsCompStatusList());
        } finally {
            clearButtonClicked();
        }
        return;
    }

    public final void deleteNeshapsSubparts() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            // delete row of table.
            Iterator<Object> i = neshapsSubpartsWrapper.getData().iterator();
            while (i.hasNext()) {
                if (((FacilityComplianceStatus)i.next()).isDelete()) {
                    i.remove();
                }
            }
            neshapsSubpartsWrapper = new TableSorter();
            neshapsSubpartsWrapper.setWrappedData(facilityHistory.getNeshapsCompStatusList());
        } finally {
            clearButtonClicked();
        }
        return;
    }
   
    public final void addNspsPollutant() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            // Add row to table.
            facilityHistory.getNspsCompStatusList().add(new FacilityComplianceStatus());
            nspsPollutantsWrapper = new TableSorter();
            nspsPollutantsWrapper.setWrappedData(facilityHistory.getNspsCompStatusList());
        } finally {
            clearButtonClicked();
        }
        return;
    }
    
    public final void deleteNspsPollutants() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            // delete row of table.
            Iterator<Object> i = nspsPollutantsWrapper.getData().iterator();
            while (i.hasNext()) {
                if (((FacilityComplianceStatus)i.next()).isDelete()) {
                    i.remove();
                }
            }
            nspsPollutantsWrapper = new TableSorter();
            nspsPollutantsWrapper.setWrappedData(facilityHistory.getNspsCompStatusList());
        } finally {
            clearButtonClicked();
        }
        return;
    }
    
    public final void addNsrPollutant() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            // Add row to table.
            facilityHistory.getNsrNonAttainmentCompStatusList().add(new FacilityComplianceStatus());
            nsrPollutantsWrapper = new TableSorter();
            nsrPollutantsWrapper.setWrappedData(facilityHistory.getNsrNonAttainmentCompStatusList());
        } finally {
            clearButtonClicked();
        }
        return;
    }

    public final void deleteNsrPollutants() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            // delete row of table.
            Iterator<Object> i = nsrPollutantsWrapper.getData().iterator();
            while (i.hasNext()) {
                if (((FacilityComplianceStatus)i.next()).isDelete()) {
                    i.remove();
                }
            }
            nsrPollutantsWrapper = new TableSorter();
            nsrPollutantsWrapper.setWrappedData(facilityHistory.getNsrNonAttainmentCompStatusList());
        } finally {
            clearButtonClicked();
        }
        return;
    }
    
    public final void addPsdPollutant() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            // Add row to table.
            facilityHistory.getPsdCompStatusList().add(new FacilityComplianceStatus());
            psdPollutantsWrapper = new TableSorter();
            psdPollutantsWrapper.setWrappedData(facilityHistory.getPsdCompStatusList());
        } finally {
            clearButtonClicked();
        }
        return;
    }

    public final void deletePsdPollutants() {
        if(!firstButtonClick()) { // protect from multiple clicks
            return;
        }
        try {
            // delete row of table.
            Iterator<Object> i = psdPollutantsWrapper.getData().iterator();
            while (i.hasNext()) {
                if (((FacilityComplianceStatus)i.next()).isDelete()) {
                    i.remove();
                }
            }
            psdPollutantsWrapper = new TableSorter();
            psdPollutantsWrapper.setWrappedData(facilityHistory.getPsdCompStatusList());
        } finally {
            clearButtonClicked();
        }
        return;
    }
    
    public void editComplianceStatus() {
        viewHistory();
        if(facilityHistory != null) okToEditComplianceStatus = true;
    }
    
    public void saveComplianceStatus() {
        boolean isValid = true;
        ValidationMessage[] validationMessages = facilityHistory.validate();
        if (validationMessages.length > 0) {
            if (displayValidationMessages("notUsed",
                    validationMessages)) {
                isValid = false;
            }
        }

        if (facilityHistory != null && airPrograms != null) {
            // get the compliance codes
	    	for (FacilityComplianceStatus cs : airPrograms) {
	    		if (AirProgramsDef.MACT.equals(cs.getPollutantCd())) {
	    			facilityHistory.setMactCompCd(cs.getComplianceCd());
                    if(cs.getComplianceCd() == null || cs.getComplianceCd().length() == 0) {
                        DisplayUtil.displayError("MACT compliance status not set");
                        isValid = false;
                    }
	    		} else if (AirProgramsDef.SIP.equals(cs.getPollutantCd())) {
	    			facilityHistory.setSipCompCd(cs.getComplianceCd());
                    if(cs.getComplianceCd() == null || cs.getComplianceCd().length() == 0) {
                        DisplayUtil.displayError("SIP compliance status not set");
                        isValid = false;
                    }
	    		} else {
	    			facilityHistory.setAirProgramCompCd(cs.getComplianceCd());
                    facilityHistory.setAirProgramCd(cs.getPollutantCd());
	    		}
	    	}
            if(!isValid) return;
            okToEditComplianceStatus = false;
            try {
                getFacilityHistoryService().modifyFacilityHistory(facilityHistory);
            } catch (RemoteException e) {
                handleException(e)  ;
            }
            viewHistory();
    	}
    }
    
    public void cancelEditComplianceStatus() {
        okToEditComplianceStatus = false;
        viewHistory();
		if(facilityHistory == null) {
			logger.error("No able to retrieve facility history; ID: " + facilityHistory.getFacilityHistId());
			close();
		}
    }
    
    public final String readViewHistory(){
        SiteVisitDetail siteVisitDetail = (SiteVisitDetail)FacesUtil.getManagedBean("siteVisitDetail");
        // Make a dummy history record and populate with historyId
        facilityHistory = new FacilityHistory();
        if(siteVisitDetail.getSiteVisit() == null || siteVisitDetail.getSiteVisit().getFacilityHistId() == null) {
            DisplayUtil.displayError("Air Program History information not found");
            return null;
        }
        facilityHistory.setFacilityHistId(siteVisitDetail.getSiteVisit().getFacilityHistId());
        return viewHistory();
    }
    
    public final String viewHistory() {
        okToEditComplianceStatus = false;
        Integer fhId = facilityHistory.getFacilityHistId();
        facilityHistory = null;
        try {
            facilityHistory = getFacilityHistoryService().retrieveFacilityHistory(fhId);
        } catch(RemoteException re) {
            
        }
        if(facilityHistory != null) {
            nspsPollutantsWrapper = new TableSorter();
            psdPollutantsWrapper = new TableSorter();
            nsrPollutantsWrapper = new TableSorter();
            neshapsSubpartsWrapper = new TableSorter();
           
            nspsPollutantsWrapper.setWrappedData(facilityHistory.getNspsCompStatusList());
            psdPollutantsWrapper.setWrappedData(facilityHistory.getPsdCompStatusList());
            nsrPollutantsWrapper.setWrappedData(facilityHistory.getNsrNonAttainmentCompStatusList());
            neshapsSubpartsWrapper.setWrappedData(facilityHistory.getNeshapsCompStatusList());
            airPrograms = new ArrayList<FacilityComplianceStatus>();
            FacilityComplianceStatus cs = new FacilityComplianceStatus();
			cs.setPollutantCd(AirProgramsDef.SIP);
			cs.setComplianceCd(facilityHistory.getSipCompCd());
			setAirPrograms();
            return "dialog:viewHistory";
        } else {
            DisplayUtil.displayError("Air Program History information not found");
        }
        return null;
    }
    
    private void setAirPrograms() {
    	airPrograms = new ArrayList<FacilityComplianceStatus>();
        FacilityComplianceStatus cs = new FacilityComplianceStatus();
		cs.setPollutantCd(AirProgramsDef.SIP);
		cs.setComplianceCd(facilityHistory.getSipCompCd());
		airPrograms.add(cs);
        if (facilityHistory.isMact()) {
    		cs = new FacilityComplianceStatus();
    		cs.setPollutantCd(AirProgramsDef.MACT);
    		cs.setComplianceCd(facilityHistory.getMactCompCd());
    		airPrograms.add(cs);
    	}
        if (facilityHistory.getAirProgramCd() != null) {
        	cs = new FacilityComplianceStatus();
        	cs.setPollutantCd(facilityHistory.getAirProgramCd());
        	cs.setComplianceCd(facilityHistory.getAirProgramCompCd());
        	airPrograms.add(cs);
        }
    }

    public final void close() {
        FacesUtil.returnFromDialogAndRefresh();
    }
    
    public final FullComplianceEval[] getFceList() {
    	return fceList;
    }

    public final String getFacilityId() {
        return facilityId;
    }
    
    public final void  setFacilityId(String facilityId) {
    	this.facilityId = facilityId;
    }

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public final void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public final String getPopupRedirectOutcome() {
		return popupRedirectOutcome;
	}

	public final void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}
	
    public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }

    public boolean isFromFacility() {
        return fromFacility;
    }

    public void setFromFacility(boolean fromFacility) {
        this.fromFacility = fromFacility;
    }

    public VisitBase[] getAllSiteVisits() {
        return allSiteVisits;
    }

    public Timestamp getVisitsFrom() {
        return visitsFrom;
    }

    public Timestamp getVisitsTo() {
        return visitsTo;
    }

    public void setVisitsFrom(Timestamp visitsFrom) {
        this.visitsFrom = visitsFrom;
        try {
            allSiteVisits = getFullComplianceEvalService().searchSiteVisits(facilityId, visitsFrom, visitsTo);
        }catch(RemoteException re) {
            handleException(re);
        }
    }

    public void setVisitsTo(Timestamp visitsTo) {
        this.visitsTo = visitsTo;
        try {
            allSiteVisits = getFullComplianceEvalService().searchSiteVisits(facilityId, visitsFrom, visitsTo);
        }catch(RemoteException re) {
            handleException(re);
        }
    }

    public String getTypeOfNewVisit() {
        return typeOfNewVisit;
    }

    public void setTypeOfNewVisit(String typeOfNewVisit) {
        this.typeOfNewVisit = typeOfNewVisit;
    }

    public Integer getFceId() {
        return fceId;
    }

    public void setFceId(Integer fceId) {
        this.fceId = fceId;
    }

    public Integer getFpId() {
        return fpId;
    }

    public void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public FacilityHistory getFacilityHistory() {
        return facilityHistory;
    }

    public void setFacilityHistory(FacilityHistory facilityHistory) {
        this.facilityHistory = facilityHistory;
        histFacility = null;
    }

    public boolean isOkToEditComplianceStatus() {
        return okToEditComplianceStatus;
    }

    public void setOkToEditComplianceStatus(boolean okToEditComplianceStatus) {
        this.okToEditComplianceStatus = okToEditComplianceStatus;
    }

    public TableSorter getNspsPollutantsWrapper() {
        return nspsPollutantsWrapper;
    }

    public void setNspsPollutantsWrapper(TableSorter nspsPollutantsWrapper) {
        this.nspsPollutantsWrapper = nspsPollutantsWrapper;
    }

    public TableSorter getNsrPollutantsWrapper() {
        return nsrPollutantsWrapper;
    }

    public void setNsrPollutantsWrapper(TableSorter nsrPollutantsWrapper) {
        this.nsrPollutantsWrapper = nsrPollutantsWrapper;
    }

    public TableSorter getPsdPollutantsWrapper() {
        return psdPollutantsWrapper;
    }

    public void setPsdPollutantsWrapper(TableSorter psdPollutantsWrapper) {
        this.psdPollutantsWrapper = psdPollutantsWrapper;
    }

    public TableSorter getNeshapsSubpartsWrapper() {
        return neshapsSubpartsWrapper;
    }

    public void setNeshapsSubpartsWrapper(TableSorter neshapsSubpartsWrapper) {
        this.neshapsSubpartsWrapper = neshapsSubpartsWrapper;
    }

	public final List<FacilityComplianceStatus> getAirPrograms() {
		return airPrograms;
	}

	public final void setAirPrograms(List<FacilityComplianceStatus> airPrograms) {
		this.airPrograms = airPrograms;
	}

	public final boolean isMact() {
		if (facilityHistory != null) {
			return facilityHistory.isMact();
		}
		return false;
	}

	public final void setMact(boolean mact) {
        boolean oldValue = facilityHistory.isMact();
        if (facilityHistory != null) {
			facilityHistory.setMact(mact);			
		}
        if(oldValue == true && mact == false) {
            for (FacilityComplianceStatus cs : airPrograms) {
                if (AirProgramsDef.MACT.equals(cs.getPollutantCd())) {
                    airPrograms.remove(cs);
                    break;
                } 
            }
        }
        if(oldValue == false && mact == true) {
            FacilityComplianceStatus cs = new FacilityComplianceStatus();
            cs.setPollutantCd(AirProgramsDef.MACT);
            // cs.setComplianceCd(facilityHistory.getMactCompCd());
            airPrograms.add(cs);
        }
	}

	public final String getAirProgramCd() {
		if (facilityHistory != null) {
			return facilityHistory.getAirProgramCd();
		}
		return null;
	}

	public final void setAirProgramCd(String airProgramCd) {
		if (facilityHistory != null) {
				facilityHistory.setAirProgramCd(airProgramCd);							
		}
        for (FacilityComplianceStatus cs : airPrograms) {
            if (!AirProgramsDef.MACT.equals(cs.getPollutantCd()) && !AirProgramsDef.SIP.equals(cs.getPollutantCd())) {
                cs.setPollutantCd(airProgramCd);
                break;
            } 
        }
	}

    public boolean isDisableHistoryEdit() {
        return disableHistoryEdit;
    }

    public void setAfsId(String afsId) {
 //       this.afsId = afsId;  // may not need this variable...
        disableHistoryEdit = true;
        boolean afsLocked = afsId != null && afsId.length() > 0;
        if(isCetaUpdate() && !afsLocked || isStars2Admin()) disableHistoryEdit = false;
    }

	public final Facility getHistFacility() {
		return histFacility;
	}

	public final void setHistFacility(Facility histFacility) {
		this.histFacility = histFacility;
	}
	
	public final void setHistFacilityInfo(String facilityId) {
		histFacility = null;
        try {
            histFacility = getFacilityService().retrieveFacilityData(facilityId, -1);
        } catch(RemoteException re) {
            handleException("Failed to retrieve facility " + facilityId, re);
        }
	}
}
