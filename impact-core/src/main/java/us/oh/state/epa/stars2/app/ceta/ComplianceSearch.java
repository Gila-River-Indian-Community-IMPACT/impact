package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.AirProgramCompliance;
import us.oh.state.epa.stars2.def.ComplianceStatusDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

/*
 * Backing bean for Inspection/Site Visit work.
 */

@SuppressWarnings("serial")
public class ComplianceSearch extends ValidationBase {
    private String facilityId;
    private String facilityName;
    private String operatingStatusCd;
    private String doLaaCd;
    private String countyCd;
	private boolean hasSearchResults;
    protected String popupRedirectOutcome;
    private List<AirProgramCompliance> complianceList;

    private boolean disclosed = true;
    
    private List<String> selectedInspectClasses = new ArrayList<String>();
    private ArrayList<SelectItem> allInspectClasses = new ArrayList<SelectItem>();
    private int numInspectClasses = 0;
    
    private ArrayList<SelectItem> allCompliance = new ArrayList<SelectItem>();
    private int numCompliance = 0;
    private String noString = null;
    
    private List<String> selectedAll = new ArrayList<String>();
    private List<String> savedSelectedAll = new ArrayList<String>();
    private List<String> selectedSip = new ArrayList<String>();
    private List<String> selectedNsps = new ArrayList<String>();
    private List<String> selectedNsr = new ArrayList<String>();
    private List<String> selectedPsd = new ArrayList<String>();
    private List<String> selectedNeshaps = new ArrayList<String>();
    private List<String> selectedMact = new ArrayList<String>();
    private List<String> selectedTv = new ArrayList<String>();
    private List<String> selectedSm = new ArrayList<String>();
    
    private int displayRows = getPageLimit();
    private boolean showDisplayAll = true;
    private boolean showDisplaySome = false;
    private int numRows = 0;
    private boolean firstTime = true;
    private boolean showExplain = true;
    
	private FullComplianceEvalService fullComplianceEvalService;

	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}
    
    public ComplianceSearch() {
        super();
    }

    public String start() {
        complianceList = new ArrayList<AirProgramCompliance>();
        hasSearchResults = false;
        if(firstTime) {
            reset();
            firstTime = false;
        }     
        return "complianceStatus.search";
    }
    
    public final String submitSearch() {
        complianceList = new ArrayList<AirProgramCompliance>();
        hasSearchResults = false;
        List<String> selectedC = selectedInspectClasses;
        if(selectedInspectClasses != null && selectedInspectClasses.size() == 0) {
            selectedC = null;
        }
 
        List<String> selectedSipList = selectedSip;
        if(selectedSip != null &&selectedSip.size() == 0) {
            selectedSipList = null;
        }
        List<String> selectedNspsList = selectedNsps;
        if(selectedNsps != null &&selectedNsps.size() == 0) {
            selectedNspsList = null;
        }
        List<String> selectedMactList = selectedMact;
        if(selectedMact != null &&selectedMact.size() == 0) {
            selectedMactList = null;
        }
        List<String> selectedNeshapsList = selectedNeshaps;
        if(selectedNeshaps != null &&selectedNeshaps.size() == 0) {
            selectedNeshapsList = null;
        }
        List<String> selectedTvList = selectedTv;
        if(selectedTv != null &&selectedTv.size() == 0) {
            selectedTvList = null;
        }
        List<String> selectedSmList = selectedSm;
        if(selectedSm != null &&selectedSm.size() == 0) {
            selectedSmList = null;
        }
        List<String> selectedPsdList = selectedPsd;
        if(selectedPsd != null &&selectedPsd.size() == 0) {
            selectedPsdList = null;
        }
        List<String> selectedNsrList = selectedNsr;
        if(selectedNsr != null &&selectedNsr.size() == 0) {
            selectedNsrList = null;
        }
        try {
            complianceList = getFullComplianceEvalService().complianceSearch(facilityId, facilityName,
                    operatingStatusCd, doLaaCd, countyCd, selectedC,
                       selectedSipList, selectedMactList, selectedTvList, selectedSmList,
                       selectedNeshapsList, selectedNspsList, selectedPsdList, selectedNsrList);
            hasSearchResults = true;  
        } catch (RemoteException e) {
            handleException(e);
        }
        return "complianceStatus.search";
    }

    public final String reset() {
    	facilityId = null;
        facilityName = null;
        operatingStatusCd = OperatingStatusDef.OP;
        doLaaCd = null;
        countyCd = null;
    	hasSearchResults = false;
        selectedInspectClasses.clear();
        allInspectClasses.clear();
/*        for(SelectItem si : InspectionClassDef.getData().getItems().getAllItems()) {
            if(!((String)si.getValue()).equals(InspectionClassDef.NON_HPF)) {
                selectedInspectClasses.add((String)si.getValue());
            }
            allInspectClasses.add(new SelectItem(si.getValue(), si.getLabel()));
        } */
        numInspectClasses = allInspectClasses.size();
        
        allCompliance.clear();
        selectedAll.clear();
        savedSelectedAll.clear();
        selectedSip.clear();
        selectedNsps.clear();
        selectedNsr.clear();
        selectedPsd.clear();
        selectedNeshaps.clear();
        selectedMact.clear();
        selectedTv.clear();
        selectedSm.clear();
        for(SelectItem si : ComplianceStatusDef.getData().getItems().getAllItems()) {
            if(si.getValue().equals("Y")) continue;  // leave out Yes
            allCompliance.add(new SelectItem(si.getValue(), si.getLabel()));
            if(si.getValue().equals("N")) noString = (String)si.getValue();
        }     
        numCompliance = allCompliance.size();
        selectedAll.add(noString);
        setSelectedAll(selectedAll);
    	return "complianceStatus.search";
    }
    
    public final String turnOn() {
        showExplain = true;
        return null;
    }
    
    public final String turnOff() {
        showExplain = false;
        return null;
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
    
    public void displaySomeRows() {
        displayRows = getPageLimit();
        showDisplayAll = true;
        showDisplaySome = false;
    }
    
    public void displayAllRows() {
        displayRows = 600;
        showDisplayAll = false;
        showDisplaySome = true;
    }
 
    public boolean isShowDisplayAll() {
        return showDisplayAll && numRows > getPageLimit();
    }

    public boolean isShowDisplaySome() {
        return showDisplaySome && numRows > getPageLimit();
    }

    public String getCountyCd() {
        return countyCd;
    }

    public void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public List<String> getSelectedInspectClasses() {
        return selectedInspectClasses;
    }

    public void setSelectedInspectClasses(List<String> selectedInspectClasses) {
        this.selectedInspectClasses = selectedInspectClasses;
    }

    public List<String> getSelectedNsps() {
        return selectedNsps;
    }

    public void setSelectedNsps(List<String> selectedNsps) {
        if(selectedNsps == null) this.selectedNsps = new ArrayList<String>();
        else this.selectedNsps = selectedNsps;
    }

    public List<String> getSelectedSip() {
        return selectedSip;
    }

    public void setSelectedSip(List<String> selectedSip) {
        if(selectedSip == null) this.selectedSip = new ArrayList<String>();
        else this.selectedSip = selectedSip;
    }

    public ArrayList<SelectItem> getAllCompliance() {
        return allCompliance;
    }

    public ArrayList<SelectItem> getAllInspectClasses() {
        return allInspectClasses;
    }

    public boolean isDisclosed() {
        return disclosed;
    }

    public int getDisplayRows() {
        return displayRows;
    }

    public int getNumCompliance() {
        return numCompliance;
    }

    public int getNumInspectClasses() {
        return numInspectClasses;
    }

    public List<String> getSelectedAll() {
        return selectedAll;
    }

    public void setSelectedAll(List<String> selectedAll) {
        if(selectedAll == null) this.selectedAll = new ArrayList<String>();
        else this.selectedAll = selectedAll;
        // what changed
        // Something added to list?
        String ss = null;;
        try {
        for(String s : this.selectedAll) {
            ss = s;
            if(!contains(savedSelectedAll, s)) {
                // s was added;  add it to all lists.
                if(!contains(selectedSip, s)) selectedSip.add(s);
                if(!contains(selectedNsps, s)) selectedNsps.add(s);
                if(!contains(selectedNsr, s)) selectedNsr.add(s);
                if(!contains(selectedPsd, s)) selectedPsd.add(s);
                if(!contains(selectedNeshaps, s)) selectedNeshaps.add(s);
                if(!contains(selectedMact, s)) selectedMact.add(s);
                if(!contains(selectedTv, s)) selectedTv.add(s);
                if(!contains(selectedSm, s)) selectedSm.add(s);
            }
        }
        } catch(Exception e) {
            DisplayUtil.displayError("error 1 " + ss);
        }
        // Something removed from list?
        try {
        for(String s : savedSelectedAll) {
            ss = s;
            if(!contains(this.selectedAll, s)) {
                // s was removed; remove it from all lists
                remove(selectedSip, s);
                remove(selectedNsps, s);
                remove(selectedNsr, s);
                remove(selectedPsd, s);
                remove(selectedNeshaps, s);
                remove(selectedMact, s);
                remove(selectedTv, s);
                remove(selectedSm, s);
            }
        }
        } catch(Exception e) {
            DisplayUtil.displayError("error 1 " + ss);
        }
        // Save a copy
        savedSelectedAll.clear();
        for(String s : this.selectedAll) {
            savedSelectedAll.add(s);   
        }
    }
    
    void remove(List<String> l, String s) {
        Iterator<String> i = l.iterator();
        while(i.hasNext()) {
            if(i.next().equals(s) ) i.remove();
        }
    }
    
    boolean contains(List<String> l, String s) {
        Iterator<String> i = l.iterator();
        while(i.hasNext()) {
            if(i.next().equals(s) ) return true;
        }
        return false;
    }

    public List<String> getSelectedMact() {
        return selectedMact;
    }

    public void setSelectedMact(List<String> selectedMact) {
        if(selectedMact == null) this.selectedMact = new ArrayList<String>();
        else this.selectedMact = selectedMact;
    }

    public List<String> getSelectedNeshaps() {
        return selectedNeshaps;
    }

    public void setSelectedNeshaps(List<String> selectedNeshaps) {
        if(selectedNeshaps == null) this.selectedNeshaps = new ArrayList<String>();
        else this.selectedNeshaps = selectedNeshaps;
    }

    public List<String> getSelectedNsr() {
        return selectedNsr;
    }

    public void setSelectedNsr(List<String> selectedNsr) {
        if(selectedNsr == null) this.selectedNsr = new ArrayList<String>();
        else this.selectedNsr = selectedNsr;
    }

    public List<String> getSelectedPsd() {
        return selectedPsd;
    }

    public void setSelectedPsd(List<String> selectedPsd) {
        if(selectedPsd == null) this.selectedPsd = new ArrayList<String>();
        else this.selectedPsd = selectedPsd;
    }

    public List<String> getSelectedSm() {
        return selectedSm;
    }

    public void setSelectedSm(List<String> selectedSm) {
        if(selectedSm == null) this.selectedSm = new ArrayList<String>();
        else this.selectedSm = selectedSm;
    }

    public List<String> getSelectedTv() {
        return selectedTv;
    }

    public void setSelectedTv(List<String> selectedTv) {
        if(selectedTv == null) this.selectedTv = new ArrayList<String>();
        else this.selectedTv = selectedTv;
    }

    public List<AirProgramCompliance> getComplianceList() {
        return complianceList;
    }

    public boolean isShowExplain() {
        return showExplain;
    }

    public String getOperatingStatusCd() {
        return operatingStatusCd;
    }

    public void setOperatingStatusCd(String operatingStatusCd) {
        this.operatingStatusCd = operatingStatusCd;
    } 


}
