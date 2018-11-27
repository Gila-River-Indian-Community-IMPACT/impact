package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.webcommon.DisplayUtil;

@SuppressWarnings("serial")
public class LatePermits extends PermitSOP {   
    public LatePermits() {
        super();
    }


    public final String submit() {

        try {
            details = getReportService().retrieveLatePermitsData(
                    selectedDoLaas, selectedPermitTypes, selectedReasonCds,
                    general, express, showAll, type);
            
            hasSearchResults = true;
            showChartResults = false;
            hideShowTitle = "Show Charts";
        } 
        catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error: " + re.getMessage());
        }

        return SUCCESS;
    }
    
    public final String reset() {
        super.reset();
        showAll = false;
        return SUCCESS;
    }
}
