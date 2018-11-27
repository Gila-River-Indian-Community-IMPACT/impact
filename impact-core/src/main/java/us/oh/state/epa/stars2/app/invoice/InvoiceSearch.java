package us.oh.state.epa.stars2.app.invoice;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceList;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

@SuppressWarnings("serial")
public class InvoiceSearch extends AppBase {
    private InvoiceList searchObj = new InvoiceList();
    private InvoiceList[] invoices;
    private boolean hasSearchResults;    
    private String fromFacility = "false";    
    
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

    public InvoiceSearch() {
        super();
        
        cacheViewIDs.add("/inv/invSearch.jsp");
        cacheViewIDs.add("/facilities/invoices.jsp");
    }
    
    public final String getFromFacility() {
        return fromFacility;
    }

    public final void setFromFacility(String fromFacility) {
        this.fromFacility = fromFacility;
    }

    public final Integer getInvoiceId() {
        return searchObj.getInvoiceId();
    }

    public final void setInvoiceId(Integer invoiceId) {
        searchObj.setInvoiceId(invoiceId);
    }
    
    public final Integer getRevenueId() {
        return searchObj.getRevenueId();
    }

    public final void setRevenueId(Integer revenueId) {
        searchObj.setRevenueId(revenueId);
    }

    public final Integer getReportId() {
        return searchObj.getEmissionsRptId();
    }

    public final String getFacilityId() {
        return searchObj.getFacilityId();
    }

    public final void setFacilityId(String facilityId) {
        searchObj.setFacilityId(facilityId);
    }

    public final String getFacilityName() {
        return searchObj.getFacilityName();
    }

    public final void setFacilityName(String facilityName) {
        searchObj.setFacilityName(facilityName);
    }

    public final String getDoLaaCd() {
        return searchObj.getDoLaaCd();
    }
    
    public final List<SelectItem> getDoLaas() {
        return DoLaaDef.getData().getItems().getAllSearchItems();
    }

    public final void setDoLaaCd(String doLaaCd) {
        searchObj.setDoLaaCd(doLaaCd);
    }

	public final String submitSearch() {
        invoices = null;
        hasSearchResults = false;
        boolean revenuesFirst = false;
        
        try {
        	if(searchObj.getRevenueStateCd() != null){
        		if(searchObj.getBeginDt() == null){
        			DisplayUtil.displayError("Please select Begin date");
        			return ERROR;
        		}
        		revenuesFirst = true;        	
        	}
        	
        	User user = InfrastructureDefs.getPortalUser();
            searchObj.setUnlimitedResults(unlimitedResults());
        	invoices = getInfrastructureService().searchInvoices(user, searchObj, revenuesFirst);
            DisplayUtil.displayHitLimit(invoices.length);
  
        	if(invoices.length > 20000){
        		DisplayUtil.displayWarning("Due to Revenues system limitations, the system cannot return the more than 20,000 invoice records. "
        					+ "Please narrow your search and try again ");
        	}
        	else if (invoices.length == 0 && fromFacility.equals("false")) {
        		DisplayUtil.displayNoRecords();        		
        	} 
        	else {
        		hasSearchResults = true;
        	}
        	if(user == null){
        		DisplayUtil.displayError("Revenues not Searched : User is null");
        	}
        }catch (RemoteException re) {
            DisplayUtil.displayError("Search failed : " + re.getMessage());
            handleException(re);
        }

        return SUCCESS;
    }

    public final String reset() {
        searchObj = new InvoiceList();
        invoices = null;
        hasSearchResults = false;

        return SUCCESS;
    }

    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }

    public final InvoiceList[] getInvoices() {
        return invoices;
    }

    public final String clearResults() {
        searchObj = null;
        hasSearchResults = false;
        reset();
        return SUCCESS;
    }

    public final Integer getEmissionsRptId() {
        return searchObj.getEmissionsRptId();
    }

    public final void setEmissionsRptId(Integer emissionsRptId) {
        searchObj.setEmissionsRptId(emissionsRptId);
    }

    public final List<SelectItem> getRevenueTypes() {
        return RevenueTypeDef.getData().getItems().getItems(
                searchObj.getRevenueTypeCd(), true);
    }

    public final String getRevenueTypeCd() {
        return searchObj.getRevenueTypeCd();
    }

    public final void setRevenueTypeCd(String revenueTypeCd) {
        searchObj.setRevenueTypeCd(revenueTypeCd);
    }

//    public final List<SelectItem> getInvoiceStates() {
//        return InvoiceState.getData().getItems().getItems(
//                searchObj.getInvoiceStateCd(), true);
//    }
//    
//    public final List<SelectItem> getRevenueStates() {
//        return RevenueState.getData().getItems().getItems(
//                searchObj.getRevenueStateCd(), true);
//    }
        
    public final String getInvoiceStateCd() {
        return searchObj.getInvoiceStateCd();
    }

    public final void setInvoiceStateCd(String invoiceStateCd) {
        searchObj.setInvoiceStateCd(invoiceStateCd);
    }
    
    public final String getRevenueStateCd() {
		return searchObj.getRevenueStateCd();
	}

	public final void setRevenueStateCd(String revenueStateCd) {
		searchObj.setRevenueStateCd(revenueStateCd);
	}

	public final Date getBeginDt() {
        return searchObj.getBeginDt();
    }

    public final void setBeginDt(Date beginDt) {
        searchObj.setBeginDt(beginDt);
    }

    public final Date getEndDt() {
        return searchObj.getEndDt();
    }

    public final void setEndDt(Date endDt) {
        searchObj.setEndDt(endDt);
    }	 
    
    public void restoreCache(){
    	
    }
    
    public void clearCache(){
 
    	invoices = null;
    	hasSearchResults = false;
    }
}
