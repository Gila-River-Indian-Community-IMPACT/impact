package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtioDetails;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceSptoDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceTvDetails;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class PermitExpirationDetailReport extends AppBase {
    //private IssuanceSptoDetails[] sptoDetails;
    private IssuancePtioDetails[] ptioDetails;
    private IssuanceTvDetails[] tvDetails;
    private String displayView = "Table";
    private boolean showList;
    private String doLaaName;
    private String type;
    private String permitType;
    private String issuanceType;
    private Timestamp startDt;
    private Timestamp endDt;
    private boolean hideShutdownFacility;
    private boolean hideExemptPBR;
    private boolean hideEUPermitStatusTRS;
    private boolean hideEUExemptionDmPe;
    private boolean hideEUShutdownInvalid;
    private boolean hidePtoPtioEuActivePBR;
    private String dates;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public PermitExpirationDetailReport() {
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

            //if ("SPTO".equals(permitType))
            //	sptoDetails = getReportService().retrieveExpiredSptoDetails(startDt, endDt,
            //            doLaaName, hideShutdownFacility,hideExemptPBR,hideEUPermitStatusTRS,
            //            hideEUExemptionDmPe,hideEUShutdownInvalid,hidePtoPtioEuActivePBR, issuanceType);
            //else 
        	if ("NSR".equals(permitType))
                ptioDetails = getReportService().retrieveExpiredPtioDetails(startDt, endDt,
                        doLaaName, hideShutdownFacility,hideExemptPBR,hideEUPermitStatusTRS,
                        hideEUExemptionDmPe,hideEUShutdownInvalid,hidePtoPtioEuActivePBR, issuanceType);
            else if ("TVPTO".equals(permitType))
                tvDetails = getReportService().retrieveExpiredTvDetails(startDt, endDt,
                        doLaaName,hideShutdownFacility,hideExemptPBR,hideEUPermitStatusTRS,
                        hideEUExemptionDmPe,hideEUShutdownInvalid,hidePtoPtioEuActivePBR, issuanceType);

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
    //public final IssuanceSptoDetails[] getSptoDetails() {
    //    return sptoDetails;
    //}

    /**
     * @param ptiDetails
     */
    //public final void setSptoDetails(IssuanceSptoDetails[] sptoDetails) {
    //    this.sptoDetails = sptoDetails;
    //}

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

    public final boolean isHideShutdownFacility() {
		return hideShutdownFacility;
	}

	public final void setHideShutdownFacility(boolean hideShutdownFacility) {
		this.hideShutdownFacility = hideShutdownFacility;
	}

	public final boolean isHideExemptPBR() {
		return hideExemptPBR;
	}

	public final void setHideExemptPBR(boolean hideExemptPBR) {
		this.hideExemptPBR = hideExemptPBR;
	}

	public final boolean isHideEUPermitStatusTRS() {
		return hideEUPermitStatusTRS;
	}

	public final void setHideEUPermitStatusTRS(boolean hideEUPermitStatusTRS) {
		this.hideEUPermitStatusTRS = hideEUPermitStatusTRS;
	}

	public final boolean isHideEUExemptionDmPe() {
		return hideEUExemptionDmPe;
	}

	public final void setHideEUExemptionDmPe(boolean hideEUExemptionDmPe) {
		this.hideEUExemptionDmPe = hideEUExemptionDmPe;
	}

	public final boolean isHideEUShutdownInvalid() {
		return hideEUShutdownInvalid;
	}

	public final void setHideEUShutdownInvalid(boolean hideEUShutdownInvalid) {
		this.hideEUShutdownInvalid = hideEUShutdownInvalid;
	}

	public final boolean isHidePtoPtioEuActivePBR() {
		return hidePtoPtioEuActivePBR;
	}

	public final void setHidePtoPtioEuActivePBR(boolean hidePtoPtioEuActivePBR) {
		this.hidePtoPtioEuActivePBR = hidePtoPtioEuActivePBR;
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

    public final String getIssuanceType() {
        return issuanceType;
    }

    public final void setIssuanceType(String issuanceType) {
        this.issuanceType = issuanceType;
    }
}
