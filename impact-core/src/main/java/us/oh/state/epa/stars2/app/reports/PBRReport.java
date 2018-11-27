package us.oh.state.epa.stars2.app.reports;

/*
public class PBRReport extends AppBase {
    private boolean _hasPbrResults;
    private Timestamp _startDt;
    private Timestamp _endDt;
    private PBRCount[] _pbrCounts;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public PBRReport() {
        super();
        reset();
    }
    
    
    public final String submit() {

        try {
            _pbrCounts = getReportService().retrievePbrCountsByType(_startDt, _endDt);
            if (_pbrCounts != null && _pbrCounts.length > 0) {
                _hasPbrResults = true;
            }
        }
        catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }

        return SUCCESS;
    }

   
    public final String reset() {

        _hasPbrResults = false;
        //_pbrCounts = null;

        return SUCCESS;
    }

    
    public final boolean isHasPbrResults() {
        return _hasPbrResults;
    }

    
    public final void setHasPbrResults(boolean hasPbrResults) {
        _hasPbrResults = hasPbrResults;
    }

    
    public final PBRCount[] getPbrCounts() {
        return _pbrCounts;
    }

   
    public final Timestamp getEndDt() {
        return _endDt;
    }

    
    public final void setEndDt(Timestamp endDt) {
        _endDt = endDt;
    }

    public final Timestamp getStartDt() {
        return _startDt;
    }

    
    public final void setStartDt(Timestamp startDt) {
        _startDt = startDt;
    }

}
*/

