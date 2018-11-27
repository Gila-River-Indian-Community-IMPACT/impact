package us.oh.state.epa.stars2.app.facility;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.def.DOLaaCeta;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.wy.state.deq.impact.app.company.CompanySearch;

public class FacilitySearch extends AppBase {
	
	private static final long serialVersionUID = 3705016202981533289L;

	private String facilityName;
	private String facilityId;
	private String operatingStatusCd = OperatingStatusDef.OP;
	private String countyCd;
	private String doLaaCd;
	private String permitClassCd;
	private String naicsCd;
	private String tvPermitStatusCd;
	private String portable;
	private String portableGroupCd;
	private FacilityList[] facilities;
	private boolean hasSearchResults;
	private TableSorter resultsWrapper;
	private String address1;
	private Integer corePlaceId;
	private String facilityTypeCd;
	private String companyName;

	private FacilityService facilityService;
	
	private CompanySearch companySearch;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public CompanySearch getCompanySearch() {
		return companySearch;
	}

	public void setCompanySearch(CompanySearch companySearch) {
		this.companySearch = companySearch;
	}

	public final String getCompanyName() {
		return companyName;
	}

	public final void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public FacilitySearch() {
		super();

		resultsWrapper = new TableSorter();

		cacheViewIDs.add("/facilities/facilitySearch.jsp");
	}

	public final TableSorter getResultsWrapper() {
		return resultsWrapper;
	}

	public final String getFacilityId() {
		return facilityId;
	}

	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final String getFacilityName() {
		return facilityName;
	}

	public final void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public final String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public final void setOperatingStatusCd(String operatingStatusCd) {
		this.operatingStatusCd = operatingStatusCd;
	}

	public final String getCountyCd() {
		return countyCd;
	}

	public final void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final List<SelectItem> getDoLaas() {
		DefData dd = DoLaaDef.getData();
		DefSelectItems items = dd.getItems();
		return items.getAllSearchItems();
	}

	public final List<SelectItem> getDoLaasCeta() {
		return DOLaaCeta.getData().getItems().getAllSearchItems();
	}

	public final String getPermitClassCd() {
		return permitClassCd;
	}

	public final void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

	public final String getTvPermitStatusCd() {
		return tvPermitStatusCd;
	}

	public final void setTvPermitStatusCd(String tvPermitStatusCd) {
		this.tvPermitStatusCd = tvPermitStatusCd;
	}
	
	public LinkedHashMap<String, String> getFacilitiesByCompany(String company) {
		LinkedHashMap<String, String> companyFacilities = 
				new LinkedHashMap<String, String>();
		if (StringUtils.isNotBlank(company)) {
			setCompanyName(company);
			submitSearch(false);
			if (hasSearchResults) {
				for (FacilityList f : getFacilities()) {
					companyFacilities.put(f.getFacilityId() + " - " + 
							f.getName(),f.getFacilityId());
				}
			}
		}
		return companyFacilities;
	}

	public String submitSearch() {
		return submitSearch(true);
	}

	public String submitSearch(boolean displayInfo) {
		facilities = null;
		hasSearchResults = false;

		try {
			facilities = getFacilityService().searchFacilities(facilityName,
					facilityId, companyName, corePlaceId, countyCd,
					operatingStatusCd, doLaaCd, naicsCd, permitClassCd,
					tvPermitStatusCd, address1, null, null, null,
					portableGroupCd, unlimitedResults(), facilityTypeCd);
			DisplayUtil.displayHitLimit(facilities.length);
			resultsWrapper.setWrappedData(facilities);
			if (facilities.length == 0) {
				if (displayInfo) {
					DisplayUtil
							.displayInfo("There are no facilities that match the search criteria");
				}
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search failed");
			facilities = new FacilityList[0];
			resultsWrapper.setWrappedData(facilities);
		}

		return SUCCESS;
	}

	public String reset() {
		address1 = null;
		facilityName = null;
		facilityId = null;
		companyName = null;
		corePlaceId = null;
		operatingStatusCd = OperatingStatusDef.OP;
		countyCd = null;
		doLaaCd = null;
		permitClassCd = null;
		naicsCd = null;
		tvPermitStatusCd = null;
		facilities = null;
		portableGroupCd = null;
		portable = null;
		resultsWrapper.clearWrappedData();
		hasSearchResults = false;
		facilityTypeCd = null;
		return SUCCESS;
	}

	public final boolean getHasSearchResults() {
		return hasSearchResults;
	}

	public FacilityList[] getFacilities() {
		return facilities;
	}

	public String refreshSearchFacilities() {
		FacilityProfile fp = (FacilityProfile) FacesUtil
				.getManagedBean("facilityProfile");
		fp.setFacility(null);
		fp.setTreeData(null);

		return "facilitySearch";
	}

	public String getNaicsCd() {
		return naicsCd;
	}

	public void setNaicsCd(String naicsCd) {
		this.naicsCd = naicsCd;
	}

	public void restoreCache() {
		// submitSearch();
	}

	public void clearCache() {
		// if (resultsWrapper != null) {
		// resultsWrapper.clearWrappedData();
		// }
		//
		// facilities = null;
		// hasSearchResults = false;
	}

	public String getPortable() {
		return portable;
	}

	public void setPortable(String portable) {
		this.portable = portable;
		if (portable == null || portable.equals("N")) {
			portableGroupCd = null;
		}
	}

	public String getPortableGroupCd() {
		return portableGroupCd;
	}

	public void setPortableGroupCd(String portableGroupCd) {
		this.portableGroupCd = portableGroupCd;
	}

	public final void setFacilities(FacilityList[] facilities) {
		this.facilities = facilities;
	}

	public final void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public final String getAddress1() {
		return address1;
	}

	public final void setAddress1(String address1) {
		this.address1 = address1;
	}

	public final Integer getCorePlaceId() {
		return corePlaceId;
	}

	public final void setCorePlaceId(Integer corePlaceId) {
		this.corePlaceId = corePlaceId;
	}

	public final void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
		this.portable = FacilityTypeDef.isPortable(facilityTypeCd);
	}

	public final String getFacilityTypeCd() {
		return facilityTypeCd;
	}
}
