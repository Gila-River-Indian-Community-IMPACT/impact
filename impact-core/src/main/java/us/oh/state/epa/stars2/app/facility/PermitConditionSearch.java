package us.oh.state.epa.stars2.app.facility;

import java.util.LinkedHashMap;

import javax.faces.event.ValueChangeEvent;

import org.joda.time.DateTime;

import us.oh.state.epa.stars2.bo.PermitConditionService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dao.permit.PermitSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitConditionSearchLineItem;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;

@SuppressWarnings("serial")
public class PermitConditionSearch extends AppBase {

	public static final String PERMIT_CONDITION_SEARCH_OUTCOME = "facilities.profile.permitConditionSearch";
	
	private PermitConditionSearchCriteria searchCriteria = new PermitConditionSearchCriteria();
	private PermitService permitService;
	private PermitConditionService permitConditionService;
	
	private PermitConditionSearchLineItem[] searchResults = new PermitConditionSearchLineItem[0];
	private boolean hasSearchResults = false;
	
	private TableSorter resultsWrapper = new TableSorter();
	
	private String facilityId;
	
	public PermitConditionSearchCriteria getSearchCriteria() {
		return searchCriteria;
	}
	
	public void setSearchCriteria(PermitConditionSearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
	
	public PermitService getPermitService() {
		return permitService;
	}
	
	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}
	
	public PermitConditionService getPermitConditionService() {
		return permitConditionService;
	}
	
	public void setPermitConditionService(PermitConditionService permitConditionService) {
		this.permitConditionService = permitConditionService;
	}
	
	public PermitConditionSearchLineItem[] getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(PermitConditionSearchLineItem[] searchResults) {
		this.searchResults = searchResults;
	}
	
	public boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public TableSorter getResultsWrapper() {
		return resultsWrapper;
	}

	public void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}

	public String submitSearch() {
		searchResults = null;
		hasSearchResults = false;
		
		if (null != searchCriteria.getBeginDt() && null != searchCriteria.getEndDt()) {
			DateTime beginDate = new DateTime(searchCriteria.getBeginDt().getTime());
			DateTime endDate = new DateTime(searchCriteria.getEndDt().getTime());

			if (beginDate.isAfter(endDate)) {
				DisplayUtil.displayError("From date cannot be before the To date");
				return FAIL;
			}
		}
		
		try {
			
			// limit search to current facility only
			FacilityProfile fp = (FacilityProfile)FacesUtil.getManagedBean("facilityProfile");
			this.facilityId = fp.getFacilityId();
			searchCriteria.setFacilityId(this.facilityId);;
			
			searchResults = getPermitConditionService().searchPermitConditions(searchCriteria);
			resultsWrapper.setWrappedData(searchResults);
			if(0 == searchResults.length) {
				DisplayUtil.displayInfo("There are no permit conditions that match the search criteria.");
			} else {
				hasSearchResults = true;
			}
			
		} catch(DAOException daoe) {
			reset();
			DisplayUtil.displayError("An error occured when trying to search permit conditions");
			logger.error(daoe.getMessage());
			handleException(daoe);
		}
		
		return SUCCESS;
	}
	
	public void reset() {
		this.searchCriteria = new PermitConditionSearchCriteria(); 
		hasSearchResults = false;
		searchResults = new PermitConditionSearchLineItem[0];
		resultsWrapper.setWrappedData(searchResults); 
	}
	
	
	public LinkedHashMap<String, String> getPermitConditionSearchDateBy() {
		String permitType = this.searchCriteria.getPermitTypeCd();
		
		LinkedHashMap<String, String> permitConditionSearchDateBy = new LinkedHashMap<String, String>();
		
		permitConditionSearchDateBy.put("Final Issuance Date", PermitSQLDAO.FINAL_ISSUANCE_DATE);
		
		if(Utility.isNullOrEmpty(permitType) || (!Utility.isNullOrEmpty(permitType)
				&& (PermitTypeDef.TV_PTO.equalsIgnoreCase(permitType)))) {
			permitConditionSearchDateBy.put("Permit Basis Date", PermitSQLDAO.PERMIT_BASIS_DATE);
		}
		
		return permitConditionSearchDateBy;
	}
	
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	private boolean clearSearchResults() {
		boolean clearSearchResults = false;
		if(!Utility.isNullOrEmpty(this.facilityId)) {
			FacilityProfile fp = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
			String facilityId = fp.getFacilityId();
			clearSearchResults = facilityId.equalsIgnoreCase(this.facilityId) ? false : true;
		}
		return clearSearchResults;
	}
	
	public String from3rdLevelMenu() {
		// reset search results if the user has navigated to a different facility
		if (clearSearchResults()) {
			reset();
		}

		return PERMIT_CONDITION_SEARCH_OUTCOME;
	}
	
	public void dateByValueChanged(ValueChangeEvent vce) {
		String dateBy = (String)vce.getNewValue();
		if(Utility.isNullOrEmpty(dateBy)) {
			this.searchCriteria.setBeginDt(null);
			this.searchCriteria.setEndDt(null);
		}
	}
	
	public void permitTypeCdValueChanged(ValueChangeEvent vce) {
		String permitTypeCd = (String)vce.getNewValue();
		if(!Utility.isNullOrEmpty(permitTypeCd) 
				&& permitTypeCd.equals(PermitTypeDef.NSR)
				&& !Utility.isNullOrEmpty(this.searchCriteria.getDateBy())
				&& this.searchCriteria.getDateBy().equals(PermitSQLDAO.PERMIT_BASIS_DATE)) {
			this.searchCriteria.setDateBy(null);
			this.searchCriteria.setBeginDt(null);
			this.searchCriteria.setEndDt(null);
		}
	}
	
}
