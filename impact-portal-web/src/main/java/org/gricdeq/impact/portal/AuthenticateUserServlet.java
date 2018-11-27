package org.gricdeq.impact.portal;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.gricdeq.scs.ScsUserManagementClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This servlet is called when a user links over from the Advanced SCS dashboard.  Upon clicking
 * a hyperlink in the dashboard program services list, the user's browser performs a POST request
 * on this servlet.  The POST request contains a token, which is then stored in a cookie by this
 * servlet.  This servlet then redirects the user to the loginInit.jsf page.  The loginInit.jsf
 * page establishes an ADF session for the user and redirects back to this servlet.  This servlet
 * then retrieves the SCS token from the cookie and passes it to the ScsLoginModule via
 * HttpServletRequest.login().  Upon successful validation of the token, the user is authenticated
 * and redirected to the facilitySelector.jsf page.
 */
public class AuthenticateUserServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticateUserServlet.class);

	private static final String SCS_TOKEN = "scs-token";
	private static final String SCS_DATA = "scs-data";
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AuthenticateUserServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String scsData = null;
		String token = null;
		

		for (Cookie cookie : request.getCookies()) {
			if (SCS_DATA.equals(cookie.getName())) {
				if (null != cookie.getValue()) {
					scsData = new String(Base64.decodeBase64(cookie.getValue()), Charsets.UTF_8);
				}
			}
			if (SCS_TOKEN.equals(cookie.getName())) {
				if (null != cookie.getValue()) {
					token = new String(Base64.decodeBase64(cookie.getValue().getBytes()), Charsets.UTF_8);
				}
			}
		}

		if (StringUtils.isBlank(scsData) || StringUtils.isBlank(token)) {
			LOG.debug("No scsData and/or token; request is unauthorized");
			response.sendRedirect(request.getContextPath() + "/loginFailure.jsp");
		} else {
			try {
				if (!request.isUserInRole("IMPACT")) {
					String user = ScsLoginModule.encodeScsLoginName(scsData);
					request.login(user, token);
					LOG.debug("Request authorized for user: " + user);
				}
				response.sendRedirect(request.getContextPath() + "/home.jsf");
			} catch (ServletException e) {
				LOG.warn("Login failed; request not authorized");
				response.sendRedirect(request.getContextPath() + "/loginFailure.jsp");
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String[]> parameterMap = request.getParameterMap();
		String[] scsDataParam = parameterMap.get("SCS_DATA");

		String scsData = null;
		if (null != scsDataParam && scsDataParam.length > 0) {
			scsData = scsDataParam[0];
		}

		String[] tokenParam = parameterMap.get("token");
		String token = null;
		if (null != tokenParam && tokenParam.length > 0) {
			token = tokenParam[0];
		}

		if (StringUtils.isBlank(scsData) || StringUtils.isBlank(token)) {
			LOG.debug("No scsData and/or token; request is unauthorized");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			addCookie(SCS_DATA, scsData, response);
			addCookie(SCS_TOKEN, token, response);
	
			// TODO consider setting samesite policy (using filter?)
	
			LOG.debug("Cookies set (scsData and token); redirecting to /loginInit.jsf");
			response.sendRedirect(request.getContextPath() + "/loginInit.jsf");
		}
	}

	private void addCookie(String name, String value, HttpServletResponse response) {
		Cookie cookie = new Cookie(name, Base64.encodeBase64String(value.getBytes(Charsets.UTF_8)));
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		// cookie.setMaxAge(60); //TODO consider
		// cookie.setDomain(?); //TODO consider
		// cookie.setPath(?); //TODO consider
		response.addCookie(cookie);
	}

}
