package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitSOPData;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class PreliminaryCompleted extends AppBase {

    private List<String> selectedDoLaas = new ArrayList<String>();
    private List<String> selectedPermitTypes = new ArrayList<String>();
    private List<String> selectedReasonCds = new ArrayList<String>();
    
    private String doLaaCd;
    private String permitTypeCd;
    private String reasonCd;
    private Timestamp fromDate;
    private Timestamp toDate;
    
    private PermitSOPData[] details;
    private boolean hasSearchResults;
    private boolean showChartResults;
    private String hideShowTitle = "Show Charts";
    private boolean showList;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public PreliminaryCompleted() {
        super();
        reset();
    }
    
    public final String submit() {

        try {
            details = getReportService().retrievePreliminaryReviewCompletedData(
                    selectedDoLaas, selectedPermitTypes, selectedReasonCds, 
                    fromDate, toDate);

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

    public final String reset() {
        details = null;
        hasSearchResults = false;
        showChartResults = false;
        hideShowTitle = "Show Charts";

        selectedDoLaas = null;
        selectedPermitTypes = null;
        selectedReasonCds = null;
        fromDate = null;
        toDate = null;
        
        return SUCCESS;
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
    
    public final PermitSOPData[] getDetails() {
        return details;
    }

    public final void setDetails(PermitSOPData[] details) {
        this.details = details;
    }

    /**
     * @return the fromDate
     */
    public final Timestamp getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public final void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public final Timestamp getToDate() {
        return toDate;
    }

    /**
     * @param toDate the toDate to set
     */
    public final void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

}
