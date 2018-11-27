package us.oh.state.epa.stars2.webcommon.correspondence;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.CorrespondenceService;
import us.oh.state.epa.stars2.database.dao.document.CorrespondenceSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.def.CityDef;
import us.oh.state.epa.stars2.def.CorrespondenceCategoryDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;

@SuppressWarnings("serial")
public class CorrespondenceSearch extends AppBase {
	private String correspondenceDesc;
	private boolean hasSearchResults;
	private Correspondence searchObj = new Correspondence();
	private Correspondence[] correspondence;
	private String fromFacility = "false";
	private String fromEnforcementAction = "false";
	private String fromLinkedTo = "none";
	private Timestamp startDate = null;
	private Timestamp endDate = null;
	private TableSorter resultsWrapper;
	private TableSorter facResultsWrapper;
	private TableSorter enfResultsWrapper;
	private String dateField;
	private CorrespondenceSearch backup;
		
    private CorrespondenceService correspondenceService;
    
    public static String SEARCH_OUTCOME = "correspondence.search";
    
    public CorrespondenceSearch() {
    	super();
    	
    	resultsWrapper = new TableSorter();
    	facResultsWrapper = new TableSorter();
    	enfResultsWrapper = new TableSorter();
    }

    public CorrespondenceService getCorrespondenceService() {
		return correspondenceService;
	}

	public void setCorrespondenceService(CorrespondenceService correspondenceService) {
		this.correspondenceService = correspondenceService;
	}

	public final String submitSearch() {
		correspondence = null;
		hasSearchResults = false;

		try {
			resultsWrapper = new TableSorter();
    		facResultsWrapper = new TableSorter();
    		enfResultsWrapper = new TableSorter();
			correspondence = getCorrespondenceService().searchCorrespondence(searchObj, dateField, startDate, endDate);
			resultsWrapper.setWrappedData(correspondence);
			facResultsWrapper.setWrappedData(correspondence);
			//enfResultsWrapper.setWrappedData(correspondence); // this is intentionally commented out - Sharon
			                                                    // This method is not used to set enfResultsWrapper.
																// See setCorresForLinkedByTo().
			if (correspondence.length == 0) {
				DisplayUtil
						.displayInfo("Cannot find any correspondence for this search.");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			DisplayUtil.displayError("Search Failed.");
			logger.error(re.getMessage(), re);
		}
		return SUCCESS;
	}

	public final String reset() {
		searchObj = new Correspondence();
		hasSearchResults = false;
		startDate = null;
		endDate = null;
		dateField = null;
		resultsWrapper.clearWrappedData();
		facResultsWrapper.clearWrappedData();
		enfResultsWrapper.clearWrappedData();
		return SUCCESS;
	}

	public final String getCorrespondenceDesc() {
		return correspondenceDesc;
	}

	public final void setCorrespondenceDesc(String correspondenceDesc) {
		this.correspondenceDesc = correspondenceDesc;
	}

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public final void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public final Correspondence[] getCorrespondence() {
		return correspondence;
	}

	public final TableSorter getResultsWrapper() {
		if (fromFacility.equals("false")) {
			return resultsWrapper;
		}

		return facResultsWrapper;
	}

	public final void setResultsWrapper(TableSorter resultsWrapper) {
		if (fromFacility.equals("false")) {
			this.resultsWrapper = resultsWrapper;
		} else {
			this.facResultsWrapper = resultsWrapper;
		}
	}
	
	public TableSorter getEnfResultsWrapper() {
		return enfResultsWrapper;
	}

	public void setEnfResultsWrapper(TableSorter enfResultsWrapper) {
		this.enfResultsWrapper = enfResultsWrapper;
	}

	public final Integer getCorrespondenceId() {
		return searchObj.getCorrespondenceID();
	}

	public final void setCorrespondenceId(Integer correspondenceId) {
		searchObj.setCorrespondenceID(correspondenceId);
	}

	public final Integer getFpId() {
		return searchObj.getFpId();
	}

	public final void setFpId(Integer fpId) {
		searchObj.setFpId(fpId);
	}

	public final String getFacilityId() {
		return searchObj.getFacilityID();
	}

	public final void setFacilityId(String facilityId) {
		searchObj.setFacilityID(facilityId);
	}

	public final String getFacilityNm() {
		return searchObj.getFacilityNm();
	}

	public final void setFacilityNm(String facilityName) {
		searchObj.setFacilityNm(facilityName);
	}

	public final Timestamp getDateGenerated() {
		return searchObj.getDateGenerated();
	}

	public final void setDateGenerated(Timestamp dateGenerated) {
		searchObj.setDateGenerated(dateGenerated);
	}

	public final String getCorrespondenceTypeCd() {
		return searchObj.getCorrespondenceTypeCode();
	}

	public final void setCorrespondenceTypeCd(String correspondenceTypeCd) {
		searchObj.setCorrespondenceTypeCode(correspondenceTypeCd);
	}

	public final String getCorrespondenceTypeDescription() {
		return searchObj.getCorrespondenceTypeDescription();
	}

	public final String getAdditionalInfo() {
		return searchObj.getAdditionalInfo();
	}

	public final void setAdditionalInfo(String additionalInfo) {
		searchObj.setAdditionalInfo(additionalInfo);
	}

	public final List<SelectItem> getCorrespondenceDef() {
		return CorrespondenceDef.getDescriptionData().getItems().getItems(
				searchObj.getCorrespondenceTypeCode(), true);
	}
	
	public final List<SelectItem> getCorrespondenceCategoryDef() {
		return CorrespondenceCategoryDef.getData().getItems().getCurrentItems();
	}

	public final List<SelectItem> getCorrespondenceDirectionDef() {
		return CorrespondenceDirectionDef.getData().getItems().getCurrentItems();
	}
	
	public String getFromFacility() {
		return fromFacility;
	}

	public void setFromFacility(String fromFacility) {
		this.fromFacility = fromFacility;
	}
	
    public String getFromEnforcementAction() {
		return fromEnforcementAction;
	}

	public void setFromEnforcementAction(String fromEnforcementAction) {
		this.fromEnforcementAction = fromEnforcementAction;
	}

	public final Timestamp getStartDate() {
        return startDate;
    }

    public final void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public final Timestamp getEndDate() {
        return endDate;
    }

    public final void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

	public final String getFromLinkedTo() {
		return fromLinkedTo;
	}

	public final void setFromLinkedTo(String fromLinkedTo) {
		this.fromLinkedTo = fromLinkedTo;
	}
	
	public void setCorresForLinkedByTo(Correspondence[] correspondence) {
		// It is intentional that we don't refresh resultsWrapper and facResultsWrapper here.
		// See submitSearch().
		this.correspondence = correspondence;
    	enfResultsWrapper = new TableSorter();
		enfResultsWrapper.setWrappedData(correspondence);
		if (correspondence.length != 0) {
			hasSearchResults = true;
		}
	}
	
	public String refreshSearch() {	
		this.setFromFacility("false");
		this.setFromEnforcementAction("false");
		
        if (backup != null) {
            copy(backup);
            backup = null;
            this.submitSearch();
        }
      
		return SEARCH_OUTCOME;
	}
	
	public final LinkedHashMap<String, String> getCorrespondenceDateFields() {
		return buildCorrespondenceDateFields(this.searchObj.getDirectionCd());
	}
	
	public static LinkedHashMap<String, String> buildCorrespondenceDateFields(String directionCd) {
		LinkedHashMap<String, String> correspondenceDateFields = new LinkedHashMap<String, String>();
		
		if (Utility.isNullOrEmpty(directionCd)) {
			correspondenceDateFields.put("Date Generated", CorrespondenceSQLDAO.DATE_GENERATED);
			correspondenceDateFields.put("Receipt Date", CorrespondenceSQLDAO.RECEIPT_DATE);
		} else {
			if (directionCd.equalsIgnoreCase(CorrespondenceDirectionDef.INCOMING)) {
				correspondenceDateFields.put("Receipt Date", CorrespondenceSQLDAO.RECEIPT_DATE);
			} 
			
			if (directionCd.equalsIgnoreCase(CorrespondenceDirectionDef.OUTGOING)) {
				correspondenceDateFields.put("Date Generated", CorrespondenceSQLDAO.DATE_GENERATED);
			}
		}
		
		return correspondenceDateFields;
	}

	public String getDateField() {
		return dateField;
	}

	public void setDateField(String dateField) {
		this.dateField = dateField;
	}
	
	public final String getDirectionCd() {
		return searchObj.getDirectionCd();
	}

	public final void setDirectionCd(String directionCd) {
		searchObj.setDirectionCd(directionCd);
	}
	
	public final String getCorrespondenceCategoryCd() {
		return searchObj.getCorrespondenceCategoryCd();
	}

	public final void setCorrespondenceCategoryCd(String correspondenceCategoryCd) {
		searchObj.setCorrespondenceCategoryCd(correspondenceCategoryCd);
	}

	public String getCompanyId() {
		return searchObj.getCompanyId();
	}

	public void setCompanyId(String companyId) {
		searchObj.setCompanyId(companyId);
	}
	
	public final String getDistrict() {
        return searchObj.getDistrict();
    }

    public final void setDistrict(String district) {
        searchObj.setDistrict(district);
    }

    public final List<SelectItem> getDistricts() {
        return getDoLaas(getDistrict());
    }
    
    public final String getCountyCd() {
        return searchObj.getCountyCd();
    }

    public final void setCountyCd(String countyCd) {
        searchObj.setCountyCd(countyCd);
    }

    public final List<SelectItem> getCountiesAll() {
        return searchObj.getCountiesAll();
    }
    
    public final String getCityCd() {
        return searchObj.getCityCd();
    }

    public final void setCityCd(String cityCd) {
        searchObj.setCityCd(cityCd);
    }

    public final List<SelectItem> getCitiesAll() {
        return searchObj.getCitiesAll();
    }
    
    public final String getCorId() {
		return searchObj.getCorId();
	}

	public final void setCorId(String corId) {
		searchObj.setCorId(corId);
	}
    
    public final List<SelectItem> getCountyCds() {
		return County.getData().getItems()
				.getItems(getCountyCd(), true);
	}
    
//    public final List<SelectItem> getCityCds() {
//		return City.getData().getItems()
//				.getItems(getCityCd(), true);
//	}
    
    public final List<SelectItem> getCounties() {
		List<SelectItem> ret = County
				.getDistrictCounties(getDistrict());
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}
    
    public final List<SelectItem> getCities() {
		List<SelectItem> ret = CityDef
				.getCountyCities(getCountyCd());
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}
    
    public Correspondence getSearchObj() {
		return searchObj;
	}

	public void setSearchObj(Correspondence searchObj) {
		this.searchObj = searchObj;
	}

	public void copy(CorrespondenceSearch cso) {
		setSearchObj(cso.getSearchObj());
    }
    
    public void setBackup(CorrespondenceSearch backup) {
        this.backup = backup;
    }
	
}
