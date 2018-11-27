package org.gricdeq.scs;

public interface ScsConfigurationProvider {

	String getSecondFactorAuthenticationServiceUri();

	String getAuthServiceUri();

	String getSharedPortalServiceUri();

	String getSignatureAndCorServiceUri();

	String getUserManagementServiceUri();
	

}
