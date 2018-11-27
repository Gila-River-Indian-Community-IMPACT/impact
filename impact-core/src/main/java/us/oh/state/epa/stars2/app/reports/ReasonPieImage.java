package us.oh.state.epa.stars2.app.reports;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.webcommon.bean.Image;
import us.oh.state.epa.stars2.webcommon.output.ChartData;
import us.oh.state.epa.stars2.webcommon.output.PieChartOutputManager;

@SuppressWarnings("serial")
public class ReasonPieImage extends ReportImageBase implements Image {
    private String assigned;
    private String doLaaName;
    private String doLaas;
    private String activityName;
    private String activityStatusCd;
    private Integer userId;
    private String type;
    private String permitType;
    private String issuanceType;
    private String dates;
    private String params;
    private String workloadParams;
    private String reportType;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public ReasonPieImage() {
        super();
        setWidth(300);
        setHeight(300);
    }
    
    public final String getAssigned() {
        return assigned;
    }

    /**
     * 
     */
    private void resetProfile() {
        setWidth(300);
        setHeight(300);
        setStartDt(null);
        setEndDt(null);
        assigned = null;
        image = null;
        areas = null;
        activityName = null;
        userId = null;
        doLaaName = null;
        activityStatusCd = null;
    }

    /**
     * draw
     * 
     * @param width
     * @param height
     * @throws Exception
     */
    public final void draw(int width, int height) throws Exception {
        SimpleIdDef[] pts;
        if (getStartDt() == null) {
            pts = getReportService().retrieveActivePermitsByReasonByType(permitType,
                    issuanceType, doLaas, userId, activityName, activityStatusCd);
        } else {
            if ("EXP".equalsIgnoreCase(reportType)) {
                pts = getReportService().retrieveExpiredPermitsByReasonByType(getStartDt(),
                        getEndDt(), permitType, issuanceType, doLaaName);
            } else {
                pts = getReportService().retrieveIssuedPermitsByReasonByType(getStartDt(),
                        getEndDt(), permitType, issuanceType, doLaaName);
            }
        }

        ArrayList<Object> als = new ArrayList<Object>();
        ChartData cd;
        int color = 0;
        for (SimpleIdDef p : pts) {
            cd = new ChartData();
            cd.setColor(colors[color]);
            cd.setLabel(p.getDescription());
            cd.setQparam("params=" + issuanceType + "." + permitType + "."
                    + doLaaName + "." + getStartDt() + "." + getEndDt());
            cd.setValue(p.getId());
            als.add(cd);
            if (++color == colors.length) {
                color = 1;
            }
        }

        PieChartOutputManager pc = new PieChartOutputManager();
        pc.setHeight(height);
        pc.setWidth(width);
        pc.setTitle(getTitle());
        pc.setCreateLegend(false);
        pc.setClickURL(getClickURL());
        image = pc.process(als.toArray(new ChartData[0]), this, null);
    }

    public final String submitProfile() {
        return SUCCESS;
    }

    public final String getDoLaaName() {
        return doLaaName;
    }

    public final void setDoLaaName(String doLaaName) {
        this.doLaaName = doLaaName;
    }

    public final String getType() {
        return type;
    }

    public final void setType(String type) {

        StringTokenizer st = new StringTokenizer(type, ".");

        permitType = st.nextToken();
        if (st.hasMoreTokens()) {
            issuanceType = st.nextToken();
        } else {
            issuanceType = null;
        }
        
        if (st.hasMoreTokens()) {
            reportType = st.nextToken();
        } else {
            reportType = null;
        }

        this.type = type;
    }

    public final String getActivityName() {
        return activityName;
    }

    public final void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public final Integer getUserId() {
        return userId;
    }

    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    public final String getDates() {
        return dates;
    }

    public final String getDoLaas() {
        return doLaas;
    }

    public final void setDoLaas(String doLaas) {
        this.doLaas = doLaas;
    }

    public final void setDates(String dates) {
        this.dates = dates;
        StringTokenizer st = new StringTokenizer(dates, "%");

        String start = st.nextToken();
        setStartDt(Timestamp.valueOf(start));

        String end = null;
        if (st.hasMoreTokens()) {
            end = st.nextToken();
            setEndDt(Timestamp.valueOf(end));
        }
    }

    public final String getParams() {
        return params;
    }

    public final void setParams(String params) {
        resetProfile();

        this.params = params;
        StringTokenizer st = new StringTokenizer(params, "/");

        String token = st.nextToken();
        setType(token);
        token = st.nextToken();

        //
        // A single Do/LAA Name
        //
        setDoLaaName(token);
        token = st.nextToken();
        setDates(token);
    }

    public final String getWorkloadParams() {
        return workloadParams;
    }

    public final void setWorkloadParams(String workloadParams) {
        resetProfile();
        this.workloadParams = workloadParams;
        StringTokenizer st = new StringTokenizer(workloadParams, SEPARATOR);

        String token = st.nextToken();
        setType(token);
        token = st.nextToken();

        //
        // Comma delimiter set of DO/LAA Codes
        //
        setDoLaas(token);

        if (st.hasMoreTokens()) {
            token = st.nextToken();
            setActivityName(token);
        }

        //
        // If we are at the Total Row there is no user_id but
        // there could be an ActivityStatusCd
        if (!"Total".equalsIgnoreCase(getActivityName()))
        {
            if (st.hasMoreTokens()) {
                token = st.nextToken();
                setUserId(new Integer(token));
            }
        }
        
        if (st.hasMoreTokens()) {
            token = st.nextToken();
            setActivityStatusCd(token);
        }

    }

	public final String getActivityStatusCd() {
		return activityStatusCd;
	}

	public final void setActivityStatusCd(String activityStatusCd) {
		this.activityStatusCd = activityStatusCd;
	}
}
