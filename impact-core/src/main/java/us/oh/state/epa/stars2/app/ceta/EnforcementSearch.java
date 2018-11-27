package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.EnforcementActionService;
import us.oh.state.epa.stars2.bo.FacilityHistoryService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.EnforcementAction;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.def.AirProgramsDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EnfCaseStateDef;
import us.oh.state.epa.stars2.def.EnforcementActionTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

@SuppressWarnings("serial")
public class EnforcementSearch extends AppBase {
	private String facilityId;
	private String facilityName;
	private String cmpId;
	private String doLaaCd;
	private String countyCd;
	private String addToHPVList = null;
	private Timestamp beginActionDt;
	private Timestamp endActionDt;
	private Timestamp novIssuedDate;
	private Integer evaluator;
	private Integer fpId; // to access the associated fpId for compilance
	
	private String eventCd;

	private Integer enforcementActionId;
	private String searchEnfId;
	
	private String docketNumber;
	
	private ArrayList<SelectItem> allEnfCaseStates = new ArrayList<SelectItem>();
	private List<String> selectedEnfCaseStates = new ArrayList<String>();
	private int numEnfCaseStates = 0;
	
	private String enfCaseNum;
	private String actionTypeCd;
	private String actionStateCd;
	//private List<EnforcementAction> enforcements;
	private List<EnforcementAction> enforcementActions;
	private boolean hasSearchResults;
	private boolean fromFacility;
	private EnforcementSearch backup;
	private Facility facility;
	List<PollutantCompCode> airPrograms;
	private boolean okToEditComplianceStatus;
	
    private EnforcementActionService enforcementActionService;
    private FacilityHistoryService facilityHistoryService;
	private FacilityService facilityService;
	
	private String popupRedirectOutcome;
	
	public static final String SEARCH_OUTCOME = "enforcements.search";

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
    public EnforcementActionService getEnforcementActionService() {
		return enforcementActionService;
	}
	public void setEnforcementActionService(EnforcementActionService enforcementActionService) {
		this.enforcementActionService = enforcementActionService;
	}

	public final String reset() {
    	facilityId = null;
    	facilityName = null;
    	cmpId = null;
    	doLaaCd = null;
    	countyCd = null;
    	addToHPVList = null;
    	hasSearchResults = false;
    	beginActionDt = null;
    	endActionDt = null;
    	novIssuedDate = null;
    	evaluator = null;
    	searchEnfId = null;
    	selectedEnfCaseStates.clear();
    	allEnfCaseStates.clear();
    	for(SelectItem si : EnfCaseStateDef.getData().getItems().getAllItems()) {
    		allEnfCaseStates.add(new SelectItem(si.getValue(), si.getLabel()));
        }
        numEnfCaseStates = allEnfCaseStates.size();
    	enfCaseNum = null;
    	actionTypeCd = null;
    	actionStateCd = null;
    	docketNumber = null;
    	eventCd = null;
    	return SUCCESS;
    }
	
	/**
	 * Invoked from Enforcement search screen.
	 * @return
	 */
	public final String search() {
		if(!firstButtonClick()) { // protect from multiple clicks
            return null;
        }
		
		if ((this.getBeginActionDt() != null && this.getEndActionDt() != null)
				&& (!Utility.isNullOrEmpty(this.getBeginActionDt().toString()) && !Utility
						.isNullOrEmpty(this.getEndActionDt().toString()))
				&& this.getBeginActionDt().after(this.getEndActionDt())) {
			DisplayUtil.displayError("To Date must not be before From Date");
			return "enforcementSearch";
		}
		
		try {
            hasSearchResults = false;
        	enforcementActions = new ArrayList<EnforcementAction>();
        	List<String> selectedC = selectedEnfCaseStates;
            //if(selectedEnfCaseStates != null && (selectedEnfCaseStates.size() == allEnfCaseStates.size() || selectedEnfCaseStates.size() == 0)) {
            //    selectedC = null;
            //}
            if (enforcementActionId == null) {
            	enforcementActions = getEnforcementActionService().searchEnforcementActions(facilityId, facilityName, doLaaCd,
            			countyCd, addToHPVList, eventCd, beginActionDt, endActionDt,
            			evaluator, searchEnfId, selectedC, enfCaseNum, actionTypeCd, cmpId,
            			docketNumber);
            } else {
            	fromFacility = false;
            	enforcementActions = new ArrayList<EnforcementAction>();
            	EnforcementAction enf = getEnforcementActionService().retrieveEnforcementAction(enforcementActionId);
            	if (enf != null) {
            		enforcementActions.add(enf);
            	}
            }
            DisplayUtil.displayHitLimit(enforcementActions.size());
            if (enforcementActions.size() == 0) {
            	if (fromFacility) {
            		facility = getFacilityService().retrieveFacility(facilityId);
            		initAirPrograms();
            	} else {
            		DisplayUtil.displayInfo("There are no enforcement actions that match the search criteria.");
            	}
            } else {
            	/*
            	for (EnforcementAction enf : enforcementActions) {
            		if (!fromFacility) {
            			if (enf.getLatestEnforcementAction() != null) {
                			enforcementActions.add(enf.getLatestEnforcementAction());
            			}
            		} else {
	            		for (EnforcementAction action : enf.getEnforcementActions()) {
	            			action.setEnforcement(enf);
	            			enforcementActions.add(action);
	            		}
            		}
            	}
            	*/
            	if (fromFacility) {
            		facility = getFacilityService().retrieveFacility(facilityId);
            		initAirPrograms();
            	}
                hasSearchResults = true;
            }
		} catch (RemoteException e) {
			handleException(e);
		} finally {
			clearButtonClicked();
		}
		return "enforcementSearch";
	}

    public final String refresh() {
        String ret = "enforcements.search";
        fromFacility = false;
        selectedEnfCaseStates.clear();
    	allEnfCaseStates.clear();
        for(SelectItem si : EnfCaseStateDef.getData().getItems().getAllItems()) {
    		allEnfCaseStates.add(new SelectItem(si.getValue(), si.getLabel()));
        }
        if (backup != null) {
            //copy(backup);
            backup = null;
            reset();
            //search();
        }
        return ret;
    }
    
    public final void copy(EnforcementSearch es) {
    	enforcementActionId = es.enforcementActionId;
    	facilityId = es.facilityId;
    	addToHPVList = es.addToHPVList;
    	facilityName = es.facilityName;
    	doLaaCd = es.doLaaCd;
    	countyCd = es.countyCd;
    	beginActionDt = es.beginActionDt;
    	endActionDt = es.endActionDt;
    	novIssuedDate = es.novIssuedDate;
    	evaluator = es.evaluator;
    	searchEnfId = es.searchEnfId;
    	selectedEnfCaseStates = es.selectedEnfCaseStates;
    	allEnfCaseStates = es.allEnfCaseStates;
    	enfCaseNum = es.enfCaseNum;
    	actionTypeCd = es.actionTypeCd;
    	actionStateCd = es.actionStateCd;
    	docketNumber = es.docketNumber;
    	eventCd = es.getEventCd();
    }
	
	/**
	 * This is the method called by the third-level menu of the facility inventory to
	 * display the enforcement table.
	 * @return
	 */
	public final String initEnforcements() {
		return initEnforcementsInter(false);
	}
	
	public final String initEnforcementsInter(boolean fromToDo) {
		FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
		EnforcementActionDetail ed = (EnforcementActionDetail)FacesUtil.getManagedBean("enforcementActionDetail");
		backup = new EnforcementSearch();
        backup.copy(this);
        reset();
		facilityId = fp.getFacilityId();
		fpId = fp.getFpId();
		//ed.setFacilityId(facilityId);
		//ed.setSingleEnforcementView(false);
		//ed.setFromTODOList(fromToDo);
        fromFacility = true;
		if (facilityId != null && facilityId.trim().length() > 0) {
			search();
		}
        return "facilities.profile.enforcements";
	}
	
	public final String getFacilityId() {
		return facilityId;
	}
	
	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	
	public final String getFacilityName() {
		return facilityName;
	}

	public final void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final String getCountyCd() {
		return countyCd;
	}

	public final void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}

	public final String getAddToHPVList() {
		return addToHPVList;
	}

	public final void setAddToHPVList(String addToHPVList) {
		this.addToHPVList = addToHPVList;
	}

	public final Timestamp getBeginActionDt() {
		return beginActionDt;
	}

	public final void setBeginActionDt(Timestamp beginActionDt) {
		this.beginActionDt = beginActionDt;
	}

	public final Timestamp getEndActionDt() {
		return endActionDt;
	}

	public final void setEndActionDt(Timestamp endActionDt) {
		this.endActionDt = endActionDt;
	}
	
	public final Timestamp getNovIssuedDate() {
		return novIssuedDate;
	}

	public final void setNovIssuedDate(Timestamp novIssuedDate) {
		this.novIssuedDate = novIssuedDate;
	}
	
	
	public final Integer getEvaluator() {
		return evaluator;
	}

	public final void setEvaluator(Integer evaluator) {
		this.evaluator = evaluator;
	}

	public final Integer getEnforcementActionId() {
		return enforcementActionId;
	}

	public final void setEnforcementActionId(Integer enforcementActionId) {
		this.enforcementActionId = enforcementActionId;
	}

	public final String getEnfCaseNum() {
		return enfCaseNum;
	}

	public final void setEnfCaseNum(String enfCaseNum) {
		this.enfCaseNum = enfCaseNum;
	}

	public final String getActionTypeCd() {
		return actionTypeCd;
	}

	public final void setActionTypeCd(String actionTypeCd) {
		this.actionTypeCd = actionTypeCd;
	}
	
	public final String getActionStateCd() {
		return actionStateCd;
	}

	public final void setActionStateCd(String actionStateCd) {
		this.actionStateCd = actionStateCd;
	}

	public final String getSearchEnfId() {
		return searchEnfId;
	}

	public final void setSearchEnfId(String searchEnfId) {
		this.searchEnfId = searchEnfId;
	}

	public final List<EnforcementAction> getEnforcementActions() {
		return enforcementActions;
	}

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}
	
    public final boolean isFromFacility() {
		return fromFacility;
	}

	public final void setFromFacility(boolean fromFacility) {
		this.fromFacility = fromFacility;
	}
	
	public final boolean isOkToEditComplianceStatus() {
		return okToEditComplianceStatus;
	}
	
	public final void setOkToEditComplianceStatus(boolean okToEditComplianceStatus) {
		this.okToEditComplianceStatus = okToEditComplianceStatus;
	}
    
    public final List<PollutantCompCode> getAirPrograms() {
    	if (airPrograms == null) {
    		airPrograms = new ArrayList<PollutantCompCode>();
    	}
        return airPrograms;
    }
    
    private void initAirPrograms() {
    	airPrograms = new ArrayList<PollutantCompCode>();
    	if (facility != null) {
    		PollutantCompCode cs;
    		cs = new PollutantCompCode();
			cs.setPollutantCd(AirProgramsDef.SIP);
			cs.setPollutantCompCd(facility.getSipCompCd());
			airPrograms.add(cs);
	    	if (facility.isMact()) {
	    		cs = new PollutantCompCode();
	    		cs.setPollutantCd(AirProgramsDef.MACT);
	    		cs.setPollutantCompCd(facility.getMactCompCd());
	    		airPrograms.add(cs);
	    	}
	    	if (facility.getAirProgramCd() != null) {
	    		cs = new PollutantCompCode();
	    		cs.setPollutantCd(facility.getAirProgramCd());
	    		cs.setPollutantCompCd(facility.getAirProgramCompCd());
	    		airPrograms.add(cs);
	    	}
    	} else {
    		logger.error("No facility or history defined for facility: " + facilityId);
    	}
    }
    
    public final String editComplianceStatus() {
    	okToEditComplianceStatus = true;
    	return null;
    }
    
    public final String saveComplianceStatus() {
    	okToEditComplianceStatus = false;
    	if (facility != null && airPrograms != null) {
	    	for (PollutantCompCode cs : airPrograms) {
	    		if (AirProgramsDef.MACT.equals(cs.getPollutantCd())) {
	    			facility.setMactCompCd(cs.getPollutantCompCd());
	    		} else if (AirProgramsDef.SIP.equals(cs.getPollutantCd())) {
	    			facility.setSipCompCd(cs.getPollutantCompCd());
	    		} else {
	    			facility.setAirProgramCompCd(cs.getPollutantCompCd());
	    		}
	    	}
	    	try {
	    		int currentUserId = InfrastructureDefs.getCurrentUserId();
				getFacilityHistoryService().modifyCurrentFacilityHistory(facility, currentUserId, true);
				facility = getFacilityService().retrieveFacility(fpId);
                initAirPrograms();
                FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
                if(facility.getFacilityId().equals(fp.getFacilityId())) {
                    //  Refresh facility in case the user clicks on Federal Rules.
                    fp.refreshProfileRemotely();
                }
			} catch (RemoteException e) {
				handleException(e)	;
			}
    	}
    	return null;
    }
    
    public final String cancelEditComplianceStatus() {
    	okToEditComplianceStatus = false;
    	try {
    		facility = getFacilityService().retrieveFacility(fpId);
    		initAirPrograms();
    	} catch (RemoteException e) {
			handleException(e);
		}
    	return null;
    }
    
	public String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
	}
    
    public final void setPopupRedirectOutcome(String popupRedirectOutcome) {
        this.popupRedirectOutcome = popupRedirectOutcome;
    }

	public final Facility getFacility() {
		return facility;
	}
	
	public final void setFacility(Facility facility) {
		this.facility = facility;
	}

	public final void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}
	
	public final String getCmpId() {
		return cmpId;
	}

	public final List<String> getSelectedEnfCaseStates() {
		return selectedEnfCaseStates;
	}

	public final void setSelectedEnfCaseStates(List<String> selectedEnfCaseStates) {
		this.selectedEnfCaseStates = selectedEnfCaseStates;
	}

	public final ArrayList<SelectItem> getAllEnfCaseStates() {
		return allEnfCaseStates;
	}

	public final void setAllEnfCaseStates(ArrayList<SelectItem> allEnfCaseStates) {
		this.allEnfCaseStates = allEnfCaseStates;
	}

	public final int getNumEnfCaseStates() {
		return numEnfCaseStates;
	}

	public final void setNumEnfCaseStates(int numEnfCaseStates) {
		this.numEnfCaseStates = numEnfCaseStates;
	}
	
	public final DefSelectItems getEnforcementActionTypeDef() {
		return EnforcementActionTypeDef.getData().getItems();
	}
	
	public String getDocketNumber() {
		return docketNumber;
	}

	public void setDocketNumber(String docketNumber) {
		this.docketNumber = docketNumber;
	}

    public final String getEventCd() {
        return eventCd;
    }

    public final void setEventCd(String eventCd) {
        this.eventCd = eventCd;
    }
}
