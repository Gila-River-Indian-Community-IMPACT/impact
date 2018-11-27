package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.FacilityPermitCount;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class FacilityPermitReport extends AppBase {
    private boolean _hasFacPmtResults;
    private Timestamp _startDt;
    private Timestamp _endDt;
    private FacilityPermitCount[] _facilityPermitCounts;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public FacilityPermitReport() {
        super();
        reset();
    }
    
    /**
     * @return
     */
    public final String submit() {

        try {
            _facilityPermitCounts = getReportService().retrieveFacilityCountsByPermitType(_endDt);
            if (_facilityPermitCounts != null && _facilityPermitCounts.length > 0) {
                _hasFacPmtResults = true;
            }
        }
        catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        return SUCCESS;
    }

    /**
     * @return
     */
    public final String reset() {
        _hasFacPmtResults = false;
        return SUCCESS;
    }

    /**
     * @return
     */
    public final boolean isHasFacPmtResults() {
        return _hasFacPmtResults;
    }

    /**
     * @param hasFacPmtResults
     */
    public final void setHasFacPmtResults(boolean hasFacPmtResults) {
        _hasFacPmtResults = hasFacPmtResults;
    }

    /**
     * @return
     */
    public final FacilityPermitCount[] getFacilityCounts() {
        return _facilityPermitCounts;
    }

    /**
     * @return
     */
    public final Timestamp getEndDt() {
        return _endDt;
    }

    /**
     * @param endDt
     */
    public final void setEndDt(Timestamp endDt) {
        _endDt = endDt;
    }

    /**
     * @return
     */
    public final Timestamp getStartDt() {
        return _startDt;
    }

    /**
     * @param startDt
     */
    public final void setStartDt(Timestamp startDt) {
        _startDt = startDt;
    }

}
