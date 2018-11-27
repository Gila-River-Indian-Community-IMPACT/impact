package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitSOPData;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitSOPTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class PermitSOP extends AppBase {

    protected String doLaaCd;
    protected String doLaaShortDsc;
    protected List<String> selectedDoLaas = new ArrayList<String>();
    protected String permitTypeCd;
    protected List<String> selectedPermitTypes = new ArrayList<String>();
    protected String reasonCd;
    protected List<String> selectedReasonCds = new ArrayList<String>();
    protected PermitSOPData[] details;
    protected boolean hasSearchResults;
    protected boolean showChartResults;
    
    // display only flags
    protected boolean general;
    protected boolean express;
    protected boolean backlogged;
    protected boolean showAll;
    protected String hideShowTitle = "Show Charts";
    protected boolean showList;
    
    // This is used for Preliminary/CO
    protected String type = PermitSOPTypeDef.ALL;
    
    // exclude flags
    private boolean hideShutdownFacility;
    private boolean hideDeadEndedPermit;
    private boolean hideShutDownInvalidEU;
    
    // include flag
    private boolean showNotes;
    private boolean showNaics = false;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
    
    public PermitSOP() {
        super();
        reset();
    }

    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final boolean isShowChartResults() {
        return showChartResults && hasSearchResults;
    }

    public final void setHasChartResults(boolean showChartResults) {
        this.showChartResults = showChartResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    public final String getHideShowTitle() {
        return hideShowTitle;
    }

    public final void setHideShowTitle(String hideShowTitle) {
        this.hideShowTitle = hideShowTitle;
    }

    public final boolean isShowList() {
        return showList;
    }

    public final void setShowList(boolean showList) {
        this.showList = showList;
    }
    
    public final String getReviewerHeaderText(){
        if (type != null && type.equals(PermitSOPTypeDef.CO))
            return "CO Technical Reviewer";
        
        return "DO/LAA Technical Reviewer";
    }

    public String submit() {

        try {
            details = getReportService().retrievePermitSOPData(
                    selectedDoLaas, selectedPermitTypes, selectedReasonCds,
                    general, express, hideShutdownFacility, hideDeadEndedPermit, hideShutDownInvalidEU, 
                    backlogged, showAll, type, showNotes);

            hasSearchResults = true;
            showChartResults = false;
            hideShowTitle = "Show Charts";
        } 
        catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error: " + re.getMessage());
        }

        return SUCCESS;
    }

    public final String submitChart() {

        if (showChartResults) {
            showChartResults = false;
            hideShowTitle = "Show Charts";
        } else {
            showChartResults = true;
            hideShowTitle = "Hide Charts";
        }

        return SUCCESS;
    }

    public String reset() {
        details = null;
        hasSearchResults = false;
        showChartResults = false;
        hideShowTitle = "Show Charts";
        express = false;
        general = false;
        showAll = true;
        showNaics = false;
        backlogged = false;
        

        hideShutdownFacility = false;
        hideDeadEndedPermit = false;
        hideShutDownInvalidEU = false;

        selectedDoLaas = null;
        selectedPermitTypes = null;
        selectedReasonCds = null;
        
        type = null;

        return SUCCESS;
    }
    
    public final boolean isExpressSelected(){
        return express;
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

    public final List<String> getSelectedDoLaas() {
        return selectedDoLaas;
    }

    public final void setSelectedDoLaas(List<String> selectedDoLaas) {
        this.selectedDoLaas = selectedDoLaas;
    }

    public final String getPermitTypeCd() {
        return permitTypeCd;
    }

    public final void setPermitTypeCd(String permitTypeCd) {
        this.permitTypeCd = permitTypeCd;
    }

    public final List<SelectItem> getPermitTypes() {
        return getPermitTypes(getPermitTypeCd());
    }

    public final String getReasonCd() {
        return reasonCd;
    }

    public final void setReasonCd(String reasonCd) {
        this.reasonCd = reasonCd;
    }

    public final List<SelectItem> getReasonCds() {
        //
        // Note: We are getting all Permit reason codes. Multiple Permits types
        // can be selected and there is no way we can restrict the reasons
        // displayed by permit type (at least very easily)
        return PermitReasonsDef.getAllPermitReasons();
    }

    public final List<String> getSelectedReasonCds() {
        return selectedReasonCds;
    }

    public final void setShowChartResults(boolean showChartResults) {
        this.showChartResults = showChartResults;
    }

    public final List<String> getSelectedPermitTypes() {
        return selectedPermitTypes;
    }

    public final void setSelectedPermitTypes(List<String> selectedPermitTypes) {
        this.selectedPermitTypes = selectedPermitTypes;
    }

    public final void setSelectedReasonCds(List<String> selectedReasonCds) {
        this.selectedReasonCds = selectedReasonCds;
        if (!isBackloggedEnabled()) {
        	backlogged = false;
        }
    }

    public final PermitSOPData[] getDetails() {
        return details;
    }

    public final void setDetails(PermitSOPData[] details) {
        this.details = details;
    }

    public final boolean isExpress() {
        return express;
    }

    public final void setExpress(boolean express) {
        this.express = express;
    }

    public final boolean isGeneral() {
        return general;
    }

    public final void setGeneral(boolean general) {
        this.general = general;
    }

    public final boolean isShowAll() {
        return showAll;
    }

    public final void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    /**
     * @return the type
     */
    public final String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public final void setType(String type) {
        this.type = type;
    }

    /**
     * @return the types
     */
    public final List<SelectItem> getTypes() {
        return PermitSOPTypeDef.getData().getItems().getAllItems();
    }
    

    /**
     * @return the showNotes
     */
    public final boolean isShowNotes() {
        return showNotes;
    }

    /**
     * @param showNotes the showNotes to set
     */
    public final void setShowNotes(boolean showNotes) {
        this.showNotes = showNotes;
    }

    public final boolean isShowNaics() {
		return showNaics;
	}

	public final void setShowNaics(boolean showNaics) {
		this.showNaics = showNaics;
	}

	public final String getWidth(){
        String ret = "1100";
        if (showNotes)
            ret = "2000";
        return ret;
    }

	public final boolean isBacklogged() {
		return backlogged;
	}

	public final void setBacklogged(boolean backlogged) {
		this.backlogged = backlogged;
	}

	public final boolean isHideShutdownFacility() {
		return hideShutdownFacility;
	}

	public final void setHideShutdownFacility(boolean hideShutdownFacility) {
		this.hideShutdownFacility = hideShutdownFacility;
	}

	public final boolean isHideDeadEndedPermit() {
		return hideDeadEndedPermit;
	}

	public final void setHideDeadEndedPermit(boolean hideDeadEndedPermit) {
		this.hideDeadEndedPermit = hideDeadEndedPermit;
	}

	public final boolean isHideShutDownInvalidEU() {
		return hideShutDownInvalidEU;
	}

	public final void setHideShutDownInvalidEU(boolean hideShutDownInvalidEU) {
		this.hideShutDownInvalidEU = hideShutDownInvalidEU;
	}
	
	public final boolean isBackloggedEnabled() {
		boolean enabled = false;
		// for now, the backlogged choice should only be enabled when renewal is the only
		// permit reason selected. This could change.
		if (selectedReasonCds != null && selectedReasonCds.size() == 1 &&
				selectedReasonCds.contains(PermitReasonsDef.RENEWAL)) {
			enabled = true;
		}
		return enabled;
	}

    public String getDoLaaShortDsc() {
        return doLaaShortDsc;
    }

    public void setDoLaaShortDsc(String doLaaShortDsc) {
        this.doLaaShortDsc = doLaaShortDsc;
    }
}
