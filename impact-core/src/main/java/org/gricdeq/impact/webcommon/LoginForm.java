package org.gricdeq.impact.webcommon;

import java.io.IOException;

import javax.faces.context.FacesContext;

import us.oh.state.epa.stars2.framework.config.Config;

public class LoginForm {

	private static final String DEFAULT_EPA_PORTAL = (String) Config.getEnvEntry("app/defaultEPAPortal", "DEFAULT_EPA_PORTAL_NOT_FOUND");

	public LoginForm() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(DEFAULT_EPA_PORTAL);
		} catch (IOException e) {
			throw new RuntimeException("Error occurred while redirecting to login page.");
		}
	}

	public String getScsLoginUrl() {
		return DEFAULT_EPA_PORTAL;
	}
}
