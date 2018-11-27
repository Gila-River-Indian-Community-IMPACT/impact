package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

/**
 * @author Kbradley
 *
 */
@SuppressWarnings("serial")
public class WorkloadToDoSearch extends AppBase {
    private String facilityId;
    private String facilityNm;
    private Integer userId;
    private String activityStatusCd;
    private String activityNm;
    private String doLaaIn;
    private String ProcessTypeCd;
    private String type;
    private String permitType;
    private String issuanceType;
    private Integer externalId;
    private ProcessActivity[] shortActivities;
    private ProcessActivity pa = new ProcessActivity();
    private boolean hasSearchResults;
    private ProcessActivity[] activities;
    private boolean showList;
    private String workloadParams;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public WorkloadToDoSearch() {
        super();
        reset();
    }

    /**
     * @return
     */
    public final ProcessActivity[] getShortActivities() {
        return shortActivities;
    }

    /**
     * @param shortActivities
     */
    public final void setShortActivities(ProcessActivity[] shortActivities) {
        this.shortActivities = shortActivities;
    }

    /**
     * @return
     */
    public final String getActivityStatusCd() {
        return activityStatusCd;
    }

    /**
     * @param activityStatusCd
     */
    public final void setActivityStatusCd(String activityStatusCd) {
        if ("All".equalsIgnoreCase(activityStatusCd)) {
            //
            // Special case for workload mgmt report. Reseting for the
            // next retrieval. "All" is set in workloadReport.jsp.
            //
            this.activityStatusCd = null;
        } else {
            this.activityStatusCd = activityStatusCd;
        }
    }

    /**
     * @return
     */
    public final String getActivityNm() {
        return activityNm;
    }

    /**
     * @param activityNm
     */
    public final void setActivityNm(String activityNm) {
        this.activityNm = activityNm;
    }

    /**
     * @return
     */
    public final String getFacilityId() {
        return facilityId;
    }

    /**
     * @param facilityId
     */
    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * @return
     */
    public final String getFacilityNm() {
        return facilityNm;
    }

    /**
     * @param facilityNm
     */
    public final void setFacilityNm(String facilityNm) {
        this.facilityNm = facilityNm;
    }

    /**
     * @return
     */
    public final String getProcessTypeCd() {
        return ProcessTypeCd;
    }

    /**
     * @param processTypeCd
     */
    public final void setProcessTypeCd(String processTypeCd) {
        ProcessTypeCd = processTypeCd;
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
    public final String getDoLaaIn() {
        return doLaaIn;
    }

    /**
     * @param doLaaIn
     */
    public final void setDoLaaIn(String doLaaIn) {
        this.doLaaIn = doLaaIn;
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
        showList = true;
        StringTokenizer st = new StringTokenizer(type, ".");

        permitType = st.nextToken();

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
    public final boolean isHasSearchResults() {
        return hasSearchResults;
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
    public final ProcessActivity[] getActivities() {
        return activities;
    }

    /**
     * @param activities
     */
    public final void setActivities(ProcessActivity[] activities) {
        this.activities = activities;
    }

    /**
     * @return
     */
    public final String submit() {
        try {
            pa.setFacilityId(facilityId);
            pa.setFacilityNm(facilityNm);
            pa.setUserId(userId);

            if (activityStatusCd.contains(",")) {
                String[] codes = activityStatusCd.split("\\,");
                ArrayList<String> activityStatusCds = new ArrayList<String>();
                for (String code : codes) {
                    activityStatusCds.add(code);
                }
                pa.setInStatus(true);
                pa.setActivityStatusCds(activityStatusCds);
            } else {
                pa.setActivityStatusCd(activityStatusCd);
            }
            pa.setActivityTemplateNm(activityNm);
            pa.setProcessCd(ProcessTypeCd);
            pa.setPerformerTypeCd("M");
            pa.setExternalId(externalId);
            pa.setDoLaaName(doLaaIn);
            pa.setPermitType(permitType);
            pa.setIssuanceType(issuanceType);
            pa.getCurrent();

            activities = getReportService().retrieveActivityList(pa, doLaaIn);
            hasSearchResults = true;

            ArrayList<ProcessActivity> ret = new ArrayList<ProcessActivity>();
            ArrayList<ProcessActivity> aggs = new ArrayList<ProcessActivity>();
            ArrayList<ProcessActivity> maggs = new ArrayList<ProcessActivity>();

            for (ProcessActivity ta : activities) {
                if (ta.getAggregate().equalsIgnoreCase("Y")) {
                    if (maggs.contains(ta)) {
                        continue;
                    } else if (aggs.contains(ta)) {
                        String name = ta.getActivityTemplateNm() + " *";
                        ta = aggs.get(aggs.indexOf(ta));
                        ta.setActivityTemplateNm(name);
                        maggs.add(ta);
                        continue;
                    } else {
                        aggs.add(ta);
                    }
                }
                ret.add(ta);
                shortActivities = ret.toArray(new ProcessActivity[0]);
            }
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
        pa = new ProcessActivity();
        activities = null;
        facilityId = null;
        facilityNm = null;
        userId = null;
        activityStatusCd = "IP";
        activityNm = null;
        ProcessTypeCd = null;
        externalId = null;
        hasSearchResults = false;
        showList = false;
        shortActivities = null;
        doLaaIn = null;
        return SUCCESS;
    }

    /**
     * @return
     */
    public final Integer getExternalId() {
        return externalId;
    }

    /**
     * @param externalId
     */
    public final void setExternalId(Integer externalId) {
        reset();
        this.externalId = externalId;
        activityStatusCd = null;
        userId = null;
        showList = true;
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
    public final String getWorkloadParams() {
        return workloadParams;
    }

    /**
     * @param workloadParams
     */
    public final void setWorkloadParams(String workloadParams) {
        reset();
        this.workloadParams = workloadParams;
        StringTokenizer st = new StringTokenizer(workloadParams, ReportImageBase.SEPARATOR);

        String token = st.nextToken();
        setType(token);
        token = st.nextToken();

        //
        // If we are at a Total row then we have a preconfigured IN clause
        // with all selected doLaas. If not we just have a single doLaa
        // that must be formatted.
        //
        setDoLaaIn(token);

        token = st.nextToken();
        setActivityStatusCd(token);

        if (st.hasMoreTokens()) {
            token = st.nextToken();
            setActivityNm(token);
        }

        if (st.hasMoreTokens()) {
            token = st.nextToken();
            setUserId(new Integer(token));
        }
    }
}
