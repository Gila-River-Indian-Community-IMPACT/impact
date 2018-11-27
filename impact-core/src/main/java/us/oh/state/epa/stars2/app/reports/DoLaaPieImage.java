package us.oh.state.epa.stars2.app.reports;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.webcommon.bean.Image;
import us.oh.state.epa.stars2.webcommon.output.ChartData;
import us.oh.state.epa.stars2.webcommon.output.PieChartOutputManager;

public class DoLaaPieImage extends ReportImageBase implements Image {
    private String assigned;
    private String doLaaName;
    private String doLaas;
    private String activityName;
    private Integer userId;
    private String type;
    private String reportType;
    private String permitType;
    private String issuanceType;
    private String dates;
    private String params;
    private String workloadParams;
    private String activityStatusCd;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	public DoLaaPieImage() {
        super();
        setWidth(300);
        setHeight(300);
    }
    
    /**
     * @return
     */
    public final String getAssigned() {
        return assigned;
    }

    /**
     * @param assigned
     */
    public final void setAssigned(String assigned) {
        this.assigned = assigned;
    }

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

        SimpleIdDef[] pts = null;
        if ("WORK".equalsIgnoreCase(reportType)) {
            pts = getReportService().retrieveActivePermitsByDoLaaByType(permitType,
                    issuanceType, doLaas, userId, activityName, activityStatusCd);
        } else if ("ISSUANCE".equalsIgnoreCase(reportType)) {
            pts = getReportService().retrieveIssuedPermitsByDoLaaByType(getStartDt(), getEndDt(),
                    permitType, issuanceType, doLaaName);
        } else if ("EXP".equalsIgnoreCase(reportType)) {
            pts = getReportService().retrieveExpiredPermitsByDoLaaByType(getStartDt(), getEndDt(),
                    permitType, issuanceType, doLaaName);
        } else {
            logger.error("Report type unknown to DoLaaPieImage: " + reportType);
        }

        ArrayList<Object> als = new ArrayList<Object>();
        ChartData cd;
        int color = 0;
        if (pts != null) {
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
        }

        PieChartOutputManager pc = new PieChartOutputManager();
        pc.setHeight(height);
        pc.setWidth(width);
        pc.setTitle(getTitle());
        pc.setCreateLegend(false);
        pc.setClickURL(getClickURL());
        image = pc.process(als.toArray(new ChartData[0]), this, null);
    }

    /**
     * @return
     */
    public final String submitProfile() {
        return SUCCESS;
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
        StringTokenizer st = new StringTokenizer(type, ".");

        reportType = st.nextToken();
        if (st.hasMoreTokens()) {
            permitType = st.nextToken();
        } else {
            permitType = null;
        }

        if (st.hasMoreTokens()) {
            issuanceType = st.nextToken();
        } else {
            issuanceType = null;
        }

        this.type = type;
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
    public final Integer getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public final void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return
     */
    public final String getActivityName() {
        return activityName;
    }

    /**
     * @param activityName
     */
    public final void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    /**
     * @return
     */
    public final String getDates() {
        return dates;
    }

    /**
     * @return
     */
    public final String getDoLaas() {
        return doLaas;
    }

    /**
     * @param doLaas
     */
    public final void setDoLaas(String doLaas) {
        this.doLaas = doLaas;
    }

    /**
     * @param dates
     */
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

    /**
     * @return
     */
    public final String getParams() {
        return params;
    }

    /**
     * @param params
     */
    public final void setParams(String params) {
        resetProfile();
        this.params = params;
        StringTokenizer st = new StringTokenizer(params, "/");

        String token = st.nextToken();
        setType(token);
        token = st.nextToken();
        setDoLaaName(token);
        if (st.hasMoreTokens()) {
            token = st.nextToken();
            setDates(token);
        }
    }

    /**
     * @return
     */
    public final String getWorkloadParams() {
        return workloadParams;
    }

    /**
     * @param workloadParams
     */
    public final void setWorkloadParams(String workloadParams) {
        resetProfile();
        this.workloadParams = workloadParams;
        StringTokenizer st = new StringTokenizer(workloadParams, SEPARATOR);

        String token = st.nextToken();
        setType(token);
        token = st.nextToken();
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
