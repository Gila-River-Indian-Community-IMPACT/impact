package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceDetails;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

/**
 * @author Kbradley
 * 
 */
public class IssuanceReportSearch extends AppBase {
    private Timestamp fromDate;
    private Timestamp toDate;
    private IssuanceDetails[] details;
    private boolean hasSearchResults;
    private boolean hasSummaryResults;
    private boolean hasPtiResults;
    private boolean hasPtioResults;
    private boolean hasPbrResults;
    private boolean hasTvResults;
    private boolean hasTotalResults;
    private boolean hasGeneralResults;
    private boolean hasDolaaResults;
    private boolean hasReasonResults;
    private boolean hasFinalResults;
    private String displayView = "Table";
    private boolean showList;
    private String doLaa;
    private String dates;
    
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public IssuanceReportSearch() {
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
    
    public final boolean isHasSearchResultsOnly() {
        boolean ret = false;
        
        if (hasSearchResults) {
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
            if (count == 0) {
                doLaa = token;
            }
            if ("Pti".equalsIgnoreCase(token)) {
                hasPtiResults = true;
            }
            if ("Ptio".equalsIgnoreCase(token)) {
                hasPtioResults = true;
            }
            if ("Pbr".equalsIgnoreCase(token)) {
                hasPbrResults = true;
            }
            if ("Tv".equalsIgnoreCase(token)) {
                hasTvResults = true;
            }
            if ("Total".equalsIgnoreCase(token)) 
                hasTotalResults = true;
            if ("General".equalsIgnoreCase(token))
                hasGeneralResults = true;
            if ("Dolaa".equalsIgnoreCase(token))
                hasDolaaResults = true;
            if ("Reason".equalsIgnoreCase(token))
                hasReasonResults = true;
            if ("Final".equalsIgnoreCase(token))
                hasFinalResults = true;
            count++;
        }

        this.displayView = displayView;
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
        if (fromDate == null || toDate == null){
            DisplayUtil.displayError("Please enter required data.");
        } else {
            try {
                details = getReportService().retrieveIssuanceDetails(fromDate, toDate);
                hasSearchResults = true;
            } catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil
                        .displayError("System error. Please contact system administrator");
            }
        }

        return SUCCESS;
    }

    /**
     * @return
     */
    public final String reset() {
        details = null;
        fromDate = null;
        toDate = null;
        hasSearchResults = false;
        hasSummaryResults = false;
        hasPtiResults = false;
        hasPtioResults = false;
        hasTvResults = false;
        hasPbrResults = false;
        hasTotalResults = false;

        hasDolaaResults = false;
        hasGeneralResults = false;
        hasReasonResults = false;
        hasFinalResults = false;

        displayView = "Table";

        dates = null;

        return SUCCESS;
    }

    /**
     * @return
     */
    public final String refresh() {
        displayView = "Table";
        hasSummaryResults = false;
        hasPtiResults = false;
        hasPtioResults = false;
        hasTvResults = false;
        hasPbrResults = false;
        hasTotalResults = false;
        hasDolaaResults = false;
        hasGeneralResults = false;
        hasReasonResults = false;
        hasFinalResults = false;
        return SUCCESS;
    }

    /**
     * @return
     */
    public final Timestamp getToDate() {
        return toDate;
    }

    /**
     * @param toDate
     */
    public final void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

    /**
     * @return
     */
    public final Timestamp getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate
     */
    public final void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return
     */
    public final IssuanceDetails[] getDetails() {
        return details;
    }

    /**
     * @param details
     */
    public final void setDetails(IssuanceDetails[] details) {
        this.details = details;
    }

    /**
     * @return
     */
    public final String getDates() {
        dates = fromDate.toString() + "%" + toDate.toString();
        return dates;
    }

    /**
     * @param dates
     */
    public final void setDates(String dates) {
        this.dates = dates;
    }

    public final String getDrillDown() {

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String sDate = df.format(fromDate);
        String eDate = df.format(toDate);

        return "DO/LAA: " + doLaa + ", Dates: From " + sDate + " To " + eDate;
    }
}
