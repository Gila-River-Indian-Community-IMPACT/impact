package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;


/*
 * Parameters:
 *    ReportingYear
 *    ReferenceDate  -- must not be in the future; default to today's date
 *    Category:  NTV, SMTV, TV
 *    GracePeriod:  for example 45 or 90 days
 *    
 * Report States:
 *   Processed_State:  Approved, Approved/Emissions Inventory Prior/Invalid, Submitted/Emissions Inventory Prior/Invalid
 *               Submitted/Revision Needed, Approved/Revision Needed
 *               
 *   Not_Processed_State:  Submitted, Submitted/Caution
 *   
 *   States_to_Ignore Report:   In Progress
 *   
 * For the specified year examine all reports in the category and do the following.
 *   For CO metrics
 * 
 *   FOR DO/LAA metrics
 *     Examine each report.
 *     Ignore report if in Not_Processed_State and submittedDate + GracePeriod is after ReferenceDate.
 *     The denominator of the score is all the remaining reports
 *     The numerator of the score is all of those remaining reports where the approvedDate - submittedDate is within GracePeriod.
 *     The reports "hurting" the score are those where the grace period was
 *     not met (approvedDate - submittedDate greater than GracePriod OR if not processed then ReferenceDate - submittedDate greater than GracePriod.
 *  
 *   Do we need a list of facilities which "owe" reports but there is not yet a report in the "plain" Approved State?
 *  
 *   A useful feature might be a listing of all submitted reports that have not been processed listed with oldest one first. 
 *     
 *   Two year reporting cycle:   
 *     
 *   Enhancements needed to accomplish this:
 *       All the processed_states will need to set that date into the report (into approvedDate but it mean what the state indicates)
 *         Note that currently, when the approved date is not null it is assumed approved--this will have to change.
 *       The current state "Emissions Inventory Prior/Invalid" needs to be broken into two states.
 */

public class ReportReviewAnalysis extends AppBase {

	private static final long serialVersionUID = 252492137918825714L;

	private static final long MIL_SECOND_PER_DAY = 24 * 60 * 60 * 1000;

	//NEW ATTRIBUES
    private String doLaaCd = null;
    private String emissionsReportingCategory = null;
    private Timestamp fromDate = null;
    private Timestamp toDate = null;
    boolean timely = false;  // include all reports that are used for metric
    private EmissionsReportSearch[] reports = new EmissionsReportSearch[0];

    private boolean hasSearchResults;
    private String revenueType;
    private EmissionsReportSearch searchObj;
    private Thread searchThread;
    private boolean searchStarted;
    private boolean searchCompleted;
    private boolean browserCompleted;
    private DisplayUtil displayUtilBean;
    private boolean showProgressBar;
    private String refreshStr = " ";
    
    Integer smtvDaysV;
    Integer tvDaysV;
    Integer ntv50DaysV;
    Integer ntv100DaysV;
    int smtvDaysNum;
    int tvDaysNum;
    int ntv50DaysNum;
    int ntv100DaysNum;
    int smtvDaysDenom;
    int tvDaysDenom;
    int ntv50DaysDenom;
    int ntv100DaysDenom;
    float tvMetric;
    float smtvMetric;
    float ntv50Metric;
    float ntv100Metric;
    String tvMetricInfo = null;
    String smtvMetricInfo = null;
    String ntv50MetricInfo = null;
    String ntv100MetricInfo = null;
    StringBuffer dateRange = null;
    
    boolean haveTV = false;
    boolean haveSMTV = false;
    boolean haveNTV = false;
    
    ArrayList<ReportReviewData> stats = new ArrayList<ReportReviewData>();
    
    ArrayList<EmissionsReportSearch> ntvList = new ArrayList<EmissionsReportSearch>();
    ArrayList<EmissionsReportSearch> tvList = new ArrayList<EmissionsReportSearch>();
    ArrayList<EmissionsReportSearch> smtvList = new ArrayList<EmissionsReportSearch>();

    private EmissionsReportService emissionsReportService;
    
    public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(
			EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}
    public String getRefreshStr() {
        return refreshStr;
    }

    public ReportReviewAnalysis() {
        super();
        if(fromDate == null && toDate == null) {
            resetDates();
        } 
    }
    
    public void resetDates() {
        int adjBeginYear;
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.MONTH) >= Calendar.JUNE) {
            // do for this year
            adjBeginYear = -1;
        } else {
            // do for last year
            adjBeginYear = -2;
        }
        int y = cal.get(Calendar.YEAR) + adjBeginYear;
        cal.clear();
        cal.set(y, Calendar.OCTOBER, 1, 0, 0);
        fromDate = new Timestamp(cal.getTimeInMillis());
        cal.set(y + 1, Calendar.SEPTEMBER, 30, 0, 0);
        toDate = new Timestamp(cal.getTimeInMillis());
    }

    public final String submit() {
        if (fromDate == null || toDate == null) {
            DisplayUtil.displayError("Enter From and Two Dates");
            return FAIL;
        }

        stats = new ArrayList<ReportReviewData>();
        reports = new EmissionsReportSearch[0];
        searchObj = new EmissionsReportSearch();
        searchObj.setDolaaCd(doLaaCd);
        searchObj.setBeginDt(fromDate);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTimeInMillis(toDate.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Timestamp pastEndDate = new Timestamp(cal.getTimeInMillis());
        searchObj.setEndDt(pastEndDate);
        searchObj.setUnlimitedResults(unlimitedResults());
        
        haveTV = false;
        haveSMTV = false;
        haveNTV = false;
        if(emissionsReportingCategory == null) {
            haveTV = true;
            haveSMTV = true;
            haveNTV = true;
        } else {
            if(EmissionReportsDef.TV.equals(emissionsReportingCategory)) {
                haveTV = true;
            } else if(EmissionReportsDef.SMTV.equals(emissionsReportingCategory)) {
                haveSMTV = true;
            } else {
                haveNTV = true;
            }
        }
        
        tvDaysV = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_TV_ReviewDays", null);
        smtvDaysV = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_SMTV_ReviewDays", null);
        ntv50DaysV = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_NTV_50_Percent_ReviewDays", null);
        ntv100DaysV = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_NTV_100_Percent_ReviewDays", null);

        if(smtvDaysV == null || tvDaysV == null || ntv50DaysV == null || ntv100DaysV == null) {
        	DisplayUtil.displayError("One of the following is missing from the system parameters table:  ER_TV_ReviewDays, ER_SMTV_ReviewDays, ER_NTV_50_Percent_ReviewDays or ER_NTV_100_Percent_ReviewDays");
        	return FAIL;
        }
            
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        dateRange = new StringBuffer(100);
        dateRange.append(sdf.format(fromDate.getTime()));
        dateRange.append("-");
        dateRange.append(sdf.format(toDate.getTime()));
        if (!isSeachStarted() && getSearchThread() == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            displayUtilBean = DisplayUtil.getSessionInstance(facesContext, true);
            hasSearchResults = false;

            String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
                + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
                + "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
                + "<META HTTP-EQUIV=\"Cache-Control\" "
                + "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
            refreshStr = refresh;

            setSearchStarted(true);

            setSearchThread(new Thread("Report Review Analysis Thread") {

                public void run() {
                    try {
                        setHasSearchResults(searchReports());
                        setSearchStarted(false);
                        setSearchCompleted(true);
                        setBrowserCompleted(true);
                        refreshStr = " ";
                    }
                    catch (Exception e) {
                        String error = "Search failed: System Error. ";
                        if (e.getMessage() != null && e.getMessage().length() > 0) {
                            error += e.getMessage();
                        } else {
                            error += e.getClass().getName();
                        }
                        setErrorMessage(error + " Please contact the System Administrator.");
                        logger.error(error, e);
                        setHasSearchResults(false);
                        setSearchStarted(false);
                        setSearchCompleted(false);
                        refreshStr = " ";
                    }
                }
            });

            getSearchThread().setDaemon(true);
            setBrowserCompleted(false);

            start(searchThread);

            if (!isSearchCompleted()) {
                DisplayUtil.displayInfo("Searching Emissions Inventories. This may take several moments. "
                        + "You may cancel the operation by pressing the \"Reset\" button.");
            }
            else {
                refreshStr = " ";
                setSearchStarted(false);
                setBrowserCompleted(true);
            }
        }
        return SUCCESS;
    }

    public boolean searchReports() throws RemoteException {
        init();
        ntvList = new ArrayList<EmissionsReportSearch>();
        tvList = new ArrayList<EmissionsReportSearch>();
        smtvList = new ArrayList<EmissionsReportSearch>();
        tvMetricInfo = null;
        smtvMetricInfo = null;
        ntv50MetricInfo = null;
        ntv100MetricInfo = null;
        reports = getEmissionsReportService().searchEmissionsReportForScore(searchObj);
        if (reports == null || reports.length == 0) {
            setInfoMessage("Search returned no records.");
            return false;
        } else {
            // process reports
            String lastDoLaaCd = null;
            for(EmissionsReportSearch ers : reports) {
                if(lastDoLaaCd == null) {
                    lastDoLaaCd = ers.getDolaaCd();
                }
                if(!lastDoLaaCd.equals(ers.getDolaaCd())) {
                    // Finish previous and initialize for next
                    processDoLaa(lastDoLaaCd);
                    init();
                    lastDoLaaCd = ers.getDolaaCd();
                }
                boolean letPass = false;
                int days;
                boolean includeLateRpt = false;
                boolean includeRpt = false;   // should report be displayed
                boolean actedOn = false;
                Timestamp approveDate = ers.getApprovedDate();
                if (ers.getApprovedDate() == null) {
                    if(!ReportReceivedStatusDef.isSubmittedCode(ers.getReportingState())) {
                        // The report preceeded mantis #2924: Specific Report needed to support schedule specified in contract w/ Locals for NTV review 
                        letPass = true;
                        actedOn = true;
                    }
                    approveDate = new Timestamp(System.currentTimeMillis());
                } else {
                    actedOn = true;
                }
                if(!letPass) {
                    long sDays = ers.getRptReceivedStatusDate().getTime() / MIL_SECOND_PER_DAY;
                    long eDays = approveDate.getTime() / MIL_SECOND_PER_DAY;
                    days = new Long(eDays - sDays).intValue();
                    ers.setDaysToApprove(days);
                } else {
                    days = 0;
                    ers.setDaysToApprove(null);
                }
                    haveTV = true;
                    if(days > tvDaysV) {
                        ers.setMark(2);
                        includeLateRpt = true;
                        tvDaysNum++;
                        includeRpt = true;
                    }
                    if(actedOn || includeLateRpt) {
                        tvDaysDenom++;
                        if(timely)  {
                            includeRpt = true;
                        }
                    }
                    if(includeRpt && doLaaCd != null) {
                        tvList.add(ers);
                    }
            }
            processDoLaa(lastDoLaaCd);
            init();
        }
        return true;
    }
    
    void init() {
        smtvDaysNum = 0;
        tvDaysNum = 0;
        ntv50DaysNum = 0;
        ntv100DaysNum = 0;
        smtvDaysDenom = 0;
        tvDaysDenom = 0;
        ntv50DaysDenom = 0;
        ntv100DaysDenom = 0;
    }
    
    @SuppressWarnings("unchecked")
    void processDoLaa(String doLaaCd) {
        Comparator c = new Comparator() {
            public int compare(Object o1, Object o2) {
                int rtn = ((EmissionsReportSearch)o1).getYear().
                    compareTo(((EmissionsReportSearch)o2).getYear());
                if(rtn == 0) {
                    Integer i1 = ((EmissionsReportSearch)o1).getDaysToApprove();
                    if(i1 == null) {
                        i1 = 0;
                    }
                    Integer i2 = ((EmissionsReportSearch)o2).getDaysToApprove();
                    if(i2 == null) {
                        i2 = 0;
                    }
                    rtn = - (i1.compareTo(i2));
                }
                return rtn;
            }};
 
        if(haveTV) {
            Collections.sort(tvList, c);
            ReportReviewData d = new ReportReviewData(doLaaCd, EmissionReportsDef.TV, tvDaysV, tvDaysNum, tvDaysDenom);
            stats.add(d);
            tvMetricInfo = d.toString();
        }
        if(haveSMTV) {
            Collections.sort(smtvList, c);
            ReportReviewData d = new ReportReviewData(doLaaCd, EmissionReportsDef.SMTV, smtvDaysV, smtvDaysNum, smtvDaysDenom);
            stats.add(d);
            smtvMetricInfo = d.toString();
        }
        if(haveNTV) {
            Collections.sort(ntvList, c);
            ReportReviewData d = new ReportReviewData(doLaaCd, EmissionReportsDef.NTV, ntv50DaysV, ntv50DaysNum, ntv50DaysDenom);
            stats.add(d);
            ntv50MetricInfo = d.toString();
            d = new ReportReviewData(doLaaCd, EmissionReportsDef.NTV, ntv100DaysV, ntv100DaysNum, ntv100DaysDenom);
            stats.add(d);
            ntv100MetricInfo = d.toString();
        }
    }

    public final List<SelectItem> getRevenueTypes() {
        return RevenueTypeDef.getData().getItems().getItems(
                revenueType, true);
    }

    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    public final String reset() {
        resetDates();
        emissionsReportingCategory = null;
        doLaaCd = null;
        refreshStr = " ";

        if (getSearchThread() != null && getSearchThread().isAlive()) {
            try {
                getSearchThread().interrupt();
                getSearchThread().join();
            }
            catch (Exception e) {
                // Ignore.
            }
            finally {
                setSearchThread(null);
            }
        }
        else {
            setSearchThread(null);
        }

        setSearchStarted(false);
        setSearchCompleted(false);
        setBrowserCompleted(false);

        hasSearchResults = false;
        return SUCCESS;
    }

    public final Thread getSearchThread() {
        return searchThread;
    }

    public final void setSearchThread(Thread searchThread) {
        this.searchThread = searchThread;
    }

    public final boolean isSeachStarted() {
        return searchStarted;
    }

    public final void setSearchStarted(boolean searchStarted) {
        this.searchStarted = searchStarted;
        if (searchStarted) {
            setShowProgressBar(true);
        }
    }

    public final synchronized void setErrorMessage(String error) {
        displayUtilBean.addMessageToQueue(error, DisplayUtil.ERROR, null);
    }

    public final synchronized void setInfoMessage(String info) {
        displayUtilBean.addMessageToQueue(info, DisplayUtil.INFO, null);
    }

    public final boolean isSearchCompleted() {
        return searchCompleted;
    }

    public final void setSearchCompleted(boolean searchCompleted) {
        this.searchCompleted = searchCompleted;
        if (searchCompleted) {
            setShowProgressBar(false);
        }
    }

    /** Start the thread that will do the search work. */
    public final synchronized void start(Thread thread) {
        setSearchCompleted(false);
        thread.start();
    }

    public final synchronized void setBrowserCompleted(boolean completed) {

        browserCompleted = completed;

        if (browserCompleted) {

            if (getSearchThread() != null && getSearchThread().isAlive()) {
                try {
                    getSearchThread().interrupt();
                    getSearchThread().join();
                }
                catch (Exception e) {
                    // Ignore.
                }
                finally {
                    setSearchThread(null);
                }
            }
            else {
                setSearchThread(null);
            }
            setSearchStarted(false);
        }

    }
    
    public final boolean isShowProgressBar() {
        return showProgressBar;
    }

    public final void setShowProgressBar(boolean showProgressBar) {
        this.showProgressBar = showProgressBar;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public String getEmissionsReportingCategory() {
        return emissionsReportingCategory;
    }

    public void setEmissionsReportingCategory(String emissionsReportingCategory) {
        this.emissionsReportingCategory = emissionsReportingCategory;
    }

    public Timestamp getFromDate() {
        return fromDate;
    }

    public void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    public Timestamp getToDate() {
        return toDate;
    }

    public void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

    public EmissionsReportSearch[] getReports() {
        return reports;
    }

    public boolean isTimely() {
        return timely;
    }

    public void setTimely(boolean timely) {
        this.timely = timely;
    }

    public ArrayList<EmissionsReportSearch> getNtvList() {
        return ntvList;
    }

    public ArrayList<EmissionsReportSearch> getSmtvList() {
        return smtvList;
    }

    public ArrayList<EmissionsReportSearch> getTvList() {
        return tvList;
    }

    public ArrayList<ReportReviewData> getStats() {
        return stats;
    }

    public String getDateRange() {
        return dateRange.toString();
    }

    public String getNtv100MetricInfo() {
        return ntv100MetricInfo;
    }

    public String getNtv50MetricInfo() {
        return ntv50MetricInfo;
    }

    public String getSmtvMetricInfo() {
        return smtvMetricInfo;
    }

    public String getTvMetricInfo() {
        return tvMetricInfo;
    }

    public int getNtv100DaysV() {
        return ntv100DaysV;
    }

    public int getNtv50DaysV() {
        return ntv50DaysV;
    }
}