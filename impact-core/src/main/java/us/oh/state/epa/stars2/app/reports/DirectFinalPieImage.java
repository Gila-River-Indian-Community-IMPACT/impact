package us.oh.state.epa.stars2.app.reports;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.bean.Image;
import us.oh.state.epa.stars2.webcommon.output.ChartData;
import us.oh.state.epa.stars2.webcommon.output.PieChartOutputManager;

@SuppressWarnings("serial")
public class DirectFinalPieImage extends ReportImageBase implements Image {
    private String assigned;
    private String doLaaName;
    private String type;
    private String permitType;
    private String issuanceType;
    private String dates;
    private String params;
    private String reportType;

    public DirectFinalPieImage() {
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

    /**
     * 
     */
    private void resetProfile() {
        setWidth(300);
        setHeight(300);
        assigned = null;
        image = null;
        areas = null;
    }

    /**
     * draw
     * 
     * @param width
     * @param height
     * @throws Exception
     */
    public final void draw(int width, int height) throws Exception {
        ReportService repBO = ServiceFactory.getInstance().getReportService();
        SimpleIdDef[] pts = null;
        if ("EXP".equalsIgnoreCase(reportType)) {
            pts = repBO.retrieveExpiredPermitsByDirectFinalByType(
                    getStartDt(), getEndDt(), permitType, issuanceType, doLaaName);
        } else {
            pts = repBO.retrieveIssuedPermitsByDirectFinalByType(
                    getStartDt(), getEndDt(), permitType, issuanceType, doLaaName);
        }
        ArrayList<Object> als = new ArrayList<Object>();
        ChartData cd;
        int color = 0;
        for (SimpleIdDef p : pts) {
            cd = new ChartData();
            cd.setColor(colors[color]);
            cd.setLabel(p.getDescription());
            String paramsValue = issuanceType + "." + permitType + ".";
            if (reportType != null) {
                paramsValue = paramsValue + reportType + ".";
            }
            paramsValue = paramsValue +doLaaName + "." + getStartDt() + "." + getEndDt();
            cd.setQparam("params=" + paramsValue);
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

    /**
     * @return
     */
    public final String submitProfile() {
        return SUCCESS;
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
        StringTokenizer st = new StringTokenizer(type, ".");

        permitType = st.nextToken();
        issuanceType = st.nextToken();
        
        if (st.hasMoreTokens()) {
            reportType = st.nextToken();
        } else {
            reportType = null;
        }

        this.type = type;
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
        token = st.nextToken();
        setDates(token);
    }
}
