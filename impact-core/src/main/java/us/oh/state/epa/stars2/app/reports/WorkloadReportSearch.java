package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.report.WorkloadDetails;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class WorkloadReportSearch extends AppBase {
    private String doLaaCd;
    private List<String> doLaas = new ArrayList<String>();
    private List<String> selectedDoLaas = new ArrayList<String>();
    private WorkloadDetails[] details;
    private boolean hasSearchResults;
    private boolean hasSummaryResults;
    private boolean hasPtiResults;
    private boolean hasPtioResults;
    private boolean hasPbrResults;
    private boolean hasTivResults;
    private boolean hasTvResults;
    private boolean hasTotalResults;
    private boolean hasGeneralResults;
    private boolean hasDolaaResults;
    private boolean hasReasonResults;
    private boolean hasFinalResults;
    private String displayView = "Table";
    private boolean showList;
    private String doLaaIn = new String(" ");
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public WorkloadReportSearch() {
        super();
        reset();
    }

    /**
     * @return
     */
    public final boolean isHasSearchResults() {
        boolean ret = false;

        if (hasSearchResults && displayView.equals("Table")) {
            ret = true;
        }

        return ret;
    }

    /**
     * @param hasSearchResults
     */
    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    /**
     * @return
     */
    public final boolean isHasSummaryResults() {
        return hasSummaryResults;
    }

    /**
     * @param hasSummaryResults
     */
    public final void setHasSummaryResults(boolean hasSummaryResults) {
        this.hasSummaryResults = hasSummaryResults;
    }

    /**
     * @return
     */
    public final boolean isHasPbrResults() {
        return hasPbrResults && hasSearchResults;
    }

    /**
     * @param hasPbrResults
     */
    public final void setHasPbrResults(boolean hasPbrResults) {
        this.hasPbrResults = hasPbrResults;
    }

    /**
     * @return
     */
    public final boolean isHasPtioResults() {
        return hasPtioResults && hasSearchResults;
    }

    /**
     * @param hasPtioResults
     */
    public final void setHasPtioResults(boolean hasPtioResults) {
        this.hasPtioResults = hasPtioResults;
    }

    /**
     * @return
     */
    public final boolean isHasPtiResults() {
        return hasPtiResults && hasSearchResults;
    }

    /**
     * @param hasPtiResults
     */
    public final void setHasPtiResults(boolean hasPtiResults) {
        this.hasPtiResults = hasPtiResults;
    }

    public final boolean isHasTivResults() {
        return hasTivResults;
    }

    public final void setHasTivResults(boolean hasTivResults) {
        this.hasTivResults = hasTivResults;
    }

    /**
     * @return
     */
    public final boolean isHasTvResults() {
        return hasTvResults;
    }

    /**
     * @param hasTvResults
     */
    public final void setHasTvResults(boolean hasTvResults) {
        this.hasTvResults = hasTvResults;
    }

    /**
     * @return
     */
    public final boolean isHasTotalResults() {
        return hasTotalResults && hasSearchResults;
    }

    /**
     * @param hasTotalResults
     */
    public final void setHasTotalResults(boolean hasTotalResults) {
        hasPbrResults = hasSearchResults && displayView.equals("Pbr");
        this.hasTotalResults = hasTotalResults;
    }

    /**
     * @return
     */
    public final boolean isHasGeneralResults() {
        return hasGeneralResults && hasSearchResults;
    }

    /**
     * @param hasGeneralResults
     */
    public final void setHasGeneralResults(boolean hasGeneralResults) {
        this.hasGeneralResults = hasGeneralResults;
    }

    /**
     * @return
     */
    public final boolean isHasDolaaResults() {
        return hasDolaaResults && hasSearchResults;
    }

    /**
     * @param hasDolaaResults
     */
    public final void setHasDolaaResults(boolean hasDolaaResults) {
        this.hasDolaaResults = hasDolaaResults;
    }

    /**
     * @return
     */
    public final boolean isHasReasonResults() {
        return hasReasonResults && hasSearchResults;
    }

    /**
     * @param hasReasonResults
     */
    public final void setHasReasonResults(boolean hasReasonResults) {
        this.hasReasonResults = hasReasonResults;
    }

    /**
     * @return
     */
    public final boolean isHasFinalResults() {
        return hasFinalResults && hasSearchResults;
    }

    /**
     * @param hasFinalResults
     */
    public final void setHasFinalResults(boolean hasFinalResults) {
        this.hasFinalResults = hasFinalResults;
    }

    /**
     * @return
     */
    public final String getDisplayView() {
        return displayView;
    }

    /**
     * @param displayView
     */
    public final void setDisplayView(String displayView) {
        StringTokenizer st = new StringTokenizer(displayView, ".");
        // reset();
        hasSummaryResults = true;

        int count = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            
            if (count == 1) {
                hasDolaaResults = true;
                doLaaCd = token;
            }
            if ("Pti".equals(token)) {
                hasPtiResults = true;
                hasTotalResults = false;
            }
            if ("Ptio".equals(token)) {
                hasPtioResults = true;
                hasTotalResults = false;
            }
            if ("Pbr".equals(token)) {
                hasPbrResults = true;
                hasTotalResults = false;
            }
            if ("Tiv".equals(token)) {
                hasTivResults = true;
                hasTotalResults = false;
            }
            if ("Tv".equals(token)) {
                hasTvResults = true;
                hasTotalResults = false;
            } 
            if ("Total".equals(token)) {
                hasTotalResults = true;
            }
            if ("General".equals(token)) {
                hasGeneralResults = true;
            }
            if ("Dolaa".equals(token)) {
                hasDolaaResults = true;
            }
            if ("Reason".equals(token)) {
                hasReasonResults = true;
            }
            if ("Final".equals(token)) {
                hasFinalResults = true;
            }
            count++;
        }

        this.displayView = displayView;
    }

    /**
     * @return
     */
    public final String getDrillDown() {

        String drillDown = "";

        if (displayView != null) {
            int count = 0;
            StringTokenizer st = new StringTokenizer(displayView, ".");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                /*if (count == 0) {
                    drillDown = "DO/LAA: ";
                    if (token == null || token.length() < 1
                        || "All".equals(token)) {
                        boolean isFirst = true;
                        if (selectedDoLaas == null) {
                            drillDown += "All";
                        }
                        else {
                            for (String dl : selectedDoLaas) {
                                if (!isFirst) {
                                    drillDown += ", ";
                                }
                                drillDown += dl;
                                isFirst = false;
                            }
                        }
                    }
                    else {
                        drillDown += token;
                    }
                }
                else*/ if (count == 1) {
                    if (token == null || token.length() < 1) {
                        drillDown += "Task: All";
                    }
                    else {
                        drillDown += "Task: " + token;
                    }
                }
                else if (count == 2) {
                    if ("Pti".equals(token)) {
                        drillDown += ", Permit Type: PTI";
                    }
                    if ("Ptio".equals(token)) {
                        drillDown += ", Permit Type: NSR";
                    }
                    if ("Pbr".equals(token)) {
                        drillDown += ", Permit Type: PBR";
                    }
                    if ("Tv".equals(token)) {
                        drillDown += ", Permit Type: Title V";
                    }
                    if ("Total".equals(token)) {
                        drillDown += ", Permit Type: All";
                    }
                }
                else if (count == 3) {
                    drillDown += ", Stage: " + token;
                }
                else if (count == 4) {
                    if ("IP".equals(token)) {
                        drillDown += ", In Progress";
                    }
                    if ("NC".equals(token)) {
                        drillDown += ", Not Completed";
                    }
                    if ("Total".equals(token)) {
                        drillDown = drillDown + ", Totals";
                    }
                }
                count++;
            }
        }
        return drillDown;
    }

    /**
     * @return
     */
    public final boolean isShowList() {
        return showList;
    }

    /**
     * @param showList
     */
    public final void setShowList(boolean showList) {
        this.showList = showList;
    }

    /**
     * @return
     */
    public final String submit() {
        try {
            details = getReportService().retrieveWorkloadDetails(selectedDoLaas, "IP,RF");
            hasSearchResults = true;
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        return SUCCESS;
    }

    /**
     * @return
     */
    public final String reset() {
        details = null;
        hasSearchResults = false;
        hasSummaryResults = false;
        hasPtiResults = false;
        hasPtioResults = false;
        hasTivResults = false;
        hasTvResults = false;
        hasTotalResults = false;

        hasDolaaResults = false;
        hasGeneralResults = false;
        hasReasonResults = false;
        hasFinalResults = false;
        selectedDoLaas = null;
        doLaaIn = new String(" ");

        displayView = "Table";
        return SUCCESS;
    }

    /**
     * @return
     */
    public final String refresh() {
        hasSummaryResults = false;
        hasPtiResults = false;
        hasPtioResults = false;
        hasTivResults = false;
        hasTvResults = false;
        hasTotalResults = false;

        hasDolaaResults = false;
        hasGeneralResults = false;
        hasReasonResults = false;
        hasFinalResults = false;

        displayView = "Table";
        return SUCCESS;
    }

    /**
     * @return
     */
    public final String getDoLaaCd() {
        return doLaaCd;
    }

    /**
     * @param doLaaCd
     */
    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    /**
     * @return
     */
    public final WorkloadDetails[] getDetails() {
        return details;
    }

    /**
     * @param details
     */
    public final void setDetails(WorkloadDetails[] details) {
        this.details = details;
    }

    /**
     * @return
     */
    public final List<SelectItem> getDoLaas() {
        return DoLaaDef.getData().getItems().getAllSearchItems();
    }

    /**
     * @param dolaas
     */
    public final void setDoLaas(List<String> dolaas) {
        this.doLaas = dolaas;
    }

    /**
     * @return
     */
    public final List<String> getSelectedDoLaas() {
        return selectedDoLaas;
    }

    /**
     * @param selectedDoLaas
     */
    public final void setSelectedDoLaas(List<String> selectedDoLaas) {
        this.selectedDoLaas = selectedDoLaas;

        //
        // If nothing is selected - default to all
        //
        if (selectedDoLaas == null) {
            this.selectedDoLaas = doLaas;
        }

        if (selectedDoLaas != null) {
            StringBuffer buffer = new StringBuffer("");
            for (String doLaaCd : selectedDoLaas) {
                buffer.append(doLaaCd + ",");
            }
            buffer.replace(buffer.length() - 1, buffer.length(), " ");
            doLaaIn = buffer.toString();
        }
    }

    /**
     * @return
     */
    public final String getDoLaaIn() {
        return doLaaIn;
    }

    /**
     * @param doLaaIn
     */
    public final void setDoLaaIn(String doLaaIn) {
        this.doLaaIn = doLaaIn;
    }
}
