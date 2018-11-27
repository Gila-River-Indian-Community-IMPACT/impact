package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.WorkloadTrend;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class WorkloadTrendReport extends AppBase {

    private List<String> selectedTypes = new ArrayList<String>();
    
    private WorkloadTrend[] details;
    private boolean hasSearchResults;
    private ReportService reportService;

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

    public WorkloadTrendReport() {
        super();
        reset();
    }

    public final List<SelectItem> getTypes() {
        List<SelectItem> ret = new ArrayList<SelectItem>();
        ret.add(new SelectItem("II","Initial installation"));
        ret.add(new SelectItem("PTIOR","PTIO renewal"));
        ret.add(new SelectItem("ITV","Initial Title V"));
        //ret.add(new SelectItem("C31M","Chapter 31 mod"));
        ret.add(new SelectItem("NA","Not yet assigned"));
        ret.add(new SelectItem("RTV","Renewal Title V"));
        return ret;
    }
    
    private void add(ArrayList<String> array, String s) {
        if (!array.contains(s))
            array.add(s);
    }
    
    public final String submit() {
        ArrayList<String> permitTypes = new ArrayList<String>();
        ArrayList<String> permitReasons = new ArrayList<String>();

        if (selectedTypes.size() == 0 || selectedTypes.contains("II")){
            add(permitTypes, PermitTypeDef.NSR);
            //add(permitTypes, PermitTypeDef.TVPTI);
            add(permitReasons, PermitReasonsDef.INITIAL);
        }
        if (selectedTypes.size() == 0 || selectedTypes.contains("PTIOR")){
            add(permitTypes, PermitTypeDef.NSR);
            add(permitReasons, PermitReasonsDef.RENEWAL);
        }
        if (selectedTypes.size() == 0 || selectedTypes.contains("ITV")){
            add(permitTypes, PermitTypeDef.TV_PTO);
            add(permitReasons, PermitReasonsDef.INITIAL);
        }
        //if (selectedTypes.size() == 0 || selectedTypes.contains("C31M")){
        //    add(permitTypes, PermitTypeDef.NSR);
        //    add(permitTypes, PermitTypeDef.TVPTI);
        //    add(permitReasons, PermitReasonsDef.CHAPTER_31_MOD);
        //}
        if (selectedTypes.size() == 0 || selectedTypes.contains("NA")){
            add(permitTypes, PermitTypeDef.NSR);
            //add(permitTypes, PermitTypeDef.TIV_PTO);
            //add(permitTypes, PermitTypeDef.TVPTI);
            add(permitTypes, PermitTypeDef.TV_PTO);
            add(permitReasons, PermitReasonsDef.NOT_ASSIGNED);
        }
        if (selectedTypes.size() == 0 || selectedTypes.contains("RTV")){
            //add(permitTypes, PermitTypeDef.TIV_PTO);
            //add(permitTypes, PermitTypeDef.TVPTI);
            add(permitTypes, PermitTypeDef.TV_PTO);
            add(permitReasons, PermitReasonsDef.RENEWAL);
        }
        
        try {
            
            details = getReportService().retrieveWorkloadTrendReport(
                    permitTypes.toArray(new String[0]), permitReasons.toArray(new String[0]));

            hasSearchResults = true;
        } 
        catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error: " + re.getMessage());
        }

        return SUCCESS;
    }

    public final String reset() {
        details = null;
        hasSearchResults = false;
        
        selectedTypes = new ArrayList<String>();
        return SUCCESS;
    }

    public final List<String> getSelectedTypes() {
        return selectedTypes;
    }

    public final void setSelectedTypes(List<String> selectedTypes) {
        this.selectedTypes = selectedTypes;
    }

    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    /**
     * @return the details
     */
    public final WorkloadTrend[] getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    public final void setDetails(WorkloadTrend[] details) {
        this.details = details;
    }

}
