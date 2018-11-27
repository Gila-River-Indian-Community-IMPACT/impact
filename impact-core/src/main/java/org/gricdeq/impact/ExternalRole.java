package org.gricdeq.impact;

import org.gricdeq.scs.schema.sharedcromerr.portal._1.UserRoleWithOrganizationType;

import us.oh.state.epa.stars2.framework.config.Config;
import us.wy.state.deq.impact.app.contact.ExternalOrganization;

public class ExternalRole {
	
	public static final String CERTIFIER_ROLE = (String) Config.getEnvEntry("app/certifierRole", "CERTIFIER_ROLE_NOT_FOUND");
	public static final String PREPARER_ROLE = (String) Config.getEnvEntry("app/preparerRole", "PREPARER_ROLE_NOT_FOUND");
	
	private UserRoleWithOrganizationType userRoleWithOrganizationType;
	
	public ExternalRole() {
		super();
	}

	public ExternalRole(UserRoleWithOrganizationType userRoleWithOrganizationType) {
		super();
		this.userRoleWithOrganizationType = userRoleWithOrganizationType;
	}

	public Long getUserRoleId() {
		return userRoleWithOrganizationType.getUserRoleId();
	}

	public void setUserRoleId(Long value) {
		userRoleWithOrganizationType.setUserRoleId(value);
	}

	public String getUserRoleStatus() {
		return userRoleWithOrganizationType.getUserRoleStatus();
	}

	public void setUserRoleStatus(String value) {
		userRoleWithOrganizationType.setUserRoleStatus(value);
	}

	public String getUserRoleSubject() {
		return userRoleWithOrganizationType.getUserRoleSubject();
	}

	public void setUserRoleSubject(String value) {
		userRoleWithOrganizationType.setUserRoleSubject(value);
	}

	public String getDataflow() {
		return userRoleWithOrganizationType.getDataflow();
	}

	public void setDataflow(String value) {
		userRoleWithOrganizationType.setDataflow(value);
	}

	public Long getRoleId() {
		return userRoleWithOrganizationType.getRoleId();
	}

	public void setRoleId(Long value) {
		userRoleWithOrganizationType.setRoleId(value);
	}

	public String getRoleName() {
		return userRoleWithOrganizationType.getRoleName();
	}

	public void setRoleName(String value) {
		userRoleWithOrganizationType.setRoleName(value);
	}

	public String getPartnerId() {
		return userRoleWithOrganizationType.getPartnerId();
	}

	public void setPartnerId(String value) {
		userRoleWithOrganizationType.setPartnerId(value);
	}

	public ExternalOrganization getOrganization() {
		return new ExternalOrganization(userRoleWithOrganizationType.getOrganization());
	}

	public void setOrganization(ExternalOrganization value) {
		userRoleWithOrganizationType.setOrganization(value.getUserOrganizationType());
	}

	public UserRoleWithOrganizationType getUserRoleWithOrganizationType() {
		return userRoleWithOrganizationType;
	}

	public void setUserRoleWithOrganizationType(UserRoleWithOrganizationType userRoleWithOrganizationType) {
		this.userRoleWithOrganizationType = userRoleWithOrganizationType;
	}
	
}
