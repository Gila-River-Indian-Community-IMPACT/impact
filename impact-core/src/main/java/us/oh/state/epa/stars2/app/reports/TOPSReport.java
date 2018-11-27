package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.TOPSData;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class TOPSReport extends AppBase {    
    private boolean _hasTopsResults;
    private Timestamp _reportDt;
    private TOPSData _topsData;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public TOPSReport() {
        super();
        reset();
    }
    
    /**
     * @return
     */
    public final String submit() {

        try {
            if (_reportDt == null) {
                _reportDt = new Timestamp(System.currentTimeMillis());
            }
            _topsData = getReportService().retrieveTOPSData(_reportDt);
            _hasTopsResults = true;
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
        _hasTopsResults = false;
        _reportDt = null;
        return SUCCESS;
    }

    /**
     * @return
     */
    public final boolean isHasTopsResults() {
        return _hasTopsResults;
    }

    /**
     * @param hasPbrResults
     */
    public final void setHasTopsResults(boolean hasTopsResults) {
        _hasTopsResults = hasTopsResults;
    }

    public final TOPSData getTopsData() {
        return _topsData;
    }

    /**
     * @return
     */
    public final Timestamp getReportDt() {
        return _reportDt;
    }

    /**
     * @param reportDt
     */
    public final void setReportDt(Timestamp reportDt) {
        _reportDt = reportDt;
    }

    /**
     * @return
     */
    public final String getStartDt() {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(_reportDt.getTime());
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 6);
        return df.format(new Timestamp(cal.getTimeInMillis()));
    }

    /**
     * @return
     */
    public final String getEndDt() {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(_reportDt);
    }

}
