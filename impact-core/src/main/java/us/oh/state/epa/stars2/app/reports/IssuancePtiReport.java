package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtioDetails;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class IssuancePtiReport extends AppBase {
    private IssuancePtioDetails[] ptioDetails;
    private String displayView = "Table";
    private boolean showList;
    private String doLaaName;
    private String type;
    private String permitType;
    private String issuanceType;
    private Timestamp startDt;
    private Timestamp endDt;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public IssuancePtiReport() {
        super();
        reset();
    }

    /**
     * @return
     */
    public final String getDisplayView() {
        return displayView;
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
            ptioDetails = getReportService().retrieveIssuancePtioDetails(startDt, endDt,
                    permitType, issuanceType);

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

        displayView = "Table";
        return SUCCESS;
    }

    /**
     * @return
     */
    public final IssuancePtioDetails[] getPtioDetails() {
        return ptioDetails;
    }

    /**
     * @param ptioDetails
     */
    public final void setPtioDetails(IssuancePtioDetails[] ptioDetails) {
        this.ptioDetails = ptioDetails;
    }

    public final String getDoLaaName() {
        return doLaaName;
    }

    /**
     * @param doLaaName
     */
    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    /**
     * @return
     */
    public final String getType() {
        return type;
    }

    /**
     * @param type
     */
    public final void setType(String type) {
        reset();

        StringTokenizer st = new StringTokenizer(type, ".");

        permitType = st.nextToken();
        issuanceType = st.nextToken();

        this.type = type;
    }

    /**
     * @return
     */
    public final Timestamp getEndDt() {
        return endDt;
    }

    /**
     * @param endDt
     */
    public final void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
    }

    /**
     * @return
     */
    public final Timestamp getStartDt() {
        return startDt;
    }

    /**
     * @param startDt
     */
    public final void setStartDt(Timestamp startDt) {
        this.startDt = startDt;
    }
}
