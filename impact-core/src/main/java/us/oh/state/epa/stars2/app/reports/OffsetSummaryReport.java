package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.OffsetSummaryLineItem;

@SuppressWarnings("serial")
public class OffsetSummaryReport extends AppBase {
	
	public static final String SUCCESS = "success";
	
	private OffsetSummaryLineItem[] offsetSummaryLineItems;
		
	private CompanyService companyService;
	
	// search fields
	String nonAttainmentAreaCd;
	String pollutantCd;
	String cmpId;
	
	boolean hasSearchResults;
	
	public OffsetSummaryReport() {
		super();
	}

	public OffsetSummaryLineItem[] getOffsetSummaryLineItems() {
		return offsetSummaryLineItems;
	}

	public void setOffsetSummaryLineItems(
			OffsetSummaryLineItem[] offsetSummaryLineItems) {
		this.offsetSummaryLineItems = offsetSummaryLineItems;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
	
	public String getNonAttainmentAreaCd() {
		return nonAttainmentAreaCd;
	}

	public void setNonAttainmentAreaCd(String nonAttainmentAreaCd) {
		this.nonAttainmentAreaCd = nonAttainmentAreaCd;
	}

	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}
	
	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public String generateOffsetSummaryReport() {
		try {
			offsetSummaryLineItems = getCompanyService().retrieveOffsetSummaryLineItems(this);
			
			if(null != offsetSummaryLineItems && offsetSummaryLineItems.length == 0) {
				hasSearchResults = false;
				DisplayUtil.displayInfo("Offset tracking or offset adjustment rows were not found");
			} else {
				hasSearchResults = true;
			}
		}catch(RemoteException re) {
			DisplayUtil.displayError("Failed to generate offsets summary report");
			hasSearchResults = false;
			handleException(re);
		}
		
		return SUCCESS;
	}
	
	public String reset() {
		setNonAttainmentAreaCd(null);
		setPollutantCd(null);
		setCmpId(null);
		setHasSearchResults(false);
		
		return SUCCESS;
	}
	
	public List<SelectItem> getNonAttainmentAreaPollutants() {
		List<SelectItem> pollutants = new ArrayList<SelectItem>();
		try{
			PollutantDef[] pdefs = getCompanyService()
					.findPollutantsByNonattainmentArea(getNonAttainmentAreaCd());
			for(PollutantDef def : pdefs) {
				pollutants.add(new SelectItem(def.getCode(), def.getDescription(), "", def.isDeprecated()));
			}
			
		}catch(RemoteException re) {
			handleException (re);
		}
		
		return pollutants;
	}
	
	public void nonAttainmentAreaValueChanged(ValueChangeEvent vce) {
		setPollutantCd(null);
	}
}
