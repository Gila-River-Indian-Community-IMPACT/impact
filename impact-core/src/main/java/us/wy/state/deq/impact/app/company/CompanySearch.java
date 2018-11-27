package us.wy.state.deq.impact.app.company;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

public class CompanySearch extends AppBase {

	private static final long serialVersionUID = -6941580630700610343L;

	private String companyId;
	private String companyName;
	private String companyAlias;
	private String phone;
	private String externalCompanyId;

	private Company[] companies;
	private LinkedHashMap<String, String> allCompanies;
	private boolean hasSearchResults;
	private TableSorter resultsWrapper;

	private CompanyService companyService;
	
	private CompanyProfile companyProfile;

	public CompanySearch() {
		super();
		resultsWrapper = new TableSorter();
		cacheViewIDs.add("/companies/companySearch.jsp");
	}

	public CompanyProfile getCompanyProfile() {
		return companyProfile;
	}

	public void setCompanyProfile(CompanyProfile companyProfile) {
		this.companyProfile = companyProfile;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public String getCompanyAlias() {
		return companyAlias;
	}

	public void setCompanyAlias(String companyAlias) {
		this.companyAlias = companyAlias;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getExternalCompanyId() {
		return externalCompanyId;
	}

	public void setExternalCompanyId(String externalCompanyId) {
		this.externalCompanyId = externalCompanyId;
	}

	public Company[] getCompanies() {
		return companies;
	}

	public void setCompanies(Company[] companies) {
		this.companies = companies;
	}

	public boolean getHasSearchResults() {
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

	public LinkedHashMap<String, String> getAllCompanies() {
		//if (allCompanies == null) {
			try {
				Company[] tempCompanies = getCompanyService().retrieveCompanies();

				allCompanies = new LinkedHashMap<String, String>();
				for (Company tempCompany : tempCompanies) {
					allCompanies.put(tempCompany.getName(),
							tempCompany.getCmpId());
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				DisplayUtil
						.displayError("System error. Please contact system administrator");
			}
		//}
		return allCompanies;
	}

	public void setAllCompanies(LinkedHashMap<String, String> allCompanies) {
		this.allCompanies = allCompanies;
	}

	public String submitSearch() {
		companies = null;
		hasSearchResults = false;

		Map<String, String> params = new HashMap<String, String>();
		params.put("id", companyId);
		params.put("name", companyName);
		params.put("alias", companyAlias);
		params.put("phone", phone);
		params.put("enviteId", externalCompanyId);
		try {
			companies = getCompanyService().searchCompanies(params, unlimitedResults());
			DisplayUtil.displayHitLimit(companies.length);
			resultsWrapper.setWrappedData(companies);
			if (companies.length == 0) {
				DisplayUtil
						.displayInfo("There are no companies that match the search criteria");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search failed");
			companies = new Company[0];
			resultsWrapper.setWrappedData(companies);
		}

		return SUCCESS;
	}

	public String reset() {
		companyName = null;
		companyId = null;
		companyAlias = null;
		externalCompanyId = null;
		phone = null;
		companies = null;
		resultsWrapper.clearWrappedData();
		hasSearchResults = false;

		return SUCCESS;
	}

	public String refreshSearchCompanies() {
		companyProfile.setCompany(null);
		companyProfile.setTreeData(null);

		return "companySearch";
	}

}
