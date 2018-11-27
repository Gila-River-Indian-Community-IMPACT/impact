package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.report.GenericIssuanceCount;
import us.oh.state.epa.stars2.def.GenericIssuanceTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class GenericIssuanceReport extends AppBase {
    private boolean hasResults;
    private List<String> doLaaCds = new ArrayList<String>();
    private List<String> issuanceTypeCds = new ArrayList<String>();
    private Timestamp startDt;
    private Timestamp endDt;
    private GenericIssuanceCount[] counts;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public GenericIssuanceReport() {
        super();
        reset();
    }
    
    /**
     * @return
     */
    public final String submit() {
        if (startDt == null || endDt == null){
            DisplayUtil.displayError("Please enter required data.");
        }else{
            try {
                
                if (getDoLaaCds().size() == 0){
                    for (SelectItem si : DoLaaDef.getData().getItems().getAllItems())
                        doLaaCds.add((String)si.getValue());
                }
                
                if (getIssuanceTypeCds().size() == 0){
                    for (SelectItem si : GenericIssuanceTypeDef.getData().getItems().getAllItems())
                        issuanceTypeCds.add((String)si.getValue());
                }
                counts = getReportService().retrieveGenericIssuanceCount(
                        doLaaCds.toArray(new String[0]), 
                        issuanceTypeCds.toArray(new String[0]), 
                        startDt, endDt);
                if (counts != null && counts.length > 0) {
                    hasResults = true;
                }
            }
            catch (RemoteException re) {
                logger.error(re.getMessage(), re);
                DisplayUtil.displayError("System error. Please contact system administrator");
            }
        }

        return SUCCESS;
    }

    /**
     * @return
     */
    public final String reset() {
        doLaaCds = null;
        issuanceTypeCds = null;
        hasResults = false;
        startDt = null;
        endDt = null;

        return SUCCESS;
    }

    /**
     * @return the counts
     */
    public final GenericIssuanceCount[] getCounts() {
        return counts;
    }

    /**
     * @param counts the counts to set
     */
    public final void setCounts(GenericIssuanceCount[] counts) {
        this.counts = counts;
    }

    /**
     * @return the endDt
     */
    public final Timestamp getEndDt() {
        return endDt;
    }

    /**
     * @param endDt the endDt to set
     */
    public final void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
    }

    /**
     * @return the hasResults
     */
    public final boolean isHasResults() {
        return hasResults;
    }

    /**
     * @param hasResults the hasResults to set
     */
    public final void setHasResults(boolean hasResults) {
        this.hasResults = hasResults;
    }

    /**
     * @return the startDt
     */
    public final Timestamp getStartDt() {
        return startDt;
    }

    /**
     * @param startDt the startDt to set
     */
    public final void setStartDt(Timestamp startDt) {
        this.startDt = startDt;
    }

    /**
     * @return the doLaaCds
     */
    public final List<String> getDoLaaCds() {
        if (doLaaCds == null)
            doLaaCds = new ArrayList<String>();
        return doLaaCds;
    }

    /**
     * @param doLaaCds the doLaaCds to set
     */
    public final void setDoLaaCds(List<String> doLaaCds) {
        this.doLaaCds = doLaaCds;
    }

    /**
     * @return the issuanceTypeCds
     */
    public final List<String> getIssuanceTypeCds() {
        if (issuanceTypeCds == null)
            issuanceTypeCds = new ArrayList<String>();
        return issuanceTypeCds;
    }

    /**
     * @param issuanceTypeCds the issuanceTypeCds to set
     */
    public final void setIssuanceTypeCds(List<String> issuanceTypeCds) {
        this.issuanceTypeCds = issuanceTypeCds;
    }

}
