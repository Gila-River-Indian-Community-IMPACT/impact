package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePbrDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtiDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtioDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceTvDetails;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class IssuanceDetailReport extends AppBase {
    private IssuancePtiDetails[] ptiDetails;
    private IssuancePtioDetails[] ptioDetails;
    private IssuanceTvDetails[] tvDetails;
    private IssuancePbrDetails[] pbrDetails;
    private String displayView = "Table";
    private boolean showList;
    private String doLaaName;
    private String type;
    private String permitType;
    private String issuanceType;
    private Timestamp startDt;
    private Timestamp endDt;
    private String dates;
    
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public IssuanceDetailReport() {
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

            //if ("TVPTI".equals(permitType))
            //    ptiDetails = getReportService().retrieveIssuancePtiDetails(startDt, endDt,
            //            doLaaName, issuanceType);
            //else 
        	if ("NSR".equals(permitType))
                ptioDetails = getReportService().retrieveIssuancePtioDetails(startDt, endDt,
                        doLaaName, issuanceType);
            else if ("TVPTO".equals(permitType))
                tvDetails = getReportService().retrieveIssuanceTvDetails(startDt, endDt,
                        doLaaName, issuanceType);
            //else if ("PBR".equals(permitType))
            //    pbrDetails = getReportService().retrieveIssuancePbrDetails(startDt, endDt,
            //            doLaaName);

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
    public final IssuancePtiDetails[] getPtiDetails() {
        return ptiDetails;
    }

    /**
     * @param ptiDetails
     */
    public final void setPtiDetails(IssuancePtiDetails[] ptiDetails) {
        this.ptiDetails = ptiDetails;
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

    /**
     * @return
     */
    public final IssuanceTvDetails[] getTvDetails() {
        return tvDetails;
    }

    /**
     * @param tvDetails
     */
    public final void setTvDetails(IssuanceTvDetails[] tvDetails) {
        this.tvDetails = tvDetails;
    }

    /**
     * @return
     */
    public final IssuancePbrDetails[] getPbrDetails() {
        return pbrDetails;
    }

    /**
     * @param pbrDetails
     */
    public final void setPbrDetails(IssuancePbrDetails[] pbrDetails) {
        this.pbrDetails = pbrDetails;
    }

    /**
     * @return
     */
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
        if (st.hasMoreTokens())
            issuanceType = st.nextToken();
        else
            issuanceType = null;

        this.type = type;
    }

    /**
     * @return
     */
    public final Timestamp getEndDt() {
        return endDt;
    }

    /**
     * @return
     */
    public final String getDates() {
        return dates;
    }

    /**
     * @param dates
     */
    public final void setDates(String dates) {
        this.dates = dates;
        StringTokenizer st = new StringTokenizer(dates, "%");

        String start = st.nextToken();
        this.startDt = Timestamp.valueOf(start);

        String end = null;
        if (st.hasMoreTokens()) {
            end = st.nextToken();
            this.endDt = Timestamp.valueOf(end);
        }
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

    /**
     * @return
     */
    public final String getParams() {
        return dates;
    }

    /**
     * @param params
     */
    public final void setParams(String params) {
        StringTokenizer st = new StringTokenizer(params, "/");

        String token = st.nextToken();
        setType(token);
        token = st.nextToken();
        setDoLaaName(token);
        token = st.nextToken();
        setDates(token);

        submit();
    }
}
