package org.gricdeq.impact;

import org.gricdeq.scs.ScsConfigurationProvider;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.framework.config.Config;

@Component
public class ImpactScsConfigurationProvider implements ScsConfigurationProvider {

	@Override
	public String getSecondFactorAuthenticationServiceUri() {
		return (String)Config.getEnvEntry("app/scsSecondFactorAuthenticationServiceUri", "SCS_SECOND_FACTOR_AUTHENTICATION_SERVICE_URI_NOT_FOUND");
	}

	@Override
	public String getAuthServiceUri() {
		return (String)Config.getEnvEntry("app/scsAuthServiceUri", "SCS_AUTH_SERVICE_URI_NOT_FOUND");
	}

	@Override
	public String getSharedPortalServiceUri() {
		return (String)Config.getEnvEntry("app/scsSharedPortalServiceUri", "SCS_SHARED_PORTAL_SERVICE_URI_NOT_FOUND");
	}

	@Override
	public String getSignatureAndCorServiceUri() {
		return (String)Config.getEnvEntry("app/scsSignatureAndCorServiceUri", "SCS_SIGNATURE_AND_COR_SERVICE_URI_NOT_FOUND");
	}

	@Override
	public String getUserManagementServiceUri() {
		return (String)Config.getEnvEntry("app/scsUserManagementServiceUri", "SCS_USER_MANAGEMENT_SERVICE_URI_NOT_FOUND");
	}

}
