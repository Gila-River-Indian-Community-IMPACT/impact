package us.oh.state.epa.stars2.portal.reports;
//
//import java.rmi.RemoteException;
//import java.sql.Timestamp;
//import java.util.StringTokenizer;
//
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceDetails;
import us.oh.state.epa.stars2.webcommon.AppBase;
//import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class IssuanceReportSearch extends AppBase {
//    private Timestamp fromDate;
//    private Timestamp toDate;
//    private IssuanceDetails[] details;
//    private boolean hasSearchResults;
//    private boolean hasSummaryResults;
//    private boolean hasPtiResults;
//    private boolean hasPtioResults;
//    private boolean hasPbrResults;
//    private boolean hasTvResults;
//    private boolean hasTotalResults;
//    private boolean hasGeneralResults;
//    private boolean hasDolaaResults;
//    private boolean hasReasonResults;
//    private boolean hasFinalResults;
//    private String displayView = "Table";
//    private boolean showList;
//    private String dates;
//
//    public IssuanceReportSearch() {
//        super();
        // reset();
//    }
//
//    public final boolean isHasSearchResults() {
//        if (hasSearchResults && displayView.equals("Table"))
//            return true;
//
//        return false;
//    }
//
//    public final void setHasSearchResults(boolean hasSearchResults) {
//        this.hasSearchResults = hasSearchResults;
//    }
//
//    public final boolean isHasSummaryResults() {
//        return hasSummaryResults;
//    }
//
//    public final void setHasSummaryResults(boolean hasSummaryResults) {
//        this.hasSummaryResults = hasSummaryResults;
//    }
//
//    public final boolean isHasPbrResults() {
//        return hasPbrResults && hasSearchResults;
//    }
//
//    public final void setHasPbrResults(boolean hasPbrResults) {
//        this.hasPbrResults = hasPbrResults;
//    }
//
//    public final boolean isHasPtioResults() {
//        return hasPtioResults && hasSearchResults;
//    }
//
//    public final void setHasPtioResults(boolean hasPtioResults) {
//        this.hasPtioResults = hasPtioResults;
//    }
//
//    public final boolean isHasPtiResults() {
//        return hasPtiResults && hasSearchResults;
//    }
//
//    public final void setHasPtiResults(boolean hasPtiResults) {
//        this.hasPtiResults = hasPtiResults;
//    }
//
//    public final boolean isHasTvResults() {
//        return hasTvResults;
//    }
//
//    public final void setHasTvResults(boolean hasTvResults) {
//        this.hasTvResults = hasTvResults;
//    }
//
//    public final boolean isHasTotalResults() {
//        return hasTotalResults && hasSearchResults;
//    }
//
//    public final void setHasTotalResults(boolean hasTotalResults) {
//        hasPbrResults = hasSearchResults && displayView.equals("Pbr");
//        this.hasTotalResults = hasTotalResults;
//    }
//
//    public final boolean isHasGeneralResults() {
//        return hasGeneralResults && hasSearchResults;
//    }
//
//    public final void setHasGeneralResults(boolean hasGeneralResults) {
//        this.hasGeneralResults = hasGeneralResults;
//    }
//
//    public final boolean isHasDolaaResults() {
//        return hasDolaaResults && hasSearchResults;
//    }
//
//    public final void setHasDolaaResults(boolean hasDolaaResults) {
//        this.hasDolaaResults = hasDolaaResults;
//    }
//
//    public final boolean isHasReasonResults() {
//        return hasReasonResults && hasSearchResults;
//    }
//
//    public final void setHasReasonResults(boolean hasReasonResults) {
//        this.hasReasonResults = hasReasonResults;
//    }
//
//    public final boolean isHasFinalResults() {
//        return hasFinalResults && hasSearchResults;
//    }
//
//    public final void setHasFinalResults(boolean hasFinalResults) {
//        this.hasFinalResults = hasFinalResults;
//    }
//
//    public final String getDisplayView() {
//        return displayView;
//    }
//
//    public final void setDisplayView(String displayView) {
//        StringTokenizer st = new StringTokenizer(displayView, ".");
//        // reset();
//        hasSummaryResults = true;
//
//        while (st.hasMoreTokens()) {
//            String token = st.nextToken();
//            if ("Pti".equals(token))
//                hasPtiResults = true;
//            if ("Ptio".equals(token))
//                hasPtioResults = true;
//            if ("Pbr".equals(token))
//                hasPbrResults = true;
//            if ("Tv".equals(token))
//                hasTvResults = true;
//            if ("Total".equals(token))
//                hasTotalResults = true;
//            if ("General".equals(token))
//                hasGeneralResults = true;
//            if ("Dolaa".equals(token))
//                hasDolaaResults = true;
//            if ("Reason".equals(token))
//                hasReasonResults = true;
//            if ("Final".equals(token))
//                hasFinalResults = true;
//        }
//
//        this.displayView = displayView;
//    }
//
//    public final boolean isShowList() {
//        return showList;
//    }
//
//    public final void setShowList(boolean showList) {
//        this.showList = showList;
//    }
//
//    public final String submit() {
//        try {
//            details = reportBO().retrieveIssuanceDetails(fromDate, toDate);
//            hasSearchResults = true;
//        } catch (RemoteException re) {
//            logger.error(re.getMessage(), re);
//            DisplayUtil.displayError("System error. Please contact system administrator");
//        }
//
//        return SUCCESS;
//    }
//
//    public final String reset() {
//        details = null;
//        fromDate = null;
//        toDate = null;
//        hasSearchResults = false;
//        hasSummaryResults = false;
//        hasPtiResults = false;
//        hasPtioResults = false;
//        hasTvResults = false;
//        hasPbrResults = false;
//        hasTotalResults = false;
//
//        hasDolaaResults = false;
//        hasGeneralResults = false;
//        hasReasonResults = false;
//        hasFinalResults = false;
//
//        displayView = "Table";
//        return SUCCESS;
//    }
//
//    public final String refresh() {
//        displayView = "Table";
//        hasSummaryResults = false;
//        hasPtiResults = false;
//        hasPtioResults = false;
//        hasTvResults = false;
//        hasPbrResults = false;
//        hasTotalResults = false;
//        hasDolaaResults = false;
//        hasGeneralResults = false;
//        hasReasonResults = false;
//        hasFinalResults = false;
//        return SUCCESS;
//    }
//
//    public final Timestamp getToDate() {
//        return toDate;
//    }
//
//    public final void setToDate(Timestamp toDate) {
//        this.toDate = toDate;
//    }
//
//    public final Timestamp getFromDate() {
//        return fromDate;
//    }
//
//    public final void setFromDate(Timestamp fromDate) {
//        this.fromDate = fromDate;
//    }
//
//    public final IssuanceDetails[] getDetails() {
//        return details;
//    }
//
//    public final void setDetails(IssuanceDetails[] details) {
//        this.details = details;
//    }
//
//    public final String getDates() {
//        dates = fromDate.toString() + "%" + toDate.toString();
//        return dates;
//    }
//
//    public final void setDates(String dates) {
//        this.dates = dates;
//    }
}
