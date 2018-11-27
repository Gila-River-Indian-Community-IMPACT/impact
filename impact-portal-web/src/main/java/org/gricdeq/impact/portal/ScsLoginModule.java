package org.gricdeq.impact.portal;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.gricdeq.impact.ExternalRole;
import org.gricdeq.impact.RolePrincipal;
import org.gricdeq.impact.ScsExceptionHandler;
import org.gricdeq.impact.UserPrincipal;
import org.gricdeq.scs.ScsAuthClient;
import org.gricdeq.scs.ScsSharedPortalClient;
import org.gricdeq.scs.ScsUserManagementClient;
import org.gricdeq.scs.schema.auth._3.DomainTypeCode;
import org.gricdeq.scs.schema.auth._3.ValidateResponse;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.RetrieveRolesWithOrganizationsForDataflowAndPartnerResponse;
import org.gricdeq.scs.schema.sharedcromerr.portal._1.UserRoleWithOrganizationType;
import org.gricdeq.scs.schema.sharedcromerr.usermgmt._1.AuthenticateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.exchangenetwork.wsdl.sharedcromerr.portal._1.SharedPortalException;
import net.exchangenetwork.wsdl.sharedcromerr.usermgmt._1.SharedCromerrException;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactRole;
import us.oh.state.epa.stars2.framework.config.Config;
import us.wy.state.deq.impact.App;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.bo.ContactService;
import us.wy.state.deq.impact.database.dbObjects.company.Company;

public class ScsLoginModule implements LoginModule {

	private static final Logger LOG = LoggerFactory.getLogger(ScsUserManagementClient.class);

	private static final String SCS_TOKEN_KEY = String.valueOf(Math.random());
	private static final String SCS_ADMIN_ID = (String) Config.getEnvEntry("app/scsAdminId", "SCS_ADMIN_ID_NOT_FOUND");
	private static final String SCS_ADMIN_PWD = (String) Config.getEnvEntry("app/scsAdminPassword","SCS_ADMIN_PWD_NOT_FOUND");
	private static final String SCS_DATAFLOW = (String) Config.getEnvEntry("app/scsDataflow", "SCS_DATAFLOW_NOT_FOUND");
	private static final String SCS_PARTNER = (String) Config.getEnvEntry("app/scsPartner", "SCS_PARTNER_NOT_FOUND");
	private static final String SECURITY_ROLE = "IMPACT";

	private ScsUserManagementClient userManagementClient;
	private ScsAuthClient authClient;
	private ScsSharedPortalClient portalClient;

	private CallbackHandler handler;
	private Subject subject;
	private UserPrincipal userPrincipal;
	private RolePrincipal rolePrincipal;
	private Contact contact;
	private String login;
	private List<ContactRole> contactRoles = new ArrayList<ContactRole>();
	private ExternalRole initialRole;
	private CompanyService companyService;
	private ContactService contactService;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {

		handler = callbackHandler;
		this.subject = subject;
		this.userManagementClient = App.getApplicationContext().getBean(ScsUserManagementClient.class);
		this.authClient = App.getApplicationContext().getBean(ScsAuthClient.class);
		this.portalClient = App.getApplicationContext().getBean(ScsSharedPortalClient.class);
		this.companyService = App.getApplicationContext().getBean(CompanyService.class);
		this.contactService = App.getApplicationContext().getBean(ContactService.class);
	}

	@Override
	public boolean login() throws LoginException {

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("login");
		callbacks[1] = new PasswordCallback("password", true);

		try {
			handler.handle(callbacks);
			String name = ((NameCallback) callbacks[0]).getName();
			String password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());

			if (null != name && null != password) {
				
				try {
					AuthenticateResponse authenticateResponse = userManagementClient.authenticate(SCS_ADMIN_ID, SCS_ADMIN_PWD);
					String adminToken = authenticateResponse.getSecurityToken();
					
					Long userRoleId = null;
					if (name.startsWith(SCS_TOKEN_KEY)) { // user linked here
															// from
															// the ascs
															// dashboard
						String clientIp = decodeScsLoginName(name);
						String userToken = password;
						String resourceUri = "";
						DomainTypeCode domainTypeCode = DomainTypeCode.DEFAULT;

						LOG.debug("calling authClient.validate service ... ");
						ValidateResponse response = authClient.validate(SCS_ADMIN_ID, SCS_ADMIN_PWD, domainTypeCode,
								userToken, clientIp, resourceUri);

						String returnString = URLDecoder.decode(response.getReturn(), "UTF-8");

						LOG.debug("returnString = " + returnString);

						String[] returnElements = null != returnString? returnString.split("&") : new String[0];
						Map<String, String> returnMap = new HashMap<String, String>();
						for (String e : returnElements) {
							LOG.debug("e = " + e);
							returnMap.put(e.split("=")[0], e.split("=")[1]);
						}

						userRoleId = Long.valueOf(returnMap.get("UserRoleId"));
						login = returnMap.get("UserId");
					} else { // user submitted the login form
						LOG.debug("calling userManagementClient.authenticateUser service ... ");
						userManagementClient.authenticateUser(adminToken, name, password);
						login = name;
					}
					// look up contact by name
					Contact contact = getContactService().retrieveContactByExternalUsername(login);
					
					if (null != contact) {
						setContact(contact);
					} else {
						throw new LoginException("Login failed; Contact not found with username: " + login);
					}
					
					Company[] authorizedCompanies = getCompanyService().retrieveAuthorizedCompanies(login);

					RetrieveRolesWithOrganizationsForDataflowAndPartnerResponse response = 
							portalClient.retrieveRolesWithOrganizationsForDataflowAndPartner(adminToken, login, SCS_DATAFLOW, SCS_PARTNER);
					List<UserRoleWithOrganizationType> roles = response.getRoleWithOrganization();
					List<UserRoleWithOrganizationType> authorizedRoles = new ArrayList<UserRoleWithOrganizationType>();
					for (UserRoleWithOrganizationType role : roles) {
						if (checkImpactAuthorization(authorizedCompanies,role)) {
							if (null != userRoleId && userRoleId.equals(role.getUserRoleId())) {
								this.initialRole = new ExternalRole(role);
								LOG.debug("Adding authorized role: " + role.getRoleName());
								authorizedRoles.add(role);
							}
						}
					}
					this.contactRoles = transform(authorizedRoles);
					
				} catch (SharedCromerrException e) {
					ScsExceptionHandler.handleException(e);
					throw new LoginException(e.getMessage());
				} catch (SharedPortalException e) {
					ScsExceptionHandler.handleException(e);
					throw new LoginException(e.getMessage());
				}
				return true;
			}

			// If credentials are NOT OK we throw a LoginException
			throw new LoginException("Authentication failed");

		} catch (UnsupportedCallbackException | IOException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	private boolean checkImpactAuthorization(Company[] authorizedCompanies, 
			UserRoleWithOrganizationType role) {
		for (Company company : authorizedCompanies) {
			Long externalCompanyId = Long.valueOf(company.getExternalCompanyId());
			if (role.getOrganization().getOrganizationId().equals(externalCompanyId)) {
				LOG.debug("Confirmed authorization for external company: " + externalCompanyId);
				return true;
			}
		}
		return false;
	}

	private List<ContactRole> transform(List<UserRoleWithOrganizationType> roles) {
		List<ContactRole> contactRoles = new ArrayList<ContactRole>();
		for (UserRoleWithOrganizationType role : roles) {
			contactRoles.add(new ContactRole(new ExternalRole(role)));
		}
		return contactRoles;
	}

	@Override
	public boolean commit() throws LoginException {
		userPrincipal = new UserPrincipal(getContact());
		userPrincipal.getContact().setContactRoles(this.contactRoles);
		userPrincipal.setInitialRole(null == initialRole? null : new ContactRole(initialRole));
		subject.getPrincipals().add(userPrincipal);
		
		rolePrincipal = new RolePrincipal(SECURITY_ROLE);
		subject.getPrincipals().add(rolePrincipal);
		return true;
	}

	@Override
	public boolean abort() throws LoginException {
		return false;
	}

	@Override
	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(userPrincipal);
		subject.getPrincipals().remove(rolePrincipal);
		return true;
	}

	static String encodeScsLoginName(String scsData) {
		return SCS_TOKEN_KEY + ":" + scsData;
	}

	static String decodeScsLoginName(String name) {
		return name.substring(name.lastIndexOf(":") + 1);
	}

	public ScsUserManagementClient getScsClient() {
		return userManagementClient;
	}

	public void setScsClient(ScsUserManagementClient scsClient) {
		this.userManagementClient = scsClient;
	}

	public ScsAuthClient getAuthClient() {
		return authClient;
	}

	public void setAuthClient(ScsAuthClient authClient) {
		this.authClient = authClient;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

}
