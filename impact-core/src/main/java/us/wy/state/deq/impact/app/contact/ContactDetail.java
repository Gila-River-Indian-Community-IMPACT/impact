package us.wy.state.deq.impact.app.contact;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.gricdeq.impact.ExternalRole;
import org.gricdeq.impact.ScsExceptionHandler;
import org.gricdeq.scs.ScsSharedPortalClient;
import org.gricdeq.scs.ScsUserManagementClient;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.RetrieveRolesWithOrganizationsForDataflowAndPartnerResponse;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.RetrieveUserResponse;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.UserRoleWithOrganizationType;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.UserType;
import org.gricdeq.scs.schema.sharedcromerr.usermgmt._1.AuthenticateResponse;

import net.exchangenetwork.wsdl.sharedcromerr.portal._1.SharedPortalException;
import net.exchangenetwork.wsdl.sharedcromerr.usermgmt._1.SharedCromerrException;
import oracle.adf.view.faces.context.AdfFacesContext;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.util.SendMail;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.correspondence.CreateCorrespondence;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.webcommon.contact.ContactDetailBase;

public class ContactDetail extends ContactDetailBase {

	private static final long serialVersionUID = -6503387910025692417L;

	public static final String CPD_OUTCOME = "contactPortalAccount";
	private ContactRole[] contactRoles = new ContactRole[0];
	private TableSorter contactRolesWrapper = new TableSorter();
	private FacilityList[] companyExcludedFacilities;
	private FacilityList[] allExcludedFacilities;
	private TableSorter companyFacilityExclusionListWrapper = new TableSorter();
	private TableSorter facilityExclusionListWrapper = new TableSorter();
	private boolean facilityExclusionListEditable;
	private String authorizedCmpId;
	private boolean portalDetailEditDisabled;
	private boolean testMode;
	private String externalFirstName;
	private String externalLastName;
	private String externalEmail;
	private String mailSubject;
	private String mailMessage;
	private List<String> contactCmpIds = new ArrayList<String>();
	
	//TODO relocate these down to the business/data tier
	private ScsUserManagementClient userManagementClient;
	private ScsSharedPortalClient portalClient;

	public ContactDetail() {
		super();
		testMode = "true".equals(Config.getEnvEntry("app/testMode"));
		this.userManagementClient = App.getApplicationContext().getBean(ScsUserManagementClient.class);
		this.portalClient = App.getApplicationContext().getBean(ScsSharedPortalClient.class);

	}


	public final String refreshContactDetail() {
		String outcome = CTP_OUTCOME;
		if (contactId != null) {
			TreeNode tempSelectedTreeNode = selectedTreeNode;
			String tempCurrent = current;
			refreshContact();
			selectedTreeNode = tempSelectedTreeNode;
			current = tempCurrent;
		} else {
			outcome = "contactSearch";
		}
		return outcome;
	}

	//TODO move these to a common location
	private static final String SCS_ADMIN_ID = (String) Config.getEnvEntry("app/scsAdminId", "SCS_ADMIN_ID_NOT_FOUND");
	private static final String SCS_ADMIN_PWD = (String) Config.getEnvEntry("app/scsAdminPassword",
			"SCS_ADMIN_PWD_NOT_FOUND");
	private static final String SCS_DATAFLOW = (String) Config.getEnvEntry("app/scsDataflow", "SCS_DATAFLOW_NOT_FOUND");
	private static final String SCS_PARTNER = (String) Config.getEnvEntry("app/scsPartner", "SCS_PARTNER_NOT_FOUND");

	
	private List<ContactRole> transform(List<UserRoleWithOrganizationType> roles) {
		List<ContactRole> contactRoles = new ArrayList<ContactRole>();
		for (UserRoleWithOrganizationType role : roles) {
			contactRoles.add(new ContactRole(new ExternalRole(role)));
		}
		return contactRoles;
	}

	public String refreshPortalDetail() {
		setEditable(false);
		setFacilityExclusionListEditable(false);
		setContactRoles(new ContactRole[0]);
		setContactRolesWrapper(new TableSorter());
		companyExcludedFacilities = null;
		allExcludedFacilities = null;
		setCompanyFacilityExclusionListWrapper(new TableSorter());
		setFacilityExclusionListWrapper(new TableSorter());
		setAuthorizedCmpId(null);
		setExternalFirstName(null);
		setExternalLastName(null);
		setExternalEmail(null);
		this.contactCmpIds = new ArrayList<String>();

		if (contactId == null) {
			DisplayUtil.displayError("Contact doesn't currently exist.");
			return "contactSearch";
		}

		refreshContactDetail();

		// get cromerr organizations
		if (testMode && null != getContact().getExternalUser()) { // bypass STS since we cannot simulate easily
			logger.debug("----> TEST MODE !!! searching for all 'oil and gas' companies as test data ... ");
			DisplayUtil.displayInfo("Test mode enabled.  Communication with CROMERR service not attempted.");
			setupTestExternalOrgs();
		} else {
			Contact contact = getContact();
			
			if (null != contact && null != contact.getExternalUser() && 
					!StringUtils.isBlank(contact.getExternalUser().getUserName()) &&
					contact.getExternalUser().getUserName().length() >= 8) {
				AuthenticateResponse authenticateResponse = null;
				try {
					authenticateResponse = userManagementClient.authenticate(SCS_ADMIN_ID, SCS_ADMIN_PWD);
				} catch (SharedCromerrException e) {
					ScsExceptionHandler.handleException(e);
					return CPD_OUTCOME;
				}
				String adminToken = authenticateResponse.getSecurityToken();
				
				RetrieveUserResponse response = null;
				try {
					response = portalClient.retrieveUser(adminToken, contact.getExternalUser().getUserName());
				} catch (SharedPortalException e) {
					ScsExceptionHandler.handleException(e);
					return CPD_OUTCOME;
				}
				if (null != response) {
					UserType user = response.getUser();
					contact.getExternalUser().setFirstName(user.getFirstName());
					contact.getExternalUser().setLastName(user.getLastName());
					this.contactRoles = retrieveContactRoles(contact, adminToken);
				}
			} else {
				DisplayUtil.displayWarning("Unable to collect additional contact details from the CROMERR system.  If this contact is a portal user, please ensure the contact has a valid CROMERR username between 8 and 150 characters long.");
			}

		}

			
			
		if (null != contactRoles) {
			if (contactRoles.length > 0) {
				for (ContactRole contactRole : contactRoles) {
					if (contactRole.getExternalRole() != null && contactRole.getCompany() != null) {
						contactCmpIds.add(contactRole.getCompany().getCmpId());
					}
				}
			} else {
				// don't display previous contact's info if this one has no
				// cromerr mapping
				externalEmail = null;
				externalFirstName = null;
				externalLastName = null;
			}
		}
		contactRolesWrapper.setWrappedData(contactRoles);
		
		// get global facility exclusion list
		retrieveAllExcludedFacilities();
		
		// filter out facilities for companies we are not displaying
		List<FacilityList> displayableExcludedFacilitiesList = new ArrayList<FacilityList>();
		for (FacilityList facility : allExcludedFacilities) {
			for (ContactRole contactRole : contactRoles) {
				if (contactRole.getCompany().getCmpId().equals(facility.getCmpId())) {
					displayableExcludedFacilitiesList.add(facility);
				}
				contactRole.setTotalExcludedFacilities(displayableExcludedFacilitiesList.size());
			}
		}
		FacilityList[] displayableExcludedFacilities = 
				displayableExcludedFacilitiesList.toArray(new FacilityList[0]);
		facilityExclusionListWrapper.setWrappedData(displayableExcludedFacilities);
		
		
		
		return CPD_OUTCOME;
	}


	private ContactRole[] retrieveContactRoles(Contact contact, String adminToken) {
		RetrieveRolesWithOrganizationsForDataflowAndPartnerResponse response = null;
		try {
			response = portalClient.retrieveRolesWithOrganizationsForDataflowAndPartner(adminToken, contact.getExternalUser().getUserName(), SCS_DATAFLOW, SCS_PARTNER);
		} catch (SharedPortalException e) {
			ScsExceptionHandler.handleException(e);
			return new ContactRole[0];
		}
		List<UserRoleWithOrganizationType> roles = response.getRoleWithOrganization();
		
		List<UserRoleWithOrganizationType> validatedRoles = new ArrayList<UserRoleWithOrganizationType>();
		for (UserRoleWithOrganizationType role : roles) {
			if (ExternalRole.CERTIFIER_ROLE.equals(role.getRoleName()) ||
					ExternalRole.PREPARER_ROLE.equals(role.getRoleName())) {
				validatedRoles.add(role);
			}
		}

		ContactRole[] contactRoles = transform(validatedRoles).toArray(new ContactRole[0]);
		

		Company[] authorizedCompanies = getCompanyService()
				.retrieveAuthorizedCompanies(contact.getExternalUser().getUserName());
		List<Integer> authorizedCompanyIds = new ArrayList<Integer>();
		for (Company company : authorizedCompanies) {
			authorizedCompanyIds.add(company.getCompanyId());
		}

		Long externalOrgId = null;
		String externalOrgName = null;
		try {
			for (ContactRole contactRole : contactRoles) {
				externalOrgId = contactRole.getExternalRole().getOrganization().getOrganizationId();
				externalOrgName = contactRole.getExternalRole().getOrganization().getOrganizationName();
				Company company = getCompanyService().retrieveCompanyByExternalCompanyId(externalOrgId, false);
				if (null != company) {
					contactRole.setCompany(company);
					
					//TODO ...
//					int totalExcludedFacilities = 0;
//					if (excludedFacilities != null && excludedFacilities.length > 0) {
//						for (FacilityList excludedFacility : excludedFacilities) {
//							if (excludedFacility.getCmpId().equals(
//									company.getCmpId())) {
//								totalExcludedFacilities++;
//							}
//						}
//					}
//	
//					FacilityList[] companyFacilities = company.getFacilities();
//					int totalFacilitiesOwned = 0;
//					if (companyFacilities != null && companyFacilities.length > 0) {
//						for (FacilityList ownedFacility : companyFacilities) {
//							if (ownedFacility.getEndDate() == null) {
//								totalFacilitiesOwned++;
//							}
//						}
//					}
//					externalOrg.setTotalFacilitiesOwned(totalFacilitiesOwned); //TODO
//					externalOrg.setTotalExcludedFacilities(totalExcludedFacilities); //TODO
					
					if ("Active".equalsIgnoreCase(contactRole.getExternalRole().getUserRoleStatus())) {
						contactRole.setActive(authorizedCompanyIds.contains(company.getCompanyId()));
					}
					
					
				} else {
					DisplayUtil.displayWarning("Company (" + externalOrgName + ") not found with CROMERR ID: " + externalOrgId);
					DisplayUtil.displayWarning("Please check to see if a Company (" + externalOrgName + ") needs to be configured with this CROMERR ID: " + externalOrgId);
				}
			}
		} catch (DAOException e) {
			throw new RuntimeException("error getting company for external id = " + externalOrgId + ": " + e);
		}
		return contactRoles;
	}
	
	private void setupTestExternalOrgs() {
		try {
			Map<String,String> params = new HashMap<String,String>();
			params.put("name", "%oil and gas%");
			Company[] sampleCompanies = getCompanyService().searchCompanies(params, true);
			if(sampleCompanies == null || sampleCompanies.length == 0){
				sampleCompanies = new Company[0];
			} 
			contactRoles = new ContactRole[sampleCompanies.length];
			for (int i = 0; i < sampleCompanies.length; i++) {
				ContactRole contactRole = new ContactRole();
//				externalRole.setOrganization(sampleCompanies[i]);
				
				//TODO redo this 
				
//				externalOrg.setContactAuthorized(true);
//				externalOrg.setClaimRequestApproved(true);
//				externalOrg.setRoleId(1);
//				String externalCompanyId = sampleCompanies[i].getExternalCompanyId();
//				if (externalCompanyId != null) {
//					externalOrg.setOrganizationId(Integer.parseInt(externalCompanyId));
//				}
//				externalOrg.setContactEmail("test@unicon-intl.com");
//				externalOrg.setContactFirstName("test.firstname");
//				externalOrg.setContactLastName("test.lastName");
				
				contactRoles[i] = contactRole;
			}
		} catch (RemoteException e) {
			handleException(e);
		}
	}
	
	public String getExternalFirstName() {
		return externalFirstName;
	}


	public void setExternalFirstName(String externalFirstName) {
		this.externalFirstName = externalFirstName;
	}


	public String getExternalLastName() {
		return externalLastName;
	}


	public void setExternalLastName(String externalLastName) {
		this.externalLastName = externalLastName;
	}


	public String getExternalEmail() {
		return externalEmail;
	}


	public void setExternalEmail(String externalEmail) {
		this.externalEmail = externalEmail;
	}



	public ContactRole[] getContactRoles() {
		return contactRoles;
	}


	public void setContactRoles(ContactRole[] contactRoles) {
		this.contactRoles = contactRoles;
	}


	public TableSorter getContactRolesWrapper() {
		return contactRolesWrapper;
	}


	public void setContactRolesWrapper(TableSorter contactRolesWrapper) {
		this.contactRolesWrapper = contactRolesWrapper;
	}


	public FacilityList[] retrieveAllExcludedFacilities() {
		allExcludedFacilities = getContactService().retrieveExcludedFacilities(getContact());
		List<FacilityList> filteredExcludedFacilities = new ArrayList<FacilityList>();
		for (FacilityList f : allExcludedFacilities) {
			if (contactCmpIds.contains(f.getCmpId())) {
				filteredExcludedFacilities.add(f);
			}
		}
		return filteredExcludedFacilities.toArray(new FacilityList[0]);
	}

	public FacilityList[] retrieveCompanyExcludedFacilities() {
		companyExcludedFacilities = null;

		try {
			// retrieve company facility exclusion list
			if (authorizedCmpId != null) {
				Company authorizedCompany = getCompanyService().retrieveCompany(
						authorizedCmpId);
				if (authorizedCompany != null) {

					companyExcludedFacilities = authorizedCompany
							.getFacilities();

					if (companyExcludedFacilities != null
							&& companyExcludedFacilities.length > 0) {
						FacilityList[] contactExcludedFacilities = getContactService()
								.retrieveExcludedFacilities(getContact());

						List<FacilityList> excludedFacilities = null;

						if (contactExcludedFacilities != null
								&& contactExcludedFacilities.length > 0) {
							excludedFacilities = Arrays
									.asList(contactExcludedFacilities);
						} else {
							return companyExcludedFacilities;
						}

						for (FacilityList facility : companyExcludedFacilities) {
							for (FacilityList authorizedFacility : excludedFacilities) {
								if (facility.getFacilityId().equals(
										authorizedFacility.getFacilityId())) {
									facility.setSelected(true);
								}
							}
						}

					}

				}

			}

		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}

		return companyExcludedFacilities;
	}

	public String selectAllFacilities() {
		if (companyExcludedFacilities != null) {
			for (FacilityList fl : companyExcludedFacilities) {
				fl.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneFacilities() {
		if (companyExcludedFacilities != null) {
			for (FacilityList fl : companyExcludedFacilities) {
				fl.setSelected(false);
			}
		}
		return null;
	}

	public int getContactRolesLength() {
		return null == contactRoles ? 0 : contactRoles.length;
	}

	public TableSorter getFacilityExclusionListWrapper() {
		return facilityExclusionListWrapper;
	}

	public void setFacilityExclusionListWrapper(
			TableSorter facilityExclusionListWrapper) {
		this.facilityExclusionListWrapper = facilityExclusionListWrapper;
	}

	public boolean isFacilityExclusionListEditable() {
		return facilityExclusionListEditable;
	}

	public void setFacilityExclusionListEditable(
			boolean facilityExclusionListEditable) {
		this.facilityExclusionListEditable = facilityExclusionListEditable;
	}

	protected final boolean validatePortalDetail(Contact contact) {
		boolean isValid = true;
		contact.clearValidationMessages();
		ValidationMessage[] validationMessages = null;

		try {
			validationMessages = getContactService().validateContactPortalDetail(
					contact);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing Contact failed for validation");
			isValid = false;
		}

		if (displayValidationMessages("contactExternalDetail:",
				validationMessages)) {
			isValid = false;
		}

		return isValid;
	}

	public String savePortalDetail() {
		boolean operationOk = true;
		try {
			Contact contact = getContact();

			operationOk = validatePortalDetail(contact);

			if (!operationOk) {
				return null;
			}

			if (testMode && (contactRoles == null || contactRoles.length == 0)
					&& (getContact().getExternalUser() != null)) {
				logger.debug("Setting up first time Portal Contact CROMERR companies for test mode...");
				setupTestExternalOrgs();
			}
			
			getContactService().savePortalDetail(contact, contactRoles);
			DisplayUtil.displayInfo("Company permissions saved successfully.");
		} catch (RemoteException re) {
			handleException(re);
		}
		return refreshPortalDetail();
	}

	public String editPortalDetail() {
		setEditable(true);
		setFacilityExclusionListEditable(false);

		return null;
	}

	public String cancelEditPortalDetail() {
		setEditable(false);
		return refreshPortalDetail();
	}

	public String editFacilityExclusionList() {
		setFacilityExclusionListEditable(true);
		setEditable(false);

		return null;
	}

	public String cancelEditFacilityExclusionList() {
		setFacilityExclusionListEditable(false);
		setEditable(false);

		return null;
	}

	public String saveFacilityExclusionList() {
		try {
			List<FacilityList> newExcludedFacilityList = new ArrayList<FacilityList>();
			List<FacilityList> newAuthorizedFacilityList = new ArrayList<FacilityList>();
			if (companyExcludedFacilities != null
					&& companyExcludedFacilities.length > 0) {
				for (FacilityList fl : companyExcludedFacilities) {
					if (fl.isSelected()) {
						newExcludedFacilityList.add(fl);
					} else {
						newAuthorizedFacilityList.add(fl);
					}
				}

				getContactService().modifyFacilityExclusionList(getContact(),
						newExcludedFacilityList.toArray(new FacilityList[0]),
						newAuthorizedFacilityList.toArray(new FacilityList[0]));
			} else {
				DisplayUtil
						.displayError("No facilities were available to be managed.");
				return null;
			}

			DisplayUtil
					.displayInfo("Facility exclusion list saved successfully.");
		} catch (RemoteException re) {
			handleException(re);
		}

		setFacilityExclusionListEditable(false);
		setEditable(false);

		refreshPortalDetail();
		FacesUtil.returnFromDialogAndRefresh();

		return null;
	}

	public String getAuthorizedCmpId() {
		return authorizedCmpId;
	}

	public void setAuthorizedCmpId(String authorizedCmpId) {
		this.authorizedCmpId = authorizedCmpId;
	}

	public TableSorter getCompanyFacilityExclusionListWrapper() {
		return companyFacilityExclusionListWrapper;
	}

	public void setCompanyFacilityExclusionListWrapper(
			TableSorter companyFacilityExclusionListWrapper) {
		this.companyFacilityExclusionListWrapper = companyFacilityExclusionListWrapper;
	}

	public String manageFacilityExclusionList() {
		setFacilityExclusionListEditable(false);
		setEditable(false);
		retrieveCompanyExcludedFacilities();
		companyFacilityExclusionListWrapper
				.setWrappedData(companyExcludedFacilities);

		return "dialog:viewExcludedFacilities";
	}

	public boolean isPortalDetailEditDisabled() {
		portalDetailEditDisabled = true;
		
		if(isPortalDetailEditable()){
			portalDetailEditDisabled = false;
		}
		
		return portalDetailEditDisabled;
	}

	public void setPortalDetailEditDisabled(boolean portalDetailEditDisabled) {
		this.portalDetailEditDisabled = portalDetailEditDisabled;
	}
	
	public String setupEmail() {

		this.mailSubject = null;
		this.mailMessage = null;
		return "dialog:setupEmail";
		
	}
	
	public String getMailSubject() {
		return mailSubject;
	}
	
	public final void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	
	public String getMailMessage() {
		return mailMessage;
	}
	
	public final void setMailMessage(String mailMessage) {
		this.mailMessage = mailMessage;
	}
	
	public void sendEmail() {
		
		String[] recipients = new String[1];

		try {

			SendMail mailer = new SendMail();
			mailer.setSubject(getMailSubject());
			mailer.setMessage(getMailMessage());
			recipients[0] = getContact().getEmailAddressTxt();
			mailer.setRecipients(recipients);
			mailer.send();
			
		} catch (Exception e) {
			logger.error("Exception caught while sending email: " + e.getMessage(), e);
		}
		
		CreateCorrespondence createCorr = (CreateCorrespondence) FacesUtil.getManagedBean("createCorrespondence");
		createCorr.reset();
		Correspondence corr = new Correspondence();
		corr.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
		corr.setCorrespondenceCategoryCd("3");
		corr.setDateGenerated(new Timestamp(System.currentTimeMillis()));
		corr.setToPerson(recipients[0]);
		UserAttributes uas = InfrastructureDefs.getCurrentUserAttrs();
		if (uas == null) {
			logger.error("UserAttributes is null.");
		}
		String fullName = uas.getUserFullName();
		if (fullName == null || fullName.length() == 0) {
			logger.error("Sender full name is null.");
			fullName = uas.getUserName();
		}
		corr.setFromPerson(fullName);
		corr.setRegarding(getMailSubject());
		corr.setAdditionalInfo(getMailMessage());
		createCorr.setCorrespondence(corr);

		setPopupRedirect("createCorrespondenceFromEmail");
		FacesUtil.returnFromDialogAndRefresh();
		return;

	}
	
	public final void cancelSendMail() {
		
		FacesUtil.returnFromDialogAndRefresh();
		
	}

}
