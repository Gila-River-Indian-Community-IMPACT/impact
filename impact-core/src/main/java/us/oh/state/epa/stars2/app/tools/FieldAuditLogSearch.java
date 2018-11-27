package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.wy.state.deq.impact.app.company.CompanyProfile;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;


@SuppressWarnings("serial")
public class FieldAuditLogSearch extends AppBase {
	private LinkedHashMap<String, String> fieldAudLogAttrDefs;
	private FieldAuditLog searchFieldAuditLog = new FieldAuditLog();
	private String categoryCd;
	private String selFacilityId;
	private String selCmpId;
	private FieldAuditLog[] fieldAudLogs;
	private boolean hasSearchResults;
	private boolean cmpSearch;
	private TableSorter resultsWrapper;

	private CompanyService companyService;
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;
	
	private Timestamp beginDt = new Timestamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30));
	private Timestamp endDt = new Timestamp(System.currentTimeMillis());

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public FieldAuditLogSearch() {
		super();

		resultsWrapper = new TableSorter();

		cacheViewIDs.add("/tools/fieldAuditLog.jsp");
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public final String submitSearch() {
		fieldAudLogs = null;
		hasSearchResults = false;

		if (categoryCd == null) {
			DisplayUtil
					.displayError("Category is required");
			return FAIL;
		}
		
		if (endDt != null && beginDt !=null && getBeginDt().after(endDt)){
			DisplayUtil.displayError("To Date must not be before From Date");
			return FAIL;
		}
		/*
		if (categoryCd != null) {
			// check to see if all input is null
			if (searchFieldAuditLog.getFacilityId() == null
					&& searchFieldAuditLog.getCmpId() == null) {
				DisplayUtil
						.displayError("Facility ID, Company ID or Category is required");
				return FAIL;
			}

			// CMP Id, facility Id, and Category is all empty
			if (searchFieldAuditLog.getFacilityId() == null) {
				if (searchFieldAuditLog.getCmpId() != null) {
					if ((searchFieldAuditLog.getCmpId().trim().length() == 0)) {
						DisplayUtil
								.displayError("Facility ID, Company ID or Category is required");
						return FAIL;
					}
				}
			}

			// CMP Id, facility Id, and Category is all empty
			if (searchFieldAuditLog.getCmpId() == null) {
				if (searchFieldAuditLog.getFacilityId() != null) {
					if ((searchFieldAuditLog.getFacilityId().trim().length() == 0)) {
						DisplayUtil
								.displayError("Facility ID, Company ID or Category is required");
						return FAIL;
					}
				}
			}

			// check if both facility id and cmp id are empty
			if (searchFieldAuditLog.getFacilityId() != null
					&& searchFieldAuditLog.getCmpId() != null) {
				if (searchFieldAuditLog.getFacilityId().trim().length() == 0
						&& searchFieldAuditLog.getCmpId().trim().length() == 0) {
					DisplayUtil
							.displayError("Facility ID, Company ID or Category is required");
					return FAIL;
				}

			}
		}
		*/

		// prevent both search fields from being searched
		/*
		if (searchFieldAuditLog.getFacilityId() != null
				&& searchFieldAuditLog.getCmpId() != null) {
			// check box length
			if (searchFieldAuditLog.getFacilityId().trim().length() != 0
					&& searchFieldAuditLog.getCmpId().trim().length() != 0) {
				DisplayUtil
						.displayError("Enter only Facility ID or Company ID; not both");
				return FAIL;
			}

		}
		*/

		// check to see if searching for company audit logs
		
/*		if (categoryCd.contains("cmp")) {
			setCmpSearch(true);
			searchFieldAuditLog.setFacilityId(null);
		} else {
			setCmpSearch(false);
		}
*/
		searchFieldAuditLog.setBeginDt(this.beginDt);
		searchFieldAuditLog.setEndDt(this.endDt);
		try {
			fieldAudLogs = getInfrastructureService().searchFieldAuditLog(
					searchFieldAuditLog);

			resultsWrapper.setWrappedData(fieldAudLogs);

			if (fieldAudLogs.length == 0) {
				DisplayUtil
						.displayInfo("Cannot find any field audit logs for this search");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search failed");
		}
		
		searchFieldAuditLog.setUniqueId(null);
		searchFieldAuditLog.setCorrEmuId(null);
		return SUCCESS;
	}

	
	public final String submitProfileFacility() {
		String ret = FAIL;

		Facility facility;
		try {
			facility = getFacilityService().retrieveFacilityData(selFacilityId, -1);
			FacilityProfile facilityProfile = (FacilityProfile) FacesUtil
					.getManagedBean("facilityProfile");
			facilityProfile.setFpId(facility.getFpId());
			facilityProfile.submitProfile();
			ret = "facilityProfile";
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Accessing Current facility detail failed");
			logger.error(re.getMessage(), re);
		}
		return ret;
	}
	
	
	public final String submitProfileCompany() {
		String ret = FAIL;

		// determine which profile to go to when selecting id
		Company company;
		try {
			company = getCompanyService().retrieveCompanyProfile(selCmpId);
			CompanyProfile companyProfile = (CompanyProfile) FacesUtil
					.getManagedBean("companyProfile");
			companyProfile.setCmpId(company.getCmpId());
			companyProfile.submitProfile();
			ret = "companyProfile";
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Accessing Current company detail failed");
			logger.error(re.getMessage(), re);
		}
		return ret;
	}
	
	public final String reset() {
		searchFieldAuditLog = new FieldAuditLog();
		categoryCd = null;
		fieldAudLogs = null;
		fieldAudLogAttrDefs = null;
		hasSearchResults = false;
		resultsWrapper = new TableSorter();
		beginDt = new Timestamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30));
		endDt = new Timestamp(System.currentTimeMillis());
		return SUCCESS;
	}

	public final FieldAuditLog getSearchFieldAuditLog() {
		return searchFieldAuditLog;
	}

	public final void setSearchFieldAuditLog(FieldAuditLog searchFieldAuditLog) {
		this.searchFieldAuditLog = searchFieldAuditLog;
	}

	public final LinkedHashMap<String, String> getFieldAudLogAttrDefs() {
		return fieldAudLogAttrDefs;
	}

	private void retrieveFieldAudLogAttrDefs() {
		try {
			SimpleDef[] tempDefs = getInfrastructureService().retrieveSimpleDefs(
					"fl_attribute_category_def", "attribute_cd",
					"attribute_dsc", "deprecated", null, "category_cd",
					categoryCd);

			fieldAudLogAttrDefs = new LinkedHashMap<String, String>();
			for (SimpleDef tempDef : tempDefs) {
				if (tempDef.getCode().equals("eues")
						|| tempDef.getCode().equals("eups")
						|| tempDef.getCode().equals("eupt")
						|| tempDef.getCode().equals("eutc")){
					// do nothing
				} else {
					fieldAudLogAttrDefs.put(tempDef.getDescription(),
							tempDef.getCode());
				}
			}
		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}
	}

	public final String getCategoryCd() {
		return categoryCd;
	}

	public final void setCategoryCd(String categoryCd) {
		this.categoryCd = categoryCd;
		retrieveFieldAudLogAttrDefs();
		searchFieldAuditLog.setCategoryCd(categoryCd);
	}

	public final TableSorter getResultsWrapper() {
		return resultsWrapper;
	}

	public final void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public final void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public final String getSelFacilityId() {
		return selFacilityId;
	}

	public final void setSelFacilityId(String selFacilityId) {
		this.selFacilityId = selFacilityId;
	}

	public String refreshSearchAudLog() {
		if (hasSearchResults) {
			submitSearch();
		}

		return "tools.fieldAuditLog";
	}

	public void restoreCache() {
	}

	public void clearCache() {
		// if (resultsWrapper != null) {
		// resultsWrapper.clearWrappedData();
		// }
		//
		// fieldAudLogs = null;
		// hasSearchResults = false;
	}

	public boolean getCmpSearch() {
		return cmpSearch;
	}

	public void setCmpSearch(boolean cmpSearch) {
		this.cmpSearch = cmpSearch;
	}

	public String getSelCmpId() {
		return selCmpId;
	}

	public void setSelCmpId(String selCmpId) {
		this.selCmpId = selCmpId;
	}
	
	public Timestamp getBeginDt() {
		return this.beginDt;
	}
	
	public void setBeginDt(Timestamp beginDt) {
		this.beginDt = beginDt;
	}
	
	public Timestamp getEndDt() {
		return this.endDt;
	}

	public void setEndDt(Timestamp endDt) {
		this.endDt = endDt;
	}
	
}
