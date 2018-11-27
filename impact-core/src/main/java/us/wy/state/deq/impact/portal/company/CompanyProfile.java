package us.wy.state.deq.impact.portal.company;

import java.rmi.RemoteException;

import javax.faces.model.SelectItem;

import org.gricdeq.impact.ExternalRole;

import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityRequestList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.webcommon.company.CompanyProfileBase;

@SuppressWarnings("serial")
public class CompanyProfile extends CompanyProfileBase {
	public static final String CMP_OFFSET_TRACKING_SUMMARY_DIALOG = "dialog:companyOffsetTrackingSummary";
	
	private TableSorter cmpFacChoiceWrapper;
	private TableSorter contactRolesWrapper;
	private TableSorter cmpFacRequestWrapper;
	private FacilityList[] authorizedFacilities;
	private FacilityRequestList[] requestedFacilities;
	private ContactRole[] contactRoles;
	public static String FAC_SELECTOR_OUTCOME = "facilitySelector";
	public static String CMP_SELECTOR_OUTCOME = "companySelector";
	private boolean testMode = "true".equals(Config.getEnvEntry("app/testMode"));
	
	private Integer facilityRequestCount = 0;

	public CompanyProfile() {
		super();
		setCmpFacChoiceWrapper(new TableSorter());
		setContactRolesWrapper(new TableSorter());
		setCmpFacRequestWrapper(new TableSorter());
	}

	public Company getCompanyByExternalCompanyId(Long externalCompanyId,
			boolean staging) throws RemoteException {
		this.staging = staging;
		try {
			company = getCompanyService().retrieveCompanyByExternalCompanyId(
					externalCompanyId, staging);
			setOtherParts();
		} catch (RemoteException re) {
			throw re;
		}
		return company;
	}

	public void setOtherParts() {
		if (company != null) {
			// get currently owned facilities
			getAuthorizedFacilities();
			setCmpFacChoiceWrapper();
			
			getRequestedFacilities();
			setCmpFacRequestWrapper();
			
			// Set facilityRequestCount to the number of Pending Facility Creation Requests
			facilityRequestCount = getFacilityRequestCountPending();
		}
	}

	public TableSorter getCmpFacChoiceWrapper() {
		return cmpFacChoiceWrapper;
	}

	public void setCmpFacChoiceWrapper(TableSorter cmpFacChoiceWrapper) {
		this.cmpFacChoiceWrapper = cmpFacChoiceWrapper;
	}

	private void setCmpFacChoiceWrapper() {
		this.cmpFacChoiceWrapper.setWrappedData(authorizedFacilities);
	}

	public FacilityList[] getAuthorizedFacilities() {
		return this.authorizedFacilities;
	}
	


	public SelectItem[] getAuthorizedFacilitySelectItems() {
		FacilityList[] facilities = getAuthorizedFacilities();
		SelectItem[] items = new SelectItem[facilities.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = new SelectItem(facilities[i].getFacilityId(),
					facilities[i].getName());
		}
		return items;
	}

	public FacilityList[] getAuthorizedFacilitiesByUsername(
			String externalUsername) {
		this.authorizedFacilities = null;

		if (company != null) {
			this.authorizedFacilities = getCompanyService()
					.retrieveAuthorizedFacilities(company.getCmpId(),
							externalUsername);
			if (this.authorizedFacilities.length == 0) {
				DisplayUtil
						.displayInfo("There are no facilities currently owned by this company.");
			}

		}
		return this.authorizedFacilities;
	}

	public void setAuthorizedFacilities(FacilityList[] authorizedFacilities) {
		this.authorizedFacilities = authorizedFacilities;
	}
	
	// Methods for Factility Creation Request section of page.
	public TableSorter getCmpFacRequestWrapper() {
		return cmpFacRequestWrapper;
	}

	public void setCmpFacRequestWrapper(TableSorter cmpFacRequestWrapper) {
		this.cmpFacRequestWrapper = cmpFacRequestWrapper;
	}

	private void setCmpFacRequestWrapper() {
		this.cmpFacRequestWrapper.setWrappedData(requestedFacilities);
	}

	public FacilityRequestList[] getRequestedFacilities() {
		return this.requestedFacilities;
	}

	public FacilityRequestList[] getRequestedFacilitiesForCompanyId() {
		this.requestedFacilities = null;

		if (company != null) {

			try {
				requestedFacilities = getFacilityService()
						.searchFacilityRequests(null, null, null, null, null,
								null, null, null, null, null, null, null, null,
								null, unlimitedResults(), null,
								company.getCompanyId());

				DisplayUtil.displayHitLimit(requestedFacilities.length);
				cmpFacRequestWrapper.setWrappedData(requestedFacilities);
				if (requestedFacilities.length == 0) {
					DisplayUtil
							.displayInfo("There are no facility requests for this company.");
				} // else {
					// hasSearchResults = true;
					// }
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Search failed");
				requestedFacilities = new FacilityRequestList[0];
				cmpFacRequestWrapper.setWrappedData(requestedFacilities);
			}
		}

		return this.requestedFacilities;
	}

	public void setRequestedFacilities(FacilityRequestList[] requestedFacilities) {
		this.requestedFacilities = requestedFacilities;
	}
	
	public final Integer getFacilityRequestCountPending() {
		facilityRequestCount = 0;
		if (company == null) {
			logger.debug("getFacilityRequestCountPending: company is null");
		}

		if (company != null) {
			try {
				facilityRequestCount = getFacilityService().retrieveFacilityRequestCount(
						company.getCompanyId());
				
				logger.debug("facilityRequestCount = " + facilityRequestCount);

			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing facility request count failed");
			}
		} else {
			logger.debug("Company is null ... cannot query for facility request count.");
		}
	
		return facilityRequestCount;
	}
	
	public Integer getFacilityRequestCount() {
		return facilityRequestCount;
	}

	public void setFacilityRequestCount(Integer facilityRequestCount) {
		this.facilityRequestCount = facilityRequestCount;
	}
	// end Methods for Factility Creation Request section of page.

	
	
	public String goFacilitySelector() {
		return goFacilitySelector(null);
	}
		
	public String goFacilitySelector(MyTasks mt) {
		if (null == mt) {
			mt = (MyTasks) FacesUtil.getManagedBean("myTasks");
		}
		String externalUsername = mt.getLoginName();

		// refresh company
		try {
			company = getCompanyService().retrieveCompanyByExternalCompanyId(
					mt.getExternalCompanyId(), false);
		} catch (DAOException e) {
			logger.error(
					"An error occurred when retrieving company by CROMERR id: "
							+ mt.getExternalCompanyId(), e);
		} catch (RemoteException e) {
			logger.error(
					"An error occurred when retrieving company by CROMERR id: "
							+ mt.getExternalCompanyId(), e);
		}

		if (!Utility.isNullOrEmpty(externalUsername)) {
			getAuthorizedFacilitiesByUsername(externalUsername);
		}

		getAuthorizedFacilities();
		setCmpFacChoiceWrapper();
		
		getRequestedFacilitiesForCompanyId();
		getRequestedFacilities();
		setCmpFacRequestWrapper();
		
		// Set facilityRequestCount to the number of Pending Facility Creation Requests
		facilityRequestCount = getFacilityRequestCountPending();
		
		mt.hideTabs();
		
		FacesUtil.renderSimpleMenuItem("menuItem_cmpSelector");
		FacesUtil.renderSimpleMenuItem("menuItem_facSelector");

		return FAC_SELECTOR_OUTCOME;
	}

	public String goCompanySelector() {
		MyTasks mt = (MyTasks) FacesUtil.getManagedBean("myTasks");
		String externalUsername = mt.getLoginName();
		if (externalUsername != null && !externalUsername.equals("")) {
			getActiveContactRolesByUsername(externalUsername);
		}

		getActiveContactRoles();
		setContactRolesWrapper();

		mt.setCurrentRole(null);
		mt.setFacilityId(null);

		mt.hideTabs();
		
		FacesUtil.renderSimpleMenuItem("menuItem_cmpSelector");
		FacesUtil.hideSimpleMenuItem("menuItem_facSelector");

		return CMP_SELECTOR_OUTCOME;
	}

	public TableSorter getContactRolesWrapper() {
		return contactRolesWrapper;
	}

	public void setContactRolesWrapper(TableSorter contactRolesWrapper) {
		this.contactRolesWrapper = contactRolesWrapper;
	}

	public void setContactRolesWrapper() {
		this.contactRolesWrapper.setWrappedData(this.contactRoles);
	}

	public ContactRole[] getActiveContactRolesByUsername(String externalUsername) {
		if (!Utility.isNullOrEmpty(externalUsername)) {
			logger.debug("Getting authorized companies for user: "
					+ externalUsername);

			if (testMode) { // bypass STS since we cannot simulate
				String roleId = ExternalRole.PREPARER_ROLE;
				if (externalUsername.contains("esigner")) {
					roleId = ExternalRole.CERTIFIER_ROLE;
				}
				Company[] authorizedCompanies = getCompanyService().retrieveAuthorizedCompanies(externalUsername);
				contactRoles = new ContactRole[authorizedCompanies.length];
				for (int i = 0; i < authorizedCompanies.length; i++) {
					ContactRole contactRole = new ContactRole();
					contactRole.setCompany(authorizedCompanies[i]);
					contactRoles[i] = contactRole;
				}
			} else {
				// check both STS and local db for auth data
				contactRoles = getCompanyService().retrieveActiveContactRolesFromSession(
						externalUsername);
			}

			if (contactRoles.length == 0) {
				DisplayUtil.displayInfo("No companies are available.");
			}
		}

		return contactRoles;
	}

	public ContactRole[] getActiveContactRoles() {
		return contactRoles;
	}

	public void setActiveContactRoles(ContactRole[] contactRoles) {
		this.contactRoles = contactRoles;
	}
	
	public String displayOffsetTrackingSummary() {
		return CMP_OFFSET_TRACKING_SUMMARY_DIALOG;
	}
	
	public boolean isOffsetTrackingInfoAvailable() {
		return this.getCompanyEmissionsOffsetRows().length > 0
				|| this.getEmissionsOffsetAdjustmentWrapper().getRowCount() > 0;
				
	}

}
